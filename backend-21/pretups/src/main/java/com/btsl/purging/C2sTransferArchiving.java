/*
 * Created on Dec 22, 2005
 */
package com.btsl.purging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.DatabasePurgingLog;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

/**
 * @author ankit.singhal
 */
public class C2sTransferArchiving {
    private static long _totalRecordsWrittenInFile = 0;
    private static String _c2sTransferTableName1 = PretupsI.C2S_TRA;
    private static String _c2sTransferTableName2 = PretupsI.C2S_TRA_ITEMS;
    private static String _c2sTransferFileName = PretupsI.C2S_TRA_ITEMS;
    public static SimpleDateFormat simpleDateFormat;
    private static String _month = null;
    private static String _year = null;
    private static int _monthInt = 0;
    private static int _yearInt = 0;
    private static Date _userToDate = null;
    private static Date _userFromDate = null;
    private static Log _logger = LogFactory.getLog(C2sTransferArchiving.class.getName());
    private static String _userEnterFromDayOnly = null;
    private static String _userEnterToDayOnly = null;
    private static String _masterDirectory = null;
    private static String _LINE = "------------------------------------------------------------------------------------";
    private static String _separator = "_";
    private static String _ENDOFFILE = "EOF";
    private static int _FILEFOOTERLENGTH = 50;
    private static String _FILEFOOTER = "ENDOFFILE=";

