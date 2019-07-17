package com.receiptit.network.service

import com.receiptit.network.model.SimpleResponse
import retrofit2.Call
import com.receiptit.network.model.receipt.ReceiptCreateBody
import com.receiptit.network.model.receipt.ReceiptCreateResponse
import com.receiptit.network.model.receipt.UserReceiptsRetrieveResponse
import retrofit2.http.*

interface ReceiptApi {

    @POST("/receipt")
    fun createReceipt(@Body body: ReceiptCreateBody): Call<ReceiptCreateResponse>

    @GET("/receipt")
    fun getUserReceipts(@Query("userId") userId: Int): Call<UserReceiptsRetrieveResponse>

    @DELETE("/receipt/{receiptId}")
    fun deleteReceipt(@Path("receiptId") receiptId: Int): Call<SimpleResponse>

    @GET("/receiptId/{receiptId}")
    fun getReceiptProducts(@Path("receiptId") receiptId: Int): Call<>_

}