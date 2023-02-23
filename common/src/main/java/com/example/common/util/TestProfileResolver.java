package com.example.common.util;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.test.context.ActiveProfilesResolver;

// https://github.com/spring-projects/spring-boot/issues/21631#issuecomment-659079864
public class TestProfileResolver implements ActiveProfilesResolver {

    private static final String PROFILE_TEST = "test";

    @Override
    public String[] resolve(Class<?> testClass) {
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, PROFILE_TEST);
        return new String[] {PROFILE_TEST};
    }
}