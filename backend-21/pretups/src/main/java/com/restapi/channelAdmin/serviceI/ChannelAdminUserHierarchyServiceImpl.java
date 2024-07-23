package com.restapi.channelAdmin.serviceI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.btsl.util.MessageResources;
import org.owasp.esapi.reference.IntegerAccessReferenceMap;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.EmailSendToUser;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.GetChannelUsersMsg;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListService;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListServiceImpl;
import com.btsl.pretups.channel.user.businesslogic.BatchUserDAO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.transfer.businesslogic.errorfilerequest.ErrorFileRequestVO;
import com.btsl.pretups.transfer.businesslogic.errorfileresponse.ErrorFileResponse;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.user.requesthandler.ChannelSOSSettlementHandler;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.xl.BatchUserCreationExcelRWPOI;
import com.btsl.user.businesslogic.GetChannelUsersListResponseVo;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;

import com.monitorjbl.xlsx.StreamingReader;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.channelAdmin.ChannelAdminTransferVO;
import com.restapi.channelAdmin.ChannelAdminUserHierarchyController;
import com.restapi.channelAdmin.requestVO.ApprovalBarredForDltRequestVO;
import com.restapi.channelAdmin.requestVO.BarredusersrequestVO;
import com.restapi.channelAdmin.requestVO.BulkModifyUserRequestVO;
import com.restapi.channelAdmin.requestVO.SuspendResumeUserHierarchyRequestVO;
import com.restapi.channelAdmin.responseVO.BulkModifyUserResponseVO;
import com.restapi.channelAdmin.service.ChannelAdminUserHierarchyService;
import com.restapi.superadmin.repository.ChannelTransferRuleWebDAO;
import com.restapi.user.service.FileDownloadResponseMulti;
import com.restapi.user.service.UserHierachyCARequestVO;
import com.restapi.user.service.UserHierarchyUIResponseData;
import com.web.pretups.channel.user.businesslogic.BatchUserWebDAO;
import com.web.pretups.channel.user.businesslogic.ChannelUserTransferWebDAO;
import com.web.pretups.channel.user.web.BatchUserForm;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

@Service
public class ChannelAdminUserHierarchyServiceImpl  implements ChannelAdminUserHierarchyService{
	
	public static final Log LOG = LogFactory.getLog(ChannelAdminUserHierarchyServiceImpl.class.getName());

	public static final Log log = LogFactory.getLog(ChannelAdminUserHierarchyServiceImpl.class.getName());



	@Override
	public int getUserHierarchyListCA(Connection con, String loginID, UserHierachyCARequestVO requestVO,
			List<UserHierarchyUIResponseData> responseVO, HttpServletResponse responseSwag) throws SQLException, BTSLBaseException 
	{
		UserDAO userDAO = new UserDAO();
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		UserVO channelUserVO = null;
		UserVO loggedinUserVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
		String userStatus = "ALL";
		if(requestVO.isAdvancedSearch() && requestVO.getUserStatus()!=null)
			userStatus = requestVO.getUserStatus();
		int maxLevel =0; 
		DomainDAO domainDao = new DomainDAO();
		GeographicalDomainDAO geoDomainDao = new GeographicalDomainDAO();
		responseVO.add(new UserHierarchyUIResponseData());
		GetChannelUsersListResponseVo response = null;
		
		// if user entered the mobile number then we perform the various
        // validations as
        // 1. mobile number must be from the login network
        // 2. if channel user login then it should be in its user hierarchy
        // 3. if channel admin login then it should be in its allowed
        // geographical domains and channel domains.
		
		if(!requestVO.isAdvancedSearch())
		{
			if(!BTSLUtil.isEmpty(requestVO.getLoginID()))
			{
				channelUserVO = userDAO.loadAllUserDetailsByLoginID(con, requestVO.getLoginID());
				
				
				if(channelUserVO == null){
					throw new BTSLBaseException(PretupsErrorCodesI.LOGIN_ID_INVALID);
				}
				
				if(!PretupsI.USER_TYPE_CHANNEL.equalsIgnoreCase(channelUserVO.getUserType())) {
					throw new BTSLBaseException(PretupsErrorCodesI.USER_HIERARCHY_ERROR_MESSAGE);
				}
				
				ArrayList<ListValueVO> domainList = domainDao.loadDomainListByUserId(con,loggedinUserVO.getUserID());
                if (domainList != null && !domainList.isEmpty()) {
                    ListValueVO listValueVO = null;
                    boolean domainfound = false;

                    for (int i = 0, j = domainList.size(); i < j; i++) {
                        listValueVO = (ListValueVO) domainList.get(i);
                        if (channelUserVO.getCategoryVO().getDomainCodeforCategory().equals(listValueVO.getValue())) {
                            domainfound = true;
                            break;
                        }
                    }
                    if (!domainfound) {

                    	throw new BTSLBaseException(PretupsErrorCodesI.USER_NOT_IN_DOMAIN);
                         
                    }
                }
                
                boolean geoFound=false;
                List<UserGeographiesVO> userGeoDomains=channelUserVO.getGeographicalAreaList();
                if(userGeoDomains.size()==0)
                	throw new BTSLBaseException(PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN);
                for(UserGeographiesVO userGeoDomain:userGeoDomains) {
                	if (geoDomainDao.isGeoDomainExistInHierarchy(con, userGeoDomain.getGraphDomainCode(), loggedinUserVO.getUserID())) {
                		geoFound =true;
                		break;
                	}
                }
                
                if(!geoFound) {
            		throw new BTSLBaseException(PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN);
                }
				
			}
			else if(!BTSLUtil.isEmpty(requestVO.getMsisdn()))
			{ 
			
				final String msisdn = requestVO.getMsisdn();
				final String filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
				final String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
				

				final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
				if (networkPrefixVO == null) {
					throw new BTSLBaseException(PretupsErrorCodesI.ERROR_NETWORK_NOTFOUND);
				}
				final String networkCode = networkPrefixVO.getNetworkCode();
				
				if (networkCode == null || !networkCode.equals(loggedinUserVO.getNetworkID())) {
					throw new BTSLBaseException(PretupsErrorCodesI.ERROR_NETWORK_NOTFOUND);
				}
				
				channelUserVO = userDAO.loadUserDetailsByMsisdn(con, requestVO.getMsisdn());
				
				if(channelUserVO!=null && channelUserVO.getUserCode()==null){
                	 
	                	throw new BTSLBaseException(PretupsErrorCodesI.INVALID_IDENTIFIER_VALUE);
	                }
	            if (channelUserVO == null || !channelUserVO.getUserCode().equals(filteredMsisdn)) {
	                    throw new BTSLBaseException(PretupsErrorCodesI.MSISDN_INVALID);
	                }
	            if(!PretupsI.USER_TYPE_CHANNEL.equalsIgnoreCase(channelUserVO.getUserType())) {
					throw new BTSLBaseException(PretupsErrorCodesI.USER_HIERARCHY_ERROR_MESSAGE);
				}
	            
	            ArrayList<ListValueVO> domainList = domainDao.loadDomainListByUserId(con,loggedinUserVO.getUserID());
                if (domainList != null && !domainList.isEmpty()) {
                    ListValueVO listValueVO = null;
                    boolean domainfound = false;

                    for (int i = 0, j = domainList.size(); i < j; i++) {
                        listValueVO = (ListValueVO) domainList.get(i);
                        if (channelUserVO.getCategoryVO().getDomainCodeforCategory().equals(listValueVO.getValue())) {
                            domainfound = true;
                            break;
                        }
                    }
                    if (!domainfound) {
                    	throw new BTSLBaseException(PretupsErrorCodesI.USER_NOT_IN_DOMAIN);    
                    }
                }
                
                final GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
                if (!geographicalDomainDAO.isGeoDomainExistInHierarchy(con, channelUserVO.getGeographicalCode(), loggedinUserVO.getUserID())) {
                	throw new BTSLBaseException(PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN);
                }
			}
			else
			{
				throw new BTSLBaseException(PretupsErrorCodesI.BLANK_IDENTIFIER_VALUE);
			}
		}
		else
		{
			channelUserVO = userDAO.loadUserDetailsFormUserID(con, requestVO.getOwnerName());
		}
		
		
		
		responseVO.get(0).setMsisdn(channelUserVO.getMsisdn());
		responseVO.get(0).setLevel(0);
		responseVO.get(0).setUsername(channelUserVO.getUserName());
		responseVO.get(0).setUserID(channelUserVO.getUserID());
		response = userDAO.getChannelUsersList
				(con,"ALL","ALL","ALL",channelUserVO.getUserID(),"ALL",true);
		
		List<UserHierarchyUIResponseData> responseList = new ArrayList<UserHierarchyUIResponseData>();
		Map<UserHierarchyUIResponseData,String> unattachedMap = new LinkedHashMap<UserHierarchyUIResponseData,String>(); 
		for(GetChannelUsersMsg getChannelUsersMsg : response.getChannelUsersList())
		{
			UserHierarchyUIResponseData responseObj = new UserHierarchyUIResponseData();
			
			responseObj.setMsisdn(getChannelUsersMsg.getMsisdn());
			responseObj.setBalanceList(getChannelUsersMsg.getBalanceList());
			responseObj.setUsername(getChannelUsersMsg.getUserName());
			responseObj.setParentID(getChannelUsersMsg.getParentID());
			responseObj.setUserID(getChannelUsersMsg.getUserID());
			responseObj.setStatus(getChannelUsersMsg.getStatus());
			responseObj.setStatusCode(getChannelUsersMsg.getStatusCode());
			responseObj.setCategory(getChannelUsersMsg.getCategory());
			responseObj.setUserType(getChannelUsersMsg.getUserType());
			responseObj.setCategoryCode(getChannelUsersMsg.getCategoryCode());
			responseObj.setLoginId(getChannelUsersMsg.getLoginID());
			
			if(responseObj.getUserID().equals(channelUserVO.getUserID()))
			{
				responseVO.get(0).setBalanceList(responseObj.getBalanceList());
				responseVO.get(0).setCategory(getChannelUsersMsg.getCategory());
				responseVO.get(0).setCategoryCode(getChannelUsersMsg.getCategoryCode());
				responseVO.get(0).setStatus(getChannelUsersMsg.getStatus());
				responseVO.get(0).setStatusCode(getChannelUsersMsg.getStatusCode());
				responseVO.get(0).setLoginId(getChannelUsersMsg.getLoginID());
			}
			else if(responseObj.getParentID().equals(channelUserVO.getUserID()))
			{
					maxLevel =1;
					responseObj.setLevel(1);
					if(responseVO.get(0).getChildList() == null)
					{
						ArrayList<UserHierarchyUIResponseData> childList = new ArrayList<UserHierarchyUIResponseData>();
						responseVO.get(0).setChildList(childList);
					}
					responseVO.get(0).getChildList().add(responseObj);
				
			}
			else
			{
				unattachedMap.put(responseObj, getChannelUsersMsg.getParentID());
			}
			
			
			responseList.add(responseObj);			
		}
		
		
		
			for (Entry<UserHierarchyUIResponseData, String> entry : unattachedMap.entrySet()) 
			{
				for(UserHierarchyUIResponseData obj1 : responseList)
				{
					if(entry.getValue().equalsIgnoreCase(obj1.getUserID()))
					{
						entry.getKey().setLevel(obj1.getLevel()+1);
						if(obj1.getChildList() == null)
						{
							ArrayList<UserHierarchyUIResponseData> childList = new ArrayList<UserHierarchyUIResponseData>();
							obj1.setChildList(childList);
						}
						obj1.getChildList().add(entry.getKey());
						
						if(entry.getKey().getLevel() >maxLevel)
						{
							maxLevel = entry.getKey().getLevel();
						}
					}
				}
			}
			
		
			if(!requestVO.isAdvancedSearch())
				return maxLevel;
			
			if(requestVO.isAdvancedSearch()) {
				List<UserHierarchyUIResponseData> childList= responseVO.get(0).getChildList();
				if(childList==null) {
					throw new BTSLBaseException("Data not Found");
				}
				
				
				List<UserHierarchyUIResponseData> finalHierarchy = new ArrayList<>();
				Queue<UserHierarchyUIResponseData> queue = new LinkedList<>();
				queue.add(responseVO.get(0));
				
				if(requestVO.getParentCategory().equals(requestVO.getUserCategory())) {
					while(true) {
						int size=queue.size();
						
						if(size==0)
							break;
						
						boolean found=false;
						while(size-->0) {
							UserHierarchyUIResponseData newData=queue.poll();
							if(newData.getUserID().equals(requestVO.getParentUserId()) && (requestVO.getUserStatus().equals("ALL") || newData.getStatusCode().equalsIgnoreCase(requestVO.getUserStatus()))) {
								found=true;
								finalHierarchy.add(newData);
							}else {
								List<UserHierarchyUIResponseData> childLists=newData.getChildList();
								if(childLists!=null) {
								for(UserHierarchyUIResponseData tempData:childLists) {
									queue.add(tempData);
									}
								}
							}
						}
						if(found)
							break;
						maxLevel--;
					}
				}else {
					while(true) {
						int size=queue.size();
						
						if(size==0)
							break;
						
						boolean found=false;
						while(size-->0) {
							UserHierarchyUIResponseData newData=queue.poll();
							if(newData.getUserID().equals(requestVO.getParentUserId())) {
								found=true;
								List<UserHierarchyUIResponseData> childLists=newData.getChildList();
								if(childLists!=null) {
								for(UserHierarchyUIResponseData tempData:childLists) {
									if(tempData.getCategoryCode().equals(requestVO.getUserCategory()) &&( requestVO.getUserStatus().equals("ALL") || tempData.getStatusCode().equals(requestVO.getUserStatus()))){
										finalHierarchy.add(tempData);
										}
									}
								}
							}else {
								List<UserHierarchyUIResponseData> childLists=newData.getChildList();
								if(childLists!=null) {
								for(UserHierarchyUIResponseData tempData:childLists) {
									queue.add(tempData);
								}
								}
							}
						}
						if(found)
							break;
						maxLevel--;
					}
				}
				
				
				responseVO.clear();
				responseVO.addAll(finalHierarchy);
			}
			
			return maxLevel;
		
	}
	
