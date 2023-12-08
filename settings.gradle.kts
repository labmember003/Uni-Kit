pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://my.pspdfkit.com/maven/")
            url = uri("https://jcenter.bintray.com")
            url = uri("https://repo.itextsupport.com/android")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://my.pspdfkit.com/maven/")
            url = uri("https://jcenter.bintray.com")
        }
    }
}

rootProject.name = "Comicify"
include(":app")
 