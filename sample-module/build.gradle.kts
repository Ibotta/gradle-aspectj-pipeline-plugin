import com.ibotta.gradle.aop.AopWeaveExtension

plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android-extensions")
    id(Plugin.PLUGIN_ID) version Plugin.VERSION apply false
}

apply(plugin = Plugin.PLUGIN_ID)

android {
    compileSdkVersion(Sdk.COMPILE_SDK_VERSION)

    defaultConfig {
        minSdkVersion(Sdk.MIN_SDK_VERSION)
        targetSdkVersion(Sdk.TARGET_SDK_VERSION)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    lintOptions {
        isWarningsAsErrors = false
        isAbortOnError = true
    }

    sourceSets.getByName("main") {
        java.srcDir("src/main/kotlin")
        resources.srcDir("src/main/res")
    }
    sourceSets.getByName("test") {
        java.srcDir("src/test/kotlin")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk7"))
    implementation(Dependencies.ASPECT_J_RUNTIME)
    implementation(SupportLibs.ANDROIDX_APPCOMPAT)
}