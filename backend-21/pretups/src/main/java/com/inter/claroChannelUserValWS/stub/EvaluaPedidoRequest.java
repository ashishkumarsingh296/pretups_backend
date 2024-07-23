/**
 * EvaluaPedidoRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroChannelUserValWS.stub;

public class EvaluaPedidoRequest  implements java.io.Serializable {
    private com.inter.claroChannelUserValWS.stub.AudiTypeRequest audit;

    private java.lang.String codigoDAC;

    private java.lang.String montoPedido;

    private com.inter.claroChannelUserValWS.stub.RequestOpcionalComplexType[] listaOpcionalRequest;

    public EvaluaPedidoRequest() {
    }

    public EvaluaPedidoRequest(
           com.inter.claroChannelUserValWS.stub.AudiTypeRequest audit,
           java.lang.String codigoDAC,
           java.lang.String montoPedido,
           com.inter.claroChannelUserValWS.stub.RequestOpcionalComplexType[] listaOpcionalRequest) {
           this.audit = audit;
           this.codigoDAC = codigoDAC;
           this.montoPedido = montoPedido;
           this.listaOpcionalRequest = listaOpcionalRequest;
    }


    /**
     * Gets the audit value for this EvaluaPedidoRequest.
     * 
     * @return audit
     */
    public com.inter.claroChannelUserValWS.stub.AudiTypeRequest getAudit() {
        return audit;
    }


    /**
     * Sets the audit value for this EvaluaPedidoRequest.
     * 
     * @param audit
     */
    public void setAudit(com.inter.claroChannelUserValWS.stub.AudiTypeRequest audit) {
        this.audit = audit;
    }


    /**
     * Gets the codigoDAC value for this EvaluaPedidoRequest.
     * 
     * @return codigoDAC
     */
    public java.lang.String getCodigoDAC() {
        return codigoDAC;
    }


    /**
     * Sets the codigoDAC value for this EvaluaPedidoRequest.
     * 
     * @param codigoDAC
     */
    public void setCodigoDAC(java.lang.String codigoDAC) {
        this.codigoDAC = codigoDAC;
    }


    /**
     * Gets the montoPedido value for this EvaluaPedidoRequest.
     * 
     * @return montoPedido
     */
    public java.lang.String getMontoPedido() {
        return montoPedido;
    }


    /**
     * Sets the montoPedido value for this EvaluaPedidoRequest.
     * 
     * @param montoPedido
     */
    public void setMontoPedido(java.lang.String montoPedido) {
        this.montoPedido = montoPedido;
    }


    /**
     * Gets the listaOpcionalRequest value for this EvaluaPedidoRequest.
     * 
     * @return listaOpcionalRequest
     */
    public com.inter.claroChannelUserValWS.stub.RequestOpcionalComplexType[] getListaOpcionalRequest() {
        return listaOpcionalRequest;
    }


    /**
     * Sets the listaOpcionalRequest value for this EvaluaPedidoRequest.
     * 
     * @param listaOpcionalRequest
     */
    public void setListaOpcionalRequest(com.inter.claroChannelUserValWS.stub.RequestOpcionalComplexType[] listaOpcionalRequest) {
        this.listaOpcionalRequest = listaOpcionalRequest;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EvaluaPedidoRequest)) return false;
        EvaluaPedidoRequest other = (EvaluaPedidoRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.audit==null && other.getAudit()==null) || 
             (this.audit!=null &&
              this.audit.equals(other.getAudit()))) &&
            ((this.codigoDAC==null && other.getCodigoDAC()==null) || 
             (this.codigoDAC!=null &&
              this.codigoDAC.equals(other.getCodigoDAC()))) &&
            ((this.montoPedido==null && other.getMontoPedido()==null) || 
             (this.montoPedido!=null &&
              this.montoPedido.equals(other.getMontoPedido()))) &&
            ((this.listaOpcionalRequest==null && other.getListaOpcionalRequest()==null) || 
             (this.listaOpcionalRequest!=null &&
              java.util.Arrays.equals(this.listaOpcionalRequest, other.getListaOpcionalRequest())));
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
        if (getAudit() != null) {
            _hashCode += getAudit().hashCode();
        }
        if (getCodigoDAC() != null) {
            _hashCode += getCodigoDAC().hashCode();
        }
        if (getMontoPedido() != null) {
            _hashCode += getMontoPedido().hashCode();
        }
        if (getListaOpcionalRequest() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getListaOpcionalRequest());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getListaOpcionalRequest(), i);
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
        new org.apache.axis.description.TypeDesc(EvaluaPedidoRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", ">evaluaPedidoRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("audit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "audit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "audiTypeRequest"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codigoDAC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "codigoDAC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("montoPedido");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "montoPedido"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listaOpcionalRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "listaOpcionalRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "RequestOpcionalComplexType"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "RequestOpcional"));
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
