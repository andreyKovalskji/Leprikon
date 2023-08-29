package io.play.leprikon.url

import android.content.Context
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerClient.InstallReferrerResponse
import com.android.installreferrer.api.InstallReferrerStateListener
import com.facebook.applinks.AppLinkData
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.Request

class DataReceiver {
    companion object {

        private const val BASE_URL = "https://sampiset.com/lauth/?deep=%s&%s"

        suspend fun getData(context: Context): String? {
            var ref: String? = null
            var deep: String? = null

            var refTook = false
            var deepTook = false

            val rClient = InstallReferrerClient.newBuilder(context).build()
            rClient.startConnection(
                object: InstallReferrerStateListener {
                    override fun onInstallReferrerSetupFinished(p0: Int) {
                        when(p0) {

                            InstallReferrerResponse.OK -> {
                                ref  = rClient.installReferrer.installReferrer
                                Log.i("Install referrer", "Connection is OK. Install referrer: $ref")
                                refTook = true
                            }
                            InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                                Log.i("Install referrer", "Service unavailable...")
                            }
                            InstallReferrerResponse.PERMISSION_ERROR -> {
                                Log.i("Install referrer", "Permission error...")
                            }
                            else -> {
                                Log.i("Install referrer", "Else branch.")
                            }
                        }
                        rClient.endConnection()
                    }
                    override fun onInstallReferrerServiceDisconnected() {
                        Log.i("Install referrer", "Disconnected...")
                    }
                }
            )

            AppLinkData.fetchDeferredAppLinkData(context
            ) { appLinkData: AppLinkData? ->
                val tree = if(appLinkData != null) {
                    val argumentBundle = appLinkData.argumentBundle
                    argumentBundle?.getString("target_url")
                }
                else {
                    null
                }
                val treeSplitter = "$"
                val uri = tree?.split(treeSplitter)
                deep = if(uri != null) {
                    "https://$uri"
                }
                else {
                    null
                }
                Log.i("Deep link", deep ?: "null...")
                deepTook = true
            }

            while(!refTook) {
                delay(100)
            }

            while(!deepTook) {
                delay(100)
            }
            val url = BASE_URL.format(deep, ref)
            Log.i("Data receiver", "Url: $url")
            val request = Request.Builder()
                .url(url)
                .build()
            val call = OkHttpClient().newCall(request)
            val response = call.execute()
            if (response.isSuccessful) {
                return response.body?.string()
            }
            return null
        }
    }
}