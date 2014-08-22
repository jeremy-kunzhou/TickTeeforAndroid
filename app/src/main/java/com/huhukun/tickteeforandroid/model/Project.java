package com.huhukun.tickteeforandroid.model;

import android.database.Cursor;
import android.util.Log;

import com.huhukun.tickteeforandroid.App_Constants;
import com.huhukun.utils.FormatHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants.*;
import static com.huhukun.tickteeforandroid.providers.WebApiConstants.*;
/**
 * Created by kun on 18/08/2014.
 */
public class Project {
    private static final String TAG = App_Constants.APP_TAG +"Project";
    public void setSyncMode(SyncMode syncMode) {
        this.syncMode = syncMode;
    }

    public enum SyncMode {
        I,
        U,
        D

    }

    public enum AlertType {
        OFF,
        PER_DAY,
        EVERY_MONDAY,
        EVERY_TUESDAY,
        EVERY_WEDNESDAY,
        EVERY_THURSDAY,
        EVERY_FRIDAY,
        EVERY_SATURDAY,
        EVERY_SUNDAY
    }

    private long _id;
    private long projectId;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private BigDecimal expectedProgress;
    private BigDecimal currentProgress;
    private BigDecimal target;
    private BigDecimal start;
    private boolean isDecimalUnit;
    private String unit;
    private AlertType alertType;
    private Date createdTime;
    private Date lastUpdateTime;

    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getExpectedProgress() {
        return expectedProgress;
    }

    public void setExpectedProgress(BigDecimal expectedProgress) {
        this.expectedProgress = expectedProgress;
    }

    public BigDecimal getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(BigDecimal currentProgress) {
        this.currentProgress = currentProgress;
    }

    public BigDecimal getTarget() {
        return target;
    }

    public void setTarget(BigDecimal target) {
        this.target = target;
    }

    public BigDecimal getStart() {
        return start;
    }

    public void setStart(BigDecimal start) {
        this.start = start;
    }

    public boolean isDecimalUnit() {
        return isDecimalUnit;
    }

    public void setDecimalUnit(boolean isDecimalUnit) {
        this.isDecimalUnit = isDecimalUnit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

//    private long projectsId;
    private Date transDate;
    private long requestId;
    private int httpResult;
    private SyncMode syncMode;

//    public long getProjectsId() {
//        return projectsId;
//    }
//
//    public void setProjectsId(long projectsId) {
//        this.projectsId = projectsId;
//    }

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public int getHttpResult() {
        return httpResult;
    }

    public void setHttpResult(int httpResult) {
        this.httpResult = httpResult;
    }

    public SyncMode getSyncMode() {
        return syncMode;
    }

    public Project() {}

    public Project(JSONObject json) throws JSONException, ParseException {

        this.projectId = json.getLong(PARAM_PROJECTS_ID);
        Log.d(TAG, "Make json "+ projectId);
        this.name = json.getString(PARAM_NAME);
        this.description = json.getString(PARAM_DESCRIPTION);
        this.startDate = FormatHelper.serverDateFormatter.parse(json.getString(PARAM_START_AT));
        this.endDate = FormatHelper.serverDateFormatter.parse(json.getString(PARAM_END_AT));
        this.expectedProgress = new BigDecimal(json.getString(PARAM_EXPECTED_PROGRESS));
        this.currentProgress = new BigDecimal(json.getString(PARAM_CURRENT_PROGRESS));
        this.createdTime = FormatHelper.serverDateTimeFormatter.parse(json.getString(PARAM_CREATED_AT));
        this.lastUpdateTime = FormatHelper.serverDateTimeFormatter.parse(json.getString(PARAM_UPDATED_AT));
    }

    public Project(Cursor cursor) throws ParseException {
        this._id = cursor.getLong(cursor.getColumnIndex(_ID));
        this.projectId = cursor.getLong(cursor.getColumnIndex(COL_PROJECT_ID));
        this.name = cursor.getString(cursor.getColumnIndex(COL_NAME));
        this.description = cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION));
        this.startDate = FormatHelper.serverDateFormatter.parse(cursor.getString(cursor.getColumnIndex(COL_START_AT)));
        this.endDate = FormatHelper.serverDateFormatter.parse(cursor.getString(cursor.getColumnIndex(COL_END_AT)));
        this.expectedProgress = new BigDecimal(cursor.getString(cursor.getColumnIndex(COL_EXPECTED_PROGRESS)));
        this.currentProgress = new BigDecimal(cursor.getString(cursor.getColumnIndex(COL_CURRENT_PROGRESS)));
        this.createdTime = FormatHelper.serverDateTimeFormatter.parse(cursor.getString(cursor.getColumnIndex(COL_CREATED_AT)));
        this.lastUpdateTime = FormatHelper.serverDateTimeFormatter.parse(cursor.getString(cursor.getColumnIndex(COL_UPDATED_AT)));
    }

    public JSONObject toJson() throws JSONException {

        JSONObject json = new JSONObject();
        json.put(PARAM_PROJECTS_ID, this.getProjectId());
        json.put(PARAM_NAME, this.getName());
        json.put(PARAM_DESCRIPTION, this.getDescription());
        json.put(PARAM_START_AT, FormatHelper.serverDateFormatter.format(this.getStartDate()));
        json.put(PARAM_END_AT, FormatHelper.serverDateFormatter.format(this.getEndDate()));
        json.put(PARAM_EXPECTED_PROGRESS, this.getExpectedProgress().toString());
        json.put(PARAM_CURRENT_PROGRESS, this.getCurrentProgress().toString());
        json.put(PARAM_CREATED_AT, FormatHelper.serverDateTimeFormatter.format(this.getCreatedTime()));
        json.put(PARAM_UPDATED_AT, FormatHelper.serverDateTimeFormatter.format(this.getLastUpdateTime()));
        return json;
    }


}
