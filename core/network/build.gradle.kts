
plugins {
    id("com.omar.android.library")
    id("com.omar.android.hilt")
}

android {
    namespace = "com.omar.musica.network"

    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            consumerProguardFile("consumer-rules.pro")
        }
    }

}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.gson.converter)
    implementation(libs.gson)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}