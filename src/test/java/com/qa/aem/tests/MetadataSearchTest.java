package com.qa.aem.tests;

import com.qa.aem.base.BaseTest;
import com.qa.aem.pojo.QueryBuilderResponse;
import com.qa.aem.utils.ConfigReader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

/**
 * Filters product assets by GS1 IMS and Image Category metadata, like the Postman DAM validation collection.
 */
public class MetadataSearchTest extends BaseTest {

    @DataProvider(name = "metadataFilters")
    public Object[][] metadataFilters() {
        return new Object[][] {
                {"lcl:ims", "GS1 Ecommerce"},
                {"lcl:ims", "GS1 Marketing"},
                {"lcl:ims", "GS1 Planogram"},
                {"lcl:ims", "GS1 Nutritional"},
                {"lcl:imageCategory", "plan"},
                {"lcl:imageCategory", "mark"},
                {"lcl:imageCategory", "nutripanel"},
                {"lcl:imageCategory", "foodingrts"},
                {"lcl:imageCategory", "ecomm"}
        };
    }

    @Test(dataProvider = "metadataFilters", groups = {"smoke", "regression"},
            description = "Filtering product assets by a metadata value returns assets that have that value")
    public void filterByMetadata(String property, String value) {
        String productFolder = ConfigReader.get("productAssetsPath");

        Response response = queryBuilderApi.searchByMetadata(productFolder, property, value);

        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/querybuilder-response.schema.json"));

        QueryBuilderResponse result = response.as(QueryBuilderResponse.class);
        Assert.assertTrue(result.isSuccess(), "The query was not successful");
        Assert.assertFalse(result.getHits().isEmpty(), "No assets found with " + property + " = " + value);

        SoftAssert softAssert = new SoftAssert();
        for (Map<String, Object> hit : result.getHits()) {
            String assetPath = (String) hit.get("jcr:path");
            softAssert.assertTrue(assetPath.startsWith(productFolder), assetPath + " is outside " + productFolder);
        }
        softAssert.assertAll();

        String firstAssetPath = (String) result.getHits().get(0).get("jcr:path");
        String actualValue = assetsApi.getMetadata(firstAssetPath)
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("'" + property + "'");
        Assert.assertNotNull(actualValue, firstAssetPath + " has no " + property + " metadata");
        Assert.assertTrue(actualValue.toLowerCase().contains(value.toLowerCase()),
                firstAssetPath + " has " + property + " = " + actualValue + ", expected " + value);
    }

    @Test(groups = "regression", description = "Filtering by a value that no asset has returns no results")
    public void filterWithUnknownValue() {
        String productFolder = ConfigReader.get("productAssetsPath");

        queryBuilderApi.searchByMetadata(productFolder, "lcl:imageCategory", "no-such-category-" + System.currentTimeMillis())
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("hits", empty());
    }
}
