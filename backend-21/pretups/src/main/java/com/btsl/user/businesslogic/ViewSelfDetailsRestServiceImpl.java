package com.btsl.user.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.pretups.domain.businesslogic.DomainWebDAO;
import com.web.pretups.master.businesslogic.DivisionDeptWebDAO;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;
import com.web.pretups.roles.businesslogic.UserRolesWebDAO;
import com.web.user.businesslogic.UserWebDAO;



public class ViewSelfDetailsRestServiceImpl implements ViewSelfDetailsRestService {

	public static final Log _log = LogFactory.getLog(ViewSelfDetailsRestServiceImpl.class.getName());
	private static final String CLASS_NAME = "ViewSelfDetailsRestServiceImpl";
	private PretupsResponse<ChannelUserVO> response;
	
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<ChannelUserVO> viewSelfDetails(String requestData)
			throws BTSLBaseException, IOException, SQLException,
			ValidatorException, SAXException {
		final String methodName = "#loadSelfDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(CLASS_NAME+methodName, "Entered");
        }
        
         Connection connection= null;MComConnectionI mcomCon = null;
         PretupsResponse<ChannelUserVO> response =null;
		 UserDAO userDAO = new UserDAO();
		 UserWebDAO userwebDAO = new UserWebDAO();
		 ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		 DivisionDeptWebDAO divisionwebDAO = new DivisionDeptWebDAO();
		
