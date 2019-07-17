package com.receiptit.network.service

import com.receiptit.network.model.CreateUserBody
import com.receiptit.network.model.UpdateUserBody
import com.receiptit.network.model.UserInfoResponse
import retrofit2.Call
import retrofit2.http.*

interface UserApi{

    @GET("/user/{userId}")
    fun getUserInfo(@Path("userId") userId: Int): Call<UserInfoResponse>

    @POST("/user")
    fun createUser(@Body body: CreateUserBody)

    @PUT("/user/{userID}")
    fun updateUserInfo(@Body body:UpdateUserBody? = null)
}