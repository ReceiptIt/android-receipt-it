package com.receiptit.network.service

import com.receiptit.network.model.SimpleResponse
import com.receiptit.network.model.user.*
import retrofit2.Call
import retrofit2.http.*

interface UserApi{

    @GET("/user/{userId}")
    fun getUserInfo(@Path("userId") userId: Int): Call<UserInfoRetrieveResponse>

    @POST("/user")
    fun createUser(@Body body: UserCreateBody): Call<UserCreateResponse>

    @PUT("/user/{userID}")
    fun updateUserInfo(@Body body: UserUpdateBody? = null): Call<SimpleResponse>
}