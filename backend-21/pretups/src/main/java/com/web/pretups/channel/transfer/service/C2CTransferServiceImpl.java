package com.web.pretups.channel.transfer.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.btsl.pretups.channel.transfer.businesslogic.*;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.txn.pretups.channel.profile.businesslogic.TransferProfileTxnDAO;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;
import com.web.pretups.channel.transfer.web.C2CTransferModel;

import nl.captcha.Captcha;


@Service("C2CTransferService")

public class C2CTransferServiceImpl implements C2CTransferService {
	public static final Log log = LogFactory.getLog(C2CTransferServiceImpl.class.getName());
	private static final String PANEL_NO = "PanelNo";
	private static final String SUCCESS_KEY = "success";
	private static final String FAIL_KEY = "fail";
	private static final String MODEL_KEY = "c2cTransfer";

	public List<ListValueVO> loadCategoryList(ChannelUserVO channelUserVO) throws BTSLBaseException {
		if (log.isDebugEnabled()) {
			log.debug("C2CTransferServiceImpl#loadCategoryList", PretupsI.ENTERED);
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		final ArrayList<ListValueVO> catgeoryList = new ArrayList<ListValueVO>();
		final ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
		try {
			mcomCon = new MComConnection();
			try {
				con = mcomCon.getConnection();
			} 
			catch (SQLException e) 
			{
				log.error("loadCategoryList","SQLException : ", e.getMessage());
			}
			final ArrayList catgList = channelTransferRuleWebDAO.loadTransferRulesCategoryList(con,
					channelUserVO.getNetworkID(), channelUserVO.getCategoryCode());

			ChannelTransferRuleVO rulesVO = null;
			// Now filter the transfer rule list for which the Transfer allowed
			// field is 'Y' or Transfer Channel by pass is Y
			for (int i = 0, k = catgList.size(); i < k; i++) {
				rulesVO = (ChannelTransferRuleVO) catgList.get(i);
				if (PretupsI.YES.equals(rulesVO.getDirectTransferAllowed())
						|| PretupsI.YES.equals(rulesVO.getTransferChnlBypassAllowed())) {
					// attached the domain code for the display purpose
					catgeoryList
							.add(new ListValueVO(rulesVO.getToCategoryDes() + " (" + rulesVO.getToDomainCode() + ")",
									rulesVO.getToCategory()));
				}
			}
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2CTransferServiceImpl#loadCategoryList");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("C2CTransferServiceImpl#loadCategoryList", PretupsI.EXITED);
			}
			
		}

		return catgeoryList;

	}

	@Override
	public List<AutoCompleteUserDetailsResponseVO> loadUserList(String categorycode, String userName, ChannelUserVO channelUserVO)
			throws BTSLBaseException {
		if (log.isDebugEnabled()) {
			log.debug("C2CTransferServiceImpl#loadUserList", PretupsI.ENTERED +"");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
		ArrayList<AutoCompleteUserDetailsResponseVO> list = null;
		try {
			mcomCon = new MComConnection();
			try {
				con = mcomCon.getConnection();
			} 
			
			catch (SQLException e) 
			{
				log.error("loadUserList", "SQLException : ", e.getMessage());
			}
			
			final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con,
					channelUserVO.getNetworkID(), channelUserVO.getDomainID(), channelUserVO.getCategoryCode(),
					categorycode, PretupsI.TRANSFER_RULE_TYPE_CHANNEL, false);
			list = ChannelUserBL.loadChannelUserForXfrWithXfrRule(con, channelTransferRuleVO, categorycode, userName,
					channelUserVO);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2CTransferServiceImpl#loadUserList");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("C2CTransferServiceImpl#loadUserList", PretupsI.EXITED);
			}
		}

