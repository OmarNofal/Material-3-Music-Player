import com.omar.musica.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply("com.google.dagger.hilt.android")
      pluginManager.apply("org.jetbrains.kotlin.kapt")

      dependencies {
        "implementation"(libs.findLibrary("hilt.android").get())
        "kapt"(libs.findLibrary("hilt.compiler").get())
        "kaptAndroidTest"(libs.findLibrary("hilt.compiler").get())
        "kaptTest"(libs.findLibrary("hilt.compiler").get())
      }
    }
  }
}