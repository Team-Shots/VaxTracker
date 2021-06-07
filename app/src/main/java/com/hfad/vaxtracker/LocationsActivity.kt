package com.hfad.vaxtracker

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

class LocationsActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {

        // variables to compare distance between locations
        var currLat: Double?
        var currLong: Double?
        var locLat: Double?
        var locLang: Double?
        // variable to store results of distance calculation
        var distance = FloatArray(3)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

        

        //Create the location request and set the parameters

        fun createLocationRequest() {
            val locationRequest = LocationRequest.create()?.apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        }

    }
}