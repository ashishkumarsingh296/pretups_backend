package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ErrorMap;
import com.btsl.common.IDGenerator;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.BatchC2CFileProcessLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.OracleUtil;
import com.btsl.util.SqlParameterEncoder;
import com.restapi.user.service.C2CFileUploadApiRequest;
import com.restapi.user.service.C2CFileUploadApiResponse;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

/**
     * @author md.sohail 
     * @(#) C2CFileUploadApiController. Upload the base64 encoded file for batch c2c processing.
	 * @param networkCode
	 * @param identifierType
	 * @param identifierValue
	 * @param operationType
	 * @param category
	 * @param product
	 * @param c2CFileUploadApiRequest
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws BTSLBaseException
	 
 */

@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2CFileUploadApiController.name}", description = "${C2CFileUploadApiController.desc}")//@Api(tags = "C2C File Operations", defaultValue = "Upload Base64 encoded file for C2C batch processing")
@RestController
@RequestMapping(value = "/v1/c2cFileServices")
public class C2CFileUploadApiController {

	public static final Log log = LogFactory.getLog(C2CFileUploadApiController.class.getName());
	public static OperatorUtilI _operatorUtil = null;
	private String partialProceesAllowed = null;
	
	private C2CFileUploadApiRequest request;
	C2CFileUploadApiResponse response = null;
	private ArrayList<String> errorList = null;
	private ArrayList<String> fileDataErrorsCode = null;
	private ArrayList<Integer> rowNumber = null;
	private String requestFileName;
	private String filePathCons;
	private String fileFormatCons;
	private String contentTypeCons;
	private boolean isValidFile;
    private C2CStockTransferMultRequestVO c2cStockTransferMultRequestVOs;
    private List<DataStockMul> dataStockMuls;
	private String base64val;
	private String fileNamewithextention;
	private String filepathtemp;
	private String filepathtempError;
	private String errorMessage;
	private boolean isFileWritten;
	private  String errorMessageCode;
	private int status;
	private RequestVO p_requestVO;
	 boolean processRunning = true;
	ProcessStatusVO processVO = null;
	C2CBatchItemsVO c2cBatchItemVO = null;
	 long batchDetailID = 0;
	 int errorSize=0;
	 ArrayList<MasterErrorList> inputValidations=null;
	 boolean fileExist=false;

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/uploadFile", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody

	/*@ApiOperation(value = "Upload Base64 encoded file for C2C batch processing", notes = ("Api Info:") + ("\n")
			+ ("1. TransferType = W for withdraw or T Transfer.") + ("\n") +  ("2. Supported File formate: csv, xls, xlsx.")
			, response = C2CFileUploadApiResponse.class, authorizations = {
					@Authorization(value = "Authorization") })

	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = C2CFileUploadApiResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), 
			@ApiResponse(code = 404, message = "Not Found") })
*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${uploadFile.summary}", description="${uploadFile.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2CFileUploadApiResponse.class))
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



	public C2CFileUploadApiResponse uploadC2CBatchFile( 
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1,

			@Parameter(description = "Operation Type", required = true)// allowableValues = "T,W")
	        @RequestParam("operationType") String operationType,
			@Parameter(description = "Category", required = true)// allowableValues = "Super Distributor,Dealer,Agent,Retailer")
	        @RequestParam("category") String userCategoryName,
			@RequestBody C2CFileUploadApiRequest c2CFileUploadApiRequest )
			throws IOException, SQLException, BTSLBaseException {

		final String methodName = "uploadC2CBatchFile";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;

		
		p_requestVO = new RequestVO();
		errorList = new ArrayList<String>();
		fileDataErrorsCode = new ArrayList<String>();
		rowNumber = new ArrayList<Integer>();
		isValidFile = true;
		isFileWritten = false;
		errorMessage = "";
		dataStockMuls = new ArrayList<>();
		errorSize=0;
		processRunning = true;
        fileExist=false;
		this.request = c2CFileUploadApiRequest;

		/* validateInputs(); */

