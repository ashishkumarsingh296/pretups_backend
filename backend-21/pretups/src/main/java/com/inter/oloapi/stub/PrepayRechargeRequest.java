/**
 * PrepayRechargeRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.oloapi.stub;

public class PrepayRechargeRequest  implements java.io.Serializable {
    private com.inter.oloapi.stub.Security security;

    private java.lang.String companyCode;

    private java.lang.String customerId;

    private java.lang.String idplan;

    private java.lang.String inserted;

    private java.lang.String mobiquityTransactionId;

    public PrepayRechargeRequest() {
    }

    public PrepayRechargeRequest(
           com.inter.oloapi.stub.Security security,
           java.lang.String companyCode,
           java.lang.String customerId,
           java.lang.String idplan,
           java.lang.String inserted,
           java.lang.String mobiquityTransactionId) {
           this.security = security;
           this.companyCode = companyCode;
           this.customerId = customerId;
           this.idplan = idplan;
           this.inserted = inserted;
           this.mobiquityTransactionId = mobiquityTransactionId;
    }


    /**
     * Gets the security value for this PrepayRechargeRequest.
     * 
     * @return security
     */
    public com.inter.oloapi.stub.Security getSecurity() {
        return security;
    }


    /**
     * Sets the security value for this PrepayRechargeRequest.
     * 
     * @param security
     */
    public void setSecurity(com.inter.oloapi.stub.Security security) {
        this.security = security;
    }


    /**
     * Gets the companyCode value for this PrepayRechargeRequest.
     * 
     * @return companyCode
     */
    public java.lang.String getCompanyCode() {
        return companyCode;
    }


    /**
     * Sets the companyCode value for this PrepayRechargeRequest.
     * 
     * @param companyCode
     */
    public void setCompanyCode(java.lang.String companyCode) {
        this.companyCode = companyCode;
    }


    /**
     * Gets the customerId value for this PrepayRechargeRequest.
     * 
     * @return customerId
     */
    public java.lang.String getCustomerId() {
        return customerId;
    }


    /**
     * Sets the customerId value for this PrepayRechargeRequest.
     * 
     * @param customerId
     */
    public void setCustomerId(java.lang.String customerId) {
        this.customerId = customerId;
    }


    /**
     * Gets the idplan value for this PrepayRechargeRequest.
     * 
     * @return idplan
     */
    public java.lang.String getIdplan() {
        return idplan;
    }


    /**
     * Sets the idplan value for this PrepayRechargeRequest.
     * 
     * @param idplan
     */
    public void setIdplan(java.lang.String idplan) {
        this.idplan = idplan;
    }


    /**
     * Gets the inserted value for this PrepayRechargeRequest.
     * 
     * @return inserted
     */
    public java.lang.String getInserted() {
        return inserted;
    }


    /**
     * Sets the inserted value for this PrepayRechargeRequest.
     * 
     * @param inserted
     */
    public void setInserted(java.lang.String inserted) {
        this.inserted = inserted;
    }


    /**
     * Gets the mobiquityTransactionId value for this PrepayRechargeRequest.
     * 
     * @return mobiquityTransactionId
     */
    public java.lang.String getMobiquityTransactionId() {
        return mobiquityTransactionId;
    }


    /**
     * Sets the mobiquityTransactionId value for this PrepayRechargeRequest.
     * 
     * @param mobiquityTransactionId
     */
    public void setMobiquityTransactionId(java.lang.String mobiquityTransactionId) {
        this.mobiquityTransactionId = mobiquityTransactionId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PrepayRechargeRequest)) return false;
        PrepayRechargeRequest other = (PrepayRechargeRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.security==null && other.getSecurity()==null) || 
             (this.security!=null &&
              this.security.equals(other.getSecurity()))) &&
            ((this.companyCode==null && other.getCompanyCode()==null) || 
             (this.companyCode!=null &&
              this.companyCode.equals(other.getCompanyCode()))) &&
            ((this.customerId==null && other.getCustomerId()==null) || 
             (this.customerId!=null &&
              this.customerId.equals(other.getCustomerId()))) &&
            ((this.idplan==null && other.getIdplan()==null) || 
             (this.idplan!=null &&
              this.idplan.equals(other.getIdplan()))) &&
            ((this.inserted==null && other.getInserted()==null) || 
             (this.inserted!=null &&
              this.inserted.equals(other.getInserted()))) &&
            ((this.mobiquityTransactionId==null && other.getMobiquityTransactionId()==null) || 
             (this.mobiquityTransactionId!=null &&
              this.mobiquityTransactionId.equals(other.getMobiquityTransactionId())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getSecurity() != null) {
            _hashCode += getSecurity().hashCode();
        }
        if (getCompanyCode() != null) {
            _hashCode += getCompanyCode().hashCode();
        }
        if (getCustomerId() != null) {
            _hashCode += getCustomerId().hashCode();
        }
        if (getIdplan() != null) {
            _hashCode += getIdplan().hashCode();
        }
        if (getInserted() != null) {
            _hashCode += getInserted().hashCode();
        }
        if (getMobiquityTransactionId() != null) {
            _hashCode += getMobiquityTransactionId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PrepayRechargeRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:prepay", "PrepayRechargeRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("security");
        elemField.setXmlName(new javax.xml.namespace.QName("", "security"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:prepay", "Security"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("companyCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "companyCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customerId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idplan");
        elemField.setXmlName(new javax.xml.namespace.QName("", "idplan"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inserted");
        elemField.setXmlName(new javax.xml.namespace.QName("", "inserted"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mobiquityTransactionId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mobiquityTransactionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