	@Override
	public void suspendResumeUserHierarchyListCA(Connection con, String loginID,
			SuspendResumeUserHierarchyRequestVO requestVO, HttpServletResponse responseSwag)
			throws SQLException, BTSLBaseException {
		final String methodName = "suspendResumeUserHierarchyListCA";
		if (log.isDebugEnabled()) {
	         log.debug(methodName, "Entered");
	     }
		
		if(BTSLUtil.isNullOrEmptyList(requestVO.getLoginIdList())) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.IS_REQUIRED, 0, new String[]{"LoginId list"}, null);
		}
		
		if(BTSLUtil.isNullorEmpty(requestVO.getRequestType())) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.IS_REQUIRED, 0, new String[]{"Request type"}, null);
		}
		
		UserDAO userDAO = new UserDAO();
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
		DomainDAO domainDao = new DomainDAO();
		GeographicalDomainDAO geoDomainDao = new GeographicalDomainDAO();
		String changeStatusTo = "";
		String status = "";
		String statusDesc = "";
		ChannelUserVO sessionUserVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
		ArrayList<ChannelUserVO> channelUserVOList = new ArrayList<ChannelUserVO>();
		ArrayList<ChannelUserVO> actionUserList = new ArrayList<ChannelUserVO>();
		
		if (sessionUserVO != null) {
			if (!sessionUserVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.UNAUTHORIZED_REQUEST, 0, null, null);
			}
		} else {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.UNAUTHORIZED_REQUEST, 0, null, null);
		}
		
		if (requestVO.getRequestType().equals(PretupsI.USER_STATUS_SUSPEND)) {
			changeStatusTo = PretupsI.USER_STATUS_SUSPEND;
			status = PretupsI.USER_STATUS_ACTIVE;
			statusDesc = PretupsI.ACTIVE;
		} else if (requestVO.getRequestType().equals(PretupsI.USER_STATUS_ACTIVE)) {
			changeStatusTo = PretupsI.USER_STATUS_ACTIVE;
			status = PretupsI.USER_STATUS_SUSPEND;
			statusDesc = PretupsI.SUSPENDED;
		} else {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_REQUEST_TYPE, 0, null, null);
		}
		
		ArrayList<String> searchUserList = requestVO.getLoginIdList();
		for (String searchLoginId : searchUserList) {
			ChannelUserVO channelUserVO = null;
			channelUserVO = userDAO.loadAllUserDetailsByLoginID(con, searchLoginId);

			if (channelUserVO == null) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_IDENTIFIER_VALUE, 0, null, null);
			}

			if (!PretupsI.USER_TYPE_CHANNEL.equalsIgnoreCase(channelUserVO.getUserType())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_HIERARCHY_ERROR_MESSAGE, 0, null, null);
			}

			if (!status.equals(channelUserVO.getStatus())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SELECT_STATUS_USERS, 0, new String[]{statusDesc.toLowerCase()}, null);

			}

			ArrayList<ListValueVO> domainList = domainDao.loadDomainListByUserId(con, sessionUserVO.getUserID());
			if (domainList != null && !domainList.isEmpty()) {
				ListValueVO listValueVO = null;
				boolean domainfound = false;

				for (int i = 0, j = domainList.size(); i < j; i++) {
					listValueVO = (ListValueVO) domainList.get(i);
					if (channelUserVO.getCategoryVO().getDomainCodeforCategory().equals(listValueVO.getValue())) {
						domainfound = true;
						break;
					}
				}
				if (!domainfound) {

					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_IN_DOMAIN, 0, null, null);

				}
			}

			boolean geoFound = false;
			List<UserGeographiesVO> userGeoDomains = channelUserVO.getGeographicalAreaList();
			if (userGeoDomains.size() == 0) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN, 0, null, null);
			}
			
			for (UserGeographiesVO userGeoDomain : userGeoDomains) {
				if (geoDomainDao.isGeoDomainExistInHierarchy(con, userGeoDomain.getGraphDomainCode(),
						sessionUserVO.getUserID())) {
					geoFound = true;
					break;
				}
			}

			if (!geoFound) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN, 0, null, null);
			}
			channelUserVOList.add(channelUserVO);
		}
		/* validations end */
		for (ChannelUserVO searchChannelUserVO : channelUserVOList) {
			String arr[] = new String[1];
			arr[0] = searchChannelUserVO.getUserID();
			ArrayList<ChannelUserVO> userHierarchyList = channelUserDAO.loadUserHierarchyList(con, arr, PretupsI.ALL,
					PretupsI.STATUS_IN, status, searchChannelUserVO.getCategoryCode());
			/*for (ChannelUserVO hierarchyUser : userHierarchyList) {
				actionUserList.add(hierarchyUser);
			}*/
			userHierarchyList.stream().forEach(hierarchyUser->{
				actionUserList.add(hierarchyUser);
			});
		}

		int updateCount = 0;
		final Date modified_on = new Date();
		sessionUserVO.setModifiedOn(modified_on);
		if (actionUserList != null && !actionUserList.isEmpty()) {
			updateCount = channelUserWebDAO.changeChannelUserStatus(con, actionUserList, sessionUserVO, changeStatusTo);
			if (con != null) {

				if (updateCount > 0) {
					con.commit();
					/* mcomCon.finalCommit(); */
					if(PretupsI.USER_STATUS_ACTIVE.equals(changeStatusTo)) {
						ChannelUserLog.log("RESUME", actionUserList, sessionUserVO, true, "Channel user hierarchy resume");
					}else if(PretupsI.USER_STATUS_SUSPEND.equals(changeStatusTo)) {
						ChannelUserLog.log("SUSPEND", actionUserList, sessionUserVO, true, "Channel user hierarchy suspend");
					}
				} else {
					con.rollback();
					/* mcomCon.finalRollback(); */
					if(PretupsI.USER_STATUS_ACTIVE.equals(changeStatusTo)) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_RESUME_FAILED, 0, null, null);
					}else if(PretupsI.USER_STATUS_SUSPEND.equals(changeStatusTo)) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_SUSPEND_FAILED, 0, null, null);
					}
				}
			}
		} else {
			if(PretupsI.USER_STATUS_ACTIVE.equals(changeStatusTo)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_USER_HIERARCHY_FOUND_TO_RESUME, 0, null, null);
			}else if(PretupsI.USER_STATUS_SUSPEND.equals(changeStatusTo)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_USER_HIERARCHY_FOUND_TO_SUSPEND, 0, null, null);
			}
		}
		
		if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting");
        	}
	}

	
	@Override
	public BaseResponse confirmChannelUserTransfer(Connection con, MComConnectionI mcomCon,BaseResponse response, HttpServletResponse response1,
			ArrayList userList, ChannelUserVO channelUserVO, UserVO sessionUserVO, ChannelAdminTransferVO requestVO) throws BTSLBaseException, SQLException {
		final String METHOD_NAME = "confirmChannelUserTransfer";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered ");
		}
		 String[] arr = null;
		 ChannelUserWebDAO channelUserWebDAO = null;
	     ChannelUserTransferWebDAO channelUserTransferwebDAO = null;
	     Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
         int updateCount = 0;
 		 Map lockedDataMap = null;
 		 ChannelUserTransferVO channelUserTransferVO = null;
        try {
        	 channelUserWebDAO = new ChannelUserWebDAO();
             channelUserTransferwebDAO = new ChannelUserTransferWebDAO();
             channelUserTransferVO = new ChannelUserTransferVO();
            
             createTransferVO(con,channelUserVO ,channelUserTransferVO , sessionUserVO);
             
             final String transferMode = "ALL";
             final String[] categoryArr = (channelUserVO.getCategoryVO().getDomainCodeforCategory() + ":" + channelUserVO.getCategoryVO()
             .getSequenceNumber() + "|" + channelUserVO.getCategoryCode()).split("\\|");
             final String status = PretupsBL.userStatusNotIn();
             final String statusUsed = PretupsI.STATUS_NOTIN;
             
             arr = new String[1];
             arr[0] = channelUserVO.getUserID();
             final int totalUser = userList.size();
             
             if (totalUser > 999) {
                 final ArrayList UserHierarchyList = new ArrayList();
                 int i = 0;
                 String st = null;
                 for (int k = 0; k < totalUser; k++) {
                     final String user_id = ((ChannelUserTransferVO) userList.get(k)).getUserID();
                     if (!BTSLUtil.isNullString(user_id)) {
                         if (st == null) {
                             st = user_id;
                         } else {
                             st = st + "," + user_id;
                         }
                         i++;
                     }
                     if (i >= 999 || (k + 1) >= totalUser) {
                         if (st != null) {
                             UserHierarchyList.addAll(channelUserWebDAO.loadUserHierarchyListForTransfer(con, st.split(","), PretupsI.MULTIPLE, statusUsed, status,
                                 categoryArr[1]));
                         }
                         i = 0;
                         st = null;
                     }
                 }
                 channelUserTransferVO.setUserHierarchyList(UserHierarchyList);
                
             } else {
            	 channelUserTransferVO.setUserHierarchyList(channelUserWebDAO
                     .loadUserHierarchyListForTransfer(con, arr, PretupsI.ALL, statusUsed, status, categoryArr[1]));
             }
            
             final ArrayList userHierarchyList = channelUserTransferVO.getUserHierarchyList();
             final ChannelUserVO chVO = (ChannelUserVO) channelUserTransferVO.getUserHierarchyList().get(channelUserTransferVO.getUserHierarchyList().size() - 1);
            
             //check status of every user in herarchy
             if (userHierarchyList != null && !userHierarchyList.isEmpty()) {
            	 channelUserTransferVO.setIsOperationNotAllow(false);
                 boolean isNotSuspendedFound = false;
                 ChannelUserVO channelUserVOHL = null;
                 for (int i = 0, j = userHierarchyList.size(); i < j; i++) {
                	 channelUserVOHL = (ChannelUserVO) userHierarchyList.get(i);
                     if (!PretupsI.USER_STATUS_SUSPEND.equals(channelUserVOHL.getStatus())) {
                         isNotSuspendedFound = true;
                         break;
                     }
                 }
                 if (isNotSuspendedFound) {
                	 channelUserTransferVO.setIsOperationNotAllow(true);
     				throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.COMPLETE_USER_HIERARCHY_NOT_SUSPENDED, 0,null,null);
                 }
             }
          // now check that any pending O2C/FOC or Batch FOC transaction is
             // exist
             if (userHierarchyList != null && !userHierarchyList.isEmpty()) {
            	 channelUserTransferVO.setIsOperationNotAllow(false);
                 boolean isPendingTxnFound = false;
                 ChannelUserVO channelUserVONew = null;
                 final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
                 final FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
 		        ChannelSOSSettlementHandler channelSOSSettlementHandler = new ChannelSOSSettlementHandler();
     			UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
     			UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
                 for (int i = 0, j = userHierarchyList.size(); i < j; i++) {
                	 channelUserVONew = (ChannelUserVO) userHierarchyList.get(i);
 			        // Checking SOS Pending transactions
                     if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
 				        boolean isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(con, channelUserVONew.getUserID());
 				        if (isSOSPendingFlag) {
 				        	channelUserTransferVO.setIsOperationNotAllow(true);
			                throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.TRF_SOS_PENDING, 0,null,null);
 				        }
 					}
                     // Checking for pending LR transactions
             		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED))).booleanValue()){
             			userTrfCntVO = userTrfCntDAO.selectLastLRTxnID(channelUserVONew.getUserID(), con, false, null);
             			if (userTrfCntVO!=null){ 
             				channelUserTransferVO.setIsOperationNotAllow(true);
			                throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.TRF_LR_PENDING, 0,null,null);
             			}
             		}
                     if (channelTransferDAO.isPendingTransactionExist(con, channelUserVONew.getUserID())) {
                         isPendingTxnFound = true;
                         break;
                     } else if (batchTransferDAO.isPendingTransactionExist(con, channelUserVONew.getUserID())) {
                         isPendingTxnFound = true;
                         break;
                     }
                 }
                 
                 if (isPendingTxnFound) {
                	 channelUserTransferVO.setIsOperationNotAllow(true);
		                throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.TRF_O2C_PENDING, 0,null,null);
                 }
             }
             try{
				 lockedDataMap = channelUserTransferwebDAO.transferChannelUserIntermediate(con,channelUserTransferVO);
				if (log.isDebugEnabled())
					log.debug(METHOD_NAME,"update Count : "+lockedDataMap.size()+", UserCount:"+channelUserTransferVO.getUserHierarchyList().size());
				
				if(channelUserTransferVO.getUserHierarchyList().size()!=lockedDataMap.size()){
					con.rollback();
					throw new BTSLBaseException(this,METHOD_NAME,"User not transfered.","");
				}
			}
			catch(BTSLBaseException be){
				try{
					con.rollback();
					
					if(PretupsErrorCodesI.USER_MIGRATION_IN_PROCESS.equalsIgnoreCase(be.getMessageKey()))
		                throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.USER_MIGRATION_IN_PROCESS, 0,null,null);
					else
						throw new BTSLBaseException(this,METHOD_NAME,"channeluser.viewuserhierarchy.msg.trfunsuccess","");
				}
				catch(SQLException sqe){
					throw new BTSLBaseException(this,METHOD_NAME,"channeluser.viewuserhierarchy.msg.trfunsuccess","");
				}
			}
             
             final Date currentDate = new Date();
             
             channelUserTransferVO.setCreatedBy(sessionUserVO.getUserID());
             channelUserTransferVO.setCreatedOn(currentDate);
             channelUserTransferVO.setModifiedBy(sessionUserVO.getUserID());
             channelUserTransferVO.setModifiedOn(currentDate);
             channelUserTransferVO.setNetworkCode(sessionUserVO.getNetworkID());
             channelUserTransferwebDAO = new ChannelUserTransferWebDAO();
            
             getNewGeographicalDomainCode(con, channelUserTransferVO);
             updateCount = channelUserTransferwebDAO.transferChannelUser(con, channelUserTransferVO);
             if (con != null) {
                 if (updateCount > 0) {
                 
                 	mcomCon.finalCommit();
                 	response.setStatus(200);
                 	String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
       			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
                 	response.setMessage(msg);
                 }
                 else {
                     
                   	mcomCon.finalRollback();
                       throw new BTSLBaseException(this, METHOD_NAME, "User not Transfered.", "");
                   }
                }
            
     		  
             
        }catch (BTSLBaseException be) {
		
	    
	    	log.error(METHOD_NAME, "Exceptin:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			return response;
        } 
        finally {
        	if (mcomCon != null) {
				mcomCon.close(METHOD_NAME+"#"+METHOD_NAME);
				mcomCon = null;
			}
			
			
			
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exited");
			}
		}
        
		return response;
	}
	private void getNewGeographicalDomainCode(Connection p_con, ChannelUserTransferVO p_channelUserTransferVO) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("getNewGeographicalDomainCode", "Entered" +  ",channelUserTransferVO=" + p_channelUserTransferVO);
        }
        final String METHOD_NAME = "getNewGeographicalDomainCode";
        String geoGraphicalDomainType = null;
        final ChannelUserTransferWebDAO chnlUserTransferwebDAO = new ChannelUserTransferWebDAO();
        try {
            final ArrayList geoList = chnlUserTransferwebDAO.loadGeogphicalHierarchyListByToParentId(p_con, p_channelUserTransferVO.getToParentID());
            final ArrayList userList = p_channelUserTransferVO.getUserHierarchyList();
            if (userList != null && !userList.isEmpty()) {
                for (int j = 0; j < userList.size(); j++) {
                    final ChannelUserVO chnlUserVO = (ChannelUserVO) userList.get(j);
                    geoGraphicalDomainType = chnlUserVO.getCategoryVO().getGrphDomainType();
                    if (geoList != null && !geoList.isEmpty()) {
                        for (int i = 0; i < geoList.size(); i++) {
                            final GeographicalDomainVO geoDomainVO = (GeographicalDomainVO) geoList.get(i);
                            if (geoDomainVO.getGrphDomainType().equals(geoGraphicalDomainType)) {
                                chnlUserVO.setGeographicalCode(geoDomainVO.getGrphDomainCode());
                                break;
                            }
                        }
                    }
                }
            }

        } catch (BTSLBaseException be) {
            log.error("getNewGeographicalDomainCode", "Exceptin:be=" + be);
            log.errorTrace(METHOD_NAME, be);
            throw be;
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("getNewGeographicalDomainCode", "Exiting ");
            }
        }

       
    }
	 public void createTransferVO(Connection con,ChannelUserVO channelUserVO,ChannelUserTransferVO channelUserTransferVO,UserVO sessionVO) throws BTSLBaseException {
	    	ChannelUserWebDAO channelUserWebDAO = null;
	        final String statusUsed = PretupsI.STATUS_IN;
	        final String status = PretupsBL.userStatusIn() + ", '" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";

	        String [] arr = new  String [2];
	        channelUserWebDAO =new ChannelUserWebDAO();
			channelUserTransferVO.setUserID(channelUserVO.getUserID());
			channelUserTransferVO.setUserCategoryCode(channelUserVO.getCategoryCode());
			channelUserTransferVO.setUserCategoryDesc(channelUserVO.getCategoryCodeDesc());
			channelUserTransferVO.setUserName(channelUserVO.getUserName());
			channelUserTransferVO.setFromParentID(channelUserVO.getParentID());
			channelUserTransferVO.setFromOwnerID(channelUserVO.getOwnerID());
			channelUserTransferVO.setStatus(channelUserVO.getStatus());
			channelUserTransferVO.setToOwnerID(sessionVO.getUserID());
			channelUserTransferVO.setToParentID(sessionVO.getUserID());
			channelUserTransferVO.setDomainCode(channelUserVO.getDomainID());
		//	channelUserTransferVO.setZoneCode(sessionVO.getGeographicalAreaList().get(0).getGraphDomainCode());
			channelUserTransferVO.setNetworkCode(channelUserVO.getNetworkCode());
			channelUserTransferVO.setCreatedBy(channelUserVO.getCreatedBy());
			channelUserTransferVO.setCreatedOn(channelUserVO.getCreatedOn());
			channelUserTransferVO.setMultibox(PretupsI.RESET_CHECKBOX);
			channelUserTransferVO.setServiceType(channelUserVO.getServiceTypes());
			channelUserTransferVO.setDomainName(channelUserVO.getDomainName());
			channelUserTransferVO.setCategoryName(channelUserVO.getCategoryName());
			channelUserTransferVO.setMsisdn(channelUserVO.getMsisdn());
			channelUserTransferVO.setLoginId(channelUserVO.getLoginID());
			channelUserTransferVO.setParentUserName(channelUserVO.getParentName());
			channelUserTransferVO.setToParentUserName(sessionVO.getUserName());
			channelUserTransferVO.setIsOperationNotAllow(true);
			channelUserTransferVO.setParentUserID(channelUserVO.getParentID());
			channelUserTransferVO.setDomainCodeDesc(channelUserVO.getDomainTypeCode());
			channelUserTransferVO.setDomainList(channelUserVO.getDomainList());
			channelUserTransferVO.setGeographicalCode(channelUserVO.getGeographicalCode());
	 }

	public void downloadBulkModifyUsersList(MultiValueMap<String, String> headers, String domainType, String categoryType, String geoDomainType,
			BatchUserForm form, UserVO userVO, FileDownloadResponseMulti response, Connection con)
			throws BTSLBaseException, SQLException, IOException {
		final String METHOD_NAME = "loadDownloadFile";
        if (log.isDebugEnabled()) {
            log.debug("loadDownloadFile", "Entered");
        }
                
        boolean userVoucherTypeAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);

		MComConnectionI mcomCon = null;
        final HashMap masterDataMap = new HashMap();
        final ArrayList finalList = new ArrayList();
        ChannelUserWebDAO channelUserWebDAO = null;
        Locale locale = null;
        try {        	
    		locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
            channelUserWebDAO = new ChannelUserWebDAO();
            final BatchUserForm theForm = (BatchUserForm) form;
			mcomCon = new MComConnection();
            String filePath =Constants.getProperty("DOWNLOADMODIFYBULKUSERPATH");
            try {
                final File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
                log.error("loadDownloadFile", "Exception" + e.getMessage());
                throw new BTSLBaseException(this, "loadDownloadFile", "bulkuser.bulkusermodify.downloadfile.error.dirnotcreated", "selectDomainForBatchModify");

            }
            final String fileName = (theForm.getCategoryCode()).split(":")[0] + Constants.getProperty("DOWNLOADMODIFYBULKUSERFILENAMEPREFIX")+ BTSLUtil
                    .getFileNameStringFromDate(new Date())+ ".xlsx";
            final ArrayList geoDomainList = theForm.getGeographyList();
            final ArrayList domainList = theForm.getDomainList();
            if (geoDomainList != null && geoDomainList.size() > 1) {
                UserGeographiesVO userGeographiesVO = null;
                for (int i = 0, j = geoDomainList.size(); i < j; i++) {
                    userGeographiesVO = (UserGeographiesVO) geoDomainList.get(i);
                    if (userGeographiesVO.getGraphDomainCode().equals(theForm.getGeographyCode())) {
                        theForm.setGeographyName(userGeographiesVO.getGraphDomainName());
                        theForm.setGeographyStr(userGeographiesVO.getGraphDomainName());
                        break;
                    }
                }
            }
            // modified for category fixes during batch user update
            ArrayList catlist = null;
            final CategoryDAO categoryDAO = new CategoryDAO();
            catlist = categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT);
            theForm.setCategoryList(catlist);
            // end here
            CategoryVO categoryVO = null;
            for (int i = 0, j = catlist.size(); i < j; i++) {
                categoryVO = (CategoryVO) catlist.get(i);
                if (categoryVO.getCombinedKey().equals(theForm.getCategoryCode())) {
                    theForm.setCategoryVO(categoryVO);
                    theForm.setCategoryName(categoryVO.getCategoryName());
                    break;
                }
            }
            // Done for adding RSA Authentication in master sheet
            final Iterator catIter = catlist.listIterator();
            while (catIter.hasNext()) {
                catlist.remove(0);
            }
            catlist.add(categoryVO);

            ListValueVO listVO = null;
            if (domainList != null && domainList.size() > 1) {
                listVO = BTSLUtil.getOptionDesc(theForm.getDomainCode(), theForm.getDomainAllList());
                theForm.setDomainName(listVO.getLabel());
                theForm.setDomainType(listVO.getOtherInfo());
            }
            final BatchUserDAO batchUserDAO = new BatchUserDAO();
            final BatchUserWebDAO batchUserWebDAO = new BatchUserWebDAO();
            final SubLookUpDAO sublookupDAO = new SubLookUpDAO();
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO(); // Added
       
            masterDataMap.put(PretupsI.BATCH_USR_EXCEL_DATA, batchUserDAO.loadBatchUserListForModify(con, geoDomainType,
                (theForm.getCategoryCode()).split(":")[0], userVO.getUserID()));
            masterDataMap.put(PretupsI.BATCH_USR_CATEGORY_NAME, theForm.getCategoryName());
            masterDataMap.put(PretupsI.BATCH_USR_CATEGORY_VO, theForm.getCategoryVO());

            masterDataMap.put(PretupsI.BATCH_USR_CREATED_BY, userVO.getUserName());
            masterDataMap.put(PretupsI.BATCH_USR_GEOGRAPHY_NAME, theForm.getGeographyStr());
            masterDataMap.put(PretupsI.BATCH_USR_DOMAIN_NAME, theForm.getDomainName());
            masterDataMap.put(PretupsI.BATCH_USR_USER_PREFIX_LIST, LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true));
            masterDataMap.put(PretupsI.BATCH_USR_OUTLET_LIST, LookupsCache.loadLookupDropDown(PretupsI.OUTLET_TYPE, true));
            masterDataMap.put(PretupsI.BATCH_USR_SUBOUTLET_LIST, sublookupDAO.loadSublookupByLookupType(con, PretupsI.OUTLET_TYPE));
            final ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
            // Done for adding RSA Authentication in master sheet
            masterDataMap.put(PretupsI.BATCH_USR_CATEGORY_LIST, catlist);
            masterDataMap.put(PretupsI.BATCH_USR_SERVICE_LIST, servicesDAO.loadServicesList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE, theForm.getCategoryVO()
                .getCategoryCode(), false));
            masterDataMap.put(PretupsI.BATCH_USR_GEOGRAPHY_LIST, batchUserDAO.loadMasterGeographyForCategoryList(con, geoDomainType, userVO.getUserID(), (theForm
                .getCategoryCode()).split(":")[0]));
          //  masterDataMap.put(PretupsI.BATCH_USR_GEOGRAPHY_TYPE_LIST,
            //		batchUserWebDAO.loadCategoryGeographyTypeList(con, theForm.getDomainCode()));
            masterDataMap.put(PretupsI.BATCH_USR_GROUP_ROLE_LIST, batchUserDAO.loadMasterGroupRoleCodeList(con, theForm.getDomainType(), (theForm.getCategoryCode())
                .split(":")[0], PretupsI.YES));
            masterDataMap.put(PretupsI.BATCH_USR_ROLE_CODE_LIST, batchUserDAO.loadMasterGroupRoleCodeList(con, theForm.getDomainType(),
                (theForm.getCategoryCode()).split(":")[0], PretupsI.NO));
            masterDataMap.put(PretupsI.BATCH_USR_LANGUAGE_LIST, batchUserDAO.loadLanguageList(con));// added
            if (isTrfRuleUserLevelAllow) {
                masterDataMap.put(PretupsI.BATCH_USR_TRF_RULE_LIST, LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
            }
            if (lmsAppl) {
                masterDataMap.put(PretupsI.BATCH_USR_LMS_PROFILE, channelUserWebDAO.getLmsProfileList(con, userVO.getNetworkID()));
            }
         masterDataMap.put(PretupsI.BATCH_USR_COMM_LIST, batchUserDAO.loadCommProfileList(con, domainType, userVO.getNetworkID(),theForm.getCategoryCode().split(":")[0],
            		userVO.getUserType()));
            masterDataMap.put(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST, batchUserDAO.loadMasterTransferProfileList(con, domainType,
                    userVO.getNetworkID(), theForm.getCategoryCode().split(":")[0], userVO.getUserType()));
            masterDataMap.put(PretupsI.BATCH_USR_GRADE_LIST, batchUserDAO.loadMasterCategoryGradeList(con, domainType, theForm.getCategoryCode().split(":")[0], userVO.getUserType()));
            masterDataMap.put(PretupsI.USER_TYPE, userVO.getUserType());
            masterDataMap.put(PretupsI.USER_DOCUMENT_TYPE, LookupsCache.loadLookupDropDown(PretupsI.USER_DOCUMENT_TYPE, true));
            masterDataMap.put(PretupsI.PAYMENT_INSTRUMENT_TYPE, LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true));
            if(userVoucherTypeAllowed && ((Boolean) PreferenceCache.getControlPreference(PreferenceI.CHNLUSR_VOUCHER_CATGRY_ALLWD, userVO.getNetworkID(), theForm.getCategoryCode().split(":")[0])).booleanValue())
                masterDataMap.put(PretupsI.VOUCHER_TYPE_LIST, new VomsProductDAO().loadVoucherTypeList(con));	
            if (SystemPreferences.USERWISE_LOAN_ENABLE)
            	masterDataMap.put(PretupsI.BATCH_LOAN_PROFILE_LIST, batchUserDAO.getAllLoanProfileList(con));
			
            theForm.setBulkUserMasterMap(masterDataMap);            
            finalList.add(masterDataMap.get("BATCH_USR_EXCEL_DATA"));
            
            final BatchUserCreationExcelRWPOI excelRW = new BatchUserCreationExcelRWPOI();
            String [] queryParams = new String[4];
            queryParams[0] = geoDomainType;
            queryParams[1] = theForm.getCategoryCode().split(":")[0];
            queryParams[2] = userVO.getUserID();
            queryParams[3]= theForm.getDomainName();
            excelRW.writeBulkModifyExcel(ExcelFileIDI.BATCH_CHNL_USER_MODIFY, masterDataMap,
            		locale,
            		filePath + fileName, con, queryParams);     
            
			File fileNew = new File(filePath + "" + fileName);
			byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
			String encodedString = Base64.getEncoder().encodeToString(
					fileContent);
			String file1 = fileNew.getName();
			response.setFileattachment(encodedString);
			response.setFileType("xlsx");
			response.setFileName(file1);
			String sucess = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(sucess);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

            filePath = BTSLUtil.encrypt3DesAesText(filePath);



        } catch (Exception ex) {
			log.errorTrace(METHOD_NAME, ex);
			log.error(
					METHOD_NAME,
					"Unable to write data into a file Exception = "
							+ ex.getMessage());
			throw new BTSLBaseException(
					ChannelAdminUserHierarchyController.class.getName(), METHOD_NAME,
					PretupsErrorCodesI.FILE_WRITE_ERROR);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChannelAdminUserHeirarchyServiceImpl#downloadBulkModifyUsersList");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:forward=");
            }
        }
	}
	
	public ArrayList<MasterErrorList> basicFileValidations(BulkModifyUserRequestVO request, BulkModifyUserResponseVO response, String domainType, String categoryType, String geoDomainType, Locale locale, ArrayList<MasterErrorList> inputValidations) {

		String pattern=Constants.getProperty("NAME_REGEX_ALPHANUMERIC");
		 
		 if(BTSLUtil.isNullString(categoryType)) {
			 MasterErrorList masterErrorList = new MasterErrorList();
				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_APLHA_CAT,null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_APLHA_CAT);
				masterErrorList.setErrorMsg(msg);
				inputValidations.add(masterErrorList);
				response.setMessage(msg);
			}
		 if(!BTSLUtil.isNullString(categoryType)) {
		 String noSpaceStr = categoryType.replaceAll("\\s", ""); // using built in method just to check for aplhanumeric  
		 if(!noSpaceStr.matches(pattern)){
			 MasterErrorList masterErrorList = new MasterErrorList();
			 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_APLHA_CAT,null);
			 masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_APLHA_CAT);
				masterErrorList.setErrorMsg(msg);
				inputValidations.add(masterErrorList);
			}
		 }
		 
		 if(BTSLUtil.isNullString(domainType)) {
			 MasterErrorList masterErrorList = new MasterErrorList();
				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GRPH_INVALID_DOMAIN,null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.GRPH_INVALID_DOMAIN);
				masterErrorList.setErrorMsg(msg);
				inputValidations.add(masterErrorList);
			}
		 if(!BTSLUtil.isNullString(domainType)) {
		 String noSpaceStr = domainType.replaceAll("\\s", ""); // using built in method just to check for aplhanumeric  
		 if(!noSpaceStr.matches(pattern)){
			 MasterErrorList masterErrorList = new MasterErrorList();
			 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GRPH_INVALID_DOMAIN,null);
			 masterErrorList.setErrorCode(PretupsErrorCodesI.GRPH_INVALID_DOMAIN);
				masterErrorList.setErrorMsg(msg);
				inputValidations.add(masterErrorList);
			}
		 }
		 
