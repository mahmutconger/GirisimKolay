import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    // Firebase
    // alias(libs.plugins.google.services)
    // alias(libs.plugins.firebase.crashlytics.plugin)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(projects.sharedLogic)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.androidx.compose.materialIconsExtended)
    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Ktor (for AppModule.kt which references HttpClient directly)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.logging)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Firebase (BOM ensures all versions are aligned)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "com.anlarsinsoftware.girisimkolay"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.anlarsinsoftware.girisimkolay"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            buildConfigField("String", "BASE_URL", "\"https://api.girisimkolay.com\"")
            buildConfigField("String", "APP_ENVIRONMENT", "\"production\"")
            buildConfigField("boolean", "USE_FIREBASE_EMULATORS", "false")
        }
        getByName("debug") {
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8000\"")
            buildConfigField("String", "APP_ENVIRONMENT", "\"development\"")
            buildConfigField("boolean", "USE_FIREBASE_EMULATORS", "true")
        }
        create("staging") {
            initWith(getByName("debug"))
            matchingFallbacks += listOf("debug")
            buildConfigField("String", "BASE_URL", "\"https://staging-api.girisimkolay.com\"")
            buildConfigField("String", "APP_ENVIRONMENT", "\"staging\"")
            buildConfigField("boolean", "USE_FIREBASE_EMULATORS", "false")
        }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
