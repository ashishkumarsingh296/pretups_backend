package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

////import org.apache.struts.action.ActionForward;
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
import com.btsl.pretups.channel.reports.businesslogic.Channel2ChannelTransferRetWidRptDAO;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.userreturn.service.ChnnlToChnnlReturnWithdrawServiceImpl;
import com.btsl.pretups.common.DownloadCSVReports;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

@Service("Channel2ChannelTransferReportsService")
public class Channel2ChannelTransferReportsServiceImpl implements Channel2ChannelTransferReportsService {
	public static final Log log = LogFactory.getLog(ChnnlToChnnlReturnWithdrawServiceImpl.class.getName());
	private static final String PANEL_NO="PanelNo";
	private static final String FAIL_KEY = "fail";
	private static final String ERROR_MSG ="Name selected did not had ID";

	public Boolean loadC2CTransferReportPage(UserVO userVO,UsersReportModel usersReportModel,Model model) throws  IOException{
		final String methodName="Channel2ChannelTransferReportsServiceImpl#loadC2CTransferReportPage";
		if (log.isDebugEnabled()) {
        	log.debug(methodName, PretupsI.ENTERED);
        }
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {

			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			BTSLCommonController btslCommonController =new BTSLCommonController();

			if (TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode()))
				usersReportModel.setZoneList(new GeographicalDomainDAO().loadUserGeographyList(con,userVO.getUserID(), userVO.getNetworkID()));
			else
				usersReportModel.setZoneList(userVO.getGeographicalAreaList());

			usersReportModel.setDomainList(BTSLUtil.displayDomainList(userVO.getDomainList()));
			usersReportModel.setTxnSubTypeList(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE, true));
			usersReportModel.setTransferCategoryList(LookupsCache.loadLookupDropDown(PretupsI.C2C_TRANSFER_TYPE, true));
			usersReportModel.setNetworkCode(userVO.getNetworkID());
			usersReportModel.setUserType(userVO.getUserType());
			usersReportModel.setCategorySeqNo(((userVO.getCategoryVO().getSequenceNumber()) + "").trim());

			ListValueVO listValueVO = null;
			btslCommonController.commonGeographicDetails(usersReportModel, listValueVO);
			usersReportModel.setLoggedInUserCategoryCode(userVO.getCategoryVO().getCategoryCode());
			if (userVO.isStaffUser()) {
				usersReportModel.setLoggedInUserName(userVO.getParentName());
			} else {
				usersReportModel.setLoggedInUserName(userVO.getUserName());
			}
			usersReportModel.setLoginUserID(userVO.getUserID());
			final ArrayList<ListValueVO> loggedInUserDomainList = new ArrayList<>();

