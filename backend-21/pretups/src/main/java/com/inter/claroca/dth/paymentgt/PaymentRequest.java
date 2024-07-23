/**
 * PaymentRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.paymentgt;

public class PaymentRequest  implements java.io.Serializable {
    private java.lang.String countryCode;

    private java.lang.Integer originTrxnId;

    private java.lang.Integer amount;

    private java.lang.String tipo_producto;

    private java.lang.String phoneNumber;

    private java.lang.String[] factura;

    private java.lang.String distId;

    private java.lang.String distPass;

    private java.lang.String distCategory;

    private java.lang.String contrato;

    public PaymentRequest() {
    }

    public PaymentRequest(
           java.lang.String countryCode,
           java.lang.Integer originTrxnId,
           java.lang.Integer amount,
           java.lang.String tipo_producto,
           java.lang.String phoneNumber,
           java.lang.String[] factura,
           java.lang.String distId,
           java.lang.String distPass,
           java.lang.String distCategory,
           java.lang.String contrato) {
           this.countryCode = countryCode;
           this.originTrxnId = originTrxnId;
           this.amount = amount;
           this.tipo_producto = tipo_producto;
           this.phoneNumber = phoneNumber;
           this.factura = factura;
           this.distId = distId;
           this.distPass = distPass;
           this.distCategory = distCategory;
           this.contrato = contrato;
    }


    /**
     * Gets the countryCode value for this PaymentRequest.
     * 
     * @return countryCode
     */
    public java.lang.String getCountryCode() {
        return countryCode;
    }


    /**
     * Sets the countryCode value for this PaymentRequest.
     * 
     * @param countryCode
     */
    public void setCountryCode(java.lang.String countryCode) {
        this.countryCode = countryCode;
    }


    /**
     * Gets the originTrxnId value for this PaymentRequest.
     * 
     * @return originTrxnId
     */
    public java.lang.Integer getOriginTrxnId() {
        return originTrxnId;
    }


    /**
     * Sets the originTrxnId value for this PaymentRequest.
     * 
     * @param originTrxnId
     */
    public void setOriginTrxnId(java.lang.Integer originTrxnId) {
        this.originTrxnId = originTrxnId;
    }


    /**
     * Gets the amount value for this PaymentRequest.
     * 
     * @return amount
     */
    public java.lang.Integer getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this PaymentRequest.
     * 
     * @param amount
     */
    public void setAmount(java.lang.Integer amount) {
        this.amount = amount;
    }


    /**
     * Gets the tipo_producto value for this PaymentRequest.
     * 
     * @return tipo_producto
     */
    public java.lang.String getTipo_producto() {
        return tipo_producto;
    }


    /**
     * Sets the tipo_producto value for this PaymentRequest.
     * 
     * @param tipo_producto
     */
    public void setTipo_producto(java.lang.String tipo_producto) {
        this.tipo_producto = tipo_producto;
    }


    /**
     * Gets the phoneNumber value for this PaymentRequest.
     * 
     * @return phoneNumber
     */
    public java.lang.String getPhoneNumber() {
        return phoneNumber;
    }


    /**
     * Sets the phoneNumber value for this PaymentRequest.
     * 
     * @param phoneNumber
     */
    public void setPhoneNumber(java.lang.String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    /**
     * Gets the factura value for this PaymentRequest.
     * 
     * @return factura
     */
    public java.lang.String[] getFactura() {
        return factura;
    }


    /**
     * Sets the factura value for this PaymentRequest.
     * 
     * @param factura
     */
    public void setFactura(java.lang.String[] factura) {
        this.factura = factura;
    }

    public java.lang.String getFactura(int i) {
        return this.factura[i];
    }

    public void setFactura(int i, java.lang.String _value) {
        this.factura[i] = _value;
    }


    /**
     * Gets the distId value for this PaymentRequest.
     * 
     * @return distId
     */
    public java.lang.String getDistId() {
        return distId;
    }


    /**
     * Sets the distId value for this PaymentRequest.
     * 
     * @param distId
     */
    public void setDistId(java.lang.String distId) {
        this.distId = distId;
    }


    /**
     * Gets the distPass value for this PaymentRequest.
     * 
     * @return distPass
     */
    public java.lang.String getDistPass() {
        return distPass;
    }


    /**
     * Sets the distPass value for this PaymentRequest.
     * 
     * @param distPass
     */
    public void setDistPass(java.lang.String distPass) {
        this.distPass = distPass;
    }


    /**
     * Gets the distCategory value for this PaymentRequest.
     * 
     * @return distCategory
     */
    public java.lang.String getDistCategory() {
        return distCategory;
    }


    /**
     * Sets the distCategory value for this PaymentRequest.
     * 
     * @param distCategory
     */
    public void setDistCategory(java.lang.String distCategory) {
        this.distCategory = distCategory;
    }


    /**
     * Gets the contrato value for this PaymentRequest.
     * 
     * @return contrato
     */
    public java.lang.String getContrato() {
        return contrato;
    }


    /**
     * Sets the contrato value for this PaymentRequest.
     * 
     * @param contrato
     */
    public void setContrato(java.lang.String contrato) {
        this.contrato = contrato;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PaymentRequest)) return false;
        PaymentRequest other = (PaymentRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.countryCode==null && other.getCountryCode()==null) || 
             (this.countryCode!=null &&
              this.countryCode.equals(other.getCountryCode()))) &&
            ((this.originTrxnId==null && other.getOriginTrxnId()==null) || 
             (this.originTrxnId!=null &&
              this.originTrxnId.equals(other.getOriginTrxnId()))) &&
            ((this.amount==null && other.getAmount()==null) || 
             (this.amount!=null &&
              this.amount.equals(other.getAmount()))) &&
            ((this.tipo_producto==null && other.getTipo_producto()==null) || 
             (this.tipo_producto!=null &&
              this.tipo_producto.equals(other.getTipo_producto()))) &&
            ((this.phoneNumber==null && other.getPhoneNumber()==null) || 
             (this.phoneNumber!=null &&
              this.phoneNumber.equals(other.getPhoneNumber()))) &&
            ((this.factura==null && other.getFactura()==null) || 
             (this.factura!=null &&
              java.util.Arrays.equals(this.factura, other.getFactura()))) &&
            ((this.distId==null && other.getDistId()==null) || 
             (this.distId!=null &&
              this.distId.equals(other.getDistId()))) &&
            ((this.distPass==null && other.getDistPass()==null) || 
             (this.distPass!=null &&
              this.distPass.equals(other.getDistPass()))) &&
            ((this.distCategory==null && other.getDistCategory()==null) || 
             (this.distCategory!=null &&
              this.distCategory.equals(other.getDistCategory()))) &&
            ((this.contrato==null && other.getContrato()==null) || 
             (this.contrato!=null &&
              this.contrato.equals(other.getContrato())));
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
        if (getCountryCode() != null) {
            _hashCode += getCountryCode().hashCode();
        }
        if (getOriginTrxnId() != null) {
            _hashCode += getOriginTrxnId().hashCode();
        }
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getTipo_producto() != null) {
            _hashCode += getTipo_producto().hashCode();
        }
        if (getPhoneNumber() != null) {
            _hashCode += getPhoneNumber().hashCode();
        }
        if (getFactura() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFactura());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFactura(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDistId() != null) {
            _hashCode += getDistId().hashCode();
        }
        if (getDistPass() != null) {
            _hashCode += getDistPass().hashCode();
        }
        if (getDistCategory() != null) {
            _hashCode += getDistCategory().hashCode();
        }
        if (getContrato() != null) {
            _hashCode += getContrato().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PaymentRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "PaymentRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "countryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("originTrxnId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "originTrxnId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipo_producto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "tipo_producto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phoneNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "phoneNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("factura");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "factura"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("distId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "distId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("distPass");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "distPass"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("distCategory");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "distCategory"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contrato");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "contrato"));
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
