package com.volarvideo.demoapp.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


public class B3Utils {
	private static String TAG = "B3Utils";
	
	public static Date dateFromString(String dateString){
		Date date;
		if (dateString.length() == 19){
			try {
				date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
				return date;
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			try {
				date = new SimpleDateFormat("M-d-yyyy").parse(dateString);
				return date;
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}

	}
	
	public static void addEventToCalendar(Context ctx, long when, String title, int activityResult) {
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("beginTime", when);
		//intent.putExtra("allDay", true);
		//intent.putExtra("rrule", "FREQ=YEARLY");
		//intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
		intent.putExtra("title", title);
		((Activity) ctx).startActivityForResult(intent, activityResult);
	}
	
	public static int getTotalCalendarEvents(Context context) {
	    Cursor cursor = context.getContentResolver().query(
            Uri.parse("content://com.android.calendar/events"),
            new String[] { "calendar_id", "title", "description",
                    "dtstart", "dtend", "eventLocation" }, null,
                    null, null);
	    cursor.moveToFirst();

	    /*
	    List<String> nameOfEvent = new ArrayList<String>();
	    String calNames[] = new String[cursor.getCount()];
	    for (int i = 0; i < calNames.length; i++) {
	    	nameOfEvent.add(cursor.getString(1));
	    	cursor.moveToNext();
	    }
	    */
	    return cursor.getCount();
	}
	
	public static String m3u8Parse(String m3u8URL){
		String m3u8String;
		String audioOnly = null;
		try{
			URL url = new URL(m3u8URL);				
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        	InputStream recievedDataStream = urlConnection.getInputStream();
        	BufferedReader reader = new BufferedReader(new InputStreamReader(recievedDataStream));
			StringBuilder sb = new StringBuilder();
			String line = reader.readLine();
			while (line != null){
				sb.append(line);
				line = reader.readLine();
			}
			reader.close();
			m3u8String = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		String[] poundArray = m3u8String.split("#");
		for(String subPoundString:poundArray){
			if (subPoundString.contains("http://")){
				String[] httpArray = subPoundString.split("http://");
				if(httpArray[httpArray.length - 1].length() > 0){
					if (!httpArray[httpArray.length - 1].contains("audioonly")){
						return "http://" + httpArray[httpArray.length - 1];
					} else {
						audioOnly = "http://" + httpArray[httpArray.length - 1];
					}
				}
			}
		}
		
		return audioOnly;
	}
	
	public static String stringForString(String string){
		try {
			return URLDecoder.decode(string,"UTF-8").replaceAll("(\\\\'){2}", "\"").replaceAll("(\\\\'){1}","'").replaceAll("\\\\\"", "\"");
		} catch (UnsupportedEncodingException e) {
			Log.d(TAG,"StringForString error");
			e.printStackTrace();
			return null;
		}
	}
}
