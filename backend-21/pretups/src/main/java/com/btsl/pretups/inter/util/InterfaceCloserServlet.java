package com.btsl.pretups.inter.util;

/**
 * @InterfaceCloserServlet.java
 *                              Copyright(c) 2007, Bharti Telesoft Int. Public
 *                              Ltd.
 *                              All Rights Reserved
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Ashish Kumar Mar 16, 2007 Initial Creation
 *                              ------------------------------------------------
 *                              ------------------------------------------------
 */
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
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.Constants;

public class InterfaceCloserServlet extends HttpServlet {
  
    private static final Log _log = LogFactory
			.getLog(InterfaceCloserServlet.class.getName());
    private InterfaceCloserController _interfaceCloserController = null;

    /**
     * Constructor of the object.
     */
    public InterfaceCloserServlet() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy();
        if (_log.isDebugEnabled())
            _log.debug("destroy ", "Entered");
        String interfaceCloserIDs = Constants.getProperty("INTERFACE_CLOSER_IN_IDS");
        if (_log.isDebugEnabled())
            _log.debug("destroy", "interfaceCloserIDs = " + interfaceCloserIDs);
        if (!InterfaceUtil.isNullString(interfaceCloserIDs))
            _interfaceCloserController.destroy(interfaceCloserIDs.trim());
        if (_log.isDebugEnabled())
            _log.debug("destroy ", "Exited");
    }

    /**
     * The doGet method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to get.
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
        String interfaceID = request.getParameter("INTERFACE_ID");
        if (_log.isDebugEnabled())
            _log.debug("doGet", "action =" + action + " interfaceID =" + interfaceID);
        if (InterfaceUtil.isNullString(action))
            return;
        if (action.equals("UPDATE")) {
            if (!InterfaceUtil.isNullString(interfaceID)) {
                // Destroys the pooled objects for the given interface id or
                // ids(if present would comma seprated.)
                destroyInterfaceClosur(interfaceID.trim());
                update(interfaceID.trim());
            } else {
                // Destroys the pooled objects for All the interface id or ids
                // defined in the constant props.
                destroy();
                initialize();
            }
        }// end if
        if (_log.isDebugEnabled())
            _log.debug("doGet", "Exited");
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
     * Initialization of the servlet. <br>
     * 
     * @throws ServletException
     *             if an error occure
     */
    public void init() throws ServletException {
        // Put your code here
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
     * This method is used to initialize Interface closer parameters for the
     * interface ids defined in the constant props
     * 
     * @return void
     */
    public void initialize() {
        if (_log.isDebugEnabled())
            _log.debug("initialize", "Entered");
        String interfaceCloserIDs = null;
        try {
            _interfaceCloserController = new InterfaceCloserController();
            interfaceCloserIDs = Constants.getProperty("INTERFACE_CLOSER_IN_IDS");
            if (_log.isDebugEnabled())
                _log.debug("initialize", "interfaceCloserIDs = " + interfaceCloserIDs);
            if (!InterfaceUtil.isNullString(interfaceCloserIDs)) {
                _interfaceCloserController.initialize(interfaceCloserIDs);
            }
        } catch (BTSLBaseException be) {
            _log.error("initialize", "BTSLBaseException be::" + be.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("initialize", "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, interfaceCloserIDs, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceCloserServlet[initialize]", "", "", "", "Exception while initializing interface closer paramerers  Exception:" + e.getMessage());
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
    public void update(String p_interfaceId) {
        if (_log.isDebugEnabled())
            _log.debug("update", "Entered p_interfaceId::" + p_interfaceId);
        try {
            _interfaceCloserController = new InterfaceCloserController();
            if (!InterfaceUtil.isNullString(p_interfaceId)) {
                _interfaceCloserController.update(p_interfaceId);
            }
        } catch (BTSLBaseException be) {
            _log.error("update", "BTSLBaseException be::" + be.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("update", "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, p_interfaceId, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceCloserServlet[update]", "", "", "", "Exception while initializing interface closer paramerers Exception:" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("update", "Exited");
        }// end of finally
    }// end of initialize

    public void destroyInterfaceClosur(String p_interfaceID) {
        super.destroy();
        if (_log.isDebugEnabled())
            _log.debug("destroy ", "Entered with p_interfaceID=" + p_interfaceID);
        _interfaceCloserController.destroy(p_interfaceID);
        if (_log.isDebugEnabled())
            _log.debug("destroy ", "Exited");
    }

}
