package com.volarvideo.demoapp.models;

import java.util.Date;

import org.json.JSONObject;

import com.volarvideo.demoapp.util.B3Utils;

public class Broadcast {

	private static final String KeyBroadcastStatus     		= "status";
	private static final String KeyBroadcastStatusScheduled 	= "scheduled";
	private static final String KeyBroadcastStatusStreaming 	= "streaming";
	private static final String KeyBroadcastStatusArchived 	= "archived";
	private static final String KeyBroadcastID         		= "id";
	private static final String KeyBroadcastTitle      		= "title";
	private static final String KeyBroadcastDescr      		= "description";
	private static final String KeyBroadcastURL    			= "vmap";
	private static final String KeyBroadcastVMAPURL    		= "vmap";
	private static final String KeyBroadcastThumbURL   		= "thumbnail";
	private static final String KeyBroadcastStartDate  		= "start_date";
	private static final String KeyBroadcastEditDate   		= "edit_date";
	private static final String KeyBroadcastRating     		= "rating";
	private static final String KeyBroadcastAudioOnly  		= "audioOnly";
	private static final String KeyBroadcastProgress   		= "progress";
	private static final String KeyBroadcastAuthorDict 		= "author";
	private static final String KeyAuthorDictName      		= "full_name";
	private static final String KeyBroadcastIsStreaming 		= "isStreaming";
	
	public int id;
	public String title = "", description = "";
	public BroadcastStatus status = BroadcastStatus.Unknown;
	public String url = "", vmapURL = "", thumbnailURL = "";
	public Date editDate, startDate;
	public int numberOfReviews;
	public double averageReview, userReview;
	public boolean audioOnly;
	public double progress;
	public String authorName = "";
	public boolean isStreaming;
	
	public Broadcast(JSONObject json) {
		status = BroadcastStatus.fromString(json.optString(KeyBroadcastStatus));
		if(status != BroadcastStatus.Unknown) {
			id = json.optInt(KeyBroadcastID);
			title = json.optString(KeyBroadcastTitle);
			description = json.optString(KeyBroadcastDescr);
			url = json.optString(KeyBroadcastURL);
			vmapURL = json.optString(KeyBroadcastVMAPURL);
			thumbnailURL = json.optString(KeyBroadcastThumbURL);
			editDate = B3Utils.dateFromString(json.optString(KeyBroadcastEditDate));
			startDate = B3Utils.dateFromString(json.optString(KeyBroadcastStartDate));
			numberOfReviews = json.optInt("numberOfReviews");
			averageReview = json.optDouble(KeyBroadcastRating);
			userReview = json.optDouble("userReview");
			audioOnly = json.optBoolean(KeyBroadcastAudioOnly);
			progress = json.optDouble(KeyBroadcastProgress);
			JSONObject authorDict = json.optJSONObject(KeyBroadcastAuthorDict);
			authorName = null;
			if(authorDict != null)
				authorName = authorDict.optString(KeyAuthorDictName);
			isStreaming = json.optBoolean(KeyBroadcastIsStreaming);
		}
	}

	public enum BroadcastStatus {
		Unknown,
		Scheduled,
		Streaming,
		Archived,
		All;
		
		public static BroadcastStatus fromString(String str) {
			if(str.equalsIgnoreCase(KeyBroadcastStatusScheduled))
				return Scheduled;
			else if(str.equalsIgnoreCase(KeyBroadcastStatusStreaming))
				return Streaming;
			else if(str.equalsIgnoreCase(KeyBroadcastStatusArchived))
				return Archived;
			else
				return Unknown;
		}
	}
}
