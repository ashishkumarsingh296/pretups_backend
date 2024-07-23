package com.inter.claro.vas.scheduler;

import java.util.ArrayList;

/**
 * @(#)NodeVO
 *                 Copyright(c) 2016, Comviva Technologies Ltd.
 * 				   All Rights Reserved
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Author Date History
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Pankaj Sharma Spt 28,2016 Initial Creation
 *                 ------------------------------------------------------------
 *                 ------------------------------------
 *                 This servlet is responsible to initialize the node details at
 *                 server start up.
 */
public class NodeVO {

    
    private String url; // Defines the IP of the Node.

    private int nodeNumber = 1;// Defines the node number of the Interface
    private long expiryDuration;// Defines the expiry time of the node.
    private int readTimeOut = 0;// Defines the read time out for the validation
                                 // of node.

    // private int _maxConPerNode=0;//Defines the number of connection supported
    // by single node.
    private int maxSpanNodes = 0;// Defines the number of nodes to be span in
                                  // case of failure.
    private boolean isBlocked;// This defines whether the node is blocked or
                               // not.If connection is timed out this flag is
                               // set to be TRUE.
    private long blokedAt = 0;// Time at which the NODE is blocked.

    // private int _conNumber=0;//Defines the current connection number of the
    // Node.
    // private boolean _isMaxConReached=false;//set as true when maximum allowed
    // connection for the node is reached.

    private ArrayList transIdList = new ArrayList();// This list is used to
                                                     // contains the transaction
                                                     // id for connections of
                                                     // the NODE.
    private String transactionId = null;// Each node contains the transaction
                                         // id of request that is to be
                                         // proccessed.
    private long warnTime = 0;// Defines the threshold time for the request and
                               // response interval.

    private String userName = "";// User Name for the Node.
    private String wssdFileLoc = "";// Wssd Location File for the Node.
    private String soapuri = "";// Soap URL for the Node.
    private String pwbackcall = "";// Pwdbackcall for the Node.
    private int maxBarredCount = 0;
    private int barredCount = 0;
    private String password;

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(", nodeNumber=" + nodeNumber);
        sb.append(", _expiryTime=" + expiryDuration);
        sb.append(", isBlocked=" + isBlocked);
        sb.append(", maxBarredCount=" + maxBarredCount);
        sb.append(", barredCount=" + barredCount);
        sb.append(", ReadTimeOut=" + readTimeOut);
        sb.append(", _maxNumAllowed=" + maxSpanNodes);
        sb.append(", blokedAt=" + blokedAt);
        sb.append(", transIdList=" + transIdList);
        sb.append("url =" + url);
        sb.append(", userName=" + userName);
        sb.append(", wssdFileLoc=" + wssdFileLoc);
        sb.append(", pwbackcall=" + pwbackcall);
        sb.append(", soapuri=" + soapuri);
        sb.append(", password=" + password);
        return sb.toString();
    }

    /**
     * @return Returns the keepAlive.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param keepAlive
     *            The keepAlive to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Returns the warnTime.
     */
    public long getWarnTime() {
        return warnTime;
    }

    /**
     * @param warnTime
     *            The warnTime to set.
     */
    public void setWarnTime(long warnTime) {
        this.warnTime = warnTime;
    }

    /**
     * @return Returns the readTimeOut.
     */
    public int getMaxBarredCount() {
        return maxBarredCount;
    }

    /**
     * @param readTimeOut
     *            The readTimeOut to set.
     */
    public void setMaxBarredCount(int maxBarredCount) {
        this.maxBarredCount = maxBarredCount;
    }

    /**
     * @return Returns the readTimeOut.
     */
    public int getBarredCount() {
        return barredCount;
    }

    /**
     * @param readTimeOut
     *            The readTimeOut to set.
     */
    public void setBarredCount(int barredCount) {
        this.barredCount = barredCount;
    }

    public void incrementBarredCount() {
        barredCount++;
    }

    public void resetBarredCount() {
        barredCount = 0;
    }

    /**
     * @return Returns the readTimeOut.
     */
    public int getReadTimeOut() {
        return readTimeOut;
    }

    /**
     * @param readTimeOut
     *            The readTimeOut to set.
     */
    public void setReadTimeOut(int readTimeOut) {
    	this.readTimeOut = readTimeOut;
    }

    /**
     * @return Returns the keepAlive.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param keepAlive
     *            The keepAlive to set.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return Returns the keepAlive.
     */
    public String getWssdFileLoc() {
        return wssdFileLoc;
    }

    /**
     * @param keepAlive
     *            The keepAlive to set.
     */
    public void setWssdFileLoc(String wssdFileLoc) {
        this.wssdFileLoc = wssdFileLoc;
    }

    /**
     * @return Returns the ip.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param ip
     *            The ip to set.
     */
    public void setUrl(String ip) {
        url = ip;
    }

    /**
     * @return Returns the ip.
     */
    public String getSoapUri() {
        return soapuri;
    }

    /**
     * @param ip
     *            The ip to set.
     */
    public void setSoapUri(String soapuri) {
        this.soapuri = soapuri;
    }

    /**
     * @return Returns the ip.
     */
    public String getPwbackCall() {
        return pwbackcall;
    }

    /**
     * @param ip
     *            The ip to set.
     */
    public void setPwbackCall(String pwbackcall) {
        this.pwbackcall = pwbackcall;
    }

    /**
     * @return Returns the expiryTime.
     */
    public long getExpiryDuration() {
        return expiryDuration;
    }

    /**
     * @param expiryDuration
     *            The expiryTime to set.
     */
    public void setExpiryDuration(long expiryDuration) {
        this.expiryDuration = expiryDuration;
    }

    /**
     * @return Returns the maxNumAllowed.
     */
    public int getMaxSpanNodes() {
        return maxSpanNodes;
    }

    /**
     * @param maxSpanNodes
     *            The maxNumAllowed to set.
     */
    public void setMaxSpanNodes(int maxSpanNodes) {
        this.maxSpanNodes = maxSpanNodes;
    }

    /**
     * @return Returns the nodeNumber.
     */
    public int getNodeNumber() {
        return nodeNumber;
    }

    /**
     * @param nodeNumber
     *            The nodeNumber to set.
     */
    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    /**
     * @return Returns the isBlocked.
     */
    public boolean isBlocked() {
        return isBlocked;
    }

    /**
     * @param isBlocked
     *            The isBlocked to set.
     */
    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }


    public void incrementNode() {
        nodeNumber++;
    }

    /**
     * @return Returns the blokedAt.
     */
    public long getBlokedAt() {
        return blokedAt;
    }

    /**
     * @param blokedAt
     *            The blokedAt to set.
     */
    public void setBlokedAt(long blokedAt) {
        this.blokedAt = blokedAt;
    }

    public ArrayList getTransIdList() {
        return transIdList;
    }

    /**
     * @param transIdList
     *            The transIdList to set.
     */
    public void setTransIdList(ArrayList transIdList) {
        this.transIdList = transIdList;
    }

    /**
     * @return Returns the transactionId.
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId
     *            The transactionId to set.
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

}
