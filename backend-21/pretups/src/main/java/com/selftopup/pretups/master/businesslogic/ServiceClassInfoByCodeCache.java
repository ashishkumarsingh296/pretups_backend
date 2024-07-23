package com.selftopup.pretups.master.businesslogic;

import java.util.HashMap;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;

public class ServiceClassInfoByCodeCache {

    private static Log _log = LogFactory.getLog(ServiceClassInfoByCodeCache.class.getName());
    private static HashMap<String, ServiceClassVO> _serviceClassByCodeMap = new HashMap<String, ServiceClassVO>();

    /**
     * Description : This method loads the load Service Class By Code Map At
     * Startup
     * Method : loadServiceClassByCodeMapAtStartup
     * 
     * @return
     */
    public static void loadServiceClassByCodeMapAtStartup() {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceClassByCodeMapAtStartup", "Entered");
        _serviceClassByCodeMap = loadMapping();
        if (_log.isDebugEnabled())
            _log.debug("loadServiceClassByCodeMapAtStartup()", "Exited");
    }

    /**
     * Description : This method loads the service Class By Code mapping
     * Method : loadMapping
     * 
     * @return HashMap
     */
    private static HashMap<String, ServiceClassVO> loadMapping() {
        if (_log.isDebugEnabled())
            _log.debug("loadMapping", "Entered");

        HashMap<String, ServiceClassVO> serviceClassByCodeMap = null;
        ServiceClassDAO serviceClassDAO = null;
        try {
            serviceClassDAO = new ServiceClassDAO();
            serviceClassByCodeMap = serviceClassDAO.loadServiceClassInfoByCodeWithAll();
        } catch (Exception e) {
            _log.error("loadMapping", "Exception e:" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("loadMapping", "Exiting. serviceClassByCodeMap.size()=" + serviceClassByCodeMap.size());
        }
        return serviceClassByCodeMap;
    }

    /**
     * getServiceClassByCode() method returns the details of service class from
     * cache
     * 
     * @param p_interfaceCode
     * @param p_serviceClassCode
     * @return ServiceClassVO
     */
    public static ServiceClassVO getServiceClassByCode(String p_serviceClassCode, String p_interfaceCode) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getServiceClassByCode()", "entered p_serviceClassCode: " + p_serviceClassCode + ", p_interfaceCode: " + p_interfaceCode);
        ServiceClassVO serviceClassVO = null;
        try {
            serviceClassVO = (ServiceClassVO) _serviceClassByCodeMap.get(p_serviceClassCode + "_" + p_interfaceCode);
            /*
             * if(serviceClassVO==null)
             * {
             * throw new BTSLBaseException("ServiceClassInfoByCodeCache",
             * "getServiceClassByCode"
             * ,PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_NOTFOUND,0,null);
             * }
             */
        } catch (Exception e) {
            _log.error("getServiceClassByCode", "SQLException " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassInfoByCodeCache[getServiceClassByCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ServiceClassInfoByCodeCache", "getServiceClassByCode", "error.general.processing");
        }
        if (_log.isDebugEnabled())
            _log.debug("getServiceClassByCode()", "exited serviceClassVO: " + serviceClassVO);
        return serviceClassVO;
    }

}
