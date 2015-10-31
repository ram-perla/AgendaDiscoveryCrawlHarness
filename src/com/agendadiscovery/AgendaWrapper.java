package com.agendadiscovery;

import java.io.File;
import java.util.Date;

public class AgendaWrapper {
	private Date date;
	private String title;
	private File pdf;
	private String link;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public File getPdf() {
		return pdf;
	}
	public void setPdf(File pdf) {
		this.pdf = pdf;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	
}
