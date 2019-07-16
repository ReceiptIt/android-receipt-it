package com.receiptit.network.service

import com.receiptit.network.model.LoginBody
import com.receiptit.network.model.LoginResponse
import com.receiptit.network.model.LogoutResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthenticationApi {

    @POST("/auth/login")
    fun login(@Body body: LoginBody): Call<LoginResponse>

    @GET("/auth/logout")
    fun logout(): Call<LogoutResponse>
}