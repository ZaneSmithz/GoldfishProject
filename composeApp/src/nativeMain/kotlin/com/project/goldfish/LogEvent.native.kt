package com.project.goldfish

import platform.Foundation.NSLog

actual fun logEvent(message: String) {
    NSLog(message)
}