package com.hfad.vaxtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) { //called when activity is first created
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) //which layout activity uses
        val button:Button = findViewById(R.id.button) //find location button
        button.setOnClickListener {
            val intent = Intent (this@MainActivity, LocationsActivity::class.java) //button directs to Locations page when clicked
            startActivity(intent)

        val button2:Button = findViewById(R.id.button2) //use current location button
        button2.setOnClickListener {
            val intent2 = Intent (this@MainActivity, LocationsActivity::class.java) //button directs to Locations page when clicked
            startActivity(intent2)
        }

    }


}
}