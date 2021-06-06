package com.hfad.vaxtracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView

class LocationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // variables to compare distance between locations
        var currLat: Double?
        var currLong: Double?
        var locLat: Double?
        var locLang: Double?
        // variable to store results of distance calculation
        var distance = FloatArray(3)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locations)

        // creates OnClickListener for the listview options
        val itemClickListener =
            OnItemClickListener { listView, itemView, position, id ->
                val intent = Intent(this, DetailsActivity::class.java)
                startActivity(intent)
            }

        // adds the listener to the locations listview
        val listView = findViewById<View>(R.id.list_locations) as ListView
        listView.onItemClickListener = itemClickListener


        //android.location.Location.distanceBetween(currLat, currLong, locLat, locLang, distance)

        val btnBackToFirstPage: Button = findViewById(R.id.newZipButton)
        btnBackToFirstPage.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}