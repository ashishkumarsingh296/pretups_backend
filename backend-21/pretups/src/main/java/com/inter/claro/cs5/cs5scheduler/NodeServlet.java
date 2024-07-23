package com.inter.claro.cs5.cs5scheduler;

import java.io.IOException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @(#)NodeServlet
 *  * Copyright(c) 2016, Comviva Technologies LTD.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author               Date            History
 *-------------------------------------------------------------------------------------------------
 * Sanjay Kumar Bind1       30-Sep-2016     Initial Creation
 * ------------------------------------------------------------------------------------------------
 * This Servlet is responsible to initialize the node details at server start up.
 */
public class NodeServlet extends HttpServlet {
	private static final Log log = LogFactory.getLog(NodeServlet.class);
    /**
     * Constructor of the object.
     */
    public NodeServlet() {
        super();
    }
    
    @Override
    public void init()
    {
        /*
         * 
         */
    }
    
    @Override
    public void init(ServletConfig conf) throws ServletException
    {
        if(log.isDebugEnabled())
        	log.debug("init",PretupsI.ENTERED);
        super.init(conf);
        initialize();
        if(log.isDebugEnabled())
        	log.debug("init","Exit");
    }
    /**
     * Destruction of the servlet. <br>
     */
    

    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
    	final String methodName = "doGet";
        if(log.isDebugEnabled())
        	log.debug(methodName,PretupsI.ENTERED);
		String action=request.getParameter("action");
		String interfaceID=request.getParameter("INTERFACE_ID");
		if(log.isDebugEnabled())
			log.debug(methodName,"action ="+action+" interfaceID ="+interfaceID);
		if(action==null)
			return;
		if("RELOAD".equals(action))
		{
			if(interfaceID!=null)
				//Done so that if we need to make changes in the file it would take the same for a particluar ID or IDs
				initialize(interfaceID);	
			else
				initialize();
		}//end if
		if(log.isDebugEnabled())
			log.debug(methodName,PretupsI.EXITED);
    }//end doGet
    /**
     *This method is used to load the Node parameter of the interface ids defined in the constant props
     *@return void
     */
    public void initialize()
    {
    	final String methodName = "initialize";
        if(log.isDebugEnabled())
        	log.debug(methodName,PretupsI.ENTERED);
        String schINIDs =  null;
        try
        {
            //Fetch the Scheduler IN id's from the constant props
	        schINIDs =  Constants.getProperty("SCH_IN_IDS");
	        if(log.isDebugEnabled())
	        	log.debug(methodName,"schINIDs = "+schINIDs);
	        //If ids are defined in constant props,invoke the initialize method of NodeManager
	        if(!BTSLUtil.isNullString(schINIDs))
	            NodeManager.initialize(schINIDs);
        }
        catch(BTSLBaseException be)
        {
        	log.errorTrace(PretupsI.EXCEPTION+methodName,be);
        }
        catch(Exception e)
        {
        	log.errorTrace(PretupsI.EXCEPTION+methodName,e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,schINIDs,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"NodeServlet[initialize]","","","","Exception while initialize IN Scheduler Parameters Exception:"+e.getMessage());
            throw e;
        }//end of catch-Exception
        finally
        {
            if(log.isDebugEnabled())
            	log.debug(methodName,PretupsI.EXITED);
        }//end finally
    }//end of initialize
    /**
     * This method is used to load the NODE parameters for the interface ids, supplied as argument
     * @param	String pInterfaceId
     */
    public void initialize(String pInterfaceId)
    {
    	final String methodName = "initialize";
        if(log.isDebugEnabled())
        	log.debug(methodName,PretupsI.ENTERED+" pInterfaceId::"+pInterfaceId);
        try
        {
            if(!BTSLUtil.isNullString(pInterfaceId))
	            NodeManager.initialize(pInterfaceId);
        }
        catch(BTSLBaseException be)
        {
        	log.errorTrace(PretupsI.EXCEPTION+methodName,be);
        }
        catch(Exception e)
        {
        	log.errorTrace(PretupsI.EXCEPTION+methodName,e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,pInterfaceId,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"NodeServlet[initialize]","","","","Exception while initialize IN Scheduler Parameters Exception:"+e.getMessage());
            throw e;
        }//end of catch-Exception
        finally
        {
            if(log.isDebugEnabled())
            	log.debug(methodName,PretupsI.EXITED);
        }//end of finally
    }//end of initialize
}
