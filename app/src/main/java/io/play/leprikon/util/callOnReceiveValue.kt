package io.play.leprikon.util

import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback

fun callOnReceiveValue(resultCode: Int, data: Intent?, mFilePathCallback: ValueCallback<Array<Uri>>, callback: Uri?) {
    if (resultCode == -1) {
        if (data != null) {
            if (data.dataString != null) {
                val u = Uri.parse(data.dataString)
                mFilePathCallback.onReceiveValue(arrayOf(u))
            } else {
                if (callback != null) {
                    mFilePathCallback.onReceiveValue(arrayOf(callback))
                } else {
                    mFilePathCallbackOnReceiveValueNull(mFilePathCallback)
                }
            }
        } else {
            if (callback != null) {
                mFilePathCallback.onReceiveValue(arrayOf(callback))
            } else {
                mFilePathCallbackOnReceiveValueNull(mFilePathCallback)
            }
        }
    } else {
        mFilePathCallbackOnReceiveValueNull(mFilePathCallback)
    }
}

private fun mFilePathCallbackOnReceiveValueNull(mFilePathCallback: ValueCallback<Array<Uri>>) {
    mFilePathCallback.onReceiveValue(null)
}