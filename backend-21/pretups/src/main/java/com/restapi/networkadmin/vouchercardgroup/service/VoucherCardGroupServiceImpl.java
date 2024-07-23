package com.restapi.networkadmin.vouchercardgroup.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import lombok.*;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.poi.ss.formula.functions.LinearRegressionFunction.FUNCTION;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListSorterUtil;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.BonusAccountDetailsVO;
import com.btsl.pretups.cardgroup.businesslogic.BonusBundleDAO;
import com.btsl.pretups.cardgroup.businesslogic.BonusBundleDetailVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupBL;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupCache;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVersionDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVersionVO;
import com.btsl.pretups.channel.profile.businesslogic.CalculateVoucherTransferRuleVO;
import com.btsl.pretups.channel.profile.businesslogic.CardGroupStatusVO;
import com.btsl.pretups.channel.profile.businesslogic.DefaultCardGroupVO;
import com.btsl.pretups.channel.profile.businesslogic.LoadVersionListRequestVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.vomsproduct.businesslogic.VoucherTypeVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Functions;
import com.ibm.icu.util.Calendar;
import com.restapi.networkadmin.requestVO.CardGroupStatusRequestVO;
import com.restapi.networkadmin.responseVO.C2SCardGroupStatusSaveResponseVO;
import com.restapi.networkadmin.vouchercardgroup.request.ChangeVoucherCardGroupStatusListRequestVO;
import com.restapi.networkadmin.vouchercardgroup.request.ChangeVoucherCardGroupStatusRequest;
import com.restapi.networkadmin.vouchercardgroup.request.DefaultVoucherCardGroupRequestVO;
import com.restapi.networkadmin.vouchercardgroup.request.ModifyVoucherCardGroupDetailsRequestVO;
import com.restapi.networkadmin.vouchercardgroup.request.VersionDetailsAndStatusVO;
import com.restapi.networkadmin.vouchercardgroup.request.VoucherCardGroupTransferValueRequestVO;
import com.restapi.networkadmin.vouchercardgroup.request.VoucherCardGroupVersionListDetails;
import com.restapi.networkadmin.vouchercardgroup.request.VoucherGroupDetails;
import com.restapi.networkadmin.vouchercardgroup.request.VoucherGroupDetailsRequestVO;
import com.restapi.networkadmin.vouchercardgroup.response.AddVoucherGroupDropDownResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.CalculateTransferValueResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.DefaultVoucherCardGroupResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.DenaminationDetailsDropdownsResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.LoadVoucherCardGroupServicesResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.SaveVoucherGroupResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.ViewVoucherCardGroupResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.VoucherCardGroupSetDetails;
import com.restapi.networkadmin.vouchercardgroup.response.VoucherCardGroupStatusResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.VoucherCardGroupVersionNumberListResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.VoucherCardGroupVersionResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.VoucherTransferValueResponseVO;
import com.restapi.networkadmin.vouchercardgroup.serviceI.VoucherCardGroupServiceI;
import com.web.pretups.transfer.businesslogic.TransferWebDAO;

@Service("VoucherCardGroupServiceI")
public class VoucherCardGroupServiceImpl implements VoucherCardGroupServiceI {
	public static final String CLASS_NAME = "VoucherCardGroupServiceImpl";
	public static final Log LOG = LogFactory.getLog(VoucherCardGroupServiceImpl.class.getName());

