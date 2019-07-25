package com.receiptit.network.model.tabScanner

data class TabScannerResult (var establishment: String, var validatedEstablishment: Boolean, var date: String,
                             var total: String, var url: String?, var phoneNumber: String, var paymentMethod: String,
                             var address: String, var validatedTotal: Boolean, var subTotal: String,
                             var validatedSubTotal: Boolean, var cash: String, var lineItems: ArrayList<TabScannerItem>,
                             var addressNorm: TabScannerAddressNorm)