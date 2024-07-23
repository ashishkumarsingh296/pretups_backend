package com.btsl.pretups.master.service;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.fasterxml.jackson.core.type.TypeReference;

@Service("batchGeographicalDomainService")
public class BatchGeographicalDomainServiceImpl implements BatchGeographicalDomainService{
	
	public static final Log _log = LogFactory.getLog(BatchGeographicalDomainServiceImpl.class.getName());
	
	@Autowired
	private PretupsRestClient pretupsRestClient;
	
	/**
	 * This method to Download  List
	 * @param pUserStatus
	 * @throws BTSLBaseException
	 * @return
	 * @author vikas.chaudhary 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public InputStream downloadList(ModelMap modelMap) throws BTSLBaseException{
		final String methodName = "BatchGeographicalDomainServiceImpl[downloadList()]";

		String responseString = null;
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		Map<String, Object> data = new HashMap();
		Map<String, Object> object = new HashMap();
		data.put("networkID",modelMap.get("networkID"));
		object.put("data", data);
		PretupsResponse<byte[]> response;

		try {
			responseString = pretupsRestClient.postJSONRequest(object, PretupsI.BATCH_GRPH_DOMAIN_DOWNLOAD);
			response = (PretupsResponse<byte[]>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<byte[]>>() {});
			
			if(response.hasGlobalError())
				modelMap.put("failDownload","error.general.processing");
			
		} catch (IOException io) {
			throw new BTSLBaseException(io);
		}
		
		byte[] bytes = (byte[]) response.getDataObject();
		if(bytes==null) {
			modelMap.put("failDownload","no.user.approval.state");
			return null;
		}
		try(InputStream inputStream = new ByteArrayInputStream(bytes)){
			
			modelMap.put("successDownload" ,PretupsRestUtil.getMessageString("file.download.successfully"));
			return inputStream;
		}
		catch (IOException io) {
			throw new BTSLBaseException(io);
		}
		finally{
			printLog(methodName, PretupsI.EXITED);
		}

	}
	
	/**this method Print Log
	 * @param methodName
	 * @param log
	 */
	private void printLog(String methodName , String log) {
		if (_log.isDebugEnabled())
			_log.debug(methodName, log);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initiateBatchGeographicalDomainCreation(String batchName, String filePath, String fileName, UserVO userVO, ModelMap modelMap) throws BTSLBaseException {
		
		final String methodName = "BatchGeographicalDomainServiceImpl[initiateBatchGeographicalDomainCreation()]";
		String responseString;

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}

		PretupsResponse<List<ListValueVO>> response = null;

		Map<String, Object> approveData = new HashMap<>();
		Map<String, Object> approveObject = new HashMap<>();
		
		try {
			approveData.put("filePath", filePath);
			approveData.put("batchName", batchName);
			approveData.put("fileName", fileName);
			approveData.put("networkID",userVO.getNetworkID());
			approveData.put("userID",userVO.getUserID());

			approveObject.put("data", approveData);

			responseString = pretupsRestClient.postJSONRequest(approveObject, PretupsI.BATCH_GRPH_DOMAIN_INITIATE);
			response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {});

			if(response.hasFormError()) {
				modelMap.put("failUpload", PretupsRestUtil.getMessageString(response.getFormError(),response.getParameters()));
			}
			else {
				modelMap.put("successUpload", PretupsRestUtil.getMessageString(response.getSuccessMsg(),response.getParameters()));
			}
			modelMap.put("errorList",response.getDataObject());
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, e);
			}
			modelMap.put("failUpload", PretupsRestUtil.getMessageString("error.general.processing"));
		} finally {
			printLog(methodName, PretupsI.EXITED);
		}
		
		return;
	}
}
