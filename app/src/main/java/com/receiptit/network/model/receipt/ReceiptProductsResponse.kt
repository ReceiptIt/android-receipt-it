package com.receiptit.network.model.receipt

//TODO: change to receiptInfo
data class ReceiptProductsResponse (var receiptId: Int, var userId: Int, var purchaseDate: String, var totalAmount: Double,
                                    var merchant: String, var postcode: String, var updatedAt: String, var createdAt: String,
                                    )