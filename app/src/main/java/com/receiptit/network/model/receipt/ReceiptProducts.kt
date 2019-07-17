package com.receiptit.network.model.receipt

import java.util.*

data class ReceiptProducts(var merchant: String, var postcode: String, var purchaseDate: Date,
                           var totalAmount: Double, var currency: Currency, var comment: String,
                           var productList: ArrayList<ReceiptProductItem>)