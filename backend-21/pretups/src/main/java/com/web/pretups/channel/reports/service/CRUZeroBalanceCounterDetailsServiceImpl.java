package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.btsl.common.BTSLCommonController;
import com.btsl.common.BTSLMessages;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.ChannelReportsUserVO;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO;
import com.btsl.pretups.channel.reports.businesslogic.ZeroBalanceCounterDetailsRptDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.DownloadCSVReports;
//import com.btsl.pretups.common.DownloadCSVReportsBL;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
/**
 * @author tarun.kumar
 *
 */
@Service("cRUZeroBalanceCounterDetailsService")
public class CRUZeroBalanceCounterDetailsServiceImpl implements CRUZeroBalanceCounterDetailsService{

	public static final Log _log = LogFactory.getLog(CRUZeroBalanceCounterDetailsServiceImpl.class.getName());
	private static final String PANEL_NO="PanelNo";		
	private static final String FAIL_KEY = "fail";	
	private static final String LIST_ALL = "list.all";
	@Override
	public void loadZeroBalCounterDetail(HttpServletRequest request,HttpServletResponse response, UsersReportModel usersReportModel,UserVO userVO, Model model){
		
		final String methodName = "loadZeroBalCounterDetail";
        enteredMethod(methodName);         
        Connection con = null;
        MComConnectionI mcomCon = null;              
        try {     
        	BTSLCommonController bTSLCommonController = new BTSLCommonController();       	
            mcomCon = new MComConnection();           
			con=mcomCon.getConnection();			                       
            usersReportModel.setThresholdTypeList(LookupsCache.loadLookupDropDown(PretupsI.THRESHOLD_COUNTER_TYPE, true));                       		   
            setZoneList(usersReportModel, userVO, con);
		    usersReportModel.setNetworkCode(userVO.getNetworkID());
		    usersReportModel.setUserType(userVO.getUserType());		  
		    usersReportModel.setCategorySeqNo(Integer.toString(userVO.getCategoryVO().getSequenceNumber()));	
            ListValueVO listValueVO = null;
            bTSLCommonController.commonGeographicDetails(usersReportModel, listValueVO);
            final ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();            
            listValueVO = null;
            usersReportModel.setLoggedInUserCategoryCode(userVO.getCategoryVO().getCategoryCode());
            setLoggedInUserName(usersReportModel, userVO);
            usersReportModel.setLoginUserID(userVO.getUserID());
            final ArrayList<ListValueVO> loggedInUserDomainList = new ArrayList<>();
            final ArrayList<ListValueVO> fromCatList = new ArrayList<>();
            final ArrayList<ListValueVO> toCatList = new ArrayList<>();
            ArrayList<?> transferRulCatList = null;
            ArrayList<?> domainList = userVO.getDomainList();
            if ((domainList == null || domainList.isEmpty()) && PretupsI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO.getCategoryVO().getFixedDomains())) {
                domainList = new DomainDAO().loadCategoryDomainList(con);
            } else if (domainList!=null && domainList.size() == 1) {             	
                listValueVO = (ListValueVO) domainList.get(0);
                usersReportModel.setDomainCode(listValueVO.getValue());
                usersReportModel.setDomainName(listValueVO.getLabel());
            }
            usersReportModel.setDomainList(BTSLUtil.displayDomainList(domainList));
            setDomainContent(usersReportModel, userVO, loggedInUserDomainList);          
            transferRulCatList = channelTransferRuleWebDAO.loadChannelTransferRuleVOList(con, usersReportModel.getNetworkCode(), usersReportModel.getDomainCode(),PretupsI.TRANSFER_RULE_TYPE_OPT);
            boolean isForAllCategory = true;
            String categoryCode = null;
            if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                isForAllCategory = false;
                categoryCode = userVO.getCategoryCode();
            }          
            // for operator user load all the categories.
            addCategory(fromCatList, toCatList, transferRulCatList,isForAllCategory, categoryCode);
            final ArrayList<ListValueVO> tempFromCat = new ArrayList<>();         
            fromCatList(fromCatList, tempFromCat);
            usersReportModel.setParentCategoryList(tempFromCat);
            toCategoryList(toCatList);        
        } catch (BTSLBaseException | SQLException e) {
            _log.errorTrace(methodName, e);           
        } 
        finally {
        	if(mcomCon != null){ 
        		mcomCon.close("CRUZeroBalanceCounterDetailsServiceImpl#loadZeroBalCounterDetail");
        	   mcomCon=null;
        	}
            exitMethod(methodName);
        }       
	}
	private void exitMethod(final String methodName) {
		if (_log.isDebugEnabled()) {
		    _log.debug(methodName, "Exit");
		}
	}
	private void enteredMethod(final String methodName) {
		if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
	}
	private void setDomainContent(UsersReportModel usersReportModel,
			UserVO userVO, final ArrayList<ListValueVO> loggedInUserDomainList) {
		if (usersReportModel.getDomainListSize() == 0) {
		    loggedInUserDomainList.add(new ListValueVO(userVO.getDomainName(), userVO.getDomainID()));
		    usersReportModel.setDomainList(loggedInUserDomainList);
		    usersReportModel.setDomainCode(userVO.getDomainID());
		    usersReportModel.setDomainName(userVO.getDomainName());
		}
	}
	private void addCategory(final ArrayList<ListValueVO> fromCatList,
			final ArrayList<ListValueVO> toCatList,
			ArrayList<?> transferRulCatList, boolean isForAllCategory,
			String categoryCode) {
		ChannelTransferRuleVO channelTransferRuleVO;
		if (isForAllCategory) {
		    for (int i = 0, k = transferRulCatList.size(); i < k; i++) {
		        channelTransferRuleVO = (ChannelTransferRuleVO) transferRulCatList.get(i);                  
		        fromCatList.add(new ListValueVO(channelTransferRuleVO.getToCategoryDes(), channelTransferRuleVO.getDomainCode() + ":" + channelTransferRuleVO.getToCategory()));
		        toCatList.add(new ListValueVO(channelTransferRuleVO.getToCategoryDes(), channelTransferRuleVO.getFromCategory() + ":" + channelTransferRuleVO.getToCategory()));
		    }           
		// for channel user load the category down the hierarchy
		}else {
		     addCatList(fromCatList, toCatList,transferRulCatList, categoryCode);
		}
	}
	private void setLoggedInUserName(UsersReportModel usersReportModel,
			UserVO userVO) {
		if (userVO.isStaffUser()) {
			usersReportModel.setLoggedInUserName(userVO.getParentName());
		} else {
			usersReportModel.setLoggedInUserName(userVO.getUserName());
		}
	}
	private void setZoneList(UsersReportModel usersReportModel, UserVO userVO,
			Connection con) throws BTSLBaseException {
		if(TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode())){
			usersReportModel.setZoneList(new GeographicalDomainDAO().loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID()));
		}
		else{
		usersReportModel.setZoneList(userVO.getGeographicalAreaList());
		}
	}
	private void toCategoryList(final ArrayList<ListValueVO> toCatList) {
		ListValueVO listValueVO;
		ListValueVO listValueVONext;
		int i;
		int j;		
		for ( i = 0, j = toCatList.size(); i < j - 1;) {
		    listValueVO = toCatList.get(i);
		    listValueVONext = toCatList.get(i + 1);
		    if (listValueVO.getValue().equals(listValueVONext.getValue())) {
		        toCatList.remove(i + 1);
		        j--;
		    } else {
		        i++;
		    }
		}
	}
	private void fromCatList(final ArrayList<ListValueVO> fromCatList,
			final ArrayList<ListValueVO> tempFromCat) {
		ListValueVO listValueVO;
		ListValueVO listValueVONext;
		
		for (int i = 0, j = fromCatList.size(); i < j; i++) {
		    listValueVO = fromCatList.get(i);
		    boolean flag = true;
		    for (int k = i + 1, l = fromCatList.size(); k < l; k++) {
		        listValueVONext = fromCatList.get(k);
		        if(listValueVO.getValue().equals(listValueVONext.getValue())) {
		            flag = false;
		            break;
		        }
		    }
		    if (flag) {
		        tempFromCat.add(new ListValueVO(listValueVO.getLabel(), listValueVO.getValue()));
		    }
		}
	}
	private void addCatList(final ArrayList<ListValueVO> fromCatList,final ArrayList<ListValueVO> toCatList,ArrayList<?> transferRulCatList, String categoryCode) {
		ChannelTransferRuleVO channelTransferRuleVO;
		 boolean isCatMatchedNew = false;
		for (int i = 0, k = transferRulCatList.size(); i < k; i++) {
		    channelTransferRuleVO = (ChannelTransferRuleVO) transferRulCatList.get(i);
		    if (categoryCode.equals(channelTransferRuleVO.getToCategory())) {
		        isCatMatchedNew = true;
		    }
		    if (isCatMatchedNew) {                       
		        fromCatList.add(new ListValueVO(channelTransferRuleVO.getToCategoryDes(), channelTransferRuleVO.getDomainCode() + ":" + channelTransferRuleVO.getToCategory()));
		        toCatList.add(new ListValueVO(channelTransferRuleVO.getToCategoryDes(), channelTransferRuleVO.getFromCategory() + ":" + channelTransferRuleVO.getToCategory()));
		    }
		}
		
	}
	@Override
	public boolean displayUserBalanceReportList(HttpServletRequest request,HttpServletResponse response,UsersReportModel usersReportModel,UserVO userVO, Model model,BindingResult bindingResult) {		
								
		final String methodName = "displayUserBalanceReportList";  
        debugMethodEntered(methodName);
        StringBuilder loggerValue= new StringBuilder(); 
        String datePattern=PretupsI.TIMESTAMP_DATESPACEHHMMSS;
        Connection con = null;
        MComConnectionI mcomCon = null;
        ArrayList<?> userList = new ArrayList<>();
        String tempCatCode = "";
        ChannelUserWebDAO channelUserWebDAO=null;
        
        try {  
        	channelUserWebDAO = new ChannelUserWebDAO();
			/* for field validation Start */
			fieldValidation(request, usersReportModel, model, bindingResult);
			if (bindingResult.hasFieldErrors()) {
				return false;
			}
			/* for field validation End */
			 ZeroBalanceCounterDetailsRptDAO zeroBalanceCounterDetailsRptDAO = new ZeroBalanceCounterDetailsRptDAO();
			 new CategoryWebDAO();
			 userVO.getCategoryVO().getSequenceNumber();
			 usersReportModel.setThresholdTypeList(LookupsCache.loadLookupDropDown(PretupsI.THRESHOLD_COUNTER_TYPE, true));			 			 
			 setThresholdName(usersReportModel);
			 //date range for 60 days validation
			 Date fromd = BTSLUtil.getDateFromDateString(usersReportModel.getFromDate());
			 Date tod = BTSLUtil.getDateFromDateString(usersReportModel.getToDate());
			 int diff = BTSLUtil.getDifferenceInUtilDates(fromd, tod);
			 if (diff > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_REPORT_MAX_DATEDIFF))).intValue()) {
			      model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("btsl.date.error.datecompare",new String[] { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_REPORT_MAX_DATEDIFF))).intValue()) }));
			      return false;
			 }			 						 
        	setDateTime(usersReportModel, datePattern); 
            usersReportModel.setNetworkCode(userVO.getNetworkID());
            usersReportModel.setNetworkName(userVO.getNetworkName());
            usersReportModel.setReportHeaderName(userVO.getReportHeaderName());
            ListValueVO listValueVO = null;
            //new DownloadCSVReportsBL();
            mcomCon = new MComConnection();          
			con=mcomCon.getConnection();			                    
            if (!BTSLUtil.isNullString(usersReportModel.getMsisdn())){// MSISDN based            	
            	// check if the msisdn belongs to login user network
            	final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(usersReportModel.getMsisdn())));      
        		if (prefixVO == null || !prefixVO.getNetworkCode().equals(usersReportModel.getNetworkCode())) {
                    final String arr1[] = { usersReportModel.getMsisdn(), usersReportModel.getNetworkName() };
                    loggerValue.setLength(0);
                    loggerValue.append("Error: MSISDN Number");
                    loggerValue.append(usersReportModel.getMsisdn());
                    loggerValue.append(" not belongs to ");
                    loggerValue.append(usersReportModel.getNetworkName());
                    loggerValue.append("network");
                    _log.error(methodName,  loggerValue);                   
                    model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2s.reports.error.msisdnnotinsamenetwork", arr1));
            		return false;
                }
            	final String status = "'" + PretupsI.USER_STATUS_NEW + "','" + PretupsI.USER_STATUS_CANCELED + "','" + PretupsI.USER_STATUS_DELETED + "'";
                final String statusUsed = PretupsI.STATUS_NOTIN;
                final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                final String filteredMSISDN = PretupsBL.getFilteredMSISDN(usersReportModel.getMsisdn());
                ChannelUserVO channelUserVO = null;
                // load user details on the basis of msisdn
                channelUserVO = loadChannelUser(userVO, con, status,statusUsed, channelUserDAO, filteredMSISDN);               

                if (!(PretupsBL.getFilteredMSISDN(usersReportModel.getMsisdn()).equals(userVO.getMsisdn())) && (channelUserVO == null || PretupsI.STAFF_USER_TYPE.equals(channelUserVO.getUserType()))) {                      
    			    String[] ar ;
    		        ar = new String[1];
    		        ar[0] = usersReportModel.getMsisdn();                       
    		        model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2s.reports.error.nouserbymsisdn", ar));
    				return false;
    		    }
                    // check msisdn belongs to hierarchy of login user if hierarchy
                    // is allowed to login user
                List<?> channelUserList = getChannelUserList(userVO, con,channelUserWebDAO, channelUserDAO);
        		if (channelUserList != null && !channelUserList.isEmpty()
        				&& !(channelUserVO.getCategoryVO().getAgentDomainCodeforCategory()).equals(userVO.getCategoryVO().getDomainCodeforCategory())) {
        		        final String arr1[] = { usersReportModel.getMsisdn() };
        		        loggerValue.setLength(0);
        		        loggerValue.append("Error: MSISDN Number");
        		        loggerValue.append(usersReportModel.getMsisdn());
        		        loggerValue.append(" does not belong to channel domain");
        		        _log.error("userNotinDomain",  loggerValue );                          
        		        model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2s.reports.error.usernotindomain", arr1));
        				return false;
        		}
                validateMsisdn(filteredMSISDN, userVO, usersReportModel, channelUserVO, con);              
           
            }
            else {
                setZoneName(usersReportModel);
                listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getDomainCode(), usersReportModel.getDomainList());
                usersReportModel.setDomainName(listValueVO.getLabel());
                final ChannelUserReportDAO channelUserReportDAO = new ChannelUserReportDAO();
                if (usersReportModel.getParentCategoryCode().equals(TypesI.ALL)) {
                	usersReportModel.setCategoryName(TypesI.ALL);               	
                    userList = channelUserReportDAO.loadUserListBasisOfZoneDomainCategory(con, PretupsI.ALL, usersReportModel.getDomainCode(), usersReportModel.getZoneCode(), usersReportModel.getUserName(), userVO.getUserID());
                } else {
                	if("%%%".equalsIgnoreCase(usersReportModel.getUserName())){
                		model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2s.reports.error.userName"));
                		return false;
                	}
                    listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getParentCategoryCode(), usersReportModel.getParentCategoryList());
                    usersReportModel.setCategoryName(listValueVO.getLabel());
                    final String[] arr = usersReportModel.getParentCategoryCode().split("\\:");
                    usersReportModel.setParentCategoryCode(arr[1]);
                    tempCatCode = usersReportModel.getParentCategoryCode();                  
                    String[] parts = usersReportModel.getUserName().split("\\(");					
                    usersReportModel.setUserName(parts[0]);
                    String userID = parts[1];
					userID = userID.replaceAll("\\)", "");
					usersReportModel.setUserID(userID);					
                    userList = channelUserReportDAO.loadUserListBasisOfZoneDomainCategory(con, arr[1], usersReportModel.getDomainCode(), usersReportModel.getZoneCode(), usersReportModel.getUserName(), userVO.getUserID());
                }
                setUserReportModel(usersReportModel, userList, tempCatCode);
            }
            setServiceTypeName(usersReportModel);
            setToTransferStatusName(usersReportModel);                      
            getZeroBalanceCounterDetailsList(usersReportModel, con,	zeroBalanceCounterDetailsRptDAO);         
        } catch (BTSLBaseException | ParseException | ValidatorException | IOException | SAXException | SQLException e) {
            _log.errorTrace(methodName, e);
          
        } finally {
        	if(mcomCon != null){
        		mcomCon.close("CRUZeroBalanceCounterDetailsServiceImpl#displayUserBalanceReportList");
        		mcomCon=null;
        	}           
        }              		
		return true;		
	}
	private void setToTransferStatusName(UsersReportModel usersReportModel) {
		ListValueVO listValueVO;
		if (!BTSLUtil.isNullString(usersReportModel.getTransferStatus())) {
		    listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getTransferStatus(), usersReportModel.getTransferStatusList());
		    usersReportModel.setTransferStatusName(listValueVO.getLabel());
		}
	}
	private void debugMethodEntered(final String methodName) {
		if (_log.isDebugEnabled()) {
            _log.debug("Method Name :" , methodName);
        }
	}
	private List<?> getChannelUserList(UserVO userVO, Connection con,
			ChannelUserWebDAO channelUserWebDAO,
			final ChannelUserDAO channelUserDAO) throws BTSLBaseException {
		List<?> channelUserList ;		
		if ("Y".equals(userVO.getCategoryVO().getHierarchyAllowed())) {
		    channelUserList = channelUserWebDAO.loadChannelUserHierarchy(con, userVO.getUserCode(), false);
		} else {
		    channelUserList = channelUserWebDAO.loadChannelUserHierarchy(con, (channelUserDAO.loadChannelUser(con, userVO.getParentID())).getUserCode(), false);
		}
		return channelUserList;
	}
		
	private void setServiceTypeName(UsersReportModel usersReportModel) {
		ListValueVO listValueVO;
		if (!BTSLUtil.isNullString(usersReportModel.getServiceType()) && usersReportModel.getServiceType().equals(TypesI.ALL)) {
			usersReportModel.setServiceTypeName(PretupsRestUtil.getMessageString(LIST_ALL));
		} else if (!BTSLUtil.isNullString(usersReportModel.getServiceType())) {
		    listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getServiceType(), usersReportModel.getServiceTypeList());
		    usersReportModel.setServiceTypeName(listValueVO.getLabel());
		}
	}
	private void getZeroBalanceCounterDetailsList(
			UsersReportModel usersReportModel, Connection con,
			ZeroBalanceCounterDetailsRptDAO zeroBalanceCounterDetailsRptDAO) {
		String rptCode;
		if(TypesI.CHANNEL_USER_TYPE.equalsIgnoreCase(usersReportModel.getUserType())){               	        	
		  rptCode = "ZBALCNTDT02";
		  usersReportModel.setrptCode(rptCode);                	              	
		  List<ChannelReportsUserVO> zeroBalanceCounterDetailsList;			
		  zeroBalanceCounterDetailsList = zeroBalanceCounterDetailsRptDAO.loadZeroBalanceCounterChnlUserDetailsReport(con, usersReportModel);			
		  usersReportModel.setZeroBalanceCounterDetailsList(zeroBalanceCounterDetailsList);                  	
		}
		if(TypesI.OPERATOR_USER_TYPE.equalsIgnoreCase(usersReportModel.getUserType())){ 
		  rptCode = "ZBALCNTDT01";
		  usersReportModel.setrptCode(rptCode);                	              	
		  List<ChannelReportsUserVO> zeroBalanceCounterDetailsList = zeroBalanceCounterDetailsRptDAO.loadZeroBalanceCounterDetailsReport(con, usersReportModel);
		  usersReportModel.setZeroBalanceCounterDetailsList(zeroBalanceCounterDetailsList);                	          
        }
	}
	private void setUserReportModel(UsersReportModel usersReportModel,
			ArrayList<?> userList, String tempCatCode) {
		if (usersReportModel.getUserName().equalsIgnoreCase(PretupsRestUtil.getMessageString(LIST_ALL))) {
			usersReportModel.setUserID(PretupsI.ALL);
		} else if (userList == null || userList.isEmpty()) {
			usersReportModel.setParentCategoryCode(usersReportModel.getParentCategoryCode());
		    new BTSLMessages("user.selectcategoryforedit.error.usernotexist", "loadUserBalance");
		} else if (userList.size() == 1) {
		    final ChannelUserTransferVO channelUserTransferVO = (ChannelUserTransferVO) userList.get(0);
		    usersReportModel.setUserName(channelUserTransferVO.getUserName());
		    usersReportModel.setUserID(channelUserTransferVO.getUserID());
		} else if (userList.size() > 1) {                    
		    setUserNameAndCategoryCode(usersReportModel, userList,tempCatCode);
		}
	}
	private ChannelUserVO loadChannelUser(UserVO userVO, Connection con,
			final String status, final String statusUsed,final ChannelUserDAO channelUserDAO, final String filteredMSISDN)throws BTSLBaseException {
		    ChannelUserVO channelUserVO;
		if (PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())) {
		    channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMSISDN, null, statusUsed, status);
		} else {
		    channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMSISDN, userVO.getUserID(), statusUsed, status);
		}
		return channelUserVO;
	}
	private void setZoneName(UsersReportModel usersReportModel) {
		ListValueVO listValueVO;
		if (usersReportModel.getZoneCode().equals(TypesI.ALL)) {
			usersReportModel.setZoneName(PretupsRestUtil.getMessageString(LIST_ALL));
		} else {
		    listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getZoneCode(), usersReportModel.getZoneList());
		    usersReportModel.setZoneName(listValueVO.getLabel());
		}
	}
	private void setDateTime(UsersReportModel usersReportModel,String datePattern) throws ParseException {
		SimpleDateFormat dateFormat;
		Timestamp fromDateTimestamp;
		Timestamp toDateTimestamp;
		Date fromDate;
		Date toDate;
		if(!BTSLUtil.isNullString(usersReportModel.getFromDate())) {                                                       	
		    usersReportModel.setFromDateTime(BTSLUtil.getDateFromDateString(usersReportModel.getFromDate()+" 00:00:00", datePattern));          
		    dateFormat = new SimpleDateFormat(datePattern);
			fromDate = dateFormat.parse(BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
			fromDateTimestamp = new Timestamp(fromDate.getTime());
			usersReportModel.setFromDateTimestamp(fromDateTimestamp);
		}
		 if(!BTSLUtil.isNullString(usersReportModel.getToDate())) {                                            	           
			usersReportModel.setToDateTime(BTSLUtil.getDateFromDateString(usersReportModel.getToDate()+" 23:59:00", datePattern));	        	
			dateFormat = new SimpleDateFormat(datePattern);
			toDate = dateFormat.parse(BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
			toDateTimestamp = new Timestamp(toDate.getTime());
			usersReportModel.setToDateTimestamp(toDateTimestamp);
		 }
	}
	private void setThresholdName(UsersReportModel usersReportModel) {
		if (!BTSLUtil.isNullString(usersReportModel.getThresholdType())) {
			   
			    ListValueVO listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getThresholdType(), usersReportModel.getThresholdTypeList());
		        if(PretupsI.THRESHOLD_TYPE_ALL.equalsIgnoreCase(usersReportModel.getThresholdType())){
		        	usersReportModel.setThresholdName(PretupsI.THRESHOLD_TYPE_ALL);	
		        }else{	                	
		        	usersReportModel.setThresholdName(listValueVO.getLabel());
		        }
		    }
	}
	private void fieldValidation(HttpServletRequest request,
			UsersReportModel usersReportModel, Model model,
			BindingResult bindingResult) throws ValidatorException,
			IOException, SAXException {
		if (request.getParameter("submitThreshold") != null) {
			CommonValidator commonValidator = new CommonValidator(
			"configfiles/c2sreports/validator-zeroBalCounterDetails.xml", usersReportModel,
			"ZeroBalanceCounterDetailsPanelOne");							
			Map<String, String> errorMessages = commonValidator.validateModel();				
			PretupsRestUtil pru = new PretupsRestUtil();
			pru.processFieldError(errorMessages, bindingResult);
			model.addAttribute(PANEL_NO, "Panel-One");
			request.getSession().setAttribute(PANEL_NO, "Panel-One");
		}
		if (request.getParameter("submitChannelCategory") != null) {
			CommonValidator commonValidator = new CommonValidator(
			"configfiles/c2sreports/validator-zeroBalCounterDetails.xml", usersReportModel,
			"ZeroBalanceCounterDetailsPanelTwo");  
			Map<String, String> errorMessages = commonValidator.validateModel();
			PretupsRestUtil pru = new PretupsRestUtil();
			pru.processFieldError(errorMessages, bindingResult);
			model.addAttribute(PANEL_NO, "Panel-Two");
			request.getSession().setAttribute(PANEL_NO, "Panel-Two");
		}
	}
	private void setUserNameAndCategoryCode(UsersReportModel usersReportModel,ArrayList<?> userList, String tempCatCode) {
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
		    new BTSLMessages("user.selectcategoryforedit.error.usermorethanone", "loadUserBalance");
		}
	}	
	private void validateMsisdn(String filteredMSISDN, UserVO userVO,UsersReportModel usersReportModel, ChannelUserVO channelUserVO,	Connection con) throws BTSLBaseException {
		
		 if (filteredMSISDN.equals(userVO.getMsisdn()) && (!(PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())))) {
	            usersReportModel.setUserName(userVO.getUserName());
	            usersReportModel.setUserID(userVO.getUserID());

	            usersReportModel.setTransferUserCategoryCode(userVO.getCategoryCode());
	            usersReportModel.setCategoryName(userVO.getCategoryVO().getCategoryName());

	            usersReportModel.setDomainCode(userVO.getCategoryVO().getDomainCodeforCategory());
	            usersReportModel.setDomainName(userVO.getDomainName());

	            usersReportModel.setParentCategoryCode(userVO.getCategoryVO().getCategoryCode());

	            UserGeographiesVO userGeoVO =  userVO.getGeographicalAreaList().get(0);
	            usersReportModel.setZoneCode(userGeoVO.getGraphDomainCode());
	            usersReportModel.setZoneName(userGeoVO.getGraphDomainName());
	        }
		 else {
	            usersReportModel.setUserName(channelUserVO.getUserName());
	            usersReportModel.setUserID(channelUserVO.getUserID());

	            usersReportModel.setTransferUserCategoryCode(channelUserVO.getCategoryVO().getCategoryCode());
	            usersReportModel.setCategoryName(channelUserVO.getCategoryVO().getCategoryName());

	            usersReportModel.setDomainCode(channelUserVO.getCategoryVO().getDomainCodeforCategory());
	            usersReportModel.setDomainName(channelUserVO.getDomainName());
	            usersReportModel.setParentCategoryCode(channelUserVO.getCategoryVO().getCategoryCode());

	            ArrayList<?> list = new GeographicalDomainDAO().loadGeoDomainCodeHeirarchy(con, userVO.getCategoryVO().getGrphDomainType(), channelUserVO.getGeographicalCode(), false);
	            GeographicalDomainVO geographicalDomainVO = (GeographicalDomainVO) list.get(0);

	            usersReportModel.setZoneCode(geographicalDomainVO.getGrphDomainCode());
	            usersReportModel.setZoneName(geographicalDomainVO.getGrphDomainName());
	        }
	}
	@Override
	public String downloadCSVReportZeroBalCounterReportFile(UsersReportModel usersReportModel) {
		
		 final String methodName ="downloadCSVReportZeroBalCounterReportFile";
		 DownloadCSVReports downloadCSVReports = new  DownloadCSVReports();
		 Connection con = null ;
	     MComConnectionI mcomCon = null;
	     String rptCode = "ZBALCNTDT02";
      	 String filePath = null;
	     try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			
			filePath = downloadCSVReports.prepareDataForZeroBalanceCounterDetails(usersReportModel,rptCode, con);
			
		} catch (BTSLBaseException |SQLException | ParseException e) {
			 _log.errorTrace(methodName, e);
		} 
		 return filePath;				
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<ListValueVO> getUserList(UserVO userVO, String zoneCode,String domainCode, String userName, String parentCategoryCode) {
		
		 if (_log.isDebugEnabled()) { 
		   _log.debug("CRUZeroBalanceCounterDetailsServiceImpl#loadUserList", PretupsI.ENTERED +"");
		 }	
		   final String methodName ="getUserList";
		    Connection con = null;
			MComConnectionI mcomCon = null;
			ChannelUserReportDAO channelUserDAO = null;
			List<ListValueVO> userList = null;
			final String[] arr = parentCategoryCode.split(":");
		  try {			
			    mcomCon = new MComConnection();			
				con=mcomCon.getConnection();			
				channelUserDAO = new ChannelUserReportDAO(); 
				
				if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
	                if (parentCategoryCode.equals(PretupsI.ALL)) {
	                    userList = channelUserDAO.loadUserListOnZoneCategoryHierarchy(con, PretupsI.ALL,zoneCode, userName, userVO.getUserID(),domainCode);
	                } else {
	                    userList = channelUserDAO.loadUserListOnZoneCategoryHierarchy(con, arr[1],zoneCode, userName, userVO.getUserID(), domainCode);
	                }
	            } else {
	                if (parentCategoryCode.equals(PretupsI.ALL)) {
	                    userList = channelUserDAO.loadUserListOnZoneDomainCategory(con, PretupsI.ALL,zoneCode, null, "%" + userName + "%", userVO.getUserID(),
	                       domainCode);
	                } else {
	                    userList = channelUserDAO.loadUserListOnZoneDomainCategory(con, arr[1], zoneCode, null, "%" + userName + "%", userVO.getUserID(), domainCode);
	                }
	            } 
			}catch(BTSLBaseException | SQLException e){
			  _log.errorTrace(methodName, e);
		  }		 
		finally {
			if(mcomCon != null){
				mcomCon.close("CRUZeroBalanceCounterDetailsServiceImpl#getUserList");
				mcomCon=null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug("CRUZeroBalanceCounterDetailsServiceImpl#loadUserList", PretupsI.EXITED);
			}
		}
		return userList;
	}
		
}
