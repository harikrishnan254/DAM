package com.qa.aem.api;

import com.qa.aem.utils.APIResources;
import com.qa.aem.utils.SpecBuilder;
import com.qa.aem.utils.TestDataBuild;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.File;

import static io.restassured.RestAssured.given;

/**
 * Calls to the AEM Assets HTTP API (/api/assets). Folder and asset paths are relative to /content/dam.
 */
public class AssetsApi {

    private static final String ASSETS_API = APIResources.ASSETS_API.getResource();

    public Response createFolder(String folderPath) {
        return given()
                .spec(SpecBuilder.getWriteSpec())
                .contentType(ContentType.JSON)
                .body(TestDataBuild.createFolderPayload("Automation test folder"))
                .when()
                .post(ASSETS_API + "/" + folderPath);
    }

    public Response getFolder(String folderPath) {
        return given()
                .spec(SpecBuilder.getRequestSpec())
                .when()
                .get(ASSETS_API + "/" + folderPath + ".json");
    }

    public Response uploadAsset(String folderPath, String assetName, File file) {
        return given()
                .spec(SpecBuilder.getWriteSpec())
                .multiPart("file", file, "image/jpeg")
                .when()
                .post(ASSETS_API + "/" + folderPath + "/" + assetName);
    }

    public Response getAsset(String assetPath) {
        return given()
                .spec(SpecBuilder.getRequestSpec())
                .when()
                .get(ASSETS_API + "/" + assetPath + ".json");
    }

    public Response updateMetadata(String assetPath, String title, String description) {
        return given()
                .spec(SpecBuilder.getWriteSpec())
                .contentType(ContentType.JSON)
                .body(TestDataBuild.updateMetadataPayload(title, description))
                .when()
                .put(ASSETS_API + "/" + assetPath);
    }

    /** Reads the metadata node of an asset, for example /content/dam/folder/image.jpg/jcr:content/metadata.json */
    public Response getMetadata(String assetJcrPath) {
        return given()
                .spec(SpecBuilder.getRequestSpec())
                .when()
                .get(assetJcrPath + "/jcr:content/metadata.json");
    }

    public Response copyAsset(String assetPath, String newAssetPath) {
        return given()
                .spec(SpecBuilder.getWriteSpec())
                .header("X-Destination", ASSETS_API + "/" + newAssetPath)
                .header("X-Depth", "infinity")
                .header("X-Overwrite", "F")
                .when()
                .request("COPY", ASSETS_API + "/" + assetPath);
    }

    public Response moveAsset(String assetPath, String newAssetPath) {
        return given()
                .spec(SpecBuilder.getWriteSpec())
                .header("X-Destination", ASSETS_API + "/" + newAssetPath)
                .header("X-Depth", "infinity")
                .header("X-Overwrite", "F")
                .when()
                .request("MOVE", ASSETS_API + "/" + assetPath);
    }

    /** Deletes an asset, or a folder with everything in it. */
    public Response delete(String path) {
        return given()
                .spec(SpecBuilder.getWriteSpec())
                .when()
                .delete(ASSETS_API + "/" + path);
    }

    public Response getRendition(String assetPath, String renditionName) {
        return given()
                .spec(SpecBuilder.getRequestSpec())
                .when()
                .get(ASSETS_API + "/" + assetPath + "/renditions/" + renditionName);
    }

    /**
     * AEM creates renditions in the background after an upload, so this checks every 2 seconds,
     * for up to 2 minutes, until the rendition exists.
     */
    public Response waitForRendition(String assetPath, String renditionName) throws InterruptedException {
        for (int attempt = 1; attempt <= 60; attempt++) {
            Response response = getRendition(assetPath, renditionName);
            if (response.statusCode() == 200) {
                return response;
            }
            Thread.sleep(2000);
        }
        throw new AssertionError("Rendition " + renditionName + " was not created within 2 minutes");
    }
}
