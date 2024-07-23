package com.restapi.channelAdmin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
//import org.apache.struts.action.ActionForward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.user.businesslogic.BatchUserDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.transfer.businesslogic.errorfileresponse.ErrorFileResponse;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.channelAdmin.requestVO.BatchUploadAndProcessAssosiateAlertRequestVO;
import com.restapi.channelAdmin.responseVO.BatchUploadAndProcessAssosiateAlertResponseVO;
import com.restapi.channelAdmin.service.AssosiateAlertMsisdnServiceI;
import com.restapi.user.service.FileDownloadResponseMulti;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${AssosiateAlertMsisdnController.name}", description = "${AssosiateAlertMsisdnController.desc}")//@Api(tags ="Channel Admin", value="Channel Admin")
@RestController	
@RequestMapping(value = "/v1/channeladmin")
public class AssosiateAlertMsisdnController {
	
	public static final Log log = LogFactory.getLog(AssosiateAlertMsisdnController.class.getName());
	public static final String classname = "AssosiateAlertMsisdnController";
	
	@Autowired
	private AssosiateAlertMsisdnServiceI assosiateAlertMsisdnServiceI;
	
	@GetMapping(value= "/downloadFileForAssosiateAlert", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Download File For Assosiate Alert",
	           response = FileDownloadResponseMulti.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = FileDownloadResponseMulti.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
*/

    @io.swagger.v3.oas.annotations.Operation(summary = "${downloadFileForAssosiateAlert.summary}", description="${downloadFileForAssosiateAlert.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FileDownloadResponseMulti.class))
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


    public FileDownloadResponseMulti downloadFileForAssosiateAlert(
	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,

	//@Parameter(description = "domainCode", required = true, allowableValues = "Corporate,Distributor,OwnShops")
	@Parameter(description = "domainCode", required = true)//allowableValues = "CORP,DIST,COMP")
	@RequestParam("domainCode") String domainType,
	//@Parameter(description = "categoryCode", required = true, allowableValues = "Corporate Executive,Corporate Agent,Super Distributor,Dealer,Agent,Retailer,OwnShops")
	@Parameter(description = "categoryCode", required = true)// allowableValues = "CORPE,CORPA,DIST,SE,AG,SE,OS")
	@RequestParam("categoryCode") String categoryType,
	//@Parameter(description = "geoDomainCode", required = true, allowableValues = "Delhi-Ncr,UP (West)")
	@Parameter(description = "geoDomainCode", required = true)//allowableValues = "ALL,DELHI,UW")
	@RequestParam("geoDomainCode") String geoDomainType,
	@Parameter(description = "calledFile", required = true)//allowableValues = "USERLISTINGTEMPLATE,SAMPLETEMPLATE")
	@RequestParam("calledFile") String calledFile,
	 HttpServletResponse responseSwag,
	 HttpServletRequest request
			)throws Exception{

		
		final String methodName =  "downloadFileForAssosiateAlert";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		FileDownloadResponseMulti response=null;
		response = new FileDownloadResponseMulti();
		
		
		DownloadFileForAssosiateAlertVO downloadFileForAssosiateAlertVO = new DownloadFileForAssosiateAlertVO();
		
		Connection con = null;MComConnectionI mcomCon = null;
        String fileName = "";
        Writer fileWriter = null;
        BatchUserDAO batchUserDAO = null;
        ListValueVO listVO1 = null;
        CategoryVO categoryVO = null;
        ChannelUserVO ChannelUserData = null;
        
		UserDAO userDao = new UserDAO();
        Locale locale = null;
        
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		
		CategoryDAO categoryDAO = null;
		
		ArrayList geoList = null;
		
		File file=null;
        
        try {
        	locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			
            //final AlertMsisdnForm theForm = (AlertMsisdnForm) form;
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            
            oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,responseSwag);			

			ChannelUserVO userVO = userDao.loadAllUserDetailsByLoginID( con, oAuthUser.getData().getLoginid() );
            
            //final UserVO userVO = this.getUserFormSession(request);
            String filePath = Constants.getProperty("DownloadUserListingTemplatePath");
            if (BTSLUtil.isNullString(filePath)) {
                throw new BTSLBaseException(this, "loadDownloadFile", "channel.loadDownloadFile.error.downloadpathnotdefined", "selectZoneForInitiate");
            }
            try {
                final File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                log.error("loadDownloadFile", "Exception" + e.getMessage());
                throw new BTSLBaseException(this, "loadDownloadFile", "downloadfile.error.dirnotcreated", "selectZoneForInitiate");
            }
            
            
            //setting  domain, category and geography
            
            //load domains
            final ArrayList domainListAll = new DomainDAO().loadCategoryDomainList(con);
            if (domainListAll == null || domainListAll.isEmpty()) {
                throw new BTSLBaseException(this, "selectZoneForUpload", "channel.processUploadedFile.error.msg.nodomainlist", "selectZoneForInitiate");
            } else {
            	downloadFileForAssosiateAlertVO.setDomainAllList(domainListAll);
            }

            ArrayList domainList = userVO.getDomainList();
            if ((domainList == null || domainList.isEmpty()) && PretupsI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO
                .getCategoryVO().getFixedDomains())) {
                domainList = domainListAll;
            }
            if (domainList == null || domainList.isEmpty()) {
                throw new BTSLBaseException(this, "selectZoneForUpload", "channel.processUploadedFile.error.msg.nodomainlist", "selectZoneForInitiate");
            } else {
            	downloadFileForAssosiateAlertVO.setDomainList(BTSLUtil.displayDomainList(domainList));
            }
            if (domainList.size() == 1) {
                final ListValueVO listVO = BTSLUtil.getOptionDesc(((ListValueVO) domainList.get(0)).getValue(), domainListAll);
                downloadFileForAssosiateAlertVO.setDomainName(listVO.getLabel());
                downloadFileForAssosiateAlertVO.setDomainCode(listVO.getValue());
                downloadFileForAssosiateAlertVO.setDomainType(listVO.getOtherInfo());
            }
            
            //load category
            categoryDAO = new CategoryDAO();
            final ArrayList catlist = categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT);
            if (catlist == null || catlist.isEmpty()) {
                throw new BTSLBaseException(this, "selectZoneForUpload", "channel.processUploadedFile.error.msg.nocategorylist");
            }
            downloadFileForAssosiateAlertVO.setCategoryList(catlist);
            
            //setting category code
            downloadFileForAssosiateAlertVO.setCategoryCode(categoryType);
            
            //load geography
            downloadFileForAssosiateAlertVO.setGeographyList(userVO.getGeographicalAreaList());
            final ArrayList geoDomainList = downloadFileForAssosiateAlertVO.getGeographyList();
            if (geoDomainList != null && !geoDomainList.isEmpty()) {
                UserGeographiesVO userGeographiesVO = null;
                geoList = new ArrayList();
                for (int i = 0, j = geoDomainList.size(); i < j; i++) {

                    userGeographiesVO = (UserGeographiesVO) geoDomainList.get(i);
                    geoList.add(new ListValueVO(userGeographiesVO.getGraphDomainName(), userGeographiesVO.getGraphDomainCode()));
                }
                if (geoList != null && geoList.size() == 1) {
                    final ListValueVO listVO = (ListValueVO) geoList.get(0);
                    downloadFileForAssosiateAlertVO.setGeographyCode(listVO.getValue());
                    downloadFileForAssosiateAlertVO.setGeographyName(listVO.getLabel());
                    downloadFileForAssosiateAlertVO.setGeographyList(geoList);
                } else {
                	downloadFileForAssosiateAlertVO.setGeographyList(geoList);
                }
            } else {
                throw new BTSLBaseException(this, "loadDownloadFile", "channel.processUploadedFile.error.msg.nogeodomain", "selectZoneForInitiate");
            }
            
            
            //continuing code for downloading file
            if (!BTSLUtil.isNullString(geoDomainType) && geoDomainType.equals(TypesI.ALL)) {
//            	downloadFileForAssosiateAlertVO.setGeographyName(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "list.all"));
            	downloadFileForAssosiateAlertVO.setGeographyName(RestAPIStringParser.getMessage(locale, "list.all", null));
            } else {
                listVO1 = BTSLUtil.getOptionDesc(geoDomainType, downloadFileForAssosiateAlertVO.getGeographyList());
                downloadFileForAssosiateAlertVO.setGeographyName(listVO1.getLabel());
            }

            final ArrayList catlist1 = downloadFileForAssosiateAlertVO.getCategoryList();
            if (catlist1 != null && !catlist1.isEmpty()) {
            	int catlists=catlist1.size();
                for (int i = 0, j = catlists; i < j; i++) {
                    categoryVO = (CategoryVO) catlist1.get(i);
                    if (categoryVO.getCategoryCode().equalsIgnoreCase(downloadFileForAssosiateAlertVO.getCategoryCode())) {
                    	downloadFileForAssosiateAlertVO.setCategoryVO(categoryVO);
                    	downloadFileForAssosiateAlertVO.setCategoryName(categoryVO.getCategoryName());
                        break;
                    }
                }
            } else {
                throw new BTSLBaseException(this, "loadDownloadFile", "channel.processUploadedFile.error.msg.nocategorylist", "selectZoneForInitiate");
            }
            
            System.out.println("end");
            
            
            
            final ArrayList domainList1 = downloadFileForAssosiateAlertVO.getDomainList();
            if (domainList != null && !domainList.isEmpty()) {
                listVO1 = BTSLUtil.getOptionDesc(domainType, downloadFileForAssosiateAlertVO.getDomainAllList());
                downloadFileForAssosiateAlertVO.setDomainName(listVO1.getLabel());
                downloadFileForAssosiateAlertVO.setDomainType(listVO1.getOtherInfo());
            } else {
                throw new BTSLBaseException(this, "loadDownloadFile", "channel.processUploadedFile.error.msg.nodomainlist", "selectZoneForInitiate");
            }
            batchUserDAO = new BatchUserDAO();
            final ArrayList dataList = batchUserDAO.loadBatchUserListForModify(con, geoDomainType, categoryVO.getCategoryCode(), userVO.getUserID());
            if (dataList == null || dataList.isEmpty()) {
                throw new BTSLBaseException(this, "selectZoneForUpload", "channel.processUploadedFile.error.msg.nodaodatalist");
            }
            //final String calledFile = request.getParameter("getFile");

            if (BTSLUtil.isNullString(Constants.getProperty("DownloadFileTemplateWithUserData"))) {
                throw new BTSLBaseException(this, "loadDownloadFile", "channel.processUploadedFile.error.msg.nofile1name", "selectZoneForInitiate");
            }
            if (BTSLUtil.isNullString(Constants.getProperty("DownloadFileBlankTemplate"))) {
                throw new BTSLBaseException(this, "loadDownloadFile", "channel.processUploadedFile.error.msg.nofile2name", "selectZoneForInitiate");
            }
            
            //new
            
            
            if (calledFile.equals(Constants.getProperty("DownloadFileTemplateWithUserData"))) {
                fileName = Constants.getProperty("DownloadFileTemplateWithUserData") + BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";
//                final File file = new File(filePath + "" + fileName);
                file = new File(filePath + "" + fileName);
                try {
                    fileWriter = new BufferedWriter(new FileWriter(file));
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    log.error("loadDownloadFile", "Exception" + e.getMessage());
                }
                try {
                    // writing heading
                    //fileWriter.write(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "channel.associateAlertMsisdn.file1.heading") + ",");
                	String message1 = RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file1.heading", null);
                	fileWriter.write( message1 + ",");
                    fileWriter.write("\n");
//                    fileWriter.write(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "channel.associateAlertMsisdn.file1.usrCreatedBy") + "," + userVO
//                        .getUserName());
                    fileWriter.write(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file1.usrCreatedBy", null)+ "," + userVO
                          .getUserName());
                    fileWriter.write("\n");
                    fileWriter.write( RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file1.domainName", null) + "," + downloadFileForAssosiateAlertVO
                        .getDomainName());
                    fileWriter.write("\n");
                    fileWriter.write(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file1.categoryName", null) + "," + downloadFileForAssosiateAlertVO
                        .getCategoryName());
                    fileWriter.write("\n");
                    fileWriter
                        .write( RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file1.GeographyName", null) + "," + downloadFileForAssosiateAlertVO
                            .getGeographyName());
                    fileWriter.write("\n");

                    // writing labels of the columns
                    fileWriter.write(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file1.lable.mobileNumber", null) + ",");
                    fileWriter.write(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file1.lable.category", null) + ",");
                    fileWriter.write(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file1.lable.alertMSISDN", null) + ",");
                    fileWriter.write(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file1.lable.alertType", null) + ",");
                    fileWriter.write(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file1.lable.alertEmail", null) + ",");
                    fileWriter.write("\n");
                    fileWriter.write("SOF,,,,,");
                    fileWriter.write("\n");
                    if (!dataList.isEmpty()) {
                        for (int i = 0; i < dataList.size(); i++) {
                            ChannelUserData = (ChannelUserVO) dataList.get(i);
                            fileWriter
                                .write(ChannelUserData.getUserPhoneVO().getMsisdn() + "," + ChannelUserData.getCategoryName() + "," + (ChannelUserData.getAlertMsisdn() == null ? "" : ChannelUserData
                                    .getAlertMsisdn()) + "," + (ChannelUserData.getAlertType() == null ? "" : ChannelUserData.getAlertType()) + "," + (ChannelUserData
                                    .getAlertEmail() == null ? "" : ChannelUserData.getAlertEmail()));
                            fileWriter.write("\n");
                        }
                    }
                    fileWriter.write("EOF,");
                    fileWriter.write("\n");
                    fileWriter.close();
                } catch (IOException ioex) {
                    log.errorTrace(methodName, ioex);
                    log.error("loadDownloadFile", "Unable to write data into a file Exception = " + ioex.getMessage());
                }
            }else if (calledFile.equals(Constants.getProperty("DownloadFileBlankTemplate"))) {
                fileName = Constants.getProperty("DownloadFileBlankTemplate") + BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";
//                final File file = new File(filePath + "" + fileName);
                file = new File(filePath + "" + fileName);
                try {
                    fileWriter = new BufferedWriter(new FileWriter(file));
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    log.error("loadDownloadFile", "Exception" + e.getMessage());
                }
                try {
                    // writing heading
//                    final String heading = this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "channel.associateAlertMsisdn.file2.heading");
//                    fileWriter.write(heading);
                	fileWriter.write(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file2.heading", null)+ ",");
                    fileWriter.write("\n");
//                    fileWriter.write(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "channel.associateAlertMsisdn.file1.usrCreatedBy") + "," + userVO
//                        .getUserName());
                    fileWriter.write(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file1.usrCreatedBy", null)+ "," + userVO
                            .getUserName());
                      fileWriter.write("\n");
//                    fileWriter.write(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "channel.associateAlertMsisdn.file1.domainName") + "," + theForm
//                        .getDomainName());
                      fileWriter.write( RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file1.domainName", null) + "," + downloadFileForAssosiateAlertVO
                              .getDomainName());
                    fileWriter.write("\n");
//                    fileWriter.write(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "channel.associateAlertMsisdn.file1.categoryName") + "," + theForm
//                        .getCategoryName());
                    fileWriter.write(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file1.categoryName", null) + "," + downloadFileForAssosiateAlertVO
                            .getCategoryName());
                    fileWriter.write("\n");
//                    fileWriter
//                        .write(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "channel.associateAlertMsisdn.file1.GeographyName") + "," + theForm
//                            .getGeographyName());
                    fileWriter
                    .write( RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file1.GeographyName", null) + "," + downloadFileForAssosiateAlertVO
                        .getGeographyName());
                    fileWriter.write("\n");
                    // writing labels of the columns
//                    fileWriter.write(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "channel.associateAlertMsisdn.file2.lable.mobileNumber") + ",");
//                    fileWriter.write(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "channel.associateAlertMsisdn.file2.lable.alertMSISDN") + ",");
//                    fileWriter.write(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "channel.associateAlertMsisdn.file2.lable.alertType") + ",");
//                    fileWriter.write(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "channel.associateAlertMsisdn.file2.lable.alertEmail") + ",");
                    fileWriter.write(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file2.lable.mobileNumber", null) + ",");
                    fileWriter.write(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file2.lable.alertMSISDN", null) + ",");
                    fileWriter.write(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file2.lable.alertType", null) + ",");
                    fileWriter.write(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file2.lable.alertEmail", null) + ",");
                    fileWriter.write("\n");
                    fileWriter.write("SOF,,,,,");
                    fileWriter.write("\n");
                    fileWriter.write("EOF,,,,,");
                    fileWriter.close();
                } catch (IOException ioex) {
                    log.error("loadDownloadFile", "Unable to write data into a file Exception = " + ioex.getMessage());
                    log.errorTrace(methodName, ioex);
                }
            }
            
            
            byte[] fileContent = FileUtils.readFileToByteArray(file);
			String encodedString = Base64.getEncoder().encodeToString(
					fileContent);
			String file1 = file.getName();
			response.setFileattachment(encodedString);
			response.setFileType("csv");
			response.setFileName(file1);
			String sucess = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(sucess);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

            filePath = BTSLUtil.encrypt3DesAesText(filePath);
            //
            
            
            
        }
        catch (BTSLBaseException e) {
            log.error("loadDownloadFile", "Exception:e=" + e);
            log.errorTrace(methodName, e);
            if (!BTSLUtil.isNullString(e.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
				response.setMessageCode(e.getMessage());
				response.setMessage(msg);
				response.setStatus("400");
				//responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
            //return super.handleError(this, "loadDownloadFile", e, request, mapping);
        } catch (Exception e) {
            log.error("loadDownloadFile", "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus("400");
            //return super.handleError(this, "loadDownloadFile", e, request, mapping);
        } finally {
        	
        	try {
                if (fileWriter != null) {
                	fileWriter.close();
                }
            } catch (Exception e1 ){
                log.errorTrace(methodName, e1);
            }
        	
			if (mcomCon != null) {
				mcomCon.close("AlertMsisdnAction#loadDownloadFile");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug("loadDownloadFile", "Exiting:forward=" );
            }
        }
		 
		 
		 
		 
		 return response;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@PostMapping(value = "/uploadAndProcessBatchAssosiateAlert", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Upload Base64 encoded file for Assosiate Alert Msisdn of Channel Users",
					response = BatchOperatorUserInitiateResponseVO.class,
					authorizations = {
					@Authorization(value = "Authorization") })

	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = BatchUploadAndProcessAssosiateAlertResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

    @io.swagger.v3.oas.annotations.Operation(summary = "${uploadAndProcessBatchAssosiateAlert.summary}", description="${uploadAndProcessBatchAssosiateAlert.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BatchUploadAndProcessAssosiateAlertResponseVO.class))
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



    public BatchUploadAndProcessAssosiateAlertResponseVO uploadAndProcessBatchAssosiateAlert(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,

			//@Parameter(description = "domainCode", required = true, allowableValues = "Corporate,Distributor,OwnShops")
			@Parameter(description = "domainCode", required = true)// allowableValues = "CORP,DIST,COMP")
			@RequestParam("domainCode") String domainType,
			//@Parameter(description = "categoryCode", required = true,allowableValues = "Corporate Executive,Corporate Agent,Super Distributor,Dealer,Agent,Retailer,OwnShops")
			@Parameter(description = "categoryCode", required = true)// allowableValues = "CORPE,CORPA,DIST,SE,AG,SE,OS")
			@RequestParam("categoryCode") String categoryType,
			//@Parameter(description = "geoDomainCode", required = true,allowableValues = "Delhi-Ncr,UP (West)")
			@Parameter(description = "geoDomainCode", required = true)//allowableValues = "ALL,DELHI,UW")
			@RequestParam("geoDomainCode") String geoDomainType,
			@RequestBody BatchUploadAndProcessAssosiateAlertRequestVO request,
			 HttpServletResponse responseSwag) {
		
		final String methodName = "uploadAndProcessBatchAssosiateAlert";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		//common
		BatchUploadAndProcessAssosiateAlertVO batchUploadAndProcessAssosiateAlertVO = new BatchUploadAndProcessAssosiateAlertVO();
		BatchUploadAndProcessAssosiateAlertResponseVO response = null;
		
		//loadDownloadFile
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		UserDAO userDao = new UserDAO();
		ListValueVO listVO1 = null;
		CategoryDAO categoryDAO = null;
		ArrayList geoList = null;
		Locale locale = null;
		
		//process
        boolean success = false;
        
        //upload  function variables taken from struts
        final String forwarderrorJSP = "showResult";
        final String forwardBack = "loadFileForAlertMsisdn";
        int totalRecords = 0;
        int records = 0;
        int newLines = 0;
        final int DATAROWOFFSET = 8;
        final int DATAROWOFFSETUSERLIST = 7;
        boolean isEndFound = false;
        boolean fileValidationErrorExists = false;
        String[] arr = null;
        FileReader fileReader = null;
        //FileReader fileReader;
        BufferedReader bufferReader = null;
        Connection con = null;MComConnectionI mcomCon = null;
        //ActionForward forward = null;
        ArrayList fileContents = null;
        ArrayList fileErrorList = null;
        String filePath = null;
        String tempStr = null;
        String fileSize = null;
        CategoryVO categoryVO = null;
        ListValueVO errorVO = null;
        //ListValueVO listVO = null;
        BatchUserDAO batchUserDAO = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        
        //file upload variables
        final String file = request.getFileName();
        BufferedReader br = null;
        String line = null;
        InputStream is = null;
        InputStreamReader inputStreamReader = null;
        ReadGenericFileUtil fileUtil = null;
        HashMap<String, String> fileDetailsMap = null;
        boolean isFileUploaded = false;
        boolean isFileProcessed = false;
        
        ArrayList fileContentsSampleNoError = null;
        ArrayList fileContentsUserListingNoError = null;
        
        if(request.getFileType().equals("csv")) {
        	request.setFileType("text/csv");
        }
		
        try {
        	//common
        	ErrorMap errorMap = new ErrorMap();
        	response = new BatchUploadAndProcessAssosiateAlertResponseVO();
        	//common ends
        	
        	channelUserWebDAO = new ChannelUserWebDAO();
        	//final AlertMsisdnForm theForm = (AlertMsisdnForm) form;
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,responseSwag);			

			ChannelUserVO userVO = userDao.loadAllUserDetailsByLoginID( con, oAuthUser.getData().getLoginid() );
			
			//old code from downloadFileForAssosiateAlert starts
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			//load domains
            final ArrayList domainListAll = new DomainDAO().loadCategoryDomainList(con);
            if (domainListAll == null || domainListAll.isEmpty()) {
                throw new BTSLBaseException(this, "selectZoneForUpload", "channel.processUploadedFile.error.msg.nodomainlist", "selectZoneForInitiate");
            } else {
            	batchUploadAndProcessAssosiateAlertVO.setDomainAllList(domainListAll);
            }

            ArrayList domainList = userVO.getDomainList();
            if ((domainList == null || domainList.isEmpty()) && PretupsI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO
                .getCategoryVO().getFixedDomains())) {
                domainList = domainListAll;
            }
            if (domainList == null || domainList.isEmpty()) {
                throw new BTSLBaseException(this, "selectZoneForUpload", "channel.processUploadedFile.error.msg.nodomainlist", "selectZoneForInitiate");
            } else {
            	batchUploadAndProcessAssosiateAlertVO.setDomainList(BTSLUtil.displayDomainList(domainList));
            }
            if (domainList.size() == 1) {
                final ListValueVO listVO = BTSLUtil.getOptionDesc(((ListValueVO) domainList.get(0)).getValue(), domainListAll);
                batchUploadAndProcessAssosiateAlertVO.setDomainName(listVO.getLabel());
                batchUploadAndProcessAssosiateAlertVO.setDomainCode(listVO.getValue());
                batchUploadAndProcessAssosiateAlertVO.setDomainType(listVO.getOtherInfo());
            }
            
            //load category
            categoryDAO = new CategoryDAO();
            final ArrayList catlist = categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT);
            if (catlist == null || catlist.isEmpty()) {
                throw new BTSLBaseException(this, "selectZoneForUpload", "channel.processUploadedFile.error.msg.nocategorylist");
            }
            batchUploadAndProcessAssosiateAlertVO.setCategoryList(catlist);
            
            //setting category code
            batchUploadAndProcessAssosiateAlertVO.setCategoryCode(categoryType);
            
            //load geography
            batchUploadAndProcessAssosiateAlertVO.setGeographyList(userVO.getGeographicalAreaList());
            final ArrayList geoDomainList = batchUploadAndProcessAssosiateAlertVO.getGeographyList();
            if (geoDomainList != null && !geoDomainList.isEmpty()) {
                UserGeographiesVO userGeographiesVO = null;
                geoList = new ArrayList();
                for (int i = 0, j = geoDomainList.size(); i < j; i++) {

                    userGeographiesVO = (UserGeographiesVO) geoDomainList.get(i);
                    geoList.add(new ListValueVO(userGeographiesVO.getGraphDomainName(), userGeographiesVO.getGraphDomainCode()));
                }
                if (geoList != null && geoList.size() == 1) {
                    final ListValueVO listVO = (ListValueVO) geoList.get(0);
                    batchUploadAndProcessAssosiateAlertVO.setGeographyCode(listVO.getValue());
                    batchUploadAndProcessAssosiateAlertVO.setGeographyName(listVO.getLabel());
                    batchUploadAndProcessAssosiateAlertVO.setGeographyList(geoList);
                } else {
                	batchUploadAndProcessAssosiateAlertVO.setGeographyList(geoList);
                }
            } else {
                throw new BTSLBaseException(this, "loadDownloadFile", "channel.processUploadedFile.error.msg.nogeodomain", "selectZoneForInitiate");
            }
            
            
            //continuing code for downloading file
            if (!BTSLUtil.isNullString(geoDomainType) && geoDomainType.equals(TypesI.ALL)) {
//            	downloadFileForAssosiateAlertVO.setGeographyName(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "list.all"));
            	batchUploadAndProcessAssosiateAlertVO.setGeographyName(RestAPIStringParser.getMessage(locale, "list.all", null));
            } else {
                listVO1 = BTSLUtil.getOptionDesc(geoDomainType, batchUploadAndProcessAssosiateAlertVO.getGeographyList());
                batchUploadAndProcessAssosiateAlertVO.setGeographyName(listVO1.getLabel());
            }

            final ArrayList catlist1 = batchUploadAndProcessAssosiateAlertVO.getCategoryList();
            if (catlist1 != null && !catlist1.isEmpty()) {
            	int catlists=catlist1.size();
                for (int i = 0, j = catlists; i < j; i++) {
                    categoryVO = (CategoryVO) catlist1.get(i);
                    if (categoryVO.getCategoryCode().equalsIgnoreCase(batchUploadAndProcessAssosiateAlertVO.getCategoryCode())) {
                    	batchUploadAndProcessAssosiateAlertVO.setCategoryVO(categoryVO);
                    	batchUploadAndProcessAssosiateAlertVO.setCategoryName(categoryVO.getCategoryName());
                        break;
                    }
                }
            } else {
                throw new BTSLBaseException(this, "loadDownloadFile", "channel.processUploadedFile.error.msg.nocategorylist", "selectZoneForInitiate");
            }
            
            System.out.println("end");
            
            
            
            final ArrayList domainList1 = batchUploadAndProcessAssosiateAlertVO.getDomainList();
            if (domainList != null && !domainList.isEmpty()) {
                listVO1 = BTSLUtil.getOptionDesc(domainType, batchUploadAndProcessAssosiateAlertVO.getDomainAllList());
                batchUploadAndProcessAssosiateAlertVO.setDomainName(listVO1.getLabel());
                batchUploadAndProcessAssosiateAlertVO.setDomainType(listVO1.getOtherInfo());
            } else {
                throw new BTSLBaseException(this, "loadDownloadFile", "channel.processUploadedFile.error.msg.nodomainlist", "selectZoneForInitiate");
            }
            batchUserDAO = new BatchUserDAO();
            final ArrayList dataList = batchUserDAO.loadBatchUserListForModify(con, geoDomainType, categoryVO.getCategoryCode(), userVO.getUserID());
            if (dataList == null || dataList.isEmpty()) {
                throw new BTSLBaseException(this, "selectZoneForUpload", "channel.processUploadedFile.error.msg.nodaodatalist");
            }
			//old code from downloadFileForAssosiateAlert ends
            
            
          //adding code for file upload***********-------------------------------------------
        	fileDetailsMap = new HashMap<String, String>();
			fileUtil = new ReadGenericFileUtil();
			fileDetailsMap.put(PretupsI.FILE_NAME, request.getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, request.getFileAttachment());
			fileDetailsMap.put(PretupsI.FILE_TYPE, request.getFileType());
			validateFileDetailsMap(fileDetailsMap);
        	
        	//adding code for file upload  - ends
        	
            // this section checks for the valid name for the file
            final String fileName = request.getFileName();// accessing
            // name
            // of
            // the
            // file
            final boolean message = BTSLUtil.isValideFileName(fileName);// validating
            // name of the
            // file
            // if not a valid file name then throw exception
            if (!message) {
//                throw new BTSLBaseException(this, "confirmFileUploadForUnReg", "invalid.uploadfile.msg.unsuccessupload", "uploadSubscriberFileForUnReg");
            		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE, PretupsI.RESPONSE_FAIL, null);
            }// akanksha ends
             // Cross site Scripting removal
            //
            //String fileData = request.getFileAttachment();
            //
            
            //final byte[] data = fileData.getBytes();// ak
            
            try {
                filePath = Constants.getProperty("UploadAlertMsisdnFilePath");
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                if (log.isDebugEnabled()) {
                    log.debug("processUploadedFile", "File path not defined in Constant Property file");
                }
                throw new BTSLBaseException(this, "processUploadedFile", "channel.processUploadedFile.error.norecordinfile");
            }
            final File filePathAndFileName = new File(filePath + "" + fileName + ".csv");
            if (log.isDebugEnabled()) {
                log.debug("processUploadedFile", "Initializing the fileReader, filepath : " + filePathAndFileName);
            }
            
            
            final byte[] data = fileUtil.decodeFile(request.getFileAttachment());
            is = new ByteArrayInputStream(data);
            inputStreamReader = new InputStreamReader(is);
            br = new BufferedReader(inputStreamReader);
//            while ((line = br.readLine()) != null) {
//                final boolean isFileContentValid = BTSLUtil.isFileContentValid(line);
//                if (!isFileContentValid) {
////                    throw new BTSLBaseException(this, "confirmFileUploadForUnReg", "routing.delete.msg.invalidfilecontent", "uploadSubscriberFileForUnReg");
//                	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_CONTENT, PretupsI.RESPONSE_FAIL, null);
//                }
//            }
            
            batchUploadAndProcessAssosiateAlertVO.setFileNameStr(request.getFileName());
            
            
            //new function uploadAndProcessFile from struts file
            //final String dir = Constants.getProperty("UploadFileForUnRegPath");
            
            //final String dir = Constants.getProperty("UploadAlertMsisdnFilePath");
            
            if (request.getFileType().equals("text/csv")) {
				request.setFileType(BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT));
				//final String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);
				final String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_CSV);
				//String fileSize = Constants.getProperty("OTHER_FILE_SIZE");
				fileSize = Constants.getProperty("MAX_CSV_FILE_SIZE_FOR_ALERTMSISDN");
				//isFileUploaded = BTSLUtil.uploadFileToServerWithHashMap(fileDetailsMap, dir, contentType, "uploadSubscriberFileForUnReg", Long.parseLong(fileSize),data, request.getFileType());
				//isFileUploaded = BTSLUtil.uploadFileToServerWithHashMap(fileDetailsMap, dir, contentType, "uploadSubscriberFileForUnReg", Long.parseLong(fileSize),data, request.getFileType());
				isFileUploaded = BTSLUtil.uploadCsvFileToServerWithHashMapForAssosiateAlert(fileDetailsMap, filePath, contentType, forwardBack, data, Long.parseLong(fileSize));
				
//				if (isFileUploaded) {
//                    // now process uploaded file
//                   // forward = this.processUploadedFileForUnReg(mapping, form, request);
//					//isFileProcessed = deRegisterSubscriberBatchServiceI.processUploadedFileForUnRegSubscriber(fileDetailsMap,request,requestSwag,userVO,con,deRegisterSubscriberBatchVO,response,responseSwag);
//					//response = deRegisterSubscriberBatchServiceI.processUploadedFileForUnRegSubscriber(fileDetailsMap,request,requestSwag,userVO,con,deRegisterSubscriberBatchVO,response,responseSwag);
//                } else {
////                    throw new BTSLBaseException(this, "uploadAndProcessFile", "p2p.subscriber.uploadsubscriberfileforunreg.error.filenotuploaded",
////                        "uploadSubscriberFileForUnReg");
//                	throw new BTSLBaseException(this, methodName , PretupsErrorCodesI.FILE_UPLOAD_ERROR_ON_SERVER, PretupsI.RESPONSE_FAIL, null);
//                }
            }
            
//            if(isFileProcessed) {
//            	response.setMessageCode(Integer.toString(HttpStatus.SC_OK));
//    			response.setMessage("De-registered successfully");
//    			responseSwag.setStatus(HttpStatus.SC_OK);
//    			//response.setStatus(200);
//            }
            
            if (!isFileUploaded) {
                throw new BTSLBaseException(this, "processUploadedFile", "channel.processUploadedFile.error.filenotuploaded", forwardBack);
            }

            final String fileTemp1 = Constants.getProperty("DownloadFileTemplateWithUserData");
            if (BTSLUtil.isNullString(fileTemp1)) {
                throw new BTSLBaseException(this, "processUploadedFile", "channel.processUploadedFile.error.msg.nofile1name", "loadFileForAlertMsisdn");
            }
            final String fileTemp2 = Constants.getProperty("DownloadFileBlankTemplate");
            if (BTSLUtil.isNullString(fileTemp2)) {
                throw new BTSLBaseException(this, "processUploadedFile", "channel.processUploadedFile.error.msg.nofile2name", "loadFileForAlertMsisdn");
            }
            
            // adding code for file upload ends***********------------------------------
            
            // adding code for file processing starts+++++++++++++++++++++++++++++++
            boolean sampletemplate = false;
            boolean listingtemplate = false;
            fileReader = new FileReader(filePathAndFileName);
            if (fileReader != null) {
                bufferReader = new BufferedReader(fileReader);
            } else {
                // we can stop processing here also
                bufferReader = null;
                log.error("processUploadedFile", "Object of BufferReader is not initialized");
                throw new BTSLBaseException(this, "processUploadedFile", "channel.processUploadedFile.error.norecordinfile");
            }
            if (bufferReader != null && bufferReader.ready()) {
                tempStr = bufferReader.readLine().trim();
            }
            //if (tempStr != null && tempStr.contains(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "channel.associateAlertMsisdn.file1.heading"))) {
            //RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file2.heading", null)
            if (tempStr != null && tempStr.contains(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file1.heading", null))) {
                listingtemplate = true;
            }
            //if (tempStr != null && tempStr.contains(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "channel.associateAlertMsisdn.file2.heading"))) {
            if (tempStr != null && tempStr.contains(RestAPIStringParser.getMessage(locale, "channel.associateAlertMsisdn.file2.heading", null))) {
                sampletemplate = true;
            }
            
            if (sampletemplate) {

                fileContents = new ArrayList();
                fileContentsSampleNoError = new ArrayList();
                if (bufferReader != null && bufferReader.ready()) {
                    // Read all data from file line by line if any blank
                    // line found then treat it as NEWLINE
                    // if read line contains only ,,,,,, like data treat it
                    // also as NEWLINE
                    // We will assoume that on the top (excluding blank
                    // lines or ,,,,, type lines) 2 lines are heading
                    // of the file and rest is data.

                    ChannelUserVO sampleTemplateData = null;
                    //int rowCount = 2;
                    int rowCount = 1;
                    RowListProcessor rp= PretupsRestUtil.readCsvFile(filePathAndFileName.getAbsolutePath(), 0);
                   java.util.List<String[]> rows =  rp.getRows();
                    
                   
                   
                   if(rows.size() > DATAROWOFFSET){
                	   
                   
                   int r = 0;
              		while (r < rows.size()) {
              			if(r >= DATAROWOFFSET-1 ){
              			String[] temp=rows.get(r);
          
              			//
              			if(temp[0]==null && temp[1]==null && temp[2]==null && temp[3]==null) {
              				//throw new BTSLBaseException(this, "processUploadedFile", "channel.processUploadedFile.error.msg.nofile2name", "loadFileForAlertMsisdn");
              				throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.FILE_CONTAINS_BLANK_LINE, 0, null);
              			}
              			//

              			tempStr=Arrays.toString(temp);
              			tempStr=tempStr.replace(", null,", ",,");
              			tempStr= tempStr.replace(", null]", "");
              			tempStr= tempStr.trim();
              			
                      

                            if (!BTSLUtil.isNullString(tempStr)) {
                            	
                            	   if(tempStr.contains("EOF"))
                        		   {
                            		   isEndFound = true;
                                       
                                       	break;
                        		   }
                                tempStr = tempStr.trim();
                                int i = 0;
                                int j = 0;
                              arr = tempStr.split(",");
                                if (tempStr.contains("\"")) {
                                    i = tempStr.indexOf("\"");
                                    j = tempStr.lastIndexOf("\"");
                                    arr[2] = tempStr.substring(i, j);
                                    if (arr.length == 5) {
                                        arr[3] = arr[4];
                                        arr[4] = "";
                                    } else {
                                        arr[3] = "";
                                    }
                                }
                                arr[0]=arr[0].replace('[', ' ');
                                arr[0] = arr[0].trim();
                                arr[1]=arr[1].trim();
                                if(arr.length > 2) {
                                	arr[2]=arr[2].trim();
                                }
                                if (arr.length == 0) {
                                    fileContents.add("NEWLINE");
                                    newLines++;
                                } 
                                else if (arr.length == 1) {
                                    records = rowCount;
                                    final String rowNumber = Integer.toString(records);
                                    sampleTemplateData = new ChannelUserVO();
                                    sampleTemplateData.setMsisdn(arr[0]);
                                    sampleTemplateData.setRecordNumber(rowNumber);
                                    fileContents.add(sampleTemplateData);
                                } else {
                                    records = rowCount;
                                    final String rowNumber = Integer.toString(records);
                                    sampleTemplateData = new ChannelUserVO();
                                   
                                    sampleTemplateData.setMsisdn(arr[0]);
                                    sampleTemplateData.setAlertMsisdn(arr[1]);
                                    if (arr.length > 2) {
                                        if (arr[2].contains(",")) {
                                        	arr[2]= arr[2].replace(",", "");
                                        }
                                        sampleTemplateData.setAlertType(arr[2]);
                                        if(BTSLUtil.isNullString(arr[2]))
                                        	sampleTemplateData.setLowBalAlertAllow(PretupsI.NO);
                                        else
                                        	sampleTemplateData.setLowBalAlertAllow(PretupsI.YES);
                                    }
                                    // Added for Email
                                    if (arr.length > 3) {
                                    	arr[3]=arr[3].replace(']', ' ');
                                    	arr[3]= arr[3].trim();
                                        sampleTemplateData.setAlertEmail(arr[3]);
                                        
                                    }
                                    sampleTemplateData.setRecordNumber(rowNumber);
                                    fileContents.add(sampleTemplateData);
                                }
                            } else // add the new lines position in
                                   // arraylist
                            {
                                fileContents.add("NEWLINE");
                                newLines++;
                            }
                        
                   }
              			r++;
              			rowCount=rowCount+1;
              		}
                   }
                   
                   
                    
                    String rowNumber = null;
                    String[] alertTypeArr = null;
                    int alertTypeLen = 0;
                    final int validEmailCount = 0;
                    final int invalidEmailCount = 0;
                    fileErrorList = new ArrayList();
                    UserDAO userDAO = new UserDAO();
                    UserVO userVOforCategory = new UserVO();
					if(log.isDebugEnabled()){
                    	log.debug(methodName,"Size of excel sheet to be process data = "+fileContents.size());
                    }
                    if (!fileContents.isEmpty()) {
                    	int   filesContents=fileContents.size();
                        for (int j = 0; j <filesContents; j++) {
                            sampleTemplateData = (ChannelUserVO) fileContents.get(j);
                            //
                            fileValidationErrorExists=false;
                            //
                            rowNumber = sampleTemplateData.getRecordNumber();
                            if (!BTSLUtil.isNullString(sampleTemplateData.getMsisdn())) {
                                if (!BTSLUtil.isValidMSISDN(sampleTemplateData.getMsisdn())) {
//                                    errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                        "channel.processUploadedFile.error.msisdnIsValid"));
                                	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.msisdnIsValid", null));
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                }
                                    
                                
                                
                                    userVOforCategory = userDAO.loadUserDetailsByMsisdn(con, sampleTemplateData.getMsisdn());
                                    if(userVOforCategory==null) {
                                    	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.msisdnUnmatched", null));
                                        fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                    }
                                    if (!BTSLUtil.isNullObject(userVOforCategory)) {
	                                    if(!userVOforCategory.getCategoryCode().equals(categoryVO.getCategoryCode()))
	                                    {
	//                                        errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
	//                                                "channel.processUploadedFile.error.msisdn.different.category"));
	                                    	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.msisdn.different.category", null));
	                                            fileErrorList.add(errorVO);
	                                            fileValidationErrorExists = true;
	                                    }
                                    }
                                
                                
                            } else {
//                                errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                    "channel.processUploadedFile.error.msisdnIsNull"));
                                errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.msisdnIsNull", null));
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                            }
                            if (!BTSLUtil.isNullString(sampleTemplateData.getAlertType())) {
                                if ((!sampleTemplateData.getAlertType().contains(";") && (sampleTemplateData.getAlertType().length() > 1))) {
//                                    errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                        "channel.processUploadedFile.error.alertTypeNotSeparatedBySemicolon"));
                                    errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertTypeNotSeparatedBySemicolon", null));
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                } else {
                                    alertTypeArr = sampleTemplateData.getAlertType().split(";");
                                    alertTypeLen = alertTypeArr.length;
                                    if(alertTypeLen>3){
//                                    	errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                                "channel.processUploadedFile.error.alertTypeIsInvalid"));
                                    	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertTypeIsInvalid", null));
                                            fileErrorList.add(errorVO);
                                            fileValidationErrorExists = true;
                                            continue;
                                    }
                                    for (int k = 0; k < alertTypeLen; k++) {
                                        alertTypeArr[k] = alertTypeArr[k].toUpperCase().trim();
                                        if (!(alertTypeArr[k].equals(PretupsI.ALERT_TYPE_SELF) || alertTypeArr[k].equals(PretupsI.ALERT_TYPE_OTHER) || alertTypeArr[k]
                                            .equals(PretupsI.ALERT_TYPE_PARENT))) {
//                                            errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                                "channel.processUploadedFile.error.alertTypeIsInvalid"));
                                            errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertTypeIsInvalid", null));
                                            fileErrorList.add(errorVO);
                                            fileValidationErrorExists = true;
                                            continue;
                                        }

                                        if (alertTypeArr[k].equals(PretupsI.ALERT_TYPE_OTHER)) {

                                            if (!BTSLUtil.isNullString(sampleTemplateData.getAlertMsisdn())) {
//                                                if (!BTSLUtil.isValidMSISDN(sampleTemplateData.getAlertMsisdn())) {
////                                                    errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
////                                                        "channel.processUploadedFile.error.alertMsisdnIsValid"));
//                                                    errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertMsisdnIsValid", null));
//                                                    fileErrorList.add(errorVO);
//                                                    fileValidationErrorExists = true;
//                                                }
//                                                //
//                                                String[] alertMsisdnSampleArr = new String[100];
//                                            	alertMsisdnSampleArr = sampleTemplateData.getAlertMsisdn().split(";");
//                                                int alertMsisdnSampleLen = alertMsisdnSampleArr.length;
//                                                for(int l=0;l<alertMsisdnSampleLen;l++) {
//	                                                if (!BTSLUtil.isValidMSISDN(alertMsisdnSampleArr[l])) {
//	//                                                    errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//	//                                                        "channel.processUploadedFile.error.alertMsisdnIsValid"));
//	                                                	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertMsisdnIsValid", null));
//	                                                    fileErrorList.add(errorVO);
//	                                                    fileValidationErrorExists = true;
//	                                                   
//	                                                }
//                                                }
//                                            //
                                                //
                                                String[] alertMsisdnArr = new String[100];
                                            	alertMsisdnArr = sampleTemplateData.getAlertMsisdn().split(";");
                                                int alertMsisdnLen = alertMsisdnArr.length;
                                                Set<String> alertMsisdnSet = new LinkedHashSet<String>();
                                                for(int l=0;l<alertMsisdnLen;l++) {
                                                	alertMsisdnSet.add(alertMsisdnArr[l]);
	                                                if (!BTSLUtil.isValidMSISDN(alertMsisdnArr[l])) {
	//                                                    errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
	//                                                        "channel.processUploadedFile.error.alertMsisdnIsValid"));
	                                                	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertMsisdnIsValid", null));
	                                                    fileErrorList.add(errorVO);
	                                                    fileValidationErrorExists = true;
	                                                    
	                                                }
                                                }
                                                StringBuffer sb = new StringBuffer();
                                                for(String msisdn : alertMsisdnSet) {
                                               	 sb.append(msisdn);
                                               	 sb.append(";");
                                                }
                                                sb.deleteCharAt(sb.length()-1);  
                                                String str = sb.toString();
                                                //
                                                sampleTemplateData.setAlertMsisdn(str);
                                                //
                                            } else {
//                                                errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                                    "channel.processUploadedFile.error.alertMSISDNIsNull"));
                                                errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertMSISDNIsNull", null));
                                                fileErrorList.add(errorVO);
                                                fileValidationErrorExists = true;
                                            }
                                        }
                                    }
                                }

                            } else {
//                                errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                    "channel.processUploadedFile.error.alertTypeIsNull"));
                                errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertTypeIsNull", null));
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                            }
                  
                            if (!BTSLUtil.isNullString(sampleTemplateData.getAlertEmail())) {
                            	
                                // Added for Email
                                if (!((BTSLUtil.isNullString(sampleTemplateData.getAlertEmail())) && (sampleTemplateData.getAlertEmail().contains("@")))) {

//                                    if (!BTSLUtil.validateEmailID(sampleTemplateData.getAlertEmail())) {
////                                        errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
////                                            "channel.processUploadedFile.error.alertEmailIsInvalid"));
//                                        errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertEmailIsInvalid", null));
//                                        fileErrorList.add(errorVO);
//                                        fileValidationErrorExists = true;
//                                    }
                                    //
                                    if(!(sampleTemplateData.getAlertType().contains("O"))) {
	                                    if (!BTSLUtil.validateEmailID(sampleTemplateData.getAlertEmail())) {
	//                                        errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
	//                                            "channel.processUploadedFile.error.alertEmailIsInvalid"));
	                                    	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertEmailIsInvalid", null));
	                                        fileErrorList.add(errorVO);
	                                        fileValidationErrorExists = true;
	                                        
	                                    }
                                	}else {
                                		String[] alertEmailArr = new String[100];
                                		//ArrayList<String> alertEmailArr = new ArrayList<String>();
                                		 alertEmailArr = sampleTemplateData.getAlertEmail().split(";");
                                         int alertEmailLen = alertEmailArr.length;
                                         Set<String> alertEmailSet = new LinkedHashSet<String>();
                                         
                                         for(int k=0;k<alertEmailLen;k++) {
                                        	 alertEmailSet.add(alertEmailArr[k]);
                                        	 if (!BTSLUtil.validateEmailID(alertEmailArr[k])) {
                                        	// 		errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
                                        	//                                            "channel.processUploadedFile.error.alertEmailIsInvalid"));
                                        			errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertEmailIsInvalid", null));
                                        			fileErrorList.add(errorVO);
                                        			fileValidationErrorExists = true;
                                        			
                                        	 }
                                         }
                                         //
                                         StringBuffer sb = new StringBuffer();
                                         for(String email : alertEmailSet) {
                                        	 sb.append(email);
                                        	 sb.append(";");
                                         }
                                         sb.deleteCharAt(sb.length()-1);  
                                         String str = sb.toString();
                                         //
                                         sampleTemplateData.setAlertEmail(str);
                                	}
                                    //
                                    if (sampleTemplateData.getAlertEmail().charAt(0) == '"') {
//                                        errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                            "channel.processUploadedFile.error.alertEmailIsNumber"));
                                        errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertEmailIsNumber", null));
                                        fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                    }

                                }
                            }
							 if (!BTSLUtil.isNullString(sampleTemplateData.getAlertEmail())) {
                          
//                                if ((sampleTemplateData.getAlertEmail().contains(";")) || (sampleTemplateData.getAlertEmail().contains(",")) || (sampleTemplateData
//                                    .getAlertEmail().contains(":")) || (sampleTemplateData.getAlertEmail().contains(" "))) {
////                                    errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
////                                        "channel.processUploadedFile.error.alertEmailIsNumber"));
//                                    errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertEmailIsNumber", null));
//                                    fileErrorList.add(errorVO);
//                                    fileValidationErrorExists = true;
//                                }
								 
								 if(!(sampleTemplateData.getAlertType().contains("O"))) {
		                                if ((sampleTemplateData.getAlertEmail().contains(";")) || (sampleTemplateData.getAlertEmail().contains(",")) || (sampleTemplateData
		                                    .getAlertEmail().contains(":")) || (sampleTemplateData.getAlertEmail().contains(" "))) {
		//                            	if ((processChannelData.getAlertEmail().contains(",")) || (processChannelData
		//                                        .getAlertEmail().contains(":")) || (processChannelData.getAlertEmail().contains(" "))) {
		//                                    errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
		//                                        "channel.processUploadedFile.error.alertEmailIsNumber"));
		                                	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertEmailIsNumber", null));
		                                    fileErrorList.add(errorVO);
		                                    fileValidationErrorExists = true;
		                    
		                                }
	                            	}else {
	                            		
	    	                            	if ((sampleTemplateData.getAlertEmail().contains(",")) || (sampleTemplateData
	    	                                        .getAlertEmail().contains(":")) || (sampleTemplateData.getAlertEmail().contains(" "))) {
	    	//                                    errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
	    	//                                        "channel.processUploadedFile.error.alertEmailIsNumber"));
	    	                                	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertEmailIsNumber", null));
	    	                                    fileErrorList.add(errorVO);
	    	                                    fileValidationErrorExists = true;
	    	                                }
	                            	}
                            }

							//validation starts for alert msisdn
	                            if (!((BTSLUtil.isNullString(sampleTemplateData.getAlertMsisdn())))) {
	                            	if (!BTSLUtil.isNullString(sampleTemplateData.getAlertType())) {
		                            	if(!(sampleTemplateData.getAlertType().contains("O"))) {
		                                    if (!BTSLUtil.isValidMSISDN(sampleTemplateData.getAlertMsisdn())) {
	//	                                        errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
	//	                                            "channel.processUploadedFile.error.alertEmailIsInvalid"));
		                                    	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertMsisdnIsValid", null));
		                                        fileErrorList.add(errorVO);
		                                        fileValidationErrorExists = true;
		                                        
		                                    }
		                            	}
	                            	}else {
	                            		errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertTypeIsNull", null));
	                                    fileErrorList.add(errorVO);
	                                    fileValidationErrorExists = true;
	                            	}
	                            }
                            // ********************Alert Type validation
                            // ends here*****************************
                            totalRecords = j + 1;
                            
                            //
                            if(!fileValidationErrorExists) {
                            	fileContentsSampleNoError.add(sampleTemplateData);
                            }
                            //
                        }
                    } else {
                        throw new BTSLBaseException(this, "processUploadedFile", "channel.processUploadedFile.error.norecordinfile", forwardBack);
                    }

                }
                HashMap<String, String> hm1 = new HashMap<>();
                for(int i=0;i<fileErrorList.size();i++) {
                	ListValueVO temp = (ListValueVO) fileErrorList.get(i);
                	hm1.put(temp.getLabel(),temp.getValue());
                }
                if (fileValidationErrorExists) {
                    response.setErrorList(fileErrorList);
                    response.setErrorFlag("true");
                    response.setTotalRecords(totalRecords); // total records
                    response.setNoOfRecords(String.valueOf(fileErrorList.size()));
                    //response.setValidRecords(totalRecords-fileErrorList.size());
                    //response.setValidRecords(fileErrorList.size());
                    response.setValidRecords(totalRecords-hm1.size());
                    
                 // adding code for errormap  and downloading errormap file starts--------------------------
                    Collections.sort(fileErrorList);
                    response.setErrorList(fileErrorList);
                    response.setErrorMap(errorMap);
                    int errorListSize = fileErrorList.size();
                    //response.setNoOfRecords(String.valueOf(rows - rowOffset - errorListSize + reptRowNo));
                    for (int i = 0, j = errorListSize; i < j; i++) {
                    	ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);
//      		            if(!BTSLUtil.isNullString(errorvo.getOtherInfo2()))
//                        {
//      		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
//      		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
//      		            	MasterErrorList masterErrorList = new MasterErrorList();
//      						String msg = errorvo.getOtherInfo2();
//      						masterErrorList.setErrorMsg(msg);
//      						masterErrorLists.add(masterErrorList);
//      						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
//      						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getOtherInfo())));
//      						rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
//      						if(errorMap.getRowErrorMsgLists() == null)
//      							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
//      						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
//      					    
//                        }
                    	//modifiying error log as per our ListValueVO starts
                    	if(!BTSLUtil.isNullString(errorvo.getValue()))
                        {
      		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
      		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
      		            	MasterErrorList masterErrorList = new MasterErrorList();
      						String msg = errorvo.getValue();
      						masterErrorList.setErrorMsg(msg);
      						masterErrorLists.add(masterErrorList);
      						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
      						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getLabel())));
      						rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
      						if(errorMap.getRowErrorMsgLists() == null)
      							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
      						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
      					    
                        }
                    	//modifiying error log as per our ListValueVO ends
                    }
                    
                    Integer invalidRecordCount = fileErrorList.size();
            		ErrorFileResponse errorResponse = new ErrorFileResponse();
            		if(invalidRecordCount>0) {
            			downloadErrorLogFile(fileErrorList, userVO, response, responseSwag);
            		}
            		
            		if(invalidRecordCount>0) {
            			if (totalRecords-hm1.size()>0) { //partial failure
            				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
                			response.setMessage(msg);
            				response.setStatus("400");
            				responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
            				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
            			}
            			//else if(invalidRecordCount == rows - rowOffset) { //total failure
            			else if(totalRecords-hm1.size()==0) { //total failure
            				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
                			response.setMessage(msg);
            				response.setStatus("400");
            				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
            				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
            			}
            		}
                    //adding code for errormap and downloading errormap ends---------------------
                    
                }
                if ((fileErrorList != null && !fileErrorList.isEmpty())) {
                    response.setTotalRecords(totalRecords);
                    response.setNoOfRecords(String.valueOf(fileErrorList.size()));
                    //forward = mapping.findForward(forwarderrorJSP);
                }
                ArrayList dbErrorList = new ArrayList();
               // if (fileContents != null && !fileContents.isEmpty() && (fileErrorList == null || fileErrorList.isEmpty()) ){
                if (fileContentsSampleNoError != null && !fileContentsSampleNoError.isEmpty()){
//                    dbErrorList = channelUserWebDAO.updateAlertMsisdnTemplate(con, fileContents, (MessageResources) request.getAttribute(Globals.MESSAGES_KEY), BTSLUtil
//                        .getBTSLLocale(request));
                	dbErrorList = channelUserWebDAO.updateAlertMsisdnTemplateNew(con, fileContentsSampleNoError, locale);
                }
                HashMap<String, String> hm2 = new HashMap<>();
                HashMap<String, String> hm3 = new HashMap<>();
                if ((fileErrorList.isEmpty()) && (dbErrorList != null && !dbErrorList.isEmpty())) {
                    fileErrorList.addAll(dbErrorList);
                    for(int i=0;i<fileErrorList.size();i++) {
                    	ListValueVO temp = (ListValueVO) fileErrorList.get(i);
                    	hm2.put(temp.getLabel(),temp.getValue());
                    }
                    Collections.sort(fileErrorList);
                    response.setErrorList(fileErrorList);
                    response.setTotalRecords(totalRecords);
                    response.setNoOfRecords(String.valueOf(fileErrorList.size()));
                    //response.setValidRecords(totalRecords-fileErrorList.size());
                    response.setValidRecords(totalRecords-hm2.size());
                    
                    // adding code for errormap  and downloading errormap file starts--------------------------
                    Collections.sort(fileErrorList);
                    response.setErrorList(fileErrorList);
                    response.setErrorMap(errorMap);
                    int errorListSize = fileErrorList.size();
                    //response.setNoOfRecords(String.valueOf(rows - rowOffset - errorListSize + reptRowNo));
                    for (int i = 0, j = errorListSize; i < j; i++) {
                    	ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);

                    	//modifiying error log as per our ListValueVO starts
                    	if(!BTSLUtil.isNullString(errorvo.getValue()))
                        {
      		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
      		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
      		            	MasterErrorList masterErrorList = new MasterErrorList();
      						String msg = errorvo.getValue();
      						masterErrorList.setErrorMsg(msg);
      						masterErrorLists.add(masterErrorList);
      						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
      						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getLabel())));
      						rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
      						if(errorMap.getRowErrorMsgLists() == null)
      							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
      						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
      					    
                        }
                    	//modifiying error log as per our ListValueVO ends
                    }
                    
                    Integer invalidRecordCount = fileErrorList.size();
            		ErrorFileResponse errorResponse = new ErrorFileResponse();
            		if(invalidRecordCount>0) {
            			downloadErrorLogFile(fileErrorList, userVO, response, responseSwag);
            		}
            		
            		if(invalidRecordCount>0) {
            			if (totalRecords-hm2.size()>0) { //partial failure
            				//
            				mcomCon.finalCommit();
            				//
            				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
                			response.setMessage(msg);
            				response.setStatus("400");
            				responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
            				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
            			}
            			//else if(invalidRecordCount == rows - rowOffset) { //total failure
            			else if(totalRecords-hm2.size()==0) { //total failure
            				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
                			response.setMessage(msg);
            				response.setStatus("400");
            				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
            				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
            			}
            		}
                    //adding code for errormap and downloading errormap ends---------------------
                    
                    
                    //forward = mapping.findForward(forwarderrorJSP);
                }
                // adding code if both dberrorlist and fileerrorlist occurs
                else if ((fileErrorList != null || (!fileErrorList.isEmpty())) && (dbErrorList != null && !dbErrorList.isEmpty())) {
                    fileErrorList.addAll(dbErrorList);
                    for(int i=0;i<fileErrorList.size();i++) {
                    	ListValueVO temp = (ListValueVO) fileErrorList.get(i);
                    	hm3.put(temp.getLabel(),temp.getValue());
                    }
                    Collections.sort(fileErrorList);
                    response.setErrorList(fileErrorList);
                    response.setTotalRecords(totalRecords);
                    response.setNoOfRecords(String.valueOf(fileErrorList.size()));
                    response.setValidRecords(totalRecords-hm3.size());
                    
                 // adding code for errormap  and downloading errormap file starts--------------------------
                    Collections.sort(fileErrorList);
                    response.setErrorList(fileErrorList);
                    response.setErrorMap(errorMap);
                    int errorListSize = fileErrorList.size();
                    //response.setNoOfRecords(String.valueOf(rows - rowOffset - errorListSize + reptRowNo));
                    for (int i = 0, j = errorListSize; i < j; i++) {
                    	ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);
                    	//modifiying error log as per our ListValueVO starts
                    	if(!BTSLUtil.isNullString(errorvo.getValue()))
                        {
      		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
      		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
      		            	MasterErrorList masterErrorList = new MasterErrorList();
      						String msg = errorvo.getValue();
      						masterErrorList.setErrorMsg(msg);
      						masterErrorLists.add(masterErrorList);
      						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
      						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getLabel())));
      						rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
      						if(errorMap.getRowErrorMsgLists() == null)
      							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
      						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
      					    
                        }
                    	//modifiying error log as per our ListValueVO ends
                    }
                    
