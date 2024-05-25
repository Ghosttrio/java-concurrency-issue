package com.ghosttrio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.locks.DefaultLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

@Configuration
public class LockConfig {

    @Bean
    public LockRegistry lockRegistry() {
        return new DefaultLockRegistry();
    }
}