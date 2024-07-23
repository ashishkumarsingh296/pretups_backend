package com.restapi.networkadmin.servicetypeselectormapping.service;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingDAO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.networkadmin.servicetypeselectormapping.requestVO.ModifyServiceTypeSelectorMappingRequestVO;
import com.restapi.networkadmin.servicetypeselectormapping.requestVO.ServiceTypeSelectorMappingRequestVO;
import com.restapi.networkadmin.servicetypeselectormapping.responseVO.SaveServiceTypeSelectorMappingResponseVO;
import com.restapi.networkadmin.servicetypeselectormapping.responseVO.ServiceTypeResponseVO;
import com.restapi.networkadmin.servicetypeselectormapping.responseVO.ServiceTypeSelectorMappingDetailsVO;
import com.restapi.networkadmin.servicetypeselectormapping.responseVO.ServiceTypeSelectorMappingListResponseVO;
import com.restapi.networkadmin.servicetypeselectormapping.responseVO.ViewServiceTypeSelectorMappingResponseVO;
import com.restapi.networkadmin.servicetypeselectormapping.serviceI.ServiceTypeSelectorMappingServiceI;

@Service
public class ServiceTypeSelectorMappingServiceImpl implements ServiceTypeSelectorMappingServiceI {

	public static final Log LOG = LogFactory.getLog(ServiceTypeSelectorMappingServiceImpl.class.getName());
	public static final String CLASS_NAME = "ServiceTypeSelectorMappingServiceImpl";

