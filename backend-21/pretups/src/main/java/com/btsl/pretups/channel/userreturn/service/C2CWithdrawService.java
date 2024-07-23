package com.btsl.pretups.channel.userreturn.service;

import java.io.IOException;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.userreturn.web.C2CWithdrawVO;
/*
 *  * Interface which provides base for C2CWithdrawServiceImpl class
 *   * also declares different method for C2CWithdrawService via Channel Admin functionalities
 *    */
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface C2CWithdrawService {

		/*
		 * Method loadCategory to load the receiver's category
		 * @param domain
		 * @param networkId
		 * @throws BTSLBaseException,Exception
		 * @returns ArrayList
		 */
        public List loadCategory(String domain,String networkId) throws Exception ;    //load category
        
        /*
		 * Method loadDomain to load the domain
		 * @throws BTSLBaseException,Exception
		 * @returns List<ListValueVO>
		 */
        public List<ListValueVO> loadDomain() throws Exception;       //load domain
		
        /*
		 * Method loadUserData to load the user data
		 * @param domain
		 * @param category
		 * @param geography
		 * @param user
		 * @throws BTSLBaseException,Exception
		 * @returns List<UserVO>
		 */
        public List<ListValueVO> loadUserData(String domain , String category , String geography,String user) throws BTSLBaseException, IOException;  //load owner user
		
        /*
		 * Method loadChannelUserData to load the receiver channel user
		 * @param category
		 * @param ownerId
		 * @param user
		 * @throws BTSLBaseException,Exception
		 * @returns List<UserVO>
		 */
        public List<UserVO> loadChannelUserData(String category , String ownerId,String user) throws BTSLBaseException, IOException; //load channel user
		
        /*
		 * Method loadUserDetails to load the user details
		 * @param userID
		 * @param isUserId
		 * @throws BTSLBaseException,Exception
		 * @returns ChannelUserVO
		 */
        public ChannelUserVO loadUserDetails(C2CWithdrawVO withdrawVO,Boolean isUserId) throws BTSLBaseException, IOException ;   //load channel user details
		
        /*
		 * Method loadCatListByTrfRule to load the sendor category on basis of transfer rule
		 * @param cat_code
		 * @param net_code
		 * @throws BTSLBaseException,Exception
		 * @returns List<ChannelTransferRuleVO>
		 */
        public List loadCatListByTrfRule(String domain,int seqNo) throws BTSLBaseException, IOException; //load category of sender by transfer rule
		
        /*
		 * Method loadUserListSender to load the sender user list
		 * @param to_cat
		 * @param from_cat
		 * @param domain
		 * @param networkId
		 * @param userID
		 * @param userVO
		 * @param user
		 * @throws BTSLBaseException,Exception
		 * @returns ArrayList
		 */
        public List<ListValueVO> loadUserListSender(String toCat,String fromCat,String domain,String networkId,String userID, UserVO userVO,String user) throws BTSLBaseException, JsonParseException, JsonMappingException, IOException;
		
        /*
		 * Method validateUser to validate the sender user
		 * @param from_cat
		 * @param domain
		 * @param networkId
		 * @param from_userID
		 * @param to_userID
		 * @param isUserId
		 * @throws IOException
		 * @returns PretupsResponse<Map<String,List>>
		 */
        public PretupsResponse<C2CWithdrawVO> validateUser(C2CWithdrawVO c2cWithdrawVO,UserVO userVO,Boolean isUserId) throws IOException ;
		
        /*
		 * Method confirmWithdraw to calculate tax and other things
		 * @param amount
		 * @param networkId
		 * @param from_userID
		 * @param to_userID
		 * @param isUserId
		 * @throws BTSLBaseException,Exception
		 * @returns PretupsResponse<Map<String,String>>
		 */
        public PretupsResponse<ChannelTransferItemsVO> confirmWithdraw(C2CWithdrawVO withdrawVO,UserVO userVO) throws IOException;
		
        /*
		 * Method withdraw to perform C2CWithdraw
		 * @param map
		 * @param networkId
		 * @param from_userID
		 * @param to_userID
		 * @param isUserId
		 * @throws BTSLBaseException,Exception
		 * @returns ChannelTransferVO
		 */
        public ChannelTransferVO withdraw(C2CWithdrawVO withdrawVO,UserVO userVO,ChannelTransferItemsVO itemsVO) throws Exception;
		
}