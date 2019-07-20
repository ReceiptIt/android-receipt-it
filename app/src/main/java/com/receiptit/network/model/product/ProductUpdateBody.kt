package com.receiptit.network.model.product

import java.util.*

data class ProductUpdateBody (var receipt_id: Int, var product_id: Int, var description: String? = null,
                              var currency_code: Currency? = null, var name: String? = null, var quantity: Int? = null,
                              var price: Double? = null)