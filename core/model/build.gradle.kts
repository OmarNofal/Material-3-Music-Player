plugins {
    id("com.omar.kotlin.library")
}


dependencies {
    // We need the runtime here to enable Compose to
    // mark our data models as stable
    //implementation(libs.androidx.compose.runtime)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}