package com.seniorproject.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
//This class is for showing railway website in our app to allow user to book tickets
//It is run on our app
class WebActivity : AppCompatActivity() {
    private lateinit var Webview: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //hiding the bar
        supportActionBar!!.hide()
        setContentView(R.layout.activity_web)
        //binding
        Webview = findViewById(R.id.webVieww)
        Webview.settings.setJavaScriptEnabled(true)
        //loading
        Webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    view?.loadUrl(url)
                }
                return true
            }
        }
        //url of railway website
        Webview.loadUrl("https://dticket.railway.co.th/DTicketPublicWeb/home/Home")
    }
}