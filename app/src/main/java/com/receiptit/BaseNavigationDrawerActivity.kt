package com.receiptit

import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.user.UserInfo
import com.receiptit.network.model.user.UserInfoRetrieveResponse
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.UserApi
import com.receiptit.userProfile.EditUserActivity
import kotlinx.android.synthetic.main.activity_receipt_list.*
import retrofit2.Call
import retrofit2.Response

abstract class BaseNavigationDrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val USER_INFO = "USER_INFO"
    private val ACTIVITY_RESULT_EDIT_USER_ACTIVITY = 1

   protected fun onCreateDrawer() {
        createNavDrawer()
   }

    private fun createNavDrawer() {
        val drawerLayout: DrawerLayout = findViewById(R.id.dl_drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        updateUserInfo()

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
    }

    protected fun onActivityResultUpdateUserInfo(requestCode: Int, resultCode: Int) {
        if (requestCode == ACTIVITY_RESULT_EDIT_USER_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                refreshUserInfo()
            }
        }
    }

    private fun refreshUserInfo() {
        val userInfo = intent.getSerializableExtra(USER_INFO) as UserInfo
        val userId = userInfo.user_id
        val userService = ServiceGenerator.createService(UserApi::class.java)
        val call = userId.let { userService.getUserInfo(it) }
        call.enqueue(RetrofitCallback(object: RetrofitCallbackListener<UserInfoRetrieveResponse> {
            override fun onResponseSuccess(
                call: Call<UserInfoRetrieveResponse>?,
                response: Response<UserInfoRetrieveResponse>?
            ) {
                intent.putExtra(USER_INFO, response?.body()?.userInfo)
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
        val userInfo = intent.getSerializableExtra(USER_INFO) as UserInfo
        val navView: NavigationView = findViewById(R.id.nav_view)
        val header = navView.getHeaderView(0)
        val userName: TextView = header.findViewById(R.id.tv_nav_header_user_name)
        val userEmail: TextView = header.findViewById(R.id.tv_nav_header_user_email)

        userName.text = userInfo.first_name + " " + userInfo.last_name
        userEmail.text = userInfo.email
    }

    private fun showUserInfoError(error: String){
        Toast.makeText(this, getString(R.string.receipt_list_edit_user) + error, Toast.LENGTH_SHORT).show()
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
        val drawerLayout: DrawerLayout = findViewById(R.id.dl_drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.dl_drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}