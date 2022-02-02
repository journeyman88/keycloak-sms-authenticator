/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.journeyman88.gateway;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author m.bignami
 */
public class GatewayLoader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayLoader.class);
    private static ServiceLoader<GatewayProvider> PROVIDERS = ServiceLoader.load(GatewayProvider.class);
    
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
