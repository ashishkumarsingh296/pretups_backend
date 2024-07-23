/**
 * CardRechargeMgrBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.inter.umniah.voucherrecharge.HuaweiVoucherRechargeINHandler;

public class CardRechargeMgrBindingStub extends org.apache.axis.client.Stub implements com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgr {
   
	private Log _log = LogFactory.getLog(CardRechargeMgrBindingStub.class.getName());
	private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[9];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("VoucherRecharge");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "VoucherRechargeRequestMsg"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherRechargeRequestMsg"), com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeRequestMsg.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherRechargeResultMsg"));
        oper.setReturnClass(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeResultMsg.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "VoucherRechargeResultMsg"));
       
        if (!BTSLUtil.isNullString(Constants.getProperty("SOAP_WEBSERVICE_STYLE")) && Constants.getProperty("SOAP_WEBSERVICE_STYLE").equalsIgnoreCase("WRAPPED")) {
            oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        } else {
            oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        }
        
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("VoucherRechargeEnquiry");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "VoucherRechargeEnquiryRequestMsg"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherRechargeEnquiryRequestMsg"), com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeEnquiryRequestMsg.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherRechargeEnquiryResultMsg"));
        oper.setReturnClass(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeEnquiryResultMsg.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "VoucherRechargeEnquiryResultMsg"));
        
        if (!BTSLUtil.isNullString(Constants.getProperty("SOAP_WEBSERVICE_STYLE")) && Constants.getProperty("SOAP_WEBSERVICE_STYLE").equalsIgnoreCase("WRAPPED")) {
                oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        } else {
            oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        }
      
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("VoucherEnquiryBySeq");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "VoucherEnquiryBySeqRequestMsg"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherEnquiryBySeqRequestMsg"), com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryBySeqRequestMsg.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherEnquiryBySeqResultMsg"));
        oper.setReturnClass(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryBySeqResultMsg.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "VoucherEnquiryBySeqResultMsg"));
        
        if (!BTSLUtil.isNullString(Constants.getProperty("SOAP_WEBSERVICE_STYLE")) && Constants.getProperty("SOAP_WEBSERVICE_STYLE").equalsIgnoreCase("WRAPPED")) {
                oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        } else {
            oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        }
      
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("VoucherEnquiryByPIN");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "VoucherEnquiryByPINRequestMsg"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherEnquiryByPINRequestMsg"), com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryByPINRequestMsg.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherEnquiryByPINResultMsg"));
        oper.setReturnClass(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryByPINResultMsg.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "VoucherEnquiryByPINResultMsg"));
        
        if (!BTSLUtil.isNullString(Constants.getProperty("SOAP_WEBSERVICE_STYLE")) && Constants.getProperty("SOAP_WEBSERVICE_STYLE").equalsIgnoreCase("WRAPPED")) {
                oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        } else {
            oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        }
      
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ModifyVoucherState");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "ModifyVoucherStateRequestMsg"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">ModifyVoucherStateRequestMsg"), com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.ModifyVoucherStateRequestMsg.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">ModifyVoucherStateResultMsg"));
        oper.setReturnClass(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.ModifyVoucherStateResultMsg.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "ModifyVoucherStateResultMsg"));
        
        if (!BTSLUtil.isNullString(Constants.getProperty("SOAP_WEBSERVICE_STYLE")) && Constants.getProperty("SOAP_WEBSERVICE_STYLE").equalsIgnoreCase("WRAPPED")) {
                oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        } else {
            oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        }
      
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("VoucherRechargeBySeq");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "VoucherRechargeBySeqRequestMsg"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherRechargeBySeqRequestMsg"), com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeBySeqRequestMsg.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherRechargeBySeqResultMsg"));
        oper.setReturnClass(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeBySeqResultMsg.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "VoucherRechargeBySeqResultMsg"));
        
        if (!BTSLUtil.isNullString(Constants.getProperty("SOAP_WEBSERVICE_STYLE")) && Constants.getProperty("SOAP_WEBSERVICE_STYLE").equalsIgnoreCase("WRAPPED")) {
                oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        } else {
            oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        }
      
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("DeleteRechageBlack");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "DeleteRechageBlackRequestMsg"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">DeleteRechageBlackRequestMsg"), com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.DeleteRechageBlackRequestMsg.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">DeleteRechageBlackResultMsg"));
        oper.setReturnClass(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.DeleteRechageBlackResultMsg.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "DeleteRechageBlackResultMsg"));
        
        if (!BTSLUtil.isNullString(Constants.getProperty("SOAP_WEBSERVICE_STYLE")) && Constants.getProperty("SOAP_WEBSERVICE_STYLE").equalsIgnoreCase("WRAPPED")) {
                 oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        } else {
            oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        }
      
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("BatchDeleteRechageBlack");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "BatchDeleteRechageBlackRequestMsg"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">BatchDeleteRechageBlackRequestMsg"), com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.BatchDeleteRechageBlackRequestMsg.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">BatchDeleteRechageBlackResultMsg"));
        oper.setReturnClass(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.BatchDeleteRechageBlackResultMsg.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "BatchDeleteRechageBlackResultMsg"));
        
        if (!BTSLUtil.isNullString(Constants.getProperty("SOAP_WEBSERVICE_STYLE")) && Constants.getProperty("SOAP_WEBSERVICE_STYLE").equalsIgnoreCase("WRAPPED")) {
                  oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        } else {
            oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        }
      
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("QueryRechargeBlack");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "QueryRechargeBlackRequestMsg"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">QueryRechargeBlackRequestMsg"), com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.QueryRechargeBlackRequestMsg.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">QueryRechargeBlackResultMsg"));
        oper.setReturnClass(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.QueryRechargeBlackResultMsg.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "QueryRechargeBlackResultMsg"));
        
        if (!BTSLUtil.isNullString(Constants.getProperty("SOAP_WEBSERVICE_STYLE")) && Constants.getProperty("SOAP_WEBSERVICE_STYLE").equalsIgnoreCase("WRAPPED")) {
                oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        } else {
            oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        }
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
      
        _operations[8] = oper;

    }

    public CardRechargeMgrBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public CardRechargeMgrBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public CardRechargeMgrBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
        addBindings0();
        addBindings1();
    }

    private void addBindings0() {
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
            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">BatchDeleteRechageBlackRequestMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.BatchDeleteRechageBlackRequestMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">BatchDeleteRechageBlackResultMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.BatchDeleteRechageBlackResultMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">DeleteRechageBlackRequestMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.DeleteRechageBlackRequestMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">DeleteRechageBlackResultMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.DeleteRechageBlackResultMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">ModifyVoucherStateRequestMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.ModifyVoucherStateRequestMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">ModifyVoucherStateResultMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.ModifyVoucherStateResultMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">QueryRechargeBlackRequestMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.QueryRechargeBlackRequestMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">QueryRechargeBlackResultMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.QueryRechargeBlackResultMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherEnquiryByPINRequestMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryByPINRequestMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherEnquiryByPINResultMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryByPINResultMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherEnquiryBySeqRequestMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryBySeqRequestMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherEnquiryBySeqResultMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryBySeqResultMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherRechargeBySeqRequestMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeBySeqRequestMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherRechargeBySeqResultMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeBySeqResultMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherRechargeEnquiryRequestMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeEnquiryRequestMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherRechargeEnquiryResultMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeEnquiryResultMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherRechargeRequestMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeRequestMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherRechargeResultMsg");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeResultMsg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">>>VoucherRechargeEnquiryResult>RechargeLog>RechargeBonus>AccountTypeDescription");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">>VoucherRechargeEnquiryResult>RechargeLog>Batch");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">>VoucherRechargeEnquiryResult>RechargeLog>OperatorID");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">>VoucherRechargeEnquiryResult>RechargeLog>RechargeBonus");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeEnquiryResultRechargeLogRechargeBonus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">>VoucherRechargeEnquiryResult>RechargeLog>RechargeType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">>VoucherRechargeEnquiryResult>RechargeLog>Sequence");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">>VoucherRechargeResult>RechargeBonus>AccountTypeDescription");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">ModifyVoucherStateRequest>Reason");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">ModifyVoucherStateRequest>Sequence");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherEnquiryByPINRequest>CardPinNumber");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherEnquiryByPINResult>BatchNo");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherEnquiryByPINResult>CardCosID");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherEnquiryByPINResult>Currency");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherEnquiryByPINResult>OprType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherEnquiryByPINResult>Sequence");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherEnquiryBySeqRequest>Sequence");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherEnquiryBySeqResult>BatchNo");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherEnquiryBySeqResult>CardCosID");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherEnquiryBySeqResult>Currency");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherEnquiryBySeqResult>OprType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherEnquiryBySeqResult>Sequence");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeBySeqRequest>CardSequence");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeBySeqResult>RechargeBonus");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeBySeqResultRechargeBonus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeEnquiryRequest>RechargeType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeEnquiryResult>RechargeLog");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeEnquiryResultRechargeLog.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeRequest>BankCode");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeRequest>CardPinNumber");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeRequest>CellID");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeRequest>DepositType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeRequest>Location");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeRequest>LogID");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeRequest>PaymentMode");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeRequest>RechargeType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeResult>OfferList");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.subscribe.OfferOrderInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "OfferOrderInfo");
            qName2 = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "Offer");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeResult>RechargeBonus");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeResultRechargeBonus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "BatchDeleteRechageBlackRequest");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.BatchDeleteRechageBlackRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "DeleteRechageBlackRequest");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.DeleteRechageBlackRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "ModifyVoucherStateRequest");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.ModifyVoucherStateRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "QueryRechargeBlackRequest");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.QueryRechargeBlackRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "QueryRechargeBlackResult");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.QueryRechargeBlackResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherEnquiryByPINRequest");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryByPINRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherEnquiryByPINResult");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryByPINResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherEnquiryBySeqRequest");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryBySeqRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherEnquiryBySeqResult");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryBySeqResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherRechargeBySeqRequest");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeBySeqRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherRechargeBySeqResult");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeBySeqResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherRechargeEnquiryRequest");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeEnquiryRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherRechargeEnquiryResult");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeEnquiryResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherRechargeRequest");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherRechargeResult");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>additionInfo");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>BelToAreaID");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>CommandId");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>currentCell");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>InterFrom");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>InterMedi");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>InterMode");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>OperatorID");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>PartnerID");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>PartnerOperID");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>Remark");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>RequestType");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeaderRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>SequenceId");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>SerialNo");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>ThirdPartyID");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>TradePartnerID");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>TransactionId");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>Version");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>visitArea");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">ResultHeader>CommandId");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">ResultHeader>Reserve1");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">ResultHeader>Reserve2");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">ResultHeader>Reserve3");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">ResultHeader>ResultCode");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">ResultHeader>ResultDesc");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">ResultHeader>SequenceId");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">ResultHeader>TransactionId");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">ResultHeader>Version");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">SessionEntityType>Name");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">SessionEntityType>RemoteAddress");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">SimpleProperty>Id");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">SimpleProperty>Value");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

    }
    private void addBindings1() {
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
            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "AccountCode");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "AccountType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "Balance");
            cachedSerQNames.add(qName);
            cls = long.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "Date");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "DateTime");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "FileName");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "OfferCode");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "OfferId");
            cachedSerQNames.add(qName);
            cls = int.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "OfferOrderCode");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "OfferOrderKey");
            cachedSerQNames.add(qName);
            cls = long.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "Password");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "RequestHeader");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "ResultHeader");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.common.ResultHeader.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "SessionEntityType");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.common.SessionEntityType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "SimpleProperty");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.common.SimpleProperty.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "SubscriberNo");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", ">OfferOrderInfo>AutoType");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.subscribe.OfferOrderInfoAutoType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", ">OfferOrderInfo>OfferName");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "OfferOrderInfo");
            cachedSerQNames.add(qName);
            cls = com.inter.umniah.huawei.www.bme.cbsinterface.subscribe.OfferOrderInfo.class;
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

    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeResultMsg voucherRecharge(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeRequestMsg voucherRechargeRequestMsg) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("VoucherRecharge");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "VoucherRecharge"));

        setRequestHeaders(_call);
        setAttachments(_call);
        
        try {        
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {voucherRechargeRequestMsg});
        try{
        	_log.info("voucherRecharge","Request XML="+_call.getMessageContext().getRequestMessage().getSOAPPart().getEnvelope().toString());
        }catch(Exception e){}

        if (_resp instanceof java.rmi.RemoteException) {
        	throw (java.rmi.RemoteException)_resp;
        }
        else {
        	try{
            	_log.info("voucherRecharge","Response XML="+_call.getMessageContext().getResponseMessage().getSOAPPart().getEnvelope().toString());
            }catch(Exception e){}

        	extractAttachments(_call);
        	try {
        		return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeResultMsg) _resp;
        	} catch (java.lang.Exception _exception) {
        		return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeResultMsg) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeResultMsg.class);
        	}
        }
        } catch (org.apache.axis.AxisFault axisFaultException) {
        	throw axisFaultException;
        }
    }

    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeEnquiryResultMsg voucherRechargeEnquiry(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeEnquiryRequestMsg voucherRechargeEnquiryRequestMsg) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("VoucherRechargeEnquiry");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "VoucherRechargeEnquiry"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {voucherRechargeEnquiryRequestMsg});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeEnquiryResultMsg) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeEnquiryResultMsg) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeEnquiryResultMsg.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryBySeqResultMsg voucherEnquiryBySeq(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryBySeqRequestMsg voucherEnquiryBySeqRequestMsg) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("VoucherEnquiryBySeq");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "VoucherEnquiryBySeq"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {voucherEnquiryBySeqRequestMsg});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryBySeqResultMsg) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryBySeqResultMsg) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryBySeqResultMsg.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryByPINResultMsg voucherEnquiryByPIN(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryByPINRequestMsg voucherEnquiryByPINRequestMsg) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("VoucherEnquiryByPIN");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "VoucherEnquiryByPIN"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {voucherEnquiryByPINRequestMsg});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryByPINResultMsg) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryByPINResultMsg) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryByPINResultMsg.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.ModifyVoucherStateResultMsg modifyVoucherState(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.ModifyVoucherStateRequestMsg modifyVoucherStateRequestMsg) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("ModifyVoucherState");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "ModifyVoucherState"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {modifyVoucherStateRequestMsg});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.ModifyVoucherStateResultMsg) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.ModifyVoucherStateResultMsg) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.ModifyVoucherStateResultMsg.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeBySeqResultMsg voucherRechargeBySeq(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeBySeqRequestMsg voucherRechargeBySeqRequestMsg) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("VoucherRechargeBySeq");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "VoucherRechargeBySeq"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {voucherRechargeBySeqRequestMsg});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeBySeqResultMsg) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeBySeqResultMsg) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeBySeqResultMsg.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.DeleteRechageBlackResultMsg deleteRechageBlack(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.DeleteRechageBlackRequestMsg deleteRechageBlackRequestMsg) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("DeleteRechageBlack");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "DeleteRechageBlack"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {deleteRechageBlackRequestMsg});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.DeleteRechageBlackResultMsg) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.DeleteRechageBlackResultMsg) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.DeleteRechageBlackResultMsg.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.BatchDeleteRechageBlackResultMsg batchDeleteRechageBlack(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.BatchDeleteRechageBlackRequestMsg batchDeleteRechageBlackRequestMsg) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("BatchDeleteRechageBlack");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "BatchDeleteRechageBlack"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {batchDeleteRechageBlackRequestMsg});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.BatchDeleteRechageBlackResultMsg) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.BatchDeleteRechageBlackResultMsg) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.BatchDeleteRechageBlackResultMsg.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.QueryRechargeBlackResultMsg queryRechargeBlack(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.QueryRechargeBlackRequestMsg queryRechargeBlackRequestMsg) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("QueryRechargeBlack");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "QueryRechargeBlack"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {queryRechargeBlackRequestMsg});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.QueryRechargeBlackResultMsg) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.QueryRechargeBlackResultMsg) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.QueryRechargeBlackResultMsg.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
