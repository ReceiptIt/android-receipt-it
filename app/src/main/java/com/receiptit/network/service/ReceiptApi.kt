package com.receiptit.network.service

import com.receiptit.network.model.SimpleResponse
import com.receiptit.network.model.receipt.*
import retrofit2.Call
import retrofit2.http.*

interface ReceiptApi {

    @POST("/receipt")
    fun createReceipt(@Body body: ReceiptCreateBody): Call<ReceiptCreateResponse>

    @GET("/receipt")
    fun getUserReceipts(@Query("userId") userId: Int): Call<UserReceiptsRetrieveResponse>

    @DELETE("/receipt/{receiptId}")
    fun deleteReceipt(@Path("receiptId") receiptId: Int): Call<SimpleResponse>

    @GET("/receipt/{receiptId}")
    fun getReceiptProducts(@Path("receiptId") receiptId: Int): Call<ReceiptProductsResponse>

    @PUT("/receipt/{receiptId}")
    fun updateReceipt(@Path("receiptId") receiptId: Int, @Body body: ReceiptUpdateBody): Call<SimpleResponse>

}