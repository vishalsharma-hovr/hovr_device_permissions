package com.hovr.devicepermissions.location

import android.content.Context
import android.location.LocationManager
import androidx.core.content.ContextCompat

internal object LocationServiceChecker {
    fun isLocationEnabled(context: Context): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}
