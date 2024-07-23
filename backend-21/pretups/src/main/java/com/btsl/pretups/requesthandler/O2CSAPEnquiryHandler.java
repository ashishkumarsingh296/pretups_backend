package com.btsl.pretups.requesthandler;

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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.pretups.channel.transfer.businesslogic.ChannelTransferTxnDAO;

public class O2CSAPEnquiryHandler implements ServiceKeywordControllerI {

    private Log log = LogFactory.getLog(O2CSAPEnquiryHandler.class.getName());

    private RequestVO _requestVO = null;
    private HashMap _requestMap = null;
    private String _extTxnNo = null;
    private Date _extTxnDate = null;
    private static String _transactionID = null;
    private ChannelTransferDAO channelTransferDAO = null;
    private ChannelTransferTxnDAO channelTransferTxnDAO = null;
    private Date _toDate = null;
    private Date _fromDate = null;

    private void validate(RequestVO p_requestVO) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "validate";
        if (log.isDebugEnabled()) {
            log.debug("validate", " Entered " + p_requestVO);
        }
        String fromDate = (String) _requestMap.get("FROMDATE");
        String toDate = (String) _requestMap.get("TODATE");
        try {

            if (BTSLUtil.isNullString(fromDate)) {
                _requestMap.put("RES_ERR_KEY", "FROMDATE");
                throw new BTSLBaseException("O2CTransferEnquiryHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }
            if (BTSLUtil.isNullString(toDate)) {
                _requestMap.put("RES_ERR_KEY", "TODATE");
                throw new BTSLBaseException("O2CTransferEnquiryHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }
            HashMap dateMap = null;

            // date format validation
            try {
                dateMap = HandlerUtil.dateValidation(fromDate, toDate);
            } catch (BTSLBaseException be) {
                log.errorTrace(METHOD_NAME, be);
                _requestMap.put("RES_ERR_KEY", "DATEFORMAT");
                throw be;
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
                _requestMap.put("RES_ERR_KEY", "DATEFORMAT");
                throw new BTSLBaseException("O2CTransferEnquiryHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_MANDATORY_VALUE);
            }
            _fromDate = (Date) dateMap.get(HandlerUtil.FROM_DATE);
            _toDate = (Date) dateMap.get(HandlerUtil.TO_DATE);
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            _requestMap.put("RES_ERR_KEY", "DATEFORMAT");
            throw new BTSLBaseException("O2CTransferEnquiryHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
        }
        if (log.isDebugEnabled()) {
            log.debug("validate", "Exit ");
        }
    }

    private void validateO2CUpdateReq(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "validateO2CUpdateReq";
        if (log.isDebugEnabled()) {
            log.debug("validateO2CUpdateReq", " Entered " + p_requestVO);
        }
        _transactionID = (String) p_requestVO.getTransactionID();
        String extTxnNumber = (String) p_requestVO.getExternalTransactionNum();
        String extTxnDate = (String) p_requestVO.getExternalTransactionDate();
        try {

            if (BTSLUtil.isNullString(_transactionID)) {
                _requestMap.put("RES_ERR_KEY", "TRANSFER_ID");
                throw new BTSLBaseException("O2CTransferEnquiryHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            } else if (_transactionID.length() > 20) {
                _requestMap.put("RES_ERR_KEY", "TRANSFER_ID_LENGTH");
                throw new BTSLBaseException("O2CTransferEnquiryHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_MANDATORY_VALUE);

            }
            if (BTSLUtil.isNullString(extTxnDate)) {
                _requestMap.put("RES_ERR_KEY", "EXT_TXN_DATE");
                throw new BTSLBaseException("O2CTransferEnquiryHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_EXTERNAL_DATE_FORMAT);
            }
            if (BTSLUtil.isNullString(extTxnNumber)) {
                _requestMap.put("RES_ERR_KEY", "EXT_TXN_ID");
                throw new BTSLBaseException("O2CTransferEnquiryHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            } else if (extTxnNumber.length() > 20) {
                _requestMap.put("RES_ERR_KEY", "EXT_TXN_ID_LENGTH");
                throw new BTSLBaseException("O2CTransferEnquiryHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_MANDATORY_VALUE);

            } else {

                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_NUMERIC))).booleanValue()) {
                    long externalTxnIDLong = 0;
                    try {
                        if (BTSLUtil.isNumeric(extTxnNumber)) {
                            externalTxnIDLong = Long.parseLong(extTxnNumber);
                            if (externalTxnIDLong < 0) {
                                _requestMap.put("RES_ERR_KEY", "EXT_TXN_ID_INVALID");
                                throw new BTSLBaseException("O2CSAPEnquiryHandler", "validateExtCodeUpdateRequest", PretupsErrorCodesI.ERROR_EXT_TXN_NO_NOT_POSITIVE);
                            }
                        } else {
                            _requestMap.put("RES_ERR_KEY", "EXT_TXN_ID_INVALID");
                            throw new BTSLBaseException("O2CSAPEnquiryHandler", "validateExtCodeUpdateRequest", PretupsErrorCodesI.ERROR_EXT_TXN_NO_NOT_NUMERIC);
                        }
                    } catch (Exception e) {
                        log.errorTrace(METHOD_NAME, e);
                        _requestMap.put("RES_ERR_KEY", "EXT_TXN_ID_INVALID");
                        throw new BTSLBaseException("O2CSAPEnquiryHandler", "validateExtCodeUpdateRequest", PretupsErrorCodesI.ERROR_EXT_TXN_NO_NOT_NUMERIC);
                    }
                }

                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE))).booleanValue()) {

                    boolean isExternalTxnExists = channelTransferDAO.isExtTxnExists(p_con, extTxnNumber, null);
                    if (isExternalTxnExists) {
                        _requestMap.put("RES_ERR_KEY", "EXT_TXN_ID_ALREADY_EXIT");
                        throw new BTSLBaseException("O2CSAPEnquiryHandler", "validateExtCodeUpdateRequest", PretupsErrorCodesI.ERROR_EXT_TXN_NO_NOT_UNIQUE);
                    }
                }
                _extTxnNo = extTxnNumber;
            }

            String dateFormat = Constants.getProperty("CCE_XML_EXTERNAL_DATE_FORMAT");

            Date p_fromDate = null;
            if (!BTSLUtil.isNullString(extTxnDate)) {
                try {
                    p_fromDate = BTSLUtil.getDateFromDateString(extTxnDate, dateFormat);
                } catch (ParseException pe) {
                    log.errorTrace(METHOD_NAME, pe);
                    _requestMap.put("RES_ERR_KEY", "EXT_TXN_DATE_FORMAT");
                    throw new BTSLBaseException("HandlerUtil", "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_EXTERNAL_DATE_FORMAT);
                }
                p_fromDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(p_fromDate));
            }

            _extTxnDate = p_fromDate;

        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            _requestMap.put("RES_ERR_KEY", "DATEFORMAT");
            throw new BTSLBaseException("O2CTransferEnquiryHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
        }
        if (log.isDebugEnabled()) {
            log.debug("validate", "Exit ");
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        Connection con = null;
        MComConnectionI mcomCon = null;
        String type = null;
        try {
            _requestVO = p_requestVO;
            _requestMap = p_requestVO.getRequestMap();
            type = (String) _requestVO.getDecryptedMessage();
            channelTransferTxnDAO = new ChannelTransferTxnDAO();
            _requestVO.setSuccessTxn(false);
            mcomCon = new MComConnection();con=mcomCon.getConnection();

            if (type.equalsIgnoreCase(PretupsI.O2C_SAP_ENQUIRY)) {
                validate(_requestVO);

                ArrayList transferList = channelTransferTxnDAO.loadO2CChannelTransfersListForSAP(con, _fromDate, _toDate);

                _requestMap.put("TRANSFERLIST", transferList);

                if (transferList != null && transferList.size() > 0) {
                    _requestVO.setSuccessTxn(true);
                } else {
                    _requestVO.setSuccessTxn(false);
                    throw new BTSLBaseException(this, "loadTransferSummary", PretupsErrorCodesI.CCE_ERROR_TRANSFER_DETAILS_NOT_FOUND);
                }
                _requestVO.setSenderMessageRequired(false);

            } else if (type.equalsIgnoreCase(PretupsI.O2C_SAP_UPDATE)) {
                validateO2CUpdateReq(con, _requestVO);
                ArrayList transferList = channelTransferTxnDAO.loadO2CChannelTransfersListForSAPUPdate(con, _transactionID);

                if (transferList != null && transferList.size() > 0) {
                    _requestMap.put("TRANSFERLIST", transferList);
                    int updateCount = channelTransferTxnDAO.updateExternalCodeForAutoO2CRequest(con, _transactionID, _extTxnNo, _extTxnDate);
                    if (updateCount > 0) {
                        _requestVO.setSuccessTxn(true);
                        _requestVO.setMessageCode(PretupsErrorCodesI.AUTO_O2C_EXTCODE_UPDATE_SUCCESS);
                        mcomCon.finalCommit();
                    } else {
                        _requestVO.setSuccessTxn(false);
                        _requestVO.setMessageCode(PretupsErrorCodesI.AUTO_O2C_EXTCODE_UPDATE_INVALIDDATA);
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.AUTO_O2C_EXTCODE_UPDATE_INVALIDDATA);
                    }
                } else {
                    _requestVO.setSuccessTxn(false);
                    _requestVO.setMessageCode(PretupsErrorCodesI.AUTO_O2C_EXTCODE_UPDATE_INVALIDDATA);
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.AUTO_O2C_EXTCODE_UPDATE_INVALIDDATA);

                }
            }

        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                log.errorTrace(METHOD_NAME, ee);
            }
            log.error("process", "Exception " + be.getMessage());
            p_requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                log.errorTrace(METHOD_NAME, ee);
            }
            log.error("process", "Exception " + e.getMessage());

            p_requestVO.setSuccessTxn(false);
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CSAPEnquiryHandler[process]", "", "", "", "Exception:" + e.getMessage());
        } finally {
            p_requestVO.setRequestMap(_requestMap);
			if (mcomCon != null) {
				mcomCon.close("O2CSAPEnquiryHandler#process");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug("process", "", " Exited ");
            }
        }

    }

}
