/**
 * EjecutarRecargaRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroRechargeWS.stub;

public class EjecutarRecargaRequest  implements java.io.Serializable {
    private com.inter.claroRechargeWS.stub.AudiTypeRequest auditRequest;

    private java.lang.String msisdn;

    private java.lang.String montoRecarga;

    private com.inter.claroRechargeWS.stub.RequestOpcionalComplexType[] listaAdicional;

    public EjecutarRecargaRequest() {
    }

    public EjecutarRecargaRequest(
           com.inter.claroRechargeWS.stub.AudiTypeRequest auditRequest,
           java.lang.String msisdn,
           java.lang.String montoRecarga,
           com.inter.claroRechargeWS.stub.RequestOpcionalComplexType[] listaAdicional) {
           this.auditRequest = auditRequest;
           this.msisdn = msisdn;
           this.montoRecarga = montoRecarga;
           this.listaAdicional = listaAdicional;
    }


    /**
     * Gets the auditRequest value for this EjecutarRecargaRequest.
     * 
     * @return auditRequest
     */
    public com.inter.claroRechargeWS.stub.AudiTypeRequest getAuditRequest() {
        return auditRequest;
    }


    /**
     * Sets the auditRequest value for this EjecutarRecargaRequest.
     * 
     * @param auditRequest
     */
    public void setAuditRequest(com.inter.claroRechargeWS.stub.AudiTypeRequest auditRequest) {
        this.auditRequest = auditRequest;
    }


    /**
     * Gets the msisdn value for this EjecutarRecargaRequest.
     * 
     * @return msisdn
     */
    public java.lang.String getMsisdn() {
        return msisdn;
    }


    /**
     * Sets the msisdn value for this EjecutarRecargaRequest.
     * 
     * @param msisdn
     */
    public void setMsisdn(java.lang.String msisdn) {
        this.msisdn = msisdn;
    }


    /**
     * Gets the montoRecarga value for this EjecutarRecargaRequest.
     * 
     * @return montoRecarga
     */
    public java.lang.String getMontoRecarga() {
        return montoRecarga;
    }


    /**
     * Sets the montoRecarga value for this EjecutarRecargaRequest.
     * 
     * @param montoRecarga
     */
    public void setMontoRecarga(java.lang.String montoRecarga) {
        this.montoRecarga = montoRecarga;
    }


    /**
     * Gets the listaAdicional value for this EjecutarRecargaRequest.
     * 
     * @return listaAdicional
     */
    public com.inter.claroRechargeWS.stub.RequestOpcionalComplexType[] getListaAdicional() {
        return listaAdicional;
    }


    /**
     * Sets the listaAdicional value for this EjecutarRecargaRequest.
     * 
     * @param listaAdicional
     */
    public void setListaAdicional(com.inter.claroRechargeWS.stub.RequestOpcionalComplexType[] listaAdicional) {
        this.listaAdicional = listaAdicional;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EjecutarRecargaRequest)) return false;
        EjecutarRecargaRequest other = (EjecutarRecargaRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.auditRequest==null && other.getAuditRequest()==null) || 
             (this.auditRequest!=null &&
              this.auditRequest.equals(other.getAuditRequest()))) &&
            ((this.msisdn==null && other.getMsisdn()==null) || 
             (this.msisdn!=null &&
              this.msisdn.equals(other.getMsisdn()))) &&
            ((this.montoRecarga==null && other.getMontoRecarga()==null) || 
             (this.montoRecarga!=null &&
              this.montoRecarga.equals(other.getMontoRecarga()))) &&
            ((this.listaAdicional==null && other.getListaAdicional()==null) || 
             (this.listaAdicional!=null &&
              java.util.Arrays.equals(this.listaAdicional, other.getListaAdicional())));
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
        if (getAuditRequest() != null) {
            _hashCode += getAuditRequest().hashCode();
        }
        if (getMsisdn() != null) {
            _hashCode += getMsisdn().hashCode();
        }
        if (getMontoRecarga() != null) {
            _hashCode += getMontoRecarga().hashCode();
        }
        if (getListaAdicional() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getListaAdicional());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getListaAdicional(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EjecutarRecargaRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", ">ejecutarRecargaRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("auditRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "auditRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "audiTypeRequest"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msisdn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "msisdn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("montoRecarga");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "montoRecarga"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listaAdicional");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "listaAdicional"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "RequestOpcionalComplexType"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "RequestOpcional"));
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
