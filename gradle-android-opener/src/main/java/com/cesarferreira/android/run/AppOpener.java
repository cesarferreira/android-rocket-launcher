
package com.cesarferreira.android.run;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import java.io.File;
import java.util.HashMap;
import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Exec;

public class AppOpener implements Plugin<Project> {
	@Override
	public void apply(Project project) {

		if (!project.getPlugins().hasPlugin(AppPlugin.class)) {
			throw new RuntimeException("should be declared after 'com.android.application'");
		}

		AppExtension ext = project.getExtensions().getByType(AppExtension.class);

		ext.getApplicationVariants().all(v -> {
			String taskName = "open"+capitalize(v.getName());
			DefaultTask parentTask = v.getInstall();
			File adb = ext.getAdbExe();

			if (v.isSigningReady()) {

				String packageId = v.getApplicationId();

				HashMap variantAction = new HashMap();
				variantAction.put("dependsOn", parentTask);
				variantAction.put("description", "Installs and runs " + v.getDescription());
				variantAction.put("type", Exec.class);
				variantAction.put("group", "Open");

				Exec t = (Exec) project.task(variantAction, taskName);

				t.setCommandLine(adb, "shell", "monkey", "-p", packageId, "-c", "android.intent.category.LAUNCHER", "1");
			}
		});

	}

	private String capitalize(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
}
