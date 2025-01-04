plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.kotlin.ksp)
}

android {
    compileSdk = 35
    buildToolsVersion = "35.0"
    namespace = "com.zooro.mvvmnewsapp"

    defaultConfig {
        applicationId = "com.zooro.mvvmnewsapp"
        minSdk = 21
        targetSdk = 35
        versionCode = 2
        versionName = "2.0"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas".toString())
            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.kotlinStdlib)
    implementation(libs.appcompat)
    implementation(libs.coreKtx)
    implementation(libs.constraintLayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidxJunit)
    androidTestImplementation(libs.espressoCore)

    // Architectural Components
    implementation(libs.lifecycleViewModelKtx)

    // Room
    implementation(libs.roomRuntime)
    implementation(libs.roomKtx)
    ksp(libs.roomCompiler)

    // Kotlin Extensions and Coroutines support for Room
    implementation(libs.roomKtx)

    // Coroutines
    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)

    // Coroutine Lifecycle Scopes
    implementation(libs.lifecycleViewModelKtx)
    implementation(libs.lifecycleRuntimeKtx)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converterGson)
    implementation(libs.loggingInterceptor)

    // Navigation Components
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.navigation.fragment)

    // Glide
    implementation(libs.glide)
    ksp(libs.glideCompiler)

    // Material Design
    implementation(libs.material)
    implementation(libs.webkit)
}