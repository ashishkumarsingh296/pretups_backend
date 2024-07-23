package com.btsl.pretups.common;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class CSVTotalC2SReportReader implements Runnable {
	
	private BlockingQueue<String> queue;
	private PreparedStatement preparedStatement;
	private MutableBoolean mutableBoolean;
	private String[] resultSetColumnName;
	private int totalRC=0;
	private int totalVAS=0;
	
	private Log log = LogFactory.getLog(this.getClass().getName());

	public CSVTotalC2SReportReader(BlockingQueue<String> queue, PreparedStatement preparedStatement, MutableBoolean mutableBoolean,String[] resultSetColumnName) {
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
			 log.debug(methodName, "Started Reading..................");
			rs = preparedStatement.executeQuery();
			StringBuilder row=new StringBuilder();
			while (rs.next()) {
				for(String columnName : resultSetColumnName){
					if (row.length() > 0) {
						row.append(",");
					}
					row.append(rs.getString(columnName) != null ? rs.getString(columnName).trim() : "");
					
					if (columnName.equalsIgnoreCase("RECHARGE"))
					{totalRC=totalRC+rs.getInt("RECHARGE");}
					if (columnName.equalsIgnoreCase("BUNDLE"))
					{totalVAS=totalVAS+rs.getInt("BUNDLE");}
				}
			    queue.put(row.toString().trim());
			    row.setLength(0);
				
			}
			
		    row.setLength(0);
			row.append("TOTAL:");
			row.append(",");
			row.append(totalRC);
			row.append(",");
			row.append(totalVAS);
			queue.put(row.toString().trim());
		    row.setLength(0);
			
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
