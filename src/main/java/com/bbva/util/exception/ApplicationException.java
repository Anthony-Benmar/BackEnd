package com.bbva.util.exception;

import javax.ws.rs.WebApplicationException;

public class ApplicationException extends WebApplicationException{
    
    private static final long serialVersionUID = 4786788406015349220L;

	public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

}
