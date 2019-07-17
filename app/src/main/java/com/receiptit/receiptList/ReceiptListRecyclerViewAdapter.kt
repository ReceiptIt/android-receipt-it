package com.receiptit.receiptList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.receiptit.R
import com.receiptit.network.model.receipt.ReceiptInfo
import com.receiptit.util.StringResourceManager
import com.receiptit.util.TimeStringFormatter

class ReceiptListRecyclerViewAdapter(private val list: List<ReceiptInfo>, private var mOnReceiptListItemClickListener: onReceiptListItemClickListerner): RecyclerView.Adapter<ReceiptListRecyclerViewAdapter.ReceiptListItemViewHolder>()  {

    interface onReceiptListItemClickListerner{
        fun onReceiptListItemClick(receiptId: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptListItemViewHolder {
        return ReceiptListItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.receipt_list_item, parent, false), mOnReceiptListItemClickListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ReceiptListItemViewHolder, position: Int) {
        val text: ReceiptInfo = list[position]
        holder.bind(text)
    }

    class ReceiptListItemViewHolder(itemView: View, var listener: onReceiptListItemClickListerner) : RecyclerView.ViewHolder(itemView) {
        private val tvReceiptMerchant: TextView = itemView.findViewById(R.id.tv_receipt_list_item_merchant)
        private val tvReceiptPostcode: TextView = itemView.findViewById(R.id.tv_receipt_list_item_postcode)
        private val tvReceiptTotalAmount: TextView = itemView.findViewById(R.id.tv_receipt_list_item_total_amount)
        private var tvReceiptCurrency: TextView = itemView.findViewById(R.id.tv_receipt_list_item_currency_code)
        private var tvReceiptPurchasedDate: TextView = itemView.findViewById(R.id.tv_receipt_list_item_purchased_date)

        fun bind(item: ReceiptInfo) {
            tvReceiptMerchant.text = item.merchant
            tvReceiptPostcode.text = item.postcode
            tvReceiptTotalAmount.text = item.total_amount.toString()
            tvReceiptCurrency.text = StringResourceManager.getDefaultCurrencyCode()
            tvReceiptPurchasedDate.text = TimeStringFormatter.format(item.purchase_date)
            itemView.setOnClickListener {
                listener.onReceiptListItemClick(item.receipt_id)
            }
        }
    }

}