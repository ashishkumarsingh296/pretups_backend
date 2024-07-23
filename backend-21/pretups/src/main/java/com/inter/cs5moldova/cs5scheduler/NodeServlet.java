package com.inter.cs5moldova.cs5scheduler;

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
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @(#)NodeCloser.java
 * Copyright(c) 2011, Comviva Technologies Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * 	  Author				Date				 History
 *-------------------------------------------------------------------------------------------------
 * Vikas Jauhari        Mar 29, 2012		    Initial Creation
 * ------------------------------------------------------------------------------------------------
 */

public class NodeServlet extends HttpServlet 
{
	private static Log log = LogFactory.getLog(NodeServlet.class.getName());
	/**
	 * Constructor of the object.
	 */
	private static final String METHOD_START = "Entered";
	private static final String METHOD_END = "Exit";
	public NodeServlet() 
	{
		super();
	}
	@Override
	public void init()
	{
		//Method will be empty, no default implementation
	}
	
	@Override
	public void init(ServletConfig conf) throws ServletException
	{
		final String methodName = "init";
		if(log.isDebugEnabled()) log.debug(methodName,METHOD_START);
		super.init(conf);
		initialize();
		if(log.isDebugEnabled()) log.debug(methodName,METHOD_END);
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy()
	{
		super.destroy(); 
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
	{
		final String methodName = "doGet";
		if(log.isDebugEnabled()) log.debug(methodName,METHOD_START);
		String action=request.getParameter("action");
		String interfaceID=request.getParameter("INTERFACE_ID");

		if(log.isDebugEnabled()) log.debug(methodName,"action ="+action+" interfaceID ="+interfaceID);
		if(action==null)
			return;

		if(action.equals("RELOAD"))
		{
			if(interfaceID!=null)
				initialize(interfaceID);//Done so that if we need to make changes in the file it would take the same for a particluar ID or IDs	
			else
				initialize();
		}
		if(log.isDebugEnabled()) log.debug(methodName,METHOD_END);
	}

	/**
	 *This method is used to load the Node parameter of the interface ids defined in the constant props
	 *@return void
	 */
	public void initialize()
	{
		final String methodName = "initialize";
		if(log.isDebugEnabled()) log.debug(methodName,METHOD_START);
		String schINIDs =  null;
		try
		{
			schINIDs =  Constants.getProperty("SCH_IN_IDS");//Fetch the Scheduler IN id's from the constant props
			if(log.isDebugEnabled()) log.debug(methodName,"schINIDs = "+schINIDs);
			if(!BTSLUtil.isNullString(schINIDs))
				NodeManager.initialize(schINIDs);
		}
		catch(BTSLBaseException be)
		{
			log.error(methodName,"BTSLBaseException be::"+be.getMessage());
		}
		catch(Exception e)
		{
			log.errorTrace(methodName, e);
			log.error(methodName,"Exception e::"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,schINIDs,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"NodeServlet[initialize]","","","","Exception while initialize IN Scheduler Parameters Exception:"+e.getMessage());
		}
		finally
		{
			if(log.isDebugEnabled()) log.debug(methodName,METHOD_END);
		}
	}

	/**
	 * This method is used to load the NODE parameters for the interface ids, supplied as argument
	 * @param	String p_interfaceId
	 */
	public void initialize(String interfaceId)
	{
		final String methodName = "initialize";
		if(log.isDebugEnabled()) log.debug(methodName,"Entered p_interfaceId::"+interfaceId);
		try
		{
			if(!BTSLUtil.isNullString(interfaceId))
				NodeManager.initialize(interfaceId);
		}
		catch(BTSLBaseException be)
		{
			log.error(methodName,"BTSLBaseException be::"+be.getMessage());
		}
		catch(Exception e)
		{
			log.errorTrace(methodName, e);
			log.error(methodName,"Exception e::"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,interfaceId,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"NodeServlet[initialize]","","","","Exception while initialize IN Scheduler Parameters Exception:"+e.getMessage());
		}
		finally
		{
			if(log.isDebugEnabled()) log.debug(methodName,METHOD_END);
		}
	}
}
