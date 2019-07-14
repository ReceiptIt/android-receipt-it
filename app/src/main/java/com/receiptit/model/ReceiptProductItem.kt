package com.receiptit.model

import java.util.*

data class ReceiptProductItem(var productName: String, var quantity: Int, var price: Double,
                               var currency: Currency, var description: String)
