package com.restapi.c2s.services;

import com.btsl.common.BaseResponse;
import com.btsl.common.*;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.*;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.SimProfileCache;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.pretups.channel.logging.ChannelGatewayRequestLog;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.GatewayParsersI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.inter.util.VOMSVoucherDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgVO;
import com.btsl.pretups.receiver.FixedInformationVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.transfer.businesslogic.MessageFormater;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.*;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.Constants;
import com.btsl.util.*;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.web.pretups.channel.transfer.businesslogic.C2STransferWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;


@Service("C2SServiceInterface")
public class C2SServiceImpl implements C2SServiceI{
	 private final Log log = LogFactory.getLog(this.getClass().getName());

	    private static final String PROCESSREQUEST = "C2SServiceImpl[processRequest]";
	    private static final String RSTRECEIVER = "RestReceiver";
	    private static final String TXNSTATUS = "TXNSTATUS";
	    private static final String DATE = "DATE";
	    private static final String MESSAGE = "MESSAGE";
	    private static final String GATEWAYTYPE = " For Gateway Type=";
	    private static final String MESSAGECODE = " Message Code=";
	    private static final String ARGS = "Args=";
	    private static final String SERVICEPORT = "Service Port=";
	    private static final String BTSLBASEEXP ="BTSLBaseException be:";
	    private static final String REQSTART =" requestStartTime=";
	    private HttpServletResponse responseSwag;
	    private DvdRequestVO dvdRequestVO;
	    private DvdApiResponse dvdResponse;
	    private BaseResponseMultiple baseResponseMultiple;
	    private DvdDetails dvdDetails;
	    private ArrayList<BaseResponse> baseResponseList;
	    private ArrayList<RowErrorMsgLists> rowErrorMsgListsFinal;
	    private ArrayList<MasterErrorList> masterErrorList;
	    private ArrayList<TxnIDBaseResponse> txnIdDetailsList;
	    private ErrorMap errorMap;
	    private String txnBatchId= null;
	    private int failureCount ;
	    private int successCount;
	    private int rowCount;
	   
	
	public void loadDenomination(MvdDenominationResponseVO response) throws BTSLBaseException {
		
		 String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	     String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	
		
		 response.setService("c2cServices");
	     response.setReferenceId(1986);
	     response.setErrorMap(null);
	     
	     
	     ArrayList<ListValueVO> voucherDenomList2=VOMSVoucherDAO.loadDenominationForBulkVoucherDistribution(false);
	     
	     MvdResponseData mvdResponseData=null;
	     ArrayList<MvdResponseData> voucherDenomList=new ArrayList<>();
	     for(ListValueVO prod:voucherDenomList2) {
	    	 mvdResponseData =new MvdResponseData();
	    	 mvdResponseData.setLabel(prod.getLabel());
	    	 mvdResponseData.setValue(prod.getValue());
	    	 mvdResponseData.setCodeName(prod.getCodeName());
	    	 mvdResponseData.setLabelWithValue(prod.getLabelWithValue());
	    	 voucherDenomList.add(mvdResponseData);
	       }
	     
	     Collections.sort(voucherDenomList, new Comparator<MvdResponseData>() {
				@Override
				public int compare(MvdResponseData o1, MvdResponseData o2) {
					// TODO Auto-generated method stub
					 if (Double.parseDouble(o1.getValue()) < Double.parseDouble(o2.getValue())){return -1;} 
				        if (Double.parseDouble(o1.getValue()) > Double.parseDouble(o2.getValue())){return 1;} 
				        return 0;
					
				}
				
		    	
		    	
		     });	
	     
	     response.setVoucherDenomList(voucherDenomList);
	     
	     if(!BTSLUtil.isNullOrEmptyList(response.getVoucherDenomList())) {
	    	 
	    	String sucess=Integer.toString(PretupsI.RESPONSE_SUCCESS) ;
	    	response.setStatus(sucess);
	 		response.setMessageCode(PretupsErrorCodesI.SUCCESS);
	 		String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
	       response.setMessage(resmsg);
	  
	     }
	     else {
	    	   throw new BTSLBaseException("C2SServiceImpl", "loadDenomination", PretupsErrorCodesI.NO_DENOM);
	    	 
	     }
	
	}
	
	
	/**
	 * MethodName processRequestDVD
	 */
	@Override
	public DvdApiResponse processRequestDVD(DvdSwaggRequestVO requestVO,
			String requestIDStr, MultiValueMap<String, String> headers, HttpServletResponse responseSwag, HttpServletRequest httpServletRequest)
			throws BTSLBaseException {
		    final String methodName = "processRequestDVD";
		    
		    PretupsResponse<JsonNode> jsonResponse;
		    Set<String> profileSet  = new HashSet<String>(); 
		    errorMap = new ErrorMap();
		    rowErrorMsgListsFinal = new ArrayList<RowErrorMsgLists>();
		    baseResponseList = new ArrayList<BaseResponse>();
		    masterErrorList = new ArrayList<MasterErrorList>();
		    txnIdDetailsList = new ArrayList<TxnIDBaseResponse>();
		    this.responseSwag = responseSwag;
		    failureCount = 0;
		    successCount = 0;
		    rowCount = 0;
            txnBatchId = null;
            HashMap<String, String> responseBasicDetails= null;
            ArrayList<VomsVoucherVO> vomsVoucherList = null;
            Connection con = null;
    		MComConnectionI mcomCon = null;
 	        try {
 	        	baseResponseMultiple = new BaseResponseMultiple();
 	        	dvdResponse = new DvdApiResponse();
 	        	dvdDetails = new DvdDetails();
 	        	dvdRequestVO = new DvdRequestVO();
 	        	dvdRequestVO.setData(dvdDetails);
 	        	mcomCon = new MComConnection();
 				con = mcomCon.getConnection();
 	        	responseBasicDetails= new HashMap<String, String>();
 	        	vomsVoucherList = new ArrayList<VomsVoucherVO>();
 	        	OAuthenticationUtil.validateTokenApi(dvdRequestVO, headers, baseResponseMultiple);
 	        	
 	        	dvdResponse = validateMsisdn2AndNetworkCode(dvdRequestVO.getData().getMsisdn(),requestVO.getMsisdn2(), requestVO.getExtnwcode(), true);
 	        	if(!dvdResponse.getStatus().equals("200")) {
 	        		return dvdResponse;
 	        	}
 	        	dvdResponse = validatePinForDVD(dvdRequestVO.getData().getMsisdn(), requestVO.getPin());
 	        	if(!dvdResponse.getStatus().equals("200")) {
 	        		return dvdResponse;
 	        	}
 	   
				final List<DvdSwaggVoucherDetails> voucherDetailsList = requestVO.getVoucherDetails();
				
				VomsProductDAO vomsProductDAO = new VomsProductDAO();
				if(!BTSLUtil.isNullorEmpty(voucherDetailsList) && voucherDetailsList.size() > 0) {
					checkValidListSize(voucherDetailsList.size()); //throw BTSLBaseException
					for(DvdSwaggVoucherDetails voucherDetails : voucherDetailsList) {
			        	rowCount += 1;
			        	
			        	boolean isVoucherDetailsValid = validateVoucherDetails(rowCount, voucherDetails);
			        	if(!isVoucherDetailsValid) {
			        		failureCount += 1;
			        		continue;
			        	}
			        	
			        	/*boolean isProfileNotExist = checkDuplicateProfile(voucherDetails, profileSet);//false if profile already processed
			        	if(!isProfileNotExist) {
			        		createFailureResponseForDuplicateProfile(voucherDetails.getVoucherProfile(),  voucherDetails.getDenomination());
			        		continue;
			        	}*/
			        	createCommonReqToProcess(requestVO, voucherDetails, rowCount);
			        	waitForNextDVD();
			        	
			            jsonResponse = processRequestChannel(dvdRequestVO, responseSwag, responseBasicDetails, httpServletRequest, vomsVoucherList);
			            
			            //getting profile name
			            String voucherProfileName = vomsProductDAO.getProductName(con, voucherDetails.getVoucherProfile());
			        	createResponse(jsonResponse, responseBasicDetails.get("transactionID"), voucherProfileName, voucherDetails.getVoucherProfile(), vomsVoucherList, responseBasicDetails.get("transactionDateTime"));
			        }
				}else {
		        	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_VOUCHER_DETAILS);
		        }
				
				createFinalResponse(rowCount);
		        return dvdResponse;
		        
 	       } catch (BTSLBaseException be) {
 				log.error(methodName, "Exception:e=" + be);
 				log.errorTrace(methodName, be);
 				
 				if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage()) ){
 					dvdResponse.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
 	            	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
 	            }else{
 	            	dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
 	                responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
 	             }
 				
 				String resmsg = RestAPIStringParser.getMessage(
 						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
 						be.getArgs());
 				dvdResponse.setMessageCode(be.getMessage());
 				dvdResponse.setMessage(resmsg);

 			} catch (Exception e) {
 				log.error(methodName, "Exceptin:e=" + e);
 				log.errorTrace(methodName, e);
 				
 				dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
 				 responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
 				dvdResponse.setMessageCode(e.toString());
 				dvdResponse.setMessage(e.toString() + " : " + e.getMessage());
 			} finally {
 				if (mcomCon != null) {
 					mcomCon.close("C2SServiceImpl#processRequestDVD");
 					mcomCon = null;
 				}
 				if (log.isDebugEnabled()) {
 					log.debug(methodName, dvdResponse);
 					log.debug(methodName, "Exiting ");
 				}
 			}
 	       return dvdResponse;

	}
	
	/**
	 * 
	 * @param senderMSISDN
	 * @param receiverMSISDN
	 * @param extNwCode
	 * @param p_isManOrOpt
	 * @return
	 */
	public DvdApiResponse validateMsisdn2AndNetworkCode(String senderMSISDN, String receiverMSISDN, String extNwCode ,boolean p_isManOrOpt) {
		try {
			if (senderMSISDN.equals(receiverMSISDN)) {
                throw new BTSLBaseException(this, "validateMsisdn2AndNetworkCode", PretupsErrorCodesI.CHNL_ERROR_SELF_VOUCHER_DIST_NOTALLOWED);
            }
			
			
			XMLTagValueValidation.validateExtNetworkCode(extNwCode, p_isManOrOpt);
			XMLTagValueValidation.validateMsisdn2(receiverMSISDN, extNwCode,p_isManOrOpt);
			dvdResponse.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
			return dvdResponse;
		} catch (BTSLBaseException be) {
			be.printStackTrace();
			String errorCode = (be.getMessageKey() != null) ? be.getMessageKey(): null;
			String[] args = (be.getArgs()!= null) ? be.getArgs() : null;
			
			log.debug("C2SServiceImpl:validateMsisdn2 = ",   errorCode + " | getArgs = " + args);
			String message = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					errorCode, args );
			dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			dvdResponse.setMessage(message);
			dvdResponse.setMessageCode(be.getMessageKey());
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);

			return dvdResponse;
		}
		catch (Exception e) {
			e.printStackTrace();
			dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			return dvdResponse;
		}
		
	}
	
	
	/**
	 * 
	 * @param msisdn
	 * @param pin
	 * @return
	 */
	public DvdApiResponse validatePinForDVD(String msisdn, String pin) {
		final String methodName = "validatePinForDVD";
		
		Connection con = null;
		MComConnectionI mcomCon = null;

		ChannelUserVO senderVO;
		try {
			 mcomCon = new MComConnection();con=mcomCon.getConnection();
			senderVO = new ChannelUserDAO().loadChannelUserDetails(con, msisdn);
			if (senderVO.getUserPhoneVO().getPinRequired().equals(PretupsI.YES)) {
				try {
					if (!BTSLUtil.isNullString(pin)) {
						ChannelUserBL.validatePIN(con, senderVO, pin);
						dvdResponse.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
					} else {
						throw new BTSLBaseException(PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
					}
				} catch (BTSLBaseException be) {

					if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
							|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
						OracleUtil.commit(con);
					}
					dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
					dvdResponse.setMessage(PretupsRestUtil.getMessageString(be.getMessageKey(), null));
					dvdResponse.setMessageCode(be.getMessageKey());
					responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);

					return dvdResponse;
				}
			}
		} catch (BTSLBaseException be) {
			be.printStackTrace();
			dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			dvdResponse.setMessage(PretupsI.FAIL);
			dvdResponse.setMessageCode(be.getMessageKey());
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			dvdResponse.setMessage(PretupsI.FAIL);
		}finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("FetchChannelUserDetailsController#" + "validatePinForDVD");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			LogFactory.printLog(methodName, " Exited ", log);
		}
		return dvdResponse;
	}
	
	/**
	 * 
	 * @param size
	 * @throws BTSLBaseException
	 */
	private void checkValidListSize(int size) throws BTSLBaseException{
		int validListSize = 5; // load from db
		if(size > validListSize) {
			throw new BTSLBaseException(this, "checkValidListSize", PretupsErrorCodesI.INVALID_LIST_SIZE_FOR_DVD,
					new String[] { String.valueOf(validListSize) }); 
		}
	}
	
	/**
	 * 
	 * @param rowCount
	 * @param voucherDetails
	 * @return
	 */
	private boolean validateVoucherDetails(int rowCount, DvdSwaggVoucherDetails voucherDetails) {
	    boolean isVomsDetailsNotNull = true;
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
		RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
		String message = null;
		String errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
		
		if(!BTSLUtil.isNullorEmpty(voucherDetails.getVoucherType())
				&&  !BTSLUtil.isNullorEmpty(voucherDetails.getVoucherSegment())
				&& !BTSLUtil.isNullorEmpty(voucherDetails.getDenomination()) 
				&& !BTSLUtil.isNullorEmpty(voucherDetails.getVoucherProfile())
				&& !BTSLUtil.isNullorEmpty(voucherDetails.getQuantity()))
		{
			return isVomsDetailsNotNull;
		}else {
			if(BTSLUtil.isNullorEmpty(voucherDetails.getVoucherType())){
				isVomsDetailsNotNull = false;
				message = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
						errorCode, new String[] { "VOUCHERTYPE" });
				createFailureResponseForVoucherDetails(masterErrorLists, message, errorCode );
			}
			
	         if(BTSLUtil.isNullorEmpty(voucherDetails.getVoucherSegment())){
	        	 isVomsDetailsNotNull = false;
	        	 message = RestAPIStringParser.getMessage(
	 					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
	 					errorCode, new String[] { "VOUCHERSEGMENT" });
	 			createFailureResponseForVoucherDetails(masterErrorLists, message, errorCode );
			}
	         
	         if(BTSLUtil.isNullorEmpty(voucherDetails.getDenomination())){
	        	 isVomsDetailsNotNull = false;
	        	 message = RestAPIStringParser.getMessage(
	 					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
	 					errorCode, new String[] { "DENOMINATION" });
	 			createFailureResponseForVoucherDetails(masterErrorLists, message, errorCode );
	 		}
	         
	         if(BTSLUtil.isNullorEmpty(voucherDetails.getVoucherProfile())){
	        	 isVomsDetailsNotNull = false;
	        	 message = RestAPIStringParser.getMessage(
	 					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
	 					errorCode, new String[] { "VOUCHERPROFILE" });
	        	 
	 			createFailureResponseForVoucherDetails(masterErrorLists, message, errorCode );
	 		}
	         
	         if(BTSLUtil.isNullorEmpty(voucherDetails.getQuantity())){
	        	 isVomsDetailsNotNull = false;
	        	 message = RestAPIStringParser.getMessage(
	 					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
	 					errorCode, new String[] { "QUANTITY" });
	        	 
	 			createFailureResponseForVoucherDetails(masterErrorLists, message, errorCode );
	 		} 
		}
		 	
     	rowErrorMsgListsObj.setRowName("row: " + rowCount);
		rowErrorMsgListsObj.setRowValue(String.valueOf(rowCount));
		rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
		rowErrorMsgListsFinal.add(rowErrorMsgListsObj);
		
		
         
         return isVomsDetailsNotNull;
		/*return ObjectUtils.allNotNull(voucherDetails.getDenomination(), voucherDetails.getQuantity(), voucherDetails.getVoucherProfile(), 
				voucherDetails.getVoucherSegment(), voucherDetails.getVoucherType());*/
		
	}
	
	/**
	 * 
	 * @param masterErrorLists
	 * @param message
	 * @param errorCode
	 */
	private void createFailureResponseForVoucherDetails(ArrayList<MasterErrorList> masterErrorLists, String message, String errorCode) {
		MasterErrorList masterErrorListObj = new MasterErrorList();
		
		masterErrorListObj.setErrorCode(errorCode);
		masterErrorListObj.setErrorMsg(message);
		masterErrorLists.add(masterErrorListObj);
		
		
	}
	
	/**
	 * 
	 * @param voucherDetails
	 * @param profileSet
	 * @return
	 */
	private boolean checkDuplicateProfile(DvdSwaggVoucherDetails voucherDetails, Set<String> profileSet){
		return profileSet.add(voucherDetails.getVoucherProfile());
	}
	
	/**
	 * 
	 * @param requestVO
	 * @param voucherDetails
	 * @param rowCount
	 */
	public void createCommonReqToProcess(DvdSwaggRequestVO requestVO, DvdSwaggVoucherDetails voucherDetails, int rowCount){
		
        dvdDetails.setVoucherprofile(voucherDetails.getVoucherProfile());
        dvdDetails.setVouchersegment(voucherDetails.getVoucherSegment());
        dvdDetails.setVouchertype(voucherDetails.getVoucherType());
        dvdDetails.setAmount(voucherDetails.getDenomination());
        dvdDetails.setQuantity(voucherDetails.getQuantity());
        
        dvdDetails.setDate("");
        dvdDetails.setExtnwcode(requestVO.getExtnwcode());  //must
        //dvdDetails.setExtcode("");
        dvdDetails.setExtrefnum(dvdDetails.getExtcode());
        dvdDetails.setLanguage1(requestVO.getLanguage1());
        dvdDetails.setLanguage2(requestVO.getLanguage2());
        dvdDetails.setPin(requestVO.getPin());
        dvdDetails.setMsisdn2(requestVO.getMsisdn2());
        dvdDetails.setSelector(requestVO.getSelector());
        dvdDetails.setSendSms(requestVO.getSendSms());
        dvdDetails.setRowCount(rowCount); 
        dvdRequestVO.setData(dvdDetails);
        
	}
	
	private void waitForNextDVD(){
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			log.debug("waitForNextDVD", "Sleeping thread get interrupted.");
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 
	 * @param requestVO1
	 * @param responseSwag
	 * @param responseBasicDetails
	 * @return
	 */
	public PretupsResponse<JsonNode> processRequestChannel(DvdRequestVO requestVO1, HttpServletResponse responseSwag,
			HashMap<String,String> responseBasicDetails, HttpServletRequest httpServletRequest, ArrayList<VomsVoucherVO> vomsVoucherList)  {
        PretupsResponse<JsonNode> response = new PretupsResponse<>();
        final String methodName = "processRequestChannel";
        StringBuilder loggerValue= new StringBuilder(); 
        StringBuilder eventhandler= new StringBuilder(); 
        final RequestVO requestVO = new RequestVO();
        String message = null;
        Connection con = null;MComConnectionI mcomCon = null;
        NetworkPrefixVO networkPrefixVO = null;
        ChannelUserVO channelUserVO = null;
        final Date currentDate = new Date();
        SimProfileVO simProfileVO = null;
        final long requestStartTime = System.currentTimeMillis();
        long requestEndTime = 0;
        GatewayParsersI gatewayParsersObj = null;
        String networkID = null;
        String externalInterfaceAllowed = null;
        String filteredMSISDN= null;
        String ERROR =null;
        try {
            String instanceCode = Constants.getProperty("INSTANCE_ID");
            requestVO.setReqContentType(httpServletRequest.getContentType());
            requestVO.setModule(PretupsI.C2S_MODULE);
            requestVO.setInstanceID(instanceCode);
            requestVO.setCreatedOn(currentDate);
            requestVO.setLocale(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            requestVO.setDecreaseLoadCounters(false);
            requestVO.setRequestStartTime(requestStartTime);
            requestVO.setServiceKeyword(PretupsI.SERVICE_TYPE_DVD);
            requestVO.setActionValue(PretupsI.CHANNEL_RECEIVER_ACTION);
            requestVO.setTxnBatchId(txnBatchId);
            mcomCon = new MComConnection();con=mcomCon.getConnection();
 	       
            ObjectMapper mapper = new ObjectMapper();
            JsonNode request = mapper.valueToTree(requestVO1);
            requestVO.setRequestMessageOrigStr(request.toString());
            requestVO.setLogin(requestVO1.getReqGatewayLoginId());
            requestVO.setSendSms(requestVO1.getData().getSendSms());            	
            
            response = parseRequestfromJson(request, requestVO, httpServletRequest);
            if (!response.getStatus()) {
                return response;
            }
            PretupsBL.validateRequestMessageGateway(requestVO);

            gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO()
                    .getHandlerClass());

            response = parseRequestForMessage(request, requestVO);
            if (!response.getStatus()) {
                return response;
            }

            ChannelGatewayRequestLog.inLog(requestVO);
          
            gatewayParsersObj.parseChannelRequestMessage(requestVO, con);

            gatewayParsersObj.validateUserIdentification(requestVO);
            filteredMSISDN = requestVO.getFilteredMSISDN();
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
            }
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getRequestGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQ_MESSAGE_GATEWAY_NOT_ACTIVE);
            }

            gatewayParsersObj.loadValidateNetworkDetails(requestVO);

            networkPrefixVO = (NetworkPrefixVO) requestVO.getValueObject();

            networkID = networkPrefixVO.getNetworkCode();
            requestVO.setRequestNetworkCode(networkID);

            String requestHandlerClass;

            channelUserVO = gatewayParsersObj.loadValidateUserDetails(con, requestVO);

            if (requestVO.getMessageGatewayVO().getAccessFrom() == null
                    || requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.ACCESS_FROM_PHONE)) {
                PretupsBL
                        .unBarredUserAutomaic(con, channelUserVO.getUserPhoneVO().getMsisdn(),
                                networkPrefixVO.getNetworkCode(), PretupsI.C2S_MODULE, PretupsI.USER_TYPE_SENDER,
                                channelUserVO);
            }

            channelUserVO.setModifiedOn(currentDate);
            final UserPhoneVO userPhoneVO = channelUserVO.getUserPhoneVO();
            userPhoneVO.setModifiedBy(channelUserVO.getUserID());
            userPhoneVO.setModifiedOn(currentDate);
            if (channelUserVO.isStaffUser() && PretupsI.NOT_AVAILABLE.equals(userPhoneVO.getSmsPin())) {
                final ChannelUserVO parentVO = (ChannelUserVO) requestVO.getSenderVO();
                parentVO.setModifiedOn(currentDate);
                final UserPhoneVO parentPhoneVO = parentVO.getUserPhoneVO();
                parentPhoneVO.setModifiedBy(channelUserVO.getUserID());
                parentPhoneVO.setModifiedOn(currentDate);
            }
            
            if (userPhoneVO.getLastAccessOn() == null) {
                userPhoneVO.setAccessOn(true);
            }
            if (requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.ACCESS_FROM_PHONE)) {
                userPhoneVO.setLastAccessOn(currentDate);
            }
            LogFactory.printLog(methodName, "Sender Locale in Request if any" + requestVO.getSenderLocale(), log);
            if (requestVO.getSenderLocale() != null) {
                requestVO.setLocale(requestVO.getSenderLocale());
            } else {
                requestVO.setSenderLocale(requestVO.getLocale());
            }

            if (!requestVO.isMessageAlreadyParsed()) {
                PretupsBL.isPlainMessageAndAllowed(requestVO);

                if (!requestVO.isPlainMessage()) {
                    PretupsBL.getEncryptionKeyForUser(con, requestVO);
                    simProfileVO = SimProfileCache.getSimProfileDetails(userPhoneVO.getSimProfileID());
                    PretupsBL.parseBinaryMessage(requestVO, simProfileVO);
                }
            }
            try {
				gatewayParsersObj.checkRequestUnderProcess(con, requestVO, PretupsI.C2S_MODULE, true, channelUserVO);
			} catch (BTSLBaseException e1) {
				log.errorTrace(methodName, e1);
				throw e1;
			} finally {
				mcomCon.finalCommit();
			}
            if(mcomCon != null)
            {
            	mcomCon.close("RestReceiver#processRequestChannel");
            	mcomCon=null;
            }
            MessageFormater.handleChannelMessageFormat(requestVO, channelUserVO);

            populateLanguageSettings(requestVO, channelUserVO);
            final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);

            if (serviceKeywordCacheVO == null) {
            	eventhandler.setLength(0);
            	eventhandler.append("Service keyword not found for the keyword=");
            	eventhandler.append(requestVO.getRequestMessageArray()[0]);
            	eventhandler.append(GATEWAYTYPE);
            	eventhandler.append(requestVO.getRequestGatewayType());
            	eventhandler.append(SERVICEPORT);
            	eventhandler.append(requestVO.getServicePort());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED,
                        EventLevelI.INFO, PROCESSREQUEST, null, filteredMSISDN, "",eventhandler.toString());
                throw new BTSLBaseException(RSTRECEIVER, methodName,
                        PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD);
            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
            	eventhandler.setLength(0);
            	eventhandler.append("Service keyword suspended for the keyword=");
            	eventhandler.append(requestVO.getRequestMessageArray()[0]);
            	eventhandler.append(GATEWAYTYPE);
            	eventhandler.append(requestVO.getRequestGatewayType());
            	eventhandler.append(SERVICEPORT);
            	eventhandler.append(requestVO.getServicePort());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED,
                        EventLevelI.INFO, PROCESSREQUEST, null, filteredMSISDN, "",eventhandler.toString());
                throw new BTSLBaseException(RSTRECEIVER, methodName,
                        PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
            }
            requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
            requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
            requestVO.setType(serviceKeywordCacheVO.getType());
            requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
            requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());

            channelUserVO.setServiceTypes(requestVO.getServiceType());
            response = validateServiceType(requestVO, serviceKeywordCacheVO, channelUserVO);  //
            if (!response.getStatus()) {
                return response;
            }
            externalInterfaceAllowed = serviceKeywordCacheVO.getExternalInterface();
            requestVO.setGroupType(serviceKeywordCacheVO.getGroupType());

            final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL
                    .getServiceKeywordHandlerObj(requestHandlerClass);
            controllerObj.process(requestVO);


        } catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            requestVO.setSuccessTxn(false);
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append(BTSLBASEEXP);
            	loggerValue.append(be.getMessage());
                log.debug(methodName,  loggerValue);
            }
            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                message = requestVO.getSenderReturnMessage();
            }
            if (be.isKey()) {
            	ERROR=be.getMessageKey();
                response.setMessageCode(be.getMessageKey());
                response.setResponse(PretupsI.RESPONSE_SUCCESS, true,
                        PretupsRestUtil.getMessageString(be.getMessageKey(), be.getArgs()));
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else {
                requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            
            

        } catch (Exception e) {
            requestVO.setSuccessTxn(false);
            loggerValue.setLength(0);
            loggerValue.append("Exception e:");
            loggerValue.append(e.getMessage());
            log.error(methodName, loggerValue );
            log.errorTrace(methodName, e);
            if (BTSLUtil.isNullString(requestVO.getMessageCode())) {
                requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            eventhandler.setLength(0);
            eventhandler.append("Exception in ChannelReceiver:");
            eventhandler.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    PROCESSREQUEST, "", "", "",  eventhandler.toString());
            
            
            
            
        } finally {

            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                message = requestVO.getSenderReturnMessage();
            }
            if(PretupsErrorCodesI.NO_EXPIRY_IN_PAYLOAD.equals(ERROR)||PretupsErrorCodesI.INVALID_TOKEN_FORMAT.equals(ERROR)||PretupsErrorCodesI.MAPP_INVALID_TOKEN.equals(ERROR)||PretupsErrorCodesI.MAPP_TOKEN_EXPIRED.equals(ERROR)||PretupsErrorCodesI.UNAUTHORIZED_REQUEST.equals(ERROR))
            {
            	 RestAPIStringParser.generateJsonResponse(requestVO);
            	 prepareJsonResponse(requestVO);
            	 response = requestVO.getJsonReponse();
            }
            else
            {
            	if (gatewayParsersObj == null) {
            	
                try {
                    gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO
                            .getMessageGatewayVO().getHandlerClass());
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                if (log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("gatewayParsersObj=");
                	loggerValue.append(gatewayParsersObj);
                    log.debug(this,  loggerValue);
                }
            }

            if (requestVO.getMessageGatewayVO() != null) {
            	loggerValue.setLength(0);
            	loggerValue.append("Gateway Time out=");
            	loggerValue.append(requestVO.getMessageGatewayVO().getTimeoutValue());
                LogFactory.printLog(methodName,loggerValue.toString(), log);
            }

            if (requestVO.getSenderLocale() != null) {
                requestVO.setLocale(requestVO.getSenderLocale());
            }

            if (gatewayParsersObj != null) {
                gatewayParsersObj.generateChannelResponseMessage(requestVO);
            } else {
                if (!BTSLUtil.isNullString(requestVO.getReqContentType())
                        && PretupsI.JSON_CONTENT_TYPE.equals(requestVO.getReqContentType()) ) {
                    prepareJsonResponse(requestVO);
                } else {
                    if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                        message = requestVO.getSenderReturnMessage();
                    } else {
                        message = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(),
                                requestVO.getMessageArguments());
                    }

                    requestVO.setSenderReturnMessage(message);
                }
            }
            response = requestVO.getJsonReponse();   //
            try {
                String reqruestGW = requestVO.getRequestGatewayCode();
                final String altrnetGW = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
                if ((!BTSLUtil.isNullString(altrnetGW) && (altrnetGW.split(":")).length >= 2)
                        && reqruestGW.equalsIgnoreCase(altrnetGW.split(":")[0])) {
                    reqruestGW = (altrnetGW.split(":")[1]).trim();
                    loggerValue.setLength(0);
                    loggerValue.append("processRequest: Sender Message push through alternate GW");
                    loggerValue.append(reqruestGW);
                    loggerValue.append("Requested GW was:");
                    loggerValue.append(requestVO.getRequestGatewayCode());
                    LogFactory.printLog(methodName, loggerValue.toString(), log);
                }
                int messageLength = 0;
                final String messLength = BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
                if (!BTSLUtil.isNullString(messLength)) {
                    messageLength = (new Integer(messLength)).intValue();
                }

                if (!(!BTSLUtil.isNullString(requestVO.getReqContentType()))
                        && !PretupsI.GATEWAY_TYPE_USSD.equals(requestVO.getRequestGatewayType())) {
                 

                    message = requestVO.getSenderReturnMessage();
                    String message1 = null;
                    if ((messageLength > 0) && (message.length() > messageLength)) {
                        message1 = BTSLUtil.getMessage(requestVO.getLocale(), PretupsErrorCodesI.REQUEST_IN_QUEUE_UB,
                                requestVO.getMessageArguments());
                        final PushMessage pushMessage1 = new PushMessage(requestVO.getFilteredMSISDN(), message1,
                                requestVO.getRequestIDStr(), requestVO.getRequestGatewayCode(), requestVO.getLocale());
                        pushMessage1.push();
                        requestVO.setRequestGatewayCode(reqruestGW);
                    }
                    message = requestVO.getSenderReturnMessage();

                } else {
                    message = requestVO.getSenderReturnMessage();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Locale=");
            	loggerValue.append(requestVO.getLocale());
            	loggerValue.append(" requestEndTime=");
            	loggerValue.append(requestEndTime);
            	loggerValue.append (REQSTART);
            	loggerValue.append(requestStartTime);
            	loggerValue.append(MESSAGECODE);
            	loggerValue.append(requestVO.getMessageCode());
            	loggerValue.append(ARGS);
            	loggerValue.append(requestVO.getMessageArguments());
                log.debug(methodName,  loggerValue );
            }
            if (requestVO.getMessageGatewayVO() == null
                    || requestVO.getMessageGatewayVO().getResponseType()
                            .equalsIgnoreCase(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE)
                    || (requestEndTime - requestStartTime) / 1000 < requestVO.getMessageGatewayVO().getTimeoutValue()) {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE);

                if (requestVO.isSuccessTxn() && requestVO.isSenderMessageRequired()
                        && (PretupsI.JSON_CONTENT_TYPE.equalsIgnoreCase(requestVO.getReqContentType())||("c2cvomstrfini").equalsIgnoreCase(requestVO.getServiceKeyword())
                        		||("c2cvomstrf").equalsIgnoreCase(requestVO.getServiceKeyword())||("c2ctrfini").equalsIgnoreCase(requestVO.getServiceKeyword()))
                        && !PretupsI.YES.equals(externalInterfaceAllowed)) {
                    final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(),
                            requestVO.getMessageArguments());
                    final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), senderMessage,
                            requestVO.getRequestIDStr(), requestVO.getRequestGatewayCode(), requestVO.getLocale());

                    if (!BTSLUtil.isNullString(senderMessage) && !"null".equalsIgnoreCase(senderMessage)) {
                        pushMessage.push();
                    }
                }
            } else {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_PUSH);
                if (!PretupsI.KEYWORD_TYPE_ADMIN.equals(requestVO.getServiceType())
                        && requestVO.isSenderMessageRequired()) {

                    final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(),
                            requestVO.getSenderReturnMessage(), requestVO.getRequestIDStr(),
                            requestVO.getRequestGatewayCode(), requestVO.getLocale());
                    pushMessage.push();

                }
            }
            }
            ChannelGatewayRequestLog.outLog(requestVO);
            LogFactory.printLog(methodName, "Exiting", log);
            if(mcomCon != null){mcomCon.close("RestReceiver#processRequestChannel");mcomCon=null;}
        }
        
        if(response!=null && response.getDataObject()!=null && response.getDataObject().get("txnbatchid").textValue() != null){ // get txnBatchId
        	txnBatchId = response.getDataObject().get("txnbatchid").textValue();
        }
        String voucherProfile = requestVO.getVoucherProfile();
        String transactionID = requestVO.getTransactionID();
        responseBasicDetails.put("voucherProfile", voucherProfile);
        responseBasicDetails.put("transactionID", transactionID);
        responseBasicDetails.put("transactionDateTime", BTSLDateUtil.getSystemLocaleDate(requestVO.getTxnDate(), PreferenceI.SYSTEM_DATETIME_FORMAT));
        if(requestVO.getVomsVoucherList() != null && requestVO.getVomsVoucherList().size()>0) {
        	vomsVoucherList.addAll(requestVO.getVomsVoucherList());
        }
        
        return response;
    }
	
	
	/**
	 * 
	 * @param request
	 * @param requestVO
	 * @return
	 * @throws BTSLBaseException
	 */
	public PretupsResponse<JsonNode> parseRequestfromJson(JsonNode request, RequestVO requestVO, HttpServletRequest httpServletRequest)
            throws BTSLBaseException {
        PretupsResponse<JsonNode> response = new PretupsResponse<>();
        final String methodName = "parseRequestfromJson";
        if (BTSLUtil.isNullString(requestVO.getReqContentType())) {
            requestVO.setReqContentType("CONTENT_TYPE");
        }
        String reqGatewayCode = getNodeValue(request, "reqGatewayCode");
        String reqGatewayType = getNodeValue(request, "reqGatewayType");
        LogFactory.printLog(methodName, " reqGatewayCode " + reqGatewayCode + "reqGatewayType " + reqGatewayType, log);

        if (BTSLUtil.isNullString(reqGatewayCode)) {
            response.setMessageCode(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTID);
            response.setResponse(PretupsI.RESPONSE_FAIL, false,
                    PretupsRestUtil.getMessageString(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTID));
            return response;
        } else {
            requestVO.setRequestGatewayCode(reqGatewayCode.trim());
        }
        if (BTSLUtil.isNullString(reqGatewayType)) {
            response.setMessageCode(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTTYPE);
            response.setResponse(PretupsI.RESPONSE_FAIL, false,
                    PretupsRestUtil.getMessageString(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTTYPE));
            return response;
        } else {
            requestVO.setRequestGatewayType(reqGatewayType.trim());
        }

        requestVO.setLogin(getNodeValue(request, "reqGatewayLoginId"));
        requestVO.setPassword(getNodeValue(request, "reqGatewayPassword"));
        requestVO.setServicePort(getNodeValue(request, "servicePort"));
        requestVO.setRemoteIP(httpServletRequest.getRemoteAddr());
        requestVO.setSourceType(getNodeValue(request, "sourceType"));

        response.setStatus(true);
        return response;

    }
	
	/**
	 * 
	 * @param request
	 * @param prequestVO
	 * @return
	 * @throws BTSLBaseException
	 */
	public PretupsResponse<JsonNode> parseRequestForMessage(JsonNode request, RequestVO prequestVO)
            throws BTSLBaseException {
        final String methodName = "parseRequestForMessage";
        PretupsResponse<JsonNode> response = new PretupsResponse<>();
        String msisdn;
        String requestMessage;
        String loginID;
        JsonNode data;
        if (request != null) {
            data = request.get("data");
            requestMessage = data.toString();
        } else {
            response.setMessageCode(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
            response.setResponse(PretupsI.RESPONSE_FAIL, false,
                    PretupsRestUtil.getMessageString(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE));
            return response;
        }
        msisdn = getNodeValue(request, "msisdn");
        if ((BTSLUtil.isNullString(msisdn) || BTSLUtil.isNullString(requestMessage))
                && PretupsI.JSON_CONTENT_TYPE.equals("application/json")) {
            prequestVO.setRequestMessage(requestMessage);
            prequestVO.setReqContentType("application/json");

        }
        if (!BTSLUtil.isNullString(msisdn)) {
            prequestVO.setRequestMSISDN(msisdn);
        }
        
        
        LogFactory.printLog(methodName, "requestMessage " + requestMessage, log);
        prequestVO.setRequestMessage(requestMessage);
        response.setStatus(true);
        return response;

    }
	 /**
	  * 
	  * @param node
	  * @param value
	  * @return
	  */
	 private String getNodeValue(JsonNode node, String value) {
        if (node.get(value) != null) {
            return node.get(value).textValue();
        } else {
            return "";
        }
    }
	 
	 /**
	  * 
	  * @param prequestVO
	  * @param channelUserVO
	  */
	 private void populateLanguageSettings(RequestVO prequestVO, ChannelUserVO channelUserVO) {
         final FixedInformationVO fixedInformationVO = (FixedInformationVO) prequestVO.getFixedInformationVO();
         if (prequestVO.getLocale() == null) {
             prequestVO.setLocale(new Locale((channelUserVO.getUserPhoneVO()).getPhoneLanguage(), (channelUserVO
                     .getUserPhoneVO()).getCountry()));
         }
         if (fixedInformationVO != null) {
             PretupsBL.getCurrentLocale(prequestVO, channelUserVO.getUserPhoneVO());
         }
     }
	 
	 
	 /**
	  * 
	  * @param prequestVO
	  * @param pserviceKeywordCacheVO
	  * @param channelUserVO
	  * @return
	  * @throws BTSLBaseException
	  */
	 private PretupsResponse<JsonNode> validateServiceType(RequestVO prequestVO,
             ServiceKeywordCacheVO pserviceKeywordCacheVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
         PretupsResponse<JsonNode> response = new PretupsResponse<>();
         final String methodName = "validateServiceType";
         final String serviceType = prequestVO.getServiceType();
         if (PretupsI.C2S_MODULE.equalsIgnoreCase(prequestVO.getModule())&&(PretupsI.YES.equals(pserviceKeywordCacheVO.getExternalInterface())
                 || PretupsI.AUTO_ASSIGN_SERVICES.equals(pserviceKeywordCacheVO.getExternalInterface()))) {
             final ListValueVO listValueVO = BTSLUtil.getOptionDesc(serviceType,
                     channelUserVO.getAssociatedServiceTypeList());
             if (listValueVO == null || BTSLUtil.isNullString(listValueVO.getLabel())) {
                 LogFactory.printError(methodName, " MSISDN=" + prequestVO.getFilteredMSISDN()
                         + " Service Type not found in allowed List", log);
                 response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
                 response.setResponse(PretupsI.RESPONSE_FAIL, false,
                         PretupsRestUtil.getMessageString(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED));
                 return response;
             } else if (PretupsI.C2S_MODULE.equalsIgnoreCase(prequestVO.getModule())&&!PretupsI.YES.equals(listValueVO.getLabel())) {
                 LogFactory.printError(methodName, " MSISDN=" + prequestVO.getFilteredMSISDN()
                         + " Service Type is suspended in allowed List", log);
                 response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_SUSPENDED);
                 response.setResponse(PretupsI.RESPONSE_FAIL, false,
                         PretupsRestUtil.getMessageString(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_SUSPENDED));
                 return response;
             }

         }
         if (PretupsI.OPT_MODULE.equalsIgnoreCase(prequestVO.getModule()) && PretupsI.YES.equals(pserviceKeywordCacheVO.getExternalInterface())) {
             final ListValueVO listValueVO = BTSLUtil.getOptionDesc(serviceType, channelUserVO.getAssociatedServiceTypeList());
             if (listValueVO == null || BTSLUtil.isNullString(listValueVO.getLabel())) {
                 log.error("validateServiceType", prequestVO.getRequestIDStr(), " MSISDN=" + prequestVO.getFilteredMSISDN() + " Service Type not found in allowed List");
                 response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
                 response.setResponse(PretupsI.RESPONSE_FAIL, false,
                         PretupsRestUtil.getMessageString(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED));
                 return response;
             } else if (!PretupsI.YES.equals(listValueVO.getLabel())) {
                 log.error("validateServiceType", prequestVO.getRequestIDStr(),
                     " MSISDN=" + prequestVO.getFilteredMSISDN() + " Service Type is suspended in allowed List");
                 response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
                 response.setResponse(PretupsI.RESPONSE_FAIL, false,
                         PretupsRestUtil.getMessageString(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_SUSPENDED));
                 return response;
             }// end if
         }
         response.setStatus(true);
         return response;
     }
	 
	 /**
	  * MethodName prepareJsonResponse 
	  * @param prequestVO
	  */
	 protected void prepareJsonResponse(RequestVO prequestVO) {
         final String methodName = "prepareJsonResponse";
         final java.util.Date date = new java.util.Date();
         final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
         String responseStr;
         LogFactory.printLog(methodName, "Entered", log);
         try {
             PretupsResponse<JsonNode> response = new PretupsResponse<>();
             JsonObject json = new JsonObject();
             if (!BTSLUtil.isNullString(prequestVO.getMessageCode())) {
                 if (prequestVO.isSuccessTxn()) {
                     json.addProperty(TXNSTATUS, PretupsI.TXN_STATUS_SUCCESS);

                 } else {
                     String message = prequestVO.getMessageCode();
                     if (message.indexOf('_') != -1) {
                         message = message.substring(0, message.indexOf('_'));
                     }
                     json.addProperty(TXNSTATUS, message);
                 }

                 sdf.setLenient(false);
                 json.addProperty(DATE.toUpperCase(), sdf.format(date));
                 json.addProperty(MESSAGE,
                         PretupsRestUtil.getMessageString(prequestVO.getMessageCode(), prequestVO.getMessageArguments()));
             }
             responseStr = json.toString();
             response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,
                     (JsonNode) PretupsRestUtil.convertJSONToObject(responseStr, new TypeReference<JsonNode>() {
                     }));
             prequestVO.setJsonReponse(response);
             prequestVO.setSenderReturnMessage(responseStr);

         } catch (Exception e) {
             log.errorTrace(methodName, e);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                     "ChannelReceiver[prepareJsonResponse]", prequestVO.getRequestIDStr(), "", "",
                     "Exception while generating XML response:" + e.getMessage());
         }
         if (log.isDebugEnabled()) {
             log.debug(methodName, prequestVO.getRequestIDStr(),
                     "Exiting with message=" + prequestVO.getSenderReturnMessage());
         }

     }

	
	/**
	 * 
	 * @param jsonResponse
	 * @param transactionID
	 * @param voucherProfileID
	 */
	private void createResponse(PretupsResponse<JsonNode>  jsonResponse, String transactionID, String voucherProfileName, String voucherProfileID, ArrayList<VomsVoucherVO> vomsVoucherList, String transactionDateTime) {
		
	    if(jsonResponse!=null && jsonResponse.getDataObject()!=null && jsonResponse.getDataObject().get("txnstatus") != null){ //here should be ==200
	    	 String errorCode= null;
	    	 int status =  Integer.parseInt(jsonResponse.getDataObject().get("txnstatus").textValue());
	    	 String message = jsonResponse.getDataObject().get("message").textValue();
	    	 
	    	 if(!BTSLUtil.isNullorEmpty(message)  && !message.endsWith(".")) {
	    		 message = message + ".";
	    	 }
	    	 
	    	 if(transactionID != null) {
	    		 message  = message + " "  + "Transaction ID for this transaction is: " + transactionID;
	    	 }
	    	 
	    	 if(jsonResponse.getDataObject().get("errorcode") != null) {
	    		 errorCode = jsonResponse.getDataObject().get("errorcode").textValue();
	    	 }
	    	
	    	
	    	if(jsonResponse.getDataObject().get("txnstatus").textValue().equals("200")) {
	    		
	    		createSuccessResponse(status, message, errorCode, voucherProfileName, transactionID, voucherProfileID, vomsVoucherList, transactionDateTime);
	    	}else {
	    		createFailureResponse(message, errorCode, transactionID, voucherProfileName, voucherProfileID, transactionDateTime);
	    	}
	     }
	}
	
	
	/**
	 * 
	 * @param status
	 * @param message
	 * @param errorCode
	 * @param transactionID
	 * @param voucherProfileID
	 */
	private void createSuccessResponse(int status, String message, String errorCode, String voucherProfileName, String transactionID, String voucherProfileID, ArrayList<VomsVoucherVO> vomsVoucherList, String transactionDateTime) {
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setStatus(status);
    	baseResponse.setMessage(message);
    	baseResponse.setMessageCode(errorCode);
    	baseResponseList.add(baseResponse);
    	
    	addTransactionDetailsInResponse(rowCount,PretupsI.SUCCESS, transactionID, voucherProfileName, voucherProfileID, vomsVoucherList, transactionDateTime);
    	
    	successCount += 1;
		
	}
	
	/**
	 * 
	 * @param message
	 * @param errorCode
	 * @param transactionID
	 * @param voucherProfileID
	 */
	private void createFailureResponse(String message, String errorCode, String transactionID, String voucherProfileName, String voucherProfileID, String transactionDateTime) {
		MasterErrorList masterErrorListObj = new MasterErrorList();
		RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
		
		failureCount += 1;
		masterErrorListObj.setErrorCode(errorCode);
		masterErrorListObj.setErrorMsg(message);
		masterErrorLists.add(masterErrorListObj);
		
		rowErrorMsgListsObj.setRowName("row: " + rowCount);
		rowErrorMsgListsObj.setRowValue(String.valueOf(rowCount));
		rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
		
		rowErrorMsgListsFinal.add(rowErrorMsgListsObj);
		
		addTransactionDetailsInResponse(rowCount, PretupsI.FAIL, transactionID, voucherProfileName, voucherProfileID, null, transactionDateTime);
	}
	
	/**
	 * 
	 * @param voucherProfileID
	 * @param denomination
	 */
	private void createFailureResponseForDuplicateProfile(String voucherProfileName, String voucherProfileID, String denomination) {
		MasterErrorList masterErrorListObj = new MasterErrorList();
		RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
		
		failureCount += 1;
		String errorCode = PretupsErrorCodesI.DUPLICATE_PROFILE;
		String message = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
				PretupsErrorCodesI.DUPLICATE_PROFILE, new String[] {voucherProfileID, denomination});
		masterErrorListObj.setErrorCode(errorCode);
		masterErrorListObj.setErrorMsg(message);
		masterErrorLists.add(masterErrorListObj);
		
		rowErrorMsgListsObj.setRowName("row" + rowCount);
		rowErrorMsgListsObj.setRowValue(String.valueOf(rowCount));
		rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
		
		rowErrorMsgListsFinal.add(rowErrorMsgListsObj);
		
		addTransactionDetailsInResponse(rowCount, PretupsI.FAIL, null, voucherProfileName, voucherProfileID, null, null);
	}
	
	/**
	 * 
	 * @param rowCount
	 * @param message
	 * @param transactionID
	 * @param voucherProfileID
	 */
	private void addTransactionDetailsInResponse(int rowCount,  String message, String transactionID, String voucherProfileName, String voucherProfileID, ArrayList<VomsVoucherVO> vomsVoucherList, String transactionDateTime) {
		TxnIDBaseResponse txnIdDetailsObj = new TxnIDBaseResponse();
		txnIdDetailsObj.setRow(String.valueOf(rowCount));
    	txnIdDetailsObj.setMessage(message);
    	txnIdDetailsObj.setTransactionID(transactionID);
    	txnIdDetailsObj.setProfileID(voucherProfileID);
    	txnIdDetailsObj.setProfileName(voucherProfileName);
    	txnIdDetailsObj.setTransactionDateTime(transactionDateTime);
    	if(vomsVoucherList != null) {
    		addVoucherListInResponse(txnIdDetailsObj, vomsVoucherList);
    	}
    	txnIdDetailsList.add(txnIdDetailsObj);
	}
	
	private void addVoucherListInResponse(TxnIDBaseResponse txnIdDetailsObj, ArrayList<VomsVoucherVO> vomsVoucherList) {
		List<String> vomsVoucherListResponse = new ArrayList<String>();
		vomsVoucherList.stream().forEach((c) -> vomsVoucherListResponse.add(c.getSerialNo()+":"+VomsUtil.decryptText(c.getPinNo())));
		txnIdDetailsObj.setVoucherList(vomsVoucherListResponse);
	}
	
	/**
	 * 
	 * @param rowCount
	 */
	private void createFinalResponse(int rowCount){
		if(failureCount == rowCount) {
			dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			dvdResponse.setMessage(PretupsI.FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}else {
			dvdResponse.setStatus(String.valueOf(HttpStatus.SC_OK));
			dvdResponse.setMessage(PretupsI.SUCCESS);
			responseSwag.setStatus(HttpStatus.SC_OK);
		}
		dvdResponse.setSuccessList(baseResponseList);
		errorMap.setRowErrorMsgLists(rowErrorMsgListsFinal);
		dvdResponse.setErrorMap(errorMap);
		dvdResponse.setTxnBatchId(txnBatchId);
		dvdResponse.setService(PretupsI.SERVICE_TYPE_DVD);
		dvdResponse.setTxnDetailsList(txnIdDetailsList);
	}


	@Override
	public List<ChannelTransferVO> getReversalList(Connection p_con, UserVO channelUserVO, String senderMsisdn, 
			String receiverMsisdn, String txnId) throws Exception {
		
		String methodName = "getReversalList";

		List<ChannelTransferVO> reversalList = new ArrayList<ChannelTransferVO>();
		final C2STransferWebDAO c2STransferwebDAO = new C2STransferWebDAO();
		final ChannelTransferVO channeltransferVO = new ChannelTransferVO();

		OperatorUtilI operatorUtil = null;
		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();

		if(BTSLUtil.isEmpty(senderMsisdn))
		{
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MSISDN_INVALID_OR_BLANK, 0, null, null);
		}
		if (BTSLUtil.isEmpty(receiverMsisdn) && BTSLUtil.isEmpty(txnId))
		{
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MSISDN_TXNID_BLANK, 0, null, null);

		}
		PrivateRchrgVO prvo = null;
		if ((prvo = operatorUtil.getPrivateRechargeDetails(p_con, receiverMsisdn)) != null) {
			channeltransferVO.setToUserMsisdn(prvo.getMsisdn());
			channeltransferVO.setSubSid(receiverMsisdn);
		} else {
			channeltransferVO.setToUserMsisdn(receiverMsisdn);
		}

		channeltransferVO.setTransferID(txnId);

		reversalList = c2STransferwebDAO.getReversalTransactions(p_con, channeltransferVO, senderMsisdn,
				channelUserVO.getCategoryCode());

		return reversalList;
	}


	@Override
	public GetUserServiceBalanceResponseVO processRequest(String serviceName, MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag) throws BTSLBaseException {
		
		
		 final String METHOD_NAME = "processRequest";
	        if (log.isDebugEnabled()) {
	            log.debug(METHOD_NAME, "Entered");
	        }
			Connection con = null;
			MComConnectionI mcomCon = null;
	    	Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
	    	GetUserServiceBalanceResponseVO response = new GetUserServiceBalanceResponseVO();
	    	ChannelUserWebDAO channelUserWebDAO = null;
	    	BaseResponseMultiple baseResponseMultiple = null;
			OAuthUserData oAuthdata = null;
			OAuthUser oAuthreqVo = null;
			ChannelUserDAO channelUserDAO = null;
			ChannelUserVO  channelUserVO = null;
	        try{
	        	mcomCon = new MComConnection();
	        	con=mcomCon.getConnection();

				baseResponseMultiple = new BaseResponseMultiple();
				oAuthdata = new OAuthUserData();
				oAuthreqVo = new OAuthUser();
				oAuthreqVo.setData(oAuthdata);

				// validate token
				OAuthenticationUtil.validateTokenApi(oAuthreqVo, headers, baseResponseMultiple);
				ArrayList<UserBalanceVO> balList = new ArrayList<UserBalanceVO>();
	        	channelUserWebDAO = new ChannelUserWebDAO();
	        	 channelUserDAO = new ChannelUserDAO();
	        	//channelUserVO = channelUserDAO.loadChannelUserDetails(con, oAuthreqVo.getData().getMsisdn());
	        	 
	        	//anand modifications starts
	        	
	        	String userType = channelUserDAO.loadUserTypeByLoginID(con, oAuthreqVo.getData().getLoginid());
	        	if(userType != null && userType.equalsIgnoreCase(PretupsI.USER_TYPE_STAFF)) {
	        		String parentMsisdn=channelUserDAO.loadParentUserMsisdn(con,oAuthreqVo.getData().getLoginid(),"LOGINID");
	        		channelUserVO = channelUserDAO.loadChannelUserDetails(con, parentMsisdn);
	        	}else if (userType.equalsIgnoreCase(PretupsI.CHANNEL_USER_TYPE)){
					channelUserVO = channelUserDAO.loadChannelUserDetails(con, oAuthreqVo.getData().getMsisdn());
	        	}else if(userType.equalsIgnoreCase(PretupsI.OPERATOR_USER_TYPE)){
					channelUserVO = (ChannelUserVO) new UserDAO().loadAllUserDetailsByLoginID(con,oAuthreqVo.getData().getLoginid());
				}
	        	
	        	//anand modification ends
	        	if(channelUserVO == null ) {
	        		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHANNEL_USR_NOT_FOUND, "");
	        	}
	        	   final ArrayList assignedserviceList = channelUserVO.getAssociatedServiceTypeList();
	               if (assignedserviceList == null || assignedserviceList.isEmpty()) {
	                   throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SVC_NOT_ASSOCIATED, "");
	               }
	               
	               
	               final ArrayList serviceKeywordList = new ArrayList();
	               final ArrayList serviceTypeList = new ArrayList();
	               final ArrayList serviceKeywordCacheList = new ServiceKeywordDAO().loadServiceCache(con);
	               Iterator iterator = serviceKeywordCacheList.iterator();
	               String key = null;
	               ServiceKeywordCacheVO serviceKeywordCacheVO2 = null;
	               while (iterator.hasNext()) {
	                   serviceKeywordCacheVO2 = (ServiceKeywordCacheVO) iterator.next();
	                   if (PretupsI.C2S_MODULE.equals(serviceKeywordCacheVO2.getModule()) && PretupsI.YES.equals(serviceKeywordCacheVO2.getExternalInterface()) && PretupsI.GATEWAY_TYPE_WEB
	                       .equals(serviceKeywordCacheVO2.getRequestInterfaceType())) {
	                       serviceKeywordList.add(serviceKeywordCacheVO2);
	                   }
	               }

	               iterator = serviceKeywordList.iterator();
	               final int assignServiceSize = assignedserviceList.size();
	               ListValueVO listVO = null;
	               while (iterator.hasNext()) {
	                   serviceKeywordCacheVO2 = (ServiceKeywordCacheVO) iterator.next();
	                   for (int i = 0; i < assignServiceSize; i++) {
	                       key = serviceKeywordCacheVO2.getServiceType();

	                       if (key.equalsIgnoreCase(((ListValueVO) assignedserviceList.get(i)).getValue())) {
	                           listVO = new ListValueVO(serviceKeywordCacheVO2.getName(), key);
	                           serviceTypeList.add(listVO);
	                       }
	                   }
	               }
	 
	        	String serviceballist = channelUserWebDAO.loadChannelUserBalanceServiceWise(channelUserVO.getUserID(),balList);
	        	
	        	
	        	String[] servicebal  = serviceballist.split(",");
	        	ArrayList<ServiceViceBalanceVO> serviceBalLst = new ArrayList<ServiceViceBalanceVO>();
	        	if(servicebal.length == 0) {
	        		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SVC_NOT_ASSOCIATED, "");
	        	}else if(userType.equalsIgnoreCase(PretupsI.OPERATOR_USER_TYPE)){
					ServiceViceBalanceVO serviceViceBalanceVO = new ServiceViceBalanceVO();
					serviceViceBalanceVO.setServiceCode(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL);
					serviceViceBalanceVO.setServiceName(null);
					serviceViceBalanceVO.setBalanceAssociated(null);
					serviceBalLst.add(serviceViceBalanceVO);
					response.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
					response.setMessageCode(PretupsErrorCodesI.SUCCESS);
					String resmsg = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.SUCCESS, null);
					response.setMessage(resmsg);
					response.setServiceList(serviceBalLst);
