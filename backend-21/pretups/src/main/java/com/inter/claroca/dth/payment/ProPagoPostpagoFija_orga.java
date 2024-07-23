/**
 * ProPagoPostpagoFija_orga.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.payment;

public class ProPagoPostpagoFija_orga  implements java.io.Serializable {
    private java.lang.String cod_pais;

    private java.lang.String cod_banco;

    private java.lang.String num_browse;

    private java.lang.String contrato;

    private java.lang.String factura;

    private java.lang.String monto;

    private java.lang.String sec_banco;

    private java.lang.String tipo_producto;

    public ProPagoPostpagoFija_orga() {
    }

    public ProPagoPostpagoFija_orga(
           java.lang.String cod_pais,
           java.lang.String cod_banco,
           java.lang.String num_browse,
           java.lang.String contrato,
           java.lang.String factura,
           java.lang.String monto,
           java.lang.String sec_banco,
           java.lang.String tipo_producto) {
           this.cod_pais = cod_pais;
           this.cod_banco = cod_banco;
           this.num_browse = num_browse;
           this.contrato = contrato;
           this.factura = factura;
           this.monto = monto;
           this.sec_banco = sec_banco;
           this.tipo_producto = tipo_producto;
    }


    /**
     * Gets the cod_pais value for this ProPagoPostpagoFija_orga.
     * 
     * @return cod_pais
     */
    public java.lang.String getCod_pais() {
        return cod_pais;
    }


    /**
     * Sets the cod_pais value for this ProPagoPostpagoFija_orga.
     * 
     * @param cod_pais
     */
    public void setCod_pais(java.lang.String cod_pais) {
        this.cod_pais = cod_pais;
    }


    /**
     * Gets the cod_banco value for this ProPagoPostpagoFija_orga.
     * 
     * @return cod_banco
     */
    public java.lang.String getCod_banco() {
        return cod_banco;
    }


    /**
     * Sets the cod_banco value for this ProPagoPostpagoFija_orga.
     * 
     * @param cod_banco
     */
    public void setCod_banco(java.lang.String cod_banco) {
        this.cod_banco = cod_banco;
    }


    /**
     * Gets the num_browse value for this ProPagoPostpagoFija_orga.
     * 
     * @return num_browse
     */
    public java.lang.String getNum_browse() {
        return num_browse;
    }


    /**
     * Sets the num_browse value for this ProPagoPostpagoFija_orga.
     * 
     * @param num_browse
     */
    public void setNum_browse(java.lang.String num_browse) {
        this.num_browse = num_browse;
    }


    /**
     * Gets the contrato value for this ProPagoPostpagoFija_orga.
     * 
     * @return contrato
     */
    public java.lang.String getContrato() {
        return contrato;
    }


    /**
     * Sets the contrato value for this ProPagoPostpagoFija_orga.
     * 
     * @param contrato
     */
    public void setContrato(java.lang.String contrato) {
        this.contrato = contrato;
    }


    /**
     * Gets the factura value for this ProPagoPostpagoFija_orga.
     * 
     * @return factura
     */
    public java.lang.String getFactura() {
        return factura;
    }


    /**
     * Sets the factura value for this ProPagoPostpagoFija_orga.
     * 
     * @param factura
     */
    public void setFactura(java.lang.String factura) {
        this.factura = factura;
    }


    /**
     * Gets the monto value for this ProPagoPostpagoFija_orga.
     * 
     * @return monto
     */
    public java.lang.String getMonto() {
        return monto;
    }


    /**
     * Sets the monto value for this ProPagoPostpagoFija_orga.
     * 
     * @param monto
     */
    public void setMonto(java.lang.String monto) {
        this.monto = monto;
    }


    /**
     * Gets the sec_banco value for this ProPagoPostpagoFija_orga.
     * 
     * @return sec_banco
     */
    public java.lang.String getSec_banco() {
        return sec_banco;
    }


    /**
     * Sets the sec_banco value for this ProPagoPostpagoFija_orga.
     * 
     * @param sec_banco
     */
    public void setSec_banco(java.lang.String sec_banco) {
        this.sec_banco = sec_banco;
    }


    /**
     * Gets the tipo_producto value for this ProPagoPostpagoFija_orga.
     * 
     * @return tipo_producto
     */
    public java.lang.String getTipo_producto() {
        return tipo_producto;
    }


    /**
     * Sets the tipo_producto value for this ProPagoPostpagoFija_orga.
     * 
     * @param tipo_producto
     */
    public void setTipo_producto(java.lang.String tipo_producto) {
        this.tipo_producto = tipo_producto;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProPagoPostpagoFija_orga)) return false;
        ProPagoPostpagoFija_orga other = (ProPagoPostpagoFija_orga) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cod_pais==null && other.getCod_pais()==null) || 
             (this.cod_pais!=null &&
              this.cod_pais.equals(other.getCod_pais()))) &&
            ((this.cod_banco==null && other.getCod_banco()==null) || 
             (this.cod_banco!=null &&
              this.cod_banco.equals(other.getCod_banco()))) &&
            ((this.num_browse==null && other.getNum_browse()==null) || 
             (this.num_browse!=null &&
              this.num_browse.equals(other.getNum_browse()))) &&
            ((this.contrato==null && other.getContrato()==null) || 
             (this.contrato!=null &&
              this.contrato.equals(other.getContrato()))) &&
            ((this.factura==null && other.getFactura()==null) || 
             (this.factura!=null &&
              this.factura.equals(other.getFactura()))) &&
            ((this.monto==null && other.getMonto()==null) || 
             (this.monto!=null &&
              this.monto.equals(other.getMonto()))) &&
            ((this.sec_banco==null && other.getSec_banco()==null) || 
             (this.sec_banco!=null &&
              this.sec_banco.equals(other.getSec_banco()))) &&
            ((this.tipo_producto==null && other.getTipo_producto()==null) || 
             (this.tipo_producto!=null &&
              this.tipo_producto.equals(other.getTipo_producto())));
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
        if (getCod_pais() != null) {
            _hashCode += getCod_pais().hashCode();
        }
        if (getCod_banco() != null) {
            _hashCode += getCod_banco().hashCode();
        }
        if (getNum_browse() != null) {
            _hashCode += getNum_browse().hashCode();
        }
        if (getContrato() != null) {
            _hashCode += getContrato().hashCode();
        }
        if (getFactura() != null) {
            _hashCode += getFactura().hashCode();
        }
        if (getMonto() != null) {
            _hashCode += getMonto().hashCode();
        }
        if (getSec_banco() != null) {
            _hashCode += getSec_banco().hashCode();
        }
        if (getTipo_producto() != null) {
            _hashCode += getTipo_producto().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ProPagoPostpagoFija_orga.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">proPagoPostpagoFija_orga"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cod_pais");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "cod_pais"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cod_banco");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "cod_banco"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("num_browse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "num_browse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contrato");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "contrato"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("factura");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "factura"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("monto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "monto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sec_banco");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "sec_banco"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipo_producto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "tipo_producto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
