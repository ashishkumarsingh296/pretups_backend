package com.restapi.networkadmin.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.networkadmin.requestVO.GeoDomainAddRequestVO;
import com.restapi.networkadmin.requestVO.GeoDomainDeleteRequestVO;
import com.restapi.networkadmin.requestVO.GeoDomainListRequestVO;
import com.restapi.networkadmin.responseVO.GeoDomainTypeListResponseVO;
import com.restapi.networkadmin.responseVO.ParentGeoDomainResponseVO;
import com.restapi.networkadmin.serviceI.GeographicalDomainService;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;

@Service
public class GeographicalDomainServiceImpl implements GeographicalDomainService {
	protected static final Log LOG = LogFactory.getLog(GeographicalDomainServiceImpl.class.getName());
	protected ArrayList parentTypeList;
	private String indexParentCode;
	private String indexParentValue;
	private String parentDomainCode;
	private String parentDomainName;
	private String parentDomainType;
	String[] errDomain;
	private ArrayList<GeographicalDomainVO> geographicalDomainList;

	@Override
	public GeoDomainTypeListResponseVO loadGeoDomainTypeList(Connection con, Locale locale,
			HttpServletResponse responseSwag) throws SQLException {
		final String methodName = "loadGeoDomainTypeList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}
		GeoDomainTypeListResponseVO response = new GeoDomainTypeListResponseVO();
		try {
			response.setGeoDomTypeList(new GeographicalDomainWebDAO().loadDomainTypeList(con));
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
		} catch (BTSLBaseException be) {
			LOG.error("", "Exceptin:e=" + be);
			LOG.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception ex) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.errorTrace(methodName, ex);
			LOG.error(methodName, "Exception = " + ex.getMessage());
		} finally {

			if (con != null)
				con.close();
		}
		return response;
	}

	public GeoDomainTypeListResponseVO loadParentGeoDomainTypeList(Connection con, Locale locale,
			HttpServletResponse responseSwag, String geoDomainType) throws SQLException {
		final String methodName = "loadGeoDomainTypeList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		GeoDomainTypeListResponseVO response = new GeoDomainTypeListResponseVO();
		try {
			parentTypeList = new GeographicalDomainWebDAO().loadParentTypeList(con, geoDomainType);
			if (parentTypeList.size() != 0)
				parentTypeList.remove(0);
			response.setGeoDomTypeList(parentTypeList);
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
		} catch (BTSLBaseException be) {
			LOG.error("", "Exceptin:e=" + be);
			LOG.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception ex) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.errorTrace(methodName, ex);
			LOG.error(methodName, "Exception = " + ex.getMessage());
		} finally {

			if (con != null)
				con.close();
		}

		return response;
	}

	private void checkParentHierarchy(Connection con, GeoDomainListRequestVO requestVO, int p_index, String DomainCode)
			throws Exception {
		final String methodName = "checkParentHierarchy";

		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered p_index=" + p_index);
		}

		//
		if (p_index == 0) {
			throw new BTSLBaseException(this.getClass().getName().toString(), methodName,
					PretupsErrorCodesI.GRPHDOMAIN_SELECT_PARENT, 0, null);
		}
		if (parentTypeList == null) {
			throw new BTSLBaseException(this.getClass().getName().toString(), methodName,
					PretupsErrorCodesI.PARENT_TYPE_LIST_EMPTY, 0, null);
		}
		if (p_index != requestVO.getParentTypeList().length) {
			throw new BTSLBaseException(this.getClass().getName().toString(), methodName,
					PretupsErrorCodesI.GRPHDOMAIN_SELECT_PARENT, 0, null);
		}
		// checking that all the parent domain names upto the index has been
		// entered in the text boxes.
