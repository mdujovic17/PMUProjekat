package com.markonrt8519.pmuprojekat.data.order

import java.util.*

data class Employee (val employeeId: Int,
                     val lastName: String,
                     val firstName: String,
                     val title: String,
                     val titleOfCourtesy: String,
                     val birthDate: Date,
                     val hireDate: Date,
                     val address: String,
                     val city: String,
                     val region: String,
                     val postalCode: String,
                     val country: String,
                     val homePhone: String,
                     val extension: String,
                     val notes: String,
                     val reportsTo: Int) {
}