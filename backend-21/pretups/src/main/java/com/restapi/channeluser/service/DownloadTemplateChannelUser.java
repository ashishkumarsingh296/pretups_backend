package com.restapi.channeluser.service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.channel.user.businesslogic.BatchUserDAO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.xl.BatchUserCreationExcelRWPOI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.restapi.user.service.FileDownloadResponseMulti;
import com.web.pretups.channel.user.businesslogic.BatchUserWebDAO;
import com.web.pretups.channel.user.web.BatchUserForm;

import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${DownloadTemplateChannelUser.name}", description = "${DownloadTemplateChannelUser.desc}")//@Api(tags= "File Operations", value="Channel User Services")
@RestController
@RequestMapping(value = "/v1/channelUserServices")
public class DownloadTemplateChannelUser {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	 StringBuilder loggerValue= new StringBuilder(); 


public static final Log log = LogFactory.getLog(DownloadTemplateChannelUser.class.getName());	
	
@Context
private HttpServletRequest httpServletRequest;
@Autowired
private MessageSource messageSource;

@GetMapping(value= "/downloadUsersBatch", produces = MediaType.APPLICATION_JSON)	
@ResponseBody
/*
@ApiOperation(value = "Download Users List API",
           authorizations = {
               @Authorization(value = "Authorization")})
@ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = FileDownloadResponseMulti.class),
      @ApiResponse(code = 400, message = "Bad Request" ),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 404, message = "Not Found")
      })
*/

@io.swagger.v3.oas.annotations.Operation(summary = "${downloadUsersBatch.summary}", description="${downloadUsersBatch.description}",

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


public FileDownloadResponseMulti downloadUsersList(
@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,

 HttpServletResponse responseSwag
		)throws IOException, SQLException, BTSLBaseException{

	
	final String methodName =  "downloadUsersList";
	if (log.isDebugEnabled()) {
		log.debug(methodName, "Entered ");
	}
	
	
    FileDownloadResponseMulti response=null;
    response = new FileDownloadResponseMulti();
	 String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
     String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
     Connection con = null;
		MComConnectionI mcomCon = null;
     final HashMap masterDataMap = new HashMap();
    try {
    	
        response.setService("batchChannelServices");
        response.setReferenceId(1986);
		
        final BatchUserForm theForm = new BatchUserForm();
		/*
		 * Authentication
		 * @throws BTSLBaseException
		 */
	//	OAuthenticationUtil.validateToken(headers);
		
        OAuthUser oAuthUserData=new OAuthUser();
        oAuthUserData.setData(new OAuthUserData());
        OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,new BaseResponseMultiple());
        mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		ListValueVO listValueVO = null;
		 String msisdn =  oAuthUserData.getData().getMsisdn();
		 LoginDAO _loginDAO = new LoginDAO();
         // load the information of user by passing login id and password
         // of user
		 UserVO userVO = _loginDAO.loadUserDetails(con, oAuthUserData.getData().getLoginid(), oAuthUserData.getData().getPassword(), new Locale(lang, country));

	      //UserVO userVO=new UserDAO().loadUsersDetails(con, msisdn);
		 if (userVO.getUserType().equals(PretupsI.USER_TYPE_CHANNEL)) {
             theForm.setDomainName(userVO.getDomainName());
             theForm.setDomainCode(userVO.getDomainID());
         } else {
             ArrayList domainList = userVO.getDomainList();
             if ((domainList == null || domainList.isEmpty()) &&

             PretupsI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO.getCategoryVO().getFixedDomains())) {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
                 domainList = new DomainDAO().loadCategoryDomainList(con);
             }

             if (domainList != null && domainList.size() == 1) {
                 listValueVO = (ListValueVO) domainList.get(0);
                 theForm.setDomainCode(listValueVO.getValue());
                 theForm.setDomainName(listValueVO.getLabel());
             } else {
                 theForm.setDomainList(domainList);
             }
         }
         // End of changes made for batch user creation by channel users

         // load the geographies
         final ArrayList geoList = new ArrayList();
         UserGeographiesVO geographyVO = null;
         ArrayList geographyList = new GeographicalDomainDAO().loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID());
         userVO.setGeographicalAreaList(geographyList);
         final ArrayList userGeoList = userVO.getGeographicalAreaList();
         // if there is only one geographies associated with user then there
         // will be
         // no drop down will appear on the screen. just display the
         // geography
         if (userGeoList != null) {
             if (userGeoList.size() == 1) {
                 geographyVO = (UserGeographiesVO) userGeoList.get(0);
                 theForm.setGeographyCode(geographyVO.getGraphDomainCode());
                 theForm.setGeographyName(geographyVO.getGraphDomainName());
                 theForm.setGeographyStr(geographyVO.getGraphDomainName());
             } else {
                 for (int i = 0, k = userGeoList.size(); i < k; i++) {
                     geographyVO = (UserGeographiesVO) userGeoList.get(i);
                     geoList.add(new ListValueVO(geographyVO.getGraphDomainName(), geographyVO.getGraphDomainCode()));
                 }
                 theForm.setGeographyList(geoList);
             }
         }

     
      final String userType = userVO.getUserType();
      String filePath = Constants.getProperty("DownloadBulkUserPath");
      try {
          final File fileDir = new File(filePath);
          if (!fileDir.isDirectory()) {
              fileDir.mkdirs();
          }
      } catch (Exception e) {
          _log.errorTrace(methodName, e);
          _log.error("loadDownloadFile", "Exception" + e.getMessage());
          throw new BTSLBaseException(this, "loadDownloadFile", "downloadfile.error.dirnotcreated", "selectDomainForInitiate");

      }
      final String fileName = theForm.getDomainCode() + Constants.getProperty("DownloadBulkUserFileNamePrefix") + BTSLUtil.getFileNameStringFromDate(new Date()) + ".xlsx";
      final ArrayList geoDomainList = theForm.getGeographyList();
      final ArrayList domainList = theForm.getDomainList();
      ListValueVO listVO = null;
      if (theForm.getGeographyCode().equals(PretupsI.ALL)) {
          String geographyCode = "";
          theForm.setGeographyName(PretupsRestUtil.getMessageString("list.all"));
          for (int i = 0, j = geoDomainList.size(); i < j; i++) {
              listVO = (ListValueVO) geoDomainList.get(i);
              geographyCode = geographyCode + listVO.getValue() + ",";
          }
          geographyCode = geographyCode.substring(0, geographyCode.length() - 1);
          theForm.setGeographyStr(geographyCode);
      } else if (geoDomainList != null && geoDomainList.size() > 1) {
          listVO = BTSLUtil.getOptionDesc(theForm.getGeographyCode(), geoDomainList);
          final String geographyName = listVO.getLabel();
          theForm.setGeographyName(geographyName);
          theForm.setGeographyStr(geographyName);
      }
      if (domainList != null && domainList.size() > 1) {
          listVO = BTSLUtil.getOptionDesc(theForm.getDomainCode(), theForm.getDomainList());
          final String domainName = listVO.getLabel();
          theForm.setDomainName(domainName);
      }
      final BatchUserDAO batchUserDAO = new BatchUserDAO();
      final BatchUserWebDAO batchUserWebDAO = new BatchUserWebDAO();
      final SubLookUpDAO sublookupDAO = new SubLookUpDAO();
      masterDataMap.put(PretupsI.BATCH_USR_CREATED_BY, userVO.getUserName());
      masterDataMap.put(PretupsI.BATCH_USR_GEOGRAPHY_NAME, theForm.getGeographyStr());
      masterDataMap.put(PretupsI.BATCH_USR_DOMAIN_NAME, theForm.getDomainName());
      masterDataMap.put(PretupsI.BATCH_USR_USER_PREFIX_LIST, LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true));
      masterDataMap.put(PretupsI.BATCH_USR_OUTLET_LIST, LookupsCache.loadLookupDropDown(PretupsI.OUTLET_TYPE, true));
      masterDataMap.put(PretupsI.BATCH_USR_SUBOUTLET_LIST, sublookupDAO.loadSublookupByLookupType(con, PretupsI.OUTLET_TYPE));
      final ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
      masterDataMap.put(PretupsI.BATCH_USR_SERVICE_LIST, servicesDAO.loadServicesList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE, null, false));
      masterDataMap.put(PretupsI.BATCH_USR_GEOGRAPHY_LIST, batchUserWebDAO.loadMasterGeographyList(con, theForm.getGeographyCode(), userVO.getUserID()));
      // /////////////////////////////
      masterDataMap.put(PretupsI.BATCH_USR_GEOG_LIST, batchUserWebDAO.loadGeographyList(con));
      masterDataMap.put(PretupsI.BATCH_USR_COMM_LIST, batchUserWebDAO.loadCommProfileList(con, theForm.getDomainCode(), userVO.getNetworkID(), userVO.getCategoryCode(),
          userType));
      masterDataMap.put(PretupsI.BATCH_USR_GEOGRAPHY_TYPE_LIST, batchUserWebDAO.loadCategoryGeographyTypeList(con, theForm.getDomainCode()));
      masterDataMap
          .put(PretupsI.BATCH_USR_CATEGORY_HIERARCHY_LIST, batchUserWebDAO.loadMasterCategoryHierarchyList(con, theForm.getDomainCode(), userVO.getNetworkID()));
      // Added by Shashank
      // Change made for batch user creation by channel users
      masterDataMap.put(PretupsI.BATCH_USR_CATEGORY_LIST, batchUserDAO.loadMasterCategoryList(con, theForm.getDomainCode(), userVO.getCategoryCode(), userType));
      masterDataMap.put(PretupsI.USER_TYPE, userType);
      masterDataMap.put(PretupsI.BATCH_USR_LANGUAGE_LIST, batchUserDAO.loadLanguageList(con));// added
      // by
      // deepika
      // aggarwal
      masterDataMap.put(PretupsI.BATCH_USR_GROUP_ROLE_LIST, batchUserDAO.loadMasterGroupRoleList(con, theForm.getDomainCode(), userVO.getCategoryCode(), userType));
      masterDataMap.put(PretupsI.BATCH_USR_GRADE_LIST, batchUserDAO.loadMasterCategoryGradeList(con, theForm.getDomainCode(), userVO.getCategoryCode(), userType));
      masterDataMap.put(PretupsI.USER_DOCUMENT_TYPE, LookupsCache.loadLookupDropDown(PretupsI.USER_DOCUMENT_TYPE, true));
      masterDataMap.put(PretupsI.PAYMENT_INSTRUMENT_TYPE, LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true));
      if (userType.equals(PretupsI.OPERATOR_USER_TYPE) || (userType.equals(PretupsI.CHANNEL_USER_TYPE) && SystemPreferences.BATCH_USER_PROFILE_ASSIGN))

      {

          masterDataMap.put(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST, batchUserDAO.loadMasterTransferProfileList(con, theForm.getDomainCode(),
              userVO.getNetworkID(), userVO.getCategoryCode(), userType));

          if (SystemPreferences.IS_TRF_RULE_USER_LEVEL_ALLOW) {
              masterDataMap.put(PretupsI.BATCH_USR_TRF_RULE_LIST, LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
          }
      }
      if(SystemPreferences.USER_VOUCHERTYPE_ALLOWED)
          masterDataMap.put(PretupsI.BATCH_OPT_USR_VOUCHERTYPE_LIST, new VomsProductDAO().loadVoucherTypeList(con));
      // End of Changes Made for batch user creation by channel users
      theForm.setBulkUserMasterMap(masterDataMap);
      // Call the ExcelWrite Method.. & write in XLS file for Master Data
      // Creation.
      final BatchUserCreationExcelRWPOI excelRW = new BatchUserCreationExcelRWPOI();
      
      excelRW.writeUserCreateExcel(ExcelFileIDI.BATCH_USER_INITIATE, masterDataMap, null, new Locale(lang, country), 
    		  filePath + fileName);

      File fileNew = new File(filePath + fileName);
		byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
		String encodedString = Base64.getEncoder().encodeToString(
				fileContent);
		String file1 = fileNew.getName();
		response.setFileattachment(encodedString);
		response.setFileType("xlsx");
		response.setFileName(file1);
		String sucess = Integer.toString(PretupsI.RESPONSE_SUCCESS);
		response.setStatus(sucess);
		response.setMessageCode(PretupsErrorCodesI.SUCCESS);
		String resmsg = RestAPIStringParser.getMessage(new Locale(lang, country),
				PretupsErrorCodesI.SUCCESS, null);
		response.setMessage(resmsg);
		
		

    }  catch (BTSLBaseException be) {
      	 log.error(methodName, "Exception:e=" + be);
         log.errorTrace(methodName, be);
         if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
         		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
        	 String unauthorised=Integer.toString(HttpStatus.SC_UNAUTHORIZED) ;
        	response.setStatus(unauthorised);
         	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
         	
         	 
         }
          else{
          String badReq=Integer.toString(HttpStatus.SC_BAD_REQUEST) ;
          response.setStatus(badReq);
          responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
        
          }
         String resmsg ="";
         if(be.getArgs()!=null) {
        	 resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), be.getArgs());

         }else {
        	 resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);

         }
 	   response.setMessageCode(be.getMessage());
 	   response.setMessage(resmsg);
	}
    catch (Exception e) {
        log.error(methodName, "Exceptin:e=" + e);
        log.errorTrace(methodName, e);
        String fail=Integer.toString(PretupsI.RESPONSE_FAIL) ;
        response.setStatus(fail);
		response.setMessageCode("error.general.processing");
		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
		return response;
    } finally {
		try {
			if (mcomCon != null) {
				mcomCon.close("DownloadTemplateChannelUser");
				mcomCon = null;
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

	}
	return response;

}
}