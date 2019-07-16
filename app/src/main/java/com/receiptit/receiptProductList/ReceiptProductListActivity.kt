package com.receiptit.receiptProductList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.receiptit.R
import com.receiptit.network.model.ReceiptProductItem
import com.receiptit.network.model.ReceiptProducts
import com.receiptit.product.ProductActivity
import kotlinx.android.synthetic.main.activity_receipt_product_list.*

import java.util.*
import kotlin.collections.ArrayList

class ReceiptProductListActivity : AppCompatActivity(), ReceiptProductListRecyclerViewAdapter.onReceiptProductListItemClickListerner {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_product_list)
        title = getString(R.string.receipt_product_list_activity)

        val recyclerView = rv_receipt_product_list
        recyclerView.layoutManager = LinearLayoutManager(this)

        //sample input
        val list = ArrayList<ReceiptProductItem>()
        list.add(
            ReceiptProductItem(
                "Apple",
                5,
                10.03,
                Currency.getInstance("USD"),
                "test1"
            )
        )
        list.add(
            ReceiptProductItem(
                "Peach",
                3,
                10.20,
                Currency.getInstance("CAD"),
                "test2"
            )
        )
        val product = ReceiptProducts(
            "Costo",
            "110ABD",
            Date(),
            100.50,
            Currency.getInstance("USD"),
            "Wanna go home",
            list
        )
        val adapter = ReceiptProductListRecyclerViewAdapter(product, this)
        recyclerView.adapter = adapter
    }

    override fun onReceiptProductListItemClick() {
        startActivity(Intent(this, ProductActivity::class.java))
    }
}
