/**
 * @(#)FileCache.java
 *                    Copyright(c) 2005, Bharti Telesoft Ltd.
 *                    All Rights Reserved
 * 
 *                    <description>
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Abhijit Chauhan June 22,2005 Initital Creation
 *                    avinash.kamthan June 22,2005
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 * 
 */

package com.selftopup.pretups.inter.cache;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.logging.CacheOperationLog;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

public class FileCache {

    private static Log _log = LogFactory.getLog(FileCache.class.getName());

    private static HashMap _interfaceMap = new HashMap();

    private static ArrayList _list = null; // list required for web only.

    /**
     * get the value for particular I
     * 
     * @param p_interfaceID
     *            interfaceId name
     * @param p_parameter
     *            value to be search in properties file
     * @return String value of the key
     */
    public static String getValue(String p_interfaceID, String p_parameter) {
        return ((String) ((Properties) _interfaceMap.get(p_interfaceID)).get(p_parameter));
    }

    /**
     * Load the interface detail from the file System
     */
    public static void loadAtStartUp() {

        if (_log.isDebugEnabled())
            _log.debug("loadAtStartUp()", "Entered");

        _interfaceMap = loadInterfaceDetail();

        if (_log.isDebugEnabled())
            _log.debug("loadAtStartUp()", "Exited");
    }

