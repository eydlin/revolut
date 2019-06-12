package com.eydlin.revolut.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import com.eydlin.revolut.ApplicationException;

@Provider
public class ApplicationExceptionMapper implements ExceptionMapper<ApplicationException> {
	private Logger log = Logger.getRootLogger();
	
	@Override
	public Response toResponse(ApplicationException ex) {
		log.debug(ex.getMessage(), ex);
		return Response.status(ex.getErrorType().getStatusCode()).
				entity(ex.getErrorType().getErrorCode()).
				type("text/plain").build();
	}
}