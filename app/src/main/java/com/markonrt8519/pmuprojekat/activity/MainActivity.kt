package com.markonrt8519.pmuprojekat.activity

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

}