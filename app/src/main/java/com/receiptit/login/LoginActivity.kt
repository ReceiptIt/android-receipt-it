package com.receiptit.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.receiptit.R
import com.receiptit.databinding.ActivityLoginBinding
import com.receiptit.viewModel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    val model: LoginViewModel by lazy {
        ViewModelProviders.of(this, ViewModelFactory {LoginViewModel()}).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewmodel = model
        binding.lifecycleOwner = this

        //TODO: find a way to access EditText value via data binding
        btn_login_button.setOnClickListener {
            showLoginInvalidMessage()

        }
    }

    fun showLoginInvalidMessage() {
        if (ed_username.text.toString() == "" || ed_password.text.toString() == "" )
            Toast.makeText(this, getString(R.string.login_error_empty_username_or_password), Toast.LENGTH_SHORT).show()
        return
    }

    fun rememberUser() {
        if (cb_remember_me.isChecked) {
            storeUserInfoIntoCache()
        }
    }

    //TODO: store user info into cache
    fun storeUserInfoIntoCache() {
    }



}
