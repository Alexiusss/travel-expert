package com.example.user.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PreAuthorize("hasAnyAuthority('ADMIN', 'MODER') || hasAnyRole('ADMIN', 'MODER')")
public @interface AdminOrModerRoleAccess {
}