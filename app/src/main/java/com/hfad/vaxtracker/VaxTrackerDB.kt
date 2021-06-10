package com.hfad.vaxtracker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class VaxTrackerDatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) { //gets called when db is first created, use to create table & insert data
        db.execSQL(
            "CREATE TABLE LOCATIONS (_id INTEGER PRIMARY KEY AUTOINCREMENT, " //create drink table
                    + "PHARMACY TEXT, "
                    + "ADDRESS TEXT, "
                    + "PHONENUMBER TEXT, "
                    + "WEBSITE TEXT);"
        )
        insertLocation(
            db, "Bartell Drug", "1628 5th Avenue, Seattle, Washington 98101", "(206) 622-0582",
            "https://www.bartelldrugs.com/store/downtown/#scheduler"
        )
        insertLocation(
            db, "Walgreen Drug Store", "222 Pike Street, Seattle, Washington", "(206) 903-8392",
            "https://www.walgreens.com/findcare/vaccination/covid-19/location-screening"
        )
        insertLocation(
            db,
            "One Medical",
            "1600 7th Avenue Suite 110, Seattle, Washington 98101",
            "1-888-663-6331",
            "https://onemedical.com/SEAVAX/"
        )
        insertLocation(
            db,
            "Seattle Infectious Disease Clinic",
            "509 Olive Way Suite 752, Seattle, Washington 98101",
            "(206) 682-3444",
            "https://covid-19vaccine-100874.square.site/"
        )
        insertLocation(
            db, "CVS", "1401 2nd Avenue, Seattle, Washington 98101", "(206) 494-3251",
            "https://www.cvs.com/vaccine/intake/store/covid-screener/covid-qns"
        )
        insertLocation(
            db,
            "Genoa Healthcare",
            "4200 Stone Way North Suite P1, Seattle, Washington 98103",
            "(206) 480-0100",
            "https://a.flexbooker.com/widget/ac492cfe-aaa7-4b28-aba9-6337cb396e22/employee/Seattle20177-clinica#chooseService"
        )
        insertLocation(
            db,
            "Kelley-Ross Pharmacy",
            "904 7th Avenue #103, Seattle, Washington 98104",
            "(206) 488-2002",
            "https://www.kelley-ross.com/polyclinic/covid-19-vaccine/"
        )
        insertLocation(
            db,
            "Fred Hutch/SCCA Covid-19 Vaccine Program",
            "1100 Fairview Avenue North, Seattle, Washington 98109",
            "(206) 667-5000",
            "https://www.solvhealth.com/book-online/AM8450"
        )
        insertLocation(
            db,
            "UW Medicine/UW Medical Center",
            "1959 Northeast Pacific Street, Seattle, Washington 98195",
            "(206) 598-3300",
            "https://mychartos.uwmedicine.org/prod01/covid19#/"
        )
        insertLocation(
            db, "Costco", "4401 4th Avenue South, Seattle, Washington 98134", "(206) 682-6244",
            "https://book.appointment-plus.com/d133yng2#/"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {
        private const val DB_NAME = "VaxTracker" //name of database
        private const val DB_VERSION = 1 //version of db
        private fun insertLocation(
            db: SQLiteDatabase,
            pharmacy: String,
            address: String,
            phoneNumber: String,
            website: String
        ) { //method to insert locations into db
            val locationValues = ContentValues()
            locationValues.put("PHARMACY", pharmacy)
            locationValues.put("ADDRESS", address)
            locationValues.put("PHONENUMBER", phoneNumber)
            locationValues.put("WEBSITE", website)
            db.insert("LOCATIONS", null, locationValues)
        }
    }
}