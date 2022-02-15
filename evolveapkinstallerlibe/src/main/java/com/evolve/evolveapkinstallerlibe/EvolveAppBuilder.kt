package com.evolve.evolveapkinstallerlibe

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class EvolveAppBuilder(
    private val activity: AppCompatActivity,
    private val url: String,
    private val fileName: String,
    private val appId: String
) {
    companion object {
        private const val FILE_BASE_PATH = "file://"
        private const val MIME_TYPE = "application/vnd.android.package-archive"
        private const val PROVIDER_PATH = ".provider"
        private const val APP_INSTALL_PATH = "\"application/vnd.android.package-archive\""
    }

    private val request by lazy {
        activity.permissionsBuilder(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).build()
    }

    fun install() {
        if (checkStoragePermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            && checkStoragePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        )
            request.send { result ->
                when {
                    result.anyPermanentlyDenied() -> showStorageEnableDialog()
                    result.anyShouldShowRationale() -> showStorageEnableDialog()
                    result.allGranted() -> downloadAndInstallApk()
                }
            }
        else downloadAndInstallApk()
    }

    private fun showStorageEnableDialog() {
        MaterialAlertDialogBuilder(activity)
            .setTitle(activity.getString(R.string.title_storage_permission))
            .setMessage(activity.getString(R.string.desc_storage_permission))
            .setNeutralButton(activity.getString(R.string.btn_label_ok)) { dialog, _ ->
                dialog.dismiss()
                activity.finish()
            }
            .show()
    }

    private fun checkStoragePermission(permission: String) = ContextCompat.checkSelfPermission(
        activity,
        permission
    ) != PackageManager.PERMISSION_GRANTED

    class Builder(private val context: AppCompatActivity) {
        private lateinit var url: String
        private lateinit var fileName: String
        private lateinit var appId: String

        fun url(u: String) = apply { url = u }

        fun fileName(f: String) = apply { fileName = f }

        fun appId(id: String) = apply { appId = id }

        fun build() = EvolveAppBuilder(context, url, fileName, appId)
    }

    private fun downloadAndInstallApk() {
        var destination =
            activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"
        destination += fileName
        val uri = Uri.parse("${FILE_BASE_PATH}$destination")
        val file = File(destination)
        if (file.exists()) file.delete()
        val downloadManager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)
        val request = DownloadManager.Request(downloadUri)
        request.setMimeType(MIME_TYPE)
        request.setTitle("APK Download")
        request.setDescription("DOWNLOADING $fileName")
        request.setDestinationUri(uri)
        showInstallOption(destination, uri)
        downloadManager.enqueue(request)
        Toast.makeText(
            activity,
            "DOWNLOADING $fileName",
            Toast.LENGTH_LONG
        )
            .show()
    }

    private fun showInstallOption(
        destination: String,
        uri: Uri,
    ) {
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val contentUri = FileProvider.getUriForFile(
                        context,
                        appId + PROVIDER_PATH,
                        File(destination)
                    )
                    val install = Intent(Intent.ACTION_VIEW)
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                    install.data = contentUri
                    context.startActivity(install)
                    context.unregisterReceiver(this)
                } else {
                    val install = Intent(Intent.ACTION_VIEW)
                    install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    install.setDataAndType(
                        uri,
                        APP_INSTALL_PATH
                    )
                    context.startActivity(install)
                    context.unregisterReceiver(this)
                }
            }
        }
        activity.registerReceiver(
            onComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }
}