/**
 * ConClienteResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.enquiry;

public class ConClienteResponse  implements java.io.Serializable {
    private com.inter.claroca.dth.enquiry.ConClienteResponseConClienteResult conClienteResult;

    public ConClienteResponse() {
    }

    public ConClienteResponse(
           com.inter.claroca.dth.enquiry.ConClienteResponseConClienteResult conClienteResult) {
           this.conClienteResult = conClienteResult;
    }


    /**
     * Gets the conClienteResult value for this ConClienteResponse.
     * 
     * @return conClienteResult
     */
    public com.inter.claroca.dth.enquiry.ConClienteResponseConClienteResult getConClienteResult() {
        return conClienteResult;
    }


    /**
     * Sets the conClienteResult value for this ConClienteResponse.
     * 
     * @param conClienteResult
     */
    public void setConClienteResult(com.inter.claroca.dth.enquiry.ConClienteResponseConClienteResult conClienteResult) {
        this.conClienteResult = conClienteResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConClienteResponse)) return false;
        ConClienteResponse other = (ConClienteResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.conClienteResult==null && other.getConClienteResult()==null) || 
             (this.conClienteResult!=null &&
              this.conClienteResult.equals(other.getConClienteResult())));
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
        if (getConClienteResult() != null) {
            _hashCode += getConClienteResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConClienteResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">conClienteResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conClienteResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "conClienteResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>conClienteResponse>conClienteResult"));
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
