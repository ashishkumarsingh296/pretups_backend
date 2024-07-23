package com.restapi.channelAdmin.serviceMgmt.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.UserServicesCache;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.CategoryServiceDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.channelAdmin.serviceMgmt.requestVO.AddServiceMgmtReqVO;
import com.restapi.channelAdmin.serviceMgmt.responseVO.AddServiceMgmtRespVO;
import com.restapi.channelAdmin.serviceMgmt.responseVO.C2Sservices;
import com.restapi.channelAdmin.serviceMgmt.responseVO.OtherServices;
import com.restapi.channelAdmin.serviceMgmt.responseVO.SearchServiceMgmtRespVO;
import com.restapi.channelAdmin.serviceMgmt.responseVO.ServiceManagementUIDataTableVO;
import com.restapi.channelAdmin.serviceMgmt.responseVO.ServiceMgmtInptRespVO;
import com.restapi.channelAdmin.serviceMgmt.responseVO.ServiceTypeDataRespVO;
import com.restapi.channelAdmin.serviceMgmt.serviceI.ServiceMgmtServiceI;
import com.restapi.superadmin.CategoryManagementController;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;


@Component
public class ServiceMgmtServiceImpl implements ServiceMgmtServiceI {
	
	
	public static final Log log = LogFactory.getLog(ServiceMgmtServiceImpl.class.getName());

