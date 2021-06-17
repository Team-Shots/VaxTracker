package com.hfad.vaxtracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // grabs the location object from the intent
        val locationObject = intent.getSerializableExtra("EXTRA_LOCATION") as dbLocation

        val nameView = findViewById<TextView>(R.id.location_name)
        nameView.text = locationObject.locName
        val addressView = findViewById<TextView>(R.id.location_address)
        addressView.text = locationObject.locAddress
        val phoneView = findViewById<Button>(R.id.call_phone_button)
        phoneView.text = " 📞 " + locationObject.locPhone

        // view other locations button redirects back to the locations page
        val viewOtherLocBtn : Button = findViewById(R.id.view_other_locations_button)
        viewOtherLocBtn.setOnClickListener {
            // closes the activity to return to the previous activity
            finish()
        }

        // call location button sends intent to dial phone number
        val callLocBtn: Button = findViewById(R.id.call_phone_button)
        callLocBtn.setOnClickListener {
            // because intent only dials the phone number in, it does not call permissions
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + locationObject.locPhone))
            startActivity(dialIntent)
        }

        val url = Uri.parse(locationObject.locWebsite).toString()

        // visit website button sends intent to open a web browser
        val visitWebsiteBtn: Button = findViewById(R.id.visit_website_button)
        visitWebsiteBtn.setOnClickListener {
//            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(locationObject.locWebsite))
//            startActivity(browserIntent)
            val intent = Intent(this, WebViewActivity :: class.java)
            intent.putExtra("URL", url)
            startActivity(intent)
        }
    }
}