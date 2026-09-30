package com.qa.aem.tests;

import com.qa.aem.utils.APIResources;
import com.qa.aem.utils.ConfigReader;
import com.qa.aem.utils.SpecBuilder;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;

public class HealthCheckTest {

    @Test(groups = {"smoke", "regression"}, description = "The AEM login page loads within 5 seconds")
    public void loginPageIsReachable() {
        given()
                .baseUri(ConfigReader.get("baseUrl"))
        .when()
                .get(APIResources.LOGIN_PAGE.getResource())
        .then()
                .statusCode(200)
                .time(lessThan(5000L));
    }

    @Test(groups = {"smoke", "regression"}, description = "A valid user gets a CSRF token")
    public void csrfTokenIsIssued() {
        given()
                .spec(SpecBuilder.getRequestSpec())
        .when()
                .get(APIResources.CSRF_TOKEN.getResource())
        .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test(groups = {"smoke", "regression"}, description = "Wrong credentials are rejected with 401 Unauthorized")
    public void invalidCredentialsAreRejected() {
        given()
                .baseUri(ConfigReader.get("baseUrl"))
                .auth().preemptive().basic("invalid-user", "invalid-password")
        .when()
                .get(APIResources.CSRF_TOKEN.getResource())
        .then()
                .statusCode(401);
    }
}
