package com.qa.aem.utils;

/**
 * AEM endpoints used by the framework. Each path is added to the base URL.
 */
public enum APIResources {

    LOGIN_PAGE("/libs/granite/core/content/login.html"),
    CSRF_TOKEN("/libs/granite/csrf/token.json"),
    ASSETS_API("/api/assets"),
    QUERY_BUILDER("/bin/querybuilder.json"),
    DAM_ROOT("/content/dam");

    private final String resource;

    APIResources(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }
}
