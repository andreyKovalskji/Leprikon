package io.play.leprikon.pages.web

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import io.play.leprikon.R

class WebPage(
    private val url: String,
    private val requestPermissionLauncher: ActivityResultLauncher<String>,
    private val getmFilePathCallback: () -> ValueCallback<Array<Uri>>?,
    private val setMFilePathCallback: (ValueCallback<Array<Uri>>?) -> Unit
): Fragment() {

    private var mFilePathCallback
        get() = getmFilePathCallback()
        set(value) = setMFilePathCallback(value)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.page_web, container, false)

    fun getWebView(): WebView? = view?.allViews?.filter { it is WebView }?.firstOrNull() as? WebView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webView = getWebView()
        webView?.let {
            WebPageSetter(requestPermissionLauncher, setMFilePathCallback).setParamsForWebView(it)
            it.loadUrl(url)
        }
    }


}