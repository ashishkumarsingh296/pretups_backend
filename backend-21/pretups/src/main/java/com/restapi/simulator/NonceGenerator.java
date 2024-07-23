package com.restapi.simulator;

import java.io.IOException;
import java.security.SecureRandom;

import javax.ws.rs.core.MediaType;


import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${NonceGenerator.name}", description = "${NonceGenerator.desc}")//@Api(tags = "Nonce Generator")
@RestController
@RequestMapping(value = "/v1/nonceGenerator")
public class NonceGenerator {

	
	@GetMapping(value = "/generateNonce", produces = MediaType.APPLICATION_JSON)
	public String processGenerateNonce() {
		byte[] nonce = new byte[8];
		new SecureRandom().nextBytes(nonce);
		return convertBytesToHex(nonce);
	}

	private static String convertBytesToHex(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte temp : bytes) {
			result.append(String.format("%02x", temp));
		}
		return result.toString();
	}

	@PostMapping(value = "/calculateSignature",consumes=  MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	//@ApiOperation(value = "Calculate Signature", response = NonceGeneratorResponseVO.class)

	@io.swagger.v3.oas.annotations.Operation(summary = "${calculateSignature.summary}", description="${calculateSignature.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = NonceGeneratorResponseVO.class))
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

	public NonceGeneratorResponseVO calculateSignature(String token , @RequestBody String body) {
		NonceGeneratorResponseVO nonceGeneratorResponseVO = new NonceGeneratorResponseVO();
		String nonce = processGenerateNonce();
		String KEY = token + "." + nonce;
		nonceGeneratorResponseVO.setNonce(nonce);
		try {
			nonceGeneratorResponseVO.setSignature(resolveEncodedBody(body, KEY));
			nonceGeneratorResponseVO.setMessage("Success");
		} catch (IOException e) {
			nonceGeneratorResponseVO.setMessage(e.getMessage());
		}
		return nonceGeneratorResponseVO;
	}

	private String resolveEncodedBody(String body, final String KEY) throws IOException {
		final HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, KEY);
		String jsonBody = body;
		return (hmacUtils.hmacHex(jsonBody));
	}

}
