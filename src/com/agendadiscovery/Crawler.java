package com.agendadiscovery;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Crawler {

	public static void main(String[] args) {
		System.out.println("Crawl Started");
		try{
			// Process City
			Class cls = Class.forName("com.agendadiscovery.AZ_Scottsdale");
			Object obj = cls.newInstance();

			//call the printIt method
			Method method = cls.getDeclaredMethod("getAgendas");
			ArrayList<AgendaWrapper> aws = (ArrayList<AgendaWrapper>) method.invoke(obj, null);

			for(AgendaWrapper aw: aws){
				System.out.println(aw.getTitle()+"\t"+aw.getLink()+"\t"+aw.getDate());
			}
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Caught exception: " + ex.getMessage());
		}

	}

}
