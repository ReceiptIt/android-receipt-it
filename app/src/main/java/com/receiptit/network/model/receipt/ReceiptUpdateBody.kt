package com.receiptit.network.model.receipt

data class ReceiptUpdateBody (var purchase_date: String? = null, var total_amount: Double? = null,
                              var merchant: String? = null, var postcode: String? = null,
                              var comment: String? = null)