package com.receiptit.receiptProductList

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.receiptit.BaseNavigationDrawerActivity
import com.receiptit.R
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.receipt.ReceiptProductsResponse
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ReceiptApi
import com.receiptit.product.ProductActivity
import kotlinx.android.synthetic.main.activity_receipt_product_list.*
import retrofit2.Call
import retrofit2.Response

class ReceiptProductListActivity : BaseNavigationDrawerActivity(), ReceiptProductListRecyclerViewAdapter.onReceiptProductListItemClickListerner {

    private val RECEIPT_ID = "RECEIPT_ID"
    private val PRODUCT_ID = "PRODUCT_ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_product_list)
        title = getString(R.string.receipt_product_list_activity)
        refreshReceiptProducts()
    }

    override fun onReceiptProductListItemClick(productId: Int) {
        val intent = Intent(this, ProductActivity::class.java)
        intent.putExtra(PRODUCT_ID, productId)
        startActivity(intent)
    }

    private fun createList(products: ReceiptProductsResponse) {
        val recyclerView = rv_receipt_product_list
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter =  ReceiptProductListRecyclerViewAdapter(products, this)
        recyclerView.adapter = adapter
    }

    private fun showError(error: String){
        Toast.makeText(this, getString(R.string.receipt_product_list_retrieve_error) + error, Toast.LENGTH_SHORT).show()
    }

    private fun refreshReceiptProducts() {
        val receiptId = intent.getIntExtra(RECEIPT_ID, 0)
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call = receiptService.getReceiptProducts(receiptId)
        call.enqueue(RetrofitCallback(object : RetrofitCallbackListener<ReceiptProductsResponse> {
            override fun onResponseSuccess(
                call: Call<ReceiptProductsResponse>?,
                response: Response<ReceiptProductsResponse>?
            ) {
                val list = response?.body()
                list?.let { createList(it) }
            }

            override fun onResponseError(
                call: Call<ReceiptProductsResponse>?,
                response: Response<ReceiptProductsResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showError(it) }
            }

            override fun onFailure(call: Call<ReceiptProductsResponse>?, t: Throwable?) {
                t?.message?.let { showError(it) }
            }

        }))

    }
}
