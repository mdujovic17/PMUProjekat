package com.markonrt8519.pmuprojekat.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.markonrt8519.pmuprojekat.R
import com.markonrt8519.pmuprojekat.activity.OrderDetailsActivity
import com.markonrt8519.pmuprojekat.data.order.Order

class OrderViewAdapter(val ctx: Context, val data: LiveData<List<Order>>): RecyclerView.Adapter<OrderViewAdapter.OrderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewAdapter.OrderViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.order_row, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewAdapter.OrderViewHolder, position: Int) {
        holder.bindItems(data.value!!.get(position))
    }

    override fun getItemCount(): Int {
        if (data.value != null) {
            return data.value!!.size
        }
        return 0    }

    inner class OrderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItems(model: Order) {
            val orderId = model.orderId

            val id = itemView.findViewById<TextView>(R.id.orderId)
            val details = itemView.findViewById<Button>(R.id.orderDetails)

            id.text = orderId.toString()

            details.setOnClickListener {
                val intent = Intent(ctx, OrderDetailsActivity::class.java)
                intent.putExtra("orderId", orderId)
                ctx.startActivity(intent)
            }
        }
    }
}