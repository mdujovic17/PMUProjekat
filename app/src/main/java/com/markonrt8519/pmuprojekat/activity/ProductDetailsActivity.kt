package com.markonrt8519.pmuprojekat.activity

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.beust.klaxon.Klaxon
import com.markonrt8519.pmuprojekat.R
import com.markonrt8519.pmuprojekat.api.Routes
import com.markonrt8519.pmuprojekat.api.handler.NorthwindAPIHandler
import com.markonrt8519.pmuprojekat.data.product.Category
import com.markonrt8519.pmuprojekat.data.product.Product
import com.markonrt8519.pmuprojekat.data.product.Supplier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProductDetailsActivity : AppCompatActivity() {

    private val supplierPopupView by lazy { ListPopupWindow(this) }
    private val categoryPopupView by lazy { ListPopupWindow(this) }

    private var categoriesString: MutableList<String> = mutableListOf()
    private var suppliersString: MutableList<String> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_item)

        val supplierTextView = findViewById<TextView>(R.id.supplierTextView)
        val categoryTextView = findViewById<TextView>(R.id.categoryTextView)

        val apiHandler = NorthwindAPIHandler()

        categoriesString = fetchCategories(Routes.CATEGORIES, apiHandler)
        suppliersString = fetchSuppliers(Routes.SUPPLIERS, apiHandler)

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

        val productId: Any?
        if (intent.extras != null) {
            productId = intent.extras!!["productId"]
            fetchProduct(Routes.PRODUCTS + productId, supplierTextView, categoryTextView)
        }
    }

    private fun deleteProduct(productId: Int) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Obrisi proizvod")
        dialog.setMessage("Da li ste sigurni da zelite da obrisete ovaj proizvod? Ova radnja se ne moze opozvati!")

        dialog.setNegativeButton(R.string.no) { _, _ ->
            Toast.makeText(applicationContext, "Brisanje otkazano", Toast.LENGTH_LONG).show()
        }

        dialog.setPositiveButton(R.string.yes) { _, _ ->
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val apiHandler = NorthwindAPIHandler()
                    val result = apiHandler.deleteRequest(Routes.PRODUCTS + productId)
                    if (result != null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "Uspesno obrisan proizvod", Toast.LENGTH_LONG).show()
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

        dialog.show()
    }

    private fun saveProduct(productId: Int, productName: String, supplierId: Int, categoryId: Int, quantityPerUnit: String, unitPrice: Double, unitsInStock: Int, unitsOnOrder: Int?, reorderLevel: Int, discontinued: Boolean) {
        val alteredProduct = Product(productId, productName, supplierId, categoryId, quantityPerUnit, unitPrice, unitsInStock, unitsOnOrder, reorderLevel, discontinued)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiHandler = NorthwindAPIHandler()
                val result = apiHandler.putRequest(Routes.PRODUCTS + productId, Klaxon().toJsonString(alteredProduct))
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

    private fun fetchProduct(sUrl: String, supplierTextView: TextView, categoryTextView: TextView): Product? {
        Thread.sleep(1000) //For syncing
        var product: Product? = null
        lifecycleScope.launch(Dispatchers.IO) {
            val apiHandler = NorthwindAPIHandler()
            val result = apiHandler.getRequest(sUrl)
            if (result != null) {
                try {
                    product = Klaxon().parse(result)


                    withContext(Dispatchers.Main) {
                        val productName = findViewById<EditText>(R.id.productNameTextView)
                        productName.setText(product!!.productName)
                        val unitPrice = findViewById<EditText>(R.id.pricePerUnitTextView)
                        unitPrice.setText(product!!.unitPrice.toString())
                        val quantityPerUnit = findViewById<EditText>(R.id.quantityPerUnitTextNumber)
                        quantityPerUnit.setText(product!!.quantityPerUnit)
                        val unitsInStock = findViewById<EditText>(R.id.unitsInStockTextView)
                        unitsInStock.setText(product!!.unitsInStock.toString())
                        val unitsOnOrder = findViewById<EditText>(R.id.unitsOnOrderTextView)
                        unitsOnOrder.setText(product!!.unitsOnOrder.toString())
                        val reorderLevel = findViewById<EditText>(R.id.reorderLevelTextView)
                        reorderLevel.setText(product!!.reorderLevel.toString())

                        val supplierId = findViewById<TextView>(R.id.supplierTextView)
                        val categoryId = findViewById<TextView>(R.id.categoryTextView)

                        for (i in 0 until suppliersString.size) {
                            if (product!!.supplierId == suppliersString[i].split(" ")[0].toInt()) {
                                supplierId.text = suppliersString[i]
                            }
                        }

                        for (i in 0 until categoriesString.size) {
                            if (product!!.categoryId == categoriesString[i].split(" ")[0].toInt()) {
                                categoryId.text = categoriesString[i]
                            }
                        }

                        val discontinued = findViewById<Switch>(R.id.discontinuedSwitch)
                        discontinued.isChecked = product!!.discontinued == true

                        val productDeletion = findViewById<Button>(R.id.categoryAction2)
                        val saveChanges = findViewById<Button>(R.id.categoryAction)

                        productDeletion.text = "Obrisi"
                        productDeletion.isVisible = true
                        productDeletion.setOnClickListener { product!!.productId?.let { it1 ->
                            deleteProduct(
                                it1
                            )
                        } }

                        saveChanges.text = "Snimi"
                        saveChanges.setOnClickListener {
                            product!!.productId?.let { it1 ->
                                saveProduct(
                                    it1,
                                    productName.text.toString(),
                                    supplierTextView.text.toString().split(" ")[0].toInt(),
                                    categoryTextView.text.toString().split(" ")[0].toInt(),
                                    quantityPerUnit.text.toString(),
                                    unitPrice.text.toString().toDouble(),
                                    unitsInStock.text.toString().toInt(),
                                    unitsOnOrder.text.toString().toInt(),
                                    reorderLevel.text.toString().toInt(),
                                    discontinued.isActivated
                                )
                            }
                        }
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
        return product
    }

//    fun populateSpinners() {
//        val categoryId: Spinner = findViewById<Spinner>(R.id.categoriesSpinner)
//        val supplierId = findViewById<Spinner>(R.id.suppliersSpinner)
//
//        val apiHandler = NorthwindAPIHandler()
//
//        //val categories: List<Category>? = fetchCategories(Routes.CATEGORIES, apiHandler)
//        //val suppliers: List<Supplier> = fetchSuppliers(Routes.SUPPLIERS, apiHandler)
//
//        val categoriesString: MutableList<String> = fetchCategories(Routes.CATEGORIES, apiHandler)
//        val suppliersString: MutableList<String> = fetchSuppliers(Routes.SUPPLIERS, apiHandler)
//
//
//        val categoryArrayAdapter = ArrayAdapter(this, androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, categoriesString)
//        val supplierArrayAdapter = ArrayAdapter(this, androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, suppliersString)
//
//        categoryArrayAdapter.setDropDownViewResource(androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item)
//        supplierArrayAdapter.setDropDownViewResource(androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item)
//
//        categoryId.adapter = categoryArrayAdapter
//        supplierId.adapter = supplierArrayAdapter
//
//        categoryId.onItemSelectedListener = this
//        supplierId.onItemSelectedListener = this
//
//        categoryArrayAdapter.notifyDataSetChanged()
//        supplierArrayAdapter.notifyDataSetChanged()
//    }

    private fun fetchSuppliers(sUrl: String, apiHandler: NorthwindAPIHandler): MutableList<String> {
        var suppliers: List<Supplier>?
        val supplierString: MutableList<String> = mutableListOf()
        lifecycleScope.launch(Dispatchers.IO) {
            val result = apiHandler.getRequest(sUrl)
            if (result != null) {
                try {
                    suppliers = Klaxon().parseArray(result)

                    for (s in suppliers!!) {
                        supplierString.add("" + s.supplierId + " " + s.companyName)
                    }
                }
                catch (err: Error) {
                    print("Error when parsing JSON: " + err.localizedMessage)
                }
            }
            else {
                print("Errpr: GET request returned no response")
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

                    for (c in categories!!) {
                        categoriesString.add("" + c.categoryId + " " + c.categoryName)
                    }
                }
                catch (err: Error) {
                    print("Error when parsing JSON: " + err.localizedMessage)
                }
            }
            else {
                print("Errpr: GET request returned no response")
            }
        }
        return categoriesString
    }
}