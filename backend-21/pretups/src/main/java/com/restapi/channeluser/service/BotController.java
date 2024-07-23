package com.restapi.channeluser.service;

import java.io.IOException;
import java.io.PrintWriter;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.captcha.botdetect.web.servlet.SimpleCaptcha;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${BotController.name}", description = "${BotController.desc}")//@Api(tags="Bot Controller")
@RestController
@RequestMapping(value = "/v1/bot")
public class BotController {
    
	@PostMapping(value= "/captcha", produces = MediaType.APPLICATION_JSON)
  @ResponseBody
	//@ApiOperation(tags= "Bot Controller", value = "Bot Controller")
    @io.swagger.v3.oas.annotations.Operation(summary = "${testendpoint.summary}", description="${testendpoint.description}",

            responses = {
                   /* @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Tester1.class))
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
                    })*/
            }
    )

    public void yourFormPostAction(HttpServletRequest request,HttpServletResponse response) throws IOException {

	  PrintWriter out = response.getWriter();
      Gson gson = new Gson();
    JsonParser parser = new JsonParser();
    JsonObject formDataObj = (JsonObject) parser.parse(request.getReader());

    String userEnteredCaptchaCode = formDataObj.get("userEnteredCaptchaCode").getAsString();
    String captchaId = formDataObj.get("captchaId").getAsString();

    // create a captcha instance to be used for the captcha validation
    SimpleCaptcha yourFirstCaptcha = null;//SimpleCaptcha.load(request);
    // execute the captcha validation
    boolean isHuman = yourFirstCaptcha.validate(userEnteredCaptchaCode, captchaId);

    BasicValidationResult validationResult = new BasicValidationResult();
    
    if (isHuman == false) {
        // captcha validation failed
        validationResult.setSuccess(false);
        // TODO: consider logging the attempt
    } else {
        // captcha validation succeeded
        validationResult.setSuccess(true);
    }
    
    try {
        // return the json string with the validation result to the frontend
        out.write(gson.toJson(validationResult));
    } catch(Exception ex) {
        out.write(ex.getMessage());
    } finally {
        out.close();
    }
}

class BasicValidationResult {

    private boolean success;
    
	public BasicValidationResult() {
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
}
