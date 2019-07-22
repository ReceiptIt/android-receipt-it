package com.receiptit.navDrawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.receiptit.R
import com.receiptit.network.model.product.ProductInfo
import kotlin.collections.ArrayList

class ExpenseReportListRecyclerViewAdapter(var list: ArrayList<ProductInfo>) :
    RecyclerView.Adapter<ExpenseReportListRecyclerViewAdapter.ReceiptProductListProductInfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptProductListProductInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_list_item, parent, false)
        return ReceiptProductListProductInfoViewHolder(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ReceiptProductListProductInfoViewHolder, position: Int) {
        val info: ProductInfo = list[position]
        holder.bind(info)
    }

    class ReceiptProductListProductInfoViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val tvProductInfoProductName: TextView =
            itemView.findViewById(R.id.tv_receipt_product_list_product_info_product_name)
        private val tvProductInfoQuantity: TextView =
            itemView.findViewById(R.id.tv_receipt_product_list_product_info_quantity)
        private val tvProductInfoPrice: TextView =
            itemView.findViewById(R.id.tv_receipt_product_list_product_info_price)

        fun bind(productInfo: ProductInfo) {
            tvProductInfoProductName.text = productInfo.name
            tvProductInfoQuantity.text = productInfo.quantity.toString()
            tvProductInfoPrice.text = productInfo.price.toString()

        }
    }

}