package com.btsl.pretups.requesthandler;

/**
 * @(#)TransferRuleTypeController.java
 *                                     Name Date History
 *                                     ----------------------------------------
 *                                     --------------------------------
 *                                     Gurjeet Singh Bedi 05/07/2005 Initial
 *                                     Creation
 *                                     Abhijit Aug 10,2006 Modification for
 *                                     ID=SUBTYPVALRECLMT
 *                                     Ankit Zindal Nov 20,2006
 *                                     ChangeID=LOCALEMASTER
 *                                     Divyakant Verma Feb 07 2008
 *                                     ChannelRequestDailyLog introduced to log
 *                                     time taken by IN for validation & topup.
 *                                     ----------------------------------------
 *                                     --------------------------------
 *                                     Copyright (c) 2005 Bharti Telesoft Ltd.
 *                                     Controller class for handling the Channel
 *                                     to subscriber transfers
 */

import java.sql.Connection;
import java.util.ArrayList;
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
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.txn.pretups.channel.transfer.businesslogic.ChannelTransferTxnDAO;
import com.txn.pretups.domain.businesslogic.CategoryTxnDAO;

public class TransferRuleTypeController implements ServiceKeywordControllerI {
    private Log log = LogFactory.getLog(TransferRuleTypeController.class.getName());
    private static  final String CATCODE_STR = "CATCODE";
    private static final String SUCCESS_RES_TYPE_STR = "EXTTRFRULETYPERESP";
    private HashMap<String, Object> _requestMap = null;
    private RequestVO _requestVO = null;

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (log.isDebugEnabled()) {
            log.debug("process", "Entered....: p_requestVO= " + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        ChannelUserVO chanelUserVO = null;
        CategoryVO categoryVO = null;
        String categoryCode = null;
        String networkCode = null;
        _requestVO = p_requestVO;
        boolean ruleExists = false;
        try {
            _requestMap = _requestVO.getRequestMap();
            CategoryTxnDAO catDAO = new CategoryTxnDAO();
            categoryCode = (String) _requestMap.get(CATCODE_STR);
            networkCode = (String) _requestMap.get("EXTNWCODE");
            if (_requestVO.getSenderVO() != null) {
                chanelUserVO = (ChannelUserVO) _requestVO.getSenderVO();
            }
            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // Check for Category Code Valid
            categoryVO = catDAO.isValidCategoryCode(con, categoryCode);
            if ((BTSLUtil.isNullString(categoryVO.getDomainCodeforCategory()) || categoryVO.getDomainCodeforCategory().equals(PretupsI.OPERATOR_TYPE_OPT))) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXT_GRPH_INVALID_CATEGORY);
            }

            if ((chanelUserVO != null) && categoryVO.getSequenceNumber() == 1) {
                String strarr[] = { categoryCode };
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXT_GRPH_HIERARCHY_ERROR, 0, strarr, null);
            }
            // this call validates all the passed parameters of the request

            if (chanelUserVO == null && categoryVO.getSequenceNumber() > 1)// added
                                                                           // for
                                                                           // bug
                                                                           // removal
            {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXT_USRADD_PARENT_MISSING);
            }

            // Check for Parent User can create child in this category if check
            if (chanelUserVO != null && categoryVO.getSequenceNumber() > 1) {
                ruleExists = new ChannelTransferTxnDAO().isC2SRulesListForChannelUserAssociation(con, networkCode, chanelUserVO.getCategoryCode(), categoryCode);
                if (!ruleExists) {
                    String strarr[] = { categoryCode };
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXT_GRPH_HIERARCHY_ERROR, 0, strarr, null);
                }
            } else if (chanelUserVO == null && categoryVO.getSequenceNumber() == 1) {
                ruleExists = true;
            }
            if (ruleExists) {
                getTransferRuleTypeMap();
            }
            _requestVO.setSuccessTxn(true);
            _requestVO.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _requestMap.put("RES_TYPE", SUCCESS_RES_TYPE_STR);
            p_requestVO.setRequestMap(_requestMap);
        } catch (BTSLBaseException be) {
            log.error("process", "BTSLBaseException " + be.getMessage());
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
                log.errorTrace(METHOD_NAME, e);
            }
            log.errorTrace(METHOD_NAME, be);
        } catch (Exception ex) {
            _requestVO.setSuccessTxn(false);
            // Rollbacking the connection
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                log.errorTrace(METHOD_NAME, ee);
            }
            log.error("process", "Exception " + ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRuleTypeController[process]", "", "", "", "Exception:" + ex.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            log.errorTrace(METHOD_NAME, ex);
        }// end of Exception
        finally {
            _requestVO.setRequestMap(_requestMap);
            p_requestVO = _requestVO;
            // setting the variable to null for efficient garbage collection
            _requestMap = null;
            _requestVO = null;
			if (mcomCon != null) {
				mcomCon.close("TransferRuleTypeController#process");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug("process", " Exited ");
            }
        }// end of finally
    }

    /**
     * 
     * @throws BTSLBaseException
     */
    private void getTransferRuleTypeMap() throws BTSLBaseException {
        final String METHOD_NAME = "getTransferRuleTypeMap";
        if (log.isDebugEnabled()) {
            log.debug("getTransferRuleTypeMap", "Entered.... ");
        }

        ArrayList tfrRuleTypeList = null;
        try {
            tfrRuleTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true);
            if (tfrRuleTypeList.size() <= 0) {
                throw new BTSLBaseException("TransferRuleTypeController", "getTransferRuleTypeMap", PretupsErrorCodesI.EXT_TRF_RULE_TYPE_NOT_FOUND);
            }
            _requestMap.put(PretupsI.RULETYPEDETAILS_STR, tfrRuleTypeList);
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error("getTransferRuleTypeMap", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error("getTransferRuleTypeMap", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRuleTypeController[getTransferRuleTypeMap]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("TransferRuleTypeController", "getTransferRuleTypeMap", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug("getTransferRuleTypeMap", "Exiting....");
        }
    }
}
