package com.ibotta.gradle.aop.kotlin

import com.ibotta.gradle.aop.CallerType
import com.ibotta.gradle.aop.MessageListener

class KotlinTargetExample {
    fun demonstrateKotlinAOP(messageListener: MessageListener) {
        messageListener.onMessage("Kotlin method with AOP attached is executed.", CallerType.TARGET)
    }
}