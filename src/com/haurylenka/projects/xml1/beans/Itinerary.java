package com.haurylenka.projects.xml1.beans;

public class Itinerary {

	public static final String NODE_NAME = "itinerary";
	public static final String TYPE = "type";
	public static final String ORDINAL = "ordinal";
	public static final String NUMBER = "number";
	public static final String FROM = "from";
	public static final String TO = "to";
	public static final String PERIODICITY = "periodicity";
	private String number;
	private Airport from;
	private Airport to;
	private Periodicity periodicity;
	
	public Itinerary() {}

	public Itinerary(String number, Airport from, Airport to, 
			Periodicity periodicity) {
		this.number = number;
		this.from = from;
		this.to = to;
		this.periodicity = periodicity;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Airport getFrom() {
		return from;
	}

	public void setFrom(Airport from) {
		this.from = from;
	}

	public Airport getTo() {
		return to;
	}

	public void setTo(Airport to) {
		this.to = to;
	}

	public Periodicity getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(Periodicity periodicity) {
		this.periodicity = periodicity;
	}
	
}
