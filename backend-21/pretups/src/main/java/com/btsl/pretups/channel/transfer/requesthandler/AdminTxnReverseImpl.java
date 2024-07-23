package com.btsl.pretups.channel.transfer.requesthandler;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTxnsForReversalRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTxnsForReversalResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSWithdrawBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.channel.userreturn.web.ChnnlToChnnlReturnWithdrawForm;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;
import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;

@Service("AdminTxnReverseI")
public class AdminTxnReverseImpl implements AdminTxnReverseI {

	protected final Log log = LogFactory.getLog(getClass().getName());

	@Override
	public ArrayList<ChannelTransferVO> getC2CTxnsForReversal(Connection con, C2CTxnsForReversalRequestVO c2cTxnsForReversalRequestVO,
			HttpServletResponse responseSwag, ChannelUserVO sessionUser) throws BTSLBaseException, SQLException {

		final String methodName = "getC2CTxnsForReversal";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		ChnnlToChnnlReturnWithdrawForm theForm = new ChnnlToChnnlReturnWithdrawForm();

		// validating and setting theForm
		setFormForC2CTxnsForReversal(c2cTxnsForReversalRequestVO, theForm);
		if (BTSLUtil.isNullString(theForm.getTransferNum()) && BTSLUtil.isNullString(theForm.getUserCode())
				&& BTSLUtil.isNullString(theForm.getUserLoginID()) && BTSLUtil.isNullString(theForm.getDomainCode())
				&& BTSLUtil.isNullString(theForm.getCategoryCode())
				&& BTSLUtil.isNullString(theForm.getRevTransferNum())) {
			throw new BTSLBaseException(PretupsErrorCodesI.MAND_FIELD_MISSING);
		} else if (BTSLUtil.isNullString(theForm.getTransferNum()) && BTSLUtil.isNullString(theForm.getUserCode())
				&& BTSLUtil.isNullString(theForm.getUserLoginID())
				&& BTSLUtil.isNullString(theForm.getRevTransferNum())) {
			if (BTSLUtil.isNullString(theForm.getDomainCode())) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_MISSING, new String[] { "Domain code" });
			}
			if (BTSLUtil.isNullString(theForm.getCategoryCode())) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_MISSING, new String[] { "Category code" });
			}
			if (BTSLUtil.isNullString(theForm.getToUserName())) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_MISSING, new String[] { "Username" });
			}
		}

		if (!BTSLUtil.isNullString(theForm.getUserCode())) {
			if (!BTSLUtil.isValidMSISDN(theForm.getUserCode())) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Sender msisdn" });
			}
		}
		if (!BTSLUtil.isNullString(theForm.getMsisdn())) {
			if (!BTSLUtil.isValidMSISDN(theForm.getMsisdn())) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Reciever msisdn" });
			}
		}

		Date fromDate = null;
		Date toDate = null;
		final Date currentDate = new Date();
		UserWebDAO userwebDAO = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		channelUserWebDAO = new ChannelUserWebDAO();
		userwebDAO = new UserWebDAO();

		ArrayList<ChannelTransferVO> transferList = loadTransactionForC2CType(con, theForm, sessionUser);
       //write condition to check the transaction not in new status
		transferList=(ArrayList<ChannelTransferVO>)transferList.stream().filter(p->!p.getStatus().equalsIgnoreCase(PretupsI.CHANNEL_TRANSFER_ORDER_NEW))
		.collect(Collectors.toList());
		if(transferList.size()==0) {
          throw new BTSLBaseException(PretupsErrorCodesI.NO_TXNS_FOR_REVERSAL);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited ");
		}
		return transferList;
	}

	private ArrayList<ChannelTransferVO> loadTransactionForC2CType(Connection p_con, ChnnlToChnnlReturnWithdrawForm theForm,
			ChannelUserVO userVO) throws BTSLBaseException {
		final String methodName = "loadTransactionForC2CType";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		Date fromDate = null;
		Date toDate = null;
		final Date currentDate = new Date();
		UserWebDAO userwebDAO = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		ArrayList<ChannelTransferVO> transferList = null;
		ArrayList<ChannelTransferVO> finalTransferList = new ArrayList<>();
		List<String> userIdList = new ArrayList<>();
		try {
			channelUserWebDAO = new ChannelUserWebDAO();
			userwebDAO = new UserWebDAO();
			final int daysToadd = ((Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.RVERSE_TRN_EXPIRY,
					userVO.getNetworkID())).intValue();
			fromDate = BTSLUtil.addDaysInUtilDate(currentDate, -daysToadd);
			final String format = ((String) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT));
			final String toDateStr = BTSLUtil.getDateTimeStringFromDate(currentDate, format);
			final String fromDateStr = BTSLUtil.getDateTimeStringFromDate(fromDate, format);
			fromDate = BTSLUtil.getDateFromDateString(fromDateStr, format);
			toDate = BTSLUtil.getDateFromDateString(toDateStr, format);

			String userLoginId = null;
			String transferID = null;
			String userMobileNo = null;
			String userId = null;
			ChannelUserVO channelUserVO = null;
			final String statusUsed = PretupsI.STATUS_IN;
			String status = "'" + PretupsI.USER_STATUS_ACTIVE + "','" + PretupsI.USER_STATUS_SUSPEND + "','"
					+ PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			
			ArrayList domList = userVO.getDomainList();
			if ((domList == null || domList.isEmpty()) && PretupsI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO
	                .getCategoryVO().getFixedDomains())) {
	                domList = new DomainDAO().loadCategoryDomainList(p_con);
	            }
	        domList = BTSLUtil.displayDomainList(domList);
            theForm.setChannelDomainList(domList);

			if (!BTSLUtil.isNullString(theForm.getTransferNum())) {
				transferID = theForm.getTransferNum().trim();
			} else if (!BTSLUtil.isNullString(theForm.getUserCode()))// mobile
			// no
			{
				userMobileNo = theForm.getUserCode().trim();
				final String filteredMSISDN = PretupsBL.getFilteredMSISDN(userMobileNo);

				final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache
						.getObject(PretupsBL.getMSISDNPrefix(filteredMSISDN));
				if (prefixVO == null || !prefixVO.getNetworkCode().equals(userVO.getNetworkID())) {
					final String[] arr1 = { userMobileNo, userVO.getNetworkName() };
					throw new BTSLBaseException(PretupsErrorCodesI.MSISDN_NOT_IN_NETWORK, arr1);
				}
				if (PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())) {
					channelUserVO = channelUserDAO.loadUsersDetails(p_con, filteredMSISDN, null, statusUsed, status);
				} else {
					final String userID = userVO.getUserID();
					channelUserVO = channelUserDAO.loadUsersDetails(p_con, filteredMSISDN, userID, statusUsed, status);
				}
				if (channelUserVO != null) {
					final boolean isDomainFlag = this.isExistDomain(theForm.getChannelDomainList(), channelUserVO);
					if (!isDomainFlag) {
						// check the user in the same domain or not
						final String arr2[] = { userMobileNo };
						throw new BTSLBaseException(PretupsErrorCodesI.MSISDN_NOT_IN_DOMAIN, arr2);
					}
					final boolean isGeoDomainFlag = userwebDAO.isUserInSameGRPHDomain(p_con, channelUserVO.getUserID(),
							channelUserVO.getCategoryVO().getGrphDomainType(), userVO.getUserID(),
							userVO.getCategoryVO().getGrphDomainType());
					if (!isGeoDomainFlag) {
						// check the user in the same domain or not
						final String arr2[] = { userMobileNo };
						throw new BTSLBaseException(PretupsErrorCodesI.MSISDN_NOT_IN_GEODOMAIN, arr2);
					}

					userId = channelUserVO.getUserID();
				} else if (/*
							 * PretupsI.CHANNEL_USER_TYPE.equals(userVO.getUserType ()) ||
							 */PretupsI.STAFF_USER_TYPE.equals(userVO.getUserType())) {
					if (filteredMSISDN.equalsIgnoreCase(userVO.getMsisdn())) {
						userId = userVO.getUserID();
					} else {
						throw new BTSLBaseException(PretupsErrorCodesI.MOBILE_NO_NOT_EXIST);
					}

				} else {
					// throw exception no user exist with this Mobile No
					final String arr2[] = { userMobileNo };
					throw new BTSLBaseException(PretupsErrorCodesI.MOBILE_NO_NOT_EXIST);
				}
			} else if (!BTSLUtil.isNullString(theForm.getUserLoginID())) { // on the selection of login id
				userLoginId = theForm.getUserLoginID().trim();
				if (PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())) {
					channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(p_con, userLoginId, null, statusUsed,
							status);
				} else {
					final String userID = userVO.getUserID();
					channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(p_con, userLoginId, userID, statusUsed,
							status);
				}
				if (channelUserVO != null) {
					if (PretupsI.STAFF_USER_TYPE.equals(channelUserVO.getUserType())) {
						final String arr2[] = { userLoginId };
						throw new BTSLBaseException(PretupsErrorCodesI.USER_NOT_EXIST);
					}
					final boolean isDomainFlag = this.isExistDomain(theForm.getChannelDomainList(), channelUserVO);
					if (!isDomainFlag) {
						// check the user in the same domain or not
						final String arr2[] = { userLoginId };
						throw new BTSLBaseException(PretupsErrorCodesI.USER_NOT_IN_DOMAIN);
					}
					final boolean isGeoDomainFlag = userwebDAO.isUserInSameGRPHDomain(p_con, channelUserVO.getUserID(),
							channelUserVO.getCategoryVO().getGrphDomainType(), userVO.getUserID(),
							userVO.getCategoryVO().getGrphDomainType());
					if (!isGeoDomainFlag) {
						// check the user in the same domain or not
						final String arr2[] = { userLoginId };
						throw new BTSLBaseException(PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN);
					}

					userId = channelUserVO.getUserID();
				} else if (/*
							 * PretupsI.CHANNEL_USER_TYPE.equals(userVO.getUserType ()) ||
							 */PretupsI.STAFF_USER_TYPE.equals(userVO.getUserType())) {
					if (userLoginId.equalsIgnoreCase(userVO.getLoginID())) {
						userId = userVO.getUserID();
					} else {
						throw new BTSLBaseException(PretupsErrorCodesI.USER_NOT_EXIST);
					}

				} else {
					// throw exception no user exist with this lodin id
					throw new BTSLBaseException(PretupsErrorCodesI.USER_NOT_EXIST);
				}
			} else if (!BTSLUtil.isNullString(theForm.getToUserName())) { // for username
				ListValueVO listValueVO = null;
				final String userName = theForm.getToUserName().trim();
				status = "'" + PretupsI.USER_STATUS_ACTIVE + "','" + PretupsI.USER_STATUS_SUSPEND + "','"
						+ PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
				theForm.setToCategoryCode(theForm.getCategoryCode());
				boolean isLoginChannelUser = false;
				if (PretupsI.CHANNEL_USER_TYPE.equals(userVO.getUserType())) {
					isLoginChannelUser = true;
				}
				final ArrayList list = channelUserWebDAO.loadCategoryUsersWithinGeoDomainHirearchy(p_con,
						theForm.getToCategoryCode(), userVO.getNetworkID(), userName, userVO.getUserID(), status, null,
						null, isLoginChannelUser);
				if (list == null || list.isEmpty()) {
					throw new BTSLBaseException(PretupsErrorCodesI.USER_NOT_EXIST);
				} else if (list.size() == 1) {
					listValueVO = (ListValueVO) list.get(0);
					;
					// exact name not required
					theForm.setToUserName(listValueVO.getLabel());
					theForm.setToUserID(listValueVO.getValue());

				} else if (list.size() > 1) {

					boolean isExist = false;
					if (!BTSLUtil.isNullString(theForm.getUserID())) {
						for (int i = 0, k = list.size(); i < k; i++) {
							listValueVO = (ListValueVO) list.get(i);
							if (listValueVO.getValue().equals(theForm.getToUserID())
									&& theForm.getToUserName().equalsIgnoreCase(listValueVO.getLabel())) {
								theForm.setToUserName(listValueVO.getLabel());
								theForm.setToUserID(listValueVO.getValue());
								isExist = true;
								break;
							}
						}
					} else {
//						ListValueVO listValueVONext = null;
						for (int i = 0, k = list.size(); i < k; i++) {
							listValueVO = (ListValueVO) list.get(i);
							if (theForm.getToUserName().compareTo(listValueVO.getLabel()) == 0) {
								if (((i + 1) <= k)) {
									userIdList.add(((ListValueVO) list.get(i)).getValue());
									continue;
								}

//									theForm.setUserID(listValueVO.getValue());
//									theForm.setToUserName(listValueVO.getLabel());
//									theForm.setToUserID(listValueVO.getValue());
//									isExist = true;
//									break;
//								}
//								theForm.setUserID(listValueVO.getValue());
//								theForm.setToUserName(listValueVO.getLabel());
//								theForm.setToUserID(listValueVO.getValue());
//								isExist = true;
//								break;
							}
						}
						if (!userIdList.isEmpty()) {
							isExist = true;
						} else {
							isExist = false;
						}
					}
					if (!isExist) {
						final String arr[] = { theForm.getToUserName() };
						throw new BTSLBaseException(PretupsErrorCodesI.USER_NOT_EXIST);
					}
				}
				userId = theForm.getToUserID();
			}

			// load the user
			// load the user hierarchy to validate the input values.
			final ArrayList hierarchyList = null;
			channelUserVO = null;
			final ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
			// load the transaction list on the basis of the input values.
			ArrayList mixedList = new ArrayList<>();
			ArrayList mixedLists = new ArrayList<>();
			if (userId != null) {
				// load the transaction list on the basis of the input values.
				mixedLists.add(channelTransferWebDAO.loadReversalChnlToChnlTransfersList(p_con, userId, null,
						theForm.getMsisdn(), fromDate, toDate, transferID, PretupsI.CHANNEL_TYPE_C2C));
			} else {
				if (userIdList != null && !userIdList.isEmpty()) {
					for (String usersId : userIdList) {
						userId = usersId;
						mixedLists.add(channelTransferWebDAO.loadReversalChnlToChnlTransfersList(p_con, userId, null,
								theForm.getMsisdn(), fromDate, toDate, transferID, PretupsI.CHANNEL_TYPE_C2C));
					}
				}
			}
			if (!BTSLUtil.isNullString(theForm.getTransferNum())) {
				mixedLists.add(channelTransferWebDAO.loadReversalChnlToChnlTransfersList(p_con, userId, null,
						theForm.getMsisdn(), fromDate, toDate, transferID, PretupsI.CHANNEL_TYPE_C2C));
			}
			ArrayList<ChannelTransferVO> reverseTransferList = null;
			if (mixedLists != null && !mixedLists.isEmpty()) {
				for (int k = 0; k < mixedLists.size(); k++) {
					mixedList = (ArrayList) mixedLists.get(k);

					if (mixedList != null && !mixedList.isEmpty()) {
						// index 0 already reversed
						// reverseTransferList=new ArrayList();
						reverseTransferList = (ArrayList) mixedList.get(0);
						// index 1 not reversed
						// transferList=new ArrayList();
						transferList = (ArrayList) mixedList.get(1);
						if (transferList.size() == 1 && !BTSLUtil.isNullString(theForm.getTransferNum())) {
							ChannelTransferVO transferVO = new ChannelTransferVO();
							transferVO = (ChannelTransferVO) transferList.get(0);
							if (transferVO != null)
								if (transferVO.getTransactionMode().equalsIgnoreCase(PretupsI.SOS_TRANSACTION_MODE)) {
									throw new BTSLBaseException(PretupsErrorCodesI.SOS_TXN_REVERSAL_NOT_PERMITTED);
								}
							finalTransferList.addAll(transferList);
						} else if (transferList != null && !transferList.isEmpty()) {
							ArrayList tempList = new ArrayList();
							for (int i = 0, j = transferList.size(); i < j; i++) {
								ChannelTransferVO transferVO = new ChannelTransferVO();
								transferVO = (ChannelTransferVO) transferList.get(i);
								if (!transferVO.getTransactionMode().equalsIgnoreCase(PretupsI.SOS_TRANSACTION_MODE)) {
									tempList.add(transferVO);
								}
							}
							transferList = new ArrayList();
							transferList.addAll(tempList);
							finalTransferList.addAll(tempList);
						}
					}
				}
				// no data found
				if ((reverseTransferList == null || reverseTransferList.isEmpty())
						&& (finalTransferList == null || finalTransferList.isEmpty())) {
					throw new BTSLBaseException(PretupsErrorCodesI.NO_TXNS_FOR_REVERSAL);
				} else if ((!BTSLUtil.isNullString(theForm.getTransferNum()))
						&& (reverseTransferList != null && !reverseTransferList.isEmpty())
						&& (finalTransferList != null && finalTransferList.isEmpty())) {
					throw new BTSLBaseException(PretupsErrorCodesI.TXN_ALREADY_REVERSED,
							new String[] { theForm.getTransferNum() });
				}
			} else {
				throw new BTSLBaseException(PretupsErrorCodesI.NO_TXNS_FOR_REVERSAL);
			}
			theForm.setReverseTransferList(reverseTransferList);


		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			log.error("loadTransactionForC2CType", "Exception:e=" + e);
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited ");
		}
		return finalTransferList;
	}

	private void setFormForC2CTxnsForReversal(C2CTxnsForReversalRequestVO c2cTxnsForReversalRequestVO,
			ChnnlToChnnlReturnWithdrawForm theForm) {

		final String methodName = "setFormForC2CTxnsForReversal";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		theForm.setTransferNum(c2cTxnsForReversalRequestVO.getTxnId());
		theForm.setUserCode(c2cTxnsForReversalRequestVO.getSenderMsisdn());// senderMsisdn
		theForm.setUserLoginID(c2cTxnsForReversalRequestVO.getSenderLoginId());
		theForm.setDomainCode(c2cTxnsForReversalRequestVO.getDomainCode());
		theForm.setCategoryCode(c2cTxnsForReversalRequestVO.getCategoryCode());
		theForm.setMsisdn(c2cTxnsForReversalRequestVO.getRcvrMsisdn());
		theForm.setToUserName(c2cTxnsForReversalRequestVO.getSenderUsername());
		theForm.setRevTransferNum(null);// doubt regarding where its used

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited ");
		}
	}

	private boolean isExistDomain(ArrayList p_domainList, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
		if (log.isDebugEnabled()) {
			log.debug("isExistDomain",
					"Entered p_domainList.size()=" + p_domainList.size() + ", p_channelUserVO=" + p_channelUserVO);
		}
		final String METHOD_NAME = "isExistDomain";
		if (p_domainList == null || p_domainList.isEmpty()) {
			return true;
		}
		boolean isDomainExist = false;
		try {
			ListValueVO listValueVO = null;
			for (int i = 0, j = p_domainList.size(); i < j; i++) {
				listValueVO = (ListValueVO) p_domainList.get(i);
				if (listValueVO.getValue().equals(p_channelUserVO.getCategoryVO().getDomainCodeforCategory())) {
					isDomainExist = true;
					break;
				}
			}
		} catch (Exception e) {
			log.error("isExistDomain", "Excepion E=" + e.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "Exception in Domain Existance Check.");
		}
		if (log.isDebugEnabled()) {
			log.debug("isExistDomain", "Exiting isDomainExist=" + isDomainExist);
		}
		return isDomainExist;
	}

	@Override
	public C2CTxnsForReversalResponseVO performC2CTxnReversal(MComConnectionI mcomCon, Connection con, HttpServletResponse responseSwag,
			ChannelUserVO sessionUser, String txnId, String nwCode, String nwCodeFor, String remarks) throws BTSLBaseException {
		
		final String methodName = "performC2CTxnReversal";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		String resMsg = null;
		C2CTxnsForReversalResponseVO response = new C2CTxnsForReversalResponseVO();
		
		try {
			
		
			ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO(); 
			ChannelTransferVO channelTransferVO = new ChannelTransferVO();
			final ChnnlToChnnlReturnWithdrawForm theForm = new ChnnlToChnnlReturnWithdrawForm();
		
			channelTransferVO.setTransferID(txnId);
			channelTransferVO.setNetworkCode(nwCode);
			channelTransferVO.setNetworkCodeFor(nwCodeFor);
			theForm.setRemarks(remarks);
			if (BTSLUtil.isNullString(theForm.getRemarks())) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_MISSING, new String[] { "remarks" });
            }
			
			if (BTSLUtil.isNullString(channelTransferVO.getTransferID())) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_MISSING, new String[] { "Transfer ID" });
			}

			if (BTSLUtil.isNullString(channelTransferVO.getNetworkCode())) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_MISSING, new String[] { "Network code" });
			}

			if (BTSLUtil.isNullString(channelTransferVO.getNetworkCodeFor())) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_MISSING, new String[] { "Receiver network code" });
			}
			
			if (theForm.getRemarks().length()>100) {
				throw new BTSLBaseException(PretupsErrorCodesI.INVALID_LENGTH_REMARKS);
			}

			NetworkDAO networkDAO = new NetworkDAO();
			if(networkDAO.loadNetwork(con, channelTransferVO.getNetworkCode())==null) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Network code" });
			}
			if(networkDAO.loadNetwork(con, channelTransferVO.getNetworkCodeFor())==null) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Network code for" });
			}
			
			channelTransferDAO.loadChannelTransfersVO(con, channelTransferVO);
			if(channelTransferVO.getCreatedBy()==null) {
	            	throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Transfer ID" });
			}
			
			//checking if txn is already reversed
			C2CTxnsForReversalRequestVO c2CTxnsForReversalRequestVO = new C2CTxnsForReversalRequestVO();
			c2CTxnsForReversalRequestVO.setTxnId(channelTransferVO.getTransferID());
			getC2CTxnsForReversal(con, c2CTxnsForReversalRequestVO, responseSwag, sessionUser);
			
			theForm.setChannelTransferVO(channelTransferVO);
			final ArrayList voList = new ArrayList();
			final ChannelTransferVO channelTransferVOOld = new ChannelTransferVO();
			PropertyUtils.copyProperties(channelTransferVOOld, channelTransferVO);

			voList.add(channelTransferVOOld);
			theForm.setOldTransferVoList(voList);

			ArrayList itemsList = ChannelTransferBL.loadChannelTransferItemsWithBalances(con, channelTransferVO.getTransferID(), channelTransferVO.getNetworkCode(),
					channelTransferVO.getNetworkCodeFor(), channelTransferVO.getToUserID());
			
			// load sender balance
            final ArrayList balancesList = new UserBalancesDAO().loadUserBalanceList(con, channelTransferVO.getFromUserID(), channelTransferVO.getNetworkCode(),
                channelTransferVO.getNetworkCodeFor());
            ChannelTransferItemsVO channelTransferItemsVO = null;
            UserBalancesVO balancesVO = null;
            String commProfileSetId = null;
            String commissionProfileVersion = null;
            final ArrayList oldList = new ArrayList();
            long balance=0;
            boolean mwFlag=false;
            ChannelTransferItemsVO channelTransferItemsVOOld = null;
            
            if (balancesList != null && !balancesList.isEmpty()) {
                for (int i = 0, k = itemsList.size(); i < k; i++) {
                    channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
                    channelTransferItemsVO.setReversalRequestedQuantity(PretupsBL.getDisplayAmount(channelTransferItemsVO.getRequiredQuantity()));
                    channelTransferItemsVOOld = new ChannelTransferItemsVO();
                    BeanUtils.copyProperties(channelTransferItemsVOOld, channelTransferItemsVO);
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                    	for (int m = 0, n = balancesList.size(); m < n; m++){
                    		balancesVO = (UserBalancesVO) balancesList.get(m);
                    		if (balancesVO.getProductCode().equals(channelTransferItemsVO.getProductCode())) {
                    			balance+=balancesVO.getBalance();
                            }
                    	}
                    	channelTransferItemsVO.setSenderBalance(balance);
                    }
                    else{
                    	for (int m = 0, n = balancesList.size(); m < n; m++) {
                            balancesVO = (UserBalancesVO) balancesList.get(m);
                            if (balancesVO.getProductCode().equals(channelTransferItemsVO.getProductCode())) {
                                channelTransferItemsVO.setSenderBalance(balancesVO.getBalance());
                            }
                        }
                    }
                    if (channelTransferItemsVO.getTransferID().equals(channelTransferVO.getTransferID())) {
                        commProfileSetId = channelTransferVO.getCommProfileSetId();
                        commissionProfileVersion = channelTransferVO.getCommProfileVersion();
                    }
                    oldList.add(channelTransferItemsVOOld);
                }
            }
 
            theForm.setProductListWithTaxes(oldList);
            calculateTotalAmt(theForm, itemsList);
            theForm.setFromCommissionProfileID(commProfileSetId);
            theForm.setFromCommissionProfileVersion(commissionProfileVersion);
            theForm.setTransferItemsList(itemsList);
            theForm.setProductList(itemsList);
            theForm.setTransferMRP(channelTransferItemsVO.getProductMrpStr());
            
            //load txn detail api ends
            
            validateReversalInputParamters(theForm);
            Date curDate = new Date();
            final ChannelTransferVO oldChannelTransferVO = theForm.getChannelTransferVO();
            final ArrayList itemsListTemp = theForm.getTransferItemsList();

            itemsList = new ArrayList();
            final ArrayList itemsList2 = new ArrayList();
            channelTransferItemsVO = null;
            long mrpAmt = 0L;
            ChannelTransferVO newChannelTransferVO = null;
            ChannelTransferItemsVO channelTransferItemsOldVOtmp1 = null;
            ChannelTransferItemsVO channelTransferItemsOldVOtmp1Prod = null;
            ChannelTransferItemsVO channelTransferItemsVO1 = null;
            final ArrayList pList = new ArrayList();
            final ArrayList invalidProductLsit = new ArrayList();
            
            for (int m = 0, n = theForm.getProductList().size(); m < n; m++) {
                channelTransferItemsOldVOtmp1 = (ChannelTransferItemsVO) theForm.getProductList().get(m);
                channelTransferItemsOldVOtmp1Prod = new ChannelTransferItemsVO();
                BeanUtils.copyProperties(channelTransferItemsOldVOtmp1Prod, channelTransferItemsOldVOtmp1);
                pList.add(channelTransferItemsOldVOtmp1Prod);
            }
            
            channelTransferItemsOldVOtmp1 = null;
            if (itemsListTemp != null && !itemsListTemp.isEmpty()) {
                ChannelTransferItemsVO channelTransferItemsOldVO = null;

                for (int i = 0, j = itemsListTemp.size(); i < j; i++) {
                    channelTransferItemsOldVO = (ChannelTransferItemsVO) itemsListTemp.get(i);
                    channelTransferItemsVO = new ChannelTransferItemsVO();
                    channelTransferItemsVO1 = new ChannelTransferItemsVO();
                    BeanUtils.copyProperties(channelTransferItemsVO1, channelTransferItemsOldVO);
                    channelTransferItemsVO = channelTransferItemsOldVO;
                    channelTransferItemsVO.setProductName(channelTransferItemsOldVO.getShortName());
                    for (int m = 0, n = theForm.getProductList().size(); m < n; m++) {
                        channelTransferItemsOldVOtmp1 = (ChannelTransferItemsVO) theForm.getProductList().get(m);
                        // channelTransferItemsVO.setProductName(channelTransferItemsOldVOtmp1.getShortName());
                        if (channelTransferItemsOldVO.getProductCode().equals(channelTransferItemsOldVOtmp1.getProductCode())) {

                            if (BTSLUtil.isNullString(channelTransferItemsOldVOtmp1.getReversalRequestedQuantity())) {
                                invalidProductLsit.add(channelTransferItemsOldVOtmp1.getProductCode());
                                continue;
                            }
                            if (channelTransferItemsOldVO.getBalance() <= 0) {
                                throw new BTSLBaseException(PretupsErrorCodesI.CHNL_TRANSFER_ERROR_USER_BALANCE_NOT_EXIST_SUBKEY, new String[] { channelTransferItemsVO
                                    .getProductName() });
                            }

                            mrpAmt = PretupsBL.getSystemAmount(channelTransferItemsOldVOtmp1.getReversalRequestedQuantity());
                            channelTransferItemsVO.setRequestedQuantity(channelTransferItemsOldVOtmp1.getReversalRequestedQuantity());
                            channelTransferItemsVO.setRequiredQuantity(mrpAmt);
                            channelTransferItemsVO.setReversalRequest(true);
                            break;
                        }
                    }
                    BeanUtils.copyProperties(channelTransferItemsVO, channelTransferItemsOldVO);
                    itemsList.add(channelTransferItemsVO);
                    itemsList2.add(channelTransferItemsVO1);
                }
                try {
                    channelTransferVO = ChannelTransferVO.getInstance();
                    channelTransferVO = theForm.getChannelTransferVO();

                    if (oldChannelTransferVO != null) {
                        channelTransferVO.setTransferSubType(oldChannelTransferVO.getTransferSubTypeValue());
                    }
                    channelTransferVO.setChannelTransferitemsVOList(itemsList);
					channelTransferVO.setToUserMsisdn(((ChannelTransferVO)theForm.getOldTransferVoList().get(0)).getToUserCode());
					channelTransferVO.setRequestGatewayCode(PretupsI.GATEWAY_TYPE_WEB);
                    channelTransferVO.setOtfFlag(false);

                    
                    
                    ChannelTransferBL.loadAndCalculateTaxOnProducts(con, theForm.getFromCommissionProfileID(), theForm.getFromCommissionProfileVersion(), channelTransferVO,
                        true, "viewC2Cdetails", PretupsI.TRANSFER_TYPE_C2C);
                    channelTransferVO.setFromUserID(channelTransferVO.getToUserID());

                

                } catch (Exception e) {
                    theForm.setTransferItemsList(itemsList2);
                    theForm.setProductList(pList);
                    throw new BTSLBaseException(this, "saveC2CReverceTrx", "Exception in saving C2C reverse Transaction.");
                }

                if (oldChannelTransferVO != null) {
                    newChannelTransferVO = new ChannelTransferVO();
                    constructVofromOldVo(sessionUser, oldChannelTransferVO, newChannelTransferVO, curDate);
                    newChannelTransferVO.setFromUserID(oldChannelTransferVO.getCreatedBy());
                    newChannelTransferVO.setChannelRemarks(theForm.getRemarks().trim());
                    newChannelTransferVO.setRefTransferID(oldChannelTransferVO.getTransferID());
                    newChannelTransferVO.setTransferID(oldChannelTransferVO.getTransferID());
                    newChannelTransferVO.setLastModifiedTime(oldChannelTransferVO.getLastModifiedTime());
                    if (invalidProductLsit != null && !invalidProductLsit.isEmpty()) {
                        for (int i = 0, k = itemsList.size(); i < k; i++) {
                            channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
                            if (invalidProductLsit.contains(channelTransferItemsVO.getProductCode())) {
                                channelTransferItemsVO.setRequestedQuantity("0");
                                channelTransferItemsVO.setRequiredQuantity(0);
                                channelTransferItemsVO.setNetPayableAmount(0);
                                channelTransferItemsVO.setPayableAmount(0);
                                channelTransferItemsVO.setProductTotalMRP(0);
                            }
                        }
                    }
                    
                    // calculate total amount
                    calculateTotalAmt(theForm, itemsList);

                    long requestedQuantity = 0, totalStock = 0, totalComm = 0, transferMRP = 0, payableAmount = 0, netPayableAmount = 0, totalTax1 = 0, totalTax2 = 0, totalTax3 = 0;

                    for (int i = 0, k = itemsList.size(); i < k; i++) {
                        channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
                        requestedQuantity += channelTransferItemsVO.getRequiredQuantity();
                        transferMRP += channelTransferItemsVO.getUnitValue() * Double.parseDouble(channelTransferItemsVO.getRequestedQuantity());
                        payableAmount += channelTransferItemsVO.getPayableAmount();
                        netPayableAmount += channelTransferItemsVO.getNetPayableAmount();
                        totalTax1 += channelTransferItemsVO.getTax1Value();
                        totalTax2 += channelTransferItemsVO.getTax2Value();
                        totalTax3 += channelTransferItemsVO.getTax3Value();
                        totalComm += channelTransferItemsVO.getCommValue();
                        totalStock += channelTransferItemsVO.getBalance();
                        //channelTransferItemsVO.setProductMrpStr(PretupsBL.getDisplayAmount(channelTransferItemsVO.getRequiredQuantity()));
                    }

                    newChannelTransferVO.setNetPayableAmount(netPayableAmount);
                    newChannelTransferVO.setTotalTax1(totalTax1);
                    newChannelTransferVO.setTotalTax2(totalTax2);
                    newChannelTransferVO.setTotalTax3(totalTax3);
                    newChannelTransferVO.setPayableAmount(payableAmount);
                    newChannelTransferVO.setTransferMRP(transferMRP);
                    newChannelTransferVO.setRequestedQuantity(requestedQuantity);
                    newChannelTransferVO.setChannelTransferitemsVOList(itemsList);
                }
            }
            
            theForm.setChannelTransferVO(newChannelTransferVO);
            //save c2c rev txn func ends

            channelTransferVO = theForm.getChannelTransferVO();
            String oldTrxID = channelTransferVO.getTransferID();
            if (BTSLUtil.isNullString(oldTrxID)) {
                oldTrxID = ((ChannelTransferVO) theForm.getOldTransferVoList().get(0)).getTransferID();
                if (!BTSLUtil.isNullString(oldTrxID)) {
                    channelTransferVO.setTransferID(oldTrxID);
                }
            }
            curDate = channelTransferVO.getCreatedOn();
            int count = this.createReverseTrxC2C(con, channelTransferVO, true, curDate);
            constructVofromOldVo(sessionUser, oldChannelTransferVO, newChannelTransferVO, curDate);

            if (count > 0 && ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RVERSE_TXN_APPRV_LVL))).intValue() == 0) {
                channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                count = debitAndCreditUsers(con, channelTransferVO, true, null ,curDate);
            }
            
          //block added for sos settlelment in c2c reversal 
            UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
            String userID = null;
    		if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {
    			userID = channelTransferVO.getToUserID();
    		} 
    		
    		UserTransferCountsVO countsVO = userTransferCountsDAO.loadTransferCounts(con, userID, true);
    		
    		if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
    		{
        		if((channelTransferVO.getType() != null && !channelTransferVO.getType().equalsIgnoreCase(PretupsI.TRANSFER_TYPE_FOC)) && (channelTransferVO.getWalletType() !=null && !channelTransferVO.getWalletType().equalsIgnoreCase(PretupsI.FOC_WALLET_TYPE))) {
        			ChannelTransferBL.decreaseOptOTFCounts(con, channelTransferVO);
        		}
    		}
    		
    		if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {

    			channelTransferVO.setTransactionCode(PretupsI.C2C_REVERSAL);
    			Map hashmap = ChannelTransferBL.checkSOSstatusAndAmount(con, countsVO,
    					channelTransferVO);
    			if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true)) {
    				final String args[] = { channelTransferVO.getToUserName() };
    				throw new BTSLBaseException(PretupsErrorCodesI.SOS_PENDING_FOR_SETTLEMENT, args);
    			}
    			
    			

    			if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
    				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
    				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.SOS_REQUEST_TYPE);
    			}
    		
    		}
    		
    		BTSLMessages messages = null;
    		
    		if (count > 0) {
            	mcomCon.finalCommit();


                itemsList=channelTransferVO.getChannelTransferitemsVOList();
                channelTransferItemsVO=null;
                final UserDAO userDAO = new UserDAO();
                Locale locale = null;
                PushMessage pushMessage = null;
                String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
                String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
                // sender
                // sendeer
                final UserPhoneVO phoneVOSender = userDAO.loadUserPhoneVO(con, channelTransferVO.getFromUserID());
                final UserPhoneVO phoneVOReciever = userDAO.loadUserPhoneVO(con, channelTransferVO.getToUserID());

                
                for(int i=0,j=itemsList.size();i<j;i++) {
                    channelTransferItemsVO = (ChannelTransferItemsVO)itemsList.get(i);
                    
                if (phoneVOSender != null) {
                    country = phoneVOSender.getCountry();
                    language = phoneVOSender.getPhoneLanguage();
                    locale = new Locale(language, country);
                    final String[] array = { channelTransferVO.getTransferID(),channelTransferItemsVO.getProductName(), channelTransferVO.getRefTransferID(),PretupsBL.getDisplayAmount(channelTransferItemsVO.getPreviousBalance() + channelTransferItemsVO.getReceiverCreditQty()) };// ,reqProdMrp,prodbalanceSender,tempProd};
                    messages = new BTSLMessages(PretupsErrorCodesI.C2C_REVERSAL_TRX_SENDER, array); 
                    pushMessage = new PushMessage(phoneVOSender.getMsisdn(), messages, "", "", locale, channelTransferVO.getNetworkCode());
                    pushMessage.push();
                }
                if (phoneVOReciever != null)// receiver message for withdraw
                {
                    country = phoneVOReciever.getCountry();
                    language = phoneVOReciever.getPhoneLanguage();
                    locale = new Locale(language, country); // new:old:amount
                    // trans:total
                    // bala:product
                    final String[] array = { channelTransferVO.getTransferID(),channelTransferItemsVO.getProductName(), channelTransferVO.getRefTransferID() ,PretupsBL.getDisplayAmount(channelTransferItemsVO.getBalance() - channelTransferItemsVO.getSenderDebitQty() ) };// ,reqProdMrp,prodbalanceSender,tempProd};
                    messages = new BTSLMessages(PretupsErrorCodesI.C2C_REVERSAL_TRX_RECIEVER, array);
                    pushMessage = new PushMessage(phoneVOReciever.getMsisdn(), messages, "", "", locale, channelTransferVO.getNetworkCode());
                    pushMessage.push();
                }
                }

                final String arr[] = { channelTransferVO.getTransferID(), channelTransferVO.getRefTransferID(), channelTransferVO.getToUserName(), channelTransferVO
                    .getFromUserName() };

                if (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RVERSE_TXN_APPRV_LVL))).intValue() == 0) {
                    messages = new BTSLMessages("channelreversetrx.reverse.msg.success", arr, "usersearch");
                    resMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TXN_REVERSAL_SUCCESS, arr);
                    response.setMessage(resMsg);
    				response.setTransactionId(newChannelTransferVO.getTransferID());

                } else {
                	resMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TXN_REVERSAL_INIT_SUCCESS, arr);
                    response.setMessage(resMsg);
    				response.setTransactionId(newChannelTransferVO.getTransferID());

                }

            } else {
            	mcomCon.finalRollback();
                final String arr[] = { channelTransferVO.getTransferID(), channelTransferVO.getToUserName(), channelTransferVO.getFromUserName() };
                throw new BTSLBaseException(PretupsErrorCodesI.REVERSAL_TXN_FAILED,arr);
            }
            
    		
            
            
		} catch (BTSLBaseException e) {
            throw e;
        } catch (IllegalAccessException e) {
        	log.error(methodName, e.getMessage());
        	log.error(methodName, e.getMessage());
            throw new BTSLBaseException(e.getMessage());
        } catch (InvocationTargetException e) {
        	log.error(methodName, e.getMessage());
        	throw new BTSLBaseException(e.getMessage());
        } catch (NoSuchMethodException e) {
        	log.error(methodName, e.getMessage());
        	throw new BTSLBaseException(e.getMessage());
        } catch (Exception e) {
        	log.error(methodName, e.getMessage());
        	throw new BTSLBaseException(e.getMessage());
        } 
		
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited ");
		}
		
		return response;
	}
	
	private void calculateTotalAmt(ChnnlToChnnlReturnWithdrawForm theForm, ArrayList itemsList) throws BTSLBaseException {
		
		final String methodName = "calculateTotalAmt";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
        long totTax1 = 0L, totTax2 = 0L, totTax3 = 0L, totReqQty = 0L, totStock = 0L, totComm = 0L, netPayableAmount = 0, payableAmount = 0,totOthComm=0L,totalOtfValue=0;
        double mrpAmt=0.0,totMRP=0.0;
        ChannelTransferItemsVO channelTransferItemsVO = null;
        if (itemsList != null && !itemsList.isEmpty()) {
            for (int i = 0, j = itemsList.size(); i < j; i++) {
                channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
               mrpAmt = Double.parseDouble(channelTransferItemsVO.getProductMrpStr()) * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
                channelTransferItemsVO.setProductMrpStr(Double.toString(mrpAmt));
                totTax1 += channelTransferItemsVO.getTax1Value();
                totTax2 += channelTransferItemsVO.getTax2Value();
                totTax3 += channelTransferItemsVO.getTax3Value();
                totComm += channelTransferItemsVO.getCommValue();
				totOthComm += channelTransferItemsVO.getOthCommValue();
                totMRP += mrpAmt;
                totReqQty += channelTransferItemsVO.getRequiredQuantity();
                totStock += channelTransferItemsVO.getNetworkStock();
                netPayableAmount += channelTransferItemsVO.getNetPayableAmount();
                payableAmount += channelTransferItemsVO.getPayableAmount();
                totalOtfValue += channelTransferItemsVO.getOtfAmount();
                
            }
        }
        theForm.setTotalComm(PretupsBL.getDisplayAmount(totComm));
		theForm.setTotalOtherComm(PretupsBL.getDisplayAmount(totOthComm));
        theForm.setTotalTax1(PretupsBL.getDisplayAmount(totTax1));
        theForm.setTotalTax2(PretupsBL.getDisplayAmount(totTax2));
        theForm.setTotalTax3(PretupsBL.getDisplayAmount(totTax3));
        theForm.setTotalStock(PretupsBL.getDisplayAmount(totStock));
        theForm.setTotalReqQty(PretupsBL.getDisplayAmount(totReqQty));
        theForm.setTotalMRP(String.valueOf(totMRP));
        theForm.setPayableAmount(PretupsBL.getDisplayAmount(payableAmount));
        theForm.setNetPayableAmount(PretupsBL.getDisplayAmount(netPayableAmount));
        theForm.setTotalOtfValue(PretupsBL.getDisplayAmount(totalOtfValue));        
        
        if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited ");
		}
    }
	
	private void validateReversalInputParamters(ChnnlToChnnlReturnWithdrawForm theForm) throws BTSLBaseException {
		final String methodName = "validateReversalInputParamters";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
        final HashMap map = new HashMap();
        String[] arr = null;
        boolean isAllFieldBlank = true;
        for (int i = 0, k = theForm.getProductList().size(); i < k; i++) {
            final ChannelTransferItemsVO channelProductsVO = (ChannelTransferItemsVO) theForm.getProductList().get(i);
            String rqstQty = channelProductsVO.getReversalRequestedQuantity();
            if (rqstQty == null || rqstQty.equals("")) {
                channelProductsVO.setReversalRequestedQuantity(PretupsBL.getDisplayAmount(channelProductsVO.getRequiredQuantity()));
            }
            rqstQty = channelProductsVO.getReversalRequestedQuantity();
            arr = new String[2];
            arr[0] = channelProductsVO.getShortName();
            if (!BTSLUtil.isNullString(rqstQty)) {
                isAllFieldBlank = false;
                if (!BTSLUtil.isDecimalValue(rqstQty)) {
                	throw new BTSLBaseException(PretupsErrorCodesI.REVERSAL_QUANTITY_NOT_VALID, arr);
                } else if (!BTSLUtil.isNullString(rqstQty)) {
                    long requestedQty = 0;
                    try {
                        requestedQty = PretupsBL.getSystemAmount(rqstQty);
                        // to perform the basic validation on the requested qty.
                        // as should be greater than 0
                        if (requestedQty <= 0) {
                        	throw new BTSLBaseException(PretupsErrorCodesI.REVERSAL_QUANTITY_NOT_VALID, arr);

                        }
                        // ends here
                        else if (requestedQty > channelProductsVO.getBalance()) {
                        	throw new BTSLBaseException(PretupsErrorCodesI.REQ_QUANTITY_MORE_THAN_USER_BALANCE, arr);
                        } else if (requestedQty > channelProductsVO.getRequiredQuantity()) {
                            arr[1] = PretupsBL.getDisplayAmount(channelProductsVO.getRequiredQuantity());
                            throw new BTSLBaseException(PretupsErrorCodesI.REQ_QUANTITY_MORE_THAN_TXN_VALUE, arr);
                        }
                    } catch (BTSLBaseException e) {
                    	throw new BTSLBaseException(PretupsErrorCodesI.REVERSAL_QUANTITY_NOT_VALID, arr);
                    }
                }
            }
        }
        if (isAllFieldBlank) {
        	throw new BTSLBaseException(PretupsErrorCodesI.NO_PRODUCT_SELECTED_FOR_REVERSAL, arr);
        }
        
        if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited ");
		}
        
    }
	
	private void constructVofromOldVo(ChannelUserVO sessionUser, ChannelTransferVO p_oldChannelTransferVO, ChannelTransferVO p_channelTransferVO, Date p_curDate) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("constructVofromOldVo",
                "Entered old ChannelTransferVO: " + p_oldChannelTransferVO + " ChannelTransferVO: " + p_channelTransferVO + " CurDate " + p_curDate);
        }

        final ChannelUserVO channelUserVO = sessionUser;

        p_oldChannelTransferVO.setRefTransferID(p_channelTransferVO.getTransferID());
        p_channelTransferVO.setRefTransferID(p_oldChannelTransferVO.getTransferID());
        
        // p_channelTransferVO=p_oldChannelTransferVO;
        p_channelTransferVO.setTransferDate(p_curDate);
        p_channelTransferVO.setModifiedBy(channelUserVO.getActiveUserID());
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setTransferSubType(PretupsI.TRANSFER_TYPE_REVERSE_SUB_TYPE);
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setCreatedBy(channelUserVO.getActiveUserID());
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
        p_channelTransferVO.setCloseDate(p_curDate);
        p_channelTransferVO.setTransferInitatedBy(channelUserVO.getUserID());
        p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
        p_channelTransferVO.setNetworkCode(p_oldChannelTransferVO.getNetworkCode());
        p_channelTransferVO.setNetworkCodeFor(p_oldChannelTransferVO.getNetworkCodeFor());
        p_channelTransferVO.setCategoryCode(p_oldChannelTransferVO.getReceiverCategoryCode());
        p_channelTransferVO.setReceiverCategoryCode(p_oldChannelTransferVO.getCategoryCode());
        p_channelTransferVO.setReceiverGradeCode(p_oldChannelTransferVO.getSenderGradeCode());
        p_channelTransferVO.setSenderGradeCode(p_oldChannelTransferVO.getReceiverGradeCode());
        p_channelTransferVO.setDomainCode(p_oldChannelTransferVO.getReceiverDomainCode());
        p_channelTransferVO.setReceiverDomainCode(p_oldChannelTransferVO.getDomainCode());
        p_channelTransferVO.setFromUserID(p_oldChannelTransferVO.getToUserID());
        p_channelTransferVO.setToUserID(p_oldChannelTransferVO.getFromUserID());
        p_channelTransferVO.setFromUserName(p_oldChannelTransferVO.getToUserName());
        p_channelTransferVO.setToUserName(p_oldChannelTransferVO.getFromUserName());
        p_channelTransferVO.setToUserCode(p_oldChannelTransferVO.getFromUserCode());
        p_channelTransferVO.setFromUserCode(p_oldChannelTransferVO.getToUserCode());
        p_channelTransferVO.setGraphicalDomainCode(p_oldChannelTransferVO.getReceiverGgraphicalDomainCode());
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_oldChannelTransferVO.getGraphicalDomainCode());
        p_channelTransferVO.setSenderTxnProfile(p_oldChannelTransferVO.getReceiverTxnProfile());
        p_channelTransferVO.setReceiverTxnProfile(p_oldChannelTransferVO.getSenderTxnProfile());
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        p_channelTransferVO.setReferenceNum(p_oldChannelTransferVO.getReferenceNum());
        p_channelTransferVO.setExternalTxnNum(p_oldChannelTransferVO.getExternalTxnNum());
        p_channelTransferVO.setExternalTxnDate(p_oldChannelTransferVO.getExternalTxnDate());
        p_channelTransferVO.setType(p_oldChannelTransferVO.getType());
        p_channelTransferVO.setProductType(p_oldChannelTransferVO.getProductType());
        p_channelTransferVO.setTransferCategoryCode(p_oldChannelTransferVO.getTransferCategoryCode());
        p_channelTransferVO.setPayInstrumentType(p_oldChannelTransferVO.getPayInstrumentType());
        p_channelTransferVO.setPayInstrumentNum(p_oldChannelTransferVO.getPayInstrumentNum());
        p_channelTransferVO.setPayInstrumentDate(p_oldChannelTransferVO.getPayInstrumentDate());
        p_channelTransferVO.setPayInstrumentAmt(p_oldChannelTransferVO.getPayInstrumentAmt());
        p_channelTransferVO.setPaymentInstSource(p_oldChannelTransferVO.getPaymentInstSource());
        p_channelTransferVO.setSenderLoginID(p_oldChannelTransferVO.getReceiverLoginID());
        p_channelTransferVO.setReceiverLoginID(p_oldChannelTransferVO.getSenderLoginID());
        p_channelTransferVO.setCommProfileSetId(p_oldChannelTransferVO.getCommProfileSetId());
        p_channelTransferVO.setCommProfileVersion(p_oldChannelTransferVO.getCommProfileVersion());
        p_channelTransferVO.setTransferCategory(p_oldChannelTransferVO.getTransferCategory());
        p_channelTransferVO.setDefaultLang(p_oldChannelTransferVO.getDefaultLang());
        p_channelTransferVO.setSecondLang(p_oldChannelTransferVO.getSecondLang());
        p_channelTransferVO.setControlTransfer(p_oldChannelTransferVO.getControlTransfer());
        p_channelTransferVO.setTransferMRP(p_oldChannelTransferVO.getTransferMRP());
        p_channelTransferVO.setDualCommissionType(p_oldChannelTransferVO.getDualCommissionType());
        
        if (channelUserVO.getSessionInfoVO().getMessageGatewayVO() != null) {
            p_channelTransferVO.setRequestGatewayCode(channelUserVO.getSessionInfoVO().getMessageGatewayVO().getGatewayCode());
            p_channelTransferVO.setRequestGatewayType(channelUserVO.getSessionInfoVO().getMessageGatewayVO().getGatewayType());
        }

        if (log.isDebugEnabled()) {
            log.debug("constructVofromOldVo", "Exited ChannelTransferVO: " + p_channelTransferVO + " CurDate " + p_curDate);
        }

    }
	
	private int createReverseTrxC2C(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean p_fromWEB, Date p_curDate) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
        	log.debug("createReverseTrxC2C",
                "Entered p_channelTransferVO: " + p_channelTransferVO + " fromWeb :" + p_fromWEB + "p_curDate" + p_curDate);
        }

        int updateCount = 0;
        final String oldTrxID = p_channelTransferVO.getTransferID();
        if (BTSLUtil.isNullString(oldTrxID)) {
            return updateCount;
        }

        /*
         * generate the TXN ID for the txn as Reverse
         */

        ChannelTransferBL.genrateChnnlToChnnlReversalTrx(p_channelTransferVO);

        // insert the TXN data in the parent and child tables.
        final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
        final ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
        if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PROCESS_FEE_REV_ALLOWED))).booleanValue()) {
            for (final Object obj : p_channelTransferVO.getChannelTransferitemsVOList()) {
                final ChannelTransferItemsVO itemVo = (ChannelTransferItemsVO) obj;
                // itemVo.setNetPayableAmount(itemVo.getNetPayableAmount()-itemVo.getTax1Value()-itemVo.getTax2Value()-itemVo.getTax3Value());
                itemVo.setNetPayableAmount(itemVo.getPayableAmount());
                itemVo.setTax1Value(0);
                itemVo.setTax1Rate(0);
                itemVo.setTax2Value(0);
                itemVo.setTax2Rate(0);
                itemVo.setTax3Value(0);
                itemVo.setTax3Rate(0);
                if(PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType()))
                {
                	itemVo.setSenderDebitQty(itemVo.getSenderDebitQty()+itemVo.getCommQuantity());
                	itemVo.setReceiverCreditQty(itemVo.getReceiverCreditQty()-itemVo.getCommQuantity());
                }
            }
            // p_channelTransferVO.setNetPayableAmount(p_channelTransferVO.getNetPayableAmount()-p_channelTransferVO.getTotalTax1()-p_channelTransferVO.getTotalTax2()-p_channelTransferVO.getTotalTax3());
            p_channelTransferVO.setNetPayableAmount(p_channelTransferVO.getPayableAmount());
            p_channelTransferVO.setTotalTax1(0);
            p_channelTransferVO.setTotalTax2(0);
            p_channelTransferVO.setTotalTax3(0);
        }

        updateCount = channelTransferDAO.addChannelTransfer(p_con, p_channelTransferVO);
        // String revTrxID=p_channelTransferVO.getTransferID();
        // p_channelTransferVO.setTransferID(oldTrxID);
        p_channelTransferVO.setRefTransferID(oldTrxID);
        updateCount = channelTransferWebDAO.updatChannelTransferAfterReverseTrx(p_con, p_channelTransferVO);
        if (updateCount <= 0) {
            return updateCount;
        }
        UserEventRemarksVO userEventRemarksVO = null;
        userEventRemarksVO = constructEventVOFromChannelTransferVO(userEventRemarksVO, p_channelTransferVO);
        updateCount = new UserEventRemarksDAO().addUserEventRemark(p_con, userEventRemarksVO);

        if (log.isDebugEnabled()) {
        	log.debug("createReverseTrxC2C", "Exited updateCount: " + updateCount);
        }

        return updateCount;
    }
	
	private UserEventRemarksVO constructEventVOFromChannelTransferVO(UserEventRemarksVO p_userEventRemarksVO, ChannelTransferVO p_channelTransferVO) {
		final String methodName = "constructEventVOFromChannelTransferVO";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
        p_userEventRemarksVO = new UserEventRemarksVO();
        p_userEventRemarksVO.setCreatedBy(p_channelTransferVO.getModifiedBy());
        p_userEventRemarksVO.setCreatedOn(p_channelTransferVO.getModifiedOn());
        p_userEventRemarksVO.setUserType(PretupsI.CHANNEL_USER_TYPE);
        p_userEventRemarksVO.setModule(PretupsI.C2S_MODULE);
        final String remarks = p_channelTransferVO.getChannelRemarks() != null ? p_channelTransferVO.getChannelRemarks() : p_channelTransferVO.getFirstApprovalRemark();
        p_userEventRemarksVO.setRemarks(remarks);
        p_userEventRemarksVO.setEventType(PretupsI.TRANSFER_TYPE_REVERSE_EVENT_TYPE);
        p_userEventRemarksVO.setUserID(p_channelTransferVO.getToUserID());
        p_userEventRemarksVO.setMsisdn(p_channelTransferVO.getToUserCode());
        if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited ");
		}
        return p_userEventRemarksVO;
    }
	
	private int debitAndCreditUsers(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean p_fromWEB, String p_forwardPath, Date p_curDate) throws BTSLBaseException {

        final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();

        final UserBalancesVO userBalancesVO = constructBalanceVOFromTxnVO(p_channelTransferVO);
        userBalancesVO.setUserID(p_channelTransferVO.getFromUserID());// reciever
        userBalancesDAO.updateUserDailyBalances(p_con, p_curDate, userBalancesVO);
        userBalancesVO.setUserID(p_channelTransferVO.getToUserID());
        userBalancesDAO.updateUserDailyBalances(p_con, p_curDate, userBalancesVO);// sender
        final ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
        int updateCount = 0;

        // debit the reciever
        updateCount = channelUserWebDAO.debitUserBalancesForRevTxn(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);
        if (updateCount > 0) {
            updateCount = channelUserWebDAO.creditUserBalancesForRevTxn(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);
        }

        // insert the TXN data in the parent and child tables.
        final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
        if (updateCount > 0) {
            updateCount = channelTransferDAO.updateReverseTransferApp1(p_con, p_channelTransferVO);
        }
        final String revTrxID = p_channelTransferVO.getTransferID();

        if (updateCount <= 0) {
            return updateCount;
        }
        UserEventRemarksVO userEventRemarksVO = null;
        userEventRemarksVO = constructEventVOFromChannelTransferVO(userEventRemarksVO, p_channelTransferVO);
        updateCount = new UserEventRemarksDAO().addUserEventRemark(p_con, userEventRemarksVO);
        
        if (log.isDebugEnabled()) {
            log.debug("reversalTrxChannelToChannel", "Exited updateCount: " + updateCount);
        }

        return updateCount;

    }
	
	private UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
        if (log.isDebugEnabled()) {
        	log.debug("constructBalanceVOFromTxnVO", "Entered:ChannelTransferVO=>" + p_channelTransferVO);
        }
        final UserBalancesVO userBalancesVO = UserBalancesVO.getInstance();
        userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
        userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
        userBalancesVO.setLastTransferOn(p_channelTransferVO.getTransferDate());

        if (log.isDebugEnabled()) {
        	log.debug("constructBalanceVOFromTxnVO", "Exiting userBalancesVO=" + userBalancesVO);
        }
        return userBalancesVO;
    }

}