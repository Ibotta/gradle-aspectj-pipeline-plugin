package com.ibotta.gradle.aop.mixed

interface MessageListener {
    fun onMessage(message: String, callerType: CallerType)
}