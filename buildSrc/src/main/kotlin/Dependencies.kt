object Plugin {
    const val GROUP = "com.ibotta"
    const val PLUGIN_ID = "$GROUP.gradle.aop"
    const val NAME = "aopWeaverPipeline"
    const val IMPLEMENTATION_CLASS = "$PLUGIN_ID.PipelineAopWeaverPlugin"
    const val WEBSITE = "https://github.com/Ibotta/gradle-aspectj-pipeline-plugin"
    const val DESCRIPTION = "A Gradle plugin for Android projects which performs AspectJ weaving using Android's bytcode manipulation pipeline."
    const val DISPLAY_NAME = "Android AspectJ Gradle Plugin"
    const val JVM_TARGET = "1.8"
    private const val BUILD_NUMBER = "" // Dynamically updated by publishLocal.sh on Travis. Otherwise left as-is.
    const val VERSION = "1.3.0$BUILD_NUMBER"
    val TAGS = listOf("Android", "AspectJ", "Kotlin", "Java")
}

object SampleAppCommon {
    const val APP_VERSION_CODE = 1
    const val APP_VERSION_NAME = "1.0.0"
    const val FILTER = "com/ibotta/gradle/aop"
}

object MixedApp {
    const val APP_ID = "com.ibotta.gradle.aop.mixed"
}

object KotlinApp {
    const val APP_ID = "com.ibotta.gradle.aop.kotlin"
}

object JavaApp {
    const val APP_ID = "com.ibotta.gradle.aop.java"
}

object Sdk {
    const val MIN_SDK_VERSION = 21
    const val TARGET_SDK_VERSION = 31
    const val COMPILE_SDK_VERSION = 31
}

object Versions {
    const val ANDROID_BUILD_TOOLS_VERSION = "7.0.4"
    const val APPCOMPAT_VERSION = "1.4.1"
    const val ASPECTJ_VERSION = "1.9.6"
    const val GRADLE_PLUGIN_PUBLISH_VERSION = "0.20.0"
    const val JACOCO_ANDROID_VERSION = "0.2"
    const val JUNIT_VERSION = "5.8.2"
    const val KOTLIN_VERSION = "1.6.10"
    const val KOTLIN_DSL_VERSION = "2.2.0"
    const val KOTLINX_SERIALIZATION_RUNTIME_VERSION = "0.20.0"
    const val MOCKITO_CORE_VERSION = "4.2.0"
    const val MOCKK_VERSION = "1.12.2"
}

object SupportLibs {
    const val ANDROIDX_APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APPCOMPAT_VERSION}"
}

object Dependencies {
    const val ANDROID_BUILD_TOOLS = "com.android.tools.build:gradle:${Versions.ANDROID_BUILD_TOOLS_VERSION}"
    const val KOTLIN_GRADLE_PLUGIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN_VERSION}"
    const val KOTLIN_SERIALIZATION_RUNTIME = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.KOTLINX_SERIALIZATION_RUNTIME_VERSION}"
    const val ASPECT_J_TOOLS = "org.aspectj:aspectjtools:${Versions.ASPECTJ_VERSION}"
    const val ASPECT_J_RUNTIME = "org.aspectj:aspectjrt:${Versions.ASPECTJ_VERSION}"
    const val JACOCO_ANDROID = "com.hiya:jacoco-android:${Versions.JACOCO_ANDROID_VERSION}"
    const val JUNIT_BOM = "org.junit:junit-bom:${Versions.JUNIT_VERSION}"
    const val JUNIT_JUPITER = "org.junit.jupiter:junit-jupiter:${Versions.JUNIT_VERSION}"
    const val MOCKITO = "org.mockito:mockito-core:${Versions.MOCKITO_CORE_VERSION}"
    const val MOCKK = "io.mockk:mockk:${Versions.MOCKK_VERSION}"
}
