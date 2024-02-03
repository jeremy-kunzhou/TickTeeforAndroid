package com.huhukun.tickteeforandroid;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import android.os.Process;

import androidx.core.app.NotificationCompat;

import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.tickteeforandroid.providers.MethodEnum;
import com.huhukun.utils.BooleanUtils;
import com.huhukun.utils.FormatHelper;
import com.huhukun.utils.NumberUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by kun on 5/09/2014.
 */
public class MyAlarmService extends Service {
    private static final String TAG = App_Constants.APP_TAG +"MyAlarmService";
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private List<String> overdueList = new ArrayList<String>();
    private List<String> onProgressList = new ArrayList<String>();
    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            overdueList.clear();
            onProgressList.clear();
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
//            long endTime = System.currentTimeMillis() + 5*1000;
//            while (System.currentTimeMillis() < endTime) {
//                synchronized (this) {
//                    try {
//                        wait(endTime - System.currentTimeMillis());
//                    } catch (Exception e) {
//                    }
//                }
//            }
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(SqlOpenHelper.TableConstants.TABLE_NAME);


            qb.appendWhere( SqlOpenHelper.TableConstants.COL_STATUS );
            qb.appendWhere( "!=" );
            qb.appendWhere( "'" );
            qb.appendWhere( MethodEnum.DELETE.toString() );
            qb.appendWhere( "'" );
            qb.appendWhere( " AND ");
            qb.appendWhere( SqlOpenHelper.TableConstants.COL_ALERT_TYPE );
            qb.appendWhere( "!=" );
            qb.appendWhere( "'" );
            qb.appendWhere(Project.AlertType.OFF.toString());
            qb.appendWhere( "'" );

            // If no sort order is specified use the default.
            String orderBy = SqlOpenHelper.TableConstants.DEFAULT_SORT_ORDER;
            SqlOpenHelper dbHelper = new SqlOpenHelper(MyAlarmService.this);
            // Get the database and run the query.
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = qb.query(
                    db, SqlOpenHelper.LOADER_COLUMNS, null, null, null, null, orderBy);

            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                @SuppressLint("Range") BigDecimal current = new BigDecimal(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_CURRENT_PROGRESS)));
                @SuppressLint("Range") BigDecimal target = new BigDecimal(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_TARGET)));
                @SuppressLint("Range") Project.AlertType alert = Project.AlertType.valueOf(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_ALERT_TYPE)));
                Calendar calendar = Calendar.getInstance();
                int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
                Project.AlertType todayAlert = Project.AlertType.OFF;
                switch (day_of_week){
                    case 1:
                        todayAlert = Project.AlertType.EVERY_SUNDAY;
                        break;
                    case 2:
                        todayAlert = Project.AlertType.EVERY_MONDAY;
                        break;
                    case 3:
                        todayAlert = Project.AlertType.EVERY_TUESDAY;
                        break;
                    case 4:
                        todayAlert = Project.AlertType.EVERY_WEDNESDAY;
                        break;
                    case 5:
                        todayAlert = Project.AlertType.EVERY_THURSDAY;
                        break;
                    case 6:
                        todayAlert = Project.AlertType.EVERY_FRIDAY;
                        break;
                    case 7:
                        todayAlert = Project.AlertType.EVERY_SATURDAY;
                        break;
                }
                if(alert == Project.AlertType.EVERY_DAY || alert == todayAlert) {
                    @SuppressLint("Range") String start_date_string = cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_START_AT));
                    @SuppressLint("Range") String end_date_string = cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_END_AT));
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_NAME));
                    @SuppressLint("Range") boolean isConsumed = BooleanUtils.parse(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_IS_CONSUMED)));
                    if (start_date_string != null && end_date_string != null && !TextUtils.isEmpty(start_date_string) && !TextUtils.isEmpty(end_date_string)) {
                        try {
                            Date start = FormatHelper.toLocalDateFromUTCString(start_date_string);
                            Date end = FormatHelper.toLocalDateFromUTCString(end_date_string);
                            Date now = new Date();
                            Log.d(TAG, name + ": " + NumberUtils.getPercentage(current, target) + " " + NumberUtils.getPercentage(start, end, now)
                                    + " " + (end.getTime() - start.getTime()) + " " + (now.getTime() - start.getTime()));
                            if (!isConsumed && NumberUtils.getPercentage(current, target) < NumberUtils.getPercentage(start, end, now)) {
                                overdueList.add(name);
                            } else if (isConsumed && NumberUtils.getPercentage(current, target) > NumberUtils.getPercentage(start, end, now)) {
                                overdueList.add(name);
                            } else {
                                onProgressList.add(name);
                            }
                        } catch (ParseException e) {
                            Log.d(TAG, e.toString());
                        }
                    }

                }
                cursor.moveToNext();
            }
            cursor.close();

            StringBuilder contentBuilder = new StringBuilder();
            if(overdueList.size() == 1)
            {
                contentBuilder.append(String.format("%s is Overdue.", overdueList.get(0)));
            }
            else if (overdueList.size() > 1){
                contentBuilder.append(String.format("%s and %d more are Overdue.", overdueList.get(0), overdueList.size() - 1));
            }
            else {
                if (onProgressList.size() == 1){
                    contentBuilder.append(String.format("%s is on progress. ", onProgressList.get(0)));
                }
                else if (onProgressList.size() > 1){
                    contentBuilder.append(String.format("%s and %d more are on progress. ", onProgressList.get(0), onProgressList.size()));
                }
            }
            if (overdueList.size() == 0 && onProgressList.size() == 0)
            {
                Log.d(TAG, "No projects need to be alarmed");
            }
            else {
                final Intent notificationIntent = new Intent(MyAlarmService.this.getApplicationContext(), MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyAlarmService.this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(MyAlarmService.this.getApplicationContext())
                                .setSmallIcon(R.drawable.ic_action_done)
                                .setContentTitle(App_Constants.APP_TAG)
                                .setContentText(contentBuilder.toString())
                                .setOnlyAlertOnce(true);
                mBuilder.setContentIntent(pendingIntent);
                mBuilder.setDefaults(Notification.DEFAULT_ALL);
                mBuilder.setAutoCancel(true);
                mBuilder.setWhen(System.currentTimeMillis());
                mBuilder.setAutoCancel(true);
//            startForeground(1, mBuilder.build());
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(1, mBuilder.build());

                AlertDialog.Builder dialog=new AlertDialog.Builder(MyAlarmService.this);
                dialog.setTitle("Notice");
                dialog.setIcon(android.R.drawable.ic_dialog_info);
                dialog.setMessage(contentBuilder.toString());
                dialog.setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //to MainActivity
//                        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        MyAlarmService.this.startActivity(notificationIntent);
                    }
                });
                AlertDialog mDialog=dialog.create();
                // set to system alert type
                mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                mDialog.show();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {

//        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
