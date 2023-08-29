package io.play.leprikon.screenContainers.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import io.play.leprikon.R
import io.play.leprikon.pages.games.GamesPage
import io.play.leprikon.pages.loading.LoadingPage
import io.play.leprikon.pages.web.WebPage
import io.play.leprikon.url.DataReceiver
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.view.WindowManager
import android.webkit.ValueCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import io.play.leprikon.callbacks.MainOnBackPressedCallback
import io.play.leprikon.extentions.goBackIfCan
import io.play.leprikon.pages.blank.BlankPage
import io.play.leprikon.pages.slots.SlotsPage
import io.play.leprikon.util.callOnReceiveValue
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    var callback: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycleScope.launch (Dispatchers.IO) {
            val data = DataReceiver.getData(this@MainActivity)
//            if(data != null && data.let_in && data.url != null && data.contains("http")) {
            if(data != null && data.contains("http")) {
                Log.i("Data receiver", "Received data: $data")
                @Suppress("DEPRECATION")
                this@MainActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.pages, WebPage(data, requestPermissionLauncher,
                        { mFilePathCallback }) {
                        mFilePathCallback = it
                        Log.i("mFilePathCallback", "Changed (is ${if(it == null) "" else "not "}null)")
                    })
                    .commitAllowingStateLoss()
            }
            else {
                Log.i("Data receiver", when {
                    data == null -> "Received data: null..."
                    !data.contains("http") -> "Received data not contains \"http\" (data: ${data})"
                    else -> "Unknown reason for else branch."
                })
                supportFragmentManager.beginTransaction()
                    .replace(R.id.pages, GamesPage())
                    .commitAllowingStateLoss()
            }
        }
        onBackPressedDispatcher.addCallback(
            MainOnBackPressedCallback {
                when (supportFragmentManager.fragments.firstOrNull()) {
                    null, is GamesPage, is LoadingPage -> finish()
                    is SlotsPage, is BlankPage -> supportFragmentManager.beginTransaction().replace(R.id.pages, GamesPage()).commit()
                    is WebPage -> (supportFragmentManager.fragments.first() as WebPage).getWebView()?.goBackIfCan()
                }
            }
        )
        supportFragmentManager.beginTransaction()
            .add(R.id.pages, LoadingPage())
            .commit()
    }

    val requestPermissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean? ->
        lifecycleScope.launch(Dispatchers.IO) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {

                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(File.createTempFile(
                        "photoFile",
                        ".jpg",
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    ))
                )
                callback = if(VERSION.SDK_INT >= 33) {
                    takePictureIntent.getParcelableExtra(MediaStore.EXTRA_OUTPUT, Uri::class.java)
                }
                else {
                    @Suppress("DEPRECATION")
                    takePictureIntent.getParcelableExtra<Uri>(MediaStore.EXTRA_OUTPUT)
                }
            } catch (ioException: IOException) {
                Log.e("PhotoFile", "Unable to create photoFile.", ioException)
            }
            val old = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            old.let {
                it.type = "*/*"
            }
            val chooserIntent = Intent(Intent.ACTION_CHOOSER)
            chooserIntent.putExtras(
                listOf(Intent.EXTRA_INTENT to old),
                listOf(Intent.EXTRA_INITIAL_INTENTS to takePictureIntent.toArray())
            )
            @Suppress("DEPRECATION")
            startActivityForResult(chooserIntent, 1)
        }
    }

    private fun Intent.toArray() = arrayOf(this)
    private fun Intent.putExtras(extras: List<Pair<String, Parcelable>> = emptyList(), extrasArrays: List<Pair<String, Array<Intent>>>) {
        for(extra in extras) {
            putExtra(extra.first, extra.second)
        }
        for(extra in extrasArrays) {
            putExtra(extra.first, extra.second)
        }
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mFilePathCallback?.let {
            callOnReceiveValue(resultCode, data, it, callback)
            mFilePathCallback = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val page = supportFragmentManager.fragments.firstOrNull()
        if(page is WebPage) {
            page.getWebView()?.saveState(outState)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val page = supportFragmentManager.fragments.firstOrNull()
        if(page is WebPage) {
            page.getWebView()?.restoreState(savedInstanceState)
        }
    }
}