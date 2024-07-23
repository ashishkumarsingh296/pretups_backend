package com.btsl.security;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.io.BufferedInputStream;



/**
 * @(#)EncodingFilter.java
 *                         Copyright(c) 2010, Comviva Technologies Limited
 *                         All Rights Reserved
 *                         Action class for interaction between front end and
 *                         backend
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Sanjeev Sharma 09/02/2010 Initial Creation
 *                         ----------------------------------------------------
 *                         --------------------------------------------
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.OperatorUtilI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SecurityConstants;
import com.restapi.oauth.services.CustomHandlerInterceptor;
import com.restapi.oauth.services.NonceValidatorService;

/**
 * Servlet 2.3/2.4 Filter that allows one to specify a character encoding for
 * requests. This is useful because current browsers typically do not set a
 * character encoding even if specified in the HTML page or form.
 *
 * <p>
 * This filter can either apply its encoding if the request does not already
 * specify an encoding, or enforce this filter's encoding in any case
 * ("forceEncoding"="true"). In the latter case, the encoding will also be
 * applied as default response encoding on Servlet 2.4+ containers (although
 * this will usually be overridden by a full content type set in the view).
 *
 * @see #setEncoding
 * @see #setForceEncoding
 * @see jakarta.servlet.http.HttpServletRequest#setCharacterEncoding
 * @see jakarta.servlet.http.HttpServletResponse#setCharacterEncoding
 */
public class EncodingFilter extends HttpServlet implements Filter {

	/**
	 * Commons Logging instance.
	 */
	private Log _log = LogFactory.getLog(this.getClass().getName());

	private static final long serialVersionUID = 1L;
	private FilterConfig filterConfig;
	private static boolean doReqEncoding = true;
	private static boolean doResEncoding = true;
	private static boolean no_init = true;
	private static final String SKIP_SECURITY_HEADER_VALIDATION_TAG = "skip.security.header.validation.tag";
	private static final String GATEWAY_APPENDED_PARAMS_PRESENT = "gateWayAppended";

   /* @Autowired
    private NonceValidatorService nonceValidatorService;
   */

	/**
	 * Set the encoding to use for requests. This encoding will be passed
	 * into a <code>ServletRequest.setCharacterEncoding</code> call.
	 * <p>
	 * Whether this encoding will override existing request encodings depends on
	 * the "forceEncoding" flag.
	 *
	 * @see #setForceEncoding
	 * @see jakarta.servlet.ServletRequest#setCharacterEncoding
	 */
	private String encoding;

	/**
	 * Set whether the encoding of this filter should override existing
	 * request encodings and whether it will be applied as default response
	 * encoding as well. Default is "false", i.e. do not modify encoding if
	 * <code>ServletRequest.getCharacterEncoding</code> returns a non-null
	 * value.
	 *
	 * @see #setEncoding
	 * @see jakarta.servlet.ServletRequest#getCharacterEncoding
	 */
	private String forceEncoding;

	public EncodingFilter() {
		encoding = null;
		no_init = false;

	}

