/*
 * Created on June 18, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.simactivationth;

/**
 * @author abhay.singh
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public interface SIMActivateI {
    public int ACTION_VALIDATE = 1;
    public int ACTION_SIM_ACTIVATE = 2;
    public String HTTP_STATUS_SUCCESS = "200";
    public String HTTP_STATUS_FAIL = "206";
    public String SUCCESS_MSG = "Tu solicitud de activacion de la SimCard ha sido enviada exitosamente.";
    public String FAILURE_MSG = "Error en la peticion de activacion de simcard.";

}
