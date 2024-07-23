/**
 * AutogestionWsSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.enquiry;

public interface AutogestionWsSoap_PortType extends java.rmi.Remote {
    public com.inter.claroca.dth.enquiry.ConFacturaHistorialFijoResponseConFacturaHistorialFijoResult conFacturaHistorialFijo(java.lang.String cod_pais, java.lang.String num_browse, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.enquiry.ConSaldoPostpagoFijaResponseConSaldoPostpagoFijaResult conSaldoPostpagoFija(java.lang.String cod_pais, java.lang.String num_browse, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.enquiry.ConSinPagarFacturasResponseConSinPagarFacturasResult conSinPagarFacturas(java.lang.String cod_pais, java.lang.String num_browse, java.lang.String contrato, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.enquiry.ConPlanesContratadosResponseConPlanesContratadosResult conPlanesContratados(java.lang.String cod_pais, java.lang.String num_browse, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.enquiry.ConUltimos3PagosResponseConUltimos3PagosResult conUltimos3Pagos(java.lang.String cod_pais, java.lang.String num_browse, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.enquiry.ConClienteResponseConClienteResult conCliente(java.lang.String cod_pais, java.lang.String num_browse, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.enquiry.ConServiciosPaquetesResponseConServiciosPaquetesResult conServiciosPaquetes(java.lang.String cod_pais, java.lang.String num_browse, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.enquiry.ProPagoPostpagoFijaResponseProPagoPostpagoFijaResult proPagoPostpagoFija(java.lang.String cod_pais, java.lang.String cod_banco, java.lang.String num_browse, java.lang.String contrato, java.lang.String factura, java.lang.String monto, java.lang.String sec_banco, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.enquiry.ValidaTelefonoResponseValidaTelefonoResult validaTelefono(java.lang.String cod_pais, java.lang.String cod_banco, java.lang.String num_browse, java.lang.String monto, java.lang.String sec_banco, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.enquiry.AplicaRecargaResponseAplicaRecargaResult aplicaRecarga(java.lang.String cod_pais, java.lang.String cod_banco, java.lang.String num_browse, java.lang.String monto, java.lang.String sec_banco, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.enquiry.AplicaVentaServicioResponseAplicaVentaServicioResult aplicaVentaServicio(java.lang.String cod_pais, java.lang.String cod_banco, java.lang.String num_browse, java.lang.String monto, java.lang.String sec_banco, java.lang.String tipo_producto, java.lang.String codProductoSKU) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.enquiry.GetTasaCambioResponseGetTasaCambioResult getTasaCambio(java.lang.String cod_pais, java.lang.String cod_banco, java.lang.String tipo_producto) throws java.rmi.RemoteException;
}
