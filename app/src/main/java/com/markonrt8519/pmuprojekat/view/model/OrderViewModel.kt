package com.markonrt8519.pmuprojekat.view.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.markonrt8519.pmuprojekat.data.order.Order
import com.markonrt8519.pmuprojekat.data.order.OrderDetails

class OrderViewModel: ViewModel() {
    val listOrders = MutableLiveData<List<Order>>()
    val listOrderDetails = MutableLiveData<List<OrderDetails>>()
    val order = MutableLiveData<Order>()
    val orderDetails = MutableLiveData<OrderDetails>()
}