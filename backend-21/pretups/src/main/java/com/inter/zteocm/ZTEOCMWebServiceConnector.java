package com.inter.zteocm;

/**
 * @(#)ZTEOBOWebServiceConnector.java
 *                                    Copyright(c) 2011, Comviva Technologies
 *                                    Ltd.
 *                                    All Rights Reserved
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Vinay Kumar Singh April 09, 2011 Initial
 *                                    Creation
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    ------------
 */
import java.util.HashMap;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.xml.soap.SOAPException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.message.SOAPHeaderElement;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class ZTEOCMWebServiceConnector {
    private Log _log = LogFactory.getLog(ZTEOCMWebServiceConnector.class.getName());

    public ZTEOCMWebServiceConnector() {
    }

    public Call callService(HashMap<String, String> p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("callService", "Entered p_map:" + p_map);

        Call call = null;
        try {
            Service service = new Service();
            try {
                call = (Call) service.createCall();

            } catch (ServiceException se) {
                throw se;
            }
            call.setTargetEndpointAddress((String) p_map.get("END_POINT"));
            // set soap action:
            call.setUseSOAPAction(true);
            call.setSOAPActionURI((String) p_map.get("SOAP_ACTION_URI"));
            // set operation
            OperationDesc oper = new OperationDesc();
            oper.setName((String) p_map.get("OPERATION_NAME"));
            // set input parameter description
            oper.addParameter(new ParameterDesc(new QName((String) p_map.get("ZTE_NAMESPACE"), "reqXml"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false));
            call.setOperation(oper);
            call.setOperationName(new javax.xml.namespace.QName((String) p_map.get("ZTE_NAMESPACE"), (String) p_map.get("OPERATION_NAME")));
            call.setEncodingStyle(null);
            // set soap header
            SOAPHeaderElement soapHeader = new SOAPHeaderElement(new QName((String) p_map.get("AUTH_HEADER")));
            soapHeader.setActor(null);
            try {
                soapHeader.addChildElement("Username").addTextNode((String) p_map.get("USER_NAME"));
                soapHeader.addChildElement("Password").addTextNode((String) p_map.get("PASSWORD"));
                call.addHeader(soapHeader);
            } catch (SOAPException soe) {
                throw soe;
            }
            return call;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("callService", "Exception e=" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("callService", "Exited p_map:" + p_map);
        }
    }
}
