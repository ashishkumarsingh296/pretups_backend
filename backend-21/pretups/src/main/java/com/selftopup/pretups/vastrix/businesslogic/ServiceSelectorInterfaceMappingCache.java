package com.selftopup.pretups.vastrix.businesslogic;

import java.util.HashMap;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;

/**
 * @author rahul.dutt
 */
public class ServiceSelectorInterfaceMappingCache {

    private static Log _log = LogFactory.getLog(ServiceSelectorInterfaceMappingCache.class.getName());

    private static HashMap _serviceSelInterfaceMappingMap = new HashMap();

    public static void loadServSelInterfMappingOnStartup() {
        if (_log.isDebugEnabled()) {
            _log.debug("loadServSelInterfMappOnStartup", "entered");
        }
        _serviceSelInterfaceMappingMap = loadServSelInterfMapping();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServSelInterfMappOnStartup", "exited");
        }
    }

    /**
     * @return
     */
    private static HashMap loadServSelInterfMapping() {
        if (_log.isDebugEnabled()) {
            _log.debug("loadServSelInterfMapping", "entered");
        }
        HashMap serviceSelInterMap = null;
        ServiceSelectorInterfaceMappingDAO serviceSelectorInterfaceMappingDAO = new ServiceSelectorInterfaceMappingDAO();
        try {
            serviceSelInterMap = serviceSelectorInterfaceMappingDAO.loadServSelInterfMappingCache();
        } catch (Exception e) {
            _log.error("loadServSelInterfMapping", "Exeception e" + e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadServSelInterfMappOnStartup", "exiting serviceSelInterMap size" + serviceSelInterMap.size());
        }
        return serviceSelInterMap;

    }

    public static void updateServSelInterfMapping() {

        if (_log.isDebugEnabled())
            _log.debug("updateServSelInterfMapping()", " Entered");
        HashMap currentMap = loadServSelInterfMapping();
        // if required write logic to compare old and new maps here
        _serviceSelInterfaceMappingMap = currentMap;
        if (_log.isDebugEnabled())
            _log.debug("updateServSelInterfMapping()", "exited " + _serviceSelInterfaceMappingMap.size());
    }

    /**
     * @param p_servSelInterCode
     * @return
     */
    public static ServiceSelectorInterfaceMappingVO getObject(String p_servSelInterCode) {

        if (_log.isDebugEnabled())
            _log.debug("getObject()", "entered " + p_servSelInterCode);
        ServiceSelectorInterfaceMappingVO mappingVO = null;
        mappingVO = (ServiceSelectorInterfaceMappingVO) _serviceSelInterfaceMappingMap.get(p_servSelInterCode);
        if (_log.isDebugEnabled())
            _log.debug("getObject()", "exited " + mappingVO);
        return mappingVO;
    }
}
