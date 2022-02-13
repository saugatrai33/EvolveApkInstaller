# EvolveApkInstaller
Apk installer library for android.

## Installation
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

```
dependencies {
	implementation 'com.github.saugatrai33:EvolveApkInstaller:Tag'
}
```

## In your Activity or Fragment
	val intent = Intent(this, EvolveAppInstallActivity::class.java)
	intent.putExtra("APK_URL", it.downloadLink!!)
	intent.putExtra("APK_NAME", "RMAP-UPDATE-${System.currentTimeMillis()}-${firebaseResponseModel?.newAppVersion}.apk",)
	intent.putExtra("APP_ID", BuildConfig.APPLICATION_ID)
	startActivity(intent)
```