//		 if(BTSLUtil.isNullString(geoDomainType)) {
//			 MasterErrorList masterErrorList = new MasterErrorList();
//				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEO_DOMAIN_TYPE_INVALID,null);
//				masterErrorList.setErrorCode(PretupsErrorCodesI.GEO_DOMAIN_TYPE_INVALID);
//				masterErrorList.setErrorMsg(msg);
//				inputValidations.add(masterErrorList);
//			}
//		 if(!BTSLUtil.isNullString(geoDomainType)) {
//		 String noSpaceStr = geoDomainType.replaceAll("\\s", ""); // using built in method just to check for aplhanumeric  
//		 if(!noSpaceStr.matches(pattern)){
//			 MasterErrorList masterErrorList = new MasterErrorList();
//			 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEO_DOMAIN_TYPE_INVALID,null);
//			 masterErrorList.setErrorCode(PretupsErrorCodesI.GEO_DOMAIN_TYPE_INVALID);
//				masterErrorList.setErrorMsg(msg);
//				inputValidations.add(masterErrorList);
//			}
//		 }
		 
			if (!BTSLUtil.isNullorEmpty(request.getFileName()) &&  !BTSLUtil.isNullorEmpty(request.getFileAttachment())
					&& !BTSLUtil.isNullorEmpty(request.getFileType())) {
				String base64val = request.getFileAttachment();
				String requestFileName = request.getFileName();

		        boolean isValid = true;
		          
		        if (request.getFileName().length() > 100) {
		  		    MasterErrorList masterErrorListFileName = new MasterErrorList();
		  			masterErrorListFileName.setErrorMsg("File Name length too large.");
		  			masterErrorListFileName.setErrorCode("");
		  			inputValidations.add(masterErrorListFileName);
		  			isValid = false ;
		  	    }
		  		if (!C2CFileUploadApiController.isValideFileName(request.getFileName())) {
		  			MasterErrorList masterErrorList = new MasterErrorList();
		  			masterErrorList.setErrorMsg("Invalid file name.");
		  			masterErrorList.setErrorCode("");
		  			inputValidations.add(masterErrorList);
		  			isValid = false ;
		  		}
		  		if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(request.getFileType().toUpperCase())) {
		  			String fileNamewithextention = requestFileName + ".csv";
		  		} else if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(request.getFileType().toUpperCase())) {
		  			String fileNamewithextention = requestFileName + ".xls";
		  		} else if (PretupsI.FILE_CONTENT_TYPE_XLSX.equals(request.getFileType().toUpperCase())) {
		  			String fileNamewithextention = requestFileName + ".xlsx";
		  		} else {
		  			MasterErrorList masterErrorList = new MasterErrorList();
		  			masterErrorList.setErrorMsg("Invalid file type.");
		  			masterErrorList.setErrorCode("");
		  			inputValidations.add(masterErrorList);
		  			isValid = false ;
		  		}
		  		
			} else {
				boolean isValid = true;

				if (BTSLUtil.isNullorEmpty(request.getFileName())) {
					MasterErrorList masterErrorList = new MasterErrorList();
					masterErrorList.setErrorMsg("File name is empty.");
					masterErrorList.setErrorCode("");
					inputValidations.add(masterErrorList);
					isValid = false ;
				}
				if (BTSLUtil.isNullorEmpty(request.getFileAttachment())) {
					MasterErrorList masterErrorList = new MasterErrorList();
					masterErrorList.setErrorMsg("File attachment is empty.");
					masterErrorList.setErrorCode("");
					inputValidations.add(masterErrorList);
					response.setMessage("File is Empty");
					isValid = false ;
				}
				if (BTSLUtil.isNullorEmpty(request.getFileType())) {
					MasterErrorList masterErrorList = new MasterErrorList();
					masterErrorList.setErrorMsg("File type is empty.");
					masterErrorList.setErrorCode("");
					inputValidations.add(masterErrorList);
					isValid = false ;
				}
			}
			
			return inputValidations;
	}
	
	public boolean uploadAndValidateModifyBulkUserFile(Connection con,MComConnectionI mcomCon, ChannelUserVO userVO, BulkModifyUserRequestVO request, BulkModifyUserResponseVO response,ArrayList fileErrorList) throws BTSLBaseException, SQLException {
        
		final String methodName = "uploadAndValidateModifyBulkUserFile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        
        ProcessStatusVO processVO = null;
//        final ProcessBL processBL = new ProcessBL();
        boolean processRunning = true;
        boolean isUploaded = false;

        
        try {
            final ProcessBL processBL = new ProcessBL();
            try {
                processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.BATCH_USR_PROCESS_ID,userVO.getNetworkID());
            } catch (BTSLBaseException e) {
                LOG.error("uploadAndValidateModifyBulkUserFile", "Exception:e=" + e);
                LOG.errorTrace(methodName, e);
                processRunning = false;
            }
//            processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.BATCH_USR_PROCESS_ID,userVO.getNetworkID());
            if (processVO != null && !processVO.isStatusOkBool()) {
                processRunning = false;
                throw new BTSLBaseException(PretupsErrorCodesI.C2C_BATCH_ALREADY_RUNNING);
            }
            
            // If The process is not running commit the connection to update
            // Process status
        
            mcomCon.partialCommit()	;
            processVO.setNetworkCode(userVO.getNetworkID());
            
            final String dir = Constants.getProperty("UPLOADMODIFYBATCHUSERFILEPATH"); // Upload

            if (BTSLUtil.isNullString(dir)) {
                throw new BTSLBaseException(this, methodName, "bulkuser.uploadandvalidatebulkuserfile.error.pathnotdefined");
            }
            
            final String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_XLSX);

            String fileSize = null;
            fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE_FOR_BULKUSER");
            if (BTSLUtil.isNullString(fileSize)) {
                fileSize = String.valueOf(0);
            }
            
			ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
	    	ErrorMap errorMap = new ErrorMap();
	        LinkedHashMap<String, List<String>> bulkDataMap = null; ;
            String file = request.getFileAttachment();
            String filePath = Constants.getProperty("DOWNLOADMODIFYBULKUSERPATH");
			HashMap<String, String>  fileDetailsMap = new HashMap<String, String>();
			fileDetailsMap.put(PretupsI.FILE_TYPE1, request.getFileType());
			fileDetailsMap.put(PretupsI.FILE_NAME,request.getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, file);
			fileDetailsMap.put(PretupsI.SERVICE_KEYWORD, PretupsI.BATCH_OPT_USR_MODIFICATION_SERVICE);	
			bulkDataMap = fileUtil.uploadAndReadGenericFileBatchUserModify(fileDetailsMap, 1, 5, errorMap, fileErrorList);
			bulkDataMap = getMapInFileFormat(bulkDataMap);
			isUploaded = true;
			
        } 
			finally {
				try {
	            	if(mcomCon != null){
	            		mcomCon.partialRollback();
	            	}
	            } catch (Exception ee) {
	                log.errorTrace(methodName, ee);
	            }
            if (processRunning) {
                try {
                    processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    final ProcessStatusDAO processDAO = new ProcessStatusDAO();
                    if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {              
                    	mcomCon.finalCommit();
                    } else {
                     
                    	mcomCon.finalRollback();
                    }
                } catch (Exception e) {

                    if (LOG.isDebugEnabled()) {
                        LOG.error(methodName, " Exception in update process detail for bulk user modification" + e.getMessage());
                    }
                    LOG.errorTrace(methodName, e);
                }
			}
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting");
            }
        }   
        return isUploaded;
	}
	
	public boolean processUploadedModifyBulkUserFile(Connection con,MComConnectionI mcomCon, ChannelUserVO userVO, String categoryType, String geoDomainType, BulkModifyUserRequestVO request, BulkModifyUserResponseVO response, HttpServletResponse responseSwag,ArrayList fileErrorList,int emptyRowCount) throws BTSLBaseException, SQLException, IOException{
		
        final String methodName = "processUploadedModifyBulkUserFile";
        if (log.isDebugEnabled()) {
        	log.debug(methodName, "Entered");
        }
        
        final String XLS_PINPASSWARD = "****";
        boolean isFnameLnameAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED);
        String pinPasswordEnDeCryptionType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE);
        boolean batchUserPasswordModifyAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PASSWD_MODIFY_ALLOWED);
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        boolean rsaAuthenticationRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RSA_AUTHENTICATION_REQUIRED);
        boolean authTypeReq = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTH_TYPE_REQ);
        boolean userVoucherTypeAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
        boolean loginPasswordAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED);
        boolean spaceAllowInLogin = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SPACE_ALLOW_IN_LOGIN);
        String c2sDefaultPassword = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD);
        String c2sDefaultSmsPin = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN);
        boolean externalCodeMandatoryForUser = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORUSER);
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        boolean realtimeOtfMsgs = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS);
        int rows = 0;
        int cols = 0;
        CommonUtil 	commutil = new CommonUtil();
		  String regExMsg=null;
        final HashMap<String, String> map = new HashMap<String, String>();
        
        int totalCols = 30;
        if (isFnameLnameAllowed) {
            totalCols += 1;
        }
        
        log.info(methodName,  "Constant.properties IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES ->  "  + Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES") );

        String[][] excelArr = null;
        boolean fileValidationErrorExists = false;
        ArrayList arr = null;
        String modificationType = null;
        String userID = null;
        String password = null;
        boolean passwordStatus = true;
        String mobileno = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        
        final int DATAROWOFFSET = 7;
        int totalRecordsInFile = 0;
        int noOftasks = 0;
        int taskSize = 0;
        String pinEntry="";
		String fileName = request.getFileName();
		final String dir = Constants.getProperty("UPLOADMODIFYBATCHUSERFILEPATH");

        try (InputStream isDummy = new FileInputStream(new File(dir+fileName+"."+request.getFileType()))){
        	channelUserWebDAO = new ChannelUserWebDAO();
        	// Open the uploaded XLS file parse row by row and validate the file
        	final BatchUserCreationExcelRWPOI excelRW = new BatchUserCreationExcelRWPOI();
        	final BatchUserDAO batchUserDAO = new BatchUserDAO();
        	final BatchUserWebDAO batchUserWebDAO = new BatchUserWebDAO();
        	final SubLookUpDAO sublookupDAO = new SubLookUpDAO();
        	final HashMap masterDataMap = new HashMap();
        	final UserDAO userDAO = new UserDAO();
            ArrayList catlist = null;
            final CategoryDAO categoryDAO = new CategoryDAO();
            catlist = categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT);
            CategoryVO categoryVO = null;
            for (int i = 0, j = catlist.size(); i < j; i++) {
                categoryVO = (CategoryVO) catlist.get(i);
                if ((categoryVO.getCategoryName().equals(categoryType)) || (categoryVO.getCategoryCode().equals(categoryType))) {
                	break;
                }
            }
			final ArrayList lmsProfileList = channelUserWebDAO.getLmsProfileList(con, userVO.getNetworkID());
        	if ((categoryVO.getWebInterfaceAllowed()).equals(PretupsI.YES)) {
        		if ("SHA".equalsIgnoreCase(pinPasswordEnDeCryptionType)) {
        			totalCols = totalCols + 3;
        		} else {
        			if (batchUserPasswordModifyAllow) {
        				totalCols = totalCols + 4;
        			} else {
        				totalCols = totalCols + 3;
        			}
        		}
        	}
        	
        	if (categoryVO.getSmsInterfaceAllowed().equals(PretupsI.YES)) {
        		if ("SHA".equalsIgnoreCase(pinPasswordEnDeCryptionType)) {
        			totalCols = totalCols + 1;
        		} else {
        			totalCols = totalCols + 2;
        		}
        	}
        	
        	if (categoryVO.getServiceAllowed().equals(PretupsI.YES)) {
        		totalCols = totalCols + 1;
        	}
        	if (categoryVO.getOutletsAllowed().equals(PretupsI.YES)) {
        		totalCols = totalCols + 2;
        	}
        	if (categoryVO.getLowBalAlertAllow().equals(PretupsI.YES)) {
        		totalCols++;
        	}
        	if (isTrfRuleUserLevelAllow) {
        		totalCols++;
        	}
        	if (rsaAuthenticationRequired) {
        		totalCols++;
        	}

        	if (authTypeReq) {
        		totalCols++;
        	}

        	if (lmsAppl) {
        		totalCols++;
        	}
        	if (userVoucherTypeAllowed && ((Boolean) PreferenceCache.getControlPreference(PreferenceI.CHNLUSR_VOUCHER_CATGRY_ALLWD, userVO.getNetworkID(), 
        			categoryVO.getCategoryCode())).booleanValue()) {
        		totalCols++;
        	}
        	if (SystemPreferences.USERWISE_LOAN_ENABLE) {
        		totalCols++;
        	}
        	
       	 int maxAllowedRecords = 0;
       	 try {
       		 maxAllowedRecords = Integer.parseInt(Constants.getProperty("MAXRECORDSINBULKUSERMODIFY"));
            } catch (Exception e) {
           	 maxAllowedRecords = 1000;
                LOG.error(methodName, "Exception:e=" + e);
                LOG.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserInitiateAction[processUploadedFile]", "",
                    "", "", "Exception:" + e.getMessage());
            }
     	 Workbook workbookDummy = StreamingReader.builder()
    			.rowCacheSize(100)   
    			.bufferSize(4096)    
    			.open(isDummy);
     	int  sheetCnt = workbookDummy.getNumberOfSheets() - 1;
     	int c = 0;
     	for (int sheetNo = 0; sheetNo < sheetCnt; sheetNo++) {
     		Sheet  excelsheet = workbookDummy.getSheetAt(sheetNo);
     		for(Row r : excelsheet) {
     			rows++;
     			if(rows == DATAROWOFFSET) {
     				for(Cell cell : r) c++;
     				if(c!=totalCols) {
     					isDummy.close();  
     					this.deleteUploadedFile(request);
     	        		throw new BTSLBaseException(this, methodName, "bulkuser.processuploadedfile.error.notvalidfile");
     				}
     			}
     			if(rows > DATAROWOFFSET+maxAllowedRecords) {
     				isDummy.close();
 					this.deleteUploadedFile(request);
             		throw new BTSLBaseException(this, methodName, "bulkuser.processuploadedfile.modify.error.maxlimitexceeded");
     			}  
     		}
     	}
    	if(rows == 0 || rows == DATAROWOFFSET) {
    		isDummy.close();
				this.deleteUploadedFile(request);
    		throw new BTSLBaseException(this, "processUploadedFile", "bulkuser.processuploadedfile.error.norecordinfile");
    	}	
        
    	totalRecordsInFile = rows - DATAROWOFFSET;
    	rows = 0;
    	ChannelUserVO channelUserVO = null;
    	ChannelUserVO channelUserOldVO = null;
    	ListValueVO errorVO = null;
    	ArrayList channelUserVOList = new ArrayList();
    	int colIndex;

    	ArrayList geographicalAreaList = null;
        ArrayList geographicalAreaListAll = userVO.getGeographicalAreaList();
    	UserGeographiesVO geographiesVO = null;
        for (int i = 0, j = geographicalAreaListAll.size(); i < j; i++) {
        	geographiesVO = (UserGeographiesVO) geographicalAreaListAll.get(i);
            if ((geographiesVO.getGraphDomainName().equals(geoDomainType)) || (geographiesVO.getGraphDomainCode().equals(geoDomainType))){
                break;	
            }
        }
        
        ListValueVO listvalueVO = null;
        listvalueVO = BTSLUtil.getOptionDesc(categoryVO.getDomainCodeforCategory(), new DomainDAO().loadCategoryDomainList(con));
        
    	UserGeographiesVO userGeographiesVO = null;
    	
    	GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
    	String geographyName  = geographicalDomainDAO.getGeographyName(con, geoDomainType, true);
     	masterDataMap.put(PretupsI.BATCH_USR_EXCEL_DATA, batchUserDAO.loadBatchUserListForModifyPOI(con, geographiesVO.getGraphDomainCode(),
                categoryVO.getCategoryCode(), userVO.getUserID()));
    	masterDataMap.put(PretupsI.BATCH_USR_CATEGORY_NAME, categoryVO.getCategoryName());
    	masterDataMap.put(PretupsI.BATCH_USR_CREATED_BY, userVO.getUserName());
    	masterDataMap.put(PretupsI.BATCH_USR_GEOGRAPHY_NAME, geographiesVO.getGraphDomainName());
    	masterDataMap.put(PretupsI.BATCH_USR_DOMAIN_NAME, listvalueVO.getLabel());
    	masterDataMap.put(PretupsI.BATCH_USR_USER_PREFIX_LIST, LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true));
    	masterDataMap.put(PretupsI.BATCH_USR_OUTLET_LIST, LookupsCache.loadLookupDropDown(PretupsI.OUTLET_TYPE, true));
    	masterDataMap.put(PretupsI.BATCH_USR_SUBOUTLET_LIST, sublookupDAO.loadSublookupByLookupType(con, PretupsI.OUTLET_TYPE));
    	final ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
    	masterDataMap.put(PretupsI.BATCH_USR_SERVICE_LIST, servicesDAO.loadServicesList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE, categoryVO.getCategoryCode(), false));
    	masterDataMap.put(PretupsI.BATCH_USR_GEOGRAPHY_LIST, batchUserDAO.loadMasterGeographyForCategoryList(con, geographiesVO.getGraphDomainCode(), userVO.getUserID(),
    			categoryVO.getCategoryCode()));
    	masterDataMap.put(PretupsI.BATCH_USR_GROUP_ROLE_LIST, batchUserDAO.loadMasterGroupRoleCodeList(con, listvalueVO.getOtherInfo(), categoryVO.getCategoryCode(),
    			PretupsI.YES));
    	masterDataMap.put(PretupsI.BATCH_USR_ROLE_CODE_LIST, batchUserDAO.loadMasterGroupRoleCodeList(con, listvalueVO.getOtherInfo(), categoryVO.getCategoryCode()
    			, PretupsI.NO));
    	if (isTrfRuleUserLevelAllow) {
    		masterDataMap.put(PretupsI.BATCH_USR_TRF_RULE_LIST, LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
    	}
    	masterDataMap.put(PretupsI.BATCH_USR_LANGUAGE_LIST, batchUserDAO.loadLanguageList(con));// added
    	// by
    	// gaurav
        masterDataMap.put(PretupsI.BATCH_USR_COMM_LIST, batchUserDAO.loadCommProfileList(con, categoryVO.getDomainCodeforCategory(),  userVO.getNetworkID(), categoryVO.getCategoryCode() ,
        		userVO.getUserType()));
        masterDataMap.put(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST, batchUserDAO.loadMasterTransferProfileList(con, categoryVO.getDomainCodeforCategory(),
                userVO.getNetworkID(), categoryVO.getCategoryCode(), userVO.getUserType()));
        masterDataMap.put(PretupsI.BATCH_USR_GRADE_LIST, batchUserDAO.loadMasterCategoryGradeList(con, categoryVO.getDomainCodeforCategory(), categoryVO.getCategoryCode(), userVO.getUserType()));
        masterDataMap.put(PretupsI.USER_DOCUMENT_TYPE, LookupsCache.loadLookupDropDown(PretupsI.USER_DOCUMENT_TYPE, true));
        masterDataMap.put(PretupsI.PAYMENT_INSTRUMENT_TYPE, LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true));
        VomsProductDAO voucherDAO = new VomsProductDAO();
        if(userVoucherTypeAllowed && ((Boolean) PreferenceCache.getControlPreference(PreferenceI.CHNLUSR_VOUCHER_CATGRY_ALLWD, userVO.getNetworkID(), categoryVO.getCategoryCode())).booleanValue()){
           masterDataMap.put(PretupsI.VOUCHER_TYPE_LIST, voucherDAO.loadVoucherTypeList(con));
        }
        
        int gradeSize = 0, grpSize = 0, comPrfSize = 0, transPrfSize = 0;
        ArrayList commPrfList = null;
        ArrayList transferPrfList = null;            
        ArrayList gradeList = null;
        CommissionProfileSetVO commissionProfileSetVO = null;
        GradeVO gradeVO = null;
        gradeList = (ArrayList) masterDataMap.get(PretupsI.BATCH_USR_GRADE_LIST);
        if (gradeList != null) {
            gradeSize = gradeList.size();
        }
        commPrfList = (ArrayList) masterDataMap.get(PretupsI.BATCH_USR_COMM_LIST);
        comPrfSize = commPrfList.size();
        transferPrfList = (ArrayList) masterDataMap.get(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST);
        transPrfSize = transferPrfList.size();
