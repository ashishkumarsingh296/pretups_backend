package com.restapi.superadmin.serviceI;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.btsl.logging.Log;
import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.restapi.superadmin.AssignGeographyResponseVO;
import com.restapi.superadmin.AssignSevicesResponseVO;
import com.restapi.superadmin.DepartementListResponseVO;
import com.restapi.superadmin.InfoForEditOperatorResponseVO;
import com.restapi.superadmin.SMSCprofileResponseVO;
import com.restapi.superadmin.service.InfoForEditOperatorService;
import com.web.pretups.domain.businesslogic.DomainWebDAO;
import com.web.pretups.master.businesslogic.DivisionDeptWebDAO;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;

@Service("InfoForEditOperatorService")
public class InfoForEditOperatorServiceI implements InfoForEditOperatorService {

	public static final Log LOG = LogFactory.getLog(InfoForEditOperatorServiceI.class.getName());
	public static final String classname = "InfoForEditOperatorServiceI";

	@Override
	public InfoForEditOperatorResponseVO getInfo(Connection con, String loginId, HttpServletResponse responseSwag,
			String categoryCode, String userId, String networkId) throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "getInfo";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		InfoForEditOperatorResponseVO response = new InfoForEditOperatorResponseVO();
		try {

			if (TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(categoryCode)) {

				DomainWebDAO domainWebDAO = new DomainWebDAO();
				ArrayList domainList = domainWebDAO.loadUserDomainList(con, userId);
				if (!BTSLUtil.isNullOrEmptyList(domainList)) {
					response.setDomainList(domainList);
				}

			}

			
				ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
				ArrayList serviceList = null;
			   if (PretupsI.OPERATOR_CATEGORY.equalsIgnoreCase(categoryCode)|| PretupsI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(categoryCode)) {
					   serviceList =servicesDAO.assignServicesToChlAdmin(con, networkId);
	                } else {
	                	serviceList=servicesDAO.loadServicesList(con, networkId);
	                }
				
				if (!BTSLUtil.isNullorEmpty(serviceList)) {
					response.setSerivesList(serviceList);
				}
			

			if (TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(categoryCode)) {
				ProductTypeDAO productTypeDAO = new ProductTypeDAO();
				ArrayList productList = productTypeDAO.loadUserProductsList(con, userId);
				if (!BTSLUtil.isNullOrEmptyList(productList)) {
					response.setProductList(productList);
				}

			}
			VomsProductDAO voucherDAO = null;
			voucherDAO = new VomsProductDAO();
			boolean userVoucherTypeAllowed = (boolean) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
			if (userVoucherTypeAllowed) {
				ArrayList voucherList = voucherDAO.loadUserVoucherTypeList(con, userId);
				if (!BTSLUtil.isNullOrEmptyList(voucherList)) {
					response.setVoucherTypeList(voucherList);
				}

			}
			if (!(TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(categoryCode)
					|| TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(categoryCode)
					|| TypesI.MONITOR_SERVER.equalsIgnoreCase(categoryCode))) {

				ArrayList segmentList = voucherDAO.loadUserVoucherSegmentList(con, userId);
				if (!BTSLUtil.isNullOrEmptyList(segmentList)) {
					response.setVoucherSegmentList(segmentList);
				}
			}

			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
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
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		return response;
	}

