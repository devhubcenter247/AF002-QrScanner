pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://artifact.bytedance.com/repository/pangle/")
        maven(url = "https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
        maven {
            url = uri("https://maven.pkg.github.com/Dong0610/InAppUpdate")
        }
        maven(url = "https://maven.pkg.github.com/dbv0610/AdsApplication") {
            credentials {
                username = "dbv0610"
                password = providers.gradleProperty("adsGithubToken").orNull
            }
        }
    }
}
rootProject.name = "QrScaner"
include(":app")
include(":qr")

