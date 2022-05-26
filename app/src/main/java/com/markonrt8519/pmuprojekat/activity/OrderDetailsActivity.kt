package com.markonrt8519.pmuprojekat.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.beust.klaxon.Klaxon
import com.markonrt8519.pmuprojekat.R
import com.markonrt8519.pmuprojekat.api.Routes
import com.markonrt8519.pmuprojekat.api.handler.NorthwindAPIHandler
import com.markonrt8519.pmuprojekat.data.order.OrderDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Route

class OrderDetailsActivity : AppCompatActivity() {

    var orderDetails: OrderDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details2)
        val orderId = intent.extras!!["orderId"]
        orderDetails = fetchOrderDetail(Routes.ORDER_DETAILS + orderId)
    }

    @SuppressLint("SetTextI18n")
    private fun fetchOrderDetail(sUrl: String): OrderDetails? {
        var orderDetail: OrderDetails? = null
        lifecycleScope.launch(Dispatchers.IO) {
            val apiHandler = NorthwindAPIHandler()
            val result = apiHandler.getRequest(sUrl)
            if (result != null) {
                try {
                    orderDetail = Klaxon().parse(result)

                    withContext(Dispatchers.Main) {
                        val orderDetails = findViewById<TextView>(R.id.orderDetailsId)
                        orderDetails.text = "Detalji porudzbine: " + orderDetail!!.orderId
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
        return orderDetail
    }
}