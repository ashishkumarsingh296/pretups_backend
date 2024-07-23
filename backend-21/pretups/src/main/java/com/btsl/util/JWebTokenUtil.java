package com.btsl.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.OAuthRefTokenReq;
import com.btsl.user.businesslogic.OAuthRefTokenRequest;
import com.btsl.user.businesslogic.OAuthTokenReq;
import com.btsl.user.businesslogic.OAuthTokenRequest;

/**
 * Utility class for JWT
 */
public class JWebTokenUtil {

	private static final String SECRET_KEY = "PreTUPS_RoadMap_Key";
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	private static final String ISSUER = "pretups.oauth";
	private static final String JWT_HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
	private JSONObject payload = new JSONObject();
	private String signature;
	private String encodedHeader;

	private JWebTokenUtil() {
		encodedHeader = encode(new JSONObject(JWT_HEADER));
	}

	public JWebTokenUtil(OAuthRefTokenReq oAuthRefTokenReq, long expires) {
		this();
		payload.put("exp", expires);
		payload.put("iat", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
		payload.put("iss", ISSUER);
		payload.put("jti", UUID.randomUUID().toString());
		signature = hmacSha256(encodedHeader + "." + encode(payload), SECRET_KEY);
	}

	public JWebTokenUtil(OAuthTokenReq oAuthTokenReq, long expires) {
		this();

		if (oAuthTokenReq != null) {
			payload.put("loginId", oAuthTokenReq.getLoginId());
			payload.put("msisdn", oAuthTokenReq.getMsisdn());
			payload.put("msisdn", oAuthTokenReq.getReqGatewayType());
			payload.put("msisdn", oAuthTokenReq.getReqGatewayCode());
			payload.put("msisdn", oAuthTokenReq.getReqGatewayLoginId());
			payload.put("msisdn", oAuthTokenReq.getServicePort());
			payload.put("msisdn", oAuthTokenReq.getSourceType());
		}

		payload.put("exp", expires);
		payload.put("iat", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
		payload.put("iss", ISSUER);
		payload.put("jti", UUID.randomUUID().toString());
		signature = hmacSha256(encodedHeader + "." + encode(payload), SECRET_KEY);
	}

	
	public JWebTokenUtil(OAuthRefTokenRequest oAuthRefTokenReq, long expires, String tokenId) {
		this();
		payload.put("exp", expires);
		payload.put("iat", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
		payload.put("iss", ISSUER);
		payload.put("jti", UUID.randomUUID().toString());
		payload.put("tokenId", tokenId);
		signature = hmacSha256(encodedHeader + "." + encode(payload), SECRET_KEY);
		
	}
	
	public JWebTokenUtil(OAuthRefTokenRequest oAuthRefTokenReq, long expires) {
		this();
		payload.put("exp", expires);
		payload.put("iat", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
		payload.put("iss", ISSUER);
		payload.put("jti", UUID.randomUUID().toString());
		signature = hmacSha256(encodedHeader + "." + encode(payload), SECRET_KEY);
	}

	public JWebTokenUtil(OAuthTokenRequest oAuthTokenReq, long expires) {
		this();

		if (oAuthTokenReq != null) {
			payload.put("loginId", oAuthTokenReq.getIdentifierValue());
			payload.put("msisdn", oAuthTokenReq.getReqGatewayType());
			payload.put("msisdn", oAuthTokenReq.getReqGatewayCode());
			payload.put("msisdn", oAuthTokenReq.getReqGatewayLoginId());
			payload.put("msisdn", oAuthTokenReq.getServicePort());
			payload.put("msisdn", oAuthTokenReq.getSourceType());
		}

		payload.put("exp", expires);
		payload.put("iat", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
		payload.put("iss", ISSUER);
		payload.put("jti", UUID.randomUUID().toString());
		signature = hmacSha256(encodedHeader + "." + encode(payload), SECRET_KEY);
	}


	public JWebTokenUtil(OAuthTokenReq oAuthTokenReq, long expires, String loginId, String msisdn) {
		this();

		Random rand = new Random(); 

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
		String tokenId  = sdf.format(date) +"."+rand.nextInt(10000);
		
		if (oAuthTokenReq != null) {
			payload.put("loginId", loginId);
			payload.put("msisdn", msisdn);
			payload.put("reqGatewayType", oAuthTokenReq.getReqGatewayType());
			payload.put("reqGatewayCode", oAuthTokenReq.getReqGatewayCode());
			payload.put("reqGatewayLoginId", oAuthTokenReq.getReqGatewayLoginId());
			payload.put("servicePort", oAuthTokenReq.getServicePort());
			payload.put("sourceType", oAuthTokenReq.getSourceType());
			
		}
		payload.put("tokenId", tokenId);
		payload.put("exp", expires);
		payload.put("iat", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
		payload.put("iss", ISSUER);
		payload.put("jti", UUID.randomUUID().toString());
		signature = hmacSha256(encodedHeader + "." + encode(payload), SECRET_KEY);
	}

	public JWebTokenUtil(OAuthTokenRequest oAuthTokenReq, long expires, String loginId, String msisdn, String tokenId) {
		this();

		if (oAuthTokenReq != null) {
			payload.put("loginId", loginId);
			payload.put("msisdn", msisdn);
			payload.put("reqGatewayType", oAuthTokenReq.getReqGatewayType());
			payload.put("reqGatewayCode", oAuthTokenReq.getReqGatewayCode());
			payload.put("reqGatewayLoginId", oAuthTokenReq.getReqGatewayLoginId());
			payload.put("servicePort", oAuthTokenReq.getServicePort());
			payload.put("sourceType", oAuthTokenReq.getSourceType());
			
		}
		payload.put("tokenId", tokenId);
		payload.put("exp", expires);
		payload.put("iat", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
		payload.put("iss", ISSUER);
		payload.put("jti", UUID.randomUUID().toString());
		signature = hmacSha256(encodedHeader + "." + encode(payload), SECRET_KEY);
	}

	/**
	 * For verification
	 *
	 * @param token
	 * @throws java.security.NoSuchAlgorithmException
	 */
/*	public JWebTokenUtil(String token) throws NoSuchAlgorithmException {
		
		this();
		String[] parts = token.split("\\.");
		if (parts.length != 3) {
			throw new IllegalArgumentException("Invalid Token format");
		}
		if (encodedHeader.equals(parts[0])) {
			encodedHeader = parts[0];
		} else {
			throw new NoSuchAlgorithmException("JWT Header is Incorrect: " + parts[0]);
		}

		payload = new JSONObject(decode(parts[1]));
		if (payload == null) {
			throw new JSONException("Payload is Empty: ");
		}
		if (!payload.has("exp")) {
			throw new JSONException("Payload doesn't contain expiry " + payload);
		}
		signature = parts[2];
	}

	*/
	
	public static String retrieveTokenId(String token) throws NoSuchAlgorithmException, BTSLBaseException {

		String[] parts = token.split("\\.");
		String encodedHeader = encode(new JSONObject(JWT_HEADER));

		if (parts.length != 3) {
			/* throw new IllegalArgumentException("Invalid Token format"); */
			throw new BTSLBaseException("JWebTokenUtil", "JWebTokenUtil", PretupsErrorCodesI.INVALID_TOKEN_FORMAT,
					PretupsI.RESPONSE_FAIL, null);
		}
		if (encodedHeader.equals(parts[0])) {
			encodedHeader = parts[0];
		} else {
			throw new BTSLBaseException("JWebTokenUtil", "JWebTokenUtil", PretupsErrorCodesI.INVALID_TOKEN_FORMAT,
					PretupsI.RESPONSE_FAIL, null);
		}

		JSONObject payload = new JSONObject(decode(parts[1]));

		if (!payload.has("tokenId")) {
			/* throw new JSONException("Payload doesn't contain expiry " + payload); */
			throw new BTSLBaseException("JWebTokenUtil", "JWebTokenUtil", PretupsErrorCodesI.INVALID_TOKEN_FORMAT,
					PretupsI.RESPONSE_FAIL, null);
		}else {
			return payload.getString("tokenId");
		}

	}

	public static void validateToken(String token) throws NoSuchAlgorithmException, BTSLBaseException {
	
		String[] parts = token.split("\\.");
		String encodedHeader = encode(new JSONObject(JWT_HEADER));
		
		if (parts.length != 3) {
			/*throw new IllegalArgumentException("Invalid Token format");*/
			 throw new BTSLBaseException("JWebTokenUtil", "JWebTokenUtil", PretupsErrorCodesI.INVALID_TOKEN_FORMAT, PretupsI.RESPONSE_FAIL,null);
		}
		if (encodedHeader.equals(parts[0])) {
			encodedHeader = parts[0];
		} else {
			throw new BTSLBaseException("JWebTokenUtil", "JWebTokenUtil", PretupsErrorCodesI.INVALID_TOKEN_FORMAT, PretupsI.RESPONSE_FAIL,null);
		}


		JSONObject payload = new JSONObject(decode(parts[1]));

		if (!payload.has("exp")) {
			/*throw new JSONException("Payload doesn't contain expiry " + payload);*/
			throw new BTSLBaseException("JWebTokenUtil", "JWebTokenUtil", PretupsErrorCodesI.NO_EXPIRY_IN_PAYLOAD, PretupsI.RESPONSE_FAIL,null);
		}
		
		//String signatureObj = hmacInternalSha256(encodedHeader + "." + encode(payload), SECRET_KEY);
		String signatureObj = hmacSha256(encodedHeader + "." + encode(payload), SECRET_KEY);

		if (signatureObj.equalsIgnoreCase(parts[2]) == false) {
			throw new BTSLBaseException("JWebTokenUtil", "JWebTokenUtil", PretupsErrorCodesI.INVALID_TOKEN_FORMAT, PretupsI.RESPONSE_FAIL,null);
		}
		
		
	}
	
	@Override
	public String toString() {
		return encodedHeader + "." + encode(payload) + "." + signature;
	}

	public boolean isValid() {
		return payload.getLong("exp") > (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) // token not expired
				&& signature.equals(hmacSha256(encodedHeader + "." + encode(payload), SECRET_KEY)); // signature matched
	}

	private static String encode(JSONObject obj) {
		try{
			return new CryptoUtil().encrypt(obj.toString(), Constants.KEY);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private static String encode(byte[] bytes) {
		//return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
		return BTSLUtil.encryptText(bytes.toString());
	}

	private static String decode(String encodedString) {
		try{
			return new CryptoUtil().decrypt(encodedString, Constants.KEY);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private static String hmacSha256(String data, String secret) {
		
		final HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret);
		return hmacUtils.hmacHex(data);
		
	}
	/**
	 * Sign with HMAC SHA256 (HS256)
	 *
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private static String hmacInternalSha25611(String data, String secret) {
		try {

			// MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = secret.getBytes(StandardCharsets.UTF_8);// digest.digest(secret.getBytes(StandardCharsets.UTF_8));

			Mac sha256Hmac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
			sha256Hmac.init(secretKey);

			byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
			return encode(signedBytes);
		} catch (NoSuchAlgorithmException | InvalidKeyException ex) {
			Logger.getLogger(JWebTokenUtil.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}
	
	/**
	 * Sign with HMAC SHA256 (HS256)
	 *
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private String hmacSha2562222(String data, String secret) {
		try {

			// MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = secret.getBytes(StandardCharsets.UTF_8);// digest.digest(secret.getBytes(StandardCharsets.UTF_8));

			Mac sha256Hmac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
			sha256Hmac.init(secretKey);

			byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
			return encode(signedBytes);
		} catch (NoSuchAlgorithmException | InvalidKeyException ex) {
			Logger.getLogger(JWebTokenUtil.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}
/*
	public static void main(String args[]) {
		
		OAuthTokenReq  oAuthTokenReq = null;
		
		JWebTokenUtil jwt = new JWebTokenUtil(oAuthTokenReq , 30L, null, null);
		
		try {
			JWebTokenUtil  jwtV = new JWebTokenUtil(jwt.toString());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("jwt " + jwt.toString());

	}
*/}
