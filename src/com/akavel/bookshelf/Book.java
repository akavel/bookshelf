package com.akavel.bookshelf;

import java.util.Date;

public class Book {
	public Book(String title, String author, String path) {
		this.title = title;
		this.author = author;
		this.path = path;
	}

	public Book(String path) {
		this.path = path;
	}

	public String title;
	public String author;
	public String path;
	public Date lastOpened;
	public Date fileModified;
}
