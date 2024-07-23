/**
 * CMSResponse.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC1 Sep 29, 2004 (08:29:40 EDT) WSDL2Java emitter.
 */

package com.inter.postvfe.postvfestub;

public class CMSResponse implements java.io.Serializable {
    private java.lang.String statusCode;
    private java.lang.String statusDesc;
    private java.lang.String transactionId;
    private com.inter.postvfe.postvfestub.CMSParamType[] outparams;

    public CMSResponse() {
    }

    public CMSResponse(java.lang.String statusCode, java.lang.String statusDesc, java.lang.String transactionId, com.inter.postvfe.postvfestub.CMSParamType[] outparams) {
        this.statusCode = statusCode;
        this.statusDesc = statusDesc;
        this.transactionId = transactionId;
        this.outparams = outparams;
    }

    /**
     * Gets the statusCode value for this CMSResponse.
     * 
     * @return statusCode
     */
    public java.lang.String getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the statusCode value for this CMSResponse.
     * 
     * @param statusCode
     */
    public void setStatusCode(java.lang.String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets the statusDesc value for this CMSResponse.
     * 
     * @return statusDesc
     */
    public java.lang.String getStatusDesc() {
        return statusDesc;
    }

    /**
     * Sets the statusDesc value for this CMSResponse.
     * 
     * @param statusDesc
     */
    public void setStatusDesc(java.lang.String statusDesc) {
        this.statusDesc = statusDesc;
    }

    /**
     * Gets the transactionId value for this CMSResponse.
     * 
     * @return transactionId
     */
    public java.lang.String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the transactionId value for this CMSResponse.
     * 
     * @param transactionId
     */
    public void setTransactionId(java.lang.String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Gets the outparams value for this CMSResponse.
     * 
     * @return outparams
     */
    public com.inter.postvfe.postvfestub.CMSParamType[] getOutparams() {
        return outparams;
    }

    /**
     * Sets the outparams value for this CMSResponse.
     * 
     * @param outparams
     */
    public void setOutparams(com.inter.postvfe.postvfestub.CMSParamType[] outparams) {
        this.outparams = outparams;
    }

    public com.inter.postvfe.postvfestub.CMSParamType getOutparams(int i) {
        return this.outparams[i];
    }

    public void setOutparams(int i, com.inter.postvfe.postvfestub.CMSParamType _value) {
        this.outparams[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CMSResponse))
            return false;
        CMSResponse other = (CMSResponse) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && ((this.statusCode == null && other.getStatusCode() == null) || (this.statusCode != null && this.statusCode.equals(other.getStatusCode()))) && ((this.statusDesc == null && other.getStatusDesc() == null) || (this.statusDesc != null && this.statusDesc.equals(other.getStatusDesc()))) && ((this.transactionId == null && other.getTransactionId() == null) || (this.transactionId != null && this.transactionId.equals(other.getTransactionId()))) && ((this.outparams == null && other.getOutparams() == null) || (this.outparams != null && java.util.Arrays.equals(this.outparams, other.getOutparams())));
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
        if (getStatusCode() != null) {
            _hashCode += getStatusCode().hashCode();
        }
        if (getStatusDesc() != null) {
            _hashCode += getStatusDesc().hashCode();
        }
        if (getTransactionId() != null) {
            _hashCode += getTransactionId().hashCode();
        }
        if (getOutparams() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getOutparams()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOutparams(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(CMSResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://cmsadapter.asset.com/interfaces/webservice/types/", "CMSResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statusCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "statusCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statusDesc");
        elemField.setXmlName(new javax.xml.namespace.QName("", "statusDesc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outparams");
        elemField.setXmlName(new javax.xml.namespace.QName("", "outparams"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cmsadapter.asset.com/interfaces/webservice/types/", "CMSParamType"));
        elemField.setMinOccurs(0);
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
    public static org.apache.axis.encoding.Serializer getSerializer(java.lang.String mechType, java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(java.lang.String mechType, java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
    }

}
