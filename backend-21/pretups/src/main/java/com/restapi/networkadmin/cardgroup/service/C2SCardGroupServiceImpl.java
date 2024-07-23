package com.restapi.networkadmin.cardgroup.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.james.mime4j.field.datetime.DateTime;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListSorterUtil;
import com.btsl.common.ListValueVO;
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
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.transfer.businesslogic.TransferRulesCache;
import com.btsl.pretups.transfer.businesslogic.TransferRulesVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.ibm.icu.util.Calendar;
import com.restapi.networkadmin.cardgroup.controller.C2SCardGroupController;
import com.restapi.networkadmin.cardgroup.requestVO.C2SAddCardGroupSaveRequestVO;
import com.restapi.networkadmin.cardgroup.requestVO.CardGroupCalculateC2STransferValueRequestVO;
import com.restapi.networkadmin.cardgroup.requestVO.CardGroupDetailsRequestVO;
import com.restapi.networkadmin.cardgroup.requestVO.DefaultCardGroupRequestVO;
import com.restapi.networkadmin.cardgroup.responseVO.AddTempCardGroupListResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.C2SAddCardGroupSaveResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.C2SCardGroupSetNameListResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.C2SCardGroupVersionNumbersListResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.CardGroupCalculateC2STransferValueResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.ChangeDefaultCardGroupResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.GroupSetDetails;
import com.restapi.networkadmin.cardgroup.responseVO.LoadC2SCardGroupListResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.LoadC2SCardGroupResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.LoadC2SCardGroupVersionListResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.LoadCardGroupTransferValuesResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.UpdateSaveC2SCardGroupResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.VersionDetailsAndDeleteStatusVO;
import com.restapi.networkadmin.cardgroup.responseVO.ViewC2SCardGroupResponseVO;
import com.restapi.networkadmin.cardgroup.serviceI.C2SCardGroupServiceI;
import com.web.pretups.gateway.businesslogic.MessageGatewayWebDAO;
import com.web.pretups.master.businesslogic.ServiceClassWebDAO;
import com.web.pretups.transfer.businesslogic.TransferWebDAO;

@Service("C2SCardGroupServiceI")
public class C2SCardGroupServiceImpl implements C2SCardGroupServiceI {
public static final String CLASS_NAME = "AddC2SCardGroupService";
public static final Log LOG = LogFactory.getLog(C2SCardGroupController.class.getName());

@Override
public LoadC2SCardGroupResponseVO loadServiceAndSubServiceList(Connection con, String networkID) throws BTSLBaseException {

    final String methodName = "loadServiceAndSubServiceList";
    if (LOG.isDebugEnabled()) {
        LOG.debug(methodName, "Entered");
    }
 
    LoadC2SCardGroupResponseVO response = new LoadC2SCardGroupResponseVO();
    
    	response.setViewSlabCopy(String.valueOf(PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_CARD_GROUP_SLAB_COPY)));
        // set the viewCopy to 0 to hide the button and 1 to show the button
    	response.setViewCopy(Integer.parseInt(Constants.getProperty("CARD_GROUP_SET_COPY_OPTION")));
        // load sub service service type list
        // ==============================================================================================
        // theForm.setCardGroupSubServiceList(LookupsCache.loadLookupDropDown(PretupsI.SUB_SERVICES,true));
    	response.setCardGroupSubServiceList(ServiceSelectorMappingCache.loadSelectorDropDownForCardGroup());
        // ==============================================================================================
        if (response.getCardGroupSubServiceList() != null && response.getCardGroupSubServiceList().size() == 1) {
            response.setCardGroupSubServiceID(((ListValueVO) response.getCardGroupSubServiceList().get(0)).getValue());
            response.setCardGroupSubServiceName(BTSLUtil.getOptionDesc(response.getCardGroupSubServiceID(), response.getCardGroupSubServiceList()).getLabel());
        }
       
        // Load the service type list for the C2S module
        response.setServiceTypeList(new CardGroupDAO().loadServiceTypeList(con, networkID, PretupsI.C2S_MODULE));
        if (response.getServiceTypeList().isEmpty()) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_SERVICE_GROUP_LIST);
        }
        if (response.getServiceTypeList().size() == 1) {
        	response.setServiceTypeId(((ListValueVO) response.getServiceTypeList().get(0)).getValue());
        	ListValueVO listValueVO = BTSLUtil.getOptionDesc(response.getServiceTypeId(), response.getServiceTypeList());
        	response.setServiceTypedesc(listValueVO.toString());
        }
        response.setSetTypeList(LookupsCache.loadLookupDropDown(PretupsI.CARD_GROUP_SET_TYPE, true));
        if (response.getSetTypeList() != null && response.getSetTypeList().size() == 1) {
            response.setSetType(((ListValueVO) response.getSetTypeList().get(0)).getValue());
            response.setSetTypeName(BTSLUtil.getOptionDesc(response.getSetType(), response.getSetTypeList()).getLabel());
        }
        return response;
        
    }


