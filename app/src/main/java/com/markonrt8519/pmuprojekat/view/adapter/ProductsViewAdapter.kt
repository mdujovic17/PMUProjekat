package com.markonrt8519.pmuprojekat.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.markonrt8519.pmuprojekat.R
import com.markonrt8519.pmuprojekat.data.product.Product

class ProductsViewAdapter(val ctx: Context, val data: LiveData<List<Product>>): RecyclerView.Adapter<ProductsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.product_row, parent, false)
        return ProductsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.bindItems(data.value!!.get(position))
    }

    override fun getItemCount(): Int {
        if (data.value != null) {
            return data.value!!.size
        }
        return 0
    }
}

class ProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindItems(model: Product) {
        val productId = model.productId
        val name = itemView.findViewById<TextView>(R.id.productName)
        name.text=model.productName
    }
}