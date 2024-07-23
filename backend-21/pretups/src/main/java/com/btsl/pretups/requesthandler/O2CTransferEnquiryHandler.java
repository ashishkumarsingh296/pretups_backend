/**
 * @(#)O2CTransferEnquiryHandler.java
 *                                    This controller is used for O2C Transfer
 *                                    Enquiry.
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    vikas yadav Dec 13, 2006 Initital Creation
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Copyright(c) 2006, Bharti Telesoft Ltd.
 *                                    All Rights Reserved
 */
package com.btsl.pretups.requesthandler;

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
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class O2CTransferEnquiryHandler implements ServiceKeywordControllerI {
	private String CLASS_NAME = "O2CTransferEnquiryHandler";
    private final Log log = LogFactory.getLog(O2CTransferEnquiryHandler.class.getName());
    private RequestVO _requestVO = null;
    private HashMap _requestMap = null;
    private String _trfCategory = null;
    private String _transactionId = null;
    private Date _toDate = null;
    private Date _fromDate = null;
    private String msisdn = null;

    /**
     * and sets the Channel User details in the p_requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     */
    @Override
	public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            _requestVO = p_requestVO;
            _requestMap = p_requestVO.getRequestMap();
            _trfCategory = (String) _requestMap.get("TRFCATEGORY");
            _transactionId = (String) _requestMap.get("TRANSACTIONID");

            // validate the request
            validate(_requestVO);
            mcomCon = new MComConnection();con=mcomCon.getConnection();

            ArrayList transferList = this.loadTransferSummary(con);
            ArrayList trfDetails = null;
            HashMap transferMap = new HashMap();
            if (!BTSLUtil.isNullString(_transactionId)) {
                ChannelTransferVO transferVO = (ChannelTransferVO) transferList.get(0);
                if (!(transferVO.getNetworkCode()).equals(((UserVO) p_requestVO.getSenderVO()).getNetworkID())) {
                    throw new BTSLBaseException(CLASS_NAME, "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
                trfDetails = this.loadTransferDetails(con, transferVO);
                transferMap.put("DETAILS", trfDetails);
                
                _requestVO.setServiceKeyword("O2CENQUIRYDTLS");                  
            }
            transferMap.put("SUMMARY", transferList);
            _requestVO.setValueObject(transferMap);
            _requestVO.setSuccessTxn(true);
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            try {
                if (con != null) {
                    con.rollback();
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

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                log.errorTrace(METHOD_NAME, ee);
            }
            log.error("process", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            p_requestVO.setSuccessTxn(false);
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CTransferEnquiryHandler[process]", "", "", "", "Exception:" + e.getMessage());
        } finally {
            p_requestVO.setRequestMap(_requestMap);
            if(mcomCon != null){mcomCon.close("O2CTransferEnquiryHandler#process");mcomCon=null;}
            if (log.isDebugEnabled()) {
                log.debug("process", "", " Exited ");
            }
        }
    }

    /**
     * check the mondatroy field, that is transaction Id or msisdn , transfer
     * category , from Date , and to date is comming in the request or not
     * 
     * @param p_requestVO
     * @return
     * @throws BTSLBaseException
     */
    private void validate(RequestVO p_requestVO) throws BTSLBaseException, Exception {
    	final String Method_Name="validate";
        if (log.isDebugEnabled()) {
            log.debug(Method_Name, " Entered " + p_requestVO);
        }
         msisdn = (String) _requestMap.get("MSISDN");
        
        String trfCategory = (String) _requestMap.get("TRFCATEGORY");
        String fromDate = (String) _requestMap.get("FROMDATE");
        String toDate = (String) _requestMap.get("TODATE");
        String transactionId = (String) _requestMap.get("TRANSACTIONID");
        String msisdnPrefix = null;
        String filteredMsisdn = null;
        String arr[] = null;
        // if transaction id is present then no need to check other parameters
        if (BTSLUtil.isNullString(transactionId)) {
            if (BTSLUtil.isNullString(msisdn) && BTSLUtil.isNullString(trfCategory) && BTSLUtil.isNullString(fromDate) && BTSLUtil.isNullString(toDate)) {
                _requestMap.put("RES_ERR_KEY", "TRANSACTIONID");
                throw new BTSLBaseException(CLASS_NAME, Method_Name, PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            } else if (BTSLUtil.isNullString(msisdn)) {
                _requestMap.put("RES_ERR_KEY", "MSISDN");
                throw new BTSLBaseException(CLASS_NAME, Method_Name, PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            } else if (BTSLUtil.isNullString(trfCategory)) {
                _requestMap.put("RES_ERR_KEY", "TRFCATEGORY");
                throw new BTSLBaseException(CLASS_NAME, Method_Name, PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }
            /*
             * else if(BTSLUtil.isNullString(fromDate)) //suggest by ved
             * {
             * _requestMap.put("RES_ERR_KEY","FROMDATE");
             * throw new BTSLBaseException("O2CTransferEnquiryHandler",
             * "validate" ,
             * PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
             * }
             * else if(BTSLUtil.isNullString(toDate))
             * {
             * _requestMap.put("RES_ERR_KEY","TODATE");
             * throw new BTSLBaseException("O2CTransferEnquiryHandler",
             * "validate" ,
             * PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
             * }
             */

            // filtering the msisdn for country independent dial format
            filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
            if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                _requestMap.put("RES_ERR_KEY", "MSISDN");
                throw new BTSLBaseException(this, Method_Name, PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
            }
            msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
            _requestVO.setFilteredMSISDN(filteredMsisdn);
            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null) {
                arr = new String[] { filteredMsisdn };
                throw new BTSLBaseException(CLASS_NAME, Method_Name, PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK, arr);
            }
            String networkCode = networkPrefixVO.getNetworkCode();
            if (networkCode != null && !networkCode.equals(((UserVO) p_requestVO.getSenderVO()).getNetworkID())) {
                throw new BTSLBaseException(CLASS_NAME, Method_Name, PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
            }
            // trf category check
            ArrayList transferCategoryList = LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE_FOR_TRFRULES, true);
            if (BTSLUtil.isNullString(BTSLUtil.getOptionDesc(trfCategory, transferCategoryList).getValue())) {
                throw new BTSLBaseException(CLASS_NAME, Method_Name, PretupsErrorCodesI.CCE_ERROR_INVALID_TRF_CATEGORY);
            }
            // date format validation
            HashMap dateMap = HandlerUtil.dateValidation(fromDate, toDate);
            _fromDate = (Date) dateMap.get(HandlerUtil.FROM_DATE);
            _toDate = (Date) dateMap.get(HandlerUtil.TO_DATE);
        }
        if (log.isDebugEnabled()) {
            log.debug(Method_Name, "Exit ");
        }
    }

    /**
     * This method loads the transfer details based on the passed values of
     * transactionId,fromDate,toDate and trfCategory
     * 
     * @param p_con
     * @return ArrayList
     * @throws BTSLBaseException
     */

    private ArrayList loadTransferSummary(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadTransferSummary";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered....");
        }

        ArrayList transferVOList = null;
        ChannelTransferVO transferVO = null;
       // String msisdn = (String) _requestMap.get("MSISDN");
        String userid=null;
		
		// code changes for bug fix for gp defect 55
		if (!BTSLUtil.isNullString(msisdn))
		{
			msisdn=msisdn.trim();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(p_con,msisdn);
			if(channelUserVO == null)
				throw new BTSLBaseException("this",METHOD_NAME,PretupsErrorCodesI.CCE_ERROR_USER_DETAIL_NOT_FOUND);
			userid=channelUserVO.getUserID();
		}
		
        try {

            ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
            // Load the enquiry Channel Transfer List
            transferVOList = channelTransferDAO.loadEnquiryChannelTransfersList(p_con, _transactionId, null, _fromDate, _toDate, null, null, null, _trfCategory, _requestVO.getFilteredMSISDN());
            if (!(transferVOList != null && transferVOList.size() > 0)) {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_ERROR_TRANSFER_DETAILS_NOT_FOUND);
            }
            
            // now if user entered the txnID direct and try to view the detail
            // of the txn then check the channel domain
            // and the geographical domain of the txn by the loggedIN user
            if (((UserVO) _requestVO.getSenderVO()).getUserType().equals(PretupsI.OPERATOR_USER_TYPE) && (!BTSLUtil.isNullString(_transactionId) || !BTSLUtil.isNullString(msisdn))) {
                transferVO = (ChannelTransferVO) transferVOList.get(0);
                UserVO userVO = ((UserVO) _requestVO.getSenderVO());
                boolean isChDomainCheckRequired = false;
                boolean isGeoDomainCheckRequired = false;
                ArrayList domainList = null;
                userid=transferVO.getToUserID();
                if (PretupsI.BCU_USER.equals(userVO.getCategoryVO().getCategoryCode())) {
                    isGeoDomainCheckRequired = true;
                }
                if (PretupsI.DOMAINS_ASSIGNED.equals(userVO.getCategoryVO().getFixedDomains())) {
                    domainList = userVO.getDomainList();
                    if (domainList == null || domainList.isEmpty()) {
                        throw new BTSLBaseException(this, CLASS_NAME, PretupsErrorCodesI.CCE_XML_ERROR_DOMAIN_NOTASSIGNED);
                    }
                    isChDomainCheckRequired = true;
                }
                if (isChDomainCheckRequired || isGeoDomainCheckRequired) {
                    if (isChDomainCheckRequired)// check domain
                    {
                        if (BTSLUtil.isNullString(BTSLUtil.getOptionDesc(transferVO.getDomainCode(), domainList).getValue())) {
                            if (!BTSLUtil.isNullString(_transactionId)) {
                                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CCE_ERROR_USER_NOTIN_DOMAIN);
                            } else if (!BTSLUtil.isNullString(msisdn)) {
                                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CCE_ERROR_SENDER_NOT_AUTHORIZE_DOMAIN);
                            }
                        }
                    }
                    if (isGeoDomainCheckRequired)// check geographical domain
                                                 // hierarchy
                    {
                        GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
                    	// code changes for bug fix (userid is passed instead of msisdn)
                        if (!geographicalDomainDAO.isGeoDomainExistInHierarchy(p_con, transferVO.getGraphicalDomainCode(), userid)) {
                            if (!BTSLUtil.isNullString(_transactionId)) {
                                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CCE_ERROR_USER_NOTIN_GEOGRAPHY);
                            } else if (!BTSLUtil.isNullString(msisdn)) {
                                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CCE_ERROR_SENDER_NOT_AUTHORIZE_GEOGRAPHY);
                            }
                        }
                    }
                }

            }
            _requestMap.put("RES_TYPE", "SUMMARY");

        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error(METHOD_NAME, "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CTransferEnquiryHandler[loadTransferSummary]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Exiting....");
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
    private ArrayList loadTransferDetails(Connection p_con, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadTransferDetails";
        if (log.isDebugEnabled()) {
            log.debug("loadTransferDetails", "Entered....");
        }
        ArrayList transferVOList = null;
        try {
            transferVOList = ChannelTransferBL.loadChannelTransferItemsWithBalances(p_con, p_channelTransferVO.getTransferID(), p_channelTransferVO.getNetworkCode(), p_channelTransferVO.getNetworkCodeFor(), p_channelTransferVO.getToUserID());
            if (!(transferVOList != null && transferVOList.size() > 0)) {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_ERROR_TRANSFER_DETAILS_NOT_FOUND);
            }
            _requestMap.put("RES_TYPE", "DETAILS");
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error("loadTransferDetails", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error(METHOD_NAME, "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CTransferEnquiryHandler[loadTransferDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Exiting....");
        }
        return transferVOList;
    }
}
