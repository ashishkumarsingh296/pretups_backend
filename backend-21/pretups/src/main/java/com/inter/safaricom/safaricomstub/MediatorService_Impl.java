// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3

package com.inter.safaricom.safaricomstub;

import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.client.ServiceExceptionImpl;
import com.sun.xml.rpc.util.exception.*;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.client.HandlerChainImpl;
import javax.xml.rpc.*;
import javax.xml.rpc.encoding.*;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.namespace.QName;

public class MediatorService_Impl extends com.sun.xml.rpc.client.BasicService implements MediatorService {
    private static final QName serviceName = new QName("https://med1-svr:8443/Mediator/", "MediatorService");
    private static final QName ns1_Mediator_QNAME = new QName("https://med1-svr:8443/Mediator/", "Mediator");
    private static final Class mediator_PortClass = com.inter.safaricom.safaricomstub.Mediator.class;
    
    public MediatorService_Impl() {
        super(serviceName, new QName[] {
                        ns1_Mediator_QNAME
                    },
            new com.inter.safaricom.safaricomstub.MediatorService_SerializerRegistry().getRegistry());
        
    }
    
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, java.lang.Class serviceDefInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (portName.equals(ns1_Mediator_QNAME) &&
                serviceDefInterface.equals(mediator_PortClass)) {
                return getMediator();
            }
        } catch (Exception e) {
            throw new ServiceExceptionImpl(new LocalizableExceptionAdapter(e));
        }
        return super.getPort(portName, serviceDefInterface);
    }
    
    public java.rmi.Remote getPort(java.lang.Class serviceDefInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (serviceDefInterface.equals(mediator_PortClass)) {
                return getMediator();
            }
        } catch (Exception e) {
            throw new ServiceExceptionImpl(new LocalizableExceptionAdapter(e));
        }
        return super.getPort(serviceDefInterface);
    }
    
    public com.inter.safaricom.safaricomstub.Mediator getMediator() {
        java.lang.String[] roles = new java.lang.String[] {};
        HandlerChainImpl handlerChain = new HandlerChainImpl(getHandlerRegistry().getHandlerChain(ns1_Mediator_QNAME));
        handlerChain.setRoles(roles);
        com.inter.safaricom.safaricomstub.Mediator_Stub stub = new com.inter.safaricom.safaricomstub.Mediator_Stub(handlerChain);
        try {
            stub._initialize(super.internalTypeRegistry);
        } catch (JAXRPCException e) {
            throw e;
        } catch (Exception e) {
            throw new JAXRPCException(e.getMessage(), e);
        }
        return stub;
    }
}
