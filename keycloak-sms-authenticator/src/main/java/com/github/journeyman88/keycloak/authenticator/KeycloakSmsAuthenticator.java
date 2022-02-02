package com.github.journeyman88.keycloak.authenticator;

import com.github.journeyman88.gateway.GatewayLoader;
import com.github.journeyman88.gateway.GatewayProvider;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.client.CredentialsProvider;
//import org.apache.http.impl.client.BasicCredentialsProvider;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * Created by joris on 11/11/2016.
 */
public class KeycloakSmsAuthenticator implements Authenticator {

    private static final Logger LOGGER = Logger.getLogger(KeycloakSmsAuthenticator.class);

    public static final String CREDENTIAL_TYPE = "sms_validation";

    private static enum CODE_STATUS {
        VALID,
        INVALID,
        EXPIRED
    }
    
    private boolean checkOnce(AuthenticationFlowContext context)
    {
        boolean retVal = false;
        if (KeycloakSmsAuthenticatorUtil.getConfigBoolean(context.getAuthenticatorConfig(), KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_ONCE, false))
        {
            LOGGER.info("Verify once is active.");
            String once = KeycloakSmsAuthenticatorUtil.getAttributeValue(context.getUser(), KeycloakSmsAuthenticatorContstants.USER_ATTRIBUTE_VALIDATION);
            retVal = ((once != null) && (once.equalsIgnoreCase("done")));
        }
        return retVal;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        LOGGER.debug("authenticate called ... context = " + context);
        
        AuthenticatorConfigModel config = context.getAuthenticatorConfig();
        
        if (checkOnce(context))
        {
            LOGGER.info("User already verified.");
            context.success();
            return;
        }
        
        String mobileNumberAttribute = KeycloakSmsAuthenticatorUtil.getConfigString(config, KeycloakSmsAuthenticatorContstants.CONF_PRP_USR_ATTR_MOBILE);
        if(mobileNumberAttribute == null) {
            LOGGER.error("Mobile number attribute is not configured for the SMS Authenticator.");
            Response challenge =  context.form()
                    .setError("Mobile number can not be determined.")
                    .createForm("sms-validation-error.ftl");
            context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR, challenge);
            return;
        }

