package com.example.quanlitntt_backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("classpath:application.properties")
public class EnvPropertySource {

    private final Environment environment;

    public EnvPropertySource(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        Map<String, Object> envProperties = new HashMap<>();

        dotenv.entries().forEach(entry -> envProperties.put(entry.getKey(), entry.getValue()));

        MutablePropertySources propertySources = ((StandardServletEnvironment) environment).getPropertySources();
        propertySources.addFirst(new MapPropertySource("dotenvProperties", envProperties));
    }
}

