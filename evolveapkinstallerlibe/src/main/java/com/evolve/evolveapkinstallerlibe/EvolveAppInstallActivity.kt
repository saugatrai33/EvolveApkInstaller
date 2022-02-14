package com.evolve.evolveapkinstallerlibe

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

class EvolveAppInstallActivity : AppCompatActivity() {
    companion object {
        private const val MIME_TYPE = "application/vnd.android.package-archive"
        private const val APP_INSTALL_PATH = "\"application/vnd.android.package-archive\""
        private const val KEY_FIREBASE_URL = "APK_URL"
        private const val KEY_APK_NAME = "APK_NAME"
        private const val KEY_APP_ID = "APP_ID"
        private const val PROVIDER_PATH = ".provider"
    }

    private val url: String? by lazy {
        intent?.extras?.getString(KEY_FIREBASE_URL, "")
    }
    private val fileName: String? by lazy {
        intent?.extras?.getString(KEY_APK_NAME, "")
    }
    private val appId: String? by lazy {
        intent?.extras?.getString(KEY_APP_ID, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evolve_app_install)
        enqueueDownload()
    }

    private fun enqueueDownload() {
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(directory, fileName)
        if (file.exists()) file.delete()
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setMimeType(MIME_TYPE)
        request.setTitle("APK Download")
        request.setDescription("Downloading apk")
        showInstallOption(file)
        downloadManager.enqueue(request)
        Toast.makeText(
            this,
            "APK downloading starting.....",
            Toast.LENGTH_SHORT)
            .show()
    }

    private fun showInstallOption(
        file: File
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
                        file
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
                        Uri.fromFile(file),
                        APP_INSTALL_PATH
                    )
                    context.startActivity(install)
                    context.unregisterReceiver(this)
                }
            }
        }
        registerReceiver(
            onComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }
}