/**
 * ConSaldoPostpagoFijaResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.payment;

public class ConSaldoPostpagoFijaResponse  implements java.io.Serializable {
    private com.inter.claroca.dth.payment.ConSaldoPostpagoFijaResponseConSaldoPostpagoFijaResult conSaldoPostpagoFijaResult;

    public ConSaldoPostpagoFijaResponse() {
    }

    public ConSaldoPostpagoFijaResponse(
           com.inter.claroca.dth.payment.ConSaldoPostpagoFijaResponseConSaldoPostpagoFijaResult conSaldoPostpagoFijaResult) {
           this.conSaldoPostpagoFijaResult = conSaldoPostpagoFijaResult;
    }


    /**
     * Gets the conSaldoPostpagoFijaResult value for this ConSaldoPostpagoFijaResponse.
     * 
     * @return conSaldoPostpagoFijaResult
     */
    public com.inter.claroca.dth.payment.ConSaldoPostpagoFijaResponseConSaldoPostpagoFijaResult getConSaldoPostpagoFijaResult() {
        return conSaldoPostpagoFijaResult;
    }


    /**
     * Sets the conSaldoPostpagoFijaResult value for this ConSaldoPostpagoFijaResponse.
     * 
     * @param conSaldoPostpagoFijaResult
     */
    public void setConSaldoPostpagoFijaResult(com.inter.claroca.dth.payment.ConSaldoPostpagoFijaResponseConSaldoPostpagoFijaResult conSaldoPostpagoFijaResult) {
        this.conSaldoPostpagoFijaResult = conSaldoPostpagoFijaResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConSaldoPostpagoFijaResponse)) return false;
        ConSaldoPostpagoFijaResponse other = (ConSaldoPostpagoFijaResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.conSaldoPostpagoFijaResult==null && other.getConSaldoPostpagoFijaResult()==null) || 
             (this.conSaldoPostpagoFijaResult!=null &&
              this.conSaldoPostpagoFijaResult.equals(other.getConSaldoPostpagoFijaResult())));
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
        if (getConSaldoPostpagoFijaResult() != null) {
            _hashCode += getConSaldoPostpagoFijaResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConSaldoPostpagoFijaResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">ConSaldoPostpagoFijaResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conSaldoPostpagoFijaResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "ConSaldoPostpagoFijaResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>ConSaldoPostpagoFijaResponse>ConSaldoPostpagoFijaResult"));
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
