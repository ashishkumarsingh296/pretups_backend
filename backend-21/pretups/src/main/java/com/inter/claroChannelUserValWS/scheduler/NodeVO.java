package com.inter.claroChannelUserValWS.scheduler;

import java.util.ArrayList;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @(#)NodeVO
 * Copyright(c) 2013, Bharti Telesoft Int. Public Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author				Date			History
 *-------------------------------------------------------------------------------------------------
 * Vipan Kumar			Oct 11,2013     Initial Creation
 * 
 * ------------------------------------------------------------------------------------------------
 * 
 */
public class NodeVO {
    
	private Log _log = LogFactory.getLog(this.getClass().getName());
    private String _url;  //Defines the IP of the Node.
   
    private int _nodeNumber=1;//Defines the node number of the Interface
    private long _expiryDuration;//Defines the expiry time of the node.
    private int _ReadTimeOut=0;//Defines the read time out for the validation of node.
    
    //private int _maxConPerNode=0;//Defines the number of connection supported by single node.
    private int _maxSpanNodes=0;//Defines the number of nodes to be span in case of failure.
    private boolean _isBlocked;//This defines whether the node is blocked or not.If connection is timed out this flag is set to be TRUE.
    private long _blokedAt=0;//Time at which the NODE is blocked.
  
    //private int _conNumber=0;//Defines the current connection number of the Node.    
   // private boolean _isMaxConReached=false;//set as true when maximum allowed connection for the node is reached.
    
    private ArrayList _transIdList=new ArrayList();//This list is used to contains the transaction id for connections of the NODE.
    private String _transactionId=null;//Each node contains the transaction id of request that is to be proccessed.
    private long _warnTime=0;//Defines the threshold time for the request and response interval.
    
    private String _userName="";//User Name for the Node.
  
    private int _maxBarredCount=0; 
    private int _barredCount=0;
	private String _password="";
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(", _nodeNumber=" + _nodeNumber);
        sb.append(", _expiryTime=" + _expiryDuration);
        sb.append(", _isBlocked=" + _isBlocked);       
        sb.append(", _maxBarredCount=" + _maxBarredCount);
        sb.append(", _barredCount=" + _barredCount);
        sb.append(", _ReadTimeOut=" + _ReadTimeOut);       
        sb.append(", _maxNumAllowed=" + _maxSpanNodes);
        sb.append(", _blokedAt=" + _blokedAt);
        sb.append(", _transIdList=" + _transIdList);
        sb.append("_url =" + _url);
        sb.append(", _userName=" + _userName);
        sb.append(", _password=" + _password);
        return sb.toString();
    }

	/**
     * @return Returns the keepAlive.
     */
    public String getPassword() {
        return _password;
    }
    /**
     * @param keepAlive The keepAlive to set.
     */
    public void setPassword(String password) {
    	_password = password;
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
     * @return Returns the readTimeOut.
     */
    public int getMaxBarredCount() {
        return _maxBarredCount;
    }
    /**
     * @param readTimeOut The readTimeOut to set.
     */
    public void setMaxBarredCount(int maxBarredCount) {
    	_maxBarredCount = maxBarredCount;
    }
    
    /**
     * @return Returns the readTimeOut.
     */
    public int getBarredCount() {
        return _barredCount;
    }
    /**
     * @param readTimeOut The readTimeOut to set.
     */
    public void setBarredCount(int barredCount) {
    	_barredCount = barredCount;
    }
    
    public void incrementBarredCount()
    {
    	_barredCount++;
    }
    
    public void resetBarredCount()
    {		_barredCount=0;
    }
    /**
     * @return Returns the readTimeOut.
     */
    public int getReadTimeOut() {
        return _ReadTimeOut;
    }
    /**
     * @param readTimeOut The readTimeOut to set.
     */
    public void setReadTimeOut(int readTimeOut) {
        _ReadTimeOut = readTimeOut;
    }
    
    /**
     * @return Returns the keepAlive.
     */
    public String getUserName() {
        return _userName;
    }
    /**
     * @param keepAlive The keepAlive to set.
     */
    public void setUserName(String userName) {
        _userName = userName;
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
   /* public int getMaxConPerNode() {
        return _maxConPerNode;
    }*/
    /**
     * @param maxConPerNode The maxAllowedConPerNode to set.
     */
    /*public void setMaxConPerNode(int maxConPerNode) {
        _maxConPerNode = maxConPerNode;
    }*/
    
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
   /* public int getConNumber() {
        return _conNumber;
    }*/
    /**
     * @param conNumberOfNode The conNumberOfNode to set.
     */
    /*public void setConNumber(int conNumberOfNode) {
        _conNumber = conNumberOfNode;
    }*/
    /**
     * This method increments the connection number associated with the node.
     * Also add the transactionId to transIdList, that acuired the node at that time. 
     * @param p_inTxnID
     */
  /*  public void incrementConNumber(String p_inTxnID)
    {
        if(_transIdList.add(p_inTxnID))
        _log.info("incrementConNumber","p_inTxnID::"+p_inTxnID+" is added to the List");
        _conNumber++;
    }*/
    /**
     * This method is used to decrement the connection number after processing the request.
     * Also remove the transaction id which acquired the node from the transIdList.
     * @param p_inTxnID
     */
   /* public void decrementConNumber(String p_inTxnID)
    {
        //Removing the transaction id from the Node List.
        if(_transIdList.remove(p_inTxnID))
            _log.info("incrementConNumber","p_inTxnID::"+p_inTxnID+" is removed from the List");
        if(_conNumber>0)_conNumber--;
    }*/
    /**
     * @return Returns the isMaxConOfNodeReached.
     */
   /* public boolean isMaxConReached() {
        return _isMaxConReached;
    }*/
    /**
     * @param isMaxConReached The isMaxConOfNodeReached to set.
     */
    /*public void setMaxConReached(boolean isMaxConReached) {
        this._isMaxConReached = isMaxConReached;
    }*/
    
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
    
 
}
