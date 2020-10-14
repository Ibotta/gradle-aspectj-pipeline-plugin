package com.ibotta.gradle.aop.java;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class AopTest {
    @Mock private MessageListener mockMessageListener;
    private JavaTargetExample target = new JavaTargetExample();

    @BeforeEach
    public void before() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenAOPBeforeHook_whenTargetInvoked_thenAOPMethodRuns() {
        target.demonstrateJavaAOP(mockMessageListener);
        verify(mockMessageListener).onMessage(anyString(), eq(CallerType.BEFORE_HOOK));
    }

    @Test
    public void givenAOPAfterHook_whenTargetInvoked_thenAOPMethodRuns() {
        target.demonstrateJavaAOP(mockMessageListener);
        verify(mockMessageListener).onMessage(anyString(), eq(CallerType.AFTER_HOOK));
    }
}