@Override
public LoadC2SCardGroupListResponseVO loadC2SCardGroupList(Connection con, String networkID, String subService     ) throws BTSLBaseException {	

	
LoadC2SCardGroupListResponseVO response = new LoadC2SCardGroupListResponseVO();
Date currentDate = new Date();
final DateFormat dateFormat = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
final Calendar cal = BTSLDateUtil.getInstance();
response.setReversalModifiedDateAsString(BTSLDateUtil.getLocaleTimeStamp(dateFormat.format(cal.getTime())));
response.setReversalModifiedDate(currentDate);
// populate the drop downs
response.setAmountTypeList(LookupsCache.loadLookupDropDown(PretupsI.AMOUNT_TYPE, true));
response.setValidityTypeList(LookupsCache.loadLookupDropDown(PretupsI.VALIDITY_TYPE, true));

try {
ArrayList bonusBundlesList = new BonusBundleDAO().loadBonusBundles(con);
bonusBundlesList = this.arrangeBonuses(bonusBundlesList, subService, false);
response.setBonusBundleList(bonusBundlesList);

ArrayList cardGroupSubServiceList = ServiceSelectorMappingCache.loadSelectorDropDownForCardGroup();
ArrayList serviceTypeList = new CardGroupDAO().loadServiceTypeList(con, networkID, PretupsI.C2S_MODULE); 
ArrayList setTypeList = LookupsCache.loadLookupDropDown(PretupsI.CARD_GROUP_SET_TYPE, true);
// -1 define the user mode is insert
    response.setLocationIndex(-1);
    response.setSetStatus("Y");
    
    if (cardGroupSubServiceList != null) {
        final ListValueVO listVO = BTSLUtil.getOptionDesc(subService, cardGroupSubServiceList);
        response.setCardGroupSubServiceName(listVO.getLabel());
    }
      
    BonusAccountDetailsVO bonusAccountDetailsVO = null;
    final ArrayList bonusAccList = new ArrayList();
    final int listLength = bonusBundlesList.size();
    for (int i = 0; i < listLength; i++) {
        bonusAccountDetailsVO = new BonusAccountDetailsVO();
        bonusAccList.add(bonusAccountDetailsVO);
        
        
    }
    response.setTempAccList(bonusAccList);
}
catch(BTSLBaseException e) {
	throw e;
}
catch(Exception e) {
	throw e;
}
    return response;

}
	
 @Override
 public C2SAddCardGroupSaveResponseVO saveC2SCardGroup(Connection con, HttpServletRequest request,C2SAddCardGroupSaveResponseVO response, UserVO userVO, C2SAddCardGroupSaveRequestVO requestVO) throws Exception {
        final String methodName = "saveC2SCardGroup";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        
        boolean alreadyRunning = false;
      //added for choice recharge
		Connection con1 = null;
		MComConnectionI mcomCon1 = null;
		
		
        try {
            // Security CSRF starts here
          //  final boolean flag = CSRFTokenUtil.isValid(request);

            final ArrayList cardGroupList1 = requestVO.getCardGroupList();
            final Iterator iterator = cardGroupList1.iterator();
            int count = 0;
//            if (!flag) {
//                if (LOG.isDebugEnabled()) {
//                    LOG.debug("CSRF", "ATTACK!");
//                }
//                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "DetailView");
//            }
            String slabCount = null;
            while (iterator.hasNext()) {
                count++;
                slabCount = String.valueOf(count);
                final CardGroupDetailsVO cardGroupDetailVO = (CardGroupDetailsVO) iterator.next();

                if (!BTSLUtil.isNullString(requestVO.getCardGroupSubServiceID())) {
                    final String serviceID = requestVO.getCardGroupSubServiceID();
                    final int index = serviceID.indexOf(":");
                    if (index != -1) {
                        cardGroupDetailVO.setServiceTypeSelector(requestVO.getCardGroupSubServiceID().replace(':', '_'));
                    }
                }
                // check the start range value(Talk time is valid or
                // not)
               
                ArrayList bounsAccList= cardGroupDetailVO.getBonusAccList();
                
                for(int i=0;i<bounsAccList.size(); i++) {
                	BonusAccountDetailsVO vo = new BonusAccountDetailsVO();
                	 LinkedHashMap<Object, Object> l =(LinkedHashMap) bounsAccList.get(i);
                	 
                	 for (Map.Entry<Object, Object> it : l.entrySet()) {
                		 String key = it.getKey().toString();
                		 if( key.equals(PretupsI.CARDGROUP_SET_ID)) {
                			 if(it.getValue() != null)
                			 vo.setCardGroupSetID(it.getValue().toString());
                		 }
                		 if( key.equals(PretupsI.VERSION)) {
                			 if(it.getValue() != null)
                			 vo.setVersion(it.getValue().toString());
                		 }
                		 if( key.equals(PretupsI.CARDGROUP_ID)) {
                			 if(it.getValue() != null)
                			 vo.setCardGroupID(it.getValue().toString());
                		 }
                		 if( key.equals(PretupsI.BUNDLE_ID)) {
                			 if(it.getValue() != null)
                			 vo.setBundleID(it.getValue().toString());
                		 }
                		 if( key.equals(PretupsI.TYPE)) {
                			 if(it.getValue() != null)
                			 vo.setType(it.getValue().toString());
                		 }
                		 if( key.equals(PretupsI.BONUS_VALIDITY)) {
                			 if(it.getValue() != null)
                			 vo.setBonusValidity(it.getValue().toString());
                		 }
                		 if( key.equals(PretupsI.BONUS_VALUE)) {
                			 if(it.getValue() != null)
                			 vo.setBonusValue(it.getValue().toString());
                		 }
                		 if( key.equals(PretupsI.MULT_FACTOR)) {
                			 if(it.getValue() != null)
                			 vo.setMultFactor(it.getValue().toString());
                		 }
                		 if( key.equals(PretupsI.BONUS_NAME)) {
                			 if(it.getValue() != null)
                			 vo.setBonusName(it.getValue().toString());
                		 }
                		 if( key.equals(PretupsI.BUNDLE_TYPE)) {
                			 if(it.getValue() != null)
                			 vo.setBundleType(it.getValue().toString());
                		 }
                		 if( key.equals(PretupsI.RESTRICTED_ON_IN)) {
                			 if(it.getValue() != null)
                			 vo.setRestrictedOnIN(it.getValue().toString());
                		 }
                		 if( key.equals(PretupsI.BONUS_ACC_DETAIL_LIST)) {
                			 if(it.getValue() != null)
                			 vo.setBonusAccDetailList((ArrayList)it.getValue());
                		 }
                		 if( key.equals(PretupsI.BOUNUS_CODE)) {
                			 if(it.getValue() != null)
                			 vo.setBonusCode(it.getValue().toString());
                		 }
                		
                	}
                	bounsAccList.set(i, vo);
                }
                
                cardGroupDetailVO.setBonusAccList(bounsAccList);
                final C2STransferVO c2sTransferVO = this.calculateTalkTime(request, cardGroupDetailVO, cardGroupDetailVO.getStartRange(), requestVO
                    .getCardGroupSubServiceID().split(":")[1], false);// update
              
                if (c2sTransferVO.getReceiverTransferValue() < 0) {
                    // invalid talk time
                	String errorMsg = null;
                	
                		
                	final String[] arr = {slabCount, PretupsBL.getDisplayAmount(c2sTransferVO.getReceiverTransferValue())};
                    Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
                    errorMsg= RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_RECEIVER_TRANSFER_VALUE_SLABWISE ,arr);
                    response.setMessage(errorMsg);
                    response.setMessageCode(PretupsErrorCodesI.INVALID_RECEIVER_TRANSFER_VALUE_SLABWISE);
                    throw new BTSLBaseException(CLASS_NAME, methodName,
							PretupsErrorCodesI.INVALID_RECEIVER_TRANSFER_VALUE_SLABWISE,  arr);
                	
              //      throw new BTSLBaseException(CLASS_NAME, methodName, errorMsg);
                }

            }
            ArrayList<CardGroupDetailsVO> updatedCardGroupList = new ArrayList<>();
            updatedCardGroupList =requestVO.getCardGroupList();
            final ArrayList sortedList1 = requestVO.getCardGroupList();
            Collections.sort(sortedList1, (Comparator<CardGroupDetailsVO>) (o1, o2) -> Integer.compare((int) o1.getStartRange(), (int) o2.getStartRange()));
            String startRangeLabel = PretupsI.START_RANGE;
            if (sortedList1 != null && !sortedList1.isEmpty()) {
                HashSet<String> cardGroupName = new HashSet<>();{
                    for(CardGroupDetailsVO card : updatedCardGroupList){
                        if(cardGroupName.contains(card.getCardName())){
                            throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.DUPLCATE_CARD_GROUP);
                        }
                        cardGroupName.add(card.getCardName());
                    }
                }
                CardGroupDetailsVO preCardVO = (CardGroupDetailsVO) sortedList1.get(0);
                preCardVO.setRowIndex(1);
                String endRangeLabel = null;
                CardGroupDetailsVO nextCardVO = null;
                for (int i = 1, j = sortedList1.size(); i < j; i++) {
                    nextCardVO = (CardGroupDetailsVO) sortedList1.get(i);
                    nextCardVO.setRowIndex(i + 1);
                    if (nextCardVO.getStartRange() <= preCardVO.getEndRange()) {
                        startRangeLabel = PretupsI.START_RANGE;
                        endRangeLabel = PretupsI.END_RANGE;
                        final String[] arr1 = { startRangeLabel, String.valueOf(i + 1), endRangeLabel, String.valueOf(i) };
                        requestVO.setSelectCardGroupSetId(null);
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CARDGROUP_CARDGROUP_C2S_DETAILS_INVALIDSLAB, 0, arr1, "Success");
                    }
                    preCardVO = nextCardVO;

                }
            }
            

          
			
               
                final Date currentDate = new Date();
                final CardGroupDAO cardDAO = new CardGroupDAO();
                final CardGroupSetVersionDAO cardGroupSetVersionDAO = new CardGroupSetVersionDAO();
                final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
                
                //added for choice recharge
               List<CardGroupDetailsVO> dblist=new ArrayList<CardGroupDetailsVO>();
               
                ProcessStatusVO  processVO=null;
                ProcessStatusDAO processDAO = null;
                ProcessBL processBL=new ProcessBL();
                
                
                    // check whether the Card Group Name is already exist or not
                    if (cardGroupSetDAO.isCardGroupSetNameExist(con, userVO.getNetworkID(), requestVO.getCardGroupSetName(), null)) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CARD_GROUP_NAME_ALREADY_EXIST , "DetailView");
                    }
                    // populating CardGroupSetVO from the form parameters
                    CardGroupSetVO cardGroupSetVO = new CardGroupSetVO();
                    cardGroupSetVO.setCardGroupSetID(String.valueOf(IDGenerator.getNextID(PretupsI.CARD_GROUP_SET_ID, TypesI.ALL)));
                    cardGroupSetVO.setCardGroupSetName(requestVO.getCardGroupSetName());
                    cardGroupSetVO.setNetworkCode(userVO.getNetworkID());
                    cardGroupSetVO.setCreatedOn(currentDate);
                    cardGroupSetVO.setCreatedBy(userVO.getUserID());
                    cardGroupSetVO.setModifiedOn(currentDate);
                    cardGroupSetVO.setModifiedBy(userVO.getUserID());
                    cardGroupSetVO.setLastVersion(requestVO.getVersion());
                    cardGroupSetVO.setModuleCode(PretupsI.C2S_MODULE);
                    cardGroupSetVO.setStatus(PretupsI.STATUS_ACTIVE);
                    cardGroupSetVO.setSubServiceType(requestVO.getCardGroupSubServiceID().split(":")[1]);
                    cardGroupSetVO.setServiceType(requestVO.getServiceTypeID());
                    cardGroupSetVO.setSetType(requestVO.getSetType());
                    if(requestVO.getDefaultCardGroup()!= null)
                    	cardGroupSetVO.setDefaultCardGroup(requestVO.getDefaultCardGroup());
                    else
                    	cardGroupSetVO.setDefaultCardGroup(PretupsI.NO);
                    //added for choice recharge
                    List cardGroupList= requestVO.getCardGroupList();
                    Iterator itr = cardGroupList.iterator();
	                boolean systemStatusOK = false;
	                
	                if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHOICE_RECHARGE_APPLICABLE)).booleanValue() && PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equals(requestVO.getServiceTypeID()))
	                {
	                	 try {
							mcomCon1 = new MComConnection();
							con1 = mcomCon1.getConnection();
	 						processVO=processBL.checkProcessUnderProcessNetworkWise(con1,PretupsI.BAT_MOD_C2S_CG_PROCESS_ID,userVO.getNetworkID());
	 						systemStatusOK = processVO.isStatusOkBool();
	 						
	 						
	 						if(!systemStatusOK)  { 
	 							throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.CARD_GROUP_CANNOT_BE_ADDED_FOR_CHOICE_RECHARGE_LOCK_FAILED,"DetailView"); 
	 						}
	 						con1.commit();
	 					}catch(Exception e)
	 					{   
	 						LOG.errorTrace(methodName, e);
	 						alreadyRunning = true;
	 						throw new BTSLBaseException(CLASS_NAME, methodName,PretupsErrorCodesI.CARD_GROUP_CANNOT_BE_ADDED_FOR_CHOICE_RECHARGE_LOCK_FAILED,"DetailView"); 
	 					}
	                	
	                }
	                while(itr.hasNext())
	                {
		            	CardGroupDetailsVO cardGroupDetailVO = (CardGroupDetailsVO) itr.next();

		            	if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHOICE_RECHARGE_APPLICABLE)).booleanValue() && PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equals(requestVO.getServiceTypeID()))
		            	{
		            		CardGroupDAO cardGroupDAO=new CardGroupDAO();
							dblist=cardGroupDAO.validateCardGroupDetailsForChoiceRecharge(String.valueOf(cardGroupDetailVO.getStartRange()),String.valueOf(cardGroupDetailVO.getEndRange()),requestVO.getCardGroupSubServiceID(),userVO.getNetworkID());
				            if(dblist!=null)
				            {
				            	choiceRechargeValidation(dblist,cardGroupDetailVO,con1);
				            }
		            	}
	                }
	              //choice recharge changes end here
	                
                    // insert Card_Group_Set
	                CardGroupDAO cardGroupDAO = new CardGroupDAO();
	                String defaultCardGroupID = null;
	                List<String> defaultCardGroupList = (cardGroupDAO.loadDefaultCardGroup(con, requestVO.getServiceTypeID(), requestVO.getCardGroupSubServiceID().split(":")[1], PretupsI.YES,userVO.getNetworkID()));
                    if(defaultCardGroupList!=null && !defaultCardGroupList.isEmpty())
                    	defaultCardGroupID = defaultCardGroupList.get(0);
                    
	                final int insertSetCount = cardGroupSetDAO.addCardGroupSet(con, cardGroupSetVO);
                   
	                
                    if (insertSetCount <= 0) {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            LOG.errorTrace(methodName, e);
                        }
                        LOG.error(methodName, "Error: while Inserting Card_Group_Set");
                        throw new BTSLBaseException(this, methodName, "error.general.processing");
                    }
                    if(requestVO.getDefaultCardGroup().equals(PretupsI.YES) && defaultCardGroupID!=null) {
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
                    		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
                            String arr[]= { requestVO.getCardGroupSetName()};
                    		String resmsg = RestAPIStringParser.getMessage(locale,
                    				PretupsErrorCodesI.SUCCESSFULLY_CHANGE_DEFAULT_CARD_GROUP, arr);
                            final AdminOperationVO adminOperationVO = new AdminOperationVO();
                            adminOperationVO.setSource(PretupsI.LOGGER_OPERATION_MAKE_DEFAULT_CARDGROUP);
        					adminOperationVO.setDate(currentDate);
        					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
        					adminOperationVO.setInfo(resmsg);
        					adminOperationVO.setLoginID(userVO.getLoginID());
        					adminOperationVO.setUserID(userVO.getUserID());
        					adminOperationVO.setCategoryCode(userVO.getCategoryCode());
        					adminOperationVO.setNetworkCode(userVO.getNetworkID());
        					adminOperationVO.setMsisdn(userVO.getMsisdn());
        					AdminOperationLog.log(adminOperationVO);
                    	}
                    	else {
                    		con.rollback();
    	                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.SELECTED_CARD_GROUP_SET_IS_UNABLE_TO_MAKE_DEFAULT, "");
                    	}
                    }
                  
                    CardGroupDAO cardGroupDao = new CardGroupDAO();
                    // insert Card Group Version and Card Group Details
                    this.add(requestVO, userVO, con, cardGroupSetVO, currentDate);
                    requestVO.setOrigCardGroupSetNameList(cardGroupDao.loadCardGroupSet(con, userVO.getNetworkID(), PretupsI.C2S_MODULE));

                    final ArrayList list = new ArrayList();
                    if (requestVO.getOrigCardGroupSetNameList() != null && !BTSLUtil.isNullString(requestVO.getCardGroupSubServiceID()) && !BTSLUtil.isNullString(requestVO.getServiceTypeID())) {
                        cardGroupSetVO = null;
                        for (int i = 0, j = requestVO.getOrigCardGroupSetNameList().size(); i < j; i++) {
                            cardGroupSetVO = (CardGroupSetVO) requestVO.getOrigCardGroupSetNameList().get(i);
                            if (cardGroupSetVO.getSubServiceType().equals(requestVO.getCardGroupSubServiceID().split(":")[1]) && cardGroupSetVO.getServiceType().equals(
                            		requestVO.getCardGroupSubServiceID().split(":")[0]) && cardGroupSetVO.getServiceType().equals(requestVO.getServiceTypeID())) // updated
                           
                            {
                                list.add(cardGroupSetVO);
                            }
                        }
                    }

                    
                    requestVO.setCardGroupSetNameList(list);
                    ArrayList namelist= list;
                    CardGroupSetVO setVO = null;
                    final ArrayList messageList = new ArrayList();
                    KeyArgumentVO keyArg = new KeyArgumentVO();
                    keyArg.setKey("cardgroup.cardgroupc2sdetailsview.successaddmessage");
                    messageList.add(keyArg);
                    final ArrayList cardList = requestVO.getCardGroupList();
                    ArrayList cardSlabList = null;
                    String[] slabToSetArray = null;
                    int i = 0;
                    final Iterator itr4 = cardList.iterator();
                    Iterator itr1 = null;
                    CardGroupDetailsVO cardDetail = null;
                    CardGroupDetailsVO cardDetailVO = null;
                    CardGroupSetVersionVO setVersionVO = null;
                    setVO = null;
                    String lastVersion = null;
                    ListSorterUtil sort = new ListSorterUtil();
                    final int size = cardList.size() * requestVO.getCardGroupSetNameList().size();
                    final String[] notCopiedSets = new String[size];
                    final String[] copiedSets = new String[size];
                    boolean isValidSlab = false;
                    CardGroupDetailsVO preCardVO = null;
                    ArrayList sortedList = null;
                    CardGroupDetailsVO nextCardVO = null;
                    Iterator itr2 = null;
                    int version = 0;
                    int updateCount = 0;
                    String currentDateString = null;
                    CardGroupSetVO newSetVO;
                    Date applicableFrom = null;
                    Set keyForCardGroupSet = null;
                    int deleteCount = 0;
                    String cardGroupSetId = null;
                    String setName = null;
                    final Map setsCopySlabs = new HashMap(); // will store the
                                                             // card
                    // group set ID and
                    // corresponding to be
                    // copied slabs as key
                    // value
                    ArrayList slabList = null;
                    final String format = Constants.getProperty("CARDGROUP_DATE_FORMAT");
                    Iterator keyForCardGroupSetIterator = null;
                    ArrayList newSlabsList = null;
 
                    final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATE_HHMM);
                    int count1 = 0;
                    int count2 = 0;
                    ArrayList tempSlabList = new ArrayList();

                    while (itr4.hasNext())// loop for all the slabs in the
                    // current cardgroupset
                    {
                        cardDetail = (CardGroupDetailsVO) itr4.next();
                        slabToSetArray = cardDetail.getCardGroupList();
                        if (slabToSetArray != null) {
                     
                        	for (int p = 0; p < slabToSetArray.length; p++) {
                                if (!setsCopySlabs.containsKey(slabToSetArray[p])) {
                                    slabList = new ArrayList();
                                    slabList.add(cardDetail);
       
                                    setsCopySlabs.put(slabToSetArray[p], slabList);
                                } else {
                                    slabList = new ArrayList();
                                    tempSlabList = (ArrayList) setsCopySlabs.get(slabToSetArray[p]);
                                    slabList.addAll(tempSlabList);
                                    slabList.add(cardDetail);
                                    setsCopySlabs.put(slabToSetArray[p], slabList);
                                }
                            }
                        }
                    }
                    
                    
                    
                    
                    keyForCardGroupSet = setsCopySlabs.keySet();
                    keyForCardGroupSetIterator = keyForCardGroupSet.iterator();
                    i = 0;
                    while (keyForCardGroupSetIterator.hasNext()) {
                        cardGroupSetId = (String) keyForCardGroupSetIterator.next();

                        isValidSlab = true;
                        cardGroupDao = new CardGroupDAO();
                        itr1 = requestVO.getCardGroupSetVersionList().iterator();
                        itr2 = requestVO.getCardGroupSetNameList().iterator();

                        while (itr1.hasNext()) { // getting the latest version
                            // for the particular set in
                            // which we want to copy the
                            // slab
                            setVersionVO = (CardGroupSetVersionVO) itr1.next();
                            if (setVersionVO.getCardGroupSetID().equals(cardGroupSetId)) {
                                lastVersion = setVersionVO.getVersion();
                                break;
                            }
                        }
                        while (itr2.hasNext()) {
                            setVO = (CardGroupSetVO) itr2.next();
                            if (setVO.getCardGroupSetID().equals(cardGroupSetId)) {
                                break;
                            }
                        }

                        newSetVO = new CardGroupSetVO();
                        newSetVO.setModuleCode(setVO.getModuleCode());
                        newSetVO.setCardGroupSetID(setVO.getCardGroupSetID());
                        newSetVO.setCardGroupSetName(setVO.getCardGroupSetName());
                        newSetVO.setCreatedBy(setVO.getCreatedBy());
                        newSetVO.setCreatedOn(setVO.getCreatedOn());
                        newSetVO.setLanguage1Message(setVO.getLanguage1Message());
                        newSetVO.setLanguage2Message(setVO.getLanguage2Message());
                        newSetVO.setModifiedBy(setVO.getModifiedBy());
                        requestVO.getCardGroupSetNameList().remove(setVO);
                        newSetVO.setLastModifiedOn(currentDate.getTime());
                        cardSlabList = cardGroupDao.loadCardGroupDetailsListByID(con, cardGroupSetId, lastVersion);
                        newSlabsList = (ArrayList) setsCopySlabs.get(cardGroupSetId);
                        cardSlabList.addAll(newSlabsList);
                        setName = ((CardGroupDetailsVO) cardSlabList.get(0)).getCardGroupSetName();
                        sort = new ListSorterUtil();
                        sortedList = (ArrayList) sort.doSort(PretupsI.START_RANGE, null, cardSlabList);
                        if (sortedList != null && !sortedList.isEmpty()) {
                            preCardVO = (CardGroupDetailsVO) sortedList.get(0);
                            for (int j = 1, n = sortedList.size(); j < n; j++) {
                                nextCardVO = (CardGroupDetailsVO) sortedList.get(j);
                                if (nextCardVO.getStartRange() <= preCardVO.getEndRange()) {
                                    notCopiedSets[i] = setName;
                                    count2++;
                                    isValidSlab = false;
                                    i++;
                                    break;
                                }
                                preCardVO = nextCardVO;
                            }
                        }
                        newSetVO.setLastVersion(lastVersion);
                        if (isValidSlab) {
                            if (setVersionVO.getApplicableFrom().getTime() < currentDate.getTime()) {
                                version = Integer.parseInt(lastVersion) + 1;
                                setVO.setLastVersion(String.valueOf(version));
                                newSetVO.setLastVersion(String.valueOf(version));
                            }
                            setVO.setModifiedOn(currentDate);
                            setVO.setModifiedBy(userVO.getUserID());
                            setVO.setDefaultCardGroup(requestVO.getDefaultCardGroup());
                            updateCount = cardGroupSetDAO.updateCardGroupSet(con, setVO);
                            setVO.setLastModifiedOn(currentDate.getTime());
                            if (updateCount <= 0) {
                                try {
                                    con.rollback();
                                } catch (Exception e) {
                                    LOG.errorTrace(methodName, e);
                                }
                                LOG.error(methodName, "Error: while Updating Card_Group_Set");
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
                            }
                               

                            if (setVersionVO.getApplicableFrom().getTime() < currentDate.getTime()) {
                                currentDateString = sdf.format(currentDate);
                                applicableFrom = BTSLUtil.getDateFromDateString(currentDateString.split("-")[0] + " " + currentDateString.split("-")[1], format);
                                setVersionVO.setApplicableFrom(applicableFrom);
                                setVersionVO.setCreatedBy(userVO.getUserID());
                                setVersionVO.setVersion(String.valueOf(version));
                                // currentDate= new Date();
                                setVersionVO.setCreadtedOn(currentDate);
                                setVersionVO.setModifiedBy(userVO.getUserID());
                                setVersionVO.setModifiedOn(currentDate);
                                // insert Card_Group_Set_Version
                                updateCount = cardGroupSetDAO.addCardGroupSetVersion(con, setVersionVO);
                                if (updateCount <= 0) {
                                    try {
                                        con.rollback();
                                    } catch (Exception e) {
                                        LOG.errorTrace(methodName, e);
                                    }
                                    LOG.error(methodName, "Error: while Updating Card_Group_Set_Version");
                                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
                                }
                                

                                // set the default values
                                for (int x = 0, y = sortedList.size(); x < y; x++) {
                                    cardDetailVO = new CardGroupDetailsVO();
                                    cardDetailVO = (CardGroupDetailsVO) sortedList.get(x);
                                    cardDetailVO.setCardGroupSetID(setVO.getCardGroupSetID());
                                    cardDetailVO.setCardGroupID(String.valueOf(IDGenerator.getNextID(PretupsI.CARD_GROUP_ID, TypesI.ALL)));
                                    cardDetailVO.setVersion(String.valueOf(version));

                                    // Added for Bonus Bundle feature.
                                    cardDetailVO.setSenderConvFactor("1");
                                    cardDetailVO.setReceiverConvFactor(cardDetailVO.getReceiverConvFactor());
                                }
                                updateCount = cardDAO.addCardGroupDetails(con, sortedList);
                                if (updateCount <= 0) {
                                    try {
                                        con.rollback();
                                    } catch (Exception e) {
                                        LOG.errorTrace(methodName, e);
                                    }
                                    LOG.error(methodName, "Error: while Updating Card_Group_Details");
                                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
                                }
                                
                                
                            } else {
                                setVersionVO.setModifiedBy(userVO.getUserID());
                                setVersionVO.setModifiedOn(currentDate);
                                // update card group set version table
                                updateCount = cardGroupSetVersionDAO.updateCardGroupSetVersion(con, setVersionVO);
                                if (updateCount <= 0) {
                                    try {
                                        con.rollback();
                                    } catch (Exception e) {
                                        LOG.errorTrace(methodName, e);
                                    }
                                    LOG.error(methodName, "Error: while Updating Card_Group_Set_Version");
                                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
                                }
                                
                                // delete data from the card_group_details
                                deleteCount = cardGroupSetVersionDAO.deleteCardGroupDetails(con, setVO.getCardGroupSetID(), setVersionVO.getVersion());
                                if (deleteCount < 0) {
                                    try {
                                        con.rollback();
                                    } catch (Exception e) {
                                        LOG.errorTrace(methodName, e);
                                    }
                                    LOG.error(methodName, "Error: while Deleting Card_Group_Details");
                                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
                                }
                                // set the default values
                                for (int x = 0, y = sortedList.size(); x < y; x++) {
                                    cardDetailVO = new CardGroupDetailsVO();
                                    cardDetailVO = (CardGroupDetailsVO) sortedList.get(x);
                                    cardDetailVO.setCardGroupSetID(setVO.getCardGroupSetID());
                                    //cardDetailVO.setReversalModifiedDate(requestVO.getCardGroupList().get(0).getReversalModifiedDate());
                                    cardDetailVO.setCardGroupID(String.valueOf(IDGenerator.getNextID(PretupsI.CARD_GROUP_ID, TypesI.ALL)));
                                    cardDetailVO.setVersion(setVersionVO.getVersion());
                                    // Added for Bonus Bundle feature.
                                    cardDetailVO.setSenderConvFactor("1");
                                    cardDetailVO.setReceiverConvFactor(cardDetailVO.getReceiverConvFactor());
                                }
                                updateCount = cardDAO.addCardGroupDetails(con, sortedList);

                                if (updateCount <= 0) {
                                    try {
                                        con.rollback();
                                    } catch (Exception e) {
                                        LOG.errorTrace(methodName, e);
                                    }
                                    LOG.error(methodName, "Error: while Updating Card_Group_Details");
                                    throw new BTSLBaseException(this, methodName,  PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
                                }
                                
                            }
                            copiedSets[count1] = setVO.getCardGroupSetName();
                            count1++;
                        }

                    }
                    if (count1 > 0) {
                        final StringBuffer sbfCopySuccessful = new StringBuffer();
                        for (int m = 0; m < count1; m++) {
                            if (m == 0) {
                                sbfCopySuccessful.append(copiedSets[m]);
                            } else if (m > 0 && m < count1 - 1) {
                                sbfCopySuccessful.append(copiedSets[m]);
                                sbfCopySuccessful.append(",");
                            } else if (m == count1 - 1) {
                                sbfCopySuccessful.append(" ");
                                sbfCopySuccessful.append("and");
                                sbfCopySuccessful.append(" ");
                                sbfCopySuccessful.append(copiedSets[m]);
                            }/* else{ } */
                        }
                        keyArg = new KeyArgumentVO();
                        keyArg.setKey("cardgroup.slab.successfully.copied.tosets");
                        keyArg.setArguments(sbfCopySuccessful.toString());
                        messageList.add(keyArg);
                    }
                    if (count2 > 0) {
                        final StringBuffer sbfCopyNotSuccessful = new StringBuffer();
                        for (int m = 0; m < count2; m++) {
                            if (m == 0) {
                                sbfCopyNotSuccessful.append(notCopiedSets[m]);
                            } else if (m > 0 && m < count2 - 1) {
                                sbfCopyNotSuccessful.append(notCopiedSets[m]);
                                sbfCopyNotSuccessful.append(",");
                            } else if (m == count2 - 1) {
                                sbfCopyNotSuccessful.append(" ");
                                sbfCopyNotSuccessful.append("and");
                                sbfCopyNotSuccessful.append(" ");
                                sbfCopyNotSuccessful.append(notCopiedSets[m]);
                            }
                        }
                        keyArg = new KeyArgumentVO();
                        keyArg.setKey("cardgroup.slab.copy.unsuccessful.tosets");
                        keyArg.setArguments(sbfCopyNotSuccessful.toString());
                        messageList.add(keyArg);
                    }
                    con.commit();
                    Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
                    String arr[]= { requestVO.getCardGroupSetName()};
            		String resmsg = RestAPIStringParser.getMessage(locale,
            				PretupsErrorCodesI.SAVE_C2S_CARD_GROUP_LIST_SUCCESS, arr);
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
                    
                 // if choice recharge case...then update the process status
    				if(processVO != null && processVO.isStatusOkBool()) {
    					processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);							
						processDAO=new ProcessStatusDAO();
						if(processDAO.updateProcessDetailNetworkWise(con1,processVO)>0) {
							con1.commit();
						} else {
							con1.rollback();
							LOG.error(methodName, "Error: while modifying process status : "+PretupsI.BAT_MOD_C2S_CG_PROCESS_ID);
						}
    				}    				
    			
      
       	                  
               	            

        }catch (BTSLBaseException e) {
        	loggerValue.append("Exceptin:e=");
        	loggerValue.append(e);
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, e);
            throw e;
        }
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exceptin:e=");
        	loggerValue.append(e);
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, e);
            throw e;
        } finally {
        	
        	try {
            	if(alreadyRunning) 
            		con1.rollback();
            }
        	catch(Exception e) {
      		        if (LOG.isDebugEnabled())
      		        	loggerValue.setLength(0);
      		        	loggerValue.append("Error: while modifying process status : ");
      		        	loggerValue.append(PretupsI.BAT_MOD_C2S_CG_PROCESS_ID);
      		        	LOG.error(methodName, loggerValue);
      		        LOG.errorTrace(methodName,e);
      		 }
        	
			
			if (mcomCon1 != null) {
				mcomCon1.close(CLASS_NAME+"#"+methodName);
				mcomCon1 = null;
			}
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting  forward=");
            //	loggerValue.append(forward);
                LOG.debug(methodName,loggerValue);
            }
        }

        return  response;
    }


 private void choiceRechargeValidation(List<CardGroupDetailsVO> dblist,CardGroupDetailsVO cardGroupDetailVO,Connection con1) throws BTSLBaseException, SQLException
	{
    	final String methodName="choiceRechargeValidation";
    	LogFactory.printLog(methodName, "Entered:", LOG);
    	 
    		 boolean errorFlag=false;
    		 StringBuilder sbf=new StringBuilder();
    		 StringBuilder sbf1=new StringBuilder();
    		 StringBuilder sbf2=new StringBuilder();
    		 ProcessStatusVO  processVO=null;
    		 ProcessStatusDAO processDAO;
    		 int dblists=dblist.size();
    		 for(int i=0;i<dblists;i++)
    		 {
    			 CardGroupDetailsVO cardList = dblist.get(i);
			
    			 if(cardGroupDetailVO.getStartRange() >= cardList.getStartRange() && cardGroupDetailVO.getEndRange() <= cardList.getEndRange())
    			 {
    				 sbf.append(cardList.getCardGroupSetName());
    				 sbf1.append(cardList.getCardName());
    				 sbf2.append(cardList.getCardGroupSubServiceIdDesc());
    				 errorFlag=true;
    				 break;
    			 }
 				
    			 if(cardGroupDetailVO.getStartRange() >= cardList.getStartRange() && cardGroupDetailVO.getStartRange() <= cardList.getEndRange() )
         		 {
          			sbf.append(cardList.getCardGroupSetName());
          			sbf1.append(cardList.getCardName());
          			sbf2.append(cardList.getCardGroupSubServiceIdDesc());
          			errorFlag=true;
          			break;
         		 }
    			 
    			 if (cardGroupDetailVO.getStartRange() < cardList.getStartRange() && cardGroupDetailVO.getEndRange() >= cardList.getStartRange() && cardGroupDetailVO.getEndRange() <= cardList.getEndRange())
    			 {
    				 sbf.append(cardList.getCardGroupSetName());
    				 sbf1.append(cardList.getCardName());
    				 sbf2.append(cardList.getCardGroupSubServiceIdDesc());
    				 errorFlag=true;
    				 break;
    			 }
 		
    			 if (cardGroupDetailVO.getStartRange() < cardList.getStartRange() && cardGroupDetailVO.getEndRange() >= cardList.getStartRange() && cardGroupDetailVO.getEndRange() >= cardList.getEndRange())
    			 {
    				 sbf.append(cardList.getCardGroupSetName());
    				 sbf1.append(cardList.getCardName());
					sbf2.append(cardList.getCardGroupSubServiceIdDesc());
					errorFlag=true;
					break;
    			 }
    		 }
    		 if(errorFlag)
    		 {
    			 String[] arr={sbf.toString().trim(),sbf1.toString().trim(), sbf2.toString().trim()};
    			 if(processVO != null && processVO.isStatusOkBool()) {
    				 processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);							
    				 processDAO=new ProcessStatusDAO();
    				 if(processDAO.updateProcessDetailNetworkWise(con1,processVO)>0) {
    					 con1.commit();
    				 } else {
					con1.rollback();
    				 }
    			 }
                throw new BTSLBaseException(PretupsErrorCodesI.CARD_GROUP_CANNOT_BE_ADDED_FOR_CHOICE_RECHARGE,arr,"DetailView");
    		 }
    	}
 
 private void add(C2SAddCardGroupSaveRequestVO requestVO, UserVO userVO, Connection con, CardGroupSetVO cardGroupSetVO, Date currentDate) throws Exception {
        final String methodName = "add";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
            
        }


        final CardGroupDAO cardDAO = new CardGroupDAO();
        // populating CardGroupsetVersionVO from the from Parameters
        final CardGroupSetVersionVO cardGroupVersionVO = new CardGroupSetVersionVO();
        final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
        cardGroupVersionVO.setCardGroupSetID(cardGroupSetVO.getCardGroupSetID());
        cardGroupVersionVO.setVersion(requestVO.getVersion());
        final String format = Constants.getProperty("CARDGROUP_DATE_FORMAT");
        String fromHour = null;
        if (BTSLUtil.isNullString(requestVO.getApplicableFromHour())) {
            fromHour = "00:00";
        } else {
            fromHour = requestVO.getApplicableFromHour();
        }
        final Date newDate = BTSLUtil.getDateFromDateString(requestVO.getApplicableFromDate() + " " + fromHour, format);
        cardGroupVersionVO.setApplicableFrom(newDate);
        cardGroupVersionVO.setCreatedBy(userVO.getUserID());
        cardGroupVersionVO.setCreadtedOn(currentDate);
        cardGroupVersionVO.setModifiedBy(userVO.getUserID());
        cardGroupVersionVO.setModifiedOn(currentDate);
        // insert Card_Group_Set_Version
        final int insertVersionCount = cardGroupSetDAO.addCardGroupSetVersion(con, cardGroupVersionVO);

        if (insertVersionCount <= 0) {
            try {
                con.rollback();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            LOG.error(methodName, "Error: while Inserting Card_Group_Set_Versions");
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
        }
        
        // set the default valus in the detail VO
        if (requestVO.getCardGroupList() != null && !requestVO.getCardGroupList().isEmpty()) {
            // set the default values
            for (int i = 0, j = requestVO.getCardGroupList().size(); i < j; i++) {
               final CardGroupDetailsVO cardDetailVO = (CardGroupDetailsVO) requestVO.getCardGroupList().get(i);
                cardDetailVO.setCardGroupSetID(cardGroupSetVO.getCardGroupSetID());
                cardDetailVO.setCardGroupID(String.valueOf(IDGenerator.getNextID(PretupsI.CARD_GROUP_ID, TypesI.ALL)));
                cardDetailVO.setVersion(requestVO.getVersion());
            }
            // insert Card_Group_Details
            final int insertDetailCount = cardDAO.addCardGroupDetails(con, requestVO.getCardGroupList());

            if (insertDetailCount <= 0) {
                try {
                    con.rollback();
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
                LOG.error(methodName, "Error: while Inserting Card_Group_Details");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
            }
        
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting");
        }
    }


private  ArrayList arrangeBonuses(ArrayList p_bonusBundleList, String p_cardGroupSubServiceID, boolean p_isBonusAccDetailList) throws BTSLBaseException {
    final String methodName = "arrangeBonuses";
    StringBuilder loggerValue= new StringBuilder();
    if (LOG.isDebugEnabled()) {
    	loggerValue.setLength(0);
    	loggerValue.append("Entered CardGroupSubServiceID=");
    	loggerValue.append(p_cardGroupSubServiceID);
		loggerValue.append(", p_bonusBundleList=");
    	loggerValue.append(p_bonusBundleList);
    	loggerValue.append(", p_isBonusAccDetailList=");
    	loggerValue.append(p_isBonusAccDetailList);
        LOG.debug(methodName,loggerValue );
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
    try {
        
            serviceSelectorKey = p_cardGroupSubServiceID.replace(':', '_');
            listSize = tempList.size();
            if (tempList != null && listSize > 0) {
                serviceSelectorMappingVO = (ServiceSelectorMappingVO) ServiceSelectorMappingCache.getServiceSelectorMap().get(serviceSelectorKey);
                receiverBonusID = serviceSelectorMappingVO.getReceiverBundleID();
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("receiverBonusID=");
                	loggerValue.append(receiverBonusID);
                    LOG.debug(methodName,loggerValue);
                }
                receiverBonusID =   BTSLUtil.NullToString(receiverBonusID);
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
        
    } catch (Exception e) {
    	throw e;
    }
    
   finally {
    	loggerValue.setLength(0);
    	loggerValue.append("Exited arrangedList=");
    	loggerValue.append(arrangedList);
        LOG.debug(methodName,loggerValue);
    }
    return arrangedList;
}


@Override
 public AddTempCardGroupListResponseVO addTempList(Connection con, HttpServletRequest request, UserVO userVO, CardGroupDetailsRequestVO requestVO) throws Exception {
        final String methodName = "addTempList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }	       
        final CardGroupDAO cardDAO = new CardGroupDAO();
        AddTempCardGroupListResponseVO response = new AddTempCardGroupListResponseVO();
        try {
           
            

            final CardGroupDetailsVO cardGroupDetailVO = new CardGroupDetailsVO();

            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
                if (BTSLUtil.isNullString(requestVO.getCosRequired() )|| "N".equalsIgnoreCase(requestVO.getCosRequired())) {
                    requestVO.setCosRequired("N");
                    cardGroupDetailVO.setCosRequired("N");
                }
            }
            if (BTSLUtil.isNullString(requestVO.getReversalPermitted()) || "N".equalsIgnoreCase(requestVO.getReversalPermitted())) {
                requestVO.setReversalPermitted("N");
                cardGroupDetailVO.setReversalPermitted("N");
            }
            
            // populate VO contents from Form
            this.constructVOFromForm(cardGroupDetailVO, requestVO);

            

            cardGroupDetailVO.setEditDetail("N");
            // Added for bonus bundles
            if (requestVO.getTempAccList() != null && !requestVO.getTempAccList().isEmpty()) {
                ArrayList tempsAcclist = null;
                tempsAcclist = requestVO.getBonusAccList();
                if (tempsAcclist == null) {
                    tempsAcclist = new ArrayList();
                }
                tempsAcclist.addAll(requestVO.getTempAccList());
                cardGroupDetailVO.setBonusAccList(tempsAcclist);
            }

            ArrayList list = null;
            if (requestVO.getLocationIndex() <= -1) {
                if (requestVO.getCardGroupList() == null || requestVO.getCardGroupList().isEmpty()) {
                    list = new ArrayList();
                    list.add(cardGroupDetailVO);
                } else {
                    list = new ArrayList(requestVO.getCardGroupList());
                    list.add(cardGroupDetailVO);
                }
            } else {
                list = new ArrayList(requestVO.getCardGroupList());
    
            }

            final ListSorterUtil sort = new ListSorterUtil();
            list = (ArrayList) sort.doSort("startRange", null, list);
            String startRangeLabel = null;

            if (list != null && !list.isEmpty()) {
                CardGroupDetailsVO preCardVO = (CardGroupDetailsVO) list.get(0);
                String endRangeLabel = null;
                CardGroupDetailsVO nextCardVO = null;
                CardGroupDetailsVO currCrdVO = null;
                currCrdVO = (CardGroupDetailsVO) list.get(list.size() - 1);
                for (int i = 1, j = list.size(); i < j; i++) {
                    nextCardVO = (CardGroupDetailsVO) list.get(i);
                    if (nextCardVO.getStartRange() <= preCardVO.getEndRange()) {
                        startRangeLabel =PretupsI.START_RANGE; 
                        endRangeLabel = PretupsI.END_RANGE;
                        final String[] arr = { startRangeLabel, String.valueOf(i + 1), endRangeLabel, String.valueOf(i) };
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CARDGROUP_CARDGROUP_C2S_DETAILS_INVALIDSLAB, 0, arr, "Detail");
                    }
                    if (currCrdVO.getCardGroupCode().equalsIgnoreCase(preCardVO.getCardGroupCode())) {
						if(!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DUPLICATE_CARDGROUP_CODE_ALLOW)).booleanValue()){
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CARDGROUP_CARDGROUP_DETAILS_ERROR_DUPLICATE_CARDGROUPCODE, "Detail");
							}
                    } else if (currCrdVO.getCardName().equalsIgnoreCase(preCardVO.getCardName())) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CARDGROUP_DETAILS_ERROR_SLAB_NAME, "Detail");
                    }
                    preCardVO = nextCardVO;
                }
            }

            // check the start range value(Talk time is valid or not)
            final C2STransferVO c2sTransferVO = this.calculateTalkTime(request, cardGroupDetailVO, cardGroupDetailVO.getStartRange(), requestVO.getCardGroupSubServiceID()
                .split(":")[1], false);// update
    
            if (c2sTransferVO.getReceiverTransferValue() < 0) {
                // invalid talk time
                final String[] arr = { PretupsBL.getDisplayAmount(c2sTransferVO.getReceiverTransferValue()) };
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CARDGROUP_C2S_DETATILS_ERROR_INVALID_RECEIVER_TRANSFERRULE, 0, arr, "Detail");
            }

            
            // if above validation succed, copy the list contents on the form
            // Block to handle special behaviour by arraylist in form added on
    
            final ArrayList tempList = new ArrayList();
            final Iterator itr = list.iterator();
            CardGroupDetailsVO vo = null;
            CardGroupDetailsVO vo1 = null;
            while (itr.hasNext()) {
                vo = (CardGroupDetailsVO) itr.next();
                vo1 = new CardGroupDetailsVO();
                vo1.setReceiverAccessFeeType(vo.getReceiverAccessFeeType());
                vo1.setReceiverTax1Type(vo.getReceiverTax1Type());
                vo1.setReceiverTax2Type(vo.getReceiverTax2Type());
                vo1.setSenderAccessFeeType(vo.getSenderAccessFeeType());
                vo1.setSenderTax1Type(vo.getSenderTax1Type());
                vo1.setSenderTax2Type(vo.getSenderTax2Type());


                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
                    vo1.setCosRequired(vo.getCosRequired());
                }
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue()) {
                    vo1.setInPromoAsString(vo.getInPromo());
                }
                BeanUtils.copyProperties(vo1, vo);
                tempList.add(vo1);

            }
            response.setTempCardGroupList(tempList);
            // Block end to handle special behaviour by arraylist in form.

            //forward = mapping.findForward("AddTemp");
        } catch (BTSLBaseException e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            throw e;
        } catch (Exception e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            throw e;
        } 
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting");
        }
        return response;
    }
 
  private C2STransferVO calculateTalkTime(HttpServletRequest request, CardGroupDetailsVO cardGroupDetailVO, long amount, String subServiceID, boolean checkMultipleOff) throws BTSLBaseException {
        final String METHOD_NAME = "calculateTalkTime";
        if (LOG.isDebugEnabled()) {
            LOG.debug("calculateTalkTime", "Entered amount:" + amount);
        }

        final C2STransferVO c2sTransferVO = new C2STransferVO();
        try {
            c2sTransferVO.setTransferValue(amount);
            c2sTransferVO.setRequestedAmount(amount);
            final C2STransferItemVO itemVO1 = new C2STransferItemVO();
            final C2STransferItemVO itemVO2 = new C2STransferItemVO();
            final Date currentDate = new Date();
            itemVO2.setPreviousExpiry(currentDate);
            itemVO2.setTransferDateTime(currentDate);
            itemVO2.setTransferDate(currentDate);
            final ArrayList itemList = new ArrayList();
            itemList.add(itemVO1);
            itemList.add(itemVO2);
            c2sTransferVO.setTransferItemList(itemList);
            // CardGroupBL.calculateC2SSenderValues(c2sTransferVO,cardGroupDetailVO);
            CardGroupBL.calculateC2SReceiverValues(c2sTransferVO, cardGroupDetailVO, subServiceID, checkMultipleOff);
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            throw new BTSLBaseException(this, "calculateTalkTime", be.getMessage(), 0, be.getArgs(), "Detail");
        } catch (Exception e) {
        	throw new BTSLBaseException(this, METHOD_NAME, "");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("calculateTalkTime", "Exiting");
        }

        return c2sTransferVO;
    }



   private void constructVOFromForm(CardGroupDetailsVO cardVO, CardGroupDetailsRequestVO theForm) throws Exception {
        final String methodName = "constructVOFromForm";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered Form params=" + theForm.toString());
        }

        cardVO.setCardGroupID(theForm.getCardGroupID());
        cardVO.setCardGroupCode(theForm.getCardGroupCode());
        if (!BTSLUtil.isNullString(String.valueOf(theForm.getStartRange()))) {
            cardVO.setStartRangeAsString(String.valueOf(theForm.getStartRange()));
        }

        if (!BTSLUtil.isNullString(String.valueOf(theForm.getEndRange()))) {
            cardVO.setEndRangeAsString(String.valueOf(theForm.getEndRange()));
        }

        cardVO.setValidityPeriodType(theForm.getValidityPeriodType());

        if (!BTSLUtil.isNullString(String.valueOf(theForm.getValidityPeriod()))) {
            cardVO.setValidityPeriod(theForm.getValidityPeriod());
        }

        if (!BTSLUtil.isNullString(String.valueOf(theForm.getGracePeriod()))) {
            cardVO.setGracePeriod(theForm.getGracePeriod());
        }

        // if(!BTSLUtil.isNullString(theForm.getMultipleOf()))
        // cardVO.setMultipleOf(Long.parseLong(theForm.getMultipleOf()));

        if (!BTSLUtil.isNullString(String.valueOf(theForm.getMultipleOf()))) {
            cardVO.setMultipleOfAsString(String.valueOf(theForm.getMultipleOf()));
        }

        cardVO.setReceiverTax1Name(theForm.getReceiverTax1Name());
        cardVO.setReceiverTax1Type(theForm.getReceiverTax1Type());

        if (!BTSLUtil.isNullString(String.valueOf(theForm.getReceiverTax1Rate()))) {
            cardVO.setReceiverTax1RateAsString(theForm.getReceiverTax1Rate());
        }
        cardVO.setReceiverTax2Name(theForm.getReceiverTax2Name());
        cardVO.setReceiverTax2Type(theForm.getReceiverTax2Type());

        if (!BTSLUtil.isNullString(String.valueOf(theForm.getReceiverTax2Rate()))) {
            cardVO.setReceiverTax2RateAsString(theForm.getReceiverTax2Rate());
        }

        cardVO.setReceiverAccessFeeType(theForm.getReceiverAccessFeeType());

        if (!BTSLUtil.isNullString(String.valueOf(theForm.getReceiverAccessFeeRate()))) {
            cardVO.setReceiverAccessFeeRateAsString(theForm.getReceiverAccessFeeRate());
        }

        if (!BTSLUtil.isNullString(String.valueOf(theForm.getMinReceiverAccessFee()))) {
            cardVO.setMinReceiverAccessFeeAsString(String.valueOf(theForm.getMinReceiverAccessFee()));
        }

        if (!BTSLUtil.isNullString(String.valueOf(theForm.getMaxReceiverAccessFee()))) {
            cardVO.setMaxReceiverAccessFeeAsString(String.valueOf(theForm.getMaxReceiverAccessFee()));
        }

        // added for card group slab suspend/resume
        if (!BTSLUtil.isNullString(theForm.getStatus())) {
            cardVO.setStatus(theForm.getStatus());
        }

        if (!BTSLUtil.isNullString(theForm.getOnline())) {
            cardVO.setOnline(theForm.getOnline());
        }

        if (!BTSLUtil.isNullString(theForm.getBoth())) {
            cardVO.setBoth(theForm.getBoth());
        }

        if (!BTSLUtil.isNullString(String.valueOf(theForm.getBonusValidityValue()))) {
            cardVO.setBonusValidityValue(theForm.getBonusValidityValue());
        }

        if (!BTSLUtil.isNullString(theForm.getReceiverConvFactor())) {
            cardVO.setReceiverConvFactor(theForm.getReceiverConvFactor());
        }

        if (!BTSLUtil.isNullString(theForm.getCardGroupSubServiceID())) {
            final String serviceID = theForm.getCardGroupSubServiceID();
            final int index = serviceID.indexOf(":");
            if (index != -1) {
                cardVO.setServiceTypeSelector(theForm.getCardGroupSubServiceID().replace(':', '_'));
            }
        }
        // added for cos by gaurav
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
            if (("N".equalsIgnoreCase(theForm.getCosRequired())) || BTSLUtil.isNullString(theForm.getCosRequired())) {
                cardVO.setCosRequired("N");
            } else {
                cardVO.setCosRequired("Y");
            }
        }
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue()) {
            if (!BTSLUtil.isNullString(String.valueOf(theForm.getInPromo()))) {
                cardVO.setInPromoAsString(theForm.getInPromo());
                // cardVO.setInPromo(Double.parseDouble(theForm.getInPromo()));
            }
        }

        cardVO.setCardName(theForm.getCardName());
        final DateFormat df2 = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
        cardVO.setReversalModifiedDate(df2.parse(BTSLDateUtil.getGregorianTimeStampInString(theForm.getReversalModifiedDateAsString())));
        cardVO.setReversalModifiedDateAsString(theForm.getReversalModifiedDateAsString());
        if (("N".equalsIgnoreCase(theForm.getReversalPermitted())) || BTSLUtil.isNullString(theForm.getReversalPermitted())) {
            cardVO.setReversalPermitted("N");
        } else {
            cardVO.setReversalPermitted("Y");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting: CardGroupDetailsVO=" + cardVO.toString());
        }
    }
   
   @Override
	public C2SCardGroupSetNameListResponseVO loadC2SCardGroupSetNameList(Connection con, String networkID,
			String serviceType, String SubserviceType) throws BTSLBaseException{
	
       final String methodName = "loadC2SCardGroupSetNameList";
       if (LOG.isDebugEnabled()) {
           LOG.debug(methodName, "Entered");
       }

		String defaultCardName = null;
		C2SCardGroupSetNameListResponseVO response = new C2SCardGroupSetNameListResponseVO();
		//response.setCurrentDefaultCardGroup(defaultCardName);
		
			final CardGroupDAO cardGroupDAO = new CardGroupDAO();
			final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
			// load the card group set names dropdown
			ArrayList cardGroupSetNameList = cardGroupDAO.loadCardGroupSet(con, networkID, PretupsI.C2S_MODULE);

			// load the card group set version dropdown
			final Date currentDate = new Date();
			ArrayList cardGroupSetVersionList =cardGroupSetDAO.loadCardGroupSetVersion(con, networkID, currentDate, PretupsI.C2S_MODULE);
			
			 final ArrayList list = new ArrayList();
	            if (cardGroupSetNameList != null && !BTSLUtil.isNullString(SubserviceType) && !BTSLUtil.isNullString(serviceType)) {
	                CardGroupSetVO cardGroupSetVO = null;
	                for (int i = 0, j = cardGroupSetNameList.size(); i < j; i++) {
	                    cardGroupSetVO = (CardGroupSetVO) cardGroupSetNameList.get(i);
	                    if (cardGroupSetVO.getSubServiceType().equals(SubserviceType.split(":")[1]) && cardGroupSetVO.getServiceType().equals(
	                        SubserviceType.split(":")[0]) && cardGroupSetVO.getServiceType().equals(serviceType))
	                    {
	                        if ((cardGroupSetVO.getDefaultCardGroup() != null) && (PretupsI.YES).equals(cardGroupSetVO.getDefaultCardGroup())) {
	                            defaultCardName = cardGroupSetVO.getCardGroupSetName();
	                        } else {
	                            list.add(cardGroupSetVO);
	                        }
	                    }
	                }
	            }
	            response.setCardGroupSetNameList(list);
	           response.setCurrentDefaultCardGroup(defaultCardName);

	            			
		
		return response;
	}

	@Override
	public LoadC2SCardGroupVersionListResponseVO loadVersionList(Connection con, String networkID,String service, String subService, String cardGroupSetType, Date dateTime ) throws Exception {
	        final String methodName = "loadVersionList";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered");
	        }
	    	LoadC2SCardGroupVersionListResponseVO response = new LoadC2SCardGroupVersionListResponseVO();
	        
			
	        		        
	            // set the card group set name dropdowm desc
	        	// load the card group set version

	           
	            final CardGroupSetDAO cardGroupSetNewDAO = new CardGroupSetDAO();
	            CardGroupDAO cardGroupDao = new CardGroupDAO();
	            
	            Calendar c = Calendar.getInstance(); 
	            c.setTime(dateTime); 
	            //c.add(Calendar.DATE, 1);
	            dateTime = c.getTime();
	            final ArrayList versionList = cardGroupSetNewDAO.loadCardGroupSetVersionNew(con, networkID, dateTime, PretupsI.C2S_MODULE);
	            if(versionList == null||versionList.isEmpty()) {
	            	throw new BTSLBaseException("C2SCardGroupServiceImpl",methodName,PretupsErrorCodesI.NO_DATA_FOUND_CRITERIA);
	            }
		        ArrayList cardGroupsetNamelist = cardGroupDao.loadCardGroupSet(con, networkID, PretupsI.C2S_MODULE);
		       // ArrayList cardGroupsetTypeList =cardGroupDao.loadCardGrou(con, networkID, methodName)
		        List<GroupSetDetails> groupSetDetailsList= new ArrayList();
		        
		        if (cardGroupsetNamelist != null && !cardGroupsetNamelist.isEmpty()) {
	                CardGroupSetVO cardGroupSetVO = null;
	                
	                for (int i = 0, j = cardGroupsetNamelist.size(); i < j; i++) {
	                	GroupSetDetails groupSetDetails = new GroupSetDetails();
	                    cardGroupSetVO = (CardGroupSetVO) cardGroupsetNamelist.get(i);
	                    if (cardGroupSetVO.getSetType().equals(cardGroupSetType) && cardGroupSetVO.getServiceType().equals(service)&&cardGroupSetVO.getSubServiceType().equals(subService)) {
	                    	groupSetDetails.setCardGroupSetName(cardGroupSetVO.getCardGroupSetName());
	                    	groupSetDetails.setCardGroupSubServiceName(cardGroupSetVO.getSubServiceTypeDescription());
	                    	groupSetDetails.setServiceTypedesc(cardGroupSetVO.getServiceTypeDesc());
	                    	groupSetDetails.setSetTypeName(cardGroupSetVO.getSetTypeName());
	                    	groupSetDetails.setDefaultCardGroupRequired(cardGroupSetVO.getDefaultCardGroup());
	                    	groupSetDetails.setCardGroupSetstatus(cardGroupSetVO.getStatus());
	                    	
	                    	 if (versionList != null && !versionList.isEmpty()) {
	         	                CardGroupSetVersionVO cardGroupSetVersionVO = null;
	         	                final ArrayList list = new ArrayList();
	         	                for (int k = 0, l = versionList.size(); k < l; k++) {
	         	                	VersionDetailsAndDeleteStatusVO versionDetailsAndDeleteStatusVO = new VersionDetailsAndDeleteStatusVO();
	         	                    cardGroupSetVersionVO = (CardGroupSetVersionVO) versionList.get(k);
	         	                    if (cardGroupSetVersionVO.getCardGroupSetID().equals(cardGroupSetVO.getCardGroupSetID())) {
	         	                    	
	         	                    	 final String[] arr = cardGroupSetVersionVO.getCardGroupSetCombinedID().split(":");
	         	                    	ArrayList cardGroupList = cardGroupDao.loadCardGroupDetailsListByID(con, arr[0], arr[1]);
	         	                    	StringBuffer strBuff = new StringBuffer();
	         	                    	final boolean isDeleteAllowed = checkDeleteVersionAllowed(cardGroupSetVO.getCardGroupSetID(), PretupsI.C2S_MODULE, strBuff);
	         	          	            if (isDeleteAllowed) {
	         	          	               	          	                
	         	          	            	if (cardGroupSetVersionVO.getApplicableFrom().after(new Date())) {
	         	          	            		versionDetailsAndDeleteStatusVO.setDeleteStatus(true);
	         	          	                } else {
	         	          	                	versionDetailsAndDeleteStatusVO.setDeleteStatus(false);
	         	          	                }
	         	          	            } else {
	         	          	            	versionDetailsAndDeleteStatusVO.setDeleteStatus(false);
	         	          	            }
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
	            response.setGroupDetailsList(groupSetDetailsList);
	            
	            
	            
	        
	            if (LOG.isDebugEnabled()) {
	                LOG.debug(methodName, "Exiting  forward=");
	            }
	        

	    
		return response;
	}
	
	public C2SCardGroupVersionNumbersListResponseVO loadVersionListBasedOnCardGroupSetIDAndDate(Connection con, String cardGroupSetID, Date dateTime ) throws Exception {
        final String methodName = "loadVersionListBasedOnCardGroupSetIDAndDate";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        C2SCardGroupVersionNumbersListResponseVO c2SCardGroupVersionNumbersListResponseVO =new C2SCardGroupVersionNumbersListResponseVO();
        final CardGroupSetDAO cardGroupSetNewDAO = new CardGroupSetDAO();
        CardGroupDAO cardGroupDao = new CardGroupDAO();
    	
		
        final ArrayList versionList = cardGroupSetNewDAO.loadCardGroupSetVersionNumbers(con, cardGroupSetID, dateTime);
        if(versionList.isEmpty()) {
        	throw new BTSLBaseException(PretupsErrorCodesI.NO_DATA_FOUND_FOR_FILTERS_CRITERIA);
        }
        c2SCardGroupVersionNumbersListResponseVO.setVersionList(versionList);
        c2SCardGroupVersionNumbersListResponseVO.setCardGroupSetID(cardGroupSetID);
        
        return c2SCardGroupVersionNumbersListResponseVO;
	}
	@Override
	public ViewC2SCardGroupResponseVO viewC2SCardGroupDetails(Connection con, String networkID, String selectCardGroupSetId, String version)throws BTSLBaseException, Exception {
		
	        final String methodName = "viewC2SCardGroupDetails";
	        StringBuilder loggerValue= new StringBuilder();
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered");
	        }
	        ViewC2SCardGroupResponseVO response = new ViewC2SCardGroupResponseVO();
	       
	        
	        	final CardGroupDAO cardGroupDAO = new CardGroupDAO();
	        	final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
				// load the card group set names dropdown
	        	
				ArrayList cardGroupSetNameList = cardGroupDAO.loadCardGroupSet(con, networkID, PretupsI.C2S_MODULE);

	        	response.setViewSlabCopy(String.valueOf(PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_CARD_GROUP_SLAB_COPY)));
	            // populate the drop downs
	        	response.setValidityTypeList(LookupsCache.loadLookupDropDown(PretupsI.VALIDITY_TYPE, true));
	            // ==============================================================================================
	            response.setSelectCardGroupSetId(selectCardGroupSetId);
	            response.setSelectCardGroupSetVersionId(version);
	           
	            // set the Card Group Set Name Info
	            CardGroupSetVO setVO = null;
	            StringBuilder cardGroupSuService=new StringBuilder();
	            String cardGroupSuSer;
	            for (int i = 0, j = cardGroupSetNameList.size(); i < j; i++) {
	                setVO = (CardGroupSetVO) cardGroupSetNameList.get(i);
	                if (response.getSelectCardGroupSetId().equals(setVO.getCardGroupSetID())) {
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
	                }
	            }

	            /*
	             * Card group Set Version drop down key = combination of groupId and
	             * version
	             * so we spilt the id and version
	             */
	            // set the Card Group Set Version Info
	            final CardGroupSetDAO cardGroupSetNewDAO = new CardGroupSetDAO();
	            CardGroupDAO cardGroupDao = new CardGroupDAO();
	        	
	    		
	            final Date applicableFromDate = cardGroupSetNewDAO.loadCardGroupSetVersionApplicableFromDate(con, selectCardGroupSetId, version);
	            
	                    response.setApplicableFromDate(BTSLUtil.getDateStringFromDate(applicableFromDate));
	                    final int hour = BTSLUtil.getHour(applicableFromDate);
	                    final int minute = BTSLUtil.getMinute(applicableFromDate);
	                    final String time = BTSLUtil.getTimeinHHMM(hour, minute);

	                    response.setApplicableFromHour(time);
	                    response.setOldApplicableFromDate(BTSLUtil.getDateStringFromDate(applicableFromDate));
	                    response.setOldApplicableFromHour(time);
	                    response.setVersion(version);
	           
	            // load the Card Group Details info
	            response.setCardGroupList(cardGroupDAO.loadCardGroupDetailsListByID(con, selectCardGroupSetId, version));

	            if (response.getCardGroupList() != null && !response.getCardGroupList().isEmpty()) {
	                for (int i = 0, j = response.getCardGroupList().size(); i < j; i++) {
	                    final CardGroupDetailsVO cardDetailVO = (CardGroupDetailsVO) response.getCardGroupList().get(i);
	                    if (LOG.isDebugEnabled()) {
	                    	loggerValue.setLength(0);
	                    	loggerValue.append("SlabStatusInForLoop=");
	                    	loggerValue.append(cardDetailVO.getStatus());
	                        LOG.debug(methodName,loggerValue);
	                    }
	                }
	            }
	            final ListSorterUtil sort = new ListSorterUtil();
	            final ArrayList sortedList = (ArrayList) sort.doSort("startRange", null, response.getCardGroupList());
	            response.setCardGroupList(sortedList);
	            final StringBuffer strBuff = new StringBuffer();
	            final boolean isDeleteAllowed = checkDeleteVersionAllowed(selectCardGroupSetId, PretupsI.C2S_MODULE, strBuff);
	            if (isDeleteAllowed) {
	                final String format = Constants.getProperty("CARDGROUP_DATE_FORMAT");
	                StringBuilder dateBuilder=new StringBuilder();
	                dateBuilder.append(response.getOldApplicableFromDate());
	                dateBuilder.append(" ");
	                dateBuilder.append(response.getOldApplicableFromHour());
	                String oldDat=dateBuilder.toString();
	                final Date oldDate = BTSLUtil.getDateFromDateString(oldDat, format);
	                if (oldDate.after(new Date())) {
	                    response.setDeleteAllowed(true);
	                } else {
	                    response.setDeleteAllowed(false);
	                }
	            } else {
	                response.setDeleteAllowed(false);
	            }

	          
	            if (response.getCardGroupList() != null && !response.getCardGroupList().isEmpty()) {
	                final CardGroupDetailsVO vo = (CardGroupDetailsVO) response.getCardGroupList().get(0);
	                vo.setBonusAccList(this.arrangeBonuses(vo.getBonusAccList(), response.getCardGroupSubServiceID(), true));
	                this.constructFormFromVO(response, vo);
	            }
		return response;
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

	
	 private void constructFormFromVO(ViewC2SCardGroupResponseVO theForm, CardGroupDetailsVO cardVO) throws Exception {
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
	        theForm.setReceiverTax1Rate(cardVO.getReceiverTax1RateAsString());
	        theForm.setReceiverTax2Name(cardVO.getReceiverTax2Name());
	        theForm.setReceiverTax2Type(cardVO.getReceiverTax2Type());
	        theForm.setReceiverTax2Rate(cardVO.getReceiverTax2RateAsString());
	        theForm.setReceiverAccessFeeType(cardVO.getReceiverAccessFeeType());
	        theForm.setReceiverAccessFeeRate(cardVO.getReceiverAccessFeeRateAsString());
	        theForm.setMinReceiverAccessFee(cardVO.getMinReceiverAccessFeeAsString());
	        theForm.setMaxReceiverAccessFee(cardVO.getMaxReceiverAccessFeeAsString());
	        // added for card group slab suspend/resume
	        theForm.setCGStatus(cardVO.getStatus());
	        theForm.setBonusValidityValue(String.valueOf(cardVO.getBonusValidityValue()));
	        theForm.setOnline(cardVO.getOnline());
	        theForm.setBoth(cardVO.getBoth());
	        theForm.setReceiverConvFactor(cardVO.getReceiverConvFactor());
	        theForm.setCardName(cardVO.getCardName());
	        theForm.setReversalPermitted(cardVO.getReversalPermitted());
	        theForm.setReversalModifiedDate(cardVO.getReversalModifiedDate());

	        final DateFormat dateFormat = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
	        theForm.setReversalModifiedDateAsString(BTSLDateUtil.getLocaleTimeStamp(dateFormat.format(cardVO.getReversalModifiedDate())));
	        theForm.setTempAccList(cardVO.getBonusAccList());
	        // added for cos by gaurav
	        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
	            theForm.setCosRequired(cardVO.getCosRequired());
	        }
	        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue()) {
	            theForm.setInPromo("" + cardVO.getInPromo());
	        }
	        // checkDisplayBundles(theForm);

	        if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Exiting: Form Params Populated from CardGroupDetailsVO:" + theForm.toString());
	        }
	    }

	 @Override
		public AddTempCardGroupListResponseVO addModifyC2SCardGroupTempList(Connection con,
				HttpServletRequest httpServletRequest, UserVO userVO, CardGroupDetailsRequestVO requestVO)throws Exception {
			final String methodName = "refreshCard";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered");
	        }
	        
	        AddTempCardGroupListResponseVO response = new AddTempCardGroupListResponseVO();
	        try {
	        	final CardGroupDAO cardDAO =  new CardGroupDAO();
	            

	            final CardGroupDetailsVO cardGroupDetailVO = new CardGroupDetailsVO();

	      
	            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
	                if (BTSLUtil.isNullString(requestVO.getCosRequired()) || "N".equalsIgnoreCase(requestVO.getCosRequired())) {
	                	requestVO.setCosRequired("N");
	                    cardGroupDetailVO.setCosRequired("N");
	                } else {
	                    requestVO.setCosRequired(requestVO.getCosRequired());
	                    cardGroupDetailVO.setCosRequired(requestVO.getCosRequired());
	                }
	            }
	            if (BTSLUtil.isNullString(requestVO.getReversalPermitted()) || "N".equalsIgnoreCase(requestVO.getReversalPermitted())) {
	                requestVO.setReversalPermitted("N");
	                cardGroupDetailVO.setReversalPermitted("N");
	            } else {
	                requestVO.setReversalPermitted(requestVO.getReversalPermitted());
	                cardGroupDetailVO.setReversalPermitted(requestVO.getReversalPermitted());
	            }
	            
	                Date currentDate = null;
	                final CardGroupDetailsVO cdgrpVO = (CardGroupDetailsVO) requestVO.getCardGroupList().get(0);
	            
	                currentDate = new Date();
	                if (!cardDAO.checkForReversalPermittedChange(con, cdgrpVO.getCardGroupSetID(), requestVO.getCardGroupID(), requestVO.getReversalPermitted())) {
	                    requestVO.setReversalModifiedDate(currentDate);
	                    final DateFormat dateFormat = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
	                    final Calendar cal = BTSLDateUtil.getInstance();
	                    requestVO.setReversalModifiedDateAsString(BTSLDateUtil.getLocaleTimeStamp(dateFormat.format(cal.getTime())));
	                }
	            
	            // populate VO contents from Form
	            this.constructVOFromForm(cardGroupDetailVO, requestVO);

	            // added for suspend/resume of slab
	            /*
	             * if((BTSLUtil.isNullString(request.getParameter("suspendSlab")) &&
	             * !(request.getParameter("suspendSlab")=="")) ||
	             * (BTSLUtil.isNullString(request.getParameter("resumeSlab")) &&
	             * !(request.getParameter("resumeSlab")=="")))
	             * return forward = mapping.findForward("AddTemp");
	             */

	            // validate the slabs
	            /*
	             * Here we ppopulate the list
	             * if locationIndex == -1
	             * user send a request for addnew card group, so we are adding the
	             * VO into the list
	             * else
	             * set the modified VO from the form into the list
	             */

	            cardGroupDetailVO.setEditDetail("N");
	            // Added for bonus bundles
	            if (requestVO.getTempAccList() != null && !requestVO.getTempAccList().isEmpty()) {
	                ArrayList tempsAcclist = null;
	                tempsAcclist = requestVO.getBonusAccList();
	                if (tempsAcclist == null) {
	                    tempsAcclist = new ArrayList();
	                }
	                tempsAcclist.addAll(requestVO.getTempAccList());
	                cardGroupDetailVO.setBonusAccList(tempsAcclist);
	            }
	            ArrayList list = null;
	            if (requestVO.getLocationIndex() <= -1) {
	                if (requestVO.getCardGroupList() == null || requestVO.getCardGroupList().isEmpty()) {
	                    list = new ArrayList();
	                    list.add(cardGroupDetailVO);
	                } else {
	                    list = new ArrayList(requestVO.getCardGroupList());
	                    list.add(cardGroupDetailVO);
	                }
	            } else {
	                list = new ArrayList(requestVO.getCardGroupList());
	                 

	               
	                // add for suspend/resume slab on 03/09/08
	                final String status = requestVO.getRequest();
	                if (status != null) {
	                    if (("Suspend".equalsIgnoreCase(status))) {
	                    	
	                    	cardGroupDetailVO.setStatus("S");
	                    } else if ("Resume".equalsIgnoreCase(status)) {
	                    	cardGroupDetailVO.setStatus("Y");

	                    	
	                    }
	                }
	                // add for card group slab deletion on 09/04/2007
	                if (status.equalsIgnoreCase("delete")) {
	                    if (list.size() == 1) {
	                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CARDGROUP_CARDGROUP_C2S_DETAILS_INVALIDSLAB, "Detail");
	                    }
	                    if ("Y".equals(cardGroupDetailVO.getStatus())) {
	                        list.remove(requestVO.getLocationIndex());
	                    }
	                }
	                else {
	                
	                    if ("Y".equals(cardGroupDetailVO.getStatus())) {
	                        list.set(requestVO.getLocationIndex(), cardGroupDetailVO);
	                    }
	                }

	            
	            }
	           
	            final ListSorterUtil sort = new ListSorterUtil();
	            list = (ArrayList) sort.doSort("startRange", null, list);
	            String startRangeLabel = null;

	            if (list != null && !list.isEmpty()) {
	                CardGroupDetailsVO preCardVO = (CardGroupDetailsVO) list.get(0);
	                String endRangeLabel = null;
	                CardGroupDetailsVO nextCardVO = null;
	                CardGroupDetailsVO currCrdVO = null;
	                currCrdVO = (CardGroupDetailsVO) list.get(list.size() - 1);
	                for (int i = 1, j = list.size(); i < j; i++) {
	                    nextCardVO = (CardGroupDetailsVO) list.get(i);
	                    if (nextCardVO.getStartRange() <= preCardVO.getEndRange()) {
	                    	  startRangeLabel =PretupsI.START_RANGE; 
		                        endRangeLabel = PretupsI.END_RANGE;
	                        final String[] arr = { startRangeLabel, String.valueOf(i + 1), endRangeLabel, String.valueOf(i) };
	                        throw new BTSLBaseException(this, methodName, "cardgroup.cardgroupc2sdetails.error.invalidslab", 0, arr, "Detail");
	                    }
	                    if (currCrdVO.getCardGroupCode().equalsIgnoreCase(preCardVO.getCardGroupCode())) {
							if(!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DUPLICATE_CARDGROUP_CODE_ALLOW)).booleanValue()){
								throw new BTSLBaseException(this, methodName, "cardgroup.cardgroupdetails.error.cardgroupcode", "Detail");
								}
	                    } else if (currCrdVO.getCardName().equalsIgnoreCase(preCardVO.getCardName())) {
	                        throw new BTSLBaseException(this, methodName, "cardgroup.cardgroupdetails.error.slabname", "Detail");
	                    }
	                    preCardVO = nextCardVO;
	                }
	            }

	            // check the start range value(Talk time is valid or not)
	            final C2STransferVO c2sTransferVO = this.calculateTalkTime(httpServletRequest, cardGroupDetailVO, cardGroupDetailVO.getStartRange(), requestVO.getCardGroupSubServiceID()
	                .split(":")[1], false);// update
	            // by
	            // shishu

	            /*
	             * if(c2sTransferVO.getSenderTransferValue()<0)
	             * {
	             * //invalid talk time
	             * String startRangeLabel =
	             * this.getResources(request).getMessage(BTSLUtil
	             * .getBTSLLocale(request
	             * ),"cardgroup.cardgroupdetails.label.startrange");
	             * String[] arr =
	             * {startRangeLabel,String.valueOf(c2sTransferVO.getSenderTransferValue
	             * ())};
	             * throw new BTSLBaseException(this,"refreshCard",
	             * "cardgroup.cardgroupdetails.error.invalidsendertransfervalue"
	             * ,0,arr,"Detail");
	             * }
	             */

	            if (c2sTransferVO.getReceiverTransferValue() < 0) {
	                // invalid talk time
	                final String[] arr = { PretupsBL.getDisplayAmount(c2sTransferVO.getReceiverTransferValue()) };
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_RECEIVER_TRANSFER_VALUE_SLABWISE, 0, arr, "Detail");
	            }

	            // if above validation succed, copy the list contents on the form
	            // Block to handle special behaviour by arraylist in form added on
	            // 16/10/2008 .
	            final ArrayList tempList = new ArrayList();
	            final Iterator itr = list.iterator();
	            CardGroupDetailsVO vo = null;
	            CardGroupDetailsVO vo1 = null;
	            while (itr.hasNext()) {
	                vo = (CardGroupDetailsVO) itr.next();
	                vo1 = new CardGroupDetailsVO();
	                vo1.setReceiverAccessFeeType(vo.getReceiverAccessFeeType());
	                vo1.setReceiverTax1Type(vo.getReceiverTax1Type());
	                vo1.setReceiverTax2Type(vo.getReceiverTax2Type());
	                vo1.setSenderAccessFeeType(vo.getSenderAccessFeeType());
	                vo1.setSenderTax1Type(vo.getSenderTax1Type());
	                vo1.setSenderTax2Type(vo.getSenderTax2Type());

	                // added by gaurav for cos
	                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
	                    vo1.setCosRequired(vo.getCosRequired());
	                }
	                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue()) {
	                    vo1.setInPromoAsString(vo.getInPromo());
	                }
	                BeanUtils.copyProperties(vo1, vo);
	                tempList.add(vo1);
	            }
	            response.setTempCardGroupList(tempList);
	           
	            
	           
	            } catch (Exception e) {
	            LOG.error(methodName, "Exceptin:e=" + e);
	            LOG.errorTrace(methodName, e);
	            throw e;
	        } 
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Exiting");
	        }

			return response;
		}
	 
		@Override
		public Integer deleteTempC2SCardGroupList(Connection con,MComConnectionI mcomCon, String selectCardGroupSetId, String version, String oldApplicableFromDate, String oldApplicableFromHour ) throws Exception {
		        final String methodName = "deleteTempC2SCardGroupList";
		        StringBuilder loggerValue= new StringBuilder(); 
		        if (LOG.isDebugEnabled()) {
		            LOG.debug(methodName, "Entered");
		        }
		        Integer deleted =0;

		        		final StringBuffer strBuff = new StringBuffer();
		                final boolean isDeleteAllowed = checkDeleteVersionAllowed(selectCardGroupSetId, PretupsI.C2S_MODULE, strBuff);
		                if (isDeleteAllowed) {
		                    final String format = Constants.getProperty("CARDGROUP_DATE_FORMAT");
		                    final Date oldDate = BTSLUtil.getDateFromDateString(oldApplicableFromDate + " " + oldApplicableFromHour, format);
		                    if (oldDate.after(new Date())) {
					
		                        final CardGroupSetVersionDAO cardgroupSetVersionDAO = new CardGroupSetVersionDAO();
		                         deleted = cardgroupSetVersionDAO.deleteVersion(con, selectCardGroupSetId, version);
		                        if (deleted > 0) {
		                            con.commit();
		                        } else {
		                            con.rollback();
		                            LOG.error("deleteVersion", "Error: while Deleting Card_Group_Set_Version");
		                            throw new BTSLBaseException(this, "deleteVersion", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
		                        }
		                       
		                    } else {
		                    	LOG.error(methodName, "The version is not of future so can not be deleted");
                                throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.FUTURE_CARD_GROUP_CANNOT_BE_DELETED);		                    }
		                } else {
		                   
		                    if ("1".equals(version)) {
		                        LOG.error(methodName, "This version is only version of its card group set so can not be deleted");

                                throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.FUTURE_CARD_GROUP_CANNOT_BE_DELETED);		                    } else {
		                        LOG.error(methodName, "This record has been modified by someone else, please reload the data.");
		                       
		                        throw new BTSLBaseException(this, "deleteVersion", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
		                    }
		                }
		             
		        return deleted;
		    

		}

		@Override
		public UpdateSaveC2SCardGroupResponseVO modifySaveC2SCardGroup(Connection con, HttpServletRequest request, UserVO userVO, C2SAddCardGroupSaveRequestVO requestVO) throws BTSLBaseException, Exception {
			final String methodName = "modifySaveC2SCardGroup";
	        StringBuilder loggerValue= new StringBuilder(); 
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered");
	        }
	        UpdateSaveC2SCardGroupResponseVO response = new UpdateSaveC2SCardGroupResponseVO();
	        boolean alreadyRunning = false;
	      //added for choice recharge
			Connection con1 = null;
			MComConnectionI mcomCon1 = null;
			 
			final ArrayList cardGroupList1 = requestVO.getCardGroupList();
	            final Iterator iterator = cardGroupList1.iterator();
	            int count = 0;
	            String slabCount = null;
	            while (iterator.hasNext()) {
	                count++;
	                slabCount = String.valueOf(count);
	                final CardGroupDetailsVO cardGroupDetailVO = (CardGroupDetailsVO) iterator.next();

	                if (!BTSLUtil.isNullString(requestVO.getCardGroupSubServiceID())) {
	                    final String serviceID = requestVO.getCardGroupSubServiceID();
	                    final int index = serviceID.indexOf(":");
	                    if (index != -1) {
	                        cardGroupDetailVO.setServiceTypeSelector(requestVO.getCardGroupSubServiceID().replace(':', '_'));
	                    }
	                }
	                // check the start range value(Talk time is valid or
	                // not)
	               
	                ArrayList bounsAccList= cardGroupDetailVO.getBonusAccList();
	                
	                for(int i=0;i<bounsAccList.size(); i++) {
	                	BonusAccountDetailsVO vo = new BonusAccountDetailsVO();
	                	 LinkedHashMap<Object, Object> l =(LinkedHashMap) bounsAccList.get(i);
	                	 
	                	 for (Map.Entry<Object, Object> it : l.entrySet()) {
	                		 String key = it.getKey().toString();
	                		 if( key.equals(PretupsI.CARDGROUP_SET_ID)) {
	                			 if(it.getValue() != null)
	                			 vo.setCardGroupSetID(it.getValue().toString());
	                		 }
	                		 if( key.equals(PretupsI.VERSION)) {
	                			 if(it.getValue() != null)
	                			 vo.setVersion(it.getValue().toString());
	                		 }
	                		 if( key.equals(PretupsI.CARDGROUP_ID)) {
	                			 if(it.getValue() != null)
	                			 vo.setCardGroupID(it.getValue().toString());
	                		 }
	                		 if( key.equals(PretupsI.BUNDLE_ID)) {
	                			 if(it.getValue() != null)
	                			 vo.setBundleID(it.getValue().toString());
	                		 }
	                		 if( key.equals(PretupsI.TYPE)) {
	                			 if(it.getValue() != null)
	                			 vo.setType(it.getValue().toString());
	                		 }
	                		 if( key.equals(PretupsI.BONUS_VALIDITY)) {
	                			 if(it.getValue() != null)
	                			 vo.setBonusValidity(it.getValue().toString());
	                		 }
	                		 if( key.equals(PretupsI.BONUS_VALUE)) {
	                			 if(it.getValue() != null)
	                			 vo.setBonusValue(it.getValue().toString());
	                		 }
	                		 if( key.equals(PretupsI.MULT_FACTOR)) {
	                			 if(it.getValue() != null)
	                			 vo.setMultFactor(it.getValue().toString());
	                		 }
	                		 if( key.equals(PretupsI.BONUS_NAME)) {
	                			 if(it.getValue() != null)
	                			 vo.setBonusName(it.getValue().toString());
	                		 }
	                		 if( key.equals(PretupsI.BUNDLE_TYPE)) {
	                			 if(it.getValue() != null)
	                			 vo.setBundleType(it.getValue().toString());
	                		 }
	                		 if( key.equals(PretupsI.RESTRICTED_ON_IN)) {
	                			 if(it.getValue() != null)
	                			 vo.setRestrictedOnIN(it.getValue().toString());
	                		 }
	                		 if( key.equals(PretupsI.BONUS_ACC_DETAIL_LIST)) {
	                			 if(it.getValue() != null)
	                			 vo.setBonusAccDetailList((ArrayList)it.getValue());
	                		 }
	                		 if( key.equals(PretupsI.BOUNUS_CODE)) {
	                			 if(it.getValue() != null)
	                			 vo.setBonusCode(it.getValue().toString());
	                		 }
	                		
	                	}
	                	bounsAccList.set(i, vo);
	                }
	                
	                cardGroupDetailVO.setBonusAccList(bounsAccList);
	                final C2STransferVO c2sTransferVO = this.calculateTalkTime(request, cardGroupDetailVO, cardGroupDetailVO.getStartRange(), requestVO
	                    .getCardGroupSubServiceID().split(":")[1], false);// update
	              
	                if (c2sTransferVO.getReceiverTransferValue() < 0) {
	                    // invalid talk time
	                    final String[] arr = { PretupsBL.getDisplayAmount(c2sTransferVO.getReceiverTransferValue()), slabCount };
	                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_RECEIVER_TRANSFER_VALUE_SLABWISE, 0, arr, "Success");
	                }

	            }
	            final ArrayList sortedList1 = requestVO.getCardGroupList();
            Collections.sort(sortedList1, (Comparator<CardGroupDetailsVO>) (o1, o2) -> Integer.compare((int) o1.getStartRange(), (int) o2.getStartRange()));
	            String startRangeLabel = PretupsI.START_RANGE;
	            if (sortedList1 != null && !sortedList1.isEmpty()) {
	                CardGroupDetailsVO preCardVO = (CardGroupDetailsVO) sortedList1.get(0);
	                preCardVO.setRowIndex(1);
	                String endRangeLabel = null;
	                CardGroupDetailsVO nextCardVO = null;
	                for (int i = 1, j = sortedList1.size(); i < j; i++) {
	                    nextCardVO = (CardGroupDetailsVO) sortedList1.get(i);
	                    nextCardVO.setRowIndex(i + 1);
	                    if (nextCardVO.getStartRange() <= preCardVO.getEndRange()) {
	                        startRangeLabel = PretupsI.START_RANGE;;
	                        endRangeLabel = PretupsI.END_RANGE;;
	                        final String[] arr1 = { startRangeLabel, String.valueOf(i + 1), endRangeLabel, String.valueOf(i) };
	                        requestVO.setSelectCardGroupSetId(null);
	                        throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.CARDGROUP_CARDGROUP_C2S_DETAILS_INVALIDSLAB, 0, arr1, "Success");
	                    }
	                    preCardVO = nextCardVO;
	                }
	            }
	            final CardGroupDAO cardDAO = new CardGroupDAO();
	            CardGroupDAO cardGroupDAO = new CardGroupDAO();
	            CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
	            CardGroupSetVersionDAO cardGroupSetVersionDAO = new CardGroupSetVersionDAO();
	            final Date currentDate = new Date();
	            ProcessStatusVO  processVO=null;
                ProcessStatusDAO processDAO = new ProcessStatusDAO();
                ProcessBL processBL=new ProcessBL();

	            ArrayList cardGroupSetNameList = cardGroupDAO.loadCardGroupSet(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);

	            ArrayList cardGroupSubServiceList = ServiceSelectorMappingCache.loadSelectorDropDownForCardGroup();
	            ArrayList serviceTypeList = new CardGroupDAO().loadServiceTypeList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE); 
	            ArrayList setTypeList = LookupsCache.loadLookupDropDown(PretupsI.CARD_GROUP_SET_TYPE, true);
	            final ArrayList versionList = cardGroupSetDAO.loadCardGroupSetVersion(con, userVO.getNetworkID(), currentDate, PretupsI.C2S_MODULE);
	            

                final String format = Constants.getProperty("CARDGROUP_DATE_FORMAT");
                String fromHour = null;
                if (BTSLUtil.isNullString(requestVO.getApplicableFromHour())) {
                    fromHour = "00:00";
                } else {
                    fromHour = requestVO.getApplicableFromHour();
                }
                final Date newDate = BTSLUtil.getDateFromDateString(requestVO.getApplicableFromDate() + " " + fromHour, format);
                final Date oldDate = BTSLUtil.getDateFromDateString(requestVO.getOldApplicableFromDate() + " " + requestVO.getOldApplicableFromHour(), format);
                int version = 0;
                
                if (oldDate.getTime() != newDate.getTime()){// we need to
                // insert the new
                // version                {
                    
                    CardGroupSetVO setVO = null;
                    // get the selected card group set from the
                    // CardGroupSetNameList
                    for (int i = 0, j = cardGroupSetNameList.size(); i < j; i++) {
                        setVO = (CardGroupSetVO) cardGroupSetNameList.get(i);
                        if (requestVO.getSelectCardGroupSetId().equals(setVO.getCardGroupSetID())) {
                            version = Integer.parseInt(setVO.getLastVersion());
                            break;
                        }
                    }

                    final String[] arr = { String.valueOf(version + 1) };
                  
                }
                
        ///update save
            
            CardGroupSetVO setVO = null;
            // get the selected card group set from the
            // CardGroupSetNameList
            for (int i = 0, j = cardGroupSetNameList.size(); i < j; i++) {
                setVO = (CardGroupSetVO) cardGroupSetNameList.get(i);
                if (requestVO.getSelectCardGroupSetId().equals(setVO.getCardGroupSetID())) {
                    break;
                }
            }
            // check whether the Card Group Name is already exist or not
            if (cardGroupSetDAO.isCardGroupSetNameExist(con, userVO.getNetworkID(), requestVO.getCardGroupSetName(), setVO.getCardGroupSetID())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CARD_GROUP_NAME_ALREADY_EXIST, "DetailView");
            }

            
