package com.restapi.channeluser.service;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EmailSendToUser;
import com.btsl.common.ErrorMap;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.AreaData;
import com.btsl.pretups.channel.transfer.businesslogic.AreaSearchAdminRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.AreaSearchRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.AreaSearchResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchUserInitiateRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchUserInitiateResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.SAPResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserDeletionBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListService;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListServiceImpl;
import com.btsl.pretups.channel.user.businesslogic.BatchUserDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
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
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.transfer.businesslogic.errorfilerequest.ErrorFileRequestVO;
import com.btsl.pretups.transfer.businesslogic.errorfileresponse.ErrorFileResponse;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.ExtUserDAO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.user.requesthandler.ChannelSOSSettlementHandler;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
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
import com.restapi.staffuser.StaffUserServiceImpl;
import com.restapi.users.logiid.LoginIdResponseVO;
import com.web.pretups.channel.transfer.businesslogic.BatchO2CTransferWebDAO;
import com.web.pretups.channel.user.businesslogic.BatchUserWebDAO;
import com.web.pretups.channel.user.businesslogic.ChannelUserTransferWebDAO;
import com.web.pretups.channel.user.web.BatchUserForm;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;
import com.web.user.web.UserForm;

//import jdk.nashorn.internal.ir.RuntimeNode.Request;

@Service("AreaSearchServiceI")
public class ChannelUserServicesImpl implements ChannelUserServicesI{

	public static final Log log = LogFactory.getLog(StaffUserServiceImpl.class.getName());
	public static final String  classname = "AreaSearchServiceImpl";
	private UserDAO userDAO = null;
	private UserWebDAO userwebDAO=null;
	private ChannelUserDAO channelUserDao = null;
	private ChannelUserWebDAO channelUserWebDao = null;
	private ExtUserDAO extUserDao = null;
	private ChannelUserVO senderVO = null;
	private final GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
	private ArrayList<AreaData> areaList = null; // to be used by searchArea()
	
	
	@Override
	public AreaSearchResponseVO searchArea(String loginId, Connection con, AreaSearchRequestVO requestVO,
			HttpServletResponse responseSwag) {
		
		final String METHOD_NAME = "searchArea";
	     if (log.isDebugEnabled()) {
	         log.debug(METHOD_NAME, "Entered");
	     }
	     
	     NetworkPrefixVO networkPrefixVO = null;
		 ServicesTypeDAO servicesDAO = null;
		 String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		 String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		 Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
		 UserPhoneVO phoneVO = null;
		 channelUserDao = new ChannelUserDAO();
		 channelUserWebDao = new ChannelUserWebDAO();
		 userDAO = new UserDAO();
		 extUserDao = new ExtUserDAO();
		 AreaSearchResponseVO response = new AreaSearchResponseVO();
		 OperatorUtilI operatorUtili = new OperatorUtil();
		 String senderPin = "";
	     String webPassword = null;
	     String randomPwd = null;
	     String defaultGeoCode = "";
	     final String fromTime = "00:00";
	     final String toTime = "23:59";
		
	     
	     try {
	    	 
	    	 final CategoryDAO categoryDAO = new CategoryDAO();
	    	 final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
	    	 final UserVO userSessionVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
	    	 final UserVO parentVO = userDAO.loadAllUserDetailsByLoginID(con, requestVO.getParentLoginId());
	    	 final UserForm theForm = new UserForm();
	    	 final GeographicalDomainDAO geographyDAO = new GeographicalDomainDAO();
	         Boolean isGeographyListSet = true;
	         String userIdForSearch = null;
	         ArrayList geographyList = null;
	         Integer hierarchyLength = 1;  
	     
	         
	         //Author: Priyank Sharma
	         
	         if(parentVO==null) {
	        	 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PARENT_USER_NOT_FOUND, new String[] {requestVO.getParentLoginId()});
	         }
	         //setting form req type
	         theForm.setRequestType(requestVO.getRequestType());
	         
	         //settring domain code
	         theForm.setDomainCode(requestVO.getDomainCode());
	         
	         //setting form userid
	         theForm.setUserId(parentVO.getUserID());
	         
	         //setting theform.categoryVO
	         if (PretupsI.OPERATOR_TYPE_OPT.equals(userSessionVO.getDomainID())) {
	        	 theForm.setOrigCategoryList(categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT));
	         }else {
	        	 theForm.setOrigCategoryList(categoryWebDAO.loadCategorListByDomainCode(con, userSessionVO.getDomainID()));
	         }

	         if (theForm.getOrigCategoryList() != null) {
                 CategoryVO vo = null;
                 // parentID is the combination of categoryCode, Domain Code
                 // and sequenceNo
                 theForm.setCategoryCode(requestVO.getCategoryCode());
                 for (int i = 0, j = theForm.getOrigCategoryList().size(); i < j; i++) {
                     vo = (CategoryVO) theForm.getOrigCategoryList().get(i);
//                     System.out.println(vo.getCategoryCode()+" "+vo.getCategoryName());
                     if (vo.getCategoryCode().equalsIgnoreCase(requestVO.getCategoryCode())) {
                         theForm.setCategoryVO(vo);
                         break;
                     }
                 }
             }
	         
	         if(theForm.getCategoryVO()==null) {
	        	 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CATEGORY_NOT_EXIST);
	         }
	         
	         //setting theform.categoryList
	         if(!PretupsI.OPERATOR_TYPE_OPT.equals(userSessionVO.getDomainID())){
	        	 final ArrayList categoryList = theForm.getOrigCategoryList();
	        	 if ( categoryList != null) {
	                    CategoryVO categoryVO = null;
	                    final ArrayList list = new ArrayList();
	                    for (int i = 0, j = categoryList.size(); i < j; i++) {
	                        categoryVO = (CategoryVO) categoryList.get(i);
	                        // added by vikas (if in system preferences
	                        // PRF_ASSOCIATE_AGENT flag is true then user can modify
	                        // his agent only )
	                        // if value of flag is false then user can modify all
	                        // the user's below in the hierarchy
	                        if ("associate".equals(theForm.getRequestType()) && SystemPreferences.PROFILEASSOCIATE_AGENT_PREFERENCES) {
	                            if ((categoryVO.getSequenceNumber() == userSessionVO.getCategoryVO().getSequenceNumber() + 1) && PretupsI.AGENTCATEGORY.equals(categoryVO
	                                            .getCategoryType())) {
	                                list.add(categoryVO);
	                            }

	                        }

	                        else if ("associateOther".equals(theForm.getRequestType()) && SystemPreferences.PROFILEASSOCIATE_AGENT_PREFERENCES) {
	                            if ((categoryVO.getSequenceNumber() == userSessionVO.getCategoryVO().getSequenceNumber() + 1) && PretupsI.AGENTCATEGORY.equals(categoryVO
	                                            .getCategoryType())) {
	                                list.add(categoryVO);
	                            }

	                        } else if (categoryVO.getSequenceNumber() > userSessionVO.getCategoryVO().getSequenceNumber()) {
	                            list.add(categoryVO);
	                        }
	                    }
	                    theForm.setCategoryList(list);
	              }
	        	 
	         }else {//if operator user has logged in
	        	 final ArrayList list = new ArrayList();
	        	 if (theForm.getOrigCategoryList() != null && !BTSLUtil.isNullString(theForm.getDomainCode())) {
	                 CategoryVO categoryVO = null;
	                 for (int i = 0, j = theForm.getOrigCategoryList().size(); i < j; i++) {
	                     categoryVO = (CategoryVO) theForm.getOrigCategoryList().get(i);
	                     // here value is the combination of categoryCode,domain_code
	                     // and sequenceNo so we split the value
	                     if (categoryVO.getDomainCodeforCategory().equals(theForm.getDomainCode())) {
	                         list.add(categoryVO);
	                     }
	                 }
	             }
	             theForm.setCategoryList(list);
	             if(list.size()==0) {
	            	 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DOMAIN_NOT_EXIST);
	             }
	         }
	         
	         //setting theForm.searchList
	         if(theForm.getCategoryVO().getSequenceNumber()==1) {// if the user we are creating is on top of his chain, then no need to search
	        	 //do nothing
	         }else if(!PretupsI.OPERATOR_TYPE_OPT.equals(userSessionVO.getDomainID()) && parentVO.getCategoryVO().getSequenceNumber() == userSessionVO.getCategoryVO()
                     .getSequenceNumber()) {//if user who has logged in is channel user and the parent of user we are creating is same as who has logged in: then no need to search
	        	 //do nothing
	         }else {
	        	 if(parentVO.getCategoryVO().getSequenceNumber() == 1 && !PretupsI.OPERATOR_TYPE_OPT.equals(userSessionVO.getDomainID())) {//if parent of user we are creating is top of chain and operator user has not logged in: no need to search
	        		 //do nothing
	        	 }else {
	        		 // here will have to create search list
	        		 final ArrayList list = new ArrayList();
                     CategoryVO categoryVO = null;
                     
                     if (PretupsI.OPERATOR_TYPE_OPT.equals(userSessionVO.getDomainID())) {
                         for (int i = 0, j = theForm.getCategoryList().size(); i < j; i++) {
                             categoryVO = (CategoryVO) theForm.getCategoryList().get(i);
                             if (categoryVO.getSequenceNumber() == 1) {
                                 list.add(categoryVO);
                             }

                             if (categoryVO.getSequenceNumber() != 1 && parentVO.getCategoryVO().getSequenceNumber() == categoryVO.getSequenceNumber()) {
                                 list.add(categoryVO);
                             }
                         }
                     } else {
                         for (int i = 0, j = theForm.getOrigCategoryList().size(); i < j; i++) {
                             categoryVO = (CategoryVO) theForm.getOrigCategoryList().get(i);
                             if (categoryVO.getSequenceNumber() == 1) {
                                 list.add(categoryVO);
                             }

                             if ( categoryVO.getSequenceNumber() != 1 && parentVO.getCategoryVO().getSequenceNumber() == categoryVO.getSequenceNumber() ) {
                                 list.add(categoryVO);
                             }
                         }
                     }
                     theForm.setSearchList(list);
                     theForm.setSearchTextArraySize();
                     theForm.setSearchUserIdSize();
                     theForm.setDistributorSearchFlagSize();
                     if (!PretupsI.OPERATOR_TYPE_OPT.equals(userSessionVO.getDomainID())) {
                         theForm.setOwnerID(userSessionVO.getOwnerID());
                         theForm.setDistributorSearchFlagIndexed(0, "true");
                         theForm.setSearchTextArrayIndexed(0, userSessionVO.getOwnerName());
                         theForm.setSearchUserIdIndexed(0, userSessionVO.getUserID());
                     }
	        	 }
	         }
	         
	         //setting associated geographical list and geographical list
	         	final C2STransferDAO c2STransferDAO = new C2STransferDAO();
	         	theForm.setOrigParentCategoryList(c2STransferDAO.loadC2SRulesListForChannelUserAssociation(con, userSessionVO.getNetworkID()));
	            theForm.setParentCategoryList(null);

	            final ArrayList geolist = userSessionVO.getGeographicalAreaList();
	            theForm.setAssociatedGeographicalList(geolist);
	            /*
	             * if list size greater than 1, means user associated with multiple
	             * geographies
	             * e.g BCU assosciated with multiple zones so first we need to
	             * select the zone
	             * first, so here we set on the form for user selction
	             */
	            
	            if(requestVO.getGeoDomainCode()!=null) {
	            	for(Object vo : theForm.getAssociatedGeographicalList()) {
	            		UserGeographiesVO geoVO = (UserGeographiesVO) vo;
	            		if(!geoVO.getGraphDomainCode().trim().equals(requestVO.getGeoDomainCode().trim())) continue;
	            		theForm.setParentDomainCode(geoVO.getGraphDomainCode());
		                theForm.setParentDomainDesc(geoVO.getGraphDomainName());
		                theForm.setParentDomainTypeDesc(geoVO.getGraphDomainTypeName());
	            	}
	            	if(theForm.getParentDomainTypeDesc()==null) {
	            		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.GEOGRAPHY_DOMAIN_NOT_EXIST); 
	            	}
	            }
//	            theForm.getParentDomainCode()
	            if (userSessionVO.getCategoryVO().getGrphDomainType().equals(theForm.getCategoryVO().getGrphDomainType())) {
                    if (theForm.getAssociatedGeographicalList() != null && theForm.getAssociatedGeographicalList().size() > 1) {
                        theForm.setGeographicalList(theForm.getAssociatedGeographicalList());

                        /*
                         * check whether the user has mutiple geographical
                         * area or not
                         * if multiple then set into the zoneCode array
                         * else set into the zone code
                         */
                        UserGeographiesVO geographyVO = null;
                        if (TypesI.YES.equals(theForm.getCategoryVO().getMultipleGrphDomains())) {
                            final String[] arr = new String[1];
                            for (int i = 0, j = theForm.getAssociatedGeographicalList().size(); i < j; i++) {
                                geographyVO = (UserGeographiesVO) theForm.getAssociatedGeographicalList().get(i);
                                if (geographyVO.getGraphDomainCode().equals(theForm.getParentDomainCode())) {
                                    arr[0] = geographyVO.getGraphDomainCode();
                                    theForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
                                    break;
                                }
                            }
                            theForm.setGeographicalCodeArray(arr);
                        } else {
                            for (int i = 0, j = theForm.getAssociatedGeographicalList().size(); i < j; i++) {
                                geographyVO = (UserGeographiesVO) theForm.getAssociatedGeographicalList().get(i);
                                if (geographyVO.getGraphDomainCode().equals(theForm.getParentDomainCode())) {
                                    // theForm.setGeographicalCode(geographyVO.getGraphDomainCode());
                                    theForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
                                    break;
                                }
                            }
                        }
                    }
                }else {
                	theForm.setGrphDomainTypeName(theForm.getCategoryVO().getGrphDomainTypeName());
                }

	            ArrayList<AreaData> geoDataList = new ArrayList<>();
	        
	         /*
	             * 1)if sequence no is 1 means category is Network Admin
	             * load the session network details
	             * 2)If search list is null(No search has performed session user is
	             * the parent user)
	             * a)if graph_domain_type of the session user and added user are
	             * same
	             * then the geography list of the new user = session user
	             * geographies list
	             * 
	             * b)load the list of all geographies on the basis of parent domain
	             * code
	             * c)need to perform search, so prepare the list for search
	             * 3)If ParentId is null(No search has performed session user is the
	             * parent user)
	             * d)if graph_domain_type of the searched user and added user are
	             * same
	             * then the geography list of the new user = searched user
	             * geographies list
	             * 
	             * e)load the list of all geographies on the basis of domain code of
	             * the searched user
	             * f)need to perform search, so prepare the list for search
	             */
	            // 1
	            if(theForm.getCategoryVO().getGrphDomainSequenceNo() == 1) {
	                UserGeographiesVO geographyVO = null;
	                geographyList = new ArrayList();
	                geographyVO = new UserGeographiesVO();
	                geographyVO.setGraphDomainCode(userSessionVO.getNetworkID());
	                geographyVO.setGraphDomainName(userSessionVO.getNetworkName());
	                geographyVO.setGraphDomainTypeName(userSessionVO.getCategoryVO().getGrphDomainTypeName());
	                theForm.setGrphDomainTypeName(userSessionVO.getCategoryVO().getGrphDomainTypeName());
	                geographyList.add(geographyVO);
	                theForm.setGeographicalList(geographyList);
	                
	                //setting hierarchy response
	                AreaData areaData = new AreaData();
	                areaData.setGeoCode(userSessionVO.getNetworkID());
	                areaData.setGeoName(userSessionVO.getNetworkName());
	                areaData.setGeoDomainName(theForm.getCategoryVO().getGrphDomainTypeName());
	                areaData.setGeoDomainSequenceNo("1");
	                geoDataList.add(areaData);
	                hierarchyLength = 1; // 
	            }
	            // 2
	            else if (theForm.getSearchList() == null || theForm.getSearchList().isEmpty()) {
	                // a
	                if (userSessionVO.getCategoryVO().getGrphDomainType().equals(theForm.getCategoryVO().getGrphDomainType())) {
	                    geographyList = userSessionVO.getGeographicalAreaList();
	                    theForm.setGeographicalList(geographyList);
	                    if (geographyList != null && geographyList.size() > 0) {
	                        /*
	                         * set the grphDoaminTypeName on the form
	                         * GrphDomainTypeName is same for all VO's in list
	                         */
	                        final UserGeographiesVO geographyVO = (UserGeographiesVO) geographyList.get(0);
	                        theForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
	                    }
	                    
	                    //setting hierarchy response
	                    geoDataList = setHierarchicalResponse(con, geographyList, theForm, userSessionVO.getParentID());
	                    	
	                    }
	                    
	                
	                // b
	                else if ((userSessionVO.getCategoryVO().getGrphDomainSequenceNo() + 1) == theForm.getCategoryVO().getGrphDomainSequenceNo()) {
	                    geographyList = geographicalDomainWebDAO.loadGeographyList(con, userSessionVO.getNetworkID(), theForm.getParentDomainCode(), "%");
	                    theForm.setGeographicalList(geographyList);
	                    if (geographyList != null && geographyList.size() > 0) {
	                        /*
	                         * set the grphDoaminTypeName on the form
	                         * GrphDomainTypeName is same for all VO's in list
	                         */
	                        final UserGeographiesVO geographyVO = (UserGeographiesVO) geographyList.get(0);
	                        theForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
	                    }
	                    
	                  //setting hierarchy response
	                    geoDataList = setHierarchicalResponse(con, geographyList, theForm, userSessionVO.getUserID());
	                    
	                }
	                // c
	                else {
	                    ArrayList list = geographicalDomainWebDAO.loadDomainTypes(con, userSessionVO.getCategoryVO().getGrphDomainSequenceNo(), theForm.getCategoryVO()
	                                    .getGrphDomainSequenceNo());

	                    if (list != null && list.size() > 0) {
	                        theForm.setDomainSearchList(list);
	                        theForm.setSearchDomainTextArrayCount();
	                        theForm.setSearchDomainCodeCount();
	                        isGeographyListSet = false;
	                        userIdForSearch = userSessionVO.getUserID();
	                    }
	                    
	                    // Again load geography for fix the bug hierchy transfer
	                    // by santanu mohanty

	                    if (!BTSLUtil.isNullString(theForm.getSearchMsisdn()) || !BTSLUtil.isNullString(theForm.getSearchLoginId())) {
	                        String parentId = parentVO.getUserID();//self change
	                        if (PretupsI.ROOT_PARENT_ID.equals(parentId)) {
	                            parentId = theForm.getUserId();
	                        }
	                        final ArrayList parentUserGeographyList = geographyDAO.loadUserGeographyList(con, parentId, userSessionVO.getNetworkID());
	                        final String geoCode = ((UserGeographiesVO) parentUserGeographyList.get(0)).getGraphDomainCode();
	                        list = geographyDAO.loadGeoDomainCodeHeirarchy(con, theForm.getCategoryVO().getGrphDomainType(), geoCode, true);
	                        if (list != null) {
	                            final ArrayList oldList = theForm.getGeographicalList();
	                            final ArrayList finalList = new ArrayList();
	                            UserGeographiesVO geographyVO = null;
	                            GeographicalDomainVO geographicalDomainVO = new GeographicalDomainVO();
	                            for (int i = 0, j = list.size(); i < j; i++) {
	                                geographyVO = new UserGeographiesVO();
	                                geographicalDomainVO = (GeographicalDomainVO) list.get(i);
	                                geographyVO.setGraphDomainCode(geographicalDomainVO.getGrphDomainCode());
	                                geographyVO.setGraphDomainName(geographicalDomainVO.getGrphDomainName());
	                                geographyVO.setGraphDomainType(geographicalDomainVO.getGrphDomainType());
	                                if (oldList != null) {
	                                    geographyVO.setGraphDomainTypeName(((UserGeographiesVO) oldList.get(0)).getGraphDomainTypeName());
	                                    geographyVO.setGraphDomainSequenceNumber(((UserGeographiesVO) oldList.get(0)).getGraphDomainSequenceNumber());
	                                }
	                                finalList.add(geographyVO);
	                            }
	                            theForm.setGeographicalList(finalList);
	                        }
	                    }// end by santanu

	                }
	            
	            }// 3
	       else {// search was performed
	            	
	            	
	                String parentId = parentVO.getUserID();// self change      
	                
	                // load the geographies info from the user_geographies of the
	                // parent user(searched user)
	                final ArrayList parentUserGeographyList = geographyDAO.loadUserGeographyList(con, parentId, userSessionVO.getNetworkID());

	                UserGeographiesVO geographyVO = null;
	                if (parentUserGeographyList != null && parentUserGeographyList.size() > 0) {
	                    for (int i = 0, j = parentUserGeographyList.size(); i < j; i++) {
	                        geographyVO = (UserGeographiesVO) parentUserGeographyList.get(i);
	                        // System.out.println("AVVV "+
	                        // geographyVO.getGraphDomainCode()+" theForm="+theForm.getParentDomainCode());
	                        if (geographyVO.getGraphDomainCode().equals(theForm.getParentDomainCode())) {
	                            break;
	                        }
	                    }
	                    if (geographyVO.getGraphDomainType().equals(theForm.getCategoryVO().getGrphDomainType())) {
	                        theForm.setGeographicalList(parentUserGeographyList);
	                        /*
	                         * set the grphDoaminTypeName on the form
	                         * GrphDomainTypeName is same for all VO's in list
	                         */
	                        theForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
	                        
	                      //setting hierarchy response
			              geoDataList = setHierarchicalResponse(con, parentUserGeographyList, theForm, parentVO.getParentID());

	                    }
	                    // e
	                    else if ((geographyVO.getGraphDomainSequenceNumber() + 1) == theForm.getCategoryVO().getGrphDomainSequenceNo()) {
	                        geographyList = geographicalDomainWebDAO.loadGeographyList(con, userSessionVO.getNetworkID(), geographyVO.getGraphDomainCode(), "%");
	                        theForm.setGeographicalList(geographyList);
	                        if (geographyList != null && geographyList.size() > 0) {
	                            /*
	                             * set the grphDoaminTypeName on the form
	                             * GrphDomainTypeName is same for all VO's in list
	                             */
	                            geographyVO = (UserGeographiesVO) geographyList.get(0);
	                            theForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
	                        }
	                        
	                      //setting hierarchy response
		                  geoDataList = setHierarchicalResponse(con, geographyList, theForm, parentVO.getUserID());
	                    }
	                    // f
	                    else {
	                        final ArrayList list = geographicalDomainWebDAO.loadDomainTypes(con, geographyVO.getGraphDomainSequenceNumber(), theForm.getCategoryVO()
	                                        .getGrphDomainSequenceNo());

	                        if (list != null && list.size() > 0) {
	                        	isGeographyListSet = false;
	                        	userIdForSearch = parentId;
	                            theForm.setParentDomainCode(geographyVO.getGraphDomainCode());
	                            theForm.setDomainSearchList(list);
	                            theForm.setSearchDomainTextArrayCount();
	                            theForm.setSearchDomainCodeCount();
	                        }
	                    }

	                }// end of if(parentUserGeographyList!=null)
	                else {
	                	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PARENT_GEOGRAPHY_NOT_EXIST);
	                }
	            }// end of else
	             // change 1 - Bug No 9 start
	            if ("add".equals(theForm.getRequestType()) && theForm.getGeographicalListCount() > 0) {
	                final UserGeographiesVO geographyVO = (UserGeographiesVO) theForm.getGeographicalList().get(0);
	                theForm.setGeographicalCode(geographyVO.getGraphDomainCode());
	            }

	         if(theForm.getGeographicalList()!=null && theForm.getGeographicalList().size()>0) {
		         response.setAreaList(geoDataList);
	         }
	         if(isGeographyListSet==false) { //here will have to search for child geographies and also set parent geographies for response
	        	 
	        	 // loading geographies for current grph seq number
	        	 ArrayList graphDomainList = geographicalDomainWebDAO.loadGeographyList(con, userSessionVO.getNetworkID(), theForm.getParentDomainCode(), "%%");
	        	 
	        	 //setting parent hierarchy
	        	 ArrayList<AreaData> geoDataSearchList = setHierarchicalResponse(con, graphDomainList, theForm, userIdForSearch);
	        	 
	        	 //searching for child geo data
	        	 childGeoDataSearch(con, graphDomainList, theForm, userSessionVO, areaList, areaList.get(0).getGeoDomainSequenceNo());
	        	 
	        	 //setting hierarchy length
	        	 hierarchyLength = setHierarchyLength(geoDataSearchList, hierarchyLength);
	        	 
		         response.setAreaList(geoDataSearchList);
	         }
