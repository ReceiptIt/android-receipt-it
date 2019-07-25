package com.receiptit.network.service

import com.receiptit.network.model.imageProcessor.ImageProcessResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageProcessApi {

    @GET("/receipt/process/result")
    fun processImage(@Query("imageUrl") imageUrl: String): Call<ImageProcessResponse>
}