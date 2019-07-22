package com.receiptit.receiptProductList

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.receiptit.R
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.SimpleResponse
import com.receiptit.network.model.image.ReceiptImageRetrieveResponse
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ImageApi
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_receipt_image.*
import retrofit2.Call
import retrofit2.Response


private const val RECEIPT_IMAGE_URL = "RECEIPT_IMAGE_URL"
private const val RECEIPT_ID = "RECEIPT_ID"

class ReceiptImageFragment : Fragment() {

    private var imageUrl: String? = null
    private var receiptId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUrl = arguments?.getString(RECEIPT_IMAGE_URL)
        receiptId = arguments?.getInt(RECEIPT_ID)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_receipt_image, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        refreshReceiptImage()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_receipt_image, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.menu_receipt_image_delete) {
            if (imageUrl != null)
                deleteReceiptImage()
            true
        } else
            super.onOptionsItemSelected(item)
    }

    private fun deleteReceiptImage() {
        val imageService = ServiceGenerator.createService(ImageApi::class.java)
        val call = receiptId?.let { imageService.deleteReceiptImage(it) }
        call?.enqueue(RetrofitCallback(object: RetrofitCallbackListener<SimpleResponse>{
            override fun onResponseSuccess(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                resetReceiptImage()
                imageUrl = null
            }

            override fun onResponseError(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showDeleteProductError(it) }
            }

            override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                t?.message?.let { showDeleteProductError(it) }
            }

        }))
    }

     fun refreshReceiptImage() {
        val imageService = ServiceGenerator.createService(ImageApi::class.java)
        val call = receiptId?.let { imageService.getReceiptImage(it) }
        call?.enqueue(RetrofitCallback(object: RetrofitCallbackListener<ReceiptImageRetrieveResponse>{
            override fun onResponseSuccess(call: Call<ReceiptImageRetrieveResponse>?, response: Response<ReceiptImageRetrieveResponse>?) {
                imageUrl = response?.body()?.image_url
                loadReceiptImage()
            }

            override fun onResponseError(call: Call<ReceiptImageRetrieveResponse>?, response: Response<ReceiptImageRetrieveResponse>?) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showDeleteProductError(it) }
            }

            override fun onFailure(call: Call<ReceiptImageRetrieveResponse>?, t: Throwable?) {
                t?.message?.let { showDeleteProductError(it) }
            }
        }))
    }

    private fun showDeleteProductError(error: String) {
        Toast.makeText(context, getString(R.string.receipt_product_list_delete_receipt_error) + error, Toast.LENGTH_SHORT).show()
    }

    private fun resetReceiptImage() {
        val receiptImageView: ImageView? = view?.findViewById(R.id.iv_receipt_product_list_receipt_image)
        receiptImageView?.setImageResource(android.R.color.transparent)
    }

    private fun loadReceiptImage() {
        val receiptImageView: ImageView? = view?.findViewById(R.id.iv_receipt_product_list_receipt_image)
        if (imageUrl != null) {
            empty_view.visibility = View.GONE
            Picasso.get().load(imageUrl).into(receiptImageView)
        } else {
            empty_view.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(imageUrl: String?, receiptId: Int): ReceiptImageFragment {
            return ReceiptImageFragment().apply {
                arguments = Bundle().apply {
                    putString(RECEIPT_IMAGE_URL, imageUrl)
                    putInt(RECEIPT_ID, receiptId)
                }
            }
        }
    }
}