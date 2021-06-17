package com.hfad.vaxtracker

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.*


class ZipActivity : AppCompatActivity() {

    // variables to compare distance between locations
    private var currLat = 0.0
    private var currLong = 0.0
    private var locLat = 0.0
    private var locLang = 0.0
    // variable to store results of distance calculation
    private var results = FloatArray(3)

    // onCreate of activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locations)

        if (intent.hasExtra("EXTRA_ZIP")) {
            zipConverter(applicationContext, intent.getStringExtra("EXTRA_ZIP"))
        }
        else {
            // displays message when zip code field is empty
            Toast.makeText(this, "Please enter a zip code", Toast.LENGTH_LONG).show()
            // returns to the main activity requesting a zip code
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val btnBackToFirstPage: Button = findViewById(R.id.newZipButton)
        btnBackToFirstPage.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // uses geocoder to translate a zip code into latitude and longitude values
    private fun zipConverter(context: Context?, zipCode: String?) {
        val geocoder = Geocoder(this)
        try {
            val addresses = geocoder.getFromLocationName(zipCode, 1)
            if (addresses != null && !addresses.isEmpty()) {
                val address = addresses[0]
                // saves converted lat/long values
                currLat = address.latitude
                currLong = address.longitude

                // runs location comparison to find nearest locations
                findClosestLocations()
            } else {
                // displays message when Geocoder services are not available
                Toast.makeText(this, "Unable to geocode zipcode", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
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
    }
}