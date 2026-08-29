rootProject.name = "VitranShop"

pluginManagement {
    repositories {
        // Portal first for Gradle convention plugins; then mirror + Google for AGP/Kotlin.
        gradlePluginPortal()
        maven(url = "https://maven.myket.ir")
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven(url = "https://maven.myket.ir")
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// include(":androidApp")
// include(":desktopApp")
include(":shared")
include(":webApp")