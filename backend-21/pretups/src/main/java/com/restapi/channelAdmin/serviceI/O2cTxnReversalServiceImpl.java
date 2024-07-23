package com.restapi.channelAdmin.serviceI;

import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.channelAdmin.requestVO.O2CTxnReversalListRequestVO;
import com.restapi.channelAdmin.requestVO.O2CTxnReversalRequestVO;
import com.restapi.channelAdmin.responseVO.O2CTransferDetailsResponseVO;
import com.restapi.channelAdmin.responseVO.ParentCategoryListResponseVO;
import com.restapi.channelAdmin.responseVO.O2CTxnReversalListResponseVO;
import com.restapi.channelAdmin.responseVO.OwnerListAndCUListO2cTxnRevResponseVO;
import com.restapi.channelAdmin.service.O2cTxnReversalService;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferWebDAO;
import com.web.pretups.channel.transfer.web.ChannelTransferEnquiryModel;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

@Service
public class O2cTxnReversalServiceImpl implements O2cTxnReversalService {

	public static final String classname = "O2cTxnReversalServiceImpl";

	protected final Log log = LogFactory.getLog(getClass().getName());

	@Override
	public void getO2CTxnReversalList(O2CTxnReversalListRequestVO requestVO, O2CTxnReversalListResponseVO response,
			HttpServletResponse responseSwag, OAuthUser oAuthUserData, Locale locale, String searchBy)
			throws BTSLBaseException, Exception {

		final String methodName = "getO2CTxnReversalList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		ChannelTransferDAO channelTransferDAO = null;
		String transactionID = null;
		Date fromDate = null;
		Date toDate = null;
		String toUserCode = null;
		String transferSubTypeCode = null;
		String transferCategory = null;
		String userID = null;
		String status = null;
		String productType = null;
		String geography = null;
		String catCode = null;
		ArrayList transferList = null;
		ChannelTransferVO transferVO = null;
		ChannelUserVO sessionUserVO = null;
		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			this.validategetO2CTxnReversalListRequest(con, requestVO, response, responseSwag, locale, searchBy,
					oAuthUserData);

			if (PretupsErrorCodesI.MULTI_VALIDATION_ERROR.equals(response.getMessageCode())) {
				return;
			}

			userDao = new UserDAO();
			channelTransferDAO = new ChannelTransferDAO();

			sessionUserVO = userDao.loadAllUserDetailsByLoginID(con,
					oAuthUserData.getData().getLoginid());

			if (PretupsI.SEARCH_BY_TRANSACTIONID.equalsIgnoreCase(searchBy)) {
				transactionID = requestVO.getTransactionID().trim();
				transferSubTypeCode = PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER;
				status = PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE;
				final Date currentDate = new Date();
				final int maxDays = ((Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.RVERSE_TRN_EXPIRY,
						sessionUserVO.getNetworkID())).intValue();
				fromDate = BTSLUtil.addDaysInUtilDate(currentDate, -maxDays);
				toDate = currentDate;

			} else if (PretupsI.SEARCH_BY_MSISDN.equalsIgnoreCase(searchBy)) {

				fromDate = BTSLUtil.getDateFromDateString(requestVO.getFromDate());
				toDate = BTSLUtil.getDateFromDateString(requestVO.getToDate());
				toUserCode = PretupsBL.getFilteredMSISDN(requestVO.getMsisdn().trim());
				status = PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE;
				transferSubTypeCode = PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER;
				transferCategory = requestVO.getTransferCategory();
				
				final int maxDays = ((Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.RVERSE_TRN_EXPIRY,
						sessionUserVO.getNetworkID())).intValue();
				
				final Date minfromdate = BTSLUtil.addDaysInUtilDate(new Date(), -maxDays);
				final int diffdays = BTSLUtil.getDifferenceInUtilDates(minfromdate, BTSLUtil.getDateFromDateString(requestVO.getFromDate()));
	            if (diffdays < 0) {
	            	throw new BTSLBaseException(this, methodName,"o2cchannelreversetrx.reverse.fromdate.lt.minlimit", new String[] {BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(minfromdate)) });
	             }
			} else {

//				final ArrayList prodTypListtemp = new ArrayList(sessionUserVO.getAssociatedProductTypeList());
//				OperatorUtilI _operatorUtil = null;
//	        	String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
//	            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
//	           
//	            ArrayList prodTypList= _operatorUtil.removeVMSProductCodeList(prodTypListtemp);
//
//	            ListValueVO listValueVO = null;
//	            for (int i = 0, j = prodTypList.size(); i < j; i++) {
//	                listValueVO = (ListValueVO) prodTypList.get(i);
//	                if (PretupsI.P2P_MODULE.equals(listValueVO.getValue())) {
//	                    prodTypList.remove(i);
//	                    i--;
//	                    j--;
//	                }
//	            }
//	            productType =((ListValueVO) prodTypList.get(0)).getValue();
				
				final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
				boolean sameOwner = false;
				String ownerCat = null;
				String ownerCatName = null;
				String ownerUsername=null;
				String ownerUserId=null;
				final CategoryVO categoryVO = categoryWebDAO.loadOwnerCategory(con, requestVO.getDomain());
				
				if (categoryVO != null) {
					ownerCat=categoryVO.getCategoryCode();
					ownerCatName=categoryVO.getCategoryName();
	            }
				
				if (categoryVO != null && categoryVO.getCategoryCode().equals(requestVO.getCategory())) {
					sameOwner = true;
	            }
				
				/*
                 * if user directly enter the user name, then first check
                 * that owner search is required or not.
                 * if required then first owner user then after it search
                 * the user.
                 * if more than one user is there then display the error
                 * message.
                 */

                // check the owner information if exist i.e. selectd
                // category is not owner's category
				ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
				String userName=null;
                if (!sameOwner) {
                    userName = requestVO.getOwnerUsername();
                    if (!BTSLUtil.isNullString(userName)) {
                        userName = "%" + userName + "%";
                    }

                    final ArrayList userList = channelUserWebDAO.loadUsersForEnquiry(con, ownerCat, sessionUserVO.getNetworkID(), userName, null,
                        requestVO.getGeography(), sessionUserVO.getUserID(), false);
                    
                    userName = requestVO.getOwnerUsername();
                    ListValueVO listValueVO =null;
                    if (userList.size() == 1) {
                        listValueVO = (ListValueVO) userList.get(0);
                        ownerUsername=listValueVO.getLabel();
                        ownerUserId=listValueVO.getValue();
                    } else if (userList.size() > 1) {
                        boolean isExist = false;
                        if (!BTSLUtil.isNullString(requestVO.getOwnerUserId())) {
                            for (int i = 0, k = userList.size(); i < k; i++) {
                                listValueVO = (ListValueVO) userList.get(i);
                                if (listValueVO.getValue().equals(requestVO.getOwnerUserId()) && (userName.compareTo(
                                    listValueVO.getLabel()) == 0)) {
                                	ownerUsername=listValueVO.getLabel();
                                	ownerUserId=listValueVO.getValue();
                                    isExist = true;
                                    break;
                                }
                            }
                        }else {
                            ListValueVO listValueNextVO = null;
                            for (int i = 0, k = userList.size(); i < k; i++) {
                                listValueVO = (ListValueVO) userList.get(i);
                                if (userName.compareTo(listValueVO.getLabel()) == 0) {
                                    if (((i + 1) < k)) {
                                        listValueNextVO = (ListValueVO) userList.get(i + 1);
                                        if (userName.compareTo(listValueNextVO.getLabel()) == 0) {
                                            isExist = false;
                                            break;
                                        }
                                        ownerUsername=listValueVO.getLabel();
                                        ownerUserId=listValueVO.getValue();
                                        isExist = true;
                                        break;
                                    }
                                    ownerUsername=listValueVO.getLabel();
                                    ownerUserId=listValueVO.getValue();
                                    isExist = true;
                                    break;
                                }
                            }
                        }
                        if (!isExist) {
                     
                            throw new BTSLBaseException(this, methodName,"message.channeltransfer.usermorethanoneexist.msg", new String[] { userName });
                        }
                    } else {
                      
                        throw new BTSLBaseException(this, methodName,"message.channeltransfer.usernotfound.msg", new String[] { userName });
                        
                    }
                }

                
                String channelUsername=null;
                String channelUserId=null;
                userName = requestVO.getOwnerUsername();
                if (!BTSLUtil.isNullString(userName)) {
                    userName = "%" + userName + "%";
                }
                final ArrayList userList = channelUserWebDAO.loadUsersForEnquiry(con, requestVO.getCategory(), sessionUserVO.getNetworkID(), userName, requestVO.getOwnerUserId(), requestVO.getGeography(), sessionUserVO.getUserID(), false);
                
                ListValueVO listValueVO=null;
                if (userList.size() == 1) {
                    listValueVO = (ListValueVO) userList.get(0);
                    channelUsername=listValueVO.getLabel();
                    channelUserId=listValueVO.getValue();
                } else // display this message only when user doesnot come
                       // from search user screen.
                if (userList.size() > 1) {
                    boolean isExist = false;
                    if (!BTSLUtil.isNullString(requestVO.getUserId())) {
                        for (int i = 0, k = userList.size(); i < k; i++) {
                            listValueVO = (ListValueVO) userList.get(i);
                            if (listValueVO.getValue().equals(requestVO.getUserId()) && (requestVO.getUserName().compareTo(listValueVO.getLabel()) == 0)) {
                            	channelUsername=listValueVO.getLabel();
                                channelUserId=listValueVO.getValue();
                                isExist = true;
                                break;
                            }
                        }
                    } else {
                        ListValueVO listValueVONext = null;
                        for (int i = 0, k = userList.size(); i < k; i++) {
                            listValueVO = (ListValueVO) userList.get(i);
                            if (requestVO.getUserName().compareTo(listValueVO.getLabel()) == 0) {
                                if (((i + 1) < k)) {
                                    listValueVONext = (ListValueVO) userList.get(i + 1);
                                    if (requestVO.getUserName().compareTo(listValueVONext.getLabel()) == 0) {
                                        isExist = false;
                                        break;
                                    }
                                    channelUsername=listValueVO.getLabel();
                                    channelUserId=listValueVO.getValue();
                                    isExist = true;
                                    break;
                                }
                                channelUsername=listValueVO.getLabel();
                                channelUserId=listValueVO.getValue();
                                isExist = true;
                                break;
                            }
                        }
                    }
                    if (!isExist) {
                        throw new BTSLBaseException(this, methodName,"message.channeltransfer.usermorethanoneexist.msg", new String[] { requestVO.getUserName() });
                    }
                } else {
                    throw new BTSLBaseException(this, methodName,"message.channeltransfer.usernotfound.msg", new String[] { requestVO.getUserName() });
                }
            
			
			
			
				productType = "PREPROD";
				fromDate = BTSLUtil.getDateFromDateString(requestVO.getFromDate());
				toDate = BTSLUtil.getDateFromDateString(requestVO.getToDate());
				userID = requestVO.getUserId();
				status = PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE;
				transferSubTypeCode = PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER;
				transferCategory = requestVO.getTransferCategory();
				catCode = requestVO.getCategory();
				geography = requestVO.getGeography();
				
				final int maxDays = ((Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.RVERSE_TRN_EXPIRY,
						sessionUserVO.getNetworkID())).intValue();
				
				final Date minfromdate = BTSLUtil.addDaysInUtilDate(new Date(), -maxDays);
				final int diffdays = BTSLUtil.getDifferenceInUtilDates(minfromdate, BTSLUtil.getDateFromDateString(requestVO.getFromDate()));
	            if (diffdays < 0) {
	            	throw new BTSLBaseException(this, methodName,"o2cchannelreversetrx.reverse.fromdate.lt.minlimit", new String[] {BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(minfromdate)) });
	             }
			}

			transferList = channelTransferDAO.loadO2CChannelTransfersList(con, transactionID, userID, fromDate, toDate,
					status, transferSubTypeCode, productType, transferCategory, toUserCode, catCode, geography);

			if (transferList.size() == 1 && !BTSLUtil.isNullString(transactionID)) {
				transferVO = new ChannelTransferVO();
				transferVO = (ChannelTransferVO) transferList.get(0);
				if (transferVO != null)
					if (transferVO.getTransactionMode().equalsIgnoreCase(PretupsI.SOS_TRANSACTION_MODE)) {

						throw new BTSLBaseException(this, methodName, "channelreversetrx.trxtype.sos");
					} else if (transferVO.getTransactionMode().equalsIgnoreCase(PretupsI.LR_TRANSACTION_MODE)) {

						throw new BTSLBaseException(this, methodName, "channelreversetrx.trxtype.last.recharge");
					}
			} else if (transferList != null && !transferList.isEmpty()) {
				ArrayList tempList = new ArrayList();
				for (int i = 0, j = transferList.size(); i < j; i++) {
					transferVO = new ChannelTransferVO();
					transferVO = (ChannelTransferVO) transferList.get(i);
					if (!transferVO.getTransactionMode().equalsIgnoreCase(PretupsI.SOS_TRANSACTION_MODE)
							&& !transferVO.getTransactionMode().equalsIgnoreCase(PretupsI.LR_TRANSACTION_MODE)) {
						tempList.add(transferVO);
					}
				}
				transferList = new ArrayList();
				transferList.addAll(tempList);
			}

