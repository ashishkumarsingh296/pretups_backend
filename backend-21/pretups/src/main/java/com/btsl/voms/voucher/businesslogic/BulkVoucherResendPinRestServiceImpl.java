package com.btsl.voms.voucher.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomslogging.BulkVoucherResendPinProcessLog;
import com.btsl.voms.vomsreport.businesslogic.VomsVoucherResendPinVO;
import com.btsl.voms.vomsreport.businesslogic.VoucherResendDAO;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * BulkVoucherResendPinRestServiceImpl Implements BulkVoucherResendPinRestService and use for RestService to bulk voucher resend pin Request
 * @author Hargovind Karki
 * @since 12/01/2017
 */



public class BulkVoucherResendPinRestServiceImpl implements  BulkVoucherResendPinRestService{
	
	
	public static final Log _log = LogFactory.getLog(BulkVoucherResendPinRestServiceImpl.class.getName());
	
	private VomsVoucherResendPinVO errorVO=null;
	private VomsVoucherResendPinVO voucherVO=null;
	private VomsVoucherResendPinVO vomsVoucherResendPinInvalidVO = null;
	
	private List<VomsVoucherResendPinVO> invalidVoucherList =null;
	private List<ListValueVO> errorFileList =null;
	//private static Locale locale = null;
	private long delayTime = 0;
	private static OperatorUtilI _operatorUtilI;
	