                    Integer invalidRecordCount = fileErrorList.size();
            		ErrorFileResponse errorResponse = new ErrorFileResponse();
            		if(invalidRecordCount>0) {
            			downloadErrorLogFile(fileErrorList, userVO, response, responseSwag);
            		}
            		
            		if(invalidRecordCount>0) {
            			if (totalRecords-hm3.size()>0) { //partial failure
            				//
            				mcomCon.finalCommit();
            				//
            				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
                			response.setMessage(msg);
            				response.setStatus("400");
            				responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
            				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
            			}
            			//else if(invalidRecordCount == rows - rowOffset) { //total failure
            			else if(totalRecords-hm3.size()==0) { //total failure
            				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
                			response.setMessage(msg);
            				response.setStatus("400");
            				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
            				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
            			}
            		}
                    //adding code for errormap and downloading errormap ends---------------------
                    
//                    forward = mapping.findForward(forwarderrorJSP);
                }
                //
                else if (((!fileValidationErrorExists) && dbErrorList.isEmpty())) {
                    if (!(dbErrorList != null && !dbErrorList.isEmpty())) {
                   
                    	mcomCon.finalCommit();
                        final BTSLMessages btslMessage = new BTSLMessages("channel.processUploadedFile.modify.msg.success", new String[] { String.valueOf(totalRecords) },
                            "goToStart");
//                        forward = super.handleMessage(btslMessage, request, mapping);
//                        forward = mapping.findForward("goToStart");
                        String resmsg = RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.modify.msg.success", new String[] { String.valueOf(totalRecords) });
                        response.setStatus("200");
                        response.setMessage(resmsg);
                        response.setTotalRecords(totalRecords);
                        response.setNoOfRecords(String.valueOf(fileErrorList.size()));
                        response.setValidRecords(totalRecords-hm1.size());
                        response.setMessageCode("bulkuser.processuploadedfile.msg.succes");
                        responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
                    } else {
                     
                    	mcomCon.finalRollback();
                    }
                }
                else if ((dbErrorList.isEmpty())&& (fileValidationErrorExists)) {
                    if (dbErrorList == null ||  dbErrorList.isEmpty()) {
                    
                    	mcomCon.finalCommit();
                        final BTSLMessages btslMessage = new BTSLMessages("channel.processUploadedFile.modify.msg.success", new String[] { String.valueOf(totalRecords-hm1.size()) },
                            "goToStart");
                        
                        String resmsg = RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.modify.msg.success", new String[] { String.valueOf(totalRecords-hm1.size()) });
                        //response.setStatus("200");
                        response.setStatus("400");
                        response.setMessage(resmsg);
                        response.setMessageCode("bulkuser.processuploadedfile.msg.succes");
                        responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
                        response.setTotalRecords(totalRecords);
                        response.setNoOfRecords(String.valueOf(fileErrorList.size()));
                        response.setValidRecords(totalRecords-hm1.size());
//                        forward = super.handleMessage(btslMessage, request, mapping);
//                        forward = mapping.findForward("goToStart");
                    } else {
                    
                    	mcomCon.finalRollback();
                    }
                }
                if (!isEndFound) {
                    if (log.isDebugEnabled()) {
                        log.debug("processFile", "No [EOF] tag found");
                    }
                    throw new BTSLBaseException(this, "processUploadedFile", "channel.processUploadedFile.error.norecordinfile");
                }
            }
            else if (listingtemplate) {
                fileContents = new ArrayList();
                fileContentsUserListingNoError = new ArrayList();
               
                
                if (bufferReader != null && bufferReader.ready()) {
                    // Read all data from file line by line if any blank
                    // line found then treat it as NEWLINE
                    // if read line contains only ,,,,,, like data treat it
                    // also as NEWLINE
                    // We will assoume that on the top (excluding blank
                    // lines or ,,,,, type lines) 2 lines are heading
                    // of the file and rest is data.
                    ChannelUserVO ChannelUserData = null;
                    ChannelUserVO processChannelData = null;
                    int rowCount = 1;
                    String rowNumber = null;
                    

                    while ((tempStr = bufferReader.readLine()) != null) // read
                    // the
                    // file
                    // until
                    // it
                    // reaches
                    // to
                    // end
                    {
                        if (rowCount > DATAROWOFFSETUSERLIST) {

                            if (!BTSLUtil.isNullString(tempStr)) {
                                tempStr = tempStr.trim();
                                int i = 0;
                                int j = 0;
                                arr = tempStr.split(",");
                                if (tempStr.contains("\"")) {
                                    i = tempStr.indexOf("\"");
                                    j = tempStr.lastIndexOf("\"");
                                    arr[3] = tempStr.substring(i, j);
                                    if (arr.length == 6) {
                                        arr[4] = arr[5];
                                        arr[5] = "";
                                    } else {
                                        arr[4] = "";
                                    }
                                }

                                if (arr.length == 0) {
                                    fileContents.add("NEWLINE");
                                    newLines++;
                                    //
                                    throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.FILE_CONTAINS_BLANK_LINE, 0, null);
                                    //
                                } else if (arr.length == 1 && "EOF".equalsIgnoreCase(arr[0])) {
                                    isEndFound = true;
                                    break;
                                } else if (arr.length == 2) {
                                    records = rowCount;
                                    rowNumber = Integer.toString(records);
                                    processChannelData = new ChannelUserVO();

                                    processChannelData.setMsisdn(arr[0]);
                                    processChannelData.setCategoryName(arr[1]);
                                    processChannelData.setRecordNumber(rowNumber);
                                    fileContents.add(processChannelData);
                                } else {
                                    records = rowCount;
                                    rowNumber = Integer.toString(records);
                                    processChannelData = new ChannelUserVO();
                                    processChannelData.setRecordNumber(rowNumber);

                                    processChannelData.setMsisdn(arr[0]);
                                    processChannelData.setCategoryName(arr[1]);
                                    processChannelData.setAlertMsisdn(arr[2]);
                                    if (arr.length > 3) {
                                        if (arr[3].contains(",")) {
                                            arr[3].replace(",", "");
                                        }
                                        processChannelData.setAlertType(arr[3]);
                                    }
                                    // Added for Email
                                    if (arr.length > 4) {
                                        processChannelData.setAlertEmail(arr[4]);
                                    }

                                    fileContents.add(processChannelData);
                                }
                            } else // add the new lines position in
                                   // arraylist
                            {
                                fileContents.add("NEWLINE");
                                newLines++;
                                //
                                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.FILE_CONTAINS_BLANK_LINE, 0, null);
                                //
                            }
                        }
                        rowCount++;
                    }
                    fileErrorList = new ArrayList();
                    
                    String[] alertTypeArr = new String[5];
                    int alertTypeLen = 0;
                    if (!fileContents.isEmpty()) {
                    	
                        for (int i = 0, j = 0; j < fileContents.size(); i++, j++) {
                        	
                            processChannelData = (ChannelUserVO) fileContents.get(j);
                            rowNumber = processChannelData.getRecordNumber();
                            fileValidationErrorExists=false;
                            if (!BTSLUtil.isNullString(processChannelData.getMsisdn())) {
                                boolean msisdnFound = false;
                                for (int k = 0; k < dataList.size(); k++) {
                                    ChannelUserData = (ChannelUserVO) dataList.get(k);
                                    if (processChannelData.getMsisdn().equals(ChannelUserData.getUserPhoneVO().getMsisdn())) {
                                        msisdnFound = true;
                                        break;
                                    }

                                }
                                if (!msisdnFound) {
//                                    errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                        "channel.processUploadedFile.error.msisdnUnmatched"));
                                	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.msisdnUnmatched", null));
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    

                                } else {
                                    processChannelData.setUserID(ChannelUserData.getUserID());
                                }

                               

                            } else {
//                                errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                    "channel.processUploadedFile.error.msisdnIsNull"));
                            	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.msisdnIsNull", null));
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                    
                            }
                            if (!BTSLUtil.isNullString(processChannelData.getCategoryName())) {
                                if (!processChannelData.getCategoryName().equals(ChannelUserData.getCategoryName())) {
//                                    errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                        "channel.processUploadedFile.error.categoryNameUnmatched"));
                                	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.categoryNameUnmatched", null));
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                   
                                }
                            } else {
//                                errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                    "channel.processUploadedFile.error.categoryNameIsNull"));
                            	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.categoryNameIsNull", null));
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                               
                            }
                            // *************Added by Harpreet Alert Type
                            // validation starts
                            // here*****************************
                            if (!BTSLUtil.isNullString(processChannelData.getAlertType())) {
                                if ((!processChannelData.getAlertType().contains(";") && (processChannelData.getAlertType().length() > 1))) {
//                                    errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                        "channel.processUploadedFile.error.alertTypeNotSeparatedBySemicolon"));
                                	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertTypeNotSeparatedBySemicolon", null));
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                                    
                                } else {
                                    alertTypeArr = processChannelData.getAlertType().split(";");
                                    alertTypeLen = alertTypeArr.length;
                                    if(alertTypeLen>3){
//                                    	errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                                "channel.processUploadedFile.error.alertTypeIsInvalid"));
                                    	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertTypeIsInvalid", null));
                                            fileErrorList.add(errorVO);
                                            fileValidationErrorExists = true;
                                           
                                            continue;
                                    }
                                    for (int k = 0; k < alertTypeLen; k++) {
                                        alertTypeArr[k] = alertTypeArr[k].toUpperCase().trim();
                                        if (!(alertTypeArr[k].equals(PretupsI.ALERT_TYPE_SELF) || alertTypeArr[k].equals(PretupsI.ALERT_TYPE_OTHER) || alertTypeArr[k]
                                            .equals(PretupsI.ALERT_TYPE_PARENT))) {
//                                            errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                                "channel.processUploadedFile.error.alertTypeIsInvalid"));
                                        	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertTypeIsInvalid", null));
                                            fileErrorList.add(errorVO);
                                            fileValidationErrorExists = true;
                                           
                                            continue;
                                        }
                                        
                                     
                                        if (alertTypeArr[k].equals(PretupsI.ALERT_TYPE_OTHER)) {

                                            if (!BTSLUtil.isNullString(processChannelData.getAlertMsisdn())) {
                                            	String[] alertMsisdnArr = new String[100];
                                            	alertMsisdnArr = processChannelData.getAlertMsisdn().split(";");
                                                int alertMsisdnLen = alertMsisdnArr.length;
                                                Set<String> alertMsisdnSet = new LinkedHashSet<String>();
                                                for(int l=0;l<alertMsisdnLen;l++) {
                                                	alertMsisdnSet.add(alertMsisdnArr[l]);
	                                                if (!BTSLUtil.isValidMSISDN(alertMsisdnArr[l])) {
	//                                                    errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
	//                                                        "channel.processUploadedFile.error.alertMsisdnIsValid"));
	                                                	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertMsisdnIsValid", null));
	                                                    fileErrorList.add(errorVO);
	                                                    fileValidationErrorExists = true;
	                                                    
	                                                }
                                                }
                                                StringBuffer sb = new StringBuffer();
                                                for(String msisdn : alertMsisdnSet) {
                                               	 sb.append(msisdn);
                                               	 sb.append(";");
                                                }
                                                sb.deleteCharAt(sb.length()-1);  
                                                String str = sb.toString();
                                                //
                                                processChannelData.setAlertMsisdn(str);
                                            } else {
//                                                errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                                    "channel.processUploadedFile.error.alertMSISDNIsNull"));
                                            	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertMSISDNIsNull", null));
                                                fileErrorList.add(errorVO);
                                                fileValidationErrorExists = true;
                                                
                                            }
                                        }
                                    }
                                }

                            } else {
//                                errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                    "channel.processUploadedFile.error.alertTypeIsNull"));
                            	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertTypeIsNull", null));
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                
                            }
                      
                            if (!BTSLUtil.isNullString(processChannelData.getAlertEmail())) {
                              
                                // Added for Email Alert
                                if (!((BTSLUtil.isNullString(processChannelData.getAlertEmail())) && (processChannelData.getAlertEmail().contains("@")))) {
                                		
                                	if(!(processChannelData.getAlertType().contains("O"))) {
	                                    if (!BTSLUtil.validateEmailID(processChannelData.getAlertEmail())) {
	//                                        errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
	//                                            "channel.processUploadedFile.error.alertEmailIsInvalid"));
	                                    	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertEmailIsInvalid", null));
	                                        fileErrorList.add(errorVO);
	                                        fileValidationErrorExists = true;
	                                        
	                                    }
                                	}else {
                                		String[] alertEmailArr = new String[100];
                                		//ArrayList<String> alertEmailArr = new ArrayList<String>();
                                		 alertEmailArr = processChannelData.getAlertEmail().split(";");
                                         int alertEmailLen = alertEmailArr.length;
                                         Set<String> alertEmailSet = new LinkedHashSet<String>();
                                         
                                         for(int k=0;k<alertEmailLen;k++) {
                                        	 alertEmailSet.add(alertEmailArr[k]);
                                        	 if (!BTSLUtil.validateEmailID(alertEmailArr[k])) {
                                        	// 		errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
                                        	//                                            "channel.processUploadedFile.error.alertEmailIsInvalid"));
                                        			errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertEmailIsInvalid", null));
                                        			fileErrorList.add(errorVO);
                                        			fileValidationErrorExists = true;
                                        			
                                        	 }
                                         }
                                         //
                                         StringBuffer sb = new StringBuffer();
                                         for(String email : alertEmailSet) {
                                        	 sb.append(email);
                                        	 sb.append(";");
                                         }
                                         sb.deleteCharAt(sb.length()-1);  
                                         String str = sb.toString();
                                         //
                                         processChannelData.setAlertEmail(str);
                                	}
                                    if (processChannelData.getAlertEmail().charAt(0) == '"') {
//                                        errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
//                                            "channel.processUploadedFile.error.alertEmailIsNumber"));
                                    	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertEmailIsNumber", null));
                                        fileErrorList.add(errorVO);
                                        fileValidationErrorExists = true;
                                        
                                    }
                                }

                            if (!BTSLUtil.isNullString(processChannelData.getAlertEmail())) {
                            	
                            	if(!(processChannelData.getAlertType().contains("O"))) {
	                                if ((processChannelData.getAlertEmail().contains(";")) || (processChannelData.getAlertEmail().contains(",")) || (processChannelData
	                                    .getAlertEmail().contains(":")) || (processChannelData.getAlertEmail().contains(" "))) {
	//                            	if ((processChannelData.getAlertEmail().contains(",")) || (processChannelData
	//                                        .getAlertEmail().contains(":")) || (processChannelData.getAlertEmail().contains(" "))) {
	//                                    errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
	//                                        "channel.processUploadedFile.error.alertEmailIsNumber"));
	                                	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertEmailIsNumber", null));
	                                    fileErrorList.add(errorVO);
	                                    fileValidationErrorExists = true;
	                                    
	                                }
                            	}else {
                            		
    	                            	if ((processChannelData.getAlertEmail().contains(",")) || (processChannelData
    	                                        .getAlertEmail().contains(":")) || (processChannelData.getAlertEmail().contains(" "))) {
    	//                                    errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
    	//                                        "channel.processUploadedFile.error.alertEmailIsNumber"));
    	                                	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertEmailIsNumber", null));
    	                                    fileErrorList.add(errorVO);
    	                                    fileValidationErrorExists = true;
    	                                   
    	                                }
                            	}
                            }
                        }
                            
                            //validation starts for alert msisdn
                            if (!((BTSLUtil.isNullString(processChannelData.getAlertMsisdn())))) {
                        		
                            	if (!BTSLUtil.isNullString(processChannelData.getAlertType())) {
	                            	if(!(processChannelData.getAlertType().contains("O"))) {
	                                    if (!BTSLUtil.isValidMSISDN(processChannelData.getAlertMsisdn())) {
	//                                        errorVO = new ListValueVO(rowNumber, this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),
	//                                            "channel.processUploadedFile.error.alertEmailIsInvalid"));
	                                    	errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertMsisdnIsValid", null));
	                                        fileErrorList.add(errorVO);
	                                        fileValidationErrorExists = true;
	                                        
	                                    }
	                            	}
                            	}
                            	else {
                            		errorVO = new ListValueVO(rowNumber, RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.error.alertTypeIsNull", null));
                                    fileErrorList.add(errorVO);
                                    fileValidationErrorExists = true;
                            	}

                                
                            }
                            
                            //validation ends for alert msisdn
                            
                            
                            // *******************Added by Harpreet Alert
                            // Type validation ends
                            // here*****************************
                            totalRecords = j + 1;
                            
                            //
                            if(!fileValidationErrorExists) {
                            	fileContentsUserListingNoError.add(processChannelData);
                            }
                            //
                            
                        }
                        
                        
                    } else {
                        throw new BTSLBaseException(this, "processUploadedFile", "channel.processUploadedFile.error.norecordinfile", forwardBack);
                    }

                }
                HashMap<String, String> hm1 = new HashMap<>();
                for(int i=0;i<fileErrorList.size();i++) {
                	ListValueVO temp = (ListValueVO) fileErrorList.get(i);
                	hm1.put(temp.getLabel(),temp.getValue());
                }
                
                if (fileValidationErrorExists) {
                    response.setErrorList(fileErrorList);
                    response.setErrorFlag("true");
                    response.setTotalRecords(totalRecords); // total records
                    response.setNoOfRecords(String.valueOf(fileErrorList.size()));
                    //response.setValidRecords(totalRecords-fileErrorList.size());
                    response.setValidRecords(totalRecords-hm1.size());
                    //response.setValidRecords(fileErrorList.size());
                    
                 // adding code for errormap  and downloading errormap file starts--------------------------
                    Collections.sort(fileErrorList);
                    response.setErrorList(fileErrorList);
                    response.setErrorMap(errorMap);
                    int errorListSize = fileErrorList.size();
                    //response.setNoOfRecords(String.valueOf(rows - rowOffset - errorListSize + reptRowNo));
                    for (int i = 0, j = errorListSize; i < j; i++) {
                    	ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);

                    	
                    	//modifiying error log as per our ListValueVO starts
                    	if(!BTSLUtil.isNullString(errorvo.getValue()))
                        {
      		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
      		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
      		            	MasterErrorList masterErrorList = new MasterErrorList();
      						String msg = errorvo.getValue();
      						masterErrorList.setErrorMsg(msg);
      						masterErrorLists.add(masterErrorList);
      						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
      						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getLabel())));
      						rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
      						if(errorMap.getRowErrorMsgLists() == null)
      							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
      						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
      					    
                        }
                    	//modifiying error log as per our ListValueVO ends
                    }
                    
                    Integer invalidRecordCount = fileErrorList.size();
            		ErrorFileResponse errorResponse = new ErrorFileResponse();
            		if(invalidRecordCount>0) {
            			downloadErrorLogFile(fileErrorList, userVO, response, responseSwag);
            		}
            		
            		if(invalidRecordCount>0) {
            			if (totalRecords-hm1.size()>0) { //partial failure
            				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
                			response.setMessage(msg);
            				response.setStatus("400");
            				responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
            				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
            			}
            			//else if(invalidRecordCount == rows - rowOffset) { //total failure
            			else if(totalRecords-hm1.size()==0) { //total failure
            				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
                			response.setMessage(msg);
            				response.setStatus("400");
            				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
            				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
            			}
            		}
                    //adding code for errormap and downloading errormap ends---------------------
                    
                }
                if ((fileErrorList != null && !fileErrorList.isEmpty())) {
                    response.setTotalRecords(totalRecords);
                    response.setNoOfRecords(String.valueOf(fileErrorList.size()));
                    //forward = mapping.findForward(forwarderrorJSP);
                }
                ArrayList dbErrorList = new ArrayList();
