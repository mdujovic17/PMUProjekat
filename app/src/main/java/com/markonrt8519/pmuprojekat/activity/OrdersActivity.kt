package com.markonrt8519.pmuprojekat.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import com.markonrt8519.pmuprojekat.R
import com.markonrt8519.pmuprojekat.api.handler.NorthwindAPIHandler
import com.markonrt8519.pmuprojekat.data.order.Order
import com.markonrt8519.pmuprojekat.view.adapter.OrderViewAdapter
import com.markonrt8519.pmuprojekat.view.model.OrderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersActivity : AppCompatActivity() {

    val viewModel: OrderViewModel by viewModels()
    var orderViewAdapter: OrderViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
    }

    private fun fetchOrders(ctx: Context, sUrl: String): List<Order>? {
        var orders: List<Order>? = null
        lifecycleScope.launch(Dispatchers.IO) {
            val apiHandler = NorthwindAPIHandler()
            val result = apiHandler.getRequest(sUrl)

            if (result != null) {
                try {
                    orders = Klaxon().parseArray(result)

                    withContext(Dispatchers.Main) {
                        viewModel.listOrders.value = orders
                        val listOrderView = findViewById<RecyclerView>(R.id.listOrderView)
                        listOrderView.layoutManager = LinearLayoutManager(ctx)
                        orderViewAdapter = OrderViewAdapter(ctx, viewModel.listOrders)
                        listOrderView.adapter = orderViewAdapter
                    }
                }
                catch (err: Error) {
                    print("Error when parsing JSON: " + err.localizedMessage)
                }
            }
            else {
                print("Error: GET request returned no response")
            }
        }
        return orders
    }
}