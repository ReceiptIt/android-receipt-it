package com.receiptit.receiptProductList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.receiptit.R
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.squareup.picasso.Picasso
import java.net.URL


private const val RECEIPT_IMAGE_URL = "RECEIPT_IMAGE_URL"

class ReceiptImageFragment : Fragment() {

    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUrl = arguments?.getString(RECEIPT_IMAGE_URL)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_receipt_image, container, false)
        val image: ImageView = root.findViewById(R.id.iv_receipt_product_list_receipt_image)
        Picasso.get().load(imageUrl).into(image)
        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(imageUrl: String): ReceiptImageFragment {
            return ReceiptImageFragment().apply {
                arguments = Bundle().apply {
                    putString(RECEIPT_IMAGE_URL, imageUrl)
                }
            }
        }
    }
}