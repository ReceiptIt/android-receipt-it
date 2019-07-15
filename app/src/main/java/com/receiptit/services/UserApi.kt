package com.receiptit.services

import com.receiptit.model.UserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi{

    @GET("user/{userId}")
    fun getUserInfo(@Path("userId") userId: Int): Call<UserInfoResponse>

}