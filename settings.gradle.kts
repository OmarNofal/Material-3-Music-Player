import java.net.URI

include(":feature:widgets")


include(":feature:albums")


include(":core:network")


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
include(":feature:songs")
include(":feature:playlists")
include(":feature:nowplaying")
include(":core:database")
include(":feature:settings")
include(":feature:tageditor")