//    	theForm.setBulkUserMasterMap(masterDataMap);
    	final ArrayList prefixList = (ArrayList) masterDataMap.get(PretupsI.BATCH_USR_USER_PREFIX_LIST);
    	int geoSize = 0;
    	final ArrayList geographyList = (ArrayList) masterDataMap.get(PretupsI.BATCH_USR_GEOGRAPHY_LIST);
    	final ArrayList geographyCodeList = new ArrayList();
    	if (geographyList != null) {
    		geoSize = geographyList.size();
    		for (int i = 0; i < geoSize; i++) {
    			userGeographiesVO = (UserGeographiesVO) geographyList.get(i);
    			geographyCodeList.add(userGeographiesVO.getGraphDomainCode());
    		}
    	}
        
    	final ArrayList outletList = (ArrayList) masterDataMap.get(PretupsI.BATCH_USR_OUTLET_LIST);
    	final ArrayList subOutletList = (ArrayList) masterDataMap.get(PretupsI.BATCH_USR_SUBOUTLET_LIST);
    	final ArrayList documentTypeList = (ArrayList) masterDataMap.get(PretupsI.USER_DOCUMENT_TYPE);
    	final ArrayList paymentTypeList = (ArrayList) masterDataMap.get(PretupsI.PAYMENT_INSTRUMENT_TYPE);
    	final ArrayList voucherTypeList = (ArrayList) masterDataMap.get(PretupsI.VOUCHER_TYPE_LIST);
    	
    	int MAX_VOUCHERS = 0;
    	String voucherTypeArr[] = null;
    	int voucherTypeLen = 0;
    	if (voucherTypeList != null && (MAX_VOUCHERS = voucherTypeList.size()) > 0) {
    		voucherTypeArr = new String[MAX_VOUCHERS];
    	}
    	
    	String filteredMsisdn = null;
    	boolean found = false;
    	int MAX_SERVICES = 0;
    	String serviceArr[] = null;
    	int serviceLen = 0;
    	final ArrayList serviceList = (ArrayList) masterDataMap.get(PretupsI.BATCH_USR_SERVICE_LIST);
    	if (serviceList != null && (MAX_SERVICES = serviceList.size()) > 0) {
    		serviceArr = new String[MAX_SERVICES];
    	}
    	UserRolesVO rolesVO = null;
    	String msisdnPrefix = null;
    	NetworkPrefixVO networkPrefixVO = null;
    	ArrayList newServiceList = new ArrayList();
    	ArrayList newVoucherTypeList = new ArrayList();
    	final Date currentDate = new Date();
        TransferProfileVO profileVO = null;
        
    	HashMap<String,ChannelUserVO> channelUserVOMap = (HashMap<String, ChannelUserVO>) masterDataMap.get(PretupsI.BATCH_USR_EXCEL_DATA);
    	final HashMap languageMap = (HashMap) masterDataMap.get(PretupsI.BATCH_USR_LANGUAGE_LIST);
    	final ArrayList roleCodeList = new ArrayList();
    	final ArrayList groupRoleCodeList = new ArrayList();
    	final ArrayList serviceCodeList = new ArrayList();
    	final ArrayList voucherCodeList = new ArrayList();
    	ListValueVO listValueVO = null;
    	if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
    		ArrayList rolesVOList = (ArrayList) masterDataMap.get(PretupsI.BATCH_USR_ROLE_CODE_LIST);
    		rolesVO = null;
    		if (rolesVOList != null) {
    			for (int i = 0, j = rolesVOList.size(); i < j; i++) {
    				rolesVO = (UserRolesVO) rolesVOList.get(i);
    				roleCodeList.add(rolesVO.getRoleCode());
    			}
    		}
    		rolesVOList = null;
    		rolesVOList = (ArrayList) masterDataMap.get(PretupsI.BATCH_USR_GROUP_ROLE_LIST);
    		rolesVO = null;
    		if (rolesVOList != null) {
    			for (int i = 0, j = rolesVOList.size(); i < j; i++) {
    				rolesVO = (UserRolesVO) rolesVOList.get(i);
    				groupRoleCodeList.add(rolesVO.getRoleCode());
    			}
    		}
    	}
    	
    	if (categoryVO.getServiceAllowed().equals(PretupsI.YES)) {
    		final ArrayList listValueVOList = (ArrayList) masterDataMap.get(PretupsI.BATCH_USR_SERVICE_LIST);

    		if (listValueVOList != null) {
    			int listValueVOLists=listValueVOList.size();
    			for (int i = 0, j =listValueVOLists ; i < j; i++) {
    				listValueVO = (ListValueVO) listValueVOList.get(i);
    				serviceCodeList.add(listValueVO.getValue());
    			}
    		}
    	}
        
    	if(userVoucherTypeAllowed && masterDataMap.get(PretupsI.VOUCHER_TYPE_LIST) != null) {
    		final ArrayList listValueVOList = (ArrayList) masterDataMap.get(PretupsI.VOUCHER_TYPE_LIST);
    		if (listValueVOList != null) {
    			int listValueVOLists=listValueVOList.size();
    			for (int i = 0, j =listValueVOLists ; i < j; i++) {
    				listValueVO = (ListValueVO) listValueVOList.get(i);
    				voucherCodeList.add(listValueVO.getValue());
    			}
    		}
    	}
        
    	ArrayList trfRuleTypeList = null;
    	if (isTrfRuleUserLevelAllow) {
    		trfRuleTypeList = (ArrayList) masterDataMap.get(PretupsI.BATCH_USR_TRF_RULE_LIST);
    	}
    	int colIt = 0;
    	boolean foundUserId = false;
         ArrayList channelUserVODataList = new ArrayList();
		List<ChannelUserVO> list= new ArrayList<>();
    	ListValueVO listVO = null;
    	String pin = null;
    	boolean invaliedgeographyCode = false;
    	boolean invaliedRoleCode = false;
    	boolean invailedService = false;
    	boolean invailedVoucher = false;
    	boolean[] isPinModify = null;
    	UserPhoneVO userPhoneVO = null;
    	int reptRowNo = 0;
    	HashMap error_messageMap = null;
    	String errorMessage = "";
    	Set passwordErrSetKey = null;
    	Iterator itr = null;
    	String rowVal = null;
    	OperatorUtilI operatorUtili = null;
    	int noOfMsisdn = 0;
    	int adjustCounter = 0;
    	Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
    	
    	try {
    		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
    		operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
    	} catch (Exception e) {
    		LOG.errorTrace(methodName, e);
    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelAdminUserHeirarchyServiceImpl[processUploadedModifyBulkUserFile]", "",
    				"", "", "Exception while loading the class at the call:" + e.getMessage());
    	}
    	// End of OCI changes
        Integer invalidRecordCount = 0;

    	ArrayList dbErrorList = new ArrayList();    
    	StringBuilder ashuBuild = new StringBuilder("");
    	  String excel[][] = new String[maxAllowedRecords+10][1000];
    	//ASHU reading xlsx files using xlsx streamer
    	int threadPoolSize =  Runtime.getRuntime().availableProcessors() + 1;
    	taskSize = totalRecordsInFile / threadPoolSize;
    	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize);     
    	InputStream is = null;
		ErrorMap errMap = new ErrorMap();
		List<RowErrorMsgLists> rowErrors = new ArrayList<>();
    	try{
    	is = new FileInputStream(new File(dir+fileName+"."+request.getFileType()));
    	Workbook workbook = StreamingReader.builder()
    			.rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
    			.bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
    			.open(is);            // InputStream or File for XLSX file (required)

    	int  sheetCount = workbook.getNumberOfSheets() - 1;
		String grphDomainCode = "";
		Cell cell=null;
		
		
    	for (int sheetNo = 0; sheetNo < sheetCount; sheetNo++) {
    		Sheet  excelsheet = workbook.getSheetAt(sheetNo);
    		fileValidationErrorExists = false;
    		
    		
			for(Row r : excelsheet) {    
    			boolean checkErrors = false;
    			fileValidationErrorExists = false;
    		    rows++;
    		    Iterator it = fileErrorList.listIterator();
    		    
    		    for (int i = 0, j = fileErrorList.size(); i < j; i++) {
    		    	ArrayList list2 = (ArrayList)fileErrorList.get(i);
   	        	 	if((list2.get(0)).equals(""+rows)){
   	        	 		rows++;
   	        	 }

               	 }
    		    RowErrorMsgLists singleRowError = new RowErrorMsgLists();
    		    List<MasterErrorList> masterErrorObjList = new ArrayList<>();
    		    singleRowError.setMasterErrorList(masterErrorObjList);
             	singleRowError.setRowValue(String.valueOf(rows));
             	
             	if(r.getRowNum()==3) {
             		Cell value = r.getCell(1);
             		
             		String str  = cellValueNull(value);
             		if(!str.equals(categoryVO.getCategoryName())) {
             			throw new BTSLBaseException(this, methodName, "bulkuser.processuploadedfile.error.notvalidfile");
             		}
             	}if(r.getRowNum()==4) {
             		Cell value = r.getCell(1);
             		
             		String str  = cellValueNull(value);
             		if(!str.equals(geographyName)) {
             			throw new BTSLBaseException(this, methodName, "bulkuser.processuploadedfile.error.notvalidfile");
             		}
             	}
    			if(r.getRowNum() < DATAROWOFFSET) continue;  
    			int noOfRows = r.getLastCellNum();
    			boolean emptyRow = true;
    			if(noOfRows>0) {
    				
    				for(int colNo =0; colNo<=noOfRows;colNo++) {
    					Cell value = r.getCell(colNo);
    					
    					if(!BTSLUtil.isNullString(cellValueNull(value))&& !cellValueNull(value).isEmpty()) {
    						emptyRow =false;
    						break;
    					}
    						
    					}
    				}
    				if(emptyRow) {
    					
    				String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EMPTY_ROW, null);
                 	MasterErrorList err = new MasterErrorList();
                 	err.setErrorCode(PretupsErrorCodesI.EMPTY_ROW);
                 	err.setErrorMsg(error);
                 	
//    				error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    						"bulkuser.processuploadedfile.error.useridmissing")	);
//    				add master error to master error list
                	ArrayList allRowErrorList = new ArrayList();
                 	allRowErrorList.add(String.valueOf(rows));
                 	allRowErrorList.add(error);
					fileErrorList.add(allRowErrorList);
    				fileValidationErrorExists = true;
    				checkErrors = true;
                 	singleRowError.getMasterErrorList().add(err);
                 	invalidRecordCount++;
                 	
    			}if(!emptyRow){
    			colIt = 0;
    			
    			found = false;
    			foundUserId = false;
    			 cell = r.getCell(colIt);
    			String cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) // User id check
    			{
   				cellValue = cellValue.trim();
    				//ASHU changes to search channel user in a map
    				channelUserOldVO = channelUserVOMap.get(cellValue);
    				foundUserId = (channelUserOldVO!=null) ? true : false;                                  
    			} else {
                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.useridmissing", null);
                 	MasterErrorList err = new MasterErrorList();
                 	err.setErrorCode("bulkuser.processuploadedfile.error.useridmissing");
                 	err.setErrorMsg(error);
                 	
//    				error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    						"bulkuser.processuploadedfile.error.useridmissing")	);
//    				add master error to master error list
                	ArrayList allRowErrorList = new ArrayList();
                 	allRowErrorList.add(String.valueOf(rows));
                 	allRowErrorList.add(error);
					fileErrorList.add(allRowErrorList);
    				fileValidationErrorExists = true;
    				checkErrors = true;
                 	singleRowError.getMasterErrorList().add(err);
                 	
                 	 
    				
    			}
    			if (!foundUserId) {
                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.useridnotexist", null);
//    				error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    						"bulkuser.processuploadedfile.error.useridnotexist"));
                 	MasterErrorList err = new MasterErrorList();
                 	err.setErrorCode("bulkuser.processuploadedfile.error.useridnotexist");
                 	err.setErrorMsg(error);
                 	//masterErrorList.add(err);
                	ArrayList allRowErrorList = new ArrayList();
                 	allRowErrorList.add(String.valueOf(rows));
                 	allRowErrorList.add(error);
					fileErrorList.add(allRowErrorList);
    				fileValidationErrorExists = true;
    				checkErrors = true;
    				singleRowError.getMasterErrorList().add(err);
    		    	errMap.setRowErrorMsgLists(rowErrors);
    		    	
    		    	
    			
    			}
    			// *********Field Number 2: User Name Prefix
    			// validation*****************************************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
    			cell = r.getCell(colIt);
    			 cellValue = cellValueNull(cell);
    			if (BTSLUtil.isNullString(cellValue)) // User Name
    				// Prefix is
    				// Mandatory
    				// field
    			{   if(!PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES"))) {
                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.unameprefixmissing", null);
                 	MasterErrorList err = new MasterErrorList();
                 	err.setErrorCode("bulkuser.processuploadedfile.error.unameprefixmissing");
                 	err.setErrorMsg(error);
                 	//masterErrorList.add(err);
//    				error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    						"bulkuser.processuploadedfile.error.unameprefixmissing"));
                	ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    				fileValidationErrorExists = true;
    				checkErrors = true;
    				
    				singleRowError.getMasterErrorList().add(err);
    		    	errMap.setRowErrorMsgLists(rowErrors);
    		    	
    				
    			}
    				
    			} else // Validate Prefixes Mr/Miss etc from the List
    			{
    				cellValue = cellValue.trim();
    				listVO = BTSLUtil.getOptionDesc(cellValue, prefixList);
    				if (BTSLUtil.isNullString(listVO.getValue())) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.unameprefixinvalid", null);
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.unameprefixinvalid");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.unameprefixinvalid"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				checkErrors = true;
    					fileValidationErrorExists = true;
    					singleRowError.getMasterErrorList().add(err);
        		    	errMap.setRowErrorMsgLists(rowErrors);
    				
    				}
    			}
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			
    			if (!isFnameLnameAllowed) {
    				++colIt; 
        			cell = r.getCell(colIt);
        			 cellValue = cellValueNull(cell);
    				if (BTSLUtil.isNullString(cellValue)) // Channel
    					// User Name is mandatory
    				{
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.unamemissing", null);
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.unamemissing");
                     	err.setErrorMsg(error);
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				checkErrors = true;
    					fileValidationErrorExists = true;
    					singleRowError.getMasterErrorList().add(err);
        		    	errMap.setRowErrorMsgLists(rowErrors);
    				
    				} else {
    					cellValue = cellValue.trim();
    					// Check User Name length
    					if (cellValue.length() > 80) {
                         	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.unamelengtherr", null);
                         	MasterErrorList err = new MasterErrorList();
                         	err.setErrorCode("bulkuser.processuploadedfile.error.unamelengtherr");
                         	err.setErrorMsg(error);
                         	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.unamelengtherr"));
                        	ArrayList allRowErrorList = new ArrayList();
                         	allRowErrorList.add(String.valueOf(rows));
                         	allRowErrorList.add(error);
        					fileErrorList.add(allRowErrorList);
    						fileValidationErrorExists = true;
    	    				checkErrors = true;
    	    				singleRowError.getMasterErrorList().add(err);
    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    				
    					}
    				}
    				excel[r.getRowNum()+1][colIt]= cellValue;
    			} else {
    				++colIt; 
        			cell = r.getCell(colIt);
        			 cellValue = cellValueNull(cell);
    				if (BTSLUtil.isNullString(cellValue)) // Channel
    					// User First name is mandatory
    				{
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.firstnamemissing", null);
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.firstnamemissing");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.firstnamemissing"));
                    	ArrayList allRowErrorList = new ArrayList();
                     	allRowErrorList.add(String.valueOf(rows));
                     	allRowErrorList.add(error);
                     	
    					fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
        		    	errMap.setRowErrorMsgLists(rowErrors);
    				
    				} else {
    					cellValue = cellValue.trim();
    					// Check User first Name length
    					if (cellValue.length() > 40) {
                         	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.firstnamelengtherr", null);
                         	MasterErrorList err = new MasterErrorList();
                         	err.setErrorCode("bulkuser.processuploadedfile.error.firstnamelengtherr");
                         	err.setErrorMsg(error);
                         	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.firstnamelengtherr"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    						fileValidationErrorExists = true;
    	    				checkErrors = true;
    	    				singleRowError.getMasterErrorList().add(err);
    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    						
    					}
						
						if(!BTSLUtil.isValidName(cellValue)){
	                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.firstnameinvalid", null);
	                     	MasterErrorList err = new MasterErrorList();
                         	err.setErrorCode("bulkuser.processuploadedfile.error.firstnameinvalid");
                         	err.setErrorMsg(error);
                         	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.firstnameinvalid"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    	    				checkErrors = true;
    						fileValidationErrorExists = true;
    						singleRowError.getMasterErrorList().add(err);
    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    						//continue;	
    					}
    				}
    				excel[r.getRowNum()+1][colIt]= cellValue;
    				++colIt; 
        			cell = r.getCell(colIt);
        			 cellValue = cellValueNull(cell);
    				if (!BTSLUtil.isNullString(cellValue)) {
    					cellValue = cellValue.trim();
    					// Check User Last Name length
    					if (cellValue.length() > 40) {
                         	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.lastnamelengtherr", null);
                         	MasterErrorList err = new MasterErrorList();
                         	err.setErrorCode("bulkuser.processuploadedfile.error.lastnamelengtherr");
                         	err.setErrorMsg(error);
                         	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.lastnamelengtherr"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    						fileValidationErrorExists = true;
    	    				checkErrors = true;
    	    				singleRowError.getMasterErrorList().add(err);
    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    						
    					}
						
						if(!BTSLUtil.isValidName(cellValue)){
	                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.lastnameinvalid", null);
	                     	MasterErrorList err = new MasterErrorList();
                         	err.setErrorCode("bulkuser.processuploadedfile.error.lastnameinvalid");
                         	err.setErrorMsg(error);
                         	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.lastnameinvalid"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    						fileValidationErrorExists = true;
    	    				checkErrors = true;
    	    				singleRowError.getMasterErrorList().add(err);
    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    							
    					}

    				}
    				excel[r.getRowNum()+1][colIt]= cellValue;	
    			}
    			if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
    				++colIt; 
        			cell = r.getCell(colIt);
        			 cellValue = cellValueNull(cell);
    				if (BTSLUtil.isNullString(cellValue) && loginPasswordAllowed) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.loginidreqforweb", new String[] { cellValue });
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.loginidreqforweb");
                     	err.setErrorMsg(error);
                     	
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.loginidreqforweb", new String[] { cellValue }));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
        		    	errMap.setRowErrorMsgLists(rowErrors);
    					
    				} else if (!BTSLUtil.isNullString(cellValue)) {
    					cellValue = cellValue.trim();
    					if (cellValue.length() > 20) {
	                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.loginlenerr", null);
	                     	MasterErrorList err = new MasterErrorList();
	                     	err.setErrorCode("bulkuser.processuploadedfile.error.loginlenerr");
	                     	err.setErrorMsg(error);
	                     	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.loginlenerr"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    						fileValidationErrorExists = true;
    	    				checkErrors = true;
    	    				singleRowError.getMasterErrorList().add(err);
    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    					
    					}
    					if (!spaceAllowInLogin && cellValue.contains(" ")) {
	                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.loginspacenotallowed", null);
	                     	MasterErrorList err = new MasterErrorList();
	                     	err.setErrorCode("bulkuser.processuploadedfile.error.loginspacenotallowed");
	                     	err.setErrorMsg(error);
	                     	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.loginspacenotallowed"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    						fileValidationErrorExists = true;
    	    				checkErrors = true;
    	    				singleRowError.getMasterErrorList().add(err);
    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    						
    					}
    				}
    				excel[r.getRowNum()+1][colIt]= cellValue;
    				// **************Web log in ID validation ends
    				// here***********************************

    				// ***********Password related validation starts
    				// here**********************
    				// If Password is blank then system default Password(0000)
    				// will be allocated
    				// done by ashishT , skiping the code in case of hashing
    				// mode.
    				if (!"SHA".equalsIgnoreCase(pinPasswordEnDeCryptionType)) {
    					if (batchUserPasswordModifyAllow)

    					{
    						++colIt; 
                			cell = r.getCell(colIt);
                			 cellValue = cellValueNull(cell);
    						password = cellValue;

    						// EDITED BY HITESH
    						if (BTSLUtil.isNullString(password) && loginPasswordAllowed) {
    							password = c2sDefaultPassword;
    							cellValue = password = password.trim();
    							channelUserOldVO.setPasswordModifyFlag(true);
    						} else if (!BTSLUtil.isNullString(password)) {
    							cellValue = password = password.trim();
    							if (password.equalsIgnoreCase(XLS_PINPASSWARD)) {
    								cellValue = channelUserOldVO.getPassword();
    								channelUserOldVO.setPasswordModifyFlag(false);
    							} else {
    								// /For OCI changes By sanjeew Date 19/07/07
    								error_messageMap = null;
    								error_messageMap = operatorUtili.validatePassword(r.getCell(colIt-1).getStringCellValue().trim(), cellValue);
    								if (!error_messageMap.isEmpty()) {
    									errorMessage = "";
    									passwordErrSetKey = null;
    									itr = null;
    									rowVal = null;
    									passwordErrSetKey = error_messageMap.keySet();
    									itr = passwordErrSetKey.iterator();
    									rowVal = String.valueOf(rows);
    									while (itr.hasNext()) {
    										MasterErrorList err1 = new MasterErrorList();
    										errorMessage = (String) itr.next();
    				                     	String error = RestAPIStringParser.getMessage(locale, errorMessage, (String[]) error_messageMap.get(errorMessage));
    				                     	err1.setErrorCode(errorMessage);
    				                     	err1.setErrorMsg(error);
    				                     	//masterErrorList.add(err1);
    				                     	singleRowError.getMasterErrorList().add(err1);
//    										error = new ListValueVO("", rowVal, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), errorMessage,
//    												(String[]) error_messageMap.get(errorMessage)));
    										ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);    			                     	
    										reptRowNo++;
    									}
    									reptRowNo = reptRowNo - 1;
    									fileValidationErrorExists = true;
    				    				checkErrors = true;
    				    				
    				    		    	errMap.setRowErrorMsgLists(rowErrors);
    									//continue;
    								}
    								channelUserOldVO.setPasswordModifyFlag(true);
    								// End of OCI changes
    							}
    							// check Last 'X' password for user from
    							// pin_password_history table. //by santanu
    							if (channelUserOldVO.isPasswordModifyFlag()) {
    								modificationType = PretupsI.USER_PASSWORD_MANAGEMENT;
    								userID = r.getCell(0).getStringCellValue().trim();
    								password = r.getCell(4).getStringCellValue().trim();
    								// here msidn not required
    								passwordStatus = userDAO.checkPasswordHistory(con, modificationType, userID, userVO.getMsisdn(), BTSLUtil.encryptText(password));
    								if (passwordStatus) {
    			                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.newpwdexist", null);
    			                     	MasterErrorList err = new MasterErrorList();
    			                     	err.setErrorCode("bulkuser.processuploadedfile.error.newpwdexist");
    			                     	err.setErrorMsg(error);
    			                     	//masterErrorList.add(err);
//    									error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    											"bulkuser.processuploadedfile.error.newpwdexist"));
    									ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    				    				checkErrors = true;
    									fileValidationErrorExists = true;
    									singleRowError.getMasterErrorList().add(err);
    		    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    									
    								}
    							} // end pasword check
    						}
    						excel[r.getRowNum()+1][colIt]= cellValue;
    					}
    				}// SysetemPreference check ends here..
    			}
    			if (PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
    				// ************MSISDN related validations starts
    				// here*******************
    				noOfMsisdn = 0;
    				++colIt; 
        			cell = r.getCell(colIt);
        			 cellValue = cellValueNull(cell);
    				if (BTSLUtil.isNullString(cellValue)) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.msisdnreqforsmserr", new String[] { cellValue });
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.msisdnreqforsmserr");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.msisdnreqforsmserr", new String[] { cellValue }));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
	    		    	errMap.setRowErrorMsgLists(rowErrors);
    					
    				} else {
    					final String[] msisdnStringArray = cellValue.trim().split(",");
    					noOfMsisdn = msisdnStringArray.length;
    					if (noOfMsisdn > categoryVO.getMaxTxnMsisdnInt()) {
                         	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.msisdnmorethanallowed", null);
                         	MasterErrorList err = new MasterErrorList();
                         	err.setErrorCode("bulkuser.processuploadedfile.error.msisdnmorethanallowed");
                         	err.setErrorMsg(error);
                         	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.msisdnmorethanallowed"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    						fileValidationErrorExists = true;
    	    				checkErrors = true;
    	    				singleRowError.getMasterErrorList().add(err);
    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    					
    					}
    					final StringBuffer msisdnString = new StringBuffer();
    					// if one msisdn is missmatch then reject whole user

    					final HashMap hm = new HashMap();
    					int val = 0;

    					for (int i = 0; i < noOfMsisdn; i++) {
    						try {

    							filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdnStringArray[i].trim());
    							msisdnString.append(filteredMsisdn);
    							msisdnString.append(",");
    						} catch (Exception ee) {
    							log.errorTrace(methodName, ee);
                             	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.msisdnisinvalid", new String[] { filteredMsisdn });
                             	MasterErrorList err = new MasterErrorList();
                             	err.setErrorCode("bulkuser.processuploadedfile.error.msisdnisinvalid");
                             	err.setErrorMsg(error);
                             	//masterErrorList.add(err);
//    							error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    									"bulkuser.processuploadedfile.error.msisdnisinvalid", new String[] { filteredMsisdn }));
    							ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    							fileValidationErrorExists = true;
    		    				checkErrors = true;
    		    				singleRowError.getMasterErrorList().add(err);
	    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    							adjustCounter++;
    							//continue;
    						}
    						// check duplicates

    						if (hm.containsKey(filteredMsisdn)) {
    							// Key already present... update the value.
    							val = ((Integer) hm.get(filteredMsisdn)).intValue();
    							val++;
    							hm.put(filteredMsisdn, new Integer(val));
    						} else {
    							hm.put(filteredMsisdn, new Integer(1));
    						}

    						if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                             	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.msisdnisinvalid", new String[] { filteredMsisdn });
                             	MasterErrorList err = new MasterErrorList();
                             	err.setErrorCode("bulkuser.processuploadedfile.error.msisdnisinvalid");
                             	err.setErrorMsg(error);
                             	//masterErrorList.add(err);
//    							error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    									"bulkuser.processuploadedfile.error.msisdnisinvalid", new String[] { filteredMsisdn }));
    							ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    							fileValidationErrorExists = true;
    		    				checkErrors = true;
    		    				singleRowError.getMasterErrorList().add(err);
	    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    							adjustCounter++;
    					
    						} 

    						else if (filteredMsisdn.length() > 15) {
                             	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.msisdnlenerr", new String[] { filteredMsisdn });
                             	MasterErrorList err = new MasterErrorList();
                             	err.setErrorCode("bulkuser.processuploadedfile.error.msisdnlenerr");
                             	err.setErrorMsg(error);
                             	//masterErrorList.add(err);
//    							error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    									"bulkuser.processuploadedfile.error.msisdnlenerr", new String[] { filteredMsisdn }));
    							ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    							fileValidationErrorExists = true;
    		    				checkErrors = true;
    		    				singleRowError.getMasterErrorList().add(err);
	    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    							adjustCounter++;
    					
    						}
    						// Check for network prefix
    						msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); 
    						networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
    						if (networkPrefixVO == null) {
                             	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.nonetworkprefixfound", new String[] { filteredMsisdn });
                             	MasterErrorList err = new MasterErrorList();
                             	err.setErrorCode("bulkuser.processuploadedfile.error.nonetworkprefixfound");
                             	err.setErrorMsg(error);
                             	//masterErrorList.add(err);
//    							error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    									"bulkuser.processuploadedfile.error.nonetworkprefixfound", new String[] { filteredMsisdn }));
    							ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    							fileValidationErrorExists = true;
    		    				checkErrors = true;
    		    				singleRowError.getMasterErrorList().add(err);
	    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    							adjustCounter++;
    					
    						} else if (!networkPrefixVO.getNetworkCode().equals(userVO.getNetworkID())) {
                             	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.notsupportingnetwork", new String[] { filteredMsisdn });
                             	MasterErrorList err = new MasterErrorList();
                             	err.setErrorCode("bulkuser.processuploadedfile.error.notsupportingnetwork");
                             	err.setErrorMsg(error);
                             	///masterErrorList.add(err);
//    							error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    									"bulkuser.processuploadedfile.error.notsupportingnetwork", new String[] { filteredMsisdn }));
    							ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    							fileValidationErrorExists = true;
    		    				checkErrors = true;
    		    				singleRowError.getMasterErrorList().add(err);
	    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    							adjustCounter++;
    					
    						}
    					}
    					if (adjustCounter > 0) {
    						reptRowNo = reptRowNo + (adjustCounter - 1);
    						//continue;
    					} else if (hm.size() != noOfMsisdn) {
                         	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.multiplemsisdnduplicate", new String[] { msisdnString.substring(0, msisdnString.length() - 1) });
                         	MasterErrorList err = new MasterErrorList();
                         	err.setErrorCode("bulkuser.processuploadedfile.error.multiplemsisdnduplicate");
                         	err.setErrorMsg(error);
                         	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.multiplemsisdnduplicate", new String[] { msisdnString.substring(0, msisdnString.length() - 1) }));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    						fileValidationErrorExists = true;
    	    				checkErrors = true;
    	    				singleRowError.getMasterErrorList().add(err);
    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    						reptRowNo++;
    						
    					}
    					cellValue = msisdnString.substring(0, msisdnString.length() - 1);
    					excel[r.getRowNum()+1][colIt]= cellValue;
    				}
    				// ************MSISDN related validations ends
    				if (!"SHA".equalsIgnoreCase(pinPasswordEnDeCryptionType)) {
    					++colIt; 
    					cell = r.getCell(colIt);
            			cellValue = cellValueNull(cell);
    					final StringBuffer pinString = new StringBuffer();
    					String[] pinStringArray = null;
    					// set the length of isPinModify flag array equal to number of msisns       					
    					isPinModify = new boolean[noOfMsisdn];
    					boolean noPinStatus = false;
    					pin = cellValue;
    					 
    					if (BTSLUtil.isNullString(pin)) {
    						for (int i = 0; i < noOfMsisdn; i++) {
    							isPinModify[i] = true;
    							pinString.append(c2sDefaultSmsPin);

    							pinString.append(",");
    						}
    						
    						cellValue = pinString.substring(0, pinString.length() - 1);
       						noPinStatus = true;
    					}
    					if (!noPinStatus) {
    						pinStringArray = cellValue.trim().split(",");
    						// set the same number of pins as the number of
    						// msisdns
    						for (int i = 0; i < noOfMsisdn; i++)

    						{
    							// no of pin should be same as no of msisdn, so
    							// if less no of pins are specified, using
    							// default pin
    							try {
    								try {
    									pin = pinStringArray[i].trim();
    								} catch (ArrayIndexOutOfBoundsException e) {
    									LOG.errorTrace(methodName, e);
    									pin = c2sDefaultSmsPin;
    								}

    								if (pin.equalsIgnoreCase(XLS_PINPASSWARD)) {
    									pin = ((UserPhoneVO) channelUserOldVO.getMsisdnList().get(i)).getSmsPin();
    									isPinModify[i] = false;
    								} else {
    									// added by vikram for vfe
    									// checking that pin is not same as web
    									// login id
    									if (operatorUtili.isPinUserId(pin, r.getCell(13).getStringCellValue().trim())) {
    			                         	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.pinsameasloginid", null);
    			                         	MasterErrorList err = new MasterErrorList();
    			                         	err.setErrorCode("bulkuser.processuploadedfile.error.pinsameasloginid");
    			                         	err.setErrorMsg(error);
    			                         	//masterErrorList.add(err);
//    										error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    												"user.addchanneluser.error.pinsameasloginid", null));
    										ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    										fileValidationErrorExists = true;
    					    				checkErrors = true;
    					    				singleRowError.getMasterErrorList().add(err);
    			    	    		    	errMap.setRowErrorMsgLists(rowErrors);
    						
    									}
    									// For OCI changes By sanjeew Date
    									// 19/07/07
    									error_messageMap = null;
    									error_messageMap = operatorUtili.pinValidate(pin);
    									if (!error_messageMap.isEmpty()) {
    										errorMessage = "";
    										passwordErrSetKey = null;
    										itr = null;
    										rowVal = null;
    										passwordErrSetKey = error_messageMap.keySet();
    										itr = passwordErrSetKey.iterator();
    										rowVal = String.valueOf(rows);
    										while (itr.hasNext()) {
        										MasterErrorList err1 = new MasterErrorList();
    											errorMessage = (String) itr.next();
    											final String[] args = (String[]) error_messageMap.get(errorMessage);
    											String[] arg1 = null;
    											if (args == null) {
    												arg1 = new String[1];
    												arg1[0] = rowVal;
    											} else {
    												final int size = args.length;
    												arg1 = new String[size + 2];
    												for (int j = 0; j < size; j++) {
    													arg1[j] = args[j];
    												}
    												arg1[size] = rowVal;
    											}
        			                         	String error = RestAPIStringParser.getMessage(locale, errorMessage, arg1);
        			                         	err1.setErrorCode(errorMessage);
        			                         	err1.setErrorMsg(error);
        			                         	//masterErrorList.add(err1);
        			                         	singleRowError.getMasterErrorList().add(err1);
//    											error = new ListValueVO("", rowVal, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), errorMessage,
//    													arg1));
    											ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    											reptRowNo++;
    										}
    										reptRowNo = reptRowNo - 1;
    					    				checkErrors = true;
    										fileValidationErrorExists = true;
    								    	
    										//continue;
    									}
    									// End of OCI changes
    									isPinModify[i] = true;
    								}

    								// check if Last 'X' pin exist in
    								// pin_password_history table when user
    								// modified his pin.
    								if (!BTSLUtil.isNullString(pin) && isPinModify[i]) {

    									modificationType = PretupsI.USER_PIN_MANAGEMENT;
    									userID = r.getCell(0).getStringCellValue().trim();

    									try {
    										mobileno = ((UserPhoneVO) (channelUserOldVO.getMsisdnList().get(i))).getMsisdn();
    										password = pin.trim();
    										passwordStatus = userDAO.checkPasswordHistory(con, modificationType, userID, mobileno, BTSLUtil.encryptText(password));
    										if (passwordStatus) {
        			                         	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.newpinexist", null);
        			                         	MasterErrorList err = new MasterErrorList();
        			                         	err.setErrorCode("bulkuser.processuploadedfile.error.newpinexist");
        			                         	err.setErrorMsg(error);
        			                         //	masterErrorList.add(err);
//    											error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    													"bulkuser.processuploadedfile.error.newpinexist"));
    											ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    											fileValidationErrorExists = true;
    						    				checkErrors = true;
    						    				singleRowError.getMasterErrorList().add(err);
    											adjustCounter++;
    						
    										}
    									} catch (IndexOutOfBoundsException ie) {
    										LOG.errorTrace(methodName, ie);
    									}
    								}
    							} catch (StringIndexOutOfBoundsException e) {
    								LOG.errorTrace(methodName, e);
    								pin = c2sDefaultSmsPin;
    								isPinModify[i] = true;
    							}
    							pinString.append(pin);
    							pinString.append(",");
    							// *******************PIN releted validation
    							// ends here************************
    						}// check ends here for system preference..
    						if (adjustCounter > 0) {
    							reptRowNo = reptRowNo + (adjustCounter - 1);
    							//continue;
    						}
    						if(!BTSLUtil.isNullString(pinString.toString())){
    						cellValue = pinString.substring(0, pinString.length() - 1);
    						
    						}
    					}
    				pinEntry = cellValue;
    				excel[r.getRowNum()+1][colIt]= cellValue;
    				}
    			}// end
    			// *********************Geography related validation starts
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (BTSLUtil.isNullString(cellValue)) // Geography is
    				// Mandatory
    				// field
    			{
                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.geographymissing", null);
                 	MasterErrorList err = new MasterErrorList();
                 	err.setErrorCode("bulkuser.processuploadedfile.error.geographymissing");
                 	err.setErrorMsg(error);
                 //	masterErrorList.add(err);
//    				error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    						"bulkuser.processuploadedfile.error.geographymissing"));
    				ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    				fileValidationErrorExists = true;
    				checkErrors = true;
    				singleRowError.getMasterErrorList().add(err);
    				
    			} else {
    				cellValue = cellValue.trim();
					grphDomainCode = cellValue;
    				// Geographies will be validated from the master sheet,
    				// check weather the geography
    				// lie under the category
    				if (categoryVO.getMultipleGrphDomains().equals(PretupsI.YES)) {
    					if (cellValue.contains(",")) {
    						final String[] geographyArray = BTSLUtil.removeDuplicatesString(cellValue.split(","));
    						if (geographyArray.length > geoSize) {
    		                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.geographyisinvalid", null);
    		                 	MasterErrorList err = new MasterErrorList();
    		                 	err.setErrorCode("bulkuser.processuploadedfile.error.geographyisinvalid");
    		                 	err.setErrorMsg(error);
    		                 	//masterErrorList.add(err);
//    							error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    									"bulkuser.processuploadedfile.error.geographyisinvalid"));
    							ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    							fileValidationErrorExists = true;
    		    				checkErrors = true;
    		    				singleRowError.getMasterErrorList().add(err);
    				
    						}
    						invaliedgeographyCode = false;
    						int geographyArrays=geographyArray.length;
    						for (int i = 0, j =geographyArrays ; i < j; i++) {
    							if (!geographyCodeList.contains(geographyArray[i])) // Role
    							{
        		                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.geographycodenotexist", null);
        		                 	MasterErrorList err = new MasterErrorList();
        		                 	err.setErrorCode("bulkuser.processuploadedfile.error.geographycodenotexist");
        		                 	err.setErrorMsg(error);
        		                 	//masterErrorList.add(err);
//    								error = new ListValueVO("", String.valueOf(rows), "'" + geographyArray[i] + "' " + this.getResources(request).getMessage(
//    										BTSLUtil.getBTSLLocale(request), "bulkuser.processuploadedfile.error.geographycodenotexist"));
    								ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    								invaliedgeographyCode = true;
    								singleRowError.getMasterErrorList().add(err);
    								break;
    							}
    						}
    						if (invaliedgeographyCode) {
    							fileValidationErrorExists = true;
    		    				checkErrors = true;
    							//continue;
    						}
    					}
    				} else {
    					if (cellValue.contains(",")) {
		                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.multiplegeographynotallow", null);
		                 	MasterErrorList err = new MasterErrorList();
		                 	err.setErrorCode("bulkuser.processuploadedfile.error.multiplegeographynotallow");
		                 	err.setErrorMsg(error);
		                 	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.multiplegeographynotallow"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    						fileValidationErrorExists = true;
    	    				checkErrors = true;
    	    				singleRowError.getMasterErrorList().add(err);
    				
    					}
    					for (int i = 0; i < geoSize; i++) {
    						userGeographiesVO = (UserGeographiesVO) geographyList.get(i);
    						if (cellValue.equals(userGeographiesVO.getGraphDomainCode())) {
    							found = true;
    							break;
    						}
    					}
    					if (!found) {
		                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.geographyisinvalid", null);
		                 	MasterErrorList err = new MasterErrorList();
		                 	err.setErrorCode("bulkuser.processuploadedfile.error.geographyisinvalid");
		                 	err.setErrorMsg(error);
		                 	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.geographyisinvalid"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    	    				checkErrors = true;
    						fileValidationErrorExists = true;
    						singleRowError.getMasterErrorList().add(err);
    						
    					}
    				}
    			}
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			// *********************Geography related validation ends
    			// here**********

    			// *************Group Role code and Role code related validation
    			// Starts here*****************************
    			if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
    				++colIt; 
					cell = r.getCell(colIt);
        			cellValue = cellValueNull(cell);
    				// edited by hitesh
    				if (loginPasswordAllowed && BTSLUtil.isNullString(cellValue)) 
    				{
	                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.rolecodedetailsmissing", null);
	                 	MasterErrorList err = new MasterErrorList();
	                 	err.setErrorCode("bulkuser.processuploadedfile.error.rolecodedetailsmissing");
	                 	err.setErrorMsg(error);
	                 	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.rolecodedetailsmissing"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    					
    				} else {
    					cellValue = cellValue.trim();
    					if (cellValue.equals(PretupsI.YES)) // Role
    					{
    						excel[r.getRowNum()+1][colIt]= cellValue;
    						++colIt; 
    						cell = r.getCell(colIt);
    	        			cellValue = cellValueNull(cell);
    						if (BTSLUtil.isNullString(cellValue) && loginPasswordAllowed) // Role
    						{
    		                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.grouprolecodemissing", null);
    		                 	MasterErrorList err = new MasterErrorList();
    		                 	err.setErrorCode("bulkuser.processuploadedfile.error.grouprolecodemissing");
    		                 	err.setErrorMsg(error);
    		                 	//masterErrorList.add(err);
//    							error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    									"bulkuser.processuploadedfile.error.grouprolecodemissing"));
    							ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    							fileValidationErrorExists = true;
    		    				checkErrors = true;
    		    				singleRowError.getMasterErrorList().add(err);
    					
    						} else if (!BTSLUtil.isNullString(cellValue) && !groupRoleCodeList.contains(cellValue.trim())) // Role
    						{
    		                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.grouprolecodenotexist", null);
    		                 	MasterErrorList err = new MasterErrorList();
    		                 	err.setErrorCode("bulkuser.processuploadedfile.error.grouprolecodenotexist");
    		                 	err.setErrorMsg(error);
    		                 	//masterErrorList.add(err);
//    							error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    									"bulkuser.processuploadedfile.error.grouprolecodenotexist"));
    							ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    							fileValidationErrorExists = true;
    		    				checkErrors = true;
    		    				singleRowError.getMasterErrorList().add(err);
    					
    						}
    						int login_id = 0;
    						if (isFnameLnameAllowed) {
    							login_id = 4;

    						} else {
    							login_id = 3;
    						}
    						if (!BTSLUtil.isNullString(cellValue) && BTSLUtil.isNullString(r.getCell(login_id).getStringCellValue().trim())) {
    		                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.rolesnotassignedwithoutloginid", null);
    		                 	MasterErrorList err = new MasterErrorList();
    		                 	err.setErrorCode("bulkuser.processuploadedfile.error.rolesnotassignedwithoutloginid");
    		                 	err.setErrorMsg(error);
    		                 	//masterErrorList.add(err);
//    							error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    									"user.adduser.error.rolesnotassignedwithoutloginid"));
    							ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    							fileValidationErrorExists = true;
    		    				checkErrors = true;
    		    				singleRowError.getMasterErrorList().add(err);
    					
    						}
    						excel[r.getRowNum()+1][colIt]= cellValue;
    					} else if (cellValue.equals(PretupsI.NO)) // Role
    						// code
    						// flag
    						// is
    						// 'N'
    					{
    						excel[r.getRowNum()+1][colIt]= cellValue;
    						++colIt; 
    						cell = r.getCell(colIt);
    	        			cellValue = cellValueNull(cell);
    						// edited by hitesh
    						if (BTSLUtil.isNullString(cellValue) && loginPasswordAllowed) // Role
    							// code
    							// is
    							// mandateroy
    							// field
    						{
    		                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.rolecodemissing", null);
    		                 	MasterErrorList err = new MasterErrorList();
    		                 	err.setErrorCode("bulkuser.processuploadedfile.error.rolecodemissing");
    		                 	err.setErrorMsg(error);
    		                 	//masterErrorList.add(err);
//    							error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    									"bulkuser.processuploadedfile.error.rolecodemissing"));
    							ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    							fileValidationErrorExists = true;
    		    				checkErrors = true;
    		    				singleRowError.getMasterErrorList().add(err);
    					
    						} else if (!BTSLUtil.isNullString(cellValue)) {
    							cellValue = cellValue.trim();
    							String[] rolecodeArray = null;
    							if (cellValue.contains(",")) {
    								rolecodeArray = BTSLUtil.removeDuplicatesString(cellValue.split(","));
    							} else {
    								rolecodeArray = new String[1];
    								rolecodeArray[0] = cellValue;
    							}
    							if (rolecodeArray.length > roleCodeList.size()) {
        		                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.rolecodeinvalied", null);
        		                 	MasterErrorList err = new MasterErrorList();
        		                 	err.setErrorCode("bulkuser.processuploadedfile.error.rolecodeinvalied");
        		                 	err.setErrorMsg(error);
        		                 	//masterErrorList.add(err);
//    								error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    										"bulkuser.processuploadedfile.error.rolecodeinvalied"));
    								ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    			    				checkErrors = true;
    								fileValidationErrorExists = true;
    								singleRowError.getMasterErrorList().add(err);
    					
    							}
    							invaliedRoleCode = false;
    							for (int i = 0, j = rolecodeArray.length; i < j; i++) {
    								if (!roleCodeList.contains(rolecodeArray[i])) // Role
    								{
    									String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.rolecodenotexist", null);
    									MasterErrorList err = new MasterErrorList();
            		                 	err.setErrorCode("bulkuser.processuploadedfile.error.rolecodenotexist");
            		                 	err.setErrorMsg(error);
            		                 	singleRowError.getMasterErrorList().add(err);
//    									error = new ListValueVO("", String.valueOf(rows), "'" + rolecodeArray[i] + "' " + this.getResources(request).getMessage(
//    											BTSLUtil.getBTSLLocale(request), "bulkuser.processuploadedfile.error.rolecodenotexist"));
    									ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    									invaliedRoleCode = true;
    									break;
    								}
    							}
    							if (invaliedRoleCode) {    								
    			    				checkErrors = true;
    								fileValidationErrorExists = true;
    					
    							}
    						}
    						excel[r.getRowNum()+1][colIt]= cellValue;
    						
    					} else {
		                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.rolecodedetailsnotexist", null);
		                 	MasterErrorList err = new MasterErrorList();
		                 	err.setErrorCode("bulkuser.processuploadedfile.error.rolecodedetailsnotexist");
		                 	err.setErrorMsg(error);
		                 	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.rolecodedetailsnotexist"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    	    				checkErrors = true;
    						fileValidationErrorExists = true;
    						singleRowError.getMasterErrorList().add(err);
    					
    					}

    				}

    			}
    			// *************Group Role code and Role code validation Ends
    			// here*****************************

    			// *************Service related validation Starts
    			// here*****************************

    			if (PretupsI.YES.equals(categoryVO.getServiceAllowed())) {
    				++colIt; 
					cell = r.getCell(colIt);
        			cellValue = cellValueNull(cell);
    				// ******************Services validation
    				// starts*********************
    				if (BTSLUtil.isNullString(cellValue)) 
    				{
	                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.servicesmissing", null);
	                 	MasterErrorList err = new MasterErrorList();
	                 	err.setErrorCode("bulkuser.processuploadedfile.error.servicesmissing");
	                 	err.setErrorMsg(error);
	                 	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.servicesmissing"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				checkErrors = true;
    					fileValidationErrorExists = true;
    					singleRowError.getMasterErrorList().add(err);
    					
    				} else {
    					cellValue = cellValue.trim();
    					// Check the valid length of comma seperated services
    					// Itratae the comma seperated services for storing in
    					// arraylist
    					if (cellValue.contains(",")) {
    						serviceArr = BTSLUtil.removeDuplicatesString(cellValue.split(","));
    					} else {
    						serviceArr = new String[1];
    						serviceArr[0] = cellValue;
    					}
    					// If there will be no comma serviceArr.length will be 1
    					serviceLen = serviceArr.length;

    					if (serviceLen > serviceList.size()) {
    						// Error: The services specified in the XLS file
    						// will be greater than the
    						// services applicable
    	                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.serviceisinvalid", null);
    	                 	MasterErrorList err = new MasterErrorList();
    	                 	err.setErrorCode("bulkuser.processuploadedfile.error.serviceisinvalid");
    	                 	err.setErrorMsg(error);
    	                 	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.serviceisinvalid"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    	    				checkErrors = true;
    						fileValidationErrorExists = true;
    						singleRowError.getMasterErrorList().add(err);
    					
    					}
    					if (serviceArr != null && serviceLen > 0) {
    						newServiceList = new ArrayList();
    						invailedService = false;
    						for (int i = 0; i < serviceLen; i++) {
    							serviceArr[i] = serviceArr[i].toUpperCase().trim();
    							if (!BTSLUtil.isNullString(BTSLUtil.getOptionDesc(serviceArr[i], serviceList).getLabel())) {
    								newServiceList.add(serviceArr[i]);
    							} else {
    	    	                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.serviceisinvalid", null);
    	    	                 	MasterErrorList err = new MasterErrorList();
    	    	                 	err.setErrorCode("bulkuser.processuploadedfile.error.serviceisinvalid");
    	    	                 	err.setErrorMsg(error);
    	    	                 	singleRowError.getMasterErrorList().add(err);
    	    	                 	//masterErrorList.add(err);
//    								error = new ListValueVO("", String.valueOf(rows), "'" + serviceArr[i] + "' " + this.getResources(request).getMessage(
//    										BTSLUtil.getBTSLLocale(request), "bulkuser.processuploadedfile.error.serviceisinvalid"));
    								ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    								invailedService = true;
    								break;
    							}
    						}
    						if (invailedService) {
    		    				checkErrors = true;
    							fileValidationErrorExists = true;
//    							RowErrorMsgLists singleRowError = new RowErrorMsgLists();
//    	                     	singleRowError.setRowValue(String.valueOf(rows));
//    	                     	singleRowError.setMasterErrorList(masterErrorList);
//    	                     	rowErrors.add(singleRowError);
//    	        		    	errMap.setRowErrorMsgLists(rowErrors);
    					
    						}
    					}
    					
						
    				}
    				// ******************Services validation
    				// ends*********************
    			}
    			// *************Service related validation ends
    			// here*****************************

    			// *************User short name validation starts
    			// here*****************************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (cellValue.length() > 15) {
	                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.shortnameinvalid", null);
	                 	MasterErrorList err = new MasterErrorList();
	                 	err.setErrorCode("bulkuser.processuploadedfile.error.shortnameinvalid");
	                 	err.setErrorMsg(error);
	                 	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.shortnameinvalid"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				checkErrors = true;
    					fileValidationErrorExists = true;
    					singleRowError.getMasterErrorList().add(err);
    					
    				}
    			}
    			// **************User short name validation ends
    			// here*****************************

    			// *****************Subscriber code validation starts
    			// here**************************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (cellValue.length() > 12) {
	                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.subscriberlenerr", null);
	                 	MasterErrorList err = new MasterErrorList();
	                 	err.setErrorCode("bulkuser.processuploadedfile.error.subscriberlenerr");
	                 	err.setErrorMsg(error);
	                 	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.subscriberlenerr"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    					
    				}
    			}
    			// *****************Subscriber code validation ends
    			// here**************************

    			// *****************External code validation starts
    			// here****************************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (cellValue.length() > 20) {
	                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.extercodelenerr", null);
	                 	MasterErrorList err = new MasterErrorList();
	                 	err.setErrorCode("bulkuser.processuploadedfile.error.extercodelenerr");
	                 	err.setErrorMsg(error);
	                 	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.extercodelenerr"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    					
    				}
    			} else if (externalCodeMandatoryForUser) {
                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.extcodemissing", null);
                 	MasterErrorList err = new MasterErrorList();
                 	err.setErrorCode("bulkuser.processuploadedfile.error.extcodemissing");
                 	err.setErrorMsg(error);
                 	//masterErrorList.add(err);
//    				error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    						"bulkuser.processuploadedfile.error.extcodemissing"));
    				ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    				fileValidationErrorExists = true;
    				checkErrors = true;
    				singleRowError.getMasterErrorList().add(err);
    				
    				
    			}
    			String extCode = cellValue;
    			// *****************External code validation ends
    			// here****************************

    			// *****************In Suspend(Y/N) validation starts
    			// here**************************
    			
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (!(cellValue.equals(PretupsI.YES) || cellValue.equals(PretupsI.NO))) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.insuspendinvalied", null);
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.insuspendinvalied");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.insuspendinvalied"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    				
    				}
    			}
    			// *****************In Suspend(Y/N) validation ends
    			// here**************************

    			// *****************Out Suspend(Y/N) validation starts
    			// here**************************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (!(cellValue.equals(PretupsI.YES) || cellValue.equals(PretupsI.NO))) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.outsuspendinvalied", null);
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.outsuspendinvalied");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.outsuspendinvalied"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				checkErrors = true;
    					fileValidationErrorExists = true;
    					singleRowError.getMasterErrorList().add(err);
    				
    				}
    			}
    			// *****************Out Suspend(Y/N) validation ends
    			// here**************************

    			// **************Contact persion validation starts
    			// here***************************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (cellValue.length() > 80) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.contactpesronlenerr", null);
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.contactpesronlenerr");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.contactpesronlenerr"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    				
    				}
    			 
    				
    				regExMsg =commutil.validateRegexWithMessage(Constants.getProperty(PretupsI.ALPHABET_WITH_SPACE), cellValue, "Contact person", "Alphabets and space ");
    				if(!BTSLUtil.isNullString(regExMsg)) {
    					String error = regExMsg;
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode(PretupsI.REGEX_ERROR_CODE);
                     	err.setErrorMsg(error);
                    	ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    				}
    				
    				
    				
    				
    				
    				
    				
    			}
    			// **************Contact persion validation ends
    			// here***************************

    			// **************Contact Number validation starts
    			// here*************************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (cellValue.length() > 50) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.contactnumberlenerr", null);
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.contactnumberlenerr");
                     	err.setErrorMsg(error);
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    				}
    				
    				regExMsg =commutil.validateRegexWithMessage(Constants.getProperty(PretupsI.REGEX_NUMERIC_ONLY), cellValue, PretupsI.CONTACT_NUMBER_LABEL, PretupsI.NUMERIC_LABEL);
    				if(!BTSLUtil.isNullString(regExMsg)) {
    					String error = regExMsg;
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode(PretupsI.REGEX_ERROR_CODE);
                     	err.setErrorMsg(error);
                    	ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    				}
    				
    				
    				
    				
    			}
    			// **************Contact Number validation ends
    			// here***************************
    			// **************SSN validation starts
    			// here***************************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (cellValue.length() > 15) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.ssnlenerr", null);
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.ssnlenerr");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.ssnlenerr"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				checkErrors = true;
    					fileValidationErrorExists = true;
    					singleRowError.getMasterErrorList().add(err);
    				
    				}
    			}
    			
    			// **************SSN validation ends
    			// here***************************

    			// **************Designation validation starts
    			// here***************************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (cellValue.length() > 30) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.designationlenerr", null);
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.designationlenerr");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.designationlenerr"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				checkErrors = true;
    					fileValidationErrorExists = true;
    					singleRowError.getMasterErrorList().add(err);
    				
    				}
    			}
    			// **************Designation validation ends
    			// here***************************

    			// **************Address1 validation starts
    			// here***************************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (cellValue.length() > 50) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.add1lenerr", null);
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.add1lenerr");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.add1lenerr"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				checkErrors = true;
    					fileValidationErrorExists = true;
    					singleRowError.getMasterErrorList().add(err);
    				
    				}
    			}
    			// **************Address1 validation ends
    			// here***************************

    			// **************Address2 validation starts
    			// here***************************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (cellValue.length() > 50) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.add2lenerr", null);
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.add2lenerr");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.add2lenerr"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				checkErrors = true;
    					fileValidationErrorExists = true;
    					singleRowError.getMasterErrorList().add(err);
    				
    				}
    			}
    			// **************Address2 validation ends
    			// here***************************

    			// **************City validation starts
    			// here***************************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (cellValue.length() > 30) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.citylenerr", null);
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.citylenerr");
                     	err.setErrorMsg(error);
                     	///masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.citylenerr"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				checkErrors = true;
    					fileValidationErrorExists = true;
    					singleRowError.getMasterErrorList().add(err);
    				
    				}
    			}
    			// **************City validation ends
    			// here***************************

    			// **************State validation starts
    			// here***************************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (cellValue.length() > 30) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.statelenerr", null);
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.statelenerr");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.statelenerr"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    				
    				}
    			}
    			// **************State validation ends
    			// here***************************

    			// *************Country validation starts
    			// here**********************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (cellValue.length() > 20) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.countrylenerr", null);
                     	MasterErrorList err = new MasterErrorList();
                     	err.setErrorCode("bulkuser.processuploadedfile.error.countrylenerr");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.countrylenerr"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				checkErrors = true;
    					fileValidationErrorExists = true;
    					singleRowError.getMasterErrorList().add(err);
    				
    				}
    			}
    			// *************Country validation ends
    			// *************Company validation starts
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (cellValue.length() > 80) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.companynameinvalid", null);
                     	MasterErrorList err = new MasterErrorList();
                    	err.setErrorCode("bulkuser.processuploadedfile.error.companynameinvalid");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.companynameinvalid"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    				
    				}
    			}

    			// *************Company validation ends
    			// *************fax validation starts here**********************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (cellValue.length() > 20) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.faxinvalid", null);
                     	MasterErrorList err = new MasterErrorList();
                    	err.setErrorCode("bulkuser.processuploadedfile.error.faxinvalid");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.faxinvalid"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    				
    				}
    			}

    			// *************fax validation ends here**********************
    			// **************Language validation starts
    			// here************************
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (BTSLUtil.isNullString(cellValue)){
    				if(!PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES"))) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.languagemissing", null);
                     	MasterErrorList err = new MasterErrorList();
                    	err.setErrorCode("bulkuser.processuploadedfile.error.languagemissing");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//        						"bulkuser.processuploadedfile.error.languagemissing"));
        				ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
        			
    				}
    				
    			} else {
    				cellValue = cellValue.trim();
    				if (!languageMap.containsKey(cellValue)) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.languageinvalid", null);
                     	MasterErrorList err = new MasterErrorList();
                    	err.setErrorCode("bulkuser.processuploadedfile.error.languageinvalid");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.languageinvalid"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    				
    				}
    			}

    			// ***************Language validation end
    			// *************E-mail validation starts
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell);
    			if (!BTSLUtil.isNullString(cellValue)) {
    				cellValue = cellValue.trim();
    				if (cellValue.length() > 60) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.emaillenerr", null);
                     	MasterErrorList err = new MasterErrorList();
                    	err.setErrorCode("bulkuser.processuploadedfile.error.emaillenerr");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.emaillenerr"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    				
    				} else if (!BTSLUtil.validateEmailID(cellValue)) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.invalidemail", null);
                     	MasterErrorList err = new MasterErrorList();
                    	err.setErrorCode("bulkuser.processuploadedfile.error.invalidemail");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.invalidemail"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    				
    				}
    			}
    			// *************E-mail validation ends
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			
    			if (categoryVO.getOutletsAllowed().equals(PretupsI.YES)){
    				// **********************Outlet code validation starts
    				++colIt; 
					cell = r.getCell(colIt);
        			cellValue = cellValueNull(cell);
    				if (!BTSLUtil.isNullString(cellValue)) {
    					// Check the MAX length
    					cellValue = cellValue.trim();
    					if (cellValue.length() > 10) {
                         	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.outletleninvalid", null);
                         	MasterErrorList err = new MasterErrorList();
                        	err.setErrorCode("bulkuser.processuploadedfile.error.outletleninvalid");
                         	err.setErrorMsg(error);
                         	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.outletleninvalid"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    						fileValidationErrorExists = true;
    	    				checkErrors = true;
    	    				singleRowError.getMasterErrorList().add(err);
    				
    					}

    					// Validate the Outlets from the Master Sheet
    					listVO = BTSLUtil.getOptionDesc(cellValue, outletList);
    					if (BTSLUtil.isNullString(listVO.getValue())) {
                         	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.outletisinvalid", null);
                         	MasterErrorList err = new MasterErrorList();
                        	err.setErrorCode("bulkuser.processuploadedfile.error.outletisinvalid");
                         	err.setErrorMsg(error);
                         	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.outletisinvalid"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    	    				checkErrors = true;
    						fileValidationErrorExists = true;
    						singleRowError.getMasterErrorList().add(err);
            		    	errMap.setRowErrorMsgLists(rowErrors);
    				
    					}
    				} else {
    					// Insert the Default outlet TCOM
    					cellValue = PretupsI.OUTLET_TYPE_DEFAULT;
    				}
    				// **********************Outlet code validation ends
    				// here***************************************

    				// **********************Suboutlet code validation starts
    				// here***************************************
    				excel[r.getRowNum()+1][colIt]= cellValue;
    				++colIt; 
					cell = r.getCell(colIt);
        			cellValue = cellValueNull(cell);
    				// Check the MAX length
    				boolean flag = false;
    				if (BTSLUtil.isNullString(cellValue)) {
    					for (int k = 0, l = subOutletList.size(); k < l; k++) {
    						flag = false;
    						listVO = (ListValueVO) subOutletList.get(k);
    						final String sub[] = listVO.getValue().split(":");

    						if (excel[r.getRowNum()+1][colIt-1].trim().equals(sub[1])) {
    							flag = true;
    							cellValue = sub[0];
    							break;
    						}
    					}
    				} else if (cellValue.trim().length() > 10) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.suboutletleninvalid", null);
                     	MasterErrorList err = new MasterErrorList();
                    	err.setErrorCode("bulkuser.processuploadedfile.error.suboutletleninvalid");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.suboutletleninvalid"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    				
    				} else {
    					cellValue = cellValue.trim();
    					for (int k = 0, l = subOutletList.size(); k < l; k++) {
    						flag = false;
    						listVO = (ListValueVO) subOutletList.get(k);
    						final String sub[] = listVO.getValue().split(":");
    						if (excel[r.getRowNum()+1][colIt-1].trim().equals(sub[1])) {
    							if (cellValue.equals(sub[0])) {
    								flag = true;
    								cellValue = sub[0];
    								break;
    							}
    						}
    					}
    				}
    				// Validate the subOutlets from the Master Sheet
    				if (!flag) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.suboutletisinvalid", null);
                     	MasterErrorList err = new MasterErrorList();
                    	err.setErrorCode("bulkuser.processuploadedfile.error.suboutletisinvalid");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.suboutletisinvalid"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				checkErrors = true;
    					fileValidationErrorExists = true;
    					singleRowError.getMasterErrorList().add(err);
    				
    				}
    				// **********************Suboutlet code validation ends
    				// here***************************************

    			}

    			// For Low alert Balance date 19/07/07 By sanjeew
    			// ***************************For Low balance alert
    			// validation*Start*************************
    			excel[r.getRowNum()+1][colIt]= cellValue;
				
    			if (categoryVO.getLowBalAlertAllow().equalsIgnoreCase(PretupsI.YES)) {
    				++colIt; 
					cell = r.getCell(colIt);
        			cellValue = cellValueNull(cell);
    				if (BTSLUtil.isNullString(cellValue)) {
    					cellValue = PretupsI.NO;
    				} else {
    					cellValue = (cellValue.trim()).toUpperCase();
    					if (!(cellValue.equalsIgnoreCase(PretupsI.YES) || cellValue.equalsIgnoreCase(PretupsI.NO))) {
                         	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.invaliedlowalertbalance", null);
                         	MasterErrorList err = new MasterErrorList();
                        	err.setErrorCode("bulkuser.processuploadedfile.error.invaliedlowalertbalance");
                         	err.setErrorMsg(error);
                         	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.invaliedlowalertbalance"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    	    				checkErrors = true;
    						fileValidationErrorExists = true;
    						singleRowError.getMasterErrorList().add(err);
    				
    					}
    				}
    			}

    			// ***************************For Low balance alert
    			// validation*End*************************
    			// End of low alert Balance
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			// ***************************For Transfer Rule Type at User
    			// level Start*************************
    			final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, userVO.getNetworkID(), (categoryVO.getCategoryCode()))).booleanValue();// Here
    			// excelArr[r][5]=category_code
    			if (isTrfRuleTypeAllow&&!BTSLUtil.isNullString(channelUserOldVO.getTrannferRuleTypeId())) {
    				++colIt; 
					cell = r.getCell(colIt);
        			cellValue = cellValueNull(cell);
    				if (BTSLUtil.isNullString(cellValue)) // Transfer
    					// Rule Code
    					// is
    					// Mandatory
    					// field
    				{
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.trfruletypecode", null);
                     	MasterErrorList err = new MasterErrorList();
                    	err.setErrorCode("bulkuser.processuploadedfile.error.trfruletypecode");
                     	err.setErrorMsg(error);
                 //    	masterErrorList.add(err);
//    					error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    							"bulkuser.processuploadedfile.error.trfruletypecode"));
    					ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    					fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
    				
    				} else {
    					cellValue = cellValue.trim();
    					listVO = BTSLUtil.getOptionDesc(cellValue, trfRuleTypeList);
    					if (BTSLUtil.isNullString(listVO.getValue())) {
                         	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.trfruletypecodeinvalid", null);
                         	MasterErrorList err = new MasterErrorList();
                        	err.setErrorCode("bulkuser.processuploadedfile.error.trfruletypecodeinvalid");
                         	err.setErrorMsg(error);
                         	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.trfruletypecodeinvalid"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    						fileValidationErrorExists = true;
    	    				checkErrors = true;
    	    				singleRowError.getMasterErrorList().add(err);
    				
    					}
    				}
    				excel[r.getRowNum()+1][colIt]= cellValue;
    			}else if(isTrfRuleTypeAllow&&BTSLUtil.isNullString(channelUserOldVO.getTrannferRuleTypeId())){
    				++colIt;
    			}
    			
    			if (rsaAuthenticationRequired) {
    				boolean rsaRequired = false;
    				int ssnColVal = 0;
    				try {
    					rsaRequired = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.RSA_AUTHENTICATION_REQUIRED, userVO.getNetworkID(), (categoryVO.getCategoryCode()))).booleanValue();
    				} catch (Exception e) {
    					LOG.errorTrace(methodName, e);
    				}
    				if (rsaRequired) {
    					++colIt; 
    					cell = r.getCell(colIt);
            			cellValue = cellValueNull(cell);
    					if (isTrfRuleTypeAllow) {
    						ssnColVal = colIt - 15;
    					} else {
    						ssnColVal = colIt - 14;
    					}
    					if (PretupsI.YES.equals(categoryVO.getWebInterfaceAllowed())) {
    						if (PretupsI.YES.equalsIgnoreCase(cellValue)) {
    							if (BTSLUtil.isNullString(r.getCell(ssnColVal).getStringCellValue().trim())) {
    	                         	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.ssnnullerr", null);
    	                         	MasterErrorList err = new MasterErrorList();
    	                        	err.setErrorCode("bulkuser.processuploadedfile.error.ssnnullerr");
    	                         	err.setErrorMsg(error);
    	                      //   	masterErrorList.add(err);
//    								error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    										"bulkuser.processuploadedfile.error.ssnnullerr"));
    								ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    			    				checkErrors = true;
    								fileValidationErrorExists = true;
    								singleRowError.getMasterErrorList().add(err);
    				
    							}
    						} else if ((PretupsI.NO.equalsIgnoreCase(cellValue) || BTSLUtil.isNullString(cellValue)) && r.getCell(ssnColVal) != null && !BTSLUtil.isNullString(r.getCell(ssnColVal).getStringCellValue())) {
	                         	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.ssnnotnullerr", null);
	                         	MasterErrorList err = new MasterErrorList();
	                        	err.setErrorCode("bulkuser.processuploadedfile.error.ssnnotnullerr");
	                         	err.setErrorMsg(error);
	                         	//masterErrorList.add(err);
//    							error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    									"bulkuser.processuploadedfile.error.ssnnotnullerr"));
    							ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    		    				checkErrors = true;
    							fileValidationErrorExists = true;
    							singleRowError.getMasterErrorList().add(err);
    				
    						}
    					} else {
    						if ((!BTSLUtil.isNullString(cellValue) && !cellValue.equals(PretupsI.NO)) || !BTSLUtil.isNullString(r.getCell(ssnColVal).getStringCellValue().trim())) {
	                         	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.rsanotallowederr", null);
	                         	MasterErrorList err = new MasterErrorList();
	                        	err.setErrorCode("bulkuser.processuploadedfile.error.rsanotallowederr");
	                         	err.setErrorMsg(error);
	                         	//masterErrorList.add(err);
//    							    	error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    									"bulkuser.processuploadedfile.error.rsanotallowederr"));
    							ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    		    				checkErrors = true;
    							fileValidationErrorExists = true;
    							singleRowError.getMasterErrorList().add(err);
    				
    						}
    					}
    					excel[r.getRowNum()+1][colIt]= cellValue;		
    				} else {
    					if ((!BTSLUtil.isNullString(cellValue) && !cellValue.equals(PretupsI.NO)) || !BTSLUtil.isNullString(r.getCell(ssnColVal).getStringCellValue().trim())) {
                         	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.rsanotallowederr", null);
                         	MasterErrorList err = new MasterErrorList();
                        	err.setErrorCode("bulkuser.processuploadedfile.error.rsanotallowederr");
                         	err.setErrorMsg(error);
                         	//masterErrorList.add(err);
//    								error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.rsanotallowederr"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    	    				checkErrors = true;
    						fileValidationErrorExists = true;
    						singleRowError.getMasterErrorList().add(err);
    				
    					}
    				}
    				
    			}
    			if (authTypeReq) {
    				++colIt; 
					cell = r.getCell(colIt);
        			cellValue = cellValueNull(cell);
        			excel[r.getRowNum()+1][colIt]= cellValue;
        			// OTP/LDAP allowed
    			}
    			
    	
    			// **************RSA validation ends
    			// here***************************/
    			// LMS Profile validation starts here
    			
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell); // Longitude
    			if (!BTSLUtil.isNullString(cellValue)) {
                    cellValue = cellValue.trim();
                    if (cellValue.length() > 15) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.longitudelengthexceed", null);
                     	MasterErrorList err = new MasterErrorList();
                    	err.setErrorCode("bulkuser.processuploadedfile.error.longitudelengthexceed");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//                        error = new ListValueVO("", String.valueOf(r.getRowNum()+1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                            "bulkuser.processuploadedfile.error.longitudelengthexceed"));
                        ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
                        fileValidationErrorExists = true;
        				checkErrors = true;
        				singleRowError.getMasterErrorList().add(err);
                    
                    }
                } 
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell); // Latitude
    			if (!BTSLUtil.isNullString(cellValue)) {
                    cellValue = cellValue.trim();
                    if (cellValue.length() > 15) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.latitudelengthexceed", null);
                     	MasterErrorList err = new MasterErrorList();
                    	err.setErrorCode("bulkuser.processuploadedfile.error.latitudelengthexceed");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//                        error = new ListValueVO("", String.valueOf(r.getRowNum()+1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                            "bulkuser.processuploadedfile.error.latitudelengthexceed"));
                        ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				checkErrors = true;
                        fileValidationErrorExists = true;
                        singleRowError.getMasterErrorList().add(err);
                    
                    }
                } 
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell); // DocumentType
    			boolean docTypeExist = false;
    			if (!BTSLUtil.isNullString(cellValue)) {
                    cellValue = cellValue.trim();
                    listVO = BTSLUtil.getOptionDesc(cellValue, documentTypeList);
                    if (BTSLUtil.isNullString(listVO.getValue())) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.documenttypeinvalid", null);
                     	MasterErrorList err = new MasterErrorList();
                    	err.setErrorCode("bulkuser.processuploadedfile.error.documenttypeinvalid");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//                        error = new ListValueVO("", String.valueOf(r.getRowNum()+1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                            "bulkuser.processuploadedfile.error.documenttypeinvalid"));
                        ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				checkErrors = true;
                        fileValidationErrorExists = true;
                        singleRowError.getMasterErrorList().add(err);
                    
                    }else{
                    	docTypeExist = true;
                    }
                }
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell); // DocumentNo
    			boolean docNoExist = false;
    			if (!BTSLUtil.isNullString(cellValue)) {
                    cellValue = cellValue.trim();
                    if (cellValue.length() > 20) {
                     	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.documentnoinvalidlength", null);
                     	MasterErrorList err = new MasterErrorList();
                    	err.setErrorCode("bulkuser.processuploadedfile.error.documentnoinvalidlength");
                     	err.setErrorMsg(error);
                     	//masterErrorList.add(err);
//                        error = new ListValueVO("", String.valueOf(r.getRowNum()+1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                            "bulkuser.processuploadedfile.error.documentnoinvalidlength"));
                        ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
        				checkErrors = true;
                        fileValidationErrorExists = true;
                        singleRowError.getMasterErrorList().add(err);
                    
                    }else{
                    	docNoExist = true;
                    }
                } 
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			if ((docTypeExist && !docNoExist) || (!docTypeExist && docNoExist)) {
                 	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.doctypedocno.eitherbothmandatoryoroptional", null);
                 	MasterErrorList err = new MasterErrorList();
                	err.setErrorCode("bulkuser.processuploadedfile.error.doctypedocno.eitherbothmandatoryoroptional");
                 	err.setErrorMsg(error);
                 	//masterErrorList.add(err);
//    				error = new ListValueVO("", String.valueOf(r.getRowNum()+1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    						"bulkuser.processuploadedfile.error.doctypedocno.eitherbothmandatoryoroptional"));
    				ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    				checkErrors = true;
    				fileValidationErrorExists = true;
    				singleRowError.getMasterErrorList().add(err);
    				
    			} 
    			++colIt; 
				cell = r.getCell(colIt);
    			cellValue = cellValueNull(cell); // PaymentType
    			
    			String paymentTypeArr[] = null;
    			
    			if (!BTSLUtil.isNullString(cellValue)) {
                    cellValue = cellValue.trim();
                    
                    if (cellValue.contains(",")) {
						paymentTypeArr = BTSLUtil.removeDuplicatesString(cellValue.split(","));
					} else {
						paymentTypeArr = new String[1];
						paymentTypeArr[0] = cellValue;
					}
                    for (int i = 0; i < paymentTypeArr.length; i++) {							 
						 listVO = BTSLUtil.getOptionDesc(paymentTypeArr[i].toUpperCase().trim(), paymentTypeList);
                       if (BTSLUtil.isNullString(listVO.getValue())) {
                        	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.paymenttypeinvalid", null);
                        	MasterErrorList err = new MasterErrorList();
                        	err.setErrorCode("bulkuser.processuploadedfile.error.paymenttypeinvalid");
                         	err.setErrorMsg(error);
                         	///masterErrorList.add(err);
//							error = new ListValueVO("", String.valueOf(r.getRowNum()+1), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//								"bulkuser.processuploadedfile.error.paymenttypeinvalid"));
							ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
		    				checkErrors = true;
							fileValidationErrorExists = true;	
							singleRowError.getMasterErrorList().add(err);
							 break;
                          }                           						  
			       }
                   if (fileValidationErrorExists) {       							
    				//	continue;
    				}                                              
                }
    			excel[r.getRowNum()+1][colIt]= cellValue;
    			if (lmsAppl&&!BTSLUtil.isNullString(channelUserOldVO.getLmsProfile()) ){
    				++colIt; 
    				cell = r.getCell(colIt);
    				cellValue = cellValueNull(cell);
    				if (BTSLUtil.isNullString(cellValue)) {
    					final String str = cellValue;
    					final String profileName = null;

    					if (!lmsProfileList.contains(str) && !"".equals(str)) {
                        	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.lmsprofileerr", null);
                        	MasterErrorList err = new MasterErrorList();
                        	err.setErrorCode("bulkuser.processuploadedfile.error.lmsprofileerr");
                         	err.setErrorMsg(error);
                         	//masterErrorList.add(err);
//    						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//    								"bulkuser.processuploadedfile.error.lmsprofileerr"));
    						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    	    				checkErrors = true;
    						fileValidationErrorExists = true;
    						singleRowError.getMasterErrorList().add(err);
    					
    					}
    				}
    				excel[r.getRowNum()+1][colIt]= cellValue;
    			}else if(lmsAppl&&BTSLUtil.isNullString(channelUserOldVO.getLmsProfile())){
    				++colIt;
    			}
    			
    			// LMS Profile validation ends here
    			
				  ++colIt;
				   cellValue = null;
				   if (r.getCell(colIt)==null) {
		            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.commprfmissing", null);
		            	MasterErrorList err = new MasterErrorList();
		            	err.setErrorCode("bulkuser.processuploadedfile.error.commprfmissing");
		             	err.setErrorMsg(error);
		             	//masterErrorList.add(err);
//		                error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//		                    "bulkuser.processuploadedfile.error.commprfmissing"));
		                ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
						checkErrors = true;
		                fileValidationErrorExists = true;
		                singleRowError.getMasterErrorList().add(err);
		                
		            }

				   else if(r.getCell(colIt)!=null) {
					  cellValue = r.getCell(colIt).getStringCellValue();
				  if (BTSLUtil.isNullString(cellValue)) {
		            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.commprfmissing", null);
		            	MasterErrorList err = new MasterErrorList();
		            	err.setErrorCode("bulkuser.processuploadedfile.error.commprfmissing");
		             	err.setErrorMsg(error);
		             	//masterErrorList.add(err);
//		                error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//		                    "bulkuser.processuploadedfile.error.commprfmissing"));
		                ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
						checkErrors = true;
		                fileValidationErrorExists = true;
		                singleRowError.getMasterErrorList().add(err);
		                
		            }
				  else if(!BTSLUtil.isNullString(channelUserOldVO.getCommissionProfileSetID())){
            if (BTSLUtil.isNullString(cellValue)) {
            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.commprfmissing", null);
            	MasterErrorList err = new MasterErrorList();
            	err.setErrorCode("bulkuser.processuploadedfile.error.commprfmissing");
             	err.setErrorMsg(error);
             	//masterErrorList.add(err);
//                error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                    "bulkuser.processuploadedfile.error.commprfmissing"));
                ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
				checkErrors = true;
                fileValidationErrorExists = true;
                singleRowError.getMasterErrorList().add(err);
                
            } else {
              cellValue = cellValue.trim();
                for (int i = 0; i < comPrfSize; i++) {
                    commissionProfileSetVO = (CommissionProfileSetVO) commPrfList.get(i);
// Map the category code entered in the xls
                    // file with master data
                    
                    if (commissionProfileSetVO.getCategoryCode().equals(categoryVO.getCategoryCode()))// excelArr[r][z-15]))
                    { // first match the profile ids
                        if (!cellValue.equals(commissionProfileSetVO.getCommProfileSetId())) {
                            found = false; // profile-id match
                        }
                        else { // profile-id match success
                            found = true; // assume grade, geog,
								 // id match found. Now
                            // check against the
                            // commissionProfileSetVO
                            // data 
                
                            String gradeCode = "";
                            if(r.getCell(colIt + 2)!=null)
                            	gradeCode = r.getCell(colIt + 2).getStringCellValue();
                            if (!grphDomainCode.equals(commissionProfileSetVO.getGrphDomainCode()) || !gradeCode.equals(commissionProfileSetVO
                                .getGradeCode())) {
                                found = false; // geog, grade
                            }
                            if ("ALL".equals(commissionProfileSetVO.getGrphDomainCode()) && gradeCode.equals(commissionProfileSetVO.getGradeCode())) {
                                found = true; // check
                                break;
                            } else if ("ALL".equals(commissionProfileSetVO.getGradeCode()) && grphDomainCode.equals(commissionProfileSetVO
                                .getGrphDomainCode())) {
                                found = true; // check
                                break;
                            } else if ("ALL".equals(commissionProfileSetVO.getGrphDomainCode()) && "ALL".equals(commissionProfileSetVO.getGradeCode())) {
                                found = true; // check
                                break;
                            }
                            if (found) {
                                break; // if found break, else
                            }
                        }
                    }
                }
                if (!found) {
                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.commprfnotundercaterr", new String[] { cellValue, categoryVO.getCategoryCode() });
                	MasterErrorList err = new MasterErrorList();
                	err.setErrorCode("bulkuser.processuploadedfile.error.commprfnotundercaterr");
                 	err.setErrorMsg(error);
                 	//masterErrorList.add(err);
//                    error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                        "bulkuser.processuploadedfile.error.commprfnotundercaterr", new String[] { cellValue, theForm.getCategoryCode() }));
                    ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
                    fileValidationErrorExists = true;
    				checkErrors = true;
    				singleRowError.getMasterErrorList().add(err);
                
                }
            }
    		}}
            excel[r.getRowNum()+1][colIt]= cellValue;
            ++colIt;// z=25/26(IF_FNAME_LNAME_ALLOWED)
            cellValue = null;
            if (r.getCell(colIt)==null) // Transfer
            {
            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.trfprfmissing", null);
            	MasterErrorList err = new MasterErrorList();
            	err.setErrorCode("bulkuser.processuploadedfile.error.trfprfmissing");
             	err.setErrorMsg(error);
             	//masterErrorList.add(err);
//                error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                    "bulkuser.processuploadedfile.error.trfprfmissing"));
                ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
				checkErrors = true;
                fileValidationErrorExists = true;
                singleRowError.getMasterErrorList().add(err);
                
            }
            else if(r.getCell(colIt)!=null) {
            	cellValue = r.getCell(colIt).getStringCellValue();
            if (BTSLUtil.isNullString(cellValue)) // Transfer
            {
            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.trfprfmissing", null);
            	MasterErrorList err = new MasterErrorList();
            	err.setErrorCode("bulkuser.processuploadedfile.error.trfprfmissing");
             	err.setErrorMsg(error);
             	//masterErrorList.add(err);
//                error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                    "bulkuser.processuploadedfile.error.trfprfmissing"));
                ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
				checkErrors = true;
                fileValidationErrorExists = true;
                singleRowError.getMasterErrorList().add(err);
                
            }
            else if(!BTSLUtil.isNullString(channelUserOldVO.getTransferProfileID())){
            if (BTSLUtil.isNullString(cellValue)) // Transfer
            {
            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.trfprfmissing", null);
            	MasterErrorList err = new MasterErrorList();
            	err.setErrorCode("bulkuser.processuploadedfile.error.trfprfmissing");
             	err.setErrorMsg(error);
             	//masterErrorList.add(err);
//                error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                    "bulkuser.processuploadedfile.error.trfprfmissing"));
                ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
				checkErrors = true;
                fileValidationErrorExists = true;
                singleRowError.getMasterErrorList().add(err);
                
            } else {
                cellValue = cellValue.trim();
                for (int i = 0; i < transPrfSize; i++) {
                    found = false;
                    profileVO = (TransferProfileVO) transferPrfList.get(i);
                    if (profileVO.getCategory().equals(categoryVO.getCategoryCode()))// excelArr[r][z-16]))
                    {
//                    	System.out.println(cellValue+profileVO.getProfileId());
                        if (!cellValue.equals(profileVO.getProfileId())) {
                            found = false;
                        } else {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.trprfnotundercaterr", new String[] { cellValue, categoryVO.getCategoryCode() });
                	MasterErrorList err = new MasterErrorList();
                	err.setErrorCode("bulkuser.processuploadedfile.error.trprfnotundercaterr");
                 	err.setErrorMsg(error);
                 	//masterErrorList.add(err);
//                    error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                        "bulkuser.processuploadedfile.error.trprfnotundercaterr", new String[] { cellValue, theForm.getCategoryCode() }));
                    ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
    				checkErrors = true;
                    fileValidationErrorExists = true;
                    singleRowError.getMasterErrorList().add(err);
                
                }
            }
    		}}
            excel[r.getRowNum()+1][colIt]= cellValue;
            ++colIt;// z=26/27(IF_FNAME_LNAME_ALLOWED)
            cellValue = null;
            if (r.getCell(colIt)== null) // Grade
            {
            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.grademissing", null);
            	MasterErrorList err = new MasterErrorList();
            	err.setErrorCode("bulkuser.processuploadedfile.error.grademissing");
             	err.setErrorMsg(error);
             	//masterErrorList.add(err);
//                error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                    "bulkuser.processuploadedfile.error.grademissing"));
                ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
                fileValidationErrorExists = true;
				checkErrors = true;
				singleRowError.getMasterErrorList().add(err);
                
            }
            else if(r.getCell(colIt)!=null) {
				cellValue = r.getCell(colIt).getStringCellValue();
            if (BTSLUtil.isNullString(cellValue)) // Grade
            {
            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.grademissing", null);
            	MasterErrorList err = new MasterErrorList();
            	err.setErrorCode("bulkuser.processuploadedfile.error.grademissing");
             	err.setErrorMsg(error);
             	//masterErrorList.add(err);
//                error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                    "bulkuser.processuploadedfile.error.grademissing"));
                ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
                fileValidationErrorExists = true;
				checkErrors = true;
				singleRowError.getMasterErrorList().add(err);
                
            }
            else if(!BTSLUtil.isNullString(channelUserOldVO.getUserGrade())){
            if (BTSLUtil.isNullString(cellValue)) // Grade
            {
            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.grademissing", null);
            	MasterErrorList err = new MasterErrorList();
            	err.setErrorCode("bulkuser.processuploadedfile.error.grademissing");
             	err.setErrorMsg(error);
             	//masterErrorList.add(err);
//                error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                    "bulkuser.processuploadedfile.error.grademissing"));
                ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
                fileValidationErrorExists = true;
				checkErrors = true;
				singleRowError.getMasterErrorList().add(err);
                
            } else {
              cellValue = cellValue.trim();
                for (int i = 0; i < gradeSize; i++) {
                    gradeVO = (GradeVO) gradeList.get(i);
                    if (gradeVO.getCategoryCode().equals(categoryVO.getCategoryCode()))// excelArr[r][z-19]))
                    {
                        if (!cellValue.equals(gradeVO.getGradeCode())) {
                            found = false;
                        } else {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.gradecodemismatch", null);
                	MasterErrorList err = new MasterErrorList();
                	err.setErrorCode("bulkuser.processuploadedfile.error.gradecodemismatch");
                 	err.setErrorMsg(error);
                 	//masterErrorList.add(err);
//                    error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                        "bulkuser.processuploadedfile.error.gradecodemismatch"));
                    ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
                    fileValidationErrorExists = true;
    				checkErrors = true;
    				singleRowError.getMasterErrorList().add(err);
                
                }
            }
            }}
            excel[r.getRowNum()+1][colIt]= cellValue;
            
           
              if(userVoucherTypeAllowed  && ((Boolean) PreferenceCache.getControlPreference(PreferenceI.CHNLUSR_VOUCHER_CATGRY_ALLWD, userVO.getNetworkID(), categoryVO.getCategoryCode())).booleanValue()) { //Voucher Type 
  				    ++colIt; 
					cell = r.getCell(colIt);
        			cellValue = cellValueNull(cell);
  				// ******************Vouchers validation
  				// starts*********************
  				 if(!BTSLUtil.isNullString(cellValue)) {
  					cellValue = cellValue.trim();
  					// Check the valid length of comma seperated vouchers
  					// Itratae the comma seperated vouchers for storing in
  					// arraylist
  					if (cellValue.contains(",")) {
  						voucherTypeArr = BTSLUtil.removeDuplicatesString(cellValue.split(","));
  					} else {
  						voucherTypeArr = new String[1];
  						voucherTypeArr[0] = cellValue;
  					}
  					// If there will be no comma voucherTypeArr.length will be 1
  					voucherTypeLen = voucherTypeArr.length;

  					if (voucherTypeLen > voucherTypeList.size()) {
  						// Error: The vouchers specified in the XLS file
  						// will be greater than the
  						// vouchers applicable
  	                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.voucherisinvalid", null);
  	                	MasterErrorList err = new MasterErrorList();
  	                	err.setErrorCode("bulkuser.processuploadedfile.error.voucherisinvalid");
  	                 	err.setErrorMsg(error);
  	                 	//masterErrorList.add(err);
//  						error = new ListValueVO("", String.valueOf(rows), this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//  								"bulkuser.processuploadedfile.error.voucherisinvalid"));
  						ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
  						fileValidationErrorExists = true;
  	    				checkErrors = true;
  	    				singleRowError.getMasterErrorList().add(err);
  				
  					}
  					if (voucherTypeArr != null && voucherTypeLen > 0) {
  						newVoucherTypeList = new ArrayList();
  						invailedVoucher = false;
  						for (int i = 0; i < voucherTypeLen; i++) {
  							voucherTypeArr[i] = voucherTypeArr[i].toUpperCase().trim();
  							if (!BTSLUtil.isNullString(BTSLUtil.getOptionDesc(voucherTypeArr[i], voucherTypeList).getLabel())) {
  								newVoucherTypeList.add(voucherTypeArr[i]);
  							} else {
  		  	                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.voucherisinvalid", null);
  		  	                MasterErrorList err = new MasterErrorList();
  		  	                	err.setErrorCode("bulkuser.processuploadedfile.error.voucherisinvalid");
  		  	                 	err.setErrorMsg(error);
  		  	                 	//masterErrorList.add(err);
//  								error = new ListValueVO("", String.valueOf(rows), "'" + voucherTypeArr[i] + "' " + this.getResources(request).getMessage(
//  										BTSLUtil.getBTSLLocale(request), "bulkuser.processuploadedfile.error.voucherisinvalid"));
  								ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(rows)); allRowErrorList.add(error); fileErrorList.add(allRowErrorList);
  								invailedVoucher = true;
  			    				checkErrors = true;
  			    				singleRowError.getMasterErrorList().add(err);
  								break;
  							}
  						}
  						if (invailedVoucher) {
  							fileValidationErrorExists = true;
  				
  						}
  					}
  					
						
  				}
  				// ******************Vouchers validation
  				// ends*********************
  			}
				 excel[r.getRowNum()+1][colIt]= cellValue;
            
            //Voucher Type End
            
    			channelUserVO = ChannelUserVO.getInstance();
    			userPhoneVO = new UserPhoneVO();
    			arr = new ArrayList();
    			if (!fileValidationErrorExists) {
    				colIndex = 0;
    				channelUserVO.setPasswordModifyFlag(channelUserOldVO.isPasswordModifyFlag());
    				channelUserVO.setDomainID(categoryVO.getDomainCodeforCategory());
    				channelUserVO.setNetworkID(userVO.getNetworkID());
    				channelUserVO.setMsisdn(channelUserOldVO.getMsisdn());
    				channelUserVO.setUserCode(channelUserOldVO.getUserCode());
    				channelUserVO.setRecordNumber(String.valueOf(rows));
    				channelUserVO.setUserID(r.getCell(colIndex).getStringCellValue().trim());
    				String prefix =null;
    				//String prefix = r.getCell(++colIndex).getStringCellValue()
    				if(r.getCell(++colIndex)!=null) {
    					prefix=	r.getCell(colIndex).getStringCellValue();
    				}		
    				
    				if(BTSLUtil.isNullString(prefix)) {
    					if(PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES")))
    						prefix = "MR";
    				}
    				else {
    					prefix = prefix.trim().toUpperCase();
    					
    				}
    				channelUserVO.setUserNamePrefix(prefix);
    				if (!isFnameLnameAllowed) {
    					channelUserVO.setUserName(excel[r.getRowNum()+1][++colIndex]);
    				} else {
    					
    					if(excel[r.getRowNum()+1][++colIndex]!=null) {
    						channelUserVO.setFirstName(excel[r.getRowNum()+1][colIndex].trim());
    					}else {
    						channelUserVO.setFirstName(null);	
    					}
    					
    					if(excel[r.getRowNum()+1][++colIndex]!=null ){
    						channelUserVO.setLastName(excel[r.getRowNum()+1][colIndex].trim());
    					}
    					if (channelUserVO.getLastName() != null) {

    						ashuBuild.append(channelUserVO.getFirstName()).append(" ").append(channelUserVO.getLastName());
    						channelUserVO.setUserName(ashuBuild.toString());
    						ashuBuild.setLength(0);
    						ashuBuild.trimToSize();
    					} else {
    						channelUserVO.setUserName(channelUserVO.getFirstName());
    					}
    				}
    				if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) { // change
    					channelUserVO.setLoginID(excel[r.getRowNum()+1][++colIndex].trim());
    					if (!"SHA".equalsIgnoreCase(pinPasswordEnDeCryptionType)) {
    						if (batchUserPasswordModifyAllow) {
    							if(channelUserOldVO.isPasswordModifyFlag())
    							channelUserVO.setPassword(BTSLUtil.encryptText(excel[r.getRowNum()+1][++colIndex].trim()));
    							else
    							{
    								channelUserVO.setPassword(BTSLUtil.encryptText(channelUserOldVO.getPassword()))	;
    								++colIndex;
    							}
    						}
    					}
    				}
    				
    				String[] msisdnArray = null;
    				String[] pinArray = null;
    				String msisdnEntry="";    				if (PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
    					msisdnEntry=excel[r.getRowNum()+1][++colIndex];
    					msisdnArray = msisdnEntry.trim().split(",");
    					channelUserVO.setMsisdn(msisdnArray[0]);
    					channelUserVO.setUserCode(channelUserVO.getMsisdn());
    					if (!"SHA".equalsIgnoreCase(pinPasswordEnDeCryptionType)) {        						
    						if(isPinModify[0]){
        					++colIndex;
    							pinArray = pinEntry.trim().split(",");
    						}	else
        						{	
        						pinArray=new String[channelUserOldVO.getMsisdnList().size()];
        						for(int i=0;i<channelUserOldVO.getMsisdnList().size();i++)
    						{
    							pinArray[i]=((UserPhoneVO) channelUserOldVO.getMsisdnList().get(i)).getSmsPin();
    						
    						}
    						
    						++colIndex;
    						}
    						channelUserVO.setSmsPin(BTSLUtil.encryptText(pinArray[0]));
    					}
    				}
    				channelUserVO.setGeographicalCode(excel[r.getRowNum()+1][++colIndex].trim().toUpperCase().trim());
    				if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
    					channelUserVO.setGroupRoleFlag(excel[r.getRowNum()+1][++colIndex].trim().toUpperCase().trim());
    					channelUserVO.setGroupRoleCode(excel[r.getRowNum()+1][++colIndex].trim());
    				}
    				if (categoryVO.getServiceAllowed().equals(PretupsI.YES)) {
    					channelUserVO.setServiceTypes(excel[r.getRowNum()+1][++colIndex].trim());
    				}
    				channelUserVO.setShortName(excel[r.getRowNum()+1][++colIndex].trim());
    				channelUserVO.setEmpCode(excel[r.getRowNum()+1][++colIndex].trim());
    				channelUserVO.setExternalCode(excel[r.getRowNum()+1][++colIndex]);
    				
    				channelUserVO.setInSuspend(excel[r.getRowNum()+1][++colIndex].trim().toUpperCase());
    				channelUserVO.setOutSuspened(excel[r.getRowNum()+1][++colIndex].trim().toUpperCase());
    				channelUserVO.setContactPerson(excel[r.getRowNum()+1][++colIndex].trim());
    				channelUserVO.setContactNo(excel[r.getRowNum()+1][++colIndex].trim());
    				channelUserVO.setSsn(excel[r.getRowNum()+1][++colIndex].trim());
    				channelUserVO.setDesignation(excel[r.getRowNum()+1][++colIndex].trim());
    				channelUserVO.setAddress1(excel[r.getRowNum()+1][++colIndex].trim());
    				channelUserVO.setAddress2(excel[r.getRowNum()+1][++colIndex].trim());
    				channelUserVO.setCity(excel[r.getRowNum()+1][++colIndex].trim());
    				channelUserVO.setState(excel[r.getRowNum()+1][++colIndex].trim());
    				channelUserVO.setCountry(excel[r.getRowNum()+1][++colIndex].trim());
    				channelUserVO.setCompany(excel[r.getRowNum()+1][++colIndex].trim());
    				channelUserVO.setFax(excel[r.getRowNum()+1][++colIndex].trim());
    				String language = excel[r.getRowNum()+1][++colIndex];
    				if(BTSLUtil.isNullString(language)) {
    					if(PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES")))
    						language = defaultLanguage+"_"+defaultCountry;
    				}
    				else {
    					language = language.trim();
    				}		
    				channelUserVO.setLanguage(language);
    				channelUserVO.setEmail(excel[r.getRowNum()+1][++colIndex].trim());
    				if (categoryVO.getOutletsAllowed().equals(PretupsI.YES)) {
    					channelUserVO.setOutletCode(excel[r.getRowNum()+1][++colIndex].trim().toUpperCase());
    					channelUserVO.setSubOutletCode(excel[r.getRowNum()+1][++colIndex].trim().toUpperCase());
    				}
    				if (categoryVO.getLowBalAlertAllow().equalsIgnoreCase(PretupsI.YES)) {
    					channelUserVO.setLowBalAlertAllow(excel[r.getRowNum()+1][++colIndex].trim().toUpperCase().trim());
    				} else {
    					channelUserVO.setLowBalAlertAllow(PretupsI.NO);
    				}
    				if (isTrfRuleTypeAllow&&!BTSLUtil.isNullString(channelUserOldVO.getTrannferRuleTypeId())) {
    					channelUserVO.setTrannferRuleTypeId(excel[r.getRowNum()+1][++colIndex].trim());
    				}else if(isTrfRuleTypeAllow&&BTSLUtil.isNullString(channelUserOldVO.getTrannferRuleTypeId())){
    					++colIndex;
    				}
    				if (rsaAuthenticationRequired) {
    					boolean rsaRequired = false;
    					try {
    						rsaRequired = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.RSA_AUTHENTICATION_REQUIRED, userVO.getNetworkID(), r.getCell(5).getStringCellValue().trim()))
    								.booleanValue();
    					} catch (Exception e) {
    						LOG.errorTrace(methodName, e);
    					}
    					if (rsaRequired) {
    						if (!BTSLUtil.isNullString(excel[r.getRowNum()+1][++colIndex].trim())) {
    							channelUserVO.setRsaFlag(excel[r.getRowNum()+1][colIndex].trim().toUpperCase().trim());
    						} else {
    							channelUserVO.setRsaFlag(PretupsI.NO);
    						}

    					}
    				}
    				if (authTypeReq) {

    					if (!BTSLUtil.isNullString(excel[r.getRowNum()+1][++colIndex].trim())) {
    						channelUserVO.setAuthTypeAllowed(excel[r.getRowNum()+1][colIndex].trim().toUpperCase().trim());
    					} else {
    						channelUserVO.setAuthTypeAllowed(PretupsI.NO);
    					}

    				}
    				++colIndex; 
    				if (!BTSLUtil.isNullString(excel[r.getRowNum()+1][colIndex].trim())) {
    					channelUserVO.setLongitude(excel[r.getRowNum()+1][colIndex].trim());
    				}
    				++colIndex; 
    				if (!BTSLUtil.isNullString(excel[r.getRowNum()+1][colIndex].trim())) {
    					channelUserVO.setLatitude(excel[r.getRowNum()+1][colIndex].trim());
    				}
    				++colIndex; 
    				if (!BTSLUtil.isNullString(excel[r.getRowNum()+1][colIndex].trim())) {
    					channelUserVO.setDocumentType(excel[r.getRowNum()+1][colIndex].trim());
    				}
    				++colIndex; 
    				if (!BTSLUtil.isNullString(excel[r.getRowNum()+1][colIndex].trim())) {
    					channelUserVO.setDocumentNo(excel[r.getRowNum()+1][colIndex].trim());
    				}        					        				 	        				
    				++colIndex; 
    				channelUserVO.setPaymentType(excel[r.getRowNum()+1][colIndex].trim());	        				        				       				
    				
        			if (lmsAppl&&!BTSLUtil.isNullString(channelUserOldVO.getLmsProfile())) {
    					++colIndex;
    					channelUserVO.setLmsProfile(excel[r.getRowNum()+1][colIndex].trim());
    				}else if(lmsAppl&&BTSLUtil.isNullString(channelUserOldVO.getLmsProfile())){
    					++colIndex;
    				}
    				channelUserVO.setCategoryVO(categoryVO);
    				channelUserVO.setModifiedBy(userVO.getUserID());
    				channelUserVO.setModifiedOn(currentDate);
    				channelUserVO.setUserType(PretupsI.CHANNEL_USER_TYPE);

    				userPhoneVO.clearInstance();
    				userPhoneVO.setMsisdn(channelUserVO.getMsisdn());
    				for (int i = 0; i < noOfMsisdn; i++) {
    					userPhoneVO = new UserPhoneVO();
    					userPhoneVO.clearInstance();
    					userPhoneVO.setMsisdn(msisdnArray[i]);
    					userPhoneVO.setShowSmsPin(BTSLUtil.encryptText(pinArray[i]));// for
    					userPhoneVO.setSmsPin(BTSLUtil.encryptText(pinArray[i]));
    					userPhoneVO.setPinModifyFlag(isPinModify[i]);
    					final String lang_country[] = (channelUserVO.getLanguage()).split("_");
    					userPhoneVO.setPhoneLanguage(lang_country[0]);
    					userPhoneVO.setCountry(lang_country[1]);
    					arr.add(userPhoneVO);
    				}
    				channelUserVO.setMsisdnList(arr);
    				++colIndex;
    				if(!BTSLUtil.isNullString(channelUserOldVO.getCommissionProfileSetID())){
					channelUserVO.setCommissionProfileSetID(excel[r.getRowNum()+1][colIndex].trim());
    				}
    				++colIndex;
    				if(!BTSLUtil.isNullString(channelUserOldVO.getTransferProfileID())){
					channelUserVO.setTransferProfileID(excel[r.getRowNum()+1][colIndex].trim());
    				}
    				++colIndex;
    				if(!BTSLUtil.isNullString(channelUserOldVO.getUserGrade())){
					channelUserVO.setUserGrade(excel[r.getRowNum()+1][colIndex].trim());
    				}
    				++colIndex;
    				if(userVoucherTypeAllowed && ((Boolean) PreferenceCache.getControlPreference(PreferenceI.CHNLUSR_VOUCHER_CATGRY_ALLWD, userVO.getNetworkID(), categoryVO.getCategoryCode())).booleanValue()) {	        				 	        				
        			
    					channelUserVO.setVoucherTypes(excel[r.getRowNum()+1][colIndex].trim());	        				
    				}
    				
    				
    				channelUserVODataList.add(channelUserVO);
    				//update to the database
    				if (channelUserVODataList != null && channelUserVODataList.size() == taskSize) {
    						executor.execute(new UpdateRecordsInDB(channelUserVODataList,fileErrorList, batchUserWebDAO, categoryVO.getDomainCodeforCategory(), locale, fileName+"."+request.getFileType()));
    					channelUserVODataList = new ArrayList();	              				  
    				}
    			}
    			else {
    				invalidRecordCount++;
    			}
    			}
    			
    		    if((r.getRowNum() % 1000) == 0) {
                	Runtime runtime = Runtime.getRuntime();
                    long memory = runtime.totalMemory() - runtime.freeMemory();
                    LOG.debug("processUploadedFile","Used memory in megabytes before gc: " + (memory)/1048576);
                    // Run the garbage collector
                    runtime.gc();
                    // Calculate the used memory
                    memory = runtime.totalMemory() - runtime.freeMemory();
                    LOG.debug("processUploadedFile","Used memory in megabytes after gc: " + (memory)/1048576);
                }
    		    
    		    if (checkErrors) {
    		     	rowErrors.add(singleRowError);
    		    }
    		    
    		}
    	}
    	errMap.setRowErrorMsgLists(rowErrors);
    	response.setErrorMap(errMap);
		
    	}finally{
    		if(is!=null) 
    			is.close();      
    	}       	
        executor.shutdown();   // Now close the executor service 
		while (!executor.isTerminated()) {
		}
		log.debug("processUploadedFile","Finished all threads");			
    	//processing the leftover user list
        if(channelUserVODataList !=null && !channelUserVODataList.isEmpty()) {
        	dbErrorList = batchUserWebDAO.modifyBulkChannelUserList(con, channelUserVODataList, categoryVO.getDomainCodeforCategory(), locale, fileName+"."+request.getFileType());
			con.commit();
			fileErrorList.addAll(dbErrorList);
			list.addAll(channelUserVODataList);
			channelUserVODataList.clear();
			channelUserVODataList = null;
        }

		ErrorFileResponse errorResponse = new ErrorFileResponse();
		if(fileErrorList.size()>0) {
			DownloadUserListService downloadUserListService = new DownloadUserListServiceImpl();
    		ErrorFileRequestVO errorFileRequestVO = new ErrorFileRequestVO();
    		errorFileRequestVO.setFile(request.getFileAttachment());
    		errorFileRequestVO.setFiletype(request.getFileType());
    		errorFileRequestVO.setRowErrorMsgLists(rowErrors);
    		errorFileRequestVO.setAdditionalProperty(PretupsI.SERVICE_KEYWORD, PretupsI.BATCH_OPT_USR_MODIFICATION_SERVICE);
    		errorFileRequestVO.setAdditionalProperty("row", 6);//6 is header row, count start from 0
    		if(invalidRecordCount + emptyRowCount < (totalRecordsInFile+emptyRowCount)) errorFileRequestVO.setPartialFailure(true);
    		if(invalidRecordCount==(totalRecordsInFile)) errorFileRequestVO.setPartialFailure(false);
    		downloadErrorLogFile(fileErrorList, userVO, errorResponse, responseSwag);
		}
		
		//setting response
		response.setTotalRecords(totalRecordsInFile+emptyRowCount);
		response.setValidRecords(totalRecordsInFile- invalidRecordCount);
		if(fileErrorList.size()  > 0) {
			fileValidationErrorExists = true;
			response.setFileAttachment(errorResponse.getFileAttachment());
			response.setFileName(errorResponse.getFileName());
			for(RowErrorMsgLists obj : rowErrors) {
				String rowNum = obj.getRowValue();
				int rowInt = Integer.parseInt(rowNum);
				rowNum = "Row "+Integer.toString(rowInt);
				obj.setRowValue(rowNum);
			}
			errMap.setRowErrorMsgLists(rowErrors);
			if(invalidRecordCount+emptyRowCount<totalRecordsInFile+emptyRowCount) {//partial failure
				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
    			response.setMessage(msg);
				response.setStatus("400");
				responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
			}else if(invalidRecordCount + emptyRowCount == (totalRecordsInFile+emptyRowCount)) {//all records failed
				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
    			response.setMessage(msg);
				response.setStatus("400");
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
			}		}
        
