package com.btsl.pretups.restrictedsubs.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.restrictedsubs.businesslogic.BatchRechargeRescheduleValidator;
import com.btsl.pretups.restrictedsubs.businesslogic.ScheduledBatchDetailDAO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.web.pretups.restrictedsubs.web.RestrictedSubscriberModel;

/**
 * @author jashobanta.mahapatra
 * This class process all the requests which comes from Batch Re-charge Reschedule
 */
@Service("batchRechageRescheduleService")
public class BatchRechageRescheduleServiceImpl implements BatchRechageRescheduleService{

    private Log log = LogFactory.getLog(this.getClass().getName());
	private String className = "BatchRechageRescheduleServiceImpl";
	
	@Autowired
	private RestrictedSubscriberService restrictedSubscriberService;

	@Override
	@SuppressWarnings("unchecked")
	public void loadScheduledBatchList(RestrictedSubscriberModel restrictedTopUpForm , String loginID, ModelMap modelMap,BindingResult bindingResult) throws BTSLBaseException{
		String methodName = className+":loadScheduledBatchList";
		log.debug(methodName, PretupsI.ENTERED);
		try{
			restrictedTopUpForm.setMultipartFile(null);
			Map<String, Object> requestObject = new HashMap<>();
			requestObject.put(PretupsI.TYPE, PretupsI.LOADBATCHLIST);
			requestObject.put(PretupsI.LOGIN_ID, loginID);
			requestObject.put(PretupsI.DATA, restrictedTopUpForm);
			PretupsRestClient client = new PretupsRestClient();
			String responseString = client.postJSONRequest(requestObject, PretupsI.LOADBATCHLIST);
			PretupsResponse<RestrictedSubscriberModel> pretupsResponse = (PretupsResponse<RestrictedSubscriberModel>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<RestrictedSubscriberModel>>() {});
			
			if(!pretupsResponse.getStatus()){
				if(pretupsResponse.hasFormError())
					modelMap.addAttribute("fail",PretupsRestUtil.getMessageString(pretupsResponse.getFormError()));
				else if(pretupsResponse.hasGlobalError())
					modelMap.addAttribute("fail",PretupsRestUtil.getMessageString(pretupsResponse.getGlobalError()));
				else if(pretupsResponse.hasFieldError()){
					Map<String, String> fieldError = pretupsResponse.getFieldError();
					for (Map.Entry<String, String> entry : fieldError.entrySet()){
						bindingResult.rejectValue(entry.getKey() , entry.getValue());
					}
				}
			}
			else{
				RestrictedSubscriberModel restrictedTopUpForm1 = pretupsResponse.getDataObject();
				modelMap.put("restrictedSubscriberModel", restrictedTopUpForm1);
			}
		}
		catch (Exception e) {
			log.debug(methodName, e);
			throw new BTSLBaseException(e);
		}
		log.debug(methodName, PretupsI.EXITED);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void downloadBatchFile(RestrictedSubscriberModel thisForm, String loginID,ModelMap modelMap, HttpServletResponse response,BindingResult bindingResult) throws BTSLBaseException {
		String methodName = className+":downloadBatchFile";
		log.debug(methodName, PretupsI.ENTERED);
		FileInputStream fileInputStream = null;
		try{
			thisForm.setMultipartFile(null);
			Map<String, Object> requestObject = new HashMap<>();
			requestObject.put(PretupsI.TYPE, PretupsI.DWNLDBATCHFILE);
			requestObject.put(PretupsI.LOGIN_ID, loginID);
			requestObject.put(PretupsI.DATA, thisForm);
			PretupsRestClient client = new PretupsRestClient();
			String responseString = client.postJSONRequest(requestObject, PretupsI.DWNLDBATCHFILE);
			PretupsResponse<RestrictedSubscriberModel> pretupsResponse = (PretupsResponse<RestrictedSubscriberModel>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<RestrictedSubscriberModel>>() {});
			if(pretupsResponse.getStatus()){
				RestrictedSubscriberModel restrictedSubscriberModel = pretupsResponse.getDataObject();
				String downloadedPath = restrictedSubscriberModel.getDownloadFilePath();
				//download csv file from specified path
				response.setContentType("text/csv");  
				PrintWriter out = response.getWriter();  
				String fileName = FilenameUtils.getBaseName(downloadedPath)+".csv";
				response.setContentType("APPLICATION/OCTET-STREAM");   
				response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\"");   
				fileInputStream = new FileInputStream(downloadedPath);  
				int i;   
				while ((i=fileInputStream.read()) != -1) {  
					out.write(i);   
				}   
				out.close();   
	
			}
			else{
				if(pretupsResponse.hasFormError())
					modelMap.addAttribute("fail",PretupsRestUtil.getMessageString(pretupsResponse.getFormError()));
				else if(pretupsResponse.hasGlobalError())
					modelMap.addAttribute("fail",PretupsRestUtil.getMessageString(pretupsResponse.getGlobalError()));
				else if(pretupsResponse.hasFieldError()){
					Map<String, String> fieldError = pretupsResponse.getFieldError();
					for (Map.Entry<String, String> entry : fieldError.entrySet()){
						bindingResult.rejectValue(entry.getKey() , entry.getValue());
					}
					
				}
			}

		}
		catch (Exception e) {
		   	log.debug(methodName, e);
			throw new BTSLBaseException(e);
		}finally{
			try{
        		if (fileInputStream != null) {
        			fileInputStream.close();
                }
        	}catch(Exception e){
        		log.errorTrace(methodName, e);
        	}
			log.debug(methodName, PretupsI.EXITED);
		}
		
	}

