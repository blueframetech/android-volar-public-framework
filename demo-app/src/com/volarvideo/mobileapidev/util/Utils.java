package com.volarvideo.mobileapidev.util;

import java.io.File;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class Utils {
	public static void setupImageLoader(Context ctx) {

		File cacheDir = StorageUtils.getOwnCacheDirectory(ctx, "Volar/Cache");
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
    	
		// Create configuration for ImageLoader
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx)
			.defaultDisplayImageOptions(options)
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.diskCache(new UnlimitedDiscCache(cacheDir))
			.denyCacheImageMultipleSizesInMemory()
			.diskCacheFileNameGenerator(new Md5FileNameGenerator())
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.build();
		// Initialize ImageLoader with created configuration. Do it once on Application start.
		ImageLoader.getInstance().init(config);
	}
	
	public static boolean onSameDate(Calendar c1, Calendar c2) {
		if(c1 != null && c2 != null) {
			if(
				c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
				c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH) &&
				c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
			)
				return true;
		}
		return false;
	}
}
