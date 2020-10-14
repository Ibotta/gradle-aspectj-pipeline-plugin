package com.ibotta.gradle.aop.mixed

class KotlinTargetExample {
    fun demonstrateKotlinAOP(messageListener: MessageListener) {
        messageListener.onMessage("Kotlin method with AOP attached is executed.", CallerType.TARGET)
    }
}