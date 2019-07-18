package com.receiptit.receiptList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.receiptit.R
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.receipt.ReceiptInfo
import com.receiptit.network.model.receipt.UserReceiptsRetrieveResponse
import com.receiptit.network.model.user.UserInfo
import com.receiptit.network.model.user.UserInfoRetrieveResponse
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ReceiptApi
import com.receiptit.network.service.UserApi
import com.receiptit.receiptProductList.ReceiptProductListActivity
import com.receiptit.userProfile.EditUserActivity
import kotlinx.android.synthetic.main.activity_receipt_list.*

import kotlinx.android.synthetic.main.content_receipt_list.*
import retrofit2.Call
import retrofit2.Response

class ReceiptListActivity : AppCompatActivity(), ReceiptListRecyclerViewAdapter.onReceiptListItemClickListerner,
    NavigationView.OnNavigationItemSelectedListener{

    private val USER_INFO = "USER_INFO"
    private val RECEIPT_ID = "RECEIPT_ID"
    private val ACTIVITY_RESULT_EDIT_USER_ACTIVITY = 1
    private var userInfo : UserInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_list_nav_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
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

    private fun createNavDrawer(userInfo: UserInfo) {
        val drawerLayout: DrawerLayout = findViewById(R.id.dl_receipt_list_drawer_layout)
        val navView: NavigationView = findViewById(R.id.nv_receipt_list_nav_view)

        updateUserInfo()

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
    }

    private fun showReceiptListError(error: String){
        Toast.makeText(this, getString(R.string.receipt_list_retrieve_error) + error, Toast.LENGTH_SHORT).show()
    }

    private fun showUserInfoError(error: String){
        Toast.makeText(this, getString(R.string.receipt_list_edit_user) + error, Toast.LENGTH_SHORT).show()
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
                createNavDrawer(userInfo!!)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val userInfo = intent.getSerializableExtra(USER_INFO) as UserInfo
        when (item.itemId) {
            R.id.receipt_list_edit_user -> {
                val intent = Intent(this, EditUserActivity::class.java)
                intent.putExtra(USER_INFO, userInfo)
                startActivityForResult(intent, ACTIVITY_RESULT_EDIT_USER_ACTIVITY)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.dl_receipt_list_drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.dl_receipt_list_drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_receipt_list, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.receipt_list_actions_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ACTIVITY_RESULT_EDIT_USER_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                refreshUserInfo()
            }
        }
    }

    private fun refreshUserInfo() {
        val userId = userInfo?.user_id
        val userService = ServiceGenerator.createService(UserApi::class.java)
        val call = userId?.let { userService.getUserInfo(it) }
        call?.enqueue(RetrofitCallback(object: RetrofitCallbackListener<UserInfoRetrieveResponse> {
            override fun onResponseSuccess(
                call: Call<UserInfoRetrieveResponse>?,
                response: Response<UserInfoRetrieveResponse>?
            ) {
                userInfo = response?.body()?.userInfo
                updateUserInfo()
            }

            override fun onResponseError(
                call: Call<UserInfoRetrieveResponse>?,
                response: Response<UserInfoRetrieveResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showUserInfoError(it) }
            }

            override fun onFailure(call: Call<UserInfoRetrieveResponse>?, t: Throwable?) {
                t?.message?.let { showUserInfoError(it) }
            }

        }))
    }

    fun updateUserInfo() {
        val userName: TextView = findViewById(R.id.tv_receipt_list_user_name)
        val userEmail: TextView = findViewById(R.id.tv_receipt_list_user_email)

        userName.text = userInfo?.first_name + " " + userInfo?.last_name
        userEmail.text = userInfo?.email
    }


}
