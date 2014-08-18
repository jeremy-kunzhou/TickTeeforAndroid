package com.huhukun.tickteeforandroid.model;

import org.json.JSONException;

import java.text.ParseException;
import java.util.List;

/**
 * Created by kun on 18/08/2014.
 */
public interface IProjectsManagement {

    // sync Projects
    void SyncProjects() throws JSONException, ParseException;

    // View function
    List<Project> getAllProjects();
    List<Project> getProjectsOnProgress();
    List<Project> getProjectsOverdue();
    List<Project> getProjectsComplete();
    Project getProjectById(long id);

    // Update function
    void updateProject(Project project);

    // Delete function
    void deleteProject(Project project);

    // Summary function
    int[] getSummary();
}
