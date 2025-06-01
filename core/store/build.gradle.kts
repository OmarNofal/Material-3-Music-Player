plugins {
    id("com.omar.android.library")
    id("com.omar.android.hilt")
}

android {
    namespace = "com.omar.musica.store"

    buildTypes {
        release {
            consumerProguardFile("proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.datastore)
    implementation(libs.jaudio.tagger)
    implementation(project(mapOf("path" to ":core:model")))
    implementation(project(mapOf("path" to ":core:database")))
    implementation(project(mapOf("path" to ":core:network")))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}