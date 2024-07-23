package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLMessages;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;
import com.btsl.util.OracleUtil;

class SubscriberRetailerDetailsVO {
	private String subscriberMsisdn;
	private String retailer1Msisdn;
	private String retailer2Msisdn;
	private String retailer3Msisdn;
	private String retailer4Msisdn;
	private String retailer5Msisdn;

	private String retailer1FirstName;
	private String retailer2FirstName;
	private String retailer3FirstName;
	private String retailer4FirstName;
	private String retailer5FirstName;

	private String retailer1LastName;
	private String retailer2LastName;
	private String retailer3LastName;
	private String retailer4LastName;
	private String retailer5LastName;

	private String status;
	private Date createdOn;
	private Date lastUpdatedOn;
	private String networkCode;
	private int batchNo;

	public String getSubscriberMsisdn() {
		return subscriberMsisdn;
	}

	public void setSubscriberMsisdn(String subscriberMsisdn) {
		this.subscriberMsisdn = subscriberMsisdn;
	}

	public String getRetailer1Msisdn() {
		return retailer1Msisdn;
	}

	public void setRetailer1Msisdn(String retailer1Msisdn) {
		this.retailer1Msisdn = retailer1Msisdn;
	}

	public String getRetailer2Msisdn() {
		return retailer2Msisdn;
	}

	public void setRetailer2Msisdn(String retailer2Msisdn) {
		this.retailer2Msisdn = retailer2Msisdn;
	}

	public String getRetailer3Msisdn() {
		return retailer3Msisdn;
	}

	public void setRetailer3Msisdn(String retailer3Msisdn) {
		this.retailer3Msisdn = retailer3Msisdn;
	}

	public String getRetailer4Msisdn() {
		return retailer4Msisdn;
	}

	public void setRetailer4Msisdn(String retailer4Msisdn) {
		this.retailer4Msisdn = retailer4Msisdn;
	}

	public String getRetailer5Msisdn() {
		return retailer5Msisdn;
	}

	public void setRetailer5Msisdn(String retailer5Msisdn) {
		this.retailer5Msisdn = retailer5Msisdn;
	}

	public String getRetailer1FirstName() {
		return retailer1FirstName;
	}

	public void setRetailer1FirstName(String retailer1FirstName) {
		this.retailer1FirstName = retailer1FirstName;
	}

	public String getRetailer2FirstName() {
		return retailer2FirstName;
	}

	public void setRetailer2FirstName(String retailer2FirstName) {
		this.retailer2FirstName = retailer2FirstName;
	}

	public String getRetailer3FirstName() {
		return retailer3FirstName;
	}

	public void setRetailer3FirstName(String retailer3FirstName) {
		this.retailer3FirstName = retailer3FirstName;
	}

	public String getRetailer4FirstName() {
		return retailer4FirstName;
	}

	public void setRetailer4FirstName(String retailer4FirstName) {
		this.retailer4FirstName = retailer4FirstName;
	}

	public String getRetailer5FirstName() {
		return retailer5FirstName;
	}

	public void setRetailer5FirstName(String retailer5FirstName) {
		this.retailer5FirstName = retailer5FirstName;
	}

	public String getRetailer1LastName() {
		return retailer1LastName;
	}

	public void setRetailer1LastName(String retailer1LastName) {
		this.retailer1LastName = retailer1LastName;
	}

	public String getRetailer2LastName() {
		return retailer2LastName;
	}

	public void setRetailer2LastName(String retailer2LastName) {
		this.retailer2LastName = retailer2LastName;
	}

	public String getRetailer3LastName() {
		return retailer3LastName;
	}

	public void setRetailer3LastName(String retailer3LastName) {
		this.retailer3LastName = retailer3LastName;
	}

	public String getRetailer4LastName() {
		return retailer4LastName;
	}

	public void setRetailer4LastName(String retailer4LastName) {
		this.retailer4LastName = retailer4LastName;
	}

	public String getRetailer5LastName() {
		return retailer5LastName;
	}

	public void setRetailer5LastName(String retailer5LastName) {
		this.retailer5LastName = retailer5LastName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}
	
	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	
	public int getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(int batchNo) {
		this.batchNo = batchNo;
	}