//      rows-DATAROWOFFSET is total number of records in file,
//      if total errors is >= total no of recs then i
        if ((invalidRecordCount - reptRowNo) >= ((rows+emptyRowCount) - DATAROWOFFSET)) {
				this.deleteUploadedFile(request);
    			throw new BTSLBaseException(this, "processUploadedFile", "bulkuser.processuploadedfile.modify.error.invalidfile");
    		}

    		// ***********************Sort the fileErrorList...
//    		Collections.sort(fileErrorList);
//    		theForm.setErrorList(fileErrorList);
    		if ((fileErrorList != null && !fileErrorList.isEmpty())) {
    		} else {
    			// No error will be added in the file as well as db list
    			final BTSLMessages btslMessage = new BTSLMessages("bulkuser.processuploadedfile.modify.msg.success", new String[] { String.valueOf(rows - DATAROWOFFSET) },
    					"goToStart");
    			  
                //OTF Message function for batch modify users 
              	 if(realtimeOtfMsgs && ((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,userVO.getNetworkID()) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,userVO.getNetworkID()))){
                     	TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
                     	tbcm.loadCommissionProfileDetailsForOTFMessages(con,list);
                 }    			
    		}       	
        } catch(Exception e){
			e.printStackTrace();
		}finally {
	            if (LOG.isDebugEnabled()) {
	                LOG.debug("processUploadedFile", "Exiting:forward=");
	            }
        }   	
        return (!fileValidationErrorExists);
	}
	
	public List<ChannelUserVO> fetchChannelUsersByStatusForBarredfrdltReq(Connection con,BarredusersrequestVO requestVo) throws BTSLBaseException {
		final  String methodName="fetchChannelUsersByStatusForBarredfrdltReq";
	   UserDAO userDAO = new UserDAO();
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		String validUserStatus="";
		ChannelUserDAO channelUserDao = new ChannelUserDAO();
		List<ChannelUserVO> userList=null;
		ChannelUserVO uservo=null;
		
		
		if(BTSLUtil.isNullorEmpty(requestVo.getUserStatus())){
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Search UserStatus"}, null);
		}
		else {
			if(requestVo.getUserStatus().equalsIgnoreCase(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) {
				validUserStatus=PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST;
			}
			else if(requestVo.getUserStatus().equalsIgnoreCase(PretupsI.USER_STATUS_BARRED)) {
				validUserStatus=PretupsI.USER_STATUS_BARRED;
			}
			else if(requestVo.getUserStatus().equalsIgnoreCase(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE)) {
				validUserStatus=PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE;
			}
			else if(requestVo.getUserStatus().equalsIgnoreCase(PretupsI.USER_STATUS_ACTIVE)) {
				validUserStatus=PretupsI.USER_STATUS_ACTIVE;
			}
			else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROPERTY_INVALID, PretupsI.RESPONSE_FAIL, new String[] {"Search UserStatus"}, null);
			}
		}
		if(BTSLUtil.isNullorEmpty(requestVo.getSearchType())){
			throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Search Type"}, null);
		}
		else {
			if(requestVo.getSearchType().equalsIgnoreCase(PretupsI.SEARCH_BY_MSISDN)) {
				if(BTSLUtil.isNullorEmpty(requestVo.getMobileNumber())){
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Mobile No"}, null);
				}
				else if(BTSLUtil.isNullorEmpty(requestVo.getLoggedUserNeworkCode())) {
	            	   throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Network Code"}, null);
				}
//				 check user existed with msisddn
				else if(!userDAO.isMSISDNExist(con, requestVo.getMobileNumber(), null)) {
					throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.USER_NOT_EXIST,PretupsI.RESPONSE_FAIL,new String[] {},null );
				}
				else {
					 uservo= channelUserDao.loadUsersDetailsByMsisdnOrLogin(con, requestVo.getMobileNumber(),null, PretupsI.STATUS_IN, requestVo.getUserStatus(),requestVo.getLoggedUserNeworkCode());
				     if(!BTSLUtil.isNullorEmpty(uservo)) { 
				    	 userList=new ArrayList<ChannelUserVO>();
				    	 userList.add(uservo);
				     }else { 
				    	 throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.USER_NOT_ALLOWED
				    			 ,PretupsI.RESPONSE_FAIL,new String[] {},null );
				     }
				}
			}
			else if(requestVo.getSearchType().equalsIgnoreCase(PretupsI.LOGINID)) {
				if(BTSLUtil.isNullorEmpty(requestVo.getLoginID())){
					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Login Id"}, null);
			 	}
				else if(BTSLUtil.isNullorEmpty(requestVo.getLoggedUserNeworkCode())) {
	            	   throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Network Code"}, null);
				 }
				else if(!userDAO.isUserLoginExist(con,requestVo.getLoginID(),null)) {
					throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.USER_NOT_EXIST,PretupsI.RESPONSE_FAIL,new String[] {},null );
				}
				else {
					
					 uservo= channelUserDao.loadUsersDetailsByMsisdnOrLogin(con, null, requestVo.getLoginID(),PretupsI.STATUS_IN, requestVo.getUserStatus(),requestVo.getLoggedUserNeworkCode());
					 if(!BTSLUtil.isNullorEmpty(uservo)) { 
				    	 userList=new ArrayList<ChannelUserVO>();
				    	 userList.add(uservo); 
				     }else { 
				    	 throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.USER_NOT_ALLOWED
				    			 ,PretupsI.RESPONSE_FAIL,new String[] {},null );
				     } 
				}
			}
			else if(requestVo.getSearchType().equalsIgnoreCase(PretupsI.SEARCH_BY_ADVANCED)) {
				if(BTSLUtil.isNullorEmpty(requestVo.getDomain())){
					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Domain"}, null);
			 	}
				if(BTSLUtil.isNullorEmpty(requestVo.getCategory())) {
					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Category"}, null);
				}
               if(BTSLUtil.isNullorEmpty(requestVo.getGeography())) {
            	   throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Geography"}, null);
			   }
               if(BTSLUtil.isNullorEmpty(requestVo.getLoggedUserNeworkCode())) {
            	   throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Network Code"}, null);
			   }
			   else {
				   
					List<ChannelUserVO> userList1=channelUserDao.loadApprovalUsersList
								   (con, requestVo.getCategory(), PretupsI.USER_STATUS_TYPE,
										 requestVo.getLoggedUserNeworkCode(), requestVo.getGeography(), 
										 requestVo.getUserStatus(),PretupsI.CHANNEL_USER_TYPE);
						 if(!BTSLUtil.isNullorEmpty(userList1)) {
							 userList=userList1;
						 }
			    }
			}
			else if(requestVo.getSearchType().equalsIgnoreCase(PretupsI.SEARCH_BY_EXTCODE)) {
				if(BTSLUtil.isNullorEmpty(requestVo.getExternalcode())){
					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"External code"}, null);
			 	}
				else if(BTSLUtil.isNullorEmpty(requestVo.getLoggedUserNeworkCode())) {
	            	   throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Network Code"}, null);
				 }
