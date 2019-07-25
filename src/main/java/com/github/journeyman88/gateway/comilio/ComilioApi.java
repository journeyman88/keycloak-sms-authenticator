/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.journeyman88.gateway.comilio;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 *
 * @author m.bignami
 */
public interface ComilioApi {
    
    @POST("message")
    public Call<ComilioResponse> sendSms(@Header("Authorization") String auth, ComilioMessage message);
    
    
}
