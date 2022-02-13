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
```
val url:String = "https://www.google.com/"
val apkInstaller = EvolveAppInstaller(this)
apkInstaller.installApk(Uri.parse(url))
```
