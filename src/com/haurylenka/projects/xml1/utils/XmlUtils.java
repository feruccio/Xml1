package com.haurylenka.projects.xml1.utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.haurylenka.projects.xml1.beans.Airport;
import com.haurylenka.projects.xml1.beans.Itinerary;
import com.haurylenka.projects.xml1.beans.Periodicity;
import com.haurylenka.projects.xml1.enums.PeriodType;
import com.haurylenka.projects.xml1.enums.SeatType;
import com.haurylenka.projects.xml1.exceptions.XmlException;
import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;

public class XmlUtils {

	public static void exportScheduleToXML(String fileName, 
			List<Itinerary> itineraries, String schemaPath) {
		try {
			DocumentBuilderFactory docFactory = 
					DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
			// Schema
			File schemaFile = new File(schemaPath);
			String xmlns = XMLConstants.W3C_XML_SCHEMA_NS_URI;
			SchemaFactory schemaFactory = SchemaFactory.newInstance(xmlns);
			Schema schema = schemaFactory.newSchema(schemaFile);
			docFactory.setSchema(schema);
			// DocumentBuilder
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			docBuilder.setErrorHandler(new DefaultErrorHandler());
			// Document
			Document doc = docBuilder.newDocument();
			// root element
			Element rootElement = doc.createElement("schedule");
			rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", 
					"xmlns", "http://www.example.org/schedule");
			rootElement.setAttributeNS(
					"http://www.w3.org/2001/XMLSchema-instance", 
					"xsi:schemaLocation", 
					"http://www.example.org/schedule schedule.xsd");
			doc.appendChild(rootElement);
			// building DOM
			for (Itinerary itinerary : itineraries) {
				Element entry = doc.createElement("itinerary");
				rootElement.appendChild(
						populateItineraryElement(doc, entry, itinerary));
			}
			// saving to an xml file
			TransformerFactory transformerFactory = 
					TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fileName));
			transformer.transform(source, result);
			// done
			System.out.println("Export done");
		} catch (ParserConfigurationException pce) {
			throw new XmlException(
					"Unnable to create a DocumentBuilder object", pce);
		} catch (TransformerException tfe) {
			throw new XmlException(
					"Unnable to transform DOM into xml", tfe);
		} catch (SAXException sxe) {
			throw new XmlException(
					"Unnable to create a Schema object", sxe);
		}
	}

	private static Element populateItineraryElement(Document doc, 
			Element entry, Itinerary itinerary) {
		Attr attr = doc.createAttribute("number");
		attr.setValue(itinerary.getNumber());
		entry.setAttributeNode(attr);
		Element element = doc.createElement("from");
		entry.appendChild(populateAirportElement(doc, element, 
				itinerary.getFrom()));
		element = doc.createElement("to");
		entry.appendChild(populateAirportElement(doc, element, 
				itinerary.getTo()));
		element = doc.createElement("periodicity");
		entry.appendChild(populatePeriodicityElement(doc, element, 
				itinerary.getPeriodicity()));
		return entry;
	}

	private static Element populateAirportElement(Document doc, 
			Element entry, Airport airport) {
		Element element = doc.createElement("name");
		element.setTextContent(airport.getName());
		entry.appendChild(element);
		element = doc.createElement("city");
		element.setTextContent(airport.getCity());
		entry.appendChild(element);
		return entry;
	}
	
	private static Element populatePeriodicityElement(Document doc, 
			Element entry, Periodicity periodicity) {
		Attr attr = doc.createAttribute("type");
		attr.setValue(periodicity.getType().toString().toLowerCase());
		entry.setAttributeNode(attr);
		attr = doc.createAttribute("ordinal");
		attr.setValue("" + periodicity.getOrdinal());
		entry.setAttributeNode(attr);
		Element element = doc.createElement("departure");
		element.setTextContent(periodicity.getDepartTime());
		entry.appendChild(element);
		element = doc.createElement("arrival");
		element.setTextContent(periodicity.getArrivalTime());
		entry.appendChild(element);
		element = doc.createElement("nextDay");
		element.setTextContent("" + periodicity.isNextDay());
		entry.appendChild(element);
		element = doc.createElement("gate");
		element.setTextContent(periodicity.getGate());
		entry.appendChild(element);
		element = doc.createElement("prices");
		entry.appendChild(populatePricesElement(doc, element, 
				periodicity.getPrices()));
		return entry;
	}

	private static Element populatePricesElement(Document doc, 
			Element entry, Map<SeatType, Integer> prices) {
		for (Map.Entry<SeatType, Integer> price : prices.entrySet()) {
			Element element = doc.createElement("price");
			entry.appendChild(populatePriceElement(doc, element, price));
		}
		return entry;
	}

	private static Element populatePriceElement(Document doc, Element entry, 
			Entry<SeatType, Integer> price) {
		Element element = doc.createElement("seatType");
		element.setTextContent(price.getKey().toString().toLowerCase());
		entry.appendChild(element);
		element = doc.createElement("base");
		element.setTextContent("" + price.getValue());
		entry.appendChild(element);
		return entry;
	}

	public static List<Itinerary> readScheduleFromFile(String fileName) {
		List<Itinerary> itineraries = new ArrayList<>();
		try {
			File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = 
					DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			NodeList nList = doc.getElementsByTagName(Itinerary.NODE_NAME);
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					itineraries.add(getItinerary(eElement));
				}
			}
			System.out.println("Import done");
		} catch (SAXException | ParserConfigurationException | IOException e) {
			throw new XmlException("XML file parse error", e);
		}
		return itineraries;

	}
	
	private static Itinerary getItinerary(Element element) {
		Itinerary itinerary = new Itinerary();
		
		itinerary.setNumber(element.getAttribute(Itinerary.NUMBER));
		NodeList itElements = element.getChildNodes();
		
		for (int i = 0; i < itElements.getLength(); i++) {
			Node node = itElements.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element nElement = (Element) node;
				switch (nElement.getNodeName()) {
				case Itinerary.FROM:
					itinerary.setFrom(getAirport(nElement));
					break;
					
				case Itinerary.TO:
					itinerary.setTo(getAirport(nElement));
					break;
					
				case Itinerary.PERIODICITY:
					itinerary.setPeriodicity(getPeriodicity(nElement));
					break;

				default:
					throw new XmlException("No such element in itinerary: " 
							+ node.getNodeName());
				}
			}
		}
		
		return itinerary;
	}

	private static Airport getAirport(Element element) {
		Airport airport = new Airport();
		NodeList airElements = element.getChildNodes();
		for (int i = 0; i < airElements.getLength(); i++) {
			Node node = airElements.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element nElement = (Element) node;
				switch (nElement.getNodeName()) {
				case Airport.NAME:
					airport.setName(nElement.getTextContent());
					break;
					
				case Airport.CITY:
					airport.setCity(nElement.getTextContent());
					break;

				default:
					throw new XmlException("No such element in airport: " 
							+ node.getNodeName());
				}
			}
		}
		return airport;
	}

	private static Periodicity getPeriodicity(Element element) {
		Periodicity periodicity = new Periodicity();
		String typeStr = element.getAttribute(Itinerary.TYPE);
		PeriodType type = PeriodType.valueOf(typeStr.toUpperCase());
		periodicity.setType(type);
		String ordinalStr = element.getAttribute(Itinerary.ORDINAL);
		int ordinal = Integer.valueOf(ordinalStr);
		periodicity.setOrdinal(ordinal);
		String departure = null;
		String arrival = null;
		String nextDay = null;
		NodeList perElements = element.getChildNodes();
		for (int i = 0; i < perElements.getLength(); i++) {
			Node node = perElements.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element nElement = (Element) node;
				switch (nElement.getNodeName()) {
				case Periodicity.DEPARTURE:
					departure = node.getTextContent();
					break;
					
				case Periodicity.ARRIVAL:
					arrival = node.getTextContent();
					break;
					
				case Periodicity.NEXT_DAY:
					nextDay = node.getTextContent();
					break;
					
				case Periodicity.GATE:
					periodicity.setGate(node.getTextContent());
					break;
					
				case Periodicity.PRICES:
					periodicity.setPrices(getPrices(nElement));
					break;

				default:
					throw new XmlException("No such element in periodicity: " 
							+ node.getNodeName());
				}
			}
		}
		try {
			periodicity.setDates(departure, arrival, nextDay);
		} catch (ParseException e) {
			throw new XmlException(
					"An error occurred while parsing Periodicity dates", e);
		}
		return periodicity;
	}

	private static Map<SeatType, Integer> getPrices(Element element) {
		Map<SeatType, Integer> prices = new HashMap<>();
		NodeList prElements = element.getChildNodes();
		for (int i = 0; i < prElements.getLength(); i++) {
			Node node = prElements.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element nElement = (Element) node;
				if (nElement.getNodeName().equals(Periodicity.PRICE)) {
					getPrice(prices, nElement);
				}
			}
		}
		return prices;
	}

	private static void getPrice(Map<SeatType, Integer> prices, Element element) {
		NodeList nodes = element.getChildNodes();
		String seatTypeStr = null;
		String baseStr = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element nElement = (Element) node;
				switch (nElement.getNodeName()) {
				case Periodicity.SEAT_TYPE:
					seatTypeStr = nElement.getTextContent();
					break;
					
				case Periodicity.BASE:
					baseStr = nElement.getTextContent();
					break;

				default:
					throw new XmlException("No such element in price: " 
							+ node.getNodeName());
				}
			}
		}
		SeatType seatType = SeatType.valueOf(seatTypeStr.toUpperCase());
		int base = Integer.valueOf(baseStr);
		prices.put(seatType, base);
	}

}

