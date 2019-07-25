/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.journeyman88.gateway.fakesms;

import com.github.journeyman88.gateway.GatewayProvider;
import java.io.IOException;
import okhttp3.Credentials;
import org.jboss.logging.Logger;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 *
 * @author m.bignami
 */
public class FakeSmsProvider implements GatewayProvider {
    
    private static final Logger LOGGER = Logger.getLogger(FakeSmsProvider.class);
    private static final String PROVIDER_ID = "FakeSms";
    
    public FakeSmsProvider()
    {
    }
    
    @Override
    public boolean sendSms(String username, String password, String from, String to, String message) {
        
        LOGGER.infof("Username: '%s', Password : '%s', From : '%s', To : '%s', Text : '%s'", username, password, from, to, message);
        return true;
    }

    @Override
    public String getProviderId() {
        return PROVIDER_ID;
    }
    
}
