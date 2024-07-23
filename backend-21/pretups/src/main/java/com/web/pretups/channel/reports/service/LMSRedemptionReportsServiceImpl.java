package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//import org.apache.struts.action.ActionForward;
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
import com.btsl.pretups.channel.reports.businesslogic.LmsRedemptionRetWidRptDAO;
import com.btsl.pretups.channel.userreturn.service.ChnnlToChnnlReturnWithdrawServiceImpl;
import com.btsl.pretups.common.DownloadLMSReports;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.lms.businesslogic.LmsRedemptionDetailsVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.ActivationBonusLMSDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.profile.businesslogic.ActivationBonusWebDAO;
import com.web.pretups.channel.reports.web.LmsRedemptionReportModel;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;


/**
 * 
 * @author sweta.verma
 *
 */
@Service("LMSRedemptionReportsService")
public class LMSRedemptionReportsServiceImpl implements LMSRedemptionReportsService {
	public static final Log log = LogFactory.getLog(ChnnlToChnnlReturnWithdrawServiceImpl.class.getName());
		private static final String FAIL_KEY = "fail";
		private static final String PANEL_NO = "PanelNo";
	
	@Override
	public Boolean loadLmsRedemptionReportPage(UserVO userVO,
			LmsRedemptionReportModel lmsRedemptionReportModel, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		 	Connection con = null;
	        MComConnectionI mcomCon = null;
	        CategoryWebDAO categoryWebDAO = null;
	        ListValueVO listValueVO = null;
	        ArrayList geoList = null;
	        ArrayList zoneList = null;
	        UserGeographiesVO geographyVO = null;
	        ArrayList loggedInUserDomainList = null;
	        ArrayList profileList = null;
	        ActivationBonusLMSDAO lmsDAO = null;
	        Iterator profileListItr = null;
	        BTSLCommonController btslCommonController = new BTSLCommonController();
	        try {
	            categoryWebDAO = new CategoryWebDAO();
	            loggedInUserDomainList = new ArrayList();
	            //userVO = btslCommonController.getUserFormSession(request);
	            mcomCon = new MComConnection();
	            con=mcomCon.getConnection();
	            lmsRedemptionReportModel.setZoneList(userVO.getGeographicalAreaList());
	            loggedInUserDomainList = BTSLUtil.displayDomainList(userVO.getDomainList());
			
			lmsRedemptionReportModel.setRedemptionTypeList(LookupsCache.loadLookupDropDown(PretupsI.LMS_REDEMP_TYPE, true));
			
			if ((loggedInUserDomainList == null || loggedInUserDomainList.isEmpty())) {
	            loggedInUserDomainList.add(new ListValueVO(userVO.getDomainName(), userVO.getDomainID()));
	            lmsRedemptionReportModel.setDomainList(loggedInUserDomainList);
	            lmsRedemptionReportModel.setDomainCode(userVO.getDomainID());
	            lmsRedemptionReportModel.setDomainName(userVO.getDomainName());
	        } else if (loggedInUserDomainList.size() == 1) {
	            listValueVO = (ListValueVO) loggedInUserDomainList.get(0);
	            lmsRedemptionReportModel.setDomainCode(listValueVO.getValue());
	            lmsRedemptionReportModel.setDomainName(listValueVO.getLabel());
	        }
	        lmsRedemptionReportModel.setLoginUserID(userVO.getUserID());
	        lmsRedemptionReportModel.setDomainList(loggedInUserDomainList);
	        lmsRedemptionReportModel.setUserType(userVO.getUserType());
	        
	        if ((PretupsI.OPERATOR_USER_TYPE).equals(userVO.getUserType())) {
	            lmsRedemptionReportModel.setCategoryList(categoryWebDAO.loadCategoryReportList(con));
	        } else if ((PretupsI.CHANNEL_USER_TYPE).equals(userVO.getUserType())) {
	            final ArrayList catList = new ArrayList();
	            catList.add(new ListValueVO(userVO.getCategoryVO().getCategoryName(), userVO.getDomainID() + ":" + userVO.getCategoryVO().getSequenceNumber() + "|" + userVO
	                            .getCategoryCode()));
	            lmsRedemptionReportModel.setCategoryList(catList);
	            lmsRedemptionReportModel.setUserID(userVO.getUserID());
	            lmsRedemptionReportModel.setUserName(userVO.getUserName());
	            lmsRedemptionReportModel.setCategoryCode(userVO.getDomainID() + ":" + userVO.getCategoryVO().getSequenceNumber() + "|" + userVO.getCategoryCode());
	            lmsRedemptionReportModel.setCategoryName(userVO.getCategoryVO().getCategoryName());
	        }
	        zoneList = lmsRedemptionReportModel.getZoneList();
	        geoList = new ArrayList();
	        for (int i = 0, k = zoneList.size(); i < k; i++) {
	            geographyVO = (UserGeographiesVO) zoneList.get(i);
	            geoList.add(new ListValueVO(geographyVO.getGraphDomainName(), geographyVO.getGraphDomainCode()));
	        }
	        if (geoList.size() == 1) {
	            listValueVO = (ListValueVO) geoList.get(0);
	            lmsRedemptionReportModel.setZoneCode(listValueVO.getValue());
	            lmsRedemptionReportModel.setZoneName(listValueVO.getLabel());
	        }
	        lmsRedemptionReportModel.setZoneList(geoList);
	    } catch (Exception e) {
	        log.errorTrace("loadLmsRedemptionReport", e);
	        
	    }
	        finally {
				if (mcomCon != null) {
					mcomCon.close("LmsReportsAction#loadReportDetails");
					mcomCon = null;
				}
	            
	        }
	        return true;
	}
	
	
	@Override
	public boolean displayLMSRedemptionDetailsReportPage(UserVO userVO,
			LmsRedemptionReportModel lmsRedemptionReportModel, Model model,
			BindingResult bindingResult, HttpServletRequest request)
			throws IOException {
		final String METHOD_NAME = "lmsUserRedemptionReport";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        //ActionForward forward = null;
        ListValueVO listValueVO = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        
        
        ActivationBonusWebDAO activationBonusWebDAO = null;
        ArrayList userList = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        try {
            channelUserWebDAO = new ChannelUserWebDAO();
            final LmsRedemptionReportModel thisForm = lmsRedemptionReportModel;
            thisForm.setNetworkCode(userVO.getNetworkID());
            thisForm.setNetworkName(userVO.getNetworkName());
            thisForm.setReportHeaderName(userVO.getReportHeaderName());
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            
            if ((request.getParameter("submitButtonForMobNum") != null)||(request.getParameter("iNETReportPanelOne") != null)) {
        		CommonValidator commonValidator = new CommonValidator(
						"configfiles/lmsreports/validation-lmsRedemptionReports.xml", lmsRedemptionReportModel,
						"lmsRedemptionFormByMob");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				request.getSession().setAttribute(PANEL_NO, "Panel-One");
        		
        	}
            if ((request.getParameter("submitButtonForLoginId") != null)||(request.getParameter("iNETReportPanelTwo") != null)) {
        		CommonValidator commonValidator = new CommonValidator(
						"configfiles/lmsreports/validation-lmsRedemptionReports.xml", lmsRedemptionReportModel,
						"lmsRedemptionReportByUserName");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				request.getSession().setAttribute(PANEL_NO, "Panel-Two");
        	}
            if ((request.getParameter("submitButtonForCategory") != null)||(request.getParameter("iNETReportPanelThree") != null)) {
            	CommonValidator commonValidator = new CommonValidator(
						"configfiles/lmsreports/validation-lmsRedemptionReports.xml", lmsRedemptionReportModel,
						"lmsRedemptionReportByCategory");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				request.getSession().setAttribute(PANEL_NO, "Panel-Three");
            }
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            ChannelUserVO channelUserVO1 = new ChannelUserVO();
            ChannelUserVO channelUserVO = null;
            Date frDate = null;
            java.util.Date tDate = null;
            if (!BTSLUtil.isNullString(thisForm.getRedemptionType())) {
                thisForm.setRedemptionType(thisForm.getRedemptionType());
            }
            if (!BTSLUtil.isNullString(thisForm.getFromDate())) {
                //frDate = BTSLUtil.getDateFromDateString(thisForm.getFromDate());
              //  final String fromdate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(frDate));
                frDate = BTSLUtil.getDateFromDateString(thisForm.getFromDate());
                
              //  final String fromdate = BTSLUtil.getDateFromDateString(thisForm.getFromDate() + " 00:00:00", PretupsI.TIMESTAMP_DATESPACEHHMMSS);
                thisForm.setRptfromDate(BTSLUtil.getSQLDateFromUtilDate(frDate));
            }
            if (!BTSLUtil.isNullString(thisForm.getToDate())) {
                tDate = BTSLUtil.getDateFromDateString(thisForm.getToDate());
                //final String todate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(tDate));
                thisForm.setRpttoDate(BTSLUtil.getSQLDateFromUtilDate(tDate));
            }
            if (BTSLUtil.isNullString(thisForm.getMsisdn()) && BTSLUtil.isNullString(thisForm.getLoginID())  &&  request.getParameter(PANEL_NO).equals("Panel-Three") ) {
                if (BTSLUtil.isNullString(thisForm.getZoneCode())) {
                	 model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("lms.reports.lmspointadjustment.label.zone.required"));
     				return false;
                } else if (BTSLUtil.isNullString(thisForm.getCategoryCode())) {
                	model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("lms.reports.bonuspoint.label.categtory.required"));
     				return false;
                } else if (BTSLUtil.isNullString(thisForm.getDomainCode())) {
                	model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("lms.reports.bonuspoint.label.domain.required"));
     				return false;
                    
                } else if (BTSLUtil.isNullString(thisForm.getUserName())) {
                	model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("lms.reports.bonuspoint.label.user.required"));
     				return false;

                }
            }

            if (!BTSLUtil.isNullString(thisForm.getMsisdn())) {
                if (BTSLUtil.isValidMSISDN(thisForm.getMsisdn())) {
                    channelUserVO = channelUserDAO.loadChannelUserDetails(con, thisForm.getMsisdn());
                    if (channelUserVO == null) {
                    	model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("bonuses.lms.reports.msg.error.invalid.msisdn"));
         				return false;
                    } else {
                        channelUserVO1 = channelUserWebDAO.parentGeographyDetails(con, channelUserVO.getGeographicalCode());
                        thisForm.setDomainCode(channelUserVO.getDomainID());
                        thisForm.setDomainName(channelUserVO.getDomainName());
                        thisForm.setCategoryCode(channelUserVO.getCategoryCode());
                        thisForm.setCategoryName(channelUserVO.getCategoryVO().getCategoryName());
                        thisForm.setUserName(channelUserVO.getUserName());
                        thisForm.setUserID(channelUserVO.getUserID());
                        if (channelUserVO1 != null) {
                            // LMS redemption report not getting generated based
                            // on MSISDN
                            // thisForm.setZoneCode(channelUserVO1.getParentGeographyCode());
                            // thisForm.setZoneName(channelUserVO1.getGeographicalDesc());
                            thisForm.setZoneCode("ALL");
                            thisForm.setZoneName("ALL");
                        } else {
                        	model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("bonuses.lms.reports.msg.error.parent.geo.notfound"));
             				return false;
                        }

                    }

                } else {
                	model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("bonuses.lms.reports.msg.error.invalid.msisdn"));
     				return false;
                }
            } else if (!BTSLUtil.isNullString(thisForm.getLoginID())) {

                channelUserVO = channelUserWebDAO.loadChannelUserDetailsByLoginIDANDORMSISDN(con, thisForm.getMsisdn(), thisForm.getLoginID());
                if (channelUserVO == null) {
                	model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("bonuses.lms.reports.msg.error.invalid.loginID"));
     				return false;
                } else {
                    channelUserVO1 = channelUserWebDAO.parentGeographyDetails(con, channelUserVO.getGeographicalCode());
                    thisForm.setDomainCode(channelUserVO.getDomainID());
                    thisForm.setDomainName(channelUserVO.getDomainID());
                    thisForm.setCategoryCode(channelUserVO.getCategoryCode());
                    thisForm.setCategoryName(channelUserVO.getCategoryVO().getCategoryName());
                    thisForm.setUserName(channelUserVO.getUserName());
                    thisForm.setUserID(channelUserVO.getUserID());
                    if (channelUserVO1 != null) {
                        // LMS redemption report not getting generated based on
                        // MSISDN
                        // thisForm.setZoneCode(channelUserVO1.getParentGeographyCode());
                        // thisForm.setZoneName(channelUserVO1.getGeographicalDesc());
                        thisForm.setZoneCode("ALL");
                        thisForm.setZoneName("ALL");
                    } else {
                    	model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("bonuses.lms.reports.msg.error.invalid.loginID"));
         				return false;
                    }

                }

            }

            else {
                if (!BTSLUtil.isNullString(thisForm.getDomainCode()) && thisForm.getDomainCode().equals(TypesI.ALL)) {
                    thisForm.setDomainName(PretupsRestUtil.getMessageString("list.all"));
                    String domain = "";
                    for (int i = 0, j = thisForm.getDomainList().size(); i < j; i++) {
                        listValueVO = (ListValueVO) thisForm.getDomainList().get(i);
                        domain = domain + listValueVO.getValue() + "','";
                    }
                    domain = domain.substring(0, domain.length() - 3);
                    thisForm.setDomainCode(domain);
                } else if (!BTSLUtil.isNullString(thisForm.getDomainCode())) {
                    listValueVO = BTSLUtil.getOptionDesc(thisForm.getDomainCode(), thisForm.getDomainList());
                    thisForm.setDomainName(listValueVO.getLabel());
                }

                if (thisForm.getZoneCode().equals(TypesI.ALL)) {
                    thisForm.setZoneName(PretupsRestUtil.getMessageString("list.all"));
                } else {
                    listValueVO = BTSLUtil.getOptionDesc(thisForm.getZoneCode(), thisForm.getZoneList());
                    thisForm.setZoneName(listValueVO.getLabel());
                }

                if (!BTSLUtil.isNullString(thisForm.getCategoryCode()) && thisForm.getCategoryCode().equals(TypesI.ALL)) {
                    thisForm.setCategoryCode(TypesI.ALL);
                    thisForm.setCategoryName(PretupsRestUtil.getMessageString("list.all"));
                } else if (!BTSLUtil.isNullString(thisForm.getCategoryCode())) {
                    listValueVO = BTSLUtil.getOptionDesc(thisForm.getCategoryCode(), thisForm.getCategoryList());
                    thisForm.setCategoryName(listValueVO.getLabel());
                    final String cat = thisForm.getCategoryCode();
                    final String category[] = cat.split("\\|");
                    thisForm.setCategoryCode(category[1]);
                }
                // //////////////////////// new validation for Hand written user
                // name
                if (!("activatedSummary".equals(thisForm.getRequestType()))) {
                    activationBonusWebDAO = new ActivationBonusWebDAO();
                    userList = new ArrayList();

                    if (!("ALL".equalsIgnoreCase(thisForm.getUserName()))) { // Bug
                        // Fixed
                        // by
                        // ashishT
                        // ...
                        // user
                        // no
                        // found
                        // issue
                        // userList=activationBonusDAO.validateUserdetails(con,
                        // thisForm.getZoneCode(),
                        // thisForm.getDomainCode(),
                        // thisForm.getCategoryCode(),
                        // thisForm.getUserName());
                    	String[] userName = thisForm.getUserName().split("\\(");
                        userList = activationBonusWebDAO.validateUserdetails(con, thisForm.getZoneCode(), thisForm.getDomainCode(), thisForm.getCategoryCode(), userName[0], userVO.getUserID());
                    }

                    if (!BTSLUtil.isNullString(thisForm.getUserName()) && thisForm.getUserName().equals(
                    		PretupsRestUtil.getMessageString("list.all"))) {
                        thisForm.setUserID(PretupsI.ALL);

                    } else if (!BTSLUtil.isNullString(thisForm.getUserName()) && userList == null ||userList.isEmpty()) {
                    	model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("redemption.lms.reports.msg.error.invalid.username.notexist"));
         				return false;
                    } else if (!BTSLUtil.isNullString(thisForm.getUserName()) && userList.size() == 1) {
                        final ListValueVO channelUserTransferVO = (ListValueVO) userList.get(0);
                        thisForm.setUserName(channelUserTransferVO.getLabel());
                        thisForm.setUserID(channelUserTransferVO.getValue());
                    } else if (!BTSLUtil.isNullString(thisForm.getUserName()) && userList.size() > 1) {
                        boolean flag = true;
                        if (!BTSLUtil.isNullString(thisForm.getUserName())) {
                            for (int i = 0, j = userList.size(); i < j; i++) {
                                final ListValueVO channelUserTransferVO = (ListValueVO) userList.get(i);
                                if (thisForm.getUserName().equals(channelUserTransferVO.getLabel())) {
                                    thisForm.setUserID(channelUserTransferVO.getValue());
                                    flag = false;
                                    break;
                                    
                                }
                            }
                        }
                        if (flag) {
                        	model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("redemption.lms.reports.msg.error.invalid.username.moreexist"));
             				return false;
                        }
                        return flag;
                    }
                }
            }

            // end of validation block
            
            
            	
            
            
            
            
           /* if ("activatedSummary".equals(thisForm.getRequestType()))// request
            // for add
            {

                forward = mapping.findForward("activatedSummaryDetails");
            } else {
                forward = mapping.findForward("lmsUserRedemptionReport");
            }*/
            	
            	String rptCode = null;
            	String rptFile ="";
                LmsRedemptionRetWidRptDAO lmsRedemptionRetWidRptDAO = new LmsRedemptionRetWidRptDAO();
                
                				rptFile="lmsRedemption.rpt";
    							rptCode = "LMSRED001";
    							final ArrayList<LmsRedemptionDetailsVO> LmsRedemptionDetailsVOList = lmsRedemptionRetWidRptDAO.loadLmsRedemptionDataList(con, lmsRedemptionReportModel);
    				            thisForm.setLmsRedemptionDetailsVoList(LmsRedemptionDetailsVOList);
    				            thisForm.setRptCode(rptCode);
    	    		
                thisForm.setLmsRedemptionDetailsListSize( thisForm.getLmsRedemptionDetailsVoList().size());
            	
            	
        } catch (Exception e) {
        	log.errorTrace(METHOD_NAME, e);
            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
            return false;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("LmsReportsAction#lmsUserRedemptionReport");
				mcomCon = null;
			}
            /*if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting: forward=" + forward);
            }*/
        }
        
    

        return true;
	
	}
	
	@Override
	public String downloadLmsReport(LmsRedemptionReportModel lmsRedemptionReportModel) throws BTSLBaseException, SQLException, InterruptedException {
		DownloadLMSReports downloadLMSReports = new  DownloadLMSReports();
		 Connection con ;
	        MComConnectionI mcomCon;
	    mcomCon = new MComConnection();
	            con=mcomCon.getConnection();
	            String rptCode="LMSRED001";
 
	            return downloadLMSReports.prepareData(lmsRedemptionReportModel,rptCode, con);

	}
	
	
	

}
