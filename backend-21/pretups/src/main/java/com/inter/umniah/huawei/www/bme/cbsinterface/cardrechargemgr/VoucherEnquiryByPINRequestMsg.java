/**
 * VoucherEnquiryByPINRequestMsg.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr;

public class VoucherEnquiryByPINRequestMsg  implements java.io.Serializable {
    private com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader requestHeader;

    private com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryByPINRequest voucherEnquiryByPINRequest;

    public VoucherEnquiryByPINRequestMsg() {
    }

    public VoucherEnquiryByPINRequestMsg(
           com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader requestHeader,
           com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryByPINRequest voucherEnquiryByPINRequest) {
           this.requestHeader = requestHeader;
           this.voucherEnquiryByPINRequest = voucherEnquiryByPINRequest;
    }


    /**
     * Gets the requestHeader value for this VoucherEnquiryByPINRequestMsg.
     * 
     * @return requestHeader
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader getRequestHeader() {
        return requestHeader;
    }


    /**
     * Sets the requestHeader value for this VoucherEnquiryByPINRequestMsg.
     * 
     * @param requestHeader
     */
    public void setRequestHeader(com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader requestHeader) {
        this.requestHeader = requestHeader;
    }


    /**
     * Gets the voucherEnquiryByPINRequest value for this VoucherEnquiryByPINRequestMsg.
     * 
     * @return voucherEnquiryByPINRequest
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryByPINRequest getVoucherEnquiryByPINRequest() {
        return voucherEnquiryByPINRequest;
    }


    /**
     * Sets the voucherEnquiryByPINRequest value for this VoucherEnquiryByPINRequestMsg.
     * 
     * @param voucherEnquiryByPINRequest
     */
    public void setVoucherEnquiryByPINRequest(com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryByPINRequest voucherEnquiryByPINRequest) {
        this.voucherEnquiryByPINRequest = voucherEnquiryByPINRequest;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoucherEnquiryByPINRequestMsg)) return false;
        VoucherEnquiryByPINRequestMsg other = (VoucherEnquiryByPINRequestMsg) obj;
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
            ((this.voucherEnquiryByPINRequest==null && other.getVoucherEnquiryByPINRequest()==null) || 
             (this.voucherEnquiryByPINRequest!=null &&
              this.voucherEnquiryByPINRequest.equals(other.getVoucherEnquiryByPINRequest())));
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
        if (getVoucherEnquiryByPINRequest() != null) {
            _hashCode += getVoucherEnquiryByPINRequest().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoucherEnquiryByPINRequestMsg.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherEnquiryByPINRequestMsg"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("", "RequestHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "RequestHeader"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voucherEnquiryByPINRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("", "VoucherEnquiryByPINRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherEnquiryByPINRequest"));
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
