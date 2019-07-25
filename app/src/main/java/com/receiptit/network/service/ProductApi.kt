package com.receiptit.network.service

import com.receiptit.network.model.SimpleResponse
import com.receiptit.network.model.product.*
import retrofit2.Call
import retrofit2.http.*

interface ProductApi {

    @GET("/product/{productId}")
    fun getProduct(@Path("productId") productId: Int): Call<ProductInfo>

    @GET("/product")
    fun getProductsFromReceipt(@Query("receiptId") receiptId: Int): Call<ProductFromReceiptResponse>

    @POST("/product")
    fun createProduct(@Body body: ProductCreateBody): Call<ProductCreateResponse>

    @PUT("/product")
    fun updateProduct(@Body body: ProductUpdateBody): Call<SimpleResponse>

    @DELETE("/product/{productId}")
    fun deleteProduct(@Path("productId") productId: Int): Call<SimpleResponse>

    @POST("/product/batch")
    fun createBatchProduct(@Body body: ProductBatchCreateBody): Call<ProductBatchCreateResponse>
}