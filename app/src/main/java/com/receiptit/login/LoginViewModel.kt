package com.receiptit.login

import android.view.View
import androidx.lifecycle.*
import com.receiptit.model.AuthenticationBody
import com.receiptit.model.AuthenticationResponse
import com.receiptit.services.AuthenticationApi
import com.receiptit.singleLiveEvent.SingleLiveEvent
import com.receiptit.services.ServiceGenerator
import retrofit2.Call
import retrofit2.Response


/***
 * https://stackoverflow.com/questions/50876372/live-data-and-2-way-data-binding-custom-setter-not-being-called
 */

class LoginViewModel : ViewModel() {

    private val mutableUsername = MutableLiveData<String>().apply { value = "testhjbawdjkda@test1.com" }
    val username = MediatorLiveData<String>().apply {
        addSource(mutableUsername) { value ->
            setValue(value)
        }
    }.also { it.observeForever { /* empty */ } }

    private val mutablePassword = MutableLiveData<String>().apply { value = "213131" }
    val password = MediatorLiveData<String>().apply {
        addSource(mutablePassword) { value ->
            setValue(value)
        }
    }.also { it.observeForever { /* empty */ } }

    private val mutableRememberMe = MutableLiveData<Boolean>().apply { value = false }
    val rememberMe = MediatorLiveData<Boolean>().apply {
        addSource(mutableRememberMe) { value ->
            setValue(value)
        }
    }.also { it.observeForever { /* empty */ } }

    val mutableProgressBarVisible = MutableLiveData<Int>().apply { value = View.GONE }

    /***
     * https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
     */
    private val singleIsUserInfoValidEvent = SingleLiveEvent<Any>()
    val isUserInfoValidEvent : LiveData<Any>
    get() = singleIsUserInfoValidEvent

    private val singleUserLoginSuccessEvent = SingleLiveEvent<Any>()
    val userLoginSuccessEvent : LiveData<Any>
    get() = singleUserLoginSuccessEvent

    private val singleUserLoginFailEvent = SingleLiveEvent<Any>()
    val userLoginFailEvent : LiveData<Any>
        get() = singleUserLoginFailEvent


    private fun isUserInfoValid(): Boolean {
        return !(password.value.toString() == "" || username.value.toString() == "")
    }

    private fun isRememberMe(): Boolean {
        return rememberMe.value!!
    }

    private fun rememberUser() {
        if (isRememberMe()) {
            storeUserInfoIntoCache()
        }
    }

    //TODO: store user info into cache
    private fun storeUserInfoIntoCache() {
    }

    private fun login() {
        rememberUser()
        //user login success
        mutableProgressBarVisible.value = View.VISIBLE
        val loginService = ServiceGenerator.createAuthenticationService(AuthenticationApi::class.java)
        val body = AuthenticationBody(username.value!!, password.value!!)
        val call = loginService.login(body)

        call.enqueue(object: retrofit2.Callback<AuthenticationResponse>{
            override fun onFailure(call: Call<AuthenticationResponse>?, t: Throwable?) {
                mutableProgressBarVisible.value = View.GONE
                singleUserLoginFailEvent.call()
            }

            override fun onResponse(call: Call<AuthenticationResponse>?, response: Response<AuthenticationResponse>?) {
                val result = response?.body()
                result?.authToken?.let { ServiceGenerator.storeAuthToken(it) }
                mutableProgressBarVisible.value = View.GONE
                singleUserLoginSuccessEvent.call()
            }
        })
    }

    fun onClick() {
        if (!isUserInfoValid()) {
            singleIsUserInfoValidEvent.call()
        } else {
            login()
        }
    }


}