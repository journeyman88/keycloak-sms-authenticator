/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.journeyman88.gateway.comilio;

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
public class ComilioProvider implements GatewayProvider {
    
    private static final Logger LOGGER = Logger.getLogger(ComilioProvider.class);
    private static final String PROVIDER_ID = "Comilio";
    
    private final ComilioApi service;
    
    public ComilioProvider()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.comilio.it/rest/v1/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        service = retrofit.create(ComilioApi.class);
    }
    
    @Override
    public boolean sendSms(String username, String password, String from, String to, String message) {
        boolean result = false;
        try {
            String auth = Credentials.basic(username, password);
            ComilioMessage sms = new ComilioMessage(from, to, message);
            Call<ComilioResponse> call = service.sendSms(auth, sms);
            Response<ComilioResponse> wrap = call.execute();
            if (wrap.isSuccessful())
            {
                LOGGER.info("Chiamata riuscita");
                result = wrap.body().getMessage_id() != null;
            }
            else
            {
                LOGGER.error("Chiamata fallita");
                LOGGER.error(wrap.errorBody().string());
            }
        } catch (IOException ex) {
            LOGGER.error(null, ex);
        }
        return result;
    }

    @Override
    public String getProviderId() {
        return PROVIDER_ID;
    }
    
}
