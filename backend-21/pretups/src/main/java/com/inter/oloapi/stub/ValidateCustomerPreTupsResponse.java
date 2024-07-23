/**
 * ValidateCustomerPreTupsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.oloapi.stub;

public class ValidateCustomerPreTupsResponse  implements java.io.Serializable {
    private java.lang.String status;

    private java.lang.String name;

    private java.lang.String type;

    private java.lang.String num_plans;

    private java.lang.String mobiquityTransactionId;

    private com.inter.oloapi.stub.ValidateCustomerPlan[] plans;

    public ValidateCustomerPreTupsResponse() {
    }

    public ValidateCustomerPreTupsResponse(
           java.lang.String status,
           java.lang.String name,
           java.lang.String type,
           java.lang.String num_plans,
           java.lang.String mobiquityTransactionId,
           com.inter.oloapi.stub.ValidateCustomerPlan[] plans) {
           this.status = status;
           this.name = name;
           this.type = type;
           this.num_plans = num_plans;
           this.mobiquityTransactionId = mobiquityTransactionId;
           this.plans = plans;
    }


    /**
     * Gets the status value for this ValidateCustomerPreTupsResponse.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this ValidateCustomerPreTupsResponse.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the name value for this ValidateCustomerPreTupsResponse.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this ValidateCustomerPreTupsResponse.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the type value for this ValidateCustomerPreTupsResponse.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this ValidateCustomerPreTupsResponse.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }


    /**
     * Gets the num_plans value for this ValidateCustomerPreTupsResponse.
     * 
     * @return num_plans
     */
    public java.lang.String getNum_plans() {
        return num_plans;
    }


    /**
     * Sets the num_plans value for this ValidateCustomerPreTupsResponse.
     * 
     * @param num_plans
     */
    public void setNum_plans(java.lang.String num_plans) {
        this.num_plans = num_plans;
    }


    /**
     * Gets the mobiquityTransactionId value for this ValidateCustomerPreTupsResponse.
     * 
     * @return mobiquityTransactionId
     */
    public java.lang.String getMobiquityTransactionId() {
        return mobiquityTransactionId;
    }


    /**
     * Sets the mobiquityTransactionId value for this ValidateCustomerPreTupsResponse.
     * 
     * @param mobiquityTransactionId
     */
    public void setMobiquityTransactionId(java.lang.String mobiquityTransactionId) {
        this.mobiquityTransactionId = mobiquityTransactionId;
    }


    /**
     * Gets the plans value for this ValidateCustomerPreTupsResponse.
     * 
     * @return plans
     */
    public com.inter.oloapi.stub.ValidateCustomerPlan[] getPlans() {
        return plans;
    }


    /**
     * Sets the plans value for this ValidateCustomerPreTupsResponse.
     * 
     * @param plans
     */
    public void setPlans(com.inter.oloapi.stub.ValidateCustomerPlan[] plans) {
        this.plans = plans;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ValidateCustomerPreTupsResponse)) return false;
        ValidateCustomerPreTupsResponse other = (ValidateCustomerPreTupsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.num_plans==null && other.getNum_plans()==null) || 
             (this.num_plans!=null &&
              this.num_plans.equals(other.getNum_plans()))) &&
            ((this.mobiquityTransactionId==null && other.getMobiquityTransactionId()==null) || 
             (this.mobiquityTransactionId!=null &&
              this.mobiquityTransactionId.equals(other.getMobiquityTransactionId()))) &&
            ((this.plans==null && other.getPlans()==null) || 
             (this.plans!=null &&
              java.util.Arrays.equals(this.plans, other.getPlans())));
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
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getNum_plans() != null) {
            _hashCode += getNum_plans().hashCode();
        }
        if (getMobiquityTransactionId() != null) {
            _hashCode += getMobiquityTransactionId().hashCode();
        }
        if (getPlans() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPlans());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPlans(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ValidateCustomerPreTupsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:prepay", "ValidateCustomerPreTupsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("num_plans");
        elemField.setXmlName(new javax.xml.namespace.QName("", "num_plans"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mobiquityTransactionId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mobiquityTransactionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("plans");
        elemField.setXmlName(new javax.xml.namespace.QName("", "plans"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:prepay", "ValidateCustomerPlan"));
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
