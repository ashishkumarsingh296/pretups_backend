/**
 * ConServiciosPaquetesResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.enquiry;

public class ConServiciosPaquetesResponse  implements java.io.Serializable {
    private com.inter.claroca.dth.enquiry.ConServiciosPaquetesResponseConServiciosPaquetesResult conServiciosPaquetesResult;

    public ConServiciosPaquetesResponse() {
    }

    public ConServiciosPaquetesResponse(
           com.inter.claroca.dth.enquiry.ConServiciosPaquetesResponseConServiciosPaquetesResult conServiciosPaquetesResult) {
           this.conServiciosPaquetesResult = conServiciosPaquetesResult;
    }


    /**
     * Gets the conServiciosPaquetesResult value for this ConServiciosPaquetesResponse.
     * 
     * @return conServiciosPaquetesResult
     */
    public com.inter.claroca.dth.enquiry.ConServiciosPaquetesResponseConServiciosPaquetesResult getConServiciosPaquetesResult() {
        return conServiciosPaquetesResult;
    }


    /**
     * Sets the conServiciosPaquetesResult value for this ConServiciosPaquetesResponse.
     * 
     * @param conServiciosPaquetesResult
     */
    public void setConServiciosPaquetesResult(com.inter.claroca.dth.enquiry.ConServiciosPaquetesResponseConServiciosPaquetesResult conServiciosPaquetesResult) {
        this.conServiciosPaquetesResult = conServiciosPaquetesResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConServiciosPaquetesResponse)) return false;
        ConServiciosPaquetesResponse other = (ConServiciosPaquetesResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.conServiciosPaquetesResult==null && other.getConServiciosPaquetesResult()==null) || 
             (this.conServiciosPaquetesResult!=null &&
              this.conServiciosPaquetesResult.equals(other.getConServiciosPaquetesResult())));
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
        if (getConServiciosPaquetesResult() != null) {
            _hashCode += getConServiciosPaquetesResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConServiciosPaquetesResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">conServiciosPaquetesResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conServiciosPaquetesResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "conServiciosPaquetesResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>conServiciosPaquetesResponse>conServiciosPaquetesResult"));
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
