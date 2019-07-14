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
        ViewModelProviders.of(this, ViewModelFactory { LoginViewModel() }).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewmodel = model
        binding.lifecycleOwner = this
        init()

    }

    fun init() {
        btn_login_button.setOnClickListener {
            if (!model.isUserInfoValid()) {
                Toast.makeText(this, getString(R.string.login_error_empty_username_or_password), Toast.LENGTH_SHORT)
                    .show()
            } else {
                model.rememberUser()
            }
        }
    }

}
