package com.example.demo;

import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpComponentsClientHttpRequestFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class HttpComponentsConfig {

    @Bean
    public HttpComponentsClientHttpRequestFactoryBuilder httpComponentsClientHttpRequestFactoryBuilder() {
        return ClientHttpRequestFactoryBuilder.httpComponents()
            .withConnectionManagerCustomizer(builder ->
                builder.setMaxConnTotal(2)
                    .setMaxConnPerRoute(2)
            ).withDefaultRequestConfigCustomizer(c -> c.setConnectionRequestTimeout(Timeout.of(Duration.ofMillis(3000))));
    }

}
