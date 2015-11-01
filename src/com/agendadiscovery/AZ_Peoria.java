package com.agendadiscovery;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AZ_Peoria {
	WebDriver driver;

	public static void main(String[] args) throws Exception {
		doTrustToCertificates();
		AZ_Peoria az_Peoria = new AZ_Peoria();
		az_Peoria.getAgendas();

	}

	public ArrayList<AgendaWrapper> getAgendas() throws Exception {
		ArrayList<AgendaWrapper> aws = new ArrayList<AgendaWrapper>();
		String baseUrl;

		// Set up the basics
		driver = new FirefoxDriver();
		baseUrl = "https://www.peoriaaz.gov/NewSecondary.aspx?id=54805";
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		// Navigate to the Agendas
		driver.get(baseUrl);

		// Wait for agenda tables to load
		WebDriverWait wait = new WebDriverWait(driver, 30);

		By tbodyBy = By
				.xpath("//div['ctl00_ContentPlaceHolder_secondaryContentBlock']//table/tbody/tr");
		wait.until(ExpectedConditions.presenceOfElementLocated(tbodyBy));

		List<WebElement> elements = driver.findElements(tbodyBy);
		Date pDate = new Date();

		for (Iterator<WebElement> iterator = elements.iterator(); iterator.hasNext();) {
			List<WebElement> tdElements = iterator.next().findElements(By.tagName("td"));

			if (tdElements.size() == 4) {
				String date = tdElements.get(0).getText();
				if (!date.equals("DATES") && date.contains("2015")) {
					pDate = Util.parseDate("Peoria_AZ", "MMMM dd, yyyy", date.trim());
					List<WebElement> agenda = tdElements.get(1).findElements(By.tagName("a"));
					for (Iterator<WebElement> anchorIterator = agenda.iterator(); anchorIterator
							.hasNext();) {
						WebElement webElement = anchorIterator.next();
						AgendaWrapper agendaWrapper = new AgendaWrapper();
						agendaWrapper.setDate(pDate);
						agendaWrapper.setTitle(webElement.getText());
						String pdfUrl = webElement.getAttribute("href");
						agendaWrapper.setLink(pdfUrl);
						agendaWrapper.setPdf(Util.downloadPDF("Peoria_AZ", pdfUrl));
						aws.add(agendaWrapper);
					}
				}
			}
		}

		driver.quit();

		return aws;
	}

	/**
	 * To download file with HTTPS, to trust all certificates.
	 * 
	 * @throws GeneralSecurityException
	 */
	private static void doTrustToCertificates() throws GeneralSecurityException {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
					String authType) {
				// No need to implement.
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
					String authType) {
				// No need to implement.
			}
		} };

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
				}
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}

}
