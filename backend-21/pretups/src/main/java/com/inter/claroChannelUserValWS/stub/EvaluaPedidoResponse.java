/**
 * EvaluaPedidoResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroChannelUserValWS.stub;

public class EvaluaPedidoResponse  implements java.io.Serializable {
    private java.lang.String idTransaccion;

    private java.lang.String valorResultadoCrediticio;

    private java.lang.String codigoRespuesta;

    private java.lang.String mensajeRespuesta;

    private com.inter.claroChannelUserValWS.stub.ResponseOpcionalComplexType[] listaOpcionalResponse;

    public EvaluaPedidoResponse() {
    }

    public EvaluaPedidoResponse(
           java.lang.String idTransaccion,
           java.lang.String valorResultadoCrediticio,
           java.lang.String codigoRespuesta,
           java.lang.String mensajeRespuesta,
           com.inter.claroChannelUserValWS.stub.ResponseOpcionalComplexType[] listaOpcionalResponse) {
           this.idTransaccion = idTransaccion;
           this.valorResultadoCrediticio = valorResultadoCrediticio;
           this.codigoRespuesta = codigoRespuesta;
           this.mensajeRespuesta = mensajeRespuesta;
           this.listaOpcionalResponse = listaOpcionalResponse;
    }


    /**
     * Gets the idTransaccion value for this EvaluaPedidoResponse.
     * 
     * @return idTransaccion
     */
    public java.lang.String getIdTransaccion() {
        return idTransaccion;
    }


    /**
     * Sets the idTransaccion value for this EvaluaPedidoResponse.
     * 
     * @param idTransaccion
     */
    public void setIdTransaccion(java.lang.String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }


    /**
     * Gets the valorResultadoCrediticio value for this EvaluaPedidoResponse.
     * 
     * @return valorResultadoCrediticio
     */
    public java.lang.String getValorResultadoCrediticio() {
        return valorResultadoCrediticio;
    }


    /**
     * Sets the valorResultadoCrediticio value for this EvaluaPedidoResponse.
     * 
     * @param valorResultadoCrediticio
     */
    public void setValorResultadoCrediticio(java.lang.String valorResultadoCrediticio) {
        this.valorResultadoCrediticio = valorResultadoCrediticio;
    }


    /**
     * Gets the codigoRespuesta value for this EvaluaPedidoResponse.
     * 
     * @return codigoRespuesta
     */
    public java.lang.String getCodigoRespuesta() {
        return codigoRespuesta;
    }


    /**
     * Sets the codigoRespuesta value for this EvaluaPedidoResponse.
     * 
     * @param codigoRespuesta
     */
    public void setCodigoRespuesta(java.lang.String codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }


    /**
     * Gets the mensajeRespuesta value for this EvaluaPedidoResponse.
     * 
     * @return mensajeRespuesta
     */
    public java.lang.String getMensajeRespuesta() {
        return mensajeRespuesta;
    }


    /**
     * Sets the mensajeRespuesta value for this EvaluaPedidoResponse.
     * 
     * @param mensajeRespuesta
     */
    public void setMensajeRespuesta(java.lang.String mensajeRespuesta) {
        this.mensajeRespuesta = mensajeRespuesta;
    }


    /**
     * Gets the listaOpcionalResponse value for this EvaluaPedidoResponse.
     * 
     * @return listaOpcionalResponse
     */
    public com.inter.claroChannelUserValWS.stub.ResponseOpcionalComplexType[] getListaOpcionalResponse() {
        return listaOpcionalResponse;
    }


    /**
     * Sets the listaOpcionalResponse value for this EvaluaPedidoResponse.
     * 
     * @param listaOpcionalResponse
     */
    public void setListaOpcionalResponse(com.inter.claroChannelUserValWS.stub.ResponseOpcionalComplexType[] listaOpcionalResponse) {
        this.listaOpcionalResponse = listaOpcionalResponse;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EvaluaPedidoResponse)) return false;
        EvaluaPedidoResponse other = (EvaluaPedidoResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.idTransaccion==null && other.getIdTransaccion()==null) || 
             (this.idTransaccion!=null &&
              this.idTransaccion.equals(other.getIdTransaccion()))) &&
            ((this.valorResultadoCrediticio==null && other.getValorResultadoCrediticio()==null) || 
             (this.valorResultadoCrediticio!=null &&
              this.valorResultadoCrediticio.equals(other.getValorResultadoCrediticio()))) &&
            ((this.codigoRespuesta==null && other.getCodigoRespuesta()==null) || 
             (this.codigoRespuesta!=null &&
              this.codigoRespuesta.equals(other.getCodigoRespuesta()))) &&
            ((this.mensajeRespuesta==null && other.getMensajeRespuesta()==null) || 
             (this.mensajeRespuesta!=null &&
              this.mensajeRespuesta.equals(other.getMensajeRespuesta()))) &&
            ((this.listaOpcionalResponse==null && other.getListaOpcionalResponse()==null) || 
             (this.listaOpcionalResponse!=null &&
              java.util.Arrays.equals(this.listaOpcionalResponse, other.getListaOpcionalResponse())));
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
        if (getIdTransaccion() != null) {
            _hashCode += getIdTransaccion().hashCode();
        }
        if (getValorResultadoCrediticio() != null) {
            _hashCode += getValorResultadoCrediticio().hashCode();
        }
        if (getCodigoRespuesta() != null) {
            _hashCode += getCodigoRespuesta().hashCode();
        }
        if (getMensajeRespuesta() != null) {
            _hashCode += getMensajeRespuesta().hashCode();
        }
        if (getListaOpcionalResponse() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getListaOpcionalResponse());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getListaOpcionalResponse(), i);
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
        new org.apache.axis.description.TypeDesc(EvaluaPedidoResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", ">evaluaPedidoResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idTransaccion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "idTransaccion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("valorResultadoCrediticio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "valorResultadoCrediticio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codigoRespuesta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "codigoRespuesta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mensajeRespuesta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "mensajeRespuesta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listaOpcionalResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "listaOpcionalResponse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "ResponseOpcionalComplexType"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "ResponseOpcional"));
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
