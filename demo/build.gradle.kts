plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    androidTarget {
        compilations.configureEach {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        val androidMain by getting

        androidMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
//            implementation(compose.material)
            implementation(compose.ui)
            implementation(libs.androidx.activity.compose)
            implementation(libs.kotlinx.coroutines)
            implementation(project(":descopesdk"))
        }
    }
}

android {
    namespace = "com.descope.demo"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

compose {
    // temp, revert before PR is reviewed
    kotlinCompilerPlugin.set(libs.versions.compose.multiplatform.compiler)
}