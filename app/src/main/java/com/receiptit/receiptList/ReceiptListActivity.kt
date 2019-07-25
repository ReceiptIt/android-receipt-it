package com.receiptit.receiptList

import android.app.Activity
import android.app.ProgressDialog
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
import com.receiptit.network.model.user.UserInfo
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.receiptProductList.ReceiptProductListActivity

import kotlinx.android.synthetic.main.content_receipt_list.*
import retrofit2.Call
import retrofit2.Response
import androidx.appcompat.app.AlertDialog
import com.receiptit.network.model.SimpleResponse
import kotlinx.android.synthetic.main.activity_receipt_list.*
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.content.FileProvider
import com.receiptit.network.model.imageProcessor.ImageProcessResponse
import com.receiptit.network.model.product.ProductBatchCreateBody
import com.receiptit.network.model.product.ProductBatchCreateResponse
import com.receiptit.network.model.product.ProductBatchInfo
import com.receiptit.network.model.receipt.*
import com.receiptit.network.model.tabScanner.TabScannerGetReceiptProcessedResponse
import com.receiptit.network.model.tabScanner.TabScannerUploadReceiptImageResponse
import com.receiptit.network.service.*
import com.receiptit.util.TimeStringFormatter
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReceiptListActivity : ReceiptListRecyclerViewAdapter.OnReceiptListItemClickListener,
    BaseNavigationDrawerActivity(), AddReceiptFragment.OnAddReceiptFragmentCloseListener {

    private val USER_INFO = "USER_INFO"
    private val RECEIPT_ID = "RECEIPT_ID"
    private val RECEIPT_URL = "RECEIPT_URL"
    private var userInfo: UserInfo? = null
    private var isFragmentShow = false

    private var fragment: AddReceiptFragment? = null

    private var ACTIVITY_RESULT_MANUALLY_CREATE_RECEIPT_ACTIVITY = 2
    private var ACTIVITY_RESULT_RECEIPT_PRODUCT_ACTIVITY = 3
    private var ACTIVITY_RESULT_CAMERA = 4
    private lateinit var currentPhotoPath: String

    private lateinit var dialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_list_nav_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        super.onCreateDrawer()
        init()
        dialog = ProgressDialog(this)
        hideProgressBar()
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
        if (list.isNotEmpty()) {
            empty_view.visibility = View.GONE
        } else {
            empty_view.visibility = View.VISIBLE
        }
        hideProgressBar()
    }

    private fun showGetReceiptListError(error: String) {
        Toast.makeText(this, getString(R.string.receipt_list_retrieve_error) + error, Toast.LENGTH_LONG).show()
    }

    private fun showDeleteReceiptListError(error: String) {
        Toast.makeText(this, getString(R.string.receipt_list_delete_receipt_error) + error, Toast.LENGTH_LONG).show()
    }

    private fun showCreateReceiptError(error: String) {
        Toast.makeText(this, getString(R.string.receipt_list_add_receipt_manually_error) + error, Toast.LENGTH_LONG)
            .show()
    }

    private fun showSendReceiptImageError(error: String) {
        Toast.makeText(this, getString(R.string.receipt_list_scan_receipt_error) + error, Toast.LENGTH_LONG).show()
    }

    private fun showImageProcessError(error: String) {
        Toast.makeText(this, getString(R.string.receipt_list_image_process_error) + error, Toast.LENGTH_LONG).show()
    }

    private fun showProductBatchError(error: String) {
        Toast.makeText(this, getString(R.string.receipt_list_product_batch_error) + error, Toast.LENGTH_LONG).show()
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
                hideProgressBar()
            }

            override fun onFailure(call: Call<UserReceiptsRetrieveResponse>?, t: Throwable?) {
                t?.message?.let { showGetReceiptListError(it) }
                hideProgressBar()
            }

        }))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResultUpdateUserInfo(requestCode, resultCode)
        if (requestCode == ACTIVITY_RESULT_MANUALLY_CREATE_RECEIPT_ACTIVITY ||
            requestCode == ACTIVITY_RESULT_RECEIPT_PRODUCT_ACTIVITY
        ) {
            if (resultCode == Activity.RESULT_OK) {
                refreshReceiptList()
            }
        } else if (requestCode == ACTIVITY_RESULT_CAMERA) {
            if (File(currentPhotoPath).length() != 0L) {
                showProgressBar()
                createFakeReceipt()
            }
        }
    }

    //need to manually create a receipt at this time
    private fun createFakeReceipt() {
        // create a fake receipt
        val receiptBody = createReceiptCreateBody()
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call = receiptBody?.let { receiptService.createReceipt(it) }
        call?.enqueue(RetrofitCallback(object : RetrofitCallbackListener<ReceiptCreateResponse> {
            override fun onResponseSuccess(
                call: Call<ReceiptCreateResponse>?,
                response: Response<ReceiptCreateResponse>?
            ) {
                // upload image
                val receiptId = response?.body()?.receiptInfo?.receipt_id
                receiptId?.let { sendReceiptImageToTabScanner(it)}
            }

            override fun onResponseError(
                call: Call<ReceiptCreateResponse>?,
                response: Response<ReceiptCreateResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showCreateReceiptError(it) }
                hideProgressBar()
            }

            override fun onFailure(call: Call<ReceiptCreateResponse>?, t: Throwable?) {
                t?.message?.let { showDeleteReceiptListError(it) }
                hideProgressBar()
            }

        }))

    }

