/*
 * Created on Jul 31, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.ericssion;

import java.io.IOException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.Constants;

/**
 * @author ashishk
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class EricssionConnectionServlet extends HttpServlet {
    private static Log _log = LogFactory.getLog(EricssionConnectionServlet.class.getName());
    private EricSocketConnectionPool _pool = null;

    /**
     * Constructor of the object.
     */
    public EricssionConnectionServlet() {
        super();
    }

    public void init(ServletConfig conf) throws ServletException {
        System.out.println("EricssionConnectionServlet init() ::EricssionConnectionServlet Entered ");
        super.init(conf);
        initialize();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
        if (_log.isDebugEnabled())
            _log.debug("destroy ", "Entered");
        String poolIDs = Constants.getProperty("SOCKET_CONN_POOL_IDS");
        if (_log.isDebugEnabled())
            _log.debug("destroy", "poolIDs = " + poolIDs);
        if (poolIDs != null)
            _pool.destroy(poolIDs, "ALL");
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
        String action = request.getParameter("action");
        String interfaceID = request.getParameter("INTERFACE_ID");
        if (_log.isDebugEnabled())
            _log.debug("doGet", "Entered action: " + action);
        if (action == null)
            return;
        if (action.equals("RELOAD")) {
            if (interfaceID != null) {
                // Done so that if we need to make changes in the file it would
                // take the same for a particluar ID
                destroy(interfaceID);
                initialize(interfaceID);
                initializeThreads(interfaceID);
            } else {
                // Done so that if we need to make changes in the file it would
                // take the same
                destroy();
                initialize();
                initializeThreads();
            }
        } else if (action.equals("UPDATE")) {
            if (interfaceID != null) {
                // Done so that if we need to make changes in the file it would
                // take the same for a particluar ID
                updateThreads(interfaceID);
            } else {
                // Done so that if we need to make changes in the file it would
                // take the same
                updateThreads();
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("doGet", "Exiting action: " + action);
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
        if (_log.isDebugEnabled())
            _log.debug("doPost", "Entered");
        doGet(request, response);
        if (_log.isDebugEnabled())
            _log.debug("doPost", "Exiting");
    }

    /**
     * Initialization of the servlet. <br>
     * 
     * @throws ServletException
     *             if an error occure
     */
    public void initialize() {
        if (_log.isDebugEnabled())
            _log.debug("initialize ", "Entered");
        String poolIDs = Constants.getProperty("SOCKET_CONN_POOL_IDS");
        if (_log.isDebugEnabled())
            _log.debug("initialize", "poolIDs = " + poolIDs);
        if (poolIDs != null)
            _pool = new EricSocketConnectionPool(poolIDs);
    }

    /**
     * Method to initialize the complete interface ID connection details
     * 
     * @param p_interfaceID
     */
    public void initialize(String p_interfaceID) {
        if (_log.isDebugEnabled())
            _log.debug("initialize ", "Entered with p_interfaceID=" + p_interfaceID);
        _pool = new EricSocketConnectionPool(p_interfaceID);
    }

    /**
     * Method to destroy the interface ID socket connection
     * 
     * @param p_interfaceID
     */
    public void destroy(String p_interfaceID) {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
        if (_log.isDebugEnabled())
            _log.debug("destroy ", "Entered with p_interfaceID=" + p_interfaceID);
        _pool.destroy(p_interfaceID);
    }

    /**
     * Method to initialize IN and OUT Threads
     * 
     */
    public void initializeThreads() {
        // Put your code here
        String poolIDs = Constants.getProperty("SOCKET_CONN_POOL_IDS");
        if (_log.isDebugEnabled())
            _log.debug("initializeThreads ", "poolIDs=" + poolIDs);
        if (poolIDs != null)
            new EricClient().initializeThreads(poolIDs);
    }

    /**
     * Method to initialize IN and OUT Threads for a interface ID
     * 
     * @param p_interfaceID
     */
    public void initializeThreads(String p_interfaceID) {
        // Put your code here
        if (_log.isDebugEnabled())
            _log.debug("initializeThreads ", "Entered with p_interfaceID=" + p_interfaceID);
        new EricClient().initializeThreads(p_interfaceID);
    }

    /**
     * Method to update IN and OUT Threads with File Cache Related values
     * 
     */
    public void updateThreads() {
        // Put your code here
        String poolIDs = Constants.getProperty("SOCKET_CONN_POOL_IDS");
        if (_log.isDebugEnabled())
            _log.debug("updateThreads ", "poolIDs=" + poolIDs);
        if (poolIDs != null)
            new EricClient().updateThreads(poolIDs);
    }

    /**
     * Method to update IN and OUT Threads with File Cache Related values for an
     * interface
     * 
     * @param p_interfaceID
     */
    public void updateThreads(String p_interfaceID) {
        // Put your code here
        if (_log.isDebugEnabled())
            _log.debug("updateThreads ", "Entered with p_interfaceID=" + p_interfaceID);
        new EricClient().updateThreads(p_interfaceID);
    }
}
