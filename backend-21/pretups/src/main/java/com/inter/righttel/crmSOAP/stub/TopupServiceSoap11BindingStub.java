/**
 * TopupServiceSoap11BindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.righttel.crmSOAP.stub;

public class TopupServiceSoap11BindingStub extends org.apache.axis.client.Stub implements com.inter.righttel.crmSOAP.stub.TopupServicePortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[5];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("queryTopup");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "QueryTopupRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "QueryTopupRequestType"), com.inter.righttel.crmSOAP.stub.QueryTopupRequestType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "QueryTopupResponseType"));
        oper.setReturnClass(com.inter.righttel.crmSOAP.stub.QueryTopupResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "QueryTopupResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("topup");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "TopupRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "topupRequestType"), com.inter.righttel.crmSOAP.stub.TopupRequestType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "TopupResponseType"));
        oper.setReturnClass(com.inter.righttel.crmSOAP.stub.TopupResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "TopupResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("initTopup");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "InitTopupRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "InitTopupRequestType"), com.inter.righttel.crmSOAP.stub.InitTopupRequestType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "InitTopupResponseType"));
        oper.setReturnClass(com.inter.righttel.crmSOAP.stub.InitTopupResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "InitTopupResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("cancelTopup");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "CancelTopupRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "CancelTopupRequestType"), com.inter.righttel.crmSOAP.stub.CancelTopupRequestType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "CancelTopupResponseType"));
        oper.setReturnClass(com.inter.righttel.crmSOAP.stub.CancelTopupResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "CancelTopupResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("validateTopup");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "ValidateTopupRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "ValidateTopupRequestType"), com.inter.righttel.crmSOAP.stub.ValidateTopupRequestType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "ValidateTopupResponseType"));
        oper.setReturnClass(com.inter.righttel.crmSOAP.stub.ValidateTopupResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "ValidateTopupResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

    }

    public TopupServiceSoap11BindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public TopupServiceSoap11BindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public TopupServiceSoap11BindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "AccountType");
            cachedSerQNames.add(qName);
            cls = com.inter.righttel.crmSOAP.stub.AccountType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "BaseRequestType");
            cachedSerQNames.add(qName);
            cls = com.inter.righttel.crmSOAP.stub.BaseRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "CancelTopupRequestType");
            cachedSerQNames.add(qName);
            cls = com.inter.righttel.crmSOAP.stub.CancelTopupRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "CancelTopupResponseType");
            cachedSerQNames.add(qName);
            cls = com.inter.righttel.crmSOAP.stub.CancelTopupResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "InitTopupRequestType");
            cachedSerQNames.add(qName);
            cls = com.inter.righttel.crmSOAP.stub.InitTopupRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "InitTopupResponseType");
            cachedSerQNames.add(qName);
            cls = com.inter.righttel.crmSOAP.stub.InitTopupResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "MSISDN");
            cachedSerQNames.add(qName);
            cls = com.inter.righttel.crmSOAP.stub.MSISDN.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "QueryTopupRequestType");
            cachedSerQNames.add(qName);
            cls = com.inter.righttel.crmSOAP.stub.QueryTopupRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "QueryTopupResponseType");
            cachedSerQNames.add(qName);
            cls = com.inter.righttel.crmSOAP.stub.QueryTopupResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "SourceType");
            cachedSerQNames.add(qName);
            cls = com.inter.righttel.crmSOAP.stub.SourceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "SubscriberType");
            cachedSerQNames.add(qName);
            cls = com.inter.righttel.crmSOAP.stub.SubscriberType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "topupRequestType");
            cachedSerQNames.add(qName);
            cls = com.inter.righttel.crmSOAP.stub.TopupRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "TopupResponseType");
            cachedSerQNames.add(qName);
            cls = com.inter.righttel.crmSOAP.stub.TopupResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "ValidateTopupRequestType");
            cachedSerQNames.add(qName);
            cls = com.inter.righttel.crmSOAP.stub.ValidateTopupRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "ValidateTopupResponseType");
            cachedSerQNames.add(qName);
            cls = com.inter.righttel.crmSOAP.stub.ValidateTopupResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public com.inter.righttel.crmSOAP.stub.QueryTopupResponseType queryTopup(com.inter.righttel.crmSOAP.stub.QueryTopupRequestType parameters1) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.inew-cs.com/mvno/integration/TopupService/queryTopup");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "queryTopup"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters1});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.righttel.crmSOAP.stub.QueryTopupResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.righttel.crmSOAP.stub.QueryTopupResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.righttel.crmSOAP.stub.QueryTopupResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.inter.righttel.crmSOAP.stub.TopupResponseType topup(com.inter.righttel.crmSOAP.stub.TopupRequestType parameters1) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.inew-cs.com/mvno/integration/TopupService/topup");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "topup"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters1});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.righttel.crmSOAP.stub.TopupResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.righttel.crmSOAP.stub.TopupResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.righttel.crmSOAP.stub.TopupResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.inter.righttel.crmSOAP.stub.InitTopupResponseType initTopup(com.inter.righttel.crmSOAP.stub.InitTopupRequestType parameters1) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.inew-cs.com/mvno/integration/TopupService/initTopup");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "initTopup"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters1});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.righttel.crmSOAP.stub.InitTopupResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.righttel.crmSOAP.stub.InitTopupResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.righttel.crmSOAP.stub.InitTopupResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.inter.righttel.crmSOAP.stub.CancelTopupResponseType cancelTopup(com.inter.righttel.crmSOAP.stub.CancelTopupRequestType parameters1) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.inew-cs.com/mvno/integration/TopupService/cancelTopup");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "cancelTopup"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters1});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.righttel.crmSOAP.stub.CancelTopupResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.righttel.crmSOAP.stub.CancelTopupResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.righttel.crmSOAP.stub.CancelTopupResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.inter.righttel.crmSOAP.stub.ValidateTopupResponseType validateTopup(com.inter.righttel.crmSOAP.stub.ValidateTopupRequestType parameters1) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.inew-cs.com/mvno/integration/TopupService/validateTopup");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "validateTopup"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters1});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.righttel.crmSOAP.stub.ValidateTopupResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.righttel.crmSOAP.stub.ValidateTopupResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.righttel.crmSOAP.stub.ValidateTopupResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
