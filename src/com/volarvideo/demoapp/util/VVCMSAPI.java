package com.volarvideo.demoapp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.androidtools.networking.Networking;
import com.volarvideo.demoapp.models.Broadcast;
import com.volarvideo.demoapp.models.Broadcast.BroadcastStatus;

/**Wrapper for api
 * 
 * @author Chris Allen on Mar 21, 2013
 */
public class VVCMSAPI {

	// executor
	private ExecutorService executor;
	
	// hard coded endpoints
//	private static final String LoginEP 		 = "api/auth/login";
	private static final String KeyUsername 	 = "email";
	private static final String KeyPassword 	 = "password";
	private static final String DomainInfoEP 	 = "api/info/domain";

	// domain response keys
	private static final String KeySuccess       = "success";
//	private static final String KeyError 		 = "error";
//	private static final String KeyErrorCode 	 = "code";
//	private static final String KeyErrorMessage  = "message";
	private static final String KeySites 		 = "sites";
	private static final String KeySiteTitle	 = "title";
	private static final String KeySiteEndPoints = "endpoints";
//	private static final String KeyEventId		 = "id";

	// user name response keys
	private static final String KeyUser                = "user";
	private static final String KeyUserName            = "name";

	// end point keys
//	private static final String KeyAuthLogout          = "auth/logout";
	private static final String KeyUserInfo            = "user/info";
	private static final String KeyAllBroadcasts       = "broadcast/all";
	private static final String KeyScheduledBroadcasts = "broadcast/scheduled";
	private static final String KeyStreamingBroadcasts = "broadcast/streaming";
	private static final String KeyArchivedBroadcasts  = "broadcast/archived";

//	private static final String ErrorDomain = "com.vv.cmsapi";

	// broadcast(s) responds keys
	private static final String KeyBroadcastsArray     		= "broadcasts";

	private RestTemplate REST = Networking.defaultRest();
	private String apiURL;
	private JSONArray sitesArray;
	private int currentSiteIndex = 0;
	private JSONObject endpointDict;
	private boolean loggedIn;

	private VVCMSAPIDelegate delegate;
	
	public VVCMSAPI() {
		executor = Executors.newFixedThreadPool(5);
	}
	
	public void setDelegate(VVCMSAPIDelegate d) {
		delegate = d;
	}
	
