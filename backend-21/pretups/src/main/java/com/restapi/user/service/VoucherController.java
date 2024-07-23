package com.restapi.user.service;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${VoucherController.name}", description = "${VoucherController.desc}")//@Api(tags = "Tag1")
//@Tag(name = "Tag1")
@RestController
@RequestMapping(value = "/v1/voucher")
public class VoucherController {

	public static final Log log = LogFactory.getLog(VoucherController.class.getName());

	@PostMapping(value = "/voucherConsumption", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = SwaggerAPIDescriptionI.VOUCHER_CONSUMPTION_API, response = VoucherConResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = VoucherConResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${voucherConsumption.summary}", description="${voucherConsumption.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = VoucherConResponseVO.class))
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

	public VoucherConResponseVO consumeVoucher(HttpServletRequest httpServletRequest,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "Voucher Consuption Request") @RequestBody VoucherConRequestVO voucherConRequestVO,
			HttpServletResponse response1) throws IOException, SQLException, BTSLBaseException {

		final String methodName = "consumeVoucher";
		if (log.isDebugEnabled()) {
			log.debug("entering", methodName);
		}

		VoucherConResponseVO response = new VoucherConResponseVO();
		final VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		String type = "VOMSCONSREQ";
		OAuthUser oAuthUserData = new OAuthUser();
		oAuthUserData.setData(new OAuthUserData());
		Locale locale = null;

		try {
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),
					(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, new BaseResponseMultiple());

			// getting voucher MRP
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			VomsVoucherVO vomsVoucherVO = vomsVoucherDAO.getVoucherDetails(con, voucherConRequestVO.getSerialnumber());
			if (vomsVoucherVO == null) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOMS_O2C_VOUCHER_NOT_FOUND);
			}
			con.close();

			String payload = generatePayload(type, voucherConRequestVO, vomsVoucherVO);
			String url = getInstanceURL();
			url = url + "P2PReceiver?REQUEST_GATEWAY_CODE=" + Constants.getProperty("voucherConsumption.gateway_code")
			+ "&REQUEST_GATEWAY_TYPE=" + Constants.getProperty("voucherConsumption.gateway_type") + "&LOGIN="
			+ Constants.getProperty("voucherConsumption.gateway_loginId") + "&PASSWORD=" + Constants.getProperty("voucherConsumption.gateway_pswd")
			+ "&SOURCE_TYPE=" + Constants.getProperty("voucherConsumption.gateway_src_type") + "&SERVICE_PORT="
			+ Constants.getProperty("voucherConsumption.gateway_port");

			// Create the request headers
			HttpHeaders p2Pheaders = new HttpHeaders();
			p2Pheaders.setContentType(org.springframework.http.MediaType.APPLICATION_XML);

			// Create the request entity
			HttpEntity<String> requestEntity = new HttpEntity<>(payload, p2Pheaders);
			RestTemplate restTemplate = new RestTemplate();
