import com.ibotta.gradle.aop.AopWeaveExtension

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
    id("com.google.dagger.hilt.android") version "2.42"
    id(Plugin.PLUGIN_ID) version Plugin.VERSION apply false
}

apply(plugin = Plugin.PLUGIN_ID)

android {
    compileSdkVersion(Sdk.COMPILE_SDK_VERSION)

    defaultConfig {
        minSdkVersion(Sdk.MIN_SDK_VERSION)
        targetSdkVersion(Sdk.TARGET_SDK_VERSION)

        applicationId = KotlinApp.APP_ID
        versionCode = SampleAppCommon.APP_VERSION_CODE
        versionName = SampleAppCommon.APP_VERSION_NAME
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

    lint {
        warningsAsErrors = false
        abortOnError = true
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
    implementation("com.google.dagger:hilt-android:2.42")
    kapt("com.google.dagger:hilt-android-compiler:2.42")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    testImplementation(platform(Dependencies.JUNIT_BOM))
    testImplementation(Dependencies.JUNIT_JUPITER)
    testImplementation(Dependencies.MOCKK)
}

configure<AopWeaveExtension> {
    filter = SampleAppCommon.FILTER
}