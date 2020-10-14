package com.ibotta.gradle.aop

import com.hiya.plugins.JacocoAndroidUnitTestReportExtension
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.io.File

private const val JAVA_COMPILE_TASK_TEMPLATE = "compile%sJavaWithJavac"
private const val KOTLIN_COMPILE_TASK_TEMPLATE = "compile%sKotlin"
private const val JACOCO_REPORT_TASK_TEMPLATE = "jacocoTest%sUnitTestReport"
private const val JACOCO_ANDROID_REPORT_EXTENSION = "jacocoAndroidUnitTestReport"
const val LANG_JAVA = "Java"
const val LANG_KOTLIN = "Kotlin"

fun Project.aopLog(msg: String) = logger.warn("/** Ibotta AOP **/: $msg")

fun Project.javaCompileTask(variantName: String): AbstractCompile? =
    compileTask(JAVA_COMPILE_TASK_TEMPLATE.format(variantName))

fun Project.kotlinCompileTask(variantName: String): AbstractCompile? =
    compileTask(KOTLIN_COMPILE_TASK_TEMPLATE.format(variantName))

private fun Project.compileTask(taskName: String): AbstractCompile? {
    val firstTask = firstTask(taskName)

    return if (firstTask != null) {
        firstTask as AbstractCompile
    } else {
        null
    }
}

fun Project.javaCompileTaskProvider(variantName: String) =
    tasks.namedOrNull(JAVA_COMPILE_TASK_TEMPLATE.format(variantName))

fun Project.kotlinCompileTaskProvider(variantName: String) =
    tasks.namedOrNull(KOTLIN_COMPILE_TASK_TEMPLATE.format(variantName))

fun Project.jacocoReportTaskProvider(variantName: String): TaskProvider<JacocoReport>? {
    val jacocoTask = tasks.namedOrNull(JACOCO_REPORT_TASK_TEMPLATE.format(variantName))

    return if (jacocoTask != null) {
        jacocoTask as TaskProvider<JacocoReport>
    } else {
        null
    }
}

fun Project.jacocoAndroidReportExtension(): JacocoAndroidUnitTestReportExtension? {
    val jacocoExt = extensions.findByName(JACOCO_ANDROID_REPORT_EXTENSION)

    return if (jacocoExt != null) {
        jacocoExt as JacocoAndroidUnitTestReportExtension
    } else {
        null
    }
}

fun Project.firstTask(taskName: String) = getTasksByName(taskName, false).firstOrNull()

fun TaskContainer.namedOrNull(taskName: String): TaskProvider<Task>? {
    return try {
        named(taskName)
    } catch (e: UnknownDomainObjectException) {
        null
    }
}

fun File?.isEmpty() = ((this == null) || (list() == null) || list().isEmpty())
