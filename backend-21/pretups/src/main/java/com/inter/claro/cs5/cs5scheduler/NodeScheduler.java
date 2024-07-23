package com.inter.claro.cs5.cs5scheduler;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;

/**
 * @(#)NodeScheduler
 *  * Copyright(c) 2016, Comviva Technologies LTD.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author               Date            History
 *-------------------------------------------------------------------------------------------------
 * Sanjay Kumar Bind1       30-Sep-2016     Initial Creation
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
	static final String CLASSNAME = "NodeScheduler";
	static final String INETRFACEID ="INTERFACE_ID::";
	static final String PTRANSID = "p_transId::";

	private static final Log log = LogFactory.getLog(NodeScheduler.class);
    private HashMap<String,NodeVO> nodeTable = null;//Conatins the node number as Key and VO as nod detail
    private int nodeNumber=1;//Initialize equal to 1 first time so that it will represent the Node number 1.
    private int totalNodes=0;//Defines the total number of nodes supported by the Interface.
    private int retryNum=1;//Defines the max number by which scheduled node would be retried.
    private String hostName;//Defines the host name for the Interface and would be used for the authentication.
    private String headerHostName;//Defines host name for the Interface and would be used for the authentication and would be set to the request header.
    private String userAgent;//In the case if host name is not present this would be used for the authentication.
    private String interfaceID;//IN id of the interface.
    int maxAllowedNum=0;//This number describes that the number of nodes to be shifted incase of failure.
    public NodeScheduler()
    {
        /*
         * 
         */
    }
    /**
     * Implements the logic to load all the parameters of entire Nodes defined in the corresponding interface.
     * @param	String pCS5INId
     * @throws	Exception
     */
    public NodeScheduler(String pCS5INId) throws BTSLBaseException
    {
    	final String methodName = "NodeScheduler";
        if(log.isDebugEnabled())
        	log.debug(CLASSNAME+methodName,"Entered pCS5INId::"+pCS5INId);
		String url=null;
		long expiryTime=0;//Defines the expiry duration of blocked node.
		int readTimeOutVal=0;//Defines the read time out for the validation request
		int readTimeOutTop=0;//Defines the read time out for the topup request(credit/debit)
		long warnTime=0;//Defines the time threshold for request and response.
		String keepAlive="";//Defines the keep alive parameter.
		String userName="";//Defines username For Node
		String password;//Defines password for Node.
		NodeVO cs5NodeVO=null;//Contains all the detail of node
		int connectionTimeOut=0;//Defines the connection time out for a node.
		int maxConPerNode=0;//Defines the max connection supported by a node.
		try
		{
            nodeTable = new HashMap();
            interfaceID=pCS5INId;
	        try
			{
	            totalNodes = Integer.parseInt(FileCache.getValue(pCS5INId,"MAX_NODE").trim());
				if(log.isDebugEnabled())
					log.debug(CLASSNAME+methodName,"MAX_NODE::"+totalNodes);
				
			}
			catch(Exception e)
			{
				log.errorTrace(PretupsI.ERROR+methodName,e);
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "check the value of MAX_NODE in IN File Exception e::"+e.getMessage());
			    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
			}
			try
			{
			    maxAllowedNum = Integer.parseInt(FileCache.getValue(pCS5INId,"MAX_ALWD_NO").trim());
			    if(log.isDebugEnabled())
			    	log.debug(CLASSNAME+methodName,"MAX_ALWD_NO::"+maxAllowedNum);
			}
			catch(Exception e)
			{
				log.errorTrace(PretupsI.ERROR+methodName,e);
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "Check the value of MAX_ALWD_NO in INFile Exception e::"+e.getMessage());
			    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
			}
			try
			{
			    retryNum = Integer.parseInt(FileCache.getValue(pCS5INId,"RETRY_NUM").trim());
			    if(log.isDebugEnabled())
			    	log.debug(CLASSNAME+methodName,"RETRY_NUM::"+retryNum);
			    setRetryNum(retryNum);
			}
			catch(Exception e)
			{
				log.errorTrace(PretupsI.ERROR+methodName,e);
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "Check the value of RETRY_NUM in INFile Exception e::"+e.getMessage());
			    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
			}
			try
			{
	            
				headerHostName= FileCache.getValue(pCS5INId,"HEADER_HOST_NAME").trim();
				if(InterfaceUtil.isNullString(headerHostName))
				{
				    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "HEADER_HOST_NAME is not defined into IN File");
				    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
				}
			    if(log.isDebugEnabled())
			    	log.debug(CLASSNAME+methodName,"HEADER_HOST_NAME::"+headerHostName);
			}
			catch(Exception e)
			{
				log.errorTrace(PretupsI.ERROR+methodName,e);
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "check the value of HEADER_HOST_NAME in IN File Exception e::"+e.getMessage());
			    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
			}
			try
			{
	            
				hostName= FileCache.getValue(pCS5INId,"HOST_NAME").trim();
				if(InterfaceUtil.isNullString(hostName))
				{
				    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "HOST_NAME is not defined into IN File");
				    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
				}
			    if(log.isDebugEnabled())
			    	log.debug(CLASSNAME+methodName,"HOST_NAME::"+hostName);
			}
			catch(Exception e)
			{
				log.errorTrace(PretupsI.ERROR+methodName,e);
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "check the value of HOST_NAME in IN File Exception e::"+e.getMessage());
			    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
			}
			try
			{
	            //This field is though not require for authentication when host type is present hence Exception is not thrown.
			    userAgent= FileCache.getValue(pCS5INId,"USER_AGENT").trim();
			    if(log.isDebugEnabled())
			    	log.debug(CLASSNAME+methodName,"USER_AGENT::"+userAgent);
			}
			catch(Exception e)
			{
				log.errorTrace(PretupsI.ERROR+methodName,e);
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "check the value of USER_AGENT in IN File Exception e::"+e.getMessage());
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
			for(int l=1;l<=totalNodes;l++)
			{
			   try
			   {
				    //Create an instance of NodeVO
				    cs5NodeVO = new NodeVO();
				    //Set the Node number as key.
				    cs5NodeVO.setNodeNumber(l);
				    url = FileCache.getValue(pCS5INId,"URL_"+l);
				    if(log.isDebugEnabled())
				    	log.debug(CLASSNAME+methodName,"url_"+l+"::"+url);
					if(InterfaceUtil.isNullString(url))
					{
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "Check the value of URL_"+l +" in INFile "+pCS5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}   
				    cs5NodeVO.setUrl(url.trim());
					try
					{
					    connectionTimeOut = Integer.parseInt(FileCache.getValue(pCS5INId,"CONN_TIMEOUT").trim());
					    if(log.isDebugEnabled())
					    	log.debug(CLASSNAME+methodName,"CONN_TIMEOUT::"+connectionTimeOut);
					    cs5NodeVO.setConnectionTimeOut(connectionTimeOut);
					}
					catch(Exception e)
					{
						log.errorTrace(PretupsI.ERROR+methodName,e);
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "Check the value of CONN_TIMEOUT in INFile "+pCS5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{
						maxConPerNode = Integer.parseInt(FileCache.getValue(pCS5INId,"MAX_CON_NODE").trim());
						if(log.isDebugEnabled())
							log.debug(CLASSNAME+methodName,"MAX_CON_NODE::"+maxConPerNode);
						cs5NodeVO.setMaxConPerNode(maxConPerNode);
					}
					catch(Exception e)
					{
						log.errorTrace(PretupsI.ERROR+methodName,e);
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "check the value of MAX_CON_NODE for interface id pCS5INId ::"+pCS5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{
					    String valReadTimeOutStr = FileCache.getValue(pCS5INId,"VAL_READ_TIMEOUT").trim();
					    readTimeOutVal = Integer.parseInt(valReadTimeOutStr);
					    if(log.isDebugEnabled())
					    	log.debug(CLASSNAME+methodName,"VAL_READ_TIMEOUT::"+readTimeOutVal);
					    cs5NodeVO.setValReadTimeOut(readTimeOutVal);
					}
					catch(Exception e)
					{
						log.errorTrace(PretupsI.ERROR+methodName,e);
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "Check the value of VAL_READ_TIMEOUT in INFile "+pCS5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{
					    String readTimeOutStr = FileCache.getValue(pCS5INId,"TOP_READ_TIMEOUT").trim();
					    readTimeOutTop = Integer.parseInt(readTimeOutStr);
					    if(log.isDebugEnabled())
					    	log.debug(CLASSNAME+methodName,"TOP_READ_TIMEOUT::"+readTimeOutTop);
					    cs5NodeVO.setTopReadTimeOut(readTimeOutTop);
					}
					catch(Exception e)
					{
						log.errorTrace(PretupsI.ERROR+methodName,e);
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "Check the value of TOP_READ_TIMEOUT in INFile "+pCS5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{
					    String warnTimeStr = FileCache.getValue(pCS5INId,"WARN_TIME").trim();
					    warnTime = Long.parseLong(warnTimeStr);
					    if(log.isDebugEnabled())
					    	log.debug(CLASSNAME+methodName,"WARN_TIME::"+readTimeOutVal);
					    cs5NodeVO.setWarnTime(warnTime);
					}
					catch(Exception e)
					{
						log.errorTrace(PretupsI.ERROR+methodName,e);
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "Check the value of WARN_TIME in INFile "+pCS5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{
					    String expiryTimeStr = FileCache.getValue(pCS5INId,"EXPIRY_TIME").trim();
					    if(log.isDebugEnabled())
					    	log.debug(CLASSNAME+methodName,"EXPIRY_TIME::"+expiryTimeStr);
					    expiryTime = Long.parseLong(expiryTimeStr);
					    cs5NodeVO.setExpiryDuration(expiryTime);
					}
					catch(Exception e)
					{
						log.errorTrace(PretupsI.ERROR+methodName,e);
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "Check the value of EXPIRY_TIME in INFile "+pCS5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{ 
						keepAlive = FileCache.getValue(pCS5INId,"KEEP_ALIVE").trim();
						if(log.isDebugEnabled())
							log.debug(CLASSNAME+methodName,"KEEP_ALIVE::"+keepAlive);
						cs5NodeVO.setKeepAlive(keepAlive);
					}
					catch(Exception e)
					{
						log.errorTrace(PretupsI.ERROR+methodName,e);
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "Check the value of KEEP_ALIVE_"+l +" in INFile "+pCS5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{ 
						userName = FileCache.getValue(pCS5INId,"USERNAME").trim();
						if(log.isDebugEnabled())
							log.debug(CLASSNAME+methodName,"USERNAME::"+userName);
						cs5NodeVO.setUsername(userName);
					}
					catch(Exception e)
					{
						log.errorTrace(PretupsI.ERROR+methodName,e);
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "Check the value of USERNAME in INFile "+pCS5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}
					try
					{ 
						password = FileCache.getValue(pCS5INId,"PASSWORD").trim();
						if(log.isDebugEnabled())
							log.debug(CLASSNAME+methodName,"PASSWORD::"+password);
						cs5NodeVO.setPassword(password);
					}
					catch(Exception e)
					{
						log.errorTrace(PretupsI.ERROR+methodName,e);
					    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "Check the value of PASSWORD in INFile "+pCS5INId);
					    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
					}

				    String thresholdTimeStr = FileCache.getValue(pCS5INId,"THRESHOLD_TIME");
		            if(InterfaceUtil.isNullString(thresholdTimeStr)||!InterfaceUtil.isNumeric(thresholdTimeStr.trim()))
		            {
		            	log.error("NodeScheduler[initialize]","THRESHOLD_TIME is either not defined in the INFile or its value is not numeric");
		            	EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,CLASSNAME+methodName,"","interfaceID:"+interfaceID,"","THRESHOLD_TIME is either not defined in the INFile or its value is not numeric");
		                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_INTERFACE_CLOSER);
		            }
		            cs5NodeVO.setThresholdTime(Long.parseLong(thresholdTimeStr.trim()));
		            
				   
		            cs5NodeVO.setProcessSingleReqFlag(false);
		            //Set the instance of InterfaceClser
		            cs5NodeVO.setNodeCloser(new NodeCloser());
		            cs5NodeVO.setSuspended(false);
		            
		            
					nodeTable.put(String.valueOf(l),cs5NodeVO);
			   }
			   catch(BTSLBaseException be)
			   {
			       log.error(CLASSNAME+methodName,"BTSLBaseException be::"+be.getMessage());
			       throw be;
			   }
			   catch(Exception e)
			   {
				   log.errorTrace(PretupsI.ERROR+methodName,e);
			       EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "","Exception while initializing the Node details of interfaceID = "+pCS5INId +" Exception e="+e.getMessage());
			       throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
			   }
			}
		}
		catch(BTSLBaseException be)
		{
		    log.error(CLASSNAME+methodName,"BTSLBaseException be::"+be.getMessage());
		    throw be;
		}
		catch(Exception e)
		{
			log.errorTrace(PretupsI.ERROR+methodName,e);
		    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "","Exception while initializing the Node details of interfaceID = "+pCS5INId +" Exception e="+e.getMessage());
		    log.error(this,"[constructor]",e.getMessage());
		    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
		}
		finally
		{
		    if(log.isDebugEnabled())
		    	log.debug(CLASSNAME+methodName,"Exiting nodeTable::"+nodeTable);
		}//end of finally
    }//end of constructor
    
    /**
     * @return Returns the retryNum.
     */
    public int getRetryNum() {
        return retryNum;
    }
    /**
     * @param retryNum The retryNum to set.
     */
    public void setRetryNum(int retryNum) {
        this.retryNum = retryNum;
    }
    /**
     * Increment the node number and initialize the nodeNumber=1 if it reaches the MaximumNode number.
     */
    public void incrementNodeNumber()
    {
        if(nodeNumber==totalNodes)
            nodeNumber=1;
        else
        nodeNumber++;
    }
    
    /**
     * @return Returns the hostName.
     */
    public String getHostName() {
        return hostName;
    }
    /**
     * @return Returns the userAgent.
     */
    public String getHeaderHostName() {
        return headerHostName;
    }
    
    /**
     * @return Returns the userAgent.
     */
    public String getUserAgent() {
        return userAgent;
    }
    /**
     * This method used to get the URL for corresponding node.
     * @param	String pTransId
     * @return	NodeVO
     * @throws	BTSLBaseException
     */
    public synchronized NodeVO getNodeVO(String pTransId) throws BTSLBaseException
    {
    	final String methodName = "getNodeVO";
        if(log.isDebugEnabled())
        	log.debug(methodName,pTransId,"Entered pTransId"+pTransId);
        String nodeNumStr=null;
        NodeVO cs5NodeVO=null;
        //1.Get the Available node from getNode.
        //2.If there is not any available node throw an error.
        //3.If a valid node is available return the detail as VO
        try
        {
            nodeNumStr = getNode(pTransId);
            if(nodeNumStr==null)
            {
                log.error(methodName,pTransId,"There is no scheduled node to process the request for pTransId::"+pTransId);
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_SCHEDULING);
            }
            cs5NodeVO = nodeTable.get(nodeNumStr);
            if(cs5NodeVO==null)
            {
                log.error(methodName,pTransId,"No detail found for the Node number::"+nodeNumStr);
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_SCHEDULING);
            }
        }
        catch(BTSLBaseException be)
        {
            log.error(methodName,pTransId,"while processing the request for pTransId::"+pTransId+ " get BTSLBaseException  be::"+be.getMessage());
            throw be;
        }
        catch(Exception e)
        {
        	log.errorTrace(PretupsI.ERROR+methodName,e);
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[getNodeVO]", INETRFACEID+interfaceID +" & Node number =["+nodeNumber+"] pTransId: "+pTransId ,"", "", "Exception,while getting Node for Scheduling e="+e.getMessage());
            log.error(methodName,pTransId,"Exception  e::"+e.getMessage()+" pTransId::"+pTransId);
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_SCHEDULING);
        }//end of catch-Exception
        finally
        {
            if(log.isDebugEnabled())
            	log.debug(methodName,pTransId,"Exited cs5NodeVO::"+cs5NodeVO);
        }//end of finally
        return cs5NodeVO;
    }//end of getNodeVO
    /**
     * This method is used to give the available(scheduled)node for connection.
     * @param	String pTransId
     * @return	String
     * @throws	BTSLBaseException
     */
    private synchronized String getNode(String pTransId) throws BTSLBaseException
    {
    	final String methodName = "getNode";
        if(log.isDebugEnabled())
        	log.debug(methodName,pTransId,"Entered pTransId::"+pTransId);
        boolean isNext=true;
        String nodeNumStr=null;
        NodeVO cs5NodeVO=null;
        int tempCount=1;
        boolean connIncremented=false;
        try
        {
            //Get the detail corresponding to the node that is available as currentNode.
            cs5NodeVO = nodeTable.get(String.valueOf(nodeNumber));
            //Loop this checks as the number defined in INFile.
	        while(isNext)
	        {
	            try
	            {
		            if(tempCount>maxAllowedNum)
	                {
	                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, CLASSNAME+methodName, INETRFACEID+interfaceID,"", "", "MAX ITERATION ["+tempCount+"] is reached");
	                    nodeNumStr=String.valueOf(nodeNumber);
	                    break;
	                }
		            tempCount++;//This represents the total iteration made to find a scheduled node and should not be greater than the max node.
		            if(log.isDebugEnabled())
		            	log.debug(methodName,pTransId,"tempCount::"+tempCount);
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
		            cs5NodeVO=nodeTable.get(String.valueOf(nodeNumber));
		            if(cs5NodeVO==null)
		            {
		                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,  CLASSNAME+methodName, INETRFACEID+interfaceID,"", PTRANSID+pTransId, "There is no details corresponding to NODE NUMBER="+nodeNumber);
		                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_DETAIL_NOT_FOUND);
		            }
		            //Check whether Node is blocked or NOT.
		            if(!cs5NodeVO.isBlocked())
		            {
		                if(cs5NodeVO.getConNumber()>cs5NodeVO.getMaxConPerNode())
		                {
		                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,  CLASSNAME+methodName, INETRFACEID+interfaceID,"", PTRANSID+pTransId, "FOR NODE No.["+nodeNumber+"] Current Connection Number ["+cs5NodeVO.getConNumber()+"] Reached to Max allowed connection["+cs5NodeVO.getMaxConPerNode()+"]");
		                    incrementNodeNumber();
		        	        continue; 
		                }
		                //Increment the connection number for node and decrement this number after completion of request.
		                cs5NodeVO.incrementConNumber(pTransId);
		                //Get the current NODE.
		                nodeNumStr = String.valueOf(nodeNumber);
		                //Increment the NODE number.
		                incrementNodeNumber();
		                connIncremented=true;
		                break;
		            }
	                if(log.isDebugEnabled())
	                	log.debug(methodName,pTransId,"NodeNumber ["+nodeNumber+"] is blocked"+ PTRANSID+pTransId);
	                //Check whether the blocked node is expired or not,if expired then set isExpired as false and break the loop.
	                if(isExpired(cs5NodeVO.getBlokedAt(),cs5NodeVO.getExpiryDuration()))
	                {
		                //Increment the connection number for node and decrement this number after completion of request.
		                cs5NodeVO.incrementConNumber(pTransId);
		                //Increment the NODE number.
		                //Get the current NODE.
		                nodeNumStr = String.valueOf(nodeNumber);
		                incrementNodeNumber();
		                connIncremented=true;
	                    cs5NodeVO.setBlocked(false);
	                    //Initialize the block time if it is expired.
	                    cs5NodeVO.setBlokedAt(0);
	                    break;
	                }
	                //Log the detail and Increment the NODE number when node is blocked and it is not expired.
	                log.info(methodName,pTransId,"Node number ::"+nodeNumber+" of interfaceID::"+interfaceID+"is blocked and its expiry is not reached");
	                incrementNodeNumber();
	            }
	            catch(BTSLBaseException be)
	            {
	                throw be;
	            }
	            catch(Exception e)
	            {
	                //decrement the connection counter if any error occurs after incrementing the connection number.
	            	log.errorTrace(PretupsI.ERROR+methodName,e);
	                log.error(methodName,pTransId,"interfaceID"+interfaceID+" pTransId ::"+pTransId+" Exception e::"+e.getMessage());
	                if(connIncremented)
	                    cs5NodeVO.decrementConNumber(pTransId);
	                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,  CLASSNAME+methodName, INETRFACEID+interfaceID +" and Node number =["+nodeNumber+"]","", "transId::"+pTransId, "Exception while getting Node e="+e.getMessage());
	                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_SCHEDULING);
	            }//end of catch-Exception
	        }//end of while
        }
        catch(BTSLBaseException be)
        {
            log.error(methodName,pTransId,PTRANSID+pTransId+" BTSLBaseException  be::"+be.getMessage());
            throw be;
        }
        catch(Exception e)
        {
        	log.errorTrace(PretupsI.ERROR+methodName,e);
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,  CLASSNAME+methodName, INETRFACEID+interfaceID+" and Node number =["+nodeNumber+"]","", "", "Exception while getting Node e="+e.getMessage());
            log.error(methodName,pTransId,PTRANSID+pTransId+" Exception e::"+e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_SCHEDULING);
        }//end of catch-Exception
        finally
        {
            if(log.isDebugEnabled())
            	log.debug(methodName,pTransId,"Exited nodeNumStr::"+nodeNumStr);
        }//end of finally
        return nodeNumStr;
    }//end of 
    /**
     * This method is used to check the expiry of node,if it is blocked.
     * @param pBlockedAt
     * @param pExpiryDuration
     * @return boolean
     * @throws Exception
     */
    public boolean isExpired(long pBlockedAt,long pExpiryDuration)throws BTSLBaseException
    {
    	final String methodName = "isExpired";
        if(log.isDebugEnabled())
        	log.debug(methodName,"Entered pBlockedAt::"+pBlockedAt+" pExpiryDuration::"+pExpiryDuration);
        boolean isExp=false;
        try
        {
            long currentTime = System.currentTimeMillis();
            if(log.isDebugEnabled())
            	log.debug(methodName,"currentTime::"+currentTime);
            if((currentTime-pBlockedAt)>=pExpiryDuration)
                isExp=true;
        }
        catch(Exception e)
        {
        	log.errorTrace(PretupsI.ERROR+methodName,e);
            log.error(methodName,"While checking the expiry of blocked node get the Exception e::"+e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_SCHEDULING);
        }//end of catch-Exception
        finally
        {
            if(log.isDebugEnabled())
            	log.debug(methodName,"isExp::"+isExp);
        }//end of finally
        return isExp;
    }//end of isExpired
    
    
    public HashMap<String,NodeVO> getNodeTable()
    {
    	return nodeTable;
    }
}
