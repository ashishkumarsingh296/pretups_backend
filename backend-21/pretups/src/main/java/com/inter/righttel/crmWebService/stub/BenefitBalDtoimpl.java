/**
 * BenefitBalDtoimpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.righttel.crmWebService.stub;

public class BenefitBalDtoimpl  implements java.io.Serializable {
    private java.lang.String balance;

    private java.lang.String expDate;

    private java.lang.String effDate;

    private java.lang.String acctResCode;

    public BenefitBalDtoimpl() {
    }

    public BenefitBalDtoimpl(
           java.lang.String balance,
           java.lang.String expDate,
           java.lang.String effDate,
           java.lang.String acctResCode) {
           this.balance = balance;
           this.expDate = expDate;
           this.effDate = effDate;
           this.acctResCode = acctResCode;
    }


    /**
     * Gets the balance value for this BenefitBalDtoimpl.
     * 
     * @return balance
     */
    public java.lang.String getBalance() {
        return balance;
    }


    /**
     * Sets the balance value for this BenefitBalDtoimpl.
     * 
     * @param balance
     */
    public void setBalance(java.lang.String balance) {
        this.balance = balance;
    }


    /**
     * Gets the expDate value for this BenefitBalDtoimpl.
     * 
     * @return expDate
     */
    public java.lang.String getExpDate() {
        return expDate;
    }


    /**
     * Sets the expDate value for this BenefitBalDtoimpl.
     * 
     * @param expDate
     */
    public void setExpDate(java.lang.String expDate) {
        this.expDate = expDate;
    }


    /**
     * Gets the effDate value for this BenefitBalDtoimpl.
     * 
     * @return effDate
     */
    public java.lang.String getEffDate() {
        return effDate;
    }


    /**
     * Sets the effDate value for this BenefitBalDtoimpl.
     * 
     * @param effDate
     */
    public void setEffDate(java.lang.String effDate) {
        this.effDate = effDate;
    }


    /**
     * Gets the acctResCode value for this BenefitBalDtoimpl.
     * 
     * @return acctResCode
     */
    public java.lang.String getAcctResCode() {
        return acctResCode;
    }


    /**
     * Sets the acctResCode value for this BenefitBalDtoimpl.
     * 
     * @param acctResCode
     */
    public void setAcctResCode(java.lang.String acctResCode) {
        this.acctResCode = acctResCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BenefitBalDtoimpl)) return false;
        BenefitBalDtoimpl other = (BenefitBalDtoimpl) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.balance==null && other.getBalance()==null) || 
             (this.balance!=null &&
              this.balance.equals(other.getBalance()))) &&
            ((this.expDate==null && other.getExpDate()==null) || 
             (this.expDate!=null &&
              this.expDate.equals(other.getExpDate()))) &&
            ((this.effDate==null && other.getEffDate()==null) || 
             (this.effDate!=null &&
              this.effDate.equals(other.getEffDate()))) &&
            ((this.acctResCode==null && other.getAcctResCode()==null) || 
             (this.acctResCode!=null &&
              this.acctResCode.equals(other.getAcctResCode())));
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
        if (getBalance() != null) {
            _hashCode += getBalance().hashCode();
        }
        if (getExpDate() != null) {
            _hashCode += getExpDate().hashCode();
        }
        if (getEffDate() != null) {
            _hashCode += getEffDate().hashCode();
        }
        if (getAcctResCode() != null) {
            _hashCode += getAcctResCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BenefitBalDtoimpl.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://osb.rightel.ir", "BenefitBalDtoimpl"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("balance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://osb.rightel.ir", "Balance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://osb.rightel.ir", "ExpDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("effDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://osb.rightel.ir", "EffDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acctResCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://osb.rightel.ir", "AcctResCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
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