//	         theForm.getGeographicalList()
	         if(response.getAreaList()!=null) {
	        	 response.setStatus(200);
	             response.setMessageCode(PretupsErrorCodesI.AREA_SEARCH_SUCCESS);
	             String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.AREA_SEARCH_SUCCESS,new String[] {""});
	             response.setMessage(msg);
	             response.setHierarchyLength(hierarchyLength);
	         }else{
	        	 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PARENT_GEOGRAPHY_NOT_EXIST);
	         }
	         
	         }catch(BTSLBaseException be) {
		        log.error("processFile", "Exceptin:e=" + be);
		        log.errorTrace(METHOD_NAME, be);
	       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),be.getArgs());
		        response.setMessageCode(be.getMessageKey());
		        response.setMessage(msg);
	        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	        		 responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    	         response.setStatus(401);
	            }
	           else{
	        	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	           		response.setStatus(400);
	           }
	        }catch (Exception e) {
	            log.debug("processFile", e);
	            response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
	            String resmsg = RestAPIStringParser.getMessage(
	    				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
	    				null);
	            response.setMessage(resmsg);
	            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	        	response.setStatus(400);
	        	
	    	}
	    
	     return response;
	     }
	
	@Override
	public AreaSearchResponseVO searchAreaAdmin(String loginId, Connection con, AreaSearchAdminRequestVO requestVO,
			HttpServletResponse responseSwag) throws BTSLBaseException, Exception {

		final String METHOD_NAME = "searchAreaAdmin";
	     if (log.isDebugEnabled()) {
	         log.debug(METHOD_NAME, "Entered");
	     }
		
		final UserForm theForm = new UserForm();
		AreaSearchResponseVO areaSearchResponseVO = new AreaSearchResponseVO();
		UserDAO userDao = new UserDAO();
		CategoryDAO categoryDAO = new CategoryDAO();
		DomainDAO domainDAO = new DomainDAO();
		UserVO userSessionVO = userDao.loadAllUserDetailsByLoginID(con, loginId);
		
		if (userSessionVO == null) {
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.USER_NOT_FOUND,
					new String[] { loginId });
		}
		
		if(!PretupsI.OPERATOR_TYPE_OPT.equals(userSessionVO.getDomainID())) {
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_USERADD_OPTLOGIN_NOT_OPERATOR ,new String[]{loginId}) ;
		}

		if ("edit".equalsIgnoreCase(requestVO.getRequestType())) {
			theForm.setRequestType("edit");
			if (BTSLUtil.isNullString(requestVO.getSearchLoginId())) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,
						new String[] { "Search Login ID" });
			}
			UserVO searchUserVO = userDao.loadAllUserDetailsByLoginID(con, requestVO.getSearchLoginId());
			if (searchUserVO == null) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.USER_NOT_FOUND,
						new String[] { requestVO.getSearchLoginId() });
			}
			this.isUserInGeoDomain(userSessionVO, searchUserVO, con);

			theForm.setParentID(searchUserVO.getParentID());
			theForm.setUserId(searchUserVO.getUserID());
			CategoryVO categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con, searchUserVO.getCategoryCode());
			if (categoryVO == null) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CATEGORY_DETAILS_NOT_FOUND);
			}
			theForm.setCategoryVO(categoryVO);
			theForm.setSearchLoginId(requestVO.getSearchLoginId());
			theForm.setGeographicalList(searchUserVO.getGeographicalAreaList());

		} else if ("add".equalsIgnoreCase(requestVO.getRequestType())) {
			boolean domainFlag = false;
			boolean categoryFlag = false;
			boolean geoDomainFlag = false;

			if(BTSLUtil.isNullString(requestVO.getDomainCode())) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED ,new String[]{"domain"}) ;
			}
			if(BTSLUtil.isNullString(requestVO.getCategoryCode())) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED ,new String[]{"category"}) ;
			}
			
			if (userSessionVO.getDomainID()!=null &&  !PretupsI.OPERATOR_TYPE_OPT.equalsIgnoreCase(userSessionVO.getDomainID()) )   {
				ArrayList sessionUserDomainList = userSessionVO.getDomainList();
				for (ListValueVO listValueVO : (ArrayList<ListValueVO>) sessionUserDomainList) {
					if (listValueVO.getValue().equals(requestVO.getDomainCode())) {
						domainFlag = true;
					}
				}
			
			if (!domainFlag) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.INVALID_DOMAIN_OR_EMTPY) ;

			}
			}else {
				
				DomainVO  domainVO =domainDAO.loadDomainVO(con, requestVO.getDomainCode());
				if(BTSLUtil.isNullObject(domainVO)) {
					throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.INVALID_DOMAIN_OR_EMTPY) ;
				}
				
			}
			CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
			ArrayList<CategoryVO> categoryList = categoryWebDAO.loadCategorListByDomainCode(con,
					requestVO.getDomainCode());
			for (CategoryVO categoryVO : categoryList) {
				if (categoryVO.getCategoryCode().equals(requestVO.getCategoryCode())) {
					theForm.setCategoryVO(categoryVO);
					categoryFlag = true;
				}
			}
			if (!categoryFlag) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.INVALID_CATEGORY_OR_EMTPY) ;
			}
			
			/*If sequence number is one one that means we are creating top hierarchy user
			 * and parent id is not required in that case
			 * If parent id is provided then we will fetch allowed geoDomain from that only
			 */
			if(theForm.getCategoryVO().getSequenceNumber() == 1) {
				if(BTSLUtil.isNullString(requestVO.getGeoDomainCode())) {
						throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED ,new String[]{"Geo Domain"}) ;
				}
				ArrayList<UserGeographiesVO> sessionUserGeographyList = userSessionVO.getGeographicalAreaList();
				for (UserGeographiesVO userGeographiesVO : sessionUserGeographyList) {
					if (userGeographiesVO.getGraphDomainCode().equals(requestVO.getGeoDomainCode())) {
						geoDomainFlag = true;
					}
				}
				if (!geoDomainFlag) {
					throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.INVALID_GEOGRAPHY_OR_EMTPY) ;
				}
				theForm.setParentDomainCode(requestVO.getGeoDomainCode());
			}else {
				if(BTSLUtil.isNullString(requestVO.getParentLoginId())) {
						throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED ,new String[]{"Parent login id"}) ;
				}
				UserVO searchParentUserVO = userDao.loadAllUserDetailsByLoginID(con, requestVO.getParentLoginId());
				if(searchParentUserVO == null) {
					
				}
				ArrayList<CategoryVO> allowedParentCategories = getParentCategoryList(userSessionVO,requestVO.getCategoryCode(), con);
				boolean parentCategoryAllowed = false;
				for(CategoryVO parentCategoryVO : allowedParentCategories) {
					if(parentCategoryVO.getCategoryCode().equals(searchParentUserVO.getCategoryCode())) {
						parentCategoryAllowed = true;
					}
				}
				if(!parentCategoryAllowed) {
						throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PARENT_CATEGORY_INVALID) ;
				}
				this.isUserInGeoDomain(userSessionVO, searchParentUserVO, con);
				ArrayList<String> searchList = new ArrayList<String>();
				searchList.add(searchParentUserVO.getUserID());
				theForm.setSearchList(searchList);
			}
			theForm.setRequestType("add");
		} else {
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.INVALID_REQUEST_TYPE) ;
		}
		
		areaSearchResponseVO.setOutletAllow(theForm.getCategoryVO().getOutletsAllowed());

		// starting copied code
		final GeographicalDomainDAO geographyDAO = new GeographicalDomainDAO();
		final GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
		ArrayList geographyList = null;

		/*
		 * 1)if sequence no is 1 means category is Network Admin load the session
		 * network details 2)If search list is null(No search has performed session user
		 * is the parent user) a)if graph_domain_type of the session user and added user
		 * are same then the geography list of the new user = session user geographies
		 * list
		 * 
		 * b)load the list of all geographies on the basis of parent domain code c)need
		 * to perform search, so prepare the list for search 3)If ParentId is null(No
		 * search has performed session user is the parent user) d)if graph_domain_type
		 * of the searched user and added user are same then the geography list of the
		 * new user = searched user geographies list
		 * 
		 * e)load the list of all geographies on the basis of domain code of the
		 * searched user f)need to perform search, so prepare the list for search
		 */
		// 1
		if (theForm.getCategoryVO().getGrphDomainSequenceNo() == 1) {
			UserGeographiesVO geographyVO = null;
			geographyList = new ArrayList();
			geographyVO = new UserGeographiesVO();
			geographyVO.setGraphDomainCode(userSessionVO.getNetworkID());
			geographyVO.setGraphDomainName(userSessionVO.getNetworkName());
			geographyVO.setGraphDomainTypeName(userSessionVO.getCategoryVO().getGrphDomainTypeName());
			theForm.setGrphDomainTypeName(userSessionVO.getCategoryVO().getGrphDomainTypeName());
			geographyList.add(geographyVO);

			theForm.setGeographicalList(geographyList);
		}
		// 2
		else if (theForm.getSearchList() == null || theForm.getSearchList().isEmpty()) {
			// a
			if (userSessionVO.getCategoryVO().getGrphDomainType().equals(theForm.getCategoryVO().getGrphDomainType())) {
				geographyList = userSessionVO.getGeographicalAreaList();
				theForm.setGeographicalList(geographyList);
				if (geographyList != null && geographyList.size() > 0) {
					/*
					 * set the grphDoaminTypeName on the form GrphDomainTypeName is same for all
					 * VO's in list
					 */
					final UserGeographiesVO geographyVO = (UserGeographiesVO) geographyList.get(0);
					theForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
				}
			}
			// b
			else if ((userSessionVO.getCategoryVO().getGrphDomainSequenceNo() + 1) == theForm.getCategoryVO()
					.getGrphDomainSequenceNo()) {
				geographyList = geographicalDomainWebDAO.loadGeographyList(con, userSessionVO.getNetworkID(),
						theForm.getParentDomainCode(), "%");
				theForm.setGeographicalList(geographyList);
				if (geographyList != null && geographyList.size() > 0) {
					/*
					 * set the grphDoaminTypeName on the form GrphDomainTypeName is same for all
					 * VO's in list
					 */
					final UserGeographiesVO geographyVO = (UserGeographiesVO) geographyList.get(0);
					theForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
				}
			}
			// c
			else {
				ArrayList list = geographicalDomainWebDAO.loadDomainTypes(con,
						userSessionVO.getCategoryVO().getGrphDomainSequenceNo(),
						theForm.getCategoryVO().getGrphDomainSequenceNo());

				if (list != null && list.size() > 0) {
					theForm.setDomainSearchList(list);
					theForm.setSearchDomainTextArrayCount();
					theForm.setSearchDomainCodeCount();
				}
				// Again load geography for fix the bug hierchy transfer
				// by santanu mohanty

				if (!BTSLUtil.isNullString(theForm.getSearchMsisdn())
						|| !BTSLUtil.isNullString(theForm.getSearchLoginId())) {
					String parentId = theForm.getParentID();
					if (PretupsI.ROOT_PARENT_ID.equals(parentId)) {
						parentId = theForm.getUserId();
					}
					final ArrayList parentUserGeographyList = geographyDAO.loadUserGeographyList(con, parentId,
							userSessionVO.getNetworkID());
					final String geoCode = ((UserGeographiesVO) parentUserGeographyList.get(0)).getGraphDomainCode();
					list = geographyDAO.loadGeoDomainCodeHeirarchy(con, theForm.getCategoryVO().getGrphDomainType(),
							geoCode, true);
					if (list != null) {
						final ArrayList oldList = theForm.getGeographicalList();
						final ArrayList finalList = new ArrayList();
						UserGeographiesVO geographyVO = null;
						GeographicalDomainVO geographicalDomainVO = new GeographicalDomainVO();
						for (int i = 0, j = list.size(); i < j; i++) {
							geographyVO = new UserGeographiesVO();
							geographicalDomainVO = (GeographicalDomainVO) list.get(i);
							geographyVO.setGraphDomainCode(geographicalDomainVO.getGrphDomainCode());
							geographyVO.setGraphDomainName(geographicalDomainVO.getGrphDomainName());
							geographyVO.setGraphDomainType(geographicalDomainVO.getGrphDomainType());
							if (oldList != null) {
								geographyVO.setGraphDomainTypeName(
										((UserGeographiesVO) oldList.get(0)).getGraphDomainTypeName());
								geographyVO.setGraphDomainSequenceNumber(
										((UserGeographiesVO) oldList.get(0)).getGraphDomainSequenceNumber());
							}
							finalList.add(geographyVO);
						}
						theForm.setGeographicalList(finalList);
					}
				} // end by santanu

			}
		}
		// 3
		else {
			String parentId = null;
			/*
			 * In add mode we need to load the geographies of the parent user so we are
			 * fetching the values from the SearchUserId array In edit mode if user perform
			 * a single search(only channel user search) at this time parentID =
			 * sessionUserId else parentId = parentUserId
			 */
			if ("add".equals(theForm.getRequestType())) {
				/*if (theForm.getSearchUserId() != null) {
					for (int i = 0, j = theForm.getSearchUserId().length; i < j; i++) {
//						parentId = theForm.getSearchUserIdIndexed(i);
						parentId = theForm.getSearchUserIdIndexed(i);
					}
				}*/
				parentId = (String)theForm.getSearchList().get(0);
			} else {
				/*if (theForm.getSearchUserId() != null && theForm.getSearchUserId().length > 1) {
					parentId = theForm.getSearchUserIdIndexed(0);
				} else {
					parentId = userSessionVO.getUserID();
				}*/
				parentId = (String)theForm.getSearchList().get(0);
			}
			if (parentId == null) {
				if (log.isDebugEnabled()) {
					log.debug("methodName", "theForm.getParentID(): " + theForm.getParentID());
				}
				parentId = theForm.getParentID();
			}
			// need to discuss with sanjay
			// whenever above code commented, uncommented the code shown
			// below
			/*
			 * if(theForm.getSearchUserId()!=null) { for(int
			 * i=0,j=theForm.getSearchUserId().length; i<j ; i++) { parentId =
			 * theForm.getSearchUserIdIndexed(i); } }
			 */

			// load the geographies info from the user_geographies of the
			// parent user(searched user)
			final ArrayList parentUserGeographyList = geographyDAO.loadUserGeographyList(con, parentId,
					userSessionVO.getNetworkID());

			UserGeographiesVO geographyVO = null;
			if (parentUserGeographyList != null && parentUserGeographyList.size() > 0) {
				for (int i = 0, j = parentUserGeographyList.size(); i < j; i++) {
					geographyVO = (UserGeographiesVO) parentUserGeographyList.get(i);
					// System.out.println("AVVV "+
					// geographyVO.getGraphDomainCode()+" theForm="+theForm.getParentDomainCode());
					if (geographyVO.getGraphDomainCode().equals(theForm.getParentDomainCode())) {
						break;
					}
				}
				if (geographyVO.getGraphDomainType().equals(theForm.getCategoryVO().getGrphDomainType())) {
					theForm.setGeographicalList(parentUserGeographyList);
					/*
					 * set the grphDoaminTypeName on the form GrphDomainTypeName is same for all
					 * VO's in list
					 */
					theForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());

				}
				// e
				else if ((geographyVO.getGraphDomainSequenceNumber() + 1) == theForm.getCategoryVO()
						.getGrphDomainSequenceNo()) {
					geographyList = geographicalDomainWebDAO.loadGeographyList(con, userSessionVO.getNetworkID(),
							geographyVO.getGraphDomainCode(), "%");
					theForm.setGeographicalList(geographyList);
					if (geographyList != null && geographyList.size() > 0) {
						/*
						 * set the grphDoaminTypeName on the form GrphDomainTypeName is same for all
						 * VO's in list
						 */
						geographyVO = (UserGeographiesVO) geographyList.get(0);
						theForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
					}
				}
				// f
				else {
					final ArrayList list = geographicalDomainWebDAO.loadDomainTypes(con,
							geographyVO.getGraphDomainSequenceNumber(),
							theForm.getCategoryVO().getGrphDomainSequenceNo());

					if (list != null && list.size() > 0) {
						theForm.setParentDomainCode(geographyVO.getGraphDomainCode());
						theForm.setDomainSearchList(list);
						theForm.setSearchDomainTextArrayCount();
						theForm.setSearchDomainCodeCount();
					}
				}

			} // end of if(parentUserGeographyList!=null)
			else {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PARENT_GEOGRAPHY_NOT_EXIST);
			}
		} // end of else
			// change 1 - Bug No 9 start
		if ("add".equals(theForm.getRequestType()) && theForm.getGeographicalListCount() > 0) {
			final UserGeographiesVO geographyVO = (UserGeographiesVO) theForm.getGeographicalList().get(0);
			theForm.setGeographicalCode(geographyVO.getGraphDomainCode());
		}
		// change 1 - Bug No 9 end
		// end copied code

		ArrayList<AreaData> finalResponseList = new ArrayList<AreaData>();
		if(theForm.getGeographicalList() == null || theForm.getGeographicalList().isEmpty()) {
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.NO_RECORDS_FOUND);
		}
		for (Object obj : theForm.getGeographicalList()) {
			UserGeographiesVO geoVO = (UserGeographiesVO) obj;
			processGeographies(con, finalResponseList, geoVO, userSessionVO);
		}

		areaSearchResponseVO.setAreaList(finalResponseList);
		areaSearchResponseVO.setHierarchyLength(1);
		areaSearchResponseVO.setMessage(PretupsI.SUCCESS);
		areaSearchResponseVO.setStatus(HttpStatus.SC_OK);
		responseSwag.setStatus(HttpStatus.SC_OK);
		
		if (log.isDebugEnabled()) {
	         log.debug(METHOD_NAME, "Exited");
	     }
		
		return areaSearchResponseVO;
	}

	private void processGeographies(Connection con, List<AreaData> responseList, UserGeographiesVO geoVO, UserVO userSessionVO) throws BTSLBaseException {
		Stack<UserGeographiesVO> stack = new Stack<>();
		Stack<AreaData> areaDataStack = new Stack<>();


		stack.push(geoVO);
		areaDataStack.push(new AreaData());

		while (!stack.isEmpty()) {

			UserGeographiesVO currentGeoVO = stack.pop();
			AreaData currentAreaData = areaDataStack.pop();


			ArrayList<UserGeographiesVO> geoList = geographicalDomainWebDAO.loadGeographyList(con, userSessionVO.getNetworkID(), currentGeoVO.getGraphDomainCode(), "%");
			ArrayList<AreaData> childAreaList = new ArrayList<>();

			for (UserGeographiesVO childGeoVO : geoList) {
				stack.push(childGeoVO);
				AreaData childAreaData = new AreaData();
				areaDataStack.push(childAreaData);
				childAreaList.add(childAreaData);
			}


			currentAreaData.setGeoCode(currentGeoVO.getGraphDomainCode());
			currentAreaData.setGeoName(currentGeoVO.getGraphDomainName());
			currentAreaData.setGeoDomainName(currentGeoVO.getGraphDomainTypeName());
			currentAreaData.setGeoDomainSequenceNo(Integer.toString(currentGeoVO.getGraphDomainSequenceNumber()));
			currentAreaData.setIsDefault(geographicalDomainWebDAO.getGeographyDomainData(con, currentGeoVO.getGraphDomainCode()).getIsDefault());
			currentAreaData.setGeoList(childAreaList);

			if (currentGeoVO == geoVO) {
				responseList.add(currentAreaData);
			}
		}
	}
	private void isUserInGeoDomain(UserVO userSessionVO, UserVO searchUserVO, Connection con) throws BTSLBaseException {
		final String METHOD_NAME = "isUserInGeoDomain";
		
		if (log.isDebugEnabled()) {
	         log.debug(METHOD_NAME, "Entered");
	     }
		
		DomainDAO domainDao = new DomainDAO();
		GeographicalDomainDAO geoDomainDao = new GeographicalDomainDAO();

		ArrayList<ListValueVO> domainList = domainDao.loadDomainListByUserId(con, userSessionVO.getUserID());
		if (domainList != null && !domainList.isEmpty()) {
			ListValueVO listValueVO = null;
			boolean domainfound = false;

			for (int i = 0, j = domainList.size(); i < j; i++) {
				listValueVO = (ListValueVO) domainList.get(i);
				if (searchUserVO.getCategoryVO().getDomainCodeforCategory().equals(listValueVO.getValue())) {
					domainfound = true;
					break;
				}
			}
			if (!domainfound) {
				throw new BTSLBaseException(classname, "METHOD_NAME", PretupsErrorCodesI.USER_NOT_IN_DOMAIN, 0, null);

			}
		}
		boolean geoFound = false;
		List<UserGeographiesVO> userGeoDomains = searchUserVO.getGeographicalAreaList();
		if (userGeoDomains.size() == 0)
			throw new BTSLBaseException(classname, "METHOD_NAME", PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN, 0, null);
		for (UserGeographiesVO userGeoDomain : userGeoDomains) {
			if (geoDomainDao.isGeoDomainExistInHierarchy(con, userGeoDomain.getGraphDomainCode(),
					userSessionVO.getUserID())) {
				geoFound = true;
				break;
			}
		}
		if (!geoFound) {
			throw new BTSLBaseException(classname, "METHOD_NAME", PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN, 0, null);
		}
		
		if (log.isDebugEnabled()) {
	         log.debug(METHOD_NAME, "Exit");
	     }
		
	}
	
	public void validateSelectedGeographyAndDomain(ArrayList domainList, ArrayList<UserGeographiesVO> geographyList , String selectedDomainCode , CategoryVO selectedCategoryVO , String selectedGeoDomainCode) throws Exception{
		final String METHOD_NAME = "validateSelectedGeographyAndDomain";
		if (log.isDebugEnabled()) {
	         log.debug(METHOD_NAME, "Entered");
	     }
		boolean domainFlag = false;
		boolean categoryFlag = false;
		boolean geoDomainFlag = false;
		for(ListValueVO listValueVO: (ArrayList<ListValueVO>)domainList) {
			if(listValueVO.getValue().equals(selectedDomainCode)) {
				domainFlag = true;
				if(listValueVO.getValue().equals(selectedCategoryVO.getDomainCodeforCategory())) {
					categoryFlag = true;
				}
			}
		}
		
		if(!domainFlag) {
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.INVALID_DOMAIN_OR_EMTPY) ;
		}
		
		if(!categoryFlag) {
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CATEGORY_NOT_IN_DOMAIN) ;
		}
		
		for(UserGeographiesVO userGeographiesVO: geographyList) {
			if(userGeographiesVO.getGraphDomainCode().equals(selectedGeoDomainCode)) {
				geoDomainFlag = true;
			}
		}
		
		if(!geoDomainFlag) {
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.INVALID_GEOGRAPHY_OR_EMTPY) ;
		}
		if (log.isDebugEnabled()) {
	         log.debug(METHOD_NAME, "Exited");
	     }
	}
	
	private Integer setHierarchyLength(ArrayList<AreaData> geoDataSearchList, Integer hierarchyLength) {
		if(geoDataSearchList==null) return 0;
		
		int maxLength = hierarchyLength;
		for(AreaData obj : geoDataSearchList) {
			maxLength = Math.max( maxLength, setHierarchyLength(obj.getGeoList() , hierarchyLength+1) );
		}
		return maxLength;
	}

	public void childGeoDataSearch(Connection con, ArrayList<UserGeographiesVO> graphDomainList, UserForm theForm, UserVO userSessionVO, ArrayList<AreaData> currentLeafList, String currentSeqNum) throws Exception{
			if(Integer.toString( theForm.getCategoryVO().getGrphDomainSequenceNo() ).equals( currentSeqNum )) return;

			ArrayList<UserGeographiesVO> newGraphDomainList = null;	
			String newSequenceNumber=null;
			ArrayList<AreaData> newAreaList= new ArrayList<>();
			
				for(UserGeographiesVO geoVO : graphDomainList) {
					AreaData curAreaData = null;
					
					// searching for areaData
					for(AreaData  obj : currentLeafList) {
						if(obj.getGeoCode().equals(geoVO.getGraphDomainCode())) { curAreaData=obj; break;}
					}
					
					//searching for child geo
					newGraphDomainList = geographicalDomainWebDAO.loadGeographyList(con, userSessionVO.getNetworkID(),curAreaData.getGeoCode(), "%");
					if(newGraphDomainList.size()==0) continue;
					
					//setting data 
					ArrayList<AreaData> childAreaList = new ArrayList<>();
					for(UserGeographiesVO childGeoObj : newGraphDomainList) {
						AreaData areaData = new AreaData();
			            areaData.setGeoCode(childGeoObj.getGraphDomainCode());
			            areaData.setGeoName(childGeoObj.getGraphDomainName());
			            areaData.setGeoDomainName(childGeoObj.getGraphDomainTypeName());
			            areaData.setGeoDomainSequenceNo(Integer.toString( childGeoObj.getGraphDomainSequenceNumber() ));
			            newSequenceNumber = areaData.getGeoDomainSequenceNo();
			            UserGeographiesVO tempGeoVO = geographicalDomainWebDAO.getGeographyDomainData(con, childGeoObj.getGraphDomainCode());
			            areaData.setIsDefault(tempGeoVO.getIsDefault());
			            childAreaList.add(areaData);
					}
					
					//adding it to the tree
					curAreaData.setGeoList(childAreaList);
					newAreaList.add(curAreaData);
					
					//searching for further child
					childGeoDataSearch(con, newGraphDomainList, theForm, userSessionVO, newAreaList, newSequenceNumber);
					newAreaList = new ArrayList<>();
					
				}
				
			
		
	}
	
	public ArrayList<AreaData> setHierarchicalResponse(Connection con, ArrayList geographyList, UserForm theForm, String userId) throws Exception{
		
		ArrayList<AreaData> geoDataList = new ArrayList<>();
		
		String grphDomainCode = null;
        ArrayList<AreaData> leafGeoDataList = new ArrayList<>();
        for(Object obj : geographyList) {
    		UserGeographiesVO geoVO = (UserGeographiesVO) obj;
    		AreaData areaData = new AreaData();
            areaData.setGeoCode(geoVO.getGraphDomainCode());
            areaData.setGeoName(geoVO.getGraphDomainName());
            areaData.setGeoDomainName(geoVO.getGraphDomainTypeName());
            areaData.setGeoDomainSequenceNo(Integer.toString( geoVO.getGraphDomainSequenceNumber() ));
            UserGeographiesVO tempGeoVO = geographicalDomainWebDAO.getGeographyDomainData(con, geoVO.getGraphDomainCode());
            areaData.setIsDefault(tempGeoVO.getIsDefault());
            grphDomainCode = geoVO.getGraphDomainCode();
            leafGeoDataList.add(areaData);
    	}
        areaList = leafGeoDataList;
        geoDataList = leafGeoDataList;
        return geoDataList;
	}

	@Override
	public SAPResponseVO fetchUserData(String network, String extCode, HttpServletResponse responseSwag) 
	{
		SAPResponseVO responseVO = new SAPResponseVO();
		HashMap<String, String> responseMap = null;
		 Date mydate = new Date();
		 SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
		 String minut2Compare = null;
		 Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));

		 
		 if(SystemPreferences.SAP_ALLOWED)
		 {
			 minut2Compare = _sdfCompare.format(mydate);
			final int currentMinut = Integer.parseInt(minut2Compare);
			 int _prevMinut = 0;
			 
		 }
		 else
		 {
			 responseVO.setCity(Constants.getProperty(network +"_"+extCode+"_"+"CITY"));
			 responseVO.setAddress(Constants.getProperty(network +"_"+extCode+"_"+"ADDRESS"));
			 responseVO.setName(Constants.getProperty(network +"_"+extCode+"_"+"NAME"));
			 responseVO.setTelephone(Constants.getProperty(network +"_"+extCode+"_"+"TELEPHONE"));
			 responseVO.setEmail(Constants.getProperty(network +"_"+extCode+"_"+"EMAIL"));
			 responseVO.setState(Constants.getProperty(network +"_"+extCode+"_"+"STATE"));
			 responseVO.setCountry(Constants.getProperty(network +"_"+extCode+"_"+"COUNTRY"));
			 responseVO.setEmpCode(Constants.getProperty(network +"_"+extCode+"_"+"EMPCODE"));
		 }	 
			 if(BTSLUtil.isNullString(responseVO.getEmail()) && BTSLUtil.isNullString(responseVO.getName()))
			 {
				 responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
				 responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				 responseVO.setMessageCode(PretupsErrorCodesI.CHANNEL_USR_NOT_FOUND);
				 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CHANNEL_USR_NOT_FOUND,null);
				 responseVO.setMessage(msg);
				 
			 }
			 else
			 {
				 responseVO.setMessage(PretupsI.SUCCESS);
				 responseVO.setStatus(HttpStatus.SC_OK);
				 responseSwag.setStatus(HttpStatus.SC_OK);
				 responseVO.setMessageCode(PretupsErrorCodesI.SUCCESS);
			 }
			
			 
		
		 return responseVO;
	}
	
	
	@Override
	public LoginIdResponseVO getLoginID(Connection con, String userId) throws BTSLBaseException {
	 List<String> listLoginIdNew = new ArrayList<>();
		LoginIdResponseVO response = new LoginIdResponseVO();
		 channelUserDao=new ChannelUserDAO();
		if (BTSLUtil.isNullString(userId) || userId.equalsIgnoreCase("")) {
			throw new BTSLBaseException(ChannelUserServicesImpl.class.getName(), PretupsErrorCodesI.LOGIN_ID_IS_NULL);
		}
		try {
			List<String> listLoginId1 = new ArrayList<String>();
			boolean searcheduserId = channelUserDao.searchByUserId(con, userId, listLoginId1);
			response.setLoginIdExist(searcheduserId);
			if (searcheduserId) {
				response.setLoginIdExist(true);
				String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
						PretupsErrorCodesI.LOGIN_ID_EXISTS, null);
				response.setMessage(resmsg);
				response.setMessageCode(resmsg);

				for (int i = 1; i < 10; i++) {
					if (listLoginId1.contains(userId.toLowerCase() + "0" + i)) {
						continue;
					}

					else {
						listLoginIdNew.add(userId + "0" + i);

					}
				}
				response.setListLoginIdNew(listLoginIdNew);

			} else
				throw new BTSLBaseException(ChannelUserServicesImpl.class.getName(),
						PretupsErrorCodesI.LOGIN_ID_DOES_NOT_EXISTS);
			response.setStatus(HttpStatus.SC_OK);

		} catch (BTSLBaseException be) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setLoginIdExist(false);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					be.getForwardPath(), null);
			response.setMessage(resmsg);

		} catch (Exception e) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setLoginIdExist(false);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.LOGIN_ID_DOES_NOT_EXISTS, null);
			response.setMessage(resmsg);
		}

		return response;
	}
	@Override
	public UserHierarchyResponseVO  fetchUserHierarchy(Connection con, UserHierarchyResponseVO response,
			HttpServletResponse responseSwag, String userDomain, String parentCategory, String userCategory,
			String geography, String status, String loginId, String msisdn,ChannelUserVO channelUserVO) throws SQLException, BTSLBaseException {
			final String methodName = "fetchUserHierarchy";
	     if (log.isDebugEnabled()) {
	         log.debug(methodName, "Entered");
	     }
	     	ChannelUserDAO channelUserDAO = null;
	        ChannelUserTransferWebDAO channelUserTransferwebDAO = null;
	        ChannelUserWebDAO channelUserWebDAO = null;
	      	ArrayList<ChannelUserVO> hierarchyuserList =null;
	        String[] arr = null;
	        Locale locale =null;
	        try {
			   channelUserDAO = new ChannelUserDAO();
			   channelUserTransferwebDAO = new ChannelUserTransferWebDAO();
			   channelUserWebDAO = new ChannelUserWebDAO();
			   String status1=status;//.map(Object::toString).orElse("");
			   locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
	           
	            final String transferMode = PretupsI.ALL;
	           
	            String statusMode;
	            if (status1.equals(PretupsI.ALL) || status1.isEmpty() || status1 == "") {
	                statusMode = PretupsI.STATUS_IN;
	                status1 = PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
	            } else {
	                statusMode = PretupsI.STATUS_EQUAL;
	            
	            }
	        
	            // Added for transferred channel user report.
	            // Checking that user have typed wrong from parent name and
                // clicked on submit button
	            arr = new String[1];
               arr[0] = parentCategory;
                // load all the parents of status Y, S, SR
                final String parentStatus = PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
                
                ArrayList parentList = channelUserWebDAO.loadCategoryUsers(con, parentCategory, channelUserVO.getNetworkID(), "%" + channelUserVO.getUserName() + "%",
                    channelUserVO.getOwnerID(), PretupsI.STATUS_IN, parentStatus);
                if (parentList != null && parentList.isEmpty()) {
                    arr[0] = channelUserVO.getParentName();
                    throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.PARENT_NOT_FOUND);
                    } 
                else if (parentList != null && parentList.size() > 1) {
                    boolean recordFound = false;
                    ListValueVO listValueVO;
                    if (!BTSLUtil.isNullString(channelUserVO.getParentID())) {
                        for (int i = 0, j = parentList.size(); i < j; i++) {
                            listValueVO = (ListValueVO) parentList.get(i);
                            if (listValueVO.getValue().equals(channelUserVO.getParentID()) && (listValueVO.getLabel().compareTo(
                                channelUserVO.getParentName()) == 0)) {
                                recordFound = true;
                                break;
                            }
                        }
                    }
                    if (!recordFound) {
                    	throw new BTSLBaseException("UserHierarchyServiceImpl",methodName,PretupsErrorCodesI.NO_RECORD_AVAILABLE);
                    }
                } else {
                    final ListValueVO listValueVO = (ListValueVO) parentList.get(0);
                    channelUserVO.setParentID(listValueVO.getValue());
                    channelUserVO.setParentName(listValueVO.getLabel());
                }
	            if (transferMode.equals(PretupsI.ALL)) {
	                arr = new String[1];
	                arr[0] = channelUserVO.getUserID();
	                hierarchyuserList = channelUserDAO.loadUserHierarchyList(con, arr, PretupsI.ALL, statusMode, status1, userCategory);

	            
	            }
	            
	            final Integer seqNo = channelUserVO.getCategoryVO().getSequenceNumber();
	            final ArrayList<ListValueVO> categoryList = channelUserTransferwebDAO.loadTransferCategoryList(con, userDomain, seqNo);
	            if (categoryList != null && !categoryList.isEmpty()) {
	            	channelUserVO.setCategoryList(categoryList);
	            } else {
	   	           throw new BTSLBaseException("viewUserDetails",methodName,PretupsErrorCodesI.CCE_ERROR_USER_NOTIN_DOMAIN);
	            }

	           response.setUserHierarchyList(hierarchyuserList); 
	           response.setChanerUserVO(channelUserVO);
	           response.setStatus(200); 
	           responseSwag.setStatus(HttpStatus.SC_OK);
	           response.setMessageCode(PretupsErrorCodesI.SUCCESS);
	           String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS,new String[] {""});
	           response.setMessage(msg);
		   } finally {
			  
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
            if (log.isDebugEnabled()) {
                log.debug("Exiting:" + methodName, "");
            }
           
		   }
		return response;
	}
	@Override
	@SuppressWarnings({"rawtypes" , "unchecked"})
	public BatchUserInitiateResponseVO batchUserInitiateProcess(String loginId, BatchUserInitiateRequestVO requestVO, HttpServletResponse responseSwag) {
		
		final String METHOD_NAME = "batchUserInitiateProcess";
	     if (log.isDebugEnabled()) {
	         log.debug(METHOD_NAME, "Entered");
	     }
		
	    Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
		BatchUserInitiateResponseVO response= new BatchUserInitiateResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		final ProcessBL processBL = new ProcessBL();
		ProcessStatusVO processVO = null;
        boolean processRunning = true;
        LinkedHashMap<String, List<String>> bulkDataMap = null; ;
        ErrorMap errorMap = new ErrorMap();
        BatchUserForm theForm = new BatchUserForm();
        boolean isUploaded = false;
        ReadGenericFileUtil fileUtil=null;
        HashMap<String, String>  fileDetailsMap = null;
		try {
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userDAO = new UserDAO();
			final UserVO userSessionVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			
			//checking if already some file is being processed or not
			processVO = new ProcessStatusVO();
			processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.BATCH_USR_PROCESS_ID,userSessionVO.getNetworkID());
			if (processVO != null && !processVO.isStatusOkBool()) {
                processRunning = false;
                throw new BTSLBaseException("process already running");
            }
			mcomCon.partialCommit();// if no process running, then do partial commit
            processVO.setNetworkCode(userSessionVO.getNetworkID()); // new change
            
            if(requestVO.getFileName().length()>30) {
            	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.LENGTH_EXCEED, new String[] {"File name", "30"}); 
            }
            
            
            
            // upload file on server
            Workbook workbook = null;
            Sheet  excelsheet = null;
            int firstRecInd = 6; //0 based indexing 
            int rowItr = 0;
            
            fileUtil = new ReadGenericFileUtil();
			fileDetailsMap = new HashMap<String, String>();
			fileDetailsMap.put(PretupsI.FILE_TYPE1, requestVO.getFileType());
			fileDetailsMap.put(PretupsI.FILE_NAME,requestVO.getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getFileAttachment());
			fileDetailsMap.put(PretupsI.SERVICE_KEYWORD, PretupsI.BATCH_OPT_USR_INITIATION_SERVICE);
			validateFileDetailsMap(fileDetailsMap);
			if(requestVO.getFileType()=="xls") {
				bulkDataMap = fileUtil.uploadAndReadGenericFileBatchUserInitiate(fileDetailsMap, 1, 4, errorMap);//will start reading from 1st row, 4th row is header
				bulkDataMap = getMapInFileFormat(bulkDataMap);
				isUploaded = true;
			}else {//for xlsx
				String filePathCons = Constants.getProperty("UploadBatchUserFilePath");
				String filePathConstemp = filePathCons + "temp/";        // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/temp/
	    		createDirectory(filePathConstemp);
	    		String fileNamewithextention = "";
	    		if(requestVO.getFileType().equals("xlsx")) fileNamewithextention = requestVO.getFileName()+".xlsx";
	    		else if(requestVO.getFileType().equals("xls")) fileNamewithextention = requestVO.getFileName()+".xls";
	    		String filepath = filePathConstemp + fileNamewithextention;   // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/temp/c2cBatchTransfer.xlsx
	    		byte[] base64Bytes = fileUtil.decodeFile(fileDetailsMap.get(PretupsI.FILE_ATTACHMENT));
	    		log.debug("filepathtemp:", filepath);
	    		log.debug("base64Bytes:", base64Bytes);
	    		validateFileSize(Constants.getProperty("MAX_XLS_FILE_SIZE_FOR_BULKUSER"), base64Bytes);  // throws exception
	    		writeByteArrayToFile(filepath, base64Bytes);
	            isUploaded = true;
	            fileDetailsMap.put("filePath", filepath );
				try(FileInputStream inDummy = new FileInputStream(new File(filepath))){
					try {
						workbook = StreamingReader.builder()
								.rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
								.bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
								.open(inDummy);
					}catch(Exception e) {
						throw new BTSLBaseException("file attachment is invalid");
					}

					excelsheet = workbook.getSheetAt(0);
					excelsheet.getLastRowNum();
					for(Row r : excelsheet) {
						Cell cell = r.getCell(0);
						String val = cellValueNull(cell);

						if(val.equals("Domain name")) {
							cell = r.getCell(1);
							val = cellValueNull(cell);
							fileDetailsMap.put("domainName", val);
							continue;
						}
						if(val.equals("Geography name")) {
							cell = r.getCell(1);
							val = cellValueNull(cell);
							fileDetailsMap.put("geoName", val);
							break;
						}
					}
					fileDetailsMap.put("recordCount", Integer.toString(excelsheet.getLastRowNum()-6 + 1));//6 is header offset
				}
	            
			}

           			
			//getting geoCode and domainCode
			for(UserGeographiesVO geoVO : userSessionVO.getGeographicalAreaList()) {
				if(fileDetailsMap.get("geoName").equals(geoVO.getGraphDomainName())) theForm.setGeographyCode(geoVO.getGraphDomainCode());
			}
			if(userSessionVO.getDomainList().size()!=0) {
				for(Object obj : userSessionVO.getDomainList()) {
					ListValueVO vo = (ListValueVO) obj;
					if(vo.getLabel().equals(fileDetailsMap.get("domainName"))) {theForm.setDomainCode(vo.getValue()); break;}
				}
			}else theForm.setDomainCode(userSessionVO.getDomainID());
			
			if(requestVO.getBatchName()!=null && !requestVO.getBatchName().equals("")) theForm.setBatchName(requestVO.getBatchName());
			else throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PROPERTY_MISSING, new String[] {"Batch name"});
			
			
			//getting prop from db and sys preference needed for user addition
			int maxRowSize = 0;
			try {
                maxRowSize = Integer.parseInt(Constants.getProperty("maxRecordsInBulkUserInitiate"));
            } catch (Exception e) {maxRowSize = 1000;}
			if (Integer.parseInt( fileDetailsMap.get("recordCount") ) > maxRowSize) {
				fileUtil.filedelete(fileDetailsMap.get("filePath"));
                throw new BTSLBaseException(this, "processUploadedFile", "bulkuser.processuploadedfile.error.maxlimitofrecsreached", 0, new String[] { String
                    .valueOf(maxRowSize) }, "selectDomainForInitiate");
            }
			if (Integer.parseInt( fileDetailsMap.get("recordCount") ) == 0) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_NO_RECORDS_ERROR, new String[] {""});
            }
			
			int noOfMsisdn = 0;
            ChannelUserVO channelUserVO = null;
            ListValueVO errorVO = null;
            final int blankLines = 0;
            final ArrayList fileErrorList = new ArrayList();
            ArrayList channelUserVOList = new ArrayList();
            int colIndex;
            final BatchUserDAO batchUserDAO = new BatchUserDAO();
            final BatchUserWebDAO batchUserWebDAO = new BatchUserWebDAO();
            final String domainCode = theForm.getDomainCode();
            final String userType = userSessionVO.getUserType();
            final ArrayList mPayProfileIDList = null;
			final SubLookUpDAO sublookupDAO = new SubLookUpDAO();
			final HashMap masterMap = new HashMap();
			
            masterMap.put(PretupsI.BATCH_USR_USER_PREFIX_LIST, LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true));
            masterMap.put(PretupsI.BATCH_USR_OUTLET_LIST, LookupsCache.loadLookupDropDown(PretupsI.OUTLET_TYPE, true));
            masterMap.put(PretupsI.BATCH_USR_SUBOUTLET_LIST, sublookupDAO.loadSublookupByLookupType(con, PretupsI.OUTLET_TYPE));
            final ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
            masterMap.put(PretupsI.BATCH_USR_SERVICE_LIST, servicesDAO.loadServicesList(con, userSessionVO.getNetworkID(), PretupsI.C2S_MODULE, null, false));
            masterMap.put(PretupsI.BATCH_USR_GEOGRAPHY_LIST, batchUserWebDAO.loadMasterGeographyList(con, theForm.getGeographyCode(), userSessionVO.getUserID()));
            masterMap.put(PretupsI.BATCH_USR_GEOGRAPHY_TYPE_LIST, batchUserWebDAO.loadCategoryGeographyTypeList(con, domainCode));
            masterMap.put(PretupsI.BATCH_USR_CATEGORY_HIERARCHY_LIST, batchUserWebDAO.loadMasterCategoryHierarchyList(con, domainCode, userSessionVO.getNetworkID()));
            // Changes Made for batch user creation by channel users
            masterMap.put(PretupsI.BATCH_USR_CATEGORY_LIST, batchUserDAO.loadMasterCategoryList(con, domainCode, userSessionVO.getCategoryCode(), userType));
            masterMap.put(PretupsI.BATCH_USR_GROUP_ROLE_LIST, batchUserDAO.loadMasterGroupRoleList(con, domainCode, userSessionVO.getCategoryCode(), userType));
            masterMap.put(PretupsI.BATCH_USR_LANGUAGE_LIST, batchUserDAO.loadLanguageList(con));
            masterMap.put(PretupsI.USER_DOCUMENT_TYPE, LookupsCache.loadLookupDropDown(PretupsI.USER_DOCUMENT_TYPE, true));
            masterMap.put(PretupsI.PAYMENT_INSTRUMENT_TYPE, LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true));
            if (userType.equals(PretupsI.OPERATOR_USER_TYPE) || (userType.equals(PretupsI.CHANNEL_USER_TYPE) && SystemPreferences.BATCH_USER_PROFILE_ASSIGN)) {
                masterMap.put(PretupsI.BATCH_USR_GRADE_LIST, batchUserDAO.loadMasterCategoryGradeList(con, domainCode, userSessionVO.getCategoryCode(), userType));
                masterMap.put(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST, batchUserDAO.loadMasterTransferProfileList(con, domainCode, userSessionVO.getNetworkID(), userSessionVO
                    .getCategoryCode(), userType));

                masterMap.put(PretupsI.BATCH_USR_COMM_LIST, batchUserWebDAO.loadCommProfileList(con, domainCode, userSessionVO.getNetworkID(), userSessionVO.getCategoryCode(), userType));

                if (SystemPreferences.IS_TRF_RULE_USER_LEVEL_ALLOW) {
                    masterMap.put(PretupsI.BATCH_USR_TRF_RULE_LIST, LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
                }
            }
            VomsProductDAO voucherDAO = new VomsProductDAO();
            if(SystemPreferences.USER_VOUCHERTYPE_ALLOWED){
               masterMap.put(PretupsI.VOUCHER_TYPE_LIST, voucherDAO.loadVoucherTypeList(con));
            }
            theForm.setBulkUserMasterMap(masterMap);

            // Get the prefix list from Master Data & prepare the service array
            final ArrayList prefixList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_USER_PREFIX_LIST);
            // Get the category list from Master Data & prepare the category
            // array
            final ArrayList categoryList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_CATEGORY_LIST);
            int size = 0, gradeSize = 0, geoSize = 0, grpSize = 0, comPrfSize = 0, transPrfSize = 0;
            if (categoryList != null) {
                size = categoryList.size();
            }
            CategoryVO categoryVO = null;
            String categoryCodeInSheet = null;
            // Get the geography list from Master data & prepare geographyArray
            final ArrayList geographyList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_GEOGRAPHY_LIST);
            if (geographyList != null) {
                geoSize = geographyList.size();
            }
            // Get the outlet list from the Master data & prepare outlet Array
            final ArrayList outletList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_OUTLET_LIST);
            // Get the suboutlet list from the Master data & prepare outlet
            // Array
            final ArrayList subOutletList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_SUBOUTLET_LIST);
            final ArrayList documentTypeList = (ArrayList) masterMap.get(PretupsI.USER_DOCUMENT_TYPE);
            final ArrayList paymentTypeList = (ArrayList) masterMap.get(PretupsI.PAYMENT_INSTRUMENT_TYPE);
            
            final HashMap languageMap = (HashMap) masterMap.get(PretupsI.BATCH_USR_LANGUAGE_LIST);
            String filteredMsisdn = null;
            boolean found = false;
            ListValueVO listVO = null;
            int MAX_SERVICES = 0;
            int MAX_VOUCHERS = 0;
            String serviceArr[] = null;
            String voucherTypeArr[]=null;
            int serviceLen = 0;
            int voucherTypeLen=0;
            int processing =0;
            ArrayList channelUsers = new ArrayList<>();
            // Get the service list from Master Data & prepare the service array
            final ArrayList serviceList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_SERVICE_LIST);
           
            if (serviceList != null && (MAX_SERVICES = serviceList.size()) > 0) {
                serviceArr = new String[MAX_SERVICES];
            }
            final ArrayList voucherTypeList= (ArrayList) masterMap.get(PretupsI.VOUCHER_TYPE_LIST);
            
            if (voucherTypeList != null && (MAX_VOUCHERS = voucherTypeList.size()) > 0) {
                voucherTypeArr = new String[MAX_VOUCHERS];
            }
            // Get the group list from the master map.
            final ArrayList groupList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_GROUP_ROLE_LIST);
            grpSize = groupList.size();
            UserRolesVO rolesVO = null;
            ArrayList commPrfList = null;
            ArrayList transferPrfList = null;
            ArrayList trfRuleTypeList = null;
            ArrayList gradeList = null;
            int totColsinXls = 31;// added 2 new columns longitude and latitude
            // ; added 3 more columns :
            // company,fax,language:1 for email added by
            // akanksha gupta for claro
            // Changes Made for batch user creation by channel users
            if (userType.equals(PretupsI.OPERATOR_USER_TYPE) || (userType.equals(PretupsI.CHANNEL_USER_TYPE) && SystemPreferences.BATCH_USER_PROFILE_ASSIGN)) {
                // Get the grade list from Master data & prepare grade array
                gradeList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_GRADE_LIST);
                if (gradeList != null) {
                    gradeSize = gradeList.size();
                }

                // Get the commision profile list from Master map.
                commPrfList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_COMM_LIST);
                comPrfSize = commPrfList.size();
                // Get the transfer profile list from Master map.
                transferPrfList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST);
                transPrfSize = transferPrfList.size();
                trfRuleTypeList = null;
                if (SystemPreferences.IS_TRF_RULE_USER_LEVEL_ALLOW) {
                    trfRuleTypeList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_TRF_RULE_LIST);
                }
                totColsinXls += 3;
                // for Zebra and Tango By sanjeew 09/07/07
                if (SystemPreferences.PTUPS_MOBQUTY_MERGD) {
                    totColsinXls = totColsinXls + 2;
                }
                // end Zebra and Tango
                if (SystemPreferences.IS_TRF_RULE_USER_LEVEL_ALLOW) {
                    totColsinXls = totColsinXls + 1;
                }
                if (SystemPreferences.RSA_AUTHENTICATION_REQUIRED) {
                    totColsinXls = totColsinXls + 1;
                }
                if (SystemPreferences.AUTH_TYPE_REQ) {
                    totColsinXls = totColsinXls + 1;
                }
                if(SystemPreferences.USER_VOUCHERTYPE_ALLOWED) {
                    totColsinXls = totColsinXls + 1;
                }
            }
            
            // End of Changes Made for batch user creation by channel users
            ArrayList dberrorList = new ArrayList();
            boolean db = false;
            if (SystemPreferences.IS_FNAME_LNAME_ALLOWED) {
                totColsinXls += 1;
            }
            
            int totalRecordsInFile = Integer.parseInt( fileDetailsMap.get("recordCount") );
                        
            if(totalRecordsInFile==0) throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_NO_RECORDS_ERROR, new String[] {""});
            
            int rows = 0;
            int invalidRecordCount = 0;
            boolean insertBatch = true;
            int totalSize = 0;
            boolean fileValidationErrorExists = false;            
            OperatorUtilI operatorUtili = null;
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
            final HashMap categoryMap = batchUserWebDAO.loadCategoryList(con, domainCode, userType, userSessionVO.getCategoryCode());
            
            //generating batchID
            String batchID = operatorUtili.formatBatchesID(userSessionVO.getNetworkID(), PretupsI.BULK_USR_ID_PREFIX, new Date(), IDGenerator.getNextID(PretupsI.BULK_USR_BATCH_ID,
                    BTSLUtil.getFinancialYear(), userSessionVO.getNetworkID()));
            
            //setting up threads
            int threadPoolSize =  Runtime.getRuntime().availableProcessors() + 1;
            int taskSize = 500;//totalRecordsInFile/threadPoolSize ;
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize);
            
            //iterate over data
            if(requestVO.getFileType().equals("xls")) {
            	for (Entry<String, List<String>> entry : bulkDataMap.entrySet()) {
                    rows++;
                	if(rows==1) continue; // header row
                	ArrayList<String> dataRow = (ArrayList<String>) entry.getValue();
                	fileValidationErrorExists = validateRow(con, dataRow, rows+5, fileErrorList, locale, categoryMap, languageMap, operatorUtili, userDAO, userSessionVO, masterMap);//5 is rowoffset
                    if(fileValidationErrorExists) invalidRecordCount++;
                	if(!fileValidationErrorExists) {
                    	channelUserVO = setChannelUserVO(con,dataRow, rows+5,theForm, userSessionVO, categoryMap, operatorUtili);
                    	channelUserVOList.add(channelUserVO);
                    	
                    	if (channelUserVOList != null && channelUserVOList.size() == taskSize) {
                			int recordsProcessedTillNow = rows-1;// coz 1st row is header
                			totalSize = recordsProcessedTillNow - invalidRecordCount;
                			insertBatch=false;
                			if(recordsProcessedTillNow==totalRecordsInFile){//-1 coz 1st row rep header
                				db=true;
                				break;
                			}
                			UpdateRecordsInDB updateRecordsInDB = new UpdateRecordsInDB(channelUserVOList,dberrorList,batchUserWebDAO,theForm.getDomainCode(),locale,userSessionVO,fileDetailsMap.get(PretupsI.FILE_NAME),batchID,insertBatch,totalSize,false);
                			executor.execute(updateRecordsInDB);
                			// in case when remaing records are failed so channel user list will remain empty need to update batch
                			channelUsers.add(channelUserVOList.get(0));
                			channelUserVOList = new ArrayList();
                			processing=fileErrorList.size();
                    	}
                    	
                    }
                    if((rows % 1000) == 0) {
                    	Runtime runtime = Runtime.getRuntime();
                        long memory = runtime.totalMemory() - runtime.freeMemory();
                        log.debug("processUploadedFile","Used memory in megabytes before gc: " + (memory)/1048576);
                        runtime.gc(); // Run the garbage collector
                        memory = runtime.totalMemory() - runtime.freeMemory();// Calculate the used memory
                        log.debug("processUploadedFile","Used memory in megabytes after gc: " + (memory)/1048576);
                    }
                    
                }
            }else if(requestVO.getFileType().equals("xlsx")) {
            	int headerOffset = 6;
            	excelsheet = workbook.getSheetAt(0);
	            excelsheet.getLastRowNum();
	           	for(Row r : excelsheet) {	           		
	           		if(r.getRowNum()<headerOffset) continue;
	           		int colIt=0;
	           		ArrayList<String> dataRow = new ArrayList<>();
	           		while(colIt!=totColsinXls) dataRow.add(cellValueNull(r.getCell(colIt++)));
	           		
	           		rows++;
                	fileValidationErrorExists = validateRow(con, dataRow, rows+5, fileErrorList, locale, categoryMap, languageMap, operatorUtili, userDAO, userSessionVO, masterMap);//5 is rowoffset
                    if(fileValidationErrorExists) invalidRecordCount++;
                	if(!fileValidationErrorExists) {
                    	channelUserVO = setChannelUserVO(con,dataRow, rows+5,theForm, userSessionVO, categoryMap, operatorUtili);
                    	channelUserVOList.add(channelUserVO);
                    	
                    	if (channelUserVOList != null && channelUserVOList.size() == taskSize) {
                			int recordsProcessedTillNow = rows-1;// coz 1st row is header
                			totalSize = recordsProcessedTillNow - invalidRecordCount;
                			insertBatch=false;
                			if(recordsProcessedTillNow==totalRecordsInFile){//-1 coz 1st row rep header
                				db=true;
                				break;
                			}
                			UpdateRecordsInDB updateRecordsInDB = new UpdateRecordsInDB(channelUserVOList,dberrorList,batchUserWebDAO,theForm.getDomainCode(),locale,userSessionVO,fileDetailsMap.get(PretupsI.FILE_NAME),batchID,insertBatch,totalSize,false);
                			executor.execute(updateRecordsInDB);
                			// in case when remaing records are failed so channel user list will remain empty need to update batch
                			channelUsers.add(channelUserVOList.get(0));
                			channelUserVOList = new ArrayList();
                			processing=fileErrorList.size();
                    	}
                    	
                    }
                    if((rows % 1000) == 0) {
                    	Runtime runtime = Runtime.getRuntime();
                        long memory = runtime.totalMemory() - runtime.freeMemory();
                        log.debug("processUploadedFile","Used memory in megabytes before gc: " + (memory)/1048576);
                        runtime.gc(); // Run the garbage collector
                        memory = runtime.totalMemory() - runtime.freeMemory();// Calculate the used memory
                        log.debug("processUploadedFile","Used memory in megabytes after gc: " + (memory)/1048576);
                    }
	           		
	           	}
	           		      		
           }
            
            executor.shutdown();   // Now close the executor service 
    		while (!executor.isTerminated()) {//waiting for all threads to finish work
    		}
    		log.debug(METHOD_NAME,"Finished all threads");	
    		
    		//saving remaining valid records
    		if (channelUserVOList != null &&(!channelUsers.isEmpty()||!channelUserVOList.isEmpty())) {
    			boolean inbatch = false;
    			insertBatch=true;
    			if(!db){
    			totalSize=totalSize+channelUserVOList.size();
    			}
    			if(channelUserVOList.isEmpty()){
    				channelUserVOList.add(channelUsers.get(0));
    				 inbatch=true;
    			}
    			totalSize =totalRecordsInFile - invalidRecordCount - dberrorList.size();
    			ArrayList dbErrorList = batchUserWebDAO.addChannelUserList(con, channelUserVOList, theForm.getDomainCode(),locale,userSessionVO,fileDetailsMap.get(PretupsI.FILE_NAME),batchID,insertBatch,totalSize,inbatch);
    			con.commit();
    			if(dbErrorList.size()==1) {
    				ListValueVO obj =  (ListValueVO) dbErrorList.get(0);
    				if(obj.getLabel()!=null) dberrorList.addAll(dbErrorList);
    			}else {
    				dbErrorList.remove(dbErrorList.size()-1);
    				dberrorList.addAll(dbErrorList);
    			}
    			
    		}
    		
    		if (dberrorList != null && !dberrorList.isEmpty()) fileErrorList.addAll(dberrorList);
    		//Sort fileErrorlist    		
    		Collections.sort(fileErrorList, new Comparator<ListValueVO>() {
				@Override
				public int compare(ListValueVO arg0, ListValueVO arg1) {
					return arg0.compareLabelTo(arg1);
				}
    		});
    		
    		//setting errors in response
    		invalidRecordCount = 0;
    		String oldRow = "";
    		List<RowErrorMsgLists> rowErrorMsgLists = null;
    		List<RowErrorMsgLists> rowErrorMsgListsForResponse = null;
    		if (fileErrorList != null && !fileErrorList.isEmpty()) {
    			rowErrorMsgLists = new ArrayList<RowErrorMsgLists>();
    			rowErrorMsgListsForResponse = new ArrayList<RowErrorMsgLists>();
    			for (int i = 0; i < fileErrorList.size(); i++) {
                    errorVO = (ListValueVO) fileErrorList.get(i);
                    if(!oldRow.equals(errorVO.getLabel())) {
                    	invalidRecordCount++;
                    	oldRow = errorVO.getLabel();
                    }
                    RowErrorMsgLists rowError = new RowErrorMsgLists();
                    rowError.setRowName(errorVO.getValue());
                    rowError.setRowValue("row "+ errorVO.getLabel());
                    List<MasterErrorList> masterErrorList = new ArrayList<MasterErrorList>();
                    MasterErrorList masterError = new MasterErrorList();
                    masterError.setErrorCode(errorVO.getIDValue());
                    masterError.setErrorMsg(errorVO.getOtherInfo());
                    masterErrorList.add(masterError);
                    rowError.setMasterErrorList(masterErrorList);
                    rowErrorMsgLists.add(rowError);
                    if(i<=10) {
                    	rowErrorMsgListsForResponse.add(rowError);
                    }
                }
    			errorMap.setRowErrorMsgLists(rowErrorMsgLists);
    			response.setErrorMap(errorMap);
    		}
    		
    		//creating error file 
    		ErrorFileResponse errorResponse = new ErrorFileResponse();
    		if(invalidRecordCount>0) {
    			DownloadUserListService downloadUserListService = new DownloadUserListServiceImpl();
        		ErrorFileRequestVO errorFileRequestVO = new ErrorFileRequestVO();
        		errorFileRequestVO.setFile(requestVO.getFileAttachment());
        		errorFileRequestVO.setFiletype(requestVO.getFileType());
        		errorFileRequestVO.setRowErrorMsgLists(rowErrorMsgLists);
        		errorFileRequestVO.setAdditionalProperty(PretupsI.SERVICE_KEYWORD, PretupsI.BATCH_OPT_USR_INITIATION_SERVICE);
        		errorFileRequestVO.setAdditionalProperty("row", 5);//5 is header row, count start from 0
        		if(invalidRecordCount<totalRecordsInFile) errorFileRequestVO.setPartialFailure(true);
        		if(invalidRecordCount==totalRecordsInFile) errorFileRequestVO.setPartialFailure(false);
        		downloadUserListService.downloadErrorFile(errorFileRequestVO, errorResponse, responseSwag);
    		}
    		
    		//setting response
    		response.setTotalRecords(totalRecordsInFile);
    		response.setValidRecords(totalRecordsInFile - invalidRecordCount);
    		if(batchID!=null) response.setBatchID(batchID);
    		if(invalidRecordCount>0) {
    			response.setFileAttachment(errorResponse.getFileAttachment());
				response.setFileName(errorResponse.getFileName());
				for(RowErrorMsgLists obj : rowErrorMsgListsForResponse) {
					String rowNum = obj.getRowValue();
					int rowInt = Integer.parseInt(rowNum.split(" ",-1)[1]);
					rowInt -= 6;
					rowNum = "row "+Integer.toString(rowInt);
					obj.setRowValue(rowNum);
				}
				errorMap.setRowErrorMsgLists(rowErrorMsgListsForResponse);
    			if(invalidRecordCount<totalRecordsInFile) {//partial failure
    				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
        			response.setMessage(msg);
    				response.setStatus("400");
    				responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
    				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
    			}else if(invalidRecordCount==totalRecordsInFile) {//all records failed
    				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
        			response.setMessage(msg);
    				response.setStatus("400");
    				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
    				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
    			}
    		}else if(invalidRecordCount==0) {// all records inserted in db
	            String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BATCH_USER_INITIATE_SUCCESS,new String[] {""});
    			response.setMessage(msg);
				response.setStatus("200");
				responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
				response.setMessageCode(PretupsErrorCodesI.BATCH_USER_INITIATE_SUCCESS);
    		}
    		
		}catch(BTSLBaseException be) {
			log.error("processFile", "Exceptin:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),be.getArgs());
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);
			if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
				response.setStatus(Integer.toString(401));
			}
			else{
				response.setStatus(Integer.toString(400));
			}
		}catch (Exception e) {
			log.debug("processFile", e);
			response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
					null);
			response.setMessage(resmsg);
			response.setStatus(Integer.toString(400));
    	
		}finally {
			//commenting the below code to avoid the deletion of file uploaded on server.
//			if(isUploaded) {
//				try{
//					fileUtil.filedelete(fileDetailsMap.get("filePath"));
//				}catch(Exception e) {
//					if(log.isDebugEnabled()) log.debug(METHOD_NAME, e.getMessage());
//					log.errorTrace(METHOD_NAME, e);
//				}
//			}
			if(processRunning) {
				try {
                    processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    final ProcessStatusDAO processDAO = new ProcessStatusDAO();
                    if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
                    	mcomCon.finalCommit();
                    } else {
                        
                    	mcomCon.finalRollback();
                    }
                } catch (Exception e) {
                	if(log.isDebugEnabled()) log.debug(METHOD_NAME, e.getMessage());
					log.errorTrace(METHOD_NAME, e);
                }
			}
			
		}

		return response;
		
	}
	
	private ChannelUserVO setChannelUserVO(Connection con, ArrayList<String> r, int rowNum, BatchUserForm theForm, UserVO userSessionVO, HashMap categoryMap, OperatorUtilI operatorUtili) throws BTSLBaseException {
		final String METHOD_NAME = "setChannelUserVO";
	     if (log.isDebugEnabled()) {
	         log.debug(METHOD_NAME, "entered");
	     }
	     
	    HashMap masterMap = theForm.getBulkUserMasterMap();
	    int noOfMsisdn = (int) masterMap.get("noOfMsisdn");
	    ArrayList newServiceList = (ArrayList) masterMap.get("newServiceList");
	    final String userType = userSessionVO.getUserType();
	    final Date currentDate = new Date();
	    CategoryVO categoryVO = (CategoryVO) masterMap.get("categoryVO");
	    String categoryCodeInSheet = (String) masterMap.get("categoryCodeInSheet");
	    ArrayList newVoucherTypeList = (ArrayList) masterMap.get("newVoucherTypeList");
	    ChannelUserVO channelUserVO = new ChannelUserVO();
	    int colIt = 0;
	    int a = 5;
	    
	    channelUserVO.setBatchName(theForm.getBatchName().trim());
	    channelUserVO.setDomainID(theForm.getDomainCode());
        channelUserVO.setNetworkID(userSessionVO.getNetworkID());
        channelUserVO.setRecordNumber(String.valueOf(rowNum));
        
        channelUserVO.setParentMsisdn(r.get(colIt));
        colIt++;
        channelUserVO.setUserNamePrefix(r.get(colIt));
        colIt++;
        
        if (!SystemPreferences.IS_FNAME_LNAME_ALLOWED) {
        	
            channelUserVO.setUserName(r.get(colIt));
            colIt++;
        } else {
            channelUserVO.setFirstName(r.get(colIt));
            colIt++;
            channelUserVO.setLastName(r.get(colIt));
            colIt++;
            if (channelUserVO.getLastName() != null) {
                channelUserVO.setUserName(channelUserVO.getFirstName() + " " + channelUserVO.getLastName());
            } else {
                channelUserVO.setUserName(channelUserVO.getFirstName());
            }
        }
        
        channelUserVO.setShortName(r.get(colIt));
        colIt++;
        channelUserVO.setCategoryCode(r.get(colIt));
        colIt++;
        channelUserVO.setCategoryVO((CategoryVO) categoryMap.get(channelUserVO.getCategoryCode()));
        channelUserVO.setExternalCode(r.get(colIt));
        colIt++;
        channelUserVO.setContactPerson(r.get(colIt));
        colIt++;
        channelUserVO.setAddress1(r.get(colIt));
        colIt++;
        channelUserVO.setCity(r.get(colIt));
        colIt++;
        channelUserVO.setState(r.get(colIt));
        colIt++;
        channelUserVO.setSsn(r.get(colIt));
        colIt++;
        channelUserVO.setCountry(r.get(colIt));
        colIt++;	     
        channelUserVO.setCompany(r.get(colIt));
        colIt++;
        channelUserVO.setFax(r.get(colIt));
        colIt++;
        channelUserVO.setEmail(r.get(colIt));
        colIt++;
        channelUserVO.setLanguage(r.get(colIt));
        colIt++;
        
        if (channelUserVO.getCategoryVO().getWebInterfaceAllowed().equals(PretupsI.YES)) {
        	
            channelUserVO.setLoginID(r.get(colIt));
            colIt++;

            if (!BTSLUtil.isNullString(r.get(colIt))) {
                channelUserVO.setPassword(BTSLUtil.encryptText(r.get(colIt)));
                colIt++;
            }
        } else {
        	colIt++;
            channelUserVO.setLoginID(null);
            colIt++;//now pt to mobile num
            channelUserVO.setPassword(null);
        }
        
        String[] msisdnArray = null;
        String[] pinArray = null;
        ArrayList arr = null;
        UserPhoneVO userPhoneVO = null;
        String tempMsisdn = r.get(colIt).trim();
        colIt++;
        msisdnArray = tempMsisdn.split(",");
        channelUserVO.setMsisdn(msisdnArray[0]);
        channelUserVO.setMultipleMsisdnlist(tempMsisdn);
        
        if (!SystemPreferences.AUTO_PIN_GENERATE_ALLOW) {
        	
            pinArray = r.get(colIt).trim().split(",");
            colIt++;
            channelUserVO.setSmsPin(BTSLUtil.encryptText(pinArray[0]));
            final int totalPin = pinArray.length;
            if (noOfMsisdn - totalPin != 0) {
                final String[] temp = new String[noOfMsisdn];
                System.arraycopy(pinArray, 0, temp, 0, pinArray.length);
                for (int m = 0, n = noOfMsisdn - totalPin; m < n; m++) {
                    temp[totalPin + m] = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN);
                }
                pinArray = temp;
            }
        } else {
            pinArray = new String[noOfMsisdn];
            for (int m = 0, n = noOfMsisdn; m < n; m++) {
                pinArray[m] = operatorUtili.generateRandomPin();
            }
            channelUserVO.setSmsPin(BTSLUtil.encryptText(pinArray[0]));
            colIt++;
        }
        
        channelUserVO.setGeographicalCode(r.get(colIt));
        colIt++;
        channelUserVO.setGroupRoleCode(r.get(colIt));
        colIt++;
        channelUserVO.setServiceList(newServiceList);
        ++colIt;
        channelUserVO.setOutletCode(r.get(colIt));
        colIt++;
        channelUserVO.setSubOutletCode(r.get(colIt));
        colIt++;
        
        if (userType.equals(PretupsI.OPERATOR_USER_TYPE) || (userType.equals(PretupsI.CHANNEL_USER_TYPE) && SystemPreferences.BATCH_USER_PROFILE_ASSIGN)) {
            channelUserVO.setCommissionProfileSetID(r.get(colIt).toUpperCase().trim());
            colIt++;
            channelUserVO.setTransferProfileID(r.get(colIt).toUpperCase().trim());
            colIt++;
            channelUserVO.setUserGrade(r.get(colIt).toUpperCase().trim());
            colIt++;
            //Handling of All Grade in case of commission profile not defined with specific geography and Grade
        	if("ALL".equalsIgnoreCase(channelUserVO.getUserGrade())){
        		CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
        		ArrayList<GradeVO>  channelGradeList= categoryGradeDAO.loadGradeList(con,channelUserVO.getCategoryCode());
        		if(channelGradeList.get(0)!=null && channelGradeList.size()>0) {
            		String gradeCode = channelGradeList.get(0).getGradeCode();
            		if(!BTSLUtil.isNullString(gradeCode)){
            			channelUserVO.setUserGrade(gradeCode);
            			if (log.isDebugEnabled())
            				log.debug(METHOD_NAME, "In case of ALL Grade, setting Grade ="+gradeCode);
            		}
        		}
        	}
            if (SystemPreferences.PTUPS_MOBQUTY_MERGD) {
            	String mCommerceServiceAllow = r.get(colIt).trim();
            	colIt++;
                if (BTSLUtil.isNullString(mCommerceServiceAllow)) {
                    channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
                } else {
                    channelUserVO.setMcommerceServiceAllow(mCommerceServiceAllow);
                }
                channelUserVO.setMpayProfileID(r.get(colIt).toUpperCase().trim());
                colIt++;
            } else {
                channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
                channelUserVO.setMpayProfileID("");
            }
            final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, userSessionVO
                .getNetworkID(), categoryCodeInSheet)).booleanValue();// Here
            // excelArr[r][5]=category_code
            if (isTrfRuleTypeAllow) {
                channelUserVO.setTrannferRuleTypeId(r.get(colIt));
                colIt++;
            }
            // Added for Rsa Authentication
            if (SystemPreferences.RSA_AUTHENTICATION_REQUIRED) {
                boolean rsaRequired = false;
                try {
                    rsaRequired = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.RSA_AUTHENTICATION_REQUIRED, userSessionVO.getNetworkID(),
                        categoryCodeInSheet)).booleanValue();
                } catch (Exception e) {
                    log.errorTrace(METHOD_NAME, e);
                }
                if (rsaRequired) {
                    final String st = r.get(colIt).trim();
                    colIt++;
                    if (!BTSLUtil.isNullString(st)) {
                        channelUserVO.setRsaFlag(st.toUpperCase().trim());
                    } else {
                        channelUserVO.setRsaFlag(PretupsI.NO);
                    }
                }
            }
            if (SystemPreferences.AUTH_TYPE_REQ) {
                final String st = r.get(colIt).trim();
                colIt++;
                if (!BTSLUtil.isNullString(st)) {
                    channelUserVO.setAuthTypeAllowed(st.toUpperCase().trim());
                } else {
                    channelUserVO.setAuthTypeAllowed(PretupsI.NO);
                }

            }
        } else {
            channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
            channelUserVO.setMpayProfileID("");
            channelUserVO.setCommissionProfileSetID("");
            channelUserVO.setTransferProfileID("");
            channelUserVO.setUserGrade("");
            channelUserVO.setTrannferRuleTypeId("");
            channelUserVO.setRsaFlag(PretupsI.NO);
        }
        
        if (categoryVO.getLowBalAlertAllow().equalsIgnoreCase(PretupsI.YES)) {
            channelUserVO.setLowBalAlertAllow(r.get(colIt).toUpperCase().trim());
            colIt++;
        } else {
            channelUserVO.setLowBalAlertAllow(PretupsI.NO);
        }
        
        // Added for latitude and longitude
        channelUserVO.setLongitude(r.get(colIt).trim());
        colIt++;
        channelUserVO.setLatitude(r.get(colIt).trim());
        colIt++;
        channelUserVO.setDocumentType(r.get(colIt).trim());
        colIt++;
        channelUserVO.setDocumentNo(r.get(colIt).trim());
        colIt++;
        channelUserVO.setPaymentType(r.get(colIt).trim());
        colIt++;
        if(SystemPreferences.USER_VOUCHERTYPE_ALLOWED) {
        channelUserVO.setVoucherList(newVoucherTypeList);
        }
        // Addition ends
        channelUserVO.setUserProfileID(channelUserVO.getUserID());
        // end Zebra and Tango

        channelUserVO.setUserCode(channelUserVO.getMsisdn());
        channelUserVO.setModifiedBy(userSessionVO.getUserID());
        channelUserVO.setModifiedOn(currentDate);
        channelUserVO.setCreatedBy(userSessionVO.getUserID());
        channelUserVO.setCreatedOn(currentDate);
        channelUserVO.setUserType(PretupsI.CHANNEL_USER_TYPE);

        if (channelUserVO.getCategoryVO().getSequenceNumber() == 1) {
            channelUserVO.setParentID(PretupsI.ROOT_PARENT_ID);
        }
        
        arr = new ArrayList();
        for (int i = 0; i < noOfMsisdn; i++) {
            userPhoneVO = new UserPhoneVO();
            userPhoneVO.setMsisdn(msisdnArray[i]);
            userPhoneVO.setShowSmsPin(BTSLUtil.encryptText(pinArray[i]));
            userPhoneVO.setSmsPin(BTSLUtil.encryptText(pinArray[i]));
            arr.add(userPhoneVO);
            final String lang_country[] = (channelUserVO.getLanguage()).split("_");
            userPhoneVO.setPhoneLanguage(lang_country[0]);
            userPhoneVO.setCountry(lang_country[1]);
        }
        channelUserVO.setMsisdnList(arr);
        
	    log.debug(METHOD_NAME, "Exit");
		return channelUserVO;
	}
	
	private boolean validateRow(Connection con, ArrayList<String> r, int rowNum,  ArrayList fileErrorList, Locale locale, HashMap categoryMap, HashMap languageMap, OperatorUtilI operatorUtili, UserDAO userDAO2, UserVO userSessionVO, HashMap masterMap) throws BTSLBaseException {
		final String METHOD_NAME = "validateRow";
	     if (log.isDebugEnabled()) {
	         log.debug(METHOD_NAME, "entered");
	     }
		
	    int reptRowNo = 0;
	    String msisdnPrefix = null;
	    String filteredMsisdn = null;
	    ListValueVO listVO = null;
	    int voucherTypeLen=0;
	    String voucherTypeArr[]=null;
	    ArrayList newVoucherTypeList=new ArrayList();
	    final ArrayList mPayProfileIDList = null;
	    GradeVO gradeVO = null;
	    TransferProfileVO profileVO = null;
	    CommissionProfileSetVO commissionProfileSetVO = null;
	    ArrayList newServiceList = new ArrayList();
	    UserRolesVO rolesVO = null;
	    String serviceArr[] = null;
	    boolean found = false;
	    Iterator itr = null;
	    NetworkPrefixVO networkPrefixVO = null;
	    int noOfMsisdn = 0;
	    ListValueVO errorVO = null;
	    String errorMessage = "";
        Set passwordErrSetKey = null;
		boolean fileValidationErrorExists = false;
		String tempMsisdn = null;
		int colIt = 0;
		String cellValue = "";
		String categoryCodeInSheet;
		String ssnCodeInSheet = null;
		HashMap error_messageMap = null;
		CategoryVO categoryVO = null;
		
		//col 1: Parent Mobile Number
		cellValue = r.get(colIt++).trim();
		cellValue = PretupsBL.getFilteredMSISDN(cellValue);
		if (!BTSLUtil.isValidMSISDN(cellValue)) {
            errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Parent MSISDN"}));
            fileErrorList.add(errorVO);
            fileValidationErrorExists = true;
            return true;
        }
		r.set(colIt-1, cellValue);
		
		//col 2: User Name Prefix
		cellValue = r.get(colIt++).trim();
		if (BTSLUtil.isNullString(cellValue)){
        	//If in Constants.props IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES  is not null and Y, give default value
            if(!BTSLUtil.isNullString(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES"))
               && PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES")) ){
            	cellValue="MR";
            	r.set(colIt-1, cellValue);
			}
            //If in Constants.props IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES  is null or N, add error message
			else{
				errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"Username prefix"}));
				fileErrorList.add(errorVO);
				fileValidationErrorExists=true;
				return true;
			}
        } else // Validate Prefixes Mr/Miss etc from the List
        {
            cellValue = cellValue.trim();
            final ArrayList prefixList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_USER_PREFIX_LIST);
            listVO = BTSLUtil.getOptionDesc(cellValue, prefixList);
            if (BTSLUtil.isNullString(listVO.getValue())) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Username prefix"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            }
        }
		r.set(colIt-1, cellValue);
		
		//col 3: UName validation
		cellValue = r.get(colIt++).trim();
		if (!SystemPreferences.IS_FNAME_LNAME_ALLOWED) {
			if (BTSLUtil.isNullString(cellValue)) {//mandatory
				errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"First name"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
			}else {
                cellValue = cellValue.trim();
                // Check User Name length
                if (cellValue.length() > 80) {
                	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"Username", "80"}));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    return true;
                }
            }
		} else {
            // **********First Name validation
        	Pattern p = Pattern.compile("[^0-9a-zA-Z ]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(cellValue);
            if(m.find()){
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"First name"}));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    return true;
            }
            if (BTSLUtil.isNullString(cellValue)) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.FIRST_NAME_BLANK, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FIRST_NAME_BLANK,new String[] {""}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            } else {
                cellValue = cellValue.trim();
                // Check User Name length
                if (cellValue.length() > 40) {
                	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"First name", "40"}));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    return true;
                }
            }
            r.set(colIt-1, cellValue);
            
            //col 4: Lname validation
            cellValue = r.get(colIt++).trim();
            if (!BTSLUtil.isNullString(cellValue)) {
                cellValue = cellValue.trim();
                // Check User Name length
                m=p.matcher(cellValue);
                if (cellValue.length() > 40) {
                	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"Last name", "40"}));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    return true;
                }
                if(m.find()){
                		errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Last name"}));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        return true;
                }
            }
        }
		r.set(colIt-1, cellValue);
		
		//col 5: Short name validation
        cellValue = r.get(colIt++).trim();
		if (BTSLUtil.isNullString(cellValue)) { // Short Name mandatory
			
        	if(!BTSLUtil.isNullString(Constants.getProperty("SECURITY_QUESTION_FIELD")) && (Constants.getProperty("SECURITY_QUESTION_FIELD").equals("SHORT_NAME")))
        	{	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.SHORT_NAME_BLANK, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SHORT_NAME_BLANK,new String[] {""}));
				fileErrorList.add(errorVO);
				fileValidationErrorExists=true;
				return true;
        	}
        }
        else  {
            cellValue = cellValue.trim();
            if (cellValue.length() > 15) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Short name"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            }
        }
		r.set(colIt-1, cellValue);
		
		//col 6: category code validation
        cellValue = r.get(colIt++).trim();
        if (BTSLUtil.isNullString(cellValue)){
        		errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"Category code"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            } else {
                cellValue = cellValue.trim();

                // Category code must be validated from the master
                // sheet.
                if (!categoryMap.containsKey(cellValue)) {
                	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Category Code"}));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    return true;
                } else {
                    // If category code exists then check if WEB/SMS is allowed for that category
                    categoryVO = (CategoryVO) categoryMap.get(cellValue);
                    categoryCodeInSheet = cellValue;
                }
            }
        masterMap.put("categoryVO", categoryVO);
        masterMap.put("categoryCodeInSheet", categoryCodeInSheet);
        r.set(colIt-1, cellValue);
        
        //col 7: external code validation
        cellValue = r.get(colIt++).trim();
        if (SystemPreferences.EXTERNAL_CODE_MANDATORY_FORUSER){
                if (BTSLUtil.isNullString(cellValue)){//mandatory
                	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.EXTSYS_REQ_EXTERNALCODE_BLANK_OR_LENGTH_EXCEEDS, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_REQ_EXTERNALCODE_BLANK_OR_LENGTH_EXCEEDS,new String[] {""}));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    return true;
                }
        }
        if (!BTSLUtil.isNullString(cellValue)) {
             cellValue = cellValue.trim();
             if (cellValue.length() > 20) {
            	 errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.EXTSYS_REQ_EXTERNALCODE_BLANK_OR_LENGTH_EXCEEDS, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_REQ_EXTERNALCODE_BLANK_OR_LENGTH_EXCEEDS,new String[] {""}));
                 fileErrorList.add(errorVO);
                 fileValidationErrorExists = true;
                 return true;
              }
              if (!BTSLUtil.isAlphaNumeric(cellValue)){
            	  errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.EXTERNALCODE_ALPHANUMERIC, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTERNALCODE_ALPHANUMERIC,new String[] {""}));
                  fileErrorList.add(errorVO);
                  fileValidationErrorExists = true;
                  return true;
              }
                
        }
        r.set(colIt-1, cellValue);
        
        //col 8: Contact person validation 
        cellValue = r.get(colIt++).trim();
        if(BTSLUtil.isNullString(cellValue))
        {
        	if(!BTSLUtil.isNullString(Constants.getProperty("SECURITY_QUESTION_FIELD")) && (Constants.getProperty("SECURITY_QUESTION_FIELD").equals("CONTACT_PERSON")))
        	{
        		errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"Contact Person"}));
				fileErrorList.add(errorVO);
				fileValidationErrorExists=true;
				return true;
        	}
        }
        else {
            cellValue = cellValue.trim();
            if (cellValue.length() > 80) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"Contact person", "80"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            }
            Pattern p = Pattern.compile("[^0-9a-zA-Z ]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(cellValue);
            if(m.find()){
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Contact Person"}));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    return true;
            }
        } 
        r.set(colIt-1, cellValue);
        
        
        //col 9: address1 validation 
        cellValue = r.get(colIt++).trim();
        if (!BTSLUtil.isNullString(cellValue)) {
            cellValue = cellValue.trim();
            if (cellValue.length() > 50) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"Address", "50"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            }
        }
        r.set(colIt-1, cellValue);
        
        //col 10: city validation 
        cellValue = r.get(colIt++).trim();
        if (!BTSLUtil.isNullString(cellValue)) {
            cellValue = cellValue.trim();
            if (cellValue.length() > 30) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"City", "30"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            }
        }
        r.set(colIt-1, cellValue);
        
        //col 11: state validation 
        cellValue = r.get(colIt++).trim();
        if (!BTSLUtil.isNullString(cellValue)) {
            cellValue = cellValue.trim();
            if (cellValue.length() > 30) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"State", "30"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            }
        }
        r.set(colIt-1, cellValue);
        
        //col 12: RSASecurID/SSN validation 
        cellValue = r.get(colIt++).trim();
        if(BTSLUtil.isNullString(cellValue))
        {
        	if(!(BTSLUtil.isNullString(Constants.getProperty("SECURITY_QUESTION_FIELD"))) && ((Constants.getProperty("SECURITY_QUESTION_FIELD").equals("SSN")) || SystemPreferences.RSA_AUTHENTICATION_REQUIRED))
        	{
        		errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"SSN"}));
                fileErrorList.add(errorVO);
				fileValidationErrorExists=true;
				return true;
        	}
        }
        else {
            cellValue = cellValue.trim();
            ssnCodeInSheet = cellValue;
            if (cellValue.length() > 15) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"SSN", "15"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            }
        }
        r.set(colIt-1, cellValue);
        
        //col 13: Country validation 
        cellValue = r.get(colIt++).trim();
        if (!BTSLUtil.isNullString(cellValue)) {
            cellValue = cellValue.trim();
            if (cellValue.length() > 20) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"Country", "20"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            }
        }
        r.set(colIt-1, cellValue);
        
        //col 14: Company validation 
        cellValue = r.get(colIt++).trim();
        if (!BTSLUtil.isNullString(cellValue)) {
            cellValue = cellValue.trim();
            if (cellValue.length() > 80) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"Company", "80"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            }
        }
        r.set(colIt-1, cellValue);
        
        //col 15: Fax validation 
        cellValue = r.get(colIt++).trim();
        if (!BTSLUtil.isNullString(cellValue)) {
            cellValue = cellValue.trim();
            Pattern p = Pattern.compile("[^0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(cellValue);
            if (cellValue.length() > 60||m.find()) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"FAX"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            }
        }
        r.set(colIt-1, cellValue);
        
        //col 16: EmailID validation 
        cellValue = r.get(colIt++).trim();
        if (BTSLUtil.isStringContain(SystemPreferences.USER_CREATION_MANDATORY_FIELDS, "email")) {

            if (BTSLUtil.isNullString(cellValue.trim())) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"EmailID"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;

            }

        }
        if (!BTSLUtil.isNullString(cellValue)) {
            if (!BTSLUtil.validateEmailID(cellValue.trim())) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"EmailID"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            }
            if(cellValue.length()>60){
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"EmailID", "60"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            }
        }
        r.set(colIt-1, cellValue);
        
        //col 17: Language validation 
        cellValue = r.get(colIt++).trim();
        if (BTSLUtil.isNullString(cellValue)){//mandatory
            	//If in Constants.props IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES  is not null and Y, give default value
                if(!BTSLUtil.isNullString(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES"))
                	&& PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES"))){
                	cellValue=(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)+"_"+(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
                	r.set(colIt-1, cellValue);
				}
                //If in Constants.props IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES  is null or N, add error message
				else{
					errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"Language"}));
	                fileErrorList.add(errorVO);
					fileValidationErrorExists=true;
					return true;
				}
            } else // Validate language en/fr etc from the List
            {
                cellValue = cellValue.trim();
                r.set(colIt-1, cellValue);
                if (!languageMap.containsKey(cellValue)||cellValue.length()>10) {
                	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Language"}));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    return true;
                }
              
            }
        r.set(colIt-1, cellValue);
        
        //col 18: Login ID validation 
        cellValue = r.get(colIt++).trim();
        String login_ID = null;// Loginid & password is mandatory. Password will be "0000" if it is blank.
        if (PretupsI.YES.equals(categoryVO.getWebInterfaceAllowed())) {
            if (BTSLUtil.isNullString(cellValue) && SystemPreferences.LOGIN_PASSWORD_ALLOWED) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"LoginID"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            } else if (!BTSLUtil.isNullString(cellValue)) {
                cellValue = cellValue.trim();
                if (cellValue.length() > 20) {
                	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"LoginID", "20"}));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    return true;
                }
                if (!SystemPreferences.SPACE_ALLOW_IN_LOGIN && cellValue.contains(" ")) {
                	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LOGINID_SPACE_NOT_ALLOWED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LOGINID_SPACE_NOT_ALLOWED,new String[] {""}));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    return true;
                }
                Pattern p = Pattern.compile("([a-zA-Z\\d\\s_]*)");
                Matcher m = p.matcher(cellValue);
                boolean b = m.matches();
                if(!b)
              	{
                	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"LoginID"}));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    return true;
              	}   
                if (userDAO.isUserLoginExist(con, cellValue, null)){
                	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LOGINID_EXIST_ALREADY, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LOGINID_EXIST_ALREADY,new String[] {"LoginID"}));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    return true;
                }
                login_ID = cellValue;
            }
            r.set(colIt-1, login_ID);
            
            //col 19: password validation
            cellValue = r.get(colIt++).trim();
            String password = cellValue;
            if (BTSLUtil.isNullString(password) && !BTSLUtil.isNullString(login_ID)) {
                if (SystemPreferences.AUTO_PWD_GENERATE_ALLOW) {
                    password = operatorUtili.generateRandomPassword();
                } else {
                    password = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD);
                }
                cellValue = password = password.trim();
                r.set(colIt-1, cellValue);
            } else if (!BTSLUtil.isNullString(cellValue)) {
                cellValue = password = password.trim();
                r.set(colIt-1, cellValue);
                               
                Matcher m = Pattern.compile("(.+)\\1+").matcher(cellValue);
                if(m.find()){
                	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.CHANGE_PASSWORD_NEWPASSWORD_CONSSECUTIVE, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CHANGE_PASSWORD_NEWPASSWORD_CONSSECUTIVE,new String[] {""}));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    return true;
                }
                
                // For OCI changes 
                error_messageMap = operatorUtili.validatePassword(login_ID, cellValue);
                if (!error_messageMap.isEmpty()) {
                	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.ERROR_ERP_CHNL_USER_INVALID_PASSWORD, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_INVALID_PASSWORD,new String[] {""}));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    return true;
                }
                
                r.set(colIt-1, cellValue);
            }
            //col 20: msisdn validation
            cellValue = r.get(colIt++).trim();
        }else {
        	colIt++;
        }
        
        if (BTSLUtil.isNullString(cellValue)) {
        	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"MSISDN"}));
            fileErrorList.add(errorVO);
            fileValidationErrorExists = true;
            return true;
        } else {

            noOfMsisdn = 0;
            cellValue = cellValue.trim();
            final String[] msisdnInput = cellValue.split(",");
            noOfMsisdn = msisdnInput.length;
            // Integer.parseInt(categoryVO.getMaxTxnMsisdn())
            if (msisdnInput.length > categoryVO.getMaxTxnMsisdnInt()) {
                errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.MSISDN_COUNT_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MSISDN_COUNT_EXCEED,new String[] {categoryVO.getMaxTxnMsisdn()}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            } else {

                final ArrayList multipleMsisdnErr = new ArrayList();
                final HashMap hm = new HashMap();
                int val = 0;
                final StringBuffer msisdnString = new StringBuffer();
                int   msisdnInputs=msisdnInput.length;
                for (int k = 0, j =msisdnInputs; k < j; k++) {

                    try {

                        msisdnInput[k] = msisdnInput[k].trim();
                        filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdnInput[k]);
                        msisdnString.append(filteredMsisdn);
                        msisdnString.append(",");
                    } catch (Exception ee) {
                    	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"MSISDN"}));
                        fileErrorList.add(errorVO);
                        multipleMsisdnErr.add(errorVO);
                        if (multipleMsisdnErr.size() >= 2) {
                            reptRowNo--;
                        }
                        fileValidationErrorExists = true;
                        continue;
                    }
                    if (hm.containsKey(filteredMsisdn)) {
                        // Key already present... update the value.
                        val = ((Integer) hm.get(filteredMsisdn)).intValue();
                        val++;
                        hm.put(filteredMsisdn, new Integer(val));
                    } else {
                        hm.put(filteredMsisdn, new Integer(1));
                    }

                    if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"MSISDN"}));
                        fileErrorList.add(errorVO);
                        multipleMsisdnErr.add(errorVO);
                        if (multipleMsisdnErr.size() >= 2) {
                            reptRowNo--;
                        }
                        fileValidationErrorExists = true;
                        continue;
                    }
                    if (filteredMsisdn.length() > 15) {
                    	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"MSISDN", "15"}));
                        fileErrorList.add(errorVO);
                        multipleMsisdnErr.add(errorVO);
                        if (multipleMsisdnErr.size() >= 2) {
                            reptRowNo--;
                        }
                        fileValidationErrorExists = true;
                        continue;
                    }
                    // Check for network prefix
                    msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get the prefix of msisdn
                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                    if (networkPrefixVO == null) {
                    	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.NONE_NETWORK_PREFIX_FOUND, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NONE_NETWORK_PREFIX_FOUND,new String[] {filteredMsisdn}));
                        fileErrorList.add(errorVO);
                        multipleMsisdnErr.add(errorVO);
                        if (multipleMsisdnErr.size() >= 2) {
                            reptRowNo--;
                        }
                        fileValidationErrorExists = true;
                        continue;
                    } else if (!networkPrefixVO.getNetworkCode().equals(userSessionVO.getNetworkID())) {
                    	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.MSISDN_NOT_SUPPORTING_NETWORK, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MSISDN_NOT_SUPPORTING_NETWORK,new String[] {filteredMsisdn}));
                        fileErrorList.add(errorVO);
                        multipleMsisdnErr.add(errorVO);
                        if (multipleMsisdnErr.size() >= 2) {
                            reptRowNo--;
                        }
                        fileValidationErrorExists = true;
                        continue;
                    }
                    
                    if (userDAO.isMSISDNExist(con, filteredMsisdn, null)) {// if given msisdn already exist in system
                    	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.MSISDN_EXIST, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MSISDN_EXIST,new String[] {filteredMsisdn}));
                        fileErrorList.add(errorVO);
                        multipleMsisdnErr.add(errorVO);
                        if (multipleMsisdnErr.size() >= 2) {
                            reptRowNo--;
                        }
                        fileValidationErrorExists = true;
                        continue;
                    }
                    
                } // End for Loop
                if (fileValidationErrorExists) {
                    return true;
                }

                if (hm.size() != noOfMsisdn) {
                	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.MULTIPLE_DUPLICATE_MSISDN_FOUND, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MULTIPLE_DUPLICATE_MSISDN_FOUND,new String[] {""}));
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    reptRowNo++;
                    return true;
                }
            }// end of else
        }
        masterMap.put("noOfMsisdn", noOfMsisdn);
        r.set(colIt-1, cellValue);
        
        //col 21: PIN validation
        cellValue = r.get(colIt++).trim();
        if (PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {// PIN is a Mandatory field.
            String pin = cellValue;
            final StringBuffer pinString = new StringBuffer();
            if (BTSLUtil.isNullString(pin)) {
                pin = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN);
                for (int i = 0; i < noOfMsisdn; i++) {
                    pinString.append(pin);
                    pinString.append(",");
                }
                cellValue = pin = pin.trim();
                r.set(colIt-1, pinString.toString());
            } else {
                cellValue = pin = pin.trim();
                final String[] pinInput = pin.split(",");
                for (int k = 0, l = pinInput.length; k < l; k++) {
                    pin = pinInput[k];
                                        
                    Matcher m = Pattern.compile("(\\d+)\\1+").matcher(cellValue);
                    if(m.find()){
                    	errorVO = new ListValueVO(String.valueOf(rowNum), pin,PretupsErrorCodesI.PIN_CONSECUTIVE_CHAR, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PIN_CONSECUTIVE_CHAR,new String[] {""}));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                    Matcher m2 = Pattern.compile("[^0-9 ]").matcher(cellValue);
                    if(m2.find()){
                    	errorVO = new ListValueVO(String.valueOf(rowNum), pin,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"PIN"}));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                    
                    error_messageMap = null;
                    error_messageMap = operatorUtili.pinValidate(pin);
                    if (!error_messageMap.isEmpty()) {
                    	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.ERROR_INVALID_PIN, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_INVALID_PIN,new String[] {""}));
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        return true;
                    }
                    
                }// End of for loop
                
            } 
            if(fileValidationErrorExists) return true;
            r.set(colIt-1, cellValue);
        }
        
        //col 22: Geography validation
        cellValue = r.get(colIt++).trim();
        if (BTSLUtil.isNullString(cellValue)){// mandatory field
        	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"Geography"}));
            fileErrorList.add(errorVO);
            fileValidationErrorExists = true;
            return true;
           }else{
			cellValue = cellValue.trim();
			// Geographies will be validated from the master
			// sheet, check weather the geography
			// lie under the category
			final ArrayList geographyList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_GEOGRAPHY_LIST);
			for (int i = 0; i < geographyList.size(); i++) {
				UserGeographiesVO userGeographiesVO = (UserGeographiesVO) geographyList
						.get(i);
				if (cellValue.equals(userGeographiesVO
						.getGraphDomainCode())) {
					found = true;
					break;
				}
			}
			if (!found) {
				errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Geography code"}));
	            fileErrorList.add(errorVO);
				fileValidationErrorExists = true;
				return true;
			}
		}
        r.set(colIt-1, cellValue);
        
        //col 23: Group Role Code
        cellValue = r.get(colIt++).trim();
        if (!BTSLUtil.isNullString(cellValue)) {
            cellValue = cellValue.trim();
            if (cellValue.trim().length() > 20) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"Group Role Code", "20"}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            }
            // Check that the group role will be validated corr.
            // to master sheet.
            final ArrayList groupList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_GROUP_ROLE_LIST);
            for (int i = 0; i < groupList.size(); i++) {
                rolesVO = (UserRolesVO) groupList.get(i);
                // Map the category code entered in the xls file
                // with master data
                if (rolesVO.getCategoryCode().equals(categoryCodeInSheet))// excelArr[r][z-13]))
                {
                    if (!cellValue.equals(rolesVO.getRoleCode())) {
                        found = false;
                    } else {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
            	errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.GRP_ROLE_NOT_UNDER_CATEGORY, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GRP_ROLE_NOT_UNDER_CATEGORY,new String[] {cellValue, categoryCodeInSheet}));
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;
                return true;
            }	
        } else {
        	List<UserRolesVO> list = (ArrayList) masterMap.get(PretupsI.BATCH_USR_GROUP_ROLE_LIST);
            UserRolesVO rolesVO1 = null;
            if (!list.isEmpty()) {
            	int  lists=list.size();
                for (int i = 0; i <lists; i++) {
                	rolesVO1 = (UserRolesVO) list.get(i);
                	if(rolesVO1.getCategoryCode().equals(categoryCodeInSheet)&&rolesVO1.getDefaultType().equals(PretupsI.YES)){
                		cellValue=rolesVO1.getRoleCode();
                	}
                }
            }
            
            if(("").equals(cellValue)||cellValue==null)
            {
            		errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.DEFAULT_GROUP_ROLE_NOT_FOUND, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DEFAULT_GROUP_ROLE_NOT_FOUND,new String[] {""}));
                	fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    return true;
            }
            
            r.set(colIt-1, cellValue);
        }
        
        //col 24: Services validation
        cellValue = r.get(colIt++).trim();
        boolean validation = false;
        boolean invailedVoucher=false;
        if (PretupsI.YES.equals(categoryVO.getServiceAllowed())) {
           if (BTSLUtil.isNullString(cellValue)){
        	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"Service(s)"}));
	           fileErrorList.add(errorVO);
               fileValidationErrorExists = true;
               return true;
           } else {
               serviceArr = cellValue.split(",");
               // If there will be no comma serviceArr.length
               // will be 1
               int serviceLen = serviceArr.length;
               ArrayList serviceList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_SERVICE_LIST);
               if (serviceLen > serviceList.size()) {
                   // Error: The services specified in the XLS
                   // file will be greater than the
                   // services applicable
            	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Services list"}));
    	           fileErrorList.add(errorVO);
                   fileValidationErrorExists = true;
                   return true;
               }
               if (serviceArr != null && serviceLen > 0) {
                   newServiceList = new ArrayList();
                   for (int i = 0; i < serviceLen; i++) {
                       serviceArr[i] = serviceArr[i].toUpperCase().trim();
                       if (!BTSLUtil.isNullString(BTSLUtil.getOptionDesc(serviceArr[i], serviceList).getLabel())) {
                           newServiceList.add(serviceArr[i]);
                       } else {
                       	if(!validation){
                       	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Services list"}));
             	           fileErrorList.add(errorVO);
                           fileValidationErrorExists = true;
                           validation = true;
                           return true;
                       	}
                       }
                   }//for loop ends
               }
           }

       }
       r.set(colIt-1, cellValue);
       masterMap.put("newServiceList", newServiceList);
       
       //col 25: outlet code validation
       cellValue = r.get(colIt++).trim();
       if (!BTSLUtil.isNullString(cellValue)) {
           // Check the MAX length
           cellValue = cellValue.trim();
           if (cellValue.length() > 10) {
        	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Outlet code"}));
	           fileErrorList.add(errorVO);
               fileValidationErrorExists = true;
               return true;
           }

           // Validate the Outlets from the Master Sheet
           listVO = BTSLUtil.getOptionDesc(cellValue, (ArrayList) masterMap.get(PretupsI.BATCH_USR_OUTLET_LIST));
           if (BTSLUtil.isNullString(listVO.getValue())) {
        	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Outlet code"}));
	           fileErrorList.add(errorVO);
               fileValidationErrorExists = true;
               return true;
           }
       } else {
           // Insert the Default outlet TCOM
           cellValue = PretupsI.OUTLET_TYPE_DEFAULT;
           r.set(colIt-1, cellValue);
       }
       
       //col 26: sub-outlet code validation
       cellValue = r.get(colIt++).trim();
       boolean flag = false;
       int prevcolIt = colIt-2;
       String prevCellValue=  r.get(prevcolIt);
       final ArrayList subOutletList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_SUBOUTLET_LIST);
       if (BTSLUtil.isNullString(cellValue)) {
           for (int k = 0, l = subOutletList.size(); k < l; k++) {
               flag = false;
               listVO = (ListValueVO) subOutletList.get(k);
               final String sub[] = listVO.getValue().split(":");
               if (prevCellValue.equals(sub[1])) {
                   flag = true;
                   r.set(colIt-1, sub[0]);
                   break;
               }
           }
       } else if (cellValue.trim().length() > 10) {
    	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"Sub-outlet code", "10"}));
           fileErrorList.add(errorVO);
           fileValidationErrorExists = true;
           return true;
       } else {
           for (int k = 0, l = subOutletList.size(); k < l; k++) {
               flag = false;
               listVO = (ListValueVO) subOutletList.get(k);
               final String sub[] = listVO.getValue().split(":");
               if (prevCellValue.equals(sub[1])) {
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
    	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Sub-outlet code"}));
           fileErrorList.add(errorVO);
           fileValidationErrorExists = true;
           return true;
       }
       r.set(colIt-1, cellValue);
       
       //col 27: Commission profile validation
       cellValue = r.get(colIt++).trim();
       if (userSessionVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE) || (userSessionVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE) && SystemPreferences.BATCH_USER_PROFILE_ASSIGN)) {
           if (BTSLUtil.isNullString(cellValue)) {
        	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"Comission profile"}));
               fileErrorList.add(errorVO);
               fileValidationErrorExists = true;
               return true;
           } else {
               cellValue = cellValue.trim();
               ArrayList commPrfList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_COMM_LIST);
               for (int i = 0; i < commPrfList.size(); i++) {
                   commissionProfileSetVO = (CommissionProfileSetVO) commPrfList.get(i);
                   if (commissionProfileSetVO.getCategoryCode().equals(categoryCodeInSheet))// excelArr[r][z-15]))
                   { // first match the profile ids
                       if (!cellValue.equals(commissionProfileSetVO.getCommProfileSetId())) {
                           found = false; // profile-id match
                           // failed
                       }else { // profile-id match success
                           found = true; // assume grade, geog,
                           // id match found. Now
                           // check against the
                           // commissionProfileSetVO
                           // data
                           prevcolIt = colIt-6;//pt to geo code cell
                           prevCellValue = r.get(prevcolIt);
                           int forwacolIt= colIt+1;
                           String forwCellValue = r.get(colIt+1);//gradeCode
                           if (!prevCellValue.equals(commissionProfileSetVO.getGrphDomainCode()) || !forwCellValue.equals(commissionProfileSetVO
                               .getGradeCode())) {
                               found = false; // geog, grade
                               // mismatch

                           }
                           if ("ALL".equals(commissionProfileSetVO.getGrphDomainCode()) && forwCellValue.equals(commissionProfileSetVO.getGradeCode())) {
                               found = true; // check
                               // bypassed....geog
                               // in vo="ALL",
                               // grades in the
                               // vo and excel
                               // match
                               break;
                           } 
                           else if ("ALL".equals(commissionProfileSetVO.getGradeCode()) && prevCellValue.equals(commissionProfileSetVO
                               .getGrphDomainCode())) {
                               found = true; // check
                               // bypassed....grade
                               // in vo="ALL",
                               // geogs in the vo
                               // and excel match
                               break;
                           } else if ("ALL".equals(commissionProfileSetVO.getGrphDomainCode()) && "ALL".equals(commissionProfileSetVO.getGradeCode())) {
                               found = true; // check
                               // bypassed....grade
                               // in vo="ALL",
                               // geog in the
                               // vo="ALL"
                               break;
                           }

                           if (found) {
                               break; // if found break, else
                               // continue with your
                               // quest.
                           }
                       }
                   }
               }
               if (!found) {
            	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.COMM_PROFILE_NOT_FOUND_UNDER_CATEGORY, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.COMM_PROFILE_NOT_FOUND_UNDER_CATEGORY,new String[] {cellValue, categoryCodeInSheet}));
                   fileErrorList.add(errorVO);
                   fileValidationErrorExists = true;
                   return true;
               }
           }
           r.set(colIt-1, cellValue);
           
           //col 28: Transfer profile validation
           cellValue = r.get(colIt++).trim();
           if (BTSLUtil.isNullString(cellValue)){//mandatory
        	   		errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"Transfer profile"}));
        	   		fileErrorList.add(errorVO);
        	   		fileValidationErrorExists = true;
        	   		return true;
              } else {
                   // Check the Transfer profile is valid for the
                   // category or not. Check from master sheet.
                   cellValue = cellValue.trim();
                   ArrayList transferPrfList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST);
                   for (int i = 0; i < transferPrfList.size(); i++) {
                       found = false;
                       profileVO = (TransferProfileVO) transferPrfList.get(i);
                       // Map the category code entered in the xls
                       // file with master data
                       if (profileVO.getCategory().equals(categoryCodeInSheet))// excelArr[r][z-16]))
                       {
                           if (!cellValue.equals(profileVO.getProfileId())) {
                               found = false;
                           } else {
                               found = true;
                               break;
                           }
                       }
                   }
                   if (!found) {
                	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.TRF_PROFILE_NOT_FOUND_UNDER_CATEGORY, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TRF_PROFILE_NOT_FOUND_UNDER_CATEGORY,new String[] {cellValue, categoryCodeInSheet}));
                       fileErrorList.add(errorVO);
                       fileValidationErrorExists = true;
                       return true;
                   }
               }
           r.set(colIt-1, cellValue);
           
           //col 29: Grade validation
           cellValue = r.get(colIt++).trim();
           if (BTSLUtil.isNullString(cellValue)){//mandatory
        	   		errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"Grade code"}));
   	   				fileErrorList.add(errorVO);
   	   				fileValidationErrorExists = true;
   	   				return true;
               } else {
                   // Grade will be validated from the master sheet
                   cellValue = cellValue.trim();
                   ArrayList gradeList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_GRADE_LIST);
                   for (int i = 0; i < gradeList.size(); i++) {
                       gradeVO = (GradeVO) gradeList.get(i);
                       if (gradeVO.getCategoryCode().equals(categoryCodeInSheet))// excelArr[r][z-19]))
                       {
                           if (!cellValue.equals(gradeVO.getGradeCode())) {
                           	//Handling of All Grade in case of commission profile not defined with specific geography and Grade
           					if("ALL".equalsIgnoreCase(cellValue)){
           						//check bypassed....GradeCode in vo="ALL"
           						found = true;
           					} else {
           						found=false;
           					}
                           } else {
                               found = true;
                               break;
                           }
                       }
                   }
                   if (!found) {
                	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Grade code"}));
      	   				fileErrorList.add(errorVO);
                       fileValidationErrorExists = true;
                       return true;
                   }
               }
           r.set(colIt-1, cellValue);
           
           //col 30: M-commerce service and mpay profile id validation
           cellValue = r.get(colIt++).trim();
           String forwCellValue  = r.get(colIt);//mpay profile id
           prevCellValue = r.get(colIt-2);
           if (SystemPreferences.PTUPS_MOBQUTY_MERGD) {
               if (BTSLUtil.isNullString(cellValue)){
                   cellValue = "";
                   r.set(colIt-1, cellValue);
                   forwCellValue = "";
                   r.set(colIt, forwCellValue);
               } else {
                   cellValue = (cellValue.trim()).toUpperCase();
                   if (cellValue.equals(PretupsI.YES)) {
                       if (BTSLUtil.isNullString(forwCellValue)) // MPay
                       // profileID
                       // is
                       // Mandatory
                       // field
                       {
                    	    errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"Mpay profile ID"}));
                           fileErrorList.add(errorVO);
                           fileValidationErrorExists = true;
                           return true;
                       } else {
                       	forwCellValue = forwCellValue.trim();
                           if (!mPayProfileIDList.contains(categoryCodeInSheet + ":" + prevCellValue + ":" + forwCellValue)) {
                        	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Mpay profile ID"}));
                               fileErrorList.add(errorVO);
                               fileValidationErrorExists = true;
                               return true;
                           }
                       }
                   } else {
                       cellValue = PretupsI.NO;
                       forwCellValue = "";
                   }
               }
               r.set(colIt-1, cellValue);
               r.set(colIt, forwCellValue);
               colIt++;
               //col 32: Transfer rule code validation
               cellValue = r.get(colIt++).trim();
           }
           //m-commerce service and mpay profile id val ends
           
           
           final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, userSessionVO.getNetworkID(),
                   categoryCodeInSheet)).booleanValue();
           if (isTrfRuleTypeAllow) {
               if (BTSLUtil.isNullString(cellValue)){//mandatory
            	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"Transfer Rule Code"}));
                   fileErrorList.add(errorVO);
                   fileValidationErrorExists = true;
                   return true;
               } else {
                   cellValue = cellValue.trim();
                   ArrayList trfRuleTypeList = (ArrayList) masterMap.get(PretupsI.BATCH_USR_TRF_RULE_LIST);
                   listVO = BTSLUtil.getOptionDesc(cellValue, trfRuleTypeList);
                   if (BTSLUtil.isNullString(listVO.getValue())) {
                	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Transfer Rule Code"}));
                       fileErrorList.add(errorVO);
                       fileValidationErrorExists = true;
                       return true;
                   }
               }
               r.set(colIt-1, cellValue);
               //col 31: RSA validation
               cellValue = r.get(colIt++).trim();
           }
           
           if (SystemPreferences.RSA_AUTHENTICATION_REQUIRED) {
               boolean rsaRequired = false;
               try {
                   rsaRequired = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.RSA_AUTHENTICATION_REQUIRED, userSessionVO.getNetworkID(),
                       categoryCodeInSheet)).booleanValue();
               } catch (Exception e) {
                   log.errorTrace(METHOD_NAME, e);
               }
               if (rsaRequired) {
                   categoryVO = (CategoryVO) categoryMap.get(categoryCodeInSheet);
                   if (PretupsI.YES.equals(categoryVO.getWebInterfaceAllowed())) {
                       if (PretupsI.YES.equalsIgnoreCase(cellValue)) {
                           if (BTSLUtil.isNullString(ssnCodeInSheet)) {
                        	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.SSN_NULL, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SSN_NULL,new String[] {""}));
                               fileErrorList.add(errorVO);
                               fileValidationErrorExists = true;
                               return true;
                           }
                       } else if ((PretupsI.NO.equalsIgnoreCase(cellValue) || BTSLUtil.isNullString(cellValue)) && !BTSLUtil
                           .isNullString(ssnCodeInSheet)) {
                    	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.SSN_NOT_NULL, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SSN_NOT_NULL,new String[] {""}));
                           fileErrorList.add(errorVO);
                           fileValidationErrorExists = true;
                           return true;
                       }
                   } else {
                       if ((!BTSLUtil.isNullString(cellValue) && !cellValue.equals(PretupsI.NO)) || !BTSLUtil.isNullString(ssnCodeInSheet)) {
                    	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.RSA_NOT_ALLOWED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RSA_NOT_ALLOWED,new String[] {""}));
                           fileErrorList.add(errorVO);
                           fileValidationErrorExists = true;
                           return true;
                       }
                   }
               } else {
                   if ((!BTSLUtil.isNullString(cellValue) && !cellValue.equals(PretupsI.NO)) || !BTSLUtil.isNullString(ssnCodeInSheet)) {
                	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.RSA_NOT_ALLOWED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RSA_NOT_ALLOWED,new String[] {""}));
                       fileErrorList.add(errorVO);
                       fileValidationErrorExists = true;
                       return true;
                   }
               }
               r.set(colIt-1, cellValue);
               //col 
               cellValue = r.get(colIt++);
           }
           
           // **************RSA validation ends
           // ******* Added for Authentication Type *********
           if (SystemPreferences.AUTH_TYPE_REQ) {
               if (!BTSLUtil.isNullString((cellValue).trim())) {
                   cellValue = (cellValue).trim();
               } else {
                   cellValue = PretupsI.NO;
               }
               r.set(colIt-1, cellValue);
               //col 
               cellValue = r.get(colIt++);

           }
       }
       
       // ***************************For Low balance alert validation Start*************************
       if (categoryVO.getLowBalAlertAllow().equalsIgnoreCase(PretupsI.YES)) {
           if (BTSLUtil.isNullString(cellValue)) {
               cellValue = PretupsI.NO;
           } else {
               cellValue = (cellValue.trim()).toUpperCase();
               if (!(cellValue.equalsIgnoreCase(PretupsI.YES) || cellValue.equalsIgnoreCase(PretupsI.NO))) {
            	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Allow low balance parameter"}));
                   fileErrorList.add(errorVO);
                   fileValidationErrorExists = true;
                   return true;
               }
           }
       }else {
           cellValue = PretupsI.NO;
       }
       r.set(colIt-1, cellValue); 
       cellValue = r.get(colIt++);
       //col: longitude validation
       if (cellValue.length() > 15) {
    	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"Longitude", "15"}));
           fileErrorList.add(errorVO);
           fileValidationErrorExists = true;
           return true;
       }
       r.set(colIt-1, cellValue); 
       cellValue = r.get(colIt++);
       //col: latitude validation
       if (cellValue.length() > 15) {
    	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"Latitude", "15"}));
           fileErrorList.add(errorVO);
           fileValidationErrorExists = true;
           return true;
       } 
       r.set(colIt-1, cellValue); 
       cellValue = r.get(colIt++);
       //col: Document type validation
       boolean docTypeExist = false;
       final ArrayList documentTypeList = (ArrayList) masterMap.get(PretupsI.USER_DOCUMENT_TYPE);
       if (!BTSLUtil.isNullString(cellValue)) {
           cellValue = cellValue.trim();
           listVO = BTSLUtil.getOptionDesc(cellValue, documentTypeList);
           if (BTSLUtil.isNullString(listVO.getValue())) {
        	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Document type"}));
               fileErrorList.add(errorVO);
               fileValidationErrorExists = true;
               return true;
           }else{
           	docTypeExist = true;
           }
       }
       r.set(colIt-1, cellValue); 
       cellValue = r.get(colIt++);
       //col: Document No validation
       boolean docNoExist = false;
		if (!BTSLUtil.isNullString(cellValue)) {
           cellValue = cellValue.trim();
           if (cellValue.length() > 20) {
        	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.LENGTH_EXCEED, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LENGTH_EXCEED,new String[] {"Document No", "20"}));
               fileErrorList.add(errorVO);
               fileValidationErrorExists = true;
               return true;
           }else{
           	docNoExist = true;
           }
		} 
		r.set(colIt-1, cellValue); 
		if ((docTypeExist && !docNoExist) || (!docTypeExist && docNoExist)) {
			errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.DOCUMENT_EITHER_BOTH_MANDATORY_OPTIONAL, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOCUMENT_EITHER_BOTH_MANDATORY_OPTIONAL,new String[] {""}));
            fileErrorList.add(errorVO);
            fileValidationErrorExists = true;
            return true;
		} 
		cellValue = r.get(colIt++);
		
		//col: Payment type validation
		String paymentTypeArr[] = null;
		if (!BTSLUtil.isNullString(cellValue)) {
            cellValue = cellValue.trim();
            if (cellValue.contains(",")) {
				paymentTypeArr = BTSLUtil.removeDuplicatesString(cellValue.split(","));
			} else {
				paymentTypeArr = new String[1];
				paymentTypeArr[0] = cellValue;
			}
            final ArrayList paymentTypeList = (ArrayList) masterMap.get(PretupsI.PAYMENT_INSTRUMENT_TYPE);
            for (int i = 0; i < paymentTypeArr.length; i++) {							 
				 listVO = BTSLUtil.getOptionDesc(paymentTypeArr[i].toUpperCase().trim(), paymentTypeList);
              if (BTSLUtil.isNullString(listVO.getValue())) {
            	   errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.PROPERTY_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID,new String[] {"Payment type"}));
                   fileErrorList.add(errorVO);
                   fileValidationErrorExists = true;	
				   break;
                 }                           						  
	       }
          if (fileValidationErrorExists) {       							
				return true;
			} 
            
        }
		r.set(colIt-1, cellValue);

        if(SystemPreferences.USER_VOUCHERTYPE_ALLOWED) {
        	cellValue = r.get(colIt++);
			newVoucherTypeList = new ArrayList();
			// ******************Vouchers validation starts*********************
			 if(!BTSLUtil.isNullString(cellValue)) {
				final ArrayList voucherTypeList= (ArrayList) masterMap.get(PretupsI.VOUCHER_TYPE_LIST);
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
					errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.VOUCHER_LIST_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOUCHER_LIST_INVALID,new String[] {""}));
	                fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					return true;
				}
				if (voucherTypeArr != null && voucherTypeLen > 0) {
					invailedVoucher = false;
					for (int i = 0; i < voucherTypeLen; i++) {
						voucherTypeArr[i] = voucherTypeArr[i].trim();
						if (!BTSLUtil.isNullString(BTSLUtil.getOptionDesc(voucherTypeArr[i], voucherTypeList).getLabel())) {
							newVoucherTypeList.add(voucherTypeArr[i]);
						} else {
							errorVO = new ListValueVO(String.valueOf(rowNum), cellValue,PretupsErrorCodesI.VOUCHER_LIST_INVALID, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOUCHER_LIST_INVALID,new String[] {""}));
							fileErrorList.add(errorVO);
							invailedVoucher = true;
							break;
						}
					}
					if (invailedVoucher) {
						fileValidationErrorExists = true;
						return true;
					}
				}
								        					   						
			}
			masterMap.put("newVoucherTypeList", newVoucherTypeList);
			r.set(colIt-1, cellValue);
		} 					
		
	    log.debug(METHOD_NAME, "Exit");
		return false;
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
	 
	public void createDirectory(String filePathConstemp) throws BTSLBaseException {

		String methodName = "createDirectory";
		File fileTempDir = new File(filePathConstemp);
		if (!fileTempDir.isDirectory()) {
			fileTempDir.mkdirs();
		}
		if (!fileTempDir.exists()) {
			log.debug("Directory does not exist : ", fileTempDir);
			throw new BTSLBaseException("OAuthenticationUtil", methodName,
				PretupsErrorCodesI.BATCH_UPLOAD_DIRECTORY_DO_NOT_EXISTS, PretupsI.RESPONSE_FAIL, null); // provide
																										// your own
		}
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
	
	public void validateFileSize(String fileSize, byte[] base64Bytes) throws BTSLBaseException{
		final String methodName = "validateFileSize";
		if (BTSLUtil.isNullorEmpty(fileSize)) {
			log.error(methodName, "VOMS_MAX_FILE_LENGTH is null in Constant.props");
			 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_FILE_SIZE_IN_CONSTANTS,
			 PretupsI.RESPONSE_FAIL,null); 
		}else if(base64Bytes.length > Long.parseLong(fileSize) ){
			 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.FILE_SIZE_LARGE,
					 PretupsI.RESPONSE_FAIL,null); 
		}
	}
	
	public void validateFileName(String fileName) throws BTSLBaseException {
		final String pattern = Constants.getProperty("FILE_NAME_WHITE_LIST");
		final Pattern r = Pattern.compile(pattern);
		final Matcher m = r.matcher(fileName);
		if (!m.find()) {
			throw new BTSLBaseException(this, "validateFileName", PretupsErrorCodesI.INVALID_FILE_NAME1,
					 PretupsI.RESPONSE_FAIL,null); 
		}
	}
	
	public void writeByteArrayToFile(String filePath, byte[] base64Bytes) throws BTSLBaseException {
		try {
			log.debug("writeByteArrayToFile: ", filePath);
			log.debug("writeByteArrayToFile: ", base64Bytes);
			if (new File(filePath).exists()) {
				throw new BTSLBaseException("OAuthenticationUtil", "writeByteArrayToFile",
						PretupsErrorCodesI.BATCH_UPLOAD_FILE_EXISTS, PretupsI.RESPONSE_FAIL, null);
			}
			FileUtils.writeByteArrayToFile(new File(filePath), base64Bytes);
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			log.debug("writeByteArrayToFile: ", e.getMessage());
			log.error("writeByteArrayToFile", "Exceptin:e=" + e);
			log.errorTrace("writeByteArrayToFile", e);

		}
	}
	
	public void validateFileType(String fileType) throws BTSLBaseException {
		//getting fileType Preference for validation
				String allowedContentType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_UPLOAD_FILE_FORMATS);
				String[] allowedContentTypes = new String[] { "xls", "xlsx" };

				List<String> allowedFileTypelist = Arrays.asList(allowedContentTypes);
				
		if (!allowedFileTypelist.contains(fileType)) {
			throw new BTSLBaseException(this, "validateFileType", PretupsErrorCodesI.INVALID_FILE_FORMAT, PretupsI.RESPONSE_FAIL, null);
		}

	}
	
	private void validateFileDetailsMap(HashMap<String,String> fileDetailsMap) throws BTSLBaseException{

		if(!BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_TYPE1)) && !BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_NAME)) 
				&& !BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_ATTACHMENT))) 
		{
			 validateFileName(fileDetailsMap.get(PretupsI.FILE_NAME)); // throw exception
			 validateFileType(fileDetailsMap.get(PretupsI.FILE_TYPE1));
		} 
		else 
		{
			if (BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.SERVICE_KEYWORD))) //This condition won't occur in actual deployment
			{
				log.error("validateFileInput", "SERVICEKEYWORD IS NULL");
				 throw new BTSLBaseException(this, "validateFileInput", PretupsErrorCodesI.SERVICE_KEYWORD_REQUIRED,
				 PretupsI.RESPONSE_FAIL,null);
			}
			else {
				log.error("validateFileInput", "FILETYPE/FILENAME/FILEATTACHMENT IS NULL");
				 throw new BTSLBaseException(this, "validateFileInput", PretupsErrorCodesI.INVALID_FILE_INPUT,
				 PretupsI.RESPONSE_FAIL,null); 
			}
			 
		}

	}

	@Override
	public List<ChannelUserVO> fetchChannelUsersByStatusForSRAndDelReq(Connection con,ChannelUserSearchReqVo requestVo) throws BTSLBaseException {
		final  String methodName="fetchChannelUsersByStatusForSRAndDelReq";
	   userDAO=new UserDAO();
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		String validUserStatus="";
		channelUserDao=new ChannelUserDAO();
		List<ChannelUserVO> userList=null;
		ChannelUserVO uservo=null;
		
		
		if(BTSLUtil.isNullorEmpty(requestVo.getUserStatus())){
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Search UserStatus"}, null);
		}
		else {
			if(requestVo.getUserStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
				validUserStatus=PretupsI.USER_STATUS_SUSPEND_REQUEST;
			}
			else if(requestVo.getUserStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
				validUserStatus=PretupsI.USER_STATUS_SUSPEND;
			}
			else if(requestVo.getUserStatus().equalsIgnoreCase(PretupsI.USER_STATUS_DELETE_REQUEST)) {
				validUserStatus=PretupsI.USER_STATUS_DELETE_REQUEST;
			}
			else if(requestVo.getUserStatus().equalsIgnoreCase(PretupsI.USER_STATUS_ACTIVE)) {
				validUserStatus=PretupsI.USER_STATUS_ACTIVE;
			}
			else if(requestVo.getUserStatus().equalsIgnoreCase(PretupsI.USER_STATUS_DELETED)) {
				validUserStatus=PretupsI.USER_STATUS_DELETED;
			}
			else if(requestVo.getUserStatus().equalsIgnoreCase(PretupsI.USER_STATUS_APPROVED)) {
				validUserStatus=PretupsI.USER_STATUS_APPROVED;
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
				// check user existed with msisddn
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
			else {
				throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.PROPERTY_INVALID, PretupsI.RESPONSE_FAIL, new String[] {"Search Type"}, null);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exit");
		}
		return userList;
	}

	@Override
	public boolean approvalOrRejectSuspendUser(Connection con, ActionOnUserReqVo actionReqVo, OAuthUserData oauthUserData)
			throws SQLException, BTSLBaseException {
		final  String methodName="approvalOrRejectSuspendUser";
		boolean changeUserStatus=false;
		userDAO = new UserDAO();
		userwebDAO=new UserWebDAO();
		UserVO newUserVO =null;
		UserEventRemarksVO userRemarksVO = null;
		 BTSLMessages btslMessage = null;
	    boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        boolean isEmailServiceAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
 		
        final UserVO userSessionVO = userDAO.loadUsersDetails(con,oauthUserData.getMsisdn());
		UserVO userVO=userDAO.loadUserDetailsByLoginId(con,actionReqVo.getLoginId());
		if(BTSLUtil.isNullorEmpty(userVO)) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOGIN_ID_DOES_NOT_EXISTS , PretupsI.RESPONSE_FAIL, new String[] {}, null);
		}
		else if(userVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)){
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CCE_ERROR_USER_ALREADY_SUSPENDED, PretupsI.RESPONSE_FAIL, new String[] {}, null);
		}
		else if(!userVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_ALLOWED, PretupsI.RESPONSE_FAIL, new String[] {}, null);
		}
        ArrayList<UserEventRemarksVO> deleteSuspendRemarkList = null;
        //to checks == null empty
         newUserVO = new UserVO();
         final ArrayList newUserList = new ArrayList();
         final Date currentDate = new Date();
            if(!actionReqVo.getRequestType().equalsIgnoreCase("SUSPENDAPPROVAL")) {
            	throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.PROPERTY_INVALID, PretupsI.RESPONSE_FAIL, new String[] {"RequestType"}, null);
            }
         
	        if (PretupsI.USER_APPROVE.equals(actionReqVo.getAction()) ){
	            newUserVO.setStatus(PretupsI.USER_STATUS_SUSPEND);
	            newUserVO.setPreviousStatus(userVO.getStatus());
	        } else if (PretupsI.USER_REJECTED.equals(actionReqVo.getAction())){
	            newUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
	            newUserVO.setPreviousStatus(userVO.getStatus());
	        }
	
	        if (!PretupsI.USER_DISCARD.equals(actionReqVo.getAction())) {
	            newUserVO.setUserID(userVO.getUserID());
	            newUserVO.setLastModified(userVO.getLastModified());
	            newUserVO.setModifiedBy(userSessionVO.getUserID());
	            newUserVO.setModifiedOn(currentDate);
	            newUserVO.setUserName(userVO.getUserName());
	            newUserVO.setLoginID(userVO.getLoginID());
	            newUserVO.setMsisdn(userVO.getMsisdn());
	            newUserList.add(newUserVO);
	        }
	        int suspendCount = 0;
            if (newUserList.size() > 0) {
                suspendCount = userDAO.deleteSuspendUser(con, newUserList);
                if (suspendCount <= 0) {
                    con.rollback();
                    log.error(methodName, "Error: while Suspending User");
                    throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, PretupsI.RESPONSE_FAIL, new String[] {}, null);
                }
                if (suspendCount > 0) {
                    int suspendRemarkCount = 0;
                    deleteSuspendRemarkList = new ArrayList<UserEventRemarksVO>();
                    userRemarksVO = new UserEventRemarksVO();
                    userRemarksVO.setCreatedBy(userSessionVO.getUserID());
                    userRemarksVO.setCreatedOn(currentDate);
                    userRemarksVO.setEventType(PretupsI.SUSPEND_EVENT_APPROVAL);
                    userRemarksVO.setMsisdn(userVO.getMsisdn());
                    userRemarksVO.setRemarks(actionReqVo.getRemarks());
                    userRemarksVO.setUserID(userVO.getUserID());
                    userRemarksVO.setUserType(userVO.getUserType());
                    userRemarksVO.setModule(PretupsI.C2S_MODULE);
                    deleteSuspendRemarkList.add(userRemarksVO);
                    suspendRemarkCount = userwebDAO.insertEventRemark(con, deleteSuspendRemarkList);
                    if (suspendRemarkCount <= 0) {
                        con.rollback();
                        changeUserStatus=false;
                        log.error(methodName, "Error: while inserting into userEventRemarks Table");
                      //  throw new BTSLBaseException(this, "save", "error.general.processing");
                        throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, PretupsI.RESPONSE_FAIL, new String[] {}, null);
                    }
                }
                con.commit();
                changeUserStatus=true;
                int indexSuspend=0;
                //send msg
                for (indexSuspend=0;indexSuspend<newUserList.size();indexSuspend++){
                    try {
						userVO = (UserVO) newUserList.get(indexSuspend);
						BTSLMessages sendBtslMessage=null;
						
						if(userVO.getStatus().equals(PretupsI.USER_STATUS_SUSPEND)) {
							  String arr[] = {userVO.getUserName(),"suspended"};
						    sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.MSG_SUCCESSFUL_OF_OPERACTION,arr);
   
						}
                        else {
                        	 String arr[] = {userVO.getUserName(),"Activated from Suspend Request"};
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
		                    PushMessage pushMessage=new PushMessage(channelUser.getMsisdn(),sendBtslMessage,"","", locale,userSessionVO.getNetworkID(), null, PretupsI.SERVICE_TYPE_EXT_USER_SUSPEND_RESUME);
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
	
	
	@Override
    public boolean approvalOrRejectDeleteUser(Connection con,ActionOnUserReqVo actionReqVo,OAuthUserData oauthUserData)
    		throws SQLException, BTSLBaseException, Exception{
		final  String methodName="approvalOrRejectDeleteUser";
		boolean changeUserStatus=false;
		userDAO = new UserDAO();
		userwebDAO=new UserWebDAO();
		UserVO newUserVO =null;
		UserEventRemarksVO userRemarksVO = null;
		 BTSLMessages btslMessage = null;
	    boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        boolean isEmailServiceAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
 		
        final UserVO userSessionVO = userDAO.loadUsersDetails(con,oauthUserData.getMsisdn());
		UserVO userVO=userDAO.loadUserDetailsByLoginId(con,actionReqVo.getLoginId());	
		
		if(BTSLUtil.isNullorEmpty(userVO)) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOGIN_ID_DOES_NOT_EXISTS , PretupsI.RESPONSE_FAIL, new String[] {}, null);
		}
		else if(userVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_DELETED)){
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CCE_ERROR_USER_ALREADY_SUSPENDED, PretupsI.RESPONSE_FAIL, new String[] {}, null);
		}
		else if(!userVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_DELETE_REQUEST)) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_ALLOWED, PretupsI.RESPONSE_FAIL, new String[] {}, null);
		}
		
		ArrayList<UserEventRemarksVO> deleteSuspendRemarkList = null;
		
		
		
		
		/*
         * Before deleting three checks will be perform
         * a)Check whether the child user is active or not
         * b)Check the balance of the deleted user
         * c)Check for no O2C Transfer pending (closed and canceled
         * Txn)
         */
		 boolean isBalanceFlag = false;
         boolean isO2CPendingFlag = false;
         boolean isChildFlag = false;
         boolean isSOSPendingFlag = false;
         boolean isLRPendingFlag = false;
         
         isChildFlag = userDAO.isChildUserActive(con, userVO.getUserID());

         if (isChildFlag) {
             //throw new BTSLBaseException(this, methodName, "user.viewdsapprovalusersview.error.childuserexist", 0, new String[] { userVO.getUserName() },
              //               "UserDeletionApprovalListView");
        	 //throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_ALLOWED, PretupsI.RESPONSE_FAIL, new String[] { userVO.getUserName() }, null);
        	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHILD_USER_EXIST, PretupsI.RESPONSE_FAIL, new String[] { userVO.getUserName() }, null);
         }else {
           	if(channelSosEnable)
				{
			        // Checking SOS Pending transactions
			        ChannelSOSSettlementHandler channelSOSSettlementHandler = new ChannelSOSSettlementHandler();
			        isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(con, userVO.getUserID());
				}
       }
     if(isSOSPendingFlag){
//         throw new BTSLBaseException(this, methodName, "user.viewdsapprovalusersview.error.userSOSpendingTxnExist", 0, new String[] { userVO
//                 .getUserName() }, "UserDeletionApprovalListView");
    	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SOS_TRANSACTION_PENDING, PretupsI.RESPONSE_FAIL, new String[] { userVO.getUserName() }, null);
     }else {
     	// Checking for pending LR transactions
 		if(lrEnabled){
 			UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
 			UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
 			userTrfCntVO = userTrfCntDAO.selectLastLRTxnID(userVO.getUserID(), con, false, null);
 			if (userTrfCntVO!=null) 
 				isLRPendingFlag = true;
 		}
     }
     if(isLRPendingFlag){
//     	throw new BTSLBaseException(this, methodName, "user.viewdsapprovalusersview.error.user.LR.pendingTxnExist", 0, new String[] { userVO
//                 .getUserName() }, "UserDeletionApprovalListView"); 
    	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LR_TRANSACTION_PENDING, PretupsI.RESPONSE_FAIL, new String[] { userVO.getUserName() }, null);
     }else{
         // Checking O2C Pending transactions
         final ChannelTransferDAO transferDAO = new ChannelTransferDAO();
         isO2CPendingFlag = transferDAO.isPendingTransactionExist(con, userVO.getUserID());
     }
     
     
     
     boolean isRestrictedMsisdnFlag = false;
     boolean isbatchFocPendingTxn = false;
     boolean isBatchC2CTxnPending=false;
     boolean isBatchO2CTxnPending=false;
     if (isO2CPendingFlag) {
//         throw new BTSLBaseException(this, methodName, "user.viewdsapprovalusersview.error.userbatchfocpendingtxnexist", 0, new String[] { userVO
//                         .getUserName() }, "UserDeletionApprovalListView");
    	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.O2C_TRANSACTION_PENDING, PretupsI.RESPONSE_FAIL, new String[] { userVO.getUserName() }, null);
    	 
     } else {
         // Checking Batch Foc Pending transactions
         final FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
         isbatchFocPendingTxn = batchTransferDAO.isPendingTransactionExist(con, userVO.getUserID());
     }
     if (isbatchFocPendingTxn) {
//         throw new BTSLBaseException(this, methodName, "user.deletesuspendchanneluser.error.userbatchfocpendingtxnexist", 0, new String[] { userVO
//                         .getUserName() }, "UserDeletionApprovalListView");
    	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.FOC_TRANSACTION_PENDING, PretupsI.RESPONSE_FAIL, new String[] { userVO.getUserName() }, null);
     } else {
         if (PretupsI.STATUS_ACTIVE.equals(userVO.getCategoryVO().getRestrictedMsisdns())) {
             final RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
             isRestrictedMsisdnFlag = restrictedSubscriberDAO.isSubscriberExistByChannelUser(con, userVO.getUserID());
         }
     }
     if (isRestrictedMsisdnFlag) {
//         throw new BTSLBaseException(this, methodName, "user.viewdsapprovalusersview.error.userrestrictedexist", 0, new String[] { userVO.getUserName() },
//                         "UserDeletionApprovalListView");
    	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.RESTRICTED_MSISDN, PretupsI.RESPONSE_FAIL, new String[] { userVO.getUserName() }, null);
     }else {
         // Checking Batch C2C Pending transactions
         final C2CBatchTransferDAO c2cBatchTransferDAO = new C2CBatchTransferDAO();
         isBatchC2CTxnPending = c2cBatchTransferDAO.isPendingC2CTransactionExist(con, userVO.getUserID());
     }
     if (isBatchC2CTxnPending) {
//         throw new BTSLBaseException(this, methodName, "user.deletesuspendchanneluser.error.userbatchc2cpendingtxnexist", 0, new String[] { userVO
//                         .getUserName() }, "UserDeletionApprovalListView");
    	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2C_TRANSACTION_PENDING, PretupsI.RESPONSE_FAIL, new String[] { userVO.getUserName() }, null);
     } 
     else {
         // Checking Batch O2C Pending transactions
         final BatchO2CTransferWebDAO o2cBatchTransferDAO = new BatchO2CTransferWebDAO();
         isBatchO2CTxnPending = o2cBatchTransferDAO.isPendingO2CTransactionExist(con, userVO.getUserID());
     }
     if (isBatchO2CTxnPending) {
//         throw new BTSLBaseException(this, methodName, "user.deletesuspendchanneluser.error.userbatcho2cpendingtxnexist", 0, new String[] { userVO
//                         .getUserName() }, "UserDeletionApprovalListView");
    	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BATCH_O2C_TRANSACTION_PENDING, PretupsI.RESPONSE_FAIL, new String[] { userVO.getUserName() }, null);
     } 
		
		
		
		newUserVO = new UserVO();
        final ArrayList newUserList = new ArrayList();
        final Date currentDate = new Date();
           if(!actionReqVo.getRequestType().equalsIgnoreCase("DELETEAPPROVAL")) {
           	throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.PROPERTY_INVALID, PretupsI.RESPONSE_FAIL, new String[] {"RequestType"}, null);
           }
		
           
           if (PretupsI.USER_APPROVE.equals(actionReqVo.getAction())){
	            newUserVO.setStatus(PretupsI.USER_STATUS_DELETED);
				newUserVO.setPreviousStatus(userVO.getStatus());
				
				
				
				isBalanceFlag = userDAO.isUserBalanceExist(con, userVO.getUserID());
                if (isBalanceFlag) {
                    // to implement
                	
                	ChannelUserDAO channelUserDAO=new ChannelUserDAO();
                    final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
                    ArrayList<UserBalancesVO> userBal = null;
                    UserBalancesVO userBalancesVO = null;
                    
                    final ChannelUserVO fromChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, userVO.getUserID(), false, currentDate,false);
                    
                    fromChannelUserVO.setGateway(PretupsI.REQUEST_SOURCE_TYPE_WEB);
                    final ChannelUserVO toChannelUserVO = channelUserDAO
                                    .loadChannelUserDetailsForTransfer(con, fromChannelUserVO.getOwnerID(), false, currentDate,false);
                    userBal = userBalancesDAO.loadUserBalanceForDelete(con, fromChannelUserVO.getUserID());// user
                    // to
                    // be
                    // deleted
                    Iterator<UserBalancesVO> itr = userBal.iterator();
                    itr = userBal.iterator();
                    boolean sendMsgToOwner = false;
                    long totBalance = 0;
                    while (itr.hasNext()) {
                        userBalancesVO = itr.next();
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RETURN_TO_OPERATOR_STOCK)).booleanValue() || fromChannelUserVO
                                        .getOwnerID().equals(userVO.getUserID())) {
                            UserDeletionBL.updateBalNChnlTransfersNItemsO2C(con, fromChannelUserVO, toChannelUserVO, PretupsI.REQUEST_SOURCE_TYPE_WEB,
                                            PretupsI.REQUEST_SOURCE_TYPE_WEB, userBalancesVO);
                        } else {

                        	if(!PretupsI.USER_STATUS_SUSPEND.equalsIgnoreCase(toChannelUserVO.getStatus()))
                        	{
                            UserDeletionBL.updateBalNChnlTransfersNItemsC2C(con, fromChannelUserVO, toChannelUserVO, userSessionVO.getUserID(),
                                            PretupsI.REQUEST_SOURCE_TYPE_WEB, userBalancesVO);
                            sendMsgToOwner = true; 
                            totBalance += userBalancesVO.getBalance();
                        	}
                        	else
                        		throw new BTSLBaseException(this, "save", "user.channeluser.deletion.parentsuspended");
                        	
                        }
                    }
                  //ASHU
                    if(sendMsgToOwner) {
                    	
                    	ChannelUserVO prntChnlUserVO = new ChannelUserDAO().loadChannelUserByUserID(con, fromChannelUserVO.getOwnerID());
                    	 //Added for sending the notification language as per user assigned
	                    UserPhoneVO userPhoneVO = prntChnlUserVO.getUserPhoneVO();
	                    Locale locale =null;
	                    if(userPhoneVO!=null){
	                    	locale = new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry());
	                    } else {
	                    	locale = new Locale(defaultLanguage,defaultCountry);
	                    }
                        String msgArr [] = {fromChannelUserVO.getMsisdn(),PretupsBL.getDisplayAmount(totBalance)};
                        final BTSLMessages sendBtslMessageToOwner = new BTSLMessages(PretupsErrorCodesI.OWNER_USR_BALCREDIT,msgArr);
                        final PushMessage pushMessageToOwner = new PushMessage(prntChnlUserVO.getMsisdn(), sendBtslMessageToOwner, "", "", locale, fromChannelUserVO.getNetworkID(), null, PretupsI.SERVICE_TYPE_USR_SUSPEND_RESUME);
                        pushMessageToOwner.push();    
                    	
                    } 
                }
               
				
                btslMessage = null;
                btslMessage = new BTSLMessages("user.viewdsapprovalusersview.deletesuccessmessage", "DeleteApprovalSuccess");

	        } else if (PretupsI.USER_REJECTED.equals(actionReqVo.getAction())){
	            newUserVO.setStatus(userVO.getPreviousStatus());
	            newUserVO.setPreviousStatus(userVO.getStatus());
	        }
	
	        if (!PretupsI.USER_DISCARD.equals(actionReqVo.getAction())) {
	            newUserVO.setUserID(userVO.getUserID());
	            newUserVO.setLastModified(userVO.getLastModified());
	            newUserVO.setModifiedBy(userSessionVO.getUserID());
	            newUserVO.setModifiedOn(currentDate);
	            newUserVO.setUserName(userVO.getUserName());
	            newUserVO.setLoginID(userVO.getLoginID());
	            newUserVO.setMsisdn(userVO.getMsisdn());
	            newUserList.add(newUserVO);
	        }
	        int deleteCount = 0;
           if (newUserList.size() > 0) {
               deleteCount = userDAO.deleteSuspendUser(con, newUserList);
          
               if (deleteCount <= 0) {
                   con.rollback();
                   log.error(methodName, "Error: while Deleting User");
                   throw new BTSLBaseException(this, methodName, "error.general.processing");
               }
               
               if (deleteCount > 0) {
                   int suspendRemarkCount = 0;
                   deleteSuspendRemarkList = new ArrayList<UserEventRemarksVO>();
                   userRemarksVO = new UserEventRemarksVO();
                   userRemarksVO.setCreatedBy(userSessionVO.getCreatedBy());
                   userRemarksVO.setCreatedOn(currentDate);
         
                   userRemarksVO.setEventType(PretupsI.DELETE_EVENT_APPROVAL);
                   userRemarksVO.setMsisdn(userVO.getMsisdn());
                   userRemarksVO.setRemarks(actionReqVo.getRemarks());
                   userRemarksVO.setUserID(userVO.getUserID());
                   userRemarksVO.setUserType(userVO.getUserType());
                   userRemarksVO.setModule(PretupsI.C2S_MODULE);
                   deleteSuspendRemarkList.add(userRemarksVO);
                   suspendRemarkCount = userwebDAO.insertEventRemark(con, deleteSuspendRemarkList);
                   if (suspendRemarkCount <= 0) {
                       con.rollback();
                       log.error(methodName, "Error: while inserting into userEventRemarks Table");
                       throw new BTSLBaseException(this, "save", "error.general.processing");
                   }

               }
               
               con.commit();
               changeUserStatus=true;
               
               
               //sending message and notification starts
               
               
               
               int indexSuspend=0;
               //send msg
               for (indexSuspend=0;indexSuspend<newUserList.size();indexSuspend++){
                   try {
						userVO = (UserVO) newUserList.get(indexSuspend);
						BTSLMessages sendBtslMessage=null;
						
						if(userVO.getStatus().equals(PretupsI.USER_STATUS_DELETED)) {

							//BTSLMessages sendBtslMessage = null;
							if(userVO.getUserBalanceList()!=null && userVO.getUserBalanceList().size()>=1) {
								String args[]= new String[] {(String) userVO.getUserBalanceList().get(0)};
								sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.USER_DELETE_SUCCESS, args);
							} else {
								sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.USER_DELETED_SUCCESS);
							}
							//Added for sending the notification language as per user assigned
		                    UserPhoneVO userPhoneVO = userDAO.loadUserPhoneVO(con,userVO.getUserID());
		                    Locale locale =null;
		                    if(userPhoneVO!=null){
		                    	locale = new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry());
		                    } else {
		                    	locale = new Locale(defaultLanguage,defaultCountry);
		                    }
							PushMessage pushMessage=new PushMessage(userVO.getMsisdn(),sendBtslMessage,"","", locale,userSessionVO.getNetworkID(), null, PretupsI.SERVICE_TYPE_EXT_USER_DELETE);
							pushMessage.push();
							
						}
						/*
                       else {
                       	 String arr[] = {userVO.getUserName(),"Activated from Suspend Request"};
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
		                    PushMessage pushMessage=new PushMessage(channelUser.getMsisdn(),sendBtslMessage,"","", locale,userSessionVO.getNetworkID(), null, PretupsI.SERVICE_TYPE_EXT_USER_SUSPEND_RESUME);
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
						}*/
						
					} catch (RuntimeException e) {
						log.errorTrace(methodName, e);
					}
                   btslMessage = null;
               }
               
               
               if (deleteCount > 0) {
                   for (int i = 0, j = newUserList.size(); i < j; i++) {
                       userVO = (UserVO) newUserList.get(i);
                       if (userVO.getStatus().equals(PretupsI.USER_STATUS_DELETED)) {
                           ChannelUserLog.log("APPDELCHNLUSR", userVO, userSessionVO, false, null);
                           final BTSLMessages sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_DEREGISTER);
                           //Added for sending the notification language as per user assigned
		                    UserPhoneVO userPhoneVO = userDAO.loadUserPhoneVO(con,userVO.getUserID());
		                    Locale locale =null;
		                    if(userPhoneVO!=null){
		                    	locale = new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry());
		                    } else {
		                    	locale = new Locale(defaultLanguage,defaultCountry);
		                    }
                           final PushMessage pushMessage = new PushMessage(userVO.getMsisdn(), sendBtslMessage, "", "", locale, userSessionVO.getNetworkID(), null, PretupsI.SERVICE_TYPE_EXT_USER_ADD);
                           try {
								pushMessage.push();
							} catch (Exception e) {
								log.errorTrace(methodName, e);
							}
                           // Email for pin & password
                           if (isEmailServiceAllow && !BTSLUtil.isNullString(userVO.getEmail())) {
//                               final String subject = this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "subject.eachuser.email.delete.message");
//                               final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, sendBtslMessage, BTSLUtil.getBTSLLocale(request), userVO
//                                               .getNetworkID(), "Email has ben delivered recently", userVO, userSessionVO);
//                               emailSendToUser.sendMail();
                        	   final EmailSendToUser emailSendToUser = new EmailSendToUser("", sendBtslMessage, locale, userSessionVO.getNetworkID(),
                                       "Email has ben delivered recently", userVO, userSessionVO);
                        	   emailSendToUser.sendMail();
                           }
                       } else {
                           ChannelUserLog.log("REJDELCHNLUSR", userVO, userSessionVO, false, null);
                       }
                   }
               }
               
               //sending message and notification ends
               
           }
		return changeUserStatus;
    }

	@Override
	public AreaSearchResponseVO searchRegion(String loginId, Connection con, String geoDomainCode, HttpServletResponse responseSwag) {
		final String METHOD_NAME = "searchRegion";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}

		AreaSearchResponseVO areaSearchResponseVO = new AreaSearchResponseVO();
		UserDAO userDao = new UserDAO();
		ArrayList geographyList = new ArrayList();
		ArrayList finalList = new ArrayList();

		try {
			UserVO userSessionVO = userDao.loadAllUserDetailsByLoginID(con, loginId);
			if(userSessionVO != null) {
				geographyList = geographicalDomainWebDAO.loadGeographyList(con, userSessionVO.getNetworkID(), geoDomainCode, "%");

				if (geographyList != null) {
					for (Object obj : geographyList) {
						UserGeographiesVO geoVO = (UserGeographiesVO) obj;
						AreaData areaData = new AreaData();
						areaData.setGeoCode(geoVO.getGraphDomainCode());
						areaData.setGeoName(geoVO.getGraphDomainName());
						areaData.setGeoDomainName(geoVO.getGraphDomainTypeName());
						areaData.setGeoDomainSequenceNo(Integer.toString(geoVO.getGraphDomainSequenceNumber()));
						UserGeographiesVO tempGeoVO = geographicalDomainWebDAO.getGeographyDomainData(con, geoVO.getGraphDomainCode());
						areaData.setIsDefault(tempGeoVO.getIsDefault());
						finalList.add(areaData);
					}

					areaSearchResponseVO.setAreaList(finalList);
					areaSearchResponseVO.setHierarchyLength(1);
					areaSearchResponseVO.setMessage(PretupsI.SUCCESS);
					areaSearchResponseVO.setStatus(HttpStatus.SC_OK);
					responseSwag.setStatus(HttpStatus.SC_OK);
				}

				else {
					throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.USER_NOT_FOUND);
				}
			}
			else{
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.USER_NOT_FOUND);
			}


		} catch (Exception e){
			log.errorTrace(METHOD_NAME, e);
		}
		return areaSearchResponseVO;

	}

	class UpdateRecordsInDB implements Runnable {
	
	public final Log log = LogFactory.getLog(UpdateRecordsInDB.class.getName());
	private Connection innerCon=null;
	private MComConnectionI innermcomCon = null;
	private ArrayList<ChannelUserVO> channelUserList = null;
	private ArrayList fileErrorList = null;
	private String domCode = null;
	private Locale locale = null;
	private BatchUserWebDAO batchUserWebDAO = null;
	private String fileName = null;
	private UserVO userVO = null;
	private String batchID = null;
	private boolean insertintoBatches = false;
	private boolean insbatch;
    private int total =0;    	
	public UpdateRecordsInDB(ArrayList<ChannelUserVO> list,ArrayList fileErrorList, BatchUserWebDAO batchUserWebDAO, String domCode, Locale locale, UserVO userVO, String fileName,String batchID,boolean insertintobatches,int totalSize,boolean inbatch) {
		this.channelUserList = list;
		this.fileErrorList = fileErrorList;
		this.domCode = domCode;
		this.locale = locale;
		this.batchUserWebDAO = batchUserWebDAO;
		this.fileName = fileName;   		
		this.userVO = userVO;
		this.batchID = batchID;
		this.insertintoBatches = insertintobatches;
		this.total = totalSize;
		this.insbatch = inbatch;
	}

	public void run() {
		final String METHOD_NAME = "run";
		try {
			double startTime = System.currentTimeMillis();
			if(innermcomCon==null){
				innermcomCon = new MComConnection();
				innerCon = innermcomCon.getConnection();
			}
			log.debug("UpdateRecordsInDB","Hey Harshad thread "+total+insertintoBatches);
			ArrayList dbErrorList = batchUserWebDAO.addChannelUserList(innerCon, channelUserList, domCode, locale,userVO,fileName,batchID,insertintoBatches,total,insbatch);
			innerCon.commit();
			synchronized (this) {
				fileErrorList.addAll(dbErrorList);
			}
			log.debug("UpdateRecordsInDB","Hey ASHU thread "+Thread.currentThread().getName()+" processed "+channelUserList.size()+" records, time taken = "+(System.currentTimeMillis()-startTime)+" ms");
			this.channelUserList.clear();
			this.channelUserList = null;
        	Runtime runtime = Runtime.getRuntime();
            long memory = runtime.totalMemory() - runtime.freeMemory();
            log.debug("run","Used memory in megabytes before gc: " + (memory)/1048576);
            // Run the garbage collector
            runtime.gc();
            // Calculate the used memory
            memory = runtime.totalMemory() - runtime.freeMemory();
            log.debug("run","Used memory in megabytes after gc: " + (memory)/1048576);
		} catch (BTSLBaseException e1) {
			log.errorTrace(METHOD_NAME,e1);
		} catch (SQLException e) {
			log.errorTrace(METHOD_NAME,e);
		} finally {
			if (innermcomCon != null) {
				innermcomCon.close("BatchUserInitiateAction#UpdateRecordsInDB");
				innermcomCon = null;
			}
		}
	}
	
}