	public void init(FilterConfig config) {
		if (_log.isDebugEnabled())
			_log.debug("init", "Entered in init to read filter configuration");

		this.filterConfig = config;
		encoding = filterConfig.getInitParameter("encoding");
		forceEncoding = filterConfig.getInitParameter("forceEncoding");
		if (this.encoding == null || this.encoding.length() == 0)
			doReqEncoding = false;
		if (this.forceEncoding == null || this.forceEncoding.length() == 0)
			doResEncoding = false;

		if (_log.isDebugEnabled())
			_log.debug("init", "Exiting init after reading filter configuration with encoding :: " + encoding + ", forceEncoding :: " + forceEncoding);
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		request = new RequestDecryptionWrapper((HttpServletRequest) request);

		/*
		 * Default value of forceEncoding is "false", i.e. do not modify the
		 * encoding
		 * if jakarta.servlet.http.HttpServletRequest#getCharacterEncoding()
		 * returns a non-null value. Switch this to "true" to enforce the
		 * specified
		 * encoding in any case, applying it as default response encoding as
		 * well.
		 */
		final String METHOD_NAME = "doFilter";

		final String RESPONSE_ENCODING = Optional.ofNullable(com.btsl.util.Constants.getProperty("RESPONSE_ENCODING_CHAR_SET"))
				.orElse(StandardCharsets.UTF_8.name());
		String[] acceptedServletUrls = {
				"swagger",
				"api-docs",
				"/rest/",
				"/rstapi",
				"/imageCaptchaServlet",
				"/PaymentGatewayTestServer",
				"/ReportScheduling",
				"/ReportSchedulingDaily",
				"/DownloadTemplateUtil",
				"/OPTReceiver",
				"/Captcha.jpg",
				"/ReportServlet",
				"/ComverseTestServer",
				"/VASTestServer",
				"/SOSTestServer",
				"/CS5ClaroTestServer2",
				"/CS5ClaroTestServer1",
				"/VomsReciever",
				"/SystemReceiver",
				"/VomsTestServer",
				"/commonAction",
				"/P2PReceiver",
				"/C2SReceiver",
				"/ExtGWChannelReceiver",
				"/C2SSubscriberReceiver",
				"/FermaTestServer",
				"/FermaConnectionServlet",
				"/PostPaidTestServlet",
				"/InterfaceCloserServlet",
				"/NodeServlet",
				"/downloadUtil",
				"/UpdateCacheServlet",
				"/UpdateRedisCacheServlet",
				"/InstanceUpdateCacheServlet",
				"/PushMessage",
				"/simple-captcha-endpoint",
				"/captchaBasic",
				"/CS3NigeriaTestServer1",
				"/CS3NigeriaTestServer2",
				"/CS3NigeriaTestServer3",
				"/reportserver",
				"/CS5MobinilTestServer1",
				"/CS5MobinilTestServer2",
				"/CS5MobinilTestServer3",
				"/CS5MobinilTestServer4",
				"/CS5MobinilTestServer5",
				"/CS5MobinilTestServer6",
				"/MobinilVOMSTestServlet",
				"/rest"


		};

		try {
			boolean checkSession = false;
			if (doReqEncoding && (doResEncoding || request.getCharacterEncoding() == null)) {
				request.setCharacterEncoding(this.encoding);
				if ("true".equalsIgnoreCase(this.forceEncoding))
					response.setCharacterEncoding(this.encoding);
				for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
					String temp = (String) e.nextElement();
					if (temp.contains("prompt")) {
						checkSession = true;
						String tempvalue = request.getParameter(temp);
						if ((tempvalue.toLowerCase()).contains("select") || (tempvalue.toLowerCase()).contains("from") || (tempvalue.toLowerCase()).contains("delete") || (tempvalue.toLowerCase()).contains("alter") || (tempvalue.toLowerCase()).contains("where") || (tempvalue.toLowerCase()).contains("commit")) {

							response.reset();
							return;
						}
					}
				}
				if (checkSession) {
					String secureSessionId = request.getParameter("securebrowsersession");
					String currentSessionId = request.getParameter("requestsession");
					if ("SHA".equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
						if (secureSessionId.contains(" "))
							secureSessionId = secureSessionId.replaceAll(" ", "+");
					}
					if (secureSessionId == null) {

						response.reset();
						return;
					}
					if (currentSessionId == null) {

						response.reset();
						return;
					}
					if (!BTSLUtil.encryptText(currentSessionId).equalsIgnoreCase(secureSessionId)) {

						response.reset();
						return;
					}
					String reportPath = request.getParameter("report");
					if ((reportPath == null) || reportPath.contains("http") || reportPath.contains("https") || reportPath.contains("www") || !(reportPath.contains("WEB-INF"))) {
						response.reset();
						return;
					}
				}
			}
		} catch (UnsupportedEncodingException unsupex) {
			if (_log.isDebugEnabled())
				_log.debug("doFilter", "Got exception with message ::" + unsupex.getMessage());
			_log.errorTrace(METHOD_NAME, unsupex);
		}

		/** SecurityConstant Validation started
		 *  If any request parameter is defined in SecurityConstant.props,
		 *  And if it fails system 'll redirect
		 *  to xss.jsp error page**/
		HttpServletRequest request1 = (HttpServletRequest) request;
		@SuppressWarnings("unchecked")
		List<String> requestParameterNames = Collections.list((Enumeration<String>)request1.getParameterNames());
		Pattern commonValidationPattern = null;
		if (!BTSLUtil.isNullString(Constants.getProperty("IS_SECURITY_COMMON_VALIDATION_REQUIRED")) && PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_SECURITY_COMMON_VALIDATION_REQUIRED"))){
			commonValidationPattern = Pattern.compile(SecurityConstants.getProperty("VALIDPATTERN"));
		}

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String uri = req.getRequestURI();

		/* Check each parameters of request for security*/
//		if (!BTSLUtil.isNullString(uri) && !uri.contains("rstapi")) {

