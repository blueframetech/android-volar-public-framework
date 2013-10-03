package com.volarvideo.demoapp.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;
import android.webkit.URLUtil;

/**VVUtils
 * 
 * @author Chris Allen on Mar 22, 2013
 */
public class VVUtils {

	public static float intervalFromString(String str) {
		if(str == null || str.length() ==0)
			return 0;
		
		// 00:00:00
		Matcher m = Pattern.compile("^(\\d{2}):(\\d{2}):(\\d{2})$").matcher(str);
		if(m.find()) return matcherToMilli(m) / 1000;
		
		// 00:00:00.000
		m = Pattern.compile("^(\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{1,4})$").matcher(str);
		if(m.find()) return matcherToMilli(m) / 1000;
		
		// 20.25%
		m = Pattern.compile("^(\\d{0,2}|100)((\\.|,)(\\d*))?\\s*\\%?\\s*$").matcher(str);
		if(m.find()) {
			if(m.group(1).length() > 0) {
				String num = m.group(1);
				if(num != "100" && m.group(2) != null)
					num += m.group(2);
				float percent = Float.parseFloat(num)/100;
				//long result = (long) (1000 * percent);
				return -percent;
			}
		}
		return 0;
	}
	
	public static String[] formatTimeStatus(long currentTimeUs, long durationUs, boolean hideLeft) {
		int[] hms = new int[3];
		long durUs = Math.abs(durationUs);
					
		String totalDurationString =  prettyPrintSeconds(durUs, hms, false, false);			
		String currentPositionString = hideLeft ? "" :  prettyPrintSeconds(currentTimeUs, hms, hms[0]>0||hms[1]>9, hms[0]>0);
		
		return new String[] { currentPositionString, (durationUs<0?"-":"")+totalDurationString };
	}
	
	private static String prettyPrintSeconds(long uSeconds, int[] hms, boolean leadingMinutes, boolean leadingHours) {
		int seconds = (int) (uSeconds / 1000 / 1000);
		
		int h = hms[0] = (int) Math.floor(seconds / 3600f);
		int m = hms[1] = (int) Math.floor(seconds % 3600f / 60f);
		int s = hms[2] = (int) (seconds % 60f);
		
		return ((h > 0 || leadingHours) ? (h + ":") : "")
		+ (((h > 0 || leadingMinutes) && m < 10f) ? "0" : "")
			+ m + ":" 
			+ (s < 10f ? "0" : "") 
			+ s;
	}	
	
	private static long matcherToMilli(Matcher m) {
		if(m == null)
			return 0;
		
		long timeInterval = 0;
		if(m.groupCount() >= 3) {
			timeInterval += Integer.parseInt(m.group(1)) * 60 * 60 * 1000;	// hours
			timeInterval += Integer.parseInt(m.group(2)) * 60 * 1000;		// minutes
			timeInterval += Integer.parseInt(m.group(3)) * 1000;			// seconds
		}
		if(m.groupCount() == 4)
			timeInterval += Float.parseFloat("0."+m.group(4)) * 1000;		// seconds fraction
		
		return timeInterval;
	}
	
	public static long timeSinceNow(long pastTime)
	{
		return -(System.currentTimeMillis() - pastTime);
	}
	
	public static long timeRemaining(long futureTime)
	{
		return futureTime - System.currentTimeMillis();
	}

	public static boolean toBoolean(String val) {
		val = val.trim();
		return (val.equalsIgnoreCase("true")
				|| val.equalsIgnoreCase("1")
				|| val.equalsIgnoreCase("yes"));
	}
	
	public static boolean isEmpty(String val) {
		return (val == null || val.length() == 0);
	}
	
	public static String cacheBreak(String url) {
		int x = new Random().nextInt(100000000);
		url = url.replace("[CACHEBUSTING]", zeroPad(x, 8));
		url = url.replace("[CACHEBUSTER]", zeroPad(x, 8));
		url = url.replace("[CACHEBREAKER]", zeroPad(x, 8));
		return url;
	}
	
	private static String zeroPad(int number, int width) {
		String ret = ""+number;
		while( ret.length() < width )
			ret="0" + ret;
		return ret;
	}
	
	public static boolean isUrl(String s) {
		return URLUtil.isValidUrl(s);
	}

	public static int convertToInt(String str) throws NumberFormatException {
		int s, e;
		for (s = 0; s < str.length(); s++)
			if (Character.isDigit(str.charAt(s)))
				break;
		for (e = str.length(); e > 0; e--)
			if (Character.isDigit(str.charAt(e - 1)))
				break;
		if (e > s) {
			try {
				return Integer.parseInt(str.substring(s, e));
			} catch (NumberFormatException ex) {
				throw new NumberFormatException();
			}
		} else {
			throw new NumberFormatException();
		}
	}
}
