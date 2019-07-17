package com.receiptit.network.model.receipt

import com.receiptit.network.model.product.ProductInfo

//TODO: change to receiptInfo
data class ReceiptProductsResponse (var receiptId: Int, var userId: Int, var purchaseDate: String, var totalAmount: Double,
                                    var merchant: String, var postcode: String, var comment: String?,
                                    var updatedAt: String, var createdAt: String, var products: ArrayList<ProductInfo>)