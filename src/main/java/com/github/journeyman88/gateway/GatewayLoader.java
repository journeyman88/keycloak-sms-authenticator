/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.journeyman88.gateway;

import com.github.journeyman88.gateway.fakesms.FakeSmsProvider;
import com.github.journeyman88.gateway.smshosting.SmsHostingProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import org.jboss.logging.Logger;

/**
 *
 * @author m.bignami
 */
public class GatewayLoader {
    
    private static final Logger LOGGER = Logger.getLogger(GatewayLoader.class);
    private static final Set<GatewayProvider> PROVIDERS;
    
    static {
        PROVIDERS = new HashSet<>();
        PROVIDERS.add(new FakeSmsProvider());
        PROVIDERS.add(new SmsHostingProvider());
    }
    
    public List<String> getProviderIds() {
        LOGGER.debug("getProviderIds");
        List<String> retVal = new ArrayList<>();
        Iterator<GatewayProvider> iterator = PROVIDERS.iterator();
        while (iterator.hasNext())
        {
            GatewayProvider tmp = iterator.next();
            LOGGER.debug("providerId : " + tmp.getProviderId());
            retVal.add(tmp.getProviderId());
        }
        return retVal;
    }
    
    public GatewayProvider getProvider(String providerId)
    {
        Iterator<GatewayProvider> iterator = PROVIDERS.iterator();
        while (iterator.hasNext())
        {
            GatewayProvider tmp = iterator.next();
            if (Objects.equals(tmp.getProviderId(), providerId))
            {
                return tmp;
            }
        }
        return null;
    }
}
