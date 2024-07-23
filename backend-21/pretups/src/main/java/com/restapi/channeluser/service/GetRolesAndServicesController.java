package com.restapi.channeluser.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;



//@io.swagger.v3.oas.annotations.tags.Tag(name = "${}", description = "${}")//@Api(tags= "Get Roles and Services", value="Get Roles and Services")
@io.swagger.v3.oas.annotations.tags.Tag(name = "${GetRolesAndServicesController.name}", description = "${GetRolesAndServicesController.desc}")//@Api(tags="Channel Users",value="Channel Users")
@RestController
@RequestMapping(value = "/v1/channelUsers")
public class GetRolesAndServicesController {
	
	public static final Log log = LogFactory.getLog(GetRolesAndServicesController.class.getName());
	
	@GetMapping(value= "/rolesServices", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags="Channel Users", value = "Get Roles and Services ",
	  
	  authorizations = {
	            @Authorization(value = "Authorization")})
@ApiResponses(value = {
@ApiResponse(code = 200, message = "OK", response = GetRolesAndServicesResponseVO.class),
@ApiResponse(code = 400, message = "Bad Request"),
@ApiResponse(code = 401, message = "Unauthorized"),
@ApiResponse(code = 404, message = "Not Found")
})*/

    @io.swagger.v3.oas.annotations.Operation(summary = "${rolesServices.summary}", description="${rolesServices.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GetRolesAndServicesResponseVO.class))
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

	public GetRolesAndServicesResponseVO getRolesAndServices(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "userCategeryCode", required = true)
	        @RequestParam("userCategeryCode") String userCategeryCode,
	        @Parameter(description = "networkCode", required = true)
	        @RequestParam("networkCode") String networkCode,HttpServletResponse response
			) throws IOException, SQLException, BTSLBaseException {
		final String methodName =  "getRolesAndServices";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		GetRolesAndServicesResponseVO response1 = null;
        String messageArray[] = new String[1];
        Connection con = null;MComConnectionI mcomCon = null;
        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
        	response1= new GetRolesAndServicesResponseVO();
			
			OAuthenticationUtil.validateTokenApi(headers);
			
			if(BTSLUtil.isNullString(networkCode))
			{
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NETWORK_CODE_EMPTY, PretupsI.RESPONSE_FAIL, null);
			}
			if(BTSLUtil.isNullString(userCategeryCode))
			{
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_NOT_EXIST, PretupsI.RESPONSE_FAIL, null);
			}
			final List catList = new CategoryDAO().loadCategoryDetailsUsingCategoryCode(con, userCategeryCode);
			CategoryVO categoryVO = (CategoryVO) catList.get(0);
			Map rolesMap = null;
			Map<String,HashMap<String, ArrayList<UserRolesVO>>> rolesMapNew=new LinkedHashMap<>();
			Map<String, ArrayList<UserRolesVO>> rolesMapNew1=new HashMap<String, ArrayList<UserRolesVO>>();
			HashMap result = new HashMap<>();
			   if (PretupsI.YES.equalsIgnoreCase(categoryVO.getWebInterfaceAllowed())) {
                   final UserRolesDAO userRolesDAO = new UserRolesDAO();
                   
                       	rolesMap =userRolesDAO.loadRolesListByGroupRole_new(con, userCategeryCode, "N");
                       	response1.setSystemRole(rolesMap);
                       	
                       	rolesMap =userRolesDAO.loadRolesListByGroupRole(con, userCategeryCode, "Y");
                       	response1.setGroupRole(rolesMap);
//                            Set rolesKeys = rolesMap.keySet();
//                            ArrayList<UserRolesVO> rolesListNew=new ArrayList<UserRolesVO>();
//                            ArrayList<UserRolesVO> rolesListNew1=new ArrayList<UserRolesVO>();
//                            ArrayList<Object> grouproles = new ArrayList<>();
//                            Iterator keyiter = rolesKeys.iterator();
//                           while(keyiter.hasNext()){
//                               String rolename=(String)keyiter.next();
//                               ArrayList rolesVOList=(ArrayList)rolesMap.get(rolename);
//                               rolesListNew=new ArrayList<UserRolesVO>();
//                               rolesListNew1=new ArrayList<UserRolesVO>();
//                               Iterator i=rolesVOList.iterator();
//                               while(i.hasNext()){
//                                     UserRolesVO rolesVO=(UserRolesVO)i.next();
//                                     if(rolesVO.getStatus().equalsIgnoreCase("Y"))
//                                     {
//                                    	 if(rolesVO.getGroupRole().equals("Y"))
//                                    	 {
//                                    		 rolesListNew1.add(rolesVO);
//                                    	 }
//                                           grouproles.add(rolesVO.getRoleCode());
//                                     }
//                               }
//                              
//                               if(rolesListNew1.size()>0)
//                               rolesMapNew1.put(rolename, rolesListNew1);
//                               result.put("GROUP ROLE",rolesMapNew1);
//                         }
               }        	
			   ArrayList<ListValueVO> serviceList = new ServicesTypeDAO().loadServicesList(con, networkCode, PretupsI.C2S_MODULE, userCategeryCode, false);
			   List<String> serviceTypeList = new ArrayList<String>();
               int serviceLists=serviceList.size();
               ListValueVO listValueVO = null;
               for (int i = 0; i <serviceLists ; i++) {
                   listValueVO = serviceList.get(i);
                   serviceTypeList.add(listValueVO.getCodeName());
               }
               ArrayList<String> voucTypes = new ArrayList<String>();
               if(SystemPreferences.USER_VOUCHERTYPE_ALLOWED && ((Boolean)PreferenceCache.getControlPreference(PreferenceI.CHNLUSR_VOUCHER_CATGRY_ALLWD, networkCode, userCategeryCode)).booleanValue())
               {
            	   VomsProductDAO voucherDAO = new VomsProductDAO();
                   ArrayList <ListValueVO>voucherList = new ArrayList<ListValueVO>();
                   voucherList = voucherDAO.loadVoucherTypeList(con);
                   
                   for(int i=0;i<voucherList.size();i++)
                   {
                   	voucTypes.add(voucherList.get(i).getCodeName());
                   }
               }
               final ArrayList localeList = LocaleMasterCache.getLocaleListForSMS();
               ArrayList languageList = null;
               if (localeList != null) {
                   languageList = new ArrayList();
                   LocaleMasterVO localeMasterVO = null;
                   Locale locale = null;
                   for (int i = 0, j = localeList.size(); i < j; i++) {
                       locale = (Locale) localeList.get(i);
                       localeMasterVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                       languageList.add(localeMasterVO);
                   }
               }
               
               UserDAO userDAO = new UserDAO();
               ArrayList profileList = userDAO.loadPhoneProfileList(con,userCategeryCode);
               
               response1.setProfileList(profileList);
               response1.setVoucherList(voucTypes);
               response1.setLanguagesList(languageList);
               response1.setServicesList(serviceTypeList);
//               response1.setGroupRole(result);
               response1.setService("ROLESSERVVOURESP");
               response1.setStatus(String.valueOf(HttpStatus.SC_OK));
               response1.setMessage("Roles,Services and Voucher list successfully fetched");
        }
        catch (BTSLBaseException be) {
        	 log.error(methodName, "Exception:e=" + be);
             log.errorTrace(methodName, be);
             if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage()))
           {
             	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
             	 response1.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
             }
              else{
              response.setStatus(HttpStatus.SC_BAD_REQUEST);
              response1.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
              }
     	   String resmsg  = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessage(), messageArray);
     	    response1.setMessageCode(be.getMessage());
     	   response1.setMessage(resmsg);
     	   
	}
        catch (Exception e) {
        	 response.setStatus(HttpStatus.SC_BAD_REQUEST);
        	response1.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
         	  response1.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
         	  
        } finally {
            try {
            	if (mcomCon != null) {
    				mcomCon.close("GetRolesAndServicesController#getRolesAndServices");
    				mcomCon = null;
    			}
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }
        }
		return response1;
	}
}