//                if (fileContents != null && !fileContents.isEmpty() && fileErrorList == null || fileErrorList.isEmpty()) {
////                    dbErrorList = channelUserWebDAO.updateAlertMsisdn(con, fileContents, (MessageResources) request.getAttribute(Globals.MESSAGES_KEY), BTSLUtil
////                        .getBTSLLocale(request));
//                    dbErrorList = channelUserWebDAO.updateAlertMsisdnNew(con, fileContents, locale);
//                }
                if (fileContentsUserListingNoError != null && !fileContentsUserListingNoError.isEmpty()) {
//                  dbErrorList = channelUserWebDAO.updateAlertMsisdn(con, fileContents, (MessageResources) request.getAttribute(Globals.MESSAGES_KEY), BTSLUtil
//                      .getBTSLLocale(request));
                	System.out.println("db query executed");
                	dbErrorList = channelUserWebDAO.updateAlertMsisdnNew(con, fileContentsUserListingNoError, locale);
              }
                HashMap<String, String> hm2 = new HashMap<>();
                HashMap<String, String> hm3 = new HashMap<>();
                if ((fileErrorList == null || fileErrorList.isEmpty()) && (dbErrorList != null && !dbErrorList.isEmpty())) {
                    fileErrorList.addAll(dbErrorList);
                    for(int i=0;i<fileErrorList.size();i++) {
                    	ListValueVO temp = (ListValueVO) fileErrorList.get(i);
                    	hm2.put(temp.getLabel(),temp.getValue());
                    }
                    Collections.sort(fileErrorList);
                    response.setErrorList(fileErrorList);
                    response.setTotalRecords(totalRecords);
                    response.setNoOfRecords(String.valueOf(fileErrorList.size()));
                    response.setValidRecords(totalRecords-hm2.size());
                    
                 // adding code for errormap  and downloading errormap file starts--------------------------
                    Collections.sort(fileErrorList);
                    response.setErrorList(fileErrorList);
                    response.setErrorMap(errorMap);
                    int errorListSize = fileErrorList.size();
                    //response.setNoOfRecords(String.valueOf(rows - rowOffset - errorListSize + reptRowNo));
                    for (int i = 0, j = errorListSize; i < j; i++) {
                    	ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);

                    	//modifiying error log as per our ListValueVO starts
                    	if(!BTSLUtil.isNullString(errorvo.getValue()))
                        {
      		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
      		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
      		            	MasterErrorList masterErrorList = new MasterErrorList();
      						String msg = errorvo.getValue();
      						masterErrorList.setErrorMsg(msg);
      						masterErrorLists.add(masterErrorList);
      						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
      						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getLabel())));
      						rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
      						if(errorMap.getRowErrorMsgLists() == null)
      							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
      						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
      					    
                        }
                    	//modifiying error log as per our ListValueVO ends
                    }
                    
                    Integer invalidRecordCount = fileErrorList.size();
            		ErrorFileResponse errorResponse = new ErrorFileResponse();
            		if(invalidRecordCount>0) {
            			downloadErrorLogFile(fileErrorList, userVO, response, responseSwag);
            		}
            		
            		if(invalidRecordCount>0) {
            			if (totalRecords-hm2.size()>0) { //partial failure
            				//
            				mcomCon.finalCommit();
            				//
            				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
                			response.setMessage(msg);
            				response.setStatus("400");
            				responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
            				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
            			}
            			//else if(invalidRecordCount == rows - rowOffset) { //total failure
            			else if(totalRecords-hm2.size()==0) { //total failure
            				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
                			response.setMessage(msg);
            				response.setStatus("400");
            				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
            				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
            			}
            		}
                    //adding code for errormap and downloading errormap ends---------------------
                    
