package com.receiptit.receiptProductList

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.receiptit.R
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.product.ProductCreateBody
import com.receiptit.network.model.product.ProductCreateResponse
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ProductApi
import kotlinx.android.synthetic.main.activity_create_product.*
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception

private const val RECEIPT_ID = "RECEIPT_ID"

class CreateProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_product)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_create_product, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_create_product_save) {
            if (validInput())
                saveProduct()
            else
                showError("Invalid Input")
            return true
        } else
            super.onOptionsItemSelected(item)
    }

    private fun validInput(): Boolean {
        val name = ed_receipt_product_list_add_product_name_value.text.toString()
        val quantity = ed_receipt_product_list_add_product_quantity_value.text.toString()
        val price = ed_receipt_product_list_add_product_price_value.text.toString()

        return try {
            quantity.toInt()
            price.toDouble()
            name != ""
        } catch (e: Exception) {
            false
        }

    }

    private fun saveProduct(){
        val body = createBody()
        val productService = ServiceGenerator.createService(ProductApi::class.java)
        val call = productService.createProduct(body)
        call.enqueue(RetrofitCallback(object: RetrofitCallbackListener<ProductCreateResponse>{
            override fun onResponseSuccess(
                call: Call<ProductCreateResponse>?,
                response: Response<ProductCreateResponse>?
            ) {
                setResult(Activity.RESULT_OK)
                finish()
            }

            override fun onResponseError(
                call: Call<ProductCreateResponse>?,
                response: Response<ProductCreateResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showError(it) }
            }

            override fun onFailure(call: Call<ProductCreateResponse>?, t: Throwable?) {
                t?.message?.let { showError(it) }
            }
        }))
    }

    private fun createBody(): ProductCreateBody {
        val receiptId = intent.getIntExtra(RECEIPT_ID, 0)
        val name = ed_receipt_product_list_add_product_name_value.text.toString()
        val quantity = ed_receipt_product_list_add_product_quantity_value.text.toString().toInt()
        val price = ed_receipt_product_list_add_product_price_value.text.toString().toDouble()
        val description = ed_receipt_product_list_add_product_description_value.text.toString()


        val body = ProductCreateBody(receiptId, name, quantity, price)

        if (description != "")
            body.description = description

        return body
    }

    private fun showError(error: String){
        Toast.makeText(this, getString(R.string.receipt_product_list_product_add_error) + error, Toast.LENGTH_SHORT).show()
    }

}
