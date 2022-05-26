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
import com.markonrt8519.pmuprojekat.activity.CategoryDetailsActivity
import com.markonrt8519.pmuprojekat.data.product.Category

class CategoryViewAdapter (val ctx: Context, val data: LiveData<List<Category>>): RecyclerView.Adapter<CategoryViewAdapter.CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater:LayoutInflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.category_row, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bindItems(data.value!!.get(position))
    }

    override fun getItemCount(): Int {
        if (data.value != null) {
            return data.value!!.size
        }
        return 0
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(model: Category) {
            val categoryId = model.categoryId

            val name = itemView.findViewById<TextView>(R.id.categoryName)
            val details = itemView.findViewById<Button>(R.id.categoryDetails)

            name.text=model.categoryName

            details.setOnClickListener {
                val intent = Intent(ctx, CategoryDetailsActivity::class.java)
                intent.putExtra("categoryId", categoryId)
                ctx.startActivity(intent)
            }
        }
    }
}

