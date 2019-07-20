package com.receiptit.util

object TimeStringFormatter {
    private val SPLIT_POINT = "T"

    // example input: "2019-07-14T17:57:04.000Z"
    fun format(date: String): String {
         return date.split(SPLIT_POINT)[0]
    }

    fun concatenate(date: String): String {
        return date + "T00:00:00.000Z"
    }
}