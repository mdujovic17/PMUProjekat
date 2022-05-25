package com.markonrt8519.pmuprojekat.data.order

import java.util.Date

class Order (
    val id: Int,
    val customerId: String,
    val employeeId: Int,
    val orderDate: Date,
    val requiredDate: Date,
    val ShippedDate: Date,
    val shipVIa: Int,
    val freight: Double,
    val shipName: String,
    val shipAddress: String,
    val shipRegion: String,
    val shipPostalCode: String,
    val shipCountry: String,
    val customer: Customer,
    val employee: Employee,
    val shipper: Shipper
        ) {
}