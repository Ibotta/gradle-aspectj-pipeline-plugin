package com.ibotta.gradle.aop.kotlin

class KotlinTargetExample {
    fun demonstrateKotlinAOP(messageListener: MessageListener) {
        messageListener.onMessage("Kotlin method with AOP attached is executed.", CallerType.TARGET)
    }
}