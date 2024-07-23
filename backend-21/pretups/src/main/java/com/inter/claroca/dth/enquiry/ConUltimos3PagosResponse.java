/**
 * ConUltimos3PagosResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.enquiry;

public class ConUltimos3PagosResponse  implements java.io.Serializable {
    private com.inter.claroca.dth.enquiry.ConUltimos3PagosResponseConUltimos3PagosResult conUltimos3PagosResult;

    public ConUltimos3PagosResponse() {
    }

    public ConUltimos3PagosResponse(
           com.inter.claroca.dth.enquiry.ConUltimos3PagosResponseConUltimos3PagosResult conUltimos3PagosResult) {
           this.conUltimos3PagosResult = conUltimos3PagosResult;
    }


    /**
     * Gets the conUltimos3PagosResult value for this ConUltimos3PagosResponse.
     * 
     * @return conUltimos3PagosResult
     */
    public com.inter.claroca.dth.enquiry.ConUltimos3PagosResponseConUltimos3PagosResult getConUltimos3PagosResult() {
        return conUltimos3PagosResult;
    }


    /**
     * Sets the conUltimos3PagosResult value for this ConUltimos3PagosResponse.
     * 
     * @param conUltimos3PagosResult
     */
    public void setConUltimos3PagosResult(com.inter.claroca.dth.enquiry.ConUltimos3PagosResponseConUltimos3PagosResult conUltimos3PagosResult) {
        this.conUltimos3PagosResult = conUltimos3PagosResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConUltimos3PagosResponse)) return false;
        ConUltimos3PagosResponse other = (ConUltimos3PagosResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.conUltimos3PagosResult==null && other.getConUltimos3PagosResult()==null) || 
             (this.conUltimos3PagosResult!=null &&
              this.conUltimos3PagosResult.equals(other.getConUltimos3PagosResult())));
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
        if (getConUltimos3PagosResult() != null) {
            _hashCode += getConUltimos3PagosResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConUltimos3PagosResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">conUltimos3PagosResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conUltimos3PagosResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "conUltimos3PagosResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>conUltimos3PagosResponse>conUltimos3PagosResult"));
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
