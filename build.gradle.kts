import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm") version Versions.KOTLIN_VERSION
    id("org.gradle.kotlin.kotlin-dsl") version Versions.KOTLIN_DSL_VERSION
    id("com.android.application") version Versions.ANDROID_BUILD_TOOLS_VERSION apply false
    kotlin("android") version Versions.KOTLIN_VERSION apply false
    kotlin("plugin.serialization") version Versions.KOTLIN_VERSION apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://plugins.gradle.org/m2/")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = Plugin.JVM_TARGET
        }
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = Plugin.JVM_TARGET
        targetCompatibility = Plugin.JVM_TARGET
    }
}
