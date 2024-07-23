package com.inter.ferma;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class FermaBalanceTest extends HttpServlet {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (_log.isDebugEnabled())
            _log.debug("FermaBalanceTest mamammamamma", "Entered ");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;
        String message = null;
        try {
            FermaINHandler inhandler = new FermaINHandler();
            HashMap _requestMap = new HashMap();
            _requestMap.put("IN_TXN_ID", "5846111");
            _requestMap.put("FERMA_INTERFACE_ID", "329f3d:107c6f0cd2f:-7e22");
            _requestMap.put("MSISDN", "0123456001");
            _requestMap.put("AccessType", "1");
            _requestMap.put("AccountId", "0");
            _requestMap.put("Profile", "1");
            _requestMap.put("AccountStatus", "A");
            _requestMap.put("LockStatus", "0");
            _requestMap.put("RechInstallment", "1");
            _requestMap.put("BalanceId", "0");
            _requestMap.put("LifeCycle", "1");
            _requestMap.put("Option", "0");
            _requestMap.put("Amount", "1000");
            _requestMap.put("UnitType", "0");
            _requestMap.put("RechargeValue", "300");
            _requestMap.put("CurrentValidityDate", "20051111");
            _requestMap.put("CurrentGraceDate", "20052011");
            _requestMap.put("NewValidityDate", "");
            _requestMap.put("NewGraceDate", "");
            _requestMap.put("CurrentState", "A");
            _requestMap.put("INTERFACE_ID", "INTID00089");
            // inhandler.balance(requestMap); // for get account info
            inhandler.validate(_requestMap); // for validate user account
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
