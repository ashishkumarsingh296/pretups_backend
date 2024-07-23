package com.restapi.c2s.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CACknowledgePDFResponse;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRptUIConsts;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * 
 * @author Subesh KCV
 *
 */
@Service("O2CTransferAckwledgePDFReportGen")
public class O2CTransferAckwledgePDFReportGen extends CommonService {
	protected final Log log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();
	

	
	public O2CACknowledgePDFResponse generatePDF(ChannelTransferVO channelTransferVO) throws IOException {
		final String methodName="generatePDF";
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		String emptyRow[]= {" "," "," "," "," "," "};
		
		//Document document = new Document(PageSize.A4, 0f, 0f, 0f, 0f);
		String fileContentString=null;
		Document document = new Document();
		String offlineDownloadLocation = SystemPreferences.OFFLINERPT_DOWNLD_PATH;
		//String offlineDownloadLocation = "D:\\";
		String fileName ="O2CACkDetails" + System.currentTimeMillis();
		String fileType =".pdf";
		
		Date currentDate = new Date();
		String reportDate =BTSLDateUtil.getGregorianDateInString(BTSLDateUtil.getLocaleDateTimeFromDate(currentDate));
		
		StringBuffer sb = new StringBuffer();
		sb.append(offlineDownloadLocation);
		sb.append(fileName);
		sb.append(fileType);
		String filePath =sb.toString();
		File file = new File(filePath);
		O2CACknowledgePDFResponse o2CACknowledgePDFResponse = new O2CACknowledgePDFResponse();
		o2CACknowledgePDFResponse.setFileName(fileName);
		o2CACknowledgePDFResponse.setFileType(fileType);
		LinkedHashMap<String, String> mp = new LinkedHashMap();
		
		float totalRequestedQuantity =0.0f;
		Double totalApprovedQuantityLevel1=0d;
		Double totalApprovedQuantityLevel2=0d;
		Long totalApprovedQuantityLevel3=0l;
		float totalCommssionAmount=0l;
		float totalTax1Amount=0l;
		float totalTax2Amount=0l;
		Long totalCBCAmount=0l;
		float totalTDSAmount=0l;
		float totalReceivedCreditQnty=0.0f;
		Float totalDenominationAmt=0.0f;
		Long totalPayableAmount=0l;
		Long totalNetPayableAmount=0l;
		FileOutputStream fs = null;
		PdfWriter writer = null;




		try
		
		{

			fs = new FileOutputStream(filePath);
			writer = PdfWriter.getInstance(document, fs );
			document.open();
			Font sectionHeader = new Font();
			sectionHeader.setStyle(Font.BOLD); 
			sectionHeader.setSize(8);  
			Font myfont = new Font();
     		  myfont.setStyle(Font.NORMAL); 
			  myfont.setSize(6);  
			  Font headerFont = new Font(); 
			  headerFont.setStyle(Font.BOLD);   
			  headerFont.setSize(6);  
			PdfPTable table = new PdfPTable(5); // 3 columns.
			table.setWidthPercentage(100); //Width 100%
			table.setSpacingBefore(10f); //Space before table
			table.setSpacingAfter(10f); //Space after table
			table.getDefaultCell().setBorder(Rectangle.BOX);  
			PdfPCell cell=null;
			//Set Column widths 
			float[] columnWidths = {1f, 1f, 1f,1f,1f}; 
			table.setWidths(columnWidths);
			
			String o2crowHeader[]= {"O2C transfer acknowledgement details ","","","",""};
			
			for(int i=0;i<o2crowHeader.length;i++) {
			cell = new PdfPCell(new Paragraph(o2crowHeader[i],sectionHeader));
			cell.setColspan(5);     
			cell.setBorder(Rectangle.NO_BORDER); 
			cell.setBorderColor(BaseColor.BLUE);
			cell.setPaddingLeft(10); 
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			}
			
			
			String row1[]= {"Transaction Details","","","",""};
			String reportDateLabel =RestAPIStringParser.getMessage(locale,
					"REPORT_DATE", null);
			for(int i=0;i<row1.length;i++) {
				if (i==3) {
			   	cell = new PdfPCell(new Paragraph(reportDateLabel,sectionHeader));		
				}else if(i==4) {
					cell = new PdfPCell(new Paragraph(reportDate,sectionHeader));	
				}else {
					cell = new PdfPCell(new Paragraph(row1[i],sectionHeader));
				}
			cell.setBorder(Rectangle.BOTTOM); 
			cell.setBorderColor(BaseColor.BLACK);
			cell.setPaddingLeft(10); 
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			}
			
			
			String row2[]= {"TRANSFER NUMBER","GEGRAPHICAL DOMAIN","DOMAIN","CATEGORY","TRANSFER SUB TYPE"};
			
			for(int i=0;i<row2.length;i++) {
			cell = new PdfPCell(new Paragraph(row2[i],headerFont));
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setBorderColor(BaseColor.BLUE);
			cell.setPaddingLeft(10);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			}
			
			
			String row3[]= new String[5];
			row3[0]=channelTransferVO.getTransferID();
			row3[1]=channelTransferVO.getGrphDomainCodeDesc();
			row3[2]=channelTransferVO.getDomainCodeDesc();
			row3[3]=channelTransferVO.getReceiverCategoryDesc();
			row3[4]=channelTransferVO.getTransferSubTypeAsString();
			for(int i=0;i<row3.length;i++) {
			cell = new PdfPCell(new Paragraph(row3[i],myfont));
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setBorderColor(BaseColor.BLUE);
			cell.setPaddingLeft(10);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			}
			
			
String row4[]= {"USER NAME","MOBILE NUMBER","ADDRESS","GRADE","EXTERNAL CODE"};
			
			for(int i=0;i<row4.length;i++) {
			cell = new PdfPCell(new Paragraph(row4[i],headerFont));
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setBorderColor(BaseColor.BLUE);
			cell.setPaddingLeft(10);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			}

			String row5[]= new String[5];
			row5[0]=channelTransferVO.getTransferInitatedByName();
			row5[1]=channelTransferVO.getUserMsisdn();
			row5[2]=channelTransferVO.getFullAddress();
			row5[3]=channelTransferVO.getReceiverGradeCode();
			row5[4]=channelTransferVO.getErpNum();
			for(int i=0;i<row5.length;i++) {
			cell = new PdfPCell(new Paragraph(row5[i],myfont));
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setBorderColor(BaseColor.BLUE);
			cell.setPaddingLeft(10);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			}

String row6[]= {"EXTERNAL TRANSACTION NUMBER","EXTERNAL TRANSACTION DATE","COMMISSION PROFILE","TRANSFER PROFILE","TRANSFER DATE"};
			
			for(int i=0;i<row6.length;i++) {
			cell = new PdfPCell(new Paragraph(row6[i],headerFont));
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setBorderColor(BaseColor.BLUE);
			cell.setPaddingLeft(10);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			}

			String row7[]= new String[5];
			row7[0]=channelTransferVO.getExternalTxnNum();
			row7[1]=channelTransferVO.getExternalTxnDateAsString();
			row7[2]=channelTransferVO.getCommProfileName();
			row7[3]=channelTransferVO.getReceiverTxnProfileName();
			row7[4]=channelTransferVO.getTransferDateAsString();
			for(int i=0;i<row7.length;i++) {
			cell = new PdfPCell(new Paragraph(row7[i],myfont));
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setBorderColor(BaseColor.BLUE);
			cell.setPaddingLeft(10);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			}
			
			
			
			String row8[]= {"TRANSFER CATEGORY","REFERENCE NUMBER","TRANSACTION STATUS","ORDER DATE","REPORT CODE"};
			
			for(int i=0;i<row8.length;i++) {
			cell = new PdfPCell(new Paragraph(row8[i],headerFont));
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setBorderColor(BaseColor.BLUE);
			cell.setPaddingLeft(10);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			}

			String row9[]= new String[5];
			row9[0]=channelTransferVO.getTransferCategory();
			row9[1]=channelTransferVO.getReferenceNum();
			row9[2]=channelTransferVO.getStatus();
//			 if(channelTransferVO.getCloseDate()!=null) {
//				 row9[3]=String.valueOf(channelTransferVO.getCloseDate());	 
//			 }else {
//				 row9[3]=" ";
//			 }
			row9[3]=channelTransferVO.getCreatedOnAsString();
			row9[4]="O2CTRFACK01";
			for(int i=0;i<row9.length;i++) {
			cell = new PdfPCell(new Paragraph(row9[i],myfont));
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setBorderColor(BaseColor.BLUE);
			cell.setPaddingLeft(10);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			}

			
     String row10[]= {"NETWORK NAME","","","",""};
			
			for(int i=0;i<row10.length;i++) {
				cell = new PdfPCell(new Paragraph(row10[i],headerFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setBorderColor(BaseColor.BLUE);
				cell.setPaddingLeft(10);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);
			}

			String row11[]= new String[5];
			row11[0]=channelTransferVO.getNetworkName();
			row11[1]="";
			row11[2]="";
			row11[3]="";
			row11[4]="";
			for(int i=0;i<row11.length;i++) {
				cell = new PdfPCell(new Paragraph(row11[i],myfont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setBorderColor(BaseColor.BLUE);
				cell.setPaddingLeft(10);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);
			}
			
			PdfPTable table2 = new PdfPTable(6); // 3 columns.
			table2.setWidthPercentage(100); //Width 100%
			table2.setSpacingBefore(10f); //Space before table
			table2.setSpacingAfter(10f); //Space after table 
			table2.getDefaultCell().setBorder(PdfPCell.RECTANGLE); 
			//Set Column widths
			float[] columnWidths2 = {1f, 1f, 1f,1f,1f,1f}; 
			table2.setWidths(columnWidths2);
			
			String productsSection[]= {"Product details","","","","",""};
			for(int i=0;i<productsSection.length;i++) {
			cell = new PdfPCell(new Paragraph(productsSection[i],sectionHeader));
			cell.setBorder(Rectangle.BOTTOM); 
			cell.setBorderColor(BaseColor.BLACK);
			cell.setPaddingLeft(10); 
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table2.addCell(cell);
			}
			
			
			
			for(int i=0;i<PretupsRptUIConsts.SIX.getNumValue();i++) {
				cell = commonFill(emptyRow[i], sectionHeader);
				table2.addCell(cell);
			}
			List<ChannelTransferItemsVO> channelTransferItemVOList =channelTransferVO.getChannelTransferitemsVOList();
			String productDetails =RestAPIStringParser.getMessage(locale,
					"o2cAcknowledge_label_productDetails", null);
			cell = commonFill(productDetails, headerFont);
			cell.setColspan(2);
			table2.addCell(cell);
			for(ChannelTransferItemsVO channelTransferItemsVO:channelTransferItemVOList) {
				cell = commonFill(channelTransferItemsVO.getProductName(), headerFont);
				table2.addCell(cell);
				
				if(!BTSLUtil.isNullorEmpty(channelTransferItemsVO.getRequestedQuantity())) {
					totalRequestedQuantity =totalRequestedQuantity+Float.valueOf(channelTransferItemsVO.getProductCost());
					}			
					if(channelTransferItemsVO.getFirstApprovedQuantity()!=null) {
							totalApprovedQuantityLevel1=totalApprovedQuantityLevel1+Double.valueOf(channelTransferItemsVO.getFirstApprovedQuantity());
						}
						
					if(channelTransferItemsVO.getSecondApprovedQuantity()!=null) {
						totalApprovedQuantityLevel2=totalApprovedQuantityLevel2+Double.valueOf(channelTransferItemsVO.getSecondApprovedQuantity());
					}

					if(channelTransferItemsVO.getThirdApprovedQuantity()!=null) {
						totalApprovedQuantityLevel3=totalApprovedQuantityLevel3+Long.valueOf(channelTransferItemsVO.getThirdApprovedQuantity());
					}
											
					if(channelTransferItemsVO.getTax1ValueAsString()!=null) {
						totalTax1Amount=totalTax1Amount+Float.valueOf(channelTransferItemsVO.getTax1ValueAsString());
					}

					if(channelTransferItemsVO.getTax1RateAsString()!=null) {
												totalTax1Amount=totalTax1Amount+Float.valueOf(channelTransferItemsVO.getTax2ValueAsString());
											}


					if(channelTransferItemsVO.getOtfAmount()!=0l) {
											totalCBCAmount=totalCBCAmount+Long.valueOf(channelTransferItemsVO.getOtfAsString());
											}
					if(channelTransferItemsVO.getReceiverCreditQtyAsString()!=null) {
						totalReceivedCreditQnty=totalReceivedCreditQnty+Float.valueOf(channelTransferItemsVO.getReceiverCreditQtyAsString());
					}
					
					if(channelTransferItemsVO.getUnitValueAsString()!=null) {
						totalDenominationAmt=totalDenominationAmt+Float.valueOf(channelTransferItemsVO.getProductMrpStr());
					}

					if(channelTransferItemsVO.getPayableAmountAsString()!=null) {
						totalPayableAmount=totalPayableAmount + channelTransferItemsVO.getPayableAmount();
					}
					if(channelTransferItemsVO.getNetPayableAmountAsString()!=null) {
						totalNetPayableAmount=totalNetPayableAmount + channelTransferItemsVO.getNetPayableAmount();
			        }
					
					if(channelTransferItemsVO.getCommAsString()!=null) {
						totalCommssionAmount=totalCommssionAmount + Float.valueOf(channelTransferItemsVO.getCommAsString());
			        }

				
			}
			
			String dummyText=" ";
			int totProducts =channelTransferItemVOList.size();
			 if(totProducts>0) {
				 int remainingEmptycolums = (PretupsRptUIConsts.FOUR.getNumValue() -totProducts);  // 1 for "Product details text"
				 for(int i=1 ;i<=remainingEmptycolums;i++) {
						cell = commonFill(dummyText, headerFont);
						table2.addCell(cell);
				 }
			 }
			
			 
			 LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<String,String>(); // <attributeName,methodName>
			 linkedHashMap.put("_productCode", "getProductCode");
			 linkedHashMap.put("_productShortCode", "getProductShortCode");
			 linkedHashMap.put("_unitValue","getUnitValueAsString");
			 linkedHashMap.put("_requestedQuantity", "getRequestedQuantity");
			 linkedHashMap.put("_firstApprovedQuantity", "getFirstApprovedQuantity");
			 linkedHashMap.put("_secondApprovedQuantity", "getSecondApprovedQuantity");
			 linkedHashMap.put("_thirdApprovedQuantity", "getThirdApprovedQuantity");
			 linkedHashMap.put("_tax1Rate", "getTax1RateAsString");
			 linkedHashMap.put("_tax1Value", "getTax1ValueAsString");
			 linkedHashMap.put("_tax1Type", "getTax1Type");
			 linkedHashMap.put("_tax2Rate", "getTax2RateAsString");
			 linkedHashMap.put("_tax2Value", "getTax2ValueAsString");
			 linkedHashMap.put("_tax2Type", "getTax2Type");
			 linkedHashMap.put("_commRate", "getCommRate");
			 linkedHashMap.put("_commValue","getCommValue");
			 linkedHashMap.put("commType","getCommType");
			 linkedHashMap.put("otfRate","getOtfRateAsString");
			 linkedHashMap.put("otfAmount","getOtfAsString");
			 linkedHashMap.put("otfTypePctOrAMt","getOtfTypePctOrAMt");
			 linkedHashMap.put("_tax3Value","getTax3ValueAsString");
			 linkedHashMap.put("_receiverCreditQty","getReceiverCreditQtyAsString");
			 linkedHashMap.put("_productMrpStr", "getProductMrpStr");
			 linkedHashMap.put("_payableAmount","getPayableAmountAsString");
			 linkedHashMap.put("_netPayableAmount","getNetPayableAmountAsString");
			 
			 /*
			 HashMap<String,String> rightAlignmentMAP = new HashMap<String,String>(); // <attributeName,methodName>
			 rightAlignmentMAP.put("_productShortCode", "getProductShortCode");
			 rightAlignmentMAP.put("_unitValue","getUnitValueAsString");
			 rightAlignmentMAP.put("_requestedQuantity", "getRequestedQuantity");
			 rightAlignmentMAP.put("_firstApprovedQuantity", "getFirstApprovedQuantity");
			 rightAlignmentMAP.put("_secondApprovedQuantity", "getSecondApprovedQuantity");
			 rightAlignmentMAP.put("_thirdApprovedQuantity", "getThirdApprovedQuantity");
			 rightAlignmentMAP.put("_tax1Rate", "getTax1RateAsString");
			 rightAlignmentMAP.put("_tax1Value", "getTax1ValueAsString");
			 rightAlignmentMAP.put("_tax2Rate", "getTax2RateAsString");
			 rightAlignmentMAP.put("_tax2Value", "getTax2ValueAsString");
			 rightAlignmentMAP.put("_commRate", "getCommRate");
			 rightAlignmentMAP.put("_commValue","getCommValue");
			 rightAlignmentMAP.put("otfRate","getOtfRateAsString");
			 rightAlignmentMAP.put("otfAmount","getOtfAsString");
			 rightAlignmentMAP.put("_tax3Value","getTax3ValueAsString");
			 rightAlignmentMAP.put("_receiverCreditQty","getReceiverCreditQtyAsString");
			 rightAlignmentMAP.put("_productMrpStr", "getProductMrpStr");
			 rightAlignmentMAP.put("_payableAmount","getPayableAmountAsString");
			 rightAlignmentMAP.put("_netPayableAmount","getNetPayableAmountAsString"); */
			 
			 
			 
			 Set<String> keys = linkedHashMap.keySet();
			 CommonUtil commonUtil = new CommonUtil();
		        // printing the elements of LinkedHashMap
			 String propKey=null;
			 String objectFieldValue = null;
			 String sideHeader=null;
			 String sideColData=null;
			 
		        for (String key : keys) {
		            System.out.println(key + " -- "
		                               + linkedHashMap.get(key));
		            propKey=PretupsI.O2CACKNOWLEDGE_LABEL_PREFIX+key;
		             sideHeader =  RestAPIStringParser.getMessage(locale,
		            		propKey, null);
		            cell = commonFill(sideHeader, myfont);
		            cell.setColspan(2);
					table2.addCell(cell);
		            for(ChannelTransferItemsVO channelTransferItemsVO:channelTransferItemVOList) {
		            	 objectFieldValue= commonUtil.invokeMethodofClass(channelTransferItemsVO,linkedHashMap.get(key));
		            	 if (objectFieldValue!=null) {
		            		 sideColData=objectFieldValue;
		            	 }else {
		             		 sideColData=" ";
		            	 }
		            	 cell = commonFillNoGray(sideColData, myfont);
//		            	  if(rightAlignmentMAP.containsKey(key)) {
//		            		  cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//		          		  }else {
		            		  cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		          		  //}
		            	  cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						table2.addCell(cell);
					}
					 for(int i=1 ;i<=(PretupsRptUIConsts.FOUR.getNumValue() -totProducts);i++) {
							cell = commonFill(dummyText, sectionHeader);
							table2.addCell(cell);
					 }

		            propKey=null;
		        }
			
			
			mp.put(PretupsI.TOTALREQUESTEDCREDITQUANTITY,totalRequestedQuantity+"");
			mp.put(PretupsI.TOTAPPRV_QNNTY_LEVEL1,totalApprovedQuantityLevel1+"");
			mp.put(PretupsI.TOTAPPRV_QNNTY_LEVEL2,totalApprovedQuantityLevel2+"");
			mp.put(PretupsI.TOTAPPRV_QNNTY_LEVEL3,totalApprovedQuantityLevel3+"");
			mp.put(PretupsI.TOT_TAX1_AMOUNT,totalTax1Amount+"");
			mp.put(PretupsI.TOT_TAX2_AMOUNT,totalTax2Amount+"");
			mp.put(PretupsI.TOT_CBC_AMOUNT,totalCBCAmount+"");
			mp.put(PretupsI.TOT_COMM_AMOUNT,totalCommssionAmount+"");
			mp.put(PretupsI.TOTALTDS,totalTDSAmount+"");
			mp.put(PretupsI.TOT_RECV_CREDIT_QNTY,totalReceivedCreditQnty+"");
			mp.put(PretupsI.TOT_DENOM_AMNT,totalDenominationAmt+"");
			mp.put(PretupsI.TOT_PAYABLE_AMNT,PretupsBL.getDisplayAmount(totalPayableAmount)+"");
			mp.put(PretupsI.TOT_NET_PAYABLE_AMNT,PretupsBL.getDisplayAmount(totalNetPayableAmount)+"");
		HashMap<String,String> labelMap = new HashMap();	
		labelMap.put(PretupsI.TOTALREQUESTEDCREDITQUANTITY,"Total requested quantity");
		labelMap.put(PretupsI.TOTAPPRV_QNNTY_LEVEL1,"Total approval quantity level1");
		labelMap.put(PretupsI.TOTAPPRV_QNNTY_LEVEL2,"Total approval quantity level2");
		labelMap.put(PretupsI.TOTAPPRV_QNNTY_LEVEL3,"Total approval quantity level3");
		labelMap.put(PretupsI.TOT_TAX1_AMOUNT,"Total tax1 amount");
		labelMap.put(PretupsI.TOT_TAX2_AMOUNT,"Total tax2 amount");
		labelMap.put(PretupsI.TOT_TAX2_AMOUNT,"Total tax2 amount");
		labelMap.put(PretupsI.TOT_COMM_AMOUNT,"Total commission amount");
		labelMap.put(PretupsI.TOT_CBC_AMOUNT,"Total CBC amount");
		labelMap.put(PretupsI.TOTALTDS,"Total TDS");
		labelMap.put(PretupsI.TOT_RECV_CREDIT_QNTY,"Total received credit quantity");
		labelMap.put(PretupsI.TOT_DENOM_AMNT,"Total denomination amount");
		labelMap.put(PretupsI.TOT_PAYABLE_AMNT,"Total payable amount");
		labelMap.put(PretupsI.TOT_NET_PAYABLE_AMNT,"Total net payable amount");
		
		for(int i=0;i<PretupsRptUIConsts.SIX.getNumValue();i++) {
			cell = commonFill(emptyRow[i], sectionHeader);
			table2.addCell(cell);
		}	
					
//		Totals section			
			String totalsSection[]= {"Totals section","","","","",""};
			for(int i=0;i<totalsSection.length;i++) {
			cell = new PdfPCell(new Paragraph(totalsSection[i],sectionHeader));
			cell.setBorder(Rectangle.NO_BORDER); 
			cell.setBorderColor(BaseColor.BLACK);
			cell.setPaddingLeft(10); 
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table2.addCell(cell);
			}
		
			
			Iterator<Entry<String, String>> mpIterator
            = mp.entrySet().iterator();
 
        // Iterating every set of entry in the HashMap
        while (mpIterator.hasNext()) {
        	Map.Entry<String, String> new_Map
            = (Map.Entry<String, String>)
            		mpIterator.next();
 
            // Displaying HashMap
        	if(log.isDebugEnabled()) {
        	log.debug(methodName,new_Map.getKey() + " = "
                               + new_Map.getValue());
        	}
            
            //total summary label section
            cell = new PdfPCell(new Paragraph(labelMap.get(new_Map.getKey()),myfont));
            cell.setColspan(2);
			cell.setBorder(Rectangle.BOX);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setBorderColor(BaseColor.BLACK);
			cell.setPaddingLeft(10); 
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table2.addCell(cell);  
			//total summary data section
			 cell = new PdfPCell(new Paragraph(mp.get(new_Map.getKey()),myfont)); 
	            cell.setColspan(2); 
	            cell.setBorder(Rectangle.BOX); 
				cell.setBorderColor(BaseColor.BLACK);
				cell.setPaddingLeft(10); 
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table2.addCell(cell);
			String dummy="";
			
			 cell = new PdfPCell(new Paragraph(dummy,myfont)); 
	            cell.setColspan(2); 
	            cell.setBorder(Rectangle.NO_BORDER); 
				cell.setBorderColor(BaseColor.BLACK);
				cell.setPaddingLeft(10); 
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table2.addCell(cell);
			
        }
        
        for(int i=0;i<PretupsRptUIConsts.SIX.getNumValue();i++) {
			cell = commonFill(emptyRow[i], sectionHeader);
			table2.addCell(cell);
		}		
			
			String paymentsSection[]= {"Payment details","","","","",""};
			for(int i=0;i<paymentsSection.length;i++) {
			cell = new PdfPCell(new Paragraph(paymentsSection[i],sectionHeader));
			cell.setBorder(Rectangle.BOTTOM); 
			cell.setBorderColor(BaseColor.BLACK);
			cell.setPaddingLeft(10); 
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table2.addCell(cell);
			}
						


			
// Payment details section			
String row12[]= {"Payment mode","Payment instrument number","Payment instrument date","Payment instument amount","",""};
			
			for(int i=0;i<row12.length;i++) {
				cell = new PdfPCell(new Paragraph(row12[i],headerFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setBorderColor(BaseColor.BLUE);
				cell.setPaddingLeft(10);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table2.addCell(cell);
			}

			String row13[]= new String[6];
			row13[0]=channelTransferVO.getPayInstrumentType();
			row13[1]=channelTransferVO.getPayInstrumentNum();
			row13[2]=channelTransferVO.getPayInstrumentDateAsString();
			row13[3]=PretupsBL.getDisplayAmount(channelTransferVO.getPayInstrumentAmt());
			row13[4]="";
			row13[5]="";
			for(int i=0;i<row13.length;i++) {
				cell = new PdfPCell(new Paragraph(row13[i],myfont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setBorderColor(BaseColor.BLUE);
				cell.setPaddingLeft(10);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table2.addCell(cell);
			}

			
			if(channelTransferVO.getVoucherDetails()!=null) {
				for(int i=0;i<PretupsRptUIConsts.SIX.getNumValue();i++) {
					cell = commonFill(emptyRow[i], sectionHeader);
					table2.addCell(cell);
				}	
				// Voucher Details
				String voucherDetailsSection[]= {"Voucher details","","","","",""};
				for(int i=0;i<voucherDetailsSection.length;i++) {
				cell = new PdfPCell(new Paragraph(voucherDetailsSection[i],sectionHeader));
				cell.setBorder(Rectangle.BOTTOM); 
				cell.setBorderColor(BaseColor.BLACK);
				cell.setPaddingLeft(10); 
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table2.addCell(cell);
				}
	
				
				
			List<VomsBatchVO> voucherDetailsList =channelTransferVO.getVoucherDetails();
			
			
			//Voucher details 
			for(VomsBatchVO vomsBatchVO:voucherDetailsList) { 	
			// Voucher details details section			
			String voucherlHeader1[]= {"Batch no","Batch type","Voucher Segment","Voucher Type","Voucher denomination","Voucher Profile"};
						for(int i=0;i<voucherlHeader1.length;i++) {
							cell = new PdfPCell(new Paragraph(voucherlHeader1[i],headerFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setBorderColor(BaseColor.BLACK);
							cell.setPaddingLeft(10);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							table2.addCell(cell);
						}
						String voucherlHeader1Data[]= new String[6];
						voucherlHeader1Data[0]=vomsBatchVO.getBatchNo();
						voucherlHeader1Data[1]=vomsBatchVO.getBatchTypeDesc();
						voucherlHeader1Data[2]=vomsBatchVO.getVouchersegment();
						voucherlHeader1Data[3]=vomsBatchVO.getVoucherType();
						voucherlHeader1Data[4]=vomsBatchVO.getDenomination();
						voucherlHeader1Data[5]=vomsBatchVO.getProductName();
						for(int i=0;i<voucherlHeader1Data.length;i++) {
							cell = new PdfPCell(new Paragraph(voucherlHeader1Data[i],myfont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setBorderColor(BaseColor.BLUE);
							cell.setPaddingLeft(10);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							table2.addCell(cell);
						}
						
						String voucherSerialNoHeader[]= {"Total no of Vouchers","From serial no","To serial no","","",""};
						for(int i=0;i<voucherSerialNoHeader.length;i++) {  
							cell = new PdfPCell(new Paragraph(voucherSerialNoHeader[i],headerFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setBorderColor(BaseColor.BLACK);
							cell.setPaddingLeft(10);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							table2.addCell(cell);
						}
						String voucherSerialNoHeaderData[]= new String[6];
						voucherSerialNoHeaderData[0]=vomsBatchVO.getTotalVoucherPerOrder()+"";
						voucherSerialNoHeaderData[1]=vomsBatchVO.getFromSerialNo();
						voucherSerialNoHeaderData[2]=vomsBatchVO.getToSerialNo();
						voucherSerialNoHeaderData[3]="";
						voucherSerialNoHeaderData[4]="";
						voucherSerialNoHeaderData[5]="";
						for(int i=0;i<voucherSerialNoHeaderData.length;i++) {
							cell = new PdfPCell(new Paragraph(voucherSerialNoHeaderData[i],myfont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setBorderColor(BaseColor.BLUE);
							cell.setPaddingLeft(10);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE); 
							table2.addCell(cell);
						}

		
						

			}

			}
			
			for(int i=0;i<PretupsRptUIConsts.SIX.getNumValue();i++) {
				cell = commonFill(emptyRow[i], sectionHeader);
				table2.addCell(cell);
			}	
			String remarkSection[]= {"Remarks","","","","",""};
			for(int i=0;i<remarkSection.length;i++) {
			cell = new PdfPCell(new Paragraph(remarkSection[i],sectionHeader));
			cell.setBorder(Rectangle.BOTTOM); 
			cell.setBorderColor(BaseColor.BLACK);
			cell.setPaddingLeft(10); 
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table2.addCell(cell);
			}
			
			
//remarks			
			String rowRemarks[]= {"Remarks","First approver remarks","Second approver remarks","Third approver remarks","",""};
						
						for(int i=0;i<rowRemarks.length;i++) {
							cell = new PdfPCell(new Paragraph(rowRemarks[i],headerFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setBorderColor(BaseColor.BLUE);
							cell.setPaddingLeft(10);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							table2.addCell(cell);
						}

						String rowRemarksData[]= new String[6];
						rowRemarksData[0]=channelTransferVO.getChannelRemarks();
						rowRemarksData[1]=channelTransferVO.getFirstApprovalRemark();
						rowRemarksData[2]=channelTransferVO.getSecondApprovalRemark();
						rowRemarksData[3]=channelTransferVO.getThirdApprovalRemark();
						rowRemarksData[4]="";
						rowRemarksData[5]="";
						for(int i=0;i<rowRemarksData.length;i++) {
							cell = new PdfPCell(new Paragraph(rowRemarksData[i],myfont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setBorderColor(BaseColor.BLUE);
							cell.setPaddingLeft(10);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							table2.addCell(cell);
						}

			
			
			document.add(table);
			document.add(table2);
			
	
			
			document.close();
			writer.close();
			
			byte[] fileContent = Files.readAllBytes(file.toPath());
	        fileContentString = Base64.getEncoder().encodeToString(fileContent);
	        o2CACknowledgePDFResponse.setFileData(fileContentString);
			
		} catch (Exception e)
		{
			log.error(methodName,"Error occured while generating PDF");
		}
		finally {
			if(writer!=null) {
				writer.close();
			}
            fs.close();
        }
		return o2CACknowledgePDFResponse;
		
		
	}
	
	public PdfPCell commonFill(String data, Font fontvalue) {
		PdfPCell cell = new PdfPCell(new Paragraph(data,fontvalue));
		if(data.trim().length()==0) {
			cell.setBorder(Rectangle.NO_BORDER);
		}else {
			cell.setBorder(Rectangle.BOX);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		}	
			cell.setBorderColor(BaseColor.BLACK);
			
			cell.setPaddingLeft(10); 
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	return cell;
	}
	
	public PdfPCell commonFillNoGray(String data, Font fontvalue) {
		PdfPCell cell = new PdfPCell(new Paragraph(data,fontvalue));
//		if(data.trim().length()==0) {
//			cell.setBorder(Rectangle.NO_BORDER);
//		}else {
//			cell.setBorder(Rectangle.BOX);
//		}	
		cell.setBorder(Rectangle.BOX);
			cell.setBorderColor(BaseColor.BLACK);
			cell.setPaddingLeft(10); 
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	return cell;
	}
}