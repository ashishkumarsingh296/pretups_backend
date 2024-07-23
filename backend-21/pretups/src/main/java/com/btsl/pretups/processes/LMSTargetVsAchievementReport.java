package com.btsl.pretups.processes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.EMailSender;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyPointsRedemptionVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.ibm.icu.util.Calendar;

/********Initial Creation by Diwakar for introducing Apache POI for huge excel data reading/writing*************/

public class LMSTargetVsAchievementReport {
	private static Log _log = LogFactory.getLog(LMSTargetVsAchievementReport.class.getName());
	private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO = null;
    private static ProcessStatusVO _processStatusMISVO = null;
    private static PreparedStatement _saveBonusStmt = null;
    private static PreparedStatement _checkUserExistStmt = null;
    private static PreparedStatement _updateBonusStmt = null;
    private static Locale _locale = null;
    private static int row = 0;
    private static CellStyle style = null;
    private static int currentRecordWritten = 0;
    private static int sheetCount = 0;
    private static int stepSize = 0;
    private static int noOfRowsPerTemplate=0;
    
    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            if(args.length <2 ||args.length>3 ) {
                System.out.println("Usage : LMSTargetVsAchievementReport [Constants file] [LogConfig file] [locale]");
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
            try {
                _locale = LocaleMasterCache.getLocaleFromCodeDetails(args[2]);
                if (_locale == null) {
                    _locale = LocaleMasterCache.getLocaleFromCodeDetails("0");
                    if (_log.isDebugEnabled()) {
                        _log.debug(METHOD_NAME, "Error : Invalid Locale " + args[2] + " It should be 0 or 1, thus using default locale code 0");
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MonthlyReport4Pos[main]", "", "", "",
                        "  Message:  Invalid Locale " + args[2] + " It should be 0 (EN) or 1 (OTH) ");
                }
            } catch (Exception e) {
                _log.error(METHOD_NAME, " Invalid locale : " + args[5] + " Exception:" + e.getMessage());
                _locale = new Locale("en", "US");
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MonthlyReport4Pos[main]", "", "", "",
                    "  Message:  Not able to get the locale");
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            LookupsCache.loadLookAtStartup();
        }// end try
        catch (Exception ex) {
        	System.out.println("Error in Loading Configuration files ...........................: " + ex);
        	ConfigServlet.destroyProcessCache();
            return;
        }

