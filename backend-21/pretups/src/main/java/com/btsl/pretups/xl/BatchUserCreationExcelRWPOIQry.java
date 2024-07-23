package com.btsl.pretups.xl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface BatchUserCreationExcelRWPOIQry {
	
	public PreparedStatement writeModifyExcelQry(Connection con,String categoryCode,String geographyCode , String userId ) throws SQLException;

}
