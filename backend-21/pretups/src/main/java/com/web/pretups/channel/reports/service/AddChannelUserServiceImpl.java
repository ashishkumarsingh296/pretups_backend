package com.web.pretups.channel.reports.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EmailSendToUser;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.security.csrf.CSRFTokenUtil;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

/**
 * @author tarun.kumar
 *
 */
@Service("addChannelUserService")
public class AddChannelUserServiceImpl implements AddChannelUserService{    

	public static final Log _log = LogFactory.getLog(AddChannelUserServiceImpl.class.getName());  
	private static final String FAIL_KEY = "fail";	
	
	
	@Override  
	public void loadDomainList(HttpServletRequest request,HttpServletResponse response, UsersReportModel usersReportModel,UserVO userVO, Model model) {
		
		final String methodName = "loadDomainList";  
        enteredLoadDomainList(methodName);
       
        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        @SuppressWarnings("rawtypes")
		List<?> channelUserTypeList = null;
        try {
            channelUserWebDAO = new ChannelUserWebDAO();          
            usersReportModel.setRequestType("add");                          
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();			
            channelUserTypeList = channelUserWebDAO.loadChannelUserTypeList(con);
            usersReportModel.setChannelUserTypeList(channelUserTypeList);
            final CategoryDAO categoryDAO = new CategoryDAO();
            final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
            usersReportModel.setNetworkCode(userVO.getNetworkID());
           
            final ArrayList<ListValueVO> loggedInUserDomainList = new ArrayList<>();
            ListValueVO listValueVO = null;

            if (PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())) {
                usersReportModel.setSelectDomainList(BTSLUtil.displayDomainList(userVO.getDomainList()));               
                if (usersReportModel.getSelectDomainList() != null && usersReportModel.getSelectDomainList().size() >1) {
                	usersReportModel.setSelectDomainListSize(usersReportModel.getSelectDomainList().size());
                    usersReportModel.setDomainShowFlag(true);
                } else if(usersReportModel.getSelectDomainList() != null && usersReportModel.getSelectDomainList().size()==1) {               	
                	
                	usersReportModel.setSelectDomainListSize(usersReportModel.getSelectDomainList().size());
                	listValueVO = (ListValueVO) usersReportModel.getSelectDomainList().get(0);
    				usersReportModel.setDomainCode(listValueVO.getValue());
    				usersReportModel.setDomainCodeDesc(listValueVO.getLabel());
    				listValueVO.getValue();
               
                }else{
                	usersReportModel.setSelectDomainListSize(0);
                    usersReportModel.setDomainShowFlag(false);
                    
                    loggedInUserDomainList.add(new ListValueVO(userVO.getDomainName(), userVO.getDomainID()));
    				usersReportModel.setSelectDomainList(loggedInUserDomainList);
    				usersReportModel.setSelectDomainListSize(usersReportModel.getSelectDomainList().size());
    				usersReportModel.setDomainCode(userVO.getDomainID());
    				usersReportModel.setDomainCodeDesc(userVO.getDomainID());    				
    				usersReportModel.getDomainCode();
                }
                 ArrayList<ListValueVO> origCatList = new ArrayList<>();
                 ArrayList<CategoryVO> catList = new ArrayList<>();
                 ArrayList<?> loadOtherCategorList=categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT);
                CategoryVO categoryVO = null;	
                for (int i = 0, k = loadOtherCategorList.size(); i < k; i++) {
					categoryVO = (CategoryVO) loadOtherCategorList.get(i);					
					origCatList.add(new ListValueVO(categoryVO.getCategoryName(),categoryVO.getCategoryCode()+":"+categoryVO.getDomainCodeforCategory()+":"+categoryVO.getSequenceNumber() ));					
					catList.add(categoryVO);
                }
                usersReportModel.setOrigCategoryList(origCatList);
                usersReportModel.setCatList(catList);
                usersReportModel.setOrigCategoryListSize(usersReportModel.getOrigCategoryList().size());

            } else {
            					
                usersReportModel.setDomainCode(userVO.getDomainID());
                usersReportModel.setDomainCodeDesc(userVO.getDomainName());
                usersReportModel.setDomainShowFlag(true);
                
                loggedInUserDomainList.add(new ListValueVO(userVO.getDomainName(), userVO.getDomainID()));
				usersReportModel.setSelectDomainList(loggedInUserDomainList);
				usersReportModel.setSelectDomainListSize(usersReportModel.getSelectDomainList().size());
				usersReportModel.getDomainCode();
               
                final ArrayList<?> categoryList = categoryWebDAO.loadCategorListByDomainCode(con, userVO.getDomainID());
                ArrayList<ListValueVO> origCatList = new ArrayList<>();
                ArrayList<CategoryVO> catList = new ArrayList<>();
                CategoryVO categoryVOO = null;	
                for (int i = 0, k = categoryList.size(); i < k; i++) {
					categoryVOO = (CategoryVO) categoryList.get(i);					
					origCatList.add(new ListValueVO(categoryVOO.getCategoryName(),categoryVOO.getCategoryCode()+":"+ categoryVOO.getDomainCodeforCategory()+":"+categoryVOO.getSequenceNumber() ));					
				    catList.add(categoryVOO);
                }               
                usersReportModel.setOrigCategoryList(origCatList);
                usersReportModel.setCatList(catList);
                usersReportModel.setOrigCategoryListSize(usersReportModel.getOrigCategoryList().size());
                if (categoryList != null) {
                    CategoryVO categoryVO = null;
                    final ArrayList<CategoryVO> list = new ArrayList<>();
                    for (int i = 0, j = categoryList.size(); i < j; i++) {
                        categoryVO = (CategoryVO) categoryList.get(i);
                       
                        if ("associate".equals(usersReportModel.getRequestType()) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue()) {
                            checkSecqNo(userVO, categoryVO, list);
                        }

                        else if ("associateOther".equals(usersReportModel.getRequestType()) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue()) {
                            checkSecqNo(userVO, categoryVO, list);
                        }
                        else if (categoryVO.getSequenceNumber() > userVO.getCategoryVO().getSequenceNumber()) {
                            list.add(categoryVO);
                        }
                    }
                    usersReportModel.setCategoryList(list);
                    if ("associate".equals(usersReportModel.getRequestType()) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue()) {
                        if (list.isEmpty()) {
                            throw new BTSLBaseException(this, methodName, "user.loaddomainlist.error.noagentcategoryfound");
                        }
                    }

                    else if ("associateOther".equals(usersReportModel.getRequestType()) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue()) {
                        if (list.isEmpty()) {
                            throw new BTSLBaseException(this, methodName, "user.loaddomainlist.error.noagentcategoryfound");
                        }
                    }

                }
            }

            final C2STransferDAO c2STransferDAO = new C2STransferDAO();
           
            usersReportModel.setOrigParentCategoryList(c2STransferDAO.loadC2SRulesListForChannelUserAssociation(con, userVO.getNetworkID()));
            usersReportModel.setParentCategoryList(null);

            final ArrayList<?> list = userVO.getGeographicalAreaList();
           
            if (list != null && list.size() > 1) {
                usersReportModel.setAssociatedGeographicalList(list);
                usersReportModel.setAssociatedGeographicalListSize(list.size());
                final UserGeographiesVO vo = (UserGeographiesVO) list.get(0);
                usersReportModel.setParentDomainTypeDesc(vo.getGraphDomainTypeName());
            } else if (list != null && list.size() == 1) {
            	usersReportModel.setAssociatedGeographicalListSize(list.size());
                usersReportModel.setAssociatedGeographicalList(null);
                final UserGeographiesVO vo = (UserGeographiesVO) list.get(0);
                usersReportModel.setParentDomainCode(vo.getGraphDomainCode());
                usersReportModel.setParentDomainDesc(vo.getGraphDomainName());
                usersReportModel.setParentDomainTypeDesc(vo.getGraphDomainTypeName());
            } else {
            	usersReportModel.setAssociatedGeographicalListSize(0);
                usersReportModel.setAssociatedGeographicalList(null);
            }
            
