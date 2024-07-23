package com.inter.claroColUserInfoWS.scheduler;

import java.util.Hashtable;

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

/**
 * @(#)NodeScheduler
 *                   Copyright(c) 2016, Comviva Technologies Ltd.
 * 				  	 All Rights Reserved
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Author Date History
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Pankaj Sharma Spt 28,2016 Initial Creation
 *                   ----------------------------------------------------------
 *                   --------------------------------------
 *                   This class implements the logic to schedule the node.
 *                   Scheduling of node includes following
 *                   1.If node is not blocked
 *                   a.If the node does not reach the max connection limit.
 *                   b.If the node reached the max connection limit, next node
 *                   is checked for the scheduling.
 *                   2.If node is blocked
 *                   a.Expiry duration of blocked node is checked, if it is
 *                   expired then node is scheduled.
 *                   b.If expiry is not reached, next node is checked for the
 *                   scheduling.
 * 
 *                   Blocked Node-A node is set as blocked node from the
 *                   ComverseTGINHandler while creation of connection for the
 *                   node results into error.
 */
public class NodeScheduler {
    private static Log log = LogFactory.getLog(NodeScheduler.class.getName());
    private Hashtable nodeTable = null;// Conatins the node number as Key and
                                        // VO as nod detail
    private int nodeNumber = 1;// Initialize equal to 1 first time so that it
                                // will represent the Node number 1.
    private int totalNodes = 0;// Defines the total number of nodes supported
                                // by the Interface.
    private int retryNum = 1;// Defines the max number by which scheduled node
                              // would be retried.
    private String hostName;// Defines the host name for the Interface and
                             // would be used for the authentication.
    private String headerHostName;// Defines host name for the Interface and
                                   // would be used for the authentication and
                                   // would be set to the request header.
    private String userAgent;// In the case if host name is not present this
                              // would be used for the authentication.
    private String interfaceID;// IN id of the interface.
    int maxAllowedNum = 0;// This number describes that the number of nodes to
                           // be shifted incase of failure.
    private static final String INTERFACE_ID="INTERFACE_ID::";
    private static final String COMMON_EXCEPTION_TEXT = "BTSLBaseException be :: ";
    public NodeScheduler() {
    	//auto
    }

