import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    // alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    // listOf(
    //     iosArm64(),
    //     iosSimulatorArm64()
    // ).forEach { iosTarget ->
    //     iosTarget.binaries.framework {
    //         baseName = "Shared"
    //         isStatic = true
    //     }
    // }
    
    // jvm()
    
    js {
        browser()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    
    // android {
    //    namespace = "com.vitran.shop.shared"
    //    compileSdk = libs.versions.android.compileSdk.get().toInt()
    //    minSdk = libs.versions.android.minSdk.get().toInt()
    //
    //    compilerOptions {
    //        jvmTarget = JvmTarget.JVM_11
    //    }
    //    androidResources {
    //        enable = true
    //    }
    //    withHostTest {
    //        isIncludeAndroidResources = true
    //    }
    //    withDeviceTestBuilder {
    //        sourceSetTreeName = "test"
    //    }.configure {
    //        instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    //    }
    // }
    
    sourceSets {
        // androidMain.dependencies {
        //     implementation(libs.compose.uiToolingPreview)
        //     implementation(libs.compose.uiTooling)
        //     implementation(libs.ktor.client.android)
        //     implementation(libs.media3.exoplayer)
        //     implementation(libs.androidx.security.crypto)
        // }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(project(":core:domain"))
            implementation(project(":core:network"))
            implementation(project(":core:session"))
            implementation(project(":core:platform"))
            implementation(project(":feature:auth"))
            implementation(project(":feature:account"))
            implementation(project(":feature:location"))
            implementation(project(":feature:taxonomy"))
            implementation(project(":feature:marketplace"))
            implementation(project(":feature:home"))
            implementation(project(":feature:engagement"))
            implementation(project(":feature:seller"))
            implementation(project(":feature:content"))
            implementation(project(":feature:admin"))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        // iosMain.dependencies {
        //     implementation(libs.ktor.client.darwin)
        // }
        // jvmMain.dependencies {
        //     implementation(libs.ktor.client.java)
        // }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
        wasmJsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
    }
}

// dependencies {
//     androidRuntimeClasspath(libs.compose.uiTooling)
// }