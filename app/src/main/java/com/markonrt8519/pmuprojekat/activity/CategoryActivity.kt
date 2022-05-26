package com.markonrt8519.pmuprojekat.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

    val viewModel: CategoryViewModel by viewModels()
    var categoryViewAdapter:CategoryViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        fetchCategories(this, Routes.CATEGORIES)
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

                    withContext(Dispatchers.Main) {
                        viewModel.listCategory.value = categories
                        val listCategoryView = findViewById<RecyclerView>(R.id.listCategoryView)
                        listCategoryView.layoutManager = LinearLayoutManager(ctx)
                        categoryViewAdapter = CategoryViewAdapter(ctx, viewModel.listCategory)
                        listCategoryView.adapter = categoryViewAdapter
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
        return categories
    }

    fun showCategoryInput(view: View) {
        setContentView(R.layout.category_item)
        val categoryButton = findViewById<Button>(R.id.categoryAction)
        categoryButton.text = "Dodaj"
        val categoryName = findViewById<EditText>(R.id.categoryName)
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
                        print("Error: Post request returned no response")
                    }
                } catch (err: Error) {
                    print("Error when parsing JSON: " + err.localizedMessage)
                }
            }
        }
    }
}