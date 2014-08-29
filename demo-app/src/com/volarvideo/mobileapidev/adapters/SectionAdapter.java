package com.volarvideo.mobileapidev.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.volarvideo.mobilesdk.models.VVCMSSection;

public class SectionAdapter extends BaseAdapter {
	
	private Context context;
	private List<VVCMSSection> sections;
	
	public SectionAdapter(Context ctx, List<VVCMSSection> s) {
		context = ctx;
		sections = s; 
	}

	@Override
	public int getCount() {
		return sections.size() + 1;
	}
	
	public void addItems(List<VVCMSSection> b) {
		sections.addAll(b);
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int pos) {
		if(pos == 0)
			return null;
		return sections.get(pos-1);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View recycledView, ViewGroup container) {
		View v = recycledView;
		if(v == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        	v = inflater.inflate(android.R.layout.simple_list_item_1, null);
		}
		
		if(pos == 0)
			((TextView)v.findViewById(android.R.id.text1)).setText("- All -");
		else {
			VVCMSSection section = (VVCMSSection) getItem(pos);
			((TextView)v.findViewById(android.R.id.text1)).setText(section.title);
		}
		
		return v;
	}
}
