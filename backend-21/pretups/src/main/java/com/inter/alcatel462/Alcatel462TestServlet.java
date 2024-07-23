package com.inter.alcatel462;

/**
 * @(#)Alcatel462TestServlet.java
 *                                Copyright(c) 2011, COMVIVA TECHNOLOGIES
 *                                LIMITED. All rights reserved.
 *                                COMVIVA PROPRIETARY/CONFIDENTIAL. Use is
 *                                subject to license terms.
 *                                All Rights Reserved
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Vinay Kumar Singh January 25, 2011 Initial
 *                                Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                ----
 *                                This class can be used to simulate IN.
 */
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.btsl.pretups.inter.module.InterfaceUtil;

public class Alcatel462TestServlet extends HttpServlet {
    private static final long serialVersionUID = 8977293968931195220L;

    /**
     * Constructor of the object.
     */
    public Alcatel462TestServlet() {
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
        Alcatel462TestUtility alcatel462TestUtility = null;
        try {
            requestAction = request.getParameter("action");
            interfaceID = request.getParameter("INTERFACE_ID");
            cardGroupSelector = request.getParameter("CARD_GROUP_SELECTOR");
            if (InterfaceUtil.isNullString(requestAction))
                requestAction = "0";
            alcatel462TestUtility = new Alcatel462TestUtility(requestAction, interfaceID, cardGroupSelector);
            alcatel462TestUtility.executeINMethods(requestAction);
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