		try {

			final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
	        try {
	            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
	        } catch (Exception e) {
	            log.errorTrace("static", e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[initialize]", "", "", "",
	                "Exception while loading the class at the call:" + e.getMessage());
	        }
			
			
			
			response = new C2CFileUploadApiResponse();
			userCategoryName = SqlParameterEncoder.encodeParams(userCategoryName);
			/*userProduct = SqlParameterEncoder.encodeParams(userProduct);*/
			operationType = SqlParameterEncoder.encodeParams(operationType);

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			//basic form validation at api level
        	inputValidations = new ArrayList<>();
			 Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
			/*
			 * Authentication
			 * 
			 * @throws BTSLBaseException
			 */
			c2cStockTransferMultRequestVOs = new C2CStockTransferMultRequestVO();
			c2cStockTransferMultRequestVOs.setData(new OAuthUserData());
        	OAuthenticationUtil.validateTokenApi(c2cStockTransferMultRequestVOs, headers,new BaseResponseMultiple<>());
 
			
        	ChannelUserVO channelUserVO=new ChannelUserVO();
			 ChannelTransferVO channelTransferVO =new ChannelTransferVO();
			 ChannelUserDAO channelUserDAO = new ChannelUserDAO();

			channelUserVO = channelUserDAO.loadActiveUserId(con, c2cStockTransferMultRequestVOs.getData().getLoginid(), "LOGINID");
			if(channelUserVO.getUserType().equals(PretupsI.STAFF_USER_TYPE)) {
				UserDAO _userDAO = new UserDAO();
            	channelUserVO = channelUserDAO.loadStaffUserDetailsByLoginId(con, c2cStockTransferMultRequestVOs.getData().getLoginid());
            	settingStaffDetails(channelUserVO);
            	if(channelUserVO.getUserPhoneVO().getMsisdn()==null) {//means staff has no msisdn
            		UserPhoneVO parentPhoneVO = _userDAO.loadUserPhoneVO(con, channelUserVO.getUserID());//getting parent User phoneVO
            		channelUserVO.setUserPhoneVO(parentPhoneVO);
            	}
			}else	channelUserVO = channelUserDAO.loadChannelUserDetails(con,c2cStockTransferMultRequestVOs.getData().getMsisdn());
			
			if(PretupsI.YES.equals(channelUserVO.getUserPhoneVO().getPinRequired()))
			{
                if(BTSLUtil.isNullString(request.getPin()))
                {
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN,null);
					response.setStatus("400");
					  response.setService("c2cFileServicesresp");
					  response.setMessage(msg);
					  response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
					 return response;
                }
                else
                	{
                	try {
					ChannelUserBL.validatePIN(con, channelUserVO, request.getPin());
				} catch (BTSLBaseException be) {
					if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
							|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
						OracleUtil.commit(con);
					}
					String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
					response.setStatus("400");
					  response.setService("c2cFileServicesresp");
					  response.setMessage(msg);
					  response.setMessageCode(be.getMessageKey());
					  return response;
				}
                	}
			
			}
			
			
			
			 String pattern= "^[a-zA-Z]*$";
			 
			 if(BTSLUtil.isNullString(userCategoryName)) {
				 MasterErrorList masterErrorList = new MasterErrorList();
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_APLHA_CAT,null);
					masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_APLHA_CAT);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}
			 if(!BTSLUtil.isNullString(userCategoryName)) {
			 String noSpaceStr = userCategoryName.replaceAll("\\s", ""); // using built in method just to check for aplhanumeric  
			 if(!noSpaceStr.matches(pattern)){
				 MasterErrorList masterErrorList = new MasterErrorList();
				 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_APLHA_CAT,null);
				 masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_APLHA_CAT);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}
			 }
			 
			 if(BTSLUtil.isNullString(operationType)) {
				 MasterErrorList masterErrorList = new MasterErrorList();
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_APLHA_OPERATIONTYPE,null);
					masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_APLHA_OPERATIONTYPE);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}
			 if(!BTSLUtil.isNullString(operationType)) {
			 if(!operationType.matches(pattern)){
				 MasterErrorList masterErrorList = new MasterErrorList();
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_APLHA_OPERATIONTYPE,null);
					masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_APLHA_OPERATIONTYPE);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}
			 }
			
		
			/* if(BTSLUtil.isNullString(userProduct)){
				 MasterErrorList masterErrorList = new MasterErrorList();
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_PRODUCTCODE,null);
					masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_PRODUCTCODE);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}*/
			 

				
			
			 /*
				 * request input validation
				 */

				if (!BTSLUtil.isNullorEmpty(request.getFileName()) &&  !BTSLUtil.isNullorEmpty(request.getFileAttachment())
						&& !BTSLUtil.isNullorEmpty(request.getFileType())) {
					base64val = request.getFileAttachment();
					requestFileName = request.getFileName();

					validateFileTypeAndName();
				} else {
					requestValidation();
				}
				//throwing list of basic form errors
				 if(!BTSLUtil.isNullOrEmptyList(inputValidations)) {
				  response.setStatus("400");
				  response.setService("c2cFileServicesresp");
				  response.setErrorMap(new ErrorMap());
				  response.getErrorMap().setMasterErrorList(inputValidations);
				  return response;
				 }
			
			
			
		      
		        if (PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(channelUserVO.getOutSuspened())) {
		            if (log.isDebugEnabled()) {
		                log.debug("userSearch", "USER IS OUT SUSPENDED IN THE SYSTEM");
		            }

					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.OUT_SUSPENDED, PretupsI.RESPONSE_FAIL, null);

		        }
		       

		        
		        /*
		         * Now load the list of categories for which the transfer rule is
		         * defined where FROM CATEGORY is the logged in user category.
		         */
		       // mcomCon = new MComConnection();con=mcomCon.getConnection();
		        final ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
		        final ArrayList catgList = channelTransferRuleWebDAO.loadTransferRulesCategoryList(con, channelUserVO.getNetworkID(), channelUserVO.getCategoryCode());
		        ChannelTransferRuleVO rulesVO = null;
		        // Now filter the transfer rule list for which the Transfer allowed
		        // field is 'Y' or Transfer Channel by pass is Y
		        int validCategory=0;
		        for (int i = 0, k = catgList.size(); i < k; i++) {
		            rulesVO = (ChannelTransferRuleVO) catgList.get(i);
		            if (PretupsI.YES.equals(rulesVO.getDirectTransferAllowed()) || PretupsI.YES.equals(rulesVO.getTransferChnlBypassAllowed())) {
		                // Validating whether category in req is valid or not
		            	if(userCategoryName.equalsIgnoreCase(rulesVO.getToCategoryDes())) {
		            		userCategoryName = rulesVO.getToCategory();
		            		validCategory=1;
		            		break;
		            	}
		            }
		        }
		        //if not valid
		        if(validCategory==0){
		     	   
		     	   throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY, PretupsI.RESPONSE_FAIL, null);
		     	   
		        }
		        
		       
		       

			 
			//checking for transfer type and setting accordingy
			if(!BTSLUtil.isNullString(operationType)&& (operationType.equalsIgnoreCase("T"))) {
			
				  if (rulesVO != null) {
			        	channelTransferVO.setTransferCategory(rulesVO.getTransferType());
			        }
			        channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER);
			        channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);

				
			}else if(!BTSLUtil.isNullString(operationType)&& (operationType.equalsIgnoreCase("W"))) {
				channelTransferVO.setTransferCategory(rulesVO.getTransferType());
		        channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
		        channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);

				
				
			}else{
	    		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_TRF_TYPE, PretupsI.RESPONSE_FAIL, null);
	    	}
			
			
			
			  // load the Product Type list and validating
			ProductTypeDAO productTypeDAO = new ProductTypeDAO();
		       String productShortCode = null;
			/*
			 * Uploading and validating file
			 */
           uploadAndValidateFile();
			
           
          //Creating response
		   createResponse(response1);
		   
		   
		   try
           {
           	partialProceesAllowed = Constants.getProperty("BATCH_C2C_PARTIAL_PROCESS_ALLOWED").toString();
           	if (BTSLUtil.isNullString(partialProceesAllowed))
           	{
           		partialProceesAllowed="N";
           	}
           	
         	  
           }
           catch(Exception e)
           {
         	  log.errorTrace(methodName,e);
         	  partialProceesAllowed  = "N";
         	  log.info("loadConstantValues"," partial processing  (Entry BATCH_C2C_PARTIAL_PROCESS_ALLOWED) not found in Constants . Thus taking default values as N");
           }
		   
		   Boolean isC2CSmsNotify = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_SMS_NOTIFY);
		   if("200".equals(String.valueOf(response1.getStatus())))
		   {
			   ArrayList mobileList = new ArrayList<>();
			   final ArrayList batchItemsList = new ArrayList();
			   final C2CBatchMasterVO batchMasterVO = new C2CBatchMasterVO();
			   String msisdn = c2cStockTransferMultRequestVOs.getData().getMsisdn();
			   ChannelUserVO senderVO = null;
				
				senderVO = channelUserDAO.loadActiveUserId(con, c2cStockTransferMultRequestVOs.getData().getLoginid(), "LOGINID");
				if(senderVO.getUserType().equals(PretupsI.STAFF_USER_TYPE)) {
					UserDAO _userDAO = new UserDAO();
					senderVO = channelUserDAO.loadStaffUserDetailsByLoginId(con, c2cStockTransferMultRequestVOs.getData().getLoginid());
	            	settingStaffDetails(senderVO);
	            	if(senderVO.getUserPhoneVO().getMsisdn()==null) {//means staff has no msisdn
	            		UserPhoneVO parentPhoneVO = _userDAO.loadUserPhoneVO(con, senderVO.getUserID());//getting parent User phoneVO
	            		senderVO.setUserPhoneVO(parentPhoneVO);
	            	}
				}else	
					senderVO = channelUserDAO.loadChannelUserDetails(con,msisdn);
				
				if("W".equalsIgnoreCase(operationType))
				p_requestVO.setServiceType("WD");
				else
				p_requestVO.setServiceType("TRF");	
				p_requestVO.setRequestGatewayType(c2cStockTransferMultRequestVOs.getReqGatewayType());
				p_requestVO.setSmsDefaultLang(request.getLanguage1());
				p_requestVO.setSmsSecondLang(request.getLanguage2());
				p_requestVO.setLocale(new Locale(senderVO.getUserPhoneVO().getPhoneLanguage(),senderVO.getUserPhoneVO().getCountry()));
				p_requestVO.setFilteredMSISDN(c2cStockTransferMultRequestVOs.getData().getMsisdn());
				ErrorMap errorMap = new ErrorMap();
				ArrayList<RowErrorMsgLists> rowErrorMsgListsFinal = new ArrayList<>();
				ArrayList<BaseResponse> baseResponseFinalSucess = new ArrayList<>();
				for(int i=0;i<dataStockMuls.size();i++)
				{
					DataStockMul dataStockMul = dataStockMuls.get(i);
					List<Products> prodList=dataStockMul.getProducts();
					for(int j=0;j<dataStockMul.getProducts().size();j++)
					{
						Products prod=prodList.get(j);
						ArrayList <C2sBalanceQueryVO>prodList1 =productTypeDAO.getProductsDetails(con);
						for(C2sBalanceQueryVO prod1:prodList1) {
					    	  //if(prod.getProductcode().equalsIgnoreCase(prod1.getProductCode())) {
								if(prod.getProductcode().equalsIgnoreCase(prod1.getProductShortCode())) {
					    		  productShortCode=prod1.getProductShortCode();
					    		  prod.setProductcode(productShortCode);
					    		  batchMasterVO.setProductCode(prod1.getProductCode());
					    		  break;
					    	  }
					       }
					}
					dataStockMul.setLanguage1("1");
				}
			   c2cStockTransferMultRequestVOs.setDataStkTrfMul(dataStockMuls);
			   batchMasterVO.setBatchTotalRecord(c2cStockTransferMultRequestVOs.getDataStkTrfMul().size());
			   final ProcessBL processBL = new ProcessBL();
			   try {
	                processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.C2C_BATCH_PROCESS_ID,senderVO.getNetworkID());
	            } catch (BTSLBaseException e) {
	                log.error(methodName, "Exception:e=" + e);
	                log.errorTrace(methodName, e);
	                processRunning = false;
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BATCH_EXECUTION_ALREADY_PROCESS);
	            }
	            if (processVO != null && !processVO.isStatusOkBool()) {
	                processRunning = false;
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BATCH_EXECUTION_ALREADY_PROCESS);
	            }
	            con.commit();
	            
	            final Date curDate = new Date();
	            // Construct c2cBatchMasterVO
	           
	            batchMasterVO.setNetworkCode(senderVO.getNetworkID());
	            batchMasterVO.setNetworkCodeFor(senderVO.getNetworkID());
	            batchMasterVO.setBatchName(request.getBatchName());
	            batchMasterVO.setStatus(PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_UNDERPROCESS);
	            // batchMasterVO.setCreatedBy(channelUserVO.getUserID());
	            if(senderVO.getUserType().equals(PretupsI.STAFF_USER_TYPE))
	            	batchMasterVO.setCreatedBy(senderVO.getActiveUserID());
	            else
	            	batchMasterVO.setCreatedBy(channelUserVO.getUserID());
	            
	            batchMasterVO.setCreatedOn(curDate);
	            // batchMasterVO.setModifiedBy(channelUserVO.getUserID());
	            if(senderVO.getUserType().equals(PretupsI.STAFF_USER_TYPE))
	            	batchMasterVO.setModifiedBy(senderVO.getActiveUserID());
	            else
	            	batchMasterVO.setModifiedBy(channelUserVO.getUserID());
	            
	            
	            batchMasterVO.setModifiedOn(curDate);
	            batchMasterVO.setDomainCode(senderVO.getDomainID());
	            /*batchMasterVO.setProductCode(userProduct);*/
	            batchMasterVO.setBatchFileName(request.getFileName());
	            batchMasterVO.setBatchDate(curDate);
	            batchMasterVO.setDefaultLang(request.getLanguage1());
	            batchMasterVO.setSecondLang(request.getLanguage2());
	            batchMasterVO.setUserId(senderVO.getUserID());
	            batchMasterVO.setTransferType(channelTransferVO.getTransferType());
	            batchMasterVO.setTransferSubType(channelTransferVO.getTransferSubType());
	            batchMasterVO.setCategoryCode(senderVO.getCategoryCode());

	            this.genrateC2CBatchMasterTransferID(batchMasterVO);

	            this.insertInC2CBatch(con,batchMasterVO);
	            
				for (int i = 0; i < c2cStockTransferMultRequestVOs.getDataStkTrfMul().size(); i++) {
					RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
					BaseResponse baseResponse = new BaseResponse();
					MasterErrorList masterErrorList = new MasterErrorList();
					ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
					DataStockMul dataStkTrfMul = c2cStockTransferMultRequestVOs.getDataStkTrfMul().get(i);
					RowErrorMsgLists rowErrorMsgListsValidate = new RowErrorMsgLists();
					Boolean flag = false;
					try {
						{
							if("W".equalsIgnoreCase(operationType))
							{
								flag = WithdrawBL.C2CValidate(con,
										 dataStkTrfMul, senderVO, rowErrorMsgListsValidate,true);
							}
							else
								{
								flag = TransferBL.C2CValidate(con,
									 dataStkTrfMul, senderVO, rowErrorMsgListsValidate,true);
								}
							if(flag)
							{
								++errorSize;
								int rowValue=i+1;
								String rowName = !BTSLUtil.isNullString(dataStkTrfMul.getMsisdn2())
										? dataStkTrfMul.getMsisdn2()
										: !BTSLUtil.isNullString(dataStkTrfMul.getLoginid2()) ? dataStkTrfMul.getLoginid2()
												: !BTSLUtil.isNullString(dataStkTrfMul.getExtcode2())
														? dataStkTrfMul.getExtcode2() : "";
								mobileList.add(rowName);
								rowErrorMsgListsValidate.setRowValue("Line "+String.valueOf(rowValue+1));
								rowErrorMsgListsValidate.setRowName(rowName);
								rowErrorMsgListsFinal.add(rowErrorMsgListsValidate);
								
							} 
							else{
							if("W".equalsIgnoreCase(operationType))
							{
								WithdrawBL.c2cWithdrawService(con, c2cStockTransferMultRequestVOs, p_requestVO, dataStkTrfMul,
										senderVO,true);
							}
							else
							{
								TransferBL.c2cService(con, c2cStockTransferMultRequestVOs, p_requestVO, dataStkTrfMul,
									senderVO,true);
							}
							
	                        c2cBatchItemVO = new C2CBatchItemsVO();
	                        c2cBatchItemVO.setBatchId(batchMasterVO.getBatchId());
	                        c2cBatchItemVO.setBatchDetailId(this.genrateC2CBatchDetailTransferID(batchMasterVO.getBatchId(), ++batchDetailID));
	                        c2cBatchItemVO.setMsisdn(dataStkTrfMul.getMsisdn2());
	                        c2cBatchItemVO.setLoginID(dataStkTrfMul.getLoginid2());
	                        if (((Integer) PreferenceCache.getSystemPreferenceValue(PretupsI.C2C_BATCH_APPROVAL_LEVEL)).intValue() == 0) {
	                            c2cBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
	                        } else {
	                            c2cBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
	                        }
	                        
	                        
	                        if(senderVO.getUserType().equals(PretupsI.STAFF_USER_TYPE))
	                        	c2cBatchItemVO.setModifiedBy(channelUserVO.getActiveUserID());
	        	            else
	        	            	c2cBatchItemVO.setModifiedBy(channelUserVO.getUserID());
	        	            
	                        c2cBatchItemVO.setModifiedOn(curDate);
	                        c2cBatchItemVO.setTransferType(channelTransferVO.getTransferType());
	                        c2cBatchItemVO.setTransferSubType(channelTransferVO.getTransferSubType());
	                        c2cBatchItemVO.setTransferDate(curDate);
	                        c2cBatchItemVO.setRequestedQuantity(Long.valueOf(dataStkTrfMul.getProducts().get(0).getQty()));
	                        c2cBatchItemVO.setInitiatorRemarks(dataStkTrfMul.getRemarks());
	                        c2cBatchItemVO.setExternalCode(dataStkTrfMul.getExtcode2());
	                        c2cBatchItemVO.setCategoryCode(userCategoryName);
	                        batchItemsList.add(c2cBatchItemVO);
	                        this.insertInC2CBatchItems(con, senderVO, batchMasterVO, c2cBatchItemVO, (ChannelTransferItemsVO)p_requestVO.getChannelTransferVO().getChannelTransferitemsVOList().get(0), p_requestVO.getChannelTransferVO());
							baseResponse.setMessage("Row "+String.valueOf(i+1)+" has been processed for approval. ");
							baseResponse.setMessageCode("");
							baseResponse.setStatus(200);
							baseResponseFinalSucess.add(baseResponse);
							if (((Integer) PreferenceCache.getSystemPreferenceValue(PretupsI.C2C_BATCH_APPROVAL_LEVEL)).intValue() == 0) {
							KeyArgumentVO keyArgumentVO = new KeyArgumentVO();
							ArrayList<PushMessage> pushMessList =new ArrayList<PushMessage>();;
							String []array =null;
							String[] argsArr = new String[2];
							argsArr[1] = PretupsBL.getDisplayAmount(((ChannelTransferItemsVO)p_requestVO.getChannelTransferVO().getChannelTransferitemsVOList().get(0)).
									getRequiredQuantity());
							argsArr[0] = String.valueOf(((ChannelTransferItemsVO)p_requestVO.getChannelTransferVO().getChannelTransferitemsVOList().get(0))
									.getShortName());
							keyArgumentVO
									.setKey(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS2);
							keyArgumentVO.setArguments(argsArr);
							ArrayList txnSmsMessageList = new ArrayList();
							ArrayList balSmsMessageList = new ArrayList();
							txnSmsMessageList.add(keyArgumentVO);
							ArrayList<UserBalancesVO>userbalanceList=channelUserDAO.loadUserBalances(con, channelUserVO.getNetworkCode(), channelUserVO.getNetworkCode(), p_requestVO.getChannelTransferVO().getToUserID());
							
							for (int index1 = 0, n = userbalanceList.size(); index1 < n; index1++) {
								UserBalancesVO balancesVO = (UserBalancesVO) userbalanceList.get(index1);
								if (balancesVO.getProductCode().equals(
										((ChannelTransferItemsVO)p_requestVO.getChannelTransferVO().getChannelTransferitemsVOList().get(0))
										.getProductCode())) {
									argsArr = new String[2];
									argsArr[1] = balancesVO.getBalanceAsString();
									argsArr[0] = balancesVO.getProductShortName();
									keyArgumentVO = new KeyArgumentVO();
									keyArgumentVO
											.setKey(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS_BALSUBKEY);
									keyArgumentVO.setArguments(argsArr);
									balSmsMessageList.add(keyArgumentVO);
									break;
								}
							}
							String c2cNotifyMsg = null;
							if (isC2CSmsNotify) {
								final LocaleMasterVO localeVO = LocaleMasterCache
										.getLocaleDetailsFromlocale(p_requestVO.getLocale());
								if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
									c2cNotifyMsg = channelTransferVO.getDefaultLang();
								} else {
									c2cNotifyMsg = channelTransferVO.getSecondLang();
								}
								array = new String[] { channelTransferVO.getTransferID(),
										BTSLUtil.getMessage(locale, txnSmsMessageList),
										BTSLUtil.getMessage(locale, balSmsMessageList),
										c2cNotifyMsg };
							}

							if (c2cNotifyMsg == null) {
								array = new String[] { channelTransferVO.getTransferID(),
										BTSLUtil.getMessage(locale, txnSmsMessageList),
										BTSLUtil.getMessage(locale, balSmsMessageList) };
							}

							BTSLMessages messages = new BTSLMessages(
									PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS1, array);
							PushMessage pushMessage = new PushMessage(c2cBatchItemVO.getMsisdn(),
									messages, channelTransferVO.getTransferID(), null,
									locale, channelTransferVO.getNetworkCode());
							 if(!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.NO))
								 pushMessList.add(pushMessage);
							
							// push SMS
							//pushMessage.push();
							 baseResponse.setMessage(RestAPIStringParser.getMessage(
									    new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), p_requestVO.getMessageCode(),
									    p_requestVO.getMessageArguments()));
										baseResponse.setMessageCode(p_requestVO.getMessageCode());
										baseResponse.setStatus(200);
										baseResponseFinalSucess.add(baseResponse);

							}
						}
					
						}	
					} catch (BTSLBaseException be) {
						int rowValue=i+1;
						++errorSize;
						rowErrorMsgLists.setRowName(!BTSLUtil.isNullString(dataStkTrfMul.getMsisdn2())
								? dataStkTrfMul.getMsisdn2()
								: !BTSLUtil.isNullString(dataStkTrfMul.getLoginid2()) ? dataStkTrfMul.getLoginid2()
										: !BTSLUtil.isNullString(dataStkTrfMul.getExtcode2())
												? dataStkTrfMul.getExtcode2() : "");
						mobileList.add(rowErrorMsgLists.getRowName());
						rowErrorMsgLists.setRowValue("Line "+String.valueOf(rowValue+1));
						masterErrorList.setErrorCode(String.valueOf(be.getMessageKey()));
						masterErrorList.setErrorMsg(RestAPIStringParser.getMessage(
								new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
								String.valueOf(be.getMessageKey()), be.getArgs()));
						masterErrorLists.add(masterErrorList);
						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
						rowErrorMsgListsFinal.add(rowErrorMsgLists);
						if(BTSLUtil.isNullObject(c2cBatchItemVO))
						{
							c2cBatchItemVO = new C2CBatchItemsVO();
						}
						BatchC2CFileProcessLog
						.detailLog(
								methodName,
								batchMasterVO,
								c2cBatchItemVO,
								masterErrorList.getErrorMsg(),
								"");
						continue;
					}
				}

					if(!BTSLUtil.isNullOrEmptyList(rowErrorMsgListsFinal))
					{
						response.setStatus("400");
						response.setService("c2cFileServicesresp");
						errorMap.setRowErrorMsgLists(rowErrorMsgListsFinal);
						response.setSuccessList(baseResponseFinalSucess);
						response.setErrorMap(errorMap);
						 if (batchMasterVO
								 .getBatchTotalRecord()== errorSize) // If all
				                // the
				                // records
				                // contains
				                // error
				                // in db
							 
				                {
							 response.setMessage(RestAPIStringParser.getMessage(
									    new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.ALL_RECORDS_ERROR,
									    null));
							 response.setSuccessList(new ArrayList<>());
							 filedelete();
				                    BatchC2CFileProcessLog.c2cBatchMasterLog(methodName, batchMasterVO, "FAIL : All records contains DB error in batch",
				                        "TOTAL RECORDS=0, FILE NAME=" + batchMasterVO.getBatchFileName());
				                }
						 else if(!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.YES) )
						{
							 response.setMessage(RestAPIStringParser.getMessage(
									    new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.BATCH_ID_GENERATED,
									    new String[]{batchMasterVO.getBatchId(),String.valueOf(batchMasterVO.getBatchTotalRecord()-errorSize)}));
							 response.setBatchID(batchMasterVO.getBatchId());
							con.commit();
						}
						 else if(!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.NO))
						{
							if(errorSize>0)
								{
								con.rollback();
								response.setSuccessList(new ArrayList<>());
								response.setMessage(RestAPIStringParser.getMessage(
									    new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.PARTIAL_PROCESS_NOT_ALLOWED,
									    null));
								}
							else
							{
								response.setBatchID(batchMasterVO.getBatchId());
								con.commit();
								BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,batchMasterVO,"PASS : Batch generated successfully","TOTAL RECORDS="+batchMasterVO.getBatchTotalRecord()+", FILE NAME="+batchMasterVO.getBatchFileName());
			       	        	
							}
						}
						updateBatchItems(con, batchMasterVO.getBatchTotalRecord(), batchMasterVO);
						
						List<List<String>> rows = new ArrayList<>();
						for(int i=0;i<errorMap.getRowErrorMsgLists().size();i++)
						{
							RowErrorMsgLists rowErrorMsgList = errorMap.getRowErrorMsgLists().get(i);
							for(int i1=0;i1<rowErrorMsgList.getMasterErrorList().size();i1++)
							{
								MasterErrorList masterErrorList=rowErrorMsgList.getMasterErrorList().get(i1);
							    rows.add(( Arrays.asList(rowErrorMsgList.getRowValue(), (String)mobileList.get(i), masterErrorList.getErrorMsg())));
							    
							}
							rowErrorMsgList.setRowErrorMsgList(null);//done to remove rowerrormsglist deliberately in response
						}
						filePathCons = Constants.getProperty("ErrorBatchC2CUserListFilePath");
						validateFilePathCons(filePathCons);
						
						String filePathConstemp = filePathCons + "temp/";        
						createDirectory(filePathConstemp);
						

						filepathtemp = filePathConstemp ;   

						String logErrorFilename = "Errorlog_" + (System.currentTimeMillis()); 
						//writeExcel(rows,filepathtemp+logErrorFilename+ ".xls");
						writeCSV(rows,filepathtemp+logErrorFilename+ ".csv");
						File error =new File(filepathtemp+logErrorFilename+ ".csv");
						byte[] fileContent = FileUtils.readFileToByteArray(error);
				   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
				   		response.setFileAttachment(encodedString);
				   		response.setFileName(logErrorFilename+".csv");
						
						return response;
						
					}
					
					response.setSuccessList(baseResponseFinalSucess);
					response.setMessageCode(p_requestVO.getMessageCode());
					response.setMessage(RestAPIStringParser.getMessage(
						    new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.ALL_RECORDS_PROCESSED,
						    null));
					response.setStatus("200");
					response.setService("c2cFileServicesresp");
					if(!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.YES) )
					{
						response.setBatchID(batchMasterVO.getBatchId());
						con.commit();
					}
					if(!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.NO))
					{
						if(errorSize>0)
							{
							con.rollback();
							response.setSuccessList(new ArrayList<>());
							}
						else
						{
							response.setBatchID(batchMasterVO.getBatchId());
							con.commit();
						}
					}
					if (((Integer) PreferenceCache.getSystemPreferenceValue(PretupsI.C2C_BATCH_APPROVAL_LEVEL)).intValue() == 0) 
						{
							response.setMessageCode("");
							response.setMessage("All records processed successfully.Batch ID:"+c2cBatchItemVO.getBatchId());
							

						}
						else
						{
							response.setMessageCode("");
							response.setMessage("Batch generated successfully.Batch ID:"+c2cBatchItemVO.getBatchId());
						}
					updateBatchItems(con, batchMasterVO.getBatchTotalRecord(), batchMasterVO);
					BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,batchMasterVO,"PASS : Batch generated successfully","TOTAL RECORDS="+batchItemsList.size()+", FILE NAME="+batchMasterVO.getBatchFileName());
       	        	
				
		   }

		} catch (BTSLBaseException be) {
			if(!fileExist)
			filedelete();
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			String [] args=null;
			if(!BTSLUtil.isNullorEmpty(be.getArgs())) {
				args = be.getArgs();
			}
			if(!BTSLUtil.isNullorEmpty(be.getMessageKey())) {
				errorMessage = RestAPIStringParser.getMessage( new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
						be.getMessageKey(), args);
				errorMessageCode = be.getMessageKey();

				
			}
			if(!BTSLUtil.isNullorEmpty(be.getErrorCode()) &&  be.getErrorCode() !=0) {
				status = be.getErrorCode();
				} else {
					status= 400;
				}
		
			createResponse( response1);
		
		} catch (Exception e) {
			filedelete();
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
			response.setMessageCode("error.general.processing");
			response.setMessage("Check File Type supplied.");
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			 
		} finally {
			
			 if (processRunning) {
	                try {
	                    processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
	                    final ProcessStatusDAO processDAO = new ProcessStatusDAO();
	                    if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
	                        con.commit();
	                    } else {
	                        con.rollback();
	                    }
	                } catch (Exception e) {
	                    if (log.isDebugEnabled()) {
	                        log.error(methodName, " Exception in update process detail for batch c2c initiation " + e.getMessage());
	                    }
	                    log.errorTrace(methodName, e);
	                }
	            } else // delete the uploaded file
	            {
	                // Delete the file form the uploaded path if file validations
	                // are failed.
	              
	                filedelete();
	                
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
		// log.debug(methodName, response);
		return response;

	}
  
	/*
	 * Validation for File Name and File Type
	 */
	private boolean validateFileTypeAndName() {
          boolean isValid = true;
      
        if (request.getFileName().length() > 100) {
		    MasterErrorList masterErrorListFileName = new MasterErrorList();
			masterErrorListFileName.setErrorMsg("File Name length too large.");
			masterErrorListFileName.setErrorCode("");
			inputValidations.add(masterErrorListFileName);
			isValid = false ;
	    }
		if (!isValideFileName(request.getFileName())) {
			MasterErrorList masterErrorList = new MasterErrorList();
			masterErrorList.setErrorMsg("Invalid file name.");
			masterErrorList.setErrorCode("");
			inputValidations.add(masterErrorList);
			isValid = false ;
		}
		if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(request.getFileType().toUpperCase())) {
			fileNamewithextention = requestFileName + ".csv";
		} else if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(request.getFileType().toUpperCase())) {
			fileNamewithextention = requestFileName + ".xls";
		} else if (PretupsI.FILE_CONTENT_TYPE_XLSX.equals(request.getFileType().toUpperCase())) {
			fileNamewithextention = requestFileName + ".xlsx";
		} else {
			MasterErrorList masterErrorList = new MasterErrorList();
			masterErrorList.setErrorMsg("Invalid file type.");
			masterErrorList.setErrorCode("");
			inputValidations.add(masterErrorList);
			isValid = false ;
		}
		return isValid;


	}
	
    //Validation for request input for file upload
	private boolean requestValidation() throws BTSLBaseException {
		boolean isValid = true;

		if (BTSLUtil.isNullorEmpty(request.getFileName())) {
			MasterErrorList masterErrorList = new MasterErrorList();
			masterErrorList.setErrorMsg("File name is empty.");
			masterErrorList.setErrorCode("");
			inputValidations.add(masterErrorList);
			isValid = false ;
		}
		if (BTSLUtil.isNullorEmpty(request.getFileAttachment())) {
			MasterErrorList masterErrorList = new MasterErrorList();
			masterErrorList.setErrorMsg("File attachment is empty.");
			masterErrorList.setErrorCode("");
			inputValidations.add(masterErrorList);
			isValid = false ;
		}
		if (BTSLUtil.isNullorEmpty(request.getFileType())) {
			MasterErrorList masterErrorList = new MasterErrorList();
			masterErrorList.setErrorMsg("File type is empty.");
			masterErrorList.setErrorCode("");
			inputValidations.add(masterErrorList);
			isValid = false ;
		}
		if (BTSLUtil.isNullorEmpty(request.getBatchName())) {
			MasterErrorList masterErrorList = new MasterErrorList();
			masterErrorList.setErrorMsg(RestAPIStringParser.getMessage( new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.BATCH_NAME_EMPTY, null));
			masterErrorList.setErrorCode(PretupsErrorCodesI.BATCH_NAME_EMPTY);
			inputValidations.add(masterErrorList);
			isValid = false ;
		}
		if (!BTSLUtil.isNullorEmpty(request.getLanguage2())&& request.getLanguage2().length()>30) {
			MasterErrorList masterErrorList = new MasterErrorList();
			masterErrorList.setErrorMsg(RestAPIStringParser.getMessage( new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.LANGUAGE2_LENGTH, null));
			masterErrorList.setErrorCode(PretupsErrorCodesI.LANGUAGE2_LENGTH);
			inputValidations.add(masterErrorList);
			isValid = false ;
		}
		if (!BTSLUtil.isNullorEmpty(request.getLanguage1())&& request.getLanguage1().length()>30) {
			MasterErrorList masterErrorList = new MasterErrorList();
			masterErrorList.setErrorMsg(RestAPIStringParser.getMessage( new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.LANGUAGE1_LENGTH, null));
			masterErrorList.setErrorCode(PretupsErrorCodesI.LANGUAGE1_LENGTH);
			inputValidations.add(masterErrorList);
			isValid = false ;
		}
		return isValid;

	}

    /**
     * Method uploadAndValidateFile. 
     *This method will load base file path from Constants.props. 
     * Write File at path.
     * Read File and Validate it's content
     * 
     */
	private void uploadAndValidateFile() throws BTSLBaseException {

		String methodName = "uploadAndValidateFile";
		
		filePathCons = Constants.getProperty("UploadBatchC2CUserListFilePath");
		validateFilePathCons(filePathCons);
		
		String filePathConstemp = filePathCons + "temp/";        // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/temp/
		createDirectory(filePathConstemp);
		

		setFileNameWithExtention();
		
		filepathtemp = filePathConstemp + fileNamewithextention;   // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/temp/c2cBatchTransfer.xlsx

		String logFilename = "uploadError_" + (System.currentTimeMillis()) + ".log"; // uploadError_1594091247732.log
		filepathtempError = filePathConstemp + logFilename;
		byte[] base64Bytes = decodeFile(base64val);
		log.debug("filepathtemp:", filepathtemp);
		log.debug("base64Bytes:", base64Bytes);
		

		/*
		 * Loadin file size from Constants.props and validating.
		 */
		String fileSize = Constants.getProperty("VOMS_MAX_FILE_LENGTH");
		if (BTSLUtil.isNullorEmpty(fileSize)) {
			log.error(methodName, "VOMS_MAX_FILE_LENGTH is null in Constant.props");
			 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_FILE_SIZE_IN_CONSTANTS,
			 PretupsI.RESPONSE_FAIL,null); 
		}else if(base64Bytes.length > Long.parseLong(fileSize) ){
			 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.FILE_SIZE_LARGE,
					 PretupsI.RESPONSE_FAIL,null); 
		}
        
		/*
		 *  Writing File to the path
		 */
		writeByteArrayToFile(filepathtemp, base64Bytes);

		try {

			String fileValueSeparator = Constants.getProperty("BL_VOMS_FILE_SEPARATOR");
			if (BTSLUtil.isNullorEmpty(fileValueSeparator)) {
				log.error(methodName, "fileValueSeparator is null in Constants.props");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_SEPERATOR_IN_CONSTANTS,
				 PretupsI.RESPONSE_FAIL,null); 
			}
	
			List<String> fileValueArray = readuploadedfile();

			String[] fileRecord = null;
			try {

				String productCodeToCheck = "";
				
				for (int loop = 0; loop < fileValueArray.size(); loop++) {   // add size method outside
					String fileData1 = null;
					fileData1 = (String) fileValueArray.get(loop);
					fileRecord = fileData1.split(fileValueSeparator, -1);
					String msisdn = fileRecord[0].trim();
					String loginId = fileRecord[1].trim();
					String extCode = fileRecord[2].trim();
					String quantity = fileRecord[3].trim();
					String productCode = fileRecord[4].trim();
					String remarks = fileRecord[5].trim();
					int rowNo = loop + 1;
					
					if(rowNo ==2) {
						productCodeToCheck = productCode;
					}
					
					//File data validation
					if(rowNo ==1) {
						if(!validateInputType(msisdn, loginId, extCode, quantity, remarks,productCode)) {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_COLUMN_HEADER,
									PretupsI.RESPONSE_FAIL, null);
						}
					}else {
						/*checkDuplicateField(msisdn, loginId, extCode, msisdnMultiMap, loginIdMultiMap, extCodeMultiMap,
								rowNo);*/
						/*checkMandatoryFields(msisdn, loginId, extCode, rowNo);
						checkQuantity(quantity, rowNo);*/
						if(BTSLUtil.isNullOrEmptyList(fileDataErrorsCode))
						{
							if(!productCodeToCheck.equals(productCode)) {
								throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2C_BULK_ONLY_ONE_ALLOWED,
										PretupsI.RESPONSE_FAIL, null);
							}
							Products products = new Products();
							List<Products> prodlist= new ArrayList<>();
							DataStockMul dataStockMul = new DataStockMul();
							dataStockMul.setLoginid2(loginId);
							dataStockMul.setExtcode2(extCode);
							dataStockMul.setMsisdn2(msisdn);
							dataStockMul.setRemarks(remarks);
							dataStockMul.setRow(rowNo);
							prodlist.add(products);
							dataStockMul.setProducts(prodlist);
							dataStockMul.getProducts().get(0).setQty(quantity);
							dataStockMul.getProducts().get(0).setProductcode(productCode);
							dataStockMuls.add(dataStockMul);
						}

					}

				}
				if(dataStockMuls.size()==0)
				{
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_RECORD_AVAILABLE,
							PretupsI.RESPONSE_FAIL, null);
				}

			} catch (ArrayIndexOutOfBoundsException e) {
				//filedelete();
				log.error(methodName, "Exceptin:e=" + e);
				log.errorTrace("uploadAndProcessFile", e);
				e.printStackTrace();
				errorMessage = "Check File Type supplied." ;
				isValidFile = false;
			}

		} catch (IOException io) {
			//filedelete();
			errorMessage = io.toString();
			isValidFile = false;
			log.error(methodName, "Exceptin:e=" + io);
			log.errorTrace("uploadAndProcessFile", io);
			io.printStackTrace();
			errorMessage = io.toString();
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT,
					PretupsI.RESPONSE_FAIL, null);

		} finally {
			LogFactory.printLog(methodName, " Exited ", log);
		}
	}
	
	/**
	 * Method createDirectory will create directory at specified path if direcry do not exists
	 * 
	 * @param filePathConstemp
	 * @throws BTSLBaseException
	 */

	public void createDirectory(String filePathConstemp) throws BTSLBaseException {

		String methodName = "createDirectory";
		File fileTempDir = new File(filePathConstemp);
		if (!fileTempDir.isDirectory()) {
			fileTempDir.mkdirs();
		}
		if (!fileTempDir.exists()) {
			log.debug("Directory does not exist : ", fileTempDir);
			throw new BTSLBaseException("OAuthenticationUtil", methodName,
					PretupsErrorCodesI.BATCH_UPLOAD_DIRECTORY_DO_NOT_EXISTS, PretupsI.RESPONSE_FAIL, null); // provide
																											// your own
		}
	}
	
	/**
	 * Method setFileNameWithExtention 
	 */
  
	private void setFileNameWithExtention() {
		if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(request.getFileType().toUpperCase())) {
			fileNamewithextention = requestFileName + ".csv";
		} else if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(request.getFileType().toUpperCase())) {
			fileNamewithextention = requestFileName + ".xls";
		} else if (PretupsI.FILE_CONTENT_TYPE_XLSX.equals(request.getFileType().toUpperCase())) {
			fileNamewithextention = requestFileName + ".xlsx";
		}
	}
	

	private byte[] decodeFile(String base64value) throws BTSLBaseException {
		byte[] base64Bytes = null;
		try {
			
			base64Bytes = Base64.getMimeDecoder().decode(base64value);
			
		} catch (IllegalArgumentException il) {
			log.debug("Invalid file format", il);
			log.error("Invalid file format", il);
			log.errorTrace("Invalid file format", il);
			throw new BTSLBaseException(this, "decodeFile", PretupsErrorCodesI.INVALID_FILE_FORMAT,
					PretupsI.RESPONSE_FAIL, null);
		}
		return base64Bytes;
	}
	
	
	/**
	 * Method writeByteArrayToFile write decode data at specified path
	 * 
	 * @param filePath
	 * @param base64Bytes
	 * @throws BTSLBaseException
	 */

	private void writeByteArrayToFile(String filePath, byte[] base64Bytes) throws BTSLBaseException {
		try {
			log.debug("writeByteArrayToFile: ", filePath);
			log.debug("writeByteArrayToFile: ", base64Bytes);
			
			if (new File(filepathtemp).exists()) {
				fileExist=true;
				throw new BTSLBaseException("OAuthenticationUtil", "writeByteArrayToFile",
						PretupsErrorCodesI.BATCH_UPLOAD_FILE_EXISTS, PretupsI.RESPONSE_FAIL, null);
			}
			FileUtils.writeByteArrayToFile(new File(filePath), base64Bytes);
			isFileWritten = true ;
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			log.debug("writeByteArrayToFile: ", e.getMessage());
			log.error("writeByteArrayToFile", "Exceptin:e=" + e);
			log.errorTrace("writeByteArrayToFile", e);

		}
	}
	
	
	/**
	 * Method readuploadedfile read data from from and store in in ArrayList of String
	 * 
	 * @return fileValueArray
	 * @throws IOException
	 */

	private List<String> readuploadedfile() throws IOException {
		List<String> fileValueArray = null;
		if (PretupsI.FILE_CONTENT_TYPE_XLSX.equals(request.getFileType().toUpperCase())) {
			fileValueArray = readExcelForXLSX(filepathtemp);
		} else if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(request.getFileType().toUpperCase())) {
			fileValueArray = readExcelForXLS(filepathtemp);

		} else {
			fileValueArray = readFile(filepathtemp);
		}
		return fileValueArray;
	}
	/**
	 * 
	 * @param filepathtemp
	 * @return
	 * @throws IOException
	 */

	public static List<String> readExcelForXLSX(String filepathtemp) throws IOException {
		List<String> fileValueArray = new ArrayList<String>();
		XSSFWorkbook workbook = null;
		XSSFSheet excelsheet = null;
		String tempStr = "";
		DataFormatter formatter = null;
		try (FileInputStream file = new FileInputStream(filepathtemp)) {
			workbook = new XSSFWorkbook(file);
			excelsheet = workbook.getSheetAt(0);
			int rowcount = excelsheet.getLastRowNum();
			int temp = 0;
			while (temp != (rowcount + 1)) {
				tempStr = "";
				for (int i = 0; i <= 5; i++) { // NumberConstants.THREE.getIntValue()
					formatter = new DataFormatter();
					tempStr = tempStr + formatter.formatCellValue(excelsheet.getRow(temp).getCell(i));
					if (i <= 5) {
						tempStr = tempStr + ",";
					}
				}
				temp++;
				fileValueArray.add(tempStr);
			}
		}
		return fileValueArray;
	}
	/**
	 * Method readExcelForXLS
	 * 
	 * @param filepathtemp
	 * @return fileValueArray
	 * @throws IOException
	 */

	public static List<String> readExcelForXLS(String filepathtemp) throws IOException {
		List<String> fileValueArray = new ArrayList<String>();
		HSSFWorkbook workbook = null;
		HSSFSheet excelsheet = null;
		String tempStr = "";
		DataFormatter formatter = null;
		try (FileInputStream file = new FileInputStream(filepathtemp)) {
			workbook = new HSSFWorkbook(file);
			excelsheet = workbook.getSheetAt(0);
			int rowcount = excelsheet.getLastRowNum();
			int temp = 0;
			while (temp != (rowcount + 1)) {
				tempStr = "";
				for (int i = 0; i <= 5; i++) { // NumberConstants.THREE.getIntValue()
					formatter = new DataFormatter();
					String s;
					try {
						 s=formatter.formatCellValue(excelsheet.getRow(temp).getCell(i));
					}catch (Exception e){
						s="";
					}
					
					
					tempStr = tempStr +  s;//formatter.formatCellValue(excelsheet.getRow(temp).getCell(i));
					if (i <= 4) {
						tempStr = tempStr + ",";
					}
				}
				temp++;
				fileValueArray.add(tempStr);
			}
		}
		return fileValueArray;
	}
	/**
	 * Method readFile
	 * 
	 * @param filePath
	 * @return fileValueArray
	 * @throws FileNotFoundException
	 * @throws IOException
	 */

	private List<String> readFile(String filePath) throws FileNotFoundException, IOException {
		List<String> fileValueArray = new ArrayList<String>();
		try (BufferedReader inFile = new BufferedReader(new java.io.FileReader(filePath))) {
			String fileData = null;
			while ((fileData = inFile.readLine()) != null) {
				if (BTSLUtil.isNullorEmpty(fileData)) {
					log.debug("uploadAndProcessFile", "Record Number" + 0 + "Not found");
					continue;
				}
				fileValueArray.add(fileData);
			}
		}
		return fileValueArray;
	}
	/**
	 * Method filedelete
	 */

	private void filedelete() {
		if(!BTSLUtil.isNullString(filepathtemp))
		{File file = new File(filepathtemp);
		if (file.delete()) {
			log.debug("filedelete", "******** Method uploadAndProcessFile :: Got exception and deleted the file");
		}
		}
	}

	/**
	 * Validates the name of the file being uploaded
	 * 
	 * @param fileName
	 * @return boolean
	 */
	public static boolean isValideFileName(String fileName) {
		boolean isValidFileContent = true;
		final String pattern = Constants.getProperty("FILE_NAME_WHITE_LIST");
		final Pattern r = Pattern.compile(pattern);
		final Matcher m = r.matcher(fileName);
		if (!m.find()) {
			isValidFileContent = false;
		}
		return isValidFileContent;
	}
