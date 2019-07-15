package com.receiptit.services

import com.receiptit.model.LoginBody
import com.receiptit.model.LoginResponse
import com.receiptit.model.LogoutResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthenticationApi {

    @POST("/auth/login")
    fun login(@Body body: LoginBody): Call<LoginResponse>

    @GET("auth/logout")
    fun logout(): Call<LogoutResponse>
}