//		
		for (String x : requestVO.getParentTypeList()) {
			if (x == null || x.trim().length() == 0) {
				throw new BTSLBaseException(this.getClass().getName().toString(), methodName,
						PretupsErrorCodesI.GRPHDOMAIN_SELECT_PARENT, 0, null);

			}
		}

		GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
		GeographicalDomainVO geographicalDomainVO = null;

		// This is the parent code for the first domain code form the list of
		// parents (here ZONE is the first domain)
		parentDomainCode = DomainCode;
		indexParentValue = requestVO.getParentTypeList()[0];
		ArrayList domainList;
		ListValueVO listValueVO = null;

		// array to show the domain name in the error message.
		errDomain = new String[1];

		for (int i = 0, j = p_index; i < j; i++) {
			// extracting the domain vo from the parent list.
			listValueVO = (ListValueVO) parentTypeList.get(i);

			domainList = geographicalDomainWebDAO.loadParentDomainList(con, DomainCode, parentDomainCode,
					listValueVO.getValue(), "%" + requestVO.getParentTypeList()[i] + "%");

			if (domainList.isEmpty()) {
				errDomain[0] = requestVO.getParentTypeList()[i];
//				throw new BTSLBaseException(this, "loadParentList", "grphdomain.operation.msg.nodata", 0, errDomain,
//						p_froward);
				throw new BTSLBaseException(this.getClass().getName().toString(), methodName,
						PretupsErrorCodesI.GEO_DOMAIN_NO_DATA, 0, errDomain, null);
			}

			else if (domainList.size() > 1) {
				boolean recordFound = false;
				if (!BTSLUtil.isNullString(indexParentCode)) {
					for (int k = 0, l = domainList.size(); k < l; k++) {
						geographicalDomainVO = (GeographicalDomainVO) domainList.get(k);
						if (indexParentCode.equals(geographicalDomainVO.getParentDomainCode())
								&& (indexParentValue.compareTo(geographicalDomainVO.getParentDomainName()) == 0)) {
							parentDomainCode = geographicalDomainVO.getParentDomainCode();
							indexParentCode = parentDomainCode;
							indexParentValue = geographicalDomainVO.getParentDomainName();
							recordFound = true;
							break;
						}
					}
				} else {
					GeographicalDomainVO geographicalDomainNextVO = null;
					for (int k = 0, l = domainList.size(); k < l; k++) {
						geographicalDomainVO = (GeographicalDomainVO) domainList.get(k);
						if (indexParentValue.compareTo(geographicalDomainVO.getParentDomainName()) == 0) {
							if (((k + 1) < l)) {
								geographicalDomainNextVO = (GeographicalDomainVO) domainList.get(k + 1);
								if (indexParentValue.compareTo(geographicalDomainNextVO.getParentDomainName()) == 0) {
									recordFound = false;
									break;
								}
								parentDomainCode = geographicalDomainVO.getParentDomainCode();
								indexParentCode = parentDomainCode;
								indexParentValue = geographicalDomainVO.getParentDomainName();
								recordFound = true;
								break;
							}
							parentDomainCode = geographicalDomainVO.getParentDomainCode();
							indexParentCode = parentDomainCode;
							indexParentValue = geographicalDomainVO.getParentDomainName();
							recordFound = true;
							break;
						}
					}
				}
				if (!recordFound) {
					errDomain[0] = indexParentValue;
					throw new BTSLBaseException(this.getClass().getName().toString(), methodName,
							PretupsErrorCodesI.GEO_DOMAIN_MULTIPLE_RECORD, 0, errDomain, null);
				}
			} else {
				geographicalDomainVO = (GeographicalDomainVO) domainList.get(0);
				parentDomainCode = geographicalDomainVO.getParentDomainCode();
				indexParentCode = parentDomainCode;
				indexParentValue = geographicalDomainVO.getParentDomainName();
			}

		}
		parentDomainName = geographicalDomainVO.getParentDomainName();
		parentDomainType = listValueVO.getLabel();
		if (LOG.isDebugEnabled()) {
			LOG.debug("checkParentHierarchy", "Exiting ");
		}
	}

	@Override
	public ParentGeoDomainResponseVO loadGeoDomainList(Connection con, Locale locale, HttpServletResponse responseSwag,
			UserVO userVO, GeoDomainListRequestVO requestVO, String geoDomainType) throws SQLException {

		final String methodName = "loadGeoDomainList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}
		GeographicalDomainWebDAO geographicalDomainWebDAO = null;
		GeographicalDomainVO geographicalDomainVO = null;
		LookupsVO lookupsVO = null;
		ParentGeoDomainResponseVO response = new ParentGeoDomainResponseVO();
		try {
			if (parentTypeList != null && !parentTypeList.isEmpty()) {
				this.checkParentHierarchy(con, requestVO, parentTypeList.size(), userVO.getNetworkID());
				indexParentCode = null;
				indexParentValue = null;
				geographicalDomainWebDAO = new GeographicalDomainWebDAO();
				geographicalDomainList = geographicalDomainWebDAO.loadDomainList(con, userVO.getNetworkID(),
						parentDomainCode, geoDomainType);
			} else {
				indexParentCode = null;
				indexParentValue = null;
				geographicalDomainWebDAO = new GeographicalDomainWebDAO();

				geographicalDomainList = geographicalDomainWebDAO.loadDomainList(con, userVO.getNetworkID(),
						userVO.getNetworkID(), geoDomainType);
			}

			if (geographicalDomainList != null && !geographicalDomainList.isEmpty()) {
				for (int i = 0, j = geographicalDomainList.size(); i < j; i++) {
					geographicalDomainVO = (GeographicalDomainVO) geographicalDomainList.get(i);
					lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.STATUS_TYPE,
							geographicalDomainVO.getStatus());
					geographicalDomainVO.setStatusDescription(lookupsVO.getLookupName());
				}
			}

			response.setParentDomainList(geographicalDomainList);
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
		} catch (BTSLBaseException be) {
			LOG.error("", "Exceptin:e=" + be);
			LOG.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), errDomain);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception ex) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.errorTrace(methodName, ex);
			LOG.error(methodName, "Exception = " + ex.getMessage());
		} finally {

			if (con != null)
				con.close();
		}

		return response;
	}

	@Override
	public ParentGeoDomainResponseVO searchValue(Connection con, Locale locale, String index, String indexValue,
			String parentValueDes, HttpServletResponse responseSwag, UserVO userVO) throws SQLException {
		final String methodName = "searchValue";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}
		ParentGeoDomainResponseVO response = new ParentGeoDomainResponseVO();
		GeographicalDomainWebDAO geographicalDomainWebDAO = null;
		ArrayList parentDomainList = null;
		ListValueVO listValueVO = null;
		String parentDomainCode = null;
		geographicalDomainWebDAO = new GeographicalDomainWebDAO();

		try {
			String domainName = indexValue;
			//for blank value search
			if (domainName == null) {
				response.setParentDomainList(null);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(PretupsI.RESPONSE_FAIL);
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MANDATORY_EMPTY, null);
				response.setMessageCode(PretupsErrorCodesI.MANDATORY_EMPTY);
				response.setMessage(resmsg);
				return response;
			}
			domainName = "%" + domainName + "%";
			if (Integer.parseInt(index) == 0) {
				parentDomainCode = userVO.getNetworkID();
			} else {
				parentDomainCode = parentValueDes;

			}

			listValueVO = (ListValueVO) parentTypeList.get(Integer.parseInt(index));
			parentDomainList = geographicalDomainWebDAO.loadParentDomainList(con, userVO.getNetworkID(),
					parentDomainCode, listValueVO.getValue(), domainName);
			response.setParentDomainList(parentDomainList);
			response.setParentDomainType(listValueVO.getLabel());
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);

		} catch (BTSLBaseException be) {
			LOG.error("", "Exceptin:e=" + be);
			LOG.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception ex) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.errorTrace(methodName, ex);
			LOG.error(methodName, "Exception = " + ex.getMessage());
		} finally {

			if (con != null)
				con.close();
		}

		return response;
	}

	public BaseResponse addGeoDomain(Connection con, Locale locale, HttpServletResponse responseSwag, UserVO userVO,
			GeoDomainAddRequestVO requestVO) throws SQLException {
		final String methodName = "addGeoDomain";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}
		BaseResponse response = new BaseResponse();
		boolean shortNameAlreadyExist = false;
		boolean nameAlreadyExist = false;
		GeographicalDomainDAO geographicalDomainDAO = null;
		GeographicalDomainWebDAO geographicalDomainWebDAO = null;
		GeographicalDomainVO geographicalDomainVO = null;

		int addCount = 0;

		try {
			geographicalDomainDAO = new GeographicalDomainDAO();
			geographicalDomainWebDAO = new GeographicalDomainWebDAO();
			Date currentDate = new Date();
			geographicalDomainVO = new GeographicalDomainVO();

			if (geographicalDomainDAO.isGeographicalDomainExist(con, requestVO.getGrphDomainCode(), false)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "isgeographicalDomainCodeExist=true");
				}
				throw new BTSLBaseException(this, methodName, "grphdomain.operation.msg.codealreadyexist", methodName);
			}

			if (geographicalDomainList != null && !geographicalDomainList.isEmpty()) {

				nameAlreadyExist = geographicalDomainList.stream()
						.anyMatch(x -> x.getGrphDomainName().equals(requestVO.getGrphDomainName()));
				shortNameAlreadyExist = geographicalDomainList.stream()
						.filter(x -> x.getGrphDomainShortName().compareTo(requestVO.getGrphDomainShortName()) == 0)
						.findAny().isPresent() ? true : false;

			}
			if (nameAlreadyExist) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "isgeographicalDomainNameExist=true");
				}
				throw new BTSLBaseException(this.getClass().getName().toString(), methodName,
						"grphdomain.operation.msg.namealreadyexist", 0, null);
			}
			// if GrphDomainShortName is already exist then show error message.
			if (shortNameAlreadyExist) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "isgeographicalDomainShortNameExist=true");
				}
				throw new BTSLBaseException(this.getClass().getName().toString(), methodName,
						"grphdomain.operation.msg.shortnamealreadyexist", 0, null);
			}

			geographicalDomainVO.setCreatedBy(userVO.getUserID());
			geographicalDomainVO.setCreatedOn(currentDate);
			geographicalDomainVO.setDescription(requestVO.getDescription());
			geographicalDomainVO.setGrphDomainCode(requestVO.getGrphDomainCode());
			geographicalDomainVO.setGrphDomainName(requestVO.getGrphDomainName());
			geographicalDomainVO.setGrphDomainShortName(requestVO.getGrphDomainShortName());
			geographicalDomainVO.setGrphDomainType(requestVO.getGrphDomainType());
			geographicalDomainVO.setModifiedBy(userVO.getUserID());
			geographicalDomainVO.setModifiedOn(currentDate);
			geographicalDomainVO.setParentDomainCode(requestVO.getParentDomainCode());
			geographicalDomainVO.setNetworkCode(userVO.getNetworkID());
			geographicalDomainVO.setStatus(requestVO.getStatus());
			geographicalDomainVO.setLastModifiedTime(System.currentTimeMillis());
			geographicalDomainVO.setIsDefault(requestVO.getIsDefault());

			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()
					&& PretupsI.YES.equals(requestVO.getIsDefault())) {
				geographicalDomainWebDAO.updatedeDefaultGeography(con, geographicalDomainVO.getNetworkCode(),
						geographicalDomainVO.getParentDomainCode());
			}
			addCount = geographicalDomainWebDAO.addGeographicalDomain(con, geographicalDomainVO);
			if (con != null) {
				if (addCount > 0) {
					con.commit();
					response.setStatus(PretupsI.RESPONSE_SUCCESS);
					response.setMessageCode(PretupsErrorCodesI.GEOGRAPHICAL_DOMAIN_ADDED_SUCCESSFULLY);
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEOGRAPHICAL_DOMAIN_ADDED_SUCCESSFULLY, null);
					response.setMessage(resmsg);
					responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);

				} else {
					con.rollback();
					throw new BTSLBaseException(this, methodName, "grphdomain.addgrphdomain.msg.addunsuccess",
							methodName);
				}
			}
		} catch (BTSLBaseException be) {
			LOG.error("", "Exceptin:e=" + be);
			LOG.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception ex) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.errorTrace(methodName, ex);
			LOG.error(methodName, "Exception = " + ex.getMessage());
		} finally {

			if (con != null)
				con.close();
		}

		return response;
	}

	public BaseResponse modifyGeoDomain(Connection con, Locale locale, HttpServletResponse responseSwag, UserVO userVO,
			GeoDomainAddRequestVO requestVO) throws SQLException {
		final String methodName = "modifyGeoDomain";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}
		BaseResponse response = new BaseResponse();
		boolean shortNameAlreadyExist = false;
		boolean nameAlreadyExist = false;
		GeographicalDomainDAO geographicalDomainDAO = null;
		GeographicalDomainWebDAO geographicalDomainWebDAO = null;
		GeographicalDomainVO geographicalDomainVO = null;
		GeographicalDomainVO geographicalDomainVOPre = null;

		int updateCount = 0;

		try {
			geographicalDomainDAO = new GeographicalDomainDAO();
			geographicalDomainWebDAO = new GeographicalDomainWebDAO();
			Date currentDate = new Date();
			geographicalDomainVO = new GeographicalDomainVO();

			if (geographicalDomainList != null && !geographicalDomainList.isEmpty()) {

				nameAlreadyExist = geographicalDomainList.stream()
						.filter(x -> !x.getGrphDomainCode().equals(requestVO.getGrphDomainCode())
								&& x.getGrphDomainName().compareTo(requestVO.getGrphDomainName()) == 0)
						.findAny().isPresent() ? true : false;
				shortNameAlreadyExist = geographicalDomainList.stream()
						.filter(x -> !x.getGrphDomainCode().equals(requestVO.getGrphDomainCode())
								&& x.getGrphDomainShortName().compareTo(requestVO.getGrphDomainShortName()) == 0)
						.findAny().isPresent() ? true : false;

				geographicalDomainVOPre = geographicalDomainList.stream()
						.filter(x -> x.getGrphDomainCode().equals(requestVO.getGrphDomainCode()))
						.collect(Collectors.toList()).get(0);

			}
			if (nameAlreadyExist) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "isgeographicalDomainNameExist=true");
				}
				throw new BTSLBaseException(this.getClass().getName().toString(), methodName,
						"grphdomain.operation.msg.namealreadyexist", 0, null);
			}
			// if GrphDomainShortName is already exist then show error message.
			if (shortNameAlreadyExist) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "isgeographicalDomainShortNameExist=true");
				}
				throw new BTSLBaseException(this.getClass().getName().toString(), methodName,
						"grphdomain.operation.msg.shortnamealreadyexist", 0, null);
			}

			geographicalDomainVO.setCreatedBy(geographicalDomainVOPre.getCreatedBy());
			geographicalDomainVO.setCreatedOn(geographicalDomainVOPre.getCreatedOn());
			geographicalDomainVO.setDescription(requestVO.getDescription());
			geographicalDomainVO.setGrphDomainCode(requestVO.getGrphDomainCode());
			geographicalDomainVO.setGrphDomainName(requestVO.getGrphDomainName());
			geographicalDomainVO.setGrphDomainShortName(requestVO.getGrphDomainShortName());
			geographicalDomainVO.setGrphDomainType(requestVO.getGrphDomainType());
			geographicalDomainVO.setModifiedBy(userVO.getUserID());
			geographicalDomainVO.setModifiedOn(currentDate);
			geographicalDomainVO.setParentDomainCode(requestVO.getParentDomainCode());
			geographicalDomainVO.setNetworkCode(userVO.getNetworkID());
			geographicalDomainVO.setStatus(requestVO.getStatus());
			geographicalDomainVO.setLastModifiedTime(geographicalDomainVOPre.getLastModifiedTime());
			geographicalDomainVO.setIsDefault(requestVO.getIsDefault());

			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()
					&& PretupsI.YES.equals(requestVO.getIsDefault())) {
				geographicalDomainWebDAO.updatedeDefaultGeography(con, geographicalDomainVO.getNetworkCode(),
						geographicalDomainVO.getParentDomainCode());
			}
			updateCount = geographicalDomainWebDAO.updateGeographicalDomain(con, geographicalDomainVO);
			if (con != null) {
				if (updateCount > 0) {
					con.commit();
					response.setStatus(PretupsI.RESPONSE_SUCCESS);
					response.setMessageCode(PretupsErrorCodesI.GEOGRAPHICAL_DOMAIN_MODIFIED_SUCCESSFULLY);
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEOGRAPHICAL_DOMAIN_MODIFIED_SUCCESSFULLY, null);
					response.setMessage(resmsg);
					responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);

				} else {
					con.rollback();
					throw new BTSLBaseException(this, methodName, "grphdomain.addgrphdomain.msg.addunsuccess",
							methodName);
				}
			}
		} catch (BTSLBaseException be) {
			LOG.error("", "Exceptin:e=" + be);
			LOG.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception ex) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.errorTrace(methodName, ex);
			LOG.error(methodName, "Exception = " + ex.getMessage());
		} finally {

			if (con != null)
				con.close();
		}

		return response;
	}

	public BaseResponse deleteGeoDomain(Connection con, Locale locale, HttpServletResponse responseSwag, UserVO userVO,
			GeoDomainDeleteRequestVO requestVO) throws SQLException {
		final String methodName = "deleteGeoDomain";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}
		BaseResponse response = new BaseResponse();

		GeographicalDomainDAO geographicalDomainDAO = null;
		GeographicalDomainWebDAO geographicalDomainWebDAO = null;
		GeographicalDomainVO geographicalDomainVO = null;
		GeographicalDomainVO geographicalDomainVOPre = null;

		int deleteCount = 0;

		try {
			geographicalDomainDAO = new GeographicalDomainDAO();
			geographicalDomainWebDAO = new GeographicalDomainWebDAO();
			Date currentDate = new Date();
			geographicalDomainVO = new GeographicalDomainVO();

			if (geographicalDomainList != null && !geographicalDomainList.isEmpty()) {
				geographicalDomainVOPre = geographicalDomainList.stream()
						.filter(x -> x.getGrphDomainCode().equals(requestVO.getGrphDomainCode()))
						.collect(Collectors.toList()).get(0);
			}
			if (geographicalDomainVOPre != null) {
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE)))
						.booleanValue() && PretupsI.YES.equals(geographicalDomainVOPre.getIsDefault())) {
					throw new BTSLBaseException(this, methodName, "grphdomain.operation.msg.deletedefaultProfile",
							methodName);
				}
				// if requested Domain has active children then show the error
				// message.
				if (geographicalDomainWebDAO.isGeographicalDomainActive(con, requestVO.getGrphDomainCode())) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(methodName, "isgeographicalDomainActive=true");
					}
					throw new BTSLBaseException(this, methodName, "grphdomain.operation.msg.domainactive", methodName);
				}
				// if any active channel user is associated with the requested
				// Domain then show the error message.
				if (new UserGeographiesDAO().isActiveUserAssociatedWithGrphDomain(con, requestVO.getGrphDomainCode())) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(methodName, "isActiveUserAssociatedWithGrphDomain=true");
					}
					throw new BTSLBaseException(this, methodName, "grphdomain.operation.msg.activeuser", methodName);
				}

				geographicalDomainVO.setCreatedBy(geographicalDomainVOPre.getCreatedBy());
				geographicalDomainVO.setCreatedOn(geographicalDomainVOPre.getCreatedOn());
				geographicalDomainVO.setDescription(geographicalDomainVOPre.getDescription());
				geographicalDomainVO.setGrphDomainCode(requestVO.getGrphDomainCode());
				geographicalDomainVO.setGrphDomainName(geographicalDomainVOPre.getGrphDomainName());
				geographicalDomainVO.setGrphDomainShortName(geographicalDomainVOPre.getGrphDomainShortName());
				geographicalDomainVO.setGrphDomainType(geographicalDomainVOPre.getGrphDomainType());
				geographicalDomainVO.setModifiedBy(userVO.getUserID());
				geographicalDomainVO.setModifiedOn(currentDate);
				geographicalDomainVO.setParentDomainCode(geographicalDomainVOPre.getParentDomainCode());
				geographicalDomainVO.setNetworkCode(userVO.getNetworkID());
				geographicalDomainVO.setStatus(PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_DELETE);
				geographicalDomainVO.setLastModifiedTime(geographicalDomainVOPre.getLastModifiedTime());
				geographicalDomainVO.setIsDefault(PretupsI.NO);

				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE)))
						.booleanValue() && PretupsI.YES.equals(requestVO.getIsDefault())) {
					geographicalDomainWebDAO.updatedeDefaultGeography(con, geographicalDomainVO.getNetworkCode(),
							geographicalDomainVO.getParentDomainCode());
				}
				deleteCount = geographicalDomainWebDAO.updateGeographicalDomain(con, geographicalDomainVO);
				if (con != null) {
					if (deleteCount > 0) {
						con.commit();
						response.setStatus(PretupsI.RESPONSE_SUCCESS);
						response.setMessageCode(PretupsErrorCodesI.GEOGRAPHICAL_DOMAIN_DELETED_SUCCESSFULLY);
						String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEOGRAPHICAL_DOMAIN_DELETED_SUCCESSFULLY, null);
						response.setMessage(resmsg);
						responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);

					} else {
						con.rollback();
						throw new BTSLBaseException(this, methodName, "grphdomain.addgrphdomain.msg.addunsuccess",
								methodName);
					}
				}
			}
		} catch (BTSLBaseException be) {
			LOG.error("", "Exceptin:e=" + be);
			LOG.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception ex) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.errorTrace(methodName, ex);
			LOG.error(methodName, "Exception = " + ex.getMessage());
		} finally {

			if (con != null)
				con.close();
		}

		return response;
	}
}
