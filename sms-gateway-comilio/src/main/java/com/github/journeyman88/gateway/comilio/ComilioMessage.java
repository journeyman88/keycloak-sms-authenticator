/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.journeyman88.gateway.comilio;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author m.bignami
 */
public class ComilioMessage {
    
    @JsonProperty("message_type")
    private String messageType;
    @JsonProperty("phone_numbers")
    private List<String> phoneNumbers;
    @JsonProperty("sender_string")
    private String sender;
    private String text;
    
    public ComilioMessage()
    {
        
    }
    
    public ComilioMessage(String from, String to, String message)
    {
        this.messageType = "Smart";
        this.sender = from;
        this.text = message;
        this.phoneNumbers = new ArrayList<>();
        this.phoneNumbers.add(to);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    
}
