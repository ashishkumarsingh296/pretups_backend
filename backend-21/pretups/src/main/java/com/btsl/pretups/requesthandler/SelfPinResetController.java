package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserPhonesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;
import com.web.user.businesslogic.UserWebDAO;

public class SelfPinResetController implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(SelfPinResetController.class.getName());
    private String _requestID = null;
    private static OperatorUtilI _operatorUtil = null;
    
    
	private String _service_type = null;
	private String _childMsisdn = null;
	private String _parentId = null;
	private String _networkCode = null;
	private String _parentMsisdn = null;
	private String _parentPin = null;
	private String _parentLoginId = null;
	private String _parentPassword = null;
	private String _extRefNum = null;
	private String _dateTime = null;
	private HashMap _requestMap = null;
	
	ArrayList<UserEventRemarksVO> pinPswdRemarksList = null;
	UserWebDAO userwebDAO = null;
	private ChannelUserVO _parentVO = null;
	private ChannelUserVO _childVO = null;

    static {
        String utilClassName = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClassName).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ResetPinController[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }
	
	@Override
	public void process(RequestVO p_requestVO) {
    
		final String METHOD_NAME = "process";
        Connection con = null;MComConnectionI mcomCon = null;
        _requestID = p_requestVO.getRequestIDStr();
        String answer = null;
        String msisdn = null;
        String parentMsisdn = null;
        String balance = null;
        String productCode = null;
        boolean balanceMatch =false;
        ArrayList<UserEventRemarksVO> pinPswdRemarksList = null;
        UserWebDAO userwebDAO = null;
        String newPin=null;
        String[] arr = null;
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, _requestID, " Entered " + p_requestVO);
        }
        
        //for child rest pin (BL)
        _requestMap = p_requestVO.getRequestMap();
        _parentMsisdn = p_requestVO.getFilteredMSISDN();
        _parentLoginId = p_requestVO.getSenderLoginID();
        _parentPassword = p_requestVO.getPassword();
        _childMsisdn = (String)_requestMap.get("MSISDN2");
        _service_type = p_requestVO.getServiceType();
        _parentId = p_requestVO.getActiverUserId();
        _networkCode = p_requestVO.getRequestNetworkCode();
        _parentPin = (String) _requestMap.get("PIN");
        Date expiryTime = null;
        
        try {
            String[] p_requestArr = p_requestVO.getRequestMessageArray();
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            UserPhonesDAO userPhonesDAO = new UserPhonesDAO();
            ChannelUserVO channelUserVO = new ChannelUserVO();
            ArrayList<Object> list = new ArrayList<Object>();
            msisdn = p_requestArr[1];
            // msisdn = p_requestVO.getRequestMSISDN();
            
            int messageLength = p_requestArr.length;
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Message Length=" + messageLength);
            }

            

           
            
            
            	msisdn = p_requestArr[1];
            	balance = p_requestArr[2];
            	parentMsisdn = p_requestArr[3];
            	productCode = p_requestArr[4];
            	
            	
            	if(PretupsI.SERVICE_TYPE_CHILDPINRESET.equalsIgnoreCase(_service_type))
            	{
	                //Random pin generation
	            	if(PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_RANDOM_PIN_GENERATE)!=null && (boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_RANDOM_PIN_GENERATE))
	                {
	                    newPin = _operatorUtil.generateRandomPin();
	                } else {
	                    newPin = SystemPreferences.C2S_DEFAULT_SMSPIN;
	                }
	            	
	            	
	            	
	            	//validate pin if sender authentication is done by msisdn and pin
	            	if( BTSLUtil.isNullString(_parentLoginId) || BTSLUtil.isNullString(_parentPassword)) {
	            		
	            		if(PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(p_requestVO.getRequestGatewayType()) && PretupsI.NO.equalsIgnoreCase(Constants.getProperty("MAPPGW_PIN_BYPASS"))) {
	    	        			
	    	                try {
	    	                    ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), _parentPin);
	    	                } catch (BTSLBaseException be) {
	    	                    _log.errorTrace(METHOD_NAME, be);
	    	                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
	    	                                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
	    	                        con.commit();
	    	                    }
	    	                    _log.error(METHOD_NAME, "BTSLBaseException " + be);
	    	                    throw be;
	    	                }
	    	        	}
	            	
	            	}
	                	
	            	// check if user is child of sender or not
	    			
	            	_parentVO = (ChannelUserVO)p_requestVO.getSenderVO();
	            	ChannelUserTxnDAO channelUserTxnDAO = null;
	            	channelUserTxnDAO = new ChannelUserTxnDAO();
	            	_childVO = channelUserTxnDAO.loadOtherUserBalanceVO(con, _childMsisdn, _parentVO);
	            	
	            	
	    			if(_childVO.getUserName() == null) {
	    				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.USER_NOT_CHILD);
	    			}
	    			
	    			//reset pin
	    			channelUserVO = (ChannelUserVO) channelUserDAO.loadChannelUserDetails(con, _childMsisdn);
	                Date currentDate = null;
	                currentDate = BTSLUtil.getTimestampFromUtilDate(new java.util.Date());
	                if(_log.isDebugEnabled()) {
	                	_log.debug(METHOD_NAME, "Current date time= " + currentDate);
	                }
	                
	                int expiry_interval = SystemPreferences.TEMP_PIN_EXPIRY_DURATION;
	                if(_log.isDebugEnabled()) {
	                	_log.debug(METHOD_NAME, "expiry time duration in hours= " + expiry_interval);
	                }
	                long t = currentDate.getTime();
	                
	                final long ONE_MINUTE_IN_MILLIS = 60000;
	                final long ONE_HOUR_IN_MINUTES = 60;
	                expiryTime = new Date(t + (expiry_interval*ONE_HOUR_IN_MINUTES*ONE_MINUTE_IN_MILLIS));
	                //expiryTime = BTSLUtil.getTimestampFromUtilDate(expiryTime);
	                if(_log.isDebugEnabled()) {
	                	_log.debug(METHOD_NAME, "Pin Expiry date time= " + expiryTime);
	                }
	                channelUserVO.setModifiedBy(channelUserVO.getUserID());
	                channelUserVO.setModifiedOn(currentDate);
	                int count = channelUserDAO.resetPin(con, newPin, channelUserVO);
	                
	    			if(count>0) {
	    				
	                    pinPswdRemarksList = new ArrayList<UserEventRemarksVO>();
	                    userwebDAO = new UserWebDAO();
	                    UserEventRemarksVO  remarksVO = new UserEventRemarksVO();
	                    remarksVO.setCreatedBy(p_requestVO.getActiverUserId());
	                    remarksVO.setCreatedOn(new Date());
	                    remarksVO.setEventType(PretupsI.PIN_RESET);
	                    remarksVO.setMsisdn(_childMsisdn);
	                    remarksVO.setRemarks(p_requestVO.getRemarks());
	                    remarksVO.setUserID(channelUserVO.getUserID());
	                    remarksVO.setUserType(channelUserVO.getUserType());
	                    remarksVO.setModule(PretupsI.C2S_MODULE);
	                    pinPswdRemarksList.add(remarksVO);
	                   int  insertCount = userwebDAO.insertEventRemark(con, pinPswdRemarksList);
	         
	                   if(insertCount <= 0) {
	                       
	                	   con.rollback();
	                       throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CANNOT_BE_PROCESSED);
	                   }
	    				
	    				
	    				
	    				
	                    con.commit();
	                    p_requestVO.setSuccessTxn(true);
	                    p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
	                    // push message on successful pin reset
	                    BTSLMessages btslMessage = null;
	                    PushMessage pushMessage = null;
	
	                    String recAlternetGatewaySMS = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
	                    String reqruestGW = p_requestVO.getRequestGatewayCode();
	                    if (!BTSLUtil.isNullString(recAlternetGatewaySMS) && (recAlternetGatewaySMS.split(":")).length >= 2) {
	                        if (reqruestGW.equalsIgnoreCase(recAlternetGatewaySMS.split(":")[0])) {
	                            reqruestGW = (recAlternetGatewaySMS.split(":")[1]).trim();
	                            if (_log.isDebugEnabled()) {
	                                _log.debug(METHOD_NAME, reqruestGW, "Requested GW was:" + p_requestVO.getRequestGatewayCode());
	                            }
	                        }
	                    }
	                    UserPhoneVO phoneVO = channelUserDAO.loadUserPhoneDetails(con, channelUserVO.getUserID());
	                    Locale locale = null;
	                    locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
	                    p_requestVO.setLocale(locale);
	
	                    arr = new String[2];
	                    arr[0] = newPin;
	                    String expiryDateArg = BTSLUtil.getDateTimeStringFromDate(expiryTime);
	                    
	                    if(_log.isDebugEnabled()) {
	                    	_log.debug(METHOD_NAME, "Pin Expiry date string arg= " + expiryDateArg);
	                    }
	                    
	                    arr[1] = expiryDateArg;
	                    btslMessage = new BTSLMessages(PretupsErrorCodesI.CHILD_PIN_RESET_SUCCESS, arr);
	                    
	                    pushMessage = new PushMessage(_childMsisdn, btslMessage, null, null, locale, p_requestVO.getNetworkCode()); // success
	                                                                                                                                                   // SMS
	                                                                                                                                                   // push
	                    pushMessage.push();
	                    
	    			}else {
	                    p_requestVO.setMessageCode(PretupsErrorCodesI.CANNOT_BE_PROCESSED);
	                    p_requestVO.setSuccessTxn(false);
	                    throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.CANNOT_BE_PROCESSED);
	                }
    			
                    
            	}  // child pin reset end here  
                    
                    
            	else {
            		 channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
                     if(!  PretupsI.ROOT_PARENT_ID.equals(channelUserVO.getParentID())){
                     	String correctParentMsisdn = channelUserDAO.loadParentMsisdn(con, channelUserVO.getParentID());
                     	if(!correctParentMsisdn.equals(parentMsisdn)) {
                     		p_requestVO.setSuccessTxn(false);
                     		throw new BTSLBaseException("ResetPinController", METHOD_NAME, PretupsErrorCodesI.INVALID_PARENT_MSISDN);
                     	}
                     	
                     }
                     ArrayList  balancesList = channelUserDAO.loadUserBalances(con, channelUserVO.getNetworkID(), channelUserVO.getNetworkID(), channelUserVO.getUserID());
                    
                     
                     for(int i =0; i< balancesList.size() ;i++) {
                     	UserBalancesVO  balancesVO = (UserBalancesVO) balancesList.get(i);
                     	
                     	 if (_log.isDebugEnabled()) {
                              _log.debug(METHOD_NAME, "balancesVO.getBalanceStr():" + balancesVO.getBalanceStr()+"balancesVO.getProductCode()::"+balancesVO.getProductCode());
                          }
                     	 
                     	 if(balancesVO.getBalanceStr().contains(".")) {
                     		 if(productCode.equals(balancesVO.getProductCode()) && balance.equals(balancesVO.getBalanceStr().substring(0,balancesVO.getBalanceStr().indexOf('.')))) {
                     			 balanceMatch=true;
                     			 break;
                     		 }
                     	 }
                     	 else {
                     		 if(productCode.equals(balancesVO.getProductCode()) && balance.equals(balancesVO.getBalanceStr())) {
                     			 balanceMatch=true;
                     			 break;
                     		 }
                     	 }
                     }
                     
                     if(!balanceMatch) {
                     	  p_requestVO.setSuccessTxn(false);
                 	    	throw new BTSLBaseException("ResetPinController", METHOD_NAME, PretupsErrorCodesI.INVALID_BALANCE);
         	          }
                                
                    
                     Date currentDate = null;
                     currentDate = BTSLUtil.getTimestampFromUtilDate(new java.util.Date());

                     channelUserVO.setModifiedBy(channelUserVO.getUserID());
                     channelUserVO.setModifiedOn(currentDate);
                     int count = channelUserDAO.changePin(con, newPin, channelUserVO);
                     
            
                     
                     if (count > 0) {
                     	
                         pinPswdRemarksList = new ArrayList<UserEventRemarksVO>();
                         userwebDAO = new UserWebDAO();
                         UserEventRemarksVO  remarksVO = new UserEventRemarksVO();
                         remarksVO.setCreatedBy(p_requestVO.getActiverUserId());
                         remarksVO.setCreatedOn(new Date());
                          remarksVO.setEventType(PretupsI.PIN_RESET);
                         remarksVO.setMsisdn(p_requestVO.getMsisdn());
                         remarksVO.setRemarks(p_requestVO.getRemarks());
                         remarksVO.setUserID(channelUserVO.getUserID());
                         remarksVO.setUserType(channelUserVO.getUserType());
                         remarksVO.setModule(PretupsI.C2S_MODULE);
                         pinPswdRemarksList.add(remarksVO);
                        int  insertCount = userwebDAO.insertEventRemark(con, pinPswdRemarksList);
              
                        if(insertCount <= 0) {
                            //con.rollback();
                     	   mcomCon.finalRollback();
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CANNOT_BE_PROCESSED);
                        }
                        
                         con.commit();
                         p_requestVO.setSuccessTxn(true);
                         p_requestVO.setMessageCode(PretupsErrorCodesI.PIN_RESET_SUCCESSFUL);

                         // push message on successful pin reset
                         BTSLMessages btslMessage = null;
                         PushMessage pushMessage = null;

                         String recAlternetGatewaySMS = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
                         String reqruestGW = p_requestVO.getRequestGatewayCode();
                         if (!BTSLUtil.isNullString(recAlternetGatewaySMS) && (recAlternetGatewaySMS.split(":")).length >= 2) {
                             if (reqruestGW.equalsIgnoreCase(recAlternetGatewaySMS.split(":")[0])) {
                                 reqruestGW = (recAlternetGatewaySMS.split(":")[1]).trim();
                                 if (_log.isDebugEnabled()) {
                                     _log.debug(METHOD_NAME, reqruestGW, "Requested GW was:" + p_requestVO.getRequestGatewayCode());
                                 }
                             }
                         }
                         UserPhoneVO phoneVO = channelUserDAO.loadUserPhoneDetails(con, channelUserVO.getUserID());
                         Locale locale = null;
                         locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                         p_requestVO.setLocale(locale);

                         arr = new String[1];
                         arr[0] = SystemPreferences.C2S_DEFAULT_SMSPIN;
                         btslMessage = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_RESETPIN_MSG, arr);
                         
                          pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), btslMessage, null, null, locale, p_requestVO.getNetworkCode()); // success
                                                                                                                                                        // SMS
                                                                                                                                                        // push
                         pushMessage.push();

                     } else {
                         p_requestVO.setMessageCode(PretupsErrorCodesI.CANNOT_BE_PROCESSED);
                         p_requestVO.setSuccessTxn(false);
                         throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.CANNOT_BE_PROCESSED);
                     }

            	}
            	    
           
        }catch (BTSLBaseException be) {
        	  p_requestVO.setMessageCode(be.getMessage());
              p_requestVO.setSuccessTxn(false);
              _log.error(METHOD_NAME, "Exception:be=" + be);
              _log.errorTrace(METHOD_NAME, be);

        }
        catch (Exception be) {
            p_requestVO.setSuccessTxn(false);
            _log.error(METHOD_NAME, "Exception:be=" + be);
            _log.errorTrace(METHOD_NAME, be);
        }

        finally {
			if (mcomCon != null) {
				mcomCon.close(METHOD_NAME);
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME,PretupsI.EXITED);
            }
        }
        return;

    
	}

}
