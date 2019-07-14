package com.receiptit.model

import java.util.*

data class ReceiptListItem(var merchant:String, var postcode: String,
                           var currency: Currency, var totalAmount: Double, var purchaseTime: Date) {

}