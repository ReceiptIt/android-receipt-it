package com.receiptit.receiptProductList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.receiptit.R
import com.receiptit.network.model.product.ProductInfo
import kotlin.collections.ArrayList

class ReceiptProductListRecyclerViewAdapter(
    var list: ArrayList<ProductInfo>,
    private var mOnReceiptListItemClickListener: OnReceiptProductListItemClickListener
) : RecyclerView.Adapter<ReceiptProductListRecyclerViewAdapter.ReceiptProductListProductInfoViewHolder>() {

//    private var list: ArrayList<ReceiptProductListItem>
//    private val TYPE_RECEIPT_INFO = 0
//    private val TYPE_PRODUCT_INFO = 1

//    init {
//        list = processProducts(product)
//    }

    interface OnReceiptProductListItemClickListener {
        fun onReceiptProductListItemClick(productId: Int)
        fun onReceiptProductListItemLongClick(productId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptProductListProductInfoViewHolder {
//        val view: View
//        return if (viewType == TYPE_RECEIPT_INFO) {
//            view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.receipt_product_list_receipt_info_item, parent, false)
//            ReceiptProductListReceiptInfoViewHolder(view)
//        } else {
//            view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.fragment_receipt_product, parent, false)
//            ReceiptProductListProductInfoViewHolder(view, mOnReceiptListItemClickListener)
//        }
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_receipt_product, parent, false)
        return ReceiptProductListProductInfoViewHolder(view, mOnReceiptListItemClickListener)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ReceiptProductListProductInfoViewHolder, position: Int) {
        val info: ProductInfo = list[position]
        holder.bind(info)
    }

//    override fun getItemViewType(position: Int): Int {
//        return if (list[position].viewType == ReceiptProductListViewType.RECEIPT_INFO)
//            TYPE_RECEIPT_INFO
//        else
//            TYPE_PRODUCT_INFO
//    }


//    enum class ReceiptProductListViewType {
//        RECEIPT_INFO,
//        PRODUCT_INFO
//    }
//
//    abstract class ReceiptProductListItem(var viewType: ReceiptProductListViewType)
//
//    class ReceiptProductListReceiptInfo(
//        var merchant: String, var postcode: String, var purchaseDate: String,
//        var totalAmount: Double, var currency: String, var comment: String?
//    ) :
//        ReceiptProductListItem(ReceiptProductListViewType.RECEIPT_INFO)
//
//    class ReceiptProductListProductInfo(
//        var productId: Int, var productName: String, var quantity: Int, var price: Double,
//        var currency: Currency, var description: String?
//    ) :
//        ReceiptProductListItem(ReceiptProductListViewType.PRODUCT_INFO)

//    private fun processProducts(product: ReceiptProductsResponse): ArrayList<ReceiptProductListItem> {
//        val list = ArrayList<ReceiptProductListItem>()
//        val currencyCode = StringResourceManager.getDefaultCurrencyCode()
//        list.add(
//            ReceiptProductListReceiptInfo(
//                product.merchant, product.postcode, product.purchase_date, product.total_amount,
//                currencyCode, product.comment
//            )
//        )
//
//        product.products.forEach {
//            list.add(ReceiptProductListProductInfo(it.product_id, it.name, it.quantity, it.price, it.currency_code,
//                it.description))
//        }
//        return list
//    }

//    abstract class ReceiptProductListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        abstract fun bind(info: ReceiptProductListItem)
//    }

//    class ReceiptProductListReceiptInfoViewHolder(itemView: View) : ReceiptProductListViewHolder(itemView) {
//        private val edReceiptInfoMerchant: EditText =
//            itemView.findViewById(R.id.ed_product_name_value)
//        private val edReceiptInfoPostcode: EditText =
//            itemView.findViewById(R.id.ed_product_quantity_value)
//        private val edReceiptInfoTotalAmount: EditText =
//            itemView.findViewById(R.id.ed_product_currency_value)
//        private var edReceiptInfoCurrency: EditText =
//            itemView.findViewById(R.id.ed_receipt_product_list_receipt_info_currency_value)
//        private var edReceiptInfoPurchasedDate: EditText =
//            itemView.findViewById(R.id.ed_product_price_value)
//        private var edReceiptInfoComment: EditText =
//            itemView.findViewById(R.id.ed_product_description_value)
//
//        override fun bind(info: ReceiptProductListItem) {
//            val receiptInfo = info as ReceiptProductListReceiptInfo
//            edReceiptInfoMerchant.hint = receiptInfo.merchant
//            edReceiptInfoPostcode.hint = receiptInfo.postcode
//            edReceiptInfoTotalAmount.hint = receiptInfo.totalAmount.toString()
//            edReceiptInfoCurrency.hint = receiptInfo.currency
//            edReceiptInfoPurchasedDate.hint = TimeStringFormatter.format(receiptInfo.purchaseDate)
//            edReceiptInfoComment.hint = receiptInfo.comment
//        }
//    }


    class ReceiptProductListProductInfoViewHolder(itemView: View, var listener: OnReceiptProductListItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private val tvProductInfoProductName: TextView =
            itemView.findViewById(R.id.tv_receipt_product_list_product_info_product_name)
        private val tvProductInfoQuantity: TextView =
            itemView.findViewById(R.id.tv_receipt_product_list_product_info_quantity)
        private val tvProductInfoPrice: TextView =
            itemView.findViewById(R.id.tv_receipt_product_list_product_info_price)
        private var tvProductInfoCurrency: TextView =
            itemView.findViewById(R.id.tv_receipt_product_list_product_info_currency)
        private var edProductInfoDescription: EditText =
            itemView.findViewById(R.id.ed_receipt_product_list_product_info_description_value)

        fun bind(productInfo: ProductInfo) {
            tvProductInfoProductName.text = productInfo.name
            tvProductInfoQuantity.text = "Quantity: " + productInfo.quantity
            tvProductInfoPrice.text = productInfo.price.toString()
            tvProductInfoCurrency.text = productInfo.currency_code.currencyCode
            edProductInfoDescription.hint = productInfo.description

            itemView.setOnClickListener {
                listener.onReceiptProductListItemClick(productInfo.product_id)
            }
            itemView.setOnLongClickListener(View.OnLongClickListener {
                listener.onReceiptProductListItemLongClick(productInfo.product_id)
                true
            })

        }
    }

}