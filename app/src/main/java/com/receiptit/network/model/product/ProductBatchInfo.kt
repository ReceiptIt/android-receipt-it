package com.receiptit.network.model.product

data class ProductBatchInfo (var receipt_id: Int, var name: String, var description: String?, var quantity: Int,
                            var currency_code: String, var price: Double)