package com.inter.claro.cs5.cs5scheduler;

import java.util.ArrayList;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @(#)NodeVO
 *  * Copyright(c) 2015, Comviva Technologies LTD.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author               Date            History
 *-------------------------------------------------------------------------------------------------
 * Sanjay Kumar Bind1       30-Sep-2016     Initial Creation
 * ------------------------------------------------------------------------------------------------
 *
 */
public class NodeVO {
	private static final Log log = LogFactory.getLog(NodeVO.class);
    private String url;  //Defines the IP of the Node.
    private int connectionTimeOut;//Defines the Connection time out of Node.
    private int nodeNumber=1;//Defines the node number of the Interface
    private long expiryDuration;//Defines the expiry time of the node.
    private int valReadTimeOut=0;//Defines the read time out for the validation of node.
    private int topReadTimeOut=0;//Defines the read time out for the validation of node.
    private int maxConPerNode=0;//Defines the number of connection supported by single node.
    private int maxSpanNodes=0;//Defines the number of nodes to be span in case of failure.
    private String keepAlive="";//Keep Alive for the Node.
    private boolean isBlocked;//This defines whether the node is blocked or not.If connection is timed out this flag is set to be TRUE.
    private long blokedAt=0;//Time at which the NODE is blocked.
    private int conNumber=0;//Defines the current connection number of the Node.
    private boolean isMaxConReached=false;//set as true when maximum allowed connection for the node is reached.
    private ArrayList transIdList=new ArrayList();//This list is used to contains the transaction id for connections of the NODE.
    private String transactionId=null;//Each node contains the transaction id of request that is to be proccessed.
    private long warnTime=0;//Defines the threshold time for the request and response interval.
    
    private boolean isBlockedByReadTimeOut=false;
    private boolean isBlockedByConTimeOut=false;
    
    private String username=null;
    private String password=null;
    
    private long lastSuspendedAt;
    private long suspendedAt;
    private boolean isSuspended=false;
    private NodeCloser nodeCloser=null;
    private long timeOfFirstAmbiguousTxn;//Contains the time at the first AMBIGUOUS transaction occurs.
    private long currentAmbTxnTime;//represents current ambiguous txn time
    private int numberOfAmbguousTxnAllowed;//This would represent the allowed no of Amb txn with specified duration.
    private long thresholdTime;//Represents the Threshold time.
    private int currentAmbTxnCounter;//Represent the current number of Ambiguous transaction number.
    private boolean processSingleReqFlag = false;//flag is used to ensure that just after expiry only one request 
    									//would be sent to IN to check whether IN is resumed or not. Otherwise if
    									//IN is not Resumed many ambiguous case may occour.
    
    
    
    @Override
    public String toString()
    {
    	StringBuilder sb = new StringBuilder();
        sb.append("url =" + url);
        sb.append(", username="+username);
        sb.append(", password="+password);
        sb.append(", connectionTimeOut=" + connectionTimeOut);
        sb.append(", valReadTimeOut=" + valReadTimeOut);
        sb.append(",topReadTimeOut=" + topReadTimeOut);
        sb.append(", nodeNumber=" + nodeNumber);
        sb.append(", _expiryTime=" + expiryDuration);
        sb.append(", keepAlive=" + keepAlive);
        sb.append(", _maxNumAllowed=" + maxSpanNodes);
        sb.append(", isBlocked=" + isBlocked);
        sb.append(", _maxAllowedConPerNode=" + maxConPerNode);
        sb.append(", blokedAt=" + blokedAt);
        sb.append(", conNumber=" + conNumber);
        sb.append(", isMaxConReached=" + isMaxConReached);
        sb.append(", transIdList=" + transIdList);
        
        sb.append(", lastSuspendedAt=" + lastSuspendedAt);
        sb.append(", suspendedAt=" + suspendedAt);
        sb.append(", isSuspended=" + isSuspended);
        sb.append(", timeOfFirstAmbiguousTxn=" + timeOfFirstAmbiguousTxn);
        sb.append(", currentAmbTxnTime=" + currentAmbTxnTime);
        sb.append(", numberOfAmbguousTxnAllowed=" + numberOfAmbguousTxnAllowed);
        sb.append(", thresholdTime=" + thresholdTime);
        sb.append(", currentAmbTxnCounter=" + currentAmbTxnCounter);
        sb.append(", processSingleReqFlag=" + processSingleReqFlag);
       
        return sb.toString();
    }
    
    
    /**
     * @return Returns the connectionTimeOut.
     */
    public int getConnectionTimeOut() {
        return connectionTimeOut;
    }
    /**
     * @param connectionTimeOut The connectionTimeOut to set.
     */
    public void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }
    /**
     * @return Returns the expiryTime.
     */
    public long getExpiryDuration() {
        return expiryDuration;
    }
    /**
     * @param expiryDuration The expiryTime to set.
     */
    public void setExpiryDuration(long expiryDuration) {
        this.expiryDuration = expiryDuration;
    }
    /**
     * @return Returns the ip.
     */
    public String getUrl() {
        return url;
    }
    /**
     * @param ip The ip to set.
     */
    public void setUrl(String ip) {
        url = ip;
    }
    /**
     * @return Returns the readTimeOut.
     */
    public int getValReadTimeOut() {
        return valReadTimeOut;
    }
    /**
     * @param readTimeOut The readTimeOut to set.
     */
    public void setValReadTimeOut(int readTimeOut) {
        valReadTimeOut = readTimeOut;
    }
    
    /**
     * @return Returns the keepAlive.
     */
    public String getKeepAlive() {
        return keepAlive;
    }
    /**
     * @param keepAlive The keepAlive to set.
     */
    public void setKeepAlive(String keepAlive) {
        this.keepAlive = keepAlive;
    }
    
    /**
     * @return Returns the maxNumAllowed.
     */
    public int getMaxSpanNodes() {
        return maxSpanNodes;
    }
    /**
     * @param maxSpanNodes The maxNumAllowed to set.
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
     * @param nodeNumber The nodeNumber to set.
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
     * @param isBlocked The isBlocked to set.
     */
    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }
    
    /**
     * @return Returns the maxAllowedConPerNode.
     */
    public int getMaxConPerNode() {
        return maxConPerNode;
    }
    /**
     * @param maxConPerNode The maxAllowedConPerNode to set.
     */
    public void setMaxConPerNode(int maxConPerNode) {
        this.maxConPerNode = maxConPerNode;
    }
    public void incrementNode()
    {
        nodeNumber++;
    }
    
    /**
     * @return Returns the blokedAt.
     */
    public long getBlokedAt() {
        return blokedAt;
    }
    /**
     * @param blokedAt The blokedAt to set.
     */
    public void setBlokedAt(long blokedAt) {
        this.blokedAt = blokedAt;
    }
    
    /**
     * @return Returns the conNumberOfNode.
     */
    public int getConNumber() {
        return conNumber;
    }
    /**
     * @param conNumberOfNode The conNumberOfNode to set.
     */
    public void setConNumber(int conNumberOfNode) {
        conNumber = conNumberOfNode;
    }
    /**
     * This method increments the connection number associated with the node.
     * Also add the transactionId to transIdList, that acuired the node at that time. 
     * @param pInTxnID
     */
    public void incrementConNumber(String pInTxnID)
    {
        if(transIdList.add(pInTxnID)) {
        	log.info("incrementConNumber","pInTxnID::"+pInTxnID+" is added to the List");
        }
        
        conNumber++;
    }
    /**
     * This method is used to decrement the connection number after processing the request.
     * Also remove the transaction id which acquired the node from the transIdList.
     * @param pInTxnID
     */
    public void decrementConNumber(String pInTxnID)
    {
        //Removing the transaction id from the Node List.
        if(transIdList.remove(pInTxnID))
            log.info("incrementConNumber","pInTxnID::"+pInTxnID+" is removed from the List");
        if(conNumber>0)
        	conNumber--;
    }
    /**
     * @return Returns the isMaxConOfNodeReached.
     */
    public boolean isMaxConReached() {
        return isMaxConReached;
    }
    /**
     * @param isMaxConReached The isMaxConOfNodeReached to set.
     */
    public void setMaxConReached(boolean isMaxConReached) {
        this.isMaxConReached = isMaxConReached;
    }
    
    /**
     * @return Returns the transIdList.
     */
    public ArrayList getTransIdList() {
        return transIdList;
    }
    /**
     * @param transIdList The transIdList to set.
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
     * @param transactionId The transactionId to set.
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    /**
     * @return Returns the warnTime.
     */
    public long getWarnTime() {
        return warnTime;
    }
    /**
     * @param warnTime The warnTime to set.
     */
    public void setWarnTime(long warnTime) {
        this.warnTime = warnTime;
    }
    
    /**
     * @return Returns the topReadTimeOut.
     */
    public int getTopReadTimeOut() {
        return topReadTimeOut;
    }
    /**
     * @param topReadTimeOut The topReadTimeOut to set.
     */
    public void setTopReadTimeOut(int topReadTimeOut) {
        this.topReadTimeOut = topReadTimeOut;
    }
    
    //============================================================================
    //Blocked by read time out OR connect time out
    public void setBlockedByReadTimeOut(boolean pIsBlockedByReadTimeOut) {
    	isBlockedByReadTimeOut = pIsBlockedByReadTimeOut;
    }
    
    public boolean isBlockedByReadTimeOut() {
    	return isBlockedByReadTimeOut;
    }
    
    public void setBlockedByConTimeOut(boolean pIsBlockedByConTimeOut) {
    	isBlockedByConTimeOut = pIsBlockedByConTimeOut;
    }
    
    public boolean isBlockedByConTimeOut() {
    	return isBlockedByConTimeOut;
    }
    //================================================================================
    
    
    
    
    /**
     * @return Returns the numberOfAmbguousTxnAllowed.
     */
    public int getNumberOfAmbguousTxnAllowed() {
        return numberOfAmbguousTxnAllowed;
    }
    /**
     * @param numberOfAmbguousTxnAllowed The numberOfAmbguousTxnAllowed to set.
     */
    public void setNumberOfAmbguousTxnAllowed(int numberOfAmbguousTxnAllowed) {
        this.numberOfAmbguousTxnAllowed = numberOfAmbguousTxnAllowed;
    }
    /**
     * @return Returns the thresholdTime.
     */
    public long getThresholdTime() {
        return thresholdTime;
    }
    /**
     * @param thresholdTime The thresholdTime to set.
     */
    public void setThresholdTime(long thresholdTime) {
        this.thresholdTime = thresholdTime;
    }
    /**
     * @return Returns the timeOfFirstAmbiguousTxn.
     */
    public long getTimeOfFirstAmbiguousTxn() {
        return timeOfFirstAmbiguousTxn;
    }
    /**
     * @param timeOfFirstAmbiguousTxn The timeOfFirstAmbiguousTxn to set.
     */
    public void setTimeOfFirstAmbiguousTxn(long timeOfFirstAmbiguousTxn) {
        this.timeOfFirstAmbiguousTxn = timeOfFirstAmbiguousTxn;
    }
    
    /**
     * @return Returns the NodeCloser.
     */
    public NodeCloser getNodeCloser() {
        return nodeCloser;
    }
    /**
     * @param NodeCloser The NodeCloser to set.
     */
    public void setNodeCloser(NodeCloser nodeCloser) {
        this.nodeCloser = nodeCloser;
    }
    
    /**
     * @return Returns the currentAmbTxnCounter.
     */
    public int getCurrentAmbTxnCounter() {
        return currentAmbTxnCounter;
    }
    /**
     * @param currentAmbTxnCounter The currentAmbTxnCounter to set.
     */
    public void setCurrentAmbTxnCounter(int currentAmbTxnCounter) {
        this.currentAmbTxnCounter = currentAmbTxnCounter;
    }
    /**
     * Increment the currentAmbTxnCounter by one each time.
     *
     */
    public void incrementCurrentAmbTxnCounter()
    {
        ++currentAmbTxnCounter;
    }
   
    /**
     * @return Returns the currentAmbTxnTime.
     */
    public long getCurrentAmbTxnTime() {
        return currentAmbTxnTime;
    }
    /**
     * @param currentAmbTxnTime The currentAmbTxnTime to set.
     */
    public void setCurrentAmbTxnTime(long currentAmbTxnTime) {
        this.currentAmbTxnTime = currentAmbTxnTime;
    }
    
    /**
     * @return Returns the lastSuspendedAt.  LAST RETRY TIME
     */
    public long getLastSuspendedAt() {
        return lastSuspendedAt;
    }
    /**
     * @param suspendAt The lastSuspendedAt to set.
     */
    
    public void setLastSuspendedAt(long lastSuspendedAt) {
    	this.lastSuspendedAt = lastSuspendedAt;
    }
    
    
    /**
     * @return Returns the suspendedAt.  LAST RETRY TIME
     */
    public long getSuspendedAt() {
        return suspendedAt;
    }
    /**
     * @param suspendAt The suspendedAt to set.
     */
    
    public void setSuspendedAt(long suspendedAt) {
    	this.suspendedAt = suspendedAt;
    }
    
    
    /**
     * @return Returns the isSuspended.
     */
    public boolean isSuspended() {
        return isSuspended;
    }
    /**
     * @param isSuspended The isSuspended to set.
     */
    public void setSuspended(boolean isSuspended) {
        this.isSuspended = isSuspended;
    }
    
    /**
     * @return Returns the expiryFlag.
     */
    public boolean getProcessSingleReqFlag() {
        return processSingleReqFlag;
    }
    /**
     * 
     * @param expiryFlag The expiryFlag to set.
     */
    public void setProcessSingleReqFlag(boolean processSingleReqFlag) {
    	this.processSingleReqFlag = processSingleReqFlag;
    }
	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}
}
