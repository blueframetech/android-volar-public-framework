package com.volarvideo.mobileapidev;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.URLConnectionImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.volarvideo.mobileapidev.adapters.BroadcastAdapter;
import com.volarvideo.mobileapidev.util.Conversions;
import com.volarvideo.mobilesdk.api.VVCMSAPI;
import com.volarvideo.mobilesdk.api.VVCMSAPIDelegate;
import com.volarvideo.mobilesdk.models.VVCMSBroadcast;
import com.volarvideo.mobilesdk.models.VVCMSBroadcast.BroadcastStatus;

@SuppressWarnings("deprecation")
public class BroadcastsActivity extends VolarActivity implements VVCMSAPIDelegate {

	public static final String VolarContentDomain = "vcloud.volarvideo.com";
	
	private VVCMSAPI api = new VVCMSAPI();
	private Button upcomingButton, liveButton, archivedButton;
	private ListView listView;
	private List<VVCMSBroadcast> upcomingBroadcasts = new ArrayList<VVCMSBroadcast>();
	private List<VVCMSBroadcast> liveBroadcasts = new ArrayList<VVCMSBroadcast>();
	private List<VVCMSBroadcast> archivedBroadcasts = new ArrayList<VVCMSBroadcast>();
	private BroadcastAdapter currAdapter, upcomingAdapter, liveAdapter, archivedAdapter;
	protected int FOOTER_HEIGHT = 45; // dp
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.media_list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		
		// Fix phones that have trouble drawing gradients
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);

		setupImageLoader();
		setupViews();

		showDialog(DIALOG_LOADING);
		api.setDelegate(this);
		api.authenticationRequestForDomainWithSlug("themwc", VolarContentDomain, null, null);

		((ImageView)getWindow().findViewById(R.id.leftButton)).setImageResource(R.drawable.refresh_selector);
		getWindow().findViewById(R.id.leftButton).setVisibility(View.VISIBLE);
		getWindow().findViewById(R.id.leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(api.siteName() != null) {
	        		showDialog(DIALOG_LOADING);
					api.requestBroadcastsWithStatus(BroadcastStatus.All, 0, 0);
				}
			}
		});
	}

    private void setupViews() {
		listView = (ListView) findViewById(R.id.mediaList);
		
		findViewById(R.id.mediaFooter).setVisibility(View.VISIBLE);
    	FOOTER_HEIGHT = (int) Conversions.pixelsToDp(this, FOOTER_HEIGHT);
		LayoutParams params = (LayoutParams) findViewById(R.id.mediaList).getLayoutParams();
		params.bottomMargin = FOOTER_HEIGHT;
		findViewById(R.id.mediaList).setLayoutParams(params);
		
		upcomingButton = (Button) findViewById(R.id.upcoming);
		upcomingButton.setText(getString(R.string.upcoming)+" (0)");
		upcomingButton.setEnabled(false);
		upcomingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currAdapter != upcomingAdapter && upcomingAdapter.getCount() > 0) {
					upcomingButton.setSelected(true);
					liveButton.setSelected(false);
					archivedButton.setSelected(false);
					currAdapter = upcomingAdapter;
					listView.setAdapter(currAdapter);
				}
			}
		});
		
		liveButton = (Button) findViewById(R.id.live);
		liveButton.setText(getString(R.string.live)+" (0)");
		liveButton.setEnabled(false);
		liveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currAdapter != liveAdapter && liveAdapter.getCount() > 0) {
					upcomingButton.setSelected(false);
					liveButton.setSelected(true);
					archivedButton.setSelected(false);
					currAdapter = liveAdapter;
					listView.setAdapter(currAdapter);
				}
			}
		});
		
		archivedButton = (Button) findViewById(R.id.archived);
		archivedButton.setText(getString(R.string.archived)+" (0)");
		archivedButton.setEnabled(false);
		archivedButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currAdapter != archivedAdapter && archivedAdapter.getCount() > 0) {
					upcomingButton.setSelected(false);
					liveButton.setSelected(false);
					archivedButton.setSelected(true);
					currAdapter = archivedAdapter;
					listView.setAdapter(currAdapter);
				}
			}
		});

		final EditText searchEdit = (EditText)findViewById(R.id.mediaSearch);
		TextWatcher watcher = new TextWatcher() {
		    public void afterTextChanged(Editable s) { }
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
		    public void onTextChanged(CharSequence s, int start, int before, int count) {
		    	if(currAdapter != null)
		    		currAdapter.getFilter().filter(s);
		    }
		};
		searchEdit.addTextChangedListener(watcher);
		
		findViewById(R.id.clearSearch).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchEdit.setText("");
			}
		});
    }
    
    private void setupBroadcasts(List<VVCMSBroadcast> broadcasts) {
    	upcomingBroadcasts = new ArrayList<VVCMSBroadcast>();
    	liveBroadcasts = new ArrayList<VVCMSBroadcast>();
    	archivedBroadcasts = new ArrayList<VVCMSBroadcast>();
    	for(VVCMSBroadcast b: broadcasts) {
    		if(b.status == BroadcastStatus.Scheduled)
    			upcomingBroadcasts.add(b);
    		else if(b.status == BroadcastStatus.Streaming)
    			liveBroadcasts.add(b);
    		else
    			archivedBroadcasts.add(b);
    	}

		upcomingAdapter = new BroadcastAdapter(BroadcastsActivity.this, upcomingBroadcasts);
		liveAdapter = new BroadcastAdapter(BroadcastsActivity.this, liveBroadcasts);
		archivedAdapter = new BroadcastAdapter(BroadcastsActivity.this, archivedBroadcasts);
		
		upcomingButton.setText(getString(R.string.upcoming)+" (0)");
		liveButton.setText(getString(R.string.live)+" (0)");
		archivedButton.setText(getString(R.string.archived)+" (0)");
		
		upcomingButton.setText(getString(R.string.upcoming)+" ("+upcomingBroadcasts.size()+")");
		upcomingButton.setEnabled(true);
			
		liveButton.setText(getString(R.string.live)+" ("+liveBroadcasts.size()+")");
		liveButton.setEnabled(true);
		
		archivedButton.setText(getString(R.string.archived)+" ("+archivedBroadcasts.size()+")");
		archivedButton.setEnabled(true);

		upcomingButton.setSelected(false);
		liveButton.setSelected(false);
		archivedButton.setSelected(true);
		currAdapter = archivedAdapter;
    	listView.setAdapter(currAdapter);
    	
        listView.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> a, View v, int pos, long arg3) {
        		VVCMSBroadcast broadcast = (VVCMSBroadcast) currAdapter.getItem(pos);
        		switch(broadcast.status) {
        		case Archived:
        		case Streaming:
	        		String url = broadcast.vmapURL;
	        		Intent intent = new Intent(Globals.VIDEO_PLAY_ACTION);
	        		intent.putExtra(Globals.VIDEO_PLAY_ACTION_EXTRA_URL, url);
	        		startActivity(intent);
        			break;
        		default:
        			Toast.makeText(
        				BroadcastsActivity.this,
        				getString(R.string.invalid_broadcast),
        				Toast.LENGTH_SHORT
        			).show();
        			break;
        		}
        	}
        });
    }

	private void setupImageLoader() {
		File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "iHigh/Cache");

		// Get singletone instance of ImageLoader
		ImageLoader imageLoader = ImageLoader.getInstance();

		// Create default options which will be used for every 
		// displayImage(...) call if no options will be passed to this method
    	DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
    		.cacheInMemory()
	        .cacheOnDisc()
	        .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
	        .build();
    	
		// Create configuration for ImageLoader (all options are optional, use only those you really want to customize)
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.defaultDisplayImageOptions(defaultOptions)
			.threadPoolSize(3)
			.threadPriority(Thread.NORM_PRIORITY - 1)
			.denyCacheImageMultipleSizesInMemory()
			.offOutOfMemoryHandling()
			.memoryCache(new WeakMemoryCache())
			.discCache(new UnlimitedDiscCache(cacheDir)) // This is apparently the fastest
			.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
			.imageDownloader(new URLConnectionImageDownloader(5 * 1000, 20 * 1000)) // connectTimeout (5 s), readTimeout (20 s)
			.build();
		// Initialize ImageLoader with created configuration. Do it once on Application start.
		imageLoader.init(config);
	}


	protected void setCustomTitle(int resID) {
		TextView title = (TextView) getWindow().findViewById(R.id.titleText);
		title.setText(resID);
	}

	protected void setCustomTitle(String str) {
		TextView title = (TextView) getWindow().findViewById(R.id.titleText);
		title.setText(str);
	}

	@Override
	public void domainRequestComplete(VVCMSAPI api, String domain, Exception e) {
		
	}

	@Override
	public void authenticationRequestDidFinish(final VVCMSAPI api, final Exception e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(e != null) {
					if(!isFinishing())
						removeDialog(DIALOG_LOADING);
					Toast.makeText(BroadcastsActivity.this, "Could not connect to domain", Toast.LENGTH_SHORT).show();
					return;
				}
				setCustomTitle(api.siteName());
				if(api.siteName() != null)
					api.requestBroadcastsWithStatus(BroadcastStatus.All, 0, 0);
				else if(!isFinishing())
					removeDialog(DIALOG_LOADING);
			}
		});
	}

	@Override
	public void logoutRequestDidFinish(VVCMSAPI api, Exception e) {
		
	}

	@Override
	public void requestForBroadcastsOfStatusNameComplete(VVCMSAPI api,
			BroadcastStatus status, final List<VVCMSBroadcast> events, Exception e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing())
					removeDialog(DIALOG_LOADING);

				if(events != null)
					setupBroadcasts(events);
			}
		});
	}
}
