package com.restapi.oauth.services;


import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.btsl.util.Constants;

import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
public class NonceInterceptor /*implements HandlerInterceptor */{
    private static final String NONCE = "Nonce";
    private static final String REQUEST = "REQUEST";
    private static final Logger LOGGER = LoggerFactory.getLogger(NonceInterceptor.class);

    @Autowired
    private NonceValidatorService nonceValidatorService;
/*    @Autowired
    private MFSConfiguration configuration;*/

   // @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    	
        if (RequestMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod())) {
        	return true;
        }
        	
        if (!request.getDispatcherType().name().equals(REQUEST)) {
            return true;
        }
        
        if(request.getRequestURI() != null && (request.getRequestURI().contains("regex") || request.getRequestURI().contains("isPinChangeOnTxRequired") || request.getRequestURI().contains("login") || request.getRequestURI().contains("getLocaleList") || request.getRequestURI().contains("getSublookupsCache"))) {
        	return true;
        }
        if(skipSecurityCheck(request) || !isCheckSumValidationEnabled()){
            return true;
        }
        try{
           String nonce = ofNullable(request.getHeader(NONCE)).
                          filter(StringUtils::isNotEmpty).
                          filter(nonceValue -> nonceValue.matches("[^[a-zA-Z0-9]+$]{16}")).
                          orElseThrow(() -> new RuntimeException("Invalid Nonce"));
           LOGGER.debug("nonce value = {}",nonce);
           nonceValidatorService.validateNonce(nonce);
        }catch (Exception e){
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            LOGGER.error("Error occurred while validating nonce",e);
            return false;
        }
        return true;
    }

    private boolean skipSecurityCheck(HttpServletRequest request) {
        String skipSecurityHeaderName = Constants.getProperty("skip.security.header.validation.tag");
        String skipSecurityHeaderValue = request.getHeader(skipSecurityHeaderName);
        LOGGER.debug("skipSecurityHeaderName = {},skipSecurityHeaderValue = {}",skipSecurityHeaderName,skipSecurityHeaderValue);
        return Boolean.parseBoolean(skipSecurityHeaderValue);
    }

    private boolean isCheckSumValidationEnabled(){
        return Boolean.parseBoolean(Constants.getProperty("checksum.validate.enable"));
    }
}

