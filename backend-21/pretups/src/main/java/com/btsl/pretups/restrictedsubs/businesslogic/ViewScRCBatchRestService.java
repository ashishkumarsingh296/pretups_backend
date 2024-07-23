package com.btsl.pretups.restrictedsubs.businesslogic;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;

/*
 * Interface which provides base for BarredUserRestServiceImpl class
 * also declares different service url with method type and  for Bar User functionalities

 */


@Path("/schedulerc")
public interface ViewScRCBatchRestService {

	@POST
	@Path("/view-schedulerc")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<ScheduleBatchMasterVO>> viewSCRCBatch(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException, ParseException;


	@POST
	@Path("/view-schedulerc1")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<ListValueVO>> loaddropdownforViewSCRBatch(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException, ParseException;

	@POST
	@Path("/view-schedulerc-link")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<LinkedHashMap> loadScheduleBatchDetailsList(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException, ParseException;

	@POST
	@Path("/view-schedulerc-link-detail")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<LinkedHashMap> loadScheduleBatchDetailsMap(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException, ParseException;
	
}
