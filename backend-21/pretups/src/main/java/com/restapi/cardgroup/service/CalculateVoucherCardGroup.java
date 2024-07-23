package com.restapi.cardgroup.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.core.MediaType;


import com.annotation.RestEasyAnnotation;
import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
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
import com.btsl.pretups.channel.profile.businesslogic.CalculateVoucherTransferRuleVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferRulesCache;
import com.btsl.pretups.transfer.businesslogic.TransferRulesVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.vomsproduct.businesslogic.VoucherTypeVO;
import com.web.pretups.gateway.businesslogic.MessageGatewayWebDAO;
import com.web.pretups.master.businesslogic.ServiceClassWebDAO;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RestEasyAnnotation
@RequestMapping(value = "/cardGroup")
@Tag(name = "${CalculateVoucherCardGroup.name}", description = "${CalculateVoucherCardGroup.desc}")//@Api(value="Calculate voucher card group")
public class CalculateVoucherCardGroup {
	public static final Log log = LogFactory.getLog(ViewCardGroup.class.getName());


	//@ApiOperation(value = "Calculate voucher transfer value", response = PretupsResponse.class)
	@PostMapping(value = "/calVoucherTransferRule", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public PretupsResponse<HashMap<String,Object>> calculateVoucherTransferRule(
			@Parameter(description = SwaggerAPIDescriptionI.CALCULATE_VOUCHER_TRANSFER_RULE, required = true)
			@RequestBody CalculateVoucherTransferRuleVO requestVO)
	{
		  final String methodName = "calculateVoucherTransferRule";
	        if (log.isDebugEnabled()) {
	        	log.debug(methodName, "Entered");
	        }
	        Connection con = null;
			MComConnectionI mcomCon = null;
			PretupsResponse<HashMap<String, Object>> response = new PretupsResponse<>();
	        try
	        {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			String categories = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSFER_RULE_ALLOWED_CATEGORIES));
			String[] allowedCategories = categories.split(",");
			PretupsRestUtil.validateLoggedInUser(requestVO.getIdentifierType(), requestVO.getIdentifierValue(), con, response,
					allowedCategories);
			if (response.hasFormError()) {
				response.setStatus(false);
				response.setMessageCode("user.unauthorized");
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			//Loading of data to find Transfer Rule 
		    //Loading Service Type List and fetching Serviced Type Id 
		    ArrayList serviceTypeList = new CardGroupDAO().loadServiceTypeList(con, requestVO.getNetworkCode(), PretupsI.P2P_MODULE);
		    final String serviceTypeId=getServiceTypeId(serviceTypeList,requestVO.getServiceType());
		    //Fetching GateWay type List 
		    List<ListValueVO> reqGatewayList = new ArrayList<ListValueVO>();
		    MessageGatewayWebDAO msgGatewaywebDAO = new MessageGatewayWebDAO();
		    reqGatewayList = msgGatewaywebDAO.loadGatewayTypeList(con, PretupsI.GATEWAY_DISPLAY_ALLOW_YES, PretupsI.GATEWAY_MODIFIED_ALLOW_YES);
		    String gatewayId=null;
		    if(reqGatewayList != null & reqGatewayList.size() > 0) {
		    	String gatewayCode = requestVO.getGatewayCode();
		    	if(PretupsI.ALL.equals(gatewayCode)) {
		    		gatewayId = PretupsI.ALL;
		    	} else {
		    		Iterator itr = reqGatewayList.iterator(); 
	             	while (itr.hasNext()) 
	             	{ 
	             		ListValueVO vo = (ListValueVO) itr.next();
	             		if(vo.getLabel().equals(requestVO.getGatewayCode()))
	             		{
	             			gatewayId= vo.getValue();	
	             			break;
	             		}
	             	}
		    	}
            }
		    //Loading Voucher Type List , Segmennt list and product List 
		  
		    String voucherSegmentId =null;
		    String voucherTypeId=null;
		    String voucherType=null;
		    String voucherProductId=null;
		    if("VCN".equals(serviceTypeId))
		    {  	
		    // Loading Voucher Type List 
		    VomsProductDAO vomsProductDAO = new VomsProductDAO();
		    ArrayList<VoucherTypeVO> voucherTypeList = vomsProductDAO.loadVoucherDetails(con);
	
		    if(voucherTypeList != null & voucherTypeList.size() > 0) {
    	    	Iterator itr =voucherTypeList.iterator(); 
    	    	while (itr.hasNext()) 
    	    	{ 
    	    		VoucherTypeVO vo = (VoucherTypeVO) itr.next();
             	if(vo.getVoucherName().equals(requestVO.getVoucherType()))
             	{
             		
             		voucherTypeId=vo.getVoucherType();
             		break;
            		
             	}
             	}
             } 
		    
		    
		    
		    voucherType= vomsProductDAO.getTypeFromVoucherType(con,voucherTypeId);
        	if(!BTSLUtil.isNullString(voucherType)) {
	        	ArrayList segmentList = BTSLUtil.getSegmentList(voucherType, LookupsCache.loadLookupDropDown(VOMSI.VOUCHER_SEGMENT, true));
	        	if (segmentList == null || segmentList.isEmpty()) {
	                if (log.isDebugEnabled()) {
	                    log.debug(methodName, "No voucher segment found");
	                }
	                throw new BTSLBaseException(this, methodName, "cardgroup.cardgroupdetails.err.msg.novouchersegmentfound");
	            }
	        	 // getting segment id from segment name 
	        	
	             if(segmentList != null & segmentList.size() > 0) {
    		    	Iterator itr = segmentList.iterator(); 
    		    	while (itr.hasNext()) 
    		    	{ 
    	         	ListValueVO vo = (ListValueVO) itr.next();
    	         	if(vo.getLabel().equals(requestVO.getVoucherSegment()))
    	         	{
    	         		
    	         		voucherSegmentId=vo.getValue();
    	         		break;
    	        		
    	         	}
    	         	}
	             }
	        	String mrp= String.valueOf(PretupsBL.getSystemAmount(requestVO.getDenomination()));      
	        	ArrayList<VomsProductVO> voucherProductlist = null; 
	        	voucherProductlist = vomsProductDAO.loadMrpProductDetailsList(con, voucherTypeId, "'" + VOMSI.VOMS_STATUS_ACTIVE + "'", false, "", requestVO.getNetworkCode(), voucherSegmentId,mrp);
	             if (voucherProductlist == null || voucherProductlist.isEmpty()) {
	                 if (log.isDebugEnabled()) {
	                     log.debug(methodName, "No product found");
	                 }
	                 throw new BTSLBaseException(this, methodName, "cardgroup.cardgroupdetails.err.msg.noproductfound");
	             }	
	       // Loading Voucher product ID 
	          
     		   if(voucherProductlist!= null & voucherProductlist.size() > 0) {
     		    	Iterator itr = voucherProductlist.iterator(); 
     		    	while (itr.hasNext()) 
     		    	{ 
     		    		VomsProductVO vo = (VomsProductVO) itr.next();
     	         	if(vo.getProductName().equals(requestVO.getProductName()))
     	         	{
     	         		
     	         		voucherProductId=vo.getProductID();
     	         		break;
     	        		
     	         	}
     	         	}
     	         }
        	}}
		    
		    //Loading Subscriber Type List 
		    ArrayList subscriberTypeList= LookupsCache.loadLookupDropDown(PretupsI.SUBSRICBER_TYPE, true);
		    String senderTypeId=null ;
		    String receiverTypeId=null;
		    if(!"VCN".equals(serviceTypeId))
		    { 
		    if(subscriberTypeList != null & subscriberTypeList.size() > 0) {
      		Iterator itr = subscriberTypeList.iterator(); 
            while (itr.hasNext()) 
            { 
              	ListValueVO vo = (ListValueVO) itr.next();
              	if(vo.getLabel().equals(requestVO.getSenderType()))
              	{
              		senderTypeId= vo.getValue();	
              		break;
              	}
            }
            }
		    }
		    if(subscriberTypeList != null & subscriberTypeList.size() > 0) {
       		Iterator itr = subscriberTypeList.iterator(); 
            while (itr.hasNext()) 
            { 
               	ListValueVO vo = (ListValueVO) itr.next();
               	if(vo.getLabel().equals(requestVO.getReceiverType()))
               	{
               		receiverTypeId= vo.getValue();	
               		break;
               	}
             }
             }
		    if(senderTypeId==null && "VCN".equals(serviceTypeId))
		    senderTypeId=PretupsI.VOUCHER_SENDER_TYPE_ID;//Prepaid Subscriber
		    
	
         // Loading Service Class List and Getting Id 
		    final ServiceClassWebDAO serviceClasswebDAO = new ServiceClassWebDAO();
		    final String interfaceCategory = "'" + PretupsI.INTERFACE_CATEGORY_PREPAID + "','" + PretupsI.INTERFACE_CATEGORY_POSTPAID + "','" + PretupsI.INTERFACE_CATEGORY_PG + "'";
		    ArrayList serviceClassList= serviceClasswebDAO.loadServiceClassList(con, interfaceCategory);
		    String senderClassId=null; String receiverClassId=null;
		    if(!"VCN".equals(serviceTypeId))
		    { 
		 if(serviceClassList != null & serviceClassList.size() > 0) {
        		Iterator itr = serviceClassList.iterator(); 
                while (itr.hasNext()) 
                { 
                	ListValueVO vo = (ListValueVO) itr.next();
                	if(vo.getLabel().equals(requestVO.getSenderServiceClass()))
                	{
                		if(vo.getValue().split(":")[0].equals(senderTypeId))
                		{
                		senderClassId= vo.getValue().split(":")[1];	
                		break;
                		}
                	}
                	}
                }
		    }
		    if(serviceClassList != null & serviceClassList.size() > 0) {
		    	Iterator itr = serviceClassList.iterator(); 
		    	while (itr.hasNext()) 
		    	{ 
             	ListValueVO vo = (ListValueVO) itr.next();
             	if(vo.getLabel().equals(requestVO.getReceiverServiceClass()))
             	{
             		if(vo.getValue().split(":")[0].equals(receiverTypeId))
            		{
             		receiverClassId= vo.getValue().split(":")[1];
             		break;
            		}
             	}
             	}
             }
		    if(senderClassId==null && "VCN".equals(serviceTypeId))
		        senderClassId=PretupsI.VOUCHER_SENDER_CLASS_ID;// SAT_REST(ALL)
         // Loading Selector Code  for Selector Id
		    String cardGroupSubServiceID=null;
		    ArrayList<ServiceSelectorMappingVO> cardGroupSubServiceTypeList = ServiceSelectorMappingCache.getSelectorListForServiceType(serviceTypeId);
         
		    for( ServiceSelectorMappingVO serviceSelectorMappingVO:cardGroupSubServiceTypeList){
		    	if (serviceSelectorMappingVO.getSelectorName().equalsIgnoreCase(requestVO.getSubService())) {
		    		cardGroupSubServiceID = (serviceSelectorMappingVO.getSelectorCode());
		    		break;
		    	}
		    }
         
                                            // Loading Transfer Rule 
         // ================================================================================================
         	final TransferRulesVO transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(serviceTypeId, PretupsI.P2P_MODULE, requestVO.getNetworkCode(), senderTypeId, receiverTypeId, senderClassId, receiverClassId,cardGroupSubServiceID , PretupsI.NOT_APPLICABLE,gatewayId);
         
         // ================================================================================================
         	
            if (transferRulesVO != null) {
            	 final P2PTransferVO transferVO = new P2PTransferVO();
                 transferVO.setTransferValue(PretupsBL.getSystemAmount(requestVO.getDenomination()));
                 transferVO.setRequestedAmount(PretupsBL.getSystemAmount(requestVO.getDenomination()));
                 transferVO.setCardGroupSetID(transferRulesVO.getCardGroupSetID());

                 String fromHour = null;
                 final String format = Constants.getProperty("CARDGROUP_DATE_FORMAT");
                 if (BTSLUtil.isNullString(requestVO.getApplicableTime())) {
                     fromHour = "00:00";
                 } else {
                     fromHour = requestVO.getApplicableTime();
                     if (fromHour.indexOf(":") == -1) {
                         fromHour = fromHour + ":00";
                     }
                 }
                 if("VCN".equals(serviceTypeId))
     		    {
                	 transferVO.setVoucherSegment(voucherSegmentId);
                	 transferVO.setVoucherType(voucherTypeId);
                	 transferVO.setProductId(voucherProductId);
                	 
     		    }
                 final Date newDate = BTSLUtil.getDateFromDateString(requestVO.getApplicableFrom() + " " + fromHour, format);
                 transferVO.setTransferDateTime(newDate);
                 final TransferItemVO itemVO1 = new TransferItemVO();
                 final TransferItemVO itemVO2 = new TransferItemVO();
                 itemVO2.setPreviousExpiry(BTSLUtil.getDateFromDateString(requestVO.getValidityDate()));
                 itemVO2.setTransferDateTime(newDate);
                 itemVO2.setTransferDate(newDate);
                 final ArrayList itemList = new ArrayList();
                 itemList.add(itemVO1);
                 itemList.add(itemVO2);
                 transferVO.setTransferItemList(itemList);
                 transferVO.setServiceType(serviceTypeId);
                 transferVO.setSubService(cardGroupSubServiceID);
            
                 try {
                     CardGroupBL.calculateCardGroupDetails(con, transferVO, PretupsI.P2P_MODULE, true);
                 } catch (BTSLBaseException be) {
                	 String messageKey = PretupsI.VOUCHER_CONS_SERVICE.equals(transferVO.getServiceType()) ? PretupsErrorCodesI.VOUCHER_CARD_GROUP_SLAB_SUSPENDED : 
                     	PretupsErrorCodesI.CARD_GROUP_SLAB_SUSPENDED;
                     if (be.getMessage().equals(messageKey)) {
                    	response.setStatus(false);
                     	response.setStatusCode(PretupsI.RESPONSE_FAIL);
                     	response.setMessageCode("cardgroup.slab.message.amountsuspended");
                     	//response.setMessageKey("cardgroup.slab.message.amountsuspended");
                     	response.setSuccessMsg("Request is not allowed to be processed with the specified amount, please send request with other amount.");	
                     	return response;
                     }else if(PretupsErrorCodesI.VOUCHER_CARD_GROUP_VALUE_NOT_IN_RANGE.equals(be.getMessage()))
                     {
                    	 throw new BTSLBaseException("calculateVoucherTransferRule", methodName, PretupsErrorCodesI.CARD_GROUP_VALUE_NOT_IN_RANGE, 0, be.getArgs(), null);
                     }
                    	 
                     throw be;
                 }
                 // Load Card Group Details
                 CardGroupDetailsVO cardGroupDetailsVO=null;
                 if ( "VCN".equals(transferVO.getServiceType())) {
                	 cardGroupDetailsVO = CardGroupBL.loadCardGroupDetails(con, transferVO.getCardGroupSetID(), transferVO.getRequestedAmount(), transferVO
                             .getTransferDateTime(), transferVO.getVoucherType(), transferVO.getVoucherSegment(),voucherProductId);
     			} else {
     				cardGroupDetailsVO = CardGroupBL.loadCardGroupDetails(con, transferVO.getCardGroupSetID(), transferVO.getRequestedAmount(), transferVO
     		                .getTransferDateTime());
     			}
                 
                 // Loading Bonus Bundle 
                 ArrayList bonusBundlesList = new BonusBundleDAO().loadBonusBundles(con);
                 
                 bonusBundlesList = this.arrangeBonuses(bonusBundlesList, serviceTypeId+":"+cardGroupSubServiceID, false);
                 
       
                 HashMap<String,Object> map=new HashMap<String,Object>();
                 map.put("transferVO", transferVO);
                 map.put("cardGroupVO", cardGroupDetailsVO);
                 map.put("bonusBundlesList", bonusBundlesList);
         		response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, map);
            	}
            else
            	{
            	response.setStatus(false);
            	response.setStatusCode(PretupsI.RESPONSE_FAIL);
            	response.setMessageCode("cardgroup.cardgrouplist.message.notranaferruleassociated");
            	//response.setMessageKey("cardgroup.cardgrouplist.message.notranaferruleassociated");
            	response.setSuccessMsg("No transfer rule  associated with selected combination values");
            	return response;
            	}
	        	}
	       
