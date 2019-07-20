package com.receiptit.receiptList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.receiptit.BaseNavigationDrawerActivity
import com.receiptit.R
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.receipt.ReceiptInfo
import com.receiptit.network.model.receipt.UserReceiptsRetrieveResponse
import com.receiptit.network.model.user.UserInfo
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ReceiptApi
import com.receiptit.receiptProductList.ReceiptProductListActivity

import kotlinx.android.synthetic.main.content_receipt_list.*
import retrofit2.Call
import retrofit2.Response
import androidx.appcompat.app.AlertDialog
import com.receiptit.network.model.SimpleResponse


class ReceiptListActivity : ReceiptListRecyclerViewAdapter.onReceiptListItemClickListerner,
    BaseNavigationDrawerActivity(), AddReceiptFragment.OnAddReceiptFragmentCloseListener{

    private val USER_INFO = "USER_INFO"
    private val RECEIPT_ID = "RECEIPT_ID"
    private var userInfo : UserInfo? = null
    private var isFragmentShow = false

    private var fragment: AddReceiptFragment? = null

    private var ACTIVITY_RESULT_MANUALLY_CREATE_RECEIPT_ACTIVITY = 2
    private var ACTIVITY_RESULT_RECEIPT_PRODUCT_ACTIVITY = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_list_nav_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        super.onCreateDrawer()
        refreshReceiptList()
    }

    override fun onReceiptListItemClick(receiptId: Int) {
        val intent = Intent(this, ReceiptProductListActivity::class.java)
        intent.putExtra(RECEIPT_ID, receiptId)
        intent.putExtra(USER_INFO, userInfo)
        startActivityForResult(intent, ACTIVITY_RESULT_RECEIPT_PRODUCT_ACTIVITY)
    }

    private fun createList(list: List<ReceiptInfo>) {
        val recyclerView = rv_receipt_list
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ReceiptListRecyclerViewAdapter(list, this)
        recyclerView.adapter = adapter
    }

    private fun showGetReceiptListError(error: String){
        Toast.makeText(this, getString(R.string.receipt_list_retrieve_error) + error, Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteReceiptListError(error: String){
        Toast.makeText(this, getString(R.string.receipt_list_delete_receipt_error) + error, Toast.LENGTH_SHORT).show()
    }

    private fun refreshReceiptList() {
        userInfo = intent.getSerializableExtra(USER_INFO) as UserInfo
        val userId = userInfo?.user_id
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call = userId?.let { receiptService.getUserReceipts(it) }
        call?.enqueue(RetrofitCallback(object : RetrofitCallbackListener<UserReceiptsRetrieveResponse> {
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
                message?.getErrorMessage()?.let { showGetReceiptListError(it) }
            }

            override fun onFailure(call: Call<UserReceiptsRetrieveResponse>?, t: Throwable?) {
                t?.message?.let { showGetReceiptListError(it) }
            }

        }))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResultUpdateUserInfo(requestCode, resultCode)
        if (requestCode == ACTIVITY_RESULT_MANUALLY_CREATE_RECEIPT_ACTIVITY ||
            requestCode == ACTIVITY_RESULT_RECEIPT_PRODUCT_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                refreshReceiptList()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_receipt_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_receipt_list_add_receipt) {
            showFragment()
            true
        } else
            super.onOptionsItemSelected(item)
    }

    private fun showFragment() {
        supportActionBar?.hide()
        fragment = userInfo?.let { AddReceiptFragment.newInstance(it)}
        val fm = supportFragmentManager
        val fragmentTransaction = fm.beginTransaction()
        fragment?.let { fragmentTransaction.replace(R.id.fg_receipt_list_add_receipt, it)}
        fragmentTransaction.commit()
        isFragmentShow = true
    }

    override fun onAddReceiptFragmentClose() {
       if (isFragmentShow) {
           fragment?.let { supportFragmentManager.beginTransaction().remove(it)}?.commit()
           isFragmentShow = false
           supportActionBar?.show()
       }
    }

    override fun onAddReceiptManually() {
        val intent = Intent(this, ManuallyCreateActivity::class.java)
        intent.putExtra(USER_INFO, userInfo)
        startActivityForResult(intent, ACTIVITY_RESULT_MANUALLY_CREATE_RECEIPT_ACTIVITY)
    }

    override fun onReceiptListItemLongClick(receiptId: Int) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.receipt_list_delete_receipt_dialog_title))
            .setMessage(getString(R.string.receipt_list_delete_receipt_dialog_message))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                deleteReceipt(receiptId)
            }

            .setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun deleteReceipt(receiptId: Int) {
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call = receiptService.deleteReceipt(receiptId)
        call.enqueue(RetrofitCallback(object : RetrofitCallbackListener<SimpleResponse>{
            override fun onResponseSuccess(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                refreshReceiptList()
            }

            override fun onResponseError(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showDeleteReceiptListError(it) }
            }

            override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                t?.message?.let { showDeleteReceiptListError(it) }
            }

        }))
    }

}
