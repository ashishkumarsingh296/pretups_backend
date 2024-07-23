package com.btsl.pretups.channel.userreturn.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChnlToChnlTransferTransactionCntrl;
import com.btsl.pretups.channel.userreturn.web.ChnnlToChnnlReturnWithdrawModel;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;
import com.web.user.businesslogic.UserWebDAO;

import nl.captcha.Captcha;
import com.btsl.user.businesslogic.UserLoanVO;

/**
 * @author Akanksha 
 * This class implements ChnnlToChnnlReturnWithdrawService and
 *         provides methods for processing C2C withdraw Return via Channel Users
 */
@Service("ChnnlToChnnlReturnWithdrawService")
public class ChnnlToChnnlReturnWithdrawServiceImpl implements
		ChnnlToChnnlReturnWithdrawService {
	public static final Log _log = LogFactory
			.getLog(ChnnlToChnnlReturnWithdrawServiceImpl.class.getName());
	private static final String PANEL_NAME="formNumber";

	/**
	 * Loads category list for C2C withdraw by channel users
	 * 
	 * @return List The list of lookup filtered from DB
	 * @throws IOException
	 * @throws BTSLBaseException
	 * 
	 */
	private static final String SUCCESS_KEY = "success";
	private static final String FAIL_KEY = "fail";
	@SuppressWarnings("unchecked")
	@Override
	public List<ListValueVO> loadCategory(ChannelUserVO channelUserVO)
			throws IOException, BTSLBaseException {
		final String methodName="loadCategory";
		if (_log.isDebugEnabled()) {
			_log.debug("ChnnlToChnnlReturnWithdrawServiceImpl#loadCategory",
					PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		final List<ListValueVO> categoryList = new ArrayList<>();
		try {
			mcomCon = new MComConnection();
			try{con=mcomCon.getConnection();}catch(SQLException e){
				  _log.error(methodName, "SQLException " + e);
			      _log.errorTrace(methodName,e);
			}
			List<ChannelTransferRuleVO> catgList = new ChannelTransferRuleWebDAO()
					.loadTransferRulesCategoryList(con,
							channelUserVO.getNetworkID(),
							channelUserVO.getCategoryCode());

			ChannelTransferRuleVO rulesVO = null;
			// get only those categories having withdraw allowed YES or withdraw
			// by pass is allowed to YES.
			for (int i = 0, k = catgList.size(); i < k; i++) {
				rulesVO = catgList.get(i);
				if (PretupsI.YES.equals(rulesVO.getWithdrawAllowed())
						|| PretupsI.YES.equals(rulesVO
								.getWithdrawChnlBypassAllowed())) {
					// attached the domain code for the display purpose
					categoryList.add(new ListValueVO(rulesVO.getToCategoryDes()
							+ " (" + rulesVO.getToDomainCode() + ")", rulesVO
							.getToCategory()));
				}
			}
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChnnlToChnnlReturnWithdrawServiceImpl#loadCategory");
				mcomCon = null;
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("ChnnlToChnnlReturnWithdrawServiceImpl#loadCategory", PretupsI.EXITED);
		}
		return categoryList;

	}
	
	
	
	/**
	 * The loadUserList method loads users list for C2C withdraw by channel users
	 * 
	 * @return List The list of lookup filtered from DB
	 * @throws IOException
	 * @throws BTSLBaseException
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ListValueVO> loadUserList(ChannelUserVO channelUserVO,String toCategoryCode,String userName)
			throws IOException, BTSLBaseException {
	    final String methodName="loadUserList";
		if (_log.isDebugEnabled()) {
			_log.debug("ChnnlToChnnlReturnWithdrawServiceImpl#loadUserList",
					PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		 ChannelTransferRuleDAO channelTransferRuleDAO = null;
		List<ListValueVO> list = null;
		try{
			mcomCon = new MComConnection();
			try{con=mcomCon.getConnection();}catch(SQLException e){
				_log.error(methodName, "SQLException " + e);
			    _log.errorTrace(methodName,e);
			}
			channelTransferRuleDAO = new ChannelTransferRuleDAO();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
         final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, channelUserVO.getNetworkID(), channelUserVO.getDomainID(), channelUserVO.getCategoryCode()
           , toCategoryCode, PretupsI.TRANSFER_RULE_TYPE_CHANNEL, false);
         // user search for parent category
         
         
         if (!channelUserVO.isReturnFlag()) {
             list = ChannelUserBL.loadChannelUserForWithdrawWithXfrRule(con, channelTransferRuleVO, toCategoryCode, userName, channelUserVO.getUserID(),
                 channelUserVO);
         } else {
             list = ChannelUserBL.loadChannelUserForReturnWithXfrRule(con, channelTransferRuleVO, toCategoryCode, userName, channelUserVO.getUserID(),
                 channelUserVO);
         }
		
			
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChnnlToChnnlReturnWithdrawServiceImpl#loadUserList");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug("ChnnlToChnnlReturnWithdrawServiceImpl#loadUserList", PretupsI.EXITED);
			}
		}
		return list;

	}
	
	
	/**
	 * The showProductDetails method loads users product list for C2C withdraw by channel users
	 * 
	 * @return List The list of lookup filtered from DB
	 * @throws IOException
	 * @throws BTSLBaseException
	 * 
	 */
	@Override
	public Boolean showProductDetails(ChannelUserVO sessionUser,ChnnlToChnnlReturnWithdrawModel withdrawReturnModel, BindingResult bindingResult, Locale locale ,Model model,HttpServletRequest request) throws  IOException{
        
        final String methodName = "showProductDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        Connection con = null;MComConnectionI mcomCon = null;
        ChnnlToChnnlReturnWithdrawModel theForm = withdrawReturnModel;
        boolean valueSwaped = false;
        try {
            theForm.flushProductDetail();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            ChannelTransferRuleVO channelTransferRuleVO = null;
            ChannelTransferRuleDAO channelTransferRuleDAO = null;
            String trfRuleID = null;
            if(request.getParameter("submitMsisdn")!=null){
   			 CommonValidator commonValidator=new CommonValidator("configfiles/userreturn/validator-userC2CWithdrawReturn.xml", theForm, "ChnnlToChnnlReturnWithdrawModelMsisdn");
            	Map<String, String> errorMessages = commonValidator.validateModel();
            	PretupsRestUtil pru=new PretupsRestUtil();
            	pru.processFieldError(errorMessages, bindingResult);
            	model.addAttribute(PANEL_NAME, "Panel-One");
            	request.getSession().setAttribute(PANEL_NAME, "Panel-One");
   		 }
   		 if(request.getParameter("submitUsrSearch")!=null){
   			 CommonValidator commonValidator=new CommonValidator("configfiles/userreturn/validator-userC2CWithdrawReturn.xml", theForm, "ChnnlToChnnlReturnWithdrawModelSearch");
            	Map<String, String> errorMessages = commonValidator.validateModel();
            	PretupsRestUtil pru=new PretupsRestUtil();
            	pru.processFieldError(errorMessages, bindingResult); 
            	model.addAttribute(PANEL_NAME, "Panel-Two");
            	request.getSession().setAttribute(PANEL_NAME, "Panel-Two");
   		 }
   		 if(bindingResult.hasFieldErrors()){
   			    
         		return false;
         	 }
            // validate the user name if user has change the name after the
            // search or type the name directly.
            if (BTSLUtil.isNullString(theForm.getUserCode())) {
            	channelTransferRuleDAO = new ChannelTransferRuleDAO();
                channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, sessionUser.getNetworkID(), sessionUser.getDomainID(), sessionUser.getCategoryCode(),
                    theForm.getToCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_CHANNEL, true);
                theForm.setToCategoryDesc(BTSLUtil.getOptionDesc(theForm.getToCategoryCode(), theForm.getCategoryList()).getLabel());
                if (channelTransferRuleVO == null) {
                    model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.message.channel.withdraw.transferrulenotdefine"));
                   return false;
                }
                trfRuleID = channelTransferRuleVO.getTransferRuleID();
                ListValueVO listValueVO = null;
                String userNameAndID=theForm.getToUserName();
                
                String[] userDet=userNameAndID.split("\\(");
               
                String userName = userDet[0];
                theForm.setToUserName(userName);
                try{
                	String userID=userDet[1];
                	String ID=userID.replaceAll("\\)", "");
                	theForm.setUserID(ID);
                }catch(Exception e){
                	 _log.error(methodName, "Name selected did not had ID " + e);
                	 _log.errorTrace(methodName, e);
                }
                ArrayList userList = null;
                if (!theForm.getReturnFlag()) {
                    userList = ChannelUserBL.loadChannelUserForWithdrawWithXfrRule(con, channelTransferRuleVO, theForm.getToCategoryCode(), userName, theForm.getFromUserID(),
                        sessionUser);
                } else {
                    userList = ChannelUserBL.loadChannelUserForReturnWithXfrRule(con, channelTransferRuleVO, theForm.getToCategoryCode(), userName, theForm.getFromUserID(),
                        sessionUser);
                }
                if (userList.size() == 1) {
                    listValueVO = (ListValueVO) userList.get(0);
                    theForm.setUserID(listValueVO.getValue());
                    theForm.setToUserName(listValueVO.getLabel());
                    theForm.setToUserID(listValueVO.getValue());
                } else if (userList.size() > 1) {
                    boolean isExist = false;
                    if (!BTSLUtil.isNullString(theForm.getUserID())) {
                        for (int i = 0, k = userList.size(); i < k; i++) {
                            listValueVO = (ListValueVO) userList.get(i);
                            if (listValueVO.getValue().equals(theForm.getUserID()) && theForm.getToUserName().compareTo(listValueVO.getLabel()) == 0) {
                                theForm.setToUserName(listValueVO.getLabel());
                                theForm.setToUserID(listValueVO.getValue());
                                isExist = true;
                                break;
                            }
                        }

                    } else {
                        ListValueVO listValueVONext = null;
                        for (int i = 0, k = userList.size(); i < k; i++) {
                            listValueVO = (ListValueVO) userList.get(i);
                            if (theForm.getToUserName().compareTo(listValueVO.getLabel()) == 0) {
                                if ((i + 1) < k) {
                                    listValueVONext = (ListValueVO) userList.get(i + 1);
                                    if (theForm.getToUserName().compareTo(listValueVONext.getLabel()) == 0) {
                                        isExist = false;
                                        break;
                                    }
                                    theForm.setUserID(listValueVO.getValue());
                                    theForm.setToUserName(listValueVO.getLabel());
                                    theForm.setToUserID(listValueVO.getValue());
                                    isExist = true;
                                    break;
                                }
                                theForm.setUserID(listValueVO.getValue());
                                theForm.setToUserName(listValueVO.getLabel());
                                theForm.setToUserID(listValueVO.getValue());
                                isExist = true;
                                break;
                            }
                        }
                    }

                    if (!isExist) {
                        model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.channel.withdraw.chnltochnlsearchuser.usermorethanoneexist.msg"));
                        return false;
                    }
                } else {
                    model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.channel.withdraw.chnltochnlsearchuser.usernotfound.msg"));
                    return false;
                }
            }// search user validation ends here.
            theForm.setDomainName(sessionUser.getDomainName());
            theForm.setUserName(sessionUser.getUserName());
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            final Date curDate = new Date();
            final ChannelUserVO toChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, sessionUser.getUserID(), false, curDate,false);

            ChannelUserVO fromChannelUserVO = null;
            String argument = null;

            // finding that user entered user mobile no. or searched user name
            final UserDAO userDAO = new UserDAO();
            UserPhoneVO phoneVO = null;
            String[] args = null;
            if (!BTSLUtil.isNullString(theForm.getUserCode())) {
                theForm.setUserCode(PretupsBL.getFilteredMSISDN(theForm.getUserCode()));
            }

            // user life cycle
            String userStatusAllowed1 = null;
            boolean statusAllowed = false;
            final UserStatusVO userStatusVO1 = (UserStatusVO) UserStatusCache.getObject(toChannelUserVO.getNetworkID(), toChannelUserVO.getCategoryCode(),
                PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
            if (userStatusVO1 != null) {
                if (!theForm.getIsReturnFlag()) {
                    userStatusAllowed1 = userStatusVO1.getUserReceiverAllowed();
                } else {
                    userStatusAllowed1 = userStatusVO1.getUserSenderAllowed();
                }
                final String[] status = userStatusAllowed1.split(",");
                for (int k = 0; k < status.length; k++) {
                    if (status[k].equals(toChannelUserVO.getStatus())) {
                        statusAllowed = true;
                    }
                }
            } else {
                args = new String[] { toChannelUserVO.getMsisdn() };
                model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.message.channel.withdraw.usernotallowed.msg",args));
                return false;

            }

            if (!statusAllowed) {
                args = new String[] { toChannelUserVO.getMsisdn() };
                model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.message.channel.withdraw.usersuspended.msg",args));
                return false;
            }

            // end user life cycle
            if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
                if (BTSLUtil.isNullString(theForm.getUserCode())) {
                    fromChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, theForm.getToUserID(), false, curDate,false);
                    argument = theForm.getToUserName();
                } else {
                    fromChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, theForm.getUserCode(), true, curDate,false);
                    argument = theForm.getUserCode();
                }
            } else {
                if (!BTSLUtil.isNullString(theForm.getUserCode())) {
                    phoneVO = userDAO.loadUserAnyPhoneVO(con, theForm.getUserCode());
                    if (phoneVO == null) {
                        args = new String[] { theForm.getUserCode() };
                        model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.message.channel.withdraw.userdetailnotfound.msg",args));
                        return false;
                    }
                    argument = theForm.getUserCode();
                    theForm.setToUserID(phoneVO.getUserId());
                } else {
                    argument = theForm.getToUserName();
                }
                fromChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, theForm.getToUserID(), false, curDate,false);
                if (fromChannelUserVO == null) {
                    args = new String[] { theForm.getUserCode() };
                    model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.message.channel.withdraw.userdetailnotfound.msg",args));
                    return false;
                }

                if (phoneVO != null && !("Y").equalsIgnoreCase(phoneVO.getPrimaryNumber())) {
                    fromChannelUserVO.setPrimaryMsisdn(fromChannelUserVO.getMsisdn());
                    fromChannelUserVO.setMsisdn(phoneVO.getMsisdn());
                } else {
                    fromChannelUserVO.setPrimaryMsisdn(fromChannelUserVO.getMsisdn());
                }
                theForm.setFromPrimaryMSISDN(fromChannelUserVO.getPrimaryMsisdn());
            }
            if(fromChannelUserVO==null){
            	args = new String[] { theForm.getUserCode() };
            	model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.message.channel.withdraw.userdetailnotfound.msg",args));
                return false;
            }
            if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
            	theForm.setFromUserSosAllowed(fromChannelUserVO.getSosAllowed());
            	theForm.setFromUserSosAllowedAmount(fromChannelUserVO.getSosAllowedAmount());
            	theForm.setFromUserSosThresholdLimit(fromChannelUserVO.getSosThresholdLimit());
            }

                // user life cycle
                statusAllowed = false;
                String userStatusAllowed = null;
                final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(fromChannelUserVO.getNetworkID(), fromChannelUserVO.getCategoryCode(),
                    fromChannelUserVO.getUserType(), PretupsI.REQUEST_SOURCE_TYPE_WEB);
                if (userStatusVO != null) {
                    if (!theForm.getIsReturnFlag()) {
                        userStatusAllowed = userStatusVO.getUserSenderAllowed();
                    } else {
                        userStatusAllowed = userStatusVO.getUserReceiverAllowed();
                    }
                    final String[] status = userStatusAllowed.split(",");
                    for (int i = 0; i < status.length; i++) {
                        if (status[i].equals(fromChannelUserVO.getStatus())) {
                            statusAllowed = true;
                        }
                    }
                } else {
                    args = new String[] { theForm.getFromUserName() };
                    model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.message.channel.withdraw.usernotallowed.msg",args));
                    return false;
                }
            
            if (!statusAllowed) {
                args = new String[] { argument };
                model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.message.channel.withdraw.usersuspended.msg",args));
                return false;
            } else if (fromChannelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
                args = new String[] { argument };
                model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.message.channel.withdraw.usernocommprofileapplicable.msg",args));
                return false;
            }

            /*
             * Add the requested commission profile message in the
             * fromChannelUserVO
             */
            fromChannelUserVO.setCommissionProfileSuspendMsg(fromChannelUserVO.getCommissionProfileLang2Msg());
            toChannelUserVO.setCommissionProfileSuspendMsg(toChannelUserVO.getCommissionProfileLang2Msg());
            final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
            if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                fromChannelUserVO.setCommissionProfileSuspendMsg(fromChannelUserVO.getCommissionProfileLang1Msg());
                toChannelUserVO.setCommissionProfileSuspendMsg(toChannelUserVO.getCommissionProfileLang1Msg());
            }
            boolean isOutsideHirearchy = false;
            boolean isUserCodeFlag = false;
            if (!BTSLUtil.isNullString(theForm.getUserCode())) {
                isUserCodeFlag = true;
            }

           ArrayList<ChannelTransferItemsVO> productList = null;
            if (!theForm.getReturnFlag()) {
                isOutsideHirearchy = ChannelTransferBL.validateSenderAndReceiverWithXfrRule(con, fromChannelUserVO, toChannelUserVO, isUserCodeFlag, "usersearch", true,
                    PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
                trfRuleID = toChannelUserVO.getTransferRuleID();
                this.constructFormFromVOs(theForm, fromChannelUserVO, toChannelUserVO);
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
                    toChannelUserVO.setPrimaryMsisdn(theForm.getFromPrimaryMSISDN());
                }
                valueSwaped = true;
                productList = ChannelTransferBL.loadC2CXfrProductsWithXfrRule(con, fromChannelUserVO.getUserID(), sessionUser.getNetworkID(), theForm
                    .getFromCommissionProfileID(), curDate, trfRuleID, "usersearch", true, argument, locale, null, PretupsI.TRANSFER_TYPE_C2C);
            } else if (!theForm.getIsReturnToParentFlag()) {
                if (PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(fromChannelUserVO.getInSuspend())) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "USER IS SUSPENDED IN THE SYSTEM");
                    }
                    model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.message.channel.withdraw.return.errormsg.userinsuspend",new String[] { fromChannelUserVO.getUserName() }));
                    return false;
                }
                isOutsideHirearchy = ChannelTransferBL.validateSenderAndReceiverWithXfrRule(con, toChannelUserVO, fromChannelUserVO, isUserCodeFlag, "usersearch", true,
                    PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);
                trfRuleID = toChannelUserVO.getTransferRuleID();
                this.constructFormFromVOs(theForm, toChannelUserVO, fromChannelUserVO);
                productList = ChannelTransferBL.loadC2CXfrProductsWithXfrRule(con, toChannelUserVO.getUserID(), sessionUser.getNetworkID(), toChannelUserVO
                    .getCommissionProfileSetID(), curDate, trfRuleID, "usersearch", true, toChannelUserVO.getUserName(), locale, null, PretupsI.TRANSFER_TYPE_C2C);
            }
            theForm.setCurrentDate(BTSLUtil.getDateStringFromDate(curDate));
            theForm.setOutsideHierarchyFlag(isOutsideHirearchy);
            theForm.setTransferCategory(fromChannelUserVO.getTransferCategory());
            theForm.setProductList(productList);
            if (!BTSLUtil.isNullString(sessionUser.getActiveUserMsisdn()) && !PretupsI.NOT_AVAILABLE.equals(sessionUser.getActiveUserMsisdn()) && !sessionUser.getMsisdn()
                .equals(sessionUser.getActiveUserMsisdn())) {
                theForm.setDisplayMsisdn(sessionUser.getActiveUserMsisdn());
            } else {
                theForm.setDisplayMsisdn(sessionUser.getMsisdn());
            }

            theForm.setToChannelUserStatus(toChannelUserVO.getStatus());
            theForm.setFromChannelUserStatus(fromChannelUserVO.getStatus());
        }catch(BTSLBaseException be){
        	  _log.error(methodName, "Exception:e =" + be);
              _log.errorTrace(methodName, be);
              if (valueSwaped) {
                  this.swapUserInfo(theForm);
              }
              model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(be.getMessageKey(),be.getArgs()));
              return false;
        } 
        catch (Exception e) {
            _log.error(methodName, "Exception:e =" + e);
            _log.errorTrace(methodName, e);
            if (valueSwaped) {
                this.swapUserInfo(theForm);
            }
            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
            return false;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("ChnnlToChnnlReturnWithdrawServiceImpl#showProductDetails");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
            }
        }
        return true;
    }
	
	
	
	 /**
     * Method swapUserInfo.
     * This method is to swap the user information so that the proper data can
     * be viewd on the jsp.
     * This method will mainly called in the c2c withdraw operation since in the
     * withdraw we swap the from/to user
     * infromation as per the business rules.
     * 
     * 
     * @throws BTSLBaseException
     */
    private void swapUserInfo(ChnnlToChnnlReturnWithdrawModel form) {

        if (_log.isDebugEnabled()) {
            _log.debug("swapUserInfo", "Entered ");
        }
        final ChnnlToChnnlReturnWithdrawModel theForm =form;

        String temp = theForm.getFromCategoryCode();
        theForm.setFromCategoryCode(theForm.getToCategoryCode());
        theForm.setToCategoryCode(temp);

        temp = theForm.getFromUserID();
        theForm.setFromUserID(theForm.getToUserID());
        theForm.setToUserID(temp);

        temp = theForm.getFromUserName();
        theForm.setFromUserName(theForm.getToUserName());
        theForm.setToUserName(temp);

        if (_log.isDebugEnabled()) {
            _log.debug("swapUserInfo", "Exit ");
        }

    }
    
    
    /**
     * Method constructFormFromVOs.
     * 
     * @param form
     *            ActionForm
     * @param fromChannelUserVO
     *            ChannelUserVO
     * @param toChannelUserVO
     *            ChannelUserVO
     * @throws BTSLBaseException
     */
    private void constructFormFromVOs(ChnnlToChnnlReturnWithdrawModel form, ChannelUserVO fromChannelUserVO, ChannelUserVO toChannelUserVO) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("constructFormFromVOs", "Entered fromChannelUserVO=> " + fromChannelUserVO + " toChannelUserVO=> " + toChannelUserVO);
        }

        final ChnnlToChnnlReturnWithdrawModel theForm = form;
        theForm.setFromUserName(fromChannelUserVO.getUserName());
        theForm.setFromUserID(fromChannelUserVO.getUserID());
        theForm.setFromMSISDN(fromChannelUserVO.getMsisdn());
        theForm.setFromGradeCode(fromChannelUserVO.getUserGrade());
        theForm.setFromGradeCodeDesc(fromChannelUserVO.getUserGradeName());
        theForm.setFromCommissionProfileID(fromChannelUserVO.getCommissionProfileSetID());
        theForm.setFromCommissionProfileIDDesc(fromChannelUserVO.getCommissionProfileSetName());
        theForm.setFromTxnProfile(fromChannelUserVO.getTransferProfileID());
        theForm.setFromTxnProfileDesc(fromChannelUserVO.getTransferProfileName());
        theForm.setFromCategoryCode(fromChannelUserVO.getCategoryVO().getCategoryCode());
        theForm.setFromCategoryDesc(fromChannelUserVO.getCategoryVO().getCategoryName());
        theForm.setFromCommissionProfileVersion(fromChannelUserVO.getCommissionProfileSetVersion());
        theForm.setFromUsrDualCommType(fromChannelUserVO.getDualCommissionType());
        theForm.setFromGeoDomain(fromChannelUserVO.getGeographicalCode());
        if (BTSLUtil.isNullString(theForm.getUserCode())) {
        theForm.setNameAndId(fromChannelUserVO.getUserName()+"("+fromChannelUserVO.getUserID()+")");
        }else
        {
        theForm.setNameAndId("");
        }
        theForm.setToUserID(toChannelUserVO.getUserID());
        theForm.setToUserName(toChannelUserVO.getUserName());
        theForm.setToMSISDN(toChannelUserVO.getMsisdn());
        theForm.setToGradeCode(toChannelUserVO.getUserGrade());
        theForm.setToGradeCodeDesc(toChannelUserVO.getUserGradeName());
        theForm.setToCommissionProfileID(toChannelUserVO.getCommissionProfileSetID());
        theForm.setToCommissionProfileIDDesc(toChannelUserVO.getCommissionProfileSetName());
        theForm.setToTxnProfile(toChannelUserVO.getTransferProfileID());
        theForm.setToTxnProfileDesc(toChannelUserVO.getTransferProfileName());
        theForm.setToCategoryCode(toChannelUserVO.getCategoryCode());
        theForm.setToCategoryDesc(toChannelUserVO.getCategoryVO().getCategoryName());
        theForm.setToCommissionProfileVersion(toChannelUserVO.getCommissionProfileSetVersion());
        theForm.setToUsrDualCommType(toChannelUserVO.getDualCommissionType());
        theForm.setToGeoDomain(toChannelUserVO.getGeographicalCode());
        theForm.setDomainCode(fromChannelUserVO.getDomainID());
        theForm.setToDomainCode(toChannelUserVO.getDomainID());
        if (_log.isDebugEnabled()) {
            _log.debug("constructFormFromVOs", "Exit ");
        }

    }
    
    /**
     * This method loads the list of the products for withdrawal.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     */
    @Override
	public Boolean confirmWithdrawUserProducts(ChannelUserVO sessionUser,ChnnlToChnnlReturnWithdrawModel withdrawReturnModel, BindingResult bindingResult, Locale locale ,Model model,HttpServletRequest request) throws  IOException{
		 final String methodName = "confirmWithdrawUserProducts";
		if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
     // create the transferItems which are selected by user.
        ChannelTransferItemsVO channelTransferItemsVO = null;
        final ArrayList<ChannelTransferItemsVO> itemsList = new ArrayList<>();
        Connection con=null;
        MComConnectionI mcomCon = null;
        ChannelTransferItemsVO channelTransferItemsOldVO = null;
        String[] args=null;
        boolean anyProductQuantiyAvailable=false;
        try{
        ChnnlToChnnlReturnWithdrawModel theForm=withdrawReturnModel;
        for (int i = 0, k = withdrawReturnModel.getProductList().size(); i < k; i++) {
            channelTransferItemsOldVO = theForm.getProductList().get(i);
            /**
             * if no requested quantity specified by user for any product.
             * not consider it in return.
             */
            if (BTSLUtil.isNullString(channelTransferItemsOldVO.getRequestedQuantity())) {
                continue;
            }
            anyProductQuantiyAvailable=true;
            args = new String[] { channelTransferItemsOldVO.getShortName() };
            if (!BTSLUtil.isDecimalValue(channelTransferItemsOldVO.getRequestedQuantity())) {
            	model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.userreturn.withdrawreturn.error.qtynumeric",args));
                return false;
            }
            long requestedQty = 0;
                requestedQty = PretupsBL.getSystemAmount(channelTransferItemsOldVO.getRequestedQuantity());
                if (requestedQty <= 0) {
                	model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.userreturn.withdrawreturn.error.qtygtzero",args));
                    return false;
                }else if (!BTSLUtil.isNumeric(channelTransferItemsOldVO.getRequestedQuantity())) {
                    final int length = channelTransferItemsOldVO.getRequestedQuantity().length();
                    final int index = channelTransferItemsOldVO.getRequestedQuantity().indexOf(".");
                    if (index != -1 && length > index + 3) {
                        model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.userreturn.channeltochannelwithdraw.error.upto2decimal",args));
                        return false;
                    }
                }
                
                else if (requestedQty > channelTransferItemsOldVO.getBalance()) {
                	model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.userreturn.withdrawreturn.error.qtymorenbalance",args));
                    return false;
                }
            channelTransferItemsVO = new ChannelTransferItemsVO();
            this.populateTransferItemsVO(channelTransferItemsVO, channelTransferItemsOldVO, theForm);
            itemsList.add(channelTransferItemsVO);
        }
        if(!anyProductQuantiyAvailable){
				model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.channel.withdraw.return.transferdetails.error.noproductselect"));
				return false;
        }
        
        if (theForm.getRemarks() != null && theForm.getRemarks().trim().length() > 100) {
            model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.userreturn.withdrawreturn.error.remarklength",args));
            return false;
        } else if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS))).booleanValue()) {
            if (BTSLUtil.isNullString(theForm.getRemarks())) {
                model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.userreturn.withdrawreturn.error.remarkrequired",args));
                return false;
            }
        }
        mcomCon = new MComConnection();
        con=mcomCon.getConnection();
        // make a new channel TransferVO to transfer into the method during
        // tax calculataion
        final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
        channelTransferVO.setChannelTransferitemsVOList(itemsList);
        if (theForm.getReturnFlag()) {
            channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);
        } else {
            channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
        }
        channelTransferVO.setOtfFlag(false);

        ChannelTransferBL.loadAndCalculateTaxOnProducts(con, theForm.getFromCommissionProfileID(), theForm.getFromCommissionProfileVersion(), channelTransferVO, true,
            "viewproduct", PretupsI.TRANSFER_TYPE_C2C);
        // set the itemsList on the Form with calculated Taxes and
        // commsssion value
        theForm.setProductListWithTaxes(itemsList);

        long requestedQuantity = 0, totalStock = 0, totalComm = 0, transferMRP = 0, payableAmount = 0, netPayableAmount = 0, totalTax1 = 0, totalTax2 = 0, totalTax3 = 0;
        long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
        for (int i = 0, k = itemsList.size(); i < k; i++) {
            channelTransferItemsVO = itemsList.get(i);

            requestedQuantity += channelTransferItemsVO.getRequiredQuantity();
            if (PretupsI.COMM_TYPE_POSITIVE.equals(theForm.getToUsrDualCommType())) {
                transferMRP += (channelTransferItemsVO.getReceiverCreditQty()) * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
            } else {
                transferMRP += channelTransferItemsVO.getUnitValue() * Double.parseDouble(channelTransferItemsVO.getRequestedQuantity());
            }
            payableAmount += channelTransferItemsVO.getPayableAmount();
            netPayableAmount += channelTransferItemsVO.getNetPayableAmount();
            totalTax1 += channelTransferItemsVO.getTax1Value();
            totalTax2 += channelTransferItemsVO.getTax2Value();
            totalTax3 += channelTransferItemsVO.getTax3Value();
            totalComm += channelTransferItemsVO.getCommValue();
            totalStock += channelTransferItemsVO.getBalance();
            commissionQty += channelTransferItemsVO.getCommQuantity();
            senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
            receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
            channelTransferItemsVO.setProductMrpStr(PretupsBL.getDisplayAmount(channelTransferItemsVO.getProductTotalMRP()));
        }

        theForm.setTotalReqQty(PretupsBL.getDisplayAmount(requestedQuantity));
        theForm.setTransferMRP(PretupsBL.getDisplayAmount(transferMRP));
        theForm.setPayableAmount(PretupsBL.getDisplayAmount(payableAmount));
        theForm.setNetPayableAmount(PretupsBL.getDisplayAmount(netPayableAmount));
        theForm.setTotalTax1(PretupsBL.getDisplayAmount(totalTax1));
        theForm.setTotalTax2(PretupsBL.getDisplayAmount(totalTax2));
        theForm.setTotalTax3(PretupsBL.getDisplayAmount(totalTax3));
        theForm.setTotalComm(PretupsBL.getDisplayAmount(totalComm));
        theForm.setTotalStock(PretupsBL.getDisplayAmount(totalStock));

        theForm.setSenderDebitQty(PretupsBL.getDisplayAmount(senderDebitQty));
        theForm.setReceiverCreditQty(PretupsBL.getDisplayAmount(receiverCreditQty));
        theForm.setNetCommQty(PretupsBL.getDisplayAmount(commissionQty));

        final ChannelUserVO sessionUserVO = sessionUser;
      
        final UserDAO userDAO = new UserDAO();
        sessionUserVO.setUserPhoneVO(userDAO.loadUserPhoneVO(con, sessionUserVO.getUserID()));

        sessionUserVO.setPinReset(sessionUserVO.getUserPhoneVO().getPinReset());

        if (theForm.getReturnFlag()) {
            sessionUserVO.setServiceTypes(PretupsI.SERVICE_TYPE_CHNL_RETURN);
        } else {
            sessionUserVO.setServiceTypes(PretupsI.SERVICE_TYPE_CHNL_WITHDRAW);
        }
        
     // For CAPTCHA
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SHOW_CAPTCHA))).booleanValue()) {
            final String parm = request.getParameter("j_captcha_response");
            Captcha captcha =  (Captcha) request.getSession().getAttribute(Captcha.NAME);
			final String jcaptchaCode1 = captcha.getAnswer();
            if (parm != null && jcaptchaCode1 != null) {
                if (!parm.equals(jcaptchaCode1)) {
                	 model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("captcha.error.wrongentry"));
                     return false;
                     }
            }

            if (parm == null || parm.isEmpty()) {
            	model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("captcha.error.wrongentry"));
                return false;
            }
            withdrawReturnModel.setJ_captcha_response("");
        }
        
        if (!BTSLUtil.isNullString(theForm.getSmsPin())) {
            theForm.setDisplayPin(BTSLUtil.getDefaultPasswordText(theForm.getSmsPin()));
        }
        if (PretupsI.YES.equals(sessionUserVO.getPinRequired())) {
            if (BTSLUtil.isNullString(sessionUserVO.getSmsPin())) {
                _log.error(methodName, "**************Pin not found in session of logged in user**************");
                model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.userreturn.withdrawreturn.error.sessiondatanotfound"));
                return false;
            }

            if (BTSLUtil.isNullString(theForm.getSmsPin())) {
                model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.userreturn.withdrawreturn.error.smspinnull"));
                return false;
            }
            try {
                ChannelUserBL.validatePIN(con, sessionUserVO, theForm.getSmsPin());
            } catch (BTSLBaseException be) {
            	_log.errorTrace(methodName, be);
                model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(be.getMessage(), be.getArgs()));
               return false;
            }
        }
       }
        catch (BTSLBaseException e) {
            _log.error(methodName, "Exception:e=" + e);
            List<KeyArgumentVO> a=e.getMessageList();
            List errors=new ArrayList<>();
            
            for (Object object : a) {
            	KeyArgumentVO ke=(KeyArgumentVO)object;
            	errors.add(PretupsRestUtil.getMessageString(ke.getKey(), ke.getArguments()));
			}
            model.addAttribute("errors_list",errors);
            
            return false;
        }  
       catch (Exception e) {
            _log.error(methodName, "Exception:e=" + e);
            
            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
            return false;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("ChnnlToChnnlReturnWithdrawServiceImpl#confirmWithdrawUserProducts");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
            }
        }
        return true;
        }
       
		
	 /**
     * 
     * @param pTransferItemsVO
     * @param pChannelTransferItemOldVO
     * @param pTheForm
     * @throws BTSLBaseException
     */
    private void populateTransferItemsVO(ChannelTransferItemsVO pTransferItemsVO, ChannelTransferItemsVO pChannelTransferItemOldVO, ChnnlToChnnlReturnWithdrawModel pTheForm) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("populateTransferItemsVO",
                "Entered p_transferItemsVO " + pTransferItemsVO + " UserBalancesVO " + pChannelTransferItemOldVO + " ChannelReturnForm " + pTheForm);
        }

        pTransferItemsVO.setProductType(pChannelTransferItemOldVO.getProductType());
        pTransferItemsVO.setProductCode(pChannelTransferItemOldVO.getProductCode());
        pTransferItemsVO.setShortName(pChannelTransferItemOldVO.getShortName());
        pTransferItemsVO.setProductName(pChannelTransferItemOldVO.getShortName());
        if (pTheForm.getReturnFlag()) {
            pTransferItemsVO.setCommProfileDetailID(pTheForm.getFromCommissionProfileID());
        } else {
            pTransferItemsVO.setCommProfileDetailID(pTheForm.getToCommissionProfileID());
        }

        pTransferItemsVO.setRequestedQuantity(pChannelTransferItemOldVO.getRequestedQuantity());
        if (pChannelTransferItemOldVO.getRequestedQuantity() != null) {
            pTransferItemsVO.setRequiredQuantity(PretupsBL.getSystemAmount(pChannelTransferItemOldVO.getRequestedQuantity()));
        }
        pTransferItemsVO.setUnitValue(pChannelTransferItemOldVO.getUnitValue());
        pTransferItemsVO.setBalance(pChannelTransferItemOldVO.getBalance());
        pTransferItemsVO.setProductShortCode(pChannelTransferItemOldVO.getProductShortCode());
        pTransferItemsVO.setTaxOnChannelTransfer(pChannelTransferItemOldVO.getTaxOnChannelTransfer());
        pTransferItemsVO.setTaxOnFOCTransfer(pChannelTransferItemOldVO.getTaxOnFOCTransfer());
        if (_log.isDebugEnabled()) {
            _log.debug("populateTransferItemsVO",
                "Exiting p_transferItemsVO " + pTransferItemsVO + " UserBalancesVO " + pChannelTransferItemOldVO + " ChannelReturnForm " + pTheForm);
        }
    }

    /**
     * The approveWithdrawReturn updates database with transaction details for C2C withdraw via channel users
     * @param pTransferItemsVO
     * @param pChannelTransferItemOldVO
     * @param pTheForm
     * @throws BTSLBaseException
     */
    @Override
    public Boolean approveWithdrawReturn(ChannelUserVO sessionUser,ChnnlToChnnlReturnWithdrawModel withdrawReturnModel, BindingResult bindingResult, Locale locale ,Model model) throws  IOException{
        if (_log.isDebugEnabled()) {
            _log.debug("approveWithdrawReturn", "Entered");
        }
        
        final String methodName = "approveWithdrawReturn";
        Connection con = null;MComConnectionI mcomCon = null;
        List<ChannelSoSVO> chnlSoSVOList = new ArrayList<>();
        final ChnnlToChnnlReturnWithdrawModel theForm = withdrawReturnModel;
        try {
            
            final Date curDate = new Date();
            final String errorDispalyPath = "userreturn/chnnlTochnnlWithdrawReturnSearchUser";
            final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
            final ChannelUserVO userVO =sessionUser;
            this.constructVofromForm(userVO,theForm, channelTransferVO, curDate);
            model.addAttribute(PANEL_NAME, "Panel-One");
            channelTransferVO.setChannelTransferitemsVOList(theForm.getProductListWithTaxes());
            channelTransferVO.setActiveUserId(userVO.getActiveUserID());
            
            
            if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)).booleanValue()&&PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(channelTransferVO.getTransferSubType())){
            	//amount gets debited from Logged in user
            	channelTransferVO.setUserLoanVOList(userVO.getUserLoanVOList());
            }else if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)).booleanValue()&&PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(channelTransferVO.getTransferSubType())){
            	//in case of withdraw amount gets debited from the account of from user not the loggedin user
            	channelTransferVO.setUserLoanVOList(userVO.getUserLoanVOList());
            }
            else if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()&&PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(channelTransferVO.getTransferSubType())){
            	//amount gets debited from Logged in user
            	chnlSoSVOList.add(new ChannelSoSVO(userVO.getUserID(),userVO.getMsisdn(),userVO.getSosAllowed(),userVO.getSosAllowedAmount(),userVO.getSosThresholdLimit()));
            	channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
            }else if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()&&PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(channelTransferVO.getTransferSubType())){
            	//in case of withdraw amount gets debited from the account of from user not the loggedin user
            	chnlSoSVOList.add(new ChannelSoSVO(theForm.getFromUserID(),theForm.getFromMSISDN(),theForm.getFromUserSosAllowed(),theForm.getFromUserSosAllowedAmount(),theForm.getFromUserSosThresholdLimit()));
            	channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
            }
            


            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final int count = ChnlToChnlTransferTransactionCntrl.withdrawAndReturnChannelToChannel(con, channelTransferVO, theForm.isOutsideHierarchyFlag(), true,
                errorDispalyPath, curDate);

            if (count > 0) {
            	 if(((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS)).booleanValue())
	   	           { 
		             	UserEventRemarksVO userRemarskVO=null;
						ArrayList<UserEventRemarksVO> c2cRemarks=null;
						if(channelTransferVO!=null)
			    		   {
			    			   int insertCount=0;
			    			c2cRemarks=new ArrayList<>();
		                  	userRemarskVO=new UserEventRemarksVO();
		                  	userRemarskVO.setCreatedBy(channelTransferVO.getCreatedBy());
		                  	userRemarskVO.setCreatedOn(new Date());
		                  	userRemarskVO.setEventType(PretupsI.TRANSFER_TYPE_C2C);
		                  	userRemarskVO.setRemarks(channelTransferVO.getChannelRemarks());
		                  	userRemarskVO.setMsisdn(channelTransferVO.getFromUserCode());
		                  	userRemarskVO.setUserID(channelTransferVO.getFromUserID());
		                  	userRemarskVO.setUserType("SENDER");
		                  	userRemarskVO.setModule(PretupsI.C2C_MODULE);
		                  	c2cRemarks.add(userRemarskVO);
		                  	insertCount=new UserWebDAO().insertEventRemark(con, c2cRemarks);
		                  	if(insertCount<=0)
		                  	{
		                  		con.rollback();
		     	                 _log.error("process","Error: while inserting into userEventRemarks Table");
		     	                 throw new BTSLBaseException(this,"save","error.general.processing");
		                  	}
		                  	
			    		   }	
	   	           }
            	con.commit();
                theForm.setOperationPerformed(true);
                ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
                String senderTxnSubKey;
                String senderBalSubKey;
                String senderSMSKey;
                String receiverTxnSubKey;
                String receiverBalSubKey;
                String receiverSMSKey;
                String forwardPath;
                String sendSMSReceiverKey; // means sms send to receiver
                String sendSMSSenderKey; // means sms send to sender
                String sendSMSKey;
                boolean sendSMSReceiver = false;
                boolean sendSMSSender = true;
                String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
                String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);

                if (theForm.getReturnFlag()) {
                   senderTxnSubKey = PretupsErrorCodesI.CHNL_RETURN_SUCCESS_TXNSUBKEY;
                    senderBalSubKey = PretupsErrorCodesI.CHNL_RETURN_SUCCESS_BALSUBKEY;
                    senderSMSKey = PretupsErrorCodesI.CHNL_RETURN_SUCCESS;
                    receiverTxnSubKey = PretupsErrorCodesI.C2S_CHNL_CHNL_RETURN_RECEIVER_TXNSUBKEY;
                    receiverBalSubKey = PretupsErrorCodesI.C2S_CHNL_CHNL_RETURN_RECEIVER_BALSUBKEY;
                    receiverSMSKey = PretupsErrorCodesI.C2S_CHNL_CHNL_RETURN_RECEIVER;
                    forwardPath = "firstpage";
                    sendSMSReceiverKey = "pretups.channeltochannel.return.msg.success.nophoneinfosender";
                    sendSMSSenderKey = "pretups.channeltochannel.return.msg.success.nophoneinforeceiver";
                    sendSMSKey = "pretups.channeltochannel.return.msg.success.nophoneinfosenderreceiver";
                    if (PretupsI.TRANSFER_CATEGORY_TRANSFER.equals(channelTransferVO.getTransferCategory())) {
                        receiverSMSKey = PretupsErrorCodesI.CHNL_RETURN_SUCCESS_RECEIVER_AGENT;
                        senderSMSKey = PretupsErrorCodesI.CHNL_RETURN_SUCCESS_SENDER_AGENT;
                    }
                    // sending sms to the receiver
                    final UserDAO userDAO = new UserDAO();
                    final String userID = channelTransferVO.getToUserID();
                    UserPhoneVO primaryPhoneVOS = null;
                    UserPhoneVO phoneVO = null;
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue() && !((channelTransferVO.getToUserCode()).equalsIgnoreCase(theForm.getFromPrimaryMSISDN()))) {
                            primaryPhoneVOS = userDAO.loadUserAnyPhoneVO(con, theForm.getFromPrimaryMSISDN());
                        }
                        phoneVO = userDAO.loadUserAnyPhoneVO(con, channelTransferVO.getToUserCode());
                    } else {
                        phoneVO = userDAO.loadUserPhoneVO(con, userID);
                    }
                    Object[] smsListArr = null;
                    if (phoneVO != null) {
                        country = phoneVO.getCountry();
                        language = phoneVO.getPhoneLanguage();
                        smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiverForC2C(con, channelTransferVO, receiverTxnSubKey, receiverBalSubKey);
                        locale = new Locale(language, country);
                        final String[] array = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO
                            .getTransferID(), theForm.getNetPayableAmount(), theForm.getFromMSISDN() };
                        final BTSLMessages messages = new BTSLMessages(receiverSMSKey, array);
                        final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO
                            .getNetworkCode());
                        pushMessage.push();
                        sendSMSReceiver = true;
                    }
                    if (primaryPhoneVOS != null) {
                        country = primaryPhoneVOS.getCountry();
                        language = primaryPhoneVOS.getPhoneLanguage();
                        smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiverForC2C(con, channelTransferVO, receiverTxnSubKey, receiverBalSubKey);
                        locale = new Locale(language, country);
                        final String[] array = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO
                            .getTransferID(), theForm.getNetPayableAmount(), theForm.getFromMSISDN() };
                        final BTSLMessages messages = new BTSLMessages(receiverSMSKey, array);
                        final PushMessage pushMessage = new PushMessage(theForm.getFromPrimaryMSISDN(), messages, channelTransferVO.getTransferID(), null, locale,
                            channelTransferVO.getNetworkCode());
                        pushMessage.push();
                        sendSMSReceiver = true;
                    }
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_TO_LOGIN_USER)).booleanValue()) {
                        
                        final String prefUserID = channelTransferVO.getActiveUserId();
                        phoneVO = userDAO.loadUserPhoneVO(con, prefUserID);
                        if (phoneVO != null) {
                            country = phoneVO.getCountry();
                            language = phoneVO.getPhoneLanguage();
                            smsListArr = prepareSMSMessageList(theForm.getProductListWithTaxes(), senderTxnSubKey, senderBalSubKey);
                            locale = new Locale(language, country);
                            final String[] array = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO
                                .getTransferID(), theForm.getNetPayableAmount(), theForm.getToMSISDN() };
                            final BTSLMessages messages = new BTSLMessages(senderSMSKey, array);
                            final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO
                                .getNetworkCode());
                            pushMessage.push();
                            sendSMSSender = true;
                        } else {
                            sendSMSSender = false;
                        }
                    }
                    if (userVO.isStaffUser()) { // then send the sms to the
                        // parent user
                        senderSMSKey = PretupsErrorCodesI.CHNL_RETURN_SUCCESS_STAFF;
                        // ChannelUserVO channelUserVO= userDAO.load
                        phoneVO = userDAO.loadUserPhoneVO(con, channelTransferVO.getFromUserID());
                        if (phoneVO != null) {
                            country = phoneVO.getCountry();
                            language = phoneVO.getPhoneLanguage();
                            locale = new Locale(language, country);
                            smsListArr = prepareSMSMessageList(theForm.getProductListWithTaxes(), PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_TXNSUBKEY,
                                PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_BALSUBKEY);
                            final String[] array = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO
                                .getTransferID(), theForm.getNetPayableAmount(), theForm.getToMSISDN(), userVO.getUserName() };
                            final BTSLMessages messages = new BTSLMessages(senderSMSKey, array);
                            final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO
                                .getNetworkCode());
                            pushMessage.push();
                            sendSMSSender = true;
                        } else {
                            sendSMSSender = false;
                        }
                    }
                } else {
                    receiverTxnSubKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_TXNSUBKEY;
                    receiverBalSubKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_BALSUBKEY;
                    receiverSMSKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS;

                    senderTxnSubKey = PretupsErrorCodesI.C2S_CHNL_CHNL_WITHDRAW_RECEIVER_TXNSUBKEY;
                    senderBalSubKey = PretupsErrorCodesI.C2S_CHNL_CHNL_WITHDRAW_RECEIVER_BALSUBKEY;
                    senderSMSKey = PretupsErrorCodesI.C2S_CHNL_CHNL_WITHDRAW_RECEIVER;
                    forwardPath = "approveorder";

                    sendSMSReceiverKey = "pretups.channeltochannel.withdraw.msg.success.nophoneinfosender";
                    sendSMSSenderKey = "pretups.channeltochannel.withdraw.msg.success.nophoneinforeceiver";
                    sendSMSKey = "pretups.channeltochannel.withdraw.msg.success.nophoneinfosenderreceiver";

                    if (PretupsI.TRANSFER_CATEGORY_TRANSFER.equals(channelTransferVO.getTransferCategory())) {
                        senderSMSKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_RECEIVER_AGENT;
                        receiverSMSKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_SENDER_AGENT;
                    }

                    // sending sms to the receiver
                    final UserDAO userDAO = new UserDAO();
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_TO_LOGIN_USER)).booleanValue()) {
                       
                        final String userID = channelTransferVO.getActiveUserId();
                        UserPhoneVO phoneVO = null;
                        phoneVO = userDAO.loadUserPhoneVO(con, userID);
                        if (phoneVO != null) {
                            country = phoneVO.getCountry();
                            language = phoneVO.getPhoneLanguage();
                            final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiverForC2C(con, channelTransferVO, receiverTxnSubKey, receiverBalSubKey);
                            final Locale local = new Locale(language, country);
                            final String[] array = { BTSLUtil.getMessage(local, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(local, (ArrayList) smsListArr[1]), channelTransferVO
                                .getTransferID(), theForm.getNetPayableAmount(), theForm.getFromMSISDN() };
                            final BTSLMessages messages = new BTSLMessages(receiverSMSKey, array);
                            final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, local, channelTransferVO
                                .getNetworkCode());
                            pushMessage.push();
                            sendSMSReceiver = true;
                        } else {
                            sendSMSSender = false;
                        }
                    }
                    if (userVO.isStaffUser()) { // then send the sms to the
                        // parent user
                        receiverSMSKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_STAFF;
                        UserPhoneVO phoneVO = null;
                        phoneVO = userDAO.loadUserPhoneVO(con, channelTransferVO.getToUserID());
                        if (phoneVO != null) {
                            country = phoneVO.getCountry();
                            language = phoneVO.getPhoneLanguage();
                            final Locale local = new Locale(language, country);
                            final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiverForC2C(con, channelTransferVO, PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_TXNSUBKEY, PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_BALSUBKEY);
                           
                            final String[] array = { BTSLUtil.getMessage(local, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(local, (ArrayList) smsListArr[1]), channelTransferVO
                                .getTransferID(), theForm.getNetPayableAmount(), theForm.getFromMSISDN(), userVO.getUserName() };
                            final BTSLMessages messages = new BTSLMessages(receiverSMSKey, array);
                            final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, local, channelTransferVO
                                .getNetworkCode());
                            pushMessage.push();
                            sendSMSReceiver = true;
                        } else {
                            sendSMSSender = false;
                        }
                    }

                    final String prefUserID = channelTransferVO.getFromUserID();
                    UserPhoneVO primaryPhoneVOS = null;
                    UserPhoneVO phoneVO = null;
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue() && !((channelTransferVO.getFromUserCode()).equalsIgnoreCase(theForm.getFromPrimaryMSISDN()))) {
                            primaryPhoneVOS = userDAO.loadUserAnyPhoneVO(con, theForm.getFromPrimaryMSISDN());
                        }
                        phoneVO = userDAO.loadUserAnyPhoneVO(con, channelTransferVO.getFromUserCode());
                    } else {
                        phoneVO = userDAO.loadUserPhoneVO(con, prefUserID);
                    }
                    Object[] smsListArr = null;
                    Locale local = null;
                    if (phoneVO != null) {
                        country = phoneVO.getCountry();
                        language = phoneVO.getPhoneLanguage();
                        smsListArr = prepareSMSMessageList(theForm.getProductListWithTaxes(), senderTxnSubKey, senderBalSubKey);
                        local = new Locale(language, country);
                        final String[] array = { BTSLUtil.getMessage(local, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(local, (ArrayList) smsListArr[1]), channelTransferVO
                            .getTransferID(), theForm.getNetPayableAmount(), theForm.getToMSISDN() };
                        final BTSLMessages messages = new BTSLMessages(senderSMSKey, array);
                        final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, local, channelTransferVO
                            .getNetworkCode());
                        pushMessage.push();
                        sendSMSSender = true;
                        sendSMSReceiver = true;
                    }
                    if (primaryPhoneVOS != null) {
                        country = primaryPhoneVOS.getCountry();
                        language = primaryPhoneVOS.getPhoneLanguage();
                        local = new Locale(language, country);
                        final String[] array = { BTSLUtil.getMessage(local, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(local, (ArrayList) smsListArr[1]), channelTransferVO
                            .getTransferID(), theForm.getNetPayableAmount(), theForm.getToMSISDN() };
                        final BTSLMessages messages = new BTSLMessages(senderSMSKey, array);
                        final PushMessage pushMessage = new PushMessage(theForm.getFromPrimaryMSISDN(), messages, channelTransferVO.getTransferID(), null, local,
                            channelTransferVO.getNetworkCode());
                        pushMessage.push();
                        sendSMSSender = true;
                        sendSMSReceiver = true;
                    }
                }
                if (sendSMSReceiver && sendSMSSender) {
                    if (theForm.getReturnFlag()) {
                       model.addAttribute(SUCCESS_KEY, PretupsRestUtil.getMessageString("pretups.userreturn.msg.success",new String[] { channelTransferVO.getTransferID()}));
                        return true;
                    } else {
                        model.addAttribute(SUCCESS_KEY, PretupsRestUtil.getMessageString("pretups.userreturn.withdraw.msg.success",new String[] { channelTransferVO.getTransferID()}));
                        return true;
                    }
                }
                final String[]  arr= { channelTransferVO.getTransferID(), channelTransferVO.getToUserName() };
                String message=null;
                if (sendSMSReceiver && !sendSMSSender) {
                   
                    message= PretupsRestUtil.getMessageString(sendSMSReceiverKey,arr);
                }
                if (!sendSMSReceiver && sendSMSSender) {
                    message= PretupsRestUtil.getMessageString(sendSMSSenderKey,arr);
                }
                if (!sendSMSReceiver && !sendSMSSender) {
                	message= PretupsRestUtil.getMessageString(sendSMSKey,arr);
                }
                model.addAttribute(SUCCESS_KEY,message);
                return true;
            } else {
                con.rollback();
                if (theForm.getReturnFlag()) {
                    model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.userreturn.msg.unsuccess"));
                    return false;
                } else {
                    model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.userreturn.withdraw.msg.unsuccess"));
                    return false;
                }
            }
            } catch (Exception e) {
            _log.error(methodName, "Exception:e=" + e);
            _log.errorTrace(methodName, e);
            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
            return false;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("ChnnlToChnnlReturnWithdrawServiceImpl#approveWithdrawReturn");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
            }
        }
    }
    
    private void constructVofromForm(ChannelUserVO channelUserVO, ChnnlToChnnlReturnWithdrawModel pTheForm, ChannelTransferVO pChannelTransferVO, Date pCurDate) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("constructVofromForm", "Entered TheForm: " + pTheForm + " ChannelTransferVO: " + pChannelTransferVO + " CurDate " + pCurDate);
        }
        pChannelTransferVO.setNetworkCode(channelUserVO.getNetworkID());
        pChannelTransferVO.setNetworkCodeFor(channelUserVO.getNetworkID());
        pChannelTransferVO.setCategoryCode(pTheForm.getFromCategoryCode());
        pChannelTransferVO.setSenderGradeCode(pTheForm.getFromGradeCode());
        pChannelTransferVO.setReceiverGradeCode(pTheForm.getToGradeCode());
        pChannelTransferVO.setDomainCode(pTheForm.getDomainCode());
        pChannelTransferVO.setFromUserID(pTheForm.getFromUserID());
        pChannelTransferVO.setFromUserName(pTheForm.getFromUserName());
        pChannelTransferVO.setToUserID(pTheForm.getToUserID());
        pChannelTransferVO.setToUserName(pTheForm.getToUserName());
        pChannelTransferVO.setTransferDate(pCurDate);
        pChannelTransferVO.setGraphicalDomainCode(pTheForm.getFromGeoDomain());
        pChannelTransferVO.setCommProfileSetId(pTheForm.getFromCommissionProfileID());
        pChannelTransferVO.setCommProfileVersion(pTheForm.getFromCommissionProfileVersion());
        pChannelTransferVO.setDualCommissionType(pTheForm.getFromUsrDualCommType());
        pChannelTransferVO.setChannelRemarks(pTheForm.getRemarks());
        pChannelTransferVO.setCreatedOn(pCurDate);
        pChannelTransferVO.setCreatedBy(channelUserVO.getActiveUserID());
        pChannelTransferVO.setModifiedOn(pCurDate);
        pChannelTransferVO.setModifiedBy(channelUserVO.getActiveUserID());
        pChannelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        pChannelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        pChannelTransferVO.setTransferInitatedBy(channelUserVO.getUserID());
        pChannelTransferVO.setSenderTxnProfile(pTheForm.getFromTxnProfile());
        pChannelTransferVO.setReceiverTxnProfile(pTheForm.getToTxnProfile());
        pChannelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
        pChannelTransferVO.setReceiverCategoryCode(pTheForm.getToCategoryCode());
        pChannelTransferVO.setTransferCategory(pTheForm.getTransferCategory());
        pChannelTransferVO.setRequestedQuantity(PretupsBL.getSystemAmount(pTheForm.getTotalReqQty()));
        pChannelTransferVO.setTransferMRP(PretupsBL.getSystemAmount(pTheForm.getTransferMRP()));
        pChannelTransferVO.setPayableAmount(PretupsBL.getSystemAmount(pTheForm.getPayableAmount()));
        pChannelTransferVO.setNetPayableAmount(PretupsBL.getSystemAmount(pTheForm.getNetPayableAmount()));
        pChannelTransferVO.setTotalTax1(PretupsBL.getSystemAmount(pTheForm.getTotalTax1()));
        pChannelTransferVO.setTotalTax2(PretupsBL.getSystemAmount(pTheForm.getTotalTax2()));
        pChannelTransferVO.setTotalTax3(PretupsBL.getSystemAmount(pTheForm.getTotalTax3()));
        pChannelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
        if (pTheForm.isOutsideHierarchyFlag()) {
            pChannelTransferVO.setControlTransfer(PretupsI.NO);
        } else {
            pChannelTransferVO.setControlTransfer(PretupsI.YES);
        }
        if (pTheForm.getReturnFlag()) {
            pChannelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);
        } else {
            pChannelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
        }
        if (channelUserVO.getSessionInfoVO().getMessageGatewayVO() != null) {
            pChannelTransferVO.setRequestGatewayCode(channelUserVO.getSessionInfoVO().getMessageGatewayVO().getGatewayCode());
            pChannelTransferVO.setRequestGatewayType(channelUserVO.getSessionInfoVO().getMessageGatewayVO().getGatewayType());
        }
        // adding the some additional information for sender/reciever
        pChannelTransferVO.setReceiverGgraphicalDomainCode(pTheForm.getToGeoDomain());
        pChannelTransferVO.setReceiverDomainCode(pTheForm.getToDomainCode());
        pChannelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(pTheForm.getToMSISDN()));
        pChannelTransferVO.setFromUserCode(PretupsBL.getFilteredMSISDN(pTheForm.getFromMSISDN()));
        if (pTheForm.getIsReturnFlag()) {
            pChannelTransferVO.setToChannelUserStatus(pTheForm.getFromChannelUserStatus());
            pChannelTransferVO.setFromChannelUserStatus(pTheForm.getToChannelUserStatus());
        } else {
            pChannelTransferVO.setToChannelUserStatus(pTheForm.getToChannelUserStatus());
            pChannelTransferVO.setFromChannelUserStatus(pTheForm.getFromChannelUserStatus());
        }

        if (_log.isDebugEnabled()) {
            _log.debug("constructVofromForm", "Exited TheForm: " + pTheForm + " ChannelTransferVO: " + pChannelTransferVO + " CurDate " + pCurDate);
        }
    }
    
    
    /**
     * Prepare the SMS message which we have to send the user as SMS
     * 
     * @param pReturnedProductList
     *            ArrayList
     * @param p_smsKey
     * @return ArrayList
     */
    private Object[] prepareSMSMessageList(ArrayList pReturnedProductList, String pTxnKey, String pBalKey) {
        if (_log.isDebugEnabled()) {
            _log.debug("prepareSMSMessageList",
                "Entered p_returnedProductList size =  : " + pReturnedProductList.size() + " p_txnKey : " + pTxnKey + " p_balKey : " + pBalKey);
        }
        final ArrayList txnSmsMessageList = new ArrayList();
        final ArrayList balSmsMessageList = new ArrayList();
        KeyArgumentVO keyArgumentVO;
        String[] argsArr;
        ChannelTransferItemsVO channelTransferItemsVO;
        for (int i = 0, k = pReturnedProductList.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) pReturnedProductList.get(i);
            keyArgumentVO = new KeyArgumentVO();
            argsArr = new String[3];
            argsArr[1] = channelTransferItemsVO.getRequestedQuantity();
            argsArr[0] = String.valueOf(channelTransferItemsVO.getShortName());
            keyArgumentVO.setKey(pTxnKey);
            keyArgumentVO.setArguments(argsArr);
            txnSmsMessageList.add(keyArgumentVO);

            keyArgumentVO = new KeyArgumentVO();
            argsArr = new String[3];
            argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemsVO.getAfterTransSenderPreviousStock() - channelTransferItemsVO.getApprovedQuantity());
            argsArr[0] = String.valueOf(channelTransferItemsVO.getShortName());
            keyArgumentVO.setKey(pBalKey);
            keyArgumentVO.setArguments(argsArr);
            balSmsMessageList.add(keyArgumentVO);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("prepareSMSMessageList", "Exited  txnSmsMessageList.size() = " + txnSmsMessageList.size() + ", balSmsMessageList.size()" + balSmsMessageList.size());
        }
        return new Object[] { txnSmsMessageList, balSmsMessageList };
    }
    
}
