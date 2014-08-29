package com.volarvideo.mobileapidev;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.volarvideo.mobileapidev.adapters.BroadcastAdapter;
import com.volarvideo.mobileapidev.adapters.SectionAdapter;
import com.volarvideo.mobileapidev.util.Conversions;
import com.volarvideo.mobileapidev.util.EndlessScrollListener;
import com.volarvideo.mobileapidev.util.Utils;
import com.volarvideo.mobilesdk.api.VVCMSAPI;
import com.volarvideo.mobilesdk.api.VVCMSAPIDelegate;
import com.volarvideo.mobilesdk.models.VVCMSBroadcast;
import com.volarvideo.mobilesdk.models.VVCMSBroadcast.BroadcastParams;
import com.volarvideo.mobilesdk.models.VVCMSBroadcast.BroadcastStatus;
import com.volarvideo.mobilesdk.models.VVCMSClip;
import com.volarvideo.mobilesdk.models.VVCMSSection;
import com.volarvideo.mobilesdk.models.VVCMSSection.SectionParams;
import com.volarvideo.mobilesdk.models.VVCMSSite;
import com.volarvideo.mobilesdk.util.VVUtils;

@SuppressWarnings("deprecation")
public class MediaListActivity extends Activity implements VVCMSAPIDelegate {

	public static final String DOMAIN = "vcloud.volarvideo.com";
	private static final String API_KEY = "<insert api key>";

	private VVCMSAPI api;
	private List<VVCMSSection> sections = new ArrayList<VVCMSSection>();
	private TextView upcomingButton, liveButton, archivedButton;
	private TextView fromDateButton, toDateButton, sectionButton;
	private Calendar fromDate, toDate;
	private boolean dateSetShouldFire = true;
	private VVCMSSection section = null;
	private ListView listView;
	private View listFooter;
	private EditText searchEdit;
	private BroadcastAdapter adapter;
	private final int RESULTS_PER_PAGE = 25;
	protected int FOOTER_HEIGHT = 90; // dp

	// Dialogs
	public static final int DIALOG_LOADING = 0;

	// Handler to delay searching
	private int searchCount = 0;
	private SearchHandler handler = new SearchHandler(this);
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.media_list);

        Utils.setupImageLoader(getApplicationContext());
		setupViews();

		api = new VVCMSAPI(DOMAIN, API_KEY);
