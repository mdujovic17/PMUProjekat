package com.markonrt8519.pmuprojekat.view.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.markonrt8519.pmuprojekat.data.product.Supplier

class SupplierViewModel: ViewModel() {
    val listSuppliers = MutableLiveData<List<Supplier>>()
    val supplier = MutableLiveData<Supplier>()
}