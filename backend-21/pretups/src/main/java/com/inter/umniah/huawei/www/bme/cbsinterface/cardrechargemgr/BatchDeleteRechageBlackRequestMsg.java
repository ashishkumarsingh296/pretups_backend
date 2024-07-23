/**
 * BatchDeleteRechageBlackRequestMsg.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr;

public class BatchDeleteRechageBlackRequestMsg  implements java.io.Serializable {
    private com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader requestHeader;

    private com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.BatchDeleteRechageBlackRequest batchDeleteRechageBlackRequest;

    public BatchDeleteRechageBlackRequestMsg() {
    }

    public BatchDeleteRechageBlackRequestMsg(
           com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader requestHeader,
           com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.BatchDeleteRechageBlackRequest batchDeleteRechageBlackRequest) {
           this.requestHeader = requestHeader;
           this.batchDeleteRechageBlackRequest = batchDeleteRechageBlackRequest;
    }


    /**
     * Gets the requestHeader value for this BatchDeleteRechageBlackRequestMsg.
     * 
     * @return requestHeader
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader getRequestHeader() {
        return requestHeader;
    }


    /**
     * Sets the requestHeader value for this BatchDeleteRechageBlackRequestMsg.
     * 
     * @param requestHeader
     */
    public void setRequestHeader(com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader requestHeader) {
        this.requestHeader = requestHeader;
    }


    /**
     * Gets the batchDeleteRechageBlackRequest value for this BatchDeleteRechageBlackRequestMsg.
     * 
     * @return batchDeleteRechageBlackRequest
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.BatchDeleteRechageBlackRequest getBatchDeleteRechageBlackRequest() {
        return batchDeleteRechageBlackRequest;
    }


    /**
     * Sets the batchDeleteRechageBlackRequest value for this BatchDeleteRechageBlackRequestMsg.
     * 
     * @param batchDeleteRechageBlackRequest
     */
    public void setBatchDeleteRechageBlackRequest(com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.BatchDeleteRechageBlackRequest batchDeleteRechageBlackRequest) {
        this.batchDeleteRechageBlackRequest = batchDeleteRechageBlackRequest;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BatchDeleteRechageBlackRequestMsg)) return false;
        BatchDeleteRechageBlackRequestMsg other = (BatchDeleteRechageBlackRequestMsg) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.requestHeader==null && other.getRequestHeader()==null) || 
             (this.requestHeader!=null &&
              this.requestHeader.equals(other.getRequestHeader()))) &&
            ((this.batchDeleteRechageBlackRequest==null && other.getBatchDeleteRechageBlackRequest()==null) || 
             (this.batchDeleteRechageBlackRequest!=null &&
              this.batchDeleteRechageBlackRequest.equals(other.getBatchDeleteRechageBlackRequest())));
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
        if (getRequestHeader() != null) {
            _hashCode += getRequestHeader().hashCode();
        }
        if (getBatchDeleteRechageBlackRequest() != null) {
            _hashCode += getBatchDeleteRechageBlackRequest().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BatchDeleteRechageBlackRequestMsg.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">BatchDeleteRechageBlackRequestMsg"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("", "RequestHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "RequestHeader"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("batchDeleteRechageBlackRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("", "BatchDeleteRechageBlackRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "BatchDeleteRechageBlackRequest"));
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
