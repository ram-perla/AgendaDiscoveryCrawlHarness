package com.agendadiscovery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.TextToPDF;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Util {
	public static File convertToPDF(String city, File file) {
		TextToPDF ttp = new TextToPDF();
		PDDocument doc = null;
		File pdf = null;
		
		try{
			pdf = File.createTempFile("agenda-", ".pdf");
			doc = ttp.createPDFFromText(new FileReader(file));
			doc.save(pdf);
		}catch (IOException io) {
			System.out.println("Util convertToPDF "+city+" IOException: " + io.getMessage());
		}catch (Exception e) {
			System.out.println("Util convertToPDF "+city+" Exception: " + e.getMessage());
		}
		finally {
			try{
				if (doc != null) {
					doc.close();
				}
			}catch(IOException io){
				System.out.println("Util convertToPDF  "+city+" IOException: " + io.getMessage());
			}
			
		}
		
		return pdf;
	}
	
	public static File downloadPDF(String city, String urlStr) {
		File file = null;
		
		try{
			URL url = new URL(urlStr);
			file = File.createTempFile("agenda-", ".pdf");
			FileUtils.copyURLToFile(url, file, 60000, 60000);
		}catch(FileNotFoundException e){
			System.out.println("Util downloadPDF  "+city+" "+urlStr+" FileNotFoundException: " + e.getMessage());
		} catch (MalformedURLException e) {
			System.out.println("Util downloadPDF  "+city+" "+urlStr+" MalformedURLException: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Util downloadPDF  "+city+" "+urlStr+" IOException: " + e.getMessage());
		}
		
		return file;
	}

	public static boolean existsElement(String city, WebElement context, By by) {
	    try {
	    	context.findElement(by);
	    } catch (NoSuchElementException e) {
	    	System.out.println(city+" existsElement: " + e.getMessage());
	        return false;
	    }
	    return true;
	}

	public static Date parseDate(String city, String dateFormat, String dateStr){
		DateFormat df = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
		Date result = new Date();
		
		try {
			result = df.parse(dateStr);
		} catch (ParseException e) {
			System.out.println( city+" Error parsing date string: " + e.getMessage());
		}
		
		return result;
	}

	public static String replaceMonthAbbr(String dateStr){
		dateStr = dateStr.toLowerCase().replace("jan.", "january");
		dateStr = dateStr.toLowerCase().replace("feb.", "february");
		dateStr = dateStr.toLowerCase().replace("march", "march");
		dateStr = dateStr.toLowerCase().replace("april", "april");
		dateStr = dateStr.toLowerCase().replace("may", "may");
		dateStr = dateStr.toLowerCase().replace("june", "june");
		dateStr = dateStr.toLowerCase().replace("july", "july");
		dateStr = dateStr.toLowerCase().replace("aug.", "august");
		dateStr = dateStr.toLowerCase().replace("sep.", "september");
		dateStr = dateStr.toLowerCase().replace("oct.", "october");
		dateStr = dateStr.toLowerCase().replace("nov.", "november");
		dateStr = dateStr.toLowerCase().replace("dec.", "december");

		return dateStr;
	}

}