//          //  final String format = Constants.getProperty("CARDGROUP_DATE_FORMAT");
//            String fromHour2 = null;
//            if (BTSLUtil.isNullString(requestVO.getApplicableFromHour())) {
//                fromHour = "00:00";
//            } else {
//                fromHour = requestVO.getApplicableFromHour();
//            }
            StringBuilder newDateBuilder =new StringBuilder();
            newDateBuilder.append(requestVO.getApplicableFromDate());
            newDateBuilder.append(" ");
            newDateBuilder.append(fromHour);
            String newDat=newDateBuilder.toString();
            final Date newDate2 = BTSLUtil.getDateFromDateString(newDat , format);
            // check whether the Card Group Set is already exist with
            // the same applicable date of the same set id
            if (cardGroupSetVersionDAO.isCardGroupAlreadyExist(con, newDate2, setVO.getCardGroupSetID(), requestVO.getVersion())) {
                final String[] arr = { BTSLUtil.getDateTimeStringFromDate(newDate2) };
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CARD_DETAILS_ALREADY_EXIST, 0, arr, "DetailView");
            }
            StringBuilder oldDateBuilder =new StringBuilder();
            oldDateBuilder.append(requestVO.getOldApplicableFromDate());
            oldDateBuilder.append(" ");
            oldDateBuilder.append(requestVO.getOldApplicableFromHour());
            String oldDat=oldDateBuilder.toString();
            final Date oldDate2 = BTSLUtil.getDateFromDateString(oldDat, format);
            /*
             * if the below conditon is true means user change the
             * applicable from date and we
             * need to insert a new version
             */
            if (oldDate2.getTime() != newDate2.getTime())// we need to
            // insert the new
            // version
            {
                version = Integer.parseInt(setVO.getLastVersion()) + 1;
                requestVO.setVersion(String.valueOf(version));
                Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
                String arr[]= {requestVO.getCardGroupSetName(), String.valueOf(version)};
        		String resmsg = RestAPIStringParser.getMessage(locale,
        				PretupsErrorCodesI.MODIFIED_CARD_GROUP_VERSION_NUMBER, arr);
        		response.setIsVersionCreated(resmsg);

                
            } else// we need to update the same version
            {
                version = Integer.parseInt(setVO.getLastVersion());
            }

            // update version in Card_Group_Set table

            setVO.setCardGroupSetName(requestVO.getCardGroupSetName());
            setVO.setLastVersion(String.valueOf(version));
            setVO.setModifiedOn(currentDate);
            setVO.setModifiedBy(userVO.getUserID());
            setVO.setSetType(requestVO.getSetType());
            setVO.setDefaultCardGroup(requestVO.getDefaultCardGroup());
            // setVO.setSubServiceType(theForm.getCardGroupSubServiceID()
            // );
            
            String defaultCardGroupID = null;
            List<String> defaultCardGroupList = (cardGroupDAO.loadDefaultCardGroup(con, requestVO.getServiceTypeID(), requestVO.getCardGroupSubServiceID().split(":")[1], PretupsI.YES,userVO.getNetworkID()));
            if(defaultCardGroupList!=null && !defaultCardGroupList.isEmpty())
            	defaultCardGroupID = defaultCardGroupList.get(0);

            int updateCount = cardGroupSetDAO.updateCardGroupSet(con, setVO);
            if (updateCount <= 0) {
                try {
                    con.rollback();
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
                LOG.error(methodName, "Error: while Updating Card_Group_Set");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
            }

            if(requestVO.getDefaultCardGroup().equals(PretupsI.YES) && defaultCardGroupID!=null) {
            	boolean status =false;
            	try {
            		status = cardGroupSetDAO.updateAsDefault(con, defaultCardGroupID, setVO.getCardGroupSetID(), userVO.getUserID(), currentDate);
            	}catch (Exception e) {
                    con.rollback();
                    LOG.errorTrace(methodName, e);
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.UNABLE_TO_MAKE_DEFAULT_AS_NO, "");
                }
          		if(status) {
          			con.commit();
            		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
                    String arr[]= { requestVO.getCardGroupSetName()};
            		String resmsg = RestAPIStringParser.getMessage(locale,
            				PretupsErrorCodesI.SUCCESSFULLY_CHANGE_DEFAULT_CARD_GROUP, arr);
                    final AdminOperationVO adminOperationVO = new AdminOperationVO();
					adminOperationVO.setSource(PretupsI.LOGGER_OPERATION_MAKE_DEFAULT_CARDGROUP);
					adminOperationVO.setDate(currentDate);
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
					adminOperationVO.setInfo(resmsg);
					adminOperationVO.setLoginID(userVO.getLoginID());
					adminOperationVO.setUserID(userVO.getUserID());
					adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					adminOperationVO.setNetworkCode(userVO.getNetworkID());
					adminOperationVO.setMsisdn(userVO.getMsisdn());
					AdminOperationLog.log(adminOperationVO);
            	}
            	else {
            		con.rollback();
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.SELECTED_CARD_GROUP_SET_IS_UNABLE_TO_MAKE_DEFAULT, "");
            	}
            }

            // changes for choice recharge
            ArrayList cardGroupList= requestVO.getCardGroupList();
			Iterator itr = cardGroupList.iterator();
			CardGroupDetailsVO cardGroupDetailVO = null;
			List<CardGroupDetailsVO> dblist1=new ArrayList<CardGroupDetailsVO>();
			
			boolean systemStatusOK = false;
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHOICE_RECHARGE_APPLICABLE)).booleanValue() && PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equals(requestVO.getServiceTypeID()))
			{   
				try {
					mcomCon1 = new MComConnection();
					con1 = mcomCon1.getConnection();
				processVO=processBL.checkProcessUnderProcessNetworkWise(con1,PretupsI.BAT_MOD_C2S_CG_PROCESS_ID,userVO.getNetworkID());
				systemStatusOK = processVO.isStatusOkBool();
				
				if(!systemStatusOK)  { 
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CARD_GROUP_CANNOT_BE_ADDED_FOR_CHOICE_RECHARGE_LOCK_FAILED,"DetailView"); 
				}
				con1.commit();
				}catch(Exception e)
				{  
					LOG.errorTrace(methodName, e);
					alreadyRunning = true;
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CARD_GROUP_CANNOT_BE_ADDED_FOR_CHOICE_RECHARGE_LOCK_FAILED,"DetailView"); 
				}
				
				while(itr.hasNext())
				{
					cardGroupDetailVO = (CardGroupDetailsVO) itr.next();
					dblist1=cardGroupDAO.validateCardGroupDetailsForChoiceRecharge(String.valueOf(cardGroupDetailVO.getStartRange()),String.valueOf(cardGroupDetailVO.getEndRange()),requestVO.getCardGroupSubServiceID(),userVO.getNetworkID());
					if(dblist1!=null)
					{
						choiceRechargeValidation(dblist1,cardGroupDetailVO,con1);
					}
				}
			}
            //choice recharge changes end here
			
            /*
             * if the below conditon is true means user change the
             * applicable from date and we
             * need to insert a new version
             */
            if (oldDate.getTime() != newDate.getTime())// we need to
            // insert the new
            // version
            {
                // insert data into the card_group_details and
                // card_group_set_versions table
                this.add(requestVO, userVO, con, setVO, currentDate);
            } else// we need to update the same version
            {
                // update the card_group_set_version
                this.updateSetVersion(versionList, requestVO.getSelectCardGroupSetVersionId(), userVO, con, cardGroupSetVersionDAO, currentDate);
                // delete data from the card_group_details
                int deleteCount = cardGroupSetVersionDAO.deleteCardGroupDetails(con, setVO.getCardGroupSetID(), requestVO.getVersion());
                if (deleteCount < 0) {
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        LOG.errorTrace(methodName, e);
                        throw e;
                    }
                    LOG.error(methodName, "Error: while Deleting Card_Group_Details");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
                }
                try {
                    deleteCount = 0;
                    final BonusBundleDAO bonusBundleDAO = new BonusBundleDAO();
                    deleteCount = bonusBundleDAO.deletePreviousBonus(con, setVO.getCardGroupSetID(), requestVO.getVersion());

                } catch (Exception sqe) {
                    // Create New message and paste here
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        LOG.errorTrace(methodName, e);
                        throw e;
                    }
                    LOG.error(methodName, "Error: while Deleting Card_Group_Details");
                    LOG.errorTrace(methodName, sqe);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
                }
                if (deleteCount <= 0) {
                    // Create New message and paste here
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        LOG.errorTrace(methodName, e);
                        throw e;
                    }
                    LOG.error(methodName, "Error: while Deleting Card_Group_Details");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
                }
                // set the default valus in the detail VO
                if (requestVO.getCardGroupList() != null && !requestVO.getCardGroupList().isEmpty()) {
                    // set the default values
                    for (int i = 0, j = requestVO.getCardGroupList().size(); i < j; i++) {
                        final CardGroupDetailsVO cardDetailVO = (CardGroupDetailsVO) requestVO.getCardGroupList().get(i);
                        cardDetailVO.setCardGroupSetID(setVO.getCardGroupSetID());
                        cardDetailVO.setCardGroupID(String.valueOf(IDGenerator.getNextID(PretupsI.CARD_GROUP_ID, TypesI.ALL)));
                        cardDetailVO.setVersion(requestVO.getVersion());
                        cardDetailVO.setSenderConvFactor("1");
                        cardDetailVO.setReceiverConvFactor(cardDetailVO.getReceiverConvFactor());
                    }
                    // insert Card_Group_Details
                    final int insertDetailCount = cardDAO.addCardGroupDetails(con, requestVO.getCardGroupList());

                    if (insertDetailCount <= 0) {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            LOG.errorTrace(methodName, e);
                        }
                        LOG.error(methodName, "Error: while Inserting Card_Group_Details");
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
                    }
                }
            }

            final List messageList = new ArrayList();
            List messageListForChoiceRecharge =new ArrayList();
            KeyArgumentVO keyArg = new KeyArgumentVO();
            keyArg.setKey("cardgroup.cardgroupdetailsview.successeditmessage");
            messageList.add(keyArg);
            KeyArgumentVO keyArg1= new KeyArgumentVO();
			keyArg1.setKey("cardgroup.cardgroupdetailsview.successeditmessage.forchoicerecharge");
			messageListForChoiceRecharge.add(keyArg1);
            final ArrayList cardList = requestVO.getCardGroupList();
            ArrayList cardSlabList = null;
            String[] slabToSetArray = null;
            int i = 0;
            final Iterator itr4 = cardList.iterator();
            Iterator itr1 = null;
            CardGroupDetailsVO cardDetail = null;
            CardGroupDetailsVO cardDetailVO = null;
            CardGroupDAO cardGroupDao = null;
            CardGroupSetVersionVO setVersionVO = null;
            setVO = null;
            String lastVersion = null;
            ListSorterUtil sort = new ListSorterUtil();
            final int size = cardList.size() * cardGroupSetNameList.size();
            final String[] notCopiedSets = new String[size];
            final String[] copiedSets = new String[size];
            boolean isValidSlab = false;
            CardGroupDetailsVO preCardVO = null;
            ArrayList sortedList = null;
            CardGroupDetailsVO nextCardVO = null;
            Iterator itr2 = null;
            version = 0;
            updateCount = 0;
            String currentDateString = null;
            CardGroupSetVO newSetVO;
            Date applicableFrom = null;
            Set keyForCardGroupSet = null;
            int deleteCount = 0;
            String cardGroupSetId = null;
            String setName = null;
            final Map setsCopySlabs = new HashMap(); // will store the
                                                     // card
            // group set ID and
            // corresponding to be
            // copied slabs as key
            // value
            ArrayList slabList = null;

            Iterator keyForCardGroupSetIterator = null;
            ArrayList newSlabsList = null;

            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATE_HHMM);

            int count1 = 0;
            int count2 = 0;
            ArrayList tempSlabList = new ArrayList();

            while (itr4.hasNext())// loop for all the slabs in the
            // current cardgroupset
            {
                cardDetail = (CardGroupDetailsVO) itr4.next();
                slabToSetArray = cardDetail.getCardGroupList();
                if (slabToSetArray != null) {
                    for (int p = 0; p < slabToSetArray.length; p++) {
                        if (!setsCopySlabs.containsKey(slabToSetArray[p])) {
                            slabList = new ArrayList();
                            slabList.add(cardDetail);
                            setsCopySlabs.put(slabToSetArray[p], slabList);
                        } else {
                            slabList = new ArrayList();
                            tempSlabList = (ArrayList) setsCopySlabs.get(slabToSetArray[p]);
                            slabList.addAll(tempSlabList);
                            slabList.add(cardDetail);
                            setsCopySlabs.put(slabToSetArray[p], slabList);
                        }
                    }
                }
            }

            keyForCardGroupSet = setsCopySlabs.keySet();
            keyForCardGroupSetIterator = keyForCardGroupSet.iterator();
            i = 0;
            while (keyForCardGroupSetIterator.hasNext()) {
                cardGroupSetId = (String) keyForCardGroupSetIterator.next();

                isValidSlab = true;
                cardGroupDao = new CardGroupDAO();
                itr1 = requestVO.getCardGroupSetVersionList().iterator();
                itr2 = requestVO.getCardGroupSetNameList().iterator();

                while (itr1.hasNext()) { // getting the latest version
                    // for the particular set in
                    // which we want to copy the
                    // slab
                    setVersionVO = (CardGroupSetVersionVO) itr1.next();
                    if (setVersionVO.getCardGroupSetID().equals(cardGroupSetId)) {
                        lastVersion = setVersionVO.getVersion();
                        break;
                    }
                }
                while (itr2.hasNext()) {
                    setVO = (CardGroupSetVO) itr2.next();
                    if (setVO.getCardGroupSetID().equals(cardGroupSetId)) {
                        break;
                    }
                }

                newSetVO = new CardGroupSetVO();
                newSetVO.setModuleCode(setVO.getModuleCode());
                newSetVO.setCardGroupSetID(setVO.getCardGroupSetID());
                newSetVO.setCardGroupSetName(setVO.getCardGroupSetName());
                newSetVO.setCreatedBy(setVO.getCreatedBy());
                newSetVO.setCreatedOn(setVO.getCreatedOn());
                newSetVO.setLanguage1Message(setVO.getLanguage1Message());
                newSetVO.setLanguage2Message(setVO.getLanguage2Message());
                newSetVO.setModifiedBy(setVO.getModifiedBy());
                requestVO.getCardGroupSetNameList().remove(setVO);
                newSetVO.setLastModifiedOn(currentDate.getTime());
                cardSlabList = cardGroupDao.loadCardGroupDetailsListByID(con, cardGroupSetId, lastVersion);
                newSlabsList = (ArrayList) setsCopySlabs.get(cardGroupSetId);
                cardSlabList.addAll(newSlabsList);
                setName = ((CardGroupDetailsVO) cardSlabList.get(0)).getCardGroupSetName();
                sort = new ListSorterUtil();
                sortedList = (ArrayList) sort.doSort("startRange", null, cardSlabList);
                if (sortedList != null && !sortedList.isEmpty()) {
                    preCardVO = (CardGroupDetailsVO) sortedList.get(0);
                    for (int j = 1, n = sortedList.size(); j < n; j++) {
                        nextCardVO = (CardGroupDetailsVO) sortedList.get(j);
                        if (nextCardVO.getStartRange() <= preCardVO.getEndRange()) {
                            notCopiedSets[i] = setName;
                            count2++;
                            isValidSlab = false;
                            i++;
                            break;
                        }
                        preCardVO = nextCardVO;
                    }
                }
                newSetVO.setLastVersion(lastVersion);
                if (isValidSlab) {
                    if (setVersionVO.getApplicableFrom().getTime() < currentDate.getTime()) {
                        version = Integer.parseInt(lastVersion) + 1;
                        setVO.setLastVersion(String.valueOf(version));
                        newSetVO.setLastVersion(String.valueOf(version));
                    }
                    setVO.setModifiedOn(currentDate);
                    setVO.setModifiedBy(userVO.getUserID());
                    setVO.setDefaultCardGroup(requestVO.getDefaultCardGroup());
                    updateCount = cardGroupSetDAO.updateCardGroupSet(con, setVO);
                    setVO.setLastModifiedOn(currentDate.getTime());
                    if (updateCount <= 0) {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            LOG.errorTrace(methodName, e);
                        }
                        LOG.error(methodName, "Error: while Updating Card_Group_Set");
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
                    }
                    if (setVersionVO.getApplicableFrom().getTime() < currentDate.getTime()) {
                        currentDateString = sdf.format(currentDate);
                        StringBuilder applicableFromBuilder =new StringBuilder();
                        applicableFromBuilder.append(currentDateString.split("-")[0]);
                        applicableFromBuilder.append(" ");
                        applicableFromBuilder.append(currentDateString.split("-")[1]);
                        String applicableFrm=applicableFromBuilder.toString();
                        applicableFrom = BTSLUtil.getDateFromDateString(applicableFrm, format);
                        setVersionVO.setApplicableFrom(applicableFrom);
                        setVersionVO.setCreatedBy(userVO.getUserID());
                        setVersionVO.setVersion(String.valueOf(version));
                        setVersionVO.setCreadtedOn(currentDate);
                        setVersionVO.setModifiedBy(userVO.getUserID());
                        setVersionVO.setModifiedOn(currentDate);
                        // insert Card_Group_Set_Version
                        updateCount = cardGroupSetDAO.addCardGroupSetVersion(con, setVersionVO);
                        if (updateCount <= 0) {
                            try {
                                con.rollback();
                            } catch (Exception e) {
                                LOG.errorTrace(methodName, e);
                            }
                            LOG.error(methodName, "Error: while Updating Card_Group_Set_Version");
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
                        }
                        // set the default values
                        for (int x = 0, y = sortedList.size(); x < y; x++) {
                            cardDetailVO = new CardGroupDetailsVO();
                            cardDetailVO = (CardGroupDetailsVO) sortedList.get(x);
                            cardDetailVO.setCardGroupSetID(setVO.getCardGroupSetID());
                            cardDetailVO.setCardGroupID(String.valueOf(IDGenerator.getNextID(PretupsI.CARD_GROUP_ID, TypesI.ALL)));
                            cardDetailVO.setVersion(String.valueOf(version));
                            // Added for Bonus Bundle feature.
                            cardDetailVO.setSenderConvFactor("1");
                            cardDetailVO.setReceiverConvFactor(cardDetailVO.getReceiverConvFactor());
                        }
                        updateCount = cardDAO.addCardGroupDetails(con, sortedList);
                        if (updateCount <= 0) {
                            try {
                                con.rollback();
                            } catch (Exception e) {
                                LOG.errorTrace(methodName, e);
                                throw e;
                            }
                            LOG.error(methodName, "Error: while Updating Card_Group_Details");
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
                        }
                    } else {
                        setVersionVO.setModifiedBy(userVO.getUserID());
                        setVersionVO.setModifiedOn(currentDate);
                        // update card group set version table
                        updateCount = cardGroupSetVersionDAO.updateCardGroupSetVersion(con, setVersionVO);
                        if (updateCount <= 0) {
                            try {
                                con.rollback();
                            } catch (Exception e) {
                                LOG.errorTrace(methodName, e);
                                throw e;
                            }
                            LOG.error(methodName, "Error: while Updating Card_Group_Set_Version");
                            throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
                        }
                        // delete data from the card_group_details
                        deleteCount = cardGroupSetVersionDAO.deleteCardGroupDetails(con, setVO.getCardGroupSetID(), setVersionVO.getVersion());
                        if (deleteCount < 0) {
                            try {
                                con.rollback();
                            } catch (Exception e) {
                                LOG.errorTrace(methodName, e);
                                throw e;
                            }
                            LOG.error(methodName, "Error: while Deleting Card_Group_Details");
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
                        }
                        // set the default values
                        for (int x = 0, y = sortedList.size(); x < y; x++) {
                            cardDetailVO = new CardGroupDetailsVO();
                            cardDetailVO = (CardGroupDetailsVO) sortedList.get(x);
                            cardDetailVO.setCardGroupSetID(setVO.getCardGroupSetID());
                            cardDetailVO.setCardGroupID(String.valueOf(IDGenerator.getNextID(PretupsI.CARD_GROUP_ID, TypesI.ALL)));
                            cardDetailVO.setVersion(setVersionVO.getVersion());
                            // Added for Bonus Bundle feature.
                            cardDetailVO.setSenderConvFactor("1");
                            cardDetailVO.setReceiverConvFactor(cardDetailVO.getReceiverConvFactor());
                        }
                        updateCount = cardDAO.addCardGroupDetails(con, sortedList);

                        if (updateCount <= 0) {
                            try {
                                con.rollback();
                            } catch (Exception e) {
                                LOG.errorTrace(methodName, e);
                            }
                            LOG.error(methodName, "Error: while Updating Card_Group_Details");
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
                        }
                    }
                    copiedSets[count1] = setVO.getCardGroupSetName();
                    count1++;
                }

            }
            if (count1 > 0) {
                final StringBuffer sbfCopySuccessful = new StringBuffer();
                for (int m = 0; m < count1; m++) {
                    if (m == 0) {
                        sbfCopySuccessful.append(copiedSets[m]);
                    } else if (m > 0 && m < count1 - 1) {
                        sbfCopySuccessful.append(copiedSets[m]);
                        sbfCopySuccessful.append(",");
                    } else if (m == count1 - 1) {
                        sbfCopySuccessful.append(" ");
                        sbfCopySuccessful.append("and");
                        sbfCopySuccessful.append(" ");	
                        sbfCopySuccessful.append(copiedSets[m]);
                    }// else{}
                }
                keyArg = new KeyArgumentVO();
                keyArg.setKey("cardgroup.slab.successfully.copied.tosets");
                keyArg.setArguments(sbfCopySuccessful.toString());
                messageList.add(keyArg);
                messageListForChoiceRecharge.add(keyArg);
            }
            if (count2 > 0) {
                final StringBuffer sbfCopyNotSuccessful = new StringBuffer();
                for (int m = 0; m < count2; m++) {
                    if (m == 0) {
                        sbfCopyNotSuccessful.append(notCopiedSets[m]);
                    } else if (m > 0 && m < count2 - 1) {
                        sbfCopyNotSuccessful.append(notCopiedSets[m]);
                        sbfCopyNotSuccessful.append(",");
                    } else if (m == count2 - 1) {
                        sbfCopyNotSuccessful.append(" ");
                        sbfCopyNotSuccessful.append("and");
                        sbfCopyNotSuccessful.append( " ");
                        sbfCopyNotSuccessful.append(notCopiedSets[m]);
                    }
                }
                keyArg = new KeyArgumentVO();
                keyArg.setKey("cardgroup.slab.copy.unsuccessful.tosets");
                keyArg.setArguments(sbfCopyNotSuccessful.toString());
                messageList.add(keyArg);
                messageListForChoiceRecharge.add(keyArg);
            }
            con.commit();
            String arr[]= { requestVO.getCardGroupSetName()};
    		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
            final AdminOperationVO adminOperationVO = new AdminOperationVO();
    		adminOperationVO.setSource(PretupsI.LOGGER_CARD_GROUP_SOURCE);
    		adminOperationVO.setDate(currentDate);
    		adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
    		String resmsg = RestAPIStringParser.getMessage(locale,
    				PretupsErrorCodesI.MODIFY_C2S_CARDGROUP_SAVE_SUCCUSSFULLY, arr);
    		adminOperationVO.setInfo(resmsg );
    		adminOperationVO.setLoginID(userVO.getLoginID());
    		adminOperationVO.setUserID(userVO.getUserID());
    		adminOperationVO.setCategoryCode(userVO.getCategoryCode());
    		adminOperationVO.setNetworkCode(userVO.getNetworkID());
    		adminOperationVO.setMsisdn(userVO.getMsisdn());
    		AdminOperationLog.log(adminOperationVO);
            // if choice recharge case...then update the process status
			if(processVO != null && processVO.isStatusOkBool()) {
				processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);							
				processDAO=new ProcessStatusDAO();
				if(processDAO.updateProcessDetailNetworkWise(con1,processVO)>0) {
					con1.commit();
				} else {
					con1.rollback();
					loggerValue.setLength(0);
	            	loggerValue.append("Error: while modifying process status : ");
	            	loggerValue.append(PretupsI.BAT_MOD_C2S_CG_PROCESS_ID);
					LOG.error(methodName, loggerValue);
				}
			}
			
			
			return response;
		}
		
		  private void updateSetVersion(ArrayList cardGroupSetVersionList, String selectCardGroupSetVersionID, UserVO userVO, Connection con, CardGroupSetVersionDAO cardGroupSetVersionDAO, Date currentDate) throws Exception {
		        final String methodName = "updateSetVersion";
		        if (LOG.isDebugEnabled()) {
		            LOG.debug(methodName, "Entered");
		        }
		  
		        /*
		         * Card group Set Version drop down key = combination of groupId and
		         * version
		         * so we spilt the id and version
		         */
		        final String[] arr = selectCardGroupSetVersionID.split(":");

		        // set the Card Group Set Version Info
		        CardGroupSetVersionVO setVersionVO = null;
		        for (int i = 0, j = cardGroupSetVersionList.size(); i < j; i++) {
		            setVersionVO = (CardGroupSetVersionVO) cardGroupSetVersionList.get(i);
		            // get the selected version info from the versionList
		            if (arr[0].equals(setVersionVO.getCardGroupSetID()) && arr[1].equals(setVersionVO.getVersion())) {
		                break;
		            }
		        }

		        setVersionVO.setModifiedBy(userVO.getUserID());
		        setVersionVO.setModifiedOn(currentDate);

		        final int updateCount = cardGroupSetVersionDAO.updateCardGroupSetVersion(con, setVersionVO);
		        if (updateCount <= 0) {
		            try {
		                con.rollback();
		            } catch (Exception e) {
		                LOG.errorTrace(methodName, e);
		                throw e;
		            }
		            LOG.error(methodName, "Error: while Updating Card_Group_Set_Versions");
		            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
		        }

		        if (LOG.isDebugEnabled()) {
		            LOG.debug(methodName, "Exiting");
		        }
		    }
		  
		  
		  @Override
			public CardGroupCalculateC2STransferValueResponseVO getCardGroupTransferRuleValue(Connection con, UserVO userVO,CardGroupCalculateC2STransferValueResponseVO response, CardGroupCalculateC2STransferValueRequestVO requestVO) throws BTSLBaseException, Exception
			{
			
			    final String METHOD_NAME = "getCardGroupTransferRuleValue";
		        StringBuilder loggerValue= new StringBuilder(); 
		        if (LOG.isDebugEnabled()) {
		            LOG.debug(METHOD_NAME, "Entered");
		        }
		        try {

		         		            // populate the drop downs
		           // response.setAmountTypeList(LookupsCache.loadLookupDropDown(PretupsI.AMOUNT_TYPE, true));
		            response.setValidityLookupList(LookupsCache.loadLookupDropDown(PretupsI.VALIDITY_TYPE, true));
		            /*
		             * while viewing Transfer Rule For C2S pass senderId = domainCode
		             * and senderClassId = All(by default value)
		             */
		            String categoryId=requestVO.getCategoryId();
					String gradeID=requestVO.getGradeId();
					if(!BTSLUtil.isNullString(categoryId) && !categoryId.equals(PretupsI.ALL))
						categoryId=categoryId.split(":")[0];
					
					if(!BTSLUtil.isNullString(gradeID) && !gradeID.equals(PretupsI.ALL))
						gradeID=gradeID.split(":")[1];
					
					TransferRulesVO transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(requestVO.getServiceTypeId(), requestVO.getModuleTypeId(), userVO.getNetworkID(), requestVO
			                .getDomainCode(), requestVO.getReceiverTypeId(), TypesI.ALL, requestVO.getReceiverClassId(), requestVO.getCardGroupSubServiceID().split(":")[1],
			                PretupsI.NOT_APPLICABLE, requestVO.getGatewayId(),categoryId , gradeID);
				
		            if (transferRulesVO != null) {
		                final C2STransferVO c2sTransferVO = new C2STransferVO();
		                c2sTransferVO.setTransferValue(PretupsBL.getSystemAmount(requestVO.getAmount()));
		                c2sTransferVO.setRequestedAmount(PretupsBL.getSystemAmount(requestVO.getAmount()));
		                c2sTransferVO.setCardGroupSetID(transferRulesVO.getCardGroupSetID());

		                String fromHour = null;
		                final String format = Constants.getProperty("CARDGROUP_DATE_FORMAT");
		                if (BTSLUtil.isNullString(requestVO.getApplicableFromHour())) {
		                    fromHour = "00:00";
		                } else {
		                    fromHour = requestVO.getApplicableFromHour();
		                   if (fromHour.indexOf(":") == -1) {
		                        fromHour = fromHour + ":00";
		                    }
		                    requestVO.setApplicableFromHour(fromHour);
		                }
		                StringBuilder newDateBuilder=new StringBuilder();
		                newDateBuilder.append(requestVO.getApplicableFromDate());
		                newDateBuilder.append(" ");
		                newDateBuilder.append(fromHour);
		                String newDat=newDateBuilder.toString();
		                final Date newDate = BTSLUtil.getDateFromDateString(newDat, format);
		                c2sTransferVO.setTransferDateTime(newDate);
		                final C2STransferItemVO itemVO1 = new C2STransferItemVO();
		                final C2STransferItemVO itemVO2 = new C2STransferItemVO();
		                // Date currentDate = new Date();
		                itemVO2.setPreviousExpiry(BTSLUtil.getDateFromDateString(requestVO.getOldValidityDate()));
		                itemVO2.setTransferDateTime(newDate);
		                itemVO2.setTransferDate(newDate);
		                final ArrayList itemList = new ArrayList();
		                itemList.add(itemVO1);
		                itemList.add(itemVO2);
		                c2sTransferVO.setTransferItemList(itemList);
		                c2sTransferVO.setServiceType(requestVO.getServiceTypeId());
		                c2sTransferVO.setSubService(requestVO.getCardGroupSubServiceID().split(":")[1]);// updated
		                // by
		                // shishu
		                // calculate transfer values

		                // changed for suspend/esume card group slab
		                try {
		                    CardGroupBL.calculateCardGroupDetails(con, c2sTransferVO, requestVO.getModuleTypeId(), true);
		                } catch (BTSLBaseException be) {
		                    if (be.getMessage().equals(PretupsErrorCodesI.CARD_GROUP_SLAB_SUSPENDED)) {
		                    	throw new BTSLBaseException(PretupsErrorCodesI.CARDGROUP_SLAB_MESSAGE_AMOUNTSUSPENDED);
		                    }
		                    String str[]= {requestVO.getAmount()};
		                    Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		            		String resmsg = RestAPIStringParser.getMessage(locale,
		            				PretupsErrorCodesI.INVALID_TRANSFER_VALUE, str);
		            		response.setMessageCode(PretupsErrorCodesI.INVALID_TRANSFER_VALUE);
		            		response.setMessage(resmsg);
		            		throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_TRANSFER_VALUE, str);

		                }

		                // load card group details
		                final CardGroupDAO cardDAO = new CardGroupDAO();
		                final CardGroupDetailsVO cardGroupVO = cardDAO.loadCardGroupDetails(con, c2sTransferVO.getCardGroupSetID(), c2sTransferVO.getTransferValue(), c2sTransferVO
		                    .getTransferDateTime());
		                // Added for Bonus Bundle feature.
		                cardGroupVO.setSenderConvFactor("1");
		                cardGroupVO.setReceiverConvFactor(cardGroupVO.getReceiverConvFactor());
		                ViewC2SCardGroupResponseVO cardGroupResponseVO = new ViewC2SCardGroupResponseVO();
		                this.constructFormFromVO(cardGroupResponseVO, cardGroupVO);
		                response.setCardGroupSetName(cardGroupVO.getCardGroupSetName());
		                response.setViewC2SCardGroupResponseVO(cardGroupResponseVO);
		                StringBuilder subServiceBuilder=new StringBuilder();
		                subServiceBuilder.append(cardGroupVO.getServiceTypeId());
		                subServiceBuilder.append( ":" );
		                subServiceBuilder.append(cardGroupVO.getCardGroupSubServiceId());
		                String subService=subServiceBuilder.toString();
		                response.setCardGroupSubServiceID(subService);
		                response.setCardGroupSubServiceName(cardGroupVO.getCardGroupSubServiceIdDesc());
		                response.setServiceTypeId(cardGroupVO.getServiceTypeId());
		                response.setServiceTypeDesc(cardGroupVO.getServiceTypeDesc());
		                response.setSetType(cardGroupVO.getSetType());
		                response.setNewValidDate(BTSLUtil.getDateStringFromDate(c2sTransferVO.getValidityDateToBeSet(), format));
		                response.setSetTypeName(cardGroupVO.getSetTypeName());
		                response.setRowIndex(cardGroupVO.getRowIndex());
		                float trfValue =  c2sTransferVO.getReceiverTransferValue()/100f;
		                response.setReceiverTransferValue(String.valueOf(trfValue));
		                float tax1 =  c2sTransferVO.getReceiverTax1Value()/100f;
		                response.setReceiverTransferValuesTax1(String.valueOf(tax1));
		                float tax2 =  c2sTransferVO.getReceiverTax2Value()/100f;
		                response.setReceiverTransferValuesTax2(String.valueOf(tax2));
		                float proccessing =  c2sTransferVO.getReceiverAccessFee()/100f;
		                response.setReceiverTransferValuesProcessingValue(String.valueOf(proccessing));
		                
		                ArrayList bonusBundlesList = new BonusBundleDAO().loadBonusBundles(con);
		                bonusBundlesList = this.arrangeBonuses(bonusBundlesList, requestVO.getCardGroupSubServiceID(), false);
		                response.setBonusBundleList(bonusBundlesList);
		            } else {
		                
		                throw new BTSLBaseException(PretupsErrorCodesI.NO_TRANSFER_RULE_ASSOCIATED);
		            }

		        } catch (BTSLBaseException be) {
		        	loggerValue.setLength(0);
		        	loggerValue.append("Exceptin:e=");
		        	loggerValue.append(be);
		            LOG.error(METHOD_NAME,loggerValue);
		            LOG.errorTrace(METHOD_NAME, be);
		           throw be;
		        } catch (Exception e) {
		        	loggerValue.setLength(0);
		        	loggerValue.append("Exceptin:e=");
		        	loggerValue.append(e);
		            LOG.error(METHOD_NAME,loggerValue);
		            LOG.errorTrace(METHOD_NAME, e);
		            throw e;
		        } finally {
		        	
		            if (LOG.isDebugEnabled()) {
		            	loggerValue.setLength(0);
		            	loggerValue.append("Exiting forward=");
		            	loggerValue.append(METHOD_NAME);
		                LOG.debug(METHOD_NAME,loggerValue);
		            }
		        }
		        return response;
		}

		  
		  

		  @Override
		  public LoadCardGroupTransferValuesResponseVO loadCardGroupCalculateTransferRuleValueDropDown(Connection con, UserVO userVO) throws BTSLBaseException, Exception{
		        final String methodName = "loadCardGroupCalculateTransferRuleValueDropDown";
		        if (LOG.isDebugEnabled()) {
		            LOG.debug(methodName, "Entered");
		        }
		        StringBuilder loggerValue= new StringBuilder(); 
		        LoadCardGroupTransferValuesResponseVO response = new LoadCardGroupTransferValuesResponseVO();
		        try {
		            
		            final String moduleName = ((LookupsVO) LookupsCache.getObject(PretupsI.MODULE_TYPE, PretupsI.C2S_MODULE)).getLookupName();
		            response.setModuleTypeId(PretupsI.C2S_MODULE);
		            response.setModuleTypeIdDesc(moduleName);
		            response.setCardGroupSubServiceList(ServiceSelectorMappingCache.loadSelectorDropDownForCardGroup());
		            if (response.getCardGroupSubServiceList() != null && response.getCardGroupSubServiceList().size() == 1) {
		                response.setCardGroupSubServiceID(((ListValueVO) response.getCardGroupSubServiceList().get(0)).getValue());
		                response.setCardGroupSubServiceName(BTSLUtil.getOptionDesc(response.getCardGroupSubServiceID(), response.getCardGroupSubServiceList()).getLabel());
		            }
		       
				
		            response.setServiceTypeList(new CardGroupDAO().loadServiceTypeList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE));
		            if (response.getServiceTypeList() != null && response.getServiceTypeList().size() == 1) {
		                response.setServiceTypeId(((ListValueVO) response.getServiceTypeList().get(0)).getValue());
		                response.setServiceTypedesc(BTSLUtil.getOptionDesc(response.getServiceTypeId(), response.getServiceTypeList()).getLabel());
		            }
		            response.setSetTypeList(LookupsCache.loadLookupDropDown(PretupsI.CARD_GROUP_SET_TYPE, true));
		            if (response.getSetTypeList() != null && response.getSetTypeList().size() == 1) {
		                response.setSetType(((ListValueVO) response.getSetTypeList().get(0)).getValue());
		                response.setSetTypeName(BTSLUtil.getOptionDesc(response.getSetType(), response.getSetTypeList()).getLabel());
		            }
		            if (TypesI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO.getCategoryVO().getFixedDomains())) {
		                final DomainDAO domainDAO = new DomainDAO();
		                response.setDomainList(BTSLUtil.displayDomainList(domainDAO.loadDomainList(con, PretupsI.DOMAIN_TYPE_CODE)));
		            } else {
		                response.setDomainList(BTSLUtil.displayDomainList(userVO.getDomainList()));
		            }
		            response.setSubscriberTypeList(LookupsCache.loadLookupDropDown(PretupsI.SUBSRICBER_TYPE, true));
		            final ServiceClassWebDAO serviceClasswebDAO = new ServiceClassWebDAO();
		            StringBuilder interfaceCategoryBuilder=new StringBuilder();
		            interfaceCategoryBuilder.append("'");
		            interfaceCategoryBuilder.append(PretupsI.INTERFACE_CATEGORY_PREPAID );
		            interfaceCategoryBuilder.append("','");
		            interfaceCategoryBuilder.append(PretupsI.INTERFACE_CATEGORY_POSTPAID);
		            interfaceCategoryBuilder.append("'");
		            final String interfaceCategory =interfaceCategoryBuilder.toString();
		            response.setSubscriberServiceTypeList(serviceClasswebDAO.loadServiceClassList(con, interfaceCategory));
		            CategoryDAO categoryDAO = new CategoryDAO();
		            List<CategoryVO> catlist = new ArrayList<CategoryVO>();
		            catlist = categoryDAO.loadAllCategoryOfDomains(con, response.getDomainList());
		            if (catlist == null || catlist.isEmpty()) {
		                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_CATEGORY_LIST);
		            }
		            List<ListValueVO> catValueList = new ArrayList<ListValueVO>();
		            int catlists=catlist.size();
		            for (int loop = 0; loop <catlists ; loop++) {
		                CategoryVO categoryVO = catlist.get(loop);
		                catValueList.add(new ListValueVO(categoryVO.getCategoryName(), categoryVO.getTrasnferKey()));
		            }
		            response.setCategoryList(catValueList);
		            List<GradeVO> gradeList = new ArrayList<GradeVO>();
		            CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
		            gradeList = categoryGradeDAO.loadGradeList(con);
		            if (gradeList == null || gradeList.isEmpty()) {
		                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_GRADE);
		            }
		            List<ListValueVO> gradeTypeValueList = new ArrayList<ListValueVO>();
		            int gradeLists=gradeList.size();
		            for (int loop = 0; loop < gradeLists; loop++) {
		                GradeVO geoListObj = gradeList.get(loop);
		                gradeTypeValueList.add(new ListValueVO(geoListObj.getGradeName(), geoListObj.getCombinedKey()));
		            }
		            response.setGradeList(gradeTypeValueList);
		            List<ListValueVO> reqGatewayList = new ArrayList<ListValueVO>();
		            MessageGatewayWebDAO msgGatewaywebDAO = new MessageGatewayWebDAO();
		            reqGatewayList = msgGatewaywebDAO.loadGatewayTypeList(con, PretupsI.GATEWAY_DISPLAY_ALLOW_YES, PretupsI.GATEWAY_MODIFIED_ALLOW_YES);
		            response.setGatewayList(reqGatewayList);
		            
		        } catch (BTSLBaseException e) {
		        	loggerValue.setLength(0);
		        	loggerValue.append("Exceptin:e=");
		        	loggerValue.append(e);
		            LOG.error(methodName, loggerValue);
		            LOG.errorTrace(methodName, e);
		            throw e;
		        }  catch (Exception e) {
		        	loggerValue.setLength(0);
		        	loggerValue.append("Exceptin:e=");
		        	loggerValue.append(e);
		            LOG.error(methodName,loggerValue);
		            LOG.errorTrace(methodName, e);
		            throw e;
		        } finally {
					
		            if (LOG.isDebugEnabled()) {
		            	loggerValue.setLength(0);
		            	loggerValue.append("Exiting forward=");
		            	loggerValue.append(methodName);
		                LOG.debug(methodName,loggerValue);
		            }
		        }
		        return response;
		    }


		

		@Override
		public ChangeDefaultCardGroupResponseVO changeDefaultCardGroup(Connection con, UserVO userVO,
				DefaultCardGroupRequestVO requestVO)throws BTSLBaseException, Exception {
			  final String methodName = "changeDefaultCardGroup";
		        if (LOG.isDebugEnabled()) {
		            LOG.debug(methodName, "Entered");
		        }  
		        StringBuilder loggerValue= new StringBuilder();
	       
		        TransferWebDAO transferwebDAO = null;
	        
	            transferwebDAO = new TransferWebDAO();
	            ChangeDefaultCardGroupResponseVO response = new ChangeDefaultCardGroupResponseVO();
	                final CardGroupDAO cardGroupDAO = new CardGroupDAO();
	                final CardGroupSetVersionDAO cardGroupSetVersionDAO = new CardGroupSetVersionDAO();
	                final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
	                final String serviceType = requestVO.getServiceTypeId();
	                final String subService = requestVO.getCardGroupSubServiceID().split(":")[1];
	                final String newCardgroupID = requestVO.getSelectCardGroupSetId();
	                final String ntwkcode=userVO.getNetworkID();
	                final String defaultCardGroupID = (cardGroupDAO.loadDefaultCardGroup(con, serviceType, subService, PretupsI.YES,ntwkcode)).get(0);
	                requestVO.setPreviousDefaultCardGroup(defaultCardGroupID);
	                CardGroupDAO cardGroupDao = new CardGroupDAO();
	                List<CardGroupSetVO> selectedCardGroupSetNameList = new ArrayList<>();
	            	List<CardGroupSetVO> cardGroupSetNameList = cardGroupDao.loadCardGroupSet(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
	    	        
	            	if (cardGroupSetNameList != null && !BTSLUtil.isNullString(requestVO.getCardGroupSubServiceID()) && !BTSLUtil.isNullString(requestVO.getServiceTypeId())) {
		                CardGroupSetVO setVO = null;
		                for (int i = 0, j =cardGroupSetNameList.size(); i < j; i++) {
		                	setVO =  cardGroupSetNameList.get(i);
		                    if (setVO.getSubServiceType().equals(requestVO.getCardGroupSubServiceID().split(":")[1]) && setVO.getServiceType().equals(requestVO.getServiceTypeId()) ) 
		                    {
		                    	selectedCardGroupSetNameList.add(setVO);
		                    }
		                }
	            	}
	                if ((newCardgroupID.equals(defaultCardGroupID))) {
	                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.SELECTED_CARD_GROUP_SET_IS_DEFAULT_CARD_GROUP, "");
	                }
	               
	                CardGroupSetVO cardGroupSetVOTmp = null;
	                CardGroupSetVO cardGroupSetVO= null;
	                if (selectedCardGroupSetNameList != null && !selectedCardGroupSetNameList.isEmpty()) {
	                    for (int i = 0, l = selectedCardGroupSetNameList.size(); i < l; i++) {
	                        cardGroupSetVOTmp = (CardGroupSetVO) selectedCardGroupSetNameList.get(i);
	                        if ((cardGroupSetVOTmp.getCardGroupSetID()).equals(newCardgroupID)) {
	                            cardGroupSetVO = cardGroupSetVOTmp;
	                            break;
	                        }
	                    }
	                }
	                if (cardGroupSetVO != null && !((PretupsI.YES).equals(cardGroupSetVO.getStatus()))) {
	                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.SELECTED_CARD_GROUP_SET_IS_SUSPENDED, "");
	                }
	                final Date currentTime = new Date();
	                boolean isUpdated = false;
	                if (!cardGroupSetVersionDAO.isApplicableNow(con, currentTime, newCardgroupID)) {
	                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.SELECTED_CARD_GROUP_SET_IS_NO_CURRENT_VERSION, "");
	                }
	                try {
	                    isUpdated = cardGroupSetDAO.updateAsDefault(con, defaultCardGroupID, newCardgroupID, userVO.getUserID(), currentTime);
	                } catch (Exception e) {
	                    con.rollback();
	                    LOG.errorTrace(methodName, e);
	                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.SELECTED_CARD_GROUP_SET_IS_UNABLE_TO_MAKE_DEFAULT, "");
	                }
	                if (isUpdated) {
	                    con.commit();
	                    Date currentDate = new Date();
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
	                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.SELECTED_CARD_GROUP_SET_IS_UNABLE_TO_MAKE_DEFAULT, "");
	                }
	            
	                if (LOG.isDebugEnabled()) {
		            	loggerValue.setLength(0);
		            	loggerValue.append("Exiting forward=");
		            	loggerValue.append(methodName);
		                LOG.debug(methodName,loggerValue);
		            }
	                response.setUpdateddefaultCadgroup(cardGroupSetVO.getCardGroupSetName());
			return response;
		}
		  
		public String getCardGroupSetNameById(Connection con, String cardGroupSetId, String version) throws BTSLBaseException {
			   CardGroupDAO cardGroupDAO = new CardGroupDAO();
	             ArrayList<CardGroupDetailsVO> list=  cardGroupDAO.loadCardGroupDetailsListByID(con, cardGroupSetId, version);
	             return list.get(0).getCardGroupSetName();
		}
		  



}
