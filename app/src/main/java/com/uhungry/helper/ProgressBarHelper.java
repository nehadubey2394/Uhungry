package com.uhungry.helper;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.uhungry.R;

public class ProgressBarHelper {

	private View progressBarView;

	public ProgressBarHelper(Activity activity) {
		progressBarView = LayoutInflater.from(activity).inflate(R.layout.stay_on_top_progress_bar, null);
		activity.addContentView(progressBarView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	public ProgressBarHelper setText(int resourceId) {
		((TextView) progressBarView.findViewById(R.id.progress_bar_text)).setText(resourceId);
		return this;
	}

	public ProgressBarHelper setText(String text) {
		((TextView) progressBarView.findViewById(R.id.progress_bar_text)).setText(text);
		return this;
	}

	public void show() {
		progressBarView.setVisibility(View.VISIBLE);
	}

	public void hide() {
		progressBarView.setVisibility(View.GONE);
	}

	public View getProgressBarView() {
		return progressBarView;
	}

	public void setProgressBarView(View progressBarView) {
		this.progressBarView = progressBarView;
	}
}
