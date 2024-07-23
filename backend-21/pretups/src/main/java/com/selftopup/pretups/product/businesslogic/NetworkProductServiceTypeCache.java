package com.selftopup.pretups.product.businesslogic;

/*
 * NetworkProductServiceTypeCache.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 25/08/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class to load the Service Type and Product Type mapping
 */

import java.util.HashMap;
import java.util.Iterator;

import com.selftopup.common.ListValueVO;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.logging.CacheOperationLog;
import com.selftopup.util.BTSLUtil;

public class NetworkProductServiceTypeCache {

    private static Log _log = LogFactory.getLog(NetworkProductServiceTypeCache.class.getName());
    private static HashMap _productServericeTypeMap = new HashMap();
    private static HashMap _networkProductMapping = new HashMap();
    private static NetworkProductDAO _networkProductDAO = new NetworkProductDAO();

    public static void refreshProductServiceTypeMapping() {
        if (_log.isDebugEnabled())
            _log.debug("refreshProductServiceTypeMapping", "Entered: ");
        try {
            HashMap tempMap = null;

            if (_log.isDebugEnabled())
                _log.debug("refreshProductServiceTypeMapping", " Before loading:" + _productServericeTypeMap);
            tempMap = _networkProductDAO.loadProductServiceTypeMapping();
            // No need to compair the map
            // compareMaps(_productServericeTypeMap,tempMap);
            _productServericeTypeMap = tempMap;
            if (_log.isDebugEnabled())
                _log.debug("refreshProductServiceTypeMapping", " After loading:" + _productServericeTypeMap.size());
        } catch (Exception e) {
            _log.error("refreshProductServiceTypeMapping", "Exception " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void refreshNetworkProductMapping() {
        if (_log.isDebugEnabled())
            _log.debug("refreshNetworkProductMapping", "Entered: ");
        try {
            HashMap tempMap = null;

            if (_log.isDebugEnabled())
                _log.debug("refreshNetworkProductMapping", " Before loading:" + _productServericeTypeMap);
            tempMap = _networkProductDAO.loadNetworkProductMapping();
            compareNetworkProductMaps(_networkProductMapping, tempMap);
            _networkProductMapping = tempMap;
            if (_log.isDebugEnabled())
                _log.debug("refreshNetworkProductMapping", " After loading:" + _productServericeTypeMap.size());
        } catch (Exception e) {
            _log.error("refreshNetworkProductMapping", "Exception " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * private static void compareMaps(HashMap p_previousMap, HashMap
     * p_currentMap)
     * {
     * if (_log.isDebugEnabled())
     * _log.debug("compareMaps()","Entered p_previousMap: "
     * +p_previousMap+"  p_currentMap: "+p_currentMap);
     * try
     * {
     * Iterator iterator = null;
     * Iterator copiedIterator = null;
     * if(p_previousMap.size() == p_currentMap.size() )
     * {
     * iterator = p_previousMap.keySet().iterator();
     * copiedIterator = p_previousMap.keySet().iterator();
     * }
     * else
     * if(p_previousMap.size() > p_currentMap.size() )
     * {
     * iterator = p_previousMap.keySet().iterator();
     * copiedIterator = p_previousMap.keySet().iterator();
     * }
     * else
     * if(p_previousMap.size() < p_currentMap.size() )
     * {
     * iterator = p_currentMap.keySet().iterator();
     * copiedIterator = p_previousMap.keySet().iterator();
     * }
     * 
     * boolean isNewAdded = false;
     * while (iterator.hasNext())
     * {
     * String key = (String)iterator.next();
     * String prevValue= (String) p_previousMap.get(key);
     * String currValue= (String) p_currentMap.get(key);
     * 
     * if(prevValue != null && currValue == null)
     * {
     * isNewAdded = true;
     * CacheOperationLog.log("ProductServiceTypeMapping",BTSLUtil.formatMessage(
     * "Delete",key,prevValue));
     * }
     * else if(prevValue == null && currValue != null)
     * CacheOperationLog.log("ProductServiceTypeMapping",BTSLUtil.formatMessage(
     * "Add",key,currValue));
     * else if(prevValue != null && currValue != null)
     * {
     * if( ! currValue.equals(prevValue))
     * CacheOperationLog.log("ProductServiceTypeMapping",BTSLUtil.formatMessage(
     * "Modify",key,"From :"+prevValue+" To:"+currValue));
     * }
     * }
     * 
     * // Note: this case arises when same number of element added and deleted
     * as well
     * if(p_previousMap.size() == p_currentMap.size() && isNewAdded )
     * {
     * HashMap tempMap = new HashMap(p_currentMap);
     * while (copiedIterator.hasNext())
     * {
     * tempMap.remove((String)copiedIterator.next());
     * }
     * 
     * Iterator iterator2 = tempMap.keySet().iterator();
     * while(iterator2.hasNext())
     * {
     * String currValue= (String) p_currentMap.get(iterator2.next());
     * CacheOperationLog.log("ProductServiceTypeMapping",BTSLUtil.formatMessage(
     * "Add",(String)iterator2.next(),currValue));
     * }
     * }
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * }
     * if (_log.isDebugEnabled()) _log.debug("compareMaps()","Exited");
     * }
     */

    public static ListValueVO getProductServiceValueVO(String p_serviceType, String p_subService) {
        String key = p_serviceType + "_" + p_subService;
        return (ListValueVO) _productServericeTypeMap.get(key);
    }

    public static NetworkProductVO getNetworkProductDetails(String p_networkCode, String p_productCode) {
        return (NetworkProductVO) _networkProductMapping.get(p_networkCode + "_" + p_productCode);
    }

    private static void compareNetworkProductMaps(HashMap p_previousMap, HashMap p_currentMap) {
        if (_log.isDebugEnabled())
            _log.debug("compareNetworkProductMaps()", "Entered PreviousMap " + p_previousMap + "  Current Map" + p_currentMap);
        try {
            Iterator iterator = null;
            Iterator copiedIterator = null;
            if (p_previousMap.size() == p_currentMap.size()) {
                iterator = p_previousMap.keySet().iterator();
                copiedIterator = p_previousMap.keySet().iterator();
            } else if (p_previousMap.size() > p_currentMap.size()) {
                iterator = p_previousMap.keySet().iterator();
                copiedIterator = p_previousMap.keySet().iterator();
            } else if (p_previousMap.size() < p_currentMap.size()) {
                iterator = p_currentMap.keySet().iterator();
                copiedIterator = p_previousMap.keySet().iterator();
            }

            boolean isNewAdded = false;
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                NetworkProductVO prevVO = (NetworkProductVO) p_previousMap.get(key);
                NetworkProductVO curVO = (NetworkProductVO) p_currentMap.get(key);

                if (prevVO != null && curVO == null) {
                    isNewAdded = true;
                    CacheOperationLog.log("NetworkProductMapping", BTSLUtil.formatMessage("Delete", key, prevVO.logInfo()));
                } else if (prevVO == null && curVO != null)
                    CacheOperationLog.log("NetworkProductMapping", BTSLUtil.formatMessage("Add", key, curVO.logInfo()));
                else if (prevVO != null && curVO != null) {
                    if (!curVO.equals(prevVO))
                        CacheOperationLog.log("NetworkProductMapping", BTSLUtil.formatMessage("Modify", key, curVO.differences(prevVO)));
                }
            }

            // Note: this case arises when same number of element added and
            // deleted as well
            if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    NetworkProductVO mappingVO = (NetworkProductVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("NetworkProductMapping", BTSLUtil.formatMessage("Add", (String) iterator2.next(), mappingVO.logInfo()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("compareNetworkProductMaps()", "Exited");
    }
}
