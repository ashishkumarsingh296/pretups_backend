package com.inter.huawei84;

/**
 * @(#)Huawei84PoolLoaderServlet.java
 *                                    Copyright(c) 2007, Bharti Telesoft Int.
 *                                    Public Ltd.
 *                                    All Rights Reserved
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Vinay Kumar Singh December 26, 2007
 *                                    Initial Creation
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -----------
 *                                    This class is responsible to instantiate
 *                                    the PoolManager either at the server
 *                                    start-up or
 *                                    when a corresponding URL is submitted
 *                                    through browser.
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

public class Huawei84PoolLoaderServlet extends HttpServlet {
    private Log _log = LogFactory.getLog(Huawei84PoolLoaderServlet.class.getName());
    private Huawei84PoolManager _poolManager = null;
    private Huawei84HeartBeatController _huaweiHeartController = null;

    /**
     * Constructor of the object.
     */
    public Huawei84PoolLoaderServlet() {
        super();
    }

    /**
     * Initialization of the servlet.
     * 
     * @throws ServletException
     *             if an error occur
     */
    public void init() throws ServletException {
    }

    /**
     * Initialization of the servlet.
     * 
     * @throws ServletException
     *             if an error occur
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
     * The doPost method of the servlet.
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
     * The doGet method of the servlet.
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
        String interfaceID = request.getParameter("INTERFACE_ID");
        if (_log.isDebugEnabled())
            _log.debug("doGet", "action =" + action + " interfaceID =" + interfaceID);
        if (action == null)
            return;
        if (action.equals("RELOAD")) {
            if (!InterfaceUtil.isNullString(interfaceID)) {
                // Destroys the pooled objects for the given interface id or
                // ids(if present would comma seprated.)
                destroy(interfaceID.trim());
                initialize(interfaceID.trim());
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

    public void destroy(String p_interfaceID) {
        super.destroy();
        if (_log.isDebugEnabled())
            _log.debug("destroy ", "Entered with p_interfaceID=" + p_interfaceID);
        _poolManager.destroy(p_interfaceID);
        _huaweiHeartController.stopHeartBeat(p_interfaceID);
        if (_log.isDebugEnabled())
            _log.debug("destroy ", "Exited");
    }

    /**
     * Destruction of the servlet.
     */
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
        if (_log.isDebugEnabled())
            _log.debug("destroy ", "Entered");
        String poolIDs = Constants.getProperty("HUAWEI84_POOL_IN_IDS");
        if (_log.isDebugEnabled())
            _log.debug("destroy", "poolIDs = " + poolIDs);
        if (!InterfaceUtil.isNullString(poolIDs)) {
            _poolManager.destroy(poolIDs.trim(), "ALL");
            _huaweiHeartController.stopHeartBeat(poolIDs);
        }
        if (_log.isDebugEnabled())
            _log.debug("destroy ", "Exited");
    }

    /**
     * This method is used to instantiate the PoolManager for the interface ids
     * defined in the constant props
     * 
     * @return void
     */
    public void initialize() {
        if (_log.isDebugEnabled())
            _log.debug("initialize", "Entered");
        String poolINIDs = null;
        try {
            _poolManager = new Huawei84PoolManager();
            poolINIDs = Constants.getProperty("HUAWEI84_POOL_IN_IDS");
            if (_log.isDebugEnabled())
                _log.debug("initialize", "poolINIDs = " + poolINIDs);
            if (!InterfaceUtil.isNullString(poolINIDs)) {
                _poolManager.initialize(poolINIDs);
                _huaweiHeartController = new Huawei84HeartBeatController(poolINIDs);
            }
        } catch (BTSLBaseException be) {
            _log.error("initialize", "BTSLBaseException be::" + be.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("initialize", "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, poolINIDs, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PoolLoader[initialize]", "", "", "", "Exception while initialize IN PoolManager Exception:" + e.getMessage());
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
            _poolManager = new Huawei84PoolManager();
            if (!InterfaceUtil.isNullString(p_interfaceId)) {
                _poolManager.initialize(p_interfaceId);
                _huaweiHeartController = new Huawei84HeartBeatController(p_interfaceId);
            }
        } catch (BTSLBaseException be) {
            _log.error("initialize", "BTSLBaseException be::" + be.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("initialize", "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, p_interfaceId, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PoolLoader[initialize]", "", "", "", "Exception while initialize IN PoolManager Parameters Exception:" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("initialize", "Exited");
        }// end of finally
    }// end of initialize
}