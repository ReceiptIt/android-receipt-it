package com.receiptit.network.model.product

import java.util.*

data class ProductUpdateBody (var receiptId: Int, var productId: Int, var description: String?,
                              var currencyCode: Currency?, var name: String?, var quantity: Int?,
                              var price: Double?)