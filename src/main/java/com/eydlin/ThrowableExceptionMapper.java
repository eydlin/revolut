package com.eydlin;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {
	private Logger log = Logger.getLogger(this.getClass());
	
	@Override
	public Response toResponse(Throwable th) {
		log.error(th.getMessage(), th);
		return Response.status(500).
				entity(th.getMessage()).
				type("text/plain").build();
	}
}
