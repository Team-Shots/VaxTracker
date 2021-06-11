package com.hfad.vaxtracker

import android.app.Activity
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast

class AccessDB : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get location from the intent
        val locationsId = intent.extras!![EXTRA_LOCATIONSID] as Int //ID of location user chose

        //Create a cursor
        val vaxTrackerDatabaseHelper: SQLiteOpenHelper = VaxTrackerDatabaseHelper(this)
        try {
            val db = vaxTrackerDatabaseHelper.readableDatabase
            val cursor = db.query(
                "LOCATIONS", arrayOf("PHARMACY", "ADDRESS", "PHONENUMBER", "WEBSITE"),
                "_id = ?", arrayOf(Integer.toString(locationsId)),
                null, null, null, null
            )

            //move to first record in the Cursor
            if (cursor.moveToFirst()) {

                //Get location details from cursor
                val pharmacyTEXT =
                    cursor.getString(0) //First item in the cursor (first column) and so on
                val addressTEXT = cursor.getString(1)
                val phonenumberTEXT = cursor.getString(2)
                val websiteTEXT = cursor.getString(3)

                //Populate location pharmacy name
                val pharmacy = findViewById<TextView>(R.id.pharmacy)
                //pharmacy.setTEXT(pharmacyTEXT); //Set pharmacy name from db

                //Populate location address
                val address = findViewById<TextView>(R.id.location_name)
                //address.setTEXT(addressTEXT);

                //Populate location phone number
                val phonenumber = findViewById<TextView>(R.id.phonenumber)
                //phonenumber.setTEXT(phonenumberTEXT);

                //Populate location website
                val website = findViewById<TextView>(R.id.website)
                //website.setTEXT(websiteTEXT);
            }
            cursor.close()
            db.close() //Close cursor & db
        } catch (e: SQLiteException) {
            val toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    companion object {
        const val EXTRA_LOCATIONSID = "locationsId"
    }
}