package com.restapi.channelAdmin.serviceI;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.BaseResponseReversal;
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.OracleUtil;
import com.btsl.util.XMLTagValueValidation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapi.c2s.services.C2SRechargeReversalDetails;
import com.restapi.c2s.services.C2SRechargeReversalRequestVO;
import com.restapi.channelAdmin.requestVO.CAC2STransferReversalConfirmVO;
import com.restapi.channelAdmin.requestVO.CAC2STransferReversalListRequestVO;
import com.restapi.channelAdmin.responseVO.CAC2STransferReversalResponseVO;
import com.restapi.channelAdmin.service.CAC2STransferReversalService;
import com.web.pretups.channel.transfer.businesslogic.C2STransferWebDAO;

@Service
public class CAC2STransferReversalServiceI implements CAC2STransferReversalService{
	protected static final Log LOG = LogFactory.getLog(CAC2STransferReversalServiceI.class.getName());

	@Override
	public CAC2STransferReversalResponseVO getTransferReversalList(Connection con, String loginID,
			CAC2STransferReversalListRequestVO requestVO) throws SQLException {
		final String methodName = "getTransferReversalList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        CAC2STransferReversalResponseVO  response= new CAC2STransferReversalResponseVO();
        ChannelUserVO channelUserVO = new ChannelUserVO();
	    UserDAO userDAO = new UserDAO();
	    C2STransferWebDAO c2STransferwebDAO = new C2STransferWebDAO();
	    ChannelTransferVO channeltransferVO = new ChannelTransferVO();
	    Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

        try {
        	channeltransferVO.setToUserMsisdn(requestVO.getUserMsisdn());
        	channeltransferVO.setTransferID(requestVO.getTransactionID());
        	
        	channelUserVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			  final List assignedserviceList = channelUserVO.getAssociatedServiceTypeList();
	            if(!(PretupsI.BCU_USER.equalsIgnoreCase(channelUserVO.getCategoryCode()) || PretupsI.CUSTOMER_CARE.equalsIgnoreCase(channelUserVO.getCategoryCode()) || TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(channelUserVO.getCategoryCode()) || TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(channelUserVO.getCategoryCode())))
	            {
	            	if (assignedserviceList == null || assignedserviceList.isEmpty()) {
	            		throw new BTSLBaseException(this, methodName, "c2stranfer.c2srecharge.error.noserviceassign");
	            	}
	            }
	            List<ChannelTransferVO> al = new ArrayList<ChannelTransferVO>();
	            al = c2STransferwebDAO.getReversalTransactions(con, channeltransferVO, requestVO.getSenderMsisdn(), channelUserVO.getCategoryCode());
	            response.setTransferList(al);
	            response.setStatus(200);
	            response.setMessageCode(PretupsErrorCodesI.SUCCESS);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
	            response.setMessage(msg);
		} catch (BTSLBaseException be) {
			LOG.error(methodName, "Exceptin:e=" + be);
			LOG.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			return response;
        } 
        
        finally {
        	
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exited");
			}
		}
        
