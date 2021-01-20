package com.jmr.dropboxbrowser.util.extension

import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.showConfirmDialog(title: String, message: String, actionIfAgree: () -> Unit, actionIfDeny: (() -> Unit)?) {
    val alertDialog = AlertDialog.Builder(requireContext()).create()
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

fun Fragment.checkAndRequestPermission(
    title: String, message: String,
    manifestPermission: String, requestCode: Int,
    actionIfAlreadyApproved: (() -> Unit)? = null,
    actionForLater: (() -> Unit)? = null,
    actionIfRefused: (() -> Unit)? = null
) {
    val permissionStatus = ContextCompat.checkSelfPermission(requireContext(), manifestPermission)

    if (permissionStatus == PackageManager.PERMISSION_DENIED) {
        actionForLater?.invoke()
        if (this.shouldShowRequestPermissionRationale(manifestPermission)) {
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

fun Fragment.requestPermission(manifestPermission: String, requestCode: Int) {
    this.requestPermissions(arrayOf(manifestPermission), requestCode)
}