package com.slerpio.teachme.api;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slerp.core.CoreException;
import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class RestExceptionHandler {	

	@Autowired
	private MessageSource messageSource;
	static private Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

	@ExceptionHandler(HttpServerErrorException.class)
	public ResponseEntity<ErrorItem> httpServerExceptionHandler(HttpServerErrorException e) {
		log.error("HttpServerErrorException : {}", e.getMessage());
		ErrorItem item = new ErrorItem();
		String errResult = e.getResponseBodyAsString();
		log.info("Error Response ", errResult);
		if (!errResult.isEmpty()) {
			Domain errorDomain = new Domain(errResult);
			item.setStatus(1);

			try {
				String message = messageSource.getMessage(errorDomain.getString("message"), null,
						LocaleContextHolder.getLocale());
				item.setBody(message);
			} catch (NoSuchMessageException ex) {
				item.setBody(e.getMessage());
			}
		}
		return new ResponseEntity<>(item, HttpStatus.OK);
	}

	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<ErrorItem> resourceAccessExceptionHandler(ResourceAccessException e) {
		log.error("ResourceAccessException : {}", e.getMessage());
		ErrorItem item = new ErrorItem();
		String errResult = "server.fault";
		item.setStatus(1);
		try {
			String message = messageSource.getMessage(errResult, null, LocaleContextHolder.getLocale());
			item.setBody(message);
		} catch (NoSuchMessageException ex) {
			item.setBody(e.getMessage());
		}
		return new ResponseEntity<>(item, HttpStatus.OK);
	}

	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorItem> constraintViolationHandler(DataIntegrityViolationException e) {
		log.error("DataIntegrityViolationException : {}", e.getMessage());
		log.error("DataIntegrityViolationException : >>> {}", e);
		ErrorItem item = new ErrorItem(1, e.getMessage());
		return new ResponseEntity<>(item, HttpStatus.OK);
	}

	@ExceptionHandler(CoreException.class)
	public ResponseEntity<ErrorItem> coreHandle(CoreException e) {
		log.error("CoreException : {}", e.getMessage());
		log.error("CoreException : >>>> \n", e);
		ErrorItem item = new ErrorItem();
		item.setStatus(1);
		try {
			String body = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
			item.setBody(body);
		} catch (NoSuchMessageException ex) {
			item.setBody(e.getMessage());
		}

		return new ResponseEntity<>(item, HttpStatus.OK);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorItem> coreHandle(HttpMessageNotReadableException e) {
		ErrorItem item = new ErrorItem(2,
				messageSource.getMessage("failed.to.read.json.data", null, LocaleContextHolder.getLocale()));
		return new ResponseEntity<>(item, HttpStatus.OK);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorItem> httpMethod(HttpRequestMethodNotSupportedException e) {
		ErrorItem item = new ErrorItem(2, "should.be.use.method." + e.getSupportedMethods()[0]);
		return new ResponseEntity<>(item, HttpStatus.OK);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ErrorItem> unsupportMediaType(HttpMediaTypeNotSupportedException e) {
		ErrorItem item = new ErrorItem(2, "failed.to.handle.media.type");
		return new ResponseEntity<>(item, HttpStatus.OK);
	}

	public static class ErrorItem {
		private int status;
		private String body;

		public ErrorItem() {
		}

		public ErrorItem(int status, String body) {
			super();
			this.status = status;
			this.body = body;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

	}

	@XmlRootElement(name = "errors")
	public static class ErrorResponse {
		private List<ErrorItem> errors = new ArrayList<>();

		@XmlElement(name = "error")
		public List<ErrorItem> getErrors() {
			return errors;
		}

		public void setErrors(List<ErrorItem> errors) {
			this.errors = errors;
		}

		public void addError(ErrorItem error) {
			this.errors.add(error);
		}
	}
}