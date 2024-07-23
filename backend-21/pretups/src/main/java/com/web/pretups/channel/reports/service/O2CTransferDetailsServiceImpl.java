package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

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
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO;
import com.btsl.pretups.channel.reports.businesslogic.O2CTransferDetailsRptDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.DownloadCSVReports;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;







/**
 * @author rahul.arya
 *
 */
@Service("o2CTransferDetailsService")
public class O2CTransferDetailsServiceImpl implements O2CTransferDetailsService  {

	
	
	public static final Log _log = LogFactory.getLog(O2CTransferDetailsServiceImpl.class.getName());
	private static final String MODEL_KEY = "usersReportModel";
	
	private static final String FAIL_KEY = "fail";
	
	private static final String LIST_ALL ="list.all";
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void loadO2CTransferDetails(HttpServletRequest request,HttpServletResponse response,UsersReportModel usersReportModel,UserVO userVO,org.springframework.ui.Model model) throws IOException,BTSLBaseException,ParseException, ServletException
	{
	    final String methodName = "loadO2CTransferDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, PretupsI.ENTERED);
        }
      
        
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
      
        	BTSLCommonController bTSLCommonController = new BTSLCommonController();
        	
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
		    if(TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode()))
		    	usersReportModel.setZoneList(new GeographicalDomainDAO().loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID()));
		    else
		    	usersReportModel.setZoneList(userVO.getGeographicalAreaList());
		    usersReportModel.setTxnSubTypeList(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE, true));
		    usersReportModel.setTransferCategoryList(LookupsCache.loadLookupDropDown(PretupsI.C2C_TRANSFER_TYPE, true));
		    usersReportModel.setNetworkCode(userVO.getNetworkID());
		    usersReportModel.setUserType(userVO.getUserType());
		    usersReportModel.setCategorySeqNo(((userVO.getCategoryVO().getSequenceNumber()) + "").trim());

            ListValueVO listValueVO = null;
            bTSLCommonController.commonGeographicDetails(usersReportModel, listValueVO);

            final ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
            

            listValueVO = null;

            usersReportModel.setLoggedInUserCategoryCode(userVO.getCategoryVO().getCategoryCode());
            if (userVO.isStaffUser()) {
            	usersReportModel.setLoggedInUserName(userVO.getParentName());
            } else {
            	usersReportModel.setLoggedInUserName(userVO.getUserName());
            }
            usersReportModel.setLoginUserID(userVO.getUserID());
            final ArrayList loggedInUserDomainList = new ArrayList();

            final ArrayList fromCatList = new ArrayList();
            final ArrayList toCatList = new ArrayList();
            ArrayList transferRulCatList = null;

            ArrayList domainList = userVO.getDomainList();
            if ((domainList == null || domainList.isEmpty()) && PretupsI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO
                .getCategoryVO().getFixedDomains())) {
                domainList = new DomainDAO().loadCategoryDomainList(con);
            } else if (domainList.size() == 1) {
                listValueVO = (ListValueVO) domainList.get(0);
                usersReportModel.setDomainCode(listValueVO.getValue());
                usersReportModel.setDomainName(listValueVO.getLabel());
            }
            usersReportModel.setDomainList(BTSLUtil.displayDomainList(domainList));
            if (usersReportModel.getDomainListSize() == 0) {
                loggedInUserDomainList.add(new ListValueVO(userVO.getDomainName(), userVO.getDomainID()));
                usersReportModel.setDomainList(loggedInUserDomainList);
                usersReportModel.setDomainCode(userVO.getDomainID());
                usersReportModel.setDomainName(userVO.getDomainName());
            }
           
            transferRulCatList = channelTransferRuleWebDAO.loadChannelTransferRuleVOList(con, usersReportModel.getNetworkCode(), usersReportModel.getDomainCode(),
                    PretupsI.TRANSFER_RULE_TYPE_OPT);
            ChannelTransferRuleVO channelTransferRuleVO = null;
            boolean isForAllCategory = true;
            String categoryCode = null;
            if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                isForAllCategory = false;
                categoryCode = userVO.getCategoryCode();
            }
            boolean isCatMatched = false;
            // for operator user load all the categories.
            if (isForAllCategory) {
                for (int i = 0, k = transferRulCatList.size(); i < k; i++) {
                    channelTransferRuleVO = (ChannelTransferRuleVO) transferRulCatList.get(i);
                    
                    fromCatList.add(new ListValueVO(channelTransferRuleVO.getToCategoryDes(), channelTransferRuleVO.getDomainCode() + ":" + channelTransferRuleVO
                            .getToCategory()));
                    toCatList.add(new ListValueVO(channelTransferRuleVO.getToCategoryDes(), channelTransferRuleVO.getFromCategory() + ":" + channelTransferRuleVO
                        .getToCategory()));
                }

            }
            // for channel user load the category down the hierarchy
            else {
                for (int i = 0, k = transferRulCatList.size(); i < k; i++) {
                    channelTransferRuleVO = (ChannelTransferRuleVO) transferRulCatList.get(i);
                    if (categoryCode.equals(channelTransferRuleVO.getToCategory())) {
                        isCatMatched = true;
                    }
                    if (isCatMatched) {
                        
                        fromCatList.add(new ListValueVO(channelTransferRuleVO.getToCategoryDes(), channelTransferRuleVO.getDomainCode() + ":" + channelTransferRuleVO
                                .getToCategory()));
                        toCatList.add(new ListValueVO(channelTransferRuleVO.getToCategoryDes(), channelTransferRuleVO.getFromCategory() + ":" + channelTransferRuleVO
                            .getToCategory()));

                    }
                }
            }

            ListValueVO listValueVONext = null;
           
            final ArrayList tempFromCat = new ArrayList();
            boolean flag = true;
            for (int i = 0, j = fromCatList.size(); i < j; i++) {
                listValueVO = (ListValueVO) fromCatList.get(i);
                flag = true;
                for (int k = i + 1, l = fromCatList.size(); k < l; k++) {
                    listValueVONext = (ListValueVO) fromCatList.get(k);
                    if (listValueVO.getValue().equals(listValueVONext.getValue())) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    tempFromCat.add(new ListValueVO(listValueVO.getLabel(), listValueVO.getValue()));
                }
            }
            usersReportModel.setFromCategoryList(tempFromCat);
            
            listValueVONext = null;
            for (int i = 0, j = toCatList.size(); i < j - 1;) {
                listValueVO = (ListValueVO) toCatList.get(i);
                listValueVONext = (ListValueVO) toCatList.get(i + 1);
                if (listValueVO.getValue().equals(listValueVONext.getValue())) {
                    toCatList.remove(i + 1);
                    j--;
                } else {
                    i++;
                }

            }

            usersReportModel.setToCategoryList(toCatList);
            model.addAttribute(MODEL_KEY, usersReportModel);
        } catch (BTSLBaseException | SQLException e) {
            _log.errorTrace(methodName, e);
            
        }  finally {
        	if(mcomCon != null)
        	{mcomCon.close("O2CTransferDetailsServiceImpl#loadO2CTransferDetails");
        	mcomCon=null;
        	}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exit");
            }
        }
        
		
		
		
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean loadEnquiryList(HttpServletRequest request, HttpServletResponse response, UsersReportModel usersReportModel, UserVO userVO,org.springframework.ui.Model model,BindingResult bindingResult) throws ValidatorException, IOException, SAXException {
		final String methodName = "loadEnquiryList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        Boolean bool = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        ArrayList userList = null;
        String tempfromCatCode = "";
        String temptoCatCode = "";
        final Date currentDate = new Date();
        
       CommonValidator commonValidator = new CommonValidator(
				"configfiles/c2sreports/validator-O2CTransferDetails.xml",
				usersReportModel, "O2CTrfDetails");
		Map<String, String> errorMessages = commonValidator
				.validateModel();
		PretupsRestUtil pru = new PretupsRestUtil();
		pru.processFieldError(errorMessages, bindingResult);
        
		if (bindingResult.hasFieldErrors()) {
			request.getSession().setAttribute(MODEL_KEY, usersReportModel);
			return false;
		}
        try {
            
        	BTSLCommonController bTSLCommonController = new BTSLCommonController();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final ChannelUserReportDAO channelUserDAO = new ChannelUserReportDAO();
            
            Date frDate = null;
            Date tDate = null;
            Date fromDate = null;
            Date toDate = null;
        	if(!BTSLUtil.isNullString(usersReportModel.getFromDate())) {
                frDate=BTSLUtil.getDateFromDateString(usersReportModel.getFromDate());
                
                if(!BTSLUtil.isNullString(usersReportModel.getFromTime()))
                {
                	fromDate=BTSLUtil.getDateFromDateString(usersReportModel.getFromDate()+" "+usersReportModel.getFromTime()+":00", PretupsI.TIMESTAMP_DATESPACEHHMMSS);
                	usersReportModel.setFromDateTime(fromDate);
                }
                else
                {
                	fromDate=BTSLUtil.getDateFromDateString(usersReportModel.getFromDate()+" 00:00:00", PretupsI.TIMESTAMP_DATESPACEHHMMSS);
                	usersReportModel.setFromDateTime(fromDate);
                }
            }
			if(!BTSLUtil.isNullString(usersReportModel.getToDate())) {
                tDate=BTSLUtil.getDateFromDateString(usersReportModel.getToDate());
                if(!BTSLUtil.isNullString(usersReportModel.getToTime()))
                {
                	toDate=BTSLUtil.getDateFromDateString(usersReportModel.getToDate()+" "+usersReportModel.getToTime()+":59", PretupsI.TIMESTAMP_DATESPACEHHMMSS);
                	usersReportModel.setToDateTime(toDate);
                }
                else
                {
                	toDate=BTSLUtil.getDateFromDateString(usersReportModel.getToDate()+" 23:59:59", "ddd/MM/yy HH:mm:ss");
                	usersReportModel.setToDateTime(toDate);
                }
                
            }
		
	        tempfromCatCode = usersReportModel.getFromtransferCategoryCode();
            temptoCatCode = usersReportModel.getTotransferCategoryCode();
            bTSLCommonController.commonReportDateFormat(usersReportModel, methodName);
            
            if ("DAILY".equals(usersReportModel.getRequestType())) {
                if ((!(usersReportModel.getToDate().equalsIgnoreCase(BTSLUtil.getDateStringFromDate(currentDate)))) && (!(usersReportModel.getFromDate().equalsIgnoreCase(BTSLUtil
                    .getDateStringFromDate(currentDate))))) {
                    final ChannelUserVO channelUserVo = (ChannelUserVO) userVO;
                    if (!BTSLUtil.checkDateFromMisDates(frDate, tDate, channelUserVo, ProcessI.C2SMIS)) {
                        final String[] arr = { BTSLUtil.getDateStringFromDate(channelUserVo.getC2sMisFromDate()), BTSLUtil.getDateStringFromDate(channelUserVo
                            .getC2sMisToDate()) };
                        throw new BTSLBaseException("report.date.range.error.msg", arr, methodName);
                        
                        
                    }
                }
            }
            
            ListValueVO listValueVO = null;
            displayO2CHistory(usersReportModel, userVO, listValueVO, request);
            usersReportModel.setNetworkCode(userVO.getNetworkID());
            usersReportModel.setNetworkName(userVO.getNetworkName());
            usersReportModel.setReportHeaderName(userVO.getReportHeaderName());
            
            if(usersReportModel.getDomainCode().equals(PretupsI.ALL))
            {    
            	usersReportModel.setDomainName(PretupsRestUtil.getMessageString(LIST_ALL));
                if(usersReportModel.getDomainList() != null && !usersReportModel.getDomainList().isEmpty())
                {
                    String domainCode = "";
                    for(int i=0, j=usersReportModel.getDomainList().size(); i<j; i++)
                    {
                        listValueVO = (ListValueVO)usersReportModel.getDomainList().get(i);
                        domainCode = domainCode + listValueVO.getValue()+"','";
                    }
                    domainCode = domainCode.substring(0,domainCode.length()-3);
                    usersReportModel.setDomainListString(domainCode);
                }
            }
            else
            {
            listValueVO=BTSLUtil.getOptionDesc(usersReportModel.getDomainCode(),usersReportModel.getDomainList());
            usersReportModel.setDomainName(listValueVO.getLabel());
            usersReportModel.setDomainListString(usersReportModel.getDomainCode());
            }

            if (!BTSLUtil.isNullString(usersReportModel.getTxnSubType()) && (usersReportModel.getTxnSubType().trim()).equals(PretupsI.ALL)) {
            	usersReportModel.setTxnSubTypeName(PretupsRestUtil.getMessageString(LIST_ALL)); 
            	
            } else if (!BTSLUtil.isNullString(usersReportModel.getTxnSubType())) {
                listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getTxnSubType(), usersReportModel.getTxnSubTypeList());
                usersReportModel.setTxnSubTypeName(listValueVO.getLabel());
            }
            if (!BTSLUtil.isNullString(usersReportModel.getTransferCategory()) && usersReportModel.getTransferCategory().equals(PretupsI.ALL)) {
            	usersReportModel.setTransferCategoryName(PretupsRestUtil.getMessageString(LIST_ALL));
            } else if (!BTSLUtil.isNullString(usersReportModel.getTransferCategory())) {
                listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getTransferCategory(), usersReportModel.getTransferCategoryList());
                usersReportModel.setTransferCategoryName(listValueVO.getLabel());
            }
            String user = usersReportModel.getUserName();
            try{
            if(!("ALL").equals(user))
            {
            String[] parts = user.split("\\(");
			String userName = parts[0]; 
			usersReportModel.setUserName(userName);
			String a = parts[1];
			String[] w1=a.split("\\)");
			usersReportModel.setUserID(w1[0]);
            }
            }
            catch(Exception e)
            {
            	_log.error(methodName, "Name selected did not had ID "
						+ e);
				_log.errorTrace(methodName, e);
            }
            if (!BTSLUtil.isNullString(usersReportModel.getUserName())) {
            	
                if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                    userList = channelUserDAO.loadUserListOnZoneCategoryHierarchy(con, usersReportModel.getFromtransferCategoryCode(), usersReportModel.getZoneCode(), usersReportModel.getUserName(),
                    		usersReportModel.getLoginUserID(), usersReportModel.getDomainCode());
                } else {
                    userList = channelUserDAO.loadUserListOnZoneDomainCategory(con, usersReportModel.getFromtransferCategoryCode(), usersReportModel.getZoneCode(), null, usersReportModel
                        .getUserName(), usersReportModel.getLoginUserID(), usersReportModel.getDomainCode());
                }
            }

            if (!BTSLUtil.isNullString(usersReportModel.getUserName()) && usersReportModel.getUserName().equals(
            		PretupsRestUtil.getMessageString(LIST_ALL))) {
            	usersReportModel.setUserID(PretupsI.ALL);
            	bool = true;
                
            } else if (!BTSLUtil.isNullString(usersReportModel.getUserName()) && (userList == null || userList.isEmpty())) {
            	usersReportModel.setFromtransferCategoryCode(tempfromCatCode);
            	usersReportModel.setTotransferCategoryCode(temptoCatCode);
                model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransfers.error.fromusernotexist"));
                bool = false;
                
            } else if (!BTSLUtil.isNullString(usersReportModel.getUserName()) && userList.size() == 1) {
                final ListValueVO channelUserTransferVO = (ListValueVO) userList.get(0);
                usersReportModel.setUserName(channelUserTransferVO.getLabel());
                usersReportModel.setUserID(channelUserTransferVO.getValue());
                bool = true;
                
            } else if (!BTSLUtil.isNullString(usersReportModel.getUserName()) && userList.size() > 1) {
                /*
                 * This is the case when userList size greater than 1
                 * if user click the submit button(selectcategoryForEdit.jsp)
                 * after performing
                 * search through searchUser and select one form the shown list
                 * at that time we
                 * set the userid on the form(becs two user have the same name
                 * but different id)
                 * so here we check the userId is null or not it is not null
                 * iterate the list and open the screen
                 * in edit mode corresponding to the userid
                 */
                boolean flag = true;
                if (!BTSLUtil.isNullString(usersReportModel.getUserID())) {
                    for (int i = 0, j = userList.size(); i < j; i++) {
                        final ListValueVO channelUserTransferVO = (ListValueVO) userList.get(i);
                        if (usersReportModel.getUserID().equals(channelUserTransferVO.getValue())) {
                        	usersReportModel.setUserName(channelUserTransferVO.getLabel());
                            flag = false;
                            bool = true;
                            break;
                        }
                    }
                }
                if (flag) {
                	usersReportModel.setFromtransferCategoryCode(tempfromCatCode);
                	usersReportModel.setTotransferCategoryCode(temptoCatCode);
                    
                    
                    model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransfers.error.fromusermoreexist"));
                    bool = false;
                    
                }
            }
            

            if (BTSLUtil.isNullString(usersReportModel.getUserName())) {
                bool = true;
            }
            if(toDate!=null && fromDate!=null && toDate.compareTo(fromDate)<0){
				String[] ar=null;
				bool = false;
                throw new BTSLBaseException(this, methodName, "reports.validfromtimegreater.error.msg", 0, ar, methodName);
            }
            if(usersReportModel.getFromMonth()!=null && usersReportModel.getToMonth()!=null && usersReportModel.getToMonth().compareTo(usersReportModel.getFromMonth())<0)
            {
            	String[] ar=null;
				bool = false;
                throw new BTSLBaseException(this, methodName, "btsl.error.msg.frommonthbeforetomonth", 0, ar, methodName);
            }
            O2CTransferDetailsRptDAO o2CTransferDetailsRptDAO = new O2CTransferDetailsRptDAO(); 
            if(("DETAILS").equals(usersReportModel.getRequestType()) && ("CHANNEL").equals(usersReportModel.getUserType()))
            {
            	String rptCode = "02CTRFDET02";
            	final ArrayList<ChannelTransferVO> o2cTransferList = o2CTransferDetailsRptDAO .loado2cTransferDetailsChannelUserReport(con,usersReportModel);
            	
            	usersReportModel.setTransferList(o2cTransferList); 
            	usersReportModel.setrptCode(rptCode);
            	 usersReportModel.setTransferListSize(o2cTransferList.size());
            }
            if(("DETAILS").equals(usersReportModel.getRequestType()) && ("OPERATOR").equals(usersReportModel.getUserType()))
            	
            {
            	String rptCode = "02CTRFDET01";
            	final ArrayList<ChannelTransferVO> o2cTransferList = o2CTransferDetailsRptDAO .loado2cTransferDetailsReport(con,usersReportModel);
            	usersReportModel.setTransferList(o2cTransferList); 
            	usersReportModel.setrptCode(rptCode);
                usersReportModel.setTransferListSize(o2cTransferList.size());
            	 
            }
            if(("DAILY").equals(usersReportModel.getRequestType()))
            	
            {
            	String rptCode = "02CTRFDLY01";
            	final ArrayList<ChannelTransferVO> o2cTransferList = o2CTransferDetailsRptDAO .loado2cTransferDailyDetailsReport(con,usersReportModel);
            	usersReportModel.setTransferList(o2cTransferList); 
            	usersReportModel.setrptCode(rptCode);
            	 usersReportModel.setTransferListSize(o2cTransferList.size());
            }
           
        } catch (BTSLBaseException e) {
            _log.errorTrace(methodName, e);
            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage(),e.getArgs()));
            request.getSession().setAttribute(MODEL_KEY, usersReportModel);
            return false;
            
        }  catch (Exception e) {
            _log.errorTrace(methodName, e);
            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
            request.getSession().setAttribute(MODEL_KEY, usersReportModel);
            return false;
            
            
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("O2CTransferDetailsServiceImpl#loadEnquirySearch");
        	mcomCon=null;
        	}
            if (_log.isDebugEnabled()) {
                _log.error(methodName, "Exiting");
            }
        }
        	request.getSession().setAttribute("usersReportModel", usersReportModel);
       return bool;
	}
	
	@SuppressWarnings("unchecked")
	@Override	
    public List<ListValueVO> loadC2cFromUserList(UserVO userVO,String zoneCode,String domainCode,String userName,String fromtransferCategoryCode,UsersReportModel usersReportModel) {

        final String methodName = "loadC2cFromUserList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserReportDAO channelUserDAO = null;

        
        ArrayList<ListValueVO> userList = new ArrayList<ListValueVO>();
        try {
            // ====== Throw Exception if You have performed an invalid
            // operation=================
            final long time = usersReportModel.getTime();
            long newTime = 0;
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " time: " + time + " newTime: " + newTime);
            }
            if (newTime != 0 && newTime != time) {
                throw new BTSLBaseException("common.securitymanager.error.invalidoperation");
            }
            // ===============================================================================================
           
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            channelUserDAO = new ChannelUserReportDAO();
            


            final String[] arr = fromtransferCategoryCode.split(":");

           
            if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                if (fromtransferCategoryCode.equals(PretupsI.ALL)) {
                    userList = channelUserDAO.loadUserListOnZoneCategoryHierarchy(con, PretupsI.ALL,zoneCode, userName, userVO.getUserID(),domainCode);
                } else {
                    userList = channelUserDAO.loadUserListOnZoneCategoryHierarchy(con, arr[1],zoneCode, userName, userVO.getUserID(), domainCode);
                }
            } else {
                if (fromtransferCategoryCode.equals(PretupsI.ALL)) {
                    userList = channelUserDAO.loadUserListOnZoneDomainCategory(con, PretupsI.ALL,zoneCode, null, "%" + userName + "%", userVO.getUserID(),
                       domainCode);
                } else {
                    userList = channelUserDAO.loadUserListOnZoneDomainCategory(con, arr[1], zoneCode, null, "%" + userName + "%", userVO.getUserID(), domainCode);
                }
            }
            usersReportModel.setUserList(userList);
            usersReportModel.setUserListSize(userList);

            
            return userList;

        } catch (Exception e) {
             _log.error(methodName, "Exceptin:e=" + e);
             _log.errorTrace(methodName, e);
            
        } finally {
        	if(mcomCon != null)
        	{
        	mcomCon.close("O2CTransferDetailsServiceImpl#loadC2cFromUserList");
        	mcomCon=null;
        	}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exit");
            }
        }
		return userList;
       
    }
	 /**
	 * @param thisModel
	 * @param userVO
	 * @param listValueVO
	 * @param request
	 */
	public void displayO2CHistory(UsersReportModel thisModel, UserVO userVO, ListValueVO listValueVO, HttpServletRequest request) {
		 BTSLCommonController btslCommonController =new BTSLCommonController();
	        thisModel.setNetworkCode(userVO.getNetworkID());
	        thisModel.setNetworkName(userVO.getNetworkName());
	        thisModel.setReportHeaderName(userVO.getReportHeaderName());
	        btslCommonController.domainValidate(thisModel, listValueVO, request);
	        if (!BTSLUtil.isNullString(thisModel.getTransferInOrOut())) {
	            if (thisModel.getTransferInOrOut().equalsIgnoreCase(PretupsI.ALL)) {
	                thisModel.setTransferInOrOutName(PretupsRestUtil.getMessageString(LIST_ALL));
	            } else if (thisModel.getTransferInOrOut().equalsIgnoreCase(PretupsI.IN)) {
	                thisModel.setTransferInOrOutName(PretupsRestUtil.getMessageString("c2s.reports.o2candc2creturnwithdraw.combo.in"));
	            } else if (thisModel.getTransferInOrOut().equalsIgnoreCase(PretupsI.OUT)) {
	                thisModel.setTransferInOrOutName(PretupsRestUtil.getMessageString("c2s.reports.o2candc2creturnwithdraw.combo.out"));
	            }
	        }
	        if (thisModel.getZoneCode().equals(PretupsI.ALL)) {
	            thisModel.setZoneName(PretupsRestUtil.getMessageString(LIST_ALL));
	        } else {
	            listValueVO = BTSLUtil.getOptionDesc(thisModel.getZoneCode(), thisModel.getZoneList());
	            thisModel.setZoneName(listValueVO.getLabel());
	        }

	        if (thisModel.getFromtransferCategoryCode().equals(PretupsI.ALL)) {
	            thisModel.setFromtransferCategoryName(PretupsRestUtil.getMessageString(LIST_ALL));
	        } else {
	            listValueVO = BTSLUtil.getOptionDesc(thisModel.getFromtransferCategoryCode(), thisModel.getFromCategoryList());
	            thisModel.setFromtransferCategoryName(listValueVO.getLabel());
	            String fromCatCode[] = thisModel.getFromtransferCategoryCode().split(":");
	            thisModel.setFromtransferCategoryCode(fromCatCode[1]);
	        }
	    }
	 


	@Override
	public String downloadFileforEnq(UsersReportModel usersReportModel) throws BTSLBaseException, SQLException, InterruptedException {
		DownloadCSVReports downloadCSVReports = new  DownloadCSVReports();
		 Connection con ;
		 String methodName="downloadFileforEnq";
	        MComConnectionI mcomCon;
	        mcomCon = new MComConnection();
	            con=mcomCon.getConnection();
	    try{
	            String rptCode=usersReportModel.getrptCode();
	            return downloadCSVReports.prepareData(usersReportModel,rptCode, con);
	    }
    	finally{
            if (mcomCon != null) {
                mcomCon.close(methodName);
                mcomCon = null;
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, PretupsI.EXITED);
            
        }
        }
	}

}
