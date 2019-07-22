package com.receiptit.receiptProductList

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.receiptit.R
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.SimpleResponse
import com.receiptit.network.model.product.ProductFromReceiptResponse
import com.receiptit.network.model.product.ProductInfo
import com.receiptit.network.model.receipt.ReceiptProductsResponse
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ProductApi
import com.receiptit.network.service.ReceiptApi
import kotlinx.android.synthetic.main.fragment_product_list.*
import retrofit2.Call
import retrofit2.Response

private const val RECEIPT_ID = "RECEIPT_ID"

class ProductListFragment : Fragment(),
    ReceiptProductListRecyclerViewAdapter.OnReceiptProductListItemClickListener {

    private var receiptId: Int? = null
    private var listener: OnProductListFragmentItemClickListener? = null
    private var recyclerView: RecyclerView? = null
    private var mView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            receiptId = it.getInt(RECEIPT_ID)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_product_list, container, false)
//        mView = view
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        refreshProductList()
    }

    fun refreshProductList() {
        val productService = ServiceGenerator.createService(ProductApi::class.java)
        val call = receiptId?.let { productService.getProductsFromReceipt(it) }
        call?.enqueue(RetrofitCallback(object : RetrofitCallbackListener<ProductFromReceiptResponse> {
            override fun onResponseSuccess(
                call: Call<ProductFromReceiptResponse>?,
                response: Response<ProductFromReceiptResponse>?
            ) {
                val list = response?.body()?.products
                list?.let { createList(it) }
            }

            override fun onResponseError(
                call: Call<ProductFromReceiptResponse>?,
                response: Response<ProductFromReceiptResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showCreateProductError(it) }
            }

            override fun onFailure(call: Call<ProductFromReceiptResponse>?, t: Throwable?) {
                t?.message?.let { showCreateProductError(it) }
            }

        }))
    }

    private fun showCreateProductError(error: String) {
        Toast.makeText(context, getString(R.string.receipt_product_list_retrieve_error) + error, Toast.LENGTH_SHORT)
            .show()
    }

    private fun showDeleteProductError(error: String) {
        Toast.makeText(
            context,
            getString(R.string.receipt_product_list_product_delete_error) + error,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun createList(products: ArrayList<ProductInfo>) {
        recyclerView = view?.findViewById(R.id.rv_receipt_product_list)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        val adapter = ReceiptProductListRecyclerViewAdapter(products, this)
        recyclerView?.adapter = adapter
        if (products.isNotEmpty()) {
            empty_view.visibility = View.GONE
        } else {
            empty_view.visibility = View.VISIBLE
        }
    }

    override fun onReceiptProductListItemClick(productId: Int) {
        listener?.onProductListFragmentItemClick(productId)
    }

    override fun onReceiptProductListItemLongClick(productId: Int) {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.receipt_product_list_delete_product_dialog_title))
                .setMessage(getString(R.string.receipt_product_list_delete_product_dialog_message))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    deleteProduct(productId)
                }

                .setNegativeButton(android.R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_add_product, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.menu_add_product_add) {
            receiptId?.let { listener?.onProductListItemCreate(it) }
            true
        } else
            super.onOptionsItemSelected(item)
    }


    private fun deleteProduct(productId: Int) {
        val productService = ServiceGenerator.createService(ProductApi::class.java)
        val call = productService.deleteProduct(productId)
        call.enqueue(RetrofitCallback(object : RetrofitCallbackListener<SimpleResponse> {
            override fun onResponseSuccess(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                refreshProductList()
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnProductListFragmentItemClickListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        mView = null
    }


    interface OnProductListFragmentItemClickListener {
        fun onProductListFragmentItemClick(productId: Int)
        fun onProductListItemCreate(receiptId: Int)
    }

    companion object {
        @JvmStatic
        fun newInstance(receiptId: Int) =
            ProductListFragment().apply {
                arguments?.clear()
                arguments = Bundle().apply {
                    putInt(RECEIPT_ID, receiptId)
                }
            }
    }
}
