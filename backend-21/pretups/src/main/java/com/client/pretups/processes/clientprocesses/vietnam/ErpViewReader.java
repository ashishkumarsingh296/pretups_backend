package com.client.pretups.processes.clientprocesses.vietnam;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

/**
 * Process for Coping data from  External View to pretups Table
 * @author 
 * @since 06/10/2017
 *
 */
public class ErpViewReader {

    private static final Log LOG = LogFactory.getLog(ErpViewReader.class.getName());
    private static String erpTableName=null;
    private static String erpDateTimeFormat= null;
    private static String erpOrderDateDBTimeFormat=null;
    private static String erpOrderDateEntryTimeFormat=null;
    private static String orderItemType =null;
    
    /**
     * Method Main
     * Main Method for the process
     * 
     * @param args
     */
    
    public static void main(String[] args) {
        final String methodName = "main";
        if (args.length!=4){
            LogFactory.printLog(methodName, "Usage : ErpViewReader [Constants file] [LogConfig file] [fromDate] [todate] ",LOG);
            return;
        }
        try {
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                LogFactory.printLog(methodName, "Constants File Not Found .............",LOG);
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                LogFactory.printLog(methodName, " Logconfig File Not Found .............",LOG);
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }catch (Exception ex) {
            LogFactory.printLog(methodName, "Error in Loading Configuration files ...........................: " + ex,LOG);
            LOG.errorTrace(methodName, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            // getting all the required parameters from Constants.props
            loadConstantParameters();
            process(args[2].trim(),args[3].trim());
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        } finally {
            LogFactory.printLog(methodName, " Exiting",LOG);
            ConfigServlet.destroyProcessCache();
        }
    }
    
    private static void loadConstantParameters() {
        final String methodName = "loadConstantParameters";
        LogFactory.printLog(methodName, " Entered: ",LOG);
        try {
            erpTableName=Constants.getProperty("ERP_TABLE_NAME");
            if (BTSLUtil.isNullString(erpTableName)) {
                LogFactory.printError(methodName, " Could not find file label for ERP_TABLE_NAME in the Constants file.",LOG);
            } else {
                LogFactory.printLog(methodName, " erpTableName=" + erpTableName,LOG);
                LogFactory.printLog(methodName,  " Required information successfully loaded from Constants.props...............: ",LOG);
            }
            erpDateTimeFormat=Constants.getProperty("ERP_DATE_TIME_FORMAT");
            if (BTSLUtil.isNullString(erpDateTimeFormat)) {
                LogFactory.printError(methodName, " Could not find file label for ERP_DATE_TIME_FORMAT in the Constants file.",LOG);
            } else {
                LogFactory.printLog(methodName, " erpDateTimeFormat = " + erpDateTimeFormat,LOG);
                LogFactory.printLog(methodName,  "Required information  successfuly loaded from  Constants.props...............: ",LOG);
            }
            erpOrderDateDBTimeFormat=Constants.getProperty("ERP_ORDER_DATE_DB_TIME_FORMAT");
            if (BTSLUtil.isNullString(erpOrderDateDBTimeFormat)) {
                LogFactory.printError(methodName, " Could not find file label for ERP_ORDER_DATE_DB_TIME_FORMAT in the Constants file.",LOG);
            } else {
                LogFactory.printLog(methodName, " erpOrderDateDBTimeFormat=" + erpDateTimeFormat,LOG);
                LogFactory.printLog(methodName,  " Required information successfuly loaded from  Constants.props...............: ",LOG);
            }
            erpOrderDateEntryTimeFormat=Constants.getProperty("ERP_ORDER_DATE_ENTRY_TIME_FORMAT");
            if (BTSLUtil.isNullString(erpOrderDateEntryTimeFormat)) {
                LogFactory.printError(methodName, " Could not find file label for ERP_ORDER_DATE_ENTRY_TIME_FORMAT in the Constants file.",LOG);
            } else {
                LogFactory.printLog(methodName, " erpOrderDateEntryTimeFormat=" + erpDateTimeFormat,LOG);
                LogFactory.printLog(methodName,  " Required information successfuly loaded from  Constants.props...............: ",LOG);
            }
            orderItemType=Constants.getProperty("ERP_ORDER_ITEM_TYPE");
            if (BTSLUtil.isNullString(orderItemType)) {
                LogFactory.printError(methodName, " Could not find file label for ERP_ORDER_ITEM_TYPE in the Constants file.",LOG);
            } else {
                LogFactory.printLog(methodName, " orderItemType=" + orderItemType,LOG);
                LogFactory.printLog(methodName,  " Required information successfuly loaded from Constants.props...............: ",LOG);
            }
            LogFactory.printLog(methodName,  "Completely Required information loaded from Constants.props...............: ",LOG);
        } catch (Exception e) {
            LogFactory.printError(methodName, "Exception : " + e.getMessage(),LOG);
            LOG.errorTrace(methodName, e);
        }

    }
    

    private static void process(String fromDate, String toDate) throws BTSLBaseException {
        final String methodName = "process";
        LogFactory.printLog(methodName, " Entered: fromDate: "+fromDate +" toDate: "+toDate,LOG);
        Connection con = null;MComConnectionI mcomCon = null;
        Connection conErp = null;
        int count=0;
        java.util.Date toUtilDate=new Date();
        java.util.Date fromUtilDate=new Date();
        try{
            fromUtilDate = BTSLUtil.getDateFromDateString(fromDate,erpDateTimeFormat);
            toUtilDate = BTSLUtil.getDateFromDateString(toDate,erpDateTimeFormat);
        }catch(Exception e){
            throw new BTSLBaseException(methodName, "Not able to parse date from the give format:"+erpDateTimeFormat);    
        }
        try{
        	conErp=OracleUtil.getExternalDBConnection();
            if (conErp == null) {
                LogFactory.printLog(methodName, " ERP DATABASE Connection is NULL ", LOG);
                throw new BTSLBaseException(methodName, " Not able to get the connection ");
            }
            mcomCon = new MComConnection();
            try{con=mcomCon.getConnection();}catch(SQLException e){}
            if (con == null) {
                LogFactory.printLog(methodName, " Pretups DATABASE Connection is NULL ", LOG);
                throw new BTSLBaseException(methodName, "Not able to get the connection");
            }
            count=erpRead(con, conErp,fromUtilDate,toUtilDate);               
        }catch(BTSLBaseException be){
            throw be;
        }finally{
            if (con != null){
                try {
                    mcomCon.finalCommit();
                } catch (SQLException e) {
                    LOG.errorTrace(methodName, e);
                    LogFactory.printLog(methodName, "issue while con commit"+count, LOG);
                }
            }
			if (mcomCon != null) {
				mcomCon.close("ErpViewReader#process");
				mcomCon = null;
			}
            OracleUtil.closeQuietly(conErp);
            LogFactory.printLog(methodName, " Exiting . Records taken from ERP to PreTUPS is=:"+count, LOG);
        }
    }
    
    
    
    private static int erpRead(Connection con,Connection conErp, Date fromDate,Date toDate) throws BTSLBaseException{
        final String methodName = "erpRead";
        LogFactory.printLog(methodName, "Entered with fromDate:"+fromDate+ " toDate:"+toDate, LOG);
        String[] orderItems =orderItemType.split(",");
        StringBuilder selectQueryBuf = new StringBuilder("SELECT * from  ");
        selectQueryBuf.append(erpTableName.trim());
        selectQueryBuf.append(" WHERE ORDERED_ITEM in ( ");
        for (int i =0; i<orderItems.length;i++){
            if (i==orderItems.length-1){
                selectQueryBuf.append(" ? ");
            }else{
                selectQueryBuf.append(" ?, ");
            }
        }
        selectQueryBuf.append(" ) and ORDER_DATE >= to_date(?,?) and ORDER_DATE <= to_date(?,?) ");
        String selectQuery = selectQueryBuf.toString();
        LogFactory.printLog(methodName, "selectQuery :"+selectQuery, LOG);
        PreparedStatement selectPstmt = null;
        ResultSet rst = null;
        int count=0;
        try{
            selectPstmt = conErp.prepareStatement(selectQuery);
            int i=1;
            for (int j=0; j<orderItems.length; j++){
                selectPstmt.setString(i++,orderItems[j]);
            }
            selectPstmt.setString(i++,BTSLUtil.getDateStringFromDate(fromDate,erpOrderDateEntryTimeFormat));
            selectPstmt.setString(i++,erpOrderDateDBTimeFormat);
            selectPstmt.setString(i++,BTSLUtil.getDateStringFromDate(toDate,erpOrderDateEntryTimeFormat));
            selectPstmt.setString(i++,erpOrderDateDBTimeFormat);
            rst = selectPstmt.executeQuery();
            count=erpWrite(con, rst);
        }catch(SQLException sqle){
            LOG.errorTrace(methodName, sqle);
            LogFactory.printError(methodName, "ERP select Query caused an issue.", LOG);
            throw new BTSLBaseException(methodName, "ERP select Query caused an issue.");
        }catch(BTSLBaseException be){
            throw be;
        }catch(Exception e){
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(methodName, "ERP erpReadReader  Issue faced.");
        }finally{
            OracleUtil.closeQuietly(rst);
            OracleUtil.closeQuietly(selectPstmt);
            LogFactory.printLog(methodName, "Exiting with insert count:"+count, LOG);
        }
        return count;
    }
    
    private static int erpWrite(Connection con, ResultSet rst) throws BTSLBaseException{
        final String methodName = "erpWrite";
        LogFactory.printLog(methodName, "Entering with resultset:", LOG);
        StringBuilder insertQueryBuf = new StringBuilder("Insert into ERP ");
        insertQueryBuf.append(" (ORDER_NUMBER, HEADER_ID, ORDER_TYPE_ID, ORDER_TYPE, SHIP_TO_ORG_ID, ");
        insertQueryBuf.append("    SALE_CHANNEL, PARTY_TYPE, PARTY_NUMBER, PARTY_ID, DEALER_CODE, ");
        insertQueryBuf.append("    PARTY_NAME, ORDER_DATE, LINE_NUMBER, LINE_ID, INVENTORY_ITEM_ID, ");
        insertQueryBuf.append("    ITEM_TYPE_CODE, ORDER_QUANTITY_UOM, SHIPPING_QUANTITY_UOM, ORDERED_QUANTITY, PRICING_QUANTITY, ");
        insertQueryBuf.append("    PRICING_QUANTITY_UOM, AMOUNT, UNIT_LIST_PRICE, TAX_VALUE, ORDERED_ITEM, ");
        insertQueryBuf.append("    ORDERED_ITEM_ID, LAST_MODIFIED, CUST_PO_NUMBER ,TRANSFER_ID, STATUS, ERROR_CODE) ");
        insertQueryBuf.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertQuery = insertQueryBuf.toString();
        LogFactory.printLog(methodName, "insertQuery: "+insertQuery, LOG);
        PreparedStatement insertPstmt = null;
        int insertCount=0;
        int count=0;
        try{
            while(rst.next()){
                insertPstmt=con.prepareStatement(insertQuery);
                insertPstmt.setInt(1 ,rst.getInt("ORDER_NUMBER") );
                insertPstmt.setInt(2, rst.getInt("HEADER_ID"));
                insertPstmt.setInt(3 , rst.getInt("ORDER_TYPE_ID") );
                insertPstmt.setString(4 ,rst.getString("ORDER_TYPE"));
                insertPstmt.setInt(5 , rst.getInt("SHIP_TO_ORG_ID"));
                insertPstmt.setString(6 ,rst.getString("SALE_CHANNEL"));
                insertPstmt.setString(7 ,rst.getString("PARTY_TYPE"));
                insertPstmt.setInt(8 , rst.getInt("PARTY_NUMBER"));
                insertPstmt.setInt(9 , rst.getInt("PARTY_ID"));
                insertPstmt.setString(10 ,rst.getString("DEALER_CODE"));
                insertPstmt.setString(11 ,rst.getString("PARTY_NAME"));
                insertPstmt.setDate(12 , rst.getDate("ORDER_DATE"));
                insertPstmt.setInt(13 , rst.getInt("LINE_NUMBER"));
                insertPstmt.setInt(14 , rst.getInt("LINE_ID"));
                insertPstmt.setInt(15 , rst.getInt("INVENTORY_ITEM_ID"));
                insertPstmt.setString(16 ,rst.getString("ITEM_TYPE_CODE"));
                insertPstmt.setString(17 ,rst.getString("ORDER_QUANTITY_UOM"));
                insertPstmt.setString(18 ,rst.getString("ORDER_QUANTITY_UOM"));
                insertPstmt.setDouble(19 , convertAmountInPretupsString(rst.getDouble("ORDERED_QUANTITY")));
                insertPstmt.setDouble(20 , convertAmountInPretupsString(rst.getDouble("PRICING_QUANTITY")));
                insertPstmt.setString(21 , rst.getString("PRICING_QUANTITY_UOM"));
                insertPstmt.setDouble(22 , convertAmountInPretupsString( rst.getDouble("AMOUNT")));
                insertPstmt.setDouble(23 , convertAmountInPretupsString( rst.getDouble("UNIT_LIST_PRICE")));
                insertPstmt.setDouble(24 , convertAmountInPretupsString( rst.getDouble("TAX_VALUE")));
                insertPstmt.setString(25 , rst.getString("ORDERED_ITEM"));
                insertPstmt.setInt(26 , rst.getInt("ORDERED_ITEM_ID"));
                insertPstmt.setDate(27 , rst.getDate("LAST_MODIFIED"));
                insertPstmt.setString(28 , rst.getString("CUST_PO_NUMBER"));
                insertPstmt.setString(29 , "");
                insertPstmt.setString(30 , "");
                insertPstmt.setString(31 , "");
                insertCount = insertPstmt.executeUpdate();
                count+=insertCount;
            }
        }catch(SQLException e){
            LOG.errorTrace(methodName, e);
            LogFactory.printError(methodName, "ERP Insert Query caused an issue.", LOG);
            throw new BTSLBaseException(methodName, "ERP Insert Query caused an issue.");
        }catch(Exception be){
            LOG.errorTrace(methodName, be);
            LogFactory.printError(methodName, "ERP erpWriteReader caused an issue.", LOG);
            throw new BTSLBaseException(methodName, "PreTUPS erpWriteReader caused an issue!");
        }finally{
            OracleUtil.closeQuietly(insertPstmt);
            LogFactory.printLog(methodName, "Exiting with count="+count, LOG);
        }
        return count;
    }
        
    private static Long convertAmountInPretupsString(Double amount){
        Double amountDouble=amount*((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
        amountDouble=java.lang.Math.floor(amountDouble);
        String amountString=DecimalFormat.getIntegerInstance().format(amountDouble);
        return Long.parseLong(amountString.replaceAll(",",""));
    }
}
