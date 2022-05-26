package com.markonrt8519.pmuprojekat.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.beust.klaxon.Klaxon
import com.markonrt8519.pmuprojekat.R
import com.markonrt8519.pmuprojekat.api.Routes
import com.markonrt8519.pmuprojekat.api.handler.NorthwindAPIHandler
import com.markonrt8519.pmuprojekat.data.product.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_item)
        val categoryId = intent.extras!!["categoryId"]
        fetchCategory(Routes.CATEGORIES + categoryId)
    }

    private fun deleteCategory(categoryId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiHandler = NorthwindAPIHandler()
                val result = apiHandler.deleteRequest(Routes.CATEGORIES + categoryId)
                if (result != null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Uspesno obrisana kategorija", Toast.LENGTH_LONG).show()
                    }
                }
                else {
                    print("Error: Delete request returned no response")
                }
            }
            catch (err: Error) {
                print("Error when parsing JSON: " + err.localizedMessage)
            }


        }
    }

    private fun saveCategory(categoryId: Int, categoryName: String, categoryDescription: String) {
        val alteredCategory = Category(categoryId, categoryName, categoryDescription)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiHandler = NorthwindAPIHandler()
                val result = apiHandler.putRequest(Routes.CATEGORIES + categoryId, Klaxon().toJsonString(alteredCategory))
                if (result != null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Uspesno izmenjeni podaci", Toast.LENGTH_LONG).show()
                    }
                }
                else {
                    print("Error: Put request returned no response")
                }
            }
            catch (err: Error) {
                print("Error when parsing JSON: " + err.localizedMessage)
            }
        }
    }

    private fun fetchCategory(sUrl: String): Category? {
        var category: Category? = null
        lifecycleScope.launch(Dispatchers.IO) {
            val apiHandler = NorthwindAPIHandler()
            val result = apiHandler.getRequest(sUrl)
            if (result != null) {
                try {
                    // Parse result string JSON to data class
                    category = Klaxon().parse(result)

                    withContext(Dispatchers.Main) {
                        val categoryName = findViewById<EditText>(R.id.categoryName)
                        categoryName.setText(category!!.categoryName)
                        val categoryDescription = findViewById<EditText>(R.id.categoryDescription)
                        categoryDescription.setText(category!!.description)
                        val brisanjeKategorije = findViewById<Button>(R.id.categoryAction2)
                        brisanjeKategorije.text = "Obrisi"
                        brisanjeKategorije.isVisible = true
                        brisanjeKategorije.setOnClickListener { deleteCategory(category!!.categoryId) }
                        val snimiIzmene = findViewById<Button>(R.id.categoryAction)
                        snimiIzmene.text = "Snimi"
                        snimiIzmene.setOnClickListener {
                            saveCategory(
                                category!!.categoryId,
                                categoryName.text.toString(),
                                categoryDescription.text.toString()
                            )
                        }

                    }
                } catch (err: Error) {
                    print("Error when parsing JSON: " + err.localizedMessage)
                }
            }
            else {
                print("Error: Get request returned no response")
            }
        }
        return category
    }
}