package com.receiptit.network.model.receipt

import com.receiptit.network.model.product.ProductInfo

//TODO: change to receiptInfo
data class ReceiptProductsResponse (var receipt_id: Int, var user_id: Int, var purchase_date: String, var total_amount: Double,
                                    var merchant: String, var postcode: String, var comment: String?,
                                    var updatedAt: String, var createdAt: String, var products: ArrayList<ProductInfo>)