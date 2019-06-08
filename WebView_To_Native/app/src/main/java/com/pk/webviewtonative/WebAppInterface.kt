package com.pk.webviewtonative

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import org.json.JSONObject
import org.json.JSONException





class WebAppInterface// Instantiate the interface and set the context
internal constructor(internal var mContext: Context, var webView: WebView) {

    val androidVersion: Int
        @JavascriptInterface
        get() = android.os.Build.VERSION.SDK_INT

    // Show a toast from the web page
    @JavascriptInterface
    fun showToast(toast: String) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()

    }

    @JavascriptInterface
    fun showAndroidVersion(versionName: String) {
        Toast.makeText(mContext, versionName, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun postMessage(jsonString: String) {

        try {
            val jsonObject = JSONObject(jsonString)
            Log.i("JSONObject", "" + jsonObject)

            val address = jsonObject["street"].toString() + " " + jsonObject["state"].toString() + " " + jsonObject["country"].toString()
            Log.d("Address",address)

            val latlng = GeocoderHelper.instance.getLocationFromAddress(mContext, address)

            val latitude = java.lang.Double.toString(latlng.latitude)
            val longitude = java.lang.Double.toString(latlng.longitude)

            //Calling a javascript function in html page
            //webView.loadUrl("javascript:alert(setLatLon(" + latitude + "," + longitude +")")
            // Getting Error java.lang.RuntimeException: java.lang.Throwable: A WebView method was called on thread 'JavaBridge'

            webView.post( Runnable {
                  run() {

                    //Calling a javascript function in html page
                    webView.loadUrl("javascript:alert(setLatLon(" + latitude + "," + longitude +"))")
                }
            });


        } catch (err: JSONException) {
            Log.d("Error", err.toString())
        }

        //Toast.makeText(mContext, jsonObject["street"].toString(), Toast.LENGTH_SHORT).show()
    }

}
