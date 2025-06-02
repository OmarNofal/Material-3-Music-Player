import java.net.URI


pluginManagement {
  includeBuild("build-logic")
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven { url = URI("https://jitpack.io") }
  }
}

rootProject.name = "Musica"

include(":app")

include(":core:model")
include(":core:store")
include(":core:playback")
include(":core:ui")
include(":core:database")
include(":core:network")

include(":feature:widgets")
include(":feature:albums")
include(":feature:artists")
include(":feature:songs")
include(":feature:playlists")
include(":feature:nowplaying")
include(":feature:settings")
include(":feature:tageditor")
include(":feature:folders")
include(":feature:audiosearch")
