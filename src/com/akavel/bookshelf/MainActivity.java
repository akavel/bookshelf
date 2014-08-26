package com.akavel.bookshelf;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.akavel.bookshelf.ScanBooksTask.OnProgressListener;

public class MainActivity extends FragmentActivity {
	public final static String EXTRA_MESSAGE = "com.akavel.bookshelf.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		BooksStorage books = new BooksStorage(getBaseContext()); // or getApplicationContext()?
		
		/*
		{
			ArrayList<Book> sampleBooks = new ArrayList<Book>();
			sampleBooks.add(new Book("Pan Tadeusz", "Adam Mickiewicz", "/mnt/sdcard/foo/bar/baz.epub"));

			{
				//Cursor cursor = books.getWritableDatabase().rawQuery("SELECT title, author, path FROM shelves ORDER BY title;", null);
				Cursor cursor = books.openCursor(0); //FIXME: use some special ID, e.g. -1, for 'clipboard'
				cursor.moveToFirst();
				do {
					sampleBooks.add(new Book(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
					cursor.moveToNext();
				} while(!cursor.isAfterLast());
				//cursor.close();
			}
			
			if (false) {
				Cursor cursor = new BooksStorage(this).openCursor(1); //FIXME: use some special ID, e.g. -1, for 'clipboard'
				cursor.moveToFirst();
				do {
					sampleBooks.add(new Book(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
					cursor.moveToNext();
				} while(!cursor.isAfterLast());
			}
			
			ClipboardAdapter clipboardAdapter = new ClipboardAdapter(this, R.layout.layout_book, sampleBooks);
			
			ListView shelves = (ListView) findViewById(R.id.clipboard);
			shelves.setAdapter(clipboardAdapter);
			
		}
		 */
		
		Cursor cursor = books.openCursor(0);
		//cursor.moveToFirst();
		final BooksDataAdapter shelfAdapter = new BooksDataAdapter(cursor);
		ListView shelf = (ListView) findViewById(R.id.shelf2);
		shelf.setAdapter(shelfAdapter);
		
		ScanBooksTask booksScan = new ScanBooksTask();
		booksScan.setOnProgressListener(new OnProgressListener() {
			@Override
			public void OnProgress(int progress) {
				//findViewById(R.id.shelf2).invalidate();
				shelfAdapter.notifyDataSetChanged();
				
				setTitle(getResources().getString(R.string.app_name) + ": scanned " + progress + " book(s)");
				// TODO Auto-generated method stub
				
			}
		});
		booksScan.execute(this);
	}
		
	public class ClipboardAdapter extends ArrayAdapter<Book> {
		
		public ClipboardAdapter(Context context, int textViewResourceId,
				ArrayList<Book> sampleBooks) {
			super(context, textViewResourceId, sampleBooks);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = new BookView(getItem(position), getContext(), null);
			}
			return v;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void sendMessage(View view) {
		/*
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
		*/
	}

}
