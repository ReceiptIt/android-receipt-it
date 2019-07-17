package com.receiptit.network.model.receipt

data class ReceiptInfo (var receipt_id: Int, var user_id: Int, var purchase_date: String, var total_amount: Double,
                        var merchant: String, var postcode: String, var comment: String?,
                        var updatedAt: String, var createdAt: String)