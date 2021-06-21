package com.hfad.vaxtracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient


class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val url = intent.getSerializableExtra("URL") as String

        // visit website button sends intent to open a webview in a new activity
        //val visitWebsiteBtn: Button = findViewById(R.id.visit_website_button)
        //visitWebsiteBtn.setOnClickListener {
        //val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(locationObject.locWebsite))
        //startActivity(browserIntent)
        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.settings.javaScriptEnabled = true
        myWebView.settings.domStorageEnabled = true
        myWebView.webViewClient = WebViewClient()
        myWebView.loadUrl(url)

        val visitConfirmationActivity: Button = findViewById(R.id.close_button)
        visitConfirmationActivity.setOnClickListener {
            val intent = Intent(this, ConfirmationActivity :: class.java )
            startActivity(intent)
        }
    }
}