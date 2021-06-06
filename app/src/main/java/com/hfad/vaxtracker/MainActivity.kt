package com.hfad.vaxtracker


import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnOpenSecondPage: Button = findViewById(R.id.button)
        btnOpenSecondPage.setOnClickListener {
            val intent = Intent(this, LocationsActivity::class.java)
            startActivity(intent)
        }

        val btnOpenFourthPage: Button = findViewById(R.id.button2)
        btnOpenFourthPage.setOnClickListener {
            val intent = Intent(this, LocationsActivity::class.java)
            startActivity(intent)
        }
    }
}