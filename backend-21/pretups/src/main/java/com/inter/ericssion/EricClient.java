package com.inter.ericssion;

/**
 * @(#)EricClient.java
 *                     Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                     All Rights Reserved
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Abhijit Chauhan June 22,2005 Initial Creation
 *                     --------------------------------------------------------
 *                     ----------------------------------------
 *                     This class is resposible creates the two thread and
 *                     shared data storage HashMap(inBucket and OutBucket)
 */
import java.util.*;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.util.Constants;

public class EricClient {
    public static HashMap inBucketMap = null;// Contains a key as InterfaceID
                                             // whose value is vector that
                                             // contains the request
    public static HashMap busyBucketMap = null;// Busy bucket is used to
                                               // contains all the requests that
                                               // are being processed for
                                               // corresponding interface.
    public static HashMap outBucketMap = null;// Contains a key as interfaceID
                                              // whose value is a
                                              // HashMap(outBucket),outBucket's
                                              // key is transactionID and value
                                              // is response string.
    private static Log _log = LogFactory.getLog(EricClient.class.getName());
    public static HashMap _inOutMaps = new HashMap();// Store the mapping of in
                                                     // and out socket
                                                     // connection.
    static {
        int MAX_ERIC_POOL_SIZE = 0;// Defines the maximum pool size.

        // Get the interfaces from the Constant props
        String ericPoolINIds = Constants.getProperty("SOCKET_CONN_POOL_IDS");
        if (ericPoolINIds == null || ericPoolINIds.trim().length() <= 0) {
            _log.error("static", "ericPoolINIds not defined in configuration file, EricClient Pool could not be created");
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricClient[static block]", "", "", "", "ericPoolINIds not defined in configuration file, Pool could not be created");
        }
        try {
            StringTokenizer strTokens = new StringTokenizer(ericPoolINIds, ",");
            inBucketMap = new HashMap(strTokens.countTokens());
            busyBucketMap = new HashMap(strTokens.countTokens());
            outBucketMap = new HashMap(strTokens.countTokens());
            String strINId = null;
            while (strTokens.hasMoreElements()) {
                strINId = strTokens.nextToken().trim();
                // Put the interfaceId as key and vector that will
                // contain,request string as value in the inBucket.
                inBucketMap.put(strINId, new Vector());
                // Put the interfaceId as key and vector of request string as
                // value in the busy bucket.
                busyBucketMap.put(strINId, new Vector());
                outBucketMap.put(strINId, Collections.synchronizedMap(new HashMap()));
                try {
                    MAX_ERIC_POOL_SIZE = Integer.parseInt(FileCache.getValue(strINId, "POOL_SIZE"));
                } catch (Exception e) {
                    throw new Exception("Check the POOL_SIZE defined in INFile for Interface ID=" + strINId);
                }
                // Make array of InClass thread with the size defined in INFile.
                InClass[] inArray = new InClass[MAX_ERIC_POOL_SIZE];
                // Make the array of OutClass thread with the size defined in
                // INFile.
                OutClass[] outArray = new OutClass[MAX_ERIC_POOL_SIZE];
                for (int i = 1; i <= MAX_ERIC_POOL_SIZE; i++) {
                    _log.info("static", "creating in and out threads for IN ID: " + strINId + " Number i=" + i);

                    inArray[i - 1] = new InClass(strINId, i);
                    outArray[i - 1] = new OutClass(strINId, i);
                    // Start the in thread for corresponding interface id,that
                    // is responsible to send the request.
                    inArray[i - 1].start();
                    // Start the out thread for corresponding interface id,that
                    // is responsible to fetch the response.
                    outArray[i - 1].start();

                    _inOutMaps.put(strINId + "_IN", inArray);
                    _inOutMaps.put(strINId + "_OUT", outArray);

                    Thread.currentThread().sleep(100);
                }
            }
        } catch (Exception e) {
            _log.error("static", "Exception e:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Method to initailize thrads
     * 
     * @param p_interfaceID
     */
    public void initializeThreads(String p_interfaceID) {
        try {
            StringTokenizer strTokens = new StringTokenizer(p_interfaceID, ",");
            String strINId = null;
            int MAX_ERIC_POOL_SIZE = 0;// Defines the maximum pool size.
            while (strTokens.hasMoreElements()) {
                strINId = strTokens.nextToken().trim();
                // Put the interfaceId as key and vector that will
                // contain,request string as value in the inBucket.
                inBucketMap.put(strINId, new Vector());
                // Put the interfaceId as key and vector of request string as
                // value in the busy bucket.
                busyBucketMap.put(strINId, new Vector());
                outBucketMap.put(strINId, Collections.synchronizedMap(new HashMap()));
                try {
                    MAX_ERIC_POOL_SIZE = Integer.parseInt(FileCache.getValue(strINId, "POOL_SIZE"));
                } catch (Exception e) {
                    throw new Exception("Check the POOL_SIZE defined in INFile for Interface ID=" + strINId);
                }
                InClass[] oldInClass = (InClass[]) _inOutMaps.get(strINId + "_IN");
                OutClass[] oldOutClass = (OutClass[]) _inOutMaps.get(strINId + "_OUT");
                try {
                    if (oldInClass != null) {
                        for (int i = 1; i <= oldInClass.length; i++) {
                            _log.info("initializeThreads", "stopping previous in and out threads for IN ID: " + strINId + " Number i=" + i);
                            // Stopping the existing threads
                            oldInClass[i - 1].stop();
                            oldOutClass[i - 1].stop();

                            /*
                             * oldInClass[i-1].join();
                             * oldOutClass[i-1].join();
                             */
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Make array of InClass thread with the size defined in INFile.
                InClass[] inArray = new InClass[MAX_ERIC_POOL_SIZE];
                // Make the array of OutClass thread with the size defined in
                // INFile.
                OutClass[] outArray = new OutClass[MAX_ERIC_POOL_SIZE];

                for (int i = 1; i <= MAX_ERIC_POOL_SIZE; i++) {
                    inArray[i - 1] = new InClass(strINId, i);
                    outArray[i - 1] = new OutClass(strINId, i);

                    _log.info("initializeThreads", "creating new in and out threads for IN ID: " + strINId + " Number i=" + i);

                    // Start the threads for corresponding interface id,that is
                    // responsible to send the request.
                    inArray[i - 1].start();
                    outArray[i - 1].start();

                    _inOutMaps.put(strINId + "_IN", inArray);
                    _inOutMaps.put(strINId + "_OUT", outArray);

                    Thread.currentThread().sleep(100);
                }
            }
        } catch (Exception e) {
            _log.error("initializeThreads", "Exception e:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Method to update the IN and Out threads
     * 
     * @param p_interfaceID
     */
    public void updateThreads(String p_interfaceID) {
        try {
            StringTokenizer strTokens = new StringTokenizer(p_interfaceID, ",");
            String strINId = null;
            while (strTokens.hasMoreElements()) {
                strINId = strTokens.nextToken().trim();
                InClass[] oldInClass = (InClass[]) _inOutMaps.get(strINId + "_IN");
                OutClass[] oldOutClass = (OutClass[]) _inOutMaps.get(strINId + "_OUT");
                try {
                    if (oldInClass != null) {
                        for (int i = 1; i <= oldInClass.length; i++) {
                            _log.info("updateThreads", "updateThreads previous in and out threads for IN ID: " + strINId + " Number i=" + i);
                            // Stopping the existing threads
                            oldInClass[i - 1].update(strINId, i);
                            oldOutClass[i - 1].update(strINId, i);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            _log.error("initializeThreads", "Exception e:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
