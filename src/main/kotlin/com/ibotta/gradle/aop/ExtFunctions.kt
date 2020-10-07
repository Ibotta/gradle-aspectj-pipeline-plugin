package com.ibotta.gradle.aop

import com.hiya.plugins.JacocoAndroidUnitTestReportExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
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

fun Project.javaCompileTask(variantName: String) =
    firstTask(JAVA_COMPILE_TASK_TEMPLATE.format(variantName)) as AbstractCompile

fun Project.javaCompileTaskProvider(variantName: String) =
    tasks.named(JAVA_COMPILE_TASK_TEMPLATE.format(variantName))

fun Project.kotlinCompileTaskProvider(variantName: String) =
    tasks.named(KOTLIN_COMPILE_TASK_TEMPLATE.format(variantName))

fun Project.jacocoReportTaskProvider(variantName: String) =
    tasks.named(JACOCO_REPORT_TASK_TEMPLATE.format(variantName)) as TaskProvider<JacocoReport>

fun Project.jacocoAndroidReportExtension() =
    extensions.findByName(JACOCO_ANDROID_REPORT_EXTENSION) as JacocoAndroidUnitTestReportExtension

fun Project.firstTask(taskName: String) = getTasksByName(taskName, false).first() ?:
    throw GradleException("Task is required, but not found: $taskName")

fun File?.isEmpty() = ((this == null) || (list() == null) || list().isEmpty())
