package com.restapi.c2s.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.IDGenerator;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.channel.profile.businesslogic.ServiceTypeobjVO;
import com.btsl.pretups.channel.transfer.businesslogic.AddServiceKeywordReq;
import com.btsl.pretups.channel.transfer.businesslogic.AddServiceKeywordResp;
import com.btsl.pretups.channel.transfer.businesslogic.DeleteServiceKeywordResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetServiceKeywordListResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetServiceTypeListResp;
import com.btsl.pretups.channel.transfer.businesslogic.ModifyServiceKeywordReq;
import com.btsl.pretups.channel.transfer.businesslogic.ModifyServiceKeywordResp;
import com.btsl.pretups.channel.transfer.businesslogic.ServiceKeywordResp;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;


@Service
public class ServiceKeywordServiceImpl  implements ServiceKeywordServiceI{
	
	private Log log = LogFactory.getLog(this.getClass().getName());

	@Override
	public GetServiceTypeListResp getServiceTypeList(Connection con) throws BTSLBaseException {
		// TODO Auto-generated method stub
		GetServiceTypeListResp  getServiceTypeListResp = new GetServiceTypeListResp();
		ServiceKeywordDAO ServiceKeywordDAO = new ServiceKeywordDAO();
		List<ServiceTypeobjVO> list =  ServiceKeywordDAO.loadServiceTypeListData(con);
	      if (list == null || list.size() == 0) {
	    		throw new BTSLBaseException("ServiceKeywordServiceImpl", "getServiceTypeList",
						PretupsErrorCodesI.NO_RECORDS_FOUND, 0, null); 
		   }
		
		getServiceTypeListResp.setListServiceListObj(list);
		return getServiceTypeListResp;
	}

	@Override
	public GetServiceKeywordListResp    searchServiceKeywordbyServiceType(Connection con, String inputServiceType)
			throws BTSLBaseException {
		GetServiceKeywordListResp  getServiceKeywordListResp = new GetServiceKeywordListResp();
		ServiceKeywordDAO serviceKeywordDAO = new ServiceKeywordDAO();
		List<ServiceKeywordVO> list =  serviceKeywordDAO.loadServiceTypeData(con, inputServiceType);
	      if (list == null || list.size() == 0) {
	    		throw new BTSLBaseException("ServiceKeywordServiceImpl", "getServiceTypeList",
						PretupsErrorCodesI.NO_RECORDS_FOUND, 0, null); 
		   }
		
	      getServiceKeywordListResp.setListServiceListObj(list);
		return getServiceKeywordListResp;
		
	}

