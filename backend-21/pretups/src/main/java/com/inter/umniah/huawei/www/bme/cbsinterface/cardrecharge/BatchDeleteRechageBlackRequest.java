/**
 * BatchDeleteRechageBlackRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge;

public class BatchDeleteRechageBlackRequest  implements java.io.Serializable {
    private java.lang.String fileName;

    private java.lang.Integer operationType;

    private java.lang.String taskTime;

    public BatchDeleteRechageBlackRequest() {
    }

    public BatchDeleteRechageBlackRequest(
           java.lang.String fileName,
           java.lang.Integer operationType,
           java.lang.String taskTime) {
           this.fileName = fileName;
           this.operationType = operationType;
           this.taskTime = taskTime;
    }


    /**
     * Gets the fileName value for this BatchDeleteRechageBlackRequest.
     * 
     * @return fileName
     */
    public java.lang.String getFileName() {
        return fileName;
    }


    /**
     * Sets the fileName value for this BatchDeleteRechageBlackRequest.
     * 
     * @param fileName
     */
    public void setFileName(java.lang.String fileName) {
        this.fileName = fileName;
    }


    /**
     * Gets the operationType value for this BatchDeleteRechageBlackRequest.
     * 
     * @return operationType
     */
    public java.lang.Integer getOperationType() {
        return operationType;
    }


    /**
     * Sets the operationType value for this BatchDeleteRechageBlackRequest.
     * 
     * @param operationType
     */
    public void setOperationType(java.lang.Integer operationType) {
        this.operationType = operationType;
    }


    /**
     * Gets the taskTime value for this BatchDeleteRechageBlackRequest.
     * 
     * @return taskTime
     */
    public java.lang.String getTaskTime() {
        return taskTime;
    }


    /**
     * Sets the taskTime value for this BatchDeleteRechageBlackRequest.
     * 
     * @param taskTime
     */
    public void setTaskTime(java.lang.String taskTime) {
        this.taskTime = taskTime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BatchDeleteRechageBlackRequest)) return false;
        BatchDeleteRechageBlackRequest other = (BatchDeleteRechageBlackRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.fileName==null && other.getFileName()==null) || 
             (this.fileName!=null &&
              this.fileName.equals(other.getFileName()))) &&
            ((this.operationType==null && other.getOperationType()==null) || 
             (this.operationType!=null &&
              this.operationType.equals(other.getOperationType()))) &&
            ((this.taskTime==null && other.getTaskTime()==null) || 
             (this.taskTime!=null &&
              this.taskTime.equals(other.getTaskTime())));
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
        if (getFileName() != null) {
            _hashCode += getFileName().hashCode();
        }
        if (getOperationType() != null) {
            _hashCode += getOperationType().hashCode();
        }
        if (getTaskTime() != null) {
            _hashCode += getTaskTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BatchDeleteRechageBlackRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "BatchDeleteRechageBlackRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fileName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "FileName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("operationType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "OperationType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("taskTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "TaskTime"));
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
