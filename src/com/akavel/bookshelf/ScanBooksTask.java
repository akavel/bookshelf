package com.akavel.bookshelf;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.jxpath.JXPathContext;
import org.jaxen.JaxenException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class ScanBooksTask extends AsyncTask<Object, Book, Void> {
	private Context context;

	@Override
	protected Void doInBackground(Object... args) {
		context = (Context) args[0];

		// FIXME: process all filesystem rooted at /

//		Log.i("", "EXTERNAL_STORAGE=" + System.getenv("EXTERNAL_STORAGE"));
//		// All Secondary SD-CARDs (all exclude primary) separated by ":"
//		Log.i("", "SECONDARY_STORAGE=" + System.getenv("SECONDARY_STORAGE"));

		
		//FIXME: nothing below seems to really work on Nook Simple Touch :/
		// File dir = Environment.getExternalStorageDirectory();
		//File dir = Environment.getRootDirectory();
		String[] dirs = getStorageDirectories();
		
		//String[] dirs = new String[] { "/mnt" }; // FIXME: array not necessary
		if (dirs==null)
			return null; //FIXME: throw, or something; find out some default dir, maybe "/"
		for (String path : dirs) {
			File dir = new File(path);
			scan(dir);
		}
		Log.d(ScanBooksTask.class.getSimpleName(), "scanning finished.");

		return null;
	}

	private void scan(File dir) {
		if (dir == null || !dir.isDirectory())
			return;

		Log.i(ScanBooksTask.class.getSimpleName(),
				"Scanning dir: " + dir.getAbsolutePath());

		File[] children = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File child) {
				if (child.isDirectory())
					return true;
				Log.i(ScanBooksTask.class.getSimpleName(),
						" file: " + child.getName());
				return child.getName().endsWith(".epub"); // FIXME: case
															// insensitive
			}
		});

		if (children == null)
			return;

		for (File child : children) {
			if (child.isDirectory()) {
				scan(child);
				continue;
			}

			try {
				Book book = new Book("", "", child.getAbsolutePath());
				extractMetadata(book);
				publishProgress(book);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(ScanBooksTask.class.getSimpleName(), child.getAbsolutePath(), e);
			}
		}
	}

