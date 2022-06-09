package com.markonrt8519.pmuprojekat.data.product

data class Product (
    val productId: Int?,
    val productName: String?,
    val supplierId: Int?,
    val categoryId: Int?,
    val quantityPerUnit: String?,
    val unitPrice: Double?,
    val unitsInStock: Int?,
    val unitsOnOrder: Int?,
    val reorderLevel: Int?,
    val discontinued: Boolean?
)