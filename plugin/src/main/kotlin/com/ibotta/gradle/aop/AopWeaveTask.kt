package com.ibotta.gradle.aop

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.tasks.TaskStateInternal
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

@CacheableTask
open class AopWeaveTask : DefaultTask() {
    // Using this as a Gradle cache busting strategy. Increment this value if needed to prevent an older weave cache
    // entry from getting picked up, despite new logic existing.
    @Input
    var version: Long = 1L
    
    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    var inPath: FileCollection? = null
    
    @Internal
    var aspectPathResolver: () -> FileCollection? = { null }
    
    @Classpath
    var aspectPath: FileCollection? = null
        get() = aspectPathResolver()
        set(value) { field = value }

    @Internal
    var classPathResolver: () -> FileCollection? = { null }
    
    @Classpath
    var classPath: FileCollection? = null
        get() = classPathResolver()
        set(value) { field = value }
    
    @Classpath
    var bootClassPath: FileCollection? = null
    
    @OutputDirectory
    var outputDir: File? = null
    
    @Internal
    var logPath: File? = null
    
    @TaskAction
    fun weave() {
        if (inPath!!.isEmpty()) {
            project.aopLog("No classes found. Will skip.")
            return
        }
        
        AspectJExecutor(
            inPath = inPath!!.asPath,
            aspectPath = aspectPath!!.asPath,
            outputDir = outputDir!!.absolutePath,
            logPath = logPath!!.absolutePath,
            classPath = classPath!!.asPath,
            bootClassPath = bootClassPath!!.asPath,
            logger = logger
        ).execute()
        
        inPath!!.forEach {
            copyNonClassFiles(it, outputDir!!)    
        }
    }

    /**
     * Copies all non .class files over from the pre-weave directory, to the weave output directory. This makes sure
     * things like resources and the MANIFEST make it over.
     */
    private fun copyNonClassFiles(from: File, to: File) {
        if (!from.exists()) {
            return 
        } else if (!from.isDirectory) {
            throw GradleException("Expected a directory: from=$from, to=$to")
        }
        
        from.listFiles { file -> file.isDirectory }
            .forEach { fromDir ->
                val toDir = File(to, fromDir.name).also { if (!it.exists()) it.mkdirs() }
                copyNonClassFiles(fromDir, toDir)
            }

        from.listFiles { file -> (!file.isDirectory && !file.name.endsWith(".class")) }
            .forEach { fromFile ->
                fromFile.copyTo(File(to, fromFile.name), true)
            }
    }
}