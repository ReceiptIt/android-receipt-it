package com.receiptit.network.model.product

import java.io.Serializable
import java.util.*

data class ProductInfo (var product_id: Int, var receipt_id: Int, var name: String, var description: String?,
                        var quantity: Int, var currency_code: Currency, var price: Double, var createdAt: String,
                        var updatedAt: String): Serializable