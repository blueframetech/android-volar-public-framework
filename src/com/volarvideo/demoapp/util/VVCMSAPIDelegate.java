package com.volarvideo.demoapp.util;

import java.util.List;

import com.volarvideo.demoapp.models.Broadcast;
import com.volarvideo.demoapp.models.Broadcast.BroadcastStatus;

/**Delegate for api
 * 
 * @author Chris Allen
 */
public interface VVCMSAPIDelegate {
	public void domainRequestComplete(VVCMSAPI api, String domain, Exception e);
	public void authenticationRequestDidFinish(VVCMSAPI api, Exception e);
	public void logoutRequestDidFinish(VVCMSAPI api, Exception e);
	public void requestForUserNameComplete(VVCMSAPI api, String userName, Exception e);
	public void requestForBroadcastsOfStatusNameComplete(VVCMSAPI api, BroadcastStatus status, List<Broadcast> events, Exception e);
}
