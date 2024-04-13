import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.omar.android.application")
    id("com.omar.android.application.compose")
    id("com.omar.android.hilt")
}

android {
    namespace = "com.omar.musica"
    //compileSdk = 33

    defaultConfig {
        applicationId = "com.omar.musica"

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }

        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "MP debug")
        }
    }


    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {
    implementation(libs.core.ktx)
    implementation(libs.media3.session)
    implementation(libs.media3.exoplayer)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.timber)
    implementation(project(mapOf("path" to ":feature:songs")))
    implementation(project(":feature:albums"))
    implementation(project(mapOf("path" to ":core:ui")))
    implementation(project(mapOf("path" to ":core:store")))
    implementation(project(mapOf("path" to ":core:model")))
    implementation(project(mapOf("path" to ":core:playback")))
    implementation(project(mapOf("path" to ":feature:playlists")))
    implementation(project(mapOf("path" to ":feature:nowplaying")))
    implementation(project(mapOf("path" to ":feature:settings")))
    implementation(project(mapOf("path" to ":feature:tageditor")))
    implementation(project(mapOf("path" to ":feature:widgets")))
    api(libs.accompanist.permissions)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}