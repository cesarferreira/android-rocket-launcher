# Android Rocket Launcher [ ![Download](https://api.bintray.com/packages/riyaz/me.riyaz/android-build-and-run/images/download.svg) ](https://bintray.com/riyaz/me.riyaz/android-build-and-run/_latestVersion)

Gradle plugin that adds tasks to your android apps for installing and launching all variants.
Based on [android-rocket-launcher](https://github.com/cesarferreira/android-rocket-launcher)
## How to use
Paste this code into your module's `build.gradle`

```groovy
apply plugin: 'android-build-and-run'

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'me.riyaz:android-build-and-run:0.0.2'
    }
}
```
Now, when you run `./gradlew tasks`, you'll see something like this:

```
runDemoDebug - Install and run DebugDemo build
runFreeDebug - Install and run DebugFree build
runPaidDebug - Install and run DebugPaid build
```

### Alternative
Copy-pasting this gradle task on every project

```groovy
// Running the APK on your Android Device
android.applicationVariants.all { variant ->
    if (variant.install) {
        tasks.create(name: "run${variant.name.capitalize()}", type: Exec,
                dependsOn: variant.install) {
            group = 'Run'
            description "Installs and Runs the APK for ${variant.description}."
            def getMainActivity = { file ->
                new XmlSlurper().parse(file).application.activity.find {
                    it.'intent-filter'.find { filter ->
                        return filter.action.find {
                            it.'@android:name'.text() == 'android.intent.action.MAIN'
                        } \
                                 && filter.category.find {
                            it.'@android:name'.text() == 'android.intent.category.LAUNCHER'
                        }
                    }
                }.'@android:name'
            }
            doFirst {
                def activityClass =
                        getMainActivity(variant.outputs.processManifest.manifestOutputFile)
                commandLine android.adbExe, 'shell', 'am', 'start', '-n',
                        "${variant.applicationId}/${activityClass}"
            }
        }
    }
}
```
