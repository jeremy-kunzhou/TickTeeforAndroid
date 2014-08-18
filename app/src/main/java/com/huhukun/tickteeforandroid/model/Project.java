package com.huhukun.tickteeforandroid.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kun on 18/08/2014.
 */
public class Project {

    public static final DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public enum AlertType{
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

    private long id;
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
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Project (JSONObject json) throws JSONException, ParseException {
        this.id = json.getLong("id");
        this.name = json.getString("name");
        this.description = json.getString("description");
        this.startDate = dateFormat.parse(json.getString("start_at"));
        this.endDate = dateFormat.parse(json.getString("end_at"));
        this.expectedProgress = new BigDecimal(json.getString("expected_progress"));
        this.currentProgress = new BigDecimal(json.getString("current_progress"));
        this.createdTime = utcFormat.parse(json.getString("created_at"));
        this.lastUpdateTime = utcFormat.parse(json.getString("updated_at"));
    }
}
