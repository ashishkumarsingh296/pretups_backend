package com.btsl.pretups.inter.util;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;

/**
 * @InterfaceCloser.java
 *                       Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                       All Rights Reserved
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Ashish Kumar Mar 16, 2007 Initial Creation
 *                       ------------------------------------------------------
 *                       ------------------------------------------
 */
public class InterfaceCloser {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method would check the expiry of suspended interface and does
     * following.
     * A. Find the difference of time when the interface was suspended and
     * CurrentRequestTime
     * 1. If the difference is greater than the configured expiry time, method
     * throws exception.
     * 
     * @param p_interfaceCloserVO
     */
    public synchronized void checkExpiry(InterfaceCloserVO p_interfaceCloserVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkExpiry", "Entered p_interfaceCloserVO:" + p_interfaceCloserVO);
        // flag to indicate whether expiry time is lapsed or not
        boolean isSuspensionExpired = false;
        try {
            if (p_interfaceCloserVO.getExpiryFlag()) {
                _log.error("checkExpiry", "Interface Suspended.");
                throw new BTSLBaseException(this, "checkExpiry", InterfaceErrorCodesI.INTERFACE_SUSPENDED);
            }
            long currentRequestTime = System.currentTimeMillis();
            _log.error("checkExpiry", "IcurrentRequestTime = " + currentRequestTime);
            // Check if difference of current time and last retry time is
            // greater than expiry time or not.
            // if yes make the flag true. Depending on this flag exception would
            // be thrown (if value is false)
            // else request would be sent to IN
            if ((currentRequestTime - p_interfaceCloserVO.getSuspendAt()) > p_interfaceCloserVO.getExpiryTime()) {
                // set the flag true. expiry time is lapsed
                isSuspensionExpired = true;
                // this will ensure that just after expiry, only one request
                // would be sent to IN (to confirm whether IN is resumed or
                // not). In between if any request comes,
                // that will be refused by throwing exception while entering in
                // checkExpiry method. After receiving response,
                // p_interfaceCloserVO.setExpiryFlag() will be set to false as
                // it's default value(In updateCountersOnAmbiguousResp and
                // resetCounters method).
                p_interfaceCloserVO.setExpiryFlag(true);

            }
            // if flag is false, throw exception stating that expiry Interface
            // Suspended and expiry time has not been expired.So not sending
            // request to IN
            if (!isSuspensionExpired) {
                _log.error("checkExpiry", "Interface Suspended and expiry time has not been expired.So not sending request to IN");
                throw new BTSLBaseException(this, "checkExpiry", InterfaceErrorCodesI.INTERFACE_SUSPENDED);
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("checkExpiry", "Exception e:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("checkExpiry", "Exited isSuspensionExpired:" + isSuspensionExpired);
        }
    }

    /**
     * This method would check the expiry of suspended interface and does
     * following.
     * A. Find the difference of time when the interface was suspended and
     * CurrentRequestTime
     * 1. If the difference is greater than the configured expiry time, method
     * throws exception.
     * 
     * @param p_interfaceCloserVO
     */
    public synchronized void checkExpiryWithoutExpiryFlag(InterfaceCloserVO p_interfaceCloserVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkExpiryWithoutExpiryFlag", "Entered p_interfaceCloserVO:" + p_interfaceCloserVO);
        // flag to indicate whether expiry time is lapsed or not
        boolean isSuspensionExpired = false;
        try {
            long currentRequestTime = System.currentTimeMillis();
            _log.error("checkExpiryWithoutExpiryFlag", "IcurrentRequestTime = " + currentRequestTime);
            // Check if difference of current time and last retry time is
            // greater than expiry time or not.
            // if yes make the flag true. Depending on this flag exception would
            // be thrown (if value is false)
            // else request would be sent to IN
            if ((currentRequestTime - p_interfaceCloserVO.getSuspendAt()) > p_interfaceCloserVO.getExpiryTime()) {
                // set the flag true. expiry time is lapsed
                isSuspensionExpired = true;
            }
            // if flag is false, throw exception stating that expiry Interface
            // Suspended and expiry time has not been expired.So not sending
            // request to IN
            if (!isSuspensionExpired) {
                _log.error("checkExpiryWithoutExpiryFlag", "Interface Suspended and expiry time has not been expired.So not sending request to IN");
                throw new BTSLBaseException(this, "checkExpiryWithoutExpiryFlag", InterfaceErrorCodesI.INTERFACE_SUSPENDED);
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("checkExpiryWithoutExpiryFlag", "Exception e:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("checkExpiryWithoutExpiryFlag", "Exited isSuspensionExpired:" + isSuspensionExpired);
        }
    }

    /**
     * This method updates the counters when the response of request is obtained
     * from interface is AMBIGUOUS.
     * The following parameters are updated
     * 1.Check the First Ambiguous transaction time and does the following.
     * 2.Current request time - First AMB time >= Threshold time.
     * a. Initialize the counters.
     * b. Update the first AMB transaction time.
     * 3.Current request time - First AMB time < Threshold time
     * a. Increment the running counter
     * 4.While incrementing the running counter, also check the following
     * a. If threshold reached, notify the controller to suspend the interface
     * and also update the interface status as S.
     * b. Else increment the running counters in the memory.
     * 
     * @param InterfaceCloserVO
     *            p_interfaceCloserVO
     */
    public synchronized void updateCountersOnAmbiguousResp(InterfaceCloserVO p_interfaceCloserVO, HashMap p_requestMap) {

        if (_log.isDebugEnabled())
            _log.debug("updateCountersOnAmbiguousResp", "Entered printInterfaceCloserVO:" + printInterfaceCloserVO(p_interfaceCloserVO));
        try {
            p_interfaceCloserVO.setCurrentAmbTxnTime(System.currentTimeMillis());
            long firstAmbTxnTime = p_interfaceCloserVO.getTimeOfFirstAmbiguousTxn();
            long currentAmbTxnTime = p_interfaceCloserVO.getCurrentAmbTxnTime();
            long thresholdTime = p_interfaceCloserVO.getThresholdTime();
            p_interfaceCloserVO.incrementCurrentAmbTxnCounter();

            // There are two conditions when this 'if' block executes.
            // 1. first ambiguous case (Interface is in Resumed state)
            // 2. when threshold time is crossed (Interface is in Suspended
            // state)
            if ((currentAmbTxnTime - firstAmbTxnTime) >= thresholdTime) {
                // if interface is suspended and above mentioned condition
                // (currentAmbTxnTime-firstAmbTxnTime)>=thresholdTime)is
                // satisfied,
                // set last retry time equal to current ambiuous txn time.
                if (p_interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND)) {
                    p_interfaceCloserVO.setSuspendAt(p_interfaceCloserVO.getCurrentAmbTxnTime());
                }
                // otherwise if it is first ambiguous txn set last retry time to
                // 0 and current ambiguous txn counter to 1.
                // set the first ambiguous txn time.
                else {
                    p_interfaceCloserVO.setSuspendAt(0);
                    p_interfaceCloserVO.setCurrentAmbTxnCounter(1);
                    p_interfaceCloserVO.setTimeOfFirstAmbiguousTxn(p_interfaceCloserVO.getCurrentAmbTxnTime());
                    // This if block executes only when ambiguous txn counter
                    // threshold is 1. It means on first
                    // ambiguous txn, interface will be suspended.
                    if (p_interfaceCloserVO.getCurrentAmbTxnCounter() >= p_interfaceCloserVO.getNumberOfAmbguousTxnAllowed()) {
                        // suspend interface, set the first suspend time,
                        // set last retry time to current ambiguous txn time. It
                        // is the first time when last retry time is set after
                        // initialiation
                        // or reset of counters (in case of max ambiguous txn
                        // allowed=1)
                        p_interfaceCloserVO.setInterfaceStatus(InterfaceCloserI.INTERFACE_SUSPEND);
                        p_interfaceCloserVO.setFirstSuspendAt(p_interfaceCloserVO.getCurrentAmbTxnTime());
                        p_interfaceCloserVO.setSuspendAt(p_interfaceCloserVO.getCurrentAmbTxnTime());
                    }
                }
            }
            // This block executes when number of ambiguous txn is equal to or
            // greater than max number of ambiguous txn allowed in threshold
            // time
            else if ((currentAmbTxnTime - firstAmbTxnTime) < thresholdTime && p_interfaceCloserVO.getCurrentAmbTxnCounter() >= p_interfaceCloserVO.getNumberOfAmbguousTxnAllowed()) {
                // this executes when when number of ambiguous txns are exactly
                // equal to max allowed in a threshold time.
                if (p_interfaceCloserVO.getCurrentAmbTxnCounter() == p_interfaceCloserVO.getNumberOfAmbguousTxnAllowed()) {
                    // Now suspend the interface and set First suspend time
                    p_interfaceCloserVO.setInterfaceStatus(InterfaceCloserI.INTERFACE_SUSPEND);
                    p_interfaceCloserVO.setFirstSuspendAt(p_interfaceCloserVO.getCurrentAmbTxnTime());
                }
                // set last retry time to current ambiguous txn time. It is the
                // first time when last retry time
                // is set after initialiation or reset of counters (in case of
                // max ambiguous txn allowed>1)
                p_interfaceCloserVO.setSuspendAt(p_interfaceCloserVO.getCurrentAmbTxnTime());

            }
            p_interfaceCloserVO.setExpiryFlag(false);
            // logic for following if condition:
            // if current interface status is SUSPEND(S) and previous status is
            // either null or ACTIVE(A)
            // then only put the changed status in to map.
            // previous status 'null' is checked if number of max allowed amb
            // txn=1. In this case prevoius status of
            // interface would be null.
            if (InterfaceCloserI.INTERFACE_SUSPEND.equals(p_interfaceCloserVO.getInterfaceStatus()) && (InterfaceUtil.isNullString(p_interfaceCloserVO.getInterfacePrevStatus()) || InterfaceCloserI.INTERFACE_AUTO_ACTIVE.equals(p_interfaceCloserVO.getInterfacePrevStatus())))
                p_requestMap.put("INT_SET_STATUS", InterfaceCloserI.INTERFACE_SUSPEND);
            //
            p_interfaceCloserVO.setInterfacePrevStatus(p_interfaceCloserVO.getInterfaceStatus());
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("updateCountersOnAmbiguousResp", "Exception e:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("updateCountersOnAmbiguousResp", "Exited printInterfaceCloserVO:" + printInterfaceCloserVO(p_interfaceCloserVO));
        }
    }

    /**
     * This method updates the counters when the response of request is obtained
     * from interface is SUCCESS or FAIL.
     * The following parameters are updated
     * 1. Reset the Interface Prev Status
     * 
     * @param p_interfaceCloserVO
     * 
     */
    public synchronized void updateCountersOnSuccessResp(InterfaceCloserVO p_interfaceCloserVO) {
        if (_log.isDebugEnabled())
            _log.debug("updateCountersOnSuccessResp", "Entered p_interfaceCloserVO:" + p_interfaceCloserVO);
        try {
            // Reset the Interface Prev Status
            p_interfaceCloserVO.setInterfacePrevStatus(p_interfaceCloserVO.getInterfaceStatus());
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("updateCountersOnSuccessResp", "Exception e:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("updateCountersOnSuccessResp", "Exited");
        }
    }

    /**
     * This method resets the counters
     * 
     * @param InterfaceCloserVO
     *            p_interfaceCloserVO
     */

    public synchronized void resetCounters(InterfaceCloserVO p_interfaceCloserVO, HashMap p_requestMap) {
        if (_log.isDebugEnabled())
            _log.debug("resetCounters", "Entered printInterfaceCloserVO:" + printInterfaceCloserVO(p_interfaceCloserVO));
        try {
            // this if block executes when reset is called from
            // checkInterfaceB4SendingRequest method of INHandler
            if (InterfaceCloserI.INTERFACE_MANNUAL_ACTIVE.equals(p_interfaceCloserVO.getControllerIntStatus()) && p_interfaceCloserVO.getFirstSuspendAt() != 0)
                p_interfaceCloserVO.setInterfacePrevStatus(null);
            // this if block executes when reset counters method is called from
            // sendRequestToIN method of INHandler
            else if (InterfaceCloserI.INTERFACE_SUSPEND.equals(p_interfaceCloserVO.getInterfaceStatus())) {
                p_requestMap.put("INT_SET_STATUS", InterfaceCloserI.INTERFACE_RESUME);
                p_interfaceCloserVO.setExpiryFlag(false);
            }
            p_interfaceCloserVO.setInterfaceStatus(InterfaceCloserI.INTERFACE_AUTO_ACTIVE);
            p_interfaceCloserVO.setCurrentAmbTxnTime(0);
            p_interfaceCloserVO.setTimeOfFirstAmbiguousTxn(0);
            p_interfaceCloserVO.setCurrentAmbTxnCounter(0);
            p_interfaceCloserVO.setSuspendAt(0);
            p_interfaceCloserVO.setFirstSuspendAt(0);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("resetCounters", "Exception e:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("resetCounters", "Exited printInterfaceCloserVO:" + printInterfaceCloserVO(p_interfaceCloserVO));
        }
    }

    public String printInterfaceCloserVO(InterfaceCloserVO p_interfaceCloserVO) {
        if (_log.isDebugEnabled())
            _log.debug("printInterfaceCloserVO", "Entered p_interfaceCloserVO:" + p_interfaceCloserVO);
        StringBuffer buff = null;
        buff = new StringBuffer("getControllerIntStatus:" + p_interfaceCloserVO.getControllerIntStatus());
        buff.append(",getExpiryTime:" + p_interfaceCloserVO.getExpiryTime());
        buff.append(",getInterfaceStatus:" + p_interfaceCloserVO.getInterfaceStatus());
        buff.append(",getInterfacePrevStatus:" + p_interfaceCloserVO.getInterfacePrevStatus());
        buff.append(",getNumberOfAmbguousTxnAllowed:" + p_interfaceCloserVO.getNumberOfAmbguousTxnAllowed());
        buff.append(",getThresholdTime:" + p_interfaceCloserVO.getThresholdTime());
        buff.append(",getTimeOfFirstAmbiguousTxn:" + p_interfaceCloserVO.getTimeOfFirstAmbiguousTxn());
        buff.append(",getCurrentAmbTxnCounter:" + p_interfaceCloserVO.getCurrentAmbTxnCounter());
        buff.append(",getCurrentAmbTxnTime:" + p_interfaceCloserVO.getCurrentAmbTxnTime());
        buff.append(",getSuspendAt:" + p_interfaceCloserVO.getSuspendAt());
        buff.append(",getFirstSuspendAt:" + p_interfaceCloserVO.getFirstSuspendAt());
        // buff.append(",getControllerIntPrevStatus:"+p_interfaceCloserVO.getControllerIntPrevStatus());
        if (_log.isDebugEnabled())
            _log.debug("printInterfaceCloserVO", "Exited");
        return buff.toString();
    }
}
