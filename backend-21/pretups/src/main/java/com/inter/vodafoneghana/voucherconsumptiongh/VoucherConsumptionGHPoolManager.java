package com.inter.vodafoneghana.voucherconsumptiongh;

/**
 * @(#)ZTEPoolManager.java
 *                         Copyright(c) 2009, Bharti Telesoft Int. Public Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Shamit June 27, 2009 Initial Creation
 *                         ----------------------------------------------------
 *                         -------------------------------------------
 *                         This class is responsible to instantiate the Client
 *                         object and maintain a pool.
 */
import netscape.ldap.LDAPConnection;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;

public class VoucherConsumptionGHPoolManager {

    static Log _logger = LogFactory.getLog(VoucherConsumptionGHPoolManager.class.getName());

    /**
     * 
     * @param p_arr
     * @return
     */
    public static boolean isNullArray(String[] p_arr) {
        if (_logger.isDebugEnabled())
            _logger.debug("isNullArray Entered ", " p_arr: " + p_arr);
        boolean isNull = true;
        if (p_arr != null) {
            for (int i = 0, j = p_arr.length; i < j; i++) {
                if (!BTSLUtil.isNullString(p_arr[i])) {
                    isNull = false;
                    break;
                }
            }
        }
        if (_logger.isDebugEnabled())
            _logger.debug("isNullArray Exited ", " isNull: " + isNull);
        return isNull;
    }

    /**
     * This method is responsible to store the instance of Client objects into a
     * HashMap with interface id as Key.
     * 
     * @param String
     *            p_interfaceIDs
     * @throws BTSLBaseException
     */
    public static void initialize(String p_interfaceIDs) throws BTSLBaseException {
        final String methodName = "VoucherConsumptionGHPoolManager[initialize]";
        if (_logger.isDebugEnabled())
            _logger.debug(methodName, "Entered p_interfaceIDs::" + p_interfaceIDs);
        String interfaceId = null;
        String[] inStrArray = null;
        try {

            inStrArray = p_interfaceIDs.split(",");

            if (isNullArray(inStrArray))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);

            for (int i = 0, size = inStrArray.length; i < size; i++) {
                // Create a list in which store the client object equal to
                // configured number.
                interfaceId = "";
                interfaceId = inStrArray[i].trim();
                try {

                    VoucherConsumptionGHPoolUtil consumptionGHPoolUtil = new VoucherConsumptionGHPoolUtil();
                    LDAPConnection ldapConnection = consumptionGHPoolUtil.getConnection(interfaceId);
                    consumptionGHPoolUtil.retunConnection(interfaceId, ldapConnection);
                } catch (Exception e) {
                    e.printStackTrace();
                    _logger.error(methodName, " Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", "While instantiation of the Client objects MAX_POOL_SIZE/POOL_SLEEP is either not defined or not numeric, in the INFile");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
                }
            }
        } catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException be:" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {

            _logger.error(methodName, "Exception e::" + e.getMessage());
            // Destroying the Client Objects from Hash table _freeBucket
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "String of interface ids=" + p_interfaceIDs, "", "", "While initializing the instance of Client Object for the INTERFACE_ID =" + interfaceId + " get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
        }// end of catch-Exception
        finally {
            if (_logger.isDebugEnabled())
                _logger.debug(methodName, " Exited _poolBucket::" + "");
        }// end of finally
    }// end of initialize

    /**
     * This method is responsible to store the instance of Client objects into a
     * HashMap with interface id as Key.
     * 
     * @param String
     *            p_interfaceIDs
     * @throws BTSLBaseException
     */
    public static void destroy(String p_interfaceIDs) {
        final String methodName = "VoucherConsumptionGHPoolManager[destroy]";
        if (_logger.isDebugEnabled())
            _logger.debug(methodName, " Enteredp_interfaceIDs::" + p_interfaceIDs);
        String interfaceId = null;
        String[] inStrArray = null;
        try {

            inStrArray = p_interfaceIDs.split(",");

            if (isNullArray(inStrArray))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);

            for (int i = 0, size = inStrArray.length; i < size; i++) {
                // Create a list in which store the client object equal to
                // configured number.
                interfaceId = "";
                interfaceId = inStrArray[i].trim();
                try {
                    VoucherConsumptionGHPoolUtil consumptionGHPoolUtil = new VoucherConsumptionGHPoolUtil();
                    consumptionGHPoolUtil.removeConnection(interfaceId);

                } catch (Exception e) {
                    e.printStackTrace();
                    _logger.error(methodName, " Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", "While instantiation of the Client objects MAX_POOL_SIZE/POOL_SLEEP is either not defined or not numeric, in the INFile");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
                }
            }
        } catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException be:" + be.getMessage());
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _logger.error(methodName, "Exception e::" + e.getMessage());
            // Destroying the Client Objects from Hash table _freeBucket
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "String of interface ids=" + p_interfaceIDs, "", "", "While initializing the instance of Client Object for the INTERFACE_ID =" + interfaceId + " get Exception=" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_logger.isDebugEnabled())
                _logger.debug(methodName, " Exited _poolBucket::" + "");
        }// end of finally
    }// end of initialize
}
