package com.restapi.cardgroup.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.core.MediaType;


import com.annotation.RestEasyAnnotation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.spring.custom.action.action.ActionErrors;
import org.spring.custom.action.action.ActionMessage;
import org.spring.custom.action.action.ActionMessages;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListSorterUtil;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
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
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVersionDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVersionVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.vomsproduct.businesslogic.VoucherTypeVO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * This class implements LookupsRestService and provides basic method to load Look and sublookups
 */
/*@RestController
@RequestMapping("/cardGroup")*/
@RestController
@RestEasyAnnotation
@RequestMapping(value = "/cardGroup")
@Tag(name = "${AddP2PCardGroup.name}", description = "${AddP2PCardGroup.desc}")
public class AddP2PCardGroup {

	public static final Log log = LogFactory.getLog(AddCardGroup.class.getName());
	
	@PostMapping(value = "/addP2PCardgroup", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	//@ApiOperation(value = "Add P2P card group", response = PretupsResponse.class)

	public PretupsResponse<JsonNode> addCardGroup(
			@Parameter(description = SwaggerAPIDescriptionI.ADD_P2P_CARD_GROUP)
			@RequestBody String requestData)
			throws IOException, SQLException, BTSLBaseException, ParseException {
		final String methodName =  "addCardGroup";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
        MComConnectionI mcomCon = null;
        PretupsResponse<JsonNode> response = new PretupsResponse<>();
        final Date currentDate = new Date();
        try {
			mcomCon = new MComConnection();
            con=mcomCon.getConnection();
			response = new PretupsResponse<JsonNode>();
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
			
			
			
			String categories = PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.CARD_GROUP_ALLOWED_CATEGORIES);//SystemPreferences.CARD_GROUP_ALLOWED_CATEGORIES;
			 String[] allowedCategories = categories.split(",");
			PretupsRestUtil.validateLoggedInUser(requestNode, con, response, allowedCategories);
			if (response.hasFormError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setMessageCode("user.unauthorized");
				return response;
			}
			
			JsonNode dataNode =  requestNode.get("data");
			final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();

			CardGroupSetVO cardGroupSetVO = objectMapper.readValue(dataNode.get("cardGroupDetails").toString(), CardGroupSetVO.class);
			
            cardGroupSetVO.setCardGroupSetID(String.valueOf(IDGenerator.getNextID(PretupsI.CARD_GROUP_SET_ID, TypesI.ALL)));
            cardGroupSetVO.setCreatedOn(currentDate);
            cardGroupSetVO.setModifiedOn(currentDate);
           // set the default value of the version initially its value is 1
            cardGroupSetVO.setLastVersion("1");
            cardGroupSetVO.setVersion("1");
            
            if(!dataNode.has("cardGroupList") || dataNode.size() == 0){
            	String msg;
            	if(PretupsI.VOUCHER_CONS_SERVICE.equals(cardGroupSetVO.getServiceType()))
 					msg="cardgroup.voucher.error.cardgroupset.required";
            	else
            		msg="cardgroup.cp2p.error.cardgroupset.required";
            	throw new BTSLBaseException(this, methodName, msg);
            }
            JsonNode cardGroupSetListNode =  dataNode.get("cardGroupList");
            getReversalModifiedDate(cardGroupSetListNode);
           
            final  List<CardGroupDetailsVO> cardGroupList = Arrays.asList(objectMapper.readValue(cardGroupSetListNode.toString(), CardGroupDetailsVO[].class));
          //load list data
            this.loadData(con, cardGroupSetVO);
            
          //check for applicable from date
           
                Date currDate=cardGroupSetVO.getCreatedOn();
                String applicableFrmDate=cardGroupSetVO.getApplicableFromDate();
                Date applicableDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
                sdf.setLenient(false); // this is required else it will convert
                
                applicableDate = sdf.parse(applicableFrmDate+" 23:59:59");
                if (BTSLUtil.getDifferenceInUtilDates(applicableDate, currDate) > 0) 
    			{	throw new BTSLBaseException(this, methodName, "cardgroup.addcardgroup.error.lesserapplicablefromdate");
    			}
                
                
               
          
            //do validations check
           ActionErrors errors =  this.validation(cardGroupSetVO, cardGroupSetListNode,objectMapper,con);
            if(errors != null && !errors.isEmpty())
            {
            	Iterator<ActionMessage> iterator = errors.get();
            	ActionMessage error = iterator.next();
				throw new BTSLBaseException(this, methodName,error.getKey());
            }
			if(cardGroupSetVO != null) {
				// check whether the Card Group Name is already exist or not
				if (cardGroupSetDAO.isCardGroupSetNameExist(con, cardGroupSetVO.getNetworkCode(), cardGroupSetVO.getCardGroupSetName(), null)) {
					throw new BTSLBaseException(this, methodName, "cardgroup.error.cardgroupnamealreadyexist");
				}
	            
				// insert Card_Group_Set
                final int insertSetCount = cardGroupSetDAO.addCardGroupSet(con, cardGroupSetVO);

                if (insertSetCount <= 0) {
                    try {
                        mcomCon.finalRollback();
                    } catch (Exception e) {
                    	log.errorTrace(methodName, e);
                    }
                    log.error(methodName, "Error: while Inserting Card_Group_Set");
                    throw new BTSLBaseException(this,methodName, "error.general.processing");
                }
				
              this.add(cardGroupSetVO, currentDate, con, cardGroupList, cardGroupSetListNode, objectMapper);
              mcomCon.finalCommit();
              // if above method execute successfully commit the
              // transaction
              //Prepare Response
              response.setResponse(PretupsI.RESPONSE_SUCCESS,true,"cardgroup.cardgroupdetailsview.successaddmessage");
			
		}
        }catch (BTSLBaseException e) {
        		response.setStatus(false);
        	 	response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(e.getMessage());
				response.setMessageKey(e.getMessage());
		}
		finally {
         try {
         	if (mcomCon != null) {
 				mcomCon.close("addCardGroup#" + methodName);
 				mcomCon = null;
 			}
         } catch (Exception e) {
        	 log.error(methodName, "Exception:e=" + e);
         }
         if (log.isDebugEnabled()) {
             log.debug(methodName, " Exited ");
         }
     }
        
		return response;
	}

	
	public void add(CardGroupSetVO cardGroupSetVO,Date currentDate,Connection con,List<CardGroupDetailsVO> cardGroupList, JsonNode cardGroupSetListNode,ObjectMapper objectMapper) throws BTSLBaseException, JsonParseException, JsonMappingException, IOException{
		 // insert Card Group Version and Card Group Details
		String methodName = "add";
        final CardGroupSetVersionVO cardGroupVersionVO = new CardGroupSetVersionVO();
        final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
        final CardGroupDAO cardDAO = new CardGroupDAO();
        final String format = Constants.getProperty("CARDGROUP_DATE_FORMAT");
        String fromHour = null;
        if (BTSLUtil.isNullString(cardGroupSetVO.getApplicableFromHour())) {
            fromHour = "00:00";
        } else {
            fromHour = cardGroupSetVO.getApplicableFromHour();
        }
     // populating CardGroupsetVersionVO from the from Parameters
        try{
        	final Date newDate = BTSLUtil.getDateFromDateString(cardGroupSetVO.getApplicableFromDate() + " " + fromHour, format);
        	cardGroupVersionVO.setApplicableFrom(newDate);
        }catch (Exception e) {
			throw new BTSLBaseException(this, "add", "promotrfrule.addpromoc2stransferrules.error.invalidformat");

		}
        cardGroupVersionVO.setCardGroupSetID(cardGroupSetVO.getCardGroupSetID());
        cardGroupVersionVO.setCreatedBy(cardGroupSetVO.getCreatedBy());
        cardGroupVersionVO.setCreadtedOn(currentDate);
        cardGroupVersionVO.setModifiedBy(cardGroupSetVO.getModifiedBy());
        cardGroupVersionVO.setModifiedOn(currentDate);
        cardGroupVersionVO.setVersion(cardGroupSetVO.getVersion());
        
        // insert Card_Group_Set_Version
        final int insertVersionCount = cardGroupSetDAO.addCardGroupSetVersion(con, cardGroupVersionVO);

        if (insertVersionCount <= 0) {
            try {
                con.rollback();
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error("add", "Error: while Inserting Card_Group_Set_Versions");
            throw new BTSLBaseException(this, "add", "error.general.processing");
        }
        
        
        // set the default values in the detail VO
        if (cardGroupList != null && !cardGroupList.isEmpty()) {
            // set the default values
            for (int i = 0, j = cardGroupList.size(); i < j; i++) {
            	final CardGroupDetailsVO cardDetailVO = (CardGroupDetailsVO) cardGroupList.get(i);
                cardDetailVO.setCardGroupSetID(cardGroupSetVO.getCardGroupSetID());
                cardDetailVO.setCardGroupID(String.valueOf(IDGenerator.getNextID(PretupsI.CARD_GROUP_ID, TypesI.ALL)));
                cardDetailVO.setVersion(cardGroupVersionVO.getVersion());
                if (!BTSLUtil.isNullString(cardDetailVO.getValidityPeriodTypeDesc()))
                {
    			String validityPeriod = BTSLUtil.getValidityPeriodType(cardGroupSetVO.getValidityTypeList(), cardDetailVO.getValidityPeriodTypeDesc());
    			if (BTSLUtil.isNullString(validityPeriod)){
    				throw new BTSLBaseException(this, "add", "cardgroup.cardgroupdetails.err.msg.novoucherperiodtypefound");
    	        }
    			cardDetailVO.setValidityPeriodType(validityPeriod);
                }
                if(PretupsI.VOUCHER_CONS_SERVICE.equals(cardGroupSetVO.getServiceType()))
                	{
                	cardDetailVO.setCardGroupType(PretupsI.CARD_GROUP_VMS);
                	try{
                			this.loadVoucherData(con, cardGroupSetVO, cardDetailVO);
                		}catch (BTSLBaseException e) {
                			throw new BTSLBaseException(this, "add", e.getMessage());
                		}
                	}
                else
                	cardDetailVO.setCardGroupType(PretupsI.CARD_GROUP_P2P);
                if(cardDetailVO.getBonusAccList() != null && !cardDetailVO.getBonusAccList().isEmpty()){
                	BonusAccountDetailsVO bonAccDetailsVO = null;
                	cardDetailVO.setBonusAccList(new ArrayList<>(Arrays.asList(objectMapper.readValue(cardGroupSetListNode.get(i).get("bonusAccList").toString(), BonusAccountDetailsVO[].class))));
                	BonusBundleDAO bonusBundleDAO = new BonusBundleDAO();
                	ArrayList<BonusBundleDetailVO> bonusBundleList = bonusBundleDAO.loadBonusBundles(con);
                	for(int k =0; k<cardDetailVO.getBonusAccList().size();k++)
                	{
                		bonAccDetailsVO= (BonusAccountDetailsVO)cardDetailVO.getBonusAccList().get(k);
                		for( BonusBundleDetailVO bonusBundleDetailVO:bonusBundleList){
                			if (bonusBundleDetailVO.getBundleName().equalsIgnoreCase(bonAccDetailsVO.getBonusName())) {
                				bonAccDetailsVO.setBundleID(bonusBundleDetailVO.getBundleID());
                				break;
                			}
                		}
                	}
                }
            }
            // insert Card_Group_Details
        final int insertDetailCount = cardDAO.addCardGroupDetails(con, cardGroupList);

        if (insertDetailCount <= 0) {
            try {
                con.rollback();
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error("add", "Error: while Inserting Card_Group_Details");
            	throw new BTSLBaseException(this, "add", "error.general.processing");
        	}
        }
	}
	public void loadData(Connection con,CardGroupSetVO cardGroupSetVO) throws BTSLBaseException{
		CardGroupDAO cardDAO = new CardGroupDAO();
		  try{
			  ArrayList<ListValueVO> amountTypeList = LookupsCache.loadLookupDropDown(PretupsI.AMOUNT_TYPE, true);
		  ArrayList<ListValueVO> validityTypeList = LookupsCache.loadLookupDropDown(PretupsI.VALIDITY_TYPE, true);
		cardGroupSetVO.setAmountTypeList(amountTypeList);
    	cardGroupSetVO.setValidityTypeList(validityTypeList);
		cardGroupSetVO.setServiceType(cardDAO.loadServiceType(con, cardGroupSetVO.getNetworkCode(), cardGroupSetVO.getModuleCode(), cardGroupSetVO.getServiceTypeDesc()));
		ArrayList<ServiceSelectorMappingVO> subServiceTypeList = ServiceSelectorMappingCache.getSelectorListForServiceType(cardGroupSetVO.getServiceType());
		cardGroupSetVO.setSubServiceTypeList(subServiceTypeList);
		for( ServiceSelectorMappingVO serviceSelectorMappingVO:subServiceTypeList){
			if (serviceSelectorMappingVO.getSelectorName().equalsIgnoreCase(cardGroupSetVO.getSubServiceTypeDescription())) {
				cardGroupSetVO.setSubServiceType(serviceSelectorMappingVO.getSelectorCode());
				break;
			}
		}
		ArrayList<ListValueVO> setTypeList =  LookupsCache.loadLookupDropDown(PretupsI.CARD_GROUP_SET_TYPE, true);
		for (ListValueVO listValueVO :setTypeList) {
			if(listValueVO.getLabel().equals(cardGroupSetVO.getSetTypeName())){
				cardGroupSetVO.setSetType(listValueVO.getValue());
				break;
			}
		}
		  }catch (Exception e) {
			log.error("load", "Exceptin:e=" + e);
			log.errorTrace("load", e);
		}
	}
	
	public void loadVoucherData(Connection con,CardGroupSetVO cardGroupSetVO,CardGroupDetailsVO cardDetailVO) throws BTSLBaseException{
		String methodName = "loadVoucherData";
		VomsProductDAO vomsProductDAO = new VomsProductDAO();
			   
			ArrayList<VoucherTypeVO> voucherTypeList = vomsProductDAO.loadVoucherDetails(con);
			String voucherType = BTSLUtil.getVoucherType(voucherTypeList, cardDetailVO.getVoucherTypeDesc());
			if (BTSLUtil.isNullString(voucherType)){
		            throw new BTSLBaseException(this, methodName, "cardgroup.cardgroupdetails.err.msg.novouchertypefound");
		        }
	    	cardDetailVO.setVoucherType(voucherType);
	    	
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
	
	
	
	@PostMapping(value = "/modifyP2PCardgroup", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	//@ApiOperation(value = "Modify P2P card group", response = PretupsResponse.class)
	public PretupsResponse<JsonNode> modifyCardGroup(
			@Parameter(description = SwaggerAPIDescriptionI.MODIFY_P2P_CARD_GROUP)
			@RequestBody String requestData)
			throws Exception {
		final String methodName =  "modifyCardGroup";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
        MComConnectionI mcomCon = null;
        PretupsResponse<JsonNode> response = new PretupsResponse<>();
        final Date currentDate = new Date();
        try {
			mcomCon = new MComConnection();
            con=mcomCon.getConnection();
			response = new PretupsResponse<JsonNode>();
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
			
			String categories = SystemPreferences.CARD_GROUP_ALLOWED_CATEGORIES;
			String[] allowedCategories = categories.split(",");
			PretupsRestUtil.validateLoggedInUser(requestNode, con, response, allowedCategories);
			if (response.hasFormError()) {
				response.setStatus(false);
				response.setMessageCode("user.unauthorized");
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			JsonNode dataNode =  requestNode.get("data");
			
			final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
			 final CardGroupDAO cardDAO = new CardGroupDAO();
	         final CardGroupSetVersionDAO cardGroupSetVersionDAO = new CardGroupSetVersionDAO();
	         CardGroupSetVO cardGroupVO = objectMapper.readValue(dataNode.get("cardGroupDetails").toString(), CardGroupSetVO.class);
	         this.loadData(con, cardGroupVO);
	         
	         //get card_group_set_id
	        // cardGroupVO.setCardGroupSetID(cardDAO.loadCardgroupSetID(con,cardGroupVO.getCardGroupID(), cardGroupVO.getVersion()));
				ArrayList<CardGroupSetVO> cardGroupSetNameList= cardDAO.loadCardGroupSet(con, cardGroupVO.getNetworkCode(), PretupsI.P2P_MODULE);
				
				 CardGroupSetVO setVO = null;
	              if (cardGroupSetNameList != null) {
	            	// get the selected card group set from the CardGroupSetNameList
	                  for (int i = 0, j = cardGroupSetNameList.size(); i < j; i++) {
	                      setVO = cardGroupSetNameList.get(i);
	                      if (cardGroupVO.getServiceType().equals(setVO.getServiceType()) && cardGroupVO.getSubServiceType().equals(setVO.getSubServiceType())&& cardGroupVO.getCardGroupSetID().equals(setVO.getCardGroupSetID())) {
	                          break;
	                      }
	                  }
	              }
                  
	              final CardGroupSetVO cardGroupSetVO = setVO;
	              //set API data in VO
	              	cardGroupSetVO.setAmountTypeList(cardGroupVO.getAmountTypeList());
	          		cardGroupSetVO.setValidityTypeList(cardGroupVO.getValidityTypeList());
	          		cardGroupSetVO.setSubServiceTypeList(cardGroupVO.getSubServiceTypeList());
	          		cardGroupSetVO.setSetTypeList(cardGroupVO.getSetTypeList());
	          		cardGroupSetVO.setModifiedBy(cardGroupVO.getModifiedBy());
	          		cardGroupSetVO.setCardGroupSetName(cardGroupVO.getCardGroupSetName());
	          		cardGroupSetVO.setVersion(cardGroupVO.getVersion());
	          		
	              final ArrayList<CardGroupSetVersionVO> cardGroupSetVersionList = cardGroupSetDAO.loadCardGroupSetVersion(con,cardGroupSetVO.getNetworkCode(), currentDate, PretupsI.P2P_MODULE);
	              
	              // set the Card Group Set Version Info
	              CardGroupSetVersionVO setVersionVO = null;
	              for (int i = 0, j = cardGroupSetVersionList.size(); i < j; i++) {
	                  setVersionVO = (CardGroupSetVersionVO) cardGroupSetVersionList.get(i);
	                  // get the selected version info from the versionList
	                  if (cardGroupSetVO.getCardGroupSetID().equals(setVersionVO.getCardGroupSetID()) && cardGroupSetVO.getVersion().equals(setVersionVO.getVersion())) {
	                	  cardGroupSetVO.setApplicableFromDate(cardGroupVO.getApplicableFromDate());
	                	  cardGroupSetVO.setApplicableFromHour(cardGroupVO.getApplicableFromHour());
	                       int hour = BTSLUtil.getHour(setVersionVO.getApplicableFrom());
	                       int minute = BTSLUtil.getMinute(setVersionVO.getApplicableFrom());
	                       String time = BTSLUtil.getTimeinHHMM(hour, minute);
	                      cardGroupSetVO.setOldApplicableFromDate(BTSLUtil.getDateStringFromDate(setVersionVO.getApplicableFrom()));
	                      cardGroupSetVO.setOldApplicableFromHour(time);
	                      break;
	                  }
	              }
	              
	              
	              if(BTSLUtil.isNullString(cardGroupSetVO.getOldApplicableFromDate())){
	            	  throw new BTSLBaseException(this, methodName,"cardgroup.error.cardgroupversionnotexist");
	              }
	              final CardGroupDAO cardGroupDAO = new CardGroupDAO();
	              // load the Card Group Details info
	              JsonNode cardGroupSetListNode =  dataNode.get("cardGroupList");
	              getReversalModifiedDate(cardGroupSetListNode);
	              List<CardGroupDetailsVO> cardGroupList=Arrays.asList(objectMapper.readValue(cardGroupSetListNode.toString(), CardGroupDetailsVO[].class));
	            //load list data
	              this.loadData(con, cardGroupSetVO);
	              //do validations check
	             ActionErrors errors =  this.validation(cardGroupSetVO, cardGroupSetListNode,objectMapper,con);
	             if(errors != null && !errors.isEmpty())
	             {
	             	Iterator<ActionMessage> iterator = errors.get();
	             	ActionMessage error = iterator.next();
	 				throw new BTSLBaseException(this, methodName,error.getKey());
	             }
	            // if cardgroup name is changed check whether the Card Group Name is already exist or not
                if (cardGroupSetDAO.isCardGroupSetNameExist(con,cardGroupSetVO.getNetworkCode(), cardGroupSetVO.getCardGroupSetName(), cardGroupSetVO.getCardGroupSetID())) {
                    throw new BTSLBaseException(this, methodName, "cardgroup.error.cardgroupnamealreadyexist", "modifyCardGroup");
                }

                final String format = Constants.getProperty("CARDGROUP_DATE_FORMAT");
                String fromHour = null;
                if (BTSLUtil.isNullString(cardGroupSetVO.getApplicableFromHour())) {
                    fromHour = "00:00";
                } else {
                    fromHour = cardGroupSetVO.getApplicableFromHour();
                }
                final Date newDate = BTSLUtil.getDateFromDateString(cardGroupSetVO.getApplicableFromDate() + " " + fromHour, format);
                // check whether the Card Group Set is already exist with
                // the same applicable date of the same set id
                if (cardGroupSetVersionDAO.isCardGroupAlreadyExist(con, newDate, cardGroupSetVO.getCardGroupSetID(), cardGroupSetVO.getVersion())) {
                    final String[] arr = { BTSLUtil.getDateTimeStringFromDate(newDate) };
                    throw new BTSLBaseException(this, methodName, "cardgroup.error.cardgroupalreadyexist", 0, arr, "DetailView");
                }
                final Date oldDate = BTSLUtil.getDateFromDateString(cardGroupSetVO.getOldApplicableFromDate() + " " + cardGroupSetVO.getOldApplicableFromHour(), format);
                /*
                 * if the below conditon is true means user change the
                 * applicable from date and we
                 * need to insert a new version
                 */
                int version = 0;
                if (oldDate.getTime() != newDate.getTime())// we need to
                    // insert the new
                    // version
                    {
                        version = Integer.parseInt(cardGroupSetVO.getLastVersion()) + 1;
                        cardGroupSetVO.setLastVersion(String.valueOf(version));
                    } else// we need to update the same version
                    {
                        version = Integer.parseInt(cardGroupSetVO.getVersion());
                    }

                cardGroupSetVO.setVersion(String.valueOf(version));
                cardGroupSetVO.setModifiedOn(currentDate);
                
                int updateCount = cardGroupSetDAO.updateCardGroupSet(con, cardGroupSetVO);
                if (updateCount <= 0) {
                    try {
                        mcomCon.finalRollback();
                    } catch (Exception e) {
                        log.errorTrace(methodName, e);
                    }
                    log.error(methodName, "Error: while Updating Card_Group_Set");
                    throw new BTSLBaseException(this, methodName, "error.general.processing");
                }
               
                if (oldDate.getTime() != newDate.getTime())// we need to
                {
                	// insert the new version
                   this.add(cardGroupSetVO, currentDate, con, cardGroupList, cardGroupSetListNode, objectMapper);
                } else
                {
                	// update the card_group_set_version
                	this.updateSetVersion(con, cardGroupSetVersionDAO, setVersionVO, currentDate,cardGroupSetVO.getModifiedBy());
                	// delete data from the card_group_details
                    int deleteCount = cardGroupSetVersionDAO.deleteCardGroupDetails(con, cardGroupSetVO.getCardGroupSetID(), cardGroupSetVO.getVersion());
                    if (deleteCount < 0) {
                        try {
                            mcomCon.finalRollback();
                        } catch (Exception e) {
                            log.errorTrace(methodName, e);
                        }
                        log.error(methodName, "Error: while Deleting Card_Group_Details");
                        throw new BTSLBaseException(this, methodName, "error.general.processing");
                    }
                    
                    try {
                        deleteCount = 0;
                        final BonusBundleDAO bonusBundleDAO = new BonusBundleDAO();
                        deleteCount = bonusBundleDAO.deletePreviousBonus(con, cardGroupSetVO.getCardGroupSetID(), cardGroupSetVO.getVersion());

                    } catch (Exception sqe) {
                        // Create New message and paste here
                        try {
                            mcomCon.finalRollback();
                        } catch (Exception e) {
                            log.errorTrace(methodName, e);
                        }
                        log.error(methodName, "Error: while Deleting Card_Group_Details");
                        log.errorTrace(methodName, sqe);
                        throw new BTSLBaseException(this, methodName, "error.general.processing");
                    }
                    if (deleteCount <= 0) {
                        // Create New message and paste here
                        try {
                            mcomCon.finalRollback();
                        } catch (Exception e) {
                            log.errorTrace(methodName, e);
                        }
                        log.error(methodName, "Error: while Deleting Card_Group_Details");
                        throw new BTSLBaseException(this, methodName, "error.general.processing");
                    }
                    
                    
                    // set the default valus in the detail VO
                    if (cardGroupList != null && !cardGroupList.isEmpty()) {
                        // set the default values
                        for (int i = 0, j = cardGroupList.size(); i < j; i++) {
                        	final CardGroupDetailsVO cardDetailVO = (CardGroupDetailsVO) cardGroupList.get(i);
                            cardDetailVO.setCardGroupSetID(cardGroupSetVO.getCardGroupSetID());
                            cardDetailVO.setCardGroupID(String.valueOf(IDGenerator.getNextID(PretupsI.CARD_GROUP_ID, TypesI.ALL)));
                            cardDetailVO.setVersion(setVersionVO.getVersion());
                            if (!BTSLUtil.isNullString(cardDetailVO.getValidityPeriodTypeDesc()))
                            {
                			String validityPeriod = BTSLUtil.getValidityPeriodType(cardGroupSetVO.getValidityTypeList(), cardDetailVO.getValidityPeriodTypeDesc());
                			if (BTSLUtil.isNullString(validityPeriod)){
                				throw new BTSLBaseException(this, "add", "cardgroup.cardgroupdetails.err.msg.novoucherperiodtypefound");
                	        }
                			cardDetailVO.setValidityPeriodType(validityPeriod);
                            }
                            if(PretupsI.VOUCHER_CONS_SERVICE.equals(cardGroupSetVO.getServiceType()))
                            	{
                            	cardDetailVO.setCardGroupType(PretupsI.CARD_GROUP_VMS);
                            	try{
                            			this.loadVoucherData(con, cardGroupSetVO, cardDetailVO);
                            		}catch (BTSLBaseException e) {
                            			throw new BTSLBaseException(this, "add", e.getMessage());
                            		}
                            	}
                            else
                            	cardDetailVO.setCardGroupType(PretupsI.CARD_GROUP_P2P);
                            if(cardDetailVO.getBonusAccList() != null && !cardDetailVO.getBonusAccList().isEmpty()){
                            	BonusAccountDetailsVO bonAccDetailsVO = null;
                            	cardDetailVO.setBonusAccList(new ArrayList<>(Arrays.asList(objectMapper.readValue(cardGroupSetListNode.get(i).get("bonusAccList").toString(), BonusAccountDetailsVO[].class))));
                            	BonusBundleDAO bonusBundleDAO = new BonusBundleDAO();
                            	ArrayList<BonusBundleDetailVO> bonusBundleList = bonusBundleDAO.loadBonusBundles(con);
                            	for(int k =0; k<cardDetailVO.getBonusAccList().size();k++)
                            	{
                            		bonAccDetailsVO= (BonusAccountDetailsVO)cardDetailVO.getBonusAccList().get(k);
                            		for( BonusBundleDetailVO bonusBundleDetailVO:bonusBundleList){
                            			if (bonusBundleDetailVO.getBundleName().equalsIgnoreCase(bonAccDetailsVO.getBonusName())) {
                            				bonAccDetailsVO.setBundleID(bonusBundleDetailVO.getBundleID());
                            				break;
                            			}
                            		}
                            	}
                            }
                        }
                        // insert Card_Group_Details
                        final int insertDetailCount = cardDAO.addCardGroupDetails(con, cardGroupList);

                        if (insertDetailCount <= 0) {
                            try {
                                mcomCon.finalRollback();
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            log.error(methodName, "Error: while Inserting Card_Group_Details");
                            throw new BTSLBaseException(this, methodName, "error.general.processing");
                        }
                    }
                }
                mcomCon.finalCommit();
                // if above method execute successfully commit the
                // transaction
                //Prepare Response
                response.setResponse(PretupsI.RESPONSE_SUCCESS,true,"cardgroup.cardgroupdetailsview.successeditmessage");
        }catch (BTSLBaseException e) {
        	log.error(methodName, "Exceptin:e=" + e);
    		response.setStatus(false);
    	 	response.setStatusCode(PretupsI.RESPONSE_FAIL);
			response.setMessageCode(e.getMessage());
			response.setMessageKey(e.getMessage());
        }finally {
        	try {
        		if (mcomCon != null) {
        			mcomCon.close("modifyCardGroup#" + methodName);
        			mcomCon = null;
        		}
        	} catch (Exception e) {
        		log.error(methodName, "Exception:e=" + e);
        	}
        	if (log.isDebugEnabled()) {
        		log.debug(methodName, " Exited ");
        	}
        }
     
		return response;
	}
	
	
	public ActionErrors validation(CardGroupSetVO cardGroupSetVO, JsonNode cardGroupSetListNode, ObjectMapper objectMapper, Connection con) throws BTSLBaseException{

        final String METHOD_NAME = "validate";
        ActionErrors errors = null;
       
         String noCheckRequiredFor = Constants.getProperty("P2P_OTHER_CONFIG_SERVICES");
         String slabCount = null;
         int count = 0;
        try {	
        	errors = new ActionErrors();
        	final  ArrayList cardGroupList = new ArrayList<>(Arrays.asList(objectMapper.readValue(cardGroupSetListNode.toString(), CardGroupDetailsVO[].class)));
            for (int k = 0, j = cardGroupList.size(); k < j; k++) {
            	CardGroupDetailsVO cardGroupDetailsVO =  (CardGroupDetailsVO)cardGroupList.get(k);
                double startRange = 0;
                double endRange = 0;
                String value = null;
                count++;
                slabCount = String.valueOf(count);
                if(PretupsI.VOUCHER_CONS_SERVICE.equals(cardGroupSetVO.getServiceType())) {
                	startRange = Double.parseDouble(cardGroupDetailsVO.getVoucherDenomination());
                	endRange = Double.parseDouble(cardGroupDetailsVO.getVoucherDenomination());
                }
                else{
	                try {
	                    startRange = Double.parseDouble(cardGroupDetailsVO.getStartRangeAsString());
	                    if (startRange < 0) {
	                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidstartrange"));
	                    } else if (startRange > Double.parseDouble(PretupsI.MAX_LONG_VALUE)) {
	                        value = PretupsI.MAX_LONG_VALUE;
	                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.greaterstartrange", value));
	                    }
	                } catch (Exception e) {
	                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidstartrange"));
	                }
	                
	                
                // check for end range
	                try {
	                	endRange = Double.parseDouble(cardGroupDetailsVO.getEndRangeAsString());
	                    if (endRange > Double.parseDouble(PretupsI.MAX_LONG_VALUE)) {
	                        value = PretupsI.MAX_LONG_VALUE;
	                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.greaterendrange", value));
	                    } else if (startRange > endRange) {
	                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidendrange"));
	                    }
	                } catch (Exception e) {
	                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidendrange"));
	                }
                }
                //check for cardgroupCode
                try {
                    if (BTSLUtil.isNullString(cardGroupDetailsVO.getCardGroupCode())) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.blankcardgroupcode"));
                        return errors;
                    }
                } catch (Exception e) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.blankcardgroupcode"));
                   
                }
                
              //check for cardgroupset name
                try {
                	if(BTSLUtil.isNullString(cardGroupSetVO.getCardGroupSetName())){
                		if(PretupsI.CARD_GROUP_VMS.equals(cardGroupDetailsVO.getCardGroupType())){
                			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.voucherversionlist.label.cardgroupsetname.required.error"));
                		}else{
                			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.p2pversionlist.label.cardgroupsetname.required.error"));
                		}
                	}
                } catch (Exception e) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.voucherversionlist.label.cardgroupsetname.required.error"));
                   
                }

                
                // check for validity days should be greater than equal to 0
                try {
                    if (Long.parseLong(cardGroupDetailsVO.getValidityPeriodAsString()) < 0) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidvalidityperiodrange"));
                    }
                } catch (Exception e) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidvalidityperiodrange"));
                }

                // check for grace period should be greater than equal to 0
                try {
                    if (cardGroupDetailsVO.getGracePeriod() < 0) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidgraceperiodrange"));
                    }
                } catch (Exception e) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidgraceperiodrange"));
                }

                // check for multiple off should be greater than 0
                if(!PretupsI.VOUCHER_CONS_SERVICE.equals(cardGroupSetVO.getServiceType())) {
                try {
                    if (Double.parseDouble(cardGroupDetailsVO.getMultipleOfAsString()) <= 0) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidmultipleofrange"));
                    }
                    if (BTSLUtil.floatEqualityCheck(startRange % Double.parseDouble(cardGroupDetailsVO.getMultipleOfAsString()), 0d, "!=")) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidstartormultiple",startRange,cardGroupDetailsVO.getMultipleOf()));
                    }
                } catch (Exception e) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidmultipleofrange"));
                }
                }
                value = null;
                // check for sender tax1 rate
                if(!PretupsI.VOUCHER_CONS_SERVICE.equals(cardGroupSetVO.getServiceType())) {
	                try {
	                    final double tax1Rate = Double.parseDouble(cardGroupDetailsVO.getSenderTax1RateAsString());
	                    value = "100";
	                    if(!BTSLUtil.isStringContain(noCheckRequiredFor , cardGroupDetailsVO.getServiceTypeId())){
		                    if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(cardGroupDetailsVO.getSenderTax1Type())) {
		                        if (tax1Rate < 0 || tax1Rate > 100) {
		                            value = "100";
		                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidsendertax1rate", value));
		                        }
		                    } else {
		                        if (tax1Rate < 0 || tax1Rate > startRange) {
		                            value = String.valueOf(startRange) + " (Start Range)";
		                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidsendertax1rate", value));
		                        }
		                    }
	                    }
	                } catch (Exception e) {
	                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidsendertax1rate", value));
	                }

                // check for sender tax2 rate
	                try {
	                    final double tax2Rate = Double.parseDouble(cardGroupDetailsVO.getSenderTax2RateAsString());
	                    value = "100";
						if(!BTSLUtil.isStringContain(noCheckRequiredFor ,  cardGroupDetailsVO.getServiceTypeId())){
	                         if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(cardGroupDetailsVO.getSenderTax2Type())) {
	                        if (tax2Rate < 0 || tax2Rate > 100) {
	                            value = "100";
	                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidsendertax2rate", value));
	                        }
	                    } else {
	                        if (tax2Rate < 0 || tax2Rate > startRange) {
	                            value = String.valueOf(startRange) + " (Start Range)";
	                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidsendertax2rate", value));
	                        }
	                    }
					}
	                } catch (Exception e) {
	                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidsendertax2rate", value));
	                }

	                // check for sender access fee
	                try {
	                    final double accessFeeRate = Double.parseDouble(cardGroupDetailsVO.getSenderAccessFeeRateAsString());
	                    value = "100";
	                    if(!BTSLUtil.isStringContain(noCheckRequiredFor , cardGroupDetailsVO.getServiceTypeId())){
		                    if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(cardGroupDetailsVO.getSenderAccessFeeType())) {
		                        if (accessFeeRate < 0 || accessFeeRate > 100) {
		                            value = "100";
		                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidsenderaccessfeerate", value));
		                        }
		                    } else {
		                        if (accessFeeRate < 0 || accessFeeRate > startRange) {
		                            value = String.valueOf(startRange) + " (Start Range)";
		                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidsenderaccessfeerate", value));
		                        }
		                    }
	                    }
	                } catch (Exception e) {
	                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidsenderaccessfeerate", value));
	                }
	
	                // check for max sender access fee
	                try {
	                    if (Double.parseDouble(cardGroupDetailsVO.getMinSenderAccessFeeAsString()) > Double.parseDouble(cardGroupDetailsVO.getMaxSenderAccessFeeAsString())) {
	                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidmaxsenderaccessfee", cardGroupDetailsVO.getEndRangeAsString()));
	                    } else if (!BTSLUtil.isNullString(cardGroupDetailsVO.getEndRangeAsString()) && BTSLUtil.isDecimalValue(cardGroupDetailsVO.getEndRangeAsString())) {
	                    if(!BTSLUtil.isStringContain(noCheckRequiredFor , cardGroupDetailsVO.getServiceTypeId()) && Double.parseDouble(cardGroupDetailsVO.getMaxSenderAccessFeeAsString())>endRange){
	                        errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("cardgroup.addcardgroup.error.invalidmaxsenderaccessfee",cardGroupDetailsVO.getEndRangeAsString()));
	                        }
	                    }
	                } catch (Exception e) {
	                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidmaxsenderaccessfee", cardGroupDetailsVO.getEndRangeAsString()));
	                }
                }

                // check for receiver tax1 rate
                try {
                    final double tax1Rate = Double.parseDouble(cardGroupDetailsVO.getReceiverTax1RateAsString());
                    value = "100";
                    if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(cardGroupDetailsVO.getReceiverTax1Type())) {
                        if (tax1Rate < 0 || tax1Rate > 100) {
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidreceivertax1rate", value));
                        }
                    } else {
                        if (tax1Rate < 0 || tax1Rate > startRange) {
                            value = String.valueOf(startRange) + " (Start Range)";
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidreceivertax1rate", value));
                        }
                    }
                } catch (Exception e) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidreceivertax1rate", value));
                }

                // check for receiver tax2 rate
                try {
                    final double tax2Rate = Double.parseDouble(cardGroupDetailsVO.getReceiverTax2RateAsString());
                    value = "100";
                    if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(cardGroupDetailsVO.getReceiverTax2Type())) {
                        if (tax2Rate < 0 || tax2Rate > 100) {
                            value = "100";
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidreceivertax2rate", value));
                        }
                    } else {
                        if (tax2Rate < 0 || tax2Rate > startRange) {
                            value = String.valueOf(startRange) + " (Start Range)";
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidreceivertax2rate", value));
                        }
                    }
                } catch (Exception e) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidreceivertax2rate", value));
                }

                // check for receiver access fee
                try {
                    final double accessFeeRate = Double.parseDouble(cardGroupDetailsVO.getReceiverAccessFeeRateAsString());
                    value = "100";
                    if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(cardGroupDetailsVO.getReceiverAccessFeeType())) {
                        if (accessFeeRate < 0 || accessFeeRate > 100) {
                            value = "100";
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidreceiveraccessfeerate", value));
                        }
                    } else {
                        if (accessFeeRate < 0 || accessFeeRate > startRange) {
                            value = String.valueOf(startRange) + " (Start Range)";
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidreceiveraccessfeerate", value));
                        }
                    }
                } catch (Exception e) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidreceiveraccessfeerate", value));
                }

                // check for max receiver access fee
                if(PretupsI.VOUCHER_CONS_SERVICE.equals(cardGroupSetVO.getServiceType())) {
                try {
                    if (Double.parseDouble(cardGroupDetailsVO.getMinReceiverAccessFeeAsString()) > Double.parseDouble(cardGroupDetailsVO.getMaxReceiverAccessFeeAsString())) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.voucher.error.invalidmaxreceiveraccessfee", cardGroupDetailsVO.getEndRangeAsString()));
                    } else if (!BTSLUtil.isNullString(cardGroupDetailsVO.getEndRangeAsString()) && BTSLUtil.isDecimalValue(cardGroupDetailsVO.getEndRangeAsString())) {
                        if (Double.parseDouble(cardGroupDetailsVO.getMaxReceiverAccessFeeAsString()) > endRange) {
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.voucher.error.invalidmaxreceiveraccessfee", cardGroupDetailsVO.getEndRangeAsString()));
                        }
                    }
                } catch (Exception e) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.voucher.error.invalidmaxreceiveraccessfee", cardGroupDetailsVO.getEndRangeAsString()));
                }
                }
                else{
                    try {
                        if (Double.parseDouble(cardGroupDetailsVO.getMinReceiverAccessFeeAsString()) > Double.parseDouble(cardGroupDetailsVO.getMaxReceiverAccessFeeAsString())) {
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidmaxreceiveraccessfee", cardGroupDetailsVO.getEndRangeAsString()));
                        } else if (!BTSLUtil.isNullString(cardGroupDetailsVO.getEndRangeAsString()) && BTSLUtil.isDecimalValue(cardGroupDetailsVO.getEndRangeAsString())) {
                            if (Double.parseDouble(cardGroupDetailsVO.getMaxReceiverAccessFeeAsString()) > endRange) {
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidmaxreceiveraccessfee", cardGroupDetailsVO.getEndRangeAsString()));
                            }
                        }
                    } catch (Exception e) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.addcardgroup.error.invalidmaxreceiveraccessfee", cardGroupDetailsVO.getEndRangeAsString()));
                    }
                }
                // If Sender conversion factor is less than 0, show an error
                // message.
                if(!PretupsI.VOUCHER_CONS_SERVICE.equals(cardGroupSetVO.getServiceType())) {
                try {
                    if (Double.parseDouble(cardGroupDetailsVO.getSenderConvFactor()) <= 0) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.invalidsenderconvfactorrange"));
                    }
                } catch (Exception e) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.invalidsenderconvfactorrange"));
                }
                }
                // If Receiver conversion factor is less than 0, show an error
                // message.
                try {
                    if (Double.parseDouble(cardGroupDetailsVO.getReceiverConvFactor()) <= 0) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.invalidreceiverconvfactorrange"));
                    }
                } catch (Exception e) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.invalidreceiverconvfactorrange"));
                }

                // If Bonus validity days is less than 0, show an error message.
                if(!PretupsI.VOUCHER_CONS_SERVICE.equals(cardGroupSetVO.getServiceType())) {
                	try {
                    if (cardGroupDetailsVO.getBonusValidityValue() < 0) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.invalidbonusvalidityraterange"));
                    }
                } catch (Exception e) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.invalidbonusvalidityraterange"));
                }
                }
                // validity for in promo
                try {
                    if (SystemPreferences.IN_PROMO_REQUIRED) {
                        if (BTSLUtil.isNullString(cardGroupDetailsVO.getInPromoAsString())) {
                        	cardGroupDetailsVO.setInPromoAsString("0");                        }
                        if (!this.isNumeric(cardGroupDetailsVO.getInPromoAsString())) {
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.rp2p.error.numeric.IN_promo"));
                        }
                        if (Double.parseDouble(cardGroupDetailsVO.getInPromoAsString()) < 0) {
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.rp2p.error.positive.IN_promo"));
                        }

                    }
                } catch (Exception e) {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.rp2p.error.positive.IN_promo"));
                }

                // Validation for the Bonus Bundles.
                try {
                	cardGroupDetailsVO.setBonusAccList(new ArrayList<>(Arrays.asList(objectMapper.readValue(cardGroupSetListNode.get(k).get("bonusAccList").toString(), BonusAccountDetailsVO[].class))));

                    final ArrayList bonusAccList = cardGroupDetailsVO.getBonusAccList();
                    BonusAccountDetailsVO bonusAccVO = null;
                    String bonusName ;
                    String type ;
                    String bonusValue ;
                    String bonusValidity ;
                    String bonusConcFac ;
                    final int listSize = bonusAccList.size();
                    for (int i = 0; i < listSize; i++) {
                        bonusAccVO = (BonusAccountDetailsVO) bonusAccList.get(i);

                        bonusName = bonusAccVO.getBonusName();
                        type = bonusAccVO.getType();
                        bonusValue = bonusAccVO.getBonusValue();
                        bonusValidity = bonusAccVO.getBonusValidity();
                        bonusConcFac = bonusAccVO.getMultFactor();

                        // Validation for the Bonus Value
                            // If bonus value is null, show an error message.
                            if (BTSLUtil.isNullString(bonusValue)) {
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.bonusvalue.required", bonusName));
                            } else if (!this.isNumeric(bonusValue)) {
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.numeric.bonusvalue", bonusName));
                            } else if (Double.parseDouble(bonusValue) < 0) {
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.positive.bonusvalue", bonusName));
                            } else if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(type)) {
                                if (Double.parseDouble(bonusValue) < 0 || Double.parseDouble(bonusValue) > 100) {
                                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.bonusvalue.percentage", bonusName));
                                }
                            }
                        // Check for Bonus Validity.
                            // If Bonus validity is null, show an error message.
                            if (BTSLUtil.isNullString(bonusValidity)) {
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.bonusvalidity.required", bonusName));
                            } else if (!this.isNumeric(bonusValidity)) {
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.numeric.bonusvalidity", bonusName));
                            } else if (!this.isInteger(bonusValidity)) {
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.integer.bonusvalidity", bonusName));
                            } else if (Double.parseDouble(bonusValidity) < 0) {
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.positive.bonusvalidity", bonusName));
                            }
                        // Check for Bonus Conversion Factor.
                            // If Bonus conversion factor is null, show an error
                            // message.
                            if (BTSLUtil.isNullString(bonusConcFac)) {
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.bonusconvfactor.required", bonusName));
                            } else if (!this.isNumeric(bonusConcFac)) {
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.numeric.bonusconvfactor", bonusName));
                            } else if (Double.parseDouble(bonusConcFac) <= 0) {
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cp2p.error.positive.bonusconvfactor", bonusName));
                            }
                    }
                } catch (Exception e) {
                	log.error(METHOD_NAME, "Exception:e=" + e);
                }
                
                
                if (!BTSLUtil.isNullString(cardGroupSetVO.getSubServiceType())) {
                    	cardGroupDetailsVO.setServiceTypeSelector(cardGroupSetVO.getServiceType() + "_" + cardGroupSetVO.getSubServiceType());
                }
                if (!BTSLUtil.isNullString(cardGroupDetailsVO.getValidityPeriodTypeDesc()))
                {
    			String validityPeriod = BTSLUtil.getValidityPeriodType(cardGroupSetVO.getValidityTypeList(), cardGroupDetailsVO.getValidityPeriodTypeDesc());
    			if (BTSLUtil.isNullString(validityPeriod)){
    				throw new BTSLBaseException(this, "add", "cardgroup.cardgroupdetails.err.msg.novoucherperiodtypefound");
    	        }
    			cardGroupDetailsVO.setValidityPeriodType(validityPeriod);
                }
             // check the start range value(Talk time is valid or not)
                final P2PTransferVO p2pTransferVO ;
                if(PretupsI.VOUCHER_CONS_SERVICE.equals(cardGroupSetVO.getServiceType()))
                {
                	cardGroupDetailsVO.setCardGroupType(PretupsI.CARD_GROUP_VMS);
        			p2pTransferVO = this.calculateTalkTime(cardGroupDetailsVO, PretupsBL.getSystemAmount(cardGroupDetailsVO.getVoucherDenomination()), cardGroupSetVO.getSubServiceType(), false);
                }else
            		p2pTransferVO = this.calculateTalkTime(cardGroupDetailsVO, cardGroupDetailsVO.getStartRange(), cardGroupSetVO.getSubServiceType(), true); // update
               

                if (p2pTransferVO.getSenderTransferValue() < 0) {
                    // invalid talk time
                    final String[] arr = { PretupsBL.getDisplayAmount(p2pTransferVO.getSenderTransferValue()), slabCount };
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cardgroupdetails.error.invalidsendertransfervalue.slabwise", arr));
                }

                if (p2pTransferVO.getReceiverTransferValue() < 0) {
                    // invalid talk time
                    final String[] arr = { PretupsBL.getDisplayAmount(p2pTransferVO.getReceiverTransferValue()), slabCount };
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cardgroupdetails.error.invalidreceivertransfervalue.slabwise", arr));
                }
            }
            
            if (cardGroupList != null && !cardGroupList.isEmpty()) {
    			
                final ListSorterUtil sort = new ListSorterUtil();
                ArrayList sortedlist = (ArrayList) sort.doSort("startRange", null,cardGroupList);
              //validate the list
                CardGroupDetailsVO preCardVO = (CardGroupDetailsVO)sortedlist.get(0);
                CardGroupDetailsVO nextCardVO = null;
                CardGroupDetailsVO currCrdVO = null;
                if(PretupsI.VOUCHER_CONS_SERVICE.equals(cardGroupSetVO.getServiceType()))
                {
                	currCrdVO = (CardGroupDetailsVO) sortedlist.get(0);
                	HashMap<String, String> cardGroups = new HashMap<String, String>();
                	try{
                		this.loadVoucherData(con, cardGroupSetVO, currCrdVO);
                	}catch (BTSLBaseException e) {
            			throw new BTSLBaseException(this, "validation", e.getMessage());
            		}
               	 	StringBuilder key = new StringBuilder(currCrdVO.getVoucherType()).append("_").append(currCrdVO.getVoucherSegment()).append("_").append(currCrdVO.getVoucherDenomination()).append("_").append(currCrdVO.getVoucherProductId());
               	 	String newkey =key.toString();
               	 	cardGroups.put(newkey, newkey);
               		for (int i = 1, j = sortedlist.size(); i < j; i++) {
                		 nextCardVO = (CardGroupDetailsVO) sortedlist.get(i);
                		 newkey = "";
                		 if (currCrdVO.getCardGroupCode().equalsIgnoreCase(nextCardVO.getCardGroupCode())) {
     						if(!SystemPreferences.DUPLICATE_CARDGROUP_CODE_ALLOW){
     							throw new BTSLBaseException(this, "refreshCard", "cardgroup.cardgroupdetails.error.cardgroupcode", "Detail");
     							}
                         } else if (currCrdVO.getCardName().equalsIgnoreCase(nextCardVO.getCardName())) {
                             throw new BTSLBaseException(this, "refreshCard", "cardgroup.cardgroupdetails.error.slabname", "Detail");
                         }
                		 key = new StringBuilder(nextCardVO.getVoucherType()).append("_").append(nextCardVO.getVoucherSegment()).append("_").append(nextCardVO.getVoucherDenomination()).append("_").append(nextCardVO.getVoucherProductId());
                		 newkey =key.toString();
                		 if(cardGroups.containsKey(newkey))
                		 	{
                			 String arr1[] ={ nextCardVO.getProductName()};
                			 throw new BTSLBaseException(this, "refreshCard", "cardgroup.cardgroupdetails.voucher.error.invalidslab", 0, arr1, "Detail");
                		 	} else {
                   			 cardGroups.put(newkey, newkey);
                		 	}
                		 	key=null;
                			currCrdVO = nextCardVO;
                	}
                }
            else{
                	 String startRangeLabel = null;
                	 String endRangeLabel = null;
                	 currCrdVO = (CardGroupDetailsVO)sortedlist.get(sortedlist.size() - 1);
                	 for (int i = 1, j = sortedlist.size(); i < j; i++) {
                		 nextCardVO = (CardGroupDetailsVO)sortedlist.get(i);
                		 if (nextCardVO.getStartRange() <= preCardVO.getEndRange()) {
                        startRangeLabel =  "cardgroup.cardgroupdetails.label.startrange";
                        endRangeLabel = "cardgroup.cardgroupdetails.label.endrange";
                        final String[] arr = { startRangeLabel, String.valueOf(i + 1), endRangeLabel, String.valueOf(i) };
    					 errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cardgroupdetails.error.invalidslab",arr));

                    }
                    if (currCrdVO.getCardGroupCode().equalsIgnoreCase(preCardVO.getCardGroupCode())) {
						if(!SystemPreferences.DUPLICATE_CARDGROUP_CODE_ALLOW){
							final String[] arr = { startRangeLabel, String.valueOf(i + 1), endRangeLabel, String.valueOf(i) };
        					 errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cardgroupdetails.error.cardgroupcode"));

							}
                    } else if (currCrdVO.getCardName().equalsIgnoreCase(preCardVO.getCardName())) {
					 errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cardgroup.cardgroupdetails.error.slabname"));

                    }
                    preCardVO = nextCardVO;
                }
               }
             }
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "validation", e.getMessage());
        }
        

        return errors;
    
	}
	
	
	 private boolean isNumeric(String p_value) {
	        boolean isNumeric = false;
	        try {
	            if (p_value != null) {
	                new Double(p_value);
	                isNumeric = true;
	            }
	        } catch (NumberFormatException e) {
	            isNumeric = false;
	        }
	        return isNumeric;
	    }
	 
	 
	 private boolean isInteger(String p_value) {
	        boolean isInteger = false;
	        try {
	            if (p_value != null) {
	                new Integer(p_value);
	                isInteger = true;
	            }
	        } catch (NumberFormatException e) {
	            isInteger = false;
	        }
	        return isInteger;
	    }
	 
	 
	 public boolean checkDeleteVersionAllowed(String p_cardGroupSetID, String p_moduleCode, StringBuffer strBuff) {
	        final String methodName = "checkDeleteVersionAllowed";
	        if (log.isDebugEnabled()) {        	
	            log.debug(methodName, "Entered p_cardGroupSetID" + p_cardGroupSetID + "p_moduleCode" + p_moduleCode);
	        }
	        boolean isAllowed = false;

	        final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
	        Connection con = null;
	        MComConnectionI mcomCon = null;
	        try {
	        	mcomCon = new MComConnection();
	        	con=mcomCon.getConnection();
	            final ArrayList versionList = cardGroupSetDAO.loadCardGroupSetVersionList(con, p_cardGroupSetID, p_moduleCode);
	            final int versionListSize = versionList.size();
	            if (versionListSize == 1) {
	                isAllowed = false;
	                strBuff.append(((CardGroupSetVersionVO) versionList.get(0)).getVersion());
	            } else {
	                isAllowed = true;
	            }
	            mcomCon.finalCommit();
	        } catch (BTSLBaseException ex) {
	            log.errorTrace(methodName, ex);
	            log.error(methodName, "Exceptin:ex=" + ex);
	            try {
	                if (con != null) {
	                    mcomCon.finalRollback();
	                }
	            } catch (Exception e) {
	                log.errorTrace(methodName, e);
	            }
	        } catch (SQLException e) {
	            log.errorTrace(methodName, e);
	            log.error(methodName, "Exceptin:ex=" + e);
	            try {
	                if (con != null) {
	                    mcomCon.finalRollback();
	                }
	            } catch (Exception ex) {
	                log.errorTrace(methodName, ex);
	            }
	        } catch (Exception ex) {
	            log.errorTrace(methodName, ex);
	            log.error(methodName, "Exceptin:ex=" + ex);
	            try {
	                if (con != null) {
	                    mcomCon.finalRollback();
	                }
	            } catch (Exception e) {
	                log.errorTrace(methodName, e);
	            }
	        } finally {
				if (mcomCon != null) {
					mcomCon.close("CardGroupP2PDetailsAction#" + methodName);
					mcomCon = null;
				}
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting");
	            }
	        }
	        return isAllowed;
	    }
	 
	 
	 private void updateSetVersion(Connection con, CardGroupSetVersionDAO cardGroupSetVersionDAO, CardGroupSetVersionVO setVersionVO,Date currentDate,String modifiedBy) throws Exception {
	        final String methodName = "updateSetVersion";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered");
	        }
	        /*
	         * Card group Set Version drop down key = combination of groupId and
	         * version
	         * so we spilt the id and version
	         */

	        setVersionVO.setModifiedBy(modifiedBy);
	        setVersionVO.setModifiedOn(currentDate);

	        final int updateCount = cardGroupSetVersionDAO.updateCardGroupSetVersion(con, setVersionVO);
	        if (updateCount <= 0) {
	            try {
	                con.rollback();
	            } catch (Exception e) {
	                log.errorTrace(methodName, e);
	            }
	            log.error(methodName, "Error: while Updating Card_Group_Set_Versions");
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        }

	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Exiting");
	        }
	    }
	 
	 
	 private P2PTransferVO calculateTalkTime(CardGroupDetailsVO cardGroupDetailVO, long amount, String subServiceID, boolean checkMultipleOff) throws BTSLBaseException  {
	        final String methodName = "calculateTalkTime";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered amount:" + amount);
	        }

	        final P2PTransferVO p2pTransferVO = new P2PTransferVO();
	        try {
	            p2pTransferVO.setTransferValue(amount);
	            p2pTransferVO.setRequestedAmount(amount);
	            final TransferItemVO itemVO1 = new TransferItemVO();
	            final TransferItemVO itemVO2 = new TransferItemVO();
	            final Date currentDate = new Date();
	            itemVO2.setPreviousExpiry(currentDate);
	            itemVO2.setTransferDateTime(currentDate);
	            itemVO2.setTransferDate(currentDate);
	            final ArrayList itemList = new ArrayList();
	            itemList.add(itemVO1);
	            itemList.add(itemVO2);
	            p2pTransferVO.setTransferItemList(itemList);
	            if(!PretupsI.CARD_GROUP_VMS.equals(cardGroupDetailVO.getCardGroupType())) {
	            	CardGroupBL.calculateP2PSenderValues(p2pTransferVO, cardGroupDetailVO, subServiceID, checkMultipleOff);
	            }
	            CardGroupBL.calculateP2PReceiverValues(p2pTransferVO, cardGroupDetailVO, subServiceID, checkMultipleOff);
	        } catch (BTSLBaseException be) {
	            log.errorTrace(methodName, be);
	            throw new BTSLBaseException(this, methodName, be.getMessage(), 0, be.getArgs(), "Detail");
	        } catch (Exception e) {
	            log.errorTrace(methodName, e);
	            throw new BTSLBaseException(this, methodName, "");
	        }

	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Exiting");
	        }

	        return p2pTransferVO;
	    }
	 
	 
	 
	 public void getReversalModifiedDate(JsonNode cardGroupSetListNode)
	 {
		 if (log.isDebugEnabled()) {
	            log.debug("getReversalModifiedDate", "Entered");
	        }
         if(!BTSLUtil.isNullObject(cardGroupSetListNode) && cardGroupSetListNode.size() > 0) {
         	int listSize = cardGroupSetListNode.size();
         	for(int i=0; i < listSize; i++) {
         		JsonNode cardGroupSetNode = cardGroupSetListNode.get(i);
         		if(!BTSLUtil.isNullObject(cardGroupSetNode) && !BTSLUtil.isNullObject(cardGroupSetNode.get("reversalModifiedDate"))) {
	            		String dateStr = cardGroupSetNode.get("reversalModifiedDate").asText();
	            		String dateStr1 = PretupsI.EMPTY;
	            		if(!BTSLUtil.isNullString(dateStr)) {
							try {
								dateStr1 = BTSLDateUtil.getDateInFormat(dateStr, PretupsI.DATE_FORMAT_YYYYMMDD_HYPHEN);
							} catch (ParseException e) {
								e.printStackTrace();
							}
	            			((ObjectNode)cardGroupSetNode).put("reversalModifiedDate", dateStr1);
	            		}
         		}
         	}
         }
	 }
	 
}
