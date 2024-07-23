package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferWebDAO;
import com.web.voms.vomscategory.businesslogic.VomsCategoryWebDAO;

import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2CVoucherTransferAPICalculationController.name}", description = "${C2CVoucherTransferAPICalculationController.desc}")//@Api(tags= "C2C Receiver", value="C2C Receiver")
@RestController
@RequestMapping(value = "/v1/c2cReceiver")
public class C2CVoucherTransferAPICalculationController  {
	 private static final float EPSILON = 0.0000001f; 
	private final Log log = LogFactory.getLog(C2CVoucherTransferAPICalculationController.class.getName());
	@PostMapping(value = "/vtxncalviw", consumes =MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags= "C2C Receiver", value = "VOUCHER C2C TRF/ BUY  API FOR CALCULATION",
	  authorizations = {
	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = TransferDetailsVoucherResp.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${vtxncalviw.summary}", description="${vtxncalviw.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TransferDetailsVoucherResp.class))
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




	public TransferDetailsVoucherResp processCP2PUserRequest(HttpServletRequest httpServletRequest,
    		    @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
		        @RequestBody C2CVoucherTransferDetailsData c2CVoucherTransferDetailsData, HttpServletResponse response) 
		        		throws IOException, SQLException, BTSLBaseException {
		        			final String methodName =  "processCP2PUserRequest";
		        			if (log.isDebugEnabled()) {
		        				log.debug(methodName, "Entered ");
		        			}
		        			    String identifiertype ="LOGINID";
	        	        	    String identifiervalue = "";
		        			    TransferDetailsVoucherResp response1 = null;
		        			    OAuthUser oAuthUser = null;
		        				OAuthUserData oAuthUserData = null;
		        			    Connection con = null;
		        		        MComConnectionI mcomCon = null;
		        		        
		        		        
		        		        String messageArray[] = new String[1];
		        			    UserDAO userDao = null;
		        			    ChannelUserDAO channelUserDAO = null;
		        			    StringBuilder responseStr = null;
		        			    String product = VOMSI.DEFAULT_PRODUCT_CODE;
		        			    HashMap<String, Object> resMap= new HashMap<>();
		        				String transferType,transferSubType,dualCommission,cbcflag,commissionProfileID,commissionProfileVersion,requestType;	
		        				ChannelTransferItemsVO channelTransferItemsVO = null;
		        				String networkCode = null;
		        				long totTax1 = 0, totTax2 = 0, totTax3 = 0, totRequestedQty = 0, payableAmount = 0, netPayableAmt = 0, totTransferedAmt = 0, totalMRP = 0, totcommission = 0;
		        				long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0,otfValue = 0;
		        				double totalRequestedQuantity = 0;
		        			 	long totalVoucherQty = 0;
		        			 	final long unused_vouchers = 0L;
		        			 	final ArrayList voucherErrorList = new ArrayList();
		        			 	UserVO sessionUserVO = new UserVO();
		        			try {
		        				mcomCon = new MComConnection();
		        	            con=mcomCon.getConnection();
		        				response1 = new TransferDetailsVoucherResp ();
		        				
		        				/*
			        			 * Authentication
			        			 * @throws BTSLBaseException
			        			 */
								//OAuthenticationUtil.validateTokenApi(headers);
								//Authenication 
					        	oAuthUser = new OAuthUser();
								oAuthUserData = new OAuthUserData();
								oAuthUser.setData(oAuthUserData);
					 	        OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response);
					 	        identifiervalue = oAuthUser.getData().getLoginid();
								ChannelTransferItemsVO tempChannelTransferItemsVO = new ChannelTransferItemsVO();
								final ArrayList<ChannelTransferItemsVO> itemsList = new ArrayList<ChannelTransferItemsVO>();
								dualCommission=c2CVoucherTransferDetailsData.getDualCommission();
								networkCode = c2CVoucherTransferDetailsData.getExtnwcode();
								commissionProfileID=c2CVoucherTransferDetailsData.getCommissionProfileID();
								commissionProfileVersion=c2CVoucherTransferDetailsData.getCommissionProfileVersion();
								transferType  = c2CVoucherTransferDetailsData.getTransferType();
								transferSubType = c2CVoucherTransferDetailsData.getTransferSubType();
								cbcflag = c2CVoucherTransferDetailsData.getCbcflag();
								SlabList[] slabArr = c2CVoucherTransferDetailsData.getSlablist();
								ArrayList<VomsBatchVO> slabsList = new ArrayList<VomsBatchVO>();
								requestType = c2CVoucherTransferDetailsData.getRequestType();
								for(int i=0;i<slabArr.length;i++)	
								{	
									VomsBatchVO vomsBatchVO = new VomsBatchVO();
									vomsBatchVO.setVoucherType(slabArr[i].getVoucherType());
									vomsBatchVO.setSegment(slabArr[i].getSegmentType());
									vomsBatchVO.setMrp(slabArr[i].getVoucherMrp());
									vomsBatchVO.setDenomination(slabArr[i].getVoucherMrp());
									if(requestType.equals("TRANSFER")) {
									vomsBatchVO.setFromSerialNo(slabArr[i].getFromSerialNo());
									vomsBatchVO.setToSerialNo(slabArr[i].getToSerialNo());			
								    String productId =  (new ChannelTransferDAO()).retreiveProductId(con, vomsBatchVO.getFromSerialNo());
								    vomsBatchVO.setProductID(productId);		
									}else {
									vomsBatchVO.setQuantity(slabArr[i].getQty());
									}
									slabsList.add(vomsBatchVO);
								}
								
								if (!BTSLUtil.isNullString(networkCode)) {
					                NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(networkCode);
					                if(networkVO==null){
					                messageArray[0] = networkCode;
					               throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID, messageArray);
					                }
					            }
								else
								{
									 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK, 0,null,null);
								}
								boolean validateuser = false;
								PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
								validateuser = pretupsRestUtil.validateUser(identifiertype, identifiervalue, networkCode, con);
								if(validateuser == false){
									throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_USER, 0,null,null);
								}
								final UserDAO userDAO = new UserDAO();
								final ChannelUserDAO _channelUserDAO = new ChannelUserDAO(); 
								 if(PretupsI.MSISDN.equalsIgnoreCase(identifiertype)){
									 if("O2C".equals(transferType)) {
										 LoginDAO _loginDAO = new LoginDAO();
										 sessionUserVO = (UserVO) _loginDAO.loadUserDetails(con,  oAuthUser.getData().getLoginid(),  oAuthUser.getData().getPassword(), new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
									            
									 }else {
										 sessionUserVO = (UserVO) userDAO.loadUserDetailsByMsisdn(con,identifiervalue);
									 }
						            	if(sessionUserVO==null){
											throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
										}
										if(PretupsI.USER_STATUS_CANCELED.equalsIgnoreCase(sessionUserVO.getStatus())||PretupsI.USER_STATUS_DELETED.equalsIgnoreCase(sessionUserVO.getStatus()) ){
											throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
										}
						            }
								 
								 
						            else if(PretupsI.LOGINID.equalsIgnoreCase(identifiertype)){
						            	sessionUserVO = (UserVO) userDAO.loadAllUserDetailsByLoginID(con,identifiervalue);
						            	//adding code for staff
						            	if(sessionUserVO.getUserType().equals(TypesI.STAFF_USER_TYPE)) {
						            		ChannelUserVO channelUserVO = _channelUserDAO.loadStaffUserDetailsByLoginId(con, sessionUserVO.getLoginID());
						            		settingStaffDetails(channelUserVO);
						            		if(channelUserVO.getUserPhoneVO().getMsisdn()==null) {//means staff has no msisdn
						                		UserPhoneVO parentPhoneVO = userDAO.loadUserPhoneVO(con, channelUserVO.getUserID());//getting parent User phoneVO
						                		channelUserVO.setUserPhoneVO(parentPhoneVO);
						                	}
						            		sessionUserVO = channelUserVO;
						            	}
						            	if(sessionUserVO==null){
						            		throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
										}
						            }
								 
								
								
							
								validateSlabListDetails(slabsList,voucherErrorList,requestType,networkCode);
								 if (voucherErrorList != null && !voucherErrorList.isEmpty()) {
						                final String[] array = { BTSLUtil.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), voucherErrorList) };
						                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG, 0, array, null);
						            }
								 
									validateSlabListForSerialNo(con,slabsList,voucherErrorList,unused_vouchers,networkCode,sessionUserVO.getUserID(),requestType,transferType);
								 if (voucherErrorList != null && !voucherErrorList.isEmpty()) {
						                final String[] array = { BTSLUtil.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), voucherErrorList) };
						                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG, 0, array, null);
						            }
								final NetworkProductDAO networkProductDAO = new NetworkProductDAO();
								final ArrayList<NetworkProductVO> prodList = networkProductDAO.loadProductListForXfr(con, product, networkCode);
								
								 
								if (prodList.isEmpty()) {
									throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOPRODUCT_EXIST);
								}
								final ArrayList productList = new ArrayList();
								ChannelTransferItemsVO channelTransferItemsVO1 = null;
								NetworkProductVO networkProductVO = null;
								
								int prodListSizes=prodList.size();
								for (int i = 0, j = prodListSizes; i < j; i++) {
									networkProductVO = (NetworkProductVO) prodList.get(i);
									if (networkProductVO.getStatus().equals(PretupsI.STATUS_ACTIVE)) {
										channelTransferItemsVO1 = new ChannelTransferItemsVO();
										channelTransferItemsVO1.setProductType(networkProductVO.getProductType());
										channelTransferItemsVO1.setProductCode(networkProductVO.getProductCode());
										channelTransferItemsVO1.setProductName(networkProductVO.getProductName());
										channelTransferItemsVO1.setShortName(networkProductVO.getShortName());
										channelTransferItemsVO1.setProductShortCode(networkProductVO.getProductShortCode());
										channelTransferItemsVO1.setProductCategory(networkProductVO.getProductCategory());
										channelTransferItemsVO1.setErpProductCode(networkProductVO.getErpProductCode());
										channelTransferItemsVO1.setStatus(networkProductVO.getStatus());
										channelTransferItemsVO1.setUnitValue(networkProductVO.getUnitValue());
										channelTransferItemsVO1.setModuleCode(networkProductVO.getModuleCode());
										channelTransferItemsVO1.setProductUsage(networkProductVO.getProductUsage());
										productList.add(channelTransferItemsVO1);
									}
								}
								if (productList.isEmpty()) {
									throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOTMAPPED_NETWORK);
								}
								
						
								ArrayList<VoucherSlabListDetails> slabDetailslist = new ArrayList();
								for (int i = 0; i < slabsList.size(); i++) {
									VoucherSlabListDetails voucherSlabListDetails = new VoucherSlabListDetails();
									final VomsBatchVO vomsBatchVO = (VomsBatchVO) slabsList.get(i);
									if (vomsBatchVO.getProductlist() != null && vomsBatchVO.getProductlist().size() > 0) {
									final double denomination = Double.parseDouble(vomsBatchVO.getDenomination());
									final Long quantity;
									if(requestType.equals("TRANSFER")) {
									 quantity = (Long.parseLong(vomsBatchVO.getToSerialNo())) - (Long.parseLong(vomsBatchVO.getFromSerialNo())) + 1 - unused_vouchers;
									} else {
								     quantity = Long.parseLong(vomsBatchVO.getQuantity());
									}
									final double requestedMrp = denomination * quantity;
									voucherSlabListDetails.setSalbAmount(String.valueOf(requestedMrp));
									voucherSlabListDetails.setSlabQty(String.valueOf(quantity));
									slabDetailslist.add(voucherSlabListDetails);
									vomsBatchVO.setQuantity(String.valueOf(quantity));
									totalVoucherQty = totalVoucherQty+quantity;//number of vouchers in total
									totalRequestedQuantity = totalRequestedQuantity + requestedMrp; // totoal mrp*qty
									int fromArrayLists=productList.size();
									for (int j = 0, k = fromArrayLists; j < k; j++) {
										channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(j);
										if (product.equalsIgnoreCase(channelTransferItemsVO.getProductType()) && VOMSI.DEFAULT_PRODUCT_CODE_VCR.equalsIgnoreCase(channelTransferItemsVO.getProductCode())){
											tempChannelTransferItemsVO = (ChannelTransferItemsVO) channelTransferItemsVO.clone();
											tempChannelTransferItemsVO.setRequestedQuantity(Double.toString(totalRequestedQuantity));
											tempChannelTransferItemsVO.setVoucherQuantity(totalVoucherQty);
											vomsBatchVO.setChannelTransferItemsVO(tempChannelTransferItemsVO);
											//vomsBatchVO.setProductName(((VomsProductVO)(vomsBatchVO.getProductlist().get(0))).getProductName());
											break;
										}
								    }
						        }
						     }
								
								itemsList.add(tempChannelTransferItemsVO);
								
								final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
								channelTransferVO.setChannelTransferitemsVOList(itemsList);
								channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER);
								channelTransferVO.setDualCommissionType(dualCommission);
								if(PretupsI.TRANSFER_TYPE_C2C.equals(transferType))
								channelTransferVO.setType(PretupsI.TRANSFER_TYPE_C2C);
								else
								channelTransferVO.setType(PretupsI.TRANSFER_TYPE_O2C);	
								if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode())){ 
									if(cbcflag.equalsIgnoreCase("Y"))
							        {
										ChannelTransferBL.increaseOptOTFCounts(con, channelTransferVO); 
							        	channelTransferVO.setOtfFlag(true);
							        }
							        else {
							            channelTransferVO.setOtfFlag(false);	
							        }
								} else {
									channelTransferVO.setOtfFlag(false);
								}
								channelTransferVO.setPaymentInstType(c2CVoucherTransferDetailsData.getPaymentInfo());
								channelTransferVO.setPayInstrumentType(c2CVoucherTransferDetailsData.getPaymentInfo());
								
								
								ChannelTransferBL.loadAndCalculateTaxOnDenominations(con, commissionProfileID, commissionProfileVersion,
										channelTransferVO, false, null, channelTransferVO.getType());
							
								ChannelTransferItemsVO transferItemsVO = null;
								
								ArrayList<ChannelVoucherTransferDetails> detailsList = new 	ArrayList<ChannelVoucherTransferDetails>();
								int itemsLists = itemsList.size();
								
								for (int k = 0; k < itemsLists; k++) {
									ChannelVoucherTransferDetails transferItemsVOTemp = new ChannelVoucherTransferDetails();
									transferItemsVO = (ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOList().get(k);
									totTax1 += transferItemsVO.getTax1Value();
									totTax2 += transferItemsVO.getTax2Value();
									totTax3 += transferItemsVO.getTax3Value();
									totcommission += transferItemsVO.getCommValue();
									if (transferItemsVO.getRequestedQuantity() != null && BTSLUtil.isDecimalValue(transferItemsVO.getRequestedQuantity())) {
										totRequestedQty += PretupsBL.getSystemAmount(transferItemsVO.getRequestedQuantity());
										if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {

											totTransferedAmt += transferItemsVO.getReceiverCreditQty() * Long.parseLong(PretupsBL.getDisplayAmount(transferItemsVO.getUnitValue()));
										} else {
											totTransferedAmt += (Double.parseDouble(transferItemsVO.getRequestedQuantity()) * transferItemsVO.getUnitValue());
										}
									}
									payableAmount += transferItemsVO.getPayableAmount();
									netPayableAmt += transferItemsVO.getNetPayableAmount();
									totalMRP += transferItemsVO.getProductTotalMRP();
									commissionQty += transferItemsVO.getCommQuantity();
									senderDebitQty += transferItemsVO.getSenderDebitQty();
									receiverCreditQty += transferItemsVO.getReceiverCreditQty();
									otfValue +=transferItemsVO.getOtfAmount();
									transferItemsVOTemp.setCommRate(transferItemsVO.getCommRate());
									transferItemsVOTemp.setCommType(transferItemsVO.getCommType());
									transferItemsVOTemp.setCommValue(PretupsBL.getDisplayAmount(transferItemsVO.getCommValue()));
									transferItemsVOTemp.setNetPayableAmount(PretupsBL.getDisplayAmount(transferItemsVO.getNetPayableAmount()));
									transferItemsVOTemp.setPayableAmount(PretupsBL.getDisplayAmount(transferItemsVO.getPayableAmount()));
									transferItemsVOTemp.setProductCode(transferItemsVO.getProductCode());
									transferItemsVOTemp.setReceiverCreditQty(PretupsBL.getDisplayAmount(transferItemsVO.getReceiverCreditQty()));
									transferItemsVOTemp.setRequestedQty(PretupsBL.getDisplayAmount(PretupsBL.getSystemAmount(transferItemsVO.getRequestedQuantity())));
									transferItemsVOTemp.setSenderDebitQty(PretupsBL.getDisplayAmount(transferItemsVO.getSenderDebitQty()));
									transferItemsVOTemp.setTax1Rate(transferItemsVO.getTax1Rate());
									transferItemsVOTemp.setTax1Type(transferItemsVO.getTax1Type());
									transferItemsVOTemp.setTax1Value(PretupsBL.getDisplayAmount(transferItemsVO.getTax2Value()));
									transferItemsVOTemp.setTax2Rate(transferItemsVO.getTax2Rate());
									transferItemsVOTemp.setTax2Type(transferItemsVO.getTax2Type());
									transferItemsVOTemp.setTax2Value(PretupsBL.getDisplayAmount(transferItemsVO.getTax2Value()));
									transferItemsVOTemp.setTax3Rate(transferItemsVO.getTax3Rate());
									transferItemsVOTemp.setTax3Type(transferItemsVO.getTax3Type());
									transferItemsVOTemp.setTax3Value(PretupsBL.getDisplayAmount(transferItemsVO.getTax3Value()));
									transferItemsVOTemp.setCbcRate(transferItemsVO.getOtfRate());
									transferItemsVOTemp.setCbcAmount(transferItemsVO.getOtfAmount());
									transferItemsVOTemp.setOtfTypePctOrAMt(transferItemsVO.getOtfTypePctOrAMt());
									detailsList.add(transferItemsVOTemp);
								}
								response1.setTotalVoucherOrderAmount(tempChannelTransferItemsVO.getRequestedQuantity());
								response1.setTotalVoucherOrderQuantity(String.valueOf(tempChannelTransferItemsVO.getVoucherQuantity()));
								response1.setTotalMRP(PretupsBL.getDisplayAmount(totalMRP));
								response1.setTotalNetPayableAmount(PretupsBL.getDisplayAmount(netPayableAmt));
								response1.setTotalPayableAmount(PretupsBL.getDisplayAmount(payableAmount));
								response1.setTotalReqQty(PretupsBL.getDisplayAmount(totRequestedQty));
								response1.setTotalTransferedAmount(PretupsBL.getDisplayAmount(totTransferedAmt));
								response1.setTotalTax1(PretupsBL.getDisplayAmount(totTax1));
								response1.setTotalTax2(PretupsBL.getDisplayAmount(totTax2));
								response1.setTotalTax3(PretupsBL.getDisplayAmount(totTax3));
								response1.setTotalComm(PretupsBL.getDisplayAmount(totcommission));
								response1.setCommissionQuantity(PretupsBL.getDisplayAmount(commissionQty));
								response1.setSenderDrQty(PretupsBL.getDisplayAmount(senderDebitQty));
								response1.setReceiverCrQty(PretupsBL.getDisplayAmount(receiverCreditQty));
								response1.setTotalOtfValue(PretupsBL.getDisplayAmount(otfValue));
							    response1.setTansferProductdetailList(detailsList);
							    response1.setSlabDetails(slabDetailslist);
					            response1.setStatus(PretupsI.RESPONSE_SUCCESS);
					            response1.setMessageCode(PretupsErrorCodesI.SUCCESS);
					            String resmsg  = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.SUCCESS, null);
					            response1.setMessage(resmsg);
								
							} catch (BTSLBaseException be) {
					        	 log.error(methodName, "Exception:e=" + be);
					             log.errorTrace(methodName, be);
					             if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
					             		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
					             	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
					             	 response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
					             }
					              else{
					              response.setStatus(HttpStatus.SC_BAD_REQUEST);
					              response1.setStatus(HttpStatus.SC_BAD_REQUEST);
					              }
					             if(be.getMessage().equals("5022")) {
					            	 StringBuffer str=new StringBuffer();
					            	 response1.setMessageCode(be.getMessage());
					            	 Iterator it =voucherErrorList.iterator();
					            	 while(it.hasNext()) {
					            		 KeyArgumentVO keyArgumentVO  = (KeyArgumentVO) it.next();
					            		   String resmsg  = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), keyArgumentVO.getKey(), keyArgumentVO.getArguments());
					            		   if(it.hasNext()) {
					            			   str.append(resmsg).append("|");
					            		   }
					            		   else {
					            			   str.append(resmsg);
					            		   }
					            	 }
					        		
					        		 messageArray[0] = str.toString();
							     	 response1.setMessage(messageArray[0]);
					             }else {
					     	   String resmsg  = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessage(), messageArray);
					     	   response1.setMessageCode(be.getMessage());
					     	   response1.setMessage(resmsg);
					           }
						  }
					        catch (Exception e) {
					        	 response.setStatus(HttpStatus.SC_BAD_REQUEST);
					        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
					            log.error(methodName, "Exception:e=" + e);
					            log.errorTrace(methodName, e);
					         	  response1.setStatus(PretupsI.RESPONSE_FAIL);
					         	  
					        } finally {
					            try {
					            	if (mcomCon != null) {
					    				mcomCon.close("UserAssociateProfileController#processCP2PUserRequest");
					    				mcomCon = null;
					    			}
					            } catch (Exception e) {
					                log.errorTrace(methodName, e);
					            }
					            if (log.isDebugEnabled()) {
					                log.debug(methodName, " Exited ");
					            }
					        }
							return response1;
						}
	
	void validateSlabListDetails(ArrayList<VomsBatchVO> slabsList,ArrayList voucherErrorList,String requestType,String extNwcode) {
		  long quantity = 0L;
          double denomination = 0;
          VomsProductVO vomsProductVO = null;
          final Map<String, String> denomMap = new HashMap<String, String>();
          String METHOD_NAME = "validateSlabListDetails";
         
		if(slabsList!=null && slabsList.size() > 0) {
			 for (int i = 0; i < slabsList.size(); i++) {
		 VomsBatchVO vomsBatchVO = (VomsBatchVO) slabsList.get(i);
		 denomination = 0;
		 
		 if(BTSLUtil.isNullString(vomsBatchVO.getVoucherType())) {
			 KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
          	keyArgumentVO.setKey(PretupsErrorCodesI.VTYPE_REQ);
          	keyArgumentVO.setArguments(i + 1 + "");
          	voucherErrorList.add(keyArgumentVO);
          	continue;
		 }
         
         if(BTSLUtil.isNullString(vomsBatchVO.getSegment())) {
        	 KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
          	keyArgumentVO.setKey(PretupsErrorCodesI.SEG_REQ);
          	keyArgumentVO.setArguments(i + 1 + "");
          	voucherErrorList.add(keyArgumentVO);
          	continue;
         }
         if (!BTSLUtil.isNullString(vomsBatchVO.getDenomination())) {
             try {
                 denomination = Double.parseDouble(vomsBatchVO.getDenomination());
             } catch (Exception e) {
             	log.errorTrace(METHOD_NAME, e);
               KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
             	keyArgumentVO.setKey(PretupsErrorCodesI.DNEO_NOT_NUMERIC);
             	keyArgumentVO.setArguments(i + 1 + "");
             	voucherErrorList.add(keyArgumentVO);
             }
         } 
         if(requestType.equals("TRANSFER")) {
         if (!BTSLUtil.isNullString(vomsBatchVO.getFromSerialNo())) 
         {
             try {
                 quantity = Long.parseLong(vomsBatchVO.getFromSerialNo()); 
             } catch (Exception e) {
             	log.errorTrace(METHOD_NAME, e);
             	 KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
              	keyArgumentVO.setKey(PretupsErrorCodesI.FROM_SR_NOT_NUMERIC);
              	keyArgumentVO.setArguments(i + 1 + "");
              	voucherErrorList.add(keyArgumentVO);
             }
         }

         if (!BTSLUtil.isNullString(vomsBatchVO.getToSerialNo())) 
         {
             try {
                 quantity = Long.parseLong(vomsBatchVO.getToSerialNo());
             } catch (Exception e) {
             	log.errorTrace(METHOD_NAME, e);
             	 KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
               	keyArgumentVO.setKey(PretupsErrorCodesI.TO_SR_NOT_NUMERIC);
               	keyArgumentVO.setArguments(i + 1 + "");
               	voucherErrorList.add(keyArgumentVO);
             }
         }
         if (!BTSLUtil.isNullString(vomsBatchVO.getDenomination()) && BTSLUtil.isNullString(vomsBatchVO.getFromSerialNo())) 
         {
        	 KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
            	keyArgumentVO.setKey(PretupsErrorCodesI.FROM_SR_REQ);
            	keyArgumentVO.setArguments(i + 1 + "");
            	voucherErrorList.add(keyArgumentVO);
         }
         if (!BTSLUtil.isNullString(vomsBatchVO.getFromSerialNo()) && BTSLUtil.isNullString(vomsBatchVO.getDenomination())) {
        	 KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
      	keyArgumentVO.setKey(PretupsErrorCodesI.DENO_REQ);
      	keyArgumentVO.setArguments(i + 1 + "");
      	voucherErrorList.add(keyArgumentVO);
         }
         if (!BTSLUtil.isNullString(vomsBatchVO.getDenomination()) && BTSLUtil.isNullString(vomsBatchVO.getToSerialNo())) 
         {
        	KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
         	keyArgumentVO.setKey(PretupsErrorCodesI.TO_SR_REQ);
         	keyArgumentVO.setArguments(i + 1 + "");
         	voucherErrorList.add(keyArgumentVO);
         }
         if (!BTSLUtil.isNullString(vomsBatchVO.getToSerialNo()) && !BTSLUtil.isNullString(vomsBatchVO.getFromSerialNo()) && BTSLUtil.isNumeric(vomsBatchVO
             .getToSerialNo()) && BTSLUtil.isNumeric(vomsBatchVO.getFromSerialNo())) {
             if (Long.parseLong(vomsBatchVO.getToSerialNo()) < Long.parseLong(vomsBatchVO.getFromSerialNo())) {
            	KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
               	keyArgumentVO.setKey(PretupsErrorCodesI.FR_SR_LESS_THAN_TO_SR);
               	keyArgumentVO.setArguments(i + 1 + "");
               	voucherErrorList.add(keyArgumentVO);
   
             }

             if (denomMap.containsKey(vomsBatchVO.getDenomination())) {
                 if (Long.parseLong(vomsBatchVO.getFromSerialNo()) <= Long.parseLong(denomMap.get(vomsBatchVO.getDenomination()))) {
                	 KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
                    	keyArgumentVO.setKey(PretupsErrorCodesI.VCR_NOT_SEQ);
                    	keyArgumentVO.setArguments(i + 1 + "");
                    	voucherErrorList.add(keyArgumentVO);
                    	continue;
                 } else {
                     denomMap.put(vomsBatchVO.getDenomination(), vomsBatchVO.getToSerialNo());
                 }
             } else {
                 denomMap.put(vomsBatchVO.getDenomination(), vomsBatchVO.getToSerialNo());
             }
         }
         }else {
        	 if (!BTSLUtil.isNullString(vomsBatchVO.getQuantity())) 
			 {
			try {
					 quantity = Long.parseLong(vomsBatchVO.getQuantity()); 
				 } catch (Exception e) {
					 log.errorTrace(METHOD_NAME, e);
					 KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
                 	keyArgumentVO.setKey(PretupsErrorCodesI.QTY_NOT_NUMERIC);
                 	keyArgumentVO.setArguments(i + 1 + "");
                 	voucherErrorList.add(keyArgumentVO);
                 	continue;
				 }
			 }

			 if (!BTSLUtil.isNullString(vomsBatchVO.getDenomination()) && BTSLUtil.isNullString(vomsBatchVO.getQuantity())) // gaurav
			 {
				 KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
              	keyArgumentVO.setKey(PretupsErrorCodesI.QTY_REQ);
              	keyArgumentVO.setArguments(i + 1 + "");
              	voucherErrorList.add(keyArgumentVO);
			 }
			 if (!BTSLUtil.isNullString(vomsBatchVO.getQuantity()) && BTSLUtil.isNullString(vomsBatchVO.getDenomination())) {
				 KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
		         	keyArgumentVO.setKey(PretupsErrorCodesI.DENO_REQ);
		         	keyArgumentVO.setArguments(i + 1 + "");
		         	voucherErrorList.add(keyArgumentVO);
			 }
         }

         if (denomination < 0) {
        	 KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
          	keyArgumentVO.setKey(PretupsErrorCodesI.DENO_LESS_THAN_ZERO);
          	keyArgumentVO.setArguments(i + 1 + "");
          	voucherErrorList.add(keyArgumentVO);
         }
         HashMap<String,Object> map  = populateMrpOnVoucherType(vomsBatchVO.getVoucherType(),extNwcode);
         final String vomsActiveMrp[] = ((String) map.get("VomsActiveMrp")).split(",");
         boolean found = false;
         int vomsLength = vomsActiveMrp.length;
         for (int index = 0; index < vomsLength; index++) {
             if (vomsActiveMrp[index].equals(Double.toString(denomination))) {
                 found = true;
                 break;
             }
         }
         if (!found) {
        	 KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
            	keyArgumentVO.setKey(PretupsErrorCodesI.INVALID_DENO);
            	keyArgumentVO.setArguments(i + 1 + "");
            	voucherErrorList.add(keyArgumentVO);
         }
         ArrayList itemlist = new ArrayList();
         if(null != map.get("VomsProductList")){
         	int vomsListSize = ((ArrayList) map.get("VomsProductList")).size();
         	
         	for (int j = 0; j < vomsListSize; j++) {
         		vomsProductVO = (VomsProductVO) ((ArrayList) map.get("VomsProductList")).get(j);
         		if (Math.abs(denomination-vomsProductVO.getMrp()) < EPSILON) {
         			itemlist.add(vomsProductVO);
         		}
         	}
         	vomsBatchVO.setVcrTypeProductlist((ArrayList) map.get("VomsProductList"));
         	if(itemlist.size() > 0)
         	vomsBatchVO.setProductlist(itemlist);
         	else {
         	vomsBatchVO.setProductlist(null);
           }
         } 
	   }
	  }
	}
	
	
	
	public HashMap<String,Object> populateMrpOnVoucherType(String voucherType,String networkCode) {
		final String methodName = "populateMrpOnVoucherType";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		HashMap<String, Object> map = new HashMap<String,Object>();
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList categoryList = null;
		VomsCategoryWebDAO vomsCategorywebDAO = null;
		VomsCategoryVO vomsCategoryVO = null;
		String genratedmrpstr = null;
		String mrp = null;
		ArrayList mrplist;
		ListValueVO lv = null;
		ArrayList vomsProductlist = null;
		VomsProductDAO vomsProductDAO = null;
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			mrplist = new ArrayList();
			vomsCategorywebDAO = new VomsCategoryWebDAO();
			vomsProductDAO = new VomsProductDAO();
            
			categoryList = vomsCategorywebDAO.loadCategoryList(con, voucherType, VOMSI.VOMS_STATUS_ACTIVE, VOMSI.EVD_CATEGORY_TYPE_FIXED, true, networkCode, null);
			map.put("VomsCategoryLis",categoryList);
			map.put("MrpList",mrplist);
			// load product for vouchertype
			vomsProductlist = vomsProductDAO.loadProductDetailsList(con, voucherType, "'" + VOMSI.VOMS_STATUS_ACTIVE + "'", false, "", null, null);
			map.put("VomsProductList",vomsProductlist);

			if (categoryList.isEmpty()) {
				throw new BTSLBaseException(PretupsErrorCodesI.NO_ACTIVE_MRP);
			}
			int categoryLists=categoryList.size();
			for (int i = 0; i < categoryLists; i++) {
				vomsCategoryVO = (VomsCategoryVO) categoryList.get(i);
				if (BTSLUtil.isNullString(genratedmrpstr)) {
					genratedmrpstr = Double.toString(vomsCategoryVO.getMrp());
					mrp = Double.toString(vomsCategoryVO.getMrp());
					lv = new ListValueVO(mrp, mrp);
					mrplist.add(lv);
				} else {
					genratedmrpstr = genratedmrpstr + "," + vomsCategoryVO.getMrp();
					mrp = Double.toString(vomsCategoryVO.getMrp());
					lv = new ListValueVO(mrp, mrp);
					mrplist.add(lv);
				}
			}

			if (mrplist.size() > 1) {
				map.put("VomsActiveMrp", null);

			} else if (mrplist.size() == 1) {
				map.put("VomsActiveMrp",(((ListValueVO) mrplist.get(0)).getValue()));

			}
			map.put("VomsActiveMrp",genratedmrpstr);
			map.put("MrpList",mrplist);
		} catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
		
		} finally {
			if(mcomCon != null)
			{
				mcomCon.close("VoucherOrderRequestAction#populateMrpOnVoucherType");
				mcomCon=null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited");
			}
		
		}
		return map;
	}

	void validateSlabListForSerialNo(Connection con, ArrayList<VomsBatchVO> slabsList,ArrayList voucherProductErrorList,long unused_vouchers,String networkCode,String userID,String reqtype, String transferType) throws BTSLBaseException{
		boolean isProductexist = false;
		for (int i = 0; i < slabsList.size(); i++) {
			isProductexist = false;
			final VomsBatchVO vomsBatchVO = (VomsBatchVO) slabsList.get(i);
		if(vomsBatchVO.getProductlist() != null && vomsBatchVO.getProductlist().size() > 0 && vomsBatchVO.getVcrTypeProductlist().size()!=0) {
			if("TRANSFER".equals(reqtype)) {
			VomsProductDAO vomsProductDAO = new VomsProductDAO();
		for (int j = 0; j <vomsBatchVO.getVcrTypeProductlist().size() ; j++) {
			VomsProductVO vomsProductVO = (VomsProductVO) vomsBatchVO.getVcrTypeProductlist().get(j);
			if (vomsProductVO.getProductID().equals(vomsBatchVO.getProductID())) {
				isProductexist = true;
				final ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
				if(PretupsI.TRANSFER_TYPE_C2C.equals(transferType)) {
					if (channelTransferWebDAO.validateVoucherSerialNoC2C(con, vomsBatchVO.getFromSerialNo(),"null",PretupsI.TRANSFER_TYPE_C2C)) {
						KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
			          	keyArgumentVO.setKey(PretupsErrorCodesI.VOMS_O2C_FROM_SERIAL_PENDING_APPROVAL);
			          	String[] args = {i + 1 + "",vomsProductVO.getProductName(),vomsBatchVO.getDenomination()};
			          	keyArgumentVO.setArguments(args);
			          	voucherProductErrorList.add(keyArgumentVO);
			          	continue;
					}
					
					//Check voucher C2S status if sold or not
					if (channelTransferWebDAO.validateVoucherSerialNoC2S(con, vomsBatchVO.getFromSerialNo())) {
						KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
			          	keyArgumentVO.setKey(PretupsErrorCodesI.VOMS_SERIAL_NO_ALREADY_SOLD_C2S);
			          	String[] args = {i + 1 + "",vomsProductVO.getProductName(),vomsBatchVO.getDenomination()};
			          	keyArgumentVO.setArguments(args);
			          	voucherProductErrorList.add(keyArgumentVO);
			          	continue;
					}
				}else {
					 if (channelTransferWebDAO.validateVoucherSerialNo(con, vomsBatchVO.getFromSerialNo(),"null",PretupsI.TRANSFER_TYPE_O2C)) {
						    KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
				          	keyArgumentVO.setKey(PretupsErrorCodesI.VOMS_O2C_FROM_SERIAL_PENDING_APPROVAL);
				          	String[] args = {i + 1 + "",vomsProductVO.getProductName(),vomsBatchVO.getDenomination()};
				          	keyArgumentVO.setArguments(args);
				          	voucherProductErrorList.add(keyArgumentVO);
				          	continue;
					 }
						
				}
				if(PretupsI.TRANSFER_TYPE_C2C.equals(transferType)) {
					if (channelTransferWebDAO.validateVoucherSerialNoC2C(con, vomsBatchVO.getToSerialNo(), "null",PretupsI.TRANSFER_TYPE_C2C)) {
						KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
			          	keyArgumentVO.setKey(PretupsErrorCodesI.VOMS_O2C_TO_SERIAL_PENDING_APPROVAL);
			          	String[] args = {i + 1 + "",vomsProductVO.getProductName(),vomsBatchVO.getDenomination()};
			          	keyArgumentVO.setArguments(args);
			          	voucherProductErrorList.add(keyArgumentVO);
			          	continue;
					}
					
					//Check voucher C2S status if sold or not
					if (channelTransferWebDAO.validateVoucherSerialNoC2S(con, vomsBatchVO.getToSerialNo())) {
						KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
			          	keyArgumentVO.setKey(PretupsErrorCodesI.VOMS_SERIAL_NO_ALREADY_SOLD_C2S);
			          	String[] args = {i + 1 + "",vomsProductVO.getProductName(),vomsBatchVO.getDenomination()};
			          	keyArgumentVO.setArguments(args);
			          	voucherProductErrorList.add(keyArgumentVO);
			          	continue;
					}
					
				}else {
					if (channelTransferWebDAO.validateVoucherSerialNo(con, vomsBatchVO.getToSerialNo(), "null",PretupsI.TRANSFER_TYPE_O2C)) {
						KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
			          	keyArgumentVO.setKey(PretupsErrorCodesI.VOMS_O2C_TO_SERIAL_PENDING_APPROVAL);
			          	String[] args = {i + 1 + "",vomsProductVO.getProductName(),vomsBatchVO.getDenomination()};
			          	keyArgumentVO.setArguments(args);
			          	voucherProductErrorList.add(keyArgumentVO);
			          	continue;
					}	
				
				}
					
				
				
				if(PretupsI.TRANSFER_TYPE_C2C.equals(transferType))
				{
				if (!channelTransferWebDAO.areAllVouchersAssociated(con, vomsBatchVO.getFromSerialNo(),vomsBatchVO.getToSerialNo(), (Long.parseLong(vomsBatchVO.getToSerialNo())) - (Long.parseLong(vomsBatchVO.getFromSerialNo())) + 1 - unused_vouchers,userID,vomsBatchVO.getVoucherType(),vomsBatchVO.getSegment(),vomsBatchVO.getDenomination(),networkCode)) {
					KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
		          	keyArgumentVO.setKey(PretupsErrorCodesI.C2C_VOMS_NOT_ASSOCIATED);
		          	String[] args = {vomsBatchVO.getFromSerialNo(),vomsBatchVO.getToSerialNo()};
		          	keyArgumentVO.setArguments(args);
		          	voucherProductErrorList.add(keyArgumentVO);
		          	continue;
				  }
				}
				if(channelTransferWebDAO.doesRangeContainMultipleProfiles(con, vomsBatchVO.getFromSerialNo(),vomsBatchVO.getToSerialNo(), (Long.parseLong(vomsBatchVO.getToSerialNo())) - (Long.parseLong(vomsBatchVO.getFromSerialNo())) + 1 - unused_vouchers,userID,vomsBatchVO.getVoucherType(),vomsBatchVO.getSegment(),vomsBatchVO.getDenomination(),networkCode)){
					KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
		          	keyArgumentVO.setKey(PretupsErrorCodesI.C2C_VOMS_MULTIPLE_PROFILES);
		          	String[] args = {vomsBatchVO.getFromSerialNo(),vomsBatchVO.getToSerialNo()};
		          	keyArgumentVO.setArguments(args);
		          	voucherProductErrorList.add(keyArgumentVO);
		          	continue;
				}
				
			}
			if(j+1 == vomsBatchVO.getVcrTypeProductlist().size() && isProductexist == false  )	{
				KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
		      	keyArgumentVO.setKey(PretupsErrorCodesI.INVAILD_VOUCHER);
		      	String[] args = {i+1+"",vomsBatchVO.getDenomination()};
		      	keyArgumentVO.setArguments(args);
		      	voucherProductErrorList.add(keyArgumentVO);
			}
		  }
		}
			
	}
	else
	{		
		KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
      	keyArgumentVO.setKey(PretupsErrorCodesI.NO_PROD_EXIST);
      	String[] args = {vomsBatchVO.getDenomination(),i+1+""};
      	keyArgumentVO.setArguments(args);
      	voucherProductErrorList.add(keyArgumentVO);
	}
  }		
}
	
    private void settingStaffDetails(ChannelUserVO channelUserVO) {

		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			channelUserVO.setActiveUserID(channelUserVO.getUserID());
			UserDAO userDao = new UserDAO();
            UserPhoneVO phoneVO = userDao.loadUserPhoneVO(con, channelUserVO.getUserID());
            if (phoneVO != null) {
                channelUserVO.setActiveUserMsisdn(phoneVO.getMsisdn());
                channelUserVO.setActiveUserPin(phoneVO.getSmsPin());
               }
            ChannelUserVO staffUserVO = new ChannelUserVO();
            UserPhoneVO staffphoneVO = new UserPhoneVO();
            BeanUtils.copyProperties(staffUserVO, channelUserVO);
            if (phoneVO != null) {
                BeanUtils.copyProperties(staffphoneVO, phoneVO);
                staffUserVO.setUserPhoneVO(staffphoneVO);
            }
            staffUserVO.setPinReset(channelUserVO.getPinReset());
            channelUserVO.setStaffUserDetails(staffUserVO);
            ChannelUserVO parentChannelUserVO = new UserDAO().loadUserDetailsFormUserID(con, channelUserVO.getParentID());
            staffUserDetails(channelUserVO, parentChannelUserVO);
            channelUserVO.setPrefixId(parentChannelUserVO.getPrefixId());
				
		}catch(Exception e) {
			
		}finally {
			if(mcomCon != null)
			{
				mcomCon.close("C2CTransferController#checkAndSetStaffVO");
				mcomCon=null;
			}
		}
		
	}
	
	protected void staffUserDetails(ChannelUserVO channelUserVO, ChannelUserVO parentChannelUserVO) {
        channelUserVO.setUserID(channelUserVO.getParentID());
        channelUserVO.setParentID(parentChannelUserVO.getParentID());
        channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
        channelUserVO.setStatus(parentChannelUserVO.getStatus());
        channelUserVO.setUserType(parentChannelUserVO.getUserType());
        channelUserVO.setStaffUser(true);
        channelUserVO.setMsisdn(parentChannelUserVO.getMsisdn());
        channelUserVO.setPinRequired(parentChannelUserVO.getPinRequired());
        channelUserVO.setSmsPin(parentChannelUserVO.getSmsPin());
        channelUserVO.setParentLoginID(parentChannelUserVO.getLoginID());
    }
	
}
