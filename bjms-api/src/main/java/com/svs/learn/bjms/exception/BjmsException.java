package com.svs.learn.bjms.exception;

public class BjmsException extends RuntimeException {

	private static final long serialVersionUID = -7401409439097039414L;

	public BjmsException() {
		super();
	}

	public BjmsException(String message) {
		super(message);
	}

	public BjmsException(String message, Throwable t) {
		super(message, t);
	}
}
