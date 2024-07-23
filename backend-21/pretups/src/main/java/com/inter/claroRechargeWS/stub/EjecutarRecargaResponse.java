/**
 * EjecutarRecargaResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroRechargeWS.stub;

public class EjecutarRecargaResponse  implements java.io.Serializable {
    private com.inter.claroRechargeWS.stub.AudiTypeRespose auditResponse;

    private com.inter.claroRechargeWS.stub.DatosClienteComplexType[] cliente;

    private com.inter.claroRechargeWS.stub.ResponseOpcionalComplexType[] listaAdicionalResponse;

    public EjecutarRecargaResponse() {
    }

    public EjecutarRecargaResponse(
           com.inter.claroRechargeWS.stub.AudiTypeRespose auditResponse,
           com.inter.claroRechargeWS.stub.DatosClienteComplexType[] cliente,
           com.inter.claroRechargeWS.stub.ResponseOpcionalComplexType[] listaAdicionalResponse) {
           this.auditResponse = auditResponse;
           this.cliente = cliente;
           this.listaAdicionalResponse = listaAdicionalResponse;
    }


    /**
     * Gets the auditResponse value for this EjecutarRecargaResponse.
     * 
     * @return auditResponse
     */
    public com.inter.claroRechargeWS.stub.AudiTypeRespose getAuditResponse() {
        return auditResponse;
    }


    /**
     * Sets the auditResponse value for this EjecutarRecargaResponse.
     * 
     * @param auditResponse
     */
    public void setAuditResponse(com.inter.claroRechargeWS.stub.AudiTypeRespose auditResponse) {
        this.auditResponse = auditResponse;
    }


    /**
     * Gets the cliente value for this EjecutarRecargaResponse.
     * 
     * @return cliente
     */
    public com.inter.claroRechargeWS.stub.DatosClienteComplexType[] getCliente() {
        return cliente;
    }


    /**
     * Sets the cliente value for this EjecutarRecargaResponse.
     * 
     * @param cliente
     */
    public void setCliente(com.inter.claroRechargeWS.stub.DatosClienteComplexType[] cliente) {
        this.cliente = cliente;
    }


    /**
     * Gets the listaAdicionalResponse value for this EjecutarRecargaResponse.
     * 
     * @return listaAdicionalResponse
     */
    public com.inter.claroRechargeWS.stub.ResponseOpcionalComplexType[] getListaAdicionalResponse() {
        return listaAdicionalResponse;
    }


    /**
     * Sets the listaAdicionalResponse value for this EjecutarRecargaResponse.
     * 
     * @param listaAdicionalResponse
     */
    public void setListaAdicionalResponse(com.inter.claroRechargeWS.stub.ResponseOpcionalComplexType[] listaAdicionalResponse) {
        this.listaAdicionalResponse = listaAdicionalResponse;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EjecutarRecargaResponse)) return false;
        EjecutarRecargaResponse other = (EjecutarRecargaResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.auditResponse==null && other.getAuditResponse()==null) || 
             (this.auditResponse!=null &&
              this.auditResponse.equals(other.getAuditResponse()))) &&
            ((this.cliente==null && other.getCliente()==null) || 
             (this.cliente!=null &&
              java.util.Arrays.equals(this.cliente, other.getCliente()))) &&
            ((this.listaAdicionalResponse==null && other.getListaAdicionalResponse()==null) || 
             (this.listaAdicionalResponse!=null &&
              java.util.Arrays.equals(this.listaAdicionalResponse, other.getListaAdicionalResponse())));
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
        if (getAuditResponse() != null) {
            _hashCode += getAuditResponse().hashCode();
        }
        if (getCliente() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCliente());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCliente(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getListaAdicionalResponse() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getListaAdicionalResponse());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getListaAdicionalResponse(), i);
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
        new org.apache.axis.description.TypeDesc(EjecutarRecargaResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", ">ejecutarRecargaResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("auditResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "auditResponse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "audiTypeRespose"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cliente");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "cliente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "DatosClienteComplexType"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "DatosCliente"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listaAdicionalResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "listaAdicionalResponse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "ResponseOpcionalComplexType"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "ResponseOpcional"));
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