	public void authenticationRequestForDomain(final String url, final String uname, final String pwd) {
		if(delegate == null)
			return;
		
		executor.submit(new Runnable() {
			@Override
			public void run() {
				Reference ref = new Reference();
				ref.url = url;
				Exception e = validateDomainUrl(ref);
				if(e != null) {
					delegate.authenticationRequestDidFinish(VVCMSAPI.this,  e);
					return;
				}
				
				if(uname != null && pwd != null) {
			    	try {
						JSONObject payload = new JSONObject();
						payload.put(KeyUsername, uname);
						payload.put(KeyPassword, pwd);
						
						String response = REST.postForObject(ref.url+DomainInfoEP, payload.toString(), String.class);
						JSONObject json = null;
						if(response != null) json = new JSONObject(response);
						boolean success = false;
						if(json != null) {
							success = json.getBoolean(KeySuccess);
							if(success) {
								loggedIn = true;
								sitesArray = json.getJSONArray(KeySites);
								json = sitesArray.getJSONObject(currentSiteIndex).getJSONObject(KeySiteEndPoints);
								endpointDict = json;
								delegate.authenticationRequestDidFinish(VVCMSAPI.this, null);
							}
						}
						if(!success) {
							delegate.authenticationRequestDidFinish(VVCMSAPI.this, new BadRequestException());
							return;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						delegate.authenticationRequestDidFinish(VVCMSAPI.this, e1);
					}
				}
				else {
			    	try {
						String response = REST.getForObject(ref.url+DomainInfoEP, String.class);
						JSONObject json = null;
						if(response != null) json = new JSONObject(response);
						boolean success = false;
						if(json != null) {
							success = json.getBoolean(KeySuccess);
							if(success) {
								sitesArray = json.getJSONArray(KeySites);
								json = sitesArray.getJSONObject(currentSiteIndex).getJSONObject(KeySiteEndPoints);
								endpointDict = json;
								delegate.authenticationRequestDidFinish(VVCMSAPI.this, null);
							}
						}
						if(!success) {
							delegate.authenticationRequestDidFinish(VVCMSAPI.this, new BadRequestException());
							return;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						delegate.authenticationRequestDidFinish(VVCMSAPI.this, e1);
					}
				}
			}
		});
	}
	
	public void requestUserName() {
		if(delegate == null)
			return;
		if(!loggedIn) {
			delegate.requestForUserNameComplete(VVCMSAPI.this, null, new NotLoggedInException());
			return;
		}
		executor.submit(new Runnable() {
			@Override
			public void run() {
				String userInfoURL = endpointDict.optString(KeyUserInfo);
				if(userInfoURL != null) {
					String name = null;
			    	try {
						String response = REST.getForObject(userInfoURL, String.class);
						JSONObject json = null;
						if(response != null) json = new JSONObject(response);
						if(json != null) {
							boolean success = json.getBoolean(KeySuccess);
							if(success) {
								name = json.getJSONObject(KeyUser).getString(KeyUserName);
								delegate.requestForUserNameComplete(VVCMSAPI.this, name, null);
							}
							else {
								delegate.requestForUserNameComplete(VVCMSAPI.this, name, new BadRequestException());
								return;
							}
						}
					} catch (Exception e1) {
						delegate.requestForUserNameComplete(VVCMSAPI.this, name, e1);
					}
				}
			}
		});
	}
	
	public void requestBroadcastsWithStatus(final BroadcastStatus status, final int page, final int resultsPerPage) {
		if(delegate == null)
			return;
		String endPoint = "";
		switch(status) {
		case Unknown:
			delegate.requestForBroadcastsOfStatusNameComplete(VVCMSAPI.this, status, null, null);
			return;
		case All:
			endPoint = KeyAllBroadcasts;
			break;
		case Archived:
			endPoint = KeyArchivedBroadcasts;
			break;
		case Scheduled:
			endPoint = KeyScheduledBroadcasts;
			break;
		case Streaming:
			endPoint = KeyStreamingBroadcasts;
			break;
		}
		
		if(endpointDict == null)
			return;
		
		String endPointUrl = endpointDict.optString(endPoint);
		
		if(endPointUrl == "" || endPointUrl == null) {
			delegate.requestForBroadcastsOfStatusNameComplete(VVCMSAPI.this, status, null, new UnknownEndpointException());
			return;
		}
		
		if(resultsPerPage > 0 && page >= 0) {
			String conjunction = "?";
			if(endPointUrl.contains(conjunction))
				conjunction = "&";
			endPointUrl += conjunction+"page="+page+"&per_page="+resultsPerPage+"&sort_by=date&sort_dir=asc";
		}
		
		final String urlString = endPointUrl;

		executor.submit(new Runnable() {
			@Override
			public void run() {
		    	try {
					String response = REST.getForObject(urlString, String.class);
					JSONObject json = null;
					Broadcast b = null;
					if(response != null) json = new JSONObject(response);
					if(json != null) {
						boolean success = json.getBoolean(KeySuccess);
						if(success) {
							JSONArray events = json.getJSONArray(KeyBroadcastsArray);
							List<Broadcast> broadcasts = new ArrayList<Broadcast>();
							if(events != null) {
								for(int index=0; index < events.length(); index++) {
									b = new Broadcast(events.getJSONObject(index));
									if(b.status != BroadcastStatus.Unknown)
										broadcasts.add(b);
								}
								delegate.requestForBroadcastsOfStatusNameComplete(VVCMSAPI.this, status, broadcasts, null);
							}
						}
						else {
							delegate.requestForBroadcastsOfStatusNameComplete(VVCMSAPI.this, status, null, new BadRequestException());
							return;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					delegate.requestForBroadcastsOfStatusNameComplete(VVCMSAPI.this, status, null, e);
				}
			}
		});
	}
	
	private Exception validateDomainUrl(Reference ref) {
		if(ref == null || ref.url == null)
			return new NoDomainURLException();
		
		ref.url = ref.url.toLowerCase();
		if(!ref.url.contains("http://") && !ref.url.contains("https://"))
			ref.url = "http://"+ref.url;
		
		if(!ref.url.endsWith("/"))
			ref.url += "/";
		
		try {
			String response = REST.getForObject(ref.url, String.class);
			if(response == null)
				return new DomainUnreachableException();
			else if(apiURL == null || !apiURL.equalsIgnoreCase(ref.url)) {
				if(loggedIn)
					logout();
				apiURL = ref.url;
				currentSiteIndex = 0;
				sitesArray = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new DomainUnreachableException();
		}
		
		return null;
	}
	
	public void requestDomain(final String urlString) {
		if(delegate == null)
			return;
		executor.submit(new Runnable() {
			@Override
			public void run() {
				Reference ref = new Reference();
				ref.url = urlString;
				Exception e = validateDomainUrl(ref);
				if(e != null) {
					delegate.domainRequestComplete(VVCMSAPI.this, urlString, e);
					return;
				}
		
		    	try {
					String response = REST.getForObject(ref.url+DomainInfoEP, String.class);
					JSONObject json = null;
					if(response != null) json = new JSONObject(response);
					if(json != null) {
						boolean success = json.getBoolean(KeySuccess);
						if(success) {
							sitesArray = json.getJSONArray(KeySites);
							json = sitesArray.getJSONObject(currentSiteIndex).getJSONObject(KeySiteEndPoints);
							endpointDict = json;
							loggedIn = true;
							delegate.domainRequestComplete(VVCMSAPI.this, urlString, null);
						}
						else {
							delegate.domainRequestComplete(VVCMSAPI.this, urlString, new BadRequestException());
							return;
						}
					}
				} catch (Exception e1) {
					delegate.domainRequestComplete(VVCMSAPI.this, urlString, new InvalidDomainURLException());
				}
			}
		});
	}
	
	public String[] siteNames() {
		if(sitesArray != null && sitesArray.length() > 0) {
			String[] names = new String[sitesArray.length()];
			for(int index=0; index< sitesArray.length(); index++)
				try {
					names[index] = sitesArray.getJSONObject(index).getString(KeySiteTitle);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			return names;
		}
		return null;
	}
	
	public void setCurrentSiteIndex(int index) {
		if(sitesArray != null && sitesArray.length() > currentSiteIndex && currentSiteIndex >= 0) {
			currentSiteIndex = index;
			try {
				endpointDict = sitesArray.getJSONObject(currentSiteIndex).getJSONObject(KeySiteEndPoints);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String siteName() {
		if(sitesArray != null && sitesArray.length() > currentSiteIndex)
			try {
				return (String) sitesArray.getJSONObject(currentSiteIndex).getString(KeySiteTitle);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	public void logout() {
		
	}

	private static boolean APIDataLoaderLatestReachableResult = false;
	private static boolean APIDataLoaderReachableMessageShown = false;
	
	public boolean isReachable(Context ctx) {
    	if(ctx != null) {
	        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	        // if no network is available networkInfo will be null
	        // otherwise check if we are connected
	        if (networkInfo != null && networkInfo.isConnected()) {
	            APIDataLoaderLatestReachableResult = true;
	        } else {
	        	APIDataLoaderLatestReachableResult = false;
	        }
    	}
    	return APIDataLoaderLatestReachableResult;
	}
	
	public boolean latestReachabilityResult() {
		return APIDataLoaderLatestReachableResult;
	}
	
	public void showReachabilityMessageIfNeedTo(Context ctx) {
		if(!APIDataLoaderReachableMessageShown) {
			APIDataLoaderReachableMessageShown = true;
			Toast.makeText(ctx, "API could not be reached.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class Reference {
		String url;
	}

	/** InvalidDelegateException */
	public static class InvalidDelegateException extends Exception{
		private static final long serialVersionUID = 1L;

		public InvalidDelegateException(){
	      super("A valid delegate was not provided.");
	    }
	}
	/** BadRequestException */
	public static class BadRequestException extends Exception{
		private static final long serialVersionUID = 1L;

		public BadRequestException(){
	      super("The server responded with a 400.  Was your payload formatted correctly?");
	    }
	}
	/** NoDomainURLException */
	public static class NoDomainURLException extends Exception{
		private static final long serialVersionUID = 1L;

		public NoDomainURLException(){
	      super("No domain given.");
	    }
	}
	/** DomainUnreachableException */
	public static class DomainUnreachableException extends Exception{
		private static final long serialVersionUID = 1L;

		public DomainUnreachableException(){
	      super("Domain not reachable.");
	    }
	}
	/** InvalidDomainURLException */
	public static class InvalidDomainURLException extends Exception{
		private static final long serialVersionUID = 1L;

		public InvalidDomainURLException(){
	      super("Invalid domain URL.");
	    }
	}
	/** NotLoggedInException */
	public static class NotLoggedInException extends Exception{
		private static final long serialVersionUID = 1L;

		public NotLoggedInException(){
	      super("Not logged in.");
	    }
	}
	/** UnknownEndpointException */
	public static class UnknownEndpointException extends Exception{
		private static final long serialVersionUID = 1L;

		public UnknownEndpointException(){
	      super("Unknown Endpoint URL. Are you sure you are logged in?");
	    }
	}
}
