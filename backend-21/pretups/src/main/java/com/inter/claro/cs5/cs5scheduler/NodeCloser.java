package com.inter.claro.cs5.cs5scheduler;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

/* 
* Copyright(c) 2016, Comviva Technologies LTD.
* All Rights Reserved
*-------------------------------------------------------------------------------------------------
* Author               Date            History
*-------------------------------------------------------------------------------------------------
* Sanjay Kumar Bind1       30-Sep-2016     Initial Creation
* ------------------------------------------------------------------------------------------------
*/

public class NodeCloser {
    private static final Log log = LogFactory.getLog(NodeCloser.class);
    
    
    /**
     * This method would check the expiry of suspended interface and does following.
     * A. Find the difference of time when the interface was suspended and CurrentRequestTime
     * 1. If the difference is greater than the configured expiry time, method throws exception.
     * @param p_interfaceCloserVO
     */
    public synchronized void checkExpiry(NodeVO pNodeVO) throws BTSLBaseException
    {
    	final String methodName = "checkExpiry";

        if(log.isDebugEnabled())
        	log.debug(methodName,PretupsI.ENTERED+" pNodeVO:"+pNodeVO);
        //flag to indicate whether expiry time is lapsed or not 
        boolean isSuspensionExpired=false;         
        try
        {
        	if(pNodeVO.getProcessSingleReqFlag())
        	{
        		log.error(methodName,"Interface Suspended.");
        		throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_SUSPENDED);
        	}        		
            long currentRequestTime=System.currentTimeMillis();
            log.error(methodName,"IcurrentRequestTime = " +currentRequestTime);
            //Check if difference of current time and last retry time is greater than expiry time or not.
            //if yes make the flag true. Depending on this flag  exception would be thrown (if value is false)
            //else request would be sent to IN 
            if((currentRequestTime-pNodeVO.getLastSuspendedAt())>pNodeVO.getExpiryDuration())
            {
            	//set the flag true. expiry time is lapsed
        		isSuspensionExpired=true;
         		//this will ensure that just after expiry, only one request would be sent to IN (to confirm whether IN is resumed or not). In between if any request comes,
        		//that will be refused by throwing exception while entering in checkExpiry method. After receiving response,
        		//p_interfaceCloserVO.setExpiryFlag() will be set to false as it's default value(In updateCountersOnAmbiguousResp and resetCounters method).
        		pNodeVO.setProcessSingleReqFlag(true); 
                                                        
            }
            //if flag is false, throw exception stating that expiry Interface Suspended and expiry time has not been expired.So not sending request to IN
            if(!isSuspensionExpired)
            {
            	log.error(methodName,"Interface Suspended and expiry time has not been expired.So not sending request to IN");
        		throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_SUSPENDED);
            }
        }
        catch(BTSLBaseException be)
		{
        	throw be;
		}
        catch(Exception e)
        {
            log.error(methodName,PretupsI.EXCEPTION+e.getMessage());
            throw e;
        }
        finally
        {
            if(log.isDebugEnabled())
            	log.debug(methodName,PretupsI.EXITED+" isSuspensionExpired:"+isSuspensionExpired);
        }        
    
    }
    

    /**
     * This method updates the counters when the response of request is obtained from interface is AMBIGUOUS.
     * The following parameters are updated
     *1.Check the First Ambiguous transaction time and does the following.
     *2.Current request time - First AMB time >= Threshold time.
     *	a.	Initialize the counters.
     *	b.	Update the first AMB transaction time.
     *3.Current request time - First AMB time < Threshold time
     *	a.	Increment the running counter
     *4.While incrementing the running counter, also check the following
     *	a.	If threshold reached, notify the controller to suspend the interface and also update the interface status as S.
     *	b.	Else increment the running counters in the memory.
     * @param	InterfaceCloserVO p_interfaceCloserVO
     */
    public synchronized void updateCountersOnAmbiguousResp(NodeVO pNodeVO)
    {
    	final String methodName = "updateCountersOnAmbiguousResp";
    	if(log.isDebugEnabled())
    		log.debug(methodName,PretupsI.ENTERED+" printNodeVO:"+printNodeVO(pNodeVO));   
        try
        {
        	pNodeVO.setCurrentAmbTxnTime(System.currentTimeMillis());
            long firstAmbTxnTime=pNodeVO.getTimeOfFirstAmbiguousTxn();
            long currentAmbTxnTime=pNodeVO.getCurrentAmbTxnTime();
            long thresholdTime=pNodeVO.getThresholdTime();            
            pNodeVO.incrementCurrentAmbTxnCounter();
            
            //There are two conditions when this 'if' block executes.
            //1. first ambiguous case (Interface is in Resumed state)
            //2. when threshold time is crossed (Interface is in Suspended state)
            if((currentAmbTxnTime-firstAmbTxnTime)>=thresholdTime) 
            {
            	//if interface is suspended and above mentioned condition (currentAmbTxnTime-firstAmbTxnTime)>=thresholdTime)is satisfied,
            	//set last retry time equal to current ambiuous txn time. 
            	if(pNodeVO.isSuspended())
                {                	
            		pNodeVO.setLastSuspendedAt(pNodeVO.getCurrentAmbTxnTime());      
                }
            	//otherwise if it is first ambiguous txn set last retry time to 0 and current ambiguous txn counter to 1.
            	//set the first ambiguous txn time.
            	else
                {
            		pNodeVO.setLastSuspendedAt(0);
            		pNodeVO.setCurrentAmbTxnCounter(1);
            		pNodeVO.setTimeOfFirstAmbiguousTxn(pNodeVO.getCurrentAmbTxnTime());
                    //This if block executes only when ambiguous txn counter threshold is 1. It means on first 
                    //ambiguous txn, interface will be suspended.
                    if(pNodeVO.getCurrentAmbTxnCounter()>=pNodeVO.getNumberOfAmbguousTxnAllowed())
                    {
                    	//suspend interface, set the first suspend time,
                    	//set last retry time to current ambiguous txn time. It is the first time when last retry time is set after initialiation
                    	//or reset of counters (in case of max ambiguous txn allowed=1) 
                    	pNodeVO.setSuspended(true);
                    	pNodeVO.setLastSuspendedAt(pNodeVO.getCurrentAmbTxnTime());
                    	pNodeVO.setSuspendedAt(pNodeVO.getCurrentAmbTxnTime());
                    }
                }
            }
            // This block executes when number of ambiguous txn is equal to or greater than max number of ambiguous txn allowed in threshold time
            else if((currentAmbTxnTime-firstAmbTxnTime)< thresholdTime && pNodeVO.getCurrentAmbTxnCounter()>=pNodeVO.getNumberOfAmbguousTxnAllowed())
            {
            	//this executes when when number of ambiguous txns are exactly equal to max allowed in a threshold time.
            	if(pNodeVO.getCurrentAmbTxnCounter()==pNodeVO.getNumberOfAmbguousTxnAllowed())
            	{
            		//Now suspend the interface and set First suspend time            		
            		pNodeVO.setSuspended(true);
            		pNodeVO.setSuspendedAt(pNodeVO.getCurrentAmbTxnTime());
            	}
            	//set last retry time to current ambiguous txn time. It is the first time when last retry time 
        		//is set after initialiation or reset of counters (in case of max ambiguous txn allowed>1) 
            	pNodeVO.setLastSuspendedAt(pNodeVO.getCurrentAmbTxnTime());
            	                      	
            }
            pNodeVO.setProcessSingleReqFlag(false);
            
            //logic for following if condition:
        	//if current interface status is SUSPEND(S) and previous status is either null or ACTIVE(A) 
        	//then only put the changed status in to map. 
        	//previous status 'null' is checked if number of max allowed amb txn=1. In this case prevoius status of 
        	//interface would be null.
        	
        }
        catch(Exception e)
        {
            log.error(methodName,PretupsI.EXCEPTION+e.getMessage());
            throw e;
        }
        finally
        {
            if(log.isDebugEnabled())
            	log.debug(methodName,PretupsI.EXITED+" printNodeVO:"+printNodeVO(pNodeVO));
        }
    
    }
    
    
    /**
     * This method updates the counters when the response of request is obtained from interface is SUCCESS or FAIL.
     * The following parameters are updated
     * 1. Reset the Interface Prev Status
     * @param p_interfaceCloserVO
     * 
     */
    public synchronized void updateCountersOnSuccessResp(NodeVO pNodeVO)
    {
    	pNodeVO.setSuspended(false);
    	   	
    }
    
    
    /**
     * This method resets the counters 
     * @param	NodeVO pNodeVO
     */
    
    public synchronized void resetCounters(NodeVO pNodeVO)
    {
    	final String methodName = "resetCounters";
        if(log.isDebugEnabled())
        	log.debug(methodName,PretupsI.ENTERED+" printNodeVO:"+printNodeVO(pNodeVO));
        try
        {
        	if(pNodeVO.isSuspended())
        		pNodeVO.setProcessSingleReqFlag(false);
        	
        	pNodeVO.setSuspended(false);
        	pNodeVO.setCurrentAmbTxnTime(0);
    		pNodeVO.setTimeOfFirstAmbiguousTxn(0);
            pNodeVO.setCurrentAmbTxnCounter(0);
            pNodeVO.setLastSuspendedAt(0);
            pNodeVO.setSuspendedAt(0);        	
        }
        catch(Exception e)
        {
            log.error(methodName,PretupsI.EXCEPTION+e.getMessage());
            throw e;
        }
        finally
        {
            if(log.isDebugEnabled())
            	log.debug(methodName,PretupsI.EXITED+" printNodeVO:"+printNodeVO(pNodeVO));
        }
    
    }
    
    public String printNodeVO(NodeVO pNodeVO)
    {
    	return pNodeVO.toString();
    }    
}
