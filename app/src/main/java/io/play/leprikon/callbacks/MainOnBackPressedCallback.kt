package io.play.leprikon.callbacks

import androidx.activity.OnBackPressedCallback

class MainOnBackPressedCallback(private val _handleOnBackPressed: () -> Unit): OnBackPressedCallback(true) {
    override fun handleOnBackPressed() {
        _handleOnBackPressed()
    }
}