//                    forward = mapping.findForward(forwarderrorJSP);
                }
                //handling case if both dberrorlist and fileerrorList occurs starts
                else if ((fileErrorList != null || (!fileErrorList.isEmpty())) && (dbErrorList != null && !dbErrorList.isEmpty())) {
                    fileErrorList.addAll(dbErrorList);
                    for(int i=0;i<fileErrorList.size();i++) {
                    	ListValueVO temp = (ListValueVO) fileErrorList.get(i);
                    	hm3.put(temp.getLabel(),temp.getValue());
                    }
                    Collections.sort(fileErrorList);
                    response.setErrorList(fileErrorList);
                    response.setTotalRecords(totalRecords);
                    response.setNoOfRecords(String.valueOf(fileErrorList.size()));
                    response.setValidRecords(totalRecords-hm3.size());
                    
                 // adding code for errormap  and downloading errormap file starts--------------------------
                    Collections.sort(fileErrorList);
                    response.setErrorList(fileErrorList);
                    response.setErrorMap(errorMap);
                    int errorListSize = fileErrorList.size();
                    //response.setNoOfRecords(String.valueOf(rows - rowOffset - errorListSize + reptRowNo));
                    for (int i = 0, j = errorListSize; i < j; i++) {
                    	ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);

                    	//modifiying error log as per our ListValueVO starts
                    	if(!BTSLUtil.isNullString(errorvo.getValue()))
                        {
      		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
      		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
      		            	MasterErrorList masterErrorList = new MasterErrorList();
      						String msg = errorvo.getValue();
      						masterErrorList.setErrorMsg(msg);
      						masterErrorLists.add(masterErrorList);
      						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
      						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getLabel())));
      						rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
      						if(errorMap.getRowErrorMsgLists() == null)
      							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
      						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
      					    
                        }
                    	//modifiying error log as per our ListValueVO ends
                    }
                    
                    Integer invalidRecordCount = fileErrorList.size();
            		ErrorFileResponse errorResponse = new ErrorFileResponse();
            		if(invalidRecordCount>0) {
            			downloadErrorLogFile(fileErrorList, userVO, response, responseSwag);
            		}
            		
            		if(invalidRecordCount>0) {
            			if (totalRecords-hm3.size()>0) { //partial failure
            				//
            				mcomCon.finalCommit();
            				//
            				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
                			response.setMessage(msg);
            				response.setStatus("400");
            				responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
            				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
            			}
            			//else if(invalidRecordCount == rows - rowOffset) { //total failure
            			else if(totalRecords-hm3.size()==0) { //total failure
            				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
                			response.setMessage(msg);
            				response.setStatus("400");
            				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
            				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
            			}
            		}
                    //adding code for errormap and downloading errormap ends---------------------
                    
