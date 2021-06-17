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
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import java.io.IOException
import java.io.Serializable
import java.util.*


class LocationsActivity : AppCompatActivity() {

    // location request code for location permission requests
    private val locReqCode = 10001

    // variables to compare distance between locations
    private var currLat = 0.0
    private var currLong = 0.0
    private var locLat = 0.0
    private var locLang = 0.0
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
                // stores devices' current latitude and longitude
                currLat = location.latitude
                currLong = location.longitude

                // runs function to find closest locations
                findClosestLocations()
            }
            super.onLocationResult(locationResult)
        }
    }

    // onCreate of activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locations)

        // variable calls fused location provider client to request, start and stop location updates
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // initializes/creates location request
        createLocationRequest()

        val btnBackToFirstPage: Button = findViewById(R.id.goBackButton)
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

    // returns result of permission request
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

    // goes through the database, calculates distance for each location and sorts the closest locations
    fun findClosestLocations() {

        // get the locations database
        val helper = VaxTrackerDatabaseHelper(applicationContext)
        var db = helper.readableDatabase
        var cursor = db.rawQuery("SELECT * FROM LOCATIONS", null)

        // list stores locations with distances
        var locationsArray: MutableList<dbLocation> = mutableListOf()

        cursor.moveToFirst()
        // goes through each location in the database
        for (i in 0 until cursor.count) {

            // converts location address to lat and long values
            val convertedAddress = getLocationFromAddress(applicationContext, cursor.getString(2))
            // stores the lat and long vals
            if (convertedAddress != null) {
                locLat = convertedAddress.latitude
                locLang = convertedAddress.longitude
            }

            // gets distance between the location and user
            Location.distanceBetween(currLat, currLong, locLat, locLang, results)

            // stores distance in miles
            val miles = results[0] * 0.00062137

            // create location object using calculated distance
            val dbLocationObj = dbLocation(
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                miles
            )

            // addres location object to the array
            locationsArray.add(dbLocationObj)

            // moves cursor to next object in the database
            cursor.moveToNext()
        }

        // sorts the locations list
        locationsArray.sortBy { it.dist }
        // goes through each location in the list
        // formats distance and logs each location
        for (j in locationsArray) {
            j.dist = String.format("%.2f", j.dist).toDouble()
            Log.d("Sorted List", j.locName + ": " + j.dist + " miles away")
        }

        // closes the cursor and db connection
        cursor.close()
        db.close()

        // after locations are sorted, runs set views function
        setLocationViews(locationsArray)
    }

    // sets text, listener and intents for each location button
    fun setLocationViews(locationsArray: MutableList<dbLocation>) {
        // grabs button views to modify
        val loc1 = findViewById<Button>(R.id.locationButton1)
        val loc2 = findViewById<Button>(R.id.locationButton2)
        val loc3 = findViewById<Button>(R.id.locationButton3)
        // sets button texts to display name and distance of each location
        loc1.text = locationsArray[0].locName + ": " + locationsArray[0].dist + " miles away"
        loc2.text = locationsArray[1].locName + ": " + locationsArray[1].dist + " miles away"
        loc3.text = locationsArray[2].locName + ": " + locationsArray[2].dist + " miles away"

        // inserts top 3 location objects into the buttons intent
        val loc1Button: Button = findViewById(R.id.locationButton1)
        loc1Button.setOnClickListener {
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("EXTRA_LOCATION", locationsArray[0])
            startActivity(intent)
        }
        val loc2Button: Button = findViewById(R.id.locationButton2)
        loc2Button.setOnClickListener {
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("EXTRA_LOCATION", locationsArray[1])
            startActivity(intent)
        }
        val loc3Button: Button = findViewById(R.id.locationButton3)
        loc3Button.setOnClickListener {
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("EXTRA_LOCATION", locationsArray[2])
            startActivity(intent)
        }

        // stops the location updates after all required functions are finished
        stopLocationUpdates()
    }
}

// class object for location aqcuired from the database
class dbLocation(name: String, address: String, phone: String , website: String, distance: Double) : Serializable {
    val locName = name
    val locAddress = address
    val locPhone = phone
    val locWebsite = website
    var dist = distance
}
