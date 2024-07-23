package com.btsl.pretups.common;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.LmsRedemptionRetWidRptQry;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.LmsRedemptionReportModel;

/**
 * @author sweta.verma
 *
 */
public class DownloadLMSReports{
	public static final Log log = LogFactory.getLog(DownloadLMSReports.class.getName());
	private static String c2cTrfDetails = Constants.getProperty("C2C_TRANSFER_DETAILS_REPORTS_DOWNLOAD_FILE_NAME");
	private static String lmsReportsDetails = Constants.getProperty("LMS_TRANSFER_DETAILS_REPORTS_DOWNLOAD_FILE_NAME");
	private static String path=Constants.getProperty("REPORTS_DOWNLOAD_PATH");
     
    private LmsRedemptionRetWidRptQry lmsRedemptionRetWidRptQry;
	
    /**
     * Constructor
     */
	public DownloadLMSReports(){
		lmsRedemptionRetWidRptQry = (LmsRedemptionRetWidRptQry) ObjectProducer.getObject(QueryConstants.LMS_REDEMPTION_RET_WID_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
		
	}
	
	/**
	 * 
	 * @param lmsRedemptionReportModel
	 * @param rptCode
	 * @param con
	 * @return
	 * @throws InterruptedException
	 */
	public String prepareData(LmsRedemptionReportModel lmsRedemptionReportModel, String rptCode, Connection con) throws InterruptedException
	{
		if (log.isDebugEnabled())
			log.debug("prepareData Entered: ", rptCode);
		String filePath = "";
		
			filePath = prepareDataLMSRept(lmsRedemptionReportModel,con);
			

		

		return filePath;
		
		

	}

	/**
	 * 
	 * @param lmsRedemptionReportModel
	 * @param con
	 * @return
	 * @throws InterruptedException
	 */
	private String prepareDataLMSRept(
			LmsRedemptionReportModel lmsRedemptionReportModel, Connection con) throws InterruptedException {
		PreparedStatement pstmt = null;
		String methodName = "prepareDataLMSRept";		
		if (log.isDebugEnabled()) {
			log.debug("prepareDataLMSRept", " " + lmsRedemptionReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";

		try {
			fileName = "lmsReportsDetails"
					+ BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";

			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}

			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);
			
				pstmt = lmsRedemptionRetWidRptQry.loadRedemptionRetWidQry(con, lmsRedemptionReportModel);
				
				MutableBoolean mutableBoolean = new MutableBoolean(false);
				MutableBoolean filewritten = new MutableBoolean(false);
				StringBuilder sb =  new StringBuilder(1024);
				final String reportTopHeaders = sb.append(PretupsRestUtil
						.getMessageString("pretups.c2c.reports.staff.label.heading")).append(" ; ").append("")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.rptcode")).append(" : ")
						.append( lmsRedemptionReportModel.getRptCode()).append(" , ")

						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.networkname")).append(" : ")
						.append( lmsRedemptionReportModel.getNetworkName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.zone")).append(" : ")
						.append( lmsRedemptionReportModel.getZoneName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.domain")).append(" : ")
						.append( lmsRedemptionReportModel.getDomainName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.date")).append(" : ")
						.append( lmsRedemptionReportModel.getCurrentDate()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.cat")).append(" : ")
						.append( lmsRedemptionReportModel.getCategoryName()).toString();
						
				sb.setLength(0);
				final String columnHeader = sb.append(PretupsRestUtil.getMessageString("lms.reports.table.header.userName")).append( ",")
						.append(PretupsRestUtil.getMessageString("lms.reports.table.header.msisdn")).append( ",")
						.append(PretupsRestUtil.getMessageString("lms.reports.table.header.redemption.id")).append( ",")
						.append(PretupsRestUtil.getMessageString("lms.reports.table.header.reference.id")).append( ",")
						.append(PretupsRestUtil.getMessageString("lms.reports.table.header.category")).append( ",")
						.append(PretupsRestUtil.getMessageString("lms.reports.table.header.geography")).append( ",")
						.append(PretupsRestUtil.getMessageString("lms.reports.table.header.product.name")).append( ",")
						.append(PretupsRestUtil.getMessageString("lms.reports.table.header.redemption.date")).append( ",")
						.append(PretupsRestUtil.getMessageString("lms.reports.table.header.redemption.by")).append( ",")
						.append(PretupsRestUtil.getMessageString("lms.reports.table.header.points.redeemed")).append( ",")
						.append(PretupsRestUtil.getMessageString("lms.reports.table.header.amount")).toString();
						
				final String[] resultSetColumnName = { "USER_NAME",
						"MSISDN", "REDEMPTION_ID", "REFERENCE_ID",
						"CATEGORY_NAME", "GRPH_DOMAIN_NAME", "PRODUCT_NAME", "REDEMPTION_DATE",
						"REDEMPTION_BY", "POINTS_REDEEMED", "AMOUNT_TRANSFERED"};

				CSVReportWriter csvWriter = new CSVReportWriter(queue,
						filePath, fileName, mutableBoolean, filewritten,
						columnHeader, reportTopHeaders);
				CSVReportReader reader = new CSVReportReader(queue, pstmt,
						mutableBoolean, resultSetColumnName);

				this.executeReaderWriterThread(reader, csvWriter);

			
		} catch (ParseException | SQLException e) {
			
			log.errorTrace(methodName, e);
		} finally {
			try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
		}
		return filePath + fileName;
	}

	/**
	 * 
	 * @param csvReportReader
	 * @param csvReportWriter
	 * @throws InterruptedException
	 */
	public void executeReaderWriterThread(CSVReportReader csvReportReader,
			CSVReportWriter csvReportWriter) throws InterruptedException {
		final String methodName = "executeReaderWriterThread";
		Thread w = new Thread(csvReportWriter, "Writer Thread");
		Thread r = new Thread(csvReportReader, "Reader Thread");
		w.setUncaughtExceptionHandler((thread, exception) -> {
			log.debug("Exception occurred in ", thread.getName());
			log.errorTrace(methodName, exception);
		});
		r.setUncaughtExceptionHandler((thread, exception) -> {
			log.debug("Exception occurred in ", thread.getName());
			log.errorTrace(methodName, exception);
		});

		log.debug("Starting thread to strat writing on the file", w.getName());
		long startTime = System.currentTimeMillis();
		w.start();
		log.debug("Starting thread to strat reading on the file", r.getName());
		r.start();
		w.join();
		long endTime = System.currentTimeMillis();
		log.debug(
				"Total time in reading data from DB and wrting data on csv file ",
				(endTime - startTime) + "miliseconds");
	}


	
	
	
	}
