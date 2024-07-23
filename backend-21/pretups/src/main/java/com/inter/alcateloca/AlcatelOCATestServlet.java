package com.inter.alcateloca;

/**
 * @(#)AlcatelOCATestServlet.java
 *                                Copyright(c) 2009, Comviva Technologies Ltd.
 *                                All Rights Reserved
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Vinay Kumar Singh Aug 04, 2009 Initial
 *                                Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                ----
 *                                This is a servlet class for testing.
 */

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.btsl.pretups.inter.module.InterfaceUtil;

public class AlcatelOCATestServlet extends HttpServlet {

    /**
     * Constructor of the object.
     */
    public AlcatelOCATestServlet() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
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
        doPost(request, response);
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
        String requestAction = null;
        String interfaceID = null;
        String cardGroupSelector = null;
        AlcatelOCATestUtility AlcatelOCATestUtility = null;
        try {
            requestAction = request.getParameter("action");
            interfaceID = request.getParameter("INTERFACE_ID");
            cardGroupSelector = request.getParameter("CARD_GROUP_SELECTOR");
            if (InterfaceUtil.isNullString(requestAction))
                requestAction = "0";
            AlcatelOCATestUtility = new AlcatelOCATestUtility(requestAction, interfaceID, cardGroupSelector);
            AlcatelOCATestUtility.executeINMethods(requestAction);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception e:" + e.getMessage());
        }
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

}