//				else if(!userDAO.isUserLoginExist(con,requestVo.getExternalcode(),null)) {
//					throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.USER_NOT_EXIST,PretupsI.RESPONSE_FAIL,new String[] {},null );
//				}
				else {
					uservo= channelUserDao.loadUsersDetailsByExtcode(con,requestVo.getExternalcode(),null,PretupsI.STATUS_IN,requestVo.getUserStatus());
					 if(!BTSLUtil.isNullorEmpty(uservo)) { 
				    	 userList=new ArrayList<ChannelUserVO>();
				    	 userList.add(uservo); 
				     }else { 
				    	 throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.USER_NOT_ALLOWED
				    			 ,PretupsI.RESPONSE_FAIL,new String[] {},null );
				     } 
				}
				
			}
			else {
				throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.PROPERTY_INVALID, PretupsI.RESPONSE_FAIL, new String[] {"Search Type"}, null);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exit");
		}
		return userList;
	}
	
	public boolean approvalOrRejectBarredUser(Connection con,ApprovalBarredForDltRequestVO approvalBarredForDltRequestVO,OAuthUserData oauthUserData)throws SQLException, BTSLBaseException{
		final  String methodName="approvalOrRejectBarreduser";
		boolean changeUserStatus=false;
		UserDAO userDAO = new UserDAO();
		UserWebDAO userwebDAO = new UserWebDAO();
		UserVO user = null;
		UserEventRemarksVO userRemarksVO = null;
		 BTSLMessages btslMessage = null;
	    boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        boolean isEmailServiceAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
        int reqCuserBarApproval = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REQ_CUSER_BAR_APPROVAL))).intValue();
        
        
        final UserVO userSessionVO = userDAO.loadUsersDetails(con,oauthUserData.getMsisdn());
		UserVO userVO=userDAO.loadUserDetailsByLoginId(con,approvalBarredForDltRequestVO.getLoginId());
		if(BTSLUtil.isNullorEmpty(userVO)) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOGIN_ID_DOES_NOT_EXISTS , PretupsI.RESPONSE_FAIL, new String[] {}, null);
		}
		else if(userVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_BARRED)){
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BAR_FOR_DEL_PROCESS_ALREADY_EXECUTED, PretupsI.RESPONSE_FAIL, new String[] {}, null);
		}
		else if(!(userVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST) || userVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE ))) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_ALLOWED, PretupsI.RESPONSE_FAIL, new String[] {}, null);
		}
        ArrayList<UserEventRemarksVO> BarredRemarkList = null;
        boolean isO2CPendingFlag = false;
        int barCount=0;
        boolean isSOSPendingFlag = false;
        boolean isLRPendingFlag = false;
        //to checks == null empty
         user = new UserVO();
         final ArrayList newUserList = new ArrayList();
         final Date currentDate = new Date();
         if(!(approvalBarredForDltRequestVO.getRequestType().equalsIgnoreCase("BARREDAPPROVAL1") || approvalBarredForDltRequestVO.getRequestType().equalsIgnoreCase("BARREDAPPROVAL2"))) {
         	throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.PROPERTY_INVALID, PretupsI.RESPONSE_FAIL, new String[] {"RequestType"}, null);
         }
         else {
             final boolean isChildFlag = userDAO.isChildUserActive(con, userVO.getUserID());

             if (isChildFlag) {
            	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHILD_USER_EXIST1,PretupsI.RESPONSE_FAIL,new String[] {}, null);
             } else {
            	 if (channelSosEnable) {
						// Checking SOS Pending transactions
						ChannelSOSSettlementHandler channelSOSSettlementHandler = new ChannelSOSSettlementHandler();
						isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(con, userVO.getUserID());
					}
             }
             if (isSOSPendingFlag) {
					throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.SOS_TRANSACTION_PENDING1,PretupsI.RESPONSE_FAIL,new String[] {},
							null);

				}else {
             	// Checking for pending LR transactions
					if (lrEnabled) {
						UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
						UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
						userTrfCntVO = userTrfCntDAO.selectLastLRTxnID(userVO.getUserID(), con, false, null);
						if (userTrfCntVO != null)
							isLRPendingFlag = true;
					}
				}
             if (isLRPendingFlag) {

					throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.LR_TRANSACTION_PENDING1, PretupsI.RESPONSE_FAIL,new String[] {},
							null);
				}else{
                 // Checking O2C Pending transactions
					final ChannelTransferDAO transferDAO = new ChannelTransferDAO();
					isO2CPendingFlag = transferDAO.isPendingTransactionExist(con, userVO.getUserID());
				}
             boolean isbatchFocPendingTxn = false;
				if (isO2CPendingFlag) {

					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.O2C_TRANSACTION_PENDING1, PretupsI.RESPONSE_FAIL,new String[] {},
							null);
				} else {
					final FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
					isbatchFocPendingTxn = batchTransferDAO.isPendingTransactionExist(con, userVO.getUserID());
				}
				if (isbatchFocPendingTxn) {
					throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.FOC_TRANSACTION_PENDING1, PretupsI.RESPONSE_FAIL,new String[] {},
							null);
				}
         }
				user.setUserID(userVO.getUserID());
				if (PretupsI.USER_APPROVE.equals(approvalBarredForDltRequestVO.getAction())) {
					if ("BARREDAPPROVAL1".equals(approvalBarredForDltRequestVO.getRequestType())) {
                        if (reqCuserBarApproval == 1 ) {
//                            if (userwebDAO.checkBarLimit(con) <= 0) {
//                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_CANNOT_BE_BARRED_DELETED,PretupsI.RESPONSE_FAIL,new String[] {},null);
//                            }

                            user.setPreviousStatus(userVO.getStatus());
                            user.setStatus(PretupsI.USER_STATUS_BARRED);
                            userwebDAO.editRoles(con, userVO.getUserID());
                        } else {
                            user.setPreviousStatus(userVO.getStatus());
                            user.setStatus(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE);
                        }
                    }
					else {
//                        if (userwebDAO.checkBarLimit(con) <= 0) {
//                            throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.USER_CANNOT_BE_BARRED_DELETED,PretupsI.RESPONSE_FAIL,new String[] {},null);
//                        }

                        user.setPreviousStatus(userVO.getStatus());
                        user.setStatus(PretupsI.USER_STATUS_BARRED);
                        userwebDAO.editRoles(con, userVO.getUserID());
                    }
				
				}
				else if (PretupsI.USER_REJECTED.equals(approvalBarredForDltRequestVO.getAction())) {
                    user.setPreviousStatus(userVO.getStatus());
                    user.setStatus(PretupsI.USER_STATUS_ACTIVE);
                }
				if (!PretupsI.USER_DISCARD.equals(approvalBarredForDltRequestVO.getAction())) {
                    user.setUserID(userVO.getUserID());
                    user.setLastModified(userVO.getLastModified());
                    user.setModifiedBy(userSessionVO.getUserID());
                    user.setModifiedOn(currentDate);
                    user.setNetworkID(userVO.getNetworkID());

                    user.setUserName(userVO.getUserName());
                    user.setLoginID(userVO.getLoginID());
                    user.setMsisdn(userVO.getMsisdn());
                    user.setBatchID(null);
                    barCount = userwebDAO.barForDelUser(con, user);
                    newUserList.add(user);
                }
				if ((barCount > 0) && ((userVO.getStatus().equals(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) ||(userVO.getStatus().equals(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE)))) {
                    int barRemarkCount = 0;
                    BarredRemarkList = new ArrayList<UserEventRemarksVO>();
                    userRemarksVO = new UserEventRemarksVO();
                    userRemarksVO.setCreatedBy(userSessionVO.getUserID());
                    userRemarksVO.setCreatedOn(currentDate);// Modified
                    // while
                    // Mobinil
                    // bug fix
                    userRemarksVO.setEventType(PretupsI.BARRED_REQUEST_EVENT);
                    userRemarksVO.setMsisdn(userVO.getMsisdn());
                    userRemarksVO.setRemarks(approvalBarredForDltRequestVO.getRemarks());
                    userRemarksVO.setUserID(userVO.getUserID());
                    userRemarksVO.setUserType(userVO.getUserType());
                    userRemarksVO.setModule(PretupsI.C2S_MODULE);
                    BarredRemarkList.add(userRemarksVO);
                    barRemarkCount = userwebDAO.insertEventRemark(con, BarredRemarkList);
                    if (barRemarkCount <= 0) {
                    	con.rollback();
						throw new BTSLBaseException(this,methodName,
								PretupsErrorCodesI.USER_CANNOT_BE_BARRED_DELETED, 0, null);
                    }
                    con.commit();
                    changeUserStatus=true;
                    int indexSuspend=0;
                    //send msg
                    for (indexSuspend=0;indexSuspend<newUserList.size();indexSuspend++){
                        try {
    						userVO = (UserVO) newUserList.get(indexSuspend);
    						BTSLMessages sendBtslMessage=null;
    						
    						if(userVO.getStatus().equals(PretupsI.USER_STATUS_BARRED)) {
    							  String arr[] = {userVO.getUserName(),"barred"};
    						    sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.MSG_SUCCESSFUL_OF_OPERACTION,arr);
       
    						}
                            else {
                            	 String arr[] = {userVO.getUserName(),"Activated from Barred Request"};
                               sendBtslMessage=new BTSLMessages(PretupsErrorCodesI.MSG_SUCCESSFUL_OF_OPERACTION,arr); 
                            }
    						//Added for sending the notification language as per user assigned
    	                    ChannelUserVO channelUser = userDAO.loadUserDetailsCompletelyByMsisdn(con,userVO.getMsisdn());
    	                    Locale locale =null;
    	                    if(channelUser.getUserPhoneVO()!=null){
    	                    	locale = new Locale(channelUser.getUserPhoneVO().getPhoneLanguage(),channelUser.getUserPhoneVO().getCountry());
    	                    } else {
    	                    	locale = new Locale(defaultLanguage,defaultCountry);
    	                    }
    						try {
    		                    PushMessage pushMessage=new PushMessage(channelUser.getMsisdn(),sendBtslMessage,"","", locale,userSessionVO.getNetworkID(), null, PretupsI.SERVICE_TYPE_EXT_CHNL_BARRED);
    							pushMessage.push();
    						}catch (Exception e) {
    							log.errorTrace(methodName, e);
    						}
    						try {
    					    if (isEmailServiceAllow && !BTSLUtil.isNullString(channelUser.getEmail())) {
                            
                                final EmailSendToUser emailSendToUser = new EmailSendToUser("", sendBtslMessage, locale, userSessionVO.getNetworkID(),
                                                "Email has ben delivered recently", channelUser, userSessionVO);
                                emailSendToUser.sendMail();
                            }
    					    }catch (Exception e) {
    					    	log.errorTrace(methodName, e);
    						}
    						
    					} catch (RuntimeException e) {
    						log.errorTrace(methodName, e);
    					}
                        btslMessage = null;
                    }
                }
                return changeUserStatus;

	}
	

	
	
	public void downloadErrorLogFile(ArrayList errorList, UserVO userVO, ErrorFileResponse response, HttpServletResponse responseSwag)
	{
	    final String METHOD_NAME = "downloadErrorLogFile";
	    Writer out =null;
	    File newFile = null;
        File newFile1 = null;
        String fileHeader=null;
        Date date= new Date();
		if (LOG.isDebugEnabled())
			LOG.debug(METHOD_NAME, "Entered");
		try
		{
			String filePath = Constants.getProperty("DownloadErLogFilePath");
			try
			{
				File fileDir = new File(filePath);
				if(!fileDir.isDirectory())
					fileDir.mkdirs();
			}
			catch(Exception e)
			{			
				LOG.errorTrace(METHOD_NAME,e);
				LOG.error(METHOD_NAME,"Exception" + e.getMessage());
				throw new BTSLBaseException(this,METHOD_NAME,"bulkuser.processuploadedfile.downloadfile.error.dirnotcreated");
			}
			
			String _fileName = Constants.getProperty("BatchUSerModifyErLog")+BTSLUtil.getFileNameStringFromDate(new Date())+".csv";
		    String networkCode = userVO.getNetworkID();
		    newFile1=new File(filePath);
            if(! newFile1.isDirectory())
         	 newFile1.mkdirs();
             String absolutefileName=filePath+_fileName;
             fileHeader=Constants.getProperty("ERROR_FILE_HEADER_MOVEUSER");
           
             newFile = new File(absolutefileName);
             out = new OutputStreamWriter(new FileOutputStream(newFile));
             out.write(fileHeader +"\n");
             
             List<ListValueVO> unsortedList = new ArrayList<>();
             for (int i = 0, j = errorList.size(); i < j; i++) {
            	 if(errorList.get(i) instanceof  ListValueVO  ) {
            	 ListValueVO listValueVO = (ListValueVO) errorList.get(i);
            	 ListValueVO lVV= new ListValueVO();
	        	lVV.setOtherInfo(listValueVO.getOtherInfo());
	        	lVV.setOtherInfo2( listValueVO.getOtherInfo2());
	        	unsortedList.add(lVV);
            	 }else {
            		 
            		 ListValueVO lVV= new ListValueVO() ;

            		 ArrayList singleRowErrorList = (ArrayList) errorList.get(i);
            		 lVV.setOtherInfo( (singleRowErrorList.get(0).toString() ));
            		 lVV.setOtherInfo2(singleRowErrorList.get(1).toString());
            		 unsortedList.add(lVV);
            	 }
             }
             
             unsortedList.sort ((o1,o2)->Integer.parseInt(o1.getOtherInfo())-(Integer.parseInt(o2.getOtherInfo())));
    		 
           
             for (int i = 0, j = unsortedList.size(); i < j; i++) {
            	 if(unsortedList.get(i) instanceof  ListValueVO  ) {
            	 ListValueVO listValueVO = (ListValueVO) unsortedList.get(i);
	        	 out.write(listValueVO.getOtherInfo()+ ",");
	        	 out.write(listValueVO.getOtherInfo2() + ",");
            	 }
	        	 out.write(",");
	        	 out.write("\n");
	         }
	         
 			out.close();
 			File error =new File(absolutefileName);
			byte[] fileContent = FileUtils.readFileToByteArray(error);
	   		String encodedString = Base64.getEncoder().encodeToString(fileContent);	   		
	   		response.setFileAttachment(encodedString);
	   		response.setFileName(_fileName);
 			
		}
		catch (Exception e)
		{
			LOG.error(METHOD_NAME,"Exception:e="+e);
			LOG.errorTrace(METHOD_NAME,e);
		}
		finally
         {
         	if (LOG.isDebugEnabled()){
         		LOG.debug(METHOD_NAME,"Exiting... ");
         	}
             if (out!=null)
             	try{
             		out.close();
             		}
             catch(Exception e){
            	 LOG.errorTrace(METHOD_NAME, e);
             }
             	
         }
	}
	
    private void deleteUploadedFile(BulkModifyUserRequestVO request) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("deleteUploadedFile", "Entered");
        }
        final String METHOD_NAME = "deleteUploadedFile";
        String fileStr = Constants.getProperty("UPLOADMODIFYBATCHUSERFILEPATH");
        fileStr = fileStr + request.getFileName() + "." + request.getFileType();
        final File f = new File(fileStr);
        if (f.exists()) {
            try {
            	boolean isDeleted = f.delete();
                if(isDeleted){
                 LOG.debug(METHOD_NAME, "File deleted successfully");
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                LOG.error("deleteUploadedFile", "Error in deleting the uploaded file" + f.getName() + " as file validations are failed Exception::" + e);
                throw new BTSLBaseException(this, METHOD_NAME, "Exception in deleting uploaded file as file validations failed");
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("deleteUploadedFile", "Exiting:");
        }
    }

	
	private LinkedHashMap<String, List<String>> getMapInFileFormat(LinkedHashMap<String, List<String>> bulkDataMap){
		
		LinkedHashMap<String, List<String>> fileDetailsMap = new LinkedHashMap<String, List<String>>();
		List<String> fileHeader = new ArrayList(bulkDataMap.keySet());
		fileDetailsMap.put("0", fileHeader);
		List<List<String>> listGroup = bulkDataMap.values().stream().collect(Collectors.toList());
		
		for(int row = 0; row < listGroup.get(0).size(); row++) {
			ArrayList<String> rows = new ArrayList<String>();
			
			for(int col =0;  col< fileHeader.size(); col++) {
				rows.add(listGroup.get(col).get(row));
			}
			String key = String.valueOf(row + 1);
			fileDetailsMap.put(key, rows);
		}
		return fileDetailsMap;
	}
	
	public String cellValueNull(Cell cell){
    	String cellval;
    	if(cell==null){
    		cellval="";
		}
    	else{
    		cellval = cell.getStringCellValue();
    	}
    	return cellval;
    }
	
    //define a runnable task for processing defined number of records
    private class UpdateRecordsInDB implements Runnable {
		private Connection innerCon=null;
		private MComConnectionI innermcomCon = null;
		private ArrayList<ChannelUserVO> channelUserList = null;
		private ArrayList fileErrorList = null;
		private String domCode = null;
		private MessageResources msgRsc = null;
		private Locale locale = null;
		private BatchUserWebDAO batchUserWebDAO = null;
		private String fileName = null;
		public UpdateRecordsInDB(ArrayList<ChannelUserVO> list,ArrayList fileErrorList, BatchUserWebDAO batchUserWebDAO, String domCode, Locale locale, String fileName) {
			this.channelUserList = list;
			this.fileErrorList = fileErrorList;
			this.batchUserWebDAO = batchUserWebDAO;
			this.domCode = domCode;
			this.locale = locale;
			this.fileName = fileName;   		
		}
	
		@Override
		public void run() {
			final String METHOD_NAME = "run";
			try { double startTime = System.currentTimeMillis();
				if(innermcomCon==null){ 
					innermcomCon = new MComConnection();
					innerCon = innermcomCon.getConnection();
				}
				ArrayList dbErrorList = batchUserWebDAO.modifyBulkChannelUserList(innerCon, channelUserList, domCode, locale, fileName);
				innerCon.commit();
				synchronized (this) {
					fileErrorList.addAll(dbErrorList);
				}
				LOG.debug("UpdateRecordsInDB","Hey ASHU thread "+Thread.currentThread().getName()+" processed "+channelUserList.size()+" records, time taken = "+(System.currentTimeMillis()-startTime)+" ms");
				this.channelUserList.clear();
				this.channelUserList = null;
	        	Runtime runtime = Runtime.getRuntime();
	            long memory = runtime.totalMemory() - runtime.freeMemory();
	            LOG.debug("run","Used memory in megabytes before gc: " + (memory)/1048576);
	            // Run the garbage collector
	            runtime.gc();
	            // Calculate the used memory
	            memory = runtime.totalMemory() - runtime.freeMemory();
	            LOG.debug("run","Used memory in megabytes after gc: " + (memory)/1048576);
			} catch (BTSLBaseException e1) {
				LOG.error(METHOD_NAME, "BTSLBaseException:e=" + e1);
				LOG.errorTrace(METHOD_NAME, e1);
			} catch (SQLException e) {
				LOG.error(METHOD_NAME, "SQLException:e=" + e);
				LOG.errorTrace(METHOD_NAME, e);
			} finally {
				if (innermcomCon != null) {
					innermcomCon.close("BatchUserUpdateAction#UpdateRecordsInDB");
					innermcomCon = null;
				}
			}
		}
    }
}