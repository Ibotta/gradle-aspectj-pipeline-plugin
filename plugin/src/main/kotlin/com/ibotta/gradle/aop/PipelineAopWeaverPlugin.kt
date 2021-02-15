package com.ibotta.gradle.aop

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.hiya.plugins.JacocoAndroidUnitTestReportExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * A Gradle plugin that performs AOP weaving using a technique recommended by Gradle reps. It taps into Android's
 * bytcode manipulation pipeline which is a far more logical approach.
 *
 * The basic idea is:
 *      1. Change the Kotlin and Java compile output directories.
 *      2. Copy the Kotlin/Java compiled output to one directory.
 *      3. Weave the combined Kotlin/Java classes.
 *      4. Be registered as a bytcode generator so that Android recognizes the custom AOP weaving as a formal step
 *         in the build pipeline.
 */
class PipelineAopWeaverPlugin : Plugin<Project> {
    private companion object {
        private const val MISSING_PLUGIN_ERROR = "'com.android.application' or 'com.android.library' plugin required."
        private const val ANDROID_EXTENSION_NAME = "android"
        private const val ANDROID_JAR_TEMPLATE = "%s/platforms/%s/android.jar"
        private const val PRE_WEAVE_DIR_TEMPLATE = "preWeave/%s/%s"
        private const val POST_WEAVE_DIR_TEMPLATE = "postWeave/%s"
        private const val AOP_WEAVE_TASK = "aopWeave%s"
        private const val AOP_LOG = "aop.log"
    }

    override fun apply(project: Project) {
        val isAndroid = project.plugins.hasPlugin(AppPlugin::class.java)
        val isLibrary = project.plugins.hasPlugin(LibraryPlugin::class.java)

        if (!isAndroid && !isLibrary) {
            throw GradleException(MISSING_PLUGIN_ERROR)
        }

        project.aopLog("Plugin started.")

        val extension = project.extensions.create(AopWeaveExtension.AOP_WEAVE_EXTENSION, AopWeaveExtension::class.java)
        val android = project.extensions.findByName(ANDROID_EXTENSION_NAME) as BaseExtension

        project.afterEvaluate {
            val variants = if (isAndroid) {
                (android as AppExtension).applicationVariants
            } else {
                (android as LibraryExtension).libraryVariants
            }

            variants.forEach { variant ->
                // Various strings in different case forms which we'll need when creating directories, or finding
                // tasks.
                val javaLangLowercase = LANG_JAVA.toLowerCase()
                val kotlinLangLowercase = LANG_KOTLIN.toLowerCase()
                val variantNameLowercase = variant.name
                val variantNameCapitalized = variant.name.capitalize()

                // We will configure the Kotlin and Java compilation tasks to output classes into "pre-weave"
                // directories.
                val preWeaveJavaDir = project.layout
                    .buildDirectory
                    .dir(PRE_WEAVE_DIR_TEMPLATE.format(variantNameLowercase, javaLangLowercase))
                val preWeaveKotlinDir = project.layout
                    .buildDirectory
                    .dir(PRE_WEAVE_DIR_TEMPLATE.format(variantNameLowercase, kotlinLangLowercase))

                // This is where we will output woven Kotlin and Java classes.
                val postWeaveDir = project.layout
                    .buildDirectory
                    .dir(POST_WEAVE_DIR_TEMPLATE.format(variantNameLowercase))

                // Now we'll acquire the Task providers for the compilation steps, as well as our own custom weave
                // task. Task providers allow us to tell Gradle how we will want the tasks to be configured and executed,
                // before the tasks exists.
                val kotlinCompileProvider = project.kotlinCompileTaskProvider(variantNameCapitalized)
                val javaCompileProvider = project.javaCompileTaskProvider(variantNameCapitalized)
                val kaptTaskProvider = project.kaptTaskProvider(variantNameCapitalized)
                val aopWeaveProvider = project.tasks
                    .register(AOP_WEAVE_TASK.format(variantNameCapitalized), AopWeaveTask::class.java)
                val jacocoReportTaskProvider = project.jacocoReportTaskProvider(variantNameCapitalized)

                // Before Kapt can run, we'll need to restore any pre-weave classes from previous
                // runs of compile tasks back into the destination directories. This is where Kapt
                // expects to find these classes. If we don't do this, incremental builds will fail.
                if (kaptTaskProvider != null) {
                    configureKaptTask(kaptTaskProvider, kotlinCompileProvider, javaCompileProvider, preWeaveJavaDir, preWeaveKotlinDir)
                }

                // We'll need to configure the Kotlin/Java tasks to output compiled classes to a different directory
                // than what it uses by default.
                if (kotlinCompileProvider != null) {
                    configureKotlinCompileTask(kotlinCompileProvider, preWeaveKotlinDir)
                }
                if (javaCompileProvider != null) {
                    configureJavaCompileTask(project, javaCompileProvider, preWeaveKotlinDir, preWeaveJavaDir)
                }

                // Set up the configuration for the JacocoReport task in case the build requires it.
                if (jacocoReportTaskProvider != null) {
                    configureJacocoReportTask(jacocoReportTaskProvider, postWeaveDir)
                }

                // We'll need this jar for weaving.
                val androidJarPath = ANDROID_JAR_TEMPLATE.format(
                    android.sdkDirectory.absolutePath,
                    android.compileSdkVersion
                )

                // Finally, we can configure our AOP weaving task.
                configureAopWeaveTask(
                    project = project,
                    extension = extension,
                    variantNameCapitalized = variantNameCapitalized,
                    javaCompileProvider = javaCompileProvider,
                    kotlinCompileProvider = kotlinCompileProvider,
                    aopWeaveProvider = aopWeaveProvider,
                    androidJarPath = androidJarPath,
                    preWeaveJavaDir = preWeaveJavaDir,
                    preWeaveKotlinDir = preWeaveKotlinDir,
                    postWeaveDir = postWeaveDir
                )

                // The last step is to let the Android build logic know it should expect bytecode-modified classes.
                // This is a mechanism that allows us to add our own work to a pipeline before final module assembly.
                variant.registerPostJavacGeneratedBytecode(project.files(aopWeaveProvider.map { it.outputDir!! }))
            }
        }
    }

