// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false

    // Kotlin 버전 업그레이드
    alias(libs.plugins.jetbrains.kotlin.android) version "2.1.0" apply false

    id("com.google.gms.google-services") version "4.4.3" apply false

    // Hilt 의존성 주입 (DI) 라이브러리 사용
    id("com.google.dagger.hilt.android") version "2.55" apply false

    // Spotless 의존성 주입 (Ktlint 관련 툴)
    id("com.diffplug.spotless") version "6.22.0"

    // Compose Complier 플러그인 추가
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

    spotless {
        kotlin {
            target("**/*.kt")
            ktlint()
        }
    }
}