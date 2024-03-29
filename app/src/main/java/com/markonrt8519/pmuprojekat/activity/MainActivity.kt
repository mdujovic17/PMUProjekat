package com.markonrt8519.pmuprojekat.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.markonrt8519.pmuprojekat.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openActivityCategories(view: View) {
        val intent = Intent(this, CategoryActivity::class.java)
        startActivity(intent)
    }


    fun openActivityProducts(view: View) {
        val intent = Intent(this, ProductsActivity::class.java)
        startActivity(intent)
    }

    fun openActivityOSGroups(view: View) {
        val intent = Intent(this, SortedViewActivity::class.java)
        startActivity(intent)
    }
}