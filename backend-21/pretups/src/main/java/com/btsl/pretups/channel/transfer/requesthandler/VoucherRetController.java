package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.txn.voms.voucher.businesslogic.VomsVoucherTxnDAO;

public class VoucherRetController implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(VoucherRetController.class.getName());
    private C2STransferVO _c2sTransferVO = null;
    private TransferItemVO _senderTransferItemVO = null;
    private TransferItemVO _receiverTransferItemVO = null;
    private Date _currentDate = null;
    private String _transferID;
    private boolean _recValidationFailMessageRequired = false;
    private boolean _recTopupFailMessageRequired = false;
    private String _notAllowedSendMessGatw;
    private static OperatorUtilI _operatorUtil = null;

    private String _lastTransferId = null;
    private boolean _oneLog = false;
    // Loads Operator specific class. In MVD controller it is used for
    // validating the message format.
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherRetController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /*
     * In the constructor of VoucherRetController initialize the date variable
     * _currentDate with current date. The
     * variables MVD_REC_GEN_FAIL_MSG_REQD_V & MVD_REC_GEN_FAIL_MSG_REQD_T
     * decides whether the validation and
     * top up failed message send to receiver or not.
     */

    public VoucherRetController() {
        _log.debug("process", "Entered");
        _c2sTransferVO = new C2STransferVO();
        _currentDate = new Date();
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
        final HashMap<String, String> responseMap = new HashMap<String, String>();
        final String methodName = "process";
        List<String> vouchersPINAndSerial = null;
        // String tablename=null;
        final VomsVoucherTxnDAO vomsVoucherTxnDAO = new VomsVoucherTxnDAO();
        // VomsProductVO vomsProductVO=new VomsProductVO();
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
                    responseMap.put(VOMSI.VOMS_MRP, "N.A");
                    responseMap.put(VOMSI.TOPUP, "N.A");
                    responseMap.put(VOMSI.VOMS_TALKTIME, "N.A");
                    responseMap.put(VOMSI.VOMS_VALIDITY, "N.A");
                    responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
                    responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
                    responseMap.put(VOMSI.VALID, PretupsI.NO);
                    responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_TPYE_NULL);
                    responseMap.put(VOMSI.ERROR, "voucher type is null");
                    responseMap.put(VOMSI.CONSUMED, "N.A");
                    responseMap.put(VOMSI.VOMS_PIN, "N.A");
                    populateVOfromMap(responseMap, p_requestVO);
                } else {
                    if (!vomsVoucherTxnDAO.validateVoucherType(con, p_requestVO.getVoucherType())) {
                        responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
                        responseMap.put(VOMSI.VOMS_MRP, "N.A");
                        responseMap.put(VOMSI.VOMS_TALKTIME, "N.A");
                        responseMap.put(VOMSI.VOMS_VALIDITY, "N.A");
                        responseMap.put(VOMSI.TOPUP, "N.A");
                        responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
                        responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
                        responseMap.put(VOMSI.VALID, PretupsI.NO);
                        responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_TPYE_INVALID);
                        responseMap.put(VOMSI.ERROR, "voucher type is INVALID");
                        responseMap.put(VOMSI.CONSUMED, "N.A");
                        responseMap.put(VOMSI.VOMS_PIN, "N.A");
                        populateVOfromMap(responseMap, p_requestVO);
                    } else {
                        validateRequestFormat(p_requestVO);
                        getVoucher(p_requestVO, con);
                        p_requestVO.setVomsMessage(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                    }

                }
            }

            else {
                validateRequestFormat(p_requestVO);
                getVoucher(p_requestVO, con);
                p_requestVO.setVomsMessage(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            }

        } catch (BTSLBaseException be) {
            _log.error("processRequest", "BTSLBaseException" + be);
            responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
            responseMap.put(VOMSI.VOMS_MRP, "N.A");
            responseMap.put(VOMSI.TOPUP, "N.A");
            responseMap.put(VOMSI.VOMS_TALKTIME, "N.A");
            responseMap.put(VOMSI.VOMS_VALIDITY, "N.A");
            responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
            responseMap.put(VOMSI.VALID, PretupsI.NO);
            responseMap.put(VOMSI.MESSAGE, be.getMessage());
            responseMap.put(VOMSI.ERROR, " Exception " + be.getMessage());
            responseMap.put(VOMSI.CONSUMED, "N.A");
            responseMap.put(VOMSI.VOMS_PIN, "N.A");

            populateVOfromMap(responseMap, p_requestVO);
        } catch (Exception e) {
            _log.error("processRequest", "Exception" + e);
            responseMap.put(VOMSI.SERIAL_NO, PretupsI.NOT_APPLICABLE);
            responseMap.put(VOMSI.VOMS_MRP, "N.A");
            responseMap.put(VOMSI.TOPUP, "N.A");
            responseMap.put(VOMSI.VOMS_TALKTIME, "N.A");
            responseMap.put(VOMSI.VOMS_VALIDITY, "N.A");
            responseMap.put(VOMSI.SUBSCRIBER_ID, PretupsI.NOT_APPLICABLE);
            responseMap.put(VOMSI.REGION, PretupsI.NOT_APPLICABLE);
            responseMap.put(VOMSI.VALID, PretupsI.NO);
            responseMap.put(VOMSI.MESSAGE, PretupsErrorCodesI.ERROR_VOMS_GEN);
            responseMap.put(VOMSI.ERROR, " Exception " + e.getMessage());
            responseMap.put(VOMSI.CONSUMED, "N.A");
            responseMap.put(VOMSI.VOMS_PIN, "N.A");
            populateVOfromMap(responseMap, p_requestVO);
        } finally {
        	if(mcomCon != null){mcomCon.close("VoucherRetController#process");mcomCon=null;}
        }
        // added by nilesh: consolidated for logger
        if (_oneLog) {
            OneLineTXNLog.log(_c2sTransferVO, _senderTransferItemVO, _receiverTransferItemVO);
            // making entry in the transaction log
        }
        TransactionLog.log(_transferID + "-" + _lastTransferId, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
            PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + p_requestVO.getMessageCode());
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting");
        }
    }// end of finally

    private void populateVOfromMap(HashMap p_map, RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("populateVOfromMap", "Entered p_map" + p_map);
        }
        final String METHOD_NAME = "populateVOfromMap";
        p_requestVO.setSerialNo((String) p_map.get(VOMSI.SERIAL_NO));
        try {
            p_requestVO.setVoucherAmount(Long.parseLong((String) p_map.get(VOMSI.TOPUP)));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        p_requestVO.setVomsMessage((String) p_map.get(VOMSI.MESSAGE));
        p_requestVO.setVomsError((String) p_map.get(VOMSI.ERROR));
        p_requestVO.setReceiverMsisdn((String) p_map.get(VOMSI.SUBSCRIBER_ID));
        p_requestVO.setValidity((String) p_map.get(VOMSI.VOMS_VALIDITY));
        p_requestVO.setTalkTime((String) p_map.get(VOMSI.VOMS_TALKTIME));
        p_requestVO.setVomsValid((String) p_map.get(VOMSI.VALID));
        p_requestVO.setReqAmount((String) p_map.get(VOMSI.VOMS_MRP));
        p_requestVO.setTalkTime((String) p_map.get(VOMSI.TOPUP));
        p_requestVO.setEvdPin((String) p_map.get(VOMSI.VOMS_PIN));

    }

    private void validateRequestFormat(RequestVO p_requestVO) throws BTSLBaseException {
        final String obj = "validateRequestFormat";
        if ((p_requestVO.getRequestMessageArray().length == 6) || (p_requestVO.getRequestMessageArray().length == 3) || (p_requestVO.getRequestMessageArray().length == 7) ) {
            final String SubID = p_requestVO.getRequestMessageArray()[1];
            if (!BTSLUtil.isValidMSISDN(SubID)) {
                throw new BTSLBaseException(this, obj, PretupsErrorCodesI.ERROR_VOMS_SUBID_INVALID);
            }
        } else {
            throw new BTSLBaseException(this, obj, PretupsErrorCodesI.ERROR_VOMS_INVALID_REQUEST_FORMAT);
        }

    }

    private void getVoucher(RequestVO p_requestVO, Connection con) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getVoucher", "Entered ");
        }
        final String obj = "getVoucher";
        List<String> listOfVouchersPINAndSerial = null;
        try {
            final VomsProductVO vomsProductVO = new VomsProductVO();
            final VomsVoucherTxnDAO vomsVoucherTxnDAO = new VomsVoucherTxnDAO();
            if ("VR".equals(p_requestVO.getRequestMessageArray()[0])) {
                vomsProductVO.setServiceCode(p_requestVO.getRequestMessageArray()[3]);
                vomsProductVO.setMrpStr(String.valueOf(PretupsBL.getSystemAmount(p_requestVO.getRequestMessageArray()[2])));
                vomsProductVO.setSubService(p_requestVO.getRequestMessageArray()[4]);
               	vomsVoucherTxnDAO.loadProductActiveDetilsFromService(con, vomsProductVO);

                if (BTSLUtil.isNullString(vomsProductVO.getProductID())) {
                    throw new BTSLBaseException(this, obj, PretupsErrorCodesI.ERROR_VOMS_NO_ACTIVE_PRODUCT_FOUND);
                } else {
                	if((p_requestVO.getRequestMessageArray().length == 7)){
                		vomsProductVO.setVoucherQuantity(p_requestVO.getRequestMessageArray()[5]);
                		listOfVouchersPINAndSerial = VomsVoucherTxnDAO.getVoucherForMrp(con, vomsProductVO,p_requestVO.getRequestMessageArray()[1], p_requestVO.getRequestMessageArray()[6]);
                	}
                    else{
                    	listOfVouchersPINAndSerial = VomsVoucherTxnDAO.getVoucherForMrp(con, vomsProductVO,p_requestVO.getRequestMessageArray()[1], p_requestVO.getRequestMessageArray()[5]);
                    }
                    if (listOfVouchersPINAndSerial == null || listOfVouchersPINAndSerial.isEmpty()) {
                        throw new BTSLBaseException(this, obj, PretupsErrorCodesI.PIN_NOTUPDATED);
                    } else {
                    	if((p_requestVO.getRequestMessageArray().length == 7)){
                    		 if (_log.isDebugEnabled()) {
                    	            _log.debug("getVoucher", "listOfVouchersPINAndSerial.get(0) "+ listOfVouchersPINAndSerial.get(0) 
                    	            		+" and length= "+listOfVouchersPINAndSerial.get(0).trim().length());
                    	        }
                    		StringBuilder decryptedPINinCSV  = new StringBuilder();
                    		String[] pins = listOfVouchersPINAndSerial.get(0)
                    				.substring(0, listOfVouchersPINAndSerial.get(0).trim().lastIndexOf(','))
                    				.split(",");
                    		for(String pin : pins){
                    			pin = VomsUtil.decryptText(pin);
                    			decryptedPINinCSV.append(pin+",");
                    		}
                    		p_requestVO.setEvdPin(decryptedPINinCSV.toString().substring(0,decryptedPINinCSV.toString().lastIndexOf(',')) );
                    		p_requestVO.setSerialNo(listOfVouchersPINAndSerial.get(1).substring(0, listOfVouchersPINAndSerial.get(1).lastIndexOf(',')));
                    	}
                    	else{
                    		final String pin = VomsUtil.decryptText(vomsProductVO.getPinNo());
                    		p_requestVO.setEvdPin(pin);
                    		p_requestVO.setSerialNo(vomsProductVO.getSerialNo());

                    	}
                    	p_requestVO.setTalkTime(vomsProductVO.getTalkTimeStr());
                        p_requestVO.setValidity(String.valueOf(vomsProductVO.getValidity()));
                		p_requestVO.setExpiryDate(vomsProductVO.getExpiryDate());
                        p_requestVO.setReqAmount(p_requestVO.getRequestMessageArray()[2]);
                        p_requestVO.setReceiverMsisdn(p_requestVO.getRequestMessageArray()[1]);
                        p_requestVO.setState("EN");
                        p_requestVO.setVomsError("0");
                    }
                }
            } else {
                vomsProductVO.setSubID(p_requestVO.getRequestMessageArray()[1]);
                vomsProductVO.setTxnID(p_requestVO.getRequestMessageArray()[2]);
                vomsVoucherTxnDAO.getVoucherByTransactionID(con, vomsProductVO);
                if (BTSLUtil.isNullString(vomsProductVO.getPinNo())) {
                    throw new BTSLBaseException(this, obj, PretupsErrorCodesI.PIN_NOTUPDATED);
                } else {
                    final String pin = VomsUtil.decryptText(vomsProductVO.getPinNo());
                    p_requestVO.setEvdPin(pin);
                    p_requestVO.setSerialNo(vomsProductVO.getSerialNo());
                    p_requestVO.setTalkTime(PretupsBL.getDisplayAmount(vomsProductVO.getTalkTime()));
                    p_requestVO.setReqAmount(vomsProductVO.getMrpStr());
                    p_requestVO.setReceiverMsisdn(p_requestVO.getRequestMessageArray()[1]);
                    p_requestVO.setExpiryDate(vomsProductVO.getExpiryDate());
                    p_requestVO.setState("EN");
                    p_requestVO.setVomsError("0");
                }

            }

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {

            _log.error("getVoucher", "Exception" + e);
            _log.errorTrace("getVoucher", e);
        }

    }

}
