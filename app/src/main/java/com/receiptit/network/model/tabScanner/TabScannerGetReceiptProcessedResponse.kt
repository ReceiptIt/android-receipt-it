package com.receiptit.network.model.tabScanner

data class TabScannerGetReceiptProcessedResponse (var messgae: String, var status: String, var status_code: String,
                                                  var result: TabScannerResult)