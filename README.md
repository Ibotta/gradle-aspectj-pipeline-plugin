  
# Android AspectJ Gradle Plugin
A Gradle plugin for Kotlin/Java Android projects which performs AspectJ weaving using Android's bytcode manipulation pipeline.

# Table Of Contents
- [Who This Is For?](#who-this-is-for)
- [How This Project Came Into Existence?](#how-this-project-came-into-existence)
- [How Is This Implementation Different?](#how-is-this-implementation-different)
- [How Do I Use This Plugin?](#how-do-i-use-this-plugin)
- [We Have Sample Apps!](#we-have-sample-apps)
- [How To Build The Project](#how-to-build-the-project)

# Who This Is For?
This plugin may be suitable for anyone looking to leverage the power of AOP in their Android projects, regardless of whether you use Kotlin, Java, or a mix of languages. It is _especially_ for those of you who have tried many similar plugins, but have failed to get them to work (like us).

If you are unfamiliar with AspectJ and/or AOP weaving in general, you will want to familiarize yourself with the concepts:

- [I Want My AOP](https://www.infoworld.com/article/2073918/i-want-my-aop---part-1.html)
- [AspectJ](https://www.baeldung.com/aspectj)


# How This Project Came Into Existence?
There are several libraries out there that aim to solve the problem of AspectJ weaving on Android. We've tried all of them, but they simply didn't work for us. There were many reasons this could have been. Maybe our internal projects are just too complex with all of the Gradle plugins we depend on, causing some incompatibility. Sometimes the library we'd try was just too far out of date, and incompatible with the latest versions of Kotlin, Android, Gradle, etc. Whatever the cause, we ended up writing our own solution.

Our requirements were simple. *We needed a tool that could perform AspectJ weaving of our mixed Kotlin/Java Android project. And we should be able to verify that woven code is executing as expected with unit tests.*

At the start of this project, we had little to no experience writing Gradle Plugins. We were fortunate enough to have a relationship with some members of the Gradle team, who aided us in accomplishing our goals. Rather than fumble our way to a solution, we ended up with one that was guided by the Gradle team's expertise in their own tooling.

For a more in-depth look at our journey, visit our blog post: https://medium.com/building-ibotta/ibottas-solution-for-aop-weaving-on-android-944a432294c5

# How Is This Implementation Different?
This plugin leverages a mechanism in the Android build which we were introduced to as the "bytecode manipulation pipeline". Essentially, the plugin registers itself with the build as something that will manipulate bytecode, thus making it a formal part of the overall build. Unfortunately, documentation on this mechanism seems to be scarce. The relevant hook for doing this registration can be found here, though: https://android.googlesource.com/platform/tools/base/+/refs/heads/mirror-goog-studio-master-dev/build-system/gradle-core/src/main/java/com/android/build/gradle/api/BaseVariant.java#600

The AOP weaving occurs after compilation of Java, Kotlin, or both (in the case of a "mixed" project). And then the AspectJ output is provided to the build for subsequent packaging, etc. From our experience reading through the source code of similar projects, this appears to be a simpler approach to solving the problem. And it works well! The overall footprint of the plugin is miniscule.

# How Do I Use This Plugin?

### Step 1 - Update your root project's Gradle build script to depend on the plugin:

#### Groovy syntax `build.gradle` file:
```
buildscript {
	...
	repositories {
		...
		maven { url "https://plugins.gradle.org/m2/" }
		...
	}

	dependencies {
		...
		classpath "com.ibotta:plugin:1.0.7"
		...
	}
}

...
```

#### Kotlin syntax `build.gradle.kts` file:
```
buildscript {
	...
	repositories {
		...
		maven("https://plugins.gradle.org/m2/")
		...
	}

	dependencies {
		...
		classpath("com.ibotta:plugin:1.0.7")
		...
	}
}

...
```

### Step 2 - Update your Android sub-project's Gradle build script to apply and configure the plugin:

####  Groovy syntax `build.gradle` file:
```
plugins {
	...
	id 'com.ibotta.gradle.aop'
	...
}

android {
	...
}

dependencies {
	...
}

// This block is optional, but recommended. The "filter" will be used to limit the scope of classes
// that AspectJ will look at. For performance reasons, and to avoid accidentally picking up AspectJ
// annotations from other libraries, specify a filter which will target only your code.
aopWeave {
	filter = "com/example/myapp"
}

...
```

####  Kotlin syntax `build.gradle.kts` file:
```
plugins {
	...
	id("com.ibotta.gradle.aop")
	...
}

android {
	...
}

dependencies {
	...
}

// This block is optional, but recommended. The "filter" will be used to limit the scope of classes
// that AspectJ will look at. For performance reasons, and to avoid accidentally picking up AspectJ
// annotations from other libraries, specify a filter which will target only your code.
aopWeave {
	filter = "com/example/myapp"
}

...
```


# We Have Sample Apps!
In this project, you will find a few sample apps that use this plugin to perform some simple AOP weaving. They include tests to help prove that the weaving occurred, and is running as expected. Maybe you have a Kotlin only project, or Java only, or a mix! We have examples of each:

 - [Kotlin Only Sample App](sample-kotlin)
 - [Java Only Sample App](sample-java)
 - [Mixed Java/Kotlin Sample App](sample-mixed)

# How To Build The Project
If you've made no code changes to the project, and simply want to build it, then you can just run the following command from the root of the project: `./gradlew build`

If you've made changes to one of the sample projects, ***but not the plugin source code,*** you can simply build the sample project and run it. For example (building): `./gradlew :sample-mixed:build`

If you've made changes to the plugin's source code, and would like to test them using one of the sample applications, then you will want to do the following:

1. Update the `Dependencies.kt` file by incrementing the `VERSION` variable to the next minor value (or, something unique, that has not been used before). This will make sure a new version of the plugin is constructed before publishing it to Maven local.
2. Execute the `./publishLocal.sh` script in the root director. This will push the plugin, as an artifact, to the Maven local repository.
3. Finally, build and run the sample project you're testing with. The sample projects reference the same `VERSION` variable which you updated in step number 1. It should automatically pick up the version of the plugin published to Maven local in step number 2.
