package com.btsl.loadcontroller;

import java.io.IOException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * @(#)QueueProcessingServlet.java
 *                                 Copyright(c) 2006, Bharti Telesoft Ltd.
 *                                 All Rights Reserved
 * 
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Gurjeet Bedi Sep 20, 2006 Initial Creation
 * 
 */

public class QueueProcessingServlet extends HttpServlet {
    private static final Log _log = LogFactory.getLog(QueueProcessingServlet.class.getName());
    private String sleepTimeStr = "500";

    /**
     * Constructor of the object.
     */
    public QueueProcessingServlet() {
        super();
    }

    public void init(ServletConfig conf) throws ServletException {
        if (_log.isDebugEnabled()) {
            _log.debug("QueueProcessingServlet init() ::", "QueueProcessingServlet Entered ");
        }
        super.init(conf);
        sleepTimeStr = getInitParameter("process_sleep_time");
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
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
        final String METHOD_NAME = "doGet";
        if ("RELOAD".equalsIgnoreCase(request.getParameter("action"))) {
            QueueLoadManagerThread tt = new QueueLoadManagerThread();
            if (!BTSLUtil.isNullString(request.getParameter("SLEEP_TIME"))) {
                long sleepTime = 0;
                try {
                    sleepTime = Long.parseLong(request.getParameter("SLEEP_TIME"));
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    sleepTime = 500;
                }
                tt.setSleepTime(sleepTime);
            } else {
                tt.setSleepTime(500);
            }
        } else {

            long sleepTime = 0;
            try {
                sleepTime = Long.parseLong(sleepTimeStr);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                sleepTime = 500;
            }
            QueueLoadManagerThread tt = new QueueLoadManagerThread(sleepTime);
            tt.setName("Interface Queue Manager thread");
            tt.start();
        }
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
        if (_log.isDebugEnabled()) {
            _log.debug("doPost", "Entered");
        }
        doGet(request, response);
        if (_log.isDebugEnabled()) {
            _log.debug("doPost", "Exiting");
        }
    }
}
