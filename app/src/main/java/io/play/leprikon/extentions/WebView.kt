package io.play.leprikon.extentions

import android.webkit.WebView

fun WebView.goBackIfCan() {
    if(canGoBack()) {
        goBack()
    }
}