	@Override
	public LoadVoucherCardGroupServicesResponseVO loadServiceAndSubServiceList(Connection con, String networkID) throws BTSLBaseException, Exception{

		LoadVoucherCardGroupServicesResponseVO response = new LoadVoucherCardGroupServicesResponseVO();

		final String methodName = "loadServiceAndSubServiceList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		// load sub service dropdown
		// ===============================================================================================
		List<ListValueVO> subServiceList = ServiceSelectorMappingCache.loadSelectorDropDownForCardGroup();
		List<ListValueVO> filterSubServiceList = new ArrayList<>();
		for(ListValueVO vo : subServiceList) {
			if(vo.getValue().split(":")[0].equals(PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_SERVICES))) {
				filterSubServiceList.add(vo);
			}
		}
		response.setCardGroupSubServiceList(filterSubServiceList);
		// ===============================================================================================
		final CardGroupDAO cardGroupDAO = new CardGroupDAO();
		// load the card group set names
		//response.setOrigCardGroupSetNameList(cardGroupDAO.loadCardGroupSet(con, networkID, PretupsI.P2P_MODULE));
		ArrayList serviceTypeList = cardGroupDAO.loadServiceTypeList(con, networkID, PretupsI.P2P_MODULE);
		response.setServiceTypeList(filterServiceTypeList(serviceTypeList, PretupsI.CARD_GROUP_VMS));
		if (response.getServiceTypeList().isEmpty()) {
			throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.NO_SERVICE_EXISTS);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting");
		}


		return response;
	}

	@Override
	public ViewVoucherCardGroupResponseVO viewVoucherCardGroupDetails(Connection con, UserVO userVO,
																	  String cardGroupSetId, String version) throws BTSLBaseException, Exception {
		final String methodName = "viewVoucherCardGroupDetails";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}
		ViewVoucherCardGroupResponseVO response = new ViewVoucherCardGroupResponseVO();
		CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();

		final CardGroupDAO cardGroupDAO = new CardGroupDAO();
		ArrayList<CardGroupSetVO> cardGroupSetNameList =cardGroupDAO.loadCardGroupSet(con, userVO.getNetworkID(), PretupsI.P2P_MODULE);
		CardGroupSetVO setVO = null;
		StringBuilder cardGroupSuService=new StringBuilder();
		String cardGroupSuSer;
		for (int i = 0, j = cardGroupSetNameList.size(); i < j; i++) {
			setVO = (CardGroupSetVO) cardGroupSetNameList.get(i);
			if (cardGroupSetId.equals(setVO.getCardGroupSetID())) {
				response.setCardGroupSetName(setVO.getCardGroupSetName());
				cardGroupSuService.setLength(0);
				cardGroupSuService.append(setVO.getServiceType());
				cardGroupSuService.append(":");
				cardGroupSuService.append(setVO.getSubServiceType());
				cardGroupSuSer=cardGroupSuService.toString();
				response.setCardGroupSubServiceID(cardGroupSuSer);
				response.setCardGroupSubServiceName(setVO.getSubServiceTypeDescription());
				response.setServiceTypeId(setVO.getServiceType());
				response.setServiceTypedesc(setVO.getServiceTypeDesc());
				response.setSetType(setVO.getSetType());
				response.setSetTypeName(setVO.getSetTypeName());
				response.setDefaultCardGroupRequired(setVO.getDefaultCardGroup());
				break;
			}
		}
		final CardGroupSetDAO cardGroupSetNewDAO = new CardGroupSetDAO();
		CardGroupDAO cardGroupDao = new CardGroupDAO();


		final Date applicableFromDate = cardGroupSetNewDAO.loadCardGroupSetVersionApplicableFromDate(con, cardGroupSetId, version);
		response.setApplicableFromDate(BTSLUtil.getDateStringFromDate(applicableFromDate));
		final int hour = BTSLUtil.getHour(applicableFromDate);
		final int minute = BTSLUtil.getMinute(applicableFromDate);
		final String time = BTSLUtil.getTimeinHHMM(hour, minute);

		response.setApplicableFromHour(time);
		response.setOldApplicableFromDate(BTSLUtil.getDateStringFromDate(applicableFromDate));
		response.setOldApplicableFromHour(time);
		response.setVersion(version);

		// load the Card Group Details info
		Map<String, Object> data = new HashMap<>();
		data.put("networkCode", userVO.getNetworkID());
		data.put("subServiceTypeDesc",response.getCardGroupSubServiceName());
		data.put("numberOfDays","30");
		data.put("cardGroupSetId", cardGroupSetId);
		data.put("version", version);
		data.put("serviceTypeDesc",response.getServiceTypedesc());
		Map<String, Object> requestObject = new HashMap<>();
		String password = BTSLUtil.decryptText(userVO.getPassword());
		userVO.setPassword(password);
		PretupsRestUtil.setLoginDetailsInRequest(requestObject, userVO);
		requestObject.put("data", data);
		PretupsRestClient pretupsRestClient=new PretupsRestClient();
		String responseString = pretupsRestClient.postJSONRequest(requestObject, PretupsRestI.VIEW_CARDGROUP);

		PretupsResponse<JsonNode> pretupsResponse = (PretupsResponse<JsonNode>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<JsonNode>>() {
				});
		if (pretupsResponse.getDataObject() != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode cardGroupSetListNode =  pretupsResponse.getDataObject();
			final  List<CardGroupDetailsVO> cardGroupList = Arrays.asList(objectMapper.readValue(cardGroupSetListNode.toString(), CardGroupDetailsVO[].class));
			for(int i =0;i<cardGroupList.size();i++){
				CardGroupDetailsVO cardGroupDetailsVO = cardGroupList.get(i);
				cardGroupDetailsVO.setBonusAccList(new ArrayList<>(Arrays.asList(objectMapper.readValue(cardGroupSetListNode.get(i).get("bonusAccList").toString(), BonusAccountDetailsVO[].class))));
			}
			response.setCardGroupList(new ArrayList<>(cardGroupList));
		}

		else
		{
			if(pretupsResponse.getMessageCode().equals("cardgroup.cardgroupdetails.err.msg.noproductfound"))
				throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.PRODUCTS_NOT_FOUND, "");
			else {
				throw new BTSLBaseException(CLASS_NAME, methodName, pretupsResponse.getMessageCode(), "");
			}
		}

		if (response.getCardGroupList() != null && !response.getCardGroupList().isEmpty()) {
			for (int i = 0, j = response.getCardGroupList().size(); i < j; i++) {
				final CardGroupDetailsVO cardDetailVO = (CardGroupDetailsVO) response.getCardGroupList().get(i);

				ArrayList<VoucherTypeVO> voucherTypeList = populateVoucherTypeList(con);
				cardDetailVO.setVoucherDenomination(String.valueOf(Double.parseDouble(PretupsBL.getDisplayAmount(cardDetailVO.getStartRange()))));
				response.setVoucherDenomination(cardDetailVO.getVoucherDenomination());
				response.setVoucherType(cardDetailVO.getVoucherType());
				response.setSegment(cardDetailVO.getVoucherSegment());
				response.setSelectCardGroupSetId(cardDetailVO.getCardGroupSetID());
				ArrayList denominationProfileList = populateDenominationProfileList(con, userVO,cardDetailVO.getVoucherType(),cardDetailVO.getVoucherSegment(),cardDetailVO.getVoucherDenomination());
				cardDetailVO.setVoucherTypeDesc((BTSLUtil.getVoucherTypeDesc(voucherTypeList, cardDetailVO.getVoucherType())));
				cardDetailVO.setVoucherSegmentDesc(BTSLUtil.getSegmentDesc(cardDetailVO.getVoucherSegment()));
				cardDetailVO.setProductName(BTSLUtil.getVoucherProductName(denominationProfileList, cardDetailVO.getVoucherProductId()));
				cardDetailVO.setBonusAccList((ArrayList<BonusAccountDetailsVO>)cardDetailVO.getBonusAccList());

				if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "cardDetailVO = " + cardDetailVO);
				}
			}
		}
		final ListSorterUtil sort = new ListSorterUtil();
		final ArrayList sortedList = (ArrayList) sort.doSort("startRange", null, response.getCardGroupList());
		response.setCardGroupList(sortedList);
		final StringBuffer strBuff = new StringBuffer();
		final boolean isDeleteAllowed = checkDeleteVersionAllowed(response.getSelectCardGroupSetId(), PretupsI.P2P_MODULE, strBuff);
		if (isDeleteAllowed) {
			final String format = Constants.getProperty("CARDGROUP_DATE_FORMAT");
			final Date oldDate = BTSLUtil.getDateFromDateString(response.getOldApplicableFromDate() + " " + response.getOldApplicableFromHour(), format);
			if (oldDate.after(new Date())) {
				response.setDeleteAllowed(true);
			} else {
				response.setDeleteAllowed(false);
			}
		} else {
			response.setDeleteAllowed(false);
		}

		if (response.getCardGroupList() != null && !response.getCardGroupList().isEmpty())
			this.constructFormFromVO(response, (CardGroupDetailsVO) response.getCardGroupList().get(0));


		return response;
	}
	@Override
	public VoucherCardGroupVersionResponseVO loadVoucherCardGroupversionList(Connection con, UserVO userVO,
																			 String service, String subService, String cardGroupSetType, Date applicableFromDateAndTime)
			throws BTSLBaseException, Exception {
		final String methodName = "loadVoucherCardGroupversionList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}
		VoucherCardGroupVersionResponseVO response = new VoucherCardGroupVersionResponseVO();
		CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
		final LoadVersionListRequestVO requestVO = new LoadVersionListRequestVO();

		requestVO.setModuleCode(PretupsI.CARD_GROUP_P2P);
		requestVO.setNumberOfDays("30");
		requestVO.setNetworkCode(userVO.getNetworkID());
		requestVO.setIdentifierType(userVO.getLoginID());
		String password = BTSLUtil.decryptText(userVO.getPassword());
		requestVO.setIdentifierValue(password);

		final CardGroupSetDAO cardGroupSetNewDAO = new CardGroupSetDAO();
		CardGroupDAO cardGroupDao = new CardGroupDAO();


		ArrayList<CardGroupSetVersionVO> versionList2 = cardGroupSetNewDAO.loadCardGroupSetVersionNew(con, userVO.getNetworkID(), applicableFromDateAndTime, PretupsI.P2P_MODULE);

		PretupsRestClient pretupsRestClient=new PretupsRestClient();
		String responseString = pretupsRestClient.postJSONRequest(requestVO, PretupsRestI.VIEW_CARDGROUP_VERSIONLIST);
		PretupsResponse<JsonNode> pretupsResponse =  (PretupsResponse<JsonNode>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<JsonNode>>() {
				});
		List<VoucherCardGroupVersionListDetails>  cardGroupVersionList = null;
		if (pretupsResponse.getDataObject() != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode cardGroupSetVersionListNode =  pretupsResponse.getDataObject();
			cardGroupVersionList = Arrays.asList(objectMapper.readValue(cardGroupSetVersionListNode.toString(), VoucherCardGroupVersionListDetails[].class));
		}
		ArrayList<VoucherCardGroupVersionListDetails> beforeApplicableDateFromList = new ArrayList();
		for(int i=0;i<cardGroupVersionList.size();i++) {
			VoucherCardGroupVersionListDetails vo =(VoucherCardGroupVersionListDetails)cardGroupVersionList.get(i);
			if(applicableFromDateAndTime.compareTo(vo.getApplicableFrom())>0) {

				beforeApplicableDateFromList.add(vo);
			}
		}
		List<Optional<VoucherCardGroupVersionListDetails>> latestOptionalVersionList =beforeApplicableDateFromList.stream()
				.collect( Collectors.groupingBy(VoucherCardGroupVersionListDetails:: getCardGroupSetID,Collectors
						.maxBy(Comparator.comparing(VoucherCardGroupVersionListDetails::getVersion))))
				.entrySet()
				.stream()
				.map(o->o.getValue())
				.collect(Collectors.toList());
		if(cardGroupVersionList == null||cardGroupVersionList.isEmpty()) {
			throw new BTSLBaseException(CLASS_NAME, methodName,PretupsErrorCodesI.NO_DATA_FOUND_CRITERIA);
		}
		CardGroupDAO cardGroupDAO = new CardGroupDAO();
		List<CardGroupSetVO> cardGroupsetNamelist = cardGroupDAO.loadCardGroupSet(con, userVO.getNetworkID(), PretupsI.P2P_MODULE);
		List<VoucherCardGroupSetDetails> groupSetDetailsList= new ArrayList();
		if (cardGroupsetNamelist != null && !cardGroupsetNamelist.isEmpty()) {
			CardGroupSetVO cardGroupSetVO = null;
			for (int i = 0, j = cardGroupsetNamelist.size(); i < j; i++) {
				VoucherCardGroupSetDetails groupSetDetails = new VoucherCardGroupSetDetails();
				cardGroupSetVO = (CardGroupSetVO) cardGroupsetNamelist.get(i);
				if (cardGroupSetVO.getSetType().equals(cardGroupSetType) && cardGroupSetVO.getServiceType().equals(service)&&cardGroupSetVO.getSubServiceType().equals(subService.split(":")[1])) {


					if (latestOptionalVersionList != null && !latestOptionalVersionList.isEmpty()) {
						VoucherCardGroupVersionListDetails cardGroupSetVersionVO = null;
						final ArrayList list = new ArrayList();
						for (int k = 0, l = latestOptionalVersionList.size(); k < l; k++) {
							VersionDetailsAndStatusVO versionDetailsAndDeleteStatusVO = new VersionDetailsAndStatusVO();
							cardGroupSetVersionVO = (VoucherCardGroupVersionListDetails) latestOptionalVersionList.get(k).get();
							if (cardGroupSetVersionVO.getCardGroupSetID().equals(cardGroupSetVO.getCardGroupSetID())) {
								groupSetDetails.setCardGroupSetName(cardGroupSetVO.getCardGroupSetName());
								groupSetDetails.setCardGroupSubServiceName(cardGroupSetVO.getSubServiceTypeDescription());
								groupSetDetails.setServiceTypedesc(cardGroupSetVO.getServiceTypeDesc());
								groupSetDetails.setSetTypeName(cardGroupSetVO.getSetTypeName());
								groupSetDetails.setDefaultCardGroupRequired(cardGroupSetVO.getDefaultCardGroup());
								groupSetDetails.setCardGroupSetstatus(cardGroupSetVO.getStatus());
								final String[] arr = cardGroupSetVersionVO.getCardGroupSetCombinedID().split(":");
								ArrayList cardGroupList = cardGroupDAO.loadCardGroupDetailsListByID(con, arr[0], arr[1]);
								StringBuffer strBuff = new StringBuffer();

								versionDetailsAndDeleteStatusVO.setCardGroupSetVersionVO(cardGroupSetVersionVO);
								list.add(versionDetailsAndDeleteStatusVO);
								if (list.size() == 1) {
									groupSetDetails.setSelectCardGroupSetVersionId(cardGroupSetVersionVO.getCardGroupSetCombinedID());
								}
							}
						}
						groupSetDetails.setCardGroupSetVersionList(list);
					}
					if(!groupSetDetails.getCardGroupSetVersionList().isEmpty())
						groupSetDetailsList.add(groupSetDetails);
				}

			}
		}
		if(groupSetDetailsList.isEmpty())
			throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.NO_DATA_FOUND_FOR_FILTERS_CRITERIA, 0, null, null);
		else
			response.setVoucherCardGroupDetailsList(groupSetDetailsList);




		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting  forward=");
		}
		return response;
	}


	private ArrayList filterServiceTypeList(ArrayList list, String cardGroupType) {
		final String methodName = "filterServiceTypeList";
		if(list != null & list.size() > 0) {
			Iterator itr = list.iterator();
			while (itr.hasNext())
			{
				ListValueVO vo = (ListValueVO) itr.next();
				String codeName = vo.getValue();
				if(!BTSLUtil.isNullString(cardGroupType) && !BTSLUtil.isNullString(codeName)) {
					if(PretupsI.CARD_GROUP_VMS.equals(cardGroupType) && !BTSLUtil.isVoucherService(codeName) ||
							!PretupsI.CARD_GROUP_VMS.equals(cardGroupType) && BTSLUtil.isVoucherService(codeName)) {
						itr.remove();
					}
				}
			}
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exited : list.size()="+list.size());
		}
		return list;
	}

	public boolean checkDeleteVersionAllowed(String p_cardGroupSetID, String p_moduleCode, StringBuffer strBuff) throws Exception{
		final String methodName = "checkDeleteVersionAllowed";
		StringBuilder loggerValue= new StringBuilder();
		if (LOG.isDebugEnabled()) {
			loggerValue.append("Entered p_cardGroupSetID");
			loggerValue.append(p_cardGroupSetID);
			loggerValue.append("p_moduleCode");
			loggerValue.append(p_moduleCode);
			LOG.debug(methodName,loggerValue);

		}
		boolean isAllowed = false;

		final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			final ArrayList versionList = cardGroupSetDAO.loadCardGroupSetVersionList(con, p_cardGroupSetID, p_moduleCode);
			final int versionListSize = versionList.size();
			if (versionListSize == 1) {
				isAllowed = false;
				strBuff.append(((CardGroupSetVersionVO) versionList.get(0)).getVersion());
			} else {
				isAllowed = true;
			}
			mcomCon.finalCommit();;
		} catch (BTSLBaseException ex) {
			LOG.errorTrace(methodName, ex);
			loggerValue.setLength(0);
			loggerValue.append("Exceptin:ex=");
			loggerValue.append(ex);
			LOG.error(methodName,loggerValue);
			try {
				if (con != null) {
					mcomCon.finalRollback();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			finally {
				throw ex;
			}
		} catch (SQLException ex) {
			LOG.errorTrace(methodName, ex);
			loggerValue.setLength(0);
			loggerValue.append("Exceptin:ex=");
			loggerValue.append(ex);
			LOG.error(methodName,loggerValue);
			try {
				if (con != null) {
					mcomCon.finalRollback();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			finally {
				throw ex;
			}
		} catch (Exception ex) {
			LOG.errorTrace(methodName, ex);
			loggerValue.setLength(0);
			loggerValue.append("Exceptin:ex=");
			loggerValue.append(ex);
			LOG.error(methodName,loggerValue);
			try {
				if (con != null) {
					mcomCon.finalRollback();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			finally {
				throw ex;
			}
		} finally {
			if (mcomCon != null) {
				mcomCon.close(CLASS_NAME+"#checkDeleteVersionAllowed");
				mcomCon = null;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting");
			}
		}
		return isAllowed;
	}
	@Override
	public AddVoucherGroupDropDownResponseVO addVoucherGroupDropDown(Connection con, UserVO userVO, String cardGroupSubServiceID)throws BTSLBaseException, Exception {
		final String methodName = "addVoucherGroupDropDown";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		AddVoucherGroupDropDownResponseVO response = new AddVoucherGroupDropDownResponseVO();

		// populate the drop downs
		response.setAmountTypeList(LookupsCache.loadLookupDropDown(PretupsI.AMOUNT_TYPE, true));
		response.setValidityTypeList(LookupsCache.loadLookupDropDown(PretupsI.VALIDITY_TYPE, true));
		ArrayList bonusBundlesList = new BonusBundleDAO().loadBonusBundles(con);
		bonusBundlesList = this.arrangeBonuses(bonusBundlesList, cardGroupSubServiceID, false);
		response.setBonusBundleList(bonusBundlesList);
		// Code added for change in card group screen. 16-June-09
		BonusAccountDetailsVO bonusAccountDetailsVO = null;
		final ArrayList bonusAccList = new ArrayList();
		final int listLength = bonusBundlesList.size();
		for (int i = 0; i < listLength; i++) {
			bonusAccountDetailsVO = new BonusAccountDetailsVO();
			bonusAccList.add(bonusAccountDetailsVO);
		}
		response.setTempAccList(bonusAccList);
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting forward=" );
		}

		return response;
	}

	private ArrayList arrangeBonuses(ArrayList p_bonusBundleList, String p_cardGroupSubServiceID, boolean p_isBonusAccDetailList)  throws BTSLBaseException{
		final String methodName = "arrangeBonuses";
		if (LOG.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
			msg.append("Entered CardGroupSubServiceID=");
			msg.append(p_cardGroupSubServiceID);
			msg.append(", p_bonusBundleList=");
			msg.append(p_bonusBundleList);
			msg.append(", p_isBonusAccDetailList=");
			msg.append(p_isBonusAccDetailList);

			String message=msg.toString();
			LOG.debug(methodName,message);
		}

		ArrayList arrangedList = null;
		final ArrayList tempList = p_bonusBundleList;
		int listSize = 0;
		String serviceSelectorKey = null;
		String receiverBonusID = null;
		BonusBundleDetailVO bundleDetailVO = null;
		BonusAccountDetailsVO accountDetailVO = null;

		ServiceSelectorMappingVO serviceSelectorMappingVO = null;
		String bundleId = null;
		final int index = p_cardGroupSubServiceID.indexOf(":");
		if (index != -1) {
			serviceSelectorKey = p_cardGroupSubServiceID.replace(':', '_');
			listSize = tempList.size();
			if (tempList != null && listSize > 0) {
				serviceSelectorMappingVO = (ServiceSelectorMappingVO) ServiceSelectorMappingCache.getServiceSelectorMap().get(serviceSelectorKey);
				receiverBonusID = serviceSelectorMappingVO.getReceiverBundleID();
				if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "receiverBonusID=" + receiverBonusID);
				}
				arrangedList = new ArrayList();
				for (int i = 0; i < listSize; i++) {
					if (p_isBonusAccDetailList) {
						accountDetailVO = (BonusAccountDetailsVO) tempList.get(i);
						bundleId = accountDetailVO.getBundleID();
						if (receiverBonusID.equals(bundleId)) {
							arrangedList.add(accountDetailVO);
							tempList.remove(accountDetailVO);
							break;
						}
					} else {
						bundleDetailVO = (BonusBundleDetailVO) tempList.get(i);
						bundleId = bundleDetailVO.getBundleID();
						if (receiverBonusID.equals(bundleId)) {
							arrangedList.add(bundleDetailVO);
							tempList.remove(bundleDetailVO);
							break;
						}
					}
				}
				arrangedList.addAll(tempList);
			} else {
				arrangedList = p_bonusBundleList;
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exited arrangedList=" + arrangedList);
		}

		return arrangedList;
	}

	private ArrayList<VoucherTypeVO> populateVoucherTypeList(Connection con)throws BTSLBaseException,Exception {
		final String methodName = "populateVoucherTypeList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		VomsProductDAO vomsProductDAO = new VomsProductDAO();

		ArrayList<VoucherTypeVO> voucherTypeList = vomsProductDAO.loadVoucherDetails(con);
		if (voucherTypeList == null || voucherTypeList.isEmpty()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "No voucher type found");
			}
			throw new BTSLBaseException(this, methodName, "cardgroup.cardgroupdetails.err.msg.novouchertypefound");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exited");
		}
		return voucherTypeList;
	}
	public  ArrayList populateSegmentList(Connection con)throws BTSLBaseException, Exception {
		final String methodName = "populateSegmentList";
		VomsProductDAO vomsProductDAO = new VomsProductDAO();
		String type = vomsProductDAO.getDetailFromVoucherType(con, "TYPE");
		ArrayList segmentList = null;
		if(!BTSLUtil.isNullString(type)) {
			segmentList = BTSLUtil.getSegmentList(type, LookupsCache.loadLookupDropDown(VOMSI.VOUCHER_SEGMENT, true));
			if (segmentList == null || segmentList.isEmpty()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "No voucher segment found");
				}
				throw new BTSLBaseException(CLASS_NAME, methodName, "cardgroup.cardgroupdetails.err.msg.novouchersegmentfound");
			}
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exited");
		}
		return segmentList;
	}
	@Override
	public DenaminationDetailsDropdownsResponseVO denominationDetailsList(Connection con, UserVO userVO ) throws BTSLBaseException, Exception{
		final String methodName = "denominationDetailsList";
		DenaminationDetailsDropdownsResponseVO response = new DenaminationDetailsDropdownsResponseVO();

		response.setVoucherTypeList(this.populateVoucherTypeList(con));
		VomsProductDAO vomsProductDAO = null;
		ArrayList<VomsCategoryVO> denominationList = null;
		vomsProductDAO = new VomsProductDAO();

		denominationList = vomsProductDAO.getMrpList(con, userVO.getNetworkID());
		if (denominationList == null || denominationList.isEmpty()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "No MRP found");
			}
			throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.NO_ACTIVE_MRP ,"");
		}


		response.setDenominationList(denominationList);
		response.setSegmentList(this.populateSegmentList(con));
		response.setDenominationProfileList(this.populateDenominationProfileList(con, userVO));

		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exited");
		}
		return  response;
	}
	public ArrayList<VomsProductVO> populateDenominationProfileList(Connection con,UserVO userVO,String voucherType,String voucherSegment, String voucherDenomination)throws BTSLBaseException, Exception {
		final String methodName = "populateDenominationProfileList";
		VomsProductDAO vomsProductDAO = null;

		ArrayList<VomsProductVO> voucherDenominationProfileList = null;
		String mrp = null;

		vomsProductDAO = new VomsProductDAO();

		if(!BTSLUtil.isNullString(voucherDenomination)) {
			mrp= String.valueOf(PretupsBL.getSystemAmount(Double.parseDouble(voucherDenomination)));
			voucherDenominationProfileList = vomsProductDAO.loadMrpProductDetailsList(con, voucherType, "'" + VOMSI.VOMS_STATUS_ACTIVE + "'", false, "",userVO.getNetworkID(), voucherSegment,mrp);
			if (voucherDenominationProfileList == null || voucherDenominationProfileList.isEmpty()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "No product found");
				}
				throw new BTSLBaseException(CLASS_NAME, methodName, "cardgroup.cardgroupdetails.err.msg.noproductfound");
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exited");
		}
		return voucherDenominationProfileList;
	}


	public ArrayList<VomsProductVO> populateDenominationProfileList(Connection con,UserVO userVO)throws BTSLBaseException, Exception {
		final String methodName = "populateDenominationProfileList";
		VomsProductDAO vomsProductDAO = null;

		ArrayList<VomsProductVO> voucherDenominationProfileList = null;
		String mrp = null;

		vomsProductDAO = new VomsProductDAO();


		voucherDenominationProfileList = vomsProductDAO.loadMrpProductDetailsList(con,  "'" + VOMSI.VOMS_STATUS_ACTIVE + "'", false, userVO.getNetworkID());
		if (voucherDenominationProfileList == null || voucherDenominationProfileList.isEmpty()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "No product found");
			}
			throw new BTSLBaseException(CLASS_NAME, methodName, "cardgroup.cardgroupdetails.err.msg.noproductfound");
		}


		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exited");
		}
		return voucherDenominationProfileList;
	}
	@Override
	public SaveVoucherGroupResponseVO saveVoucherGroup(Connection con,SaveVoucherGroupResponseVO response,UserVO userVO, VoucherGroupDetailsRequestVO request) throws BTSLBaseException, Exception {

		final String methodName = "saveVoucherGroup";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		ArrayList <CardGroupDetailsVO> updatedCardGroupList = new ArrayList();
		final ArrayList<VoucherGroupDetails> cardGroupList = request.getVoucherGroupDetailsList();
		final Iterator itr = cardGroupList.iterator();
		int count = 0;
		String slabCount = null;
		while (itr.hasNext()) {
			count++;
			slabCount = String.valueOf(count);
			VoucherGroupDetails voucherGroupDetails=  (VoucherGroupDetails) itr.next();
			final CardGroupDetailsVO cardGroupDetailVO = new CardGroupDetailsVO();
			convertRequestToCardGroup(voucherGroupDetails,cardGroupDetailVO);
			updatedCardGroupList.add(cardGroupDetailVO);
			if (!BTSLUtil.isNullString(request.getSubService())) {
				final String serviceID = request.getSubService();
				final int index = serviceID.indexOf(":");
				if (index != -1) {
					cardGroupDetailVO.setServiceTypeSelector(request.getSubService().replace(':', '_'));
				}
			}
			// check the start range value(Talk time is valid or
			// not)
			final P2PTransferVO p2pTransferVO ;
			p2pTransferVO = this.calculateTalkTime( cardGroupDetailVO, PretupsBL.getSystemAmount(request.getVoucherGroupDetailsList().get(count-1).getDenomination()), request
					.getSubService().split(":")[1], false);
			// by
			// shishu


			if (p2pTransferVO.getReceiverTransferValue() < 0) {
				// invalid talk time
				String errorMsg = null;


				final String[] arr = {slabCount, PretupsBL.getDisplayAmount(p2pTransferVO.getReceiverTransferValue())};
				Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
				errorMsg= RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_RECEIVER_TRANSFER_VALUE_SLABWISE ,arr);
				response.setMessage(errorMsg);
				response.setMessageCode(PretupsErrorCodesI.INVALID_RECEIVER_TRANSFER_VALUE_SLABWISE);
				throw new BTSLBaseException(CLASS_NAME, methodName,
						PretupsErrorCodesI.INVALID_RECEIVER_TRANSFER_VALUE_SLABWISE,  arr);
			}

		}
		final ListSorterUtil sort = new ListSorterUtil();
		final ArrayList sortedList =  (ArrayList) sort.doSort("startRange", null, updatedCardGroupList);
		String startRangeLabel = PretupsI.START_RANGE;;
		if (sortedList != null && !sortedList.isEmpty()) {
			HashSet<String> cardGroupName = new HashSet<>();{
				for(CardGroupDetailsVO card : updatedCardGroupList){
					if(cardGroupName.contains(card.getCardName())){
						throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.DUPLCATE_CARD_GROUP);
					}
					cardGroupName.add(card.getCardName());
				}
			}
			CardGroupDetailsVO preCardVO = (CardGroupDetailsVO) sortedList.get(0);
			preCardVO.setRowIndex(1);
			String endRangeLabel = null;
			CardGroupDetailsVO nextCardVO = null;
			CardGroupDetailsVO currCrdVO = null;
			currCrdVO = (CardGroupDetailsVO) sortedList.get(0);
			HashMap<String, String> cardGroups = new HashMap<String, String>();
			StringBuilder key = new StringBuilder(currCrdVO.getVoucherType()).append("_").append(currCrdVO.getVoucherSegment()).append("_").append(currCrdVO.getVoucherDenomination()).append("_").append(currCrdVO.getVoucherProductId());
			String newkey =key.toString();
			cardGroups.put(newkey, newkey);
			for (int i = 1, j = sortedList.size(); i < j; i++) {
				nextCardVO = (CardGroupDetailsVO) sortedList.get(i);
				key = new StringBuilder(nextCardVO.getVoucherType()).append("_").append(nextCardVO.getVoucherSegment()).append("_").append(nextCardVO.getVoucherDenomination()).append("_").append(nextCardVO.getVoucherProductId());
				newkey =key.toString();
				if(cardGroups.containsKey(newkey))
				{
					String arr1[] ={ nextCardVO.getProductName()};
					throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_CARDGROUP_INVALID_SLAB, "");
				} else {
					cardGroups.put(newkey, newkey);
				}
				key=null;
				currCrdVO = nextCardVO;
			}



		}

		final Date currentDate = new Date();
		final CardGroupDAO cardDAO = new CardGroupDAO();
		final CardGroupSetVersionDAO cardGroupSetVersionDAO = new CardGroupSetVersionDAO();
		final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
		Map<String, Object> data = new HashMap<>();
		CardGroupSetVO cardGroupSetVO = new CardGroupSetVO();
		cardGroupSetVO.setCardGroupSetName(request.getCardGroupSetName());
		cardGroupSetVO.setNetworkCode(userVO.getNetworkID());
		cardGroupSetVO.setCreatedBy(userVO.getUserID());
		cardGroupSetVO.setModifiedBy(userVO.getUserID());
		cardGroupSetVO.setModuleCode(PretupsI.P2P_MODULE);
		cardGroupSetVO.setStatus(request.getStatus());
		cardGroupSetVO.setSubServiceTypeDescription(request.getSubServiceDesc());
		cardGroupSetVO.setServiceTypeDesc(request.getServiceTypeDesc());
		cardGroupSetVO.setSetTypeName(request.getCardGroupSetType());
		cardGroupSetVO.setApplicableFromHour(request.getApplicableFromtime());
		cardGroupSetVO.setApplicableFromDate(request.getApplicableFromDate());
		cardGroupSetVO.setDefaultCardGroup(request.getDefaultCardGroup());

		data.put("cardGroupDetails",cardGroupSetVO);
		data.put("cardGroupList",updatedCardGroupList);
		Map<String, Object> requestObject = new HashMap<>();
		String password = BTSLUtil.decryptText(userVO.getPassword());
		userVO.setPassword(password);
		PretupsRestUtil.setLoginDetailsInRequest(requestObject, userVO);
		requestObject.put("data", data);
		PretupsRestClient pretupsRestClient=new PretupsRestClient();
		CardGroupDAO cardGroupDAO = new CardGroupDAO();
		if (request.getDefaultCardGroup().equals(PretupsI.YES) && !((PretupsI.YES).equals(cardGroupSetVO.getStatus()))) {
			throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.SELECTED_CARD_GROUP_SET_IS_SUSPENDED, "");
		}
		final String defaultCardGroupID = (cardGroupDAO.loadDefaultCardGroup(con, request.getServiceType(), request.getSubService().split(":")[1], PretupsI.YES,userVO.getNetworkID())).get(0);
		String responseString = pretupsRestClient.postJSONRequest(requestObject, PretupsRestI.ADD_CARDGROUP);
		PretupsResponse<JsonNode> pretupsResponse = (PretupsResponse<JsonNode>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<JsonNode>>() {
				});
		if(pretupsResponse.getStatus()) {
			con.commit();
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			String arr[]= { request.getCardGroupSetName()};
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESSFULY_ADD_VOUCHER_CARD_GROUP_LOG, arr);
			final AdminOperationVO adminOperationVO = new AdminOperationVO();
			adminOperationVO.setSource(PretupsI.LOGGER_CARD_GROUP_SOURCE);
			adminOperationVO.setDate(currentDate);
			adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
			adminOperationVO.setInfo(resmsg);
			adminOperationVO.setLoginID(userVO.getLoginID());
			adminOperationVO.setUserID(userVO.getUserID());
			adminOperationVO.setCategoryCode(userVO.getCategoryCode());
			adminOperationVO.setNetworkCode(userVO.getNetworkID());
			adminOperationVO.setMsisdn(userVO.getMsisdn());
			AdminOperationLog.log(adminOperationVO);

			if(request.getDefaultCardGroup().equals(PretupsI.YES)&& defaultCardGroupID!=null) {

				boolean status =false;
				try {
					status = cardGroupSetDAO.updateDefaultAsNo(con, defaultCardGroupID, userVO.getUserID(), currentDate);
				}catch (Exception e) {
					con.rollback();
					LOG.errorTrace(methodName, e);
					throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.UNABLE_TO_MAKE_DEFAULT_AS_NO, "");
				}
				if(status) {
					con.commit();
				}
			}
			String cardGroupSetID = cardGroupSetDAO.loadCardGroupSetID(con, request.getServiceType(), request.getSubService().split(":")[1], request.getCardGroupSetType().substring(0,1), userVO.getNetworkID(), request.getCardGroupSetName());
			response.setCardGroupSetId(cardGroupSetID);
			return response;
		}
		else {
			if(pretupsResponse.getMessage()!= null)
				throw new BTSLBaseException(CLASS_NAME, methodName, pretupsResponse.getMessageCode());
			if(pretupsResponse.getMessageCode().equals("cardgroup.error.cardgroupnamealreadyexist")) {
				throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.CARD_GROUP_NAME_ALREADY_EXIST);
			}
			if(pretupsResponse.getMessageCode().equals("cardgroup.cardgroupdetails.voucher.error.invalidslab")) {
				throw new BTSLBaseException(CLASS_NAME, methodName,PretupsErrorCodesI.VOUCHER_CARDGROUP_INVALID_SLAB,"");
			}


			if(pretupsResponse.getMessage()==null)
				throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_GROUP_DETAILS_ADD_FAILED, "");
		}
		return response;


	}


	public SaveVoucherGroupResponseVO modifyVoucherGroup(Connection con, UserVO userVO,SaveVoucherGroupResponseVO response, ModifyVoucherCardGroupDetailsRequestVO requestVO ) throws BTSLBaseException, Exception {
		final String methodName = "updateVoucherCardGroup";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		ArrayList <CardGroupDetailsVO> updatedCardGroupList = new ArrayList();
		final ArrayList<VoucherGroupDetails> cardGroupList = requestVO.getVoucherGroupDetailsList();
		final Iterator itr = cardGroupList.iterator();
		int count = 0;
		String slabCount = null;
		while (itr.hasNext()) {
			count++;
			slabCount = String.valueOf(count);
			VoucherGroupDetails voucherGroupDetails=  (VoucherGroupDetails) itr.next();
			final CardGroupDetailsVO cardGroupDetailVO = new CardGroupDetailsVO();

			this.convertRequestToCardGroup(voucherGroupDetails, cardGroupDetailVO);
			cardGroupDetailVO.setCardGroupSetID(requestVO.getCardGroupSetId());
			cardGroupDetailVO.setCardGroupID(requestVO.getCardGroupId());
			cardGroupDetailVO.setVersion(requestVO.getVersion());
			updatedCardGroupList.add(cardGroupDetailVO);
			if (!BTSLUtil.isNullString(requestVO.getSubService())) {
				final String serviceID = requestVO.getSubService();
				final int index = serviceID.indexOf(":");
				if (index != -1) {
					cardGroupDetailVO.setServiceTypeSelector(requestVO.getSubService().replace(':', '_'));
				}
			}
			// check the start range value(Talk time is valid or
			// not)
			final P2PTransferVO p2pTransferVO ;
			p2pTransferVO = this.calculateTalkTime( cardGroupDetailVO, PretupsBL.getSystemAmount(requestVO.getVoucherGroupDetailsList().get(count-1).getDenomination()), requestVO
					.getSubService().split(":")[1], false);
			if (p2pTransferVO.getReceiverTransferValue() < 0) {
				// invalid talk time
				String errorMsg = null;


				final String[] arr = {slabCount, PretupsBL.getDisplayAmount(p2pTransferVO.getReceiverTransferValue())};
				Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
				errorMsg= RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_RECEIVER_TRANSFER_VALUE_SLABWISE ,arr);
				response.setMessage(errorMsg);
				response.setMessageCode(PretupsErrorCodesI.INVALID_RECEIVER_TRANSFER_VALUE_SLABWISE);
				throw new BTSLBaseException(CLASS_NAME, methodName,
						PretupsErrorCodesI.INVALID_RECEIVER_TRANSFER_VALUE_SLABWISE,  arr);
			}

		}
		final ListSorterUtil sort = new ListSorterUtil();
		final ArrayList sortedList =  (ArrayList) sort.doSort("startRange", null, updatedCardGroupList);
		String startRangeLabel = PretupsI.START_RANGE;;
		if (sortedList != null && !sortedList.isEmpty()) {
			CardGroupDetailsVO preCardVO = (CardGroupDetailsVO) sortedList.get(0);
			preCardVO.setRowIndex(1);
			String endRangeLabel = null;
			CardGroupDetailsVO nextCardVO = null;
			CardGroupDetailsVO currCrdVO = null;
			currCrdVO = (CardGroupDetailsVO) sortedList.get(0);
			HashMap<String, String> cardGroups = new HashMap<String, String>();
			StringBuilder key = new StringBuilder(currCrdVO.getVoucherType()).append("_").append(currCrdVO.getVoucherSegment()).append("_").append(currCrdVO.getVoucherDenomination()).append("_").append(currCrdVO.getVoucherProductId());
			String newkey =key.toString();
			cardGroups.put(newkey, newkey);
			for (int i = 1, j = sortedList.size(); i < j; i++) {
				HashSet<String> cardGroupName = new HashSet<>();{
					for(CardGroupDetailsVO card : updatedCardGroupList){
						if(cardGroupName.contains(card.getCardName())){
							throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.DUPLCATE_CARD_GROUP);
						}
						cardGroupName.add(card.getCardName());
					}
				}

				nextCardVO = (CardGroupDetailsVO) sortedList.get(i);
				key = new StringBuilder(nextCardVO.getVoucherType()).append("_").append(nextCardVO.getVoucherSegment()).append("_").append(nextCardVO.getVoucherDenomination()).append("_").append(nextCardVO.getVoucherProductId());
				newkey =key.toString();
				if(cardGroups.containsKey(newkey))
				{
					String arr1[] ={ nextCardVO.getProductName()};
					throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_CARDGROUP_INVALID_SLAB,  "");
				} else {
					cardGroups.put(newkey, newkey);
				}
				key=null;
				currCrdVO = nextCardVO;
			}



		}


		// added for suspend/resume card group slab

		final String format = Constants.getProperty("CARDGROUP_DATE_FORMAT");
		String fromHour = null;
		if (BTSLUtil.isNullString(requestVO.getApplicableFromtime())) {
			fromHour = "00:00";
		} else {
			fromHour = requestVO.getApplicableFromtime();
		}
		final Date newDate = BTSLUtil.getDateFromDateString(requestVO.getApplicableFromDate() + " " + fromHour, format);
		final Date oldDate = BTSLUtil.getDateFromDateString(requestVO.getOldApplicableFromDate() + " " + requestVO.getOldApplicableFromHour(), format);
		String versionStr =null;
		if (oldDate.getTime() != newDate.getTime())// we need to
		// insert the new
		// version
		{
			int version = 0;

			// get the selected card group set from the
			// CardGroupSetNameList

			version = Integer.parseInt(requestVO.getVersion());

			versionStr = String.valueOf(version ) ;

		}else {
			versionStr = requestVO.getVersion();
		}






		Map<String, Object> data = new HashMap<>();
		CardGroupSetVO cardGroupSetVO = new CardGroupSetVO();
		cardGroupSetVO.setCardGroupSetName(requestVO.getCardGroupSetName());
		cardGroupSetVO.setCardGroupSetID(requestVO.getCardGroupSetId());
		cardGroupSetVO.setNetworkCode(userVO.getNetworkID());
		cardGroupSetVO.setModifiedBy(userVO.getUserID());
		cardGroupSetVO.setModuleCode(PretupsI.P2P_MODULE);
		cardGroupSetVO.setSubServiceTypeDescription(requestVO.getSubServiceDesc());
		cardGroupSetVO.setServiceTypeDesc(requestVO.getServiceTypeDesc());
		cardGroupSetVO.setApplicableFromHour(requestVO.getApplicableFromtime());
		cardGroupSetVO.setApplicableFromDate(requestVO.getApplicableFromDate());
		cardGroupSetVO.setVersion(versionStr);
		cardGroupSetVO.setStatus(requestVO.getStatus());
		cardGroupSetVO.setCardGroupID(requestVO.getCardGroupId());
		cardGroupSetVO.setDefaultCardGroup(requestVO.getDefaultCardGroup());

		data.put("cardGroupDetails",cardGroupSetVO);
		data.put("cardGroupList",updatedCardGroupList);
		Map<String, Object> requestObject = new HashMap<>();
		String password = BTSLUtil.decryptText(userVO.getPassword());
		userVO.setPassword(password);
		PretupsRestUtil.setLoginDetailsInRequest(requestObject, userVO);
		requestObject.put("data", data);
		Date currentDate = new Date();
		if(requestVO.getDefaultCardGroup().equals(PretupsI.YES)) {
			CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
			CardGroupSetVersionDAO cardGroupSetVersionDAO = new CardGroupSetVersionDAO();
			final ArrayList<CardGroupSetVersionVO> cardGroupSetVersionList = cardGroupSetDAO.loadCardGroupSetVersion(con,cardGroupSetVO.getNetworkCode(), currentDate, PretupsI.P2P_MODULE);

			// set the Card Group Set Version Info
			CardGroupSetVersionVO setVersionVO = null;
			boolean versionStatus = false;
			for (int i = 0, j = cardGroupSetVersionList.size(); i < j; i++) {
				setVersionVO = (CardGroupSetVersionVO) cardGroupSetVersionList.get(i);
				// get the selected version info from the versionList
				if (cardGroupSetVO.getCardGroupSetID().equals(setVersionVO.getCardGroupSetID()) && cardGroupSetVO.getVersion().equals(setVersionVO.getVersion())) {
					versionStatus=true;
					break;
				}
			}
			if (!versionStatus) {
				throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_CARD_GROUP_SET_APPLICABLE_DATE_NOT_FEATURE_DATE_OR_NOT_LATEST_VERSION, "");
			}
			if (!cardGroupSetVersionDAO.isApplicableNow(con, currentDate, requestVO.getCardGroupSetId())) {
				throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_CARD_GROUP_SHOULD_BE_CURRENTLY_APPLICABLE,"");
			}

		}


		PretupsRestClient pretupsRestClient=new PretupsRestClient();
		String responseString = pretupsRestClient.postJSONRequest(requestObject, PretupsRestI.MODIFY_CARDGROUP);

		PretupsResponse<JsonNode> pretupsResponse = (PretupsResponse<JsonNode>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<JsonNode>>() {
				});
		if(pretupsResponse.getStatusCode()==200){
			// Added to update the cache after changes in card group
			// table
			CardGroupCache.loadCardGroupMapAtStartup();
			con.commit();
			CardGroupDAO cardGroupDAO= new CardGroupDAO();

			final String defaultCardGroupID = (cardGroupDAO.loadDefaultCardGroup(con, requestVO.getServiceType(), requestVO.getSubService().split(":")[1], PretupsI.YES, userVO.getNetworkID())).get(0);

			if(requestVO.getDefaultCardGroup().equals(PretupsI.YES) && !(defaultCardGroupID.equals(requestVO.getCardGroupSetId())) ) {
				DefaultVoucherCardGroupRequestVO defalutRequestVO= new DefaultVoucherCardGroupRequestVO();
				defalutRequestVO.setCardGroupSubServiceID(requestVO.getSubService().split(":")[1]);
				defalutRequestVO.setSelectCardGroupSetId(requestVO.getCardGroupSetId());
				defalutRequestVO.setServiceTypeId(requestVO.getServiceType());
				userVO.setPassword(BTSLUtil.encryptText(userVO.getPassword()));


				final CardGroupStatusVO statusVO = new CardGroupStatusVO();
				final DefaultCardGroupVO requestDefaultVO = new DefaultCardGroupVO();

				statusVO.setModuleCode(PretupsI.CARD_GROUP_P2P);
				statusVO.setNetworkCode(userVO.getNetworkID());
				statusVO.setIdentifierType(userVO.getLoginID());
				statusVO.setIdentifierValue(password);

				requestDefaultVO.setModuleCode(PretupsI.CARD_GROUP_P2P);
				requestDefaultVO.setNetworkCode(userVO.getNetworkID());
				requestDefaultVO.setIdentifierType(userVO.getLoginID());

				requestDefaultVO.setIdentifierValue(password);
				requestDefaultVO.setUserId(userVO.getUserID());
				requestDefaultVO.setServiceTypeId(requestVO.getServiceType());
				requestDefaultVO.setSubServiceTypeId(requestVO.getSubService().split(":")[1]);
				requestDefaultVO.setCardGroupSetId(requestVO.getCardGroupSetId());
				ArrayList<CardGroupSetVO> allCcardGroupList = cardGroupDAO.loadCardGroupSet(con, userVO.getNetworkID(), PretupsI.P2P_MODULE);

				CardGroupSetVO cardGroupSetVO2 = null;
				if (cardGroupList != null && !allCcardGroupList.isEmpty()) {
					for (int i = 0, l = allCcardGroupList.size(); i < l; i++) {
						cardGroupSetVO2 = (CardGroupSetVO) allCcardGroupList.get(i);
						if ((cardGroupSetVO.getCardGroupSetID()).equals(requestVO.getCardGroupSetId())) {
							break;
						}
					}
				}
				if (cardGroupSetVO != null && !((PretupsI.YES).equals(cardGroupSetVO.getStatus()))) {
					throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.ERROR_CARD_GROUP_SET_SUSPENDED, "");
				}
				PretupsRestClient pretupsRestClient2=new PretupsRestClient();
				String responseString2 = pretupsRestClient2.postJSONRequest(requestDefaultVO, PretupsRestI.DEFAULT_CARDGROUP);

				PretupsResponse<JsonNode> pretupsResponse2 = (PretupsResponse<JsonNode>) PretupsRestUtil
						.convertJSONToObject(responseString2, new TypeReference<PretupsResponse<JsonNode>>() {
						});

				if (pretupsResponse2.getStatus()) {
					con.commit();
					Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
					final AdminOperationVO adminOperationVO = new AdminOperationVO();
					adminOperationVO.setSource(PretupsI.LOGGER_OPERATION_MAKE_DEFAULT_CARDGROUP);
					adminOperationVO.setDate(currentDate);
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
					String str[]= {cardGroupSetVO.getCardGroupSetName()};

					String resmsg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.VOUCHER_CARD_GROUP_CHANGE_DEFAULT_CARD_GROUP_SET, str);
					adminOperationVO.setInfo(resmsg );
					adminOperationVO.setLoginID(userVO.getLoginID());
					adminOperationVO.setUserID(userVO.getUserID());
					adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					adminOperationVO.setNetworkCode(userVO.getNetworkID());
					adminOperationVO.setMsisdn(userVO.getMsisdn());
					AdminOperationLog.log(adminOperationVO);

				} else {
					con.rollback();
					if(pretupsResponse2.getMessageCode().equals(PretupsI.NO_CURRENT_VERSION)) {
						throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_CARD_GROUP_SHOULD_BE_CURRENTLY_APPLICABLE);
					}
					else
						throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.SELECTED_CARD_GROUP_SET_IS_NO_CURRENT_VERSION, "");
				}
				//this.changeDefaultVoucherCardGroup(con, userVO, defalutRequestVO);
			}

			String arr[]= { requestVO.getCardGroupSetName()};
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			final AdminOperationVO adminOperationVO = new AdminOperationVO();
			adminOperationVO.setSource(PretupsI.LOGGER_OPERATION_MAKE_VOUCHER_CARDGROUP);
			adminOperationVO.setDate(currentDate);
			adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.VOUCHER_CARD_GROUP_MODIFY_LOG, arr);
			adminOperationVO.setInfo(resmsg );
			adminOperationVO.setLoginID(userVO.getLoginID());
			adminOperationVO.setUserID(userVO.getUserID());
			adminOperationVO.setCategoryCode(userVO.getCategoryCode());
			adminOperationVO.setNetworkCode(userVO.getNetworkID());
			adminOperationVO.setMsisdn(userVO.getMsisdn());
			AdminOperationLog.log(adminOperationVO);
			response.setStatus(HttpStatus.SC_OK);

			if (oldDate.getTime() != newDate.getTime())// we need to
			// insert the new
			// version
			{
				List<String> newVersionList = new CardGroupSetDAO().loadCardGroupSetVersionNumbers(con, requestVO.getCardGroupSetId(), newDate);
				List<Integer> intVersionList = newVersionList.stream().map(Integer::parseInt).collect(Collectors.toList());
				Integer updatedVersion= intVersionList.stream().sorted(Comparator.comparing(Functions.identity())).max(Comparator.comparing(Functions.identity())).get();
				//String updatedVersion = (String)newVersion.get(newVersion.size()-1);
				requestVO.setVersion(String.valueOf(updatedVersion));
				String arrWithVersion[]= {requestVO.getCardGroupSetName(), String.valueOf(updatedVersion)};
				String resmsgWithVersion = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.MODIFIED_CARD_GROUP_VERSION_NUMBER, arrWithVersion);
				response.setIsVersionCreated(resmsgWithVersion);

			}
			return response;

		} else {
			con.rollback();
			if(pretupsResponse.getMessage()!= null)
				throw new BTSLBaseException(CLASS_NAME, methodName, pretupsResponse.getMessageCode());
			if(pretupsResponse.getMessageCode().equals(PretupsI.CARD_GROUP_NAME_ALREADY_EXIST)) {
				throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.CARD_GROUP_NAME_ALREADY_EXIST);
			}
			if(pretupsResponse.getMessageCode().equals(PretupsI.INVALID_SLAB)) {
				throw new BTSLBaseException(CLASS_NAME, methodName,PretupsErrorCodesI.VOUCHER_CARDGROUP_INVALID_SLAB,"");
			}
			if(pretupsResponse.getMessageCode().equals(PretupsI.CARD_GROUP_ALREADY_EXIST)) {
				Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
				String arrApplicbleDate[]= {requestVO.getApplicableFromDate()};
				String resmsgApplicbleDate = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.VOUCHER_CARD_GROUP_SET_ALREADY_EXISTS_WITH_THE_SAME_APPLICABLE_DATE, arrApplicbleDate);
				response.setMessage(resmsgApplicbleDate);
				response.setMessageCode(PretupsErrorCodesI.VOUCHER_CARD_GROUP_SET_ALREADY_EXISTS_WITH_THE_SAME_APPLICABLE_DATE);
				throw new BTSLBaseException(CLASS_NAME, methodName,PretupsErrorCodesI.VOUCHER_CARD_GROUP_SET_ALREADY_EXISTS_WITH_THE_SAME_APPLICABLE_DATE,arrApplicbleDate);
			}
			if(pretupsResponse.getMessageCode().equals(PretupsI.VERSION_NOT_EXIST)){
				throw new BTSLBaseException(CLASS_NAME, methodName,PretupsErrorCodesI.VOUCHER_CARD_GROUP_SET_APPLICABLE_DATE_NOT_FEATURE_DATE_OR_NOT_LATEST_VERSION,"");
			}
			if(pretupsResponse.getMessage()==null)
				throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_GROUP_MODIFY_FAILED, "");

		}

		return response;


	}
	private void convertRequestToCardGroup(VoucherGroupDetails voucherGroupDetails, CardGroupDetailsVO cardGroupDetailVO) throws BTSLBaseException, Exception {
		cardGroupDetailVO.setVoucherType(voucherGroupDetails.getVoucherType());
		cardGroupDetailVO.setVoucherSegment(voucherGroupDetails.getVoucherSegment());
		cardGroupDetailVO.setVoucherProductId(voucherGroupDetails.getDenominationProfile());
		cardGroupDetailVO.setVoucherTypeDesc(voucherGroupDetails.getVoucherTypeDesc());
		cardGroupDetailVO.setVoucherSegmentDesc(voucherGroupDetails.getVoucherSegmentDesc());
		if(voucherGroupDetails.getCardGroupName()!=null)
			cardGroupDetailVO.setCardName(voucherGroupDetails.getCardGroupName());
		cardGroupDetailVO.setProductName(voucherGroupDetails.getProfileDesc());
		cardGroupDetailVO.setCardGroupType(PretupsI.CARD_GROUP_VMS);
		cardGroupDetailVO.setCardGroupCode(voucherGroupDetails.getCardGroupCode());
		cardGroupDetailVO.setValidityPeriodType(voucherGroupDetails.getValidityType());
		cardGroupDetailVO.setValidityPeriod(voucherGroupDetails.getValidityDays());
		cardGroupDetailVO.setGracePeriod(voucherGroupDetails.getGracePeriodDays());
		cardGroupDetailVO.setReceiverTax1Name(voucherGroupDetails.getTax1Name());
		cardGroupDetailVO.setReceiverTax1Type(voucherGroupDetails.getTax1Type());
		if(voucherGroupDetails.getTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
			cardGroupDetailVO.setReceiverTax1Rate(PretupsBL.getSystemAmount(voucherGroupDetails.getTax1Rate()));
		else
			cardGroupDetailVO.setReceiverTax1Rate(voucherGroupDetails.getTax1Rate());
		cardGroupDetailVO.setReceiverTax2Name(voucherGroupDetails.getTax2Name());
		cardGroupDetailVO.setReceiverTax2Type(voucherGroupDetails.getTax2Type());
		if(voucherGroupDetails.getTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
			cardGroupDetailVO.setReceiverTax2Rate(PretupsBL.getSystemAmount(voucherGroupDetails.getTax2Rate()));
		else
			cardGroupDetailVO.setReceiverTax2Rate(voucherGroupDetails.getTax2Rate());
		cardGroupDetailVO.setReceiverAccessFeeType(voucherGroupDetails.getProcessingFeeType());
		if(voucherGroupDetails.getProcessingFeeType().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
			cardGroupDetailVO.setReceiverAccessFeeRate(PretupsBL.getSystemAmount(voucherGroupDetails.getProcessingfee()));
		else
			cardGroupDetailVO.setReceiverAccessFeeRate(voucherGroupDetails.getProcessingfee());
		cardGroupDetailVO.setMinReceiverAccessFee(PretupsBL.getSystemAmount(voucherGroupDetails.getMinAmount()));
		cardGroupDetailVO.setMaxReceiverAccessFee(PretupsBL.getSystemAmount(voucherGroupDetails.getMaxAmount()));
		cardGroupDetailVO.setStartRange(PretupsBL.getSystemAmount(voucherGroupDetails.getDenomination()));
		cardGroupDetailVO.setEndRange(PretupsBL.getSystemAmount(voucherGroupDetails.getDenomination()));
		cardGroupDetailVO.setVoucherDenomination(voucherGroupDetails.getDenomination());
		cardGroupDetailVO.setStatus(voucherGroupDetails.getStatus());
		cardGroupDetailVO.setReceiverConvFactor("1");
		ArrayList<BonusAccountDetailsVO>bounsAccList= voucherGroupDetails.getBonusBandleList();
		cardGroupDetailVO.setBonusAccList(bounsAccList);

	}
	private P2PTransferVO calculateTalkTime( CardGroupDetailsVO cardGroupDetailVO, long amount, String subServiceID, boolean checkMultipleOff) throws BTSLBaseException  {
		final String methodName = "calculateTalkTime";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered amount:" + amount);
		}

		final P2PTransferVO p2pTransferVO = new P2PTransferVO();
		p2pTransferVO.setTransferValue(amount);
		p2pTransferVO.setRequestedAmount(amount);
		final TransferItemVO itemVO1 = TransferItemVO.getInstance();
		final TransferItemVO itemVO2 = TransferItemVO.getInstance();
		final Date currentDate = new Date();
		itemVO2.setPreviousExpiry(currentDate);
		itemVO2.setTransferDateTime(currentDate);
		itemVO2.setTransferDate(currentDate);
		final ArrayList itemList = new ArrayList();
		itemList.add(itemVO1);
		itemList.add(itemVO2);
		p2pTransferVO.setTransferItemList(itemList);
		//CardGroupBL.calculateP2PSenderValues(p2pTransferVO, cardGroupDetailVO, subServiceID, checkMultipleOff);
		CardGroupBL.calculateP2PReceiverValues(p2pTransferVO, cardGroupDetailVO, subServiceID, checkMultipleOff);
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting");
		}
		return p2pTransferVO;
	}


	@Override
	public DefaultVoucherCardGroupResponseVO changeDefaultVoucherCardGroup(Connection con, UserVO userVO,
																		   DefaultVoucherCardGroupRequestVO requestVO) throws BTSLBaseException, Exception {
		final String methodName = "changeDefaultVoucherCardGroup";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}
		DefaultVoucherCardGroupResponseVO response = new DefaultVoucherCardGroupResponseVO();
		StringBuilder loggerValue= new StringBuilder();
		final String serviceType = requestVO.getServiceTypeId();
		final String subService = requestVO.getCardGroupSubServiceID();
		final String newCardGroupSetId = requestVO.getSelectCardGroupSetId();
		final String ntwkcode=userVO.getNetworkID();
		final CardGroupDAO cardGroupDAO = new CardGroupDAO();
		final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
		final CardGroupSetVersionDAO cardGroupSetVersionDAO= new CardGroupSetVersionDAO();
		// load the card group set names
		ArrayList<CardGroupSetVO> cardGroupList = cardGroupDAO.loadCardGroupSet(con, userVO.getNetworkID(), PretupsI.P2P_MODULE);


		final String defaultCardGroupID = (cardGroupDAO.loadDefaultCardGroup(con, serviceType, subService, PretupsI.YES,ntwkcode)).get(0);
		ArrayList serviceTypeList = cardGroupDAO.loadServiceTypeList(con, userVO.getNetworkID(), PretupsI.P2P_MODULE);


		final Date currentDate = new Date();

		final CardGroupStatusVO statusVO = new CardGroupStatusVO();
		final DefaultCardGroupVO requestDefaultVO = new DefaultCardGroupVO();

		statusVO.setModuleCode(PretupsI.CARD_GROUP_P2P);
		statusVO.setNetworkCode(userVO.getNetworkID());
		statusVO.setIdentifierType(userVO.getLoginID());
		String password = BTSLUtil.decryptText(userVO.getPassword());
		statusVO.setIdentifierValue(password);

		requestDefaultVO.setModuleCode(PretupsI.CARD_GROUP_P2P);
		requestDefaultVO.setNetworkCode(userVO.getNetworkID());
		requestDefaultVO.setIdentifierType(userVO.getLoginID());

		requestDefaultVO.setIdentifierValue(password);
		requestDefaultVO.setUserId(userVO.getUserID());
		requestDefaultVO.setServiceTypeId(serviceType);
		requestDefaultVO.setSubServiceTypeId(subService);
		requestDefaultVO.setCardGroupSetId(newCardGroupSetId);
		CardGroupSetVO cardGroupSetVO = null;
		if (cardGroupList != null && !cardGroupList.isEmpty()) {
			for (int i = 0, l = cardGroupList.size(); i < l; i++) {
				cardGroupSetVO = (CardGroupSetVO) cardGroupList.get(i);
				if ((cardGroupSetVO.getCardGroupSetID()).equals(newCardGroupSetId)) {
					break;
				}
			}
		}
		if (cardGroupSetVO != null && !((PretupsI.YES).equals(cardGroupSetVO.getStatus()))) {
			throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.ERROR_CARD_GROUP_SET_SUSPENDED, "");
		}
		if (!cardGroupSetVersionDAO.isApplicableNow(con, currentDate, newCardGroupSetId)) {
			throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_CARD_GROUP_SHOULD_BE_CURRENTLY_APPLICABLE,"");
		}
		PretupsRestClient pretupsRestClient=new PretupsRestClient();
		String responseString = pretupsRestClient.postJSONRequest(requestDefaultVO, PretupsRestI.DEFAULT_CARDGROUP);

		PretupsResponse<JsonNode> pretupsResponse = (PretupsResponse<JsonNode>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<JsonNode>>() {
				});

		if (pretupsResponse.getStatus()) {
			con.commit();
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			final AdminOperationVO adminOperationVO = new AdminOperationVO();
			adminOperationVO.setSource(PretupsI.LOGGER_OPERATION_MAKE_DEFAULT_CARDGROUP);
			adminOperationVO.setDate(currentDate);
			adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
			String str[]= {cardGroupSetVO.getCardGroupSetName()};

			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESSFULLY_CHANGE_DEFAULT_CARD_GROUP, str);
			adminOperationVO.setInfo(resmsg );
			adminOperationVO.setLoginID(userVO.getLoginID());
			adminOperationVO.setUserID(userVO.getUserID());
			adminOperationVO.setCategoryCode(userVO.getCategoryCode());
			adminOperationVO.setNetworkCode(userVO.getNetworkID());
			adminOperationVO.setMsisdn(userVO.getMsisdn());
			AdminOperationLog.log(adminOperationVO);

		} else {
			con.rollback();
			throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.SELECTED_CARD_GROUP_SET_IS_NO_CURRENT_VERSION, "");
		}

		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exiting forward=");
			loggerValue.append(methodName);
			LOG.debug(methodName,loggerValue);
		}
		response.setUpdateddefaultVoucherCadgroup(cardGroupSetVO.getCardGroupSetName());
		return response;
	}

	@Override
	public VoucherCardGroupVersionNumberListResponseVO loadVersionListBasedOnCardGroupSetIDAndDate(Connection con,
																								   String cardGroupSetId, Date date) throws BTSLBaseException, Exception {
		// TODO Auto-generated method stub5
		return null;
	}


	public VoucherTransferValueResponseVO viewVoucherTransferValue(Connection con, UserVO userVO, VoucherCardGroupTransferValueRequestVO request)throws BTSLBaseException, Exception {
		String methodName = "viewVoucherTransferValue";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}
		VoucherTransferValueResponseVO response = new VoucherTransferValueResponseVO();
		final CalculateVoucherTransferRuleVO requestVO = new CalculateVoucherTransferRuleVO();
		requestVO.setReceiverType(request.getReceiverType());
		requestVO.setSubService(request.getSubserviceType());
		requestVO.setReceiverServiceClass(request.getReceiverServiceClass());
		requestVO.setVoucherSegment(request.getVoucherSegment());
		requestVO.setVoucherType(request.getVoucherType());
		requestVO.setProductName(request.getProfile());
		requestVO.setNetworkCode(userVO.getNetworkID());
		requestVO.setUserId(PretupsI.SYSTEM);
		requestVO.setModuleType(PretupsI.CARD_GROUP_VMS);
		requestVO.setGatewayCode(request.getGateway());
		requestVO.setServiceType(request.getServiceDesc());
		requestVO.setDenomination(request.getDenomination());
		requestVO.setValidityDate(request.getOldValidityDate());
		requestVO.setApplicableFrom(request.getApplicableFromDate());
		requestVO.setApplicableTime(request.getApplicableFromHour());
		requestVO.setIdentifierType(userVO.getLoginID());
		String password = BTSLUtil.decryptText(userVO.getPassword());
		requestVO.setIdentifierValue(password);
		PretupsRestClient pretupsRestClient=new PretupsRestClient();
		String responseString = pretupsRestClient.postJSONRequest(requestVO, PretupsRestI.CALCULATE_VOUCHER_CARDGROUP);
		PretupsResponse<JsonNode> pretupsResponse = (PretupsResponse<JsonNode>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<JsonNode>>() {
				});

		if(pretupsResponse.getStatus()!=null && pretupsResponse.getStatus()==true)
		{
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode dataObject =  pretupsResponse.getDataObject();
			CardGroupDetailsVO cardGroupVO = (CardGroupDetailsVO)PretupsRestUtil.convertJSONToObject(pretupsResponse.getDataObject().get("cardGroupVO").toString(), new TypeReference<CardGroupDetailsVO>() {});
			P2PTransferVO transferVO=(P2PTransferVO)PretupsRestUtil.convertJSONToObject(pretupsResponse.getDataObject().get("transferVO").toString(), new TypeReference<P2PTransferVO>() {});
			List<BonusBundleDetailVO> bonusBundlesList1 = Arrays.asList(objectMapper.readValue(pretupsResponse.getDataObject().get("bonusBundlesList").toString(), BonusBundleDetailVO[].class));
			ViewVoucherCardGroupResponseVO viewVoucherCardGroupResponseVO = new ViewVoucherCardGroupResponseVO();
			this.constructFormFromVO(viewVoucherCardGroupResponseVO, cardGroupVO);
			response.setCardGroupSetName(cardGroupVO.getCardGroupSetName());
			response.setCardGroupSubServiceID(cardGroupVO.getServiceTypeId() + ":" + cardGroupVO.getCardGroupSubServiceId());// updated
			// by
			// shishu
			final String format = Constants.getProperty("CARDGROUP_DATE_FORMAT");
			response.setCardGroupSubServiceName(request.getSubserviceType());
			response.setServiceTypeDesc(request.getServiceDesc());
			response.setSetType(cardGroupVO.getSetType());
			response.setSetTypeName(cardGroupVO.getSetTypeName());
			// set the transfer rules value on the form
			response.setNewValidDate(BTSLUtil.getDateStringFromDate(transferVO.getValidityDateToBeSet(), format));
			response.setSetTypeName(cardGroupVO.getSetTypeName());
			response.setRowIndex(cardGroupVO.getRowIndex());
			response.setReceiverTransferValue(transferVO.getReceiverTransferValueAsString());
			response.setReceiverTransferValuesTax1(transferVO.getReceiverTax1ValueAsString());
			response.setReceiverTransferValuesTax2(transferVO.getReceiverTax2ValueAsString());
			response.setReceiverTransferValuesProcessingValue(transferVO.getReceiverAccessFeeAsString());
			response.setProfileId(transferVO.getProductId());
			String transferValue = String.valueOf(   PretupsBL.getSystemAmount(transferVO.getTransferValue()));
			response.setDenomination(Double.valueOf(transferValue));
			ArrayList bonusBundlesList=new ArrayList<>();
			bonusBundlesList.addAll(bonusBundlesList1);
			response.setBonusBundleList(bonusBundlesList);
			response.setViewVoucherCardGroupResponseVO(viewVoucherCardGroupResponseVO);

		}
		else
		{
			BTSLMessages btslMessage=null;
			if(pretupsResponse.getParameters()==null)
			{
				throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.NO_TRANSFER_RULE_ASSOCIATED, "");

			}
		}


		return response;



	}


	private void constructFormFromVO(ViewVoucherCardGroupResponseVO  theForm, CardGroupDetailsVO cardVO) throws Exception {
		final String methodName = "constructFormFromVO";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered CardGroupDetailsVO=" + cardVO.toString());
		}

		theForm.setCardGroupID(cardVO.getCardGroupID());
		theForm.setCardGroupCode(cardVO.getCardGroupCode());
		theForm.setStartRange(cardVO.getStartRangeAsString());
		theForm.setEndRange(cardVO.getEndRangeAsString());
		theForm.setValidityPeriodType(cardVO.getValidityPeriodType());
		theForm.setValidityPeriod(String.valueOf(cardVO.getValidityPeriod()));
		theForm.setGracePeriod(String.valueOf(cardVO.getGracePeriod()));
		// theForm.setMultipleOf(String.valueOf(cardVO.getMultipleOf()));
		theForm.setMultipleOf(cardVO.getMultipleOfAsString());
		theForm.setReceiverTax1Name(cardVO.getReceiverTax1Name());
		theForm.setReceiverTax1Type(cardVO.getReceiverTax1Type());
		if(cardVO.getReceiverTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
			theForm.setReceiverTax1Rate(PretupsBL.getDisplayAmount(cardVO.getReceiverTax1Rate()));
		}
		else
			theForm.setReceiverTax1Rate(cardVO.getReceiverTax1RateAsString());
		theForm.setReceiverTax2Name(cardVO.getReceiverTax2Name());
		theForm.setReceiverTax2Type(cardVO.getReceiverTax2Type());
		if(cardVO.getReceiverTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
			theForm.setReceiverTax2Rate(PretupsBL.getDisplayAmount(cardVO.getReceiverTax2Rate()));
		}
		else
			theForm.setReceiverTax2Rate(cardVO.getReceiverTax2RateAsString());

		theForm.setReceiverAccessFeeType(cardVO.getReceiverAccessFeeType());
		if(cardVO.getReceiverAccessFeeType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
			theForm.setReceiverAccessFeeRate(PretupsBL.getDisplayAmount(cardVO.getReceiverAccessFeeRate()));
		}
		else
			theForm.setReceiverAccessFeeRate(cardVO.getReceiverAccessFeeRateAsString());
		theForm.setMinReceiverAccessFee(cardVO.getMinReceiverAccessFeeAsString());
		theForm.setMaxReceiverAccessFee(cardVO.getMaxReceiverAccessFeeAsString());
		// added for card group slab suspend/resume
		theForm.setCGStatus(cardVO.getStatus());
		theForm.setBonusValidityValue(String.valueOf(cardVO.getBonusValidityValue()));
		theForm.setReceiverConvFactor(cardVO.getReceiverConvFactor());
		theForm.setCardName(cardVO.getCardName());
		theForm.setReversalPermitted(cardVO.getReversalPermitted());
		theForm.setReversalModifiedDate(cardVO.getReversalModifiedDate());

		final DateFormat dateFormat = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
		if(cardVO.getReversalModifiedDate()!=null)
			theForm.setReversalModifiedDateAsString(BTSLDateUtil.getLocaleTimeStamp(dateFormat.format(cardVO.getReversalModifiedDate())));
		theForm.setTempAccList(cardVO.getBonusAccList());
		// added for cos by gaurav


		if(cardVO.getVoucherType()!=null)
		{
			theForm.setVoucherType(cardVO.getVoucherType());

		}
		if(cardVO.getVoucherTypeDesc()!=null)
			theForm.setVoucherTypeDesc(cardVO.getVoucherTypeDesc());
		if(cardVO.getVoucherSegment()!=null)
		{
			theForm.setSegment(cardVO.getVoucherSegment());

		}
		if(cardVO.getVoucherSegmentDesc()!=null)
			theForm.setSegmentDesc(cardVO.getVoucherSegmentDesc());
		if(cardVO.getVoucherDenomination()!=null)
		{
			theForm.setVoucherDenomination(String.valueOf(Double.valueOf(cardVO.getVoucherDenomination())));

		}
		theForm.setVoucherDenomination(String.valueOf(cardVO.getStartRange()));
		if(cardVO.getProductName()!=null)
			theForm.setDenominationProfileDesc(cardVO.getProductName());


		// checkDisplayBundles(theForm);

		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting: Form Params Populated from VoucherCardGroupDetailsVO:" + theForm.toString());
		}
	}
	@Override
	public int deleteVoucherCardGroup(Connection con, UserVO userVO, String cardGroupSetId, String serviceTypeDesc, String subServiceTypeDesc, String cardGroupSetName) throws BTSLBaseException, Exception {
		final String methodName = "deleteVoucherCardGroup";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered cardGroupSetId=" + cardGroupSetId);
		}

		Date currentDate = new Date();
		CardGroupSetVO setVO =  new CardGroupSetVO();
		setVO.setModifiedBy(userVO.getUserID());
		setVO.setModifiedOn(currentDate);
		setVO.setStatus(PretupsI.STATUS_DELETE);
		setVO.setSubServiceTypeDescription(subServiceTypeDesc);
		Map<String, Object> data = new HashMap<>();
		data.put("networkCode",userVO.getNetworkID());
		data.put("moduleCode", PretupsI.CARD_GROUP_P2P);
		data.put("serviceTypeDesc", serviceTypeDesc);
		data.put("subServiceTypeDesc",subServiceTypeDesc);
		data.put("cardGroupSetName",cardGroupSetName);
		data.put("modifiedBy", userVO.getUserID());
		Map<String, Object> requestObject = new HashMap<>();
		String password = BTSLUtil.decryptText(userVO.getPassword());
		userVO.setPassword(password);
		PretupsRestUtil.setLoginDetailsInRequest(requestObject, userVO);
		requestObject.put("data", data);
		// check wheather card group is assiciated with active transfer
		// rule or not
		// added this check for CR00045 date 09/06/06
		TransferWebDAO transferwebDAO = new TransferWebDAO();
		final boolean transferRuleExists = transferwebDAO.isTransferRuleExistforCardGroup(con, setVO.getCardGroupSetID(), PretupsI.P2P_MODULE);
		if (!transferRuleExists) {
			PretupsRestClient pretupsRestClient=new PretupsRestClient();
			String responseString = pretupsRestClient.postJSONRequest(requestObject, PretupsRestI.DELETE_CARDGROUP);

			PretupsResponse<JsonNode> pretupsResponse = (PretupsResponse<JsonNode>) PretupsRestUtil
					.convertJSONToObject(responseString, new TypeReference<PretupsResponse<JsonNode>>() {
					});
			if (pretupsResponse.getStatusCode() == 200 && pretupsResponse.getStatus()) {

				con.commit();
				Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
				final AdminOperationVO adminOperationVO = new AdminOperationVO();
				adminOperationVO.setSource(PretupsI.LOGGER_OPERATION_DELETE);
				adminOperationVO.setDate(currentDate);
				adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
				String str[]= {cardGroupSetName};

				String resmsg = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.SUCCESSFULLY_DELETED_VOUCHER_CARD_GROUP_SET, str);
				adminOperationVO.setInfo(resmsg );
				adminOperationVO.setLoginID(userVO.getLoginID());
				adminOperationVO.setUserID(userVO.getUserID());
				adminOperationVO.setCategoryCode(userVO.getCategoryCode());
				adminOperationVO.setNetworkCode(userVO.getNetworkID());
				adminOperationVO.setMsisdn(userVO.getMsisdn());
				AdminOperationLog.log(adminOperationVO);


				return 1;
			}
			else {
				throw new BTSLBaseException(CLASS_NAME, methodName, pretupsResponse.getMessageCode(), 0, pretupsResponse.getParameters(), null);

			}


		}else {
			throw new BTSLBaseException(CLASS_NAME, methodName,PretupsErrorCodesI.TRANSFER_RULE_EXIXTS, "");

		}

	}
	@Override
	public VoucherCardGroupStatusResponseVO loadVoucherCardGroupStatusList(Connection con, UserVO userVO, List<CardGroupStatusRequestVO> requestVO)throws BTSLBaseException, Exception {

		final String methodName = "loadVoucherCardGroupStatusList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered loadVoucherCardGroupStatusList=");
		}


		final CardGroupDAO cardGroupDAO = new CardGroupDAO();
		List<CardGroupSetVO> cardGroupSetNameList = null;
		// load the card group set names
		VoucherCardGroupStatusResponseVO response = new VoucherCardGroupStatusResponseVO();
		PretupsRestClient pretupsRestClient = new PretupsRestClient();
		Map<String, Object> data = new HashMap<>();

		final CardGroupStatusVO VO = new CardGroupStatusVO();

		VO.setModuleCode(PretupsI.CARD_GROUP_P2P);
		VO.setNetworkCode(userVO.getNetworkID());
		VO.setIdentifierType(userVO.getLoginID());
		String password = BTSLUtil.decryptText(userVO.getPassword());
		VO.setIdentifierValue(password);



		cardGroupSetNameList = cardGroupDAO.loadCardGroupSet(con, userVO.getNetworkID(), PretupsI.P2P_MODULE);

		List<CardGroupSetVO> responseCardList = new ArrayList<>();
		for(CardGroupStatusRequestVO vo: requestVO) {
			for(int i=0;i<cardGroupSetNameList.size();i++) {
				CardGroupSetVO setVO = cardGroupSetNameList.get(i);
				if((vo.getCardGroupSetName().equals(setVO.getCardGroupSetName())) &&(vo.getServiceType().equals(setVO.getServiceType()))&&(vo.getSubServiceType().equals(setVO.getSubServiceType())) && (vo.getSetType().equals(setVO.getSetType()))){
					responseCardList.add(setVO);
				}
			}

		}

		response.setCardGroupStatusList(responseCardList);
		if(cardGroupSetNameList != null &&cardGroupSetNameList.size()>0) {
			return response;
		}
		else {
			throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.FAILED_LOAD_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS, "");

		}


	}
	private ArrayList filterCardGroupSetNameList(List list, String cardGroupType) {
		final String methodName = "filterCardGroupSetNameList";
		if(list != null && list.size() > 0) {
			Iterator itr = list.iterator();
			while (itr.hasNext())
			{
				CardGroupSetVO cardGroupSetVO = (CardGroupSetVO) itr.next();
				String serviceType = cardGroupSetVO.getServiceType();
				if(!BTSLUtil.isNullString(cardGroupType) && !BTSLUtil.isNullString(serviceType)) {
					if(PretupsI.CARD_GROUP_VMS.equals(cardGroupType) && !"VCN".equals(serviceType) ||
							!PretupsI.CARD_GROUP_VMS.equals(cardGroupType) && "VCN".equals(serviceType)) {
						itr.remove();
					}
				}
			}
		}
		ArrayList arrayList = new ArrayList();
		arrayList.addAll(list);
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exited : list.size()="+list.size());
		}
		return arrayList;
	}

	@Override
	public C2SCardGroupStatusSaveResponseVO changeVoucherCardGroupStatusList(Connection con, UserVO userVO,
																			 ChangeVoucherCardGroupStatusListRequestVO requestVO) throws BTSLBaseException, Exception {
		final String methodName = "changeVoucherCardGroupStatusList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}


		final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();

		C2SCardGroupStatusSaveResponseVO response = new C2SCardGroupStatusSaveResponseVO();

		final Date currentDate = new Date();
		List<CardGroupSetVO> cardGroupSetVOList = new ArrayList<>();

		// set the default values
		for (int i = 0, j = requestVO.getChangeVoucherCardGroupStatusRequestList().size(); i < j; i++) {
			ChangeVoucherCardGroupStatusRequest statusVO = requestVO.getChangeVoucherCardGroupStatusRequestList().get(i);
			CardGroupSetVO cardGroupSetVO = new CardGroupSetVO();
			cardGroupSetVO.setModifiedOn(currentDate);
			cardGroupSetVO.setModifiedBy(userVO.getUserID());
			cardGroupSetVO.setCardGroupSetName(statusVO.getCardGroupSetName());
			cardGroupSetVO.setCardGroupSetID(statusVO.getCardGroupSetID());
			cardGroupSetVO.setNetworkCode(userVO.getNetworkID());
			cardGroupSetVO.setCreatedOn(statusVO.getCreatedOn());
			cardGroupSetVO.setCreatedBy(statusVO.getCreatedBy());
			cardGroupSetVO.setLastVersion(statusVO.getLastVersion());
			cardGroupSetVO.setModuleCode(PretupsI.P2P_MODULE);
			cardGroupSetVO.setStatus(statusVO.getStatus());
			cardGroupSetVO.setLastModifiedOn(statusVO.getLastModifiedOn());
			cardGroupSetVO.setLanguage1Message(statusVO.getLanguage1Message());
			cardGroupSetVO.setLanguage2Message(statusVO.getLanguage2Message());
			cardGroupSetVO.setSubServiceType(statusVO.getSubServiceType());
			cardGroupSetVO.setSubServiceTypeDescription(statusVO.getSubServiceTypeDesc());
			cardGroupSetVO.setServiceTypeDesc(statusVO.getServiceTypeDesc());
			cardGroupSetVO.setServiceTypeDesc(statusVO.getServiceTypeDesc());
			cardGroupSetVO.setSetType(statusVO.getSetType());
			cardGroupSetVO.setDefaultCardGroup(PretupsI.NO);
			cardGroupSetVO.setSetTypeName(statusVO.getSetTypeName());
			cardGroupSetVOList.add(cardGroupSetVO);
		}

		// Delete Commission Profile Set
		int updateCount = 0;
		int statusCode = 0;
		Map<String, Object> data = new HashMap<>();
		data.put("moduleCode", PretupsI.CARD_GROUP_P2P);
		data.put("networkCode", userVO.getNetworkID());

//                    ArrayList list = theForm.getCardGroupSetNameList();
		data.put("cardGroupSetList", cardGroupSetVOList);
		Map<String, Object> object = new HashMap<>();
		String password = BTSLUtil.decryptText(userVO.getPassword());

		userVO.setPassword(password);
		PretupsRestUtil.setLoginDetailsInRequest(object, userVO);
		object.put("data", data);

		PretupsRestClient pretupsRestClient = new PretupsRestClient();
		String responseString = pretupsRestClient.postJSONRequest(object, PretupsRestI.SUSPEND_CARDGROUP);
		PretupsResponse<List<CardGroupSetVO>> restResponse = (PretupsResponse<List<CardGroupSetVO>>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<CardGroupSetVO>>>() {
				});
		statusCode =  restResponse.getStatusCode();

		if(statusCode == 200 && restResponse.getStatus()) {
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			final AdminOperationVO adminOperationVO = new AdminOperationVO();
			adminOperationVO.setSource(PretupsI.LOGGER_OPERATION_CHANGE_STATUS);
			adminOperationVO.setDate(currentDate);
			adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_CHANGE_STATUS);


			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.VOUCHER_GROUP_STATUS_UPDATED_SUCCESSFULLY, null);
			adminOperationVO.setInfo(resmsg );
			adminOperationVO.setLoginID(userVO.getLoginID());
			adminOperationVO.setUserID(userVO.getUserID());
			adminOperationVO.setCategoryCode(userVO.getCategoryCode());
			adminOperationVO.setNetworkCode(userVO.getNetworkID());
			adminOperationVO.setMsisdn(userVO.getMsisdn());
			AdminOperationLog.log(adminOperationVO);
			return response;
		}

		else {
			throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_GROUP_STATUS_UPDATION_FAILED  ,"");
		}
	}

	public CalculateTransferValueResponseVO calculateTransferValue(Connection con, UserVO userVO,CalculateTransferValueResponseVO response, VoucherGroupDetailsRequestVO requestVO)  throws BTSLBaseException, Exception{
		final String methodName = "calculateTransferValue";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}
		VoucherGroupDetails voucherCardgroup = requestVO.getVoucherGroupDetailsList().get(0);
		String denomination = voucherCardgroup.getDenomination();
		long amount = PretupsBL.getSystemAmount(voucherCardgroup.getDenomination());
		final CardGroupDetailsVO cardGroupDetailVO = new CardGroupDetailsVO();
		this.convertRequestToCardGroup(voucherCardgroup,cardGroupDetailVO );
		if (!BTSLUtil.isNullString(requestVO.getSubService())) {
			final String serviceID = requestVO.getSubService();
			final int index = serviceID.indexOf(":");
			if (index != -1) {
				cardGroupDetailVO.setServiceTypeSelector(requestVO.getSubService().replace(':', '_'));
			}
		}
		cardGroupDetailVO.setCardGroupType(PretupsI.CARD_GROUP_VMS);
		// check the start range value(Talk time is valid or not)
		final P2PTransferVO p2pTransferVO ;

		p2pTransferVO = this.calculateTalkTime(cardGroupDetailVO, PretupsBL.getSystemAmount(requestVO.getVoucherGroupDetailsList().get(0).getDenomination()), requestVO.getSubService().split(":")[1], false);

		// by
		// shishu


		if (p2pTransferVO.getReceiverTransferValue() < 0) {
			// invalid talk time
			String errorMsg = null;

			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			final String[] arr = {PretupsBL.getDisplayAmount(p2pTransferVO.getReceiverTransferValue())};
			errorMsg= RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOUCHER_CARDGROUP_RECIVER_TRANSFER_VALUE_INVALID_SLAB ,arr);
			response.setMessage(errorMsg);
			response.setMessageCode(PretupsErrorCodesI.VOUCHER_CARDGROUP_RECIVER_TRANSFER_VALUE_INVALID_SLAB);
			throw new BTSLBaseException(CLASS_NAME, methodName,
					PretupsErrorCodesI.VOUCHER_CARDGROUP_RECIVER_TRANSFER_VALUE_INVALID_SLAB,  arr);

		}

//            theForm.setP2pTransferVO(p2pTransferVO);
		response.setNewValidityDate(p2pTransferVO.getValidityDateToBeSetAsString());
		response.setReceiverTax1Value(p2pTransferVO.getReceiverTax1ValueAsString());
		response.setReceiverTax2Value(p2pTransferVO.getReceiverTax2ValueAsString());
		response.setReceiverAccessFee(p2pTransferVO.getReceiverAccessFeeAsString());
		response.setReceiverTransferValue(p2pTransferVO.getReceiverTransferValueAsString());



		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting");
		}

		return response;
	}


}
