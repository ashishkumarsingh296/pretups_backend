package com.web.pretups.channel.query.Service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.cardgroup.businesslogic.BonusBundleDAO;
import com.btsl.pretups.cardgroup.businesslogic.BonusBundleDetailVO;
import com.btsl.pretups.channel.transfer.businesslogic.BonusTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.query.web.C2STransferEnquiryModel;
import com.web.pretups.channel.transfer.businesslogic.C2STransferWebDAO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;
/**
 * 
 * @author Deepa.Shyam
 * This class implements C2STransferEnquiryService and
 *         provides methods for processing C2S Transfer Enquiry via Channel Users and Operators Users
 */
@Service("C2STransferEnquiryService")
public class C2STransferEnquiryServiceImpl implements C2STransferEnquiryService {
	private static final Log log = LogFactory
			.getLog(C2STransferEnquiryServiceImpl.class.getName());

	private static final String FAIL_KEY = "fail";
	private static final String PANEL_NAME = "formNumber";
	public static final String C2SEnquiry_MODEL = "C2STransferEnquiryModel";

	/**
	 * The loadList method will load the data that is required for c2s transfer enquiry
	 */
	@Override
	public void loadList(UserVO userVO,
			C2STransferEnquiryModel c2sTransferEnquiryModel, Model model)
			throws BTSLBaseException {
		final String methodName = "loadList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();
			try{con=mcomCon.getConnection();}catch(SQLException e){
					log.error(methodName, "Exceptin:e=" + e);
					log.errorTrace(methodName, e);
			}
		    loadServices(con, c2sTransferEnquiryModel);
			loadUserDetailsByUserType(con, userVO, c2sTransferEnquiryModel);
		} catch ( RuntimeException e) {

			throw new BTSLBaseException(e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2STransferEnquiryServiceImpl#loadList");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
	}

	/**
	 *  the loadTransferEnquiryListFromData method will provide the transaction list based on the enquiry done by user 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean loadTransferEnquiryListFromData(UserVO userVO,C2STransferEnquiryModel theForm, BindingResult bindingResult,
			HttpServletRequest request, Model model) throws BTSLBaseException,ValidatorException, IOException, SAXException {

		final String methodName = "loadTransferEnquiryListFromData";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		NetworkPrefixVO networkPrefixVO = null;
		String msisdnPrefix = null;
		String networkCode = null;
		Date fromDate = null, toDate = null;
		String senderMsisdn = null;
		String receiverMsisdn = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		ChannelUserVO channelUserVO = null;
		ArrayList<ChannelUserVO> hierarchyList = null;
		try {
			if (request.getParameter("SubmitOne")!=null) {
				CommonValidator commonValidator = new CommonValidator("configfiles/c2squery/validation-c2sTransferEnquiry.xml",
						theForm, "c2sEnquiryByTransferId");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				request.getSession().setAttribute(PANEL_NAME, "Panel-One");
			}
			if (request.getParameter("SubmitTwo")!=null) {
				CommonValidator commonValidator = new CommonValidator("configfiles/c2squery/validation-c2sTransferEnquiry.xml",
						theForm, "c2sEnquiryBySenderMsisdn");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				request.getSession().setAttribute(PANEL_NAME, "Panel-Two");
			}
			if (request.getParameter("SubmitThree")!=null) {
				CommonValidator commonValidator = new CommonValidator("configfiles/c2squery/validation-c2sTransferEnquiry.xml",
						theForm, "c2sEnquiryByReceiverMsisdn");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				request.getSession().setAttribute(PANEL_NAME, "Panel-Three");
			}
			if (bindingResult.hasFieldErrors()) {

				return false;
			}
			fromDate = BTSLUtil.getDateFromDateString(theForm.getFromDate());
			toDate = BTSLUtil.getDateFromDateString(theForm.getToDate());
			 int diff = BTSLUtil.getDifferenceInUtilDates(fromDate, toDate);
	           if(diff > 20){
	           model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.transferenquiry.enquirysearchattribute.msg.datediffernce", new String[]{Constants.getProperty("DATE_DIFFERENCE")}));
	               return false;
	           }
			networkCode = userVO.getNetworkID();
			String service = theForm.getServiceType();
			String transferID = null;
			channelUserWebDAO = new ChannelUserWebDAO();
			if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_RPT_DB)).booleanValue() && PretupsI.RESET_CHECKBOX
					.equals(theForm.getCurrentDateFlag()))) {
				mcomCon = new MComReportDBConnection();
				try{con=mcomCon.getConnection();}catch(SQLException e){
					log.error(methodName, "Exceptin:e=" + e);
	                log.errorTrace(methodName, e);
				}
			} else {
				mcomCon = new MComConnection();
				try{con=mcomCon.getConnection();}catch(SQLException e){
					log.error(methodName, "Exceptin:e=" + e);
	                log.errorTrace(methodName, e);
				}
			}
			// load the user hierarchy to validate the sender msisdn and
			// with in the login user hierarchy.

			if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
				String userID = null;
				if (PretupsI.CATEGORY_TYPE_AGENT.equals(userVO.getCategoryVO()
						.getCategoryType())
						&& PretupsI.NO.equals(userVO.getCategoryVO()
								.getHierarchyAllowed())) {
					userID = userVO.getParentID();
				} else {
					userID = userVO.getUserID();
				}
				// load whole hierarchy of the form user
				hierarchyList = channelUserWebDAO.loadChannelUserHierarchy(con,
						userID, false);
			}

			if (!BTSLUtil.isNullString(theForm.getTransferID())) {
				transferID = theForm.getTransferID().trim();
			}
			if (!BTSLUtil.isNullString(theForm.getReceiverMsisdn())) {
				// Change ID=ACCOUNTID
				// FilteredMSISDN is replaced by getFilteredIdentificationNumber
				// This is done because this field can contains msisdn or
				// account id
				receiverMsisdn = PretupsBL.getFilteredIdentificationNumber(theForm
								.getReceiverMsisdn());
				msisdnPrefix = PretupsBL.getMSISDNPrefix(receiverMsisdn);
				networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache
						.getObject(msisdnPrefix);
				if (networkPrefixVO == null) {
					String[] args = new String[] { theForm.getReceiverMsisdn() };
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("c2senquiry.viewc2stransfers.msg.notsupportnetwork",
											args));
					return false;
				}

			}

			// for c2s enquiry
			if (!theForm.isStaffEnquiry()) {
				if(!enquiryForChanneluser(con, userVO, theForm, model,
						networkPrefixVO, senderMsisdn, msisdnPrefix,
						networkCode, fromDate, toDate, receiverMsisdn,
						transferID, service, hierarchyList))
				{
					 return false;
				}
				if(theForm.getC2sTransferVOList().size() == 0){
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("c2s.query.c2stransferenquirydetails.msg.nodata"));
					return false;
				}
			} else {
				if(!enquiryForStaffUser(con, userVO, theForm, model, senderMsisdn,
						receiverMsisdn, transferID, service, fromDate, toDate)){
					 return false;
				}
			}

			// validate the information of the txn list as down to the login
			// user hierarchy only if senderMSISDN is null.
			if (!theForm.isStaffEnquiry()) {
				C2STransferVO c2sTransferVO = null;
				boolean isMatched = false;
				boolean isFormCheck = false;
				final ArrayList<C2STransferVO> txnList = theForm
						.getC2sTransferVOList();
				if (BTSLUtil.isNullString(senderMsisdn)
						&& hierarchyList != null && !hierarchyList.isEmpty()
						&& txnList != null && !txnList.isEmpty()) {
					isFormCheck = true;
					for (int m = 0, n = txnList.size(); m < n; m++) {
						c2sTransferVO = txnList.get(m);
						isMatched = false;
						for (int i = 0, j = hierarchyList.size(); i < j; i++) {
							channelUserVO = hierarchyList.get(i);
							if (channelUserVO.getMsisdn().equals(
									c2sTransferVO.getSenderMsisdn())) {
								isMatched = true;
								break;
							}
						}
						if (!isMatched) {
							if (BTSLUtil.isNullString(receiverMsisdn)) {
								model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2senquiry.viewc2stransfers.msg.notauthorize2"));
								return false;
							}
							txnList.remove(m);
							m--;
							n--;
						}
					}
				}
				if (txnList.isEmpty() && isFormCheck) {
					model.addAttribute(FAIL_KEY,
							PretupsRestUtil.getMessageString("c2senquiry.viewc2stransfers.msg.notauthorize2"));
					return false;
				}
			}
			
		} catch (ParseException | RuntimeException e) {
			log.error(methodName, "Exception:e =" + e);
            log.errorTrace(methodName, e);
			model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
			return false;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2STransferEnquiryServiceImpl#loadTransferEnquiryListFromData");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
		return true;
	}

	/**
	 * The loadServices method load services in service type list
	 * @param con
	 * @param c2sTransferEnquiryModel
	 * @throws BTSLBaseException
	 */
	private void loadServices(Connection con,
			C2STransferEnquiryModel c2sTransferEnquiryModel)
			throws BTSLBaseException {
		String methodName = "loadservices";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		try {
			final ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
			c2sTransferEnquiryModel
					.setServiceTypeList(servicesTypeDAO
							.loadServicesListForReconciliation(con,
									PretupsI.C2S_MODULE));

		} catch (RuntimeException e) {
			throw new BTSLBaseException(e);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
	}

	/**
	 * loads user details 
	 * @param con
	 * @param userVO
	 * @param c2sTransferEnquiryModel
	 * @throws BTSLBaseException
	 */
	private void loadUserDetailsByUserType(Connection con, UserVO userVO,
			C2STransferEnquiryModel c2sTransferEnquiryModel)
			throws BTSLBaseException {
		String methodName = "loadUserDetailsByUserType";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		final ArrayList<ListValueVO> loggedInUserDomainList = new ArrayList<ListValueVO>();
		try {
			c2sTransferEnquiryModel.setUserType(userVO.getUserType());
			if (userVO.getCategoryCode().equalsIgnoreCase(
					PretupsI.CUSTOMER_CARE)) {
				c2sTransferEnquiryModel.setUserType(PretupsI.CUSTOMER_CARE);
			}
			if (!c2sTransferEnquiryModel.getUserType().equals(
					PretupsI.CUSTOMER_CARE)) {
				final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
				c2sTransferEnquiryModel.setZoneList(userVO
						.getGeographicalAreaList());
				c2sTransferEnquiryModel.setDomainList(BTSLUtil
						.displayDomainList(userVO.getDomainList()));
				final int loginSeqNo = userVO.getCategoryVO()
						.getSequenceNumber();
				c2sTransferEnquiryModel.setCategorySeqNo(loginSeqNo + "");
				if (userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
					c2sTransferEnquiryModel
							.setParentCategoryList(categoryWebDAO
									.loadCategoryReportList(con));
				} else {
					c2sTransferEnquiryModel
							.setParentCategoryList(categoryWebDAO
									.loadCategoryReporSeqtList(con, loginSeqNo));
				}

				c2sTransferEnquiryModel.setLoginUserID(userVO.getUserID());
				c2sTransferEnquiryModel.setLoggedInUserCategoryCode(userVO
						.getCategoryVO().getCategoryCode());
				if (userVO.isStaffUser()) {
					c2sTransferEnquiryModel.setLoggedInUserName(userVO
							.getParentName());
				} else {
					c2sTransferEnquiryModel.setLoggedInUserName(userVO
							.getUserName());
				}
				if (c2sTransferEnquiryModel.getDomainListSize() == 0) {
					loggedInUserDomainList.add(new ListValueVO(userVO
							.getDomainName(), userVO.getDomainID()));
					c2sTransferEnquiryModel
							.setDomainList(loggedInUserDomainList);
					c2sTransferEnquiryModel.setDomainCode(userVO.getDomainID());
					c2sTransferEnquiryModel.setDomainName(userVO
							.getDomainName());
				} else if (c2sTransferEnquiryModel.getDomainListSize() == 1) {
					final ListValueVO listvo = (ListValueVO) c2sTransferEnquiryModel
							.getDomainList().get(0);
					c2sTransferEnquiryModel.setDomainCode(listvo.getValue());
					c2sTransferEnquiryModel.setDomainName(listvo.getLabel());
				}

				@SuppressWarnings("unchecked")
				final ArrayList<UserGeographiesVO> zoneList = c2sTransferEnquiryModel
						.getZoneList();
				UserGeographiesVO geographyVO = null;
				ListValueVO listValueVOZone = null;
				final ArrayList<ListValueVO> geoList = new ArrayList<ListValueVO>();

				for (int i = 0, k = zoneList.size(); i < k; i++) {
					geographyVO = zoneList.get(i);
					geoList.add(new ListValueVO(geographyVO
							.getGraphDomainName(), geographyVO
							.getGraphDomainCode()));
				}
				if (geoList.size() == 1) {
					listValueVOZone = geoList.get(0);
					c2sTransferEnquiryModel.setZoneCode(listValueVOZone
							.getValue());
					c2sTransferEnquiryModel.setZoneName(listValueVOZone
							.getLabel());
					c2sTransferEnquiryModel.setZoneList(geoList);
				} else {
					c2sTransferEnquiryModel.setZoneList(geoList);
				}
			}

		} catch (RuntimeException e) {
			throw new BTSLBaseException(e);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}

	}

	/**
	 * method enquiryForChanneluser
	 * loads the information of channel user if logged in  user is a channel user
	 */
	private boolean enquiryForChanneluser(Connection con, UserVO userVO,
			C2STransferEnquiryModel theForm, Model model,
			NetworkPrefixVO networkPrefixVO, String senderMsisdn,
			String msisdnPrefix, String networkCode, Date fromDate,
			Date toDate, String receiverMsisdn, String transferID,
			String service, ArrayList<ChannelUserVO> hierarchyList)
			throws BTSLBaseException {

		String methodName = "enquiryByUserType";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		ChannelUserVO channelUserVO = null;
		C2STransferDAO c2STransferDAO = null;
		try {

			if (!BTSLUtil.isNullString(theForm.getSenderMsisdn())) {
				senderMsisdn = PretupsBL.getFilteredMSISDN(theForm
						.getSenderMsisdn());
				msisdnPrefix = PretupsBL.getMSISDNPrefix(senderMsisdn);
				networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache
						.getObject(msisdnPrefix);
				if (networkPrefixVO == null) {
					model.addAttribute(FAIL_KEY,
							PretupsRestUtil.getMessageString("pretups.c2senquiry.viewc2stransfers.msg.notsupportnetwork",new String[] { theForm.getSenderMsisdn() }));
					 return false;
				}
				networkCode = networkPrefixVO.getNetworkCode();
				if (networkCode == null
						|| !networkCode.equals(userVO.getNetworkID())) {
					model.addAttribute(FAIL_KEY,
							PretupsRestUtil.getMessageString("pretups.c2senquiry.viewc2stransfers.msg.notsupportnetwork",new String[] { theForm.getSenderMsisdn() }));
					 return false;
				}
			}

			// load the user hierarchy to validate the sender msisdn and
			// with in the login user hierarchy.

			if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
				// check to user
				// under the hierarchy.

				if (hierarchyList == null || hierarchyList.isEmpty()) {
					if (log.isDebugEnabled()) {
						log.debug(methodName,
								"Logged in user has no child user so there would be no transactions");
					}
					model.addAttribute(FAIL_KEY,
							PretupsRestUtil.getMessageString("pretups.c2senquiry.viewc2stransfers.msg.nohierarchy"));
					 return false;
				}

				// if sender msisdn is not null then validate it in the
				// hierarchy.
				if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
					if (!BTSLUtil.isNullString(senderMsisdn)) {
						boolean isMatched = false;
						if (!hierarchyList.isEmpty()) {
							isMatched = false;
							for (int i = 0, j = hierarchyList.size(); i < j; i++) {
								channelUserVO = (ChannelUserVO) hierarchyList
										.get(i);
								if (channelUserVO.getMsisdn().equals(
										senderMsisdn)) {
									isMatched = true;
									break;
								}
							}
							if (!isMatched) {
								model.addAttribute(FAIL_KEY,
										PretupsRestUtil.getMessageString("pretups.c2senquiry.viewc2stransfers.msg.notauthorize",new String[] { theForm.getSenderMsisdn() }));
								 return false;
							}
						}
					}
				} else {
					UserPhoneVO userPhoneVO = null;
					final UserDAO userDAO = new UserDAO();
					userPhoneVO = userDAO.loadUserAnyPhoneVO(con, senderMsisdn);
					boolean isMatched = false;
					if (userPhoneVO != null && hierarchyList != null
							&& !hierarchyList.isEmpty()) {
						isMatched = false;
						for (int i = 0, j = hierarchyList.size(); i < j; i++) {
							channelUserVO = (ChannelUserVO) hierarchyList
									.get(i);
							if (channelUserVO.getUserID().equals(
									userPhoneVO.getUserId())) {
								isMatched = true;
								break;
							}
						}
						if (!isMatched) {
							model.addAttribute(FAIL_KEY,
									PretupsRestUtil.getMessageString("pretups.c2senquiry.viewc2stransfers.msg.notauthorize",new String[] { theForm.getSenderMsisdn() }));
							 return false;
						}
					}
				}
			}
			c2STransferDAO = new C2STransferDAO();
			theForm.setC2sTransferVOList(c2STransferDAO.loadC2STransferVOList(
					con, networkCode, fromDate, toDate, senderMsisdn,
					receiverMsisdn, transferID, service));
			return true;
		} catch (RuntimeException e) {
			throw new BTSLBaseException(e);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
	}
	/**
	 * method enquiryForStaffUser
	 * loads the information of staff user if logged in  user is a staff user
	 */
	@SuppressWarnings("unchecked")
	private boolean enquiryForStaffUser(Connection con, UserVO userVO,
			C2STransferEnquiryModel theForm, Model model, String senderMsisdn,
			String receiverMsisdn, String transferID, String service,
			Date fromDate, Date toDate) throws BTSLBaseException {

		String methodName = "enquiryForStaffUser";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		ArrayList<ListValueVO> userList = null;
		String UserId = null;
		String category_code = null;
		C2STransferWebDAO c2STransferwebDAO = null;
		C2STransferDAO c2STransferDAO = null;
		try {
			final UserWebDAO userwebDAO = new UserWebDAO();
			UserId = PretupsI.OPERATOR_USER_TYPE;
			c2STransferDAO = new C2STransferDAO();
			String userName = null;
			if (!(theForm.getUserType().equals(PretupsI.CUSTOMER_CARE))
					&& BTSLUtil.isNullString(receiverMsisdn)
					&& BTSLUtil.isNullString(transferID)) {
				if (((theForm.getLoginId().equals(PretupsI.ALL)) || theForm
						.getLoginId().equalsIgnoreCase("%"))
						&& BTSLUtil.isNullString(theForm.getParentUserName())) {
					model.addAttribute(FAIL_KEY,
							PretupsRestUtil.getMessageString("pretups.c2s.reports.web.userreportform.error.msg.required.value"));
					 return false;
				}
			}
			if ((theForm.getLoginId().equals(PretupsI.ALL))
					|| (BTSLUtil.isNullString(theForm.getLoginId()))) {
				userName = "%%%"; // for selecting all the child users
			} else if (theForm.getLoginId().equals("SELF")) {
				// enquiry
				userName = userVO.getLoginID();
			} else {
				userName = theForm.getLoginId();
			}
			if (!(theForm.getUserType().equals(PretupsI.CUSTOMER_CARE))) {
				if (PretupsI.USER_TYPE_CHANNEL.equals(userVO.getUserType())
						|| PretupsI.STAFF_USER_TYPE
								.equals(userVO.getUserType())) {
					UserId = userVO.getUserID();
				} else if (((!BTSLUtil.isNullString(receiverMsisdn) || (!BTSLUtil
						.isNullString(transferID) || !BTSLUtil
						.isNullString(userName))))
						&& (userVO.getCategoryCode().equals(PretupsI.BCU_USER))) {
					UserId = PretupsI.OPERATOR_USER_TYPE;
				} else {
					if (((BTSLUtil.isNullString(theForm.getZoneCode()) || BTSLUtil
							.isNullString(theForm.getDomainCode())) || BTSLUtil
							.isNullString(theForm.getParentCategoryCode()))
							&& BTSLUtil.isNullString(receiverMsisdn)
							&& BTSLUtil.isNullString(transferID)) {
						model.addAttribute(FAIL_KEY,
								PretupsRestUtil.getMessageString("pretups.c2s.reports.web.userreportform.error.msg.required.value"));
						 return false;
						
					}
					if (BTSLUtil.isNullString(theForm.getParentUserID())) {
						UserId = userVO.getUserID();
					} else {
						UserId = theForm.getParentUserID();
					}
					category_code = theForm.getParentCategoryCode()
							.split("\\|")[1];
				}
				if (UserId.equalsIgnoreCase(PretupsI.OPERATOR_USER_TYPE)) {
					if (!BTSLUtil.isNullString(theForm.getParentUserID())
							&& theForm.getLoginId().equals(PretupsI.ALL)) {
						UserId = theForm.getParentUserID();
						userName = "%%%";
						category_code = theForm.getParentCategoryCode().split(
								"\\|")[1];
					} else if (!BTSLUtil
							.isNullString(theForm.getParentUserID())
							&& !theForm.getLoginId().equals(PretupsI.ALL)) {
						UserId = theForm.getParentUserID();
						category_code = theForm.getParentCategoryCode().split(
								"\\|")[1];
					}
				}
			}
			userList = userwebDAO.loadUserListByLogin(con, UserId,
					PretupsI.STAFF_USER_TYPE, userName);

			if (userList != null && userList.isEmpty()
					&& !PretupsI.OPERATOR_USER_TYPE.equals(UserId)) {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("pretups.c2senquiry.viewc2stransfers.msg.nohierarchy"));
				 return false;

			}
			// removing all the user not requird for enquiry.
			ArrayList<String> domainList = null;
			if (UserId.equalsIgnoreCase(PretupsI.OPERATOR_USER_TYPE)
					&& !(userVO.getCategoryCode()
							.equalsIgnoreCase(PretupsI.CUSTOMER_CARE))) {
				final ArrayList<ListValueVO> domainList1 = BTSLUtil
						.displayDomainList(userVO.getDomainList());
				domainList = new ArrayList<>();
				int domainList10=domainList1.size();
				for (int i = 0; i <domainList10 ; i++) {
					final ListValueVO domain = (ListValueVO) domainList1.get(i);
					domainList.add(domain.getValue());
				}
			}

			if (!theForm.getLoginId().equals(PretupsI.ALL)
					&& !BTSLUtil.isNullString(theForm.getLoginId())) {
				int usersLists=userList.size();
				for (int i = 0; i < usersLists; i++) {
					final ListValueVO user = (ListValueVO) userList.get(i);
					if (!userName.equals(user.getLabel())) {
						userList.remove(i);
					}
				}
			}
			if ((UserId.equalsIgnoreCase(PretupsI.OPERATOR_USER_TYPE) && domainList != null)
					&& (userList != null && !userList.isEmpty() && !(userVO
							.getCategoryCode()
							.equalsIgnoreCase(PretupsI.CUSTOMER_CARE)))) {
				for (int i = 0; i < userList.size(); i++) {
					final ListValueVO user = (ListValueVO) userList.get(i);
					if (!domainList.contains(user.getIDValue())) {
						userList.remove(i);
					}
				}
			}
			if ((userList.isEmpty() || theForm.getLoginId().equals("%"))
					&& !PretupsI.OPERATOR_USER_TYPE.equals(UserId)) {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("pretups.staffc2senquiry.viewc2stransfers.error.loginidnotexist"));
				 return false;
			}
			final ArrayList<C2STransferVO> c2sTransferVOList = new ArrayList<>();

			// now load the transaction list
			c2STransferwebDAO = new C2STransferWebDAO();
			if (theForm.isStaffEnquiry()) {
				theForm.setC2sTransferVOList(c2STransferwebDAO
						.loadC2STransferVOList(con, userVO.getNetworkID(),
								fromDate, toDate, userList, receiverMsisdn,
								transferID, service, category_code));
				int theFormC2sTransferVOLists=theForm.getC2sTransferVOList().size();
				for (int i = 0; i < theFormC2sTransferVOLists; i++) {
					C2STransferVO c2sTransferVO = new C2STransferVO();
					c2sTransferVO = (C2STransferVO) theForm
							.getC2sTransferVOList().get(i);
					c2sTransferVO.setActiveUserName(c2sTransferVO
							.getSenderName());
					for (int k = 0; k < userList.size(); k++) {
						final ListValueVO user = (ListValueVO) userList.get(k);
						if (c2sTransferVO.getActiveUserId().equals(
								user.getValue())) {
							c2sTransferVO
									.setActiveUserName(user.getOtherInfo());
							break;
						}
					}
					c2sTransferVOList.add(c2sTransferVO);
				}
				theForm.setC2sTransferVOList(c2sTransferVOList);
			}
			// for c2s enquiry
			else {
				theForm.setC2sTransferVOList(c2STransferDAO
						.loadC2STransferVOList(con, userVO.getNetworkID(),
								fromDate, toDate, senderMsisdn, receiverMsisdn,
								transferID, service));
			}
			
			 return true;
		} catch (RuntimeException e) {
			model.addAttribute(FAIL_KEY,
					PretupsRestUtil.getMessageString(e.getMessage()));
			throw new BTSLBaseException(e);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
	}

