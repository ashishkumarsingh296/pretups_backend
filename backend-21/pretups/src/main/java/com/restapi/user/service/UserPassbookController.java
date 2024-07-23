package com.restapi.user.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;


import org.springframework.web.bind.annotation.RequestBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.PassbookDetailsVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPassboobVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.ibm.icu.util.Calendar;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 
 * @author akhilesh.mittal1
 *
 */
@Path("")
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value = "Download Passbook")
public class UserPassbookController implements ServiceKeywordControllerI {

	public static final Log log = LogFactory.getLog(UserPassbookController.class.getName());

	@POST
	@Path("/downloadPassbook")
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	//@ApiOperation(value = "User Passbook - Download PDF", response = Response.class)

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadPassbook.summary}", description="${downloadPassbook.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Response.class))
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

	public Response downloadPassbook(@RequestBody UserPassboobVO requestData)
			throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException {

		String methodName = "downloadPassbook";

		try {
			
			String fromDate = requestData.getFromDate();
			String toDate = requestData.getToDate();
			String loginId = requestData.getLoginId();
			String msisdn = requestData.getMsisdn();

			if (BTSLUtil.isNullString(fromDate)) {

				ResponseBuilder response = Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
						.entity("fromDate is mandatory");
				return response.build();

			}
			if (BTSLUtil.isNullString(toDate)) {

				ResponseBuilder response = Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
						.entity("toDate is mandatory");
				return response.build();
			}

			if (BTSLUtil.isNullString(loginId) && BTSLUtil.isNullString(msisdn)) {

				ResponseBuilder response = Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
						.entity("Blank loginId/msisdn");
				return response.build();
			}

			String file = createPDFFile(loginId, fromDate, toDate, msisdn);

			File fileNew = new File(file);
			String fileName = fileNew.getName();
			System.out.println("2");
			ResponseBuilder response = Response.ok((Object) fileNew);
			response.header("Content-Disposition", "attachment;filename=" + fileName);

			return response.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(methodName, "Exception has occured " + e);

			ResponseBuilder response = Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
					.entity("Exception has occured " + e);
			return response.build();

		}
	}

	@Override
	public void process(RequestVO p_requestVO) {
		try {
			if(p_requestVO.getRequestMessage() != null &&  p_requestVO.getRequestMessage().contains("<FROMDATE>")) {

				
				String fromDate = p_requestVO.getRequestMessage().substring(p_requestVO.getRequestMessage().indexOf("<FROMDATE>")+10);
				
				if(fromDate != null && fromDate.contains("</FROMDATE>")) {
				fromDate = fromDate.substring(0, fromDate.indexOf("</FROMDATE>"));
				}
				
				String toDate = p_requestVO.getRequestMessage().substring(p_requestVO.getRequestMessage().indexOf("<TODATE>")+8);
				if(toDate != null && toDate.contains("</TODATE>")) {
					toDate = toDate.substring(0, toDate.indexOf("</TODATE>"));
				}
				
				
				p_requestVO.setFromDate(fromDate);
				p_requestVO.setToDate(toDate);
			
				
			}
			else if(p_requestVO.getRequestMessage() != null &&  p_requestVO.getRequestMessage().contains("FROMDATE")) {
				
				String fromDate = p_requestVO.getRequestMessage().substring(p_requestVO.getRequestMessage().indexOf("FROMDATE=")+9);
				
				if(fromDate != null && fromDate.contains("&")) {
				fromDate = fromDate.substring(0, fromDate.indexOf("&"));
				}
				
				String toDate = p_requestVO.getRequestMessage().substring(p_requestVO.getRequestMessage().indexOf("TODATE=")+7);
				if(toDate != null && toDate.contains("&")) {
					toDate = toDate.substring(0, toDate.indexOf("&"));
				}
				
				
				p_requestVO.setFromDate(fromDate);
				p_requestVO.setToDate(toDate);
			}
			
			String file = createPDFFile(p_requestVO.getUserLoginId(), p_requestVO.getFromDate(),
					p_requestVO.getToDate(), p_requestVO.getFilteredMSISDN());

			File fileNew = new File(file);
			String fileName = fileNew.getName();
			ResponseBuilder response = Response.ok((Object) fileNew);
			response.header("Content-Disposition", "attachment;filename=" + fileName);

			p_requestVO.setResponseMultiPartpath(file);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private String createPDFFile(String loginId, String fromDate, String toDate, String msisdn) throws Exception {

		
		
		
		Document document = new Document();
		Connection con = null;
		MComConnectionI mcomCon = null;
		OutputStream outputstream = null;
		ChannelUserDAO channelUserDAO = null;
		String filePath = null;
		
		try {
			
			Date dateFile = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyhhmmss");
			filePath = Constants.getProperty("VOMS_LOGGER_PATH")+"/"+formatter.format(dateFile)+".pdf";
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			channelUserDAO = new ChannelUserDAO();

			UserDAO userDao = new UserDAO();
			PassbookDetailsVO passbookDetailsVO = new PassbookDetailsVO();

			if (msisdn != null) {
				ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);
				passbookDetailsVO.setUserID(channelUserVO.getUserID());
			}else if(loginId != null) {
				ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetailsByLoginId(con, loginId);
				passbookDetailsVO.setUserID(channelUserVO.getUserID());
			}
			
			
			
			
			Calendar call = Calendar.getInstance();
			call.setTime(BTSLDateUtil.getGregorianDate(fromDate));
			
			call.add(Calendar.DATE, -1);
			
			Date prevDay = call.getTime();
			
			passbookDetailsVO.setFromDate(prevDay);
			passbookDetailsVO.setToDate(BTSLDateUtil.getGregorianDate(toDate));

			LinkedHashMap<Date, PassbookDetailsVO> map = userDao.loadStockSalesC2C(con, passbookDetailsVO);
			try {
				map = userDao.loadStockSalesC2S(con, passbookDetailsVO, map);
			} catch (Exception e) {
			}
			try {
				map = userDao.loadCommissionQtyC2C(con, passbookDetailsVO, map);
			} catch (Exception e) {
			}
			try {
				map = userDao.loadCommissionQtyC2S(con, passbookDetailsVO, map);
			} catch (Exception e) {
			}
			try {
				map = userDao.loadClosingBalance(con, passbookDetailsVO, map);
			} catch (Exception e) {
			}
			try {
				map = userDao.loadWithdrawBalance(con, passbookDetailsVO, map);
			} catch (Exception e) {
			}
			try {
				map = userDao.loadReturnBalance(con, passbookDetailsVO, map);
			} catch (Exception e) {
			}

			SimpleDateFormat simpleDateFormatHeader = new SimpleDateFormat("dd MMM YYYY");
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM");
			outputstream = new FileOutputStream(filePath);
			
			PdfWriter writer = PdfWriter.getInstance(document, outputstream);
			document.open();

			
			
			
			
			
			
			
			Set<Date> keys = map.keySet();
			document.add(new Paragraph(new Phrase("            "+simpleDateFormatHeader.format(BTSLDateUtil.getGregorianDate(fromDate))+" - "+simpleDateFormatHeader.format(BTSLDateUtil.getGregorianDate(toDate)))));
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			
			Font fontBold = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);
			Font fontR = FontFactory.getFont(FontFactory.HELVETICA, 11);
			
			Paragraph pp = new Paragraph(new Phrase("            Opening Balance: ", fontR));
			pp.add(new Chunk(map.get(prevDay).getClosingBalance()+"", fontBold));
			document.add(pp);
			
			
			document.add(Chunk.NEWLINE);
			
			
			
			Date k = prevDay;
			
			
			String lastClosingBal = null;
			
			while(k.before(BTSLDateUtil.getGregorianDate(toDate))  || k.compareTo(BTSLDateUtil.getGregorianDate(toDate)) == 0) {
				
				k = addDay(k);
			
				if(map.get(k) != null) {
					
				PdfPTable table = new PdfPTable(2);

				
				String date = simpleDateFormat.format(k);
				Font fontH1 = FontFactory.getFont(FontFactory.HELVETICA, 10);
				
				Font fontB = FontFactory.getFont(FontFactory.HELVETICA, 11);
				fontB.setColor(new BaseColor(158, 121, 225));
				
								
				Paragraph para = new Paragraph();
				PdfPCell c1 = new PdfPCell(new Phrase(date + "", fontB));
				c1.setHorizontalAlignment(Element.ALIGN_LEFT);

				c1.setBorder(Rectangle.NO_BORDER);
				c1.setBackgroundColor(new BaseColor(248, 248, 248));
				
				table.addCell(c1);

				c1 = new PdfPCell(new Phrase("Value", fontB));

				c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				c1.setBorder(Rectangle.NO_BORDER);
				c1.setBackgroundColor(new BaseColor(248, 248, 248));

				table.addCell(c1);

				table.setHeaderRows(1);

				//table.addCell(new PdfPCell(new Phrase("")));
				//table.addCell(new PdfPCell(new Phrase("")));
				
				
				c1 = new PdfPCell(new Phrase("C2C Stock Sales", fontH1));
				c1.setHorizontalAlignment(Element.ALIGN_LEFT);
				c1.setBorder(Rectangle.NO_BORDER);
				c1.setBackgroundColor(new BaseColor(248, 248, 248));
				table.addCell(c1);

				
				
				c1 = new PdfPCell(new Phrase("-" + (map.get(k).getC2CStockSales() ), fontH1));
				c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				c1.setBorder(Rectangle.NO_BORDER);
				c1.setBackgroundColor(new BaseColor(248, 248, 248));
				table.addCell(c1);
				
				
				
				
				
				
				c1 = new PdfPCell(new Phrase("C2S Stock Sales", fontH1));
				c1.setHorizontalAlignment(Element.ALIGN_LEFT);
				c1.setBorder(Rectangle.NO_BORDER);
				c1.setBackgroundColor(new BaseColor(248, 248, 248));
				table.addCell(c1);

				
				
				c1 = new PdfPCell(new Phrase("-" + ( map.get(k).getC2SStockSales() ), fontH1));
				c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				c1.setBorder(Rectangle.NO_BORDER);
				c1.setBackgroundColor(new BaseColor(248, 248, 248));
				table.addCell(c1);
				
				

				c1 = new PdfPCell(new Phrase("Commission", fontH1));
				c1.setHorizontalAlignment(Element.ALIGN_LEFT);
				c1.setBorder(Rectangle.NO_BORDER);
				c1.setBackgroundColor(new BaseColor(248, 248, 248));
				table.addCell(c1);

				c1 = new PdfPCell(new Phrase("+" + map.get(k).getCommissionValue(), fontH1));
				c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				c1.setBorder(Rectangle.NO_BORDER);
				c1.setBackgroundColor(new BaseColor(248, 248, 248));

				table.addCell(c1);

				c1 = new PdfPCell(new Phrase("Stock Purchase", fontH1));
				c1.setHorizontalAlignment(Element.ALIGN_LEFT);
				c1.setBorder(Rectangle.NO_BORDER);
				c1.setBackgroundColor(new BaseColor(248, 248, 248));

				table.addCell(c1);

				c1 = new PdfPCell(new Phrase("+" + map.get(k).getStockPurchase(), fontH1));
				c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				c1.setBorder(Rectangle.NO_BORDER);
				c1.setBackgroundColor(new BaseColor(248, 248, 248));
				table.addCell(c1);

				c1 = new PdfPCell(new Phrase("Closing Balance", fontH1));
				c1.setHorizontalAlignment(Element.ALIGN_LEFT);
				c1.setBorder(Rectangle.NO_BORDER);
				c1.setBackgroundColor(new BaseColor(248, 248, 248));
				table.addCell(c1);

				c1 = new PdfPCell(new Phrase("" + map.get(k).getClosingBalance(), fontH1));
				c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				c1.setBorder(Rectangle.NO_BORDER);
				c1.setBackgroundColor(new BaseColor(248, 248, 248));
				table.addCell(c1);
				
				lastClosingBal = map.get(k).getClosingBalance() +""; 

				c1 = new PdfPCell(new Phrase("Withdraw/Return", fontH1));
				c1.setHorizontalAlignment(Element.ALIGN_LEFT);
				c1.setBorder(Rectangle.NO_BORDER);
				c1.setBackgroundColor(new BaseColor(248, 248, 248));
				table.addCell(c1);

				c1 = new PdfPCell(new Phrase("" + ( map.get(k).getWithdrawBalance() + map.get(k).getReturnBalance() ), fontH1));
				c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				c1.setBorder(Rectangle.NO_BORDER);
				c1.setBackgroundColor(new BaseColor(248, 248, 248));
				table.addCell(c1);

				para.add(table);

				document.add(para);

				document.add(Chunk.NEWLINE);
				document.add(Chunk.NEWLINE);

			}
			}

			

			Paragraph ppClosing = new Paragraph(new Phrase("            Closing Balance: ", fontR));
			ppClosing.add(new Chunk(lastClosingBal, fontBold));
			document.add(ppClosing);
			
			
			
			document.close();
			writer.close();

			
		} catch (DocumentException e) {
			throw e;
		} catch (FileNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			
			try {
				if (outputstream !=null) {
					outputstream.close();
				}
				if (mcomCon != null) {
					mcomCon.close("UserPassbookController#createPDFFile");
					mcomCon = null;
				}
				if (log.isDebugEnabled()) {
					log.debug("process", " Exited ");
				}
				
			} catch (Exception e2) {				
				log.error("Error", e2);
			}
			

		}

		return filePath;
	}
	
	
	private Date addDay(Date dateNow) {
		
		
		Calendar call = Calendar.getInstance();
		call.setTime(dateNow);
		call.add(Calendar.DATE, 1);
		return call.getTime();
	}

}
