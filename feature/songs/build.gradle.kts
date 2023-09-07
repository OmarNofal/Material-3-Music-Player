
plugins {
    id("com.omar.android.library")
    id("com.omar.android.hilt")
}

android {
    namespace = "com.omar.musica.songs"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}