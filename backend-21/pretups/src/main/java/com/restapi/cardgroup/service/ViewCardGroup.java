package com.restapi.cardgroup.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.MediaType;


import com.annotation.RestEasyAnnotation;
import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.BonusAccountDetailsVO;
import com.btsl.pretups.cardgroup.businesslogic.BonusBundleDetailVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsReqVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVersionVO;
import com.btsl.pretups.channel.profile.businesslogic.LoadVersionListRequestVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.vomsproduct.businesslogic.VoucherTypeVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.web.pretups.transfer.businesslogic.TransferWebDAO;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RestEasyAnnotation
@RequestMapping(value = "/cardGroup")
@Tag(name = "${ViewCardGroup.name}", description = "${ViewCardGroup.desc}")//@Api(value="View voucher card group")
public class ViewCardGroup {

	public static final Log log = LogFactory.getLog(ViewCardGroup.class.getName());
	
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/cardGroupSetVersions", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	//@ApiOperation(value = "Load voucher card group versions list", response = PretupsResponse.class)
	public PretupsResponse<List<CardGroupSetVersionVO>> loadVersionList(
	@Parameter(description = SwaggerAPIDescriptionI.LOAD_VERSION_LIST, required = true)
	@RequestBody  LoadVersionListRequestVO requestVO) throws IOException, SQLException, BTSLBaseException {
		final String methodName =  "loadVersionList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
        MComConnectionI mcomCon = null;
        Date currentDate = new Date();
        int numberOfDays = 0;
        PretupsResponse<List<CardGroupSetVersionVO>> response = null;
        try {
			mcomCon = new MComConnection();
            con=mcomCon.getConnection();
			response = new PretupsResponse<List<CardGroupSetVersionVO>>();
			
			String categories = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CARD_GROUP_ALLOWED_CATEGORIES));
			String[] allowedCategories = categories.split(",");
			PretupsRestUtil.validateLoggedInUser(requestVO.getIdentifierType(), requestVO.getIdentifierValue(), con,
					response, allowedCategories);
			if (response.hasFormError()) {
				response.setStatus(false);
				response.setMessageCode("user.unauthorized");
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			if(BTSLUtil.isEmpty(requestVO.getNetworkCode()) || BTSLUtil.isEmpty(requestVO.getModuleCode())) {
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "Invalid Format");
				return response;
			}
			String numberOfDaysStr = requestVO.getNumberOfDays();
			if (!BTSLUtil.isNullString(numberOfDaysStr) && BTSLUtil.isNumeric(numberOfDaysStr)) {
                numberOfDays = Integer.parseInt(numberOfDaysStr);
                if(numberOfDays<(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LAST_DAYS_CG))).intValue())|| numberOfDays >(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LAST_DAYS_CG))).intValue())){
                	String message1 = "cardgroup.setversion.error.range";
                	final String args[] = {String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LAST_DAYS_CG))).intValue()),String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LAST_DAYS_CG))).intValue()) };
   				 	final BTSLMessages messages = new BTSLMessages(message1, args);
   				 	response.setStatus(false);
   				 	response.setMessageCode(messages.toString());
   				 	response.setStatusCode(PretupsI.RESPONSE_FAIL);
   				 	return response;
                }
                numberOfDays = 0 - numberOfDays;
            }
			else{
				String message1 = "cardgroup.setversion.error.range";
            	final String args[] = {String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LAST_DAYS_CG))).intValue()),String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LAST_DAYS_CG))).intValue()) };
				final BTSLMessages messages = new BTSLMessages(message1, args);
				response.setStatus(false);
				response.setMessageCode(messages.toString());
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
            currentDate = BTSLUtil.addDaysInUtilDate(currentDate, numberOfDays);
            CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, cardGroupSetDAO.loadCardGroupSetVersion(con, requestVO.getNetworkCode(), currentDate,
					requestVO.getModuleCode()));
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
        } catch (Exception e) {
            log.error(methodName, "Exceptin:e=" + e);
            log.errorTrace(methodName, e);
        } finally {
            try {
            	if (mcomCon != null) {
    				mcomCon.close("ViewCardGroup#loadCardGroupSet");
    				mcomCon = null;
    			}
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }
        }
		return response;
	}
	
	/**
	 * This method delete the card group
	 * @param requestData Json string of lookup type
	 * @return  PretupsResponse<JsonNode>
	 * @throws IOException, Exception
	 */
	@PostMapping(value = "/deleteCardGroup", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	//@ApiOperation(value = "Delete voucher card group", response = PretupsResponse.class)
	public PretupsResponse<JsonNode> deleteCardGroup(
			@Parameter(description =SwaggerAPIDescriptionI.DELETE_CARD_GROUP, required = true)
			@RequestBody String requestData) throws IOException, SQLException, BTSLBaseException {
		final String methodName =  "deleteCardGroup";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
        MComConnectionI mcomCon = null;
        PretupsResponse<JsonNode> response = null;
        try {
			mcomCon = new MComConnection();
            con = mcomCon.getConnection();
			response = new PretupsResponse<>();
			final CardGroupDAO cardGroupDAO = new CardGroupDAO();
			final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
			JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
			
			String categories = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CARD_GROUP_ALLOWED_CATEGORIES));
			String[] allowedCategories = categories.split(",");
			PretupsRestUtil.validateLoggedInUser(requestNode, con, response, allowedCategories);
			if (response.hasFormError()) {
				response.setStatus(false);
				response.setMessageCode("user.unauthorized");
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			JsonNode dataNode =  requestNode.get("data");
			
			//if the format of the api is incorrect 
			if(!dataNode.has("networkCode") || !dataNode.has("moduleCode") || 
					!dataNode.has("cardGroupSetName") || !dataNode.has("serviceTypeDesc") || !dataNode.has("subServiceTypeDesc") || !dataNode.has("modifiedBy") || dataNode.size() == 0){
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "Invalid format");
				return response;
			}
        	
			if(BTSLUtil.isNullString(dataNode.get("networkCode").textValue()))
			{
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "Network Code is empty");
				return response;
			}
			
			if(BTSLUtil.isNullString(dataNode.get("moduleCode").textValue()))
			{
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "Module Code is empty");
				return response;
			}
			
			if(BTSLUtil.isNullString(dataNode.get("serviceTypeDesc").textValue()))
			{
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "ServiceType is empty");
				return response;
			}
			
			if(BTSLUtil.isNullString(dataNode.get("subServiceTypeDesc").textValue()))
			{
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "SubService Type is empty");
				return response;
			}
			
			if(BTSLUtil.isNullString(dataNode.get("cardGroupSetName").textValue()))
			{
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "CardGroup SetName is empty");
				return response;
			}
			
			if(BTSLUtil.isNullString(dataNode.get("modifiedBy").textValue()))
			{
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "Modified By is empty");
				return response;
			}

        	String newCardGroupSetName = dataNode.get("cardGroupSetName").textValue();
			String networkCode = dataNode.get("networkCode").textValue();
        	String serviceTypeDesc = cardGroupDAO.loadServiceType(con, networkCode, PretupsI.P2P_MODULE, dataNode.get("serviceTypeDesc").textValue());
			String subServiceTypeCode = dataNode.get("subServiceTypeDesc").textValue();
			String subServiceTypeDesc = null;
			ArrayList<ServiceSelectorMappingVO> subServiceTypeList = ServiceSelectorMappingCache.getSelectorListForServiceType(serviceTypeDesc);
            
            for( ServiceSelectorMappingVO serviceSelectorMappingVO:subServiceTypeList){
             if (serviceSelectorMappingVO.getSelectorName().equalsIgnoreCase(subServiceTypeCode)) {
            	 subServiceTypeDesc = (serviceSelectorMappingVO.getSelectorCode());
                  break;
             }
            }

			
			boolean isCardGroupSetExist = cardGroupDAO.isCardGroupSetExist(con, serviceTypeDesc, subServiceTypeDesc, networkCode, newCardGroupSetName);
			if(!isCardGroupSetExist)				// No such card group
            {
            	response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setMessageCode("promotrfrule.addtrfrule.msg.nocardgroupset");
				response.setMessageKey("promotrfrule.addtrfrule.msg.nocardgroupset");
				response.setMessage(newCardGroupSetName + " card group doesn't exist in your network.");
				return response;
            }
			
			ArrayList<String> defaultCardGroup = new ArrayList<>();
			defaultCardGroup=cardGroupDAO.loadDefaultCardGroup(con,serviceTypeDesc,subServiceTypeDesc,PretupsI.YES,networkCode);
			if(!defaultCardGroup.isEmpty())
			{
			String defaultCardGroupSetName = defaultCardGroup.get(1);

			if ((newCardGroupSetName.equals(defaultCardGroupSetName))) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setMessageCode("cardgroup.cardgroupp2pdetails.error.isdefault.nottodelete");
				response.setMessageKey("cardgroup.cardgroupp2pdetails.error.isdefault.nottodelete");
				response.setMessage(newCardGroupSetName + " is default card group.");
				return response;
			}
			}
			
			TransferWebDAO transferwebDAO = new TransferWebDAO();
			CardGroupSetVO cardGroupSetVO = PretupsRestUtil.getCardGroupSet(con, dataNode);
			if(BTSLUtil.isNullObject(cardGroupSetVO))
			{
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setMessageCode("promotrfrule.addtrfrule.msg.nocardgroupset");
				response.setMessageKey("promotrfrule.addtrfrule.msg.nocardgroupset");
				response.setMessage("No card group exists with given data");
				return response;
			}
			final boolean transferRuleExists = transferwebDAO.isTransferRuleExistforCardGroup(con, cardGroupSetVO.getCardGroupSetID(), PretupsI.P2P_MODULE);
			
			if(!transferRuleExists) {
				final Date currentDate = new Date();
				cardGroupSetVO.setModifiedBy(dataNode.get("modifiedBy").textValue());
				cardGroupSetVO.setModifiedOn(currentDate);
				cardGroupSetVO.setStatus(PretupsI.STATUS_DELETE);
				
				if(cardGroupSetVO != null) {
					final int deleteCount = cardGroupSetDAO.deleteCardGroupSet(con, cardGroupSetVO);
					if(deleteCount > 0)
					{
						try {
							mcomCon.finalCommit();
                        } catch (Exception e) {
                            log.errorTrace(methodName, e);
                        }
						response.setStatus(true);
						response.setStatusCode(PretupsI.RESPONSE_SUCCESS);
						response.setMessageCode("cardgroup.cardgroupdetailsview.successdeletemessage");
						response.setMessageKey("cardgroup.cardgroupdetailsview.successdeletemessage");
						response.setMessage("Card group details successfully deleted");
					}
					
					else
					{
						mcomCon.finalRollback();
						response.setStatus(false);
						response.setStatusCode(PretupsI.RESPONSE_FAIL);
						response.setMessageCode("error.general.processing");
						response.setMessageKey("error.general.processing");
						response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
					}
				}
			}
			
			else
			{
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setMessageCode("cardgroup.cardgroupp2pdetailsview.deletetransferruleexistsmessage");
				response.setMessageKey("cardgroup.cardgroupp2pdetailsview.deletetransferruleexistsmessage");
				response.setMessage("Card group cannot be deleted as it is associated with transfer rule");
			}
			
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
        } catch (Exception e) {
            log.error(methodName, "Exceptin:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus(false);
			response.setStatusCode(PretupsI.RESPONSE_FAIL);
			response.setMessageCode("error.general.processing");
			response.setMessageKey("error.general.processing");
			response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
        } finally {
        	try {
            	if (mcomCon != null) {
    				mcomCon.close("deleteCardGroup#" + "ViewCardGroup");
    				mcomCon = null;
    			}
            } catch (Exception e) {
            	log.errorTrace(methodName, e);
            }
        	
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }
        }
        log.debug(methodName, response);
		return response;
	}
	
	@PostMapping(value = "/viewCardGroupSetDetails", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	//@ApiOperation(value = "View voucher card group detail", response = PretupsResponse.class)
	public PretupsResponse<List<CardGroupDetailsVO>> viewCardGroupSetDetails(
			@Parameter(description = SwaggerAPIDescriptionI.VIEW_CARD_GROUP_SET, required = true)
			 @RequestBody CardGroupDetailsReqVO requestData ) throws IOException, SQLException, BTSLBaseException {
		final String methodName =  "viewCardGroupSetDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
        MComConnectionI mcomCon = null;
        final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
        final CardGroupDAO cardGroupDAO = new CardGroupDAO();
        Date currentDate = new Date();
        PretupsResponse<List<CardGroupDetailsVO>> response = null;
        try {
			mcomCon = new MComConnection();
            con=mcomCon.getConnection();
			response = new PretupsResponse<List<CardGroupDetailsVO>>();			
			Gson gson = new Gson();
			log.debug(methodName, "Gson Conversion");
			String jsonStr = gson.toJson(requestData);			
			JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(jsonStr, new TypeReference<JsonNode>() {});
			
			String categories = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CARD_GROUP_ALLOWED_CATEGORIES));
			String[] allowedCategories = categories.split(",");
			PretupsRestUtil.validateLoggedInUser(requestNode, con, response, allowedCategories);
			if (response.hasFormError()) {
				response.setStatus(false);
				response.setMessageCode("user.unauthorized");
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			JsonNode dataNode =  requestNode.get("data");
			if(!dataNode.has("networkCode") || !dataNode.has("version") || !dataNode.has("serviceTypeDesc") || !dataNode.has("subServiceTypeDesc") || !dataNode.has("numberOfDays") || !dataNode.has("cardGroupSetId") || dataNode.size() == 0){
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "Invalid Format");
				return response;
			}
			
			String serviceTypeDesc = cardGroupDAO.loadServiceType(con, dataNode.get("networkCode").textValue(), PretupsI.P2P_MODULE, dataNode.get("serviceTypeDesc").textValue());
			String subServiceTypeCode = dataNode.get("subServiceTypeDesc").textValue();
			String subServiceTypeDesc = null;
			ArrayList<ServiceSelectorMappingVO> subServiceTypeList = ServiceSelectorMappingCache.getSelectorListForServiceType(serviceTypeDesc);
            
            for( ServiceSelectorMappingVO serviceSelectorMappingVO:subServiceTypeList){
             if (serviceSelectorMappingVO.getSelectorName().equalsIgnoreCase(subServiceTypeCode)) {
            	 subServiceTypeDesc = (serviceSelectorMappingVO.getSelectorCode());
                  break;
             }
            }
            
			String numberOfDaysStr = dataNode.get("numberOfDays").textValue();
			int numberOfDays = 0;
			if (!BTSLUtil.isNullString(numberOfDaysStr) && BTSLUtil.isNumeric(numberOfDaysStr)) {
                numberOfDays = Integer.parseInt(numberOfDaysStr);
                numberOfDays = 0 - numberOfDays;
            }
			
			if (BTSLUtil.isNullString("version")) {
                //code for empty version
            }

            currentDate = BTSLUtil.addDaysInUtilDate(currentDate, numberOfDays);
			final ArrayList versionList = cardGroupSetDAO.loadCardGroupSetVersion(con, dataNode.get("networkCode").textValue(), currentDate, PretupsI.P2P_MODULE);

            String cardGroupSetId = dataNode.get("cardGroupSetId").textValue();
            String version = dataNode.get("version").textValue();
            int isValid = 0;
            CardGroupSetVersionVO cardGroupSetVersionVO = null;
            ArrayList<CardGroupSetVersionVO> list = new ArrayList<>();
            if (versionList != null && !versionList.isEmpty()) {
                for (int i = 0, j = versionList.size(); i < j; i++) {
                    cardGroupSetVersionVO = (CardGroupSetVersionVO) versionList.get(i);
                    if (cardGroupSetVersionVO.getCardGroupSetID().equals(cardGroupSetId)) {
                    	list.add(cardGroupSetVersionVO);
                    	isValid = 1;
                    }
                }
			
            }
			if(isValid != 0) {
				ArrayList<CardGroupDetailsVO> cardGroupDetailList= new ArrayList<>();
				for(int i=0;i<list.size();i++)
				{
					cardGroupSetVersionVO = (CardGroupSetVersionVO)list.get(i);
					if(cardGroupSetId.equals(cardGroupSetVersionVO.getCardGroupSetID()) && version.equals(cardGroupSetVersionVO.getVersion()))
					{
						cardGroupDetailList = cardGroupDAO.loadCardGroupDetailsListByID(con, cardGroupSetId, version);
						break;
					}
				}
				
				if(cardGroupDetailList.size() <= 0)
					response.setResponse(PretupsI.RESPONSE_FAIL, false, "Card group set version does not exist.");
				
				else{
					VomsProductDAO vomsProductDAO = new VomsProductDAO();
					ArrayList<VoucherTypeVO> voucherTypeList = vomsProductDAO.loadVoucherDetails(con);
					for (int i = 0, j = cardGroupDetailList.size(); i < j; i++) {
						final CardGroupDetailsVO cardDetailVO = (CardGroupDetailsVO) cardGroupDetailList.get(i);
						if(PretupsI.VOUCHER_CONS_SERVICE.equals(serviceTypeDesc)){
						String voucherTypeDesc = BTSLUtil.getVoucherTypeDesc(voucherTypeList, cardDetailVO.getVoucherType());
						if (BTSLUtil.isNullString(voucherTypeDesc)){
					            throw new BTSLBaseException(this, methodName, "cardgroup.cardgroupdetails.err.msg.novouchertypefound");
					        }
				    	cardDetailVO.setVoucherTypeDesc(voucherTypeDesc);
                        cardDetailVO.setVoucherDenomination(String.valueOf(Double.parseDouble(PretupsBL.getDisplayAmount(cardDetailVO.getStartRange()))));
                        cardDetailVO.setVoucherSegmentDesc(BTSLUtil.getSegmentDesc(cardDetailVO.getVoucherSegment()));
                        ArrayList<VomsProductVO> voucherProductlist = vomsProductDAO.loadMrpProductDetailsList(con, cardDetailVO.getVoucherType(), "'" + VOMSI.VOMS_STATUS_ACTIVE + "'", false, "", dataNode.get("networkCode").textValue(),cardDetailVO.getVoucherSegment(),String.valueOf(PretupsBL.getSystemAmount(cardDetailVO.getVoucherDenomination())));
        	    		String voucherProduct=BTSLUtil.getVoucherProductName(voucherProductlist, cardDetailVO.getVoucherProductId());
        		    	if (BTSLUtil.isNullString(voucherProduct)){
        		    		 throw new BTSLBaseException(this, methodName, "cardgroup.cardgroupdetails.err.msg.noproductfound");
        		    	}
        		    	cardDetailVO.setProductName(voucherProduct);
						}
                        cardDetailVO.setApplicableFrom(cardGroupSetVersionVO.getApplicableFrom());
					}
				
					CardGroupDetailsVO cardDetailVO = (CardGroupDetailsVO) cardGroupDetailList.get(0);
					StringBuilder combinedCardGroupServiceID = new StringBuilder();
					combinedCardGroupServiceID.append(serviceTypeDesc);
					combinedCardGroupServiceID.append(":");
					combinedCardGroupServiceID.append(subServiceTypeDesc);
					cardDetailVO.setBonusAccList(arrangeBonuses(cardDetailVO.getBonusAccList(), combinedCardGroupServiceID.toString(), true));
					response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, cardGroupDetailList);
				}
				
			}
			
			else //if(isValid == 0)
			{
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "No such card group exists.");
			}
	
        } catch (Exception e) {
            log.error(methodName, "Exceptin:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus(false);
			response.setStatusCode(PretupsI.RESPONSE_FAIL);
			response.setMessageCode(e.getMessage());
			response.setMessageKey(e.getMessage());
			//response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
        } finally {
            try {
            	if (mcomCon != null) {
    				mcomCon.close("ViewCardGroup#viewCardGroupSetDetails");
    				mcomCon = null;
    			}
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }
        }
		return response;
	}
	
	public ArrayList arrangeBonuses(ArrayList p_bonusBundleList, String p_cardGroupSubServiceID, boolean p_isBonusAccDetailList)  throws BTSLBaseException{
        ArrayList arrangedList = null;
        final ArrayList tempList = p_bonusBundleList;
        int listSize = 0;
        String serviceSelectorKey = null;
        String receiverBonusID = null;
        BonusBundleDetailVO bundleDetailVO = null;
        BonusAccountDetailsVO accountDetailVO = null;

        ServiceSelectorMappingVO serviceSelectorMappingVO = null;
        String bundleId = null;
        try {
            final int index = p_cardGroupSubServiceID.indexOf(":");
            if (index != -1) {
                serviceSelectorKey = p_cardGroupSubServiceID.replace(':', '_');
                listSize = tempList.size();
                if (tempList != null && listSize > 0) {
                    serviceSelectorMappingVO = (ServiceSelectorMappingVO) ServiceSelectorMappingCache.getServiceSelectorMap().get(serviceSelectorKey);
                    receiverBonusID = serviceSelectorMappingVO.getReceiverBundleID();
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
        } catch (Exception e) {
            throw new BTSLBaseException(this, "arrangeBonuses", "");
        }
        return arrangedList;
    }
	
	public void loadVoucherData(Connection con,CardGroupSetVO cardGroupSetVO,CardGroupDetailsVO cardDetailVO) throws BTSLBaseException{
		String methodName = "loadVoucherData";
		VomsProductDAO vomsProductDAO = new VomsProductDAO();
			   
			ArrayList<VoucherTypeVO> voucherTypeList = vomsProductDAO.loadVoucherDetails(con);
			String voucherTypeDesc = BTSLUtil.getVoucherTypeDesc(voucherTypeList, cardDetailVO.getVoucherType());
			if (BTSLUtil.isNullString(voucherTypeDesc)){
		            throw new BTSLBaseException(this, methodName, "cardgroup.cardgroupdetails.err.msg.novouchertypefound");
		        }
	    	cardDetailVO.setVoucherTypeDesc(voucherTypeDesc);
	    	
	    	String type = vomsProductDAO.getTypeFromVoucherType(con, cardDetailVO.getVoucherType());
	    	if(!BTSLUtil.isNullString(type)) {
	    	ArrayList segmentList = BTSLUtil.getSegmentList(type, LookupsCache.loadLookupDropDown(VOMSI.VOUCHER_SEGMENT, true));
	    	String segment=	BTSLUtil.getSegment(cardDetailVO.getVoucherSegmentDesc());
	    	if (BTSLUtil.isNullString(segment)){
		            throw new BTSLBaseException(this, methodName, "cardgroup.cardgroupdetails.err.msg.novouchersegmentfound");
		        }
	    	cardDetailVO.setVoucherSegment(segment);
	    	}
	    	
	    	ArrayList<VomsCategoryVO> denominationList = vomsProductDAO.getMrpList(con, cardDetailVO.getVoucherType(), cardGroupSetVO.getNetworkCode(),cardDetailVO.getVoucherSegment()); // for
	    	if (denominationList == null || denominationList.isEmpty()) {
	            throw new BTSLBaseException(this, methodName, "cardgroup.cardgroupdetails.err.msg.nomrpfound");
	        }
	    	cardDetailVO.setStartRange(PretupsBL.getSystemAmount(cardDetailVO.getVoucherDenomination()));
	    	cardDetailVO.setEndRange(PretupsBL.getSystemAmount(cardDetailVO.getVoucherDenomination()));
	    	
	    	ArrayList<VomsProductVO> voucherProductlist = vomsProductDAO.loadMrpProductDetailsList(con, cardDetailVO.getVoucherType(), "'" + VOMSI.VOMS_STATUS_ACTIVE + "'", false, "", cardGroupSetVO.getNetworkCode(),cardDetailVO.getVoucherSegment(),String.valueOf(cardDetailVO.getStartRange()));
	    		String voucherProduct=	BTSLUtil.getVoucherProductId(voucherProductlist, cardDetailVO.getProductName());
		    	if (BTSLUtil.isNullString(voucherProduct)){
		    		 throw new BTSLBaseException(this, methodName, "cardgroup.cardgroupdetails.err.msg.noproductfound");
		    	}
	    	cardDetailVO.setVoucherProductId(voucherProduct);
	    	
	}
}