	/**
	 * Method loadTransferItemsVOList.
	 * loads the detail information about any transsasction.
	 * **/

	public void loadTransferItemsVOList(C2STransferEnquiryModel theForm,
			HttpServletRequest request) {
		final String methodName = "loadTransferItemsVOList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED, log);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		C2STransferDAO c2STransferDAO = null;
		C2STransferWebDAO c2STransferwebDAO = null;
		try {
			final String transferID = request.getParameter("transferID");
			theForm.setTmpTransferID(transferID);
			C2STransferVO transferVO = null;
			@SuppressWarnings("unchecked")
			final List<C2STransferVO> voList = theForm.getC2sTransferVOList();
			for (int i = 0, j = voList.size(); i < j; i++) {
				transferVO = (C2STransferVO) voList.get(i);
				if (transferID.equals(transferVO.getTransferID())) {
					break;
				}
			}
			theForm.setTransferVO(transferVO);
			// Separate Enquiry From Report DB.
			if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_RPT_DB)).booleanValue() && PretupsI.RESET_CHECKBOX
					.equals(theForm.getCurrentDateFlag()))) {
				mcomCon = new MComReportDBConnection();con=mcomCon.getConnection();
			} else {
				mcomCon = new MComConnection();con=mcomCon.getConnection();
			}
			c2STransferDAO = new C2STransferDAO();
			c2STransferwebDAO = new C2STransferWebDAO();
			theForm.setC2sTransferItemsVOList(c2STransferDAO
					.loadC2STransferItemsVOList(con, transferID,
							transferVO.getTransferDate(), null));
			if (transferVO.getTransferStatus() != null
					&& transferVO.getTransferStatus().equals("SUCCESS")) {
				final Date fromDate = BTSLUtil.getDateFromDateString(theForm
						.getFromDate());
				final Date toDate = BTSLUtil.getDateFromDateString(theForm
						.getToDate());

				OperatorUtilI _operatorUtilI = null;
				try {
					_operatorUtilI = (OperatorUtilI) Class
							.forName(
									(String) PreferenceCache
											.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS))
							.newInstance();
				} catch (Exception e) {
					log.errorTrace(methodName, e);
					EventHandler.handle(EventIDI.SYSTEM_ERROR,
							EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.FATAL, "BuddyMgtAction", "", "", "",
							"Exception while loading the operator util class in class :"
									+ ChannelTransferDAO.class.getName() + ":"
									+ e.getMessage());
				}
				if (!_operatorUtilI.getNewDataAftrTbleMerging(fromDate, toDate)) {
					theForm.setBonusVOList(c2STransferwebDAO
							.loadC2SBonusVOList(con, transferID));
				} else {
					theForm.setBonusVOList(this.updateBonusVOList(con,
							transferVO));
				}

			}
			// forward = mapping.findForward("viewc2strfitems");
		} catch (Exception e) {
			log.error(methodName, "Exception: e= " + e);
			log.errorTrace(methodName, e);
			// return super.handleError(this, methodName, e, request, mapping);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2STransferEnquiryServiceImpl#loadTransferItemsVOList");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED, log);
			}
		}

	}

	/**
	 * Method updateBonusVOList. This method is used to return bonusList
	 * associated with transferID
	 * 
	 * @param String
	 *            p_bonusSummaryString
	 * @return ArrayList
	 */
	@SuppressWarnings("rawtypes")
	private ArrayList updateBonusVOList(Connection p_con,
			C2STransferVO p_transferVO) {
		final String methodName = "updateBonusVOList";
		LogFactory.printLog(methodName, "Entered p_transferVO=" + p_transferVO,
				log);
		ArrayList<BonusTransferVO> bonusTransferVOList = null;
		try {
			if (!BTSLUtil.isNullString(p_transferVO.getBonusSummarySting())) {
				final BonusBundleDAO bonusBundleDAO = new BonusBundleDAO();
				final ArrayList masterBonusBundleVOList = bonusBundleDAO
						.loadBonusBundleList(p_con);
				final ArrayList<String[]> bonusDetailsList = getSplitedDataListFromString(p_transferVO
						.getBonusSummarySting());

				if (bonusDetailsList != null && !bonusDetailsList.isEmpty()) {
					bonusTransferVOList = new ArrayList<BonusTransferVO>();
					final Iterator iterator = bonusDetailsList.listIterator();
					Iterator iteratorCommon;
					while (iterator.hasNext()) {
						final BonusTransferVO bonusTransferVO = new BonusTransferVO();
						final String[] singleBonusDetail = (String[]) iterator
								.next();
						for (int j = 0; j < singleBonusDetail.length - 1;) {
							final int bonusDetailLength = singleBonusDetail.length;
							LogFactory.printLog(methodName,
									"bonusDetailLength = " + bonusDetailLength,
									log);
							if (bonusDetailLength > 0) {
								bonusTransferVO
										.setAccountCode(singleBonusDetail[j]);
							}
							if (bonusDetailLength > 1) {
								bonusTransferVO
										.setAccountType(singleBonusDetail[++j]);
							}
							if (bonusDetailLength > 2) {
								bonusTransferVO.setBalance(Double
										.valueOf(singleBonusDetail[++j]));
							}
							if (bonusDetailLength > 3) {
								bonusTransferVO.setValidity(Long
										.valueOf(singleBonusDetail[++j]));
							}
							if (bonusDetailLength > 4) {
								bonusTransferVO.setAccountRate(Double
										.valueOf(singleBonusDetail[++j]));
							}

						}
						// name and id of each bonus is segregated
						if (masterBonusBundleVOList != null
								&& !masterBonusBundleVOList.isEmpty()) {
							iteratorCommon = masterBonusBundleVOList
									.listIterator();
							while (iteratorCommon.hasNext()) {
								final BonusBundleDetailVO bonusBundleDetailVO = (BonusBundleDetailVO) iteratorCommon
										.next();
								if (bonusTransferVO.getAccountCode().equals(
										bonusBundleDetailVO.getBundleCode())) {
									bonusTransferVO
											.setAccountId(bonusBundleDetailVO
													.getBundleID());
									bonusTransferVO
											.setAccountName(bonusBundleDetailVO
													.getBundleName());
									break;
								}
							}
						}
						// previous balance of each bonus is segregated
						final ArrayList<String[]> previousBalList = getSplitedDataListFromString(p_transferVO
								.getPreviousPromoBalance());
						if (previousBalList != null
								&& !previousBalList.isEmpty()) {
							iteratorCommon = previousBalList.listIterator();
							while (iteratorCommon.hasNext()) {
								final String[] bonusPreBal = (String[]) iteratorCommon
										.next();
								int i = 0;
								if (bonusTransferVO.getAccountCode().equals(
										bonusPreBal[i])) {
									bonusTransferVO.setPreviousBalance(Double
											.valueOf(bonusPreBal[++i]));
									break;
								}
							}
						}
						// Post balance of each bonus is segregated
						final ArrayList<String[]> postBalList = getSplitedDataListFromString(p_transferVO
								.getNewPromoBalance());
						if (postBalList != null && !postBalList.isEmpty()) {
							iteratorCommon = postBalList.listIterator();
							while (iteratorCommon.hasNext()) {
								final String[] bonusPostBal = (String[]) iteratorCommon
										.next();
								int i = 0;
								if (bonusTransferVO.getAccountCode().equals(
										bonusPostBal[i])) {
									bonusTransferVO.setPostBalance(Double
											.valueOf(bonusPostBal[++i]));
									break;
								}
							}
						}
						// Previous validity of each bonus is segregated
						final ArrayList<String[]> previousValidityList = getSplitedDataListFromString(p_transferVO
								.getPreviousPromoExpiry());
						if (previousValidityList != null
								&& !previousValidityList.isEmpty()) {
							iteratorCommon = previousValidityList
									.listIterator();
							while (iteratorCommon.hasNext()) {
								final String[] bonusPreVal = (String[]) iteratorCommon
										.next();
								int i = 0;
								if (bonusTransferVO.getAccountCode().equals(
										bonusPreVal[i])) {
									bonusTransferVO
											.setPreviousValidity(BTSLUtil
													.getDateFromString(
															bonusPreVal[++i],
															PretupsI.DATE_FORMAT_DDMMYYYY));
									break;
								}
							}
						}
						// Post validity of each bonus is segregated
						final ArrayList<String[]> postValidityList = getSplitedDataListFromString(p_transferVO
								.getNewPromoExpiry());
						if (postValidityList != null
								&& !postValidityList.isEmpty()) {
							iteratorCommon = postValidityList.listIterator();
							while (iteratorCommon.hasNext()) {
								final String[] bonusPostVal = (String[]) iteratorCommon
										.next();
								int i = 0;
								if (bonusTransferVO.getAccountCode().equals(
										bonusPostVal[i])) {
									bonusTransferVO.setPostValidity(BTSLUtil
											.getDateFromString(
													bonusPostVal[++i],
													PretupsI.DATE_FORMAT_DDMMYYYY));
									break;
								}
							}
						}
						bonusTransferVO.setCreatedOn(p_transferVO
								.getCreatedOn());
						bonusTransferVO.setTransferId(p_transferVO
								.getTransferID());
						bonusTransferVOList.add(bonusTransferVO);
					}
				}
			}
		} catch (Exception e) {
			log.debug(methodName, "Exception" + e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2STransferEnquiryAction[updateBonusVOList]", "", "", "",
					"Exception:" + e.getMessage());
		} finally {
			log.debug(methodName, "Exit");
		}
		return bonusTransferVOList;
	}

	/**
	 * Method getSplitedDataListFromString. This method is used to split the
	 * entered string
	 * 
	 * @param String
	 *            p_enteredString
	 * @return ArrayList
	 */

	private ArrayList<String[]> getSplitedDataListFromString(
			String p_enteredString) throws Exception {
		final String methodName = "getSplitedDataListFromString";
		LogFactory.printLog(methodName, "Entered p_enteredString="
				+ p_enteredString, log);
		final ArrayList<String[]> splitedArrayList = new ArrayList<String[]>();
		try {
			if (!BTSLUtil.isNullString(p_enteredString)) {
				final String[] pipeSeparatedArray = p_enteredString
						.split("\\|");
				for (int i = 0; i < pipeSeparatedArray.length; i++) {
					final String[] colonSeparatedArray = pipeSeparatedArray[i]
							.split(":");
					splitedArrayList.add(colonSeparatedArray);
				}
			}
		} catch (Exception e) {
			log.debug(methodName, "Exception" + e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2STransferEnquiryAction[getSplitedDataListFromString]",
					"", "", "", "Exception:" + e.getMessage());
		} finally {
			log.debug(methodName, "Exit");
		}
		return splitedArrayList;
	}

	/**
	 * method convertTo2dArrayHeader This method is used to convert ArrayList to
	 * 2D String array for header information
	 * 
	 * @param p_fileArr
	 * @param p_form
	 * @return String[][]
	 */
	private String[][] convertTo2dArrayHeader(String[][] p_fileArr,
			C2STransferEnquiryModel p_form) {
		final String methodName = "convertTo2dArrayHeader";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append(PretupsI.ENTERED);
			loggerValue.append("p_fileArr=");
			loggerValue.append (p_fileArr);
			loggerValue.append (" p_form=");
			loggerValue.append (p_form);
			log.debug(methodName,  loggerValue, log);
		}
		try {
			final int rows = 1;
			int cols = 0;
			p_fileArr[rows][cols++] = p_form.getServiceType();
			if (!BTSLUtil.isNullString(p_form.getSenderMsisdn())) {
				p_fileArr[rows][cols++] = p_form.getSenderMsisdn();
			} else if (!BTSLUtil.isNullString(p_form.getReceiverMsisdn())) {
				p_fileArr[rows][cols++] = p_form.getReceiverMsisdn();
			}
			p_fileArr[rows][cols++] = p_form.getFromDate();
			p_fileArr[rows][cols] = p_form.getToDate();

		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception ");
			loggerValue.append(e.getMessage());
			log.debug(methodName,  loggerValue );
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception: ");
			loggerValue.append( e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2STransferEnquiryAction[convertTo2dArrayHeader]", "", "",
					"",  loggerValue.toString());
		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append(PretupsI.EXITED);
			loggerValue.append ("p_fileArr=");
			loggerValue.append (p_fileArr);
			log.debug(methodName, loggerValue,log);
		}
		return p_fileArr;
	}

	/**
	 * Method convertTo2dArray. This method is used to convert ArrayList to 2D
	 * String array
	 * 
	 * @param p_fileArr
	 *            String[][]
	 * @param ArrayList
	 *            p_batchDetalsList
	 * @param int p_rows
	 * @return p_fileArr String[][]
	 */
	@SuppressWarnings("rawtypes")
	private String[][] convertTo2dArray(String[][] p_fileArr,
			ArrayList p_transferDetalsList, int p_rows) {
		final String methodName = "convertTo2dArray";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append(PretupsI.ENTERED );
			loggerValue.append ("p_fileArr=");
			loggerValue.append (p_fileArr);
			loggerValue.append (" p_transferDetalsList.size()=");
			loggerValue.append (p_transferDetalsList.size() );
			loggerValue.append (" p_rows");
			loggerValue.append (p_rows);
			log.debug(methodName,loggerValue ,log);
		}
		try {
			final Iterator iterator = p_transferDetalsList.iterator();
			int rows = 0;
			int cols;
			C2STransferVO transferVO = null;
			while (iterator.hasNext()) {
				transferVO = (C2STransferVO) iterator.next();
				rows++;
				cols = 0;
				p_fileArr[rows][cols++] = transferVO.getTransferID();
				p_fileArr[rows][cols++] = transferVO.getTransferDateStr();
				p_fileArr[rows][cols++] = transferVO.getSubService();
				p_fileArr[rows][cols++] = transferVO.getProductName();
				if (PretupsI.YES.equalsIgnoreCase(transferVO
						.getDifferentialApplicable())) {
					p_fileArr[rows][cols++] = "Yes";
				} else if (!PretupsI.YES.equalsIgnoreCase(transferVO
						.getDifferentialApplicable())) {
					p_fileArr[rows][cols++] = "No";
				}
				if (PretupsI.YES.equalsIgnoreCase(transferVO
						.getDifferentialGiven())) {
					p_fileArr[rows][cols++] = "Yes";
				} else if (!PretupsI.YES.equalsIgnoreCase(transferVO
						.getDifferentialGiven())) {
					p_fileArr[rows][cols++] = "No";
				}
				p_fileArr[rows][cols++] = transferVO.getSenderName();
				p_fileArr[rows][cols++] = transferVO.getSenderMsisdn();
				p_fileArr[rows][cols++] = transferVO.getNetworkCode();
				p_fileArr[rows][cols++] = transferVO.getReceiverMsisdn();
				p_fileArr[rows][cols++] = transferVO.getTransferValueStr();
				if (BTSLUtil.isNullString(transferVO.getSourceType())) {
					p_fileArr[rows][cols++] = null;
				} else {
					p_fileArr[rows][cols++] = transferVO.getSourceType();
				}
				p_fileArr[rows][cols++] = transferVO.getTransferStatus();
				if (BTSLUtil.isNullString(transferVO.getErrorMessage())) {
					p_fileArr[rows][cols++] = null;
				} else {
					p_fileArr[rows][cols++] = transferVO.getErrorMessage();
				}
				if (BTSLUtil.isNullString(transferVO.getSerialNumber())) {
					p_fileArr[rows][cols++] = null;
				} else {
					p_fileArr[rows][cols++] = transferVO.getSerialNumber();
				}
				
				if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_ID_SWITCH_ID_REQUIRED)).booleanValue()){
					if (BTSLUtil.isNullString(transferVO.getCellId()))
						p_fileArr[rows][cols++] = null;
					else
						p_fileArr[rows][cols++] = transferVO.getCellId();
					if (BTSLUtil.isNullString(transferVO.getSwitchId()))
						p_fileArr[rows][cols++] = null;
					else
						p_fileArr[rows][cols++] = transferVO.getSwitchId();
				}
				
				if (BTSLUtil.isNullString(transferVO.getReverseTransferID()))
					p_fileArr[rows][cols] = null;
				else
					p_fileArr[rows][cols] = transferVO.getReverseTransferID();

			}
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(" Exception");
			loggerValue.append (e.getMessage());
			log.debug(methodName, loggerValue );
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception: ");
			loggerValue.append (e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2STransferEnquiryAction[convertTo2dArray]", "", "", "",
					loggerValue.toString());
		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append(PretupsI.EXITED);
			loggerValue.append ("p_fileArr=");
			loggerValue.append (p_fileArr);
			log.debug(methodName,  loggerValue ,log);
		}
		return p_fileArr;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String downloadFileForEnq(
			C2STransferEnquiryModel c2sTransferEnquiryModel,
			HttpServletRequest request,Model model) {
		final String methodName = "downloadFileForEnq";
		StringBuilder loggerValue= new StringBuilder(); 
		LogFactory.printLog(methodName, "Entered", log);
		String filePath = null;
		String fileName = null;
		String filelocation = null;
		String fileArr[][] = null;
		String headingArr[][] = null;
		try {
			final ArrayList transferDetalsList = c2sTransferEnquiryModel
					.getC2sTransferVOList();
			filePath = Constants.getProperty("DownloadC2STransferEnqPath");
			try {
				final File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
			} catch (SecurityException e) {
				loggerValue.setLength(0);
				loggerValue.append( "Exception");
				loggerValue.append (e.getMessage());
				log.debug(methodName, loggerValue );
				log.errorTrace(methodName, e);
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("downloadfile.error.dirnotcreated"));
			

			}
			fileName = Constants.getProperty("DownloadC2STransferEnqtFileName")
					+ BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()
					+ ".xls";
			int cols;
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_ID_SWITCH_ID_REQUIRED)).booleanValue()){
				cols = 18;
			}else{
				cols = 16;
			}
		

			final int rows = transferDetalsList.size() + 1;
			fileArr = new String[rows][cols];
			int i = 0, j = 0;
			final String heading = "channel.transfer.c2s.xls.enq.fileheading";

			if (!BTSLUtil.isNullString(c2sTransferEnquiryModel
					.getSenderMsisdn())
					|| !BTSLUtil.isNullString(c2sTransferEnquiryModel
							.getReceiverMsisdn())) {
				headingArr = new String[2][4];
			} else {
				headingArr = new String[2][3];
			}

			headingArr[0][j++] = "channel.transfer.c2s.xls.enq.service";
			if (!BTSLUtil.isNullString(c2sTransferEnquiryModel
					.getSenderMsisdn())) {
				headingArr[0][j++] = "channel.transfer.c2s.xls.enq.sendermsisdn";
			} else if (!BTSLUtil.isNullString(c2sTransferEnquiryModel
					.getReceiverMsisdn())) {
				headingArr[0][j++] = "channel.transfer.c2s.xls.enq.receivermsisdn";
			}
			headingArr[0][j++] = "channel.transfer.c2s.xls.enq.fromdate";
			headingArr[0][j++] = "channel.transfer.c2s.xls.enq.todate";

			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.transferid";
			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.trfdate";
			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.subser";
			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.prod";
			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.diffapp";
			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.diffgiven";
			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.sendername";
			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.sendermsisdn";
			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.sendernw";
			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.receivermsisdn";
			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.trfvalue";
			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.reqsrc";
			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.txnstatus";
			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.errormsg";
			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.serialno";
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_ID_SWITCH_ID_REQUIRED)).booleanValue()){
				fileArr[0][i++] = "channel.transfer.c2s.xls.enq.cellid";
				fileArr[0][i++] = "channel.transfer.c2s.xls.enq.switchid";
			}
			
			fileArr[0][i++] = "channel.transfer.c2s.xls.enq.reversalid";

			fileArr = this.convertTo2dArray(fileArr, transferDetalsList, rows);
			headingArr = this.convertTo2dArrayHeader(headingArr,
					c2sTransferEnquiryModel);
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			pretupsRestUtil.writeExcel(ExcelFileIDI.C2S_TRF_ENQ, fileArr,
					headingArr, heading, 2, BTSLUtil.getBTSLLocale(request),
					filePath + "" + fileName);
			filelocation = filePath + fileName;

		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append( "Exception");
			loggerValue.append (e.getMessage());
			log.debug(methodName, loggerValue);
			log.errorTrace(methodName, e);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED, log);
			}
		}
		return filelocation;
	}

}
