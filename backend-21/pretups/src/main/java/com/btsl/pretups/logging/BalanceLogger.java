package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.transfer.businesslogic.TransferVO;

/*
 * Created on Mar 9, 2005
 * 
 * TODO To change the template f/or this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author abhijit.chauhan
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class BalanceLogger {

    private static Log _log = LogFactory.getLog(BalanceLogger.class.getName());

    private BalanceLogger() {
		// TODO Auto-generated constructor stub
	}
    // private static Log _log =
    // LogFactory.getFactory().getInstance(BalanceLogger.class.getName());
    public static void log(TransferVO p_transferVO) {
        final String METHOD_NAME = "log";
        try {
            /*
             * ArrayList itemList=p_transferVO.getItemList();
             * ItemVO itemVO=null;
             * for(int i=0;i<itemList.size();i++)
             * {
             * itemVO=(ItemVO)itemList.get(i);
             * _log.info(p_transferVO.getFromUser().getUserID()+"::"+itemVO.
             * getProductID
             * ()+"::"+itemVO.getValue()+"::"+itemVO.getFromNewBalance
             * ()+"::"+itemVO.getFromPrevBalance());
             * _log.info(p_transferVO.getToUser().getUserID()+"::"+itemVO.
             * getProductID
             * ()+"::"+itemVO.getValue()+"::"+itemVO.getToNewBalance(
             * )+"::"+itemVO.getToPrevBalance());
             * }
             */
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
    }
}
