package com.btsl.voms.vomsprocesses;


import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.util.VOMSVoucherVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.OracleUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.ibm.icu.util.Calendar;

/**
 * @(#)VoucherDailySummary .java
 *                             Copyright(c) 2005, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 *                             This class will produce the Daily  Voucher Summary Count
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                            Mahindra Comviva 05/12/2018 Initial creation
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 */

public class VoucherDailySummary {
	private final static String  expiredVoucher="EX";
	public static String message = "";
	private static ProcessBL _processBL = null;
	private static HashMap<String, String> typeMap = null;
	private static ProcessStatusVO _processStatusVO;
	private static Log _logger = LogFactory.getLog(VoucherDailySummary.class.getName());

	/**
	 * ensures no instantiation
	 */
	private VoucherDailySummary(){

	}

	public static void main(String[] args) {
		Connection con = null;
		final String METHOD_NAME = "main";
		try {
			if (args.length != 2) {
				System.out.println("Usage : VoucherDailySummary [Constants file] [LogConfig file]");
				return;
			}
			final File constantsFile = new File(args[0]);
			if (!constantsFile.exists()) {
				System.out.println(" Constants File Not Found .............");
				return;
			}
			final File logconfigFile = new File(args[1]);
			if (!logconfigFile.exists()) {
				System.out.println(" Logconfig File Not Found .............");
				return;
			}
			ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
		} catch (Exception e) {
			System.out.println("Exception thrown in VoucherDailySummary: Not able to load files" + e);
			ConfigServlet.destroyProcessCache();
			_logger.errorTrace(METHOD_NAME, e);
			return;
		}

		try {
			// Make Connection
			con = OracleUtil.getSingleConnection();
			if (con == null) {
				if (_logger.isDebugEnabled()) {
					_logger.debug("VoucherDailySummary[main]", "Not able to get Connection for VoucherDailySummary: ");
				}
				throw new BTSLBaseException("Not able to get Connection for VoucherDailySummary: "); 
			}
			try{dailyVOMSSUMMARYExecution(con);}catch(Exception e){
				_logger.errorTrace(METHOD_NAME, e);
			}

		} catch (Exception e) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("VoucherDailySummary[main]", "Exception thrown in VoucherDailySummary: Not able to load files" + e);
			}
			_logger.errorTrace(METHOD_NAME, e);
			ConfigServlet.destroyProcessCache();
		} finally {
			if (_logger.isDebugEnabled()) {
				_logger.debug("VoucherDailySummary[main]", "Exiting");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_logger.errorTrace(METHOD_NAME, e);
			}
			ConfigServlet.destroyProcessCache();
		}
	}

	private static void dailyVOMSSUMMARYExecution(Connection con) {
		final String METHOD_NAME = "dailyVOMSSUMMARYExecution";
		if (_logger.isDebugEnabled()) {
			_logger.debug("dailyVOMSSUMMARYExecution", " Entered:");
		}
		CallableStatement cstmt = null;
		Date currentDate = null;
		String reportTo = null;
		String prevDateStr = null;
		Date processedUpto = null;
		String processId = null;
		boolean statusOk = false;
		int beforeInterval = 0;
		ResultSet resultSet = null;
		Map mapList=new HashMap();
		PreparedStatement psmt = null;
		VOMSVoucherVO vomsVoucherVO=null;
		ArrayList  statusList=null;
		Date dateCount = null;

		int maxDoneDateUpdateCount = 0;
		try {

			processId = ProcessI.VOUCHER_DAILY_SUMMARY;
			// method call to check status of the process
			_processBL = new ProcessBL();
			_processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
			statusOk = _processStatusVO.isStatusOkBool();
			beforeInterval = BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval() / (60 * 24));
			if (statusOk) {
				con.commit();
				processedUpto = _processStatusVO.getExecutedUpto();
				if (processedUpto != null) {
					final Calendar cal = BTSLDateUtil.getInstance();
					currentDate = cal.getTime(); // Current Date
					processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
				} else {
					if (_logger.isDebugEnabled()) {
						_logger.debug("VoucherDailySummary[dailyC2sMisExecution]", " Date till which process has been executed is not found.");
					}
					return;
				}
			}

			// If process is already ran for the last day then do not run again
			if (processedUpto != null && processedUpto.compareTo(currentDate) > 0) {
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherDailySummary[dailyVOMSSUMMARYExecution]", "",
						"", "", "Daily Voucher Summery process already run for the date=" + String.valueOf(currentDate));
				return;
			}
			
					
			for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval)); dateCount = BTSLUtil
					.addDaysInUtilDate(dateCount, 1)) {

				try {
					reportTo = BTSLDateUtil.getSystemLocaleDate(currentDate, PretupsI.DATE_FORMAT); // Current Date
					prevDateStr = BTSLDateUtil.getSystemLocaleDate(dateCount, PretupsI.DATE_FORMAT);// Last MIS Done Date +1
				} catch (Exception e) {
					reportTo = "";
					prevDateStr = "";
					_logger.errorTrace(METHOD_NAME, e);
					throw new BTSLBaseException("Not able to convert date to String");
				}

				if (_logger.isDebugEnabled()) {
					_logger.debug("VoucherDailySummary[dailyVOMSSUMMARYExecution]",
							"From date=" + prevDateStr + " To Date=" + reportTo + " processedUpto.compareTo(currentDate)=" + processedUpto.compareTo(currentDate));
				}

				StringBuffer strBuff = new StringBuffer("select Voucher_type,product_id,MRP,status,PRODUCTION_NETWORK_CODE,count(*) ");
				strBuff.append("as count, sold_status from voms_vouchers group by Voucher_type,product_id,MRP,status,PRODUCTION_NETWORK_CODE, sold_status");
				
				try{
				psmt= con.prepareStatement(strBuff.toString());
				resultSet = psmt.executeQuery();
				typeMap = new HashMap<String, String>();
				while (resultSet.next()) {

					if(mapList.containsKey(resultSet.getString("product_id").toString()))
					{
						vomsVoucherVO=(VOMSVoucherVO)mapList.get(resultSet.getString("product_id").toString());
					}else{
						vomsVoucherVO = new VOMSVoucherVO();
						vomsVoucherVO.setProductID(resultSet.getString("product_id").toString());
					}

					statusList=vomsVoucherVO.getVoucherUsage();

					if(statusList==null || statusList.size()==0)
					{
						statusList=new ArrayList();
					}
					statusList.add(resultSet.getString("status")+","+resultSet.getString("count"));

					vomsVoucherVO.setProductName(resultSet.getString("Voucher_type"));

					vomsVoucherVO.setProductionLocationCode(resultSet.getString("PRODUCTION_NETWORK_CODE"));

					vomsVoucherVO.setMRP(Long.parseLong(resultSet.getString("MRP").toString()));

					vomsVoucherVO.setVoucherUsage(statusList);
					vomsVoucherVO.setSoldStatus(resultSet.getString("sold_status").toString());

					mapList.put(resultSet.getString("product_id").toString(), vomsVoucherVO);

				}
				}
				
				finally{
					if(psmt!=null)
						psmt.close();
				}

				if(mapList.size()>0)
				{

					String query = "INSERT INTO VOMS_VOUCHER_DAILY_SUMMARY(summary_date,product_id,VOUCHER_TYPE,DENOMINATION,production_network_code,user_network_code,TOTAL_GENERATED,TOTAL_ENABLED,TOTAL_STOLEN,TOTAL_ON_HOLD,TOTAL_DAMAGED,TOTAL_CONSUMED,TOTAL_WAREHOUSE,TOTAL_PRINTING,TOTAL_SUSPENDED,TOTAL_EXPIRED, TOTAL_INITIATED, TOTAL_PREACTIVE, OTHER_STATUS, DAILY_SALES)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

					Iterator itr = mapList.entrySet().iterator(); 

					VOMSVoucherVO    vomsVoucherVO2=null;

					int addCount=0;

					while(itr.hasNext()) 
					{ 
						
						Map.Entry entry = (Map.Entry)itr.next();
						int i = 1;	
						String type = null;
						vomsVoucherVO2=(VOMSVoucherVO) entry.getValue();
						if(!BTSLUtil.isNullObject(typeMap) && !typeMap.containsKey(vomsVoucherVO2.getProductName())) {
							type = (new VomsProductDAO()).getTypeFromVoucherType(con, vomsVoucherVO2.getProductName());
							_logger.info("dailyVOMSSUMMARYExecution", "Voucher Type = " + vomsVoucherVO2.getProductName() 
							+ " type = " + type);
							typeMap.put(vomsVoucherVO2.getProductName(), type);
						} else {
							type = typeMap.get(vomsVoucherVO2.getProductName());
						}
//						psmt.clearParameters();
						psmt = con.prepareStatement(query);
						psmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(dateCount));
						psmt.setString(2,vomsVoucherVO2.getProductID());
						psmt.setString(3, vomsVoucherVO2.getProductName());
						_logger.info("dailyVOMSSUMMARYExecution", vomsVoucherVO2.getMRP());
						psmt.setLong(4, vomsVoucherVO2.getMRP());

						psmt.setString(5, vomsVoucherVO2.getProductionLocationCode());
						psmt.setString(6, vomsVoucherVO2.getProductionLocationCode());


						psmt.setLong(7, 0L);
						psmt.setLong(8, 0L);
						psmt.setLong(9, 0L);
						psmt.setLong(10, 0L);
						psmt.setLong(11, 0L);
						psmt.setLong(12, 0L);
						psmt.setLong(13, 0L);
						psmt.setLong(14, 0L);
						psmt.setLong(15, 0L);
						psmt.setLong(16, 0L);
						psmt.setLong(17, 0L);
						psmt.setLong(18, 0L);
						psmt.setLong(19, 0L);
						psmt.setLong(20, 0L);
						
						ArrayList s=vomsVoucherVO2.getVoucherUsage();
						for (Iterator iterator = s.iterator(); iterator.hasNext();) {
							String s1 = (String) iterator.next();
							String[] arry=s1.split(",");
							Long quantity = BTSLUtil.isNullString(arry[1]) ? 0L : Long.parseLong(arry[1]);
							Long dailySales = 0L;
							if(arry[0].equalsIgnoreCase(VOMSI.VOUCHER_NEW)){
								psmt.setLong(7, quantity);
							} else if(arry[0].equalsIgnoreCase(VOMSI.VOUCHER_ENABLE)){
								psmt.setLong(8, quantity);
								if (_logger.isDebugEnabled()) {
									_logger.debug("VoucherDailySummary", "Tejeshvi = " + quantity);
								}
								if(VOMSI.VOUCHER_TYPE_DIGITAL.equals(type) || VOMSI.VOUCHER_TYPE_TEST_DIGITAL.equals(type) || 
										VOMSI.VOUCHER_TYPE_PHYSICAL.equals(type) || VOMSI.VOUCHER_TYPE_TEST_PHYSICAL.equals(type) || 
										(VOMSI.VOUCHER_TYPE_PHYSICAL.equals(type) || VOMSI.VOUCHER_TYPE_TEST_PHYSICAL.equals(type))
										&& PretupsI.YES.equals(vomsVoucherVO2.getSoldStatus())) {
									dailySales = quantity;
								}
							}else if(arry[0].equalsIgnoreCase(VOMSI.VOUCHER_STOLEN)){
								psmt.setLong(9, quantity);
							}else if(arry[0].equalsIgnoreCase(VOMSI.VOUCHER_ON_HOLD)){
								psmt.setLong(10,quantity);
							}else if(arry[0].equalsIgnoreCase(VOMSI.VOUCHER_DAMAGED)){
								psmt.setLong(11, quantity);
							}else if(arry[0].equalsIgnoreCase(VOMSI.VOUCHER_USED)){
								psmt.setLong(12, quantity);
							}else if(arry[0].equalsIgnoreCase(VOMSI.VOMS_WARE_HOUSE_STATUS)){
								psmt.setLong(13, quantity);
							}else if(arry[0].equalsIgnoreCase(VOMSI.VOMS_PRINT_ENABLE_STATUS)){
								psmt.setLong(14,   quantity);
							}else if(arry[0].equalsIgnoreCase(VOMSI.VOMS_SUSPEND_STATUS)){
								psmt.setLong(15,  quantity);
							}else if(arry[0].equalsIgnoreCase(expiredVoucher)){
								psmt.setLong(16, quantity);
							}else if(arry[0].equalsIgnoreCase(VOMSI.VOMS_INITIATED_STATUS)){
								psmt.setLong(17, quantity);
							}else if(arry[0].equalsIgnoreCase(VOMSI.VOMS_PREACTIVE_STATUS)){
								psmt.setLong(18, quantity);
							}else{
								psmt.setLong(19, quantity);
							}
							psmt.setLong(20, dailySales);
						}
						addCount = psmt.executeUpdate();
					}
					if (addCount <= 0) {
						con.rollback();
						_logger.error(" updateSummaryTable", " Summary table not updated for Product ID=" + vomsVoucherVO2.getProductID());
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsVoucherDAO[updateSummaryTable]", "", "", "", "Summary table updated for Product ID=" + vomsVoucherVO2.getProductID());
						throw new BTSLBaseException( "updateSummaryTable", PretupsErrorCodesI.VOUCHER_ERROR_PROCESS_UPDATE_STATUS);
					}else{
						_logger.error(" updateSummaryTable", "  Summary table updated for Product ID=" + vomsVoucherVO2.getProductID());
						con.commit();
					}
				}

				_processStatusVO.setExecutedUpto(dateCount);
				_processStatusVO.setExecutedOn(currentDate);
				ProcessStatusDAO _processStatusDAO = new ProcessStatusDAO();
				maxDoneDateUpdateCount = _processStatusDAO.updateProcessDetail(con, _processStatusVO);
				// if the process is successful, transaction is
				// commit, else rollback
				if (maxDoneDateUpdateCount > 0) {
					con.commit();
				} else {
					con.rollback();
					throw new BTSLBaseException("dailyVOMSSUMMARYExecution", METHOD_NAME, PretupsErrorCodesI.VOUCHER_ERROR_PROCESS_UPDATE_STATUS);
				}

				try {
					Thread.sleep(5);
				} catch (Exception e) {
					_logger.errorTrace(METHOD_NAME, e);
				}

			}
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception sqlex) {
				if (_logger.isDebugEnabled()) {
					_logger.debug("VoucherDailySummary", "VoucherDailySummary[dailyVOMSSUMMARYExecution]::Exception while roll back" + sqlex);
				}
				_logger.errorTrace(METHOD_NAME, sqlex);
			}
			message = e.getMessage();
			// send the message as SMS
			_logger.errorTrace(METHOD_NAME, e);
		} finally {
			try {
				if (statusOk) {
					if (markProcessStatusAsComplete(con, processId) == 1) {
						try {
							con.commit();
						} catch (Exception e) {
							_logger.errorTrace(METHOD_NAME, e);
						}
					} else {
						try {
							con.rollback();
						} catch (Exception e) {
							_logger.errorTrace(METHOD_NAME, e);
						}
					}
				}
				try {
	                if (resultSet != null) {
	                	resultSet.close();
	                }
	            } catch (Exception e) {
	                _logger.errorTrace("VoucherDailySummary", e);
	            }
				try {
	                if (psmt != null) {
	                	psmt.close();
	                }
	            } catch (Exception e) {
	                _logger.errorTrace("VoucherDailySummary", e);
	            }
				if (cstmt != null) {
					cstmt.close();
				}
			} catch (Exception ex) {
				if (_logger.isDebugEnabled()) {
					_logger.debug("VoucherDailySummary", "Exception while closing statement in VoucherDailySummary[dailyVOMSSUMMARYExecution] method ");
				}
				_logger.errorTrace(METHOD_NAME, ex);
			}
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				_logger.errorTrace(METHOD_NAME, e);
			}
		}

	}


	private static int markProcessStatusAsComplete(Connection p_con, String p_processId) {
		final String METHOD_NAME = "markProcessStatusAsComplete";
		if (_logger.isDebugEnabled()) {
			_logger.debug("markProcessStatusAsComplete", " Entered:  p_processId:" + p_processId);
		}
		int updateCount = 0;
		final Date currentDate = new Date();
		final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
		_processStatusVO.setProcessID(p_processId);
		_processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
		_processStatusVO.setStartDate(currentDate);
		try {
			updateCount = processStatusDAO.updateProcessDetailForMis(p_con, _processStatusVO);
		} catch (Exception e) {
			_logger.errorTrace(METHOD_NAME, e);
			if (_logger.isDebugEnabled()) {
				_logger.debug("markProcessStatusAsComplete", "Exception= " + e.getMessage());
			}
		} finally {
			if (_logger.isDebugEnabled()) {
				_logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
			}
		} // end of finally
		return updateCount;

	}
}
