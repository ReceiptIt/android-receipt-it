package com.receiptit.receiptList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.receiptit.BaseNavigationDrawerActivity
import com.receiptit.R
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.receipt.ReceiptInfo
import com.receiptit.network.model.receipt.UserReceiptsRetrieveResponse
import com.receiptit.network.model.user.UserInfo
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ReceiptApi
import com.receiptit.receiptProductList.ReceiptProductListActivity

import kotlinx.android.synthetic.main.content_receipt_list.*
import retrofit2.Call
import retrofit2.Response
import androidx.appcompat.app.AlertDialog
import com.receiptit.network.model.SimpleResponse
import kotlinx.android.synthetic.main.activity_receipt_list.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.receiptit.network.model.image.ReceiptImageCreaetResponse
import com.receiptit.network.model.receipt.ReceiptCreateBody
import com.receiptit.network.model.receipt.ReceiptCreateResponse
import com.receiptit.network.service.ImageApi
import com.receiptit.util.TimeStringFormatter
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ReceiptListActivity : ReceiptListRecyclerViewAdapter.OnReceiptListItemClickListener,
    BaseNavigationDrawerActivity(), AddReceiptFragment.OnAddReceiptFragmentCloseListener{

    private val USER_INFO = "USER_INFO"
    private val RECEIPT_ID = "RECEIPT_ID"
    private val RECEIPT_URL = "RECEIPT_URL"
    private var userInfo : UserInfo? = null
    private var isFragmentShow = false

    private var fragment: AddReceiptFragment? = null

    private var ACTIVITY_RESULT_MANUALLY_CREATE_RECEIPT_ACTIVITY = 2
    private var ACTIVITY_RESULT_RECEIPT_PRODUCT_ACTIVITY = 3
    private var ACTIVITY_RESULT_CAMERA = 4
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_list_nav_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        super.onCreateDrawer()
        init()
        refreshReceiptList()
    }

    private fun init() {
        fab.setOnClickListener {
            takePhoto()
        }
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.receiptit",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, ACTIVITY_RESULT_CAMERA)
                }
            }
        }
    }

    override fun onReceiptListItemClick(receiptId: Int, imageUrl: String?) {
        val intent = Intent(this, ReceiptProductListActivity::class.java)
        intent.putExtra(RECEIPT_ID, receiptId)
        intent.putExtra(RECEIPT_URL, imageUrl)
        intent.putExtra(USER_INFO, userInfo)
        startActivityForResult(intent, ACTIVITY_RESULT_RECEIPT_PRODUCT_ACTIVITY)
    }

    private fun createList(list: List<ReceiptInfo>) {
        val recyclerView = rv_receipt_list
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ReceiptListRecyclerViewAdapter(list, this)
        recyclerView.adapter = adapter
    }

    private fun showGetReceiptListError(error: String){
        Toast.makeText(this, getString(R.string.receipt_list_retrieve_error) + error, Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteReceiptListError(error: String){
        Toast.makeText(this, getString(R.string.receipt_list_delete_receipt_error) + error, Toast.LENGTH_SHORT).show()
    }

    private fun showCreateReceiptError(error: String) {
        Toast.makeText(this, getString(R.string.receipt_list_add_receipt_manually_error) + error, Toast.LENGTH_SHORT).show()
    }

    private fun showSendReceiptImageError(error: String) {
        Toast.makeText(this, getString(R.string.receipt_list_scan_receipt_error) + error, Toast.LENGTH_SHORT).show()

    }

    private fun refreshReceiptList() {
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
            }

            override fun onResponseError(
                call: Call<UserReceiptsRetrieveResponse>?,
                response: Response<UserReceiptsRetrieveResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showGetReceiptListError(it) }
            }

            override fun onFailure(call: Call<UserReceiptsRetrieveResponse>?, t: Throwable?) {
                t?.message?.let { showGetReceiptListError(it) }
            }

        }))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResultUpdateUserInfo(requestCode, resultCode)
        if (requestCode == ACTIVITY_RESULT_MANUALLY_CREATE_RECEIPT_ACTIVITY ||
            requestCode == ACTIVITY_RESULT_RECEIPT_PRODUCT_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                refreshReceiptList()
            }
        } else if (requestCode == ACTIVITY_RESULT_CAMERA ){
            scanFakeReceipt()
        }
    }

    //need to manually create a receipt at this time
    private fun scanFakeReceipt() {
        // create a fake receipt
        val receiptBody = createBody()
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call = receiptBody?.let { receiptService.createReceipt(it) }
        call?.enqueue(RetrofitCallback(object: RetrofitCallbackListener<ReceiptCreateResponse>{
            override fun onResponseSuccess(
                call: Call<ReceiptCreateResponse>?,
                response: Response<ReceiptCreateResponse>?
            ) {
                // upload image
                val receiptId = response?.body()?.receiptInfo?.receipt_id
                receiptId?.let { sendReceiptImage(it) }
            }

            override fun onResponseError(
                call: Call<ReceiptCreateResponse>?,
                response: Response<ReceiptCreateResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showCreateReceiptError(it) }
            }

            override fun onFailure(call: Call<ReceiptCreateResponse>?, t: Throwable?) {
                t?.message?.let { showDeleteReceiptListError(it) }
            }

        }))
        
    }

    private fun sendReceiptImage(receiptId: Int) {
        val imageService = ServiceGenerator.createService(ImageApi::class.java)
        val imageFile = File(currentPhotoPath)
        val fileReqBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
        val part = MultipartBody.Part.createFormData("image", imageFile.name, fileReqBody)

        val call = imageService.createReceiptImage(part, receiptId)
        call.enqueue(RetrofitCallback(object: RetrofitCallbackListener<ReceiptImageCreaetResponse>{
            override fun onResponseSuccess(
                call: Call<ReceiptImageCreaetResponse>?,
                response: Response<ReceiptImageCreaetResponse>?
            ) {
                refreshReceiptList()
            }

            override fun onResponseError(
                call: Call<ReceiptImageCreaetResponse>?,
                response: Response<ReceiptImageCreaetResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showSendReceiptImageError(it) }
            }

            override fun onFailure(call: Call<ReceiptImageCreaetResponse>?, t: Throwable?) {
                t?.message?.let { showSendReceiptImageError(it) }
            }
        }))
    }
    
    private fun createBody(): ReceiptCreateBody? {
        userInfo = intent.getSerializableExtra(USER_INFO) as UserInfo
        val userId = userInfo?.user_id
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val prePurchaseDate = sdf.format(calendar.time)
        val purchaseDate = TimeStringFormatter.concatenate(prePurchaseDate)
        return userId?.let {
            ReceiptCreateBody(
                it, purchaseDate, 123.00, "Mie Mie",
                "1J2 3U4")
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_receipt_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_receipt_list_add_receipt) {
            showFragment()
            true
        } else
            super.onOptionsItemSelected(item)
    }

    private fun showFragment() {
        supportActionBar?.hide()
        fragment = userInfo?.let { AddReceiptFragment.newInstance(it)}
        val fm = supportFragmentManager
        val fragmentTransaction = fm.beginTransaction()
        fragment?.let { fragmentTransaction.replace(R.id.fg_receipt_list_add_receipt, it)}
        fragmentTransaction.commit()
        isFragmentShow = true
    }

    override fun onAddReceiptFragmentClose() {
       if (isFragmentShow) {
           fragment?.let { supportFragmentManager.beginTransaction().remove(it)}?.commit()
           isFragmentShow = false
           supportActionBar?.show()
       }
    }

    override fun onAddReceiptManually() {
        val intent = Intent(this, ManuallyCreateActivity::class.java)
        intent.putExtra(USER_INFO, userInfo)
        startActivityForResult(intent, ACTIVITY_RESULT_MANUALLY_CREATE_RECEIPT_ACTIVITY)
    }

    override fun onAddReceiptScan() {
        takePhoto()
    }

    override fun onReceiptListItemLongClick(receiptId: Int) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.receipt_list_delete_receipt_dialog_title))
            .setMessage(getString(R.string.receipt_list_delete_receipt_dialog_message))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                deleteReceipt(receiptId)
            }

            .setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun deleteReceipt(receiptId: Int) {
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call = receiptService.deleteReceipt(receiptId)
        call.enqueue(RetrofitCallback(object : RetrofitCallbackListener<SimpleResponse>{
            override fun onResponseSuccess(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                refreshReceiptList()
            }

            override fun onResponseError(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showDeleteReceiptListError(it) }
            }

            override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                t?.message?.let { showDeleteReceiptListError(it) }
            }

        }))
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

}