		 try{
			    mcomCon = new MComConnection();connection=mcomCon.getConnection();
				JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
				JsonNode data=dataObject.get("data");
				response = new PretupsResponse<>();
				if(data.get("loginId").textValue().isEmpty())
				{
					response.setFormError("viewSelfDetails.login.required");
					response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					return response;
				}
				PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
				UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(data, connection);
			    if(userVO == null)
			    {
			    	response.setFormError("viewSelfDetails.login.invalid");
					response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					return response;	
			    }
				
				
				
				
				
                /*ViewNetworkValidator viewNetworkValidator=new ViewNetworkValidator();
				viewNetworkValidator.validateViewNetworkData(data, response);
				if (response.hasFormError()) {
					response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					return response;
				}*/
				
				String divStatus = null;
	            String divStatusUsed = null;

	            divStatus = "'" + PretupsI.USER_STATUS_SUSPEND + "','" + PretupsI.USER_STATUS_ACTIVE + "'";
	            divStatusUsed = PretupsI.STATUS_IN;
	           
	            String status = "'" + PretupsI.USER_STATUS_DELETED + "','" + PretupsI.USER_STATUS_CANCELED + "'";
	            String statusUsed = PretupsI.STATUS_NOTIN;
	            
	            ChannelUserVO userVO1 = channelUserDAO.loadUsersDetailsByLoginId(connection, userVO.getLoginID(), null, statusUsed, status);
	            userVO1.setDivisionList(divisionwebDAO.loadDivisionDeptList(connection, TypesI.DIVDEPT_TYPE, TypesI.DIVDEPT_DIVISION, divStatusUsed, divStatus));
	            userVO1.setDepartmentList(divisionwebDAO.loadDivisionDeptList(connection, TypesI.DIVDEPT_TYPE, TypesI.DIVDEPT_DEPARTMENT, divStatusUsed, divStatus));
	         
	            
	            response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, userVO1);
	            boolean rsaRequired = false;
	            try {
	                rsaRequired = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.RSA_AUTHENTICATION_REQUIRED, userVO1.getNetworkID(), userVO1.getCategoryCode())).booleanValue();
	                userVO1.setRsaRequired(rsaRequired);
	            } catch (Exception e) {
	                _log.error(methodName, "Exception:e=" + e);
	            }
	             try {
					this.setDetailsOnForm(connection, userVO1);
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
				}
	      				
			}
			catch(IOException|BTSLBaseException e)
			{
				throw new BTSLBaseException (e);
			}
				finally {
					if(mcomCon != null){mcomCon.close("ViewSelfDetailsRestServiceImpl#viewSelfDetails");mcomCon=null;}
			}
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, "Exiting");
			}
			
			return response;
			
	}
	
	
	private void setDetailsOnForm(Connection con, UserVO userVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("setDetailsOnForm", "Entered");
        }

     
        String password = null;
        if (!BTSLUtil.isNullString(userVO.getPassword())) {
            password = BTSLUtil.getDefaultPasswordText(BTSLUtil.decryptText(userVO.getPassword()));
        }
        userVO.setShowPassword(password);
        userVO.setConfirmPassword(password);
        
        if (userVO.getAllowedDays() != null && userVO.getAllowedDays().trim().length() > 0) {
        	userVO.setAllowedDay(userVO.getAllowedDays().split(","));
        }
      
        userVO.setDivisionCode(userVO.getDivisionCode() + ":" + userVO.getDivisionCode());
        /*
         * In edit mode may be the division that is associated with the user is
         * suspended if it is suspended we need to explicitly add the division
         * into the existing list
         */
        if (userVO.getDivisionList() != null && userVO.getDivisionList().size() > 0) {
        	List<ListValueVO> list = new ArrayList<>();
        	for (ListValueVO listValueVO : userVO.getDivisionList()) {
        		 if (PretupsI.STATUS_ACTIVE.equals(listValueVO.getStatus())) {
                     list.add(listValueVO);
                 } else if (listValueVO.getValue().equals(userVO.getDivisionCode())) {
                     list.add(listValueVO);
                 }
			}
        	
        	userVO.setDivisionList(list);
        }

        String deptCOde=userVO.getDepartmentCode() + ":" + userVO.getDivisionCode();
        userVO.setDepartmentCode(deptCOde);
        /*
         * In edit mode may be the department that is associated with the user
         * is suspended if it is suspended we need to explicitly add the
         * department into the existing list
         */
        if (userVO.getDepartmentList() != null && userVO.getDepartmentList().size() > 0) {
        	List<ListValueVO> list = new ArrayList<>();
        	for (ListValueVO listValueVO : userVO.getDepartmentList()) {
        		 if (PretupsI.STATUS_ACTIVE.equals(listValueVO.getStatus())) {
                     list.add(listValueVO);
                 } else if (listValueVO.getValue().equals(userVO.getDepartmentCode())) {
                     list.add(listValueVO);
                 }
			}
        	
        	userVO.setDepartmentList(list);
        }

       

        
        UserWebDAO userwebDAO = new UserWebDAO();
        userVO.setCreatedBy(userwebDAO.userNameFromId(con, userVO.getCreatedBy()));
        String date = BTSLUtil.getDateStringFromDate(userVO.getCreatedOn());
        userVO.setCreated_On("" + date);

        if (userVO.getAppointmentDate() != null) {
        	userVO.setAppintmentDate(BTSLUtil.getDateStringFromDate(userVO.getAppointmentDate()));
        }
        

        if ("view".equals(userVO.getRequestType()) || "selfView".equals(userVO.getRequestType())) {
        	userVO.setCategoryVO(userVO.getCategoryVO());
        	userVO.setCategoryCode(userVO.getCategoryVO().getCategoryCode());
        	userVO.setCategoryCodeDesc(userVO.getCategoryVO().getCategoryName());
        	userVO.setParentName(userVO.getParentName());
        	userVO.setParentMsisdn(userVO.getParentMsisdn());
        	userVO.setParentCategoryName(userVO.getParentCategoryName());
        	userVO.setOwnerName(userVO.getOwnerName());
        	userVO.setOwnerMsisdn(userVO.getOwnerMsisdn());
        	userVO.setOwnerCategoryName(userVO.getOwnerCategoryName());
        }
        // this method load the other details of the user
        this.loadDetails(con, userVO);

		
        // added for approval of operator user.
        if ("aprl".equals(userVO.getRequestType())) {
            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()) {
                ListValueVO vo = BTSLUtil.getOptionDesc(userVO.getDivisionCode(), userVO.getDivisionList());
                userVO.setDivisionDesc(vo.getLabel());

                vo = BTSLUtil.getOptionDesc(userVO.getDepartmentCode(), userVO.getDepartmentList());
                userVO.setDepartmentDesc(vo.getLabel());
            } 
        } else// request for view forward to the operatorUserView.jsp
        {
            // load the Description of the corresponding selected dropdown value
            ListValueVO vo = BTSLUtil.getOptionDesc(userVO.getDivisionCode(), userVO.getDivisionList());
            userVO.setDivisionDesc(vo.getLabel());

            String deptCode = userVO.getDepartmentCode().substring(0, userVO.getDepartmentCode().lastIndexOf(":"));
            vo = BTSLUtil.getOptionDesc(deptCode, userVO.getDepartmentList());
            userVO.setDepartmentDesc(vo.getLabel());

            vo = BTSLUtil.getOptionDesc(userVO.getStatus(), userVO.getStatusList());
            userVO.setStatusDesc(vo.getLabel());

            // added for operator user
            // approval//SystemPreferences.OPT_USR_APRL_LEVEL > 0 &&
            if (BTSLUtil.isNullString(userVO.getStatusDesc())) {
            	userVO.setStatusDesc("New");
            }

            vo = BTSLUtil.getOptionDesc(userVO.getUserNamePrefixCode(), userVO.getUserNamePrefixList());
            userVO.setUserNamePrefixDesc(vo.getLabel());

            // Added by deepika aggarwal
            vo = BTSLUtil.getOptionDesc(userVO.getUserLanguage(), userVO.getUserLanguageList());
            userVO.setUserLanguageDesc(vo.getLabel());

            
        }
        if (_log.isDebugEnabled()) {
            _log.debug("setDetailsOnForm", "Exiting");
        }
       
    }
	
	 private void loadDetails(Connection p_con, UserVO userVO) throws Exception {
	        if (_log.isDebugEnabled()) {
	            _log.debug("loadDetails", "Entered");
	        }

	        
	        UserDAO p_userDAO = new UserDAO();
	        // load the phone info
	        ArrayList phoneList = p_userDAO.loadUserPhoneList(p_con, userVO.getUserID());
	        if (phoneList != null && phoneList.size() > 0) {
	        	userVO.setMsisdnList(phoneList);
	        } else if (userVO.getCategoryCode().equals(PretupsI.OPERATOR_CATEGORY) && (!userVO.getIsSerAssignChnlAdm())) {

	            ArrayList adminPhoneList = this.createPhoneVOForOptUser(p_con, userVO);
	            userVO.setMsisdnList(adminPhoneList);
	        }

	        GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
	        GeographicalDomainWebDAO _geographyDomainWebDAO = new GeographicalDomainWebDAO();
	        ArrayList geographyList = new ArrayList();
	        
	        if(!((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(userVO.getCategoryCode()))||(TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(userVO.getCategoryCode()))))
	        {
	        // load the geographies info from the user_geographies
	        	 
	        	if( (TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(userVO.getCategoryCode())))
	        	{
	        		// case when we added super channel admin from super admin, wish to load geographies that were assigned (irrespective of network code) to super channel admin
	        		 geographyList = _geographyDAO.loadUserGeographyListForSuperChannelAdmin(p_con, userVO.getUserID());
	        	}
	        	else
	        	{
	        		 geographyList = _geographyDAO.loadUserGeographyList(p_con, userVO.getUserID(), userVO.getNetworkID());
	        	}
	        
	        	userVO.setGeographicalList(geographyList);

	        	if (geographyList != null && geographyList.size() > 0)
	        	{
	            /*
	             * check whether the user has mutiple geographical area or not if
	             * multiple then set into the zoneCode array else set into the zone
	             * code
	             */
	        		UserGeographiesVO geographyVO = null;
	        		if (TypesI.YES.equals(userVO.getCategoryVO().getMultipleGrphDomains())) 
	        		{
	        			String[] arr = new String[geographyList.size()];
	        			int   geographyListSize = geographyList.size();
	        			for (int i = 0, j = geographyListSize; i < j; i++)
	        			{
	        				geographyVO = (UserGeographiesVO) geographyList.get(i);
	        				arr[i] = geographyVO.getGraphDomainCode();
	        				userVO.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
	        			}
	        			userVO.setGeographicalCodeArray(arr);
	        		}
	        		else 
	        		{
	        			if (geographyList.size() == 1)
	        			{
	        				geographyVO = (UserGeographiesVO) geographyList.get(0);
	        				userVO.setGeographicalCode(geographyVO.getGraphDomainCode());
	        				userVO.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
	        			}
	        		}
	        	}
	        }
	        else
	        {
	        	//case when we added operator users: super network admin and super cce from super admin and wish load their details (networklist is loaded instead of geographies)
	        	// load the geographies info from the user_geographies
	            ArrayList<UserGeographiesVO> networkList = _geographyDAO.loadUserNetworkList(p_con,userVO.getUserID());
	            userVO.setGeographicalList(networkList);
	            if (networkList != null && networkList.size() > 0) {
	                
	                	UserGeographiesVO geographyVO = null;
	                    String[] arr = new String[networkList.size()];
	                    int networkListSize = networkList.size();
	                    for (int i = 0, j = networkListSize; i < j; i++) {
	                        geographyVO = networkList.get(i);
	                        arr[i] = geographyVO.getGraphDomainCode();
	                    }
	                    userVO.setGeographicalCodeArray(arr);
	                    ArrayList netVOList = new NetworkDAO().loadNetworkList(p_con, "'"+PretupsI.STATUS_DELETE+"'");
	            		networkList = new ArrayList();
	            		int netVOListSize = netVOList.size();
	            		for(int i=0; i< netVOListSize; i++) {
	            			NetworkVO netVo = (NetworkVO)netVOList.get(i);
	            			UserGeographiesVO geogVO = new UserGeographiesVO();
	            			geogVO.setGraphDomainCode(netVo.getNetworkCode());
	            			geogVO.setGraphDomainName(netVo.getNetworkName());
	            			networkList.add(geogVO);
	            		}
	            		userVO.setNetworkList(networkList);
	                }
	            }
	        

	        // load the roles info from the user_roles table that are assigned with
	        // the user
	        UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();
	        ArrayList rolesList = rolesWebDAO.loadUserRolesList(p_con, userVO.getUserID());

	        if (rolesList != null && rolesList.size() > 0) {
	            String[] arr = new String[rolesList.size()];
	            rolesList.toArray(arr);
	            userVO.setRoleFlag(arr);

	        }
	        
	         /* load the roles info from the category,category-roles and roles table
	         * irrespective of the group_role flag for showing the name of the role
	         * on the jsp*/
	         
	        userVO.setRolesMap(rolesWebDAO.loadRolesList(p_con, userVO.getCategoryCode()));
	        if (userVO.getRolesMap() != null && userVO.getRolesMap().size() > 0) {
	            // this method populate the selected rolesO
	            populateSelectedRoles(userVO);
	        } else {
	            // by default set Role Type = N(means System Role radio button will
	            // be checked in edit mode if no role assigned yet)
	        	userVO.setRoleType("N");
	        }

	        // load the domain info from the user_domains table that are assigned to
	        // the user
	        DomainDAO domainDAO = new DomainDAO();
	        DomainWebDAO domainWebDAO = new DomainWebDAO();
	        ArrayList domainList = domainWebDAO.loadUserDomainList(p_con, userVO.getUserID());
	        if (domainList != null && domainList.size() > 0) {
	            String[] arr = new String[domainList.size()];
	            domainList.toArray(arr);
	            userVO.setDomainCodes(arr);
	        }
	        // load the domain info from the domain table that are asociated with
	        // the user
	        userVO.setDomainList(domainDAO.loadDomainList(p_con, PretupsI.DOMAIN_TYPE_CODE));

	        // load the services info from the user_services table that are assigned
	        // to the user
	        ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
	        ArrayList serviceList = servicesDAO.loadUserServicesList(p_con, userVO.getUserID());
	        if (serviceList.size() > 0 && userVO.getCategoryCode().equalsIgnoreCase(PretupsI.OPERATOR_CATEGORY)) {
	            // theForm.setIsSerAssignChnlAdm(true);
	            if (serviceList != null && serviceList.size() > 0) {
	                String[] arr = new String[serviceList.size()];
	                int serviceListSize = serviceList.size();
	                for (int i = 0, j = serviceListSize; i < j; i++) {
	                    ListValueVO listVO = (ListValueVO) serviceList.get(i);
	                    arr[i] = listVO.getValue();
	                }
	                userVO.setServicesTypes(arr);
	            }
	        }
	        
	         /** load all services irrespective of the module_code becs operator user
	         * can be associate with P2P as well as C2S services
	         */
	        if (PretupsI.OPERATOR_CATEGORY.equalsIgnoreCase(userVO.getCategoryCode()) || TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(userVO.getCategoryCode())) {
	        	userVO.setServicesList(servicesDAO.assignServicesToChlAdmin(p_con, userVO.getNetworkID()));
	        } else {
	        	userVO.setServicesList(servicesDAO.loadServicesList(p_con, userVO.getNetworkID()));
	        }

	        // load the Products info from the user_products table that are assigned
	        // to the user
	        ProductTypeDAO productTypeDAO = new ProductTypeDAO();
	        ArrayList productList = productTypeDAO.loadUserProductsList(p_con, userVO.getUserID());
	        if (productList != null && productList.size() > 0) {
	            String[] arr = new String[productList.size()];
	            productList.toArray(arr);
	            userVO.setProductCodes(arr);
	        }
	        // load the products list that are asociated with the Network and Module
	        // NetworkProductDAO networkProductDAO = new NetworkProductDAO();
	        // theForm.setProductsList(networkProductDAO.loadProductListByNetIdANDModuleCode(p_con,p_userVO.getNetworkID(),PretupsI.C2S_MODULE));
	        userVO.setProductsList(LookupsCache.loadLookupDropDown(PretupsI.PRODUCT_TYPE, true));

	        if (_log.isDebugEnabled()) {
	            _log.debug("loadDetails", "Exiting");
	        }
	    }
	 
	 public ArrayList createPhoneVOForOptUser(Connection p_con, UserVO p_uservo) throws Exception {
	        final String methodName = "createPhoneVOForOptUser";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "Entered");
	        }
	        ArrayList list = new ArrayList();
	        UserPhoneVO phoneVO = null;
	        MComConnectionI mcomCon = null;
	        mcomCon = new MComConnection();p_con=mcomCon.getConnection();
	        UserDAO userDAO = null;
	        ListValueVO listVO = null;
	        try {
	            
	            userDAO = new UserDAO();
	            listVO = BTSLUtil.getOptionDesc(p_uservo.getCategoryVO().getCategoryCode(), userDAO.loadPhoneProfileList(p_con, p_uservo.getCategoryVO().getCategoryCode()));
	            phoneVO = new UserPhoneVO();

	            phoneVO.setMsisdn(p_uservo.getMsisdn());
	            phoneVO.setUserId(p_uservo.getUserID());
	            phoneVO.setPrimaryNumber(PretupsI.YES);
	            // phoneVO.setShowSmsPin("****");
	            // phoneVO.setConfirmSmsPin("****");
	            phoneVO.setSmsPin(BTSLUtil.encryptText(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN))));
	            phoneVO.setDescription("");
	            if (!BTSLUtil.isNullString(phoneVO.getSmsPin())) {
	                if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
	                    phoneVO.setShowSmsPin("****");
	                    phoneVO.setConfirmSmsPin("****");
	                } else {
	                    // set the default value *****
	                    phoneVO.setShowSmsPin(BTSLUtil.getDefaultPasswordNumeric(BTSLUtil.decryptText(phoneVO.getSmsPin())));
	                    phoneVO.setConfirmSmsPin(BTSLUtil.getDefaultPasswordNumeric(BTSLUtil.decryptText(phoneVO.getSmsPin())));
	                }
	            }
	            phoneVO.setPhoneProfile(listVO.getValue());
	            phoneVO.setPhoneProfileDesc(listVO.getLabel());
	            list.add(phoneVO);
	        } catch (Exception ex) {
	            _log.errorTrace(methodName, ex);
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	        	if(mcomCon != null){mcomCon.close("ViewSelfDetailsRestServiceImpl#createPhoneVOForOptUser");mcomCon=null;}
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting: userPhoneList size=" + list.size());
	            }
	        }
	        return list;
	    }

	  private void populateSelectedRoles(UserVO userVO) throws Exception {
	        if (_log.isDebugEnabled()) {
	            _log.debug("populateSelectedRoles", "Entered");
	        }
	      
	        HashMap mp = userVO.getRolesMap();
	        HashMap newSelectedMap = new HashMap();
	        Iterator it = mp.entrySet().iterator();
	        String key = null;
	        ArrayList list = null;
	        ArrayList listNew = null;
	        UserRolesVO roleVO = null;
	        Map.Entry pairs = null;
	        boolean foundFlag = false;

	        while (it.hasNext()) {
	            pairs = (Map.Entry) it.next();
	            key = (String) pairs.getKey();
	            list = new ArrayList((ArrayList) pairs.getValue());
	            listNew = new ArrayList();
	            foundFlag = false;
	            if (list != null) {
	            	int listSize = list.size();
	                for (int i = 0, j = listSize; i < j; i++) {
	                    roleVO = (UserRolesVO) list.get(i);
	                    if (userVO.getRoleFlag() != null && userVO.getRoleFlag().length > 0) {
	                    	int roleFlagLength = userVO.getRoleFlag().length;
	                        for (int k = 0; k < roleFlagLength; k++) {
	                            if (roleVO.getRoleCode().equals(userVO.getRoleFlag()[k])) {
	                                listNew.add(roleVO);
	                                foundFlag = true;
	                                /*
	                                 * In edit RoleType radio button should be
	                                 * checked according the type of role assigned
	                                 * to it whether it can be SystemRole or
	                                 * GroupRole at a time only one type of role
	                                 * will be assigned to the user so with the help
	                                 * of roleVO we know the type of role if
	                                 * roleVO.getGroupRole = N(means System role)
	                                 * else roleVO.getGroupRole = Y(means Group
	                                 * role)
	                                 */
	                                userVO.setRoleType(roleVO.getGroupRole());
	                            }
	                        }
	                    }
	                }
	            }
	            if (foundFlag) {
	                newSelectedMap.put(key, listNew);
	            }
	        }
	        if (newSelectedMap.size() > 0) {
	        	userVO.setRolesMapSelected(newSelectedMap);
	        } else {
	            // by default set Role Type = N(means System Role radio button will
	            // be checked in edit mode if no role assigned yet)
	        	userVO.setRoleType("N");
	        	userVO.setRolesMapSelected(null);
	        }

	        if (_log.isDebugEnabled()) {
	            _log.debug("populateSelectedRoles", "Exiting");
	        }
	    }

	
	

}
