package com.restapi.oauth.services;


import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.btsl.util.Constants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Optional.*;


@Component
public class CustomHandlerInterceptor /*extends HandlerInterceptorAdapter*/ {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomHandlerInterceptor.class);
    private static final String SKIP_SECURITY_HEADER_VALIDATION_TAG = "skip.security.header.validation.tag";
    private static final String GATEWAY_APPENDED_PARAMS_PRESENT = "gateWayAppended";

   /* @Autowired
    private MFSConfiguration configuration;*/

    public static final String CREDENTIALS_NAME = "Access-Control-Allow-Credentials";
    public static final String ORIGIN_NAME = "Access-Control-Allow-Origin";
    public static final String METHODS_NAME = "Access-Control-Allow-Methods";
    public static final String HEADERS_NAME = "Access-Control-Allow-Headers";
    public static final String MAX_AGE_NAME = "Access-Control-Max-Age";
    
    
    //@Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {
        LOGGER.debug("Inside Interceptor preHandle for signature validation.");
        
        /*response.setHeader(CREDENTIALS_NAME, "true");
        response.setHeader(ORIGIN_NAME, "http://localhost:4200");
        response.setHeader(METHODS_NAME, "GET, OPTIONS, POST, PUT, DELETE");
        response.setHeader(HEADERS_NAME, "Origin, X-Requested-With, Content-Type, Accept");
        response.setHeader(MAX_AGE_NAME, "3600");
		*/
		
        if (RequestMethod.DELETE.toString().equalsIgnoreCase(request.getMethod())   ||  RequestMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod()))
            return true;

        if (!request.getDispatcherType().name().equals("REQUEST")) {
            return true;
        }
        
        if(request.getRequestURI() != null && (request.getRequestURI().contains("regex") || request.getRequestURI().contains("isPinChangeOnTxRequired") || request.getRequestURI().contains("login") || request.getRequestURI().contains("getLocaleList") || request.getRequestURI().contains("getSublookupsCache"))) {
        	return true;
        }
        
        try {
            final String signature = request.getHeader("Signature");
            final String signature2 = request.getHeader("Signature2");
            final String tokenKey = request.getHeader("Authorization");
            final String nonceKey = request.getHeader("Nonce");
            boolean skipSecurityHeaderValidate= isSkipSecurityHeaderValidation(request.getHeader(Constants.getProperty(SKIP_SECURITY_HEADER_VALIDATION_TAG)));

            Boolean  checksumValidationEnable = Boolean.parseBoolean(Constants.getProperty("checksum.validate.enable"));
            
            LOGGER.debug("checksum hash validation enabled : {}",checksumValidationEnable);

            if(checksumValidationEnable && !StringUtils.isBlank(tokenKey)){
               if  (!skipSecurityHeaderValidate && (StringUtils.isBlank(nonceKey) || StringUtils.isBlank(signature))) {
                    response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                    return false;
                }
            }

            if (!StringUtils.isBlank(tokenKey) && !StringUtils.isBlank(signature)) {
                LOGGER.debug("Start validating checksum hash");
                String[] tokenArr = tokenKey.split(" ");
                if (tokenArr.length != 2) {
                    response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                    return false;
                }

                String KEY = tokenArr[1];
                // Nonce will be part of SALT in future
                Optional<String> nonce = ofNullable(request.getHeader("Nonce"));
                if(nonce.isPresent()){
                    // Regex to be shared by UI team
                    if(nonce.filter(nonceValue -> nonceValue.matches("[^[a-zA-Z0-9]+$]{16}")).isPresent()){
                        KEY = format("%s.%s",tokenArr[1],nonce.get());
                    }else {
                        response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                        return false;
                    }
                }
                final Optional<String> encodedBody = resolveEncodedBody(request,KEY);
                LOGGER.debug("check-sum hash = {},url = {},query parameters = {}", encodedBody,request.getRequestURI(),request.getQueryString());
                if(!encodedBody.isPresent()){
                    return true;
                }
                if (encodedBody.filter(eb -> eb.equalsIgnoreCase(signature)).isPresent())
                    return true;
                else {
                    response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Internal Server Error");
                    return false;
                }
            } else
                return true;
        } catch (Exception ioex) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
    }

    private Optional<String> resolveEncodedBody(final HttpServletRequest request, final String KEY) throws IOException {
        final HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, KEY);
        
//        final HmacUtils hmacUtils = new HmacUtils(KEY);
        
        
        if(isGetRequest(request)){
            final String queryParams = request.getQueryString();
            if(StringUtils.isEmpty(queryParams)){
                LOGGER.debug("no query parameters for request = {}",request.getRequestURI());
                return empty();
            }
            final String params = request.getHeader("Params");
            boolean isGatewayAppended = isGatewayAppendedParamsPresent(request);
            LOGGER.debug("params = {}",params);
            if(StringUtils.isEmpty(params) && !isGatewayAppended){
                LOGGER.debug("Params header is missing which is mandatory when query parameters are present");
               // return of("NO_PARAMS");
                return empty();
                
            }
            if(StringUtils.isEmpty(params) && isGatewayAppended){
                LOGGER.debug("no query parameters from channels = {}",request.getRequestURI());
                return empty();
            }

            return of(hmacUtils.hmacHex(decodeQueryParameters(params)));
        }else{
            String jsonBody = CustomHandlerInterceptor.extractPostRequestBody(request);//request.getReader().lines().collect(Collectors.joining());
           // request.getInputStream().
            LOGGER.debug("Generating server-side checksum hash");
            return of(hmacUtils.hmacHex(jsonBody));
          // return of(hmacUtils.hmac(jsonBody).toString());
        }
    }

    static String extractPostRequestBody(HttpServletRequest request) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            Scanner s = null;
            try {
                s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s.hasNext() ? s.next() : "";
        }
        return "";
    }
    
    
    private String decodeQueryParameters(String params) {
        byte[] paramsDecoded = Base64.getDecoder().decode(params);
        return new String(paramsDecoded, StandardCharsets.UTF_8);
    }

    private boolean isGetRequest(HttpServletRequest request) {
        return "GET".equalsIgnoreCase(request.getMethod());
    }

    private boolean isSkipSecurityHeaderValidation(String skipSecurityHeaderValue){
        LOGGER.debug("skip security Header validation value : {}",skipSecurityHeaderValue);
        return skipSecurityHeaderValue == null ? false : Boolean.valueOf(skipSecurityHeaderValue);
    }

    private boolean isGatewayAppendedParamsPresent(HttpServletRequest request){
        if(!StringUtils.isEmpty(request.getQueryString()) && "true".equals(request.getParameter(GATEWAY_APPENDED_PARAMS_PRESENT))) {
            LOGGER.debug("Query params are added from gateway!");
            return true;
        }
        else
            return false;
    }
}
