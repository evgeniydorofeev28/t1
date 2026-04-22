package com.novisign.collector.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Retryable(
    retryFor = TransientDataAccessException.class, 
    maxAttempts = 3, 
    backoff = @Backoff(delay = 1000, multiplier = 2, random = true)
  )
public @interface DatabaseRetry {
}
