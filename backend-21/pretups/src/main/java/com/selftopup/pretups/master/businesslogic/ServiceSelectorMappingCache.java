package com.selftopup.pretups.master.businesslogic;

/*
 * ServiceSelectorMappingCache.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/05/2007 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2007 Bharti Telesoft Ltd.
 * Service Type Selector Mapping Cache
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;

public class ServiceSelectorMappingCache {
    private static Log _log = LogFactory.getLog(ServiceSelectorMappingCache.class.getName());
    private static ArrayList _serviceSelectorList = new ArrayList();
    private static HashMap _serviceTypeSelectorMasterMap = new HashMap();

    public static void loadServiceSelectorMappingCacheOnStartUp() {

        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceSelectorMappingCacheOnStartUp()", "entered");
        }
        getServiceSelectorMapping();

        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceSelectorMappingCacheOnStartUp()", "exited");
        }
    }

    /**
     * Load the cache for the Service type and selectors
     * 
     * @return
     */
    private static void getServiceSelectorMapping() {
        if (_log.isDebugEnabled())
            _log.debug("getServiceSelectorMapping", "entered");

        ServiceSelectorMappingDAO serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();
        try {
            _serviceSelectorList = serviceSelectorMappingDAO.loadServiceSelectorCache();
            _serviceTypeSelectorMasterMap = serviceSelectorMappingDAO.loadServiceTypeSelectorMap();

        } catch (Exception e) {
            _log.error("getServiceSelectorMapping", "Exception: " + e.getMessage());
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("getServiceSelectorMapping", "exited with _serviceSelectorList size=" + _serviceSelectorList.size());
    }

    /**
     * 
     * @return ArrayList
     *         * @author pankaj.namdev
     */
    public static ArrayList loadSelectorDropDownForTrfRule() {
        if (_log.isDebugEnabled())
            _log.debug("loadSelectorDropDown()", "");
        ArrayList lookupList = new ArrayList();
        try {
            ArrayList list = getServiceSelectorList();

            for (int i = 0; i < list.size(); i++) {
                ServiceSelectorMappingVO serviceSelectorMappingVO = (ServiceSelectorMappingVO) list.get(i);

                ListValueVO listValueVO = new ListValueVO(serviceSelectorMappingVO.getSelectorName(), serviceSelectorMappingVO.getSenderSubscriberType() + ":" + serviceSelectorMappingVO.getReceiverSubscriberType() + ":" + serviceSelectorMappingVO.getSelectorCode() + ":" + serviceSelectorMappingVO.getServiceType());
                // listValueVO.setOtherInfo(lookupsVO.getLookupType());
                lookupList.add(listValueVO);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadLookupDropDown()", "exited" + lookupList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lookupList;
    }

    /**
     * @return ArrayList
     * @author shishupal.singh
     */
    public static ArrayList loadSelectorDropDownForCardGroup() {
        if (_log.isDebugEnabled())
            _log.debug("loadSelectorDropDownForCardGroup()", "");
        ArrayList lookupList = new ArrayList();
        try {
            ListValueVO listVO = null;
            String str = "";
            ServiceSelectorMappingVO vo = null;
            HashMap hm = getServiceSelectorMap();
            Iterator itr = getServiceSelectorMap().keySet().iterator();
            while (itr.hasNext()) {
                str = (String) itr.next();
                vo = (ServiceSelectorMappingVO) hm.get(str);
                listVO = new ListValueVO(vo.getSelectorName(), vo.getServiceType() + ":" + vo.getSelectorCode());
                lookupList.add(listVO);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadSelectorDropDownForCardGroup()", "exited" + lookupList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lookupList;
    }

    /**
     * Method to return the Service Selector List containing the subscriber
     * types
     * 
     * @return
     */
    public static ArrayList getServiceSelectorList() {
        return _serviceSelectorList;
    }

    /**
     * Method to return the Master Map containing the Service type and Selectors
     * 
     * @return
     */
    public static HashMap getServiceSelectorMap() {
        return _serviceTypeSelectorMasterMap;
    }

    /**
     * Method to get the Default Selector For Service Type
     * 
     * @param p_serviceType
     * @return
     */
    public static ServiceSelectorMappingVO getDefaultSelectorForServiceType(String p_serviceType) {
        if (_log.isDebugEnabled())
            _log.debug("getDefaultSelectorForServiceType()", "Entered for p_serviceType=" + p_serviceType);
        ServiceSelectorMappingVO serviceSelectorMappingVO = null;
        try {
            ArrayList list = getServiceSelectorList();
            for (int i = 0; i < list.size(); i++) {
                serviceSelectorMappingVO = (ServiceSelectorMappingVO) list.get(i);
                if (serviceSelectorMappingVO.getServiceType().equalsIgnoreCase(p_serviceType) && serviceSelectorMappingVO.isDefaultCode())
                    return serviceSelectorMappingVO;
                serviceSelectorMappingVO = null;
            }
        } catch (Exception e) {
            _log.error("getDefaultSelectorForServiceType()", "Exception : " + e);
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorMappingCache[getDefaultSelectorForServiceType]", "", "", "", "Exception:" + e.getMessage() + " Check the configuration for service selector mapping");
        } finally {
            if (serviceSelectorMappingVO == null) {
                _log.error("getDefaultSelectorForServiceType()", "Check the configuration for service selector mapping, Default Selector Not Defined");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorMappingCache[getDefaultSelectorForServiceType]", "", "", "", "Check the configuration for service selector mapping, Default Selector Not Defined");
            }
            if (_log.isDebugEnabled())
                _log.debug("getDefaultSelectorForServiceType()", "Exiting for p_serviceType=" + p_serviceType + " with serviceSelectorMappingVO=" + serviceSelectorMappingVO);
        }
        return serviceSelectorMappingVO;
    }

    /**
     * Method to get the Selector List for Service Type
     * 
     * @param p_serviceType
     * @return
     */
    public static ArrayList getSelectorListForServiceType(String p_serviceType) {
        if (_log.isDebugEnabled())
            _log.debug("getSelectorListForServiceType()", "Entered for p_serviceType=" + p_serviceType);
        ServiceSelectorMappingVO serviceSelectorMappingVO = null;
        ArrayList selectorList = new ArrayList();
        try {
            ArrayList list = getServiceSelectorList();

            for (int i = 0; i < list.size(); i++) {
                serviceSelectorMappingVO = (ServiceSelectorMappingVO) list.get(i);
                if (serviceSelectorMappingVO.getServiceType().equalsIgnoreCase(p_serviceType))
                    selectorList.add(serviceSelectorMappingVO);
                serviceSelectorMappingVO = null;
            }
        } catch (Exception e) {
            _log.error("getSelectorListForServiceType()", "Exception : " + e);
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorMappingCache[getSelectorListForServiceType]", "", "", "", "Exception:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getSelectorListForServiceType()", "Exiting for p_serviceType=" + p_serviceType + " with selectorList=" + selectorList.size());
        }
        return selectorList;
    }

    /**
     * @return ArrayList
     * @author Sanjeew Kumar
     */
    public static ArrayList loadSelectorDropDownForCardGroupPPEL() {
        if (_log.isDebugEnabled())
            _log.debug("loadSelectorDropDownForCardGroupPPEL()", "");
        ArrayList lookupList = new ArrayList();
        try {
            ListValueVO listVO = null;
            String str = "";
            ServiceSelectorMappingVO vo = null;
            HashMap hm = getServiceSelectorMap();
            Iterator itr = getServiceSelectorMap().keySet().iterator();
            while (itr.hasNext()) {
                str = (String) itr.next();
                vo = (ServiceSelectorMappingVO) hm.get(str);
                listVO = new ListValueVO(vo.getSelectorName(), vo.getServiceType() + ":" + vo.getSelectorCode() + ":" + vo.getAmountStr() + ":" + vo.isModifiedAllowed());
                lookupList.add(listVO);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadSelectorDropDownForCardGroupPPEL()", "exited" + lookupList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lookupList;
    }

}
