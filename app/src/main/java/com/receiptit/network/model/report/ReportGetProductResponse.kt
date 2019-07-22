package com.receiptit.network.model.report

import com.receiptit.network.model.product.ProductInfo

data class ReportGetProductResponse (var startDate: String, var endDate: String, var userId: Int,
                                     var products: ArrayList<ProductInfo>)