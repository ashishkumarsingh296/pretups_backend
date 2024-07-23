package com.btsl.pretups.channel.receiver;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class ExtranetSimulator extends HttpServlet {
 
    private static final Log _log = LogFactory
			.getLog(ExtranetSimulator.class.getName());
    public void init() throws ServletException {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String METHOD_NAME = "doPost";
        if (_log.isDebugEnabled()) {
            _log.debug("ExtranetSimulator", "Entered...Connected to ExtranetSimulator");
        }
        response.setContentType("text/html");
        try {

            response.getOutputStream().print("VFEUID=superadmin&VFELANGID=eng");

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            //
            _log.error("doPost", "Exception e:" + e.getMessage());
        }// end of catch-Exception

    }

    public void destroy() {
        super.destroy();
    }

}
