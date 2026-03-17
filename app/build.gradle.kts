import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "moe.reimu.galaxy.weather.fix"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "moe.reimu.galaxy.weather.fix"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        create("release") {
            file("../signing.properties").let { propFile ->
                if (propFile.canRead()) {
                    val properties = Properties()
                    properties.load(propFile.inputStream())

                    storeFile = file(properties.getProperty("KEYSTORE_FILE"))
                    storePassword = properties.getProperty("KEYSTORE_PASSWORD")
                    keyAlias = properties.getProperty("SIGNING_KEY_ALIAS")
                    keyPassword = properties.getProperty("SIGNING_KEY_PASSWORD")
                } else {
                    println("Unable to read signing.properties")
                }
            }
        }
    }

    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.findByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    useLibrary("wear-sdk")
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui.tooling)
    implementation(libs.wear.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    implementation(libs.horologist.composables)
    implementation(libs.horologist.compose.material)

    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}