    /**
	 * to ensure no class instantiation 
	 */
    private C2sTransferArchiving() {
        
    }
    public static void main(String arg[]) {
        final String methodName = "main";
        String userNetworkCode = null;
        String c2sTransferLabel = null;
        String fileEXT = null;
        String directoryPath = null;
        long fileLength = 0;
        Date startDate = new Date();

        try {
            File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println(" Constants File Not Found .............");
                return;
            }
            File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println(" Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            // for taking user input
            String args[] = new String[10];

            // Flag if the process is allowed at a particular time
            boolean isOperationAllowed = false;
            String fromTime = null;
            String toTime = null;

            try {
                fromTime = Constants.getProperty("ARCHIVE_FTIME");
            } catch (Exception e) {
                fromTime = "0000";
                _logger.errorTrace(methodName, e);
            }
            try {
                toTime = Constants.getProperty("ARCHIVE_TTIME");
            } catch (Exception e) {
                toTime = "0300";
                _logger.errorTrace(methodName, e);
            }
            try {
                simpleDateFormat = new SimpleDateFormat(Constants.getProperty("SIMPLE_DATE_FORMAT"));
            } catch (Exception e) {
                simpleDateFormat = new SimpleDateFormat(PretupsI.DATE_FORMAT);
                _logger.errorTrace(methodName, e);
            }

            isOperationAllowed = ArchivePretupsUtil.archivalTime(fromTime, toTime);
            if (!isOperationAllowed) {
                if (_logger.isDebugEnabled()) {
					_logger.info("main", " This Process should be executed between " + fromTime + " and " + toTime);
				}
                return;
            }
            args = ArchivePretupsUtil.intractiveParameterCheck();

            if (!(args != null)) {
                return;
            }
            args[7] = "Archive";
            // displays the data for which processing will be done
            ArchivePretupsUtil.displayedUserInfo(args);
            userNetworkCode = BTSLUtil.NullToString(args[2]);
            _month = BTSLUtil.NullToString(args[3]);
            _year = BTSLUtil.NullToString(args[4]);
            _monthInt = Integer.parseInt(_month);
            String year = null;
            year = "" + _year.substring(_year.length() - 2);
            _yearInt = Integer.parseInt(year);
            _userFromDate = BTSLUtil.getDateFromDateString(args[5]);
            _userToDate = BTSLUtil.getDateFromDateString(args[6]);
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
				_logger.debug("main", " Error in Loading Files ...........................: " + e.getMessage());
			}
            _logger.errorTrace(methodName, e);
            return;
        }
        try {
            c2sTransferLabel = Constants.getProperty("C2S_TRA_LABEL");
            directoryPath = Constants.getProperty("DIR_PATH");
            if (BTSLUtil.isNullString(directoryPath)) {
            	_logger.error(methodName, "Directory Path is Missing ");
			}
            try {
                fileEXT = Constants.getProperty("FILEEXT");
            } catch (Exception e) {
                fileEXT = ".csv";
                _logger.errorTrace(methodName, e);
            }
            try {
                fileLength = Long.parseLong(Constants.getProperty("MAX_FILELENGTH"));
            } catch (Exception e) {
                fileLength = 10000;
                _logger.errorTrace(methodName, e);
            }
            if (_logger.isDebugEnabled()) {
				_logger.info("main", " File Length Successfully Uploaded");
			}
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
				_logger.debug("main", " ERROR in uploading File Length (loaded default)");
			}
            _logger.errorTrace(methodName, e);
            return;
        }
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
					_logger.info("main", " DATABASE Connection NULL ");
				}
                return;
            }
            _userEnterFromDayOnly = BTSLUtil.getDateStringFromDate(_userFromDate).substring(0, 2);
            _userEnterToDayOnly = BTSLUtil.getDateStringFromDate(_userToDate).substring(0, 2);
            _masterDirectory = ArchivePretupsUtil.createMasterDirectory(directoryPath, _year, _month, _userEnterFromDayOnly, _userEnterToDayOnly);
            if (_logger.isDebugEnabled()) {
				_logger.info("main", " Creating Master Directory = " + _masterDirectory);
			}
            // code block to check whether files exist in the directory or not
            boolean isFile = true;
            String data = null;
            while (isFile) {
                isFile = ArchivePretupsUtil.isFileExists(ArchivePretupsUtil.partialFileName(_year, _month, "_"), _masterDirectory);
                if (isFile) {
                    System.out.println("As archiving has been done previously. Please Move the files to the Separate folder and Press Y to continue");
                    data = ArchivePretupsUtil.getUserInputFromConsole();
                    if (BTSLUtil.NullToString(data).trim().indexOf("Y") != -1 || BTSLUtil.NullToString(data).trim().indexOf("y") != -1) {
						continue;
					}
                    System.out.println("Returning from Here ............. User Entered = " + data + " (operation aborted by user)");
                    return;
                } else {
					break;
				}
            }// end while
             // end of file checking block
            ArrayList networkList = ArchivePretupsUtil.getNetworks(con, PretupsI.CIRCLE_NETWORK_TYPE);
            if (networkList == null || networkList.isEmpty()) {
                if (_logger.isDebugEnabled()) {
					_logger.info("main", " Network List NULL, Control Returning ");
				}
                return;
            }
            String locCodeName = null;
            String networkCode = null;
            String networkName = null;
            if (_logger.isDebugEnabled()) {
				_logger.info("main", " userNetworkCode: " + userNetworkCode);
			}

            for (int i = 0; i < networkList.size(); i++) {
                locCodeName = (String) networkList.get(i);
                networkCode = locCodeName.substring(0, locCodeName.indexOf("#"));
                networkName = locCodeName.substring(locCodeName.indexOf("#") + 1);
                if (_logger.isDebugEnabled()) {
					_logger.info("main", " Network Name = " + networkName + "  Network Code = " + networkCode);
				}

                processC2sTransfer(con, directoryPath, _masterDirectory, _c2sTransferFileName, fileEXT, c2sTransferLabel, fileLength, _c2sTransferTableName1, _c2sTransferTableName2, networkCode, networkName, _userFromDate, _userToDate);

                if (_logger.isDebugEnabled()) {
					_logger.info("main", _LINE);
				}
            }
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
				_logger.debug("main", " " + e);
			}
            _logger.errorTrace(methodName, e);
            return;
        } finally {
            try {
                if (con != null) {
					con.close();
				}
            } catch (SQLException e1) {
                if (_logger.isDebugEnabled()) {
					_logger.debug("main", " SQLEXCEPTION " + e1);
				}
                _logger.errorTrace(methodName, e1);
            }
            Date endDate = new Date();
            if (_logger.isDebugEnabled()) {
				_logger.info("main", "Total Time Taken = " + ((endDate.getTime() - startDate.getTime()) / (60 * 1000)) + " Min");
			}
            if (_logger.isDebugEnabled()) {
				_logger.info("main", "Total Number of Records Written in Files = " + _totalRecordsWrittenInFile);
			}
            if (_logger.isDebugEnabled())
			 {
				_logger.info("main", "Exiting Main Method ..................");
			}
        }
    }// end main

    /**
     * @param p_fileLength
     * @return
     */
    public static long loadFileLength(String p_fileLength) {
        final String methodName = "loadFileLength";
        long fileLength = 0;
        try {
            fileLength = Long.parseLong(p_fileLength);
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
				_logger.debug("loadFileLength", "Parameter " + p_fileLength + " not found or wrong parameter");
			}
            fileLength = 10000;
            _logger.errorTrace(methodName, e);
        }
        return fileLength;
    }

    /**
     * This method is used for c2s transfer items transction
     * 
     * @param p_con
     *            Connection
     * @param p_directoyPath
     *            String
     * @param p_directoryName
     *            String
     * @param p_fileName
     *            String
     * @param p_fileExt
     *            String
     * @param p_rechargeMonth
     *            int
     * @param p_label
     *            String
     * @param p_maxFileLength
     *            long
     * @param p_tableName
     * @param p_tableName1
     * @param p_networkCode
     * @param p_networkName
     */
    public static void processC2sTransfer(Connection p_con, String p_directoyPath, String p_directoryName, String p_fileName, String p_fileExt, String p_label, long p_maxFileLength, String p_tableName1, String p_tableName2, String p_networkCode, String p_networkName, Date p_fromDate, Date p_toDate) {
        final String methodName = "processC2sTransfer";
        if (_logger.isDebugEnabled()) {
			_logger.info(methodName, " Entered:  " + p_directoryName + " " + p_fileName + " " + p_fileExt + " " + p_maxFileLength + " " + p_tableName1 + " " + p_tableName2 + " " + p_networkCode + " " + p_networkName);
		}
        StringBuffer queryBuf = new StringBuffer("SELECT ct.transfer_id||','||ct.transfer_date||','||ct.transfer_date_time||','||ct.network_code");
        queryBuf.append("||','||ct.sender_id||','||ct.sender_category||','||ct.product_code||','||ct.sender_msisdn||','||ct.receiver_msisdn");
        queryBuf.append("||','||ct.receiver_network_code||','||ct.transfer_value||','||ct.error_code||','||ct.request_gateway_type");
        queryBuf.append("||','||ct.request_gateway_code||','||ct.reference_id||','||ct.service_type||','||ct.differential_applicable");
        queryBuf.append("||','||ct.pin_sent_to_msisdn||','||ct.language||','||ct.country||','||ct.skey||','||ct.skey_generation_time");
        queryBuf.append("||','||ct.skey_sent_to_msisdn||','||ct.request_through_queue||','||ct.credit_back_status||','||ct.quantity");
        queryBuf.append("||','||ct.reconciliation_flag||','||ct.reconciliation_date||','||ct.reconciliation_by||','||ct.created_on");
        queryBuf.append("||','||ct.created_by||','||ct.modified_on||','||ct.modified_by||','||ct.transfer_status||','||ct.card_group_set_id");
        queryBuf.append("||','||ct.version||','||ct.card_group_id||','||ct.sender_transfer_value||','||ct.receiver_access_fee");
        queryBuf.append("||','||ct.receiver_tax1_type||','||ct.receiver_tax1_rate||','||ct.receiver_tax1_value||','||ct.receiver_tax2_type");
        queryBuf.append("||','||ct.receiver_tax2_rate||','||ct.receiver_tax2_value||','||ct.receiver_validity||','||ct.receiver_transfer_value");
        queryBuf.append("||','||ct.receiver_bonus_value||','||ct.receiver_grace_period||','||ct.receiver_bonus_validity");
        queryBuf.append("||','||ct.card_group_code||','||ct.receiver_valperiod_type||','||ct.temp_transfer_id||','||ct.transfer_profile_id");
        queryBuf.append("||','||ct.commission_profile_id||','||ct.differential_given||','||ct.grph_domain_code||','||ct.source_type");
        queryBuf.append("||','||ct.sub_service||','||cti.transfer_id||','||cti.msisdn||','||cti.entry_date||','||cti.request_value");
        queryBuf.append("||','||cti.previous_balance||','||cti.post_balance||','||cti.user_type||','||cti.transfer_type||','||cti.entry_type");
        queryBuf.append("||','||cti.validation_status||','||cti.update_status||','||cti.transfer_value||','||cti.interface_type");
        queryBuf.append("||','||cti.interface_id||','||cti.interface_response_code||','||cti.interface_reference_id||','||cti.subscriber_type");
        queryBuf.append("||','||cti.service_class_code||','||cti.msisdn_previous_expiry||','||cti.msisdn_new_expiry||','||cti.transfer_status");
        queryBuf.append("||','||cti.transfer_date||','||cti.transfer_date_time||','||cti.entry_date_time||','||cti.first_call||','||cti.sno");
        queryBuf.append("||','||cti.prefix_id||','||cti.service_class_id||','||cti.protocol_status||','||cti.account_status");
        queryBuf.append("||','||cti.adjust_dr_txn_type||','||cti.adjust_dr_txn_id||','||cti.adjust_dr_update_status");
        queryBuf.append("||','||cti.adjust_cr_txn_type||','||cti.adjust_cr_txn_id||','||cti.adjust_cr_update_status||','||cti.adjust_value ");
        queryBuf.append("FROM c2s_transfers ct, c2s_transfer_items cti WHERE ct.transfer_date>=? AND ct.transfer_date<=? AND cti.transfer_date>=? ");
        queryBuf.append("AND cti.transfer_date<=? AND ct.transfer_id=cti.transfer_id AND ");
        queryBuf.append(" ct.network_code=?");
        String query = queryBuf.toString();
        PreparedStatement pstmt = null;
        ResultSet rst = null;
        try {
            Date fromDate = null;
            Date toDate = null;
            ArrayList dateLst = new ArrayList();
            int status = 0;
            status = processArchivalDatesLatest(p_con, p_tableName1, p_tableName2, new Date(), p_fromDate, p_toDate, dateLst, p_networkCode);
            if (dateLst.size() > 0) {
                fromDate = (Date) dateLst.get(0);
                toDate = (Date) dateLst.get(1);
            }
            if (status == 0 && fromDate == null && toDate == null) {
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, "No Data found for c2s transfer for Processing ( Or Archival Date > To Date ). Returning From Here. ");
				}
                p_con.rollback();
                return;
            } else if (status == 0) {
                if (_logger.isDebugEnabled()) {
					_logger.debug(methodName, " Error in Updating Records(c2s transfer) . Returning From Here. ");
				}
                p_con.rollback();
                return;
            }
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, " " + p_networkName + "   " + p_networkCode + " " + " From Date = " + simpleDateFormat.format(fromDate) + " To Date = " + simpleDateFormat.format(toDate));
			}
            pstmt = p_con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            pstmt.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(fromDate));
            pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(toDate));
            pstmt.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(fromDate));
            pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(toDate));
            pstmt.setString(5, p_networkCode);
            rst = pstmt.executeQuery();
            writeInFile(p_directoyPath, p_directoryName, p_fileName, p_fileExt, rst, p_label, p_maxFileLength, false, toDate, fromDate, p_tableName1, p_networkCode, p_networkName);
        } catch (SQLException e1) {
            if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, "SQLException " + e1.getMessage());
			}
            if (p_con != null) {
				try {
                    p_con.rollback();
                } catch (SQLException e2) {
                    _logger.errorTrace(methodName, e2);
                }
			}
            _logger.errorTrace(methodName, e1);
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, "Exception " + e.getMessage());
			}
            if (p_con != null) {
				try {
                    p_con.rollback();
                } catch (SQLException e2) {
                    _logger.errorTrace(methodName, e2);
                }
			}
            _logger.errorTrace(methodName, e);
        } finally {
            if (rst != null) {
				try {
                    rst.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(methodName, e2);
                }
			}
            if (pstmt != null) {
				try {
                    pstmt.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(methodName, e3);
                }
			}
            if (p_con != null) {
				try {
                    p_con.commit();
                } catch (SQLException e2) {
                    _logger.errorTrace(methodName, e2);
                }
			}
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, "exiting :......................");
			}
        }
    }// end function

    /**
     * @param p_con
     *            Connection
     * @param p_tableName1
     *            String
     * @param p_tableName2
     *            String
     * @param presentDate
     *            Date
     * @param months
     *            Int
     * @param isOneMonth
     *            Boolean
     * @param toStoreDateLst
     *            Arraylist
     * @param networkCode
     *            String
     * @return
     * @throws Exception
     */
    public static int processArchivalDatesLatest(Connection p_con, String p_tableName1, String p_tableName2, Date p_presentDate, Date p_fromDate, Date p_toDate, ArrayList p_toStoreDateLst, String p_networkCode) {
        final String methodName = "processArchivalDatesLatest";
        if (_logger.isDebugEnabled()) {
			_logger.info(methodName, " entered ");
		}
        SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT);
        String queryDate = " SELECT archived_upto FROM archival_done_date WHERE table_name IN (?,?)  AND network_code =? ";
        int index = 0;
        int month = 0;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rst = null;
        boolean isDefaultDate = false;
        try {
            month = Integer.parseInt(Constants.getProperty("C2S_TRA_MONTHS"));
        } catch (Exception e) {
            month = 1;
            _logger.errorTrace(methodName, e);
        }
        try {
            pstmt1 = p_con.prepareStatement(queryDate);
            pstmt1.setString(1, p_tableName1.trim());
            pstmt1.setString(2, p_tableName2.trim());
            pstmt1.setString(3, p_networkCode.trim());
            rst = pstmt1.executeQuery();
            Date archivedUptoDate = null;
            if (rst.next()) {
                archivedUptoDate = rst.getDate("archived_upto");
                if (archivedUptoDate == null) {
                    archivedUptoDate = BTSLUtil.getDateFromDateString(Constants.getProperty("installation_date"));
                    isDefaultDate = true;
                    if (_logger.isDebugEnabled()) {
						_logger.info(methodName, " No entry found in database for table = " + p_tableName1 + " and " + p_tableName2 + " Network Code =" + p_networkCode);
					}
                }
            } else {
                archivedUptoDate = BTSLUtil.getDateFromDateString(Constants.getProperty("installation_date"));
                isDefaultDate = true;
            }
            Date oneMonthAfter = ArchivePretupsUtil.constructOneMonthAfterDate(archivedUptoDate);
            Date xMonthBefore = ArchivePretupsUtil.constructXBeforeDate(p_presentDate, month);
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, " Archived upto:" + sdf.format(archivedUptoDate) + " one month after:" + sdf.format(oneMonthAfter) + " x month before:" + sdf.format(xMonthBefore) + " first table:" + p_tableName1 + " second table:" + p_tableName2 + " and network code:" + p_networkCode);
			}
            if (oneMonthAfter.after(xMonthBefore)) {
                if (_logger.isDebugEnabled()) {
					_logger.debug(methodName, "  Error For(descrption given below) table= " + p_tableName1 + " and " + p_tableName2 + " Network Code=" + p_networkCode + " Total Month=" + month);
				}
                if (_logger.isDebugEnabled()) {
					_logger.debug(methodName, "  Xmonth before date is samller than one month after archival date, control returning from here");
				}
                p_fromDate = null;
                p_toDate = null;
                p_toStoreDateLst.add(p_fromDate);
                p_toStoreDateLst.add(p_toDate);
                index = 0;
                return index;
            }
            if ((p_fromDate.compareTo(archivedUptoDate) <= 0))// || new
                                                              // Date(p_fromDate.getTime()-24*60*60*1000).compareTo(archivedUptoDate)==0)&&
                                                              // p_toDate.compareTo(archivedUptoDate)>0)
            {
                if (!isDefaultDate) {
                    String updateQuery = " UPDATE archival_done_date SET archived_upto =?,modified_on =? WHERE table_name IN (?,?) and network_code =? ";
                    if (_logger.isDebugEnabled()) {
						_logger.info(methodName, " updateQuery: " + updateQuery);
					}
                    pstmt = p_con.prepareStatement(updateQuery);
                    int i = 1;
                    pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                    i++;
                    pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(new Date()));
                    i++;
                    pstmt.setString(i, p_tableName1.trim());
                    i++;
                    pstmt.setString(i, p_tableName2.trim());
                    i++;
                    pstmt.setString(i, p_networkCode.trim());
                    i++;
                    index = pstmt.executeUpdate();
                } else {
                    StringBuffer insertQueryBuff = new StringBuffer(" INSERT INTO archival_done_date (NETWORK_CODE, TABLE_NAME, ARCHIVED_UPTO, CREATED_ON, MODIFIED_ON, DELETED_UPTO) ");
                    insertQueryBuff.append("Values (?,?,?,?,?,?)");
                    if (_logger.isDebugEnabled()) {
						_logger.info(methodName, " updateQuery: " + insertQueryBuff.toString());
					}
                    pstmt = p_con.prepareStatement(insertQueryBuff.toString());
                    int i = 1;
                    pstmt.setString(i, p_networkCode.trim());
                    i++;
                    pstmt.setString(i, p_tableName1.trim());
                    i++;
                    pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                    i++;
                    pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(new Date()));
                    i++;
                    pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(new Date()));
                    i++;
                    pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(new Date()));
                    i++;
                    index = pstmt.executeUpdate();
                }
                p_toStoreDateLst.add(p_fromDate);
                p_toStoreDateLst.add(p_toDate);
            } else if (p_fromDate.compareTo(archivedUptoDate) > 0) {
                if (!isDefaultDate) {
					if (_logger.isDebugEnabled()) {
						_logger.info(methodName, " Data of previous months is still left for archival (for table) = " + p_tableName1 + " and " + p_tableName2 + " Network Code =" + p_networkCode + " .Please archive that data. Data Archived Upto " + sdf.format(archivedUptoDate));
					}
				}

                p_fromDate = null;
                p_toDate = null;
                p_toStoreDateLst.add(p_fromDate);
                p_toStoreDateLst.add(p_toDate);
                index = 0;
            }
            return index;
        } catch (SQLException e1) {
            if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, "SQLException " + e1.getMessage());
			}
            _logger.errorTrace(methodName, e1);
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, "Exception " + e.getMessage());
			}
            _logger.errorTrace(methodName, e);
        } finally {
            if (rst != null) {
				try {
                    rst.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(methodName, e2);
                }
			}
            if (pstmt != null) {
				try {
                    pstmt.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(methodName, e3);
                }
			}
            if (pstmt1 != null) {
				try {
                    pstmt1.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(methodName, e3);
                }
			}

            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, "exiting: (0 means data previously exists, 1 means successfully updated and 5 means beginning and ending dates of a month" + index);
			}
        }
		return index;
    }// end of function

    /**
     * This method is used to write into file
     * 
     * @param p_directoyPath
     *            String
     * @param p_directoryName
     *            String
     * @param p_fileName
     *            String
     * @param p_fileExt
     *            String
     * @param p_rs
     *            ResultSet
     * @param p_label
     *            String
     * @param p_maxFileLength
     *            long
     * @param isPlusMinusAllowed
     *            boolean
     * @param p_toDate
     *            Date
     * @param p_fromDate
     *            Date
     * @param p_tableName1
     *            String
     * @param p_networkCode
     *            String
     * @param p_networkName
     *            String
     */
    public static void writeInFile(String p_directoyPath, String p_directoryName, String p_fileName, String p_fileExt, ResultSet p_rs, String p_lable, long p_maxFileLength, boolean isPlusMinusAllowed, Date toDate, Date fromDate, String tableName1, String p_networkCode, String p_networkName) {
        final String methodName = "writeInFile";
        if (_logger.isDebugEnabled()) {
			_logger.info(methodName, "Entered: " + p_directoyPath + " " + p_directoryName + " " + p_fileName + " " + p_fileExt + " " + p_maxFileLength + " " + isPlusMinusAllowed + " " + toDate + " " + fromDate + " " + tableName1 + " " + p_networkName + " " + p_networkCode);
		}
        boolean isRollback = false;
        Date startTime = new Date();
        PrintWriter out = null;
        ArrayList fileNameLst = new ArrayList();
        long totalNoOfRecords = 0;
        try {
            String dirName = null;
            String fileName = null;
            long recordSetNumbers = 0;
            File newFile = null;
            ArrayList recordNumberLst = new ArrayList();
            if (p_rs != null) {
                p_rs.last();
                recordSetNumbers = p_rs.getRow();
                p_rs.beforeFirst();
            }
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, "" + p_networkName + " (" + p_networkCode + ")" + " " + recordSetNumbers);
			}
            if (recordSetNumbers == 0) {
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, "" + p_networkName + " (" + p_networkCode + ")" + " " + recordSetNumbers + "  No Records found (Returning ....................)");
				}
                return;
            }
            long count = 0;
            StringBuffer buff = new StringBuffer();

            int fileNumber = 1;
            fileName = ArchivePretupsUtil.constructFileName(_masterDirectory + File.separator + p_fileName, _yearInt, _monthInt, p_networkCode, fileNumber, p_fileExt, _separator);
            newFile = new File(fileName);
            fileNameLst.add(fileName);
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
            String tableHeader = ArchivePretupsUtil.constructFileHeader(tableName1, fromDate, toDate, p_lable, 1);
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, "" + fileName + " " + tableHeader);
			}
            out.write(tableHeader);
            String fileData = null;
            DatabasePurgingLog.archiveLog("C2sTransferArchiving", p_directoyPath, p_directoryName, fileName, tableName1, p_networkName, "START");
            while (p_rs.next()) {
                fileData = p_rs.getString(1);
                if (BTSLUtil.NullToString(fileData).indexOf("\n") != -1) {
					fileData = fileData.replaceAll("\n", "");
				}
                out.write(fileData + "\n");
                count++;
                totalNoOfRecords++;
                _totalRecordsWrittenInFile++;
                if (count >= p_maxFileLength) {
                    out.write(ArchivePretupsUtil.constructFileFooter(count, false));
                    if (_logger.isDebugEnabled()) {
						_logger.info(methodName, _ENDOFFILE + " " + fileName + " " + ArchivePretupsUtil.constructFileFooter(count, true).trim());
					}
                    recordNumberLst.add("" + count);
                    fileNumber = fileNumber + 1;
                    if (_logger.isDebugEnabled()) {
						_logger.info(methodName, "FILENAME=" + fileName);
					}
                    if (_logger.isDebugEnabled()) {
						_logger.info(methodName, "Present Records in File  = " + count + " Total Number of Records(As Yet) = " + totalNoOfRecords);
					}
                    DatabasePurgingLog.archiveLog("C2sTransferArchiving", p_directoyPath, p_directoryName, fileName, tableName1, p_networkName, "END-SUCCESS");
                    fileName = ArchivePretupsUtil.constructFileName(_masterDirectory + File.separator + p_fileName, _yearInt, _monthInt, p_networkCode, fileNumber, p_fileExt, _separator);
                    DatabasePurgingLog.archiveLog("C2sTransferArchiving", p_directoyPath, p_directoryName, fileName, tableName1, p_networkName, "START");
                    //out.close();
                    newFile = new File(fileName);
                    //out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                    BTSLUtil.closeOpenStream(out, newFile);
                    fileNameLst.add(fileName);
                    tableHeader = ArchivePretupsUtil.constructFileHeader(tableName1, fromDate, toDate, p_lable, fileNumber);
                    if (_logger.isDebugEnabled()) {
						_logger.info(methodName, "" + fileName + " " + tableHeader);
					}
                    out.write(tableHeader);
                    count = 0;
                }
            }// end of while loop
            DatabasePurgingLog.archiveLog("C2sTransferArchiving", p_directoyPath, p_directoryName, p_fileName, tableName1, p_networkName, "END-SUCCESS");
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, "FILENAME=" + fileName);
			}
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, " Present Records in File  = " + count + " Total Number of Records(As Yet) = " + totalNoOfRecords);
			}
            out.write(buff.toString());
            buff = null;
            if (count > 0) {
                out.write(ArchivePretupsUtil.constructFileFooter(count, false));
                recordNumberLst.add("" + count);
            } else {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                	_logger.debug(methodName, "File deleted successfully");
                }
                fileNameLst.remove(fileNameLst.size() - 1);
            }
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, _ENDOFFILE + " " + fileName + " " + ArchivePretupsUtil.constructFileFooter(count, true).trim());
			}
            isRollback = checkCreatedFiles(dirName, recordSetNumbers, totalNoOfRecords, fileNameLst, recordNumberLst);
            if (isRollback) {
                throw new BTSLBaseException("writeInFile Delete All Files and Rollback ");
            }
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, "" + p_fileName + " Network Code=" + p_networkCode + "  Error =" + e);
			}
            ArchivePretupsUtil.deleteAllFile(fileNameLst);
            _logger.errorTrace(methodName, e);
        } finally {
            Date endTime = new Date();
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, "" + p_networkCode + " Records=" + totalNoOfRecords + " Time Taken" + ((endTime.getTime() - startTime.getTime()) / (60 * 1000)) + " Min");
			}
            try {
            	if (out != null) {
    				out.close();
    			}
            } catch (Exception e1) {
            	_logger.errorTrace(methodName, e1);
            }
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, "Exiting ");
			}
        }
    }

    /**
     * @param directoryPath
     *            String
     * @param rsTotalNumberOfRecords
     *            Long
     * @param totalNumberOfRecords
     *            Long
     * @param fileNameList
     *            Arraylist
     * @param recordCountList
     *            Arraylis
     * @return
     */
    public static boolean checkCreatedFiles(String directoryPath, long rsTotalNumberOfRecords, long totalNumberOfRecords, ArrayList fileNameList, ArrayList recordCountList) {
        final String methodName = "checkCreatedFiles";
        if (_logger.isDebugEnabled()) {
			_logger.info(methodName, "entered: directoryPath=" + directoryPath + " rsTotalNumberOfRecords=" + rsTotalNumberOfRecords + " totalNumberOfRecords=" + totalNumberOfRecords + " FileNameList=" + fileNameList.size() + " RecordCountListSize=" + recordCountList.size());
		}
        boolean deleteAllFiles = false;
        File newFile = null;
        String fileName = null;
        try {
            if (rsTotalNumberOfRecords != totalNumberOfRecords) {
                deleteAllFiles = true;
                if (_logger.isDebugEnabled()) {
					_logger.info(methodName, "(rsTS != TS) " + rsTotalNumberOfRecords + " " + totalNumberOfRecords + " will be deleted");
				}
            } else {
                if (fileNameList.size() != recordCountList.size()) {
                    deleteAllFiles = true;
                    if (_logger.isDebugEnabled()) {
						_logger.info(methodName, "(fileNameLst != RecordLst) " + fileNameList.size() + " " + recordCountList.size() + " will be deleted");
					}
                } else {
                    String readFileFooter = null;
                    long noOfRecordsInFile = 0;
                    int index = 0;

                    for (int i = 0; i < fileNameList.size(); i++) {
                        fileName = (String) fileNameList.get(i);
                        newFile = new File(fileName);
                        readFileFooter = readFileFooter(newFile);
                        readFileFooter = BTSLUtil.NullToString(readFileFooter).trim();
                        index = readFileFooter.indexOf(_FILEFOOTER);
                        if (_logger.isDebugEnabled()) {
							_logger.info(methodName, "index value = " + index + " FooterValue=" + readFileFooter);
						}
                        if (index == -1) {
                            deleteAllFiles = true;
                            break;
                        }
                        noOfRecordsInFile = Long.parseLong(readFileFooter.substring(index + _FILEFOOTER.length()));
                        if (noOfRecordsInFile != Long.parseLong((String) recordCountList.get(i))) {
                            if (_logger.isDebugEnabled()) {
								_logger.info(methodName, "(noOfRecordsInFile != Count) " + noOfRecordsInFile + " " + Long.parseLong((String) recordCountList.get(i)) + " will be deleted");
							}
                            deleteAllFiles = true;
                            break;
                        }
                    }// end of for loop
                }// end of else
            }// end of outer else
        }// end of try block
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, "Exception: " + e);
			}
            deleteAllFiles = true;
            _logger.errorTrace(methodName, e);
        }
        if (_logger.isDebugEnabled()) {
			_logger.debug(methodName, " exiting  deleteAllFiles =" + deleteAllFiles);
		}
        return deleteAllFiles;
    }

    /**
     * @param filePathName
     *            File
     * @return
     */
    public static String readFileFooter(File filePathName) {
        String fileFooterInfo = null;
        RandomAccessFile raf = null;
        final String methodName = "readFileFooter";
        try {
            raf = new RandomAccessFile(filePathName, "r");
            if (raf != null && filePathName.length() - _FILEFOOTERLENGTH > 0) {
                raf.seek(filePathName.length() - _FILEFOOTERLENGTH);
                fileFooterInfo = raf.readLine();
            }
            if (_logger.isDebugEnabled()) {
				_logger.info(methodName, "File Footer:" + fileFooterInfo);
			}
        } catch (IOException e) {
            if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, "" + filePathName + "  IOException =" + e);
			}
            _logger.errorTrace(methodName, e);
        } finally {
            if (raf != null) {
				try {
                    raf.close();
                } catch (IOException e1) {
                    _logger.errorTrace(methodName, e1);
                }
			}
        }
        return fileFooterInfo;
    }
}// end class