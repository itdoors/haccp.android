package com.itdoors.haccp.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * Utility methods for retrieving content over HTTP using the more-supported {@code java.net} classes
 * in Android.
 */
public final class HttpHelper {
	
  private static final int IO_BUFFER_SIZE = 8 * 1024;
  private static final String TAG = HttpHelper.class.getSimpleName();

  private static final Collection<String> REDIRECTOR_DOMAINS = new HashSet<String>(Arrays.asList(
    "amzn.to", "bit.ly", "bitly.com", "fb.me", "goo.gl", "is.gd", "j.mp", "lnkd.in", "ow.ly",
    "R.BEETAGG.COM", "r.beetagg.com", "SCN.BY", "su.pr", "t.co", "tinyurl.com", "tr.im"
  ));

  private HttpHelper() {
  }
  
  public enum ContentType {
    /** HTML-like content type, including HTML, XHTML, etc. */
    HTML,
    /** JSON content */
    JSON,
    /** XML */
    XML,
    /** Plain text content */
    TEXT,
    /** ZIPPED */
    GZIP
  }


  public static void downloadViaHttp(String uri, File file, ContentType type) throws IOException {
	  
	  String contentTypes;
	    switch (type) {
	      case HTML:
	        contentTypes = "application/xhtml+xml,text/html,text/*,*/*";
	        break;
	      case JSON:
	        contentTypes = "application/json,text/*,*/*";
	        break;
	      case XML:
	        contentTypes = "application/xml,text/*,*/*";
	        break;
	      case GZIP:
	        contentTypes = "application/gzip,text/*,*/*";
	        break;
	      case TEXT:
	      default:
	        contentTypes = "text/*,*/*";
	    }
	    downloadViaHttp(uri, file, contentTypes);
  }
  
  public static void downloadViaHttp(String uri, File file, String contentTypes) throws IOException {
	    
	  	int redirects = 0;
	    while (redirects < 5) {
	      URL url = new URL(uri);
	      HttpURLConnection connection = safelyOpenConnection(url);
	      connection.setInstanceFollowRedirects(true); // Won't work HTTP -> HTTPS or vice versa
	      connection.setRequestProperty("Accept", contentTypes);
	      connection.setRequestProperty("Accept-Charset", "utf-8,*");
	      try {
	        int responseCode = safelyConnect(uri, connection);
	        switch (responseCode) {
	          case HttpURLConnection.HTTP_OK:
	        	  
	        	  int fileLength = connection.getContentLength();
	        	  downloadConnectionToStream(connection, new FileOutputStream(file));
	        	  return;
	        	  
	          case HttpURLConnection.HTTP_MOVED_TEMP:
	            String location = connection.getHeaderField("Location");
	            if (location != null) {
	              uri = location;
	              redirects++;
	              continue;
	            }
	            throw new IOException("No Location");
	          default:
	            throw new IOException("Bad HTTP response: " + responseCode);
	        }
	      } finally {
	        connection.disconnect();
	      }
	    }
	    throw new IOException("Too many redirects");
}
  
  public static String getEncoding(URLConnection connection) {
    String contentTypeHeader = connection.getHeaderField("Content-Type");
    if (contentTypeHeader != null) {
      int charsetStart = contentTypeHeader.indexOf("charset=");
      if (charsetStart >= 0) {
        return contentTypeHeader.substring(charsetStart + "charset=".length());
      }
    }
    return "UTF-8";
  }

 
  
  /**
   * Download a content from a URL and write the content to an output stream.
   *
   * @param urlString The URL to fetch
   * @return true if successful, false otherwise
 * @throws IOException 
   */
  public static boolean downloadConnectionToStream(HttpURLConnection connection, OutputStream outputStream) throws IOException {
    
	  BufferedOutputStream out = null;
      BufferedInputStream in = null;

      try {
          in = new BufferedInputStream(connection.getInputStream(), IO_BUFFER_SIZE);
          out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

          int b;
          while ((b = in.read()) != -1) {
              out.write(b);
          }
          return true;
      
      } finally {
          if (connection != null) {
              connection.disconnect();
          }
          try {
              if (out != null) {
                  out.close();
              }
              if (in != null) {
                  in.close();
              }
          } catch (final IOException e) {}
      }
     
  }
  
  public static URI unredirect(URI uri) throws IOException {
    if (!REDIRECTOR_DOMAINS.contains(uri.getHost())) {
      return uri;
    }
    URL url = uri.toURL();
    HttpURLConnection connection = safelyOpenConnection(url);
    connection.setInstanceFollowRedirects(false);
    connection.setDoInput(false);
    connection.setRequestMethod("HEAD");
    connection.setRequestProperty("User-Agent", "ZXing (Android)");
    try {
      int responseCode = safelyConnect(uri.toString(), connection);
      switch (responseCode) {
        case HttpURLConnection.HTTP_MULT_CHOICE:
        case HttpURLConnection.HTTP_MOVED_PERM:
        case HttpURLConnection.HTTP_MOVED_TEMP:
        case HttpURLConnection.HTTP_SEE_OTHER:
        case 307: // No constant for 307 Temporary Redirect ?
          String location = connection.getHeaderField("Location");
          if (location != null) {
            try {
              return new URI(location);
            } catch (URISyntaxException e) {
              // nevermind
            }
          }
      }
      return uri;
    } finally {
      connection.disconnect();
    }
  }
  
  private static HttpURLConnection safelyOpenConnection(URL url) throws IOException {
    URLConnection conn;
    try {
      conn = url.openConnection();
    } catch (NullPointerException npe) {
      // Another strange bug in Android?
      Log.w(TAG, "Bad URI? " + url);
      throw new IOException(npe.toString());
    }
    if (!(conn instanceof HttpURLConnection)) {
      throw new IOException();
    }
    return (HttpURLConnection) conn;
  }

  private static int safelyConnect(String uri, HttpURLConnection connection) throws IOException {
    try {
      connection.connect();
    } catch (NullPointerException npe) {
      // this is an Android bug: http://code.google.com/p/android/issues/detail?id=16895
      Log.w(TAG, "Bad URI? " + uri);
      throw new IOException(npe.toString());
    } catch (IllegalArgumentException iae) {
      // Also seen this in the wild, not sure what to make of it. Probably a bad URL
      Log.w(TAG, "Bad URI? " + uri);
      throw new IOException(iae.toString());
    } catch (SecurityException se) {
      // due to bad VPN settings?
      Log.w(TAG, "Restricted URI? " + uri);
      throw new IOException(se.toString());
    } catch (IndexOutOfBoundsException ioobe) {
      // Another Android problem? https://groups.google.com/forum/?fromgroups#!topic/google-admob-ads-sdk/U-WfmYa9or0
      Log.w(TAG, "Bad URI? " + uri);
      throw new IOException(ioobe.toString());
    }
    try {
      return connection.getResponseCode();
    } catch (NullPointerException npe) {
      // this is maybe this Android bug: http://code.google.com/p/android/issues/detail?id=15554
      Log.w(TAG, "Bad URI? " + uri);
      throw new IOException(npe.toString());
    } catch (IllegalArgumentException iae) {
      // Again seen this in the wild for bad header fields in the server response! or bad reads
      Log.w(TAG, "Bad server status? " + uri);
      throw new IOException(iae.toString());
    }
  }

}
