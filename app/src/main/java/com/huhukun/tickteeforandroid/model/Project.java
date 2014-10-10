package com.huhukun.tickteeforandroid.model;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.huhukun.tickteeforandroid.App_Constants;
import com.huhukun.utils.BooleanUtils;
import com.huhukun.utils.FormatHelper;
import com.huhukun.utils.MyDateUtils;
import com.huhukun.utils.NumberUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private int schedule;
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

    public int getSchedule() {
        return schedule;
    }

    public void setSchedule(int schedule) {
        this.schedule = schedule;
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
        this.schedule = json.has(PARAM_SCHEDULE)? json.getInt(PARAM_SCHEDULE) : 127;
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
        this.schedule = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COL_SCHEDULE)));
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
            json.put(PARAM_SCHEDULE, this.schedule);
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

    public long getRestDay(){
        if (schedule > 0 && schedule < 127)
            return getRestDayOnSchedule();
        long diff = 1;
        if (this.getEndDate() == null ) {
            diff = 1;
        }
        else{
            Date currentDate = new Date();
            diff = NumberUtils.diffOfDate(this.getEndDate(), currentDate);
        }
        return diff == 0? 1 : diff;
    }

    public long getRestDayOnSchedule(){
        long diff = 1;
        if (this.getEndDate() == null ) {
            diff = 1;
        }
        else{
            Date currentDate = new Date();
            diff = NumberUtils.diffOfDate(this.getEndDate(), currentDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            diff = MyDateUtils.getNumberOfDayOfWeekInDays(diff, cal.get(Calendar.DAY_OF_WEEK),parseScheduleDayOfWeekArrayFromValue(schedule));
        }
        return diff == 0? 1 : diff;
    }

    public long getLastUpdatePastDay(){
        if (schedule > 0 && schedule < 127)
            return getLastUpdatePastDayOnSchedule();
        long diff = 1;
        if (this.getStartDate() == null ) {
            diff = NumberUtils.diffOfDate(this.getCreatedTime(), this.getLastUpdateTime());
        }else{
            diff = NumberUtils.diffOfDate(this.getStartDate(), this.getLastUpdateTime());
        }
        return diff == 0? 1 : diff;
    }

    public long getLastUpdatePastDayOnSchedule(){
        long diff = 1;
        Calendar cal = Calendar.getInstance();
        if (this.getStartDate() == null ) {
            diff = NumberUtils.diffOfDate(this.getCreatedTime(), this.getLastUpdateTime());
            cal.setTime(this.getCreatedTime());
        }else{
            diff = NumberUtils.diffOfDate(this.getStartDate(), this.getLastUpdateTime());
            cal.setTime(this.getStartDate());
        }
        diff = MyDateUtils.getNumberOfDayOfWeekInDays(diff, cal.get(Calendar.DAY_OF_WEEK),parseScheduleDayOfWeekArrayFromValue(schedule));
        return diff == 0? 1 : diff;
    }

    public long getPastDay(){
        long diff = 1;
        if (this.getStartDate() == null ) {
            diff = NumberUtils.diffOfDate(this.getCreatedTime(), new Date());
        }else{
            diff = NumberUtils.diffOfDate(this.getStartDate(), new Date());
        }
        return diff == 0? 1 : diff;
    }

    public int currentPercentage(){
        return NumberUtils.getPercentage(this.currentProgress, this.target);
    }

    public int restPercentage(){
        return 100 - currentPercentage();
    }

    public BigDecimal getPastDaily(){
        return this.currentProgress.divide(new BigDecimal(getLastUpdatePastDay()), new MathContext(2));
    }

    public BigDecimal getFutureDaily() {
        return this.target.subtract(this.currentProgress).divide(new BigDecimal(getRestDay()), new MathContext(2));
    }

    public static int parseScheduleValueFromBoolean(boolean sun, boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat)
    {
        int result = 0;
        result += mon? 1 : 0;
        result += tue? 2 : 0;
        result += wed? 4 : 0;
        result += thu? 8 : 0;
        result += fri? 16 : 0;
        result += sat? 32 : 0;
        result += sun? 64 : 0;
        return result;
    }

    public static boolean[] parseScheduleBooleanArrayFromValue(int value)
    {
        boolean[] array = new boolean[7];
        array[0] = (value & 1) == 1;
        array[1] = (value & 2) == 2;
        array[2] = (value & 4) == 4;
        array[3] = (value & 8) == 8;
        array[4] = (value & 16) == 16;
        array[5] = (value & 32) == 32;
        array[6] = (value & 64) == 64;
        return array;
    }

    public static int[] parseScheduleDayOfWeekArrayFromValue(int value)
    {
        boolean[] array = parseScheduleBooleanArrayFromValue(value);
        List <Integer> resultArray = new ArrayList<Integer>();
        for (int i = 0 ; i<array.length; i++)
        {
            if (array[i])
            {
                switch (i) {
                    case 0:
                        resultArray.add(Calendar.MONDAY);
                        break;
                    case 1:
                        resultArray.add(Calendar.TUESDAY);
                        break;
                    case 2:
                        resultArray.add(Calendar.WEDNESDAY);
                        break;
                    case 3:
                        resultArray.add(Calendar.THURSDAY);
                        break;
                    case 4:
                        resultArray.add(Calendar.FRIDAY);
                        break;
                    case 5:
                        resultArray.add(Calendar.SATURDAY);
                        break;
                    case 6:
                        resultArray.add(Calendar.SUNDAY);
                        break;
                }
            }
        }
        int[] result = new int[resultArray.size()];
        for(int i = 0; i < resultArray.size(); i++) result[i] = resultArray.get(i);
        return result;
    }
}
