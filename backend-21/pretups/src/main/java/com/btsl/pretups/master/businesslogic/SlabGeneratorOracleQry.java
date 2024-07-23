package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class SlabGeneratorOracleQry implements SlabGeneratorQry {
	private Log log = LogFactory.getFactory().getInstance(SlabGeneratorOracleQry.class.getName());
	@Override
	public PreparedStatement loadSlabDatesQry(Connection conn, String networkCode, int previousMonths, int forwardMonths)throws SQLException{
	StringBuilder strBuff = new StringBuilder("SELECT DISTINCT service_type,slab_date FROM SLAB_MASTER WHERE network_code=?");
    strBuff.append(" AND to_date('01'||to_char(slab_date,'mm/yy'),'dd/mm/yy')>add_months(trunc(?),?)");
    strBuff.append(" AND to_date('01'||to_char(slab_date,'mm/yy'),'dd/mm/yy')<add_months(trunc(?),?)");
    
    LogFactory.printLog("SlabGeneratorOracleQry", "Select Query= " + strBuff.toString(), log);
    PreparedStatement  pstmtSelect = conn.prepareStatement(strBuff.toString());
    pstmtSelect.setString(1, networkCode);
    Date currDate = new Date();
    pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(currDate));
    pstmtSelect.setInt(3, -previousMonths);
    pstmtSelect.setDate(4, BTSLUtil.getSQLDateFromUtilDate(currDate));
    pstmtSelect.setInt(5, forwardMonths);
    return pstmtSelect;
	}
}
