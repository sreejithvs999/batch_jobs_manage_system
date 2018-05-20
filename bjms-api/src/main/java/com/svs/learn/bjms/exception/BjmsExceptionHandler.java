package com.svs.learn.bjms.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BjmsExceptionHandler {

	Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Same response for all exception.
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<String> handleAllException(Exception e) {

		log.error("Handled exception: ", e);
		ResponseEntity<String> re = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		return re;
	}
}
