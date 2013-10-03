package com.volarvideo.demoapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;

public class VolarActivity extends Activity {

	/* Dialogs */
	public static final int DIALOG_LOADING = 0;
	
	@Override
    protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);
    	((ProgressDialog) dialog).setProgressStyle(ProgressDialog.STYLE_SPINNER);
        switch (id) {
        case DIALOG_LOADING:
        default:
        	dialog.setMessage(getString(R.string.loading));
        	break;
        }
    	return dialog;
	}
}
