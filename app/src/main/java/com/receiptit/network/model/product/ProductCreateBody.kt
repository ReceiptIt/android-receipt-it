package com.receiptit.network.model.product

import java.util.*

data class ProductCreateBody (var receiptId: Int, var name: String, var quantity: Int, var price: Double,
                              var description: String?, var currencyCode: Currency?)