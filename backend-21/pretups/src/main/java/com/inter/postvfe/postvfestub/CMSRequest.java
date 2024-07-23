/**
 * CMSRequest.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC1 Sep 29, 2004 (08:29:40 EDT) WSDL2Java emitter.
 */

package com.inter.postvfe.postvfestub;

public class CMSRequest implements java.io.Serializable {
    private java.lang.String username;
    private java.lang.String password;
    private java.lang.String wfname;
    private com.inter.postvfe.postvfestub.CMSParamType[] params;

    public CMSRequest() {
    }

    public CMSRequest(java.lang.String username, java.lang.String password, java.lang.String wfname, com.inter.postvfe.postvfestub.CMSParamType[] params) {
        this.username = username;
        this.password = password;
        this.wfname = wfname;
        this.params = params;
    }

    /**
     * Gets the username value for this CMSRequest.
     * 
     * @return username
     */
    public java.lang.String getUsername() {
        return username;
    }

    /**
     * Sets the username value for this CMSRequest.
     * 
     * @param username
     */
    public void setUsername(java.lang.String username) {
        this.username = username;
    }

    /**
     * Gets the password value for this CMSRequest.
     * 
     * @return password
     */
    public java.lang.String getPassword() {
        return password;
    }

    /**
     * Sets the password value for this CMSRequest.
     * 
     * @param password
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }

    /**
     * Gets the wfname value for this CMSRequest.
     * 
     * @return wfname
     */
    public java.lang.String getWfname() {
        return wfname;
    }

    /**
     * Sets the wfname value for this CMSRequest.
     * 
     * @param wfname
     */
    public void setWfname(java.lang.String wfname) {
        this.wfname = wfname;
    }

    /**
     * Gets the params value for this CMSRequest.
     * 
     * @return params
     */
    public com.inter.postvfe.postvfestub.CMSParamType[] getParams() {
        return params;
    }

    /**
     * Sets the params value for this CMSRequest.
     * 
     * @param params
     */
    public void setParams(com.inter.postvfe.postvfestub.CMSParamType[] params) {
        this.params = params;
    }

    public com.inter.postvfe.postvfestub.CMSParamType getParams(int i) {
        return this.params[i];
    }

    public void setParams(int i, com.inter.postvfe.postvfestub.CMSParamType _value) {
        this.params[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CMSRequest))
            return false;
        CMSRequest other = (CMSRequest) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && ((this.username == null && other.getUsername() == null) || (this.username != null && this.username.equals(other.getUsername()))) && ((this.password == null && other.getPassword() == null) || (this.password != null && this.password.equals(other.getPassword()))) && ((this.wfname == null && other.getWfname() == null) || (this.wfname != null && this.wfname.equals(other.getWfname()))) && ((this.params == null && other.getParams() == null) || (this.params != null && java.util.Arrays.equals(this.params, other.getParams())));
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
        if (getUsername() != null) {
            _hashCode += getUsername().hashCode();
        }
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getWfname() != null) {
            _hashCode += getWfname().hashCode();
        }
        if (getParams() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getParams()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParams(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(CMSRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://cmsadapter.asset.com/interfaces/webservice/types/", "CMSRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("username");
        elemField.setXmlName(new javax.xml.namespace.QName("", "username"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wfname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "wfname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("params");
        elemField.setXmlName(new javax.xml.namespace.QName("", "params"));
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
