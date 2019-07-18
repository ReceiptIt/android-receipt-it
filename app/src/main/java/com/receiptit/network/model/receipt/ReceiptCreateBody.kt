package com.receiptit.network.model.receipt

data class ReceiptCreateBody (var user_id: Int, var purchase_date: String, var total_amount: Double, var merchant: String,
                              var postcode: String, var comment: String? = null)