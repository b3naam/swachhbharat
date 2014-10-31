package com.mahekmshah.swachhbharat;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class SwachDialog {

	public interface SingleButtonCallback {
		public void positiveActions();
	}

	public interface DoubleButtonCallback {
		public void setPositiveButton();

		public void setNegativeButton();
	}

	public static void singleActionDialogue(final Activity context, String heading, final String msg, final String buttYes, final SingleButtonCallback callback) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_singlebutton);

		TextView title = (TextView) dialog.findViewById(R.id.dialog_single_title);
		TextView message = (TextView) dialog.findViewById(R.id.dialog_single_message);
		TextView positive = (TextView) dialog.findViewById(R.id.dialog_single_positive);

		title.setText(heading);
		message.setText(msg);
		positive.setText(buttYes);

		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (callback != null) {
					callback.positiveActions();
				}
				dialog.cancel();
			}
		});

		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
	}

	public static void doubleActionDialogue(final Activity context, String heading, final String msg, final String buttYes, final String buttNo, final DoubleButtonCallback callback) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_doublebutton);

		TextView title = (TextView) dialog.findViewById(R.id.dialog_double_title);
		TextView message = (TextView) dialog.findViewById(R.id.dialog_double_message);
		TextView positive = (TextView) dialog.findViewById(R.id.dialog_double_positive);
		TextView negative = (TextView) dialog.findViewById(R.id.dialog_double_negative);

		title.setText(heading);
		message.setText(msg);
		positive.setText(buttYes);
		negative.setText(buttNo);

		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (callback != null) {
					callback.setPositiveButton();
				}
				dialog.cancel();
			}
		});

		negative.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (callback != null) {
					callback.setNegativeButton();
				}
				dialog.cancel();

			}
		});

		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
	}

}
