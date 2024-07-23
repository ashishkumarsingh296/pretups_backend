/**
 * VoucherEnquiryBySeqRequestMsg.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr;

public class VoucherEnquiryBySeqRequestMsg  implements java.io.Serializable {
    private com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader requestHeader;

    private com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryBySeqRequest voucherEnquiryBySeqRequest;

    public VoucherEnquiryBySeqRequestMsg() {
    }

    public VoucherEnquiryBySeqRequestMsg(
           com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader requestHeader,
           com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryBySeqRequest voucherEnquiryBySeqRequest) {
           this.requestHeader = requestHeader;
           this.voucherEnquiryBySeqRequest = voucherEnquiryBySeqRequest;
    }


    /**
     * Gets the requestHeader value for this VoucherEnquiryBySeqRequestMsg.
     * 
     * @return requestHeader
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader getRequestHeader() {
        return requestHeader;
    }


    /**
     * Sets the requestHeader value for this VoucherEnquiryBySeqRequestMsg.
     * 
     * @param requestHeader
     */
    public void setRequestHeader(com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader requestHeader) {
        this.requestHeader = requestHeader;
    }


    /**
     * Gets the voucherEnquiryBySeqRequest value for this VoucherEnquiryBySeqRequestMsg.
     * 
     * @return voucherEnquiryBySeqRequest
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryBySeqRequest getVoucherEnquiryBySeqRequest() {
        return voucherEnquiryBySeqRequest;
    }


    /**
     * Sets the voucherEnquiryBySeqRequest value for this VoucherEnquiryBySeqRequestMsg.
     * 
     * @param voucherEnquiryBySeqRequest
     */
    public void setVoucherEnquiryBySeqRequest(com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryBySeqRequest voucherEnquiryBySeqRequest) {
        this.voucherEnquiryBySeqRequest = voucherEnquiryBySeqRequest;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoucherEnquiryBySeqRequestMsg)) return false;
        VoucherEnquiryBySeqRequestMsg other = (VoucherEnquiryBySeqRequestMsg) obj;
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
            ((this.voucherEnquiryBySeqRequest==null && other.getVoucherEnquiryBySeqRequest()==null) || 
             (this.voucherEnquiryBySeqRequest!=null &&
              this.voucherEnquiryBySeqRequest.equals(other.getVoucherEnquiryBySeqRequest())));
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
        if (getVoucherEnquiryBySeqRequest() != null) {
            _hashCode += getVoucherEnquiryBySeqRequest().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoucherEnquiryBySeqRequestMsg.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherEnquiryBySeqRequestMsg"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("", "RequestHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "RequestHeader"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voucherEnquiryBySeqRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("", "VoucherEnquiryBySeqRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherEnquiryBySeqRequest"));
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
