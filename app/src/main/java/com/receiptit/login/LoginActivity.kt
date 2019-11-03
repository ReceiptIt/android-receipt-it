package com.receiptit.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.receiptit.R
import com.receiptit.databinding.ActivityLoginBinding
import com.receiptit.receiptList.ReceiptListActivity
import com.receiptit.viewModel.ViewModelFactory


class LoginActivity : AppCompatActivity() {

    private val model: LoginViewModel by lazy {
        ViewModelProviders.of(this, ViewModelFactory { LoginViewModel() }).get(LoginViewModel::class.java)
    }

    private val USER_INFO = "USER_INFO"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.login_activity)
        init()
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = model
        binding.lifecycleOwner = this

    }

    private fun init() {
        //TODO: create user function
        //TODO: edit user info

        model.isUserInfoValidEvent.observe(this, Observer {
            Toast.makeText(this, getString(R.string.login_error_empty_username_or_password), Toast.LENGTH_SHORT)
                .show()
        })

        model.mutableUserInfo.observe(this, Observer {
            val intent = Intent(this, ReceiptListActivity::class.java)
            intent.putExtra(USER_INFO, it)
            startActivity(intent)
        })

        model.mutableErrorMessage.observe(this, Observer {
            Toast.makeText(this, getString(R.string.login_error_authentication_fail) + ": " + it,
                Toast.LENGTH_SHORT).show()
        })
    }

}
