package com.volarvideo.mobileapidev;

import java.io.File;
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
import com.volarvideo.mobileapidev.util.EndlessScrollListener;
import com.volarvideo.mobilesdk.api.VVCMSAPI;
import com.volarvideo.mobilesdk.api.VVCMSAPIDelegate;
import com.volarvideo.mobilesdk.models.VVCMSBroadcast;
import com.volarvideo.mobilesdk.models.VVCMSBroadcast.BroadcastStatus;
import com.volarvideo.mobilesdk.util.Log;

@SuppressWarnings("deprecation")
public class BroadcastsActivity extends VolarActivity implements VVCMSAPIDelegate {

	public static final String VolarContentDomain = "vcloud.volarvideo.com";

	private VVCMSAPI api = new VVCMSAPI();
	private Button upcomingButton, liveButton, archivedButton;
	private ListView listView;
	private View listFooter;
	private EditText searchEdit;
	private BroadcastAdapter adapter;
	private final int RESULTS_PER_PAGE = 50;
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
				if(api.getCurrentSite() != null) {
	        		showDialog(DIALOG_LOADING);
	        		getData(1);
				}
			}
		});
	}

    private void setupViews() {
		listView = (ListView) findViewById(R.id.mediaList);
		View tmpView = getLayoutInflater().inflate(R.layout.media_loading, null);
		listFooter = tmpView.findViewById(R.id.progress);
		listView.addFooterView(tmpView);
		
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
				if(!upcomingButton.isSelected()) {
					upcomingButton.setSelected(true);
					liveButton.setSelected(false);
					archivedButton.setSelected(false);
		    		showDialog(DIALOG_LOADING);
					getData(1);
				}
			}
		});
		
		liveButton = (Button) findViewById(R.id.live);
		liveButton.setText(getString(R.string.live)+" (0)");
		liveButton.setEnabled(false);
		liveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!liveButton.isSelected()) {
					upcomingButton.setSelected(false);
					liveButton.setSelected(true);
					archivedButton.setSelected(false);
		    		showDialog(DIALOG_LOADING);
					getData(1);
				}
			}
		});
		
		archivedButton = (Button) findViewById(R.id.archived);
		archivedButton.setText(getString(R.string.archived)+" (0)");
		archivedButton.setEnabled(false);
		archivedButton.setSelected(true);
		archivedButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!archivedButton.isSelected()) {
					upcomingButton.setSelected(false);
					liveButton.setSelected(false);
					archivedButton.setSelected(true);
		    		showDialog(DIALOG_LOADING);
					getData(1);
				}
			}
		});

		searchEdit = (EditText)findViewById(R.id.mediaSearch);
		TextWatcher watcher = new TextWatcher() {
		    public void afterTextChanged(Editable s) { }
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
		    public void onTextChanged(CharSequence s, int start, int before, int count) {
		    	if(adapter != null)
		    		adapter.getFilter().filter(s);
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
    
    private void getData(int page) {
		BroadcastStatus status = null;
		if(upcomingButton.isSelected())
			status = BroadcastStatus.Scheduled;
		else if(liveButton.isSelected())
			status = BroadcastStatus.Streaming;
		else
			status = BroadcastStatus.Archived;
		getData(status, page);
    }
    
    private void getData(BroadcastStatus status, int page) {
		api.requestBroadcastsWithStatus(status, page, RESULTS_PER_PAGE);
    }

    
    private void setupBroadcasts(
    	final BroadcastStatus status, List<VVCMSBroadcast> b, int page, final int totalPages, int totalResults
    ) {
    	if(page == 1) {
    		adapter = new BroadcastAdapter(BroadcastsActivity.this, b);
        	listView.setAdapter(adapter);	
    		listView.setOnScrollListener(new EndlessScrollListener(RESULTS_PER_PAGE) {
    			@Override
    			public void onLoadMore(int nextPage, int totalItemsCount) {
    				if(nextPage <= totalPages) {
    					listFooter.setVisibility(View.VISIBLE);
    					getData(status, nextPage);
    				}
    			}
    		});
    	}
    	else {
			listFooter.setVisibility(View.GONE);
    		adapter.addItems(b);
    		adapter.getFilter().filter(searchEdit.getText()+"");
    	}

		
		upcomingButton.setText(getString(R.string.upcoming));
		liveButton.setText(getString(R.string.live));
		archivedButton.setText(getString(R.string.archived));
		switch(status) {
		case Scheduled:
			upcomingButton.setText(upcomingButton.getText()+" ("+totalResults+")");
			break;
		case Stopped:
		case Streaming:
			liveButton.setText(liveButton.getText()+" ("+totalResults+")");
			break;
		case Archived:
			archivedButton.setText(archivedButton.getText()+" ("+totalResults+")");
			break;
		}

		upcomingButton.setEnabled(true);
		liveButton.setEnabled(true);
		archivedButton.setEnabled(true);
    	
        listView.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> a, View v, int pos, long arg3) {
        		VVCMSBroadcast broadcast = (VVCMSBroadcast) adapter.getItem(pos);
        		switch(broadcast.status) {
        		case Archived:
        		case Streaming:
        		case Stopped:
	        		String url = broadcast.vmapURL;
	        		Intent intent = new Intent(Globals.VIDEO_PLAY_ACTION);
	        		intent.putExtra(Globals.VIDEO_PLAY_ACTION_EXTRA_URL, url);
		       		// String encryptionKey = cursor.getString(Globals.PROJECTION_ENCRYPTION_KEY);
		       		// if (encryptionKey != null) {
		       		// 	intent.putExtra(Globals.VIDEO_PLAY_ACTION_EXTRA_ENCRYPTION_KEY, encryptionKey);
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
	public void domainRequestComplete(VVCMSAPI api, String domain, Exception e) { }

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
				setCustomTitle(api.getCurrentSite().title);
				getData(1);
			}
		});
	}

	@Override
	public void logoutRequestDidFinish(VVCMSAPI api, Exception e) { }

	@Override
	public void requestForBroadcastsOfStatusNameComplete(VVCMSAPI api,
		final BroadcastStatus status, final int page,
		final int totalPages, final int totalResults,
		final List<VVCMSBroadcast> events, Exception e
	) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing())
					removeDialog(DIALOG_LOADING);

				if(events != null)
					setupBroadcasts(status, events, page, totalPages, totalResults);
			}
		});
	}
}
