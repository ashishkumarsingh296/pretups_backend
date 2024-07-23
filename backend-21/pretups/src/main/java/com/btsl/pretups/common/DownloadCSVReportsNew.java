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
import com.btsl.common.TypesI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.StaffSelfC2CQuery;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author mohit.miglani
 *
 */
public class DownloadCSVReportsNew {
	public static final Log log = LogFactory.getLog(DownloadCSVReports.class
			.getName());
	private static String path = Constants.getProperty("REPORTS_DOWNLOAD_PATH");
	private StaffSelfC2CQuery staffSelfC2CQuery;
	/**
	 * DownloadCSVReportsNew() constructor
	 */
	public DownloadCSVReportsNew() {
		
		staffSelfC2CQuery=(StaffSelfC2CQuery) ObjectProducer
				.getObject(QueryConstants.STAFF_C2C_REPORT_QRY,
						QueryConstants.QUERY_PRODUCER);

		}
	
	/**
	 * @param usersReportModel
	 * @param rptCode
	 * @param con
	 * @return
	 * @throws InterruptedException
	 */
	public String prepareData(UsersReportModel usersReportModel,
			String rptCode, Connection con) throws InterruptedException {
		if (log.isDebugEnabled())
			log.debug("prepareData Entered: ", rptCode);
		String filePath ;
		switch (rptCode) {
		
		case "STFSLFC2C01":
			filePath = prepareDatastaffSelfC2CReport(usersReportModel, con);
			break;
		
			default: filePath = "Invalid filePath";
            break;
		}
		return filePath;

	}
	/**
	 * @param csvReportReader
	 * @param csvReportWriter
	 * @throws InterruptedException
	 *             Note Generic method will be use in multiple location in
	 *             download reports
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
	/**
	 * @param usersReportModel
	 * @param con
	 * @return
	 */

	public String prepareDatastaffSelfC2CReport(
			UsersReportModel usersReportModel, Connection con) {
		final String methodName = "prepareDatastaffSelfC2CReport";

		PreparedStatement pstmt = null;
		if (log.isDebugEnabled()) {
			log.debug(methodName, "usersReportModel.getUserType()="+usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";

		try {
			fileName = "StaffSelfC2CReport"
					+ BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";

			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}

			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);

			if (TypesI.CHANNEL_USER_TYPE.equalsIgnoreCase(usersReportModel
					.getUserType())) {
				pstmt = staffSelfC2CQuery.loadStaffSelfC2CChannelReportQry(usersReportModel, con);
						
				MutableBoolean mutableBoolean = new MutableBoolean(false);
				MutableBoolean filewritten = new MutableBoolean(false);
				StringBuilder sb =  new StringBuilder(1024);
				final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.staff.label.heading")).append(" ; ").append("")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.rptcode")).append(" : ")
						.append( usersReportModel.getrptCode()).append(" , ")

						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.networkname")).append(" : ")
						.append( usersReportModel.getNetworkName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.zone")).append(" : ")
						.append( usersReportModel.getZoneName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.domain")).append(" : ")
						.append( usersReportModel.getDomainName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.date")).append(" : ")
						.append( usersReportModel.getCurrentDate()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.cat")).append(" : ")
						.append( usersReportModel.getCategoryName()).toString();
						
				sb.setLength(0);
				final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.activeuserid")).append( ",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.touserid")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.fromuser")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.touser")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.transferid")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.transfersubtype")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.type")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.closedate")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.productname")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.transfermrp")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.payableamt")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.netpayableamt")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.status")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.mrp")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.commision")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.commisionquan")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.reccreditquant")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.sendebitquant")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.tax3")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.tax1")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.tax2")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.sencatcode")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.reccatcode")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.reccatname")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.source")).toString();

				final String[] resultSetColumnName = { "ACTIVE_USER_ID",
						"TO_USER_ID", "FROM_USER", "TO_USER",
						"TRANSFER_ID", "TRANSFER_SUB_TYPE", "TYPE", "CLOSE_DATE",
						"PRODUCT_NAME", "TRANSFER_MRP", "PAYABLE_AMOUNT",
						"NET_PAYABLE_AMOUNT", "STATUS", "MRP","COMMISION","COMMISION_QUANTITY","RECEIVER_CREDIT_QUANTITY","SENDER_DEBIT_QUANTITY","TAX3_VALUE","TAX1_VALUE",
						"TAX2_VALUE","SENDER_CATEGORY_CODE","RECEIVER_CATEGORY_CODE","RECEIVER_CATEGORY_NAME","SOURCE"};

				CSVReportWriter csvWriter = new CSVReportWriter(queue,
						filePath, fileName, mutableBoolean, filewritten,
						columnHeader, reportTopHeaders);
				CSVReportReader reader = new CSVReportReader(queue, pstmt,
						mutableBoolean, resultSetColumnName);

				this.executeReaderWriterThread(reader, csvWriter);

			}

			
		} catch (InterruptedException | ParseException | SQLException e) {
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

	
	
	
	
	
	
	
	
	
	
	
}
