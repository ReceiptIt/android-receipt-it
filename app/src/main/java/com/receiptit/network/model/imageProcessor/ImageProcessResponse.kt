package com.receiptit.network.model.imageProcessor

data class ImageProcessResponse (var image_name: String, var image_url: String, var merchant: String,
                                 var postcode: String, var total_amount: Double,
                                 var products: ArrayList<ImageProcessProductInfo>)