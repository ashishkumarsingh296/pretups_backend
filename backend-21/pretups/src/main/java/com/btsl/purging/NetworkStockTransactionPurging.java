package com.btsl.purging;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.DatabasePurgingLog;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

public class NetworkStockTransactionPurging {
    private static String _messageToWrite = null; // Message that is to be
                                                 // written in logs
    private static String _networkCode = null; // To store network code
    private static Date _startDate = null;
    private static Date _endDate = null;
    private static java.sql.Date _startDateSql = null;
    private static java.sql.Date _endDateSql = null;

    private static java.sql.Date _deletedUpto = null;
    private static PreparedStatement _updatePstmt = null;
    private final static Log _logger = LogFactory.getLog(NetworkStockTransactionPurging.class.getName());

    /**
	 * to ensure no class instantiation 
	 */
    private NetworkStockTransactionPurging() {
        
    }
    public static void main(String[] arg) {
        int status = 0;
        Connection con = null;
        ArrayList networkList = null;
        ArrayList archiveDoneNetworkList = null;
        int archiveDoneNetworkListSize;
        int networkListSize;
        String locCodeName = null;
        // Flag if the process is allowed at a particular time
        boolean isOperationAllowed = false;
        String fromTime = null;
        String toTime = null;
        Date dateTillKeepData = null;
        java.sql.Date dateTillKeepDataSql = null;
        PreparedStatement retentionPstmt = null;
        ResultSet retentionRst=null;
        final String methodName = "main";
        try {
            _logger.info(methodName, "Loading Constants file............");
            File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                _logger.info(methodName, "Consts File Not Found ");
                return;
            }
            File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                _logger.info(methodName, "Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

            try {
                fromTime = Constants.getProperty("ARCHIVE_PURGE_FTIME");
            } catch (Exception e) {
                fromTime = "0000";
                _logger.errorTrace(methodName, e);
            }
            try {
                toTime = Constants.getProperty("ARCHIVE_PURGE_TTIME");
            } catch (Exception e) {
                toTime = "0300";
                _logger.errorTrace(methodName, e);
            }
            isOperationAllowed = ArchivePretupsUtil.archivalTime(fromTime, toTime);
            if (!isOperationAllowed) {

                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, " This Process should be executed between " + fromTime + " and " + toTime);
				}
                return;
            }
            String args[] = new String[9];
            args = ArchivePretupsUtil.intractiveParameterCheck();

