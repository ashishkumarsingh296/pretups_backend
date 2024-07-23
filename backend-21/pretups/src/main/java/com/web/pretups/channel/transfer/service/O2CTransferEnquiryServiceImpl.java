package com.web.pretups.channel.transfer.service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.MComReportDBConnection;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.ibm.icu.util.Calendar;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;
import com.web.pretups.channel.transfer.web.ChannelTransferEnquiryModel;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

/**
 * @author rahul.arya
 *
 */
@Service("o2CTransferEnquiryService")
public class O2CTransferEnquiryServiceImpl implements O2CTransferEnquiryService {

	public static final Log _log = LogFactory.getLog(O2CTransferEnquiryServiceImpl.class.getName());

	private static final String MODEL_KEY = "o2ctransferenquiry";
	private static final String FAIL_KEY = "fail";
    public static final String METHOD_NAME="O2CTransferEnquiryServiceImpl";
    private static final float EPSILON=0.0000001f;
	/**
	 * This method loads the information which has been displayed on the first
	 * page for the channel user.
	 */

	@SuppressWarnings("unchecked")
	@Override
	public void channelUserEnquiry(ChannelUserVO sessionUser, Model model,ChannelTransferEnquiryModel channelTransferEnquiryModel)
			throws IOException, BTSLBaseException, ParseException {
		final String METHOD_NAME = "channelUserEnquiry";
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			channelTransferEnquiryModel.setChannelUserLoginedFlag(true);

			channelTransferEnquiryModel.setChannelDomain(sessionUser.getDomainID());

			channelTransferEnquiryModel.setChannelDomainDesc(sessionUser.getDomainName());

			channelTransferEnquiryModel.setChannelOwnerCategoryUserName(sessionUser.getOwnerName());

			channelTransferEnquiryModel.setChannelOwnerCategoryUserID(sessionUser.getOwnerID());
			
			final ArrayList userGeoList = sessionUser.getGeographicalAreaList();

			//boolean bool;

			UserGeographiesVO geographiesVO = null;

			ListValueVO listValueVO = null;

			final ArrayList geoList = new ArrayList();

			for (int i = 0, k = userGeoList.size(); i < k; i++) {
				geographiesVO = (UserGeographiesVO) userGeoList.get(i);
				geoList.add(new ListValueVO(geographiesVO.getGraphDomainName(),geographiesVO.getGraphDomainCode()));
			}

			if (geoList.size() == 1) {
				listValueVO = (ListValueVO) geoList.get(0);
				channelTransferEnquiryModel.setGeoDomainCode(listValueVO.getValue());
				channelTransferEnquiryModel.setGeoDomainCodeDesc(listValueVO.getLabel());
			}

			final ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();

			final ArrayList catgList = channelTransferRuleWebDAO.loadChannelTransferRuleVOList(con,sessionUser.getNetworkID(),sessionUser.getDomainID(),
							PretupsI.OPERATOR_TYPE_OPT);

			for (int i = 0, k = catgList.size(); i < k; i++) {

				final ChannelTransferRuleVO rulesVO = (ChannelTransferRuleVO) catgList.get(i);

				if (rulesVO.getToSeqNo() < sessionUser.getCategoryVO().getSequenceNumber()) {
					catgList.remove(i);
					i--;
					k--;
				}
			}
			channelTransferEnquiryModel.setCategoryList(catgList);
			final Date currentDate = new Date();
			final Calendar cal = BTSLDateUtil.getInstance();
			cal.setTime(currentDate);
			channelTransferEnquiryModel.setToDate(BTSLUtil.getDateStringFromDate(currentDate));
			channelTransferEnquiryModel.setToDateForUserCode(BTSLUtil.getDateStringFromDate(currentDate));
			final int maxDays = Integer.parseInt(Constants.getProperty("MAX_DAYLIMIT_DATERANGE"));
			channelTransferEnquiryModel.setFromDate(BTSLUtil.getDateStringFromDate(BTSLUtil.addDaysInUtilDate(currentDate, -maxDays)));
			channelTransferEnquiryModel.setFromDateForUserCode(BTSLUtil.getDateStringFromDate(BTSLUtil.addDaysInUtilDate(
							currentDate, -maxDays)));

			channelTransferEnquiryModel.setStatusList(LookupsCache.loadLookupDropDown(PretupsI.CHANNEL_TRANSFER_ORDER_STATUS,true));

			channelTransferEnquiryModel.setTransferCategoryList(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE_FOR_TRFRULES,true));

			final ArrayList prodTypList = new ArrayList(
					sessionUser.getAssociatedProductTypeList());
			for (int i = 0, j = prodTypList.size(); i < j; i++) {
				listValueVO = (ListValueVO) prodTypList.get(i);
				if (PretupsI.P2P_MODULE.equals(listValueVO.getValue())) {
					prodTypList.remove(i);
					i--;
					j--;
				}
			}

			channelTransferEnquiryModel.setProductsTypeList(prodTypList);

