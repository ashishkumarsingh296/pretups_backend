package com.btsl.pretups.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDetailsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserLoanVO;
import com.btsl.util.BTSLUtil;

public class CSVLoanReportReader implements Runnable {

	private BlockingQueue<String> queue;
	private PreparedStatement preparedStatement;
	private MutableBoolean mutableBoolean;
	private String[] resultSetColumnName;
	private String[] amountArr;
	private String UserID = "user_id";
	private String productCode = "product_code";
	private String rptCode;
	private String LoanAmount = "LOAN_AMOUNT";
	private String LoanGiven = "LOAN_GIVEN";
	

	private Connection conn;
	private Log log = LogFactory.getLog(this.getClass().getName());

	public CSVLoanReportReader(BlockingQueue<String> queue, PreparedStatement preparedStatement,
			MutableBoolean mutableBoolean, String[] resultSetColumnName, Connection conn, String[] amountArr,
			String rptCode) {
		super();
		this.queue = queue;
		this.preparedStatement = preparedStatement;
		this.mutableBoolean = mutableBoolean;
		this.resultSetColumnName = resultSetColumnName;
		this.amountArr = amountArr;
		this.conn = conn;
		this.rptCode = rptCode;

	}

	public static OperatorUtilI calculatorI = null;
	// calculate the tax
	static {
		final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
		} catch (Exception e) {
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelTransferBL[initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}

	@Override
	public void run() {
		final String methodName = "run";
		ResultSet rs;
		UserLoanVO userLoanVO = null;
		Boolean isAmountColadded = false;

		try {
			log.debug(methodName, "Started Reading..................");
			rs = preparedStatement.executeQuery();
			StringBuilder row = new StringBuilder();
			while (rs.next()) {
				
				for (String columnName : resultSetColumnName) {
					isAmountColadded = false;
					if (row.length() > 0) {
						row.append(",");
					}
					
			
					if (columnName != null) {

						for (String amountCol : amountArr) {
							if (columnName.equals(amountCol)) {
								if(!BTSLUtil.isNullString(rs.getString(columnName))) {
								row.append(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString(columnName).trim())));
								isAmountColadded = true;
								}
								else {
									row.append("");
									isAmountColadded = true;
											
								}
								
								break;
							} 
							else if(columnName.equals("Disburserment_date_time")||columnName.equals("Settlement_date_time")) {
								java.text.SimpleDateFormat source = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								//SimpleDateFormat sm = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								//String strDate = sm.format(rs.getString(columnName));
								java.util.Date date = source.parse(rs.getString(columnName));
								
								SimpleDateFormat sm1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								String strDate = sm1.format(date);
								row.append(strDate);
								isAmountColadded = true;
								break;
							}
							else {
								continue;
							}
						}
						log.debug(methodName, "columnName--"+columnName);
						if (!isAmountColadded)
							row.append(rs.getString(columnName).trim());
						log.debug(methodName, "row--"+row);

					} else {
						row.append("");
					}

				}
				String userID = rs.getString(UserID);
				String ProductCode = rs.getString(productCode);
				String LoanAmt = rs.getString(LoanAmount);
				String loanGiven = rs.getString(LoanGiven);
				
				log.debug(methodName, "rptCode"+rptCode+"LoanGiven"+loanGiven);
				
				if (rptCode == "LOANDBMT01" && PretupsI.AGENT_ALLOWED_YES.equals(loanGiven)) {
					
					ChannelUserVO channelUserVO = new ChannelUserDAO().loadChannelUserByUserID(conn, userID);
					
					if(channelUserVO!=null) {
						for (UserLoanVO loanVO : channelUserVO.getUserLoanVOList()) {

							if (userID.equals(loanVO.getUser_id()) && loanVO.getProduct_code().equals(ProductCode)) {
								userLoanVO = loanVO;
								// isLoanExist = true;
								break;
							} else
								continue;
						}}
					if(userLoanVO!= null) {
						ArrayList<LoanProfileDetailsVO> loanProfileList = new LoanProfileDAO().loadLoanProfileSlabs(conn,
								String.valueOf(userLoanVO.getProfile_id()));

						long premiumAmount = calculatorI.calculatePremium(userLoanVO, loanProfileList);
						long totalLoanAmount = Long.parseLong(LoanAmt) + premiumAmount;
						row.append(",");
						row.append(PretupsI.AGENT_ALLOWED_NO);
						row.append(",");
						row.append(PretupsBL.getDisplayAmount(premiumAmount));
						row.append(",");
						row.append(PretupsBL.getDisplayAmount(totalLoanAmount));
					}
					else
					{
						row.append(",");
						row.append(PretupsI.AGENT_ALLOWED_NO);
						row.append(",");
						row.append(",");
					}
					queue.put(row.toString().trim());
					log.debug(methodName, row);
					row.setLength(0);
				}
				else if (rptCode == "LOANDBMT01" && PretupsI.AGENT_ALLOWED_NO.equals(loanGiven)) {
					log.debug(methodName, "rptCode"+rptCode+"LoanGiven"+loanGiven);
					
					row.append(",");
					row.append(PretupsI.AGENT_ALLOWED_YES);
					row.append(",");
					row.append(PretupsBL.getDisplayAmount(rs.getInt("SETTLEMENT_LOAN_INTEREST")));
					row.append(",");
					row.append(PretupsBL.getDisplayAmount(rs.getInt("SETTLEMENT_LOAN_AMOUNT")));
					
					log.debug(methodName, row);
					queue.put(row.toString().trim());
					row.setLength(0);
				}
				else
				{
					log.debug(methodName, row);
					queue.put(row.toString().trim());
					row.setLength(0);
				}

			}

			rs.close();
			preparedStatement.close();
			this.mutableBoolean.setValue(true);
		} catch (Exception e) {
			log.errorTrace(methodName, e);

		}
	}

}
