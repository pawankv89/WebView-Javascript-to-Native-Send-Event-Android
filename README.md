# WebView Javascript to Native Send Event Android

=========

## WebView Javascript to Native Send Event Android in Kotlin.

------------
Added Some screens here.

![](https://github.com/pawankv89/WebView-Javascript-to-Native-Send-Event-Android/blob/master/images/flow.png)
![](https://github.com/pawankv89/WebView-Javascript-to-Native-Send-Event-Android/blob/master/images/screen_1.png)
![](https://github.com/pawankv89/WebView-Javascript-to-Native-Send-Event-Android/blob/master/images/screen_2.png)


## Usage
------------

####  MainActivity.kt

```kotlin

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


```

####  WebAppInterface.kt

```kotlin

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


```

####  GeocoderHelper.kt

```kotlin

package com.pk.webviewtonative

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.io.IOException

class GeocoderHelper private constructor() {

fun getLocationFromAddress(context: Context, strAddress: String): LatLng {

val coder = Geocoder(context)
val address: List<Address>?
var latlng = LatLng()

//Set Default 0,0
latlng.initWith(0.0,0.0)

try {
// May throw an IOException
address = coder.getFromLocationName(strAddress, 5)
if (address == null) {
return latlng
}

if (address.count() == 0) {
return latlng
}

val location = address[0]

val lat = location.latitude
val lng = location.longitude
Log.i("Lat", "" + lat)
Log.i("Lng", "" + lng)
latlng.initWith(lat,lng)

} catch (ex: IOException) {

ex.printStackTrace()
return latlng
}

return latlng
}

companion object {
val instance = GeocoderHelper()
}
}


```

####  LatLng.kt

```kotlin

package com.pk.webviewtonative

class LatLng  {

var latitude: Double = 0.toDouble()
var longitude: Double = 0.toDouble()
fun  initWith(latitude: Double, longitude: Double) {

this.latitude = latitude
this.longitude = longitude

}
}




```

####  addressform.html

```html

<!DOCTYPE html>
<html>
<head>
<title>iOS/Android</title>
<meta charset="UTF-8" name="viewport" content="width=device-width, initial-scale=1">
<style>
body {
background-color: black;
}
td {
color: yellow;
font-size: 20px;
}
input {
color: black;
border: 0;
font-size: 20px;
}
h1 {
color: green;
font-size: 20px;
}
</style>
<script type="text/javascript">
function geocodeAddress() {

var address = {
street: document.getElementById("street").value,
state: document.getElementById("state").value,
country: document.getElementById("country").value
};

if (window.NativeJavascriptInterface) {
// Call Android interface

window.NativeJavascriptInterface.postMessage(JSON.stringify(address)); //JSONObject: {"street":"","state":"","country":""}
//NativeJavascriptInterface.postMessage(JSON.stringify(address)); //JSONObject: {"street":"","state":"","country":""}

} else if (window.webkit && window.webkit.messageHandlers) {

// Call iOS interface

try {
webkit.messageHandlers.NativeJavascriptInterface.postMessage(address);
document.querySelector('h1').style.color = "green";
}
catch(err) {
document.querySelector('h1').style.color = "red";
}


} else {
// No Android or iOS interface found
console.log("No native APIs found.");
}
}

function changeBackgroundColor(colorText) {
document.body.style.background = colorText;
}

//<!-- Getting value from iOS OR Android -->
function setLatLon(lat, lon) {
document.getElementById("latitude").value = lat;
document.getElementById("longitude").value = lon;
}

</script>
</head>
<body  width = "100%" height = "100%">
<h1>WebView To Native Send Event using Javascript</h1>
<table cellspacing="10" width = "100%" height = "100%">
<tr><td>Address</td><td><input id="street" type="text" width="80" value = "Noida Sector 18" /></td></tr>
<tr><td>City</td><td><input id="city" type="text" width="50" value = "Noida" /></td></tr>
<tr><td>State</td><td><input id="state" type="text" width="10" value = "UP" /></td></tr>
<tr><td>Country</td><td><input id="country" type="text" width="10" value = "IN" /></td></tr>
<tr><td/><td><input type="submit" value="Geocode Address" onclick="geocodeAddress();"></td></tr>
<tr/>
<tr><td>Latitude</td><td><input id="latitude" type="text" width="50" /></td></tr>
<tr><td>Longitude</td><td><input id="longitude" type="text" width="50" /></td></tr>
</table>
</body>
</html>


```

## References

 https://firebase.google.com/docs/analytics/android/webview
 
 ## APK Links
 
 ![](https://github.com/pawankv89/WebView-Javascript-to-Native-Send-Event-Android/blob/master/images/app.apk)
 

## License

This code is distributed under the terms and conditions of the [MIT license](LICENSE).

## Change-log

A brief summary of each this release can be found in the [CHANGELOG](CHANGELOG.mdown). 
