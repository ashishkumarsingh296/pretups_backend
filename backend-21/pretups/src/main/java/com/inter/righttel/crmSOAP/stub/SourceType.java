/**
 * SourceType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.righttel.crmSOAP.stub;

public class SourceType  implements java.io.Serializable {
    private java.lang.String distributorId;

    private java.lang.String subdistributorId;

    private java.lang.String posId;

    public SourceType() {
    }

    public SourceType(
           java.lang.String distributorId,
           java.lang.String subdistributorId,
           java.lang.String posId) {
           this.distributorId = distributorId;
           this.subdistributorId = subdistributorId;
           this.posId = posId;
    }


    /**
     * Gets the distributorId value for this SourceType.
     * 
     * @return distributorId
     */
    public java.lang.String getDistributorId() {
        return distributorId;
    }


    /**
     * Sets the distributorId value for this SourceType.
     * 
     * @param distributorId
     */
    public void setDistributorId(java.lang.String distributorId) {
        this.distributorId = distributorId;
    }


    /**
     * Gets the subdistributorId value for this SourceType.
     * 
     * @return subdistributorId
     */
    public java.lang.String getSubdistributorId() {
        return subdistributorId;
    }


    /**
     * Sets the subdistributorId value for this SourceType.
     * 
     * @param subdistributorId
     */
    public void setSubdistributorId(java.lang.String subdistributorId) {
        this.subdistributorId = subdistributorId;
    }


    /**
     * Gets the posId value for this SourceType.
     * 
     * @return posId
     */
    public java.lang.String getPosId() {
        return posId;
    }


    /**
     * Sets the posId value for this SourceType.
     * 
     * @param posId
     */
    public void setPosId(java.lang.String posId) {
        this.posId = posId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SourceType)) return false;
        SourceType other = (SourceType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.distributorId==null && other.getDistributorId()==null) || 
             (this.distributorId!=null &&
              this.distributorId.equals(other.getDistributorId()))) &&
            ((this.subdistributorId==null && other.getSubdistributorId()==null) || 
             (this.subdistributorId!=null &&
              this.subdistributorId.equals(other.getSubdistributorId()))) &&
            ((this.posId==null && other.getPosId()==null) || 
             (this.posId!=null &&
              this.posId.equals(other.getPosId())));
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
        if (getDistributorId() != null) {
            _hashCode += getDistributorId().hashCode();
        }
        if (getSubdistributorId() != null) {
            _hashCode += getSubdistributorId().hashCode();
        }
        if (getPosId() != null) {
            _hashCode += getPosId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SourceType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "SourceType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("distributorId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "distributorId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subdistributorId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subdistributorId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("posId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "posId"));
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
