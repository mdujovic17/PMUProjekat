package com.markonrt8519.pmuprojekat.data.product

class Product (
    val id: Int,
    val name: String,
    val supplierId: Int,
    val categoryId: Int,
    val quantityPerUnit: String,
    val unitPrice: Double,
    val unitsInStock: Int,
    val unitsOnOrder: Int,
    val reorderLevel: Int,
    val discounted: Boolean,
    val category: Category,
    val supplier: Supplier
)   {
}