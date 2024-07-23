/**
 * OfferOrderInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.subscribe;

public class OfferOrderInfo  implements java.io.Serializable {
    private int offerId;

    private long offerOrderKey;

    private java.lang.String effectiveDate;

    private java.lang.String expireDate;

    private com.inter.umniah.huawei.www.bme.cbsinterface.subscribe.OfferOrderInfoAutoType autoType;

    private java.lang.String offerCode;

    private java.lang.String status;

    private com.inter.umniah.huawei.www.bme.cbsinterface.common.SimpleProperty[] simpleProperty;

    private java.lang.Integer currentCycle;

    private java.lang.Integer totalCycle;

    private java.lang.String currentCycleStartDateTime;

    private java.lang.String currentCycleEndDateTime;

    private java.lang.String offerName;

    private java.lang.String offerOrderCode;

    public OfferOrderInfo() {
    }

    public OfferOrderInfo(
           int offerId,
           long offerOrderKey,
           java.lang.String effectiveDate,
           java.lang.String expireDate,
           com.inter.umniah.huawei.www.bme.cbsinterface.subscribe.OfferOrderInfoAutoType autoType,
           java.lang.String offerCode,
           java.lang.String status,
           com.inter.umniah.huawei.www.bme.cbsinterface.common.SimpleProperty[] simpleProperty,
           java.lang.Integer currentCycle,
           java.lang.Integer totalCycle,
           java.lang.String currentCycleStartDateTime,
           java.lang.String currentCycleEndDateTime,
           java.lang.String offerName,
           java.lang.String offerOrderCode) {
           this.offerId = offerId;
           this.offerOrderKey = offerOrderKey;
           this.effectiveDate = effectiveDate;
           this.expireDate = expireDate;
           this.autoType = autoType;
           this.offerCode = offerCode;
           this.status = status;
           this.simpleProperty = simpleProperty;
           this.currentCycle = currentCycle;
           this.totalCycle = totalCycle;
           this.currentCycleStartDateTime = currentCycleStartDateTime;
           this.currentCycleEndDateTime = currentCycleEndDateTime;
           this.offerName = offerName;
           this.offerOrderCode = offerOrderCode;
    }


    /**
     * Gets the offerId value for this OfferOrderInfo.
     * 
     * @return offerId
     */
    public int getOfferId() {
        return offerId;
    }


    /**
     * Sets the offerId value for this OfferOrderInfo.
     * 
     * @param offerId
     */
    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }


    /**
     * Gets the offerOrderKey value for this OfferOrderInfo.
     * 
     * @return offerOrderKey
     */
    public long getOfferOrderKey() {
        return offerOrderKey;
    }


    /**
     * Sets the offerOrderKey value for this OfferOrderInfo.
     * 
     * @param offerOrderKey
     */
    public void setOfferOrderKey(long offerOrderKey) {
        this.offerOrderKey = offerOrderKey;
    }


    /**
     * Gets the effectiveDate value for this OfferOrderInfo.
     * 
     * @return effectiveDate
     */
    public java.lang.String getEffectiveDate() {
        return effectiveDate;
    }


    /**
     * Sets the effectiveDate value for this OfferOrderInfo.
     * 
     * @param effectiveDate
     */
    public void setEffectiveDate(java.lang.String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }


    /**
     * Gets the expireDate value for this OfferOrderInfo.
     * 
     * @return expireDate
     */
    public java.lang.String getExpireDate() {
        return expireDate;
    }


    /**
     * Sets the expireDate value for this OfferOrderInfo.
     * 
     * @param expireDate
     */
    public void setExpireDate(java.lang.String expireDate) {
        this.expireDate = expireDate;
    }


    /**
     * Gets the autoType value for this OfferOrderInfo.
     * 
     * @return autoType
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.subscribe.OfferOrderInfoAutoType getAutoType() {
        return autoType;
    }


    /**
     * Sets the autoType value for this OfferOrderInfo.
     * 
     * @param autoType
     */
    public void setAutoType(com.inter.umniah.huawei.www.bme.cbsinterface.subscribe.OfferOrderInfoAutoType autoType) {
        this.autoType = autoType;
    }


    /**
     * Gets the offerCode value for this OfferOrderInfo.
     * 
     * @return offerCode
     */
    public java.lang.String getOfferCode() {
        return offerCode;
    }


    /**
     * Sets the offerCode value for this OfferOrderInfo.
     * 
     * @param offerCode
     */
    public void setOfferCode(java.lang.String offerCode) {
        this.offerCode = offerCode;
    }


    /**
     * Gets the status value for this OfferOrderInfo.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this OfferOrderInfo.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the simpleProperty value for this OfferOrderInfo.
     * 
     * @return simpleProperty
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.common.SimpleProperty[] getSimpleProperty() {
        return simpleProperty;
    }


    /**
     * Sets the simpleProperty value for this OfferOrderInfo.
     * 
     * @param simpleProperty
     */
    public void setSimpleProperty(com.inter.umniah.huawei.www.bme.cbsinterface.common.SimpleProperty[] simpleProperty) {
        this.simpleProperty = simpleProperty;
    }

    public com.inter.umniah.huawei.www.bme.cbsinterface.common.SimpleProperty getSimpleProperty(int i) {
        return this.simpleProperty[i];
    }

    public void setSimpleProperty(int i, com.inter.umniah.huawei.www.bme.cbsinterface.common.SimpleProperty _value) {
        this.simpleProperty[i] = _value;
    }


    /**
     * Gets the currentCycle value for this OfferOrderInfo.
     * 
     * @return currentCycle
     */
    public java.lang.Integer getCurrentCycle() {
        return currentCycle;
    }


    /**
     * Sets the currentCycle value for this OfferOrderInfo.
     * 
     * @param currentCycle
     */
    public void setCurrentCycle(java.lang.Integer currentCycle) {
        this.currentCycle = currentCycle;
    }


    /**
     * Gets the totalCycle value for this OfferOrderInfo.
     * 
     * @return totalCycle
     */
    public java.lang.Integer getTotalCycle() {
        return totalCycle;
    }


    /**
     * Sets the totalCycle value for this OfferOrderInfo.
     * 
     * @param totalCycle
     */
    public void setTotalCycle(java.lang.Integer totalCycle) {
        this.totalCycle = totalCycle;
    }


    /**
     * Gets the currentCycleStartDateTime value for this OfferOrderInfo.
     * 
     * @return currentCycleStartDateTime
     */
    public java.lang.String getCurrentCycleStartDateTime() {
        return currentCycleStartDateTime;
    }


    /**
     * Sets the currentCycleStartDateTime value for this OfferOrderInfo.
     * 
     * @param currentCycleStartDateTime
     */
    public void setCurrentCycleStartDateTime(java.lang.String currentCycleStartDateTime) {
        this.currentCycleStartDateTime = currentCycleStartDateTime;
    }


    /**
     * Gets the currentCycleEndDateTime value for this OfferOrderInfo.
     * 
     * @return currentCycleEndDateTime
     */
    public java.lang.String getCurrentCycleEndDateTime() {
        return currentCycleEndDateTime;
    }


    /**
     * Sets the currentCycleEndDateTime value for this OfferOrderInfo.
     * 
     * @param currentCycleEndDateTime
     */
    public void setCurrentCycleEndDateTime(java.lang.String currentCycleEndDateTime) {
        this.currentCycleEndDateTime = currentCycleEndDateTime;
    }


    /**
     * Gets the offerName value for this OfferOrderInfo.
     * 
     * @return offerName
     */
    public java.lang.String getOfferName() {
        return offerName;
    }


    /**
     * Sets the offerName value for this OfferOrderInfo.
     * 
     * @param offerName
     */
    public void setOfferName(java.lang.String offerName) {
        this.offerName = offerName;
    }


    /**
     * Gets the offerOrderCode value for this OfferOrderInfo.
     * 
     * @return offerOrderCode
     */
    public java.lang.String getOfferOrderCode() {
        return offerOrderCode;
    }


    /**
     * Sets the offerOrderCode value for this OfferOrderInfo.
     * 
     * @param offerOrderCode
     */
    public void setOfferOrderCode(java.lang.String offerOrderCode) {
        this.offerOrderCode = offerOrderCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OfferOrderInfo)) return false;
        OfferOrderInfo other = (OfferOrderInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.offerId == other.getOfferId() &&
            this.offerOrderKey == other.getOfferOrderKey() &&
            ((this.effectiveDate==null && other.getEffectiveDate()==null) || 
             (this.effectiveDate!=null &&
              this.effectiveDate.equals(other.getEffectiveDate()))) &&
            ((this.expireDate==null && other.getExpireDate()==null) || 
             (this.expireDate!=null &&
              this.expireDate.equals(other.getExpireDate()))) &&
            ((this.autoType==null && other.getAutoType()==null) || 
             (this.autoType!=null &&
              this.autoType.equals(other.getAutoType()))) &&
            ((this.offerCode==null && other.getOfferCode()==null) || 
             (this.offerCode!=null &&
              this.offerCode.equals(other.getOfferCode()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.simpleProperty==null && other.getSimpleProperty()==null) || 
             (this.simpleProperty!=null &&
              java.util.Arrays.equals(this.simpleProperty, other.getSimpleProperty()))) &&
            ((this.currentCycle==null && other.getCurrentCycle()==null) || 
             (this.currentCycle!=null &&
              this.currentCycle.equals(other.getCurrentCycle()))) &&
            ((this.totalCycle==null && other.getTotalCycle()==null) || 
             (this.totalCycle!=null &&
              this.totalCycle.equals(other.getTotalCycle()))) &&
            ((this.currentCycleStartDateTime==null && other.getCurrentCycleStartDateTime()==null) || 
             (this.currentCycleStartDateTime!=null &&
              this.currentCycleStartDateTime.equals(other.getCurrentCycleStartDateTime()))) &&
            ((this.currentCycleEndDateTime==null && other.getCurrentCycleEndDateTime()==null) || 
             (this.currentCycleEndDateTime!=null &&
              this.currentCycleEndDateTime.equals(other.getCurrentCycleEndDateTime()))) &&
            ((this.offerName==null && other.getOfferName()==null) || 
             (this.offerName!=null &&
              this.offerName.equals(other.getOfferName()))) &&
            ((this.offerOrderCode==null && other.getOfferOrderCode()==null) || 
             (this.offerOrderCode!=null &&
              this.offerOrderCode.equals(other.getOfferOrderCode())));
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
        _hashCode += getOfferId();
        _hashCode += new Long(getOfferOrderKey()).hashCode();
        if (getEffectiveDate() != null) {
            _hashCode += getEffectiveDate().hashCode();
        }
        if (getExpireDate() != null) {
            _hashCode += getExpireDate().hashCode();
        }
        if (getAutoType() != null) {
            _hashCode += getAutoType().hashCode();
        }
        if (getOfferCode() != null) {
            _hashCode += getOfferCode().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getSimpleProperty() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSimpleProperty());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSimpleProperty(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCurrentCycle() != null) {
            _hashCode += getCurrentCycle().hashCode();
        }
        if (getTotalCycle() != null) {
            _hashCode += getTotalCycle().hashCode();
        }
        if (getCurrentCycleStartDateTime() != null) {
            _hashCode += getCurrentCycleStartDateTime().hashCode();
        }
        if (getCurrentCycleEndDateTime() != null) {
            _hashCode += getCurrentCycleEndDateTime().hashCode();
        }
        if (getOfferName() != null) {
            _hashCode += getOfferName().hashCode();
        }
        if (getOfferOrderCode() != null) {
            _hashCode += getOfferOrderCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OfferOrderInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "OfferOrderInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("offerId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "OfferId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("offerOrderKey");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "OfferOrderKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("effectiveDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "EffectiveDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expireDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "ExpireDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("autoType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "AutoType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", ">OfferOrderInfo>AutoType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("offerCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "OfferCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "Status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("simpleProperty");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "SimpleProperty"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "SimpleProperty"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentCycle");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "CurrentCycle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalCycle");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "TotalCycle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentCycleStartDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "CurrentCycleStartDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentCycleEndDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "CurrentCycleEndDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("offerName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "OfferName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("offerOrderCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "OfferOrderCode"));
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
