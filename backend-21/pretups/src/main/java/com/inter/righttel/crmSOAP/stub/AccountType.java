/**
 * AccountType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.righttel.crmSOAP.stub;

public class AccountType  implements java.io.Serializable {
    private long amount;

    private long currencyId;

    private java.lang.String currencyName;

    private long unitId;

    private java.lang.String unitName;

    private long unitRelation;

    private java.util.Calendar expiryDate;

    private long newBalance;

    public AccountType() {
    }

    public AccountType(
           long amount,
           long currencyId,
           java.lang.String currencyName,
           long unitId,
           java.lang.String unitName,
           long unitRelation,
           java.util.Calendar expiryDate,
           long newBalance) {
           this.amount = amount;
           this.currencyId = currencyId;
           this.currencyName = currencyName;
           this.unitId = unitId;
           this.unitName = unitName;
           this.unitRelation = unitRelation;
           this.expiryDate = expiryDate;
           this.newBalance = newBalance;
    }


    /**
     * Gets the amount value for this AccountType.
     * 
     * @return amount
     */
    public long getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this AccountType.
     * 
     * @param amount
     */
    public void setAmount(long amount) {
        this.amount = amount;
    }


    /**
     * Gets the currencyId value for this AccountType.
     * 
     * @return currencyId
     */
    public long getCurrencyId() {
        return currencyId;
    }


    /**
     * Sets the currencyId value for this AccountType.
     * 
     * @param currencyId
     */
    public void setCurrencyId(long currencyId) {
        this.currencyId = currencyId;
    }


    /**
     * Gets the currencyName value for this AccountType.
     * 
     * @return currencyName
     */
    public java.lang.String getCurrencyName() {
        return currencyName;
    }


    /**
     * Sets the currencyName value for this AccountType.
     * 
     * @param currencyName
     */
    public void setCurrencyName(java.lang.String currencyName) {
        this.currencyName = currencyName;
    }


    /**
     * Gets the unitId value for this AccountType.
     * 
     * @return unitId
     */
    public long getUnitId() {
        return unitId;
    }


    /**
     * Sets the unitId value for this AccountType.
     * 
     * @param unitId
     */
    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }


    /**
     * Gets the unitName value for this AccountType.
     * 
     * @return unitName
     */
    public java.lang.String getUnitName() {
        return unitName;
    }


    /**
     * Sets the unitName value for this AccountType.
     * 
     * @param unitName
     */
    public void setUnitName(java.lang.String unitName) {
        this.unitName = unitName;
    }


    /**
     * Gets the unitRelation value for this AccountType.
     * 
     * @return unitRelation
     */
    public long getUnitRelation() {
        return unitRelation;
    }


    /**
     * Sets the unitRelation value for this AccountType.
     * 
     * @param unitRelation
     */
    public void setUnitRelation(long unitRelation) {
        this.unitRelation = unitRelation;
    }


    /**
     * Gets the expiryDate value for this AccountType.
     * 
     * @return expiryDate
     */
    public java.util.Calendar getExpiryDate() {
        return expiryDate;
    }


    /**
     * Sets the expiryDate value for this AccountType.
     * 
     * @param expiryDate
     */
    public void setExpiryDate(java.util.Calendar expiryDate) {
        this.expiryDate = expiryDate;
    }


    /**
     * Gets the newBalance value for this AccountType.
     * 
     * @return newBalance
     */
    public long getNewBalance() {
        return newBalance;
    }


    /**
     * Sets the newBalance value for this AccountType.
     * 
     * @param newBalance
     */
    public void setNewBalance(long newBalance) {
        this.newBalance = newBalance;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AccountType)) return false;
        AccountType other = (AccountType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.amount == other.getAmount() &&
            this.currencyId == other.getCurrencyId() &&
            ((this.currencyName==null && other.getCurrencyName()==null) || 
             (this.currencyName!=null &&
              this.currencyName.equals(other.getCurrencyName()))) &&
            this.unitId == other.getUnitId() &&
            ((this.unitName==null && other.getUnitName()==null) || 
             (this.unitName!=null &&
              this.unitName.equals(other.getUnitName()))) &&
            this.unitRelation == other.getUnitRelation() &&
            ((this.expiryDate==null && other.getExpiryDate()==null) || 
             (this.expiryDate!=null &&
              this.expiryDate.equals(other.getExpiryDate()))) &&
            this.newBalance == other.getNewBalance();
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
        _hashCode += new Long(getCurrencyId()).hashCode();
        if (getCurrencyName() != null) {
            _hashCode += getCurrencyName().hashCode();
        }
        _hashCode += new Long(getUnitId()).hashCode();
        if (getUnitName() != null) {
            _hashCode += getUnitName().hashCode();
        }
        _hashCode += new Long(getUnitRelation()).hashCode();
        if (getExpiryDate() != null) {
            _hashCode += getExpiryDate().hashCode();
        }
        _hashCode += new Long(getNewBalance()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AccountType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "AccountType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currencyId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currencyId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currencyName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currencyName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "unitId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "unitName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitRelation");
        elemField.setXmlName(new javax.xml.namespace.QName("", "unitRelation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expiryDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "expiryDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("", "newBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
