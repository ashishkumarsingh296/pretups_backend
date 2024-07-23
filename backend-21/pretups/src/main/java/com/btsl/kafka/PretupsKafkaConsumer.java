package com.btsl.kafka;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.QueryConstants;
import com.btsl.kafka.constants.IKafkaConstants;
import com.btsl.kafka.consumer.ConsumerCreator;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.adjustments.businesslogic.AdjustmentsDAO;
import com.btsl.pretups.adjustments.businesslogic.AdjustmentsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
public class PretupsKafkaConsumer implements Runnable{
	static int i = 0;
	private static Log _log = LogFactory.getLog(PretupsKafkaConsumer.class.getName());
	public static void main(String[] args) {
		try{
			Constants.load(args[0]);
			Constants.loadKafkaConf(args[1]);
			if (_log.isDebugEnabled()) {
                _log.debug("main", " Class Loaded ");
            }
			
		//	org.apache.log4j.PropertyConfigurator.configure(args[2]);
			String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
        	if (QueryConstants.DB_POSTGRESQL.equals(dbConnected))
        		QueryConstants.load(Constants.getProperty("PostgresQuerypath"));
        	else if(QueryConstants.DB_ORACLE.equals(dbConnected))
        		QueryConstants.load(Constants.getProperty("OracleQuerypath"));
        	NetworkCache.loadNetworkAtStartup();
        	PreferenceCache.loadPrefrencesOnStartUp();
        	NetworkPrefixCache.loadNetworkPrefixesAtStartup();
        	SystemPreferences.load();
        	
        	MComConnectionI mcomCon = new MComConnection();
            Connection con = mcomCon.getConnection();
            if(mcomCon != null)
			{
				mcomCon.close("ConfigServlet#init");
				mcomCon=null;
			}
			con = null;
        	
			PretupsKafkaConsumer pc1 = new PretupsKafkaConsumer(); 
			i=0;
			Thread t1 =new Thread(pc1);  
			t1.start();  
			/*Thread.sleep(2000);
			i++;
			Thread t2 =new Thread(pc1);  
			t2.start();  
			Thread.sleep(2000);
			i++;
			Thread t3 =new Thread(pc1);  
			t3.start();  */
		}catch(Exception e){
			_log.errorTrace("Main", e);
		}
	}
    
	public void run(){  
		if(i==0){try {
			addUpdateC2STransfers();
		} catch (Exception e) {
			
			_log.errorTrace("run", e);
		}}
		if(i==2){
			try {
				addAdjustments();
			} catch (Exception e) {
				_log.errorTrace("run", e);
			}
		}
	}  
	
