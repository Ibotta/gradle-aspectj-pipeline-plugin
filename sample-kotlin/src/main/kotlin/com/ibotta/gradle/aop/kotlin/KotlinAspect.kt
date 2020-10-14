package com.ibotta.gradle.aop.kotlin

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before

@Aspect
class KotlinAspect {
    @Before("execution(* demonstrateKotlinAOP(..))")
    fun before(joinPoint: JoinPoint) {
        val messageListener = joinPoint.args[0] as MessageListener
        messageListener.onMessage("Kotlin AOP before hook triggered.", CallerType.BEFORE_HOOK)
    }

    @After("execution(* demonstrateKotlinAOP(..))")
    fun after(joinPoint: JoinPoint) {
        val messageListener = joinPoint.args[0] as MessageListener
        messageListener.onMessage("Kotlin AOP after hook triggered.", CallerType.AFTER_HOOK)
    }
}