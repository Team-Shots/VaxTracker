package com.hfad.vaxtracker

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class ConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        //sets url to cdc website for info
        val url = "https://www.cdc.gov/coronavirus/2019-ncov/vaccines/expect.html?s_cid=10479:prepare%20for%20vaccine%20appointment:sem.ga:p:RG:GM:gen:PTN:FY21"

        //sends the user to cdc website. uses same view as before and returns to this page on exit
        val visitWebsiteBtn: Button = findViewById(R.id.cdcLink)
        visitWebsiteBtn.setOnClickListener {
            val intent = Intent(this, WebViewActivity :: class.java)
            intent.putExtra("URL", url)
            startActivity(intent)
        }

        val openCalendar: Button = findViewById(R.id.addToCalenderButton)
        openCalendar.setOnClickListener {
            val builder: Uri.Builder = CalendarContract.CONTENT_URI.buildUpon()
            builder.appendPath("time")
            ContentUris.appendId(builder, Calendar.getInstance().getTimeInMillis())
            val intent = Intent(Intent.ACTION_VIEW)
                .setData(builder.build())
            startActivity(intent)
        }


    }
}