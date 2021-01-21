package com.jmr.dropboxbrowser.util

import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap

class FileHelper{

    fun getFileIntent(fileName: String, fileUri: Uri): Intent {
        val mime = MimeTypeMap.getSingleton()
        val ext: String = fileName.substring(fileName.indexOf(".") + 1)
        val type = mime.getMimeTypeFromExtension(ext)

        return Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(fileUri, type)
        }
    }

}