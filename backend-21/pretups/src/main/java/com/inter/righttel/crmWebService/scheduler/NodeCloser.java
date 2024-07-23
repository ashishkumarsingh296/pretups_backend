package com.inter.righttel.crmWebService.scheduler;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

/* 
* Copyright(c) 2015, Comviva technologies LTD.
* All Rights Reserved
*-------------------------------------------------------------------------------------------------
* Author               Date            History
*-------------------------------------------------------------------------------------------------
* Karan Vijay Singh        30-Sep-2015     Initial Creation
* ------------------------------------------------------------------------------------------------
*/

public class NodeCloser {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    
    
    /**
     * This method would check the expiry of suspended interface and does following.
     * A. Find the difference of time when the interface was suspended and CurrentRequestTime
     * 1. If the difference is greater than the configured expiry time, method throws exception.
     * @param p_interfaceCloserVO
     */
    public synchronized void checkExpiry(NodeVO p_nodeVO) throws BTSLBaseException
    {

        if(_log.isDebugEnabled()) _log.debug("checkExpiry","Entered p_nodeVO:"+p_nodeVO);
        //flag to indicate whether expiry time is lapsed or not 
        boolean isSuspensionExpired=false;         
        try
        {
        	if(p_nodeVO.getProcessSingleReqFlag())
        	{
        		_log.error("checkExpiry","Interface Suspended.");
        		throw new BTSLBaseException(this,"checkExpiry",InterfaceErrorCodesI.INTERFACE_SUSPENDED);
        	}        		
            long currentRequestTime=System.currentTimeMillis();
            _log.error("checkExpiry","IcurrentRequestTime = " +currentRequestTime);
            //Check if difference of current time and last retry time is greater than expiry time or not.
            //if yes make the flag true. Depending on this flag  exception would be thrown (if value is false)
            //else request would be sent to IN 
            if((currentRequestTime-p_nodeVO.getLastSuspendedAt())>p_nodeVO.getExpiryDuration())
            {
            	//set the flag true. expiry time is lapsed
        		isSuspensionExpired=true;
         		//this will ensure that just after expiry, only one request would be sent to IN (to confirm whether IN is resumed or not). In between if any request comes,
        		//that will be refused by throwing exception while entering in checkExpiry method. After receiving response,
        		//p_interfaceCloserVO.setExpiryFlag() will be set to false as it's default value(In updateCountersOnAmbiguousResp and resetCounters method).
        		p_nodeVO.setProcessSingleReqFlag(true); 
                                                        
            }
            //if flag is false, throw exception stating that expiry Interface Suspended and expiry time has not been expired.So not sending request to IN
            if(!isSuspensionExpired)
            {
            	_log.error("checkExpiry","Interface Suspended and expiry time has not been expired.So not sending request to IN");
        		throw new BTSLBaseException(this,"checkExpiry",InterfaceErrorCodesI.INTERFACE_SUSPENDED);
            }
        }
        catch(BTSLBaseException be)
		{
        	throw be;
		}
        catch(Exception e)
        {
            e.printStackTrace();
            _log.error("checkExpiry","Exception e:"+e.getMessage());
        }
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("checkExpiry","Exited isSuspensionExpired:"+isSuspensionExpired);
        }        
    
    }
    
    /**
     * This method would check the expiry of suspended interface and does following.
     * A. Find the difference of time when the interface was suspended and CurrentRequestTime
     * 1. If the difference is greater than the configured expiry time, method throws exception.
     * @param p_interfaceCloserVO
     */
    public synchronized void checkExpiryWithoutExpiryFlag(NodeVO p_nodeVO) throws BTSLBaseException
    {}
    
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
    public synchronized void updateCountersOnAmbiguousResp(NodeVO p_nodeVO)
    {
    	if(_log.isDebugEnabled()) _log.debug("updateCountersOnAmbiguousResp","Entered printNodeVO:"+printNodeVO(p_nodeVO));   
        try
        {
        	p_nodeVO.setCurrentAmbTxnTime(System.currentTimeMillis());
            long firstAmbTxnTime=p_nodeVO.getTimeOfFirstAmbiguousTxn();
            long currentAmbTxnTime=p_nodeVO.getCurrentAmbTxnTime();
            long thresholdTime=p_nodeVO.getThresholdTime();            
            p_nodeVO.incrementCurrentAmbTxnCounter();
            
            //There are two conditions when this 'if' block executes.
            //1. first ambiguous case (Interface is in Resumed state)
            //2. when threshold time is crossed (Interface is in Suspended state)
            if((currentAmbTxnTime-firstAmbTxnTime)>=thresholdTime) 
            {
            	//if interface is suspended and above mentioned condition (currentAmbTxnTime-firstAmbTxnTime)>=thresholdTime)is satisfied,
            	//set last retry time equal to current ambiuous txn time. 
            	if(p_nodeVO.isSuspended())
                {                	
            		p_nodeVO.setLastSuspendedAt(p_nodeVO.getCurrentAmbTxnTime());      
                }
            	//otherwise if it is first ambiguous txn set last retry time to 0 and current ambiguous txn counter to 1.
            	//set the first ambiguous txn time.
            	else
                {
            		p_nodeVO.setLastSuspendedAt(0);
            		p_nodeVO.setCurrentAmbTxnCounter(1);
            		p_nodeVO.setTimeOfFirstAmbiguousTxn(p_nodeVO.getCurrentAmbTxnTime());
                    //This if block executes only when ambiguous txn counter threshold is 1. It means on first 
                    //ambiguous txn, interface will be suspended.
                    if(p_nodeVO.getCurrentAmbTxnCounter()>=p_nodeVO.getNumberOfAmbguousTxnAllowed())
                    {
                    	//suspend interface, set the first suspend time,
                    	//set last retry time to current ambiguous txn time. It is the first time when last retry time is set after initialiation
                    	//or reset of counters (in case of max ambiguous txn allowed=1) 
                    	p_nodeVO.setSuspended(true);
                    	p_nodeVO.setLastSuspendedAt(p_nodeVO.getCurrentAmbTxnTime());
                    	p_nodeVO.setSuspendedAt(p_nodeVO.getCurrentAmbTxnTime());
                    }
                }
            }
            // This block executes when number of ambiguous txn is equal to or greater than max number of ambiguous txn allowed in threshold time
            else if((currentAmbTxnTime-firstAmbTxnTime)< thresholdTime && p_nodeVO.getCurrentAmbTxnCounter()>=p_nodeVO.getNumberOfAmbguousTxnAllowed())
            {
            	//this executes when when number of ambiguous txns are exactly equal to max allowed in a threshold time.
            	if(p_nodeVO.getCurrentAmbTxnCounter()==p_nodeVO.getNumberOfAmbguousTxnAllowed())
            	{
            		//Now suspend the interface and set First suspend time            		
            		p_nodeVO.setSuspended(true);
            		p_nodeVO.setSuspendedAt(p_nodeVO.getCurrentAmbTxnTime());
            	}
            	//set last retry time to current ambiguous txn time. It is the first time when last retry time 
        		//is set after initialiation or reset of counters (in case of max ambiguous txn allowed>1) 
            	p_nodeVO.setLastSuspendedAt(p_nodeVO.getCurrentAmbTxnTime());
            	                      	
            }
            p_nodeVO.setProcessSingleReqFlag(false);
            
            //logic for following if condition:
        	//if current interface status is SUSPEND(S) and previous status is either null or ACTIVE(A) 
        	//then only put the changed status in to map. 
        	//previous status 'null' is checked if number of max allowed amb txn=1. In this case prevoius status of 
        	//interface would be null.
        	
            //p_nodeVO.setInterfacePrevStatus(p_nodeVO.getInterfaceStatus());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            _log.error("updateCountersOnAmbiguousResp","Exception e:"+e.getMessage());
        }
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("updateCountersOnAmbiguousResp","Exited printNodeVO:"+printNodeVO(p_nodeVO));
        }
    
    }
    
    
    /**
     * This method updates the counters when the response of request is obtained from interface is SUCCESS or FAIL.
     * The following parameters are updated
     * 1. Reset the Interface Prev Status
     * @param p_interfaceCloserVO
     * 
     */
    public synchronized void updateCountersOnSuccessResp(NodeVO p_nodeVO)
    {
    	p_nodeVO.setSuspended(false);
    	   	
    }
    
    
    /**
     * This method resets the counters 
     * @param	NodeVO p_nodeVO
     */
    
    public synchronized void resetCounters(NodeVO p_nodeVO)
    {

        if(_log.isDebugEnabled()) _log.debug("resetCounters","Entered printNodeVO:"+printNodeVO(p_nodeVO));
        try
        {
        	if(p_nodeVO.isSuspended())
        		p_nodeVO.setProcessSingleReqFlag(false);
        	
        	p_nodeVO.setSuspended(false);
        	p_nodeVO.setCurrentAmbTxnTime(0);
    		p_nodeVO.setTimeOfFirstAmbiguousTxn(0);
            p_nodeVO.setCurrentAmbTxnCounter(0);
            p_nodeVO.setLastSuspendedAt(0);
            p_nodeVO.setSuspendedAt(0);        	
        }
        catch(Exception e)
        {
            e.printStackTrace();
            _log.error("resetCounters","Exception e:"+e.getMessage());
        }
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("resetCounters","Exited printNodeVO:"+printNodeVO(p_nodeVO));
        }
    
    }
    
    
    
    
   /* public synchronized void updateCountersOnConnectTimeOut(NodeVO p_nodeVO)
    {    	
        if(_log.isDebugEnabled()) _log.debug("updateCountersOnConnectTimeOut","Entered printNodeVO:"+printNodeVO(p_nodeVO));   
        try
        {
        	p_nodeVO.setBlocked(true);
        	p_nodeVO.setBlokedAt(System.currentTimeMillis());
        	p_nodeVO.setLastBlokedAt(System.currentTimeMillis());
        	p_nodeVO.setBlockedByConTimeOut(true);
        	p_nodeVO.setBlockedByReadTimeOut(false);
        	
        	p_nodeVO.setCurrentAmbTxnTime(0);
    		p_nodeVO.setTimeOfFirstAmbiguousTxn(0);
            p_nodeVO.setCurrentAmbTxnCounter(0);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            _log.error("updateCountersOnAmbiguousResp","Exception e:"+e.getMessage());
        }
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("updateCountersOnConnectTimeOut","Exited printNodeVO:"+printNodeVO(p_nodeVO));
        }
    
    }*/
    
    public String printNodeVO(NodeVO p_nodeVO)
    {
    	return "";
    }    
}
