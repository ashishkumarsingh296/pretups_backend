/**
 * ServicePortalBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.righttel.crmWebService.stub;


import com.btsl.pretups.logging.InterfaceTransactionLog;

public class ServicePortalBindingStub extends org.apache.axis.client.Stub implements com.inter.righttel.crmWebService.stub.ServicePortalPortType {
	private java.util.Vector cachedSerClasses = new java.util.Vector();
	private java.util.Vector cachedSerQNames = new java.util.Vector();
	private java.util.Vector cachedSerFactories = new java.util.Vector();
	private java.util.Vector cachedDeserFactories = new java.util.Vector();
	static org.apache.axis.description.OperationDesc [] _operations;

	static {
		_operations = new org.apache.axis.description.OperationDesc[14];
		_initOperationDesc1();
		_initOperationDesc2();
	}

	private static void _initOperationDesc1(){
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("SetSupService");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "ServiceCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "Action"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[0] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CheckRightelSubscriber");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[1] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("UpdateLanguage");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "DefLang"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[2] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("MCAQuery");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[3] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("AuthenticationWithMSISDN");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "UserPwd"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "PwdType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[4] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("ResetUserPassword");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "NewPassword"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "PwdType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[5] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("NewQueryProfile2");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN"), org.apache.axis.description.ParameterDesc.INOUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "BrandName"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "SIMStatus"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "DefLang"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "SIMSubStatus"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "CustGrade"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "FixContact"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "DocNum"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "DocType"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "CustomerName"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "ISPREPAID"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "BrandCode"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "CustType"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "VCBlackList"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[6] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("QuerySubsAvailableOfferList");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "ChannelID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "OfferPrice"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "SpeedValue"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "Duration"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "Volume"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "SmsNum"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "PageIndex"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "RowPerPage"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "OfferDtoList"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://osb.rightel.ir", "OfferDtoListimpl"), com.inter.righttel.crmWebService.stub.OfferDtoListimpl[].class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "BrandName"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[7] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("QueryPricePlanOfferChargeFee");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "ChannelID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "OfferCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "PayFlag"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "OfferFee"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "Tax"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "MaximumDiscount"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[8] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("QueryAllBalance");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://osb.rightel.ir", "AllBalanceDto"));
		oper.setReturnClass(com.inter.righttel.crmWebService.stub.AllBalanceDtoListimpl[].class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://osb.rightel.ir", "AllBalanceDtoList"));
		param = oper.getReturnParamDesc();
		param.setItemQName(new javax.xml.namespace.QName("http://osb.rightel.ir", "AllBalanceDto"));
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[9] = oper;

	}

	private static void _initOperationDesc2(){
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("QueryRechargeResult");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "RequestID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "Result"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "ExceptionCode"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[10] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("OrderPricePlanOffer");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "OfferCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "ChannelID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "PayFlag"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "AU"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "Amount"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "DiscountFee"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "BankID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "CallerID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://osb.rightel.ir", "OrderNbr"));
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[11] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("RechargePPSNew");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "REQUEST_ID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "Amount"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "BankId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "AU"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "PaymentType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "FaceValueDtoList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://osb.rightel.ir", "FaceValueDtoListimpl"), com.inter.righttel.crmWebService.stub.FaceValueDtoimpl[].class, false, false);
		param.setItemQName(new javax.xml.namespace.QName("http://osb.rightel.ir", "FaceValueDto"));
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "CallerID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "PaymentMethod"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "Balance"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "ExpDate"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "AddBalance"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "BenefitBalDtoList"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://osb.rightel.ir", "BenefitBalDtoListimpl"), com.inter.righttel.crmWebService.stub.BenefitBalDtoimpl[][].class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[12] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CheckCreditLimit");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "Balance"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "CreditLimit"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "DefaultCL"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "NonDefaultCL"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "CreditUsed"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://osb.rightel.ir", "CreditAvailable"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[13] = oper;

	}

	public ServicePortalBindingStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public ServicePortalBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public ServicePortalBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
		qName = new javax.xml.namespace.QName("http://osb.rightel.ir", "AllBalanceDto");
		cachedSerQNames.add(qName);
		cls = com.inter.righttel.crmWebService.stub.AllBalanceDtoListimpl[].class;
		cachedSerClasses.add(cls);
		qName = new javax.xml.namespace.QName("http://osb.rightel.ir", "AllBalanceDtoListimpl");
		qName2 = new javax.xml.namespace.QName("http://osb.rightel.ir", "AllBalanceDto");
		cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
		cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

		qName = new javax.xml.namespace.QName("http://osb.rightel.ir", "AllBalanceDtoListimpl");
		cachedSerQNames.add(qName);
		cls = com.inter.righttel.crmWebService.stub.AllBalanceDtoListimpl.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://osb.rightel.ir", "BenefitBalDtoimpl");
		cachedSerQNames.add(qName);
		cls = com.inter.righttel.crmWebService.stub.BenefitBalDtoimpl.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://osb.rightel.ir", "BenefitBalDtoListimpl");
		cachedSerQNames.add(qName);
		cls = com.inter.righttel.crmWebService.stub.BenefitBalDtoimpl[].class;
		cachedSerClasses.add(cls);
		qName = new javax.xml.namespace.QName("http://osb.rightel.ir", "BenefitBalDtoimpl");
		qName2 = new javax.xml.namespace.QName("http://osb.rightel.ir", "BenefitBalDto");
		cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
		cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

		qName = new javax.xml.namespace.QName("http://osb.rightel.ir", "FaceValueDtoimpl");
		cachedSerQNames.add(qName);
		cls = com.inter.righttel.crmWebService.stub.FaceValueDtoimpl.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://osb.rightel.ir", "FaceValueDtoListimpl");
		cachedSerQNames.add(qName);
		cls = com.inter.righttel.crmWebService.stub.FaceValueDtoimpl[].class;
		cachedSerClasses.add(cls);
		qName = new javax.xml.namespace.QName("http://osb.rightel.ir", "FaceValueDtoimpl");
		qName2 = new javax.xml.namespace.QName("http://osb.rightel.ir", "FaceValueDto");
		cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
		cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

		qName = new javax.xml.namespace.QName("http://osb.rightel.ir", "OfferDtoListimpl");
		cachedSerQNames.add(qName);
		cls = com.inter.righttel.crmWebService.stub.OfferDtoListimpl.class;
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

	public void setSupService(java.lang.String MSISDN, java.lang.String serviceCode, java.lang.String action) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("urn:SetSupService");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://osb.rightel.ir", "SetSupService"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {MSISDN, serviceCode, action});

		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException)_resp;
		}
		extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void checkRightelSubscriber(java.lang.String MSISDN) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[1]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("urn:CheckRightelSubscriber");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://osb.rightel.ir", "CheckRightelSubscriber"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {MSISDN});

		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException)_resp;
		}
		extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void updateLanguage(java.lang.String MSISDN, java.lang.String defLang) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[2]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("urn:UpdateLanguage");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://osb.rightel.ir", "UpdateLanguage"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {MSISDN, defLang});

		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException)_resp;
		}
		extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void MCAQuery(java.lang.String MSISDN) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[3]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("urn:MCAQuery");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://osb.rightel.ir", "MCAQuery"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {MSISDN});

		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException)_resp;
		}
		extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void authenticationWithMSISDN(java.lang.String MSISDN, java.lang.String userPwd, java.lang.String pwdType) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[4]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("urn:AuthenticationWithMSISDN");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://osb.rightel.ir", "AuthenticationWithMSISDN"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {MSISDN, userPwd, pwdType});

		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException)_resp;
		}
		extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void resetUserPassword(java.lang.String MSISDN, java.lang.String newPassword, java.lang.String pwdType) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[5]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("urn:ResetUserPassword");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://osb.rightel.ir", "ResetUserPassword"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {MSISDN, newPassword, pwdType});

		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException)_resp;
		}
		extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void newQueryProfile2(javax.xml.rpc.holders.StringHolder MSISDN, javax.xml.rpc.holders.StringHolder brandName, javax.xml.rpc.holders.StringHolder SIMStatus, javax.xml.rpc.holders.StringHolder defLang, javax.xml.rpc.holders.StringHolder SIMSubStatus, javax.xml.rpc.holders.StringHolder custGrade, javax.xml.rpc.holders.StringHolder fixContact, javax.xml.rpc.holders.StringHolder docNum, javax.xml.rpc.holders.StringHolder docType, javax.xml.rpc.holders.StringHolder customerName, javax.xml.rpc.holders.StringHolder ISPREPAID, javax.xml.rpc.holders.StringHolder brandCode, javax.xml.rpc.holders.StringHolder custType, javax.xml.rpc.holders.StringHolder VCBlackList) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[6]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("urn:NewQueryProfile2");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://osb.rightel.ir", "NewQueryProfile2"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {MSISDN.value});

		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException)_resp;
		}
		else {
			extractAttachments(_call);
			java.util.Map _output;
			_output = _call.getOutputParams();
			try {
				MSISDN.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN"));
			} catch (java.lang.Exception _exception) {
				MSISDN.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "MSISDN")), java.lang.String.class);
			}
			try {
				brandName.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "BrandName"));
			} catch (java.lang.Exception _exception) {
				brandName.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "BrandName")), java.lang.String.class);
			}
			try {
				SIMStatus.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "SIMStatus"));
			} catch (java.lang.Exception _exception) {
				SIMStatus.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "SIMStatus")), java.lang.String.class);
			}
			try {
				defLang.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "DefLang"));
			} catch (java.lang.Exception _exception) {
				defLang.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "DefLang")), java.lang.String.class);
			}
			try {
				SIMSubStatus.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "SIMSubStatus"));
			} catch (java.lang.Exception _exception) {
				SIMSubStatus.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "SIMSubStatus")), java.lang.String.class);
			}
			try {
				custGrade.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "CustGrade"));
			} catch (java.lang.Exception _exception) {
				custGrade.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "CustGrade")), java.lang.String.class);
			}
			try {
				fixContact.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "FixContact"));
			} catch (java.lang.Exception _exception) {
				fixContact.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "FixContact")), java.lang.String.class);
			}
			try {
				docNum.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "DocNum"));
			} catch (java.lang.Exception _exception) {
				docNum.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "DocNum")), java.lang.String.class);
			}
			try {
				docType.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "DocType"));
			} catch (java.lang.Exception _exception) {
				docType.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "DocType")), java.lang.String.class);
			}
			try {
				customerName.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "CustomerName"));
			} catch (java.lang.Exception _exception) {
				customerName.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "CustomerName")), java.lang.String.class);
			}
			try {
				ISPREPAID.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "ISPREPAID"));
			} catch (java.lang.Exception _exception) {
				ISPREPAID.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "ISPREPAID")), java.lang.String.class);
			}
			try {
				brandCode.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "BrandCode"));
			} catch (java.lang.Exception _exception) {
				brandCode.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "BrandCode")), java.lang.String.class);
			}
			try {
				custType.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "CustType"));
			} catch (java.lang.Exception _exception) {
				custType.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "CustType")), java.lang.String.class);
			}
			try {
				VCBlackList.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "VCBlackList"));
			} catch (java.lang.Exception _exception) {
				VCBlackList.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "VCBlackList")), java.lang.String.class);
			}
		}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void querySubsAvailableOfferList(java.lang.String MSISDN, java.lang.String channelID, java.lang.String offerPrice, java.lang.String speedValue, java.lang.String duration, java.lang.String volume, java.lang.String smsNum, java.lang.String pageIndex, java.lang.String rowPerPage, com.inter.righttel.crmWebService.stub.holders.OfferDtoListimplArrayHolder offerDtoList, com.inter.righttel.crmWebService.stub.holders.StringArrayHolder brandName) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[7]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("urn:QuerySubsAvailableOfferList");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://osb.rightel.ir", "QuerySubsAvailableOfferList"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {MSISDN, channelID, offerPrice, speedValue, duration, volume, smsNum, pageIndex, rowPerPage});

		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException)_resp;
		}
		else {
			extractAttachments(_call);
			java.util.Map _output;
			_output = _call.getOutputParams();
			try {
				offerDtoList.value = (com.inter.righttel.crmWebService.stub.OfferDtoListimpl[]) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "OfferDtoList"));
			} catch (java.lang.Exception _exception) {
				offerDtoList.value = (com.inter.righttel.crmWebService.stub.OfferDtoListimpl[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "OfferDtoList")), com.inter.righttel.crmWebService.stub.OfferDtoListimpl[].class);
			}
			try {
				brandName.value = (java.lang.String[]) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "BrandName"));
			} catch (java.lang.Exception _exception) {
				brandName.value = (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "BrandName")), java.lang.String[].class);
			}
		}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void queryPricePlanOfferChargeFee(java.lang.String MSISDN, java.lang.String channelID, java.lang.String offerCode, java.lang.String payFlag, javax.xml.rpc.holders.StringHolder offerFee, javax.xml.rpc.holders.StringHolder tax, javax.xml.rpc.holders.StringHolder maximumDiscount) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[8]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("urn:QueryPricePlanOfferChargeFee");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://osb.rightel.ir", "QueryPricePlanOfferChargeFee"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {MSISDN, channelID, offerCode, payFlag});

		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException)_resp;
		}
		else {
			extractAttachments(_call);
			java.util.Map _output;
			_output = _call.getOutputParams();
			try {
				offerFee.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "OfferFee"));
			} catch (java.lang.Exception _exception) {
				offerFee.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "OfferFee")), java.lang.String.class);
			}
			try {
				tax.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "Tax"));
			} catch (java.lang.Exception _exception) {
				tax.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "Tax")), java.lang.String.class);
			}
			try {
				maximumDiscount.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "MaximumDiscount"));
			} catch (java.lang.Exception _exception) {
				maximumDiscount.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "MaximumDiscount")), java.lang.String.class);
			}
		}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public com.inter.righttel.crmWebService.stub.AllBalanceDtoListimpl[] queryAllBalance(java.lang.String MSISDN) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[9]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("urn:QueryAllBalance");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://osb.rightel.ir", "QueryAllBalance"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {MSISDN});

		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException)_resp;
		}
		else {
			extractAttachments(_call);
			try {
				return (com.inter.righttel.crmWebService.stub.AllBalanceDtoListimpl[]) _resp;
			} catch (java.lang.Exception _exception) {
				return (com.inter.righttel.crmWebService.stub.AllBalanceDtoListimpl[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.inter.righttel.crmWebService.stub.AllBalanceDtoListimpl[].class);
			}
		}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void queryRechargeResult(java.lang.String MSISDN, java.lang.String requestID, javax.xml.rpc.holders.StringHolder result, javax.xml.rpc.holders.StringHolder exceptionCode) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[10]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("urn:QueryRechargeResult");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://osb.rightel.ir", "QueryRechargeResult"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {MSISDN, requestID});
		try{
			InterfaceTransactionLog.debug("queryRechargeResult Request XML="+_call.getMessageContext().getRequestMessage().getSOAPPart().getEnvelope().toString());
		}catch(Exception e){}

		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException)_resp;
		}
		else {

			try{
				InterfaceTransactionLog.debug("queryRechargeResult Response XML="+_call.getMessageContext().getResponseMessage().getSOAPPart().getEnvelope().toString());
			}catch(Exception e){}

			extractAttachments(_call);
			java.util.Map _output;
			_output = _call.getOutputParams();
			try {
				result.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "Result"));
			} catch (java.lang.Exception _exception) {
				result.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "Result")), java.lang.String.class);
			}
			try {
				exceptionCode.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "ExceptionCode"));
			} catch (java.lang.Exception _exception) {
				exceptionCode.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "ExceptionCode")), java.lang.String.class);
			}
		}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.String orderPricePlanOffer(java.lang.String MSISDN, java.lang.String offerCode, java.lang.String channelID, java.lang.String payFlag, java.lang.String AU, java.lang.String amount, java.lang.String discountFee, java.lang.String bankID, java.lang.String callerID) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[11]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("urn:OrderPricePlanOffer");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://osb.rightel.ir", "OrderPricePlanOffer"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {MSISDN, offerCode, channelID, payFlag, AU, amount, discountFee, bankID, callerID});
		try{
			InterfaceTransactionLog.debug("orderPricePlanOffer Request XML="+_call.getMessageContext().getRequestMessage().getSOAPPart().getEnvelope().toString());
		}catch(Exception e){}
		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException)_resp;
		}
		else {

			try{
				InterfaceTransactionLog.debug("orderPricePlanOffer Response XML="+_call.getMessageContext().getResponseMessage().getSOAPPart().getEnvelope().toString());
			}catch(Exception e){}


			extractAttachments(_call);
			try {
				return (java.lang.String) _resp;
			} catch (java.lang.Exception _exception) {
				return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
			}
		}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void rechargePPSNew(java.lang.String REQUEST_ID, java.lang.String MSISDN, java.lang.String amount, java.lang.String bankId, java.lang.String AU, java.lang.String paymentType, com.inter.righttel.crmWebService.stub.FaceValueDtoimpl[] faceValueDtoList, java.lang.String callerID, java.lang.String paymentMethod, javax.xml.rpc.holders.StringHolder balance, javax.xml.rpc.holders.StringHolder expDate, javax.xml.rpc.holders.StringHolder addBalance, com.inter.righttel.crmWebService.stub.holders.BenefitBalDtoListimplArrayHolder benefitBalDtoList) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[12]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("urn:RechargePPSNew");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://osb.rightel.ir", "RechargePPSNew"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {REQUEST_ID, MSISDN, amount, bankId, AU, paymentType, faceValueDtoList, callerID, paymentMethod});

		try{
			InterfaceTransactionLog.debug("rechargePPSNew  Request XML="+_call.getMessageContext().getRequestMessage().getSOAPPart().getEnvelope().toString());
		}catch(Exception e){}

		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException)_resp;
		}
		else {

			try{
				InterfaceTransactionLog.debug("rechargePPSNew Response XML="+_call.getMessageContext().getResponseMessage().getSOAPPart().getEnvelope().toString());
			}catch(Exception e){}



			extractAttachments(_call);
			java.util.Map _output;
			_output = _call.getOutputParams();
			try {
				balance.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "Balance"));
			} catch (java.lang.Exception _exception) {
				balance.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "Balance")), java.lang.String.class);
			}
			try {
				expDate.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "ExpDate"));
			} catch (java.lang.Exception _exception) {
				expDate.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "ExpDate")), java.lang.String.class);
			}
			try {
				addBalance.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "AddBalance"));
			} catch (java.lang.Exception _exception) {
				addBalance.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "AddBalance")), java.lang.String.class);
			}
			try {
				benefitBalDtoList.value = (com.inter.righttel.crmWebService.stub.BenefitBalDtoimpl[][]) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "BenefitBalDtoList"));
			} catch (java.lang.Exception _exception) {
				benefitBalDtoList.value = (com.inter.righttel.crmWebService.stub.BenefitBalDtoimpl[][]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "BenefitBalDtoList")), com.inter.righttel.crmWebService.stub.BenefitBalDtoimpl[][].class);
			}
		}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void checkCreditLimit(java.lang.String MSISDN, javax.xml.rpc.holders.StringHolder balance, javax.xml.rpc.holders.StringHolder creditLimit, javax.xml.rpc.holders.StringHolder defaultCL, javax.xml.rpc.holders.StringHolder nonDefaultCL, javax.xml.rpc.holders.StringHolder creditUsed, javax.xml.rpc.holders.StringHolder creditAvailable) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[13]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("urn:CheckCreditLimit");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://osb.rightel.ir", "CheckCreditLimit"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {MSISDN});
		try{
			InterfaceTransactionLog.debug("checkCreditLimit Request XML="+_call.getMessageContext().getRequestMessage().getSOAPPart().getEnvelope().toString());
		}catch(Exception e){}

		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException)_resp;
		}
		else {

			try{
				InterfaceTransactionLog.debug("checkCreditLimit Response XML="+_call.getMessageContext().getResponseMessage().getSOAPPart().getEnvelope().toString());
			}catch(Exception e){}

			extractAttachments(_call);
			java.util.Map _output;
			_output = _call.getOutputParams();
			try {
				balance.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "Balance"));
			} catch (java.lang.Exception _exception) {
				balance.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "Balance")), java.lang.String.class);
			}
			try {
				creditLimit.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "CreditLimit"));
			} catch (java.lang.Exception _exception) {
				creditLimit.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "CreditLimit")), java.lang.String.class);
			}
			try {
				defaultCL.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "DefaultCL"));
			} catch (java.lang.Exception _exception) {
				defaultCL.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "DefaultCL")), java.lang.String.class);
			}
			try {
				nonDefaultCL.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "NonDefaultCL"));
			} catch (java.lang.Exception _exception) {
				nonDefaultCL.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "NonDefaultCL")), java.lang.String.class);
			}
			try {
				creditUsed.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "CreditUsed"));
			} catch (java.lang.Exception _exception) {
				creditUsed.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "CreditUsed")), java.lang.String.class);
			}
			try {
				creditAvailable.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "CreditAvailable"));
			} catch (java.lang.Exception _exception) {
				creditAvailable.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://osb.rightel.ir", "CreditAvailable")), java.lang.String.class);
			}
		}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

}
