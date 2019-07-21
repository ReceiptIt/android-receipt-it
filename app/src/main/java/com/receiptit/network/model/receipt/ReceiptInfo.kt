package com.receiptit.network.model.receipt

import java.io.Serializable

data class ReceiptInfo (var receipt_id: Int, var user_id: Int, var purchase_date: String, var total_amount: Double,
                        var merchant: String, var postcode: String, var comment: String?, var image_name: String? = null,
                        var image_url: String? = null, var updatedAt: String, var createdAt: String): Serializable