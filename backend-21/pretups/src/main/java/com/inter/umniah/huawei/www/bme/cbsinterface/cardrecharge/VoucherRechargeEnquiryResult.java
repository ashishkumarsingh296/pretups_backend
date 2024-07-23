/**
 * VoucherRechargeEnquiryResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge;

public class VoucherRechargeEnquiryResult  implements java.io.Serializable {
    private com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeEnquiryResultRechargeLog[] rechargeLog;

    private java.lang.String resultFileName;

    public VoucherRechargeEnquiryResult() {
    }

    public VoucherRechargeEnquiryResult(
           com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeEnquiryResultRechargeLog[] rechargeLog,
           java.lang.String resultFileName) {
           this.rechargeLog = rechargeLog;
           this.resultFileName = resultFileName;
    }


    /**
     * Gets the rechargeLog value for this VoucherRechargeEnquiryResult.
     * 
     * @return rechargeLog
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeEnquiryResultRechargeLog[] getRechargeLog() {
        return rechargeLog;
    }


    /**
     * Sets the rechargeLog value for this VoucherRechargeEnquiryResult.
     * 
     * @param rechargeLog
     */
    public void setRechargeLog(com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeEnquiryResultRechargeLog[] rechargeLog) {
        this.rechargeLog = rechargeLog;
    }

    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeEnquiryResultRechargeLog getRechargeLog(int i) {
        return this.rechargeLog[i];
    }

    public void setRechargeLog(int i, com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeEnquiryResultRechargeLog _value) {
        this.rechargeLog[i] = _value;
    }


    /**
     * Gets the resultFileName value for this VoucherRechargeEnquiryResult.
     * 
     * @return resultFileName
     */
    public java.lang.String getResultFileName() {
        return resultFileName;
    }


    /**
     * Sets the resultFileName value for this VoucherRechargeEnquiryResult.
     * 
     * @param resultFileName
     */
    public void setResultFileName(java.lang.String resultFileName) {
        this.resultFileName = resultFileName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoucherRechargeEnquiryResult)) return false;
        VoucherRechargeEnquiryResult other = (VoucherRechargeEnquiryResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.rechargeLog==null && other.getRechargeLog()==null) || 
             (this.rechargeLog!=null &&
              java.util.Arrays.equals(this.rechargeLog, other.getRechargeLog()))) &&
            ((this.resultFileName==null && other.getResultFileName()==null) || 
             (this.resultFileName!=null &&
              this.resultFileName.equals(other.getResultFileName())));
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
        if (getRechargeLog() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRechargeLog());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRechargeLog(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getResultFileName() != null) {
            _hashCode += getResultFileName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoucherRechargeEnquiryResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherRechargeEnquiryResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rechargeLog");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "RechargeLog"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeEnquiryResult>RechargeLog"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultFileName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "ResultFileName"));
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
