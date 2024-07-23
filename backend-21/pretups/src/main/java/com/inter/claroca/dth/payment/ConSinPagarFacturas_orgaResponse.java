/**
 * ConSinPagarFacturas_orgaResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.payment;

public class ConSinPagarFacturas_orgaResponse  implements java.io.Serializable {
    private com.inter.claroca.dth.payment.ConSinPagarFacturas_orgaResponseConSinPagarFacturas_orgaResult conSinPagarFacturas_orgaResult;

    public ConSinPagarFacturas_orgaResponse() {
    }

    public ConSinPagarFacturas_orgaResponse(
           com.inter.claroca.dth.payment.ConSinPagarFacturas_orgaResponseConSinPagarFacturas_orgaResult conSinPagarFacturas_orgaResult) {
           this.conSinPagarFacturas_orgaResult = conSinPagarFacturas_orgaResult;
    }


    /**
     * Gets the conSinPagarFacturas_orgaResult value for this ConSinPagarFacturas_orgaResponse.
     * 
     * @return conSinPagarFacturas_orgaResult
     */
    public com.inter.claroca.dth.payment.ConSinPagarFacturas_orgaResponseConSinPagarFacturas_orgaResult getConSinPagarFacturas_orgaResult() {
        return conSinPagarFacturas_orgaResult;
    }


    /**
     * Sets the conSinPagarFacturas_orgaResult value for this ConSinPagarFacturas_orgaResponse.
     * 
     * @param conSinPagarFacturas_orgaResult
     */
    public void setConSinPagarFacturas_orgaResult(com.inter.claroca.dth.payment.ConSinPagarFacturas_orgaResponseConSinPagarFacturas_orgaResult conSinPagarFacturas_orgaResult) {
        this.conSinPagarFacturas_orgaResult = conSinPagarFacturas_orgaResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConSinPagarFacturas_orgaResponse)) return false;
        ConSinPagarFacturas_orgaResponse other = (ConSinPagarFacturas_orgaResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.conSinPagarFacturas_orgaResult==null && other.getConSinPagarFacturas_orgaResult()==null) || 
             (this.conSinPagarFacturas_orgaResult!=null &&
              this.conSinPagarFacturas_orgaResult.equals(other.getConSinPagarFacturas_orgaResult())));
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
        if (getConSinPagarFacturas_orgaResult() != null) {
            _hashCode += getConSinPagarFacturas_orgaResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConSinPagarFacturas_orgaResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">conSinPagarFacturas_orgaResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conSinPagarFacturas_orgaResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "conSinPagarFacturas_orgaResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>conSinPagarFacturas_orgaResponse>conSinPagarFacturas_orgaResult"));
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