            if (args == null) {
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, " Not able to get the values from user");
				}
            }
            args[7] = "Delete";
            ArchivePretupsUtil.displayedUserInfo(args);
            _networkCode = BTSLUtil.NullToString(args[2]);
            _startDate = BTSLUtil.getDateFromDateString(args[5]);
            _endDate = BTSLUtil.getDateFromDateString(args[6]);
        }// end try
        catch (Exception exception) {
            if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, " Exception thrown in while loading files: " + exception);
			}
            _logger.errorTrace(methodName, exception);
            return;
        }// end of catch
        try {

            _startDateSql = BTSLUtil.getSQLDateFromUtilDate(_startDate);
            _endDateSql = BTSLUtil.getSQLDateFromUtilDate(_endDate);
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, " Kindly check whether the verification of files has been done or not and answer the question at the console  ");
			}
            _logger.info(methodName, " INFO: Have you verified the files before deleting the data:  ");

            int i = 0;
            String data = null;
            data = ArchivePretupsUtil.getUserInputFromConsole();
            if ((BTSLUtil.NullToString(data).trim().toUpperCase().indexOf("Y") != -1) && (BTSLUtil.NullToString(data).length() == 1)) {
                _logger.info(methodName, " QUES.: Are you sure you want to perform this operation? ");
                _logger.info(methodName, " Enter Y for Yes and N for No .");
                data = ArchivePretupsUtil.getUserInputFromConsole();
                if ((BTSLUtil.NullToString(data).trim().toUpperCase().indexOf("Y") == -1)) {
                    _logger.debug(methodName, "INFO:----Returning from Here ............. User Entered = " + data + " (operation aborted by user)");
                    if (_logger.isDebugEnabled()) {
						_logger.info(methodName, "  Returning from Here ............. User Entered = " + data + " (operation aborted by user)");
					}
                    // WRITE IN LOG
                    throw new BTSLBaseException("Exiting from the program after " + i + " answer");
                }
                if (BTSLUtil.NullToString(data).trim().toUpperCase().indexOf("Y") != -1 && BTSLUtil.NullToString(data).trim().length() > 1) {
                    _logger.debug(methodName, "INFO:-----Returning from Here ............. User Entered = " + data + " (operation aborted by user)");
                    if (_logger.isDebugEnabled()) {
						_logger.info(methodName, "  Returning from Here ............. User Entered = " + data + " (operation aborted by user)");
					}
                    // WRITE IN LOG
                    throw new BTSLBaseException("Exiting from the program after " + i + " answer");
                }
            } else {
                _logger.debug(methodName, "INFO:  Returning from Here ............. User Entered = " + data + " (operation aborted by user)");
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, "  Returning from Here ............. User Entered = " + data + " (operation aborted by user)");
				}
                throw new BTSLBaseException("Exiting from the program after " + i + " answer");
            }
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, " Starting to perform the data deletion : ");
			}
            _logger.info(methodName, "INFO: --------Kindly Refer the Purging Log File to see further logs ");
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, " Connection null");
				}
                _messageToWrite = "Not able to get Connection ";
                throw new SQLException();
            }
            _messageToWrite = null;

            // Get the network list of the system
            networkList = ArchivePretupsUtil.getNetworks(con, PretupsI.CIRCLE_NETWORK_TYPE);
            if (networkList == null || networkList.isEmpty()) {
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, " Not able to get the network lists");
				}
                _messageToWrite = "Not able to get the network lists";
            }
            // Get the network list for which archiving has been done
            archiveDoneNetworkList = ArchivePretupsUtil.getArchiveDoneNetworks(con, PretupsI.NET_STK_TRA, PretupsI.NET_STK_TRA_ITEMS);
            if (archiveDoneNetworkList == null || archiveDoneNetworkList.isEmpty()) {
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, " Not able to get the archive done network lists");
				}
                _messageToWrite = "Not able to get archive done network lists";
                throw new BTSLBaseException("Not able to load archive done network list");
            }

            // lists size check
            networkListSize = networkList.size();
            archiveDoneNetworkListSize = archiveDoneNetworkList.size();
            if (networkListSize != archiveDoneNetworkListSize) {
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, " Network lists are not of same size");
				}
                _messageToWrite = "Network lists are not of same size";
                throw new BTSLBaseException("Network lists are not of same size");
            }

            // match list items
            for (i = 0; i < networkListSize; i++) {
                locCodeName = (String) networkList.get(i);
                _networkCode = locCodeName.substring(0, locCodeName.indexOf("#"));
                for (int j = 0; j < archiveDoneNetworkListSize; j++) {
                    if (_networkCode.equalsIgnoreCase(archiveDoneNetworkList.get(j).toString())) {
						break;
					} else if (j == archiveDoneNetworkListSize - 1) {
                        if (_logger.isDebugEnabled()) {
							_logger.info(methodName, " Network mismatch occur.");
						}
                        _messageToWrite = "Network mismatch occur.";
                        DatabasePurgingLog.purgeLog("NetworkStockTransactionPurging", "NETWORK CHECK FAIL");
                        throw new BTSLBaseException("Network mismatch occured.");
                    }
                }
            }
            DatabasePurgingLog.purgeLog("NetworkStockTransactionPurging", "NETWORK CHECK SUCCESFUL");

            long startTime = 0;
            long endTime = 0;
            long processStartTime = 0;
            long processEndTime = 0;
            long difference = 0;

            int retention_period;
            String retentionStr = "SELECT retention_period from TABLES_PURGING WHERE table_name=?";
            retentionPstmt = con.prepareStatement(retentionStr);
            
            retentionPstmt.setString(1, PretupsI.NET_STK_TRA);
            retentionRst = retentionPstmt.executeQuery();
            if (retentionRst.next()) {
				retention_period = Integer.parseInt(retentionRst.getString("retention_period"));
			}
			else {
				retention_period = 90;// default retention period in days
			}
            startTime = System.currentTimeMillis();
            // Starting BLOCK for transfer Data Processing
            _messageToWrite = null;
            _deletedUpto = null;
            dateTillKeepData = null;
            dateTillKeepDataSql = null;
            status = 0;
            // Get the date till when data must be retained in the System
            dateTillKeepData = ArchivePretupsUtil.constructXBeforeDate(new Date(), retention_period / 30);
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, " Date till when Transfer Data has to be retained=" + dateTillKeepData);
			}
            if (dateTillKeepData == null) {
                if (_logger.isDebugEnabled()) {
					_logger.debug(methodName, " Not able to calculate the date till when we have to keep data");
				}
                _messageToWrite = "Not able to calculate the date till when we have to keep data";
            } else {
                dateTillKeepDataSql = BTSLUtil.getSQLDateFromUtilDate(dateTillKeepData);
                // If month end date is greater than the data till when the data
                // must be kept in the System
                if (dateTillKeepDataSql.compareTo(_endDateSql) <= 0) {
                    _messageToWrite = "Transfer Data should be kept till " + dateTillKeepDataSql;
                } else {
                    // Check for the channel transfer data that is available
                    status = checkNetworkStockTxnItems(con, _startDateSql, _endDateSql);
                    if (status == 0) {
                        if (_logger.isDebugEnabled()) {
							_logger.debug(methodName, " Some problem while purging Transfer Data");
						}
                    }
                }
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, " Final Message After Executing for Transfer Data " + _messageToWrite);
				}
            }
            endTime = System.currentTimeMillis();
            difference = ((endTime - startTime) / (60 * 1000));
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, " *********IMP == Transfer Data PROCESSING TOOK =" + difference + " Min");
			}
            processEndTime = System.currentTimeMillis();
            difference = ((processEndTime - processStartTime) / (60 * 1000));
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, " *********IMP == DATA PROCESSING TOOK =" + difference + " Min");
			}
            _logger.debug(methodName, "***********TOTAL MEMORY******** :" + Runtime.getRuntime().totalMemory());
            _logger.debug(methodName, "***********FREE MEMORY******** :" + Runtime.getRuntime().freeMemory());
            _logger.debug(methodName, "***********AVAIALBLE MEMORY******** :" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, " Successfully Deleted Records-------------");
			}
        } catch (Exception e) {
            try {
                if (con != null) {
					con.rollback();
				}
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
					_logger.debug(methodName, " Exception while rollback" + ex);
				}
                _logger.errorTrace(methodName, e);
            }
            if (!BTSLUtil.isNullString(_messageToWrite)) {
				_logger.debug(methodName, "FileLogger.ERROR:--------------EXCEPTION  =" + _messageToWrite);
			}
            _logger.errorTrace(methodName, e);
            return;
        } finally {
            try {
                if (retentionPstmt != null) {
                    retentionPstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            try {
                if (retentionRst != null) {
                	retentionRst.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            try {
                if (con != null) {
					con.close();
				}
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, " Exception Closing connection in main: " + ex.getMessage());
				}
                _logger.errorTrace(methodName, ex);
            }

            try {
                if (_updatePstmt != null) {
					_updatePstmt.close();
				}
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
					_logger.debug(methodName, " Exception Closing prepared statement in main: " + ex.getMessage());
				}
                _logger.errorTrace(methodName, ex);
            }
            // System.exit(0); // System.exit not required as main method return
            // to JVM
        }
    }// end main

    /**
     * This method will check whether there are entries available for deletion
     * or not
     * 
     * @param p_con
     *            Connection
     * @param p_date
     *            Date
     * @param p_networkCode
     *            String
     * @return int Int
     * @throws Exception
     */

    private static int checkNetworkStockTxnItems(Connection p_con, java.sql.Date p_fromDate, java.sql.Date p_toDate) {
        final String methodName = "checkNetworkStockTxnItems";
        if (_logger.isDebugEnabled()) {
			_logger.info(methodName, "Entered with from date=" + p_fromDate + " to date=" + p_toDate);
		}

        // 0=ERROR
        // 1=SUCCESSFUL BUT DONE FOR MONTH
        // 2=SUCCESSFUL AND ALL DELETED
        int returnStatus = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int dateCompare = 0;
        java.sql.Date deleteUpto = null;
        java.sql.Date checkDate = null;
        java.sql.Date lastDayUptoArchived = null;
        java.sql.Date _endDateSql = (p_toDate);
        StringBuffer strBuffCheck = new StringBuffer("SELECT min(trunc( NST.txn_date)) MINDATE   ");
        strBuffCheck.append(" FROM NETWORK_STOCK_TRANSACTIONS NST ");
        try {
            lastDayUptoArchived = getLastArchivedDate(p_con, PretupsI.NET_STK_TRA, PretupsI.NET_STK_TRA_ITEMS);
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, " Last date till files have been made=" + lastDayUptoArchived + "Inital Deleted Upto Date=" + _deletedUpto);
			}
            if (lastDayUptoArchived == null) {
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, " No data to delete as none is written in file, Please make the file of data for backUp------");
				}
                _messageToWrite = "No Channel transfer Data to purge as none is written in file, Please make the file of data for backUp ";
                returnStatus = 1;
                return returnStatus;
            }
            _startDateSql = p_fromDate;
            // Check if the data exist for date less than the starting day of
            // the month
            pstmt = p_con.prepareStatement(strBuffCheck.toString());
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, "Executing query=" + strBuffCheck.toString());
			}
            rs = pstmt.executeQuery();
            if (rs.next()) {
                checkDate = rs.getDate("MINDATE");
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, "INFO---------MINIMUM DATE IN TABLE=" + checkDate + " start date =" + _startDateSql);
				}

                if (checkDate != null && checkDate.before(_startDateSql)) {
                    if (_logger.isDebugEnabled()) {
						_logger.debug(methodName, " Data still exist for previous months , please run the file creation process for those months first ");
					}
                    _messageToWrite = "Transfer Data still exist for previous months , please run the file creation process for those months first";
                    returnStatus = 2;
                    DatabasePurgingLog.purgeLog("NetworkStockTransactionPurging", "DATA FILE HAS NOT BEEN WRITTEN");
                    return returnStatus;
                }
            }
            if (lastDayUptoArchived.compareTo(_endDateSql) < 0) {
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, " Data File has not been written till " + _endDateSql + " , please execute the program to write the files first");
				}
                _messageToWrite = "As the Transfer data file has not been written records cannot be deleted";
                returnStatus = 2;
                DatabasePurgingLog.purgeLog("NetworkStockTransactionPurging", "DATA FILE HAS NOT BEEN WRITTEN");
                return returnStatus;
            }

            dateCompare = compareDate(_startDateSql, lastDayUptoArchived);
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, " After comparing " + _startDateSql + "and " + lastDayUptoArchived + " dates, Result=" + dateCompare);
			}

            // 0=Dates are equal , delete for that day only
            // 1=Delete for 1 month only
            // 2=Delete all

            if (dateCompare == 0) {
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, " Dates are equal, delete only for that date ");
				}
                deleteUpto = deleteNetworkStockTxnItems(p_con, _startDateSql, lastDayUptoArchived);

                if (deleteUpto == null) {
                    p_con.rollback();
                    if (_logger.isDebugEnabled()) {
						_logger.info(methodName, " No Transfer data deleted");
					}
                    _messageToWrite = "No Transfer data deleted";
                    returnStatus = 0;
                } else {
                    if (_logger.isDebugEnabled()) {
						_logger.info(methodName, " Successfully Delete records till  " + deleteUpto);
					}
                    _messageToWrite = "Deleted Transfer records till date=" + deleteUpto;
                    returnStatus = 1;
                }
            } else if (dateCompare == 1) {
                deleteUpto = deleteNetworkStockTxnItems(p_con, _startDateSql, _endDateSql);
                if (deleteUpto == null) {
                    p_con.rollback();
                    if (_logger.isDebugEnabled()) {
						_logger.info(methodName, " No Transfer data deleted");
					}
                    _messageToWrite = "No Transfer data deleted";
                    returnStatus = 0;
                } else {
                    if (_logger.isDebugEnabled()) {
						_logger.info(methodName, " Successfully Delete records till  " + deleteUpto);
					}
                    _messageToWrite = "Successfully Deleted Transfer records till  " + deleteUpto;
                    returnStatus = 1;
                }
            } else {
                // If the delete upto date is after the Last day of the month
                // then delete for month only
                if (lastDayUptoArchived.after(_endDateSql)) {
                    deleteUpto = deleteNetworkStockTxnItems(p_con, _startDateSql, _endDateSql);
                    if (deleteUpto == null) {
                        p_con.rollback();
                        if (_logger.isDebugEnabled()) {
							_logger.info(methodName, " No Transfer data deleted");
						}
                        _messageToWrite = "No Transfer data deleted";
                        returnStatus = 0;
                    } else {
                        if (_logger.isDebugEnabled()) {
							_logger.info(methodName, " Data purged till " + deleteUpto + ",Please run again to delete further data");
						}
                        _messageToWrite = "Transfer Data purged till " + deleteUpto + ",Please run again to delete further data";
                        returnStatus = 1;
                    }
                } else {
                    deleteUpto = deleteNetworkStockTxnItems(p_con, _startDateSql, lastDayUptoArchived);
                    if (deleteUpto == null) {
                        p_con.rollback();
                        if (_logger.isDebugEnabled()) {
							_logger.info(methodName, " No Transfer data deleted");
						}
                        _messageToWrite = "No Transfer data deleted";
                        returnStatus = 0;
                    } else {
                        if (_logger.isDebugEnabled()) {
							_logger.info(methodName, " Successfully Delete records till  " + deleteUpto);
						}
                        _messageToWrite = "Deleted Transfer records till date=" + deleteUpto;
                        returnStatus = 1;
                    }
                }
            }
            return returnStatus;
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, " Exception while checking Transfer record " + e);
			}
            _messageToWrite = "Exception while checking Transfer record for network code=" + _networkCode + " Exception=" + e;
            _logger.errorTrace(methodName, e);
        } finally {
            try {
                if (rs != null) {
					rs.close();
				}
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
					_logger.debug(methodName, " Exception Closing RS : " + ex.getMessage());
				}
                _logger.errorTrace(methodName, ex);
            }
            try {
                if (pstmt != null) {
					pstmt.close();
				}
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
					_logger.debug(methodName, " Exception Closing Prepared Stmt: " + ex.getMessage());
				}
                _logger.errorTrace(methodName, ex);
            }
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, " Exiting : returnStatus = " + returnStatus);
			}
        }
		return returnStatus;
    }

    /**
     * This method will get the last date for which the data has been written
     * for a table
     * 
     * @param p_con
     * @param p_tableName
     * @return
     */

    private static java.sql.Date getLastArchivedDate(Connection p_con, String p_tableName1, String p_tableName2) {
        final String methodName = "getLastArchivedDate";
        if (_logger.isDebugEnabled()) {
			_logger.info(methodName, " Getting Last date for table " + p_tableName1 + " and " + p_tableName2);
		}
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        java.sql.Date lastArchiveDate = null;
        StringBuffer strBuff = new StringBuffer("SELECT archived_upto  LASTDATE,deleted_upto  ");
        strBuff.append(" FROM archival_done_date WHERE table_name IN (?,?)  ");
        try {
            pstmt = p_con.prepareStatement(strBuff.toString());
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, " Last Archived Date query=" + strBuff.toString());
			}
            pstmt.setString(1, p_tableName1);
            pstmt.setString(2, p_tableName2);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                lastArchiveDate = rs.getDate("LASTDATE");
                _deletedUpto = rs.getDate("deleted_upto");
            } else {
                lastArchiveDate = null;
                _deletedUpto = null;
            }
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, " Exception while getting last date till data has been written=" + e);
			}
            lastArchiveDate = null;
            _logger.errorTrace(methodName, e);
        } finally {
            try {
                if (rs != null) {
					rs.close();
				}
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
					_logger.debug(methodName, " Exception Closing RS : " + ex.getMessage());
				}
                _logger.errorTrace(methodName, ex);
            }
            try {
                if (pstmt != null) {
					pstmt.close();
				}
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
					_logger.debug(methodName, "  Exception Closing Prepared Stmt: " + ex.getMessage());
				}
                _logger.errorTrace(methodName, ex);
            }
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, " Exiting : with last Archived Date = " + lastArchiveDate);
			}
        }
        return lastArchiveDate;
    }

    /**
     * This method will compare two sql dates
     * 
     * @param p_fromDate
     * @param p_toDate
     * @return int values =0 , dates are equal
     *         1= Date range is too large
     *         2= Date range is within 31 days
     */
    private static int compareDate(java.sql.Date p_fromDate, java.sql.Date p_toDate) {
        if (_logger.isDebugEnabled()) {
			_logger.info("", " p_fromDate = " + p_fromDate + " p_toDate=" + p_toDate);
		}
        int diff = 0;
        int returnFlag = 0;
        long fromDate = 0;
        long toDate = 0;
        int nodays = 0;
        diff = p_fromDate.compareTo(p_toDate);
        if (diff == 0) {
            if (_logger.isDebugEnabled()) {
				_logger.info("compareDate", " Dates are equal, nothing to delete");
			}
            returnFlag = 0;
        } else {
            fromDate = p_fromDate.getTime();
            toDate = p_toDate.getTime();
            nodays = BTSLUtil.parseLongToInt(  ((toDate - fromDate) / (1000 * 60 * 60 * 24)) );
            if (nodays > 31) {
                if (_logger.isDebugEnabled()) {
					_logger.info("compareDate", " To much data to purge");
				}
                returnFlag = 1;
            } else {
                if (_logger.isDebugEnabled()) {
					_logger.info("compareDate", " Data OK for delete");
				}
                returnFlag = 2;
            }
        }
        return returnFlag;
    }

    /**
     * This table will delete network stock transaction records
     * 
     * @param p_con
     *            Date
     * @param p_fromDate
     *            Date
     * @param p_toDate
     *            Date
     * @param p_archivedTillDate
     *            Date
     * @return
     * @throws Exception
     */
    private static java.sql.Date deleteNetworkStockTxnItems(Connection p_con, Date p_fromDate, Date p_toDate) {
        final String methodName = "deleteChannelTransferItemsEntries";
        if (_logger.isDebugEnabled()) {
			_logger.info(methodName, " Entered from date=" + p_fromDate + " to date=" + p_toDate);
		}
        CallableStatement cstmt = null;
        int retUpdate = 0;
        int delCount = 0;
        long noOfRecords = 0;
        long totalRecords = 0;
        Calendar cal = BTSLDateUtil.getInstance();
        try {

            if (_logger.isDebugEnabled()) {
				_logger.info("", "---------Transfer Records Summary---------");
			}
            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                cstmt = p_con.prepareCall("{call " + Constants.getProperty("currentschema") + ".purge_transaction_tables_pkg.DELETE_NETWORK_STOCK(?,?,?,?,?)}");
            } else {
                cstmt = p_con.prepareCall("{call purge_transaction_tables_pkg.DELETE_NETWORK_STOCK(?,?,?,?,?)}");
            }
            cstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            cstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            cstmt.registerOutParameter(3, Types.VARCHAR); // Message
            cstmt.registerOutParameter(4, Types.VARCHAR); // Message for log
            cstmt.registerOutParameter(5, Types.VARCHAR); // Sql Exception
            delCount = cstmt.executeUpdate();
            DatabasePurgingLog.purgeLog("NetworkStockTransactionPurging", "DATA SUCCESSFULLY DELETED");
            if (delCount < 0) {
                if (_logger.isDebugEnabled()) {
					_logger.debug("deleteNetworkStockTxnItems", " Not able to delete records till date=" + p_toDate);
				}
                throw new BTSLBaseException("Not able to delete records for date=" + p_fromDate);
            }
            noOfRecords = noOfRecords + delCount;
            if (p_con != null) {
                retUpdate = updateLastArchivedOp(p_con, PretupsI.NET_STK_TRA, PretupsI.NET_STK_TRA_ITEMS, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                if (retUpdate > 0) {
                    p_con.commit();
                    if (_logger.isDebugEnabled()) {
						_logger.info("deleteNetworkStockTxnItems", " Successfully Deleted Transfer Records for Date=" + p_fromDate);
					}
                } else {
                    p_con.rollback();
                    p_fromDate = null;
                    _messageToWrite = "Not able to update the dates table for Transfer";
                    throw new BTSLBaseException("Not able to update the dates table");
                }
            }
            if (_logger.isDebugEnabled()) {
				_logger.info("deleteNetworkStockTxnItems", " Records deleted=" + noOfRecords + ", Transfer data=" + p_fromDate);
			}
            totalRecords = totalRecords + noOfRecords;

            cal.setTimeInMillis(p_fromDate.getTime());
            cal.add(cal.DATE, 1);
            p_fromDate = new java.sql.Date(cal.getTime().getTime());
            // Reinitialize the Counters for next day
            noOfRecords = 0;
            if (_logger.isDebugEnabled()) {
				_logger.info("deleteNetworkStockTxnItems", " Records deleted:" + totalRecords);
			}
            cal.setTimeInMillis(p_fromDate.getTime());
            cal.add(cal.DATE, -1);
            p_fromDate = new java.sql.Date(cal.getTime().getTime());
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
				_logger.debug("deleteNetworkStockTxnItems", " Exception while deleteing records" + e);
			}
            p_fromDate = null;
            _logger.errorTrace(methodName, e);
        } finally {
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, " Exiting :  with Records deleted till Date = " + p_toDate);
			}
        }
        return BTSLUtil.getSQLDateFromUtilDate(p_toDate);
    }

    /**
     * Update the deleted upto field that the data for a table has been deleted
     * 
     * @param p_con
     *            Connection
     * @param p_tableName
     *            String
     * @param p_tableName1
     *            String
     * @param p_date
     *            Date
     * @return
     */
    private static int updateLastArchivedOp(Connection p_con, String p_tableName, String p_tableName1, java.sql.Date p_date) {
        final String methodName = "updateLastArchivedOp";
        if (_logger.isDebugEnabled()) {
			_logger.info(methodName, " Getting Last date for table " + p_tableName + " and " + p_tableName1 + " date=" + p_date);
		}
        int updateCount = 0;

        StringBuffer strBuff = new StringBuffer("UPDATE archival_done_date SET deleted_upto=?,modified_on=? ");
        strBuff.append(" WHERE table_name IN (?,?) ");

        try {
            // This is done so that if by chance deletion process is run for the
            // months that
            // are already deleted then it will not update the deleted upto
            // field
            if (_deletedUpto == null || _deletedUpto.before(p_date)) {
                // update the Deleted date in the dates table to know till when
                // the data has been deleted
                if (_updatePstmt == null) {
					_updatePstmt = p_con.prepareStatement(strBuff.toString());
				} else {
					_updatePstmt.clearParameters();
				}

                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, " query=" + strBuff.toString());
				}
                _updatePstmt.setDate(1, p_date);
                _updatePstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(new Date()));
                _updatePstmt.setString(3, p_tableName);
                _updatePstmt.setString(4, p_tableName1);
                updateCount = _updatePstmt.executeUpdate();
            } else {
                updateCount = 1;
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, " No need to update deleted upto field as the process is alreday run for this before");
				}
            }
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, " Exception while updating the archive table =" + e);
			}
            updateCount = 0;
            _logger.errorTrace(methodName, e);
        } finally {
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, "Exiting : updateCount = " + updateCount);
			}
        }
        return updateCount;
    }
}// end class