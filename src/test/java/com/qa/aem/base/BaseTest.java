package com.qa.aem.base;

import com.qa.aem.api.AssetsApi;
import com.qa.aem.api.QueryBuilderApi;
import com.qa.aem.utils.ConfigReader;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Parent class of the tests: creates the API objects, gives tests their own test folder,
 * and deletes those folders after every test.
 */
public class BaseTest {

    protected AssetsApi assetsApi;
    protected QueryBuilderApi queryBuilderApi;
    private final List<String> foldersToDelete = new ArrayList<>();

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        assetsApi = new AssetsApi();
        queryBuilderApi = new QueryBuilderApi();
    }

    /** Creates a new folder with a unique name inside the sandbox folder and returns its path. */
    protected String createTestFolder() {
        String folderPath = ConfigReader.get("sandboxFolder") + "/folder-" + UUID.randomUUID().toString().substring(0, 8);
        foldersToDelete.add(folderPath);
        assetsApi.createFolder(folderPath).then().statusCode(201);
        return folderPath;
    }

    /** Uploads testdata/sample.jpg into the folder with a unique name and returns the asset's path. */
    protected String uploadTestAsset(String folderPath) {
        String assetName = "asset-" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        File image = new File("src/test/resources/testdata/sample.jpg");
        assetsApi.uploadAsset(folderPath, assetName, image).then().statusCode(201);
        return folderPath + "/" + assetName;
    }

    @AfterMethod(alwaysRun = true)
    public void deleteTestFolders() {
        for (String folderPath : foldersToDelete) {
            assetsApi.delete(folderPath);
        }
        foldersToDelete.clear();
    }
}
