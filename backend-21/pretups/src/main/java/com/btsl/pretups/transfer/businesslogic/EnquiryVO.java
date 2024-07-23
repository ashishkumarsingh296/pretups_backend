package com.btsl.pretups.transfer.businesslogic;

import java.io.Serializable;

public class EnquiryVO implements Serializable {
   
	private static final long serialVersionUID = 1L;
	private String xDescripServ;
    private String _xTipoServicio;
    private String _xMontoDebe;
    private String _xMontoFact;
    private String _xImportePagoMin;
    private String _xFechaEmision;
    private String _xFechaVenc;
    private String _xNumeroDoc;

    /**
     * @return
     */
    public String getxDescripServ() {
        return xDescripServ;
    }

    /**
     * @param descripServ
     */
    public void setxDescripServ(String descripServ) {
        xDescripServ = descripServ;
    }

    /**
     * @return
     */
    public String getxTipoServicio() {
        return _xTipoServicio;
    }

    public void setxTipoServicio(String tipoServicio) {
        _xTipoServicio = tipoServicio;
    }

    public String getxMontoDebe() {
        return _xMontoDebe;
    }

    /**
     * @param montoDebe
     */
    public void setxMontoDebe(String montoDebe) {
        _xMontoDebe = montoDebe;
    }

    /**
     * @return
     */
    public String getxMontoFact() {
        return _xMontoFact;
    }

    /**
     * @param montoFact
     */
    public void setxMontoFact(String montoFact) {
        _xMontoFact = montoFact;
    }

    /**
     * @return
     */
    public String getxImportePagoMin() {
        return _xImportePagoMin;
    }

    public void setxImportePagoMin(String importePagoMin) {
        _xImportePagoMin = importePagoMin;
    }

    /**
     * Gets the xFechaEmision value for this DeudaDocumentoType.
     * 
     * @return xFechaEmision
     */
    public String getxFechaEmision() {
        return _xFechaEmision;
    }

    /**
     * Sets the xFechaEmision value for this DeudaDocumentoType.
     * 
     * @param xFechaEmision
     */
    public void setxFechaEmision(String xFechaEmision) {
        _xFechaEmision = xFechaEmision;
    }

    /**
     * Sets the xFechaVenc value for this DeudaDocumentoType.
     * 
     * @param xFechaVenc
     */
    public void setxFechaVenc(String xFechaVenc) {
        _xFechaVenc = xFechaVenc;
    }

    /**
     * Gets the xFechaVenc value for this DeudaDocumentoType.
     * 
     * @return xFechaVenc
     */
    public String getxFechaVenc() {
        return _xFechaVenc;
    }

    /**
     * Gets the xNumeroDoc value for this DeudaDocumentoType.
     * 
     * @return xNumeroDoc
     */
    public java.lang.String getxNumeroDoc() {
        return _xNumeroDoc;
    }

    /**
     * Sets the xNumeroDoc value for this DeudaDocumentoType.
     * 
     * @param xNumeroDoc
     */
    public void setxNumeroDoc(java.lang.String xNumeroDoc) {
        _xNumeroDoc = xNumeroDoc;
    }

}
