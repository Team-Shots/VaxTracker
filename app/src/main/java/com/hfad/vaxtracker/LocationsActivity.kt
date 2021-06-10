package com.hfad.vaxtracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import java.io.IOException


class LocationsActivity : AppCompatActivity() {

    private val locReqCode = 10001

    // variables to compare distance between locations
    private var currLat = 0.0
    private var currLong = 0.0
    private var locLat = 37.3749
    private var locLang = -121.9555
    // variable to store results of distance calculation
    private var results = FloatArray(3)

    // variable calls fused location provider client to request, start and stop location updates
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // variable for location request object
    private lateinit var locationRequest: LocationRequest
    // variable for location call back object
    var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                // logs for testing location update values
                Log.d("LocationsActivity", "onLocationResult: " + location.toString())
                Log.println(Log.ASSERT, "LocationsActivity", "onLocationResult: " + location.toString())
                // stores devices' current latitude and longitude
                currLat = location.latitude
                currLong = location.longitude

                // distance between is calculated between the 2 points and stored into results array as a float
                Location.distanceBetween(currLat, currLong, locLat, locLang, results)

                // stores results value into distance variable
                val distance = results[0]
                Log.d("Dist", "Distance in meters: " + distance)

                // distance is calculated in meters, this converts distance to miles
                val miles = distance * 0.00062137
                Log.d("Dist", "Distance in Miles: " + miles)

                // runs function to find closest locations
                findClosestLocations()

                // converts address into lat/long values and stores it into an object
                val testAddress = getLocationFromAddress(applicationContext,"4401 4th Avenue South, Seattle, Washington 98134")

            }
            super.onLocationResult(locationResult)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locations)

        // variable calls fused location provider client to request, start and stop location updates
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // initializes/creates location request
        createLocationRequest()


        // creates OnClickListener for the listview options
        val itemClickListener =
            OnItemClickListener { listView, itemView, position, id ->
                val intent = Intent(this, DetailsActivity::class.java)
                startActivity(intent)
            }

        // adds the listener to the locations listview
        val listView = findViewById<View>(R.id.list_locations) as ListView
        listView.onItemClickListener = itemClickListener





        val btnBackToFirstPage: Button = findViewById(R.id.newZipButton)
        btnBackToFirstPage.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    // onStart of activity, starts location updates if permission was granted
    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkSettingsAndStartLocationUpdates()
        } else {
            askLocationPermission()
        }
    }

    // onStop of the activity, stops location updates
    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    //Create the location request and set the parameters
    fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 4000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    // begins location updates if settings are set
    private fun checkSettingsAndStartLocationUpdates() {
        val request = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val client = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask: Task<LocationSettingsResponse> = client.checkLocationSettings(request)
        locationSettingsResponseTask.addOnSuccessListener {
            // device settings are set, starts location updates
            startLocationUpdates()
        }
        locationSettingsResponseTask.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this@LocationsActivity, 1001)
                } catch (ex: SendIntentException) {
                    ex.printStackTrace()
                }
            }
        }
    }

    // requests locations services permission from user and returns results
    private fun askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("LocationsActivity", "askLocationPermission: Alert user permission")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locReqCode)
            }
            else
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locReqCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == locReqCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSettingsAndStartLocationUpdates()
            } else {
                Toast.makeText(this, "This app requires permissions", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // starts location updates with request parameters and callback
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    // stops location updates
    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    // uses geocoder to translate an address into latitude and longitude values
    // code found from https://stackoverflow.com/questions/3574644/how-can-i-find-the-latitude-and-longitude-from-address
    fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var loc: LatLng? = null
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location: Address = address[0]
            loc = LatLng(location.getLatitude(), location.getLongitude())

        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return loc
    }

    fun findClosestLocations() {

        /* psuedo code

        get the locations database

        store top 3 locations in an array

        for each loop
            goes through each location in the database
            use getLocationFromAddress() to convert address to lat and long values
            stores the lat and long vals
            Location.distanceBetween(currLat, currLong, locLat, locLang, results)
            if distance is smaller than any of the top 3 current locations, replace the location

        after 3 locations are found
        get and set the xml IDs

         */

    }

}