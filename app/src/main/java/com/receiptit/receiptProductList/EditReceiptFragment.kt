package com.receiptit.receiptProductList

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.Toast

import com.receiptit.R
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.SimpleResponse
import com.receiptit.network.model.receipt.ReceiptInfo
import com.receiptit.network.model.receipt.ReceiptUpdateBody
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ReceiptApi
import com.receiptit.util.TimeStringFormatter
import retrofit2.Call
import retrofit2.Response

private const val RECEIPT_INFO = "RECEIPT_INFO"

class EditReceiptFragment : Fragment() {
    private var receiptInfo: ReceiptInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            receiptInfo = it.getSerializable(RECEIPT_INFO) as ReceiptInfo
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_receipt, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindView()
    }

    private fun bindView() {
        val edReceiptInfoMerchant: EditText? =
            view?.findViewById(R.id.ed_product_name_value)
        val edReceiptInfoPostcode: EditText? =
            view?.findViewById(R.id.ed_product_quantity_value)
        val edReceiptInfoTotalAmount: EditText? =
            view?.findViewById(R.id.ed_product_currency_value)
        val edReceiptInfoPurchasedDate: EditText? =
            view?.findViewById(R.id.ed_product_price_value)
        val edReceiptInfoComment: EditText? =
            view?.findViewById(R.id.ed_product_description_value)

        edReceiptInfoMerchant?.hint = receiptInfo?.merchant
        edReceiptInfoPostcode?.hint = receiptInfo?.postcode
        edReceiptInfoTotalAmount?.hint = receiptInfo?.total_amount.toString()
        edReceiptInfoPurchasedDate?.hint = receiptInfo?.purchase_date?.let { TimeStringFormatter.format(it) }
        edReceiptInfoComment?.hint = receiptInfo?.comment
    }

    private fun unFocusView(editText: EditText?) {
        editText?.isFocused?.let {
            if (it)
                editText.setText("")
                editText.clearFocus()
        }
    }

    private fun setEditTextHint(editText: EditText?) {
        if (editText?.text.toString() != "")
            editText?.hint = editText?.text.toString()
    }

    private fun updateView() {
        val edReceiptInfoMerchant: EditText? =
            view?.findViewById(R.id.ed_product_name_value)
        val edReceiptInfoPostcode: EditText? =
            view?.findViewById(R.id.ed_product_quantity_value)
        val edReceiptInfoTotalAmount: EditText? =
            view?.findViewById(R.id.ed_product_currency_value)
        val edReceiptInfoPurchasedDate: EditText? =
            view?.findViewById(R.id.ed_product_price_value)
        val edReceiptInfoComment: EditText? =
            view?.findViewById(R.id.ed_product_description_value)

        setEditTextHint(edReceiptInfoMerchant)
        setEditTextHint(edReceiptInfoPostcode)
        setEditTextHint(edReceiptInfoTotalAmount)
        setEditTextHint(edReceiptInfoPurchasedDate)
        setEditTextHint(edReceiptInfoComment)

        unFocusView(edReceiptInfoMerchant)
        unFocusView(edReceiptInfoPostcode)
        unFocusView(edReceiptInfoTotalAmount)
        unFocusView(edReceiptInfoPurchasedDate)
        unFocusView(edReceiptInfoComment)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_edit_receipt, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.menu_edit_receipt_save) {
            if (validTotalAmount()) {
                saveReceiptInfo()
            } else {
                showError("Invalid Input")
            }
            true
        } else
            super.onOptionsItemSelected(item)
    }


    private fun validTotalAmount(): Boolean {
        val edReceiptInfoTotalAmount: EditText? = view?.findViewById(R.id.ed_product_currency_value)
        val totalAmount = edReceiptInfoTotalAmount?.text.toString()
        return try {
            totalAmount.toDouble()
            true
        } catch (e: Exception){
            false
        }
    }

    private fun createUpdateUserBody(): ReceiptUpdateBody{
        val body= ReceiptUpdateBody()
        val edReceiptInfoMerchant: EditText? =
            view?.findViewById(R.id.ed_product_name_value)
        val edReceiptInfoPostcode: EditText? =
            view?.findViewById(R.id.ed_product_quantity_value)
        val edReceiptInfoTotalAmount: EditText? =
            view?.findViewById(R.id.ed_product_currency_value)
        val edReceiptInfoPurchasedDate: EditText? =
            view?.findViewById(R.id.ed_product_price_value)
        val edReceiptInfoComment: EditText? =
            view?.findViewById(R.id.ed_product_description_value)

        val merchant = edReceiptInfoMerchant?.text.toString()
        val postcode = edReceiptInfoPostcode?.text.toString()
        val totalAmount = edReceiptInfoTotalAmount?.text.toString()
        val purchaseDate = edReceiptInfoPurchasedDate?.text.toString()
        val comment = edReceiptInfoComment?.text.toString()

        if (merchant != "")
            body.merchant = merchant

        if (postcode != "")
            body.postcode = postcode

        if (totalAmount != "")
            body.total_amount = totalAmount.toDouble()

        if (purchaseDate != "")
            body.purchase_date = purchaseDate

        if (comment != "")
            body.comment = comment

        return body
    }

    private fun saveReceiptInfo() {
        val body = createUpdateUserBody()
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call = receiptInfo?.receipt_id?.let { receiptService.updateReceipt(it, body) }
        call?.enqueue(RetrofitCallback(object: RetrofitCallbackListener<SimpleResponse>{
            override fun onResponseSuccess(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                updateView()
            }

            override fun onResponseError(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showError(it) }
            }

            override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                t?.message?.let { showError(it) }
            }

        }))
    }

    private fun showError(error: String){
        Toast.makeText(context, getString(R.string.receipt_product_list_receipt_edit_error) + error, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance(receiptInfo: ReceiptInfo) =
            EditReceiptFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(RECEIPT_INFO, receiptInfo)
                }
            }
    }
}
