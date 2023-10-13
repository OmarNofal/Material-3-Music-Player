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
    }


    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            val directory = "C:\\Users\\omarw\\Downloads\\Travis Scott - ASTROWORLD (2018) Mp3 (320kbps) [Hunter]"
            if (project.findProperty("composeCompilerReports") == "true") {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${directory}/compose_compiler"
                )
            }
            if (project.findProperty("composeCompilerMetrics") == "true") {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${directory}/compose_compiler"
                )
            }
        }
    }
}

dependencies {

    implementation(project(mapOf("path" to ":feature:songs")))
    implementation(libs.core.ktx)
    implementation(libs.media3.session)
    implementation(libs.media3.exoplayer)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.timber)
    implementation(project(mapOf("path" to ":core:ui")))
    implementation(project(mapOf("path" to ":core:store")))
    implementation(project(mapOf("path" to ":core:model")))
    implementation(project(mapOf("path" to ":core:playback")))
    implementation(project(mapOf("path" to ":feature:playlists")))
    implementation(project(mapOf("path" to ":feature:nowplaying")))
    implementation(project(mapOf("path" to ":feature:settings")))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}