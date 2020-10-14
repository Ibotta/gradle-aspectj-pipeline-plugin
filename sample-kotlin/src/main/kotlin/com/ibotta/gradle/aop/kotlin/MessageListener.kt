package com.ibotta.gradle.aop.kotlin

interface MessageListener {
    fun onMessage(message: String, callerType: CallerType)
}