package com.hfad.vaxtracker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class VaxTrackerDatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) { //gets called when db is first created, use to create table & insert data
        updateMyDatabase(db, 0, DB_VERSION)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        updateMyDatabase(db, oldVersion, newVersion)
    }

    private fun updateMyDatabase(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 1) {
            db.execSQL(
                "CREATE TABLE LOCATIONS (_id INTEGER PRIMARY KEY AUTOINCREMENT, " //create drink table
                        + "PHARMACY TEXT, "
                        + "ADDRESS TEXT, "
                        + "PHONENUMBER TEXT, "
                        + "WEBSITE TEXT);"
            )
            insertLocation(
                db,
                "Pharmaca Integrative Pharmacy",
                "15840 Redmond Way Redmond, WA 98052",
                "(425) 885-2323",
                "https://pharmaca.as.me/redmond"
            )
            insertLocation(
                db, "Overlake Medical Center",
                "1035 116th Ave NE, Bellevue, WA 98004",
                "(425) 688-5000",
                "https://www.overlakehospital.org/safe-in-our-care/covid-19-vaccine-information"
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
                db,
                "Safeway Pharmacy",
                "12519 NE 85th St Kirkland, WA 98033",
                "(425) 822-9235",
                "https://local.pharmacy.safeway.com/wa/kirkland/12519-ne-85th-st.html"
            )
            insertLocation(
                db,
                "Rite Aid",
                "20330 Ballinger Way NE, Shoreline, WA 98155",
                "(206) 368-0034",
                "https://www.riteaid.com/pharmacy/covid-qualifier"
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
                "Sea Mar Everett Medical Clinic",
                "1920 100th St SE building b, Everett, WA 98208",
                "(425) 312-0202",
                "https://www.seamar.org/covid-vaccine.html"
            )
            insertLocation(
                db,
                "UW Medicine/UW Medical Center",
                "1959 Northeast Pacific Street, Seattle, Washington 98195",
                "(206) 598-3300",
                "https://mychartos.uwmedicine.org/prod01/covid19#/"
            )
            insertLocation(
                db,
                "CVS Pharmacy",
                "107 Bellevue Way SE, Bellevue, WA 98004",
                "(425) 454-1818",
                "https://www.cvs.com/immunizations/covid-19-vaccine?icid=cvs-home-hero1-link1-coronavirus-vaccine"
            )
            insertLocation(
                db,
                "Medical Center Pharmacy",
                "450 NW Gilman Blvd # 107, Issaquah, WA 98027",
                "(425) 392-8650",
                "https://issaquahpharmacy.com/"
            )
            insertLocation(
                db,
                "Albertsons Pharmacy",
                "3066 Issaquah-Pine Lake Rd SE, Sammamish, WA 98075",
                " (425) 391-1582" ,
                "https://www.albertsons.com/vaccinations/home?icmpid=alb_yxt_r5_cvhc_ih"
            )
        }
    }

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