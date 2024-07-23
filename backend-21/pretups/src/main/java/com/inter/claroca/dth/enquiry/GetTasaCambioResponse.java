/**
 * GetTasaCambioResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.enquiry;

public class GetTasaCambioResponse  implements java.io.Serializable {
    private com.inter.claroca.dth.enquiry.GetTasaCambioResponseGetTasaCambioResult getTasaCambioResult;

    public GetTasaCambioResponse() {
    }

    public GetTasaCambioResponse(
           com.inter.claroca.dth.enquiry.GetTasaCambioResponseGetTasaCambioResult getTasaCambioResult) {
           this.getTasaCambioResult = getTasaCambioResult;
    }


    /**
     * Gets the getTasaCambioResult value for this GetTasaCambioResponse.
     * 
     * @return getTasaCambioResult
     */
    public com.inter.claroca.dth.enquiry.GetTasaCambioResponseGetTasaCambioResult getGetTasaCambioResult() {
        return getTasaCambioResult;
    }


    /**
     * Sets the getTasaCambioResult value for this GetTasaCambioResponse.
     * 
     * @param getTasaCambioResult
     */
    public void setGetTasaCambioResult(com.inter.claroca.dth.enquiry.GetTasaCambioResponseGetTasaCambioResult getTasaCambioResult) {
        this.getTasaCambioResult = getTasaCambioResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetTasaCambioResponse)) return false;
        GetTasaCambioResponse other = (GetTasaCambioResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getTasaCambioResult==null && other.getGetTasaCambioResult()==null) || 
             (this.getTasaCambioResult!=null &&
              this.getTasaCambioResult.equals(other.getGetTasaCambioResult())));
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
        if (getGetTasaCambioResult() != null) {
            _hashCode += getGetTasaCambioResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetTasaCambioResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">GetTasaCambioResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getTasaCambioResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "GetTasaCambioResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>GetTasaCambioResponse>GetTasaCambioResult"));
        elemField.setMinOccurs(0);
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
