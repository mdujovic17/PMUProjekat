package com.markonrt8519.pmuprojekat.view.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.markonrt8519.pmuprojekat.data.product.Category

class CategoryViewModel: ViewModel() {
    val listCategory = MutableLiveData<List<Category>>()
    val category = MutableLiveData<Category>()
}