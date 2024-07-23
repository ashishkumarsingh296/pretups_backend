package com.btsl.pretups.common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class CSVEtopReportReader implements Runnable {

	private BlockingQueue<String> queue;
	private PreparedStatement preparedStatement;
	private MutableBoolean mutableBoolean;
	private String[] resultSetColumnName;
	private Log log = LogFactory.getLog(this.getClass().getName());

	public CSVEtopReportReader(BlockingQueue<String> queue, PreparedStatement preparedStatement, MutableBoolean mutableBoolean,String[] resultSetColumnName) {
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
		double totalamnt =0;
		double discount_amount=0;
		double extra_charge_amount=0;
		double amount_after_discount=0;
		double amount_after_extra_charge=0;
		double amount_paid=0;
		
		
		try {
			 log.debug(methodName, "Started Reading..................");
			 rs = preparedStatement.executeQuery();
			 
			 
			StringBuilder row=new StringBuilder();
			while (rs.next()) {
				
				for(String columnName : resultSetColumnName){
					if (row.length() > 0) {
						row.append(",");
					}
					
					if(columnName.equalsIgnoreCase("total_amount"))
					{
						totalamnt=0;
						totalamnt= Double.parseDouble(rs.getString("unit_amount"))*Double.parseDouble(rs.getString("unit_price"));
						row.append(totalamnt);
					}
					else if(columnName.equalsIgnoreCase("discount_amount"))
					{
						discount_amount=0;
						discount_amount= Double.parseDouble(rs.getString("discount"))*totalamnt;
						row.append(discount_amount);
						
					}
					else if(columnName.equalsIgnoreCase("extra_charge_amount"))
					{
						extra_charge_amount=0;
						extra_charge_amount= Double.parseDouble(rs.getString("extra_charge"))*totalamnt;
						row.append(extra_charge_amount);
						
					}
					else if(columnName.equalsIgnoreCase("amount_after_discount"))
					{
						amount_after_discount=0;
						amount_after_discount=totalamnt+discount_amount;
						row.append(amount_after_discount);
						
					}
					else if(columnName.equalsIgnoreCase("amount_after_extra_charge"))
					{
						amount_after_extra_charge=0;
						amount_after_extra_charge=totalamnt+extra_charge_amount;
						row.append(amount_after_extra_charge);
						
					}
					else if(columnName.equalsIgnoreCase("amount_paid"))
					{
						amount_paid=0;
						amount_paid=Double.parseDouble(rs.getString("tax"))+amount_after_discount;
						row.append(amount_paid);
						
					}
					else
					row.append(rs.getString(columnName) != null ? rs.getString(columnName).trim() : "");
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