//                    forward = mapping.findForward(forwarderrorJSP);
                }
                //handling case if both dberrorlist and fileerrorList occurs ends
                else if (((!fileValidationErrorExists) && dbErrorList.isEmpty())) {
                    if (dbErrorList == null ||  dbErrorList.isEmpty()) {
                    
                    	mcomCon.finalCommit();
                        final BTSLMessages btslMessage = new BTSLMessages("channel.processUploadedFile.modify.msg.success", new String[] { String.valueOf(totalRecords) },
                            "goToStart");
                        
                        String resmsg = RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.modify.msg.success", new String[] { String.valueOf(totalRecords) });
                        response.setStatus("200");
                        response.setMessage(resmsg);
                        response.setMessageCode("bulkuser.processuploadedfile.msg.succes");
                        responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
                        response.setTotalRecords(totalRecords);
                        response.setNoOfRecords(String.valueOf(fileErrorList.size()));
                        response.setValidRecords(totalRecords-hm1.size());
//                        forward = super.handleMessage(btslMessage, request, mapping);
//                        forward = mapping.findForward("goToStart");
                    } else {
                    
                    	mcomCon.finalRollback();
                    }
                }
                else if ((dbErrorList.isEmpty())&& (fileValidationErrorExists)) {
                    if (dbErrorList == null ||  dbErrorList.isEmpty()) {
                    
                    	mcomCon.finalCommit();
                        final BTSLMessages btslMessage = new BTSLMessages("channel.processUploadedFile.modify.msg.success", new String[] { String.valueOf(totalRecords-hm1.size()) },
                            "goToStart");
                        
                        String resmsg = RestAPIStringParser.getMessage(locale, "channel.processUploadedFile.modify.msg.success", new String[] { String.valueOf(totalRecords-hm1.size()) });
                        //response.setStatus("200");
                        response.setStatus("400");
                        response.setMessage(resmsg);
                        response.setMessageCode("bulkuser.processuploadedfile.msg.succes");
                        responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
                        response.setTotalRecords(totalRecords);
                        response.setNoOfRecords(String.valueOf(fileErrorList.size()));
                        response.setValidRecords(totalRecords-hm1.size());
//                        forward = super.handleMessage(btslMessage, request, mapping);
//                        forward = mapping.findForward("goToStart");
                    } else {
                    
                    	mcomCon.finalRollback();
                    }
                }
                if (!isEndFound) {
                    if (log.isDebugEnabled()) {
                        log.debug("processFile", "No [EOF] tag found");
                    }
                    throw new BTSLBaseException(this, "processUploadedFile", "channel.processUploadedFile.error.norecordinfile");
                }
            }
            else {
                throw new BTSLBaseException(this, "processUploadedFile", "channel.processUploadedFile.error.incorrectformat", forwardBack);
            }
            
            // adding code for file processing ends  +++++++++++++++++++++++++++++++
            
            
            
        }
        catch (BTSLBaseException be) {
            log.error("loadDownloadFile", "Exception:e=" + be);
            log.errorTrace(methodName, be);
            //return super.handleError(this, "loadDownloadFile", e, request, mapping);
            if (be.getMessage().equalsIgnoreCase("1080001") || be.getMessage().equalsIgnoreCase("1080002")
					|| be.getMessage().equalsIgnoreCase("1080003") || be.getMessage().equalsIgnoreCase("241023")
					|| be.getMessage().equalsIgnoreCase("241018")) {
				response.setStatus("401");
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response.setStatus("400");
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
			response.setMessageCode(be.getMessage());
			response.setMessage(resmsg);
        } catch (Exception e) {
            log.error("loadDownloadFile", "Exception:e=" + e);
            log.errorTrace(methodName, e);
            //return super.handleError(this, "loadDownloadFile", e, request, mapping);
            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus("400");
        } finally {
            	
        	try {
        		if (bufferReader != null)
                	bufferReader.close();
                if (fileReader != null) 
                	fileReader.close();
                
            } catch (Exception e1) {
                log.errorTrace(methodName, e1);
            }
			if (mcomCon != null) {
				mcomCon.close("AlertMsisdnAction#processUploadedFile");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug("loadDownloadFile", "Exiting:forward=");
            }
        }
		
		
		
		return response;
		
	}
	
	
	
	
	
	
	public void validateFileDetailsMap(HashMap<String, String> fileDetailsMap) throws BTSLBaseException {
		if (!BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_NAME))
				&& !BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_ATTACHMENT))) {
			validateFileName(fileDetailsMap.get(PretupsI.FILE_NAME)); // throw exception
		} else {
			log.error("validateFileInput", "FILENAME/FILEATTACHMENT IS NULL");
			throw new BTSLBaseException(this, "validateFileInput", PretupsErrorCodesI.INVALID_FILE_INPUT,
					PretupsI.RESPONSE_FAIL, null);

		}
	}
	
	
	public void validateFileName(String fileName) throws BTSLBaseException {
		final String pattern = Constants.getProperty("FILE_NAME_WHITE_LIST");
		final Pattern r = Pattern.compile(pattern);
		final Matcher m = r.matcher(fileName);
		if (!m.find()) {
			throw new BTSLBaseException(this, "validateFileName", PretupsErrorCodesI.INVALID_FILE_NAME1,
					PretupsI.RESPONSE_FAIL, null);
		}
	}
	
	
	public void downloadErrorLogFile(ArrayList errorList, UserVO userVO, BatchUploadAndProcessAssosiateAlertResponseVO response, HttpServletResponse responseSwag)
	{
	    final String METHOD_NAME = "downloadErrorLogFile";
	    Writer out =null;
	    File newFile = null;
        File newFile1 = null;
        String fileHeader=null;
        Date date= new Date();
		if (log.isDebugEnabled())
			log.debug(METHOD_NAME, "Entered");
		try
		{
			String filePath = Constants.getProperty("DownloadErLogFilePath");
			try
			{
				File fileDir = new File(filePath);
				if(!fileDir.isDirectory())
					fileDir.mkdirs();
			}
			catch(Exception e)
			{			
				log.errorTrace(METHOD_NAME,e);
				log.error(METHOD_NAME,"Exception" + e.getMessage());
				throw new BTSLBaseException(this,METHOD_NAME,"bulkuser.processuploadedfile.downloadfile.error.dirnotcreated");
			}
			
			String _fileName = Constants.getProperty("BatchUserAssociateErLog")+BTSLUtil.getFileNameStringFromDate(new Date())+".csv";
		    String networkCode = userVO.getNetworkID();
		    newFile1=new File(filePath);
            if(! newFile1.isDirectory())
         	 newFile1.mkdirs();
             String absolutefileName=filePath+_fileName;
             fileHeader=Constants.getProperty("ERROR_FILE_HEADER_MOVEUSER");
             
             newFile = new File(absolutefileName);
             out = new OutputStreamWriter(new FileOutputStream(newFile));
             out.write(fileHeader +"\n");
             for (Iterator<ListValueVO> iterator = errorList.iterator(); iterator.hasNext();) {
 				
             	ListValueVO listValueVO =iterator.next();
             		out.write(listValueVO.getLabel()+",");
                 	out.write(listValueVO.getValue()+",");
             	
             	out.write(",");
             	out.write("\n");
             }
 			out.write("End");
 			out.close();
 			File error =new File(absolutefileName);
			byte[] fileContent = FileUtils.readFileToByteArray(error);
	   		String encodedString = Base64.getEncoder().encodeToString(fileContent);	   		
	   		response.setFileAttachment(encodedString);
	   		response.setFileName(_fileName);
	   		response.setFileType("csv");
 			
		}
		catch (Exception e)
		{
			log.error(METHOD_NAME,"Exception:e="+e);
			log.errorTrace(METHOD_NAME,e);
		}
		finally
         {
         	if (log.isDebugEnabled()){
         		log.debug(METHOD_NAME,"Exiting... ");
         	}
             if (out!=null)
             	try{
             		out.close();
             		}
             catch(Exception e){
            	 log.errorTrace(METHOD_NAME, e);
             }
             	
         }
	}
	
	

}
