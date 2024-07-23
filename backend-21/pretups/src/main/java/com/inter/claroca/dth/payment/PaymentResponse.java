/**
 * PaymentResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.payment;

public class PaymentResponse  extends org.apache.axis.AxisFault  implements java.io.Serializable {
    private java.lang.String msgresult;

    private java.lang.String respCode;

    private java.util.Calendar birthDate;

    private java.math.BigDecimal conversionRate;

    private java.math.BigDecimal currentBalance;

    private java.util.Calendar datetime;

    private java.lang.String distAmount;

    private java.lang.String transactionId;

    private java.lang.String originTrxnId;

    private java.lang.String identidad;

    private java.lang.String msisdn;

    private java.lang.String newBalance;

    private java.lang.String operAmount;

    private java.lang.String responseCode;

    private java.lang.String transId;

    public PaymentResponse() {
    }

    public PaymentResponse(
           java.lang.String msgresult,
           java.lang.String respCode,
           java.util.Calendar birthDate,
           java.math.BigDecimal conversionRate,
           java.math.BigDecimal currentBalance,
           java.util.Calendar datetime,
           java.lang.String distAmount,
           java.lang.String transactionId,
           java.lang.String originTrxnId,
           java.lang.String identidad,
           java.lang.String msisdn,
           java.lang.String newBalance,
           java.lang.String operAmount,
           java.lang.String responseCode,
           java.lang.String transId) {
        this.msgresult = msgresult;
        this.respCode = respCode;
        this.birthDate = birthDate;
        this.conversionRate = conversionRate;
        this.currentBalance = currentBalance;
        this.datetime = datetime;
        this.distAmount = distAmount;
        this.transactionId = transactionId;
        this.originTrxnId = originTrxnId;
        this.identidad = identidad;
        this.msisdn = msisdn;
        this.newBalance = newBalance;
        this.operAmount = operAmount;
        this.responseCode = responseCode;
        this.transId = transId;
    }


    /**
     * Gets the msgresult value for this PaymentResponse.
     * 
     * @return msgresult
     */
    public java.lang.String getMsgresult() {
        return msgresult;
    }


    /**
     * Sets the msgresult value for this PaymentResponse.
     * 
     * @param msgresult
     */
    public void setMsgresult(java.lang.String msgresult) {
        this.msgresult = msgresult;
    }


    /**
     * Gets the respCode value for this PaymentResponse.
     * 
     * @return respCode
     */
    public java.lang.String getRespCode() {
        return respCode;
    }


    /**
     * Sets the respCode value for this PaymentResponse.
     * 
     * @param respCode
     */
    public void setRespCode(java.lang.String respCode) {
        this.respCode = respCode;
    }


    /**
     * Gets the birthDate value for this PaymentResponse.
     * 
     * @return birthDate
     */
    public java.util.Calendar getBirthDate() {
        return birthDate;
    }


    /**
     * Sets the birthDate value for this PaymentResponse.
     * 
     * @param birthDate
     */
    public void setBirthDate(java.util.Calendar birthDate) {
        this.birthDate = birthDate;
    }


    /**
     * Gets the conversionRate value for this PaymentResponse.
     * 
     * @return conversionRate
     */
    public java.math.BigDecimal getConversionRate() {
        return conversionRate;
    }


    /**
     * Sets the conversionRate value for this PaymentResponse.
     * 
     * @param conversionRate
     */
    public void setConversionRate(java.math.BigDecimal conversionRate) {
        this.conversionRate = conversionRate;
    }


    /**
     * Gets the currentBalance value for this PaymentResponse.
     * 
     * @return currentBalance
     */
    public java.math.BigDecimal getCurrentBalance() {
        return currentBalance;
    }


    /**
     * Sets the currentBalance value for this PaymentResponse.
     * 
     * @param currentBalance
     */
    public void setCurrentBalance(java.math.BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }


    /**
     * Gets the datetime value for this PaymentResponse.
     * 
     * @return datetime
     */
    public java.util.Calendar getDatetime() {
        return datetime;
    }


    /**
     * Sets the datetime value for this PaymentResponse.
     * 
     * @param datetime
     */
    public void setDatetime(java.util.Calendar datetime) {
        this.datetime = datetime;
    }


    /**
     * Gets the distAmount value for this PaymentResponse.
     * 
     * @return distAmount
     */
    public java.lang.String getDistAmount() {
        return distAmount;
    }


    /**
     * Sets the distAmount value for this PaymentResponse.
     * 
     * @param distAmount
     */
    public void setDistAmount(java.lang.String distAmount) {
        this.distAmount = distAmount;
    }


    /**
     * Gets the transactionId value for this PaymentResponse.
     * 
     * @return transactionId
     */
    public java.lang.String getTransactionId() {
        return transactionId;
    }


    /**
     * Sets the transactionId value for this PaymentResponse.
     * 
     * @param transactionId
     */
    public void setTransactionId(java.lang.String transactionId) {
        this.transactionId = transactionId;
    }


    /**
     * Gets the originTrxnId value for this PaymentResponse.
     * 
     * @return originTrxnId
     */
    public java.lang.String getOriginTrxnId() {
        return originTrxnId;
    }


    /**
     * Sets the originTrxnId value for this PaymentResponse.
     * 
     * @param originTrxnId
     */
    public void setOriginTrxnId(java.lang.String originTrxnId) {
        this.originTrxnId = originTrxnId;
    }


    /**
     * Gets the identidad value for this PaymentResponse.
     * 
     * @return identidad
     */
    public java.lang.String getIdentidad() {
        return identidad;
    }


    /**
     * Sets the identidad value for this PaymentResponse.
     * 
     * @param identidad
     */
    public void setIdentidad(java.lang.String identidad) {
        this.identidad = identidad;
    }


    /**
     * Gets the msisdn value for this PaymentResponse.
     * 
     * @return msisdn
     */
    public java.lang.String getMsisdn() {
        return msisdn;
    }


    /**
     * Sets the msisdn value for this PaymentResponse.
     * 
     * @param msisdn
     */
    public void setMsisdn(java.lang.String msisdn) {
        this.msisdn = msisdn;
    }


    /**
     * Gets the newBalance value for this PaymentResponse.
     * 
     * @return newBalance
     */
    public java.lang.String getNewBalance() {
        return newBalance;
    }


    /**
     * Sets the newBalance value for this PaymentResponse.
     * 
     * @param newBalance
     */
    public void setNewBalance(java.lang.String newBalance) {
        this.newBalance = newBalance;
    }


    /**
     * Gets the operAmount value for this PaymentResponse.
     * 
     * @return operAmount
     */
    public java.lang.String getOperAmount() {
        return operAmount;
    }


    /**
     * Sets the operAmount value for this PaymentResponse.
     * 
     * @param operAmount
     */
    public void setOperAmount(java.lang.String operAmount) {
        this.operAmount = operAmount;
    }


    /**
     * Gets the responseCode value for this PaymentResponse.
     * 
     * @return responseCode
     */
    public java.lang.String getResponseCode() {
        return responseCode;
    }


    /**
     * Sets the responseCode value for this PaymentResponse.
     * 
     * @param responseCode
     */
    public void setResponseCode(java.lang.String responseCode) {
        this.responseCode = responseCode;
    }


    /**
     * Gets the transId value for this PaymentResponse.
     * 
     * @return transId
     */
    public java.lang.String getTransId() {
        return transId;
    }


    /**
     * Sets the transId value for this PaymentResponse.
     * 
     * @param transId
     */
    public void setTransId(java.lang.String transId) {
        this.transId = transId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PaymentResponse)) return false;
        PaymentResponse other = (PaymentResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.msgresult==null && other.getMsgresult()==null) || 
             (this.msgresult!=null &&
              this.msgresult.equals(other.getMsgresult()))) &&
            ((this.respCode==null && other.getRespCode()==null) || 
             (this.respCode!=null &&
              this.respCode.equals(other.getRespCode()))) &&
            ((this.birthDate==null && other.getBirthDate()==null) || 
             (this.birthDate!=null &&
              this.birthDate.equals(other.getBirthDate()))) &&
            ((this.conversionRate==null && other.getConversionRate()==null) || 
             (this.conversionRate!=null &&
              this.conversionRate.equals(other.getConversionRate()))) &&
            ((this.currentBalance==null && other.getCurrentBalance()==null) || 
             (this.currentBalance!=null &&
              this.currentBalance.equals(other.getCurrentBalance()))) &&
            ((this.datetime==null && other.getDatetime()==null) || 
             (this.datetime!=null &&
              this.datetime.equals(other.getDatetime()))) &&
            ((this.distAmount==null && other.getDistAmount()==null) || 
             (this.distAmount!=null &&
              this.distAmount.equals(other.getDistAmount()))) &&
            ((this.transactionId==null && other.getTransactionId()==null) || 
             (this.transactionId!=null &&
              this.transactionId.equals(other.getTransactionId()))) &&
            ((this.originTrxnId==null && other.getOriginTrxnId()==null) || 
             (this.originTrxnId!=null &&
              this.originTrxnId.equals(other.getOriginTrxnId()))) &&
            ((this.identidad==null && other.getIdentidad()==null) || 
             (this.identidad!=null &&
              this.identidad.equals(other.getIdentidad()))) &&
            ((this.msisdn==null && other.getMsisdn()==null) || 
             (this.msisdn!=null &&
              this.msisdn.equals(other.getMsisdn()))) &&
            ((this.newBalance==null && other.getNewBalance()==null) || 
             (this.newBalance!=null &&
              this.newBalance.equals(other.getNewBalance()))) &&
            ((this.operAmount==null && other.getOperAmount()==null) || 
             (this.operAmount!=null &&
              this.operAmount.equals(other.getOperAmount()))) &&
            ((this.responseCode==null && other.getResponseCode()==null) || 
             (this.responseCode!=null &&
              this.responseCode.equals(other.getResponseCode()))) &&
            ((this.transId==null && other.getTransId()==null) || 
             (this.transId!=null &&
              this.transId.equals(other.getTransId())));
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
        if (getMsgresult() != null) {
            _hashCode += getMsgresult().hashCode();
        }
        if (getRespCode() != null) {
            _hashCode += getRespCode().hashCode();
        }
        if (getBirthDate() != null) {
            _hashCode += getBirthDate().hashCode();
        }
        if (getConversionRate() != null) {
            _hashCode += getConversionRate().hashCode();
        }
        if (getCurrentBalance() != null) {
            _hashCode += getCurrentBalance().hashCode();
        }
        if (getDatetime() != null) {
            _hashCode += getDatetime().hashCode();
        }
        if (getDistAmount() != null) {
            _hashCode += getDistAmount().hashCode();
        }
        if (getTransactionId() != null) {
            _hashCode += getTransactionId().hashCode();
        }
        if (getOriginTrxnId() != null) {
            _hashCode += getOriginTrxnId().hashCode();
        }
        if (getIdentidad() != null) {
            _hashCode += getIdentidad().hashCode();
        }
        if (getMsisdn() != null) {
            _hashCode += getMsisdn().hashCode();
        }
        if (getNewBalance() != null) {
            _hashCode += getNewBalance().hashCode();
        }
        if (getOperAmount() != null) {
            _hashCode += getOperAmount().hashCode();
        }
        if (getResponseCode() != null) {
            _hashCode += getResponseCode().hashCode();
        }
        if (getTransId() != null) {
            _hashCode += getTransId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PaymentResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "PaymentResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msgresult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "msgresult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("respCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "respCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("birthDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "birthDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conversionRate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "conversionRate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "currentBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("datetime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "datetime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("distAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "distAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "transactionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("originTrxnId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "originTrxnId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("identidad");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "identidad"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msisdn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "msisdn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "newBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("operAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "operAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "responseCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.claro.com.gt/Payment", "transId"));
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


    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {
        context.serialize(qname, null, this);
    }
}
