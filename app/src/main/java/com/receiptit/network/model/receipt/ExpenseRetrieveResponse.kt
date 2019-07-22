package com.receiptit.network.model.receipt

import java.io.Serializable

data class ExpenseRetrieveResponse (var startDate: String, var endDate: String, var userId: Int,
                                    var totalExpense: Double): Serializable