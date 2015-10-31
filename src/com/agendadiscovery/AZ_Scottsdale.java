package com.agendadiscovery;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AZ_Scottsdale {
	WebDriver driver;

	public ArrayList<AgendaWrapper> getAgendas() throws Exception {
		ArrayList<AgendaWrapper> aws = new ArrayList<AgendaWrapper>();
		String baseUrl;

		// Set up the basics
		driver = new FirefoxDriver();
		baseUrl = "https://eservices.scottsdaleaz.gov/eServices/CityHallAgendaPlanner/Meetings.aspx";
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		// Navigate to the Agendas
		driver.get(baseUrl);

	    // Wait for agenda tables to load
		WebDriverWait wait = new WebDriverWait(driver, 30);
		
	    By titleBy = By.xpath("/html/body/form/div[3]/section/div/div/div/div[2]/div/div[2]/table/tbody/tr[1]/th");
	    wait.until(ExpectedConditions.presenceOfElementLocated(titleBy));
	    
		By rowsBy = By.xpath("/html/body/form/div[3]/section/div/div/div/div[2]/div/div[2]/table/tbody/tr");
		List<WebElement> rows = driver.findElements(rowsBy);

		// Get a reference to the current window
		String base = driver.getWindowHandle();

		// Grab a list of links to click to avoid the stale element exception
		for (WebElement row : rows) {
			Date pDate = new Date();
			String title = "";
			
			try {
				// Grab the date
		 		By agendaTitleBy = By.xpath("./td/a");
		 		
		 		if(Util.existsElement("Scottsdale AZ",row, agendaTitleBy)){
		 			WebElement we = row.findElement(agendaTitleBy);
		 			// Date
		 			String date = we.getText();
		 			pDate = Util.parseDate("Scottsdale AZ","E, MMMM dd, yyyy", date);
		 			
		 			// Title
		 			title = we.getText();
		 			String link = we.getAttribute("href");
		 			
		 			if(link != null && link != "" && link.trim() != ""){
					 	// Wrap it for processing
					 	AgendaWrapper aw = new AgendaWrapper();
			 			aw.setDate(pDate);
			 			aw.setLink(link);
			 			aw.setTitle(title);
			 			aws.add(aw);
			 			
			 			driver.navigate().back();
		 			}
		 		}
		 	} catch (NoSuchElementException e) {
		 		System.out.println("getAgendas NoSuchElementException: " + e.getMessage());
		 		continue;
		 	}
		}

		// Grab all of the links that were stale
		for(AgendaWrapper aw : aws){
			driver.get(aw.getLink());

		 	// Wait for the agenda to load
		 	By agendaBy = By.id("MiddleColumnContent");
		 	wait.until(ExpectedConditions.presenceOfElementLocated(agendaBy));

		 	// Lets try to grab the html!!!
		 	By childElements = By.xpath("//*[@id=\"MiddleColumnContent\"]");
		 	
		 	// Create the file from the raw text
		 	File agenda = Util.convertToPDF("Scottsdale AZ", getAsFile(driver.findElements(childElements)));
		 	aw.setPdf(agenda);
		}
		
		driver.quit();

		return aws;
	}

	// agenda elments loses references so cant put in the util
	public static File getAsFile(List<WebElement> agendaElements) {
		File agenda = null;		

		try{
			agenda = File.createTempFile("agenda-", ".txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(agenda));
			for(WebElement element : agendaElements){
	    	    bw.write(element.getText());
		 	}
		 	bw.close();
		}catch(FileNotFoundException e){
			System.out.println("getAsFile FileNotFoundException: " + e.getMessage());
		} catch (MalformedURLException e) {
			System.out.println("getAsFile MalformedURLException: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("getAsFile IOException: " + e.getMessage());
		}
		
		return agenda;
	}
}
