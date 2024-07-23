package com.selftopup.pretups.product.businesslogic;

import java.util.HashMap;
import java.util.Iterator;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;

public class NetworkProductCache {
    private static Log _log = LogFactory.getLog(NetworkProductCache.class.getName());
    private static HashMap<String, Object> _networkProductMap = null;

    /**
     * @author ankur.dhawan
     *         Description : This method loads the network product cache at
     *         startup
     *         Method : loadNetworkProductMapAtStartup
     * @return
     */
    public static void loadNetworkProductMapAtStartup() {
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkProductMapAtStartup", "Entered");
        _networkProductMap = loadMapping();
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkProductMapAtStartup()", "Exited");
    }

    /**
     * @author ankur.dhawan
     *         Description : This method loads the network product mapping
     *         Method : loadMapping
     * @return HashMap
     */
    private static HashMap<String, Object> loadMapping() {
        if (_log.isDebugEnabled())
            _log.debug("loadMapping", "Entered");
        HashMap<String, Object> productMap = new HashMap<String, Object>();
        NetworkProductDAO networkProductDAO = null;
        try {
            networkProductDAO = new NetworkProductDAO();
            productMap = networkProductDAO.loadNetworkProductCache();
        } catch (Exception e) {
            _log.error("loadMapping", "Exception e:" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("loadMapping", "Exiting. productMap.size()=" + productMap.size());
        }
        return productMap;
    }

    /**
     * @author ankur.dhawan
     *         Description : This method returns an object from
     *         networkProductMap
     *         Method : getObject
     * @return ProductVO
     */
    public static ProductVO getObject(String p_module, String p_productType, long p_requestAmt) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getObject", "Entered");

        ProductVO productVO = null;
        Boolean productFound = false;
        try {
            Iterator<String> mapIterator = _networkProductMap.keySet().iterator();

            while (mapIterator.hasNext()) {
                productVO = (ProductVO) _networkProductMap.get(mapIterator.next());
                if (productVO.getModuleCode().equals(p_module) && productVO.getProductType().equals(p_productType) && !productVO.getStatus().equals(PretupsI.NO) && ((productVO.getProductCategory().equals(PretupsI.PRODUCT_CATEGORY_FIXED) && productVO.getUnitValue() == p_requestAmt) || productVO.getProductCategory().equals(PretupsI.PRODUCT_CATEGORY_FLEX))) {
                    productFound = true;
                    break;
                }
            }
        } catch (Exception e) {
            _log.error("getObject", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[getObject]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("NetworkProductCache", "getObject", "error.general.processing");
        }
        if (_log.isDebugEnabled())
            _log.debug("getObject", "Exiting. productFound=" + productFound);

        if (productFound)
            return productVO;
        else
            return null;
    }
}
