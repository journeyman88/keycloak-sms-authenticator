/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.journeyman88.gateway;

/**
 *
 * @author m.bignami
 */
public interface GatewayProvider {
    
    public String getProviderId();
    public boolean sendSms(String username, String password, String from, String to, String message);
    
}
