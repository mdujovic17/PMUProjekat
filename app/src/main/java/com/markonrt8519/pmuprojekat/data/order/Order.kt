package com.markonrt8519.pmuprojekat.data.order

data class Order (
    val orderId: Int?,
    val customerId: String?,
    val employeeId: Int?,
    val orderDate: String?,
    val requiredDate: String?,
    val shippedDate: String?,
    val shipVia: Int?,
    val freight: Double?,
    val shipName: String?,
    val shipAddress: String?,
    val shipRegion: String?,
    val shipPostalCode: String?,
    val shipCountry: String?,
    val customer: Customer?,
    val employee: Employee?,
    val shipViaNavigation: Shipper?
        )