package com.btsl.pretups.requesthandler;

/**
 * * @(#)C2STransferHandler.java
 * Copyright(c) 2005-2006, Bharti Telesoft Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Siddhartha Srivastava Dec 12, 2006 Initial Creation
 * 
 * Customer care user can send the request for the enquiry of the transaction.
 * This class handles the transaction
 * enquiry of type C2S Transfer
 * 
 */

import java.sql.Connection;
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
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class C2STransferHandler implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(C2STransferHandler.class.getName());

    // declaring the tag names
    private static final String XML_TAG_TODATE = "TODATE";
    private static final String XML_TAG_FROMDATE = "FROMDATE";
    private static final String XML_TAG_SENDERMSISDN = "SENDERMSISDN";
    private static final String XML_TAG_MSISDN2 = "MSISDN2";
    private static final String XML_TAG_TRANSACTIONID = "TRANSACTIONID";
    private static final String XML_TAG_SERTYPE = "SRVTYPE";

    private HashMap _requestMap = null;
    private RequestVO _requestVO = null;
    private Date _toDate = null;
    private Date _fromDate = null;
    private String _serType = null;

    /**
     * This method is the entry point in the class.This methods in turns call
     * the private methods to carry
     * out the C2S Transfer enquiry request.
     * 
     * @param p_requestVO
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Entered....: p_requestVO= " + p_requestVO);
        }

        _requestVO = p_requestVO;
        Connection con = null;MComConnectionI mcomCon = null;

        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
            _requestMap = _requestVO.getRequestMap();
            // short code for the service type
            _serType = (String) _requestMap.get(XML_TAG_SERTYPE);
            // this call validates all the passed parameters of the request
            validate(con);

            ArrayList transferList = loadTransferSummary(con);
            ArrayList trfDetails = null;
            HashMap transferMap = new HashMap();
            // this check ensures that when the transactionID is not null, a
            // check on the network is made so that any
            // request for transaction id outside the network is not entertained
            if (!BTSLUtil.isNullString((String) _requestMap.get(XML_TAG_TRANSACTIONID))) {
                C2STransferVO transferVO = (C2STransferVO) transferList.get(0);
                if (!(transferVO.getNetworkCode()).equals(((UserVO) _requestVO.getSenderVO()).getNetworkID())) {
                    throw new BTSLBaseException("C2STransferEnquiryHandler", "process", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
                trfDetails = loadTransferDetails(con);
                transferMap.put("DETAILS", trfDetails);
                _requestVO.setServiceKeyword("C2SENQUIRYDTLS");  
            }
            transferMap.put("SUMMARY", transferList);
            _requestVO.setValueObject(transferMap);
            _requestVO.setSuccessTxn(true);
        } catch (BTSLBaseException be) {
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception ex) {
            _requestVO.setSuccessTxn(false);

            // Rollbacking the transaction
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "Exception " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferHandler[process]", "", "", "", "Exception:" + ex.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }// end of Exception
        finally {
            _requestVO.setRequestMap(_requestMap);
            p_requestVO = _requestVO;
            // setting the variable to null for efficient garbage collection
            _requestMap = null;
            _requestVO = null;
            _toDate = null;
            _fromDate = null;
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("C2STransferHandler#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }// end of finally
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Exiting....");
        }
    }

    /**
     * This methods validates the input parameters of the request.It performs
     * the checks on these paramteres and throws
     * an error if any condition is not satidfied.Also it makes an entry in the
     * requestMap in case of error
     * 
     * @param p_con
     * @throws BTSLBaseException
     */
    private void validate(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Entered....");
        }

        boolean tagValPresent = false;
        String msisdnPrefix = null;
        String arr[] = null;
        String filteredMsisdn = null;

        try {
            // Unique number of the transaction.If this is present then this
            // takes precedence over msisdn2, senderMsisdn etc
            String transactionID = (String) _requestMap.get(XML_TAG_TRANSACTIONID);

            // checking the value of transactionID for not null
            if (!BTSLUtil.isNullString(transactionID)) {
                tagValPresent = true;
            } else // checking the remaining values only in case Transaction ID
                   // is null
            {
                // channelUser Msisdn
                String senderMSISDN = (String) _requestMap.get(XML_TAG_SENDERMSISDN);
                // subscriber Msisdn
                String msisdn2 = (String) _requestMap.get(XML_TAG_MSISDN2);

                // checking the value of senderMsisdn for not null
                if (!BTSLUtil.isNullString(senderMSISDN)) {
                    // filtering the msisdn for country independent dial format
                    filteredMsisdn = PretupsBL.getFilteredMSISDN(senderMSISDN);
                    if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                        _requestMap.put("RES_ERR_KEY", XML_TAG_SENDERMSISDN);
                        throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
                    }
                    msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);

                    // checking whether the msisdn prefix is valid in the
                    // network
                    NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                    if (networkPrefixVO == null) {
                        _requestMap.put("RES_ERR_KEY", XML_TAG_SENDERMSISDN);
                        arr = new String[] { filteredMsisdn };
                        throw new BTSLBaseException("C2STransferHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK, arr);
                    }
                    String networkCode = networkPrefixVO.getNetworkCode();
                    if (networkCode != null && !networkCode.equals(((UserVO) _requestVO.getSenderVO()).getNetworkID())) {
                        throw new BTSLBaseException("C2STransferHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                    }

                    _requestVO.setFilteredMSISDN(filteredMsisdn);
                    tagValPresent = true;

                }// checking the value of msisdn2 for not null

                if (!BTSLUtil.isNullString(msisdn2)) {
                    // filtering the msisdn for country independent dial format
                    filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn2);
                    if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                        _requestMap.put("RES_ERR_KEY", XML_TAG_MSISDN2);
                        arr = new String[] { filteredMsisdn };
                        throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN, arr);
                    }
                    tagValPresent = true;
                }
            }

            // checking the value of serType for not null and then validating
            // the value from the the list of service
            // In case the match is not found , an error is thrown. In case the
            // value entered is ALL no check is made.
            // Also if no value is provided, the default value of 'ALL' is set
            // for serType
            if (!BTSLUtil.isNullString(_serType)) {
                _serType = _serType.trim();
                if (!PretupsI.ALL.equals(_serType)) {
                    ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
                    ArrayList serviceList = servicesTypeDAO.loadServicesListForReconciliation(p_con, PretupsI.C2S_MODULE);
                    if (BTSLUtil.isNullString(BTSLUtil.getOptionDesc(_serType, serviceList).getValue())) {
                        _requestMap.put("RES_ERR_KEY", XML_TAG_SERTYPE);
                        arr = new String[] { _serType };
                        throw new BTSLBaseException("C2STransferHandler", "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_SERVICE_KEYWORD, arr);
                    }
                }
                tagValPresent = true;
            } else {
                _serType = PretupsI.ALL;
                tagValPresent = true;
            }
            // this check is when no tag value is present for any among msisdn2,
            // sendermsisdn and serType or transaction ID
            if (!tagValPresent) {
                _requestMap.put("RES_ERR_KEY", XML_TAG_TRANSACTIONID + "/" + XML_TAG_SENDERMSISDN + "/" + XML_TAG_MSISDN2);
                throw new BTSLBaseException("C2STransferHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_ATLEAST_ONE_VALUE_REQUIRED);
            }

            // toDate and fromDate are mandatory in all case so seperate check
            // is made.
            // gets the fromDate in string form
            String fromDate = (String) _requestMap.get(XML_TAG_FROMDATE);
            // this checks if the FROMDATE tag has no value specified and throws
            // error if not
            if (BTSLUtil.isNullString(fromDate)) {
                _requestMap.put("RES_ERR_KEY", XML_TAG_FROMDATE);
                throw new BTSLBaseException("C2STransferHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_FROM_DATE_REQUIRED);
            }

            // gets the toDate in String form
            String toDate = (String) _requestMap.get(XML_TAG_TODATE);
            // this checks if the TODATE tag has no value specified and throws
            // error if not
            if (BTSLUtil.isNullString(toDate)) {
                _requestMap.put("RES_ERR_KEY", XML_TAG_TODATE);
                throw new BTSLBaseException("C2STransferHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_TO_DATE_REQUIRED);
            }

            // this method validates on all the aspects for date and assigns the
            // proper values to the global date variable
            // _toDate and _fromDate.This method returns an HashMap
            HashMap dateMap = HandlerUtil.dateValidation(fromDate, toDate);
            _fromDate = (Date) dateMap.get(HandlerUtil.FROM_DATE);
            _toDate = (Date) dateMap.get(HandlerUtil.TO_DATE);

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validate", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("validate", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("C2STransferHandler", "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Exiting ....");
        }
    }

    /**
     * This method loads the transfer details based on the passed values of
     * sender msisdn, receiver msisdn, service keyword .
     * 
     * @param p_con
     * @return ArrayList
     * @throws BTSLBaseException
     */

    private ArrayList loadTransferSummary(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadTransferSummary";
        if (_log.isDebugEnabled()) {
            _log.debug("loadTransferSummary", "Entered....");
        }

        C2STransferDAO c2STransferDAO = new C2STransferDAO();
        ArrayList transferVOList = null;
        try {
            transferVOList = c2STransferDAO.loadC2STransferVOList(p_con, ((UserVO) _requestVO.getSenderVO()).getNetworkID(), _fromDate, _toDate, (String) _requestMap.get(XML_TAG_SENDERMSISDN), (String) _requestMap.get(XML_TAG_MSISDN2), (String) _requestMap.get(XML_TAG_TRANSACTIONID), _serType);
            if (!(transferVOList != null && transferVOList.size() > 0)) {
                throw new BTSLBaseException("C2STransferHandler", "loadTransferSummary", PretupsErrorCodesI.CCE_ERROR_TRANSFER_DETAILS_NOT_FOUND);
            }
            _requestMap.put("RES_TYPE", "SUMMARY");
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("loadTransferSummary", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("loadTransferSummary", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferHandler[loadTransferSummary]", "", "", "", "Exception:" + e.getMessage());
            // throw new BTSLBaseException("C2STransferHandler",
            // "dateRangeCheck", "error.general.processing");
            throw new BTSLBaseException("C2STransferHandler", "loadTransferSummary", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadTransferSummary", "Exiting....");
        }
        return transferVOList;
    }

    /**
     * This method is the method which is called in addition to the
     * laodTransferSummary in case transactionID is given in request
     * This method loads the transfer details on the basis of the passed
     * transaction ID
     * 
     * @param p_con
     * @return ArrayList
     * @throws BTSLBaseException
     */
    private ArrayList loadTransferDetails(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadTransferDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("loadTransferDetails", "Entered....");
        }

        C2STransferDAO c2STransferDAO = new C2STransferDAO();
        ArrayList transferVOList = null;
        try {
            transferVOList = c2STransferDAO.loadC2STransferItemsVOList(p_con, (String) _requestMap.get(XML_TAG_TRANSACTIONID), _fromDate, _toDate);
            if (!(transferVOList != null && transferVOList.size() > 0)) {
                throw new BTSLBaseException("C2STransferHandler", "loadTransferDetails", PretupsErrorCodesI.CCE_ERROR_TRANSFER_DETAILS_NOT_FOUND);
            }
            _requestMap.put("RES_TYPE", "DETAILS");
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("loadTransferDetails", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("loadTransferDetails", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferHandler[loadTransferDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("C2STransferHandler", "loadTransferDetails", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadTransferDetails", "Exiting....");
        }
        return transferVOList;
    }

}
