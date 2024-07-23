package com.restapi.networkadmin.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

////import org.apache.struts.action.ActionForward;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.SelectorAmountMappingDAO;
import com.btsl.pretups.master.businesslogic.SelectorAmountMappingVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.requestVO.AddServiceProductAmountMappingRequestVO;
import com.restapi.networkadmin.responseVO.AddServiceProductAmountDetailsResponseVO;
import com.restapi.networkadmin.responseVO.LoadServiceAndProductListResponseVO;
import com.restapi.networkadmin.responseVO.SelectorServiceProductAmountDetailsResponseVO;
import com.restapi.networkadmin.responseVO.ServiceProductAmountDetailsVO;
import com.restapi.networkadmin.serviceI.ServiceProductAmountMappingServiceI;
@Service("ServiceProductAmountMappingServiceI")
public class ServiceProductAmountMappingServiceImpl implements ServiceProductAmountMappingServiceI{
	public static final Log LOG = LogFactory.getLog(ServiceProductAmountMappingServiceImpl.class.getName());
	public static final String CLASS_NAME = "ServiceProductAmountMappingServiceImpl";
	@Override
	public SelectorServiceProductAmountDetailsResponseVO loadSelectorAmountDetails(Connection con, UserVO userVO)
			throws BTSLBaseException, Exception {
		 final String methodName = "loadSelectorAmountDetails";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered");
	        }
	       SelectorServiceProductAmountDetailsResponseVO response = new SelectorServiceProductAmountDetailsResponseVO();
		
	       List<ServiceProductAmountDetailsVO> serviceProductAmountDetailsVOList = new ArrayList<>();
        	
           List<SelectorAmountMappingVO> selectorAmountList = new SelectorAmountMappingDAO().loadSelectorAmountList(con);
           for(SelectorAmountMappingVO mappingVO: selectorAmountList) {
        	   ServiceProductAmountDetailsVO productAmountDetailsVO = new ServiceProductAmountDetailsVO();
        	   productAmountDetailsVO.setProductId(mappingVO.getSelectorCode());
        	   productAmountDetailsVO.setProductName(mappingVO.getSelectorName());
          	   productAmountDetailsVO.setServiceId(mappingVO.getServiceType());
        	   productAmountDetailsVO.setServiceName(mappingVO.getServiceName());
        	   productAmountDetailsVO.setAmount((mappingVO.getAmountStr()));
        	   productAmountDetailsVO.setModifiedAllowed(mappingVO.getModifiedAllowed());
        	   productAmountDetailsVO.setStatus(mappingVO.getStatus());
        	   serviceProductAmountDetailsVOList.add(productAmountDetailsVO);

           }
           response.setServiceProductAmountDetailsList(serviceProductAmountDetailsVOList);
           
            if (LOG.isDebugEnabled()) {
                LOG.debug(CLASS_NAME, "Exiting:forward= "+methodName );
            }

