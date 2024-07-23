/**
 * EntregaPromocionResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroPromoWS.stub;

public class EntregaPromocionResponse  implements java.io.Serializable {
    private java.lang.String idTransaccion;

    private java.lang.String codigoRespuesta;

    private java.lang.String mensajeRespuesta;

    private com.inter.claroPromoWS.stub.DataAdicionalResp[] listaAdicionalResp;

    public EntregaPromocionResponse() {
    }

    public EntregaPromocionResponse(
           java.lang.String idTransaccion,
           java.lang.String codigoRespuesta,
           java.lang.String mensajeRespuesta,
           com.inter.claroPromoWS.stub.DataAdicionalResp[] listaAdicionalResp) {
           this.idTransaccion = idTransaccion;
           this.codigoRespuesta = codigoRespuesta;
           this.mensajeRespuesta = mensajeRespuesta;
           this.listaAdicionalResp = listaAdicionalResp;
    }


    /**
     * Gets the idTransaccion value for this EntregaPromocionResponse.
     * 
     * @return idTransaccion
     */
    public java.lang.String getIdTransaccion() {
        return idTransaccion;
    }


    /**
     * Sets the idTransaccion value for this EntregaPromocionResponse.
     * 
     * @param idTransaccion
     */
    public void setIdTransaccion(java.lang.String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }


    /**
     * Gets the codigoRespuesta value for this EntregaPromocionResponse.
     * 
     * @return codigoRespuesta
     */
    public java.lang.String getCodigoRespuesta() {
        return codigoRespuesta;
    }


    /**
     * Sets the codigoRespuesta value for this EntregaPromocionResponse.
     * 
     * @param codigoRespuesta
     */
    public void setCodigoRespuesta(java.lang.String codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }


    /**
     * Gets the mensajeRespuesta value for this EntregaPromocionResponse.
     * 
     * @return mensajeRespuesta
     */
    public java.lang.String getMensajeRespuesta() {
        return mensajeRespuesta;
    }


    /**
     * Sets the mensajeRespuesta value for this EntregaPromocionResponse.
     * 
     * @param mensajeRespuesta
     */
    public void setMensajeRespuesta(java.lang.String mensajeRespuesta) {
        this.mensajeRespuesta = mensajeRespuesta;
    }


    /**
     * Gets the listaAdicionalResp value for this EntregaPromocionResponse.
     * 
     * @return listaAdicionalResp
     */
    public com.inter.claroPromoWS.stub.DataAdicionalResp[] getListaAdicionalResp() {
        return listaAdicionalResp;
    }


    /**
     * Sets the listaAdicionalResp value for this EntregaPromocionResponse.
     * 
     * @param listaAdicionalResp
     */
    public void setListaAdicionalResp(com.inter.claroPromoWS.stub.DataAdicionalResp[] listaAdicionalResp) {
        this.listaAdicionalResp = listaAdicionalResp;
    }

    public com.inter.claroPromoWS.stub.DataAdicionalResp getListaAdicionalResp(int i) {
        return this.listaAdicionalResp[i];
    }

    public void setListaAdicionalResp(int i, com.inter.claroPromoWS.stub.DataAdicionalResp _value) {
        this.listaAdicionalResp[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EntregaPromocionResponse)) return false;
        EntregaPromocionResponse other = (EntregaPromocionResponse) obj;
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
            ((this.codigoRespuesta==null && other.getCodigoRespuesta()==null) || 
             (this.codigoRespuesta!=null &&
              this.codigoRespuesta.equals(other.getCodigoRespuesta()))) &&
            ((this.mensajeRespuesta==null && other.getMensajeRespuesta()==null) || 
             (this.mensajeRespuesta!=null &&
              this.mensajeRespuesta.equals(other.getMensajeRespuesta()))) &&
            ((this.listaAdicionalResp==null && other.getListaAdicionalResp()==null) || 
             (this.listaAdicionalResp!=null &&
              java.util.Arrays.equals(this.listaAdicionalResp, other.getListaAdicionalResp())));
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
        if (getCodigoRespuesta() != null) {
            _hashCode += getCodigoRespuesta().hashCode();
        }
        if (getMensajeRespuesta() != null) {
            _hashCode += getMensajeRespuesta().hashCode();
        }
        if (getListaAdicionalResp() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getListaAdicionalResp());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getListaAdicionalResp(), i);
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
        new org.apache.axis.description.TypeDesc(EntregaPromocionResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", ">entregaPromocionResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idTransaccion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "idTransaccion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codigoRespuesta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "codigoRespuesta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mensajeRespuesta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "mensajeRespuesta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listaAdicionalResp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "listaAdicionalResp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "dataAdicionalResp"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
