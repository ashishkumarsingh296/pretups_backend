package com.inter.vodaidea.idea.cs5.cs5scheduler;

import java.util.ArrayList;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @(#)NodeVO
 *  * Copyright(c) 2015, Comviva technologies LTD.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author               Date            History
 *-------------------------------------------------------------------------------------------------
 * Karan Vijay Singh         30-Sept-2015     Initial Creation
 * ------------------------------------------------------------------------------------------------
 *
 */
public class NodeVO {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String _url;  //Defines the IP of the Node.
    private int _connectionTimeOut;//Defines the Connection time out of Node.
    private int _nodeNumber=1;//Defines the node number of the Interface
    private long _expiryDuration;//Defines the expiry time of the node.
    private int _valReadTimeOut=0;//Defines the read time out for the validation of node.
    private int _topReadTimeOut=0;//Defines the read time out for the validation of node.
    private int _maxConPerNode=0;//Defines the number of connection supported by single node.
    private int _maxSpanNodes=0;//Defines the number of nodes to be span in case of failure.
    private String _keepAlive="";//Keep Alive for the Node.
    private boolean _isBlocked;//This defines whether the node is blocked or not.If connection is timed out this flag is set to be TRUE.
    private long _blokedAt=0;//Time at which the NODE is blocked.
    private int _conNumber=0;//Defines the current connection number of the Node.
    private boolean _isMaxConReached=false;//set as true when maximum allowed connection for the node is reached.
    private ArrayList _transIdList=new ArrayList();//This list is used to contains the transaction id for connections of the NODE.
    private String _transactionId=null;//Each node contains the transaction id of request that is to be proccessed.
    private long _warnTime=0;//Defines the threshold time for the request and response interval.
    
    private boolean _isBlockedByReadTimeOut=false;
    private boolean _isBlockedByConTimeOut=false;
    
    private String _username=null;
    private String _password=null;
    
    private long _lastSuspendedAt;
    private long _suspendedAt;
    private boolean _isSuspended=false;
    private NodeCloser _nodeCloser=null;
    private String _interfaceStatus;//This would represent the current status of the interface.
    private String _interfacePrevStatus;//This would represent, a request before  status of the interface.
    private long _timeOfFirstAmbiguousTxn;//Contains the time at the first AMBIGUOUS transaction occurs.
    private long _currentAmbTxnTime;//represents current ambiguous txn time
    private int _numberOfAmbguousTxnAllowed;//This would represent the allowed no of Amb txn with specified duration.
    private long _thresholdTime;//Represents the Threshold time.
    private int _currentAmbTxnCounter;//Represent the current number of Ambiguous transaction number.
    private boolean _processSingleReqFlag = false;//flag is used to ensure that just after expiry only one request 
    									//would be sent to IN to check whether IN is resumed or not. Otherwise if
    									//IN is not Resumed many ambiguous case may occour.
    private String _IP = "";
    private String _Port = "";
    
    
    
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("_url =" + _url);
        sb.append(", _username="+_username);
        sb.append(", _password="+_password);
        sb.append(", _connectionTimeOut=" + _connectionTimeOut);
        sb.append(", _valReadTimeOut=" + _valReadTimeOut);
        sb.append(",_topReadTimeOut=" + _topReadTimeOut);
        sb.append(", _nodeNumber=" + _nodeNumber);
        sb.append(", _expiryTime=" + _expiryDuration);
        sb.append(", _keepAlive=" + _keepAlive);
        sb.append(", _maxNumAllowed=" + _maxSpanNodes);
        sb.append(", _isBlocked=" + _isBlocked);
        sb.append(", _maxAllowedConPerNode=" + _maxConPerNode);
        sb.append(", _blokedAt=" + _blokedAt);
        sb.append(", _conNumber=" + _conNumber);
        sb.append(", _isMaxConReached=" + _isMaxConReached);
        sb.append(", _transIdList=" + _transIdList);
        
        sb.append(", _lastSuspendedAt=" + _lastSuspendedAt);
        sb.append(", _suspendedAt=" + _suspendedAt);
        sb.append(", _isSuspended=" + _isSuspended);
        sb.append(", _timeOfFirstAmbiguousTxn=" + _timeOfFirstAmbiguousTxn);
        sb.append(", _currentAmbTxnTime=" + _currentAmbTxnTime);
        sb.append(", _numberOfAmbguousTxnAllowed=" + _numberOfAmbguousTxnAllowed);
        sb.append(", _thresholdTime=" + _thresholdTime);
        sb.append(", _currentAmbTxnCounter=" + _currentAmbTxnCounter);
        sb.append(", _processSingleReqFlag=" + _processSingleReqFlag);
       
        return sb.toString();
    }
    
    
    /**
     * @return Returns the connectionTimeOut.
     */
    public int getConnectionTimeOut() {
        return _connectionTimeOut;
    }
    /**
     * @param connectionTimeOut The connectionTimeOut to set.
     */
    public void setConnectionTimeOut(int connectionTimeOut) {
        _connectionTimeOut = connectionTimeOut;
    }
    /**
     * @return Returns the expiryTime.
     */
    public long getExpiryDuration() {
        return _expiryDuration;
    }
    /**
     * @param expiryDuration The expiryTime to set.
     */
    public void setExpiryDuration(long expiryDuration) {
        _expiryDuration = expiryDuration;
    }
    /**
     * @return Returns the ip.
     */
    public String getUrl() {
        return _url;
    }
    /**
     * @param ip The ip to set.
     */
    public void setUrl(String ip) {
        _url = ip;
    }
    /**
     * @return Returns the readTimeOut.
     */
    public int getValReadTimeOut() {
        return _valReadTimeOut;
    }
    /**
     * @param readTimeOut The readTimeOut to set.
     */
    public void setValReadTimeOut(int readTimeOut) {
        _valReadTimeOut = readTimeOut;
    }
    
    /**
     * @return Returns the keepAlive.
     */
    public String getKeepAlive() {
        return _keepAlive;
    }
    /**
     * @param keepAlive The keepAlive to set.
     */
    public void setKeepAlive(String keepAlive) {
        _keepAlive = keepAlive;
    }
    
    /**
     * @return Returns the maxNumAllowed.
     */
    public int getMaxSpanNodes() {
        return _maxSpanNodes;
    }
    /**
     * @param maxSpanNodes The maxNumAllowed to set.
     */
    public void setMaxSpanNodes(int maxSpanNodes) {
        _maxSpanNodes = maxSpanNodes;
    }
    
    /**
     * @return Returns the nodeNumber.
     */
    public int getNodeNumber() {
        return _nodeNumber;
    }
    /**
     * @param nodeNumber The nodeNumber to set.
     */
    public void setNodeNumber(int nodeNumber) {
        _nodeNumber = nodeNumber;
    }
    
    /**
     * @return Returns the isBlocked.
     */
    public boolean isBlocked() {
        return _isBlocked;
    }
    /**
     * @param isBlocked The isBlocked to set.
     */
    public void setBlocked(boolean isBlocked) {
        _isBlocked = isBlocked;
    }
    
    /**
     * @return Returns the maxAllowedConPerNode.
     */
    public int getMaxConPerNode() {
        return _maxConPerNode;
    }
    /**
     * @param maxConPerNode The maxAllowedConPerNode to set.
     */
    public void setMaxConPerNode(int maxConPerNode) {
        _maxConPerNode = maxConPerNode;
    }
    public void incrementNode()
    {
        _nodeNumber++;
    }
    
    /**
     * @return Returns the blokedAt.
     */
    public long getBlokedAt() {
        return _blokedAt;
    }
    /**
     * @param blokedAt The blokedAt to set.
     */
    public void setBlokedAt(long blokedAt) {
        _blokedAt = blokedAt;
    }
    
    /**
     * @return Returns the conNumberOfNode.
     */
    public int getConNumber() {
        return _conNumber;
    }
    /**
     * @param conNumberOfNode The conNumberOfNode to set.
     */
    public void setConNumber(int conNumberOfNode) {
        _conNumber = conNumberOfNode;
    }
    /**
     * This method increments the connection number associated with the node.
     * Also add the transactionId to transIdList, that acuired the node at that time. 
     * @param p_inTxnID
     */
    public void incrementConNumber(String p_inTxnID)
    {
        if(_transIdList.add(p_inTxnID))
        _log.info("incrementConNumber","p_inTxnID::"+p_inTxnID+" is added to the List");
        _conNumber++;
    }
    /**
     * This method is used to decrement the connection number after processing the request.
     * Also remove the transaction id which acquired the node from the transIdList.
     * @param p_inTxnID
     */
    public void decrementConNumber(String p_inTxnID)
    {
        //Removing the transaction id from the Node List.
        if(_transIdList.remove(p_inTxnID))
            _log.info("incrementConNumber","p_inTxnID::"+p_inTxnID+" is removed from the List");
        if(_conNumber>0)_conNumber--;
    }
    /**
     * @return Returns the isMaxConOfNodeReached.
     */
    public boolean isMaxConReached() {
        return _isMaxConReached;
    }
    /**
     * @param isMaxConReached The isMaxConOfNodeReached to set.
     */
    public void setMaxConReached(boolean isMaxConReached) {
        this._isMaxConReached = isMaxConReached;
    }
    
    /**
     * @return Returns the transIdList.
     */
    public ArrayList getTransIdList() {
        return _transIdList;
    }
    /**
     * @param transIdList The transIdList to set.
     */
    public void setTransIdList(ArrayList transIdList) {
        this._transIdList = transIdList;
    }
    
    /**
     * @return Returns the transactionId.
     */
    public String getTransactionId() {
        return _transactionId;
    }
    /**
     * @param transactionId The transactionId to set.
     */
    public void setTransactionId(String transactionId) {
        _transactionId = transactionId;
    }
    
    /**
     * @return Returns the warnTime.
     */
    public long getWarnTime() {
        return _warnTime;
    }
    /**
     * @param warnTime The warnTime to set.
     */
    public void setWarnTime(long warnTime) {
        _warnTime = warnTime;
    }
    
    /**
     * @return Returns the topReadTimeOut.
     */
    public int getTopReadTimeOut() {
        return _topReadTimeOut;
    }
    /**
     * @param topReadTimeOut The topReadTimeOut to set.
     */
    public void setTopReadTimeOut(int topReadTimeOut) {
        _topReadTimeOut = topReadTimeOut;
    }
    
    //============================================================================
    //Blocked by read time out OR connect time out
    public void setBlockedByReadTimeOut(boolean p_isBlockedByReadTimeOut) {
    	_isBlockedByReadTimeOut = p_isBlockedByReadTimeOut;
    }
    
    public boolean isBlockedByReadTimeOut() {
    	return _isBlockedByReadTimeOut;
    }
    
    public void setBlockedByConTimeOut(boolean p_isBlockedByConTimeOut) {
    	_isBlockedByConTimeOut = p_isBlockedByConTimeOut;
    }
    
    public boolean isBlockedByConTimeOut() {
    	return _isBlockedByConTimeOut;
    }
    //================================================================================
    
    
    
    
    /**
     * @return Returns the numberOfAmbguousTxnAllowed.
     */
    public int getNumberOfAmbguousTxnAllowed() {
        return _numberOfAmbguousTxnAllowed;
    }
    /**
     * @param numberOfAmbguousTxnAllowed The numberOfAmbguousTxnAllowed to set.
     */
    public void setNumberOfAmbguousTxnAllowed(int numberOfAmbguousTxnAllowed) {
        _numberOfAmbguousTxnAllowed = numberOfAmbguousTxnAllowed;
    }
    /**
     * @return Returns the thresholdTime.
     */
    public long getThresholdTime() {
        return _thresholdTime;
    }
    /**
     * @param thresholdTime The thresholdTime to set.
     */
    public void setThresholdTime(long thresholdTime) {
        _thresholdTime = thresholdTime;
    }
    /**
     * @return Returns the timeOfFirstAmbiguousTxn.
     */
    public long getTimeOfFirstAmbiguousTxn() {
        return _timeOfFirstAmbiguousTxn;
    }
    /**
     * @param timeOfFirstAmbiguousTxn The timeOfFirstAmbiguousTxn to set.
     */
    public void setTimeOfFirstAmbiguousTxn(long timeOfFirstAmbiguousTxn) {
        _timeOfFirstAmbiguousTxn = timeOfFirstAmbiguousTxn;
    }
    
    /**
     * @return Returns the NodeCloser.
     */
    public NodeCloser getNodeCloser() {
        return _nodeCloser;
    }
    /**
     * @param NodeCloser The NodeCloser to set.
     */
    public void setNodeCloser(NodeCloser nodeCloser) {
        _nodeCloser = nodeCloser;
    }
    
    /**
     * @return Returns the currentAmbTxnCounter.
     */
    public int getCurrentAmbTxnCounter() {
        return _currentAmbTxnCounter;
    }
    /**
     * @param currentAmbTxnCounter The currentAmbTxnCounter to set.
     */
    public void setCurrentAmbTxnCounter(int currentAmbTxnCounter) {
        _currentAmbTxnCounter = currentAmbTxnCounter;
    }
    /**
     * Increment the _currentAmbTxnCounter by one each time.
     *
     */
    public void incrementCurrentAmbTxnCounter()
    {
        ++_currentAmbTxnCounter;
    }
   
    /**
     * @return Returns the currentAmbTxnTime.
     */
    public long getCurrentAmbTxnTime() {
        return _currentAmbTxnTime;
    }
    /**
     * @param currentAmbTxnTime The currentAmbTxnTime to set.
     */
    public void setCurrentAmbTxnTime(long currentAmbTxnTime) {
        _currentAmbTxnTime = currentAmbTxnTime;
    }
    
    /**
     * @return Returns the _lastSuspendedAt.  LAST RETRY TIME
     */
    public long getLastSuspendedAt() {
        return _lastSuspendedAt;
    }
    /**
     * @param suspendAt The lastSuspendedAt to set.
     */
    
    public void setLastSuspendedAt(long lastSuspendedAt) {
    	_lastSuspendedAt = lastSuspendedAt;
    }
    
    
    /**
     * @return Returns the _suspendedAt.  LAST RETRY TIME
     */
    public long getSuspendedAt() {
        return _suspendedAt;
    }
    /**
     * @param suspendAt The suspendedAt to set.
     */
    
    public void setSuspendedAt(long suspendedAt) {
    	_suspendedAt = suspendedAt;
    }
    
    
    /**
     * @return Returns the isSuspended.
     */
    public boolean isSuspended() {
        return _isSuspended;
    }
    /**
     * @param isSuspended The isSuspended to set.
     */
    public void setSuspended(boolean isSuspended) {
        _isSuspended = isSuspended;
    }
    
    /**
     * @return Returns the expiryFlag.
     */
    public boolean getProcessSingleReqFlag() {
        return _processSingleReqFlag;
    }
    /**
     * 
     * @param expiryFlag The expiryFlag to set.
     */
    public void setProcessSingleReqFlag(boolean processSingleReqFlag) {
    	_processSingleReqFlag = processSingleReqFlag;
    }
	/**
	 * @return Returns the _password.
	 */
	public String getPassword() {
		return _password;
	}
	/**
	 * @param _password The _password to set.
	 */
	public void setPassword(String _password) {
		this._password = _password;
	}
	/**
	 * @return Returns the _username.
	 */
	public String getUsername() {
		return _username;
	}
	/**
	 * @param _username The _username to set.
	 */
	public void setUsername(String _username) {
		this._username = _username;
	}


	public String getIP() {
		return _IP;
	}


	public void setIP(String _ip) {
		_IP = _ip;
	}


	public String getPort() {
		return _Port;
	}


	public void setPort(String port) {
		_Port = port;
	}
}
