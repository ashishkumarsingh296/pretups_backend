package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO;
import com.btsl.pretups.channel.reports.businesslogic.OperationSummaryReportDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.DownloadCSVReports;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;

/**
 * @author anubhav.pandey1
 *
 */
@Service
public class OperationSummaryReportServiceImpl implements OperationSummaryReportService {

	
	private static final String FAIL_KEY = "fail";
	
	private static final Log log = LogFactory.getLog(OperationSummaryReportServiceImpl.class.getName());
	private static final String USERSREPORTFORM = "UsersReportForm";
	private static final String SESSIONUSERFORM = "SessionUserForm";
	private static final String LISTALL = "list.all";
	@Override
	public UsersReportModel loadOperationSummaryReport(HttpServletRequest request, UserVO userVO) {
		
		final String methodName = "loadOperationSummaryReport";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		
        final ArrayList<ListValueVO> loggedInUserDomainList = new ArrayList<>();
        Connection con = null;
        MComConnectionI mcomCon = null;

        final UsersReportModel thisForm = new UsersReportModel();
        try {
        	
            thisForm.setRadioNetCode(PretupsI.ACCOUNT_TYPE_MAIN);
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            
            this.loadingSummaryDetails(thisForm,userVO, con, loggedInUserDomainList);
            
        } catch (Exception e) {
          
            log.errorTrace(methodName, e);
            
        } finally {
			if (mcomCon != null) {
				mcomCon.close("UserBalanceReportAction#loadUserSummary");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
			    log.debug(methodName, "Exit");
			}
        }
		
		return  thisForm;
		
		
	}
	
	
	private void loadingSummaryDetails(UsersReportModel thisForm, UserVO userVO,Connection con, ArrayList<ListValueVO> loggedInUserDomainList) {
		
		final String methodName = "loadingSummaryDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
        CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
        
       
            try {

                if(TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode()))
               	 thisForm.setZoneList(new GeographicalDomainDAO().loadUserGeographyList(con, userVO.getUserID(), userVO
        					.getNetworkID()));
               else 
                thisForm.setZoneList(userVO.getGeographicalAreaList());
                thisForm.setDomainList(BTSLUtil.displayDomainList(userVO.getDomainList()));
                int loginSeqNo = userVO.getCategoryVO().getSequenceNumber();
                thisForm.setCategorySeqNo(Integer.toString(loginSeqNo));
                thisForm.setUserType(userVO.getUserType());
                if (userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
                    thisForm.setParentCategoryList(categoryWebDAO.loadCategoryReportList(con));
                } else {
                    thisForm.setParentCategoryList(categoryWebDAO.loadCategoryReporSeqtList(con, loginSeqNo));
                }

                thisForm.setLoginUserID(userVO.getUserID());
                thisForm.setLoggedInUserCategoryCode(userVO.getCategoryVO().getCategoryCode());
                if (userVO.isStaffUser()) {
                    thisForm.setLoggedInUserName(userVO.getParentName());
                } else {
                    thisForm.setLoggedInUserName(userVO.getUserName());
                }
		        this.commonUserList(thisForm, loggedInUserDomainList, userVO);
		        this.commonGeographicDetails(thisForm);
				
			} catch (BTSLBaseException e) {
				
				log.errorTrace(methodName, e);
			}
       
           
        }


	


	private void commonGeographicDetails(UsersReportModel thisForm) {
        ArrayList<?> zoneList = thisForm.getZoneList();
        UserGeographiesVO geographyVO;
        ArrayList<ListValueVO> geoList = new ArrayList<>();

        for (int i = 0, k = zoneList.size(); i < k; i++) {
            geographyVO = (UserGeographiesVO) zoneList.get(i);
            geoList.add(new ListValueVO(geographyVO.getGraphDomainName(), geographyVO.getGraphDomainCode()));
        }
        if (geoList.size() == 1) {
            ListValueVO listValueVONew = geoList.get(0);
            thisForm.setZoneCode(listValueVONew.getValue());
            thisForm.setZoneName(listValueVONew.getLabel());
            thisForm.setZoneList(geoList);
        } else {
            thisForm.setZoneList(geoList);
        }
		
	}


	private void commonUserList(UsersReportModel thisForm,	ArrayList<ListValueVO> loggedInUserDomainList, UserVO userVO) {
       
           if (thisForm.getDomainListSize() == 0) {
            loggedInUserDomainList.add(new ListValueVO(userVO.getDomainName(), userVO.getDomainID()));
            thisForm.setDomainList(loggedInUserDomainList);
            thisForm.setDomainCode(userVO.getDomainID());
            thisForm.setDomainName(userVO.getDomainName());
        } else if (thisForm.getDomainListSize() == 1) {
            ListValueVO listvo = (ListValueVO) thisForm.getDomainList().get(0);
            thisForm.setDomainCode(listvo.getValue());
            thisForm.setDomainName(listvo.getLabel());
        }
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ChannelUserTransferVO> loadUserList(UserVO userVO, String parentCategoryCode,	String domainList, String zoneList, String userName){
        final String methodName = "loadUserList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.ENTERED);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserReportDAO channelUserDAO = new ChannelUserReportDAO();
        List<ChannelUserTransferVO> userList = new ArrayList<>();
        try {

            
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final String[] arr = parentCategoryCode.split("\\|");

            
            if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                if (parentCategoryCode.equals(PretupsI.ALL)) {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategoryHierarchy(con, PretupsI.ALL, domainList, zoneList, userName,
                        userVO.getUserID());
                } else {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategoryHierarchy(con, arr[1], domainList, zoneList, userName, userVO
                        .getUserID());
                }
            } else {
                if (parentCategoryCode.equals(PretupsI.ALL)) {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategory(con, PretupsI.ALL, domainList, zoneList, userName, userVO
                        .getUserID());
                } else {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategory(con, arr[1], domainList, zoneList, userName, userVO
                        .getUserID());
                }
            }
            


        } catch (Exception e) {
            log.error(methodName, "Exceptin:e=" + e);
            log.errorTrace(methodName, e);
        } finally {
        	if(mcomCon != null){
        		mcomCon.close("OperationSummaryReportServiceImpl#loadUserList");
        	mcomCon=null;
        	}

        }
		
		return userList;
		
		
		
	}


	@SuppressWarnings("unchecked")
	@Override
	public boolean displayOperationSummaryReport(Model model, UserVO userVO,UsersReportModel usersReportModel,UsersReportModel sessionUserReportForm, HttpServletRequest request,HttpServletResponse response, BindingResult bindingResult) {
		
		
        final String methodName = "displayOperationSummaryReport";
        if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.ENTERED);
        }
        
        Connection con = null;
        MComConnectionI mcomCon = null;
        ArrayList<ChannelUserTransferVO> userList = new ArrayList<>();
        String tempCatCode = "";
        
        try {
        	

            CommonValidator commonValidator=new CommonValidator("configfiles/c2sreports/validation-operationSummaryReport.xml", usersReportModel, USERSREPORTFORM);
    		Map<String, String> errorMessages;
            errorMessages = commonValidator.validateModel();
			PretupsRestUtil pru=new PretupsRestUtil();
    		pru.processFieldError(errorMessages, bindingResult);

    		
    		
        	setSessionUserReportForm(userVO, usersReportModel,	sessionUserReportForm);
    		if(bindingResult.hasFieldErrors()){
    			
      			 request.getSession().setAttribute(SESSIONUSERFORM, sessionUserReportForm);
      			return false;
      		} 
        	
        	
            final UsersReportModel thisForm = sessionUserReportForm;
            final java.util.Date frDate = BTSLUtil.getDateFromDateString(thisForm.getFromDate());
            final java.util.Date tDate = BTSLUtil.getDateFromDateString(thisForm.getToDate());

            final String fromdate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(frDate));
            final String todate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(tDate));

            
			if(request.getParameter("iNETReport")!= null)
			{
				thisForm
				  .setRptfromDate(BTSLUtil.reportDateFormat(fromdate));
				thisForm.setRpttoDate(BTSLUtil.reportDateFormat(todate));
			}
			else
			{
				thisForm.setRptfromDate(usersReportModel.getFromDate());
				 
				thisForm.setRpttoDate(usersReportModel.getToDate());
			}

           
            thisForm.setNetworkCode(userVO.getNetworkID());
            thisForm.setNetworkName(userVO.getNetworkName());
            thisForm.setReportHeaderName(userVO.getReportHeaderName());
            final ChannelUserVO channelUserVO = (ChannelUserVO) userVO;
            tempCatCode = thisForm.getParentCategoryCode();
           
            if (!BTSLUtil.checkDateFromMisDates(frDate, tDate, channelUserVO, ProcessI.C2SMIS)) {
                final String[] arr = { BTSLUtil.getDateStringFromDate(channelUserVO.getC2sMisFromDate()), BTSLUtil.getDateStringFromDate(channelUserVO.getC2sMisToDate()) };
                
    			model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.date.error.msg",arr));
    			return false;
            }
            
            int diff = BTSLUtil.getDifferenceInUtilDates(frDate, tDate);
            if (diff > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_REPORT_MAX_DATEDIFF))).intValue()) {
                  model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("btsl.date.error.datecompare", new String[] { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_REPORT_MAX_DATEDIFF))).intValue()) }));
                  request.getSession().setAttribute(SESSIONUSERFORM, thisForm);
                  return false;
            }


            ListValueVO listValueVO = null;

            if (thisForm.getZoneCode().equals(TypesI.ALL)) {
                thisForm.setZoneName(PretupsRestUtil.getMessageString(PretupsRestUtil.getMessageString(LISTALL)));
            } else {
                listValueVO = BTSLUtil.getOptionDesc(thisForm.getZoneCode(), thisForm.getZoneList());
                thisForm.setZoneName(listValueVO.getLabel());
            }

            listValueVO = BTSLUtil.getOptionDesc(thisForm.getDomainCode(), thisForm.getDomainList());
            thisForm.setDomainName(listValueVO.getLabel());

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final ChannelUserReportDAO channelUserDAO = new ChannelUserReportDAO();

            listValueVO = BTSLUtil.getOptionDesc(thisForm.getParentCategoryCode(), thisForm.getParentCategoryList());
            thisForm.setCategoryName(listValueVO.getLabel());
            final String[] arr = thisForm.getParentCategoryCode().split("\\|");
            thisForm.setParentCategoryCode(arr[1]);
            thisForm.setAgentCatCode(thisForm.getParentCategoryCode() + "A");
            userList = channelUserDAO.loadUserListBasisOfZoneDomainCategory(con, arr[1], thisForm.getDomainCode(), thisForm.getZoneCode(), thisForm.getUserName(), userVO
                .getUserID());
            boolean flag = true;
            
            if(thisForm.getUserName().equals(PretupsRestUtil.getMessageString(LISTALL))){
            	thisForm.setUserID(PretupsI.ALL);
            	
            } else if (userList == null || userList.isEmpty()) {
                thisForm.setParentCategoryCode(tempCatCode);
    			model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("user.selectcategoryforedit.error.usernotexist"));
    			return false;
                } else if (userList != null && userList.size() == 1) {
                final ChannelUserTransferVO channelUserTransferVO = userList.get(0);
                thisForm.setUserName(channelUserTransferVO.getUserName());
                thisForm.setUserID(channelUserTransferVO.getUserID());
                
            } else if (userList != null && userList.size() > 1) {

                
                if (!BTSLUtil.isNullString(thisForm.getUserID())) {
                    for (int i = 0, j = userList.size(); i < j; i++) {
                        final ChannelUserTransferVO channelUserTransferVO =userList.get(i);
                        if (thisForm.getUserID().equals(channelUserTransferVO.getUserID()) && thisForm.getUserName().equalsIgnoreCase(channelUserTransferVO.getUserName())) {
                            thisForm.setUserName(channelUserTransferVO.getUserName());
                            flag = false;
                             break;
                        }
                    }
                }
                if (flag) {
                    thisForm.setParentCategoryCode(tempCatCode);
        			model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("user.selectcategoryforedit.error.usermorethanone"));
        			return false;
                }
            }
            if(request.getParameter("iNETReport")!= null){
            	model.addAttribute(USERSREPORTFORM, thisForm);
            	request.getSession().setAttribute(SESSIONUSERFORM, thisForm);
            	return true;
            }
            
           OperationSummaryReportDAO  operationSummaryReportDAO = new OperationSummaryReportDAO();
           if((PretupsI.ACCOUNT_TYPE_MAIN).equals(thisForm.getRadioNetCode()) && (PretupsI.CHANNEL_USER_TYPE).equals(userVO.getUserType()))
           {
           	String rptCode = "OPSUMMAIN01";
           	final ArrayList<ChannelTransferVO> operationSummaryList = operationSummaryReportDAO.loadOperationSummaryChannelUserMainReport(con,thisForm);
           	thisForm.setTransferList(operationSummaryList);
           	thisForm.setTransferListSize(operationSummaryList.size());           	
           	thisForm.setrptCode(rptCode);
           	
           }
           if((PretupsI.ACCOUNT_TYPE_TOTAL).equals(thisForm.getRadioNetCode()) && (PretupsI.CHANNEL_USER_TYPE).equals(userVO.getUserType()))
           {
           	String rptCode = "OPSUMTOTAL02";
           	final ArrayList<ChannelTransferVO> operationSummaryList = operationSummaryReportDAO.loadOperationSummaryChannelUserTotalReport(con,thisForm);
           	thisForm.setTransferList(operationSummaryList);
           	thisForm.setTransferListSize(operationSummaryList.size());           	
           	thisForm.setrptCode(rptCode);
           	
           }
           if((PretupsI.ACCOUNT_TYPE_MAIN).equals(thisForm.getRadioNetCode()) && (PretupsI.OPERATOR_USER_TYPE).equals(userVO.getUserType()))
           {
           	String rptCode = "O2STRSUMRY02";
           	final ArrayList<ChannelTransferVO> operationSummaryList = operationSummaryReportDAO.loadOperationSummaryOperatorMainReport(con,thisForm);
           	thisForm.setTransferList(operationSummaryList);
           	thisForm.setTransferListSize(operationSummaryList.size());
           	thisForm.setrptCode(rptCode);
           	
           }
           if((PretupsI.ACCOUNT_TYPE_TOTAL).equals(thisForm.getRadioNetCode()) && (PretupsI.OPERATOR_USER_TYPE).equals(userVO.getUserType()))
           {
           	String rptCode = "O2STRSUMRY01";
           	final ArrayList<ChannelTransferVO> operationSummaryList = operationSummaryReportDAO.loadOperationSummaryOperatorTotalReport(con,thisForm);
           	thisForm.setTransferList(operationSummaryList);
           	thisForm.setTransferListSize(operationSummaryList.size());           	
           	thisForm.setrptCode(rptCode);
           	
           }
  		 request.getSession().setAttribute(SESSIONUSERFORM, thisForm);
  		 model.addAttribute(USERSREPORTFORM, thisForm);
        } catch (BTSLBaseException |ParseException |SQLException | ValidatorException | IOException | SAXException e) {
            log.error(methodName, "Exceptin:e=" + e);
            log.errorTrace(methodName, e);
           
        }  finally {
			if (mcomCon != null) {
				mcomCon.close("OperationSummaryReportServiceImpl#displayOperationSummaryReport");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
			    log.debug(methodName, "Exited");
			}
        }
       
		return true;
	}




	private void setSessionUserReportForm(UserVO userVO,UsersReportModel usersReportModel,UsersReportModel sessionUserReportForm) {
		sessionUserReportForm.setZoneCode(usersReportModel.getZoneCode());
		sessionUserReportForm.setDomainCode(usersReportModel.getDomainCode());
		sessionUserReportForm.setParentCategoryCode(usersReportModel.getParentCategoryCode());
		String userName = usersReportModel .getUserName();
		if (userName.equalsIgnoreCase(PretupsRestUtil.getMessageString(LISTALL))) {
			sessionUserReportForm.setUserID(PretupsI.ALL);
		}
		if (!BTSLUtil.isNullString(userName)) {
			String[] parts = userName.split("\\(");
			userName = parts[0];
			sessionUserReportForm.setUserName(userName);
		    String userID = null;
			if(parts.length > 1){
				userID = parts[1];
				userID = userID.replaceAll("\\)", "");

				}
			if(userID == null){
				userID = userVO.getUserID();
			}
			sessionUserReportForm.setUserID(userID);
		}
		

		sessionUserReportForm.setRadioNetCode(usersReportModel.getRadioNetCode());
		sessionUserReportForm.setFromDate(usersReportModel.getFromDate());
		sessionUserReportForm.setToDate(usersReportModel.getToDate());
	}


	@Override
	public String downloadFileforSumm(UsersReportModel usersReportModel) {
		
		final String methodName = "downloadFileforSumm";
		DownloadCSVReports downloadCSVReports = new DownloadCSVReports();
		Connection con ;
		MComConnectionI mcomCon ;
		String filePath = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			String rptCode = usersReportModel.getrptCode();
			
			filePath = downloadCSVReports.prepareData(usersReportModel, rptCode,
					con);
		} catch (BTSLBaseException |SQLException|InterruptedException e) {
			 log.errorTrace(methodName, e);
		}


		return filePath;
	}
	
	

}
