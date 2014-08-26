package com.akavel.bookshelf;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

public class BooksDataAdapter extends BaseAdapter { // implements ListAdapter { //extends SimpleCursorAdapter {
	private Cursor cursor;

	public BooksDataAdapter(Cursor cursor) {
		this.cursor = cursor;
	}
	
	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		if (!cursor.moveToPosition(position))
			return null;
		return new Book(cursor.getString(0), cursor.getString(1), cursor.getString(2));
	}

	@Override
	public long getItemId(int position) {
		if (!cursor.moveToPosition(position))
			return -1;
		return cursor.getLong(3);
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Book book = (Book) getItem(position);
		if (book == null)
			return null;
		BookView view = (BookView) convertView;
		if (view == null) {
			view = new BookView(book, parent.getContext(), null);
		}
		return view;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return getCount() == 0;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}
}