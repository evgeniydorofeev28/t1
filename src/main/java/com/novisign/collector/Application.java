package com.novisign.collector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.novisign.collector.configuration.Configuration;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableRetry
@EnableConfigurationProperties(Configuration.class)
@Import(DataSourceAutoConfiguration.class)
public class Application {  
  
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