	@Override
	public AssignSevicesResponseVO assignList(Connection con, String loginId, HttpServletResponse responseSwag,
			String categoryCode, String userId, String networkId) throws BTSLBaseException, SQLException {
		final String METHOD_NAME = "assignList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		AssignSevicesResponseVO response = new AssignSevicesResponseVO();
		try {

			if (TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(categoryCode)) {

				DomainDAO domainDAO = new DomainDAO();
				ArrayList domainList = domainDAO.loadDomainList(con, PretupsI.DOMAIN_TYPE_CODE);
				if (!BTSLUtil.isNullOrEmptyList(domainList)) {
					response.setDomainList(domainList);
				}

			}

			if (TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(categoryCode)) {
				ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
				ArrayList serviceList = servicesDAO.assignServicesToChlAdmin(con, networkId);
				if (!BTSLUtil.isNullorEmpty(serviceList)) {
					response.setServicesList(serviceList);
				}
			}

			if (TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(categoryCode)) {
				ProductTypeDAO productTypeDAO = new ProductTypeDAO();
				ArrayList productList = LookupsCache.loadLookupDropDown(PretupsI.PRODUCT_TYPE, true);
				if (!BTSLUtil.isNullOrEmptyList(productList)) {
					response.setProductList(productList);
				}

			}

			VomsProductDAO voucherDAO = new VomsProductDAO();
			ArrayList voucherList = new ArrayList();
			voucherList = voucherDAO.loadVoucherTypeList(con);
			if (!BTSLUtil.isNullOrEmptyList(voucherList)) {
				response.setVoucherTypeList(voucherList);
			}

			if (!(TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(categoryCode)
					|| TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(categoryCode)
					|| TypesI.MONITOR_SERVER.equalsIgnoreCase(categoryCode))) {

				ArrayList segmentList = LookupsCache.loadLookupDropDown(VOMSI.VOUCHER_SEGMENT, true);
				if (!BTSLUtil.isNullOrEmptyList(segmentList)) {
					response.setVoucherSegmentList(segmentList);
				}
			}

			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);

		} catch (BTSLBaseException be) {
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
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	@Override
	public AssignGeographyResponseVO assignGeography(Connection con, String loginId, HttpServletResponse responseSwag,
			String categoryCode)
			throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "assignGeography";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		AssignGeographyResponseVO response = new AssignGeographyResponseVO();
		UserVO userVO = new UserVO();
		UserDAO userDAO = new UserDAO();
		ArrayList geographyList = null;
		ArrayList networkList = null;
		String parentDomainCode = null;
		String grphDomainType = null;
		Integer grphDomainSequenceNo = null;
		
        CategoryDAO _categoryDAO = new CategoryDAO();

		try {
			
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
			ArrayList list1 = userVO.getGeographicalAreaList();
			UserGeographiesVO vo = (UserGeographiesVO) list1.get(0);
			parentDomainCode = vo.getGraphDomainCode();
			
            List <CategoryVO>categoryList = null;
			categoryList=(ArrayList<CategoryVO>)_categoryDAO.loadCategoryList(con, PretupsI.OPERATOR_TYPE_OPT, userVO
					.getCategoryCode());
			
			CategoryVO categoryVO = null;

			String graphDomainTypeName = null;
			for (int i = 0, j = categoryList.size(); i < j; i++) {
                categoryVO = (CategoryVO) categoryList.get(i);

                if (categoryVO.getCategoryCode().equalsIgnoreCase(categoryCode)) {
                    grphDomainType = categoryVO.getGrphDomainType();
                    grphDomainSequenceNo = categoryVO.getGrphDomainSequenceNo();
					graphDomainTypeName = categoryVO.getGrphDomainTypeName();
					break;
                }
            }
            
			if (userVO.getCategoryVO().getGrphDomainType().equals(grphDomainType)) {
				if ((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(categoryCode))
						|| (TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(categoryCode))) {
					ArrayList netVOList = new NetworkDAO().loadNetworkList(con, "'" + PretupsI.STATUS_DELETE + "'");
					networkList = new ArrayList<>();
					int netVOListSize = netVOList.size();
					for (int i = 0; i < netVOListSize; i++) {
						NetworkVO netVo = (NetworkVO) netVOList.get(i);
						UserGeographiesVO geogVO = new UserGeographiesVO();
						geogVO.setGraphDomainCode(netVo.getNetworkCode());
						geogVO.setGraphDomainName(netVo.getNetworkName());
						geogVO.setGraphDomainTypeName(graphDomainTypeName);
						networkList.add(geogVO);
					}
					response.setGeographyList(networkList);
					response.setStatus((HttpStatus.SC_OK));
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
				} else if (TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(categoryCode)) {
					geographyList = geographicalDomainWebDAO.loadGeographyListForSuperChannelAdmin(con, grphDomainType);
					response.setGeographyList(geographyList);
					response.setStatus((HttpStatus.SC_OK));
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);

					if (geographyList != null && geographyList.size() > 0) {
						UserGeographiesVO geographyVO = (UserGeographiesVO) geographyList.get(0);
					}
				} else {
					geographyList = userVO.getGeographicalAreaList();
					response.setGeographyList(geographyList);
					response.setStatus((HttpStatus.SC_OK));
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
					if (geographyList != null && geographyList.size() > 0) {
						UserGeographiesVO geographyVO = (UserGeographiesVO) geographyList.get(0);
					}
				}
			}

			else if (grphDomainSequenceNo == 1) {
				UserGeographiesVO geographyVO = null;
				geographyList = new ArrayList();
				geographyVO = new UserGeographiesVO();
				geographyVO.setGraphDomainCode(userVO.getNetworkID());
				geographyVO.setGraphDomainName(userVO.getNetworkName());
				geographyVO.setGraphDomainTypeName(userVO.getCategoryVO().getGrphDomainTypeName());
				geographyList.add(geographyVO);

				response.setGeographyList(geographyList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
			}

			if ((userVO.getCategoryVO().getGrphDomainSequenceNo() + 1) == grphDomainSequenceNo) {
				if (TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(categoryCode)) {
					geographyList = geographicalDomainWebDAO.loadGeographyList(con, grphDomainType);
				} else {
					geographyList = geographicalDomainWebDAO.loadGeographyList(con, userVO.getNetworkID(),
							parentDomainCode, "%");
				}
				response.setGeographyList(geographyList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
				if (geographyList != null && geographyList.size() > 0) {
					UserGeographiesVO geographyVO = (UserGeographiesVO) geographyList.get(0);
				}
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
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	@Override
	public SMSCprofileResponseVO getSMSCInfo(Connection con, String loginId, HttpServletResponse responseSwag,
			String categoryCode) throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "getSMSCInfo";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		SMSCprofileResponseVO response = new SMSCprofileResponseVO();
		ArrayList ProfileList = null;
		UserDAO userDAO = new UserDAO();

		try {

			ProfileList = userDAO.loadPhoneProfileList(con, categoryCode);
			if (!BTSLUtil.isNullOrEmptyList(ProfileList)) {
				response.setSmscProfileList(ProfileList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
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
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	@Override
	public DepartementListResponseVO getDepartement(Connection con, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "getDepartement";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		DepartementListResponseVO response = new DepartementListResponseVO();
		ArrayList departmentList = null;

		try {

			DivisionDeptWebDAO divisionwebDAO = new DivisionDeptWebDAO();
			String divStatus = null;
			String divStatusUsed = null;
			divStatus = "'" + PretupsI.USER_STATUS_SUSPEND + "','" + PretupsI.USER_STATUS_ACTIVE + "'";
			divStatusUsed = PretupsI.STATUS_IN;
			departmentList = divisionwebDAO.loadDivisionDeptList(con, TypesI.DIVDEPT_TYPE, TypesI.DIVDEPT_DEPARTMENT,
					divStatusUsed, divStatus);

			if (!BTSLUtil.isNullOrEmptyList(departmentList)) {
				response.setDepartmentList(departmentList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
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
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}
	
	
	
	@Override
	public DepartementListResponseVO getDepartementbyDivID(Connection con, HttpServletResponse responseSwag,String divID)
			throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "getDepartementbyDivID";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		DepartementListResponseVO response = new DepartementListResponseVO();
		ArrayList departmentList = null;
		try {

			DivisionDeptWebDAO divisionwebDAO = new DivisionDeptWebDAO();
			departmentList = divisionwebDAO.loadDepartmentListBYDivID(con, divID);

			if (!BTSLUtil.isNullOrEmptyList(departmentList)) {
				response.setDepartmentList(departmentList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
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
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}


}
