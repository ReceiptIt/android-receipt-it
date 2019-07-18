package com.receiptit.product

import android.os.Bundle
import android.widget.Toast
import com.receiptit.BaseNavigationDrawerActivity
import com.receiptit.R
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.product.ProductInfo
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ProductApi
import kotlinx.android.synthetic.main.receipt_product_list_receipt_info_item.*
import retrofit2.Call
import retrofit2.Response

class ProductActivity : BaseNavigationDrawerActivity() {

    private val PRODUCT_ID = "PRODUCT_ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        title = getString(R.string.product_activity)
        init()
    }

    private fun init() {
        val productId = intent.getIntExtra(PRODUCT_ID, 0)
        val productService = ServiceGenerator.createService(ProductApi::class.java)
        val call = productService.getProduct(productId)
        call.enqueue(RetrofitCallback(object : RetrofitCallbackListener<ProductInfo>{
            override fun onResponseSuccess(call: Call<ProductInfo>?, response: Response<ProductInfo>?) {
                val product = response?.body()
                product?.let { bindValueToView(it) }
            }

            override fun onResponseError(call: Call<ProductInfo>?, response: Response<ProductInfo>?) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showError(it) }
            }

            override fun onFailure(call: Call<ProductInfo>?, t: Throwable?) {
                t?.message?.let { showError(it) }
            }

        }))
    }

    fun bindValueToView(item: ProductInfo) {
        ed_product_name_value.hint = item.name
        ed_product_quantity_value.hint = item.quantity.toString()
        ed_product_price_value.hint = item.price.toString()
        ed_product_currency_value.hint = item.currency_code.currencyCode
        ed_product_description_value.hint = item.description
    }

    private fun showError(error: String){
        Toast.makeText(this, getString(R.string.product_retrieve_error) + error, Toast.LENGTH_SHORT).show()
    }
}
