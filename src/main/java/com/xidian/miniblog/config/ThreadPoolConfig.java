package com.xidian.miniblog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author qhhu
 * @date 2020/3/31 - 9:48
 */
@Configuration
@EnableAsync
@EnableScheduling
public class ThreadPoolConfig {}
