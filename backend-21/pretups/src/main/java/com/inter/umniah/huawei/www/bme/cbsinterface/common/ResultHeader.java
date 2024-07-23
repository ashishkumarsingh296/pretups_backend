/**
 * ResultHeader.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.common;

public class ResultHeader  implements java.io.Serializable {
    private java.lang.String commandId;

    private java.lang.String version;

    private java.lang.String transactionId;

    private java.lang.String sequenceId;

    /* Indicates a code of success/failure, failure reasons.     
     * 405000000: success                Other values: failure reasons */
    private java.lang.String resultCode;

    private java.lang.String resultDesc;

    /* CBSç³»ç»Ÿå�—ç�†çš„è®¢å�•å�· */
    private java.lang.Long orderId;

    private java.lang.String operationTime;

    private java.lang.String reserve1;

    private java.lang.String reserve2;

    private java.lang.String reserve3;

    private java.math.BigInteger tenantId;

    private java.lang.String language;

    public ResultHeader() {
    }

    public ResultHeader(
           java.lang.String commandId,
           java.lang.String version,
           java.lang.String transactionId,
           java.lang.String sequenceId,
           java.lang.String resultCode,
           java.lang.String resultDesc,
           java.lang.Long orderId,
           java.lang.String operationTime,
           java.lang.String reserve1,
           java.lang.String reserve2,
           java.lang.String reserve3,
           java.math.BigInteger tenantId,
           java.lang.String language) {
           this.commandId = commandId;
           this.version = version;
           this.transactionId = transactionId;
           this.sequenceId = sequenceId;
           this.resultCode = resultCode;
           this.resultDesc = resultDesc;
           this.orderId = orderId;
           this.operationTime = operationTime;
           this.reserve1 = reserve1;
           this.reserve2 = reserve2;
           this.reserve3 = reserve3;
           this.tenantId = tenantId;
           this.language = language;
    }


    /**
     * Gets the commandId value for this ResultHeader.
     * 
     * @return commandId
     */
    public java.lang.String getCommandId() {
        return commandId;
    }


    /**
     * Sets the commandId value for this ResultHeader.
     * 
     * @param commandId
     */
    public void setCommandId(java.lang.String commandId) {
        this.commandId = commandId;
    }


    /**
     * Gets the version value for this ResultHeader.
     * 
     * @return version
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this ResultHeader.
     * 
     * @param version
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }


    /**
     * Gets the transactionId value for this ResultHeader.
     * 
     * @return transactionId
     */
    public java.lang.String getTransactionId() {
        return transactionId;
    }


    /**
     * Sets the transactionId value for this ResultHeader.
     * 
     * @param transactionId
     */
    public void setTransactionId(java.lang.String transactionId) {
        this.transactionId = transactionId;
    }


    /**
     * Gets the sequenceId value for this ResultHeader.
     * 
     * @return sequenceId
     */
    public java.lang.String getSequenceId() {
        return sequenceId;
    }


    /**
     * Sets the sequenceId value for this ResultHeader.
     * 
     * @param sequenceId
     */
    public void setSequenceId(java.lang.String sequenceId) {
        this.sequenceId = sequenceId;
    }


    /**
     * Gets the resultCode value for this ResultHeader.
     * 
     * @return resultCode   * Indicates a code of success/failure, failure reasons.     
     * 405000000: success                Other values: failure reasons
     */
    public java.lang.String getResultCode() {
        return resultCode;
    }


    /**
     * Sets the resultCode value for this ResultHeader.
     * 
     * @param resultCode   * Indicates a code of success/failure, failure reasons.     
     * 405000000: success                Other values: failure reasons
     */
    public void setResultCode(java.lang.String resultCode) {
        this.resultCode = resultCode;
    }


    /**
     * Gets the resultDesc value for this ResultHeader.
     * 
     * @return resultDesc
     */
    public java.lang.String getResultDesc() {
        return resultDesc;
    }


    /**
     * Sets the resultDesc value for this ResultHeader.
     * 
     * @param resultDesc
     */
    public void setResultDesc(java.lang.String resultDesc) {
        this.resultDesc = resultDesc;
    }


    /**
     * Gets the orderId value for this ResultHeader.
     * 
     * @return orderId   * CBSç³»ç»Ÿå�—ç�†çš„è®¢å�•å�·
     */
    public java.lang.Long getOrderId() {
        return orderId;
    }


    /**
     * Sets the orderId value for this ResultHeader.
     * 
     * @param orderId   * CBSç³»ç»Ÿå�—ç�†çš„è®¢å�•å�·
     */
    public void setOrderId(java.lang.Long orderId) {
        this.orderId = orderId;
    }


    /**
     * Gets the operationTime value for this ResultHeader.
     * 
     * @return operationTime
     */
    public java.lang.String getOperationTime() {
        return operationTime;
    }


    /**
     * Sets the operationTime value for this ResultHeader.
     * 
     * @param operationTime
     */
    public void setOperationTime(java.lang.String operationTime) {
        this.operationTime = operationTime;
    }


    /**
     * Gets the reserve1 value for this ResultHeader.
     * 
     * @return reserve1
     */
    public java.lang.String getReserve1() {
        return reserve1;
    }


    /**
     * Sets the reserve1 value for this ResultHeader.
     * 
     * @param reserve1
     */
    public void setReserve1(java.lang.String reserve1) {
        this.reserve1 = reserve1;
    }


    /**
     * Gets the reserve2 value for this ResultHeader.
     * 
     * @return reserve2
     */
    public java.lang.String getReserve2() {
        return reserve2;
    }


    /**
     * Sets the reserve2 value for this ResultHeader.
     * 
     * @param reserve2
     */
    public void setReserve2(java.lang.String reserve2) {
        this.reserve2 = reserve2;
    }


    /**
     * Gets the reserve3 value for this ResultHeader.
     * 
     * @return reserve3
     */
    public java.lang.String getReserve3() {
        return reserve3;
    }


    /**
     * Sets the reserve3 value for this ResultHeader.
     * 
     * @param reserve3
     */
    public void setReserve3(java.lang.String reserve3) {
        this.reserve3 = reserve3;
    }


    /**
     * Gets the tenantId value for this ResultHeader.
     * 
     * @return tenantId
     */
    public java.math.BigInteger getTenantId() {
        return tenantId;
    }


    /**
     * Sets the tenantId value for this ResultHeader.
     * 
     * @param tenantId
     */
    public void setTenantId(java.math.BigInteger tenantId) {
        this.tenantId = tenantId;
    }


    /**
     * Gets the language value for this ResultHeader.
     * 
     * @return language
     */
    public java.lang.String getLanguage() {
        return language;
    }


    /**
     * Sets the language value for this ResultHeader.
     * 
     * @param language
     */
    public void setLanguage(java.lang.String language) {
        this.language = language;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResultHeader)) return false;
        ResultHeader other = (ResultHeader) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.commandId==null && other.getCommandId()==null) || 
             (this.commandId!=null &&
              this.commandId.equals(other.getCommandId()))) &&
            ((this.version==null && other.getVersion()==null) || 
             (this.version!=null &&
              this.version.equals(other.getVersion()))) &&
            ((this.transactionId==null && other.getTransactionId()==null) || 
             (this.transactionId!=null &&
              this.transactionId.equals(other.getTransactionId()))) &&
            ((this.sequenceId==null && other.getSequenceId()==null) || 
             (this.sequenceId!=null &&
              this.sequenceId.equals(other.getSequenceId()))) &&
            ((this.resultCode==null && other.getResultCode()==null) || 
             (this.resultCode!=null &&
              this.resultCode.equals(other.getResultCode()))) &&
            ((this.resultDesc==null && other.getResultDesc()==null) || 
             (this.resultDesc!=null &&
              this.resultDesc.equals(other.getResultDesc()))) &&
            ((this.orderId==null && other.getOrderId()==null) || 
             (this.orderId!=null &&
              this.orderId.equals(other.getOrderId()))) &&
            ((this.operationTime==null && other.getOperationTime()==null) || 
             (this.operationTime!=null &&
              this.operationTime.equals(other.getOperationTime()))) &&
            ((this.reserve1==null && other.getReserve1()==null) || 
             (this.reserve1!=null &&
              this.reserve1.equals(other.getReserve1()))) &&
            ((this.reserve2==null && other.getReserve2()==null) || 
             (this.reserve2!=null &&
              this.reserve2.equals(other.getReserve2()))) &&
            ((this.reserve3==null && other.getReserve3()==null) || 
             (this.reserve3!=null &&
              this.reserve3.equals(other.getReserve3()))) &&
            ((this.tenantId==null && other.getTenantId()==null) || 
             (this.tenantId!=null &&
              this.tenantId.equals(other.getTenantId()))) &&
            ((this.language==null && other.getLanguage()==null) || 
             (this.language!=null &&
              this.language.equals(other.getLanguage())));
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
        if (getCommandId() != null) {
            _hashCode += getCommandId().hashCode();
        }
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        if (getTransactionId() != null) {
            _hashCode += getTransactionId().hashCode();
        }
        if (getSequenceId() != null) {
            _hashCode += getSequenceId().hashCode();
        }
        if (getResultCode() != null) {
            _hashCode += getResultCode().hashCode();
        }
        if (getResultDesc() != null) {
            _hashCode += getResultDesc().hashCode();
        }
        if (getOrderId() != null) {
            _hashCode += getOrderId().hashCode();
        }
        if (getOperationTime() != null) {
            _hashCode += getOperationTime().hashCode();
        }
        if (getReserve1() != null) {
            _hashCode += getReserve1().hashCode();
        }
        if (getReserve2() != null) {
            _hashCode += getReserve2().hashCode();
        }
        if (getReserve3() != null) {
            _hashCode += getReserve3().hashCode();
        }
        if (getTenantId() != null) {
            _hashCode += getTenantId().hashCode();
        }
        if (getLanguage() != null) {
            _hashCode += getLanguage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResultHeader.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "ResultHeader"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("commandId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "CommandId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("version");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "Version"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "TransactionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sequenceId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "SequenceId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "ResultCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultDesc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "ResultDesc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "OrderId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("operationTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "OperationTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reserve1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "Reserve1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reserve2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "Reserve2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reserve3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "Reserve3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tenantId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "TenantId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("language");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "Language"));
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
