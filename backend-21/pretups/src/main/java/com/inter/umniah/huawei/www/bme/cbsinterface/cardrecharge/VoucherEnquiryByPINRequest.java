/**
 * VoucherEnquiryByPINRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge;

public class VoucherEnquiryByPINRequest  implements java.io.Serializable {
    private java.lang.String cardPinNumber;

    public VoucherEnquiryByPINRequest() {
    }

    public VoucherEnquiryByPINRequest(
           java.lang.String cardPinNumber) {
           this.cardPinNumber = cardPinNumber;
    }


    /**
     * Gets the cardPinNumber value for this VoucherEnquiryByPINRequest.
     * 
     * @return cardPinNumber
     */
    public java.lang.String getCardPinNumber() {
        return cardPinNumber;
    }


    /**
     * Sets the cardPinNumber value for this VoucherEnquiryByPINRequest.
     * 
     * @param cardPinNumber
     */
    public void setCardPinNumber(java.lang.String cardPinNumber) {
        this.cardPinNumber = cardPinNumber;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoucherEnquiryByPINRequest)) return false;
        VoucherEnquiryByPINRequest other = (VoucherEnquiryByPINRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cardPinNumber==null && other.getCardPinNumber()==null) || 
             (this.cardPinNumber!=null &&
              this.cardPinNumber.equals(other.getCardPinNumber())));
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
        if (getCardPinNumber() != null) {
            _hashCode += getCardPinNumber().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoucherEnquiryByPINRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherEnquiryByPINRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardPinNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "CardPinNumber"));
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
