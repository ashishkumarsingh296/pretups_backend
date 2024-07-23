package com.btsl.pretups.common;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;

public class CSVReportReader implements Runnable {
	
	private BlockingQueue<String> queue;
	private PreparedStatement preparedStatement;
	private MutableBoolean mutableBoolean;
	private String[] resultSetColumnName;
	private Log log = LogFactory.getLog(this.getClass().getName());
	private String parentMsisdn;

	public CSVReportReader(BlockingQueue<String> queue, PreparedStatement preparedStatement, MutableBoolean mutableBoolean,String[] resultSetColumnName) {
		super();
		this.queue = queue;
		this.preparedStatement = preparedStatement;
		this.mutableBoolean = mutableBoolean;
		this.resultSetColumnName = resultSetColumnName;
	}

	@Override
	public void run() {
		final String methodName="run";
		ResultSet rs = null;
		try {
			Connection con = null;
	        MComConnectionI mcomCon = null;
			 log.debug(methodName, "Started Reading..................");
			rs = preparedStatement.executeQuery();
			StringBuilder row=new StringBuilder();
			while (rs.next()) {
				for(String columnName : resultSetColumnName){
					if (row.length() > 0) {
						row.append(",");
					}
					if(columnName.equals("parent_id"))
					{
						if(rs.getString(columnName) == null)
						{
							row.append("");
						}
						else
						{
							if(rs.getString(columnName).equals("ROOT"))
							{
								row.append("NA");
							}
							else
							{
								row.append(rs.getString(columnName));
								 mcomCon = new MComConnection();
								 con=mcomCon.getConnection();   
								 
								parentMsisdn = new ChannelUserDAO().loadParentMsisdn(con, rs.getString(columnName));								
							}
							
						}
						
					}
					else if("Parent_USErs_MSISDN".equals(columnName))
					{
						row.append(parentMsisdn != null ? parentMsisdn : "NA");

					}
					else
					{
						row.append(rs.getString(columnName) != null ? rs.getString(columnName).trim() : "");
					}
				}
			    queue.put(row.toString().trim());
			    row.setLength(0);
				
			}
			
			preparedStatement.close();
			this.mutableBoolean.setValue(true);
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			
		}finally{
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
		}
	}

}
