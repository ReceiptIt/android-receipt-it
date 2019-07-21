package com.receiptit.receiptList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.receiptit.R
import com.receiptit.network.model.receipt.ReceiptInfo
import com.receiptit.util.TimeStringFormatter

class ReceiptListRecyclerViewAdapter(private val list: List<ReceiptInfo>, private var mOnReceiptListItemClickListener: OnReceiptListItemClickListener): RecyclerView.Adapter<ReceiptListRecyclerViewAdapter.ReceiptListItemViewHolder>()  {

    interface OnReceiptListItemClickListener{
        fun onReceiptListItemClick(receiptId: Int, imageUrl: String? = null)
        fun onReceiptListItemLongClick(receiptId: Int)
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

    class ReceiptListItemViewHolder(itemView: View, var listener: OnReceiptListItemClickListener) : RecyclerView.ViewHolder(itemView) {
        private val tvReceiptMerchant: TextView = itemView.findViewById(R.id.tv_receipt_list_item_merchant)
        private val tvReceiptPostcode: TextView = itemView.findViewById(R.id.tv_receipt_list_item_postcode)
        private val tvReceiptTotalAmount: TextView = itemView.findViewById(R.id.tv_receipt_list_item_total_amount)
        private var tvReceiptPurchasedDate: TextView = itemView.findViewById(R.id.tv_receipt_list_item_purchased_date)

        fun bind(item: ReceiptInfo) {
            tvReceiptMerchant.text = item.merchant
            tvReceiptPostcode.text = item.postcode
            tvReceiptTotalAmount.text = item.total_amount.toString()
            tvReceiptPurchasedDate.text = TimeStringFormatter.format(item.purchase_date)
            itemView.setOnClickListener {
                listener.onReceiptListItemClick(item.receipt_id, item.image_url)
            }
            itemView.setOnLongClickListener {
                itemView.isSelected = true
                listener.onReceiptListItemLongClick(item.receipt_id)
                true
            }
        }
    }

}