/*
 * Created on Jul 24, 2007
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.inter.module;

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
import com.inter.pool.ClientMarkerI;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class HandlerUtilityManager {
    private Log _log = LogFactory.getLog(HandlerUtilityManager.class.getName());
    public static HashMap _utilityObjectMap = new HashMap();

    public void initialize(String p_ids, String p_commonUtility) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("initialize", "Entered p_ids::" + p_ids + " p_commonUtility::" + p_commonUtility);
        String utilityId = null;
        Object object = null;
        String[] inStrArray = null;
        try {
            inStrArray = p_ids.split(",");
            if (InterfaceUtil.isNullArray(inStrArray))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
            utilityId = inStrArray[0].trim();
            if ("Y".equals(p_commonUtility)) {
                utilityId = inStrArray[0].trim();
                object = getNewUtilityObj(utilityId);
                _utilityObjectMap.put(utilityId, object);
                for (int i = 1, size = inStrArray.length; i < size; i++) {
                    utilityId = inStrArray[i].trim();
                    _utilityObjectMap.put(utilityId, object);
                }
            } else {
                for (int i = 0, size = inStrArray.length; i < size; i++) {
                    utilityId = inStrArray[i].trim();
                    object = getNewUtilityObj(utilityId);
                    _utilityObjectMap.put(utilityId, object);
                }
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("initialize", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HandlerUtilityManager[initialize]", "utilityId:" + utilityId, "", "", "While instantiation of HandlerCommonUTility object got the Exception " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("initialize", "Exited _utilityObjectMap:" + _utilityObjectMap);
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
    public Object getNewUtilityObj(String p_id) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getNewDBUtilityObj", "p_id::" + p_id);
        Object utilityObject = null;
        String utilityClass = null;
        Class client = null;
        Class[] argTypeClass = null;
        Constructor stringArgConstructor = null;
        try {
            // Get the full name of db utility handler class from the INFile
            utilityClass = FileCache.getValue(p_id, "COMMON_HANDLER_UTILITY_CLASS");
            // utilityClass =
            // FileCache.getValue(p_id,"CBOSS_DBUTILITY_HANDLER_CLASS");
            if (_log.isDebugEnabled())
                _log.debug("getNewDBUtilityObj", "utilityClass::" + utilityClass);
            // Check for the Null value of clientHandlerClass and handle the
            // event
            if (InterfaceUtil.isNullString(utilityClass))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_GETTIN_NEW_CLIENT_OBJECT);
            try {
                client = Class.forName(utilityClass.trim());
                argTypeClass = new Class[] { String.class };
                stringArgConstructor = client.getConstructor(argTypeClass);
                utilityObject = stringArgConstructor.newInstance(new String[] { p_id });
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("getNewDBUtilityObj", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HandlerUtilityManager[getNewDBUtilityObj]", "p_id:" + p_id, "", "", "While getting the instance of Client objects got the Exception " + e.getMessage());
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
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getNewDBUtilityObj", "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "SubscriberTypePoolManager[getNewDBUtilityObj]", "p_id:" + p_id, "", "", "While getting the instance of SubscriberTypeDBUtility objects got the Exception " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_GETTIN_NEW_CLIENT_OBJECT);// Confirm
                                                                                             // for
                                                                                             // the
                                                                                             // new
                                                                                             // InterfaceErrorCode
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getNewDBUtilityObj", "Exiting dbUtilityObject:" + utilityObject);
        }
        return utilityObject;
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
                throw new BTSLBaseException(" No Pool Id for destroying");
            String strINId = null;
            StringTokenizer strTokens = new StringTokenizer(p_interfaceId, ",");
            if (_utilityObjectMap != null) {
                while (strTokens.hasMoreElements()) {
                    strINId = strTokens.nextToken().trim();
                    ketSetCode = _utilityObjectMap.keySet();
                    iter = ketSetCode.iterator();
                    _log.info("destroy", "Destroying pool of client objects for Interface ID=" + strINId);
                    while (iter.hasNext()) {
                        key = (String) iter.next();
                        if (strINId.equals(key.trim())) {
                            _log.info("destroy", "Destroying Objects from _dbUtilityObjectMap for Interface ID=" + key);
                            ClientMarkerI utilityObj = null;
                            try {
                                utilityObj = (ClientMarkerI) _utilityObjectMap.get(strINId);
                                utilityObj.destroy();
                            } catch (Exception e) {
                                utilityObj = null;
                            }
                        }// end of if
                    }// end of innerwhile.
                }// end of outer while
            }// end of if
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("destroy", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HandlerUtilityManager[destroy]", "", "", "", "While destorying the Client objects got the Exception " + e.getMessage());
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
                throw new BTSLBaseException(" No Pool Id for destroying");
            String strINId = null;
            StringTokenizer strTokens = new StringTokenizer(p_interfaceId, ",");
            if (_utilityObjectMap != null) {
                while (strTokens.hasMoreElements()) {
                    strINId = strTokens.nextToken().trim();
                    ketSetCode = _utilityObjectMap.keySet();
                    iter = ketSetCode.iterator();
                    _log.info("destroy", "Destroying _dbUtilityObjectMap,object for Interface ID=" + strINId);
                    while (iter.hasNext()) {
                        key = (String) iter.next();
                        if (strINId.equals(key.trim())) {
                            _log.info("destroy", "Destroying ClientObjects from _freeBucket for Interface ID=" + key);
                            ClientMarkerI utilityObj = null;
                            try {
                                utilityObj = (ClientMarkerI) _utilityObjectMap.get(strINId);
                                utilityObj.destroy();
                            } catch (Exception e) {
                                utilityObj = null;
                            }
                        }// end of if
                    }// end of innerwhile.
                }// end of outer while
            }// end of if
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("destroy", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HandlerUtilityManager[destroy]", "", "", "", "While destorying the Client objects got the Exception " + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("destroy", "Exiting");
        }// end of finally
    }// end of destroy
}
