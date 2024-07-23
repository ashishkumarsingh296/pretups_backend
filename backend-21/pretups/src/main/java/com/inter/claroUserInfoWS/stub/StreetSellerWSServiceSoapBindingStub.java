/**
 * StreetSellerWSServiceSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroUserInfoWS.stub;

import com.btsl.util.Constants;

public class StreetSellerWSServiceSoapBindingStub extends org.apache.axis.client.Stub implements com.inter.claroUserInfoWS.stub.StreetSellerWSSoap_PortType {
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
        oper.setName("obtenerFactura");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "facturaRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "facturaRequestType"), com.inter.claroUserInfoWS.stub.FacturaRequestType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "facturaResponseType"));
        oper.setReturnClass(com.inter.claroUserInfoWS.stub.FacturaResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "facturaResponse"));
        if(Constants.getProperty("CHANNEL_INFO_CLARO_WEBSERVICE_STYLE").equalsIgnoreCase("WRAPPED"))
        {
        	oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        }else{
        	oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        }
    
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("obtenerDeudaDistribuidor");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorDeudaTot.xsd", "distribuidorDeudaRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorDeudaTot.xsd", "distribuidorDeudaRequestType"), com.inter.claroUserInfoWS.stub.DistribuidorDeudaRequestType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorDeudaTot.xsd", "distribuidorDeudaResponseType"));
        oper.setReturnClass(com.inter.claroUserInfoWS.stub.DistribuidorDeudaResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorDeudaTot.xsd", "distribuidorDeudaResponse"));
        if(Constants.getProperty("CHANNEL_INFO_CLARO_WEBSERVICE_STYLE").equalsIgnoreCase("WRAPPED"))
        {
        	oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        }else{
        	oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        }
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("obtenerDatosDistribuidor");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "distribuidorDataRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "distribuidorDataRequestType"), com.inter.claroUserInfoWS.stub.DistribuidorDataRequestType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "distribuidorDataResponseType"));
        oper.setReturnClass(com.inter.claroUserInfoWS.stub.DistribuidorDataResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "distribuidorDataResponse"));
        if(Constants.getProperty("CHANNEL_INFO_CLARO_WEBSERVICE_STYLE").equalsIgnoreCase("WRAPPED"))
        {
        	oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        }else{
        	oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        }
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("obtenerEstadoDistribuidor");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorStatus.xsd", "distribuidorStatusRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorStatus.xsd", "distribuidorStatusRequestType"), com.inter.claroUserInfoWS.stub.DistribuidorStatusRequestType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorStatus.xsd", "distribuidorStatusResponseType"));
        oper.setReturnClass(com.inter.claroUserInfoWS.stub.DistribuidorStatusResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorStatus.xsd", "distribuidorStatusResponse"));
        if(Constants.getProperty("CHANNEL_INFO_CLARO_WEBSERVICE_STYLE").equalsIgnoreCase("WRAPPED"))
        {
        	oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        }else{
        	oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        }
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("obtenerDistribuidor");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "distribuidorDataRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "distribuidorDataRequestType"), com.inter.claroUserInfoWS.stub.DistribuidorDataRequestType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "distribuidorDataResponseType"));
        oper.setReturnClass(com.inter.claroUserInfoWS.stub.DistribuidorDataResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "distribuidorDataResponse"));
        if(Constants.getProperty("CHANNEL_INFO_CLARO_WEBSERVICE_STYLE").equalsIgnoreCase("WRAPPED"))
        {
        	oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        }else{
        	oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        }
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

    }

    public StreetSellerWSServiceSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public StreetSellerWSServiceSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public StreetSellerWSServiceSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", ">datosDistribuidor>adminName");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", ">datosDistribuidor>ciudad");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", ">datosDistribuidor>direccion");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", ">datosDistribuidor>email");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", ">datosDistribuidor>estado");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", ">datosDistribuidor>limiteCredito");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", ">datosDistribuidor>nombre");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", ">datosDistribuidor>nroDiasPago");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", ">datosDistribuidor>nroIdentFiscal");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", ">datosDistribuidor>periodoCicloPago");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", ">datosDistribuidor>periodoPago");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", ">datosDistribuidor>telefono");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "datosDistribuidor");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.DatosDistribuidor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "distribuidorDataDatosType");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.DistribuidorDataDatosType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "distribuidorDataInfoType");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.DistribuidorDataInfoType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "distribuidorDataRequestType");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.DistribuidorDataRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "distribuidorDataResponseType");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.DistribuidorDataResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorDeudaTot.xsd", "distribuidorDeudaDatosType");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.DistribuidorDeudaDatosType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorDeudaTot.xsd", "distribuidorDeudaInfoType");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.DistribuidorDeudaInfoType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorDeudaTot.xsd", "distribuidorDeudaRequestType");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.DistribuidorDeudaRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorDeudaTot.xsd", "distribuidorDeudaResponseType");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.DistribuidorDeudaResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorStatus.xsd", "distribuidorStatusInfoType");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.DistribuidorStatusInfoType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorStatus.xsd", "distribuidorStatusRequestType");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.DistribuidorStatusRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorStatus.xsd", "distribuidorStatusResponseType");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.DistribuidorStatusResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", ">datosFactura>estado");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", ">datosFactura>fechaFactura");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", ">datosFactura>impuesto");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", ">datosFactura>nombre");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", ">datosFactura>nroCliente");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", ">datosFactura>nroDocComercial");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", ">datosFactura>valorNeto");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "datosFactura");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.DatosFactura.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "facturaDatosType");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.FacturaDatosType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "facturaInfoType");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.FacturaInfoType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "facturaRequestType");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.FacturaRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "facturaResponseType");
            cachedSerQNames.add(qName);
            cls = com.inter.claroUserInfoWS.stub.FacturaResponseType.class;
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

    public com.inter.claroUserInfoWS.stub.FacturaResponseType obtenerFactura(com.inter.claroUserInfoWS.stub.FacturaRequestType facturaRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.openuri.org/obtenerFactura");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.openuri.org/", "obtenerFactura"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {facturaRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.claroUserInfoWS.stub.FacturaResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.claroUserInfoWS.stub.FacturaResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.claroUserInfoWS.stub.FacturaResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.inter.claroUserInfoWS.stub.DistribuidorDeudaResponseType obtenerDeudaDistribuidor(com.inter.claroUserInfoWS.stub.DistribuidorDeudaRequestType distribuidorDeudaRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.openuri.org/obtenerDeudaDistribuidor");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.openuri.org/", "obtenerDeudaDistribuidor"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {distribuidorDeudaRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.claroUserInfoWS.stub.DistribuidorDeudaResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.claroUserInfoWS.stub.DistribuidorDeudaResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.claroUserInfoWS.stub.DistribuidorDeudaResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.inter.claroUserInfoWS.stub.DistribuidorDataResponseType obtenerDatosDistribuidor(com.inter.claroUserInfoWS.stub.DistribuidorDataRequestType distribuidorDataRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.openuri.org/obtenerDatosDistribuidor");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.openuri.org/", "obtenerDatosDistribuidor"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {distribuidorDataRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.claroUserInfoWS.stub.DistribuidorDataResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.claroUserInfoWS.stub.DistribuidorDataResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.claroUserInfoWS.stub.DistribuidorDataResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.inter.claroUserInfoWS.stub.DistribuidorStatusResponseType obtenerEstadoDistribuidor(com.inter.claroUserInfoWS.stub.DistribuidorStatusRequestType distribuidorStatusRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.openuri.org/obtenerEstadoDistribuidor");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.openuri.org/", "obtenerEstadoDistribuidor"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {distribuidorStatusRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.claroUserInfoWS.stub.DistribuidorStatusResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.claroUserInfoWS.stub.DistribuidorStatusResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.claroUserInfoWS.stub.DistribuidorStatusResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.inter.claroUserInfoWS.stub.DistribuidorDataResponseType obtenerDistribuidor(com.inter.claroUserInfoWS.stub.DistribuidorDataRequestType distribuidorDataRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.openuri.org/obtenerDistribuidor");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.openuri.org/", "obtenerDistribuidor"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {distribuidorDataRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.claroUserInfoWS.stub.DistribuidorDataResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.claroUserInfoWS.stub.DistribuidorDataResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.claroUserInfoWS.stub.DistribuidorDataResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
