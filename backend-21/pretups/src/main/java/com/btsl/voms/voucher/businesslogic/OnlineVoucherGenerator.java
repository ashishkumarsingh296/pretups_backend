package com.btsl.voms.voucher.businesslogic;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.stream.events.Characters;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EMailSender;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.util.SqlParameterEncoder;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
/**
 * This class will be used for online voucher generation of batches
 * 
 * @author pankaj.rawat
 */
public class OnlineVoucherGenerator implements Runnable {

    private static Log _logger = LogFactory.getLog(OnlineVoucherGenerator.class.getName());
    private long seed;
    private static String ENCKEY = null;
    private static long starttime = System.currentTimeMillis();
    private static OperatorUtilI _operatorUtil = null;
    private static ArrayList<String> networksNotAllowed = null;
    private static Sigar sigarImpl=null;
    private static SigarProxy sigar = null;
    private static volatile double dbCpuUtilization = 0.0;
    private static volatile double appServerCpuUtilization = 0.0;
    private static volatile boolean continueUtilizationCheck = true;
    
    
    
	private void startUtilizationThread() {
		continueUtilizationCheck = true;
		
		Runnable dbUtilizationCheck = () -> {
			while (continueUtilizationCheck) {
				dbCpuUtilization = getDBSystemCPUUtilization();
				try {
					Thread.sleep(300);
				} catch (Exception e) {
					_logger.debug("run", "Exception during sleep operation");
				}
			}
		};
		Thread dbUtilizationCheckTh = new Thread(dbUtilizationCheck);
		dbUtilizationCheckTh.start();
		Runnable appServerUtilizationCheck = () -> {
			while (continueUtilizationCheck) {
				appServerCpuUtilization = getSystemCPUUtilization();
				try {
					Thread.sleep(300);
				} catch (Exception e) {
					_logger.debug("run", "Exception during sleep operation");
				}
			}
		};
		Thread appServerCpuUtilizationTh = new Thread(appServerUtilizationCheck);
		appServerCpuUtilizationTh.start();
	}

    @Override
	public void run() {
    	try{
			this.process();
    	}catch(BTSLBaseException e)
    	{
    		_logger.debug("run", "Exception in process method");
    	}
    }
    