            if (BTSLUtil.isNullString(usersReportModel.getUserLanguage())) {
            	usersReportModel.setUserLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)) + "_" + (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            }
            usersReportModel.setUserLanguageList(LocaleMasterDAO.loadLocaleMasterData());// Added
            usersReportModel.setUserNamePrefixList(LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true));
            
            
        } catch (BTSLBaseException |SQLException  e) {
            _log.errorTrace(methodName, e);
          
        } catch (Exception e) {
        	 _log.errorTrace(methodName, e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("AddChannelUserServiceImpl#loadDomainList");
				mcomCon = null;
			}
            exitLoadDomainList(methodName);
        }
       
    }

	private void checkSecqNo(UserVO userVO, CategoryVO categoryVO,
			final ArrayList<CategoryVO> list) {
		if ((categoryVO.getSequenceNumber() == userVO.getCategoryVO().getSequenceNumber() + 1) && PretupsI.AGENTCATEGORY.equals(categoryVO.getCategoryType())) {
		    list.add(categoryVO);
		}
	}

	private void exitLoadDomainList(final String methodName) {
		if (_log.isDebugEnabled()) {
		    _log.debug(methodName, "Exiting");
		}
	}

	private void enteredLoadDomainList(final String methodName) {
		if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered");
        }
	}

	@Override
	public List<CategoryVO> loadCategoryLst(UsersReportModel usersReportModel, UserVO userVO,String domainCode) {
	
		final String methodName = "getCategoryList";
        enterInGetAddChanelUserList(methodName);

        final ArrayList<CategoryVO> list = new ArrayList<>();
        try {

            if (usersReportModel.getOrigCategoryList() != null && !BTSLUtil.isNullString(usersReportModel.getDomainCode())) {
                CategoryVO categoryVO ;
                for (int i = 0, j = usersReportModel.getOrigCategoryList().size(); i < j; i++) {
                    categoryVO = (CategoryVO) usersReportModel.getOrigCategoryList().get(i);                  
                    if (categoryVO.getDomainCodeforCategory().equals(usersReportModel.getDomainCode())) {
                        list.add(categoryVO);
                    }
                }
            }
           
            usersReportModel.setCategoryList(list);         
            usersReportModel.setParentCategoryList(null);                    
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
           
        }
        exitLoadDomainList(methodName);
        return list;  
	   }

	@SuppressWarnings("unchecked")
	@Override
	public List<ListValueVO> getAddChannelUserList(UserVO userVO,String zoneCode, String domainCode, String userName,String parentCategoryCode,HttpServletRequest request, HttpServletResponse response) {
	
				final String methodName ="getAddChannelUserList";
		        enterInGetAddChanelUserList(methodName);			  
			    Connection con = null;
				MComConnectionI mcomCon = null;
				ChannelUserReportDAO channelUserDAO = null;
				UserWebDAO userwebDAO = null;
				List<ListValueVO> userList = null;
				ChannelUserWebDAO channelUserWebDAO = null;
			  try {			
				    mcomCon = new MComConnection();			
					con=mcomCon.getConnection();			
					channelUserDAO = new ChannelUserReportDAO(); 
					userwebDAO = new UserWebDAO();
					channelUserWebDAO = new ChannelUserWebDAO();
					UsersReportModel theForm = (UsersReportModel) request.getSession().getAttribute("usersReport");
					if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
		                if (parentCategoryCode.equals(PretupsI.ALL)) {
		                    userList = channelUserDAO.loadUserListOnZoneCategoryHierarchy(con, PretupsI.ALL,zoneCode, userName, userVO.getUserID(),domainCode);
		                } else {
		                    userList = channelUserDAO.loadUserListOnZoneCategoryHierarchy(con, parentCategoryCode,zoneCode, userName, userVO.getUserID(), domainCode);
		                }
		            } else {
		                if (parentCategoryCode.equals(PretupsI.ALL)) {
		                    userList = channelUserDAO.loadUserListOnZoneDomainCategory(con, PretupsI.ALL,zoneCode, null, "%" + userName + "%", userVO.getUserID(),
		                       domainCode);
		                } else {
		                    userList = channelUserDAO.loadUserListOnZoneDomainCategory(con, parentCategoryCode, zoneCode, null, "%" + userName + "%", userVO.getUserID(), domainCode);
		                }
		            } 
										
	                if (((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, userVO.getNetworkID(), theForm.getCategoryCode()))
	                                .intValue() == 0 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()) {

	                	
	                    // load the Commision profile dropdown/*****/
	                    theForm.setCommissionProfileList(userwebDAO.loadCommisionProfileListByCategoryIDandGeography(con, theForm.getCategoryCode(), userVO.getNetworkID(),null));

	                    final CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
	                    // load the User Grade dropdown
	                    theForm.setUserGradeList(categoryGradeDAO.loadGradeList(con, theForm.getCategoryCode()));

	                    // load the Transfer Profile dropdown
	                    final TransferProfileDAO profileDAO = new TransferProfileDAO();
	                    theForm.setTrannferProfileList(profileDAO.loadTransferProfileByCategoryID(con, userVO.getNetworkID(), theForm.getCategoryCode(),
	                                    PretupsI.PARENT_PROFILE_ID_USER));
	                    // load the Transfer Rule Type at User level
	                    final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, userVO
	                                    .getNetworkID(), theForm.getCategoryCode())).booleanValue();
	                    if (isTrfRuleTypeAllow) {
	                        theForm.setTrannferRuleTypeList(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
	                    }

	                    
	                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
	                        theForm.setLmsProfileList(channelUserWebDAO.getLmsProfileList(con, userVO.getNetworkID()));
	                    }
	                }
	                
	                request.getSession().setAttribute("usersReport", theForm);
				}catch(BTSLBaseException | SQLException e){
				  _log.errorTrace(methodName, e);
			  }		 
			finally {
				if(mcomCon != null){
					mcomCon.close("AddChannelUserServiceImpl#getAddChannelUserList");
					mcomCon=null;
				}
				if (_log.isDebugEnabled()) {
					_log.debug("AddChannelUserServiceImpl#getAddChannelUserList", PretupsI.EXITED);
				}
			}
			return userList;
	   }

	private void enterInGetAddChanelUserList(final String methodName) {
		if (_log.isDebugEnabled()) {
		    _log.debug(methodName, "Entered");
		}
	}

	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.reports.service.AddChannelUserService#addChannelUserSubmit(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, com.btsl.pretups.channel.reports.web.UsersReportModel, com.btsl.pretups.channel.reports.web.UsersReportModel, com.btsl.user.businesslogic.UserVO, org.springframework.ui.Model, org.springframework.validation.BindingResult)
	 */
	@SuppressWarnings("unused")
	@Override
	public boolean addChannelUserSubmit(HttpServletRequest request,HttpServletResponse response, UsersReportModel theForm,UsersReportModel usersReportModelNew,UserVO sessionUserVO, Model model, BindingResult bindingResult) {

		
	        final String methodName = "save";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "Entered");
	        }

	        Connection con = null;
	        MComConnectionI mcomCon = null;
	        ChannelUserWebDAO channelUserWebDAO = null;   
	        boolean changePwdFlag = false;
	        OperatorUtilI operatorUtili = null;
	        theForm.setRequestType("add");
	        try {
	            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
	            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
	        } catch (Exception e) {
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserUpdateAction[processUploadedFile]", "", "",
	                            "", "Exception while loading the class at the call:" + e.getMessage());
	        }
	        try {
	            channelUserWebDAO = new ChannelUserWebDAO();



	            if ("add".equals(theForm.getRequestType())) {

	                final ChannelUserVO channelUserVO = new ChannelUserVO();
	                mcomCon = new MComConnection();
	                con=mcomCon.getConnection();
	                final UserDAO userDAO = new UserDAO();
	                final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
	                NetworkPrefixVO networkPrefixVO = null;
	                String filterMsisdn = null;
	                final Date currentDate = new Date();
	                String paymentNumber = "";
	                String[] servicesTypesArray =null;
	                if(usersReportModelNew.getServicesListSelected()!=null && !usersReportModelNew.getServicesListSelected().isEmpty()){
	                	servicesTypesArray = new String[usersReportModelNew.getServicesListSelected().size()];
		                for (int i = 0; i <usersReportModelNew.getServicesListSelected().size(); i++) {
		                	servicesTypesArray[i] = usersReportModelNew.getServicesListSelected().get(i).getValue();
		                }
	                }
	                               	               
	                
	                channelUserVO.setServiceTypeList(servicesTypesArray);	                	               	                
	                if ("add".equals(theForm.getRequestType())) {
	                    if (userDAO.isUserLoginExist(con, theForm.getWebLoginID(), null)) {
	                     	                       
	                        final String arr[] = { theForm.getWebLoginID() };	                     
	                        model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("user.addchanneluser.error.loginallreadyexist", arr));
	                		return false;
	                        
	                    }
	                 // Security CSRF starts here
	                    boolean flag = CSRFTokenUtil.isValid(request);
	                    if (!flag) {
	                        if (_log.isDebugEnabled()) {
	                            _log.debug("CSRF", "ATTACK!");
	                        }
		                       // throw new BTSLBaseException
	                    }
	                    // Security CSRF ends here
	                    // Unique check for External code(if given)
	                    if (!BTSLUtil.isNullString(theForm.getExternalCode())) {
	                        if (channelUserWebDAO.isExternalCodeExist(con, theForm.getExternalCode().trim(), null)) {
	                          	                        
	                        	final String arr[] = { theForm.getExternalCode() };	                     
		                        model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("user.addchanneluser.error.externalallreadyexist", arr));
		                		return false;
	                        }
	                    }
	                    final CategoryVO categoryVO = usersReportModelNew.getCategoryVO();
	                    String password = null;
	                    /*
	                     * check the type of user for add, for generating the
	                     * appropriate prefix
	                     * 1)Location Admin (prefix like DL/LA00001 DL for
	                     * networkCode LA=Location Admin)
	                     * 2)CCE (prefix like DL/CC00001 DL for networkCode
	                     * CC=Customer Care)
	                     * 3)BCU (prefix like DL/BC00001 DL for networkCode BC=BCU)
	                     */
	                    final int length = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
	                    String id = BTSLUtil.padZeroesToLeft((IDGenerator.getNextID(TypesI.USERID, TypesI.ALL, sessionUserVO.getNetworkID())) + "", length);	                   
	                    id = sessionUserVO.getNetworkID() + categoryVO.getUserIdPrefix() + id;
	                    
	                    channelUserVO.setUserID(id);
	                    _log.info(methodName, "UserId:" + channelUserVO.getUserID());

	                    channelUserVO.setNetworkID(sessionUserVO.getNetworkID());


	                    channelUserVO.setLoginID(theForm.getWebLoginID());
	                    // while inserting encrypt the password
	                    if (!BTSLUtil.isNullString(theForm.getWebLoginID())) {
	                        password = BTSLUtil.encryptText(theForm.getShowPassword());
	                    }
	                    channelUserVO.setPassword(password);
	                    channelUserVO.setPasswordModifyFlag(true);
	                    /*
	                     * when user will select the value from SelctChannelCategory
	                     * dropdown
	                     * at that time this value set into the ChannelCategoryCode
	                     * and when showParentSearch method called categoryCode set
	                     */
	                    final String[] categoryCodeValue = theForm.getFromtransferCategoryCode().split(":");
	                    channelUserVO.setCategoryCode(categoryCodeValue[0]);//need to test
	                    /*
	                     * 
	                     * 1)categoryId represent the selected category on
	                     * selectChannelUser
	                     * this is the combination of categoryCode, domainCode and
	                     * sequenceNo
	                     * Here we check
	                     * if sequenceNo == 1
	                     * psrentId = ROOT and ownerid is same with the userid of
	                     * the user that are inserted
	                     * else
	                     * get from the form(set on form after searching the parent)
	                     * theForm.getChannelCategoryCode().split(":")
	                     */

	                    final String[] categoryID = theForm.getFromtransferCategoryCode().split(":");
	                    if ("1".equals(categoryID[2])) {
	                        channelUserVO.setParentID(PretupsI.ROOT_PARENT_ID);
	                        channelUserVO.setOwnerID(channelUserVO.getUserID());
	                    } else {
	                    	String[] ownerId=theForm.getUserName().split(":");
 	                        channelUserVO.setParentID(theForm.getParentID());
	                        channelUserVO.setOwnerID(ownerId[1]);
	                        /*
	                         * This is the case when no search has performed,
	                         * Like DIST add SE at that time no search performed so
	                         * we explicitly
	                         * set the ownerid=ownerid of the session user
	                         */
	                        if (BTSLUtil.isNullString(channelUserVO.getOwnerID())) {
	                            channelUserVO.setOwnerID(sessionUserVO.getOwnerID());
	                        }

	                        /*
	                         * This is the case when no search has performed,
	                         * Like
	                         * 1)BUC add SE ownerId and parentId both are same
	                         * 2)DIST add SE parentID=userid of the session user
	                         */
	                        if (PretupsI.OPERATOR_TYPE_OPT.equals(sessionUserVO.getDomainID())) {
	                            if (BTSLUtil.isNullString(channelUserVO.getParentID())) {
	                                channelUserVO.setParentID(channelUserVO.getOwnerID());
	                            }
	                        } else {
	                            if (BTSLUtil.isNullString(channelUserVO.getParentID())) {
	                                channelUserVO.setParentID(sessionUserVO.getUserID());
	                            }
	                        }
	                    }
	                    channelUserVO.setAllowedIps(theForm.getAllowedIPs());

	                    final StringBuilder str = new StringBuilder();
	                    /*
	                     * theForm.getAllowedDays returns an string array but in DB
	                     * we insert a single
	                     * string value of the allowed days like 1,4,7, for yhis
	                     * prupose convert the string
	                     * array into string
	                     */
	                    if (theForm.getAllowedDays() != null && theForm.getAllowedDays().length > 0) {
	                        str.append(theForm.getAllowedDays()[0]);
	                        for (int i = 1, j = theForm.getAllowedDays().length; i < j; i++) {
	                            str.append("," + theForm.getAllowedDays()[i]);
	                        }
	                    }

	                    channelUserVO.setAllowedDays(str.toString());
	                    channelUserVO.setFromTime(theForm.getAllowedFormTime());
	                    channelUserVO.setToTime(theForm.getAllowedToTime());
	                    channelUserVO.setLastLoginOn(currentDate);
	                    channelUserVO.setEmpCode(theForm.getEmpCode());
	                    channelUserVO.setContactNo(theForm.getContactNo());
	                    /*
	                     * If USER_APPROVAL_LEVEL = 0 no approval required, if
	                     * USER_APPROVAL_LEVEL = 1 level 1 approval required,
	                     * if USER_APPROVAL_LEVEL = 2 level 2 approval required'
	                     * While adding user check whether the approval is required
	                     * or not

	                     * set status = N(New)//approval required
	                     * else
	                     * set status = Y(Active)
	                     */
	                    if (((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, sessionUserVO.getNetworkID(), theForm.getCategoryCode())).intValue() > 0) {
	                        channelUserVO.setStatus(PretupsI.USER_STATUS_NEW);// W
	                        // New
	                        channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_NEW);// W
	                        // New

	                    } else {
	                        if (LookupsCache.getLookupCodeList(PretupsI.ALLOWED_USER_STATUS).contains(PretupsI.USER_STATUS_PREACTIVE)) {
	                            channelUserVO.setStatus(PretupsI.USER_STATUS_PREACTIVE);// PA
	                            // Active
	                            channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_PREACTIVE);// PA
	                            // Active
	                        } else {
	                            channelUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);// A
	                            // Active
	                            channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_ACTIVE);// A
	                            // Active
	                        }
	                    }

	                    channelUserVO.setEmail(theForm.getEmail());
	        
	                    channelUserVO.setAlertEmail(theForm.getOtherEmail());
	                    // Addition ends
	              
	                    channelUserVO.setCompany(theForm.getCompany());
	                    channelUserVO.setFax(theForm.getFax());
	                    channelUserVO.setLanguage(theForm.getUserLanguage());
	                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED))).booleanValue()) {
	                        channelUserVO.setFirstName(theForm.getFirstName());
	                        channelUserVO.setLastName(theForm.getLastName());
	                        if (!BTSLUtil.isNullString(theForm.getLastName())) {
	                            channelUserVO.setUserName(theForm.getFirstName() + " " + theForm.getLastName());
	                        } else {
	                            channelUserVO.setUserName(theForm.getFirstName());
	                        }
	                    } else {
	                        channelUserVO.setUserName(theForm.getChannelUserName());
	                    }
	                    // End Added 
	                    channelUserVO.setPasswordModifiedOn(currentDate);
	                    channelUserVO.setContactNo(theForm.getContactNo());
	                    channelUserVO.setDesignation(theForm.getDesignation());

	                    final ArrayList<UserPhoneVO> phoneList = new ArrayList<>();
	                    Locale locale = null;
	                    /*
	                     * Here we prepare the phone list from the form, only add
	                     * those rows
	                     * where user entered the msisdn because msisdn list
	                     * contains
	                     * number of VOs but we have to insert only those where
	                     * msisdn != ""
	                     * 
	                     * 1) All the PhoneVOs in MSISDN list will have the same
	                     * UserId
	                     * that are inserted into the DB, so set this userId
	                     * into the List
	                     * 2)set the default vales
	                     * 3)Also set the primary number of the User from the msisdn
	                     * list
	                     */
	                   
	                    theForm.setMsisdnList(usersReportModelNew.getMsisdnList());
	                    if (theForm.getMsisdnList() != null) {
	                        UserPhoneVO phoneVO = null;
	                        for (int i = 0, j = theForm.getMsisdnList().size(); i < j; i++) {
	                            phoneVO = theForm.getMsisdnList().get(i);

	                            if (!BTSLUtil.isNullString(phoneVO.getMsisdn())) {
	                                filterMsisdn = PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn());
	                                phoneVO.setMsisdn(filterMsisdn);
	                                phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
	                                phoneVO.setUserId(channelUserVO.getUserID());
	                                // set the default values
	                                phoneVO.setCreatedBy(sessionUserVO.getActiveUserID());
	                                phoneVO.setModifiedBy(sessionUserVO.getActiveUserID());
	                                phoneVO.setCreatedOn(currentDate);
	                                phoneVO.setModifiedOn(currentDate);
	                                phoneVO.setPinModifiedOn(currentDate);

	                             
	                                if (!BTSLUtil.isNullString(channelUserVO.getLanguage())) {
	                                    final String lang_country[] = (channelUserVO.getLanguage()).split("_");
	                                    phoneVO.setPhoneLanguage(lang_country[0]);
	                                    phoneVO.setCountry(lang_country[1]);
	                                } else {
	                                    phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
	                                    phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
	                                }
	         
	                                final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(phoneVO
	                                                .getMsisdn())));
	                                phoneVO.setPrefixID(prefixVO.getPrefixID());

	                                if (TypesI.YES.equals(phoneVO.getPrimaryNumber())) {
	                                    channelUserVO.setMsisdn(phoneVO.getMsisdn());
	                                    channelUserVO.setPrimaryMsisdnPin(phoneVO.getSmsPin());
	                                    locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
	                                }
	                                /*
	                                 * Code Added for MNP
	                                 * Preference to check whether MNP is allowed in
	                                 * system or not.
	                                 * If yes then check whether Number has not been
	                                 * ported out, If yes then throw error, else
	                                 * continue
	                                 */
	                                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
	                                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(filterMsisdn));
	                                    if (networkPrefixVO == null || !networkPrefixVO.getNetworkCode().equals(sessionUserVO.getNetworkID())) {
	                                        final String[] arr1 = { filterMsisdn, networkPrefixVO.getNetworkName() };
	                                        _log.error(methodName, "Error: MSISDN Number" + filterMsisdn + " not belongs to " + sessionUserVO.getNetworkName() + "network");
	                                        model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("user.assignphone.error.msisdnnotinsamenetwork", arr1));
	                                		return false;
	                                    }
	                                    boolean numberAllowed = false;
	                                    if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
	                                        numberAllowed = new NumberPortDAO().isExists(con, filterMsisdn, "", PretupsI.PORTED_IN);
	                                        if (!numberAllowed) {
	                    	                                       
	                                        	 model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("user.assignphone.error.msisdnnotinsamenetwork", new String[] { filterMsisdn, networkPrefixVO.getNetworkName() }));
	 	                                		return false;
	                                        }
	                                    } else {
	                                        numberAllowed = new NumberPortDAO().isExists(con, filterMsisdn, "", PretupsI.PORTED_OUT);
	                                        if (numberAllowed) {
	                 
	                                        	model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("c2s.reports.error.msisdnnotinsamenetwork", new String[] { filterMsisdn, networkPrefixVO.getNetworkName() }));
	                                    		return false;
	                                        }
	                                    }
	                                }
	                                
	                                phoneVO.setPinModifyFlag(true);
	                                if (userDAO.isMSISDNExist(con, PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()), phoneVO.getUserId())) {
	                                    final String[] arr = { phoneVO.getMsisdn() };
	                                    _log.error(methodName, "Error: MSISDN Number is already assigned to another user");
	                               
	                                    model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("user.assignphone.error.msisdnallreadyexist", arr));
	                            		return false;
	                                }
	                                if(phoneVO.getPinReset()==null) 
	                                { 
	                                        phoneVO.setPinReset(PretupsI.YES); 
	                                }

	                                phoneList.add(phoneVO);
	                            }
	                        }
	                    }
	                    channelUserVO.setMsisdnList(phoneList);

	                    // while adding channel user userType value will be CHANNEL
	                    channelUserVO.setUserType(PretupsI.CHANNEL_USER_TYPE);

	                    /*
	                     * User Code value is dependent on the
	                     * System Preferences values USER_CODE_REQUIRED
	                     * if it true user can enter the user code value from the
	                     * screen
	                     * if it is false pass the primary msisdn number as user
	                     * code value
	                     * 
	                     * userCodeFlag is set on the form in the addModify method
	                     * of the channelUserAction class
	                     * on the bais of this value we set the userCode value into
	                     * the VO
	                     * 
	                     * msisdn field contain the primary msisdn number of the
	                     * user
	                     */
	                    if (theForm.isUserCodeFlag()) {
	                        channelUserVO.setUserCode(theForm.getUserCode());  
	                    } else {
	                        channelUserVO.setUserCode(channelUserVO.getMsisdn());
	                    }

	                    channelUserVO.setContactPerson(theForm.getContactPerson());

	                    channelUserVO.setUserGrade(theForm.getGradeCode());
	                    channelUserVO.setTransferProfileID(theForm.getValue());

	                    if (theForm.getCombinedKey() != null) {
	                        channelUserVO.setCommissionProfileSetID(theForm.getCombinedKey().split(":")[0]);
	                    }
	                    channelUserVO.setTrannferRuleTypeId(theForm.getTrannferRuleTypeId());
	                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
	                        channelUserVO.setLmsProfile(theForm.getLmsProfileId());

	                    }


	                    channelUserVO.setInSuspend(theForm.getInsuspend());
	                    channelUserVO.setOutSuspened(theForm.getOutsuspend());

	                    channelUserVO.setCreatedBy(sessionUserVO.getActiveUserID());
	                    channelUserVO.setCreatedOn(currentDate);
	                    channelUserVO.setModifiedBy(sessionUserVO.getActiveUserID());
	                    channelUserVO.setModifiedOn(currentDate);
	                    channelUserVO.setAddress1(theForm.getAddress1());
	                    channelUserVO.setAddress2(theForm.getAddress2());
	                    channelUserVO.setCity(theForm.getCity());
	                    channelUserVO.setState(theForm.getState());
	                    channelUserVO.setCountry(theForm.getCountry());
	                    channelUserVO.setRsaFlag(theForm.getRsaAuthentication());
	                    channelUserVO.setSsn(theForm.getSsn());
	                    channelUserVO.setUserNamePrefix(theForm.getUserNamePrefixCode());
	                    channelUserVO.setExternalCode(theForm.getExternalCode());
	                    channelUserVO.setShortName(theForm.getShortName());
	                    // Added for Authentication Type

	                    if (!BTSLUtil.isNullString(theForm.getAppointmentDate())) {
	                        channelUserVO.setAppointmentDate(BTSLUtil.getDateFromDateString(theForm.getAppointmentDate()));
	                    }

	                    // for Zebra and Tango 1 
	                    channelUserVO.setUserProfileID(channelUserVO.getUserID());
	                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()) {
	                        if (_log.isDebugEnabled()) {
	                            _log.debug("addModify", "Enter ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()=" + ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue());
	                        }
	                        channelUserVO.setMcommerceServiceAllow(theForm.getMcommerceServiceAllow());
	                        if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(theForm.getMcommerceServiceAllow())) {
	                            if (!BTSLUtil.isNullString(theForm.getMpayProfileIDWithGrad()) && theForm.getMpayProfileIDWithGrad().contains(":")) {
	                                channelUserVO.setMpayProfileID(theForm.getMpayProfileIDWithGrad().split(":")[1]);
	                            }
	                        } else {
	                            channelUserVO.setMpayProfileID("");
	                        }
	                        if (_log.isDebugEnabled()) {
	                            _log.debug("addModify", "Exit ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()=" + ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue());
	                        }
	                    } else {
	                        channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
	                        channelUserVO.setMpayProfileID("");
	                    }
	                    // Condition modified  for alert types
	                    if (TypesI.YES.equals(usersReportModelNew.getCategoryVO().getLowBalAlertAllow())) {
	                        final String delimiter = ";";
	                        final String allowforself = theForm.getLowBalAlertToSelf();
	                        final String allowforparent = theForm.getLowBalAlertToParent();
	                        final String allowforOther = theForm.getLowBalAlertToOther();
	                        final StringBuilder  alerttype = new StringBuilder ("");
	                        if (TypesI.YES.equals(allowforself)) {
	                            alerttype.append(PretupsI.ALERT_TYPE_SELF);
	                        }
	                        if (TypesI.YES.equals(allowforparent)) {
	                            alerttype.append("".equals(alerttype.toString()) ? "" : delimiter);
	                            alerttype.append(PretupsI.ALERT_TYPE_PARENT);
	                        }
	                        if (TypesI.YES.equals(allowforOther)) {
	                            alerttype.append("".equals(alerttype.toString()) ? "" : delimiter);
	                            alerttype.append(PretupsI.ALERT_TYPE_OTHER);
	                        }

	                        if (!"".equals(alerttype.toString())) {
	                            channelUserVO.setLowBalAlertAllow(TypesI.YES);
	                            channelUserVO.setAlertType(alerttype.toString());
	                        } else {
	                            channelUserVO.setLowBalAlertAllow("N");
	                        }

	                    }
	                    // modification ends
	                    else {
	                        channelUserVO.setLowBalAlertAllow("N");
	                    }

	                    if (!BTSLUtil.isNullString(theForm.getSubOutletCode())) {
	                        final String[] sublookupID = theForm.getSubOutletCode().split(":");
	                        channelUserVO.setSubOutletCode(sublookupID[0]);
	                    }
	                    channelUserVO.setLongitude(theForm.getLongitude());
	                    channelUserVO.setLatitude(theForm.getLatitude());
	                    channelUserVO.setDocumentType(theForm.getDocumentType()); 
	                    channelUserVO.setDocumentNo(theForm.getDocumentNo());
	                    channelUserVO.setPaymentType(theForm.getPaymentType());
	                    final int userCount = userDAO.addUser(con, channelUserVO);

	                    if (userCount <= 0) {
	                        con.rollback();
	                        _log.error(methodName, "Error: while Inserting User");
	                        final String arr[] = { methodName };
	                        model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("error.general.processing", arr));
	                		return false;
	                    }

	                    /*
	                     * If user status is Y(active) means user is activaed at the
	                     * creation time, so we are setting the
	                     * activated_on = currentDate. This indicate user is actived
	                     * on the same date
	                     */
	                    if (PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
	                        channelUserVO.setActivatedOn(currentDate);
	                    } else {
	                        channelUserVO.setActivatedOn(null);
	                    }
	                  //Single Association with control
	                    if (_log.isDebugEnabled()){
	     	        		_log.debug(methodName,"theForm.getLmsProfileId() = "+theForm.getLmsProfileId()+", channelUserVO.getLmsProfile() = "+channelUserVO.getLmsProfile());
	     	        	}
	    	            double targetCount = 0d;
	    	 	        double controlCount = 0d; 
	    	 	        int retval4Association = 0;
	    	 	        int retval4deAssociation = 0;
	    	 	        int retvalTargetControl = 0;
	    	 	        Map<String,Double> countOfUsersInTargetControlGroup = null;
	    	 	        double targetCountOfassprofile = 0d;
	    	 	        double controlCountOfassprofile = 0d; 
	    	 	        int retval4AssociationOfassprofile = 0;
	    	 	        int retval4deAssociationOfassprofile = 0;
	    	 	        int retvalTargetControlOfassprofile = 0;
	    	 	        Map<String,Double> countOfUsersInTargetControlGroupOfassprofile = null;
	    	 	        ChannelUserVO channelUserLMSVO = null;
	    	 	        if(!BTSLUtil.isNullString(theForm.getLmsProfileId()) && !BTSLUtil.isNullString(channelUserVO.getLmsProfile()))
	    	 	        {
	    	 	          countOfUsersInTargetControlGroup = channelUserDAO.countOfUsersInTargetControlGroup(con,channelUserVO.getLmsProfile());
	    	 	          if(countOfUsersInTargetControlGroup!=null){
	    	 	        	 controlCount = countOfUsersInTargetControlGroup.get("control_count");
	    	 	        	 targetCount = countOfUsersInTargetControlGroup.get("target_count");
	    	 	        	 retval4Association = Double.compare(targetCount, 0d);
	    	 	        	 retval4deAssociation = Double.compare(controlCount, 0d);
	    	 	        	 retvalTargetControl = Double.compare(targetCount,controlCount);
	    	 	        	if (_log.isDebugEnabled()){
	    	 	        		_log.debug(methodName,"control_count = "+controlCount+", target_count = "+targetCount+", retval4Association = "+retval4Association+", retval4deAssociation = "+retval4deAssociation+", retvalTargetControl = "+retvalTargetControl);
	    	 	        	}
	    	 	          }
	    	 	          //Need to check if channel user already associated with lms profile and wants to associate with another lms profile
	    	 	          channelUserLMSVO = channelUserDAO.loadChannelUser(con, channelUserVO.getUserID());
	    	 	        	if (_log.isDebugEnabled()) {
	    	 	        		_log.debug(methodName,"Already associated with lms profile :: channelUserLMSVO.getControlGroup() = "+channelUserLMSVO.getControlGroup());
	    	 	        	}
	    	 	        	if(!BTSLUtil.isNullString(channelUserLMSVO.getLmsProfile()) && !BTSLUtil.isNullString(channelUserLMSVO.getControlGroup()) && PretupsI.NO.equalsIgnoreCase(channelUserLMSVO.getControlGroup()))
	    	 	        	{
	    	 	        		countOfUsersInTargetControlGroupOfassprofile = channelUserDAO.countOfUsersInTargetControlGroup(con,channelUserLMSVO.getLmsProfile());
	    	 		 	          if(countOfUsersInTargetControlGroupOfassprofile!=null){
	    	 		 	        	 controlCountOfassprofile = countOfUsersInTargetControlGroupOfassprofile.get("control_count");
	    	 		 	        	 targetCountOfassprofile = countOfUsersInTargetControlGroupOfassprofile.get("target_count");
	    	 		 	        	 retval4AssociationOfassprofile = Double.compare(targetCountOfassprofile, 1d);
	    	 		 	        	 retval4deAssociationOfassprofile = Double.compare(controlCountOfassprofile, 1d);
	    	 		 	        	 retvalTargetControlOfassprofile = Double.compare(targetCountOfassprofile,controlCountOfassprofile);
	    	 		 	        	 if (_log.isDebugEnabled()){
	    	 		 	        		_log.debug(methodName,"Already associated with lms profile"+channelUserLMSVO.getLmsProfile()+" :: control_count = "+controlCountOfassprofile+", target_count = "+targetCountOfassprofile+", retval4Association = "+retval4AssociationOfassprofile+", retval4deAssociation = "+retval4deAssociationOfassprofile+", retvalTargetControl = "+retvalTargetControlOfassprofile);
	    	 		 	        	 }
	    	 		 	        	 if(retvalTargetControlOfassprofile < 0 || (retval4deAssociationOfassprofile == 0 && retval4AssociationOfassprofile==0) ){
	    								if (_log.isDebugEnabled()) _log.debug(methodName,"Already associated with lms profile :: User de-association is not allowded from target group as one user still exists into the control group of already associated profile");
	    								String arr[] = {channelUserVO.getUserName(),paymentNumber};
	    				 	            String key="user.associatechanneluser.alreadyassociated.oneuserstillexistsintocontrolgroup";
	    				 	            new BTSLMessages(key,arr,"AddSuccess");

	    							 }
	    	 		 	        	 else if(retvalTargetControlOfassprofile > 0 || ( retval4AssociationOfassprofile > 1 && retval4deAssociationOfassprofile < 0))
	    							 {
	    								if (_log.isDebugEnabled()){
	    									_log.debug(methodName,"Already associated with lms profile :: User Association is allowded into the target group as no such user into the control group of already associate profile");
	    								}
	    								
	    							 } 
	    	 		 	          }
	    	 	        	}
	    	 	          //
	    	 	          if(BTSLUtil.isNullString(channelUserVO.getControlGroup())){
	    	 	        	 if (_log.isDebugEnabled()) _log.debug(methodName,"The value of control group is missing");
	    						String arr[] = {channelUserVO.getUserName(),paymentNumber};
	    		 	            String key="user.associatechanneluser.updatecontrollednotfound";
	    		 	            new BTSLMessages(key,arr,"AddSuccess");

	    					}
	    					else if(PretupsI.YES.equalsIgnoreCase(channelUserVO.getControlGroup()))
	    					{
	    						if(retval4Association>=1)
	    						{
	    							if(channelUserDAO.isProfileActive(channelUserVO.getMsisdn(),channelUserVO.getLmsProfile())){
	    								if (_log.isDebugEnabled()) _log.debug(methodName,"User assocition is not allowded into control group profile as profile is active");
	    								String arr[] = {channelUserVO.getUserName(),paymentNumber};
	    				 	            String key="user.associatechanneluser.updatecontrolledactive";
	    				 	            new BTSLMessages(key,arr,"AddSuccess");

	    							} 
	    						} else {
	    							if (_log.isDebugEnabled()) _log.debug(methodName,"User association is not allowded into control group as  no user belong to target group of this profile");
	    							String arr[] = {channelUserVO.getUserName(),paymentNumber};
	    			 	            String key="user.associatechanneluser.controlgroup.oneuserrequiredtotargetgroup";
	    			 	            new BTSLMessages(key,arr,"AddSuccess");

	    						}
	    					}
	    					else if(PretupsI.NO.equalsIgnoreCase(channelUserVO.getControlGroup()))
	    					{
	    						// if profile was already associated with lms profile into the control group
	    		 	        	channelUserLMSVO = channelUserDAO.loadChannelUser(con, channelUserVO.getUserID());
	    		 	        	if(!BTSLUtil.isNullString(channelUserLMSVO.getLmsProfileId()) && !BTSLUtil.isNullString(channelUserLMSVO.getControlGroup()) && PretupsI.YES.equalsIgnoreCase(channelUserLMSVO.getControlGroup()))
	    		 	        	{
	    		 	        	  countOfUsersInTargetControlGroup = channelUserDAO.countOfUsersInTargetControlGroup(con,channelUserLMSVO.getLmsProfileId());
	    	 		 	          if(countOfUsersInTargetControlGroup!=null){
	    	 		 	        	 controlCount = countOfUsersInTargetControlGroup.get("control_count");
	    	 		 	        	 targetCount = countOfUsersInTargetControlGroup.get("target_count");
	    	 		 	        	 retval4Association = Double.compare(targetCount, 0d);
	    	 		 	        	 retval4deAssociation = Double.compare(controlCount, 0d);
	    	 		 	        	 retvalTargetControl = Double.compare(targetCount,controlCount);
	    	 		 	        	if (_log.isDebugEnabled()){
	    	 		 	        		_log.debug(methodName,"control_count = "+controlCount+", target_count = "+targetCount+", retval4Association = "+retval4Association+", retval4deAssociation = "+retval4deAssociation+", retvalTargetControl = "+retvalTargetControl);
	    	 		 	        	}
	    							if(retvalTargetControl == 0 || retval4Association>=0 )
	    							{
	    								if (_log.isDebugEnabled()){
	    									_log.debug(methodName,"User Association is allowded into the target group as no such user into the control group of this profile");
	    								}
	    								
	    							} else if(retval4deAssociation <=1) {
	    								if (_log.isDebugEnabled()) _log.debug(methodName,"User de-association is not allowded from target group as one user still exists into the control group of this profile");
	    								String arr[] = {channelUserVO.getUserName(),paymentNumber};
	    				 	            String key="user.associatechanneluser.targetgroup.oneuserstillexistsintocontrolgroup";
	    				 	            new BTSLMessages(key,arr,"AddSuccess");

	    							}
	    		 		 	   }
	    					}
	    					}
	    	 	        } else {
	    	 	        	if (_log.isDebugEnabled()) {
	    	 	        		_log.debug(methodName,"If profile was already associated with lms profile into the control group : channelUserVO.getUserID() = "+channelUserVO.getUserID());
	    	 	        	}
	    	 	        	// if profile was already associated with lms profile into the control group
	    	 	        	channelUserLMSVO = channelUserDAO.loadChannelUser(con, channelUserVO.getUserID());
	    	 	        	if (_log.isDebugEnabled()) {
	    	 	        		_log.debug(methodName," channelUserLMSVO.getControlGroup() = "+channelUserLMSVO.getControlGroup());
	    	 	        	}
	    	 	        	if(!BTSLUtil.isNullString(channelUserLMSVO.getLmsProfile()) && !BTSLUtil.isNullString(channelUserLMSVO.getControlGroup()))
	    	 	        	{
	    	 	        		countOfUsersInTargetControlGroup = channelUserDAO.countOfUsersInTargetControlGroup(con,channelUserLMSVO.getLmsProfile());
	    	 		 	          if(countOfUsersInTargetControlGroup!=null){
	    	 		 	        	 controlCount = countOfUsersInTargetControlGroup.get("control_count");
	    	 		 	        	 targetCount = countOfUsersInTargetControlGroup.get("target_count");
	    	 		 	        	 retval4Association = Double.compare(targetCount, 1d);
	    	 		 	        	 retval4deAssociation = Double.compare(controlCount, 1d);
	    	 		 	        	 retvalTargetControl = Double.compare(targetCount,controlCount);
	    	 		 	        	if (_log.isDebugEnabled()){
	    	 		 	        		_log.debug(methodName,"control_count = "+controlCount+", target_count = "+targetCount+", retval4Association = "+retval4Association+", retval4deAssociation = "+retval4deAssociation+", retvalTargetControl = "+retvalTargetControl);
	    	 		 	        	}
	    	 		 	        	if(retvalTargetControl > 0 || retval4Association > 0 )
	    							{
	    								if (_log.isDebugEnabled()){
	    									_log.debug(methodName,"User Association is allowded into the target group as no such user into the control group of this profile");
	    								}
	    								
	    							} else if(retval4deAssociation <=1) {
	    								if (_log.isDebugEnabled()) _log.debug(methodName,"User de-association is not allowded from target group as one user still exists into the control group of this profile");
	    								String arr[] = {channelUserVO.getUserName(),paymentNumber};
	    				 	            String key="user.associatechanneluser.targetgroup.oneuserstillexistsintocontrolgroup";
	    				 	            new BTSLMessages(key,arr,"AddSuccess");

	    							}
	    	 		 	          }
	    	 	        	}
	    					
	    				}
	                    // insert data into channelusers table
	                    final int userChannelCount = channelUserDAO.addChannelUser(con, channelUserVO);

	                    if (userChannelCount <= 0) {
	                        con.rollback();
	                        _log.error(methodName, "Error: while Inserting User");

	                        final String arr1[] = { methodName };
	                        model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("error.general.processing", arr1));
	                		return false;
	                    }

	                    // this method insert the other infomation of the user
	                    this.addUserInfo(theForm,usersReportModelNew, con, userDAO,  channelUserVO);
	                    // commit after all the above transaction executed
	                    // successfully

	                    // tango change start by sanjeew Date 10/07/07
	                    if (PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
	                        paymentNumber = null;
	  
	                    }
	                    // tango change End
	                    con.commit();
	                    final String arr[] = { channelUserVO.getUserName(), "" };
	                    if (PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus()) || PretupsI.USER_STATUS_PREACTIVE.equals(channelUserVO.getStatus())) {
	                        // send a message to the user abt there activation
	                        if (locale == null) {
	                            locale = BTSLUtil.getBTSLLocale(request);
	                        }
	                        BTSLMessages btslPushMessage = null;

	                        if (TypesI.YES.equals(theForm.getCategoryVO().getWebInterfaceAllowed()) && TypesI.YES.equals(theForm.getCategoryVO().getSmsInterfaceAllowed())) {
	                            // send message for both login id and sms pin

	                            // changes by hitesh ghanghas
	                            if (!BTSLUtil.isNullString(channelUserVO.getLoginID()) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue()) {

	                                final String[] arrArray = { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil
	                                                .decryptText(channelUserVO.getPrimaryMsisdnPin()) };

	                                // for Zebra and Tango by Sanjeew date 11/07/07
	                                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()) {
	                                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
	                                        arrArray[2] = paymentNumber;
	                                    }
	                                }
	                                // End Zebra and Tango

	                                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
	                                final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
	                                pushMessage.push();

	                            }

	                            else {
	                                if (!BTSLUtil.isNullString(channelUserVO.getLoginID())) {
	                                    final String[] arrArray = { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil
	                                                    .decryptText(channelUserVO.getPrimaryMsisdnPin()) };

	                                    // for Zebra and Tango 
	                              
	                                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()) {
	                                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
	                                            arrArray[2] = paymentNumber;
	                                        }
	                                    }
	                                    // End Zebra and Tango

	                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
	                                    final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
	                                    pushMessage.push();
	                                } else {
	                                    final String[] arrArray = { channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPrimaryMsisdnPin()) };

	                                    // for Zebra and Tango 
	                                
	                                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()) {
	                                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
	                                            arrArray[2] = paymentNumber;
	                                        }
	                                    }
	                                    // End Zebra and Tango

	                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
	                                    final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
	                                    pushMessage.push();
	                                }
	                            }
	                          
	                            // Email for pin & password-mail push
	                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {

	                            	final String subject =PretupsRestUtil.getMessageString("user.addchanneluser.addsuccessmessage", arr);
	                            	
	                                final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
	                                                "Email has ben delivered recently", channelUserVO, sessionUserVO);
	                                emailSendToUser.sendMail();
	                            }
	                        } else if (TypesI.YES.equals(theForm.getCategoryVO().getWebInterfaceAllowed()) && TypesI.NO.equals(theForm.getCategoryVO().getSmsInterfaceAllowed()) && !BTSLUtil
	                                        .isNullString(channelUserVO.getLoginID()) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue()) {
	                            // send message for login id
	                       
	                            final String[] arrArray = { channelUserVO.getLoginID(), "", BTSLUtil.decryptText(channelUserVO.getPassword()) };
	                           
	                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()) {
	                                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
	                                    arrArray[1] = paymentNumber;
	                                }
	                            }
	                            // End Zebra and Tango

	                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arrArray);
	                            final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
	                            pushMessage.push();
	                            // Email for pin & password- code for email details
	                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
	                                
	                                final String subject =PretupsRestUtil.getMessageString("user.addchanneluser.addsuccessmessage", arr);
	                                
	                                final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
	                                                "Email will be delivered shortly", channelUserVO, sessionUserVO);
	                                emailSendToUser.sendMail();
	                            }
	                        } else {
	                            // send message for sms pin
	                       
	                            final String[] arrArray = { channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPrimaryMsisdnPin()) };
	                            // for Zebra and Tango by Sanjeew date 11/07/07
	                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()) {
	                                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
	                                    arrArray[1] = paymentNumber;
	                                    arr[1] = paymentNumber;
	                                }
	                            }
	                            // End Zebra and Tango

	                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
	                            final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
	                            pushMessage.push();
	                            // Email for pin & password- code for email details
	                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
	                                final String arrOne[] = { channelUserVO.getMsisdn() };
	                               
	                                final String subject =PretupsRestUtil.getMessageString("subject.user.regmsidn.massage", arrOne);
	                                final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
	                                                "Email will be delivered shortly", channelUserVO, sessionUserVO);
	                                emailSendToUser.sendMail();
	                            }
	                        }

	                        // pusing individual sms
	                        if (channelUserVO.getMsisdnList() != null && channelUserVO.getMsisdnList().size() > 1) {
	                            // it mean that it has secondary number and now push
	                            // message to individual secondary no

	                            final ArrayList<?> newMsisdnList = channelUserVO.getMsisdnList();
	                            UserPhoneVO newUserPhoneVO = null;
	                            // Email for pin & password
	                            String subject = null;
	                            EmailSendToUser emailSendToUser = null;
	                            final String tmpMsisdn = channelUserVO.getMsisdn();
	                            for (int i = 0, j = newMsisdnList.size(); i < j; i++) {
	                                btslPushMessage = null;
	                                newUserPhoneVO = (UserPhoneVO) newMsisdnList.get(i);
	                                if (TypesI.NO.equals(newUserPhoneVO.getPrimaryNumber())) {
	                                    final String[] arrArray = { newUserPhoneVO.getMsisdn(), "", BTSLUtil.decryptText(newUserPhoneVO.getSmsPin()) };
	                                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()) {
	                                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
	                                            arrArray[1] = paymentNumber;
	                                        }
	                                    }
	                                    locale = new Locale(newUserPhoneVO.getPhoneLanguage(), newUserPhoneVO.getCountry());
	                                    if (locale == null) {
	                                        locale = BTSLUtil.getBTSLLocale(request);
	                                    }
	                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
	                                    final PushMessage pushMessage = new PushMessage(newUserPhoneVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
	                                    pushMessage.push();
	                                    // Email for pin & password- code for email
	                                    // details
	                                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
	                                        //subject = this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "subject.user.regmsidn.massage",
	                                         subject =PretupsRestUtil.getMessageString("subject.user.regmsidn.massage", new String[] { newUserPhoneVO.getMsisdn() });
	                                        
	                                        channelUserVO.setMsisdn(newUserPhoneVO.getMsisdn());
	                                        emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
	                                                        "Email will be delivered shortly", channelUserVO, sessionUserVO);
	                                        emailSendToUser.sendMail();
	                                        channelUserVO.setMsisdn(tmpMsisdn);
	                                    }
	                                }
	                            }
	                        }

	                        new BTSLMessages("user.addchanneluser.addsuccessmessage", arr, "AddSuccess");

	                    } else {
	                        new BTSLMessages("user.addchanneluser.addsuccessmessageforrequest", arr, "AddSuccess");

	                    }
	                    final String decryptPassword = BTSLUtil.decryptText(channelUserVO.getPassword());
	                    channelUserVO.setPassword(decryptPassword);
	                    ChannelUserLog.log("ADDCHNLUSR", channelUserVO, sessionUserVO, true, null);
	                    
	                    //OTF Message function while adding
	                    if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS))).booleanValue() &&((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelUserVO.getNetworkID()) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,channelUserVO.getNetworkID()))){
	                    	if(((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,channelUserVO.getNetworkID(),theForm.getCategoryCode())).intValue()==0 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()){
	                    	TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
	                    	tbcm.loadCommissionProfileDetailsForOTFMessages(con,channelUserVO);
	                    	}
	                    }
	                    
	                    
	                } else// for edit/approval1/approval2
	                {
	                    if (userDAO.isUserLoginExist(con, theForm.getWebLoginID(), theForm.getUserId())) {//testing
	                        throw new BTSLBaseException(this, methodName, "user.addchanneluser.error.loginallreadyexist", "Detail");
	                    }
	                    // Unique check for External code(if given)
	                    if (!BTSLUtil.isNullString(theForm.getExternalCode())) {
	                        if (channelUserWebDAO.isExternalCodeExist(con, theForm.getExternalCode().trim(), theForm.getUserId())) {//testing
	                            throw new BTSLBaseException(this, methodName, "user.addchanneluser.error.externalallreadyexist", "Detail");
	                        }
	                    }
	                    channelUserVO.setNetworkID(sessionUserVO.getNetworkID());
	                    channelUserVO.setUserName(theForm.getChannelUserName());//testing
	                    channelUserVO.setUserID(theForm.getUserId());//testing
	                    channelUserVO.setLastModified(theForm.getLastModified());
	                    channelUserVO.setLoginID(theForm.getWebLoginID());
	                    channelUserVO.setLongitude(theForm.getLongitude());
	                    channelUserVO.setLatitude(theForm.getLatitude());
	                    channelUserVO.setDocumentType(theForm.getDocumentType()); 
	                    channelUserVO.setDocumentNo(theForm.getDocumentNo());
	                    channelUserVO.setPaymentType(theForm.getPaymentType());
	                    if ("edit".equals(theForm.getRequestType())) {
	                        boolean flag = CSRFTokenUtil.isValid(request);
	                        if (!flag) {
	                            if (_log.isDebugEnabled()) {
	                                _log.debug("CSRF", "ATTACK!");
	                            }
	                        }
	                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue() && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PWD_GENERATE_ALLOW))).booleanValue() && theForm.getPwdGenerateAllow().equals(PretupsI.YES)) {
	                            theForm.setShowPassword(operatorUtili.generateRandomPassword());
	                        }
	                        if (!BTSLUtil.isNullString(theForm.getWebPassword()) && (theForm.getShowPassword().equals(BTSLUtil.getDefaultPasswordText(BTSLUtil.decryptText(theForm
	                                        .getWebPassword()))))) {
	                            channelUserVO.setPassword(theForm.getWebPassword());
	                            if (theForm.getPasswordModifiedOn() != null) {
	                                channelUserVO.setPasswordModifiedOn(theForm.getPasswordModifiedOn());
	                            } else {
	                                channelUserVO.setPasswordModifiedOn(currentDate);
	                            }
	                            channelUserVO.setPasswordModifyFlag(false);
	                        } else {
	                            // Change Done 
	                            if (!"SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
	                                // check if Last 'X' password exist or not in
	                                // pin_password history table during
	                                // modification time
	                                boolean passwordExist = false;
	                                passwordExist = userDAO.checkPasswordHistory(con, PretupsI.USER_PASSWORD_MANAGEMENT, channelUserVO.getUserID(), channelUserVO.getMsisdn(),
	                                                BTSLUtil.encryptText(theForm.getShowPassword()));
	                                if (passwordExist) {
	                                    throw new BTSLBaseException(this, methodName, "user.modifypwd.error.newpasswordexistcheck", 0, new String[] { String
	                                                    .valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue()) }, "Detail");
	                                }
	                            }
	                            // while updating encrypt the password
	                            String password = null;
	                            if (!BTSLUtil.isNullString(theForm.getWebLoginID())) {
	                                password = BTSLUtil.encryptText(theForm.getShowPassword());
	                            }
	                            channelUserVO.setPassword(password);
	                            channelUserVO.setPasswordModifiedOn(currentDate);
	                            changePwdFlag = true;
	                            channelUserVO.setPasswordModifyFlag(true);
	                        }
	                    } else // this block starts for approval and no need to
	                           // check password history by santanu
	                    {
	                        // generate password for approval case
	                     
	                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue() && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PWD_GENERATE_ALLOW))).booleanValue() && theForm.getPwdGenerateAllow().equals(PretupsI.YES)) {
	                            theForm.setShowPassword(operatorUtili.generateRandomPassword());
	                        }
	                        if (!BTSLUtil.isNullString(theForm.getWebLoginID())) {
	                            if (!BTSLUtil.isNullString(theForm.getWebPassword()) && (theForm.getShowPassword().equals(BTSLUtil.getDefaultPasswordText(BTSLUtil
	                                            .decryptText(theForm.getWebPassword()))))) {
	                                channelUserVO.setPassword(theForm.getWebPassword());
	                                channelUserVO.setPasswordModifyFlag(false);
	                            } else {
	                                // while updating encrypt the password
	                                String passvalue = "";
	                                if (!BTSLUtil.isNullString(theForm.getWebLoginID())) {
	                                    passvalue = BTSLUtil.encryptText(theForm.getShowPassword());
	                                }
	                                channelUserVO.setPassword(passvalue);
	                                channelUserVO.setPasswordModifyFlag(true);
	                            }
	                        }
	                    }
	                    channelUserVO.setAllowedIps(theForm.getAllowedIPs());

	                    final StringBuilder  str = new StringBuilder ();
	                    /*
	                     * theForm.getAllowedDays returns an string array but in DB
	                     * we insert a single
	                     * string value of the allowed days like 1,4,7, for yhis
	                     * prupose convert the string
	                     * array into string
	                     */
	                    if (theForm.getAllowedDays() != null && theForm.getAllowedDays().length > 0) {
	                        str.append(theForm.getAllowedDays()[0]);
	                        for (int i = 1, j = theForm.getAllowedDays().length; i < j; i++) {
	                            str.append("," + theForm.getAllowedDays()[i]);
	                        }
	                    }

	                    channelUserVO.setAllowedDays(str.toString());
	                    channelUserVO.setFromTime(theForm.getAllowedFormTime());
	                    channelUserVO.setToTime(theForm.getAllowedToTime());
	                    channelUserVO.setEmpCode(theForm.getEmpCode());
	                    // this status value set in the detailView method of the
	                    // same class
	                    channelUserVO.setStatus(theForm.getStatus());
	                    channelUserVO.setPreviousStatus(theForm.getPreviousStatus());

	                    if ("edit".equals(theForm.getRequestType())) {
	                        channelUserVO.setLevel1ApprovedBy(theForm.getLevel1ApprovedBy());
	                        channelUserVO.setLevel1ApprovedOn(theForm.getLevel1ApprovedOn());
	                        channelUserVO.setLevel2ApprovedBy(theForm.getLevel2ApprovedBy());
	                        channelUserVO.setLevel2ApprovedOn(theForm.getLevel2ApprovedOn());
	                    } else if ("approval1".equals(theForm.getRequestType())) {
	                    	// Security CSRF starts here
	                        boolean flag = CSRFTokenUtil.isValid(request);
	                        if (!flag) {
	                            if (_log.isDebugEnabled()) {
	                                _log.debug("CSRF", "ATTACK!");
	                            }
	                           // throw new BTSLBaseException
	                        }
	                        // Security CSRF ends here

	                        // set the level1ApprovedBy the id of the user in
	                        // session
	                        channelUserVO.setLevel1ApprovedBy(sessionUserVO.getActiveUserID());
	                        channelUserVO.setLevel1ApprovedOn(currentDate);

	                        channelUserVO.setLevel2ApprovedBy(theForm.getLevel2ApprovedBy());
	                        channelUserVO.setLevel2ApprovedOn(theForm.getLevel2ApprovedOn());
	                    } else if ("stkApproval".equals(theForm.getRequestType())) {

	                        channelUserVO.setLevel1ApprovedBy(sessionUserVO.getActiveUserID());
	                        channelUserVO.setLevel1ApprovedOn(currentDate);

	                        } else// for level two approval
	                    {
	                    	// Security CSRF starts here
	                        boolean flag = CSRFTokenUtil.isValid(request);
	                        if (!flag) {
	                            if (_log.isDebugEnabled()) {
	                                _log.debug("CSRF", "ATTACK!");
	                            }
	                            throw new BTSLBaseException(this, methodName, "error.general.processing", "ApprovalUserDetailView");
	                        }
	                        // Security CSRF ends here
	                        channelUserVO.setLevel1ApprovedBy(theForm.getLevel1ApprovedBy());
	                        channelUserVO.setLevel1ApprovedOn(theForm.getLevel1ApprovedOn());

	                        // set the level2ApprovedBy the id of the user in
	                        // session
	                        channelUserVO.setLevel2ApprovedBy(sessionUserVO.getActiveUserID());
	                        channelUserVO.setLevel2ApprovedOn(currentDate);
	                    }

	                    channelUserVO.setEmail(theForm.getEmail());
	                 
	                    channelUserVO.setCompany(theForm.getCompany());
	                    channelUserVO.setFax(theForm.getFax());
	                    channelUserVO.setLanguage(theForm.getUserLanguage());
	                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED))).booleanValue()) {
	                        channelUserVO.setFirstName(theForm.getFirstName());
	                        channelUserVO.setLastName(theForm.getLastName());
	                        if (!BTSLUtil.isNullString(theForm.getLastName())) {
	                            channelUserVO.setUserName(theForm.getFirstName() + " " + theForm.getLastName());
	                        } else {
	                            channelUserVO.setUserName(theForm.getFirstName());
	                        }
	                    } else {
	                        channelUserVO.setUserName(theForm.getChannelUserName());
	                    }
	                   
	                    channelUserVO.setContactPerson(theForm.getContactPerson());
	                    channelUserVO.setContactNo(theForm.getContactNo());
	                    channelUserVO.setDesignation(theForm.getDesignation());

	                    final ArrayList<UserPhoneVO> phoneList = new ArrayList<>();
	                    /*
	                     * Here we prepare the phone list from the form, only add
	                     * those rows
	                     * where user entered the msisdn because msisdn list
	                     * contains
	                     * number of VOs but we have to insert only those where
	                     * msisdn != ""
	                     * 
	                     * 1) All the PhoneVOs in MSISDN list will have the same
	                     * UserId
	                     * that are inserted into the DB, so set this userId
	                     * into the List
	                     * 2)set the default vales
	                     * 3)Also set the primary number of the User from the msisdn
	                     * list
	                     */
	                    Locale locale = null;
	                    String oldUserPhoneID = null;
	                    if (theForm.getMsisdnList() != null) {
	                        UserPhoneVO phoneVO = null;
	                        UserPhoneVO oldPhoneVO = null;
	                        for (int i = 0, j = theForm.getMsisdnList().size(); i < j; i++) {
	                            oldUserPhoneID = null;
	                            phoneVO =  theForm.getMsisdnList().get(i);
	                            phoneVO.setPinModifyFlag(true);
	                            if (!BTSLUtil.isNullString(phoneVO.getMsisdn())) {
	                                oldUserPhoneID = phoneVO.getUserPhonesId();
	                                if (theForm.getOldMsisdnList() != null && !theForm.getOldMsisdnList().isEmpty()) {
	                                    for (int k = 0, l = theForm.getOldMsisdnList().size(); k < l; k++) {
	                                        oldPhoneVO =  theForm.getOldMsisdnList().get(k);
	                                        if (!BTSLUtil.isNullString(oldPhoneVO.getMsisdn()) && phoneVO.getMsisdn().equals(oldPhoneVO.getMsisdn())) {
	                                            // if pin change
	                                            if (!(phoneVO.getShowSmsPin().equals(BTSLUtil.getDefaultPasswordNumeric(BTSLUtil.decryptText(oldPhoneVO.getSmsPin()))))) {
	                                                phoneVO.setPinModifyFlag(true);
	                                            } else {
	                                                phoneVO.setPinModifyFlag(false);
	                                            }
	                                            break;
	                                        }
	                                    }
	                                }
	                                phoneVO.setMsisdn(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()));
	                                if (BTSLUtil.isNullString(oldUserPhoneID)) {
	                                    phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
	                                    phoneVO.setOperationType("I");
	                                    phoneVO.setIdGenerate(true);
	                                } else {
	                                    phoneVO.setUserPhonesId(oldUserPhoneID);
	                                    if (!phoneVO.isIdGenerate()) {
	                                        phoneVO.setOperationType("U");
	                                    }
	                                }
	                                phoneVO.setUserId(channelUserVO.getUserID());
	                                // set the default values
	                                phoneVO.setCreatedBy(sessionUserVO.getActiveUserID());
	                                phoneVO.setModifiedBy(sessionUserVO.getActiveUserID());
	                                phoneVO.setCreatedOn(currentDate);
	                                phoneVO.setModifiedOn(currentDate);
	                               
	                                if (!BTSLUtil.isNullString(channelUserVO.getLanguage())) {
	                                    final String lang_country[] = (channelUserVO.getLanguage()).split("_");
	                                    phoneVO.setPhoneLanguage(lang_country[0]);
	                                    phoneVO.setCountry(lang_country[1]);
	                                } else {
	                                    phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
	                                    phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
	                                }
	                                if (phoneVO.isPinModifyFlag() || phoneVO.getPinModifiedOn() == null) {
	                                    phoneVO.setPinModifiedOn(currentDate);
	                                }
	                                /*
	                                 * Msisdn List consist of old(coming form the
	                                 * DB) as well as new(add explicitly in the
	                                 * list) VOs
	                                 * set the default country and language only to
	                                 * the new VOs
	                                 */
	                           
	                   
	                                filterMsisdn = PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn());
	                                final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(filterMsisdn));
	                                phoneVO.setPrefixID(prefixVO.getPrefixID());
	                                if (TypesI.YES.equals(phoneVO.getPrimaryNumber())) {
	                                    channelUserVO.setMsisdn(phoneVO.getMsisdn());
	                                    channelUserVO.setPrimaryMsisdnPin(phoneVO.getSmsPin());// for
	                                    // push
	                                    // msg
	                                    // with
	                                    // pin
	                                    locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
	                                }

	                                /*
	                                 * Code Added for MNP
	                                 * Preference to check whether MNP is allowed in
	                                 * system or not.
	                                 * If yes then check whether Number has not been
	                                 * ported out, If yes then throw error, else
	                                 * continue
	                                 */
	                                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
	                                    boolean numberAllowed = false;
	                                    if (prefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
	                                        numberAllowed = new NumberPortDAO().isExists(con, filterMsisdn, "", PretupsI.PORTED_IN);
	                                        if (!numberAllowed) {
	                                            	                                       	                                                              
	                                            model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("user.assignphone.error.msisdnnotinsamenetwork", new String[] { filterMsisdn, prefixVO.getNetworkName() }));
	                                    		return false;
	                                        
	                                        }
	                                    } else {
	                                        numberAllowed = new NumberPortDAO().isExists(con, filterMsisdn, "", PretupsI.PORTED_OUT);
	                                        if (numberAllowed) {
	                                           	                                    
	                                        	model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("user.assignphone.error.msisdnnotinsamenetwork", new String[] { filterMsisdn, prefixVO.getNetworkName() }));
	                                    		return false;
	                                        }
	                                    }
	                                }
	                                //  MNP Code End
	                                if (userDAO.isMSISDNExist(con, PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()), phoneVO.getUserId())) {
	                                    final String[] arr = { phoneVO.getMsisdn() };
	                                    _log.error(methodName, "Error: MSISDN Number is already assigned to another user");
	                                 
	                                    model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("user.assignphone.error.msisdnallreadyexist", arr));
                                		return false;
	                                
	                                }

	                                // check if pin exist in password history table
	                                // when change
	            
	                                if ((!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PIN_GENERATE_ALLOW))).booleanValue()) && phoneVO.isPinModifyFlag() && ("edit".equals(theForm.getRequestType()))) {

	                                    if (userDAO.checkPasswordHistory(con, PretupsI.USER_PIN_MANAGEMENT, phoneVO.getUserId(), PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()),
	                                                    BTSLUtil.encryptText(phoneVO.getShowSmsPin()))) {
	                                        _log.error(methodName, "Error: Pin exist in password_history table");
	                                        	                                        	                                      
	                                        model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("channeluser.changepin.error.pinhistory", new String[] { String
                                                     .valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue()), phoneVO.getMsisdn() }));
	                                		return false;
	                                    }
	                                }
	                                phoneList.add(phoneVO);
	                            } else if (!BTSLUtil.isNullString(phoneVO.getUserPhonesId())) {
	                                if (!phoneVO.isIdGenerate()) {
	                                    phoneVO.setOperationType("D");
	                                }
	                                phoneList.add(phoneVO);
	                            }
	                        }
	                    }
	                    channelUserVO.setMsisdnList(phoneList);

	                    channelUserVO.setInSuspend(theForm.getInsuspend());
	                    channelUserVO.setOutSuspened(theForm.getOutsuspend());
	                    channelUserVO.setModifiedBy(sessionUserVO.getActiveUserID());
	                    channelUserVO.setModifiedOn(currentDate);
	                    channelUserVO.setAddress1(theForm.getAddress1());
	                    channelUserVO.setAddress2(theForm.getAddress2());
	                    channelUserVO.setCity(theForm.getCity());
	                    channelUserVO.setState(theForm.getState());
	                    channelUserVO.setCountry(theForm.getCountry());
	                    channelUserVO.setRsaFlag(theForm.getRsaAuthentication());
	                    channelUserVO.setSsn(theForm.getSsn());
	                    channelUserVO.setUserNamePrefix(theForm.getUserNamePrefixCode());
	                    channelUserVO.setExternalCode(theForm.getExternalCode());
	                    // added for Authentication Type
	                    channelUserVO.setAuthTypeAllowed(theForm.getAuthTypeAllowed());
	                    // for Zebra and Tango 2 
	                   
	                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
	                        channelUserVO.setLmsProfile(theForm.getLmsProfileId());
	                        
	                    }
	                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()) {
	                        if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(theForm.getMcommerceServiceAllow())) {
	                            if (!BTSLUtil.isNullString(theForm.getMpayProfileIDWithGrad()) && theForm.getMpayProfileIDWithGrad().contains(":")) {
	                                channelUserVO.setMpayProfileID(theForm.getMpayProfileIDWithGrad().split(":")[1]);
	                            }
	                        } else {
	                            channelUserVO.setMpayProfileID("");
	                        }
	                        channelUserVO.setMcommerceServiceAllow(theForm.getMcommerceServiceAllow());
	                    } else {
	                        channelUserVO.setMpayProfileID("");
	                        channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
	                    }
	                    channelUserVO.setUserProfileID(channelUserVO.getUserID());
	                    // Modified  for alerts
	                    channelUserVO.setAlertEmail(theForm.getOtherEmail());
	                    if (TypesI.YES.equals(theForm.getCategoryVO().getLowBalAlertAllow())) {
	                        final String delimiter = ";";
	                        final String allowforself = theForm.getLowBalAlertToSelf();
	                        final String allowforparent = theForm.getLowBalAlertToParent();
	                        final String allowforOther = theForm.getLowBalAlertToOther();
	                        final StringBuilder  alerttype = new StringBuilder ("");

	                        if (TypesI.YES.equals(allowforself)) {
	                            alerttype.append(PretupsI.ALERT_TYPE_SELF);
	                        }
	                        if (TypesI.YES.equals(allowforparent)) {
	                            alerttype.append("".equals(alerttype.toString()) ? "" : delimiter);
	                            alerttype.append(PretupsI.ALERT_TYPE_PARENT);
	                        }
	                        if (TypesI.YES.equals(allowforOther)) {
	                            alerttype.append("".equals(alerttype.toString()) ? "" : delimiter);
	                            alerttype.append(PretupsI.ALERT_TYPE_OTHER);
	                        }
	                        if (!"".equals(alerttype.toString())) {
	                            channelUserVO.setLowBalAlertAllow(TypesI.YES);
	                            channelUserVO.setAlertType(alerttype.toString());
	                        } else {
	                            channelUserVO.setLowBalAlertAllow("N");
	                            // Modification ends
	                        }
	                    } else {
	                        channelUserVO.setLowBalAlertAllow("N");
	                    }
	                    // end Zebra and Tango
	                    /*
	                     * User Code value is dependent on the
	                     * System Preferences values USER_CODE_REQUIRED
	                     * if it true user can enter the user code value from the
	                     * screen
	                     * if it is false pass the primary msisdn number as user
	                     * code value
	                     * 
	                     * userCodeFlag is set on the form in the addModify method
	                     * of the channelUserAction class
	                     * on the bais of this value we set the userCode value into
	                     * the VO
	                     * 
	                     * msisdn field contain the primary msisdn number of the
	                     * user
	                     */
	                    if (theForm.isUserCodeFlag()) {
	                        channelUserVO.setUserCode(theForm.getUserCode());
	                    } else {
	                        channelUserVO.setUserCode(channelUserVO.getMsisdn());
	                    }

	                    channelUserVO.setShortName(theForm.getShortName());
	                    if (!BTSLUtil.isNullString(theForm.getAppointmentDate())) {
	                        channelUserVO.setAppointmentDate(BTSLUtil.getDateFromDateString(theForm.getAppointmentDate()));
	                    }

	                    channelUserVO.setOutletCode(theForm.getOutletCode());
	                    /*
	                     * SuboutletCode is the combination of sublookup_code and
	                     * lookup_code
	                     * so here we split the code first then set
	                     */
	                    if (!BTSLUtil.isNullString(theForm.getSubOutletCode())) {
	                        final String[] sublookupID = theForm.getSubOutletCode().split(":");
	                        channelUserVO.setSubOutletCode(sublookupID[0]);
	                    }

	                    // update the user info
	                    final int updateCount = userDAO.updateUser(con, channelUserVO);

	                    if (updateCount <= 0) {
	                        con.rollback();
	                        _log.error(methodName, "Error: while Updating User");
	                        throw new BTSLBaseException(this, methodName, "error.general.processing");
	                    }

	                    // update the channel user info
	                    if ("edit".equals(theForm.getRequestType())) {
	                        int updateChannelCount = 0;
	                        /*
	                         * Here we check whether the approval is required or not
	                         * if approval is required
	                         * than update the approval info also
	                         * 
	                         * If USER_APPROVAL_LEVEL = 0 no approval required, if
	                         * USER_APPROVAL_LEVEL = 1 level 1 approval required,
	                         * if USER_APPROVAL_LEVEL = 2 level 2 approval required'
	                         * While adding/editing user check whether the approval
	                         * is required or not

	                         * update the information of the
	                         * commission,grade,transferprofile
	                         */
	                        if (((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, sessionUserVO.getNetworkID(), theForm.getCategoryCode()))
	                                        .intValue() == 0 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()) {
	                            channelUserVO.setUserGrade(theForm.getUserGradeId());
	                            channelUserVO.setTransferProfileID(theForm.getTrannferProfileId());
	                            channelUserVO.setCommissionProfileSetID(theForm.getProfile().split(":")[0]);
	                            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
	                                channelUserVO.setLmsProfile(theForm.getLmsProfileId());
	                            }
	                         
	                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue()) {
	                                channelUserVO.setTrannferRuleTypeId(theForm.getTrannferRuleTypeId());
	                            }
	                            updateChannelCount = channelUserWebDAO.updateChannelUserApprovalInfo(con, channelUserVO);

	                            if (updateChannelCount <= 0) {
	                                con.rollback();
	                                _log.error(methodName, "Error: while Updating Channel User For Approval One");
	                       
	                                final String arr1[] = { methodName };                  
	                                model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("error.general.processing", arr1));
	                        		return false;
	                            }
	                        } else {
	                        	channelUserVO.setUserGrade(theForm.getUserGradeId());
	                            updateChannelCount = channelUserDAO.updateChannelUserInfo(con, channelUserVO);

	                            if (updateChannelCount <= 0) {
	                                con.rollback();
	                                _log.error(methodName, "Error: while Updating Channel User");
	                           
	                                final String arr1[] = { methodName };                  
	                                model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("error.general.processing", arr1));
	                        		return false;
	                            }
	                        }

	                    } else if ("approval1".equals(theForm.getRequestType()))// level
	                    // one
	                    // approval
	                    {
	                        channelUserVO.setUserGrade(theForm.getUserGradeId());
	                        channelUserVO.setTransferProfileID(theForm.getTrannferProfileId());
	                        channelUserVO.setCommissionProfileSetID(theForm.getProfile().split(":")[0]);
	                        channelUserVO.setControlGroup(theForm.getControlGroup());
	                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {    
	                            channelUserVO.setLmsProfile(theForm.getLmsProfileId());
	                        }
	                     
	                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue()) {
	                            channelUserVO.setTrannferRuleTypeId(theForm.getTrannferRuleTypeId());
	                        }
	                        int updateChannelCount = channelUserWebDAO.updateChannelUserApprovalInfo(con, channelUserVO);

	                        if (updateChannelCount <= 0) {
	                            con.rollback();
	                            _log.error(methodName, "Error: while Updating Channel User For Approval One");
	                            final String arr1[] = { methodName };                  
                                model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("error.general.processing", arr1));
                        		return false;
	                        
	                        }

	                        /*
	                         * If user status is Y(active) means user is activaed at
	                         * level 1 approval,
	                         * so we are setting the activated_on = currentDate.
	                         * This indicate user is actived
	                         */
	                        if (PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
	                            channelUserVO.setActivatedOn(currentDate);

	                            updateChannelCount = channelUserWebDAO.updateChannelUserActivatedOn(con, channelUserVO);

	                            if (updateChannelCount <= 0) {
	                                con.rollback();
	                                _log.error(methodName, "Error: while Updating Channel User For Activated On");
	                                final String arr1[] = { methodName };                  
	                                model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("error.general.processing", arr1));
	                        		return false;
	                            }
	                        }
	                    } else if ("approval2".equals(theForm.getRequestType()))// level
	                    // one
	                    // approval
	                    {
	                        channelUserVO.setUserGrade(theForm.getUserGradeId());
	                        channelUserVO.setTransferProfileID(theForm.getTrannferProfileId());
	                        channelUserVO.setCommissionProfileSetID(theForm.getProfile().split(":")[0]);
	                        channelUserVO.setControlGroup(theForm.getControlGroup());
	                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
	                            channelUserVO.setLmsProfile(theForm.getLmsProfileId());
	                        }
	                        // added by gaurav
	                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue()) {
	                            channelUserVO.setTrannferRuleTypeId(theForm.getTrannferRuleTypeId());
	                        }
	                        int updateChannelCount = channelUserWebDAO.updateChannelUserApprovalInfo(con, channelUserVO);

	                        if (updateChannelCount <= 0) {
	                            con.rollback();
	                            _log.error(methodName, "Error: while Updating Channel User For Approval Two");
	                            final String arr1[] = { methodName };                  
                                model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("error.general.processing", arr1));
                        		return false;
	                        }

	                        /*
	                         * If user status is Y(active) means user is activaed at
	                         * level 2 approval,
	                         * so we are setting the activated_on = currentDate.
	                         * This indicate user is actived
	                         */
	                        if (PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
	                            channelUserVO.setActivatedOn(currentDate);

	                            updateChannelCount = channelUserWebDAO.updateChannelUserActivatedOn(con, channelUserVO);

	                            if (updateChannelCount <= 0) {
	                                con.rollback();
	                                _log.error(methodName, "Error: while Updating Channel User For Activated On");
	                                final String arr1[] = { methodName };                  
	                                model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("error.general.processing", arr1));
	                        		return false;
	                            }
	                        }
	                    } else if ("stkApproval".equals(theForm.getRequestType()))// level
	                    // one
	                    // approval
	                    {
	                        channelUserVO.setUserGrade(theForm.getUserGradeId());
	                        channelUserVO.setTransferProfileID(theForm.getTrannferProfileId());
	                        channelUserVO.setCommissionProfileSetID(theForm.getProfile().split(":")[0]);
	                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
	                            channelUserVO.setLmsProfile(theForm.getLmsProfileId());
	                        }
	                        int updateChannelCount = channelUserWebDAO.updateChannelUserApprovalInfo(con, channelUserVO);

	                        if (updateChannelCount <= 0) {
	                            con.rollback();
	                            _log.error(methodName, "Error: while Updating Channel User For Approval One");
	                            final String arr1[] = { methodName };                  
                                model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("error.general.processing", arr1));
                        		return false;
	                        }

	                        /*
	                         * If user status is Y(active) means user is activaed at
	                         * level 1 approval,
	                         * so we are setting the activated_on = currentDate.
	                         * This indicate user is actived
	                         */
	                        if (PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
	                            channelUserVO.setActivatedOn(currentDate);

	                            updateChannelCount = channelUserWebDAO.updateChannelUserActivatedOn(con, channelUserVO);

	                            if (updateChannelCount <= 0) {
	                                con.rollback();
	                                _log.error(methodName, "Error: while Updating Channel User For Activated On");
	                                final String arr1[] = { methodName };                  
	                                model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("error.general.processing", arr1));
	                        		return false;
	                            }
	                        }
	                    }

	                    /*
	                     * This will delete the information from the following
	                     * Tables
	                     * user_phones,user_geographies,user_roles,user_domains,
	                     * user_services,user_product_types
	                     */
	                 //   userwebDAO.deleteUserInfo(con, channelUserVO.getUserID()); testing

	                    /*
	                     * This will insert the information into the following
	                     * Tables
	                     * user_phones,user_geographies,user_roles,user_domains,
	                     * user_services,user_products
	                     */
	                    //this.addUserInfo(theForm,usersReportModelNew, con, userDAO, userwebDAO, channelUserVO, currentDate);testing

	                    // tango change start 3 by sanjeew Date 07/07/07
	      
	                    // tango change End

	                    // commit after all the above transaction executed
	                    // successfully
	                    con.commit();

	                    final String arr[] = { channelUserVO.getUserName(), paymentNumber };

	                    if ("edit".equals(theForm.getRequestType())) {
	                        new BTSLMessages("user.addchanneluser.updatesuccessmessage", arr, "EditSuccess");

	                        // change  for SMS sending
	                        final List<?> oldMsisdnList = theForm.getOldMsisdnList();
	                        final List<?> newMsisdnList = channelUserVO.getMsisdnList();
	                        UserPhoneVO oldUserPhoneVO = null;
	                        UserPhoneVO newUserPhoneVO = null;
	                        String primaryNoPin = null;
	                        BTSLMessages sendbtslMessage = null;
	                        boolean primaryNoPinFlag = false;
	                        Locale localeMsisdn = null;
	                        PushMessage pushMessage = null;
	                        // Email for pin & password
	                        String subject = null;
	                        EmailSendToUser emailSendToUser = null;
	                        final String tmpMsisdn = channelUserVO.getMsisdn();
	                        

	                        if (oldMsisdnList != null) {
	                            for (int i = 0, j = oldMsisdnList.size(); i < j; i++) {
	                                sendbtslMessage = null;
	                                oldUserPhoneVO = (UserPhoneVO) oldMsisdnList.get(i);
	                                for (int k = 0, l = newMsisdnList.size(); k < l; k++) {
	                                    newUserPhoneVO = (UserPhoneVO) newMsisdnList.get(k);
	                                    if (!BTSLUtil.isNullString(newUserPhoneVO.getMsisdn()) && newUserPhoneVO.getMsisdn().equals(oldUserPhoneVO.getMsisdn())) {
	                                        if (TypesI.YES.equals(newUserPhoneVO.getPrimaryNumber())) {
	                                            if (!(newUserPhoneVO.getShowSmsPin().equals(BTSLUtil.getDefaultPasswordNumeric(BTSLUtil.decryptText(oldUserPhoneVO.getSmsPin()))))) {// primary
	                                                // no
	                                                // pin
	                                                // change
	                                                primaryNoPin = newUserPhoneVO.getShowSmsPin();// primary
	                                                // no
	                                                // pin
	                                                // change
	                                                primaryNoPinFlag = true;
	                                            }
	                                        } else if (!(newUserPhoneVO.getShowSmsPin().equals(BTSLUtil
	                                                        .getDefaultPasswordNumeric(BTSLUtil.decryptText(oldUserPhoneVO.getSmsPin()))))) {// if
	                                            // pin
	                                            // change
	                                            localeMsisdn = new Locale(newUserPhoneVO.getPhoneLanguage(), newUserPhoneVO.getCountry());

	                                            // for Zebra and Tango 
	                                       
	                                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue() && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() && PretupsI.SELECT_CHECKBOX
	                                                            .equals(channelUserVO.getMcommerceServiceAllow())) {
	                                                sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY,
	                                                                new String[] { newUserPhoneVO.getShowSmsPin(), paymentNumber });
	                                            } else {
	                                                sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY, new String[] { newUserPhoneVO.getShowSmsPin() });
	                                            }
	                                           pushMessage = new PushMessage(newUserPhoneVO.getMsisdn(), sendbtslMessage, "", "", localeMsisdn, channelUserVO.getNetworkID(),
	                                                            "SMS will be delivered shortly thankyou");
	                                            pushMessage.push();
	                                            // Email for pin & password- email
	                                            // send
	                                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
	                                                channelUserVO.setMsisdn(newUserPhoneVO.getMsisdn());
	                                                
	                                                 subject =PretupsRestUtil.getMessageString("subject.user.regmsidn.massage.modify", new String[] { newUserPhoneVO.getMsisdn() });
	                                                emailSendToUser = new EmailSendToUser(subject, sendbtslMessage, locale, channelUserVO.getNetworkID(),
	                                                                "Email will be delivered shortly", channelUserVO, sessionUserVO);
	                                                emailSendToUser.sendMail();
	                                                channelUserVO.setMsisdn(tmpMsisdn);
	                                            }
	                                        }
	                                        
	                                    }

	                                }
	                                 
	                            }
	                             
	                        }
	                        final String msg[] = new String[3];
	                        sendbtslMessage = null;
	                        if (!BTSLUtil.isNullString(theForm.getWebLoginID()) && !BTSLUtil.isNullString(theForm.getOldWebLoginID())) {
	                            // only web password change
	                            if (changePwdFlag && !primaryNoPinFlag && theForm.getWebLoginID().equals(theForm.getOldWebLoginID())) {
	                                sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PWD_MODIFY, new String[] { theForm.getShowPassword() });
	                            } else if (changePwdFlag && primaryNoPinFlag && theForm.getWebLoginID().equals(theForm.getOldWebLoginID())) {
	                                msg[0] = theForm.getShowPassword();
	                                msg[1] = primaryNoPin;
	                                sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PWD_AND_PIN_MODIFY, msg);
	                            }
	                            // web loginid and web password and primary no pin
	                            // change
	                            else if (changePwdFlag && primaryNoPinFlag && !theForm.getWebLoginID().equals(theForm.getOldWebLoginID())) {
	                                msg[0] = theForm.getWebLoginID();
	                                msg[1] = theForm.getShowPassword();
	                                msg[2] = primaryNoPin;
	                                sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_AND_PIN_MODIFY, msg);
	                            }
	                            // only login id change
	                            else if (!changePwdFlag && !primaryNoPinFlag && !theForm.getWebLoginID().equals(theForm.getOldWebLoginID())) {
	                                msg[0] = theForm.getWebLoginID();
	                                sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_MODIFY, msg);
	                            }
	                            // only login id and web password change
	                            else if (changePwdFlag && !primaryNoPinFlag && !theForm.getWebLoginID().equals(theForm.getOldWebLoginID())) {
	                                msg[0] = theForm.getWebLoginID();
	                                msg[1] = theForm.getShowPassword();
	                                sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_MODIFY, msg);
	                            }
	                            // only login id and pin change
	                            else if (!changePwdFlag && primaryNoPinFlag && !theForm.getWebLoginID().equals(theForm.getOldWebLoginID())) {
	                                msg[0] = theForm.getWebLoginID();
	                                msg[1] = primaryNoPin;
	                                sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PIN_MODIFY, msg);
	                            }
	                            // only primary no pin change.
	                            else if (!changePwdFlag && primaryNoPinFlag && theForm.getWebLoginID().equals(theForm.getOldWebLoginID())) {
	                                msg[0] = primaryNoPin;
	                                sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY, msg);
	                            }
	                        }
	                         	                         
	                        else {
	                            if (primaryNoPinFlag) {
	                                sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY, new String[] { primaryNoPin });
	                            }
	                        }
	                        // Send SMS
	                        if (sendbtslMessage != null) {
	                   
	                            pushMessage = new PushMessage(channelUserVO.getMsisdn(), sendbtslMessage, "", "", locale, channelUserVO.getNetworkID(),
	                                            "SMS will be delivered shortly thanks");
	                            pushMessage.push();
	                            // Email for pin & password    
	                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
	                               
	                                 subject =PretupsRestUtil.getMessageString("user.addchanneluser.updatesuccessmessage", arr);
	                                emailSendToUser = new EmailSendToUser(subject, sendbtslMessage, locale, channelUserVO.getNetworkID(), "Email will be delivered shortly",
	                                                channelUserVO, sessionUserVO);
	                                emailSendToUser.sendMail();
	                            }
	                        }
	                        ChannelUserLog.log("MODCHNLUSR", channelUserVO, sessionUserVO, true, null);
	                        
	                        
	                        //OTF Message function while updating
	                        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS))).booleanValue() &&(((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelUserVO.getNetworkID()) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,channelUserVO.getNetworkID())) && (((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,channelUserVO.getNetworkID(),theForm.getCategoryCode())).intValue()==0 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue())  )){
	                        	TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
	                        	tbcm.loadCommissionProfileDetailsForOTFMessages(con,channelUserVO);
	                        }
	                        
	                        
	                        
	                    } else if ("approval1".equals(theForm.getRequestType()))// level
	                    // one
	                    // approval
	                    {
	                        if (PretupsI.USER_STATUS_CANCELED.equals(theForm.getStatus()))// reject
	                        // the
	                        // request
	                        {
	                            new BTSLMessages("user.addchanneluser.level1rejectmessage", arr, "ApprovalOneSuccess");

	                        } else// approve the request
	                        {
	                            if (PretupsI.USER_STATUS_APPROVED.equals(channelUserVO.getStatus())) {
	                                new BTSLMessages("user.addchanneluser.level1approvemessagerequiredleveltwoapproval", arr,
	                                                "ApprovalOneSuccess");

	                            } else {
	                                // send a message to the user abt their
	                                // activation
	                                if (locale == null) {
	                                    locale = BTSLUtil.getBTSLLocale(request);
	                                }
	                                BTSLMessages btslPushMessage = null;

	                                if (TypesI.YES.equals(theForm.getCategoryVO().getWebInterfaceAllowed()) && TypesI.YES.equals(theForm.getCategoryVO().getSmsInterfaceAllowed())) {
	                                    // send message for both login id and sms
	                                    // pin
	                 
	                                  
	                                    if (!BTSLUtil.isNullString(channelUserVO.getLoginID()))
	                                    
	                                    {
	                                        final String[] arrArray = { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO
	                                                        .getPassword()), BTSLUtil.decryptText(channelUserVO.getPrimaryMsisdnPin()) };
	                                        btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
	                                        final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO
	                                                        .getNetworkID());
	                                        pushMessage.push();
	                                        // Email for pin & password- code for
	                                        // email details
	                                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
	                                            
	                                           
	                                            final String subject =PretupsRestUtil.getMessageString("user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval", arr);
	                                            final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
	                                                            "Email will be delivered shortly", channelUserVO, sessionUserVO);
	                                            emailSendToUser.sendMail();
	                                        }
	                                    } else {
	                                        if (!BTSLUtil.isNullString(channelUserVO.getLoginID())) {
	                                            final String[] arrArray = { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO
	                                                            .getPassword()), BTSLUtil.decryptText(channelUserVO.getPrimaryMsisdnPin()) };

	                                            // for Zebra and Tango 
	                                           
	                                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()) {
	                                                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
	                                                    arrArray[2] = paymentNumber;
	                                                }
	                                            }
	                                            // End Zebra and Tango

	                                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
	                                            final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO
	                                                            .getNetworkID());
	                                            pushMessage.push();
	                                        } else {
	                                            final String[] arrArray = { channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPrimaryMsisdnPin()) };

	                                            // for Zebra and Tango 
	                                           
	                                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()) {
	                                                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
	                                                    arrArray[2] = paymentNumber;
	                                                }
	                                            }
	                                            // End Zebra and Tango

	                                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
	                                            final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO
	                                                            .getNetworkID());
	                                            pushMessage.push();
	                                        }
	                                    } 
	                                } else if (TypesI.YES.equals(theForm.getCategoryVO().getWebInterfaceAllowed()) && TypesI.NO.equals(theForm.getCategoryVO()
	                                                .getSmsInterfaceAllowed()) && !BTSLUtil.isNullString(channelUserVO.getLoginID()))
	                                // commented for sending email &&
	                                // ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue())
	                                {
	                                    // send message for login id
	                            
	                                    final String[] arrArray = { channelUserVO.getLoginID(), "", BTSLUtil.decryptText(channelUserVO.getPassword()) };
	                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arrArray);
	                                    final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
	                                    pushMessage.push();
	                                    // Email for pin & password- code for email
	                                    // details
	                                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
	                                        
	                                        final String subject =PretupsRestUtil.getMessageString("user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval", arr);
	                                        final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
	                                                        "Email will be delivered shortly", channelUserVO, sessionUserVO);
	                                        emailSendToUser.sendMail();
	                                    }
	                                } else {
	                                    // send message for sms pin
	                                 
	                                    final String[] arrArray = { channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPrimaryMsisdnPin()) };
	                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
	                                    final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
	                                    pushMessage.push();
	                                    // Email for pin & password
	                                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
	                                        channelUserVO.getMsisdn();
	                                        
	                                        final String subject =PretupsRestUtil.getMessageString("subject.user.regmsidn.massage", arr);
	                                        final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
	                                                        "Email will be delivered shortly", channelUserVO, sessionUserVO);
	                                        emailSendToUser.sendMail();
	                                    }
	                                }

	                                // pusing message to individual msisdn
	                                if (channelUserVO.getMsisdnList() != null && channelUserVO.getMsisdnList().size() > 1) {
	                                    // it mean that it has secondary number and
	                                    // now push message to individual secondary
	                                    // no

	                                    final ArrayList<?> newMsisdnList = channelUserVO.getMsisdnList();
	                                    UserPhoneVO newUserPhoneVO = null;
	                                    // Email for pin & password
	                                    EmailSendToUser emailSendToUser = null;
	                                    String subject = null;
	                                    final String tmpMsisdn = channelUserVO.getMsisdn();
	                                    
	                                    for (int i = 0, j = newMsisdnList.size(); i < j; i++) {
	                                        btslPushMessage = null;
	                                        newUserPhoneVO = (UserPhoneVO) newMsisdnList.get(i);
	                                        if (TypesI.NO.equals(newUserPhoneVO.getPrimaryNumber())) {
	                                            final String[] arrArray = { newUserPhoneVO.getMsisdn(), "", BTSLUtil.decryptText(newUserPhoneVO.getSmsPin()) };
	                                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()) {
	                                                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
	                                                    arrArray[1] = paymentNumber;
	                                                }
	                                            }
	                                            locale = new Locale(newUserPhoneVO.getPhoneLanguage(), newUserPhoneVO.getCountry());
	                                            if (locale == null) {
	                                                locale = BTSLUtil.getBTSLLocale(request);
	                                            }
	                                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
	                                            final PushMessage pushMessage = new PushMessage(newUserPhoneVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO
	                                                            .getNetworkID());
	                                            pushMessage.push();
	                                            // Email for pin & password
	                                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
	                                                
	                                                
	                                                subject =PretupsRestUtil.getMessageString("subject.user.regmsidn.massage", arr);
	                                                channelUserVO.setMsisdn(newUserPhoneVO.getMsisdn());
	                                                emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
	                                                                "Email will be delivered shortly", channelUserVO, sessionUserVO);
	                                                emailSendToUser.sendMail();
	                                                channelUserVO.setMsisdn(tmpMsisdn);
	                                            }
	                                        }
	                                    }
	                                }

	                                new BTSLMessages("user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval", arr,
	                                                "ApprovalOneSuccess");
	                                
	                            }
	                        }
	                        ChannelUserLog.log("APP1CHNLUSR", channelUserVO, sessionUserVO, true, null);
	                        
	                        //OTF Message function  at approval 1
	                        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS))).booleanValue() &&(((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelUserVO.getNetworkID()) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,channelUserVO.getNetworkID())) && (((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,channelUserVO.getNetworkID(),theForm.getCategoryCode())).intValue()==1 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()))){
	                        	TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
	                        	tbcm.loadCommissionProfileDetailsForOTFMessages(con,channelUserVO);
	                        }

	                    } else if ("approval2".equals(theForm.getRequestType()))// level
	                    // two
	                    // approval
	                    {
	                        if (PretupsI.USER_STATUS_CANCELED.equals(theForm.getStatus()))// reject
	                        // the
	                        // request
	                        {
	                            new BTSLMessages("user.addchanneluser.level2rejectmessage", arr, "ApprovalTwoSuccess");
	                            
	                        } else// approve the request
	                        {
	                            // send a message to the user abt their activation
	                            if (locale == null) {
	                                locale = BTSLUtil.getBTSLLocale(request);
	                            }
	                            BTSLMessages btslPushMessage = null;

	                            if (TypesI.YES.equals(theForm.getCategoryVO().getWebInterfaceAllowed()) && TypesI.YES.equals(theForm.getCategoryVO().getSmsInterfaceAllowed())) {
	                                // send message for both login id and sms pin
	                              
	                                // added by hitesh ghanghas
	                                if (!BTSLUtil.isNullString(channelUserVO.getLoginID()) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue()) {
	                                    final String[] arrArray = { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil
	                                                    .decryptText(channelUserVO.getPrimaryMsisdnPin()) };
	                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
	                                    final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
	                                    pushMessage.push();
	                                    // Email for pin & password- code for email
	                                    // details
	                                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
	                                        
	                                        final String subject =PretupsRestUtil.getMessageString("user.addchanneluser.level2approvemessage", arr);
	                                        
	                                        final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
	                                                        "Email will be delivered shortly", channelUserVO, sessionUserVO);
	                                        emailSendToUser.sendMail();
	                                    }
	                                } else {
	                                    final String[] arrArray = { channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPrimaryMsisdnPin()) };
	                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
	                                    final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID(),
	                                                    "SMS will be delivered shortly thanks");
	                                    pushMessage.push();
	                                    // code for email details
	                                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
	                                        
	                                        final String subject =PretupsRestUtil.getMessageString("user.addchanneluser.level2approvemessage", arr);
	                                        
	                                        final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
	                                                        "Email will be delivered shortly", channelUserVO, sessionUserVO);
	                                        emailSendToUser.sendMail();
	                                    }
	                                } // added by hitesh ghanghas
	                            } else if (TypesI.YES.equals(theForm.getCategoryVO().getWebInterfaceAllowed()) && TypesI.NO.equals(theForm.getCategoryVO()
	                                            .getSmsInterfaceAllowed()) && !BTSLUtil.isNullString(channelUserVO.getLoginID()) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue()) {
	                                // send message for login id
	                      
	                                final String[] arrArray = { channelUserVO.getLoginID(), "", BTSLUtil.decryptText(channelUserVO.getPassword()) };
	                                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arrArray);
	                                final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
	                                pushMessage.push();
	                                // Email- for pin & password- code for email
	                                // details
	                                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
	                                    
	                                   
	                                    final String subject =PretupsRestUtil.getMessageString("user.addchanneluser.level2approvemessage", arr);
	                                    final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
	                                                    "Email will be delivered shortly", channelUserVO, sessionUserVO);
	                                    emailSendToUser.sendMail();
	                                }
	                            } else {
	                                // send message for sms pin
	                     
	                                final String[] arrArray = { channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPrimaryMsisdnPin()) };
	                                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
	                                final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
	                                pushMessage.push();
	                                // Email for pin & password
	                                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
	                                    channelUserVO.getMsisdn();
	                                   
	                                    final String subject =PretupsRestUtil.getMessageString("subject.user.regmsidn.massage", arr);
	                                    final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
	                                                    "Email will be delivered shortly", channelUserVO, sessionUserVO);
	                                    emailSendToUser.sendMail();
	                                }
	                            }

	                            // pusing message individual
	                            if (channelUserVO.getMsisdnList() != null && channelUserVO.getMsisdnList().size() > 1) {
	                                // it mean that it has secondary number and now
	                                // push message to individual secondary MSISDN

	                                final ArrayList<?> newMsisdnList = channelUserVO.getMsisdnList();
	                                UserPhoneVO newUserPhoneVO = null;
	                                // Email for pin & password
	                                EmailSendToUser emailSendToUser = null;
	                                String subject = null;
	                                final String tmpMsisdn = channelUserVO.getMsisdn();
	                                
	                                for (int i = 0, j = newMsisdnList.size(); i < j; i++) {
	                                    btslPushMessage = null;
	                                    newUserPhoneVO = (UserPhoneVO) newMsisdnList.get(i);
	                                    if (TypesI.NO.equals(newUserPhoneVO.getPrimaryNumber())) {
	                                        final String[] arrArray = { newUserPhoneVO.getMsisdn(), "", BTSLUtil.decryptText(newUserPhoneVO.getSmsPin()) };
	                                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()) {
	                                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
	                                                arrArray[1] = paymentNumber;
	                                            }
	                                        }
	                                        locale = new Locale(newUserPhoneVO.getPhoneLanguage(), newUserPhoneVO.getCountry());
	                                        if (locale == null) {
	                                            locale = BTSLUtil.getBTSLLocale(request);
	                                        }
	                                        btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
	                                        final PushMessage pushMessage = new PushMessage(newUserPhoneVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO
	                                                        .getNetworkID());
	                                        pushMessage.push();
	                                        // Email for pin & password
	                                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
	                                            newUserPhoneVO.getMsisdn();
	                                           
	                                             subject =PretupsRestUtil.getMessageString("subject.user.regmsidn.massage", arr);
	                                            channelUserVO.setMsisdn(newUserPhoneVO.getMsisdn());
	                                            emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
	                                                            "Email will be delivered shortly", channelUserVO, sessionUserVO);
	                                            emailSendToUser.sendMail();
	                                            channelUserVO.setMsisdn(tmpMsisdn);
	                                        }
	                                    }
	                                }
	                            }

	                            new BTSLMessages("user.addchanneluser.level2approvemessage", arr, "ApprovalTwoSuccess");
	                        }
	                        ChannelUserLog.log("APP2CHNLUSR", channelUserVO, sessionUserVO, true, null);
	                        
	                        //OTF Message function at approval 2
	                        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS))).booleanValue() &&(((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelUserVO.getNetworkID()) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,channelUserVO.getNetworkID())) && (((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,channelUserVO.getNetworkID(),theForm.getCategoryCode())).intValue()==2 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()) ) ){
	                        	TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
	                        	tbcm.loadCommissionProfileDetailsForOTFMessages(con,channelUserVO);
	                        }
	                        
	                        
	                    } else if ("stkApproval".equals(theForm.getRequestType())) {

	                        if (PretupsI.USER_STATUS_CANCELED.equals(theForm.getStatus()))// reject
	                        // the
	                        // request
	                        {
	                            new BTSLMessages("user.addchanneluser.level1rejectmessage", arr, "ApprovalOneSuccess");

	                            // Push Message
	                            BTSLMessages btslPushMessage = null;
	                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_DEREGISTER);
	                            final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
	                            pushMessage.push();
	                        } else// approve the request
	                        {
	                            if (PretupsI.USER_STATUS_APPROVED.equals(channelUserVO.getStatus())) {
	                                new BTSLMessages("user.addchanneluser.level1approvemessagerequiredleveltwoapproval", arr,
	                                                "ApprovalOneSuccess");

	                            } else {
	                                // send a message to the user abt their
	                                // activation
	                                if (locale == null) {
	                                    locale = BTSLUtil.getBTSLLocale(request);
	                                }
	                                BTSLMessages btslPushMessage = null;

	                                if (TypesI.YES.equals(theForm.getCategoryVO().getWebInterfaceAllowed()) && TypesI.YES.equals(theForm.getCategoryVO().getSmsInterfaceAllowed())) {
	                                    // send message for both login id and sms
	                                    // pin
	                                 
	                                    final String[] arrArray = { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil
	                                                    .decryptText(channelUserVO.getPrimaryMsisdnPin()) };
	                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
	                                    final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
	                                    pushMessage.push();
	                                } else if (TypesI.YES.equals(theForm.getCategoryVO().getWebInterfaceAllowed()) && TypesI.NO.equals(theForm.getCategoryVO()
	                                                .getSmsInterfaceAllowed())) {
	                                    // send message for login id
	                                  
	                                    final String[] arrArray = { channelUserVO.getLoginID(), "", BTSLUtil.decryptText(channelUserVO.getPassword()) };
	                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arrArray);
	                                    final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
	                                    pushMessage.push();
	                                } else {
	                                    // send message for sms pin
	                                  
	                                    final String[] arrArray = { channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPrimaryMsisdnPin()) };
	                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
	                                    final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
	                                    pushMessage.push();
	                                }

	                                // pusing message to individual msisdn
	                                if (channelUserVO.getMsisdnList() != null && channelUserVO.getMsisdnList().size() > 1) {
	                                    // it mean that it has secondary number and
	                                    // now push message to individual secondary
	                                    // no

	                                    final ArrayList<?> newMsisdnList = channelUserVO.getMsisdnList();
	                                    UserPhoneVO newUserPhoneVO = null;
	                                    for (int i = 0, j = newMsisdnList.size(); i < j; i++) {
	                                        btslPushMessage = null;
	                                        newUserPhoneVO = (UserPhoneVO) newMsisdnList.get(i);
	                                        if (TypesI.NO.equals(newUserPhoneVO.getPrimaryNumber())) {
	                                            final String[] arrArray = { newUserPhoneVO.getMsisdn(), "", BTSLUtil.decryptText(newUserPhoneVO.getSmsPin()) };
	                                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()) {
	                                                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
	                                                    arrArray[1] = paymentNumber;
	                                                }
	                                            }
	                                            locale = new Locale(newUserPhoneVO.getPhoneLanguage(), newUserPhoneVO.getCountry());
	                                            if (locale == null) {
	                                                locale = BTSLUtil.getBTSLLocale(request);
	                                            }
	                                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
	                                            final PushMessage pushMessage = new PushMessage(newUserPhoneVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO
	                                                            .getNetworkID());
	                                            pushMessage.push();
	                                        }
	                                    }
	                                }

	                                new BTSLMessages("user.addchanneluser.stk.level1approvemessagenotrequiredleveltwoapproval", arr,
	                                                "ApprovalSTKSuccess");

	                            }
	                        }
	                        ChannelUserLog.log("APPSTKCHNLUSR", channelUserVO, sessionUserVO, true, null);
	                    }
	                }
	            } 

	        } catch (Exception e) {
	            _log.errorTrace(methodName, e);

	        } finally {
				if (mcomCon != null) {
					mcomCon.close("AddChannelUserServiceImpl#save");
					mcomCon = null;
				}
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting");
	            }
	        }

	        return true;   
	    }

	public void addUserInfo(UsersReportModel theForm,UsersReportModel usersReportModelNew, Connection p_con, UserDAO p_userDAO,  UserVO p_userVO) throws Exception {
        final String methodName = "addUserInfo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }


        // insert phone info
        if ("add".equals(theForm.getRequestType())) {
            // insert phone info
            if (p_userVO.getMsisdnList() != null && !p_userVO.getMsisdnList().isEmpty()) {
                final int phoneCount = p_userDAO.addUserPhoneList(p_con, p_userVO.getMsisdnList());
                if (phoneCount <= 0) {
                    try {
                        p_con.rollback();
                    } catch (SQLException e) {
                        _log.errorTrace(methodName, e);
                    }
                    _log.error(methodName, "Error: while Inserting User Phone Info");

                }
            }
        } else {
            if (p_userVO.getMsisdnList() != null && !p_userVO.getMsisdnList().isEmpty()) {
                final int phoneCount = p_userDAO.updateInsertDeleteUserPhoneList(p_con, p_userVO.getMsisdnList());
                if (phoneCount <= 0) {
                    try {
                        p_con.rollback();
                    } catch (SQLException e) {
                        _log.errorTrace(methodName, e);
                    }
                    _log.error(methodName, "Error: while Inserting User Phone Info");

                }
            }

        }

        /*
         * here we prepare the list of the geographies, from which it belongs
         * first we check the area from which it belongs then we check the
         * MultipleGeographicalArea flag
         */
        final ArrayList<UserGeographiesVO> geographyList = new ArrayList<>();
        UserGeographiesVO geoVO ;

        // if user belongs to multiple graph domains
        if (TypesI.YES.equals(usersReportModelNew.getCategoryVO().getMultipleGrphDomains())) {
            if (usersReportModelNew.getGeographicalCodeArray() != null && usersReportModelNew.getGeographicalCodeArray().length > 0) {
                for (int i = 0, j = usersReportModelNew.getGeographicalCodeArray().length; i < j; i++) {
                    geoVO = new UserGeographiesVO();
                    geoVO.setUserId(p_userVO.getUserID());
                    geoVO.setGraphDomainCode(usersReportModelNew.getGeographicalCodeArray()[i]);
                    geographyList.add(geoVO);
                }
            }
        } else// if user belongs to single zones
        {
            if (usersReportModelNew.getGeographicalCode() != null && usersReportModelNew.getGeographicalCode().trim().length() > 0) {
                geoVO = new UserGeographiesVO();
                geoVO.setUserId(p_userVO.getUserID());
                geoVO.setGraphDomainCode(usersReportModelNew.getGeographicalCode());
                geographyList.add(geoVO);
            }
        }
        // insert geography info
        if (geographyList != null && !geographyList.isEmpty()) {
            final UserGeographiesDAO userGeographiesDAO = new UserGeographiesDAO();
            final int geographyCount = userGeographiesDAO.addUserGeographyList(p_con, geographyList);

            if (geographyCount <= 0) {
                try {
                    p_con.rollback();
                } catch (SQLException e) {
                    _log.errorTrace(methodName, e);
                }
                _log.error(methodName, "Error: while Inserting User Geography Info");

            }
            p_userVO.setGeographicalAreaList(geographyList);
        }

        // insert roles info
        if (usersReportModelNew.getRoleFlag() != null && usersReportModelNew.getRoleFlag().length > 0) {
            final UserRolesDAO rolesDAO = new UserRolesDAO();
            final int roleCount = rolesDAO.addUserRolesList(p_con, p_userVO.getUserID(), usersReportModelNew.getRoleFlag());
            if (roleCount <= 0) {
                try {
                    p_con.rollback();
                } catch (SQLException e) {
                    _log.errorTrace(methodName, e);
                }
                _log.error(methodName, "Error: while Inserting User Roles Info");

            }
        }

        // insert services info
        if (usersReportModelNew.getServicesTypes() != null && usersReportModelNew.getServicesTypes().length > 0) {
            final ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
            final int servicesCount = servicesDAO.addUserServicesList(p_con, p_userVO.getUserID(), usersReportModelNew.getServicesTypes(), PretupsI.YES);
            if (servicesCount <= 0) {
                try {
                    p_con.rollback();
                } catch (SQLException e) {
                    _log.errorTrace(methodName, e);
                }
                _log.error(methodName, "Error: while Inserting User Services Info");

            }
        }


        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting");
        }
    }

		
}   
		
	

	

