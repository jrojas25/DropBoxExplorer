package com.jmr.dropboxbrowser.util.extension

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Context.showConfirmDialog(title: String, message: String, actionIfAgree: () -> Unit, actionIfDeny: (() -> Unit)?) {
    val alertDialog = AlertDialog.Builder(this).create()
    alertDialog.setTitle(title)
    alertDialog.setMessage(message)
    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
        actionIfDeny?.invoke()
        dialog.dismiss()
    }
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { dialog, _ ->
        actionIfAgree()
        dialog.dismiss()
    }
    alertDialog.show()
}

fun Activity.checkAndRequestPermission(
    title: String, message: String,
    manifestPermission: String, requestCode: Int,
    actionIfAlreadyApproved: (() -> Unit)? = null,
    actionForLater: (() -> Unit)? = null,
    actionIfRefused: (() -> Unit)? = null
) {
    val permissionStatus = ContextCompat.checkSelfPermission(applicationContext, manifestPermission)

    if (permissionStatus == PackageManager.PERMISSION_DENIED) {
        actionForLater?.invoke()
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, manifestPermission)) {
            this.showConfirmDialog(title, message, {
                requestPermission(manifestPermission, requestCode)
            }, actionIfRefused )
        } else {
            // No explanation needed -> request the permission
            requestPermission(manifestPermission, requestCode)
        }
    } else {
        actionIfAlreadyApproved?.invoke()
    }
}

fun Activity.requestPermission(manifestPermission: String, requestCode: Int) {
    ActivityCompat.requestPermissions(this, arrayOf(manifestPermission), requestCode)
}