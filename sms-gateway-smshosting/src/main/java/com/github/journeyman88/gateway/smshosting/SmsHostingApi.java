/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.journeyman88.gateway.smshosting;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 *
 * @author m.bignami
 */
public interface SmsHostingApi {
    
    @FormUrlEncoded
    @POST("sms/send")
    public Call<SmsHostingResponse> sendSms(@Header("Authorization") String auth, @Field("from") String from, @Field("to") String to, @Field("text") String msg);
    
    
}
