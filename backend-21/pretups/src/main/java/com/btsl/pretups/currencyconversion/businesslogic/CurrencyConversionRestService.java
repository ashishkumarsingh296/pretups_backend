package com.btsl.pretups.currencyconversion.businesslogic;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;

/*
 * Interface which provides base for BarredUserRestServiceImpl class
 * also declares different service url with method type and  for Bar User functionalities

 */


@Path("/currency-conversion")
public interface CurrencyConversionRestService {

	@POST
	@Path("/loadDetails")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<CurrencyConversionVO>> loadDetails(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException;
	
	@POST
	@Path("/updateDetails")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<CurrencyConversionVO>> updateDetails(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException;
	

}
