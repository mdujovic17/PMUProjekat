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
import com.markonrt8519.pmuprojekat.view.adapter.CategoryViewAdapter
import com.markonrt8519.pmuprojekat.view.model.CategoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryActivity : AppCompatActivity() {
    private val TAG = "SortedView"

    private var progressBar: ProgressBar? = null

    private var categoriesLoaded = false

    val viewModel: CategoryViewModel by viewModels()
    var categoryViewAdapter:CategoryViewAdapter? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_category)

        progressBar = findViewById<ProgressBar>(R.id.progressBar2)
        val button = findViewById<Button>(R.id.button2)
        val textView = findViewById<TextView>(R.id.textViewCategories)
        val listCategoryView = findViewById<RecyclerView>(R.id.listCategoryView)

        progressBar!!.visibility = View.VISIBLE
        button.visibility = View.INVISIBLE
        textView.visibility = View.INVISIBLE
        listCategoryView.visibility = View.INVISIBLE

        val thread = Thread {

            fetchCategories(this, Routes.CATEGORIES)

            while (!categoriesLoaded);

            runOnUiThread {
                progressBar!!.visibility = View.GONE
                button.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                listCategoryView.visibility = View.VISIBLE
            }

        }
        thread.start()
    }

    private fun fetchCategories(ctx: Context, sUrl: String): List<Category>? {
        var categories: List<Category>? = null
        lifecycleScope.launch(Dispatchers.IO) {
            val apiHandler = NorthwindAPIHandler()
            val result = apiHandler.getRequest(sUrl)
            if (result != null) {
                try {
                    //Parse result string JSON into data class
                    categories = Klaxon().parseArray(result)
                    categoriesLoaded = true
                    Log.i(TAG, "Parsing of objects of type 'Category' was successful.")
                    withContext(Dispatchers.Main) {
                        viewModel.listCategory.value = categories
                        val listCategoryView = findViewById<RecyclerView>(R.id.listCategoryView)
                        listCategoryView.layoutManager = LinearLayoutManager(ctx)
                        categoryViewAdapter = CategoryViewAdapter(ctx, viewModel.listCategory)
                        listCategoryView.adapter = categoryViewAdapter
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
        return categories
    }

    fun showCategoryInput(view: View) {
        setContentView(R.layout.category_item)
        val categoryButton = findViewById<Button>(R.id.categoryAction)
        categoryButton.text = "Dodaj"
        val categoryName = findViewById<EditText>(R.id.orderId)
        val categoryDescription = findViewById<EditText>(R.id.categoryDescription)
        categoryButton.setOnClickListener {
            val newCategory = Category(0, categoryName.text.toString(), categoryDescription.text.toString())
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val apiHandler = NorthwindAPIHandler()
                    val result = apiHandler.postRequest(Routes.CATEGORIES, Klaxon().toJsonString(newCategory)
                    )
                    if (result != null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(getApplicationContext(), "Usepsno dodavanje kategorije", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Log.e(TAG, "Error: Post request returned no response")
                    }
                } catch (err: Error) {
                    Log.e(TAG, "Error when parsing JSON: " + err.localizedMessage)
                }
            }
        }
    }
}