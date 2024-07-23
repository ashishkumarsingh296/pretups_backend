package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class SlabGeneratorPostgresQry implements SlabGeneratorQry{	
	public static final Log log = LogFactory.getFactory().getInstance(SlabGeneratorPostgresQry.class.getName());
	@Override
	public PreparedStatement loadSlabDatesQry(Connection conn, String networkCode, int previousMonths, int forwardMonths)throws SQLException{
	int prevMonth=-previousMonths;
	int forwMonth=forwardMonths;
	StringBuilder strBuff = new StringBuilder("SELECT DISTINCT service_type,slab_date FROM SLAB_MASTER WHERE network_code=?");
    strBuff.append(" AND to_date('01'|| '/' || to_char(slab_date,'mm/yy'),'dd/mm/yy')>date_trunc('day',?::TIMESTAMP) + interval "); 
    strBuff.append(" ' " + prevMonth);
        strBuff.append(" month' ");
    strBuff.append(" AND to_date('01'|| '/' || to_char(slab_date,'mm/yy'),'dd/mm/yy')<date_trunc('day',?::TIMESTAMP) + interval ");
    strBuff.append(" ' " + forwMonth);
    strBuff.append(" month' ");
    
    LogFactory.printLog("SlabGeneratorPostgresQry", "Select Query= " + strBuff.toString(), log);
    PreparedStatement  pstmtSelect = conn.prepareStatement(strBuff.toString());
    pstmtSelect.setString(1, networkCode);
    Date currDate = new Date();
    pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(currDate));
    pstmtSelect.setDate(3, BTSLUtil.getSQLDateFromUtilDate(currDate));
    return pstmtSelect;
	}
}