			final ArrayList<ListValueVO> fromCatList = new ArrayList<>();
			final ArrayList<ListValueVO> toCatList = new ArrayList<>();
			ArrayList transferRulCatList = null;
			String domainCode = null;
			if (usersReportModel.getDomainListSize() == 0) {
				loggedInUserDomainList.add(new ListValueVO(userVO.getDomainName(), userVO.getDomainID()));
				usersReportModel.setDomainList(loggedInUserDomainList);
				usersReportModel.setDomainCode(userVO.getDomainID());
				usersReportModel.setDomainName(userVO.getDomainName());
				domainCode = usersReportModel.getDomainCode();

			} else if (usersReportModel.getDomainListSize() == 1) {
				listValueVO = (ListValueVO) usersReportModel.getDomainList().get(0);
				usersReportModel.setDomainCode(listValueVO.getValue());
				usersReportModel.setDomainName(listValueVO.getLabel());
				domainCode = listValueVO.getValue();
			}
			final ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
			transferRulCatList = channelTransferRuleWebDAO.loadChannelTransferRuleVOList(con, usersReportModel.getNetworkCode(), domainCode, PretupsI.TRANSFER_RULE_TYPE_CHANNEL);
			ChannelTransferRuleVO channelTransferRuleVO = null;
			boolean isForAllCategory = true;
			if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
				isForAllCategory = false;
				userVO.getCategoryCode();
			}
			if (isForAllCategory) {
				for (int i = 0, k = transferRulCatList.size(); i < k; i++) {
					channelTransferRuleVO = (ChannelTransferRuleVO) transferRulCatList.get(i);
					fromCatList.add(new ListValueVO(channelTransferRuleVO.getFromCategoryDes(), channelTransferRuleVO.getDomainCode() + ":" + channelTransferRuleVO
							.getFromCategory()));
					toCatList.add(new ListValueVO(channelTransferRuleVO.getToCategoryDes(), channelTransferRuleVO.getDomainCode() + ":" + channelTransferRuleVO
							.getFromCategory() + ":" + channelTransferRuleVO.getToCategory()));
					toCatList.add(new ListValueVO(channelTransferRuleVO.getToCategoryDes(),
							channelTransferRuleVO.getDomainCode() + ":" + PretupsI.ALL + ":" + channelTransferRuleVO.getToCategory()));
				}

			}

			else {
				for (int i = 0, k = transferRulCatList.size(); i < k; i++) {
					channelTransferRuleVO = (ChannelTransferRuleVO) transferRulCatList.get(i);
					if (channelTransferRuleVO.getFromSeqNo() >= userVO.getCategoryVO().getSequenceNumber()) {
						fromCatList.add(new ListValueVO(channelTransferRuleVO.getFromCategoryDes(), channelTransferRuleVO.getDomainCode() + ":" + channelTransferRuleVO
								.getFromCategory()));

					}
					if (channelTransferRuleVO.getFromSeqNo() >= userVO.getCategoryVO().getSequenceNumber()) {
						toCatList.add(new ListValueVO(channelTransferRuleVO.getToCategoryDes(), channelTransferRuleVO.getDomainCode() + ":" + channelTransferRuleVO
								.getFromCategory() + ":" + channelTransferRuleVO.getToCategory()));
						toCatList.add(new ListValueVO(channelTransferRuleVO.getToCategoryDes(),
								channelTransferRuleVO.getDomainCode() + ":" + PretupsI.ALL + ":" + channelTransferRuleVO.getToCategory()));
					}

				}
			}
			ListValueVO listValueVONext = null;
			for (int i = 0, j = fromCatList.size(); i < j - 1;) {
				listValueVO =  fromCatList.get(i);
				listValueVONext = fromCatList.get(i + 1);
				if (listValueVO.getValue().equals(listValueVONext.getValue())) {
					fromCatList.remove(i + 1);
					j--;
				} else {
					i++;
				}

			}
			final ArrayList<ListValueVO> tempToCat = new ArrayList<>();
			listValueVONext = null;
			boolean flag = false;
			for (int i = 0, j = toCatList.size(); i < j; i++) {
				listValueVO = toCatList.get(i);
				final String value = listValueVO.getValue();
				flag = true;
				for (int k = i + 1, l = toCatList.size(); k < l; k++) {
					listValueVONext = toCatList.get(k);
					final String dupValue = listValueVONext.getValue();
					if (value.equals(dupValue)) {
						flag = false;
						break;
					}
				}
				if (flag) {
					tempToCat.add(listValueVO);
				}
			}
			usersReportModel.setFromCategoryList(fromCatList);
			usersReportModel.setToCategoryList(tempToCat);

		} catch (BTSLBaseException e) {
            log.errorTrace(methodName, e);
            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
            return false;
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
            return false;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close(methodName);
        		mcomCon=null;
        	}
            if (log.isDebugEnabled()) {
                log.debug(methodName,PretupsI.EXITED);
            }
        }
      return true;
		
	}



	public boolean displayC2CTransferReportPage(UserVO userVO,UsersReportModel usersReportModel, Model model,BindingResult bindingResult,HttpServletRequest request)throws IOException {

        final String methodName = "Channel2ChannelTransferReportsServiceImpl#displayC2CTransferReportPage";
        if (log.isDebugEnabled()) {
            log.debug(methodName,PretupsI.ENTERED);
        }
        String rptCode = null;
        //ActionForward forward = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        ArrayList userList = null;
        ArrayList touserList = null;
        String tempfromCatCode = "";
        String temptoCatCode = "";
        ListValueVO listValueVO = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        BTSLCommonController btslCommonController =new BTSLCommonController();
        try {
        	if ((request.getParameter("submitButtonForMsisdn") != null)||(request.getParameter("iNETReportPanelOne") != null)) {
        		CommonValidator commonValidator = new CommonValidator(
						"configfiles/c2sreports/validation-Channel2ChannelReports.xml", usersReportModel,
						"Channel2ChannelTrasRetWidRptMsisdn");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
        		model.addAttribute(PANEL_NO, "Panel-One");
				request.getSession().setAttribute(PANEL_NO, "Panel-One");
        	}
        	else if ((request.getParameter("submitButtonForUserName") != null)||(request.getParameter("iNETReportPanelTwo") != null)) {
        		CommonValidator commonValidator = new CommonValidator(
						"configfiles/c2sreports/validation-Channel2ChannelReports.xml", usersReportModel,
						"Channel2ChannelTrasRetWidRptUserName");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
        		model.addAttribute(PANEL_NO, "Panel-Two");
				request.getSession().setAttribute(PANEL_NO, "Panel-Two");
        	}
        	if (bindingResult.hasFieldErrors()) {

				return false;
			}
            channelUserWebDAO = new ChannelUserWebDAO();
            
            final UsersReportModel thisForm = usersReportModel;
            btslCommonController.commonReportDateFormat(thisForm, methodName);
            if (!BTSLUtil.isNullString(thisForm.getTransferInOrOut())) {
                if (thisForm.getTransferInOrOut().equalsIgnoreCase(PretupsI.ALL)) {
                    thisForm.setTransferInOrOutName(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.combo.all"));
                } else if (thisForm.getTransferInOrOut().equalsIgnoreCase(PretupsI.IN)) {
                	thisForm.setTransferInOrOutName(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.combo.in"));
                } else if (thisForm.getTransferInOrOut().equalsIgnoreCase(PretupsI.OUT)) {
                	thisForm.setTransferInOrOutName(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.combo.out"));
                }
            }
            
			if (!BTSLUtil.isNullString(thisForm.getFromTime())) {
				thisForm.setFromDateTime(BTSLUtil.getDateFromDateString(thisForm.getFromDate() + " " + thisForm.getFromTime() + ":00", PretupsI.TIMESTAMP_DATESPACEHHMMSS));
			} else {
				thisForm.setFromDateTime(BTSLUtil.getDateFromDateString(thisForm.getFromDate() + " 00:00:00", PretupsI.TIMESTAMP_DATESPACEHHMMSS));
			}
			if (!BTSLUtil.isNullString(thisForm.getToTime())) {
				thisForm.setToDateTime(BTSLUtil.getDateFromDateString(thisForm.getToDate() + " " + thisForm.getToTime() + ":59", PretupsI.TIMESTAMP_DATESPACEHHMMSS));
			} else {
				thisForm.setToDateTime(BTSLUtil.getDateFromDateString(thisForm.getToDate() + " 23:59:59", "ddd/MM/yy HH:mm:ss"));
			}
			
			 if (!BTSLUtil.isNullString(thisForm.getTxnSubType()) && (thisForm.getTxnSubType().trim()).equals(PretupsI.ALL)) {
	               thisForm.setTxnSubTypeName(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.list.all"));
	            } else if (!BTSLUtil.isNullString(thisForm.getTxnSubType())) {
	                listValueVO = BTSLUtil.getOptionDesc(thisForm.getTxnSubType(), thisForm.getTxnSubTypeList());
	                thisForm.setTxnSubTypeName(listValueVO.getLabel());
	            }
           thisForm.setNetworkName(userVO.getNetworkName());
            thisForm.setReportHeaderName(userVO.getReportHeaderName());
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            String[] ar = null;
          
            if (BTSLUtil.isNullString(thisForm.getFromMsisdn()) && BTSLUtil.isNullString(thisForm.getZoneCode()) && (BTSLUtil.isNullString(thisForm.getDomainCode()) && BTSLUtil
                .isNullString(thisForm.getParentCategoryCode()) && BTSLUtil.isNullString(thisForm.getUserName()))) {
            	
            	model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2c.reports.web.userreportform.error.msg.required.value", ar));
				return false;
            }
            
            else if (BTSLUtil.isNullString(thisForm.getFromMsisdn())) {
                if (BTSLUtil.isNullString(thisForm.getZoneCode())) {
                 model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2c.reports.web.userreportform.error.msg.required.zone", ar));
    				return false;
                }
                if (BTSLUtil.isNullString(thisForm.getDomainCode())) {
                	 model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2c.reports.web.userreportform.error.msg.required.domain", ar));
    				return false;
                }
                if (BTSLUtil.isNullString(thisForm.getUserName())) {
                	 model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2c.reports.web.userreportform.error.msg.required.user", ar));
    				return false;
                }
                if (BTSLUtil.isNullString(thisForm.getTouserName())) {
                	 model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2c.reports.web.userreportform.error.msg.required.touser", ar));
    				return false;
                }
            }

            if (!BTSLUtil.isNullString(thisForm.getFromMsisdn())) {
                if (!BTSLUtil.isValidMSISDN(thisForm.getFromMsisdn())) {
                    final String[] ar1 = { thisForm.getFromMsisdn() };
                   model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2c.reports.msisdn.error.length.frommsisdn", ar));
    				return false;
                }
                if (!BTSLUtil.isNullString(thisForm.getToMsisdn())) {
                    if (!BTSLUtil.isValidMSISDN(thisForm.getToMsisdn())) {
                        final String[] ar1 = { thisForm.getToMsisdn() };
                     model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2c.reports.msisdn.error.length.tomsisdn", ar1));
        				return false;
                    }
                }
            }
         
            if (!BTSLUtil.isNullString(thisForm.getFromMsisdn()))// MSISDN based report
            {
                // check if the msisdn belongs to login user network
                NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(thisForm.getFromMsisdn())));
                if (prefixVO == null || !prefixVO.getNetworkCode().equals(thisForm.getNetworkCode())) {
                    final String arr1[] = { thisForm.getFromMsisdn(), thisForm.getNetworkName() };
                    log.error(methodName, "Error: MSISDN Number" + thisForm.getFromMsisdn() + " not belongs to " + thisForm.getNetworkName() + "network");
                   
                    model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2s.reports.error.msisdnnotinsamenetwork", arr1));
    				return false;
                }
                String filteredToMSISDN = null;
                if (!BTSLUtil.isNullString(thisForm.getToMsisdn())) {
                    prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(thisForm.getToMsisdn())));
                    if (prefixVO == null || !prefixVO.getNetworkCode().equals(thisForm.getNetworkCode())) {
                        final String arr1[] = { thisForm.getToMsisdn(), thisForm.getNetworkName() };
                        log.error(methodName, "Error: MSISDN Number" + thisForm.getToMsisdn() + " not belongs to " + thisForm.getNetworkName() + "network");
                        
                        model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2s.reports.error.msisdnnotinsamenetwork", arr1));
        				return false;
                    }
                    filteredToMSISDN = PretupsBL.getFilteredMSISDN(thisForm.getToMsisdn());
                }

                final String status = "'" + PretupsI.USER_STATUS_NEW + "','" + PretupsI.USER_STATUS_CANCELED + "','" + PretupsI.USER_STATUS_DELETED + "'";
                final String statusUsed = PretupsI.STATUS_NOTIN;
                final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                final String filteredFromMSISDN = PretupsBL.getFilteredMSISDN(thisForm.getFromMsisdn());
                 ChannelUserVO fromMsisdnChannelUserVO = null;
                 ChannelUserVO toMsisdnChannelUserVO = null;

               // load user details on the basis of msisdn
                if (!BTSLUtil.isNullString(filteredToMSISDN)) {
                    if (PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())) {
                        fromMsisdnChannelUserVO = channelUserDAO.loadUsersDetails(con, filteredFromMSISDN, null, statusUsed, status);
                        toMsisdnChannelUserVO = channelUserDAO.loadUsersDetails(con, filteredToMSISDN, null, statusUsed, status);
                    } else {
                        final String userID = userVO.getUserID();
                        fromMsisdnChannelUserVO = channelUserDAO.loadUsersDetails(con, filteredFromMSISDN, userID, statusUsed, status);
                        toMsisdnChannelUserVO = channelUserDAO.loadUsersDetails(con, filteredToMSISDN, userID, statusUsed, status);
                    }
                } else {
                    if (PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())) {
                        fromMsisdnChannelUserVO = channelUserDAO.loadUsersDetails(con, filteredFromMSISDN, null, statusUsed, status);
                      
                    } else {
                        final String userID = userVO.getUserID();
                        if (userVO.getMsisdn().equalsIgnoreCase(filteredFromMSISDN)) {
                            fromMsisdnChannelUserVO = channelUserWebDAO.loadUsersDetailsForC2C(con, filteredFromMSISDN, userID, statusUsed, status);
                        } else {
                            fromMsisdnChannelUserVO = channelUserDAO.loadUsersDetails(con, filteredFromMSISDN, userID, statusUsed, status);
                          
                        }
                    }
                }
                if (!PretupsBL.getFilteredMSISDN(thisForm.getFromMsisdn()).equals(userVO.getMsisdn()) && fromMsisdnChannelUserVO == null) {
                	ar = new String[1];
                    ar[0] = thisForm.getFromMsisdn();
                    model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2s.reports.error.nouserbymsisdn", ar));
    				return false;
                }

                if (!BTSLUtil.isNullString(thisForm.getToMsisdn())) {
                    if (!PretupsBL.getFilteredMSISDN(thisForm.getToMsisdn()).equals(userVO.getMsisdn()) && toMsisdnChannelUserVO == null) {
                    	ar = new String[1];
                        ar[0] = thisForm.getToMsisdn();
                        model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2s.reports.error.nouserbymsisdn", ar));
        				return false;
                    }
                   
                    
                }
                // check msisdn belongs to hierarchy of login user if hierarchy
                // is allowed to login user
                final ChannelUserDAO chanelUserDAO = new ChannelUserDAO();
                ArrayList channelUserList = new ArrayList();
                if ("Y".equals(userVO.getCategoryVO().getHierarchyAllowed())) {
                    channelUserList = channelUserWebDAO.loadChannelUserHierarchy(con, userVO.getUserCode(), false);
                } else {
                    ChannelUserVO channelUserVO = new ChannelUserVO();
                    channelUserVO = chanelUserDAO.loadChannelUser(con, userVO.getUserID());
                    channelUserList = channelUserWebDAO.loadChannelUserHierarchy(con, channelUserVO.getUserCode(), false);
                }
                if (channelUserList != null && !channelUserList.isEmpty()) {
                    if (!(fromMsisdnChannelUserVO.getCategoryVO().getAgentDomainCodeforCategory()).equals(userVO.getCategoryVO().getDomainCodeforCategory())) {
                        final String arr1[] = { thisForm.getFromMsisdn() };
                        log.error(methodName, "Error: MSISDN Number" + thisForm.getFromMsisdn() + " does not belong to channel domain");
                        model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2c.reports.error.usernotindomain", ar));
        				return false;
                    }
                    if ((fromMsisdnChannelUserVO.getCategoryVO().getAgentGrphDomainType()).equals(userVO.getCategoryVO().getAgentGrphDomainType())) {
                        final String arr1[] = { thisForm.getFromMsisdn() };
                        log.error(methodName, "Error: MSISDN Number" + thisForm.getFromMsisdn() + " does not belong to geographical zone");
                        model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2c.reports.error.usernotingeography", arr1));
        				return false;
                    }
                }

                final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
                ChannelTransferRuleVO channelTransferRuleVO = null;
                if (toMsisdnChannelUserVO != null) {
                    if (PretupsBL.getFilteredMSISDN(thisForm.getFromMsisdn()).equals(userVO.getMsisdn()) && (!(PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())))) {
                        channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, thisForm.getNetworkCode(), userVO.getCategoryVO().getDomainCodeforCategory(),
                            userVO.getCategoryCode(), toMsisdnChannelUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_CHANNEL, false);
                    } else {
                        if (PretupsBL.getFilteredMSISDN(thisForm.getToMsisdn()).equals(userVO.getMsisdn()) && (!(PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())))) {
                            channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, thisForm.getNetworkCode(), fromMsisdnChannelUserVO.getCategoryVO()
                                .getDomainCodeforCategory(), fromMsisdnChannelUserVO.getCategoryCode(), userVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_CHANNEL, false);
                        } else {
                            channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, thisForm.getNetworkCode(), fromMsisdnChannelUserVO.getCategoryVO()
                                .getDomainCodeforCategory(), fromMsisdnChannelUserVO.getCategoryCode(), toMsisdnChannelUserVO.getCategoryCode(),
                                PretupsI.TRANSFER_RULE_TYPE_CHANNEL, false);
                        }
                    }
                }
                if (toMsisdnChannelUserVO == null && fromMsisdnChannelUserVO != null) {
                    if (PretupsBL.getFilteredMSISDN(thisForm.getFromMsisdn()).equals(userVO.getMsisdn()) && (!(PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())))) {
                        thisForm.setUserName(userVO.getUserName());
                        thisForm.setUserID(userVO.getUserID());
                        thisForm.setFromtransferCategoryCode(userVO.getCategoryCode());
                        thisForm.setFromtransferCategoryName(userVO.getCategoryVO().getCategoryName());
                        thisForm.setDomainCode(userVO.getCategoryVO().getDomainCodeforCategory());
                        thisForm.setDomainName(userVO.getDomainName());
                        final UserGeographiesVO userGeoVO = (UserGeographiesVO) userVO.getGeographicalAreaList().get(0);
                        thisForm.setZoneCode(PretupsI.ALL);
                        thisForm.setZoneName(userGeoVO.getGraphDomainName());
                    } else {
                        thisForm.setUserName(fromMsisdnChannelUserVO.getUserName());
                        thisForm.setUserID(fromMsisdnChannelUserVO.getUserID());
                        thisForm.setFromtransferCategoryName(fromMsisdnChannelUserVO.getCategoryVO().getCategoryName());
                        thisForm.setFromtransferCategoryCode(fromMsisdnChannelUserVO.getCategoryVO().getCategoryCode());
                        thisForm.setDomainName(fromMsisdnChannelUserVO.getDomainName());
                        thisForm.setDomainCode(fromMsisdnChannelUserVO.getCategoryVO().getDomainCodeforCategory());
                        thisForm.setZoneName(fromMsisdnChannelUserVO.getGeographicalDesc());
                        thisForm.setZoneCode(PretupsI.ALL);

                    }
					if (PretupsBL.getFilteredMSISDN(thisForm.getToMsisdn()).equals(userVO.getMsisdn())) {
						thisForm.setTouserID(userVO.getUserID());
						thisForm.setTouserName(userVO.getUserName());
						thisForm.setTotransferCategoryCode(userVO.getCategoryCode());
						thisForm.setTotransferCategoryName(userVO.getCategoryVO().getCategoryName());

					} else {
						thisForm.setTouserID(PretupsI.ALL);
						thisForm.setTouserName(PretupsI.ALL);
						thisForm.setTotransferCategoryCode(PretupsI.ALL);
						thisForm.setTotransferCategoryName(PretupsI.ALL);
					}
                } else if (channelTransferRuleVO != null) {

                	btslCommonController.commonValidationMSISDN(userVO, thisForm, toMsisdnChannelUserVO, fromMsisdnChannelUserVO, methodName);

                } else {
                	model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2s.reports.error.transferrulenotexist"));
    				return false;
                }
            }// 5.1.3 new features end
            else {
                final ChannelUserReportDAO channelUserDAO = new ChannelUserReportDAO();

                tempfromCatCode = thisForm.getFromtransferCategoryCode();
                temptoCatCode = thisForm.getTotransferCategoryCode();
                final String fromCat[] = thisForm.getFromtransferCategoryCode().split(":");
                final String toCat[] = thisForm.getTotransferCategoryCode().split(":");
             
                if (!BTSLUtil.isNullString(thisForm.getUserName())&&!thisForm.getUserName().equals(PretupsI.ALL)) {
                	String[] parts = (thisForm.getUserName()).split("\\(");
    				final String userName = parts[0];
    				try {
						String userID = parts[1];
						userID = userID.replaceAll("\\)", "");
	                	thisForm.setUserID(userID);
					} catch (Exception e) {
						log.error(methodName,ERROR_MSG+ e);
						log.errorTrace(methodName, e);
					}

                    if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                        if (thisForm.getFromtransferCategoryCode().equals(PretupsI.ALL)) {
                            userList = channelUserDAO.loadUserListOnZoneCategoryHierarchy(con, PretupsI.ALL, thisForm.getZoneCode(), userName, userVO.getUserID(), thisForm
                                .getDomainCode());
                        } else {
                            userList = channelUserDAO.loadUserListOnZoneCategoryHierarchy(con, fromCat[1], thisForm.getZoneCode(), userName, userVO.getUserID(), thisForm
                                .getDomainCode());
                        }
                    } else {
                        if (thisForm.getFromtransferCategoryCode().equals(PretupsI.ALL)) {
                            userList = channelUserDAO.loadUserListOnZoneDomainCategory(con, PretupsI.ALL, thisForm.getZoneCode(), null, userName, userVO.getUserID(), thisForm
                                .getDomainCode());
                        } else {
                            userList = channelUserDAO.loadUserListOnZoneDomainCategory(con, fromCat[1], thisForm.getZoneCode(), null, userName, userVO.getUserID(), thisForm
                                .getDomainCode());
                        }
                    }

                    if (userList == null || (userList != null && userList.isEmpty())) {
                        thisForm.setFromtransferCategoryCode(tempfromCatCode);
                        thisForm.setTotransferCategoryCode(temptoCatCode);
                        /*final BTSLMessages btslMessage = new BTSLMessages("c2c.reports.c2ctransfers.error.fromusernotexist", "loadC2cTransferReturnWithdraw");*/
                        model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2c.reports.c2ctransfers.error.fromusernotexist"));
                        return false;
                    } else if (userList != null && userList.size() == 1) {
                        final ListValueVO channelUserTransferVO = (ListValueVO) userList.get(0);
                        thisForm.setUserName(channelUserTransferVO.getLabel());
                        thisForm.setUserID(channelUserTransferVO.getValue());
                      
                    } else if (userList != null && userList.size() > 1) {
                        /*
                         * This is the case when userList size greater than 1
                         * if user click the submit
                         * button(selectcategoryForEdit.jsp) after performing
                         * search through searchUser and select one form the
                         * shown list at that time we
                         * set the userid on the form(becs two user have the
                         * same name but different id)
                         * so here we check the userId is null or not it is not
                         * null iterate the list and open the screen
                         * in edit mode corresponding to the userid
                         */
                        boolean flag = true;
                        if (!BTSLUtil.isNullString(thisForm.getUserID())) {
                            for (int i = 0, j = userList.size(); i < j; i++) {
                                final ListValueVO channelUserTransferVO = (ListValueVO) userList.get(i);
                                if (thisForm.getUserID().equals(channelUserTransferVO.getValue()) && channelUserTransferVO.getLabel().equalsIgnoreCase(thisForm.getUserName())) {
                                    thisForm.setUserName(channelUserTransferVO.getLabel());
                                    flag = false;
                                  break;
                                }
                            }
                        }
                        if (flag) {
                            thisForm.setFromtransferCategoryCode(tempfromCatCode);
                            thisForm.setTotransferCategoryCode(temptoCatCode);
                            /*final BTSLMessages btslMessage = new BTSLMessages("c2c.reports.c2ctransfers.error.fromusermoreexist", "loadC2cTransferReturnWithdraw");*/
                            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2c.reports.c2ctransfers.error.fromusermoreexist"));
                            return false;
                        }
                    }
                }
                else{
                	 if (thisForm.getUserName().equals(PretupsI.ALL)) 
                         thisForm.setUserID(PretupsI.ALL);
                 }

            
                if (!BTSLUtil.isNullString(thisForm.getUserID()) && !BTSLUtil.isNullString(thisForm.getTouserName())) {
                	if(!(PretupsI.ALL.equalsIgnoreCase(thisForm.getTouserName()))){
                	String[] parts = (thisForm.getTouserName()).split("\\(");
                	thisForm.setTouserName(parts[0]);
                	try {
						String userID = parts[1];
						userID = userID.replaceAll("\\)", "");
	                	thisForm.setTouserID(userID);
					} catch (Exception e) {
						log.error(methodName,ERROR_MSG+ e);
						log.errorTrace(methodName, e);
					}

                	}

                    String toCatStr = "'";
                    thisForm.getFromCategoryList();
                    thisForm.getToCategoryList();
                    listValueVO = null;

                    if (fromCat[1].equals(PretupsI.ALL)) {
                        if (toCat[2].equals(PretupsI.ALL)) {
                            for (int i = 0, j = thisForm.getToCategoryList().size(); i < j; i++) {
                                listValueVO = (ListValueVO) thisForm.getToCategoryList().get(i);
                                final String temptoCat[] = listValueVO.getValue().split(":");
                                if (thisForm.getDomainCode().equals(temptoCat[0])) {
                                    toCatStr = toCatStr + temptoCat[2] + "','";
                                }
                            }
                        } else {
                            toCatStr = toCatStr + toCat[2] + "','";
                        }
                    } else {
                        if (toCat[2].equals(PretupsI.ALL)) {
                            for (int i = 0, j = thisForm.getToCategoryList().size(); i < j; i++) {
                                listValueVO = (ListValueVO) thisForm.getToCategoryList().get(i);
                                final String temptoCat[] = listValueVO.getValue().split(":");
                                if (thisForm.getDomainCode().equals(temptoCat[0]) && fromCat[1].equals(temptoCat[1])) {
                                    toCatStr = toCatStr + temptoCat[2] + "','";
                                }
                            }
                        } else {
                            toCatStr = toCatStr + toCat[2] + "','";
                        }
                    }
                    if (toCatStr.length() > 2) {
                        toCatStr = toCatStr.substring(0, toCatStr.length() - 2);
                    }
                    if (toCat[2].equals(PretupsI.ALL)) {
                        touserList = channelUserDAO.loadUserList(con, toCatStr, thisForm.getUserID(), thisForm.getTouserName(), thisForm.getNetworkCode(), thisForm
                            .getDomainCode());
                    } else {
                        final CategoryDAO catDAO = new CategoryDAO();
                        final CategoryVO categoryVO = catDAO.loadCategoryDetailsByCategoryCode(con, toCatStr.substring(1, toCatStr.length() - 1));
                        touserList = channelUserDAO.loadUserList(con, toCatStr, thisForm.getUserID(), thisForm.getTouserName(), thisForm.getNetworkCode(), categoryVO
                            .getDomainCodeforCategory());
                    }
                    if (thisForm.getTouserName().equals(PretupsI.ALL)) {
                        thisForm.setTouserID(PretupsI.ALL);
                      } else if (touserList == null ||  touserList.isEmpty()) {
                        thisForm.setFromtransferCategoryCode(tempfromCatCode);
                        thisForm.setTotransferCategoryCode(temptoCatCode);
                       /* final BTSLMessages btslMessage = new BTSLMessages("c2c.reports.c2ctransfers.error.tousernotexist", "loadC2cTransferReturnWithdraw");*/
                        model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2c.reports.c2ctransfers.error.tousernotexist"));
                        return false;
                     } else if (touserList != null && touserList.size() == 1) {
                        final ListValueVO channelUserTransferVO = (ListValueVO) touserList.get(0);
                        thisForm.setTouserName(channelUserTransferVO.getLabel());
                        thisForm.setTouserID(channelUserTransferVO.getValue());
                    } else if (touserList != null && touserList.size() > 1) {
                        /*
                         * This is the case when touserList size greater than 1
                         * if user click the submit
                         * button(selectcategoryForEdit.jsp) after performing
                         * search through searchUser and select one form the
                         * shown list at that time we
                         * set the userid on the form(becs two user have the
                         * same name but different id)
                         * so here we check the userId is null or not it is not
                         * null iterate the list and open the screen
                         * in edit mode corresponding to the userid
                         */
                        boolean flag = true;
                        if (!BTSLUtil.isNullString(thisForm.getTouserID())) {
                            for (int i = 0, j = touserList.size(); i < j; i++) {
                                final ListValueVO channelUserTransferVO = (ListValueVO) touserList.get(i);
                                if (thisForm.getTouserID().equals(channelUserTransferVO.getValue()) && channelUserTransferVO.getLabel().equalsIgnoreCase(
                                    thisForm.getTouserName())) {
                                    thisForm.setTouserName(channelUserTransferVO.getLabel());
                                    flag = false;
                                  break;
                                }
                            }
                        }
                        if (flag) {
                            thisForm.setFromtransferCategoryCode(tempfromCatCode);
                            thisForm.setTotransferCategoryCode(temptoCatCode);
                           /* final BTSLMessages btslMessage = new BTSLMessages("c2c.reports.c2ctransfers.error.tousermoreexist", "loadC2cTransferReturnWithdraw");*/
                            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2c.reports.c2ctransfers.error.tousermoreexist"));
                            return false;
                         }
                    }
                }
                listValueVO = BTSLUtil.getOptionDesc(thisForm.getDomainCode(), thisForm.getDomainList());
                thisForm.setDomainName(listValueVO.getLabel());

                if (thisForm.getZoneCode().equals(PretupsI.ALL)) {
                	thisForm.setZoneCode(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.list.all"));
                	thisForm.setZoneName(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.list.all"));
                   } else {
                    listValueVO = BTSLUtil.getOptionDesc(thisForm.getZoneCode(), thisForm.getZoneList());
                    thisForm.setZoneName(listValueVO.getLabel());
                }

                if (!BTSLUtil.isNullString(thisForm.getFromtransferCategoryCode()) && thisForm.getFromtransferCategoryCode().equals(PretupsI.ALL + ":" + PretupsI.ALL)) {
                	thisForm.setFromtransferCategoryName(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.list.all"));
                	thisForm.setFromtransferCategoryCode(PretupsI.ALL);
                } else if (!BTSLUtil.isNullString(thisForm.getFromtransferCategoryCode())) {
                    listValueVO = BTSLUtil.getOptionDesc(thisForm.getFromtransferCategoryCode(), thisForm.getFromCategoryList());
                    thisForm.setFromtransferCategoryName(listValueVO.getLabel());
                    final String fromCatCode[] = thisForm.getFromtransferCategoryCode().split(":");
                    thisForm.setFromtransferCategoryCode(fromCatCode[1]);
                }
                if (!BTSLUtil.isNullString(thisForm.getTotransferCategoryCode()) && thisForm.getTotransferCategoryCode().equals(
                    PretupsI.ALL + ":" + PretupsI.ALL + ":" + PretupsI.ALL)) {
                	thisForm.setTotransferCategoryName(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.list.all"));
                	thisForm.setTotransferCategoryCode(PretupsI.ALL);
                } else if (!BTSLUtil.isNullString(thisForm.getTotransferCategoryCode())) {
                    listValueVO = BTSLUtil.getOptionDesc(thisForm.getTotransferCategoryCode(), thisForm.getToCategoryList());
                    thisForm.setTotransferCategoryName(listValueVO.getLabel());
                    final String toCatCode[] = thisForm.getTotransferCategoryCode().split(":");
                    thisForm.setTotransferCategoryCode(toCatCode[2]);
                }
            }
            if(thisForm.getToDateTime().compareTo(thisForm.getFromDateTime())<0)
			{
				model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("reports.validfromtimegreater.error.msg",ar));
				return false;
			}
           String rptFile ="";
            Channel2ChannelTransferRetWidRptDAO channel2ChannelTransferRetWidRptDAO = new Channel2ChannelTransferRetWidRptDAO();
            if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
            	 if(thisForm.getTransferInOrOut().equalsIgnoreCase(PretupsI.ALL))
	    			{
	    				if(PretupsI.NO.equals(thisForm.getStaffReport()))
	    				{
	    					rptFile="C2cRetWidTransferChannelUserUnion.rpt";
							rptCode = "C2CRWTR03";
							final ArrayList<ChannelTransferVO> c2cTransferList = channel2ChannelTransferRetWidRptDAO.loadC2cRetWidTransferChannelUserUnionList(con,thisForm);
				            thisForm.setTransferList(c2cTransferList);
				            thisForm.setrptCode(rptCode);
	    				}
	    				else
	    				{
	    					rptFile="C2cRetWidTransferChannelUserUnionStaff.rpt";
							rptCode = "C2CRWTR07";
							final ArrayList<ChannelTransferVO> c2cTransferList = channel2ChannelTransferRetWidRptDAO.loadC2cRetWidTransferChannelUserUnionStaffList(con,thisForm);
				            thisForm.setTransferList(c2cTransferList);
				            thisForm.setrptCode(rptCode);
	    				}
	    				
	    			}
            	 else
		    			{
		    				if(PretupsI.NO.equals(thisForm.getStaffReport()))
		    				{
			    				rptFile="C2cRetWidTransferChannelUser.rpt";
								rptCode = "C2CRWTR04";
								final ArrayList<ChannelTransferVO> c2cTransferList = channel2ChannelTransferRetWidRptDAO.loadC2cRetWidTransferChannelUserList(con,thisForm);
					            thisForm.setTransferList(c2cTransferList);
					            thisForm.setrptCode(rptCode);
							}
							else
							{
								rptFile="C2cRetWidTransferChnlUserStaff.rpt";
								rptCode = "C2CRWTR08";
								final ArrayList<ChannelTransferVO> c2cTransferList = channel2ChannelTransferRetWidRptDAO.loadC2cRetWidTransferChnlUserStaffList(con,thisForm);
					            thisForm.setTransferList(c2cTransferList);
					            thisForm.setrptCode(rptCode);
							}	
		    			}
            }
            else {
            	if(thisForm.getTransferInOrOut().equalsIgnoreCase(PretupsI.ALL))
				{
					if(PretupsI.NO.equals(thisForm.getStaffReport()))
					{ 
						rptFile="C2cRetWidTransferUnion.rpt";
						rptCode = "C2CRWTR02";
						final ArrayList<ChannelTransferVO> c2cTransferList = channel2ChannelTransferRetWidRptDAO.loadC2cRetWidTransferUnionList(con,thisForm);
			            thisForm.setTransferList(c2cTransferList);
			            thisForm.setrptCode(rptCode);
					}
					else
					{
						rptFile = "C2cRetWidTransferUnionStaff.rpt";
						rptCode = "C2STRANSFER06";
						final ArrayList<ChannelTransferVO> c2cTransferList = channel2ChannelTransferRetWidRptDAO.loadC2cRetWidTransferUnionStaffList(con,thisForm);
			            thisForm.setTransferList(c2cTransferList);
			            thisForm.setrptCode(rptCode);
					}
					
				}
            	else
    			{
    				if(PretupsI.NO.equals(thisForm.getStaffReport()))
    				{ 
    					rptFile="C2cRetWidTransfer.rpt";
						rptCode = "C2CRWTR01";
						final ArrayList<ChannelTransferVO> c2cTransferList = channel2ChannelTransferRetWidRptDAO.loadC2cRetWidTransferList(con,thisForm);
			            thisForm.setTransferList(c2cTransferList);
			            thisForm.setrptCode(rptCode);
					}
					else
					{
						rptFile="C2cRetWidTransferStaff.rpt";
						rptCode = "C2CRWTR05";
						final ArrayList<ChannelTransferVO> c2cTransferList = channel2ChannelTransferRetWidRptDAO.loadC2cRetWidTransferStaffList(con,thisForm);
			            thisForm.setTransferList(c2cTransferList);
			            thisForm.setrptCode(rptCode);
					}
            	
            }
            	
            	
            }	
            thisForm.setTransferListSize( thisForm.getTransferList().size());
          /*Report data list End*/
         
        } catch (BTSLBaseException e) {
            log.errorTrace(methodName, e);
            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
            return false;
          
        } catch (ParseException e) {
            log.errorTrace(methodName, e);
            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
            return false;
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
            return false;
        } finally {
			if (mcomCon != null) {
				mcomCon.close(methodName);
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
               // log.error(methodName, "Exiting : forward" + forward);
            }
        }
         
		return true;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<ListValueVO> loadToUserList(UsersReportModel usersReportModel,String domainCode,UserVO userVO,String fromUserName,String fromTransferCategorycode, String toTransferCategorycode,String userName) throws BTSLBaseException {
		final String methodName = "Channel2ChannelTransferReportsServiceImpl#loadToUserList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED +"");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		final ChannelUserReportDAO channelUserDAO = new ChannelUserReportDAO();
		ChannelUserWebDAO channelUserWebDAO = null;
		ArrayList<ListValueVO> touserList = null;
		try {
			channelUserWebDAO = new ChannelUserWebDAO();
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			String  temptoCatCode =toTransferCategorycode;
			  String toCatStr = "'";
			  String fromUserID =null;
			  usersReportModel.getFromCategoryList();
			  usersReportModel.getToCategoryList();
			  ListValueVO listValueVO = null;
			  final String fromCat[] = fromTransferCategorycode.split(":");
              final String toCat[] = toTransferCategorycode.split(":");
              if (fromCat[1].equals(PretupsI.ALL)) {
                  if (toCat[2].equals(PretupsI.ALL)) {
                      for (int i = 0, j = usersReportModel.getToCategoryList().size(); i < j; i++) {
                          listValueVO = (ListValueVO) usersReportModel.getToCategoryList().get(i);
                          final String temptoCat[] = listValueVO.getValue().split(":");
                          if (domainCode.equals(temptoCat[0])) {
                              toCatStr = toCatStr + temptoCat[2] + "','";
                          }
                      }
                  } else {
                      toCatStr = toCatStr + toCat[2] + "','";
                  }
              } else {
                  if (toCat[2].equals(PretupsI.ALL)) {
                      for (int i = 0, j = usersReportModel.getToCategoryList().size(); i < j; i++) {
                          listValueVO = (ListValueVO) usersReportModel.getToCategoryList().get(i);
                          final String temptoCat[] = listValueVO.getValue().split(":");
                          if (domainCode.equals(temptoCat[0]) && fromCat[1].equals(temptoCat[1])) {
                              toCatStr = toCatStr + temptoCat[2] + "','";
                          }
                      }
                  } else {
                      toCatStr = toCatStr + toCat[2] + "','";
                  }
              }
              if (toCatStr.length() > 2) {
                  toCatStr = toCatStr.substring(0, toCatStr.length() - 2);
              }
              if(fromUserName.equalsIgnoreCase(PretupsI.ALL)){
            	  fromUserName=fromUserName+"("+fromUserName+")";
              }
              String[] parts = fromUserName.split("\\(");
              try {
            	  	fromUserName=parts[0];
					fromUserID = parts[1];
					fromUserID = fromUserID.replaceAll("\\)", "");
              	
				} catch (Exception e) {
					log.error(methodName,ERROR_MSG+ e);
					log.errorTrace(methodName, e);
				}
  			 
              if (toCat[2].equals(PretupsI.ALL)) {
                  touserList = channelUserDAO.loadUserList(con, toCatStr, fromUserID,userName, userVO.getNetworkID(), domainCode);
              } else {
                  final CategoryDAO catDAO = new CategoryDAO();
                  final CategoryVO categoryVO = catDAO.loadCategoryDetailsByCategoryCode(con, toCatStr.substring(1, toCatStr.length() - 1));
                  touserList = channelUserDAO.loadUserList(con, toCatStr, fromUserID, userName, userVO.getNetworkID(), categoryVO
                      .getDomainCodeforCategory());
              }
				
				
				
			} catch (Exception e) {
				log.errorTrace(methodName, e);
	        }
		
		finally {
			if (mcomCon != null) {
				mcomCon.close(methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}

		return touserList;
	}


	@SuppressWarnings("unchecked")
	public List<ListValueVO> loadFromUserList(UserVO userVO, String zoneCode, String domainCode,String fromTransferCategorycode,String UserName) throws BTSLBaseException {
		final String methodName="Channel2ChannelTransferReportsServiceImpl#loadFromUserList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED +"");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;

		ArrayList<ListValueVO> fromUserList =  new ArrayList();
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			
			 final ChannelUserReportDAO channelUserDAO = new ChannelUserReportDAO();
			 String tempfromCatCode = "";
		     tempfromCatCode = fromTransferCategorycode;
             final String fromCat[] = fromTransferCategorycode.split(":");
             if (UserName.equals(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.list.all"))) {
            	 fromUserList.add(new ListValueVO(UserName,UserName));
            	 return fromUserList;
             } else if (!BTSLUtil.isNullString(UserName)) {
            	String[] parts = (UserName).split("\\(");
 				final String userName = parts[0];
               
                 if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                     if (fromTransferCategorycode.equals(PretupsI.ALL)) {
                    	 fromUserList = channelUserDAO.loadUserListOnZoneCategoryHierarchy(con, PretupsI.ALL, zoneCode, userName, userVO.getUserID(), domainCode);
                     } else {
                    	 fromUserList = channelUserDAO.loadUserListOnZoneCategoryHierarchy(con, fromCat[1],zoneCode, userName, userVO.getUserID(), domainCode);
                     }
                 } else {
                     if (fromTransferCategorycode.equals(PretupsI.ALL)) {
                    	 fromUserList = channelUserDAO.loadUserListOnZoneDomainCategory(con, PretupsI.ALL,zoneCode, null, userName, userVO.getUserID(), domainCode);
                     } else {
                    	 fromUserList = channelUserDAO.loadUserListOnZoneDomainCategory(con, fromCat[1],zoneCode, null, userName, userVO.getUserID(),domainCode);
                     }
                 }
}
             }catch (Exception e) {
            	 log.errorTrace(methodName, e);
            }
		finally {
			if (mcomCon != null) {
				mcomCon.close(methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}

		return fromUserList;
	}
	
	
	public String downloadCSVReportFile(Model model,UsersReportModel usersReportModel) throws BTSLBaseException, SQLException, InterruptedException {
		final String methodName ="Channel2ChannelTransferReportsServiceImpl#downloadCSVReportFile";
		if (log.isDebugEnabled()) 
			log.debug(methodName, PretupsI.EXITED);
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		String filePath = null;
		try{
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			DownloadCSVReports downloadCSVReports = new DownloadCSVReports();
			filePath = downloadCSVReports.prepareData(usersReportModel, usersReportModel.getrptCode(), con);	
		}catch(BTSLBaseException e){
			 log.errorTrace(methodName, e);
	            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
	            return null;
		}
		catch (InterruptedException e) 
		{
			log.errorTrace(methodName, e);
		}
		finally{
			if (mcomCon != null) {
				mcomCon.close(methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			
		}
		}
		return filePath;

	}
	}

