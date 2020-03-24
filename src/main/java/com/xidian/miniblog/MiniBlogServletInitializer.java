package com.xidian.miniblog;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author qhhu
 * @date 2020/3/20 - 12:58
 */
public class MiniBlogServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MiniBlogApplication.class);
    }
}