    /**
     * Implements the logic to load all the parameters of entire Nodes defined
     * in the corresponding interface.
     * 
     * @param String pINId
     * @throws BTSLBaseException
     */
    public NodeScheduler(String pINId) throws BTSLBaseException {
    	final String methodName="NodeScheduler[constructor]";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered pINId::" + pINId);
        String url = null;
        long expiryTime = 0;// Defines the expiry duration of blocked node.
        int readTimeOut = 0;// Defines the read time out for the validation
                            // request
                               // request(credit/debit)
        long warnTime = 0;// Defines the time threshold for request and
                          // response.
        String userName = "";// Defines the keep alive parameter.
        String password ;
        NodeVO nodeVO = null;// Contains all the detail of node
        try {
            nodeTable = new Hashtable();
            interfaceID = pINId;
            try {
                totalNodes = Integer.parseInt(FileCache.getValue(pINId, "MAX_NODE").trim());
                if (log.isDebugEnabled())
                    log.debug(methodName, "MAX_NODE::" + totalNodes);

            } catch (Exception e) {
            	log.errorTrace("BTSLBaseException be ::"+methodName , e);
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, INTERFACE_ID + interfaceID, "", "", "check the value of MAX_NODE in IN File Exception e::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_INITIALIZATION);
            }
            try {
                maxAllowedNum = Integer.parseInt(FileCache.getValue(pINId, "MAX_ALWD_NO").trim());
                if (log.isDebugEnabled())
                    log.debug(methodName, "MAX_ALWD_NO::" + maxAllowedNum);
            } catch (Exception e) {
            	log.errorTrace("BTSLBaseException be  :: "+methodName , e);
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, INTERFACE_ID + interfaceID, "", "", "Check the value of MAX_ALWD_NO in INFile Exception e::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_INITIALIZATION);
            }

            try {
                retryNum = Integer.parseInt(FileCache.getValue(pINId, "RETRY_NUM").trim());
                if (log.isDebugEnabled())
                    log.debug(methodName, "RETRY_NUM::" + retryNum);
                setRetryNum(retryNum);
            } catch (Exception e) {
            	log.errorTrace("BTSLBaseException be  :: "+methodName , e);
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, INTERFACE_ID + interfaceID, "", "", "Check the value of RETRY_NUM in INFile Exception e::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_INITIALIZATION);
            }

            // Get the detail of each node for corresponding interface and set
            // these detail in a ChannelVO.
            // Tese details are
            // 1.URL
            // 2.Connection time out.
            // 3.Expiry time out.
            // 4.Number of available nodes.
            // 5.Max Number upto which the node is shifted in case the failure
            // of one node.
            // 6.Warn time
            // 7.Retry number
            // Constructing VO corresponding to each node.
            for (int l = 1; l <= totalNodes; l++) {
                try {
                    // Create an instance of NodeVO
                    nodeVO = new NodeVO();
                    // Set the Node number as key.
                    nodeVO.setNodeNumber(l);
                    url = FileCache.getValue(pINId, "URL_" + l);
                    if (log.isDebugEnabled())
                        log.debug(methodName, "url_" + l + "::" + url);
                    if (InterfaceUtil.isNullString(url)) {
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, INTERFACE_ID + interfaceID, "", "", "Check the value of URL_" + l + "in INFile " + pINId);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_INITIALIZATION);
                    }
                    nodeVO.setUrl(url.trim());

                    try {
                        String readTimeOutStr = FileCache.getValue(pINId, "READ_TIMEOUT_" + l).trim();
                        readTimeOut = Integer.parseInt(readTimeOutStr);
                        if (log.isDebugEnabled())
                            log.debug(methodName, "READ_TIMEOUT_" + l + "::" + readTimeOut);
                        nodeVO.setReadTimeOut(readTimeOut);
                    } catch (Exception e) {
                    	log.errorTrace("BTSLBaseException be:: "+methodName , e);
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, INTERFACE_ID + interfaceID, "", "", "Check the value of READ_TIMEOUT_" + l + " in   INFile " + pINId);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_INITIALIZATION);
                    }

