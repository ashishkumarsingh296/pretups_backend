package com.web.pretups.channel.user.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.subscriber.businesslogic.ChangeLocaleVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.user.web.ChangeLocaleModel;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;






/**
 * @author rahul.arya
 *
 */
@Service("ChangeNotificationLanguageService")
public class ChangeNotificationLanguageServiceImpl implements ChangeNotificationLanguageService {

	public static final Log log = LogFactory
			.getLog(ChangeNotificationLanguageServiceImpl.class.getName());
	
	private static final String FAIL_KEY = "fail";
	
	private static final String PANEL_NO = "PanelNo";
	
	private static final String ENTERED = "Entered";
	
	private static final String CHANGE_NOTIFY_MODEL="changeNotifyModel";
	
	private static final String LOAD_USER_PHONE_DETAILS="loadUserPhoneDetails";
	
	private static final String LOAD_CONFIRM_SELF_LANG="loadconfirmSelfLang";
	
	private static final String LOAD_USER_LIST="loadUserList";
	
	private static final String NO_DATA_FOUND="channeluser.searchuserforselflangsetting.err.msg.nodatafound";
	
	private static final String SELF_INFORMATION="selfInformation";
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int loadSelfinfo(
			HttpServletRequest request, UserVO userVO,
			ChangeLocaleModel changeLocaleModel, Model model)
			{
		
		
		final String methodName = "loadSelfinfo";
		if (log.isDebugEnabled()) {
			log.debug("loadSelfinfo", ENTERED);
		}

		CategoryDAO categoryDAO = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		ArrayList userPhoneInfoList = null;
		
		ArrayList languageList = null;
		try {
			channelUserWebDAO = new ChannelUserWebDAO();

			// get a connection from the connection pool
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			categoryDAO = new CategoryDAO();
			changeLocaleModel.flush();
			changeLocaleModel.setCategoryList(categoryDAO.loadCategoryHierarchyList(con, userVO.getDomainID(), userVO.getCategoryVO().getSequenceNumber()));
			changeLocaleModel.setCategoryListSize(changeLocaleModel.getCategoryList().size());
			changeLocaleModel.setLoginUserID(userVO.getUserID());
			if (userVO.isStaffUser()) {
				changeLocaleModel.setLoginUserName(userVO.getParentName());
			} else {
				changeLocaleModel.setLoginUserName(userVO.getUserName());
			}
			changeLocaleModel.setLoginUserCatCode(userVO.getCategoryCode());
			changeLocaleModel.setLoginUserCatName(userVO.getCategoryVO().getCategoryName());
			changeLocaleModel.setRequestType("default");

			// In case the logged-In user don't have any category under the
			// heirarchy
			if (changeLocaleModel.getCategoryListSize() == 1) {
				if (userVO.isStaffUser()) {
					changeLocaleModel.setUserName(userVO.getParentName());
				} else {
					changeLocaleModel.setUserName(userVO.getUserName());
				}
				changeLocaleModel.setUserID(userVO.getUserID());
				changeLocaleModel.setMsisdn(userVO.getMsisdn());
				changeLocaleModel.setCategoryName(userVO.getCategoryVO().getCategoryName());

				changeLocaleModel.setCategoryNameForMsisdn(userVO.getCategoryVO().getCategoryName());
				changeLocaleModel.setUserNameForMsisdn(userVO.getUserName());
				channelUserDAO = new ChannelUserDAO();

				languageList = channelUserDAO.loadLanguageListForUser(con);
				if (languageList == null || languageList.isEmpty()) {
				
					model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("channeluser.selfinformation.err.msg.nolangfound"));
					return 1;
				}
				// set the language list for the combo of next screen
				changeLocaleModel.setLanguageList(languageList);

				userPhoneInfoList = channelUserWebDAO.loadUserPhoneDetailsList(con, userVO.getUserID());
				if (userPhoneInfoList == null || userPhoneInfoList.isEmpty()) {

					
					 model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("channeluser.selfinformation.err.msg.nophoneuserfound"));
					 return 1;
				}
				changeLocaleModel.setUserPhoneInfoList(userPhoneInfoList);
				changeLocaleModel.setUserPhoneInfoListSize(userPhoneInfoList.size());

				// for setting the user's current language in the language combo
				// when the selfLanguageSetting.jsp called
				setLanguageCode(userPhoneInfoList, languageList);
				if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.STAFF_AS_USER))).booleanValue()) {
					model.addAttribute(CHANGE_NOTIFY_MODEL, changeLocaleModel);
					return 2;
				}
			}
		}// end of try
		catch (Exception e) {
		log.error("loadUserSelfInfo", "Exception: " + e.getMessage());
		log.errorTrace(methodName, e);
			
		} finally {
			if(mcomCon != null)
			{
				mcomCon.close("ChangeLocaleAction#loadSelfinfo");
				mcomCon=null;
			}
			if (log.isDebugEnabled()) {
				log.debug("loadUserSelfInfo","Exit");
			}
		}
		model.addAttribute(CHANGE_NOTIFY_MODEL, changeLocaleModel);
		return 1;
			}

	@SuppressWarnings("rawtypes")
	private void setLanguageCode(ArrayList userPhoneInfoList, ArrayList languageList) {
		ChannelUserVO channelUserVO;
		String phoneLang;
		if (!userPhoneInfoList.isEmpty() && !languageList.isEmpty()) {
			for (int i = 0, j = userPhoneInfoList.size(); i < j; i++) {
				channelUserVO = (ChannelUserVO) userPhoneInfoList.get(i);
				phoneLang = channelUserVO.getUserPhoneVO().getPhoneLanguage();
				for (int x = 0, y = languageList.size(); x < y; x++) {
					if (phoneLang.equals(((ChangeLocaleVO) languageList.get(x)).getLanguageCode())) {
						channelUserVO.setLanguageCode(((ChangeLocaleVO) languageList.get(x)).getLanguageCode());
						break;
					}
				}
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean loadUserPhoneDetails(ChangeLocaleModel changeLocaleModel,HttpServletRequest request,HttpServletResponse response,Model model,UserVO userVO,BindingResult bindingResult)
	{
		final String methodName = LOAD_USER_PHONE_DETAILS;
		if (log.isDebugEnabled()) {
			log.debug(LOAD_USER_PHONE_DETAILS, ENTERED);
		}

		ChannelUserDAO channelUserDAO = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList userPhoneInfoList = null;
		ArrayList userPhoneInfoListFromDAO = null;
		ListValueVO listValueVO = null;
		ArrayList userList = null;
		ChannelUserVO channelUserVO = null;
		ArrayList languageList = null;
		String msisdn = null;
		String filteredMsisdn = null;
		String msisdnPrefix = null;
		NetworkPrefixVO networkPrefixVO = null;
		String networkCode = null;
		ArrayList userMsisdnList = null;
		UserDAO userDAO = null;
		ChannelUserWebDAO channelUserWebDAO = null;
        boolean bool = false;
		try {
			channelUserWebDAO = new ChannelUserWebDAO();

			// get a connection from the connection pool
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			channelUserDAO = new ChannelUserDAO();
			if (request.getParameter("submitButtonMSISDN") != null) {
				CommonValidator commonValidator = new CommonValidator(
						"configfiles/channeluser/Validator-ChangeNotifLang.xml",
						changeLocaleModel, "changeNotificationLanguageFromMSISDN");
				Map<String, String> errorMessages = commonValidator
						.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				model.addAttribute(PANEL_NO, "Panel-One");
				request.getSession().setAttribute(PANEL_NO, "Panel-One");
			}

			if (request.getParameter("submitButtonForUserName") != null) {
				CommonValidator commonValidator = new CommonValidator(
						"configfiles/channeluser/Validator-ChangeNotifLang.xml",
						changeLocaleModel, "changeNotificationLanguageFromUserName");
				Map<String, String> errorMessages = commonValidator
						.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				model.addAttribute(PANEL_NO, "Panel-Two");
				request.getSession().setAttribute(PANEL_NO, "Panel-Two");
			}
			if (bindingResult.hasFieldErrors()) {
				request.getSession().setAttribute(
						CHANGE_NOTIFY_MODEL, changeLocaleModel);
				return false;
			}
			// If MSISDN or user name both is entered by the user,
			// then the priority would be given to msisdn and the request
			// is processed for the msisdn, otherwise for the user name
			if (!BTSLUtil.isNullString(changeLocaleModel.getMsisdn())) {
				if (!BTSLUtil.isNullString(changeLocaleModel.getMsisdn())) {
					changeLocaleModel.setRequestType("ChangeLangForMsisdn");
					// Validating the MSISDN in the current line
					msisdn = changeLocaleModel.getMsisdn().trim();

					if (log.isDebugEnabled()) {
						log.debug(LOAD_USER_PHONE_DETAILS, "Processing starts from MSISDN " + msisdn);
					}
					filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn); // before
					// process
					// MSISDN
					// filter
					// each-one

					// check for valid MSISDN
					if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
						if (log.isDebugEnabled()) {
							log.debug(LOAD_USER_PHONE_DETAILS, "Not a valid MSISDN " + msisdn);
						}
						throw new BTSLBaseException(this, LOAD_USER_PHONE_DETAILS, "channeluser.selfinformation.label.novalidmsisdn", SELF_INFORMATION);
						 
					}

					// check prefix of the MSISDN
					msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
					// the
					// prefix
					// of
					// the
					// MSISDN
					networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
					if (networkPrefixVO == null) {
						if (log.isDebugEnabled()) {
							log.debug(LOAD_USER_PHONE_DETAILS, "Not Network prefix found " + msisdn);
						}
						throw new BTSLBaseException(this, LOAD_USER_PHONE_DETAILS, "channeluser.selfinformation.label.noprefixfound", SELF_INFORMATION);
					}

					// check network support of the MSISDN
					networkCode = networkPrefixVO.getNetworkCode();
					if (!networkCode.equals(userVO.getNetworkID())) {
						if (log.isDebugEnabled()) {
							log.debug(LOAD_USER_PHONE_DETAILS, "Not supporting Network" + msisdn);
						}
					    throw new BTSLBaseException(this, LOAD_USER_PHONE_DETAILS, "channeluser.selfinformation.label.nonetsupport", SELF_INFORMATION);
					}

					userDAO = new UserDAO();
					// More than one MSISDN can be assigned to the logged-In
					// user
					userMsisdnList = userDAO.loadUserPhoneList(con, userVO.getUserID());
					boolean msisdnFlag = false;
					for (int i = 0, j = userMsisdnList.size(); i < j; i++) {
						if (((UserPhoneVO) (userMsisdnList.get(i))).getMsisdn().equals(filteredMsisdn)) {
							msisdnFlag = true;
							break;
						}
					}
					// If the entered MSISDN is of logged-In user
					if (msisdnFlag) {
						userPhoneInfoListFromDAO = channelUserWebDAO.loadUserPhoneDetailsList(con, userVO.getUserID());
						// if the size of list returned from DAO is more than 1
						// then filter this list for the entered MSISDN by the
						// end user
						if (userPhoneInfoListFromDAO.size() > 1) {
							userPhoneInfoList = new ArrayList();
							for (int i = 0, j = userPhoneInfoListFromDAO.size(); i < j; i++) {
								channelUserVO = (ChannelUserVO) userPhoneInfoListFromDAO.get(i);
								if (channelUserVO.getUserPhoneVO().getMsisdn().equals(filteredMsisdn)) {
									userPhoneInfoList.add(channelUserVO);
									break;
								}
							}
						} else {
							userPhoneInfoList = new ArrayList();
							channelUserVO = (ChannelUserVO) userPhoneInfoListFromDAO.get(0);
							userPhoneInfoList.add(channelUserVO);
						}
						changeLocaleModel.setCategoryNameForMsisdn(userVO.getCategoryVO().getCategoryName());
						changeLocaleModel.setUserNameForMsisdn(userVO.getUserName());
						changeLocaleModel.setUserIDForUpdateLang(userVO.getUserID());
					}
					else {
						// This DAO's method gives channelUserVO only under the
						// logged-In user
						// hierarchy not the logged_In user's channelUserVO
						channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMsisdn, userVO.getUserID(), PretupsI.STATUS_IN, "'Y','S','SR'");
						if (channelUserVO == null) {
							throw new BTSLBaseException(this, LOAD_USER_PHONE_DETAILS, "channeluser.selfinformation.label.msisdnnotinheirarchy", SELF_INFORMATION);
							
						}
						userPhoneInfoListFromDAO = channelUserWebDAO.loadUserPhoneDetailsList(con, channelUserVO.getUserID());
						changeLocaleModel.setUserIDForUpdateLang(channelUserVO.getUserID());
						changeLocaleModel.setCategoryNameForMsisdn(channelUserVO.getCategoryVO().getCategoryName());
						changeLocaleModel.setUserNameForMsisdn(channelUserVO.getUserName());

						// if the size of list returned from DAO is more than 1
						// then filter this list for the entered MSISDN by the
						// end user
						if (userPhoneInfoListFromDAO.size() > 1) {
							userPhoneInfoList = new ArrayList();
							for (int i = 0, j = userPhoneInfoListFromDAO.size(); i < j; i++) {
								channelUserVO = (ChannelUserVO) userPhoneInfoListFromDAO.get(i);
								if (channelUserVO.getUserPhoneVO().getMsisdn().equals(filteredMsisdn)) {
									userPhoneInfoList.add(channelUserVO);
									break;
								}
							}
						} else {
							userPhoneInfoList = new ArrayList();
							channelUserVO = (ChannelUserVO) userPhoneInfoListFromDAO.get(0);
							userPhoneInfoList.add(channelUserVO);
						}
					}// end of else
					
					bool = true;
				}
			}
			else {
				changeLocaleModel.setRequestType("ChangeLangForUserName");
				String user = changeLocaleModel.getUserName();
				
				userNameCheck(changeLocaleModel, methodName, user);
				userList = channelUserWebDAO.loadCategoryUserHierarchy(con, changeLocaleModel.getCategoryCode(), userVO.getNetworkID(), changeLocaleModel.getUserName(), userVO.getUserID());

				if (userList == null || userList.isEmpty()) {
					if (log.isDebugEnabled()) {
						log.debug(LOAD_USER_PHONE_DETAILS, "User not found");
					}
					request.getSession().setAttribute(CHANGE_NOTIFY_MODEL, changeLocaleModel);
					 model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("channeluser.searchuserforselflangsetting.err.msg.nodatafound"));
					 
					return false;
				} else if (userList.size() == 1) {
					UserVO userVo = (UserVO) userList.get(0);
					changeLocaleModel.setUserName(userVo.getUserName());
					changeLocaleModel.setUserID(userVo.getUserID());

				
					bool = true;
				} else if (userList.size() > 1) {

					// This is the case when userList size greater than 1
					// if user click the submit button after performing
					// search through searchUser and select one form the shown
					// list at that time we
					// set the userid on the form(becs two user have the same
					// name but different id)
					// so here we check the userId is null or not it is not null
					// iterate the list and open the screen
					// in edit mode corresponding to the userid

					boolean flag = true;
					if (!BTSLUtil.isNullString(changeLocaleModel.getUserID())) {
						for (int i = 0, j = userList.size(); i < j; i++) {
							UserVO uservO = (UserVO) userList.get(i);
							if (changeLocaleModel.getUserID().equals(uservO.getUserID()) && (changeLocaleModel.getUserName().compareTo(uservO.getUserName())) == 0) {
								changeLocaleModel.setUserName(uservO.getUserName());
								flag = false;

								bool = true;
								break;
							}
						}
					}
					else {
						UserVO userNextVO = null;
						for (int i = 0, j = userList.size(); i < j; i++) {
							UserVO Uservo = (UserVO) userList.get(i);
							if ((changeLocaleModel.getUserName().compareTo(Uservo.getUserName())) == 0) {
								if ((i + 1) < j) {
									userNextVO = (UserVO) userList.get(i + 1);
									if ((changeLocaleModel.getUserName().compareTo(userNextVO.getUserName())) == 0) {
										flag = true;
										break;
									} else {
										flag = false;
										break;
									}
								}
								else {
									flag = false;
									break;
								}
							}
						}
					}// end of else
						if (flag) {
							
							model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(NO_DATA_FOUND));
							
						}
				}
				else {
					
					model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(NO_DATA_FOUND));
					
				}
				listValueVO = BTSLUtil.getOptionDesc(changeLocaleModel.getCategoryCode(), changeLocaleModel.getCategoryList());
				changeLocaleModel.setCategoryName(listValueVO.getLabel());
				userPhoneInfoList = channelUserWebDAO.loadUserPhoneDetailsList(con, changeLocaleModel.getUserID());
			}// end of else

			languageList = channelUserDAO.loadLanguageListForUser(con);
			if (languageList == null || languageList.isEmpty()) {
				throw new BTSLBaseException(this, "loadUserPhoneDetail", "channeluser.selfinformation.err.msg.nolangfound", SELF_INFORMATION);
				
			}
			// set the language list for the combo of next screen
			changeLocaleModel.setLanguageList(languageList);
			if (userPhoneInfoList == null || userPhoneInfoList.isEmpty()) {
				throw new BTSLBaseException(this, "loadUserPhoneDetail", "channeluser.selfinformation.err.msg.nophoneuserfound", SELF_INFORMATION);
				
			}
			changeLocaleModel.setUserPhoneInfoList(userPhoneInfoList);
			changeLocaleModel.setUserPhoneInfoListSize(userPhoneInfoList.size());

			setLanguageCode(userPhoneInfoList, languageList);

		}// end of try
		catch (Exception e) {
			 model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
			log.error(LOAD_USER_PHONE_DETAILS, "Exception: " + e.getMessage());
			log.errorTrace(methodName, e);
			
		} finally {
			if(mcomCon != null)
			{
				mcomCon.close("ChangeLocaleAction#loadUserPhoneDetail");
				mcomCon=null;
			}
			if (log.isDebugEnabled()) {
				log.debug(LOAD_USER_PHONE_DETAILS, "Exiting ::  " );
			}
		}
		request.getSession().setAttribute(CHANGE_NOTIFY_MODEL, changeLocaleModel);
		return bool;
	}

	private void userNameCheck(ChangeLocaleModel changeLocaleModel,
			final String methodName, String user) {
		try{
		   
		    String[] parts = user.split("\\(");
			String userName = parts[0]; 
			changeLocaleModel.setUserName(userName);
			String a = parts[1];
			String[] w1=a.split("\\)");
			changeLocaleModel.setUserID(w1[0]);
		    
		    }
		    catch(Exception e)
		    {
		    	log.error(methodName, "Name selected did not had ID "
						+ e);
				log.errorTrace(methodName, e);
		    }
	}
	
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadconfirmSelfLang (ChangeLocaleModel changeLocaleModel,HttpServletRequest request,HttpServletResponse response,Model model,UserVO userVO,BindingResult bindingResult) throws SQLException
	{
		final String methodName = LOAD_CONFIRM_SELF_LANG;
		if (log.isDebugEnabled()) {
			log.debug(LOAD_CONFIRM_SELF_LANG, ENTERED);
		}

		
		ArrayList allUserVOList = null;
		ArrayList listAfterChangeLang = null;
		ChannelUserVO channelUserVO = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		MComConnectionI mcomCon = null;
		Connection con = null;
		String langCode = null;
		ArrayList langList = null;
		UserVO userVo = null;
		int allUserVOListSize = 0;
		int updateCount = 0;

		try {
			

			// Use to store all the userVO's from the selfLanguageSetting.jsp
			// (from Check boxes whether checked and unchecked)
			allUserVOList = changeLocaleModel.getUserPhoneInfoList();

			listAfterChangeLang = new ArrayList();
			if (allUserVOList != null && !allUserVOList.isEmpty()) {
				allUserVOListSize = allUserVOList.size();
			}

			// Make the list(listAfterChangeLang) which stores only VO's which
			// has been
			// checked from the selfLanguageSetting.jsp
			// <<** it is for Confirmation jsp **>>
			if (allUserVOList != null && allUserVOListSize > 0) {
				for (int i = 0, j = allUserVOListSize; i < j; i++) {
					userVo = (UserVO) allUserVOList.get(i);
					if (userVo.getStatus().equals(PretupsI.YES)) {
						listAfterChangeLang.add(userVo);
					}
				}
			}

			// if no check box is checked then give proper message
			if (listAfterChangeLang == null || listAfterChangeLang.isEmpty()) {
				throw new BTSLBaseException(this, LOAD_CONFIRM_SELF_LANG, "channeluser.selflanguagesetting.err.msg.selectsub", "selfLangSetting");
			}

			// For getting the languageName for the confirmation jsp and also
			// set the country
			// in the channelUserVO for inserting into the database
			langList = changeLocaleModel.getLanguageList();
			for (int i = 0, j = listAfterChangeLang.size(); i < j; i++) {
				channelUserVO = (ChannelUserVO) listAfterChangeLang.get(i);
				langCode = channelUserVO.getLanguageCode();
				for (int x = 0, y = langList.size(); x < y; x++) {
					if (langCode.equals(((ChangeLocaleVO) langList.get(x)).getLanguageCode())) {
						channelUserVO.setLanguageName(((ChangeLocaleVO) langList.get(x)).getLanguageName());
						channelUserVO.setCountry(((ChangeLocaleVO) langList.get(x)).getCountry());
						break;
					}
				}
			}

			changeLocaleModel.setListAfterChangeLang(listAfterChangeLang);

			channelUserWebDAO = new ChannelUserWebDAO();
			
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			Date currentDate = new Date();

			// Call the DAO's method for change the language of the
			// as selected by the selfLanguageSetting.jsp when the language
			// is changed on the basis of MSISDN
			if (("ChangeLangForMsisdn").equals(changeLocaleModel.getRequestType())) {
				updateCount = channelUserWebDAO.updateLanguage(con, changeLocaleModel.getListAfterChangeLang(), currentDate, userVO.getUserID(), changeLocaleModel.getUserIDForUpdateLang());
			} else {
				// Call the DAO's method for change the language of the
				// as selected by the selfLanguageSetting.jsp when the language
				// is changed on the basis of userID
				updateCount = channelUserWebDAO.updateLanguage(con, changeLocaleModel.getListAfterChangeLang(), currentDate, userVO.getUserID(), changeLocaleModel.getUserID());
			}

			// If user is not updated in database due to some problem then give
			// proper msg.
			if (updateCount <= 0) {
				mcomCon.finalRollback();
				throw new BTSLBaseException(this, LOAD_CONFIRM_SELF_LANG, "channeluser.selfinformation.err.msg.cannotupdate", SELF_INFORMATION);
			}
			// Give the proper success message after changing the language
			mcomCon.finalCommit();
			
			model.addAttribute("success",PretupsRestUtil.getMessageString("channeluser.selfinformation.msg.succmsg"));
		} 
		 catch (BTSLBaseException e) {
	            log.errorTrace(methodName, e);
	            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage(),e.getArgs()));
	            
	            
	        } finally {
			if (log.isDebugEnabled()) {
				log.debug("confirmSelfLangSetting", "Exiting");
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<UserVO> loadUserList(ChangeLocaleModel changeLocaleModel,UserVO userVO,String categoryCode,String userName, HttpServletRequest request) {
		final String methodName = LOAD_USER_LIST;
		if (log.isDebugEnabled()) {
			log.debug(LOAD_USER_LIST, ENTERED);
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		ArrayList userList = null;
		try {
			channelUserWebDAO = new ChannelUserWebDAO();

			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			userList = channelUserWebDAO.loadCategoryUserHierarchy(con, categoryCode, userVO.getNetworkID(), userName, userVO.getUserID());

			changeLocaleModel.setUserList(userList);
			changeLocaleModel.setUserListSize(userList.size());
		}
		catch (Exception e) {
			log.error(LOAD_USER_LIST, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			
		} finally {
			if(mcomCon != null)
			{
				mcomCon.close("ChangeNotificationLanguageServiceImpl#loadUserList");
				mcomCon=null;
			}
			if (log.isDebugEnabled()) {
				log.debug("loadUserList", "Exiting");
			}
		}
		return userList;
	}
	
	
	
	
	
	
	
	
	
	
}
