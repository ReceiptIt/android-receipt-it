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
    private var ACTIVITY_RESULT_CAMERA = 5
    private lateinit var pagerAdapter: ReceiptProductPageAdapter
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_product_nav_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        super.onCreateDrawer()
//        fab.setOnClickListener {
//            takePhoto()
//        }
        init()
    }

//    private fun takePhoto() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            // Ensure that there's a camera activity to handle the intent
//            takePictureIntent.resolveActivity(packageManager)?.also {
//                // Create the File where the photo should go
//                val photoFile: File? = try {
//                    createImageFile()
//                } catch (ex: IOException) {
//                    // Error occurred while creating the File
//                    null
//                }
//                // Continue only if the File was successfully created
//                photoFile?.also {
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                        this,
//                        "com.receiptit",
//                        it
//                    )
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, ACTIVITY_RESULT_CAMERA)
//                }
//            }
//        }
//    }

//    @Throws(IOException::class)
//    private fun createImageFile(): File {
//        // Create an image file name
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            "JPEG_${timeStamp}_", /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */
//        ).apply {
//            // Save a file: path for use with ACTION_VIEW intents
//            currentPhotoPath = absolutePath
//        }
//    }

//    private fun sendReceiptImage(receiptId: Int) {
//        val imageService = ServiceGenerator.createService(ImageApi::class.java)
//        val imageFile = File(currentPhotoPath)
//        val fileReqBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
//        val part = MultipartBody.Part.createFormData("image", imageFile.name, fileReqBody)
//
//        val call = imageService.createReceiptImage(part, receiptId)
//        call.enqueue(RetrofitCallback(object: RetrofitCallbackListener<ReceiptImageCreateResponse>{
//            override fun onResponseSuccess(
//                call: Call<ReceiptImageCreateResponse>?,
//                response: Response<ReceiptImageCreateResponse>?
//            ) {
//                val last = supportFragmentManager.fragments.size - 1
//                val fragment = supportFragmentManager.fragments[last] as ReceiptImageFragment
//                fragment.refreshReceiptImage()
////                init()
//
//            }
//
//            override fun onResponseError(
//                call: Call<ReceiptImageCreateResponse>?,
//                response: Response<ReceiptImageCreateResponse>?
//            ) {
//                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
//                message?.getErrorMessage()?.let { showSendReceiptImageError(it) }
//            }
//
//            override fun onFailure(call: Call<ReceiptImageCreateResponse>?, t: Throwable?) {
//                t?.message?.let { showSendReceiptImageError(it) }
//            }
//        }))
//    }

//    private fun showSendReceiptImageError(error: String) {
//        Toast.makeText(this, getString(R.string.receipt_list_scan_receipt_error) + error, Toast.LENGTH_SHORT).show()
//    }

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
                        it.total_amount, it.merchant, it.postcode, it.comment,
                        it.image_name, it.image_url, it.updatedAt, it.createdAt)
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
//                init()
            }
        }
//        } else if (requestCode == ACTIVITY_RESULT_CAMERA ){
//            val receiptId = intent.getIntExtra(RECEIPT_ID, 0)
//            sendReceiptImage(receiptId)
//        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}
