package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
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
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransferUserDetails;
import com.btsl.pretups.channel.transfer.businesslogic.UserProductDetailsForO2C;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${GetUserInfoForO2C.name}", description = "${GetUserInfoForO2C.desc}")//@Api(tags = "O2C Services")
@RestController
@RequestMapping(value = "/v1/o2c")

public class GetUserInfoForO2C {
	public static final Log log = LogFactory.getLog(GetUserInfoForO2C.class.getName());

	private O2CTransferUserDetails o2CTransferUserDetails = null;

	@GetMapping(value = "/getUserInfo", produces = MediaType.APPLICATION_JSON)
	@ResponseBody

	/*@ApiOperation(value = "Get O2C User Info", response = O2CTransferUserDetails.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = O2CTransferUserDetails.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getUserInfo.summary}", description="${getUserInfo.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = O2CTransferUserDetails.class))
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



	public O2CTransferUserDetails getUserInfoForO2C(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag,
			@Parameter(description = "userId", required = true) @RequestParam("userId") String userId,
			@Parameter(description = "networkCode", required = true) @RequestParam("networkCode") String networkCode

	) {

		final String methodName = "getUserInfoForO2C";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;

		ChannelUserWebDAO channelUserWebDAO = null;
		BaseResponseMultiple baseResponseMultiple = null;
		OAuthUserData oAuthdata = null;
		OAuthUser oAuthreqVo = null;
		String productType = null;
		String productTypeDesc = null;
		String messageArray[] = new String[1];
		try {
			o2CTransferUserDetails = new O2CTransferUserDetails();
			channelUserWebDAO = new ChannelUserWebDAO();
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			baseResponseMultiple = new BaseResponseMultiple();
			oAuthdata = new OAuthUserData();
			oAuthreqVo = new OAuthUser();
			oAuthreqVo.setData(oAuthdata);

			// validate token
			OAuthenticationUtil.validateTokenApi(oAuthreqVo, headers, baseResponseMultiple);
            
			//Validating Network Code
			if (!BTSLUtil.isNullString(networkCode)) {
				NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(networkCode);
				if (networkVO == null) {
					messageArray[0] = networkCode;
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID,
							messageArray);
				}
			} else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK, 0, null,
						null);
			}
			
			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			final Date curDate = new Date();

			final ChannelUserVO receiverUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, userId, false,
					curDate, false);
			if (BTSLUtil.isNullorEmpty(receiverUserVO)) {
				final String[] args = new String[] { userId };
				throw new BTSLBaseException("GetProductDetailsForO2CcController", methodName,
						"message.channeltransfer.userdetailnotfound.msg", args);
			}

			final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
			// load the approval limit of the user
			final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con,
					networkCode, receiverUserVO.getDomainID(), PretupsI.CATEGORY_TYPE_OPT,
					receiverUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);

			if (channelTransferRuleVO == null) {
				throw new BTSLBaseException("GetProductDetailsForO2CcController", methodName,
						"message.channeltransfer.transferrulenotexist");

			} else if (PretupsI.NO.equals(channelTransferRuleVO.getTransferAllowed())) {

				throw new BTSLBaseException("GetProductDetailsForO2CcController", methodName,
						"message.channeltransfer.transferrulenotdefine");
			} else if (channelTransferRuleVO.getProductVOList() == null
					|| channelTransferRuleVO.getProductVOList().isEmpty()) {
				throw new BTSLBaseException("GetProductDetailsForO2CcController", methodName,
						"message.transfer.noproductassigned.transferrule");
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
				throw new BTSLBaseException("GetProductDetailsForO2CcController", methodName,
						"channeltransfer.transfer.errormsg.noproducttype");
			}

			ArrayList<ChannelTransferItemsVO> list = new ArrayList<ChannelTransferItemsVO>();
			for(int i=0;i<prodTypList.size();i++) {
				productTypeDesc = ((ListValueVO) prodTypList.get(i)).getLabel();
				productType = ((ListValueVO) prodTypList.get(i)).getValue();

				list.addAll(ChannelTransferBL.loadO2CXfrProductList(con, productType, networkCode,
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
				throw new BTSLBaseException("GetProductDetailsForO2CcController", methodName,
						"message.transfer.noproductassigned.o2c.transferrule");
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
				final String[] args = new String[] { receiverUserVO.getUserName() };
				throw new BTSLBaseException("GetProductDetailsForO2CcController", methodName,
						"message.transfer.transferrule.noproductmatch", args);
			}

			/*
			 * Getting User balance
			 */
			final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
			ArrayList<C2sBalanceQueryVO> userBalanceList = userBalancesDAO.loadUserBalances(con, userId);
			o2CTransferUserDetails.setUserBalanceDetails(userBalanceList);

