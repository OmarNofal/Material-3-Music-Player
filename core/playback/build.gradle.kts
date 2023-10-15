
plugins {
    id("com.omar.android.library")
    id("com.omar.android.hilt")
}

android {
    namespace = "com.omar.musica.playback"
}

dependencies {

    implementation(libs.media3.session)
    implementation(libs.media3.exoplayer)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(project(mapOf("path" to ":core:model")))
    implementation(project(mapOf("path" to ":core:store")))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}