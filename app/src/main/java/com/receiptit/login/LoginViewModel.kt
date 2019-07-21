package com.receiptit.login

import android.view.View
import androidx.lifecycle.*
import com.receiptit.network.model.authentication.LoginBody
import com.receiptit.network.model.authentication.LoginResponse
import com.receiptit.network.service.AuthenticationApi
import com.receiptit.util.SingleLiveEvent
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.user.UserInfo
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import retrofit2.Call
import retrofit2.Response


/***
 * https://stackoverflow.com/questions/50876372/live-data-and-2-way-data-binding-custom-setter-not-being-called
 */

class LoginViewModel : ViewModel() {

    private val mutableUsername = MutableLiveData<String>().apply { value = "noway@android.com" }
    val username = MediatorLiveData<String>().apply {
        addSource(mutableUsername) { value ->
            setValue(value)
        }
    }.also { it.observeForever { /* empty */ } }

    private val mutablePassword = MutableLiveData<String>().apply { value = "test" }
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

    val mutableErrorMessage = MutableLiveData<String>()

    val mutableUserInfo = MutableLiveData<UserInfo>()

    /***
     * https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
     */
    private val singleIsUserInfoValidEvent = SingleLiveEvent<Any>()
    val isUserInfoValidEvent : LiveData<Any>
    get() = singleIsUserInfoValidEvent

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

    private fun setErrorMessage(message: String) {
        mutableErrorMessage.value = message
    }

    private fun setUserInfo(info: UserInfo) {
        mutableUserInfo.value = info
    }

    //TODO: store user info into cache
    private fun storeUserInfoIntoCache() {
    }

    private fun login() {
        rememberUser()
        mutableProgressBarVisible.value = View.VISIBLE
        val authenticationService = ServiceGenerator.createAuthenticationService(AuthenticationApi::class.java)
        val body = LoginBody(username.value!!, password.value!!)
        val call = authenticationService.login(body)
        call.enqueue(RetrofitCallback(object : RetrofitCallbackListener<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                mutableProgressBarVisible.value = View.GONE
                t?.message?.let { setErrorMessage("Exception: $it") }
            }

            override fun onResponseSuccess(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                val result = response?.body()
                result?.authToken?.let { ServiceGenerator.storeAuthToken(it) }
                mutableProgressBarVisible.value = View.GONE
                //TODO: retrieve user id from Api response
                result?.userInfo?.let { setUserInfo(it) }
            }

            override fun onResponseError(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                mutableProgressBarVisible.value = View.GONE
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { setErrorMessage(it) }
            }
        }))
    }

    fun onClick() {
        if (!isUserInfoValid()) {
            singleIsUserInfoValidEvent.call()
        } else {
            login()
        }
    }


}