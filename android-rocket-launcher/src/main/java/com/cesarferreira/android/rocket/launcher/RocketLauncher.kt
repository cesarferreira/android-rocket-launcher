package com.cesarferreira.android.rocket.launcher

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec

class RocketLauncher : Plugin<Project> {
    override fun apply(project: Project) {

        if (!project.plugins.hasPlugin(AppPlugin::class.java)) {
            throw RuntimeException("should be declared after 'com.android.application'")
        }

        val ext = project.extensions.getByType(AppExtension::class.java)

        ext.applicationVariants.all { v ->
            val taskName = "open" + capitalize(v.name)
            val parentTask = v.install
            val adb = ext.adbExe

            if (v.isSigningReady) {

                val packageId = v.applicationId

                val variantAction = hashMapOf(
                    "dependsOn" to parentTask,
                    "description" to "Installs and opens " + v.description,
                    "type" to Exec::class.java,
                    "group" to "Open"
                )

                val t = project.task(variantAction, taskName) as Exec

                t.setCommandLine(adb, "shell", "monkey", "-p", packageId, "-c", "android.intent.category.LAUNCHER", "1")
            }
        }
    }

    private fun capitalize(input: String): String {
        return input.substring(0, 1).toUpperCase() + input.substring(1)
    }
}
