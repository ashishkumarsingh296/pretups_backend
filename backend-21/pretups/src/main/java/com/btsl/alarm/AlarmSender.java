package com.btsl.alarm;

import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Locale;

import com.btsl.event.EventHandler;
import com.btsl.event.EventVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author ayush.abhijeet
 */
public class AlarmSender extends Thread {

    private static Log _log = LogFactory.getLog(AlarmSender.class.getName());
    private boolean _running = true;
    private Hashtable _alarmHash = null;
    private EventVO _newEventVO = null;
    private EventVO _oldEventVO = null;
    private String _key = null;
    private long _sleepTime = 0;
    private long _maxHoldTime = 0;
    private String _alerCountMessage = null;
    private String _eventLevel = null;
    private String _eventId = null;
    private String _msisdnString = null;
    private Locale _locale = null;
    private String _langCountry = null;
    private String _instanceId = null;
    public void SetRunningStatus(boolean b)
    {
    	_running =b;	
    	
    }
    public boolean GetRunningStatus()
    {
    	return _running ;	
    	
    }
    
    public AlarmSender() {
        if (_log.isDebugEnabled())
            _log.debug("default contructor", "Entered");
        _alarmHash = new Hashtable();
        refresh();
        if (_log.isDebugEnabled())
            _log.debug("default contructor", "Exiting");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new AlarmSender().send(args[0], Integer.parseInt(args[1]), args[2], args[4]);
    }

    /**
     * This method is used to send alarm,
     * 
     * @param p_componentName
     *            - Name of the component
     * @param p_alarmState
     *            - Alarm state
     * @param messageString
     *            - Alarm message
     *            return void
     */
    public void send(String p_componentName, int p_alarmID, String p_alarmState, String messageString) {
        (new AlarmManager(p_componentName, p_alarmID, p_alarmState, messageString)).start();
    }

