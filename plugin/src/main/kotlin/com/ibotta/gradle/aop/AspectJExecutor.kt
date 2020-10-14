package com.ibotta.gradle.aop

import org.aspectj.bridge.IMessage
import org.aspectj.bridge.IMessageHandler
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.logging.Logger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * A wrapper around the AspectJ weaver to make it a little easier to work with.
 */
class AspectJExecutor(
    private val inPath: String, 
    private val aspectPath: String,
    private val outputDir: String,
    private val logPath: String,
    private val classPath: String,
    private val bootClassPath: String,
    private val logger: Logger
) {
    companion object {
        private const val CMD_IN_PATH = "-inpath"
        private const val CMD_ASPECT_PATH = "-aspectpath"
        private const val CMD_OUTPUT_DIR = "-d"
        private const val CMD_CLASS_PATH = "-classpath"
        private const val CMD_BOOT_CLASS_PATH = "-bootclasspath"
        private const val CMD_LOG = "-log"
        private const val CMD_SHOW_WEAVE_INFO = "-showWeaveInfo"
        private const val CMD_JAVA_1_8 = "-1.8"
        private val lock = ReentrantLock()
    }
    
    fun execute() {
        val args = arrayOf(
            CMD_SHOW_WEAVE_INFO,
            CMD_JAVA_1_8,
            // This path should include classes that the Aspects might act on. For us,
            // this means files that use our own AOP annotations.
            CMD_IN_PATH,
            inPath,
            // This path should include classes that have Aspects.
            CMD_ASPECT_PATH,
            aspectPath,
            // Weave output directory.
            CMD_OUTPUT_DIR,
            outputDir,
            // The classpath can just be the same as the one used to compile our files.
            CMD_CLASS_PATH,
            classPath,
            // Useful log output! Will show weaving issues (if any), and will tell the
            // story of what classes were woven. Look for this log in
            // <project-root>/build.
            CMD_LOG,
            logPath,
            // I think this is the classpath that the ajc (AspectJ compiler) needs to
            // do its thing. DON'T CHANGE.
            CMD_BOOT_CLASS_PATH,
            bootClassPath
        )

        // Use a lock to prevent concurrent AspectJ runs from occurring. AspectJ does not appear to be thread safe,
        // and concurrency was causing random build failures as well as weaving failures.
        lock.withLock {
            val handler = MessageHandler(true)
            Main().run(args, handler)

            handler.getMessages(null, true).forEach {
                when (it.kind) {
                    IMessage.ABORT, IMessage.ERROR, IMessage.FAIL -> logger.error(it.message, it.thrown)
                    IMessage.WARNING -> logger.warn(it.message, it.thrown)
                    IMessage.INFO -> logger.info(it.message, it.thrown)
                    IMessage.DEBUG -> logger.debug(it.message, it.thrown)
                }
            }
        }
    }
}