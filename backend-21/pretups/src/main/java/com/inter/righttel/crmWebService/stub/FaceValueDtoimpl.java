/**
 * FaceValueDtoimpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.righttel.crmWebService.stub;

public class FaceValueDtoimpl  implements java.io.Serializable {
    private java.lang.String e_AMOUNT;

    private java.lang.String e_COUNT;

    public FaceValueDtoimpl() {
    }

    public FaceValueDtoimpl(
           java.lang.String e_AMOUNT,
           java.lang.String e_COUNT) {
           this.e_AMOUNT = e_AMOUNT;
           this.e_COUNT = e_COUNT;
    }


    /**
     * Gets the e_AMOUNT value for this FaceValueDtoimpl.
     * 
     * @return e_AMOUNT
     */
    public java.lang.String getE_AMOUNT() {
        return e_AMOUNT;
    }


    /**
     * Sets the e_AMOUNT value for this FaceValueDtoimpl.
     * 
     * @param e_AMOUNT
     */
    public void setE_AMOUNT(java.lang.String e_AMOUNT) {
        this.e_AMOUNT = e_AMOUNT;
    }


    /**
     * Gets the e_COUNT value for this FaceValueDtoimpl.
     * 
     * @return e_COUNT
     */
    public java.lang.String getE_COUNT() {
        return e_COUNT;
    }


    /**
     * Sets the e_COUNT value for this FaceValueDtoimpl.
     * 
     * @param e_COUNT
     */
    public void setE_COUNT(java.lang.String e_COUNT) {
        this.e_COUNT = e_COUNT;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FaceValueDtoimpl)) return false;
        FaceValueDtoimpl other = (FaceValueDtoimpl) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.e_AMOUNT==null && other.getE_AMOUNT()==null) || 
             (this.e_AMOUNT!=null &&
              this.e_AMOUNT.equals(other.getE_AMOUNT()))) &&
            ((this.e_COUNT==null && other.getE_COUNT()==null) || 
             (this.e_COUNT!=null &&
              this.e_COUNT.equals(other.getE_COUNT())));
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
        if (getE_AMOUNT() != null) {
            _hashCode += getE_AMOUNT().hashCode();
        }
        if (getE_COUNT() != null) {
            _hashCode += getE_COUNT().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FaceValueDtoimpl.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://osb.rightel.ir", "FaceValueDtoimpl"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("e_AMOUNT");
        elemField.setXmlName(new javax.xml.namespace.QName("http://osb.rightel.ir", "E_AMOUNT"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("e_COUNT");
        elemField.setXmlName(new javax.xml.namespace.QName("http://osb.rightel.ir", "E_COUNT"));
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
