package com.pk.webviewtonative

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Load HTML File in WebView Usiing Javascript
        loadWebViewWithHTMLFile()
    }

    fun loadWebViewWithHTMLFile(){

        //Create WebView At Run Time
        var webView =  WebView(this)
        //Create WebView At Run Time
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("file:///android_asset/addressform.html")
        setContentView(webView)

        webView.addJavascriptInterface(WebAppInterface(this, webView), "NativeJavascriptInterface") // To call methods in Android from using js in the html, NativeJavascriptInterface.showToast, NativeJavascriptInterface.getAndroidVersion etc
        val webSettings = webView.getSettings()
        webSettings.setJavaScriptEnabled(true)
        webView.setWebViewClient(MyWebViewClient())
        webView.setWebChromeClient(MyWebChromeClient())
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            //Calling a javascript function in html page
            view.loadUrl("javascript:alert(showVersion('called by Android'))")
        }
    }

    private inner class MyWebChromeClient : WebChromeClient() {
        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            Log.d("LogTag", message)
            result.confirm()
            return true
        }
    }

}
