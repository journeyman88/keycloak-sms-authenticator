/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.journeyman88.gateway.fakesms;

import com.github.journeyman88.gateway.GatewayProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author m.bignami
 */
public class FakeSmsProvider implements GatewayProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FakeSmsProvider.class);
    private static final String PROVIDER_ID = "FakeSms";
    
    public FakeSmsProvider()
    {
    }
    
    @Override
    public boolean sendSms(String username, String password, String from, String to, String message) {
        
        LOGGER.info("Username: '{}', Password : '{}', From : '{}', To : '{}', Text : '{}'", username, password, from, to, message);
        return true;
    }

    @Override
    public String getProviderId() {
        return PROVIDER_ID;
    }
    
}
