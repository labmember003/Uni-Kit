pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://my.pspdfkit.com/maven/")
            url = uri("https://jcenter.bintray.com")
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
 