package com.btsl.pretups.requesthandler;

/**
 * * @(#)C2CTransferHandler.java
 * Copyright(c) 2005-2006, Bharti Telesoft Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Siddhartha Srivastava Dec 13, 2006 Initial Creation
 * 
 * This class handles the request for the enquiry of the transaction between
 * channel users. Customer care
 * user can send the MSISDN of a retailer who was involved in the transaction
 * and on that basis can get the
 * details of the transaction
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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class C2CTransferHandler implements ServiceKeywordControllerI {

    private Log _log = LogFactory.getLog(C2STransferHandler.class.getName());

    // declaring the tag names
    private static final String XML_TAG_TODATE = "TODATE";
    private static final String XML_TAG_FROMDATE = "FROMDATE";
    private static final String XML_TAG_SENDERMSISDN = "MSISDN";
    private static final String XML_TAG_TRANSACTIONID = "TRANSACTIONID";
    private static final String XML_TAG_TRFSTYPE = "TRFSTYPE";

    private HashMap _requestMap = null;
    private RequestVO _requestVO = null;
    private Date _toDate = null;
    private Date _fromDate = null;
    private String _transferType = null;

    /**
     * This method is the entry point in the class.This methods in turns call
     * the private methods to carry
     * out the C2C Transfer enquiry request.
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
            _requestMap = _requestVO.getRequestMap();
            // this call validates all the passed parameters of the request
            validate();

            mcomCon = new MComConnection();con=mcomCon.getConnection();
            ArrayList transferList = loadTransferSummary(con);

            ArrayList trfDetails = null;
            HashMap transferMap = new HashMap();
            // this check ensures that when the transactionID is not null, a
            // check on the network is made so that any
            // request for transaction id outside the network is not entertained
            if (!BTSLUtil.isNullString((String) _requestMap.get(XML_TAG_TRANSACTIONID))) {
                ChannelTransferVO transferVO = (ChannelTransferVO) transferList.get(0);
                if (!(transferVO.getNetworkCode()).equals(((UserVO) _requestVO.getSenderVO()).getNetworkID())) {
                    throw new BTSLBaseException("C2CTransferEnquiryHandler", "process", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
                trfDetails = loadTransferDetails(con, transferVO);
                transferMap.put("DETAILS", trfDetails);
                _requestVO.setServiceKeyword("C2CENQUIRYDTLS");  
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CTransferHandler[process]", "", "", "", "Exception:" + ex.getMessage());
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
				mcomCon.close("C2CTransferHandler#process");
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
     * an error if any condition is not satisfied.Also it makes an entry in the
     * requestMap in case of error
     * 
     * @throws BTSLBaseException
     */
    private void validate() throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Entered....");
        }

        String msisdnPrefix = null;
        String arr[] = null;
        String filteredMsisdn = null;

        try {
            // Unique number of the transaction.If this is present then this
            // takes precedence over msisdn2, senderMsisdn etc
            String transactionID = (String) _requestMap.get(XML_TAG_TRANSACTIONID);

            // If transaction ID is null then only fromDate, toDate, Transfer
            // type and senderMSISDN check should be done
            // otherwise no need.If any tag value is absent then proper error
            // messages are thrown
            if (BTSLUtil.isNullString(transactionID)) {
                // channelUser Msisdn
                String senderMSISDN = (String) _requestMap.get(XML_TAG_SENDERMSISDN);

                // short code for the service type
                _transferType = (String) _requestMap.get(XML_TAG_TRFSTYPE);

                if (!BTSLUtil.isNullString(_transferType)) {
                    _transferType = _transferType.trim();
                    if (!PretupsI.ALL.equals(_transferType)) {
                        ArrayList transferTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE, true);
                        if (BTSLUtil.isNullString(BTSLUtil.getOptionDesc(_transferType, transferTypeList).getValue())) {
                            _requestMap.put("RES_ERR_KEY", XML_TAG_TRFSTYPE);
                            throw new BTSLBaseException("C2CTransferHandler", "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_TRANSFER_TYPE);
                        }
                    }
                } else {
                    _transferType = PretupsI.ALL;
                }

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
                        arr = new String[] { filteredMsisdn };
                        throw new BTSLBaseException("C2CTransferHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK, arr);
                    }
                    String networkCode = networkPrefixVO.getNetworkCode();
                    if (networkCode != null && !networkCode.equals(((UserVO) _requestVO.getSenderVO()).getNetworkID())) {
                        throw new BTSLBaseException("C2CTransferHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                    }
                    // setting the value of fillteredMsisdn which will be used
                    // later
                    _requestVO.setFilteredMSISDN(filteredMsisdn);
                } else {
                    _requestMap.put("RES_ERR_KEY", XML_TAG_SENDERMSISDN);
                    throw new BTSLBaseException("C2CTransferHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_ATLEAST_ONE_VALUE_REQUIRED);
                }

                // getting the fromDate in string form
                String fromDate = (String) _requestMap.get(XML_TAG_FROMDATE);

                // getting the toDate
                String toDate = (String) _requestMap.get(XML_TAG_TODATE);

                // this method validates on all the aspects for date and assigns
                // the proper values to the global date variable
                // _toDate and _fromDate.This method returns an HashMap
                HashMap dateMap = HandlerUtil.dateValidation(fromDate, toDate);
                _fromDate = (Date) dateMap.get(HandlerUtil.FROM_DATE);
                _toDate = (Date) dateMap.get(HandlerUtil.TO_DATE);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validate", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("validate", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CTransferHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            // throw new BTSLBaseException("C2STransferHandler", "validate",
            // "error.general.processing");
            throw new BTSLBaseException("C2CTransferHandler", "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Exiting ....");
        }
    }

    /**
     * This method loads the transfer details based on the passed values for
     * sender msisdn, transfer type from date and to date
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

        ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
        ArrayList chnlTransferList = null;
        try {
            chnlTransferList = channelTransferDAO.loadChnlToChnlEnquiryTransfersList(p_con, _requestVO.getFilteredMSISDN(), _requestVO.getFilteredMSISDN(), _fromDate, _toDate, (String) _requestMap.get(XML_TAG_TRANSACTIONID), PretupsI.CHANNEL_TYPE_C2C, _transferType);
            if (!(chnlTransferList != null && chnlTransferList.size() > 0)) {
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CTransferHandler[loadTransferSummary]", "", "", "", "Exception:" + e.getMessage());
            // throw new BTSLBaseException("C2STransferHandler",
            // "dateRangeCheck", "error.general.processing");
            throw new BTSLBaseException("C2CTransferHandler", "loadTransferSummary", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadTransferSummary", "Exiting....");
        }
        return chnlTransferList;
    }

    /**
     * This method is the method which is called in addition to the
     * laodTransferSummary in case transactionID is given in request
     * This method loads the transfer details on the basis of the passed
     * transaction ID
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @return ArrayList
     * @throws BTSLBaseException
     */
    private ArrayList loadTransferDetails(Connection p_con, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadTransferDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("loadTransferDetails", "Entered....+p_channelTransferVO:= " + p_channelTransferVO);
        }

        ArrayList transferItemsList = null;
        try {
            transferItemsList = ChannelTransferBL.loadChannelTransferItemsWithBalances(p_con, p_channelTransferVO.getTransferID(), p_channelTransferVO.getNetworkCode(), p_channelTransferVO.getNetworkCodeFor(), p_channelTransferVO.getToUserID());
            if (transferItemsList.size() <= 0) {
                throw new BTSLBaseException("C2CTransferHandler", "loadTransferDetails", PretupsErrorCodesI.CCE_ERROR_TRANSFER_DETAILS_NOT_FOUND);
            }
            _requestMap.put("RES_TYPE", "DETAILS");
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("loadTransferDetails", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("loadTransferDetails", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CTransferHandler[loadTransferDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("C2CTransferHandler", "loadTransferDetails", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadTransferDetails", "Exiting....");
        }
        return transferItemsList;
    }
}
