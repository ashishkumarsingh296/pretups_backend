package com.btsl.kafka;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.kafka.constants.IKafkaConstants;
import com.btsl.kafka.producer.ProducerCreator;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.adjustments.businesslogic.AdjustmentsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferFilterVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class PretupsKafkaProducerBL {
	private final static Log _log = LogFactory.getLog(PretupsKafkaProducerBL.class.getName());
	private final static Producer<Long, String> producer = ProducerCreator.createProducer();
	private final static ObjectMapper mapper = new ObjectMapper();

	private PretupsKafkaProducerBL() {
		// TODO Auto-generated constructor stub
	}
	public static int c2sTransfersInsertProducer(C2STransferVO c2sVO) throws BTSLBaseException {
		final String methodName = "c2sTransfersInsertProducer";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,c2sVO.toString());
		}	
		String json = null;
		ProducerRecord<Long, String> record = null;
		RecordMetadata metadata = null;
		int count = 0;
		try{
			c2sVO.setIsAddTransfer("0");
			c2sVO.getRequestVO().setValueObject(null);
			json = mapper.writeValueAsString(c2sVO);
			record = new ProducerRecord<Long, String>(IKafkaConstants.TOPIC_C2S_INSERT, json);//+"-"+Constants.getProperty("INSTANCE_ID")
			metadata = producer.send(record).get();
			count++; 
		}catch (JsonProcessingException e) {
			_log.error(methodName, "JsonProcessingException : " + e);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[PretupsKafkaProducerBL[" + methodName + "]", "", "", "", "JsonProcessingException:" + e.getMessage());
            throw new BTSLBaseException(methodName, "error.general.kafka.processing");
		}catch (ExecutionException e) {
			_log.error(methodName, "ExecutionException : " + e);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[PretupsKafkaProducerBL[" + methodName + "]", "", "", "", "ExecutionException:" + e.getMessage());
            throw new BTSLBaseException(methodName, "error.general.kafka.processing");
		}catch (InterruptedException e) {
			_log.error(methodName, "InterruptedException : " + e);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[PretupsKafkaProducerBL[" + methodName + "]", "", "", "", "InterruptedException:" + e.getMessage());
            throw new BTSLBaseException(methodName, "error.general.kafka.processing");
		}finally {
			producer.flush();
            //producer.close();
        }
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,count);
		}	
		return count;
	}
	
	public static int c2sTransfersUpdateProducer(C2STransferVO c2sVO) throws BTSLBaseException {
		final String methodName = "c2sTransfersUpdateProducer";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,c2sVO.toString());
		}	
		String json = null;
		int count = 0;
		try {
			c2sVO.setIsAddTransfer("1");
			json = mapper.writeValueAsString(c2sVO);
			ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(IKafkaConstants.TOPIC_C2S_INSERT, json);//+"-"+Constants.getProperty("INSTANCE_ID")
			RecordMetadata metadata = null;
			metadata = producer.send(record).get();
			count++;
		}catch (JsonProcessingException e) {
			_log.error(methodName, "JsonProcessingException : " + e);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[PretupsKafkaProducerBL[" + methodName + "]", "", "", "", "JsonProcessingException:" + e.getMessage());
            throw new BTSLBaseException(methodName, "error.general.kafka.processing");
		}catch (ExecutionException e) {
			_log.error(methodName, "ExecutionException : " + e);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[PretupsKafkaProducerBL[" + methodName + "]", "", "", "", "ExecutionException:" + e.getMessage());
            throw new BTSLBaseException(methodName, "error.general.kafka.processing");
		}catch (InterruptedException e) {
			_log.error(methodName, "InterruptedException : " + e);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[PretupsKafkaProducerBL[" + methodName + "]", "", "", "", "InterruptedException:" + e.getMessage());
            throw new BTSLBaseException(methodName, "error.general.kafka.processing");
		}finally{
			producer.flush();
			//producer.close();
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,count);
		}	
		return count;
	}
	
	public static int adjustmentProducer(List<AdjustmentsVO> itemList) throws BTSLBaseException {
		final String methodName = "adjustmentProducer";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,itemList.size());
		}	
		RecordMetadata metadata = null;
		String json = null;
		ProducerRecord<Long, String> record = null;
		int count = 0;
		try{
			if (itemList != null && !itemList.isEmpty()) {
				json = mapper.writeValueAsString(itemList);//json = new Gson().toJson(adjustmentsVO);
				record = new ProducerRecord<Long, String>(IKafkaConstants.TOPIC_C2S_ADJ, json);
				metadata = producer.send(record).get();
				count = itemList.size();
			} 
		}catch (JsonProcessingException e) {
			_log.error(methodName, "JsonProcessingException : " + e);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[PretupsKafkaProducerBL[" + methodName + "]", "", "", "", "JsonProcessingException:" + e.getMessage());
            throw new BTSLBaseException(methodName, "error.general.kafka.processing");
		}catch (ExecutionException e) {
			_log.error(methodName, "ExecutionException : " + e);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[PretupsKafkaProducerBL[" + methodName + "]", "", "", "", "ExecutionException:" + e.getMessage());
            throw new BTSLBaseException(methodName, "error.general.kafka.processing");
		}catch (InterruptedException e) {
			_log.error(methodName, "InterruptedException : " + e);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[PretupsKafkaProducerBL[" + methodName + "]", "", "", "", "InterruptedException:" + e.getMessage());
            throw new BTSLBaseException(methodName, "error.general.kafka.processing");
		}finally{
			producer.flush();
			//producer.close();
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,count);
		}	
		return count;
	}
	
	
	public static int c2sTransfersFilterProducer( ArrayList<C2STransferFilterVO> c2sFilterVOList) throws BTSLBaseException {
		final String methodName = "c2sTransfersFilterProducer";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,c2sFilterVOList.size());
		}	
		int count = 0;
		String json = null;
		Producer<Long, String> producer = null;
		RecordMetadata metadata = null;
		C2STransferFilterVO c2sFilterVO = null;
		ProducerRecord<Long, String> record = null;
		ObjectMapper mapper = null;
		try {
			if (c2sFilterVOList != null && !c2sFilterVOList.isEmpty()) {
				producer = ProducerCreator.createProducer();
				mapper = new ObjectMapper();
				int size = c2sFilterVOList.size();
				for (int j = 0; j < size; j++) {
					c2sFilterVO = (C2STransferFilterVO) c2sFilterVOList.get(j);
					json = mapper.writeValueAsString(c2sFilterVO);
					record = new ProducerRecord<Long, String>(IKafkaConstants.TOPIC_C2S_FILTER, json);
					metadata = producer.send(record).get();
					c2sFilterVO.setOffset(metadata.offset());
					count++;
					System.out.println("c2sTransfersProducer Record sent with key " + metadata.partition() + " with offset " + metadata.offset());
				}
			}
		}catch (JsonProcessingException e) {
			_log.error(methodName, "JsonProcessingException : " + e);
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[PretupsKafkaProducerBL[" + methodName + "]", "", "", "", "JsonProcessingException:" + e.getMessage());
			throw new BTSLBaseException(methodName, "error.general.kafka.processing");
		}catch (ExecutionException e) {
			_log.error(methodName, "ExecutionException : " + e);
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[PretupsKafkaProducerBL[" + methodName + "]", "", "", "", "ExecutionException:" + e.getMessage());
			throw new BTSLBaseException(methodName, "error.general.kafka.processing");
		}catch (InterruptedException e) {
			_log.error(methodName, "InterruptedException : " + e);
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[PretupsKafkaProducerBL[" + methodName + "]", "", "", "", "InterruptedException:" + e.getMessage());
			throw new BTSLBaseException(methodName, "error.general.kafka.processing");
		}finally{
			producer.flush();
			//producer.close();
		}

		if (_log.isDebugEnabled()) {
			_log.debug(methodName,count);
		}	
		return count;
	}
	
}