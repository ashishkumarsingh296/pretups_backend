/*
 * Created on Jul 2, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.inter.module;

import com.btsl.pretups.iat.transfer.businesslogic.IATInterfaceVO;

public interface IATInterfaceHandlerI {

    public void validate(IATInterfaceVO p_IATVO); // throws
                                                  // BTSLBaseException,Exception;

    public void credit(IATInterfaceVO p_IATVO); // throws
                                                // BTSLBaseException,Exception;

    public void Login(IATInterfaceVO p_IATVO); // throws
                                               // BTSLBaseException,Exception;

    public void Logout(IATInterfaceVO p_IATVO); // throws
                                                // BTSLBaseException,Exception;

    public void checkTxnStatus(IATInterfaceVO p_IATVO, String reqSource, int retryCount, long sleepTime); // throws
                                                                                                          // BTSLBaseException,Exception;

}