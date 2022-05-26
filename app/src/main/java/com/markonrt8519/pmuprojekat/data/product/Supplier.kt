package com.markonrt8519.pmuprojekat.data.product

data class Supplier (val supplierId: Int?,
                     val companyName: String,
                     val contactName: String?,
                     val contactTitle: String?,
                     val address: String?,
                     val city: String?,
                     val region: String?,
                     val postalCode: String?,
                     val country: String?,
                     val phone: String?,
                     val fax: String?,
                     val homePage: String?
                    )