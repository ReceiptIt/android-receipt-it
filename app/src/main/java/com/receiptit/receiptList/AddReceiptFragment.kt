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

class AddReceiptFragment : Fragment() {

    private var listener: OnAddReceiptFragmentCloseListener? = null
    private var ACTIVITY_RESULT_MANUALLY_CREATE_RECEIPT_ACTIVITY = 2
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnAddReceiptFragmentCloseListener {
        // TODO: Update argument type and name
        fun onAddReceiptFragmentClose()
        fun onAddReceiptManually()
    }

    companion object {

        // TODO: Customize parameter argument names
        const val USER_INFO = "USER_INFO"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(userInfo: UserInfo) =
            AddReceiptFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(USER_INFO, userInfo)
                }
            }
    }
}
