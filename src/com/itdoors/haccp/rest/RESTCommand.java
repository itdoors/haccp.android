package com.itdoors.haccp.rest;

import com.itdoors.haccp.exceptions.rest.AuthenticationFailureException;
import com.itdoors.haccp.exceptions.rest.DeviceConnectionException;
import com.itdoors.haccp.exceptions.rest.NetworkSystemException;
import com.itdoors.haccp.exceptions.rest.WebServiceFailedException;

public interface RESTCommand {
    
    /**
     * Implemented by concrete command classes to handle the request and response
     * specific to a particular to REST method type.
     * 
     * @param authToken Used to authenticate the request to the REST API.
     * @return The HttpStatus code.
     * @throws DeviceConnectionException Network connection is not available.
     * @throws NetworkSystemException Error configuring the network connection.
     * @throws WebServiceFailedException Error configuring the http request or
     * an invalid json response has been returned.
     */
    public int handleRequest( String authToken )
        throws DeviceConnectionException,
               NetworkSystemException, 
               WebServiceFailedException;
 
    /**
     * This method should be overwritten by concrete classes to handle
     * REST method specific logic for a resource not found by the
     * REST API in the web service.
     */
    public void handleNotFound();
     
    /**
     * This method must be overwritten by concrete classes to handle
     * REST method specific logic for HttpStatus error codes returned by 
     * the REST API.
     */
    public boolean handleError( int httpResult, boolean allowRetry );
    
    
    /**
     * Get the authToken and pass it to handleRequest().
     * 
     * @return The HttpStatus code.
     * @throws DeviceConnectionException Network connection is not available.
     * @throws NetworkSystemException Error configuring the network connection.
     * @throws WebServiceFailedException Error configuring the http request or
     * an invalid json response has been returned.
     * @throws AuthenticationFailureException Failed to authenticate the request.
     */
    public int execute() 
        throws DeviceConnectionException,
               NetworkSystemException,
               WebServiceFailedException,
               AuthenticationFailureException;
               

}

