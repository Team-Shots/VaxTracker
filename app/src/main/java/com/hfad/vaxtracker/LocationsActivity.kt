package com.hfad.vaxtracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class LocationsActivity : AppCompatActivity() {

    //Location services
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {

        // variables to compare distance between locations
        var currLat: Double?
        var currLong: Double?
        var locLat: Double?
        var locLang: Double?
        // variable to store results of distance calculation
        var distance = FloatArray(3)

        //Location services client
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

        //Check here to see if we have permissions
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
            }


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




        //Check current location settings
        val builder = LocationSettingsRequest.Builder()


        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())


        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
        }


    }

}