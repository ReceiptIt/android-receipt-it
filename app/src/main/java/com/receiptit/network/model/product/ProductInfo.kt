package com.receiptit.network.model.product

import java.util.*

data class ProductInfo (var productId: Int, var receiptId: Int, var name: String, var description: String?,
                        var quantity: Int, var currencyCode: Currency, var price: Double, var createdAt: String,
                        var updatedAt: String)