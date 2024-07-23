/*
 * Created on Aug 28, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.voms.util;

import com.btsl.voms.voucher.businesslogic.VomsBatchVO;

/**
 * @author vikas.yadav
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public interface VomsUtilI {

    /**
     * Method to format the Voms Batch ID
     * 
     * @param p_vomsBatchVO
     * @param p_batchNumber
     * @return String
     */
    public String formatVomsBatchID(VomsBatchVO p_vomsBatchVO, String p_batchNumber);

}