//			check tranferList is empty
			if (BTSLUtil.isNullOrEmptyList(transferList)) {
				String message;
				if (PretupsI.SEARCH_BY_TRANSACTIONID.equalsIgnoreCase(searchBy)) {
					message = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.O2C_TRANSFER_REVERSAL_ENQUIRY, new String[]{requestVO.getTransactionID().trim()});
				}else if (PretupsI.SEARCH_BY_MSISDN.equalsIgnoreCase(searchBy)) {
					message = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.O2C_TRANSFER_REVERSAL_ENQUIRY_MSISDN, new String[]{requestVO.getMsisdn().trim()});
				}else{
					message = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.O2C_TRANSFER_REVERSAL_ENQUIRY_ADVANCED, null);
				}


				response.setMessage(message);
				response.setO2CTxnReversalListSize(0);
				response.setO2CTxnReversalList(transferList);
				response.setStatus(Integer.toString(HttpStatus.SC_OK));

				responseSwag.setStatus(HttpStatus.SC_OK);
				return;
			}

			// now if user entered the txnID direct and try to view the detail
			// of the txn then check the channel domain
			// and the geographical domain of the txn by the loggedIN user
			else if (sessionUserVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)
					&& (!BTSLUtil.isNullString(transactionID) || !BTSLUtil.isNullString(toUserCode))) {
				transferVO = (ChannelTransferVO) transferList.get(0);
				// to check the domain of the user with the domain of the logged
				// in user

				String message = null;
				ListValueVO listValueVO = null;
				boolean domainfound = false;
				final ArrayList domainList = new DomainDAO().loadDomainListByUserId(con,
						oAuthUserData.getData().getUserid());
				for (int i = 0, j = domainList.size(); i < j; i++) {
					listValueVO = (ListValueVO) domainList.get(i);
					if (transferVO.getDomainCode().equals(listValueVO.getValue())) {
						domainfound = true;
						break;
					}
				}
				if (!domainfound) {

					if (!BTSLUtil.isNullString(transactionID)) {
						message = RestAPIStringParser.getMessage(locale,
								"o2cenquiry.viewo2ctransfers.msg.usernotindomain", new String[] { transactionID });
					} else if (!BTSLUtil.isNullString(toUserCode)) {
						message = RestAPIStringParser.getMessage(locale,
								"o2cenquiry.viewo2ctransfers.msg.usernotindomainbyucode", new String[] { toUserCode });
					}

					response.setMessage(message);
					response.setStatus(Integer.toString(HttpStatus.SC_BAD_REQUEST));
					responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
					return;
				}

				// now check that is user down in the geographical domain of the
				// loggin user or not.

				final GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
				if (!geographicalDomainDAO.isGeoDomainExistInHierarchy(con, transferVO.getGraphicalDomainCode(),
						sessionUserVO.getUserID())) {
					if (!BTSLUtil.isNullString(transactionID)) {
						throw new BTSLBaseException(this, methodName,
								"o2cenquiry.viewo2ctransfers.msg.usernotdowngeogrphy", new String[] { transactionID });
					} else if (!BTSLUtil.isNullString(transactionID)) {
						throw new BTSLBaseException(this, methodName,
								"o2cenquiry.viewo2ctransfers.msg.usernotdowngeogrphybyucode",
								new String[] { transactionID });
					}
				}
			}
			// check inquiry allowed for given transaction ID(user)

//			isMatched = false;
//
//			for (int m = 0, n = transferList.size(); m < n; m++) {
//				transferVO = (ChannelTransferVO) transferList.get(m);
//				isMatched = false;
//				for (int i = 0, j = hierarchyList.size(); i < j; i++) {
//					channelUserVO = (ChannelUserVO) hierarchyList.get(i);
//					if (channelUserVO.getUserID().equals(transferVO.getToUserID())
//							|| channelUserVO.getUserID().equals(transferVO.getFromUserID())) {
//						isMatched = true;
//						break;
//					}
//				}
//				if (!isMatched) {
//					throw new BTSLBaseException(this, methodName, "o2cenquiry.viewo2ctransfers.msg.notauthorize");
//				}
//			}

