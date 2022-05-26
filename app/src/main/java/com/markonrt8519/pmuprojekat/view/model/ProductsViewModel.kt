package com.markonrt8519.pmuprojekat.view.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.markonrt8519.pmuprojekat.data.product.Product

class ProductsViewModel: ViewModel() {
    val listProducts = MutableLiveData<List<Product>>()
    val products = MutableLiveData<Product>()
}