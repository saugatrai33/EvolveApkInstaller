package com.evolve.evolveapkinstallerlibe

import android.Manifest
import android.util.Patterns
import android.webkit.URLUtil
import androidx.appcompat.app.AppCompatActivity
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import java.util.regex.Pattern

class EvolveAppInstaller(
    private val activity: AppCompatActivity,
    private val url: String,
    private val fileName: String,
    private val appId: String
) {
    private val request by lazy {
        activity.permissionsBuilder(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).build()
    }

    fun install() {
        if (!URLUtil.isHttpsUrl(url)) {
            activity.showToast("Invalid URL.")
            return
        }
        if (activity.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
            request.send { result ->
                when {
                    result.anyPermanentlyDenied() -> activity.showDialog(
                        activity.getString(R.string.title_storage_permission),
                        activity.getString(R.string.desc_storage_permission)
                    )
                    result.anyShouldShowRationale() -> activity.showDialog(
                        activity.getString(R.string.title_storage_permission),
                        activity.getString(R.string.desc_storage_permission)
                    )
                    result.allGranted() -> downloadAndInstallApk(activity, fileName, url, appId)
                }
            }
        else downloadAndInstallApk(activity, fileName, url, appId)
    }

    class Builder(private val context: AppCompatActivity) {
        private lateinit var url: String
        private lateinit var fileName: String
        private lateinit var appId: String

        fun url(u: String) = apply { url = u }

        fun fileName(f: String) = apply { fileName = f }

        fun appId(id: String) = apply { appId = id }

        fun build() = EvolveAppInstaller(context, url, fileName, appId)
    }
}