    static {
		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
          _operatorUtilI = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } 
       catch (ClassNotFoundException e) {

        	_log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BulkVoucherResendPinRestServiceImpl", "", "", "",
                "Exception while loading the operator util class in class :" + BulkVoucherResendPinRestServiceImpl.class.getName() + ":" + e.getMessage());
        }
        catch (InstantiationException e) {

        	_log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BulkVoucherResendPinRestServiceImpl", "", "", "",
                "Exception while loading the operator util class in class :" + BulkVoucherResendPinRestServiceImpl.class.getName() + ":" + e.getMessage());
        }
        catch (IllegalAccessException e) {

        	_log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BulkVoucherResendPinRestServiceImpl", "", "", "",
                "Exception while loading the operator util class in class :" + BulkVoucherResendPinRestServiceImpl.class.getName() + ":" + e.getMessage());
        }
        
        
    }


	/** 
	 * this method take the file and check user for bulk resend pin request
	 * @param requestData
	 * @throws IOException
	 * @throws BTSLBaseException
	 * @return 
	 * @author hargovind karki
	 * @since 12/01/2017
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<ListValueVO>> uploadBulkVoucherResendPin(String requestData) throws BTSLBaseException{
		final String methodName = "uploadBulkVoucherResendPin";
		if (_log.isDebugEnabled())
			_log.debug(methodName, PretupsI.ENTERED+" with : requestData : "+requestData);
		try{
			PretupsResponse<List<ListValueVO>> response = new PretupsResponse<>();
			Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<Map<String, Object>>() {});
			Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
			if(!map.containsKey("filePath") || !map.containsKey("fileName") ||  !map.containsKey("userNetworkID")){
				printLog(methodName ,  " mendatory tags are  : MISSING : should Contains = filePath , fileName , userNetworkID ,userLanguage,userCountry");
				response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, null);
				response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "tag.is.missing");
				return response;
			}
			final String filePath = map.get("filePath").toString();
			final String fileName = map.get("fileName").toString();
			final String fileDirPath = filePath+"/"+fileName; 
			
			UserVO userVO = new UserVO();
			userVO.setNetworkID(map.get("userNetworkID").toString());
			response = bulkResendPinRequest(userVO ,fileDirPath,fileName );
			
			return response;
		
		}catch(IOException e){
			throw new BTSLBaseException(e);
		}finally{
			
			printLog(methodName, PretupsI.EXITED);
		}
	}
	
	
	
	/**this methods use to call methods of file parsing , approving request
	 * @param pUserVO
	 * @param fileDirPath
	 * @param fileName
	 * @return
	 * @throws BTSLBaseException
	 */
	private PretupsResponse<List<ListValueVO>> bulkResendPinRequest(UserVO pUserVO, String fileDirPath , String fileName) throws BTSLBaseException {
		final String methodName = "bulkResendPinRequest";
		
		printLog(methodName,PretupsI.ENTERED+" fileDirPath : fileName "+fileDirPath+" : "+fileName);
		Connection con = null;MComConnectionI mcomCon = null;
		PretupsResponse<List<ListValueVO>> response = new PretupsResponse<>();
		List<VomsVoucherResendPinVO> validVoucherList = new ArrayList<>();
		List<VomsVoucherResendPinVO> finalValidVocuherList = new ArrayList<>();
		errorFileList = new ArrayList<>();
		invalidVoucherList = new ArrayList<VomsVoucherResendPinVO>();
		
		
		
		
		// File Parsing and storing all records data into List
		try{
			mcomCon = new MComConnection();try{con=mcomCon.getConnection();}catch(SQLException e){
				_log.error(methodName,  "Exception"+ e.getMessage());
	    		_log.errorTrace(methodName, e);
			}
			// get the user locale
			//locale  = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
				 
			int totalNoRecords = BulkVoucherPinResendUtils.fileParsingForRecords( fileDirPath , fileName ,validVoucherList);
			if(totalNoRecords==0){
				printLog(methodName, "NO Records Found in File");
				response.setStatus(true);
				response.setStatusCode(PretupsI.RESPONSE_SUCCESS);
				response.setParameters(new String[] {});
				response.setFormError("bulk.voucher.pin.resend.no.records.found");
				return response;
			}
			
			finalValidVocuherList=verifyFileRecordsForUpload(pUserVO,validVoucherList, invalidVoucherList,errorFileList,con);
			if(finalValidVocuherList.isEmpty()){
				
				printLog(methodName, "NO Records Found in File");
				response.setStatus(true);
				response.setStatusCode(PretupsI.RESPONSE_SUCCESS);
				response.setParameters(new String[] { String.valueOf(finalValidVocuherList.size())});
				response.setFormError("bulk.voucher.pin.resend.no.valid.records");
				response.setDataObject(errorFileList);
				response.setStatusCode(PretupsI.RESPONSE_SUCCESS);
				return response;
			}
			
			processValidRecords(finalValidVocuherList, pUserVO ,invalidVoucherList,errorFileList,con);
			
			if(!invalidVoucherList.isEmpty()){
				response.setFormError("bulk.voucher.pin.resend.partial.records.processed");
				response.setStatus(true);
				response.setParameters(new String[] { String.valueOf(totalNoRecords-invalidVoucherList.size()) , String.valueOf(totalNoRecords)});
				response.setDataObject(errorFileList);
				response.setStatusCode(PretupsI.RESPONSE_SUCCESS);
				return response;
			}
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,errorFileList);
			response.setParameters(new String[] { String.valueOf(totalNoRecords)});
			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "bulk.voucher.pin.resend.message.success");
			return response;
		}
		catch(BTSLBaseException be){
			throw new BTSLBaseException(be);
		
		}finally {
			if(mcomCon != null){mcomCon.close("BulkVoucherResendPinRestServiceImpl#bulkResendPinRequest");mcomCon=null;}
			printLog(methodName, PretupsI.EXITED);
		}
	}

	/**
	 * CHECK EACH RECORD FROM FILE IS VALID
	 * @param realUserList
	 * @param errorFileList
	 * @param userList
	 * @param rejectUserList
	 * @param suspendOrDeleteReq
	 * @param con
	 * @throws BTSLBaseException
	 */
	private ArrayList verifyFileRecordsForUpload(UserVO pUserVO,List<VomsVoucherResendPinVO> validVoucherList, List<VomsVoucherResendPinVO> invalidVoucherList,List<ListValueVO> errorList ,Connection con) throws BTSLBaseException
	{
		final String methodName = "verifyFileRecordsForUpload";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED +" :with : pUserVO :"+pUserVO.getNetworkID());
		}
		int finalListSize=validVoucherList.size();
		int countData=0;
		int dayDiff = 0;
		String dataStr=null;
		ArrayList <VomsVoucherResendPinVO> finalValidVocuherList = new ArrayList<>();
		try
		{
			dayDiff = Integer.parseInt(Constants.getProperty("VALID_DAY_DIFF"));
		}
		catch(NumberFormatException nfe)
		{
			//setting the day differece to default value of 3 incase the property is not defined in the Constant.props
			dayDiff = 3;
		}
		
		
		String transactionidblank =  PretupsRestUtil.getMessageString("voms.bulkvoucherResendPin.err.msg.transactionidnotfound");
		String invalidTransDate=PretupsRestUtil.getMessageString("voms.bulkvoucherResendPin.err.msg.invaliddate");
		String invalidRetMSISDN=PretupsRestUtil.getMessageString("voms.bulkvoucherResendPin.errorfile.msg.invalidretailermsisdn");
		
		String invalidCustMSISDN=PretupsRestUtil.getMessageString("voms.bulkvoucherResendPin.errorfile.msg.invalidcustomermsisdn");
		String prefixNotFound=PretupsRestUtil.getMessageString("voms.bulkvoucherResendPin.errorfile.msg.networkprefixnotfound");
		String networkNotSupported=PretupsRestUtil.getMessageString("voms.bulkvoucherResendPin.errorfile.msg.networknotsupport");
		
		String invalidTransDateFmt=PretupsRestUtil.getMessageString("voms.bulkvoucherResendPin.err.msg.invaliddateformat");
		String duplicateRecords=PretupsRestUtil.getMessageString("voms.bulkvoucherResendPin.errorfile.msg.duplicaterecords");
		
		
		while( finalListSize > 0)
		{
			VomsVoucherResendPinVO vomsVoucherResendPinVO =new VomsVoucherResendPinVO();
			
			Date transDate=null;
			String filteredRetailerMsisdn=null;
			String filteredCustomerMsisdn=null;
			String msisdnPrefix;
			NetworkPrefixVO networkPrefixVO = null;
			String networkCode;
			errorVO=new VomsVoucherResendPinVO();
			voucherVO = (VomsVoucherResendPinVO)validVoucherList.get(countData);
			dataStr = voucherVO.getTransferID();
			if(_log.isDebugEnabled())
				_log.debug(methodName,"Processing starts for  "+dataStr);
			countData++;
			
			// check for transfer id
			if(BTSLUtil.isNullString(voucherVO.getTransferID().trim()))	
			{
				errorVO.setErrorCode(transactionidblank);
				setValueInInvalidVoucherList(voucherVO,errorVO);
				errorList.add(new ListValueVO(" ","voms.bulkvoucherResendPin.err.msg.transactionidnotfound"));
				finalListSize--;
				continue;
			}
			
			
			//check for transfer date
				if(BTSLUtil.isNullString(voucherVO.getTransferDate()))	
				{
					errorVO.setErrorCode(invalidTransDate);
					setValueInInvalidVoucherList(voucherVO,errorVO);
					errorList.add(new ListValueVO(voucherVO.getTransferID(),"voms.bulkvoucherResendPin.err.msg.dateblank"));
					finalListSize--;
					continue;
				}
				else
				{
					// check for valid date
					try
					{
						transDate = BTSLUtil.getDateFromVOMSDateString(voucherVO.getTransferDate());
					}
					catch(ParseException e)
					{
						  if(_log.isDebugEnabled())
								_log.debug("processUploadedFile","The date format is incorrect");
						  		_log.errorTrace(methodName, e);
						  errorVO.setErrorCode(invalidTransDateFmt);
						  setValueInInvalidVoucherList(voucherVO,errorVO);
						  errorList.add(new ListValueVO(voucherVO.getTransferID(),"voms.bulkvoucherResendPin.err.msg.invaliddateformat"));
						  finalListSize--;
							continue;
					}
					//this logic checks if the entered date is no more than defined days old from the current date
				   int noOfDays = BTSLUtil.getDifferenceInUtilDates(transDate, new Date());
				   if(noOfDays > dayDiff)
				   {
					   if(_log.isDebugEnabled())
							_log.debug("processUploadedFile","The date entered is more than "+dayDiff+" days old");
					   errorVO.setErrorCode(invalidTransDate);
					   setValueInInvalidVoucherList(voucherVO,errorVO);
					   errorList.add(new ListValueVO(voucherVO.getTransferID(),"voms.bulkvoucherResendPin.err.msg.invaliddate"));
					   finalListSize--;
					   continue;
				   }
				}
			   //check for retailer msisdn
				  if(!BTSLUtil.isNullString(voucherVO.getRetailerMSISDN()))
				  {
				   	 
					   try
					   {
						   filteredRetailerMsisdn=PretupsBL.getFilteredMSISDN(voucherVO.getRetailerMSISDN());
					   }
					   catch (BTSLBaseException e)
					   {
						   if(_log.isDebugEnabled())
								_log.debug("processUploadedFile","Not a valid MSISDN "+voucherVO.getRetailerMSISDN());
						   		_log.errorTrace(methodName, e);
						   
						   errorVO.setErrorCode(invalidRetMSISDN);
						   setValueInInvalidVoucherList(voucherVO,errorVO);
						   errorList.add(new ListValueVO(voucherVO.getTransferID(),"voms.bulkvoucherResendPin.errorfile.msg.invalidretailermsisdn"));
						   finalListSize--;
							continue;
					   }
					 //isValidMsisdn
					   if(!BTSLUtil.isValidMSISDN(filteredRetailerMsisdn))
						{
							if(_log.isDebugEnabled())
								_log.debug("processUploadedFile","Not a valid MSISDN "+voucherVO.getRetailerMSISDN());
							errorVO.setErrorCode(invalidRetMSISDN);
							setValueInInvalidVoucherList(voucherVO,errorVO);
							errorList.add(new ListValueVO(voucherVO.getTransferID(),"voms.bulkvoucherResendPin.errorfile.msg.invalidretailermsisdn"));
							finalListSize--;
							continue;
						}
					// check prefix of the MSISDN
					    msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredRetailerMsisdn); // get the prefix of the MSISDN
						networkPrefixVO = (NetworkPrefixVO)NetworkPrefixCache.getObject(msisdnPrefix);
						if(networkPrefixVO == null)
						{
							if(_log.isDebugEnabled())
								_log.debug("processUploadedFile","Not Network prefix found "+voucherVO.getRetailerMSISDN());
							errorVO.setErrorCode(prefixNotFound);
							setValueInInvalidVoucherList(voucherVO,errorVO);
							errorList.add(new ListValueVO(voucherVO.getTransferID(),"voms.bulkvoucherResendPin.errorfile.msg.networkprefixnotfound"));
							finalListSize--;
							continue;
						}
						// check network support of the MSISDN
					    networkCode=networkPrefixVO.getNetworkCode();
					    
						if(!networkCode.equals(pUserVO.getNetworkID()))
						{
							if(_log.isDebugEnabled())
								_log.debug("processUploadedFile","Not supporting Network"+pUserVO.getNetworkID());
							errorVO.setErrorCode(networkNotSupported);
							setValueInInvalidVoucherList(voucherVO,errorVO);
							errorList.add(new ListValueVO(voucherVO.getTransferID(),"voms.bulkvoucherResendPin.errorfile.msg.networknotsupport"));
							finalListSize--;
							continue;				
						}
				  }
						//check for customer msisdn
					  if(!BTSLUtil.isNullString(voucherVO.getCustomerMSISDN()))
						  {
						   try
						   {
							   filteredCustomerMsisdn=PretupsBL.getFilteredMSISDN(voucherVO.getCustomerMSISDN());
						   }
						   catch (BTSLBaseException e)
						   {
							   if(_log.isDebugEnabled())
									_log.debug("processUploadedFile","Not a valid MSISDN "+voucherVO.getCustomerMSISDN());
								   	_log.errorTrace(methodName, e);
							   errorVO.setErrorCode(invalidCustMSISDN);
							   setValueInInvalidVoucherList(voucherVO,errorVO);
							   errorList.add(new ListValueVO(voucherVO.getTransferID(),"voms.bulkvoucherResendPin.errorfile.msg.invalidcustomermsisdn"));
							   finalListSize--;
								continue;
						   }
						 //isValidMsisdn
						   if(!BTSLUtil.isValidMSISDN(filteredCustomerMsisdn))
							{
								if(_log.isDebugEnabled())
									_log.debug("processUploadedFile","Not a valid MSISDN "+voucherVO.getCustomerMSISDN());
								errorVO.setErrorCode(invalidCustMSISDN);
								setValueInInvalidVoucherList(voucherVO,errorVO);
								errorList.add(new ListValueVO(voucherVO.getTransferID(),"voms.bulkvoucherResendPin.errorfile.msg.invalidcustomermsisdn"));
								finalListSize--;
								continue;
							}
						// check prefix of the MSISDN
						    msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredCustomerMsisdn); // get the prefix of the MSISDN
							networkPrefixVO = (NetworkPrefixVO)NetworkPrefixCache.getObject(msisdnPrefix);
							if(networkPrefixVO == null)
							{
								if(_log.isDebugEnabled())
									_log.debug("processUploadedFile","Not Network prefix found "+voucherVO.getCustomerMSISDN());
								errorVO.setErrorCode(prefixNotFound);
								setValueInInvalidVoucherList(voucherVO,errorVO);
								errorList.add(new ListValueVO(voucherVO.getTransferID(),"voms.bulkvoucherResendPin.errorfile.msg.networkprefixnotfound"));
								finalListSize--;
								continue;
							}
							// check network support of the MSISDN
						    networkCode=networkPrefixVO.getNetworkCode();
						    
						    if(!networkCode.equals(pUserVO.getNetworkID()))
							{
								if(_log.isDebugEnabled())
									_log.debug("processUploadedFile","Not supporting Network"+pUserVO.getNetworkID());
								errorVO.setErrorCode(networkNotSupported);
								setValueInInvalidVoucherList(voucherVO,errorVO);
								errorList.add(new ListValueVO(voucherVO.getTransferID(),"voms.bulkvoucherResendPin.errorfile.msg.networknotsupport"));
								finalListSize--;
								continue;				
							}
					     }
						
					  	if(!finalValidVocuherList.isEmpty() && containsTrasactionId(finalValidVocuherList,voucherVO.getTransferID()))
					  	{
					  		
					  		if(_log.isDebugEnabled())
								_log.debug("processUploadedFile","Duplicate Records"+voucherVO.getTransferID());
							errorVO.setErrorCode(duplicateRecords);
							setValueInInvalidVoucherList(voucherVO,errorVO);
							errorList.add(new ListValueVO(voucherVO.getTransferID(),"voms.bulkvoucherResendPin.errorfile.msg.duplicaterecords"));
							finalListSize--;
							continue;	
					  		
					  	}
					  
					  	vomsVoucherResendPinVO=new VomsVoucherResendPinVO();
						vomsVoucherResendPinVO.setServiceType(PretupsI.SERVICE_TYPE_EVD);
						vomsVoucherResendPinVO.setTransferID(voucherVO.getTransferID());
						
						vomsVoucherResendPinVO.setTransferDate(voucherVO.getTransferDate());
						vomsVoucherResendPinVO.setRetailerMSISDN(filteredRetailerMsisdn);
						vomsVoucherResendPinVO.setCustomerMSISDN(filteredCustomerMsisdn);
						
						vomsVoucherResendPinVO.setLineNumber(voucherVO.getLineNumber());
						if(!BTSLUtil.isNullString(voucherVO.getSerialNo()))
							vomsVoucherResendPinVO.setSerialNo(voucherVO.getSerialNo());
						//insert valid data in valid list
						
						
						finalValidVocuherList.add(vomsVoucherResendPinVO);
						finalListSize--;
					  
					
			}
			
		return finalValidVocuherList;
		}
		
		
	
	/**
	 * THIS METHOD IS TO APPROVE ALL RECORDS GIVEN FOR APPROVAL
	 * 
	 * @param userList
	 * @param errorFileList
	 * @param userDAO
	 * @param userVO
	 * @param childExistList
	 * @param prepareStatementMap
	 * @param defaultLocale
	 * @param suspendOrDeleteReq
	 * @param rejectUserList
	 * @param realUserList
	 * @param con
	 * @throws BTSLBaseException
	 */
	private void processValidRecords(List<VomsVoucherResendPinVO> finalValidVocuherList ,UserVO userVO , List<VomsVoucherResendPinVO> invalidVoucherList,List<ListValueVO> errorList,Connection con) throws BTSLBaseException
	{
		final String methodName = "processValidRecords";
		printLog(methodName, PretupsI.ENTERED);
		//String errorKey="voms.bulkvoucherResendPin.msg.novaliddatainfile";
		
		VomsVoucherResendPinVO vomsVoucherResendPinVO = null;
		ArrayList validDataList = new ArrayList();
		
		
		errorVO=new VomsVoucherResendPinVO();
		
		
		try{
		/*if(finalValidVocuherList.isEmpty())
		{   
			printLog(methodName,"No valid Data in the file, size :"+finalValidVocuherList.size());
			throw new BTSLBaseException(this, "processUploadedFile", errorKey,"showResult");
		}*/	
		ArrayList newValidDataList=new ArrayList();
		VoucherResendDAO resendDAO = null;
		TransferVO transferVO=null;
		//con = OracleUtil.getConnection();
		
		BulkVoucherResendPinProcessLog.log("Bulk Voucher Resend Pin Processing START");
		int k=finalValidVocuherList.size();
		for(int m=0;m<k;m++)
		{
			vomsVoucherResendPinVO=(VomsVoucherResendPinVO)finalValidVocuherList.get(m);
			resendDAO=new VoucherResendDAO();
			
			 if (!_operatorUtilI.getNewDataAftrTbleMerging( BTSLUtil.getDateFromDateString(vomsVoucherResendPinVO.getTransferDate()),new Date())) {
	              
	                transferVO=(TransferVO)resendDAO.getVomsTransactionDetails_OLD(con, vomsVoucherResendPinVO);
	            } else {
	            	transferVO=(TransferVO)resendDAO.getVomsTransactionDetails(con, vomsVoucherResendPinVO);
	                
	            }
			
			
			//transferVO=(TransferVO)resendDAO.getVomsTransactionDetails(con, vomsVoucherResendPinVO);
			if(transferVO!=null &&  PretupsI.TXN_STATUS_SUCCESS.equalsIgnoreCase(transferVO.getTransferStatus())&& !BTSLUtil.isNullString(transferVO.getSerialNumber()))
			{
				newValidDataList.add(transferVO);
				continue;
			}
			else
			{
				printLog(methodName,"transaction detail not found");
				vomsVoucherResendPinVO.setLineNumber(vomsVoucherResendPinVO.getLineNumber());
				validateTransactionData(vomsVoucherResendPinVO,transferVO,errorVO,errorList);
				setValueInInvalidVoucherList(vomsVoucherResendPinVO,errorVO);
				
			}
		}
		
		if(!newValidDataList.isEmpty())
		{
			validDataList=null;
			validDataList=newValidDataList;
			pushmessage(validDataList,errorList,con);
		}
	}
	catch( ParseException e)
	    {
			_log.debug(methodName, "Exception"+e.getMessage());
	   	    _log.errorTrace(methodName, e);
	   	    throw new BTSLBaseException(this, methodName,e.getMessage());

	    }	
	catch( BTSLBaseException e)
    {
		_log.debug(methodName, "Exception"+e.getMessage());
   	    _log.errorTrace(methodName, e);
   	    throw new BTSLBaseException(this, methodName,e.getMessage());

    }
    finally
	{
    	BulkVoucherResendPinProcessLog.log("Bulk Voucher Resend Pin Processing END");
    	printLog(methodName,PretupsI.EXITED);
	
	}
	
		return;
	}
	
	
	



	private void validateTransactionData(VomsVoucherResendPinVO vomsVoucherResendPinVO,TransferVO transferVO, VomsVoucherResendPinVO errorVO, List<ListValueVO> errorList) 
	{
		if(transferVO==null)
		{
			errorVO.setErrorCode("data not found");
			errorList.add(new ListValueVO(vomsVoucherResendPinVO.getTransferID(),"voms.bulkvoucherResendPin.err.msg.datanotfound"));
		}
		else if(!PretupsI.TXN_STATUS_SUCCESS.equalsIgnoreCase(transferVO.getTransferStatus()))
		{
			errorVO.setErrorCode("txn fail");
			BulkVoucherResendPinProcessLog.log("Transaction Status fail for transfer ID",transferVO.getTransferID());
			errorList.add(new ListValueVO(vomsVoucherResendPinVO.getTransferID(),"voms.bulkvoucherResendPin.err.msg.txnfailed"));
		}
		else if(BTSLUtil.isNullString(transferVO.getSerialNumber()))
		{
			errorVO.setErrorCode("serial number null");
			errorList.add(new ListValueVO(vomsVoucherResendPinVO.getTransferID(),"voms.bulkvoucherResendPin.err.msg.serailnumbernull"));
		}
		
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

	private void setValueInInvalidVoucherList(VomsVoucherResendPinVO voucherVO,VomsVoucherResendPinVO errorVO)
	{
		
		vomsVoucherResendPinInvalidVO=new VomsVoucherResendPinVO();
		vomsVoucherResendPinInvalidVO.setServiceType(PretupsI.SERVICE_TYPE_EVD);
		vomsVoucherResendPinInvalidVO.setTransferID(voucherVO.getTransferID());
		
		vomsVoucherResendPinInvalidVO.setTransferDate(voucherVO.getTransferDate());
		vomsVoucherResendPinInvalidVO.setRetailerMSISDN(voucherVO.getRetailerMSISDN());
		vomsVoucherResendPinInvalidVO.setCustomerMSISDN(voucherVO.getCustomerMSISDN());
		
		vomsVoucherResendPinInvalidVO.setLineNumber(voucherVO.getLineNumber());
		if(!BTSLUtil.isNullString(voucherVO.getSerialNo()))
			vomsVoucherResendPinInvalidVO.setSerialNo(voucherVO.getSerialNo());
		
		
		vomsVoucherResendPinInvalidVO.setErrorCode(errorVO.getErrorCode());
		
		//insert invalid data in invalid list
		invalidVoucherList.add(vomsVoucherResendPinInvalidVO);
		
		
	}
	
	
	/**this method containsTrasactionId
	 * @param c
	 * @param trasactionId
	 */

public static boolean containsTrasactionId(Collection<VomsVoucherResendPinVO> c, String trasactionId) {
    for(VomsVoucherResendPinVO o : c) {
        if(o != null && o.getTransferID().equals(trasactionId)) {
            return true;
        }
    }
    return false;
}


private void pushmessage(ArrayList validDataList,List<ListValueVO> errorList,Connection con) {
	// TODO Auto-generated method stub
	
	String methodName = "pushmessage";
	TransferVO transferVO=null;
	VoucherResendDAO resendDAO = null;
	VomsVoucherResendPinVO vomsVoucherResendPinVO = new VomsVoucherResendPinVO();
	String arr[]=null;
	BTSLMessages btslMessage = null;
	
	try{
	
	for(int m=0,n=validDataList.size();m<n;m++)
	{
		
		transferVO=(TransferVO)validDataList.get(m);
		resendDAO = new VoucherResendDAO();							
		//this is used to get Pin number for the corresponding serial number entered
		String pin=VomsUtil.decryptText(resendDAO.getPin(con,transferVO.getSerialNumber()));
		if(BTSLUtil.isNullString(pin))
		{
			vomsVoucherResendPinVO.setLineNumber(vomsVoucherResendPinVO.getLineNumber());
			errorVO.setErrorCode("pin null");
			//vomsVoucherResendPinVO.setTransferID(vomsVoucherResendPinVO.getTransferID());
			//invalidVoucherList.add(errorVO);
			setValueInInvalidVoucherList(vomsVoucherResendPinVO,errorVO);
			errorList.add(new ListValueVO(vomsVoucherResendPinVO.getTransferID(),"voms.bulkvoucherResendPin.err.msg.pinisnull"));
			continue;
		}
		arr = new String[3];
		arr[0]= pin; //set the pin to be sent 
		arr[1]= transferVO.getSerialNumber();
		arr[2]= transferVO.getTransferID();
		
		btslMessage = new BTSLMessages(PretupsErrorCodesI.VOUCHER_PIN_RESEND,arr);
		//creates a new locale object based on the language and country information obtained
		Locale locale=new Locale(transferVO.getLanguage(),transferVO.getCountry());
		//this method is called to send the message to the particular msisdn for the pin.
		PushMessage pushMsg = new PushMessage(transferVO.getPinSentToMsisdn(),btslMessage,transferVO.getTransferID(),transferVO.getRequestGatewayCode(),locale,transferVO.getNetworkCode());
		
		boolean isDelayRequired = Boolean.parseBoolean(Constants.getProperty("isDelayRequiredForBulkResendPin"));
		if(BTSLUtil.isNullString(String.valueOf(isDelayRequired)))
			isDelayRequired=false;
		
		
		
		try {
			delayTime = Long.parseLong(Constants.getProperty("delayTimeForBulkResendPin"));
        } catch (NumberFormatException e) {
        	_log.errorTrace(methodName, e);
        	delayTime = 0;
        }
		
			
		if(isDelayRequired)
		{
			try{
				Thread.sleep(delayTime);
				}
			catch(InterruptedException e)
			{
			_log.debug(methodName, "Exception"+e.getMessage());
	   	    _log.errorTrace(methodName, e);
			}
		}
		pushMsg.push();	
}
	}
	catch( BTSLBaseException e)
    {
		_log.debug(methodName, "Exception"+e.getMessage());
   	    _log.errorTrace(methodName, e);
		

    }
    finally
	{
    	BulkVoucherResendPinProcessLog.log("Bulk Voucher Resend Pin Processing END");
    	printLog(methodName,PretupsI.EXITED);
	
	}

}

}