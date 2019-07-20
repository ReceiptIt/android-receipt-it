package com.receiptit.receiptProductList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.receiptit.BaseNavigationDrawerActivity
import com.receiptit.R
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.receipt.ReceiptInfo
import com.receiptit.network.model.receipt.ReceiptProductsResponse
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ReceiptApi
import com.receiptit.product.ProductActivity
import retrofit2.Call
import retrofit2.Response

class ReceiptProductListActivity : BaseNavigationDrawerActivity(), ProductListFragment.OnProductListFragmentItemClickListener{

    private val RECEIPT_ID = "RECEIPT_ID"
    private val PRODUCT_ID = "PRODUCT_ID"
    private val ACTIVITY_RESULT_PRODCUT_ACTIVITY = 3
    private val ACTIVITY_RESULT_CREATE_PRODCUT_ACTIVITY = 4
    private lateinit var pagerAdapter: ReceiptProductPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_product_nav_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        super.onCreateDrawer()
        title = getString(R.string.receipt_product_list_activity)

        init()
    }

    override fun onProductListFragmentItemClick(productId: Int) {
        val receiptId = intent.getIntExtra(RECEIPT_ID, 0)
        val intent = Intent(this, ProductActivity::class.java)
        intent.putExtra(PRODUCT_ID, productId)
        intent.putExtra(RECEIPT_ID, receiptId)
        startActivityForResult(intent, ACTIVITY_RESULT_PRODCUT_ACTIVITY)
    }

    override fun onProductListItemCreate(receiptId: Int) {
        val intent = Intent(this, CreateProductActivity::class.java)
        intent.putExtra(RECEIPT_ID, receiptId)
        startActivityForResult(intent, ACTIVITY_RESULT_CREATE_PRODCUT_ACTIVITY)
    }

    private fun showError(error: String){
        Toast.makeText(this, getString(R.string.receipt_product_list_retrieve_error) + error, Toast.LENGTH_SHORT).show()
    }

    private fun init() {
        val receiptId = intent.getIntExtra(RECEIPT_ID, 0)
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call = receiptService.getReceiptProducts(receiptId)
        call.enqueue(RetrofitCallback(object : RetrofitCallbackListener<ReceiptProductsResponse> {
            override fun onResponseSuccess(
                call: Call<ReceiptProductsResponse>?,
                response: Response<ReceiptProductsResponse>?
            ) {
                val result = response?.body()
                val receiptInfo = result?.let {
                    ReceiptInfo(
                        it.receipt_id, it.user_id, it.purchase_date,
                        it.total_amount, it.merchant, it.merchant, it.comment,
                        it.updatedAt, it.createdAt)
                }
                receiptInfo?.let { createFragments(it) }
            }

            override fun onResponseError(
                call: Call<ReceiptProductsResponse>?,
                response: Response<ReceiptProductsResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showError(it) }
            }

            override fun onFailure(call: Call<ReceiptProductsResponse>?, t: Throwable?) {
                t?.message?.let { showError(it) }
            }

        }))
    }

    private fun createFragments(receiptInfo: ReceiptInfo) {
        pagerAdapter = ReceiptProductPageAdapter(this, supportFragmentManager, receiptInfo)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = pagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResultUpdateUserInfo(requestCode, resultCode)
        if (requestCode == ACTIVITY_RESULT_CREATE_PRODCUT_ACTIVITY
            || requestCode == ACTIVITY_RESULT_PRODCUT_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                val fragment = supportFragmentManager.fragments[1] as ProductListFragment
                fragment.refreshProductList()
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