    /**
	 * 
	 *
	 */
    public void refresh() {
        final String METHOD_NAME = "refresh";
        if (_log.isDebugEnabled())
            _log.debug("refresh", "Entered");
        try {
            loggingInstanceId(METHOD_NAME);

            loggingAlarmSleepTime(METHOD_NAME);

            loggingAlarmCountMessage(METHOD_NAME);

            loggingAlarmMaxHoldTime(METHOD_NAME);

            // Added for monitoring PreTUPS by SMS by Vipul on 05/12/07
            loggingAdminMobile(METHOD_NAME);

            loggingSmsEventLevels(METHOD_NAME);

            loggingSmsEventIds(METHOD_NAME);

            try {
                _langCountry = new String(Constants.getProperty("ALARM_DEFAULT_LOCALE"));
                if (_log.isDebugEnabled())
                    _log.debug("refresh", "_langCountry: " + _langCountry);
                // locale from Constant.props is en,us; so splitting and using
                // separately to create locale
                _locale = new Locale(_langCountry.split(",")[0], _langCountry.split(",")[1]);
            } catch (Exception e8) {
                _log.errorTrace(METHOD_NAME, e8);
                _log.error("refresh", "ALARM_DEFAULT_LOCALE is not defined in Constant.props, setting default as en_us");
                _locale = new Locale("en", "US");
            }

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled())
            _log.debug("refresh", "Exiting ");
    }
	private void loggingSmsEventIds(final String METHOD_NAME) {
		try {
		    _eventId = new String(Constants.getProperty("SMS_EVENT_IDS"));
		    if (_log.isDebugEnabled())
		        _log.debug("refresh", "_eventId: " + _eventId);
		} catch (Exception e7) {
		    _log.errorTrace(METHOD_NAME, e7);
		    _log.error("refresh", "SMS_EVENT_IDS is not defined in Constant.props, setting default as 1,2,14");
		}
	}
	private void loggingSmsEventLevels(final String METHOD_NAME) {
		try {
		    _eventLevel = new String(Constants.getProperty("SMS_EVENT_LEVELS"));
		    if (_log.isDebugEnabled())
		        _log.debug("refresh", "_eventLevel: " + _eventLevel);
		} catch (Exception e6) {
		    _log.errorTrace(METHOD_NAME, e6);
		    _log.error("refresh", "SMS_EVENT_LEVELS is not defined in Constant.props, setting default as 1,2");
		    _eventLevel = "1,2";
		}
	}
	private void loggingAdminMobile(final String METHOD_NAME) {
		try {
		    _msisdnString = new String(Constants.getProperty("adminmobile"));
		    if (_log.isDebugEnabled())
		        _log.debug("refresh", "_msisdnString: " + _msisdnString);
		} catch (Exception e5) {
		    _log.errorTrace(METHOD_NAME, e5);
		    _log.error("refresh", "adminmobile is not defined in Constant.props, cannot use default");
		}
	}
	private void loggingAlarmMaxHoldTime(final String METHOD_NAME) {
		try {
		    _maxHoldTime = Long.parseLong(Constants.getProperty("ALARM_MAX_HOLD_TIME"));
		    if (_log.isDebugEnabled())
		        _log.debug("refresh", "_maxHoldTime: " + _maxHoldTime);
		} catch (Exception e4) {
		    _log.errorTrace(METHOD_NAME, e4);
		    _log.error("refresh", "ALARM_MAX_HOLD_TIME is not defined in Constant.props, setting default as 5000");
		    _maxHoldTime = 5000;
		}
	}
	private void loggingAlarmCountMessage(final String METHOD_NAME) {
		try {
		    _alerCountMessage = Constants.getProperty("ALARM_COUNT_MESSAGE");
		    if (_log.isDebugEnabled())
		        _log.debug("refresh", "_alerCountMessage: " + _alerCountMessage);
		} catch (Exception e3) {
		    _log.errorTrace(METHOD_NAME, e3);
		    _log.error("refresh", "ALARM_COUNT_MESSAGE is not defined in Constant.props, setting default");
		    _alerCountMessage = " ( {0} similar case(s) have happened in last {1} seconds )";
		}
	}
	private void loggingAlarmSleepTime(final String METHOD_NAME) {
		try {
		    _sleepTime = Long.parseLong(Constants.getProperty("ALARM_SLEEP_TIME"));
		    if (_log.isDebugEnabled())
		        _log.debug("refresh", "_sleepTime: " + _sleepTime);
		} catch (Exception e2) {
		    _log.errorTrace(METHOD_NAME, e2);
		    _log.error("refresh", "ALARM_SLEEP_TIME is not defined in Constant.props, setting default as 1000");
		    _sleepTime = 1000;
		}
	}
	private void loggingInstanceId(final String METHOD_NAME) {
		try {
		    _instanceId = Constants.getProperty("INSTANCE_ID");
		    if (_log.isDebugEnabled())
		        _log.debug("refresh", "_instanceId: " + _instanceId);
		} catch (Exception e1) {
		    _log.errorTrace(METHOD_NAME, e1);
		    _log.error("refresh", "INSTANCE_ID is not defined in Constant.props, cannot use default");
		}
	}
@Override
    public void run() {
        final String METHOD_NAME = "run";
        if (_log.isDebugEnabled())
            _log.debug("run", "Entered");
        long generatedOn = 0;
        long currentTime = 0;
        int count ;
        String message ;
        long timeDiff ;
        StringBuilder keyvalue=new StringBuilder("");
        StringBuilder msg=new StringBuilder("");    	
        while (_running) {
            generatedOn = 0;
            count = 0;
            try {
                // if have to think of logic for initilising following parameter
                // during startup and only loading when there is change
                Thread.sleep(_sleepTime);
                try {
                    // retrive alarm from message queue made bu event handler.if
                    // no alarm found just continue after sleep in exception
                	if(!EventHandler._eventList.isEmpty())
                    _newEventVO = (EventVO) EventHandler._eventList.remove(0);
                	
                    if (_newEventVO != null) {
                    	keyvalue.setLength(0);
                        keyvalue.append(_newEventVO.getComponentName());
                        keyvalue.append("|");
                        keyvalue.append( _newEventVO.getEventID());
                        keyvalue.append("|");
                        keyvalue.append(_newEventVO.getProcessName());
                        _key = keyvalue.toString();
                        if (_log.isDebugEnabled())
                            _log.debug("run", "_key: " + _key);
                        // If alarm hash contains this alarm the check for its
                        // valididty for sending to O & M
                        if (_alarmHash.containsKey(_key)) {
                            _oldEventVO = (EventVO) _alarmHash.get(_key);
                            generatedOn = _oldEventVO.getGeneratedOn();
                            currentTime = System.currentTimeMillis();
                            count = _oldEventVO.getAlarmCount();
                            timeDiff = currentTime - generatedOn;
                            if (_log.isDebugEnabled())
                            {
                            	msg.setLength(0);                            	
                            	msg.append("_key: ");
                            	msg.append(_key);
                            	msg.append(" _alarmHash size: ");
                            	msg.append(_alarmHash.size());
                            	msg.append(" _newEventVO: ");
                            	msg.append(_newEventVO);
                            	msg.append(" _alarmHash: ");
                            	msg.append(_alarmHash);
                            	String message1=msg.toString();
                                _log.debug("run", message1);
                            }
                            
                            // check whether max time expired for the alarm
                            if (timeDiff > _maxHoldTime) {
                                // if yes, then initialise the alarm count and
                                // generatedon(or just remove from alarmHash)
                                // and send alarm telling similar cases
                                // number(if >0)
                                _oldEventVO.setAlarmCount(0);
                                _oldEventVO.setGeneratedOn(currentTime);
                                message = _newEventVO.getMessage();
                                // change message only if count is greater than
                                // 1
                                if (count > 0) {
                                    if (_log.isDebugEnabled()){
                                    	msg.setLength(0);
                                    	msg.append("_key:");
                                    	msg.append(_key);
                                    	msg.append(" count: ");
                                    	msg.append(count);
                                        _log.debug("run",msg.toString());
                                    }
                                    message = message + MessageFormat.format(_alerCountMessage, new String[] { String.valueOf(count), String.valueOf(timeDiff / 1000) });
                                }
                                // If instance id is not defined, it should not
                                // be appended in the alarm
                                if (!BTSLUtil.isNullString(_instanceId)){
                                	msg.setLength(0);
                                	msg.append("[Instance:");
                                	msg.append(_instanceId);
                                	msg.append("]");
                                    message = message + msg.toString();
                                }
                                send(_newEventVO.getComponentName(), _newEventVO.getEventID(), _newEventVO.getEventStatus(), message);
                                

                                // Added for monitoring PreTUPS by SMS by Vipul
                                // on 05/12/07
                                // //Alert will be sent only if the event id and
                                // msisdn and event level are available in
                                // loaded list from Constants.props. //Alarm
                                // will be sent at admin mobile numbers loaded
                                // above in refresh()
                                // if(!BTSLUtil.isNullString(_msisdnString))
                                // //killed by Vikas 28/03/08 to take decision
                                // of sending alrm on the basis of eventid
                                if (isStringContain(_eventId, String.valueOf(_newEventVO.getEventID())) && !BTSLUtil.isNullString(_msisdnString)) {
                                    
                                    if ((isStringContain(_eventLevel, String.valueOf(_newEventVO.getLevel())))) {
                                        String[] msisdn = _msisdnString.split(",");
                                        PushMessage pushMessage ;

                                        for (int i = 0, len = msisdn.length; i < len; i++) {
                                            pushMessage = new PushMessage(msisdn[i], message, null, null, _locale);
                                            pushMessage.pushAlarm();
                                        }// end of for msisdn.length
                                    }// end of if event level
                                }// end of if event id && msisdn
                            } else {
                                // if no, just increase the alarm count without
                                // taking any action
                                _oldEventVO.setAlarmCount(count + 1);
                            }
                        } else {
                            _alarmHash.put(_key, _newEventVO);
                            // send Alarm

                            // If instance id is not defined, it should not be
                            // appended in the Alarm
                            if (!BTSLUtil.isNullString(_instanceId)){
                            	msg.setLength(0);
                            	msg.append("[Instance:");
                            	msg.append(_instanceId);
                            	msg.append("]");
                                message = _newEventVO.getMessage() + msg.toString();
                            }
                            else
                                message = _newEventVO.getMessage();

                            send(_newEventVO.getComponentName(), _newEventVO.getEventID(), _newEventVO.getEventStatus(), message);

                            // Added for monitoring PreTUPS by SMS by Vipul on
                            // 05/12/07
                            // Alert will be sent only if the event id and
                            // msisdn and event level are available in loaded
                            // list from Constants.props.
                            // Alarm will be sent at admin mobile numbers loaded
                            // above
                            // if(!BTSLUtil.isNullString(_msisdnString))
                            // //killed by Vikas 28/03/08 to take decision of
                            // sending alrm on the basis of eventid
                            if (isStringContain(_eventId, String.valueOf(_newEventVO.getEventID())) && !BTSLUtil.isNullString(_msisdnString)) {
                            
                                if ((isStringContain(_eventLevel, String.valueOf(_newEventVO.getLevel())))) {
                                    String[] msisdn = _msisdnString.split(",");
                                    PushMessage pushMessage ;
                                    for (int i = 0, len = msisdn.length; i < len; i++) {
                                        pushMessage = new PushMessage(msisdn[i], message, null, null, _locale);
                                        pushMessage.pushAlarm();
                                    }// //end of for msisdn.length
                                }// //end of if event level
                            }// end of if event id && msisdn
                        }
                        
                        
                    }
                    _newEventVO=null; 
                } catch (ArrayIndexOutOfBoundsException ax) {
                    _log.errorTrace(METHOD_NAME, ax);
                    continue;
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);// temporarily
                    continue;
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                try {
                    Thread.sleep(200);
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("run", "Exiting");
    }

    /**
     * Checks whether coma seprated value present in a string
     * 
     * @param commaStr
     * @param string
     * @return
     */
    private boolean isStringContain(String commaStr, String string) {
        if (_log.isDebugEnabled()){
        	StringBuilder msg=new StringBuilder("");
        	msg.append("Entered commaStr=");
        	msg.append(commaStr);
        	msg.append("  string=");
        	msg.append(string);
            _log.debug("isStringContain",msg.toString());
        }
        return BTSLUtil.isStringContain(commaStr, string);
    }
}
