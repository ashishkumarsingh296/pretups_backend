package com.inter.gp.cs5.cs5scheduler;

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
 * @(#)NodeServlet
 *  * Copyright(c) 2015, Comviva technologies LTD.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author               Date            History
 *-------------------------------------------------------------------------------------------------
 * Karan Vijay Singh         30-Sep-2015     Initial Creation
 * ------------------------------------------------------------------------------------------------
 * This servlet is responsible to initialize the node details at server start up.
 */
public class NodeServlet extends HttpServlet {
    private static Log log = LogFactory.getLog(NodeServlet.class.getName());
    /**
     * Constructor of the object.
     */
    public NodeServlet() {
        super();
    }
    public void init()
    {
    	LogFactory.printLog("init", "Entered", log);
    	
    }
    public void init(ServletConfig conf) throws ServletException
    {
    	LogFactory.printLog("init", "Entered"+conf, log);
        super.init(conf);
        initialize();
        LogFactory.printLog("init", "Exit", log);
    }
    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); 

    }
    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
    	LogFactory.printLog("doget", "Entered", log);
       
		String action=request.getParameter("action");
		String interfaceID=request.getParameter("INTERFACE_ID");
		LogFactory.printLog("doget", "action ="+action+" interfaceID ="+interfaceID, log);
		
		if(action==null)
			return;
		if(("RELOAD").equals(action))
		{
			if(interfaceID!=null)
				//Done so that if we need to make changes in the file it would take the same for a particluar ID or IDs
				initialize(interfaceID);	
			else
				initialize();
		}//end if
		LogFactory.printLog("doget", "Exited", log);
    }//end doGet
    /**
     *This method is used to load the Node parameter of the interface ids defined in the constant props
     *@return void
     */
    public void initialize()
    {
    	LogFactory.printLog("initialize", "Entered", log);
       
        String schINIDs =  null;
        try
        {
            //Fetch the Scheduler IN id's from the constant props
	        schINIDs =  Constants.getProperty("SCH_IN_IDS");
	        
	        LogFactory.printLog("initialize", "schINIDs = "+schINIDs, log);
	        //If ids are defined in constant props,invoke the initialize method of NodeManager
	        //if(schINIDs!=null)
	        if(!BTSLUtil.isNullString(schINIDs))
	            NodeManager.initialize(schINIDs);
        }
        catch(BTSLBaseException be)
        {
        	log.errorTrace("Exception",be);
        	log.error("initialize","BTSLBaseException be::"+be.getMessage());
        }
        catch(Exception e)
        {
        	log.errorTrace("Exception",e);
            log.error("initialize","Exception e::"+e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR,schINIDs,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"NodeServlet[initialize]","","","","Exception while initialize IN Scheduler Parameters Exception:"+e.getMessage());
        }//end of catch-Exception
        finally
        {
        	LogFactory.printLog("initialize", "Exited", log);
        }//end finally
    }//end of initialize
    /**
     * This method is used to load the NODE parameters for the interface ids, supplied as argument
     * @param	String p_interfaceId
     */
    public void initialize(String p_interfaceId)
    {
    	LogFactory.printLog("initialize", "Entered p_interfaceId::"+p_interfaceId, log);
        
        try
        {
            if(!BTSLUtil.isNullString(p_interfaceId))
	            NodeManager.initialize(p_interfaceId);
        }
        catch(BTSLBaseException be)
        {
        	log.errorTrace("Exception",be);
            log.error("initialize","BTSLBaseException be::"+be.getMessage());
        }
        catch(Exception e)
        {
        	log.errorTrace("Exception",e);
            log.error("initialize","Exception e::"+e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR,p_interfaceId,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"NodeServlet[initialize]","","","","Exception while initialize IN Scheduler Parameters Exception:"+e.getMessage());
        }//end of catch-Exception
        finally
        {
        	LogFactory.printLog("initialize", "Exited", log);
            if(log.isDebugEnabled()) log.debug("initialize","Exited");
        }//end of finally
    }//end of initialize
}
