package com.btsl.pretups.restrictedsubs.businesslogic;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.apache.commons.validator.ValidatorException;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.PretupsResponse;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.WebServiceKeywordCacheVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.web.pretups.restrictedsubs.web.RestrictedSubscriberModel;

/**
 * @author jashobanta.mahapatra
 * This class validates batch re-charge reschedule request
 */
public class BatchRechargeRescheduleValidator {
	
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private static final String className = "BatchRechargeRescheduleValidator";
	
	/**
	 * @param loginId
	 * @param userID
	 * @param batchID
	 * @param serviceTypeCode
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public boolean isValidBatchID(String loginId,String userID, String batchID ,String serviceTypeCode){
		String methodName = className+":isValidBatchID";
		log.debug(methodName, PretupsI.ENTERED);
		boolean isValidBatchID = false;
		if(null == batchID)
			return isValidBatchID;
		log.info(methodName, " batchID:"+batchID+",serviceTypeCode:"+serviceTypeCode+",userID:"+userID+",loginId:"+loginId);
		int numberOfDays = 0;
		try{
			numberOfDays = Integer.parseInt(Constants.getProperty("RESCHEDULE_BATCH_BACK_DAYS"));
		} catch (Exception e) {
			log.errorTrace(methodName +  "RESCHEDULE_BATCH_BACK_DAYS is not defined in constants.props file e=", e);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		try{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			final UserDAO userDAO = new UserDAO();
			final ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
			final ChannelUserVO channelUserVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			Date newDate = BTSLUtil.addDaysInUtilDate(new Date(), numberOfDays * -1);  
			ArrayList<ScheduleBatchMasterVO> scheduleList = (ArrayList<ScheduleBatchMasterVO>)scheduledBatchDetailDAO.loadScheduleBatchMasterList(con, userID, PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_SCHEDULED, newDate, null, null, serviceTypeCode, channelUserVO.isStaffUser(), channelUserVO.getActiveUserID());
			for(ScheduleBatchMasterVO scheduleBatchMasterVO : scheduleList){
				if(batchID.equals(scheduleBatchMasterVO.getBatchID())){
					isValidBatchID = true;
					break;
				}
			}
		}
		catch(BTSLBaseException | SQLException  ex){
			OracleUtil.rollbackConnection(con, className, methodName);
			log.errorTrace(methodName , ex);
		}
		finally{
			if (mcomCon != null) {
				mcomCon.close("BatchRechargeRescheduleValidator#isValidBatchID");
				mcomCon = null;
			}
		}
		log.info(methodName, " is Valid BatchID : "+isValidBatchID);
		log.debug(methodName, PretupsI.EXITED);
		return isValidBatchID;
	}
	
	
	/**
	 * @param uploadedFile
	 * @param bindingResult
	 * @return
	 * @throws BTSLBaseException
	 * This method validate batch file before proceeding to REST
	 */
	public boolean isValidBatchFile(MultipartFile uploadedFile, BindingResult bindingResult) throws BTSLBaseException{
		String methodName = className+":isValidBatchFile";
		log.debug(methodName, PretupsI.ENTERED);
		boolean isValidBatchFile = false;
		String bindingAttributeName = "multipartFile";
		String fileName = uploadedFile.getOriginalFilename();
		String rootPath = Constants.getProperty("UploadRestrictedMSISDNFilePath");
		//uploadfile.error.fileexists
		File dir = new File(rootPath + File.separator + fileName);
		if(dir.exists()) {
			bindingResult.rejectValue(bindingAttributeName , "uploadfile.error.fileexists");
			return isValidBatchFile;
		}
		//uploadfile.error.filesizezero
		if(uploadedFile.getSize() <= 0){
			bindingResult.rejectValue(bindingAttributeName , "uploadfile.error.filesizezero");
			return isValidBatchFile;
		}
			
		log.debug(methodName, "is Valid Batch File :"+isValidBatchFile);
		log.debug(methodName, PretupsI.EXITED);
		return true;
	}


	public boolean validateLoadBatchListRequest(RestrictedSubscriberModel model , String type, PretupsResponse<RestrictedSubscriberModel> pretupsResponse) throws ValidatorException, IOException, SAXException {
		WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache.getWebServiceTypeObject(type);
		String validationXMLpath = webServiceKeywordCacheVO.getValidatorName();
		CommonValidator commonValidator = new CommonValidator(validationXMLpath, model, "LoadBatchListRequest");
		Map<String, String> errorMessages = commonValidator.validateModel();
		pretupsResponse.setFieldError(errorMessages);
		return errorMessages.isEmpty();
	}
	
	public boolean validateBatchFileCreateRequest(RestrictedSubscriberModel model , String type, PretupsResponse<RestrictedSubscriberModel> pretupsResponse) throws ValidatorException, IOException, SAXException {
		WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache.getWebServiceTypeObject(type);
		String validationXMLpath = webServiceKeywordCacheVO.getValidatorName();
		CommonValidator commonValidator = new CommonValidator(validationXMLpath, model, "BatchFileCreateRequest");
		Map<String, String> errorMessages = commonValidator.validateModel();
		pretupsResponse.setFieldError(errorMessages);
		return errorMessages.isEmpty();
	}
	
	public boolean validateProcessRescheduleBatchFileRequest(RestrictedSubscriberModel model , String type, PretupsResponse<Object> pretupsResponse) throws ValidatorException, IOException, SAXException, ParseException {
		WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache.getWebServiceTypeObject(type);
		String validationXMLpath = webServiceKeywordCacheVO.getValidatorName();
		CommonValidator commonValidator = new CommonValidator(validationXMLpath, model, "ProcessRescheduleBatchFileRequest");
		Map<String, String> errorMessages = commonValidator.validateModel();
		pretupsResponse.setFieldError(errorMessages);
		if(!BTSLUtil.isNullString(model.getScheduleDate())){
			if(BTSLUtil.isDateBeforeToday(BTSLUtil.getDateFromDateString(model.getScheduleDate(), PretupsI.DATE_FORMAT)))
				errorMessages.put("scheduleDate", "restrictedsubs.scheduletopupdetails.schedule.date.should.not.be.past.date");
		}
		return errorMessages.isEmpty();
	}


}
