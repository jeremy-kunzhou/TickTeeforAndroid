package com.huhukun.tickteeforandroid.CalendarView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.CursorLoader;
import android.text.format.Time;
import android.util.Log;
import android.widget.BaseAdapter;

import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;
import com.huhukun.utils.FormatHelper;

public class Day{
	
	int startDay;
	int monthEndDay;
	int day;
	int year;
	int month;
    int dayOfWeek;
	Context context;
	BaseAdapter adapter;
	ArrayList<Project> events = new ArrayList<Project>();
	Calendar cal;
	Day(Context context,int day, int year, int month, int dayOfWeek){
		this.day = day;
		this.year = year;
		this.month = month;
        this.dayOfWeek = dayOfWeek;
		this.context = context;
		cal = Calendar.getInstance();
		cal.set(year, month-1, day);
		int end = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(year, month, end);
		TimeZone tz = TimeZone.getDefault();
		monthEndDay = Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));
	}
	
//	public long getStartTime(){
//		return startTime;
//	}
//	
//	public long getEndTime(){
//		return endTime;
//	}

    public Calendar getCal() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, day);
        return cal;
    }
	
	public int getMonth(){
		return month;
	}
	
	public int getYear(){
		return year;
	}
	
	public void setDay(int day){
		this.day = day;
	}
	
	public int getDay(){
		return day;
	}

    public int getDayOfWeek () {return this.dayOfWeek;}
	
	/**
	 * Add an event to the day
	 * 
	 * @param event
	 */
//	public void addEvent(Event event){
//		events.add(event);
//	}
	
	/**
	 * Set the start day
	 * 
	 * @param startDay
	 */
	public void setStartDay(int startDay){
		this.startDay = startDay;
		new GetEvents().execute();
	}
	
	public int getStartDay(){
		return startDay;
	}
	
	public int getNumOfEvenets(){
		return events.size();
	}
	
	/**
	 * Returns a list of all the colors on a day
	 * 
	 * @return list of colors
	 */
	public Set<Integer> getColors(){
		Set<Integer> colors = new HashSet<Integer>();
		if (events.size() > 0) colors.add(0);
		
		return colors;
	}
	
	/**
	 * Get all the events on the day
	 * 
	 * @return list of events
	 */
//	public ArrayList<Event> getEvents(){
//		return events;
//	}
	
	public void setAdapter(BaseAdapter adapter){
		this.adapter = adapter;
	}
	
	private class GetEvents extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
            String dateString = FormatHelper.shortLocalDateFormatter.format(getCal().getTime());
            String dayOfWeekString = "";
                switch (getDayOfWeek())
                {
                    case Calendar.MONDAY:
                        dayOfWeekString = "1";
                        break;
                    case Calendar.TUESDAY:
                        dayOfWeekString = "2";
                        break;
                    case Calendar.WEDNESDAY:
                        dayOfWeekString = "4";
                        break;
                    case Calendar.THURSDAY:
                        dayOfWeekString = "8";
                        break;
                    case Calendar.FRIDAY:
                        dayOfWeekString = "16";
                        break;
                    case Calendar.SATURDAY:
                        dayOfWeekString = "32";
                        break;
                    case Calendar.SUNDAY:
                        dayOfWeekString = "64";
                        break;
                }
            Uri baseUri = null;
            try {
                baseUri = Uri.withAppendedPath(TickteeProvider.CONTENT_URI_ON_DAY, FormatHelper.fromLocalDateTimeStringToUTCString(dateString + " 12:00 AM") + "/" + FormatHelper.fromLocalDateTimeStringToUTCString(dateString + " 11:59 PM") + "/" + dayOfWeekString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Cursor c = context.getContentResolver().query(baseUri,SqlOpenHelper.LOADER_COLUMNS, null, null, null);
			if(c != null && c.moveToFirst()){
				do{
                    try {
                        events.add(new Project(c));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }while(c.moveToNext());
				c.close();
			}

			return null;
		}
		
		protected void onPostExecute(Void par){
			adapter.notifyDataSetChanged();
		}
		
	}
	

}
