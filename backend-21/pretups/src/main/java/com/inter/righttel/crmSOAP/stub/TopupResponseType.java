/**
 * TopupResponseType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.righttel.crmSOAP.stub;

public class TopupResponseType  implements java.io.Serializable {
    private int responseCode;

    private java.lang.String responseDetail;

    private java.lang.String transactionId;

    private java.util.Calendar transactionDate;

    private com.inter.righttel.crmSOAP.stub.SourceType source;

    private java.lang.String referenceId;

    private com.inter.righttel.crmSOAP.stub.SubscriberType subscriber;

    private com.inter.righttel.crmSOAP.stub.AccountType rechargedAccount;

    private com.inter.righttel.crmSOAP.stub.AccountType bonuses;

    public TopupResponseType() {
    }

    public TopupResponseType(
           int responseCode,
           java.lang.String responseDetail,
           java.lang.String transactionId,
           java.util.Calendar transactionDate,
           com.inter.righttel.crmSOAP.stub.SourceType source,
           java.lang.String referenceId,
           com.inter.righttel.crmSOAP.stub.SubscriberType subscriber,
           com.inter.righttel.crmSOAP.stub.AccountType rechargedAccount,
           com.inter.righttel.crmSOAP.stub.AccountType bonuses) {
           this.responseCode = responseCode;
           this.responseDetail = responseDetail;
           this.transactionId = transactionId;
           this.transactionDate = transactionDate;
           this.source = source;
           this.referenceId = referenceId;
           this.subscriber = subscriber;
           this.rechargedAccount = rechargedAccount;
           this.bonuses = bonuses;
    }


    /**
     * Gets the responseCode value for this TopupResponseType.
     * 
     * @return responseCode
     */
    public int getResponseCode() {
        return responseCode;
    }


    /**
     * Sets the responseCode value for this TopupResponseType.
     * 
     * @param responseCode
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }


    /**
     * Gets the responseDetail value for this TopupResponseType.
     * 
     * @return responseDetail
     */
    public java.lang.String getResponseDetail() {
        return responseDetail;
    }


    /**
     * Sets the responseDetail value for this TopupResponseType.
     * 
     * @param responseDetail
     */
    public void setResponseDetail(java.lang.String responseDetail) {
        this.responseDetail = responseDetail;
    }


    /**
     * Gets the transactionId value for this TopupResponseType.
     * 
     * @return transactionId
     */
    public java.lang.String getTransactionId() {
        return transactionId;
    }


    /**
     * Sets the transactionId value for this TopupResponseType.
     * 
     * @param transactionId
     */
    public void setTransactionId(java.lang.String transactionId) {
        this.transactionId = transactionId;
    }


    /**
     * Gets the transactionDate value for this TopupResponseType.
     * 
     * @return transactionDate
     */
    public java.util.Calendar getTransactionDate() {
        return transactionDate;
    }


    /**
     * Sets the transactionDate value for this TopupResponseType.
     * 
     * @param transactionDate
     */
    public void setTransactionDate(java.util.Calendar transactionDate) {
        this.transactionDate = transactionDate;
    }


    /**
     * Gets the source value for this TopupResponseType.
     * 
     * @return source
     */
    public com.inter.righttel.crmSOAP.stub.SourceType getSource() {
        return source;
    }


    /**
     * Sets the source value for this TopupResponseType.
     * 
     * @param source
     */
    public void setSource(com.inter.righttel.crmSOAP.stub.SourceType source) {
        this.source = source;
    }


    /**
     * Gets the referenceId value for this TopupResponseType.
     * 
     * @return referenceId
     */
    public java.lang.String getReferenceId() {
        return referenceId;
    }


    /**
     * Sets the referenceId value for this TopupResponseType.
     * 
     * @param referenceId
     */
    public void setReferenceId(java.lang.String referenceId) {
        this.referenceId = referenceId;
    }


    /**
     * Gets the subscriber value for this TopupResponseType.
     * 
     * @return subscriber
     */
    public com.inter.righttel.crmSOAP.stub.SubscriberType getSubscriber() {
        return subscriber;
    }


    /**
     * Sets the subscriber value for this TopupResponseType.
     * 
     * @param subscriber
     */
    public void setSubscriber(com.inter.righttel.crmSOAP.stub.SubscriberType subscriber) {
        this.subscriber = subscriber;
    }


    /**
     * Gets the rechargedAccount value for this TopupResponseType.
     * 
     * @return rechargedAccount
     */
    public com.inter.righttel.crmSOAP.stub.AccountType getRechargedAccount() {
        return rechargedAccount;
    }


    /**
     * Sets the rechargedAccount value for this TopupResponseType.
     * 
     * @param rechargedAccount
     */
    public void setRechargedAccount(com.inter.righttel.crmSOAP.stub.AccountType rechargedAccount) {
        this.rechargedAccount = rechargedAccount;
    }


    /**
     * Gets the bonuses value for this TopupResponseType.
     * 
     * @return bonuses
     */
    public com.inter.righttel.crmSOAP.stub.AccountType getBonuses() {
        return bonuses;
    }


    /**
     * Sets the bonuses value for this TopupResponseType.
     * 
     * @param bonuses
     */
    public void setBonuses(com.inter.righttel.crmSOAP.stub.AccountType bonuses) {
        this.bonuses = bonuses;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TopupResponseType)) return false;
        TopupResponseType other = (TopupResponseType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.responseCode == other.getResponseCode() &&
            ((this.responseDetail==null && other.getResponseDetail()==null) || 
             (this.responseDetail!=null &&
              this.responseDetail.equals(other.getResponseDetail()))) &&
            ((this.transactionId==null && other.getTransactionId()==null) || 
             (this.transactionId!=null &&
              this.transactionId.equals(other.getTransactionId()))) &&
            ((this.transactionDate==null && other.getTransactionDate()==null) || 
             (this.transactionDate!=null &&
              this.transactionDate.equals(other.getTransactionDate()))) &&
            ((this.source==null && other.getSource()==null) || 
             (this.source!=null &&
              this.source.equals(other.getSource()))) &&
            ((this.referenceId==null && other.getReferenceId()==null) || 
             (this.referenceId!=null &&
              this.referenceId.equals(other.getReferenceId()))) &&
            ((this.subscriber==null && other.getSubscriber()==null) || 
             (this.subscriber!=null &&
              this.subscriber.equals(other.getSubscriber()))) &&
            ((this.rechargedAccount==null && other.getRechargedAccount()==null) || 
             (this.rechargedAccount!=null &&
              this.rechargedAccount.equals(other.getRechargedAccount()))) &&
            ((this.bonuses==null && other.getBonuses()==null) || 
             (this.bonuses!=null &&
              this.bonuses.equals(other.getBonuses())));
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
        _hashCode += getResponseCode();
        if (getResponseDetail() != null) {
            _hashCode += getResponseDetail().hashCode();
        }
        if (getTransactionId() != null) {
            _hashCode += getTransactionId().hashCode();
        }
        if (getTransactionDate() != null) {
            _hashCode += getTransactionDate().hashCode();
        }
        if (getSource() != null) {
            _hashCode += getSource().hashCode();
        }
        if (getReferenceId() != null) {
            _hashCode += getReferenceId().hashCode();
        }
        if (getSubscriber() != null) {
            _hashCode += getSubscriber().hashCode();
        }
        if (getRechargedAccount() != null) {
            _hashCode += getRechargedAccount().hashCode();
        }
        if (getBonuses() != null) {
            _hashCode += getBonuses().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TopupResponseType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "TopupResponseType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "responseCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseDetail");
        elemField.setXmlName(new javax.xml.namespace.QName("", "responseDetail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("source");
        elemField.setXmlName(new javax.xml.namespace.QName("", "source"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "SourceType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("referenceId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "referenceId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("rechargedAccount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rechargedAccount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "AccountType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bonuses");
        elemField.setXmlName(new javax.xml.namespace.QName("", "bonuses"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "AccountType"));
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
