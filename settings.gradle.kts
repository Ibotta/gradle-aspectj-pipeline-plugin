pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.android.application") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
            if (requested.id.id == "com.ibotta.gradle.aop") {
                useModule("com.ibotta.gradle.aop:com.ibotta.gradle.aop.gradle.plugin:${requested.version}")
            }
        }
    }
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
        maven("https://plugins.gradle.org/m2/")
    }
}

rootProject.name = "gradle-aspectj-pipeline"
rootProject.buildFileName = "build.gradle.kts"

if (System.getProperty("publishMode", "").isEmpty()) {
    include("plugin", "sample-mixed", "sample-kotlin", "sample-java", "sample-module")
} else {
    include("plugin")
}