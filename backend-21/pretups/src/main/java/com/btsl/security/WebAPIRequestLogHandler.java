package com.btsl.security;

import com.btsl.pretups.logging.WebAPIRequestLog;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.JWebTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.CoyoteOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.buf.ByteChunk;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class WebAPIRequestLogHandler {
    private static AtomicInteger requestID=new AtomicInteger();



    public boolean preHandleStr(HttpServletRequest request,HttpServletResponse response,String body ) {
        try{
            String requestIDStr=String.valueOf(requestID.incrementAndGet());
            String header=getHeaderString((HttpServletRequest)request);


            String IP="";
            if(BTSLUtil.isNullString(request.getHeader("X-Forwarded-For"))){
                IP=request.getRemoteAddr();
            }
            else
                IP=request.getHeader("X-Forwarded-For");

            if(BTSLUtil.isNullString(Constants.getProperty("API_REQUEST_BODY_LOGGED")) || "N".equalsIgnoreCase(Constants.getProperty("API_REQUEST_BODY_LOGGED"))){
                body="";
            }else{
                if(body==null)
                {
                    body = intercept((HttpServletRequest) request);
                }
            }



            WebAPIRequestLogVO customRequestResponseFilter=new WebAPIRequestLogVO("ReqIN",((HttpServletRequest)request).getMethod(), request.getRequestURI(),  header,  body, requestIDStr,  IP,System.currentTimeMillis());
            String apiNotIN="";
            if(!BTSLUtil.isNullString(Constants.getProperty("API_REQUEST_NOT_LOGGED"))){
                apiNotIN=Constants.getProperty("API_REQUEST_NOT_LOGGED");
            }
            if(!BTSLUtil.isStringIn(request.getRequestURI(), apiNotIN))
            {
                WebAPIRequestLog.log(customRequestResponseFilter.toString());
            }
            request.setAttribute("RequestID", requestIDStr);
            request.setAttribute("RequestINTIME", System.currentTimeMillis());

        }catch(Exception e)
        {
            WebAPIRequestLog.log(e.getMessage());
            return true;
        }
        return true;
    }


    String extractPostRequestBody(HttpServletRequest request) {
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



    public void postHandle(HttpServletRequest request,String responsen) throws Exception {

        String header=getHeaderString(request);

        String reqNo=(String)((HttpServletRequest)request).getAttribute("RequestID");
        String IP="";
        String bypassResponseEncryption = Constants.getProperty("BYPASS_RESPONSE_ENCRYPTION");
        if(bypassResponseEncryption != null ){
            String[] bypassResponseEncryptionArray = bypassResponseEncryption.split(",");
            for(String bypassResponseEncryptionValue : bypassResponseEncryptionArray){
                if(!request.getRequestURI().contains(bypassResponseEncryptionValue)){
                    responsen = AESEncryptionUtil.aesDecryptor(responsen.split(":")[1].replaceAll("\"",""),Constants.A_KEY);
                }
            }
        }
        long INTime=(Long)((HttpServletRequest)request).getAttribute("RequestINTIME");

        long outTime=System.currentTimeMillis();

        long totalTime=outTime-INTime;
        try{
            if(BTSLUtil.isNullString(request.getHeader("X-Forwarded-For"))){
                IP=request.getRemoteAddr();
            }
            else
                IP=request.getHeader("X-Forwarded-For");


            //String responsen=getResponseContent(response);

            if(BTSLUtil.isNullString(Constants.getProperty("API_REQUEST_BODY_LOGGED")) || "N".equalsIgnoreCase(Constants.getProperty("API_REQUEST_BODY_LOGGED"))){
                responsen="";
            }
            WebAPIRequestLogVO webAPIRequestLogVO=new WebAPIRequestLogVO("ReqOut",((HttpServletRequest)request).getMethod(), request.getRequestURI(),  header,  responsen, reqNo,  IP, totalTime);
            String apiNotIN="";
            if(!BTSLUtil.isNullString(Constants.getProperty("API_REQUEST_NOT_LOGGED"))){
                apiNotIN=Constants.getProperty("API_REQUEST_NOT_LOGGED");
            }
            if(!BTSLUtil.isStringIn(request.getRequestURI(), apiNotIN))
            {
                WebAPIRequestLog.log(webAPIRequestLogVO.toString());
            }


        }catch(Exception  e)
        {
            try{
                WebAPIRequestLogVO customRequestResponseFilter=new WebAPIRequestLogVO("ReqOut",((HttpServletRequest)request).getMethod(), request.getRequestURI(),  header,null, reqNo,  IP, totalTime);
                String apiNotIN="";
                if(!BTSLUtil.isNullString(Constants.getProperty("API_REQUEST_NOT_LOGGED"))){
                    apiNotIN=Constants.getProperty("API_REQUEST_NOT_LOGGED");
                }
                if(!BTSLUtil.isStringIn(request.getRequestURI(), apiNotIN))
                {
                    WebAPIRequestLog.log(customRequestResponseFilter.toString());
                }

            }catch(Exception e1)
            {
                WebAPIRequestLog.log(e1.getMessage());
            }
        }

    }

    public String getHeaderString(HttpServletRequest request){

        StringBuffer sb=new StringBuffer();
        try{
            String headerIN="";
            if(!BTSLUtil.isNullString(Constants.getProperty("API_HEADER_PRINT"))){
                headerIN=Constants.getProperty("API_HEADER_PRINT");
            }


            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if(BTSLUtil.isStringIn(headerName, headerIN)){
                    if(headerName.equalsIgnoreCase("authorization"))
                    {
                        String token = null;
                        String headerValue = request.getHeader(headerName);
                        token = headerValue;
                        if(token != null && token.contains("Bearer")) {
                            token = token.substring(token.indexOf("Bearer")+6).trim();
                        }
                        String tokenVaue= JWebTokenUtil.retrieveTokenId(token);
                        sb.append("token");
                        sb.append("=" + tokenVaue);
                        sb.append(", ");
                    }
                    else{
                        String headerValue = request.getHeader(headerName);
                        sb.append(headerName);

                        sb.append("=" + headerValue);
                        sb.append(", ");

                    }

                }
            }

        }catch(Exception e){
            return "";
        }
        return sb.toString();
    }

    /**
     * get Response return json content
     *
     * @param response
     * @return
     * @throws IOException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public String getResponseContent(HttpServletResponse response) throws IOException, NoSuchFieldException, IllegalAccessException {
        String responseContent = null;

        CoyoteOutputStream outputStream = (CoyoteOutputStream) response.getOutputStream();
        Class<CoyoteOutputStream> coyoteOutputStreamClass = CoyoteOutputStream.class;
        Field obField = coyoteOutputStreamClass.getDeclaredField("ob");
        if (obField.getType().toString().endsWith("OutputBuffer")) {
            obField.setAccessible(true);
            org.apache.catalina.connector.OutputBuffer outputBuffer = (org.apache.catalina.connector.OutputBuffer) obField.get(outputStream);
            Class<org.apache.catalina.connector.OutputBuffer> opb = org.apache.catalina.connector.OutputBuffer.class;
            Field outputChunkField = opb.getDeclaredField("outputChunk");
            outputChunkField.setAccessible(true);
            if (outputChunkField.getType().toString().endsWith("ByteChunk")) {
                ByteChunk bc = (ByteChunk) outputChunkField.get(outputBuffer);
                Integer length = bc.getLength();
                if (length == 0) return null;
                responseContent = new String(bc.getBytes(), "UTF-8");
                Integer responseLength = StringUtils.isBlank(responseContent) ? 0 : responseContent.length();
                if (responseLength < length) {
                    responseContent = responseContent.substring(0, responseLength);
                } else {
                    responseContent = responseContent.substring(0, length);
                }

            }
        }
        return responseContent;
    }

    private String intercept(HttpServletRequest httpRequest) throws IOException {
        String bodyInStringFormat="";

        bodyInStringFormat=httpRequest.getQueryString();

        return bodyInStringFormat;
    }


    private String readInputStreamInStringFormat(InputStream stream, Charset charset) throws IOException {
        final int MAX_BODY_SIZE = 1024;
        final StringBuilder bodyStringBuilder = new StringBuilder();
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }

        stream.mark(MAX_BODY_SIZE + 1);
        final byte[] entity = new byte[MAX_BODY_SIZE + 1];
        final int bytesRead = stream.read(entity);

        if (bytesRead != -1) {
            bodyStringBuilder.append(new String(entity, 0, Math.min(bytesRead, MAX_BODY_SIZE), charset));
            if (bytesRead > MAX_BODY_SIZE) {
                bodyStringBuilder.append("...");
            }
        }
        stream.reset();

        return bodyStringBuilder.toString();
    }
}
