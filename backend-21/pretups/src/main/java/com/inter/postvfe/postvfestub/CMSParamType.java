/**
 * CMSParamType.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC1 Sep 29, 2004 (08:29:40 EDT) WSDL2Java emitter.
 */

package com.inter.postvfe.postvfestub;

public class CMSParamType implements java.io.Serializable {
    private java.lang.String name;
    private java.lang.String type;
    private java.lang.String simpleValue;
    private com.inter.postvfe.postvfestub.CMSListType[] listValues;

    public CMSParamType() {
    }

    public CMSParamType(java.lang.String name, java.lang.String type, java.lang.String simpleValue, com.inter.postvfe.postvfestub.CMSListType[] listValues) {
        this.name = name;
        this.type = type;
        this.simpleValue = simpleValue;
        this.listValues = listValues;
    }

    /**
     * Gets the name value for this CMSParamType.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }

    /**
     * Sets the name value for this CMSParamType.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    /**
     * Gets the type value for this CMSParamType.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }

    /**
     * Sets the type value for this CMSParamType.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }

    /**
     * Gets the simpleValue value for this CMSParamType.
     * 
     * @return simpleValue
     */
    public java.lang.String getSimpleValue() {
        return simpleValue;
    }

    /**
     * Sets the simpleValue value for this CMSParamType.
     * 
     * @param simpleValue
     */
    public void setSimpleValue(java.lang.String simpleValue) {
        this.simpleValue = simpleValue;
    }

    /**
     * Gets the listValues value for this CMSParamType.
     * 
     * @return listValues
     */
    public com.inter.postvfe.postvfestub.CMSListType[] getListValues() {
        return listValues;
    }

    /**
     * Sets the listValues value for this CMSParamType.
     * 
     * @param listValues
     */
    public void setListValues(com.inter.postvfe.postvfestub.CMSListType[] listValues) {
        this.listValues = listValues;
    }

    public com.inter.postvfe.postvfestub.CMSListType getListValues(int i) {
        return this.listValues[i];
    }

    public void setListValues(int i, com.inter.postvfe.postvfestub.CMSListType _value) {
        this.listValues[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CMSParamType))
            return false;
        CMSParamType other = (CMSParamType) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && ((this.name == null && other.getName() == null) || (this.name != null && this.name.equals(other.getName()))) && ((this.type == null && other.getType() == null) || (this.type != null && this.type.equals(other.getType()))) && ((this.simpleValue == null && other.getSimpleValue() == null) || (this.simpleValue != null && this.simpleValue.equals(other.getSimpleValue()))) && ((this.listValues == null && other.getListValues() == null) || (this.listValues != null && java.util.Arrays.equals(this.listValues, other.getListValues())));
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getSimpleValue() != null) {
            _hashCode += getSimpleValue().hashCode();
        }
        if (getListValues() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getListValues()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getListValues(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(CMSParamType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://cmsadapter.asset.com/interfaces/webservice/types/", "CMSParamType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("simpleValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "simpleValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listValues");
        elemField.setXmlName(new javax.xml.namespace.QName("", "listValues"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cmsadapter.asset.com/interfaces/webservice/types/", "CMSListType"));
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
