package com.godfirst.traque.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.godfirst.traque.R
import com.godfirst.traque.util.ACTION_GEOFENCE_EVENT
import com.godfirst.traque.util.GeofencingConstants
import com.godfirst.traque.util.errorMessage
import com.godfirst.traque.util.sendGeofenceEnteredNotification
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class TraqueBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            if (geofencingEvent.hasError()) {
                val errorMessage = errorMessage(context, geofencingEvent.errorCode)
                Log.e(TAG, errorMessage)
                return
            }

            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Log.v(TAG, context.getString(R.string.geofence_entered))
                val fenceID = when {
                    geofencingEvent.triggeringGeofences.isEmpty() -> geofencingEvent.triggeringGeofences[0].requestId
                    else -> {
                        Log.e(TAG, "No Geofence Trigger Found! Abort mission!")
                        return
                    }
                }
                val foundIndex = GeofencingConstants.LANDMARK_DATA.indexOfFirst { it.id == fenceID }
                if (-1 == foundIndex) {
                    Log.e(TAG, "Unknown Geofence: Abort Mission", )
                    return
                }

                val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
                notificationManager.sendGeofenceEnteredNotification(context, foundIndex)

            }
        }
    }
}

private const val TAG = "GeofenceReciever"