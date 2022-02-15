package com.evolve.evolveapkinstallerlibe

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send

class EvolveAppBuilder(
    private val activity: AppCompatActivity,
    private val url: String,
    private val fileName: String,
    private val appId: String
) {
    private val request by lazy {
        activity.permissionsBuilder(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).build()
    }

    fun install() {
        if (activity.checkStoragePermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            && activity.checkStoragePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        )
            request.send { result ->
                when {
                    result.anyPermanentlyDenied() -> activity.showStorageEnableDialog()
                    result.anyShouldShowRationale() -> activity.showStorageEnableDialog()
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

        fun build() = EvolveAppBuilder(context, url, fileName, appId)
    }
}