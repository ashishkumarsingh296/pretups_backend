/**
 * ProPagoPostpagoFijaResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.enquiry;

public class ProPagoPostpagoFijaResponse  implements java.io.Serializable {
    private com.inter.claroca.dth.enquiry.ProPagoPostpagoFijaResponseProPagoPostpagoFijaResult proPagoPostpagoFijaResult;

    public ProPagoPostpagoFijaResponse() {
    }

    public ProPagoPostpagoFijaResponse(
           com.inter.claroca.dth.enquiry.ProPagoPostpagoFijaResponseProPagoPostpagoFijaResult proPagoPostpagoFijaResult) {
           this.proPagoPostpagoFijaResult = proPagoPostpagoFijaResult;
    }


    /**
     * Gets the proPagoPostpagoFijaResult value for this ProPagoPostpagoFijaResponse.
     * 
     * @return proPagoPostpagoFijaResult
     */
    public com.inter.claroca.dth.enquiry.ProPagoPostpagoFijaResponseProPagoPostpagoFijaResult getProPagoPostpagoFijaResult() {
        return proPagoPostpagoFijaResult;
    }


    /**
     * Sets the proPagoPostpagoFijaResult value for this ProPagoPostpagoFijaResponse.
     * 
     * @param proPagoPostpagoFijaResult
     */
    public void setProPagoPostpagoFijaResult(com.inter.claroca.dth.enquiry.ProPagoPostpagoFijaResponseProPagoPostpagoFijaResult proPagoPostpagoFijaResult) {
        this.proPagoPostpagoFijaResult = proPagoPostpagoFijaResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProPagoPostpagoFijaResponse)) return false;
        ProPagoPostpagoFijaResponse other = (ProPagoPostpagoFijaResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.proPagoPostpagoFijaResult==null && other.getProPagoPostpagoFijaResult()==null) || 
             (this.proPagoPostpagoFijaResult!=null &&
              this.proPagoPostpagoFijaResult.equals(other.getProPagoPostpagoFijaResult())));
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
        if (getProPagoPostpagoFijaResult() != null) {
            _hashCode += getProPagoPostpagoFijaResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ProPagoPostpagoFijaResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">proPagoPostpagoFijaResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("proPagoPostpagoFijaResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "proPagoPostpagoFijaResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>proPagoPostpagoFijaResponse>proPagoPostpagoFijaResult"));
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
