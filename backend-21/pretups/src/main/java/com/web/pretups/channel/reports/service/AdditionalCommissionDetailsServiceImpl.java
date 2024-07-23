package com.web.pretups.channel.reports.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLCommonController;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.AdditionalCommissionReportDAO;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.DownloadCSVReports;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

@Service("additionalCommissionDetails")
public class AdditionalCommissionDetailsServiceImpl implements  AdditionalCommissionDetailsService{

	public static final Log log = LogFactory
			.getLog(AdditionalCommissionDetailsServiceImpl.class.getName());
	
	private BTSLCommonController btslCommonController = new BTSLCommonController();
	private static final String FAIL_KEY = "fail";
	private static final String FORM_NUMBER = "formNumber";
	private static final String CLASS_NAME = "AdditionalCommissionDetailsServiceImpl#";
	private static final String EXCEPTION_KEY = "Exceptin:e=";
	private static final String ALL_KEY = "pretups.list.all";
	
	@Override
	public UsersReportModel loadUsersForAdditionalCommission(HttpServletRequest request){
		
		final String methodName = "loadUsersForAdditionalCommission";
        if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.ENTERED);
        }
    
        Connection con = null;
        MComConnectionI mcomCon = null;

        final ArrayList loggedInUserDomainList = null;
        final ListValueVO listValueVO = null;
        UsersReportModel userReportsModel = new UsersReportModel();
        
        try {
        mcomCon = new MComConnection();
        con = mcomCon.getConnection();
        btslCommonController.loadingSummaryDetails(userReportsModel, request, con, loggedInUserDomainList, listValueVO);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
            	log.debug("loadUsersForAddnlCom", "Exceptin: e=" + e.getMessage());
            }
            log.errorTrace(methodName, e);
        } finally {
        	if(mcomCon != null){
        		mcomCon.close(CLASS_NAME+methodName);
        		mcomCon = null;
        	}
            if (log.isDebugEnabled()) {
            	log.debug("loadUsersForAdditionalCommission", "Exiting:" + methodName);
            }
        }
		return userReportsModel;
        
	}
	
	
	/**
	 * 
	 * @param usersReportModel
	 * @param userVO
	 * @param zoneCode
	 * @param domainCode
	 * @param userName
	 * @param parentCategoryCode
	 * @return
	 */

	@Override
    @SuppressWarnings("unchecked")
	public List<ChannelUserTransferVO> loadUserList( UsersReportModel usersReportModel, UserVO userVO, String userName, String domainCode, String zoneCode, String parentCategory) {
        final String methodName = "loadUserList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.ENTERED);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserReportDAO channelUserDAO = null;


        try {
            long time = usersReportModel.getTime();
            long newTime = 0;
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, " time: " + time + " newTime: " + newTime);
            }
            if (newTime != 0 && newTime != time) {
                throw new BTSLBaseException("common.securitymanager.error.invalidoperation");
            }
            
            ArrayList<ChannelUserTransferVO> userList = new ArrayList<ChannelUserTransferVO>();
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            channelUserDAO = new ChannelUserReportDAO();


            String[] arr = parentCategory.split("\\|");

            if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                if (parentCategory.equals(PretupsI.ALL)) {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategoryHierarchy(con, PretupsI.ALL, domainCode, zoneCode, userName, userVO.getUserID());
                } else {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategoryHierarchy(con, arr[1], domainCode, zoneCode, userName, userVO.getUserID());
                }
            } else {
                if (parentCategory.equals(PretupsI.ALL)) {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategory(con, PretupsI.ALL, domainCode, zoneCode, userName, userVO.getUserID());
                } else {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategory(con, arr[1], domainCode, zoneCode, userName, userVO.getUserID());
                }
            }
            usersReportModel.setUserList(userList);
            usersReportModel.setUserListSize(userList);
            
            return userList;

        } catch (Exception e) {
            log.error(methodName, EXCEPTION_KEY + e);
            log.errorTrace(methodName, e);
        } finally {
        	if(mcomCon != null){
        		mcomCon.close("BTSLDispatchAction#loadUserList");
        		mcomCon=null;
        		}
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: " + methodName);
            }
        }
        return null;

    }
	
	/**
	 * 
	 * @param usersReportModel
	 * @param request
	 * @param userVO
	 * @return
	 */
    @Override
	public boolean loadAdditionalCommissionReport(UsersReportModel usersReportModel, BindingResult bindingResult, UserVO userVO, HttpServletRequest request, Model model){
		final String methodName = "loadAdditionalCommissionReport";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		Connection con = null;
		String rptCode = null;
		MComConnectionI mcomCon = null;
		ArrayList userList = new ArrayList();
		String tempCatCode = "";
		ChannelUserWebDAO channelUserWebDAO = null;
		AdditionalCommissionReportDAO additionalCommisionReportDAO = null;
		try {
			channelUserWebDAO = new ChannelUserWebDAO();
			usersReportModel.setNetworkCode(userVO.getNetworkID());
			usersReportModel.setNetworkName(userVO.getNetworkName());
			usersReportModel.setReportHeaderName(userVO.getReportHeaderName());
			additionalCommisionReportDAO = new AdditionalCommissionReportDAO();
			ListValueVO listValueVO = null;
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();

			String[] ar = null;
			if(request.getParameter("submitMsisdn") != null){
				CommonValidator commonValidator = new CommonValidator(
						"configfiles/c2sreports/validator-AdditionalCommDetailsRpt.xml", usersReportModel,
						"UserModelMsisdn");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
        		model.addAttribute(FORM_NUMBER, "Panel-One");
        		request.getSession().setAttribute(FORM_NUMBER, "Panel-One");
			}
			
			if(request.getParameter("submitUser") != null){
				CommonValidator commonValidator = new CommonValidator(
						"configfiles/c2sreports/validator-AdditionalCommDetailsRpt.xml", usersReportModel,
						"UserModelLoginId");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
        		model.addAttribute(FORM_NUMBER, "Panel-Two");
				request.getSession().setAttribute(FORM_NUMBER, "Panel-Two");
			}
			if (BTSLUtil.isNullString(usersReportModel.getMsisdn()) && BTSLUtil.isNullString(usersReportModel.getZoneCode()) && (BTSLUtil.isNullString(usersReportModel.getDomainCode()))) {
				if(BTSLUtil.isNullString(usersReportModel.getParentCategoryCode()) && BTSLUtil.isNullString(usersReportModel.getUserName())) {
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.c2s.reports.web.additionalcommission.error.msg.required.value"));
					return false;
				}
				
			} else if (BTSLUtil.isNullString(usersReportModel.getMsisdn())) {
				if (BTSLUtil.isNullString(usersReportModel.getZoneCode())) {
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.c2s.reports.web.additionalcommission.error.msg.required.zone"));
					return false;
				}
				if (BTSLUtil.isNullString(usersReportModel.getDomainCode())) {
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.c2s.reports.web.additionalcommission.error.msg.required.domain"));
					return false;
				}
				if (BTSLUtil.isNullString(usersReportModel.getParentCategoryCode())) {
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.c2s.reports.web.additionalcommission.error.msg.required.category"));
					return false;
				}
				if (BTSLUtil.isNullString(usersReportModel.getUserName())) {
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.c2s.reports.web.additionalcommission.error.msg.required.user"));
					return false;
				}
			}
			if (!BTSLUtil.isNullString(usersReportModel.getMsisdn())) {
				if (!BTSLUtil.isValidMSISDN(usersReportModel.getMsisdn())) {
					ar = new String[1];
					ar[0] = usersReportModel.getMsisdn();
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.btsl.msisdn.error.length"));
					return false;
				}
			}

			if (!BTSLUtil.isNullString(usersReportModel.getMsisdn()))// MSISDN based report
			{
				final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(usersReportModel.getMsisdn())));
				if (prefixVO == null || !prefixVO.getNetworkCode().equals(usersReportModel.getNetworkCode())) {
					final String[] arr1 = { usersReportModel.getMsisdn(), usersReportModel.getNetworkName() };
					log.error(methodName, "Error: MSISDN Number" + usersReportModel.getMsisdn() + " not belongs to " + usersReportModel.getNetworkName() + "network");
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalcommission.error.msisdnnotinsamenetwork",arr1));
					return false;
				}

				final String status = "'" + PretupsI.USER_STATUS_NEW + "','" + PretupsI.USER_STATUS_CANCELED + "'";

				final String statusUsed = PretupsI.STATUS_NOTIN;
				final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
				final String filteredMSISDN = PretupsBL.getFilteredMSISDN(usersReportModel.getMsisdn());
				ChannelUserVO channelUserVO = null;
				
				if (PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())) {
					channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMSISDN, null, statusUsed, status);
				} else {
					channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMSISDN, userVO.getUserID(), statusUsed, status);
				}
				if (!(PretupsBL.getFilteredMSISDN(usersReportModel.getMsisdn()).equals(userVO.getMsisdn())) && channelUserVO == null) {
					ar = new String[1];
					ar[0] = usersReportModel.getMsisdn();
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalcommission.error.nouserbymsisdn",ar));
					return false;
				}
				
				ArrayList channelUserList = new ArrayList();
				if ("Y".equals(userVO.getCategoryVO().getHierarchyAllowed())) {
					channelUserList = channelUserWebDAO.loadChannelUserHierarchy(con, userVO.getUserCode(), false);
				} else {
					channelUserList = channelUserWebDAO.loadChannelUserHierarchy(con, (channelUserDAO.loadChannelUser(con, userVO.getParentID())).getUserCode(), false);
				}
				if (channelUserList != null && !channelUserList.isEmpty() && !(channelUserVO.getCategoryVO().getAgentDomainCodeforCategory()).equals(userVO.getCategoryVO().getDomainCodeforCategory())) {

					final String[] arr1 = { usersReportModel.getMsisdn() };
					log.error(methodName, "Error: MSISDN Number" + usersReportModel.getMsisdn() + " does not belong to channel domain");
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalcommission.error.usernotindomain",ar));
					return false;

				}
				btslCommonController.validateMsisdn(filteredMSISDN, userVO, usersReportModel, channelUserVO, con);
				usersReportModel.setUserName(usersReportModel.getUsersName());
				usersReportModel.setPrntCatCode(usersReportModel.getParentCategoryCode());
			}
			else {
				String userName = usersReportModel.getUserName();
				String[] parts = userName.split("\\(");
				userName = parts[0];
				usersReportModel.setUserName(userName);
				tempCatCode = usersReportModel.getParentCategoryCode();
				if (usersReportModel.getZoneCode().equals(TypesI.ALL)) {
					usersReportModel.setZoneName(PretupsRestUtil.getMessageString(ALL_KEY));
				} else {
					listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getZoneCode(), usersReportModel.getZoneList());
					usersReportModel.setZoneName(listValueVO.getLabel());
				}
				listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getDomainCode(), usersReportModel.getDomainList());
				usersReportModel.setDomainName(listValueVO.getLabel());
				final ChannelUserReportDAO channelUserReportDAO = new ChannelUserReportDAO();
				if (usersReportModel.getParentCategoryCode().equals(TypesI.ALL)) {
					usersReportModel.setParentCategoryCode(TypesI.ALL);
					usersReportModel.setCategoryName(PretupsRestUtil.getMessageString(ALL_KEY));
					userList = channelUserReportDAO.loadUserListBasisOfZoneDomainCategory(con, PretupsI.ALL, usersReportModel.getDomainCode(), usersReportModel.getZoneCode(), usersReportModel
							.getUserName(), userVO.getUserID());
				} else {
					listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getParentCategoryCode(), usersReportModel.getParentCategoryList());
					usersReportModel.setCategoryName(listValueVO.getLabel());

					final String[] arr = usersReportModel.getParentCategoryCode().split("\\|");
					usersReportModel.setParentCategoryCode(arr[1]);
					userList = channelUserReportDAO.loadUserListBasisOfZoneDomainCategory(con, arr[1], usersReportModel.getDomainCode(), usersReportModel.getZoneCode(), usersReportModel
							.getUserName(), userVO.getUserID());
				}
				if (usersReportModel.getUserName().equalsIgnoreCase(PretupsRestUtil.getMessageString(ALL_KEY))) {
					usersReportModel.setUserID(PretupsI.ALL);
				} else if (userList == null ||  userList.isEmpty()) {
					usersReportModel.setParentCategoryCode(tempCatCode);
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.user.selectcategoryforedit.error.usernotexist"));
					return false;
				} else if (userList != null && userList.size() == 1) {
					final ChannelUserTransferVO channelUserTransferVO = (ChannelUserTransferVO) userList.get(0);
					usersReportModel.setUserName(channelUserTransferVO.getUserName());
					usersReportModel.setUserID(channelUserTransferVO.getUserID());
				} else if (userList != null && userList.size() > 1) {
					
					boolean flag = true;
					if (!BTSLUtil.isNullString(usersReportModel.getUserID())) {
						for (int i = 0, j = userList.size(); i < j; i++) {
							final ChannelUserTransferVO channelUserTransferVO = (ChannelUserTransferVO) userList.get(i);
							if (usersReportModel.getUserID().equals(channelUserTransferVO.getUserID()) && usersReportModel.getUserName().equalsIgnoreCase(channelUserTransferVO.getUserName())) {
								usersReportModel.setUserName(channelUserTransferVO.getUserName());
								flag = false;
								break;
							}
						}
					}
					if (flag) {
						usersReportModel.setParentCategoryCode(tempCatCode);
						model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.user.selectcategoryforedit.error.usermorethanone"));
						return false;
					}
				}
				usersReportModel.setMsisdn("Not Applicable");
				usersReportModel.setPrntCatCode(usersReportModel.getParentCategoryCode());
			}
			final Date frDate = BTSLUtil.getDateFromDateString(usersReportModel.getCurrentDate());
			if (usersReportModel.getFromTime().indexOf(":") == -1) {
				usersReportModel.setRptfromDate(usersReportModel.getCurrentDate() + " " + usersReportModel.getFromTime() + ":00");
			} else {
				usersReportModel.setRptfromDate(usersReportModel.getCurrentDate() + " " + usersReportModel.getFromTime());
			}
			if (usersReportModel.getToTime().indexOf(":") == -1) {
				usersReportModel.setRpttoDate(usersReportModel.getCurrentDate() + " " + usersReportModel.getToTime() + ":00");
			} else {
				usersReportModel.setRpttoDate(usersReportModel.getCurrentDate() + " " + usersReportModel.getToTime());
			}
			
			Date currentDate = new Date();
			final String format = Constants.getProperty("report.dtformat");
			final Date frTime = BTSLUtil.getDateFromDateString(usersReportModel.getRptfromDate(), format);
			final Date toTime = BTSLUtil.getDateFromDateString(usersReportModel.getRpttoDate(), format);
			if (currentDate.getTime() < frTime.getTime()) {
				if (currentDate.getTime() < toTime.getTime()) {
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.err.msg.fromntotimeaftercurrenttime"));
					return false;
				} else {
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.err.msg.fromtimeaftercurrenttime"));
					return false;
				}
			}
			if (currentDate.getTime() < toTime.getTime()) {
				model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.err.msg.totimeaftercurrenttime"));
				return false;
			}

			if (frTime.after(toTime)) {
				model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.err.msg.fromdateaftertodate"));
				return false;
			}
			currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(currentDate));
			if (currentDate.equals(frDate)) {
				usersReportModel.setDateType(PretupsI.DATE_CHECK_CURRENT);
			} else {
				usersReportModel.setDateType("");
			}
			final Date crDate = BTSLUtil.getDateFromDateString(usersReportModel.getCurrentDate());
			final String currdate = BTSLUtil.getDateStringFromDate(crDate);
			usersReportModel.setCurrentDate(currdate);
			
		
			OperatorUtil operatorUtil = new OperatorUtil();
			if("OPERATOR".equals(usersReportModel.getUserType())){
				rptCode = "ADDCOMDT01";
				if(operatorUtil.getNewDataAftrTbleMerging(BTSLUtil.getDateFromDateString(usersReportModel.getCurrentDate()),null))
				{
					ArrayList<ChannelTransferVO> transferList = additionalCommisionReportDAO.loadAdditionalCommisionOpeartorDetails(con, usersReportModel);
					usersReportModel.setTransferList(transferList);
					usersReportModel.setTransferListSize(transferList.size());
					usersReportModel.setrptCode(rptCode);
				}else
				{
					ArrayList<ChannelTransferVO> transferList = additionalCommisionReportDAO.loadAdditionalCommisionOpeartorOldDetails(con, usersReportModel);
					usersReportModel.setTransferList(transferList);
					usersReportModel.setTransferListSize(transferList.size());
					usersReportModel.setrptCode(rptCode);
				}
			}else{
				rptCode = "ADDCOMDTCH01";
				if(operatorUtil.getNewDataAftrTbleMerging(BTSLUtil.getDateFromDateString(usersReportModel.getCurrentDate()),null)){
					ArrayList<ChannelTransferVO> transferList = additionalCommisionReportDAO.loadAdditionalCommisionChannelDetails(con, usersReportModel);
					usersReportModel.setTransferList(transferList);
					usersReportModel.setTransferListSize(transferList.size());
					usersReportModel.setrptCode(rptCode);
				}else{
					ArrayList<ChannelTransferVO> transferList = additionalCommisionReportDAO.loadAdditionalCommisionChannelOldDetails(con, usersReportModel);
					usersReportModel.setTransferList(transferList);
					usersReportModel.setTransferListSize(transferList.size());
					usersReportModel.setrptCode(rptCode);
				}
				
			}
		} catch (BTSLBaseException | ParseException e) {
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(EXCEPTION_KEY);
				loggerValue.append(e.getMessage());
				log.debug(methodName, loggerValue);
			}
			log.errorTrace(methodName, e);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(EXCEPTION_KEY);
				loggerValue.append(e.getMessage());
				log.debug(methodName, loggerValue);
			}
			log.errorTrace(methodName, e);
		} finally {
			if(mcomCon != null){
			mcomCon.close(CLASS_NAME+methodName);
			mcomCon=null;
			}
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting:method=");
				loggerValue.append(methodName);
				log.debug(methodName, loggerValue);
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param model
	 * @param usersReportModel
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	@Override
	public String downloadAddCommDetailsCSVReportFile(Model model, UsersReportModel usersReportModel) throws SQLException {
		String methodName = "downloadCSVReportFile";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) 
			log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);

		
		Connection con = null;
		MComConnectionI mcomCon = null;
		String filePath = null;
		try{
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			DownloadCSVReports downloadCSVReports = new DownloadCSVReports();
			filePath = downloadCSVReports.prepareData(usersReportModel, usersReportModel.getrptCode(), con);	
		}catch(BTSLBaseException e){
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(EXCEPTION_KEY);
				loggerValue.append(e.getMessage());
				log.debug(methodName,  loggerValue );
			}
			log.errorTrace(CLASS_NAME+methodName, e);
			model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
			return null;
		}
		catch (InterruptedException e) 
		{
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(EXCEPTION_KEY);
				loggerValue.append(e.getMessage());
				log.debug(methodName,  loggerValue );
			}
			log.errorTrace(CLASS_NAME+methodName, e);
		}
		finally{
			if(mcomCon != null){
				mcomCon.close("AdditionalCommissionDetailsServiceImpl#"+methodName);
				mcomCon=null;
			}
			if (log.isDebugEnabled()) {
				
				log.debug(CLASS_NAME+methodName, PretupsI.EXITED);

			}
		}
		return filePath;
	}
}
