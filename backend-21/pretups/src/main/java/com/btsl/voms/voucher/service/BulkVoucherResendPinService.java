package com.btsl.voms.voucher.service;



import org.springframework.ui.ModelMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.UserVO;




/**
 *   Interface which provides base for BuckVoucherResendPin class
 * also declares different method for Bulk Voucher Pin Resend functionalities
 * 
 * @author hargovind karki
 * @since 12/01/2017
 */


public interface BulkVoucherResendPinService {
	
	
	/**do the approval for those who are in approval state
	 * @param filePath
	 * @param fileName
	 * @param userVO
	 * @param modelMap
	 * @throws BTSLBaseException
	 */
	public void bulkVoucherPinResendProcess(String filePath , String fileName , ModelMap modelMap,UserVO userVO) throws BTSLBaseException;

}
