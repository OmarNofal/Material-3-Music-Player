plugins {
  id("com.omar.android.library")
  id("com.omar.android.compose")
  id("com.omar.android.hilt")
}

android {
  namespace = "com.omar.musica.ui"

}

dependencies {
  api(libs.androidx.compose.foundation)
  api(libs.androidx.compose.foundation.layout)
  api(libs.androidx.compose.material.iconsExtended)
  api(libs.androidx.compose.material3)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.tooling)
  api(libs.androidx.material3.window.size.class1)
  api(libs.androidx.compose.runtime.livedata)
  api(libs.androidx.activity.compose)
  api(libs.androidx.navigation.compose)
  api(libs.androidx.hilt.navigation.compose)
  api(libs.coil)
  api(libs.drag.reorder)

  implementation(libs.core.ktx)
  implementation(libs.appcompat)
  implementation(libs.material)
  implementation(project(":core:model"))
  implementation(project(":core:store"))
  implementation(project(":core:playback"))
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.espresso.core)
  debugImplementation(libs.androidx.ui.tooling)
}