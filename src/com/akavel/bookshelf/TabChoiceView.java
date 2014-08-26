package com.akavel.bookshelf;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TabChoiceView extends View {
	private static final int PICW = 80;
	private static final int BORDERW = 20;
	private static final int PICH = 80;
	private static final float STROKEWIDTH = 3;
	
	public int currentTab = 0;

	public TabChoiceView(Context context) {
		super(context);
	}
	
	public TabChoiceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int w = MeasureSpec.getSize(widthMeasureSpec);
		switch (MeasureSpec.getMode(widthMeasureSpec)) {
		case MeasureSpec.UNSPECIFIED:
			w = 5*PICW+6*BORDERW;
			break;
		}
		
		int h = MeasureSpec.getSize(heightMeasureSpec);
		switch (MeasureSpec.getMode(heightMeasureSpec)) {
		case MeasureSpec.UNSPECIFIED:
		case MeasureSpec.AT_MOST:
			h = PICH;
			break;
		}
		
		setMeasuredDimension(w, h);
	}
	
	int w, h;
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		this.w = w;
		this.h = h;
		prepareDrawing();
	};
	
	Paint stroke = new Paint();
	Paint fill = new Paint();
	Path tabs[] = new Path[5];
	Path icons[] = new Path[5];
	float wUnit;
	
	private void prepareDrawing() {
		wUnit = ((float)w)/((float)5*PICW+6*BORDERW); //FIXME: do we need the casts here?
		Path tab = new Path();
		tab.moveTo(0, h-STROKEWIDTH/2);
		tab.lineTo(wUnit*BORDERW, STROKEWIDTH/2);
		tab.lineTo(wUnit*(BORDERW+PICW), STROKEWIDTH/2);
		tab.lineTo(wUnit*(BORDERW+PICW+BORDERW), h-STROKEWIDTH/2);
		for (int i=0; i<tabs.length; i++) {
			tabs[i] = new Path();
			tabs[i].addPath(tab, ((float)i)*wUnit*(BORDERW+PICW), 0);
			icons[i] = new Path();
		}
		
		float arm = (float) (Math.min(h, wUnit*PICW)*0.7/2);
		float midy = h/2;
		
		// icon 1/5: star
		float midx = wUnit*(BORDERW+PICW/2);
		{
			final int ARMS = 6;
			for (int i=0; i<ARMS; i++) {
				float angle = (float) (i*Math.PI*2/ARMS);
				icons[0].moveTo(midx, midy);
				icons[0].rLineTo(arm*(float)Math.cos(angle), arm*(float)Math.sin(angle));
				//icons[0].lineTo(midx+arm*Math.cos(angle), midy)
			}
		}
		
		// icon 2/5: circle
		midx += wUnit*(BORDERW+PICW);
		{
			icons[1].addCircle(midx, midy, arm, Direction.CCW);
		}
		
		// icon 3/5: triangle
		midx += wUnit*(BORDERW+PICW);
		{
			float h = (float) (2*arm * Math.sqrt(3)/2);
			icons[2].moveTo(midx, midy-arm);
			icons[2].rLineTo(arm, h);
			icons[2].rLineTo(-2*arm, 0);
			icons[2].close();
		}
		
		// icon 4/5: square
		midx += wUnit*(BORDERW+PICW);
		{
			float arm2 = arm*0.8f;
			icons[3].addRect(midx-arm2, midy-arm2, midx+arm2, midy+arm2, Direction.CCW);
		}
		
		// icon 5/5: slash
		midx += wUnit*(BORDERW+PICW);
		{
			float dx = arm*0.7f;
			float dy = arm*0.9f;
			icons[4].moveTo(midx-dx, midy+dy);
			icons[4].lineTo(midx+dx, midy-dy);
		}
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		
		//stroke.setColor(Color.BLACK);
		stroke.setStrokeWidth(STROKEWIDTH);
		stroke.setStyle(Paint.Style.STROKE);
		stroke.setAntiAlias(true);
		
		fill.setColor(Color.WHITE);
		fill.setStyle(Paint.Style.FILL);
		
//		canvas.drawLine(0, 0, w, h, p);

		// draw background tabs
		stroke.setColor(Color.GRAY);
		for (int i=tabs.length-1; i>=0; i--) {
			if (i==currentTab)
				continue;
			canvas.drawPath(tabs[i], fill);
			canvas.drawPath(tabs[i], stroke);
			canvas.drawPath(icons[i], stroke);
		}
		
		// draw foreground tab
		stroke.setColor(Color.BLACK);
		canvas.drawPath(tabs[currentTab], fill);
		canvas.drawPath(tabs[currentTab], stroke);
		canvas.drawPath(icons[currentTab], stroke);
		
		// draw "page edge"
		float bottom = h-STROKEWIDTH/2;
		canvas.drawLine(0, bottom, currentTab*wUnit*(PICW+BORDERW), bottom, stroke);
		canvas.drawLine((currentTab+1)*wUnit*(PICW+BORDERW)+wUnit*BORDERW, bottom, w, bottom, stroke);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (MotionEventCompat.getActionMasked(event)) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
			break;
		default:
			return super.onTouchEvent(event);
		}
		
		float x = MotionEventCompat.getX(event, 0);
		for (int i=0; i<tabs.length; i++) {
			float right = (i+1)*wUnit*(PICW+BORDERW);
			if (x>right)
				continue;
			float left = right-wUnit*PICW;
			if (x<left)
				break;
			
			if (i==currentTab)
				break;
			
			currentTab = i;
			invalidate(); //FIXME: are we in UI thread?
			break;
		}
		return true;
	}
	
	public static interface OnTabChangeListener {
		void onTabChange(TabChoiceView view, int currentTab);
	}
	
	private OnTabChangeListener onTabChangeListener;
	
	public void setOnTabChangeListener(OnTabChangeListener listener) {
		onTabChangeListener = listener;
	}
	protected void fireOnTabChangeListener() {
		if (onTabChangeListener != null) {
			onTabChangeListener.onTabChange(this, currentTab);
		}
	}
}