		for (String parameterName : requestParameterNames) {
			String value = request1.getParameter(parameterName);
			Pattern pattern = null;
			// if request parameter available in SecurityConstants, then validate the
			// request parameter
			if (null != SecurityConstants.getProperty(parameterName)) {
				pattern = Pattern.compile(SecurityConstants.getProperty(parameterName));
			}
			// if request parameter not available in SecurityConstants, but common
			// validation is required, then also validate the request parameter
			else if (null != commonValidationPattern) {
				pattern = commonValidationPattern;
			}
			if (pattern != null && !(isValid(pattern, value))) {
				// if request parameter value not lies in the pattern, redirect to common xss
				// error page
				HttpSession session = request1.getSession(false);
				if (session != null) {
					Object obj = session.getAttribute("user");
					UserVO userVO = (UserVO) obj;
					String userName = userVO.getUserName();
					String userIP = userVO.getRemoteAddress();
					_log.info(METHOD_NAME, "userIP:" + userIP + " userName: " + userName);
					_log.debug(METHOD_NAME, "Request parameters::" + parameterName + " Value " + value
							+ " whitelist value " + SecurityConstants.getProperty(parameterName));
					request.getRequestDispatcher("/xss.do?method=inValidCharacter&parameterName=" + parameterName)
							.forward((ServletRequest) request, response);
					return;
				}
			}
		}
//		}

		List<String> acceptedServletUrlsList = Arrays.asList(acceptedServletUrls);
		boolean containsUrls = false;


		for(String url: acceptedServletUrlsList) {
			if(uri.contains(url) == true) {
				containsUrls = true;
			} else {
				if (_log.isDebugEnabled()) {
					//_log.debug("doFilter", "The serverlet URL = "+acceptedServletUrlsList+" is not accepted.");
				}
			}
		}

		boolean useSwagger = Boolean.parseBoolean(Constants.getProperty("USESWAGGER"));
		if(!useSwagger && uri != null && (uri.toLowerCase().contains("swagger") || uri.toLowerCase().contains("api-docs"))){
			return;
		}



		if(uri != null && ( uri.equalsIgnoreCase("/pretups") || uri.equalsIgnoreCase("/pretups/") )) {
			request.getRequestDispatcher("/jsp/login/index.jsp").forward(request, response);
		}

		else if(uri != null && ( containsUrls == false) && !uri.contains(".") ) {
			if(uri.contains(";")) {
				request.getRequestDispatcher(uri.substring(uri.indexOf("pretups/")+7, uri.indexOf(";"))+".do"+uri.substring(uri.indexOf(";"))).forward(request, response);//Struts request , explicitly appended .do
			}else {
				request.getRequestDispatcher(uri.substring(uri.indexOf("pretups/")+7)+".do").forward(request, response);//Struts request , explicitly appended .do
			}
		}

