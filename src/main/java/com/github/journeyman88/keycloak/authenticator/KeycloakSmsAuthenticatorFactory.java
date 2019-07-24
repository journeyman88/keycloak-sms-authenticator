package com.github.journeyman88.keycloak.authenticator;

import com.github.journeyman88.gateway.GatewayLoader;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.ConfigurableAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import javax.ws.rs.HttpMethod;
import java.util.ArrayList;
import java.util.List;

import static com.github.journeyman88.keycloak.authenticator.KeycloakSmsAuthenticatorContstants.AUTH_METHOD_BASIC;
import static com.github.journeyman88.keycloak.authenticator.KeycloakSmsAuthenticatorContstants.AUTH_METHOD_INMESSAGE;


/**
 * Created by joris on 11/11/2016.
 */
public class KeycloakSmsAuthenticatorFactory implements AuthenticatorFactory, ConfigurableAuthenticatorFactory {

    public static final String PROVIDER_ID = "sms-authentication";

    private static final Logger LOGGER = Logger.getLogger(KeycloakSmsAuthenticatorFactory.class);
    private static final KeycloakSmsAuthenticator SINGLETON = new KeycloakSmsAuthenticator();


    public static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.OPTIONAL,
            AuthenticationExecutionModel.Requirement.DISABLED};

    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<ProviderConfigProperty>();

    static {
        ProviderConfigProperty property;

        // Mobile number attribute
        property = new ProviderConfigProperty();
        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_USR_ATTR_MOBILE);
        property.setLabel("Mobile number attribute");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("The attribute in which the mobile number of a user is stored.");
        CONFIG_PROPERTIES.add(property);

        // SMS Code
        property = new ProviderConfigProperty();
        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_CODE_TTL);
        property.setLabel("SMS code time to live");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("The validity of the sent code in seconds.");
        CONFIG_PROPERTIES.add(property);

        property = new ProviderConfigProperty();
        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_CODE_LENGTH);
        property.setLabel("Length of the SMS code");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("Length of the SMS code.");
        CONFIG_PROPERTIES.add(property);

        // SMS Text
        property = new ProviderConfigProperty();
        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_TEXT);
        property.setLabel("Template of text to send to the user");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("Use %sms-code% as placeholder for the generated SMS code. Use %user% and %password% as placeholder when 'In message' authentication is used.");
        CONFIG_PROPERTIES.add(property);

        // SMS GatewayProvider
        property = new ProviderConfigProperty();
        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_PROVIDER);
        property.setLabel("Provider");
        property.setHelpText("");
        GatewayLoader gl = new GatewayLoader();
        property.setType(ProviderConfigProperty.LIST_TYPE);
        property.setOptions(gl.getProviderIds());
        CONFIG_PROPERTIES.add(property);
        property = new ProviderConfigProperty();
        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_SENDER);
        property.setLabel("Sender of the message");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("Can be text or a mobile number.");
        CONFIG_PROPERTIES.add(property);
        
        // SMS Gateway
