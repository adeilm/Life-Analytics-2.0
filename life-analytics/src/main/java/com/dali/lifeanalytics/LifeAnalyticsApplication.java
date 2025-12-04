package com.dali.lifeanalytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Life Analytics 2.0 – Main Application Entry Point
 * ──────────────────────────────────────────────────
 * 
 * @SpringBootApplication combines three annotations:
 *   • @Configuration        – This class can define @Bean methods.
 *   • @EnableAutoConfiguration – Auto-configures beans based on classpath
 *                               (e.g., DataSource if H2/MySQL driver present).
 *   • @ComponentScan        – Scans com.dali.lifeanalytics and all subpackages
 *                               for @Component, @Service, @Repository, @Controller.
 *
 * Run this class to start the embedded Tomcat server on the configured port.
 */
@SpringBootApplication
public class LifeAnalyticsApplication {

    public static void main(String[] args) {
        // SpringApplication.run() bootstraps the application:
        //   1. Creates the ApplicationContext
        //   2. Scans and registers beans
        //   3. Starts the embedded web server
        SpringApplication.run(LifeAnalyticsApplication.class, args);
    }
}
