package com.restapi.superadmin.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.superadmin.responseVO.GradeTypeListResponseVO;
import com.restapi.superadmin.serviceI.GradeManagementServiceI;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.domain.businesslogic.DomainWebDAO;

@Service("GradeManagementServiceI")
public class GradeManagementServiceImpl implements GradeManagementServiceI{
	
	public static final Log LOG = LogFactory.getLog(GradeManagementServiceImpl.class.getName());
	public static final String classname = "GradeManagementServiceImpl";

	public GradeTypeListResponseVO getGradeTypeList(Connection con, MComConnectionI mcomCon, Locale locale,
			HttpServletResponse responseSwag,String reqType) throws BTSLBaseException, SQLException  {
		
		final String METHOD_NAME = "getGradeTypeList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		GradeTypeListResponseVO response =null;
		response = new GradeTypeListResponseVO();
		ArrayList<DomainVO> domainList = new ArrayList<DomainVO>();
		ArrayList<ListValueVO> categoryList = new ArrayList<ListValueVO>();
		
        try {
			CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
	        DomainDAO domainDAO = new DomainDAO();
            DomainWebDAO domainWebDAO = new DomainWebDAO();
	        
//	        domainList = domainDAO.loadDomainDetails(con);
	        domainList = domainWebDAO.loadDomainVOList(con);
			if (domainList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.GRADE_MANAGEMENT_DOMAIN_LIST_NOT_FOUND, 0, null);
			} else {
				response.setDomainList(domainList);
			}
	        
//	        categoryList = categoryWebDAO.loadCategoryVOList(con);
			if(null!=reqType && reqType.equalsIgnoreCase(PretupsI.GROUP_ROLE_MGMT_REQUEST)){
				categoryList = categoryWebDAO.loadCategoryListForGroupRole(con, TypesI.NO);
			}else{
				categoryList = categoryWebDAO.loadCategoryListForGradmanagement(con);
			}

			if (categoryList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.GRADE_MANAGEMENT_CATEGORY_LIST_NOT_FOUND, 0, null);
			} else {
				response.setCategoryList(categoryList);
			}
			
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GRADE_MANAGEMENT_DOMAIN_CATEGORY_LIST_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GRADE_MANAGEMENT_DOMAIN_CATEGORY_LIST_FOUND);
	        

        }
        catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.GRADE_MANAGEMENT_DOMAIN_CATEGORY_LIST_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GRADE_MANAGEMENT_DOMAIN_CATEGORY_LIST_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
        
        
        
		return response;
	}

	@Override
	public GradeTypeListResponseVO viewGradeList(Connection con, MComConnectionI mcomCon, Locale locale, String domainCode, String categoryCode,
			HttpServletResponse responseSwag) {
		
		final String METHOD_NAME = "viewGradeList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		GradeTypeListResponseVO response =null;
		response = new GradeTypeListResponseVO();
		
        try {
    		ArrayList<GradeVO> gradeList = new ArrayList<GradeVO>();
    		
            CategoryGradeDAO gradeDAO = new CategoryGradeDAO();
            gradeList = gradeDAO.viewGradeList(con, categoryCode);
            
            if(BTSLUtil.isNullOrEmptyList(gradeList)){
            	LOG.info(this, PretupsErrorCodesI.NO_DATA_FOUND_CRITERIA );
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.NO_DATA_FOUND_CRITERIA );
            }
            
            response.setGradeList(gradeList);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GRADE_LIST_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GRADE_LIST_FOUND);

        }
        catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.GRADE_LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GRADE_LIST_NOT_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
        		
		return response;
	}

	public GradeTypeListResponseVO addGrade(Connection con, MComConnectionI mcomCon, Locale locale,
			ChannelUserVO userVO, String categoryCode, String gradeCode, String gradeName, String defaultGrade,
			HttpServletResponse responseSwag) {
		
		
		final String METHOD_NAME = "addGrade";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		GradeTypeListResponseVO response =null;
		response = new GradeTypeListResponseVO();
		
        int addCount = -1;
        boolean gradeCodeExisting =false;
        boolean gradeNameExisting =false;
        
        
        try {
            CategoryGradeDAO gradeDAO = new CategoryGradeDAO();
            Date currentDate = new Date();
            GradeVO gradeVO = new GradeVO();
            gradeVO.setModifiedOn(currentDate);
            gradeVO.setCreatedOn(currentDate);
            gradeVO.setCreatedBy(userVO.getUserID());
            gradeVO.setModifiedBy(userVO.getUserID());
            gradeVO.setStatus(PretupsI.GRADE_STATUS_ACTIVE);
            gradeVO.setGradeCode(gradeCode);
            gradeVO.setGradeName(gradeName);
            gradeVO.setCategoryCode(categoryCode);
            gradeVO.setDefaultGrade(defaultGrade);
            gradeVO.setTwoFAallowed("N");
            gradeCodeExisting=gradeDAO.isExistsGradeCodeForAdd(con, gradeVO);
            gradeNameExisting =gradeDAO.isExistsGradeNameForAdd(con, gradeVO);
 		   String gradeErrors[] = { gradeCode,gradeName };
            if(gradeCodeExisting && gradeNameExisting) {
            	throw new BTSLBaseException(this, METHOD_NAME, PretupsI.BOTH_GRDCODE_GRDNAME_ALREADYEXISTS,gradeErrors);
            }else if (gradeCodeExisting && !gradeNameExisting) {
            	  throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GRADE_CODE_ALREADY_EXISTS);
            	
            }else if(!gradeCodeExisting && gradeNameExisting) {
            	 throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GRADE_NAME_ALREADY_EXISTS);
            }else {
            	LOG.info(METHOD_NAME, "Validid grade code and grade name");
            }
            
            if (PretupsI.YES.equals(defaultGrade)) {
                gradeDAO.updateGrade(con, gradeVO.getCategoryCode());
            }
            
            addCount = gradeDAO.saveGrade(con, gradeVO);
            if (addCount > 0) {
                mcomCon.finalCommit();
                // Enter the details for Add Grade on Admin Log
                AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(TypesI.LOGGER_GRADE_SOURCE);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                adminOperationVO.setInfo("Grade " + gradeVO.getGradeName() + " added successfully");
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);
				String msg = RestAPIStringParser.getMessage(locale, "domain.addgrade.message.success", null);
                response.setMessage(msg);
                response.setMessageCode("domain.addgrade.message.success");
				response.setStatus((HttpStatus.SC_OK));
            } else {
                mcomCon.finalRollback();
                throw new BTSLBaseException(this, "addGrade", "domain.addgrade.message.notsuccess");
            }

        }
        catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					"domain.addgrade.message.notsuccess", null);
			response.setMessage(resmsg);
			response.setMessageCode("domain.addgrade.message.notsuccess");
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
        
		
		return response;
	}
	
	public GradeTypeListResponseVO modifyGrade(Connection con, MComConnectionI mcomCon, Locale locale,
			ChannelUserVO userVO, String gradeCode, String gradeName, String defaultGrade,
			HttpServletResponse responseSwag) {
		
		
		final String METHOD_NAME = "modifyGrade";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		GradeTypeListResponseVO response =null;
		response = new GradeTypeListResponseVO();
		
        int updateCount = -1;
        
        try {        	
    		ArrayList<GradeVO> gradeList = new ArrayList<GradeVO>();
            CategoryGradeDAO gradeDAO = new CategoryGradeDAO();
            Date currentDate = new Date();
            GradeVO gradeVO = new GradeVO();
            gradeList = gradeDAO.viewGradeListByGradeCode(con, gradeCode);
            gradeVO.setModifiedOn(currentDate);
            gradeVO.setCreatedOn(gradeList.get(0).getCreatedOn());
            gradeVO.setCreatedBy(gradeList.get(0).getCreatedBy());
            gradeVO.setModifiedBy(userVO.getUserID());
            gradeVO.setStatus(gradeList.get(0).getStatus());
            gradeVO.setGradeCode(gradeList.get(0).getGradeCode());
            gradeVO.setGradeName(gradeName);
            gradeVO.setCategoryCode(gradeList.get(0).getCategoryCode());
            gradeVO.setDefaultGrade(defaultGrade);


            if (gradeDAO.isExistsGradeNameForModify(con, gradeVO)) {
            	throw new BTSLBaseException(this, "modifyGrade", "domain.gradename.alreadyexists");
            }
            
            if (PretupsI.YES.equals(gradeVO.getDefaultGrade())) {
                gradeDAO.updateGrade(con, gradeVO.getCategoryCode());
            }
            
            updateCount = gradeDAO.modifyGrade(con, gradeVO);
            if (updateCount > 0) {
                mcomCon.finalCommit();
                // Enter the details for Modify Grade on Admin Log
                AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(TypesI.LOGGER_GRADE_SOURCE);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                adminOperationVO.setInfo("Grade " + gradeVO.getGradeName() + " modified successfully");
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);
				String msg = RestAPIStringParser.getMessage(locale, "domains.modifygrade.success", null);
                response.setMessage(msg);
                response.setMessageCode("domains.modifygrade.success");
				response.setStatus((HttpStatus.SC_OK));
            } else {
                mcomCon.finalRollback();
                throw new BTSLBaseException(this, "modifyGrade", "domains.modifygrade.notsuccess");
            }

        }
        catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					"domains.modifygrade.notsuccess", null);
			response.setMessage(resmsg);
			response.setMessageCode("domains.modifygrade.notsuccess");
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
        
		
		return response;
	}
		
	public GradeTypeListResponseVO deleteGrade(Connection con, MComConnectionI mcomCon, Locale locale,
			ChannelUserVO userVO, String gradeCode,
			HttpServletResponse responseSwag) {
		
		
		final String METHOD_NAME = "deleteGrade";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		GradeTypeListResponseVO response =null;
		response = new GradeTypeListResponseVO();
		
        int deleteCount = -1;
        
        try {        	
    		ArrayList<GradeVO> gradeList = new ArrayList<GradeVO>();
            CategoryGradeDAO gradeDAO = new CategoryGradeDAO();
            Date currentDate = new Date();
            GradeVO gradeVO = new GradeVO();
            gradeList = gradeDAO.viewGradeListByGradeCode(con, gradeCode);
            gradeVO.setModifiedOn(currentDate);
            gradeVO.setCreatedOn(gradeList.get(0).getCreatedOn());
            gradeVO.setCreatedBy(gradeList.get(0).getCreatedBy());
            gradeVO.setModifiedBy(userVO.getUserID());
            gradeVO.setStatus(gradeList.get(0).getStatus());
            gradeVO.setGradeCode(gradeList.get(0).getGradeCode());
            gradeVO.setGradeName(gradeList.get(0).getGradeName());
            gradeVO.setCategoryCode(gradeList.get(0).getCategoryCode());
            gradeVO.setDefaultGrade(gradeList.get(0).getDefaultGrade());


            // Grade should not be deleted if associated with some active
            // user
            if (gradeDAO.isUserExistsForGrade(con, gradeVO)) {
                throw new BTSLBaseException(this, "deleteGrade", "domains.deletegrade.userexists", "viewgradedetails");
            }
            
            // for default grade:if Default grade is
            // 'N',then only we can delete.
            if (PretupsI.NO.equals(gradeVO.getDefaultGrade())) {
                deleteCount = gradeDAO.deleteGrade(con, gradeVO);
            }
            
            if (deleteCount > 0) {
                mcomCon.finalCommit();
                // Enter the details for delete grade on Admin Log
                AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                adminOperationVO.setSource(TypesI.LOGGER_GRADE_SOURCE);
                adminOperationVO.setInfo("Grade " + gradeVO.getGradeName() + " deleted successfully");
                AdminOperationLog.log(adminOperationVO);
				String msg = RestAPIStringParser.getMessage(locale, "domains.deletegrade.deletesuccess", null);
                response.setMessage(msg);
                response.setMessageCode("domains.deletegrade.deletesuccess");
				response.setStatus((HttpStatus.SC_OK));
            } else {
                mcomCon.finalRollback();
                throw new BTSLBaseException(this, "deleteGrade", "domains.deletegrade.deletenotsuccess");
            }

        }
        catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					"domains.modifygrade.notsuccess", null);
			response.setMessage(resmsg);
			response.setMessageCode("domains.deletegrade.deletenotsuccess");
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
        
		
		return response;
	}

}
