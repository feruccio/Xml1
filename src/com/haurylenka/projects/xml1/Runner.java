package com.haurylenka.projects.xml1;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.haurylenka.projects.xml1.beans.Airport;
import com.haurylenka.projects.xml1.beans.Itinerary;
import com.haurylenka.projects.xml1.beans.Periodicity;
import com.haurylenka.projects.xml1.enums.PeriodType;
import com.haurylenka.projects.xml1.enums.SeatType;
import com.haurylenka.projects.xml1.utils.XmlUtils;

public class Runner {
	
	private static final String DIR = "src/com/haurylenka/projects/xml1/resources/";

	public static void main(String[] args) {
		List<Itinerary> itineraries = new ArrayList<>();
		Itinerary itinerary = getItinerarySample();
		itineraries.add(itinerary);
		itineraries.add(itinerary);
		XmlUtils.exportScheduleToXML(DIR + "schedule.xml", itineraries, 
				DIR + "schedule.xsd");
		List<Itinerary> itinerariesNew = 
				XmlUtils.readScheduleFromFile(DIR + "schedule.xml");
	}
	
	private static Itinerary getItinerarySample() {
		Airport from = new Airport("JFK", "New York");
		Airport to = new Airport("Minsk2", "Minsk");
		Periodicity periodicity = new Periodicity(PeriodType.DAY, 1);
		periodicity.setDeparture(new Date());
		periodicity.setArrival(new Date(periodicity.getDeparture().getTime() 
				+ 9 * 60 * 60 * 1000L));
		periodicity.setGate("A2");
		Map<SeatType, Integer> prices = new HashMap<>();
		prices.put(SeatType.ECONOMY, 300);
		prices.put(SeatType.FIRST, 400);
		prices.put(SeatType.BUSINESS, 500);
		periodicity.setPrices(prices);
		return new Itinerary("AB1234", from, to, periodicity);
	}

}
