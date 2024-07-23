/**
 * ConSinPagarFacturasResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.payment;

public class ConSinPagarFacturasResponse  implements java.io.Serializable {
    private com.inter.claroca.dth.payment.ConSinPagarFacturasResponseConSinPagarFacturasResult conSinPagarFacturasResult;

    public ConSinPagarFacturasResponse() {
    }

    public ConSinPagarFacturasResponse(
           com.inter.claroca.dth.payment.ConSinPagarFacturasResponseConSinPagarFacturasResult conSinPagarFacturasResult) {
           this.conSinPagarFacturasResult = conSinPagarFacturasResult;
    }


    /**
     * Gets the conSinPagarFacturasResult value for this ConSinPagarFacturasResponse.
     * 
     * @return conSinPagarFacturasResult
     */
    public com.inter.claroca.dth.payment.ConSinPagarFacturasResponseConSinPagarFacturasResult getConSinPagarFacturasResult() {
        return conSinPagarFacturasResult;
    }


    /**
     * Sets the conSinPagarFacturasResult value for this ConSinPagarFacturasResponse.
     * 
     * @param conSinPagarFacturasResult
     */
    public void setConSinPagarFacturasResult(com.inter.claroca.dth.payment.ConSinPagarFacturasResponseConSinPagarFacturasResult conSinPagarFacturasResult) {
        this.conSinPagarFacturasResult = conSinPagarFacturasResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConSinPagarFacturasResponse)) return false;
        ConSinPagarFacturasResponse other = (ConSinPagarFacturasResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.conSinPagarFacturasResult==null && other.getConSinPagarFacturasResult()==null) || 
             (this.conSinPagarFacturasResult!=null &&
              this.conSinPagarFacturasResult.equals(other.getConSinPagarFacturasResult())));
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
        if (getConSinPagarFacturasResult() != null) {
            _hashCode += getConSinPagarFacturasResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConSinPagarFacturasResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">conSinPagarFacturasResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conSinPagarFacturasResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "conSinPagarFacturasResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>conSinPagarFacturasResponse>conSinPagarFacturasResult"));
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
