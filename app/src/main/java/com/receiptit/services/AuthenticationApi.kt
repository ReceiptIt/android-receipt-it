package com.receiptit.services

import com.receiptit.model.AuthenticationBody
import com.receiptit.model.AuthenticationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationApi {
    @POST("/auth/login")
    fun login(@Body body: AuthenticationBody): Call<AuthenticationResponse>
}