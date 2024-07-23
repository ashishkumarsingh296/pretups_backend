package com.btsl.pretups.restrictedsubs.businesslogic;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;

/**
 * This interface provides method declaration of Schedule Recharge
 * @author lalit.chattar
 *
 */
@Path("/file-processor")
public interface BatchScheduleRechargeRestService {
	
	
	public String[] FILE_HEADER_KEYS = new String[] {
			"restrictedsubs.scheduletopupdetails.file.heading",
			"restrictedsubs.scheduletopupdetails.file.customermsisdn.heading",
			"restrictedsubs.scheduletopupdetails.file.pstnmsisdn.heading",
			"restrictedsubs.scheduletopupdetails.file.internatemsisdn.heading" };
	
	public String[] DATA_ERROR_KEY = {
			"restrictedsubs.scheduletopupdetails.errorlog.msg.blankrow",
			"restrictedsubs.scheduletopupdetails.errorlog.msg.msisdnnull",
			"restrictedsubs.scheduletopupdetails.errorlog.msg.msisdnduplicate",
			"restrictedsubs.scheduletopupdetails.errorlog.msg.novaliddatafound",
			"restrictedsubs.scheduletopupdetails.errorlog.msg.novaliddatafound.in.the.file"};
	
	public String[] ERROR_LOG_CONSTANTS = {
			"restrictedsubs.scheduletopupdetails.errorlog.msg.linenumber",
			"restrictedsubs.scheduletopupdetails.errorlog.msg.msisdn",
			"restrictedsubs.scheduletopupdetails.errorlog.msg.failuerreason" };
	
	public String[] UPLOAD_ERROR_KEYS = new String[] {
			"restrictedsubs.scheduletopupdetails.errorfile.msg.invalidmsisdn",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.networkprefixnotfound",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.networknotsupport",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.noinfo",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.subservicenotfound",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.reqamtnotfound",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.subserviceinvalid",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.reqamtinvalid",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.amtnotinrange",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.notassociated",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.alreadyscheduled",
			"restrictedsubs.scheduletopupdetails.msg.novaliddatainfile",
			"restrictedsubs.rescheduletopupdetails.msg.novaliddatainfile",
			"restrictedsubs.scheduletopupdetails.msg.invalidcorpfiletype",
			"restrictedsubs.scheduletopupdetails.msg.unsuccess",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.languagecodeinvalid",
			"restrictedsubs.scheduletopupdetails.msg.invalidfiletype",
			"restrictedsubs.associatesubscriberdetails.msg.novaliddatainfile",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.invalidreceivermsisdn",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.invalidgiftermsisdn",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.networkprefixnotfoundreceiver",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.networkprefixnotfoundgifter",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.networknotsupportreceiver",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.networknotsupportgifter",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.languagecodeinvalidreceiver",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.languagecodeinvalidgifter",
			"restrictedsubs.scheduletopupdetails.msg.invaliGRCfiletype",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.invalidgiftername",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.gifternamenotfound",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.gifterreceivernotsame",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.receivermsisdnnull",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.giftermsisdnnull",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.languagecodenull",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.notificationMsisdnnull",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.notPstnSeries",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.invalidnotificationmsisdn",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.notGsmSeries",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.notInternateSeries",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.request.amount.not.provided",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.sub.service.not.provided"};
	
	
	public String[] FILE_COLUMN_HEADER_KEYS = new String[] {
			"restrictedsubs.scheduletopupdetails.file.label.msisdn",
			"restrictedsubs.scheduletopupdetails.file.label.subscriberid",
			"restrictedsubs.scheduletopupdetails.file.label.subscribername",
			"restrictedsubs.scheduletopupdetails.file.label.mintxnamt",
			"restrictedsubs.scheduletopupdetails.file.label.maxtxnamt",
			"restrictedsubs.scheduletopupdetails.file.label.monthlimit",
			"restrictedsubs.scheduletopupdetails.file.label.usedlimit",
			"restrictedsubs.scheduletopupdetails.file.label.subservice",
			"restrictedsubs.scheduletopupdetails.file.label.reqamt",
			"restrictedsubs.scheduletopupdetails.file.label.languagecode",
			"restrictedsubs.scheduletopupdetails.file.label.receiverlanguage",
			"restrictedsubs.scheduletopupdetails.file.label.giftermsisdn",
			"restrictedsubs.scheduletopupdetails.file.label.giftername",
			"restrictedsubs.scheduletopupdetails.file.label.gifterlanguage",
			"restrictedsubs.scheduletopupdetails.file.label.notificationMsisdn"};

	
	

	/**
	 * Provide functionality of downloading template of schedule recharge
	 * @param requestData
	 * @return PretupsResponse
	 * @throws BTSLBaseException
	 */
	@POST
	@Path("/schedule-recharge-template")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<String> downloadScheduleRechargeTemplate(String requestData) throws BTSLBaseException;
	
	
	/**
	 * process uploaded file for schedule recharge
	 * @param requestData
	 * @return PretupsResponse
	 * @throws BTSLBaseException
	 */
	@POST
	@Path("/process-schedule-recharge-uploded-file")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<Object> processUplodedScheduleRechargeFile(String requestData) throws BTSLBaseException;
}
