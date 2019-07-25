package com.receiptit.network.model.tabScanner

data class TabScannerUploadReceiptImageResponse (var message: String, var status: String, var status_code: Int,
                                                 var token: String, var success: Boolean, var code: Int,
                                                 var duplicate: Boolean?, var duplicateToken: String?)