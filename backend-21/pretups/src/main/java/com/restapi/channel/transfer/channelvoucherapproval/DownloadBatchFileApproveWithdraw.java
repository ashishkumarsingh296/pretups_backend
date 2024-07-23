package com.restapi.channel.transfer.channelvoucherapproval;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchTransferDAO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.xl.ExcelRW;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.web.pretups.channel.transfer.businesslogic.C2CBatchTransferWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${DownloadBatchFileApproveWithdraw.name}", description = "${DownloadBatchFileApproveWithdraw.desc}")//@Api(tags= "File Operations", value="Channel User Services")
@RestController
@RequestMapping(value = "/v1/c2cFileServices")
public class DownloadBatchFileApproveWithdraw {
	protected final Log log = LogFactory.getLog(getClass().getName());
	
	@GetMapping(value= "/downloadBatchTxnWdrRecords", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Download C2C Batch Transfer Withdraw",response = DownloadBatchTxnWdrResponse.class,
				  authorizations = {
		    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = DownloadBatchTxnWdrResponse.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadBatchTxnWdrRecords.summary}", description="${downloadBatchTxnWdrRecords.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DownloadBatchTxnWdrResponse.class))
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



	public DownloadBatchTxnWdrResponse getDownloadFileBatch(HttpServletRequest httpServletRequest,@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "status", example = "",required = true) 
	@RequestParam("status") String status,
	@Parameter(description = "BatchId", example = "",required = true) 
	@RequestParam("BatchId") String batchId,
	HttpServletResponse response)throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException {
	final String methodName = "getDownloadFileBatch";
    if (log.isDebugEnabled()) {
        log.debug(methodName, "Entered");
    }
    String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
    String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
    Connection con = null;
    MComConnectionI mcomCon = null;
    OAuthUser oAuthUser = null;
	OAuthUserData oAuthUserData = null;
    DownloadBatchTxnWdrResponse fileDownloadResponse = new DownloadBatchTxnWdrResponse();
    try{
    	mcomCon = new MComConnection();
    	con=mcomCon.getConnection();
    	oAuthUser = new OAuthUser();
    	oAuthUserData = new OAuthUserData();
    	oAuthUser.setData(oAuthUserData);
    	OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response);
    	ChannelUserVO channelUserVO= new UserDAO().loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
    	
    	if(channelUserVO.getUserType().equals(PretupsI.USER_TYPE_STAFF)) {
    		channelUserVO= new UserDAO().loadUserDetailsFormUserID(con, channelUserVO.getParentID());
    	}
    	
    	final C2CBatchTransferWebDAO c2cBatchTransferWebDAO = new C2CBatchTransferWebDAO();
    	final ArrayList<C2CBatchMasterVO> c2cBatchMasterVOList ;
    	String statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";

    	if(status.equals("T"))
    		c2cBatchMasterVOList = c2cBatchTransferWebDAO.loadBatchC2CMasterDetailsForTxr(con, channelUserVO.getUserID(), statusUsed,
    				PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
    	else
    		c2cBatchMasterVOList = c2cBatchTransferWebDAO.loadBatchC2CMasterDetailsForWdr(con, channelUserVO.getUserID(), statusUsed,
    				PretupsI.CHANNEL_TRANSFER_ORDER_NEW);

      /*	Map<String, C2CBatchMasterVO> batchIds = c2cBatchMasterVOList.parallelStream().
    			collect(Collectors.toMap(C2CBatchMasterVO::getBatchId,b->b));
    	C2CBatchMasterVO c2CBatchMasterVO = new C2CBatchMasterVO();          
    	if(batchIds.containsKey(batchId))
    		c2CBatchMasterVO = batchIds.get(batchId);*/
    	
    	List<C2CBatchMasterVO> list= c2cBatchMasterVOList.stream().filter(p->p.getBatchId().equals(batchId)).collect(Collectors.toList());
    	C2CBatchMasterVO c2CBatchMasterVO = new C2CBatchMasterVO(); 
    	if(list.size()>0) {
             c2CBatchMasterVO = list.get(0);
    	}   	
    	else
    	{
    		String resmsg = RestAPIStringParser.getMessage(
    				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.EMPTY_BATCH_ID,
    				null);
    		fileDownloadResponse.setMessage(resmsg);
    		fileDownloadResponse.setMessageCode(PretupsErrorCodesI.BATCH_DETAIL_NO_NOT_FOUND);
    		fileDownloadResponse.setStatus(400);
    		fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
    		return fileDownloadResponse;
    	}
    	constructFormFromVO(fileDownloadResponse, c2CBatchMasterVO);
    	final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
    	final Date curDate = new Date();
    	channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, channelUserVO.getUserID(), false, curDate,false);
    	channelUserVO.setCommissionProfileSuspendMsg(channelUserVO.getCommissionProfileLang2Msg());
    	final Locale locale = new Locale(lang,country);
    	final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);

    	if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
    		channelUserVO.setCommissionProfileSuspendMsg(channelUserVO.getCommissionProfileLang1Msg());
    	}

    	if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
    		final String arugment = channelUserVO.getUserName();
    		final String args[] = { arugment, channelUserVO.getCommissionProfileSuspendMsg() };
    		throw new BTSLBaseException("C2CBatchTransferAction", "getDownloadFileBatch", "commissionprofile.notactive.msg", 0, args, "firstPage");
    	} else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
    		final String arugment = channelUserVO.getUserName();
    		final String args[] = { arugment };
    		throw new BTSLBaseException("C2CBatchTransferAction", "getDownloadFileBatch", "transferprofile.notactive.msg", 0, args, "firstPage");
    	} else if (channelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
    		final String args[] = new String[] { channelUserVO.getUserName() };
    		throw new BTSLBaseException("C2CBatchTransferAction", "getDownloadFileBatch", "message.channeltransfer.usernocommprofileapplicable.msg", 0, args, "firstPage");
    	}

    	if ("T".equals(status)) {
    		if (PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(channelUserVO.getOutSuspened())) {
    			final String args[] = new String[] { channelUserVO.getUserName() };
    			throw new BTSLBaseException("C2CBatchTransferAction", "getDownloadFileBatch", "channeltransfer.chnltochnlsearchuser.usernotfound.msg.transferoutsuspend",
    					0, args, "firstPage");
    		}
    	} else if ("W".equals(status)) {
    		if (PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
    			final String args[] = new String[] { channelUserVO.getUserName() };
    			throw new BTSLBaseException("C2CBatchTransferAction", "getDownloadFileBatch", "channeltransfer.chnltochnlsearchuser.usernotfound.msg.transferinsuspended",
    					0, args, "firstPage");
    		}
    	}
    	String exelFileID = null;
    	boolean isNotApprovalLevel1 = true;
    	exelFileID = ExcelFileIDI.BATCH_C2C_APPRV;
    	isNotApprovalLevel1 = false;
    	statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE + "'";
    	final C2CBatchTransferDAO c2cBatchTransferDAO = new C2CBatchTransferDAO();
    	// Load the data from the database
    	final LinkedHashMap downloadDataMap = c2cBatchTransferDAO.loadBatchItemsMap(con, batchId, statusUsed);
    	final String fileArr[][] = constructFileArrForDownload(downloadDataMap, isNotApprovalLevel1);
    	String filePath = Constants.getProperty("DownloadFilePathForC2CApproval");
    	try {
    		final File fileDir = new File(filePath);
    		if (!fileDir.isDirectory()) {
    			fileDir.mkdirs();
    		}
    	} catch (Exception e) {
    		log.errorTrace(methodName, e);
    		log.error("loadDownloadFile", "Exception" + e.getMessage());
    		throw new BTSLBaseException(this, "getDownloadFileBatch", "downloadfile.error.dirnotcreated", "error");
    	}
    	final String fileName = Constants.getProperty("DownloadFileNameForC2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + ".xls";
    	final ExcelRW excelRW = new ExcelRW();
    	// Write the exel file for download
    	excelRW.writeExcelForBC2Capprove(exelFileID, fileArr, null,new Locale(lang,country),
    			filePath + "" + fileName);
    	File fileNew = new File(filePath +""+ fileName);
    	byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
    	String encodedString = Base64.getEncoder().encodeToString(
    			fileContent);
    	String file1 = fileNew.getName();
    	fileDownloadResponse.setFileName(file1);
    	fileDownloadResponse.setFileType("xls");
    	fileDownloadResponse.setFileattachment(encodedString);
    	fileDownloadResponse.setStatus(200);
    	return fileDownloadResponse;

    }
    catch (BTSLBaseException be) {
      	 log.error(methodName, "Exception:e=" + be);
           log.errorTrace(methodName, be);
           if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
           		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
           	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
           	 fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
           }
            else{
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
          
   	   String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);
   	   fileDownloadResponse.setMessageCode(be.getMessage());
   	   fileDownloadResponse.setMessage(resmsg);
   	   
    	}
    catch (Exception ex) {
    	response.setStatus(HttpStatus.SC_BAD_REQUEST);
		fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
        log.errorTrace(methodName, ex);
        log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
    }
    finally {
    	if (mcomCon != null) {
			mcomCon.close("DownloadBatchFileApproveWithdraw#getDownloadFileBatch");
			mcomCon = null;
		}
        if (log.isDebugEnabled()) {
            log.debug("getDownloadFileBatch", " Exited ");
        }
    }

    

    return fileDownloadResponse;
}
	
    /**
     * This method will set the inforamation from the C2CBatchMasterVO to form
     * 
     * @param p_form
     * @param p_c2cBatchMatserVO
     */
    private void constructFormFromVO(DownloadBatchTxnWdrResponse p_form, C2CBatchMasterVO p_c2cBatchMatserVO) {
        final String methodName = "constructFormFromVO";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_c2cBatchMatserVO=" + p_c2cBatchMatserVO.toString());
        }
        p_form.setBatchNumber(p_c2cBatchMatserVO.getBatchId());
        p_form.setBatchName(p_c2cBatchMatserVO.getBatchName());
        p_form.setApprovedTransfers(String.valueOf(p_c2cBatchMatserVO.getApprovedRecords()));
        p_form.setClosedTransfers(String.valueOf(p_c2cBatchMatserVO.getClosedRecords()));
        p_form.setProduct(p_c2cBatchMatserVO.getProductType());
        p_form.setNewTransfers(String.valueOf(p_c2cBatchMatserVO.getNewRecords()));
        p_form.setRejectedTransfers(String.valueOf(p_c2cBatchMatserVO.getRejectedRecords()));
        p_form.setTotalTransfers(String.valueOf(p_c2cBatchMatserVO.getBatchTotalRecord()));
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }
    }
    
    private String[][] constructFileArrForDownload(LinkedHashMap p_dataMap, boolean p_level2DetailsShown) throws Exception {
        final String methodName = "constructFileArrForDownload";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_dataMap=" + p_dataMap + "p_level2DetailsShown=" + p_level2DetailsShown);
        }
        final int rows = p_dataMap.size();
        String fileArr[][];
        fileArr = new String[rows + 1][13];
        fileArr[0][0] = "batchc2c.downloadfileforapproval.label.bachdetailid";
        fileArr[0][1] = "batchc2c.downloadfileforapproval.label.mobilenumber";
        fileArr[0][2] = "batchc2c.downloadfileforapproval.label.usercat";
        fileArr[0][3] = "batchc2c.downloadfileforapproval.label.grade";
        fileArr[0][4] = "batchc2c.downloadfileforapproval.label.userlogin";
        fileArr[0][5] = "batchc2c.downloadfileforapproval.label.batchid";
        fileArr[0][6] = "batchc2c.downloadfileforapproval.label.qty";
        fileArr[0][7] = "batchc2c.downloadfileforapproval.label.externalcode";
        fileArr[0][8] = "batchc2c.downloadfileforapproval.label.initiatedby";
        fileArr[0][9] = "batchc2c.downloadfileforapproval.label.Initiatedon";
        fileArr[0][10] = "batchc2c.downloadfileforapproval.label.currentstatus";
        fileArr[0][11] = "batchc2c.downloadfileforapproval.label.requiredaction";
        fileArr[0][12] = "batchc2c.downloadfileforapproval.label.remarks";

        C2CBatchItemsVO c2cBatchItemVO = null;
        final Iterator iterator = p_dataMap.keySet().iterator();
        String key = null;
        int i = 0;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            c2cBatchItemVO = (C2CBatchItemsVO) p_dataMap.get(key);
            int col = 0;
            fileArr[i + 1][col++] = c2cBatchItemVO.getBatchDetailId();
            fileArr[i + 1][col++] = c2cBatchItemVO.getMsisdn();
            fileArr[i + 1][col++] = c2cBatchItemVO.getCategoryName();
            fileArr[i + 1][col++] = c2cBatchItemVO.getGradeName();
            fileArr[i + 1][col++] = c2cBatchItemVO.getLoginID();
            fileArr[i + 1][col++] = c2cBatchItemVO.getBatchId();
            try {
                fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(c2cBatchItemVO.getRequestedQuantity());
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            fileArr[i + 1][col++] = c2cBatchItemVO.getExternalCode();
            fileArr[i + 1][col++] = c2cBatchItemVO.getInitiaterName();
            try {
                fileArr[i + 1][col++] = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(c2cBatchItemVO.getInitiatedOn()));
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            fileArr[i + 1][col++] = ((LookupsVO) LookupsCache.getObject(PretupsI.CHANNEL_TRANSFER_ORDER_STATUS, c2cBatchItemVO.getStatus())).getLookupName();
            i++;
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting fileArr:=" + fileArr);
        }
        return fileArr;
    }
}
