package io.play.leprikon.pages.web

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.result.ActivityResultLauncher

class WebPageSetter(
    private val requestPermissionLauncher: ActivityResultLauncher<String>,
    private val setMFilePathCallback: (ValueCallback<Array<Uri>>?) -> Unit
) {

    fun setParamsForWebView(webView: WebView) {
        setWebViewWebChromeClient(webView)
        setParamsForSetting(webView)
        setNonBooleanParamsForSetting(webView)
        webView.webViewClient = WebPageClient()
        setParamsForCookies(webView)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setParamsForSetting(webView: WebView) {
        webView.settings.allowContentAccess = true
        webView.settings.allowFileAccess = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.settings.databaseEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
    }

    private fun setNonBooleanParamsForSetting(webView: WebView) {
        webView.settings.mixedContentMode = getWebViewMixedContentMode()
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView.settings.userAgentString = changeUserAgentString(webView.settings.userAgentString)
    }

    private fun setWebViewWebChromeClient(webView: WebView) {
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                setMFilePathCallback(filePathCallback)
                return true
            }
        }
    }

    private fun setParamsForCookies(webView: WebView) {
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
    }

    private fun getWebViewMixedContentMode(): Int {
        return WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }

    private fun changeUserAgentString(userAgentString: String) = userAgentString.replace("; wv", "")
}