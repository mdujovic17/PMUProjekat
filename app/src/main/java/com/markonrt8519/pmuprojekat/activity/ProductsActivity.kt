package com.markonrt8519.pmuprojekat.activity

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import com.markonrt8519.pmuprojekat.R
import com.markonrt8519.pmuprojekat.api.Routes
import com.markonrt8519.pmuprojekat.api.handler.NorthwindAPIHandler
import com.markonrt8519.pmuprojekat.data.product.Category
import com.markonrt8519.pmuprojekat.data.product.Product
import com.markonrt8519.pmuprojekat.data.product.Supplier
import com.markonrt8519.pmuprojekat.view.adapter.ProductsViewAdapter
import com.markonrt8519.pmuprojekat.view.model.ProductsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductsActivity : AppCompatActivity() {
    private val TAG = "ProductsActivity"
    val viewModel: ProductsViewModel by viewModels()
    var productsViewAdapter: ProductsViewAdapter? = null

    private var progressBar: ProgressBar? = null

    private var productsLoaded = false

    private val supplierPopupView by lazy { ListPopupWindow(this) }
    private val categoryPopupView by lazy { ListPopupWindow(this) }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        progressBar = findViewById<ProgressBar>(R.id.progressBar3)
        val button = findViewById<Button>(R.id.button4)
        val textView = findViewById<TextView>(R.id.textViewProducts)
        val listProductsView = findViewById<RecyclerView>(R.id.listProductsView)

        progressBar!!.visibility = View.VISIBLE
        button.visibility = View.INVISIBLE
        textView.visibility = View.INVISIBLE
        listProductsView.visibility = View.INVISIBLE

        val thread = Thread {

            fetchProducts(this, Routes.PRODUCTS)

            while (!productsLoaded);

            runOnUiThread {
                progressBar!!.visibility = View.GONE
                button.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                listProductsView.visibility = View.VISIBLE
            }

        }
        thread.start()
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

                    productsLoaded = true

                    Log.i(TAG, "Parsing of objects of type 'Product' was successful.")
                    withContext(Dispatchers.Main) {
                        viewModel.listProducts.value = products
                        val listProductsView = findViewById<RecyclerView>(R.id.listProductsView)
                        listProductsView.layoutManager = LinearLayoutManager(ctx)
                        productsViewAdapter = ProductsViewAdapter(ctx, viewModel.listProducts)
                        listProductsView.adapter = productsViewAdapter
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
        return products
    }

    fun showProductInput(view: View) {

        setContentView(R.layout.product_item)

        val supplierTextView = findViewById<TextView>(R.id.supplierTextView)
        val categoryTextView = findViewById<TextView>(R.id.categoryTextView)

        val apiHandler = NorthwindAPIHandler()

        val categoriesString: MutableList<String> = fetchCategories(Routes.CATEGORIES, apiHandler)
        val suppliersString: MutableList<String> = fetchSuppliers(Routes.SUPPLIERS, apiHandler)

        supplierPopupView.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, suppliersString))
        categoryPopupView.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, categoriesString))

        supplierTextView.setOnClickListener {
            supplierPopupView.anchorView = supplierTextView
            supplierPopupView.show()
        }

        categoryTextView.setOnClickListener {
            categoryPopupView.anchorView = categoryTextView
            categoryPopupView.show()
        }

        supplierPopupView.setOnItemClickListener { _, _, position, _ ->
            supplierTextView.text = suppliersString[position]
            supplierPopupView.dismiss()
        }

        categoryPopupView.setOnItemClickListener { _, _, position, _ ->
            categoryTextView.text = categoriesString[position]
            categoryPopupView.dismiss()
        }

        val productButton = findViewById<Button>(R.id.categoryAction)
        productButton.text = "Dodaj"
        val productName = findViewById<EditText>(R.id.productNameTextView)
        val unitPrice = findViewById<EditText>(R.id.pricePerUnitTextView)
        val quantityPerUnit = findViewById<EditText>(R.id.quantityPerUnitTextNumber)
        val unitsInStock = findViewById<EditText>(R.id.unitsInStockTextView)
        val unitsOnOrder = findViewById<EditText>(R.id.unitsOnOrderTextView)
        val reorderLevel = findViewById<EditText>(R.id.reorderLevelTextView)

        val discontinued = findViewById<Switch>(R.id.discontinuedSwitch)

        productButton.setOnClickListener {
            val newProduct = Product(0, productName.text.toString(), supplierTextView.text.toString().split(" ")[0].toInt(), categoryTextView.text.toString().split(" ")[0].toInt(), quantityPerUnit.text.toString(), unitPrice.text.toString().toDouble(), unitsInStock.text.toString().toInt(), unitsOnOrder.text.toString().toInt(), reorderLevel.text.toString().toInt(), discontinued.isActivated)
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val apiHandler = NorthwindAPIHandler()
                    val result = apiHandler.postRequest(
                        Routes.PRODUCTS, Klaxon().toJsonString(newProduct)
                    )
                    if (result != null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(getApplicationContext(), "Usepsno dodavanje novog proizvoda", Toast.LENGTH_LONG).show()
                        }
                        Log.i(TAG, "Parsing object of type 'Product' to JSON was successful.")
                    } else {
                        Log.e(TAG, "Error: POST request returned no response")
                    }
                } catch (err: Error) {
                    Log.e(TAG, "Error when parsing JSON: " + err.localizedMessage)
                }
            }
        }
    }

    private fun fetchSuppliers(sUrl: String, apiHandler: NorthwindAPIHandler): MutableList<String> {
        var suppliers: List<Supplier>?
        val supplierString: MutableList<String> = mutableListOf()
        lifecycleScope.launch(Dispatchers.IO) {
            val result = apiHandler.getRequest(sUrl)
            if (result != null) {
                try {
                    suppliers = Klaxon().parseArray(result)
                    Log.i(TAG, "Parsing of objects of type 'Supplier' was successful.")
                    for (s in suppliers!!) {
                        supplierString.add("" + s.supplierId + " " + s.companyName)
                    }
                }
                catch (err: Error) {
                    Log.e(TAG, "Error when parsing JSON: " + err.localizedMessage)
                }
            }
            else {
                Log.e(TAG, "Errpr: GET request returned no response")
            }
        }


        return supplierString
    }

    private fun fetchCategories(sUrl: String, apiHandler: NorthwindAPIHandler): MutableList<String> {
        var categories: List<Category>?
        val categoriesString: MutableList<String> = mutableListOf()
        lifecycleScope.launch(Dispatchers.IO) {
            val result = apiHandler.getRequest(sUrl)
            if (result != null) {
                try {
                    categories = Klaxon().parseArray(result)
                    Log.i(TAG, "Parsing of objects of type 'Category' was successful.")
                    for (c in categories!!) {
                        categoriesString.add("" + c.categoryId + " " + c.categoryName)
                    }
                }
                catch (err: Error) {
                    Log.e(TAG, "Error when parsing JSON: " + err.localizedMessage)
                }
            }
            else {
                Log.e(TAG, "Errpr: GET request returned no response")
            }
        }
        return categoriesString
    }
}