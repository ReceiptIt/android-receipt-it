package com.receiptit.receiptList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.receiptit.R
import com.receiptit.network.model.user.UserInfo
import org.w3c.dom.Text

class AddReceiptFragment : Fragment() {

    private var listener: OnAddReceiptFragmentCloseListener? = null
    private var userInfo: UserInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = arguments?.getSerializable(USER_INFO) as UserInfo
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_receipt, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val closeBtn: ImageView? = getView()?.findViewById(R.id.iv_receipt_list_add_receipt_close)
        closeBtn?.bringToFront()
        closeBtn?.setOnClickListener {
            listener?.onAddReceiptFragmentClose()
        }

        val createReceipt: TextView = view.findViewById(R.id.tv_receipt_list_add_receipt_manually_create)
        createReceipt.setOnClickListener {
            listener?.onAddReceiptManually()
            listener?.onAddReceiptFragmentClose()
        }

        val scanReceipt: TextView = view.findViewById(R.id.tv_receipt_list_add_receipt_scan_receipt)
        scanReceipt.setOnClickListener {
            listener?.onAddReceiptScan()
            listener?.onAddReceiptFragmentClose()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnAddReceiptFragmentCloseListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnAddReceiptFragmentCloseListener {
        fun onAddReceiptFragmentClose()
        fun onAddReceiptManually()
        fun onAddReceiptScan()
    }

    companion object {

        const val USER_INFO = "USER_INFO"
        @JvmStatic
        fun newInstance(userInfo: UserInfo) =
            AddReceiptFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(USER_INFO, userInfo)
                }
            }
    }
}
