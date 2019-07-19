package com.receiptit.receiptList

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.receiptit.R
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.receipt.ReceiptCreateBody
import com.receiptit.network.model.receipt.ReceiptCreateResponse
import com.receiptit.network.model.user.UserInfo
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ReceiptApi
import kotlinx.android.synthetic.main.activity_manually_create.*
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception

class ManuallyCreateActivity : AppCompatActivity() {

    private val USER_INFO = "USER_INFO"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manually_create)
        ed_receipt_list_add_receipt_manually_purchase_date_value.text
    }

    private fun createReceipt() {
        val purchaseDate = ed_receipt_list_add_receipt_manually_purchase_date_value.text.toString()
        val totalAmount = ed_receipt_list_add_receipt_manually_total_amount_value.text.toString()
        val merchant = ed_receipt_list_add_receipt_manually_merchant_value.text.toString()
        val postCode = ed_receipt_list_add_receipt_manually_merchant_value.text.toString()
        val comment = ed_receipt_list_add_receipt_manually_comment_value.text.toString()

        val userInfo = intent.getSerializableExtra(USER_INFO) as UserInfo
        val userId = userInfo.user_id
        val body = ReceiptCreateBody(userId, purchaseDate, totalAmount.toDouble(), merchant, postCode)

        if (comment != "")
            body.comment = comment

        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call = receiptService.createReceipt(body)
        call.enqueue(RetrofitCallback(object: RetrofitCallbackListener<ReceiptCreateResponse>{
            override fun onResponseSuccess(
                call: Call<ReceiptCreateResponse>?,
                response: Response<ReceiptCreateResponse>?
            ) {
                setResult(Activity.RESULT_OK)
                finish()
            }

            override fun onResponseError(
                call: Call<ReceiptCreateResponse>?,
                response: Response<ReceiptCreateResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showError(it) }
            }

            override fun onFailure(call: Call<ReceiptCreateResponse>?, t: Throwable?) {
                t?.message?.let { showError(it) }
            }

        }))

    }

    private fun validInputFiled(): Boolean {
        val purchaseDate = ed_receipt_list_add_receipt_manually_purchase_date_value.text.toString()
        val totalAmount = ed_receipt_list_add_receipt_manually_total_amount_value.text.toString()
        val merchant = ed_receipt_list_add_receipt_manually_merchant_value.text.toString()
        val postCode = ed_receipt_list_add_receipt_manually_merchant_value.text.toString()

        return purchaseDate != "" && totalAmount != "" && merchant != "" && postCode != ""
    }

    private fun validTotalAmount(): Boolean {
        val totalAmount = ed_receipt_list_add_receipt_manually_total_amount_value.text.toString()
        return try {
            totalAmount.toDouble()
            true
        } catch (e: Exception){
            false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_create_receipt_manully, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_create_receipt_manually_save) {
            if (validInputFiled() && validTotalAmount()) {
                createReceipt()
            } else {
                showError("Invalid Input")
            }
            return true
        } else
            super.onOptionsItemSelected(item)
    }

    private fun showError(message: String) {
        Toast.makeText(this, getString(R.string.receipt_list_add_receipt_manually_error) + message, Toast.LENGTH_SHORT).show()
    }
}
