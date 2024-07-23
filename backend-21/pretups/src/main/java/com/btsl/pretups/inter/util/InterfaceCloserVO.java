package com.btsl.pretups.inter.util;

/**
 * @InterfaceCloserVO.java
 *                         Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Ashish Kumar Mar 16, 2007 Initial Creation
 *                         ----------------------------------------------------
 *                         --------------------------------------------
 * 
 *                         This class would store the current running counter
 *                         for an interface and its current status.
 * 
 */
public class InterfaceCloserVO {

    private String _interfaceStatus;// This would represent the current status
                                    // of the interface.
    private String _interfacePrevStatus;// This would represent, a request
                                        // before status of the interface.
    private long _timeOfFirstAmbiguousTxn;// Contains the time at the first
                                          // AMBIGUOUS transaction occurs.
    private long _currentAmbTxnTime;// represents current ambiguous txn time
    private long _suspendAt;// Represent the time of last retry made when the
                            // interface was suspended.
    private long _firstSuspendAt;// represents when interface first suspended
    private int _numberOfAmbguousTxnAllowed;// This would represent the allowed
                                            // no of Amb txn with specified
                                            // duration.
    private long _thresholdTime;// Represents the Threshold time.
    private long _expiryTime;// Represent the expiry time after which the
                             // interface is expected to be be resumed.
    private InterfaceCloser _interfaceCloser; // Represent the instance of
                                              // InterfaceCloser class.
    private int _currentAmbTxnCounter;// Represent the current number of
                                      // Ambiguous transaction number.
    private String _controllerIntStatus;// Represents Interface status at
                                        // controller
    private String _controllerIntPrevStatus; // Represents a request before
                                             // Interface status at controller
    private boolean _expiryFlag = false;// flag is used to ensure that just
                                        // after expiry only one request
                                        // would be sent to IN to check whether
                                        // IN is resumed or not. Otherwise if
                                        // IN is not Resumed many ambiguous case
                                        // may occour.

    /**
     * @return Returns the expiryFlag.
     */
    public boolean getExpiryFlag() {
        return _expiryFlag;
    }

    /**
     * 
     * @param expiryFlag
     *            The expiryFlag to set.
     */
    public void setExpiryFlag(boolean expiryFlag) {
        _expiryFlag = expiryFlag;
    }

    /**
     * @return Returns the controller Interface previous Status.
     */
    public String getControllerIntPrevStatus() {
        return _controllerIntPrevStatus;
    }

    /**
     * 
     * @param controllerIntPrevStatus
     *            The controllerIntPrevStatus to set.
     */
    public void setControllerIntPrevStatus(String controllerIntPrevStatus) {
        _controllerIntPrevStatus = controllerIntPrevStatus;
    }

    /**
     * @return Returns the controller Interface Status.
     */
    public String getControllerIntStatus() {
        return _controllerIntStatus;
    }

    /**
     * 
     * @param controllerIntStatus
     *            The controllerIntStatus to set.
     */
    public synchronized void setControllerIntStatus(String controllerIntStatus) {
        _controllerIntStatus = controllerIntStatus;
    }

    /**
     * @return Returns the expiryTime.
     */
    public long getExpiryTime() {
        return _expiryTime;
    }

    /**
     * This should be updated only when the FileCache is update.
     * 
     * @param expiryTime
     *            The expiryTime to set.
     */
    public void setExpiryTime(long expiryTime) {
        _expiryTime = expiryTime;
    }

    /**
     * @return Returns the interfaceStatus.
     */
    public String getInterfaceStatus() {
        return _interfaceStatus;
    }

    /**
     * @param interfaceStatus
     *            The interfaceStatus to set.
     */
    public void setInterfaceStatus(String interfaceStatus) {
        _interfaceStatus = interfaceStatus;
    }

    /**
     * @return Returns the interfacePrevStatus.
     */
    public String getInterfacePrevStatus() {
        return _interfacePrevStatus;
    }

    /**
     * @param interfacePrevStatus
     *            The interfacePrevStatus to set.
     */
    public void setInterfacePrevStatus(String interfacePrevStatus) {
        _interfacePrevStatus = interfacePrevStatus;
    }

    /**
     * @return Returns the numberOfAmbguousTxnAllowed.
     */
    public int getNumberOfAmbguousTxnAllowed() {
        return _numberOfAmbguousTxnAllowed;
    }

    /**
     * @param numberOfAmbguousTxnAllowed
     *            The numberOfAmbguousTxnAllowed to set.
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
     * @param thresholdTime
     *            The thresholdTime to set.
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
     * @param timeOfFirstAmbiguousTxn
     *            The timeOfFirstAmbiguousTxn to set.
     */
    public void setTimeOfFirstAmbiguousTxn(long timeOfFirstAmbiguousTxn) {
        _timeOfFirstAmbiguousTxn = timeOfFirstAmbiguousTxn;
    }

    /**
     * @return Returns the interfaceCloser.
     */
    public InterfaceCloser getInterfaceCloser() {
        return _interfaceCloser;
    }

    /**
     * @param interfaceCloser
     *            The interfaceCloser to set.
     */
    public void setInterfaceCloser(InterfaceCloser interfaceCloser) {
        _interfaceCloser = interfaceCloser;
    }

    /**
     * @return Returns the currentAmbTxnCounter.
     */
    public int getCurrentAmbTxnCounter() {
        return _currentAmbTxnCounter;
    }

    /**
     * @param currentAmbTxnCounter
     *            The currentAmbTxnCounter to set.
     */
    public void setCurrentAmbTxnCounter(int currentAmbTxnCounter) {
        _currentAmbTxnCounter = currentAmbTxnCounter;
    }

    /**
     * Increment the _currentAmbTxnCounter by one each time.
     * 
     */
    public void incrementCurrentAmbTxnCounter() {
        ++_currentAmbTxnCounter;
    }

    /**
     * @return Returns the currentAmbTxnTime.
     */
    public long getCurrentAmbTxnTime() {
        return _currentAmbTxnTime;
    }

    /**
     * @param currentAmbTxnTime
     *            The currentAmbTxnTime to set.
     */
    public void setCurrentAmbTxnTime(long currentAmbTxnTime) {
        _currentAmbTxnTime = currentAmbTxnTime;
    }

    /**
     * @return Returns the suspendAt.
     */
    public long getFirstSuspendAt() {
        return _firstSuspendAt;
    }

    /**
     * @param suspendAt
     *            The suspendAt to set.
     */

    public void setFirstSuspendAt(long firstSuspendAt) {
        _firstSuspendAt = firstSuspendAt;
    }

    /**
     * @return Returns the suspendAt.
     */
    public long getSuspendAt() {
        return _suspendAt;
    }

    /**
     * @param suspendAt
     *            The suspendAt to set.
     */
    public void setSuspendAt(long suspendAt) {
        _suspendAt = suspendAt;
    }
}
