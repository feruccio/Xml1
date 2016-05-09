package com.haurylenka.projects.xml1.beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.haurylenka.projects.xml1.enums.PeriodType;
import com.haurylenka.projects.xml1.enums.SeatType;

public class Periodicity {

	public static final String DEPARTURE = "departure";
	public static final String ARRIVAL = "arrival";
	public static final String NEXT_DAY = "nextDay";
	public static final String GATE = "gate";
	public static final String PRICES = "prices";
	public static final String PRICE = "price";
	public static final String SEAT_TYPE = "seatType";
	public static final String BASE = "base";
	private PeriodType type;
	private int ordinal;
	private Date departure;
	private Date arrival;
	private boolean nextDay;
	private String gate;
	private Map<SeatType, Integer> prices;
	
	public Periodicity() {}

	public Periodicity(PeriodType type, int ordinal) {
		this.type = type;
		this.ordinal = ordinal;
	}

	public PeriodType getType() {
		return type;
	}

	public void setType(PeriodType type) {
		this.type = type;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public Date getDeparture() {
		return departure;
	}
	
	public String getDepartTime() {
		return getTime(getDeparture());
	}
	
	public String getArrivalTime() {
		return getTime(getArrival());
	}
	
	private String getTime(Date date) {
		final String PATTERN = "HH:mm:ss";
		SimpleDateFormat format = new SimpleDateFormat(PATTERN);
		return format.format(date);
	}

	public void setDeparture(Date departure) {
		this.departure = departure;
	}

	public Date getArrival() {
		return arrival;
	}

	public void setArrival(Date arrival) {
		if (departure == null) {
			throw new IllegalStateException("Departure time is needed first");
		}
		final String PATTERN = "yyyy-MM-dd";
		SimpleDateFormat format = new SimpleDateFormat(PATTERN);
		String departDate = format.format(departure);
		String arrivalDate = format.format(arrival);
		nextDay = !arrivalDate.equals(departDate);
		this.arrival = arrival;
	}

	public boolean isNextDay() {
		return nextDay;
	}

	public void setNextDay(boolean nextDay) {
		this.nextDay = nextDay;
	}

	public String getGate() {
		return gate;
	}

	public void setGate(String gate) {
		this.gate = gate;
	}

	public Map<SeatType, Integer> getPrices() {
		return prices;
	}

	public void setPrices(Map<SeatType, Integer> prices) {
		this.prices = prices;
	}

	public void setDates(String departure, String arrival, String nextDay) 
			throws ParseException {
		final String PATTERN = "HH:mm:ss";
		SimpleDateFormat format = new SimpleDateFormat(PATTERN);
		this.departure = format.parse(departure);
		this.arrival = format.parse(arrival);
		this.nextDay = Boolean.valueOf(nextDay);
		if (this.nextDay) {
			// adding 24 hours
			this.arrival = new Date(this.arrival.getTime() + 24 * 60 * 60 * 1000L);
		}
	}
	
}
