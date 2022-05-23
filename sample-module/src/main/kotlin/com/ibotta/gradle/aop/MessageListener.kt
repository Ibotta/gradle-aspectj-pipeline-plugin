package com.ibotta.gradle.aop

interface MessageListener {
    fun onMessage(message: String, callerType: CallerType)
}