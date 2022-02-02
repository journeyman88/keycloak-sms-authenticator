/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.journeyman88.gateway.gatewayapi;

import com.github.journeyman88.gateway.GatewayProvider;
import java.io.IOException;
import okhttp3.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 *
 * @author m.bignami
 */
public class GatewayApiProvider implements GatewayProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayApiProvider.class);
    private static final String PROVIDER_ID = "GatewayAPI";
    
    private final GatewayApi service;
    
    public GatewayApiProvider()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gatewayapi.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        service = retrofit.create(GatewayApi.class);
    }
    
    @Override
    public boolean sendSms(String username, String password, String from, String to, String message) {
        boolean result = false;
        try {
            String auth = Credentials.basic(username, password);
            GatewayApiMessage sms = new GatewayApiMessage(from, to, message);
            Call<GatewayApiResponse> call = service.sendSms(auth, sms);
            Response<GatewayApiResponse> wrap = call.execute();
            if (wrap.isSuccessful())
            {
                LOGGER.debug("Chiamata riuscita");
                result = !wrap.body().getIds().isEmpty();
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
