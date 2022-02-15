package com.evolve.evolveapkinstallerlibe

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun AppCompatActivity.showStorageEnableDialog() {
    MaterialAlertDialogBuilder(this)
        .setTitle(this.getString(R.string.title_storage_permission))
        .setMessage(this.getString(R.string.desc_storage_permission))
        .setNeutralButton(this.getString(R.string.btn_label_ok)) { dialog, _ ->
            dialog.dismiss()
            this.finish()
        }
        .show()
}

fun AppCompatActivity.checkStoragePermission(permission: String) =
    ContextCompat.checkSelfPermission(
        this,
        permission
    ) != PackageManager.PERMISSION_GRANTED