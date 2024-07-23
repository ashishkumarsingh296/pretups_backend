package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.txn.voms.voucher.businesslogic.VomsVoucherTxnDAO;

public class VoucherEnqConHandler implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(VoucherEnqConHandler.class.getName());
    private String _transferID;
    private boolean _recValidationFailMessageRequired = false;
    private boolean _recTopupFailMessageRequired = false;
    private String _notAllowedSendMessGatw;
    private static OperatorUtilI _operatorUtil = null;

    private String _lastTransferId = null;
   // Loads Operator specific class. In MVD controller it is used for
    // validating the message format.
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherEnqConHandler[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /*
     * In the constructor of VoucherEnqConHandler initialize the date variable
     * _currentDate with current date. The
     * variables MVD_REC_GEN_FAIL_MSG_REQD_V & MVD_REC_GEN_FAIL_MSG_REQD_T
     * decides whether the validation and
     * top up failed message send to receiver or not.
     */

    public VoucherEnqConHandler() {
        _log.debug("process", "Entered");
        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("MVD_REC_GEN_FAIL_MSG_REQD_V")))) {
            _recValidationFailMessageRequired = true;
        }
        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("MVD_REC_GEN_FAIL_MSG_REQD_T")))) {
            _recTopupFailMessageRequired = true;
        }
        _notAllowedSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("MVD_SEN_MSG_NOT_REQD_GW"));
        _log.debug(
            "process",
            "Exiting with _recValidationFailMessageRequired=" + _recValidationFailMessageRequired + " _recTopupFailMessageRequired=" + _recTopupFailMessageRequired + " _notAllowedSendMessGatw: " + _notAllowedSendMessGatw);
    }

    /**
     * Method to process the request of the Electornic Voucher Distribution as
     * well as Electornic Voucher Recharge
     * 
     * @param p_requestVO
     *            RequestVO
     * @return void
     */

    public void process(RequestVO p_requestVO) {
        Connection con = null;MComConnectionI mcomCon = null;
        HashMap<String, String> responseMap = new HashMap<String, String>();
        final String methodName = "process";
        final String tablename = null;
        final VomsVoucherTxnDAO vomsVoucherTxnDAO = new VomsVoucherTxnDAO();
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                p_requestVO.getRequestIDStr(),
                "Entered for Request ID=" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN() + " _recValidationFailMessageRequired: " + _recValidationFailMessageRequired + " _recTopupFailMessageRequired" + _recTopupFailMessageRequired + " _notAllowedSendMessGatw: " + _notAllowedSendMessGatw + " ");
        }
        // boolean receiverMessageSendReq=false;
        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();

            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                if (BTSLUtil.isNullString(p_requestVO.getVoucherType())) {
                    responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
                    responseMap.put(VOMSI.TOPUP, "0");
                    responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
                    responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
                    responseMap.put(VOMSI.VALID, PretupsI.NO);
                    responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_TPYE_NULL);
                    responseMap.put(VOMSI.ERROR, "voucher type is null");

                    // changed for voucher query and rollback request
                    responseMap.put(VOMSI.CONSUMED, PretupsI.NOT_APPLICABLE);
                    populateVOfromMap(responseMap, p_requestVO);
                } else {
                    if (!vomsVoucherTxnDAO.validateVoucherType(con, p_requestVO.getVoucherType())) {
                        responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
                        responseMap.put(VOMSI.TOPUP, "0");
                        responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
                        responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
                        responseMap.put(VOMSI.VALID, PretupsI.NO);
                        responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_TPYE_INVALID);
                        responseMap.put(VOMSI.ERROR, "voucher type is INVALID");

                        // changed for voucher query and rollback request
                        responseMap.put(VOMSI.CONSUMED, PretupsI.NOT_APPLICABLE);
                        populateVOfromMap(responseMap, p_requestVO);
                    } else {
                        validateRequestFormat(p_requestVO);

                        // changed for voucher query and rollback request
                        if ((VOMSI.SERVICE_TYPE_VOUCHER_QRY.equals(p_requestVO.getServiceType())) || (VOMSI.SERVICE_TYPE_VOUCHER_ROLLBACK.equals(p_requestVO.getServiceType()))) {
                            responseMap = vomsVoucherTxnDAO.loadDataForVoucherQueryAndRollBackAPI(con, p_requestVO);
                        } else if (VOMSI.SERVICE_TYPE_VOUCHER_RETRIEVAL_ROLLBACK.equals(p_requestVO.getServiceType())) {
                            responseMap = vomsVoucherTxnDAO.loadDataForVoucherRetrievalRollBackAPI(con, p_requestVO);
                        }
						else if (VOMSI.VOUCHER_RESERVATION.equals(p_requestVO.getServiceType())) {
                        	responseMap = vomsVoucherTxnDAO.voucherReservation(con, p_requestVO.getExternalReferenceNum(), p_requestVO.getDecryptedMessage(), p_requestVO.getVoucherType(), p_requestVO.getServiceType());
                        }
											
						 else {
                            responseMap = vomsVoucherTxnDAO.loadData(con, p_requestVO.getDecryptedMessage(), p_requestVO.getVoucherType(), p_requestVO.getServiceType(), p_requestVO.getNetworkCode());
                        }

                        populateVOfromMap(responseMap, p_requestVO);
                    }

                }
            }

            else {
                validateRequestFormat(p_requestVO);
                String masterSerialNo = "";
                masterSerialNo=(String)p_requestVO.getRequestMap().get(VOMSI.MASTER_SERIAL_NO);
                if(BTSLUtil.isNullString(masterSerialNo)){
	                // changed for voucher query and rollback request
	                if ((VOMSI.SERVICE_TYPE_VOUCHER_QRY.equals(p_requestVO.getServiceType())) || (VOMSI.SERVICE_TYPE_VOUCHER_ROLLBACK.equals(p_requestVO.getServiceType()))) {
	                    responseMap = vomsVoucherTxnDAO.loadDataForVoucherQueryAndRollBackAPI(con, p_requestVO);
	                }  
	                else  if ((VOMSI.VOUCHER_VALIDATION.equals(p_requestVO.getServiceType()))||(VOMSI.SERVICE_TYPE_VOUCHER_QRY.equals(p_requestVO.getServiceType()))) {
	                    responseMap = vomsVoucherTxnDAO.loadDataForVoucherQueryAndRollBackAPI(con, p_requestVO);
	                } else if (VOMSI.VOUCHER_RESERVATION.equals(p_requestVO.getServiceType())) {
	                    responseMap = vomsVoucherTxnDAO.voucherReservation(con,p_requestVO.getExternalReferenceNum(), p_requestVO.getDecryptedMessage(), p_requestVO.getVoucherType(), p_requestVO.getServiceType());
	                } else if (VOMSI.VOUCHER_DIRECT_CONSUMPTION.equals(p_requestVO.getServiceType())) {
	                    responseMap = vomsVoucherTxnDAO.voucherDirectConsumption(con, p_requestVO.getDecryptedMessage(), p_requestVO.getVoucherType(), p_requestVO.getServiceType());
	                } else if (VOMSI.VOUCHER_DIRECT_ROLLBACK.equals(p_requestVO.getServiceType())) {
	                    responseMap = vomsVoucherTxnDAO.voucherDirectRollback(con, p_requestVO.getDecryptedMessage(), p_requestVO.getVoucherType(), p_requestVO.getServiceType());
	                }
	                else if (VOMSI.SERVICE_TYPE_VOUCHER_RETRIEVAL_ROLLBACK.equals(p_requestVO.getServiceType())) {
	                    responseMap = vomsVoucherTxnDAO.loadDataForVoucherRetrievalRollBackAPI(con, p_requestVO);
	                } else {
	                    responseMap = vomsVoucherTxnDAO.loadData(con, p_requestVO.getDecryptedMessage(), p_requestVO.getVoucherType(), p_requestVO.getServiceType(), p_requestVO.getNetworkCode());
	                }
	
	                populateVOfromMap(responseMap, p_requestVO);
                } else {
                	
                	if(VOMSI.SERVICE_TYPE_VOUCHER_QRY.equals(p_requestVO.getServiceType())){
                		ArrayList list= vomsVoucherTxnDAO.voucherEnquiryUsingMasterSerialNo(con, p_requestVO);
                		p_requestVO.setEnquiryItemList(list);
                		if(p_requestVO.getEnquiryItemList().size() >= 1){
                			p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                		} else {
                			p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_MASTER_SERIALNO);
                		}
                	}
                }
            }

        } catch (BTSLBaseException be) {
            // added while Voucher Retrieval RollBack Request
            try {
                con.rollback();
            } catch (Exception e) {
            	_log.error(methodName, "Exception:e=" + e);
    			_log.errorTrace(methodName, e);
               
            }
            _log.error("processRequest", "BTSLBaseException" + be);
            responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
            responseMap.put(VOMSI.TOPUP, "0");
            responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
            responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
            responseMap.put(VOMSI.VALID, PretupsI.NO);
            responseMap.put(VOMSI.MESSAGE, be.getMessage());
            responseMap.put(VOMSI.ERROR, " Exception " + be.getMessage());

            // changed for voucher query and rollback request
            responseMap.put(VOMSI.CONSUMED, PretupsI.NOT_APPLICABLE);
            populateVOfromMap(responseMap, p_requestVO);
        } catch (Exception e) {
            // added while Voucher Retrieval RollBack Request
            try {
                con.rollback();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            _log.error("processRequest", "Exception" + e);
            responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
            responseMap.put(VOMSI.TOPUP, "0");
            responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
            responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
            responseMap.put(VOMSI.VALID, PretupsI.NO);
            responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_GEN);
            responseMap.put(VOMSI.ERROR, " Exception " + e.getMessage());

            // changed for voucher query and rollback request
            responseMap.put(VOMSI.CONSUMED, PretupsI.NOT_APPLICABLE);
            populateVOfromMap(responseMap, p_requestVO);
        } finally {
        	if(mcomCon != null){mcomCon.close("VoucherEnqConHandler#process");mcomCon=null;}
        }
        // added by nilesh: consolidated for logger
        // commented for voucher query and rollback request
        /*
         * if(_oneLog) {
         * OneLineTXNLog.log(_c2sTransferVO,_senderTransferItemVO,
         * _receiverTransferItemVO);
         * //making entry in the transaction log
         * }
         */
        TransactionLog.log(_transferID + "-" + _lastTransferId, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
            PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + p_requestVO.getMessageCode());
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting");
        }
    }// end of finally

    private void populateVOfromMap(HashMap p_map, RequestVO p_requestVO) {
        // added for voucher query and rollback request
        final String methodName = "populateVOfromMap";
        VomsVoucherVO voucherVO=new VomsVoucherVO();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_map" + p_map);
        }
        
        if(((String) p_map.get(VOMSI.MESSAGE)).equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_SUCCESS)){
        	p_requestVO.setSuccessTxn(true);
        }else{
        	p_requestVO.setSuccessTxn(false);
        }
        
        p_requestVO.setSerialNo((String) p_map.get(VOMSI.SERIAL_NO));
        try {
            p_requestVO.setVoucherAmount(Long.parseLong(String.valueOf(p_map.get(VOMSI.TOPUP))));
        } catch (Exception e) {
            _log.error(methodName, "Exception while parsing Voucher MRP: " + e.getMessage());
            _log.errorTrace(methodName, e);
        }// changed for voucher query and rollback request
        p_requestVO.setConsumed((String) p_map.get(VOMSI.CONSUMED));
        p_requestVO.setVomsMessage((String) p_map.get(VOMSI.MESSAGE));
        p_requestVO.setMessageCode((String) p_map.get(VOMSI.MESSAGE));
        if(!BTSLUtil.isNullString(p_requestVO.getMessageCode())&&!p_requestVO.getMessageCode().equalsIgnoreCase(PretupsI.TXN_STATUS_SUCCESS)){
        	p_requestVO.setSuccessTxn(false);
        	p_requestVO.setState(VOMSI.VOMS_STATUS_DELETED);
        }else{
        
        	p_requestVO.setState(VOMSI.VOMS_STATUS_ACTIVE);
        }
        p_requestVO.setVomsError((String) p_map.get(VOMSI.ERROR));
        p_requestVO.setVomsRegion((String) p_map.get(VOMSI.REGION));
        p_requestVO.setReceiverMsisdn((String) p_map.get(VOMSI.SUBSCRIBER_ID));
        p_requestVO.setVomsValid((String) p_map.get(VOMSI.VALID));
        p_requestVO.setValidity((String) p_map.get(VOMSI.VOMS_VALIDITY));
        p_requestVO.setTalkTime(String.valueOf( p_map.get(VOMSI.VOMS_TALKTIME)));  
        p_requestVO.setTransactionID((String) p_map.get(VOMSI.VOMS_TXNID));        
        
        voucherVO.setProductID((String) p_map.get(VOMSI.PRODUCT_ID));
       
        p_requestVO.setVoucherStatus((String) p_map.get(VOMSI.VOMS_STATUS));
        p_requestVO.setPin((String) p_map.get(VOMSI.PIN));
        p_requestVO.setExpiryDate((Date) p_map.get(VOMSI.EXPIRY_DATE));
        voucherVO.setProductName((String) p_map.get(VOMSI.PRODUCT_NAME));
        voucherVO.setProductID((String) p_map.get(VOMSI.PRODUCT_ID));
        
        try{
	        	if(p_map.get(VOMSI.FIRST_CONSUMED_ON) != null) {
	        		voucherVO.setConsumedOn(BTSLUtil.getDateFromDateString((String)p_map.get(VOMSI.FIRST_CONSUMED_ON),PretupsI.TIMESTAMP_DDMMYYYYHHMMSS));
	        	}
			}catch (Exception e) {
				_log.error(methodName, "Exception " + e);
				_log.errorTrace(methodName, e);
				voucherVO.setConsumedOn(null);
			}
        if(p_map.get(VOMSI.VOUCHER_GENERATED_DATE) != null) {
        	try {
        		voucherVO.setGeneratedDateOn( BTSLUtil.getDateFromDateString(p_map.get(VOMSI.VOUCHER_GENERATED_DATE).toString(),PretupsI.DATE_FORMAT_DDMMYY));
	        } catch (ParseException e) {
				_log.error(methodName, "Exception " + e);
				_log.errorTrace(methodName, e);
				voucherVO.setGeneratedDateOn(null);
			}
        }
       // voucherVO.setConsumedOn((Date) p_map.get(VOMSI.FIRST_CONSUMED_ON));
        p_requestVO.setValidity((String) p_map.get(VOMSI.VOMS_VALIDITY));
        p_requestVO.setTalkTime(""+p_map.get(VOMSI.VOMS_TALKTIME));

        p_requestVO.setValueObject(voucherVO);
        // added for voucher query and rollback request
        if (VOMSI.SERVICE_TYPE_VOUCHER_QRY.equals(p_requestVO.getServiceType()) || VOMSI.VOUCHER_DIRECT_ROLLBACK.equals(p_requestVO.getServiceType())) {
            p_requestVO.setVoucherStatus((String) p_map.get(VOMSI.VOMS_STATUS));
            p_requestVO.setVoucherType((String) p_map.get(VOMSI.VOMS_TYPE));
            p_requestVO.setPin((String) p_map.get(VOMSI.PIN));
            p_requestVO.setExpiryDate((Date) p_map.get(VOMSI.EXPIRY_DATE));
            voucherVO.setProductName((String) p_map.get(VOMSI.PRODUCT_NAME));
            voucherVO.setProductID((String) p_map.get(VOMSI.PRODUCT_ID));
            if(p_map.get(VOMSI.FIRST_CONSUMED_ON)!=null&&!BTSLUtil.isNullString(p_map.get(VOMSI.FIRST_CONSUMED_ON).toString())){
                
                try {
                    voucherVO.setConsumedOn( BTSLUtil.getDateFromDateString(p_map.get(VOMSI.FIRST_CONSUMED_ON).toString(),PretupsI.TIMESTAMP_DATESPACEHHMMSS));
                } catch (ParseException e) {
                    _log.error(methodName, "Invalid date");
                }
                try {
                    voucherVO.setGeneratedDateOn( BTSLUtil.getDateFromDateString(p_map.get(VOMSI.VOUCHER_GENERATED_DATE).toString(),PretupsI.DATE_FORMAT_DDMMYY));
                } catch (ParseException e) {
                    _log.error(methodName, "Invalid date");
                }    
            }
            p_requestVO.setValidity((String) p_map.get(VOMSI.VOMS_VALIDITY));
            p_requestVO.setTalkTime((String) p_map.get(VOMSI.VOMS_TALKTIME));
            String enableddate=(String)p_map.get(VOMSI.VOUCHER_ENABLED_DATE);
            if(!BTSLUtil.isNullString(enableddate)) {
	            try{
	            	p_requestVO.setCreatedOn(BTSLUtil.getDateFromDateString(enableddate));
	            }catch(ParseException e){
	            	 _log.error(methodName, e.getMessage());
	            }
            }
            p_requestVO.setValueObject(voucherVO);
        }
        // added for Voucher retrieval RollBack Request
        if (VOMSI.SERVICE_TYPE_VOUCHER_RETRIEVAL_ROLLBACK.equals(p_requestVO.getServiceType())) {
            p_requestVO.setExternalTransactionNum((String) p_map.get(VOMSI.VOMS_TXNID));
        }
        p_requestVO.setResponseMap(p_map);
    }

    private void validateRequestFormat(RequestVO p_requestVO) throws BTSLBaseException {
        final String obj = "validateRequestFormat";
       if(_log.isDebugEnabled())
        _log.debug(obj, "p_requestVO"+p_requestVO.toString());
        
        if(VOMSI.SERVICE_TYPE_VOUCHER_CON.equals(p_requestVO.getServiceType()))
		{
			if(!(p_requestVO.getRequestMessageArray().length==6))
				throw new BTSLBaseException(this,obj,PretupsErrorCodesI.ERROR_VOMS_INVALID_REQUEST_FORMAT);
		}else if(VOMSI.SERVICE_TYPE_VOUCHER_ROLLBACK.equals(p_requestVO.getServiceType()))
		{
			if(!(p_requestVO.getRequestMessageArray().length==4))
				throw new BTSLBaseException(this,obj,PretupsErrorCodesI.ERROR_VOMS_INVALID_REQUEST_FORMAT);
		}
		
		else if(VOMSI.VOUCHER_RESERVATION.equals(p_requestVO.getServiceType()))
		{	
		
			if((p_requestVO.getRequestMessageArray().length<5)||(p_requestVO.getRequestMessageArray().length>6))
						throw new BTSLBaseException(this,obj,PretupsErrorCodesI.ERROR_VOMS_INVALID_REQUEST_FORMAT);
			
			if((BTSLUtil.isNullString(p_requestVO.getVoucherCode()))&&(BTSLUtil.isNullString(p_requestVO.getSerialNo()))){
				throw new BTSLBaseException(this,obj,PretupsErrorCodesI.VOUCHER_SERIAL_NO_ISREQUIRED);
			}
			
		
		}else if(VOMSI.VOUCHER_VALIDATION.equals(p_requestVO.getServiceType()))
		{	
		
			if((p_requestVO.getRequestMessageArray().length<2)||(p_requestVO.getRequestMessageArray().length>3))
				throw new BTSLBaseException(this,obj,PretupsErrorCodesI.ERROR_VOMS_INVALID_REQUEST_FORMAT);
			
			
			
			if((BTSLUtil.isNullString(p_requestVO.getVoucherCode()))&&(BTSLUtil.isNullString(p_requestVO.getSerialNo()))){
				throw new BTSLBaseException(this,obj,PretupsErrorCodesI.VOUCHER_SERIAL_NO_ISREQUIRED);
			}
		}
        
		else if(VOMSI.VOUCHER_DIRECT_CONSUMPTION.equals(p_requestVO.getServiceType())|| VOMSI.VOUCHER_DIRECT_ROLLBACK.equals(p_requestVO.getServiceType()) )
		{	
			if((p_requestVO.getRequestMessageArray().length<5)||(p_requestVO.getRequestMessageArray().length>6))
				throw new BTSLBaseException(this,obj,PretupsErrorCodesI.ERROR_VOMS_INVALID_REQUEST_FORMAT);

			if((BTSLUtil.isNullString(p_requestVO.getVoucherCode()))&&(BTSLUtil.isNullString(p_requestVO.getSerialNo()))){
				throw new BTSLBaseException(this,obj,PretupsErrorCodesI.VOUCHER_SERIAL_NO_ISREQUIRED);
			}

			if(BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn())){
				throw new BTSLBaseException(this,obj,PretupsErrorCodesI.VOUCHER_SUBSCRIBER_MSISDN_ISREQUIRED);
			}

		}
        
        
		else if(VOMSI.SERVICE_TYPE_VOUCHER_QRY.equals(p_requestVO.getServiceType())) { 
		
			if((p_requestVO.getRequestMessageArray().length<3)||(p_requestVO.getRequestMessageArray().length>4))
				throw new BTSLBaseException(this,obj,PretupsErrorCodesI.ERROR_VOMS_INVALID_REQUEST_FORMAT);
			
			if((BTSLUtil.isNullString(p_requestVO.getVoucherCode()))&&(BTSLUtil.isNullString(p_requestVO.getSerialNo()))){
				throw new BTSLBaseException(this,obj,PretupsErrorCodesI.VOUCHER_SERIAL_NO_ISREQUIRED);
			}

		}
        
		
		else{
			if(!(p_requestVO.getRequestMessageArray().length==3))
				throw new BTSLBaseException(this,obj,PretupsErrorCodesI.ERROR_VOMS_INVALID_REQUEST_FORMAT);

		}

    }
}
