/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.journeyman88.gateway.smshosting;

import java.util.List;

/**
 *
 * @author m.bignami
 */
public class SmsHostingResponse {
    
    private String from;
    private String text;
    private String transactionId;
    private Long smsInserted;
    private Long smsNotInserted;
    private List<SmsHostingDetails> sms;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getSmsInserted() {
        return smsInserted;
    }

    public void setSmsInserted(Long smsInserted) {
        this.smsInserted = smsInserted;
    }

    public Long getSmsNotInserted() {
        return smsNotInserted;
    }

    public void setSmsNotInserted(Long smsNotInserted) {
        this.smsNotInserted = smsNotInserted;
    }

    public List<SmsHostingDetails> getSms() {
        return sms;
    }

    public void setSms(List<SmsHostingDetails> sms) {
        this.sms = sms;
    }
    
}
