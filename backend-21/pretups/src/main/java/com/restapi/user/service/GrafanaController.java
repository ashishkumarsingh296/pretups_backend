package com.restapi.user.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.Constants;

import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${GrafanaController.name}", description = "${GrafanaController.desc}")//@Api(tags = "User Services")
@RestController
@RequestMapping(value = "/v1/grafanaServices")
public class GrafanaController {
	public static final Log log = LogFactory.getLog(GrafanaController.class.getName());

	@GetMapping(value = "/dashboardService",  produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags = "User Services", value = "Dashboard Sync Service", notes = "DASHBOARD_SYNC_DESC", response = BaseResponse.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
			@ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${dashboardService.summary}", description="${dashboardService.description}",

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

	public BaseResponse dashboardService(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			
			 HttpServletResponse response1)
			throws IOException, SQLException, BTSLBaseException

	{

		String methodName = "dashboardService";

		MComConnectionI mcomCon = null;
		Connection con = null;
		UriComponentsBuilder uriBuilder = null;
		BaseResponse response = null;

		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			HashMap<String, String> userIdLoginMap = fetchGrafanaUsers();
			
			RestTemplate restTemplate = new RestTemplate();
			final String uri = Constants.getProperty("GRAFANA_UPDATE_DASHBOARD_PERMISSSION_API_URL"); // "http://172.30.38.120:3000//api/dashboards/id/2/permissions";
			HttpEntity<?> entity = null;
			HttpHeaders headers1 = new HttpHeaders();

			headers1.add("Authorization", "Basic YWRtaW46YWRtaW5AMTIz");
			headers1.add("Content-Type", MediaType.APPLICATION_JSON);

			ArrayList<DashboardPermissionVO> dashboardVOList = (new UserDAO()).loaduserPermission(con, userIdLoginMap);

			for (DashboardPermissionVO dashboardPermVO : dashboardVOList) {


				System.out.println("items>>>> "+dashboardPermVO.getId());
				entity = new HttpEntity<>(dashboardPermVO.getItems(), headers1);

				uriBuilder = UriComponentsBuilder.fromHttpUrl(uri.replace("DASHBOARD_ID", dashboardPermVO.getId()));

				ResponseEntity<String> responseEntity1 = restTemplate.exchange(uriBuilder.toUriString(),
						HttpMethod.POST, entity, String.class);
				//break;
			}

			log.debug(methodName, "Permission(s) updated successfully");

		} catch (Exception ex) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			log.errorTrace(methodName, ex);
			log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
		} finally {
			if (mcomCon != null) {
				mcomCon.close("GrafanaController#dashboardService");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("dashboardService", " Exited ");
			}
		}

		return response;
	}

	private HashMap<String, String> fetchGrafanaUsers() {
		
		HttpEntity<?> entity = null;
		RestTemplate restTemplate = new RestTemplate();
		UriComponentsBuilder uriBuilder = null;
		final String uri = "http://172.30.38.120:3000//api/users";//Constants.getProperty("GRAFANA_FETCH_USERS_API_URL");
		
		
		uriBuilder = UriComponentsBuilder.fromHttpUrl(uri);

		
		HttpHeaders headers1 = new HttpHeaders();

		headers1.add("Authorization", "Basic YWRtaW46YWRtaW5AMTIz");
		headers1.add("Content-Type", MediaType.APPLICATION_JSON);

		
		
		entity = new HttpEntity<>(headers1);

		uriBuilder = UriComponentsBuilder.fromHttpUrl(uri);

		ResponseEntity<DashboardUsersVO[]> dashboardUsers = restTemplate.exchange(uriBuilder.toUriString(),
				HttpMethod.GET, entity, DashboardUsersVO[].class);
		
		HashMap<String, String> userIdLoginMap = new HashMap<String, String>();
		
		for(DashboardUsersVO userObj: dashboardUsers.getBody()) {
			userIdLoginMap.put(userObj.login, userObj.id+"");
		}
		

		
		return userIdLoginMap;
	}
	public static void main(String args[]) {
		
		
	//	fetchGrafanaUsers();
		/*
		 * 
		 * UriComponentsBuilder uriBuilder = null; BaseResponse response = null;
		 * RestTemplate restTemplate = new RestTemplate(); final String uri =
		 * "http://172.30.38.120:3000//api/dashboards/id/2/permissions"; HttpEntity<?>
		 * entity = null; HttpHeaders headers1 = new HttpHeaders();
		 * 
		 * headers1.add("Authorization", "Basic YWRtaW46YWRtaW5AMTIz");
		 * headers1.add("Content-Type", MediaType.APPLICATION_JSON);
		 * 
		 * String requestVO = "{\r\n" + "  \"items\": [\r\n" + "    \r\n" + "    {\r\n"
		 * + "       \"userId\": 2,\r\n" + "      \"permission\": 4\r\n" + "    },{\r\n"
		 * + "       \"userId\": 1,\r\n" + "      \"permission\": 4\r\n" + "    }\r\n" +
		 * "  ]\r\n" + "}";
		 * 
		 * 
		 * DashboardPermission items = new DashboardPermission();
		 * 
		 * ArrayList<Item> itemsList = new ArrayList<>();
		 * 
		 * Item item = new Item();
		 * 
		 * item.setUserId(1); item.setPermission(4); itemsList.add(item);
		 * 
		 * items.setItems(itemsList);
		 * 
		 * entity = new HttpEntity<>(items, headers1);
		 * 
		 * uriBuilder = UriComponentsBuilder.fromHttpUrl(uri);
		 * 
		 * ResponseEntity<String> responseEntity1 =
		 * restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity,
		 * String.class);
		 * 
		 * System.out.println("responseEntity1 " + responseEntity1);
		 * 
		 */}

}
