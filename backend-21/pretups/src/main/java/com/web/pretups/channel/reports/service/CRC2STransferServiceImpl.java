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
import com.btsl.pretups.channel.reports.businesslogic.C2STransferReportsUserVO;
import com.btsl.pretups.channel.reports.businesslogic.C2STransferRptDAO;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
//import com.btsl.pretups.common.DownloadCSVReportsBL;
import com.btsl.pretups.common.DownloadCSVReportsC2STransfer;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
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
@Service("cRC2STransferService")
public class CRC2STransferServiceImpl implements CRC2STransferService{

	public static final Log _log = LogFactory.getLog(CRC2STransferServiceImpl.class.getName());
	private static final String PANEL_NO="PanelNo";
	private static final String FAIL_KEY = "fail";	
	private static final String LIST_ALL = "list.all";
	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.reports.service.CRC2STransferService#loadC2STransferDetails(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, com.btsl.pretups.channel.reports.web.UsersReportModel, com.btsl.user.businesslogic.UserVO, org.springframework.ui.Model)
	 */
	@Override
	public void loadC2STransferDetails(HttpServletRequest request,HttpServletResponse response, UsersReportModel usersReportModel,UserVO userVO, Model model) {
		
		final String methodName = "loadC2STransferDetails";
        enteredMethod(methodName);         
        Connection con = null;
        MComConnectionI mcomCon = null;              
        try {     
        	BTSLCommonController bTSLCommonController = new BTSLCommonController();       	
            mcomCon = new MComConnection();           
			con=mcomCon.getConnection();
				
			getServiceTypeList(usersReportModel, con);
			getTransferStatusList(usersReportModel, con);
						
            usersReportModel.setThresholdTypeList(LookupsCache.loadLookupDropDown(PretupsI.THRESHOLD_COUNTER_TYPE, true));//not in use                       		   
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
        		mcomCon.close("CRC2STransferServiceImpl#loadZeroBalCounterDetail");
        	   mcomCon=null;
        	}
            exitMethod(methodName);
        }       
	}
	private void getServiceTypeList(UsersReportModel usersReportModel,
			Connection con) throws BTSLBaseException {
		final ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
		usersReportModel.setServiceTypeList(servicesTypeDAO.loadServicesListForReconciliation(con, PretupsI.C2S_MODULE));
		if (usersReportModel.getServiceTypeListSize() == 1) {           	
			ListValueVO listValueVO = (ListValueVO) usersReportModel.getServiceTypeList().get(0);
		    usersReportModel.setServiceType(listValueVO.getValue());
		    usersReportModel.setServiceTypeName(listValueVO.getLabel());
		}
	}
	@SuppressWarnings("unchecked")
	private void getTransferStatusList(UsersReportModel usersReportModel,
			Connection con) throws BTSLBaseException {
		final String status = "'" + PretupsErrorCodesI.TXN_STATUS_SUCCESS + "','" + PretupsErrorCodesI.TXN_STATUS_FAIL + "','" + PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS + "'";
		final ArrayList<ListValueVO> list = new ArrayList<>();
		list.addAll(new ChannelUserReportDAO().loadKeyValuesList(con, false, PretupsI.KEY_VALUE_C2C_STATUS, status));
		usersReportModel.setTransferStatusList(list);
	}
	private void enteredMethod(final String methodName) {
		if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
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
	private void setLoggedInUserName(UsersReportModel usersReportModel,
			UserVO userVO) {
		if (userVO.isStaffUser()) {
			usersReportModel.setLoggedInUserName(userVO.getParentName());
		} else {
			usersReportModel.setLoggedInUserName(userVO.getUserName());
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
	private void exitMethod(final String methodName) {
		if (_log.isDebugEnabled()) {
		    _log.debug(methodName, "Exit");
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<ListValueVO> c2sLoadUserList(UserVO userVO, String zoneCode,String domainCode, String userName, String parentCategoryCode) {
		
		 if (_log.isDebugEnabled()) {   
		   _log.debug("CRC2STransferServiceImpl#c2sLoadUserList", PretupsI.ENTERED +"");
		 }	
				final String methodName ="c2sLoadUserList";
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
				mcomCon.close("classclassname#methodmethodname");  
				mcomCon=null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug("CRC2STransferServiceImpl#c2sLoadUserList", PretupsI.EXITED);
			}
		}
		return userList;
	}
	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.reports.service.CRC2STransferService#c2sTransferSubmit(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, com.btsl.pretups.channel.reports.web.UsersReportModel, com.btsl.user.businesslogic.UserVO, org.springframework.ui.Model, org.springframework.validation.BindingResult)
	 */
	@Override
	public boolean c2sTransferSubmit(HttpServletRequest request,HttpServletResponse response, UsersReportModel usersReportModel,UserVO userVO, Model model, BindingResult bindingResult) {		
						
		final String methodName = "c2sTransferSubmit";  
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug("Method Name :" , methodName);
        }
        String datePattern=PretupsI.TIMESTAMP_DATESPACEHHMMSS;         
        Connection con = null;
        MComConnectionI mcomCon = null;
        ArrayList<?> userList = new ArrayList<>();
        String tempCatCode = "";
        ChannelUserWebDAO channelUserWebDAO=null;
        try {  
        	 channelUserWebDAO= new ChannelUserWebDAO();
			/* for field validation Start */
			fieldValidation(request, usersReportModel, model, bindingResult);
			if (bindingResult.hasFieldErrors()) {
				return false; 
			}
			/* for field validation End */
			 C2STransferRptDAO c2STransferRptDAO=new C2STransferRptDAO();
			 new CategoryWebDAO();			
			 usersReportModel.setCategorySeqNo(String.valueOf(userVO.getCategoryVO().getSequenceNumber()));
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
                    _log.error(methodName,  loggerValue );                   
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
                if("%%%".equalsIgnoreCase(usersReportModel.getUserName())){
                	
                	model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2s.reports.error.userName"));
            		return false;
           	     }
                if (usersReportModel.getParentCategoryCode().equals(TypesI.ALL)) {
                	usersReportModel.setCategoryName(TypesI.ALL);               	
                    userList = channelUserReportDAO.loadUserListBasisOfZoneDomainCategory(con, PretupsI.ALL, usersReportModel.getDomainCode(), usersReportModel.getZoneCode(), usersReportModel.getUserName(), userVO.getUserID());
                } else {                	          	
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
            if (!BTSLUtil.isNullString(usersReportModel.getTransferStatus()) && usersReportModel.getTransferStatus().equals(TypesI.ALL)) {
    			usersReportModel.setTransferStatusName(PretupsRestUtil.getMessageString(LIST_ALL));
            }else{ 
                listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getTransferStatus(), usersReportModel.getTransferStatusList());
                usersReportModel.setTransferStatusName(listValueVO.getLabel());
            }                      
            getC2STransferDetailsList(usersReportModel, con,c2STransferRptDAO);         
        } catch (BTSLBaseException | ParseException | ValidatorException | IOException | SAXException | SQLException e) {
            _log.errorTrace(methodName, e);
          
        } finally {
        	if(mcomCon != null){
        		mcomCon.close("classclassname#methodmethodname");
        		mcomCon=null;
        	}           
        }              		
		return true;	
	}
	private List<?> getChannelUserList(UserVO userVO, Connection con,ChannelUserWebDAO channelUserWebDAO,final ChannelUserDAO channelUserDAO) throws BTSLBaseException {
		List<?> channelUserList ;		
		if ("Y".equals(userVO.getCategoryVO().getHierarchyAllowed())) {
		    channelUserList = channelUserWebDAO.loadChannelUserHierarchy(con, userVO.getUserCode(), false);
		} else {
		    channelUserList = channelUserWebDAO.loadChannelUserHierarchy(con, (channelUserDAO.loadChannelUser(con, userVO.getParentID())).getUserCode(), false);
		}
		return channelUserList;
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
	private void fieldValidation(HttpServletRequest request,
			UsersReportModel usersReportModel, Model model,
			BindingResult bindingResult) throws ValidatorException,
			IOException, SAXException {
		if (request.getParameter("submitNamePanelOne") != null) {
			CommonValidator commonValidator = new CommonValidator(
			"configfiles/c2sreports/validator-c2STransferDetails.xml", usersReportModel,
			"PanelOne");							
			Map<String, String> errorMessages = commonValidator.validateModel();				
			PretupsRestUtil pru = new PretupsRestUtil();
			pru.processFieldError(errorMessages, bindingResult);
			model.addAttribute(PANEL_NO, "Panel-One");
			request.getSession().setAttribute(PANEL_NO, "Panel-One");
		}
		if (request.getParameter("submitNamePanelTwo") != null) {
			CommonValidator commonValidator = new CommonValidator(
			"configfiles/c2sreports/validator-c2STransferDetails.xml", usersReportModel,
			"PanelTwo");  
			Map<String, String> errorMessages = commonValidator.validateModel();
			PretupsRestUtil pru = new PretupsRestUtil();
			pru.processFieldError(errorMessages, bindingResult);
			model.addAttribute(PANEL_NO, "Panel-Two");
			request.getSession().setAttribute(PANEL_NO, "Panel-Two");
		}
	}
	private void setDateTime(UsersReportModel usersReportModel,String datePattern) throws ParseException {
		SimpleDateFormat dateFormat;
		Timestamp fromDateTimestamp;
		Timestamp toDateTimestamp;
		Date fromDate;
		Date toDate;
		if(!BTSLUtil.isNullString(usersReportModel.getDate())) {                                                       	
		    usersReportModel.setFromDateTime(BTSLUtil.getDateFromDateString(usersReportModel.getDate()+" "+usersReportModel.getFromTime()+":00", datePattern));           
		    dateFormat = new SimpleDateFormat(datePattern);
			fromDate = dateFormat.parse(BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime())); 
			fromDateTimestamp = new Timestamp(fromDate.getTime());
			usersReportModel.setFromDateTimestamp(fromDateTimestamp);
		}
		 if(!BTSLUtil.isNullString(usersReportModel.getDate())) {                                            	           
			usersReportModel.setToDateTime(BTSLUtil.getDateFromDateString(usersReportModel.getDate()+" "+usersReportModel.getToTime()+":59", datePattern));          	
			dateFormat = new SimpleDateFormat(datePattern);
			toDate = dateFormat.parse(BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
			toDateTimestamp = new Timestamp(toDate.getTime());
			usersReportModel.setToDateTimestamp(toDateTimestamp);
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
	private void setServiceTypeName(UsersReportModel usersReportModel) {
		ListValueVO listValueVO;
		if (!BTSLUtil.isNullString(usersReportModel.getServiceType()) && usersReportModel.getServiceType().equals(TypesI.ALL)) {
			usersReportModel.setServiceTypeName(PretupsRestUtil.getMessageString(LIST_ALL));
		} else if (!BTSLUtil.isNullString(usersReportModel.getServiceType())) {
		    listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getServiceType(), usersReportModel.getServiceTypeList());
		    usersReportModel.setServiceTypeName(listValueVO.getLabel());
		}
	}
	private void getC2STransferDetailsList(	UsersReportModel usersReportModel, Connection con,C2STransferRptDAO c2STransferRptDAO) throws BTSLBaseException, ParseException {
		
		OperatorUtil operatorUtil = new OperatorUtil();
		usersReportModel.setCurrentDate(BTSLUtil.getDateStringFromDate(new Date()));
		ifChannelUserType(usersReportModel, con, c2STransferRptDAO,operatorUtil);
		ifOperatorUserType(usersReportModel, con, c2STransferRptDAO,operatorUtil);				 
	}
	private void ifOperatorUserType(UsersReportModel usersReportModel,Connection con, C2STransferRptDAO c2STransferRptDAO,OperatorUtil operatorUtil) throws BTSLBaseException, ParseException {
		/* 
		 * Here below following given calling  dao method name same as corresponding .rpt file
		 * */
		    String rptCode;		  
		    if(TypesI.OPERATOR_USER_TYPE.equalsIgnoreCase(usersReportModel.getUserType())){ 
			  if(PretupsI.NO.equals(usersReportModel.getStaffReport())){ 				
				  if(operatorUtil.getNewDataAftrTbleMerging(BTSLUtil.getDateFromDateString(usersReportModel.getCurrentDate()),null)){						
				     List<C2STransferReportsUserVO> c2STransferReportsList = c2STransferRptDAO.loadC2sTransferNewReport(con, usersReportModel);
					 usersReportModel.setC2STransferReportsList(c2STransferReportsList);
					 usersReportModel.setC2STransferReportsListSize(c2STransferReportsList.size());
				}
				else{					
				     rptCode = "C2STRANSFER01";
				     usersReportModel.setrptCode(rptCode);
				     List<C2STransferReportsUserVO> c2STransferReportsList = c2STransferRptDAO.loadC2sTransferReport(con, usersReportModel);
					 usersReportModel.setC2STransferReportsList(c2STransferReportsList); 
					 usersReportModel.setC2STransferReportsListSize(c2STransferReportsList.size());
				}
		 	}else{
		 		if(operatorUtil.getNewDataAftrTbleMerging(BTSLUtil.getDateFromDateString(usersReportModel.getCurrentDate()),null)){		 		
		 			 List<C2STransferReportsUserVO> c2STransferReportsList = c2STransferRptDAO.loadC2sTransferStaffNewReport(con, usersReportModel);
					 usersReportModel.setC2STransferReportsList(c2STransferReportsList); 
					 usersReportModel.setC2STransferReportsListSize(c2STransferReportsList.size());
				}
		 		else{
				     rptCode = "C2STRANSFER03";
				     usersReportModel.setrptCode(rptCode);
				     List<C2STransferReportsUserVO> c2STransferReportsList = c2STransferRptDAO.loadC2sTransferStaffReport(con, usersReportModel);
					 usersReportModel.setC2STransferReportsList(c2STransferReportsList); 
					 usersReportModel.setC2STransferReportsListSize(c2STransferReportsList.size());
				}
			}

		      
	      }
	}
	private void ifChannelUserType(UsersReportModel usersReportModel,
			Connection con, C2STransferRptDAO c2STransferRptDAO,
			OperatorUtil operatorUtil) throws BTSLBaseException, ParseException { 
		    String rptCode;
		    if(TypesI.CHANNEL_USER_TYPE.equalsIgnoreCase(usersReportModel.getUserType())){
			   if(PretupsI.NO.equals(usersReportModel.getStaffReport())){
				if(operatorUtil.getNewDataAftrTbleMerging(BTSLUtil.getDateFromDateString(usersReportModel.getCurrentDate()),null)){											
					List<C2STransferReportsUserVO> c2STransferReportsList = c2STransferRptDAO.loadC2sTransferChannelUserNewReport(con, usersReportModel);
					usersReportModel.setC2STransferReportsList(c2STransferReportsList); 
					usersReportModel.setC2STransferReportsListSize(c2STransferReportsList.size());				
				}else{					
			        rptCode = "C2STRANSFER02";			       
			        usersReportModel.setrptCode(rptCode);
			        List<C2STransferReportsUserVO> c2STransferReportsList = c2STransferRptDAO.loadC2sTransferChannelUserReport(con, usersReportModel);
					usersReportModel.setC2STransferReportsList(c2STransferReportsList);
					usersReportModel.setC2STransferReportsListSize(c2STransferReportsList.size());
				}
			}else{
				if(operatorUtil.getNewDataAftrTbleMerging(BTSLUtil.getDateFromDateString(usersReportModel.getCurrentDate()),null)){										  					 
					 List<C2STransferReportsUserVO> c2STransferReportsList = c2STransferRptDAO.loadC2sTransferChannelUserStaffNewReport(con, usersReportModel);
					 usersReportModel.setC2STransferReportsList(c2STransferReportsList); 
					 usersReportModel.setC2STransferReportsListSize(c2STransferReportsList.size());
				}else{                   
			        rptCode = "C2STRANSFER04";
			        usersReportModel.setrptCode(rptCode);
			        List<C2STransferReportsUserVO> c2STransferReportsList = c2STransferRptDAO.loadC2sTransferChannelUserStaffReport(con, usersReportModel);
					usersReportModel.setC2STransferReportsList(c2STransferReportsList); 
					usersReportModel.setC2STransferReportsListSize(c2STransferReportsList.size());
				}
			}
		  }
	}
	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.reports.service.CRC2STransferService#c2STransferDownloadCSVReportFile(com.btsl.pretups.channel.reports.web.UsersReportModel)
	 */
	@Override
	public String c2STransferDownloadCSVReportFile(UsersReportModel usersReportModel) {
				
		final String methodName ="c2STransferDownloadCSVReportFile";
        DownloadCSVReportsC2STransfer downloadCSVReports =new DownloadCSVReportsC2STransfer();
		 Connection con = null ;
	     MComConnectionI mcomCon = null;
	     String filePath = null ;  
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();			
			usersReportModel.setCurrentDate(BTSLUtil.getDateStringFromDate(new Date()));			
			filePath=checkUserTypeDownload(usersReportModel, con,downloadCSVReports);						
		} catch (BTSLBaseException |SQLException | ParseException e) {
			 _log.errorTrace(methodName, e);
		} 
		 return filePath;	
	}
	
	/**
	 * @param usersReportModel
	 * @param con
	 * @param downloadCSVReports
	 * @return
	 */
	private String  checkUserTypeDownload(UsersReportModel usersReportModel,Connection con,DownloadCSVReportsC2STransfer downloadCSVReports) {
		
		 String methodName="checkUserTypeDownload";
		 OperatorUtil operatorUtil = new OperatorUtil();
		 String filePath = null;		 		 
		if(TypesI.CHANNEL_USER_TYPE.equalsIgnoreCase(usersReportModel.getUserType())){
			if(PretupsI.NO.equals(usersReportModel.getStaffReport())){
                 try {
					filePath = staffReportNoChannelUser(usersReportModel, con,downloadCSVReports, operatorUtil);
				}  catch (ParseException | BTSLBaseException e) {
					_log.errorTrace(methodName, e);
				}
			}else{
				try {
					filePath = staffReportYesChannelUser(usersReportModel, con,downloadCSVReports, operatorUtil);
				}catch (BTSLBaseException |ParseException e) {
					 _log.errorTrace(methodName, e);
              }
			}
		  }
		if(TypesI.OPERATOR_USER_TYPE.equalsIgnoreCase(usersReportModel.getUserType())){ 
			if(PretupsI.NO.equals(usersReportModel.getStaffReport())){ 
				
				try {
					filePath =staffReportNoOperatorUser(usersReportModel, con,downloadCSVReports, operatorUtil);
				}  catch (ParseException | BTSLBaseException e) {
					 _log.errorTrace(methodName, e);
				}
		 	}else{
		 		try {
		 			filePath =staffReportYesOperatorUser(usersReportModel, con,downloadCSVReports, operatorUtil);
				}  catch (ParseException | BTSLBaseException e) {
					 _log.errorTrace(methodName, e);
				}
			}

		      
	      }
		return filePath;
		
	}
	private String staffReportYesOperatorUser(UsersReportModel usersReportModel,Connection con, DownloadCSVReportsC2STransfer downloadCSVReports,OperatorUtil operatorUtil) throws BTSLBaseException, ParseException {
		String returnFilePath;
		if(operatorUtil.getNewDataAftrTbleMerging(BTSLUtil.getDateFromDateString(usersReportModel.getCurrentDate()),null)){	
			String rptCode="c2sTransferStaffNew";
			usersReportModel.setrptCode(rptCode);
			returnFilePath = downloadCSVReports.prepareDataForC2STransferDetails(usersReportModel,rptCode, con);
		}
		else{
			 String rptCode = "C2STRANSFER03";
		     usersReportModel.setrptCode(rptCode);
		     returnFilePath = downloadCSVReports.prepareDataForC2STransferDetails(usersReportModel,rptCode, con);
		     
		}
		return returnFilePath;
	}
	private String staffReportNoOperatorUser(UsersReportModel usersReportModel,Connection con, DownloadCSVReportsC2STransfer downloadCSVReports,OperatorUtil operatorUtil) throws BTSLBaseException, ParseException {
		String returnFilePath;
		if(operatorUtil.getNewDataAftrTbleMerging(BTSLUtil.getDateFromDateString(usersReportModel.getCurrentDate()),null)){	
			String rptCode="c2sTransferNew";
		    usersReportModel.setrptCode(rptCode);
		    returnFilePath = downloadCSVReports.prepareDataForC2STransferDetails(usersReportModel,rptCode, con);
		
		}else{
			 String rptCode = "C2STRANSFER01";
		     usersReportModel.setrptCode(rptCode);
		     returnFilePath = downloadCSVReports.prepareDataForC2STransferDetails(usersReportModel,rptCode, con);
		}
		return returnFilePath;
	}
	private String  staffReportYesChannelUser(UsersReportModel usersReportModel,Connection con, DownloadCSVReportsC2STransfer downloadCSVReports,OperatorUtil operatorUtil) throws BTSLBaseException, ParseException {
		String returnFilePath;
		if(operatorUtil.getNewDataAftrTbleMerging(BTSLUtil.getDateFromDateString(usersReportModel.getCurrentDate()),null)){												
		     String rptCode="c2sTransferChannelUserStaffNew";
			 usersReportModel.setrptCode(rptCode);
			 returnFilePath = downloadCSVReports.prepareDataForC2STransferDetails(usersReportModel,rptCode, con);
		}else{
		    String rptCode = "C2STRANSFER04";
		    usersReportModel.setrptCode(rptCode);
		    returnFilePath = downloadCSVReports.prepareDataForC2STransferDetails(usersReportModel,rptCode, con);
		}
		return returnFilePath;
	}
	private String staffReportNoChannelUser(UsersReportModel usersReportModel,Connection con, DownloadCSVReportsC2STransfer downloadCSVReports,OperatorUtil operatorUtil)throws BTSLBaseException, ParseException {
		String returnFilePath;
		if(operatorUtil.getNewDataAftrTbleMerging(BTSLUtil.getDateFromDateString(usersReportModel.getCurrentDate()),null)){							
			 String rptCode="c2sTransferChannelUserNew";
			 usersReportModel.setrptCode(rptCode);
			 returnFilePath = downloadCSVReports.prepareDataForC2STransferDetails(usersReportModel,rptCode, con);
		}else{
			String  rptCode = "C2STRANSFER02";			       
		    usersReportModel.setrptCode(rptCode);
		    returnFilePath = downloadCSVReports.prepareDataForC2STransferDetails(usersReportModel,rptCode, con);
		}
		return returnFilePath;
	}
	
   
}