//			response:
			int transferListSize=transferList.size();
			response.setMessage(PretupsI.SUCCESS);
			response.setO2CTxnReversalListSize(transferListSize);
			response.setO2CTxnReversalList(transferList);
			response.setStatus(Integer.toString(HttpStatus.SC_OK));

			responseSwag.setStatus(HttpStatus.SC_OK);

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			throw be;
		} catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			throw e;
		} finally {
			// if connection is not null then close the connection

			try {
				if (mcomCon != null) {
					mcomCon.close("O2cTxnReversalServiceImpl#getO2CTxnReversalList");
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
				log.debug(methodName, "Exited");
			}
		}

	}

	private void validategetO2CTxnReversalListRequest(Connection con, O2CTxnReversalListRequestVO requestVO,
			O2CTxnReversalListResponseVO response, HttpServletResponse responseSwag, Locale locale, String searchBy,
			OAuthUser oAuthUserData) throws BTSLBaseException {
		// basic form validation at api level
		final String methodName = "validategetO2CTxnReversalListRequest";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}

		MasterErrorList masterError = null;

		ErrorMap errorMap = new ErrorMap();

		new ArrayList();
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();

		if (PretupsI.SEARCH_BY_TRANSACTIONID.equalsIgnoreCase(searchBy)) {
			if (BTSLUtil.isNullString(requestVO.getTransactionID())) {
				masterError = new MasterErrorList();
				masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
						new String[] { "Transaction ID" });
				masterError.setErrorMsg(msg);
				masterErrorLists.add(masterError);
			}
		} else if (PretupsI.SEARCH_BY_MSISDN.equalsIgnoreCase(searchBy)) {

			if (BTSLUtil.isNullString(requestVO.getMsisdn())) {
				masterError = new MasterErrorList();
				masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
						new String[] { "Mobile Number" });
				masterError.setErrorMsg(msg);
				masterErrorLists.add(masterError);
			}

			if (BTSLUtil.isNullString(requestVO.getTransferCategory())) {
				masterError = new MasterErrorList();
				masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
						new String[] { "Transfer Category" });
				masterError.setErrorMsg(msg);
				masterErrorLists.add(masterError);
			}

			if (!BTSLUtil.isNullString(requestVO.getTransferCategory())) {

				ArrayList<ListValueVO> transferCategoryList = LookupsCache
						.loadLookupDropDown(PretupsI.TRANSFER_TYPE_FOR_TRFRULES, true);

				boolean transferCategoryFound = false;
				for (ListValueVO transferCategory : transferCategoryList) {
					if (transferCategory.getValue().equals(requestVO.getTransferCategory())) {
						transferCategoryFound = true;
						break;
					}
				}

				if (!transferCategoryFound) {
					masterError = new MasterErrorList();
					masterError.setErrorCode(PretupsErrorCodesI.INVALID_TRF_CATEGORY);
					String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_TRF_CATEGORY,
							new String[] { "" });
					masterError.setErrorMsg(msg);
					masterErrorLists.add(masterError);
				}
			}

			if (BTSLUtil.isNullString(requestVO.getFromDate())) {
				masterError = new MasterErrorList();
				masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
						new String[] { "From date" });
				masterError.setErrorMsg(msg);
				masterErrorLists.add(masterError);
			}
			if (BTSLUtil.isNullString(requestVO.getToDate())) {
				masterError = new MasterErrorList();
				masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
						new String[] { "To date" });
				masterError.setErrorMsg(msg);
				masterErrorLists.add(masterError);
			}
		} else if (PretupsI.SEARCH_BY_ADVANCE.equalsIgnoreCase(searchBy)) {

			if (BTSLUtil.isNullString(requestVO.getGeography())) {
				masterError = new MasterErrorList();
				masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
						new String[] { "Geography" });
				masterError.setErrorMsg(msg);
				masterErrorLists.add(masterError);
			}

			if (!BTSLUtil.isNullString(requestVO.getGeography())) {

				String msisdn = oAuthUserData.getData().getMsisdn();

				String filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
				String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
				NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
				String networkCode = networkPrefixVO.getNetworkCode();

				GeographicalDomainDAO geoDomainDao = new GeographicalDomainDAO();
				ArrayList<UserGeographiesVO> geoDomainList = geoDomainDao.loadUserGeographyList(con,
						oAuthUserData.getData().getUserid(), networkCode);

				boolean geoDomainFound = false;
				for (UserGeographiesVO geoDomain : geoDomainList) {
					if (geoDomain.getGraphDomainCode().equals(requestVO.getGeography())) {
						geoDomainFound = true;
						break;
					}
				}

				if (!geoDomainFound) {
					masterError = new MasterErrorList();
					masterError.setErrorCode(PretupsErrorCodesI.GRPH_INVALID_DOMAIN);
					String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GRPH_INVALID_DOMAIN,
							new String[] { "" });
					masterError.setErrorMsg(msg);
					masterErrorLists.add(masterError);
				}
			}

			if (BTSLUtil.isNullString(requestVO.getDomain())) {
				masterError = new MasterErrorList();
				masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
						new String[] { "Domain" });
				masterError.setErrorMsg(msg);
				masterErrorLists.add(masterError);
			}

			if (!BTSLUtil.isNullString(requestVO.getDomain())) {

				DomainDAO domainDao = new DomainDAO();
				ArrayList<ListValueVO> domainList = domainDao.loadDomainListByUserId(con,
						oAuthUserData.getData().getUserid());

				boolean domainFound = false;
				for (ListValueVO domain : domainList) {
					if (domain.getValue().equals(requestVO.getDomain())) {
						domainFound = true;
						break;
					}
				}

				if (!domainFound) {
					masterError = new MasterErrorList();
					masterError.setErrorCode(PretupsErrorCodesI.INVALID_DOMAIN);
					String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_DOMAIN,
							new String[] { "" });
					masterError.setErrorMsg(msg);
					masterErrorLists.add(masterError);
				}
			}

			if (BTSLUtil.isNullString(requestVO.getTransferCategory())) {
				masterError = new MasterErrorList();
				masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
						new String[] { "Transfer category" });
				masterError.setErrorMsg(msg);
				masterErrorLists.add(masterError);
			}

			if (!BTSLUtil.isNullString(requestVO.getTransferCategory())) {

				ArrayList<ListValueVO> transferCategoryList = LookupsCache
						.loadLookupDropDown(PretupsI.TRANSFER_TYPE_FOR_TRFRULES, true);

				boolean transferCategoryFound = false;
				for (ListValueVO transferCategory : transferCategoryList) {
					if (transferCategory.getValue().equals(requestVO.getTransferCategory())) {
						transferCategoryFound = true;
						break;
					}
				}

				if (!transferCategoryFound) {
					masterError = new MasterErrorList();
					masterError.setErrorCode(PretupsErrorCodesI.INVALID_TRF_CATEGORY);
					String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_TRF_CATEGORY,
							new String[] { "" });
					masterError.setErrorMsg(msg);
					masterErrorLists.add(masterError);
				}
			}

			if (BTSLUtil.isNullString(requestVO.getFromDate())) {
				masterError = new MasterErrorList();
				masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
						new String[] { "From date" });
				masterError.setErrorMsg(msg);
				masterErrorLists.add(masterError);
			}
			if (BTSLUtil.isNullString(requestVO.getToDate())) {
				masterError = new MasterErrorList();
				masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
						new String[] { "To date" });
				masterError.setErrorMsg(msg);
				masterErrorLists.add(masterError);
			}

			if (BTSLUtil.isNullString(requestVO.getCategory())) {
				masterError = new MasterErrorList();
				masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
						new String[] { "Category" });
				masterError.setErrorMsg(msg);
				masterErrorLists.add(masterError);
			}

		} else {
			String allowedSearchBy = PretupsI.SEARCH_BY_TRANSACTIONID + "," + PretupsI.SEARCH_BY_MSISDN + ","
					+ PretupsI.SEARCH_BY_ADVANCE;
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Invalid Search by, allowd options are: " + allowedSearchBy);
			}
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ALLOWED_SERACHY_BY,
					new String[] { allowedSearchBy });
		}

		errorMap.setMasterErrorList(masterErrorLists);

		if (errorMap.getMasterErrorList().size() >= 1) {

			response.setStatus(Integer.toString(HttpStatus.SC_BAD_REQUEST));
			response.setErrorMap(errorMap);
			response.setMessageCode(PretupsErrorCodesI.MULTI_VALIDATION_ERROR);
			RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MULTI_VALIDATION_ERROR,
					new String[] { errorMap.getMasterErrorList().get(0).getErrorMsg() });
			response.setMessage(errorMap.getMasterErrorList().get(0).getErrorMsg());
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);

			if (log.isDebugEnabled()) {
				log.error(methodName, "MULTI_VALIDATION_ERROR " + errorMap);

			}
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited");
		}

	}
	
	
	
	
	
	@Override
    public O2CTransferDetailsResponseVO enquiryDetail(MultiValueMap<String, String> headers, String transactionID, HttpServletResponse responseSwag) {
        final String methodName = "enquiryDetail";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
        O2CTransferDetailsResponseVO responseVO = null;
        ChannelTransferEnquiryModel theForm = null;
		UserDAO userDao = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		Locale locale = null;
        try {
        	
        	locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),
					(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			// validate token
			oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            theForm = new ChannelTransferEnquiryModel();
            responseVO = new O2CTransferDetailsResponseVO();
			userDao = new UserDAO();
			final ChannelUserVO loginUserVO = userDao.loadAllUserDetailsByLoginID(con,
					oAuthUser.getData().getLoginid());
			
			if( !PretupsI.USER_TYPE_OPERATOR.equalsIgnoreCase(loginUserVO.getUserType()) ) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NOT_AUTHORIZED_TO_REVERSE_O2C, PretupsI.RESPONSE_FAIL, null);
			}

			if(BTSLUtil.isNullString(transactionID)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TRANSFER_ID_REQUIRED, PretupsI.RESPONSE_FAIL, null);
			}else {
				transactionID = transactionID.trim();
			}
			
			final ChannelTransferVO channelTransferVO = this.loadChannelTransferDetails(con, loginUserVO, transactionID);
            this.constructFormFromVO(theForm, channelTransferVO);
            final ArrayList itemsList = ChannelTransferBL.loadChannelTransferItemsWithBalances(con, 
            		channelTransferVO.getTransferID(), channelTransferVO.getNetworkCode(),
            		channelTransferVO.getNetworkCodeFor(), channelTransferVO.getToUserID());
            
            long totTax1 = 0L, totTax2 = 0L, totTax3 = 0L, totReqQty = 0L, totStock = 0L, totComm = 0L, totMRP = 0L, totOthComm=0L, otfValue=0L;
            double mrpAmt = 0.0 , firAppQty = 0.0, secAppQty = 0.0, thrAppQty = 0.0, commissionQty = 0.0,recQty=0.0;
            if (itemsList != null && !itemsList.isEmpty()) {
                ChannelTransferItemsVO channelTransferItemsVO = null;
                for (int i = 0, j = itemsList.size(); i < j; i++) {
                    channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
                    mrpAmt = channelTransferItemsVO.getReceiverCreditQty() * channelTransferItemsVO.getUnitValue() / (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
                    channelTransferItemsVO.setProductMrpStr(PretupsBL.getDisplayAmount(mrpAmt));
                    otfValue +=channelTransferItemsVO.getOtfAmount();
                    totTax1 += channelTransferItemsVO.getTax1Value();
                    totTax2 += channelTransferItemsVO.getTax2Value();
                    totTax3 += channelTransferItemsVO.getTax3Value();
                    totComm += channelTransferItemsVO.getCommValue();
                    commissionQty += channelTransferItemsVO.getCommQuantity();
                    recQty +=channelTransferItemsVO.getReceiverCreditQty();
					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue()){
					totOthComm += channelTransferItemsVO.getOthCommValue();
					}
                    totMRP += mrpAmt;
                    totReqQty += channelTransferItemsVO.getRequiredQuantity();
                    totStock += channelTransferItemsVO.getWalletbalance();
                    if (!BTSLUtil.isNullString(channelTransferItemsVO.getFirstApprovedQuantity())) {
                        firAppQty += Double.parseDouble(channelTransferItemsVO.getFirstApprovedQuantity());
                    } else {	
                        channelTransferItemsVO.setFirstApprovedQuantity("NA");
                    }
                    if (!BTSLUtil.isNullString(channelTransferItemsVO.getSecondApprovedQuantity())) {
                        secAppQty += Double.parseDouble(channelTransferItemsVO.getSecondApprovedQuantity());
                    } else {
                        channelTransferItemsVO.setSecondApprovedQuantity("NA");
                    }
                    if (!BTSLUtil.isNullString(channelTransferItemsVO.getThirdApprovedQuantity())) {
                        thrAppQty += Double.parseDouble(channelTransferItemsVO.getThirdApprovedQuantity());
                    } else {
                        channelTransferItemsVO.setThirdApprovedQuantity("NA");
                    }
                }
            }
            theForm.setTotalComm(PretupsBL.getDisplayAmount(totComm));
            theForm.setTotalTax1(PretupsBL.getDisplayAmount(totTax1));
            theForm.setTotalTax2(PretupsBL.getDisplayAmount(totTax2));
            theForm.setTotalTax3(PretupsBL.getDisplayAmount(totTax3));
            theForm.setTotalStock(PretupsBL.getDisplayAmount(totStock));
            theForm.setTotalReqQty(PretupsBL.getDisplayAmount(totReqQty));
            theForm.setTotalMRP(PretupsBL.getDisplayAmount(totMRP));
            theForm.setTotalOthComm(PretupsBL.getDisplayAmount(totOthComm));
            theForm.setTotalOtf(PretupsBL.getDisplayAmount(otfValue));
            theForm.setCommissionQuantity(PretupsBL.getDisplayAmount(commissionQty));
            theForm.setReceiverCreditQuantity(PretupsBL.getDisplayAmount(recQty));
			if (BTSLUtil.floatEqualityCheck(firAppQty, 0d, "!=")) {
				theForm.setFirstLevelApprovedQuantity(String.valueOf(firAppQty));
            } else {
            	theForm.setFirstLevelApprovedQuantity("NA");
            }
			  if (BTSLUtil.floatEqualityCheck(secAppQty, 0d, "!=")) {
				  theForm.setSecondLevelApprovedQuantity(String.valueOf(secAppQty));
            } else {
            	theForm.setSecondLevelApprovedQuantity("NA");
            }
			  if (BTSLUtil.floatEqualityCheck(thrAppQty, 0d, "!=")) {
				  theForm.setThirdLevelApprovedQuantity(String.valueOf(thrAppQty));
            } else {
            	theForm.setThirdLevelApprovedQuantity("NA");
            }
            theForm.setTransferItemsList(itemsList);
            
            // setting response
            responseVO.setChannelTransferVO(channelTransferVO);
            responseVO.setTransferDetails(theForm);
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TXN_STATUS_SUCCESS, null);
			responseVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			responseVO.setMessage(msg);
			responseVO.setStatus(HttpStatus.SC_OK);
        } catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
			responseVO.setMessageCode(be.getMessageKey());
			responseVO.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
        } catch (ParseException e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseVO.setMessageCode(e.toString());
			responseVO.setMessage("Your request cannot be processed at this time, please try again later.");
		} catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseVO.setMessageCode(e.toString());
			responseVO.setMessage("Your request cannot be processed at this time, please try again later.");
		} finally {
			// setDatesToDisplayInForm(theForm);
			if (mcomCon != null) {
				mcomCon.close("O2cTxnReversalServiceImpl#" +methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting forward=" + responseVO);
			}
		}

		return responseVO;

    }

    
    /**
     * This method is used to construct a formbean from the VO. The formbean
     * contains the information which
     * we have to dispaly on the jsp .
     * 
     * @param theForm
     * @param p_channelTransferVO
     * @throws ParseException
     */
    private void constructFormFromVO(ChannelTransferEnquiryModel theForm, ChannelTransferVO p_channelTransferVO) throws ParseException, BTSLBaseException
    {
        if (log.isDebugEnabled()) {
            log.debug("constructFromFromVO", "Entered theForm  " + theForm + "  p_channelTransferVO  " + p_channelTransferVO);
        }
        theForm.setTransferNumberDispaly(p_channelTransferVO.getTransferID());
        theForm.setUserName(p_channelTransferVO.getToUserName());
        theForm.setDomainName(p_channelTransferVO.getDomainCodeDesc());
        theForm.setGeographicDomainName(p_channelTransferVO.getGrphDomainCodeDesc());
        theForm.setGeoDomainNameForUser(p_channelTransferVO.getGrphDomainCodeDesc());
        theForm.setStatusDetail(p_channelTransferVO.getStatusDesc());
        theForm.setPrimaryTxnNum(p_channelTransferVO.getUserMsisdn());
        theForm.setCategoryName(p_channelTransferVO.getReceiverCategoryDesc());
        theForm.setGardeDesc(p_channelTransferVO.getReceiverGradeCodeDesc());
        theForm.setErpCode(p_channelTransferVO.getErpNum());
        theForm.setProductType(p_channelTransferVO.getProductType());
        theForm.setProductTypeDesc(BTSLUtil.getOptionDesc(theForm.getProductType(), theForm.getProductsTypeList()).getLabel());
        theForm.setCommissionProfileName(p_channelTransferVO.getCommProfileName());
        theForm.setDualCommissionType(p_channelTransferVO.getDualCommissionType());
        if (p_channelTransferVO.getExternalTxnDate() != null) {
            theForm.setExternalTxnDate(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_channelTransferVO.getExternalTxnDate())));
        } else {
            theForm.setExternalTxnDate(null);
        }
        theForm.setExternalTxnNum(p_channelTransferVO.getExternalTxnNum());
        theForm.setRefrenceNum(p_channelTransferVO.getReferenceNum());
        // theForm.setRemarks(p_channelTransferVO.getChannelRemarks());

        if (!BTSLUtil.isNullString(p_channelTransferVO.getPayInstrumentType())) {
            theForm.setPaymentInstrumentName(((LookupsVO) LookupsCache.getObject(PretupsI.PAYMENT_INSTRUMENT_TYPE, p_channelTransferVO.getPayInstrumentType()))
                .getLookupName());
        } else {
            theForm.setPaymentInstrumentName(BTSLUtil.NullToString(p_channelTransferVO.getPayInstrumentType()));
        }

        theForm.setPaymentInstNum(p_channelTransferVO.getPayInstrumentNum());
        if (p_channelTransferVO.getPayInstrumentDate() != null) {
            theForm.setPaymentInstrumentDate(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_channelTransferVO.getPayInstrumentDate())));
        }
        theForm.setPaymentInstrumentAmt(PretupsBL.getDisplayAmount(p_channelTransferVO.getNetPayableAmount()));
        if (p_channelTransferVO.getTransferDate() != null) {
            theForm.setTransferDate(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_channelTransferVO.getTransferDate())));
        }
        theForm.setPayableAmount(PretupsBL.getDisplayAmount(p_channelTransferVO.getPayableAmount()));
        theForm.setNetPayableAmount(PretupsBL.getDisplayAmount(p_channelTransferVO.getNetPayableAmount()));
        // theForm.setApprove1Remark(p_channelTransferVO.getFirstApprovalRemark());
        // theForm.setApprove2Remark(p_channelTransferVO.getSecondApprovalRemark());
        // theForm.setApprove3Remark(p_channelTransferVO.getThirdApprovalRemark());
        theForm.setAddress(p_channelTransferVO.getFullAddress());
        theForm.setTransferProfileName(p_channelTransferVO.getReceiverTxnProfileName());
        // theForm.setTransferCategoryCode(p_channelTransferVO.getTransferCategory());
        theForm.setTransferCategoryDesc(BTSLUtil.getOptionDesc(p_channelTransferVO.getTransferCategory(), theForm.getTransferCategoryList()).getLabel());
        // added by amit for o2c transfer quantity change
        if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelOneApprovedQuantity())) {
            theForm.setFirstLevelApprovedQuantity(PretupsBL.getDisplayAmount(Long.parseLong(p_channelTransferVO.getLevelOneApprovedQuantity())));
        }
        if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelTwoApprovedQuantity())) {
            theForm.setSecondLevelApprovedQuantity(PretupsBL.getDisplayAmount(Long.parseLong(p_channelTransferVO.getLevelTwoApprovedQuantity())));
        }
        if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelThreeApprovedQuantity())) {
            theForm.setThirdLevelApprovedQuantity(PretupsBL.getDisplayAmount(Long.parseLong(p_channelTransferVO.getLevelThreeApprovedQuantity())));
        }
        // theForm.setGeoDomainCodeDesc(p_channelTransferVO.getGrphDomainCodeDesc());
        theForm.setChannelDomainDesc(p_channelTransferVO.getDomainCodeDesc());
        theForm.setCategoryCodeDesc(p_channelTransferVO.getReceiverCategoryDesc());
        theForm.setTrfTypeDetail(p_channelTransferVO.getTransferSubTypeValue());

        theForm.setSenderPostStock(PretupsBL.getDisplayAmount(Long.parseLong(p_channelTransferVO.getSenderPostStock())));
        theForm.setSenderPreviousStock(PretupsBL.getDisplayAmount(p_channelTransferVO.getSenderPreviousStock()));
        theForm.setReceiverPostStock(PretupsBL.getDisplayAmount(Long.parseLong(p_channelTransferVO.getReceiverPostStock())));
        theForm.setReceiverPreviousStock(PretupsBL.getDisplayAmount(p_channelTransferVO.getReceiverPreviousStock()));

        if (log.isDebugEnabled()) {
            log.debug("constructFromFromVO", "Exiting");
        }
    }
	
	
	

	@Override
	public BaseResponse reverseO2CTxn(MultiValueMap<String, String> headers, O2CTxnReversalRequestVO requestVO,
			HttpServletResponse responseSwag) {

		final String methodName = "reverseO2CTxn";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserWebDAO userwebDAO = null;
		UserDAO userDao = null;
		BaseResponse responseVO = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		Locale locale = null;
		try {

			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),
					(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			responseVO = new BaseResponse();
			// validate token
			oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			this.validateTxnRevarsalRequestVO(requestVO);

			userwebDAO = new UserWebDAO();
			final Date date = new Date();
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userDao = new UserDAO();
			// final ChannelUserVO loginUserVO = (ChannelUserVO)
			// this.getUserFormSession(request);
			final ChannelUserVO loginUserVO = userDao.loadAllUserDetailsByLoginID(con,
					oAuthUser.getData().getLoginid());
			
			if( !PretupsI.USER_TYPE_OPERATOR.equalsIgnoreCase(loginUserVO.getUserType()) ) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NOT_AUTHORIZED_TO_REVERSE_O2C, PretupsI.RESPONSE_FAIL, null);
			}
