package com.qa.aem.utils;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import static io.restassured.RestAssured.given;

/**
 * Builds the request specifications that every API call starts from.
 */
public class SpecBuilder {

    private static RequestSpecification requestSpec;
    private static PrintStream logFile;

    /**
     * Base URL, Basic auth, timeouts, and logging of every request and response to target/api-log.txt.
     * Built once and reused by all tests.
     */
    public static synchronized RequestSpecification getRequestSpec() {
        if (requestSpec == null) {
            requestSpec = new RequestSpecBuilder()
                    .setBaseUri(ConfigReader.get("baseUrl"))
                    .setAuth(RestAssured.preemptive().basic(ConfigReader.get("AEM_USERNAME"), ConfigReader.get("AEM_PASSWORD")))
                    .setConfig(RestAssuredConfig.config()
                            .httpClient(HttpClientConfig.httpClientConfig()
                                    .setParam("http.connection.timeout", 10000)
                                    .setParam("http.socket.timeout", 60000))
                            .logConfig(LogConfig.logConfig().blacklistHeader("Authorization", "CSRF-Token")))
                    .addFilter(RequestLoggingFilter.logRequestTo(getLogFile()))
                    .addFilter(ResponseLoggingFilter.logResponseTo(getLogFile()))
                    .build();
        }
        return requestSpec;
    }

    /**
     * Spec for requests that change content (POST, PUT, DELETE, COPY, MOVE). AEM rejects these without
     * a CSRF token, so a fresh token is fetched and sent in the CSRF-Token header.
     */
    public static RequestSpecification getWriteSpec() {
        String token = given()
                .spec(getRequestSpec())
                .when()
                .get(APIResources.CSRF_TOKEN.getResource())
                .then()
                .statusCode(200)
                .extract().path("token");

        return new RequestSpecBuilder()
                .addRequestSpecification(getRequestSpec())
                .addHeader("CSRF-Token", token)
                .build();
    }

    private static PrintStream getLogFile() {
        if (logFile == null) {
            try {
                logFile = new PrintStream(new FileOutputStream("target/api-log.txt"));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Could not create target/api-log.txt", e);
            }
        }
        return logFile;
    }
}
