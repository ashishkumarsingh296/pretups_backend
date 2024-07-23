package com.btsl.common;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.voms.util.DESedeEncryption;
import com.btsl.voms.util.VomsUtil;

public class Reader implements Runnable {
	
	private BlockingQueue<String> queue;
	private PreparedStatement preparedStatement;
	private MutableBoolean mutableBoolean;
	private String decKey;
	private Log log = LogFactory.getLog(this.getClass().getName());

	public Reader(BlockingQueue<String> queue, PreparedStatement preparedStatement, MutableBoolean mutableBoolean, String decKey) {
		super();
		this.queue = queue;
		this.preparedStatement = preparedStatement;
		this.mutableBoolean = mutableBoolean;
		this.decKey = decKey;
	}

	@Override
	public void run() {
		final String methodName="run";
		ResultSet rs = null;
		try {
			 log.debug(methodName, "Started Reading..................");
			rs = preparedStatement.executeQuery();
			DESedeEncryption dESedeEncryption = new DESedeEncryption(decKey);
			while (rs.next()) {
				String[] array = rs.getString("voucher").split(",");
				VomsUtil utilI = new VomsUtil();
				array[0] = VomsUtil.decryptText(array[0]);
				array[0] = dESedeEncryption.encrypt(array[0]);
			    queue.put(String.join(",", array));
			}
			
			preparedStatement.close();
			this.mutableBoolean.setValue(true);
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			
		} finally{
			try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
		}
	}

}
