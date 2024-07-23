package com.restapi.networkstock.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockBL;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapi.cardgroup.service.DefaultCardGroup;
import com.web.pretups.networkstock.businesslogic.NetworkStockWebDAO;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@Path("/networkStock")
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value="Network Creation")
public class NetworkStockCreation {
	public static final Log log = LogFactory.getLog(DefaultCardGroup.class.getName());
	
	@SuppressWarnings({ "unchecked" })
	@POST
    @Path("/create")
    @Consumes(value = MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
	//@ApiOperation(value = "Network Creation", response = PretupsResponse.class)
    @io.swagger.v3.oas.annotations.Operation(summary = "${create.summary}", description="${create.description}",

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
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    })
            }
    )

    public PretupsResponse<JsonNode> addNetworkStock(@RequestBody
			@Parameter(description = SwaggerAPIDescriptionI.NETWORK_STOCK_CREATION)
			String requestData) {
		
		
		final String methodName = "addNetworkStock";
	    if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
	    String cardGroupAllowedCategories = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CARD_GROUP_ALLOWED_CATEGORIES);
	    Boolean isUseHomeStock = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK);
	    Connection con = null;
    	MComConnectionI mcomCon = null;
    	PretupsResponse<JsonNode> response = new PretupsResponse<>();
    	  ChannelUserVO channelUserVO = null;
	    try
	    {
	    JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,new TypeReference<JsonNode>() {});
    	JsonNode dataNode =  dataObject.get("data");
		ObjectMapper objectMapper = new ObjectMapper();
    	final NetworkStockTxnVO networkStockTxnVO = new NetworkStockTxnVO();
        String homeStock = "";
        if (isUseHomeStock) {
            homeStock = TypesI.YES;
        }
        // getting user information 
        mcomCon = new MComConnection();
        con=mcomCon.getConnection();
    	/*LoginDAO loginDAO = new LoginDAO();
		ChannelUserVO channelUserVO = loginDAO.loadUserDetails(con, dataNode.get("loginId").textValue(), dataNode.get("password").textValue(), PretupsRestUtil.getSystemLocal());*/
        Date currentdate = new Date();
        String reqloginId = dataObject.get("identifierType").textValue();
        String reqpassword = dataObject.get("identifierValue").textValue();
        if (!BTSLUtil.isNullString(reqloginId)) {
            if (BTSLUtil.isNullString(reqpassword)) {
            	response.setStatus(false);
            	response.setStatusCode(PretupsI.RESPONSE_FAIL);
            	response.setMessageCode("networkstock.includestocktxn.error.pswblnkorinv");
            	//response.setMessageKey("networkstock.level1approval.msg.success");
            	response.setSuccessMsg("networkstock.includestocktxn.error.pswblnkorinv");
            	return response;
            }
        } else {
        	response.setStatus(false);
        	response.setStatusCode(PretupsI.RESPONSE_FAIL);
        	response.setMessageCode("networkstock.includestocktxn.error.loginidblnkorinv");
        	//response.setMessageKey("networkstock.level1approval.msg.success");
        	response.setSuccessMsg("networkstock.includestocktxn.error.loginidblnkorinv");
        	return response;
        }
        
        String categories = cardGroupAllowedCategories;
		String[] allowedCategories = categories.split(",");
		PretupsRestUtil.validateLoggedInUser(reqloginId, reqpassword, con,
				response, allowedCategories);
		if (response.hasFormError()) {
			response.setStatus(false);
			response.setMessageCode("user.unauthorized");
			response.setStatusCode(PretupsI.RESPONSE_FAIL);
			return response;
		}
		// loading loggedin user details  
		UserDAO userDAO = new UserDAO();
		UserVO userVO = (UserVO) userDAO.loadAllUserDetailsByLoginID(con,
				reqloginId);
        String networkCode = dataNode.get("networkCode").textValue();
        if(!BTSLUtil.isNullString(networkCode)) {
        	if(userVO != null && !networkCode.equalsIgnoreCase(userVO.getNetworkID()))
        	{
        		response.setStatus(false);
            	response.setStatusCode(PretupsI.RESPONSE_FAIL);
            	response.setMessageCode("networkstock.includestocktxn.error.ntwcodeidblnkorinv");
            	//response.setMessageKey("networkstock.level1approval.msg.success");
            	response.setSuccessMsg("networkstock.includestocktxn.error.ntwcodeidblnkorinv");
            	return response;
        	}
        } else {
        	response.setStatus(false);
        	response.setStatusCode(PretupsI.RESPONSE_FAIL);
        	response.setMessageCode("networkstock.includestocktxn.error.ntwcodeidblnkorinv");
        	//response.setMessageKey("networkstock.level1approval.msg.success");
        	response.setSuccessMsg("networkstock.includestocktxn.error.ntwcodeidblnkorinv");
        	return response;
        }
        String userID = dataNode.get("userId").textValue();
        if(BTSLUtil.isNullString(userID) ) {
        	response.setStatus(false);
        	response.setStatusCode(PretupsI.RESPONSE_FAIL);
        	response.setMessageCode("networkstock.createstock.msg.unsuccess");
        	//response.setMessageKey("networkstock.level1approval.msg.success");
        	response.setSuccessMsg("networkstock.createstock.msg.unsuccess");
        	return response;
        }
        networkStockTxnVO.setNetworkCode(networkCode);
        networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_TRANSFER);
        networkStockTxnVO.setTxnType(PretupsI.CREDIT);
        networkStockTxnVO.setNetworkFor(networkCode);
        if (TypesI.YES.equals(homeStock)) {
            networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
        } else {
        	networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
        }        
        networkStockTxnVO.setTxnDate(currentdate);
        networkStockTxnVO.setInitiaterRemarks(dataNode.get("remarks").textValue());
        networkStockTxnVO.setCreatedBy(dataNode.get("userId").textValue());
        networkStockTxnVO.setCreatedOn(currentdate);
        networkStockTxnVO.setModifiedBy(dataNode.get("userId").textValue());
        networkStockTxnVO.setModifiedOn(currentdate);
        networkStockTxnVO.setTxnStatus(PretupsI.NETWORK_STOCK_TXN_STATUS_CLOSE);
        networkStockTxnVO.setReferenceNo(dataNode.get("referenceNumber").textValue());
        networkStockTxnVO.setInitiatedBy(dataNode.get("userId").textValue());
        networkStockTxnVO.setUserID(dataNode.get("userId").textValue());
      //calculating mrp,requested quantity etc
   	    double totalQuantity = 0D;
        double tempTotalMrp = 0D;
        NetworkStockTxnItemsVO networkStockTxnItemsVO = null;
        NetworkStockTxnItemsVO networkStockTxnItemsVO1 = null;
        String tempQty = null;
        double quantity = 0D;
        long mrp = 0L;
        long mrpAmount = 0L;
       //Block to calculate total mrp and quantity etc 
        NetworkStockWebDAO networkStockwebDAO = new NetworkStockWebDAO();
        final  List<NetworkStockTxnItemsVO> stockProductList1 = Arrays.asList(objectMapper.readValue(dataNode.get("stockProductList").toString(), NetworkStockTxnItemsVO[].class));
        Map<String,String> productmap=new HashMap<String,String>();
        ArrayList stockProductList=null;
        for(int i=0;i<stockProductList1.size();i++)
        {
        	if( stockProductList1.size()==1 && stockProductList1.get(i).getProductCode()==null)
        	{
        		response.setStatus(false);
            	response.setStatusCode(PretupsI.RESPONSE_FAIL);
            	response.setMessageCode("networkstock.includestocktxn.error.noitems");
            	//response.setMessageKey("networkstock.level1approval.msg.success");
            	response.setSuccessMsg("networkstock.includestocktxn.error.noitems");
            	return response;
        	}
        	productmap.put( stockProductList1.get(i).getProductCode(), stockProductList1.get(i).getRequestedQuantity());//map of product code requested quantiy 
        }
        if (BTSLUtil.isNullString(dataNode.get("walletType").textValue())) {
        	response.setStatus(false);
        	response.setStatusCode(PretupsI.RESPONSE_FAIL);
        	response.setMessageCode("networkstock.includestocktxn.error.nowallet");
        	//response.setMessageKey("networkstock.level1approval.msg.success");
        	response.setSuccessMsg("networkstock.includestocktxn.error.nowallet");
        	return response;
        }
        else
        {
         stockProductList =getProductListForWalletType(networkStockwebDAO.loadProductsForStock(con, networkCode, networkCode, PretupsI.C2S_MODULE),dataNode.get("walletType").textValue());
        }
        if(stockProductList1.size()==0 )
        {
        	response.setStatus(false);
        	response.setStatusCode(PretupsI.RESPONSE_FAIL);
        	response.setMessageCode("networkstock.includestocktxn.error.noitems");
        	//response.setMessageKey("networkstock.level1approval.msg.success");
        	response.setSuccessMsg("networkstock.includestocktxn.error.noitems");
        	return response;
        }
        int stockProductLists=stockProductList.size();
        for (int i = 0, j = stockProductLists; i < j; i++) {
            networkStockTxnItemsVO = (NetworkStockTxnItemsVO) stockProductList.get(i);
            if (BTSLUtil.isNullString(networkStockTxnItemsVO.getWalletType())) {
            	response.setStatus(false);
            	response.setStatusCode(PretupsI.RESPONSE_FAIL);
            	response.setMessageCode("networkstock.includestocktxn.error.nowallet");
            	//response.setMessageKey("networkstock.level1approval.msg.success");
            	response.setSuccessMsg("networkstock.includestocktxn.error.nowallet");
            	return response;
            }
            tempQty = productmap.get(networkStockTxnItemsVO.getProductCode());
            if (BTSLUtil.isNullString(tempQty)) {
                networkStockTxnItemsVO.setAmount(0);
                networkStockTxnItemsVO.setAmountStr(null);
            }
           /* if(counter == productmap.size()) {
            	response.setStatus(false);
            	response.setStatusCode(PretupsI.RESPONSE_FAIL);
            	response.setMessageCode("networkstock.includestocktxn.error.productnotfound");
            	//response.setMessageKey("networkstock.level1approval.msg.success");
            	response.setSuccessMsg("networkstock.includestocktxn.error.productnotfound");
            	return response;
            }*/
            mrp = networkStockTxnItemsVO.getUnitValue();
            if (!BTSLUtil.isNullString(tempQty)) {
                quantity = new Double(tempQty.trim()).doubleValue();
                if(quantity<0 )
                {
                	response.setStatus(false);
                   	response.setStatusCode(PretupsI.RESPONSE_FAIL);
                   	response.setMessageCode("networkstock.includestocktxn.invalidquantity");
                   	//response.setMessageKey("networkstock.level1approval.msg.success");
                   	response.setSuccessMsg("networkstock.includestocktxn.invalidquantity");
                   	return response;
                }
               
                mrpAmount = BTSLUtil.parseDoubleToLong((quantity * mrp));
                networkStockTxnItemsVO.setAmount(mrpAmount);
                networkStockTxnItemsVO.setAmountStr(PretupsBL.getDisplayAmount(mrpAmount));
                networkStockTxnItemsVO.setRequestedQuantity(tempQty.trim());
                networkStockTxnItemsVO.setRequiredQuantity(BTSLUtil.parseDoubleToLong(quantity));
                if (Double.parseDouble(networkStockTxnItemsVO.getRequestedQuantity()) > Double.parseDouble(PretupsBL.getDisplayAmount(networkStockTxnItemsVO.getWalletbalance()))) {
                	response.setStatus(false);
                   	response.setStatusCode(PretupsI.RESPONSE_FAIL);
                   	response.setMessageCode("networkstock.includestocktxn.error.requestamt.more.than.stock");
                   	//response.setMessageKey("networkstock.level1approval.msg.success");
                   	response.setSuccessMsg("networkstock.includestocktxn.error.requestamt.more.than.stock");
                   	return response; 	
                }
            }
            tempTotalMrp += (quantity * mrp);
            totalQuantity = totalQuantity + quantity;
            quantity = 0.0;
        }
        long approveLimit = ((Long) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.NETWORK_STOCK_CIRCLE_MAXLIMIT, dataNode.get("networkCode").textValue())).longValue();
         if(tempTotalMrp > approveLimit)
        {
        	response.setStatus(false);
           	response.setStatusCode(PretupsI.RESPONSE_FAIL);
           	response.setMessageCode("networkstock.includestocktxn.error.maxlimit");
           	//response.setMessageKey("networkstock.level1approval.msg.success");
           	response.setSuccessMsg("networkstock.includestocktxn.error.maxlimit");
           	return response;
        }
        //validation on total quantity
       if(totalQuantity<=0)
       {
    	response.setStatus(false);
       	response.setStatusCode(PretupsI.RESPONSE_FAIL);
       	response.setMessageCode("networkstock.includestocktxn.invalidquantity");
       	//response.setMessageKey("networkstock.level1approval.msg.success");
       	response.setSuccessMsg("networkstock.includestocktxn.invalidquantity");
       	return response;
       }
        networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount(totalQuantity));
        networkStockTxnVO.setApprovedQuantity(PretupsBL.getSystemAmount(totalQuantity));
        networkStockTxnVO.setTxnMrp(BTSLUtil.parseDoubleToLong(tempTotalMrp));
        //generating transaction id 
        networkStockTxnVO.setTxnNo(NetworkStockBL.genrateStockTransctionID(networkStockTxnVO));
        // get the information of the selected productItems which is to
        // be associated with the order
        ArrayList tempStockItemList = new ArrayList();
        NetworkStockTxnItemsVO networkStockTxnItemsVOold = null;
        NetworkStockTxnItemsVO networkStockTxnItemsVOnew = null;
        int seqNo = 1;
        for (int i = 0, j = stockProductLists; i < j; i++) {
            networkStockTxnItemsVOold = (NetworkStockTxnItemsVO) stockProductList.get(i);
            if (!BTSLUtil.isNullString(networkStockTxnItemsVOold.getRequestedQuantity())) {
                networkStockTxnItemsVOnew = new NetworkStockTxnItemsVO();
                networkStockTxnItemsVOnew.setSNo(seqNo++);
                networkStockTxnItemsVOnew.setTxnNo(networkStockTxnVO.getTxnNo());
                networkStockTxnItemsVOnew.setProductCode(networkStockTxnItemsVOold.getProductCode());
                networkStockTxnItemsVOnew.setProductName(networkStockTxnItemsVOold.getProductName());
                networkStockTxnItemsVOnew.setRequiredQuantity(PretupsBL.getSystemAmount(networkStockTxnItemsVOold.getRequestedQuantity()));
                networkStockTxnItemsVOnew.setApprovedQuantity(networkStockTxnItemsVOnew.getRequiredQuantity());
                networkStockTxnItemsVOnew.setAmount(networkStockTxnItemsVOold.getAmount());
                networkStockTxnItemsVOnew.setMrp(networkStockTxnItemsVOold.getAmount());
                networkStockTxnItemsVOnew.setWalletBalance(networkStockTxnItemsVOold.getWalletbalance());
                networkStockTxnItemsVOnew.setDateTime(currentdate);
                networkStockTxnItemsVOnew.setTxnWallet(dataNode.get("walletType").textValue());
                tempStockItemList.add(networkStockTxnItemsVOnew);
            }
        }
        networkStockTxnVO.setTxnWallet(dataNode.get("walletType").textValue());
        networkStockTxnVO.setNetworkStockTxnItemsList(tempStockItemList);
        NetworkStockDAO networkStockDAO = null;
        ArrayList networkStockList = null;
        NetworkStockVO networkStocksVO = null;
        NetworkStockTxnItemsVO networkStockTxnItemsVO3=null;
        networkStockDAO = new NetworkStockDAO();
        networkStockList = new ArrayList();
        // creating NetworkStockVO for Updating wallet balance 
        for(int i=0;i<networkStockTxnVO.getNetworkStockTxnItemsList().size();i++)
        {
        	networkStockTxnItemsVO3=(NetworkStockTxnItemsVO) networkStockTxnVO.getNetworkStockTxnItemsList().get(i);
        	networkStocksVO = new NetworkStockVO();
        	networkStocksVO.setNetworkCode(dataNode.get("networkCode").textValue());
            networkStocksVO.setNetworkCodeFor(dataNode.get("networkCode").textValue());
            networkStocksVO.setProductCode(networkStockTxnItemsVO3.getProductCode());
            networkStocksVO.setLastTxnNum(networkStockTxnItemsVO3.getTxnNo());
            networkStocksVO.setLastTxnType(PretupsI.NETWORK_STOCK_TRANSACTION_TRANSFER);
            networkStocksVO.setLastTxnBalance(networkStockTxnItemsVO3.getApprovedQuantity());
            networkStocksVO.setWalletBalance(networkStockTxnItemsVO3.getApprovedQuantity());
            networkStocksVO.setModifiedBy(dataNode.get("userId").textValue());
            networkStocksVO.setModifiedOn(currentdate);
            networkStocksVO.setWalletType(networkStockTxnItemsVO3.getTxnWallet());
            networkStocksVO.setOtherValue(null);
            networkStockList.add(networkStocksVO);
        }
        
        
        int updateCount=0;
        updateCount = networkStockDAO.creditNetworkStock(con, networkStockList, true);
        if (updateCount <= 0) {
           throw new BTSLBaseException(this, "addNetworkStock", PretupsErrorCodesI.AUTOSTOCKCREATION_ERROR_EXCEPTION);
        } else {
       
            updateCount = networkStockDAO.addNetworkStockTransaction(con, networkStockTxnVO);
            if (updateCount <= 0) 
                throw new BTSLBaseException(this, "addNetworkStock", PretupsErrorCodesI.AUTOSTOCKCREATION_ERROR_EXCEPTION);
        }
      
     if (con != null) {
         if (updateCount > 0) {
         	mcomCon.finalCommit();
         	response.setStatus(true);
        	response.setStatusCode(PretupsI.RESPONSE_SUCCESS);
        	response.setMessageCode("networkstock.createstock.msg.success");
        //	response.setMessageKey("networkstock.level1approval.msg.success");
        	response.setSuccessMsg("networkstock.createstock.msg.success");
        	String[] str={networkStockTxnVO.getTxnNo().toString()};
        	response.setParameters(str);
         
         } else {
         	mcomCon.finalRollback();
         	response.setStatus(false);
        	response.setStatusCode(PretupsI.RESPONSE_FAIL);
        	response.setMessageCode("networkstock.createstock.msg.unsuccess");
        	//response.setMessageKey("networkstock.level1approval.msg.success");
        	response.setSuccessMsg("networkstock.createstock.msg.unsuccess");
         }
     }
	    }
	    catch (JsonParseException e) {
			log.error(methodName, "Exceptin:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus(false);
        	response.setStatusCode(PretupsI.RESPONSE_FAIL);
        	response.setMessageCode("networkstock.createstock.msg.unsuccess");
        	//response.setMessageKey("networkstock.level1approval.msg.success");
        	response.setSuccessMsg("networkstock.createstock.msg.unsuccess");
		} catch (JsonMappingException e) {
			log.error(methodName, "Exceptin:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus(false);
        	response.setStatusCode(PretupsI.RESPONSE_FAIL);
        	response.setMessageCode("networkstock.createstock.msg.unsuccess");
        	//response.setMessageKey("networkstock.level1approval.msg.success");
        	response.setSuccessMsg("networkstock.createstock.msg.unsuccess");
		} catch (IOException e) {
			log.error(methodName, "Exceptin:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus(false);
        	response.setStatusCode(PretupsI.RESPONSE_FAIL);
        	response.setMessageCode("networkstock.createstock.msg.unsuccess");
        	//response.setMessageKey("networkstock.level1approval.msg.success");
        	response.setSuccessMsg("networkstock.createstock.msg.unsuccess");
		} catch (SQLException e1) {
			log.error(methodName, "Exceptin:e=" + e1);
            log.errorTrace(methodName, e1);
            response.setStatus(false);
        	response.setStatusCode(PretupsI.RESPONSE_FAIL);
        	response.setMessageCode("networkstock.createstock.msg.unsuccess");
        	//response.setMessageKey("networkstock.level1approval.msg.success");
        	response.setSuccessMsg("networkstock.createstock.msg.unsuccess");
		} catch (BTSLBaseException e1) {
			log.error(methodName, "Exceptin:e=" + e1);
            log.errorTrace(methodName, e1);
            response.setStatus(false);
        	response.setStatusCode(PretupsI.RESPONSE_FAIL);
        	response.setMessageCode("networkstock.createstock.msg.unsuccess");
        	//response.setMessageKey("networkstock.level1approval.msg.success");
        	response.setSuccessMsg("networkstock.createstock.msg.unsuccess");
		}catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus(false);
        	response.setStatusCode(PretupsI.RESPONSE_FAIL);
        	response.setMessageCode("networkstock.createstock.msg.unsuccess");
        	//response.setMessageKey("networkstock.level1approval.msg.success");
        	response.setSuccessMsg("networkstock.createstock.msg.unsuccess");
        }
	    
	    finally
	    {
	    	 try {
	            	if (mcomCon != null) {
	    				mcomCon.close("NetworkStockCreation#addNetworkStock");
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

	private ArrayList getProductListForWalletType(ArrayList loadProductsForStock, String walletType) 
	{
		final String methodName = "getProductListForWalletType";
	    if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		ArrayList newProductList = new ArrayList();
        NetworkStockTxnItemsVO tempVO = null;
        HashMap<String, NetworkStockTxnItemsVO> map = new HashMap<String, NetworkStockTxnItemsVO>();  // product_code,wallet_type
        int productListsSize= loadProductsForStock.size();                                        
        for (int i = 0; i < productListsSize; i++) {
             tempVO = (NetworkStockTxnItemsVO) loadProductsForStock.get(i);
             if (((NetworkStockTxnItemsVO) map.get(tempVO.getProductCode())) != null) {
                 if (((NetworkStockTxnItemsVO) map.get(tempVO.getProductCode())).getWalletType() != null) {
                     continue;
                 }
             }
             if (BTSLUtil.isNullString(tempVO.getWalletType())) {
                 tempVO.setWalletBalance(0L);
                 tempVO.setWalletType(walletType);
                 newProductList.add(tempVO);
                 continue;
             } else if (tempVO.getWalletType().equals(walletType)) {
                 map.put(tempVO.getProductCode(), tempVO);
                 newProductList.add(tempVO);
                 continue;
             }

             else {
                 NetworkStockTxnItemsVO tempVO1 = new NetworkStockTxnItemsVO();
                 try {
					org.apache.commons.beanutils.BeanUtils.copyProperties(tempVO1, tempVO);
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
                 tempVO1.setWalletType(null);
                 tempVO1.setWalletBalance(0L);
                 tempVO1.setStock(0L);
                 map.put(tempVO1.getProductCode(), tempVO1);
             }

         }

         Iterator it = map.entrySet().iterator();
         tempVO = null;
         while (it.hasNext()) {
             Map.Entry pair = (Map.Entry) it.next();
             tempVO = (NetworkStockTxnItemsVO) pair.getValue();
             if (BTSLUtil.isNullString(tempVO.getWalletType())) {
                // tempVO.setWalletType(walletType);
                 newProductList.add(tempVO);
             }
         }
         if (log.isDebugEnabled()) {
             log.debug(methodName, " Exited ");
         }
		return newProductList;
	}
	    
     
	 
		
	
	}

