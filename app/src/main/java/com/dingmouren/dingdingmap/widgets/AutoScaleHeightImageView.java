package com.dingmouren.dingdingmap.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AutoScaleHeightImageView extends ImageView {

	public AutoScaleHeightImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		 Drawable drawable = getDrawable();
		 if(drawable != null){
			 int width = drawable.getMinimumWidth();
			 int height = drawable.getMinimumHeight();
			 float scale = (float)height/width;

			 int widthMeasure = MeasureSpec.getSize(widthMeasureSpec);
			 int heightMeasure = (int)(widthMeasure*scale);

			 heightMeasureSpec =  MeasureSpec.makeMeasureSpec(heightMeasure, MeasureSpec.EXACTLY);
		 }
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
