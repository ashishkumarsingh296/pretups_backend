package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;


import com.btsl.user.businesslogic.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTrfReqMessage;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDetailsRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDetailsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.Products;
import com.btsl.pretups.channel.transfer.businesslogic.TotalDetails;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.OracleUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

/*@Path("")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${TransactionAPICalculationController.name}", description = "${TransactionAPICalculationController.desc}")//@Api(tags = "C2S Receiver")
@RestController
@RequestMapping(value ="/v1/c2sReceiver")
public class TransactionAPICalculationController implements ServiceKeywordControllerI{

	private final Log _log = LogFactory.getLog(TransactionAPICalculationController.class.getName());
	/*@Context
 	private HttpServletRequest httpServletRequest;*/
 	/*@POST
 	@Path("/c2s-rest-receiver/txncalview")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)*/
	@PostMapping(value = "/txncalview", consumes =MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	
 	/*@ApiOperation(value = "TRANSACTION API FOR CALCULATION", response = PretupsResponse.class,
 			authorizations = {
    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = PretupsResponse.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
    */

	@io.swagger.v3.oas.annotations.Operation(summary = "${txncalview.summary}", description="${txncalview.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PretupsResponse.class))
							)
					}

					),


					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))

									 , examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.BAD_REQUEST)}
									 
							)
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.UNAUTH)}
									 
									 )
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.NOT_FOUND)}
					
							)
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)}
					)})
			}
	)



	public PretupsResponse<JsonNode> processCP2PUserRequest(HttpServletRequest httpServletRequest,
    		 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
    		 @Parameter( required =true)
		     @RequestBody ChannelTransferDetailsRequestVO channelTransferDetailsRequestVO, HttpServletResponse response1) 
    		 throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {	
		
		final String methodName = "processCP2PUserRequest_TransactionAPICalculationController";
 		
 		PretupsResponse<JsonNode> response;
        RestReceiver restReceiver;
        RestReceiver.updateRequestIdChannel();
        final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
        restReceiver = new RestReceiver();
        OAuthUser oAuthUser= null;
        OAuthUserData oAuthUserData =null;
        
        Connection con = null;
		MComConnectionI mcomCon = null;
		
		try {
			/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
			//OAuthenticationUtil.validateTokenApi(headers);
			
			oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
			
			channelTransferDetailsRequestVO.getData().setMsisdn(oAuthUser.getData().getMsisdn());
			channelTransferDetailsRequestVO.getData().setPin(oAuthUser.getData().getPin());
			channelTransferDetailsRequestVO.getData().setLoginid(oAuthUser.getData().getLoginid());
			channelTransferDetailsRequestVO.getData().setPassword(oAuthUser.getData().getPassword());
	//		channelTransferDetailsRequestVO.getData().setExtcode(oAuthUser.getData().getExtcode());
			
			
			
			channelTransferDetailsRequestVO.setServicePort(oAuthUser.getServicePort());
			channelTransferDetailsRequestVO.setReqGatewayCode(oAuthUser.getReqGatewayCode());
			channelTransferDetailsRequestVO.setReqGatewayLoginId(oAuthUser.getReqGatewayLoginId());
			channelTransferDetailsRequestVO.setReqGatewayPassword(oAuthUser.getReqGatewayPassword());
			channelTransferDetailsRequestVO.setReqGatewayType(oAuthUser.getReqGatewayType());
			channelTransferDetailsRequestVO.setSourceType(oAuthUser.getSourceType());
			
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
 			UserDAO userDAO = new UserDAO();
 			ChannelUserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
 			if(userVO.getUserType().equals(TypesI.STAFF_USER_TYPE)) {
 				userVO = userDAO.loadUserDetailsFormUserID(con, userVO.getParentID());
 				channelTransferDetailsRequestVO.getData().setPassword( BTSLUtil.decryptText(userVO.getPassword()) );
 				channelTransferDetailsRequestVO.getData().setLoginid(userVO.getLoginID());
 				channelTransferDetailsRequestVO.getData().setExtcode(userVO.getExternalCode());
 				channelTransferDetailsRequestVO.getData().setPin(	BTSLUtil.decryptText(userVO.getSmsPin())	);
 			}
			channelTransferDetailsRequestVO.getData().setUserType(userVO.getDomainID());
			channelTransferDetailsRequestVO.getData().setPassword(BTSLUtil.decryptText(userVO.getPassword()));
			
        response = restReceiver.processTransferDetailView(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(channelTransferDetailsRequestVO), new TypeReference<JsonNode>(){})), PretupsRestI.TRANSFERDETAILVIEW,requestIDStr);
        if(response.getStatusCode()!=200)
       	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
       if(response!=null && response.getDataObject()!=null && response.getDataObject().get("txnstatus") != null){
       	if(!response.getDataObject().get("txnstatus").textValue().equals("200"))
       		response1.setStatus(HttpStatus.SC_BAD_REQUEST);
       }
          return response;
        
		} catch (BTSLBaseException be) {
			PretupsResponse<JsonNode> baseResponse= new PretupsResponse<JsonNode>() ;
			
			_log.error(methodName, "BTSLBaseException " + be.getMessage());
            _log.errorTrace(methodName, be);
            if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
              	 response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
              }
               else{
               response1.setStatus(HttpStatus.SC_BAD_REQUEST);
               }
            	baseResponse.setMessageCode(be.getMessageKey());
            	String resmsg = RestAPIStringParser.getMessage(
    					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
    					null);
  
            	baseResponse.setStatusCode(be.getErrorCode());
            	baseResponse.setMessage(resmsg);
                return baseResponse;
            
        } catch (Exception e) {
        	PretupsResponse<JsonNode> baseResponse= new PretupsResponse<JsonNode>() ;
        	_log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            baseResponse.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
					null);
        	baseResponse.setStatusCode(PretupsI.UNABLE_TO_PROCESS_REQUEST);
        	baseResponse.setMessage(resmsg);
        	 response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            return baseResponse;
        }
		
		finally {
            LogFactory.printLog(methodName, " Exited ", _log);
        }

		
	}

	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
    @Override
	public void process(RequestVO p_requestVO) {
    final String METHOD_NAME = "process";
    if (_log.isDebugEnabled()) {
        _log.debug("process", " Entered p_requestVO=" + p_requestVO);
    }
    Connection con = null;MComConnectionI mcomCon = null;
    UserDAO userDao = null;
    ChannelUserDAO channelUserDAO = null;
    StringBuilder responseStr = null;

    try {
    	userDao = new UserDAO();
    	channelUserDAO = new ChannelUserDAO();
        final String messageArr[] = p_requestVO.getRequestMessageArray();
        HashMap reqMap= p_requestVO.getRequestMap();
        responseStr = new StringBuilder();
        HashMap<String, Object> resMap= new HashMap<>();
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Message Array " + messageArr);
        }
        mcomCon = new MComConnection();con=mcomCon.getConnection();
		Gson gson = new Gson();	
		ArrayList<ChannelTransferItemsVO> itemsList = new ArrayList<>();
		NetworkProductDAO networkProductDAO = new NetworkProductDAO(); 
		
		String transferType,transferSubType,paymentType,dualCommission,cbcflag,commissionProfileID,commissionProfileVersion;
		if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())){
		
			String products=(String) reqMap.get("PRODUCTS");
			String []itemsLists = products.split(",");
			
			for(int i=0;i<itemsLists.length;i=i+2)
			{
				ChannelTransferItemsVO channelTransferItemsVO = new ChannelTransferItemsVO();
				channelTransferItemsVO.setRequestedQuantity(itemsLists[i]);
				channelTransferItemsVO.setProductCode(channelUserDAO.product(con, itemsLists[i+1]));
				channelTransferItemsVO.setProductName(itemsLists[i+1]);
				itemsList.add(channelTransferItemsVO);
			}
			transferType=(String) reqMap.get("TRANSFERTYPE");
			transferSubType=(String)reqMap.get("TRANSFERSUBTYPE");
			paymentType=(String) reqMap.get("PAYMENTTYPE");
			 dualCommission=(String) reqMap.get("DUALCOMMISSION");
			 cbcflag=(String) reqMap.get("CBCFLAG");
			 commissionProfileID=(String) reqMap.get("COMMISSIONPROFILEID");
			 commissionProfileVersion=(String) reqMap.get("COMMISSIONPROFILEVERSION");
		}
		else
		{
			C2CTrfReqMessage resMsg = gson.fromJson(p_requestVO.getRequestMessage(), C2CTrfReqMessage.class);
			transferType = resMsg.getTransferType();
			transferSubType = resMsg.getTransferSubType();
			paymentType = resMsg.getPaymenttype();
			dualCommission=resMsg.getDualCommission();
			cbcflag=resMsg.getCbcflag();			
			commissionProfileID=resMsg.getCommissionProfileID();
			commissionProfileVersion=resMsg.getCommissionProfileVersion();
			Products[] productsArr = resMsg.getProducts();
			Products p = new Products();
			
			for(int i=0;i<productsArr.length;i++)	
			{	
				ChannelTransferItemsVO channelTransferItemsVO = new ChannelTransferItemsVO();
				p = productsArr[i];	
				channelTransferItemsVO.setRequestedQuantity(p.getQty());
				channelTransferItemsVO.setProductCode(channelUserDAO.product(con, p.getProductcode()));
				channelTransferItemsVO.setProductName(p.getProductcode());
				itemsList.add(channelTransferItemsVO);
			}
			
		}
		
		ArrayList productList=networkProductDAO.loadProductListForXfr(con,null,p_requestVO.getRequestNetworkCode());
		NetworkProductVO networkProductVO = new NetworkProductVO();
		for(int i=0;i<productList.size();i++)
		{
			networkProductVO=(NetworkProductVO)productList.get(i);
			for(int j=0;j<itemsList.size();j++)
			{
				if(networkProductVO.getProductCode().equals(((ChannelTransferItemsVO)itemsList.get(j)).getProductCode()))
                {
					((ChannelTransferItemsVO)itemsList.get(j)).setUnitValue(networkProductVO.getUnitValue());
					((ChannelTransferItemsVO)itemsList.get(j)).setShortName(networkProductVO.getShortName());
					break;
                }
			}
			}
		final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
        channelTransferVO.setChannelTransferitemsVOList(itemsList);
       
        
        
		channelTransferVO.setDualCommissionType(dualCommission);
		channelTransferVO.setType(transferType);
		channelTransferVO.setTransferSubType(transferSubType);
		channelTransferVO.setNetworkCode(p_requestVO.getRequestNetworkCode());
		channelTransferVO.setPayInstrumentType(paymentType);
		
		try {
			JSONObject reqObj = new JSONObject(p_requestVO.getRequestMessageOrigStr());
			JSONObject dataObj = (JSONObject) reqObj.get("data");
			String toMsisdn = (String) dataObj.get("toMsisdn");
			ChannelUserVO receiverUserVo =  userDao.loadUserDetailsByMsisdn(con, toMsisdn);
			
			channelTransferVO.setToUserID(receiverUserVo.getUserID());
		}catch(Exception e) {
			_log.debug("response ", "Tax Calculation Api  " + e.getMessage());
		}
		
		channelTransferVO.setOtfFlag(true);
		ChannelTransferBL.loadAndCalculateTaxOnProducts(con,commissionProfileID, commissionProfileVersion, channelTransferVO, false,
                null, channelTransferVO.getType());
		HashMap<String, Object> resultMap= new HashMap<String,Object>();
		if("REST".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()) || "WEB".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
		{
		ArrayList result = new ArrayList<>();
		for(int i=0;i<channelTransferVO.getChannelTransferitemsVOList().size();i++)
		{
			ChannelTransferDetailsVO channelTransferDetailsVO = new ChannelTransferDetailsVO();
			ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) (channelTransferVO.getChannelTransferitemsVOList()).get(i);
			channelTransferDetailsVO.setProductCode(channelTransferItemsVO.getProductCode());
			channelTransferDetailsVO.setProductName(channelTransferItemsVO.getShortName());
			channelTransferDetailsVO.setRequestedQty(channelTransferItemsVO.getRequiredQuantity());
			channelTransferDetailsVO.setTax1Value(channelTransferItemsVO.getTax1Value());
			channelTransferDetailsVO.setTax1Rate(channelTransferItemsVO.getTax1Rate());
			channelTransferDetailsVO.setTax1ValueStr(PretupsBL.getDisplayAmount(channelTransferItemsVO.getTax1Value()));
			channelTransferDetailsVO.setTax1Type(channelTransferItemsVO.getTax1Type());
			channelTransferDetailsVO.setTax2Value(channelTransferItemsVO.getTax2Value());
			channelTransferDetailsVO.setTax2DisplayValue(PretupsBL.getDisplayAmount(channelTransferItemsVO.getTax2Value()));
			channelTransferDetailsVO.setTax2Rate(channelTransferItemsVO.getTax2Rate());
			channelTransferDetailsVO.setTax2Type(channelTransferItemsVO.getTax2Type());
			if(channelTransferItemsVO.getTax3Value() < 0)
			{
				channelTransferDetailsVO.setTax3Value(0);
			}
			else {
				channelTransferDetailsVO.setTax3Value(channelTransferItemsVO.getTax3Value());
			}
			
			channelTransferDetailsVO.setTax3DisplayValue(PretupsBL.getDisplayAmount(channelTransferItemsVO.getTax3Value()));
			channelTransferDetailsVO.setTax3Rate(channelTransferItemsVO.getTax3Rate());
			channelTransferDetailsVO.setTax3Type(channelTransferItemsVO.getTax3Type());;
//			channelTransferDetailsVO.setCommValue(channelTransferItemsVO.getCommQuantity());
			if(channelTransferItemsVO.getCommValue() < 0)
			{
				channelTransferDetailsVO.setCommValue(0);
			}
			else {
				channelTransferDetailsVO.setCommValue(channelTransferItemsVO.getCommValue());
			}
			channelTransferDetailsVO.setCommValueDisplay(PretupsBL.getDisplayAmount(channelTransferItemsVO.getCommValue()));//priyank
			channelTransferDetailsVO.setCommRate(channelTransferItemsVO.getCommRate());
			channelTransferDetailsVO.setCommType(channelTransferItemsVO.getCommType());
			channelTransferDetailsVO.setCbcRate(channelTransferItemsVO.getOtfRate());
			channelTransferDetailsVO.setCbcAmount(channelTransferItemsVO.getOtfAmount());
			channelTransferDetailsVO.setCbcAmountDisplayValue(PretupsBL.getDisplayAmount(channelTransferItemsVO.getOtfAmount()));
			channelTransferDetailsVO.setOtfTypePctOrAMt(channelTransferItemsVO.getOtfTypePctOrAMt());
			
			channelTransferDetailsVO.setPayableAmount(channelTransferItemsVO.getPayableAmount());
			channelTransferDetailsVO.setPayableAmountDisplay(PretupsBL.getDisplayAmount(channelTransferItemsVO.getPayableAmount()));
			channelTransferDetailsVO.setNetPayableAmount(channelTransferItemsVO.getNetPayableAmount());
			channelTransferDetailsVO.setNetPayableAmountDisplay(PretupsBL.getDisplayAmount(channelTransferItemsVO.getNetPayableAmount()));
			channelTransferDetailsVO.setReceiverCreditQty(channelTransferItemsVO.getReceiverCreditQty());
		 	 channelTransferDetailsVO.setReceiverCreditQtyDisplay(PretupsBL.getDisplayAmount(channelTransferItemsVO.getReceiverCreditQty()));
			channelTransferDetailsVO.setSenderDebitQtyDisplay(PretupsBL.getDisplayAmount(channelTransferItemsVO.getSenderDebitQty()));
			channelTransferDetailsVO.setSenderDebitQty(channelTransferItemsVO.getSenderDebitQty());
			channelTransferDetailsVO.setCommQuantityAsString(PretupsBL.getDisplayAmount(channelTransferItemsVO.getCommQuantity()));
			result.add(channelTransferDetailsVO);
		}
		resultMap.put("productDetails", result);
		long totalOrderAmount=0,totalPayableAmount=0,totalReceiverQuanitity=0,totalSenderQuantity=0,totalNetPayableAmount=0, totalNetCommissionQuanitity=0;
		for(int i=0;i<channelTransferVO.getChannelTransferitemsVOList().size();i++)
		{
			ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) (channelTransferVO.getChannelTransferitemsVOList()).get(i);
			totalOrderAmount+=channelTransferItemsVO.getRequiredQuantity();
			totalPayableAmount+=channelTransferItemsVO.getPayableAmount();
			totalReceiverQuanitity+= channelTransferItemsVO.getReceiverCreditQty();
			totalSenderQuantity+=channelTransferItemsVO.getSenderDebitQty();
			totalNetPayableAmount+=channelTransferItemsVO.getNetPayableAmount();
			totalNetCommissionQuanitity += channelTransferItemsVO.getCommQuantity();
		}
	     TotalDetails totalDetails = new TotalDetails();
	     totalDetails.setTotalOrderAmount(BTSLUtil.getDisplayFormat(String.valueOf(totalOrderAmount)));
	     totalDetails.setTotalPayableAmount(BTSLUtil.getDisplayFormat(String.valueOf(totalPayableAmount)));
	     totalDetails.setTotalReceiverQuantity(BTSLUtil.getDisplayFormat(String.valueOf(totalReceiverQuanitity)));
	     totalDetails.setTotalSenderQuantity(BTSLUtil.getDisplayFormat(String.valueOf(totalSenderQuantity)));
	     totalDetails.setTotalNetPayableAmount(BTSLUtil.getDisplayFormat(String.valueOf(totalNetPayableAmount)));
	     totalDetails.setTotalNetCommissionQuanitity(BTSLUtil.getDisplayFormat(String.valueOf(totalNetCommissionQuanitity)));
	     
		resultMap.put("totalDetails", totalDetails);
		
	    resMap.put("map", resultMap);
	    p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_TRANSFER_SUCCESS);
		p_requestVO.setResponseMap(resMap);
		}
		else
			{
			String resType = null;
			resType = reqMap.get("TYPE") + "RES";
			responseStr.append("{ \"type\": \"" + resType + "\" ,");
			responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
			responseStr.append("\"data\":{");
			responseStr.append("\"productDetails\":[ ");
			ArrayList result = new ArrayList<>();
			for(int i=0;i<channelTransferVO.getChannelTransferitemsVOList().size();i++)
			{
				ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) (channelTransferVO.getChannelTransferitemsVOList()).get(i);
				responseStr.append("{ \"producName\": \"" +channelTransferItemsVO.getShortName()+ "\" ,");
				responseStr.append("{ \"productCode\": \"" +channelTransferItemsVO.getProductCode()+ "\" ,");
				responseStr.append("\"orderQuantity\": \""+channelTransferItemsVO.getRequiredQuantity()+ "\" ,");
				responseStr.append("\"tax1Value\": \""+PretupsBL.getDisplayAmount(channelTransferItemsVO.getTax1Value())+ "\" ,");
				responseStr.append("\"tax1Rate\": \""+PretupsBL.getDisplayAmount(channelTransferItemsVO.getTax1Rate())+ "\" ,");
				responseStr.append("\"tax1Type\": \""+channelTransferItemsVO.getTax1Type()+ "\" ,");
				responseStr.append("\"tax2Value\": \""+PretupsBL.getDisplayAmount(channelTransferItemsVO.getTax2Value())+ "\" ,");
				responseStr.append("\"tax2Rate\": \""+PretupsBL.getDisplayAmount(channelTransferItemsVO.getTax2Rate())+ "\" ,");
				responseStr.append("\"tax2Type\": \""+channelTransferItemsVO.getTax2Type()+ "\" ,");
				responseStr.append("\"tax3Value\": \""+PretupsBL.getDisplayAmount(channelTransferItemsVO.getTax3Value())+ "\" ,");
				responseStr.append("\"tax3Rate\": \""+PretupsBL.getDisplayAmount(channelTransferItemsVO.getTax3Rate())+ "\" ,");
				responseStr.append("\"tax3Type\": \""+channelTransferItemsVO.getTax3Type()+ "\" ,");
				responseStr.append("\"commission\": \""+PretupsBL.getDisplayAmount(channelTransferItemsVO.getCommQuantity())+ "\" ,");
				responseStr.append("\"commissionRate\": \""+PretupsBL.getDisplayAmount(channelTransferItemsVO.getCommRate())+ "\" ,");
				responseStr.append("\"cbcRate\": \""+PretupsBL.getDisplayAmount(channelTransferItemsVO.getOtfRate())+ "\" ,");
				responseStr.append("\"cbcValue\": \""+PretupsBL.getDisplayAmount(channelTransferItemsVO.getOtfAmount())+ "\" ,");
				responseStr.append("\"payableAmount\": \""+PretupsBL.getDisplayAmount(channelTransferItemsVO.getPayableAmount())+ "\" ,");
				responseStr.append("\"netPayableAmount\": \""+PretupsBL.getDisplayAmount(channelTransferItemsVO.getNetPayableAmount())+ "\" ,");
				responseStr.append("\"receiverQuantity\": \""+PretupsBL.getDisplayAmount(channelTransferItemsVO.getReceiverCreditQty())+ "\" ,");
				responseStr.append("\"senderQuantity\": \""+PretupsBL.getDisplayAmount(channelTransferItemsVO.getSenderDebitQty())+ "\"");
				responseStr.append("\"commQuantityAsString\": \""+BTSLUtil.getDisplayFormat(channelTransferItemsVO.getCommQuantityAsString())+ "\"");
				if(i+1 == channelTransferVO.getChannelTransferitemsVOList().size()){
					responseStr.append("}");
					}
					else
					{
					responseStr.append("},");
					}
				
			}
			responseStr.append("],");
			long totalOrderAmount=0,totalPayableAmount=0,totalReceiverQuanitity=0,totalSenderQuantity=0;
			for(int i=0;i<channelTransferVO.getChannelTransferitemsVOList().size();i++)
			{
				ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) (channelTransferVO.getChannelTransferitemsVOList()).get(i);
				totalOrderAmount+=channelTransferItemsVO.getRequiredQuantity();
				totalPayableAmount+=channelTransferItemsVO.getNetPayableAmount();
				totalReceiverQuanitity+= channelTransferItemsVO.getReceiverCreditQty();
				totalSenderQuantity+=channelTransferItemsVO.getSenderDebitQty();
			}
      			responseStr.append("\"totalDetails\":{ ");
   				responseStr.append("\"totalOrderAmount\": \"" +BTSLUtil.getDisplayFormat(String.valueOf(totalOrderAmount))+ "\" ,");
				responseStr.append("\"totalPayableAmount\": \""+BTSLUtil.getDisplayFormat(String.valueOf(totalPayableAmount))+ "\" ,");
				responseStr.append("\"totalReceiverQuanitity\": \""+BTSLUtil.getDisplayFormat(String.valueOf(totalReceiverQuanitity))+ "\" ,");
				responseStr.append("\"totalSenderQuantity\": \""+BTSLUtil.getDisplayFormat(String.valueOf(totalSenderQuantity))+ "\"");
				responseStr.append("}}}");
				_log.debug("response ", "Tax Calculation Api  " + responseStr);
				resMap.put("RESPONSE", responseStr);
				p_requestVO.setResponseMap(resMap);
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_TRANSFER_SUCCESS);
				p_requestVO.setSenderReturnMessage("Tax Calculation successfully done!");
			}
    }catch (BTSLBaseException be) {
        p_requestVO.setSuccessTxn(false);
        p_requestVO.setMessageCode(be.getMessageKey());
        OracleUtil.rollbackConnection(con, TransactionAPICalculationController.class.getName(), METHOD_NAME);
        _log.error("process", "BTSLBaseException " + be.getMessage());
        _log.errorTrace(METHOD_NAME, be);
        if (be.getMessageList() != null && !be.getMessageList().isEmpty()) {
            final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
            p_requestVO.setMessageArguments(array);
        }
        if (be.getArgs() != null) {
            p_requestVO.setMessageArguments(be.getArgs());
        }
        if (be.isKey()) {
            p_requestVO.setMessageCode(be.getMessageKey());
        } else {
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }
    } catch (Exception e) {
        p_requestVO.setSuccessTxn(false);
        OracleUtil.rollbackConnection(con, TransactionAPICalculationController.class.getName(), METHOD_NAME);
        _log.error("process", "BTSLBaseException " + e.getMessage());
        _log.errorTrace(METHOD_NAME, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PassbookDetailsController[process]", "", "", "",
                        "Exception:" + e.getMessage());
        p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        return;
    } finally {
		if (mcomCon != null) {
			mcomCon.close("TransactionAPICalculationController#process");
			mcomCon = null;
		}
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Exited ");
        }
    }

}
}
