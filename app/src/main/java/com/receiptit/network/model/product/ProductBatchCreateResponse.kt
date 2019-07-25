package com.receiptit.network.model.product

data class ProductBatchCreateResponse (var result: String, var message: String, var productInfo: ArrayList<ProductInfo>)