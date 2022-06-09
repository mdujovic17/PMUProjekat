package com.markonrt8519.pmuprojekat.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.markonrt8519.pmuprojekat.R
import com.markonrt8519.pmuprojekat.data.product.Product
import com.markonrt8519.pmuprojekat.data.product.Supplier

class SupplierVIewAdapter(val ctx: Context, val data: LiveData<List<Supplier>>, val products: List<Product>?): RecyclerView.Adapter<SupplierVIewAdapter.SupplierViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SupplierVIewAdapter.SupplierViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.supplier_row, parent, false)
        return SupplierViewHolder(view)    }

    override fun onBindViewHolder(holder: SupplierVIewAdapter.SupplierViewHolder, position: Int) {
        holder.bindItems(data.value!![position])
    }

    override fun getItemCount(): Int {
        if (data.value != null) {
            return data.value!!.size
        }
        return 0
    }

    inner class SupplierViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItems(model: Supplier) {
            val companyName = model.companyName
            var msg = ""
            var productCount: Int = 0

            if (products != null) {
                for (product in products) {
                    if (product.supplierId == model.supplierId) {
                        msg += product.productName + "\n"
                        productCount += 1
                    }
                }
            }

            val name = itemView.findViewById<TextView>(R.id.supplierId)

            val details = itemView.findViewById<Button>(R.id.supplierDetails)
            val productCountText = itemView.findViewById<TextView>(R.id.numOfProductsSuppliers)

            name.text = companyName
            productCountText.text = productCount.toString()

            details.setOnClickListener {
                val dialog = AlertDialog.Builder(ctx)
                dialog.setTitle("Dobavljac: $companyName")
                dialog.setMessage(msg)

                dialog.show()
            }
        }
    }
}