//					response.setList(balList);
				}
				else {
					if ("ALL".equalsIgnoreCase(serviceName)) {
						for (int i = 0; i < servicebal.length; i++) {
							ServiceViceBalanceVO serviceViceBalanceVO = new ServiceViceBalanceVO();
							serviceViceBalanceVO.setServiceName(BTSLUtil.getOptionDesc(servicebal[i].split(":")[0], serviceTypeList).getLabel());
							serviceViceBalanceVO.setServiceCode(servicebal[i].split(":")[0]);
							serviceViceBalanceVO.setBalanceAssociated(servicebal[i].split(":")[1]);
							serviceBalLst.add(serviceViceBalanceVO);
						}
						response.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
						response.setMessageCode(PretupsErrorCodesI.SUCCESS);
						String resmsg = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.SUCCESS, null);
						response.setMessage(resmsg);
						response.setServiceList(serviceBalLst);
						response.setList(balList);
					} else {
						boolean flag = false;
						for (int i = 0; i < servicebal.length; i++) {
							if (serviceName.equalsIgnoreCase(servicebal[i].split(":")[0]) && !BTSLUtil.isNullString(BTSLUtil.getOptionDesc(servicebal[i].split(":")[0], serviceTypeList).getLabel())) {
								ServiceViceBalanceVO serviceViceBalanceVO = new ServiceViceBalanceVO();
								serviceViceBalanceVO.setServiceName(BTSLUtil.getOptionDesc(servicebal[i].split(":")[0], serviceTypeList).getLabel());
								serviceViceBalanceVO.setServiceCode(servicebal[i].split(":")[0]);
								serviceViceBalanceVO.setBalanceAssociated(servicebal[i].split(":")[1]);
								serviceBalLst.add(serviceViceBalanceVO);
								flag = true;
								break;
							}
						}
						if (!flag) {
							String[] arg = {serviceName};
							throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SERVICE_NOT_FOUND, arg);
						} else {
							response.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
							response.setMessageCode(PretupsErrorCodesI.SUCCESS);
							String resmsg = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.SUCCESS, null);
							response.setMessage(resmsg);
							response.setServiceList(serviceBalLst);
							response.setList(balList);
						}
					}

				}
	        	
	        }catch (BTSLBaseException be) {
		        log.error("processFile", "Exceptin:e=" + be);
		        log.errorTrace(METHOD_NAME, be);
	       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),be.getArgs());
		        response.setMessageCode(be.getMessageKey());
		        response.setMessage(msg);
	        	 if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	        		 responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    	         response.setStatus("401");
	            }
	           else{
	        	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	           		response.setStatus("400");
	           }
	        }catch (Exception e) {
	            log.debug("processFile", e);
	            response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
	            String resmsg = RestAPIStringParser.getMessage(
	    				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
	    				null);
	            response.setMessage(resmsg);
	            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	        	  response.setStatus("400");
	        	
	    	}finally {
				if (mcomCon != null) {
					mcomCon.close("GetUserServiceBalanceServiceImpl#processFile");
					mcomCon = null;
				}
	                log.debug("processFile", "Exit");
	        }
			return response;
	}


	@Override
	public UserWidgetResponse processUserWiget(UserWidgetRequestVO requestVO, MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, HttpServletRequest httpServletRequest) {

		
		
		 final String METHOD_NAME = "processUserWiget";
	        if (log.isDebugEnabled()) {
	            log.debug(METHOD_NAME, "Entered");
	        }
			Connection con = null;
			MComConnectionI mcomCon = null;
	    	Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
	    	UserWidgetResponse response = new UserWidgetResponse();
	    	ChannelUserWebDAO channelUserWebDAO = null;
			OAuthUserData oAuthdata = null;
			OAuthUser oAuthreqVo = null;
			ChannelUserDAO channelUserDAO = null;
			ChannelUserVO  channelUserVO = null;
	        try{
	        	mcomCon = new MComConnection();
	        	con=mcomCon.getConnection();
				oAuthdata = new OAuthUserData();
				oAuthreqVo = new OAuthUser();
				oAuthreqVo.setData(oAuthdata);
				baseResponseMultiple = new BaseResponseMultiple();
				// validate token
				OAuthenticationUtil.validateTokenApi(oAuthreqVo, headers, baseResponseMultiple);
	        	channelUserWebDAO = new ChannelUserWebDAO();
	        	channelUserDAO = new ChannelUserDAO();
	        	channelUserVO = channelUserDAO.loadChannelUserDetails(con, oAuthreqVo.getData().getMsisdn());
	        	if(channelUserVO == null ) {
	        		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHANNEL_USR_NOT_FOUND, "");
	        	}
	        	List<String> widgetArray = requestVO.getWigetList();
	        	String widgetListAsString = "";
	        	if(!(widgetArray.size() == 0)) {
	        		widgetListAsString = widgetListAsString +widgetArray.get(0);
	        		for(int i =1;i<widgetArray.size();i++ ) {
	        			widgetListAsString = widgetListAsString +","+widgetArray.get(i);
	        		}
	        	}
	        	int updateCount = channelUserDAO.updateUserWigets(con,channelUserVO.getUserID(),widgetListAsString);
	         	if(updateCount >0)
	         	{
	         		mcomCon.finalCommit();
	         		response.setStatus(200);
	         		response.setMessage("Widget added or updated successfully");
	         		response.setMessageCode("200");
	         	}else {
	         		mcomCon.partialRollback();
	         		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHANNEL_USR_NOT_FOUND, "");
	         	}
	        	
	        }catch (BTSLBaseException be) {
		        log.error("processFile", "Exceptin:e=" + be);
		        log.errorTrace(METHOD_NAME, be);
	       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),be.getArgs());
		        response.setMessageCode(be.getMessageKey());
		        response.setMessage(msg);
	        	 if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	        		 responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    	         response.setStatus(401);
	            }
	           else{
	        	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	           		response.setStatus(400);
	           }
	        }catch (Exception e) {
	            log.debug("processFile", e);
	            response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
	            String resmsg = RestAPIStringParser.getMessage(
	    				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
	    				null);
	            response.setMessage(resmsg);
	            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	        	  response.setStatus(400);
	        	
	    	}finally {
				if (mcomCon != null) {
					mcomCon.close("C2SServiceImpl#processUserWiget");
					mcomCon = null;
				}
	                log.debug("processFile", "Exit");
	        }
			return response;

	}



}
