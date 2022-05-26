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
import com.markonrt8519.pmuprojekat.data.product.Product
import com.markonrt8519.pmuprojekat.view.adapter.ProductsViewAdapter
import com.markonrt8519.pmuprojekat.view.model.ProductsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductsActivity : AppCompatActivity() {

    val viewModel: ProductsViewModel by viewModels()
    var productsViewAdapter: ProductsViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)
        fetchProducts(this, "Products")
    }

    private fun fetchProducts(ctx: Context, sUrl: String): List<Product>? {
        var products: List<Product>? = null
        lifecycleScope.launch(Dispatchers.IO) {
            val apiHandler = NorthwindAPIHandler()
            val result = apiHandler.getRequest(sUrl)
            if (result != null) {
                try {
                    //Parse result string JSON into data class
                    products = Klaxon().parseArray(result)

                    withContext(Dispatchers.Main) {
                        viewModel.listProducts.value = products
                        val listProductsView = findViewById<RecyclerView>(R.id.listProductsView)
                        listProductsView.layoutManager = LinearLayoutManager(ctx)
                        productsViewAdapter = ProductsViewAdapter(ctx, viewModel.listProducts)
                        listProductsView.adapter = productsViewAdapter
                    }
                }
                catch (err: Error) {
                    print("Error when parsing JSON: " + err.localizedMessage)
                }
            }
            else {
                print("Error: Get request returned no response")
            }
        }
        return products
    }
}