    /**
     * Swaps any pre-weave classes generated by previous compile tasks back into the destination
     * directories. Then runs Kapt. Then copies the classes back out of the destination
     * directories, into the pre-weave directories.
     *
     * This ensures incremental builds work as expected.
     */
    private fun configureKaptTask(
        kaptTaskProvider: TaskProvider<Task>,
        kotlinCompileTaskProvider: TaskProvider<Task>?,
        javaCompileTaskProvider: TaskProvider<Task>?,
        preWeaveKotlinDir: Provider<Directory>,
        preWeaveJavaDir: Provider<Directory>
    ) {
        kaptTaskProvider.configure {
            swapPreWeaveContent(kotlinCompileTaskProvider, preWeaveKotlinDir)
            swapPreWeaveContent(javaCompileTaskProvider, preWeaveJavaDir)
        }
    }

    /**
     * Configure the Kotlin compiler to output to the pre-weave directory. We will also hook up some actions that will
     * move compiled classes around. This is done to retain incremental build functionality, and to prepare for AOP
     * weaving.
     */
    private fun configureKotlinCompileTask(
        kotlinCompileProvider: TaskProvider<Task>,
        preWeaveKotlinDir: Provider<Directory>
    ) {
        kotlinCompileProvider.configure {
            outputs.dir(preWeaveKotlinDir)

            swapPreWeaveContent(this, preWeaveKotlinDir)
        }
    }

    /**
     * Configure the Java compiler to output to the pre-weave directory. We will also hook up some actions that will
     * move compiled classes around. This is done to retain incremental build functionality, and to prepare for AOP
     * weaving.
     */
    private fun configureJavaCompileTask(
        project: Project,
        javaCompileProvider: TaskProvider<Task>,
        preWeaveKotlinDir: Provider<Directory>,
        preWeaveJavaDir: Provider<Directory>
    ) {
        javaCompileProvider.configure {
            (this as AbstractCompile).classpath += project.files(preWeaveKotlinDir)
            outputs.dir(preWeaveJavaDir)

            swapPreWeaveContent(this, preWeaveJavaDir)
        }
    }

    /**
     * Configure the JacocoReport task's class directories to include the post-weave directory we've defined here.
     * Our Jacoco plugin, [JacocoAndroidUnitTestReportExtension], uses the class directories to generate it's report.
     * When our post-weave directories are not included, the generated report is empty.
     */
    private fun configureJacocoReportTask(
        jacocoReportTaskProvider: TaskProvider<JacocoReport>,
        postWeaveDir: Provider<Directory>
    ) {
        jacocoReportTaskProvider.configure {
            val jacocoExt = project.jacocoAndroidReportExtension()

            if (jacocoExt != null) {
                // This file tree represents the post-weave directory we've
                // defined, along with the excludes we set for the Jacoco project
                val postWeaveFiltered = project.fileTree(postWeaveDir).apply {
                    setExcludes(jacocoExt.excludes)
                }
                // Re-set the Jacoco task's class directories to include our post-weave directory with our desired
                // excludes
                classDirectories.setFrom(classDirectories + postWeaveFiltered)
            }
        }
    }