	@Override
	public String toString() {
		final StringBuilder sbf = new StringBuilder();
		sbf.append(super.toString());
		sbf.append("subscriberMsisdn =").append(subscriberMsisdn);
		sbf.append(",retailer1Msisdn =").append(retailer1Msisdn);
		sbf.append(",retailer1FirstName =").append(retailer1FirstName);
		sbf.append(",retailer1LastName =").append(retailer1LastName);
		sbf.append(",retailer2Msisdn =").append(retailer2Msisdn);
		sbf.append(",retailer2FirstName =").append(retailer2FirstName);
		sbf.append(",retailer2LastName =").append(retailer2LastName);
		sbf.append(",retailer3Msisdn =").append(retailer3Msisdn);
		sbf.append(",retailer3FirstName =").append(retailer3FirstName);
		sbf.append(",retailer3LastName =").append(retailer3LastName);
		sbf.append(",retailer4Msisdn =").append(retailer4Msisdn);
		sbf.append(",retailer4FirstName =").append(retailer4FirstName);
		sbf.append(",retailer4LastName =").append(retailer4LastName);
		sbf.append(",retailer5Msisdn =").append(retailer5Msisdn);
		sbf.append(",retailer5FirstName =").append(retailer5FirstName);
		sbf.append(",retailer5LastName =").append(retailer5LastName);
		sbf.append(",status =").append(status);
		sbf.append(",createdOn =").append(createdOn);
		sbf.append(",lastUpdatedOn =").append(lastUpdatedOn);
		sbf.append(",networkCode =").append(networkCode);//Need to add in table
		sbf.append(",batchNo =").append(batchNo);//Need to add in table
		return sbf.toString();
	}
}

public class SendRetailerDetailsProcess {

