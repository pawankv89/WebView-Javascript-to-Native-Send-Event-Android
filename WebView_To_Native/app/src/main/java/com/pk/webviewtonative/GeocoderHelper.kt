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
