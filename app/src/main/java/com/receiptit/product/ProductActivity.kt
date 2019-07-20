package com.receiptit.product

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.receiptit.BaseNavigationDrawerActivity
import com.receiptit.R
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.SimpleResponse
import com.receiptit.network.model.product.ProductInfo
import com.receiptit.network.model.product.ProductUpdateBody
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ProductApi
import kotlinx.android.synthetic.main.activity_product.ed_product_description_value
import kotlinx.android.synthetic.main.activity_product.ed_product_name_value
import kotlinx.android.synthetic.main.activity_product.ed_product_price_value
import kotlinx.android.synthetic.main.activity_product.ed_product_quantity_value
import retrofit2.Call
import retrofit2.Response

class ProductActivity : BaseNavigationDrawerActivity() {

    private val PRODUCT_ID = "PRODUCT_ID"
    private val RECEIPT_ID = "RECEIPT_ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
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
        ed_product_description_value.hint = item.description
    }

    private fun showError(error: String){
        Toast.makeText(this, getString(R.string.product_update_error) + error, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_product, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_edit_product_save) {
            if (!validInput())
                showError(getString(R.string.edit_user_password_invalid_error))
            else
                saveProduct()
            true
        } else
            super.onOptionsItemSelected(item)
    }

    private fun validInput(): Boolean {
        val name = ed_product_name_value.text.toString()
        val quantity = ed_product_quantity_value.text.toString()
        val price = ed_product_price_value.text.toString()

        return try {
            quantity.toInt()
            price.toDouble()
            name != ""
        } catch (e: Exception) {
            false
        }
    }

    private fun saveProduct() {
        val body = createBody()
        val productService = ServiceGenerator.createService(ProductApi::class.java)
        val call = productService.updateProduct(body)
        call.enqueue(RetrofitCallback(object: RetrofitCallbackListener<SimpleResponse>{
            override fun onResponseSuccess(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                setResult(Activity.RESULT_OK)
                finish()
            }

            override fun onResponseError(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showError(it) }
            }

            override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                t?.message?.let { showError(it) }
            }

        }))

    }

    private fun createBody(): ProductUpdateBody{
        val productId = intent.getIntExtra(PRODUCT_ID, 0)
        val receiptId = intent.getIntExtra(RECEIPT_ID, 0)
        val body = ProductUpdateBody(receiptId, productId)
        val description = ed_product_description_value.text.toString()
        val name = ed_product_name_value.text.toString()
        val quantity = ed_product_quantity_value.text.toString()
        val price = ed_product_price_value.text.toString()

        if (description != "")
            body.description = description

        if (name != "")
            body.name = name

        if (quantity != "")
            body.quantity = quantity.toInt()

        if (price != "")
            body.price = price.toDouble()

        return body
    }

    override fun onBackPressed() {
        finish()
    }
}
