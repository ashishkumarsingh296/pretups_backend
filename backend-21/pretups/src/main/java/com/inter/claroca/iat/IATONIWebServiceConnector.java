package com.inter.claroca.iat;

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
import com.btsl.pretups.inter.cache.FileCache;

public class IATONIWebServiceConnector {
    private Log _log = LogFactory.getLog(IATONIWebServiceConnector.class.getName());

    // public constructor()
    public IATONIWebServiceConnector() {
    }

    public Call callONIWebService(String p_interfaceID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("callONIWebService", "Entered p_interfaceID:" + p_interfaceID);
        Call call = null;
        try {

            Service service = new Service();
            try {
                call = (Call) service.createCall();
            } catch (ServiceException se) {
                throw se;
            }
            call.setTargetEndpointAddress(FileCache.getValue(p_interfaceID, "IP_LOCAL_HOSTNAME"));
            call.setUseSOAPAction(true);
            call.setSOAPActionURI("http://com/wha/iat/pretups/ws");
            OperationDesc oper = new OperationDesc();
            oper.setName("rechargeRequest");
            oper.addParameter(new ParameterDesc(new QName("http://com/wha/iat/pretups/ws", "reqXml"), org.apache.axis.description.ParameterDesc.IN, new QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false));
            call.setOperation(oper);
            call.setOperationName(new javax.xml.namespace.QName("http://com/wha/iat/pretups/ws", "rechargeRequest"));
            call.setEncodingStyle(null);
            /*
             * Creating the SOAP Header for the Request
             */
            SOAPHeaderElement soapHeader = new SOAPHeaderElement(new QName("authHeader"));
            soapHeader.setActor(null);
            try {
                soapHeader.addChildElement("UserName").addTextNode(FileCache.getValue(p_interfaceID, "USER_NAME"));
                soapHeader.addChildElement("Password").addTextNode(FileCache.getValue(p_interfaceID, "USER_PASSWORD"));
                call.addHeader(soapHeader);
            } catch (SOAPException soe) {
                throw soe;
            }

            if (_log.isDebugEnabled())
                _log.debug("callONIWebService", "Getting request Message from Call:" + call.getMessageContext().getMessage());

            return call;

        } catch (Exception e) {
            e.printStackTrace();
            _log.error("callONIWebService", "Exception e=" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("callONIWebService", "Exited:");
        }
    }
}
