package com.client.pretups.channel.user.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
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



/**
 * This class provide the methods to create Request to download User lIst and approve Suspend/Delete Request
 * @author MOHD SUHEL
 *@since 17/10/2016
 *
 */
@Service("approvalUserDeleteSuspendService")
public class ApprovalUserDeleteSuspendServiceImpl implements ApprovalUserDeleteSuspendService {

	public static final Log _log = LogFactory.getLog(ApprovalUserDeleteSuspendServiceImpl.class.getName());


	@Autowired
	private PretupsRestClient pretupsRestClient;

	/**
	 * THis method Provide the Status Type need for Approval from Loopkups
	 * @return
	 * @throws BTSLBaseException
	 * @author mohd.suhel1
	 * @since 10/18/2016
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ListValueVO> loadStatusTypeList() throws BTSLBaseException {
		final String methodName = "loadStatusTypeList";
		
		printLog(methodName, PretupsI.ENTERED);
		
		try{
			Map<String, Object> data = new HashMap<>();
			data.put("lookupType", PretupsI.USER_STATUS_TYPE);
			data.put("active", true);
			Map<String, Object> object = new HashMap<>();
			object.put("data", data);
			String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.LOOKUP);
			PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil
					.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {
					});
			List<ListValueVO> list =  response.getDataObject();
			printLog(methodName, "Return Data Before Processing : "+list);
			this.processListValueVOValue(list);

			return list;

		}
		catch(IOException |RuntimeException e)
		{
			throw new BTSLBaseException(e);
		}
		finally{
			
			printLog(methodName, PretupsI.EXITED);
		}
	}



	/**
	 * This method to Download USer List
	 * @param pUserStatus
	 * @throws BTSLBaseException
	 * @return
	 * @author mohd.suhel1 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public InputStream downloadUserList(String pUserStatus , ModelMap modelMap) throws BTSLBaseException{
		final String methodName = "DownloadUserList";

		String responseString = null;
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered : with userStatus : "+pUserStatus);
		}
		Map<String, Object> data = new HashMap<>();
		data.put("userStatus",pUserStatus);
		Map<String, Object> object = new HashMap<>();
		object.put("data", data);
		PretupsResponse<byte[]> response;

		try{
			responseString = pretupsRestClient.postJSONRequest(object, PretupsI.SUSPEND_DELETE_USER_DOWNLOAD);
			response = (PretupsResponse<byte[]>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<byte[]>>() {});
			
			if(response.hasGlobalError())
				modelMap.put("failDownload","error.general.processing");
			
		}catch(IOException io)
		{
			throw new BTSLBaseException(io);
		}
		
		byte[] bytes = (byte[]) response.getDataObject();
		if(bytes==null)
		{
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





	/**
	 * this method use to approve all request from file 
	 * @param filePath
	 * @param fileName
	 * @param userVO 
	 * @param modelMap
	 * @return
	 * @throws BTSLBaseException
	 * @author mohd.suhel1
	 * @since 10/18/2016
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void approveSuspendDeleteUserBatch(String filePath , String fileName , UserVO userVO , ModelMap modelMap) throws BTSLBaseException{

		final String methodName = "UploadUserList";
		String responseString;

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}

		PretupsResponse<List<ListValueVO>> responseApprove = null;

		Map<String, Object> approveData = new HashMap<>();
		Map<String, Object> approveObject = new HashMap<>();

		try{
			approveData.put("filePath", filePath);

			approveData.put("fileName", fileName);
			approveData.put("userNetworkID",userVO.getNetworkID());
			approveData.put("userID",userVO.getUserID());

			approveObject.put("data", approveData);

			responseString = pretupsRestClient.postJSONRequest(approveObject, PretupsI.APPROVE_DELETE_SUSPEND_BATCH );
			responseApprove = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {});

			if(responseApprove.hasFormError())
			{
				modelMap.put("failUpload", PretupsRestUtil.getMessageString(responseApprove.getFormError(),responseApprove.getParameters()));

			}
			else
			{
				modelMap.put("successUpload", PretupsRestUtil.getMessageString(responseApprove.getSuccessMsg(),responseApprove.getParameters()));

			}

			modelMap.put("errorList",responseApprove.getDataObject());
		}
		catch (Exception e) {

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, e);
			}
			modelMap.put("failUpload", PretupsRestUtil.getMessageString("error.general.processing"));
		}finally{
			
			printLog(methodName, PretupsI.EXITED);
		
		}

		return;
	}
	
	
	/**process list and remove unnecessary items
	 * @param listObject
	 */
	private <T> void processListValueVOValue(List<T> listObject) {
		
		printLog("processListValueVOValue", PretupsI.ENTERED);
		ListValueVO listValueVO;
		
		Iterator<T> it = listObject.iterator();
		while(it.hasNext())
		{
			listValueVO = (ListValueVO)it.next();
			if (!"SRB".equals(listValueVO.getValue())&&!"DRB".equals(listValueVO.getValue())) {
				it.remove();
			}


		}
		printLog("processListValueVOValue", PretupsI.EXITED);
	}


	/**this method Print Log
	 * @param methodName
	 * @param log
	 */
	private void printLog(String methodName , String log)
	{
		if (_log.isDebugEnabled())
			_log.debug(methodName, log);
	}

}


