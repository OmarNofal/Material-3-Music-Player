plugins {
  id("com.omar.android.library")
  id("com.omar.android.hilt")
  id("kotlin-kapt")
}

android {
  namespace = "com.omar.musica.database"
}

dependencies {
  kapt(libs.room.compiler)
  implementation(project(":core:model"))
  implementation(libs.core.ktx)
  implementation(libs.appcompat)
  implementation(libs.material)
  implementation(libs.room.runtime)
  implementation(libs.room.ktx)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.espresso.core)
}