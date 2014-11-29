
package com.itdoors.haccp.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.itdoors.haccp.Constants;
import com.itdoors.haccp.exceptions.rest.AuthenticationFailureException;
import com.itdoors.haccp.exceptions.rest.DeviceConnectionException;
import com.itdoors.haccp.exceptions.rest.NetworkSystemException;
import com.itdoors.haccp.exceptions.rest.WebServiceFailedException;
import com.itdoors.haccp.parser.rest.Parser;
import com.itdoors.haccp.parser.rest.Responce;
import com.itdoors.haccp.sync.SyncUtils;
import com.itdoors.haccp.utils.Logger;

public abstract class BaseRESTCommand implements RESTCommand {

    private static final String TAG = "RESTCommand";

    private static final int CONNECTION_TIMEOUT = 30 * 1000;
    protected static HttpClient mHttpClient;

    protected final Context mContext;

    protected HttpMethod mMethod;
    protected Uri mUri;
    protected Bundle mParams;
    protected Parser mParser;

    protected long mRequestId;

    public BaseRESTCommand(Context context, HttpMethod method, Uri uri, Bundle params,
            Parser parser, long requestId) {

        this.mContext = context;
        mMethod = method;
        mUri = uri;
        mParams = params;
        mParser = parser;
        mRequestId = requestId;

    }

