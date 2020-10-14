package com.ibotta.gradle.aop.mixed;

public class JavaTargetExample {
    public void demonstrateJavaAOP(MessageListener messageListener) {
        messageListener.onMessage("Java method with AOP attached is executed.", CallerType.TARGET);
    }
}
