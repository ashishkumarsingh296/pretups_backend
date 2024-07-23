package com.restapi.user.service;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SqlParameterEncoder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
/**
 * @author anshul.goyal2
 * Upload the file using API
 */
@Path("")
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value = "C2C Stock")
public class C2CFileUploadController {
	@Context
	private HttpServletRequest httpServletRequest;


	private String parseFileName(MultivaluedMap<String, String> headers) {

		String[] contentDispositionHeader = headers.getFirst(
				"Content-Disposition").split(";");

		for (String name : contentDispositionHeader) {

			if ((name.trim().startsWith("filename"))) {

				String[] tmp = name.split("=");

				String fileName = tmp[1].trim().replaceAll("\"", "");

				return fileName;
			}
		}
		return "randomName";
	}

	private void saveFile(InputStream uploadedInputStream, String serverLocation) {
		OutputStream outpuStream = null;
		try {
			outpuStream = new FileOutputStream(new File(
					serverLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				outpuStream.write(bytes, 0, read);
			}
			outpuStream.flush();
			
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			if (outpuStream !=null) {
				try {
					outpuStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@POST
	@Path("/c2s-rest-receiver-upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public PretupsResponse<JsonNode> uploadFile(MultipartFormDataInput input)
			throws IOException {

		String fileName = "";

		boolean validFileFormat = false;
		
		Map<String, List<InputPart>> formParts = input.getFormDataMap();
		List<InputPart> inPart = formParts.get("file");

		
		for (InputPart inputPart : inPart) {
			try {
				MultivaluedMap<String, String> headers = inputPart.getHeaders();
				fileName = parseFileName(headers);
				if(BTSLUtil.validateFileFormat(fileName) == true ) {
				
				validFileFormat = true;
					
				InputStream istream = inputPart
						.getBody(InputStream.class, null);

				fileName = Constants
						.getProperty("UploadResAssociateMSISDNFilePath")
						+ fileName;

				saveFile(istream, fileName);

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		if(validFileFormat == true) {

		String output = "File saved to server location : " + fileName;

		
		String serviceKeyword = SqlParameterEncoder.encodeParams(input.getFormDataPart("servicekeyword",
				String.class, null));
		String jsonInput = input
				.getFormDataPart("inputreq", String.class, null);

		JsonNode request = (JsonNode) PretupsRestUtil.convertJSONToObject(
				jsonInput, new TypeReference<JsonNode>() {
				});

		PretupsResponse<JsonNode> response;
		RestReceiver restReceiver = new RestReceiver();
		final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
		response = restReceiver.processRequestChannel(httpServletRequest,request, serviceKeyword.toUpperCase(),
				requestIDStr);
		return response;
		
		}else {
			PretupsResponse<JsonNode> response = new PretupsResponse<JsonNode>(); 
			
			response.setMessage("File format is invalid. Allowable formats are [pdf, png, jpg].");
			return response;
		}
		

	}
}