//			
			final ChannelTransferVO channelTransferVO = ChannelTransferVO.getInstance();
            final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
            final ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
            channelTransferVO.setTransferID(requestVO.getTransactionID());
            channelTransferVO.setNetworkCode(loginUserVO.getNetworkID());
            channelTransferVO.setNetworkCodeFor(loginUserVO.getNetworkID());
            channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
            channelTransferDAO.loadChannelTransfersVO(con, channelTransferVO);
            
			// validating reversal request txid
			this.validateTransactionID(channelTransferVO, loginUserVO);

			final ChannelTransferVO reverseTransferVO = new ChannelTransferVO();
			reverseTransferVO.setCreatedOn(date);
			reverseTransferVO.setNetworkCode(loginUserVO.getNetworkID());
			ChannelTransferBL.genrateO2CReversalTrx(reverseTransferVO);

			this.constructReverseVofromTxnVo(loginUserVO, channelTransferVO, reverseTransferVO, date, oAuthUser);

			ChannelTransferItemsVO channelTransferItemsVO = null;
			ChannelTransferItemsVO revItemVO = null;
			final ArrayList itemlist = ChannelTransferBL.loadChannelTransferItemsWithBalances(con,
					channelTransferVO.getTransferID(), channelTransferVO.getNetworkCode(),
					channelTransferVO.getNetworkCodeFor(), channelTransferVO.getToUserID());
			final ArrayList newlist = new ArrayList();
			int itemslist = itemlist.size();
			for (int i = 0; i < itemslist; i++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) itemlist.get(i);
				revItemVO = new ChannelTransferItemsVO();
				BeanUtils.copyProperties(revItemVO, channelTransferItemsVO);
				this.populateTransferItemsVO(revItemVO, channelTransferItemsVO);
				newlist.add(revItemVO);
			}
			reverseTransferVO.setChannelTransferitemsVOList(newlist);
			// ChannelTransferBL.calculateMRPWithTaxAndDiscount(reverseTransferVO,PretupsI.TRANSFER_TYPE_O2C);

			long totTax1 = 0L, totTax2 = 0L, totTax3 = 0L, totReqQty = 0L, totStock = 0L, totComm = 0L, totMRP = 0L;
			long totalNetPayableAmount = 0L, totalPayableAmount = 0L, commissionQty = 0, senderDebitQty = 0,
					receiverCreditQty = 0, requestedQty = 0L;
			double mrpAmt = 0.0;
			long otfAmount = 0;
			if (newlist != null && !newlist.isEmpty()) {
				for (int i = 0, j = newlist.size(); i < j; i++) {
					channelTransferItemsVO = (ChannelTransferItemsVO) newlist.get(i);
					if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
						mrpAmt += (channelTransferItemsVO.getReceiverCreditQty())
								* Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
					} else {
						mrpAmt = channelTransferItemsVO.getRequiredQuantity()
								* Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
					}
					channelTransferItemsVO.setProductMrpStr(PretupsBL.getDisplayAmount(mrpAmt));
					totTax1 += channelTransferItemsVO.getTax1Value();
					totTax2 += channelTransferItemsVO.getTax2Value();
					totTax3 += channelTransferItemsVO.getTax3Value();
					totComm += channelTransferItemsVO.getCommValue();
					totMRP += mrpAmt;
					totReqQty += channelTransferItemsVO.getRequiredQuantity();
					totStock += channelTransferItemsVO.getBalance();
					totalPayableAmount += channelTransferItemsVO.getPayableAmount();
					totalNetPayableAmount += channelTransferItemsVO.getNetPayableAmount();
					commissionQty += channelTransferItemsVO.getCommQuantity();
					receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
					// Handling of approved quantity if less than requested quantity
					if (channelTransferItemsVO.getApprovedQuantity() < channelTransferItemsVO.getRequiredQuantity()) {
						channelTransferItemsVO.setRequiredQuantity(channelTransferItemsVO.getApprovedQuantity());
					}
					if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
						channelTransferItemsVO.setSenderDebitQty(
								channelTransferItemsVO.getSenderDebitQty() + channelTransferItemsVO.getCommQuantity());
						channelTransferItemsVO.setRequiredQuantity(channelTransferItemsVO.getRequiredQuantity()
								+ channelTransferItemsVO.getCommQuantity() + channelTransferItemsVO.getOthCommValue());

					} else {
						channelTransferItemsVO.setRequiredQuantity(channelTransferItemsVO.getRequiredQuantity());
					}
					senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
					otfAmount += channelTransferItemsVO.getOtfAmount();
					reverseTransferVO.setOtfRate(channelTransferItemsVO.getOtfRate());
					reverseTransferVO.setOtfTypePctOrAMt(channelTransferItemsVO.getOtfTypePctOrAMt());
				}
			}
			reverseTransferVO.setChannelRemarks(requestVO.getRemarks());
			reverseTransferVO.setTotalTax1(totTax1);
			reverseTransferVO.setTotalTax2(totTax2);
			reverseTransferVO.setTotalTax3(totTax3);
			reverseTransferVO.setRequestedQuantity(totReqQty);
			reverseTransferVO.setPayableAmount(totalPayableAmount);
			reverseTransferVO.setNetPayableAmount(totalNetPayableAmount);
			reverseTransferVO.setTransferMRP(totMRP);
			// +ve commision Apply
			reverseTransferVO.setSenderDrQty(senderDebitQty);
			reverseTransferVO.setReceiverCrQty(receiverCreditQty);
			reverseTransferVO.setCommQty(commissionQty);
			reverseTransferVO.setOtfAmount(otfAmount);
			reverseTransferVO.setDualCommissionType(channelTransferVO.getDualCommissionType());

			if (TypesI.YES.equalsIgnoreCase(channelTransferVO.getStockUpdated())) {
				this.orderReversalProcessStart(con, reverseTransferVO, loginUserVO.getUserID(), date, "processerror");
			}

			final int updateCount = channelTransferDAO.addChannelTransfer(con, reverseTransferVO);
			if (updateCount > 0) {
				channelTransferWebDAO.updatChannelTransferAfterReverseTrx(con, reverseTransferVO);
				channelTransferWebDAO.updatChannelTransferForStockUpdate(con, reverseTransferVO);
			}

			UserEventRemarksVO remarkVO = null;
			ArrayList<UserEventRemarksVO> reversalRemarkList = null;

			if (updateCount > 0) {
				ChannelUserVO channelUserVO = ChannelUserVO.getInstance();
				final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
				// to user details: 72525252
				channelUserVO = channelUserDAO.loadChannelUserDetails(con, reverseTransferVO.getFromUserCode());
				int insertCount = 0;
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS))
						.booleanValue()) {
					reversalRemarkList = new ArrayList<>();
					remarkVO = new UserEventRemarksVO();
					remarkVO.setCreatedBy(loginUserVO.getCreatedBy());
					remarkVO.setCreatedOn(date);
					remarkVO.setEventType(PretupsI.TRANSFER_TYPE_C2S_REVERSE_EVENT_TYPE);
					remarkVO.setMsisdn(channelUserVO.getMsisdn());
					remarkVO.setRemarks(requestVO.getRemarks());
					remarkVO.setUserID(channelUserVO.getUserID());
					remarkVO.setUserType(channelUserVO.getUserType());
					remarkVO.setModule(PretupsI.C2S_MODULE);
					reversalRemarkList.add(remarkVO);
					insertCount = userwebDAO.insertEventRemark(con, reversalRemarkList);
					if (insertCount <= 0) {
						con.rollback();
						log.error("saveDeleteSuspend", "Error: while inserting into userEventRemarks Table");
						throw new BTSLBaseException(this, "save", "error.general.processing");
					}
				}
				// Addition By Babu Kunwar ends
				mcomCon.partialCommit();
				ChannelTransferBL.prepareUserBalancesListForLogger(reverseTransferVO);
				channelTransferWebDAO.updatChannelTransferItemsAfterReverseTrx(con, reverseTransferVO);
				mcomCon.finalCommit();
				
				this.pushMessage(con, reverseTransferVO, locale, responseVO);
				
				// setting response
				
				// user for withdarw user name and withdraw with user code
				if (BTSLUtil.isNullString(responseVO.getMessage())) {
					final String arr[] = { reverseTransferVO.getRefTransferID() , reverseTransferVO.getTransferID()};
					String msg = RestAPIStringParser.getMessage(locale, "o2cchannelreversetrx.reverse.msg.success",
							arr);
					if(log.isDebugEnabled()) {
						log.info(methodName, "message: "+ msg);
					}
					responseVO.setMessageCode("o2cchannelreversetrx.reverse.msg.success");
					responseVO.setMessage(msg);
				}

				responseVO.setStatus(HttpStatus.SC_CREATED);
				responseVO.setTransactionId(reverseTransferVO.getTransferID());
				
				
			}

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
			responseVO.setMessageCode(be.getMessageKey());
			responseVO.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseVO.setMessageCode(e.toString());
			responseVO.setMessage("Your request cannot be processed at this time, please try again later.");
		} finally {
			// setDatesToDisplayInForm(theForm);
			if (mcomCon != null) {
				mcomCon.close("O2cTxnReversalServiceImpl#reverseO2CTxn");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting forward=" + responseVO);
			}
		}

		return responseVO;
	}
	
	
	/**
	 * 
	 * @param con
	 * @param loginUserVO
	 * @param transactionID
	 * @param channelTransferVO
	 * @throws BTSLBaseException
	 */
	private ChannelTransferVO loadChannelTransferDetails(Connection con, ChannelUserVO loginUserVO, String transactionID ) throws BTSLBaseException {
		
		final String methodName = "loadChannelTransferDetails";
		if(log.isDebugEnabled()) {
			log.debug(methodName, "Entered with transactionID " + transactionID);
		}
		
        String transferTypeCode = PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER;
        String status = PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE;
        final Date currentDate = new Date();
        final int maxDays = ((Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.RVERSE_TRN_EXPIRY, loginUserVO.getNetworkID()))
            .intValue();
        Date fromDate = BTSLUtil.addDaysInUtilDate(currentDate, -maxDays);
        Date toDate = currentDate;
        final ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
		ArrayList transferList = channelTransferWebDAO.loadO2CChannelTransfersList(con, transactionID, null, fromDate, 
				toDate, status, transferTypeCode, null, null, null);
		
		ChannelTransferVO channelTransferVO = null;
		if(!BTSLUtil.isNullOrEmptyList(transferList) && transferList.size() > 0) {
			channelTransferVO = (ChannelTransferVO) transferList.get(0);
		} else {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_TRANSACTION_ID, PretupsI.RESPONSE_FAIL, new String[] { transactionID} , null );

		}
		
		final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
		channelTransferVO.setTransferID(transactionID);
		channelTransferVO.setNetworkCode(loginUserVO.getNetworkID());
		channelTransferVO.setNetworkCodeFor(loginUserVO.getNetworkID());
		channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
		channelTransferDAO.loadChannelTransfersVO(con, channelTransferVO);
		if(log.isDebugEnabled()) {
			log.debug(methodName, "Exiting");
		}
		
		return channelTransferVO;
	}
	
	
	/**
	 * 
	 * @param requestVO
	 * @throws BTSLBaseException
	 */
	private void validateTxnRevarsalRequestVO(O2CTxnReversalRequestVO requestVO) throws BTSLBaseException {
		final String methodName = "validateTxnRevarsalRequestVO";
		
		if(BTSLUtil.isNullString(requestVO.getTransactionID()) ) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.O2C_REVERAL_BAD_REQ, PretupsI.RESPONSE_FAIL, new String[] { "Transaction ID"}, null);
		}
		
		if(BTSLUtil.isNullString(requestVO.getRemarks())) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.O2C_REVERAL_BAD_REQ, PretupsI.RESPONSE_FAIL, new String[] { "Remarks"}, null);
		}
	}
	
	
	
	/**
	 * 
	 * @param channelTransferVO
	 * @throws BTSLBaseException
	 */
	private void validateTransactionID(ChannelTransferVO channelTransferVO, ChannelUserVO loginUserVO) throws BTSLBaseException {
		final String methodName = "validateTransactionID";
		
		// no transaction exist
		if(BTSLUtil.isNullString(channelTransferVO.getToUserID() ) ) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_TRANSACTION_ID, PretupsI.RESPONSE_FAIL, new String[] { channelTransferVO.getTransferID()} , null );
		}
		
		// check already revarsal has completed
		if(!BTSLUtil.isNullString(channelTransferVO.getReferenceID())) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REVERSED_IN_PAST, PretupsI.RESPONSE_FAIL, new String[] { channelTransferVO.getTransferID()}, null);
		}
		
		
		int reversalTxnExpiry =  (Integer)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.RVERSE_TRN_EXPIRY, loginUserVO.getNetworkID());
		
		// for CLOSED status: modified date will be equal to closed date
		if(BTSLUtil.getDifferenceInUtilDates(channelTransferVO.getModifiedOn(), new Date()) > reversalTxnExpiry) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.O2C_REVERAL_ALLOWED_DAYS, PretupsI.RESPONSE_FAIL, 
					new String[] { String.valueOf(reversalTxnExpiry)}, null);
		}
		
	}

	/**
	 * Method constructVofromForm This method is to construct VO from the FORMBEAN
	 *
	 * @param channelUserVO
	 * @param p_oldChannelTransferVO
	 * @param p_reverseTransferVO
	 * @param p_curDate
	 * @param oAuthUser
	 * @throws BTSLBaseException
	 */
	private void constructReverseVofromTxnVo(ChannelUserVO channelUserVO, ChannelTransferVO p_oldChannelTransferVO,
			ChannelTransferVO p_reverseTransferVO, Date p_curDate, OAuthUser oAuthUser) throws BTSLBaseException {
		if (log.isDebugEnabled()) {
			log.debug("constructReverseVofromTxnVo",
					"Entered old ChannelTransferVO: " + p_oldChannelTransferVO + " ChannelTransferVO: "
							+ p_reverseTransferVO + " CurDate " + p_curDate + " OAuthUser " + oAuthUser);
		}

		p_oldChannelTransferVO.setRefTransferID(p_reverseTransferVO.getTransferID());
		p_reverseTransferVO.setRefTransferID(p_oldChannelTransferVO.getTransferID());

		p_reverseTransferVO.setTransferDate(p_curDate);
		p_reverseTransferVO.setModifiedBy(channelUserVO.getActiveUserID());
		p_reverseTransferVO.setCreatedOn(p_curDate);
		p_reverseTransferVO.setModifiedOn(p_curDate);
		p_reverseTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
		p_reverseTransferVO.setTransferSubType(PretupsI.TRANSFER_TYPE_REVERSE_SUB_TYPE);

		p_reverseTransferVO.setCreatedBy(channelUserVO.getActiveUserID());
		p_reverseTransferVO.setActiveUserId(channelUserVO.getActiveUserID());
		p_reverseTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
		p_reverseTransferVO.setCloseDate(p_curDate);
		p_reverseTransferVO.setTransferInitatedBy(channelUserVO.getUserID());
		p_reverseTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);

		p_reverseTransferVO.setNetworkCodeFor(p_oldChannelTransferVO.getNetworkCodeFor());
		p_reverseTransferVO.setCategoryCode(p_oldChannelTransferVO.getReceiverCategoryCode());
		p_reverseTransferVO.setReceiverCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
		p_reverseTransferVO.setReceiverGradeCode(p_oldChannelTransferVO.getSenderGradeCode());
		p_reverseTransferVO.setSenderGradeCode(p_oldChannelTransferVO.getReceiverGradeCode());

		p_reverseTransferVO.setDomainCode(p_oldChannelTransferVO.getReceiverDomainCode());
		p_reverseTransferVO.setReceiverDomainCode(p_oldChannelTransferVO.getDomainCode());
		// p_reverseTransferVO.setDomainCode(p_oldChannelTransferVO.getDomainCode());
		// p_reverseTransferVO.setReceiverDomainCode(p_oldChannelTransferVO.getReceiverDomainCode());

		p_reverseTransferVO.setFromUserID(p_oldChannelTransferVO.getToUserID());
		p_reverseTransferVO.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
		p_reverseTransferVO.setFromUserName(p_oldChannelTransferVO.getToUserName());
		p_reverseTransferVO.setToUserName(p_oldChannelTransferVO.getFromUserName());
		p_reverseTransferVO.setToUserCode(p_oldChannelTransferVO.getFromUserCode());
		p_reverseTransferVO.setFromUserCode(p_oldChannelTransferVO.getToUserCode());

		p_reverseTransferVO.setGraphicalDomainCode(p_oldChannelTransferVO.getReceiverGgraphicalDomainCode());
		p_reverseTransferVO.setReceiverGgraphicalDomainCode(p_oldChannelTransferVO.getGraphicalDomainCode());
		// p_reverseTransferVO.setGraphicalDomainCode(p_oldChannelTransferVO.getGraphicalDomainCode());
		// p_reverseTransferVO.setReceiverGgraphicalDomainCode(p_oldChannelTransferVO.getReceiverGgraphicalDomainCode());

		p_reverseTransferVO.setSenderTxnProfile(p_oldChannelTransferVO.getReceiverTxnProfile());
		p_reverseTransferVO.setReceiverTxnProfile(null);
		p_reverseTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
		p_reverseTransferVO.setReferenceNum(p_oldChannelTransferVO.getReferenceNum());
		p_reverseTransferVO.setExternalTxnNum(p_oldChannelTransferVO.getExternalTxnNum());
		p_reverseTransferVO.setExternalTxnDate(p_oldChannelTransferVO.getExternalTxnDate());
		p_reverseTransferVO.setType(p_oldChannelTransferVO.getType());
		p_reverseTransferVO.setProductType(p_oldChannelTransferVO.getProductType());
		p_reverseTransferVO.setTransferCategoryCode(p_oldChannelTransferVO.getTransferCategoryCode());
		p_reverseTransferVO.setPayInstrumentType(p_oldChannelTransferVO.getPayInstrumentType());
		p_reverseTransferVO.setPayInstrumentNum(p_oldChannelTransferVO.getPayInstrumentNum());
		p_reverseTransferVO.setPayInstrumentDate(p_oldChannelTransferVO.getPayInstrumentDate());
		p_reverseTransferVO.setPayInstrumentAmt(p_oldChannelTransferVO.getPayInstrumentAmt());
		p_reverseTransferVO.setPaymentInstSource(p_oldChannelTransferVO.getPaymentInstSource());
		p_reverseTransferVO.setSenderLoginID(p_oldChannelTransferVO.getReceiverLoginID());
		p_reverseTransferVO.setReceiverLoginID(p_oldChannelTransferVO.getSenderLoginID());
		p_reverseTransferVO.setCommProfileSetId(p_oldChannelTransferVO.getCommProfileSetId());
		p_reverseTransferVO.setCommProfileVersion(p_oldChannelTransferVO.getCommProfileVersion());
		p_reverseTransferVO.setDualCommissionType(p_oldChannelTransferVO.getDualCommissionType());
		p_reverseTransferVO.setTransferCategory(p_oldChannelTransferVO.getTransferCategory());
		p_reverseTransferVO.setDefaultLang(p_oldChannelTransferVO.getDefaultLang());
		p_reverseTransferVO.setSecondLang(p_oldChannelTransferVO.getSecondLang());
		p_reverseTransferVO.setControlTransfer(p_oldChannelTransferVO.getControlTransfer());
		
		p_reverseTransferVO.setRequestGatewayCode(oAuthUser.getReqGatewayCode());
		p_reverseTransferVO.setRequestGatewayType(oAuthUser.getReqGatewayType());
		p_reverseTransferVO.setWalletType(p_oldChannelTransferVO.getWalletType());

		if (log.isDebugEnabled()) {
			log.debug("constructReverseVofromTxnVo",
					"Exited ChannelTransferVO: " + p_reverseTransferVO + " CurDate " + p_curDate);
		}

	}

	/**
	 * Populate the transfer items vo from product list.
	 * 
	 * @param p_transferItemsVO
	 * @param p_userBalancesVO
	 * @throws BTSLBaseException
	 */
	private void populateTransferItemsVO(ChannelTransferItemsVO p_transferItemsVO,
			ChannelTransferItemsVO p_userBalancesVO) throws BTSLBaseException {
		if (log.isDebugEnabled()) {
			log.debug("populateTransferItemsVO",
					"Entered p_transferItemsVO " + p_transferItemsVO + " p_userBalancesVO " + p_userBalancesVO);
		}

		p_transferItemsVO.setProductType(p_userBalancesVO.getProductType());
		p_transferItemsVO.setProductCode(p_userBalancesVO.getProductCode());
		p_transferItemsVO.setShortName(p_userBalancesVO.getShortName());
		p_transferItemsVO.setProductName(p_userBalancesVO.getShortName());
		p_transferItemsVO.setCommProfileDetailID(p_userBalancesVO.getCommProfileDetailID());
		p_transferItemsVO.setRequestedQuantity(p_userBalancesVO.getRequestedQuantity());
		p_transferItemsVO.setProductShortCode(p_userBalancesVO.getProductShortCode());
		p_transferItemsVO.setRequiredQuantity(PretupsBL.getSystemAmount(p_userBalancesVO.getRequestedQuantity()));
		p_transferItemsVO.setUnitValue(p_userBalancesVO.getUnitValue());
		p_transferItemsVO.setBalance(p_userBalancesVO.getBalance());
		p_transferItemsVO.setApprovedQuantity(p_userBalancesVO.getApprovedQuantity());
		p_transferItemsVO.setOtfApplicable(p_userBalancesVO.isOtfApplicable());

		if (log.isDebugEnabled()) {
			log.debug("populateTransferItemsVO", "Exiting p_transferItemsVO " + p_transferItemsVO);
		}
	}

	/**
	 * method control all method call for returned
	 * 
	 * @param p_con
	 * @param p_channelTransferVO
	 * @param p_userId
	 * @param p_date
	 * @param p_forwardPath
	 * @throws BTSLBaseException
	 */
	private void orderReversalProcessStart(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userId,
			Date p_date, String p_forwardPath) throws BTSLBaseException {
		if (log.isDebugEnabled()) {
			log.debug("orderReversalProcessStart", "Entered p_channelTransferVO  " + p_channelTransferVO + " p_userId "
					+ p_userId + " p_date " + p_date + " p_forwardPath: " + p_forwardPath);
		}
		final boolean credit = false;

		// prepare networkStockList credit the network stock
		ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userId, p_date,
				credit);
		ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, p_userId, p_date);

		if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
			ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(p_con, p_channelTransferVO,
					p_userId, p_date, credit);
			ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(p_con, p_channelTransferVO, p_userId,
					p_date);
		}

		// update user daily balances
		final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
		userBalancesDAO.updateUserDailyBalances(p_con, p_date, constructBalanceVOFromTxnVO(p_channelTransferVO));

		// channel debit the user balances
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

		// for o2c reversal
		p_channelTransferVO.setReversalFlag(true);
		int updateCount;
		if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET))
				.booleanValue()) {
			updateCount = channelUserDAO.debitUserBalancesForMultipleWallet(p_con, p_channelTransferVO, true,
					p_forwardPath);
		} else {
			updateCount = channelUserDAO.debitUserBalancesO2C(p_con, p_channelTransferVO, true, p_forwardPath);
		}
		if (updateCount == -2) {
			throw new BTSLBaseException(this, "orderReversalProcessStart",
					"o2cchannelreversetrx.reverse.insuffbonusbalance", "searchattribute");
		}
		ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, p_forwardPath, p_date);
		if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,
				p_channelTransferVO.getNetworkCode())) {
			if ((p_channelTransferVO.getType() != null
					&& !p_channelTransferVO.getType().equalsIgnoreCase(PretupsI.TRANSFER_TYPE_FOC))
					&& (p_channelTransferVO.getWalletType() != null
							&& !p_channelTransferVO.getWalletType().equalsIgnoreCase(PretupsI.FOC_WALLET_TYPE))) {
				ChannelTransferBL.decreaseOptOTFCounts(p_con, p_channelTransferVO);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("orderReversalProcessStart", "Exiting");
		}
	}

	/**
	 * Method constructBalanceVOFromTxnVO.
	 * 
	 * @param p_channelTransferVO ChannelTransferVO
	 * @return UserBalancesVO
	 */
	private UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
		
		if (log.isDebugEnabled()) {
			log.debug("constructBalanceVOFromTxnVO", "Entered:NetworkStockTxnVO=>" + p_channelTransferVO);
		}
		final UserBalancesVO userBalancesVO = UserBalancesVO.getInstance();
		userBalancesVO.setUserID(p_channelTransferVO.getFromUserID());
		userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
		userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
		userBalancesVO.setLastTransferOn(p_channelTransferVO.getModifiedOn());
		// Added to log user MSISDN on 13/02/2008
		userBalancesVO.setUserMSISDN(p_channelTransferVO.getFromUserCode());
		if (log.isDebugEnabled()) {
			log.debug("constructBalanceVOFromTxnVO", "Exiting userBalancesVO=" + userBalancesVO);
		}
		return userBalancesVO;
	}
	
	
	
	
	/**
	 * 
	 * @param con
	 * @param reverseTransferVO
	 * @param locale
	 * @param responseVO
	 */
	private void pushMessage(Connection con, ChannelTransferVO reverseTransferVO, Locale locale, BaseResponse responseVO) {
		final String methodName = "pushMessage";
		
		try {
			final UserDAO userDAO = new UserDAO();
			UserPhoneVO phoneVO = null;
			UserPhoneVO primaryPhoneVO = null;

			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED))
					.booleanValue()) {
				phoneVO = userDAO.loadUserAnyPhoneVO(con, reverseTransferVO.getFromUserCode());
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED))
						.booleanValue() && !(PretupsI.YES.equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
					primaryPhoneVO = userDAO.loadUserPrimayPhoneVO(con, phoneVO.getUserId());
				}
			} else {
				phoneVO = userDAO.loadUserPhoneVO(con, reverseTransferVO.getFromUserID());
			}

			String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			// Txn reversal
			final ArrayList<ChannelTransferItemsVO> listItem = reverseTransferVO.getChannelTransferitemsVOList();
			ChannelTransferItemsVO channelTrfItemsVO = null;
			KeyArgumentVO keyArgumentVO = null;
			final ArrayList<KeyArgumentVO> balList = new ArrayList<KeyArgumentVO>();
			String[] args = null;
			int listsItemsize = listItem.size();
			for (int i = 0; i < listsItemsize; i++) {
				channelTrfItemsVO = listItem.get(i);
				keyArgumentVO = new KeyArgumentVO();
				keyArgumentVO.setKey(PretupsErrorCodesI.TXN_REVERSAL_SUB_KEY);
				args = new String[] { String.valueOf(channelTrfItemsVO.getShortName()),
						PretupsBL.getDisplayAmount(channelTrfItemsVO.getAfterTransSenderPreviousStock()
								- channelTrfItemsVO.getRequiredQuantity()) };
				keyArgumentVO.setArguments(args);
				balList.add(keyArgumentVO);
			} // end of for
			if (phoneVO != null) {
				country = phoneVO.getCountry();
				language = phoneVO.getPhoneLanguage();
				locale = new Locale(language, country);

				String[] array = null;
				BTSLMessages messages = null;
				PushMessage pushMessage = null;
				if (PretupsI.TRANSFER_CATEGORY_SALE.equalsIgnoreCase(reverseTransferVO.getTransferCategory())) {
					array = new String[] { reverseTransferVO.getTransferID(), reverseTransferVO.getRefTransferID(),
							PretupsBL.getDisplayAmount(reverseTransferVO.getNetPayableAmount()),
							PretupsBL.getDisplayAmount(reverseTransferVO.getSenderDrQty()),
							BTSLUtil.getMessage(locale, balList) };
					messages = new BTSLMessages(PretupsErrorCodesI.O2C_REVERSAL_TRX_CH_USER, array);
				}
				else if (PretupsI.TRANSFER_CATEGORY_FOC.equalsIgnoreCase(reverseTransferVO.getTransferCategory())
						|| PretupsI.TRANSFER_CATEGORY_TRANSFER
								.equalsIgnoreCase(reverseTransferVO.getTransferCategory())) {
					array = new String[] { reverseTransferVO.getTransferID(), reverseTransferVO.getRefTransferID(),
							PretupsBL.getDisplayAmount(reverseTransferVO.getNetPayableAmount()),
							PretupsBL.getDisplayAmount(reverseTransferVO.getSenderDrQty()),
							BTSLUtil.getMessage(locale, balList) };
					messages = new BTSLMessages(PretupsErrorCodesI.FOC_REVERSAL_TRX_CH_USER, array);
				}
				
				pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, reverseTransferVO.getTransferID(),
						null, locale, reverseTransferVO.getNetworkCode());
				pushMessage.push();
				// Ended Here

			} else {
				final String arr[] = { reverseTransferVO.getTransferID(), reverseTransferVO.getToUserName() };
				String msg = RestAPIStringParser.getMessage(locale, "userreturn.withdraw.msg.success.nophoneinfo",
						arr);
				responseVO.setMessage(msg);
				if(log.isDebugEnabled()) {
					log.info(methodName, "message: "+ msg);
				}
			}

			if (primaryPhoneVO != null) {
				country = primaryPhoneVO.getCountry();
				language = primaryPhoneVO.getPhoneLanguage();
				String[] array = null;
				BTSLMessages messages = null;
				PushMessage pushMessage = null;

				if (PretupsI.TRANSFER_CATEGORY_SALE.equalsIgnoreCase(reverseTransferVO.getTransferCategory())) {
					array = new String[] { reverseTransferVO.getTransferID(), reverseTransferVO.getRefTransferID(),
							PretupsBL.getDisplayAmount(reverseTransferVO.getNetPayableAmount()),
							PretupsBL.getDisplayAmount(reverseTransferVO.getSenderDrQty()),
							BTSLUtil.getMessage(locale, balList) };
					messages = new BTSLMessages(PretupsErrorCodesI.O2C_REVERSAL_TRX_CH_USER, array);
				} else if (PretupsI.TRANSFER_CATEGORY_FOC
						.equalsIgnoreCase(reverseTransferVO.getTransferCategory())) {
					array = new String[] { reverseTransferVO.getTransferID(), reverseTransferVO.getRefTransferID(),
							PretupsBL.getDisplayAmount(reverseTransferVO.getNetPayableAmount()),
							PretupsBL.getDisplayAmount(reverseTransferVO.getSenderDrQty()),
							BTSLUtil.getMessage(locale, balList) };
					messages = new BTSLMessages(PretupsErrorCodesI.FOC_REVERSAL_TRX_CH_USER, array);
				}

				pushMessage = new PushMessage(primaryPhoneVO.getMsisdn(), messages,
						reverseTransferVO.getTransferID(), null, locale, reverseTransferVO.getNetworkCode());
				pushMessage.push();
				// Ended Here
			}

			

		} catch (Exception e) {
			log.error(methodName, e);
			log.errorTrace(methodName, e);
		}

	}
	
	
	public void getParentCategoryList(ParentCategoryListResponseVO response, HttpServletResponse responseSwag,
			OAuthUser oAuthUserData, Locale locale, String categoryCode) throws Exception {
		
		final String methodName = "getParentCategoryList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		ArrayList<CategoryVO> list = null;
		ChannelUserVO sessionUserVO = null;
		final C2STransferDAO c2STransferDAO = new C2STransferDAO();
		final CategoryDAO categoryDAO = new CategoryDAO();
		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userDao = new UserDAO();
			list =  new ArrayList<CategoryVO>();
			sessionUserVO = new ChannelUserVO();
			
			if(BTSLUtil.isNullString(categoryCode)) {
				String message = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BLANK_CATEGORY, new String[] {""});
				response.setMessage(message);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				return;
			}
			
			sessionUserVO = userDao.loadAllUserDetailsByLoginID(con,oAuthUserData.getData().getLoginid());
            if(sessionUserVO==null) {
            	String message = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.INVALID_LOGGEDIN_USER, new String[] {""});
				response.setMessage(message);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				return;
            }
	
			
			ArrayList<CategoryVO> originalCategories=categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT);
			List<CategoryVO> category =originalCategories.stream().filter(cat-> cat.getCategoryCode().equals(categoryCode)).collect(Collectors.toList());
			
			if(category.size()==0) {
				String message = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.PARENT_CATEGORY_INVALID, new String[] {""});
				response.setMessage(message);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				return;
			}
			
			/*
             * Here we load all transfer rules(from_category and to category),
             * parent category list will populate on the basis of
             * category drop down value by calling a method of the same class
             * populateParentCategoryList
             */
            ArrayList<ChannelTransferRuleVO> originalParentCategoryList = c2STransferDAO.loadC2SRulesListForChannelUserAssociation(con, sessionUserVO.getNetworkID());
            
            /*
             * OrigParentCategory List contains all(Associated C2S Transfer
             * Rules category)
             * FromCategory and ToCategory information like
             * 
             * Dist -> Ret(Disttributor can transfer to retailer and
             * parentAssociationFlag = Y)
             * The above rule state while adding Retailer parent category can be
             * Distributor
             */
            if (originalParentCategoryList != null && !BTSLUtil.isNullString(categoryCode)) {
                CategoryVO categoryVO = null;
                ChannelTransferRuleVO channelTransferRuleVO = null;
                for (int i = 0, j = originalCategories.size(); i < j; i++) {
                    categoryVO = (CategoryVO) originalCategories.get(i);
                    /*
                     * If Sequence No == 1 means root owner is adding(suppose
                     * Distributor)
                     * at this time pagentCategory and category both will be
                     * same, just add
                     * the categoryVO into the parentCategoryList
                     */
                    if (1==category.get(0).getSequenceNumber() && category.get(0).getCategoryCode().equals(categoryVO.getCategoryCode())) {
                        list = new ArrayList();
                        list.add(categoryVO);
                        break;
                    }
                    /*
                     * In Case of channel admin No need to check the sequence
                     * number
                     * In Case of channel user we need to check the sequence
                     * number
                     */
                    if (PretupsI.OPERATOR_TYPE_OPT.equals(sessionUserVO.getDomainID())) {
                        for (int m = 0, n = originalParentCategoryList.size(); m < n; m++) {
                            channelTransferRuleVO = (ChannelTransferRuleVO) originalParentCategoryList.get(m);
                            /*
                             * Here three checks are checking
                             * Add those category into the list where
                             * a)FormCategory(origPatentList) =
                             * categoryCode(origcategoryList)
                             * b)selectedCategory(categoryID[0] =
                             * ToCategory(origParentCategoryList)
                             * c)selectedCategory(categoryID[0] !=
                             * FromCategory(origParentCategoryList)
                             */
                            if (categoryVO.getCategoryCode().equals(channelTransferRuleVO.getFromCategory()) && category.get(0).getCategoryCode().equals(channelTransferRuleVO.getToCategory()) && !category.get(0).getCategoryCode()
                                            .equals(channelTransferRuleVO.getFromCategory())) {
                                list.add(categoryVO);
                            }
                        }
                    } else {
                        for (int m = 0, n = originalParentCategoryList.size(); m < n; m++) {
                            channelTransferRuleVO = (ChannelTransferRuleVO) originalParentCategoryList.get(m);
                            /*
                             * Here three checks are checking
                             * Add those category into the list where
                             * a)FormCategory(origPatentList) =
                             * categoryCode(origcategoryList)
                             * b)selectedCategory(categoryID[0] =
                             * ToCategory(origParentCategoryList)
                             * c)selectedCategory(categoryID[0] !=
                             * FromCategory(origParentCategoryList)
                             */

                            if (categoryVO.getCategoryCode().equals(channelTransferRuleVO.getFromCategory()) && category.get(0).getCategoryCode().equals(channelTransferRuleVO.getToCategory()) && !category.get(0).getCategoryCode().equals(channelTransferRuleVO.getFromCategory()))
                                if (categoryVO.getSequenceNumber() >= sessionUserVO.getCategoryVO().getSequenceNumber()) {
                                    list.add(categoryVO);
                                }
                            }
                        }
                    }
                }
            
                if (list.isEmpty()) {
                	throw new BTSLBaseException(this, methodName,"user.selectchannelcategory.msg.notransferruledefined");
                }
                
                if(list.size()==1) {
                	if(list.get(0).getSequenceNumber()==1 && category.get(0).getSequenceNumber()==1){
						response.setNotApplicable(true);
						response.setParentCategoryList(null);
                	}else {
                    	response.setParentCategoryList(list);
                	}
                }else {
                	response.setParentCategoryList(list);
                }
                
                response.setMessage(PretupsI.SUCCESS);
    			response.setStatus(HttpStatus.SC_OK);
    			responseSwag.setStatus(HttpStatus.SC_OK);

    		} catch (BTSLBaseException be) {
    			log.error(methodName, "Exception:e=" + be);
    			throw be;
    		} catch (Exception e) {
    			log.error(methodName, "Exception:e=" + e);
    			throw e;
    		} finally {
    			// if connection is not null then close the connection

    			try {
    				if (mcomCon != null) {
    					mcomCon.close("O2cTxnReversalServiceImpl#getParentCategoryList");
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
    				log.debug(methodName, "Exited");
    			}
    		}
            
	}

}
