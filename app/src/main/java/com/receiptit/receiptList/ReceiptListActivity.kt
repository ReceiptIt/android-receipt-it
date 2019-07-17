package com.receiptit.receiptList

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.receiptit.R
import com.receiptit.network.model.receipt.ReceiptListItem
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

        val recyclerView = rv_receipt_list
        recyclerView.layoutManager = LinearLayoutManager(this)

        //sample input
        var list = ArrayList<ReceiptListItem>()
        list.add(
            ReceiptListItem(
                "Costo",
                "110AAA",
                Currency.getInstance("USD"),
                10.03,
                Date()
            )
        )
        list.add(
            ReceiptListItem(
                "Uwaterloo",
                "220123",
                Currency.getInstance("CAD"),
                120.80,
                Date()
            )
        )

        val adapter = ReceiptListRecyclerViewAdapter(list, this)
        recyclerView.adapter = adapter

//        val userService = ServiceGenerator.createService(UserApi::class.java)
//        val call = userService.getUserInfo(1)
//        call.enqueue(object: retrofit2.Callback<UserInfoRetrieveResponse>{
//            override fun onFailure(call: Call<UserInfoRetrieveResponse>?, t: Throwable?) {
//                Toast.makeText(applicationContext, "Fuck you", Toast.LENGTH_SHORT).show()
//            }
//
//            @RequiresApi(Build.VERSION_CODES.O)
//            override fun onResponse(call: Call<UserInfoRetrieveResponse>?, response: Response<UserInfoRetrieveResponse>?) {
//                val result = response?.body()
//                val date = LocalDate.parse(result?.userInfo?.updatedAt, DateTimeFormatter.BASIC_ISO_DATE)
//                println(date)
//            }
//
//        })

    }

    override fun onReceiptListItemClick() {
        startActivity(Intent(this, ReceiptProductListActivity::class.java))
    }

}
