package com.classes;

import java.text.MessageFormat;

import com.utils.Log;
import com.utils._masterVO;

public class MessagesDAO extends BaseTest{

	public static String prepareMessageByKey(String MessageKey, String... ParserValues) {
		Log.info("Trying to fetch Message using the Key: " + MessageKey);
		String RawMessage = _masterVO.getMessage(MessageKey);
		Log.info("Message fetched as: " + RawMessage);
		Log.info("Preparing Expected Message");
		String PreparedMessage =null;
		if(RawMessage!=null) {
			PreparedMessage= MessageFormat.format(RawMessage.replace("'", "''"), (Object[])ParserValues);
		}
		Log.info("<b>Prepared Message: </b>" + PreparedMessage);
		return PreparedMessage;
	}
	
	public static String getLabelByKey(String LabelKey) {
		return _masterVO.getMessage(LabelKey);
	}
	
	public static String prepareC2SMessageByKey(String MessageKey, String... ParserValues) {
		Log.info("Trying to fetch Message using the Key: " + MessageKey);
		String RawMessage = _masterVO.getC2SMessage(MessageKey);
		Log.info("Message fetched as: " + RawMessage);
		Log.info("Preparing Expected Message");
		String PreparedMessage = MessageFormat.format(RawMessage, (Object[])ParserValues).replace("mclass^2&pid^61:", "");
		Log.info("<b>Prepared Message: </b>" + PreparedMessage);
		return PreparedMessage;
	}
	
	public static String getC2SLabelByKey(String LabelKey) {
		return _masterVO.getC2SMessage(LabelKey).replace("mclass^2&pid^61:","");
	}

}
