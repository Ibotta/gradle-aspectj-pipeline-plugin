import com.ibotta.gradle.aop.AopWeaveExtension

plugins {
    id("com.android.application")
    id(Plugin.PLUGIN_ID) version Plugin.VERSION apply false
}

apply(plugin = Plugin.PLUGIN_ID)

android {
    compileSdkVersion(Sdk.COMPILE_SDK_VERSION)

    defaultConfig {
        minSdkVersion(Sdk.MIN_SDK_VERSION)
        targetSdkVersion(Sdk.TARGET_SDK_VERSION)

        applicationId = JavaApp.APP_ID
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

    lintOptions {
        isWarningsAsErrors = true
        isAbortOnError = true
    }

    sourceSets.getByName("main") {
        java.srcDir("src/main/java")
        resources.srcDir("src/main/res")
    }
    sourceSets.getByName("test") {
        java.srcDir("src/test/java")
    }
}

dependencies {
    implementation(Dependencies.ASPECT_J_RUNTIME)
    implementation(SupportLibs.ANDROIDX_APPCOMPAT)

    testImplementation(platform(Dependencies.JUNIT_BOM))
    testImplementation(Dependencies.JUNIT_JUPITER)
    testImplementation(Dependencies.MOCKITO)
}

configure<AopWeaveExtension> {
    filter = SampleAppCommon.FILTER
}