package com.btsl.voms.voucher.service;



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


/**
 * This class provide the methods to create Request to download and Upload bulk resend pin Request
 * @author hargovind karki
 *@since 12/01/2016
 *
 */

@Service("BulkVoucherResendPinService")
public class BulkVoucherResendPinServiceImpl implements BulkVoucherResendPinService{
	
	public static final Log _log = LogFactory.getLog(BulkVoucherResendPinServiceImpl.class.getName());


	@Autowired
	private PretupsRestClient pretupsRestClient;
	
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
	public void bulkVoucherPinResendProcess(String filePath , String fileName , ModelMap modelMap,UserVO userVO) throws BTSLBaseException{

		final String methodName = "bulkVoucherPinResendProcess";
		String responseString;

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}

		PretupsResponse<List<ListValueVO>> responseApprove = null;

		Map<String, Object> voucherData = new HashMap<>();
		Map<String, Object> voucherObject = new HashMap<>();

		try{
			voucherData.put("filePath", filePath);
			voucherData.put("fileName", fileName);
			voucherData.put("userNetworkID",userVO.getNetworkID());
			voucherObject.put("data", voucherData);

			responseString = pretupsRestClient.postJSONRequest(voucherObject, PretupsI.BULK_VOUCHER_RESEND_PIN );
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
			

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, PretupsI.EXITED);
			}
		
		
		}

		return;
	}
	
}
