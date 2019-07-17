package com.receiptit.network.model.product

import java.util.*

data class ProductUpdateBody (var receipt_id: Int, var product_id: Int, var description: String?,
                              var currency_code: Currency?, var name: String?, var quantity: Int?,
                              var price: Double?)