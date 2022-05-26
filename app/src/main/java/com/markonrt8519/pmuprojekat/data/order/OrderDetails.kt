package com.markonrt8519.pmuprojekat.data.order

import com.markonrt8519.pmuprojekat.data.product.Product

data class OrderDetails (
    val orderId: Int,
    val productId: Int,
    val unitPrice: Double,
    val quantity: Int,
    val discount: Float,
    val order: Order,
    val product: Product) {
}