        String mobileNumber = KeycloakSmsAuthenticatorUtil.getAttributeValue(context.getUser(), mobileNumberAttribute);
        if(mobileNumber != null) {
            // The mobile number is configured --> send an SMS


            long nrOfDigits = KeycloakSmsAuthenticatorUtil.getConfigLong(config, KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_CODE_LENGTH, 8L);
            LOGGER.debug("Using nrOfDigits " + nrOfDigits);


            long ttl = KeycloakSmsAuthenticatorUtil.getConfigLong(config, KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_CODE_TTL, 10 * 60L); // 10 minutes in s

            LOGGER.debug("Using ttl " + ttl + " (s)");

            String code = getSmsCode(nrOfDigits);

            storeSMSCode(context, code, new Date().getTime() + (ttl * 1000)); // s --> ms
            if (sendSmsCode(mobileNumber, code, context.getAuthenticatorConfig())) {
                Response challenge = context.form().createForm("sms-validation.ftl");
                context.challenge(challenge);
            } else {
                Response challenge =  context.form()
                        .setError("SMS could not be sent.")
                        .createForm("sms-validation-error.ftl");
                context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR, challenge);
            }
        } else {
            // The mobile number is NOT configured --> complain
            Response challenge =  context.form()
                    .setError("Missing mobile number")
                    .createForm("sms-validation-error.ftl");
            context.failureChallenge(AuthenticationFlowError.CLIENT_CREDENTIALS_SETUP_REQUIRED, challenge);
        }
    }


    @Override
    public void action(AuthenticationFlowContext context) {
        
        LOGGER.debug("action called ... context = " + context);
        
        
        if (checkOnce(context))
        {
            LOGGER.info("User already verified.");
            context.success();
            return;
        }
        CODE_STATUS status = validateCode(context);
        Response challenge = null;
        switch (status) {
            case EXPIRED:
                challenge =  context.form()
                        .setError("code is expired")
                        .createForm("sms-validation.ftl");
                context.failureChallenge(AuthenticationFlowError.EXPIRED_CODE, challenge);
                break;

            case INVALID:
                switch(context.getExecution().getRequirement())
                {
                    case CONDITIONAL:
                    case ALTERNATIVE:
                        LOGGER.debug("Calling context.attempted()");
                        context.attempted();
                        break;
                    case REQUIRED:
                        challenge =  context.form().setError("badCode").createForm("sms-validation.ftl");
                        context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
                        break;
                    default:
                        // Something strange happened
                        LOGGER.warn("Undefined execution ...");
                        break;
                }
                break;


            case VALID:
                if (KeycloakSmsAuthenticatorUtil.getConfigBoolean(context.getAuthenticatorConfig(), KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_ONCE))
                {
                    context.getUser().setAttribute(KeycloakSmsAuthenticatorContstants.USER_ATTRIBUTE_VALIDATION, Arrays.asList(new String [] {"done"}));
                }
                context.success();
                break;

        }
    }

    // Store the code + expiration time in a UserCredential. Keycloak will persist these in the DB.
    // When the code is validated on another node (in a clustered environment) the other nodes have access to it's values too.
    private void storeSMSCode(AuthenticationFlowContext context, String code, Long expiringAt) {
        UserCredentialModel credentials = new UserCredentialModel();
        credentials.setType(KeycloakSmsAuthenticatorContstants.USR_CRED_MDL_SMS_CODE);
        credentials.setValue(code);
        context.getSession().userCredentialManager().updateCredential(context.getRealm(), context.getUser(), credentials);
        context.getUser().setAttribute(KeycloakSmsAuthenticatorContstants.USR_CRED_MDL_SMS_CODE, Arrays.asList(new String[] {code}));

        credentials = new UserCredentialModel();
        credentials.setType(KeycloakSmsAuthenticatorContstants.USR_CRED_MDL_SMS_EXP_TIME);
        credentials.setValue((expiringAt).toString());
        context.getSession().userCredentialManager().updateCredential(context.getRealm(), context.getUser(), credentials);
        context.getUser().setAttribute(KeycloakSmsAuthenticatorContstants.USR_CRED_MDL_SMS_EXP_TIME, Arrays.asList(new String[] {(expiringAt).toString()}));
    }


    protected CODE_STATUS validateCode(AuthenticationFlowContext context) {
        CODE_STATUS result = CODE_STATUS.INVALID;

        LOGGER.debug("validateCode called ... ");
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String enteredCode = formData.getFirst(KeycloakSmsAuthenticatorContstants.ANSW_SMS_CODE);

        
//        String expectedCode = KeycloakSmsAuthenticatorUtil.getCredentialValue(context.getSession().userCredentialManager(), context.getRealm(), context.getUser(), KeycloakSmsAuthenticatorContstants.USR_CRED_MDL_SMS_CODE);
//        String expTimeString = KeycloakSmsAuthenticatorUtil.getCredentialValue(context.getSession().userCredentialManager(), context.getRealm(), context.getUser(), KeycloakSmsAuthenticatorContstants.USR_CRED_MDL_SMS_EXP_TIME);

        String expectedCode = KeycloakSmsAuthenticatorUtil.getAttributeValue(context.getUser(), KeycloakSmsAuthenticatorContstants.USR_CRED_MDL_SMS_CODE);
        String expTimeString = KeycloakSmsAuthenticatorUtil.getAttributeValue(context.getUser(), KeycloakSmsAuthenticatorContstants.USR_CRED_MDL_SMS_EXP_TIME);

        LOGGER.info("Expected code = " + expectedCode + "    entered code = " + enteredCode);

        if(expectedCode != null) {
            result = enteredCode.equals(expectedCode) ? CODE_STATUS.VALID : CODE_STATUS.INVALID;
            long now = new Date().getTime();

            LOGGER.debug("Valid code expires in " + (Long.parseLong(expTimeString) - now) + " ms");
            if(result == CODE_STATUS.VALID) {
                if (Long.parseLong(expTimeString) < now) {
                    LOGGER.debug("Code is expired !!");
                    result = CODE_STATUS.EXPIRED;
                }
                context.getUser().removeAttribute(KeycloakSmsAuthenticatorContstants.USR_CRED_MDL_SMS_CODE);
                context.getUser().removeAttribute(KeycloakSmsAuthenticatorContstants.USR_CRED_MDL_SMS_EXP_TIME);
            }
        }
        LOGGER.debug("result : " + result);
        return result;
    }

    @Override
    public boolean requiresUser() {
        LOGGER.debug("requiresUser called ... returning true");
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        LOGGER.debug("configuredFor called ... session=" + session + ", realm=" + realm + ", user=" + user);
        boolean result = true;
        LOGGER.debug("... returning "  +result);
        return result;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        LOGGER.debug("setRequiredActions called ... session=" + session + ", realm=" + realm + ", user=" + user);
    }

    @Override
    public void close() {
        LOGGER.debug("close called ...");
    }


    private String getSmsCode(long nrOfDigits) {
        if(nrOfDigits < 1) {
            throw new RuntimeException("Nr of digits must be bigger than 0");
        }

        double maxValue = Math.pow(10.0, nrOfDigits); // 10 ^ nrOfDigits;
        Random r = new Random();
        long code = (long)(r.nextFloat() * maxValue);
        return Long.toString(code);
    }

    private boolean sendSmsCode(String mobileNumber, String code, AuthenticatorConfigModel config) {
        // Send an SMS
        LOGGER.debug("Sending " + code + "  to mobileNumber " + mobileNumber);
        
        GatewayLoader loader = new GatewayLoader();

        String smsSrv = KeycloakSmsAuthenticatorUtil.getConfigString(config, KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_PROVIDER);
        String smsUsr = KeycloakSmsAuthenticatorUtil.getConfigString(config, KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_USERNAME);
        String smsPwd = KeycloakSmsAuthenticatorUtil.getConfigString(config, KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_PASSWORD);
        String smsFrom = KeycloakSmsAuthenticatorUtil.getConfigString(config, KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_SENDER);
        String smsText = createMessage(code, mobileNumber, config);
        
        GatewayProvider provider = loader.getProvider(smsSrv);
        return provider.sendSms(smsUsr, smsPwd, smsFrom, mobileNumber, smsText);
        
    }


    private String getPath(String mobileNumber, URL smsURL, String smsText) throws UnsupportedEncodingException {
        String path = smsURL.getPath();
        if(smsURL.getQuery() != null && smsURL.getQuery().length() > 0) {
            path += smsURL.getQuery();
        }
        path = path.replaceFirst("\\{message\\}", URLEncoder.encode(smsText, "UTF-8"));
        path = path.replaceFirst("\\{phonenumber\\}", URLEncoder.encode(mobileNumber, "UTF-8"));
        return path;
    }

    private String createMessage(String code, String mobileNumber, AuthenticatorConfigModel config) {
        String text = KeycloakSmsAuthenticatorUtil.getConfigString(config, KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_TEXT);
        text = text.replaceAll("%sms-code%", code);
        text = text.replaceAll("%phonenumber%", mobileNumber);

        return text;
    }

    private boolean isNotEmpty(String s) {
        return (s != null && s.length() > 0);
    }

}
