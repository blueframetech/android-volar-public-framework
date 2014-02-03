package com.volarvideo.mobileapidev.views;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.volarvideo.mobileapidev.R;
import com.volarvideo.mobileapidev.util.Conversions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;

/**
 * Displays bitmap with rounded corners and play button
 * 
 */
public class VideoThumbDisplayer implements BitmapDisplayer {

	private int roundPixels;
	private int HEIGHT = 65;
	private int BUTTON_WIDTH = 30;
	private int BUTTON_HEIGHT = 21;
	private Bitmap playButton;

	public VideoThumbDisplayer(Context ctx, int roundPixels) {
		this.roundPixels = (int) Conversions.pixelsToDp(ctx, roundPixels);

		HEIGHT = (int) Conversions.pixelsToDp(ctx, HEIGHT);
		BUTTON_WIDTH = (int) Conversions.pixelsToDp(ctx, BUTTON_WIDTH);
		BUTTON_HEIGHT = (int) Conversions.pixelsToDp(ctx, BUTTON_HEIGHT);
		
		Options opts = new Options();
		opts.outWidth = BUTTON_WIDTH;
		opts.outHeight = BUTTON_HEIGHT;
		InputStream is = ctx.getResources().openRawResource(R.drawable.play_video);
		playButton = BitmapFactory.decodeStream(is, null, opts);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Bitmap display(Bitmap bitmap, ImageView imageView) {		
		Bitmap roundedBitmap;
		try {
			roundedBitmap = getRoundedBitmap(bitmap);
		} catch (OutOfMemoryError e) {
			Log.e(ImageLoader.TAG, "Can't create bitmap with rounded corners. Not enough memory.", e);
			roundedBitmap = bitmap;
		}
		imageView.setBackgroundDrawable(new BitmapDrawable(roundedBitmap));
		imageView.setImageBitmap(playButton);
		
		return playButton;
	}

	public void initLayoutParams(ImageView imageView) {
		LayoutParams params = imageView.getLayoutParams();
		params.height = HEIGHT;
		imageView.setLayoutParams(params);
		imageView.setPadding(0, 0, 0, 0);
		imageView.setScaleType(ScaleType.CENTER_INSIDE);
	}

	private Bitmap getRoundedBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xFFFFFFFF);
		canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
}