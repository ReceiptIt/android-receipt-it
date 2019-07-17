package com.receiptit.receiptList

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.receiptit.R
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.receipt.ReceiptInfo
import com.receiptit.network.model.receipt.UserReceiptsRetrieveResponse
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ReceiptApi
import com.receiptit.receiptProductList.ReceiptProductListActivity

import kotlinx.android.synthetic.main.activity_receipt_list.*
import kotlinx.android.synthetic.main.content_receipt_list.*
import retrofit2.Call
import retrofit2.Response

class ReceiptListActivity : AppCompatActivity(), ReceiptListRecyclerViewAdapter.onReceiptListItemClickListerner {

    private val USER_ID = "USER_ID"
    private val RECEIPT_ID = "RECEIPT_ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_list)
        setSupportActionBar(toolbar)
        title = getString(R.string.receipt_list_activity)
        init()
    }

    override fun onReceiptListItemClick(receiptId: Int) {
        val intent = Intent(this, ReceiptProductListActivity::class.java)
        intent.putExtra(RECEIPT_ID, receiptId)
        startActivity(intent)
    }

    private fun createList(list: List<ReceiptInfo>) {
        val recyclerView = rv_receipt_list
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ReceiptListRecyclerViewAdapter(list, this)
        recyclerView.adapter = adapter
    }

    private fun showError(error: String){
        Toast.makeText(this, getString(R.string.receipt_list_retrieve_error) + error, Toast.LENGTH_SHORT).show()
    }

    private fun init() {
        val userId = intent.getIntExtra(USER_ID, 0)
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call = receiptService.getUserReceipts(userId)
        call.enqueue(RetrofitCallback(object : RetrofitCallbackListener<UserReceiptsRetrieveResponse> {
            override fun onResponseSuccess(
                call: Call<UserReceiptsRetrieveResponse>?,
                response: Response<UserReceiptsRetrieveResponse>?
            ) {
                val list = response?.body()?.receipts
                list?.let { createList(it) }
            }

            override fun onResponseError(
                call: Call<UserReceiptsRetrieveResponse>?,
                response: Response<UserReceiptsRetrieveResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showError(it) }
            }

            override fun onFailure(call: Call<UserReceiptsRetrieveResponse>?, t: Throwable?) {
                t?.message?.let { showError(it) }
            }

        }))

    }

}
