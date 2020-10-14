package com.ibotta.gradle.aop.java;

public interface MessageListener {
    void onMessage(String message, CallerType callerType);
}
