package com.huhukun.tickteeforandroid.network;

/**
 * Created by kun on 19/08/2014.
 */
public class WebApiConstants {
    public static final String PARAM_PROJECTS_ID = "id";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_START_AT = "start_at";
    public static final String PARAM_END_AT = "end_at";
    public static final String PARAM_EXPECTED_PROGRESS = "expected_progress";
    public static final String PARAM_CURRENT_PROGRESS = "current_progress";
    public static final String PARAM_CREATED_AT = "created_at";
    public static final String PARAM_UPDATED_AT = "updated_at";
    public static final String PARAM_DESCRIPTION = "description";
    public static final String PARAM_DATE_UPDATED = "trans_date";
    public static final String PARAM_DOWNLOAD_DATE = "downloadDate";
    public static final String PARAM_NEXT_DOWNLOAD_DATE = "";
    public static final String PARAM_SYNC_MODE = "";
    public static final String PARAM_SONGS_LIST = "";
    public static final String LOGIN_URL ;
    public static final String PROJECTS_URL;
    public static final String PROJECT_URL;
    public static final String BASE_URL = "http://192.168.2.3:3000/";

    public static final String HEADER_ACCESS_EMAIL_PARAM = "X_API_EMAIL";
    public static final String HEADER_ACCESS_TOKEN_PARM = "X_API_TOKEN";
    static {
        LOGIN_URL = BASE_URL + "users/sign_in.json";
        PROJECTS_URL = BASE_URL + "api/v1/projects.json";
        PROJECT_URL = BASE_URL + "api/v1/projects/%d.json";
    }
}
