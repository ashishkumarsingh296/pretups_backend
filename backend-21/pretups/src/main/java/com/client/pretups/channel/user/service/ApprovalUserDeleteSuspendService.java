package com.client.pretups.channel.user.service;


import java.io.InputStream;
import java.util.List;

import org.springframework.ui.ModelMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.user.businesslogic.UserVO;

/**
 *   Interface which provides base for ApprovalUserDeleteSuspendServiceImpl class
 * also declares different method for Approval of Suspend/Delete User Batch functionalities
 * 
 * @author mohd.suhel1
 * @since 17/10/2016
 */
public interface ApprovalUserDeleteSuspendService {

	/**load Status Type for Approval Status for Suspend/Delete
	 * @return
	 * @throws BTSLBaseException
	 */
	public List<ListValueVO> loadStatusTypeList() throws BTSLBaseException;
	
	/**Downlaod User list for Approval Request
	 * @param pUserStatus
	 * @return
	 * @throws BTSLBaseException
	 */
	public InputStream downloadUserList(String pUserStatus , ModelMap modelMap) throws BTSLBaseException;
	
	/**do the approval for those who are in approval state
	 * @param filePath
	 * @param fileName
	 * @param userVO
	 * @param modelMap
	 * @throws BTSLBaseException
	 */
	public void approveSuspendDeleteUserBatch(String filePath , String fileName , UserVO userVO , ModelMap modelMap) throws BTSLBaseException;

	
}