		return list;
	}


	@SuppressWarnings("unchecked")
	@Override
	public boolean loadUserProductsdetails(C2CTransferModel theForm, ChannelUserVO sessionUser,
			BindingResult bindingResult, Locale locale, Model model, HttpServletRequest request) throws Exception {
		final String methodName = "C2CTransferServiceImpl#loadUserProductsdetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED );
		}
		
		boolean receiverStatusAllowed = false;
		boolean senderStatusAllowed = false;
		final C2CTransferModel theFormNew = theForm;
		final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		final Date curDate = new Date();
		ChannelTransferRuleVO channelTransferRuleVO = null;
		ChannelTransferVO channelTransferVO = new ChannelTransferVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		String trfRuleID = null;
		String userName = null;
		AutoCompleteUserDetailsResponseVO listValueVO = null;

		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			request.getSession().setAttribute(MODEL_KEY, theFormNew);
			/* for field validation Start */
			if (request.getParameter("submitMsisdn") != null) {
				CommonValidator commonValidator = new CommonValidator(
						"configfiles/transfer/validator-channel2Channeltransfer.xml", theFormNew,
						"Channel2ChannelTransferModelMsisdn");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				model.addAttribute(PANEL_NO, "Panel-One");
				request.getSession().setAttribute(PANEL_NO, "Panel-One");
			}
			if (request.getParameter("submitUsrSearch") != null) {
				CommonValidator commonValidator = new CommonValidator(
						"configfiles/transfer/validator-channel2Channeltransfer.xml", theFormNew,
						"Channel2ChannelTransferSearch");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				model.addAttribute(PANEL_NO, "Panel-Two");
				request.getSession().setAttribute(PANEL_NO, "Panel-Two");
			}
			if (bindingResult.hasFieldErrors()) {

				return false;
			}
			/* for field validation End */
			if (!BTSLUtil.isNullString(sessionUser.getActiveUserMsisdn())
					&& !PretupsI.NOT_AVAILABLE.equals(sessionUser.getActiveUserMsisdn())
					&& !sessionUser.getMsisdn().equals(sessionUser.getActiveUserMsisdn())) {
				theFormNew.setDisplayMsisdn(sessionUser.getActiveUserMsisdn());
			} else {
				theFormNew.setDisplayMsisdn(sessionUser.getMsisdn());
			}
			loadloggedinUserdetails(theFormNew,model,sessionUser);
			
			
			
			
			if (!BTSLUtil.isNullString(theForm.getUserCode())) {
				theFormNew.setUserCode(PretupsBL.getFilteredMSISDN(theForm.getUserCode()));
			}
			if (BTSLUtil.isNullString(theForm.getUserCode())) {
				
				channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, sessionUser.getNetworkID(),
						sessionUser.getDomainID(),sessionUser.getCategoryVO().getCategoryCode(), theForm.getToCategoryCode(),
						PretupsI.TRANSFER_RULE_TYPE_CHANNEL, false);
				if (channelTransferRuleVO == null) {
					model.addAttribute(FAIL_KEY,
							PretupsRestUtil.getMessageString("pretups.message.channeltransfer.transferrulenotdefine"));
					return false;
				} else
				trfRuleID = channelTransferRuleVO.getTransferRuleID();
				userName = theForm.getSearchLoginId();
				theForm.setToUserName(userName);
				if (!BTSLUtil.isNullString(userName)) {
					String[] parts = userName.split("\\(");
					userName = parts[0];
					 theForm.setToUserName(userName);
					try {
						String userID = parts[1];
						userID = userID.replaceAll("\\)", "");
						theForm.setUserID(userID);
					} catch (Exception e) {
						log.error(methodName, "Name selected did not had ID "
								+ e);
						log.errorTrace(methodName, e);
					}
					/*String loginId = parts[1];
					loginId = loginId.substring(0, loginId.length() - 1);

					theFormNew.setUserID(loginId);
					theFormNew.setToUserName(userId);
					theFormNew.setToUserID(loginId);*/
					
					 
					 final ArrayList userList = ChannelUserBL.loadChannelUserForXfrWithXfrRule(con, channelTransferRuleVO, theForm.getToCategoryCode(), userName, sessionUser);
		                if (userList.size() == 1) {
		                    listValueVO = (AutoCompleteUserDetailsResponseVO) userList.get(0);
		                    theForm.setUserID(listValueVO.getUserID());
		                    theForm.setToUserName(listValueVO.getUserName());
		                    theForm.setToUserID(listValueVO.getUserID());
		                }else if (userList.size() > 1) {
		                    boolean isExist = false;

		                    if (!BTSLUtil.isNullString(theForm.getUserID())) {
		                        for (int i = 0, k = userList.size(); i < k; i++) {
		                            listValueVO = (AutoCompleteUserDetailsResponseVO) userList.get(i);
		                            if (listValueVO.getUserID().equals(theForm.getUserID()) && theForm.getToUserName().compareTo(listValueVO.getUserName()) == 0) {
		                                theForm.setUserID(listValueVO.getUserID());
		                                theForm.setToUserName(listValueVO.getUserName());
		                                theForm.setToUserID(listValueVO.getUserID());
		                                isExist = true;
		                                break;
		                            }
		                        }

		                    } else {
		                        ListValueVO listValueNextVO = null;
		                        for (int i = 0, k = userList.size(); i < k; i++) {
		                            listValueVO = (AutoCompleteUserDetailsResponseVO) userList.get(i);
		                            if (theForm.getToUserName().compareTo(listValueVO.getUserName()) == 0) {
		                                if (((i + 1) < k)) {
		                                    listValueNextVO = (ListValueVO) userList.get(i + 1);
		                                    if (theForm.getToUserName().compareTo(listValueNextVO.getLabel()) == 0) {
		                                        isExist = false;
		                                        break;
		                                    }
		                                    theForm.setUserID(listValueVO.getUserID());
		                                    theForm.setToUserName(listValueVO.getUserName());
		                                    theForm.setToUserID(listValueVO.getUserID());
		                                    isExist = true;
		                                    break;
		                                }
		                                theForm.setUserID(listValueVO.getUserID());
		                                theForm.setToUserName(listValueVO.getUserName());
		                                theForm.setToUserID(listValueVO.getUserID());
		                                isExist = true;
		                                break;
		                            }
		                        }
		                    }
		                    if (!isExist) {
		                       model.addAttribute(FAIL_KEY,
		    							PretupsRestUtil.getMessageString("pretups.channeltransfer.chnltochnlsearchuser.usermorethanoneexist.msg"));
		    					return false;
		                   
		                    }
		                } else {
		                	 model.addAttribute(FAIL_KEY,
		    							PretupsRestUtil.getMessageString("pretups.channeltransfer.chnltochnlsearchuser.usernotfound.msg"));
		    					return false;
				                }
			
				}

			}

			final ChannelUserVO fromChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con,
					sessionUser.getActiveUserID(), false, curDate, false);
			String args[] = null;
			if (fromChannelUserVO == null) {
				args = new String[] { theForm.getFromUserName() };
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("pretups.message.channeltransfer.userdetailnotfound.msg", args));
				return false;
			} else {
				final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(
						fromChannelUserVO.getNetworkID(), fromChannelUserVO.getCategoryCode(),
						fromChannelUserVO.getUserType(), PretupsI.REQUEST_SOURCE_TYPE_WEB);
				if (userStatusVO != null) {
					final String userStatusAllowed = userStatusVO.getUserSenderAllowed();
					final String status[] = userStatusAllowed.split(",");
					for (int i = 0; i < status.length; i++) {
						if (status[i].equals(fromChannelUserVO.getStatus())) {
							senderStatusAllowed = true;
						}
					}
				} else {
					args = new String[] { theForm.getFromUserName() };
					model.addAttribute(FAIL_KEY,
							PretupsRestUtil.getMessageString("pretups.message.channeltransfer.usernotallowed.msg", args));
					return false;

				}

			}
			if (!senderStatusAllowed) {
				args = new String[] { theForm.getFromUserName() };
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("pretups.message.channeltransfer.usersuspended.msg", args));
				return false;

			} else if (fromChannelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
				args = new String[] { theForm.getFromUserName() };
				model.addAttribute(FAIL_KEY, PretupsRestUtil
						.getMessageString("pretups.message.channeltransfer.usernocommprofileapplicable.msg", args));
				return false;

			}
			// added by nilesh : for auto c2c transfer
			channelTransferVO.setTransferProfileID(fromChannelUserVO.getTransferProfileID());
			// end
			ChannelUserVO toChannelUserVO = null;
			String argument = null;
			final UserDAO userDAO = new UserDAO();
			UserPhoneVO phoneVO = null;
			if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
				if (BTSLUtil.isNullString(theForm.getUserCode())) {
					toChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, theForm.getToUserID(),
							false, curDate, false);
					argument = theForm.getToUserName();
				} else {
					toChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, theForm.getUserCode(), true,
							curDate, false);
					argument = theForm.getUserCode();
				}
			} else {
				if (!BTSLUtil.isNullString(theForm.getUserCode())) {
					phoneVO = userDAO.loadUserAnyPhoneVO(con, theForm.getUserCode());
					if (phoneVO == null) {
						args = new String[] { theForm.getUserCode() };
						model.addAttribute(FAIL_KEY, PretupsRestUtil
								.getMessageString("pretups.message.channeltransfer.userdetailnotfound.msg", args));
						return false;

					}
					argument = theForm.getUserCode();
					theFormNew.setToUserID(phoneVO.getUserId());
				} else {
					argument = theForm.getToUserName();
				}
				toChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, theForm.getToUserID(), false,
						curDate, false);
				if (toChannelUserVO == null) {
					if(!BTSLUtil.isNullString(theForm.getUserCode())){
						args = new String[] { theForm.getUserCode() };
					}
					if(!BTSLUtil.isNullString(theForm.getToUserName())){
						args = new String[] { theForm.getToUserName()};
					}
					
					model.addAttribute(FAIL_KEY,
							PretupsRestUtil.getMessageString("pretups.message.channeltransfer.userdetailnotfound.msg", args));
					return false;

				}

				if (phoneVO != null && !(phoneVO.getPrimaryNumber()).equalsIgnoreCase("Y")) {
					toChannelUserVO.setPrimaryMsisdn(toChannelUserVO.getMsisdn());
					toChannelUserVO.setMsisdn(phoneVO.getMsisdn());
				} else {
					toChannelUserVO.setPrimaryMsisdn(toChannelUserVO.getMsisdn());
				}
				theFormNew.setToPrimaryMSISDN(toChannelUserVO.getPrimaryMsisdn());
			}
			if (toChannelUserVO == null) {
				args = new String[] { argument };
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("pretups.message.channeltransfer.userdetailnotfound.msg", args));
				return false;

			} else {
				final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(
						toChannelUserVO.getNetworkID(), toChannelUserVO.getCategoryCode(),
						toChannelUserVO.getUserType(), PretupsI.REQUEST_SOURCE_TYPE_WEB);
				if (userStatusVO != null) {
					final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
					final String status[] = userStatusAllowed.split(",");
					for (int i = 0; i < status.length; i++) {
						if (status[i].equals(toChannelUserVO.getStatus())) {
							receiverStatusAllowed = true;
						}
					}
				} else {
					args = new String[] { argument };
					model.addAttribute(FAIL_KEY,
							PretupsRestUtil.getMessageString("pretups.message.channeltransfer.usernotallowed.msg", args));
					return false;

				}
			}
			theFormNew.setToCategoryCode(toChannelUserVO.getCategoryCode());
			if (!receiverStatusAllowed) {
				args = new String[] { argument };
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("pretups.message.channeltransfer.usersuspended.msg", args));
				return false;

			} else if (toChannelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
				args = new String[] { argument };
				model.addAttribute(FAIL_KEY, PretupsRestUtil
						.getMessageString("pretups.message.channeltransfer.usernocommprofileapplicable.msg", args));
				return false;

			}
			boolean isOutsideHirearchy = false;
			boolean isUserCodeFlag = false;
			if (!BTSLUtil.isNullString(theForm.getUserCode())) {
				isUserCodeFlag = true;
			}

			fromChannelUserVO.setCommissionProfileSuspendMsg(fromChannelUserVO.getCommissionProfileLang2Msg());
			toChannelUserVO.setCommissionProfileSuspendMsg(toChannelUserVO.getCommissionProfileLang2Msg());

			final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
			if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
				fromChannelUserVO.setCommissionProfileSuspendMsg(fromChannelUserVO.getCommissionProfileLang1Msg());
				toChannelUserVO.setCommissionProfileSuspendMsg(toChannelUserVO.getCommissionProfileLang1Msg());
			}

			isOutsideHirearchy = ChannelTransferBL.validateSenderAndReceiverWithXfrRule(con, fromChannelUserVO,
					toChannelUserVO, isUserCodeFlag, "fromsearch", true, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
			trfRuleID = toChannelUserVO.getTransferRuleID();

			theFormNew.setOutsideHierarchyFlag(isOutsideHirearchy);
			theFormNew.setTransferCategory(fromChannelUserVO.getTransferCategory());
			// to check user status
			if (PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(fromChannelUserVO.getOutSuspened())) {
				model.addAttribute(FAIL_KEY, PretupsRestUtil
						.getMessageString("pretups.channeltransfer.chnltochnlsearchuser.usernotfound.msg.transferoutsuspend"));
				return false;

			}

			if (PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(toChannelUserVO.getInSuspend())) {
				model.addAttribute(FAIL_KEY, PretupsRestUtil
						.getMessageString("pretups.channeltransfer.chnltochnlsearchuser.usernotfound.msg.transferinsuspended"));
				return false;

			}

			theFormNew.setFromUserName(fromChannelUserVO.getUserName());
			theFormNew.setFromMSISDN(fromChannelUserVO.getMsisdn());
			theFormNew.setFromGradeCode(fromChannelUserVO.getUserGrade());
			theFormNew.setFromGradeCodeDesc(fromChannelUserVO.getUserGradeName());
			theFormNew.setFromCommissionProfileID(fromChannelUserVO.getCommissionProfileSetID());
			theFormNew.setFromCommissionProfileIDDesc(fromChannelUserVO.getCommissionProfileSetName());
			theFormNew.setFromCommissionProfileVersion(fromChannelUserVO.getCommissionProfileSetVersion());
			theFormNew.setFromTxnProfile(fromChannelUserVO.getTransferProfileID());
			theFormNew.setFromTxnProfileDesc(fromChannelUserVO.getTransferProfileName());
			theFormNew.setFromCategoryDesc(fromChannelUserVO.getCategoryVO().getCategoryName());
			theFormNew.setFromCategoryCode(fromChannelUserVO.getCategoryVO().getCategoryCode());
			theFormNew.setFromGeoDomain(fromChannelUserVO.getGeographicalCode());
			theFormNew.setFromGeoDomainDesc(fromChannelUserVO.getGeographicalDesc());
			theFormNew.setFromChannelUserStatus(fromChannelUserVO.getStatus());
			theFormNew.setFromUsrDualCommType(fromChannelUserVO.getDualCommissionType());
			
			theFormNew.setToUserName(toChannelUserVO.getUserName());
			theFormNew.setToUserID(toChannelUserVO.getUserID());
			theFormNew.setToMSISDN(toChannelUserVO.getMsisdn());
			theFormNew.setToGradeCode(toChannelUserVO.getUserGrade());
			theFormNew.setToGradeCodeDesc(toChannelUserVO.getUserGradeName());
			theFormNew.setToCommissionProfileID(toChannelUserVO.getCommissionProfileSetID());
			theFormNew.setToCommissionProfileIDDesc(toChannelUserVO.getCommissionProfileSetName());
			theFormNew.setToCommissionProfileVersion(toChannelUserVO.getCommissionProfileSetVersion());
			theFormNew.setToUsrDualCommType(toChannelUserVO.getDualCommissionType());
			theFormNew.setToTxnProfile(toChannelUserVO.getTransferProfileID());
			theFormNew.setToTxnProfileDesc(toChannelUserVO.getTransferProfileName());
			theFormNew.setCurrentDate(BTSLUtil.getDateStringFromDate(curDate));
			theFormNew.setToCategoryCode(toChannelUserVO.getCategoryVO().getCategoryCode());
			theFormNew.setToCategoryDesc(toChannelUserVO.getCategoryVO().getCategoryName());
			theFormNew.setToGeoDomain(toChannelUserVO.getGeographicalCode());
			theFormNew.setToGeoDomainDesc(toChannelUserVO.getGeographicalDesc());
			theFormNew.setToDomainCode(toChannelUserVO.getDomainID());
			theFormNew.setToChannelUserStatus(toChannelUserVO.getStatus());
			theFormNew.setToUsrDualCommType(toChannelUserVO.getDualCommissionType());

			final ArrayList<ChannelTransferItemsVO> productList = ChannelTransferBL.loadC2CXfrProductsWithXfrRule(con,
					fromChannelUserVO.getUserID(), fromChannelUserVO.getNetworkID(),
					toChannelUserVO.getCommissionProfileSetID(), curDate, trfRuleID, "fromsearch", true,
					theForm.getFromUserName(), locale, null, PretupsI.TRANSFER_TYPE_C2C);

			ChannelTransferItemsVO channelTransferItemsVO = null;
			for (int i = 0, j = productList.size(); i < j; i++) {
				channelTransferItemsVO = productList.get(i);
			}
			theFormNew.setProductCode(channelTransferItemsVO.getProductCode());

			theFormNew.setProductList(productList);

		} catch (BTSLBaseException e) {
			log.errorTrace(methodName, e);
			model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage(),e.getArgs()));
            return false;
			
		}
		catch (Exception e) {
            log.error("loadWithdrawUserProducts", "Exception:e =" + e);
            log.errorTrace(methodName, e);
            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
            return false;
            }
		finally {
			if (mcomCon != null) {
				mcomCon.close("C2CTransferServiceImpl#loadUserProductsdetails");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED );
			}
		}
		return true;
	}

	@Override
	public boolean channelproductConfirm(C2CTransferModel theFormNew, ChannelUserVO sessionUserVO, Model model,
			BindingResult bindingResult,HttpServletRequest request) {
		final String methodName = "C2CTransferServiceImpl#channelproductConfirm";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED );
		}
		final Date curDate = new Date();
		ChannelTransferItemsVO channelTransferItemsVO = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		final C2CTransferModel c2cTransferModel = theFormNew;
		String[] argument = null;
		try {
			request.getSession().setAttribute(MODEL_KEY, c2cTransferModel);
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			final ArrayList fromArrayList = c2cTransferModel.getProductList();
			final ArrayList itemsList = new ArrayList();
			int fromArrayLists=fromArrayList.size();
			for (int i = 0, k =fromArrayLists ; i < k; i++) {
				itemsList.add(fromArrayList.get(i));
			}
			long requestedQuantity = 0;
			int itemLists=itemsList.size();
			for (int i = 0; i <itemLists ;) {
				channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
				/**
				 * if no requested quantity specified by user for any product.
				 * not consider it in order.
				 */
				if (BTSLUtil.isNullString(channelTransferItemsVO.getRequestedQuantity())) {
					itemsList.remove(i);
					continue;
				}
				if (!BTSLUtil.isNullString(channelTransferItemsVO.getRequestedQuantity())) {
					final String rqstQty = channelTransferItemsVO.getRequestedQuantity();
					argument = new String[] { channelTransferItemsVO.getShortName() };
					/* case1: to check only two decimal places value allowed */
					if (!BTSLUtil.isNumeric(rqstQty)) {
						final int length = rqstQty.length();
						final int index = rqstQty.indexOf(".");
						if (index != -1 && length > index + 3) {
							model.addAttribute(FAIL_KEY, PretupsRestUtil
									.getMessageString("pretups.channeltransfer.transferdetails.error.upto2decimal", argument));
							return false;
						}
					}
					/*
					 * case2: To check only numeric value is valid for this
					 * field
					 */
					if (!BTSLUtil.isDecimalValue(rqstQty)) {
						model.addAttribute(FAIL_KEY, PretupsRestUtil
								.getMessageString("pretups.channeltransfer.chnltochnlviewproduct.error.qtynumeric", argument));
						return false;

					} else if (!BTSLUtil.isNullString(rqstQty)) {
						long requestedQty = 0;
						try {
							requestedQty = PretupsBL.getSystemAmount(rqstQty);
							/*
							 * case3: To check requestedQty to be greater than
							 * zero
							 */
							if (requestedQty <= 0) {
								model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(
										"pretups.channeltransfer.chnltochnlviewproduct.error.qtygtzero", argument));
								return false;
							}
							/*
							 * case4: To check requestedQty to be less than user
							 * balance
							 */
							else if (requestedQty > channelTransferItemsVO.getBalance()) {
								model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(
										"pretups.channeltransfer.chnltochnlviewproduct.error.qtymorenetworkstock", argument));
								return false;

							} /*
								 * case5: To check requestedQty to be between min
								 * and max value
								 */
							else if (!(requestedQty >= channelTransferItemsVO.getMinTransferValue()
									&& requestedQty <= channelTransferItemsVO.getMaxTransferValue())) {
								model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(
										"pretups.channeltransfer.chnltochnlviewproduct.error.qtybetweenmaxmin", argument));
								return false;

							} else if ((requestedQty % channelTransferItemsVO.getTransferMultipleOf()) != 0) {
								argument = new String[] { channelTransferItemsVO.getShortName(),
										PretupsBL.getDisplayAmount(channelTransferItemsVO.getTransferMultipleOf()) };
								model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(
										"pretups.channeltransfer.chnltochnlviewproduct.error.multipleof", argument));
								return false;

							}
						} catch (BTSLBaseException e) {
							log.error(methodName, "Exception1:e=" + e);
							model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(
									"pretups.channeltransfer.chnltochnlviewproduct.error.qtynumeric", argument));
							return false;

						}
					}
				}
				
				channelTransferItemsVO.setRequiredQuantity(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
				requestedQuantity += channelTransferItemsVO.getRequiredQuantity()* Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
				i++;
			}
			 if ((c2cTransferModel.getRemarks() != null && c2cTransferModel.getRemarks().trim().length() > 100)) {
		            model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.channeltransfer.transferdetails.error.remarklength"));
		            return false;
		        } else if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS))).booleanValue()) {
		            if (BTSLUtil.isNullString(c2cTransferModel.getRemarks())) {
		                model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.channeltransfer.error.remarkrequired"));
		                return false;
		            }
		        }
			 if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SHOW_CAPTCHA))).booleanValue()) {
					final String parm = request.getParameter("j_captcha_response");
					Captcha captcha = (Captcha) request.getSession().getAttribute(Captcha.NAME);
					final String jcaptchaCode1 = captcha.getAnswer();
					if (parm != null && jcaptchaCode1 != null) {
						if (!parm.equals(jcaptchaCode1)) {
							model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.channeltransfer.captcha.error.wrongentry"));
							return false;
						}
					}

					if (parm == null || parm.isEmpty()) {
						model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.channeltransfer.captcha.error.wrongentry"));
						return false;
					}
					c2cTransferModel.setJcaptcharesponse("");
				}
			 
			if (c2cTransferModel.getOutsideHierarchyFlag() && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SEP_OUTSIDE_TXN_CTRL)).booleanValue()) {
				final String senderMessage = ChannelTransferBL.checkOutsideTransferOutCounts(con,
						c2cTransferModel.getFromUserID(), c2cTransferModel.getFromTxnProfile(), sessionUserVO.getNetworkID(), false,
						curDate, requestedQuantity);
				if (senderMessage != null) {
					final String arr[] = { c2cTransferModel.getFromUserName() };
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(senderMessage, arr));
					return false;

				}
				final String receiverMessage = ChannelTransferBL.checkOutsideTransferINCounts(con,
						c2cTransferModel.getToUserID(), c2cTransferModel.getToTxnProfile(), sessionUserVO.getNetworkID(), false, curDate,
						requestedQuantity);
				if (receiverMessage != null) {
					final String arr[] = { c2cTransferModel.getToUserName() };
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(receiverMessage, arr));
					return false;

				}
			} else {
				final String senderMessage = ChannelTransferBL.checkTransferOutCounts(con, c2cTransferModel.getFromUserID(),
						c2cTransferModel.getFromTxnProfile(), sessionUserVO.getNetworkID(), false, curDate, requestedQuantity);
				if (senderMessage != null) {
					final String arr[] = { c2cTransferModel.getFromUserName() };
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(senderMessage, arr));
					return false;
				
				}
				final String receiverMessage = ChannelTransferBL.checkTransferINCounts(con, c2cTransferModel.getToUserID(),
						c2cTransferModel.getToTxnProfile(), sessionUserVO.getNetworkID(), false, curDate, requestedQuantity);
				if (receiverMessage != null) {
					final String arr[] = { c2cTransferModel.getToUserName() };
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(receiverMessage, arr));
					return false;
					
				}
			}
			// make a new channel TransferVO to transfer into the method during
			// tax calculataion
			final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
			channelTransferVO.setChannelTransferitemsVOList(itemsList);
			channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
			channelTransferVO.setToUserID(c2cTransferModel.getToUserID());
			channelTransferVO.setOtfFlag(true);
			final UserDAO userDAO = new UserDAO();

			sessionUserVO.setUserPhoneVO(userDAO.loadUserPhoneVO(con, sessionUserVO.getUserID()));

			if (PretupsI.YES.equals(sessionUserVO.getPinRequired())) {
				if(log.isDebugEnabled()){
            		log.debug(methodName, "Before.....barredUserDAO.isExists().....");
            	}
            	BarredUserDAO barredUserDAO = new BarredUserDAO();
            	boolean isBarred = barredUserDAO.isExists(con, "C2S", sessionUserVO.getNetworkID(),sessionUserVO.getMsisdn() ,PretupsI.CHANEL_BARRED_USER_TYPE_SENDER,PretupsI.BARRED_TYPE_PIN_INVALID );
                if(isBarred){
                	model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.c2stranfer.c2srecharge.error.pin.blocked"));
                	return false;
                }
				if (BTSLUtil.isNullString(sessionUserVO.getSmsPin())) {
					log.error(methodName, "**************Pin not found in session of logged in user**************");
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.channeltransfer.error.sessiondatanotfound"));
	                return false;
					
				}

				if (BTSLUtil.isNullString(c2cTransferModel.getSmsPin())) {
					final BTSLMessages messages = new BTSLMessages("pretups.channeltransfer.chnltochnlviewproduct.msg.smspinnull", "viewproduct");
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.channeltransfer.chnltochnlviewproduct.msg.smspinnull"));
	                return false;
					
				}
				if (PretupsI.YES.equals(sessionUserVO.getUserPhoneVO().getPinReset()))
				{
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.channeltransfer.chnltochnlviewproduct.msg.smspinreset"));
	                return false;
				
				}
				try {
					ChannelUserBL.validatePIN(con, sessionUserVO, c2cTransferModel.getSmsPin());
				} catch (BTSLBaseException be) {
					try{
                		if(sessionUserVO.getUserPhoneVO().isBarUserForInvalidPin()){
                			ChannelUserBL.barSenderMSISDN(con, sessionUserVO, PretupsI.BARRED_TYPE_PIN_INVALID, new Date(), PretupsI.C2S_MODULE);
                			con.commit();
							Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
							PushMessage pushMessage = new PushMessage(sessionUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.BARRED_SUBSCRIBER_SYS_RSN), null, null, locale,sessionUserVO.getNetworkCode());
							pushMessage.push();
                		}
                	} catch (BTSLBaseException bx) {
                		log.errorTrace(methodName, bx);
                		log.error(methodName, "BTSLBaseException be:" + be.getMessage());
                	} catch (Exception e){
                		log.errorTrace(methodName, e);
                	}
					log.errorTrace(methodName, be);
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(be.getMessage()));
	                return false;
				}
			}

			ChannelTransferBL.loadAndCalculateTaxOnProducts(con, c2cTransferModel.getToCommissionProfileID(),
					c2cTransferModel.getToCommissionProfileVersion(), channelTransferVO, true, "viewproduct",
					PretupsI.TRANSFER_TYPE_C2C);

			c2cTransferModel.setProductListWithTaxes(itemsList);
			c2cTransferModel.setOtfCountsUpdated(channelTransferVO.isOtfCountsUpdated());
			c2cTransferModel.setCommLatestVersion(channelTransferVO.getCommProfileVersion());
			c2cTransferModel.setCommSetId(channelTransferVO.getCommProfileSetId());
			c2cTransferModel.setNetworkCode(channelTransferVO.getNetworkCode());
			c2cTransferModel.setTargetAchieved(channelTransferVO.isTargetAchieved());
			c2cTransferModel.setChannelTransferitemsVOList(channelTransferVO.getChannelTransferitemsVOListforOTF());

			long transferMRP = 0, totalBalance = 0, totalComm = 0, totalReqQty = 0, payableAmount = 0,
					netPayableAmount = 0, totalTax1 = 0, totalTax2 = 0, totalTax3 = 0;
			long netCommQty = 0, receiverCrQty = 0, senderDrQty = 0;
			for (int i = 0, k = itemsList.size(); i < k; i++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);

				if (PretupsI.COMM_TYPE_POSITIVE.equals(c2cTransferModel.getToUsrDualCommType())) {
					transferMRP += Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()))
							* channelTransferItemsVO.getReceiverCreditQty();
				} else {
					transferMRP += channelTransferItemsVO.getUnitValue()
							* Double.parseDouble(channelTransferItemsVO.getRequestedQuantity());
				}
				payableAmount += channelTransferItemsVO.getPayableAmount();
				netPayableAmount += channelTransferItemsVO.getNetPayableAmount();
				totalTax1 += channelTransferItemsVO.getTax1Value();
				totalTax2 += channelTransferItemsVO.getTax2Value();
				totalTax3 += channelTransferItemsVO.getTax3Value();
				totalComm += channelTransferItemsVO.getCommValue();
				totalReqQty += channelTransferItemsVO.getRequiredQuantity();
				totalBalance += channelTransferItemsVO.getBalance();
				netCommQty += channelTransferItemsVO.getCommQuantity();
				receiverCrQty += channelTransferItemsVO.getReceiverCreditQty();
				senderDrQty += channelTransferItemsVO.getSenderDebitQty();
			}

			c2cTransferModel.setRequestedQuantity(PretupsBL.getDisplayAmount(totalReqQty));
			c2cTransferModel.setTransferMRP(PretupsBL.getDisplayAmount(transferMRP));
			c2cTransferModel.setPayableAmount(PretupsBL.getDisplayAmount(payableAmount));
			c2cTransferModel.setNetPayableAmount(PretupsBL.getDisplayAmount(netPayableAmount));
			c2cTransferModel.setTotalTax1(PretupsBL.getDisplayAmount(totalTax1));
			c2cTransferModel.setTotalTax2(PretupsBL.getDisplayAmount(totalTax2));
			c2cTransferModel.setTotalTax3(PretupsBL.getDisplayAmount(totalTax3));
			c2cTransferModel.setTotalComm(PretupsBL.getDisplayAmount(totalComm));
			c2cTransferModel.setTotalBalance(PretupsBL.getDisplayAmount(totalBalance));
			// /form mali --- +ve Commision Apply
			c2cTransferModel.setNetCommision(PretupsBL.getDisplayAmount(netCommQty));
			c2cTransferModel.setReceiverCrQty(PretupsBL.getDisplayAmount(receiverCrQty));
			c2cTransferModel.setSenderDrQty(PretupsBL.getDisplayAmount(senderDrQty));

			sessionUserVO.setPinReset(sessionUserVO.getUserPhoneVO().getPinReset());

			sessionUserVO.setServiceTypes(PretupsI.SERVICE_TYPE_CHNL_TRANSFER);
			// added by harsh
			sessionUserVO.setStaffUserDetails(sessionUserVO);
			// end added by
			if (!BTSLUtil.isNullString(c2cTransferModel.getSmsPin())) {
				c2cTransferModel.setDisplayPin(BTSLUtil.getDefaultPasswordText(c2cTransferModel.getSmsPin()));
			}
			// /
			// Now load the thresholds VO to validate the user balance (after
			// subtraction of the requested quantity)
			// with the thresholds as
			// 1. first check it with the MAXIMUM PERCENTAGE ALLOWED
			// 2. if not fail at previous point then check it with the MINIMUM
			// RESEDUAL BALANCE
			// /
			final TransferProfileTxnDAO transferProfileTxnDAO = new TransferProfileTxnDAO();
			final ArrayList profileProductList = transferProfileTxnDAO.loadTrfProfileProductWithCntrlValue(con,
					c2cTransferModel.getFromTxnProfile());
			TransferProfileProductVO transferProfileProductVO = null;
			ChannelTransferItemsVO channlTrnsferItemsVO = null;
			int profilesProductLists=profileProductList.size();
			for (int i = 0, k = profilesProductLists; i < k; i++) {
				transferProfileProductVO = (TransferProfileProductVO) profileProductList.get(i);
				  int c2cTransferModelsProductLists=c2cTransferModel.getProductList().size();
				for (int m = 0, n = c2cTransferModelsProductLists; m < n; m++) {
					channlTrnsferItemsVO = (ChannelTransferItemsVO) c2cTransferModel.getProductList().get(m);
					if (transferProfileProductVO.getProductCode().equals(channlTrnsferItemsVO.getProductCode())) {
						if (transferProfileProductVO.getMinResidualBalanceAsLong() > (channlTrnsferItemsVO.getBalance()
								- channlTrnsferItemsVO.getRequiredQuantity())) {

							final String[] array = {
									BTSLUtil.roundToStr(BTSLUtil.getDisplayAmount(
											transferProfileProductVO.getMinResidualBalanceAsLong()), 2),
									channlTrnsferItemsVO.getProductName(), BTSLUtil.roundToStr(
											BTSLUtil.getDisplayAmount(channlTrnsferItemsVO.getRequiredQuantity()),2) };
							model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(
									"pretups.channeltransfer.chnltochnlviewproduct.msg.min.residualbalance", array));
							return false;

						}
						break;
					}
				} // end of for
			}
		


		}
		
		catch (BTSLBaseException e) {
			log.error(methodName, "Exception:e=" + e);
			List<KeyArgumentVO> a = e.getMessageList();
			List errors = new ArrayList<>();
			for (Object object : a) {
				KeyArgumentVO ke = (KeyArgumentVO) object;
				errors.add(PretupsRestUtil.getMessageString(ke.getKey(), ke.getArguments()));
			}
			model.addAttribute("errors_list", errors);
			return false;
		}
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(e.getMessage()));
			return false;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2CTransferServiceImpl#channelproductConfirm");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED );
			}
		}

		return true;

	}

	@SuppressWarnings("unchecked")
	public boolean approveTransferOrder(C2CTransferModel theFormNew, ChannelUserVO sessionUserVO, Model model,
			BindingResult bindingResult,HttpServletRequest request) throws BTSLBaseException {

		final String methodName = "C2CTransferServiceImpl#approveTransferOrder";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED );
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
		final C2CTransferModel theForm = (C2CTransferModel) theFormNew;
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			final Date curDate = new Date();
			final String errorDispalyPath = "approveerror";
			final ChannelUserVO userVO = sessionUserVO;
			this.constructVofromForm(sessionUserVO, theForm, channelTransferVO, curDate);
			
			  if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)).booleanValue()  ) {
				channelTransferVO.setUserLoanVOList(userVO.getUserLoanVOList());
			
			  } 
			
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()) {
				List<ChannelSoSVO> chnlSoSVOList = new ArrayList<ChannelSoSVO>();
				chnlSoSVOList.add(new ChannelSoSVO(userVO.getUserID(), userVO.getMsisdn(), userVO.getSosAllowed(),
						userVO.getSosAllowedAmount(), userVO.getSosThresholdLimit()));
				channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
			}
			channelTransferVO.setChannelTransferitemsVOList(theForm.getProductListWithTaxes());
			channelTransferVO.setChannelTransferitemsVOListforOTF(theForm.getChannelTransferitemsVOList());
			channelTransferVO.setActiveUserId(userVO.getActiveUserID());
			channelTransferVO.setTargetAcheived(theForm.isTargetAchieved());

			final int count = ChnlToChnlTransferTransactionCntrl.approveChannelToChannelTransfer(con, channelTransferVO,
					theForm.getOutsideHierarchyFlag(), true, errorDispalyPath, curDate);

			if (count > 0) {
				mcomCon.finalCommit();
				ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
				// sending sms to the receiver
				final UserDAO userDAO = new UserDAO();
				UserPhoneVO phoneVO = null;
				UserPhoneVO primaryPhoneVOS = null;
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
					if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()
							&& !((theForm.getToMSISDN()).equalsIgnoreCase(theForm.getToPrimaryMSISDN()))) {
						primaryPhoneVOS = userDAO.loadUserAnyPhoneVO(con, theForm.getToPrimaryMSISDN());
					}
					phoneVO = userDAO.loadUserAnyPhoneVO(con, theForm.getToMSISDN());
				} else {
					phoneVO = userDAO.loadUserPhoneVO(con, channelTransferVO.getToUserID());
				}
				String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
				String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
				boolean sendSMSReceiver = false;
				boolean sendSMSSender = true;

				String smsKey = PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER;
				if (PretupsI.TRANSFER_CATEGORY_TRANSFER.equals(channelTransferVO.getTransferCategory())) {
					smsKey = PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_RECEIVER_AGENT;
				}
				if (phoneVO != null) {
					country = phoneVO.getCountry();
					language = phoneVO.getPhoneLanguage();
					final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con,
							channelTransferVO, PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_TXNSUBKEY,
							PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_BALSUBKEY);
					final Locale locale = new Locale(language, country);
					final String[] array = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
							BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO.getTransferID(),
							theForm.getNetPayableAmount(), theForm.getFromMSISDN() };
					final BTSLMessages messages = new BTSLMessages(smsKey, array);
					final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages,
							channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
					pushMessage.push();
					sendSMSReceiver = true;
				}
				if (primaryPhoneVOS != null) {
					country = primaryPhoneVOS.getCountry();
					language = primaryPhoneVOS.getPhoneLanguage();
					final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con,
							channelTransferVO, PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_TXNSUBKEY,
							PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_BALSUBKEY);
					final Locale locale = new Locale(language, country);
					final String[] array = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
							BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO.getTransferID(),
							theForm.getNetPayableAmount(), theForm.getFromMSISDN() };
					final BTSLMessages messages = new BTSLMessages(smsKey, array);
					final PushMessage pushMessage = new PushMessage(primaryPhoneVOS.getMsisdn(), messages,
							channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
					pushMessage.push();
					sendSMSReceiver = true;
				}
				smsKey = PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS;
				if (PretupsI.TRANSFER_CATEGORY_TRANSFER.equals(channelTransferVO.getTransferCategory())) {
					smsKey = PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_SENDER_AGENT;
				}
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_TO_LOGIN_USER))
						.booleanValue()) {
					// modified
					phoneVO = userDAO.loadUserPhoneVO(con, channelTransferVO.getActiveUserId());
					country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
					language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
					if (phoneVO != null) {
						country = phoneVO.getCountry();
						language = phoneVO.getPhoneLanguage();
						final Object[] smsListArr = prepareSMSMessageList(theForm.getProductListWithTaxes(),
								PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_TXNSUBKEY,
								PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_BALSUBKEY);
						final Locale locale = new Locale(language, country);
						final String[] array = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
								BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]),
								channelTransferVO.getTransferID(), theForm.getNetPayableAmount(),
								theForm.getToMSISDN() };
						final BTSLMessages messages = new BTSLMessages(smsKey, array);
						final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages,
								channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
						pushMessage.push();
						sendSMSSender = true;
					} else {
						sendSMSSender = false;
					}
				}
				// added by vikram
				if (userVO.isStaffUser()) { // then send the sms to the parent
					// user
					smsKey = PretupsErrorCodesI.CHNL_TRF_SUCCESS_STAFF;
					// ChannelUserVO channelUserVO= userDAO.load
					phoneVO = userDAO.loadUserPhoneVO(con, channelTransferVO.getFromUserID());
					if (phoneVO != null) {
						country = phoneVO.getCountry();
						if (BTSLUtil.isNullString(country)) {
							country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
						}
						language = phoneVO.getPhoneLanguage();
						if (BTSLUtil.isNullString(language)) {
							language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
						}
						final Locale locale = new Locale(language, country);
						final Object[] smsListArr = prepareSMSMessageList(theForm.getProductListWithTaxes(),
								PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_TXNSUBKEY,
								PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_BALSUBKEY);
						final String[] array = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
								BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]),
								channelTransferVO.getTransferID(), theForm.getNetPayableAmount(), theForm.getToMSISDN(),
								userVO.getUserName() };
						final BTSLMessages messages = new BTSLMessages(smsKey, array);
						final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages,
								channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
						pushMessage.push();
						sendSMSSender = true;
					} else {
						sendSMSSender = false;
					}
				}
				if (sendSMSReceiver && sendSMSSender) {
					
					 model.addAttribute(SUCCESS_KEY, PretupsRestUtil.getMessageString("pretups.channeltransfer.transfer.msg.success",new String[] { channelTransferVO.getTransferID() }));
                     return true;
				} else {
					final String arr[] = { channelTransferVO.getTransferID(), channelTransferVO.getToUserName() };
					BTSLMessages messages = null;
					if (sendSMSReceiver && !sendSMSSender) {
						model.addAttribute(SUCCESS_KEY, PretupsRestUtil.getMessageString(
								"pretups.channeltransfer.transfer.msg.success.nophoneinfosender",
								new String[] { channelTransferVO.getTransferID(), channelTransferVO.getToUserName() }));
						return true;
					} else if (!sendSMSReceiver && sendSMSSender) {
						
						model.addAttribute(SUCCESS_KEY, PretupsRestUtil.getMessageString(
								"pretups.channeltransfer.transfer.msg.success.nophoneinforeceiver",
								new String[] { channelTransferVO.getTransferID(), channelTransferVO.getToUserName() }));
						return true;
					} else {
						model.addAttribute(SUCCESS_KEY, PretupsRestUtil.getMessageString(
								"pretups.channeltransfer.transfer.msg.success.nophoneinfosenderreceiver",
								new String[] { channelTransferVO.getTransferID(), channelTransferVO.getToUserName() }));
						return true;

					}

				}
			} else {
				mcomCon.finalRollback();
				model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("pretups.channeltransfer.transfer.msg.unsuccess"));
				return false;

			}
		
		} catch (BTSLBaseException e) {
			log.error(methodName, "Exception:e=" + e);
			List<KeyArgumentVO> a = e.getMessageList();
			List errors = new ArrayList<>();
			for (Object object : a) {
				KeyArgumentVO ke = (KeyArgumentVO) object;
				errors.add(PretupsRestUtil.getMessageString(ke.getKey(), ke.getArguments()));
			}
			model.addAttribute("errors_list", errors);
			return false;
		}
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			 model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
	            return false;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2CTransferServiceImpl#approveTransferOrder");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED );
			}
		}
		
	}

	
	  /**
     * Method constructVofromForm.
     * 
     * @param sessionUserVO
     *            HttpServletRequest
     * @param theForm
     *            ChannelToChannelTransferForm
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @param p_curDate
     *            Date
     * @throws BTSLBaseException
     */
    private void constructVofromForm(ChannelUserVO sessionUserVO, C2CTransferModel theForm, ChannelTransferVO p_channelTransferVO, Date p_curDate) throws BTSLBaseException {
       final String methodName="C2CTransferServiceImpl#constructVofromForm";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered TheForm: " + theForm + " ChannelTransferVO: " + p_channelTransferVO + " CurDate " + p_curDate);
        }

        final ChannelUserVO channelUserVO = sessionUserVO;
        p_channelTransferVO.setNetworkCode(channelUserVO.getNetworkID());
        p_channelTransferVO.setNetworkCodeFor(channelUserVO.getNetworkID());
        p_channelTransferVO.setGraphicalDomainCode(theForm.getFromGeoDomain());
        p_channelTransferVO.setDomainCode(channelUserVO.getDomainID());
        p_channelTransferVO.setCategoryCode(theForm.getFromCategoryCode());
        p_channelTransferVO.setSenderGradeCode(theForm.getFromGradeCode());
        p_channelTransferVO.setReceiverGradeCode(theForm.getToGradeCode());
        p_channelTransferVO.setFromUserID(channelUserVO.getActiveUserID());
        p_channelTransferVO.setFromUserName(theForm.getUserName());
        p_channelTransferVO.setToUserID(theForm.getToUserID());
        p_channelTransferVO.setToUserName(theForm.getToUserName());
        p_channelTransferVO.setTransferDate(p_curDate);
        p_channelTransferVO.setReferenceNum(theForm.getRefrenceNum());
        p_channelTransferVO.setCommProfileSetId(theForm.getToCommissionProfileID());
        p_channelTransferVO.setCommProfileVersion(theForm.getToCommissionProfileVersion());
        p_channelTransferVO.setDualCommissionType(theForm.getToUsrDualCommType());
        p_channelTransferVO.setChannelRemarks(theForm.getRemarks());
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setCreatedBy(channelUserVO.getActiveUserID());
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setModifiedBy(channelUserVO.getActiveUserID());
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
        p_channelTransferVO.setTransferInitatedBy(theForm.getFromUserID());
        p_channelTransferVO.setSenderTxnProfile(theForm.getFromTxnProfile());
        p_channelTransferVO.setReceiverTxnProfile(theForm.getToTxnProfile());
        p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);

        p_channelTransferVO.setReceiverCategoryCode(theForm.getToCategoryCode());

        p_channelTransferVO.setTransferCategory(theForm.getTransferCategory());
        p_channelTransferVO.setRequestedQuantity(PretupsBL.getSystemAmount(theForm.getRequestedQuantity()));
        p_channelTransferVO.setTransferMRP(PretupsBL.getSystemAmount(theForm.getTransferMRP()));
        p_channelTransferVO.setPayableAmount(PretupsBL.getSystemAmount(theForm.getPayableAmount()));
        p_channelTransferVO.setNetPayableAmount(PretupsBL.getSystemAmount(theForm.getNetPayableAmount()));
        p_channelTransferVO.setTotalTax1(PretupsBL.getSystemAmount(theForm.getTotalTax1()));
        p_channelTransferVO.setTotalTax2(PretupsBL.getSystemAmount(theForm.getTotalTax2()));
        p_channelTransferVO.setTotalTax3(PretupsBL.getSystemAmount(theForm.getTotalTax3()));
        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
        p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
        if (theForm.getOutsideHierarchyFlag()) {
            p_channelTransferVO.setControlTransfer(PretupsI.NO);
        } else {
            p_channelTransferVO.setControlTransfer(PretupsI.YES);
        }
        if (channelUserVO.getSessionInfoVO().getMessageGatewayVO() != null) {
            p_channelTransferVO.setRequestGatewayCode(channelUserVO.getSessionInfoVO().getMessageGatewayVO().getGatewayCode());
            p_channelTransferVO.setRequestGatewayType(channelUserVO.getSessionInfoVO().getMessageGatewayVO().getGatewayType());
        }
        // adding the some additional information for sender/reciever
        p_channelTransferVO.setReceiverGgraphicalDomainCode(theForm.getToGeoDomain());
        p_channelTransferVO.setReceiverDomainCode(theForm.getToDomainCode());
        p_channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(theForm.getToMSISDN()));
        p_channelTransferVO.setFromUserCode(PretupsBL.getFilteredMSISDN(theForm.getFromMSISDN()));
        // added by nilesh:for auto c2c
        p_channelTransferVO.setTransferProfileID(channelUserVO.getTransferProfileID());
        p_channelTransferVO.setProductCode(theForm.getProductCode());
        p_channelTransferVO.setToChannelUserStatus(theForm.getToChannelUserStatus());
        p_channelTransferVO.setFromChannelUserStatus(theForm.getFromChannelUserStatus());

        // end
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited TheForm: " + theForm + " ChannelTransferVO: " + p_channelTransferVO + " CurDate " + p_curDate);
        }
    }
    /**
     * Prepare the SMS message which we have to send the user as SMS
     * 
     * @param p_channelTransferVO
     * @param p_smsKey
     * @return ArrayList
     */
    private Object[] prepareSMSMessageList(ArrayList p_returnedProductList, String p_txnKey, String p_balKey) {
    	 final String methodName="C2CTransferServiceImpl#prepareSMSMessageList";
    	if (log.isDebugEnabled()) {
            log.debug(methodName,
                "Entered p_returnedProductList size =  : " + p_returnedProductList.size() + " p_txnKey : " + p_txnKey + " p_balKey : " + p_balKey);
        }
        final ArrayList txnSmsMessageList = new ArrayList();
        final ArrayList balSmsMessageList = new ArrayList();
        KeyArgumentVO keyArgumentVO = null;
        String argsArr[] = null;
        ChannelTransferItemsVO channelTransferItemsVO = null;

        // for handling of NullPointerException if p_returnedProductList is
        // null.
        if (p_returnedProductList == null) {
            return (new Object[] { txnSmsMessageList, balSmsMessageList });
        }

        for (int i = 0, k = p_returnedProductList.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) p_returnedProductList.get(i);
            keyArgumentVO = new KeyArgumentVO();
            argsArr = new String[2];
            argsArr[0] = String.valueOf(channelTransferItemsVO.getShortName());
            argsArr[1] = channelTransferItemsVO.getRequestedQuantity();
            keyArgumentVO.setKey(p_txnKey);
            keyArgumentVO.setArguments(argsArr);
            txnSmsMessageList.add(keyArgumentVO);

            keyArgumentVO = new KeyArgumentVO();
            argsArr = new String[2];
            argsArr[0] = String.valueOf(channelTransferItemsVO.getShortName());
            argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemsVO.getAfterTransSenderPreviousStock() - channelTransferItemsVO.getApprovedQuantity());
            keyArgumentVO.setKey(p_balKey);
            keyArgumentVO.setArguments(argsArr);
            balSmsMessageList.add(keyArgumentVO);

        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited  txnSmsMessageList.size() = " + txnSmsMessageList.size() + ", balSmsMessageList.size()" + balSmsMessageList.size());
        }
        return (new Object[] { txnSmsMessageList, balSmsMessageList });
    }


	@Override
	public void loadloggedinUserdetails(C2CTransferModel c2cTransferModel, Model model, ChannelUserVO channelUserVO) {
		c2cTransferModel.setFromUserName(channelUserVO.getUserName());
		c2cTransferModel.setFromUserID(channelUserVO.getUserID());
		if (channelUserVO.getDomainList().size() == 1) {
			final ListValueVO listValueVO = (ListValueVO) channelUserVO.getDomainList().get(0);
			c2cTransferModel.setFromGeoDomain(listValueVO.getValue());
			c2cTransferModel.setFromGeoDomainDesc(listValueVO.getLabel());
		}
		c2cTransferModel.setDomainCode(channelUserVO.getDomainID());
		c2cTransferModel.setDomainDesc(channelUserVO.getDomainName());
		c2cTransferModel.setFromCategoryCode(channelUserVO.getCategoryVO().getCategoryCode());
		c2cTransferModel.setFromCategoryDesc(channelUserVO.getCategoryVO().getCategoryName());
	}
	
	
	
}
