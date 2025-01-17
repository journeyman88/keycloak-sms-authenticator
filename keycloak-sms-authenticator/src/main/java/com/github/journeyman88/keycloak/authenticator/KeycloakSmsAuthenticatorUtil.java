package com.github.journeyman88.keycloak.authenticator;

import java.util.List;
import java.util.Optional;
import org.jboss.logging.Logger;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserCredentialManager;
import org.keycloak.models.UserModel;

import java.util.stream.Stream;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.RealmModel;

/**
 * Created by joris on 18/11/2016.
 */
public class KeycloakSmsAuthenticatorUtil {

    private static Logger logger = Logger.getLogger(KeycloakSmsAuthenticatorUtil.class);

    public static Optional<String> getAttributeValue(UserModel user, String attributeName) {
        Stream<String> values = user.getAttributeStream(attributeName);
        return values.findFirst();
    }


    public static String getCredentialValue(UserCredentialManager credMan, RealmModel realm, UserModel user, String credentialName) {
        String result = null;
        List<CredentialModel> creds = credMan.getStoredCredentials(realm, user);
        for (CredentialModel cred : creds) {
            logger.info(cred.getValue());
            logger.info(cred.getType());
            if(cred.getType().equals(credentialName)) {
                result = cred.getValue();
            }
        }
        return result;
    }

    public static Boolean getConfigBoolean(AuthenticatorConfigModel config, String configName) {
        return getConfigBoolean(config, configName, null);
    }

    public static Boolean getConfigBoolean(AuthenticatorConfigModel config, String configName, Boolean defaultValue) {

        Boolean value = defaultValue;

        if (config.getConfig() != null) {
            // Get value
            value = config.getConfig().get(configName).equalsIgnoreCase("true");
        }

        return value;
    }

    public static String getConfigString(AuthenticatorConfigModel config, String configName) {
        return getConfigString(config, configName, null);
    }

    public static String getConfigString(AuthenticatorConfigModel config, String configName, String defaultValue) {

        String value = defaultValue;

        if (config.getConfig() != null) {
            // Get value
            value = config.getConfig().get(configName);
        }

        return value;
    }

    public static Long getConfigLong(AuthenticatorConfigModel config, String configName) {
        return getConfigLong(config, configName, null);
    }

    public static Long getConfigLong(AuthenticatorConfigModel config, String configName, Long defaultValue) {

        Long value = defaultValue;

        if (config.getConfig() != null) {
            // Get value
            Object obj = config.getConfig().get(configName);
            try {
                value = Long.valueOf((String) obj); // s --> ms
            } catch (NumberFormatException nfe) {
                logger.error("Can not convert " + obj + " to a number.");
            }
        }

        return value;
    }
}
