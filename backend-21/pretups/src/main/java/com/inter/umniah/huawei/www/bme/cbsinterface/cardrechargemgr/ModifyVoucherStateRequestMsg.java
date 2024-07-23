/**
 * ModifyVoucherStateRequestMsg.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr;

public class ModifyVoucherStateRequestMsg  implements java.io.Serializable {
    private com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader requestHeader;

    private com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.ModifyVoucherStateRequest modifyVoucherStateRequest;

    public ModifyVoucherStateRequestMsg() {
    }

    public ModifyVoucherStateRequestMsg(
           com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader requestHeader,
           com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.ModifyVoucherStateRequest modifyVoucherStateRequest) {
           this.requestHeader = requestHeader;
           this.modifyVoucherStateRequest = modifyVoucherStateRequest;
    }


    /**
     * Gets the requestHeader value for this ModifyVoucherStateRequestMsg.
     * 
     * @return requestHeader
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader getRequestHeader() {
        return requestHeader;
    }


    /**
     * Sets the requestHeader value for this ModifyVoucherStateRequestMsg.
     * 
     * @param requestHeader
     */
    public void setRequestHeader(com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader requestHeader) {
        this.requestHeader = requestHeader;
    }


    /**
     * Gets the modifyVoucherStateRequest value for this ModifyVoucherStateRequestMsg.
     * 
     * @return modifyVoucherStateRequest
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.ModifyVoucherStateRequest getModifyVoucherStateRequest() {
        return modifyVoucherStateRequest;
    }


    /**
     * Sets the modifyVoucherStateRequest value for this ModifyVoucherStateRequestMsg.
     * 
     * @param modifyVoucherStateRequest
     */
    public void setModifyVoucherStateRequest(com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.ModifyVoucherStateRequest modifyVoucherStateRequest) {
        this.modifyVoucherStateRequest = modifyVoucherStateRequest;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ModifyVoucherStateRequestMsg)) return false;
        ModifyVoucherStateRequestMsg other = (ModifyVoucherStateRequestMsg) obj;
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
            ((this.modifyVoucherStateRequest==null && other.getModifyVoucherStateRequest()==null) || 
             (this.modifyVoucherStateRequest!=null &&
              this.modifyVoucherStateRequest.equals(other.getModifyVoucherStateRequest())));
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
        if (getModifyVoucherStateRequest() != null) {
            _hashCode += getModifyVoucherStateRequest().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ModifyVoucherStateRequestMsg.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">ModifyVoucherStateRequestMsg"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("", "RequestHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "RequestHeader"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modifyVoucherStateRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ModifyVoucherStateRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "ModifyVoucherStateRequest"));
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
