package com.volarvideo.demoapp;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.volarvideo.demoapp.models.Broadcast;
import com.volarvideo.demoapp.models.Broadcast.BroadcastStatus;
import com.volarvideo.demoapp.util.LocalStorageHelper;
import com.volarvideo.demoapp.util.VVCMSAPI;
import com.volarvideo.demoapp.util.VVCMSAPIDelegate;

public class DomainActivity extends VolarActivity implements VVCMSAPIDelegate {

	private VVCMSAPI api = new VVCMSAPI();
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private List<String> domainList = new ArrayList<String>();
	private int currDomain = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.domain_list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		
		// Fix phones that have trouble drawing gradients
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		
		setCustomTitle(R.string.change_domain);
		
		api.setDelegate(this);
		
		
		domainList = LocalStorageHelper.getDomains(this);
		currDomain = domainList.indexOf(LocalStorageHelper.getCurrDomain(this));
		
		setupViews();

		listView.setItemChecked(currDomain, true);
    }
    
    private void setupViews() {
		listView = (ListView) findViewById(R.id.domainList);
		adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, domainList);
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setItemChecked(currDomain, true);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long arg3) {
				int currDomain = listView.getCheckedItemPosition();
				LocalStorageHelper.saveCurrDomain(DomainActivity.this, domainList.get(currDomain));
				
				Intent retIntent = new Intent();
				retIntent.putExtra("domain", domainList.get(currDomain));
				DomainActivity.this.setResult(RESULT_OK, retIntent);
				finish();
			}
		});
		
		findViewById(R.id.addDomain).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				View addDomainDialog = LayoutInflater.from(DomainActivity.this)
						.inflate(R.layout.dialog_add_domain, null);
				final EditText domainInput = (EditText) addDomainDialog.findViewById(R.id.addDomain);
				
				new AlertDialog.Builder(DomainActivity.this)
					.setTitle(R.string.title_add_domain)
					.setView(addDomainDialog)
					.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							InputMethodManager imm = (InputMethodManager)getSystemService(
								      Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(domainInput.getWindowToken(), 0);
							showDialog(DIALOG_LOADING);
							api.requestDomain(domainInput.getText()+"");
						}
					})
					.show();
			}
		});
    }

	@Override
	public void domainRequestComplete(VVCMSAPI api, final String domain, final Exception e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing())
					dismissDialog(DIALOG_LOADING);
				
				if(e != null)
					Toast.makeText(DomainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
				else if(domainList.contains(domain))
					Toast.makeText(DomainActivity.this, "Domain already exists!", Toast.LENGTH_SHORT).show();
				else {
					domainList.add(domain);
					adapter.notifyDataSetChanged();
					
					LocalStorageHelper.saveDomains(DomainActivity.this, domainList);
				}
			}
		});
	}

	@Override
	public void authenticationRequestDidFinish(VVCMSAPI api, Exception e) { }

	@Override
	public void logoutRequestDidFinish(VVCMSAPI api, Exception e) { }

	@Override
	public void requestForUserNameComplete(VVCMSAPI api, String userName,
			Exception e) { }

	@Override
	public void requestForBroadcastsOfStatusNameComplete(VVCMSAPI api,
			BroadcastStatus status, List<Broadcast> events, Exception e) { }

	protected void setCustomTitle(int resID) {
		TextView title = (TextView) getWindow().findViewById(R.id.titleText);
		title.setText(resID);
	}
}
