package com.uhungry.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.uhungry.R;

import static com.uhungry.utils.Uhungry.sessionManager;


public class AppUtility {

	public static int LENGTH_LONG = Toast.LENGTH_LONG;

	public static boolean isNetworkAvailable(Context con) {
		ConnectivityManager connectivityManager
				= (ConnectivityManager)con.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static void showToast(Context con, String msg, int length){
		Toast.makeText(con, msg, length).show();
	}

	public static void showAlertDialog_SingleButton(final Activity con, String msg, String title, String ok){

		AlertDialog.Builder builder1 = new AlertDialog.Builder(con);
		builder1.setTitle(title);
		builder1.setMessage(msg);
		builder1.setCancelable(true);
		builder1.setPositiveButton(ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						//con.finish();
					}
				});

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}

	public static  void showDialog(final Context context, String msg, String title, String ok) {
		View DialogView = View.inflate(context, R.layout.dialog_recipe_layout, null);

		final Dialog alertDailog = new Dialog(context, android.R.style.Theme_Light);
		alertDailog.setCanceledOnTouchOutside(false);
		alertDailog.setCancelable(false);

		alertDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		alertDailog.getWindow().getAttributes().windowAnimations = R.style.InOutAnimation;
		alertDailog.setContentView(DialogView);

		Button btnYes = (Button) DialogView.findViewById(R.id.btnYes);
		Button btnNo = (Button) DialogView.findViewById(R.id.btnNo);
		//ImageView ivCross = (ImageView) DialogView.findViewById(R.id.ivCross);
		LinearLayout lyCross = (LinearLayout) DialogView.findViewById(R.id.lyCross);
		TextView tv_msg = (TextView) DialogView.findViewById(R.id.tv_msg);
		TextView tv_title = (TextView) DialogView.findViewById(R.id.tv_title);
		btnNo.setVisibility(View.GONE);
		tv_msg.setText(msg);
		tv_title.setText(title);

		btnYes.setText(ok);

		btnYes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alertDailog.cancel();
			}
		});

		btnNo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alertDailog.cancel();
			}
		});

		lyCross.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alertDailog.cancel();
			}
		});

		alertDailog.show();
	}


	public static void sessionExpire(final Activity con, String msg, String title, String ok){

		AlertDialog.Builder builder1 = new AlertDialog.Builder(con);
		builder1.setTitle(title);
		builder1.setMessage(msg);
		builder1.setCancelable(true);
		builder1.setPositiveButton(ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						sessionManager.logout();

					}
				});

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}


	public interface SubcriptionListner{
		void onSubcribeClick();
	}

	private SubcriptionListner listner;

	public void showSubcriptionDialog(final Context context, SubcriptionListner subcriptionListner) {
		View DialogView = View.inflate(context, R.layout.subcription_dialog_layout, null);
		this.listner = subcriptionListner;
		final Dialog alertDailog = new Dialog(context, android.R.style.Theme_Light);
		alertDailog.setCanceledOnTouchOutside(false);
		alertDailog.setCancelable(false);

		alertDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDailog.setContentView(DialogView);
		alertDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		alertDailog.getWindow().getAttributes().windowAnimations = R.style.InOutAnimation;
		Window compTypeWindow = alertDailog.getWindow();
		compTypeWindow.setGravity(Gravity.CENTER);

		Button btnSubcribe = (Button) DialogView.findViewById(R.id.btnSubcribe);
		LinearLayout lyDialogCross = (LinearLayout) DialogView.findViewById(R.id.lyDialogCross);

		btnSubcribe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDailog.cancel();
				//if(!BillingProcessor.isIabServiceAvailable(context)) {
					//showToast(context,"In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16",0);
				//}else if(listner!=null)
					listner.onSubcribeClick();

			}
		});

		lyDialogCross.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDailog.cancel();
			}
		});
		alertDailog.show();
	}


	public static void closeKeyboard(Context c, IBinder windowToken) {
		InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(windowToken, 0);
	}

}