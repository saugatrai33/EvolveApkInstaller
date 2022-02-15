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
val url = "https://www.example.com/"
val fileName = "sample.apk"
val evolveAppBuilder = EvolveAppInstaller.Builder(this)
                        .url(url)
                        .fileName((fileName))
                        .appId(BuildConfig.APPLICATION_ID)
                        .build()
                    evolveAppBuilder.install()
```
