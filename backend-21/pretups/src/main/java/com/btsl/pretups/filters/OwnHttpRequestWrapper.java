package com.btsl.pretups.filters;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringUtils;

/**
 * @author tarun.kumar
 *  Security_SQL Injection
 */
public class OwnHttpRequestWrapper extends HttpServletRequestWrapper {

	private Map<String, String[]> escapedParametersValuesMap = new HashMap<String, String[]>();

	/**
	 * @param req
	 */
	public OwnHttpRequestWrapper(HttpServletRequest req) {

		super(req);

	}

	@Override
	public String getParameter(String name) {

		String[] escapedParameterValues = escapedParametersValuesMap.get(name);
		String escapedParameterValue = null;
		if (escapedParameterValues != null) {
			escapedParameterValue = escapedParameterValues[0];
		} else {
			String parameterValue = super.getParameter(name);
			// HTML transformation characters
			escapedParameterValue = org.springframework.web.util.HtmlUtils.htmlEscape(parameterValue);
			// SQL injection characters		
			escapedParameterValue = StringUtils.replace(escapedParameterValue, "'", "''");
			escapedParametersValuesMap.put(name,new String[] { escapedParameterValue });
		}
		return escapedParameterValue;
	}

	@Override
	public String[] getParameterValues(String name) {
		
		String[] escapedParameterValues = escapedParametersValuesMap.get(name);
		if (escapedParameterValues == null) {
			String[] parametersValues = super.getParameterValues(name);
			 escapedParameterValues = new String[parametersValues.length];		
			String escapedParameterValue=null;
			for (int i = 0; i < parametersValues.length; i++) {
				String parameterValue = parametersValues[i];
				 escapedParameterValue = parameterValue;
				// HTML transformation characters
				escapedParameterValue = org.springframework.web.util.HtmlUtils.htmlEscape(parameterValue);
				// SQL injection characters
				escapedParameterValue = StringUtils.replace(escapedParameterValue, "'", "''");
				escapedParameterValues[i] = escapedParameterValue;
			}
			escapedParametersValuesMap.put(name, escapedParameterValues);
		}
		return escapedParameterValues;
	}
}
