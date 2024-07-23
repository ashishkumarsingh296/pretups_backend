package com.security;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.utils.Log;

public class SecurityHelper {

	public Map<String, List<String>> getHTTPRawResponse(String URL) throws IOException {
		
		Log.info("<pre><b>Request URL: </b>" + URL + "</pre>");
		
		HttpURLConnection httpURLConnection;
		URL url = new URL(URL);
		httpURLConnection = (HttpURLConnection) url.openConnection();
		StringBuilder builder = new StringBuilder();
		builder.append(httpURLConnection.getResponseCode())
		       .append(" ")
		       .append(httpURLConnection.getResponseMessage())
		       .append("\n");
		
		Map<String, List<String>> map = httpURLConnection.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : map.entrySet())
		{
		    if (entry.getKey() == null) 
		        continue;
		    builder.append( entry.getKey())
		           .append(": ");

		    List<String> headerValues = entry.getValue();
		    Iterator<String> it = headerValues.iterator();
		    if (it.hasNext()) {
		        builder.append(it.next());

		        while (it.hasNext()) {
		            builder.append(", ")
		                   .append(it.next());
		        }
		    }

		    builder.append("\n");
		}
		
		Log.info("<pre><b>Raw Response:</b><br>" + builder.toString() + "</pre>");
		
		return map;
	}
}