                    try {
                        String warnTimeStr = FileCache.getValue(pINId, "WARN_TIME_" + l).trim();
                        warnTime = Long.parseLong(warnTimeStr);
                        if (log.isDebugEnabled())
                            log.debug(methodName, "WARN_TIME_" + l + "::" + warnTimeStr);
                        nodeVO.setWarnTime(warnTime);
                    } catch (Exception e) {
                    	log.errorTrace("BTSLBaseException be ::"+methodName , e);
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, INTERFACE_ID + interfaceID, "", "", "Check the value of WARN_TIME_" + l + " in INFile  " + pINId);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_INITIALIZATION);
                    }
                    try {
                        userName = FileCache.getValue(pINId, "USER_NAME_" + l).trim();
                        if (log.isDebugEnabled())
                            log.debug(methodName, "USER_NAME_" + l + "::" + userName);
                        nodeVO.setUserName(userName);
                    } catch (Exception e) {
                    	log.errorTrace("BTSLBaseException be : : "+methodName , e);
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, INTERFACE_ID + interfaceID, "", "", "Check the value of USER_NAME_" + l + " in  INFile " + pINId);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_INITIALIZATION);
                    }

                    try {
                        password = FileCache.getValue(pINId, "PASSWORD_" + l).trim();
                        if (log.isDebugEnabled())
                            log.debug(methodName, "PASSWORD_" + l + "::" + password);
                        nodeVO.setPassword(password);
                    } catch (Exception e) {
                    	log.errorTrace("BTSLBaseException be:: "+methodName , e);
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, INTERFACE_ID + interfaceID, "", "", "Check the value of PASSWORD_" + l + " in  INFile " + pINId);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_INITIALIZATION);
                    }

                    try {
                        String expiryTimeStr = FileCache.getValue(pINId, "EXPIRY_TIME_" + l).trim();
                        if (log.isDebugEnabled())
                            log.debug(methodName, "EXPIRY_TIME_" + l + "::" + expiryTimeStr);
                        expiryTime = Long.parseLong(expiryTimeStr);
                        nodeVO.setExpiryDuration(expiryTime);
                    } catch (Exception e) {
                    	log.errorTrace("BTSLBaseException be: : "+methodName , e);
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, INTERFACE_ID + interfaceID, "", "", "Check the value of EXPIRY_TIME_" + l + "in INFile " + pINId);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_INITIALIZATION);
                    }

                    try {
                        int maxBarredCount = Integer.parseInt(FileCache.getValue(pINId, "MAX_BARRED_COUNT_" + l).trim());
                        if (log.isDebugEnabled())
                            log.debug(methodName, "MAX_BARRED_COUNT_" + l + "::" + maxBarredCount);
                        nodeVO.setMaxBarredCount(maxBarredCount);
                    } catch (Exception e) {
                    	log.errorTrace("BTSLBaseException be: : "+methodName , e);
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, INTERFACE_ID + interfaceID, "", "", "Check the value of MAX_BARRED_COUNT_" + l + " in INFile " + pINId);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_INITIALIZATION);
                    }

                    nodeTable.put(String.valueOf(l), nodeVO);
                } catch (BTSLBaseException be) {
                    log.error(methodName, "BTSLBaseException be::" + be.getMessage());
                    throw be;
                } catch (Exception e) {
                	log.errorTrace(COMMON_EXCEPTION_TEXT+methodName , e);
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, INTERFACE_ID + interfaceID, "", "", "Exception while initializing the Node details of interfaceID = " + pINId + " Exception e=" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_INITIALIZATION);
                }
            }
        } catch (BTSLBaseException be) {
            log.error(methodName, "BTSLBaseException be::" + be.getMessage());
            throw be;
        } catch (Exception e) {
        	log.errorTrace(COMMON_EXCEPTION_TEXT+methodName , e);
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, INTERFACE_ID + interfaceID, "", "", "Exception while initializing the Node details of interfaceID = " + pINId + " Exception e=" + e.getMessage());
            log.error(this, "[constructor]", e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_INITIALIZATION);
        } finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting nodeTable::" + nodeTable);
        }// end of finally
    }// end of constructor

    /**
     * @return Returns the retryNum.
     */
    public int getRetryNum() {
        return retryNum;
    }

    /**
     * @param retryNum
     *            The retryNum to set.
     */
    public void setRetryNum(int retryNum) {
        this.retryNum = retryNum;
    }

    /**
     * Increment the node number and initialize the nodeNumber=1 if it reaches
     * the MaximumNode number.
     */
    public void incrementNodeNumber() {
        if (nodeNumber == totalNodes)
            nodeNumber = 1;
        else
            nodeNumber++;
    }

    /**
     * @return Returns the hostName.
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * @return Returns the userAgent.
     */
    public String getHeaderHostName() {
        return headerHostName;
    }

    /**
     * @return Returns the userAgent.
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * This method used to get the URL for corresponding node.
     * 
     * @param String pTransId
     * @return NodeVO
     * @throws BTSLBaseException
     */
    public synchronized NodeVO getNodeVO(String pTransId) throws BTSLBaseException {
    	final String methodName="getNodeVO";
        if (log.isDebugEnabled())
            log.debug(methodName, pTransId, "Entered pTransId" + pTransId);
        String nodeNumStr = null;
        NodeVO nodeVO = null;
        // 1.Get the Available node from getNode.
        // 2.If there is not any available node throw an error.
        // 3.If a valid node is available return the detail as VO
        try {
            nodeNumStr = getNode(pTransId);
            if (nodeNumStr == null) {
                log.error(methodName, pTransId, "There is no scheduled node to process the request for pTransId::" + pTransId);
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_SCHEDULING);
            }
            nodeVO = (NodeVO) nodeTable.get(nodeNumStr);
            if (nodeVO == null) {
                log.error(methodName, pTransId, "No detail found for the Node number::" + nodeNumStr);
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_SCHEDULING);
            }
        } catch (BTSLBaseException be) {
            log.error(methodName, pTransId, "while processing the request for pTransId::" + pTransId + " get BTSLBaseException  be::" + be.getMessage());
            throw be;
        } catch (Exception e) {
        	log.errorTrace(COMMON_EXCEPTION_TEXT+methodName , e);
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[getNodeVO]", INTERFACE_ID + interfaceID + "  and Node number =[" + nodeNumStr + "] pTransId: " + pTransId, "", "", "Exception,while getting Node for Scheduling e=" + e.getMessage());
            log.error(methodName, pTransId, "Exception  e::" + e.getMessage() + " pTransId::" + pTransId);
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_SCHEDULING);
        }// end of catch-Exception
        finally {
            if (log.isDebugEnabled())
                log.debug(methodName, pTransId, "Exited nodeVO::" + nodeVO);
        }// end of finally
        return nodeVO;
    }// end of getNodeVO

    /**
     * This method is used to give the available(scheduled)node for connection.
     * 
     * @param String pTransId
     * @return String
     * @throws BTSLBaseException
     */
    private synchronized String getNode(String pTransId) throws BTSLBaseException {
    	final String methodName="NodeScheduler[getNode]";
        if (log.isDebugEnabled())
            log.debug(methodName, pTransId, "Entered pTransId::" + pTransId);
        boolean isNext = true;
        String nodeNumStr = null;
        NodeVO nodeVO = null;
        int tempCount = 1;
        try {
            // Get the detail corresponding to the node that is available as
            // currentNode.
            nodeVO = (NodeVO) nodeTable.get(String.valueOf(nodeNumber));
            // Loop this checks as the number defined in INFile.
            while (isNext) {
                try {
                    if (tempCount > maxAllowedNum) {
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, methodName, INTERFACE_ID + interfaceID, "", "", "MAX ITERATION [" + tempCount + "] is reached");
                        // throw new
                        nodeNumStr = String.valueOf(nodeNumber);
                        break;
                    }
                    tempCount++;// This represents the total iteration made to
                                // find a scheduled node and should not be
                                // greater than the max node.
                    if (log.isDebugEnabled())
                        log.debug(methodName, pTransId, "tempCount::" + tempCount);
                    // 1.Check Null
                    // 2.Check for the blocked node whether this node is blocked
                    // or not.
                    // 3.If node is not blocked check whether the node reaches
                    // the Max allowed connection.
                    // 3-a.If the max connection count reaches for a node handle
                    // the event, check the next node to schedule.
                    // 3-b.If the max node reaches throw a seprate error code
                    // showing channel is full and decrement the max when a
                    // request is successful
                    // processed for that node.
                    // 4.If node is blocked do the following.
                    // 4-a.Check the expiry time of the blocked node
                    // 4-b.if diff of current time and blocked time is greater
                    // than expiry time, blocked node is expired current node is
                    // returned and increment
                    // the corresponding connection number of the node.
                    nodeVO = (NodeVO) nodeTable.get(String.valueOf(nodeNumber));
                    if (nodeVO == null) {
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, methodName, INTERFACE_ID + interfaceID, "", "pTransId:: " + pTransId, "There is no details corresponding to NODE NUMBER=" + nodeNumber);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_DETAIL_NOT_FOUND);
                    }

                    // check for the Max count reached for barred the
                    // transaction if reached then barred the node

                    // Check whether Node is blocked or NOT.
                    if (!nodeVO.isBlocked()) {

                        if (nodeVO.getBarredCount() >= nodeVO.getMaxBarredCount()) {
                            nodeVO.setBlocked(true);
                            nodeVO.setBlokedAt(System.currentTimeMillis());
                            nodeNumStr = String.valueOf(nodeNumber);
                            incrementNodeNumber();
                            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, INTERFACE_ID + interfaceID, "", "pTransId::  " + pTransId, "CCWS IP=" + nodeVO.getUrl() + " is not responding and blocked");
                            continue;
                        }
                        // Get the current NODE.
                        nodeNumStr = String.valueOf(nodeNumber);
                        // Increment the NODE number.
                        incrementNodeNumber();
                        break;
                    }

                    if (log.isDebugEnabled())
                        log.debug(methodName, pTransId, "NodeNumber [" + nodeNumber + "] is blocked" + "  pTransId::" + pTransId);
                    // Check whether the blocked node is expired or not,if
                    // expired then set isExpired as false and break the loop.
                    if (isExpired(nodeVO.getBlokedAt(), nodeVO.getExpiryDuration())) {
                        // Increment the connection number for node and
                        // decrement this number after completion of request.
                        // Increment the NODE number.
                        // Get the current NODE.
                        nodeNumStr = String.valueOf(nodeNumber);
                        incrementNodeNumber();
                        nodeVO.setBlocked(false);
                        // Initialize the block time if it is expired.
                        nodeVO.setBlokedAt(0);
                        nodeVO.setBarredCount(0);
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, INTERFACE_ID + interfaceID, "", "  pTransId::" + pTransId, "CCWS IP=" + nodeVO.getUrl() + " is unblocked");
                        break;
                    }
                    // Log the detail and Increment the NODE number when node is
                    // blocked and it is not expired.
                    log.info(methodName, pTransId, "Node number ::" + nodeNumber + " of interfaceID::" + interfaceID + "is blocked and its expiry is not reached");
                    incrementNodeNumber();
                } catch (BTSLBaseException be) {
                    throw be;
                } catch (Exception e) {
                    // decrement the connection counter if any error occurs
                    // after incrementing the connection number.
                	log.errorTrace(COMMON_EXCEPTION_TEXT+methodName , e);
                    log.error(methodName, pTransId, "interfaceID" + interfaceID + " pTransId ::" + pTransId + " Exception e::" + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, INTERFACE_ID + interfaceID + " and Node number =[" + nodeNumStr + "]", "", "transId::" + pTransId, "Exception while getting Node e=" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_SCHEDULING);
                }// end of catch-Exception
            }// end of while
        } catch (BTSLBaseException be) {
            log.error(methodName, pTransId, "pTransId::" + pTransId + " BTSLBaseException  be::" + be.getMessage());
            throw be;
        } catch (Exception e) {
        	log.errorTrace(COMMON_EXCEPTION_TEXT+methodName , e);
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, INTERFACE_ID + interfaceID + " and Node number =[" + nodeNumStr + "]", "", "", "Exception while getting Node e=" + e.getMessage());
            log.error(methodName, pTransId, "pTransId::" + pTransId + " Exception e::" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_SCHEDULING);
        }// end of catch-Exception
        finally {
            if (log.isDebugEnabled())
                log.debug(methodName, pTransId, "Exited nodeNumStr::" + nodeNumStr);
        }// end of finally
        return nodeNumStr;
    }// end of

    /**
     * This method is used to check the expiry of node,if it is blocked.
     * 
     * @param pBlockedAt
     * @param pExpiryDuration
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isExpired(long pBlockedAt, long pExpiryDuration) throws BTSLBaseException {
    	final String methodName="isExpired";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered pBlockedAt::" + pBlockedAt + " pExpiryDuration::" + pExpiryDuration);
        boolean isExp = false;
        try {
            long currentTime = System.currentTimeMillis();
            if (log.isDebugEnabled())
                log.debug(methodName, "currentTime::" + currentTime);
            if ((currentTime - pBlockedAt) >= pExpiryDuration)
                isExp = true;
        } catch (Exception e) {
        	log.errorTrace(COMMON_EXCEPTION_TEXT+methodName , e);
            log.error(methodName, "While checking the expiry of blocked node get the Exception e::" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_SCHEDULING);
        }// end of catch-Exception
        finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "isExp::" + isExp);
        }// end of finally
        return isExp;
    }// end of isExpired
}
