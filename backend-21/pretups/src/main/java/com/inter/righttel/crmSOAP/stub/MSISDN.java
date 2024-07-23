/**
 * MSISDN.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.righttel.crmSOAP.stub;

public class MSISDN  implements java.io.Serializable, org.apache.axis.encoding.SimpleType {
    private java.lang.String _value;

    private java.lang.String numberingPlan;  // attribute

    private java.lang.String natureOfAddress;  // attribute

    public MSISDN() {
    }

    // Simple Types must have a String constructor
    public MSISDN(java.lang.String _value) {
        this._value = _value;
    }
    // Simple Types must have a toString for serializing the value
    public java.lang.String toString() {
        return _value;
    }


    /**
     * Gets the _value value for this MSISDN.
     * 
     * @return _value
     */
    public java.lang.String get_value() {
        return _value;
    }


    /**
     * Sets the _value value for this MSISDN.
     * 
     * @param _value
     */
    public void set_value(java.lang.String _value) {
        this._value = _value;
    }


    /**
     * Gets the numberingPlan value for this MSISDN.
     * 
     * @return numberingPlan
     */
    public java.lang.String getNumberingPlan() {
        return numberingPlan;
    }


    /**
     * Sets the numberingPlan value for this MSISDN.
     * 
     * @param numberingPlan
     */
    public void setNumberingPlan(java.lang.String numberingPlan) {
        this.numberingPlan = numberingPlan;
    }


    /**
     * Gets the natureOfAddress value for this MSISDN.
     * 
     * @return natureOfAddress
     */
    public java.lang.String getNatureOfAddress() {
        return natureOfAddress;
    }


    /**
     * Sets the natureOfAddress value for this MSISDN.
     * 
     * @param natureOfAddress
     */
    public void setNatureOfAddress(java.lang.String natureOfAddress) {
        this.natureOfAddress = natureOfAddress;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MSISDN)) return false;
        MSISDN other = (MSISDN) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this._value==null && other.get_value()==null) || 
             (this._value!=null &&
              this._value.equals(other.get_value()))) &&
            ((this.numberingPlan==null && other.getNumberingPlan()==null) || 
             (this.numberingPlan!=null &&
              this.numberingPlan.equals(other.getNumberingPlan()))) &&
            ((this.natureOfAddress==null && other.getNatureOfAddress()==null) || 
             (this.natureOfAddress!=null &&
              this.natureOfAddress.equals(other.getNatureOfAddress())));
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
        if (get_value() != null) {
            _hashCode += get_value().hashCode();
        }
        if (getNumberingPlan() != null) {
            _hashCode += getNumberingPlan().hashCode();
        }
        if (getNatureOfAddress() != null) {
            _hashCode += getNatureOfAddress().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MSISDN.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "MSISDN"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("numberingPlan");
        attrField.setXmlName(new javax.xml.namespace.QName("", "numberingPlan"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("natureOfAddress");
        attrField.setXmlName(new javax.xml.namespace.QName("", "natureOfAddress"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_value");
        elemField.setXmlName(new javax.xml.namespace.QName("", "_value"));
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
          new  org.apache.axis.encoding.ser.SimpleSerializer(
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
          new  org.apache.axis.encoding.ser.SimpleDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
