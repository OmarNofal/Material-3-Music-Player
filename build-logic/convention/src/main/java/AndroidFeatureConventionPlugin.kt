/*
 * Copyright 2022 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import com.omar.musica.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidFeatureConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply {
        apply("com.omar.android.library")
        apply("com.omar.android.hilt")
      }
      dependencies {
        add("implementation", project(":core:model"))
        add("implementation", "androidx.compose.material3:material3-window-size-class")
        add("testImplementation", kotlin("test"))
        add("androidTestImplementation", kotlin("test"))

        add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
        add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
        add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())

        add("implementation", libs.findLibrary("kotlinx.coroutines.android").get())
      }
    }
  }
}
