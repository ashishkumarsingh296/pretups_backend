package com.restapi.superadmin;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
//import com.btsl.pretups.network.web.NetworkForm;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.superadmin.responseVO.NetworkListResponse;
import com.restapi.superadmin.serviceI.NetworkChangeI;

import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${NetworkChangeController.name}", description = "${NetworkChangeController.desc}")//@Api(tags="Super Admin")
@RestController
@RequestMapping(value = "/v1/superadmin")
public class NetworkChangeController {
	
	public static final Log log = LogFactory.getLog(NetworkChangeController.class.getName());
	
	@Autowired
	private NetworkChangeI networkChangeI;
	
	@GetMapping(value= "/loadNetworkList", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Load the network list for change",
		           response = NetworkListResponse.class,
		           authorizations = {
		               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "OK", response = NetworkListResponse.class),
		      @ApiResponse(code = 400, message = "Bad Request" ),
		      @ApiResponse(code = 401, message = "Unauthorized"),
		      @ApiResponse(code = 404, message = "Not Found")
		 })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadNetworkList.summary}", description="${loadNetworkList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = NetworkListResponse.class))
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


	public NetworkListResponse loadNetworkList (@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest request)throws Exception {
		final String methodName = "loadNetworkList";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
		
    	NetworkListResponse response = new NetworkListResponse();
    	UserDAO userDao = new UserDAO();
    	
    	Connection con = null;
		MComConnectionI mcomCon = null;
		
		
		try {
			
			
			
            
            HttpSession session = request.getSession();
//            String leftMenu = (String) session.getAttribute("leftMenu");
//            if (!TypesI.NO.equals(leftMenu)) {
//                this.authorise(request, response, "CHNW001", false);
//            }

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			
			
			
            //NetworkForm theForm = (NetworkForm) form;
            // flushing the form
           // theForm.flush();

            //UserVO userVO = (UserVO) getUserFormSession(request);
			ChannelUserVO userVO = userDao.loadAllUserDetailsByLoginID( con, OAuthUserData.getData().getLoginid() );

            // for checking the radio button of the active netwrok
            response.setCode(userVO.getNetworkID());
            NetworkDAO _networkDAO = new NetworkDAO();

            String status = "'" + PretupsI.STATUS_DELETE + "'";
            if (TypesI.NO.equals(userVO.getCategoryVO().getViewOnNetworkBlock())) {
                status = "'" + PretupsI.STATUS_DELETE + "','" + PretupsI.STATUS_SUSPEND + "'";
            }
            ArrayList networkList=null;
            if(TypesI.SUPER_NETWORK_ADMIN.equals(userVO.getCategoryCode()) || TypesI.SUPER_CUSTOMER_CARE.equals(userVO.getCategoryCode()))
            {
            	networkList = _networkDAO.loadNetworkListForSuperOperatorUsers(con, status, userVO.getUserID());
            }
            else if(TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode()))
            {
            	networkList = _networkDAO.loadNetworkListForSuperChannelAdm(con, status, userVO.getUserID());
            }
            else
            {
            	 networkList = _networkDAO.loadNetworkList(con, status);
            }

            if (!networkList.isEmpty()) {
                NetworkVO vo = (NetworkVO) networkList.get(0);

                if (BTSLUtil.isNullString(userVO.getNetworkID())) {
                    response.setCode(vo.getNetworkCode());
                } else {
                    response.setCode(userVO.getNetworkID());
                }

                // set the statusDesc field
                response.setStatusList(LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true));
                for (int i = 0, j = networkList.size(); i < j; i++) {
                    NetworkVO netVO = (NetworkVO) networkList.get(i);
                    ListValueVO listVO = BTSLUtil.getOptionDesc(netVO.getStatus(), response.getStatusList());
                    netVO.setStatusDesc(listVO.getLabel());
                }
            }

            response.setDataList(networkList);
            response.setStatus(HttpStatus.SC_OK);
            //forward = mapping.findForward("changeNetwork");
        } catch (Exception e) {
            log.error(methodName, "Exceptin:e=" + e);
            log.errorTrace(methodName, e);
            //return super.handleError(this, methodName, e, request, mapping);
//            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
//			response.setMessageCode(be.getMessageKey());
//			response.setMessage(msg);
	        response.setStatus(HttpStatus.SC_BAD_REQUEST);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setMessage(PretupsI.FAIL);
            e.printStackTrace();
        } finally {
			if (mcomCon != null) {
				mcomCon.close("NetworkAction#loadNetworkListForChange");
				mcomCon = null;
			}

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting");
            }
        }

       // return forward;
		
    	return response;
		
	}
	
	
	
	
	
	
	
	
	@GetMapping(value= "/changeUserNetwork", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Change user network from the list",
		           response = NetworkListResponse.class,
		           authorizations = {
		               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
		      @ApiResponse(code = 400, message = "Bad Request" ),
		      @ApiResponse(code = 401, message = "Unauthorized"),
		      @ApiResponse(code = 404, message = "Not Found")
		 })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${changeUserNetwork.summary}", description="${changeUserNetwork.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BaseResponse.class))
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

	public BaseResponse changeUserNetworkFromNetworkList (@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest request, @RequestParam("networkCode") String networkCode)throws Exception {
		final String methodName = "changeUserNetworkFromNetworkList";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        
        
    	BaseResponse response = new BaseResponse();
		//code for fetching network
		NetworkListResponse networkListDetails = new NetworkListResponse();
    	UserDAO userDao = new UserDAO();
    	
    	Connection con = null;
		MComConnectionI mcomCon = null;
		//
		
		try {
			
			//
			 HttpSession session = request.getSession();
//           String leftMenu = (String) session.getAttribute("leftMenu");
//           if (!TypesI.NO.equals(leftMenu)) {
//               this.authorise(request, response, "CHNW001", false);
//           }

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			
			
			
           //NetworkForm theForm = (NetworkForm) form;
           // flushing the form
          // theForm.flush();

           //UserVO userVO = (UserVO) getUserFormSession(request);
			ChannelUserVO userVO = userDao.loadAllUserDetailsByLoginID( con, OAuthUserData.getData().getLoginid() );

           // for checking the radio button of the active netwrok
		   networkListDetails.setCode(userVO.getNetworkID());
           NetworkDAO _networkDAO = new NetworkDAO();

           String status = "'" + PretupsI.STATUS_DELETE + "'";
           if (TypesI.NO.equals(userVO.getCategoryVO().getViewOnNetworkBlock())) {
               status = "'" + PretupsI.STATUS_DELETE + "','" + PretupsI.STATUS_SUSPEND + "'";
           }
           ArrayList networkList=null;
           if(TypesI.SUPER_NETWORK_ADMIN.equals(userVO.getCategoryCode()) || TypesI.SUPER_CUSTOMER_CARE.equals(userVO.getCategoryCode()))
           {
           	networkList = _networkDAO.loadNetworkListForSuperOperatorUsers(con, status, userVO.getUserID());
           }
           else if(TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode()))
           {
           	networkList = _networkDAO.loadNetworkListForSuperChannelAdm(con, status, userVO.getUserID());
           }
           else
           {
           	 networkList = _networkDAO.loadNetworkList(con, status);
           }

           if (!networkList.isEmpty()) {
               NetworkVO vo = (NetworkVO) networkList.get(0);

               if (BTSLUtil.isNullString(userVO.getNetworkID())) {
            	   networkListDetails.setCode(vo.getNetworkCode());
               } else {
            	   networkListDetails.setCode(userVO.getNetworkID());
               }

               // set the statusDesc field
               networkListDetails.setStatusList(LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true));
               for (int i = 0, j = networkList.size(); i < j; i++) {
                   NetworkVO netVO = (NetworkVO) networkList.get(i);
                   ListValueVO listVO = BTSLUtil.getOptionDesc(netVO.getStatus(), networkListDetails.getStatusList());
                   netVO.setStatusDesc(listVO.getLabel());
               }
           }

           networkListDetails.setDataList(networkList);
           //
			
			