	@Override
	public AddServiceKeywordResp addServiceKeyword(Connection con, AddServiceKeywordReq addServiceKeywordReq,UserVO userVO,Locale locale)
			throws BTSLBaseException {
		final String methodName ="addServiceKeyword";
		
		StringBuffer uniqueServiceKeywordID = null;
		ServiceKeywordVO serviceKeywordVO = null;
        Date currentDate = new Date();
        CommonUtil commutil = new CommonUtil();
        int addCount;
        uniqueServiceKeywordID = new StringBuffer();
        String idType = PretupsI.SERVICE_KEYWORD_ID;

        // generating the unique key of the table service_keywords .
        AddServiceKeywordResp addServiceKeywordResp = new AddServiceKeywordResp();
        serviceKeywordVO = constructVOFromForm(addServiceKeywordReq,userVO);
        
        ServiceKeywordDAO  serviceKeywordDAO = new ServiceKeywordDAO();
        ServiceTypeobjVO serviceTypeobjVO =    serviceKeywordDAO.getServiceTypeDetails(con, addServiceKeywordReq.getServiceType());
        GetServiceKeywordListResp    getServiceKeywordListResp = searchServiceKeywordbyServiceType(con, addServiceKeywordReq.getServiceType());
	       Map mp =null;
        
        serviceKeywordVO.setCreatedOn(currentDate);
        serviceKeywordVO.setModifiedOn(currentDate);
        serviceKeywordVO.setCreatedBy(userVO.getUserID());
        serviceKeywordVO.setModifiedBy(userVO.getUserID());
        
        // checking that is record already exists with the new key or not.
        // last parameter to indicate that donot check the
        // service_keyword_id
        
        
        if(!BTSLUtil.isNullString(addServiceKeywordReq.getMessageGatewayType()) &&  addServiceKeywordReq.getMessageGatewayType().length()>10 ) {
        	throw new BTSLBaseException(this, methodName, PretupsI.SERVICE_KEYWORD_MESSAGEGATEWAY_LENGTH_EXCEEDED);
        }	
        
        
        
        
        if(BTSLUtil.isNullString(addServiceKeywordReq.getReceivePort())) {
        	throw new BTSLBaseException(this, methodName, PretupsI.SUBKEYWORD_BLANK_VALUE_PORT);
        }else {
	        commutil.validatePortSeries(addServiceKeywordReq.getReceivePort());
	       mp= commutil.containsDuplicatePort(addServiceKeywordReq.getReceivePort());
        }
        
         //validatePortAlreadyUsedByotherkeyword(getServiceKeywordListResp, mp,null);
          commutil.validateversion(addServiceKeywordReq.getAllowedVersion());
          commutil.validateRegex(Constants.getProperty(PretupsI.NAME_REGEX), addServiceKeywordReq.getMenu(), PretupsI.LABEL_MENU, PretupsI.DESC_ALPHA_NUMERIC);
	      commutil.validateRegex(Constants.getProperty(PretupsI.NAME_REGEX), addServiceKeywordReq.getName(), PretupsI.LABEL_NAME, PretupsI.DESC_ALPHA_NUMERIC_SPACE);
          
          if(PretupsI.YES.equalsIgnoreCase(serviceTypeobjVO.getSubKeyWordApplicable())){
        	  serviceKeywordVO.setSubKeywordApplicable(true);
        	  if(BTSLUtil.isNullString(addServiceKeywordReq.getSubKeyWord())){
        		  throw new BTSLBaseException(this, methodName, PretupsI.SUBKEYWORD_INPUT_MANDATORY);
        	  }
          }
         
        if (serviceKeywordDAO.isServiceKeywordExist(con, serviceKeywordVO, false)) {
            if (log.isDebugEnabled()) {
            	log.debug(methodName, "isServiceKeywordExist=true");
            }
            // CR 000009 Forward to appropraite error page if sub keyword or
           // keyword is already modified
            if (serviceKeywordVO.isSubKeywordApplicable()) {
            	throw new BTSLBaseException(this, methodName, PretupsI.SUBKEYWORD_ALREADY_EXIST , methodName);
            }
        throw new BTSLBaseException(this, methodName, PretupsI.SERVICEKEYWORD_ALREADY_EXIST, methodName);
        } else {
        	long interfaceID = IDGenerator.getNextIDByConnection(con,idType, TypesI.ALL);
            int zeroes = 10 - (idType.length() + Long.toString(interfaceID).length());
            for (int count = 0; count < zeroes; count++) {
                uniqueServiceKeywordID.append(0);
            }
            uniqueServiceKeywordID.insert(0, idType);
            uniqueServiceKeywordID.append(Long.toString(interfaceID));
            serviceKeywordVO.setServiceKeywordID(uniqueServiceKeywordID.toString());
            addCount = serviceKeywordDAO.addServiceType(con, serviceKeywordVO);
            if (con != null) {
                if (addCount > 0) {
                	try {
                	con.commit();
                	}catch(SQLException se) {
                		throw new BTSLBaseException(this, "addServiceType", PretupsErrorCodesI.SERVICEKEYWORD_ADD_UNSUCCESS, "addservicetype");
                	}
                	
                	String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
                	addServiceKeywordResp.setStatus(success);
                	addServiceKeywordResp.setMessageCode(PretupsErrorCodesI.SERVICEKEYWORD_ADD_SUCCESS);
        			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SERVICEKEYWORD_ADD_SUCCESS, null);
        			addServiceKeywordResp.setMessage(resmsg);
                	
                	
                    // log the data in adminOperationLog.log
                    AdminOperationVO adminOperationVO = new AdminOperationVO();
                    adminOperationVO.setSource(TypesI.LOGGER_SERVICE_KEYWORD_SOURCE);
                    adminOperationVO.setDate(currentDate);
                    adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
					String subkeywordDispalylog="";
					if(serviceKeywordVO.getSubKeyword()!=null){
						subkeywordDispalylog="Sub keyword (" + serviceKeywordVO.getSubKeyword() + " )";
					}
                    adminOperationVO.setInfo("Service keyword (" + serviceKeywordVO.getKeyword() + ")" + subkeywordDispalylog+ " has added successfully");
                    adminOperationVO.setLoginID(userVO.getLoginID());
                    adminOperationVO.setUserID(userVO.getUserID());
                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
                    adminOperationVO.setMsisdn(userVO.getMsisdn());
                    AdminOperationLog.log(adminOperationVO);

                    BTSLMessages btslMessage = new BTSLMessages("servicekeyword.displaydetail.msg.addsuccess", "addservicekeywordpage");

                } else {
                	try {
                	con.rollback();
                	}catch(SQLException se) {
                		throw new BTSLBaseException(this, "addServiceType", PretupsErrorCodesI.SERVICEKEYWORD_ADD_UNSUCCESS, "addservicetype");
                	}
                	
                	String success = Integer.toString(PretupsI.RESPONSE_FAIL);
                	addServiceKeywordResp.setStatus(success);
                	addServiceKeywordResp.setMessageCode(PretupsErrorCodesI.SERVICEKEYWORD_ADD_UNSUCCESS);
        			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SERVICEKEYWORD_ADD_UNSUCCESS, null);
        			addServiceKeywordResp.setMessage(resmsg);
                
                }
            
            }
            
        }
            
		return addServiceKeywordResp;
		
		
	}
        
        
        /**
         * Method constructVOFromForm. This method is used to construct the object
         * of VO class form the FORM BEAN.
         * 
         * @param p_serviceKeywordForm
         *            ServiceKeywordForm
         * @return ServiceKeywordVO 
         * @throws Exception
         */
        private ServiceKeywordVO constructVOFromForm(AddServiceKeywordReq addServiceKeywordReq,UserVO userVO) throws BTSLBaseException {
            if (log.isDebugEnabled()) {
            	log.debug("constructVOFromForm", "Entered");
            }
            ServiceKeywordVO serviceKeywordVO = new ServiceKeywordVO();
            serviceKeywordVO.setServiceKeywordID(addServiceKeywordReq.getKeyword());
            serviceKeywordVO.setServiceType(addServiceKeywordReq.getServiceType());
            if (!BTSLUtil.isNullString(addServiceKeywordReq.getKeyword())) {
                serviceKeywordVO.setKeyword(addServiceKeywordReq.getKeyword().trim());
            } else {
                serviceKeywordVO.setKeyword(addServiceKeywordReq.getKeyword());
            }

            serviceKeywordVO.setInterface(addServiceKeywordReq.getMessageGatewayType());
            serviceKeywordVO.setReceivePort(addServiceKeywordReq.getReceivePort());
            //serviceKeywordVO.setResponseCode(p_serviceKeywordForm.getResponseCode());
            if (!BTSLUtil.isNullString(addServiceKeywordReq.getName())) {
                serviceKeywordVO.setName(addServiceKeywordReq.getName().trim());
            } else {
                serviceKeywordVO.setName(addServiceKeywordReq.getName());
            }

            serviceKeywordVO.setStatus(addServiceKeywordReq.getStatus());
            serviceKeywordVO.setGatewayRequestParam(addServiceKeywordReq.getGatewayRequestParameter());

            if (!BTSLUtil.isNullString(addServiceKeywordReq.getMenu())) {
                serviceKeywordVO.setMenu(addServiceKeywordReq.getMenu().trim());
            } else {
                serviceKeywordVO.setMenu(addServiceKeywordReq.getMenu());
            }

            if (!BTSLUtil.isNullString(addServiceKeywordReq.getSubmenu())) {
                serviceKeywordVO.setSubMenu(addServiceKeywordReq.getSubmenu().trim());
            } else {
                serviceKeywordVO.setSubMenu(addServiceKeywordReq.getSubmenu());
            }

            if (!BTSLUtil.isNullString(addServiceKeywordReq.getAllowedVersion())) {
                serviceKeywordVO.setAllowedVersion(addServiceKeywordReq.getAllowedVersion().trim());
            } else {
                serviceKeywordVO.setAllowedVersion(addServiceKeywordReq.getAllowedVersion());
            }
            
            if (!BTSLUtil.isNullString(addServiceKeywordReq.getSubKeyWord())) {
                serviceKeywordVO.setSubKeyword(addServiceKeywordReq.getSubKeyWord());
            } 

            serviceKeywordVO.setModifyAllowed(addServiceKeywordReq.getKeywordModifyAllow());
            serviceKeywordVO.setCreatedBy(userVO.getUserID());
            serviceKeywordVO.setCreatedOn(new Date());
            serviceKeywordVO.setModifiedBy(null);
            serviceKeywordVO.setModifiedOn(null);
            serviceKeywordVO.setLastModifiedTime(0l);


            if (log.isDebugEnabled()) {
            	log.debug("constructVOFromForm", "Exiting:serviceKeywordVO=" + serviceKeywordVO);
            }
            return serviceKeywordVO;
        }
        
        
        
        
        /**
         * Method constructVOFromForm. This method is used to construct the object
         * of VO class form the FORM BEAN.
         * 
         * @param p_serviceKeywordForm
         *            ServiceKeywordForm
         * @return ServiceKeywordVO 
         * @throws Exception
         */
        private ServiceKeywordVO modifyconstructVOFromForm(ModifyServiceKeywordReq modifyServiceKeywordReq,UserVO userVO) throws BTSLBaseException {
            if (log.isDebugEnabled()) {
            	log.debug("constructVOFromForm", "Entered");
            }
            ServiceKeywordVO serviceKeywordVO = new ServiceKeywordVO();
            serviceKeywordVO.setServiceKeywordID(modifyServiceKeywordReq.getKeyword());
            serviceKeywordVO.setServiceType(modifyServiceKeywordReq.getServiceType());
            if (!BTSLUtil.isNullString(modifyServiceKeywordReq.getKeyword())) {
                serviceKeywordVO.setKeyword(modifyServiceKeywordReq.getKeyword().trim());
            } else {
                serviceKeywordVO.setKeyword(modifyServiceKeywordReq.getKeyword());
            }

            serviceKeywordVO.setInterface(modifyServiceKeywordReq.getMessageGatewayType());
            serviceKeywordVO.setReceivePort(modifyServiceKeywordReq.getReceivePort());
            //serviceKeywordVO.setResponseCode(p_serviceKeywordForm.getResponseCode());
            if (!BTSLUtil.isNullString(modifyServiceKeywordReq.getName())) {
                serviceKeywordVO.setName(modifyServiceKeywordReq.getName().trim());
            } else {
                serviceKeywordVO.setName(modifyServiceKeywordReq.getName());
            }

            serviceKeywordVO.setStatus(modifyServiceKeywordReq.getStatus());
            serviceKeywordVO.setGatewayRequestParam(modifyServiceKeywordReq.getGatewayRequestParameter());

            if (!BTSLUtil.isNullString(modifyServiceKeywordReq.getMenu())) {
                serviceKeywordVO.setMenu(modifyServiceKeywordReq.getMenu().trim());
            } else {
                serviceKeywordVO.setMenu(modifyServiceKeywordReq.getMenu());
            }

            if (!BTSLUtil.isNullString(modifyServiceKeywordReq.getSubmenu())) {
                serviceKeywordVO.setSubMenu(modifyServiceKeywordReq.getSubmenu().trim());
            } else {
                serviceKeywordVO.setSubMenu(modifyServiceKeywordReq.getSubmenu());
            }

            if (!BTSLUtil.isNullString(modifyServiceKeywordReq.getAllowedVersion())) {
                serviceKeywordVO.setAllowedVersion(modifyServiceKeywordReq.getAllowedVersion().trim());
            } else {
                serviceKeywordVO.setAllowedVersion(modifyServiceKeywordReq.getAllowedVersion());
            }
            
            if (!BTSLUtil.isNullString(modifyServiceKeywordReq.getSubKeyWord())) {
                serviceKeywordVO.setSubKeyword(modifyServiceKeywordReq.getSubKeyWord());
            } 

            serviceKeywordVO.setModifyAllowed(modifyServiceKeywordReq.getKeywordModifyAllow());
            serviceKeywordVO.setCreatedBy(userVO.getUserID());
            //serviceKeywordVO.setCreatedOn(new Date());
            serviceKeywordVO.setModifiedBy(null);
            serviceKeywordVO.setModifiedOn(new Date());
            serviceKeywordVO.setLastModifiedTime(0l);


            if (log.isDebugEnabled()) {
            	log.debug("constructVOFromForm", "Exiting:serviceKeywordVO=" + serviceKeywordVO);
            }
            return serviceKeywordVO;
        }

        
        

		@Override
		public ServiceKeywordResp searchServiceKeywordbyID(Connection con, String servicekeywordID)
				throws BTSLBaseException {
			
			ServiceKeywordResp  serviceKeywordResp = new ServiceKeywordResp();
			ServiceKeywordDAO serviceKeywordDAO = new ServiceKeywordDAO();
			ServiceKeywordVO serviceKeywordVO =  serviceKeywordDAO.fetchServicekeywordByID(con, servicekeywordID);
		      if (null == serviceKeywordVO) {
		    		throw new BTSLBaseException("ServiceKeywordServiceImpl", "searchServiceKeywordbyID",
							PretupsErrorCodesI.NO_RECORDS_FOUND, 0, null); 
			   }
			
		      serviceKeywordResp.setServiceKeywordVO(serviceKeywordVO);
			return serviceKeywordResp;
		}
		
		

		
		@Override
		public ModifyServiceKeywordResp modifyServiceKeyword(Connection con,ModifyServiceKeywordReq modifyServiceKeywordReq ,UserVO userVO,Locale locale)
				throws BTSLBaseException {
			
			final String methodName ="modifyServiceKeyword";
			
			StringBuffer uniqueServiceKeywordID = null;
			ServiceKeywordVO serviceKeywordVO = null;
	        Date currentDate = new Date();
	        int addCount;	        uniqueServiceKeywordID = new StringBuffer();
	        CommonUtil commutil = new CommonUtil();
	        String idType = PretupsI.SERVICE_KEYWORD_ID;
	        ModifyServiceKeywordResp modifyServiceKeywordResp = new ModifyServiceKeywordResp();
	        serviceKeywordVO = modifyconstructVOFromForm(modifyServiceKeywordReq,userVO);
	        
	        serviceKeywordVO.setServiceKeywordID(modifyServiceKeywordReq.getServiceKeywordID());
	        serviceKeywordVO.setCreatedOn(currentDate);
	        serviceKeywordVO.setModifiedOn(currentDate);
	        serviceKeywordVO.setCreatedBy(userVO.getUserID());
	        serviceKeywordVO.setModifiedBy(userVO.getUserID());
	        ServiceKeywordDAO serviceKeywordDAO = new ServiceKeywordDAO();
	        
	        ServiceTypeobjVO serviceTypeobjVO =    serviceKeywordDAO.getServiceTypeDetails(con, modifyServiceKeywordReq.getServiceType());
	       GetServiceKeywordListResp    getServiceKeywordListResp = searchServiceKeywordbyServiceType(con, modifyServiceKeywordReq.getServiceType());
	       Map mp =null;
	    
	        
	        if(!BTSLUtil.isNullString(modifyServiceKeywordReq.getMessageGatewayType()) &&  modifyServiceKeywordReq.getMessageGatewayType().length()>10 ) {
	        	throw new BTSLBaseException(this, methodName, PretupsI.SERVICE_KEYWORD_MESSAGEGATEWAY_LENGTH_EXCEEDED);
	        }	
	        
	   	    if(BTSLUtil.isNullString(modifyServiceKeywordReq.getReceivePort())) {
	        	throw new BTSLBaseException(this, "validatePortSeries", PretupsI.SUBKEYWORD_BLANK_VALUE_PORT);
	        }else {
	        	commutil.validatePortSeries(modifyServiceKeywordReq.getReceivePort());
		     mp=commutil.containsDuplicatePort(modifyServiceKeywordReq.getReceivePort());
	        }
	    	//validatePortAlreadyUsedByotherkeyword(getServiceKeywordListResp,mp,modifyServiceKeywordReq.getServiceKeywordID()); 
	   	   	commutil.validateversion(modifyServiceKeywordReq.getAllowedVersion());
	   	 
	        commutil.validateRegex(Constants.getProperty(PretupsI.NAME_REGEX), modifyServiceKeywordReq.getMenu(), PretupsI.LABEL_MENU, PretupsI.DESC_ALPHA_NUMERIC);
	        commutil.validateRegex(Constants.getProperty(PretupsI.NAME_REGEX), modifyServiceKeywordReq.getName(), PretupsI.LABEL_NAME, PretupsI.DESC_ALPHA_NUMERIC_SPACE);
	        
	        if(PretupsI.YES.equalsIgnoreCase(serviceTypeobjVO.getSubKeyWordApplicable())){
	        	  serviceKeywordVO.setSubKeywordApplicable(true);
	        	  if(BTSLUtil.isNullString(modifyServiceKeywordReq.getSubKeyWord())){
	        		  throw new BTSLBaseException(this, methodName, PretupsI.SUBKEYWORD_INPUT_MANDATORY);
	        	  }
	        }
	        
	        // checking that is record already exists with the new key or not.
	        // last parameter to indicate that donot check the
	        // service_keyword_id
	        if (serviceKeywordDAO.isServiceKeywordExist(con, serviceKeywordVO, true)) {
	            if (log.isDebugEnabled()) {
	            	log.debug(methodName, "isServiceKeywordExist=true");
	            }

//	            // CR 000009 Forward to appropraite error page if sub keyword or
//	            // keyword is already modified
	            	if (serviceKeywordVO.isSubKeywordApplicable()) {
	            		throw new BTSLBaseException(this, methodName, PretupsI.SUBKEYWORD_ALREADY_EXIST, methodName);
	            	}
	            throw new BTSLBaseException(this, methodName, PretupsI.SERVICEKEYWORD_ALREADY_EXIST, methodName);
	        } else {
	            addCount = serviceKeywordDAO.updateServiceType(con, serviceKeywordVO);
	            if (con != null) {
	                if (addCount > 0) {
	                	try {
	                	con.commit();
	                	}catch(SQLException se) {
	                		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SERVICEKEYWORD_MODIFY_UNSUCCESS, methodName);
	                	}
	                	
	                	String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
	                	modifyServiceKeywordResp.setStatus(success);
	                	modifyServiceKeywordResp.setMessageCode(PretupsErrorCodesI.SERVICEKEYWORD_MODIFY_SUCCESS);
	        			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SERVICEKEYWORD_MODIFY_SUCCESS, null);
	        			modifyServiceKeywordResp.setMessage(resmsg);
	                	
	                	
	                    // log the data in adminOperationLog.log
	                    AdminOperationVO adminOperationVO = new AdminOperationVO();
	                    adminOperationVO.setSource(TypesI.LOGGER_SERVICE_KEYWORD_SOURCE);
	                    adminOperationVO.setDate(currentDate);
	                    adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
						String subkeywordDispalylog="";
						if(serviceKeywordVO.getSubKeyword()!=null){
							subkeywordDispalylog="Sub keyword (" + serviceKeywordVO.getSubKeyword() + " )";
						}
						adminOperationVO.setInfo("Service keyword (" + serviceKeywordVO.getKeyword() + ")" + subkeywordDispalylog+ " has modified successfully");
	                    adminOperationVO.setLoginID(userVO.getLoginID());
	                    adminOperationVO.setUserID(userVO.getUserID());
	                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
	                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
	                    adminOperationVO.setMsisdn(userVO.getMsisdn());
	                    AdminOperationLog.log(adminOperationVO);

	                    

	                } else {
	                	try {
	                	con.rollback();
	                	}catch(SQLException se) {
	                		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SERVICEKEYWORD_MODIFY_UNSUCCESS,methodName);
	                	}
	                	
	                	String success = Integer.toString(PretupsI.RESPONSE_FAIL);
	                	modifyServiceKeywordResp.setStatus(success);
	                	modifyServiceKeywordResp.setMessageCode(PretupsErrorCodesI.SERVICEKEYWORD_MODIFY_UNSUCCESS);
	        			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SERVICEKEYWORD_MODIFY_UNSUCCESS, null);
	        			modifyServiceKeywordResp.setMessage(resmsg);
	                
	                }
	            
	            }
	            
	        }
	            
			return modifyServiceKeywordResp;
			
			
		}
		
		
	private void  validatePortAlreadyUsedByotherkeyword(GetServiceKeywordListResp getServiceKeywordListResp,Map inputPortMap,String keywordID) throws BTSLBaseException {
		final String methodName ="validatePortAlreadyUsedByotherkeyword";
		//keywordID not null incase of modify keyword
		Map dbdataMap =null;
		String alreadyUsePort=null;
		CommonUtil commutil = new CommonUtil();
	    if(getServiceKeywordListResp!=null && getServiceKeywordListResp.getListServiceListObj()!=null && !getServiceKeywordListResp.getListServiceListObj().isEmpty()) {
  	    	 for( ServiceKeywordVO serviceKeywordObj :getServiceKeywordListResp.getListServiceListObj()) {
  	    		 if(!serviceKeywordObj.getServiceKeywordID().equals(keywordID)) {
	  	    		dbdataMap= commutil.containsDuplicatePort(serviceKeywordObj.getReceivePort());
	  	    	     	alreadyUsePort= commutil.compareMap1PortExistInDestMap(inputPortMap,dbdataMap);
	  	    	     	if(alreadyUsePort!=null) {
	  	    	     	String paramPort[] = { alreadyUsePort };   	
	  	    				throw new BTSLBaseException(this, methodName, PretupsI.SUBKEYWORD_PORT_ALREADY_USED, paramPort);
	  	    	 
	  	    	     	}
	  	    		}
  	    	 }
  	    		 
      	 }
		
	}
 
		@Override
		public DeleteServiceKeywordResp deleteServiceKeywordbyID(Connection con, String servicekeywordID,UserVO userVO)
					throws BTSLBaseException {
			final String methodName ="deleteServiceKeywordbyID";
			DeleteServiceKeywordResp  deleteserviceKeywordResp = new DeleteServiceKeywordResp();
			ServiceKeywordDAO serviceKeywordDAO = new ServiceKeywordDAO();
			ServiceKeywordVO serviceKeywordVO  = new ServiceKeywordVO ();
			serviceKeywordVO.setStatus(PretupsI.NO);
			serviceKeywordVO.setServiceKeywordID(servicekeywordID);
			serviceKeywordVO.setModifiedOn(new Date());
			serviceKeywordVO.setModifiedBy(userVO.getUserID());
			ServiceKeywordResp serviceKeywordResp = searchServiceKeywordbyID(con,servicekeywordID);
			Date currentDate = new Date();
			
			int res =  serviceKeywordDAO.deleteServiceType(con, serviceKeywordVO);
			if (con != null) {
                if (res > 0) {
                	try {
                	con.commit();
                	}catch(SQLException se) {
                		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SERVICEKEYWORD_DELETE_UNSUCCESS, methodName);
                	}
                }
                
                
                AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(TypesI.LOGGER_SERVICE_KEYWORD_SOURCE);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
                
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
               if (res==0) {
				   String subkeywordDispalylog="";
				   if(serviceKeywordResp.getServiceKeywordVO().getSubKeyword()!=null){
					   subkeywordDispalylog="Sub keyword (" + serviceKeywordResp.getServiceKeywordVO().getSubKeyword() + " )";
				   }
				   adminOperationVO.setInfo("Service keyword (" + serviceKeywordResp.getServiceKeywordVO().getKeyword() + ")" + subkeywordDispalylog+ " deletion unsuccessful.");
		    		throw new BTSLBaseException(this, methodName,
							PretupsErrorCodesI.SERVICEKEYWORD_DELETE_UNSUCCESS, 0, null); 
		      }	else {
				   String subkeywordDispalylog="";
				   if(serviceKeywordResp.getServiceKeywordVO().getSubKeyword()!=null){
					   subkeywordDispalylog="Sub keyword (" + serviceKeywordResp.getServiceKeywordVO().getSubKeyword() + " )";
				   }
				   adminOperationVO.setInfo("Service keyword (" + serviceKeywordResp.getServiceKeywordVO().getKeyword() + ")" + subkeywordDispalylog+ " has deleted successfully");
		    	  String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
		    	  deleteserviceKeywordResp.setStatus(success);
		    	  deleteserviceKeywordResp.setMessageCode(PretupsErrorCodesI.SERVICEKEYWORD_DELETE_SUCCESS);
					String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.SERVICEKEYWORD_DELETE_SUCCESS, null);
					deleteserviceKeywordResp.setMessage(resmsg);
		      }
               AdminOperationLog.log(adminOperationVO);
		      
		      
    }			  
			
			return deleteserviceKeywordResp;		
			}	        

		
  

}
