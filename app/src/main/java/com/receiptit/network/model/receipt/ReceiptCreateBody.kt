package com.receiptit.network.model.receipt

import java.util.*

data class ReceiptCreateBody (var userId: Int, var purchaseDate: String, var totalAmount: Double, var merchant: String,
                              var postcode: String, var comment: String?)