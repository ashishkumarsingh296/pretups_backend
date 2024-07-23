package com.restapi.networkadmin.servicePrdMapping.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
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
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingDAO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.networkadmin.servicePrdMapping.responseVO.SearchServicePrdMappingRespVO;
import com.restapi.networkadmin.servicePrdMapping.responseVO.ServicePrdInputRespVO;
import com.restapi.superadmin.service.CategoryManagementServiceImpl;


@Service
public class ServicePrdMappingInfImpl implements ServicePrdMappingIntf{
	protected static final Log log= LogFactory.getLog(ServicePrdMappingInfImpl.class.getName());

	@Override
	public ServicePrdInputRespVO getServicePrdMappingUIInputValues(UserVO userVO, Locale locale)
			throws BTSLBaseException {
		final String methodName ="getServicePrdMappingUIInputValues";
		ServicePrdInputRespVO servicePrdInputRespVO = new ServicePrdInputRespVO();
		
		
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			
			if(!BTSLUtil.isNullObject(userVO)) {
				servicePrdInputRespVO.setSelectorList(ServiceSelectorMappingCache.loadSelectorDropDownForCardGroup());
				servicePrdInputRespVO.setServiceTypeList(new SelectorAmountMappingDAO().loadServiceTypeList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE));
			
				servicePrdInputRespVO.setMessageCode(PretupsI.SUCCESS);
				servicePrdInputRespVO.setMessage(PretupsI.SUCCESS);
				servicePrdInputRespVO.setStatus(HttpStatus.SC_OK);
			}else {
				servicePrdInputRespVO.setMessageCode(PretupsErrorCodesI.NO_USER_EXIST_LOGINID);
				servicePrdInputRespVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_USER_EXIST_LOGINID, null));
				servicePrdInputRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
    	
			}

		} catch (SQLException se) {
			servicePrdInputRespVO.setMessageCode(PretupsI.FAIL);
			servicePrdInputRespVO.setMessage(PretupsI.FAIL);
			servicePrdInputRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
		} catch (Exception e) {
			servicePrdInputRespVO.setMessageCode(PretupsI.FAIL);
			servicePrdInputRespVO.setMessage(PretupsI.FAIL);
			servicePrdInputRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
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
							PretupsI.SERVICEPRDMAPPING_ERROR_CLOSE_CONNECTION, se);
				}

		}	
		

		
		
		
		
		return servicePrdInputRespVO;	
	}

	@Override
	public SearchServicePrdMappingRespVO searchServicePrdMapping(String serviceType,String selectorCode,UserVO userVO, Locale locale)
			throws BTSLBaseException {
		
		final String methodName ="searchServicePrdMapping";
		SearchServicePrdMappingRespVO servicePrdInputRespVO = new SearchServicePrdMappingRespVO();
		ArrayList<SelectorAmountMappingVO> selectorAmountList= null;
		
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
				selectorAmountList = new SelectorAmountMappingDAO().loadSelectorAmountDetailsbyServiceType(con,selectorCode,serviceType);
				
				if( !BTSLUtil.isNullOrEmptyList(selectorAmountList) ) {
			
				servicePrdInputRespVO.setSearchServicePrdlist(selectorAmountList);
	           servicePrdInputRespVO.setMessageCode(PretupsI.SUCCESS);
				servicePrdInputRespVO.setMessage(PretupsI.SUCCESS);
				servicePrdInputRespVO.setStatus(HttpStatus.SC_OK);
				}else {
				   servicePrdInputRespVO.setMessageCode(PretupsI.NO_SERVICE_PRD_MAPPING_FOUND);
					servicePrdInputRespVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.NO_SERVICE_PRD_MAPPING_FOUND, null) );
					servicePrdInputRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
	    		}
			
		} catch (SQLException se) {
			servicePrdInputRespVO.setMessageCode(PretupsI.SERVICEPRDMAPPING_SEARCH_FAILED);
			servicePrdInputRespVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SERVICEPRDMAPPING_SEARCH_FAILED, null) );
			servicePrdInputRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
		} catch (Exception e) {
			servicePrdInputRespVO.setMessageCode(PretupsI.SERVICEPRDMAPPING_SEARCH_FAILED);
			servicePrdInputRespVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SERVICEPRDMAPPING_SEARCH_FAILED, null) );
			servicePrdInputRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
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
		
		return servicePrdInputRespVO;
	}

	@Override
	public BaseResponse saveServicePrdMapping(SelectorAmountMappingVO selectorAmountMappingVO, UserVO userVO,
			Locale locale) throws BTSLBaseException {
		final String methodName ="saveServicePrdMapping";
		BaseResponse response = new BaseResponse();
		ArrayList<SelectorAmountMappingVO> selectorAmountList= null;
		
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		SelectorAmountMappingDAO amountMappingDAO=null;
		ServiceSelectorMappingDAO serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();
		ServiceKeywordDAO serviceKeywordDAO = new ServiceKeywordDAO();
		String selectorName =null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			   amountMappingDAO = new SelectorAmountMappingDAO();
			   
			if(!serviceKeywordDAO.validateServiceType(con, selectorAmountMappingVO.getServiceType())) {
				throw new BTSLBaseException(this, methodName,
						PretupsI.SERVICEPRDMAPPING_INVALID_SERVICETYPE);
			}
			   
	        if (amountMappingDAO.isSelectorAmountDetailsExist(con, selectorAmountMappingVO)) {
                throw new BTSLBaseException(this, methodName,
						PretupsI.SERVICEPRDMAPPING_SAVE_ALREADYEXIST);
            } else {
            	
            HashMap<String, ServiceSelectorMappingVO> mp =	serviceSelectorMappingDAO.loadServiceTypeSelectorMapWithparmeter(selectorAmountMappingVO.getServiceType(),selectorAmountMappingVO.getSelectorCode());
            ServiceSelectorMappingVO serviceSelectorMappingVO=   mp.get(selectorAmountMappingVO.getServiceType()+"_"+selectorAmountMappingVO.getSelectorCode());
            selectorAmountMappingVO.setSelectorName(serviceSelectorMappingVO.getSelectorName());
           
                int insertCount = amountMappingDAO.addSelectorAmountDetails(con, selectorAmountMappingVO);

                if (insertCount != 0) {
                    /*con.commit();*/
                	mcomCon.finalCommit();
                    //BTSLMessages btslMessage = new BTSLMessages("master.selectoramountmapping.add.details.success.msg", "startpage");
                	response.setStatus(HttpStatus.SC_OK);
                	response.setMessageCode(PretupsI.SERVICEPRDMAPPING_SAVE_SUCCESS);
                	response.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SERVICEPRDMAPPING_SAVE_SUCCESS, null) );
                    
                } else {
                    /*con.rollback();*/
                	mcomCon.finalRollback();
                    //throw new BTSLBaseException(this, "save", "master.selectoramountmapping.add.details.nosuccess.msg", "viewdetails");
                	response.setStatus(HttpStatus.SC_BAD_REQUEST);
                    response.setMessageCode(PretupsI.SERVICEPRDMAPPING_SAVE_FAILED);
                	response.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SERVICEPRDMAPPING_SAVE_FAILED, null) );
                }

            }
			
		} catch (SQLException se) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setMessageCode(PretupsI.SERVICEPRDMAPPING_SAVE_FAILED);
        	response.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SERVICEPRDMAPPING_SAVE_FAILED, null) );
		} catch (BTSLBaseException be) {
		log.error(methodName, "Exception:e=" + be);
		log.errorTrace(methodName, be);
		if (!BTSLUtil.isNullString(be.getMessage())) {
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
			response.setMessageCode(be.getMessage());
			response.setMessage(msg);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

	} catch (Exception e) {
		response.setStatus(HttpStatus.SC_BAD_REQUEST);
        response.setMessageCode(PretupsI.SERVICEPRDMAPPING_SAVE_FAILED);
    	response.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SERVICEPRDMAPPING_SAVE_FAILED, null) );

		log.error(methodName, "Exception: " + e.getMessage());
		log.errorTrace(methodName, e);
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
							PretupsI.SERVICEPRDMAPPING_ERROR_CLOSE_CONNECTION, se);
				}

		}
		


		return response;
	}
	
	

	
	@Override
	public BaseResponse modifyServicePrdMapping(SelectorAmountMappingVO selectorAmountMappingVO, UserVO userVO,
			Locale locale) throws BTSLBaseException {
		final String methodName ="searchServicePrdMapping";
		BaseResponse response = new BaseResponse();
		ArrayList<SelectorAmountMappingVO> selectorAmountList= null;
		
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		SelectorAmountMappingDAO amountMappingDAO=null;
		ServiceKeywordDAO serviceKeywordDAO = new ServiceKeywordDAO();
		ServiceSelectorMappingDAO serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();
		String selectorName =null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			   amountMappingDAO = new SelectorAmountMappingDAO();
		   if(!serviceKeywordDAO.validateServiceType(con, selectorAmountMappingVO.getServiceType())) {
				throw new BTSLBaseException(this, methodName,
						PretupsI.SERVICEPRDMAPPING_INVALID_SERVICETYPE);
			}   
            if (!amountMappingDAO.isSelectorAmountDetailsExist(con, selectorAmountMappingVO)) {
                throw new BTSLBaseException(this, methodName,
						PretupsI.SERVICEPRDMAPPING_MODIFY_DETNOTEXIST);
            } else {
            	
            HashMap<String, ServiceSelectorMappingVO> mp =	serviceSelectorMappingDAO.loadServiceTypeSelectorMapWithparmeter(selectorAmountMappingVO.getServiceType(),selectorAmountMappingVO.getSelectorCode());
            ServiceSelectorMappingVO serviceSelectorMappingVO=   mp.get(selectorAmountMappingVO.getServiceType()+"_"+selectorAmountMappingVO.getSelectorCode());
            selectorAmountMappingVO.setSelectorName(serviceSelectorMappingVO.getSelectorName());
             int updateCount = amountMappingDAO.updateSelectorAmountMapping(con, selectorAmountMappingVO);

                if (updateCount != 0) {
                    /*con.commit();*/
                	mcomCon.finalCommit();
                    //BTSLMessages btslMessage = new BTSLMessages("master.selectoramountmapping.add.details.success.msg", "startpage");
                	response.setStatus(HttpStatus.SC_OK);
                	response.setMessageCode(PretupsI.SERVICEPRDMAPPING_MODIFY_SUCCESS);
                	response.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SERVICEPRDMAPPING_MODIFY_SUCCESS, null) );
                    
                } else {
                    /*con.rollback();*/
                	mcomCon.finalRollback();
                    //throw new BTSLBaseException(this, "save", "master.selectoramountmapping.add.details.nosuccess.msg", "viewdetails");
                	response.setStatus(HttpStatus.SC_BAD_REQUEST);
                    response.setMessageCode(PretupsI.SERVICEPRDMAPPING_MODIFY_FAILED);
                	response.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SERVICEPRDMAPPING_MODIFY_FAILED, null) );
                }

            }
			
		} catch (SQLException se) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setMessageCode(PretupsI.SERVICEPRDMAPPING_MODIFY_FAILED);
        	response.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SERVICEPRDMAPPING_MODIFY_FAILED, null) );
		} catch (BTSLBaseException be) {
		log.error(methodName, "Exception:e=" + be);
		log.errorTrace(methodName, be);
		if (!BTSLUtil.isNullString(be.getMessage())) {
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
			response.setMessageCode(be.getMessage());
			response.setMessage(msg);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

	} catch (Exception e) {
		response.setStatus(HttpStatus.SC_BAD_REQUEST);
        response.setMessageCode(PretupsI.SERVICEPRDMAPPING_MODIFY_FAILED);
    	response.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SERVICEPRDMAPPING_MODIFY_FAILED, null) );

		log.error(methodName, "Exception: " + e.getMessage());
		log.errorTrace(methodName, e);
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
							PretupsI.SERVICEPRDMAPPING_ERROR_CLOSE_CONNECTION, se);
				}

		}
		


		return response;
	}
	
	

	@Override
	public BaseResponse deleteServicePrdMapping(SelectorAmountMappingVO selectorAmountMappingVO, UserVO userVO,
			Locale locale) throws BTSLBaseException {
		final String methodName ="deleteServicePrdMapping";
		BaseResponse response = new BaseResponse();
		ArrayList<SelectorAmountMappingVO> selectorAmountList= null;
		
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		SelectorAmountMappingDAO amountMappingDAO=null;
		ServiceSelectorMappingDAO serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();
		ServiceKeywordDAO serviceKeywordDAO = new ServiceKeywordDAO();
		String selectorName =null;
		Date currentDate=null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			currentDate=new Date();
			   amountMappingDAO = new SelectorAmountMappingDAO();
			   
		   if(!serviceKeywordDAO.validateServiceType(con, selectorAmountMappingVO.getServiceType())) {
				throw new BTSLBaseException(this, methodName,
						PretupsI.SERVICEPRDMAPPING_INVALID_SERVICETYPE);
			}   
			   
			   
            if (!amountMappingDAO.isSelectorAmountDetailsExist(con, selectorAmountMappingVO)) {
                throw new BTSLBaseException(this, methodName,
						PretupsI.SERVICEPRDMAPPING_DELETE_NOTEXIST);
            } else {
            	selectorAmountMappingVO.setModifiedBy(userVO.getUserID());
            	selectorAmountMappingVO.setModifiedOn(currentDate);
            	
            	HashMap<String, ServiceSelectorMappingVO> mp =	serviceSelectorMappingDAO.loadServiceTypeSelectorMapWithparmeter(selectorAmountMappingVO.getServiceType(),selectorAmountMappingVO.getSelectorCode());
                ServiceSelectorMappingVO serviceSelectorMappingVO=   mp.get(selectorAmountMappingVO.getServiceType()+"_"+selectorAmountMappingVO.getSelectorCode());
                selectorAmountMappingVO.setSelectorName(serviceSelectorMappingVO.getSelectorName());	
             int updateCount = amountMappingDAO.deleteSelectorAmountMapping(con, selectorAmountMappingVO);

                if (updateCount != 0) {
                    /*con.commit();*/
                	mcomCon.finalCommit();
                    //BTSLMessages btslMessage = new BTSLMessages("master.selectoramountmapping.add.details.success.msg", "startpage");
                	response.setMessageCode(PretupsI.SERVICEPRDMAPPING_DELETE_SUCCESS);
                	response.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SERVICEPRDMAPPING_DELETE_SUCCESS, null) );
                	response.setStatus(HttpStatus.SC_OK);
                	AdminOperationVO adminOperationVO = new AdminOperationVO();
                    adminOperationVO.setDate(currentDate);
                    adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
                    adminOperationVO.setLoginID(userVO.getLoginID());
                    adminOperationVO.setUserID(userVO.getUserID());
                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
                    adminOperationVO.setMsisdn(userVO.getMsisdn());
                    adminOperationVO.setSource(TypesI.LOGGER_DOMAIN_SOURCE);
                    adminOperationVO.setInfo("Service Product " + selectorAmountMappingVO.getSelectorName() + " deleted successfully");
                    AdminOperationLog.log(adminOperationVO);
                    
                } else {
                  mcomCon.finalRollback();
                  response.setStatus(HttpStatus.SC_BAD_REQUEST);
                    response.setMessageCode(PretupsI.SERVICEPRDMAPPING_DELETE_NOTSUCCESS);
                	response.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SERVICEPRDMAPPING_DELETE_NOTSUCCESS, null) );
                }

            }
			
		} catch (SQLException se) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setMessageCode(PretupsI.SERVICEPRDMAPPING_DELETE_NOTSUCCESS);
        	response.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SERVICEPRDMAPPING_DELETE_NOTSUCCESS, null) );
		} catch (BTSLBaseException be) {
		log.error(methodName, "Exception:e=" + be);
		log.errorTrace(methodName, be);
		if (!BTSLUtil.isNullString(be.getMessage())) {
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
			response.setMessageCode(be.getMessage());
			response.setMessage(msg);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

	} catch (Exception e) {
		response.setStatus(HttpStatus.SC_BAD_REQUEST);
        response.setMessageCode(PretupsI.SERVICEPRDMAPPING_DELETE_NOTSUCCESS);
    	response.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.SERVICEPRDMAPPING_DELETE_NOTSUCCESS, null) );

		log.error(methodName, "Exception: " + e.getMessage());
		log.errorTrace(methodName, e);
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
							PretupsI.SERVICEPRDMAPPING_ERROR_CLOSE_CONNECTION, se);
				}

		}
		


		return response;
	}
	


}
