package com.hfad.vaxtracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // view other locations button redirects back to the locations page
        val viewOtherLocBtn : Button = findViewById(R.id.view_other_locations_button)
        viewOtherLocBtn.setOnClickListener {
            val intent = Intent(this, LocationsActivity :: class.java )
            startActivity(intent)
        }

        // call location button sends intent to dial phone number
        val callLocBtn: Button = findViewById(R.id.call_phone_button)
        callLocBtn.setOnClickListener {
            // because intent only dials the phone number in, it does not call permissions
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + "555-555-5555"))
            startActivity(dialIntent)
        }

        // visit website button sends intent to open a web browser
        val visitWebsiteBtn: Button = findViewById(R.id.visit_website_button)
        visitWebsiteBtn.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com"))
            startActivity(browserIntent)
        }
    }
}