			channelTransferEnquiryModel.setTransferTypeList(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE, true));

			final ListValueVO trflistValueVO = new ListValueVO(PretupsI.ALL,PretupsI.ALL);

			channelTransferEnquiryModel.getTransferTypeList().add(0,trflistValueVO);

			if (prodTypList.size() == 1) {

				channelTransferEnquiryModel.setProductType(((ListValueVO) prodTypList.get(0)).getValue());

				channelTransferEnquiryModel.setProductTypeDesc(((ListValueVO) prodTypList.get(0)).getLabel());
			}

			if (prodTypList != null && prodTypList.isEmpty()) {
				model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("channeltransfer.enquiry.errormsg.noproducttype"));
			}

			model.addAttribute(MODEL_KEY, channelTransferEnquiryModel);
		} catch (BTSLBaseException e) {
			_log.error("channelUserEnquiry", "BTSLBaseException:e=" + e);
			_log.errorTrace(METHOD_NAME, e);

		} catch (ParseException e) {
			_log.error("channelUserEnquiry", "ParseException:e=" + e);
			_log.errorTrace(METHOD_NAME, e);

		} catch (Exception e) {
			_log.error("channelUserEnquiry", "Exception:e=" + e);
			_log.errorTrace(METHOD_NAME, e);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("O2CTransferEnquiryServiceImpl#channelUserEnquiry");
				mcomCon = null;
			}

		}
	}

	/**
	 * This method returns the user list. .
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ListValueVO> loadUserList(String userType, UserVO userVO,
			String categorycode, String userName, ChannelUserVO channelUserVO,
			ChannelUserVO sessionUserVO,
			ChannelTransferEnquiryModel channelTransferEnqModel)
			throws BTSLBaseException {

		ArrayList<ListValueVO> list = new ArrayList<ListValueVO>();
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {

			ChannelUserWebDAO channelUserWebDAO = null;

			channelUserWebDAO = new ChannelUserWebDAO();

			mcomCon = new MComConnection();
			try {
				con = mcomCon.getConnection();
			} catch (SQLException e) {
				_log.error("loadUserList","SQLException : ",e.getMessage());
			}

			if (userType != null && "parent".equals(userType)) {
				list = channelUserWebDAO.loadUsersForEnquiry(con,
						channelTransferEnqModel.getChannelOwnerCategory(),
						channelUserVO.getNetworkID(), userName, null,
						channelTransferEnqModel.getGeoDomainCode(),
						sessionUserVO.getUserID(), false);
			}
			if (!channelTransferEnqModel.isOwnerSame()&& BTSLUtil.isNullString(channelTransferEnqModel.getChannelOwnerCategoryUserID())) {
				throw new BTSLBaseException(this, "searchTransferUserPopup",
						"message.channeltransfer.search.chnlowneridnull",
						"popupsearch");

			} else if (userType != null && "child".equals(userType)) {

				String catg = null;
				if (channelTransferEnqModel.getCategoryCode() != null && channelTransferEnqModel.getCategoryCode().indexOf(":") > 0) {
					catg = channelTransferEnqModel.getCategoryCode().substring(
							channelTransferEnqModel.getCategoryCode().indexOf(":") + 1);
				} else {
					catg = categorycode;
				}
				list = channelUserWebDAO.loadUsersForEnquiry(con, catg, channelUserVO.getNetworkID(), userName,
								channelTransferEnqModel.getChannelOwnerCategoryUserID(),
								channelTransferEnqModel.getGeoDomainCode(),
								sessionUserVO.getUserID(), false);
			}
			channelTransferEnqModel.setUserList(list);
			channelTransferEnqModel.setListSize(list.size());
		} catch (BTSLBaseException e) {
			_log.error(METHOD_NAME, "Exception:e=" + e);
			_log.errorTrace(METHOD_NAME, e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("O2CTransferEnquiryServiceImpl#loadUserList");
				mcomCon = null;
			}
		}

		return list;

	}

	/**
	 * This method loads the transaction list on the basis of either Transaction
	 * number or mobile number or the searched user ID. Channel user can see the
	 * transactions of the users which are under his hierarchy. .
	 */
	@Override
	public boolean enquirySearch(ChannelUserVO channelUserVO, Model model,
			ChannelTransferEnquiryModel channelTransferEnqModelNew,
			HttpServletRequest request, BindingResult bindingResult)
			throws IOException, BTSLBaseException, ParseException,
			ValidatorException, SAXException {

		final String METHOD_NAME = "enquirySearch";
		String page_name;
		String transferID = null;
		String userCode = null;
		Date fromDate = null;
		Date toDate = null;
		String userID = null;
		String status = null;
		String transferTypeCode = null;
		String productType = null;
		String transferCategoryCode = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		channelUserWebDAO = new ChannelUserWebDAO();
		ChannelTransferEnquiryModel channelTransferEnqModelOrg = (ChannelTransferEnquiryModel) request
				.getSession().getAttribute("chanTransEnqMod");
		ChannelTransferEnquiryModel channelTransferEnqModel = new ChannelTransferEnquiryModel();
		
	     try{
	    	 BeanUtils.copyProperties(channelTransferEnqModel, channelTransferEnqModelOrg);
			if (request.getParameter("submitTrfID") != null) {
				channelTransferEnqModel.setTransferNum(channelTransferEnqModelNew.getTransferNum());
			} else if (request.getParameter("submitMSISDN") != null) {
				userCode = PretupsBL.getFilteredMSISDN(channelTransferEnqModelNew.getUserCode().trim());
				channelTransferEnqModel.setUserCode(userCode);

				if (channelTransferEnqModelNew.getFromDateForUserCode() != null) {
					channelTransferEnqModel.setFromDateForUserCode(channelTransferEnqModelNew.getFromDateForUserCode());
				}
				if (channelTransferEnqModelNew.getToDateForUserCode() != null) {
					channelTransferEnqModel.setToDateForUserCode(channelTransferEnqModelNew.getToDateForUserCode());
				}
				
				channelTransferEnqModel.setTrfCatForUserCode(channelTransferEnqModelNew.getTrfCatForUserCode());
			} else if (request.getParameter("submitUserSearch") != null) {

				channelTransferEnqModel.setProductType(channelTransferEnqModelNew.getProductType());

				channelTransferEnqModel.setTransferTypeCode(channelTransferEnqModelNew.getTransferTypeCode());

				channelTransferEnqModel.setTransferCategoryCode(channelTransferEnqModelNew.getTransferCategoryCode());
				channelTransferEnqModel.setStatusCode(channelTransferEnqModelNew.getStatusCode());
				if (channelTransferEnqModelNew.getFromDate() != null) {

					channelTransferEnqModel.setFromDate(channelTransferEnqModelNew.getFromDate());
				}
				if (channelTransferEnqModelNew.getToDate() != null) {

					channelTransferEnqModel.setToDate(channelTransferEnqModelNew.getToDate());
				}
				
				channelTransferEnqModel.setGeoDomainCodeDesc(channelTransferEnqModelNew
								.getGeoDomainCodeDesc());
				channelTransferEnqModel
						.setChannelDomain(channelTransferEnqModelNew
								.getChannelDomain());
				channelTransferEnqModel
						.setChannelCategoryUserName((channelTransferEnqModelNew
								.getChannelCategoryUserName()));
				channelTransferEnqModel
						.setCategoryCode(channelTransferEnqModelNew
								.getCategoryCode());
				
				
			}
			if (request.getParameter("submitTrfID") != null) {
				CommonValidator commonValidator = new CommonValidator(
						"configfiles/transfer/validator-O2CEnquiry.xml",
						channelTransferEnqModel, "O2CTrfID");
				Map<String, String> errorMessages = commonValidator
						.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				model.addAttribute("formNumber", "Panel-One");
				request.getSession().setAttribute("formNumber", "Panel-One");
			} else if (request.getParameter("submitMSISDN") != null) {
				CommonValidator commonValidator = new CommonValidator(
						"configfiles/transfer/validator-O2CEnquiry.xml",
						channelTransferEnqModel, "O2CMSISDN");
				Map<String, String> errorMessages = commonValidator
						.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				model.addAttribute("formNumber", "Panel-Two");
				request.getSession().setAttribute("formNumber", "Panel-Two");
			} else if (request.getParameter("submitUserSearch") != null) {
				CommonValidator commonValidator = new CommonValidator(
						"configfiles/transfer/validator-O2CEnquiry.xml",
						channelTransferEnqModel, "O2CUserSearch");
				Map<String, String> errorMessages = commonValidator
						.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				model.addAttribute("formNumber", "Panel-Three");
				request.getSession().setAttribute("formNumber", "Panel-Three");
			}
			if (bindingResult.hasFieldErrors()) {
				request.getSession().setAttribute("o2ctransferenquiry",
						channelTransferEnqModel);
				return false;
			}
			if(request.getParameter("submitMSISDN")!=null)
			{
				Date fromdate = BTSLUtil
					.getDateFromDateString(channelTransferEnqModel.getFromDateForUserCode());
					
					Date todate = BTSLUtil
							.getDateFromDateString(channelTransferEnqModel.getToDateForUserCode());
					
					int diff = BTSLUtil.getDifferenceInUtilDates(fromdate, todate);
			           if(diff > 20){
			           model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.transferenquiry.enquirysearchattribute.msg.datediffernce", new String[]{Constants.getProperty("DATE_DIFFERENCE")}));
			           request.getSession().setAttribute("o2ctransferenquiry",
								channelTransferEnqModel);    
			           return false;
			           }
			}
			if(request.getParameter("submitUserSearch")!=null)
			{ 
			           Date fromdate = BTSLUtil
								.getDateFromDateString(channelTransferEnqModel.getFromDate());
								
								Date todate = BTSLUtil
										.getDateFromDateString(channelTransferEnqModel.getToDate());
								
								int diff = BTSLUtil.getDifferenceInUtilDates(fromdate, todate);
						           if(diff > 20){
						           model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.transferenquiry.enquirysearchattribute.msg.datediffernce", new String[]{Constants.getProperty("DATE_DIFFERENCE")}));
						           request.getSession().setAttribute("o2ctransferenquiry",
											channelTransferEnqModel);    
						           return false;
						           }
			}
			if (channelTransferEnqModel.getChannelUserLoginedFlag()) {
				if (BTSLUtil.isNullString(channelTransferEnqModel
						.getTransferNum())
						&& BTSLUtil.isNullString(channelTransferEnqModel
								.getUserCode())) {
					if (channelTransferEnqModel.getProductsTypeList() != null) {
						channelTransferEnqModel.setProductTypeDesc((BTSLUtil
								.getOptionDesc(channelTransferEnqModel
										.getProductType(),
										channelTransferEnqModel
												.getProductsTypeList()))
								.getLabel());
					}
					if (channelTransferEnqModel.getCategoryList() != null) {
						ChannelTransferRuleVO channelTransferRuleVO = null;
						for (int i = 0, j = channelTransferEnqModel
								.getCategoryList().size(); i < j; i++) {
							channelTransferRuleVO = (ChannelTransferRuleVO) channelTransferEnqModel
									.getCategoryList().get(i);
							if (channelTransferRuleVO.getToCategory().equals(
									channelTransferEnqModel.getCategoryCode())) {
								channelTransferEnqModel
										.setCategoryCodeDesc(channelTransferRuleVO
												.getToCategoryDes());
								break;
							}
						}
					}
					if (channelTransferEnqModel.getTransferTypeList() != null) {
						channelTransferEnqModel.setTransferTypeValue(BTSLUtil
								.getOptionDesc(
										channelTransferEnqModel
												.getTransferTypeCode(),
										channelTransferEnqModel
												.getTransferTypeList())
								.getLabel());
					}
					channelTransferEnqModel.setTransferCategoryDesc(BTSLUtil
							.getOptionDesc(
									channelTransferEnqModel
											.getTransferCategoryCode(),
									channelTransferEnqModel
											.getTransferCategoryList())
							.getLabel());
					if (PretupsI.ALL.equals(channelTransferEnqModel
							.getStatusCode())) {
						channelTransferEnqModel.setStatusDesc("list.all");
					} else {
						channelTransferEnqModel
								.setStatusDesc(BTSLUtil
										.getOptionDesc(
												channelTransferEnqModel
														.getStatusCode(),
												channelTransferEnqModel
														.getStatusList())
										.getLabel());
					}
				} else if (BTSLUtil.isNullString(channelTransferEnqModel
						.getTransferNum())) {
					channelTransferEnqModel.setTransferCategoryDesc(BTSLUtil
							.getOptionDesc(
									channelTransferEnqModel
											.getTrfCatForUserCode(),
									channelTransferEnqModel
											.getTransferCategoryList())
							.getLabel());
				}
			}

			if (!BTSLUtil.isNullString(channelTransferEnqModel.getTransferNum())) {
				mcomCon = new MComConnection();
				con=mcomCon.getConnection();
			} else {
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_RPT_DB)).booleanValue()
						&& PretupsI.RESET_CHECKBOX
								.equals(channelTransferEnqModel
										.getCurrentDateFlagForUserCode())) {
					mcomCon = new MComReportDBConnection();
					con=mcomCon.getConnection();
				} else {
					mcomCon = new MComConnection();
					con=mcomCon.getConnection();
				}
			}
			final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();

			if (BTSLUtil.isNullString(channelTransferEnqModel.getTransferNum())
					&& BTSLUtil.isNullString(channelTransferEnqModel
							.getUserCode())) {
				String catg = null;
				ListValueVO listValueVO = null;
				String user = null;

				if (channelTransferEnqModel.getChannelUserLoginedFlag()) {
					catg = channelTransferEnqModel.getCategoryCode();
					user = channelTransferEnqModel
							.getChannelCategoryUserName();
					request.getSession().setAttribute("userName", user);
					String[] parts = user.split("\\(");
					String userName = parts[0]; 
					channelTransferEnqModel.setChannelCategoryUserName(userName);
					String a = parts[1];
					String[] w1=a.split("\\)");
					channelTransferEnqModel.setUserID(w1[0]);
					if (!BTSLUtil.isNullString(userName)) {
						userName = "%" + userName + "%";
					}
					
					
					final ArrayList userList = channelUserWebDAO
							.loadUsersForEnquiry(con, catg, channelUserVO
									.getNetworkID(), userName,
									channelTransferEnqModel
											.getChannelOwnerCategoryUserID(),
									channelTransferEnqModel.getGeoDomainCode(),
									channelUserVO.getUserID(), false);
					if (userList.size() == 1) {
						listValueVO = (ListValueVO) userList.get(0);
						channelTransferEnqModel.setUserID(listValueVO
								.getValue());
						channelTransferEnqModel
								.setChannelCategoryUserName(listValueVO
										.getLabel());
						channelTransferEnqModel
								.setChannelCategoryUserID(listValueVO
										.getValue());
					} else if (userList.size() > 1) {
						boolean isExist = false;
						
						if (!BTSLUtil.isNullString(channelTransferEnqModel
								.getUserID())) {
							for (int i = 0, k = userList.size(); i < k; i++) {
								listValueVO = (ListValueVO) userList.get(i);
								if (listValueVO.getValue().equals(
										channelTransferEnqModel.getUserID())
										&& (channelTransferEnqModel
												.getChannelCategoryUserName()
												.compareTo(
														listValueVO.getLabel()) == 0)) {
									channelTransferEnqModel
											.setUserID(listValueVO.getValue());
									channelTransferEnqModel
											.setChannelCategoryUserName(listValueVO
													.getLabel());
									channelTransferEnqModel
											.setChannelCategoryUserID(listValueVO
													.getValue());
									isExist = true;
									break;
								}
							}
						} else {
							ListValueVO listValueVONext = null;
							for (int i = 0, k = userList.size(); i < k; i++) {
								listValueVO = (ListValueVO) userList.get(i);
								if (channelTransferEnqModel
										.getChannelCategoryUserName()
										.compareTo(listValueVO.getLabel()) == 0) {
									if (((i + 1) < k)) {
										listValueVONext = (ListValueVO) userList
												.get(i + 1);
										if (channelTransferEnqModel
												.getChannelCategoryUserName()
												.compareTo(
														listValueVONext
																.getLabel()) == 0) {
											isExist = false;
											break;
										}
										channelTransferEnqModel
												.setUserID(listValueVO
														.getValue());
										channelTransferEnqModel
												.setChannelCategoryUserName(listValueVO
														.getLabel());
										channelTransferEnqModel
												.setChannelCategoryUserID(listValueVO
														.getValue());
										isExist = true;
										break;
									}
									channelTransferEnqModel
											.setUserID(listValueVO.getValue());
									channelTransferEnqModel
											.setChannelCategoryUserName(listValueVO
													.getLabel());
									channelTransferEnqModel
											.setChannelCategoryUserID(listValueVO
													.getValue());
									isExist = true;
									break;
								}
							}
						}
						if (!isExist) {
							final String arr[] = { channelTransferEnqModel
									.getChannelCategoryUserName() };

							model.addAttribute(
									FAIL_KEY,
									PretupsRestUtil
											.getMessageString(
													"message.channeltransfer.usermorethanoneexist.msg",
													arr));
							channelTransferEnqModel.setChannelCategoryUserName(user);
							request.getSession().setAttribute("o2ctransferenquiry",channelTransferEnqModel);
							
							return false;
						}
					} else {
						final String arr[] = { channelTransferEnqModel
								.getChannelCategoryUserName() };

						model.addAttribute(
								FAIL_KEY,
								PretupsRestUtil
										.getMessageString("message.channeltransfer.usernotfound.msg",arr));
						channelTransferEnqModel.setChannelCategoryUserName(user);
						request.getSession().setAttribute("o2ctransferenquiry",channelTransferEnqModel);
						return false;

					}
				} else {

					if (!channelTransferEnqModel.isOwnerSame()) {
						user = channelTransferEnqModel
								.getChannelOwnerCategoryUserName();
						
						String[] parts = user.split("\\(");
						String userName = parts[0];
						channelTransferEnqModel.setChannelCategoryUserName(userName);
						String a = parts[1];
						String[] w1=a.split("\\)");
						channelTransferEnqModel.setUserID(w1[0]);
						if (!BTSLUtil.isNullString(userName)) {
							userName = "%" + userName + "%";
						}

						final ArrayList userList = channelUserWebDAO
								.loadUsersForEnquiry(con,
										channelTransferEnqModel
												.getChannelOwnerCategory(),
										channelUserVO.getNetworkID(), userName,
										null, channelTransferEnqModel
												.getGeoDomainCode(),
										channelUserVO.getUserID(), false);

						if (userList.size() == 1) {
							listValueVO = (ListValueVO) userList.get(0);
							channelTransferEnqModel
									.setChannelOwnerCategoryUserName(listValueVO
											.getLabel());
							channelTransferEnqModel
									.setChannelOwnerCategoryUserID(listValueVO
											.getValue());
						} else if (userList.size() > 1) {
							boolean isExist = false;
							if (!BTSLUtil.isNullString(channelTransferEnqModel
									.getChannelOwnerCategoryUserID())) {
								for (int i = 0, k = userList.size(); i < k; i++) {
									listValueVO = (ListValueVO) userList.get(i);
									if (listValueVO
											.getValue()
											.equals(channelTransferEnqModel
													.getChannelOwnerCategoryUserID())
											&& (channelTransferEnqModel
													.getChannelOwnerCategoryUserName()
													.compareTo(
															listValueVO
																	.getLabel()) == 0)) {
										channelTransferEnqModel
												.setChannelOwnerCategoryUserName(listValueVO
														.getLabel());
										channelTransferEnqModel
												.setChannelOwnerCategoryUserID(listValueVO
														.getValue());
										isExist = true;
										break;
									}
								}
							} else {
								ListValueVO listValueNextVO = null;
								for (int i = 0, k = userList.size(); i < k; i++) {
									listValueVO = (ListValueVO) userList.get(i);
									if (channelTransferEnqModel
											.getChannelOwnerCategoryUserName()
											.compareTo(listValueVO.getLabel()) == 0) {
										if (((i + 1) < k)) {
											listValueNextVO = (ListValueVO) userList
													.get(i + 1);
											if (channelTransferEnqModel
													.getChannelOwnerCategoryUserName()
													.compareTo(
															listValueNextVO
																	.getLabel()) == 0) {
												isExist = false;
												break;
											}
											channelTransferEnqModel
													.setChannelOwnerCategoryUserName(listValueVO
															.getLabel());
											channelTransferEnqModel
													.setChannelOwnerCategoryUserID(listValueVO
															.getValue());
											isExist = true;
											break;
										}
										channelTransferEnqModel
												.setChannelOwnerCategoryUserName(listValueVO
														.getLabel());
										channelTransferEnqModel
												.setChannelOwnerCategoryUserID(listValueVO
														.getValue());
										isExist = true;
										break;
									}
								}
							}
							if (!isExist) {
								final String arr[] = { channelTransferEnqModel
										.getChannelOwnerCategoryUserName() };

								model.addAttribute(
										FAIL_KEY,
										PretupsRestUtil
												.getMessageString(
														"message.channeltransfer.usermorethanoneexist.msg",
														arr));
								channelTransferEnqModel.setChannelCategoryUserName(user);
								request.getSession().setAttribute("o2ctransferenquiry",
										channelTransferEnqModel);
								return false;
							}
						} else {
							final String arr[] = { channelTransferEnqModel
									.getChannelOwnerCategoryUserName() };
							model.addAttribute(
									FAIL_KEY,
									PretupsRestUtil
											.getMessageString(
													"message.channeltransfer.usernotfound.msg",
													arr));
							channelTransferEnqModel.setChannelCategoryUserName(user);
							request.getSession().setAttribute("o2ctransferenquiry",
									channelTransferEnqModel);
							return false;
						}
					}

					if (channelTransferEnqModel.getCategoryCode() != null
							&& channelTransferEnqModel.getCategoryCode()
									.indexOf(":") > 0) {
						catg = channelTransferEnqModel
								.getCategoryCode()
								.substring(
										channelTransferEnqModel
												.getCategoryCode().indexOf(":") + 1);
					}
					user = channelTransferEnqModel
							.getChannelCategoryUserName();
					
					String[] parts = user.split("\\(");
					String userName = parts[0]; 
					channelTransferEnqModel.setChannelCategoryUserName(userName);
					String a = parts[1];
					String[] w1=a.split("\\)");
					channelTransferEnqModel.setUserID(w1[0]);
					if (!BTSLUtil.isNullString(userName)) {
						userName = "%" + userName + "%";
					}
					final ArrayList userList = channelUserWebDAO
							.loadUsersForEnquiry(con, catg, channelUserVO
									.getNetworkID(), userName,
									channelTransferEnqModel
											.getChannelOwnerCategoryUserID(),
									channelTransferEnqModel.getGeoDomainCode(),
									channelUserVO.getUserID(), false);
					if (userList.size() == 1) {
						listValueVO = (ListValueVO) userList.get(0);
						channelTransferEnqModel.setUserID(listValueVO
								.getValue());
						channelTransferEnqModel
								.setChannelCategoryUserName(listValueVO
										.getLabel());
						channelTransferEnqModel
								.setChannelCategoryUserID(listValueVO
										.getValue());
					} else if (userList.size() > 1) {
						boolean isExist = false;
						if (!BTSLUtil.isNullString(channelTransferEnqModel
								.getUserID())) {
							for (int i = 0, k = userList.size(); i < k; i++) {
								listValueVO = (ListValueVO) userList.get(i);
								if (listValueVO.getValue().equals(
										channelTransferEnqModel.getUserID())
										&& (channelTransferEnqModel
												.getChannelCategoryUserName()
												.compareTo(
														listValueVO.getLabel()) == 0)) {
									channelTransferEnqModel
											.setUserID(listValueVO.getValue());
									channelTransferEnqModel
											.setChannelCategoryUserName(listValueVO
													.getLabel());
									channelTransferEnqModel
											.setChannelCategoryUserID(listValueVO
													.getValue());
									isExist = true;
									break;
								}
							}
						} else {
							ListValueVO listValueVONext = null;
							for (int i = 0, k = userList.size(); i < k; i++) {
								listValueVO = (ListValueVO) userList.get(i);
								if (channelTransferEnqModel
										.getChannelCategoryUserName()
										.compareTo(listValueVO.getLabel()) == 0) {
									if (((i + 1) < k)) {
										listValueVONext = (ListValueVO) userList
												.get(i + 1);
										if (channelTransferEnqModel
												.getChannelCategoryUserName()
												.compareTo(
														listValueVONext
																.getLabel()) == 0) {
											isExist = false;
											break;
										}
										channelTransferEnqModel
												.setUserID(listValueVO
														.getValue());
										channelTransferEnqModel
												.setChannelCategoryUserName(listValueVO
														.getLabel());
										channelTransferEnqModel
												.setChannelCategoryUserID(listValueVO
														.getValue());
										isExist = true;
										break;
									}
									channelTransferEnqModel
											.setUserID(listValueVO.getValue());
									channelTransferEnqModel
											.setChannelCategoryUserName(listValueVO
													.getLabel());
									channelTransferEnqModel
											.setChannelCategoryUserID(listValueVO
													.getValue());
									isExist = true;
									break;
								}
							}
						}
						if (!isExist) {
							final String arr[] = { channelTransferEnqModel
									.getChannelCategoryUserName() };
							model.addAttribute(
									FAIL_KEY,
									PretupsRestUtil
											.getMessageString(
													"message.channeltransfer.usermorethanoneexist.msg",
													arr));
							channelTransferEnqModel.setChannelCategoryUserName(user);
							request.getSession().setAttribute("o2ctransferenquiry",
									channelTransferEnqModel);
							return false;

						}
					} else {
						final String arr[] = { channelTransferEnqModel
								.getChannelCategoryUserName() };
						model.addAttribute(
								FAIL_KEY,
								PretupsRestUtil
										.getMessageString(
												"message.channeltransfer.usernotfound.msg",
												arr));
						channelTransferEnqModel.setChannelCategoryUserName(user);
						request.getSession().setAttribute("o2ctransferenquiry",
								channelTransferEnqModel);
						return false;

					}
				}
				if (channelTransferEnqModel.getFromDate() != null) {
					fromDate = BTSLUtil
							.getDateFromDateString(channelTransferEnqModel
									.getFromDate());
				}
				if (channelTransferEnqModel.getToDate() != null) {
					toDate = BTSLUtil
							.getDateFromDateString(channelTransferEnqModel
									.getToDate());
				}
				/*int diff = BTSLUtil.getDifferenceInUtilDates(fromDate, toDate);
		           if(diff > 20){
		           model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.transferenquiry.enquirysearchattribute.msg.datediffernce", new String[]{Constants.getProperty("DATE_DIFFERENCE")}));
		               return false;
		           }*/

				channelTransferEnqModel.setTransferNum(null);
				channelTransferEnqModel.setUserCode(null);
				userID = channelTransferEnqModel.getChannelCategoryUserID();
				status = channelTransferEnqModel.getStatusCode();
				transferTypeCode = channelTransferEnqModel
						.getTransferTypeCode();
				productType = channelTransferEnqModel.getProductType();
				transferCategoryCode = channelTransferEnqModel
						.getTransferCategoryCode();
				//channelTransferEnqModel.setChannelCategoryUserName(user);

			} else if (!BTSLUtil.isNullString(channelTransferEnqModel
					.getTransferNum())) {
				transferID = channelTransferEnqModel.getTransferNum().trim();
			} else {

				userCode = PretupsBL.getFilteredMSISDN(channelTransferEnqModel
						.getUserCode().trim());

				if (channelTransferEnqModel.getFromDateForUserCode() != null) {
					fromDate = BTSLUtil
							.getDateFromDateString(channelTransferEnqModel
									.getFromDateForUserCode());
				}
				if (channelTransferEnqModel.getToDateForUserCode() != null) {
					toDate = BTSLUtil
							.getDateFromDateString(channelTransferEnqModel
									.getToDateForUserCode());
				}
				channelTransferEnqModel.setTransferNum(null);
				transferCategoryCode = channelTransferEnqModel
						.getTrfCatForUserCode();
			}

			final ArrayList transferList = channelTransferDAO
					.loadEnquiryChannelTransfersList(con, transferID, userID,
							fromDate, toDate, status, transferTypeCode,
							productType, transferCategoryCode, userCode);
			channelTransferEnqModel.setTransferList(transferList);
			
			BTSLMessages messages = null;

			if (transferList == null || transferList.isEmpty()) {
				channelTransferEnqModel.setSearchListSize(0);
				if (!BTSLUtil.isNullString(channelTransferEnqModel
						.getTransferNum())) {
					if (channelTransferEnqModel.getChannelUserLoginedFlag()) {
						model.addAttribute(
								FAIL_KEY,
								PretupsRestUtil
										.getMessageString("channeltransfer.enquirytransferlist.label.nodata"));
					} else {
						model.addAttribute(
								FAIL_KEY,
								PretupsRestUtil
										.getMessageString("channeltransfer.enquirytransferlist.label.nodata"));
					}
					request.getSession().setAttribute("o2ctransferenquiry",
							channelTransferEnqModel);
					return false;
				}
				
				/*page_name = "channeltransfer/O2CenquiryTransferList";
				request.getSession().setAttribute("RETURN", page_name);
				model.addAttribute("O2CTransferModel", channelTransferEnqModel);
				request.getSession().setAttribute("o2ctransferenquiry",
						channelTransferEnqModel);
				return true;*/
				model.addAttribute(
						FAIL_KEY,
						PretupsRestUtil
								.getMessageString("channeltransfer.enquirytransferlist.label.nodata"));
				request.getSession().setAttribute("o2ctransferenquiry",
						channelTransferEnqModel);
				return false;
				
			}
			ArrayList hierarchyList = null;
			ChannelTransferVO transferVO = null;

			if (channelUserVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
				userID = null;
				if (PretupsI.CATEGORY_TYPE_AGENT.equals(channelUserVO
						.getCategoryVO().getCategoryType())
						&& PretupsI.NO.equals(channelUserVO.getCategoryVO()
								.getHierarchyAllowed())) {
					userID = channelUserVO.getParentID();
				} else {
					userID = channelUserVO.getUserID();
					
				}
				
				hierarchyList = channelUserWebDAO.loadChannelUserHierarchy(con,
						userID, false);
				if (hierarchyList == null || hierarchyList.isEmpty()) {
					if (_log.isDebugEnabled()) {
						_log.debug("enquirySearch",
								"Logged in user has no child user so there would be no transactions");
					}
					throw new BTSLBaseException(this, "enquirySearch",
							"o2cenquiry.transferlist.msg.nohierarchy",
							"backusersearch");
				}
			} else if (channelUserVO.getUserType().equals(
					PretupsI.OPERATOR_USER_TYPE)
					&& (!BTSLUtil.isNullString(channelTransferEnqModel
							.getTransferNum()) || !BTSLUtil
							.isNullString(channelTransferEnqModel.getUserCode()))) {
				transferVO = (ChannelTransferVO) transferList.get(0);
				channelTransferEnqModel.setNetworkCode(transferVO
						.getNetworkCode());
				
				if (!BTSLUtil.isNullString(channelTransferEnqModel
						.getSessionDomainCode())) {
					if (!transferVO.getDomainCode().equals(
							channelTransferEnqModel.getSessionDomainCode())) {
						if (!BTSLUtil.isNullString(channelTransferEnqModel
								.getTransferNum())) {
							model.addAttribute(
									FAIL_KEY,
									PretupsRestUtil
											.getMessageString(
													"o2cenquiry.viewo2ctransfers.msg.usernotindomain",
													new String[] { channelTransferEnqModel
															.getTransferNum() }));
							
						} else if (!BTSLUtil
								.isNullString(channelTransferEnqModel
										.getUserCode())) {
							model.addAttribute(
									FAIL_KEY,
									PretupsRestUtil
											.getMessageString(
													"o2cenquiry.viewo2ctransfers.msg.usernotindomainbyucode",
													new String[] { channelTransferEnqModel
															.getUserCode() }));
													}
						request.getSession().setAttribute("o2ctransferenquiry",
								channelTransferEnqModel);
						return false;
					}
				} else {
					ListValueVO listValueVO = null;
					boolean domainfound = false;
					final ArrayList domainList = channelTransferEnqModel
							.getChannelDomainList();
					for (int i = 0, j = domainList.size(); i < j; i++) {
						listValueVO = (ListValueVO) domainList.get(i);
						if (transferVO.getDomainCode().equals(
								listValueVO.getValue())) {
							domainfound = true;
							break;
						}
					}
					if (!domainfound) {

						if (!BTSLUtil.isNullString(channelTransferEnqModel
								.getTransferNum())) {
							model.addAttribute(
									FAIL_KEY,
									PretupsRestUtil
											.getMessageString(
													"o2cenquiry.viewo2ctransfers.msg.usernotindomain",
													new String[] { channelTransferEnqModel
															.getTransferNum() }));
							
						} else if (!BTSLUtil
								.isNullString(channelTransferEnqModel
										.getUserCode())) {
							model.addAttribute(
									FAIL_KEY,
									PretupsRestUtil
											.getMessageString(
													"o2cenquiry.viewo2ctransfers.msg.usernotindomainbyucode",
													new String[] { channelTransferEnqModel
															.getUserCode() }));
							

						}
						request.getSession().setAttribute("o2ctransferenquiry",
								channelTransferEnqModel);
						return false;
					}
				}
				final GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
				if (!geographicalDomainDAO.isGeoDomainExistInHierarchy(con,
						transferVO.getGraphicalDomainCode(),
						channelUserVO.getUserID())) {
					if (!BTSLUtil.isNullString(channelTransferEnqModel
							.getTransferNum())) {
						model.addAttribute(
								FAIL_KEY,
								PretupsRestUtil
										.getMessageString(
												"o2cenquiry.viewo2ctransfers.msg.usernotdowngeogrphy",
												new String[] { channelTransferEnqModel
														.getTransferNum() }));
						
					} else if (!BTSLUtil.isNullString(channelTransferEnqModel
							.getUserCode())) {
						model.addAttribute(
								FAIL_KEY,
								PretupsRestUtil
										.getMessageString(
												"o2cenquiry.viewo2ctransfers.msg.usernotdowngeogrphybyucode",
												new String[] { channelTransferEnqModel
														.getUserCode() }));
						
					}
					request.getSession().setAttribute("o2ctransferenquiry",
							channelTransferEnqModel);
					return false;
				}
			}
			boolean isMatched = false;
			if ((!BTSLUtil.isNullString(channelTransferEnqModel
					.getTransferNum()) || !BTSLUtil
					.isNullString(channelTransferEnqModel.getUserCode()))
					&& hierarchyList != null && !hierarchyList.isEmpty()) {
				for (int m = 0, n = transferList.size(); m < n; m++) {
					transferVO = (ChannelTransferVO) transferList.get(m);
					isMatched = false;
					for (int i = 0, j = hierarchyList.size(); i < j; i++) {
						channelUserVO = (ChannelUserVO) hierarchyList.get(i);
						if (channelUserVO.getUserID().equals(
								transferVO.getToUserID())
								|| channelUserVO.getUserID().equals(
										transferVO.getFromUserID())) {
							isMatched = true;
							break;
						}
					}
					if (!isMatched) {
						model.addAttribute(
								FAIL_KEY,
								PretupsRestUtil
										.getMessageString("o2cenquiry.viewo2ctransfers.msg.notauthorize"));
						request.getSession().setAttribute("o2ctransferenquiry",
								channelTransferEnqModel);
						return false;
					}
				}
			}
			channelTransferEnqModel.setSelectedIndex("0");
			channelTransferEnqModel.setSearchListSize(transferList.size());
			page_name = "channeltransfer/O2CenquiryTransferList";
			request.getSession().setAttribute("RETURN", page_name);
			request.getSession().setAttribute("o2ctransferenquiry",
					channelTransferEnqModel);
			boolean bool = true;
			if (transferList.size() == 1) {
				enquiryDetail(channelTransferEnqModel);
				page_name = "channeltransfer/O2CenquiryTransferView";
				request.getSession().setAttribute("RETURN", page_name);
				model.addAttribute("O2CTransferModel", channelTransferEnqModel);
				request.getSession().setAttribute("o2ctransferenquiry",
						channelTransferEnqModel);
				return true;

			}
			
		} catch (Exception e) {
			_log.error("enquirySearch", "Exception:e=" + e);
			_log.errorTrace(METHOD_NAME, e);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("O2CTransferEnquiryServiceImpl#enquirySearch");
				mcomCon = null;
			}

		}
		model.addAttribute("O2CTransferModel", channelTransferEnqModel);
		return true;
	}

	/**
	 * This method is used to calculate total of all the fields.
	 * 
	 * @param channelTransferEnquiryModel
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void enquiryDetail(
			ChannelTransferEnquiryModel channelTransferEnquiryModel)
			throws BTSLBaseException, ParseException {

		final String METHOD_NAME = "enquiryDetail";
		if (_log.isDebugEnabled()) {
			_log.debug("enquiryDetail", "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			final int index = Integer.parseInt(channelTransferEnquiryModel
					.getSelectedIndex());
			final ChannelTransferVO channelTransferVO = (ChannelTransferVO) channelTransferEnquiryModel
					.getTransferList().get(index);
			final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			channelTransferDAO.loadChannelTransfersVO(con, channelTransferVO);
			this.constructFormFromVO(channelTransferEnquiryModel,
					channelTransferVO);
			final ArrayList itemsList = ChannelTransferBL
					.loadChannelTransferItemsWithBalances(con,
							channelTransferVO.getTransferID(),
							channelTransferVO.getNetworkCode(),
							channelTransferVO.getNetworkCodeFor(),
							channelTransferVO.getToUserID());
			channelTransferEnquiryModel .setNetworkCodecheck((boolean) (PreferenceCache .getNetworkPrefrencesValue( PreferenceI.TARGET_BASED_BASE_COMMISSION, channelTransferVO .getNetworkCode())));
			long totTax1 = 0L, totTax2 = 0L, totTax3 = 0L, totReqQty = 0L, totStock = 0L, totComm = 0L, totMRP = 0L;
			long totOtf = 0L;

			double firAppQty = 0.00, secAppQty = 0.00, thrAppQty = 0.00;
			long mrpAmt = 0L;
			long senderPreStock = 0L;// BUG FIX by AshishT for Mobinil5.7
			long senderPostStock = 0L;// BUG FIX by AshishT for Mobinil5.7
			long receiverPreStock = 0L;// BUG FIX by AshishT for Mobinil5.7
			long receiverPostStock = 0L;// BUG FIX by AshishT for Mobinil5.7
			if (itemsList != null && !itemsList.isEmpty()) {
				ChannelTransferItemsVO channelTransferItemsVO = null;
				for (int i = 0, j = itemsList.size(); i < j; i++) {
					channelTransferItemsVO = (ChannelTransferItemsVO) itemsList
							.get(i);
					mrpAmt = channelTransferItemsVO.getApprovedQuantity()
							* Long.parseLong(PretupsBL
									.getDisplayAmount(channelTransferItemsVO
											.getUnitValue()));
					channelTransferItemsVO.setProductMrpStr(PretupsBL
							.getDisplayAmount(mrpAmt));
					totTax1 += channelTransferItemsVO.getTax1Value();
					totTax2 += channelTransferItemsVO.getTax2Value();
					totTax3 += channelTransferItemsVO.getTax3Value();
					totComm += channelTransferItemsVO.getCommValue();
					totMRP += mrpAmt;
					totReqQty += channelTransferItemsVO.getRequiredQuantity();
					totStock += channelTransferItemsVO.getWalletbalance();
					totOtf += channelTransferItemsVO.getOtfAmount();
					if (!BTSLUtil.isNullString(channelTransferItemsVO
							.getFirstApprovedQuantity())) {
						firAppQty += Double.parseDouble(channelTransferItemsVO
								.getFirstApprovedQuantity());
					} else {
						channelTransferItemsVO.setFirstApprovedQuantity("NA");
					}
					if (!BTSLUtil.isNullString(channelTransferItemsVO
							.getSecondApprovedQuantity())) {
						secAppQty += Double.parseDouble(channelTransferItemsVO
								.getSecondApprovedQuantity());
					} else {
						channelTransferItemsVO.setSecondApprovedQuantity("NA");
					}
					if (!BTSLUtil.isNullString(channelTransferItemsVO
							.getThirdApprovedQuantity())) {
						thrAppQty += Double.parseDouble(channelTransferItemsVO
								.getThirdApprovedQuantity());
					} else {
						channelTransferItemsVO.setThirdApprovedQuantity("NA");
					}
					senderPreStock = channelTransferItemsVO
							.getSenderPreviousStock();// BUG FIX by AshishT for
														// Mobinil5.7
					senderPostStock = channelTransferItemsVO
							.getSenderPostStock(); // BUG FIX by AshishT for
													// Mobinil5.7
					receiverPreStock = channelTransferItemsVO
							.getReceiverPreviousStock();// BUG FIX by AshishT
														// for Mobinil5.7
					receiverPostStock = channelTransferItemsVO
							.getReceiverPostStock();// BUG FIX by AshishT for
													// Mobinil5.7

				}
			}
			channelTransferEnquiryModel.setSenderPreviousStock(PretupsBL.getDisplayAmount(senderPreStock));
			 // BUG FIX by AshishT for Mobinil5.7
			 channelTransferEnquiryModel.setSenderPostStock(PretupsBL.getDisplayAmount(senderPostStock));//
			 //BUG FIX by AshishT for Mobinil5.7
			 channelTransferEnquiryModel.setReceiverPreviousStock(PretupsBL.getDisplayAmount(receiverPreStock));//
			 //BUG FIX by AshishT for Mobinil5.7
			 channelTransferEnquiryModel.setReceiverPostStock(PretupsBL.getDisplayAmount(receiverPostStock));//
			// BUG FIX by AshishT for Mobinil5.7
			channelTransferEnquiryModel.setTotalComm(PretupsBL
					.getDisplayAmount(totComm));
			channelTransferEnquiryModel.setTotalTax1(PretupsBL
					.getDisplayAmount(totTax1));
			channelTransferEnquiryModel.setTotalTax2(PretupsBL
					.getDisplayAmount(totTax2));
			channelTransferEnquiryModel.setTotalTax3(PretupsBL
					.getDisplayAmount(totTax3));
			channelTransferEnquiryModel.setTotalStock(PretupsBL
					.getDisplayAmount(totStock));
			channelTransferEnquiryModel.setTotalReqQty(PretupsBL
					.getDisplayAmount(totReqQty));
			channelTransferEnquiryModel.setTotalMRP(PretupsBL
					.getDisplayAmount(totMRP));
			channelTransferEnquiryModel.setTotalOtf(PretupsBL
					.getDisplayAmount(totOtf));

			final int paymentDetailsLevel = (Integer) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.PAYMENTDETAILSMANDATE_O2C);

			if (paymentDetailsLevel >= 0 && paymentDetailsLevel <= 3) {
				channelTransferEnquiryModel.setShowPaymentDetails(true);
			} else {
				channelTransferEnquiryModel.setShowPaymentDetails(false);
			}

			if (Math.abs(firAppQty-0)>EPSILON) {
				channelTransferEnquiryModel
						.setFirstLevelApprovedQuantity(new DecimalFormat(
								"#############.##").format(firAppQty));
			} else {
				channelTransferEnquiryModel.setFirstLevelApprovedQuantity("NA");
			}
			if (Math.abs(secAppQty-0)>EPSILON) {
				channelTransferEnquiryModel
						.setSecondLevelApprovedQuantity(new DecimalFormat(
								"#############.##").format(secAppQty));
			} else {
				channelTransferEnquiryModel
						.setSecondLevelApprovedQuantity("NA");
			}
			if (Math.abs(thrAppQty-0)>EPSILON) {
				channelTransferEnquiryModel
						.setThirdLevelApprovedQuantity(new DecimalFormat(
								"#############.##").format(thrAppQty));
			} else {
				channelTransferEnquiryModel.setThirdLevelApprovedQuantity("NA");
			}
			channelTransferEnquiryModel.setTransferItemsList(itemsList);
		} catch (BTSLBaseException e) {
			_log.error("enquiryDetail", "BTSLBaseException:e=" + e);
			_log.errorTrace(METHOD_NAME, e);

		} catch (ParseException e) {
			_log.error("enquiryDetail", "ParseException:e=" + e);
			_log.errorTrace(METHOD_NAME, e);

		} catch (Exception e) {
			_log.error("enquiryDetail", "Exception:e=" + e);
			_log.errorTrace(METHOD_NAME, e);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("O2CTransferEnquiryServiceImpl#enquiryDetail");
				mcomCon = null;
			}

		}

	}

	/**
	 * This method is used to construct a formbean from the VO. The formbean
	 * contains the information which we have to dispaly on the jsp .
	 * 
	 * @param channelTransferEnquiryModel
	 * @param channelTransferVO
	 * @throws ParseException
	 */
	public void constructFormFromVO(
			ChannelTransferEnquiryModel channelTransferEnquiryModel,
			ChannelTransferVO channelTransferVO) throws ParseException,
			BTSLBaseException {

		channelTransferEnquiryModel.setTransferNumberDispaly(channelTransferVO
				.getTransferID());
		channelTransferEnquiryModel.setUserName(channelTransferVO
				.getToUserName());
		channelTransferEnquiryModel.setDomainName(channelTransferVO
				.getDomainCodeDesc());
		// channelTransferEnquiryModel.setGeographicDomainName(p_channelTransferVO.getGrphDomainCodeDesc());
		channelTransferEnquiryModel.setGeoDomainNameForUser(channelTransferVO
				.getGrphDomainCodeDesc());
		channelTransferEnquiryModel.setStatusDetail(channelTransferVO
				.getStatusDesc());
		channelTransferEnquiryModel
				.setStatusCheck(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE
						.equalsIgnoreCase(channelTransferEnquiryModel
								.getStatusDetail()));
		channelTransferEnquiryModel.setPrimaryTxnNum(channelTransferVO
				.getUserMsisdn());
		// channelTransferEnquiryModel.setCategoryName(p_channelTransferVO.getReceiverCategoryDesc());
		channelTransferEnquiryModel.setGardeDesc(channelTransferVO
				.getReceiverGradeCodeDesc());
		channelTransferEnquiryModel.setErpCode(channelTransferVO.getErpNum());
		channelTransferEnquiryModel.setProductType(channelTransferVO
				.getProductType());
		channelTransferEnquiryModel.setProductTypeDesc(BTSLUtil.getOptionDesc(
				channelTransferEnquiryModel.getProductType(),
				channelTransferEnquiryModel.getProductsTypeList()).getLabel());
		channelTransferEnquiryModel.setCommissionProfileName(channelTransferVO
				.getCommProfileName());
		if (channelTransferVO.getExternalTxnDate() != null) {
			channelTransferEnquiryModel.setExternalTxnDate(BTSLUtil
					.getDateStringFromDate(channelTransferVO
							.getExternalTxnDate()));
		} else {
			channelTransferEnquiryModel.setExternalTxnDate(null);
		}
		channelTransferEnquiryModel.setExternalTxnNum(channelTransferVO
				.getExternalTxnNum());
		channelTransferEnquiryModel.setRefrenceNum(channelTransferVO
				.getReferenceNum());
		channelTransferEnquiryModel.setRemarks(channelTransferVO
				.getChannelRemarks());

		if (!BTSLUtil.isNullString(channelTransferVO.getPayInstrumentType())) {
			channelTransferEnquiryModel
					.setPaymentInstrumentName(((LookupsVO) LookupsCache
							.getObject(PretupsI.PAYMENT_INSTRUMENT_TYPE,
									channelTransferVO.getPayInstrumentType()))
							.getLookupName());
		} else {
			channelTransferEnquiryModel.setPaymentInstrumentName(BTSLUtil
					.NullToString(channelTransferVO.getPayInstrumentType()));
		}

		channelTransferEnquiryModel.setPaymentInstNum(channelTransferVO
				.getPayInstrumentNum());
		if (channelTransferVO.getPayInstrumentDate() != null) {
			channelTransferEnquiryModel.setPaymentInstrumentDate(BTSLUtil
					.getDateStringFromDate(channelTransferVO
							.getPayInstrumentDate()));
		}
		channelTransferEnquiryModel.setPaymentInstrumentAmt(PretupsBL
				.getDisplayAmount(channelTransferVO.getNetPayableAmount()));
		if (channelTransferVO.getTransferDate() != null) {
			channelTransferEnquiryModel
					.setTransferDate(BTSLUtil
							.getDateStringFromDate(channelTransferVO
									.getTransferDate()));
		}
		channelTransferEnquiryModel.setPayableAmount(PretupsBL
				.getDisplayAmount(channelTransferVO.getPayableAmount()));
		channelTransferEnquiryModel.setNetPayableAmount(PretupsBL
				.getDisplayAmount(channelTransferVO.getNetPayableAmount()));
		channelTransferEnquiryModel.setApprove1Remark(channelTransferVO
				.getFirstApprovalRemark());
		channelTransferEnquiryModel.setApprove2Remark(channelTransferVO
				.getSecondApprovalRemark());
		channelTransferEnquiryModel.setApprove3Remark(channelTransferVO
				.getThirdApprovalRemark());
		channelTransferEnquiryModel.setAddress(channelTransferVO
				.getFullAddress());
		channelTransferEnquiryModel.setTransferProfileName(channelTransferVO
				.getReceiverTxnProfileName());
		// channelTransferEnquiryModel.setTransferCategoryCode(p_channelTransferVO.getTransferCategory());
		channelTransferEnquiryModel.setTransferCategoryDesc(BTSLUtil
				.getOptionDesc(channelTransferVO.getTransferCategory(),
						channelTransferEnquiryModel.getTransferCategoryList())
				.getLabel());
		// added by amit for o2c transfer quantity change
		if (!BTSLUtil.isNullString(channelTransferVO
				.getLevelOneApprovedQuantity())) {
			channelTransferEnquiryModel.setFirstLevelApprovedQuantity(PretupsBL
					.getDisplayAmount(Long.parseLong(channelTransferVO
							.getLevelOneApprovedQuantity())));
		}
		if (!BTSLUtil.isNullString(channelTransferVO
				.getLevelTwoApprovedQuantity())) {
			channelTransferEnquiryModel
					.setSecondLevelApprovedQuantity(PretupsBL
							.getDisplayAmount(Long.parseLong(channelTransferVO
									.getLevelTwoApprovedQuantity())));
		}
		if (!BTSLUtil.isNullString(channelTransferVO
				.getLevelThreeApprovedQuantity())) {
			channelTransferEnquiryModel.setThirdLevelApprovedQuantity(PretupsBL
					.getDisplayAmount(Long.parseLong(channelTransferVO
							.getLevelThreeApprovedQuantity())));
		}
		// channelTransferEnquiryModel.setGeoDomainCodeDesc(p_channelTransferVO.getGrphDomainCodeDesc());
		channelTransferEnquiryModel.setChannelDomainDesc(channelTransferVO
				.getDomainCodeDesc());
		channelTransferEnquiryModel.setCategoryCodeDesc(channelTransferVO
				.getReceiverCategoryDesc());
		channelTransferEnquiryModel.setTrfTypeDetail(channelTransferVO
				.getTransferSubTypeValue());
		channelTransferEnquiryModel.setCommissionQuantity(PretupsBL
				.getDisplayAmount(channelTransferVO.getCommQty()));
		channelTransferEnquiryModel.setSenderDebitQuantity(PretupsBL
				.getDisplayAmount(channelTransferVO.getSenderDrQty()));
		channelTransferEnquiryModel.setReceiverCreditQuantity(PretupsBL
				.getDisplayAmount(channelTransferVO.getReceiverCrQty()));
		channelTransferEnquiryModel.setSosStatus(channelTransferVO
				.getSosStatus());
		channelTransferEnquiryModel.setSosCheck(PretupsI.LAST_LR_PENDING_STATUS
				.equalsIgnoreCase(channelTransferEnquiryModel.getSosStatus()));
		channelTransferEnquiryModel
				.setSosCheck1(PretupsI.LAST_LR_SETTLED_STATUS
						.equalsIgnoreCase(channelTransferEnquiryModel
								.getSosStatus()));
		if (channelTransferVO.getSosSettlementDate() != null) {
			channelTransferEnquiryModel.setSosSettlementDate(BTSLUtil
					.getDateStringFromDate(channelTransferVO
							.getSosSettlementDate()));
		}
	}

	/**
	 * Method downloadFileForEnq This method use for O2C transfer enquiry. This
	 * method write in xls file and download the xls file.
	 * 
	 * @param channelTransferEnquiryModel
	 * @param request
	 * @return String
	 * @author Rahul Arya
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String downloadFileforEnq(
			ChannelTransferEnquiryModel channelTransferEnquiryModel,
			HttpServletRequest request) throws IOException, BTSLBaseException {
		String methodName = "downloadFileForEnq";
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered");

		String filePath = null;
		String fileName = null;
		String fileArr[][] = null;
		String headingArr[][] = null;
		String fileLocation = null;
		try {

			ArrayList transferDetalsList = channelTransferEnquiryModel
					.getTransferList();
			filePath = Constants.getProperty("DownloadO2CTransferEnqPath");
			try {
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory())
					fileDir.mkdirs();
			} catch (SecurityException e) {
				_log.debug(methodName, "Exception" + e.getMessage());
				_log.errorTrace(methodName, e);
				throw new BTSLBaseException(this, methodName,
						"downloadfile.error.dirnotcreated", "error");

			}
			fileName = Constants.getProperty("DownloadO2CTransferEnqtFileName")
					+ BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()
					+ ".xls";

			int cols = 12;

			int rows = transferDetalsList.size() + 1;
			fileArr = new String[rows][cols];
			int i = 0, j = 0;
			String heading = "channel.transfer.o2c.xls.enq.fileheading";
			if (!BTSLUtil.isNullString(channelTransferEnquiryModel.getUserCode())) {
				headingArr = new String[2][4];
				headingArr[0][j] = "channel.transfer.o2c.xls.enq.msisdn";
				j = j + 1;
				headingArr[0][j] = "channel.transfer.o2c.xls.enq.trfcat";
				j = j + 1;
				headingArr[0][j] = "channel.transfer.o2c.xls.enq.fromdate";
				j = j + 1;
				headingArr[0][j] = "channel.transfer.o2c.xls.enq.todate";
				j = j + 1;
			} else {
				if (!BTSLUtil.isNullString(channelTransferEnquiryModel
						.getChannelOwnerCategoryDesc())) {
					headingArr = new String[2][10];
				}
				else{
					headingArr = new String[2][9];
				}
				headingArr[0][j] = "channel.transfer.o2c.xls.enq.geogdomain";
				j = j + 1;
				headingArr[0][j] = "channel.transfer.o2c.xls.enq.domain";
				j = j + 1;
				headingArr[0][j] = "channel.transfer.o2c.xls.enq.usercat";
				j = j + 1;
				if (!BTSLUtil.isNullString(channelTransferEnquiryModel
						.getChannelOwnerCategoryDesc())) {
					headingArr[0][j] = "channel.transfer.o2c.xls.enq.owncat";
					j = j + 1;
				}
				headingArr[0][j] = "channel.transfer.o2c.xls.enq.product";
				j = j + 1;
				headingArr[0][j] = "channel.transfer.o2c.xls.enq.trftype";
				j = j + 1;
				headingArr[0][j] = "channel.transfer.o2c.xls.enq.fromdate";
				j = j + 1;
				headingArr[0][j] = "channel.transfer.o2c.xls.enq.todate";
				j = j + 1;
				headingArr[0][j] = "channel.transfer.o2c.xls.enq.trfcat";
				j = j + 1;
				headingArr[0][j] = "channel.transfer.o2c.xls.enq.user";
				j = j + 1;
			}

			fileArr[0][i] = "channel.transfer.o2c.xls.enq.transferid";
			i = i + 1;
			fileArr[0][i] = "channel.transfer.o2c.xls.enq.orderfor";
			i = i + 1;
			fileArr[0][i] = "channel.transfer.o2c.xls.enq.geogdomain";
			i = i + 1;
			fileArr[0][i] = "channel.transfer.o2c.xls.enq.orderdate";
			i = i + 1;
			fileArr[0][i] = "channel.transfer.o2c.xls.enq.approvedby";
			i = i + 1;
			fileArr[0][i] = "channel.transfer.o2c.xls.enq.approvedon";
			i = i + 1;
			fileArr[0][i] = "channel.transfer.o2c.xls.enq.pmsisdn";
			i = i + 1;
			fileArr[0][i] = "channel.transfer.o2c.xls.enq.trftype";
			i = i + 1;
			fileArr[0][i] = "channel.transfer.o2c.xls.enq.qty";
			i = i + 1;
			fileArr[0][i] = "channel.transfer.o2c.xls.enq.amt";
			i = i + 1;
			fileArr[0][i] = "channel.transfer.o2c.xls.enq.status";
			i = i + 1;
			fileArr[0][i] = "channeltransfer.enquirytransferlist.label.transaction.mode";
			i = i + 1;

			fileArr = this.convertTo2dArray(fileArr, transferDetalsList, rows);
			headingArr = this.convertTo2dArrayHeader(headingArr,channelTransferEnquiryModel);

			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			pretupsRestUtil.writeExcel(ExcelFileIDI.O2C_TRF_ENQ, fileArr,
					headingArr, heading, 2, BTSLUtil.getBTSLLocale(request),
					filePath + "" + fileName);
			fileLocation = filePath + fileName;

		} catch (Exception e) {
			_log.debug(methodName, "Exception" + e.getMessage());
			_log.errorTrace(methodName, e);

		}

		return fileLocation;
	}

	/**
	 * method convertTo2dArrayHeader This method is used to convert ArrayList to
	 * 2D String array for header information
	 * 
	 * @param p_fileArr
	 * @param channelTransferEnquiryModel
	 * @return String[][]
	 */
	private String[][] convertTo2dArrayHeader(String[][] p_fileArr,
			ChannelTransferEnquiryModel channelTransferEnquiryModel) {
		String methodName = "convertTo2dArrayHeader";
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered p_fileArr=" + p_fileArr.length
					+ " p_form=" + channelTransferEnquiryModel);
		try {
			int rows = 1;
			int cols = 0;
			if (!BTSLUtil.isNullString(channelTransferEnquiryModel
					.getUserCode())) {
				p_fileArr[rows][cols] = channelTransferEnquiryModel
						.getUserCode();
				cols = cols + 1;
				p_fileArr[rows][cols] = channelTransferEnquiryModel
						.getTransferCategoryDesc();
				cols = cols + 1;
				p_fileArr[rows][cols] = channelTransferEnquiryModel
						.getFromDateForUserCode();
				cols = cols + 1;
				p_fileArr[rows][cols] = channelTransferEnquiryModel
						.getToDateForUserCode();
				cols = cols + 1;
			} else {
				p_fileArr[rows][cols] = channelTransferEnquiryModel
						.getGeoDomainCodeDesc();
				cols = cols + 1;
				p_fileArr[rows][cols] = channelTransferEnquiryModel
						.getChannelDomainDesc();
				cols = cols + 1;
				p_fileArr[rows][cols] = channelTransferEnquiryModel
						.getCategoryCodeDesc();
				cols = cols + 1;
				if (!BTSLUtil.isNullString(channelTransferEnquiryModel
						.getChannelOwnerCategoryDesc())) {
					p_fileArr[rows][cols] = channelTransferEnquiryModel
							.getChannelOwnerCategoryDesc();
					cols = cols + 1;
				}
				p_fileArr[rows][cols] = channelTransferEnquiryModel
						.getProductTypeDesc();
				cols = cols + 1;
				p_fileArr[rows][cols] = channelTransferEnquiryModel
						.getTransferTypeValue();
				cols = cols + 1;
				p_fileArr[rows][cols] = channelTransferEnquiryModel
						.getFromDate();
				cols = cols + 1;
				p_fileArr[rows][cols] = channelTransferEnquiryModel.getToDate();
				cols = cols + 1;
				p_fileArr[rows][cols] = channelTransferEnquiryModel
						.getTransferCategoryDesc();
				cols = cols + 1;
				p_fileArr[rows][cols] = channelTransferEnquiryModel
						.getChannelCategoryUserName();
				cols = cols + 1;
			}

		} catch (Exception e) {
			_log.debug(methodName, "Exception" + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelTransferEnquiryAction[convertTo2dArrayHeader]", "",
					"", "", "Exception:" + e.getMessage());
		}
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Exit p_fileArr=" + p_fileArr);
		return p_fileArr;
	}

	/**
	 * Method convertTo2dArray. This method is used to convert ArrayList to 2D
	 * String array
	 * 
	 * @param p_fileArr
	 *            String[][]
	 * @param ArrayList
	 *            p_transferDetalsList
	 * @param int p_rows
	 * @return p_fileArr String[][]
	 */
	private String[][] convertTo2dArray(String[][] p_fileArr,
			ArrayList p_transferDetalsList, int p_rows) {
		String methodName = "convertTo2dArray";
		if (_log.isDebugEnabled())
			_log.debug(methodName,
					"Entered p_fileArr=" + p_fileArr.length
							+ " p_transferDetalsList.size()="
							+ p_transferDetalsList.size() + " p_rows" + p_rows);
		try {
			Iterator iterator = p_transferDetalsList.iterator();
			int rows = 0;
			int cols;
			ChannelTransferVO transferVO = null;
			while (iterator.hasNext()) {
				transferVO = (ChannelTransferVO) iterator.next();
				rows++;
				cols = 0;
				p_fileArr[rows][cols] = transferVO.getTransferID();
				cols = cols + 1;
				p_fileArr[rows][cols] = transferVO.getToUserName();
				cols = cols + 1;
				p_fileArr[rows][cols] = transferVO.getGrphDomainCodeDesc();
				cols = cols + 1;
				p_fileArr[rows][cols] = transferVO.getTransferDateAsString();
				cols = cols + 1;
				if (BTSLUtil.isNullString(transferVO.getFinalApprovedBy())) {
					p_fileArr[rows][cols] = null;
					cols = cols + 1;
					p_fileArr[rows][cols] = null;
					cols = cols + 1;
				} else {
					p_fileArr[rows][cols] = transferVO.getFinalApprovedBy();
					cols = cols + 1;
					p_fileArr[rows][cols] = transferVO
							.getFinalApprovedDateAsString();
					cols = cols + 1;
				}
				p_fileArr[rows][cols] = transferVO.getUserMsisdn();
				cols = cols + 1;
				p_fileArr[rows][cols] = transferVO.getTransferSubTypeValue();
				cols = cols + 1;
				p_fileArr[rows][cols] = transferVO
						.getRequestedQuantityAsString();
				cols = cols + 1;
				p_fileArr[rows][cols] = transferVO.getPayableAmountAsString();
				cols = cols + 1;
				p_fileArr[rows][cols] = transferVO.getStatusDesc();
				cols = cols + 1;
				if (transferVO.getTransactionMode().equals("N")) {
					p_fileArr[rows][cols] = "NORMAL";
				} else {
					p_fileArr[rows][cols] = "AUTO";
				}
				cols = cols + 1;

			}
		} catch (Exception e) {
			_log.debug(methodName, "Exception" + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelTransferEnquiryAction[convertTo2dArray]", "", "",
					"", "Exception:" + e.getMessage());
		}
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Exit p_fileArr=" + p_fileArr);

		return p_fileArr;
	}

}
