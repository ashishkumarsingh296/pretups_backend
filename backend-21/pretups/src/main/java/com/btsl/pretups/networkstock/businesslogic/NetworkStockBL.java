/**
 * @(#)NetworkStockBL.java
 *                         Copyright(c) 2005, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 * 
 *                         <description>
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         avinash.kamthan Aug 13, 2005 Initital Creation
 *                         Sandeep Goel Dec 27/12/2005 Modification &
 *                         Customization
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 * 
 */

package com.btsl.pretups.networkstock.businesslogic;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.restapi.networkadmin.networkStock.NetworkStockTxnVO1;

/**
 * @author avinash.kamthan
 * 
 */
public class NetworkStockBL {
    /**
     * Field _log.
     */
    private static Log _log = LogFactory.getLog(NetworkStockBL.class.getName());
    /**
     * Field _operatorUtil.
     */
    private static int _networkStockTxnIDCounter = 0;
    private static int _prevMinut = 0;
    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
    private static OperatorUtilI calculatorI = null;
    // calculate the tax
    static {
        String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockBL[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
	 * ensures no instantiation
	 */
    private NetworkStockBL(){
    	
    }
    
    
    /**
     * Method genrateStockTransctionID.
     * 
     * @param p_currentDate
     *            Date
     * @param p_networkCode
     *            String
     * @return String
     * @throws BTSLBaseException
     */
    public static String genrateStockTransctionID(NetworkStockTxnVO p_networkStockTxnVO) throws BTSLBaseException {

        // changes added on 28-01-2015 to change format of Network Stock Txn Id
        String minut2Compare = null;
        Date mydate = null;
        final String methodName = "genrateStockTransctionID";
        if (_log.isDebugEnabled()) {
            _log.debug("genrateStockTransctionID", "Entered ");
        }
        String uniqueID = null;
        try {
            mydate = new Date();
            p_networkStockTxnVO.setCreatedOn(mydate);
            minut2Compare = _sdfCompare.format(mydate);
            int currentMinut = Integer.parseInt(minut2Compare);

            if (currentMinut != _prevMinut) {
                _networkStockTxnIDCounter = 1;
                _prevMinut = currentMinut;
            } else if (_networkStockTxnIDCounter >= 65535) {
                _networkStockTxnIDCounter = 1;
            } else {
                _networkStockTxnIDCounter++;
            }
            if (_networkStockTxnIDCounter == 0) {
                throw new BTSLBaseException("NetworkStockBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            uniqueID = calculatorI.formatNetworkStockTxnID(p_networkStockTxnVO, _networkStockTxnIDCounter);
        }

        catch (Exception e) {
            _log.error("genrateStockTransctionID", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"NetworkStockBL[genrateStockTransctionID]","","","","Exception:"+e.getMessage());
            throw new BTSLBaseException("NetworkStockBL", "genrateStockTransctionID", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("genrateStockTransctionID", "Exited  " + uniqueID);
            }
        }
        return uniqueID;
        // changes ended
        /*
         * if (_log.isDebugEnabled())
         * _log.debug("genrateStockTransctionID", "Entered ");
         * String uniqueID = null;
         * try
         * {
         * //long interfaceId =
         * IDGenerator.getNextID(PretupsI.NETWORK_STOCK_TRANSACTION_ID,
         * BTSLUtil.getFinancialYear(),p_networkStockTxnVO.getNetworkCode(),
         * p_networkStockTxnVO.getCreatedOn());
         * long interfaceId =
         * IDGenerator.getNextID(PretupsI.NETWORK_STOCK_TRANSACTION_ID,
         * BTSLUtil.getFinancialYear(), p_networkStockTxnVO);
         * uniqueID
         * =calculatorI.formatNetworkStockTxnID(p_networkStockTxnVO,interfaceId
         * );
         * }
         * catch (Exception e)
         * {
         * _log.error("genrateStockTransctionID", "Exception " +
         * e.getMessage());
         * e.printStackTrace();
         * //EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
         * EventStatusI
         * .RAISED,EventLevelI.FATAL,"NetworkStockBL[genrateStockTransctionID]"
         * ,"","","","Exception:"+e.getMessage());
         * throw new BTSLBaseException("NetworkStockBL",
         * "genrateStockTransctionID", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
         * }
         * finally
         * {
         * if (_log.isDebugEnabled())
         * _log.debug("genrateStockTransctionID", "Exited  "+uniqueID);
         * }
         * return uniqueID;
         */
    }

    /**
     * Method genrateStockTransctionID.
     * 
     * @param p_currentDate
     *            Date
     * @param p_networkCode
     *            String
     * @return String
     * @throws BTSLBaseException
     */
    public static String genrateStockTransctionID(Connection p_con, NetworkStockTxnVO p_networkStockTxnVO) throws BTSLBaseException {

        // changes added on 28-01-2015 to change format of Network Stock Txn Id
        String minut2Compare = null;
        Date mydate = null;
        final String methodName = "genrateStockTransctionID";
        if (_log.isDebugEnabled()) {
            _log.debug("genrateStockTransctionID", "Entered ");
        }
        String uniqueID = null;
        try {
            mydate = new Date();
            p_networkStockTxnVO.setCreatedOn(mydate);
            minut2Compare = _sdfCompare.format(mydate);
            int currentMinut = Integer.parseInt(minut2Compare);

            if (currentMinut != _prevMinut) {
                _networkStockTxnIDCounter = 1;
                _prevMinut = currentMinut;
            } else if (_networkStockTxnIDCounter >= 65535) {
                _networkStockTxnIDCounter = 1;
            } else {
                _networkStockTxnIDCounter++;
            }
            if (_networkStockTxnIDCounter == 0) {
                throw new BTSLBaseException("NetworkStockBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            uniqueID = calculatorI.formatNetworkStockTxnID(p_networkStockTxnVO, _networkStockTxnIDCounter);
        }

        catch (Exception e) {
            _log.error("genrateStockTransctionID", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"NetworkStockBL[genrateStockTransctionID]","","","","Exception:"+e.getMessage());
            throw new BTSLBaseException("NetworkStockBL", "genrateStockTransctionID", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("genrateStockTransctionID", "Exited  " + uniqueID);
            }
        }
        return uniqueID;
        // changes ended
        /*
         * if (_log.isDebugEnabled())
         * _log.debug("genrateStockTransctionID", "Entered ");
         * String uniqueID = null;
         * try
         * { //Change done for C2C Issue, when sender was debited and receiver
         * is not credited in case when some exception occurs during
         * CHANNEL_TRANSFERS insert.
         * //long interfaceId =
         * IDGenerator.getNextID(p_con,PretupsI.NETWORK_STOCK_TRANSACTION_ID,
         * BTSLUtil.getFinancialYear(),p_networkStockTxnVO.getNetworkCode(),
         * p_networkStockTxnVO.getCreatedOn());
         * //long interfaceId =
         * IDGenerator.getNextID(PretupsI.NETWORK_STOCK_TRANSACTION_ID,
         * BTSLUtil.getFinancialYear(),p_networkStockTxnVO.getNetworkCode(),
         * p_networkStockTxnVO.getCreatedOn());
         * long interfaceId =
         * IDGenerator.getNextID(PretupsI.NETWORK_STOCK_TRANSACTION_ID,
         * BTSLUtil.getFinancialYear(), p_networkStockTxnVO);
         * uniqueID
         * =calculatorI.formatNetworkStockTxnID(p_networkStockTxnVO,interfaceId
         * );
         * }
         * catch (Exception e)
         * {
         * _log.error("genrateStockTransctionID", "Exception " +
         * e.getMessage());
         * e.printStackTrace();
         * //EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
         * EventStatusI
         * .RAISED,EventLevelI.FATAL,"NetworkStockBL[genrateStockTransctionID]"
         * ,"","","","Exception:"+e.getMessage());
         * throw new BTSLBaseException("NetworkStockBL",
         * "genrateStockTransctionID", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
         * }
         * finally
         * {
         * if (_log.isDebugEnabled())
         * _log.debug("genrateStockTransctionID", "Exited  "+uniqueID);
         * }
         * return uniqueID;
         */
    }
    public static String genrateStockTransctionID1(NetworkStockTxnVO1 p_networkStockTxnVO) throws BTSLBaseException {
        String minut2Compare = null;
        Date mydate = null;
        final String methodName = "genrateStockTransctionID1";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        String uniqueID = null;
        try {
            mydate = new Date();
            p_networkStockTxnVO.setCreatedOn(mydate);
            minut2Compare = _sdfCompare.format(mydate);
            int currentMinut = Integer.parseInt(minut2Compare);

            if (currentMinut != _prevMinut) {
                _networkStockTxnIDCounter = 1;
                _prevMinut = currentMinut;
            } else if (_networkStockTxnIDCounter >= 65535) {
                _networkStockTxnIDCounter = 1;
            } else {
                _networkStockTxnIDCounter++;
            }
            if (_networkStockTxnIDCounter == 0) {
                throw new BTSLBaseException("NetworkStockBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            uniqueID = calculatorI.formatNetworkStockTxnID1(p_networkStockTxnVO, _networkStockTxnIDCounter);
        }

        catch (Exception e) {
            throw new BTSLBaseException("NetworkStockBL", "genrateStockTransctionID", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("genrateStockTransctionID", "Exited  " + uniqueID);
            }
        }
        return uniqueID;

    }
}
