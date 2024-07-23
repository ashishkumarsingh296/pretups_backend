/*
 * Created on Jul 24, 2007
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.inter.module;

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
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class HandlerUtilityLoaderServlet extends HttpServlet {
	private static final Log _log = LogFactory
			.getLog(HandlerUtilityLoaderServlet.class.getName());
    private HandlerUtilityManager _utilityManager = null;

    /**
     * Constructor of the object.
     */
    public HandlerUtilityLoaderServlet() {
        super();
    }

    /**
     * Initialization of the servlet. <br>
     * 
     * @throws ServletException
     *             if an error occure
     */
    public void init() throws ServletException {
    }

    /**
     * Initialization of the servlet. <br>
     * 
     * @throws ServletException
     *             if an error occure
     */
    public void init(ServletConfig config) throws ServletException {
        if (_log.isDebugEnabled())
            _log.debug("init-Config", "Entered");
        super.init(config);
        initialize();
        if (_log.isDebugEnabled())
            _log.debug("init-Config", "Exit");
    }

    /**
     * The doPost method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to
     * post.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * The doGet method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to get.
     * This method takes the value of action and interface id,submitted as part
     * of request.
     * Based on the action value,it does the following
     * 1.If action is NULL return to the method.
     * 2.If value of action is RELOAD, this method checks the value of interface
     * id and make a call to destroy and
     * initialize method and pass the interface id as method argument.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (_log.isDebugEnabled())
            _log.debug("doGet", "Entered");
        String action = request.getParameter("action");
        if (_log.isDebugEnabled())
            _log.debug("doGet", "action =" + BTSLUtil.logForgingReqParam(action));
        if (action == null)
            return;
        if (action.equals("RELOAD")) {
            destroyIDs();
            initialize();
        }// end if
        if (_log.isDebugEnabled())
            _log.debug("doGet", "Exited");
    }

   
    /**
     * Destruction of the servlet. <br>
     */
    public void destroyIDs() {
        super.destroy();
        if (_log.isDebugEnabled())
            _log.debug("destroyIDs ", "Entered");
        String dbPoolIDs = Constants.getProperty("COMMON_HANDLER_UTILITY_IDS");
        if (_log.isDebugEnabled())
            _log.debug("destroyIDs", "dbPoolIDs = " + dbPoolIDs);
        if (!InterfaceUtil.isNullString(dbPoolIDs))
            _utilityManager.destroy(dbPoolIDs.trim(), "ALL");
        if (_log.isDebugEnabled())
            _log.debug("destroyIDs ", "Exited");
    }

    /**
     * This method is used to instantiate the DBPoolManager for the interface
     * ids defined in the constant props
     * 
     * @return void
     */
    public void initialize() {
        if (_log.isDebugEnabled())
            _log.debug("initialize", "Entered");
        String dbINIds = null;
        String useCommonHandlerUtility = null;
        try {
            _utilityManager = new HandlerUtilityManager();
            dbINIds = Constants.getProperty("COMMON_HANDLER_UTILITY_IDS");
            useCommonHandlerUtility = Constants.getProperty("COMMON_UTILITY");
            if (_log.isDebugEnabled())
                _log.debug("initialize", "dbINIds = " + dbINIds);
            // Initializes the MobiDBPoolManager if there are ID defined under
            // key SUBS_TYPE_DB_POOL_ID.
            if (!InterfaceUtil.isNullString(dbINIds))
                _utilityManager.initialize(dbINIds, useCommonHandlerUtility);
        } catch (BTSLBaseException be) {
            _log.error("initialize", "BTSLBaseException be::" + be.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("initialize", "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, dbINIds, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandlerUtilityLoaderServlet[initialize]", "", "", "", "Exception while initialize HandlerUtilityManager Exception:" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("initialize", "Exited");
        }// end finally
    }// end of initialize

    /**
     * This method is used to instantiate the PoolManager for the interface ids,
     * supplied as argument
     * 
     * @param String
     *            p_interfaceId
     */
    public void initialize(String p_interfaceId) {
        if (_log.isDebugEnabled())
            _log.debug("initialize", "Entered p_interfaceId::" + p_interfaceId);
        try {
            _utilityManager = new HandlerUtilityManager();
            if (!InterfaceUtil.isNullString(p_interfaceId))
                _utilityManager.initialize(p_interfaceId, "N");
        } catch (BTSLBaseException be) {
            _log.error("initialize", "BTSLBaseException be::" + be.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("initialize", "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, p_interfaceId, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "HandlerUtilityLoaderServlet[initialize]", "", "", "", "Exception while initialize HandlerUtilityManager Parameters Exception:" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("initialize", "Exited");
        }// end of finally
    }// end of initialize
}
