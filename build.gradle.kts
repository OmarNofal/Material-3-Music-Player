// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  alias(libs.plugins.com.android.application) apply false
  alias(libs.plugins.org.jetbrains.kotlin.android) apply false
  alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
  alias(libs.plugins.com.android.library) apply false
  alias(libs.plugins.hilt) apply false
}
true // Needed to make the Suppress annotation work for the plugins block


subprojects {
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
      val directory = File(rootProject.projectDir, "compose_compiler_reports").absolutePath
      if (project.findProperty("composeCompilerReports") == "true") {
        freeCompilerArgs += listOf(
          "-P",
          "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=${rootProject.projectDir}/stability-config.txt"
        )
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