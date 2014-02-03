package com.volarvideo.mobileapidev;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.volarvideo.mobilesdk.LibsChecker;
import com.volarvideo.mobilesdk.vplayer.VVPlayerView;
import com.volarvideo.mobilesdk.vplayer.VolarPlayerController;

/**
 * {@link VVPlayerView}
 * @author user
 *
 */
public class VideoActivity extends Activity {
	
	private VolarPlayerController player;
	private VVPlayerView playerView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DITHER);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		getWindow().setBackgroundDrawable(null);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		if(!LibsChecker.checkVolarLibs(this)) {
			return;
		}
		
		setContentView(R.layout.video_activity);
        
        playerView = (VVPlayerView) findViewById(R.id.playerView);
        
		setupPlayer();
	}


	private void setupPlayer() {
		Intent intent = getIntent();
		Uri uri = intent.getData();
		String vmapURL;
		if (uri != null) {
			vmapURL = uri.toString();
		} else {
			vmapURL = intent.getStringExtra(Globals.VIDEO_PLAY_ACTION_EXTRA_URL);
			if (vmapURL == null) {
				throw new IllegalArgumentException(String.format(
						"\"%s\" did not provided",
						Globals.VIDEO_PLAY_ACTION_EXTRA_URL));
			}
		}
		
		player = new VolarPlayerController.Builder(this)
			.setVmapURI(vmapURL)
			.setPlayerView(playerView)
			.load();
		if(player == null)
			finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.video_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.toggle_qos:
			playerView.toggleQosOverlay();
			return true;
		case R.id.exit:
			finish();
			return true;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(player != null)
			player.shutdown();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
