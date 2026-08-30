import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.ksp)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "CoreDatabase"
            isStatic = true
        }
    }

    jvm()
    js { browser() }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    android {
        namespace = "com.vitran.shop.core.database"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget = JvmTarget.JVM_11 }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            api(libs.androidx.room3.runtime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.androidx.sqlite.framework)
        }
        iosMain.dependencies {
            implementation(libs.androidx.sqlite.bundled)
        }
        jvmMain.dependencies {
            implementation(libs.androidx.sqlite.bundled)
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.androidx.sqlite.bundled)
        }
        wasmJsMain.dependencies {
            implementation(libs.androidx.sqlite.web)
            implementation(libs.kotlinx.browser)
        }
        jsMain.dependencies {
            implementation(libs.androidx.sqlite.web)
            implementation(libs.kotlinx.browser)
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room3.compiler)
    add("kspJvm", libs.androidx.room3.compiler)
    add("kspIosArm64", libs.androidx.room3.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room3.compiler)
    add("kspWasmJs", libs.androidx.room3.compiler)
    add("kspJs", libs.androidx.room3.compiler)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
