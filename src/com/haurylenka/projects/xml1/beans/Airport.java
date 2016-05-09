package com.haurylenka.projects.xml1.beans;

public class Airport {

	public static final String NAME = "name";
	public static final String CITY = "city";
	private String name;
	private String city;
	
	public Airport() {}

	public Airport(String name, String city) {
		this.name = name;
		this.city = city;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
}
