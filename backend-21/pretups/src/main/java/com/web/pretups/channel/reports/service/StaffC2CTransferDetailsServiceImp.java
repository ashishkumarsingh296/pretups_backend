package com.web.pretups.channel.reports.service;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import com.btsl.common.BTSLCommonController;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListSorterUtil;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO;
import com.btsl.pretups.channel.reports.businesslogic.StaffC2CTransferdetailsDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.DownloadCSVReportsStaff;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

/**
 * @author pankaj.kumar
 *
 */

@Service("staffC2CTransferDetailsService")
public  class StaffC2CTransferDetailsServiceImp implements StaffC2CTransferDetailsService {

	public static final Log _log = LogFactory.getLog(StaffC2CTransferDetailsServiceImp.class.getName());
	private static final String MODEL_KEY = "usersReportModel";
	private static final String FAIL_KEY = "fail";
	private static final String DISSTAFFTRANSREPORT="displayStaffTransferReport";
	
	@Override
	public void loadStaffC2CTransferDetails(HttpServletRequest request,HttpServletResponse response,UsersReportModel usersReportModel, UserVO userVO,Model model) {

		final String methodName = "loadStaffC2CTransferDetails";
        enteredMethod(methodName);         
        Connection con = null;
        MComConnectionI mcomCon = null;   
        final ArrayList loggedInUserDomainList = new ArrayList();
        final ListValueVO listValueVO = null;
        try{
        	BTSLCommonController bTSLCommonController = new BTSLCommonController();      
        	  mcomCon = new MComConnection();           
  			  con=mcomCon.getConnection();
  			final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
            if(TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode()))
            	usersReportModel.setZoneList(new GeographicalDomainDAO().loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID()));
 		    else
 		    usersReportModel.setZoneList(userVO.getGeographicalAreaList());
            usersReportModel.setDomainList(BTSLUtil.displayDomainList(userVO.getDomainList()));
            final int loginSeqNo = userVO.getCategoryVO().getSequenceNumber();
            usersReportModel.setCategorySeqNo(loginSeqNo + "");
            usersReportModel.setUserType(userVO.getUserType());
           
            if (userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
            	usersReportModel.setParentCategoryList(categoryWebDAO.loadCategoryReportList(con));
            } else {
            	usersReportModel.setParentCategoryList(categoryWebDAO.loadCategoryReporSeqtList(con, loginSeqNo));
            }

            bTSLCommonController.commonUserList(usersReportModel, loggedInUserDomainList, userVO);

            if (!BTSLUtil.isNullString(userVO.getFromTime()) && !BTSLUtil.isNullString(userVO.getToTime())) {
            	usersReportModel.setFromTime(userVO.getFromTime());
            	usersReportModel.setToTime(userVO.getToTime());
            }
            bTSLCommonController.commonGeographicDetails(usersReportModel, listValueVO);
            usersReportModel.setLoginUserID(userVO.getUserID());
            if (PretupsI.CHANNEL_USER_TYPE.equals(userVO.getUserType())) {
            	usersReportModel.setLoggedInUserCategoryCode(userVO.getCategoryVO().getCategoryCode());
                if (userVO.isStaffUser()) {
                	usersReportModel.setLoggedInUserName(userVO.getParentName());
                } else {
                	usersReportModel.setLoggedInUserName(userVO.getUserName());
                }
            }

