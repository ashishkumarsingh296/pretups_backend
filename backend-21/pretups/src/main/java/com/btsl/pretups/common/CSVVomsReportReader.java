package com.btsl.pretups.common;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLDateUtil;

public class CSVVomsReportReader implements Runnable {
	
	private BlockingQueue<String> queue;
	private PreparedStatement preparedStatement;
	private MutableBoolean mutableBoolean;
	private String[] resultSetColumnName;
	private int resultSumCount;
	private Log log = LogFactory.getLog(this.getClass().getName());

	public CSVVomsReportReader(BlockingQueue<String> queue, PreparedStatement preparedStatement, MutableBoolean mutableBoolean,String[] resultSetColumnName,int resultSumCount) {
		super();
		this.queue = queue;
		this.preparedStatement = preparedStatement;
		this.mutableBoolean = mutableBoolean;
		this.resultSetColumnName = resultSetColumnName;
		this.resultSumCount=resultSumCount;
	}

	@Override
	public void run() {
		final String methodName="run";
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		try {
			 log.debug(methodName, "Started Reading..................");
			 
			 if(resultSumCount==0) {
				 rs = preparedStatement.executeQuery();
					StringBuilder row=new StringBuilder();
					
					while (rs.next()) {
						for(int i=1;i<=resultSetColumnName.length;i++) {
							if (row.length() > 0) {
								row.append(",");
							}
							
							boolean numeric = true;
							boolean date = false;
							if(rs.getString(i) != null) {
								numeric = rs.getString(i).matches("-?\\d+(\\.\\d+)?");
								date = rs.getString(i).matches("\\d{4}-\\d{2}-\\d{2}.*");						
								if(numeric && rs.getString(i).length() >10) {
									row.append("'"+rs.getString(i)+"'");
								}else if(date && rs.getString(i).length() >10) {
									String str=rs.getString(i).substring(0, 10);
									row.append(BTSLDateUtil.getSystemLocaleDate(str,"yyyy/mm/dd"));
								}
								else
									row.append(rs.getString(i));
							}else
							{
								row.append("");
							}
						}				
					    queue.put(row.toString().trim());
					    row.setLength(0);
						
					}			
					preparedStatement.close();
				 
			 }else if(resultSumCount==1) {
				 rs1 = preparedStatement.executeQuery();
					StringBuilder row=new StringBuilder();
					int[] resultCount=new int[resultSetColumnName.length-1];
					for(int k=0;k<resultCount.length;k++) {
						resultCount[k]=0;						
					}
					while (rs1.next()) {
						for(int i=1;i<=resultSetColumnName.length;i++) {
							if (row.length() > 0) {
								row.append(",");
							}
							
							boolean numeric = true;
							boolean date = false;
							if(rs1.getString(i) != null) {
								if(i>1) {
									resultCount[i-2]=resultCount[i-2]+rs1.getInt(i);									
								}
								numeric = rs1.getString(i).matches("-?\\d+(\\.\\d+)?");
								date = rs1.getString(i).matches("\\d{4}-\\d{2}-\\d{2}.*");						
								if(numeric && rs1.getString(i).length() >10) {
									row.append("'"+rs1.getString(i)+"'");
								}else if(date && rs1.getString(i).length() >10) {
									String str=rs1.getString(i).substring(0, 10);
									row.append(BTSLDateUtil.getSystemLocaleDate(str,"yyyy/mm/dd"));
								}
								else
									row.append(rs1.getString(i));
							}else
							{
								row.append("");
							}
						}				
					    queue.put(row.toString().trim());
					    row.setLength(0);
						
					}			
					preparedStatement.close();
					
					queue.put(row.toString().trim());
					// to append the Count Row
					row.append("COUNT ");
					for(int i=0;i<resultCount.length;i++) {
						row.append(","+resultCount[i]);
					}
					 queue.put(row.toString().trim());
					 row.setLength(0);
				 
			 }else if(resultSumCount==2) {
				 rs2 = preparedStatement.executeQuery();
					StringBuilder row=new StringBuilder();
					int[] resultCount=new int[resultSetColumnName.length-1];
					double[] resultSum=new double[resultSetColumnName.length-1];
					for(int k=0;k<resultCount.length;k++) {
						resultCount[k]=0;
						resultSum[k]=0;
					}
					while (rs2.next()) {
						for(int i=1;i<=resultSetColumnName.length;i++) {
							if (row.length() > 0) {
								row.append(",");
							}
							
							boolean numeric = true;
							boolean date = false;
							if(rs2.getString(i) != null) {
								if(i>1) {
									resultCount[i-2]=resultCount[i-2]+rs2.getInt(i);									
								}
								numeric = rs2.getString(i).matches("-?\\d+(\\.\\d+)?");
								date = rs2.getString(i).matches("\\d{4}-\\d{2}-\\d{2}.*");						
								if(numeric && rs2.getString(i).length() >10) {
									row.append("'"+rs2.getString(i)+"'");
								}else if(date && rs2.getString(i).length() >10) {
									String str=rs2.getString(i).substring(0, 10);
									row.append(BTSLDateUtil.getSystemLocaleDate(str,"yyyy/mm/dd"));
								}
								else
									row.append(rs2.getString(i));
							}else
							{
								row.append("");
							}
						}				
					    queue.put(row.toString().trim());
					    row.setLength(0);
						
					}			
					preparedStatement.close();
					
					 queue.put(row.toString().trim());
					// to append the Count Row
					row.append("COUNT ");
					for(int i=0;i<resultCount.length;i++) {
						row.append(","+resultCount[i]);
					}
					 queue.put(row.toString().trim());
					 row.setLength(0);
					 queue.put(row.toString().trim());
					// to append the Sum Row
						row.append("SUM ");
						for(int i=0;i<resultCount.length;i++) {
							
							row.append(","+resultCount[i]*Double.parseDouble(resultSetColumnName[i+1]));
						}
						 queue.put(row.toString().trim());
						 row.setLength(0);
			 }			
			
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
			try {
                if (rs1 != null) {
                	rs1.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
			try {
                if (rs2 != null) {
                	rs2.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
		}
	}

}
