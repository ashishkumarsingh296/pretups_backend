package com.btsl.pretups.gateway.businesslogic;


import java.util.HashMap;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.Constants;

public class PublishtoNotificationQueue {

	private static Log _log = LogFactory.getLog(PublishtoNotificationQueue.class.getName());
	
	static final String queueNameNotificationRequest = "notification.delivery.request";

	static CachingConnectionFactory cachingConnectionFactory = null;

	static RabbitTemplate rabbitTemplate = null;
	
	static ConnectionFactory connectionFactoryPub() {
		
		if(cachingConnectionFactory == null) {
		_log.debug("connectionFactoryPub", "Initializing cachingConnectionFactory");
		cachingConnectionFactory = new CachingConnectionFactory(Constants.getProperty("NOTIFICATION.SERVER.IP"), Integer.parseInt(Constants.getProperty("NOTIFICATION.SERVER.PORT")));
		cachingConnectionFactory.setUsername("pretups");
		cachingConnectionFactory.setPassword("pretups");
		rabbitTemplate = new RabbitTemplate(connectionFactoryPub());
		}
		
		return cachingConnectionFactory;
	}

	public static void publishNotification(String message, String toWhom) throws InterruptedException {

		try {

			connectionFactoryPub();
			String bodyStr = "{ \"orderId\": \"161824-219170-780570\", \"mfsTenantLocale\": \"en_US\", \"language\": \"en\", \"mfsTenantTZ\": \"GMT\", \"eventName\": \"notification\", \"mfsTenantId\": \"mfsPrimaryTenant\", \"sender\": { \"idType\": \"mobileNumber\", \"idValue\": \""+toWhom+"\", \"mobileNumber\": \""+toWhom+"\", \"preferredLanguage\":\"1\", \"smsText\":\""+message+"\", \"notificationEndpoints\": { \"SMS\": { \"accounts\": [\"20209650252\", \"1112233\"] }, \"EMAIL\": { \"accounts\": [\"akhilesh.mittal1@comviva.com\"] } } }, \"mfsTrackingData\": { \"addTrackingData\": false, \"trackingId\": \"ebc9a735-fdef-44d1-a2f4-ec50ac1c8b22\", \"startTime\": 1618242191696 }, \"serviceCode\": \"PRETUPS\", \"systemPreferredLanguage\": \"1\", \"isOrderManagementEnabled\": \"true\" }";

			_log.debug("publishNotification", "Payload :: "+bodyStr);
			
			
			MessageProperties props = new MessageProperties();

			HashMap<String, Object> map = new HashMap<String, Object>();

			Long ll = 120000L;

			map.put("x-message-ttl", ll);
			map.put("x-dead-letter-exchange", "mobiquity.money.dead.letter");
			map.put("x-dead-letter-routing-key", "low.priority.dead.service.request");

			props.setContentType(MessageProperties.CONTENT_TYPE_JSON);

			Message replyMessage = rabbitTemplate.getMessageConverter().toMessage(bodyStr, props);

			replyMessage.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);

			Queue queue = new Queue(queueNameNotificationRequest, true, false, false, map);

			_log.debug("publishNotification", "Publishing to notification queue");
			rabbitTemplate.convertAndSend(queue.getName(), replyMessage);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}