	public static void main(String[] args) {
		String batchNo = null;
		int numberOfRetailers = 0;
        try {
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
            if(!SendRetailerDetailsProcess.isNullString(args[2])) {
            	batchNo = args[2];            	
            } else {
            	System.out.println(" Batch no not passed .............");
            }
        	try {
        		int num = Integer.parseInt(args[3]);
            	if(num <= 0 || num > 5) {
            		numberOfRetailers = 5;
            	} else {
            		numberOfRetailers = num;
            	}
        	} catch(Exception ex) {
        		numberOfRetailers = 5;
        	}
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (Exception ex) {
            System.out.println("Error in Loading Configuration files ...........................: " + ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            getDataFromTable(con, batchNo, numberOfRetailers);
        } catch (Exception ex) {
        	System.out.println(" main : Exception = " + ex);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
            	System.out.println(" main : Exception = " + ex);
            }
            try {
                Thread.sleep(5000);
            } catch (Exception ex) {
            	System.out.println(" main : Exception = " + ex);
            }
            ConfigServlet.destroyProcessCache();
        }
    }
	
	/**
	 * To fetch data from SUBS_RETAILER_DETAILS table
	 * @return
	 * @throws SQLException
	 */
	public static ArrayList<SubscriberRetailerDetailsVO> getDataFromTable(Connection conn, String batchNo, int numberOfRetailers) throws SQLException {

        final String methodName = "getDataFromTable";
        SubscriberRetailerDetailsVO subscriberRetailerDetailsVO = null;
        ArrayList<SubscriberRetailerDetailsVO> subscriberRetailerDetailsVOList = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        PushMessage pushMessage = null;
        BTSLMessages messages = null;
        String[] batchList = null;
        int x = 0;
        try {
        	final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            final StringBuffer str = new StringBuffer("SELECT ");
            batchList = batchNo.split(",");
            str.append(" SUBSCRIBER_MSISDN, RETAILER1_MSISDN, RETAILER1_FIRST_NAME, RETAILER1_LAST_NAME, ");
            str.append(" RETAILER2_MSISDN, RETAILER2_FIRST_NAME, RETAILER2_LAST_NAME,");
            str.append(" RETAILER3_MSISDN, RETAILER3_FIRST_NAME, RETAILER3_LAST_NAME,");
            str.append(" RETAILER4_MSISDN, RETAILER4_FIRST_NAME, RETAILER4_LAST_NAME,");
            str.append(" RETAILER5_MSISDN, RETAILER5_FIRST_NAME, RETAILER5_LAST_NAME");
            str.append(" FROM SUBS_RETAILER_DETAILS");
            str.append(" WHERE BATCH_NO = ?");
            stmt = conn.prepareStatement(str.toString());
            System.out.println("Select Query = " +str.toString());
            
            for (int i = 0; i < batchList.length; i++) {
            	x = 0;
	            stmt.setString(++x, batchList[i]);
	            rs = stmt.executeQuery();
	            subscriberRetailerDetailsVOList = new ArrayList<SubscriberRetailerDetailsVO>();
	            while (rs.next()) {
	                subscriberRetailerDetailsVO = new SubscriberRetailerDetailsVO();
	                subscriberRetailerDetailsVO.setSubscriberMsisdn(rs.getString("SUBSCRIBER_MSISDN"));
	                subscriberRetailerDetailsVO.setRetailer1Msisdn(rs.getString("RETAILER1_MSISDN"));
	                subscriberRetailerDetailsVO.setRetailer1FirstName(rs.getString("RETAILER1_FIRST_NAME"));
	                subscriberRetailerDetailsVO.setRetailer1LastName(rs.getString("RETAILER1_LAST_NAME"));
	                subscriberRetailerDetailsVO.setRetailer2Msisdn(rs.getString("RETAILER2_MSISDN"));
	                subscriberRetailerDetailsVO.setRetailer2FirstName(rs.getString("RETAILER2_FIRST_NAME"));
	                subscriberRetailerDetailsVO.setRetailer2LastName(rs.getString("RETAILER2_LAST_NAME"));
	                subscriberRetailerDetailsVO.setRetailer3Msisdn(rs.getString("RETAILER3_MSISDN"));
	                subscriberRetailerDetailsVO.setRetailer3FirstName(rs.getString("RETAILER3_FIRST_NAME"));
	                subscriberRetailerDetailsVO.setRetailer3LastName(rs.getString("RETAILER3_LAST_NAME"));
	                subscriberRetailerDetailsVO.setRetailer4Msisdn(rs.getString("RETAILER4_MSISDN"));
	                subscriberRetailerDetailsVO.setRetailer4FirstName(rs.getString("RETAILER4_FIRST_NAME"));
	                subscriberRetailerDetailsVO.setRetailer4LastName(rs.getString("RETAILER4_LAST_NAME"));
	                subscriberRetailerDetailsVO.setRetailer5Msisdn(rs.getString("RETAILER5_MSISDN"));
	                subscriberRetailerDetailsVO.setRetailer5FirstName(rs.getString("RETAILER5_FIRST_NAME"));
	                subscriberRetailerDetailsVO.setRetailer5LastName(rs.getString("RETAILER5_LAST_NAME"));
	                
					String[] array = new String[15];
					int retailerCounter = 0;
					if(numberOfRetailers > 0 && !SendRetailerDetailsProcess.isNullString(subscriberRetailerDetailsVO.getRetailer1Msisdn())) {
						array[0] = subscriberRetailerDetailsVO.getRetailer1Msisdn();
						array[1] = subscriberRetailerDetailsVO.getRetailer1FirstName();
						array[2] = subscriberRetailerDetailsVO.getRetailer1LastName();
						retailerCounter++;
					}
					if(numberOfRetailers > 1 && !SendRetailerDetailsProcess.isNullString(subscriberRetailerDetailsVO.getRetailer2Msisdn())) {
						array[3] = subscriberRetailerDetailsVO.getRetailer2Msisdn();
						array[4] = subscriberRetailerDetailsVO.getRetailer2FirstName();
						array[5] = subscriberRetailerDetailsVO.getRetailer2LastName();
						retailerCounter++;
					}
					if(numberOfRetailers > 2 && !SendRetailerDetailsProcess.isNullString(subscriberRetailerDetailsVO.getRetailer3Msisdn())) {
						array[6] = subscriberRetailerDetailsVO.getRetailer3Msisdn();
						array[7] = subscriberRetailerDetailsVO.getRetailer3FirstName();
						array[8] = subscriberRetailerDetailsVO.getRetailer3LastName();
						retailerCounter++;
					}
					if(numberOfRetailers > 3 && !SendRetailerDetailsProcess.isNullString(subscriberRetailerDetailsVO.getRetailer4Msisdn())) {
						array[9] = subscriberRetailerDetailsVO.getRetailer4Msisdn();
						array[10] = subscriberRetailerDetailsVO.getRetailer4FirstName();
						array[11] = subscriberRetailerDetailsVO.getRetailer4LastName();
						retailerCounter++;
					}
					if(numberOfRetailers > 4 && !SendRetailerDetailsProcess.isNullString(subscriberRetailerDetailsVO.getRetailer5Msisdn())) {
						array[12] = subscriberRetailerDetailsVO.getRetailer5Msisdn();
						array[13] = subscriberRetailerDetailsVO.getRetailer5FirstName();
						array[14] = subscriberRetailerDetailsVO.getRetailer5LastName();
						retailerCounter++;
					}
					pushMessage = new PushMessage(subscriberRetailerDetailsVO.getSubscriberMsisdn(), BTSLUtil.getMessage(locale, "5265030_"+retailerCounter, array), null, null, locale);
					pushMessage.push();
	            }
	            updateTable(subscriberRetailerDetailsVO, "EX", conn, batchList[i]);
            }
        } catch (SQLException e) {
            throw new SQLException(methodName + " Exception=" + e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    System.out.println(methodName + " Exception = " + ex.getMessage());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                	System.out.println(methodName + " Exception = " + ex.getMessage());
                }
            }
        }
        return subscriberRetailerDetailsVOList;
	}
	
