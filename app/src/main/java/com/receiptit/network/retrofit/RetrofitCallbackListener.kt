package com.receiptit.network.retrofit

import retrofit2.Call
import retrofit2.Response

interface RetrofitCallbackListener<T> {
    fun onResponseSuccess(call: Call<T>?, response: Response<T>?)
    fun onResponseError(call: Call<T>?, response: Response<T>?)
//    fun onResponseUnauthorized(call: Call<T>?, response: Response<T>?)
    fun onFailure(call: Call<T>?, t: Throwable?)
}