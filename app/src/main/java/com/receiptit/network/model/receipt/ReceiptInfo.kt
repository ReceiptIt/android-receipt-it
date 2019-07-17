package com.receiptit.network.model.receipt

data class ReceiptInfo (var receiptId: Int, var userId: Int, var purchaseDate: String, var totalAmount: Double,
                        var merchant: String, var postcode: String, var comment: String?,
                        var updatedAt: String, var createdAt: String)