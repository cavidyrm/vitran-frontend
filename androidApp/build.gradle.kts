import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(project(":shared"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.core)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "com.vitran.shop"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.vitran.shop"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = (findProperty("vitran.versionCode") as String?)?.toIntOrNull() ?: 1
        versionName = (findProperty("vitran.versionName") as String?) ?: "1.0.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            val props = Properties()
            val local = rootProject.file("local.properties")
            if (local.exists()) {
                local.inputStream().use { props.load(it) }
            }
            val store = System.getenv("VITRAN_STORE_FILE")
                ?: props.getProperty("vitran.storeFile")
            val storePassword = System.getenv("VITRAN_STORE_PASSWORD")
                ?: props.getProperty("vitran.storePassword")
            val keyAlias = System.getenv("VITRAN_KEY_ALIAS")
                ?: props.getProperty("vitran.keyAlias")
            val keyPassword = System.getenv("VITRAN_KEY_PASSWORD")
                ?: props.getProperty("vitran.keyPassword")
            if (store != null && storePassword != null && keyAlias != null && keyPassword != null) {
                storeFile = file(store)
                this.storePassword = storePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            val releaseSigning = signingConfigs.getByName("release")
            if (releaseSigning.storeFile != null) {
                signingConfig = releaseSigning
            }
        }
        debug {
            // Cleartext to localhost allowed via network_security_config debug-overrides only.
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}