		return response;
	}
	
	@Override
	public BaseResponseMultiple confirmTransferReversal(Connection con, CAC2STransferReversalConfirmVO requestVO,MultiValueMap<String, String> headers, String loginID,String requestIDStr, HttpServletResponse responseSwag) {
		final String methodName = "confirmTransferReversal";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
	   
        BaseResponseMultiple response = new BaseResponseMultiple<>();
        
       
        String txn_id = requestVO.getTransactionID();
        UserDAO userDAO = new UserDAO();
        ChannelUserVO senderVO = new ChannelUserVO();
      try {
    	  
    	 senderVO = (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, loginID);

    	 	C2SRechargeReversalRequestVO requestVOReverse = new C2SRechargeReversalRequestVO();
			C2SRechargeReversalDetails c2sRechargeReversalDetails  =new C2SRechargeReversalDetails();
			c2sRechargeReversalDetails.setTxnid(txn_id);
			c2sRechargeReversalDetails.setExtNwCode(senderVO.getExternalCode());
			
			List<C2SRechargeReversalDetails> reverseList = new ArrayList();
			
			
			reverseList.add(c2sRechargeReversalDetails);
			
			requestVOReverse.setDataRev(reverseList);
			
			response = processRequestChannel(requestVOReverse, "rcrev".toUpperCase(), requestIDStr, headers, responseSwag);
			
      } catch (Exception e) {
          if (LOG.isDebugEnabled()) {
              LOG.error(methodName, "Exceptin:e=" + e);
          }
          LOG.errorTrace(methodName, e);
      }finally {
        
			
          
          if (LOG.isDebugEnabled()) {
              LOG.debug(methodName, "Exiting :");
          }
      }
		return response;
	}
	
	private InstanceLoadVO getInstanceLoadVOObject() throws RuntimeException {
		InstanceLoadVO instanceLoadVO, instanceLoadVORest;
		instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(
				Constants.getProperty("INSTANCE_ID") + "_" + PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (instanceLoadVO != null) {
			instanceLoadVORest = LoadControllerCache.getInstanceLoadForNetworkHash(
					instanceLoadVO.getRstInstanceID() + "_" + PretupsI.REQUEST_SOURCE_TYPE_REST);
		} else {
			throw new RuntimeException(
					PretupsRestUtil.getMessageString("no.mapping.found.for.web.sms.or.rest.configuration"));
		}

		if (instanceLoadVORest == null) {
			instanceLoadVORest = LoadControllerCache.getInstanceLoadForNetworkHash(
					instanceLoadVO.getRstInstanceID() + "_" + PretupsI.REQUEST_SOURCE_TYPE_SMS);
		}

		if (instanceLoadVORest == null) {
			instanceLoadVORest = instanceLoadVO;
		}

		return instanceLoadVORest;
	}

	 private BaseResponseMultiple processRequestChannel(C2SRechargeReversalRequestVO requestVO1, String serviceKeyword, String requestIdChannel ,MultiValueMap<String, String> headers,HttpServletResponse response1)  {
	        BaseResponseMultiple response = new BaseResponseMultiple<>();
	        final String methodName = "processRequestChannel";
	        final RequestVO requestVO = new RequestVO();
	        Connection con = null;MComConnectionI mcomCon = null;
	        ChannelUserVO channelUserVO = null;
	        final Date currentDate = new Date();
	        final long requestStartTime = System.currentTimeMillis();
	        C2STransferVO c2sTransferVO = null;
	        RowErrorMsgLists rowErrorMsgLists = null;
	        BaseResponseReversal baseResponse = null;
	        MasterErrorList masterErrorList = null;
	        ArrayList<MasterErrorList> masterErrorLists = null;
	        ArrayList<RowErrorMsgLists> rowErrorMsgListsFinal = new ArrayList<>();
			ArrayList<BaseResponseReversal> baseResponseFinalSucess = new ArrayList<>();
			ErrorMap errorMap = new ErrorMap();
	        try {
	            String instanceCode = Constants.getProperty("INSTANCE_ID");
	            //requestVO.setReqContentType(httpServletRequest.getContentType());
	            requestVO.setModule(PretupsI.C2S_MODULE);
	            requestVO.setInstanceID(instanceCode);
	            requestVO.setCreatedOn(currentDate);
	            requestVO.setLocale(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
	            requestVO.setSenderLocale(requestVO.getLocale());
	            requestVO.setDecreaseLoadCounters(false);
	            requestVO.setRequestStartTime(requestStartTime);
	            requestVO.setServiceKeyword(serviceKeyword);
	            requestVO.setActionValue(PretupsI.CHANNEL_RECEIVER_ACTION);
	            mcomCon = new MComConnection();con=mcomCon.getConnection();
	            String pin = requestVO1.getPin1();
	            StringBuilder requestStr=null;
	            UserDAO userDAO = new UserDAO();
	        	UserVO userVO = new UserVO();
				
				
	            String chnlMessageSep = ((String)PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR));
	            if (BTSLUtil.isNullString(chnlMessageSep)) {
	                chnlMessageSep = " ";
	            }
	            /*
				 * Authentication
				 * @throws BTSLBaseException
				 */
	            requestVO1.setData(new OAuthUserData());
	            C2STransferDAO c2sTransferDAO = new C2STransferDAO();
			    //requestVO.setActiverUserId(senderVO.getUserID());
	 	        OAuthenticationUtil.validateTokenApi(requestVO1, headers,response1);
	 	        String msisdn = requestVO1.getData().getMsisdn();
	 	       String loginID = requestVO1.getData().getLoginid();
	 	      ChannelUserVO senderVO =null;
	 	        
	 	      senderVO=(ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con,loginID);
	 	     if(!PretupsI.CATEGORY_TYPE_OPT.equalsIgnoreCase(senderVO.getDomainID())) {  // IF channel admin, no need to validate pin, as he has not initated transaction
	 	    	senderVO = new ChannelUserDAO().loadChannelUserDetails(con,msisdn); 
	 	     }
	 	        
	 	        
	 	       
			  

			    
			   if(!PretupsI.CATEGORY_TYPE_OPT.equalsIgnoreCase(senderVO.getDomainID())) {  // IF channel admin, no need to validate pin, as he has not initated transaction 
	 	        if(!BTSLUtil.isNullObject(senderVO))
	 	        {
	 	        	if (senderVO.getUserPhoneVO().getPinRequired().equals(PretupsI.YES)) {
					try {
						if(!BTSLUtil.isNullString(pin))
						ChannelUserBL.validatePIN(con, senderVO, pin);
						else{
							throw new BTSLBaseException(PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
						}
					} catch (BTSLBaseException be) {
						if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
								|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
							OracleUtil.commit(con);
						}
						response.setMessageCode(be.getMessageKey());
	 		             response.setMessage(RestAPIStringParser.getMessage(requestVO.getLocale(),be.getMessageKey(), be.getArgs()));
	 		             response.setStatus("206");
	 		             response.setService("RCREVRESP");
	 		             response1.setStatus(HttpStatus.SC_BAD_REQUEST);	 					
	 					return response;
					}
				}
	 	        }
			   }else {
				   senderVO=null; // if logged in user channel admin make it Null, so that From transaction we can pick the sender msisdn Info.
			   }
	 	        boolean error = false;
	 	        for(int i=0;i<requestVO1.getDataRev().size();i++)
	 	        	{
	 	        	requestVO.setSuccessTxn(true);
	 	        	rowErrorMsgLists = new RowErrorMsgLists();
					baseResponse = new BaseResponseReversal();
					masterErrorList = new MasterErrorList();
					 masterErrorLists = new ArrayList<>();
	 	        	String txnid=requestVO1.getDataRev().get(i).getTxnid();
	 	        	try
			    {
			    	XMLTagValueValidation.validateTxnId(txnid, true);
			    	c2sTransferVO = c2sTransferDAO.loadC2STransferDetails(con, txnid);
			    	if (BTSLUtil.isNullObject(c2sTransferVO)) {
				    requestVO.setMessageCode(PretupsErrorCodesI.C2S_REVERSAL_INVALID_TXNID);
				    throw new BTSLBaseException("C2SReverseController", methodName,
						PretupsErrorCodesI.C2S_REVERSAL_INVALID_TXNID);
			    }
			    }
			    catch(BTSLBaseException be)
			    {
		             requestVO.setMessageCode(be.getMessageKey());
		             requestVO.setMessageArguments(be.getArgs());
		             response.setStatus("206");
		             response.setService("RCREVRESP");
		             error = true;
		             requestVO.setSuccessTxn(false);
			    }
	 	        	try{
	 	        if(!error)
	 	        {
	 	        	if(BTSLUtil.isNullObject(senderVO))
	 	        	{
	 	        	userVO=userDAO.loadUsersDetails(con, msisdn);
	 	        	senderVO = new ChannelUserDAO().loadChannelUserDetails(con,c2sTransferVO.getSenderMsisdn());
	 	        	requestVO1.getData().setLoginid(senderVO.getLoginID());
	 	        	requestVO1.getData().setMsisdn(senderVO.getMsisdn());
	 	        	requestVO1.getData().setExtcode(senderVO.getExternalCode());
	 	        	requestVO1.getData().setPin(BTSLUtil.decryptText(senderVO.getUserPhoneVO().getSmsPin()));
	 	        	requestVO1.getData().setPassword(BTSLUtil.decryptText(senderVO.getPassword()));
	 	        	}
	 	        requestVO.setServiceType("RCREV");
				requestVO.setRequestGatewayType(requestVO1.getReqGatewayType());
				requestVO.setLocale(new Locale(senderVO.getUserPhoneVO().getPhoneLanguage(),senderVO.getUserPhoneVO().getCountry()));
				
	            ObjectMapper mapper = new ObjectMapper();
	            JsonNode request = mapper.valueToTree(requestVO1);;
	            requestVO.setRequestMessageOrigStr(request.toString());
	            requestVO.setLogin(requestVO1.getReqGatewayLoginId());
	            requestVO.setFilteredMSISDN(senderVO.getMsisdn());
	            requestVO.setRequestNetworkCode(requestVO1.getDataRev().get(i).getExtNwCode());
	            parseRequestfromJson(request, requestVO);
	            String requestHandlerClass;
	            PretupsBL.validateRequestMessageGateway(requestVO);
	            channelUserVO = senderVO;
	            
				 requestStr=new StringBuilder();
	            final LocaleMasterDAO localeMasterDAO = new LocaleMasterDAO();
             final String receiverLanguage = localeMasterDAO.loadLocaleMasterCode(con, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			if ((PretupsI.BCU_USER.equalsIgnoreCase(userVO.getCategoryCode())
					|| PretupsI.CUSTOMER_CARE.equalsIgnoreCase(userVO.getCategoryCode())
					|| TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(userVO.getCategoryCode())
					|| TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(userVO.getCategoryCode()))) {
				final String senderlanguageCode = localeMasterDAO.loadLocaleMasterCode(con, userVO.getLanguage(),
						userVO.getCountry());
				requestStr.append("RCREV" + chnlMessageSep
						+ c2sTransferVO.getReceiverMsisdn() + chnlMessageSep + c2sTransferVO.getTransferID()
						+ chnlMessageSep + senderlanguageCode + chnlMessageSep + receiverLanguage + chnlMessageSep
						+ userVO.getCategoryCode() + chnlMessageSep + userVO.getUserID());
			} else {
				final String senderlanguageCode = localeMasterDAO.loadLocaleMasterCode(con, channelUserVO.getUserPhoneVO().getPhoneLanguage(),
						channelUserVO.getUserPhoneVO().getCountry());
				requestStr.append("RCREV" + chnlMessageSep
						+ c2sTransferVO.getReceiverMsisdn() + chnlMessageSep + c2sTransferVO.getTransferID()
						+ chnlMessageSep + senderlanguageCode + chnlMessageSep + receiverLanguage + chnlMessageSep
						+ requestVO1.getData().getPin());
			}
	            requestVO.setDecryptedMessage(requestStr.toString());
	            requestVO.setRequestMessageArray(new String[]{"RCREV"});
	            requestVO.setServicePort(requestVO1.getServicePort());
	            requestVO.setSenderVO(senderVO);
	           
	            final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);

	            requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
	            requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
	            requestVO.setType(serviceKeywordCacheVO.getType());
	            requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
	            requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());

	            channelUserVO.setServiceTypes(requestVO.getServiceType());
	            requestVO.setGroupType(serviceKeywordCacheVO.getGroupType());

	            final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL
	                    .getServiceKeywordHandlerObj(requestHandlerClass);
	            
	            controllerObj.process(requestVO);
	 	        }	      
	 	        	}
	 	        	catch (Exception e) {
						requestVO.setSuccessTxn(false);
					}
	            if(!(requestVO.isSuccessTxn()))
				{
					response.setStatus("400");
					response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
					response.setService("RCREVRESP");
					int rowValue=i+1;
					rowErrorMsgLists.setRowName("DATA" + rowValue);
					rowErrorMsgLists.setRowValue(String.valueOf(rowValue));
					masterErrorList.setErrorCode(String.valueOf(requestVO.getMessageCode()));
					masterErrorList.setErrorMsg(RestAPIStringParser.getMessage(
							new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
							String.valueOf(requestVO.getMessageCode()), requestVO.getMessageArguments()));
					masterErrorLists.add(masterErrorList);
					rowErrorMsgLists.setMasterErrorList(masterErrorLists);
					rowErrorMsgListsFinal.add(rowErrorMsgLists);
					mcomCon.finalRollback();
				}
	            if((requestVO.isSuccessTxn()))
				{
	            	//ArrayList list1 = (ArrayList<Object>) requestVO.getValueObject();
	            	LOG.debug(methodName, requestVO.getValueObject());
	            	C2STransferVO c2sTransferVO1 = (C2STransferVO) requestVO.getValueObject();
	            	baseResponse.setTxnid(requestVO.getTransactionID());
	            	baseResponse.setReceiverTrfValue(c2sTransferVO1.getReceiverTransferValueAsString());
	            	baseResponse.setReceiveraccessValue(c2sTransferVO1.getReceiverAccessFeeAsString());
	            	baseResponse.setMessage(RestAPIStringParser.getMessage(
							new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), requestVO.getMessageCode(),
							requestVO.getMessageArguments()));
	            	baseResponse.setMessageCode(requestVO.getMessageCode());
	            	baseResponse.setStatus(200);
								baseResponseFinalSucess.add(baseResponse);
								mcomCon.finalCommit();
				}
	 	        	}
	 	       if(!BTSLUtil.isNullOrEmptyList(rowErrorMsgListsFinal))
				{
					response.setStatus("400");
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setService("RCREVRESP");
					response.setMessage("Some records contains error");
					errorMap.setRowErrorMsgLists(rowErrorMsgListsFinal);
					response.setSuccessList(baseResponseFinalSucess);
					response.setErrorMap(errorMap);
					return response;
				}
	            	response.setSuccessList(baseResponseFinalSucess);
	            	response.setMessageCode(requestVO.getMessageCode());
	            	response.setMessage("All reversals processed successfully");
	            	response.setStatus("200");
	            	response.setService("RCREVRESP");
			        return response;


	        } catch (BTSLBaseException be) {
	    		
	    		LOG.error(methodName, "BTSLBaseException " + be.getMessage());
	            LOG.errorTrace(methodName, be);
	            	String resmsg = RestAPIStringParser.getMessage(
	    					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
	    					null);
	            	response.setMessageCode(be.getMessageKey());
	            	response.setMessage(resmsg);
	            	response.setService("RCREVRESP");
	            	 if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	                  	 response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	                  	response.setStatus("401");
	                  }
	                   else{
	                   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	                   response.setStatus("400");
	                   }
	                return response;
	            
	        }catch (Exception e) {
				LOG.error(methodName, "Exception " + e);
				LOG.errorTrace(methodName, e);
				requestVO.setSuccessTxn(false);
				try {
					if (mcomCon != null) {
						mcomCon.finalRollback();
					}
				} 
				
				catch (SQLException esql) {
					LOG.error(methodName,"SQLException : ", esql.getMessage());
				}
				LOG.error("process", "BTSLBaseException " + e.getMessage());
				LOG.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
						"C2CTransferController[process]", "", "", "", "Exception:" + e.getMessage());
				response.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
				response.setMessage(RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
						null));
				response.setService("RCREVRESP");
				response.setStatus("400");
				  response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				return response;
			} finally {
				if (mcomCon != null) {
					mcomCon.close("#process");
					mcomCon = null;
				}
		        LogFactory.printLog(methodName, " Exited ", LOG);
		    }
	 }


	 
	 public PretupsResponse<JsonNode> parseRequestForMessage(JsonNode request, RequestVO prequestVO)
          throws BTSLBaseException {
      final String methodName = "parseRequestForMessage";
      PretupsResponse<JsonNode> response = new PretupsResponse<>();
      String msisdn;
      String requestMessage;
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
      
      
      LogFactory.printLog(methodName, "requestMessage " + requestMessage, LOG);
      prequestVO.setRequestMessage(requestMessage);
      response.setStatus(true);
      return response;

  }
	 
	 private String getNodeValue(JsonNode node, String value) {
      if (node.get(value) != null) {
          return node.get(value).textValue();
      } else {
          return "";
      }
  }
	 public PretupsResponse<JsonNode> parseRequestfromJson(JsonNode request, RequestVO requestVO)
          throws BTSLBaseException {
      PretupsResponse<JsonNode> response = new PretupsResponse<>();
      final String methodName = "parseRequestfromJson";
      if (BTSLUtil.isNullString(requestVO.getReqContentType())) {
          requestVO.setReqContentType("CONTENT_TYPE");
      }
      String reqGatewayCode = getNodeValue(request, "reqGatewayCode");
      String reqGatewayType = getNodeValue(request, "reqGatewayType");
      LogFactory.printLog(methodName, " reqGatewayCode " + reqGatewayCode + "reqGatewayType " + reqGatewayType, LOG);

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
      requestVO.setSourceType(getNodeValue(request, "sourceType"));

      response.setStatus(true);
      return response;

  }
	 
	 
}