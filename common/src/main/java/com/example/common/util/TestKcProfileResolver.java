package com.example.common.util;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.test.context.ActiveProfilesResolver;

public class TestKcProfileResolver implements ActiveProfilesResolver {

    private static final String PROFILE_KC = "test_kc";

    @Override
    public String[] resolve(Class<?> testClass) {
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, PROFILE_KC);
        return new String[] {PROFILE_KC};
    }
}