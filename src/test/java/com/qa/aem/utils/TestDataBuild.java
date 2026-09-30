package com.qa.aem.utils;

import com.qa.aem.pojo.FolderProperties;
import com.qa.aem.pojo.FolderRequest;
import com.qa.aem.pojo.MetadataProperties;
import com.qa.aem.pojo.MetadataRequest;

/**
 * Builds the request bodies (POJOs) sent to AEM.
 */
public class TestDataBuild {

    /** {"class":"assetFolder","properties":{"title":"..."}} */
    public static FolderRequest createFolderPayload(String title) {
        FolderProperties properties = new FolderProperties();
        properties.setTitle(title);

        FolderRequest request = new FolderRequest();
        request.setEntityClass("assetFolder");
        request.setProperties(properties);
        return request;
    }

    /** {"class":"asset","properties":{"dc:title":"...","dc:description":"..."}} */
    public static MetadataRequest updateMetadataPayload(String title, String description) {
        MetadataProperties properties = new MetadataProperties();
        properties.setTitle(title);
        properties.setDescription(description);

        MetadataRequest request = new MetadataRequest();
        request.setEntityClass("asset");
        request.setProperties(properties);
        return request;
    }
}