	@Override
	public ArrayList loadservicetypeList(Connection con, UserVO userVO) throws BTSLBaseException, Exception {
		final String METHOD_NAME = "loadservicetypeList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}

		ServiceKeywordDAO serviceKeywordDAO = null;
		String type = null;
		ArrayList finalList = new ArrayList();

		serviceKeywordDAO = new ServiceKeywordDAO();
		ArrayList<ServiceTypeResponseVO> responseVO = new ArrayList<>();
		ArrayList<ListValueVO> srvcList = serviceKeywordDAO.loadServiceTypeListByModule(con);
		for (ListValueVO vo : srvcList) {
			ServiceTypeResponseVO serviceType = new ServiceTypeResponseVO();
			serviceType.setServiceTypeName(vo.getLabel());
			serviceType.setServiceTypeCode(vo.getValue());

			responseVO.add(serviceType);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME, "Exiting:" + METHOD_NAME);
		}
		return responseVO;
	}

	@Override
	public ServiceTypeSelectorMappingListResponseVO loadServiceTypeSelectorMappingList(Connection con, UserVO userVO,
			String serviceType, ServiceTypeSelectorMappingListResponseVO responseVO)
			throws BTSLBaseException, Exception {
		final String METHOD_NAME = "loadServiceTypeSelectorMappingList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		ArrayList<ServiceTypeResponseVO> serviceTypeList = this.loadservicetypeList(con, userVO);
		boolean isServiceTypePresent = false;
		for (ServiceTypeResponseVO vo : serviceTypeList) {
			if (vo.getServiceTypeCode().equals(serviceType)) {
				isServiceTypePresent = true;
				break;
			}

		}
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		
		if (!isServiceTypePresent) {
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SERVICE_TYPE_IS_INVALID, new String []{serviceType});
			responseVO.setMessageCode(PretupsErrorCodesI.SERVICE_TYPE_IS_INVALID);
			responseVO.setMessage(msg);
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_TYPE_IS_INVALID, serviceType);
		}
		ServiceSelectorMappingDAO serviceSelectormappDao = new ServiceSelectorMappingDAO();

		List<ServiceSelectorMappingVO> selectorList = serviceSelectormappDao.loadServiceSelectorMappingDetails(con,
				serviceType);
		List<ServiceTypeSelectorMappingDetailsVO> serviceTypeSelectorMappingDetailsVOS = new ArrayList<>();
		for (ServiceSelectorMappingVO selectorVO : selectorList) {
			ServiceTypeSelectorMappingDetailsVO detailsVO = new ServiceTypeSelectorMappingDetailsVO();

			detailsVO.setSelectorName(selectorVO.getSelectorName());
			detailsVO.setSelectorCode(selectorVO.getSelectorCode());

			detailsVO.setServStatus(selectorVO.getStatus());
			detailsVO.setStatusDesc(selectorVO.getStatusDesc());
			detailsVO.setSenderSubscriberType(selectorVO.getSenderSubscriberType());
			detailsVO.setReceiverSubscriberType(selectorVO.getReceiverSubscriberType());
			detailsVO.setIsDefaultCode(selectorVO.getIsDefaultCodeStr());
			detailsVO.setServiceName(selectorVO.getServiceName());
			detailsVO.setServiceType(selectorVO.getServiceType());
			detailsVO.setSNO(selectorVO.getSno());
			serviceTypeSelectorMappingDetailsVOS.add(detailsVO);
		}
		responseVO.setServiceSelectorMappingList(serviceTypeSelectorMappingDetailsVOS);
		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME, "Exiting:" + METHOD_NAME);
		}
		return responseVO;
	}

	@Override
	public SaveServiceTypeSelectorMappingResponseVO addServiceTypeSelectorMapping(Connection con, UserVO userVO,
			ServiceTypeSelectorMappingRequestVO requestVO, SaveServiceTypeSelectorMappingResponseVO response)
			throws BTSLBaseException, Exception {

		final String METHOD_NAME = "addServiceTypeSelectorMapping";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered ");
		}
		if (requestVO.getServiceType() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_TYPE_IS_NULL, "");
		if (requestVO.getServiceName() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_TYPE_NAME_IS_NULL, "");
		if (requestVO.getSenderSubscriberType() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SENDER_SUBSCRIBER_TYPE_NULL, "");
		if (requestVO.getReceiverSubscriberType() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.RECIVER_SUBSCRIBER_TYPE_NULL, "");
		if (requestVO.getIsDefault() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DEFAULT_IS_NULL, "");
		if (requestVO.getSrvStatus() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.STATUS_IS_NULL, "");
		if (requestVO.getProductCode() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PRODUCT_CODE_IS_NULL, "");
		if (requestVO.getProductName() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PRODUCT_NAME_IS_NULL, "");
		if (requestVO.getServiceType().isBlank())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_TYPE_IS_EMPTY, "");
		if (requestVO.getServiceName().isBlank())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_TYPE_NAME_IS_EMPTY, "");
		if (requestVO.getSenderSubscriberType().isBlank())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SENDER_SUBSCRIBER_TYPE_IS_EMPTY, "");
		if (requestVO.getReceiverSubscriberType().isBlank())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.RECEIVER_SUBSCRIBER_TYPE_IS_EMPTY, "");
		if (requestVO.getIsDefault().isBlank())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DEFAULT_FIELD_IS_EMPTY, "");
		if (requestVO.getSrvStatus().isBlank())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.STATUS_FIELD_IS_EMPTY, "");
		if (requestVO.getProductCode().isBlank())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PRODUCT_CODE_IS_EMPTY, "");
		if (requestVO.getProductName().isBlank())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PRODUCT_NAME_IS_EMPTY, "");
		if (!(requestVO.getSenderSubscriberType().equals(PretupsI.SERVICE_TYPE_PRE)
				|| requestVO.getSenderSubscriberType().equals(PretupsI.SERVICE_TYPE_POST)
				|| requestVO.getSenderSubscriberType().equals(PretupsI.ALL))) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SENDER_SUBSCRIBER_TYPE_INVALID, "");
		}
		if (!(requestVO.getReceiverSubscriberType().equals(PretupsI.SERVICE_TYPE_PRE)
				|| requestVO.getReceiverSubscriberType().equals(PretupsI.SERVICE_TYPE_POST)
				|| requestVO.getReceiverSubscriberType().equals(PretupsI.ALL))) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.RECIVER_SUBSCRIBER_TYPE_INVALID,
					"");
		}
		if (!(requestVO.getIsDefault().equals(PretupsI.YES) || requestVO.getIsDefault().equals(PretupsI.NO))) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DEFAULT_IS_INVALID, "");
		}
		if (!(requestVO.getSrvStatus().equals(PretupsI.YES) || requestVO.getSrvStatus().equals(PretupsI.SUSPEND))) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.STATUS_INVALID, "");
		}
		if(requestVO.getProductCode().length()>3) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PRODUCT_CODE_LENGTH_SHOULD_BE_BELOW_CHARACTERS, "");
		}
		if(requestVO.getProductName().length()>20) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PRODUCT_NAME_LENGTH_SHOULD_BE_BELOW_CHARACTERS, "");
		}
		ArrayList<ServiceSelectorMappingVO> selectorList = null;
		ServiceSelectorMappingDAO servicetypeSelectorMappingDao = new ServiceSelectorMappingDAO();
		ServiceSelectorMappingDAO serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();
		ServiceSelectorMappingVO serviceSelectorMappingVO = new ServiceSelectorMappingVO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		ServiceTypeSelectorMappingListResponseVO serviceTypeSelectorMappingListResponseVO = this
				.loadServiceTypeSelectorMappingList(con, userVO, requestVO.getServiceType(),
						new ServiceTypeSelectorMappingListResponseVO());
		List<ServiceTypeSelectorMappingDetailsVO> mappingList = serviceTypeSelectorMappingListResponseVO
				.getServiceSelectorMappingList();
		ArrayList<ServiceTypeResponseVO> serviceTypeList = this.loadservicetypeList(con, userVO);

		boolean isServiceTypeExist = false;
		for (ServiceTypeResponseVO vo : serviceTypeList) {
			if ((vo.getServiceTypeName()).equals(requestVO.getServiceName())) {
				isServiceTypeExist = true;
				break;
			}
		}
		
		if (!isServiceTypeExist) {
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SERVICE_TYPE_NAME_INVALID, new String []{requestVO.getServiceName()});
			response.setMessageCode(PretupsErrorCodesI.SERVICE_TYPE_NAME_INVALID);
			response.setMessage(msg);
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_TYPE_NAME_INVALID,
					requestVO.getServiceName());
		}

		selectorList = servicetypeSelectorMappingDao.loadServiceSelectorMappingDetails(con, requestVO.getServiceType());
		
		String selCode = requestVO.getProductCode().trim();
		String senSubType = requestVO.getSenderSubscriberType().trim();
		String recSubType = requestVO.getReceiverSubscriberType().trim();
		if (mappingList.isEmpty()) {
			serviceSelectorMappingVO.setSelectorCode(selCode);
		} else {
			ServiceTypeSelectorMappingDetailsVO serviceTypeSelectorMappingDetailsVO = null;
			int selectorLists = selectorList.size();
			for (int i = 0; i < selectorLists; i++) {
				serviceTypeSelectorMappingDetailsVO = mappingList.get(i);
				if (selCode.equalsIgnoreCase((serviceTypeSelectorMappingDetailsVO.getSelectorCode())) || requestVO.getProductName().equalsIgnoreCase(serviceTypeSelectorMappingDetailsVO.getSelectorName())) {
					
						throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
								PretupsErrorCodesI.PRODUCT_NAME_OR_PRODUCT_CODE_ALREADY_EXISTS, "");
					
				}
			}
			if (!(BTSLUtil.isAlphaNumeric(selCode))) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
						PretupsErrorCodesI.PLEASE_ENTER_PRODUCT_CODE_IN_ALPHANUMERIC_FORMAT, "");
			} else {
				serviceSelectorMappingVO.setSelectorCode(selCode);
			}
		}
		serviceSelectorMappingVO.setCreatedBy(userVO.getUserID());
		serviceSelectorMappingVO.setModifiedBy(userVO.getUserID());
		if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(requestVO.getIsDefault())) {
			if (PretupsI.SUSPEND.equals(requestVO.getSrvStatus())) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DEFAULT_SELECTOR_CAN_NOT_BE_SUSPENDED,
						"");
			}
			Iterator it = mappingList.iterator();
			ServiceTypeSelectorMappingDetailsVO serviceTypeSelectorMappingDetailsVO = null;

			while (it.hasNext()) {
				serviceTypeSelectorMappingDetailsVO = (ServiceTypeSelectorMappingDetailsVO) it.next();
				if (serviceTypeSelectorMappingDetailsVO.getIsDefaultCode().equalsIgnoreCase(requestVO.getSrvStatus())) {
					throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
							PretupsErrorCodesI.DEFAULT_MAPPING_ALREADY_EXIST_FOR_THIS_SERVICE, "");
				}
			}
		}

		LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.STATUS_TYPE, requestVO.getSrvStatus());
		serviceSelectorMappingVO.setStatusDesc(lookupsVO.getLookupName());
		serviceSelectorMappingVO.setStatus(requestVO.getSrvStatus());

		serviceSelectorMappingVO.setMappingType(PretupsI.SERVICE_TYPE_SELECTOR_MAPPING_TYPE);

		int maxProducts = Integer.parseInt(Constants.getProperty("MAX_PRODUCTS_ALWD_PR_SERVICE"));
		if (servicetypeSelectorMappingDao.getNoOfProductsAlready(con, requestVO.getServiceType(),
				requestVO.getProductCode()) >= maxProducts) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.MAX_NO_OF_PRODUCTS_ALLOWED_PER_SERVICE_LIMIT_REACHED, "");
		}

		if (servicetypeSelectorMappingDao.isMappingExistSubscriberSelector(con, serviceSelectorMappingVO.getSno(),
				requestVO.getServiceType(), requestVO.getProductCode(), requestVO.getProductName())) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.PRODUCT_NAME_OR_PRODUCT_CODE_ALREADY_EXISTS, "");
		}

		Date currentDate = new Date(System.currentTimeMillis());
		boolean isSucess = false;
		serviceSelectorMappingVO.setServiceType(requestVO.getServiceType());
		serviceSelectorMappingVO.setSelectorCode(requestVO.getProductCode());
		serviceSelectorMappingVO.setSelectorName(requestVO.getProductName());
		serviceSelectorMappingVO.setStatus(requestVO.getSrvStatus());
		serviceSelectorMappingVO.setCreatedOn(currentDate);
		serviceSelectorMappingVO.setModifiedOn(currentDate);
		serviceSelectorMappingVO.setModifiedBy(userVO.getUserID());
		serviceSelectorMappingVO.setIsDefaultCodeStr(requestVO.getIsDefault());
		serviceSelectorMappingVO.setSenderSubscriberType(requestVO.getSenderSubscriberType());
		serviceSelectorMappingVO.setReceiverSubscriberType(requestVO.getReceiverSubscriberType());
		serviceSelectorMappingVO.setServiceName(requestVO.getServiceName());
		String sNO = String.valueOf(IDGenerator.getNextID(PretupsI.SERVICE_SELECTOR_ID, TypesI.ALL));
		serviceSelectorMappingVO.setSno(sNO);
		isSucess = serviceSelectorMappingDAO.addServiceSelectorMappingDetails(con, serviceSelectorMappingVO);
		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME, "Exiting:" + METHOD_NAME);
		}

		if (isSucess) {
			/* con.commit(); */
			con.commit();

			final AdminOperationVO adminOperationVO = new AdminOperationVO();
			adminOperationVO.setSource(PretupsI.SERVICE_TYPE_SELECTOR_MAPPING);
			adminOperationVO.setDate(currentDate);
			adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_ADDED_SUCCESSFULLY,
					new String[] { requestVO.getProductName() });
			adminOperationVO.setInfo(resmsg);
			adminOperationVO.setLoginID(userVO.getLoginID());
			adminOperationVO.setUserID(userVO.getUserID());
			adminOperationVO.setCategoryCode(userVO.getCategoryCode());
			adminOperationVO.setNetworkCode(userVO.getNetworkID());
			adminOperationVO.setMsisdn(userVO.getMsisdn());
			AdminOperationLog.log(adminOperationVO);

			response.setStatus((HttpStatus.SC_OK));
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_ADDED_SUCCESSFULLY);
			response.setSNO(sNO);
			return response;

		} else {
			con.rollback();
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_ADD_FAILED, "");
		}

	}

	@Override
	public SaveServiceTypeSelectorMappingResponseVO modifyServiceTypeSelectorMapping(Connection con, UserVO userVO,
			ModifyServiceTypeSelectorMappingRequestVO requestVO, SaveServiceTypeSelectorMappingResponseVO response)
			throws BTSLBaseException, Exception {

		final String METHOD_NAME = "addServiceTypeSelectorMapping";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered ");
		}

		if (requestVO.getIsDefault() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DEFAULT_IS_NULL, "");
		if (requestVO.getSrvStatus() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.STATUS_IS_NULL, "");
		if (requestVO.getSNo() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SNO_NULL, "");
		if (requestVO.getSNo().isBlank())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SNO_IS_EMPTY, "");
		if (requestVO.getIsDefault().isBlank())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DEFAULT_FIELD_IS_EMPTY, "");
		if (requestVO.getSrvStatus().isBlank())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.STATUS_FIELD_IS_EMPTY, "");
		if (!(requestVO.getIsDefault().equals(PretupsI.YES) || requestVO.getIsDefault().equals(PretupsI.NO))) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DEFAULT_IS_INVALID, "");
		}
		if (!(requestVO.getSrvStatus().equals(PretupsI.YES) || requestVO.getSrvStatus().equals(PretupsI.SUSPEND))) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.STATUS_INVALID, "");
		}
		ServiceSelectorMappingDAO serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();
		ServiceSelectorMappingVO serviceSelectorMappingVO = serviceSelectorMappingDAO
				.loadServiceSelectorMappingDetailsBySNo(con, requestVO.getSNo());
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		if (serviceSelectorMappingVO == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_SERIALNO, "");

		if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(requestVO.getIsDefault())) {
			if (PretupsI.SUSPEND.equals(requestVO.getSrvStatus())) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
						PretupsErrorCodesI.DEFAULT_SELECTOR_CAN_NOT_BE_SUSPENDED, "");
			}

		}

		LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.STATUS_TYPE, requestVO.getSrvStatus());
		serviceSelectorMappingVO.setStatusDesc(lookupsVO.getLookupName());
		serviceSelectorMappingVO.setStatus(requestVO.getSrvStatus());
		serviceSelectorMappingVO.setMappingType(PretupsI.SERVICE_TYPE_SELECTOR_MAPPING_TYPE);
		serviceSelectorMappingVO.setModifiedBy(userVO.getUserID());
		Date currentDate = new Date(System.currentTimeMillis());
		boolean isSucess = false;

		serviceSelectorMappingVO.setStatus(requestVO.getSrvStatus());
		serviceSelectorMappingVO.setModifiedOn(currentDate);
		serviceSelectorMappingVO.setIsDefaultCodeStr(requestVO.getIsDefault());
		isSucess = serviceSelectorMappingDAO.modifySubscriberSelectorWithModifyOn(con, serviceSelectorMappingVO);
		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME, "Exiting:" + METHOD_NAME);
		}

		if (isSucess) {
			/* con.commit(); */
			con.commit();
			final AdminOperationVO adminOperationVO = new AdminOperationVO();
			adminOperationVO.setSource(PretupsI.SERVICE_TYPE_SELECTOR_MAPPING);
			adminOperationVO.setDate(currentDate);
			adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_MODIFIED_SUCCESSFULLY,
					new String[] { serviceSelectorMappingVO.getSelectorName() });
			adminOperationVO.setInfo(resmsg);
			adminOperationVO.setLoginID(userVO.getLoginID());
			adminOperationVO.setUserID(userVO.getUserID());
			adminOperationVO.setCategoryCode(userVO.getCategoryCode());
			adminOperationVO.setNetworkCode(userVO.getNetworkID());
			adminOperationVO.setMsisdn(userVO.getMsisdn());
			AdminOperationLog.log(adminOperationVO);
			response.setStatus((HttpStatus.SC_OK));
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_MODIFIED_SUCCESSFULLY);
			response.setSNO(requestVO.getSNo());
			return response;

		} else {
			con.rollback();
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_MODIFY_FAILED, "");

		}

	}

	@Override
	public ViewServiceTypeSelectorMappingResponseVO viewServiceTypeSelectorMapping(Connection con, UserVO userVO,
			String sNO, ViewServiceTypeSelectorMappingResponseVO response) throws BTSLBaseException, Exception {
		final String METHOD_NAME = "viewServiceTypeSelectorMapping";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		ViewServiceTypeSelectorMappingResponseVO detailsVO = new ViewServiceTypeSelectorMappingResponseVO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		if (sNO == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SNO_NULL, "");
		ServiceSelectorMappingDAO serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();
		ServiceSelectorMappingVO serviceSelectorMappingVO = serviceSelectorMappingDAO
				.loadServiceSelectorMappingDetailsBySNo(con, sNO);
		if (serviceSelectorMappingVO == null) {
			ServiceSelectorMappingVO inactiveServiceSelectorMappingVO = serviceSelectorMappingDAO
					.loadMappingDetailsBySNo(con, sNO);
			List<ServiceTypeResponseVO> serviceList = this.loadservicetypeList(con, userVO);
			if(inactiveServiceSelectorMappingVO != null) {
				Optional<String> serviceType = serviceList.stream()
					.filter(o -> o.getServiceTypeCode().equals(inactiveServiceSelectorMappingVO.getServiceType()))
					.map(o -> o.getServiceTypeCode()).findAny();
			
				if (!serviceType.isPresent()) {
					throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_TYPE_INACTIVE, "");
			    }
			}
			if (inactiveServiceSelectorMappingVO != null
					&& inactiveServiceSelectorMappingVO.getStatus().equals(PretupsI.NO)) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
						PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_INACTIVE, "");
			}

			else
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_SERIALNO, "");		
			}
		else {

			response.setSelectorName(serviceSelectorMappingVO.getSelectorName());
			response.setSelectorCode(serviceSelectorMappingVO.getSelectorCode());

			response.setServStatus(serviceSelectorMappingVO.getStatus());
			response.setStatusDesc(serviceSelectorMappingVO.getStatusDesc());
			response.setSenderSubscriberType(serviceSelectorMappingVO.getSenderSubscriberType());
			response.setReceiverSubscriberType(serviceSelectorMappingVO.getReceiverSubscriberType());
			response.setIsDefaultCode(serviceSelectorMappingVO.getIsDefaultCodeStr());
			response.setServiceName(serviceSelectorMappingVO.getServiceName());
			response.setServiceType(serviceSelectorMappingVO.getServiceType());
			response.setSNO(serviceSelectorMappingVO.getSno());
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.LOAD_SERVICE_TYPE_SELECTOR_MAPPING_SUCCESSFULLY, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LOAD_SERVICE_TYPE_SELECTOR_MAPPING_SUCCESSFULLY);

		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME, "Exiting:" + METHOD_NAME);
		}

		return response;
	}

	@Override
	public SaveServiceTypeSelectorMappingResponseVO deleteServiceTypeSelectorMapping(Connection con, UserVO userVO,
			String sNO, SaveServiceTypeSelectorMappingResponseVO response) throws BTSLBaseException, Exception {
		final String METHOD_NAME = "deleteServiceTypeSelectorMapping";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		boolean isSucess = false;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		Date currentDate = new Date(System.currentTimeMillis());

		if (sNO == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SNO_NULL, "");
		ServiceSelectorMappingDAO serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();
		ServiceSelectorMappingVO serviceSelectorMappingVO = serviceSelectorMappingDAO
				.loadServiceSelectorMappingDetailsBySNo(con, sNO);
		if (serviceSelectorMappingVO == null) {
			ServiceSelectorMappingVO inactiveServiceSelectorMappingVO = serviceSelectorMappingDAO
					.loadMappingDetailsBySNo(con, sNO);
			List<ServiceTypeResponseVO> serviceList = this.loadservicetypeList(con, userVO);
			if(inactiveServiceSelectorMappingVO != null) {
				Optional<String> serviceType = serviceList.stream()
					.filter(o -> o.getServiceTypeCode().equals(inactiveServiceSelectorMappingVO.getServiceType()))
					.map(o -> o.getServiceTypeCode()).findAny();
			
				if (!serviceType.isPresent()) {
					throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_TYPE_INACTIVE, "");
			    }
			}
			if (inactiveServiceSelectorMappingVO != null
					&& inactiveServiceSelectorMappingVO.getStatus().equals(PretupsI.NO)) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
						PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_INACTIVE, "");
			}

			else
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_SERIALNO, "");		
			}
		else {
			serviceSelectorMappingVO.setModifiedOn(currentDate);
			serviceSelectorMappingVO.setModifiedBy(userVO.getUserID());
			isSucess = serviceSelectorMappingDAO.deleteSubscriberSelector(con, serviceSelectorMappingVO);
			if (isSucess) {
				con.commit();
				final AdminOperationVO adminOperationVO = new AdminOperationVO();
				adminOperationVO.setSource(PretupsI.SERVICE_TYPE_SELECTOR_MAPPING);
				adminOperationVO.setDate(currentDate);
				adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
				String resmsg = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_DELETED_SUCCESSFULLY,
						new String[] { serviceSelectorMappingVO.getSelectorName() });
				adminOperationVO.setInfo(resmsg);
				adminOperationVO.setLoginID(userVO.getLoginID());
				adminOperationVO.setUserID(userVO.getUserID());
				adminOperationVO.setCategoryCode(userVO.getCategoryCode());
				adminOperationVO.setNetworkCode(userVO.getNetworkID());
				adminOperationVO.setMsisdn(userVO.getMsisdn());
				AdminOperationLog.log(adminOperationVO);
				response.setStatus((HttpStatus.SC_OK));
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_DELETED_SUCCESSFULLY);
				response.setSNO(sNO);
			} else {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
						PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_DELETE_FAILED, "");
			}

		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME, "Exiting:" + METHOD_NAME);
		}

		return response;
	}

}
