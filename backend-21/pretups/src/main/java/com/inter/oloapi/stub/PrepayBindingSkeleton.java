/**
 * PrepayBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.oloapi.stub;

public class PrepayBindingSkeleton implements com.inter.oloapi.stub.PrepayPortType, org.apache.axis.wsdl.Skeleton {
    private com.inter.oloapi.stub.PrepayPortType impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:prepay", "ReversePaymentRequest"), com.inter.oloapi.stub.ReversePaymentRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("reversePayment", _params, new javax.xml.namespace.QName("", "return"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn:prepay", "ReversePaymentResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("prepay", "reversePayment"));
        _oper.setSoapAction("prepay#reversePayment");
        _myOperationsList.add(_oper);
        if (_myOperations.get("reversePayment") == null) {
            _myOperations.put("reversePayment", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("reversePayment")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:prepay", "PrepayRechargeRequest"), com.inter.oloapi.stub.PrepayRechargeRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("prepayRecharge", _params, new javax.xml.namespace.QName("", "return"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn:prepay", "PrepayRechargeResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("prepay", "prepayRecharge"));
        _oper.setSoapAction("prepay#prepayRecharge");
        _myOperationsList.add(_oper);
        if (_myOperations.get("prepayRecharge") == null) {
            _myOperations.put("prepayRecharge", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("prepayRecharge")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:prepay", "CheckStatusRequest"), com.inter.oloapi.stub.CheckStatusRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("checkStatus", _params, new javax.xml.namespace.QName("", "return"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn:prepay", "CheckStatusResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("prepay", "checkStatus"));
        _oper.setSoapAction("prepay#checkStatus");
        _myOperationsList.add(_oper);
        if (_myOperations.get("checkStatus") == null) {
            _myOperations.put("checkStatus", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("checkStatus")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:prepay", "ValidateCustomerRequest"), com.inter.oloapi.stub.ValidateCustomerRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("validateCustomer", _params, new javax.xml.namespace.QName("", "return"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn:prepay", "ValidateCustomerResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("prepay", "validateCustomer"));
        _oper.setSoapAction("prepay#validateCustomer");
        _myOperationsList.add(_oper);
        if (_myOperations.get("validateCustomer") == null) {
            _myOperations.put("validateCustomer", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("validateCustomer")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:prepay", "ValidateCustomerPreTupsRequest"), com.inter.oloapi.stub.ValidateCustomerPreTupsRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("validateCustomerPreTups", _params, new javax.xml.namespace.QName("", "return"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn:prepay", "ValidateCustomerPreTupsResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("prepay", "validateCustomerPreTups"));
        _oper.setSoapAction("prepay#validateCustomerPreTups");
        _myOperationsList.add(_oper);
        if (_myOperations.get("validateCustomerPreTups") == null) {
            _myOperations.put("validateCustomerPreTups", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("validateCustomerPreTups")).add(_oper);
    }

    public PrepayBindingSkeleton() {
        this.impl = new com.inter.oloapi.stub.PrepayBindingImpl();
    }

    public PrepayBindingSkeleton(com.inter.oloapi.stub.PrepayPortType impl) {
        this.impl = impl;
    }
    public com.inter.oloapi.stub.ReversePaymentResponse reversePayment(com.inter.oloapi.stub.ReversePaymentRequest request) throws java.rmi.RemoteException
    {
        com.inter.oloapi.stub.ReversePaymentResponse ret = impl.reversePayment(request);
        return ret;
    }

    public com.inter.oloapi.stub.PrepayRechargeResponse prepayRecharge(com.inter.oloapi.stub.PrepayRechargeRequest request) throws java.rmi.RemoteException
    {
        com.inter.oloapi.stub.PrepayRechargeResponse ret = impl.prepayRecharge(request);
        return ret;
    }

    public com.inter.oloapi.stub.CheckStatusResponse checkStatus(com.inter.oloapi.stub.CheckStatusRequest req) throws java.rmi.RemoteException
    {
        com.inter.oloapi.stub.CheckStatusResponse ret = impl.checkStatus(req);
        return ret;
    }

    public com.inter.oloapi.stub.ValidateCustomerResponse validateCustomer(com.inter.oloapi.stub.ValidateCustomerRequest request) throws java.rmi.RemoteException
    {
        com.inter.oloapi.stub.ValidateCustomerResponse ret = impl.validateCustomer(request);
        return ret;
    }

    public com.inter.oloapi.stub.ValidateCustomerPreTupsResponse validateCustomerPreTups(com.inter.oloapi.stub.ValidateCustomerPreTupsRequest request) throws java.rmi.RemoteException
    {
        com.inter.oloapi.stub.ValidateCustomerPreTupsResponse ret = impl.validateCustomerPreTups(request);
        return ret;
    }

}