	/**
     * Converts Util date to Sql Date
     * 
     * @param utilDate
     * @return
     */
    public static java.sql.Date getSQLDateFromUtilDate(java.util.Date utilDate) {
        java.sql.Date sqlDate = null;
     
		 if (utilDate != null) {
			 final java.util.Date utilDateNew=(Date)utilDate.clone();
			 try {
				 utilDate.setHours(0);
				 utilDate.setMinutes(0);
				 utilDate.setSeconds(0);
				 sqlDate = new java.sql.Date(utilDate.getTime());
				 utilDate=utilDateNew;
			} catch (Exception e) {
				sqlDate = new java.sql.Date(utilDate.getTime());
				utilDate=utilDateNew;
			}
	     }
	     return sqlDate;
	}
    
    /**
     * Converts Util date to Sql Date
     * 
     * @param utilDate
     * @return
     */
    public static java.sql.Timestamp getSQLDateTimeFromUtilDate(java.util.Date utilDate) {
        java.sql.Timestamp sqlDateTime = null;
        if (utilDate != null) {
            sqlDateTime = new java.sql.Timestamp(utilDate.getTime());
        }
        return sqlDateTime;
    }
    
    /**
     * This method adds the no of days in the passed date
     * 
     * @param p_date
     * @param p_no_ofDays
     * @return Date
     */
    public static Date addDaysInUtilDate(java.util.Date p_date, int p_no_ofDays) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(p_date);
        cal.add(Calendar.DATE, p_no_ofDays);
        return cal.getTime();
    }
    
    /**
     * Is Null String
     * 
     * @param str
     * @return
     */
    public static boolean isNullString(String str) {
        if (str == null || str.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Decrypts the passed text string using an encryption key for 3DES & AES
     * 
     * @param p_text
     * @return
     */
    public static String decrypt3DesText(String p_text) {
        try {
        	return new CryptoUtil().decrypt(p_text, Constants.KEY);
        } catch (Exception e) {
            System.out.println("decrypt3DesText Exception = " + e);
            return null;
        }
    }
    
    /**
     * 
     * @param subscriberRetailerDetailsVO
     * @param status
     * @param conn
     * @param batchNo
     * @return
     * @throws SQLException
     */
	public static int updateTable(SubscriberRetailerDetailsVO subscriberRetailerDetailsVO, String status, 
			Connection conn, String batchNo) throws SQLException {
		final String methodName = "updateTable";
		System.out.println(methodName + " batchNo" );
        int updateCount = 0;
        Date currentDate = null;
        try {
        	if(subscriberRetailerDetailsVO == null) {
        		return 0;
        	}
            final StringBuffer str = new StringBuffer(" UPDATE");
            str.append(" SUBS_RETAILER_DETAILS SET STATUS = ?, LAST_UPDATED_ON = ?");
            str.append(" WHERE BATCH_NO = ?");
            final String query = str.toString();
            System.out.println("Update Query = " + query);
            try(PreparedStatement psmt = conn.prepareStatement(query);) {
            	currentDate = new Date();
            	int i = 0;
	            psmt.setString(++i, status);
	            psmt.setTimestamp(++i, SendRetailerDetailsProcess.getSQLDateTimeFromUtilDate(currentDate));
	            psmt.setString(++i, batchNo);
	            updateCount = psmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SQLException(methodName + " Exception=" + e.getMessage());
        } finally {
        	System.out.println(methodName + " updateCount = " + updateCount);
        }
        return updateCount;
	}
}