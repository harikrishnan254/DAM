package com.qa.aem.api;

import com.qa.aem.utils.APIResources;
import com.qa.aem.utils.SpecBuilder;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Calls to QueryBuilder (/bin/querybuilder.json), AEM's search API.
 */
public class QueryBuilderApi {

    /**
     * Finds assets under searchPath whose metadata property has the given value, the same query as the
     * Postman "Filter by GS1 IMS" and "Filter by Image Category" requests.
     */
    public Response searchByMetadata(String searchPath, String property, String value) {
        return given()
                .spec(SpecBuilder.getRequestSpec())
                .queryParam("path", searchPath)
                .queryParam("type", "dam:Asset")
                .queryParam("property", "jcr:content/metadata/" + property)
                .queryParam("property.value", value)
                .queryParam("p.limit", 100)
                .queryParam("p.hits", "selective")
                .queryParam("p.properties", "jcr:path")
                .when()
                .get(APIResources.QUERY_BUILDER.getResource());
    }
}
