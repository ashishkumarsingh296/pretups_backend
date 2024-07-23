package com.btsl.security;

import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;


public class RequestDecryptionWrapper extends HttpServletRequestWrapper {

    public RequestDecryptionWrapper(HttpServletRequest request){
        super(request);
    }

    @Override
    public String getRequestURI() {
        String contextPath = "/pretups/rstapi/";
        boolean encryptURI = false;
        if(!(Constants.getProperty("encryptURI") == null) && !BTSLUtil.isNullString(Constants.getProperty("encryptURI"))){
            encryptURI = Boolean.parseBoolean(Constants.getProperty("encryptURI"));
        }
        try {
            String originalUri = super.getRequestURI(); // Get the original URI
            if(!encryptURI || !originalUri.contains(contextPath)){
                return originalUri;
            }
            super.getContextPath();
            String requestUri = originalUri.substring(contextPath.length());
            String decryptedUri = null;
            try{
                String decodedURI = requestUri;
                decryptedUri = AESEncryptionUtil.aesDecryptor(decodedURI, Constants.A_KEY);
            }catch(Exception e) {
                decryptedUri = requestUri;
            }
            decryptedUri = contextPath+decryptedUri;
            return decryptedUri;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
