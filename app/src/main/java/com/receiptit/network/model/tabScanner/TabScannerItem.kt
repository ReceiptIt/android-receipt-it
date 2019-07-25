package com.receiptit.network.model.tabScanner

data class TabScannerItem (var qty: Int, var desc: String, var unit: String?, var price: Double, var descClean: String?,
                           var lineTotal: Double, var productCode: String)