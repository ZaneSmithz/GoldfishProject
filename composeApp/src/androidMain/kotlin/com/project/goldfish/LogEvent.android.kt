package com.project.goldfish

import android.util.Log

actual fun logEvent(message: String) {
    Log.d("TESTTAG", message)
}