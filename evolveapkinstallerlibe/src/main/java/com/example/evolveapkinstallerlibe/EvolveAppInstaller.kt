package com.example.evolveapkinstallerlibe

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val APP_INSTALL_PATH = "\"application/vnd.android.package-archive\""
private val TAG = EvolveAppInstaller::class.java.canonicalName

class EvolveAppInstaller(
    private val context: Context
) {
    fun installApk(
        uri: Uri
    ) {
        try {
            Log.d(TAG, "installApk: starting apk installation: $")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val install = Intent(Intent.ACTION_VIEW)
                install.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                install.data = uri
                context.startActivity(install)
            } else {
                val install = Intent(Intent.ACTION_VIEW)
                install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                install.setDataAndType(
                    uri,
                    APP_INSTALL_PATH
                )
                context.startActivity(install)
            }
        } catch (e: ActivityNotFoundException) {
            Log.d(TAG, "installApk: error: ${e.localizedMessage}")
            showAlertDialog(context)
        }
    }

    private fun showAlertDialog(
        context: Context
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.tite_evolve_dialog))
            .setMessage(context.getString(R.string.msg_apk_install))
            .setNeutralButton(context.getString(R.string.label_neutral_btn)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}