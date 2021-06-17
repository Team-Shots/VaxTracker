package com.hfad.vaxtracker


import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // stores the values inputted in the zipcode entry field
        val zipField = findViewById<View>(R.id.editTextZipField) as EditText

        // button launches search activity with zip code
        val btnOpenSecondPage: Button = findViewById(R.id.zipSearchButton)
        btnOpenSecondPage.setOnClickListener {
            // grabs zip code from entry field and puts it as extra in the intent
            val zipCode = zipField.text.toString()
            val intent = Intent(this, ZipActivity::class.java)
            // puts extra only if entry field is not empty
            if (zipCode != "") {
                intent.putExtra("EXTRA_ZIP", zipCode)
            }
            startActivity(intent)
        }

        // button launches search activity with gps
        val btnOpenFourthPage: Button = findViewById(R.id.gpsSearchButton)
        btnOpenFourthPage.setOnClickListener {
            val intent = Intent(this, LocationsActivity::class.java)
            startActivity(intent)
        }
    }
}