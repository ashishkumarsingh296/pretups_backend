/**
 * BaseRequestType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.righttel.crmSOAP.stub;

public class BaseRequestType  implements java.io.Serializable {
    private long amount;

    private java.lang.Integer amountUnitRelation;

    private com.inter.righttel.crmSOAP.stub.SubscriberType subscriber;

    private com.inter.righttel.crmSOAP.stub.SourceType source;

    private java.lang.String voucherId;

    private java.lang.String voucherSeries;

    private java.lang.String referenceId;

    public BaseRequestType() {
    }

    public BaseRequestType(
           long amount,
           java.lang.Integer amountUnitRelation,
           com.inter.righttel.crmSOAP.stub.SubscriberType subscriber,
           com.inter.righttel.crmSOAP.stub.SourceType source,
           java.lang.String voucherId,
           java.lang.String voucherSeries,
           java.lang.String referenceId) {
           this.amount = amount;
           this.amountUnitRelation = amountUnitRelation;
           this.subscriber = subscriber;
           this.source = source;
           this.voucherId = voucherId;
           this.voucherSeries = voucherSeries;
           this.referenceId = referenceId;
    }


    /**
     * Gets the amount value for this BaseRequestType.
     * 
     * @return amount
     */
    public long getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this BaseRequestType.
     * 
     * @param amount
     */
    public void setAmount(long amount) {
        this.amount = amount;
    }


    /**
     * Gets the amountUnitRelation value for this BaseRequestType.
     * 
     * @return amountUnitRelation
     */
    public java.lang.Integer getAmountUnitRelation() {
        return amountUnitRelation;
    }


    /**
     * Sets the amountUnitRelation value for this BaseRequestType.
     * 
     * @param amountUnitRelation
     */
    public void setAmountUnitRelation(java.lang.Integer amountUnitRelation) {
        this.amountUnitRelation = amountUnitRelation;
    }


    /**
     * Gets the subscriber value for this BaseRequestType.
     * 
     * @return subscriber
     */
    public com.inter.righttel.crmSOAP.stub.SubscriberType getSubscriber() {
        return subscriber;
    }


    /**
     * Sets the subscriber value for this BaseRequestType.
     * 
     * @param subscriber
     */
    public void setSubscriber(com.inter.righttel.crmSOAP.stub.SubscriberType subscriber) {
        this.subscriber = subscriber;
    }


    /**
     * Gets the source value for this BaseRequestType.
     * 
     * @return source
     */
    public com.inter.righttel.crmSOAP.stub.SourceType getSource() {
        return source;
    }


    /**
     * Sets the source value for this BaseRequestType.
     * 
     * @param source
     */
    public void setSource(com.inter.righttel.crmSOAP.stub.SourceType source) {
        this.source = source;
    }


    /**
     * Gets the voucherId value for this BaseRequestType.
     * 
     * @return voucherId
     */
    public java.lang.String getVoucherId() {
        return voucherId;
    }


    /**
     * Sets the voucherId value for this BaseRequestType.
     * 
     * @param voucherId
     */
    public void setVoucherId(java.lang.String voucherId) {
        this.voucherId = voucherId;
    }


    /**
     * Gets the voucherSeries value for this BaseRequestType.
     * 
     * @return voucherSeries
     */
    public java.lang.String getVoucherSeries() {
        return voucherSeries;
    }


    /**
     * Sets the voucherSeries value for this BaseRequestType.
     * 
     * @param voucherSeries
     */
    public void setVoucherSeries(java.lang.String voucherSeries) {
        this.voucherSeries = voucherSeries;
    }


    /**
     * Gets the referenceId value for this BaseRequestType.
     * 
     * @return referenceId
     */
    public java.lang.String getReferenceId() {
        return referenceId;
    }


    /**
     * Sets the referenceId value for this BaseRequestType.
     * 
     * @param referenceId
     */
    public void setReferenceId(java.lang.String referenceId) {
        this.referenceId = referenceId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BaseRequestType)) return false;
        BaseRequestType other = (BaseRequestType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.amount == other.getAmount() &&
            ((this.amountUnitRelation==null && other.getAmountUnitRelation()==null) || 
             (this.amountUnitRelation!=null &&
              this.amountUnitRelation.equals(other.getAmountUnitRelation()))) &&
            ((this.subscriber==null && other.getSubscriber()==null) || 
             (this.subscriber!=null &&
              this.subscriber.equals(other.getSubscriber()))) &&
            ((this.source==null && other.getSource()==null) || 
             (this.source!=null &&
              this.source.equals(other.getSource()))) &&
            ((this.voucherId==null && other.getVoucherId()==null) || 
             (this.voucherId!=null &&
              this.voucherId.equals(other.getVoucherId()))) &&
            ((this.voucherSeries==null && other.getVoucherSeries()==null) || 
             (this.voucherSeries!=null &&
              this.voucherSeries.equals(other.getVoucherSeries()))) &&
            ((this.referenceId==null && other.getReferenceId()==null) || 
             (this.referenceId!=null &&
              this.referenceId.equals(other.getReferenceId())));
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
        _hashCode += new Long(getAmount()).hashCode();
        if (getAmountUnitRelation() != null) {
            _hashCode += getAmountUnitRelation().hashCode();
        }
        if (getSubscriber() != null) {
            _hashCode += getSubscriber().hashCode();
        }
        if (getSource() != null) {
            _hashCode += getSource().hashCode();
        }
        if (getVoucherId() != null) {
            _hashCode += getVoucherId().hashCode();
        }
        if (getVoucherSeries() != null) {
            _hashCode += getVoucherSeries().hashCode();
        }
        if (getReferenceId() != null) {
            _hashCode += getReferenceId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BaseRequestType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "BaseRequestType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amountUnitRelation");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amountUnitRelation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subscriber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subscriber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "SubscriberType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("source");
        elemField.setXmlName(new javax.xml.namespace.QName("", "source"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "SourceType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voucherId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "voucherId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voucherSeries");
        elemField.setXmlName(new javax.xml.namespace.QName("", "voucherSeries"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("referenceId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "referenceId"));
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
