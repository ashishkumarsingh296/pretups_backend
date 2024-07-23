package com.inter.claroColUserInfoWS.scheduler;

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
 *                Copyright(c) 2016, Comviva Technologies Ltd.
 * 				  All Rights Reserved
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Author Date History
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Pankaj Sharma Spt 28,2016 Initial Creation
 *                 ------------------------------------------------------------
 *                 ------------------------------------
 *                 This servlet is responsible to initialize the node details at
 *                 server start up.
 */
public class NodeServlet extends HttpServlet {
    private static Log log = LogFactory.getLog(NodeServlet.class.getName());

    /**
     * Constructor of the object.
     */
    public NodeServlet() {
        super();
    }
    @Override
    public void init() {
    	//auto
    }
    @Override
    public void init(ServletConfig conf) throws ServletException {
        if (log.isDebugEnabled())
            log.debug("init", "Entered ");
        super.init(conf);
        initialize();
        if (log.isDebugEnabled())
            log.debug("init", "Exit");
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	final String methodName="doGet";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered");
        String action = request.getParameter("action");
        String interfaceID = request.getParameter("INTERFACE_ID");
        if (log.isDebugEnabled())
            log.debug(methodName, "action =" + action + " interfaceID =" + interfaceID);
        if (action == null)
            return;
        if ("RELOAD".equals(action)) {
            if (interfaceID != null)
                // Done so that if we need to make changes in the file it would
                // take the same for a particluar ID or IDs
                initialize(interfaceID);
            else
                initialize();
        }// end if
        if (log.isDebugEnabled())
            log.debug(methodName, " Exited");
    }// end doGet

    /**
     * This method is used to load the Node parameter of the interface ids
     * defined in the constant props
     * 
     * @return void
     */
    public void initialize() {
    	final String methodName="initialize";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered");
        String iNIDs = null;
        try {
            // Fetch the Scheduler IN id's from the constant props
            iNIDs = Constants.getProperty("CLARO_COL_INFO_IN_IDS");
            if (log.isDebugEnabled())
                log.debug(methodName, "iNIDs = " + iNIDs);
            
            if (!BTSLUtil.isNullString(iNIDs))
                NodeManager.initialize(iNIDs);
        } catch (BTSLBaseException be) {
            log.errorTrace("BTSLBaseException be ::"+methodName , be);
        } catch (Exception e) {
        	log.errorTrace("BTSLBaseException be:: "+methodName , e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, iNIDs, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeServlet[initialize]", "", "", "", "Exception while initialize IN Scheduler Parameters Exception:" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exited");
        }// end finally
    }// end of initialize

    /**
     * This method is used to load the NODE parameters for the interface ids,
     * supplied as argument
     * 
     * @param String
     *            pInterfaceId
     */
    public void initialize(String pInterfaceId) {
    	final String methodName="initialize";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered pInterfaceId::" + pInterfaceId);
        try {
            if (!BTSLUtil.isNullString(pInterfaceId))
                NodeManager.initialize(pInterfaceId);
        } catch (BTSLBaseException be) {
        	log.errorTrace("BTSLBaseException be::"+methodName , be);
        } catch (Exception e) {
        	log.errorTrace("BTSLBaseException be::"+methodName , e);
            log.error(methodName, "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, pInterfaceId, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeServlet[initialize]", "", "", "", "Exception while initialize IN Scheduler Parameters Exception:" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exited");
        }// end of finally
    }// end of initialize
}
