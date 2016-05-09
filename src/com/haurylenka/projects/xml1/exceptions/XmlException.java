package com.haurylenka.projects.xml1.exceptions;

public class XmlException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public XmlException() {
		super();
	}

	public XmlException(String message, Throwable cause) {
		super(message, cause);
	}

	public XmlException(String message) {
		super(message);
	}

	public XmlException(Throwable cause) {
		super(cause);
	}

}