public ArrayList<CategoryVO> getParentCategoryList(UserVO sessionUserVO, String categoryCode , Connection con) throws Exception {

	final String methodName = "getParentCategoryList";
	if (log.isDebugEnabled()) {
		log.debug(methodName, PretupsI.ENTERED);
	}

	UserDAO userDao = null;
	ArrayList<CategoryVO> list = null;
	final C2STransferDAO c2STransferDAO = new C2STransferDAO();
	final CategoryDAO categoryDAO = new CategoryDAO();

	userDao = new UserDAO();
	list = new ArrayList<CategoryVO>();

	ArrayList<CategoryVO> originalCategories = categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT);
	List<CategoryVO> category = originalCategories.stream().filter(cat -> cat.getCategoryCode().equals(categoryCode))
			.collect(Collectors.toList());

	if (category.size() == 0) {
		/*
		 * String message = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.
		 * PARENT_CATEGORY_INVALID, new String[] {""}); response.setMessage(message);
		 * response.setStatus(HttpStatus.SC_BAD_REQUEST);
		 * responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		 */
		return null;
	}

	/*
	 * Here we load all transfer rules(from_category and to category), parent
	 * category list will populate on the basis of category drop down value by
	 * calling a method of the same class populateParentCategoryList
	 */
	ArrayList<ChannelTransferRuleVO> originalParentCategoryList = c2STransferDAO
			.loadC2SRulesListForChannelUserAssociation(con, sessionUserVO.getNetworkID());

	/*
	 * OrigParentCategory List contains all(Associated C2S Transfer Rules category)
	 * FromCategory and ToCategory information like
	 * 
	 * Dist -> Ret(Disttributor can transfer to retailer and parentAssociationFlag =
	 * Y) The above rule state while adding Retailer parent category can be
	 * Distributor
	 */
	if (originalParentCategoryList != null && !BTSLUtil.isNullString(categoryCode)) {
		CategoryVO categoryVO = null;
		ChannelTransferRuleVO channelTransferRuleVO = null;
		for (int i = 0, j = originalCategories.size(); i < j; i++) {
			categoryVO = (CategoryVO) originalCategories.get(i);
			/*
			 * If Sequence No == 1 means root owner is adding(suppose Distributor) at this
			 * time pagentCategory and category both will be same, just add the categoryVO
			 * into the parentCategoryList
			 */
			if (1 == category.get(0).getSequenceNumber()
					&& category.get(0).getCategoryCode().equals(categoryVO.getCategoryCode())) {
				list = new ArrayList();
				list.add(categoryVO);
				break;
			}
			/*
			 * In Case of channel admin No need to check the sequence number In Case of
			 * channel user we need to check the sequence number
			 */
			if (PretupsI.OPERATOR_TYPE_OPT.equals(sessionUserVO.getDomainID())) {
				for (int m = 0, n = originalParentCategoryList.size(); m < n; m++) {
					channelTransferRuleVO = (ChannelTransferRuleVO) originalParentCategoryList.get(m);
					/*
					 * Here three checks are checking Add those category into the list where
					 * a)FormCategory(origPatentList) = categoryCode(origcategoryList)
					 * b)selectedCategory(categoryID[0] = ToCategory(origParentCategoryList)
					 * c)selectedCategory(categoryID[0] != FromCategory(origParentCategoryList)
					 */
					if (categoryVO.getCategoryCode().equals(channelTransferRuleVO.getFromCategory())
							&& category.get(0).getCategoryCode().equals(channelTransferRuleVO.getToCategory())
							&& !category.get(0).getCategoryCode().equals(channelTransferRuleVO.getFromCategory())) {
						list.add(categoryVO);
					}
				}
			} else {
				for (int m = 0, n = originalParentCategoryList.size(); m < n; m++) {
					channelTransferRuleVO = (ChannelTransferRuleVO) originalParentCategoryList.get(m);
					/*
					 * Here three checks are checking Add those category into the list where
					 * a)FormCategory(origPatentList) = categoryCode(origcategoryList)
					 * b)selectedCategory(categoryID[0] = ToCategory(origParentCategoryList)
					 * c)selectedCategory(categoryID[0] != FromCategory(origParentCategoryList)
					 */

					if (categoryVO.getCategoryCode().equals(channelTransferRuleVO.getFromCategory())
							&& category.get(0).getCategoryCode().equals(channelTransferRuleVO.getToCategory())
							&& !category.get(0).getCategoryCode().equals(channelTransferRuleVO.getFromCategory()))
						if (categoryVO.getSequenceNumber() >= sessionUserVO.getCategoryVO().getSequenceNumber()) {
							list.add(categoryVO);
						}
				}
			}
		}
	}

	if (list.isEmpty()) {
		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TRANSFER_RULE_NOT_DEFINED);
	}

	if (log.isDebugEnabled()) {
		log.debug(methodName, PretupsI.EXITED);
	}
	
	if (log.isDebugEnabled()) {
		log.debug(methodName, PretupsI.EXITED);
	}
	
	return list;
}


}
	
	