//          String leftMenu = (String) session.getAttribute("leftMenu");
//          if (!TypesI.NO.equals(leftMenu)) {
//              this.authorise(request, response, "CHNW001", false);
//          }

			
            //NetworkForm theForm = (NetworkForm) form;
            //UserVO userVO = getUserFormSession(request);

            // get the selected radio button value i.e the NetworkCode
            //String networkCode = theForm.getCode();
			
            NetworkVO networkVO = this.loadNetworkDetail(networkCode,networkListDetails);
            
         // change the network related information into the userVO
            userVO.setNetworkID(networkVO.getNetworkCode());
            userVO.setNetworkName(networkVO.getNetworkName());
            userVO.setReportHeaderName(networkVO.getReportHeaderName());
            userVO.setNetworkStatus(networkVO.getStatus());
            LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
            if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                userVO.setMessage(networkVO.getLanguage1Message());
            } else {
                userVO.setMessage(networkVO.getLanguage2Message());
            }

            /*
             * while change the network location also change the user
             * geographical list
             * becs while adding user we check the domain type of the loginUser
             * and AddedUser
             * if domain type is same of both the user then the geographical
             * list of the added user is same as
             * the geographical list of the login user
             */
            if(!TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode()))
            {
            UserGeographiesVO geographyVO = null;
            ArrayList geographyList = new ArrayList();
            geographyVO = new UserGeographiesVO();
            geographyVO.setGraphDomainCode(userVO.getNetworkID());
            geographyVO.setGraphDomainName(userVO.getNetworkName());
            geographyVO.setGraphDomainTypeName(userVO.getCategoryVO().getGrphDomainTypeName());
            geographyList.add(geographyVO);

            userVO.setGeographicalAreaList(geographyList);
            }
            else
            {
            	ArrayList	userGeoList = new GeographicalDomainDAO().loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID());
            	userVO.setGeographicalAreaList(userGeoList);
            }
            
         // for checking the radio button of the active netwrok
            networkListDetails.setCode(userVO.getNetworkID());

            String[] arr = { userVO.getNetworkName() };
            BTSLMessages btslMessage = null;
            //HttpSession session = request.getSession();
            if (BTSLUtil.isNullString((String) session.getAttribute("leftMenu"))) {
                btslMessage = new BTSLMessages("network.changenetwork.successmessagechange", arr, "changeNetwork");
            } else {
                btslMessage = new BTSLMessages("network.changenetwork.successmessageselect", arr);
            }
            // for Zebra and Tango by Ved date 27/09/07
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()) {
                new CommonUtil().setSessionParametes(session, userVO);
            }
            String userId = userVO.getActiveUserID();
            networkChangeI.updateLoggedInNetworkCode(headers,networkCode,response1,con,userId);
            
            response.setMessageCode(Integer.toString(HttpStatus.SC_OK));
            response.setMessage(PretupsI.SUCCESS);
			response1.setStatus(HttpStatus.SC_OK);
			response.setStatus(HttpStatus.SC_OK);

		}catch (Exception e) {
	        log.error(methodName, "Exceptin:e=" + e);
	        log.errorTrace(methodName, e);
	        //return super.handleError(this, methodName, e, request, mapping);
	        response.setStatus(HttpStatus.SC_BAD_REQUEST);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setMessage(PretupsI.FAIL);
	        e.printStackTrace();
	    }
	    finally
	    {
			if (mcomCon != null) {
				mcomCon.close("NetworkAction#changeUserNetwork");
				mcomCon = null;
			}
	    }
	
	    if (log.isDebugEnabled()) {
	        log.debug(methodName, "Exiting");
	    }
				
				return response;
		
	}
	
	
	
	
	
	
	
	
	
	 protected UserVO getUserFormSession(HttpServletRequest request) throws BTSLBaseException {
	        UserVO userVO = null;
	        // HttpSession session = request.getSession(true);
	        HttpSession session = request.getSession(false);
	        Object obj = session.getAttribute("user");

	        if (obj != null) {
	            userVO = (UserVO) obj;
	        }
	        // add this condition after getting the userVO from request, if null.
	        if (obj == null || userVO == null) {
	            throw new BTSLBaseException("common.topband.message.sessionexpired", "unAuthorisedAccessF");
	        }
	        return userVO;

	    }
	 
	 
	 /**
	     * Method execute This methods will load the particular networkDetail
	     * on the basis of NetworkCode
	     * 
	     * @param networkCode
	     * @param form
	     * @return NetworkVO
	     */
	    private NetworkVO loadNetworkDetail(String p_networkCode, NetworkListResponse networkListDetails) throws Exception {
	        if (log.isDebugEnabled()) {
	            log.debug("loadNetworkDetail", "Entered p_networkCode=" + p_networkCode);
	        }
	        //NetworkForm theForm = (NetworkForm) form;
	        ArrayList networkList = networkListDetails.getDataList();
	        NetworkVO networkVO = null;
	        boolean flag = false;

	        if (networkList != null && !networkList.isEmpty()) {
	            for (int i = 0, j = networkList.size(); i < j; i++) {
	                networkVO = (NetworkVO) networkList.get(i);
	                if (p_networkCode.equals(networkVO.getNetworkCode())) {
	                    flag = true;
	                    break;
	                } else {
	                    flag = false;
	                }
	            }
	        }
	        if (log.isDebugEnabled()) {
	            log.debug("loadNetworkDetail", "Exiting networkVO=" + networkVO);
	        }

	        if (flag) {
	            return networkVO;
	        } else {
	            return null;
	        }
	    }
}
