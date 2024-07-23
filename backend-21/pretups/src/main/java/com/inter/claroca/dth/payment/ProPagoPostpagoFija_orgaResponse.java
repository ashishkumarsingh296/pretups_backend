/**
 * ProPagoPostpagoFija_orgaResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.payment;

public class ProPagoPostpagoFija_orgaResponse  implements java.io.Serializable {
    private com.inter.claroca.dth.payment.ProPagoPostpagoFija_orgaResponseProPagoPostpagoFija_orgaResult proPagoPostpagoFija_orgaResult;

    public ProPagoPostpagoFija_orgaResponse() {
    }

    public ProPagoPostpagoFija_orgaResponse(
           com.inter.claroca.dth.payment.ProPagoPostpagoFija_orgaResponseProPagoPostpagoFija_orgaResult proPagoPostpagoFija_orgaResult) {
           this.proPagoPostpagoFija_orgaResult = proPagoPostpagoFija_orgaResult;
    }


    /**
     * Gets the proPagoPostpagoFija_orgaResult value for this ProPagoPostpagoFija_orgaResponse.
     * 
     * @return proPagoPostpagoFija_orgaResult
     */
    public com.inter.claroca.dth.payment.ProPagoPostpagoFija_orgaResponseProPagoPostpagoFija_orgaResult getProPagoPostpagoFija_orgaResult() {
        return proPagoPostpagoFija_orgaResult;
    }


    /**
     * Sets the proPagoPostpagoFija_orgaResult value for this ProPagoPostpagoFija_orgaResponse.
     * 
     * @param proPagoPostpagoFija_orgaResult
     */
    public void setProPagoPostpagoFija_orgaResult(com.inter.claroca.dth.payment.ProPagoPostpagoFija_orgaResponseProPagoPostpagoFija_orgaResult proPagoPostpagoFija_orgaResult) {
        this.proPagoPostpagoFija_orgaResult = proPagoPostpagoFija_orgaResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProPagoPostpagoFija_orgaResponse)) return false;
        ProPagoPostpagoFija_orgaResponse other = (ProPagoPostpagoFija_orgaResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.proPagoPostpagoFija_orgaResult==null && other.getProPagoPostpagoFija_orgaResult()==null) || 
             (this.proPagoPostpagoFija_orgaResult!=null &&
              this.proPagoPostpagoFija_orgaResult.equals(other.getProPagoPostpagoFija_orgaResult())));
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
        if (getProPagoPostpagoFija_orgaResult() != null) {
            _hashCode += getProPagoPostpagoFija_orgaResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ProPagoPostpagoFija_orgaResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">proPagoPostpagoFija_orgaResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("proPagoPostpagoFija_orgaResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "proPagoPostpagoFija_orgaResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>proPagoPostpagoFija_orgaResponse>proPagoPostpagoFija_orgaResult"));
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