			// setting valid response
			setO2CTransferDetails(receiverUserVO, list, userBalanceList);
			o2CTransferUserDetails.setMessage(PretupsI.SUCCESS);
			o2CTransferUserDetails.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
			responseSwag.setStatus(HttpStatus.SC_OK);
			return o2CTransferUserDetails;

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				o2CTransferUserDetails.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				o2CTransferUserDetails.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					be.getMessageKey(), be.getArgs());
			o2CTransferUserDetails.setMessageCode(be.getMessage());
			o2CTransferUserDetails.setMessage(resmsg);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);

			o2CTransferUserDetails.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			o2CTransferUserDetails.setMessageCode(e.toString());
			o2CTransferUserDetails.setMessage(e.toString() + " : " + e.getMessage());
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChannelTransferAction#loadUserProducts");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting forward=" + o2CTransferUserDetails);
			}
		}
		return o2CTransferUserDetails;
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

	private void setO2CTransferDetails(ChannelUserVO receiverUserVO, ArrayList productList,
			ArrayList<C2sBalanceQueryVO> userBalanceList) {
		String balance = null;
		String mrp = null;

		UserProductDetailsForO2C productDetails = null;
		ArrayList<UserProductDetailsForO2C> productDetailsList = new ArrayList<UserProductDetailsForO2C>();

		for (int i = 0; i < productList.size(); i++) {
			ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(i);
			productDetails = new UserProductDetailsForO2C();

			productDetails.setProductName(channelTransferItemsVO.getProductName());
			productDetails.setProductCode(channelTransferItemsVO.getProductCode());
			productDetails.setProductShortCode(String.valueOf(channelTransferItemsVO.getProductShortCode()));
			productDetails.setProductUserMaxTransferValue(channelTransferItemsVO.getMaxTransferValueAsString());
			productDetails.setProductUserMinTransferValue(channelTransferItemsVO.getMinTransferValueAsString());
			productDetailsList.add(productDetails);

		}
		for (int i = 0; i < userBalanceList.size(); i++) {
			C2sBalanceQueryVO c2sBalanceQueryVO = userBalanceList.get(i);
			balance = PretupsBL.getDisplayAmount(c2sBalanceQueryVO.getBalance());
			//method requires long data type as argument
			mrp = PretupsBL.getDisplayAmount(BTSLUtil.parseStringToLong(c2sBalanceQueryVO.getUnitValue()));  
			o2CTransferUserDetails.getUserBalanceDetails().get(i).setBalance(BTSLUtil.parseStringToLong(balance));

			for (int j = 0; j < productDetailsList.size(); j++) {
				UserProductDetailsForO2C userProductDetails = productDetailsList.get(j);
				
				if (c2sBalanceQueryVO.getProductShortCode().equalsIgnoreCase(userProductDetails.getProductShortCode())) 
				{
					userProductDetails.setProductUserBalance(balance);
					userProductDetails.setProductCode(c2sBalanceQueryVO.getProductCode());
					userProductDetails.setProductShortName(c2sBalanceQueryVO.getProductShortCode());
					userProductDetails.setMrp(mrp);
					
				}
			}
		}

		o2CTransferUserDetails.setSenderUserName(receiverUserVO.getUserName());
		o2CTransferUserDetails.setSenderCommissionProfileID(receiverUserVO.getCommissionProfileSetID());
		o2CTransferUserDetails.setSenderCommissionProfileName(receiverUserVO.getCommissionProfileSetName());
		o2CTransferUserDetails.setSenderCommissionProfileSetVersion(receiverUserVO.getCommissionProfileSetVersion());
		o2CTransferUserDetails.setSenderCategoryID(receiverUserVO.getCategoryCode());
		o2CTransferUserDetails.setSenderCategoryName(receiverUserVO.getCategoryVO().getCategoryName());
		o2CTransferUserDetails.setSenderUserGradeCode(receiverUserVO.getUserGrade());
		o2CTransferUserDetails.setSenderUserGradeName(receiverUserVO.getUserGradeName());
		o2CTransferUserDetails.setSenderTransferProfileID(receiverUserVO.getTransferProfileID());
		o2CTransferUserDetails.setSenderTransferProfileName(receiverUserVO.getTransferProfileName());
		o2CTransferUserDetails.setSenderMsisdn(receiverUserVO.getMsisdn());
		o2CTransferUserDetails.setSenderDualCommission(receiverUserVO.getDualCommissionType());
		o2CTransferUserDetails.setUserProductDetails(productDetailsList);

	}

}