	        catch (BTSLBaseException be) {
	        	if(response.getStatus()==null)
	        	{
	        		response.setStatus(false);
                 	response.setStatusCode(PretupsI.RESPONSE_FAIL);
                 	response.setMessageCode(be.getMessage());
                	response.setMessageKey(be.getMessage());
                	
                	response.setParameters(be.getArgs());
                	//response.setSuccessMsg("No card group associated with selected combination values");
	        	}
	        	  log.error(methodName, "Exceptin:e=" + be);
	              log.errorTrace(methodName, be);
            }
            catch(Exception e) {
            	  log.error(methodName, "Exceptin:e=" + e);
                  log.errorTrace(methodName, e);
                  response.setStatus(false);
               	  response.setStatusCode(PretupsI.RESPONSE_FAIL);
               	  response.setMessageCode("error.general.processing");
               	  //response.setMessageKey("cardgroup.slab.message.amountsuspended");
               	  response.setSuccessMsg("Due to some technical reasons, your request could not be processed at this time. Please try later");	
	        }
	        finally {
	        	if (mcomCon != null) {
					mcomCon.close("calculateVoucherTransferRule#" + methodName);
					mcomCon = null;
				}
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting");
	            }
	        }
		return response;
		
	}
	
	public String getServiceTypeId(ArrayList list, String serviceTypeName)
	{
		if(list != null & list.size() > 0) {
    		Iterator itr = list.iterator(); 
            while (itr.hasNext()) 
            { 
            	ListValueVO vo = (ListValueVO) itr.next();
            	if(vo.getLabel().equals(serviceTypeName))
            	{
            		return vo.getValue();	
            	}
            	}
            } 
		return null;
		
	}
	 public ArrayList arrangeBonuses(ArrayList p_bonusBundleList, String p_cardGroupSubServiceID, boolean p_isBonusAccDetailList)  throws BTSLBaseException{
	        final String methodName = "arrangeBonuses";
	        if (log.isDebugEnabled()) {
	        	StringBuffer msg=new StringBuffer("");
	        	msg.append("Entered CardGroupSubServiceID=");
	        	msg.append(p_cardGroupSubServiceID);
	        	msg.append(", p_bonusBundleList=");
	        	msg.append(p_bonusBundleList);
	        	msg.append(", p_isBonusAccDetailList=");
	        	msg.append(p_isBonusAccDetailList);
	  
	        	String message=msg.toString();
	        	log.debug(methodName,message);
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
	            final int index = p_cardGroupSubServiceID.indexOf(":");
	            if (index != -1) {
	                serviceSelectorKey = p_cardGroupSubServiceID.replace(':', '_');
	                listSize = tempList.size();
	                if (tempList != null && listSize > 0) {
	                    serviceSelectorMappingVO = (ServiceSelectorMappingVO) ServiceSelectorMappingCache.getServiceSelectorMap().get(serviceSelectorKey);
	                    receiverBonusID = serviceSelectorMappingVO.getReceiverBundleID();
	                    if (log.isDebugEnabled()) {
	                    	log.debug(methodName, "receiverBonusID=" + receiverBonusID);
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
	        } catch (Exception e) {
	        	log.errorTrace(methodName, e);
	            throw new BTSLBaseException(this, methodName, "");
	        } finally {
	            if (log.isDebugEnabled()) {
	            	log.debug(methodName, "Exited arrangedList=" + arrangedList);
	            }
	        }
	        return arrangedList;
	    }
	
}
