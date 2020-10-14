plugins {
    id("org.gradle.kotlin.kotlin-dsl")
    id("maven-publish")
    kotlin("plugin.serialization")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version Versions.GRADLE_PLUGIN_PUBLISH_VERSION
}

dependencies {
    implementation(Dependencies.ANDROID_BUILD_TOOLS)
    implementation(Dependencies.KOTLIN_GRADLE_PLUGIN)
    implementation(Dependencies.KOTLIN_SERIALIZATION_RUNTIME)
    implementation(Dependencies.ASPECT_J_TOOLS)
    implementation(Dependencies.JACOCO_ANDROID)
}

pluginBundle {
    website = Plugin.WEBSITE
    vcsUrl = Plugin.WEBSITE
    tags = Plugin.TAGS
    group = Plugin.GROUP
}

gradlePlugin {
    plugins {
        create(Plugin.NAME) {
            id = Plugin.PLUGIN_ID
            group = Plugin.GROUP
            displayName = Plugin.DISPLAY_NAME
            description = Plugin.DESCRIPTION
            version = Plugin.VERSION
            implementationClass = Plugin.IMPLEMENTATION_CLASS
        }
    }
}
