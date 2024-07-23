package com.web.ota.bulkpush.businesslogic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListSorterUtil;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.bulkpush.businesslogic.BulkPushVO;
import com.btsl.ota.generator.ByteCodeGeneratorI;
import com.btsl.ota.services.businesslogic.ServicesVO;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.ota.util.OtaMessage;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class BulkPushWebDAO {

	private Log _log = LogFactory.getLog(this.getClass().getName());
	private String _jobName = null;

	private static BulkPushWebQry bulkPushWebQry;

	public BulkPushWebDAO() {
		bulkPushWebQry = (BulkPushWebQry) ObjectProducer
				.getObject(QueryConstants.BULK_PUSH_WEB_QRY,
						QueryConstants.QUERY_PRODUCER);
	}

	/**
	 * This method is used to get mobile no. of those whose sim image differ
	 * 
	 * @param con
	 *            of Connection type
	 * @param p_locCode
	 *            of String type
	 * @param p_userType
	 *            of String type
	 * @param p_profile
	 *            of String type
	 * @param p_compareList
	 *            of ArrayList type
	 * @return returns the ArrayList
	 * @exception BTSLBaseException
	 */

	public ArrayList getUnmatchedProfileList(Connection p_con,
			String p_locCode, String p_userType, String p_profile,
			ArrayList p_compareList) throws BTSLBaseException {
		final String methodName = "getUnmatchedProfileList";
		StringBuilder loggerValue= new StringBuilder(); 
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append(" Entered type=" );
			loggerValue.append(p_userType);
			loggerValue.append(",profile=");
			loggerValue.append(p_profile);
			loggerValue.append( ",locationCode=");
			loggerValue.append(p_locCode);
			loggerValue.append("p_compareList.size=");
			loggerValue.append(p_compareList.size());
			_log.debug(methodName, loggerValue);
		}

		PreparedStatement dbPs = null;
		ResultSet rs = null;
		final ArrayList mobileList = new ArrayList();
		ServicesVO serviceVO = null;
		int position = 0;
		String posStr = null;
		String compareString = null;
		String tempStr = "";
		String qryStr = null;
		StringBuffer sqlLoadBuf = null;
		 
		try {
			if (p_compareList.size() == 1) {
				serviceVO = (ServicesVO) p_compareList.get(0);
				compareString = serviceVO.getCompareHexString();
				position = serviceVO.getPosition();
				posStr = "" + position;
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, "compareString=" + compareString
							+ "position=" + position);
				}
				sqlLoadBuf = new StringBuffer("SELECT msisdn,service" + posStr
						+ " service FROM sim_image ");
				sqlLoadBuf
						.append(" WHERE user_type = ? AND profile = ? AND network_code = ? ");
				if (serviceVO.getOperation().equalsIgnoreCase(
						ByteCodeGeneratorI.ADD)) {
					tempStr = tempStr + " AND substr(SERVICE" + posStr
							+ ",0,6) <> substr('" + compareString + "',0,6) ";
					// tempStr=tempStr+
					// " AND substr(SERVICE"+posStr+",0,2) = substr('"+compareString+"',0,2) ";
				} else if (serviceVO.getOperation().equalsIgnoreCase(
						ByteCodeGeneratorI.CHANGE_TITLE)) {
					tempStr = tempStr + " AND substr(SERVICE" + posStr
							+ ",0,4) = substr('" + compareString + "',0,4) ";
					tempStr = tempStr + " AND SERVICE" + posStr + " <> '"
							+ compareString + "'";
				} else if (serviceVO.getOperation().equalsIgnoreCase(
						ByteCodeGeneratorI.DELETE)) {
					tempStr = tempStr + " AND substr(SERVICE" + posStr
							+ ",0,6) = substr('" + compareString + "',0,6) ";
				} else if (serviceVO.getOperation().equalsIgnoreCase(
						ByteCodeGeneratorI.ACTIVATE)
						|| serviceVO.getOperation().equalsIgnoreCase(
								ByteCodeGeneratorI.DEACTIVATE)) {
					tempStr = tempStr + " AND SERVICE" + posStr + " = '"
							+ compareString + "'";
				}
			} else {
				sqlLoadBuf = new StringBuffer("SELECT msisdn FROM sim_image ");
				sqlLoadBuf
						.append(" WHERE user_type = ? AND profile = ? AND network_code = ? ");
				for (int i = 0; i < p_compareList.size(); i++) {
					serviceVO = (ServicesVO) p_compareList.get(i);
					compareString = serviceVO.getCompareHexString();
					position = serviceVO.getPosition();
					if (_log.isDebugEnabled()) {
						loggerValue.setLength(0);
						loggerValue.append("compareString=");
						loggerValue.append(compareString);
						loggerValue.append("position=" );
						loggerValue.append(position);
						_log.debug(methodName,  loggerValue);
					}
					posStr = "" + position;
					if (serviceVO.getOperation().equalsIgnoreCase(
							ByteCodeGeneratorI.DELETE)) {
						if (i == 0) {
							tempStr = tempStr + " AND (substr(SERVICE" + posStr
									+ ",0,6) = substr('" + compareString
									+ "',0,6) ";
						} else {
							tempStr = tempStr + " OR substr(SERVICE" + posStr
									+ ",0,6) = substr('" + compareString
									+ "',0,6) ";
						}
					} else if (serviceVO.getOperation().equalsIgnoreCase(
							ByteCodeGeneratorI.ACTIVATE)
							|| serviceVO.getOperation().equalsIgnoreCase(
									ByteCodeGeneratorI.DEACTIVATE)) {
						if (i == 0) {
							tempStr = tempStr + " AND (SERVICE" + posStr
									+ " = '" + compareString + "'";
						} else {
							tempStr = tempStr + " OR SERVICE" + posStr + " = '"
									+ compareString + "'";
						}
					}
				}
				if (!BTSLUtil.isNullString(tempStr) && tempStr.length() > 0) {
					tempStr = tempStr + ")";
				}
			}
			qryStr = sqlLoadBuf.toString() + tempStr;
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, ",QUERY= " + qryStr);
			}
			dbPs = p_con.prepareStatement(qryStr);
			int i=0;
			i++;
			dbPs.setString(i, p_userType);
			i++;
			dbPs.setString(i, p_profile);
			i++;
			dbPs.setString(i, p_locCode);
			rs = dbPs.executeQuery();
			if (p_compareList.size() == 1) {
				while (rs.next()) {
					mobileList.add(rs.getString("msisdn") + "|"
							+ rs.getString("service"));
				}
			} else {
				while (rs.next()) {
					mobileList.add(rs.getString("msisdn") + "|");
				}
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName,
						"After executing the query getUnmatchedProfileList method ");
			}
			return mobileList;
		}

		catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception" );
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue );
			_log.errorTrace(methodName, sqe);
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception:");
			loggerValue.append( sqe.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[getUnmatchedProfileList]", "", "", "",loggerValue.toString());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}

		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(" Exception" );
			loggerValue.append(e.getMessage());
			_log.error(methodName,	loggerValue );
			_log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append(" Exception" );
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[getUnmatchedProfileList]", "", "", "",loggerValue.toString());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}

		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("mobileList=");
				loggerValue.append(mobileList.size());
				_log.debug(methodName,  loggerValue );
			}
		}
	}

	/**
	 * This method is used to add in job DB table
	 * 
	 * @param p_con
	 *            of Connection type
	 * @param p_mobileList
	 *            ArrayList
	 * @param p_jobList
	 *            ArrayList
	 * @param p_batchList
	 *            arrayList
	 * @param p_createdBy
	 *            String
	 * @param p_simProfileVO
	 *            SimProfileVO
	 * @return String
	 * @exception BTSLBaseException
	 */

	public int addMobileInJobDb(Connection p_con, ArrayList p_mobileList,
			ArrayList p_jobList, ArrayList p_batchList, String p_createdBy,
			SimProfileVO p_simProfileVO) throws BTSLBaseException {
		StringBuilder loggerValue= new StringBuilder(); 
		final String methodName = "addMobileInJobDb";
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append(" Entered..p_mobileList.size():");
			loggerValue.append(p_mobileList.size());
			_log.debug(methodName, loggerValue );
		}

		PreparedStatement dbPs = null;
		BulkPushVO bulkPushVO = null;
		BufferedWriter out = null;
		final Date currentDate = new Date(System.currentTimeMillis());
		final Timestamp sqlDate = BTSLUtil
				.getTimestampFromUtilDate(currentDate);
		int count = 0;
		int jobResponse = 0;
		int batchResponse = 0;
		String writeFile = null;
		final StringBuffer writeFileBuff = new StringBuffer();
		ArrayList temp = null;
		String filePath = null;
		String actualFileName = null;
		int counter = 0;
		boolean deleteFileFlag = false;
		try {
			jobResponse = addJobMaster(p_con, p_jobList, p_createdBy);
			if (jobResponse > 0) {
				batchResponse = addBatchMaster(p_con, p_batchList, p_createdBy);
			} else {
				throw new BTSLBaseException("BulkPushWebDAO", methodName, "Not able to insert in Job Master");
			}
			if (batchResponse > 0) {
				final StringBuffer sqlLoadBuf = new StringBuffer(
						"INSERT INTO OTA_JOB_DATABASE(JOB_ID, BATCH_ID, network_code, USER_TYPE, PROFILE, ");
				sqlLoadBuf
						.append(" MSISDN, TRANSACTION_ID, SERVICE_SET_ID, SERVICE_ID, ");
				sqlLoadBuf
						.append("   MAJOR_VERSION, MINOR_VERSION, MESSAGE, OPERATION,OPERTION_TYPE, STATUS, NEW_BATCH_ID, NEW_JOB_ID, ");
				sqlLoadBuf
						.append("CREATED_BY, CREATED_ON, MODIFIED_BY, MODIFIED_ON) ");
				sqlLoadBuf
						.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )");
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, ",QUERY= " + sqlLoadBuf.toString());
				}
				dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
                int mobileListSize = p_mobileList.size();
				for (int i = 0; i < mobileListSize; i++) {
					temp = (ArrayList) p_mobileList.get(i);
					counter = counter + 1;
					for (int k = 0; k < temp.size(); k++) {
						bulkPushVO = (BulkPushVO) temp.get(k);
						dbPs.setString(1, bulkPushVO.getJobId());
						dbPs.setString(2, bulkPushVO.getBatchId());
						dbPs.setString(3, bulkPushVO.getLocationCode());
						dbPs.setString(4, bulkPushVO.getUserType());
						dbPs.setString(5, bulkPushVO.getProfile());
						dbPs.setString(6, bulkPushVO.getMsisdn());
						dbPs.setString(7, bulkPushVO.getTransactionId()
								.toUpperCase());
						dbPs.setString(8, bulkPushVO.getServiceSetID());
						dbPs.setString(9, bulkPushVO.getServiceID());
						dbPs.setString(10, bulkPushVO.getMajorVersion());
						dbPs.setString(11, bulkPushVO.getMinorVersion());
						dbPs.setString(12, bulkPushVO.getByteCode());
						dbPs.setString(13, bulkPushVO.getOperationsHexCode());
						dbPs.setString(14, bulkPushVO.getOperationType());
						dbPs.setNull(15, Types.VARCHAR);
						dbPs.setString(16, bulkPushVO.getNewBatchId());
						dbPs.setString(17, bulkPushVO.getNewBatchId());
						dbPs.setString(18, bulkPushVO.getCreatedBy());
						dbPs.setTimestamp(19, sqlDate);
						dbPs.setString(20, bulkPushVO.getCreatedBy());
						dbPs.setTimestamp(21, sqlDate);
						count = dbPs.executeUpdate();
						dbPs.clearParameters();
					}
					writeFile = new OtaMessage().flatFileGeneration(temp,
							p_simProfileVO);
					if (!BTSLUtil.isNullString(writeFile)
							|| writeFile.length() > 0) {
						final StringTokenizer strTok = new StringTokenizer(
								writeFile, "@");
						try {
							filePath = Constants
									.getProperty("JobCreationFilePath");
							actualFileName = filePath + _jobName + "Batch"
									+ counter;
							if (_log.isDebugEnabled()) {
								_log.debug(methodName, "File name path:"
										+ actualFileName);
							}

							out = new BufferedWriter(new FileWriter(new File(
									actualFileName + ".txt")));
							if (_log.isDebugEnabled()) {
								loggerValue.setLength(0);
								loggerValue.append( "No. of entries=");
								loggerValue.append(strTok.countTokens());
								_log.debug(methodName,loggerValue );
							}

							ArrayList sortedList = new ArrayList();
							BulkPushVO bulkVO = null;
							while (strTok.hasMoreElements()) {
								// Sort the file according to msisdn & then
								// write into the file
								final StringTokenizer strTokMsisdn = new StringTokenizer(
										strTok.nextToken(), " ");
								bulkVO = new BulkPushVO();
								bulkVO.setMsisdn(strTokMsisdn.nextToken());
								bulkVO.setMessage(strTokMsisdn.nextToken());
								bulkVO.setMessageType(strTokMsisdn.nextToken());
								sortedList.add(bulkVO);
							}

							final ListSorterUtil sort = new ListSorterUtil();
							sortedList = (ArrayList) sort.doSort("msisdn",
									null, sortedList);
							if (sortedList != null && sortedList.size() > 0) {
								int sortedListSize = sortedList.size();
								for (int j = 0; j < sortedListSize; j++) {
									bulkVO = (BulkPushVO) sortedList.get(j);
									out.write(bulkVO.getMsisdn() + " "
											+ bulkVO.getMessage() + " "
											+ bulkVO.getMessageType());
									out.newLine();
								}
							}
							out.close();
						} catch (IOException e) {
							_log.errorTrace(methodName, e);
							loggerValue.setLength(0);
							loggerValue.append("IO exception=");
							loggerValue.append(e.getMessage());
							_log.debug(methodName,loggerValue );
							out.close();
							while (counter >= 0) {
								filePath = Constants
										.getProperty("JobCreationFilePath");
								actualFileName = filePath + _jobName + "Batch"
										+ counter;
								final File file1 = new File(actualFileName
										+ ".txt");
								if (file1 != null) {
									deleteFileFlag = file1.delete();
								}
								if (deleteFileFlag == true) {
									counter = counter - 1;
								} else {
									throw new BTSLBaseException("BulkPushWebDAO", methodName,
											"Not able to delete the file");
								}
							}
							throw new BTSLBaseException("BulkPushWebDAO", methodName, "Not able to write in the file");
						} catch (Exception e) {
							_log.errorTrace(methodName, e);
							_log.debug(methodName,
									"exception=" + e.getMessage());
							out.close();
							while (counter >= 0) {
								filePath = Constants
										.getProperty("JobCreationFilePath");
								actualFileName = filePath + _jobName + "Batch"
										+ counter;
								final File file1 = new File(actualFileName
										+ ".txt");
								if (file1 != null) {
									deleteFileFlag = file1.delete();
								}
								if (deleteFileFlag == true) {
									counter = counter - 1;
								} else {
									throw new BTSLBaseException("BulkPushWebDAO", methodName,
											"Not able to delete the file");
								}
							}
							throw new BTSLBaseException("BulkPushWebDAO", methodName, "Not able to write in the file");
						}
					}

					else {
						throw new BTSLBaseException("BulkPushWebDAO", methodName,
								"Not able to get the String for writing to file");
					}
				}
			} else {
				throw new BTSLBaseException("BulkPushWebDAO", methodName, "Not able to insert in batch");
			}

			/*
			 * //Logic for generating the sorted list according to msisdn
			 * ArrayList sortedList=new ArrayList();
			 * writeFile=writeFileBuff.toString(); BulkPushVO bulkVO=null;
			 * StringTokenizer strTok=new StringTokenizer(writeFile,"@");
			 * while(strTok.hasMoreElements()) { StringTokenizer
			 * strTokMsisdn=new StringTokenizer(strTok.nextToken()," ");
			 * bulkVO=new BulkPushVO();
			 * bulkVO.setMsisdn(strTokMsisdn.nextToken());
			 * bulkVO.setMessage(strTokMsisdn.nextToken());
			 * bulkVO.setMessageType("BIN"); sortedList.add(bulkVO); }
			 * 
			 * ListSorterUtil sort = new ListSorterUtil(); sortedList =
			 * (ArrayList)sort.doSort("msisdn",null,sortedList); int
			 * batchCount=0; boolean fileExists=true; if(sortedList!=null &&
			 * sortedList.size()>0) { for(int i=0;i<sortedList.size();i++) {
			 * bulkVO=(BulkPushVO)sortedList.get(i);
			 * System.out.println(bulkVO.getMsisdn()+ " " + bulkVO.getMessage()
			 * +" " +bulkVO.getMessageType()+ "\n"); //Writing into the file
			 * logic starts here try {
			 * filePath=Constants.getProperty("JobCreationFilePath");
			 * if(batchCount<counter) batchCount=batchCount+1; else
			 * batchCount=counter;
			 * 
			 * actualFileName=filePath+_jobName+"Batch"+batchCount; if
			 * (_log.isDebugEnabled())
			 * _log.debug("addMobileInJobDb","File name path:"+actualFileName);
			 * File file1=new File(actualFileName+".txt");
			 * 
			 * if(fileExists) { if(file1!=null) file1.delete();
			 * fileExists=false; }
			 * 
			 * out = new BufferedWriter(new FileWriter(file1,true)); if
			 * (_log.isDebugEnabled())
			 * _log.debug("addMobileInJobDb","No. of entries="
			 * +sortedList.size()) ; out.write(bulkVO.getMsisdn()+ " " +
			 * bulkVO.getMessage() +" " +bulkVO.getMessageType());
			 * out.newLine(); out.close(); } catch (IOException e) {
			 * _log.debug("addMobileInJobDb","IO exception="+e.getMessage());
			 * out.close(); while(batchCount>=0) {
			 * filePath=Constants.getProperty("JobCreationFilePath");
			 * actualFileName=filePath+_jobName+"Batch"+counter; File file1=new
			 * File(actualFileName+".txt"); if(file1!=null)
			 * deleteFileFlag=file1.delete(); if(deleteFileFlag==true)
			 * batchCount=batchCount-1; else throw new
			 * Exception("Not able to delete the file"); } throw new
			 * Exception("Not able to write in the file"); } catch (Exception e)
			 * { _log.debug("addMobileInJobDb","exception="+e.getMessage());
			 * out.close(); while(batchCount>=0) {
			 * filePath=Constants.getProperty("JobCreationFilePath");
			 * actualFileName=filePath+_jobName+"Batch"+counter; File file1=new
			 * File(actualFileName+".txt"); if(file1!=null)
			 * deleteFileFlag=file1.delete(); if(deleteFileFlag==true)
			 * batchCount=batchCount-1; else throw new
			 * Exception("Not able to delete the file"); } throw new
			 * Exception("Not able to write in the file"); } } //end of
			 * arraylist loop }//end of if
			 */

			return count;
		}

		catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception" );
			loggerValue.append( sqe.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqe);
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception:");
			loggerValue.append( sqe.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[addMobileInJobDb]", "", "", "",loggerValue.toString());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}

		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(" Exception" );
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue );
			_log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[addMobileInJobDb]", "", "", "", loggerValue.toString());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}

		finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("count=");
				loggerValue.append(count);
				_log.debug(methodName, loggerValue );
			}
		}
	}

	/*	*//**
	 * This method is used to add job information in the job master
	 * 
	 * @param p_con
	 *            of Connection type
	 * @param p_jobList
	 *            ArrayList
	 * @param p_createdBy
	 *            of String type
	 * @return int
	 * @exception BTSLBaseException
	 */

	public int addJobMaster(Connection p_con, ArrayList p_jobList,
			String p_createdBy) throws BTSLBaseException {
		final String methodName = "addJobMaster";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered with jobSize=" + p_jobList.size());
		}

		PreparedStatement dbPs = null;
		int count = 0;
		ListValueVO listVal = null;
		String label = null;
		String jobId = null;
		String jobName = null;
		String locationCode;
		String value = null;
		String mobileNos = null;
		long mobileCount = 0;
		String noOfMobileStr = null;
		long noOfMobiles = 0;
		String createdBy = p_createdBy;
		Timestamp sqlDate;
		final Date currentDate = new Date();

		try {
			final StringBuffer sqlLoadBuf = new StringBuffer(
					"INSERT INTO job_master (job_id, name, status, ");
			sqlLoadBuf
					.append(" mobile_count,no_of_mobiles,created_by, created_on, modified_by, modified_on,network_code) VALUES  ");
			sqlLoadBuf.append(" (?,?,?,?,?,?,?,?,?,?) ");
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, ",QUERY= " + sqlLoadBuf.toString());
			}
			int   jobListSize = p_jobList.size();
			dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
			for (int i = 0; i < jobListSize; i++) {
				listVal = (ListValueVO) p_jobList.get(i);
				sqlDate = BTSLUtil.getTimestampFromUtilDate(currentDate);
				label = listVal.getLabel();
				jobId = label.substring(0, label.indexOf(" ")).trim();
				locationCode = label.substring(label.indexOf(" ") + 1,
						label.indexOf("|")).trim();
				jobName = label.substring(label.indexOf("|") + 1).trim();
				_jobName = jobName;
				value = listVal.getValue().trim();
				noOfMobileStr = value.substring(0, value.indexOf("|")).trim();
				mobileNos = value.substring(value.indexOf("|") + 1,
						value.indexOf(" ")).trim();
				try {
					noOfMobiles = Long.parseLong(noOfMobileStr);
					mobileCount = Long.parseLong(mobileNos);
				} catch (NumberFormatException e) {
					throw new BTSLBaseException("BulkPushWebDAO" ,methodName, "Not able to convert String to number");
				}
				createdBy = value.substring(value.indexOf(" ") + 1,
						value.length()).trim();
				dbPs.setString(1, jobId);
				dbPs.setString(2, jobName);
				dbPs.setString(3, "Y");
				dbPs.setLong(4, mobileCount);
				dbPs.setLong(5, noOfMobiles);
				dbPs.setString(6, createdBy);
				dbPs.setTimestamp(7, sqlDate);
				dbPs.setString(8, createdBy);
				dbPs.setTimestamp(9, sqlDate);
				dbPs.setString(10, locationCode);
				count = dbPs.executeUpdate();
				dbPs.clearParameters();
			}
			return count;
		}

		catch (SQLException sqe) {
			_log.error(methodName, "SQL Exception" + sqe.getMessage());
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[addJobMaster]", "", "", "", "SQL Exception:"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}

		catch (Exception e) {
			_log.error(methodName, " Exception" + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[addJobMaster]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}

		finally {
			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "count=" + count);
			}
		}
	}

	/**
	 * This method is used to add Batch information in the batch master
	 * 
	 * @param con
	 *            of Connection type
	 * @param p_batchList
	 *            ArrayList
	 * @param createdBy
	 *            of String type
	 * @return int
	 * @exception BTSLBaseException
	 */

	public int addBatchMaster(Connection p_con, ArrayList p_batchList,
			String createdBy) throws BTSLBaseException {
		final String methodName = "addBatchMaster";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered with size=" + p_batchList.size());
		}

		PreparedStatement dbPs = null;
		int count = 0;
		ListValueVO listVal = null;
		String label = null;
		String jobId = null;
		String batchId = null;
		String batchName = null;
		String batchSizeStr = null;
		int batchSize = 0;
		String value = null;
		String mobileNos = null;
		int mobileCount = 0;
		Timestamp sqlDate;
		final Date currentDate = new Date();
		try {
			final StringBuffer sqlLoadBuf = new StringBuffer(
					"INSERT INTO batch_master (job_id, batch_id, name, status, ");
			sqlLoadBuf
					.append(" batch_size,no_of_mobiles,created_by, created_on, modified_by, modified_on) VALUES  ");
			sqlLoadBuf.append(" (?,?,?,?,?,?,?,?,?,? )");
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, ",QUERY= " + sqlLoadBuf.toString());
			}
			dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
			int batchListSize = p_batchList.size();
			for (int i = 0; i < batchListSize; i++) {
				listVal = (ListValueVO) p_batchList.get(i);
				sqlDate = BTSLUtil.getTimestampFromUtilDate(currentDate);
				label = listVal.getLabel();
				batchId = label.substring(0, label.indexOf(" ")).trim();
				batchName = label.substring(label.indexOf(" ") + 1,
						label.indexOf("|")).trim();
				batchSizeStr = label.substring(label.indexOf("|") + 1,
						label.length()).trim();
				value = listVal.getValue().trim();
				mobileNos = value.substring(0, value.indexOf(" ")).trim();
				try {
					mobileCount = Integer.parseInt(mobileNos);
					batchSize = Integer.parseInt(batchSizeStr);
				} catch (NumberFormatException e) {
					throw new BTSLBaseException("BulkPushWebDAO", methodName, "Not able to convert String to number");
				}
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, "value:" + value);
				}
				jobId = value.substring(value.indexOf(" ") + 1,
						value.indexOf("|")).trim();
				createdBy = value.substring(value.indexOf("|") + 1,
						value.length()).trim();
				dbPs.setString(1, jobId);
				dbPs.setString(2, batchId);
				dbPs.setString(3, batchName);
				dbPs.setString(4, "Y");
				dbPs.setInt(5, batchSize);
				dbPs.setInt(6, mobileCount);
				dbPs.setString(7, createdBy);
				dbPs.setTimestamp(8, sqlDate);
				dbPs.setString(9, createdBy);
				dbPs.setTimestamp(10, sqlDate);
				count = dbPs.executeUpdate();
				dbPs.clearParameters();
			}
			return count;
		}

		catch (SQLException sqe) {
			_log.error(methodName, "SQL Exception" + sqe.getMessage());
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[addBatchMaster]", "", "", "", "SQL Exception:"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}

		catch (Exception e) {
			_log.error(methodName, " Exception" + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[addBatchMaster]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}

		finally {
			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "count=" + count);
			}
		}
	}

	/**
	 * This method gets the job list present in the location
	 * 
	 * @param p_con
	 *            of Connection type
	 * @param p_locationCode
	 *            of String type
	 * @return ArrayList
	 * @exception BTSLBaseException
	 */

	public ArrayList getOpenJobListForDisplay(Connection p_con,
			String p_locationCode) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("getOpenJobListForDisplay ",
					"Entered.. p_locationCode= " + p_locationCode);
		}
		PreparedStatement dbPs = null;
		ResultSet rs = null;
		final ArrayList serviceList = new ArrayList();
		ListValueVO listValueVO = null;
		final String methodName = "getOpenJobListForDisplay";
		try {
			final StringBuffer sqlLoadBuf = new StringBuffer(
					"SELECT job_id, name FROM job_master WHERE MOBILE_COUNT <> NO_OF_MOBILES AND (status = 'Y' OR status is NULL) ");
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "QUERY= " + sqlLoadBuf.toString());
			}
			dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
			rs = dbPs.executeQuery();
			while (rs.next()) {
				listValueVO = new ListValueVO(rs.getString("name"),
						rs.getString("job_id"));
				serviceList.add(listValueVO);
			}
			return serviceList;
		}

		catch (SQLException sqe) {
			_log.error(methodName, "SQL Exception" + sqe.getMessage());
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[getOpenJobListForDisplay]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}

		catch (Exception e) {
			_log.error(methodName, " Exception" + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[getOpenJobListForDisplay]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}

		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName,
						"Exiting..jobList size=" + serviceList.size());
			}
		}
	}

	/**
	 * This method gets the job batches information, only those batches whose
	 * status is Y or null and mobile send <> total no. of mobiles
	 * 
	 * @param p_con
	 *            of Connection type
	 * @param p_jobId
	 *            of String type
	 * @return ArrayList
	 * @exception BTSLBaseException
	 */

	public ArrayList getJobInforamtionForDisplay(Connection p_con,
			String p_jobId) throws BTSLBaseException {
		final String methodName = "getJobInforamtionForDisplay";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered.. p_jobId=" + p_jobId);
		}

		PreparedStatement dbPs = null;
		ResultSet rs = null;
		final ArrayList serviceList = new ArrayList();

		try {
			final StringBuffer sqlLoadBuf = new StringBuffer(
					"SELECT BATCH_ID, NAME, BATCH_SIZE, NO_OF_MOBILES, MOBILE_COUNT FROM batch_master ");
			sqlLoadBuf
					.append("where job_id=? AND MOBILE_COUNT <> NO_OF_MOBILES AND (status='Y' OR status is null) ");
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, ",QUERY= " + sqlLoadBuf.toString());
			}
			dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
			dbPs.setString(1, p_jobId);
			rs = dbPs.executeQuery();
			BulkPushVO bulkpushVO = null;
			while (rs.next()) {
				bulkpushVO = new BulkPushVO();
				bulkpushVO.setJobId(p_jobId);
				bulkpushVO.setBatchId(rs.getString("BATCH_ID"));
				bulkpushVO.setJobName(rs.getString("NAME"));
				bulkpushVO.setBatchSize(rs.getInt("BATCH_SIZE"));
				bulkpushVO.setNumberOfMobiles(rs.getInt("NO_OF_MOBILES"));
				bulkpushVO.setMobileCount(rs.getInt("MOBILE_COUNT"));
				serviceList.add(bulkpushVO);
			}
			return serviceList;
		}

		catch (SQLException sqe) {
			_log.error(methodName, "SQL Exception" + sqe.getMessage());
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[getJobInforamtionForDisplay]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}

		catch (Exception e) {
			_log.error(methodName, " Exception" + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[getJobInforamtionForDisplay]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}

		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting..jobInfoList size="
						+ serviceList.size());
			}
		}
	}

	/**
	 * This method gets the mobile no.s from job and batch id list for sending
	 * messages
	 * 
	 * @param p_con
	 *            of Connection type
	 * @param p_jobId
	 *            of String type
	 * @param p_batchIdList
	 *            String
	 * @return ArrayList
	 * @exception BTSLBaseException
	 */

	public ArrayList getMobileNosInJob(Connection p_con, String p_jobId,
			String p_batchIdList) throws BTSLBaseException {

		final String methodName = "getMobileNosInJob";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered.. p_jobId=" + p_jobId
					+ "p_batchIdList=" + p_batchIdList);
		}

		PreparedStatement dbPs = null;
		ResultSet rs = null;
		final ArrayList serviceList = new ArrayList();
		ServicesVO servicesVO = null;

		try {
			final String bl = p_batchIdList.replaceAll("\" ", "");
			final String m_batchIdList[] = bl.split(",");
			final String sqlLoadBuf = bulkPushWebQry
					.getMobileNosInJobQry(m_batchIdList);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, ",QUERY= " + sqlLoadBuf);
			}
			dbPs = p_con.prepareStatement(sqlLoadBuf);
			int i = 0;
			dbPs.setString(++i, p_jobId);
			for (int x = 0; x < m_batchIdList.length; x++) {
				dbPs.setString(++i, m_batchIdList[x]);
			}
			rs = dbPs.executeQuery();
			while (rs.next()) {
				servicesVO = new ServicesVO();
				servicesVO.setMsisdn(rs.getString("MSISDN"));
				servicesVO.setTransactionId(rs.getString("TRANSACTION_ID"));
				servicesVO.setKey(rs.getString("KEY"));
				servicesVO.setByteCode(rs.getString("MESSAGE"));
				servicesVO.setCompareHexString(rs.getString("OPERATION"));
				serviceList.add(servicesVO);
			}
			return serviceList;
		}

		catch (SQLException sqe) {
			_log.error(methodName, "SQL Exception" + sqe.getMessage());
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[getMobileNosInJob]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}

		catch (Exception e) {
			_log.error(methodName, " Exception" + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[getMobileNosInJob]", "", "", "", "Exception:"
							+ e.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}

		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting..jobInfoList size="
						+ serviceList.size());
			}
		}

	}

	/**
	 * This method updates the status of the mobile no to not send for discard
	 * retry option only
	 * 
	 * @param p_con
	 *            of Connection type
	 * @param p_jobId
	 *            of String type
	 * @param p_batchIdList
	 *            of String type
	 * @return int
	 * @exception BTSLBaseException
	 */

	public int updateDiscardedMobileStatus(Connection p_con, String p_jobId,
			String p_batchIdList, String p_modifiedBy) throws BTSLBaseException {
		final String methodName = "updateDiscardedMobileStatus";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered..p_jobId:" + p_jobId
					+ "and p_batchIdList=" + p_batchIdList);
		}

		PreparedStatement dbPs = null;
		final Timestamp sqlDate = BTSLUtil.getTimestampFromUtilDate(new Date());
		int count = 0;
		try {
			final String bl = p_batchIdList.replaceAll("\" ", "");
			final String m_batchIdList[] = bl.split(",");
			final StringBuffer sqlLoadBuf = new StringBuffer(
					"UPDATE OTA_JOB_DATABASE SET  ");
			sqlLoadBuf.append("  STATUS=? , MODIFIED_BY=?, MODIFIED_ON=?  ");
			sqlLoadBuf.append(" WHERE job_id=? AND batch_id IN (");
			for (int i = 0; i < m_batchIdList.length; i++) {
				sqlLoadBuf.append(" ?");
				if (i != m_batchIdList.length - 1) {
					sqlLoadBuf.append(",");
				}
			}
			sqlLoadBuf.append(")");
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, ",QUERY= " + sqlLoadBuf.toString());
			}
			dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
			int i = 0;
			dbPs.setString(++i, ByteCodeGeneratorI.NOTSENTSTAT);
			dbPs.setString(++i, p_modifiedBy);
			dbPs.setTimestamp(++i, sqlDate);
			dbPs.setString(++i, p_jobId);
			for (int x = 0; x < m_batchIdList.length; x++) {
				dbPs.setString(++i, m_batchIdList[x]);
			}

			count = dbPs.executeUpdate();
			return count;
		}

		catch (SQLException sqe) {
			_log.error(methodName, "SQL Exception" + sqe.getMessage());
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[updateDiscardedMobileStatus]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}

		catch (Exception e) {
			_log.error(methodName, " Exception" + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[updateDiscardedMobileStatus]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}

		finally {
			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting..count=" + count);
			}
		}
	}

	/**
	 * This method gets the mobiles for resend retry option only
	 * 
	 * @param p_con
	 *            of Connection type
	 * @param p_jobId
	 *            of String type
	 * @param p_batchIdList
	 *            of String type
	 * @return ArrayList
	 * @exception BTSLBaseException
	 */

	public ArrayList getMobileListForResend(Connection p_con, String p_jobId,
			String p_batchIdList) throws BTSLBaseException {
		final String methodName = "getMobileListForResend";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered.. p_jobId=" + p_jobId
					+ "And p_batchIdList=" + p_batchIdList);
		}

		PreparedStatement dbPs = null;
		ResultSet rs = null;
		final ArrayList serviceList = new ArrayList();
		BulkPushVO bulkPushVO = null;

		try {
			final String bl = p_batchIdList.replaceAll("\" ", "");
			final String m_batchIdList[] = bl.split(",");
			final StringBuffer sqlLoadBuf = new StringBuffer(
					"SELECT JOB_ID,BATCH_ID,network_code, USER_TYPE, PROFILE, MSISDN, ");
			sqlLoadBuf
					.append(" TRANSACTION_ID, SERVICE_SET_ID, SERVICE_ID, MAJOR_VERSION, MINOR_VERSION, MESSAGE, OPERATION, OPERTION_TYPE, CREATED_BY  ");
			sqlLoadBuf
					.append(" FROM ota_job_database WHERE (status ='FAILED' OR status is null ) AND job_id=? AND batch_id IN (");
			for (int i = 0; i < m_batchIdList.length; i++) {
				sqlLoadBuf.append(" ?");
				if (i != m_batchIdList.length - 1) {
					sqlLoadBuf.append(",");
				}
			}
			sqlLoadBuf.append(")");
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, ",QUERY= " + sqlLoadBuf.toString());
			}
			dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
			int i = 0;
			dbPs.setString(++i, p_jobId);
			for (int x = 0; x < m_batchIdList.length; x++) {
				dbPs.setString(++i, m_batchIdList[x]);
			}
			rs = dbPs.executeQuery();
			while (rs.next()) {
				bulkPushVO = new BulkPushVO();
				bulkPushVO.setJobId(rs.getString("JOB_ID"));
				bulkPushVO.setBatchId(rs.getString("BATCH_ID"));
				bulkPushVO.setLocationCode(rs.getString("network_code"));
				bulkPushVO.setUserType(rs.getString("USER_TYPE"));
				bulkPushVO.setProfile(rs.getString("PROFILE"));
				bulkPushVO.setMsisdn(rs.getString("MSISDN"));
				bulkPushVO.setServiceSetID(BTSLUtil.NullToString(rs
						.getString("SERVICE_SET_ID")));
				bulkPushVO.setServiceID(BTSLUtil.NullToString(rs
						.getString("SERVICE_ID")));
				bulkPushVO.setMajorVersion(BTSLUtil.NullToString(rs
						.getString("MAJOR_VERSION")));
				bulkPushVO.setMinorVersion(BTSLUtil.NullToString(rs
						.getString("MINOR_VERSION")));
				bulkPushVO.setByteCode(BTSLUtil.NullToString(rs
						.getString("MESSAGE")));
				bulkPushVO.setOperationsHexCode(BTSLUtil.NullToString(rs
						.getString("OPERATION")));
				bulkPushVO.setOperationType(BTSLUtil.NullToString(rs
						.getString("OPERTION_TYPE")));
				bulkPushVO.setCreatedBy(BTSLUtil.NullToString(rs
						.getString("CREATED_BY")));
				serviceList.add(bulkPushVO);

			}
			return serviceList;
		}

		catch (SQLException sqe) {
			_log.error(methodName, "SQL Exception" + sqe.getMessage());
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[getMobileListForResend]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}

		catch (Exception e) {
			_log.error(methodName, " Exception" + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[getMobileListForResend]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}

		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting..mobileList size="
						+ serviceList.size());
			}
		}
	}

	/**
	 * This method update status and new batch id and new job id fields
	 * 
	 * @param p_con
	 *            of Connection type
	 * @param p_mobileList
	 *            of Arraylist type
	 * @param p_modifiedBy
	 *            String
	 * @return int
	 * @exception BTSLBaseException
	 */

	public int updateOldJobMobileStatus(Connection p_con,
			ArrayList p_mobileList, String p_modifiedBy)
			throws BTSLBaseException {
		final String methodName = "updateOldJobMobileStatus";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered..p_mobileList.size():"
					+ p_mobileList.size());
		}

		PreparedStatement dbPs = null;
		final java.sql.Timestamp sqlDate = BTSLUtil
				.getTimestampFromUtilDate(new Date());
		int count = 0;
		BulkPushVO bulkPushVO = null;
       int mobileListSize = p_mobileList.size();
		try {
			final StringBuffer sqlLoadBuf = new StringBuffer(
					"UPDATE OTA_JOB_DATABASE SET  ");
			sqlLoadBuf
					.append("  STATUS=? , NEW_BATCH_ID=?,NEW_JOB_ID=?,MODIFIED_BY=?, MODIFIED_ON=?  ");
			sqlLoadBuf.append(" WHERE job_id=? and batch_id=? AND msisdn=?");
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, ",QUERY= " + sqlLoadBuf.toString());
			}
			dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
			for (int i = 0; i < mobileListSize; i++) {
				count = 0;
				bulkPushVO = (BulkPushVO) p_mobileList.get(i);
				dbPs.setString(1, ByteCodeGeneratorI.NOTSENTSTAT);

				dbPs.setString(2, bulkPushVO.getNewBatchId());

				dbPs.setString(3, bulkPushVO.getNewJobId());

				dbPs.setString(4, p_modifiedBy);

				dbPs.setTimestamp(5, sqlDate);
				dbPs.setString(6, bulkPushVO.getJobId());

				dbPs.setString(7, bulkPushVO.getBatchId());

				dbPs.setString(8, bulkPushVO.getMsisdn());
				count = dbPs.executeUpdate();
				dbPs.clearParameters();
			}

			return count;
		}

		catch (SQLException sqe) {
			_log.error(methodName, "SQL Exception" + sqe.getMessage());
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[updateOldJobMobileStatus]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}

		catch (Exception e) {
			_log.error(methodName, " Exception" + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[updateOldJobMobileStatus]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}

		finally {
			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting..count=" + count);
			}
		}

	}

	/**
	 * This method update the status to 'N' of the batches given in the list
	 * 
	 * @param p_con
	 *            of Connection type
	 * @param p_jobId
	 *            of String type
	 * @param p_batchIdList
	 *            of String type
	 * @param p_modifiedBy
	 *            String
	 * @return int
	 * @exception BTSLBaseException
	 */

	public int updateJobBatchStatus(Connection p_con, String p_jobId,
			String p_batchIdList, String p_modifiedBy) throws BTSLBaseException {
		final String methodName = "updateJobBatchStatus";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered..p_jobId:" + p_jobId
					+ "and p_batchIdList=" + p_batchIdList);
		}

		PreparedStatement dbPs = null;
		ResultSet rs = null;
		final Timestamp sqlDate = BTSLUtil.getTimestampFromUtilDate(new Date());
		int count = 0;
		try {
			final String bl = p_batchIdList.replaceAll("\" ", "");
			final String m_batchIdList[] = bl.split(",");
			final StringBuffer sqlLoadBuf = new StringBuffer(
					"UPDATE batch_master SET  ");
			sqlLoadBuf.append("  STATUS=? , MODIFIED_BY=?, MODIFIED_ON=?  ");
			sqlLoadBuf.append(" WHERE job_id=? AND batch_id IN (");
			for (int i = 0; i < m_batchIdList.length; i++) {
				sqlLoadBuf.append(" ?");
				if (i != m_batchIdList.length - 1) {
					sqlLoadBuf.append(",");
				}
			}
			sqlLoadBuf.append(")");
			if (_log.isDebugEnabled()) {
				_log.debug("updateJobBatchStatus()",
						",QUERY= " + sqlLoadBuf.toString());
			}
			dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
			int i = 0;
			dbPs.setString(++i, "N");
			dbPs.setString(++i, p_modifiedBy);
			dbPs.setTimestamp(++i, sqlDate);
			dbPs.setString(++i, p_jobId);
			for (int x = 0; x < m_batchIdList.length; x++) {
				dbPs.setString(++i, m_batchIdList[x]);
			}
			count = dbPs.executeUpdate();
			if (_log.isDebugEnabled()) {
				_log.debug(methodName,
						"After executing the query updateJobBatchStatus method ");
			}
			if (count > 0) {
				count = 0;
				final StringBuffer sqlCheck = new StringBuffer(
						"select count(batch_id) count from batch_master  ");
				sqlCheck.append("  where job_id=? AND status='Y'  ");
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, ",QUERY= " + sqlCheck.toString());
				}
				dbPs.close();
				dbPs = p_con.prepareStatement(sqlCheck.toString());
				dbPs.setString(1, p_jobId);
				rs = dbPs.executeQuery();
				if (rs.next()) {
					if (rs.getInt("count") == 0) {
						final StringBuffer sqlUpdateJob = new StringBuffer(
								"UPDATE job_master SET  ");
						sqlUpdateJob
								.append("  STATUS=? , MODIFIED_BY=?, MODIFIED_ON=?  ");
						sqlUpdateJob.append(" WHERE job_id=? ");
						if (_log.isDebugEnabled()) {
							_log.debug(methodName,
									",QUERY= " + sqlUpdateJob.toString());
						}
						dbPs.close();
						dbPs = p_con.prepareStatement(sqlUpdateJob.toString());
						dbPs.setString(1, "N");
						dbPs.setString(2, p_modifiedBy);
						dbPs.setTimestamp(3, sqlDate);
						dbPs.setString(4, p_jobId);
						count = dbPs.executeUpdate();
					} else {
						count = 1;
					}
				} else {
					count = 1;
				}
			}
			return count;
		}

		catch (SQLException sqe) {
			_log.error(methodName, "SQL Exception" + sqe.getMessage());
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[updateJobBatchStatus]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}

		catch (Exception e) {
			_log.error(methodName, " Exception" + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"BulkPushDAO[updateJobBatchStatus]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}

		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting. count=" + count);
			}
		}
	}
}
