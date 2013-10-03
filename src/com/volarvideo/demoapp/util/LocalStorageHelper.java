package com.volarvideo.demoapp.util;

import java.util.ArrayList;
import java.util.List;

import com.volarvideo.demoapp.BroadcastsActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**Utility class that gets/saves a list of domains and keeps track of the one
 * that is currently selected.
 * 
 * @author Chris Allen on Mar 21, 2013
 */
public class LocalStorageHelper {

	/* Shared Preferences */
	private final static String PREF = "com.volarvideo.demoapp.domains";
	private final static String DOMAINS = "domains";
	private final static String CURR_DOMAIN = "curr_domain";
	
	public static List<String> getDomains(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
		String domains = prefs.getString(DOMAINS, null);

		List<String> domainList = new ArrayList<String>();
		if(domains != null)
			for(String d: domains.split(";"))
				domainList.add(d);
		else {
			domainList.add(BroadcastsActivity.VolarContentDomain);
			saveDomains(ctx, domainList);
		}
		return domainList;
	}

	public static void saveDomains(Context ctx, List<String> domainList) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);

		String domains = "";
		for(String d: domainList)
			domains += d+";";

		
		Editor edit = prefs.edit();
		edit.putString(DOMAINS, domains);
		edit.commit();
	}
	
	public static String getCurrDomain(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
		String domain = prefs.getString(CURR_DOMAIN, BroadcastsActivity.VolarContentDomain);
		
		if(domain == null)
			saveCurrDomain(ctx, BroadcastsActivity.VolarContentDomain);
		
		return domain;
	}
	
	public static void saveCurrDomain(Context ctx, String domain) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
		
		Editor edit = prefs.edit();
		edit.putString(CURR_DOMAIN, domain);
		edit.commit();
	}
}