//		api = new VVCMSAPI(DOMAIN, "john@test.com", "password");

		showDialog(DIALOG_LOADING);
		getSections(1);
		getBroadcasts(1);
	}
	
	private void refresh() {
		showDialog(DIALOG_LOADING);
		getBroadcasts(1);
	}

    @SuppressLint("InflateParams")
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
		
		upcomingButton = (TextView) findViewById(R.id.upcoming);
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
		    		getBroadcasts(1);
				}
			}
		});
		
		liveButton = (TextView) findViewById(R.id.live);
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
		    		getBroadcasts(1);
				}
			}
		});
		
		archivedButton = (TextView) findViewById(R.id.archived);
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
		    		getBroadcasts(1);
				}
			}
		});

		fromDateButton = (TextView) findViewById(R.id.from_date);
		fromDateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!fromDateButton.isSelected()) {
        			fromDateButton.setSelected(true);
        			dateSetShouldFire = true;
        			
		        	Calendar c = fromDate;
		        	if(c == null)
		        		c = Calendar.getInstance();

		        	DatePickerDialog dateDialog = new DatePickerDialog(MediaListActivity.this, new OnDateSetListener() {
			        	@Override
		        		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			        		Calendar newDate = Calendar.getInstance();
			        		newDate.set(year, monthOfYear, dayOfMonth);
			        		if(
			        			dateSetShouldFire &&
			        			(
				        			(fromDate != null && !Utils.onSameDate(newDate, fromDate)) ||
			        				(fromDate == null && !Utils.onSameDate(newDate, Calendar.getInstance()))
			        			)
			        		) {
				        		dateSetShouldFire = false;
				        		fromDate = newDate;
				        		fromDateButton.setText(String.format("%d/%d/%d", monthOfYear+1, dayOfMonth, year));
				        		refresh();
			        		}
		        		}	
		        	}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
		        	dateDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Clear", new DialogInterface.OnClickListener() {
		        		@Override
		        		public void onClick(DialogInterface dialog, int which) {
		        			dateSetShouldFire = false;
		        			if(fromDate != null) {
			        			fromDateButton.setText("From Date");
			        			fromDate = null;
				        		refresh();
		        			}
		        		}
		        	});
		        	dateDialog.setOnDismissListener(new OnDismissListener() {
		        		@Override
		        		public void onDismiss(DialogInterface dialog) {
		        			fromDateButton.setSelected(false);
		        		}
		        	});
		        	dateDialog.show();
				}
			}
		});
		
		toDateButton = (TextView) findViewById(R.id.to_date);
		toDateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!toDateButton.isSelected()) {
					toDateButton.setSelected(true);
        			dateSetShouldFire = true;
					
		        	Calendar c = toDate;
		        	if(c == null)
		        		c = Calendar.getInstance();
		        	DatePickerDialog dateDialog = new DatePickerDialog(MediaListActivity.this, new OnDateSetListener() {
			        	@Override
		        		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			        		Calendar newDate = Calendar.getInstance();
			        		newDate.set(year, monthOfYear, dayOfMonth);
			        		if(
			        			dateSetShouldFire &&
			        			(
				        			(toDate != null && !Utils.onSameDate(newDate, toDate)) ||
			        				(toDate == null && !Utils.onSameDate(newDate, Calendar.getInstance()))
			        			)
			        		) {
				        		dateSetShouldFire = false;
				        		toDate = newDate;
				        		toDateButton.setText(String.format("%d/%d/%d", monthOfYear+1, dayOfMonth, year));
				        		refresh();
			        		}
		        		}	
		        	}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
		        	dateDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Clear", new DialogInterface.OnClickListener() {
		        		@Override
		        		public void onClick(DialogInterface dialog, int which) {
		        			dateSetShouldFire = false;
		        			if(toDate != null) {
			        			toDate = null;
			        			toDateButton.setText("To Date");
				        		refresh();
		        			}
		        		}
		        	});
		        	dateDialog.setOnDismissListener(new OnDismissListener() {
		        		@Override
		        		public void onDismiss(DialogInterface dialog) {
		        			toDateButton.setSelected(false);
		        		}
		        	});
		        	dateDialog.show();
				}
			}
		});
		
		sectionButton = (TextView) findViewById(R.id.section); 
		sectionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!sectionButton.isSelected()) {
					sectionButton.setSelected(true);
					
					Context ctx = MediaListActivity.this;
					ListView listView = new ListView(ctx);
					listView.setAdapter(new SectionAdapter(ctx, sections));
					final AlertDialog dialog = new AlertDialog.Builder(ctx)
						.setTitle("Section")
						.setView(listView)
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// noop
							}
						})
						.create();
					listView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> a, View v, int pos, long id) {
							if(pos > 0) {
								section = sections.get(pos-1);
								sectionButton.setText(section.title);
							}
							else {
								section = null;
								sectionButton.setText("Section");
							}
							dialog.dismiss();
							refresh();
						}
					});
					dialog.setOnDismissListener(new OnDismissListener() {
		        		@Override
		        		public void onDismiss(DialogInterface dialog) {
							sectionButton.setSelected(false);
		        		}
		        	});
					dialog.show();
				}
			}
		});

		searchEdit = (EditText)findViewById(R.id.mediaSearch);
		TextWatcher watcher = new TextWatcher() {
		    public void afterTextChanged(Editable s) { }
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
		    public void onTextChanged(CharSequence s, int start, int before, int count) {
		    	searchCount++;
		    	handler.sendMessageDelayed(handler.obtainMessage(searchCount), 1000);
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
    
    private void getBroadcasts(int page) {
		BroadcastStatus status = null;
		if(upcomingButton.isSelected())
			status = BroadcastStatus.Scheduled;
		else if(liveButton.isSelected())
			status = BroadcastStatus.Streaming;
		else
			status = BroadcastStatus.Archived;
		getBroadcasts(status, page);
    }
    
    private void getBroadcasts(BroadcastStatus status, int page) {
    	BroadcastParams params = new BroadcastParams()
			.status(status)
			.page(page)
			.resultsPerPage(RESULTS_PER_PAGE);

    	String t = searchEdit.getText()+"";
    	if(!VVUtils.isEmpty(t))
    		params.title(t);
    	
    	if(section != null) {
    		params.sectionID(section.id);
    	}
    	
    	if(fromDate != null)
    		params.start(fromDate);

    	if(toDate != null)
    		params.end(toDate);
    	
		api.requestBroadcasts(this, params);
    }
    
    private void getSections(int page) {
    	SectionParams params = new SectionParams()
			.page(page)
			.resultsPerPage(RESULTS_PER_PAGE);

    	api.requestSections(this, params);
    }

    
    private void setupBroadcasts(
    	final BroadcastStatus status, List<VVCMSBroadcast> b, int page, final int totalPages, int totalResults
    ) {
    	if(page == 1) {
    		adapter = new BroadcastAdapter(MediaListActivity.this, b);
        	listView.setAdapter(adapter);	
    		listView.setOnScrollListener(new EndlessScrollListener(RESULTS_PER_PAGE) {
    			@Override
    			public void onLoadMore(int nextPage, int totalItemsCount) {
    				if(nextPage <= totalPages) {
    					listFooter.setVisibility(View.VISIBLE);
    					getBroadcasts(status, nextPage);
    				}
    				else
    					listFooter.setVisibility(View.GONE);
    			}
    		});
    	}
    	else {
			listFooter.setVisibility(View.GONE);
    		adapter.addItems(b);
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
		default:
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
        		case Test:
        		case Stopped:
	        		String url = broadcast.vmapURL;
	        		Intent intent = new Intent(MediaListActivity.this, VideoActivity.class);
	        		intent.putExtra(Globals.VIDEO_PLAY_ACTION_EXTRA_URL, url);
	        		startActivity(intent);
        			break;
        		default:
        			Toast.makeText(
        				MediaListActivity.this,
        				getString(R.string.invalid_broadcast),
        				Toast.LENGTH_SHORT
        			).show();
        			break;
        		}
        	}
        });
    }
	
	@Override
	public void checkCredentialsComplete(VVCMSAPI api, final Exception e) { }

	@Override
	public void requestForBroadcastsComplete(VVCMSAPI api,
		final BroadcastStatus status, final int page,
		final int totalPages, final int totalResults,
		final List<VVCMSBroadcast> events, final Exception e
	) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing())
					removeDialog(DIALOG_LOADING);

				if(e != null) {
					Toast.makeText(MediaListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					setupBroadcasts(status, new ArrayList<VVCMSBroadcast>(), page, totalPages, totalResults);
					return;
				}
					
				if(events != null)
					setupBroadcasts(status, events, page, totalPages, totalResults);
			}
		});
	}

	@Override
	public void requestForSectionsComplete(
		VVCMSAPI api, final int page, final int totalPages, final int totalResults,
		final List<VVCMSSection> results, final Exception e
	) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(e != null)
					return;

				if(page == 1)
					sections.clear();
				
				if(results != null)
					sections.addAll(results);
				
				if(page+1 <= totalPages)
					getSections(page+1);
			}
		});
	}

	@Override
	public void requestForClipsComplete(
		VVCMSAPI api, int page, int totalPages, int totalResults,
		List<VVCMSClip> clips, Exception e
	) { }

	@Override
	public void requestForSitesComplete(
		VVCMSAPI api, int page, int totalPages, int totalResults,
		List<VVCMSSite> sites, Exception e
	) { }

	@Override
	protected void onPause() {
		super.onPause();

		if(isFinishing())
			api.shutdown();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {

	    case android.R.id.home:
	        finish();
	        return true;
	    case R.id.action_refresh:
	    	refresh();
	    	return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.title_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
    protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);
    	((ProgressDialog) dialog).setProgressStyle(ProgressDialog.STYLE_SPINNER);
        switch (id) {
        case DIALOG_LOADING:
        default:
    		dialog.setCancelable(false);
        	dialog.setMessage(getString(R.string.loading));
        	break;
        }
    	return dialog;
	}
	
	// Handler for search delay
	static class SearchHandler extends Handler {
		private final WeakReference<MediaListActivity> mActivity;
		
		public SearchHandler(MediaListActivity a) {
			mActivity = new WeakReference<MediaListActivity>(a);
		}
		
		@Override
	    public void handleMessage(Message msg) {
			MediaListActivity a = mActivity.get();
			
	        if(a != null && msg.what == a.searchCount) {
	            a.getBroadcasts(1);
	        }
	    }
	}
}