	@Override
	public ServiceMgmtInptRespVO getServiceMgmtInputValues(String loginID, Locale locale) throws BTSLBaseException {
		final String methodName ="getServiceMgmtInputValues";
		UserDAO userDAO = new UserDAO();
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		ServiceMgmtInptRespVO serviceMgmtInptRespVO = new ServiceMgmtInptRespVO();
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			UserVO userVO =userDAO.loadUsersDetailsfromLoginID(con, loginID);
			
			if(!BTSLUtil.isNullObject(userVO)) {
			serviceMgmtInptRespVO.setDomainList(BTSLUtil.displayDomainList(userVO.getDomainList()));
			serviceMgmtInptRespVO.setServiceDropdownValues(LookupsCache.loadLookupDropDown(PretupsI.SERV_MGMT_TYPE, true));
			serviceMgmtInptRespVO.setMessageCode(PretupsI.SUCCESS);
			serviceMgmtInptRespVO.setMessage(PretupsI.SUCCESS);
			serviceMgmtInptRespVO.setStatus(HttpStatus.SC_OK);
			}else {
				serviceMgmtInptRespVO.setMessageCode(PretupsErrorCodesI.NO_USER_EXIST_LOGINID);
				serviceMgmtInptRespVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_USER_EXIST_LOGINID, null));
				serviceMgmtInptRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
    	
			}

		} catch (SQLException se) {
			throw new BTSLBaseException(this, methodName,
					"Error while executing sql statement", se);
		} catch (Exception e) {
			throw new BTSLBaseException(this, methodName,
					"Error while executing method getServiceMgmtInputValues ", e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				try {
					con.close();
				} catch (SQLException se) {
					throw new BTSLBaseException(this, methodName,
							"Error while close connection", se);
				}

		}	
		
		return serviceMgmtInptRespVO;
	}

	@Override
	public SearchServiceMgmtRespVO searchServiceMgmtData(String loginID, String serviceType, String domainCode,
			Locale locale) throws BTSLBaseException {
		UserDAO userDAO = new UserDAO();
		final String methodName ="searchServiceMgmtData";
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		CategoryServiceDAO categoryServiceDAO=null;
		SearchServiceMgmtRespVO serviceMgmtInptRespVO = new SearchServiceMgmtRespVO();
		List categoryListWithServices = null;
		ListValueVO listValueVO = null;
        ServicesTypeDAO servicesDAO = null;
        UserVO userVO = null;
        CategoryVO categoryVO = null;
        List categoryList = null;
        ArrayList selectedServiceList = null;
        DomainDAO domainDAO = new DomainDAO();
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userVO =userDAO.loadUsersDetailsfromLoginID(con, loginID);
			categoryListWithServices = new ArrayList();
			Map domainWiseServiceMap = new HashMap();
            Map domainWiseOtherServiceMap = new HashMap();
            categoryServiceDAO = new CategoryServiceDAO();
            CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
           List domainList =  BTSLUtil.displayDomainList(userVO.getDomainList());
           servicesDAO = new ServicesTypeDAO();
           HashMap<String,ServiceTypeDataRespVO> selectedServiceMap =servicesDAO.loadServicesListWithcategoryCodeDomain(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
           
           
           
           if(!PretupsI.ALL.equalsIgnoreCase(domainCode) ) {
           DomainVO domainVO =domainDAO.loadDomainVO(con, domainCode);  
           if(BTSLUtil.isNullObject(domainVO)) {
        	   throw new BTSLBaseException(this, methodName,
						PretupsI.SERVICE_MGMT_DOMAIN_CODE_NOTFOUND);
           }
           }
           
           for (int index = 0, k = domainList.size(); index < k; index++) {
               listValueVO = (ListValueVO) domainList.get(index);
               categoryList = categoryWebDAO.loadCategorListByDomainCodewithName(con, listValueVO.getValue());
               for (int i = 0, j = ((ArrayList) categoryList).size(); i < j; i++) {
                   categoryVO = (CategoryVO) ((ArrayList) categoryList).get(i);
                   if (PretupsI.YES.equals(categoryVO.getServiceAllowed())) {
                       categoryListWithServices.add(categoryVO);
                   }
               }
               domainWiseServiceMap.put(listValueVO.getValue(), categoryServiceDAO.loadTransferRuleServicesList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE, listValueVO.getValue()));
              // domainWiseOtherServiceMap.put(listValueVO.getValue(), loadOtherServicesList());
               if (PretupsI.ALL.equalsIgnoreCase(serviceType.trim())) {
               domainWiseOtherServiceMap.put(listValueVO.getValue(), categoryServiceDAO.loadOtherServicesList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE));
           	    }
               if(!PretupsI.ALL.equalsIgnoreCase(domainCode) &&  (listValueVO.getValue().equalsIgnoreCase(domainCode))){
            	   break;  // From UI user selected  a domain., 
               }
           }

           serviceMgmtInptRespVO.setCategoryList(categoryListWithServices);
           
           serviceMgmtInptRespVO.setTotalServiceList(servicesDAO.loadServicesList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE, null, true));
           String[] serviceData=  categoryServiceDAO.loadServices(con, userVO.getNetworkID());
           serviceMgmtInptRespVO.setServiceFlag(serviceData);
           Map map = new LinkedHashMap();
           Map map1 = new LinkedHashMap();
           ArrayList<ServiceManagementUIDataTableVO> listRowUIDataTable= new ArrayList<ServiceManagementUIDataTableVO>(); 
           for (int k = 0, len = ((ArrayList) serviceMgmtInptRespVO.getCategoryList()).size(); k < len; k++) {
               categoryVO = (CategoryVO) ((ArrayList) serviceMgmtInptRespVO.getCategoryList()).get(k);
               map.put(categoryVO.getDomainCodeforCategory() + "_" + categoryVO.getCategoryCode(), domainWiseServiceMap.get(categoryVO.getDomainCodeforCategory()));
               if (PretupsI.ALL.equalsIgnoreCase(serviceType.trim())) {
               map1.put(categoryVO.getDomainCodeforCategory() + "_" + categoryVO.getCategoryCode(), domainWiseOtherServiceMap.get(categoryVO.getDomainCodeforCategory()));
               }
               ServiceManagementUIDataTableVO serviceManagementUIDataTableVO =      constructSearchUIDatatable(serviceType.trim().toUpperCase(),categoryVO,(ArrayList)domainWiseServiceMap.get(categoryVO.getDomainCodeforCategory()),(ArrayList)domainWiseOtherServiceMap.get(categoryVO.getDomainCodeforCategory()),selectedServiceMap);
               listRowUIDataTable.add(serviceManagementUIDataTableVO);
             
           }
           serviceMgmtInptRespVO.setServiceMap(map);
           if (PretupsI.ALL.equalsIgnoreCase(serviceType.trim())) {
        	   serviceMgmtInptRespVO.setOtherServiceMap(map1);
           }
	       serviceMgmtInptRespVO.setListRowUIDataTable(listRowUIDataTable);
           serviceMgmtInptRespVO.setTotOtherServicesList(categoryServiceDAO.loadOtherServicesList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE));
           
           serviceMgmtInptRespVO.setStatus(HttpStatus.SC_OK);
           if(!BTSLUtil.isNullOrEmptyList(selectedServiceList)){
        	   serviceMgmtInptRespVO.setMessageCode(PretupsI.SEARCH_SUCCESS);
        	   serviceMgmtInptRespVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SEARCH_SUCCESS, null));
        	  Date currentDate = new Date(); 
        	   AdminOperationVO adminOperationVO = new AdminOperationVO();
               adminOperationVO.setSource(TypesI.LOGGER_SERVICE_MGMT);
               adminOperationVO.setDate(currentDate);
               adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_SEARCH);
               adminOperationVO.setInfo(RestAPIStringParser.getMessage(locale, PretupsI.SEARCH_SUCCESS, null));
               adminOperationVO.setLoginID(userVO.getLoginID());
               adminOperationVO.setUserID(userVO.getUserID());
               adminOperationVO.setCategoryCode(userVO.getCategoryCode());
               adminOperationVO.setNetworkCode(userVO.getNetworkID());
               adminOperationVO.setMsisdn(userVO.getMsisdn());
               AdminOperationLog.log(adminOperationVO);
           }
           
           
           
			
		} catch (SQLException se) {
			throw new BTSLBaseException(this, methodName,
					"Error while executing sql statement", se);
		} catch (Exception e) {
			throw new BTSLBaseException(this, methodName,
					"Error while executing method getServiceMgmtInputValues ", e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				try {
					con.close();
				} catch (SQLException se) {
					throw new BTSLBaseException(this, methodName,
							"Error while close connection", se);
				}

		}	

		
	
	 
		return serviceMgmtInptRespVO;
	}
	
	
	private ServiceManagementUIDataTableVO  constructSearchUIDatatable(String uiServiceTypeFilterSearch,CategoryVO categoryVO,ArrayList listofC2Sservices,ArrayList listofOtherservices, HashMap<String,ServiceTypeDataRespVO> selectedServiceMap) {
		ServiceManagementUIDataTableVO serviceManagementUIDataTableVO = new ServiceManagementUIDataTableVO();
		String key=null;
		serviceManagementUIDataTableVO.setDomainCode(categoryVO.getDomainCodeforCategory());
		serviceManagementUIDataTableVO.setCategoryCode(categoryVO.getCategoryCode());
		serviceManagementUIDataTableVO.setDomainName(categoryVO.getDomainName());
		serviceManagementUIDataTableVO.setCategoryName(categoryVO.getCategoryName());
		ArrayList<C2Sservices> listc2sServicesData=new ArrayList();
		ArrayList<OtherServices> listOtherServicesData=new ArrayList();
		
		if (!BTSLUtil.isNullOrEmptyList(listofC2Sservices)) {
			for (int i = 0; i < listofC2Sservices.size(); i++) {
				C2Sservices c2Sservices = new C2Sservices();
				ListValueVO listofValVO = (ListValueVO) listofC2Sservices.get(i);
				c2Sservices.setServiceName(listofValVO.getLabel());
				c2Sservices.setServiceType(listofValVO.getValue());
				key = categoryVO.getDomainCodeforCategory() + "_" + categoryVO.getCategoryCode() + "_" + c2Sservices.getServiceType();
				c2Sservices.setCheckBoxSelect(false);
				if (selectedServiceMap.containsKey(key.toUpperCase())) {
					c2Sservices.setCheckBoxSelect(true);
				}
				listc2sServicesData.add(c2Sservices);
			}
			serviceManagementUIDataTableVO.setListOfC2SServices(listc2sServicesData);	
		}
		
		if(PretupsI.ALL.equalsIgnoreCase(uiServiceTypeFilterSearch)) {
			if (!BTSLUtil.isNullOrEmptyList(listofOtherservices)) {
				for (int i = 0; i < listofOtherservices.size(); i++) {
					OtherServices otherServices = new OtherServices();
					ListValueVO listofValVO = (ListValueVO) listofOtherservices.get(i);
					otherServices.setServiceName(listofValVO.getLabel());
					otherServices.setServiceType(listofValVO.getValue());
					key = categoryVO.getDomainCodeforCategory()  + "_" + categoryVO.getCategoryCode() + "_" + otherServices.getServiceType();
					otherServices.setCheckBoxSelect(false);
					if (selectedServiceMap.containsKey(key.toUpperCase())) {
						otherServices.setCheckBoxSelect(true);
					}
					listOtherServicesData.add(otherServices);
				}
				serviceManagementUIDataTableVO.setListofOtherServices(listOtherServicesData);
			}
		}
		
		return serviceManagementUIDataTableVO;
	}

	@Override
	public AddServiceMgmtRespVO addserviceMgmtData(AddServiceMgmtReqVO addServiceMgmtReqVO, String loginID,
			Locale locale) throws BTSLBaseException, SQLException {

		UserDAO userDAO = new UserDAO();
		final String methodName ="addserviceMgmtData";
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		CategoryServiceDAO categoryServiceDAO=null;
		AddServiceMgmtRespVO addServiceMgmtRespVO = new AddServiceMgmtRespVO();
		ListValueVO listValueVO = null;
        UserVO userVO = null;
        int insertCount=0;
        Date currentDate = new Date();
        
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userVO =userDAO.loadUsersDetailsfromLoginID(con, loginID);
			List domainList =  BTSLUtil.displayDomainList(userVO.getDomainList());
			categoryServiceDAO = new CategoryServiceDAO();
			if (addServiceMgmtReqVO.getServiceFlag() == null || addServiceMgmtReqVO.getServiceFlag().length == 0) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MASTER_SERVICE_MGMT_SELECT_ATLEAST_ONE_MSG);
			}
			insertCount = categoryServiceDAO.addServices(con, addServiceMgmtReqVO.getServiceFlag(), domainList, userVO.getNetworkID());
            if (con != null) {
                if (insertCount > 0) {
                   // con.commit();
                	mcomCon.finalCommit();
                    //btslMessage = new BTSLMessages("master.servicemgmt.msg.success", "firstpage");
                	
                	addServiceMgmtRespVO.setMessageCode(PretupsI.SERVICE_MGMT_ADD_SUCCESS);
                	addServiceMgmtRespVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SERVICE_MGMT_ADD_SUCCESS, null));
                	addServiceMgmtRespVO.setStatus(HttpStatus.SC_OK);
               	   AdminOperationVO adminOperationVO = new AdminOperationVO();
                      adminOperationVO.setSource(TypesI.LOGGER_SERVICE_MGMT);
                      adminOperationVO.setDate(currentDate);
                      adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                      adminOperationVO.setInfo(RestAPIStringParser.getMessage(locale, PretupsI.SERVICE_MGMT_ADD_SUCCESS, null));
                      adminOperationVO.setLoginID(userVO.getLoginID());
                      adminOperationVO.setUserID(userVO.getUserID());
                      adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                      adminOperationVO.setNetworkCode(userVO.getNetworkID());
                      adminOperationVO.setMsisdn(userVO.getMsisdn());
                      AdminOperationLog.log(adminOperationVO);
                    
                } else {
                	
             	   AdminOperationVO adminOperationVO = new AdminOperationVO();
                   adminOperationVO.setSource(TypesI.LOGGER_SERVICE_MGMT);
                   adminOperationVO.setDate(currentDate);
                   adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                   adminOperationVO.setInfo(RestAPIStringParser.getMessage(locale, PretupsI.SERVICE_MGMT_ADD_FAILED, null));
                   adminOperationVO.setLoginID(userVO.getLoginID());
                   adminOperationVO.setUserID(userVO.getUserID());
                   adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                   adminOperationVO.setNetworkCode(userVO.getNetworkID());
                   adminOperationVO.setMsisdn(userVO.getMsisdn());
                   AdminOperationLog.log(adminOperationVO);
                    //con.rollback();
                	mcomCon.finalRollback();
                    //throw new BTSLBaseException(this, "loadServices", "master.servicemgmt.msg.fail", "assignServices");
                    throw new BTSLBaseException(CategoryManagementController.class.getName(), methodName,
    						PretupsI.SERVICE_MGMT_ADD_FAILED);
                }
            }
            
            UserServicesCache.updateServicesMap();	
		
	} finally {
		if (mcomCon != null) {
			mcomCon.close("");
			mcomCon = null;
		}
		if (con != null)
			try {
				con.close();
			} catch (SQLException se) {
				throw new BTSLBaseException(this, methodName,
						"Error while close connection", se);
			}

	}	
		
		
		
		
		return addServiceMgmtRespVO;
	}
	

}
