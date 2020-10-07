plugins {
    id("org.gradle.kotlin.kotlin-dsl") version "1.4.1"
    `maven-publish`
    kotlin("plugin.serialization") version "1.4.10"
}

repositories {
    google()
    jcenter()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    implementation("com.android.tools.build:gradle:4.0.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
    implementation("org.aspectj:aspectjtools:1.9.6")
    implementation("com.hiya:jacoco-android:0.2")
}

val pluginId = "com.ibotta.gradle.aop"

gradlePlugin {
    plugins {
        create("aopWeaverPipeline") {
            id = pluginId
            implementationClass = "$pluginId.PipelineAopWeaverPlugin"
        }
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://ibdolphin.jfrog.io/ibdolphin/mvn-private/")
            credentials {
                username = System.getenv("TRAVIS_MAVEN_USER")
                password = System.getenv("TRAVIS_MAVEN_PASS")
            }
        }
    }

    val repoUrl = "github.com/Ibotta/gradle-aspectj-pipeline-plugin"

    publications {
        create<MavenPublication>("mavenJava") {
            groupId = pluginId
            artifactId = "gradle-aspectj-pipeline-plugin"
            version = "1.0.0"

            from(components["java"])

            pom {
                name.set("Gradle AspectJ Android Plugin")
                description.set("A plugin which performs AspectJ weaving on Android projects.")
                url.set("https://$repoUrl")
                developers {
                    developer {
                        id.set("Ibotta")
                    }
                }
                scm {
                    connection.set("scm:git@$repoUrl.git")
                    developerConnection.set("scm:git@$repoUrl.git")
                    url.set("https://$repoUrl")
                }
            }
        }
    }
}
