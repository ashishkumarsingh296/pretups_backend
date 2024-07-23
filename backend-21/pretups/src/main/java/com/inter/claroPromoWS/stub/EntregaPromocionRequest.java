/**
 * EntregaPromocionRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroPromoWS.stub;

public class EntregaPromocionRequest  implements java.io.Serializable {
    private com.inter.claroPromoWS.stub.AudiTypeReq audit;

    private java.lang.String msisdn;

    private com.inter.claroPromoWS.stub.PromocionReq[] listaPromocionesReq;

    private com.inter.claroPromoWS.stub.DataAdicionalReq[] listaAdicionalReq;

    public EntregaPromocionRequest() {
    }

    public EntregaPromocionRequest(
           com.inter.claroPromoWS.stub.AudiTypeReq audit,
           java.lang.String msisdn,
           com.inter.claroPromoWS.stub.PromocionReq[] listaPromocionesReq,
           com.inter.claroPromoWS.stub.DataAdicionalReq[] listaAdicionalReq) {
           this.audit = audit;
           this.msisdn = msisdn;
           this.listaPromocionesReq = listaPromocionesReq;
           this.listaAdicionalReq = listaAdicionalReq;
    }


    /**
     * Gets the audit value for this EntregaPromocionRequest.
     * 
     * @return audit
     */
    public com.inter.claroPromoWS.stub.AudiTypeReq getAudit() {
        return audit;
    }


    /**
     * Sets the audit value for this EntregaPromocionRequest.
     * 
     * @param audit
     */
    public void setAudit(com.inter.claroPromoWS.stub.AudiTypeReq audit) {
        this.audit = audit;
    }


    /**
     * Gets the msisdn value for this EntregaPromocionRequest.
     * 
     * @return msisdn
     */
    public java.lang.String getMsisdn() {
        return msisdn;
    }


    /**
     * Sets the msisdn value for this EntregaPromocionRequest.
     * 
     * @param msisdn
     */
    public void setMsisdn(java.lang.String msisdn) {
        this.msisdn = msisdn;
    }


    /**
     * Gets the listaPromocionesReq value for this EntregaPromocionRequest.
     * 
     * @return listaPromocionesReq
     */
    public com.inter.claroPromoWS.stub.PromocionReq[] getListaPromocionesReq() {
        return listaPromocionesReq;
    }


    /**
     * Sets the listaPromocionesReq value for this EntregaPromocionRequest.
     * 
     * @param listaPromocionesReq
     */
    public void setListaPromocionesReq(com.inter.claroPromoWS.stub.PromocionReq[] listaPromocionesReq) {
        this.listaPromocionesReq = listaPromocionesReq;
    }

    public com.inter.claroPromoWS.stub.PromocionReq getListaPromocionesReq(int i) {
        return this.listaPromocionesReq[i];
    }

    public void setListaPromocionesReq(int i, com.inter.claroPromoWS.stub.PromocionReq _value) {
        this.listaPromocionesReq[i] = _value;
    }


    /**
     * Gets the listaAdicionalReq value for this EntregaPromocionRequest.
     * 
     * @return listaAdicionalReq
     */
    public com.inter.claroPromoWS.stub.DataAdicionalReq[] getListaAdicionalReq() {
        return listaAdicionalReq;
    }


    /**
     * Sets the listaAdicionalReq value for this EntregaPromocionRequest.
     * 
     * @param listaAdicionalReq
     */
    public void setListaAdicionalReq(com.inter.claroPromoWS.stub.DataAdicionalReq[] listaAdicionalReq) {
        this.listaAdicionalReq = listaAdicionalReq;
    }

    public com.inter.claroPromoWS.stub.DataAdicionalReq getListaAdicionalReq(int i) {
        return this.listaAdicionalReq[i];
    }

    public void setListaAdicionalReq(int i, com.inter.claroPromoWS.stub.DataAdicionalReq _value) {
        this.listaAdicionalReq[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EntregaPromocionRequest)) return false;
        EntregaPromocionRequest other = (EntregaPromocionRequest) obj;
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
            ((this.msisdn==null && other.getMsisdn()==null) || 
             (this.msisdn!=null &&
              this.msisdn.equals(other.getMsisdn()))) &&
            ((this.listaPromocionesReq==null && other.getListaPromocionesReq()==null) || 
             (this.listaPromocionesReq!=null &&
              java.util.Arrays.equals(this.listaPromocionesReq, other.getListaPromocionesReq()))) &&
            ((this.listaAdicionalReq==null && other.getListaAdicionalReq()==null) || 
             (this.listaAdicionalReq!=null &&
              java.util.Arrays.equals(this.listaAdicionalReq, other.getListaAdicionalReq())));
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
        if (getMsisdn() != null) {
            _hashCode += getMsisdn().hashCode();
        }
        if (getListaPromocionesReq() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getListaPromocionesReq());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getListaPromocionesReq(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getListaAdicionalReq() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getListaAdicionalReq());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getListaAdicionalReq(), i);
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
        new org.apache.axis.description.TypeDesc(EntregaPromocionRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", ">entregaPromocionRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("audit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "audit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "audiTypeReq"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msisdn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "msisdn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listaPromocionesReq");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "listaPromocionesReq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "promocionReq"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listaAdicionalReq");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "listaAdicionalReq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "dataAdicionalReq"));
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
