import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")

    // Hilt 의존성 주입 (DI) 라이브러리 사용
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")

    // Parcelable 이용을 위한 플러그인 추가
    id("kotlin-parcelize")

    // Compose Compiler 플러그인 추가
    id("org.jetbrains.kotlin.plugin.compose")

    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

val properties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.example.chaining"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.chaining"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "DATA_OPEN_API_KEY", properties["DATA_OPEN_API_KEY"].toString())

        buildConfigField(
            "String",
            "GOOGLE_API_WEB_CLIENT_ID",
            properties["GOOGLE_API_WEB_CLIENT_ID"].toString()
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Google Sign-In (Credentials API 포함)
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")

    // Google Identity Services (Google 로그인 팝업 등을 위해 필요)
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    // 구글 Firebase 사용
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")

    // Hilt 의존성 주입 (DI) 라이브러리 사용
    implementation("com.google.dagger:hilt-android:2.55")
    kapt("com.google.dagger:hilt-android-compiler:2.55")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Room (로컬 DB) 의존성 주입
    implementation("androidx.room:room-runtime:2.7.2")
    kapt("androidx.room:room-compiler:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")

    // Retrofit + Coroutine (API 통신) 의존성 주입
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Navigation 라이브러리 의존성 주입
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Coil (Jetpack Compose용)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // @kotlinx.serialization.Serializable을 쓰기 위한 의존성 주입
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    // JSON 파싱을 위한 의존성 주입
    implementation("com.google.code.gson:gson:2.10.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}