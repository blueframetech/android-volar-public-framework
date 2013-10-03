package com.volarvideo.demoapp.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.volarvideo.demoapp.R;
import com.volarvideo.demoapp.models.Broadcast;
import com.volarvideo.demoapp.util.Conversions;
import com.volarvideo.demoapp.views.VideoThumbDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;


public class BroadcastAdapter extends BaseAdapter {
	
	private Context context;
	private List<Broadcast> visibleItems;
	private List<Broadcast> items = new ArrayList<Broadcast>();
	private List<Broadcast> filtered = new ArrayList<Broadcast>();
	private Filter filter;
	
	private int DOCS_HEIGHT = 60;
	private int NEWS_HEIGHT = 60;
	
	private DisplayImageOptions thumbOptions;
	private VideoThumbDisplayer displayer;

    public BroadcastAdapter(Context ctx, List<Broadcast> f) {
    	context = ctx;
		visibleItems = f;
		items.addAll(f);
        
        init();
    }
    
    private void init() {
    	DOCS_HEIGHT = (int) Conversions.pixelsToDp(context, DOCS_HEIGHT);
    	NEWS_HEIGHT = (int) Conversions.pixelsToDp(context, NEWS_HEIGHT);
        

    	displayer = new VideoThumbDisplayer(context, 10);
    	thumbOptions = new DisplayImageOptions.Builder()
    		.cacheInMemory()
            .cacheOnDisc()
	        .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .showStubImage(R.drawable.icon_generic_video)
            .showImageForEmptyUri(R.drawable.icon_generic_video)
            .displayer(displayer)
            .build();
    }

    public int getCount() {
    	if(visibleItems != null)
    		return visibleItems.size();
    	return 0;
    }

    public Filter getFilter() {
		if(filter == null)
			filter = new BroadcastFilter();
        return filter;
    }

    public Object getItem(int position) {
    	if(visibleItems != null)
    		return visibleItems.get(position);
    	else
    		return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FavoriteHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.media_list_item, null);
            
            holder = new FavoriteHolder();
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.timestamp = (TextView) row.findViewById(R.id.timestamp);
            holder.thumb = (ImageView) row.findViewById(R.id.thumb);
            
            row.setTag(holder);
        }
        else
            holder = (FavoriteHolder)row.getTag();
        
        final Broadcast broadcast = visibleItems.get(position);

        holder.title.setText(Html.fromHtml(broadcast.title));
        if(broadcast.startDate != null)
        	holder.timestamp.setText(new SimpleDateFormat("MM-d-yy").format(broadcast.startDate));
        else
        	holder.timestamp.setText("");

    	holder.thumb.setScaleType(ScaleType.FIT_CENTER);
    	holder.thumb.setBackgroundDrawable(null);
//        if(broadcast.getAudioOnly())
//    		holder.thumb.setImageResource(R.drawable.icon_generic_audio);
//        else if(broadcast.getStatus().equalsIgnoreCase("i")) {
//        	if(broadcast.getOnCalendar() != null && broadcast.getOnCalendar())
//        		holder.thumb.setImageResource(R.drawable.alarm);
//        	else
//        		holder.thumb.setImageResource(R.drawable.icon_generic_broadcast);
//        }
//        else {
    		displayer.initLayoutParams(holder.thumb);
        	try {
    	        ImageLoader.getInstance().displayImage(broadcast.thumbnailURL, 
    	        	holder.thumb, thumbOptions);
        	} catch(OutOfMemoryError e) {
        		e.printStackTrace();
        	}
//        }

        return row;
    }

    private class FavoriteHolder {
    	ImageView thumb;
    	TextView title;
    	TextView timestamp;
    }

    private class BroadcastFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {	        	
            // NOTE: this function is *always* called from a background thread, and
            // not the UI thread.
        	constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            filtered.clear();
            if(constraint != null && constraint.length() > 0) {
                for(Broadcast broadcast: items) {
                	if(broadcast.title == null) broadcast.title = "";
                    if(broadcast.title.toLowerCase().contains(constraint)
                    /*|| broadcast.something.toLowerCase().contains(constraint)*/) {
                    	filtered.add(broadcast);
                    }
                }
                result.count = filtered.size();
                result.values = filtered;
            }
            else {
            	result.count = items.size();
            	result.values = items;
            }
            return result;
        }

        @SuppressWarnings("unchecked")
		@Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
            visibleItems.clear();
            visibleItems.addAll((List<Broadcast>)results.values);
            notifyDataSetChanged();
        }
    }
}