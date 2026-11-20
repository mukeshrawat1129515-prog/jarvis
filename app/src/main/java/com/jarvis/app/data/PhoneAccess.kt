package com.jarvis.app.data

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

data class PhoneState(
    val hasCamera: Boolean = false,
    val hasMicrophone: Boolean = false,
    val hasGps: Boolean = false,
    val hasContacts: Boolean = false,
    val hasSms: Boolean = false,
    val hasCalls: Boolean = false,
    val androidVersion: Int = 0,
    val manufacturer: String = "",
    val model: String = ""
)

class PhoneAccess(private val context: Context) {

    fun getPhoneState(): PhoneState {
        val pm = context.packageManager
        return PhoneState(
            hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA),
            hasMicrophone = pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE),
            hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS),
            hasContacts = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY),
            hasSms = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY),
            hasCalls = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY),
            androidVersion = Build.VERSION.SDK_INT,
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL
        )
    }

    fun getPhoneContext(): String {
        val state = getPhoneState()
        return buildString {
            appendLine("Phone: ${state.manufacturer} ${state.model}, Android ${state.androidVersion}")
            appendLine("Features: ${if (state.hasCamera) "camera " else ""}${if (state.hasMicrophone) "mic " else ""}${if (state.hasGps) "gps " else ""}${if (state.hasContacts) "contacts " else ""}${if (state.hasSms) "sms " else ""}")
        }
    }
}