		else {
			/*if(uri != null && uri.contains("rstapi") ) {
			    if((uri.contains("regex") || uri.contains("isPinChangeOnTxRequired") || uri.contains("login") || uri.contains("getLocaleList") || uri.contains("getSublookupsCache"))) {
			    	filterChain.doFilter(new RequestWrapper((HttpServletRequest) request, response), response);
			    }else {

			    }
			}else {*/

			//making response default encoding to UTF_8
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			String urlTocheck = null;
			String urlToByPass = com.btsl.util.Constants.getProperty("BYPASS_URLS");
			String uriTo = uri.replaceAll("\\?", "");
			if(uriTo.contains("/")){ //   /pretups/abc   /pretups/abc/ abc abc/ abc?d=g&
				if(uriTo.endsWith("/")){
					urlTocheck = uriTo.substring(0, uri.lastIndexOf("/")); // /pretups/abc abc
					if(urlTocheck.contains("/")){
						urlTocheck = urlTocheck.substring(urlTocheck.lastIndexOf("/")+1);
					}
				}else{
					urlTocheck = uriTo.substring(uriTo.lastIndexOf("/")+1);
				}
			}else{
				urlTocheck = uriTo;
			}

			HttpServletRequest req1 = (HttpServletRequest) request;
			HttpServletResponse res1 = (HttpServletResponse) response;


			String refererHeaderSkipSwagger = req1.getHeader("referer");

			String[] urlToByPassArray = urlToByPass.split(",");
			boolean isURLMatch = false;
			for (String url : urlToByPassArray) {
				if (url.equalsIgnoreCase(urlTocheck)) {
					isURLMatch = true;
					break;
				}
			}
			//if(uri != null && (!uri.contains("rstapi") || uri.contains("generateTokenAPI") || uri.contains("logout") || urlToByPass.contains(urlTocheck)) ) {
			if(uri != null && ((!uri.contains("rstapi") && !( uri.contains("C2SReceiver") && req1.getQueryString().contains("MAPPGW"))) || isURLMatch || (!BTSLUtil.isNullString(refererHeaderSkipSwagger) && refererHeaderSkipSwagger.contains("swagger-ui")) ) ) {filterChain.doFilter(new RequestWrapper((HttpServletRequest) request, response), response);
			}else {
				/** SecurityConstant Validation ended**/



				try {

					boolean validNonce = false;
					boolean valid = false;
					CustomRequestWrapper   requestWrapper = new CustomRequestWrapper((HttpServletRequest) request);
					ServletRequest   requestWrapper2 = new CustomRequestWrapper((HttpServletRequest) requestWrapper);
					HttpServletRequest requestWrapperH = (HttpServletRequest) requestWrapper;
					String nonce = null;
					String encryptedNonce = requestWrapperH.getHeader("Nonce");
					String decryptedNonce = null;
					String encryptNonce = Constants.getProperty("EncryptNonce");
					if(encryptNonce == null){
						encryptNonce = "N";
					}
					if((req1.getQueryString() != null && req1.getQueryString().contains("MAPPGW")) || encryptNonce.equalsIgnoreCase("N")){
						decryptedNonce = encryptedNonce;
					}else if(encryptedNonce != null){
						decryptedNonce = AESEncryptionUtil.aesDecryptor(encryptedNonce, Constants.A_KEY);
						if(!decryptedNonce.equals(Constants.getProperty("SECRET_NONCE_INTERNAL_API_CALL")) && decryptedNonce.equals(encryptedNonce)){
							decryptedNonce = null;
						}else{
							//nothing to do
						}
					}


					try {
//				if(requestWrapperH.getHeader("Nonce") != null && requestWrapperH.getHeader("Nonce").equalsIgnoreCase(Constants.getProperty("SECRET_NONCE_INTERNAL_API_CALL"))) {
						if(decryptedNonce != null && decryptedNonce.equalsIgnoreCase(Constants.getProperty("SECRET_NONCE_INTERNAL_API_CALL"))) {
							valid = true;
						}else {

							valid = preHandle(requestWrapperH.getRequestURI() + "", requestWrapperH.getMethod() + "",
									requestWrapperH.getDispatcherType() + "", requestWrapperH, res1,
									requestWrapper.getBody(), requestWrapper.getQueryString(),decryptedNonce);
							valid = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}



					try {
						if(decryptedNonce != null && decryptedNonce.equalsIgnoreCase(Constants.getProperty("SECRET_NONCE_INTERNAL_API_CALL"))) {
							validNonce = true;


						}else {
							if(valid == true) {
//							nonce = requestWrapperH.getHeader("Nonce") ;
								nonce = decryptedNonce;
								validNonce = preHandleNonce(requestWrapperH, requestWrapperH.getMethod() + "", res1, req1.getQueryString() , nonce);
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}




					if(valid && validNonce || true) {

						CustomResponseWrapper capturingResponseWrapper = new CustomResponseWrapper((HttpServletResponse) response , requestWrapperH);


						try {
							filterChain.doFilter(new RequestWrapper((HttpServletRequest) requestWrapper2, response), capturingResponseWrapper);
						}catch(Exception e) {

							e.printStackTrace();
							String content = capturingResponseWrapper.getCaptureAsString();
							String newContent = content;
							//response.setContentLength(newContent .length());
							response.getOutputStream().write(newContent.getBytes(RESPONSE_ENCODING));
							System.out.println(e.getMessage());
						}

						String addSecHeader = Constants.getProperty("ADD_SECURITY_HEADERS") ;


						//System.out.println("1"+addSecHeader);
						if(addSecHeader != null && addSecHeader.equalsIgnoreCase("Y") || true) {

							capturingResponseWrapper.addHeader("X-FRAME-OPTIONS", "SAMEORIGIN");
							capturingResponseWrapper.addHeader("Content-Security-Policy", "frame-ancestors 'self'");

							capturingResponseWrapper.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
							capturingResponseWrapper.setHeader("Pragma", "no-cache");
							capturingResponseWrapper.setHeader("Expires", "0");

							capturingResponseWrapper.addHeader("X-XSS-Protection", "1; mode=block");
							capturingResponseWrapper.addHeader("X-Content-Type-Options", "nosniff");



						}

						//System.out.println("2");
						//if(capturingResponseWrapper.getStatus() == 200 &&  !urlToByPass.contains(urlTocheck) &&  !req1.getQueryString().contains("MAPPGW")  && (requestWrapperH.getHeader("Nonce") == null || !requestWrapperH.getHeader("Nonce").equalsIgnoreCase(Constants.getProperty("SECRET_NONCE_INTERNAL_API_CALL"))) ) {
						if(capturingResponseWrapper.getStatus() == 200 &&  !urlToByPass.contains(urlTocheck) &&
								(  req1.getQueryString() == null || !req1.getQueryString().contains("MAPPGW") )
								&& (decryptedNonce == null ||
								!decryptedNonce.equalsIgnoreCase(Constants.getProperty("SECRET_NONCE_INTERNAL_API_CALL"))) ) {


							//System.out.println("3 nonce"+nonce);
							String content = capturingResponseWrapper.getCaptureAsString();

							String KEY="PreTUPS_Key_HMAC_SHA_256";
							final HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, KEY);




							//String newContent = "{ \"signature\" : \""+hmacUtils.hmacHex("\""+content.replaceAll("\\.0", "").replaceAll("'", "quote").replaceAll("\"", "'")+"\"")+"\", \"response\": \""+content.replaceAll("'", "quote").replaceAll("\"", "'")+"\" }";
							String newContent = content;
							//response.setContentLength(newContent.length());
							if(nonce != null) {

								//System.out.println("4");
								final HmacUtils hmacUtilsNonce = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, nonce);

								//System.out.println("Adding checksum header ..........");
								((HttpServletResponse) response).addHeader("access-control-expose-headers", "statusChecksum");
								((HttpServletResponse) response).addHeader("statusChecksum", hmacUtilsNonce.hmacHex(capturingResponseWrapper.getStatus()+""));
							}
							response.getOutputStream().write(newContent.getBytes(RESPONSE_ENCODING));
						}else {


							//System.out.println("5");
							String content = capturingResponseWrapper.getCaptureAsString();
							String newContent = content;
							//response.setContentLength(newContent .length());
							if(nonce != null) {
								final HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, nonce);

								//System.out.println("6");

								//System.out.println("Adding checksum header ..........111111111111");

								((HttpServletResponse) response).addHeader("access-control-expose-headers", "statusChecksum");
								((HttpServletResponse) response).addHeader("statusChecksum", hmacUtils.hmacHex(capturingResponseWrapper.getStatus()+""));
							}
							response.getOutputStream().write(newContent.getBytes(RESPONSE_ENCODING));
						}


					}else {
						String addSecHeader = Constants.getProperty("ADD_SECURITY_HEADERS") ;
						if(addSecHeader != null && addSecHeader.equalsIgnoreCase("Y")) {

							((HttpServletResponse) response).addHeader("X-FRAME-OPTIONS", "SAMEORIGIN");
							((HttpServletResponse) response).addHeader("Content-Security-Policy", "frame-ancestors 'self'");

							((HttpServletResponse) response).setHeader("Cache-Control", "no-cache, no-store, max-age=0");
							((HttpServletResponse) response).setHeader("Pragma", "no-cache");
							((HttpServletResponse) response).setHeader("Expires", "0");

							((HttpServletResponse) response).addHeader("X-XSS-Protection", "1; mode=block");
							((HttpServletResponse) response).addHeader("X-Content-Type-Options", "nosniff");

						}
						String newContent = "Authorization Failed!";
						((HttpServletResponse) response).addHeader("Access-Control-Allow-Origin", com.btsl.util.Constants.getProperty("AllowedOrigins"));
						((HttpServletResponse) response).setStatus(HttpStatus.SC_UNAUTHORIZED);
						//((HttpServletResponse) response).setContentLength(newContent .length());
						((HttpServletResponse) response).getOutputStream().write(newContent.getBytes(RESPONSE_ENCODING));
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String intercept(HttpServletRequest httpRequest) throws IOException {
		String bodyInStringFormat="";
		if(Arrays.asList("POST", "PUT").contains(httpRequest.getMethod())) {
			String characterEncoding = httpRequest.getCharacterEncoding();
			Charset charset = Charset.forName(characterEncoding);
			bodyInStringFormat = readInputStreamInStringFormat(httpRequest.getInputStream(), charset);


		}
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


	private boolean skipSecurityCheck(HttpServletRequest request) {
		String skipSecurityHeaderName = Constants.getProperty("skip.security.header.validation.tag");
		String skipSecurityHeaderValue = request.getHeader(skipSecurityHeaderName);
		_log.debug("skipSecurityHeaderName = {},skipSecurityHeaderValue = {}",skipSecurityHeaderName,skipSecurityHeaderValue);
		return Boolean.parseBoolean(skipSecurityHeaderValue);
	}

	private boolean isCheckSumValidationEnabled(){
		return Boolean.parseBoolean(Constants.getProperty("checksum.validate.enable"));
	}

	public boolean preHandleNonce(HttpServletRequest request,String reqMethod,  HttpServletResponse response, String queryString , String decryptedNonce) {

		String refererHeader = request.getHeader("referer");
		if(refererHeader == null){
			refererHeader = request.getHeader("Referer");
		}
		String refHeader = null;
		URL reqUrl  = null;

		boolean validHost = false;
		if(refererHeader!=null) {
			try {
				refererHeader = new URL(refererHeader).getHost();
				refHeader = request.getHeader("referer");
				reqUrl  = new URL(refHeader);

			} catch (MalformedURLException e) {
				_log.debug("preHandleNonce", "MalformedURL: "+ refererHeader);
				refererHeader = request.getHeader("referer");
			}
		}
		String allowedReferers = com.btsl.util.Constants.getProperty("ALLOWED_REFERERS");
		if (BTSLUtil.isNullString(allowedReferers)) {
			_log.error("preHandleNonce", "ALLOWED_REFERERS not provided or could not load constants.");
			return false;
		}else {
			if(refererHeader!=null) {
				String[] urls = allowedReferers.split(",");


				for(String urlStr: urls) {
					try {
						URL url
								= new URL(urlStr);


						String host = url.getHost();
						int port = url.getPort();
						String protocol = url.getProtocol();

						String reqHost = reqUrl.getHost();
						int reqPort = reqUrl.getPort();
						String reqProtocol = reqUrl.getProtocol();




						if(host.equalsIgnoreCase(reqHost) && port==reqPort && protocol.equalsIgnoreCase(reqProtocol)) {
							validHost = true ;
						}

					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}




				}

			}
		}

		if(queryString != null && queryString.contains("MAPPGW")) {

		}
		else if(!validHost) {

			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			_log.error("preHandleNonce", "Invalid Referer");
			return false;
		}

		if (RequestMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod())) {
			return true;
		}

		if (!request.getDispatcherType().name().equals("REQUEST") || reqMethod.equalsIgnoreCase("GET1")) {
			return true;
		}

       /* if(request.getRequestURI() != null && (request.getRequestURI().contains("ComverseTestServer") ||  request.getRequestURI().contains("sendSms") ||  request.getRequestURI().contains("C2SReceiver") || request.getRequestURI().contains("nonceGenerator") || request.getRequestURI().contains("calculateSignature") ||  request.getRequestURI().contains("regex") || request.getRequestURI().contains("isPinChangeOnTxRequired") || request.getRequestURI().contains("generateTokenAPI")|| request.getRequestURI().endsWith("login") || request.getRequestURI().contains("getLocaleList") || request.getRequestURI().contains("getSublookupsCache"))) {
        	return true;
        }*/
		if(skipSecurityCheck(request) || !isCheckSumValidationEnabled()){
			return true;
		}
		try{


			//System.out.println("decryptedNonce>>>>>> "+decryptedNonce);
			String nonce = ofNullable(decryptedNonce).
					filter(StringUtils::isNotEmpty).
					// filter(nonceValue -> nonceValue.matches("[^[a-zA-Z0-9]+$]{16}")).
							orElseThrow(() -> new RuntimeException("Invalid Nonce"));
			_log.debug("nonce value = {}",nonce);

			NonceValidatorService nonceValidatorService = (NonceValidatorService) com.btsl.common.ApplicationContextProvider.getApplicationContext("TEST").getBean(NonceValidatorService.class);

			nonceValidatorService.validateNonce(nonce);
		}catch (Exception e){
			e.printStackTrace();
			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			_log.error("Error occurred while validating nonce",e);
			return false;
		}
		return true;
	}


	public boolean preHandle(String uri, String reqMethod,String dispatcherType,
							 HttpServletRequest request,
							 HttpServletResponse response, String body, String queryString, String decryptedNonce) {
		_log.debug("preHandle", "Inside Interceptor preHandle for signature validation...................."+reqMethod);

		if (RequestMethod.DELETE.toString().equalsIgnoreCase(reqMethod)   ||  RequestMethod.OPTIONS.toString().equalsIgnoreCase(reqMethod))
			return true;

		if (!dispatcherType.equals("REQUEST") || ( reqMethod.equalsIgnoreCase("GET")  && queryString == null) ||  reqMethod.equalsIgnoreCase("OPTIONS")) {
			return true;
		}


		try {
			//Internal Call Check
			if (decryptedNonce !=null && decryptedNonce.equalsIgnoreCase(Constants.getProperty("SECRET_NONCE_INTERNAL_API_CALL"))) {

				String KEY = Constants.getProperty("SECRET_KEY_INTERNAL_API_CALL");
				final String signature = request.getHeader("Signature");


				Optional<String> encodedBody = null;

				if (reqMethod != null && reqMethod.equalsIgnoreCase("GET")) {
					return true;

				} else {

					encodedBody = resolveEncodedBody(reqMethod, body, KEY);

				}


				if (!encodedBody.isPresent()) {
					return true;
				}

				if (encodedBody.filter(eb -> eb.equalsIgnoreCase(signature)).isPresent())
					return true;
				else {
					response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
					//response.getWriter().write("Internal Server Error");
					return false;
				}

			}else {



				final String signature = request.getHeader("Signature");
				// final String signature2 = request.getHeader("Signature2");
				String tokenKey = request.getHeader("Authorization");


				if(queryString != null && queryString.contains("MAPPGW")) {

					tokenKey = request.getHeader("OAuthorization");
					OAuthUserData oAuthUserData =null;
					OAuthUser oAuthUser = new OAuthUser();
					oAuthUserData =new OAuthUserData();

					oAuthUser.setData(oAuthUserData);
					HashMap<String, String> headers = new HashMap<String, String>();
					headers.put("authorization", tokenKey);

					OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response);
				}
				final String nonceKey = decryptedNonce;
				boolean skipSecurityHeaderValidate= isSkipSecurityHeaderValidation(request.getHeader(Constants.getProperty(SKIP_SECURITY_HEADER_VALIDATION_TAG)));

				Boolean  checksumValidationEnable = Boolean.parseBoolean(Constants.getProperty("checksum.validate.enable"));

				_log.debug("preHandle", "checksum hash validation enabled : {}",checksumValidationEnable);

				if(checksumValidationEnable && !StringUtils.isBlank(tokenKey)){
					if  (!skipSecurityHeaderValidate && (StringUtils.isBlank(nonceKey) || StringUtils.isBlank(signature))) {
						//TEst   response.setStatus(HttpStatus.SC_UNAUTHORIZED);
						return false;
					}
				}


				_log.debug("preHandle", "uri "+uri,checksumValidationEnable);



				if (!StringUtils.isBlank(tokenKey) && !StringUtils.isBlank(signature)) {
					_log.debug("preHandle", "Start validating checksum hash........"+tokenKey);
					String[] tokenArr = tokenKey.split(" ");
					if (tokenArr.length != 2) {
						//Test  response.setStatus(HttpStatus.SC_UNAUTHORIZED);
						return false;
					}

					String KEY = tokenArr[1];
					String KEY2 = null;

					_log.debug("preHandle uri "+uri, "KEY "+KEY);


					// Nonce will be part of SALT in future
					Optional<String> nonce = ofNullable(decryptedNonce);

					_log.debug("preHandle", "uri "+uri+"  nonce "+nonce);


					if(nonce.isPresent()){
						// Regex to be shared by UI team
						if(nonce.filter(nonceValue -> nonceValue.matches("[^[a-zA-Z0-9]+$]{16}")).isPresent()){
							KEY = format("%s.%s",tokenArr[1],nonce.get());
							KEY2 = format("%s",nonce.get());
						}else {
							//Test response.setStatus(HttpStatus.SC_UNAUTHORIZED);
							return false;
						}
					}
					Optional<String> encodedBody = null;
					Optional<String> encodedBody2 = null;

					if(reqMethod != null && reqMethod.equalsIgnoreCase("GET")) {
						if(queryString != null) {
							//encodedBody = resolveEncodedBody(reqMethod, body,KEY, AESEncryptionUtil.aesDecryptor(queryString, Constants.A_KEY));
							//encodedBody2 = resolveEncodedBody(reqMethod, body,KEY2, AESEncryptionUtil.aesDecryptor(queryString, Constants.A_KEY));
							encodedBody = resolveEncodedBody(reqMethod, body,KEY, queryString);
							encodedBody2 = resolveEncodedBody(reqMethod, body,KEY2, queryString);


						}else {
							encodedBody = resolveEncodedBody(reqMethod, Constants.getProperty("CONFIDENTIAL_PAYLOAD"),KEY);
						}
					}else {
						if(body != null && (body.toLowerCase().contains("fileattachment"))){
							encodedBody = resolveEncodedBody(reqMethod, Constants.getProperty("CONFIDENTIAL_PAYLOAD"),KEY);
							encodedBody2 = resolveEncodedBody(reqMethod, Constants.getProperty("CONFIDENTIAL_PAYLOAD"),KEY2, queryString);
						}else {
							encodedBody = resolveEncodedBody(reqMethod, body, KEY);
							encodedBody2 = resolveEncodedBody(reqMethod, body,KEY2, queryString);
						}



					}

					_log.debug("preHandle", "KEY "+KEY);

					//_log.debug("preHandle", "check-sum hash = {},url = {},query parameters = {}", encodedBody,uri,request.getQueryString());
					if(!encodedBody.isPresent()){
						return true;
					}


//	                System.out.println("uri "+uri+"   encodedBody  "+encodedBody+" "+signature);
					if (encodedBody.filter(eb -> eb.equalsIgnoreCase(signature)).isPresent()) {
						return true;
					}else if (encodedBody2.filter(eb -> eb.equalsIgnoreCase(signature)).isPresent()) {
						return true;
					}else {
//	                	System.out.println("uri "+uri+"   encodedBody  "+encodedBody+" "+signature+" does not match");
						response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
						//response.getWriter().write("Internal Server Error");
						return false;
						//return true;
					}
				} else
					return true;


			}
		}catch (BTSLBaseException be) {
			be.printStackTrace();
			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			return false;

		} catch (Exception ioex) {
			ioex.printStackTrace();
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			return false;
			//return true;
		}
	}


	private Optional<String> resolveEncodedBody(String reqMethod, String body, final String KEY) throws IOException {
		final HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, KEY);

//	        final HmacUtils hmacUtils = new HmacUtils(KEY);


		if(reqMethod != null && reqMethod.equalsIgnoreCase("GET")){
			return null;

		}else{
			String jsonBody =body;//request.getReader().lines().collect(Collectors.joining());
			// request.getInputStream().
			_log.debug("","Generating server-side checksum hash");
			return of(hmacUtils.hmacHex(jsonBody));
			// return of(hmacUtils.hmac(jsonBody).toString());
		}
	}

	private Optional<String> resolveEncodedBody(String reqMethod, String body, final String KEY, String queryString) throws IOException {
		final HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, KEY);


		if(reqMethod != null && reqMethod.equalsIgnoreCase("GET")){
			final String queryParams = queryString;//request.getQueryString();
			if(StringUtils.isEmpty(queryParams)){
				_log.debug("no query parameters for request = {}", "");
				return empty();
			}
			//  final String params = request.getHeader("Params");
			///   boolean isGatewayAppended = isGatewayAppendedParamsPresent(request);
	           /* _log.debug("params = {}",params);
	            if(StringUtils.isEmpty(params)) {// && !isGatewayAppended){
	            	_log.debug("","Params header is missing which is mandatory when query parameters are present");
	               // return of("NO_PARAMS");
	                return empty();

	            }*/
	            /*if(StringUtils.isEmpty(params) && isGatewayAppended){
	            	_log.debug("no query parameters from channels = {}",request.getRequestURI());
	                return empty();
	            }
*/
			//return of(hmacUtils.hmacHex(decodeQueryParameters(params)));
			return of(hmacUtils.hmacHex(queryParams));

		}else{
			String jsonBody =body;//request.getReader().lines().collect(Collectors.joining());
			// request.getInputStream().
			_log.debug("","Generating server-side checksum hash");
			return of(hmacUtils.hmacHex(jsonBody));
			// return of(hmacUtils.hmac(jsonBody).toString());
		}
	}

	private String decodeQueryParameters(String params) {
		byte[] paramsDecoded = Base64.getDecoder().decode(params);
		return new String(paramsDecoded, StandardCharsets.UTF_8);
	}

	private boolean isGetRequest(HttpServletRequest request) {
		return "GET".equalsIgnoreCase(request.getMethod());
	}

	private boolean isSkipSecurityHeaderValidation(String skipSecurityHeaderValue){
		_log.debug("","skip security Header validation value : {}",skipSecurityHeaderValue);
		return skipSecurityHeaderValue == null ? false : Boolean.valueOf(skipSecurityHeaderValue);
	}

	private boolean isGatewayAppendedParamsPresent(HttpServletRequest request){
		if(!StringUtils.isEmpty(request.getQueryString()) && "true".equals(request.getParameter(GATEWAY_APPENDED_PARAMS_PRESENT))) {
			_log.debug("", "Query params are added from gateway!");
			return true;
		}
		else
			return false;
	}

	public void destroy() {
		this.filterConfig = null;
	}

	public boolean isValid( Pattern p , String value) {
		Matcher m = p.matcher(value);
		return  m.matches();
	}

	public SecurityConfigVO getSecurityConfigVO(String clientId){
		SecurityConfigVO securityConfigVO = new SecurityConfigVO();
		if(BTSLUtil.isNullString(clientId)){
			return securityConfigVO;
		}
		String clientDetailsQuery = "Select * from oauth_client_details where client_id = ?";
		MComConnectionI mcomCon = null;
		Connection con = null;
		String additionalSecurityConfig = "";
		String additionalSecurity ="";
		try{
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			PreparedStatement pstmt = con.prepareStatement(clientDetailsQuery);
			pstmt.setString(1,clientId);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				additionalSecurity = rs.getString("additional_security");
				if(additionalSecurity != null){
					additionalSecurityConfig = additionalSecurity;
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		try{
			Object object = new JSONParser().parse(additionalSecurityConfig);
			JSONObject jsonObject = (JSONObject)object;
			if(jsonObject.get("nonceSignatureValidation") != null && ((String)jsonObject.get("nonceSignatureValidation")).equalsIgnoreCase("Y")){
				securityConfigVO.setNonceSignatureValidation(true);
			}
			if(jsonObject.get("refererValidation") != null && ((String)jsonObject.get("refererValidation")).equalsIgnoreCase("Y")){
				securityConfigVO.setRefererValidation(true);
			}
			if(jsonObject.get("nonceEncryption") != null && ((String)jsonObject.get("nonceEncryption")).equalsIgnoreCase("Y")){
				securityConfigVO.setNonceEncryption(true);
			}
			if(jsonObject.get("payloadEncryption") != null && ((String)jsonObject.get("payloadEncryption")).equalsIgnoreCase("Y")){
				securityConfigVO.setPayloadEncryption(true);
			}
			if(jsonObject.get("nonceRegex") != null){
				securityConfigVO.setNonceRegex((String)jsonObject.get("nonceRegex"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return securityConfigVO;
	}


}
