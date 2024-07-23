package com.restapi.superadmin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.restapi.superadmin.responseVO.DivisionManagementResponseVO;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.IDGenerator;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.DivisionDeptVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.master.businesslogic.DivisionDeptWebDAO;

@Service("DivisionManagementI")
public class DivisionManagementImpl implements DivisionManagementI {

	public static final Log LOG = LogFactory.getLog(DivisionManagementImpl.class.getName());
	public static final String classname = "DivisionManagementImpl";

	@Override
	public DivisonListResponseVO viewDivisionList(Connection con, String loginId, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "viewDivisionList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		DivisonListResponseVO response = new DivisonListResponseVO();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		DivisionVO divisionVO = new DivisionVO();
		DivisionDeptWebDAO divisionwebDAO = new DivisionDeptWebDAO();
		ArrayList<DivisionVO> divisionList = new ArrayList<DivisionVO>();
		ArrayList<DivisionVO> divList = new ArrayList<DivisionVO>();

		try {

			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			divisionVO.setDivDept(PretupsI.DIVDEPT_DIVISION);
			divisionVO.setUserId(userVO.getUserID());
			divisionVO.setStatus(PretupsI.DIVISION_STATE_ACTIVE);
			divisionList = divisionwebDAO.loadDivisionDetails1(con, userVO, divisionVO);
			if (divisionList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DIV_FAIL, 0, null);
			} else {
				for (int i = 0; i < divisionList.size(); i++) {
					divList.add(divisionList.get(i));
				}

				response.setDivisionList(divList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DIV_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.DIV_SUCCESS);

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
					PretupsErrorCodesI.DIV_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DIV_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	@Override
	public BaseResponse ModifyDivisionAdmin(Connection con, String loginId, ModifyDivisionRequestVO requestVO,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "ModifyDivisionAdmin";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BaseResponse response = new BaseResponse();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		Date currentDate = new Date();
		int updateCount = -1;

		try {

			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			DivisionDeptWebDAO divisionwebDAO = new DivisionDeptWebDAO();
			DivisionDeptVO divisionVO = new DivisionDeptVO();
			divisionVO.setDivDeptName(requestVO.getDivDeptName());
			divisionVO.setDivDeptShortCode(requestVO.getDivDeptShortCode());
			divisionVO.setStatusName(requestVO.getStatusName());
			divisionVO.setStatus(requestVO.getStatus());
			divisionVO.setDivDeptId(requestVO.getDivDeptId());
			divisionVO.setDivDeptType(requestVO.getDivDeptType());
			divisionVO.setParentId(requestVO.getParentId());
			divisionVO.setDivDept(PretupsI.DIVDEPT_DIVISION);
			if (divisionwebDAO.isDivisionNameExistsForModify(con, divisionVO)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DIV_NAME_EXISTS, 0, null);
			} else if (divisionwebDAO.isDivisionShortCodeExistsForModify(con, divisionVO)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DIV_CODE_EXISTS, 0, null);
			}

			divisionVO.setModifiedOn(currentDate);
			if (BTSLUtil.isNullString(divisionVO.getStatus())) {
				divisionVO.setStatus(PretupsI.DIVISION_STATE_ACTIVE);
			}

			divisionVO.setModifiedBy(userVO.getUserID());
			updateCount = divisionwebDAO.modifyDivision(con, divisionVO);

			if (updateCount > 0) {
				con.commit();
				response.setStatus((HttpStatus.SC_OK));
				String[] messageArr = null;
		    	messageArr = new String[] {requestVO.getDivDeptName()};
		    	response.setMessageCode(PretupsErrorCodesI.DIV_MODIFY_SUCCESS);
		    	response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),messageArr));
			} else {
				con.rollback();
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DIV_MODIFY_FAIL, 0, null);

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
					PretupsErrorCodesI.DIV_MODIFY_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DIV_MODIFY_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	@Override
	public DivisionManagementResponseVO AddDivision(Connection con, String loginId, String divDeptName, String divDeptShortCode,
													String divDeptType, String status, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "AddDivision";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		DivisionManagementResponseVO response = new DivisionManagementResponseVO();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		int addCount = -1;

		try {

			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			DivisionDeptVO divisionVO = new DivisionDeptVO();
			DivisionDeptWebDAO divisionwebDAO = new DivisionDeptWebDAO();
			divisionVO.setDivDeptName(divDeptName);
			divisionVO.setDivDeptShortCode(divDeptShortCode);
			divisionVO.setDivDeptType(divDeptType);
			divisionVO.setStatus(status);
			divisionVO.setDivDept(PretupsI.DIVDEPT_DIVISION);

			if (divisionwebDAO.isDivisionNameExistsForAdd(con, divisionVO)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DIV_NAME_EXISTS, 0, null);
			} else if (divisionwebDAO.isDivisionShortCodeExistsForAdd(con, divisionVO)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DIV_CODE_EXISTS, 0, null);
			}

			String idType = PretupsI.DIVISION_ID;
			StringBuffer uniqueId = new StringBuffer();
			long divisionID = IDGenerator.getNextID(idType, PretupsI.ALL);
			int zeroes = 10 - (idType.length() + Long.toString(divisionID).length());
			for (int count = 0; count < zeroes; count++) {
				uniqueId.append(0);
			}
			uniqueId.insert(0, idType);
			uniqueId.append(Long.toString(divisionID));
			Date currentDate = new Date(System.currentTimeMillis());

			divisionVO.setDivDeptId(uniqueId.toString());
			divisionVO.setCreatedOn(currentDate);
			divisionVO.setModifiedOn(currentDate);
			divisionVO.setCreatedBy(userVO.getUserID());
			divisionVO.setModifiedBy(userVO.getUserID());
			divisionVO.setParentId(uniqueId.toString());
			divisionVO.setUserId(userVO.getUserID());

			if (BTSLUtil.isNullString(divisionVO.getStatus())) {
				divisionVO.setStatus(PretupsI.DIVISION_STATE_ACTIVE);
			}

			addCount = divisionwebDAO.addDivision(con, divisionVO);

			if (addCount > 0) {
				con.commit();
				response.setDivId(divisionVO.getDivDeptId());
				response.setStatus((HttpStatus.SC_OK));
				String[] messageArr = null;
		    	messageArr = new String[] { divDeptName};
		    	response.setMessageCode(PretupsErrorCodesI.DIV_ADD_SUCCESS);
		    	response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),messageArr));

			} else {
				con.rollback();
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DIV_ADD_FAIL, 0, null);

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
					PretupsErrorCodesI.DIV_ADD_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DIV_ADD_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	@Override
	public DivTypeListResponseVO viewDivDepList(Connection con, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "viewDivDepList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		DivTypeListResponseVO response = new DivTypeListResponseVO();
		ArrayList divDepList = new ArrayList();

		try {

			DivisionDeptWebDAO divisionwebDAO = new DivisionDeptWebDAO();
			divDepList = divisionwebDAO.loadDivisionTypeList(con);

			if (divDepList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DIV_DEP_LIST_NOT_FOUND, 0, null);
			} else {

				response.setDivDepTypeList(divDepList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DIV_DEP_LIST_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.DIV_DEP_LIST_FOUND);

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
					PretupsErrorCodesI.DIV_DEP_LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DIV_DEP_LIST_NOT_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	@Override
	public BaseResponse deleteDivision(Connection con, String loginID, String divDeptId, String parentId,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "deleteDivision";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BaseResponse response = new BaseResponse();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		int updateCount = -1;

		try {
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			DivisionDeptVO divisionVO = new DivisionDeptVO();
			DivisionDeptWebDAO divisionwebDAO = new DivisionDeptWebDAO();
			divisionVO.setParentId(parentId);
			divisionVO.setDivDeptId(divDeptId);
			if (divisionwebDAO.isDepartmentExistsForDivision(con, divisionVO)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DEP_FOR_DEV, 0, null);
			} else {

				Date currentDate = new Date();
				divisionVO.setModifiedOn(currentDate);
				divisionVO.setModifiedBy(userVO.getUserID());
				updateCount = divisionwebDAO.deleteDivision(con, divisionVO);
				if (updateCount > 0) {
					con.commit();
					response.setStatus((HttpStatus.SC_OK));
					String[] messageArr = null;
			    	messageArr = new String[] { divDeptId};
			    	response.setMessageCode(PretupsErrorCodesI.DIV_DELETE_SUCCESS);
			    	response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),messageArr));
					
				} else {
					con.rollback();
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DIV_DELETE_FAIL, 0, null);
				}
			}

		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
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
					PretupsErrorCodesI.DIV_DELETE_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DIV_DELETE_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		return response;
	}
	
	
	
	
	@Override
	public CategoryListRespVO getCategoryList(Connection con, HttpServletResponse responseSwag,String categoryCode)
			throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "getCategoryList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		CategoryListRespVO response = new CategoryListRespVO();
		

		try {

			ArrayList catList = new CategoryDAO().loadCategoryDetailsOPTCategoryCode(con,
					categoryCode);

			if (catList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CATEGORY_NOT_FOUND, 0, null);
			} else {

				response.setCategoryList(catList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CATEGORY_NOT_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.DIV_DEP_LIST_FOUND);

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
					PretupsErrorCodesI.CATEGORY_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.CATEGORY_NOT_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	@Override
	public DivisonListForDepartmentResponseVO viewDivisionListForDepartment(Connection con, String loginID,
			HttpServletResponse response1) {

		final String METHOD_NAME = "viewDivisionListForDepartment";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		DivisonListForDepartmentResponseVO response = new DivisonListForDepartmentResponseVO();
		DivisionDeptWebDAO divisionwebDAO = new DivisionDeptWebDAO();
		ArrayList divisionList = new ArrayList<>();

		try {
			divisionList = divisionwebDAO.loadDivisionListForDept(con);
			if (divisionList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DIV_FAIL, 0, null);
			} else {

				response.setDivisionList(divisionList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DIV_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.DIV_SUCCESS);

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
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.DIV_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DIV_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	public DivisonListForDepartmentResponseVO departmentListAdmin(Connection con, String loginID, String parentId,
			HttpServletResponse response1) {
		final String METHOD_NAME = "departmentList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		DivisonListForDepartmentResponseVO response = new DivisonListForDepartmentResponseVO();
		DivisionDeptWebDAO divisionwebDAO = new DivisionDeptWebDAO();
		ArrayList departmentList = new ArrayList<>();
		UserVO userVO = new UserVO();
		UserDAO userDAO = new UserDAO();

		try {

			DivisionDeptVO departmentVO = new DivisionDeptVO();
			departmentVO.setDivDept(PretupsI.DIVDEPT_DEPARTMENT);
			departmentVO.setParentId(parentId);
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			departmentList = divisionwebDAO.loadDepartmentDetails(con, userVO, departmentVO);
			if (departmentList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DEP_FAIL, 0, null);
			} else {

				response.setDivisionList(departmentList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DEP_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.DEP_SUCCESS);

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
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.DEP_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DEP_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	public BaseResponse deleteDepartment(Connection con, String loginID, String divDeptId,
			HttpServletResponse response1) {
		final String METHOD_NAME = "deleteDepartment";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BaseResponse response = new BaseResponse();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		int updateCount = -1;

		try {
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			DivisionDeptVO departmentVO = new DivisionDeptVO();
			departmentVO.setDivDeptId(divDeptId);
			DivisionDeptWebDAO departmentwebDAO = new DivisionDeptWebDAO();
			if (departmentwebDAO.isUserExistsForDepartment(con, departmentVO)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DEP_DELETE_FAIL1, 0, null);
			} else {

				Date currentDate = new Date();
				departmentVO.setModifiedOn(currentDate);
				departmentVO.setModifiedBy(userVO.getUserID());
				updateCount = departmentwebDAO.deleteDivision(con, departmentVO);
				if (updateCount > 0) {
					con.commit();
					response.setStatus((HttpStatus.SC_OK));
					String[] messageArr = null;
					messageArr = new String[] { divDeptId };
					response.setMessageCode(PretupsErrorCodesI.DEP_DELETE_SUCCESS);
					response.setMessage(RestAPIStringParser.getMessage(
							new Locale(
									(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
									(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),
							response.getMessageCode(), messageArr));

				} else {
					con.rollback();
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DEP_DELETE_FAIL, 0, null);
				}
			}

		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.DIV_DELETE_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DEP_DELETE_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		return response;
	}

	public BaseResponse AddDepartment(Connection con, String loginID, String parentId, String divDeptName,
			String divDeptShortCode, String divDeptType, String status, HttpServletResponse response1) {

		final String METHOD_NAME = "AddDepartment";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BaseResponse response = new BaseResponse();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		int addCount = -1;

		try {

			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			DivisionDeptVO divisionVO = new DivisionDeptVO();
			DivisionDeptWebDAO divisionwebDAO = new DivisionDeptWebDAO();
			divisionVO.setDivDept(PretupsI.DIVDEPT_DEPARTMENT);
			divisionVO.setParentId(parentId);
			divisionVO.setDivDeptName(divDeptName);
			divisionVO.setDivDeptShortCode(divDeptShortCode);
			divisionVO.setDivDeptType(divDeptType);
			divisionVO.setStatus(status);

			if (divisionwebDAO.isDepartmentNameExistsForAdd(con, divisionVO)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DEP_NAME_EXISTS, 0, null);
			} else if (divisionwebDAO.isDepartmentShortCodeExistsForAdd(con, divisionVO)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DEP_CODE_EXISTS, 0, null);
			}

			String idType = PretupsI.DIVISION_ID;
			StringBuffer uniqueId = new StringBuffer();
			long divisionID = IDGenerator.getNextID(idType, PretupsI.ALL);
			int zeroes = 10 - (idType.length() + Long.toString(divisionID).length());
			for (int count = 0; count < zeroes; count++) {
				uniqueId.append(0);
			}
			uniqueId.insert(0, idType);
			uniqueId.append(Long.toString(divisionID));
			Date currentDate = new Date(System.currentTimeMillis());
			divisionVO.setDivDeptId(uniqueId.toString());
			divisionVO.setCreatedOn(currentDate);
			divisionVO.setModifiedOn(currentDate);
			divisionVO.setCreatedBy(userVO.getUserID());
			divisionVO.setModifiedBy(userVO.getUserID());
			divisionVO.setUserId(userVO.getUserID());

			if (BTSLUtil.isNullString(divisionVO.getStatus())) {
				divisionVO.setStatus(PretupsI.DIVISION_STATE_ACTIVE);
			}

			addCount = divisionwebDAO.addDivision(con, divisionVO);

			if (addCount > 0) {
				con.commit();

				response.setStatus((HttpStatus.SC_OK));
				String[] messageArr = null;
				messageArr = new String[] { divDeptName };
				response.setMessageCode(PretupsErrorCodesI.DEP_ADD_SUCCESS);
				response.setMessage(RestAPIStringParser.getMessage(
						new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
								(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),
						response.getMessageCode(), messageArr));

			} else {
				con.rollback();
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DEP_ADD_FAIL, 0, null);

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
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.DEP_ADD_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DEP_ADD_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	public BaseResponse ModifyDepartmentAdmin(Connection con, String loginID, ModifyDepartmentRequestVO requestVO,
			HttpServletResponse response1) {
		final String METHOD_NAME = "ModifyDepartmentAdmin";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BaseResponse response = new BaseResponse();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		Date currentDate = new Date();
		int updateCount = -1;

		try {

			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			DivisionDeptWebDAO divisionwebDAO = new DivisionDeptWebDAO();
			DivisionDeptVO divisionVO = new DivisionDeptVO();
			divisionVO.setDivDeptName(requestVO.getDivDeptName());
			divisionVO.setDivDeptShortCode(requestVO.getDivDeptShortCode());
			divisionVO.setStatus(requestVO.getStatus());
			divisionVO.setDivDeptId(requestVO.getDivDeptId());
			divisionVO.setParentId(requestVO.getParentId());
			divisionVO.setDivDept(PretupsI.DIVDEPT_DEPARTMENT);
			divisionVO.setDivDeptType(requestVO.getDivDeptType());
			
			if (divisionwebDAO.isDepartmentNameExistsForModify(con, divisionVO)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DEP_NAME_EXISTS, 0, null);
			} else if (divisionwebDAO.isDepartmentShortCodeExistsForModify(con, divisionVO)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DEP_CODE_EXISTS, 0, null);
			}

			divisionVO.setModifiedOn(currentDate);
			if (BTSLUtil.isNullString(divisionVO.getStatus())) {
				divisionVO.setStatus(PretupsI.DIVISION_STATE_ACTIVE);
			}

			divisionVO.setModifiedBy(userVO.getUserID());
			updateCount = divisionwebDAO.modifyDivision(con, divisionVO);

			if (updateCount > 0) {
				con.commit();
				response.setStatus((HttpStatus.SC_OK));
				String[] messageArr = null;
		    	messageArr = new String[] {requestVO.getDivDeptName()};
		    	response.setMessageCode(PretupsErrorCodesI.DEP_MODIFY_SUCCESS);
		    	response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),messageArr));
			} else {
				con.rollback();
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DEP_MODIFY_FAIL, 0, null);

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
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.DEP_MODIFY_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DEP_MODIFY_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}
}
