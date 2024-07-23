/*
 * Created on June 18, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.vodafoneghana.locationservice;

import gh.com.vodafone.locationsvc.svcintfc.NameValue;
import gh.com.vodafone.locationsvc.svcintfc.Parameters;
import gh.com.vodafone.locationsvc.svcintfc.ServiceHeader;
import gh.com.vodafone.locationsvc.svcintfc.ServiceRequest;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

/**
 * @author vipan.kumar
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class LSWSRequestFormatter {
    public static Log _log = LogFactory.getLog(LSWSRequestFormatter.class);
    String lineSep = null;
    String _soapAction = "";

    public LSWSRequestFormatter() {
        // lineSep = System.getProperty("line.separator")+"\r";
        lineSep = System.getProperty("line.separator") + "";
    }

    /**
     * This method is used to parse the response string based on the type of
     * Action.
     * 
     * @param int p_action
     * @param HashMap
     *            p_map
     * @return String.
     * @throws Exception
     */
    protected Object generateRequest(int p_action, HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action::" + p_action + " map::" + p_map);
        Object object = null;
        p_map.put("action", String.valueOf(p_action));
        try {
            switch (p_action) {
            case LSWSI.ACTION_LS_VALIDATE: {
                ServiceRequest recargasRequest = generateLocationServiceRequest(p_map);
                object = (Object) recargasRequest;
                break;
            }
            }
        } catch (Exception e) {
            _log.error("generateRequest", "Exception e ::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest", "Exited Request String: object::" + object);
        }
        return object;
    }

    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private ServiceRequest generateLocationServiceRequest(HashMap p_requestMap) throws Exception {
        final String methodName = "LSWSRequestFormatter[generateLocationServiceRequest]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_requestMap::" + p_requestMap);
        ServiceRequest serviceRequest = null;
        try {
            serviceRequest = new ServiceRequest();
            ServiceHeader header = new ServiceHeader();
            header.setTransactionID(p_requestMap.get("IN_TXN_ID").toString());
            header.setSource(p_requestMap.get("LS_SOURCE").toString());
            header.setReplyExpected(p_requestMap.get("LS_REPLY_EXPECTED").toString());
            Parameters parameters = new Parameters();

            parameters.getParameter();
            NameValue nameValue = new NameValue();
            nameValue.setName("MSISDN");
            nameValue.setValue(p_requestMap.get("MSISDN").toString());

            parameters.getParameter().add(nameValue);

            serviceRequest.setServiceHeader(header);
        } catch (Exception e) {
            _log.error(methodName, e.getMessage());
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting Request serviceRequest::" + serviceRequest);
        }
        return serviceRequest;
    }

}
