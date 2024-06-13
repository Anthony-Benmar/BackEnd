package com.bbva.util;

public class ApiJiraName {
    public static final String URL_API_BASE = "https://jira.globaldevtools.bbva.com";
    public static final String URL_API_JIRA_SESSION = URL_API_BASE + "/rest/auth/1/session";
    private static final String URL_API_JIRA = URL_API_BASE + "/rest/api/2/issue/";
    public static final String URL_API_JIRA_SQL = URL_API_BASE + "/rest/api/2/search?jql=";
    private static final String HEADER_COOKIE_JIRA = "_oauth2_proxy=";
    public static final String URL_API_BROWSE = URL_API_BASE + "/browse/";

}
