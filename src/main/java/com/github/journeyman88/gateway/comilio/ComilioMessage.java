/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.journeyman88.gateway.comilio;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author m.bignami
 */
public class ComilioMessage {
    
    private String message_type;
    private List<String> phone_numbers;
    private String sender_string;
    private String text;
    
    public ComilioMessage()
    {
        
    }
    
    public ComilioMessage(String from, String to, String message)
    {
        this.message_type = "Smart";
        this.sender_string = from;
        this.text = message;
        this.phone_numbers = new ArrayList<>();
        this.phone_numbers.add(to);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public List<String> getPhone_numbers() {
        return phone_numbers;
    }

    public void setPhone_numbers(List<String> phone_numbers) {
        this.phone_numbers = phone_numbers;
    }

    public String getSender_string() {
        return sender_string;
    }

    public void setSender_string(String sender_string) {
        this.sender_string = sender_string;
    }
    
}
