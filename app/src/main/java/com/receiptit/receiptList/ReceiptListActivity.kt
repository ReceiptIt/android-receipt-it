package com.receiptit.receiptList

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
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

class ReceiptListActivity : ReceiptListRecyclerViewAdapter.onReceiptListItemClickListerner,
    BaseNavigationDrawerActivity(){

    private val USER_INFO = "USER_INFO"
    private val RECEIPT_ID = "RECEIPT_ID"
    private var userInfo : UserInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_list_nav_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        super.onCreateDrawer()
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

    private fun showReceiptListError(error: String){
        Toast.makeText(this, getString(R.string.receipt_list_retrieve_error) + error, Toast.LENGTH_SHORT).show()
    }

    private fun init() {
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
                message?.getErrorMessage()?.let { showReceiptListError(it) }
            }

            override fun onFailure(call: Call<UserReceiptsRetrieveResponse>?, t: Throwable?) {
                t?.message?.let { showReceiptListError(it) }
            }

        }))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResultUpdateUserInfo(requestCode, resultCode)
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
        val fm = supportFragmentManager
        val fr = AddReceiptFragment()
        val fragmentTransaction = fm.beginTransaction()
        fragmentTransaction.replace(R.id.fg_receipt_list_add_receipt, fr)
        fragmentTransaction.commit()
    }

}