/*	*//**
	 * Method checkDuplicateField
	 * Checks duplicate field in file
	 * 
	 * @param msisdn
	 * @param loginId
	 * @param extCode
	 * @param msisdnMultiMap
	 * @param loginIdmultiMap
	 * @param extCodeMultiMap
	 * @param rowNo
	 *//*

	private void checkDuplicateField(String msisdn, String loginId, String extCode,
			MultiValuedMap<String, String> msisdnMultiMap, MultiValuedMap<String, String> loginIdmultiMap,
			MultiValuedMap<String, String> extCodeMultiMap, int rowNo) {

		if (msisdnMultiMap.get(PretupsI.MSISDN).contains(msisdn)) {
		     
			fileDataErrorsCode.add(PretupsErrorCodesI.DUPLICATE_MOBILE_NO);
			rowNumber.add(rowNo);
			isValidFile =false;
		} else {
			msisdnMultiMap.put(PretupsI.MSISDN, msisdn);
		}

		if (loginIdmultiMap.get(PretupsI.LOGINID).contains(msisdn)) {
			fileDataErrorsCode.add(PretupsErrorCodesI.DUPLICATE_LOGIN_ID);
			rowNumber.add(rowNo);
			isValidFile =false;
		} else {
			loginIdmultiMap.put(PretupsI.LOGINID, msisdn);
		}

		if (extCodeMultiMap.get(PretupsI.EXTCODE).contains(msisdn)) {
			fileDataErrorsCode.add(PretupsErrorCodesI.DUPLICATE_EXT_CODE);
			rowNumber.add(rowNo);
			isValidFile =false;
		} else {
			extCodeMultiMap.put(PretupsI.EXTCODE, msisdn);
		}
	}*/

	

	/*
	 * private void checkRemarks(String remarks, int rowNo) { if
	 * (!BTSLUtil.isNullorEmpty(remarks)) { if(remarks.length() < 6 ||
	 * remarks.length() > 30 ) fileDataErrors.add("Remarks should be : " + rowNo); }
	 * }
	 */
	/**
	 * 
	 * @param msisdn
	 * @param loginId
	 * @param extCode
	 * @param quantity
	 * @param remarks
	 * @return
	 */
	private boolean validateInputType(String msisdn, String loginId, String extCode, String quantity, String remarks,String productCode) {
		if( PretupsI.MOBILE_NUMBER.equalsIgnoreCase(msisdn) && PretupsI.LOGIN_ID1.equalsIgnoreCase(loginId) && PretupsI.EXTCODE_1.equalsIgnoreCase(extCode) 
				&& PretupsI.QUANTITY.equalsIgnoreCase(quantity) &&  PretupsI.REMARKS.equalsIgnoreCase(remarks) && PretupsI.PRODUCT.equalsIgnoreCase(productCode)) {
			return true;
			
		}else {
			return false;
		}
	}
	
	public void validateFilePathCons(String filePathCons) throws BTSLBaseException {
		if (BTSLUtil.isNullorEmpty(filePathCons)) {
			 throw new BTSLBaseException(this, "validateFilePathCons", PretupsErrorCodesI.EMPTY_FILE_PATH_IN_CONSTANTS,
					 PretupsI.RESPONSE_FAIL,null); 
		}
	}

    /**
     * Method createResponse
     */
	private void createResponse( HttpServletResponse response1) {
		 String args[] = new String[1];
		 ArrayList<String> fileDataErrors = new ArrayList<String>();

		if(!isValidFile && isFileWritten) {
			filedelete();
		}
		
		//getting errors
		if(fileDataErrorsCode.size() == rowNumber.size()){
			for(int i=0; i< fileDataErrorsCode.size(); i++) {
				 args[0] = String.valueOf( rowNumber.get(i) ) ;
				String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
						fileDataErrorsCode.get(i), args);
				fileDataErrors.add(resmsg);
				}
		}
		if(!BTSLUtil.isNullOrEmptyList(fileDataErrors)) {
			response.setFileValidationErrorList(fileDataErrors);
		}
		
		
		
		// true
		if (isValidFile == true && BTSLUtil.isNullString(errorMessage)  && BTSLUtil.isNullOrEmptyList(fileDataErrorsCode)) {
			response.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			
			//status for swagger
			response1.setStatus(HttpStatus.SC_OK);
		
		} else {
			response.setMessage(errorMessage);
			response.setStatus(String.valueOf(status));
			response.setMessageCode(errorMessageCode);
			//status for swagger
			if(status ==400) {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}else {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			}
			
		}

	}


    private void genrateC2CBatchMasterTransferID(C2CBatchMasterVO p_batchMasterVO) throws BTSLBaseException {
        final String methodName = "genrateC2CBatchMasterTransferID";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_batchMasterVO=" + p_batchMasterVO);
        }
        try {
            final long txnId = IDGenerator.getNextID(PretupsI.C2C_BATCH_TRANSACTION_ID, BTSLUtil.getFinancialYear(), p_batchMasterVO.getNetworkCode(), p_batchMasterVO
                .getCreatedOn());
            p_batchMasterVO.setBatchId(_operatorUtil.formatC2CBatchMasterTxnID(p_batchMasterVO, txnId));
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            throw new BTSLBaseException("C2CBatchTransferAction", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exited  " + p_batchMasterVO.getBatchId());
            }
        }
        return;
    }
    

    /**
     * Method genrateC2CBatchDetailTransferID.
     * This method is called generate C2C batch detail transferID
     * 
     * @param p_batchMasterID
     *            String
     * @param p_tempNumber
     *            long
     * @throws BTSLBaseException
     * @return String
     */

    private String genrateC2CBatchDetailTransferID(String p_batchMasterID, long p_tempNumber) throws BTSLBaseException {
        final String methodName = "genrateC2CBatchDetailTransferID";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_batchMasterID=" + p_batchMasterID + ", p_tempNumber= " + p_tempNumber);
        }
        String uniqueID = null;
        try {
            uniqueID = _operatorUtil.formatC2CBatchDetailsTxnID(p_batchMasterID, p_tempNumber);
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            throw new BTSLBaseException("C2CBatchTransferAction", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exited  " + uniqueID);
            }
        }
        return uniqueID;
    }
    
    private int insertInC2CBatch(Connection con, C2CBatchMasterVO p_batchMasterVO) throws BTSLBaseException, SQLException {
    	PreparedStatement pstmtInsertBatchMaster = null;
    	
    	
		final StringBuffer strBuffInsertBatchMaster = new StringBuffer(
				"INSERT INTO c2c_batches (batch_id, network_code, ");
		strBuffInsertBatchMaster
				.append("network_code_for, batch_name, status, domain_code, product_code, ");
		strBuffInsertBatchMaster
				.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
		strBuffInsertBatchMaster
				.append(" modified_by, modified_on,sms_default_lang,sms_second_lang,user_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
		if (log.isDebugEnabled()) {
			log.debug("insertInC2CBatch", "strBuffInsertBatchMaster Query ="
					+ strBuffInsertBatchMaster);
			// ends here
		}
		pstmtInsertBatchMaster = (PreparedStatement) con
				.prepareStatement(strBuffInsertBatchMaster.toString());
		int index = 0;
		++index;
		pstmtInsertBatchMaster.setString(index,
				p_batchMasterVO.getBatchId());
		++index;
		pstmtInsertBatchMaster.setString(index,
				p_batchMasterVO.getNetworkCode());
		++index;
		pstmtInsertBatchMaster.setString(index,
				p_batchMasterVO.getNetworkCodeFor());

		// pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented
		// for DB2
		++index;
		pstmtInsertBatchMaster.setString(index,
				p_batchMasterVO.getBatchName());
		++index;
		pstmtInsertBatchMaster
				.setString(index, p_batchMasterVO.getStatus());
		++index;
		pstmtInsertBatchMaster.setString(index,
				p_batchMasterVO.getDomainCode());
		++index;
		pstmtInsertBatchMaster.setString(index,
				p_batchMasterVO.getProductCode());
		++index;
		pstmtInsertBatchMaster.setString(index,
				p_batchMasterVO.getBatchFileName());
		++index;
		pstmtInsertBatchMaster.setLong(index,
				p_batchMasterVO.getBatchTotalRecord());
		++index;
		pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil
				.getTimestampFromUtilDate(p_batchMasterVO.getBatchDate()));
		++index;
		pstmtInsertBatchMaster.setString(index,
				p_batchMasterVO.getCreatedBy());
		++index;
		pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil
				.getTimestampFromUtilDate(p_batchMasterVO.getCreatedOn()));
		++index;
		pstmtInsertBatchMaster.setString(index,
				p_batchMasterVO.getModifiedBy());
		++index;
		pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil
				.getTimestampFromUtilDate(p_batchMasterVO.getModifiedOn()));

		// pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented
		// for DB2
		++index;
		pstmtInsertBatchMaster.setString(index,
				p_batchMasterVO.getDefaultLang());
		// pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented
		// for DB2
		++index;
		pstmtInsertBatchMaster.setString(index,
				p_batchMasterVO.getSecondLang());
		++index;
		pstmtInsertBatchMaster
				.setString(index, p_batchMasterVO.getUserId());

		int queryExecutionCount = pstmtInsertBatchMaster.executeUpdate();
		if (queryExecutionCount <= 0) {
			con.rollback();
			log.error("insertInC2CBatch",
					"Unable to insert in the batch master table.");
			BatchC2CFileProcessLog
					.detailLog(
							"insertInC2CBatch",
							p_batchMasterVO,
							null,
							"FAIL : DB Error Unable to insert in the batch master table",
							"queryExecutionCount=" + queryExecutionCount);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,
					EventComponentI.SYSTEM, EventStatusI.RAISED,
					EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[closeBatchC2CTransfer]", "",
					"", "", "Unable to insert in the batch master table.");
			throw new BTSLBaseException(this, "insertInC2CBatch",
					"error.general.sql.processing");
		}
		else
		{
			con.commit();
		}
		

		if(pstmtInsertBatchMaster != null) {
      		try {
      			pstmtInsertBatchMaster.close();
			} catch (SQLException e) {
				log.error("insertInC2CBatch", "Exception occured during statement close "
						+ e);
			}
      	}
		
		return queryExecutionCount;
		// ends here

		
    }
    
    
    
    
    private void insertInC2CBatchItems(Connection con,ChannelUserVO senderVO, C2CBatchMasterVO p_batchMasterVO,C2CBatchItemsVO batchItemsVO,ChannelTransferItemsVO channelTransferItemsVO, ChannelTransferVO channelTransferVO) throws BTSLBaseException, SQLException {
    	PreparedStatement pstmtInsertBatchItems = null;
    	String txnReceiverUserStatusChang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG);
		String txnSenderUserStatusChang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG);
		ChannelUserVO receiverChannelUserVO = new ChannelUserDAO().loadChannelUserDetails(con,
				channelTransferVO.getToUserCode());
		final StringBuffer strBuffInsertBatchItems = new StringBuffer(
				"INSERT INTO c2c_batch_items (batch_id, batch_detail_id, ");
		strBuffInsertBatchItems
				.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
		strBuffInsertBatchItems.append("transfer_date, txn_profile, ");
		strBuffInsertBatchItems
				.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
		strBuffInsertBatchItems
				.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
		strBuffInsertBatchItems
				.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
		strBuffInsertBatchItems
				.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status,transfer_type,transfer_sub_type,product_code) ");
		strBuffInsertBatchItems
				.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		if (log.isDebugEnabled()) {
			log.debug("insertInC2CBatchItems", "strBuffInsertBatchItems Query ="
					+ strBuffInsertBatchItems);
		}
		pstmtInsertBatchItems = (PreparedStatement) con
				.prepareStatement(strBuffInsertBatchItems.toString());
		

		int index = 0;
		++index;
		pstmtInsertBatchItems.setString(index,
				batchItemsVO.getBatchId());
		++index;
		pstmtInsertBatchItems.setString(index,
				batchItemsVO.getBatchDetailId());
		++index;
		pstmtInsertBatchItems.setString(index,
				batchItemsVO.getCategoryCode());
		++index;
		pstmtInsertBatchItems
				.setString(index, receiverChannelUserVO.getMsisdn());
		++index;
		pstmtInsertBatchItems
				.setString(index, receiverChannelUserVO.getUserID());
		++index;
		pstmtInsertBatchItems
				.setString(index, batchItemsVO.getStatus());
		++index;
		pstmtInsertBatchItems.setString(index,
				batchItemsVO.getModifiedBy());
		++index;
		pstmtInsertBatchItems
				.setTimestamp(index, BTSLUtil
						.getTimestampFromUtilDate(batchItemsVO
								.getModifiedOn()));
		++index;
		pstmtInsertBatchItems.setString(index,
				receiverChannelUserVO.getUserGrade());
		++index;
		pstmtInsertBatchItems
				.setDate(index, BTSLUtil
						.getSQLDateFromUtilDate(batchItemsVO
								.getTransferDate()));
		++index;
		pstmtInsertBatchItems.setString(index,
				receiverChannelUserVO.getTransferProfileID());
		++index;
		pstmtInsertBatchItems.setString(index,
				channelTransferVO.getCommProfileSetId());
		++index;
		pstmtInsertBatchItems.setString(index,
				channelTransferVO.getCommProfileVersion());
		++index;
		pstmtInsertBatchItems.setString(index,
				channelTransferItemsVO.getCommProfileDetailID());
		++index;
		pstmtInsertBatchItems.setString(index,
				channelTransferItemsVO.getCommType());
		++index;
		pstmtInsertBatchItems.setDouble(index,
				channelTransferItemsVO.getCommRate());
		++index;
		pstmtInsertBatchItems.setLong(index,
				channelTransferItemsVO.getCommValue());
		++index;
		pstmtInsertBatchItems.setString(index,
				channelTransferItemsVO.getTax1Type());
		++index;
		pstmtInsertBatchItems.setDouble(index,
				channelTransferItemsVO.getTax1Rate());
		++index;
		pstmtInsertBatchItems.setLong(index,
				channelTransferItemsVO.getTax1Value());
		++index;
		pstmtInsertBatchItems.setString(index,
				channelTransferItemsVO.getTax2Type());
		++index;
		pstmtInsertBatchItems.setDouble(index,
				channelTransferItemsVO.getTax2Rate());
		++index;
		pstmtInsertBatchItems.setLong(index,
				channelTransferItemsVO.getTax2Value());
		++index;
		pstmtInsertBatchItems.setString(index,
				channelTransferItemsVO.getTax3Type());
		++index;
		pstmtInsertBatchItems.setDouble(index,
				channelTransferItemsVO.getTax3Rate());
		++index;
		pstmtInsertBatchItems.setLong(index,
				channelTransferItemsVO.getTax3Value());
		++index;
		pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getRequiredQuantity());
		++index;
		pstmtInsertBatchItems.setLong(index,
				channelTransferItemsVO.getProductTotalMRP());
		// pstmtInsertBatchItems.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented
		// for DB2
		++index;
		pstmtInsertBatchItems.setString(index,
				batchItemsVO.getInitiatorRemarks());
		++index;
		pstmtInsertBatchItems.setString(index,
				receiverChannelUserVO.getExternalCode());
		++index;
		pstmtInsertBatchItems
				.setString(
						index,
						PretupsI.CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED);
		++index;
		pstmtInsertBatchItems.setString(index,
				batchItemsVO.getTransferType());
		++index;
		pstmtInsertBatchItems.setString(index,
				batchItemsVO.getTransferSubType());
		++index;
		pstmtInsertBatchItems.setString(index,
				channelTransferItemsVO.getProductCode());
		int queryExecutionCount = pstmtInsertBatchItems.executeUpdate();
		
		if(pstmtInsertBatchItems != null) {
      		try {
      			pstmtInsertBatchItems.close();
			} catch (SQLException e) {
				log.error("insertInC2CBatchItems", "Exception occured during statement close "
						+ e);
			}
      	}
		
		if (queryExecutionCount <= 0) {
			con.rollback();
			// put error record can not be inserted
			log.error("insertInC2CBatchItems",
					"Record cannot be inserted in batch items table");
			BatchC2CFileProcessLog
					.detailLog(
							"insertInC2CBatchItems",
							p_batchMasterVO,
							batchItemsVO,
							"FAIL : DB Error Record cannot be inserted in batch items table",
							"queryExecutionCount="
									+ queryExecutionCount);
		} else {
           
			if (batchItemsVO.getStatus().equals(
					PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE)) {
				boolean changeStatusRequired = false;
				String str[] = null;
				String newStatus[] = null;
				int updatecount2 = 0;
				int updatecount1 = 0;

				
				if (!PretupsI.USER_STATUS_ACTIVE.equals(receiverChannelUserVO
						.getStatus())) {
					// int
					// updatecount1=operatorUtili.changeUserStatusToActive(
					// p_con,channelTransferVO.getToUserID(),channelUserVO.getStatus())

					str = txnReceiverUserStatusChang.split(","); // "CH:Y,EX:Y".split(",")

					for (int l = 0; l < str.length; l++) {
						newStatus = str[l].split(":");
						if (newStatus[0].equals(receiverChannelUserVO
								.getStatus())) {
							changeStatusRequired = true;
							updatecount1 = _operatorUtil
									.changeUserStatusToActive(con,
											channelTransferVO
													.getToUserID(),
													receiverChannelUserVO.getStatus(),
											newStatus[1]);
							break;
						}
					}
				}
				/* if(updatecount1>0){ */
				if (!PretupsI.USER_STATUS_ACTIVE.equals(senderVO
						.getStatus())) {
					// int
					// updatecount2=operatorUtili.changeUserStatusToActive(
					// p_con,p_batchMasterVO.getUserId(),p_senderVO.getStatus())

					str = txnSenderUserStatusChang.split(","); // "CH:Y,EX:Y".split(",")
					for (int l = 0; l < str.length; l++) {
						newStatus = str[l].split(":");
						if (newStatus[0].equals(senderVO.getStatus())) {
							changeStatusRequired = true;
							updatecount2 = _operatorUtil
									.changeUserStatusToActive(
											con,
											p_batchMasterVO.getUserId(),
											senderVO.getStatus(),
											newStatus[1]);
							break;
						}
					}

				}
				if (changeStatusRequired) {
					if (updatecount2 > 0 || updatecount1 > 0) {
						
						BatchC2CFileProcessLog.detailLog("",
								p_batchMasterVO, batchItemsVO,
								"PASS : Order is closed successfully",
								"updateCount=" + queryExecutionCount);
					} else {
						con.rollback();
						throw new BTSLBaseException(this, "insertInC2CBatch",
								"error.general.sql.processing");
					}
				}
			}
			

    }
		
		
    }
    
    private void updateBatchItems(Connection con,int p_batchItemsList,C2CBatchMasterVO p_batchMasterVO) throws SQLException, BTSLBaseException
    {
    	PreparedStatement pstmtUpdateBatchMaster = null;
		final StringBuffer strBuffUpdateBatchMaster = new StringBuffer(
				"UPDATE c2c_batches SET batch_total_record=? , status =? WHERE batch_id=?");
		if (log.isDebugEnabled()) {
			log.debug("", "strBuffUpdateBatchMaster Query ="
					+ strBuffUpdateBatchMaster);
		}
		pstmtUpdateBatchMaster = con
				.prepareStatement(strBuffUpdateBatchMaster.toString());
    	if (errorSize == p_batchItemsList) {
			if (!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.YES))
			{
				int index=0;
				int queryExecutionCount=-1;
				pstmtUpdateBatchMaster.setInt(++index,p_batchMasterVO.getBatchTotalRecord()-errorSize);
				pstmtUpdateBatchMaster.setString(++index,PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_CANCEL);
				pstmtUpdateBatchMaster.setString(++index,p_batchMasterVO.getBatchId());
				queryExecutionCount=pstmtUpdateBatchMaster.executeUpdate();
			    if(queryExecutionCount<=0) //Means No Records Updated
	   		    {
	   		        log.error("","Unable to Update the batch size in master table..");
	   		        con.rollback();
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferWebDAO[initiateBatchC2CTransfer]","","","","Error while updating C2C_BATCHES table. Batch id="+p_batchMasterVO.getBatchId());
	   		    }
	   		    else
	   		    {
	   		        con.commit();
	   		    }
			}else
				con.rollback();
			log.error("",
					"ALL the records conatins errors and cannot be inserted in db");
			BatchC2CFileProcessLog
					.c2cBatchMasterLog(
							"",
							p_batchMasterVO,
							"FAIL : ALL the records conatins errors and cannot be inserted in DB ",
							"");
		}
		// else update the master table with the open status and total
		// number of records.
		else {
			int index = 0;
			int queryExecutionCount = -1;
			++index;
			pstmtUpdateBatchMaster.setInt(
					index,
					p_batchMasterVO.getBatchTotalRecord()
							- errorSize);
			++index;
			pstmtUpdateBatchMaster.setString(index,
					PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN);
			++index;
			pstmtUpdateBatchMaster.setString(index,
					p_batchMasterVO.getBatchId());
			queryExecutionCount = pstmtUpdateBatchMaster
					.executeUpdate();
			if (queryExecutionCount <= 0) // Means No Records Updated
			{
				log.error("",
						"Unable to Update the batch size in master table..");
				con.rollback();
				EventHandler
						.handle(EventIDI.SYSTEM_ERROR,
								EventComponentI.SYSTEM,
								EventStatusI.RAISED,
								EventLevelI.FATAL,
								"C2CBatchTransferWebDAO[initiateBatchC2CTransfer]",
								"", "", "",
								"Error while updating C2C_BATCHES table. Batch id="
										+ p_batchMasterVO.getBatchId());
				throw new BTSLBaseException(this, "insertInC2CBatch",
						"error.general.sql.processing");
			} else {
				con.commit();
			}
		}
    	



    	if(pstmtUpdateBatchMaster != null) {
      		try {
      			pstmtUpdateBatchMaster.close();
			} catch (SQLException e) {
				log.error("updateBatchItems", "Exception occured during statement close "
						+ e);
			}
      	}
    }
    public void writeExcel(List<List<String>> listBook, String excelFilePath) throws IOException {
    	Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        createHeaderRow(sheet);
        int rowCount = 0;
     
        for (List<String> rowData : listBook) {
            Row row = sheet.createRow(++rowCount);
            writeBook(rowData, row);
        }
     
        try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
        }
        
    }
    public void writeCSV(List<List<String>> listBook, String excelFilePath) throws IOException {
    	FileWriter csvWriter = new FileWriter(excelFilePath);
    	csvWriter.append("Line number");
    	csvWriter.append(Constants.getProperty("FILE_SEPARATOR_C2C"));
    	csvWriter.append("Mobile number/LoginId");
    	csvWriter.append(Constants.getProperty("FILE_SEPARATOR_C2C"));
    	csvWriter.append("Reason");
    	csvWriter.append("\n");

    	for (List<String> rowData : listBook) {
    	    csvWriter.append(String.join(Constants.getProperty("FILE_SEPARATOR_C2C"), rowData));
    	    csvWriter.append("\n");
    	}

    	csvWriter.flush();
    	csvWriter.close();
        
    }
    private void writeBook(List<String> data, Row row) {
        Cell cell = row.createCell(1);
        cell.setCellValue(data.get(0));
     
        cell = row.createCell(2);
        cell.setCellValue(data.get(1));
     
        cell = row.createCell(3);
        cell.setCellValue(data.get(2));
    }
    private void createHeaderRow(Sheet sheet) {
    	 
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints(BTSLUtil.parseIntToShort(16));
        cellStyle.setFont(font);
     
        Row row = sheet.createRow(0);
        Cell cellTitle = row.createCell(1);
     
        cellTitle.setCellStyle(cellStyle);
        cellTitle.setCellValue("Line Number");
     
        Cell cellAuthor = row.createCell(2);
        cellAuthor.setCellStyle(cellStyle);
        cellAuthor.setCellValue("Mobile Number/LoginID");
     
        Cell cellPrice = row.createCell(3);
        cellPrice.setCellStyle(cellStyle);
        cellPrice.setCellValue("Reason");
    }
    
    private void settingStaffDetails(ChannelUserVO channelUserVO) {

		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			channelUserVO.setActiveUserID(channelUserVO.getUserID());
			UserDAO userDao = new UserDAO();
            UserPhoneVO phoneVO = userDao.loadUserPhoneVO(con, channelUserVO.getUserID());
            if (phoneVO != null) {
                channelUserVO.setActiveUserMsisdn(phoneVO.getMsisdn());
                channelUserVO.setActiveUserPin(phoneVO.getSmsPin());
               }
            ChannelUserVO staffUserVO = new ChannelUserVO();
            UserPhoneVO staffphoneVO = new UserPhoneVO();
            BeanUtils.copyProperties(staffUserVO, channelUserVO);
            if (phoneVO != null) {
                BeanUtils.copyProperties(staffphoneVO, phoneVO);
                staffUserVO.setUserPhoneVO(staffphoneVO);
            }
            staffUserVO.setPinReset(channelUserVO.getPinReset());
            channelUserVO.setStaffUserDetails(staffUserVO);
            ChannelUserVO parentChannelUserVO = new UserDAO().loadUserDetailsFormUserID(con, channelUserVO.getParentID());
            staffUserDetails(channelUserVO, parentChannelUserVO);
            channelUserVO.setPrefixId(parentChannelUserVO.getPrefixId());
				
		}catch(Exception e) {
			
		}finally {
			if(mcomCon != null)
			{
				mcomCon.close("C2CTransferController#checkAndSetStaffVO");
				mcomCon=null;
			}
		}
		
	}
	
	protected void staffUserDetails(ChannelUserVO channelUserVO, ChannelUserVO parentChannelUserVO) {
        channelUserVO.setUserID(channelUserVO.getParentID());
        channelUserVO.setParentID(parentChannelUserVO.getParentID());
        channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
        channelUserVO.setStatus(parentChannelUserVO.getStatus());
        channelUserVO.setUserType(parentChannelUserVO.getUserType());
        channelUserVO.setStaffUser(true);
        channelUserVO.setMsisdn(parentChannelUserVO.getMsisdn());
        channelUserVO.setPinRequired(parentChannelUserVO.getPinRequired());
        channelUserVO.setSmsPin(parentChannelUserVO.getSmsPin());
        channelUserVO.setParentLoginID(parentChannelUserVO.getLoginID());
    }
    
}
