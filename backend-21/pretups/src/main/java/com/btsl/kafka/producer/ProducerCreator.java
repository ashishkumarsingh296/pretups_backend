package com.btsl.kafka.producer;

/*  	Here, we are listing the Kafka Producer API�s main configuration settings: 
 * 	a. client.id
 *	It identifies producer application.
 * 	b. producer.type
 * 	Either sync or async.
 * 	c. acks
 * 	Basically, it controls the criteria for producer requests that are considered complete. 
 * 	d. retries
 * 	�Retries� means if somehow producer request fails, then automatically retry with the specific value. 
 * 	e. bootstrap.servers
 * 	It bootstraps list of brokers. 
 * 	f. linger.ms
 * 	Basically, we can set linger.ms to something greater than some value, if we want to reduce the number of requests. 
 * 	g. key.serializer
 * 	It is a key for the serializer interface. 
 * 	h. value.serializer
 * 	A value for the serializer interface.
 * 	i. batch.size
 * 	Simply, Buffer size. 
 * 	j. buffer.memory
 * 	�buffer.memory� controls the total amount of memory available to the producer for buffering.
 */

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.btsl.kafka.constants.IKafkaConstants;
import com.btsl.util.Constants;
public class ProducerCreator {
	
	private ProducerCreator() {
		// TODO Auto-generated constructor stub
	}
    public static Producer<Long, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.getKafkaProperty("bootstrap.servers"));//IKafkaConstants.KAFKA_BROKERS
        props.put(ProducerConfig.CLIENT_ID_CONFIG, IKafkaConstants.CLIENT_ID);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.LINGER_MS_CONFIG,0);
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,1048576);  //50826 bytes - for recharge insert
        props.put(ProducerConfig.BATCH_SIZE_CONFIG,0);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG,33554432);
        props.put("producer.type","sync");
        props.put("acks", "all");
        //props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());
        return new KafkaProducer<>(props);
    }
}