//    private fun sendReceiptImage(receiptId: Int) {
//        val imageService = ServiceGenerator.createService(ImageApi::class.java)
//        val imageFile = File(currentPhotoPath)
//        val fileReqBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
//        val part = MultipartBody.Part.createFormData("image", imageFile.name, fileReqBody)
//
//        val call = imageService.createReceiptImage(part, receiptId)
//        call.enqueue(RetrofitCallback(object : RetrofitCallbackListener<ReceiptImageCreateResponse> {
//            override fun onResponseSuccess(
//                call: Call<ReceiptImageCreateResponse>?,
//                response: Response<ReceiptImageCreateResponse>?
//            ) {
//                response?.body()?.imageInfo?.let { receiptImageProcess(it, receiptId) }
//            }
//
//            override fun onResponseError(
//                call: Call<ReceiptImageCreateResponse>?,
//                response: Response<ReceiptImageCreateResponse>?
//            ) {
//                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
//                message?.getErrorMessage()?.let { showSendReceiptImageError(it) }
//                hideProgressBar()
//            }
//
//            override fun onFailure(call: Call<ReceiptImageCreateResponse>?, t: Throwable?) {
//                t?.message?.let { showSendReceiptImageError(it) }
//                hideProgressBar()
//            }
//        }))
//    }

    private fun sendReceiptImageToTabScanner(receiptId: Int) {
        val tabScannerService = ServiceGenerator.createTabScannerService(TabScannerApi::class.java)
        val imageFile = File(currentPhotoPath)
        val fileReqBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
        val part = MultipartBody.Part.createFormData("receiptImage", imageFile.name, fileReqBody)
        val call = tabScannerService.uploadReceiptImage(part)
        call.enqueue(RetrofitCallback(object : RetrofitCallbackListener<TabScannerUploadReceiptImageResponse>{
            override fun onResponseSuccess(
                call: Call<TabScannerUploadReceiptImageResponse>?,
                response: Response<TabScannerUploadReceiptImageResponse>?
            ) {
                response?.body()?.token?.let { getProcessedReceiptImage(it, receiptId) }
            }

            override fun onResponseError(
                call: Call<TabScannerUploadReceiptImageResponse>?,
                response: Response<TabScannerUploadReceiptImageResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showSendReceiptImageError(it) }
                hideProgressBar()
            }

            override fun onFailure(call: Call<TabScannerUploadReceiptImageResponse>?, t: Throwable?) {
                t?.message?.let { showSendReceiptImageError(it) }
                hideProgressBar()
            }

        }))
    }

