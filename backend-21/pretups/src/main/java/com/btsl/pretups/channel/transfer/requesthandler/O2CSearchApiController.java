package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${O2CSearchApiController.name}", description = "${O2CSearchApiController.desc}")//@Api(tags="O2C Services")
@RestController
@RequestMapping(value = "/v1/o2c")
public class O2CSearchApiController {
	public static final Log log = LogFactory.getLog(O2CSearchApiController.class.getName());

	
	/**
	 * @(#)O2CSearchApiController.java This method gets the channel users
	 *                                            list
	 * 
	 * @param networkCode
	 * @param identifierType
	 * @param identifierValue
	 * @param idType
	 * @param id
	 * @param msisdn
	 * @param loginId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	@Context
    private HttpServletRequest httpServletRequest;
	@PostMapping(value="/getSearchDetails", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation( value = "SearchDetails",response = SearchChannelUserVOResponseVO.class,notes = ("Api Info:") + ("\n") + ("If the user selects LOGINID,MSISDN then there is no need to enter search details otherwise needs to fill.") + ("\n") ,
			 
			authorizations = {
    	            @Authorization(value = "Authorization")})
	        @ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK",response = SearchChannelUserVOResponseVO.class),
			@ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${getSearchDetails.summary}", description="${getSearchDetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SearchChannelUserVOResponseVO.class))
							)
					}

					),


					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))

									 , examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.BAD_REQUEST)}

							)
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.UNAUTH)}

									 )
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.NOT_FOUND)}

							)
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)}
					)})
			}
	)




	public SearchChannelUserVOResponseVO getSearchUserDetails(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_TYPE, required = true)// allowableValues = "LOGINID,MSISDN,USERNAME")
			@RequestParam("identifierType") String identifiertype,
			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_VALUE, example ="", required = true)
			@DefaultValue("superdistributer")@RequestParam("identifierValue") String identifiervalue,
			@RequestBody SearchUserRequestVO searchUserRequestVO)
			throws IOException, SQLException, BTSLBaseException {
		final String methodName = "getSearchUserDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		SearchChannelUserVOResponseVO response = null;
		 UserDAO userDao=null;
		 Date curDate = new Date();
		 int receiverStatusAllowed = 0;
		try {
			response = new SearchChannelUserVOResponseVO();
			/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
			searchUserRequestVO.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(searchUserRequestVO,headers);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();	
			UserVO userVO=new UserVO();
			userDao = new UserDAO();
			userVO=userDao.loadUsersDetails(con, searchUserRequestVO.getData().getMsisdn());
			ArrayList domList = userVO.getDomainList();
			if ((domList == null || domList.isEmpty()) && PretupsI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO
					.getCategoryVO().getFixedDomains())) {
				domList = new DomainDAO().loadCategoryDomainList(con);
			}
			domList = BTSLUtil.displayDomainList(domList);
			ArrayList domList1 = new ArrayList<>();
			String channelDomain = null;
			if (domList.size() == 1) {
				ListValueVO listValueVO = null;
				listValueVO = (ListValueVO) domList.get(0);
				userVO.setDomainID(listValueVO.getValue());
				channelDomain = listValueVO.getValue();
				searchUserRequestVO.getDataF().setChannelDomain(channelDomain);
			} else {
				domList1=domList;
			}
			if(identifiertype.equalsIgnoreCase("USERNAME"))
			{
				/*
				 * if user directly enter the user name, then first check that
				 * owner search is required or not.
				 * if required then first owner user then after it search the
				 * user.
				 * if more than one user is there then display the error
				 * message.
				 */
				ListValueVO listValueVO = null;
				String userName = null;
				CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
				// check the owner information if exist i.e. selectd category is
				// not owner's category
				final CategoryVO categoryVO = categoryWebDAO.loadOwnerCategory(con, searchUserRequestVO.getDataF().getChannelDomain());
				String catg = null;
				if (searchUserRequestVO.getDataF().getUserCategory() != null) {
					catg = searchUserRequestVO.getDataF().getUserCategory();
				}
				String channelOwnerCategory = null;
				if (categoryVO != null) {
					channelOwnerCategory=categoryVO.getCategoryCode();
				}

