package com.btsl.common;

import java.io.IOException;
import java.util.List;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONCommonConverter {
	private static final Log LOG = LogFactory.getLog(JSONCommonConverter.class
			.getName());

	public static final ObjectMapper mapper = new ObjectMapper();

	public JSONCommonConverter() {
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	}

	public String convertObjectToString(Object obj) {

		if (LOG.isDebugEnabled())
			LOG.debug("convertObjectToString", "Entered");

		String jsonString = "";

		ObjectWriter obw = mapper.writer().withDefaultPrettyPrinter();
		try {
			jsonString = obw.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			LOG.errorTrace("convertObjectToString", e);
		}

		LOG.info("Resultant JSON string is ", jsonString);
		return jsonString;
	}

	public Object convertStringToObject(String jsonString, Object type) {

		if (LOG.isDebugEnabled())
			LOG.debug("convertStringToObject", "Entered");
		Object obj = null;
		ObjectReader obr = mapper
				.readerFor(new TypeReference<List<InterfaceVO>>() {
				});

		try {
			obj = obr.readValue(jsonString);
		} catch (JsonProcessingException e) {

			LOG.errorTrace("convertStringToObject", e);
		} catch (IOException e) {

			LOG.errorTrace("convertStringToObject", e);
		}
		return obj;
	}

	public Object convertStringToObjectIn(String jsonString, Object type) {
		if (LOG.isDebugEnabled())
			LOG.debug("convertStringToObject", "Entered");
		Object obj = null;

		ObjectReader obr = mapper.readerFor(new TypeReference<InterfaceVO>() {
		});

		try {
			obj = obr.readValue(jsonString);
		} catch (JsonProcessingException e) {

			LOG.errorTrace("convertStringToObjectIn", e);
		} catch (IOException e) {

			LOG.errorTrace("convertStringToObjectIn", e);
		}
		return obj;
	}
}
