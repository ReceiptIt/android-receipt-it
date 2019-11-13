package com.receiptit.receiptList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.receipt.ReceiptInfo
import com.receiptit.network.model.receipt.UserReceiptsRetrieveResponse
import com.receiptit.network.model.user.UserInfo
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ReceiptApi
import retrofit2.Call
import retrofit2.Response

class ReceiptListViewModel : ViewModel() {

    private var mutableReceiptList: MutableLiveData<ArrayList<ReceiptInfo>> = MutableLiveData()

    private fun refreshReceiptList(userInfo: UserInfo) {
        val userId = userInfo.user_id
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call = userId.let { receiptService.getUserReceipts(it) }
        call.enqueue(RetrofitCallback(object : RetrofitCallbackListener<UserReceiptsRetrieveResponse> {
            override fun onResponseSuccess(
                call: Call<UserReceiptsRetrieveResponse>?,
                response: Response<UserReceiptsRetrieveResponse>?
            ) {
                val list = response?.body()?.receipts
                list?.let { mutableReceiptList.value = list }
            }

            override fun onResponseError(
                call: Call<UserReceiptsRetrieveResponse>?,
                response: Response<UserReceiptsRetrieveResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showGetReceiptListError(it) }
                hideProgressBar()
            }

            override fun onFailure(call: Call<UserReceiptsRetrieveResponse>?, t: Throwable?) {
                t?.message?.let { showGetReceiptListError(it) }
                hideProgressBar()
            }

        }))
    }
}