//			UriComponentsBuilder uriBuilder = 
//					UriComponentsBuilder.fromHttpUrl(url)
//					.queryParam("REQUEST_GATEWAY_CODE", oAuthUserData.getReqGatewayCode())
//					.queryParam("REQUEST_GATEWAY_TYPE", oAuthUserData.getReqGatewayType())
//					.queryParam("LOGIN", oAuthUserData.getReqGatewayLoginId())
//					.queryParam("PASSWORD", oAuthUserData.getReqGatewayPassword())
//					.queryParam("SOURCE_TYPE", oAuthUserData.getReqGatewayCode())
//					.queryParam("SERVICE_PORT", oAuthUserData.getServicePort());
//			log.info(methodName, uriBuilder.toUriString());

			String xmlResponse = restTemplate.postForObject(url, requestEntity, String.class);
			JAXBContext jaxbContext = JAXBContext.newInstance(VoucherConResponseVO.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			response = (VoucherConResponseVO) unmarshaller.unmarshal(new StringReader(xmlResponse));
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
		} catch (JAXBException e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response1.setStatus(PretupsI.RESPONSE_FAIL);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			response.setMessageCode("error.general.parsing");
			response.setMessage("Due to some parse issue, request failed");
		} catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			response1.setStatus(PretupsI.RESPONSE_FAIL);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");
		} finally {
			if (mcomCon != null) {
				mcomCon.close("addMessGateway");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
		log.info(methodName, response);
		return response;
	}

	private String getInstanceURL() {
		InstanceLoadVO instanceLoadVO = this.getInstanceLoadVOObject();
		if (SystemPreferences.HTTPS_ENABLE) {
			return PretupsI.HTTPS_URL + instanceLoadVO.getHostAddress() + PretupsI.COLON + instanceLoadVO.getHostPort()
					+ PretupsI.FORWARD_SLASH + instanceLoadVO.getContext() + PretupsI.FORWARD_SLASH;
		} else {
			return PretupsI.HTTP_URL + instanceLoadVO.getHostAddress() + PretupsI.COLON + instanceLoadVO.getHostPort()
					+ PretupsI.FORWARD_SLASH + instanceLoadVO.getContext() + PretupsI.FORWARD_SLASH;
		}
	}

	private InstanceLoadVO getInstanceLoadVOObject() {
		InstanceLoadVO instanceLoadVO, instanceLoadVORest;
		instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(
				Constants.getProperty("INSTANCE_ID") + "_" + PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (instanceLoadVO != null) {
			instanceLoadVORest = LoadControllerCache.getInstanceLoadForNetworkHash(
					instanceLoadVO.getRstInstanceID() + "_" + PretupsI.REQUEST_SOURCE_TYPE_REST);
		} else {
			throw new RuntimeException(
					PretupsRestUtil.getMessageString("no.mapping.found.for.web.sms.or.rest.configuration"));
		}

		if (instanceLoadVORest == null) {
			instanceLoadVORest = LoadControllerCache.getInstanceLoadForNetworkHash(
					instanceLoadVO.getRstInstanceID() + "_" + PretupsI.REQUEST_SOURCE_TYPE_SMS);
		}

		if (instanceLoadVORest == null) {
			instanceLoadVORest = instanceLoadVO;
		}

		return instanceLoadVORest;
	}
	
	private String generatePayload(String type, VoucherConRequestVO voucherConRequestVO,VomsVoucherVO vomsVoucherVO) {
		
		String extcode = "";
		
		return "<?xml version=\"1.0\"?>\n" + 
				"<COMMAND>\n" + 
						"    <TYPE>" + type + "</TYPE>\n" +
						"    <EXTNWCODE>" + voucherConRequestVO.getExtnwcode() + "</EXTNWCODE>\n" + 
						"    <MSISDN>" + voucherConRequestVO.getMsisdn() + "</MSISDN>\n" + 
						"    <PIN>" + voucherConRequestVO.getPin() + "</PIN>\n" + 
						"    <MSISDN2>" + voucherConRequestVO.getMsisdn2() + "</MSISDN2>\n" + 
						"    <AMOUNT>" + (vomsVoucherVO.getMRP()/((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()) + "</AMOUNT>\n"+ 
						"    <EXTCODE>" + extcode + "</EXTCODE>\n" + 
						"    <EXTREFNUM>" + voucherConRequestVO.getExternalRefId() + "</EXTREFNUM>\n"+ 
						"    <VOUCHERCODE>" + voucherConRequestVO.getVouchercode() + "</VOUCHERCODE>\n" + 
						"    <SERIALNUMBER>" + voucherConRequestVO.getSerialnumber() + "</SERIALNUMBER>\n" + 
						"    <LANGUAGE1>" + voucherConRequestVO.getLanguage1() + "</LANGUAGE1>\n" + 
						"    <LANGUAGE2>" + voucherConRequestVO.getLanguage2() + "</LANGUAGE2>\n" + 
						"    <SELECTOR>" + voucherConRequestVO.getSelector() + "</SELECTOR>\n" + 
						"    <INFO1>" + voucherConRequestVO.getInfo1() + "</INFO1>\n" + 
						"    <INFO2>" + voucherConRequestVO.getInfo2() + "</INFO2>\n" + 
						"    <INFO3>" + voucherConRequestVO.getInfo3() + "</INFO3>\n" + 
						"    <INFO4>" + voucherConRequestVO.getInfo4() + "</INFO4>\n" + 
						"    <INFO5>" + voucherConRequestVO.getInfo5() + "</INFO5>\n" + 
				"</COMMAND>";
	}

}
