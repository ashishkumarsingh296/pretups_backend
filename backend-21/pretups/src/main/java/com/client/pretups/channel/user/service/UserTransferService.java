package com.client.pretups.channel.user.service;

import java.io.IOException;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;


/**
 * @author akanksha.gupta
 *
 * Interface which provides base for UserTransferServiceImpl class
 * also declares different method for User Transfer functionalities
 */

public interface UserTransferService {
	/**
	 * UserTransferService.java
	 * @return
	 * @throws BTSLBaseException
	 * @throws Exception
	 * List<ListValueVO>
	 * akanksha.gupta
	 * 01-Sep-2016 3:27:54 pm
	 */
	public List<ListValueVO> loadCategory() throws BTSLBaseException,IOException ;
	/**
	 * UserTransferService.java
	 * @return
	 * @throws BTSLBaseException
	 * @throws Exception
	 * List<ListValueVO>
	 * akanksha.gupta
	 * 01-Sep-2016 3:27:57 pm
	 */
	public List<ListValueVO> loadDomain() throws BTSLBaseException, IOException;
	/**
	 * UserTransferService.java
	 * @param p_domain
	 * @param p_category
	 * @param p_geography
	 * @param p_loggedInUserID
	 * @return
	 * @throws BTSLBaseException
	 * @throws Exception
	 * List<UserVO>
	 * akanksha.gupta
	 * 01-Sep-2016 3:27:59 pm
	 */
	public List<ListValueVO> loadUserData(String domain , String category , String geography,String user,String loggedInUserID) throws BTSLBaseException, IOException ;
	/**
	 * UserTransferService.java
	 * @param userID
	 * @param isUserID
	 * @param loggedinUserID
	 * @return
	 * @throws BTSLBaseException
	 * @throws Exception
	 * PretupsResponse<ChannelUserVO>
	 * akanksha.gupta
	 * 01-Sep-2016 3:28:03 pm
	 */
	public PretupsResponse<ChannelUserVO> confirmUserDetails(String userID,Boolean isUserCode,UserVO userVO) throws BTSLBaseException, IOException  ;
	/**
	 * UserTransferService.java
	 * @param listObject
	 * @param type
	 * @throws Exception
	 * void
	 * akanksha.gupta
	 * 01-Sep-2016 3:28:07 pm
	 */
	public <T> void processListValueVOValue(List<T> listObject, String type) ;
	/**
	 * UserTransferService.java
	 * @param p_category
	 * @param ownerId
	 * @return
	 * @throws BTSLBaseException
	 * @throws Exception
	 * List<UserVO>
	 * akanksha.gupta
	 * 01-Sep-2016 3:28:12 pm
	 */
	public List<ListValueVO> loadChannelUserData(String category , String ownerId,String user) throws BTSLBaseException, IOException;
	/**
	 * UserTransferService.java
	 * @param p_domain
	 * @param p_category
	 * @param p_geography
	 * @param p_loggedInUserID
	 * @return
	 * @throws BTSLBaseException
	 * @throws Exception
	 * List<ChannelUserTransferVO>
	 * akanksha.gupta
	 * 01-Sep-2016 3:28:16 pm
	 */
	public List<ChannelUserTransferVO> loadInitiatedUserTransfererList(String domain , String category , String geography,String loggedInUserID) throws BTSLBaseException, IOException ;
	/**
	 * UserTransferService.java
	 * @param userID
	 * @param loggedinUserID
	 * @param otp
	 * @param _categoryCode
	 * @return
	 * @throws BTSLBaseException
	 * @throws Exception
	 * PretupsResponse<Object>
	 * akanksha.gupta
	 * 01-Sep-2016 3:28:19 pm
	 */
	public PretupsResponse<Object> confirmUserTransfer(String userID,String loggedinUserID,String otp,String categoryCode) throws BTSLBaseException,IOException  ;
	/**
	 * UserTransferService.java
	 * @param p_msisdn
	 * @param p_loggedInUserID
	 * @return
	 * @throws BTSLBaseException
	 * @throws Exception
	 * List<ChannelUserTransferVO>
	 * akanksha.gupta
	 * 01-Sep-2016 3:28:23 pm
	 */
	public List<ChannelUserTransferVO> loadInitiatedUserTransferDetailMsisdn(String msisdn,String loggedInUserID) throws BTSLBaseException, IOException;
}