	/* (non-Javadoc)
	 * @see com.btsl.pretups.restrictedsubs.service.BatchRechageRescheduleService#uploadAndProcessBatchReshedule(com.btsl.pretups.restrictedsubs.web.RestrictedSubscriberModel, java.lang.String, org.springframework.ui.ModelMap, org.springframework.validation.BindingResult)
	 */
	@Override
	public void uploadAndProcessBatchReshedule(RestrictedSubscriberModel thisForm,String loginID, ModelMap modelMap,BindingResult bindingResult) throws BTSLBaseException {
		//keep the batch file in the server
		boolean isUploadSucess = uploadBatchFile(thisForm, bindingResult);
		//process the batch file - send rest request
		if(isUploadSucess)
			processBatchReschdule(thisForm, loginID, modelMap, bindingResult);
	}
	
	
	/**
	 * @param thisForm
	 * @param userVO
	 */
	@SuppressWarnings("unchecked")
	public void loadScheduledBatchList(RestrictedSubscriberModel thisForm, UserVO userVO){
		String methodName = className+"loadScheduledBatchList";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		Connection con = null;
		MComConnectionI mcomCon = null;
		try{

			mcomCon = new MComConnection();
			try{con=mcomCon.getConnection();}catch(SQLException e){
				log.error(methodName, "SQLException : " + e.getMessage());
	        	log.errorTrace(methodName, e);
			}
			ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
			int numberOfDays = 0;
			try{
				numberOfDays = Integer.parseInt(Constants.getProperty("RESCHEDULE_BATCH_BACK_DAYS"));
			} catch (Exception e) {
				log.errorTrace(methodName +  "RESCHEDULE_BATCH_BACK_DAYS is not defined in constants.props file e=", e);
			}
			Date newDate = BTSLUtil.addDaysInUtilDate(new Date(), numberOfDays * -1);      
			List<ScheduleBatchMasterVO> scheduleList = (ArrayList<ScheduleBatchMasterVO>)scheduledBatchDetailDAO.loadScheduleBatchMasterList(con, thisForm.getUserID(), PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_SCHEDULED, newDate, null, null, thisForm.getServiceCode(),userVO.isStaffUser(), userVO.getActiveUserID());
			thisForm.setScheduleList(scheduleList);
			
			List<ListValueVO> frequencies = restrictedSubscriberService.loadFrequency();
			thisForm.setFrequency(frequencies);
		}
		catch ( BTSLBaseException e) {
		    	log.error(methodName, PretupsI.EXCEPTION + e);
		}
		finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
			if (mcomCon != null) {
				mcomCon.close("BatchRechageRescheduleServiceImpl#loadScheduledBatchList");
				mcomCon = null;
			}
		}
	}

	/**
	 * @param thisForm
	 * @param loginID
	 * @param modelMap
	 * @throws BTSLBaseException 
	 */
	@SuppressWarnings("unchecked")
	private void processBatchReschdule(RestrictedSubscriberModel thisForm, String loginID, ModelMap modelMap,BindingResult bindingResult) throws BTSLBaseException {
		String methodName = className+":processBatchReschdule";
		log.debug(methodName, PretupsI.ENTERED);
		try{
			thisForm.setMultipartFile(null);
			Map<String, Object> requestObject = new HashMap<>();
			requestObject.put(PretupsI.TYPE, PretupsI.PROCESSRESCHDL);
			requestObject.put(PretupsI.LOGIN_ID, loginID);
			requestObject.put(PretupsI.DATA, thisForm);
			PretupsRestClient client = new PretupsRestClient();
			String responseString = client.postJSONRequest(requestObject, PretupsI.PROCESSRESCHDL);
			PretupsResponse<Object> pretupsResponse = (PretupsResponse<Object>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<Object>>() {});
			if(pretupsResponse.getStatus()){
				modelMap.addAttribute("success",PretupsRestUtil.getMessageString(pretupsResponse.getSuccessMsg(), pretupsResponse.getParameters()));
			}
			else{
				if(pretupsResponse.hasFormError()){
					modelMap.addAttribute("fail",PretupsRestUtil.getMessageString(pretupsResponse.getFormError(), pretupsResponse.getParameters()));
					if(null != pretupsResponse.getDataObject())
						modelMap.addAttribute("fileLocation", BTSLUtil.encryptText(pretupsResponse.getDataObject().toString()));
				}
				else if(pretupsResponse.hasGlobalError())
					modelMap.addAttribute("fail",PretupsRestUtil.getMessageString(pretupsResponse.getGlobalError()));
				else if(pretupsResponse.hasFieldError()){
					Map<String, String> fieldError = pretupsResponse.getFieldError();
					for (Map.Entry<String, String> entry : fieldError.entrySet()){
						bindingResult.rejectValue(entry.getKey() , entry.getValue());
					}
				}
			}
		}
		catch (Exception e) {
			log.debug(methodName, e);
			throw new BTSLBaseException(e);
		}
		log.debug(methodName, PretupsI.EXITED);
	}

	/**
	 * @param uploadedFile
	 * @param bindingResult
	 * @throws BTSLBaseException
	 */
	private boolean uploadBatchFile(RestrictedSubscriberModel thisForm,BindingResult bindingResult) throws BTSLBaseException{
		String methodName = className+":uploadBatchFile";
		log.debug(methodName, PretupsI.ENTERED);
		MultipartFile uploadedFile = thisForm.getMultipartFile();
		BatchRechargeRescheduleValidator validator = new BatchRechargeRescheduleValidator();
		if(!validator.isValidBatchFile(uploadedFile, bindingResult)){
			log.debug(methodName, "Invalid batch file: "+bindingResult.getFieldError().getCode());
			return false;
		}
		BufferedOutputStream stream = null;
		try {
			String rootPath = Constants.getProperty("UploadRestrictedMSISDNFilePath");
			File dir = new File(rootPath);
			byte[] bytes = uploadedFile.getBytes();
			// Creating the directory to store file
			if (!dir.exists())
				dir.mkdirs();
			// Create the file on server
			String fileName = uploadedFile.getOriginalFilename();
			File serverFile = new File(dir.getAbsolutePath() + File.separator + fileName);
			stream = new BufferedOutputStream( new FileOutputStream(serverFile));
			stream.write(bytes);
			stream.close();
			thisForm.setUploadFilePath(serverFile.getAbsolutePath());
			log.debug(methodName, "Uploaded batch file "+fileName+" kept in "+rootPath+" location");
		} catch (Exception e) {
			log.debug(methodName, e);
			throw new BTSLBaseException( "error.general.processing");
		}finally{
			try{
        		if (stream != null) {
        			stream.close();
                }
        	}catch(Exception e){
        		log.errorTrace(methodName, e);
        	}
			log.debug(methodName, PretupsI.EXITED);
		}
		return true;
	}

}
