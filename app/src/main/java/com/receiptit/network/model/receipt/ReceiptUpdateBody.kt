package com.receiptit.network.model.receipt

data class ReceiptUpdateBody (var purchaseDate: String?, var totalAmount: Double?, var merchant: String,
                              var postcode: String?, var comment: String?)