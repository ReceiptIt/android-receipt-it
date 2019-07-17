package com.receiptit.network.model.receipt

data class ReceiptUpdateBody (var purchase_date: String?, var total_amount: Double?, var merchant: String,
                              var postcode: String?, var comment: String?)