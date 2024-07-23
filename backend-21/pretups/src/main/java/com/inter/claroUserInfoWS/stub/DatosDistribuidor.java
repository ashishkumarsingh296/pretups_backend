/**
 * DatosDistribuidor.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroUserInfoWS.stub;

public class DatosDistribuidor  implements java.io.Serializable {
    /* Nombre del distribuidor */
    private java.lang.String nombre;

    /* Direccion del distribuidor */
    private java.lang.String direccion;

    /* Telefono del distribuidor */
    private java.lang.String telefono;

    /* Email del distribuidor */
    private java.lang.String email;

    /* Ciudad */
    private java.lang.String ciudad;

    /* Estado/Provincia */
    private java.lang.String estado;

    /* Nombre del administrador */
    private java.lang.String adminName;

    /* Numero de identificacion fiscal */
    private java.lang.String nroIdentFiscal;

    /* Numero de dias para el pago */
    private java.lang.String nroDiasPago;

    /* Limite de credito */
    private java.lang.String limiteCredito;

    /* Ciclo de pago */
    private java.lang.String periodoCicloPago;

    /* Periodo de pago */
    private java.lang.String periodoPago;

    public DatosDistribuidor() {
    }

    public DatosDistribuidor(
           java.lang.String nombre,
           java.lang.String direccion,
           java.lang.String telefono,
           java.lang.String email,
           java.lang.String ciudad,
           java.lang.String estado,
           java.lang.String adminName,
           java.lang.String nroIdentFiscal,
           java.lang.String nroDiasPago,
           java.lang.String limiteCredito,
           java.lang.String periodoCicloPago,
           java.lang.String periodoPago) {
           this.nombre = nombre;
           this.direccion = direccion;
           this.telefono = telefono;
           this.email = email;
           this.ciudad = ciudad;
           this.estado = estado;
           this.adminName = adminName;
           this.nroIdentFiscal = nroIdentFiscal;
           this.nroDiasPago = nroDiasPago;
           this.limiteCredito = limiteCredito;
           this.periodoCicloPago = periodoCicloPago;
           this.periodoPago = periodoPago;
    }


    /**
     * Gets the nombre value for this DatosDistribuidor.
     * 
     * @return nombre   * Nombre del distribuidor
     */
    public java.lang.String getNombre() {
        return nombre;
    }


    /**
     * Sets the nombre value for this DatosDistribuidor.
     * 
     * @param nombre   * Nombre del distribuidor
     */
    public void setNombre(java.lang.String nombre) {
        this.nombre = nombre;
    }


    /**
     * Gets the direccion value for this DatosDistribuidor.
     * 
     * @return direccion   * Direccion del distribuidor
     */
    public java.lang.String getDireccion() {
        return direccion;
    }


    /**
     * Sets the direccion value for this DatosDistribuidor.
     * 
     * @param direccion   * Direccion del distribuidor
     */
    public void setDireccion(java.lang.String direccion) {
        this.direccion = direccion;
    }


    /**
     * Gets the telefono value for this DatosDistribuidor.
     * 
     * @return telefono   * Telefono del distribuidor
     */
    public java.lang.String getTelefono() {
        return telefono;
    }


    /**
     * Sets the telefono value for this DatosDistribuidor.
     * 
     * @param telefono   * Telefono del distribuidor
     */
    public void setTelefono(java.lang.String telefono) {
        this.telefono = telefono;
    }


    /**
     * Gets the email value for this DatosDistribuidor.
     * 
     * @return email   * Email del distribuidor
     */
    public java.lang.String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this DatosDistribuidor.
     * 
     * @param email   * Email del distribuidor
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }


    /**
     * Gets the ciudad value for this DatosDistribuidor.
     * 
     * @return ciudad   * Ciudad
     */
    public java.lang.String getCiudad() {
        return ciudad;
    }


    /**
     * Sets the ciudad value for this DatosDistribuidor.
     * 
     * @param ciudad   * Ciudad
     */
    public void setCiudad(java.lang.String ciudad) {
        this.ciudad = ciudad;
    }


    /**
     * Gets the estado value for this DatosDistribuidor.
     * 
     * @return estado   * Estado/Provincia
     */
    public java.lang.String getEstado() {
        return estado;
    }


    /**
     * Sets the estado value for this DatosDistribuidor.
     * 
     * @param estado   * Estado/Provincia
     */
    public void setEstado(java.lang.String estado) {
        this.estado = estado;
    }


    /**
     * Gets the adminName value for this DatosDistribuidor.
     * 
     * @return adminName   * Nombre del administrador
     */
    public java.lang.String getAdminName() {
        return adminName;
    }


    /**
     * Sets the adminName value for this DatosDistribuidor.
     * 
     * @param adminName   * Nombre del administrador
     */
    public void setAdminName(java.lang.String adminName) {
        this.adminName = adminName;
    }


    /**
     * Gets the nroIdentFiscal value for this DatosDistribuidor.
     * 
     * @return nroIdentFiscal   * Numero de identificacion fiscal
     */
    public java.lang.String getNroIdentFiscal() {
        return nroIdentFiscal;
    }


    /**
     * Sets the nroIdentFiscal value for this DatosDistribuidor.
     * 
     * @param nroIdentFiscal   * Numero de identificacion fiscal
     */
    public void setNroIdentFiscal(java.lang.String nroIdentFiscal) {
        this.nroIdentFiscal = nroIdentFiscal;
    }


    /**
     * Gets the nroDiasPago value for this DatosDistribuidor.
     * 
     * @return nroDiasPago   * Numero de dias para el pago
     */
    public java.lang.String getNroDiasPago() {
        return nroDiasPago;
    }


    /**
     * Sets the nroDiasPago value for this DatosDistribuidor.
     * 
     * @param nroDiasPago   * Numero de dias para el pago
     */
    public void setNroDiasPago(java.lang.String nroDiasPago) {
        this.nroDiasPago = nroDiasPago;
    }


    /**
     * Gets the limiteCredito value for this DatosDistribuidor.
     * 
     * @return limiteCredito   * Limite de credito
     */
    public java.lang.String getLimiteCredito() {
        return limiteCredito;
    }


    /**
     * Sets the limiteCredito value for this DatosDistribuidor.
     * 
     * @param limiteCredito   * Limite de credito
     */
    public void setLimiteCredito(java.lang.String limiteCredito) {
        this.limiteCredito = limiteCredito;
    }


    /**
     * Gets the periodoCicloPago value for this DatosDistribuidor.
     * 
     * @return periodoCicloPago   * Ciclo de pago
     */
    public java.lang.String getPeriodoCicloPago() {
        return periodoCicloPago;
    }


    /**
     * Sets the periodoCicloPago value for this DatosDistribuidor.
     * 
     * @param periodoCicloPago   * Ciclo de pago
     */
    public void setPeriodoCicloPago(java.lang.String periodoCicloPago) {
        this.periodoCicloPago = periodoCicloPago;
    }


    /**
     * Gets the periodoPago value for this DatosDistribuidor.
     * 
     * @return periodoPago   * Periodo de pago
     */
    public java.lang.String getPeriodoPago() {
        return periodoPago;
    }


    /**
     * Sets the periodoPago value for this DatosDistribuidor.
     * 
     * @param periodoPago   * Periodo de pago
     */
    public void setPeriodoPago(java.lang.String periodoPago) {
        this.periodoPago = periodoPago;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DatosDistribuidor)) return false;
        DatosDistribuidor other = (DatosDistribuidor) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.nombre==null && other.getNombre()==null) || 
             (this.nombre!=null &&
              this.nombre.equals(other.getNombre()))) &&
            ((this.direccion==null && other.getDireccion()==null) || 
             (this.direccion!=null &&
              this.direccion.equals(other.getDireccion()))) &&
            ((this.telefono==null && other.getTelefono()==null) || 
             (this.telefono!=null &&
              this.telefono.equals(other.getTelefono()))) &&
            ((this.email==null && other.getEmail()==null) || 
             (this.email!=null &&
              this.email.equals(other.getEmail()))) &&
            ((this.ciudad==null && other.getCiudad()==null) || 
             (this.ciudad!=null &&
              this.ciudad.equals(other.getCiudad()))) &&
            ((this.estado==null && other.getEstado()==null) || 
             (this.estado!=null &&
              this.estado.equals(other.getEstado()))) &&
            ((this.adminName==null && other.getAdminName()==null) || 
             (this.adminName!=null &&
              this.adminName.equals(other.getAdminName()))) &&
            ((this.nroIdentFiscal==null && other.getNroIdentFiscal()==null) || 
             (this.nroIdentFiscal!=null &&
              this.nroIdentFiscal.equals(other.getNroIdentFiscal()))) &&
            ((this.nroDiasPago==null && other.getNroDiasPago()==null) || 
             (this.nroDiasPago!=null &&
              this.nroDiasPago.equals(other.getNroDiasPago()))) &&
            ((this.limiteCredito==null && other.getLimiteCredito()==null) || 
             (this.limiteCredito!=null &&
              this.limiteCredito.equals(other.getLimiteCredito()))) &&
            ((this.periodoCicloPago==null && other.getPeriodoCicloPago()==null) || 
             (this.periodoCicloPago!=null &&
              this.periodoCicloPago.equals(other.getPeriodoCicloPago()))) &&
            ((this.periodoPago==null && other.getPeriodoPago()==null) || 
             (this.periodoPago!=null &&
              this.periodoPago.equals(other.getPeriodoPago())));
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
        if (getNombre() != null) {
            _hashCode += getNombre().hashCode();
        }
        if (getDireccion() != null) {
            _hashCode += getDireccion().hashCode();
        }
        if (getTelefono() != null) {
            _hashCode += getTelefono().hashCode();
        }
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        if (getCiudad() != null) {
            _hashCode += getCiudad().hashCode();
        }
        if (getEstado() != null) {
            _hashCode += getEstado().hashCode();
        }
        if (getAdminName() != null) {
            _hashCode += getAdminName().hashCode();
        }
        if (getNroIdentFiscal() != null) {
            _hashCode += getNroIdentFiscal().hashCode();
        }
        if (getNroDiasPago() != null) {
            _hashCode += getNroDiasPago().hashCode();
        }
        if (getLimiteCredito() != null) {
            _hashCode += getLimiteCredito().hashCode();
        }
        if (getPeriodoCicloPago() != null) {
            _hashCode += getPeriodoCicloPago().hashCode();
        }
        if (getPeriodoPago() != null) {
            _hashCode += getPeriodoPago().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DatosDistribuidor.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "datosDistribuidor"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombre");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "nombre"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("direccion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "direccion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("telefono");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "telefono"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ciudad");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "ciudad"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("estado");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "estado"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("adminName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "adminName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nroIdentFiscal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "nroIdentFiscal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nroDiasPago");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "nroDiasPago"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("limiteCredito");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "limiteCredito"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("periodoCicloPago");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "periodoCicloPago"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("periodoPago");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "periodoPago"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