//        property = new ProviderConfigProperty();
//        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_METHOD);
//        property.setLabel("HTTP method");
//        property.setHelpText("");
//        List<String> methods = new ArrayList(2);
//        methods.add(HttpMethod.GET);
//        methods.add(HttpMethod.POST);
//        property.setType(ProviderConfigProperty.LIST_TYPE);
//        property.setOptions(methods);
//        property.setDefaultValue(HttpMethod.POST);
//        CONFIG_PROPERTIES.add(property);
//
//        property = new ProviderConfigProperty();
//        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_URL);
//        property.setLabel("URL of SMS gateway");
//        property.setType(ProviderConfigProperty.STRING_TYPE);
//        property.setHelpText("Use {message} as a placeholder for the message and {phonenumber} as a placeholder for the mobile number when the SMS text is to be passed as a URL parameter.");
//        CONFIG_PROPERTIES.add(property);
//
//        property = new ProviderConfigProperty();
//        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_CONTENT_TYPE);
//        property.setLabel("Content type");
//        property.setHelpText("");
//        List<String> types = new ArrayList(2);
//        types.add("application/json");
//        types.add("application/xml");
//        property.setType(ProviderConfigProperty.LIST_TYPE);
//        property.setOptions(types);
//        property.setDefaultValue("application/json");
//        CONFIG_PROPERTIES.add(property);
//
//        // SMS Authentication
//        property = new ProviderConfigProperty();
//        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_AUTHTYPE);
//        property.setLabel("Authentication method");
//        property.setHelpText("");
//        types = new ArrayList(2);
//        types.add(AUTH_METHOD_BASIC);
//        types.add(AUTH_METHOD_INMESSAGE);
//        property.setType(ProviderConfigProperty.LIST_TYPE);
//        property.setOptions(types);
//        property.setDefaultValue(AUTH_METHOD_BASIC);
//        CONFIG_PROPERTIES.add(property);

        property = new ProviderConfigProperty();
        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_USERNAME);
        property.setLabel("Username to authenticate towards the SMS Gateway");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("");
        CONFIG_PROPERTIES.add(property);

        property = new ProviderConfigProperty();
        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_PASSWORD);
        property.setLabel("Password to authenticate towards the SMS Gateway");
        property.setType(ProviderConfigProperty.PASSWORD);
        property.setHelpText("");
        CONFIG_PROPERTIES.add(property);

        property = new ProviderConfigProperty();
        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_SMS_ONCE);
        property.setLabel("Validate Once?");
        property.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        property.setDefaultValue(Boolean.FALSE);
        property.setHelpText("Set this to validate only once.");
        CONFIG_PROPERTIES.add(property);




//        // HTTP Proxy
//        property = new ProviderConfigProperty();
//        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_PROXY_URL);
//        property.setLabel("URL of HTTP proxy to use when calling the SMS gateway");
//        property.setType(ProviderConfigProperty.STRING_TYPE);
//        property.setHelpText("Emtpy when no proxy is needed");
//        CONFIG_PROPERTIES.add(property);
//
//        property = new ProviderConfigProperty();
//        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_PROXY_USERNAME);
//        property.setLabel("Username to authenticate towards the HTTP proxy");
//        property.setType(ProviderConfigProperty.STRING_TYPE);
//        property.setHelpText("");
//        CONFIG_PROPERTIES.add(property);
//
//        property = new ProviderConfigProperty();
//        property.setName(KeycloakSmsAuthenticatorContstants.CONF_PRP_PROXY_PASSWORD);
//        property.setLabel("Password to authenticate towards the HTTP proxy");
//        property.setType(ProviderConfigProperty.PASSWORD);
//        property.setHelpText("");
//        CONFIG_PROPERTIES.add(property);

    }

    @Override
    public String getId() {
        LOGGER.debug("getId called ... returning " + PROVIDER_ID);
        return PROVIDER_ID;
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        LOGGER.debug("create called ... returning " + SINGLETON);
        return SINGLETON;
    }


    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        LOGGER.debug("getRequirementChoices called ... returning " + REQUIREMENT_CHOICES);
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        LOGGER.debug("isUserSetupAllowed called ... returning true");
        return true;
    }

    @Override
    public boolean isConfigurable() {
        boolean result = true;
        LOGGER.debug("isConfigurable called ... returning " + result);
        return result;
    }

    @Override
    public String getHelpText() {
        LOGGER.debug("getHelpText called ...");
        return "Validates an OTP sent by SMS.";
    }

    @Override
    public String getDisplayType() {
        String result = "SMS Authentication";
        LOGGER.debug("getDisplayType called ... returning " + result);
        return result;
    }

    @Override
    public String getReferenceCategory() {
        LOGGER.debug("getReferenceCategory called ... returning sms-auth-code");
        return "sms-auth-code";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        LOGGER.debug("getConfigProperties called ... returning " + CONFIG_PROPERTIES);
        return CONFIG_PROPERTIES;
    }

    @Override
    public void init(Config.Scope config) {
        LOGGER.debug("init called ... config.scope = " + config);
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        LOGGER.debug("postInit called ... factory = " + factory);
    }

    @Override
    public void close() {
        LOGGER.debug("close called ...");
    }
}
