/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.journeyman88.gateway.gatewayapi;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author m.bignami
 */
public class GatewayApiMessage {
    
    private String message;
    private List<GatewayApiRecipient> recipients;
    private String sender;
    
    public GatewayApiMessage()
    {
        
    }
    
    public GatewayApiMessage(String from, String to, String message)
    {
        this.sender = from;
        this.message = message;
        this.recipients = new ArrayList<>();
        this.recipients.add(new GatewayApiRecipient(to));
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<GatewayApiRecipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<GatewayApiRecipient> recipients) {
        this.recipients = recipients;
    }
    
}
