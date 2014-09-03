package com.huhukun.tickteeforandroid.model;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.huhukun.tickteeforandroid.App_Constants;
import com.huhukun.utils.BooleanUtils;
import com.huhukun.utils.FormatHelper;
import com.huhukun.utils.NumberUtils;

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
    public static final int TOTAL_PROJECTS = 1;
    public static final int IN_PROGRESS_PROJECTS = 2;
    public static final int OVERDUE_PROJECTS = 3;
    public static final int COMPLETE_PROJECTS = 4;

    public void setSyncMode(SyncMode syncMode) {
        this.syncMode = syncMode;
    }

    public enum SyncMode {
        I,
        U,
        D,
        NA

    }

    public enum AlertType {
        OFF,
        EVERY_DAY,
        EVERY_MONDAY,
        EVERY_TUESDAY,
        EVERY_WEDNESDAY,
        EVERY_THURSDAY,
        EVERY_FRIDAY,
        EVERY_SATURDAY,
        EVERY_SUNDAY;

        @Override
        public String toString() {
            return super.toString();
        }


        public static AlertType parse(String string){
            if (string.equals("OFF")) {
                return OFF;
            }
            if (string.equals("EVERY DAY") || string.equals("EVERY_DAY")) {
                return EVERY_DAY;
            }
            if (string.equals("EVERY MONDAY") || string.equals("EVERY_MONDAY")) {
                return EVERY_MONDAY;
            }
            if (string.equals("EVERY TUESDAY") || string.equals("EVERY_TUESDAY")) {
                return EVERY_TUESDAY;
            }
            if (string.equals("EVERY WEDNESDAY") || string.equals("EVERY_WEDNESDAY")) {
                return EVERY_WEDNESDAY;
            }
            if (string.equals("EVERY THURSDAY") || string.equals("EVERY_THURSDAY")) {
                return EVERY_THURSDAY;
            }
            if (string.equals("EVERY FRIDAY") || string.equals("EVERY_FRIDAY")) {
                return EVERY_FRIDAY;
            }
            if (string.equals("EVERY SATURDAY") || string.equals("EVERY_SATURDAY")){
                return EVERY_SATURDAY;
            }
            if (string.equals("EVERY SUNDAY") || string.equals("EVERY_SUNDAY")) {
                return EVERY_SUNDAY;
            }
            throw new IllegalArgumentException("No parse for string "+string);
        }
    }

    private long _id;
    private long projectId;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private BigDecimal expectedProgress = BigDecimal.ZERO;
    private BigDecimal currentProgress = BigDecimal.ZERO;
    private BigDecimal initProgress = BigDecimal.ZERO;
    private BigDecimal target = BigDecimal.ZERO;
    private boolean isDecimalUnit;
    private String unit;
    private AlertType alertType = AlertType.OFF;
    private Date createdTime;
    private Date lastUpdateTime;
    private boolean isConsumed;

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

    public BigDecimal getInitProgress() {
        return initProgress;
    }

    public void setInitProgress(BigDecimal initProgress) {
        this.initProgress = initProgress;
    }

    public boolean isConsumed() {
        return isConsumed;
    }

    public void setConsumed(boolean isConsumed) {
        this.isConsumed = isConsumed;
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

    private Date transDate;
    private long requestId;
    private int httpResult;
    private SyncMode syncMode;


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
        this.name = json.getString(PARAM_NAME);
        this.description = json.getString(PARAM_DESCRIPTION);
        if (json.has(PARAM_START_AT) && json.has(PARAM_END_AT)
                && !json.getString(PARAM_START_AT).equals("null")
                && !json.getString(PARAM_END_AT).equals("null")) {
            this.startDate = FormatHelper.toLocalDateFromUTCString(json.getString(PARAM_START_AT));
            this.endDate = FormatHelper.toLocalDateFromUTCString(json.getString(PARAM_END_AT));
        }
        this.expectedProgress = json.has(PARAM_EXPECTED_PROGRESS)? new BigDecimal(json.getString(PARAM_EXPECTED_PROGRESS)): BigDecimal.ZERO;
        this.currentProgress = json.has(PARAM_CURRENT_PROGRESS)? new BigDecimal(json.getString(PARAM_CURRENT_PROGRESS)) : BigDecimal.ZERO;
        this.createdTime = json.has(PARAM_CREATED_AT)? FormatHelper.toLocalDateFromUTCString(json.getString(PARAM_CREATED_AT)):null;
        this.lastUpdateTime = json.has(PARAM_UPDATED_AT)?FormatHelper.toLocalDateFromUTCString(json.getString(PARAM_UPDATED_AT)) : null;
        this.target = json.has(PARAM_TARGET)? new BigDecimal(json.getString(PARAM_TARGET)) : BigDecimal.ONE;
        this.unit = json.has(PARAM_UNIT)? json.getString(PARAM_UNIT): "";
        this.alertType = json.has(PARAM_ALERT_TYPE)? AlertType.parse(json.getString(PARAM_ALERT_TYPE)) : AlertType.OFF;
        this.isDecimalUnit = json.has(PARAM_IS_DECIMAL_UNIT)? json.getBoolean(PARAM_IS_DECIMAL_UNIT):false;
        this.initProgress = json.has(PARAM_INIT_PROGRESS)? new BigDecimal(json.getString(PARAM_INIT_PROGRESS)) : BigDecimal.ZERO;
        this.isConsumed = json.has(PARAM_IS_CONSUMED)? json.getBoolean(PARAM_IS_CONSUMED) : false;
        this.syncMode = json.has(PARAM_SYNC_MODE)? SyncMode.valueOf(json.getString(PARAM_SYNC_MODE)) : SyncMode.NA;
    }

    public Project(Cursor cursor) throws ParseException {
        this._id = cursor.getLong(cursor.getColumnIndex(_ID));
        this.projectId = cursor.getLong(cursor.getColumnIndex(COL_PROJECT_ID));
        this.name = cursor.getString(cursor.getColumnIndex(COL_NAME));
        this.description = cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION));
        if (!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(COL_START_AT)) )
                && !TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(COL_END_AT)))) {
            this.startDate = FormatHelper.toLocalDateFromUTCString(cursor.getString(cursor.getColumnIndex(COL_START_AT)));
            this.endDate = FormatHelper.toLocalDateFromUTCString(cursor.getString(cursor.getColumnIndex(COL_END_AT)));
        }
        this.expectedProgress = new BigDecimal(cursor.getString(cursor.getColumnIndex(COL_EXPECTED_PROGRESS)));
        this.currentProgress = new BigDecimal(cursor.getString(cursor.getColumnIndex(COL_CURRENT_PROGRESS)));
        this.createdTime = FormatHelper.toLocalDateFromUTCString(cursor.getString(cursor.getColumnIndex(COL_CREATED_AT)));
        this.lastUpdateTime = FormatHelper.toLocalDateFromUTCString(cursor.getString(cursor.getColumnIndex(COL_UPDATED_AT)));
        this.target = new BigDecimal(cursor.getString(cursor.getColumnIndex(COL_TARGET)));
        this.unit = cursor.getString(cursor.getColumnIndex(COL_UNIT));
        this.alertType = AlertType.parse(cursor.getString(cursor.getColumnIndex(COL_ALERT_TYPE)));
        this.isDecimalUnit = BooleanUtils.parse(cursor.getString(cursor.getColumnIndex(COL_IS_DECIMAL)));
        this.initProgress = new BigDecimal(cursor.getString(cursor.getColumnIndex(COL_INIT_PROGRESS)));
        this.isConsumed = BooleanUtils.parse(cursor.getString(cursor.getColumnIndex(COL_IS_CONSUMED)));
    }

    public JSONObject toJson() {

        JSONObject json = new JSONObject();
        try{
        json.put(PARAM_PROJECTS_ID, this.getProjectId());
        json.put(PARAM_NAME, this.getName());
        json.put(PARAM_DESCRIPTION, this.getDescription());
        if (this.getStartDate() != null) {
            json.put(PARAM_START_AT, FormatHelper.toUTCString(this.getStartDate()));
        }
        else{
            json.put(PARAM_START_AT, "");
        }
        if (this.getEndDate() != null) {
            json.put(PARAM_END_AT, FormatHelper.toUTCString(this.getEndDate()));
        }
        else {
            json.put(PARAM_END_AT, "");
        }
        json.put(PARAM_EXPECTED_PROGRESS, this.getExpectedProgress().toString());
        json.put(PARAM_CURRENT_PROGRESS, this.getCurrentProgress().toString());
        json.put(PARAM_CREATED_AT, FormatHelper.toUTCString(this.getCreatedTime()));
        json.put(PARAM_UPDATED_AT, FormatHelper.toUTCString(this.getLastUpdateTime()));
        json.put(PARAM_TARGET, this.getTarget().toString());
        json.put(PARAM_UNIT, this.getUnit());
        json.put(PARAM_ALERT_TYPE, this.getAlertType().toString());
        json.put(PARAM_IS_DECIMAL_UNIT, this.isDecimalUnit);
        json.put(PARAM_INIT_PROGRESS, this.getInitProgress().toString());
        json.put(PARAM_IS_CONSUMED, this.isConsumed);
        } catch (JSONException e) {
            Log.e(TAG, "unable to make json");
        }
        return json;
    }

    public int getExpectedPercentage()
    {
        if(this.getStartDate() == null || this.getEndDate() == null) return 0;
        Date now = new Date();
//        if (isConsumed()){
//            return 100 - NumberUtils.getPercentage(this.getStartDate(), this.getEndDate(), now);
//        }
        return NumberUtils.getPercentage(this.getStartDate(), this.getEndDate(), now);

    }


}