        try {
            new LMSTargetVsAchievementReport().process();            
        } catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception : " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.info(METHOD_NAME, " Exiting");
            }
            ConfigServlet.destroyProcessCache();
        }

    }
    
    /**
     * To process the request based on requirement
     * @author diwakar
     * @throws BTSLBaseException
     * @throws SQLException
     */
    public void process() throws BTSLBaseException, SQLException {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered: ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        boolean statusOk = false;
        Date processedUpto = null;
        Date processedUptoMIS = null;
        Date currentDate = new Date();
        Date dateCount = null;
        int count = 0;
        LoyaltyPointsRedemptionVO redemptionVO = null;
        String reportTo = null;
        String prevDateStr = null;
        int beforeInterval = 0;
        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            _processBL = new ProcessBL();
            // For MIS
            _processStatusMISVO = _processBL.checkProcessUnderProcess(con, ProcessI.C2SMIS);
            processedUptoMIS = _processStatusMISVO.getExecutedUpto();

            if (processedUptoMIS != null) {
                con.rollback();
                // Process should not execute until the MIS has not executed
                // successfully for previous day
                final Calendar cal4CurrentDate = BTSLDateUtil.getInstance();
                final Calendar cal14MisExecutedUpTo = BTSLDateUtil.getInstance();
                cal4CurrentDate.add(Calendar.DAY_OF_MONTH, -1);
                final Date currentDate1 = cal4CurrentDate.getTime(); // Current
                // Date
                cal14MisExecutedUpTo.setTime(processedUptoMIS);
                final Calendar cal24CurrentDate = BTSLDateUtil.getCalendar(cal4CurrentDate);
                final Calendar cal34MisExecutedUpTo = BTSLDateUtil.getCalendar(cal14MisExecutedUpTo);
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, "(currentDate - 1) = " + currentDate1 + " processedUptoMIS = " + processedUptoMIS);
                }
                if (cal24CurrentDate.compareTo(cal34MisExecutedUpTo) < 0) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(METHOD_NAME, "The MIS has not been executed for the previous day.");
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LMSTargetVsAchievementReport[process]", "", "", "",
                        "The MIS has not been executed for the previous day.");
                    throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
                }
                _processStatusVO = _processBL.checkProcessUnderProcess(con, ProcessI.LMS_TARGET_VS_ACHIEVEMENT);
                statusOk = _processStatusVO.isStatusOkBool();
                beforeInterval = BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval() / (60 * 24));
                // check process status.
                if (statusOk) {
                    processedUpto = _processStatusVO.getExecutedUpto();
                    if (processedUpto != null) {
                        processedUptoMIS = BTSLUtil.addDaysInUtilDate(processedUptoMIS, 1);// for
                        final Calendar cal = BTSLDateUtil.getInstance();
                        currentDate = cal.getTime(); // Current Date
                        currentDate=BTSLUtil.addDaysInUtilDate(currentDate,-beforeInterval);
                        try {
                            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT);
                            sdf.setLenient(false); 
                            prevDateStr = sdf.format(processedUpto);
                        } catch (Exception e) {
                            prevDateStr = "";
                            _log.errorTrace(METHOD_NAME, e);
                            
                        }
                        if (_log.isDebugEnabled()) {
                            _log.debug(METHOD_NAME,
                                "From date=" + prevDateStr + " To Date(currentDate-interval)=" + reportTo + " processedUpto.compareTo(currentDate-interval)=" + processedUpto
                                    .compareTo(currentDate));
                        }
                        // If process has already run for the last day, then you
                        // can't run it again )
                        if (processedUpto.compareTo(currentDate) > 0) {
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LMSTargetVsAchievementReport[main]", "", "",
                                "", "LMS Target Credit Controller has already been executed for the date=" + String.valueOf(currentDate));
                            return;
                        }
                    }
                } else {
                    throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
                }

            } else {
                throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
            }

            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_VOL_CREDIT_LOYAL_PTS))).booleanValue()) {
            	
	        	if(_log.isDebugEnabled()) {
					_log.error(METHOD_NAME," BTSLUtil.getSQLDateFromUtilDate(processedUpto)="+BTSLUtil.getSQLDateFromUtilDate(processedUpto)+" ,currentDate="+currentDate+" , compare= "+BTSLUtil.getSQLDateFromUtilDate(processedUpto).before(currentDate));				
				}
        	 String filePath = Constants.getProperty("LMSTARGETVSACHIEVEMENT_PATH");
        	 try {
                 final File fileDir = new File(filePath);
                 if (!fileDir.isDirectory()) {
                     fileDir.mkdirs();
                 }
             } catch (Exception e) {
                 _log.errorTrace(METHOD_NAME, e);
                 throw new BTSLBaseException(this, METHOD_NAME, "bulkuser.bulkusermodify.downloadfile.error.dirnotcreated", "selectDomainForBatchModify");

             }
             String fileName = null;
    			for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(currentDate); dateCount = BTSLUtil.addDaysInUtilDate(dateCount, 1)) {
    				fileName = Constants.getProperty("LMSTARGETVSACHIEVEMENT_FILENAMEPREFIX")+ BTSLUtil.getFileNameStringFromDate(dateCount)+ ".xlsx";
                    if (_log.isDebugEnabled()) {
                        _log.error(METHOD_NAME,
                            " dateCount=" + dateCount + " ,currentDate=" + dateCount.before(currentDate) + ", dateCount.before(processedUptoMIS)=" + dateCount
                                .before(processedUptoMIS));
                    }
                    final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
                    sdf.setLenient(false); // this is required else it will convert
                    reportTo = sdf.format(dateCount); // Current Date
                    // dateCount.before(processedUptoMIS)
                    if (dateCount.before(processedUptoMIS)) // we check whether MIS
                    // has been executed or
                    // not. If not, don't
                    // proceed further.
                    {
                    	_processStatusVO.setProcessID(ProcessI.LMS_TARGET_VS_ACHIEVEMENT);
                    	writeModifyExcel(ExcelFileIDI.BATCH_CHNL_USER_MODIFY,filePath,fileName,con,dateCount);         				       				                       

                    } else {
                        if (_log.isDebugEnabled()) {
                            _log.debug(METHOD_NAME, "Process has been executed upto = " + dateCount);
                        }
                        throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
                    }
                }
                
            }

            // Loading the details here.
            // one more loop here to handle skipped date(s)- Note that:
            // currentDate is (currentDate-1)
            if (_log.isDebugEnabled()) {
            	_log
                    .error(
                        METHOD_NAME,
                        " BTSLUtil.getSQLDateFromUtilDate(processedUpto)=" + BTSLUtil.getSQLDateFromUtilDate(processedUpto) + " ,currentDate=" + currentDate + " , compare= " + BTSLUtil
                            .getSQLDateFromUtilDate(processedUpto).before(currentDate));
            }
            
            // change in db executed_upto (datecount)
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Process has been executed upto = " + dateCount);
            }
            _processStatusVO.setExecutedUpto(dateCount);
            _processStatusVO.setExecutedOn(new Date());
            _processStatusVO.setProcessID(ProcessI.LMS_TARGET_VS_ACHIEVEMENT);
            final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
            final int maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);
            if (maxDoneDateUpdateCount > 0) {
                con.commit();
            } else {
                con.rollback();
                redemptionVO.setErrorCode(null);
                throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.LMS_COULD_NOT_UPDATE_MAX_DONE_DATE);
            }
        }// end daily process loop here
        catch (BTSLBaseException ex) {
            con.rollback();
            _log.errorTrace(METHOD_NAME, ex);
            if (dateCount != null) {
                _processStatusVO.setExecutedUpto(dateCount);
                _processStatusVO.setExecutedOn(new Date());
                _processStatusVO.setProcessID(ProcessI.LMS_TARGET_VS_ACHIEVEMENT);
                final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
                final int maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);
                if (maxDoneDateUpdateCount > 0) {
                    con.commit();
                } else {
                    con.rollback();
                }
            }

            _log.error(METHOD_NAME, "exit");

        }

        catch (Exception e) {
            con.rollback();
            if (dateCount != null) {
                _processStatusVO.setExecutedUpto(dateCount);
                _processStatusVO.setExecutedOn(new Date());
                _processStatusVO.setProcessID(ProcessI.LMS_TARGET_VS_ACHIEVEMENT);
                final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
                final int maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);
                if (maxDoneDateUpdateCount > 0) {
                    con.commit();
                } else {
                    con.rollback();
                }
            }
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            try {
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, "Process has been executed successfully ");
                }

                if (statusOk) {
                    if (_processBL.markProcessStatusAsComplete(con, _processStatusVO) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                    }
                }
				if (mcomCon != null) {
					mcomCon.close("LMSTargetVsAchievementReport#process");
					mcomCon = null;
				}
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            } finally {
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, " Count of given promotions: " + count);
                }
                if (_checkUserExistStmt != null) {
                    try {
                        _checkUserExistStmt.close();
                    } catch (Exception ex) {
                        _log.errorTrace(METHOD_NAME, ex);
                    }
                }
                if (_saveBonusStmt != null) {
                    try {
                        _saveBonusStmt.close();
                    } catch (Exception ex) {
                        _log.errorTrace(METHOD_NAME, ex);
                    }
                }
                if (_updateBonusStmt != null) {
                    try {
                        _updateBonusStmt.close();
                    } catch (Exception ex) {
                        _log.errorTrace(METHOD_NAME, ex);
                    }
                }
                if(mcomCon != null){mcomCon.close("LMSTargetVsAchievementReport#process");mcomCon=null;}
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, "Exiting");
                }

            }
        }

    }
    
   
    /**
     *
     * @author Diwakar
     * @param p_excelID
     * @param p_hashMap
     * @param messages
     * @param locale
     * @param p_fileName
     * @throws Exception
     */
    protected static void setCellComment(Cell cell, String message) {
    	Drawing drawing = cell.getSheet().createDrawingPatriarch();
        CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();
        ClientAnchor anchor = factory.createClientAnchor();
        anchor.setCol1(cell.getColumnIndex());
        anchor.setCol2(cell.getColumnIndex() + 5);
        anchor.setRow1(cell.getRowIndex());
        anchor.setRow2(cell.getRowIndex() + 5);
        anchor.setDx1(100);
        anchor.setDx2(100);
        anchor.setDy1(100);
        anchor.setDy2(100);
        Comment comment = drawing.createCellComment(anchor);
        RichTextString str = factory.createRichTextString(message);
        comment.setString(str);
        comment.setAuthor("Ashutosh:Genius");
        comment.setRow(cell.getRowIndex());
        comment.setColumn(cell.getColumnIndex());
        cell.setCellComment(comment);
    }
    
    /**
	  * To load Reference based Profile & User Details for active volume type LMS Promotions
	  * @author diwakar
	  * @param p_con
	  * @param p_dateCount
	  * @return
	  */
	ArrayList<LoyaltyPointsRedemptionVO> loadRefTargetProfile(Connection p_con, Date p_dateCount) {
		final String METHOD_NAME = "loadRefTargetProfile";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME," Entered : p_dateCount = "+p_dateCount);
		}
		PreparedStatement pstmtSelect=null;
		ResultSet rs = null;
		LoyaltyPointsRedemptionVO redemptionVO=null;
		ArrayList<LoyaltyPointsRedemptionVO> profileList=null;

		
		LMSTargetVsAchievementReportQry selectQueryBuffer = (LMSTargetVsAchievementReportQry) ObjectProducer.getObject(QueryConstants.LMS_TARGET_VS_ACHIEVEMENT_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
		String selectQuery=selectQueryBuffer.loadRefTargetProfileQry();
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME ,"SQL Query :"+selectQuery);
		}
		try {
			profileList=new ArrayList<LoyaltyPointsRedemptionVO>();
			int index=1;
			pstmtSelect =p_con.prepareStatement(selectQuery);
			pstmtSelect.setString(index++,PretupsI.PROFILE_VOL);
			pstmtSelect.setString(index++,PretupsI.STATUS_ACTIVE);
			pstmtSelect.setString(index++,PretupsI.LMS_PROFILE_TYPE);
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
				pstmtSelect.setString(index++,PretupsI.YES);
				pstmtSelect.setString(index++,PretupsI.NO);
				pstmtSelect.setString(index++,PretupsI.OPT_IN);
				pstmtSelect.setString(index++,PretupsI.NORMAL);	
			} else {
				pstmtSelect.setString(index++,PretupsI.NO);
				pstmtSelect.setString(index++,PretupsI.NORMAL);
			}
			pstmtSelect.setString(index++,PretupsI.NO);
			pstmtSelect.setString(index++,PretupsI.LMS_PROMOTION_TYPE_STOCK);
			pstmtSelect.setDate(index++,BTSLUtil.getSQLDateFromUtilDate(p_dateCount));
			pstmtSelect.setDate(index++,BTSLUtil.getSQLDateFromUtilDate(p_dateCount));
			pstmtSelect.setDate(index++,BTSLUtil.getSQLDateFromUtilDate(p_dateCount));
			rs = pstmtSelect.executeQuery();
			while (rs.next()) {
				redemptionVO= new LoyaltyPointsRedemptionVO();
				redemptionVO.setSetId(rs.getString("set_id"));
				redemptionVO.setItemName(rs.getString("set_name"));
				redemptionVO.setPeriodId(rs.getString("period_id"));
				redemptionVO.setServiceCode(rs.getString("service_code"));
				redemptionVO.setPointsType(rs.getString("points_type"));
				redemptionVO.setRefBaseAllowed(rs.getString("ref_based_allowed"));
				redemptionVO.setUserID(rs.getString("user_id"));
				redemptionVO.setModuleType(rs.getString("type"));
				redemptionVO.setMsisdn(rs.getString("msisdn"));
				redemptionVO.setTotalCrLoyaltyPoint(rs.getLong("points"));
				redemptionVO.setLmsTarget(rs.getString("target"));
				redemptionVO.setTarget(rs.getLong("target"));
				redemptionVO.setFromDate(rs.getDate("applicable_from"));
				redemptionVO.setPromoStartDate(String.valueOf(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from"))));
				redemptionVO.setParentID(rs.getString("parent_id"));
				redemptionVO.setNetworkID(rs.getString("network_code"));
				redemptionVO.setCategoryCode(rs.getString("category_code"));
				redemptionVO.setCategoryName(rs.getString("CATEGORY_NAME"));
				redemptionVO.setParentMsisdn(rs.getString("parent_msisdn"));
				redemptionVO.setParentEncryptedPin(rs.getString("parent_sms_pin"));
				redemptionVO.setProductCode(rs.getString("product_code"));
				redemptionVO.setProductShortCode(rs.getString("product_short_code"));
				redemptionVO.setOperatorContribution(rs.getInt("opt_contribution"));
				redemptionVO.setParentContribution(rs.getInt("prt_contribution"));
				redemptionVO.setApplicableToDate(rs.getDate("applicable_to"));
				redemptionVO.setVersion(rs.getString("version"));
				//Handling of OPT IN/OPT OUT as design changed 
				redemptionVO.setOptInOutEnabled(rs.getString("OPT_IN_OUT_ENABLED"));
				redemptionVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
				redemptionVO.setPromoEndDate(String.valueOf(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_to"))));
				redemptionVO.setUserName(rs.getString("user_name"));
				redemptionVO.setDomainName(rs.getString("DOMAIN_NAME"));
				redemptionVO.setGeographyName(rs.getString("GRPH_DOMAIN_NAME"));
				redemptionVO.setCurrentProcessDate(p_dateCount);
				redemptionVO.setSumTxnsDate(p_dateCount);
				redemptionVO = loadCummulativeTxnForUsers(p_con,  redemptionVO);
				profileList.add(redemptionVO);
			}
		} catch (SQLException sqe) {
			_log.errorTrace(METHOD_NAME,sqe);
		} catch (Exception ex) {
			_log.errorTrace(METHOD_NAME,ex);

		} finally	{
			try {
				if(rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME,e);
			}
			try {
				if(pstmtSelect != null) {
					pstmtSelect.close();
				}
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME,e);
			}
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, "Exiting: profileList size=" + profileList.size());
			}
		}
		return profileList;
	}
	
	 /**
	  * To load Non Reference based Profile & User Details for active volume type LMS Promotions
	  * @author diwakar
	  * @param p_con
	  * @param p_dateCount
	  * @return
	  */
	 ArrayList<LoyaltyPointsRedemptionVO> loadNonRefTargetProfiles(Connection p_con, Date p_dateCount) {
		final String METHOD_NAME = "loadNonRefTargetProfiles";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME," Entered p_dateCount = "+p_dateCount);
		}
		PreparedStatement pstmtSelect=null;
		ResultSet rs = null;
		LoyaltyPointsRedemptionVO redemptionVO=null;
		ArrayList<LoyaltyPointsRedemptionVO> profileList=null;
		LMSTargetVsAchievementReportQry selectQueryBuffer = (LMSTargetVsAchievementReportQry)
				ObjectProducer.getObject(QueryConstants.LMS_TARGET_VS_ACHIEVEMENT_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
		
		String selectQuery=selectQueryBuffer.loadNonRefTargetProfilesQry();
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME ,"SQL Query :"+selectQuery);
		}
		try {
			int index=1;
			profileList=new ArrayList<LoyaltyPointsRedemptionVO>();
			pstmtSelect =p_con.prepareStatement(selectQuery);
			pstmtSelect.setString(index++,PretupsI.PROFILE_VOL);
			pstmtSelect.setString(index++,PretupsI.STATUS_ACTIVE);
			pstmtSelect.setString(index++,PretupsI.LMS_PROFILE_TYPE);
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
				pstmtSelect.setString(index++,PretupsI.YES);
				pstmtSelect.setString(index++,PretupsI.NO);
				pstmtSelect.setString(index++,PretupsI.OPT_IN);
				pstmtSelect.setString(index++,PretupsI.NORMAL);	
			} else {
				pstmtSelect.setString(index++,PretupsI.NO);
				pstmtSelect.setString(index++,PretupsI.NORMAL);
			}
			pstmtSelect.setString(index++,PretupsI.NO);
			pstmtSelect.setString(index++,PretupsI.LMS_PROMOTION_TYPE_STOCK);
			pstmtSelect.setDate(index++,BTSLUtil.getSQLDateFromUtilDate(p_dateCount));
			pstmtSelect.setDate(index++,BTSLUtil.getSQLDateFromUtilDate(p_dateCount));
			pstmtSelect.setDate(index++,BTSLUtil.getSQLDateFromUtilDate(p_dateCount));
			rs = pstmtSelect.executeQuery();
			
			while (rs.next()) {
				redemptionVO= new LoyaltyPointsRedemptionVO();
				redemptionVO.setSetId(rs.getString("set_id"));
				redemptionVO.setItemName(rs.getString("set_name"));
				redemptionVO.setPeriodId(rs.getString("period_id"));
				redemptionVO.setPointsType(rs.getString("points_type"));
				redemptionVO.setRefBaseAllowed(rs.getString("ref_based_allowed"));
				redemptionVO.setUserID(rs.getString("user_id"));
				redemptionVO.setModuleType(rs.getString("type"));
				redemptionVO.setMsisdn(rs.getString("msisdn"));
				redemptionVO.setParentID(rs.getString("parent_id"));
				redemptionVO.setEndRange(rs.getString("end_range"));
				redemptionVO.setToRange(rs.getLong("end_range"));
				redemptionVO.setTotalCrLoyaltyPoint(rs.getLong("points"));
				redemptionVO.setFromDate(rs.getDate("applicable_from"));
				redemptionVO.setPromoStartDate(String.valueOf(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from"))));
				redemptionVO.setOperatorContribution(rs.getInt("opt_contribution"));
				redemptionVO.setParentContribution(rs.getInt("prt_contribution"));
				redemptionVO.setServiceCode(rs.getString("service_code"));
				redemptionVO.setProductCode(rs.getString("product_code"));
				redemptionVO.setProductShortCode(rs.getString("product_short_code"));
				redemptionVO.setNetworkID(rs.getString("network_code"));
				redemptionVO.setCategoryCode(rs.getString("category_code"));
				redemptionVO.setCategoryName(rs.getString("CATEGORY_NAME"));
				redemptionVO.setParentMsisdn(rs.getString("parent_msisdn"));
				redemptionVO.setParentEncryptedPin(rs.getString("parent_sms_pin"));
				redemptionVO.setApplicableToDate(rs.getDate("applicable_to"));
				redemptionVO.setVersion(rs.getString("version"));
				//Handling of OPT IN/OPT 
				redemptionVO.setOptInOutEnabled(rs.getString("OPT_IN_OUT_ENABLED"));
				redemptionVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
				redemptionVO.setPromoEndDate(String.valueOf(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_to"))));
				redemptionVO.setUserName(rs.getString("user_name"));
				redemptionVO.setDomainName(rs.getString("DOMAIN_NAME"));
				redemptionVO.setGeographyName(rs.getString("GRPH_DOMAIN_NAME"));
				redemptionVO.setTarget(rs.getLong("end_range"));
				redemptionVO.setCurrentProcessDate(p_dateCount);
				redemptionVO.setSumTxnsDate(p_dateCount);
				redemptionVO = loadCummulativeTxnForUsers(p_con,  redemptionVO);
				profileList.add(redemptionVO);
			}
		} catch (SQLException sqe) {
			_log.errorTrace(METHOD_NAME,sqe);
		} catch (Exception ex) {
			_log.errorTrace(METHOD_NAME,ex);

		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME,e);
			}
			try {
				if(pstmtSelect != null) {
					pstmtSelect.close();
				}
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME,e);
			}
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, "Exiting: profileList size=" + profileList.size());
			}
		}
		return profileList;

	}

	/**
	 * To call for writing the excel sheet
	 * @author diwakar
	 * @param p_excelID
	 * @param p_fileName
	 * @param fileName 
	 * @param p_con
	 * @param p_dateCount
	 * @throws Exception
	 */
    public void writeModifyExcel(String p_excelID, String p_filePath, String p_fileName, Connection p_con, Date p_dateCount) throws ParseException {
    	final String METHOD_NAME = "writeModifyExcel";            
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " p_excelID: " + p_excelID + " _locale: " + _locale + " p_fileName: " + p_fileName+" p_dateCount:"+p_dateCount);
        }
        SXSSFSheet  worksheet1 = null;
        String noOfRowsInOneTemplate = null; // No. of users data in one sheet
       
        SXSSFWorkbook  workbook = null;
        CreationHelper factory = null;
        currentRecordWritten = 0;
        sheetCount = 0;
        stepSize = 0;
        noOfRowsPerTemplate=0;
        row =0;
        
    	workbook = new SXSSFWorkbook();  
    	workbook.setCompressTempFiles(true);
    	factory = workbook.getCreationHelper();
    	style = workbook.createCellStyle();
        try( FileOutputStream outputStream = new FileOutputStream(p_filePath+p_fileName);) {
        	//Re-initization of variables
        	
        	
        	Font times16font = workbook.createFont();
        	times16font.setFontName("Arial");
        	times16font.setBold(true);
        	short fontHeight = 14;
        	times16font.setFontHeightInPoints( fontHeight);
        	style.setFont(times16font);
        	try {
                noOfRowsInOneTemplate = Constants.getProperty("NUMBEROFROWS_PERTEMPLATEFILE_LMSTARGETVSACHIEVEMENTREPORT");
                int noOfRowsPerTemplate = 0;
                if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
                    noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
                    noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
                } else {
                    noOfRowsPerTemplate = 1048500; // Default value of rows 
                }
                worksheet1 = (SXSSFSheet) workbook.createSheet("Template "+sheetCount);
                sheetCount++;
                worksheet1.setRandomAccessWindowSize(100);
                stepSize = noOfRowsPerTemplate;
                writeHeader(worksheet1, p_dateCount);
                writeLabel(worksheet1);
    			//heading of first sheet ends here
                ArrayList<LoyaltyPointsRedemptionVO> userProfileDetailListRef = null;
                ArrayList<LoyaltyPointsRedemptionVO> userProfileDetailListNonRef = null;
                userProfileDetailListNonRef = loadNonRefTargetProfiles(p_con, p_dateCount);
                workbook = writeData(p_dateCount,userProfileDetailListNonRef,workbook,worksheet1,outputStream);
                userProfileDetailListNonRef = null;
                userProfileDetailListRef = loadRefTargetProfile(p_con, p_dateCount);
                workbook = writeData(p_dateCount,userProfileDetailListRef,workbook,worksheet1,outputStream);
                userProfileDetailListRef=null;
                workbook.write(outputStream);
    			outputStream.close();
    			workbook.dispose();
                Runtime runtime = Runtime.getRuntime();
                long memory = runtime.totalMemory() - runtime.freeMemory();
                _log.debug(METHOD_NAME,"Used memory is megabytes: " + (memory)/1048576);
                // Run the garbage collector
                runtime.gc();
                // Calculate the used memory
                memory = runtime.totalMemory() - runtime.freeMemory();
                _log.debug(METHOD_NAME,"Used memory is megabytes: " + (memory)/1048576);
                sendEmail(p_filePath,p_fileName);
                if(_log.isDebugEnabled()) {
                	 _log.debug(METHOD_NAME,"LMSTargetVsAchievementReport has been generated successfully for p_dateCount = "+p_dateCount);
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMSTargetVsAchievementReport[writeModifyExcel]", "", "", "",
                     "Exception:" + ex.getMessage());
                throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
            } finally {
                if(_log.isDebugEnabled()){
                	  _log.debug(METHOD_NAME,"Exiting : p_dateCount = " +p_dateCount );
                }
                
            }         
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            
        } finally {
            worksheet1 = null;
            workbook = null;
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Exiting");
            }
        }
    }
    
    /**
     * To write  data into the excel sheet
     * @author diwakar
     * @param p_dateCount
     * @param userProfileDetailList
     * @param workbook
     * @param worksheet1
     * @param outputStream
     * @return
     * @throws IOException
     * @throws ParseException 
     */
    public SXSSFWorkbook writeData(Date p_dateCount, ArrayList<LoyaltyPointsRedemptionVO> userProfileDetailList,SXSSFWorkbook workbook, SXSSFSheet worksheet1,FileOutputStream outputStream) throws IOException, ParseException{
    	final String METHOD_NAME="writeData";
    	if(_log.isDebugEnabled()) {
    		_log.debug(METHOD_NAME, "p_dateCount = "+p_dateCount+",userProfileDetailList = "+userProfileDetailList);
    	}
    	short col = 0;
        Row rowdata = null;
 		Cell cell = null; 		
 		int totalRecords = userProfileDetailList.size();
 		for(int recordIndex=0;recordIndex<totalRecords;recordIndex++){
 			currentRecordWritten++;
            if((currentRecordWritten % stepSize)==0) {
            	Runtime runtime = Runtime.getRuntime();
                long memory = runtime.totalMemory() - runtime.freeMemory();
                _log.debug(METHOD_NAME,"Used memory is megabytes: " + (memory)/1048576);
                runtime.gc();
                memory = runtime.totalMemory() - runtime.freeMemory();
                _log.debug(METHOD_NAME,"Used memory is megabytes: " + (memory)/1048576);
            	worksheet1 = (SXSSFSheet) workbook.createSheet("Template "+sheetCount);
            	sheetCount++;
            	worksheet1.setRandomAccessWindowSize(100);
            	row =0; 
            	col = 0;
            	writeHeader(worksheet1, p_dateCount);
                writeLabel(worksheet1);
    			stepSize += noOfRowsPerTemplate;
            }    
            //End of next template heading
            //Write date into the templates
            LoyaltyPointsRedemptionVO redemptionVO= new LoyaltyPointsRedemptionVO();
            redemptionVO = userProfileDetailList.get(recordIndex);
            row++;
            col = 0;
            rowdata = worksheet1.createRow(row);
            
            cell = rowdata.createCell( col);            			
			cell.setCellValue(redemptionVO.getItemName());
			col++;
			
			cell = rowdata.createCell(col);
			cell.setCellValue(redemptionVO.getSetId());
			col++;
             	
			cell = rowdata.createCell( col);
			cell.setCellValue(redemptionVO.getPromoStartDate());
			col++;
			
			cell = rowdata.createCell(col);
			cell.setCellValue(redemptionVO.getPromoEndDate());
			col++;
			
			cell = rowdata.createCell( col);
			cell.setCellValue(redemptionVO.getUserName());
			col++;
			
			cell = rowdata.createCell( col);
			cell.setCellValue(redemptionVO.getDomainName());
			col++;
			
			cell = rowdata.createCell(col);
			cell.setCellValue(redemptionVO.getCategoryName());
			col++;
			
			cell = rowdata.createCell( col);
			cell.setCellValue(redemptionVO.getGeographyName());
			col++;
			
			cell = rowdata.createCell( col);
			cell.setCellValue(redemptionVO.getPeriodId());
			col++;
			
			cell = rowdata.createCell( col);
			cell.setCellValue(BTSLUtil.getDisplayAmount(redemptionVO.getTarget()));
			col++;
			
			cell = rowdata.createCell( col);
			cell.setCellValue(BTSLUtil.getDisplayAmount(redemptionVO.getSumAmount()));
			col++;
			
			cell = rowdata.createCell( col);
			if(redemptionVO.getSumAmount() <redemptionVO.getTarget()){
				cell.setCellValue(BTSLUtil.getDisplayAmount(redemptionVO.getTarget()-redemptionVO.getSumAmount()));
			} else { 
				cell.setCellValue(0);
			}
          }  
	 		if(_log.isDebugEnabled()) {
	    		_log.debug(METHOD_NAME, "p_dateCount = "+p_dateCount+",currentRecordWritten = "+currentRecordWritten);
	    	}
			return workbook;
    }
    
    /**
	 * To write header into the excel sheet 
	 * @author diwakar
	 * @param worksheet1 
	 * @param p_dateCount
     * @throws ParseException 
	 */
	public void writeHeader(SXSSFSheet worksheet1,Date p_dateCount) throws ParseException{
		String keyName = null;
		Row rowdata = null;
	 	Cell cell = null;
	 	short col=0;
		rowdata = worksheet1.createRow(row);       			
        keyName = BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.initiate.heading",null);
        cell = rowdata.createCell( col);
		cell.setCellStyle(style);
		cell.setCellValue(keyName);			
		row++;
        col = 0;
        keyName = BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.header.downloadedby",null);
        rowdata = worksheet1.createRow(row);
        cell = rowdata.createCell( col);
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		++col;
		cell = rowdata.createCell(col);
		cell.setCellValue(PretupsI.SYSTEM_USER);
        row++;
        col = 0;
        keyName = BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.header.createdDate",null);
        rowdata = worksheet1.createRow(row);
        cell = rowdata.createCell( col);
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		++col;
		cell = rowdata.createCell( col);
		cell.setCellValue(BTSLUtil.getFileNameStringFromDate(p_dateCount));
		row++;
        col = 0;
        
	}
	
	/**
	 * To write label into the excel sheet 
	 * @author diwakar
	 * @param worksheet1 
	 */
	public void writeLabel(SXSSFSheet worksheet1){
		String keyName = null;
		Row rowdata = null;
	 	Cell cell = null;
	 	short col = 0;
        keyName = BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.lmsprofile",null);
        rowdata = worksheet1.createRow(row);
        cell = rowdata.createCell( col);
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		setCellComment(cell,BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.lmsprofile.comment",null));
	
		keyName = BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.profileid",null);
        col++;
        cell = rowdata.createCell( col);
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		setCellComment(cell,BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.profileid.comment",null));
        
        keyName = BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.details.profilestartdate",null);
        col++;
        cell = rowdata.createCell( col);
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		setCellComment(cell,BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.profilestartdate.comment",null));

        keyName = BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.details.profileenddate",null);
        col++;
        cell = rowdata.createCell( col);
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		setCellComment(cell,BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.profileenddate.comment",null));
		
		keyName = BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.details.username",null);
        col++;
        cell = rowdata.createCell( col);
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		setCellComment(cell,BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.username.comment",null));
		
		keyName = BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.details.userdomain",null);
        col++;
        cell = rowdata.createCell( col);
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		setCellComment(cell,BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.userdomain.comment",null));
		
		keyName = BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.details.usercategory",null);
        col++;
        cell = rowdata.createCell( col);
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		setCellComment(cell,BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.usercategory.comment",null));
		
		keyName = BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.details.usergeography",null);
        col++;
        cell = rowdata.createCell( col);
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		setCellComment(cell,BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.usergeography.comment",null));
		
		keyName = BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.details.targettype",null);
        col++;
        cell = rowdata.createCell( col);
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		setCellComment(cell,BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.targettype.comment",null));
		
		keyName = BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.details.targetvalue",null);
        col++;
        cell = rowdata.createCell( col);
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		setCellComment(cell,BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.targetvalue.comment",null));
		
		keyName = BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.details.targetacheived",null);
        col++;
        cell = rowdata.createCell( col);
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		setCellComment(cell,BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.targetacheived.comment",null));
		
		keyName = BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.details.targetpending",null);
        col++;
        cell = rowdata.createCell(col);
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		setCellComment(cell,BTSLUtil.getMessage(_locale, "lms.target.vs.achievement.report.xlsfile.targetpending.comment",null));
		row++;
		        
	}
	
	/**
	 * To calculate the  sum of transfer amount based on period id of LMS profile 
	 * @author diwakar
	 * @param fromDate
	 * @return sum of transfer amount
	 */
	public LoyaltyPointsRedemptionVO loadCummulativeTxnForUsers(Connection p_con, LoyaltyPointsRedemptionVO p_redemptionVO)	{
		final String METHOD_NAME = "loadCummulativeTxnForUsers";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME," Entered with userId:"+p_redemptionVO.getUserID(),"CurrentProcessingDate:"+p_redemptionVO.getCurrentProcessDate() );
		}
		PreparedStatement pstmtSelect=null;
		ResultSet rs = null;
		Date currentProcessDate = p_redemptionVO.getCurrentProcessDate();
		Date processedDate= new Date();
		SimpleDateFormat sdf = new SimpleDateFormat (PretupsI.DATE_FORMAT);
		String date1=sdf.format(currentProcessDate); 
		try {
		 processedDate=BTSLUtil.getDateFromDateString(date1);
		} catch (Exception e) {
			_log.errorTrace(METHOD_NAME,e);
		}
		int dayDiff = 0;
		//this will determine whether we will run the query or not.
		//Date currentDate = new Date();
		StringBuffer selectQueryBuffer= new StringBuffer();
		if("C2S".equalsIgnoreCase(p_redemptionVO.getModuleType()) && "ALL".equalsIgnoreCase(p_redemptionVO.getServiceCode())) {
			selectQueryBuffer.append(" SELECT SUM (dctd.sender_transfer_amount) FROM daily_c2s_trans_details dctd ");
			selectQueryBuffer.append(" WHERE ");
			if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" dctd.user_id=? AND dctd.trans_date=?  ");
			} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId()) || "MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId()) || "EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId()))	{
				selectQueryBuffer.append(" dctd.user_id =? and dctd.trans_date >= ? and dctd.trans_date <=? ");
			}
			String selectQuery=selectQueryBuffer.toString();
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME ,"userId:"+p_redemptionVO.getUserID()+" , SQL Query :"+selectQuery);
			}
			try {
				if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					pstmtSelect =p_con.prepareStatement(selectQuery);
					pstmtSelect.setString(1,p_redemptionVO.getUserID());
					pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
					rs = pstmtSelect.executeQuery();
				} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getWeeklyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInWeek = 7;
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInWeek;
					if(dayDiff == 0 ) {												
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(date) );
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						rs = pstmtSelect.executeQuery();
					}
				} else if("MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getMonthlyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInCreatedMonth = getNoOfDaysInCreatedLMSProfileOnMonth(p_redemptionVO.getFromDate());
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInCreatedMonth; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getFromDate()));
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						rs = pstmtSelect.executeQuery();
					}
				} else if("EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					dayDiff = BTSLUtil.getDifferenceInUtilDates(processedDate,p_redemptionVO.getApplicableToDate()); 
					if(dayDiff==0) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getFromDate()) );
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getApplicableToDate()));
						rs = pstmtSelect.executeQuery();
					}
				}
				if(rs!=null) {
					while (rs.next()) {
						if(_log.isDebugEnabled()) {
							_log.debug(METHOD_NAME, "Resultset is found for UserID = "+p_redemptionVO.getUserID());
						}						
						p_redemptionVO.setSumAmount(rs.getLong(1));
						p_redemptionVO.setBonusCreditDateReached(true);
					}
				} else {
					p_redemptionVO.setBonusCreditDateReached(false);
					if(_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME, "Resultset is not found for UserID = "+p_redemptionVO.getUserID());
					}
				}
			} catch (SQLException sqe) {
				_log.errorTrace(METHOD_NAME,sqe);
			}  catch (Exception ex) {
				_log.errorTrace(METHOD_NAME,ex);

			} finally {
				try {
					if(rs != null) {
						rs.close();
					}
				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME,e);
				}
				try {
					if(pstmtSelect != null) {
						pstmtSelect.close();
					}
				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME,e);
				}
				if(_log.isDebugEnabled()) {
					_log.debug(METHOD_NAME, "Exiting with sum Amount:"+p_redemptionVO.getSumAmount()+" , UserID = "+p_redemptionVO.getUserID());
				}
			}

		} else if("C2S".equalsIgnoreCase(p_redemptionVO.getModuleType()) && "RC".equalsIgnoreCase(p_redemptionVO.getServiceCode()))	{
			selectQueryBuffer.append(" SELECT SUM (dctd.sender_transfer_amount) FROM daily_c2s_trans_details dctd ");
			selectQueryBuffer.append(" WHERE dctd.service_type='RC'");
			if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" and dctd.user_id=? AND dctd.trans_date=? AND dctd.service_type=? ");
			} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId()) || "MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())|| "EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" and dctd.user_id =? and dctd.service_type=? and dctd.trans_date >= ? and dctd.trans_date <=? ");
			}
			String selectQuery=selectQueryBuffer.toString();
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME ,"userId:"+p_redemptionVO.getUserID()+" , SQL Query :"+selectQuery);
			}
			try {
				if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					pstmtSelect =p_con.prepareStatement(selectQuery);
					pstmtSelect.setString(1,p_redemptionVO.getUserID());
					pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
					pstmtSelect.setString(3,PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
					rs = pstmtSelect.executeQuery();
				} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getWeeklyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInWeek = 7;
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInWeek; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setString(2,PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(date) );
						pstmtSelect.setDate(4,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						rs = pstmtSelect.executeQuery();
					}
				} else if("MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getMonthlyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInCreatedMonth = getNoOfDaysInCreatedLMSProfileOnMonth(p_redemptionVO.getFromDate());
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInCreatedMonth; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setString(2,PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(date));
						pstmtSelect.setDate(4,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						rs = pstmtSelect.executeQuery();
					}
				} else if("EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					dayDiff = BTSLUtil.getDifferenceInUtilDates(processedDate,p_redemptionVO.getApplicableToDate()); 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setString(2,PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getFromDate()) );
						pstmtSelect.setDate(4,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getApplicableToDate()));
						rs = pstmtSelect.executeQuery();
					}
				}
				
				if(rs!=null) {
					if(_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME, "Resultset is found for UserID = "+p_redemptionVO.getUserID());
					}
					while (rs.next()) {
						p_redemptionVO.setSumAmount(rs.getLong(1));
						p_redemptionVO.setBonusCreditDateReached(true);
					}
				} else {
					p_redemptionVO.setBonusCreditDateReached(false);
					if(_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME, "Resultset is not found for UserID = "+p_redemptionVO.getUserID());
					}
				}
			} catch (SQLException sqe) {
				_log.error(METHOD_NAME, "SQLException : " + sqe);
				_log.errorTrace(METHOD_NAME,sqe);
			} catch (Exception ex) {
				_log.error("", "Exception : " + ex);
				_log.errorTrace(METHOD_NAME,ex);

			} finally {
				try {
					if(rs != null) {
						rs.close();
					}
				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME,e);
				}
				try { 
					if(pstmtSelect != null) {
						pstmtSelect.close();
					}
				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME,e);
				}
				if(_log.isDebugEnabled()) {
					_log.debug(METHOD_NAME, "Exiting with sum Amount:"+p_redemptionVO.getSumAmount()+" , UserID = "+p_redemptionVO.getUserID());
				}
			}

		} else if("C2S".equalsIgnoreCase(p_redemptionVO.getModuleType()) && "GRC".equalsIgnoreCase(p_redemptionVO.getServiceCode())) {
			selectQueryBuffer.append(" SELECT SUM (dctd.sender_transfer_amount) FROM daily_c2s_trans_details dctd ");
			selectQueryBuffer.append(" WHERE dctd.service_type='GRC'");
			if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" and dctd.user_id=? AND dctd.trans_date=? AND dctd.service_type=? ");
			} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId()) || "MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())|| "EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" and dctd.user_id =? and dctd.service_type=? and dctd.trans_date >= ? and dctd.trans_date <=? ");
			}
			String selectQuery=selectQueryBuffer.toString();
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME ,"userId:"+p_redemptionVO.getUserID()+" , SQL Query :"+selectQuery);
			}
			try {
				if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					pstmtSelect =p_con.prepareStatement(selectQuery);
					pstmtSelect.setString(1,p_redemptionVO.getUserID());
					pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
					pstmtSelect.setString(3,PretupsI.SERVICE_TYPE_CHANNEL_GIFT_RECHARGE);
					rs = pstmtSelect.executeQuery();
				}  else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getWeeklyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInWeek = 7;
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInWeek; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setString(2,PretupsI.SERVICE_TYPE_CHANNEL_GIFT_RECHARGE);
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(date) );
						pstmtSelect.setDate(4,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						rs = pstmtSelect.executeQuery();
					}
				} else if("MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getMonthlyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInCreatedMonth = getNoOfDaysInCreatedLMSProfileOnMonth(p_redemptionVO.getFromDate());
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInCreatedMonth; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setString(2,PretupsI.SERVICE_TYPE_CHANNEL_GIFT_RECHARGE);
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(date));
						pstmtSelect.setDate(4,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						rs = pstmtSelect.executeQuery();
					}
				} else if("EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					dayDiff = BTSLUtil.getDifferenceInUtilDates(processedDate,p_redemptionVO.getApplicableToDate()); 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setString(2,PretupsI.SERVICE_TYPE_CHANNEL_GIFT_RECHARGE);
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getFromDate()) );
						pstmtSelect.setDate(4,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getApplicableToDate()));
						rs = pstmtSelect.executeQuery();
					}
				}
				
				if(rs!=null) {
					while (rs.next()) {
						if(_log.isDebugEnabled()) {
							_log.debug(METHOD_NAME, "Resultset is found for UserID = "+p_redemptionVO.getUserID());
						}
						p_redemptionVO.setSumAmount(rs.getLong(1));
						p_redemptionVO.setBonusCreditDateReached(true);
					}
				}  else {
					p_redemptionVO.setBonusCreditDateReached(false);
					if(_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME, "Resultset is not found for UserID = "+p_redemptionVO.getUserID());
					}
				}
			} catch (SQLException sqe) {
				_log.error(METHOD_NAME, "SQLException : " + sqe);
				_log.errorTrace(METHOD_NAME,sqe);
			} catch (Exception ex) {
				_log.error("", "Exception : " + ex);
				_log.errorTrace(METHOD_NAME,ex);
			} finally {
				try {
					if(rs != null) {
						rs.close();
					}
				} catch (Exception e) {
						_log.errorTrace(METHOD_NAME,e);
				}
				try {
					if(pstmtSelect != null) {
						pstmtSelect.close();
					}
				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME,e);
				}
				if(_log.isDebugEnabled()) {
					_log.debug(METHOD_NAME, "Exiting with sum Amount:"+p_redemptionVO.getSumAmount()+" , UserID = "+p_redemptionVO.getUserID());
				}
			}

		} else if("O2C".equalsIgnoreCase(p_redemptionVO.getModuleType())) {
			selectQueryBuffer.append(" SELECT SUM(dctm.O2C_TRANSFER_IN_AMOUNT) FROM daily_chnl_trans_main dctm ");
			selectQueryBuffer.append(" WHERE ");
			if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" dctm.user_id=? AND dctm.trans_date=? ");
			} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId()) || "MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())|| "EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" dctm.user_id=? AND dctm.trans_date >= ? and dctm.trans_date <=? ");
			}
			String selectQuery=selectQueryBuffer.toString();
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME ,"userId:"+p_redemptionVO.getUserID()+" , SQL Query :"+selectQuery);
			}
			try {
				if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId()))
				{
					pstmtSelect =p_con.prepareStatement(selectQuery);
					pstmtSelect.setString(1,p_redemptionVO.getUserID());
					pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
					rs = pstmtSelect.executeQuery();
				} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getWeeklyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInWeek = 7;
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInWeek; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(date) );
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						rs = pstmtSelect.executeQuery();
					}
				} else if("MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getMonthlyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInCreatedMonth = getNoOfDaysInCreatedLMSProfileOnMonth(p_redemptionVO.getFromDate());
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInCreatedMonth; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(date));
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						rs = pstmtSelect.executeQuery();
					}
				} else if("EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					dayDiff = BTSLUtil.getDifferenceInUtilDates(processedDate,p_redemptionVO.getApplicableToDate()); 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());						
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getFromDate()) );
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getApplicableToDate()));
						rs = pstmtSelect.executeQuery();
					}
				}

				if(rs!=null) {
					while (rs.next()) {
						if(_log.isDebugEnabled()) {
							_log.debug(METHOD_NAME, "Resultset is found for UserID = "+p_redemptionVO.getUserID());
						}
						p_redemptionVO.setSumAmount(rs.getLong(1));
						p_redemptionVO.setBonusCreditDateReached(true);
					}
				}  else {
					p_redemptionVO.setBonusCreditDateReached(false);
					if(_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME, "Resultset is not found for UserID = "+p_redemptionVO.getUserID());
					}
				}
			} catch (SQLException sqe) {
				_log.errorTrace(METHOD_NAME,sqe);
			} catch (Exception ex) {
				_log.errorTrace(METHOD_NAME,ex);				
			} finally {
				try {
					if(rs != null) {
						rs.close();
					}
				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME,e);
				}
				try {
					if(pstmtSelect != null) {
						pstmtSelect.close();
					}
				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME,e);
				}
				if(_log.isDebugEnabled()) {
					_log.debug(METHOD_NAME, "Exiting with sum Amount:"+p_redemptionVO.getSumAmount()+" , UserID = "+p_redemptionVO.getUserID());
				}
			}
		} else if("C2C".equalsIgnoreCase(p_redemptionVO.getModuleType())) {
			selectQueryBuffer.append(" SELECT SUM(C2C_TRANSFER_OUT_AMOUNT),SUM(C2C_TRANSFER_IN_AMOUNT) ");
			selectQueryBuffer.append(" FROM daily_chnl_trans_main dctm ");
			selectQueryBuffer.append(" WHERE ");
			if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" dctm.user_id=? AND dctm.trans_date=? ");
			} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId()) || "MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())|| "EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" dctm.user_id=? AND dctm.trans_date >= ? and dctm.trans_date <=? ");
			}
			String selectQuery=selectQueryBuffer.toString();
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME ,"userId:"+p_redemptionVO.getUserID()+" , SQL Query :"+selectQuery);
			}
			try {
				if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					pstmtSelect =p_con.prepareStatement(selectQuery);
					pstmtSelect.setString(1,p_redemptionVO.getUserID());
					pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
					rs = pstmtSelect.executeQuery();
				} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getWeeklyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInWeek = 7;
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInWeek; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(date) );
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						rs = pstmtSelect.executeQuery();
					}
				} else if("MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getMonthlyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInCreatedMonth = getNoOfDaysInCreatedLMSProfileOnMonth(p_redemptionVO.getFromDate());
					dayDiff = BTSLUtil.getDifferenceInUtilDates(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInCreatedMonth; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(date));
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						rs = pstmtSelect.executeQuery();
					}
				}  else if("EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					dayDiff = BTSLUtil.getDifferenceInUtilDates(processedDate,p_redemptionVO.getApplicableToDate()); 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						dayDiff = BTSLUtil.getDifferenceInUtilDates(currentProcessDate,p_redemptionVO.getApplicableToDate()); 
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getFromDate()) );
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getApplicableToDate()));
						rs = pstmtSelect.executeQuery();
					}
				}
				if(rs!=null) {
					while (rs.next()) {
						if(_log.isDebugEnabled()) {
							_log.debug(METHOD_NAME, "Resultset is found for UserID = "+p_redemptionVO.getUserID());
						}
						p_redemptionVO.setSumAmount(rs.getLong(1) + rs.getLong(2));
						p_redemptionVO.setBonusCreditDateReached(true);
					}
				} else {
					if(_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME, "Resultset is not found for UserID = "+p_redemptionVO.getUserID());
					}
					p_redemptionVO.setBonusCreditDateReached(false);
				}
			} catch (SQLException sqe) {
				_log.errorTrace(METHOD_NAME,sqe);
			} catch (Exception ex) {
				_log.errorTrace(METHOD_NAME,ex);				
			} finally {
				try {
					if(rs != null) {
						rs.close();
					}
				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME,e);
				}
				try {
					if(pstmtSelect != null) {
						pstmtSelect.close();
						}
				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME,e);
				}
				if(_log.isDebugEnabled()) {
					_log.debug(METHOD_NAME, "Exiting with sum Amount:"+p_redemptionVO.getSumAmount()+" , UserID = "+p_redemptionVO.getUserID());
				}
			}

		}

		return p_redemptionVO;

	}

	/**
	 * To calculate the  week date of weekly based on activation of LMS profile 
	 * @author diwakar
	 * @param fromDate
	 * @return Date of week
	 */
	private static Date getWeeklyBonusTargetCreditFromDate(Date fromDate,Date currentDate) {
		String METHOD_NAME = "getWeeklyBonusTargetCreditFromDate";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME," fromDate = "+fromDate+" , currentDate = "+currentDate);
		}
		Date date  = null;		
		int noOfReminderDays = 0;
		try {
			noOfReminderDays = ((BTSLUtil.getDifferenceInUtilDates(fromDate, currentDate))%7);
			if(noOfReminderDays == 0 && BTSLUtil.getDifferenceInUtilDates(fromDate, currentDate)>0) {
				noOfReminderDays = 7;
			}
			date = BTSLUtil.getDifferenceDate(currentDate,-noOfReminderDays) ;
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME," date = "+date);
			}
		} catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
		} finally {
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME," Exiting : date = "+date+", noOfReminderDays = "+noOfReminderDays);
			}
		}
		return date;
	}
	
	/**
	 * To calculate the  last date of month based on activation of LMS profile 
	 * @author diwakar
	 * @param fromDate
	 * @return Date of month
	 */
	private static Date getMonthlyBonusTargetCreditFromDate(Date fromDate,Date currentDateValue) {
		String METHOD_NAME = "getMonthlyBonusTargetCreditFromDate";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME," fromDate = "+fromDate+" , currentDateValue = "+currentDateValue);
		}
		Calendar cal= BTSLDateUtil.getInstance();
		Calendar cal1= BTSLDateUtil.getInstance();
		Calendar cal2= BTSLDateUtil.getInstance();
		Date currentDate= null;
		Date date  = cal.getTime();		
		try {
			currentDate = currentDateValue;
			cal.setTime(fromDate); //LMS Profile Creation Date
			date  = cal.getTime();
			cal2.setTime(fromDate);
			cal1.setTime(currentDate);//Current Date
			int diff=0;
			diff=cal1.compareTo(cal2);
			while(diff > 0) {
				cal2.add(Calendar.MONTH, 1); //Add One Month
				diff=cal1.compareTo(cal2);
				if(diff > 0) {
					date  = cal2.getTime();
				}

			}
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME," date = "+date);
			}
		} catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
		} finally {
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME," Exiting");
			}
		}
		return date;
	}
	
	/**
	 * To find the number of days in applicable LMS profile month
	 * @author diwakar
	 * @param fromDate
	 * @return number of days in month dynamically
	 */
	private static int getNoOfDaysInCreatedLMSProfileOnMonth(Date fromDate) {
		String METHOD_NAME = "getNoOfDaysInCreatedLMSProfileOnMonth";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME," fromDate = "+fromDate);
		}
		Calendar cal= BTSLDateUtil.getInstance();
		int days =0;	
		try {
			cal.setTime(fromDate); //LMS Profile Creation Date
			days = cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME," days = "+days);
			}
		} catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
		} finally {
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME," Exiting");
			}
		}
		return days;
	}
    	
	private static void  sendEmail(String p_filePath, String p_fileName){
		final String METHOD_NAME="sendEmail";
		if(_log.isDebugEnabled()){
			_log.debug(METHOD_NAME, "Enter: p_filePath="+p_filePath+",p_fileName="+p_fileName);
		}
		if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("LMSTARGETVSACHIEVEMENT_MAIL_SEND_REQUIRED"))) {
            String to = Constants.getProperty("LMSTARGETVSACHIEVEMENT_REPORT_MAIL_TO");
            if (BTSLUtil.isNullString(to)) {
                to = Constants.getProperty("LMSTARGETVSACHIEVEMENT_REPORT_MAIL_TO_DEFAULT");
            }
            final String from = Constants.getProperty("LMSTARGETVSACHIEVEMENT_REPORT_MAIL_FROM");
            final String subject = Constants.getProperty("LMSTARGETVSACHIEVEMENT_REPORT_MAIL_SUBJECT");
            final String bcc = Constants.getProperty("LMSTARGETVSACHIEVEMENT_REPORT_MAIL_BCC");
            final String cc = Constants.getProperty("LMSTARGETVSACHIEVEMENT_REPORT_MAIL_CC");
            final String msg = BTSLUtil.getMessage(_locale, "LMSTARGETVSACHIEVEMENT_REPORT_MAIL_MESSAGE",null);
            // Send mail
            EMailSender.sendMail(to, from, bcc, cc, subject, msg, true, p_filePath+"/"+p_fileName,p_fileName);
            if(_log.isDebugEnabled()){
    			_log.debug(METHOD_NAME, "Exit:");
    		}
       }

	}
}
