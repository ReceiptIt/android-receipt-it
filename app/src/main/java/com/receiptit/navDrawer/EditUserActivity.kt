package com.receiptit.navDrawer

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.receiptit.R
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.SimpleResponse
import com.receiptit.network.model.user.UserInfo
import com.receiptit.network.model.user.UserUpdateBody
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.UserApi
import kotlinx.android.synthetic.main.activity_edit_user.*
import retrofit2.Call
import retrofit2.Response



class EditUserActivity : AppCompatActivity() {

    private val USER_INFO = "USER_INFO"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
        init()
    }

    private fun init() {
        val userInfo = intent.getSerializableExtra(USER_INFO) as UserInfo
        ed_edit_user_first_name_value.hint = userInfo.first_name
        ed_edit_user_last_name_value.hint = userInfo.last_name
        ed_edit_user_email_value.hint = userInfo.email
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_user, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_edit_user_save) {
            if (!isValidNewPassword())
                showError(getString(R.string.edit_user_password_invalid_error))
            else
                saveUserProfile()
            true
        } else
            super.onOptionsItemSelected(item)
    }

    private fun isValidNewPassword(): Boolean {
        val newPassword = ed_edit_user_password_value.text.toString()
        val confirmPassword = ed_edit_user_password_confirm_value.text.toString()
        return newPassword == confirmPassword
    }

    private fun showError(message: String) {
        Toast.makeText(this, getString(R.string.edit_user_error) + message, Toast.LENGTH_SHORT).show()
    }

    private fun saveUserProfile() {
        val body = createUpdateUserBody()
        val userService = ServiceGenerator.createService(UserApi::class.java)
        val userInfo = intent.getSerializableExtra(USER_INFO) as UserInfo
        val userId = userInfo.user_id
        val call = userService.updateUserInfo(userId, body)
        call.enqueue(RetrofitCallback(object : RetrofitCallbackListener<SimpleResponse> {
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

    private fun createUpdateUserBody(): UserUpdateBody{
        val body= UserUpdateBody()
        val password  = ed_edit_user_password_confirm_value.text.toString()
        val email = ed_edit_user_email_value.text.toString()
        val firstName = ed_edit_user_first_name_value.text.toString()
        val lastName = ed_edit_user_last_name_value.text.toString()

        if (password != "")
            body.password = password

        if (email != "")
            body.email = email

        if (firstName != "")
            body.first_name = firstName

        if (lastName != "")
            body.last_name = lastName

        return body
    }
}