    public void addUpdateC2STransfers() throws Exception {
    	Consumer<Long, String> consumer = null;
    	ObjectMapper mapper = null;
    	C2STransferVO c2sVO = null;
		MComConnectionI mcomCon = null;
		Connection con = null;
		
    	try{
    		mcomCon = new MComConnection();
    		con = mcomCon.getConnection();
    		consumer = ConsumerCreator.createConsumer();
    		consumer.subscribe(Collections.singletonList(IKafkaConstants.TOPIC_C2S_INSERT));
    		mapper = new ObjectMapper();
    		ConsumerRecords<Long, String> records = null;
    		int i = 0, noRecordsFetchedCount = 0;
    		HashMap<String, C2STransferVO> insertMap = new HashMap<String, C2STransferVO>();
    		HashMap<String, C2STransferVO> updateMap = new HashMap<String, C2STransferVO>();
    		while (true) {
    			records = consumer.poll(1000);
    			int sizeOfRecords = records.count();
    			if (_log.isDebugEnabled()) {
	                _log.debug("addUpdateC2STransfers", String.valueOf(sizeOfRecords));
	            }
    			if(sizeOfRecords <= 0)
    			{	
    				noRecordsFetchedCount++;
    				if(noRecordsFetchedCount == IKafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT)
    					break;
    			}
    			else
    				noRecordsFetchedCount = 0;
    			for (final ConsumerRecord<Long, String> record : records) {
    				try{
    					c2sVO = mapper.readValue(record.value(), C2STransferVO.class);
    					if("0".equals(c2sVO.getIsAddTransfer()))
    					{
    						insertMap.put(c2sVO.getTransferID(), c2sVO);
    					}
    					else if("1".equals(c2sVO.getIsAddTransfer()))
    					{
    						updateMap.put(c2sVO.getTransferID(), c2sVO);
    					}
    					i++;
    				}catch (JsonProcessingException e) {
    					throw e;
    				} catch (IOException e) {
    					throw e;
    				}
    			}
    			
    			if(i >= 400 || (noRecordsFetchedCount >= 2 && (insertMap.size() > 0  || updateMap.size() > 0))){
    				for (Entry<String, C2STransferVO> entry : updateMap.entrySet()) {
    					String transferID = entry.getKey();
    					C2STransferVO transferVO = entry.getValue();
    					if(insertMap.containsKey(transferID))
    					{
    						insertMap.remove(transferID);
    						if (_log.isDebugEnabled()) {
    			                _log.debug("addUpdateC2STransfers", " Update found in insertMap ");
    			            }
    						new C2STransferDAO().addC2STransferDetails(con, transferVO, true);
    					}
    					
    					else
    						new C2STransferDAO().updateC2STransferDetails(con, transferVO);
    				}
    				for (Entry<String, C2STransferVO> entry : insertMap.entrySet()) 
					{
    					String transferID = entry.getKey();
    					C2STransferVO transferVO = entry.getValue();
						new C2STransferDAO().addC2STransferDetails(con, transferVO, true);
					}
    				mcomCon.finalCommit();
    				consumer.commitSync();
    				i=0;
    				insertMap = new HashMap<String, C2STransferVO>();
    				updateMap = new HashMap<String, C2STransferVO>();
    				Thread.sleep(1);
    			}
    		}
    	}catch(Exception e){
    		try{mcomCon.partialRollback();}
    		catch(Exception ex){
    			_log.errorTrace("addUpdateC2STransfers", ex);
    		}
    	}finally{
    		if(mcomCon != null)
			{
				mcomCon.close("ConfigServlet#init");
				mcomCon=null;
			}
			con = null;
    		consumer.close();
    	}
    }
    
    static void addAdjustments() {
    	Consumer<Long, String> consumer = ConsumerCreator.createConsumer();
    	consumer.subscribe(Collections.singletonList(IKafkaConstants.TOPIC_C2S_ADJ));
    	ObjectMapper mapper = new ObjectMapper();
    	ArrayList<AdjustmentsVO> ar = null;
    	MComConnectionI mcomCon = null;
    	Connection con = null;
    	try{
    		mcomCon = new MComConnection();
            con = mcomCon.getConnection();
    		ConsumerRecords<Long, String> records = null;
    		int i =0;
    		while (true) {
    			records = consumer.poll(1000);
    			for (final ConsumerRecord<Long, String> record : records) {
    				try{
    					ar = mapper.readValue(record.value(), new TypeReference<ArrayList<AdjustmentsVO>>(){});
    					new AdjustmentsDAO().addAdjustmentEntries(con, ar, "asdfasfsdafdsf");
    					i++;
    				}catch (JsonProcessingException e) {
    					throw e;
    				} catch (IOException e) {
    					throw e;
    				} 
    			}
    			if(i == 200 ){
    				mcomCon.finalCommit();
    				consumer.commitSync();
    				i=0;
    			}
    			Thread.sleep(1);
    		}
    	}catch(Exception e){
    		_log.errorTrace("addAdjustments", e);
    		try{mcomCon.partialRollback();}catch(Exception ex){
    			_log.errorTrace("addAdjustments", ex);
    		}
    	}finally{
    		if(mcomCon != null)
			{
				mcomCon.close("ConfigServlet#init");
				mcomCon=null;
			}
			con = null;
    		consumer.close();
    	}
    }
    
}