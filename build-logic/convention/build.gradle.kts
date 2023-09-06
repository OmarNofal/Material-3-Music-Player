@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    `kotlin-dsl`
}


dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


gradlePlugin {
    plugins {
        register("AndroidLibrary") {
            id = "com.omar.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("JvmLibrary") {
            id = "com.omar.kotlin.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}