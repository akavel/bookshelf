package com.akavel.bookshelf;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PageSelectorView extends LinearLayout {

	public PageSelectorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater.from(context).inflate(R.layout.layout_page_selector_view, this, true);
		
		findViewById(R.id.page_selector_view_up).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentPage <= 0)
					return;
				currentPage--;
				update();
			}
		});
		
		findViewById(R.id.page_selector_view_down).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentPage >= pagesCount-1)
					return;
				currentPage++;
				update();
			}
		});
		
		update();
	}
	
	public int currentPage = 0;
	public int pagesCount = 4;

	/// NOTE: call only from UI thread.
	public void update() {
		TextView info = (TextView) findViewById(R.id.page_selector_view_info);
		info.setText(String.format("%d/%d", currentPage+1, pagesCount));
		
		Button up = (Button) findViewById(R.id.page_selector_view_up);
		up.setEnabled(currentPage > 0);
		
		Button down = (Button) findViewById(R.id.page_selector_view_down);
		down.setEnabled(currentPage < pagesCount-1);
		
		invalidate();
	}
	
	
}
