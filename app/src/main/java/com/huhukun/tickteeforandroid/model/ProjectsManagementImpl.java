package com.huhukun.tickteeforandroid.model;

import android.content.SharedPreferences;
import android.util.Log;

import com.huhukun.tickteeforandroid.LoginActivity;
import com.huhukun.tickteeforandroid.MainActivity;
import com.huhukun.utils.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kun on 18/08/2014.
 */
public class ProjectsManagementImpl implements IProjectsManagement {


    private static final String projectListUrl = "http://192.168.2.3:3000/api/v1/projects.json";

    private static IProjectsManagement projectsManagement = new ProjectsManagementImpl();

    public static IProjectsManagement getInstance() {
        return projectsManagement;
    }

    private List<Project> projects = new ArrayList<Project>();

    private ProjectsManagementImpl() {
    }

    private static boolean isSync = false;

    @Override
    public void SyncProjects() throws JSONException, ParseException {
        if (isSync) return;
        projects.clear();
        String email = MainActivity.appSetting.getString(LoginActivity.PREF_EMAIL, null);
        String token = MainActivity.appSetting.getString(LoginActivity.PREF_TOKEN, null);

        List<NameValuePair> headers = new ArrayList<NameValuePair>(2);
        headers.add(new BasicNameValuePair("X_API_EMAIL", email));
        headers.add(new BasicNameValuePair("X_API_TOKEN", token));


        JSONArray projectsJson = new JSONArray(JSONParser.getStringFromUrlViaGet(projectListUrl, headers, null));
        for (int i = 0; i < projectsJson.length(); i++) {

            projects.add(new Project(projectsJson.getJSONObject(i)));

        }
        isSync = true;
    }

    @Override
    public List<Project> getAllProjects() {
        return projects;
    }

    @Override
    public List<Project> getProjectsOnProgress() {
        return null;
    }

    @Override
    public List<Project> getProjectsOverdue() {
        return null;
    }

    @Override
    public List<Project> getProjectsComplete() {
        return null;
    }

    @Override
    public Project getProjectById(long id) {
        Project result = null;
        for (Project p : projects) {
            if (p.getId() == id) {
                result = p;
                break;
            }
        }
        return result;
    }

    @Override
    public void updateProject(Project project) {

    }

    @Override
    public void deleteProject(Project project) {

    }

    @Override
    public int[] getSummary() {
        return new int[0];
    }
}
