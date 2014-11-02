package com.mahekmshah.swachhbharat.reusable;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class SwachIconFont extends TextView {
	private Typeface mTypeface = null;

	public SwachIconFont(Context context) {
		super(context);
		setFont(context);
	}

	public SwachIconFont(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFont(context);
	}

	public SwachIconFont(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setFont(context);
	}

	private void setFont(Context context) {
		mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/<font-file>.ttf");
		this.setTypeface(mTypeface);
	}
}
