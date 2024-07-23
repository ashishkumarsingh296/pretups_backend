/**
 * AutogestionWsSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.payment;

public interface AutogestionWsSoap_PortType extends java.rmi.Remote {
    public com.inter.claroca.dth.payment.ConFacturaHistorialFijoResponseConFacturaHistorialFijoResult conFacturaHistorialFijo(java.lang.String cod_pais, java.lang.String num_browse, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.payment.ConSaldoPostpagoFijaResponseConSaldoPostpagoFijaResult conSaldoPostpagoFija(java.lang.String cod_pais, java.lang.String num_browse, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.payment.ConSinPagarFacturasResponseConSinPagarFacturasResult conSinPagarFacturas(java.lang.String cod_pais, java.lang.String num_browse, java.lang.String contrato, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.payment.ConSinPagarFacturas_orgaResponseConSinPagarFacturas_orgaResult conSinPagarFacturas_orga(java.lang.String cod_pais, java.lang.String num_browse, java.lang.String contrato, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.payment.ConPlanesContratadosResponseConPlanesContratadosResult conPlanesContratados(java.lang.String cod_pais, java.lang.String num_browse, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.payment.ConUltimos3PagosResponseConUltimos3PagosResult conUltimos3Pagos(java.lang.String cod_pais, java.lang.String num_browse, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.payment.ConClienteResponseConClienteResult conCliente(java.lang.String cod_pais, java.lang.String num_browse, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.payment.ConServiciosPaquetesResponseConServiciosPaquetesResult conServiciosPaquetes(java.lang.String cod_pais, java.lang.String num_browse, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.payment.ProPagoPostpagoFijaResponseProPagoPostpagoFijaResult proPagoPostpagoFija(java.lang.String cod_pais, java.lang.String cod_banco, java.lang.String num_browse, java.lang.String contrato, java.lang.String factura, java.lang.String monto, java.lang.String sec_banco, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.payment.ProPagoPostpagoFija_orgaResponseProPagoPostpagoFija_orgaResult proPagoPostpagoFija_orga(java.lang.String cod_pais, java.lang.String cod_banco, java.lang.String num_browse, java.lang.String contrato, java.lang.String factura, java.lang.String monto, java.lang.String sec_banco, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.payment.ValidaTelefonoResponseValidaTelefonoResult validaTelefono(java.lang.String cod_pais, java.lang.String cod_banco, java.lang.String num_browse, java.lang.String monto, java.lang.String sec_banco, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.payment.AplicaRecargaResponseAplicaRecargaResult aplicaRecarga(java.lang.String cod_pais, java.lang.String cod_banco, java.lang.String num_browse, java.lang.String monto, java.lang.String sec_banco, java.lang.String tipo_producto) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.payment.AplicaVentaServicioResponseAplicaVentaServicioResult aplicaVentaServicio(java.lang.String cod_pais, java.lang.String cod_banco, java.lang.String num_browse, java.lang.String monto, java.lang.String sec_banco, java.lang.String tipo_producto, java.lang.String codProductoSKU) throws java.rmi.RemoteException;
    public com.inter.claroca.dth.payment.GetTasaCambioResponseGetTasaCambioResult getTasaCambio(java.lang.String cod_pais, java.lang.String cod_banco, java.lang.String tipo_producto) throws java.rmi.RemoteException;
}
