/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.journeyman88.gateway.smshosting;

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
public class SmsHostingProvider implements GatewayProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsHostingProvider.class);
    private static final String PROVIDER_ID = "SmsHosting";
    
    private final SmsHostingApi service;
    
    public SmsHostingProvider()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.smshosting.it/rest/api/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        service = retrofit.create(SmsHostingApi.class);
    }
    
    @Override
    public boolean sendSms(String username, String password, String from, String to, String message) {
        boolean result = false;
        try {
            String auth = Credentials.basic(username, password);
            Call<SmsHostingResponse> call = service.sendSms(auth, from, to, message);
            Response<SmsHostingResponse> wrap = call.execute();
            if (wrap.isSuccessful())
            {
                LOGGER.debug("{}", wrap.body());
                result = wrap.body().getSmsInserted() > 0;
                LOGGER.debug("Chiamata riuscita");
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