    private fun configureAopWeaveTask(
        project: Project,
        extension: AopWeaveExtension,
        variantNameCapitalized: String,
        javaCompileProvider: TaskProvider<Task>?,
        kotlinCompileProvider: TaskProvider<Task>?,
        aopWeaveProvider: TaskProvider<AopWeaveTask>,
        androidJarPath: String,
        preWeaveJavaDir: Provider<Directory>,
        preWeaveKotlinDir: Provider<Directory>,
        postWeaveDir: Provider<Directory>
    ) {
        aopWeaveProvider.configure {
            inPath = project.files(preWeaveJavaDir.get().asFile, preWeaveKotlinDir.get().asFile)
            aspectPathResolver = {
                project.files(gatherAspectPaths(project, variantNameCapitalized, preWeaveJavaDir, preWeaveKotlinDir))
                    .also {
                        if (extension.filter.isNotEmpty()) {
                            project.aopLog("Weaving filter in ${project.name} is: ${extension.filter}")
                        }
                    }
                    .filter { file ->
                        file.canonicalPath.contains(extension.filter)
                    }
            }
            classPathResolver = { gatherClasspaths(project, variantNameCapitalized) }
            bootClassPath = project.files(androidJarPath)
            outputDir = postWeaveDir.get().asFile
            logPath = project.layout.buildDirectory.file(AOP_LOG).get().asFile

            val dependencies = when {
                ((javaCompileProvider != null) && (kotlinCompileProvider != null)) -> {
                    arrayOf(javaCompileProvider, kotlinCompileProvider)
                }
                (javaCompileProvider != null) -> arrayOf(javaCompileProvider)
                (kotlinCompileProvider != null) -> arrayOf(kotlinCompileProvider)
                else -> throw RuntimeException("Either Java, Kotlin, or both types of compile task must be present.")
            }

            dependsOn(dependencies)
        }
    }

    private fun restoreIncrementalBehaviorAction(compileTask: AbstractCompile, preWeaveDir: Provider<Directory>) {
        if (!preWeaveDir.get().asFile.isEmpty()) {
            compileTask.apply {
                project.copy {
                    from(preWeaveDir)
                    into(destinationDirectory)
                }
            }
        }
    }

    private fun cleanUpAfterWeaving(compileTask: AbstractCompile, preWeaveDir: Provider<Directory>) {
        compileTask.apply {
            project.copy {
                from(destinationDirectory)
                into(preWeaveDir)
            }

            project.delete(destinationDir)
        }
    }

    private fun gatherAspectPaths(
        project: Project,
        variantNameCapitalized: String,
        preWeaveJavaDir: Provider<Directory>,
        preWeaveKotlinDir: Provider<Directory>
    ): ConfigurableFileCollection {
        val javaCompileTask = project.javaCompileTask(variantNameCapitalized)
        val kotlinCompileTask = project.kotlinCompileTask(variantNameCapitalized)

        return when {
            ((javaCompileTask != null) && (kotlinCompileTask != null)) -> project.files(
                javaCompileTask.classpath,
                kotlinCompileTask.classpath,
                preWeaveJavaDir.get().asFile,
                preWeaveKotlinDir.get().asFile
            )
            (javaCompileTask != null) -> project.files(javaCompileTask.classpath, preWeaveJavaDir.get().asFile)
            (kotlinCompileTask != null) -> project.files(kotlinCompileTask.classpath, preWeaveKotlinDir.get().asFile)
            else -> throw RuntimeException("Either Java, Kotlin, or both types of compile task must be present.")
        }
    }

    private fun gatherClasspaths(project: Project, variantNameCapitalized: String): ConfigurableFileCollection {
        val javaCompileTask = project.javaCompileTask(variantNameCapitalized)
        val kotlinCompileTask = project.kotlinCompileTask(variantNameCapitalized)

        return when {
            ((javaCompileTask != null) && (kotlinCompileTask != null)) -> project.files(
                javaCompileTask.classpath,
                kotlinCompileTask.classpath
            )
            (javaCompileTask != null) -> project.files(javaCompileTask.classpath)
            (kotlinCompileTask != null) -> project.files(kotlinCompileTask.classpath)
            else -> throw RuntimeException("Either Java, Kotlin, or both types of compile task must be present.")
        }
    }

    private fun Task.swapPreWeaveContent(compileTaskProvider: TaskProvider<Task>?, preWeaveDir: Provider<Directory>) {
        if (compileTaskProvider != null) {
            val compileTask = compileTaskProvider.get() as AbstractCompile
            swapPreWeaveContent(compileTask, preWeaveDir)
        }
    }

    private fun Task.swapPreWeaveContent(compileTask: Task, preWeaveDir: Provider<Directory>) {
        doFirst {
            restoreIncrementalBehaviorAction(compileTask as AbstractCompile, preWeaveDir)
        }

        doLast {
            cleanUpAfterWeaving(compileTask as AbstractCompile, preWeaveDir)
        }
    }
}