		return response;
	}
	

		@Override
		public AddServiceProductAmountDetailsResponseVO addServiceProductAmountMappingDetails(Connection con,
				UserVO userVO, AddServiceProductAmountMappingRequestVO requestVO) throws BTSLBaseException, Exception {
			 final String methodName = "addServiceProductAmountMappingDetails";

			if (LOG.isDebugEnabled()) {
		            LOG.debug(methodName, "Entered");
		        }
			AddServiceProductAmountDetailsResponseVO response = new AddServiceProductAmountDetailsResponseVO();
			SelectorAmountMappingVO amountMappingVO = null;
	        SelectorAmountMappingDAO amountMappingDAO = null;
	        //ActionForward forward = null;
	        amountMappingVO = new SelectorAmountMappingVO();
	        Date currentDate = new Date();
	        amountMappingVO.setAmount(PretupsBL.getSystemAmount(requestVO.getAmount()));
	        if (requestVO.getProductId().split(":").length == 2) {
	        	amountMappingVO.setSelectorCode(requestVO.getProductId().split(":")[1]);
	        } else {
	        	amountMappingVO.setSelectorCode(requestVO.getProductId());
	        }
	        amountMappingVO.setServiceName(requestVO.getServiceName());
	        amountMappingVO.setServiceType(requestVO.getServiceId());
	        amountMappingVO.setStatus(PretupsI.YES);
	        amountMappingVO.setModifiedAllowed(requestVO.getModifyAllowed());
	        amountMappingVO.setSelectorName(requestVO.getProductName());
	        amountMappingVO.setCreatedOn(currentDate);
	        amountMappingVO.setCreatedBy(userVO.getUserID());
	        amountMappingVO.setModifiedOn(currentDate);
	        amountMappingVO.setModifiedBy(userVO.getUserID());


	        amountMappingDAO = new SelectorAmountMappingDAO();
	        if (amountMappingDAO.isSelectorAmountDetailsExist(con, amountMappingVO)) {
	                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SELECTED_PRODUCT_AMOUNT_MAPPING_ALREADY_EXIST, "");
	        } else {
	            int insertCount = amountMappingDAO.addSelectorAmountDetails(con, amountMappingVO);
	            
	        if (insertCount != 0) {
	                        /*con.commit();*/
	                    	con.commit(); 
	                    	// Enter the details for add domain on Admin Log
	                        AdminOperationVO adminOperationVO = new AdminOperationVO();
	                        adminOperationVO.setDate(currentDate);
	                        adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
	                        adminOperationVO.setLoginID(userVO.getLoginID());
	                        adminOperationVO.setUserID(userVO.getUserID());
	                        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
	                        adminOperationVO.setNetworkCode(userVO.getNetworkID());
	                        adminOperationVO.setMsisdn(userVO.getMsisdn());
	                        adminOperationVO.setSource(TypesI.LOGGER_SERVICE_PRODUCT_AMOUNT_MAPPING);
	                        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
	                        String arr[]= {requestVO.getProductName(), requestVO.getServiceName()};
	                		String resmsg = RestAPIStringParser.getMessage(locale,
	                				PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_DOMAIN_ADDED_SUCCESSFULLY, arr);
	                        adminOperationVO.setInfo(resmsg);
	                        AdminOperationLog.log(adminOperationVO);
	         } else {
	                        /*con.rollback();*/
	                    	con.rollback();
	                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.FAILED_ADD_SERVICE_PRODUCT_AMOUNT_MAPPING_DETAILS, "");
	         }
	        }
			  if (LOG.isDebugEnabled()) {
		            LOG.debug(methodName, "Exited");
		        }
			return response;
		}
		
		
	    public int modifyServiceProductAmountMapping(Connection con,UserVO userVO, AddServiceProductAmountMappingRequestVO request) throws BTSLBaseException,Exception {
	        final String METHOD_NAME = "modifyServiceProductMapping";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(METHOD_NAME, "Entered");
	        }
	           	SelectorAmountMappingDAO selectorAmountMappingDAO= new SelectorAmountMappingDAO();
	            List<SelectorAmountMappingVO> serviceProductAmountMappingList =selectorAmountMappingDAO.loadSelectorAmountList(con);
	            SelectorAmountMappingVO updatedAmountMappingVO = new SelectorAmountMappingVO();
	            Date currentDate = new Date();
	           	for(SelectorAmountMappingVO amountMappingVO : serviceProductAmountMappingList) {
	           		if(amountMappingVO.getSelectorCode().equals(request.getProductId()) && amountMappingVO.getServiceType().equals(request.getServiceId()) ){
	           			if (amountMappingVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.NO)) {
	           				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_IS_NOT_ALLOWED_TO_MODIFY, "");
	           			} else {
	           				updatedAmountMappingVO.setSelectorName(amountMappingVO.getSelectorName());
	           				updatedAmountMappingVO.setSelectorCode(amountMappingVO.getSelectorCode());
	           				updatedAmountMappingVO.setAmount(PretupsBL.getSystemAmount((request.getAmount())));
	           				updatedAmountMappingVO.setModifiedAllowed(request.getModifyAllowed());
	           				updatedAmountMappingVO.setServiceType(amountMappingVO.getServiceType());
	           				updatedAmountMappingVO.setModifiedOn(currentDate);
	           				updatedAmountMappingVO.setModifiedBy(userVO.getUserID());
	                	}
	           		}
	           	}
	        int updateCount = selectorAmountMappingDAO.updateSelectorAmountMapping(con, updatedAmountMappingVO);
            if (updateCount > 0) {
                /* con.commit();*/
             	con.commit();
                 // Enter the details for add domain on Admin Log
                 AdminOperationVO adminOperationVO = new AdminOperationVO();
                 adminOperationVO.setDate(currentDate);
                 adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                 adminOperationVO.setLoginID(userVO.getLoginID());
                 adminOperationVO.setUserID(userVO.getUserID());
                 adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                 adminOperationVO.setNetworkCode(userVO.getNetworkID());
                 adminOperationVO.setMsisdn(userVO.getMsisdn());
                 adminOperationVO.setSource(TypesI.LOGGER_SERVICE_PRODUCT_AMOUNT_MAPPING);
                 Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
                 String arr[]= {request.getProductName(), request.getServiceName()};
         		String resmsg = RestAPIStringParser.getMessage(locale,
         				PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_DOMAIN_MODIFIED_SUCCESSFULLY, arr);
                 adminOperationVO.setInfo(resmsg);
                 AdminOperationLog.log(adminOperationVO);
             } else {
                /* con.rollback();*/
             	con.rollback();
                 throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_MODIFY_FAILED,"");

             }

	            if (LOG.isDebugEnabled()) {
	                LOG.debug(METHOD_NAME, "Exiting" + METHOD_NAME);
	            }
	        
	        return updateCount;
	    }


		@Override
		public int deleteServiceProductAmountMapping(Connection con, UserVO userVO, String serviceId, String productId)
				throws BTSLBaseException, Exception {
			 final String METHOD_NAME = "deleteServiceProductAmountMapping";

				if (LOG.isDebugEnabled()) {
			            LOG.debug(METHOD_NAME, "Entered");
			        }
				int deleteCount =0;
				SelectorAmountMappingDAO selectorAmountMappingDAO = new SelectorAmountMappingDAO();
				 List<SelectorAmountMappingVO> serviceProductAmountMappingList =selectorAmountMappingDAO.loadSelectorAmountList(con);
				for(SelectorAmountMappingVO amountMappingVO : serviceProductAmountMappingList) {
	           		if(amountMappingVO.getSelectorCode().equals(productId) && amountMappingVO.getServiceType().equals(serviceId) ){
			                    Date currentDate = new Date();
			 
			                    amountMappingVO.setModifiedOn(currentDate);
			                    amountMappingVO.setModifiedBy(userVO.getUserID());
			                    deleteCount = selectorAmountMappingDAO.deleteSelectorAmountMapping(con, amountMappingVO);
			                    if (deleteCount > 0) {
			                       /* con.commit();*/
			                    	con.commit();
			                        // Enter the details for add domain on Admin Log
			                        AdminOperationVO adminOperationVO = new AdminOperationVO();
			                        adminOperationVO.setDate(currentDate);
			                        adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
			                        adminOperationVO.setLoginID(userVO.getLoginID());
			                        adminOperationVO.setUserID(userVO.getUserID());
			                        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
			                        adminOperationVO.setNetworkCode(userVO.getNetworkID());
			                        adminOperationVO.setMsisdn(userVO.getMsisdn());
			                        adminOperationVO.setSource(TypesI.LOGGER_SERVICE_PRODUCT_AMOUNT_MAPPING);
			                        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			                        String arr[]= {amountMappingVO.getSelectorName(), amountMappingVO.getServiceName()};
			                		String resmsg = RestAPIStringParser.getMessage(locale,
			                				PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_DOMAIN_DELETED_SUCCESSFULLY, arr);
			                        adminOperationVO.setInfo(resmsg);
			                        AdminOperationLog.log(adminOperationVO);
			                    } else {
			                       /* con.rollback();*/
			                    	con.rollback();
			                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_DELETE_FAILED,"");

			                    }
			                }
			            } 			        
				 if (LOG.isDebugEnabled()) {
		                LOG.debug(METHOD_NAME, "Exiting" + METHOD_NAME);
		            }
		        
			return deleteCount;
		}

	    public LoadServiceAndProductListResponseVO  loadServiceAndProductList(Connection con, UserVO userVO)throws BTSLBaseException, Exception {
	        final String METHOD_NAME = "loadServiceAndProductList";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(METHOD_NAME, "Entered");
	        }
	        LoadServiceAndProductListResponseVO response = new LoadServiceAndProductListResponseVO();
	       

	        response.setProductList(ServiceSelectorMappingCache.loadSelectorDropDownForCardGroup());
	            
	            response.setServiceList(new SelectorAmountMappingDAO().loadServiceTypeList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE));
	            if (response.getServiceList().isEmpty()) {
	                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_SERVICE_EXISTS);
	            }
	            
	       
	            if (LOG.isDebugEnabled()) {
	                LOG.debug(METHOD_NAME, "Exiting");
	            }
	        
	        return response;

	    }


}
