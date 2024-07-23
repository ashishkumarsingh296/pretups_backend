/**
 * @(#)GeographyController.java
 *                              Copyright(c) 2009, Comviva Technologies Ltd.
 *                              All Rights Reserved
 * 
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Ankur Dhawan Initial Creation
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              The Controller will be responsible for
 *                              processing the request of Geography API and
 *                              return in response the
 *                              list of available geographies up to next level.
 * 
 */

package com.btsl.pretups.requesthandler;

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
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.util.BTSLUtil;
import com.txn.pretups.domain.businesslogic.CategoryTxnDAO;
import com.txn.pretups.master.businesslogic.GeographicalDomainTxnDAO;

public class GeographyController implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(GeographyController.class.getName());

    private HashMap<String, Object> _requestMap = null;
    private RequestVO _requestVO = null;

    /**
     * Method to process the external geography API request
     * 
     * @author ankur.dhawan
     * @param p_requestVO
     * @return
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("GeographyController[process]", "Entered ");
        }

        Connection con = null;MComConnectionI mcomCon = null;
        // CategoryDAO catDAO=new CategoryDAO();
        CategoryTxnDAO categoryTxnDAO = new CategoryTxnDAO();
        GeographicalDomainDAO geoDAO = new GeographicalDomainDAO();
        CategoryVO catVO = CategoryVO.getInstance();
        _requestVO = p_requestVO;
        _requestMap = _requestVO.getRequestMap();

        String catCode = (String) _requestMap.get("CATCODE");
        String geoCode = (String) _requestMap.get("GEOCODE");
        String msisdn = (String) _requestMap.get("MSISDN");
        String loginId = (String) _requestMap.get("LOGINID");
        String extCode = (String) _requestMap.get("EXTCODE");

        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
            catVO = categoryTxnDAO.isValidCategoryCode(con, catCode);
            if (BTSLUtil.isNullString(catVO.getDomainCodeforCategory()) || catVO.getDomainCodeforCategory().equals(PretupsI.OPERATOR_TYPE_OPT) ) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXT_GRPH_INVALID_CATEGORY);
            }

            if (catVO.getSequenceNumber() == 1 && (!BTSLUtil.isNullString(msisdn) || !BTSLUtil.isNullString(loginId) || !BTSLUtil.isNullString(extCode))) {
                String strarr[] = { catCode };
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXT_GRPH_HIERARCHY_ERROR, 0, strarr, null);
            } else if (catVO.getSequenceNumber() != 1 && geoCode.equals(PretupsI.PARENT_GEOGRAPHY_ROOT)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY);
            }

            // Owner user's parent is ROOT thus msisdn is invalid
            if (!BTSLUtil.isNullString(geoCode) && catVO.getSequenceNumber() != 1) {
                if (!geoDAO.isGeographicalDomainExist(con, geoCode, true)) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY);
                }
            }

            if (catVO.getSequenceNumber() == 1 && !geoCode.equals(PretupsI.PARENT_GEOGRAPHY_ROOT)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY);
            }

            // set parent geography as ROOT
            if (catVO.getSequenceNumber() == 1) {
                geoCode = PretupsI.PARENT_GEOGRAPHY_ROOT;
            }

            getgeographyCodeMap(con, catCode, msisdn, geoCode);

            _requestVO.setSuccessTxn(true);
            _requestVO.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _requestVO.setRequestMap(_requestMap);
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
        } catch (Exception e) {
            _requestVO.setSuccessTxn(false);
            _log.error("process", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographyController[process]", "", "", "", "Exception:" + e.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            _requestVO.setRequestMap(_requestMap);
            p_requestVO = _requestVO;
            // setting the variable to null for efficient garbage collection
            _requestMap = null;
            _requestVO = null;
			if (mcomCon != null) {
				mcomCon.close("GeographyController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("GeographyController[process]", "Exiting!! ");
        }
    }

    /**
     * Method to get the geography map
     * 
     * @author ankur.dhawan
     * @param p_categoryCode
     * @param p_msisdn
     * @param p_geoCode
     * @return
     */
    private void getgeographyCodeMap(Connection p_con, String p_categoryCode, String p_msisdn, String p_geoCode) throws BTSLBaseException {
        final String METHOD_NAME = "getgeographyCodeMap";
        if (_log.isDebugEnabled()) {
            _log.debug("GeographyController[getgeographyCodeMap]", "Entered ");
        }

        GeographicalDomainTxnDAO geographicalDomainTxnDAO = new GeographicalDomainTxnDAO();
        UserGeographiesDAO UserGeographiesDAO = new UserGeographiesDAO();
        ArrayList geographyList = null;

        try {
            if (p_geoCode.equals(PretupsI.PARENT_GEOGRAPHY_ROOT)) {
                geographyList = geographicalDomainTxnDAO.loadGeographiesForOwner(p_con, p_categoryCode);
                _requestMap.put(PretupsI.GEOGRAPHY_LIST, geographyList);
            } else if (BTSLUtil.isNullString(p_msisdn)) {
                if (!BTSLUtil.isNullString(p_geoCode)) {
                    geographyList = geographicalDomainTxnDAO.loadGeographiesForAPI(p_con, p_geoCode, p_categoryCode);
                }

                _requestMap.put(PretupsI.GEOGRAPHY_LIST, geographyList);
            } else {
                if (!BTSLUtil.isNullString(p_geoCode)) {
                    if (!UserGeographiesDAO.validateGeographyOfParent(p_con, p_geoCode, p_msisdn)) {
                        throw new BTSLBaseException(this, "getgeographyCodeMap", PretupsErrorCodesI.EXT_GRPH_DETAILS_MISMATCH);
                    }
                }

                geographyList = geographicalDomainTxnDAO.loadGeographiesForAPIByParent(p_con, p_msisdn, p_categoryCode);
                _requestMap.put(PretupsI.GEOGRAPHY_LIST, geographyList);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("getgeographyCodeMap", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("getgeographyCodeMap", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographyController[getgeographyCodeMap]", "", "", "", "Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("GeographyController", "getgeographyCodeMap", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("GeographyController[getgeographyCodeMap]", "Exiting!! ");
            }
        }
    }
}
