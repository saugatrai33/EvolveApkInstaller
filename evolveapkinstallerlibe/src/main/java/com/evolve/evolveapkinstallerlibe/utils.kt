package com.evolve.evolveapkinstallerlibe

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.evolve.evolveapkinstallerlibe.Constants.APP_INSTALL_PATH
import com.evolve.evolveapkinstallerlibe.Constants.FILE_BASE_PATH
import com.evolve.evolveapkinstallerlibe.Constants.MIME_TYPE
import com.evolve.evolveapkinstallerlibe.Constants.PROVIDER_PATH
import java.io.File

private const val TAG = "utils"

internal fun downloadAndInstallApk(
    context: Context,
    fileName: String,
    url: String,
    appId: String
) {
    println("${TAG}: downloadAndInstallApk: apkDownloadUrl -> $url")
    var destination =
        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"
    destination += fileName
    val uri = Uri.parse("${FILE_BASE_PATH}$destination")
    println("${TAG}: downloadAndInstallApk: uri -> $uri")
    val file = File(destination)
    if (file.exists()) file.delete()
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadUri = Uri.parse(url)
    val request = DownloadManager.Request(downloadUri)
    request.setMimeType(MIME_TYPE)
    request.setTitle("DOWNLOAD APK")
    request.setDescription("Downloading $fileName")
    request.setDestinationUri(uri)
    showInstallOption(context, destination, uri, appId = appId)
    downloadManager.enqueue(request)
    Toast.makeText(
        context,
        "DOWNLOADING $fileName",
        Toast.LENGTH_LONG
    )
        .show()
}

internal fun showInstallOption(
    context: Context,
    destination: String,
    uri: Uri,
    appId: String
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
                println("${TAG}: onReceive: contentUri -> $contentUri")
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
    context.registerReceiver(
        onComplete,
        IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
    )
}