//	DOMXPath titleXPath;
//	DOMXPath authorXPath;
	
	public ScanBooksTask()
	{
		/*
		try {
			SimpleNamespaceContext context = new SimpleNamespaceContext();
			context.addNamespace("opf", "http://www.idpf.org/2007/opf");
			context.addNamespace("dc", "http://purl.org/dc/elements/1.1/");
			//titleXPath = new DOMXPath("//dc:title/text()");
			titleXPath = new DOMXPath("count(//title)");
			titleXPath.setNamespaceContext(context);
			authorXPath = new DOMXPath("//dc:creator/text()");
			authorXPath.setNamespaceContext(context);
		} catch (JaxenException e) {
			throw new RuntimeException(e);
		}
		*/
	}

	private void extractMetadata(Book book) throws IOException, SAXException, ParserConfigurationException, JaxenException {
		Log.d(ScanBooksTask.class.getSimpleName(), "extracting from zip: " + book.path);
		ZipFile zip = new ZipFile(book.path);
		try {
			DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
			for (Enumeration<ZipArchiveEntry> entries=zip.getEntries(); entries.hasMoreElements(); ) {
				ZipArchiveEntry entry = entries.nextElement();
				Log.d(ScanBooksTask.class.getSimpleName(), "* zip entry: " + entry.getName());
				String filename = new File(entry.getName()).getName();
				if (!filename.equals("content.opf"))
					continue;
				
				Document doc = xmlFactory.newDocumentBuilder().parse(zip.getInputStream(entry));
				doc.normalize();
	
				JXPathContext context = JXPathContext.newContext(doc);
				context.registerNamespace("dc", "http://purl.org/dc/elements/1.1/");
				
				Object title = context.getValue("//dc:title");
				Log.d(ScanBooksTask.class.getSimpleName(), book.path + " - title=" + title);
				if (title!=null) {
					book.title = title.toString();
				}
				
				Object author = context.getValue("//dc:creator");
				Log.d(ScanBooksTask.class.getSimpleName(), book.path + " - author=" + author);
				if (author!=null) {
					book.author = author.toString();
				}
				return;
			}
		} finally {
			zip.close();
		}
	}	

	int progress = 0;

	@Override
	protected void onProgressUpdate(Book... values) {
		SQLiteDatabase db = new BooksStorage(context).getWritableDatabase();
		try {
			db.beginTransaction();
			for (Book book : values) {
				Log.d("", "Adding "+book.path);
				ContentValues data = new ContentValues();
				data.put(BooksStorage.AUTHOR, book.author);
				data.put(BooksStorage.TITLE, book.title);
				data.put(BooksStorage.PATH, book.path);

				Cursor cur = db.rawQuery("SELECT * FROM "
						+ BooksStorage.SHELVES + " WHERE " + BooksStorage.PATH
						+ "=?", new String[] { book.path });
				int n = cur.getCount();
				cur.close();
				Log.d("", " found " + n);

				if (n > 0) {
					db.update(BooksStorage.SHELVES, data, BooksStorage.PATH
							+ "=?", new String[] { book.path });
				} else {
					db.insertOrThrow(BooksStorage.SHELVES, null, data);
				}
			}
			db.setTransactionSuccessful();
			Log.d("", "committing transaction");
		} finally {
			db.endTransaction();
			db.close();
		}

		progress += values.length;
		fireOnProgressListener(progress);
	}

	interface OnProgressListener {
		void OnProgress(int progress);
	}

	private OnProgressListener onProgressListener;

	protected void setOnProgressListener(OnProgressListener listener) {
		this.onProgressListener = listener;
	}

	protected void fireOnProgressListener(int progress) {
		if (this.onProgressListener != null)
			this.onProgressListener.OnProgress(progress);
	}

    private static final Pattern DIR_SEPORATOR = Pattern.compile("/");
	private static String[] getStorageDirectories() {
		/*
		ArrayList<String> paths = new ArrayList<String>();
		
		 File file = new File("/system/etc/vold.fstab");
	        FileReader fr = null;
	        BufferedReader br = null;

	        try {
	            fr = new FileReader(file);
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } 

	        try {
	            if (fr != null) {
	                br = new BufferedReader(fr);
	                String s = br.readLine();
	                while (s != null) {
	                    if (s.startsWith("dev_mount")) {
	                        String[] tokens = s.split("\\s");
	                        String path = tokens[2]; //mount_point
	                        paths.add(path);
//	                        if (!Environment.getExternalStorageDirectory().getAbsolutePath().equals(path)) {
//	                            break;
//	                        }
	                    }
	                    s = br.readLine();
	                }
	            }            
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (fr != null) {
	                    fr.close();
	                }            
	                if (br != null) {
	                    br.close();
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return paths.toArray(new String[]{});
	        */
		
		/*
			// source: http://stackoverflow.com/a/13648873/98528
		    final HashSet<String> out = new HashSet<String>();
		    String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
		    String s = "";
		    try {
		        final Process process = new ProcessBuilder().command("mount")
		                .redirectErrorStream(true).start();
		        process.waitFor();
		        final InputStream is = process.getInputStream();
		        final byte[] buffer = new byte[1024];
		        while (is.read(buffer) != -1) {
		            s = s + new String(buffer);
		        }
		        is.close();
		    } catch (final Exception e) {
		        e.printStackTrace();
		    }

		    // parse output
		    final String[] lines = s.split("\n");
		    for (String line : lines) {
		        if (!line.toLowerCase(Locale.US).contains("asec")) {
		            if (line.matches(reg)) {
		                String[] parts = line.split(" ");
		                for (String part : parts) {
		                    if (part.startsWith("/"))
		                        if (!part.toLowerCase(Locale.US).contains("vold"))
		                            out.add(part);
		                }
		            }
		        }
		    }
		    return out.toArray(new String[]{});
		    */

	        /**
	         * Raturns all available SD-Cards in the system (include emulated)
	         *
	         * Warning: Hack! Based on Android source code of version 4.3 (API 18)
	         * Because there is no standart way to get it.
	         * TODO: Test on future Android versions 4.4+
	         *
	         * @return paths to all available SD-Cards in the system (include emulated)
	         */
            // Final set of paths
            final Set<String> rv = new HashSet<String>();
            // Primary physical SD-CARD (not emulated)
            final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
            // All Secondary SD-CARDs (all exclude primary) separated by ":"
            final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
            // Primary emulated SD-CARD
            final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
            if(TextUtils.isEmpty(rawEmulatedStorageTarget))
            {
                // Device has physical external storage; use plain paths.
                if(TextUtils.isEmpty(rawExternalStorage))
                {
                    // EXTERNAL_STORAGE undefined; falling back to default.
                    rv.add("/storage/sdcard0");
                }
                else
                {
                    rv.add(rawExternalStorage);
                }
            }
            else
            {
                // Device has emulated storage; external storage paths should have
                // userId burned into them.
                final String rawUserId;
                if(Build.VERSION.SDK_INT < 17) //Build.VERSION_CODES.JELLY_BEAN_MR1)
                {
                    rawUserId = "";
                }
                else
                {
                    final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    final String[] folders = DIR_SEPORATOR.split(path);
                    final String lastFolder = folders[folders.length - 1];
                    boolean isDigit = false;
                    try
                    {
                        Integer.valueOf(lastFolder);
                        isDigit = true;
                    }
                    catch(NumberFormatException ignored)
                    {
                    }
                    rawUserId = isDigit ? lastFolder : "";
                }
                // /storage/emulated/0[1,2,...]
                if(TextUtils.isEmpty(rawUserId))
                {
                    rv.add(rawEmulatedStorageTarget);
                }
                else
                {
                    rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                }
            }
            // Add all secondary storages
            if(!TextUtils.isEmpty(rawSecondaryStoragesStr))
            {
                // All Secondary SD-CARDs splited into array
                final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                Collections.addAll(rv, rawSecondaryStorages);
            }
            return rv.toArray(new String[rv.size()]);
        
		}
}
