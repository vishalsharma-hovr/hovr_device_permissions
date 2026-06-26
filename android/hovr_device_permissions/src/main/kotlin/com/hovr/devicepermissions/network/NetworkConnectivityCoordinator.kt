package com.hovr.devicepermissions.network

import android.content.Context
import android.net.ConnectivityManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.hovr.devicepermissions.ui.NoConnectionDialog

internal class NetworkConnectivityCoordinator(
    private val activity: FragmentActivity,
) : DefaultLifecycleObserver {
    private val connectivityManager =
        activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val noConnectionDialog = NoConnectionDialog(activity)
    private var callbackHandler: NetworkCallbackHandler? = null
    private var attached = false

    fun attach() {
        if (attached) {
            return
        }
        attached = true
        activity.lifecycle.addObserver(this)
        callbackHandler = NetworkCallbackHandler(
            onAvailable = { activity.runOnUiThread { handleOnline() } },
            onLost = { activity.runOnUiThread { handleOffline() } },
        )
        callbackHandler?.register(connectivityManager)
        if (!connectivityManager.hasInternet()) {
            handleOffline()
        }
    }

    fun detach() {
        if (!attached) {
            return
        }
        attached = false
        activity.lifecycle.removeObserver(this)
        callbackHandler?.unregister(connectivityManager)
        callbackHandler = null
        noConnectionDialog.dismiss()
    }

    fun recheck() {
        if (connectivityManager.hasInternet()) {
            handleOnline()
        } else {
            handleOffline()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        recheck()
    }

    private fun handleOffline() {
        noConnectionDialog.show { recheck() }
    }

    private fun handleOnline() {
        noConnectionDialog.dismiss()
    }
}