//    private fun receiptImageProcess(imageInfo: ReceiptImageInfo, receiptId: Int) {
//        val imageProcessService = ServiceGenerator.createImageProcessorService(ImageProcessApi::class.java)
//        val call = imageProcessService.processImage(imageInfo.url)
//        call.enqueue(RetrofitCallback(object: RetrofitCallbackListener<ImageProcessResponse> {
//            override fun onResponseSuccess(
//                call: Call<ImageProcessResponse>?,
//                response: Response<ImageProcessResponse>?
//            ) {
//                response?.body()?.let { updateReceiptInfo(it, receiptId) }
//            }
//
//            override fun onResponseError(call: Call<ImageProcessResponse>?, response: Response<ImageProcessResponse>?) {
//                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
//                message?.getErrorMessage()?.let { showImageProcessError(it) }
//                hideProgressBar()
//            }
//
//            override fun onFailure(call: Call<ImageProcessResponse>?, t: Throwable?) {
//                t?.message?.let { showImageProcessError(it) }
//                hideProgressBar()
//            }
//
//        }))
//    }

    private fun getProcessedReceiptImage(token: String, receiptId: Int) {
        val tabScannerService = ServiceGenerator.createTabScannerService(TabScannerApi::class.java)
        val call = tabScannerService.getReceiptImageProcessed(token)
        call.enqueue(RetrofitCallback(object : RetrofitCallbackListener<TabScannerGetReceiptProcessedResponse>{
            override fun onResponseSuccess(
                call: Call<TabScannerGetReceiptProcessedResponse>?,
                response: Response<TabScannerGetReceiptProcessedResponse>?
            ) {
                if (response?.body()?.status == "pending")
                    getProcessedReceiptImage(token, receiptId)
                else
                    response?.body()?.let { updateReceiptInfo(it, receiptId) }
            }

            override fun onResponseError(
                call: Call<TabScannerGetReceiptProcessedResponse>?,
                response: Response<TabScannerGetReceiptProcessedResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showImageProcessError(it) }
                hideProgressBar()

            }

            override fun onFailure(call: Call<TabScannerGetReceiptProcessedResponse>?, t: Throwable?) {
                t?.message?.let { showImageProcessError(it) }
                hideProgressBar()
            }

        }))
    }

    private fun updateReceiptInfo(imageResponse: TabScannerGetReceiptProcessedResponse, receiptId: Int) {
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val updateBody = createReceiptUpdateBody(imageResponse)
        val call = receiptService.updateReceipt(receiptId, updateBody)
        call.enqueue(RetrofitCallback(object : RetrofitCallbackListener<SimpleResponse> {
            override fun onResponseSuccess(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                createBatchProduct(imageResponse, receiptId)
            }

            override fun onResponseError(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showImageProcessError(it) }
                hideProgressBar()
            }

            override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                t?.message?.let { showImageProcessError(it) }
                hideProgressBar()
            }
        }))
    }

    private fun createBatchProduct(imageResponse: TabScannerGetReceiptProcessedResponse, receiptId: Int) {
        val productService = ServiceGenerator.createService(ProductApi::class.java)
        val body = createBatchProductBody(imageResponse, receiptId)
        val call = productService.createBatchProduct(body)
        call.enqueue(RetrofitCallback(object: RetrofitCallbackListener<ProductBatchCreateResponse> {
            override fun onResponseSuccess(
                call: Call<ProductBatchCreateResponse>?,
                response: Response<ProductBatchCreateResponse>?
            ) {
                refreshReceiptList()
            }

            override fun onResponseError(
                call: Call<ProductBatchCreateResponse>?,
                response: Response<ProductBatchCreateResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showProductBatchError(it) }
                hideProgressBar()
            }

            override fun onFailure(call: Call<ProductBatchCreateResponse>?, t: Throwable?) {
                t?.message?.let { showProductBatchError(it) }
                hideProgressBar()
            }
        }))

    }

    private fun createReceiptCreateBody(): ReceiptCreateBody? {
        userInfo = intent.getSerializableExtra(USER_INFO) as UserInfo
        val userId = userInfo?.user_id
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val prePurchaseDate = sdf.format(calendar.time)
        val purchaseDate = TimeStringFormatter.concatenate(prePurchaseDate)
        return userId?.let {
            ReceiptCreateBody(
                it, purchaseDate, 123.00, "Mie Mie",
                "1J2 3U4"
            )
        }
    }

    private fun createReceiptUpdateBody(imageResponse: TabScannerGetReceiptProcessedResponse): ReceiptUpdateBody {
        val totalAmount = imageResponse.result.total.toDouble()
        val merchant = imageResponse.result.establishment
        val postcode = imageResponse.result.addressNorm.postcode
        return ReceiptUpdateBody(total_amount = totalAmount, merchant = merchant, postcode = postcode)
    }

    private fun createBatchProductBody(imageResponse: TabScannerGetReceiptProcessedResponse, receiptId: Int): ProductBatchCreateBody {
        val productBatchList = ArrayList<ProductBatchInfo>()
        imageResponse.result.lineItems.forEach {
            val productBatchInfo = ProductBatchInfo(receipt_id = receiptId, name = defaultProductName(it.descClean),
                description = null, quantity = defaultQty(it.qty), currency_code = "CAD", price = it.lineTotal)
            productBatchList.add(productBatchInfo)
        }
        return ProductBatchCreateBody(productBatchList)
    }

    private fun defaultQty(qty: Int): Int {
        return if (qty == 0)
            1
        else
            qty
    }

    private fun defaultProductName(name: String?):String{
        return if (name == null || name == "" )
            "Missing product name"
        else
            name
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
        fragment = userInfo?.let { AddReceiptFragment.newInstance(it) }
        val fm = supportFragmentManager
        val fragmentTransaction = fm.beginTransaction()
        fragment?.let { fragmentTransaction.replace(R.id.fg_receipt_list_add_receipt, it) }
        fragmentTransaction.commit()
        isFragmentShow = true
    }

    override fun onAddReceiptFragmentClose() {
        if (isFragmentShow) {
            fragment?.let { supportFragmentManager.beginTransaction().remove(it) }?.commit()
            isFragmentShow = false
            supportActionBar?.show()
        }
    }

    override fun onBackPressed() {
        if (isFragmentShow) {
            fragment?.let { supportFragmentManager.beginTransaction().remove(it) }?.commit()
            isFragmentShow = false
            supportActionBar?.show()
        } else {
            super.onBackPressed()
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
        call.enqueue(RetrofitCallback(object : RetrofitCallbackListener<SimpleResponse> {
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

    private fun hideProgressBar() {
        if (dialog.isShowing)
            dialog.hide()
    }

    private fun showProgressBar() {
        dialog.setMessage("Loading ...")
        dialog.show()
    }

}
