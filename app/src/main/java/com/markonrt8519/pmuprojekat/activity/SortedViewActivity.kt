package com.markonrt8519.pmuprojekat.activity

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import com.markonrt8519.pmuprojekat.R
import com.markonrt8519.pmuprojekat.api.Routes
import com.markonrt8519.pmuprojekat.api.handler.NorthwindAPIHandler
import com.markonrt8519.pmuprojekat.data.order.Order
import com.markonrt8519.pmuprojekat.data.order.OrderDetails
import com.markonrt8519.pmuprojekat.data.product.Product
import com.markonrt8519.pmuprojekat.data.product.Supplier
import com.markonrt8519.pmuprojekat.view.adapter.OrderViewAdapter
import com.markonrt8519.pmuprojekat.view.adapter.SupplierVIewAdapter
import com.markonrt8519.pmuprojekat.view.model.OrderViewModel
import com.markonrt8519.pmuprojekat.view.model.SupplierViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SortedViewActivity : AppCompatActivity() {
    private val TAG = "SortedView"

    private var ordersLoaded: Boolean = false
    private var orderDetailsLoaded: Boolean = false
    private var productsLoaded: Boolean = false
    private var suppliersLoaded: Boolean = false

    private var progressBar: ProgressBar? = null

    private var products: List<Product>? = null
    private var orderDetails: List<OrderDetails>? = null

    private val orderViewModel: OrderViewModel by viewModels()
    private var orderViewAdapter: OrderViewAdapter? = null

    private val supplierViewModel: SupplierViewModel by viewModels()
    private var supplierViewAdapter: SupplierVIewAdapter? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sorted_view)

        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val buttons = findViewById<LinearLayout>(R.id.LinearLayout)
        val constLayoutOrders = findViewById<ConstraintLayout>(R.id.constraintLayoutOrders)
        val constLayoutSuppliers = findViewById<ConstraintLayout>(R.id.constraintLayoutSuppliers)

        progressBar!!.visibility = View.VISIBLE
        buttons.visibility = View.INVISIBLE
        constLayoutOrders.visibility = View.INVISIBLE
        constLayoutSuppliers.visibility = View.INVISIBLE


        val thread = Thread {
            fetchRequiredData()

            while (!ordersLoaded || !orderDetailsLoaded || !productsLoaded || !suppliersLoaded) {
                ;
            }

            runOnUiThread {
                progressBar!!.visibility = View.GONE
                buttons.visibility = View.VISIBLE
                constLayoutOrders.visibility = View.VISIBLE
            }
        }
        thread.start()
    }

    private fun fetchOrders(ctx: Context, sUrl: String): List<Order>? {

        var orders: List<Order>? = null

        lifecycleScope.launch(Dispatchers.IO) {
            var prods: MutableList<String>? = mutableListOf()

            val apiHandler = NorthwindAPIHandler()

            val result = apiHandler.getRequest(sUrl)

            if (result != null) {
                try {
                    //Parse result string JSON into data class
                    orders = Klaxon().parseArray(result)

                    ordersLoaded = true

                    Log.i(TAG, "Parsing of objects of type 'Order' was successful.")
                    withContext(Dispatchers.Main) {
                        orderViewModel.listOrders.value = orders
                        val listProductsView = findViewById<RecyclerView>(R.id.listOrdersView)
                        listProductsView.layoutManager = LinearLayoutManager(ctx)
                        orderViewAdapter = OrderViewAdapter(ctx, orderViewModel.listOrders, orderDetails)

                        if (products != null) {
                            for (p in products!!) {
                                p.productName?.let { prods!!.add(it) }
                            }
                            prods?.let { orderViewAdapter!!.recvProductsInfo(it) }
                        }
                        prods = null

                        listProductsView.adapter = orderViewAdapter
                    }
                }
                catch (err: Error) {
                    Log.e(TAG, "Error when parsing JSON: " + err.localizedMessage)
                }
            }
            else {
                Log.e(TAG, "Error: Get request returned no response")
            }
        }

        return orders
    }

    private fun fetchSuppliers(ctx: Context, sUrl: String): List<Supplier>? {

        var suppliers: List<Supplier>? = null

        lifecycleScope.launch(Dispatchers.IO) {
            val apiHandler = NorthwindAPIHandler()
            val result = apiHandler.getRequest(sUrl)
            if (result != null) {
                try {
                    //Parse result string JSON into data class
                    suppliers = Klaxon().parseArray(result)
                    suppliersLoaded = true
                    Log.i(TAG, "Parsing objects of type 'Suppliers' was successful.")
                    withContext(Dispatchers.Main) {
                        supplierViewModel.listSuppliers.value = suppliers
                        val listSuppliersView = findViewById<RecyclerView>(R.id.listSuppliersView)
                        listSuppliersView.layoutManager = LinearLayoutManager(ctx)
                        supplierViewAdapter = SupplierVIewAdapter(ctx, supplierViewModel.listSuppliers, products)

                        listSuppliersView.adapter = supplierViewAdapter
                    }
                }
                catch (err: Error) {
                    Log.e(TAG, "Error when parsing JSON: " + err.localizedMessage)
                }
            }
            else {
                Log.e(TAG, "Error: Get request returned no response")
            }
        }

        return suppliers
    }

    private inline fun <reified T> fetchAll(ctx: Context, sUrl: String): List<T>? {
        var list: List<T>? = null

        lifecycleScope.launch(Dispatchers.IO) {
            val apiHandler = NorthwindAPIHandler()
            val result = apiHandler.getRequest(sUrl)
            if (result != null) {
                try {
                    //Parse result string JSON into data class
                    list = Klaxon().parseArray(result)

                    Log.i(TAG, "Parsing of objects from $sUrl was successful.")
                }
                catch (err: Error) {
                    Log.e(TAG, "Error when parsing JSON: " + err.localizedMessage)
                }
            }
            else {
                Log.e(TAG, "Error: Get request returned no response")
            }
        }

        return list
    }

    private fun fetchOrderDetails(ctx: Context, sUrl: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val apiHandler = NorthwindAPIHandler()
            val result = apiHandler.getRequest(sUrl)
            if (result != null) {
                try {
                    //Parse result string JSON into data class
                    orderDetails = Klaxon().parseArray(result)
                    orderDetailsLoaded = true
                    Log.i(TAG, "Parsing objects of type 'OrderDetails' was successful.")
                }
                catch (err: Error) {
                    Log.e(TAG, "Error when parsing JSON: " + err.localizedMessage)
                }
            }
            else {
                Log.e(TAG, "Error: Get request returned no response")
            }
        }
    }

    private fun fetchProducts(ctx: Context, sUrl: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val apiHandler = NorthwindAPIHandler()
            val result = apiHandler.getRequest(sUrl)
            if (result != null) {
                try {
                    //Parse result string JSON into data class
                    products = Klaxon().parseArray(result)
                    productsLoaded = true
                    Log.i(TAG, "Parsing of objects of type 'Product' was successful.")
                }
                catch (err: Error) {
                    Log.e(TAG, "Error when parsing JSON: " + err.localizedMessage)
                }
            }
            else {
                Log.e(TAG, "Error: Get request returned no response")
            }
        }
    }

    fun showOrders(view: View) {
        val constLayoutOrders = findViewById<ConstraintLayout>(R.id.constraintLayoutOrders)
        val constLayoutSuppliers = findViewById<ConstraintLayout>(R.id.constraintLayoutSuppliers)

        if (constLayoutOrders.visibility != View.VISIBLE) {
            constLayoutOrders.visibility = View.VISIBLE
            constLayoutSuppliers.visibility = View.INVISIBLE
        }
        else {
            constLayoutSuppliers.visibility = View.INVISIBLE
        }
    }

    fun showSuppliers(view: View) {
        val constLayoutOrders = findViewById<ConstraintLayout>(R.id.constraintLayoutOrders)
        val constLayoutSuppliers = findViewById<ConstraintLayout>(R.id.constraintLayoutSuppliers)

        if (constLayoutSuppliers.visibility != View.VISIBLE) {
            constLayoutSuppliers.visibility = View.VISIBLE
            constLayoutOrders.visibility = View.INVISIBLE
        }
        else {
            constLayoutOrders.visibility = View.INVISIBLE
        }
    }

    private fun fetchRequiredData() {
        fetchProducts(this, Routes.PRODUCTS)
        fetchOrderDetails(this, Routes.ORDER_DETAILS)

        fetchSuppliers(this, Routes.SUPPLIERS)
        fetchOrders(this, Routes.ORDERS)
    }
}