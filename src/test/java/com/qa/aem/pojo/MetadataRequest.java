package com.qa.aem.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for updating asset metadata: {"class":"asset","properties":{"dc:title":"...","dc:description":"..."}}
 */
public class MetadataRequest {

    @JsonProperty("class")
    private String entityClass;
    private MetadataProperties properties;

    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    public MetadataProperties getProperties() {
        return properties;
    }

    public void setProperties(MetadataProperties properties) {
        this.properties = properties;
    }
}