            usersReportModel.setTxnSubTypeList(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE, true));
            usersReportModel.setNetworkCode(userVO.getNetworkID());
            usersReportModel.setUserType(userVO.getUserType());

            if (userVO.isStaffUser()) {
            	usersReportModel.setLoggedInUserName(userVO.getParentName());
            } else {
            	usersReportModel.setLoggedInUserName(userVO.getUserName());
            }
            usersReportModel.setLoginUserID(userVO.getUserID());
            
        } catch (Exception e) {
        	
        	_log.errorTrace(methodName, e);
            
        }  finally {
        	if(mcomCon != null){ 
        		mcomCon.close("StaffC2CTransferDetailsServiceImp#loadStaffC2CTransferDetails");
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
            _log.debug(methodName, "Enter");
        }
	}

	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.reports.service.StaffC2CTransferDetailsService#displayStaffC2CTransferDetailsList(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, com.btsl.pretups.channel.reports.web.UsersReportModel, com.btsl.user.businesslogic.UserVO, org.springframework.ui.Model, org.springframework.validation.BindingResult)
	 */
	@Override
	public boolean displayStaffC2CTransferDetailsList(HttpServletRequest request, HttpServletResponse response,UsersReportModel usersReportModel, UserVO userVO, Model model,BindingResult bindingResult) throws ValidatorException, IOException, SAXException {
		
		final String methodName = "displayStaffC2CTransferDetailsList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Enter");
        }
        Boolean bool = true;
        Connection con = null;
        MComConnectionI mcomCon = null;
        BTSLCommonController bTSLCommonController=null;
         
       
        ChannelUserWebDAO channelUserWebDAO=null;
       
		
		if(BTSLUtil.isNullString(usersReportModel
				.getLoginId()))
		{ 
			CommonValidator commonValidator = new CommonValidator(
				"configfiles/c2sreports/validator-StaffC2CTransferDetails.xml",
				usersReportModel, "StaffC2CTrfDetailsMOB");
		Map<String, String> errorMessages = commonValidator.validateModel();
		PretupsRestUtil pru = new PretupsRestUtil();
		
		pru.processFieldError(errorMessages, bindingResult);
		
			model.addAttribute("PanelNo", "Panel-Two");
		}
		else
		{
			CommonValidator commonValidator = new CommonValidator(
					"configfiles/c2sreports/validator-StaffC2CTransferDetails.xml",
					usersReportModel, "StaffC2CTrfDetails");
			Map<String, String> errorMessages = commonValidator.validateModel();
			PretupsRestUtil pru = new PretupsRestUtil();
			
			pru.processFieldError(errorMessages, bindingResult);
			model.addAttribute("PanelNo", "Panel-One");
		}
		if (bindingResult.hasFieldErrors()) {
			
			request.getSession().setAttribute(MODEL_KEY, usersReportModel);
			return false;
		}
		
        try {
     
        bTSLCommonController = new BTSLCommonController();
    	mcomCon = new MComConnection();
        con=mcomCon.getConnection();
        final ChannelUserReportDAO channelUserDAO = new ChannelUserReportDAO();
    
        final ChannelUserVO channelUserVo = (ChannelUserVO) userVO;
       
		
		usersReportModel.setRptfromDate(usersReportModel.getFromDate());
		 
		usersReportModel.setRpttoDate(usersReportModel.getToDate());
		
		 
       
            channelUserWebDAO = new ChannelUserWebDAO();
                     usersReportModel.setNetworkCode(userVO.getNetworkID());
            usersReportModel.setNetworkName(userVO.getNetworkName());
            usersReportModel.setReportHeaderName(userVO.getReportHeaderName());
            ListValueVO listValueVO = null;
            String userID = null;
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            String[] ar = null;
         
            if (!BTSLUtil.isNullString(usersReportModel.getTxnSubType()) && (usersReportModel.getTxnSubType().trim()).equals(PretupsI.ALL)) {
               usersReportModel.setTxnSubTypeName(PretupsRestUtil.getMessageString("list.all"));
            } else if (!BTSLUtil.isNullString(usersReportModel.getTxnSubType())) {
                listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getTxnSubType(), usersReportModel.getTxnSubTypeList());
                usersReportModel.setTxnSubTypeName(listValueVO.getLabel());
            }                                             
            if (!BTSLUtil.isNullString(usersReportModel.getParentCategoryCode())) {
                listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getParentCategoryCode(), usersReportModel.getParentCategoryList());
                usersReportModel.setCategoryName(listValueVO.getLabel());                                         
                final String[] arr = usersReportModel.getParentCategoryCode().split("\\|");
                usersReportModel.setParentCategoryCode(arr[1]);
            }
            if (!BTSLUtil.isNullString(usersReportModel.getZoneCode()) && usersReportModel.getZoneCode().equals(PretupsI.ALL)) {
            	
            	usersReportModel.setZoneName(PretupsRestUtil.getMessageString("list.all"));
            } else if (!BTSLUtil.isNullString(usersReportModel.getZoneCode())) {
                listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getZoneCode(), usersReportModel.getZoneList());
                usersReportModel.setZoneName(listValueVO.getLabel());
            }
            if (!BTSLUtil.isNullString(usersReportModel.getDomainCode())) {
                listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getDomainCode(), usersReportModel.getDomainList());
                usersReportModel.setDomainName(listValueVO.getLabel());
            }

            ArrayList parentUserList = null;
         
            	   if (BTSLUtil.isNullString(usersReportModel.getUserID())) {
            	
            
                parentUserList = channelUserWebDAO.loadChannelUserDetailsByUserName(con, usersReportModel.getUserName(), usersReportModel.getParentCategoryCode(), userVO.getUserID(), userVO
                    .getNetworkID(), usersReportModel.getDomainCode(), usersReportModel.getZoneCode());
                if (parentUserList != null) {
                    if (parentUserList.isEmpty()) {
                        throw new BTSLBaseException(this, "loadStaffUserList", "user.staffuser.loadstaffuserlist.error.channeluser.notexits", "loadUsers");
                    } else if (parentUserList.size() > 1) {
                        throw new BTSLBaseException(this, "loadStaffUserList", "user.staffuser.loadstaffuserlist.error.morethan1userexist", "loadUsers");
                    }
                    final ChannelUserTransferVO channelUserTransferVO = (ChannelUserTransferVO) parentUserList.get(0);
                    channelUserTransferVO.setParentUserID(channelUserTransferVO.getUserID());
                }
            }

            if (!BTSLUtil.isNullString(usersReportModel.getMsisdn()))// MSISDN based
            // report
            {
                if (!BTSLUtil.isNullString(usersReportModel.getMsisdn())) {
                    // check for valid msisdn
                    if (!BTSLUtil.isValidMSISDN(usersReportModel.getMsisdn())) {
                        ar = new String[1];
                        ar[0] = usersReportModel.getMsisdn();
                        throw new BTSLBaseException(this, "DISSTAFFTRANSREPORT", "btsl.msisdn.error.length", 0, ar, "loadUsers");
                    }
                }
                // check if the msisdn belongs to login user network
                final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(usersReportModel.getMsisdn())));
                if (prefixVO == null || !prefixVO.getNetworkCode().equals(usersReportModel.getNetworkCode())) {
                    final String arr1[] = { usersReportModel.getMsisdn(), usersReportModel.getNetworkName() };
                    _log.error("DISSTAFFTRANSREPORT", "Error: MSISDN Number" + usersReportModel.getMsisdn() + " not belongs to " + usersReportModel.getNetworkName() + "network");
                    throw new BTSLBaseException(this, "DISSTAFFTRANSREPORT", "c2s.reports.error.msisdnnotinsamenetwork", 0, arr1, "loadUsers");
                }
                final String filteredMSISDN = PretupsBL.getFilteredMSISDN(usersReportModel.getMsisdn());

                // load user details on the basis of msisdn
                if (filteredMSISDN.equals(channelUserVo.getMsisdn()) && !channelUserVo.isStaffUser() && PretupsI.USER_TYPE_CHANNEL.equals(channelUserVo.getUserType())) {
                	usersReportModel.setUserID(channelUserVo.getUserID());
                } else {
                    final String status = "'" + PretupsI.USER_STATUS_DELETED + "','" + PretupsI.USER_STATUS_CANCELED + "'";
                     userID = channelUserWebDAO.loadStaffUsersDetailsReport(con, filteredMSISDN, usersReportModel.getUserID(), status);
                    if (BTSLUtil.isNullString(userID)) {
                        ar = new String[1];
                        ar[0] = usersReportModel.getMsisdn();
                        throw new BTSLBaseException(this, "DISSTAFFTRANSREPORT", "staffc2s.reports.error.nouserbymsisdn", 0, ar, "loadUsers");
                    }
                    usersReportModel.setUserID(userID);
                }
                usersReportModel.setUserName(usersReportModel.getUserName());

                usersReportModel.setTransferUserCategoryCode(usersReportModel.getParentCategoryCode());
               
            } else {
                	 if (usersReportModel.getLoginId().equalsIgnoreCase(PretupsRestUtil.getMessageString("list.all"))) {	
            	usersReportModel.setUserID(PretupsI.ALL);
                          } else if (usersReportModel.getLoginId().equals(channelUserVo.getLoginID()) && !channelUserVo.isStaffUser() && PretupsI.USER_TYPE_CHANNEL.equals(channelUserVo.getUserType())) {
                	usersReportModel.setUserID(channelUserVo.getUserID());
                } else if (usersReportModel.getLoginId().equals(channelUserVo.getParentLoginID()) && channelUserVo.isStaffUser() && PretupsI.USER_TYPE_STAFF.equals(channelUserVo.getUserType())) {
                	usersReportModel.setUserID(channelUserVo.getUserID());
                } else {
                    final String status = "'" + PretupsI.USER_STATUS_DELETED + "','" + PretupsI.USER_STATUS_CANCELED + "'";
           
                   userID = channelUserWebDAO.loadStaffUsersDetailsbyLoginIDReport(con, usersReportModel.getLoginId(), usersReportModel.getUserID(), status);
                   if (BTSLUtil.isNullString(userID)) {
                      
                        ar = new String[1];
                        ar[0] = usersReportModel.getLoginId();
                        throw new BTSLBaseException(this, "DISSTAFFTRANSREPORT", "staffc2s.reports.error.nouserbyloginID", 0, ar, "loadUsers");
                    }
                    usersReportModel.setUserID(userID);
                }
                usersReportModel.setUserName(usersReportModel.getUserName());

                usersReportModel.setTransferUserCategoryCode(usersReportModel.getParentCategoryCode());
              
            }
          
            
            
            
            StaffC2CTransferdetailsDAO staffC2CTransferdetailsDAO=new StaffC2CTransferdetailsDAO();
        	String rptCode = "02CTRFDET02";
        	final ArrayList<ChannelTransferVO> o2cTransferList = staffC2CTransferdetailsDAO .loadStaffc2cTransferDetailsChannelUserReport(con,usersReportModel);
        	
        	usersReportModel.setTransferList(o2cTransferList); 
        	usersReportModel.setrptCode(rptCode);
        	 usersReportModel.setTransferListSize(o2cTransferList.size());
        }
        catch (BTSLBaseException e) {
            _log.errorTrace(methodName, e);
            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage(),e.getArgs()));
            request.getSession().setAttribute(MODEL_KEY, usersReportModel);
            return false;
            
        } 
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
            request.getSession().setAttribute(MODEL_KEY, usersReportModel);
            return false;
            
            
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("StaffC2CTransferDetailsServiceImp#displayStaffC2CTransferDetailsList");
        	mcomCon=null;
        	}
            if (_log.isDebugEnabled()) {
                _log.error(methodName, "Exiting");
            }
        }
        	request.getSession().setAttribute("usersReportModel", usersReportModel);
       return bool;
		
		
	}

	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.reports.service.StaffC2CTransferDetailsService#downloadCSVReportStaffC2CTransferDetailsFile(com.btsl.pretups.channel.reports.web.UsersReportModel)
	 */
	@Override
	public String downloadCSVReportStaffC2CTransferDetailsFile(UsersReportModel usersReportModel) throws InterruptedException, BTSLBaseException, SQLException {
	
		DownloadCSVReportsStaff downloadCSVReports = new  DownloadCSVReportsStaff();
		 Connection con ;
	        MComConnectionI mcomCon;
	    mcomCon = new MComConnection();
	            con=mcomCon.getConnection();
	            String rptCode="STFC2CTRF01";
  
	            return downloadCSVReports.prepareData(usersReportModel,rptCode, con);
		
		
	}

	
	
	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.reports.service.StaffC2CTransferDetailsService#loadC2cUserLists(com.btsl.user.businesslogic.UserVO, java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.btsl.pretups.channel.reports.web.UsersReportModel)
	 */
	@Override
	public List<ChannelUserTransferVO> loadC2cUserLists(UserVO userVO,String zoneCode, String domainCode, String userName,String parentCategoryCode, UsersReportModel usersReportModel) {

		final String methodName = "loadC2cFromUserList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserReportDAO channelUserDAO = null;

        
        ArrayList<ChannelUserTransferVO> userList = new ArrayList<ChannelUserTransferVO>();
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
            
         
            
              String[] arr = parentCategoryCode.split("\\|");

  
            if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                if (parentCategoryCode.equals(PretupsI.ALL)) {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategoryHierarchy(con, PretupsI.ALL, domainCode, zoneCode, userName, usersReportModel.getLoginUserID());
                } else {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategoryHierarchy(con, arr[1],  domainCode, zoneCode, userName, usersReportModel.getLoginUserID());
                }
            } else {
                if (parentCategoryCode.equals(PretupsI.ALL)) {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategory(con, PretupsI.ALL, domainCode, zoneCode, userName, usersReportModel.getLoginUserID());
                } else {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategory(con, arr[1], domainCode, zoneCode, userName, usersReportModel.getLoginUserID());
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

	@Override
	public List<ChannelUserTransferVO> loadUseridforO2c(UserVO userVO,String zoneCode, String domainCode, String userName,String parentCategoryCode, UsersReportModel usersReportModel,String loginId) {
		final String methodName = "loadUseridforO2c";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }


        String login=loginId;
        usersReportModel.setUserList(null);

        login += "%";
        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserReportDAO channelUserDAO = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        
        ArrayList<ChannelUserTransferVO> userList = new ArrayList<ChannelUserTransferVO>();
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
             channelUserWebDAO = new ChannelUserWebDAO();
             
             extractusername(usersReportModel, userName);
            
       
            ArrayList parentUserList = null;
            if (BTSLUtil.isNullString(usersReportModel.getUserID())) {
                final String[] array = parentCategoryCode.split("\\|");
                parentUserList = channelUserWebDAO.loadChannelUserDetailsByUserName(con, usersReportModel.getUserName(), array[1], userVO.getUserID(), userVO.getNetworkID(), usersReportModel
                    .getDomainCode(), usersReportModel.getZoneCode());
                if (parentUserList != null) {
                    if (parentUserList.isEmpty()) {
                        throw new BTSLBaseException(this, methodName, "user.staffuser.loadstaffuserlist.error.channeluser.notexits", "SearchUser");
                    } else if (parentUserList.size() > 1) {
                        throw new BTSLBaseException(this, methodName, "user.staffuser.loadstaffuserlist.error.morethan1userexist", "SearchUser");
                    }
                    ChannelUserTransferVO channelUserTransferVO = null;
                    if (parentUserList.size() == 1) {
                        channelUserTransferVO = (ChannelUserTransferVO) parentUserList.get(0);
                        usersReportModel.setParentUserID(channelUserTransferVO.getUserID());
                    }
                }
            }
            if (!BTSLUtil.isNullString(usersReportModel.getUserID())) {
                userList = channelUserWebDAO.loadStaffUserListByLogin(con, usersReportModel.getUserID(), PretupsI.STAFF_USER_TYPE, login);
               
            }

            if (userList != null && userList.isEmpty()) {
            	usersReportModel.setUserList(userList);
                _log.error(methodName, "Error: User not exist");
                throw new BTSLBaseException(this, methodName, "user.c2ctransfer.loadstaffuserlist.error.usernotexist", "SearchUser");
            } else if (userList != null && !userList.isEmpty()) {
                final ListSorterUtil sort = new ListSorterUtil();
                userList = (ArrayList) sort.doSort("loginId", null, userList);
                usersReportModel.setUserList(userList);
            }
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
	
	
	
	private void extractusername(UsersReportModel usersReportModel, String user) {
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
	    	_log.error("methodName", "Name selected did not had ID "
					+ e);
			_log.errorTrace("methodName", e);
			
			usersReportModel.setUserID(null);
	    }
	}
	

}
