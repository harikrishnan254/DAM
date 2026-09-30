package com.qa.aem.tests;

import com.qa.aem.base.BaseTest;
import com.qa.aem.utils.APIResources;
import com.qa.aem.utils.ConfigReader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;

/**
 * Create, read, update, copy, move and delete folders and assets through the Assets HTTP API.
 * Every test works in its own new folder, which BaseTest deletes afterwards.
 */
public class AssetCrudTest extends BaseTest {

    @BeforeClass(alwaysRun = true)
    public void skipOnProd() {
        if (ConfigReader.getEnvironment().equals("prod")) {
            throw new SkipException("Tests that create or delete content never run on prod");
        }
    }

    @Test(groups = "regression", description = "Creating a folder returns 201 and the folder can be read")
    public void createFolder() {
        String folderPath = createTestFolder();

        Response response = assetsApi.getFolder(folderPath);

        response.then().statusCode(200);
        String folderName = folderPath.substring(folderPath.lastIndexOf('/') + 1);
        Assert.assertEquals(response.jsonPath().getString("properties.name"), folderName);
    }

    @Test(groups = "regression", description = "Creating a folder that already exists returns 409 Conflict")
    public void createDuplicateFolder() {
        String folderPath = createTestFolder();

        assetsApi.createFolder(folderPath)
                .then()
                .statusCode(409);
    }

    @Test(groups = "regression", description = "Uploading an image returns 201 and the asset can be read")
    public void uploadAsset() {
        String assetPath = uploadTestAsset(createTestFolder());

        assetsApi.getAsset(assetPath)
                .then()
                .statusCode(200);
    }

    @Test(groups = "regression", description = "Updating metadata returns 200 and the new values are saved on the asset")
    public void updateMetadata() {
        String assetPath = uploadTestAsset(createTestFolder());

        assetsApi.updateMetadata(assetPath, "Automation title", "Updated by API automation")
                .then()
                .statusCode(200);

        assetsApi.getMetadata(APIResources.DAM_ROOT.getResource() + "/" + assetPath)
                .then()
                .statusCode(200)
                .body("'dc:title'", equalTo("Automation title"))
                .body("'dc:description'", equalTo("Updated by API automation"));
    }

    @Test(groups = "regression", description = "Copying an asset returns 201 and both the original and the copy exist")
    public void copyAsset() {
        String folderPath = createTestFolder();
        String assetPath = uploadTestAsset(folderPath);
        String copyPath = folderPath + "/copy.jpg";

        assetsApi.copyAsset(assetPath, copyPath)
                .then()
                .statusCode(201);

        assetsApi.getAsset(copyPath).then().statusCode(200);
        assetsApi.getAsset(assetPath).then().statusCode(200);
    }

    @Test(groups = "regression", description = "Moving an asset returns 201 and the asset exists only at the new path")
    public void moveAsset() {
        String folderPath = createTestFolder();
        String assetPath = uploadTestAsset(folderPath);
        String newPath = folderPath + "/moved.jpg";

        assetsApi.moveAsset(assetPath, newPath)
                .then()
                .statusCode(201);

        assetsApi.getAsset(newPath).then().statusCode(200);
        assetsApi.getAsset(assetPath).then().statusCode(404);
    }

    @Test(groups = "regression", description = "Deleting an asset returns 200 and the asset can no longer be found")
    public void deleteAsset() {
        String assetPath = uploadTestAsset(createTestFolder());

        assetsApi.delete(assetPath)
                .then()
                .statusCode(200);

        assetsApi.getAsset(assetPath).then().statusCode(404);
    }

    @Test(groups = "regression", description = "AEM creates the web rendition of an uploaded image")
    public void webRenditionIsCreated() throws InterruptedException {
        String assetPath = uploadTestAsset(createTestFolder());

        assetsApi.waitForRendition(assetPath, "cq5dam.web.1280.1280.jpeg")
                .then()
                .statusCode(200);
    }
}
