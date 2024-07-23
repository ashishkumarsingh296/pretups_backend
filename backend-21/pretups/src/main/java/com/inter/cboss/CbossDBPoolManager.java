/*
 * Created on Jul 24, 2007
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.cboss;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.inter.pool.ClientMarkerI;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class CbossDBPoolManager {
    private Log _log = LogFactory.getLog(CbossDBPoolManager.class.getName());
    public static HashMap _dbUtilityObjectMap = new HashMap();

    public void initialize(String p_interfaceIDs) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("initialize", "Entered p_interfaceIDs::" + p_interfaceIDs);
        String interfaceId = null;
        Object object = null;
        String[] inStrArray = null;
        try {
            inStrArray = p_interfaceIDs.split(",");
            if (InterfaceUtil.isNullArray(inStrArray))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
            for (int i = 0, size = inStrArray.length; i < size; i++) {
                interfaceId = inStrArray[i].trim();
                object = getNewDBUtilityObj(interfaceId);
                _dbUtilityObjectMap.put(interfaceId, object);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("initialize", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CbossDBPoolManager[initialize]", "INTERFACEID:" + interfaceId, "", "", "While instantiation of CbossDBUTility instance of CbossDBUtility objects got the Exception " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("initialize", "Exited _dbUtilityObjectMap:" + _dbUtilityObjectMap);
        }
    }

    /**
     * This method implements the logic to create and instance of DBUtility
     * Objects.
     * 
     * @param String
     *            p_interfaceID
     * @return Object
     * @throws BTSLBaseException
     */
    public Object getNewDBUtilityObj(String p_interfaceID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getNewDBUtilityObj", "p_interfaceID::" + p_interfaceID);
        Object dbUtilityObject = null;
        String dbUtilityClass = null;
        Class client = null;
        Class[] argTypeClass = null;
        Constructor stringArgConstructor = null;
        try {
            // Get the full name of db utility handler class from the INFile
            dbUtilityClass = FileCache.getValue(p_interfaceID, "CBOSS_DBUTILITY_HANDLER_CLASS");
            if (_log.isDebugEnabled())
                _log.debug("getNewDBUtilityObj", "dbUtilityClass::" + dbUtilityClass);
            // Check for the Null value of clientHandlerClass and handle the
            // event
            if (InterfaceUtil.isNullString(dbUtilityClass))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_GETTIN_NEW_CLIENT_OBJECT);
            try {
                client = Class.forName(dbUtilityClass.trim());
                argTypeClass = new Class[] { String.class };
                stringArgConstructor = client.getConstructor(argTypeClass);
                dbUtilityObject = stringArgConstructor.newInstance(new String[] { p_interfaceID });
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("getNewDBUtilityObj", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CbossDBPoolManager[getNewDBUtilityObj]", "INTERFACEID:" + p_interfaceID, "", "", "While getting the instance of Client objects got the Exception " + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_GETTIN_NEW_CLIENT_OBJECT);// Confirm
                                                                                                 // for
                                                                                                 // the
                                                                                                 // New
                                                                                                 // Error
                                                                                                 // Code,
                                                                                                 // if
                                                                                                 // required.
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getNewDBUtilityObj", "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CbossDBPoolManager[getNewDBUtilityObj]", "INTERFACEID:" + p_interfaceID, "", "", "While getting the instance of DBUtility objects got the Exception " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_GETTIN_NEW_CLIENT_OBJECT);// Confirm
                                                                                             // for
                                                                                             // the
                                                                                             // new
                                                                                             // InterfaceErrorCode
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getNewDBUtilityObj", "Exiting dbUtilityObject:" + dbUtilityObject);
        }
        return dbUtilityObject;
    }

    /**
     * This method is used to destroy all the Objects stored in the Map
     * corresponding to the interfaceId
     * 
     * @param String
     *            p_interfaceId
     */
    public void destroy(String p_interfaceId) {
        if (_log.isDebugEnabled())
            _log.debug("destroy", "Entered p_interfaceId:" + p_interfaceId);
        try {
            Set ketSetCode = null;
            Iterator iter = null;
            String key = null;
            if (InterfaceUtil.isNullString(p_interfaceId))
                throw new Exception(" No Pool Id for destroying");
            String strINId = null;
            StringTokenizer strTokens = new StringTokenizer(p_interfaceId, ",");
            if (_dbUtilityObjectMap != null) {
                while (strTokens.hasMoreElements()) {
                    strINId = strTokens.nextToken().trim();
                    ketSetCode = _dbUtilityObjectMap.keySet();
                    iter = ketSetCode.iterator();
                    _log.info("destroy", "Destroying pool of client objects for Interface ID=" + strINId);
                    while (iter.hasNext()) {
                        key = (String) iter.next();
                        if (strINId.equals(key.trim())) {
                            _log.info("destroy", "Destroying Objects from _dbUtilityObjectMap for Interface ID=" + key);
                            ClientMarkerI dbUtilityObj = null;
                            try {
                                dbUtilityObj = (ClientMarkerI) _dbUtilityObjectMap.get(strINId);
                                dbUtilityObj.destroy();
                            } catch (Exception e) {
                                dbUtilityObj = null;
                            }
                        }// end of if
                    }// end of innerwhile.
                }// end of outer while
            }// end of if
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("destroy", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CbossDBPoolManager[destroy]", "", "", "", "While destorying the Client objects got the Exception " + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("destroy", "Exiting");
        }// end of finally
    }// end of destroy

    /**
     * This method is used to destoy the ALL ClientObject references from
     * freeBucket and busyBucket.
     * 
     * @param String
     *            p_interfaceId
     * @param String
     *            p_all
     * @return void
     */
    public void destroy(String p_interfaceId, String p_all) {
        if (_log.isDebugEnabled())
            _log.debug("destroy", "Entered p_interfaceId:" + p_interfaceId + " p_all:" + p_all);
        try {
            Set ketSetCode = null;
            Iterator iter = null;
            String key = null;
            if (InterfaceUtil.isNullString(p_interfaceId))
                throw new Exception(" No Pool Id for destroying");
            String strINId = null;
            StringTokenizer strTokens = new StringTokenizer(p_interfaceId, ",");
            if (_dbUtilityObjectMap != null) {
                while (strTokens.hasMoreElements()) {
                    strINId = strTokens.nextToken().trim();
                    ketSetCode = _dbUtilityObjectMap.keySet();
                    iter = ketSetCode.iterator();
                    _log.info("destroy", "Destroying _dbUtilityObjectMap,object for Interface ID=" + strINId);
                    while (iter.hasNext()) {
                        key = (String) iter.next();
                        if (strINId.equals(key.trim())) {
                            _log.info("destroy", "Destroying ClientObjects from _freeBucket for Interface ID=" + key);
                            ClientMarkerI dbUtilityObj = null;
                            try {
                                dbUtilityObj = (ClientMarkerI) _dbUtilityObjectMap.get(strINId);
                                dbUtilityObj.destroy();
                            } catch (Exception e) {
                                dbUtilityObj = null;
                            }
                        }// end of if
                    }// end of innerwhile.
                }// end of outer while
            }// end of if
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("destroy", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CbossDBPoolManager[destroy]", "", "", "", "While destorying the Client objects got the Exception " + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("destroy", "Exiting");
        }// end of finally
    }// end of destroy
}
