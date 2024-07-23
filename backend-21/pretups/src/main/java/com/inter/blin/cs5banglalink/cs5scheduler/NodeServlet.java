package com.inter.blin.cs5banglalink.cs5scheduler;

import com.btsl.event.EventComponentI;
import com.btsl.logging.LogFactory;
import java.io.IOException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class NodeServlet extends HttpServlet {
    private static Log _log = LogFactory.getLog(NodeServlet.class.getName());

    /**
     * Constructor of the object.
     */

    public NodeServlet() {
        super();
    }

    public void init() {

    }

    public void init(ServletConfig conf) throws ServletException {
    	final String METHOD_NAME="init";
        if (_log.isDebugEnabled())
            _log.debug(METHOD_NAME, "Entered");
        super.init(conf);
        initialize();
        if (_log.isDebugEnabled())
            _log.debug(METHOD_NAME, "Exit");
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	final String METHOD_NAME="doGet";
        if (_log.isDebugEnabled())
            _log.debug(METHOD_NAME, "Entered");
        String action = request.getParameter("action");
        String interfaceID = request.getParameter("INTERFACE_ID");

        if (_log.isDebugEnabled())
            _log.debug(METHOD_NAME, "action =" + action + " interfaceID =" + interfaceID);
        if (action == null)
            return;

        if (action.equals("RELOAD")) {
            if (interfaceID != null)
                initialize(interfaceID);// Done so that if we need to make
                                        // changes in the file it would take the
                                        // same for a particluar ID or IDs
            else
                initialize();
        }
        if (_log.isDebugEnabled())
            _log.debug(METHOD_NAME, "Exited");
    }

    /**
     * This method is used to load the Node parameter of the interface ids
     * defined in the constant props
     * 
     * @return void
     */
    public void initialize() {
    	final String METHOD_NAME="initialize";
        if (_log.isDebugEnabled())
            _log.debug(METHOD_NAME, "Entered");
        String schINIDs = null;
        try {
            schINIDs = Constants.getProperty("SCH_IN_IDS");// Fetch the
                                                           // Scheduler IN id's
                                                           // from the constant
                                                           // props
            if (_log.isDebugEnabled())
                _log.debug(METHOD_NAME, "schINIDs = " + schINIDs);
            if (!BTSLUtil.isNullString(schINIDs))
                NodeManager.initialize(schINIDs);
        } catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSLBaseException be::" + be.getMessage());
        } catch (Exception e) {
        	_log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, schINIDs, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeServlet[initialize]", "", "", "", "Exception while initialize IN Scheduler Parameters Exception:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(METHOD_NAME, "Exited");
        }
    }

    /**
     * This method is used to load the NODE parameters for the interface ids,
     * supplied as argument
     * 
     * @param String
     *            p_interfaceId
     */
    public void initialize(String p_interfaceId) {
    	final String METHOD_NAME="intialize";
        if (_log.isDebugEnabled())
            _log.debug(METHOD_NAME, "Entered p_interfaceId::" + p_interfaceId);
        try {
            if (!BTSLUtil.isNullString(p_interfaceId))
                NodeManager.initialize(p_interfaceId);
        } catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSLBaseException be::" + be.getMessage());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, p_interfaceId, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeServlet[initialize]", "", "", "", "Exception while initialize IN Scheduler Parameters Exception:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(METHOD_NAME, "Exited");
        }
    }
}
