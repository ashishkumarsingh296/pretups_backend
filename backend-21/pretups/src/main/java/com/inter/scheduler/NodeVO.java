package com.inter.scheduler;

import java.util.ArrayList;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @(#)NodeVO
 *            Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *            All Rights Reserved
 *            ------------------------------------------------------------------
 *            -------------------------------
 *            Author Date History
 *            ------------------------------------------------------------------
 *            -------------------------------
 *            Ashish Kumar Sep 06,2006 Initial Creation
 *            ------------------------------------------------------------------
 *            ------------------------------
 * 
 */
public class NodeVO {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String _url; // Defines the IP of the Node.
    private int _connectionTimeOut;// Defines the Connection time out of Node.
    private int _nodeNumber = 1;// Defines the node number of the Interface
    private long _expiryDuration;// Defines the expiry time of the node.
    private int _valReadTimeOut = 0;// Defines the read time out for the
                                    // validation of node.
    private int _topReadTimeOut = 0;// Defines the read time out for the
                                    // validation of node.
    private int _maxConPerNode = 0;// Defines the number of connection supported
                                   // by single node.
    private int _maxSpanNodes = 0;// Defines the number of nodes to be span in
                                  // case of failure.
    private String _keepAlive = "";// Keep Alive for the Node.
    private boolean _isBlocked;// This defines whether the node is blocked or
                               // not.If connection is timed out this flag is
                               // set to be TRUE.
    private long _blokedAt = 0;// Time at which the NODE is blocked.
    private int _conNumber = 0;// Defines the current connection number of the
                               // Node.
    private boolean _isMaxConReached = false;// set as true when maximum allowed
                                             // connection for the node is
                                             // reached.
    private ArrayList _transIdList = new ArrayList();// This list is used to
                                                     // contains the transaction
                                                     // id for connections of
                                                     // the NODE.
    private String _transactionId = null;// Each node contains the transaction
                                         // id of request that is to be
                                         // proccessed.
    private long _warnTime = 0;// Defines the threshold time for the request and
                               // response interval.

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("_url =" + _url);
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
        return sb.toString();
    }

    /**
     * @return Returns the connectionTimeOut.
     */
    public int getConnectionTimeOut() {
        return _connectionTimeOut;
    }

    /**
     * @param connectionTimeOut
     *            The connectionTimeOut to set.
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
     * @param expiryDuration
     *            The expiryTime to set.
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
     * @param ip
     *            The ip to set.
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
     * @param readTimeOut
     *            The readTimeOut to set.
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
     * @param keepAlive
     *            The keepAlive to set.
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
     * @param maxSpanNodes
     *            The maxNumAllowed to set.
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
     * @param nodeNumber
     *            The nodeNumber to set.
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
     * @param isBlocked
     *            The isBlocked to set.
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
     * @param maxConPerNode
     *            The maxAllowedConPerNode to set.
     */
    public void setMaxConPerNode(int maxConPerNode) {
        _maxConPerNode = maxConPerNode;
    }

    public void incrementNode() {
        _nodeNumber++;
    }

    /**
     * @return Returns the blokedAt.
     */
    public long getBlokedAt() {
        return _blokedAt;
    }

    /**
     * @param blokedAt
     *            The blokedAt to set.
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
     * @param conNumberOfNode
     *            The conNumberOfNode to set.
     */
    public void setConNumber(int conNumberOfNode) {
        _conNumber = conNumberOfNode;
    }

    /**
     * This method increments the connection number associated with the node.
     * Also add the transactionId to transIdList, that acuired the node at that
     * time.
     * 
     * @param p_inTxnID
     */
    public void incrementConNumber(String p_inTxnID) {
        if (_transIdList.add(p_inTxnID))
            _log.info("incrementConNumber", "p_inTxnID::" + p_inTxnID + " is added to the List");
        _conNumber++;
    }

    /**
     * This method is used to decrement the connection number after processing
     * the request.
     * Also remove the transaction id which acquired the node from the
     * transIdList.
     * 
     * @param p_inTxnID
     */
    public void decrementConNumber(String p_inTxnID) {
        // Removing the transaction id from the Node List.
        if (_transIdList.remove(p_inTxnID))
            _log.info("incrementConNumber", "p_inTxnID::" + p_inTxnID + " is removed from the List");
        if (_conNumber > 0)
            _conNumber--;
    }

    /**
     * @return Returns the isMaxConOfNodeReached.
     */
    public boolean isMaxConReached() {
        return _isMaxConReached;
    }

    /**
     * @param isMaxConReached
     *            The isMaxConOfNodeReached to set.
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
     * @param transIdList
     *            The transIdList to set.
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
     * @param transactionId
     *            The transactionId to set.
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
     * @param warnTime
     *            The warnTime to set.
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
     * @param topReadTimeOut
     *            The topReadTimeOut to set.
     */
    public void setTopReadTimeOut(int topReadTimeOut) {
        _topReadTimeOut = topReadTimeOut;
    }
}
