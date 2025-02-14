package com.example.demo;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RetriesTestApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
        .proxyMode(true)
        .options(wireMockConfig().dynamicPort())
        .build();


    @Test
    void shouldNotLeakConnections() {
        wireMockServer.stubFor(post(urlPathMatching("/endpoint"))
            .willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()))
        );

        String url = "http://localhost:" + port + "/the-route/endpoint";
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        // Max 2 connections and 3 retries
        // It seems the first two leased connections are not released back into the pool
        wireMockServer.verify(exactly(3), getRequestedFor(urlEqualTo("/endpoint")));
    }

}