	public static Double getDBSystemCPUUtilization() {
		ChannelShell channel = null;
		PipedInputStream pipeIn = null;
		PipedOutputStream pipeOut = null;
		String response = "";
		Session session = null;
		final String METHOD_NAME = "getDBSystemCPUUtilization";
		_logger.debug(METHOD_NAME, "preparing the host information for sftp.");
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(Constants.getProperty("DB_USER_MONITORING"), Constants.getProperty("DB_HOSTNAME_MONITORING"), 22);
			session.setPassword(Constants.getProperty("DB_PASSWORD_MONITORING"));
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			_logger.debug(METHOD_NAME, "Host connected.");
			String command = Constants.getProperty("DB_COMMAND_MONITORING");
			channel = (ChannelShell) session.openChannel("shell");
			pipeIn = new PipedInputStream();
			pipeOut = new PipedOutputStream(pipeIn);
			channel.setInputStream(pipeIn);
			// channel.setOutputStream(System.out, true);
			channel.connect(3 * 1000);
			pipeOut.write(command.getBytes());
			Thread.sleep(1 * 1000);
			InputStream output = channel.getInputStream();
			Reader in = new InputStreamReader(channel.getInputStream());
			int count = 5;
			StringBuilder sb = new StringBuilder();
			while (count > 0) {
				int code = in.read();
				if (code == -1) {
					count = 0;
				} else {
					count--;
					sb.append(String.valueOf(Character.toChars(code)));
				}
			}
			_logger.debug(METHOD_NAME, "DB Utilization : " + Double.parseDouble(sb.toString()));
			return Double.parseDouble(sb.toString());

		} catch (Exception ex) {
			//ex.printStackTrace();
			/// channelSftp.exit();
			System.out.println("sftp Channel exited.");
			channel.disconnect();
			session.disconnect();
		} finally {
			try {
				pipeOut.close();
			} catch (Exception e) {
				_logger.debug(METHOD_NAME, "Exception during closing PipeOutputStream Object");
			}
			
			try {
				channel.disconnect();
			} catch (Exception e) {
				_logger.debug(METHOD_NAME, "Exception during closing ChannelShell Object");
			}
			try {
				session.disconnect();
			} catch (Exception e) {
				_logger.debug(METHOD_NAME, "Exception during closing Session Object");
			}
		}
		return 0.0;
	}
			
	public static double getSystemCPUUtilization() {
		try {
			if (sigar == null) {
				sigarImpl = new Sigar();
				sigar = SigarProxyCache.newInstance(sigarImpl,
						Integer.parseInt(Constants.getProperty("SLEEP_TIME")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		CpuPerc cpuperc = null;
		try {
			cpuperc = sigar.getCpuPerc();
		} catch (SigarException se) {
			se.printStackTrace();
		}

		return cpuperc.getCombined() * 100;
	}
    
    public void process() throws BTSLBaseException {
    	
    	startUtilizationThread();
        if (_logger.isDebugEnabled()) {
            _logger.info("process ", "Entered ");
        }
        final String METHOD_NAME = "process";
        String processId = null, _processId = null;
        ProcessBL processBL = null;
        Connection con = null;
        ProcessStatusVO processStatusVO = null, _processStatusVOForSelectedNetwork = null;
        Date currentDate = null;
        int updateCount = 0; // check process details are updated or not
        boolean flagBatchSizeZero = true;
        try {
        	//executor = Executors.newFixedThreadPool(1);
            processId = PretupsI.ONLINE_VOMS_GEN;
            con = OracleUtil.getSingleConnection();
            currentDate = BTSLUtil.getTimestampFromUtilDate(new Date());
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            if (processStatusVO.isStatusOkBool()) {
            	con.commit();
                // changes started here for online generation
                // fetch the batch(if any) which needs to be generated online based on oldest modified time and online limit
                ArrayList<VomsBatchVO> onlineGenList = new ArrayList<>();
                onlineGenList = new VomsBatchesDAO().getOnlineVoucherBatchList(con, networksNotAllowed);
                if (onlineGenList.size() > 0) {
                	_processId = PretupsI.VOMS_BATCH_INITIATE;
                	flagBatchSizeZero = false;
					
				   VomsBatchVO vb = onlineGenList.get(0);
					int networkRecordCount = 0;
						String networkCode = vb.get_NetworkCode();
						//Resetting the record counts to zero for every network code if the date changes
		                if(BTSLUtil.getDifferenceInUtilDates(currentDate, processStatusVO.getExecutedUpto()) != 0)
		                {
		                	int updateCount2 = 0;
		                	updateCount2 = (new ProcessStatusDAO()).resetProcessRecordCounts(con, _processId);
		                	if(updateCount2 > 0)
		                		con.commit();
		                	
		                	networksNotAllowed = null;
		                }
		                
						int networkLimit = Integer.parseInt(PreferenceCache.getNetworkPrefrencesValue(PreferenceI.ONLINE_VOUCHER_GEN_LIMIT_NW,networkCode).toString());
						if(BTSLUtil.isNullString(PreferenceCache.getNetworkPrefrencesValue(PreferenceI.ONLINE_VOUCHER_GEN_LIMIT_NW,networkCode).toString()))
							networkLimit =  ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ONLINE_VOUCHER_GEN_LIMIT_NW))).intValue();						

						_processStatusVOForSelectedNetwork = new ProcessStatusDAO().loadProcessDetailNetworkWise(con,_processId,networkCode);
						networkRecordCount = _processStatusVOForSelectedNetwork.getRecordCount();
						
						if(1 + networkRecordCount <= networkLimit)
						{
							boolean isDataProcessed = generateVouchersOnline(con, onlineGenList);
							if (isDataProcessed) {
								EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "OnlineVoucherGenerator[process]", "", "", "", " OnlineVoucherGenerator process has been executed successfully.");
								if (_logger.isDebugEnabled()) {
									_logger.debug("process", " successfully");
								}
								//update process status table here
				                int updateCount1 = new ProcessStatusDAO().updateProcessDetailNetworkWiseOnlineVMSGen(con, _processStatusVOForSelectedNetwork);
				                if(updateCount1 > 0)
				                	con.commit();
							}
						}
						
						else
						{
							if(networksNotAllowed != null)
								networksNotAllowed.add(networkCode);
							else
							{
								networksNotAllowed = new ArrayList<>();
								networksNotAllowed.add(networkCode);
							}
						}
                }else{
                	flagBatchSizeZero = true;
                }
            } else {
                throw new BTSLBaseException("OnlineVoucherGenerator", "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }
        }
        catch(SQLException sqle)
        {
        	_logger.error("process", "BTSLBaseException : " + sqle.getMessage());
            flagBatchSizeZero = true;
        }
        catch (BTSLBaseException be) {
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            flagBatchSizeZero = true;
            throw be;
        } catch (Exception e) {
            _logger.error("process", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "OnlineVoucherGenerator[process]", "", "", "", " OnlineVoucherGenerator process could not be executed successfully.");
            flagBatchSizeZero = true;
            throw new BTSLBaseException("OnlineVoucherGenerator", "process", PretupsErrorCodesI.ERROR_VOMS_GEN,e);
        } finally {
            try {
                if (processStatusVO.isStatusOkBool()) {
                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    processStatusVO.setExecutedOn(currentDate);
                    processStatusVO.setExecutedUpto(currentDate);
                    processStatusVO.setStartDate(currentDate);
                    updateCount = (new ProcessStatusDAO()).updateProcessDetail(con, processStatusVO);
                    if (updateCount > 0) {
                        con.commit();
                    }
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", "Exception in closing connection ");
                }
                flagBatchSizeZero = true;
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e1) {
                    _logger.errorTrace(METHOD_NAME, e1);
                }
            }
            //condition based if flagIfBatchSizeIs0
            //in a seperate thread, recall the same process again here 
            if(!flagBatchSizeZero)
            {
            	new OnlineVoucherGenerator().process();
            }
            
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "Exiting..... ");
            }
            
            continueUtilizationCheck = false;
        }
    }

    /**
     * @param p_con
     * @param p_startDate
     * @param p_endDate
     * @return
     * @author rahul.dutt
     *         This method is used to generate voucher pins and serial numbers
     *         based on Accepted batched in DB
     *         and creates vouchers with status GE
     */
    
    public static void sendEmailNotification(Connection p_con, VomsBatchVO batchVO, String p_subject) {
		final String METHOD_NAME = "sendEmailNotification";
        
		final Locale locale = BTSLUtil.getSystemLocaleForEmail();
        
		if (_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Entered ");
		}

		try {
			final String from = BTSLUtil.getMessage(locale,"o2c.email.notification.from");
			//final String from = "System";
			String cc = PretupsI.EMPTY;
			String message1 = null;
			final String bcc = "";
			String subject = "";
			String to = "";
            ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
            StringBuilder sb=new StringBuilder("");  
            sb.append("<br>");
            sb.append("<table><tr>"); 
            sb.append(" <td style='width: 12%;'>");
            sb.append(BTSLUtil.getMessage(locale, "o2c.email.notification.voucherType"));
            sb.append("</td>");
            sb.append(" <td style='width: 12%;'>");
            sb.append(BTSLUtil.getMessage(locale, "voucher.generation.email.notification.voucherSegment"));
            sb.append("</td>");
            sb.append(" <td style='width: 5%;'>");
            sb.append(BTSLUtil.getMessage(locale, "o2c.email.notification.denomination"));
            sb.append("</td>");
            sb.append(" <td style='width: 12%;'>");
            sb.append(BTSLUtil.getMessage(locale, "voucher.generation.email.notification.voucherProfile"));
            sb.append("</td>");
            sb.append(" <td style='width: 12%;'>");
            sb.append(BTSLUtil.getMessage(locale, "o2c.email.notification.batchId"));
            sb.append("</td>");
            sb.append(" <td style='width: 25%;'>");
            sb.append(BTSLUtil.getMessage(locale, "o2c.email.notification.fromSerialNo"));
            sb.append("</td>");
            sb.append(" <td style='width: 25%;'>");
            sb.append(BTSLUtil.getMessage(locale, "o2c.email.notification.toSerialNo"));
            sb.append("</td>");
            sb.append(" <td style='width: 10%;'>");
            sb.append(BTSLUtil.getMessage(locale, "o2c.email.notification.quantity"));
            sb.append("</td>");
            sb.append(" <td style='width: 12%;'>");
            sb.append(BTSLUtil.getMessage(locale, "voucher.generation.email.notification.batch.status"));
            sb.append("</td>");
            sb.append("</tr>");
            message1=sb.toString();
            StringBuilder sb1=new StringBuilder(""); 
            sb1.append("<tr>"); 
            sb1.append("<td style='width: 10%;'>");
            sb1.append(batchVO.getVoucherType());
            sb1.append("</td>");
            sb1.append("<td style='width: 10%;'>");
            sb1.append(BTSLUtil.getSegmentDesc(batchVO.getSegment()));
            sb1.append("</td>");
            sb1.append("<td style='width: 10%;'>");
            sb1.append(batchVO.getDenomination()); 
            sb1.append("</td>");  
            sb1.append("<td style='width: 10%;'>");
            sb1.append(batchVO.getProductName());
            sb1.append("</td>");
            sb1.append("<td style='width: 25%;'>");
            sb1.append(batchVO.getBatchNo());
            sb1.append("</td>");  
            sb1.append("<td style='width: 25%;'>");
            sb1.append(batchVO.getFromSerialNo());
            sb1.append("</td>");
            sb1.append("<td style='width: 25%;'>");
            sb1.append(batchVO.getToSerialNo());
            sb1.append("</td>");
            sb1.append("<td style='width: 25%;'>");
            sb1.append(batchVO.getQuantity());
            sb1.append("</td>");
            sb1.append("<td style='width: 10%;'>");
            sb1.append(((batchVO.getStatus()).equals("AC"))?"Success":"Failure");
            sb1.append("</td>");
            sb1.append("</tr>");
            message1 = message1 + sb1.toString();
            message1 = message1 + "</table>";
            subject = BTSLUtil.getMessage(locale,p_subject);
            
            //For getting name, msisdn, email of initiator
            ArrayList arrayList = new ArrayList();
            arrayList = channelUserWebDAO.loadUserNameAndEmail(p_con, batchVO.getCreatedBy());
            to = (String)(arrayList.get(2));
            if(batchVO.getFirstApprovedBy() != null)
            	to += "," + (channelUserWebDAO.loadUserNameAndEmail(p_con, batchVO.getFirstApprovedBy())).get(2);
            if(batchVO.getSecondApprovedBy() != null)
            	to += "," + (channelUserWebDAO.loadUserNameAndEmail(p_con, batchVO.getSecondApprovedBy())).get(2);
            if(batchVO.getThirdApprovedBy() != null)
            	to += "," + (channelUserWebDAO.loadUserNameAndEmail(p_con, batchVO.getThirdApprovedBy())).get(2);
            
			if (_logger.isDebugEnabled()) {
				_logger.debug("MAIL CONTENT",message1);
			}
			boolean isAttachment = false;
			String pathofFile = "";
			String fileNameTobeDisplayed = "";
			// Send email
			EMailSender.sendMail(to, from, bcc, cc, subject, message1, isAttachment, pathofFile, fileNameTobeDisplayed);
		} catch (Exception e) {
			if (_logger.isDebugEnabled()) {
				_logger.error(METHOD_NAME, " Email sending failed" + e.getMessage());
			}
			_logger.errorTrace(METHOD_NAME, e);
		}
		if (_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Exiting ....");
		}
	}
    
    /**
     * @param p_con
     * @param batchList
     * @return
     * @author pankaj.rawat
     *         This method is used to generate voucher pins and serial numbers
     *         based on Accepted batched in DB
     *         and creates vouchers with status GE
     */
    public static boolean generateVouchersOnline(Connection p_con, ArrayList batchList) throws SQLException, BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.info("generateVouchersOnline", "Entered");
        }
        final String METHOD_NAME = "generateVouchersOnline";
        boolean isDataProcessed = false;
        PreparedStatement pstmtSel = null, psmtInsert = null, psmtupdate = null;
        ;
        PreparedStatement psmtupdate1 = null;
        ResultSet rst = null, rst1 = null;
        ArrayList pinsList = null;
        VomsBatchesDAO vomsVatchesDAO = null;
        VomsBatchVO batchVO = null;
        String product_id = null, activeProductId = "", serialnumbrStr, location = null, category = null, voucher_type = null, table_name = null, segment = null;;
        String sqlSelectUpdate = null, sqlInsert = null;
        long serialNOcounter = 0, startserialnumber, fromserialnum = 0;
        VomsUtil vomsutil = null;
        Date currentdate = new Date();
        Date expiryDate = null;
        int[] updateCount = null;
        VomsVoucherDAO vomsVoucherDao = null;	
        int update = 0;
        OnlineVoucherGenerator OnlineVoucherGenerator = null;
        long successCount = 0;
        long failCount = 0;
        int retryCount = 0;
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {        	
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
        	_logger.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OnlineVoucherGenerator[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
        try {
            vomsVatchesDAO = new VomsBatchesDAO();
            StringBuffer sqlselect = new StringBuffer();
            sqlselect.append("SELECT DISTINCT  VC.serial_number_counter,VC.category_id, VC.voucher_type ");
            sqlselect.append(" FROM voms_categories VC,voms_products VP WHERE VC.CATEGORY_ID=VP.CATEGORY_ID and VP.PRODUCT_ID=?");
            sqlSelectUpdate = sqlselect.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "sqlSelect" + sqlSelectUpdate);
            }
            pstmtSel = p_con.prepareStatement(sqlSelectUpdate);
            vomsutil = new VomsUtil();
            OnlineVoucherGenerator = new OnlineVoucherGenerator();
            long mrp = 0;
            for (int i = 0; i < batchList.size(); i++) {
            	retryCount = 0;
                batchVO = (VomsBatchVO) batchList.get(i);
                product_id = batchVO.getProductID();
                location = batchVO.getLocationCode();
                segment = batchVO.getSegment();
                mrp = PretupsBL.getSystemAmount(batchVO.getMrp());
                if(batchVO.getExpiryPeriod() != 0)
                	expiryDate = BTSLUtil.addDaysInUtilDate(currentdate, batchVO.getExpiryPeriod());
                else
                {
                	expiryDate = batchVO.getExpiryDate();
                	if(expiryDate.before(currentdate)||expiryDate.equals(currentdate))
                	{
                		throw new BTSLBaseException(PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
                	}
                }
                _logger.info(METHOD_NAME, "BATCH proceesing started:" + batchVO.getBatchNo() + "product_id" + product_id);
                pstmtSel.setString(1, product_id);
                rst = pstmtSel.executeQuery();
                if (rst.next()) {
                    serialNOcounter = rst.getLong("serial_number_counter");
                    activeProductId = SqlParameterEncoder.encodeParams(rst.getString("category_id"));
                    category = SqlParameterEncoder.encodeParams(rst.getString("category_id"));
                    voucher_type = SqlParameterEncoder.encodeParams(rst.getString("voucher_type"));
                } else {
                    throw new BTSLBaseException(PretupsErrorCodesI.ERROR_VOMS_NOTFOUND_COUNTER);
                }
                if (_logger.isDebugEnabled()) {
                	_logger.debug(METHOD_NAME, "networkPrefix=" + BTSLUtil.getPrefixCodeUsingNwCode("NG"));
                    _logger.debug(METHOD_NAME, "nwCodePrefixMappingStr=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.NW_CODE_NW_PREFIX_MAPPING)));
                }
                serialnumbrStr = _operatorUtil.formatVomsSerialnum(serialNOcounter, activeProductId, segment, location);
                startserialnumber = Long.parseLong(serialnumbrStr);
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "startserialnumber" + startserialnumber);
                }
                fromserialnum = startserialnumber + 1;
                pinsList = _operatorUtil.generatePin(location, activeProductId, batchVO.getNoOfVoucher(),batchVO.getSeq_id());
                if (_logger.isDebugEnabled()) {
                	_logger.debug(METHOD_NAME, "pinListSize=" + pinsList.size());
                }
                boolean flag = true;
                l1: while (flag) {
                    try {
                        boolean matchFound = BTSLUtil.validateTableName(voucher_type);
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                            table_name = "VOMS_" + voucher_type + "_VOUCHERS";
                        } else {
                            table_name = "voms_vouchers";
                        }

                        sqlselect = new StringBuffer("INSERT INTO " + table_name);
                        sqlselect.append(" (serial_no,product_id,pin_no,generation_batch_no,attempt_used,current_status,");
                        sqlselect.append("expiry_date,consume_before,mrp,talktime,validity,production_network_code,");
                        sqlselect.append("user_network_code,modified_by,last_batch_no,modified_on,");
                        sqlselect.append("created_on,previous_status,");
                        sqlselect.append("status,seq_no,created_date,VOUCHER_TYPE, VOUCHER_SEGMENT  ");
                         if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue() ){
                        	 sqlselect.append(",sequence_id  ");
                         }
                         sqlselect.append(" ) ");
                        sqlselect.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ");
                        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue() ){
                       	 sqlselect.append(" ,?  ");
                        }
                        sqlselect.append("  ) ");
                        sqlInsert = sqlselect.toString();
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(METHOD_NAME, "sqlInsert" + sqlInsert);
                        }
                        psmtInsert = p_con.prepareStatement(sqlInsert);
                        if (failCount == 0) {
                            successCount = 0;
                        } else {
                            failCount = 0;
                        }
                        for (int j = 0; j < pinsList.size(); j++) {

                            ++serialNOcounter;
                            int k = 0;
                            psmtInsert.setLong(++k, ++startserialnumber);
                            psmtInsert.setString(++k, product_id);
                            psmtInsert.setString(++k, (String) pinsList.get(j));
                            psmtInsert.setString(++k, (String) batchVO.getBatchNo());
                            psmtInsert.setInt(++k, 0);
                            psmtInsert.setString(++k, VOMSI.VOUCHER_NEW);
                            psmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(expiryDate));
                            psmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(expiryDate));
                            psmtInsert.setLong(++k, mrp);
                            psmtInsert.setLong(++k, batchVO.getTalktime());
                            psmtInsert.setLong(++k, batchVO.getValidity());
                            psmtInsert.setString(++k, location);
                            psmtInsert.setString(++k, location);
                            psmtInsert.setString(++k, PretupsI.SYSTEM);
                            psmtInsert.setString(++k, (String) batchVO.getBatchNo());
                            psmtInsert.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(currentdate));
                            psmtInsert.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(currentdate));
                            psmtInsert.setString(++k, VOMSI.VOUCHER_NEW);
                            psmtInsert.setString(++k, VOMSI.VOUCHER_NEW);
                            psmtInsert.setLong(++k, j);
                            psmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(currentdate));
                            psmtInsert.setString(++k, voucher_type);
                            psmtInsert.setString(++k, segment);
                            
                            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
                                psmtInsert.setInt(++k,batchVO.getSeq_id());
                            }else if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()&&((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue()){
                            	psmtInsert.setInt(++k,BTSLUtil.getUniqueInteger((String) pinsList.get(j), 100, 100+((Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ID_RANGE)).intValue()));
                            }

                            psmtInsert.addBatch();
                            try {
                                if ((j + 1) % 200 == 0) {
                                    double curCPU = appServerCpuUtilization;
                                    double curDBCPU = dbCpuUtilization;
                                    _logger.debug(METHOD_NAME, " CPU Utilization : " + curCPU);
                                    _logger.debug(METHOD_NAME, " DB Utilization : " + curDBCPU);
            						if (curCPU < Integer.parseInt(Constants.getProperty("RESOURCE_UTIL_ONLINE_VOMS_GEN_THRESHOLD_LIMIT"))  &&  curDBCPU <  Integer.parseInt(Constants.getProperty("RESOURCE_UTIL_ONLINE_VOMS_GEN_THRESHOLD_LIMIT"))) {
            							// Nothing
            						} else {
            							Thread.sleep(Integer.parseInt(Constants.getProperty("PAUSE_APPLICATION_TIME")));
            						}
            						
                                    updateCount = psmtInsert.executeBatch();
                                    successCount = successCount + updateCount.length;
                                    VomsBatchInfoLog.log(currentdate + "Batch No" + batchVO.getBatchNo() + "Batch insert length:" + updateCount.length);
                                }
                            } catch (BatchUpdateException be) {
                                startserialnumber = startserialnumber - (200 - (be.getUpdateCounts().length));
                                serialNOcounter = serialNOcounter - (200 - (be.getUpdateCounts().length));
                                failCount = failCount + (200 - (be.getUpdateCounts().length));
                                successCount = successCount + (be.getUpdateCounts().length);
                                VomsBatchInfoLog.log(currentdate + "Duplicate voucher PIN generated failCount:" + failCount + ",serialNOcounter:" + serialNOcounter + ",startserialnumber:" + startserialnumber);
                                _logger.errorTrace(METHOD_NAME, be);
                            }
                        }
                        try {
                            updateCount = psmtInsert.executeBatch();
                            successCount = successCount + updateCount.length;
                        } catch (BatchUpdateException be) {
                            startserialnumber = startserialnumber - (batchList.size() % 200 - (be.getUpdateCounts().length));
                            serialNOcounter = serialNOcounter - (batchList.size() % 200 - (be.getUpdateCounts().length));
                            failCount = failCount + (batchList.size() % 200 - (be.getUpdateCounts().length ));
                            successCount = successCount + (be.getUpdateCounts().length);
                            VomsBatchInfoLog.log(currentdate + "Duplicate voucher PIN generated failCount:" + failCount + ",serialNOcounter:" + serialNOcounter + ",startserialnumber:" + startserialnumber);
                            _logger.errorTrace(METHOD_NAME, be);
                        }
                        VomsBatchInfoLog.log(currentdate + "Batch Processing commited for batch:" + batchVO.getBatchNo() + "and successCount: " + successCount);

                    } catch (SQLException e) {
                        p_con.rollback();
                        VomsBatchInfoLog.log(currentdate + "Batch Processing terminated for batch:" + batchVO.getBatchNo());
                        _logger.errorTrace(METHOD_NAME, e);
                        throw e;

                    } catch (IllegalStateException e1) {
                        VomsBatchInfoLog.log(currentdate + "Batch Processing terminated for batch:" + batchVO.getBatchNo());
                        _logger.errorTrace(METHOD_NAME, e1);
                        throw e1;
                    }
                    if (failCount > 0) {
                        VomsBatchInfoLog.log(currentdate + "Batch Processing started for fail count:" + failCount);
                        retryCount++;
                        pinsList = _operatorUtil.generatePin(location, activeProductId, failCount,batchVO.getSeq_id());
                        if (pinsList.size() > 0 && retryCount < 5) {
                            continue l1;
                        } else {
                            flag = false;
                        }
                    } else {
                        flag = false;
                    }
                }
         
                // update serial number counter
                sqlselect = new StringBuffer("UPDATE voms_categories SET serial_number_counter=? where category_id=?");
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Update Query" + sqlselect.toString());
                }
                psmtupdate1 = p_con.prepareStatement(sqlselect.toString());
                psmtupdate1.setLong(1, serialNOcounter);
                psmtupdate1.setString(2, category);
                update = psmtupdate1.executeUpdate();
                if (update <= 0) {
                    p_con.rollback();
                } else {
                    vomsVoucherDao = new VomsVoucherDAO();
                    sqlselect = new StringBuffer("UPDATE voms_batches set from_serial_no=?,to_serial_no=?,total_no_of_success=?,");
                    sqlselect.append("modified_date=?,status=?,modified_on=?,message=?,batch_type=?, TOTAL_NO_OF_FAILURE=? ");
                    sqlselect.append(" where batch_no=?");
                    psmtupdate = p_con.prepareStatement(sqlselect.toString());
                    psmtupdate.setLong(1, fromserialnum);
                    psmtupdate.setLong(2, startserialnumber);
                    psmtupdate.setLong(3, successCount);
                    psmtupdate.setDate(4, BTSLUtil.getSQLDateFromUtilDate(currentdate));
                    psmtupdate.setString(5, VOMSI.EXECUTED);
                    psmtupdate.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(currentdate));
                    psmtupdate.setString(7, "Generated Successfully");
                    psmtupdate.setString(8, VOMSI.BATCH_GENERATED);
                    psmtupdate.setLong(9, failCount);
                    psmtupdate.setString(10, batchVO.getBatchNo());
                    update = psmtupdate.executeUpdate();
                    vomsVoucherDao.updateSummaryTable(p_con, batchVO, false);
                }
                // update the batch status to GE and from serial number and to
                // serial number fields
                
                
                p_con.commit();
                // }
                _logger.info(METHOD_NAME, "BATCH proceesed successfully:" + batchVO.getBatchNo());
                serialnumbrStr = null;
                VomsBatchInfoLog.genVoucherBatchLog(batchVO);
				batchVO.setFromSerialNo(Long.toString(fromserialnum));
				batchVO.setToSerialNo(Long.toString(fromserialnum + successCount - 1));
				batchVO.setVoucherType(voucher_type);
				batchVO.setQuantity(Long.toString(successCount));
				batchVO.setDenomination(PretupsBL.getDisplayAmount(Long.parseLong(vomsVatchesDAO.getDenomination(p_con, batchVO.getProductID()))));
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_GEN_EMAIL_NOTIFICATION))).booleanValue())
					sendEmailNotification(p_con, batchVO, "voucher.generation.notification.subject");
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_GEN_SMS_NOTIFICATION))).booleanValue())
				{
					sendSMSNotification(p_con, batchVO, "voucher.generation.notification.subject");
				}
            }// end of for loop for batchlist
            isDataProcessed = true;
        } catch (SQLException sqe) {
            p_con.rollback();
            _logger.errorTrace(METHOD_NAME, sqe);
            _logger.error(METHOD_NAME, "SQLException" + sqe);
            throw sqe;
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException" + be);
            throw be;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception e" + e);
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info(METHOD_NAME, "Exciting ");
            }
            try {
                if (pstmtSel != null) {
                    pstmtSel.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtupdate != null) {
                	psmtupdate.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtupdate1 != null) {
                	psmtupdate1.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
        }
        return isDataProcessed;
    }
   
    /**
     * @param p_con
     * @param batchVO
     * @param p_subject
     * @return
     * @author pankaj.rawat
     *         This method is used to send SMS Notification to admin mobiles on generation of vouchers and rejection of vouchers. 
     */
    public static void sendSMSNotification(Connection p_con, VomsBatchVO batchVO, String p_subject) throws Exception
    {
    	final String METHOD_NAME = "sendSMSNotification";
		if (_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Entered ");
		}
		
		try {
            final String msisdnString = new String(Constants.getProperty("adminmobile"));
            final String[] smsReceivers = msisdnString.split(",");
			if (_logger.isDebugEnabled()) {
				_logger.debug("SMS RECEIVERS:",smsReceivers);
			}

			final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
			String messageKey = null;
			String[] array = new String[9];
			if(p_subject.equals("voucher.generation.notification.subject"))
			{
				messageKey = PretupsErrorCodesI.VOUCHER_GEN_NOTIFICATION;
				array[0]=batchVO.getVoucherType();
				array[1]=BTSLUtil.getSegmentDesc(batchVO.getSegment());
				array[2]=batchVO.getDenomination();
				array[3]=batchVO.getProductName();
				array[4]=batchVO.getBatchNo();
				array[5]=batchVO.getFromSerialNo();
				array[6]=batchVO.getToSerialNo();
				array[7]=batchVO.getQuantity();
				array[8]=(("AC").equals(batchVO.getStatus()))?"Success":"Failure";
			}
			else
			{
				messageKey = PretupsErrorCodesI.VOUCHER_REJECT_NOTIFICATION;
				String finalApprLvl = null;
				if(batchVO.getFirstApprovedBy() == null)
					finalApprLvl = "1";
    			else if(batchVO.getSecondApprovedBy() == null)
    				finalApprLvl = "2";
    			else
    				finalApprLvl = "3";
				array[0]=batchVO.getBatchNo();
				array[1]=finalApprLvl;
			}
			final BTSLMessages messages = new BTSLMessages(messageKey, array);
			for(String msisdn : smsReceivers){
				final PushMessage pushMessage = new PushMessage(msisdn, messages, batchVO.getBatchNo(), null, locale, batchVO.get_NetworkCode());
				pushMessage.push();
			}
		}
		catch(Exception e)
		{
			if (_logger.isDebugEnabled()) {
				_logger.error(METHOD_NAME, " Sms sending failed" + e.getMessage());
			}
			_logger.errorTrace(METHOD_NAME, e);
		}
		if (_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Exiting ....");
		}
    }
	
	public void processERP(Connection con,ArrayList<VomsBatchVO> batchList) throws BTSLBaseException {
    	final String METHOD_NAME = "processERP";
        if (_logger.isDebugEnabled()) {
            _logger.info(METHOD_NAME, "Entered ");
        }
        String processId = null, _processId = null;
        ProcessBL processBL = null;
        ProcessStatusVO processStatusVO = null, _processStatusVOForSelectedNetwork = null;
        Date currentDate = null;
        int updateCount = 0; // check process details are updated or not
        boolean flagBatchSizeZero = true;
        try {
        	//executor = Executors.newFixedThreadPool(1);
            processId = PretupsI.ONLINE_VOMS_GEN;
            currentDate = BTSLUtil.getTimestampFromUtilDate(new Date());
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            if (processStatusVO.isStatusOkBool()) {
//            	con.commit();
                // changes started here for online generation
                // fetch the batch(if any) which needs to be generated online based on oldest modified time and online limit
                ArrayList<VomsBatchVO> onlineGenList = new ArrayList<>();
                onlineGenList = new VomsBatchesDAO().getOnlineVoucherBatchListWithBatchNos(con, networksNotAllowed,batchList);
                if (onlineGenList.size() > 0) {
                	_processId = PretupsI.VOMS_BATCH_INITIATE;
                	flagBatchSizeZero = false;
					
				   VomsBatchVO vb = onlineGenList.get(0);
					int networkRecordCount = 0;
						String networkCode = vb.get_NetworkCode();
						//Resetting the record counts to zero for every network code if the date changes
		                if(BTSLUtil.getDifferenceInUtilDates(currentDate, processStatusVO.getExecutedUpto()) != 0)
		                {
		                	int updateCount2 = 0;
		                	updateCount2 = (new ProcessStatusDAO()).resetProcessRecordCounts(con, _processId);
		                	/*if(updateCount2 > 0)
		                		con.commit();*/
		                	
		                	networksNotAllowed = null;
		                }
		                
						int networkLimit = Integer.parseInt(PreferenceCache.getNetworkPrefrencesValue(PreferenceI.ONLINE_VOUCHER_GEN_LIMIT_NW,networkCode).toString());							
						if(BTSLUtil.isNullString(PreferenceCache.getNetworkPrefrencesValue(PreferenceI.ONLINE_VOUCHER_GEN_LIMIT_NW,networkCode).toString()))
							networkLimit =  SystemPreferences.ONLINE_VOUCHER_GEN_LIMIT_NW;						

						_processStatusVOForSelectedNetwork = new ProcessStatusDAO().loadProcessDetailNetworkWise(con,_processId,networkCode);
						networkRecordCount = _processStatusVOForSelectedNetwork.getRecordCount();
						if (_logger.isDebugEnabled()) 
							_logger.debug(METHOD_NAME, " networkRecordCount:" + networkRecordCount + " networkLimit:" +networkLimit);
							
						if(1 + networkRecordCount <= networkLimit)
						{
							boolean isDataProcessed = generateVouchersOnline(con, onlineGenList,VOMSI.ERP_SYSTEM);
							if (isDataProcessed) {
								EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "OnlineVoucherGenerator[process]", "", "", "", " OnlineVoucherGenerator process has been executed successfully.");
								if (_logger.isDebugEnabled()) {
									_logger.debug(METHOD_NAME, " successfully");
								}
								//update process status table here
				                int updateCount1 = new ProcessStatusDAO().updateProcessDetailNetworkWiseOnlineVMSGen(con, _processStatusVOForSelectedNetwork);
				                /*if(updateCount1 > 0)
				                	con.commit();*/
							}
						}
						
						else
						{
							if(networksNotAllowed != null)
								networksNotAllowed.add(networkCode);
							else
							{
								networksNotAllowed = new ArrayList<>();
								networksNotAllowed.add(networkCode);
							}
						}
                }else{
                	flagBatchSizeZero = true;
                }
            } else {
                throw new BTSLBaseException("OnlineVoucherGenerator", METHOD_NAME, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }
        }
        catch(SQLException sqle)
        {
        	_logger.error(METHOD_NAME, "BTSLBaseException : " + sqle.getMessage());
            flagBatchSizeZero = true;
        }
        catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            flagBatchSizeZero = true;
            throw be;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "OnlineVoucherGenerator[process]", "", "", "", " OnlineVoucherGenerator process could not be executed successfully.");
            flagBatchSizeZero = true;
            throw new BTSLBaseException("OnlineVoucherGenerator", METHOD_NAME, PretupsErrorCodesI.ERROR_VOMS_GEN,e);
        } finally {
            try {
                if (processStatusVO.isStatusOkBool()) {
                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    processStatusVO.setExecutedOn(currentDate);
                    processStatusVO.setExecutedUpto(currentDate);
                    processStatusVO.setStartDate(currentDate);
                    updateCount = (new ProcessStatusDAO()).updateProcessDetail(con, processStatusVO);
                 /*   if (updateCount > 0) {
                        con.commit();
                    }*/
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", "Exception in closing connection ");
                }
                flagBatchSizeZero = true;
            }
           
            //condition based if flagIfBatchSizeIs0
            //in a seperate thread, recall the same process again here 
//            if(!flagBatchSizeZero)
//            {
//            	new OnlineVoucherGenerator().processERP(con);
//            }
            
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
            
            continueUtilizationCheck = false;
        }
    }
	
	/**
     * @param p_con
     * @param batchList
     * @return
     * @author pankaj.rawat
     *         This method is used to generate voucher pins and serial numbers
     *         based on Accepted batched in DB
     *         and creates vouchers with status GE
     */
    public static boolean generateVouchersOnline(Connection p_con, ArrayList batchList, String source) throws SQLException, BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.info("generateVouchersOnline", "Entered source:" + source);
        }
        final String METHOD_NAME = "generateVouchersOnline";
        boolean isDataProcessed = false;
        PreparedStatement pstmtSel = null, psmtInsert = null, psmtupdate = null;
        ;
        PreparedStatement psmtupdate1 = null;
        ResultSet rst = null, rst1 = null;
        ArrayList pinsList = null;
        VomsBatchesDAO vomsVatchesDAO = null;
        VomsBatchVO batchVO = null;
        String product_id = null, activeProductId = "", serialnumbrStr, location = null, category = null, voucher_type = null, table_name = null, segment = null;;
		String description = null, secPrefixId = null, productName = null, shortName = null, itemCode = null;
        int validity;
        String sqlSelectUpdate = null, sqlInsert = null;
        long serialNOcounter = 0, startserialnumber, fromserialnum = 0;
        VomsUtil vomsutil = null;
        Date currentdate = new Date();
        Date expiryDate = null;
        int[] updateCount = null;
        VomsVoucherDAO vomsVoucherDao = null;	
        int update = 0;
        OnlineVoucherGenerator OnlineVoucherGenerator = null;
        long successCount = 0;
        long failCount = 0;
        int retryCount = 0;
        boolean isERPProcess = (VOMSI.ERP_SYSTEM.equals(source)) ? true : false;
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {        	
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
        	_logger.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OnlineVoucherGenerator[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
        try {
            vomsVatchesDAO = new VomsBatchesDAO();
            StringBuffer sqlselect = new StringBuffer();
     	    sqlselect.append("SELECT DISTINCT  VC.serial_number_counter,VC.category_id, VC.voucher_type, VP.DESCRIPTION, VP.SECONDARY_PREFIX_CODE, VP.PRODUCT_NAME, VP.SHORT_NAME, VP.VALIDITY, VP.ITEM_CODE ");
            sqlselect.append(" FROM voms_categories VC,voms_products VP WHERE VC.CATEGORY_ID=VP.CATEGORY_ID and VP.PRODUCT_ID=?");
            sqlSelectUpdate = sqlselect.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "sqlSelect" + sqlSelectUpdate);
            }
            pstmtSel = p_con.prepareStatement(sqlSelectUpdate);
            vomsutil = new VomsUtil();
            OnlineVoucherGenerator = new OnlineVoucherGenerator();
            long mrp = 0;
            for (int i = 0; i < batchList.size(); i++) {
            	retryCount = 0;
                batchVO = (VomsBatchVO) batchList.get(i);
                product_id = batchVO.getProductID();
                location = batchVO.getLocationCode();
                segment = batchVO.getSegment();
                mrp = PretupsBL.getSystemAmount(batchVO.getMrp());
                if(batchVO.getExpiryPeriod() != 0)
                	expiryDate = BTSLUtil.addDaysInUtilDate(currentdate, batchVO.getExpiryPeriod());
                else
                {
                	expiryDate = batchVO.getExpiryDate();
                	if(expiryDate.before(currentdate)||expiryDate.equals(currentdate))
                	{
                		throw new BTSLBaseException(PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
                	}
                }
                _logger.info(METHOD_NAME, "BATCH proceesing started:" + batchVO.getBatchNo() + "product_id" + product_id);
                pstmtSel.setString(1, product_id);
                rst = pstmtSel.executeQuery();
                if (rst.next()) {
                    serialNOcounter = rst.getLong("serial_number_counter");
                    activeProductId = SqlParameterEncoder.encodeParams(rst.getString("category_id"));
                    category = SqlParameterEncoder.encodeParams(rst.getString("category_id"));
                    voucher_type = SqlParameterEncoder.encodeParams(rst.getString("voucher_type"));
		    description = rst.getString("DESCRIPTION");
                    secPrefixId = rst.getString("SECONDARY_PREFIX_CODE");
                    productName = rst.getString("PRODUCT_NAME");
                    shortName = rst.getString("SHORT_NAME");
                    validity = rst.getInt("VALIDITY");
                    itemCode = rst.getString("ITEM_CODE");
                } else {
                    throw new BTSLBaseException(PretupsErrorCodesI.ERROR_VOMS_NOTFOUND_COUNTER);
                }
                String info5 = shortName+"#"+productName+"#"+secPrefixId+"#"+description+"#"+String.valueOf(validity)+"#"+itemCode;
                if (_logger.isDebugEnabled()) {
                	_logger.debug(METHOD_NAME, "networkPrefix=" + BTSLUtil.getPrefixCodeUsingNwCode("NG"));
                    _logger.debug(METHOD_NAME, "nwCodePrefixMappingStr=" + SystemPreferences.NW_CODE_NW_PREFIX_MAPPING);
                }
                serialnumbrStr = _operatorUtil.formatVomsSerialnum(serialNOcounter, activeProductId, segment, location);
                startserialnumber = Long.parseLong(serialnumbrStr);
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "startserialnumber" + startserialnumber);
                }
                fromserialnum = startserialnumber + 1;
                pinsList = _operatorUtil.generatePin(location, activeProductId, batchVO.getNoOfVoucher(),batchVO.getSeq_id());
                if (_logger.isDebugEnabled()) {
                	_logger.debug(METHOD_NAME, "pinListSize=" + pinsList.size());
                }
                boolean flag = true;
                l1: while (flag) {
                    try {
                        boolean matchFound = BTSLUtil.validateTableName(voucher_type);
                        if ((boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE)) {
                            table_name = "VOMS_" + voucher_type + "_VOUCHERS";
                        } else {
                            table_name = "voms_vouchers";
                        }

                        sqlselect = new StringBuffer("INSERT INTO " + table_name);
                        sqlselect.append(" (serial_no,product_id,pin_no,generation_batch_no,attempt_used,current_status,");
                        sqlselect.append("expiry_date,consume_before,mrp,talktime,validity,production_network_code,");
                        sqlselect.append("user_network_code,modified_by,last_batch_no,modified_on,");
                        sqlselect.append("created_on,previous_status,");
                        sqlselect.append("status,seq_no,created_date,VOUCHER_TYPE, VOUCHER_SEGMENT, INFO5  ");
                         if(SystemPreferences.SEQUENCE_ID_ENABLE || SystemPreferences.HASHING_ENABLE ){
                        	 sqlselect.append(",sequence_id  ");
                         }
                         sqlselect.append(" ) ");
                        sqlselect.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ");
                        if((boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE) || (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE) ){
                       	 sqlselect.append(" ,?  ");
                        }
                        sqlselect.append("  ) ");
                        sqlInsert = sqlselect.toString();
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(METHOD_NAME, "sqlInsert" + sqlInsert);
                        }
                        psmtInsert = p_con.prepareStatement(sqlInsert);
                        if (failCount == 0) {
                            successCount = 0;
                        } else {
                            failCount = 0;
                        }
                        String status = VOMSI.VOUCHER_NEW;
                        //For ERP process vouchers generated into WH status directly
                        if((boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.ERP_VOU_WH) && isERPProcess){
                        	status = VOMSI.WARE_HOUSE;
                        }
                        
                        for (int j = 0; j < pinsList.size(); j++) {

                            ++serialNOcounter;
                            int k = 0;
                            psmtInsert.setLong(++k, ++startserialnumber);
                            psmtInsert.setString(++k, product_id);
                            psmtInsert.setString(++k, (String) pinsList.get(j));
                            psmtInsert.setString(++k, (String) batchVO.getBatchNo());
                            psmtInsert.setInt(++k, 0);
                            psmtInsert.setString(++k, status);
                            psmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(expiryDate));
                            psmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(expiryDate));
                            psmtInsert.setLong(++k, mrp);
                            psmtInsert.setLong(++k, batchVO.getTalktime());
                            psmtInsert.setLong(++k, batchVO.getValidity());
                            psmtInsert.setString(++k, location);
                            psmtInsert.setString(++k, location);
                            psmtInsert.setString(++k, PretupsI.SYSTEM);
                            psmtInsert.setString(++k, (String) batchVO.getBatchNo());
                            psmtInsert.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(currentdate));
                            psmtInsert.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(currentdate));
                            psmtInsert.setString(++k,status);
                            psmtInsert.setString(++k,status);
                            psmtInsert.setLong(++k, j);
                            psmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(currentdate));
                            psmtInsert.setString(++k, voucher_type);
                            psmtInsert.setString(++k, segment);
			    psmtInsert.setString(++k, info5);
                            
                            if(SystemPreferences.SEQUENCE_ID_ENABLE){
                                psmtInsert.setInt(++k,batchVO.getSeq_id());
                            }else if(!SystemPreferences.SEQUENCE_ID_ENABLE&&SystemPreferences.HASHING_ENABLE){
                            	psmtInsert.setInt(++k,BTSLUtil.getUniqueInteger((String) pinsList.get(j), 100, 100+SystemPreferences.HASHING_ID_RANGE));
                            }

                            psmtInsert.addBatch();
                            try {
                                if ((j + 1) % 200 == 0) {
                                    double curCPU = appServerCpuUtilization;
                                    double curDBCPU = dbCpuUtilization;
                                    _logger.debug(METHOD_NAME, " CPU Utilization : " + curCPU);
                                    _logger.debug(METHOD_NAME, " DB Utilization : " + curDBCPU);
            						if (curCPU < Integer.parseInt(Constants.getProperty("RESOURCE_UTIL_ONLINE_VOMS_GEN_THRESHOLD_LIMIT"))  &&  curDBCPU <  Integer.parseInt(Constants.getProperty("RESOURCE_UTIL_ONLINE_VOMS_GEN_THRESHOLD_LIMIT"))) {
            							// Nothing
            						} else {
            							Thread.sleep(Integer.parseInt(Constants.getProperty("PAUSE_APPLICATION_TIME")));
            						}
            						
                                    updateCount = psmtInsert.executeBatch();
                                    successCount = successCount + updateCount.length;
                                    VomsBatchInfoLog.log(currentdate + "Batch No" + batchVO.getBatchNo() + "Batch insert length:" + updateCount.length);
                                }
                            } catch (BatchUpdateException be) {
                                startserialnumber = startserialnumber - (200 - (be.getUpdateCounts().length));
                                serialNOcounter = serialNOcounter - (200 - (be.getUpdateCounts().length));
                                failCount = failCount + (200 - (be.getUpdateCounts().length));
                                successCount = successCount + (be.getUpdateCounts().length);
                                VomsBatchInfoLog.log(currentdate + "Duplicate voucher PIN generated failCount:" + failCount + ",serialNOcounter:" + serialNOcounter + ",startserialnumber:" + startserialnumber);
                                _logger.errorTrace(METHOD_NAME, be);
                            }
                        }
                        try {
                            updateCount = psmtInsert.executeBatch();
                            successCount = successCount + updateCount.length;
                        } catch (BatchUpdateException be) {
                            startserialnumber = startserialnumber - (batchList.size() % 200 - (be.getUpdateCounts().length));
                            serialNOcounter = serialNOcounter - (batchList.size() % 200 - (be.getUpdateCounts().length));
                            failCount = failCount + (batchList.size() % 200 - (be.getUpdateCounts().length ));
                            successCount = successCount + (be.getUpdateCounts().length);
                            VomsBatchInfoLog.log(currentdate + "Duplicate voucher PIN generated failCount:" + failCount + ",serialNOcounter:" + serialNOcounter + ",startserialnumber:" + startserialnumber);
                            _logger.errorTrace(METHOD_NAME, be);
                        }
                        VomsBatchInfoLog.log(currentdate + "Batch Processing commited for batch:" + batchVO.getBatchNo() + "and successCount: " + successCount);

                    } catch (SQLException e) {
                        p_con.rollback();
                        VomsBatchInfoLog.log(currentdate + "Batch Processing terminated for batch:" + batchVO.getBatchNo());
                        _logger.errorTrace(METHOD_NAME, e);
                        throw e;

                    } catch (IllegalStateException e1) {
                        VomsBatchInfoLog.log(currentdate + "Batch Processing terminated for batch:" + batchVO.getBatchNo());
                        _logger.errorTrace(METHOD_NAME, e1);
                        throw e1;
                    }
                    if (failCount > 0) {
                        VomsBatchInfoLog.log(currentdate + "Batch Processing started for fail count:" + failCount);
                        retryCount++;
                        pinsList = _operatorUtil.generatePin(location, activeProductId, failCount,batchVO.getSeq_id());
                        if (pinsList.size() > 0 && retryCount < 5) {
                            continue l1;
                        } else {
                            flag = false;
                        }
                    } else {
                        flag = false;
                    }
                }
                
                String batch_type = VOMSI.BATCH_GENERATED;
                //For ERP process vouchers generated into WH status directly
                if(SystemPreferences.ERP_VOU_WH && isERPProcess) {
                	batch_type = VOMSI.WARE_HOUSE;
                }
         
                // update serial number counter
                sqlselect = new StringBuffer("UPDATE voms_categories SET serial_number_counter=? where category_id=?");
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Update Query" + sqlselect.toString());
                }
                psmtupdate1 = p_con.prepareStatement(sqlselect.toString());
                psmtupdate1.setLong(1, serialNOcounter);
                psmtupdate1.setString(2, category);
                update = psmtupdate1.executeUpdate();
                if (update <= 0) {
                    p_con.rollback();
                } else {
                    vomsVoucherDao = new VomsVoucherDAO();
                    sqlselect = new StringBuffer("UPDATE voms_batches set from_serial_no=?,to_serial_no=?,total_no_of_success=?,");
                    sqlselect.append("modified_date=?,status=?,modified_on=?,message=?,batch_type=?, TOTAL_NO_OF_FAILURE=? ");
                    sqlselect.append(" where batch_no=?");
                    psmtupdate = p_con.prepareStatement(sqlselect.toString());
                    psmtupdate.setLong(1, fromserialnum);
                    psmtupdate.setLong(2, startserialnumber);
                    psmtupdate.setLong(3, successCount);
                    psmtupdate.setDate(4, BTSLUtil.getSQLDateFromUtilDate(currentdate));
                    psmtupdate.setString(5, VOMSI.EXECUTED);
                    psmtupdate.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(currentdate));
                    psmtupdate.setString(7, "Generated Successfully");
                    psmtupdate.setString(8,batch_type);
                    psmtupdate.setLong(9, failCount);
                    psmtupdate.setString(10, batchVO.getBatchNo());
                    update = psmtupdate.executeUpdate();
                    vomsVoucherDao.updateSummaryTable(p_con, batchVO, false);
                }
                // update the batch status to GE and from serial number and to
                // serial number fields
                
                if(!VOMSI.ERP_SYSTEM.equals(source))
                p_con.commit();
                // }
                _logger.info(METHOD_NAME, "BATCH proceesed successfully:" + batchVO.getBatchNo());
                serialnumbrStr = null;
                VomsBatchInfoLog.genVoucherBatchLog(batchVO);
				batchVO.setFromSerialNo(Long.toString(fromserialnum));
				batchVO.setToSerialNo(Long.toString(fromserialnum + successCount - 1));
				batchVO.setVoucherType(voucher_type);
				batchVO.setQuantity(Long.toString(successCount));
				batchVO.setDenomination(PretupsBL.getDisplayAmount(Long.parseLong(vomsVatchesDAO.getDenomination(p_con, batchVO.getProductID()))));
				if(SystemPreferences.VOUCHER_GEN_EMAIL_NOTIFICATION && !isERPProcess)
					sendEmailNotification(p_con, batchVO, "voucher.generation.notification.subject");
				if(SystemPreferences.VOUCHER_GEN_SMS_NOTIFICATION && !isERPProcess)
				{
					sendSMSNotification(p_con, batchVO, "voucher.generation.notification.subject");
				}
            }// end of for loop for batchlist
            isDataProcessed = true;
        } catch (SQLException sqe) {
            p_con.rollback();
            _logger.errorTrace(METHOD_NAME, sqe);
            _logger.error(METHOD_NAME, "SQLException" + sqe);
            throw sqe;
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException" + be);
            throw be;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception e" + e);
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info(METHOD_NAME, "Exciting ");
            }
            try {
                if (pstmtSel != null) {
                    pstmtSel.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtupdate != null) {
                	psmtupdate.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtupdate1 != null) {
                	psmtupdate1.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
        }
        return isDataProcessed;
    }
} // end class
