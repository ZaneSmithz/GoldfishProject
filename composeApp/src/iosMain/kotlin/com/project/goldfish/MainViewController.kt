package com.project.goldfish

import androidx.compose.ui.window.ComposeUIViewController
import com.project.goldfish.app.App
import com.project.goldfish.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}