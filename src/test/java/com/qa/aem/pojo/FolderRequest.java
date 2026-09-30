package com.qa.aem.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for creating a DAM folder: {"class":"assetFolder","properties":{"title":"..."}}
 */
public class FolderRequest {

    @JsonProperty("class")
    private String entityClass;
    private FolderProperties properties;

    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    public FolderProperties getProperties() {
        return properties;
    }

    public void setProperties(FolderProperties properties) {
        this.properties = properties;
    }
}
