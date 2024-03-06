plugins {
    id("com.omar.android.feature")
    id("com.omar.android.compose")
}

android {
    namespace = "com.omar.nowplaying"


    kotlinOptions {
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=${rootDir.absolutePath}/stability-config.txt"
        )
    }
}




dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation(project(mapOf("path" to ":core:store")))
    implementation(project(mapOf("path" to ":core:model")))
    implementation(project(mapOf("path" to ":core:ui")))
    implementation(project(mapOf("path" to ":core:playback")))
    implementation(project(mapOf("path" to ":core:network")))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}