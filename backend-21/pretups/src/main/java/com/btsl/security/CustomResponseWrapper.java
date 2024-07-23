package com.btsl.security;

import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Base64;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class CustomResponseWrapper extends HttpServletResponseWrapper {

	private final ByteArrayOutputStream capture;
	private ServletOutputStream output;
	private PrintWriter writer;

	private HttpServletRequest request;

	public CustomResponseWrapper(HttpServletResponse response , HttpServletRequest request) {
		super(response);
		capture = new ByteArrayOutputStream(response.getBufferSize());
		this.request = request;
	}

	@Override
	public ServletOutputStream getOutputStream() {
		if (writer != null) {
			throw new IllegalStateException("getWriter() has already been called on this response.");
		}

		if (output == null) {
			// inner class - lets the wrapper manipulate the response
			output = new ServletOutputStream() {
				@Override
				public void write(int b) throws IOException {
					capture.write(b);
				}

				@Override
				public void flush() throws IOException {
					capture.flush();
				}

				@Override
				public void close() throws IOException {
					capture.close();
				}

				@Override
				public boolean isReady() {
					return false;
				}

				@Override
				public void setWriteListener(WriteListener arg0) {
				}
			};
		}

		return output;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (output != null) {
			throw new IllegalStateException("getOutputStream() has already been called on this response.");
		}

		if (writer == null) {
			writer = new PrintWriter(new OutputStreamWriter(capture, getCharacterEncoding()));
		}

		return writer;
	}

	@Override
	public void flushBuffer() throws IOException {
		super.flushBuffer();

		if (writer != null) {
			writer.flush();
		} else if (output != null) {
			output.flush();
		}
	}

	public byte[] getCaptureAsBytes() throws IOException {
		if (writer != null) {
			writer.close();
		} else if (output != null) {
			output.close();
		}

		return capture.toByteArray();
	}

	public String getCaptureAsString() throws IOException {
		String response = new String(getCaptureAsBytes(), getCharacterEncoding());
		String internalNonce = request.getHeader("Nonce");
		if(Constants.getProperty("SECRET_NONCE_INTERNAL_API_CALL").equals(internalNonce)){
			return response;
		}
		String bypassResponseEncryption = Constants.getProperty("BYPASS_RESPONSE_ENCRYPTION");
		if(bypassResponseEncryption != null ){
			String[] bypassResponseEncryptionArray = bypassResponseEncryption.split(",");
			for(String bypassResponseEncryptionValue : bypassResponseEncryptionArray){
				if(request.getRequestURI().contains(bypassResponseEncryptionValue)){
					return response;
				}
			}
		}
		String encryptedResponse = "";
		try{
			//System.out.println("Decrypted Response "+response);
			encryptedResponse = AESEncryptionUtil.aesEncryptor(response, Constants.A_KEY);
		}catch (Exception e){
			e.printStackTrace();
			return response;
		}
		String finalResponse = "{\"data\":\""+encryptedResponse+"\"}";
		return finalResponse;
	}

}
