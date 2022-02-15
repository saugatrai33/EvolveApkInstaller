package com.evolve.evolveapkinstallerlibe

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

internal fun AppCompatActivity.showDialog(
    title: String,
    message: String
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setNeutralButton(
            this.getString(R.string.btn_label_ok)
        ) { dialog, _ ->
            dialog.dismiss()
            this.finish()
        }
        .show()
}

internal fun AppCompatActivity.showToast(
    message: String
) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_LONG
    )
        .show()
}

fun AppCompatActivity.checkPermission(permission: String) =
    ContextCompat.checkSelfPermission(
        this,
        permission
    ) != PackageManager.PERMISSION_GRANTED