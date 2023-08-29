package io.play.leprikon.pages.web

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class WebPageClient: WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val url = request.url.toString()
        return if (url.contains("/")) {
            Log.i("WebPageClient", "Try connect to: $url")
            !url.contains("http")
        } else true
    }

    override fun onReceivedLoginRequest(
        view: WebView,
        realm: String,
        account: String?,
        args: String
    ) = super.onReceivedLoginRequest(view, realm, account, args)
}