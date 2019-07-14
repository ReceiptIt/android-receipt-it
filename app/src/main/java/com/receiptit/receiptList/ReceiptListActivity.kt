package com.receiptit.receiptList

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.receiptit.R
import com.receiptit.model.ReceiptListItem
import com.receiptit.receiptProductList.ReceiptProductListActivity

import kotlinx.android.synthetic.main.activity_receipt_list.*
import kotlinx.android.synthetic.main.content_receipt_list.*
import java.util.*
import kotlin.collections.ArrayList

class ReceiptListActivity : AppCompatActivity(), ReceiptListRecyclerViewAdapter.onReceiptListItemClickListerner {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_list)
        setSupportActionBar(toolbar)
        title = getString(R.string.receipt_list_activity)

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        val recyclerView = rv_receipt_list
        recyclerView.layoutManager = LinearLayoutManager(this)

        //sample input
        var list = ArrayList<ReceiptListItem>()
        list.add(ReceiptListItem("Costo", "110AAA", Currency.getInstance("USD"), 10.03, Date()))
        list.add(ReceiptListItem("Uwaterloo", "220123", Currency.getInstance("CAD"), 120.80, Date()))

        val adapter = ReceiptListRecyclerViewAdapter(list, this)
        recyclerView.adapter = adapter

    }

    override fun onReceiptListItemClick() {
        startActivity(Intent(this, ReceiptProductListActivity::class.java))
    }

}
