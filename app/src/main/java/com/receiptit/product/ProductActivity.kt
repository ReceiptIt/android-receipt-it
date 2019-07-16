package com.receiptit.product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.receiptit.R
import com.receiptit.network.model.ReceiptProductItem
import kotlinx.android.synthetic.main.receipt_product_list_receipt_info_item.*
import java.util.*

class ProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        title = getString(R.string.product_activity)
        init()

    }

    fun init() {
        var item =
            ReceiptProductItem("Peach", 3, 10.20, Currency.getInstance("CAD"), "test2")
        ed_product_name_value.hint = item.productName
        ed_product_quantity_value.hint = item.quantity.toString()
        ed_product_price_value.hint = item.price.toString()
        ed_product_currency_value.hint = item.currency.currencyCode
        ed_product_description_value.hint = item.description
    }
}