    /**
     * To load the interface detail information from the properties file
     * 
     * @return HashMap
     */
    private static HashMap loadInterfaceDetail() {

        if (_log.isDebugEnabled())
            _log.debug("loadInterfacesDetail()", "Entered");

        String directoryPath = Constants.getProperty("INTERFACE_DIRECTORY");
        String directoryPathPG = Constants.getProperty("INTERFACE_DIRECTORY_PG");

        File directory = new File(directoryPath);
        File directoryPG = new File(directoryPathPG);

        HashMap map = new HashMap();
        try {

            if (directory.isDirectory()) {

                File[] interfaceFiles = directory.listFiles();

                if (interfaceFiles != null) {
                    String fileExtension = Constants.getProperty("INTERFACE_FILE_EXTENSION");
                    String filePrefix = Constants.getProperty("INTERFACE_FILE_PREFIX");
                    String ext = null;
                    String interfaceId = null;
                    Properties properties = null;
                    for (int i = 0, k = interfaceFiles.length; i < k; i++) {
                        String fileName = interfaceFiles[i].getName();
                        int index = fileName.lastIndexOf(".");
                        if (index != -1) {
                            ext = fileName.substring(index + 1);
                            interfaceId = fileName.substring(0, index);

                            if (interfaceFiles[i].canRead() && ext.equals(fileExtension) && (filePrefix.trim().length() <= 0 || interfaceId.startsWith(filePrefix))) {
                                properties = new Properties();
                                properties.load(new FileInputStream(interfaceFiles[i]));
                                map.put(interfaceId, properties);
                            }
                        }
                    }

                }

            } else {
                throw new BTSLBaseException(FileCache.class, "loadInterfacesDetail()", "error.directory.notexist");
            }
            if (directoryPG.isDirectory()) {

                File[] interfaceFilesPG = directoryPG.listFiles();

                if (interfaceFilesPG != null) {
                    String fileExtension = Constants.getProperty("INTERFACE_FILE_EXTENSION_PG");
                    String filePrefix = Constants.getProperty("INTERFACE_FILE_PREFIX_PG");
                    String ext = null;
                    String interfaceId = null;
                    Properties properties = null;
                    for (int i = 0, k = interfaceFilesPG.length; i < k; i++) {
                        String fileName = interfaceFilesPG[i].getName();
                        int index = fileName.lastIndexOf(".");
                        if (index != -1) {
                            ext = fileName.substring(index + 1);
                            interfaceId = fileName.substring(0, index);

                            if (interfaceFilesPG[i].canRead() && ext.equals(fileExtension) && (filePrefix.trim().length() <= 0 || interfaceId.startsWith(filePrefix))) {
                                properties = new Properties();
                                properties.load(new FileInputStream(interfaceFilesPG[i]));
                                map.put(interfaceId, properties);
                            }
                        }
                    }

                }

            } else {
                throw new BTSLBaseException(FileCache.class, "loadInterfacesDetail()", "error.directory.notexist");
            }

        } catch (Exception e) {
            _log.error("loadInterfaceDetail() ", e);
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("loadInterfacesDetail()", "Exited Map Size: " + map.size());
        return map;

    }

    /**
     * to update the cache void
     */
    public static void updateData() {

        if (_log.isDebugEnabled())
            _log.debug("updateData()", "Entered");

        HashMap currentMap = loadInterfaceDetail();

        compareMaps(_interfaceMap, currentMap, true);

        _interfaceMap = currentMap;

        if (_log.isDebugEnabled())
            _log.debug("updateData()", "Exited " + _interfaceMap.size());

    }

    /**
     * This is to load the list which have information about files. requirement
     * of this method is to comapre what exist in memeory and what in the file.
     * just to verify whether user update the cacahe after made changes in file
     * 
     * @return ArrayList
     */
    public static ArrayList logDataForWeb() {
        _list = new ArrayList();
        if (_log.isDebugEnabled())
            _log.debug("logDataForWeb()", "Entered");

        HashMap currentMap = loadInterfaceDetail();
        compareMaps(_interfaceMap, currentMap, false);

        if (_log.isDebugEnabled())
            _log.debug("logDataForWeb()", "Exited " + _list.size());

        return _list;
    }

    /**
     * After displaying the data call this method is mandatory, which set the
     * arraylist to null. becuase list is not in use after displaying the
     * information
     */
    public static void setListToNull() {
        _list = null;
    }

    /**
     * Comapre the two maps
     * 
     * @param p_previousMap
     *            in memeory
     * @param p_currentMap
     *            loaded from file not in memory
     * @param p_logInfo
     *            set true to log the information in file
     */
    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap, boolean p_logInfo) {

        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "Entered p_previousMap: " + p_previousMap + "  p_currentMap: " + p_currentMap + " p_logInfo: " + p_logInfo);
        try {
            Iterator iterator = null;
            if (p_previousMap.size() == p_currentMap.size()) {
                iterator = p_previousMap.keySet().iterator();
            } else if (p_previousMap.size() > p_currentMap.size()) {
                iterator = p_previousMap.keySet().iterator();
            } else if (p_previousMap.size() < p_currentMap.size()) {
                iterator = p_currentMap.keySet().iterator();
            }

            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                Properties prevLooupList = (Properties) p_previousMap.get(key);
                Properties curLooupList = (Properties) p_currentMap.get(key);

                if (!prevLooupList.equals(curLooupList)) {
                    if ((prevLooupList != null && prevLooupList.size() > 0) && (curLooupList != null && curLooupList.size() == 0)) {
                        logData(PretupsI.CACHE_ACTION_DELETE, prevLooupList, null, p_logInfo, key);
                    } else if ((prevLooupList != null && prevLooupList.size() == 0) && (curLooupList != null && curLooupList.size() > 0)) {
                        logData(PretupsI.CACHE_ACTION_ADD, curLooupList, null, p_logInfo, key);
                    } else if ((prevLooupList != null && prevLooupList.size() > 0) || (curLooupList != null && curLooupList.size() > 0)) {
                        logData(PretupsI.CACHE_ACTION_MODIFY, prevLooupList, curLooupList, p_logInfo, key);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "Exited " + p_currentMap);
    }

    /**
     * @param p_action
     * @param p_primaryList
     * @param p_secondryList
     * @param p_logInfo
     * @param p_interfaceId
     */
    private static void logData(String p_action, Properties p_primaryList, Properties p_secondryList, boolean p_logInfo, String p_interfaceId) {

        if (_log.isDebugEnabled())
            _log.debug("logData()", "Entered  p_action: " + p_action + " Primary  " + p_primaryList + " p_secondryList: " + p_secondryList + " p_interfaceId: " + p_interfaceId);
        try {
            if (p_secondryList == null) {
                Enumeration enumeration = p_primaryList.propertyNames();
                while (enumeration.hasMoreElements()) {
                    String key = (String) enumeration.nextElement();
                    if (p_logInfo) {
                        CacheOperationLog.log("FileCache", BTSLUtil.formatMessage(p_action, p_interfaceId + "_" + key, p_primaryList.getProperty(key)));
                    } else {
                        CacheLogVO cacheVO = new CacheLogVO();
                        cacheVO.setCacheID(p_interfaceId);
                        cacheVO.setType(key);
                        cacheVO.setActionType(p_action);
                        if (PretupsI.CACHE_ACTION_ADD.equals(p_action)) {
                            cacheVO.setNewValue((String) p_primaryList.getProperty(key));
                        } else if (PretupsI.CACHE_ACTION_DELETE.equals(p_action)) {
                            cacheVO.setOldValue((String) p_primaryList.getProperty(key));
                        }

                        _list.add(cacheVO);
                    }
                }
            } else {
                Properties properties = new Properties();
                properties.putAll(p_secondryList);

                Enumeration prmEnum = p_primaryList.propertyNames();

                String startSeperator = Constants.getProperty("cachestartseparator");
                String middleSeperator = Constants.getProperty("cachemiddleseparator");

                while (prmEnum.hasMoreElements()) {
                    String primaryKey = (String) prmEnum.nextElement();
                    boolean flag = false;
                    Enumeration secEnum = properties.propertyNames();
                    while (secEnum.hasMoreElements()) {
                        String secondryKey = (String) secEnum.nextElement();
                        if (primaryKey.equals(secondryKey)) {
                            if (!p_primaryList.get(secondryKey).equals(properties.get(secondryKey))) {

                                if (p_logInfo) {
                                    StringBuffer sbf = new StringBuffer();
                                    sbf.append(startSeperator);
                                    sbf.append((String) p_primaryList.get(secondryKey));
                                    sbf.append(middleSeperator);
                                    sbf.append((String) properties.get(secondryKey));

                                    CacheOperationLog.log("FileCache", BTSLUtil.formatMessage(p_action, p_interfaceId + "_" + secondryKey, sbf.toString()));
                                    // System.out.println(BTSLUtil.formatMessage(p_action,p_interfaceId+"_"+secondryKey,
                                    // sbf.toString()));
                                } else {
                                    CacheLogVO cacheVO = new CacheLogVO();
                                    cacheVO.setCacheID(p_interfaceId);
                                    cacheVO.setType(primaryKey);
                                    cacheVO.setActionType(p_action);
                                    cacheVO.setOldValue((String) p_primaryList.getProperty(secondryKey));
                                    cacheVO.setNewValue((String) properties.getProperty(secondryKey));
                                    _list.add(cacheVO);
                                }

                            }

                            if (!p_logInfo) {
                                CacheLogVO cacheVO = new CacheLogVO();
                                cacheVO.setCacheID(p_interfaceId);
                                cacheVO.setType(primaryKey);
                                cacheVO.setActionType(PretupsI.CACHE_ACTION_SAME);
                                cacheVO.setOldValue((String) p_primaryList.getProperty(secondryKey));
                                cacheVO.setNewValue((String) properties.getProperty(secondryKey));
                                _list.add(cacheVO);
                            }

                            flag = true;
                            properties.remove(secondryKey);
                            break;
                        }
                    }// while

                    // deleted
                    if (!flag) {
                        if (p_logInfo) {
                            CacheOperationLog.log("FileCache", BTSLUtil.formatMessage(p_action, p_interfaceId + "_" + primaryKey, p_primaryList.getProperty(primaryKey)));
                            // System.out.println(BTSLUtil.formatMessage("Deleted",p_interfaceId+"_"+primaryKey,p_primaryList.getProperty(primaryKey)));

                        } else {
                            CacheLogVO cacheVO = new CacheLogVO();
                            cacheVO.setCacheID(p_interfaceId);
                            cacheVO.setType(primaryKey);
                            cacheVO.setActionType(PretupsI.CACHE_ACTION_DELETE);
                            cacheVO.setOldValue((String) p_primaryList.getProperty(primaryKey));
                            _list.add(cacheVO);
                        }
                    }
                }
                Enumeration newEnum = properties.propertyNames();
                while (newEnum.hasMoreElements()) {
                    String key = (String) newEnum.nextElement();
                    if (p_logInfo) {
                        CacheOperationLog.log("FileCache", BTSLUtil.formatMessage(PretupsI.CACHE_ACTION_ADD, p_interfaceId + "_" + key, properties.getProperty(key)));

                        // System.out.println(BTSLUtil.formatMessage("ADD",p_interfaceId+"_"+key,
                        // properties.getProperty(key)));
                    } else {
                        CacheLogVO cacheVO = new CacheLogVO();
                        cacheVO.setCacheID(p_interfaceId);
                        cacheVO.setType(key);
                        cacheVO.setActionType(PretupsI.CACHE_ACTION_ADD);
                        cacheVO.setNewValue((String) properties.getProperty(key));
                        _list.add(cacheVO);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("logData()", "Exited ");
    }
}
