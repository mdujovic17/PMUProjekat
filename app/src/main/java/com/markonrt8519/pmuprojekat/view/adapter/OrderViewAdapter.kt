package com.markonrt8519.pmuprojekat.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.markonrt8519.pmuprojekat.R
import com.markonrt8519.pmuprojekat.data.order.Order
import com.markonrt8519.pmuprojekat.data.order.OrderDetails
import com.markonrt8519.pmuprojekat.data.product.Product
import org.w3c.dom.Text

class OrderViewAdapter(
    val ctx: Context, val data: LiveData<List<Order>>, val orderDetails: List<OrderDetails>?, val products: List<Product>
): RecyclerView.Adapter<OrderViewAdapter.OrderViewHolder>() {
    private final val TAG: String = "OrderViewAdapter"

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewAdapter.OrderViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.order_row, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewAdapter.OrderViewHolder, position: Int) {
        holder.bindItems(data.value!![position])

    }

    override fun getItemCount(): Int {
        if (data.value != null) {
            return data.value!!.size
        }
        return 0
    }

    fun recvProductsInfo(productNames: List<String>) {
        //products = productNames
        Log.i(TAG, "Recieved products list successfully")
    }

    inner class OrderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItems(model: Order) {
            val orderId = model.orderId
            var msg = ""
            var productCount: Int = 0

            if (orderDetails != null) {
                for (order in orderDetails) {
                    if (order.orderId == model.orderId) {
                        for (product in products) {
                            if (product.productId == order.productId) {
                                msg += product.productName + "\n"
                                break
                            }
                        }
                        productCount += 1
                    }
                }
            }

            val id = itemView.findViewById<TextView>(R.id.orderId)
            val details = itemView.findViewById<Button>(R.id.orderDetails)
            val productCountText = itemView.findViewById<TextView>(R.id.numOfProductsOrders)

            id.text = orderId.toString()
            productCountText.text = productCount.toString()

            details.setOnClickListener {
                val dialog = AlertDialog.Builder(ctx)
                dialog.setTitle("Porudzbina: $orderId")
                dialog.setMessage(msg)

                dialog.show()
            }
        }
    }
}