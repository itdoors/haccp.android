package com.itdoors.haccp.rest;

public enum HttpMethod {
	    GET,
	    PUT,
	    POST,
	    DELETE;
	    
	    public static final String GET_STR = "get";
	    public static final String POST_STR = "post";
	    public static final String PUT_STR = "put";
	    public static final String DELETE_STR = "delete";
	    

	    public static HttpMethod fromString(String method){
	    	if(method.equals(GET_STR))
	    		return GET;
	    	else if(method.equals(POST_STR))
	    		return POST;
	    	else if(method.equals(DELETE_STR))
	    		return DELETE;
	    	else if(method.equals(PUT_STR))
	    		return PUT;
	    	else
	    		throw new IllegalArgumentException("Wrong hhtp method format: " + method);
	    }
	    
	    @Override
	    public String toString() {
	    	switch (this) {
				case GET:
					return "GET";
				case PUT:
					return "PUT";
				case POST:
					return "POST";
				case DELETE:
					return "DELETE";
			}
	    	return super.toString();
	    }
}
