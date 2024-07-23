package com.btsl.pretups.restrictedsubs.businesslogic;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.validator.ValidatorException;
import org.springframework.expression.ParseException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.restrictedsubs.web.RestrictedSubscriberModel;

@Path("/ScheduleRecharge")
public interface ScheduleRechargeRestService {
	
	
	@POST
	@Path("/view-Cancel-ScehduleSubs")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<RestrictedSubscriberModel> viewCancelScehduleSubs(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException;

	@POST
	@Path("/load-Details-For-Single")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<RestrictedSubscriberModel> loadDetailsForSingle(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException,ParseException;

	@POST
	@Path("/delete-Details-For-Selected")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<RestrictedSubscriberModel> deleteDetailsForSelectedMsisdn(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException,ParseException;

	@POST
	@Path("/load-users-batch-recharge")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<UserVO>> loadUsersBatchRecharge(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException;
	
	@POST
	@Path("/cancel-batch")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<RestrictedSubscriberModel> cancelSelectedBatch(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException;

}