    protected static void createHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient();
            final HttpParams params = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params,
                    CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT);
            ConnManagerParams.setTimeout(params, CONNECTION_TIMEOUT);
        }
    }

    protected static List<BasicNameValuePair> paramsToList(Bundle params) {
        ArrayList<BasicNameValuePair> formList = new ArrayList<BasicNameValuePair>(params.size());

        for (String key : params.keySet()) {
            Object value = params.get(key);

            // We can only put Strings in a form entity, so we call the
            // toString()
            // method to enforce. We also probably don't need to check for null
            // here
            // but we do anyway because Bundle.get() can return null.
            if (value != null)
                formList.add(new BasicNameValuePair(key, value.toString()));
        }

        return formList;
    }

    /**
     * Get the authToken used to authenticate the request to the REST API
     * 
     * @return The authToken.
     * @throws DeviceConnectionException Network connection is not available.
     * @throws AuthenticationFailureException Failed to authenticate the
     *             request. Probably due to invalid credentials.
     */

    /*
     * protected String getAuthToken() throws DeviceConnectionException,
     * AuthenticationFailureException { Bundle authBundle; String authToken; try
     * { authBundle =
     * AuthenticationHelper.getInstance().getAuthTokenInBackground( null, null,
     * false ); authToken = authBundle.getString( AccountManager.KEY_AUTHTOKEN
     * ); } catch ( IOException e ) { String msg =
     * "Authentication failed: Cannot connect to network."; Logger.Logi(TAG,
     * msg, e); throw new DeviceConnectionException(msg, e); } catch (
     * AuthenticatorException e ) { String msg =
     * "Authentication failed: Invalid credentials."; Logger.Logi(TAG, msg, e);
     * throw new AuthenticationFailureException( msg ); } return authToken; }
     */

    protected String getAuthToken() {
        return SyncUtils.getAccessToken(mContext);
    }

    @Override
    public int execute()
            throws DeviceConnectionException,
            NetworkSystemException,
            WebServiceFailedException,
            AuthenticationFailureException
    {
        String authToken = getAuthToken();
        Logger.Logi(getClass(), "execute with authToken:" + authToken);
        return handleRequest(authToken);
    }

    @Override
    public void handleNotFound() {
    }

    @Override
    public int handleRequest(String authToken) throws DeviceConnectionException,
            NetworkSystemException, WebServiceFailedException {

        Logger.Logi(getClass(), "handleRequest");

        HttpResponse httpResponce;
        Responce mResponce;

        int statusCode;
        String respText;

        HttpRequestBase request = null;

        Logger.Logi(getClass(), "perform request to server with uri: " + mUri.toString() + ","
                + "and params: " + ((mParams == null) ? "null" : mParams.toString()));
        try {

            request = createHttpRequest(authToken, mMethod, mUri, mParams);

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            String msg = "Cannot create UrlEncodedFormEntity.";
            Logger.Loge(getClass(), msg);

            throw new NetworkSystemException(msg);

        } catch (URISyntaxException e) {

            e.printStackTrace();
            String msg = "Cannot create UrlEncodedFormEntity.";
            Logger.Loge(getClass(), msg);
            throw new NetworkSystemException(msg);

        }

        createHttpClient();

        try {

            httpResponce = mHttpClient.execute(request);

        } catch (IOException e) {

            String msg = "POST method failed: Cannot connect to network.";
            Logger.Logi(TAG, msg, e);
            throw new DeviceConnectionException(msg, e);

        }

        statusCode = httpResponce.getStatusLine().getStatusCode();

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {

            Logger.Logi(getClass(), "statusCode:" + statusCode);
            try {

                respText = EntityUtils.toString(httpResponce.getEntity());
                Logger.Logi(getClass(), "server responce entity: " + respText);

            } catch (IOException e) {

                String msg = "POST method failed: Invalid response.";
                Logger.Loge(TAG, msg, e);
                throw new WebServiceFailedException(msg, e);
            }

            try {

                mResponce = mParser.parse(respText);
                mResponce.setHttpStatusCode(statusCode);
                mResponce.setRequestId(mRequestId);

                Logger.Logi(getClass(), "parsed responce: " + mResponce.toString());
            } catch (JSONException e) {
                String msg =
                        "POST method failed: Cannot parse data returned from web service.";
                Logger.Loge(TAG, msg);
                throw new WebServiceFailedException(msg);
            }

            Processor processor = Processor.getInstance(mContext);
            processor.handleResponce(mResponce);

        }

        return statusCode;
    }

    private static HttpRequestBase createHttpRequest(String authToken, HttpMethod method, Uri uri,
            Bundle params) throws UnsupportedEncodingException, URISyntaxException {

        HttpRequestBase request = null;

        params.putString(Constants.ACCESS_TOKEN_PARM, authToken);

        Logger.Logd(BaseRESTCommand.class, "method:" + method.name());
        Logger.Logd(BaseRESTCommand.class, "uri:" + uri);
        Logger.Logd(BaseRESTCommand.class, "params:" + params.toString());

        switch (method) {
            case GET: {
                request = new HttpGet();
                attachUriWithQuery(request, uri, params);
            }
                break;
            case DELETE: {
                request = new HttpDelete();
                attachUriWithQuery(request, uri, params);
            }
                break;
            case POST: {
                request = new HttpPost();
                request.setURI(new URI(uri.toString()));

                HttpPost postRequest = (HttpPost) request;

                if (params != null) {
                    UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsToList(params));
                    postRequest.setEntity(formEntity);
                    postRequest.addHeader(formEntity.getContentType());
                }
            }
                break;

            case PUT: {
                request = new HttpPut();
                request.setURI(new URI(uri.toString()));

                // Attach form entity if necessary.
                HttpPut putRequest = (HttpPut) request;

                if (params != null) {

                    UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsToList(params));
                    putRequest.setEntity(formEntity);
                    putRequest.addHeader(formEntity.getContentType());
                }
            }
                break;
        }
        /*
         * request.addHeader( WebApiConstants.HEADER_ACCESS_TOKEN_PARM,
         * WebApiConstants.HEADER_TOKEN_PREFIX + authToken );
         */
        return request;

    }

    private static void attachUriWithQuery(HttpRequestBase request, Uri uri, Bundle params) {
        try {
            if (params == null) {
                // No params were given or they have already been
                // attached to the Uri.
                request.setURI(new URI(uri.toString()));
            }
            else {
                Uri.Builder uriBuilder = uri.buildUpon();

                // Loop through our params and append them to the Uri.
                for (BasicNameValuePair param : paramsToList(params)) {
                    uriBuilder.appendQueryParameter(param.getName(), param.getValue());
                }

                uri = uriBuilder.build();
                request.setURI(new URI(uri.toString()));
            }
        } catch (URISyntaxException e) {
            Log.e(TAG, "URI syntax was incorrect: " + uri.toString());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append("method:" + mMethod.toString()).append(",");
        sb.append("uri:" + mUri.toString()).append(",");
        sb.append("params:" + mParams.toString()).append(",");
        sb.append("parser:" + mParser.getClass().getSimpleName());
        sb.append("]");

        return sb.toString();
    }
}
