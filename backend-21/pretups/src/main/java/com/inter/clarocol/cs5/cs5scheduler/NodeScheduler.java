package com.inter.clarocol.cs5.cs5scheduler;
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
//import com.btsl.pretups.inter.util.InterfaceCloser;

/**
 * @(#)NodeScheduler
 *  * Copyright(c) 2015, Comviva technologies LTD.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author               Date            History
 *-------------------------------------------------------------------------------------------------
 * Karan Vijay Singh         30-Sep-2015     Initial Creation
 * ------------------------------------------------------------------------------------------------
 *
 * This class implements the logic to schedule the node.
 * Scheduling of node includes following
 * 1.If node is not blocked
 * 	a.If the node does not reach the max connection limit.
 * 	b.If the node reached the max connection limit, next node is checked for the scheduling.
 * 2.If node is blocked
 * 	a.Expiry duration of blocked node is checked, if it is expired then node is scheduled.
 * 	b.If expiry is not reached, next node is checked for the scheduling.
 * 
 * Blocked Node-A node is set as blocked node from the CS5INHandler while creation of connection for the node results into error.
 */
public class NodeScheduler
{
    private static Log _log = LogFactory.getLog(NodeScheduler.class.getName());
    private Hashtable _nodeTable = null;//Conatins the node number as Key and VO as nod detail
    private int _nodeNumber=1;//Initialize equal to 1 first time so that it will represent the Node number 1.
    private int _totalNodes=0;//Defines the total number of nodes supported by the Interface.
    private int _retryNum=1;//Defines the max number by which scheduled node would be retried.
    private String _hostName;//Defines the host name for the Interface and would be used for the authentication.
    private String _headerHostName;//Defines host name for the Interface and would be used for the authentication and would be set to the request header.
    private String _userAgent;//In the case if host name is not present this would be used for the authentication.
    private String _interfaceID;//IN id of the interface.
    int _maxAllowedNum=0;//This number describes that the number of nodes to be shifted incase of failure.
    public NodeScheduler()
    {
        
    }
    /**
     * Implements the logic to load all the parameters of entire Nodes defined in the corresponding interface.
     * @param	String p_cs5INId
     * @throws	Exception
     */
    public NodeScheduler(String p_cs5INId) throws BTSLBaseException
    {
        if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","Entered p_cs5INId::"+p_cs5INId);
		String url=null;
		long expiryTime=0;//Defines the expiry duration of blocked node.
		int readTimeOutVal=0;//Defines the read time out for the validation request
		int readTimeOutTop=0;//Defines the read time out for the topup request(credit/debit)
		long warnTime=0;//Defines the time threshold for request and response.
		String keepAlive="";//Defines the keep alive parameter.
		String userName="";//Defines username For Node
		String password="";//Defines password for Node.
		NodeVO cs5NodeVO=null;//Contains all the detail of node
		int connectionTimeOut=0;//Defines the connection time out for a node.
		int maxConPerNode=0;//Defines the max connection supported by a node.
		try
		{
            _nodeTable = new Hashtable();
            _interfaceID=p_cs5INId;
	        try
			{
	            _totalNodes = Integer.parseInt(FileCache.getValue(p_cs5INId,"MAX_NODE").trim());
				if(_log.isDebugEnabled())_log.debug("NodeScheduler[constructor]","MAX_NODE::"+_totalNodes);
				
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "check the value of MAX_NODE in IN File Exception e::"+e.getMessage());
			    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
			}
			try
			{
			    _maxAllowedNum = Integer.parseInt(FileCache.getValue(p_cs5INId,"MAX_ALWD_NO").trim());
			    if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","MAX_ALWD_NO::"+_maxAllowedNum);
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of MAX_ALWD_NO in INFile Exception e::"+e.getMessage());
			    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
			}
			try
			{
			    _retryNum = Integer.parseInt(FileCache.getValue(p_cs5INId,"RETRY_NUM").trim());
			    if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","RETRY_NUM::"+_retryNum);
			    setRetryNum(_retryNum);
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of RETRY_NUM in INFile Exception e::"+e.getMessage());
			    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
			}
			try
			{
	            
				_headerHostName= FileCache.getValue(p_cs5INId,"HEADER_HOST_NAME").trim();
				if(InterfaceUtil.isNullString(_headerHostName))
				{
				    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "HEADER_HOST_NAME is not defined into IN File");
				    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
				}
			    if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","HEADER_HOST_NAME::"+_headerHostName);
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "check the value of HEADER_HOST_NAME in IN File Exception e::"+e.getMessage());
			    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
			}
			try
			{
	            
				_hostName= FileCache.getValue(p_cs5INId,"HOST_NAME").trim();
				if(InterfaceUtil.isNullString(_hostName))
				{
				    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "HOST_NAME is not defined into IN File");
				    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
				}
			    if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","HOST_NAME::"+_hostName);
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "check the value of HOST_NAME in IN File Exception e::"+e.getMessage());
			    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
			}
			try
			{
	            //This field is though not require for authentication when host type is present hence Exception is not thrown.
			    _userAgent= FileCache.getValue(p_cs5INId,"USER_AGENT").trim();
			    if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","USER_AGENT::"+_userAgent);
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "check the value of USER_AGENT in IN File Exception e::"+e.getMessage());
			}
			//Get the detail of each node for corresponding interface and set these detail in a ChannelVO.
			//Tese details are
			//1.URL
			//2.Connection time out.
			//3.Expiry time out.
			//4.Number of available nodes.
			//5.Max Number upto which the node is shifted in case the failure of one node.
			//6.Warn time
			//7.Retry number
			//Constructing VO corresponding to each node.
			for(int l=1;l<=_totalNodes;l++)
			{
			   try
			   {
				    //Create an instance of NodeVO
				    cs5NodeVO = new NodeVO();
				    //Set the Node number as key.
				    cs5NodeVO.setNodeNumber(l);
				    url = FileCache.getValue(p_cs5INId,"URL_"+l);
				    if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","url_"+l+"::"+url);
					if(InterfaceUtil.isNullString(url))
					{
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of URL_"+l +" in INFile "+p_cs5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}   
				    cs5NodeVO.setUrl(url.trim());
					try
					{
					    connectionTimeOut = Integer.parseInt(FileCache.getValue(p_cs5INId,"CONN_TIMEOUT").trim());
					    if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","CONN_TIMEOUT::"+connectionTimeOut);
					    cs5NodeVO.setConnectionTimeOut(connectionTimeOut);
					}
					catch(Exception e)
					{
					    e.printStackTrace();
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of CONN_TIMEOUT in INFile "+p_cs5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{
						maxConPerNode = Integer.parseInt(FileCache.getValue(p_cs5INId,"MAX_CON_NODE").trim());
						if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","MAX_CON_NODE::"+maxConPerNode);
						cs5NodeVO.setMaxConPerNode(maxConPerNode);
					}
					catch(Exception e)
					{
					    e.printStackTrace();
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "check the value of MAX_CON_NODE for interface id p_cs5INId ::"+p_cs5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{
					    String valReadTimeOutStr = FileCache.getValue(p_cs5INId,"VAL_READ_TIMEOUT").trim();
					    readTimeOutVal = Integer.parseInt(valReadTimeOutStr);
					    if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","VAL_READ_TIMEOUT::"+readTimeOutVal);
					    cs5NodeVO.setValReadTimeOut(readTimeOutVal);
					}
					catch(Exception e)
					{
					    e.printStackTrace();
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of VAL_READ_TIMEOUT in INFile "+p_cs5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{
					    String readTimeOutStr = FileCache.getValue(p_cs5INId,"TOP_READ_TIMEOUT").trim();
					    readTimeOutTop = Integer.parseInt(readTimeOutStr);
					    if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","TOP_READ_TIMEOUT::"+readTimeOutTop);
					    cs5NodeVO.setTopReadTimeOut(readTimeOutTop);
					}
					catch(Exception e)
					{
					    e.printStackTrace();
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of TOP_READ_TIMEOUT in INFile "+p_cs5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{
					    String warnTimeStr = FileCache.getValue(p_cs5INId,"WARN_TIME").trim();
					    warnTime = Long.parseLong(warnTimeStr);
					    if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","WARN_TIME::"+readTimeOutVal);
					    cs5NodeVO.setWarnTime(warnTime);
					}
					catch(Exception e)
					{
					    e.printStackTrace();
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of WARN_TIME in INFile "+p_cs5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{
					    String expiryTimeStr = FileCache.getValue(p_cs5INId,"EXPIRY_TIME").trim();
					    if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","EXPIRY_TIME::"+expiryTimeStr);
					    expiryTime = Long.parseLong(expiryTimeStr);
					    cs5NodeVO.setExpiryDuration(expiryTime);
					}
					catch(Exception e)
					{
					    e.printStackTrace();
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of EXPIRY_TIME in INFile "+p_cs5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{ 
						keepAlive = FileCache.getValue(p_cs5INId,"KEEP_ALIVE").trim();
						if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","KEEP_ALIVE::"+keepAlive);
						cs5NodeVO.setKeepAlive(keepAlive);
					}
					catch(Exception e)
					{
					    e.printStackTrace();
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of KEEP_ALIVE_"+l +" in INFile "+p_cs5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{ 
						userName = FileCache.getValue(p_cs5INId,"USERNAME").trim();
						if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","USERNAME::"+userName);
						cs5NodeVO.setUsername(userName);
					}
					catch(Exception e)
					{
					    e.printStackTrace();
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of USERNAME in INFile "+p_cs5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{ 
						password = FileCache.getValue(p_cs5INId,"PASSWORD").trim();
						if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","PASSWORD::"+password);
						cs5NodeVO.setPassword(password);
					}
					catch(Exception e)
					{
					    e.printStackTrace();
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of PASSWORD in INFile "+p_cs5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}

				    String thresholdTimeStr = FileCache.getValue(p_cs5INId,"THRESHOLD_TIME");
		            if(InterfaceUtil.isNullString(thresholdTimeStr)||!InterfaceUtil.isNumeric(thresholdTimeStr.trim()))
		            {
		            	_log.error("NodeScheduler[initialize]","THRESHOLD_TIME is either not defined in the INFile or its value is not numeric");
		            	EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"NodeScheduler[constructor]","","_interfaceID:"+_interfaceID,"","THRESHOLD_TIME is either not defined in the INFile or its value is not numeric");
		                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_INTERFACE_CLOSER);
		            }
		            cs5NodeVO.setThresholdTime(Long.parseLong(thresholdTimeStr.trim()));
		            
				   
		            cs5NodeVO.setProcessSingleReqFlag(false);
		            //Set the instance of InterfaceClser
		            cs5NodeVO.setNodeCloser(new NodeCloser());
		            cs5NodeVO.setSuspended(false);
		            
		            
					_nodeTable.put(String.valueOf(l),cs5NodeVO);
			   }
			   catch(BTSLBaseException be)
			   {
			       _log.error("NodeScheduler[constructor]","BTSLBaseException be::"+be.getMessage());
			       throw be;
			   }
			   catch(Exception e)
			   {
			       e.printStackTrace();
			       EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "","Exception while initializing the Node details of interfaceID = "+p_cs5INId +" Exception e="+e.getMessage());
			       throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
			   }
			}
		}
		catch(BTSLBaseException be)
		{
		    _log.error("NodeScheduler[constructor]","BTSLBaseException be::"+be.getMessage());
		    throw be;
		}
		catch(Exception e)
		{
		    e.printStackTrace();
		    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "","Exception while initializing the Node details of interfaceID = "+p_cs5INId +" Exception e="+e.getMessage());
		    _log.error(this,"[constructor]",e.getMessage());
		    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
		}
		finally
		{
		    if(_log.isDebugEnabled()) _log.debug("NodeScheduler[constructor]","Exiting _nodeTable::"+_nodeTable);
		}//end of finally
    }//end of constructor
    
    /**
     * @return Returns the retryNum.
     */
    public int getRetryNum() {
        return _retryNum;
    }
    /**
     * @param retryNum The retryNum to set.
     */
    public void setRetryNum(int retryNum) {
        _retryNum = retryNum;
    }
    /**
     * Increment the node number and initialize the nodeNumber=1 if it reaches the MaximumNode number.
     */
    public void incrementNodeNumber()
    {
        if(_nodeNumber==_totalNodes)
            _nodeNumber=1;
        else
        _nodeNumber++;
    }
    
    /**
     * @return Returns the hostName.
     */
    public String getHostName() {
        return _hostName;
    }
    /**
     * @return Returns the userAgent.
     */
    public String getHeaderHostName() {
        return _headerHostName;
    }
    
    /**
     * @return Returns the userAgent.
     */
    public String getUserAgent() {
        return _userAgent;
    }
    /**
     * This method used to get the URL for corresponding node.
     * @param	String p_transId
     * @return	NodeVO
     * @throws	BTSLBaseException
     */
    public synchronized NodeVO getNodeVO(String p_transId) throws BTSLBaseException
    {
        if(_log.isDebugEnabled()) _log.debug("getNodeVO",p_transId,"Entered p_transId"+p_transId);
        String nodeNumStr=null;
        NodeVO cs5NodeVO=null;
        //1.Get the Available node from getNode.
        //2.If there is not any available node throw an error.
        //3.If a valid node is available return the detail as VO
        try
        {
            nodeNumStr = getNode(p_transId);
            if(nodeNumStr==null)
            {
                _log.error("getNodeVO",p_transId,"There is no scheduled node to process the request for p_transId::"+p_transId);
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_SCHEDULING);
            }
            cs5NodeVO = (NodeVO)_nodeTable.get(nodeNumStr);
            if(cs5NodeVO==null)
            {
                _log.error("getNodeVO",p_transId,"No detail found for the Node number::"+nodeNumStr);
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_SCHEDULING);
            }
        }
        catch(BTSLBaseException be)
        {
            _log.error("getNodeVO",p_transId,"while processing the request for p_transId::"+p_transId+ " get BTSLBaseException  be::"+be.getMessage());
            throw be;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[getNodeVO]", "INTERFACE_ID::"+_interfaceID +" and Node number =["+String.valueOf(_nodeNumber)+"] p_transId: "+p_transId ,"", "", "Exception,while getting Node for Scheduling e="+e.getMessage());
            _log.error("getNodeVO",p_transId,"Exception  e::"+e.getMessage()+" p_transId::"+p_transId);
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_SCHEDULING);
        }//end of catch-Exception
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("getNodeVO",p_transId,"Exited cs5NodeVO::"+cs5NodeVO);
        }//end of finally
        return cs5NodeVO;
    }//end of getNodeVO
    /**
     * This method is used to give the available(scheduled)node for connection.
     * @param	String p_transId
     * @return	String
     * @throws	BTSLBaseException
     */
    private synchronized String getNode(String p_transId) throws BTSLBaseException
    {
        if(_log.isDebugEnabled()) _log.debug("getNode",p_transId,"Entered p_transId::"+p_transId);
        boolean isNext=true;
        String nodeNumStr=null;
        NodeVO cs5NodeVO=null;
        int tempCount=1;
        boolean connIncremented=false;
        try
        {
            //Get the detail corresponding to the node that is available as currentNode.
            cs5NodeVO = (NodeVO)_nodeTable.get(String.valueOf(_nodeNumber));
            //Loop this checks as the number defined in INFile.
	        while(isNext)
	        {
	            try
	            {
		            if(tempCount>_maxAllowedNum)
	                {
	                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "NodeScheduler[getNode]", "INTERFACE_ID::"+_interfaceID,"", "", "MAX ITERATION ["+tempCount+"] is reached");
	                    //throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_MAX_NODE_CHECK_REACH);
	                    nodeNumStr=String.valueOf(_nodeNumber);
	                    break;
	                }
		            tempCount++;//This represents the total iteration made to find a scheduled node and should not be greater than the max node.
		            if(_log.isDebugEnabled()) _log.debug("getNode",p_transId,"tempCount::"+tempCount);
		            connIncremented=false;
		            //1.Check Null
		            //2.Check for the blocked node whether this node is blocked or not.
		            //3.If node is not blocked check whether the node reaches the Max allowed connection.
		            //3-a.If the max connection count reaches for a node handle the event, check the next node to schedule.
		            //3-b.If the max node reaches throw a seprate error code showing channel is full and decrement the max when a request is successful
		            //processed for that node.
		            //4.If node is blocked do the following.
		            //4-a.Check the expiry time of the blocked node
		            //4-b.if diff of current time and blocked time is greater than expiry time, blocked node is expired current node is returned and increment
		            //the corresponding connection number of the node.
		            cs5NodeVO=(NodeVO)_nodeTable.get(String.valueOf(_nodeNumber));
		            if(cs5NodeVO==null)
		            {
		                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "NodeScheduler[getNode]", "INTERFACE_ID::"+_interfaceID,"", "p_transId::"+p_transId, "There is no details corresponding to NODE NUMBER="+_nodeNumber);
		                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_DETAIL_NOT_FOUND);
		            }
		            //Check whether Node is blocked or NOT.
		            if(!cs5NodeVO.isBlocked())
		            {
		                if(cs5NodeVO.getConNumber()>cs5NodeVO.getMaxConPerNode())
		                {
		                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "NodeScheduler[getNode]", "INTERFACE_ID::"+_interfaceID,"", "p_transId::"+p_transId, "FOR NODE No.["+_nodeNumber+"] Current Connection Number ["+cs5NodeVO.getConNumber()+"] Reached to Max allowed connection["+cs5NodeVO.getMaxConPerNode()+"]");
		                    //throw new BTSLBaseException(this,"getNode","MAX ALLOWED CONNECTION FOR NODE IS REACHED");
		                    incrementNodeNumber();
		        	        continue; 
		                }
		                //Increment the connection number for node and decrement this number after completion of request.
		                cs5NodeVO.incrementConNumber(p_transId);
		                //Get the current NODE.
		                nodeNumStr = String.valueOf(_nodeNumber);
		                //Increment the NODE number.
		                incrementNodeNumber();
		                connIncremented=true;
		                break;
		            }
	                if(_log.isDebugEnabled()) _log.debug("getNode",p_transId,"NodeNumber ["+_nodeNumber+"] is blocked"+ "p_transId::"+p_transId);
	                //Check whether the blocked node is expired or not,if expired then set isExpired as false and break the loop.
	                if(isExpired(cs5NodeVO.getBlokedAt(),cs5NodeVO.getExpiryDuration()))
	                {
		                //Increment the connection number for node and decrement this number after completion of request.
		                cs5NodeVO.incrementConNumber(p_transId);
		                //Increment the NODE number.
		                //Get the current NODE.
		                nodeNumStr = String.valueOf(_nodeNumber);
		                incrementNodeNumber();
		                connIncremented=true;
	                    cs5NodeVO.setBlocked(false);
	                    //Initialize the block time if it is expired.
	                    cs5NodeVO.setBlokedAt(0);
	                    break;
	                }
	                //Log the detail and Increment the NODE number when node is blocked and it is not expired.
	                _log.info("getNode",p_transId,"Node number ::"+_nodeNumber+" of interfaceID::"+_interfaceID+"is blocked and its expiry is not reached");
	                incrementNodeNumber();
	            }
	            catch(BTSLBaseException be)
	            {
	                throw be;
	            }
	            catch(Exception e)
	            {
	                //decrement the connection counter if any error occurs after incrementing the connection number.
	                e.printStackTrace();
	                _log.error("getNode",p_transId,"_interfaceID"+_interfaceID+" p_transId ::"+p_transId+" Exception e::"+e.getMessage());
	                if(connIncremented)
	                    cs5NodeVO.decrementConNumber(p_transId);
	                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[getNode]", "INTERFACE_ID::"+_interfaceID +" and Node number =["+String.valueOf(_nodeNumber)+"]","", "transId::"+p_transId, "Exception while getting Node e="+e.getMessage());
	                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_SCHEDULING);
	            }//end of catch-Exception
	        }//end of while
        }
        catch(BTSLBaseException be)
        {
            _log.error("getNode",p_transId,"p_transId::"+p_transId+" BTSLBaseException  be::"+be.getMessage());
            throw be;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[getNode]", "INTERFACE_ID::"+_interfaceID+" and Node number =["+String.valueOf(_nodeNumber)+"]","", "", "Exception while getting Node e="+e.getMessage());
            _log.error("getNode",p_transId,"p_transId::"+p_transId+" Exception e::"+e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_SCHEDULING);
        }//end of catch-Exception
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("getNode",p_transId,"Exited nodeNumStr::"+nodeNumStr);
        }//end of finally
        return nodeNumStr;
    }//end of 
    /**
     * This method is used to check the expiry of node,if it is blocked.
     * @param p_blockedAt
     * @param p_expiryDuration
     * @return boolean
     * @throws Exception
     */
    public boolean isExpired(long p_blockedAt,long p_expiryDuration)throws BTSLBaseException
    {
        if(_log.isDebugEnabled()) _log.debug("isExpired","Entered p_blockedAt::"+p_blockedAt+" p_expiryDuration::"+p_expiryDuration);
        boolean isExp=false;
        try
        {
            long currentTime = System.currentTimeMillis();
            if(_log.isDebugEnabled())_log.debug("isExpired","currentTime::"+currentTime);
            if((currentTime-p_blockedAt)>=p_expiryDuration)
                isExp=true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            _log.error("isExpired","While checking the expiry of blocked node get the Exception e::"+e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_SCHEDULING);
        }//end of catch-Exception
        finally
        {
            if(_log.isDebugEnabled())_log.debug("isExpired","isExp::"+isExp);
        }//end of finally
        return isExp;
    }//end of isExpired
    
    
    public Hashtable getNodeTable()
    {
    	return _nodeTable;
    }
}
