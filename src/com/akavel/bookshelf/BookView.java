package com.akavel.bookshelf;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BookView extends LinearLayout {
	/**
	 * 
	 */
	private Book book;
	private final TextView titleView, authorView, pathView;

	public BookView(Book book, Context context, AttributeSet attrs) {
		super(context, attrs);
		this.book = book;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_book, this);
		
		titleView = (TextView) findViewById(R.id.title);
		authorView = (TextView) findViewById(R.id.author);
		pathView = (TextView) findViewById(R.id.path);
		
		update();
	}

	public void update() {
		if (book == null)
			return;
		titleView.setText(book.title);
		authorView.setText(book.author);
		pathView.setText(book.path);
	}
}