				/**
				 * Note: if selected category is himself owner of channel then only
				 * one search will be appear on the
				 * screen this will control by owner same flag
				 */
				boolean ownerSame = false;
				ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
				if (categoryVO != null && categoryVO.getCategoryCode().equals(catg)) {
					ownerSame=true;
				}
				String channelOwnerCategoryUserID = searchUserRequestVO.getDataF().getChannelOwnerUserID();//dao hit to get userid
				if (!ownerSame) {
					String channelOwnerCategoryUserName =null;
					
					userName = searchUserRequestVO.getDataF().getChannelOwnerName();
					if (!BTSLUtil.isNullString(userName)) {
						userName = "%" + userName + "%";
					}

					final ArrayList userList = channelUserWebDAO.loadCategoryUsersWithinGeoDomainHirearchy(con, channelOwnerCategory, userVO.getNetworkID(),
							userName, null, searchUserRequestVO.getDataF().getGeoDomainCode(), userVO.getUserID());
					
					if (userList.size() == 1) {
						listValueVO = (ListValueVO) userList.get(0);
						/*theForm.setChannelOwnerCategoryUserName(listValueVO.getLabel());
						theForm.setChannelOwnerCategoryUserID(listValueVO.getValue());*/
						channelOwnerCategoryUserName = listValueVO.getLabel();
						channelOwnerCategoryUserID = listValueVO.getValue();
					} else if (userList.size() > 1) {
						boolean isExist = false;
						if (!BTSLUtil.isNullString(channelOwnerCategoryUserID)) {
							for (int i = 0, k = userList.size(); i < k; i++) {
								listValueVO = (ListValueVO) userList.get(i);
								if (listValueVO.getValue().equals(channelOwnerCategoryUserID) && (searchUserRequestVO.getDataF().getChannelOwnerName().compareTo(
										listValueVO.getLabel()) == 0)) {
									channelOwnerCategoryUserName=listValueVO.getLabel();
									channelOwnerCategoryUserID=listValueVO.getValue();
									isExist = true;
									break;
								}
							}
						} else {
							ListValueVO listValueNextVO = null;
							for (int i = 0, k = userList.size(); i < k; i++) {
								listValueVO = (ListValueVO) userList.get(i);
								if (searchUserRequestVO.getDataF().getChannelOwnerName().compareTo(listValueVO.getLabel()) == 0) {
									if (((i + 1) < k)) {
										listValueNextVO = (ListValueVO) userList.get(i + 1);
										if (searchUserRequestVO.getDataF().getChannelOwnerName().compareTo(listValueNextVO.getLabel()) == 0) {
											isExist = false;
											break;
										}
										channelOwnerCategoryUserName=listValueVO.getLabel();
										channelOwnerCategoryUserID=listValueVO.getValue();
										isExist = true;
										break;
									}
									channelOwnerCategoryUserName=listValueVO.getLabel();
									channelOwnerCategoryUserID=listValueVO.getValue();
									isExist = true;
									break;
								}
							}
						}
						if (!isExist) {
							final String arr[] = { searchUserRequestVO.getDataF().getChannelOwnerName() };
							/*final BTSLMessages messages = new BTSLMessages("message.channeltransfer.usermorethanoneexist.msg", arr, "usersearch");
							return super.handleMessage(messages, request, mapping);*/
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MORE_THAN_ONE_USER_EXIST, 0, arr,
									null);
						}

					} else {
						final String arr[] = { "UserName",searchUserRequestVO.getDataF().getChannelOwnerName() };
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_FOUND_MULTIPLE, 0, arr,
								null);
					}
				}

				/*// check the user's information.
				if (theForm.getCategoryCode() != null && theForm.getCategoryCode().indexOf(":") > 0) {
					catg = theForm.getCategoryCode().substring(theForm.getCategoryCode().indexOf(":") + 1);
				}*/

				userName =identifiervalue;
				if (!BTSLUtil.isNullString(userName)) {
					userName = "%" + userName + "%";
				}
				/*
				 * As disscussed with AC, GSB,Sanjay Sir we have to load
				 * owner/parent user of status Y, S, SR
				 * but the child user must be of status Y. this is handled in
				 * the DAO's method of loading users.
				 * But if child user is the owner user then it should be only of
				 * status Y so for this we are
				 * assignning the NA value to the owner user ID
				 */
				if (ownerSame) {
					channelOwnerCategoryUserID="NA";
				}

				final ArrayList userList = channelUserWebDAO.loadCategoryUsersWithinGeoDomainHirearchy(con, catg, userVO.getNetworkID(), userName, channelOwnerCategoryUserID, searchUserRequestVO.getDataF().getGeoDomainCode(), userVO.getUserID());
				if(BTSLUtil.isNullOrEmptyList(userList))
				{
					String []args = new String[] { "UserName", identifiervalue };
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_FOUND_MULTIPLE, 0, args,
							null);	
				}
				String userID = null;
				String channelCategoryUserName = null;
				String channelCategoryUserID = searchUserRequestVO.getDataF().getChannelUserID();//dao hit to get USerID
				if (userList.size() == 1) {
					listValueVO = (ListValueVO) userList.get(0);
					userID=listValueVO.getValue();
					channelCategoryUserName=listValueVO.getLabel();
					channelCategoryUserID=listValueVO.getValue();
				} else if (userList.size() > 1) {
					boolean isExist = false;
					if (!BTSLUtil.isNullString(channelCategoryUserID)) {
						for (int i = 0, k = userList.size(); i < k; i++) {
							listValueVO = (ListValueVO) userList.get(i);
							if (listValueVO.getValue().equals(channelCategoryUserID) && (identifiervalue.compareTo(listValueVO.getLabel()) == 0)) {
								userID=listValueVO.getValue();
								channelCategoryUserName=listValueVO.getLabel();
								channelCategoryUserID=listValueVO.getValue();
								isExist = true;
								break;
							}
						}
					} else {
						ListValueVO listValueNextVO = null;
						for (int i = 0, k = userList.size(); i < k; i++) {
							listValueVO = (ListValueVO) userList.get(i);
							if (identifiervalue.compareTo(listValueVO.getLabel()) == 0) {
								if (((i + 1) < k)) {
									listValueNextVO = (ListValueVO) userList.get(i + 1);
									if (identifiervalue.compareTo(listValueNextVO.getLabel()) == 0) {
										isExist = false;
										break;
									}
									userID=listValueVO.getValue();
									channelCategoryUserName=listValueVO.getLabel();
									channelCategoryUserID=listValueVO.getValue();
									isExist = true;
									break;
								}
								userID=listValueVO.getValue();
								channelCategoryUserName=listValueVO.getLabel();
								channelCategoryUserID=listValueVO.getValue();
								isExist = true;
								break;
							}
						}
					}
					if (!isExist) {
						final String arr[] = { identifiervalue };
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MORE_THAN_ONE_USER_EXIST, 0, arr,
								null);
					}
				} else {
					final String arr[] = { "UserName",identifiervalue };
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_FOUND, 0, null,
							null);
				}
				ChannelUserDAO  channelUserDAO = new ChannelUserDAO();
				String []args = null;
				final ChannelUserVO receiverUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, channelCategoryUserID, false, curDate,false);
				if (receiverUserVO == null) {
					args = new String[] { identifiervalue };
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_DETAIL_NOT_FOUND, 0, args,
							null);
				} else {

					// user life cycle by Akanksha
					final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(receiverUserVO.getNetworkID(), receiverUserVO.getCategoryCode(), receiverUserVO
							.getUserType(), PretupsI.REQUEST_SOURCE_TYPE_WEB);
					if (userStatusVO != null) {
						final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
						final String status[] = userStatusAllowed.split(",");
						for (int i = 0; i < status.length; i++) {
							if (status[i].equals(receiverUserVO.getStatus())) {
								receiverStatusAllowed = 1;
							}
						}
					} else {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_ALLOWED, 0, null,
								null);
					}
				}
				if (receiverStatusAllowed == 0) {
					args = new String[] { identifiervalue };
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_SUSPENDED, 0, args,
							null);
				} else if (receiverUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
					args = new String[] { identifiervalue };
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE, 0, args,
							null);
				} else if (!PretupsI.YES.equals(receiverUserVO.getCommissionProfileStatus())) {
					// ChangeID=LOCALEMASTER
					// commission profile suspend message has to be set in VO
					// Check which language message to be set from the locale master
					// table for the perticuler locale.
					final Locale locale = BTSLUtil.getBTSLLocale(httpServletRequest);
					args = new String[] { identifiervalue, receiverUserVO.getCommissionProfileLang2Msg() };
					final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
					if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
						args = new String[] { identifiervalue, receiverUserVO.getCommissionProfileLang1Msg() };
					}
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE, 0, args,
							null);
				} else if (!PretupsI.YES.equals(receiverUserVO.getTransferProfileStatus())) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_TRANSFER_PROFILE_SUSPENDED, 0, args,
							null);
				}

				// to check user status
				if (receiverUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(receiverUserVO.getInSuspend())) {
					args = new String[] { identifiervalue };
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_IN_SUSPENDED, 0, args,
							null);
					
				}

				final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
				// load the approval limit of the user
				final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, userVO.getNetworkID(), searchUserRequestVO.getDataF().getChannelDomain(),
						PretupsI.CATEGORY_TYPE_OPT, catg, PretupsI.TRANSFER_RULE_TYPE_OPT, true);

				if (channelTransferRuleVO == null) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_TRANSFER_RULE_NOT_EXIST, 0, args,
							null);
				} else if (PretupsI.NO.equals(channelTransferRuleVO.getTransferAllowed())) {
					String []args1 ={catg};
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_ASSOCIATED_NOT_ALLOWED, 0, args1,
							null);
				} else if (channelTransferRuleVO.getProductVOList() == null || channelTransferRuleVO.getProductVOList().isEmpty()) {
					String []args1 ={channelCategoryUserName};
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NOT_DEFINED, 0, args1,
							null);
				}
				OperatorUtilI _operatorUtil = null;
				String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
				try {
					_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
				} catch (Exception e) {
					log.errorTrace("searchattribute", e);
				}

				if (PretupsI.YES.equals(receiverUserVO.getCategoryVO().getProductTypeAllowed())) {
					receiverUserVO.setAssociatedProductTypeList(
							new ProductTypeDAO().loadUserProductsListForLogin(con, receiverUserVO.getUserID()));
				} else {
					receiverUserVO.setAssociatedProductTypeList(_operatorUtil.loadProductCodeList());
				}

				// load the Product Type list and filter it with the C2S module.
				final ArrayList prodTypListtemp = new ArrayList(receiverUserVO.getAssociatedProductTypeList());

				ArrayList prodTypList = _operatorUtil.removeVMSProductCodeList(prodTypListtemp);
				ListValueVO listValueVO1 = null;
				for (int i = 0, j = prodTypList.size(); i < j; i++) {
					listValueVO1 = (ListValueVO) prodTypList.get(i);
					if (PretupsI.P2P_MODULE.equals(listValueVO1.getValue())) {
						prodTypList.remove(i);
						i--;
						j--;
					}
				}

				if (prodTypList != null && prodTypList.isEmpty()) {
					throw new BTSLBaseException(this, methodName,
							PretupsErrorCodesI.PRODUCTS_NOT_FOUND);
				}
				String productType = null;
				ArrayList<ChannelTransferItemsVO> list = new ArrayList<ChannelTransferItemsVO>();
				for(int i=0;i<prodTypList.size();i++) {
					productType = ((ListValueVO) prodTypList.get(i)).getValue();

					list.addAll(ChannelTransferBL.loadO2CXfrProductList(con, productType, receiverUserVO.getNetworkID(),
							receiverUserVO.getCommissionProfileSetID(), curDate, null));

				}


				/*
				 * User associated with commission profile.Commission profile associated with
				 * products. Display only those products which have commission profile same as
				 * user?s commission profile. If above condition fail then display error
				 * message.
				 */

				if (list.isEmpty()) {
					// final String args[] = { receiverUserVO.getLoginID(), productTypeDesc };
					throw new BTSLBaseException(this, methodName,
							PretupsErrorCodesI.PRODUCTS_NOT_FOUND_ACCORDING_TO_TRANSFER_RULE);
				}

				/*
				 * Now further filter the list with the transfer rules list and the above list
				 * of commission profile products.
				 */
				list = filterProductWithTransferRule(list, channelTransferRuleVO.getProductVOList());

				/*
				 * This case arises suppose in transfer rule products A and B are associated In
				 * commission profile product C and D are associated. We load product with
				 * intersection of transfer rule products and commission profile products. if no
				 * product found then display below message
				 */
				if (list.isEmpty()) {
					final String[] args1 = new String[] { receiverUserVO.getUserName() };
					throw new BTSLBaseException(this, methodName,
							PretupsErrorCodesI.PRODUCTS_NOT_FOUND_FOR_COMMISSION_PROFILE, args1);
				}

				/*
				 * Getting User balance
				 */
				final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
				HashMap<String, Long> prodBal = new HashMap<>();
				ArrayList<C2sBalanceQueryVO> userBalanceList = userBalancesDAO.loadUserBalances(con, receiverUserVO.getUserID());
				if(!BTSLUtil.isNullOrEmptyList(userBalanceList))
				{
				for(int i=0;i<userBalanceList.size();i++)
				{
					C2sBalanceQueryVO c2sBalanceQueryVO = userBalanceList.get(i);
					prodBal.put(c2sBalanceQueryVO.getProductCode(), c2sBalanceQueryVO.getBalance());
				}
				for(int i=0;i<list.size();i++)
				{
					ChannelTransferItemsVO channelTransferItemsVO=(ChannelTransferItemsVO) list.get(i);
					if(prodBal.containsKey(channelTransferItemsVO.getProductCode()))
					{
					channelTransferItemsVO.setBalance(BTSLUtil.parseDoubleToLong( BTSLUtil.getDisplayAmount(prodBal.get((channelTransferItemsVO).getProductCode()))) );
					}
				}
				}
				response.setProdList(list);
				response.setChannelUserVO(receiverUserVO);
			}
			else 
				{
				if(identifiertype.equalsIgnoreCase("LOGINID"))
			{
				ChannelUserVO channelUserVO=userDao.loadUserDetailsByLoginId(con,identifiervalue);
				if(BTSLUtil.isNullObject(channelUserVO))
				{
					String []args = new String[] { "LoginID", identifiervalue };
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_FOUND_MULTIPLE, 0, args,
							null);
				}
				identifiervalue=channelUserVO.getMsisdn();
			}
			UserPhoneVO phoneVO =null;
			// Added by Amit Raheja for NNP changes
			if (!BTSLUtil.isNullString(identifiervalue)) {
				identifiervalue=PretupsBL.getFilteredMSISDN(identifiervalue);
			}
			// Addition ends
			if (!BTSLUtil.isNullString(identifiervalue)) {
				phoneVO = userDao.loadUserAnyPhoneVO(con, identifiervalue);
			}
			String args[] = null;
			if (phoneVO == null) {
				args = new String[] { "Msisdn",identifiervalue };
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_FOUND_MULTIPLE, 0, args,
						null);
			}
			ChannelUserVO channelUserVO = null;
			ChannelUserDAO channelUserDAO= new ChannelUserDAO();
			if (SystemPreferences.SECONDARY_NUMBER_ALLOWED && ((phoneVO.getPrimaryNumber()).equalsIgnoreCase("N"))) {
				channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, phoneVO.getUserId(), false, curDate,false);
				if (channelUserVO == null) {
					args = new String[] { identifiervalue };
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_DETAIL_NOT_FOUND, 0, args,
							null);
				}
				channelUserVO.setPrimaryMsisdn(channelUserVO.getMsisdn());
				channelUserVO.setMsisdn(identifiervalue);
			} else {
				channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, identifiervalue, true, curDate,false);
			}
			if (channelUserVO == null) {
				args = new String[] { identifiervalue };
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_DETAIL_NOT_FOUND, 0, args,
						null);
			} else {

				if(channelUserVO.getCommissionProfileStatus().equals(PretupsI.SUSPEND)) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.COMM_SUSPEND, 0, null);
				}

				// user life cycle by Akanksha
				final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), channelUserVO
						.getUserType(), PretupsI.REQUEST_SOURCE_TYPE_WEB);
				if (userStatusVO != null) {
					final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
					final String status[] = userStatusAllowed.split(",");
					for (int i = 0; i < status.length; i++) {
						if (status[i].equals(channelUserVO.getStatus())) {
							receiverStatusAllowed = 1;
						}
					}
				} else {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_ALLOWED, 0, null,
							null);
				}
			}
			if (receiverStatusAllowed == 0) {
				args = new String[] { identifiervalue };
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_SUSPENDED, 0, args,
						null);
			} else if (channelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
				args = new String[] { identifiervalue };
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE, 0, args,
						null);
			} else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
				// ChangeID=LOCALEMASTER
				// commission profile suspend message has to be set in VO
				// Check which language message to be set from the locale master
				// table for the perticuler locale.
				final Locale locale = BTSLUtil.getBTSLLocale(httpServletRequest);
				args = new String[] { identifiervalue, channelUserVO.getCommissionProfileLang2Msg() };
				final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
				if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
					args = new String[] { identifiervalue, channelUserVO.getCommissionProfileLang1Msg() };
				}

				/*final BTSLMessages messages = new BTSLMessages("commissionprofile.notactive.msg", args, "searchattribute");
				return super.handleMessage(messages, request, mapping);*/
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE, 0, args,
						null);
			} else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
				
				/*final BTSLMessages messages = new BTSLMessages("transferprofile.notactive.msg", args, "searchattribute");
				return super.handleMessage(messages, request, mapping);*/
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_TRANSFER_PROFILE_SUSPENDED, 0, args,
						null);
			}

			// to check user status
			if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
				/*final BTSLMessages messages = new BTSLMessages("message.channeltransfer.transfernotallowed.userinsuspend", "searchattribute");
				return super.handleMessage(messages, request, mapping);*/
				args = new String[] { identifiervalue };
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_IN_SUSPENDED, 0, args,
						null);
				
			}
			
			// to check the domain of the user with the domain of the logged in
			// user
			if (!BTSLUtil.isNullString(userVO.getDomainID())) {
				if (!channelUserVO.getDomainID().equals(userVO.getDomainID())) {
					args = new String[] { identifiervalue };
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_DOMAIN_NOTSAME, 0, args,
							null);
					
				}
			} else {
				ListValueVO listValueVO = null;
				boolean domainfound = false;
				final ArrayList domainList = domList1;
				for (int i = 0, j = domainList.size(); i < j; i++) {
					listValueVO = (ListValueVO) domainList.get(i);
					if (channelUserVO.getDomainID().equals(listValueVO.getValue())) {
						domainfound = true;
						break;
					}
				}
				if (!domainfound) {
					args = new String[] { identifiervalue };
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_DOMAIN_NOTSAME, 0, args,
							null);
				}
			}
			// now check that is user down in the geographical domain of the
			// loggin user or not.

			final GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
			if (!geographicalDomainDAO.isGeoDomainExistInHierarchy(con, channelUserVO.getGeographicalCode(), userVO.getUserID())) {
				/*final BTSLMessages messages = new BTSLMessages("message.channeltransfer.transfernotallowed.usernotdowngeogrphy", new String[] { theForm.getUserCode() },
						"searchattribute");
				return super.handleMessage(messages, request, mapping);*/
				args = new String[] { identifiervalue };
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN_HIERARCHY, 0, args,
						null);
			}
			final ChannelTransferRuleVO channelTransferRuleVO = new ChannelTransferRuleDAO().loadTransferRule(con, userVO.getNetworkID(), channelUserVO.getDomainID(),
					PretupsI.CATEGORY_TYPE_OPT, channelUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);

			if (channelTransferRuleVO == null) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_TRANSFER_RULE_NOT_EXIST, 0, args,
						null);
			} else if (PretupsI.NO.equals(channelTransferRuleVO.getTransferAllowed())) {
				String []args1 ={channelUserVO.getCategoryCode()};
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_ASSOCIATED_NOT_ALLOWED, 0, args1,
						null);
			} else if (channelTransferRuleVO.getProductVOList() == null || channelTransferRuleVO.getProductVOList().isEmpty()) {
				String []args1 ={channelUserVO.getUserName()};
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NOT_DEFINED, 0, args1,
						null);
			}
			OperatorUtilI _operatorUtil = null;
			String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			try {
				_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
			} catch (Exception e) {
				log.errorTrace("searchattribute", e);
			}

			if (PretupsI.YES.equals(channelUserVO.getCategoryVO().getProductTypeAllowed())) {
				channelUserVO.setAssociatedProductTypeList(
						new ProductTypeDAO().loadUserProductsListForLogin(con, channelUserVO.getUserID()));
			} else {
				channelUserVO.setAssociatedProductTypeList(_operatorUtil.loadProductCodeList());
			}

			// load the Product Type list and filter it with the C2S module.
			final ArrayList prodTypListtemp = new ArrayList(channelUserVO.getAssociatedProductTypeList());

			ArrayList prodTypList = _operatorUtil.removeVMSProductCodeList(prodTypListtemp);
			ListValueVO listValueVO = null;
			for (int i = 0, j = prodTypList.size(); i < j; i++) {
				listValueVO = (ListValueVO) prodTypList.get(i);
				if (PretupsI.P2P_MODULE.equals(listValueVO.getValue())) {
					prodTypList.remove(i);
					i--;
					j--;
				}
			}

			if (prodTypList != null && prodTypList.isEmpty()) {
				throw new BTSLBaseException(this, methodName,
						PretupsErrorCodesI.PRODUCTS_NOT_FOUND);
			}
			String productType = null;
			ArrayList<ChannelTransferItemsVO> list = new ArrayList<ChannelTransferItemsVO>();
			for(int i=0;i<prodTypList.size();i++) {
				productType = ((ListValueVO) prodTypList.get(i)).getValue();
				list.addAll(ChannelTransferBL.loadO2CXfrProductList(con, productType, channelUserVO.getNetworkID(),
						channelUserVO.getCommissionProfileSetID(), curDate, null));
			}


			 /*
			 * User associated with commission profile.Commission profile associated with
			 * products. Display only those products which have commission profile same as
			 * user?s commission profile. If above condition fail then display error
			 * message.
			 */

			if (list.isEmpty()) {
				// final String args[] = { receiverUserVO.getLoginID(), productTypeDesc };
				throw new BTSLBaseException(this, methodName,
						PretupsErrorCodesI.PRODUCTS_NOT_FOUND_ACCORDING_TO_TRANSFER_RULE);
			}

			/*
			 * Now further filter the list with the transfer rules list and the above list
			 * of commission profile products.
			 */
			list = filterProductWithTransferRule(list, channelTransferRuleVO.getProductVOList());

			/*
			 * This case arises suppose in transfer rule products A and B are associated In
			 * commission profile product C and D are associated. We load product with
			 * intersection of transfer rule products and commission profile products. if no
			 * product found then display below message
			 */
			if (list.isEmpty()) {
				final String[] args1 = new String[] { channelUserVO.getUserName() };
				throw new BTSLBaseException(this, methodName,
						PretupsErrorCodesI.PRODUCTS_NOT_FOUND_FOR_COMMISSION_PROFILE, args1);
			}

			/*
			 * Getting User balance
			 */
			final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
			HashMap<String,Long> prodBal = new HashMap<>();
			ArrayList<C2sBalanceQueryVO> userBalanceList = userBalancesDAO.loadUserBalances(con, channelUserVO.getUserID());
			if(!BTSLUtil.isNullOrEmptyList(userBalanceList))
			{
				for(int i=0;i<userBalanceList.size();i++)
			{
				C2sBalanceQueryVO c2sBalanceQueryVO = userBalanceList.get(i);
				prodBal.put(c2sBalanceQueryVO.getProductCode(), c2sBalanceQueryVO.getBalance());
			}
			for(int i=0;i<list.size();i++)
			{
				ChannelTransferItemsVO channelTransferItemsVO=(ChannelTransferItemsVO) list.get(i);
				if(prodBal.containsKey(channelTransferItemsVO.getProductCode()))
				{
					
					channelTransferItemsVO.setBalance(BTSLUtil.parseDoubleToLong( BTSLUtil.getDisplayAmount(prodBal.get(channelTransferItemsVO.getProductCode()))));
				}
			}
			}
			response.setProdList(list);
			response.setChannelUserVO(channelUserVO);
				}
			/*
			 * Setting response messages
			 */
			response.setService("searchApiResp");
			response.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			response.setService("searchApiResp");
			response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
					be.getArgs());
			response.setMessageCode(be.getMessage());
			response.setMessage(resmsg);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			response.setService("searchApiResp");
			response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
			response.setMessageCode(e.toString());
			response.setMessage(e.toString() + " : " + e.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("O2CSearchApiController#" + "getSearchUserDetails");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			if (log.isDebugEnabled()) {
				log.debug(methodName, " Exited ");
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, response);
			log.debug(methodName, "Exiting ");
		}

		return response;
	}
	/**
	 * Filter the product on the bases of transfer rule This method returns the list
	 * of products, which are comman in the both of the arrayLists
	 * 
	 * @param p_productList
	 * @param p_productListWithXfrRule
	 * @return ArrayList
	 */
	private ArrayList filterProductWithTransferRule(ArrayList p_productList, ArrayList p_productListWithXfrRule) {
		final String methodName = "filterProductWithTransferRule";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered p_productList: " + p_productList.size() + " p_productListWithXfrRule: "
					+ p_productListWithXfrRule.size());
		}
		ChannelTransferItemsVO channelTransferItemsVO = null;
		ListValueVO listValueVO = null;
		final ArrayList tempList = new ArrayList();
		for (int m = 0, n = p_productList.size(); m < n; m++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) p_productList.get(m);
			for (int i = 0, k = p_productListWithXfrRule.size(); i < k; i++) {
				listValueVO = (ListValueVO) p_productListWithXfrRule.get(i);
				if (channelTransferItemsVO.getProductCode().equals(listValueVO.getValue())) {
					tempList.add(channelTransferItemsVO);
					break;
				}
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting tempList: " + tempList.size());
		}

		return tempList;
	}
	

	
	@GetMapping( value = "/chUserPaymentTypes&Ranges", 
			produces =MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(tags= "O2C Payment Details ", value = "get channel user Payment Types & Ranges",
	       response = PretupsResponse.class,
 			authorizations = {
    	            @Authorization(value = "Authorization")})
	
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = PretupsResponse.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${chUserPaymentTypes.summary}", description="${chUserPaymentTypes.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PaymentModeDetailsResponse.class))
							)
					}

					),


					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))

									 , examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.BAD_REQUEST)}

							)
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.UNAUTH)}

									 )
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.NOT_FOUND)}

							)
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)}
					)})
			}
	)



	public PaymentModeDetailsResponse getPaymentTypeAndAmountRanges(
    		 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
    		 @RequestParam("channelUserId") String  channelUserId,
    		 @Parameter(description = "transferType", required = true)// allowableValues = "O2C,C2C")
    		 @RequestParam("transferType")  String transferType,
    		 HttpServletResponse response1) throws IOException, SQLException, BTSLBaseException  {
		final String methodName = "getPaymentTypeAndAmountRanges";
		final List<String> trnList=Arrays.asList("O2C","ALL","C2C");
		PaymentModeDetailsResponse response=new PaymentModeDetailsResponse();
		 final List<String> paymentCodes=Arrays.asList("ONLINE","CASH","DD","CHQ","OTH");
		 Connection con = null;
		 MComConnectionI mcomCon = null;
         OAuthUser oAuthUser= null;
         OAuthUserData oAuthUserData =null;
         List<PaymentModeDetailsDto> listPytdto=null;
         Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
         final Integer amountMultFactor =SystemPreferences.AMOUNT_MULT_FACTOR; 
         final  ChannelUserDAO channelUserDao=new ChannelUserDAO();
         try {
        	 if(BTSLUtil.isNullorEmpty(channelUserId)) {
        		 throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_MISSING, new String[] { "User ID" });
        	 }
        	 if(BTSLUtil.isNullorEmpty(transferType)) {
        		 throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_MISSING, new String[] { "TransferType" });
        	 }
        	 if(trnList.stream().noneMatch(p->p.equals(transferType))) {
        		 throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "TransferType" });
        	 }
        	oAuthUser = new OAuthUser();
 			oAuthUserData =new OAuthUserData();
 			oAuthUser.setData(oAuthUserData);
 			OAuthenticationUtil.validateTokenApi(oAuthUser, headers);
 			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			
			ChannelUserVO chUserVo=channelUserDao.loadChannelUserByUserIDAnyStatus(con, channelUserId);
			if(chUserVo==null) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "User ID" });
			}
			listPytdto=channelUserDao.loadPaymentModesAndRanges(con, channelUserId);
			if(!BTSLUtil.isNullorEmpty(listPytdto)) {
				 List<PaymentModeDetailsDto>  listpay1 =listPytdto.stream().filter(p->p.getTransferType().equals(transferType)).collect(Collectors.toList());
	 			 List<PaymentModeDetailsDto> listpay2=listPytdto.stream().filter(p->(
	 					                               p.getTransferType().equalsIgnoreCase("ALL")
	 					                               &&p.getPaymentMode().equalsIgnoreCase("ALL")
	 					                               )).collect(Collectors.toList());
	 			 Map<String, List<ProductPaymentModesVO>> detailMap=new HashMap<String, List<ProductPaymentModesVO>>(); 
	 			 Map<String, List<PaymentModeDetailsDto>> payMap =listpay1.stream().collect(Collectors.groupingBy(PaymentModeDetailsDto::getProductcode));
	 			 detailMap.put(PretupsI.PRODUCT_ETOPUP,new ArrayList<ProductPaymentModesVO>());
	 			 detailMap.put(PretupsI.PRODUCT_POSTETOPUP, new ArrayList<ProductPaymentModesVO>());
	 			 if(!listpay1.isEmpty()) {
	 				 
						 for(Map.Entry<String, List< PaymentModeDetailsDto>>  entry:payMap.entrySet()) {
							    List<ProductPaymentModesVO> list=new ArrayList<ProductPaymentModesVO>();		    
						    for(PaymentModeDetailsDto dto:entry.getValue()) {
							    	ProductPaymentModesVO prodPayM=null;
									prodPayM=new ProductPaymentModesVO();
							    	prodPayM.setPaymentMode(dto.getPaymentMode());
							    	prodPayM.setTransferType(dto.getTransferType());
							    	prodPayM.setIsSlabExisted(true);
							    	prodPayM.setIsDefault(false);
							    	prodPayM.setMinTransferValue(dto.getMinTransferValue()/amountMultFactor);
							    	prodPayM.setMaxTransferValue(dto.getMaxTransferValue()/amountMultFactor);
							    	list.add(prodPayM);
							    }
						    ProductPaymentModesVO prod=null;
						    try {
							    prod= list.stream().filter(p->p.getPaymentMode().equals("ALL")).collect(Collectors.toList()).get(0);				    	
							   }catch (Exception e) {
								prod=null;
							  }
						   for(String mode:paymentCodes) {
							   if(list.stream().noneMatch(p->p.getPaymentMode().equals(mode))) {
								   ProductPaymentModesVO prod1=new ProductPaymentModesVO(); 
								   if(prod!=null) {
									   prod1.setMaxTransferValue(prod.getMaxTransferValue());
									   prod1.setMinTransferValue(prod.getMinTransferValue());
									   prod1.setIsSlabExisted(true);
									   prod1.setIsDefault(true);
									   prod1.setTransferType(prod.getTransferType());
								   }
								   else {
									   prod1.setMaxTransferValue(0l);
									   prod1.setMinTransferValue(0l);
									   prod1.setIsSlabExisted(false);
									   prod1.setIsDefault(false);
									   prod1.setTransferType(transferType);
								   }
								   prod1.setPaymentMode(mode); 
								   list.add(prod1);
							   }
						   }
							 detailMap.put(entry.getKey(), list);  
						  }
			 			
	 			 }
	 			
				 if(!listpay2.isEmpty()) {
				   for(Map.Entry<String, List< ProductPaymentModesVO>>  entry:detailMap.entrySet()) {
					 if(!payMap.containsKey(entry.getKey())) {
						 List<PaymentModeDetailsDto>  listEach=listpay2.stream().filter(p->p.getProductcode().equals(entry.getKey())).collect(Collectors.toList());
						  if(!listEach.isEmpty()) {
							  PaymentModeDetailsDto dto=listEach.get(0);
							  List<ProductPaymentModesVO> list=new ArrayList<ProductPaymentModesVO>();
		 				     for(String mode:paymentCodes) {
		 					    ProductPaymentModesVO prod1=new ProductPaymentModesVO();
		 					    prod1.setMaxTransferValue(dto.getMaxTransferValue()/amountMultFactor);
		 					    prod1.setMinTransferValue(dto.getMinTransferValue()/amountMultFactor);
		 					    prod1.setIsDefault(true);
		 					    prod1.setIsSlabExisted(true);
		 					    prod1.setPaymentMode(mode);
		 					   list.add(prod1);
		 				     }
		 				   detailMap.put(entry.getKey(), list);
					    }
					   }
		 			 }
					 for(Map.Entry<String, List< ProductPaymentModesVO>>  entry:detailMap.entrySet()) {
						  List<PaymentModeDetailsDto>  listEach=listpay2.stream().filter(p->p.getProductcode().equals(entry.getKey())).collect(Collectors.toList());
						  if(!listEach.isEmpty()) {
							  PaymentModeDetailsDto dto=listEach.get(0);
							  for(ProductPaymentModesVO vo:entry.getValue()) {
							    	if(!vo.getIsSlabExisted()) {
							    		vo.setMaxTransferValue(dto.getMaxTransferValue()/amountMultFactor);
							    		vo.setMinTransferValue(dto.getMinTransferValue()/amountMultFactor);
							    		vo.setIsDefault(true);
							    		vo.setIsSlabExisted(true);
							    	}
							    }
						  }
					 }
				 }
				 response.setDetails(detailMap);
			 }
 			 response.setStatus(HttpStatus.SC_OK);
			 response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			 String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.SUCCESS, null);
			 response.setMessage(resmsg);
			 
 			 return response;
         }catch (BTSLBaseException be) {
        	 log.error(methodName, "Exception:e=" + be);
 			log.errorTrace(methodName, be);
 			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
 				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
 			} else {
 				response.setStatus(HttpStatus.SC_BAD_REQUEST);
 			}
 			String resmsg = "";
 			if (be.getArgs() != null) {
 				resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());

 			} else {
 				resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);

 			}
 			response.setMessageCode(be.getMessage());
 			response.setMessage(resmsg);
              
          } catch (Exception e) {
        	log.error(methodName, "Exceptin:e=" + e);
  			log.errorTrace(methodName, e);
  			response1.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
  			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
  			response.setMessageCode("error.general.processing");
  			response.setMessage(
  					"Due to some technical reasons, your request could not be processed at this time. Please try later");
          }
		 finally {
				if (mcomCon != null) {
					mcomCon.close(methodName);
					mcomCon = null;
				}
				LogFactory.printLog(methodName, " Exited ", log);
			}
		 return response;
      }	
}
